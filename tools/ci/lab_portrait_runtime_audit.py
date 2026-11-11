#!/usr/bin/env python3
"""Orientation regression for real source-built GEL QA APK (no synthetic lab success).
Runs on a disposable AVD. Never confirms hardware health, never presses YES/NO.
"""
import json
import pathlib
import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET

from ui_audit_selectors import (
    MANUAL_ENTRY, find_visible, is_visible, manual_section_pattern,
    matching, scroll_gesture,
)

PKG = "com.gel.iDoctor"
OUT = pathlib.Path(sys.argv[1]) / "portrait-regression"
OUT.mkdir(parents=True, exist_ok=True)
steps = []

def adb(*args, binary=False):
    return subprocess.check_output(["adb", *args], timeout=60,
                                   stderr=subprocess.PIPE,
                                   **({} if binary else {"text": True, "errors": "replace"}))

def dump():
    adb("shell", "uiautomator", "dump", "/sdcard/gel-portrait.xml")
    return ET.fromstring(adb("exec-out", "cat", "/sdcard/gel-portrait.xml", binary=True))

def find(pattern, scrolls=0, id_suffix=False):
    prev = None
    for attempt in range(scrolls + 1):
        root = dump()
        def matching_node(n):
            return n.get("resource-id", "").endswith("/" + pattern) if id_suffix else bool(
                matching(pattern, n.get("text", ""), re.I))
        node = find_visible(root, matching_node)
        if node is not None: return node
        snapshot = ET.tostring(root)
        if prev == snapshot: return None
        prev = snapshot
        if attempt < scrolls:
            gesture = scroll_gesture(root)
            if gesture is None: break
            adb("shell", "input", "swipe", *[str(v) for v in gesture], "260")
            time.sleep(.3)
    return None

def tap(node):
    numbers = list(map(int, re.findall(r"\d+", node.get("bounds", ""))))
    if len(numbers) != 4: raise AssertionError("Missing target bounds")
    adb("shell", "input", "tap", str((numbers[0] + numbers[2]) // 2),
        str((numbers[1] + numbers[3]) // 2))
    time.sleep(.6)

def snapshot(name):
    root = dump()
    path = OUT / name
    path.with_suffix(".xml").write_bytes(ET.tostring(root))
    path.with_suffix(".png").write_bytes(adb("exec-out", "screencap", "-p", binary=True))
    lines = [n.get("text", "") for n in root.iter("node") if n.get("text")]
    path.with_suffix(".txt").write_text("\n".join(lines), encoding="utf-8")
    return "\n".join(lines)

def foreground_labs():
    activities = adb("shell", "dumpsys", "activity", "activities")
    return "ManualTestsActivity" in activities and "mResumed" not in "" # inspect top separately

def record(stage, detail):
    steps.append({"stage": stage, "detail": detail})
    (OUT / "coverage.json").write_text(json.dumps(steps, ensure_ascii=False, indent=2))
    print("PORTRAIT_AUDIT " + json.dumps(steps[-1], ensure_ascii=False), flush=True)

try:
    # Force OS to request landscape without changing users' global settings
    # (this AVD is disposable). The diagnostics Activity must stay alive.
    adb("shell", "settings", "put", "system", "accelerometer_rotation", "0")
    adb("shell", "settings", "put", "system", "user_rotation", "0")
    adb("shell", "am", "force-stop", PKG)
    adb("shell", "am", "start", "-W", "-n", PKG + "/com.gel.cleaner.MainActivity")
    time.sleep(1.2)

    diagnostics = find("btnDiagnostics", 12, id_suffix=True)
    if diagnostics is None: raise AssertionError("Diagnostics button unreachable")
    tap(diagnostics)
    lab_entry = find(MANUAL_ENTRY, 8)
    if lab_entry is None: raise AssertionError("Manual tests entry unreachable")
    tap(lab_entry)
    time.sleep(.7)
    if not foreground_labs(): raise AssertionError("ManualTestsActivity did not open")

    section = find(manual_section_pattern(1, 5), 10)
    if section is None: raise AssertionError("Audio Labs 1–5 group absent")
    tap(section)

    for num in (1, 2):
        control = find(r"^"+str(num)+r"\.", 8)
        if control is None: raise AssertionError("Lab "+str(num)+" entry unavailable")
        tap(control)
        time.sleep(14)
        sample = snapshot("lab-"+str(num)+"-before-rotation")
        if not re.search(r"LAB\s*"+str(num)+r"\s*[—-]", sample, flags=re.I):
            raise AssertionError("Lab "+str(num)+" did not write a diagnostic log")
        record("lab-"+str(num), "Diagnostic observation log present (not a hardware pass)")

    # Reach the guided earpiece prompt, reproducing the user's reported entry
    # sequence without claiming that a virtual microphone heard real tones.
    control = find(r"^3\.", 10)
    if control is None: raise AssertionError("Lab 3 earpiece entry missing")
    tap(control)
    time.sleep(2)
    before = snapshot("lab-3-before-rotation")
    for num in (1, 2):
        if not re.search(r"LAB\s*"+str(num)+r"\s*[—-]", before, flags=re.I):
            raise AssertionError("Earlier Lab "+str(num)+" log missing BEFORE rotation")

    for target in (1, 0, 1, 0):
        adb("shell", "settings", "put", "system", "user_rotation", str(target))
        time.sleep(1.4)
        if not foreground_labs():
            raise AssertionError("ManualTestsActivity lost foreground on rotation "+str(target))
        after = snapshot("rotate-"+str(target)+"-"+str(len(steps)))
        for num in (1, 2):
            if not re.search(r"LAB\s*"+str(num)+r"\s*[—-]", after, flags=re.I):
                raise AssertionError("Lab "+str(num)+" history was erased after rotation")
        record("rotation-"+str(target), "Manual Labs foreground; Lab 1 and 2 logs retained")

    record("result", "PASS: portrait/log-lifecycle regression in emulator; physical earpiece unverified")
except Exception as exc:
    record("result", "FAIL OR INCONCLUSIVE: "+str(exc))
    raise
finally:
    adb("shell", "settings", "put", "system", "user_rotation", "0")
    try: snapshot("final-state")
    except Exception: pass
    try: (OUT/"logcat.txt").write_text(
        adb("logcat", "-b", "all", "-d", "-v", "threadtime"), encoding="utf-8")
    except Exception: pass
    adb("shell", "am", "force-stop", PKG)
