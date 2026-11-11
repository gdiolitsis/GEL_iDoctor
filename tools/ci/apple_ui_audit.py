#!/usr/bin/env python3
"""Non-destructive Apple-mode Android UI evidence audit.

Runs on the same disposable AVD as full_ui_audit.sh. Never buys, signs in,
opens customer files, exports, claims Apple hardware health or diagnoses iOS.
"""
import json
import pathlib
import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from ui_audit_selectors import find_visible, is_visible, scroll_gesture

PKG = "com.gel.iDoctor"
OUT = pathlib.Path(sys.argv[1]) / "apple"
OUT.mkdir(parents=True, exist_ok=True)
LANG = sys.argv[2]
checks = []
snap = 0

def adb(*args, binary=False):
    return subprocess.check_output(["adb", *args], timeout=45, stderr=subprocess.PIPE,
                                   **({} if binary else {"text": True, "errors": "replace"}))

def dump():
    adb("shell", "uiautomator", "dump", "/sdcard/gel-apple-qa.xml")
    return ET.fromstring(adb("exec-out", "cat", "/sdcard/gel-apple-qa.xml", binary=True))

def tap(node):
    coords = list(map(int, re.findall(r"\d+", node.get("bounds", ""))))
    if len(coords) != 4: raise AssertionError("Missing tap bounds")
    adb("shell", "input", "tap", str((coords[0]+coords[2])//2),
        str((coords[1]+coords[3])//2))
    time.sleep(.7)

def by_id(suffix):
    return lambda n: n.get("resource-id", "").endswith("/" + suffix)

def by_text(expr):
    return lambda n: bool(re.search(expr, n.get("text", ""), re.I))

def find(predicate, pages=0):
    previous = None
    for step in range(pages+1):
        root = dump()
        found = find_visible(root, predicate)
        if found is not None: return found
        state = ET.tostring(root)
        if state == previous: break
        previous = state
        if step < pages:
            coords = scroll_gesture(root, True)
            if coords is None:
                adb("shell", "input", "swipe", "400","1000","400","250","280")
            else:
                adb("shell", "input", "swipe", *(str(v) for v in coords), "280")
            time.sleep(.4)
    return None

def must(predicate, msg, pages=0):
    obj = find(predicate, pages)
    if obj is None: raise AssertionError(msg)
    return obj

def screenshot(name):
    global snap
    snap += 1
    base = OUT / ("%03d-" % snap + re.sub("[^a-z0-9_-]", "_", name.lower()))
    t = dump()
    base.with_suffix(".xml").write_bytes(ET.tostring(t, encoding="utf-8"))
    base.with_suffix(".png").write_bytes(adb("exec-out", "screencap", "-p", binary=True))
    return base.name

def check(label, fn):
    try:
        result = fn()
        evidence = screenshot(label)
        checks.append(dict(name=label, status="PASS", detail=str(result or ""), evidence=evidence))
    except Exception as exc:
        try: evidence = screenshot(label + "-failure")
        except Exception: evidence = ""
        checks.append(dict(name=label, status="FAIL", detail=str(exc), evidence=evidence))
        raise
    finally:
        (OUT/"coverage.json").write_text(json.dumps(checks, ensure_ascii=False, indent=2))

def start_home():
    adb("shell", "svc", "wifi", "enable")
    adb("shell", "am", "force-stop", PKG)
    adb("shell", "am", "start", "-W", "-n", PKG + "/com.gel.cleaner.MainActivity")
    time.sleep(1.2)

def enter_apple():
    start_home()
    if find(by_id("btnAppleDeviceDeclaration"), 10) is not None:
        return "Already in Apple mode"
    control = find(by_id("btnReturnAndroid"), 10)
    if control is not None:
        tap(control)
    else:
        # A clean install may be showing the first-run platform chooser.
        choice = must(by_text(r"OTHER APPLE DEVICE|ΑΛΛΗ ΣΥΣΚΕΥΗ APPLE"),
                      "No Apple mode switch / onboarding option", 2)
        tap(choice)
    must(by_id("btnAppleDeviceDeclaration"),
         "Apple declaration is unreachable after switching platform", 10)
    return "Apple mode entered"

def pick(model, family="iphone"):
    tap(must(by_id("btnAppleDeviceDeclaration"), "Missing Apple declare control", 10))
    family_pattern = r"iPHONE$" if family=="iphone" else r"iPAD$"
    tap(must(by_text(family_pattern), "Apple family button missing: " + family, 3))
    must(by_text(r"^(?:Select Apple Model|Επιλέξτε μοντέλο Apple)$"), "Family button did not open model picker")
    tap(must(by_text("^"+re.escape(model)+"$"), "Model cannot be selected: "+model, 12))
    declaration = must(by_id("btnAppleDeviceDeclaration"),
                       "Model selection did not return to Apple home", 10)
    if model not in declaration.get("text",""):
        raise AssertionError("Model selection not saved: " + model)
    return "Selected " + model

def open_details(control, heading, required=None):
    tap(must(by_id(control), "Detail screen entry not available: "+control, 10))
    try:
        activity = adb("shell","dumpsys","activity","activities")
        if heading not in activity:
            raise AssertionError("Expected detail Activity not opened: "+heading)
        if required:
            target = "headerSystem" if "Internal" in heading else "headerBiometrics"
            tap(must(by_id(target), "Detail section not reachable: "+target, 12))
            must(by_text(".*"+re.escape(required)+".*"),
                 "Model reference value missing: "+required, 6)
        return heading + " available"
    finally:
        adb("shell","input","keyevent","4")
        time.sleep(.7)

def labs():
    tap(must(by_id("btnAppleInfoContinue"), "Apple Labs entry unavailable", 10))
    time.sleep(.9)
    # Dismiss the documented, non-destructive first-launch panic import guide.
    guide = find(by_text(r"PANIC LOGS.*(?:Import Guide|Οδηγός Εισαγωγής)"))
    if guide is not None:
        tap(must(by_text(r"^OK$"),"Apple panic guide OK missing", 8))
    must(by_text(r"GEL (?:iPhone Diagnostics|Διαγνωστικά iPhone)"),
         "iPhone Labs screen not reached", 3)
    return "Apple Labs navigation"

def verify_eight_buttons():
    for i in range(1, 9):
        must(by_text(r"^LAB\s*"+str(i)+r"\s*-"),
             "Missing iPhone LAB "+str(i), 14)
    return "All eight lab entries visible through scrolling"

def demo_only():
    # Return to start of Labs by re-opening the screen without account actions.
    adb("shell","input","keyevent","4")
    tap(must(by_id("btnAppleInfoContinue"),"Cannot reopen Apple Labs",10))
    guide=find(by_text(r"PANIC LOGS.*(?:Import Guide|Οδηγός Εισαγωγής)"))
    if guide is not None: tap(must(by_text(r"^OK$"),"Guide OK unreachable",8))
    tap(must(by_text(r"DEMO MODE"), "Apple demonstration button unreachable", 2))
    time.sleep(1.4)
    # The demonstration must not crash, and must not count as a real import.
    activities=adb("shell","dumpsys","activity","activities")
    if "IPhoneLabsActivity" not in activities:
        raise AssertionError("Demo exited Apple Labs unexpectedly")
    return "Demo opened; customer-report isolation separately tested by source contract"

try:
    check("enter-apple-mode", enter_apple)
    for model in ("iPhone Duo","iPhone 18 Pro","iPhone 18 Pro Max",
                  "iPhone 17","iPhone 17 Pro","iPhone 17 Pro Max",
                  "iPhone Air","iPhone 17e","iPhone 16e"):
        check("select-"+model,lambda m=model:pick(m))
        if model=="iPhone Duo":
            check("duo-internal", lambda:open_details("btnPhoneInfoInternal",
                    "AppleDeviceInfoInternalActivity","iPhone Duo"))
            check("duo-touch-id",lambda:open_details("btnPhoneInfoPeripherals",
                    "AppleDeviceInfoPeripheralsActivity","Touch ID"))
        if model=="iPhone 18 Pro":
            check("18-pro-internal",lambda:open_details("btnPhoneInfoInternal",
                    "AppleDeviceInfoInternalActivity","iPhone 18 Pro"))
    check("select-ipad",lambda:pick("iPad Pro 11 (M4)","ipad"))
    check("apple-labs", labs)
    check("eight-apple-labs",verify_eight_buttons)
    check("apple-demo-ui",demo_only)
finally:
    (OUT/"coverage.json").write_text(json.dumps(checks, ensure_ascii=False, indent=2))
    try: (OUT/"logcat.txt").write_text(adb("logcat","-b","all","-d","-v","threadtime"))
    except Exception: pass
    failures=[x["name"] for x in checks if x["status"]=="FAIL"]
    print("APPLE_UI_QA_FAILURES="+json.dumps(failures),flush=True)
    if failures: sys.exit(3)
