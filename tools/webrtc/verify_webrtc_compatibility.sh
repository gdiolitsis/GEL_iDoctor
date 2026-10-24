#!/usr/bin/env bash
set -euo pipefail

if [ "$#" -ne 2 ]; then
    echo "Usage:"
    echo "  $0 <upstream-reference.aar> <gel-production.aar>"
    exit 64
fi

REFERENCE_AAR="$1"
GEL_AAR="$2"

test -s "$REFERENCE_AAR" || {
    echo "ERROR: reference AAR missing: $REFERENCE_AAR"
    exit 1
}

test -s "$GEL_AAR" || {
    echo "ERROR: GEL production AAR missing: $GEL_AAR"
    exit 1
}

for tool in unzip jar javap readelf sha256sum diff awk sort sed grep; do
    command -v "$tool" >/dev/null 2>&1 || {
        echo "ERROR: required tool missing: $tool"
        exit 1
    }
done

TMPBASE="${TMPDIR:-/tmp}"
mkdir -p "$TMPBASE"
WORK="$(mktemp -d "$TMPBASE/gel-webrtc-compat.XXXXXX")"

cleanup() {
    rm -rf "$WORK"
}
trap cleanup EXIT

echo "===== AAR SHA-256 ====="
sha256sum "$REFERENCE_AAR" "$GEL_AAR"

echo
echo "===== JAVA CLASS INVENTORY ====="

unzip -p "$REFERENCE_AAR" classes.jar > "$WORK/reference-classes.jar"
unzip -p "$GEL_AAR" classes.jar > "$WORK/gel-classes.jar"

jar tf "$WORK/reference-classes.jar"     | grep '^org/webrtc/.*\.class$'     | sort -u     > "$WORK/reference-classes.txt"

jar tf "$WORK/gel-classes.jar"     | grep '^org/webrtc/.*\.class$'     | sort -u     > "$WORK/gel-classes.txt"

diff -u "$WORK/reference-classes.txt" "$WORK/gel-classes.txt"
echo "WEBRTC_CLASS_INVENTORY_MATCH=YES"

dump_public_api() {
    local jar_file="$1"
    local class_list="$2"
    local output_file="$3"

    : > "$output_file"

    while IFS= read -r class_path; do
        class_name="${class_path%.class}"
        class_name="${class_name//\//.}"

        {
            echo "===== ${class_name} ====="
            javap -classpath "$jar_file" -public -s "$class_name"
            echo
        } >> "$output_file"
    done < "$class_list"
}

echo
echo "===== JAVA PUBLIC API ====="

dump_public_api     "$WORK/reference-classes.jar"     "$WORK/reference-classes.txt"     "$WORK/reference-api.txt"

dump_public_api     "$WORK/gel-classes.jar"     "$WORK/gel-classes.txt"     "$WORK/gel-api.txt"

diff -u "$WORK/reference-api.txt" "$WORK/gel-api.txt"
echo "WEBRTC_PUBLIC_API_MATCH=YES"

echo
echo "===== AAR MANIFEST ====="

unzip -p "$REFERENCE_AAR" AndroidManifest.xml > "$WORK/reference-manifest.xml"
unzip -p "$GEL_AAR" AndroidManifest.xml > "$WORK/gel-manifest.xml"

cmp "$WORK/reference-manifest.xml" "$WORK/gel-manifest.xml"
echo "WEBRTC_MANIFEST_MATCH=YES"

dump_dynamic_symbols() {
    local so_file="$1"
    local output_file="$2"

    readelf --dyn-syms --wide "$so_file" 2>/dev/null         | awk '$7 != "UND" && $8 != "" { print $8 }'         | sed 's/@.*$//'         | sort -u         > "$output_file"
}

dump_dynamic_dependencies() {
    local so_file="$1"
    local output_file="$2"

    readelf -d "$so_file" 2>/dev/null         | grep -E '\((NEEDED|SONAME)\)'         | sed -E 's/^.*\[(.*)\].*$/\1/'         | sort -u         > "$output_file"
}

for ABI in arm64-v8a armeabi-v7a; do
    echo
    echo "===== NATIVE ABI: $ABI ====="

    REF_SO="$WORK/reference-$ABI.so"
    GEL_SO="$WORK/gel-$ABI.so"

    unzip -p         "$REFERENCE_AAR"         "jni/$ABI/libjingle_peerconnection_so.so"         > "$REF_SO"

    unzip -p         "$GEL_AAR"         "jni/$ABI/libjingle_peerconnection_so.so"         > "$GEL_SO"

    test -s "$REF_SO"
    test -s "$GEL_SO"

    REF_BUILD_ID="$(
        readelf -n "$REF_SO" 2>/dev/null             | awk '/Build ID:/ {print $3; exit}'
    )"

    GEL_BUILD_ID="$(
        readelf -n "$GEL_SO" 2>/dev/null             | awk '/Build ID:/ {print $3; exit}'
    )"

    echo "reference Build ID : ${REF_BUILD_ID:-MISSING}"
    echo "GEL       Build ID : ${GEL_BUILD_ID:-MISSING}"
    echo "NOTE: Build IDs are expected to differ because GEL retains full debug information before stripping."

    dump_dynamic_symbols         "$REF_SO"         "$WORK/reference-$ABI-dynsym.txt"

    dump_dynamic_symbols         "$GEL_SO"         "$WORK/gel-$ABI-dynsym.txt"

    diff -u         "$WORK/reference-$ABI-dynsym.txt"         "$WORK/gel-$ABI-dynsym.txt"

    echo "DYNAMIC_SYMBOLS_MATCH=YES"

    dump_dynamic_dependencies         "$REF_SO"         "$WORK/reference-$ABI-needed.txt"

    dump_dynamic_dependencies         "$GEL_SO"         "$WORK/gel-$ABI-needed.txt"

    diff -u         "$WORK/reference-$ABI-needed.txt"         "$WORK/gel-$ABI-needed.txt"

    echo "DYNAMIC_DEPENDENCIES_MATCH=YES"
done

echo
echo "========================================"
echo "WEBRTC_COMPATIBILITY_VERIFIED=YES"
echo "GEL production AAR preserves the upstream Java API and native dynamic ABI."
echo "========================================"
