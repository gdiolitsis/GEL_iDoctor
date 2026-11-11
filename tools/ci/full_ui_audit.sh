#!/usr/bin/env bash
set -euo pipefail
apk="${1:?APK required}"
api="${2:?API required}"
language="${3:?language required}"
out="$RUNNER_TEMP/full-ui-audit"
mkdir -p "$out"
sdkmanager "platform-tools" "emulator" "system-images;android-$api;google_apis;x86_64"
sudo chmod 666 /dev/kvm
export ANDROID_USER_HOME="$RUNNER_TEMP/audit-android"
export ANDROID_AVD_HOME="$ANDROID_USER_HOME/avd"
mkdir -p "$ANDROID_AVD_HOME"
name="gel_ui_audit_$api"
image="system-images;android-$api;google_apis;x86_64"
echo no | avdmanager create avd --name "$name" --package "$image" --path "$ANDROID_AVD_HOME/$name.avd" --force
if [ "$api" = 36 ]; then
python3 - "$ANDROID_AVD_HOME/$name.avd/config.ini" <<'PY'
import pathlib,sys
p=pathlib.Path(sys.argv[1])
v=dict(x.split("=",1) for x in p.read_text().splitlines() if "=" in x)
v.update({"hw.lcd.width":"800","hw.lcd.height":"1280","hw.lcd.density":"160"})
p.write_text("".join(k+"="+x+"\n" for k,x in v.items()))
PY
fi
"$ANDROID_HOME/emulator/emulator" -avd "$name" -no-window -no-audio -no-boot-anim -no-snapshot -gpu swiftshader -feature -Vulkan -memory 3072 -camera-back emulated -camera-front none > "$out/emulator.log" 2>&1 &
emulator_pid=$!
cleanup() {
  adb logcat -b all -d -v threadtime > "$out/final-logcat.txt" 2>/dev/null || true
  adb emu kill || true
  kill "$emulator_pid" 2>/dev/null || true
}
trap cleanup EXIT
timeout 150 adb wait-for-device
booted=false
for attempt in $(seq 1 120); do
  if [ "$(timeout 5 adb shell getprop sys.boot_completed | tr -d '\r')" = 1 ]; then booted=true; break; fi
  sleep 2
done
test "$booted" = true
adb shell input keyevent 82
adb shell getprop ro.product.cpu.abilist | tee "$out/abis.txt"
grep -q arm64-v8a "$out/abis.txt"
adb install --no-incremental -g "$apk"
adb shell dumpsys package com.gel.iDoctor > "$out/package.txt"
python3 tools/ci/full_ui_audit.py "$out" "$language"
# Reproduce the owner's Lab 1 -> Lab 2 -> Lab 3 ear-rotation regression
# with non-destructive interactions and retained log evidence.
if [ "${AUDIT_LABS_PORTRAIT:-0}" = 1 ]; then
  python3 tools/ci/lab_portrait_runtime_audit.py "$out" "$language"
fi
# Apple-mode evidence runs on the same disposable emulator after Android QA.
# Do not describe green Android lab exploration as a verified iPhone import test.
if [ "${AUDIT_APPLE_MODE:-0}" = 1 ]; then
  python3 tools/ci/apple_ui_audit.py "$out" "$language"
fi
