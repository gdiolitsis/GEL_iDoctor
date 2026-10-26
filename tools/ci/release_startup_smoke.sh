#!/usr/bin/env bash
# Exercise the exact minified, signed release APK (ARM translation on CI).
# This catches provider/WorkManager failures before the first Activity.
set -euo pipefail
apk="${1:?APK path required}"
out="${2:?evidence directory required}"
pkg=com.gel.iDoctor
mkdir -p "$out"
# Reconnect only the existing device; retain app state and crash logs.
wait_online() {
  if [ "$(timeout 5 adb get-state 2>/dev/null | tr -d '\r')" = device ]; then return 0; fi
  echo "ADB transport unavailable; reconnecting device" >&2
  timeout 10 adb reconnect offline >&2 || true
  timeout 30 adb wait-for-device
  [ "$(timeout 5 adb get-state 2>/dev/null | tr -d '\r')" = device ]
}
wait_online
adb install --no-incremental -r "$apk"
adb shell dumpsys package "$pkg" > "$out/package.txt"
adb logcat -b all -c
cleanup() {
  wait_online || return 1
  timeout 30 adb logcat -b all -d -v threadtime > "$out/logcat.txt" || true
  timeout 30 adb shell dumpsys activity activities > "$out/activities.txt" || true
  timeout 30 adb exec-out screencap -p > "$out/screen.png" || true
}
trap cleanup EXIT
for launch in 1 2 3; do
  wait_online
  adb shell am force-stop "$pkg"
  timeout 60 adb shell am start -W -n "$pkg/com.gel.cleaner.MainActivity" > "$out/launch-$launch.txt"
  # Watch continuously; a successful am start alone does not prove startup.
  for second in $(seq 1 15); do
    sleep 1
    wait_online
    if ! timeout 10 adb shell pidof "$pkg" | tr -d '\r' | grep -Eq '[0-9]'; then
      echo "Release process died after launch $launch (second $second)"
      exit 1
    fi
  done
done
cleanup
trap - EXIT
if grep -Fq "Process: $pkg," "$out/logcat.txt"; then
  echo "Release crash recorded in logcat"
  exit 1
fi
grep -Fq "$pkg/" "$out/activities.txt"
echo "RELEASE_STARTUP_SMOKE=PASS" | tee "$out/result.txt"
