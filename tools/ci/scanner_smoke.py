#!/usr/bin/env python3
"""Scan an inert QR through the real release camera UI in both orientations."""
import pathlib
import re
import struct
import subprocess
import sys
import time
import xml.etree.ElementTree as ET

PKG = "com.gel.iDoctor"
PARENT = "com.gel.cleaner.ConnectToTechnicianActivity"
out = pathlib.Path(sys.argv[1])
out.mkdir(parents=True, exist_ok=True)

def adb(*args, binary=False, timeout=30):
    return subprocess.check_output(["adb", *args], timeout=timeout, **({} if binary else {"text": True, "errors": "replace"}))

def ui(path):
    adb("shell", "uiautomator", "dump", "/sdcard/gel-scanner-smoke.xml")
    data = adb("exec-out", "cat", "/sdcard/gel-scanner-smoke.xml", binary=True)
    path.write_bytes(data)
    return ET.fromstring(data)

def swipe(up):
    image = adb("exec-out", "screencap", "-p", binary=True)
    width, height = struct.unpack(">II", image[16:24])
    start, end = ((0.8, 0.25) if up else (0.25, 0.8))
    adb("shell", "input", "swipe", str(width//2), str(int(height*start)), str(width//2), str(int(height*end)), "250")

def evidence(folder):
    for name, cmd in [("logcat.txt", ("logcat", "-b", "all", "-d", "-v", "threadtime")), ("activities.txt", ("shell", "dumpsys", "activity", "activities"))]:
        try: (folder / name).write_text(adb(*cmd))
        except Exception: pass
    try: (folder / "screen.png").write_bytes(adb("exec-out", "screencap", "-p", binary=True))
    except Exception: pass

adb("shell", "pm", "grant", PKG, "android.permission.CAMERA")
adb("shell", "settings", "put", "system", "accelerometer_rotation", "0")
for rotation, label in [(0, "portrait"), (1, "landscape")]:
    folder = out / label
    folder.mkdir(exist_ok=True)
    try:
        adb("shell", "am", "force-stop", PKG)
        adb("shell", "settings", "put", "system", "user_rotation", str(rotation))
        adb("logcat", "-b", "all", "-c")
        launch = adb("shell", "am", "start", "-W", "-n", PKG + "/" + PARENT)
        (folder / "launch.txt").write_text(launch)
        assert "Status: ok" in launch, launch
        tapped = False
        for attempt in range(10):
            tree = ui(folder / "before.xml")
            if tree.get("rotation") != str(rotation):
                time.sleep(1)
                continue
            for node in tree.iter("node"):
                if node.get("text") not in ("Scan Technician QR", "Σάρωση QR Τεχνικού"): continue
                bounds = list(map(int, re.findall(r"\d+", node.get("bounds", ""))))
                if len(bounds) == 4 and bounds[2] > bounds[0] and bounds[3] > bounds[1]:
                    adb("shell", "input", "tap", str((bounds[0]+bounds[2])//2), str((bounds[1]+bounds[3])//2))
                    tapped = True
                    break
            if tapped: break
            swipe(True)
        assert tapped, "Scanner launch button not found"
        decoded = False
        for attempt in range(15):
            time.sleep(1)
            tree = ui(folder / "after.xml")
            texts = [n.get("text", "") for n in tree.iter("node")]
            # This status is set only after the actual QR result callback rejects
            # our inert payload, before any anonymous auth or backend claim.
            if any("INVALID QR" in t or "ΜΗ ΕΓΚΥΡΟ QR" in t for t in texts):
                decoded = True
                break
            activities = adb("shell", "dumpsys", "activity", "activities")
            if re.search(r"(?:mResumedActivity|topResumedActivity).*ConnectToTechnicianActivity", activities):
                swipe(True)
        assert decoded, "Camera QR decode did not return the expected invalid-session result"
        evidence(folder)
        assert "Process: " + PKG + "," not in (folder / "logcat.txt").read_text(), "App crash recorded"
        width, height = struct.unpack(">II", (folder / "screen.png").read_bytes()[16:24])
        assert (width < height) if rotation == 0 else (width > height), "Wrong final orientation"
        (folder / "result.txt").write_text("SCANNER_CAMERA_QR_" + label.upper() + "=PASS\n")
        print("SCANNER_CAMERA_QR_" + label.upper() + "=PASS", flush=True)
    finally:
        evidence(folder)
print("SCANNER_ORIENTATION_SMOKE=PASS")

