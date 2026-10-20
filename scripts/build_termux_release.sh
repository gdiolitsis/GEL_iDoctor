#!/data/data/com.termux/files/usr/bin/bash

set -Eeuo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

mkdir -p build/termux-logs

STAMP="$(date +%Y%m%d_%H%M%S)"
LOG="$ROOT/build/termux-logs/release_${STAMP}.log"

WAKE_LOCKED=0

cleanup() {
    if [ "$WAKE_LOCKED" -eq 1 ] \
       && command -v termux-wake-unlock >/dev/null 2>&1; then
        termux-wake-unlock >/dev/null 2>&1 || true
    fi
}

trap cleanup EXIT INT TERM HUP

echo "===== GEL iDoctor Termux Release Build ====="
echo "Root : $ROOT"
echo "Log  : $LOG"
echo

if command -v termux-wake-lock >/dev/null 2>&1; then
    echo "Acquiring Termux wake lock..."
    termux-wake-lock || true
    WAKE_LOCKED=1
else
    echo "WARNING: termux-wake-lock command not available."
fi

echo
echo "===== JAVA ====="
java -version 2>&1 | tee -a "$LOG"

echo
echo "===== GRADLE ====="
./gradlew --version 2>&1 | tee -a "$LOG"

echo
echo "===== STOP OLD GRADLE DAEMONS ====="
./gradlew --stop 2>&1 | tee -a "$LOG" || true

echo
echo "===== RELEASE APK + AAB ====="
echo "Using max 1 Gradle worker to minimize Android memory/CPU pressure."

./gradlew \
    --no-daemon \
    --max-workers=1 \
    --warning-mode all \
    assembleRelease \
    bundleRelease \
    2>&1 | tee -a "$LOG"

echo
echo "===== ARTIFACTS ====="

find app/build/outputs \
    -type f \
    \( -name '*.apk' -o -name '*.aab' \) \
    -print \
    | sort \
    | tee -a "$LOG"

echo
echo "===== RELEASE BUILD SUCCESSFUL ====="
echo "Log: $LOG"
