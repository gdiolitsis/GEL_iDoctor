#!/usr/bin/env bash
set -euo pipefail

if [ "$#" -ne 2 ]; then
    echo "Usage:"
    echo "  $0 <production-stripped.aar> <matching-unstripped.aar>"
    exit 64
fi

PROD_AAR="$1"
SYMBOL_AAR="$2"

test -s "$PROD_AAR" || {
    echo "ERROR: production AAR missing: $PROD_AAR"
    exit 1
}

test -s "$SYMBOL_AAR" || {
    echo "ERROR: symbol AAR missing: $SYMBOL_AAR"
    exit 1
}

READELF=""

for x in llvm-readelf readelf; do
    if command -v "$x" >/dev/null 2>&1; then
        READELF="$x"
        break
    fi
done

test -n "$READELF" || {
    echo "ERROR: llvm-readelf/readelf not found"
    exit 1
}

TMPBASE="${TMPDIR:-/tmp}"
mkdir -p "$TMPBASE"

WORK="$(mktemp -d "$TMPBASE/gel-webrtc-symbol-verify.XXXXXX")"

cleanup() {
    rm -rf "$WORK"
}
trap cleanup EXIT

LIB="libjingle_peerconnection_so.so"
ABIS="arm64-v8a armeabi-v7a"

build_id() {
    "$READELF" -n "$1" 2>/dev/null \
        | awk '/Build ID:/ {print $3; exit}'
}

echo "===== JAVA API CONSISTENCY ====="

unzip -p "$PROD_AAR" classes.jar > "$WORK/prod-classes.jar"
unzip -p "$SYMBOL_AAR" classes.jar > "$WORK/symbol-classes.jar"

PROD_CLASSES_SHA="$(
    sha256sum "$WORK/prod-classes.jar" | awk '{print $1}'
)"

SYMBOL_CLASSES_SHA="$(
    sha256sum "$WORK/symbol-classes.jar" | awk '{print $1}'
)"

echo "production classes.jar: $PROD_CLASSES_SHA"
echo "symbols    classes.jar: $SYMBOL_CLASSES_SHA"

if [ "$PROD_CLASSES_SHA" != "$SYMBOL_CLASSES_SHA" ]; then
    echo "ERROR: Java portions of the two AARs differ"
    exit 1
fi

echo "JAVA_API_MATCH=YES"

for ABI in $ABIS; do

    echo
    echo "========================================"
    echo "ABI: $ABI"
    echo "========================================"

    PROD_SO="$WORK/prod-$ABI.so"
    SYMBOL_SO="$WORK/symbol-$ABI.so"

    unzip -p \
        "$PROD_AAR" \
        "jni/$ABI/$LIB" \
        > "$PROD_SO"

    unzip -p \
        "$SYMBOL_AAR" \
        "jni/$ABI/$LIB" \
        > "$SYMBOL_SO"

    test -s "$PROD_SO" || {
        echo "ERROR: production $ABI library missing"
        exit 1
    }

    test -s "$SYMBOL_SO" || {
        echo "ERROR: unstripped $ABI library missing"
        exit 1
    }

    PROD_ID="$(build_id "$PROD_SO")"
    SYMBOL_ID="$(build_id "$SYMBOL_SO")"

    echo "production Build ID : ${PROD_ID:-MISSING}"
    echo "symbols    Build ID : ${SYMBOL_ID:-MISSING}"

    if [ -z "$PROD_ID" ] || [ -z "$SYMBOL_ID" ]; then
        echo "ERROR: GNU Build ID missing"
        exit 1
    fi

    if [ "$PROD_ID" != "$SYMBOL_ID" ]; then
        echo "ERROR: BUILD ID MISMATCH"
        exit 1
    fi

    echo "BUILD_ID_MATCH=YES"

    SECTIONS="$("$READELF" -SW "$SYMBOL_SO" 2>/dev/null)"

    if printf '%s\n' "$SECTIONS" | grep -q '\.symtab'; then
        echo "SYMTAB_PRESENT=YES"
    else
        echo "ERROR: matching unstripped library has no .symtab"
        exit 1
    fi

    if printf '%s\n' "$SECTIONS" \
        | grep -qE '\.debug_info|\.debug_line|\.debug_abbrev'; then
        echo "DWARF_PRESENT=YES"
    else
        echo "ERROR: matching unstripped library has no DWARF debug data"
        exit 1
    fi

    echo "production size: $(wc -c < "$PROD_SO") bytes"
    echo "symbols size   : $(wc -c < "$SYMBOL_SO") bytes"

done

echo
echo "========================================"
echo "WEBRTC_SYMBOL_PAIR_VERIFIED=YES"
echo "Production and symbol artifacts are safe to pair."
echo "========================================"
