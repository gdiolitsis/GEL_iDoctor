#!/usr/bin/env bash
# Exercise the signed production ARM APK, with a deterministic camera QR input.
set -euo pipefail
apk="${1:?signed APK required}"
evidence="$RUNNER_TEMP/startup-evidence"
mkdir -p "$evidence" "$RUNNER_TEMP/scanner-fixture"
core_jar="$(find "$HOME/.gradle/caches/modules-2/files-2.1/com.google.zxing/core/3.5.2" -name '*.jar' -print -quit)"
test -s "$core_jar"
javac -cp "$core_jar" -d "$RUNNER_TEMP/scanner-fixture" tools/ci/GenerateScannerQr.java
java -Djava.awt.headless=true -cp "$RUNNER_TEMP/scanner-fixture:$core_jar" GenerateScannerQr "$RUNNER_TEMP/scanner-qr.png"
sdkmanager "emulator"
sudo chmod 666 /dev/kvm
export ANDROID_USER_HOME="$RUNNER_TEMP/gel-android-user"
export ANDROID_AVD_HOME="$ANDROID_USER_HOME/avd"
mkdir -p "$ANDROID_AVD_HOME"
stop_emulator() {
  timeout 10 adb emu kill || true
  if [ -n "${emulator_pid:-}" ]; then wait "$emulator_pid" || true; fi
}
trap 'stop_emulator' EXIT
for api in 30 36; do
  name="gel_release_smoke_$api"
  image="system-images;android-$api;google_apis;x86_64"
  sdkmanager "$image"
  echo no | avdmanager create avd --name "$name" --package "$image" --path "$ANDROID_AVD_HOME/$name.avd" --force
  if [ "$api" = 36 ]; then
    # Configure tablet dimensions before boot; avoid resizing an already
    # running 320x640 display into a much larger software-rendered surface.
    python3 - "$ANDROID_AVD_HOME/$name.avd/config.ini" <<'PYCONFIG'
import pathlib, sys
path = pathlib.Path(sys.argv[1])
values = dict(line.split("=", 1) for line in path.read_text().splitlines() if "=" in line)
values.update({"hw.lcd.width": "800", "hw.lcd.height": "1280", "hw.lcd.density": "160"})
path.write_text("".join(key + "=" + value + "\n" for key, value in values.items()))
PYCONFIG
  fi
  "$ANDROID_HOME/emulator/emulator" -list-avds | grep -Fx "$name"
  "$ANDROID_HOME/emulator/emulator" -avd "$name" -no-window -no-audio -no-boot-anim -no-snapshot -gpu swiftshader -memory 3072 -camera-back "imagefile:$RUNNER_TEMP/scanner-qr.png" -camera-front none > "$RUNNER_TEMP/emulator-$api.log" 2>&1 &
  emulator_pid=$!
  if ! timeout 120 adb wait-for-device; then cat "$RUNNER_TEMP/emulator-$api.log"; exit 1; fi
  booted=false
  for attempt in $(seq 1 120); do
    if [ "$(timeout 5 adb shell getprop sys.boot_completed | tr -d '\r')" = 1 ]; then booted=true; break; fi
    sleep 2
  done
  test "$booted" = true
  adb shell input keyevent 82
  adb shell getprop ro.product.cpu.abilist | tee "$evidence/abis-$api.txt"
  grep -q arm64-v8a "$evidence/abis-$api.txt"
  if [ "$api" = 30 ] && [ -n "${UPGRADE_ARTIFACT_ID:-}" ]; then
    [[ "$UPGRADE_ARTIFACT_ID" =~ ^[0-9]+$ ]]
    curl --fail --location --retry 3 -H "Authorization: Bearer $GH_TOKEN" -H "Accept: application/vnd.github+json" \
      "https://api.github.com/repos/gdiolitsis/GEL_iDoctor/actions/artifacts/$UPGRADE_ARTIFACT_ID/zip" -o "$RUNNER_TEMP/upgrade-baseline.zip"
    mkdir -p "$RUNNER_TEMP/upgrade-baseline"
    unzip -q "$RUNNER_TEMP/upgrade-baseline.zip" -d "$RUNNER_TEMP/upgrade-baseline"
    baseline="$(find "$RUNNER_TEMP/upgrade-baseline" -name '*.apk' -print -quit)"
    "$ANDROID_HOME/build-tools/36.0.0/aapt2" dump badging "$baseline" | grep -F "package: name='com.gel.iDoctor' versionCode='51' versionName='5.1'"
    bash tools/ci/release_startup_smoke.sh "$baseline" "$evidence/api-30-baseline-v51"
    bash tools/ci/release_startup_smoke.sh "$apk" "$evidence/api-30-upgrade-v52"
    echo "V51_TO_V52_UPGRADE_STARTUP=PASS"
    adb uninstall com.gel.iDoctor
  fi
  bash tools/ci/release_startup_smoke.sh "$apk" "$evidence/api-$api-fresh-v52"
  python3 tools/ci/scanner_smoke.py "$evidence/api-$api-scanner"
  echo "API_${api}_RELEASE_STARTUP_AND_SCANNER=PASS"
  stop_emulator
  emulator_pid=""
  avdmanager delete avd --name "$name"
  sdkmanager --uninstall "$image"
done
trap - EXIT

