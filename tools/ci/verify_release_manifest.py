#!/usr/bin/env python3
"""Check the packaged AAB manifest, including dependency-merged activities."""
import sys
import xml.etree.ElementTree as ET

if len(sys.argv) != 4:
    sys.exit("Usage: verify_release_manifest.py MANIFEST VERSION_CODE VERSION_NAME")

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
assert root.get(ANDROID + "versionCode") == sys.argv[2], "Wrong version code"
assert root.get(ANDROID + "versionName") == sys.argv[3], "Wrong version name"
scanner = [a for a in app.findall("activity") if a.get(ANDROID + "name") == "com.journeyapps.barcodescanner.CaptureActivity"]
assert len(scanner) == 1, "Expected exactly one scanner activity"
assert orientation(scanner[0].get(ANDROID + "screenOrientation")) == -1, "Scanner orientation still restricted"
# These diagnostics intentionally lock portrait to keep active measurements,
# touch targets and their visible logs stable on phone rotation. This is the
# approved UI behavior, not an accidental dependency orientation restriction.
portrait_diagnostics = {
    "com.gel.cleaner.ManualTestsActivity",
    "com.gel.cleaner.EssentialHardwareChecksActivity",
    "com.gel.cleaner.PinchZoomTestActivity",
    "com.gel.cleaner.RotationCheckActivity",
}
restricted = {0, 1, 5, 6, 7, 8, 9, 11, 12, 14}
present_portrait_diagnostics = set()
for element in [app, *app.findall("activity"), *app.findall("activity-alias")]:
    name = element.get(ANDROID + "name", "application")
    actual_orientation = orientation(element.get(ANDROID + "screenOrientation"))
    if name in portrait_diagnostics:
        assert actual_orientation == 1, f"Expected intentional portrait on {name}"
        present_portrait_diagnostics.add(name)
    else:
        assert actual_orientation not in restricted, f"Unexpected orientation restriction: {name}"
    assert element.get(ANDROID + "resizeableActivity", "").lower() not in {"false", "0", "0x0", "0x00000000"}, f"Resize restriction: {name}"
assert present_portrait_diagnostics == portrait_diagnostics, (
    f"Missing portrait diagnostics: {sorted(portrait_diagnostics - present_portrait_diagnostics)}")
print("PACKAGED_MANIFEST_ORIENTATION=PASS")
