#!/usr/bin/env python3
"""Check the packaged AAB manifest, including dependency-merged activities."""
import sys
import xml.etree.ElementTree as ET

ANDROID = "{http://schemas.android.com/apk/res/android}"
root = ET.parse(sys.argv[1]).getroot()
app = root.find("application")
assert app is not None, "Missing application"
assert root.get("package") == "com.gel.iDoctor", "Wrong application"
assert root.get(ANDROID + "versionCode") == "52", "Wrong version code"
assert root.get(ANDROID + "versionName") == "5.2", "Wrong version name"
scanner = [a for a in app.findall("activity") if a.get(ANDROID + "name") == "com.journeyapps.barcodescanner.CaptureActivity"]
assert len(scanner) == 1, "Expected exactly one scanner activity"
assert scanner[0].get(ANDROID + "screenOrientation") == "unspecified", "Scanner orientation still restricted"
restricted = {"portrait", "landscape", "reversePortrait", "reverseLandscape", "sensorPortrait", "sensorLandscape", "userPortrait", "userLandscape", "locked", "nosensor"}
for element in [app, *app.findall("activity"), *app.findall("activity-alias")]:
    name = element.get(ANDROID + "name", "application")
    assert element.get(ANDROID + "screenOrientation") not in restricted, f"Orientation restriction: {name}"
    assert element.get(ANDROID + "resizeableActivity") != "false", f"Resize restriction: {name}"
print("PACKAGED_MANIFEST_ORIENTATION=PASS")
