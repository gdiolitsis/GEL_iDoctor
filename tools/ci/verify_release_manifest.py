#!/usr/bin/env python3
"""Check the packaged AAB manifest, including dependency-merged activities."""
import sys
import xml.etree.ElementTree as ET

ANDROID = "{http://schemas.android.com/apk/res/android}"
ORIENTATIONS = dict(zip(
    ("unspecified", "landscape", "portrait", "user", "behind", "sensor", "nosensor",
     "sensorLandscape", "sensorPortrait", "reverseLandscape", "reversePortrait",
     "fullSensor", "userLandscape", "userPortrait", "fullUser", "locked"),
    range(-1, 15)))

def orientation(value):
    # bundletool prints compiled enum values numerically (unspecified == -1).
    if value is None: return -1
    if value in ORIENTATIONS: return ORIENTATIONS[value]
    number = int(value, 0)
    return number - 2**32 if number >= 2**31 else number

root = ET.parse(sys.argv[1]).getroot()
app = root.find("application")
assert app is not None, "Missing application"
assert root.get("package") == "com.gel.iDoctor", "Wrong application"
assert root.get(ANDROID + "versionCode") == "52", "Wrong version code"
assert root.get(ANDROID + "versionName") == "5.2", "Wrong version name"
scanner = [a for a in app.findall("activity") if a.get(ANDROID + "name") == "com.journeyapps.barcodescanner.CaptureActivity"]
assert len(scanner) == 1, "Expected exactly one scanner activity"
assert orientation(scanner[0].get(ANDROID + "screenOrientation")) == -1, "Scanner orientation still restricted"
restricted = {0, 1, 5, 6, 7, 8, 9, 11, 12, 14}
for element in [app, *app.findall("activity"), *app.findall("activity-alias")]:
    name = element.get(ANDROID + "name", "application")
    assert orientation(element.get(ANDROID + "screenOrientation")) not in restricted, f"Orientation restriction: {name}"
    assert element.get(ANDROID + "resizeableActivity", "").lower() not in {"false", "0", "0x0", "0x00000000"}, f"Resize restriction: {name}"
print("PACKAGED_MANIFEST_ORIENTATION=PASS")
