#!/usr/bin/env bash
set -euo pipefail

MODE="production-only"
case "${1:-}" in
  ""|--production-only)
    MODE="production-only"
    ;;
  --with-symbols)
    MODE="with-symbols"
    ;;
  *)
    echo "Usage: $0 [--production-only|--with-symbols]" >&2
    exit 64
    ;;
esac

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
POLICY="$SCRIPT_DIR/webrtc-promoted.env"

test -s "$POLICY" || {
  echo "ERROR: missing promoted WebRTC policy: $POLICY" >&2
  exit 1
}

# shellcheck disable=SC1090
source "$POLICY"

for tool in curl sha256sum unzip readelf awk grep; do
  command -v "$tool" >/dev/null 2>&1 || {
    echo "ERROR: required tool missing: $tool" >&2
    exit 1
  }
done

PROD_DIR="$ROOT/app/libs"
CACHE_DIR="$ROOT/.webrtc-cache"
SYMBOL_ROOT="$ROOT/$WEBRTC_SYMBOL_DIR"

PROD_AAR="$PROD_DIR/$WEBRTC_PRODUCTION_ASSET"
UNSTRIPPED_AAR="$CACHE_DIR/$WEBRTC_UNSTRIPPED_ASSET"

mkdir -p "$PROD_DIR" "$CACHE_DIR"

fetch_verified() {
  local url="$1"
  local dest="$2"
  local expected_sha="$3"

  if [ -s "$dest" ]; then
    local existing_sha
    existing_sha="$(sha256sum "$dest" | awk '{print $1}')"
    if [ "$existing_sha" = "$expected_sha" ]; then
      echo "REUSE_VERIFIED=$(basename "$dest")"
      return 0
    fi
    echo "Discarding stale or mismatched file: $dest"
    rm -f "$dest"
  fi

  local tmp="$dest.tmp.$$"
  rm -f "$tmp"

  curl \
    --fail \
    --location \
    --retry 5 \
    --retry-all-errors \
    --connect-timeout 30 \
    "$url" \
    --output "$tmp"

  echo "$expected_sha  $tmp" | sha256sum -c -
  mv "$tmp" "$dest"
}

read_build_id() {
  local so="$1"
  readelf -n "$so" 2>/dev/null | awk '/Build ID:/ {print $3; exit}'
}

extract_so() {
  local aar="$1"
  local abi="$2"
  local dest="$3"

  mkdir -p "$(dirname "$dest")"
  unzip -p "$aar" "jni/$abi/$WEBRTC_LIBRARY" > "$dest"
  test -s "$dest"
}

verify_build_id() {
  local so="$1"
  local expected="$2"
  local label="$3"
  local actual

  actual="$(read_build_id "$so")"
  echo "$label Build ID: ${actual:-MISSING}"
  test -n "$actual"
  test "$actual" = "$expected"
}

fetch_verified \
  "$WEBRTC_PROMOTED_RELEASE_BASE/$WEBRTC_PRODUCTION_ASSET" \
  "$PROD_AAR" \
  "$WEBRTC_PRODUCTION_SHA256"

TMP_VERIFY="$(mktemp -d "${TMPDIR:-/tmp}/gel-webrtc-fetch.XXXXXX")"
cleanup() {
  rm -rf "$TMP_VERIFY"
}
trap cleanup EXIT

extract_so "$PROD_AAR" arm64-v8a "$TMP_VERIFY/production-arm64.so"
extract_so "$PROD_AAR" armeabi-v7a "$TMP_VERIFY/production-armv7.so"

verify_build_id "$TMP_VERIFY/production-arm64.so" "$WEBRTC_ARM64_BUILD_ID" "production arm64-v8a"
verify_build_id "$TMP_VERIFY/production-armv7.so" "$WEBRTC_ARMV7_BUILD_ID" "production armeabi-v7a"

echo "WEBRTC_PRODUCTION_READY=YES"

if [ "$MODE" = "production-only" ]; then
  exit 0
fi

fetch_verified \
  "$WEBRTC_PROMOTED_RELEASE_BASE/$WEBRTC_UNSTRIPPED_ASSET" \
  "$UNSTRIPPED_AAR" \
  "$WEBRTC_UNSTRIPPED_SHA256"

rm -rf "$SYMBOL_ROOT"
mkdir -p "$SYMBOL_ROOT"

extract_so "$UNSTRIPPED_AAR" arm64-v8a "$SYMBOL_ROOT/arm64-v8a/$WEBRTC_LIBRARY"
extract_so "$UNSTRIPPED_AAR" armeabi-v7a "$SYMBOL_ROOT/armeabi-v7a/$WEBRTC_LIBRARY"

verify_symbol_so() {
  local abi="$1"
  local expected="$2"
  local so="$SYMBOL_ROOT/$abi/$WEBRTC_LIBRARY"

  verify_build_id "$so" "$expected" "unstripped $abi"

  readelf -S "$so" | grep -q '\.symtab'
  readelf -S "$so" | grep -Eq '\.debug_(info|line|abbrev)'

  echo "$abi SYMTAB=YES"
  echo "$abi DWARF=YES"
}

verify_symbol_so arm64-v8a "$WEBRTC_ARM64_BUILD_ID"
verify_symbol_so armeabi-v7a "$WEBRTC_ARMV7_BUILD_ID"

echo "WEBRTC_CRASHLYTICS_SYMBOLS_READY=YES"
