#!/usr/bin/env python3
"""Static release-gate contract: diagnostic screens stay upright on phones.
On Android 16 large screens the OS may ignore orientation requests; the
ManualTestsActivity must ALSO be stateful when it is recreated.
"""
from pathlib import Path
import xml.etree.ElementTree as ET

ANDROID = "{http://schemas.android.com/apk/res/android}"
root = ET.parse(Path("app/src/main/AndroidManifest.xml")).getroot()
application = root.find("application")
assert application is not None, "Missing application"

expected = (
    ".ManualTestsActivity",
    ".IPhoneLabsActivity",
    ".TouchGridTestActivity",
    ".DisplayProTestActivity",
    ".RotationCheckActivity",
    ".ProximityCheckActivity",
)
required_config = {"orientation", "screenSize", "smallestScreenSize", "screenLayout"}
for name in expected:
    activity = next((a for a in application.findall("activity")
                     if a.get(ANDROID + "name") == name), None)
    assert activity is not None, f"Missing {name}"
    assert activity.get(ANDROID + "screenOrientation") == "portrait", (
        f"{name} must request portrait while diagnostics run")
    config = set((activity.get(ANDROID + "configChanges") or "").split("|"))
    assert required_config.issubset(config), (
        f"{name} lacks configuration-change handling: {required_config-config}")

manual = Path("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java").read_text()
assert "protected void onSaveInstanceState(Bundle outState)" in manual
assert "GELServiceLog.restoreCurrentIfEmpty(" in manual
assert "if (isFinishing() && !isChangingConfigurations())" in manual
assert "lab3GuidedPending.compareAndSet(false, true)" in manual
assert "OrientationSequence" in Path(
    "app/src/main/java/com/gel/cleaner/RotationCheckActivity.java").read_text()
print("LAB_PORTRAIT_AND_LOG_SURVIVAL_CONTRACT=PASS")
