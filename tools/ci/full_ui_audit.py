#!/usr/bin/env python3
"""Evidence-first UI exploration of the unmodified signed release; never claims hardware pass."""
import json, pathlib, re, struct, subprocess, sys, time, traceback
import xml.etree.ElementTree as ET

PKG = "com.gel.iDoctor"
out = pathlib.Path(sys.argv[1]); out.mkdir(parents=True, exist_ok=True)
lang = sys.argv[2]
results = []
counter = 0
def adb(*args, binary=False, timeout=35):
    return subprocess.check_output(["adb", *args], timeout=timeout, stderr=subprocess.PIPE,
        **({} if binary else {"text":True, "errors":"replace"}))
def tree():
    adb("shell","uiautomator","dump","/sdcard/gel-audit.xml")
    return ET.fromstring(adb("exec-out","cat","/sdcard/gel-audit.xml",binary=True))
def visible(n):
    b = list(map(int,re.findall(r"\d+",n.get("bounds",""))))
    return len(b)==4 and b[2]>b[0] and b[3]>b[1]
def tap(n):
    print("UI_TAP "+json.dumps({"text":n.get("text",""),"id":n.get("resource-id",""),"bounds":n.get("bounds","")},ensure_ascii=False),flush=True)
    b=list(map(int,re.findall(r"\d+",n.get("bounds",""))))
    adb("shell","input","tap",str((b[0]+b[2])//2),str((b[1]+b[3])//2))
    time.sleep(.6)
def swipe(up=True):
    b=adb("exec-out","screencap","-p",binary=True)
    w,h=struct.unpack(">II",b[16:24])
    y1,y2=(.72,.32) if up else (.32,.72)
    adb("shell","input","swipe",str(w//2),str(int(h*y1)),str(w//2),str(int(h*y2)),"250")
def find(predicate, scrolls=0):
    previous=None
    for attempt in range(scrolls+1):
        t=tree()
        for n in t.iter("node"):
            if visible(n) and predicate(n): return n
        signature=ET.tostring(t)
        if signature==previous: break
        previous=signature
        if attempt < scrolls: swipe()
    return None
def textmatch(pattern):
    return lambda n: bool(re.search(pattern,n.get("text",""),re.I))
def rid(name): return lambda n:n.get("resource-id","").endswith("/"+name)
def save(label):
    global counter
    counter+=1
    folder=out/("%03d-"%counter+re.sub(r"[^a-zA-Z0-9_-]","_",label))
    folder.mkdir()
    try:
        t=tree()
        (folder/"ui.xml").write_bytes(ET.tostring(t,encoding="utf-8"))
        (folder/"screen.png").write_bytes(adb("exec-out","screencap","-p",binary=True))
        (folder/"texts.txt").write_text("\n".join(n.get("text","") for n in t.iter("node")),encoding="utf-8")
        print("UI_TEXT "+label+" "+json.dumps([n.get("text","") for n in t.iter("node") if n.get("text")],ensure_ascii=False)[:6000],flush=True)
    except Exception as e: (folder/"capture-error.txt").write_text(str(e))
    (folder/"activities.txt").write_text(adb("shell","dumpsys","activity","activities"))
    return folder
def record(name,status,detail="",folder=None):
    results.append(dict(name=name,status=status,detail=detail,evidence=str(folder.relative_to(out)) if folder else ""))
    (out/"coverage.json").write_text(json.dumps(results,ensure_ascii=False,indent=2))
    print(json.dumps(results[-1],ensure_ascii=False),flush=True)
def dismiss_intro():
    stable = 0
    for attempt in range(12):
        t = tree()
        nodes = [n for n in t.iter("node") if visible(n)]
        denied = next((n for n in nodes if n.get("resource-id","").endswith("/permission_deny_button")),None)
        if denied is not None:
            tap(denied); stable=0; continue
        later = next((n for n in nodes if re.search(r"^(Not now|Όχι τώρα|Later|Αργότερα)$",n.get("text",""),re.I)),None)
        if later is not None:
            tap(later); stable=0; continue
        android = next((n for n in nodes if re.search(r"MY ANDROID DEVICE|Η ANDROID ΣΥΣΚΕΥΗ ΜΟΥ",n.get("text",""),re.I)),None)
        if android is not None:
            tap(android); stable=0; continue
        welcome = any(re.search(r"^(WELCOME|ΚΑΛΩΣ ΗΡΘΑΤΕ)$",n.get("text",""),re.I) for n in nodes)
        if welcome:
            ok = next((n for n in nodes if n.get("text","").upper()=="OK"),None)
            if ok is not None: tap(ok)
            else:
                save("welcome-actions-unreachable")
                adb("shell","input","keyevent","4")
            stable=0; continue
        # Wait for delayed first-run permission dialogs before declaring home ready.
        if any(n.get("resource-id","").endswith(("/btnLangEN","/btnLangGR","/scrollRoot")) for n in nodes):
            stable += 1
            if stable >= 2: return
        else: stable=0
        time.sleep(1)
def home():
    adb("shell","am","force-stop",PKG)
    adb("shell","am","start","-W","-n",PKG+"/com.gel.cleaner.MainActivity")
    time.sleep(1)
    dismiss_intro()
    for _ in range(3): swipe(False)
def select_language():
    home()
    save("before-language")
    n=find(rid("btnLangGR" if lang=="el" else "btnLangEN"),3)
    if n is None: raise RuntimeError("Language selector unavailable")
    tap(n); time.sleep(1); dismiss_intro()
    record("language-"+lang,"UI_OPENED",folder=save("language-"+lang))
def manual():
    home()
    n=find(rid("btnDiagnostics"),12)
    if n is None: raise RuntimeError("Diagnostics entry missing")
    tap(n)
    n=find(textmatch(r"GEL Android Manual Tests|Χειροκίνητ|Χειροκίν|Manual Tests"),8)
    if n is None: raise RuntimeError("Manual labs entry missing")
    tap(n); time.sleep(.8); dismiss_intro()
def safe_interaction():
    # Only explicit non-destructive controls. Never choose good/bad hardware answers,
    # send mail, place calls, buy, consume a trial, grant account access or delete data.
    n=find(textmatch(r"^(START|START TEST|BEGIN TEST|RUN CHECK|RUN TEST|ΕΝΑΡΞΗ|ΕΝΑΡΞΗ ΕΛΕΓΧΟΥ|ΞΕΚΙΝΑ|Έναρξη|Έναρξη ελέγχου)$"))
    if n is not None: tap(n); return True
    return False
def outcome(folder):
    text=(folder/"texts.txt").read_text() if (folder/"texts.txt").exists() else ""
    if re.search(r"Sign in with Google|Σύνδεση.*Google|Subscribe|Συνδρομ|GEL PRO.*Access|PRO required",text,re.I):
        return "ACCESS_OR_ACCOUNT_GATE"
    if re.search(r"permission|δικαίω|Usage access|πρόσβαση χρήσης|Allow.*GEL|Να επιτρέπεται",text,re.I):
        return "PERMISSION_OR_SETTINGS_GATE"
    if re.search(r"ολοκληρώθηκε|finished|completed",text,re.I):
        return "COMPLETION_TEXT_OBSERVED_NOT_HARDWARE_VERIFIED"
    return "UI_OPENED_REQUIRES_INTERACTION_OR_REVIEW"

try:
    adb("logcat","-b","all","-c")
    adb("shell","settings","put","system","accelerometer_rotation","0")
    adb("shell","settings","put","system","user_rotation","0")
    select_language()
    # Inventory home at every scroll position.
    home()
    for page in range(6):
        save("home-page-"+str(page)); swipe()
    targets=["btnPhoneInfoInternal","btnPhoneInfoPeripherals","btnCpuRamLive","btnInternetSpeedTest",
             "btnCleanAll","btnBrowserCache","btnAppCache","btnAppManager","btnGuidedOptimizer","btnDiagnostics","btnDonate"]
    for target in targets:
        try:
            home(); n=find(rid(target),12)
            if n is None: record(target,"NOT_FOUND"); continue
            tap(n); time.sleep(2)
            folder=save(target)
            record(target,outcome(folder),folder=folder)
            adb("shell","input","keyevent","4")
        except Exception as e: record(target,"HARNESS_OR_UI_ERROR",str(e),save(target+"-error"))
    # Toolbar: inspect settings/contact/overflow without sending any message.
    home()
    for label,pattern in [("contact",r"^Contact$"),("overflow",r"More options|Περισσότερες επιλογές")]:
        home()
        n=find(lambda n: bool(re.search(pattern,n.get("text","")+" "+n.get("content-desc",""),re.I)))
        if n is not None:
            tap(n); record(label,"UI_OPENED",folder=save(label)); adb("shell","input","keyevent","4")
        else: record(label,"NO_ACCESSIBLE_LABEL")
    # Settings is currently an icon with an empty title; use actual toolbar bounds,
    # not guessed absolute screen coordinates.
    home(); toolbar=find(rid("gelToolbar"))
    if toolbar is not None:
        candidates=[n for n in toolbar.iter("node") if n.get("clickable")=="true" and visible(n)]
        unnamed=[n for n in candidates if not n.get("text") and not n.get("content-desc")]
        if unnamed:
            tap(unnamed[0]); record("settings","UI_OPENED",folder=save("settings"))
            n=find(textmatch(r"^(Save|Αποθήκευση)$"))
            if n is not None: tap(n); record("settings-save","ACTION_EXECUTED",folder=save("settings-save"))
        else: record("settings","NO_ACCESSIBLE_LABEL")
    sections=[(1,5),(6,9),(10,13),(14,17),(18,20),(21,24),(25,31)]
    for number in range(1,32):
        try:
            manual()
            lo,hi=next((a,b) for a,b in sections if a<=number<=b)
            end=30 if lo==25 else hi
            header=find(textmatch(r"Manual Labs\s*"+str(lo)+r"\s*[–—-]\s*"+str(end)),16)
            if header is None:
                record("lab-%02d"%number,"SECTION_NOT_FOUND",folder=save("lab-%02d-section"%number)); continue
            tap(header)
            button=find(textmatch(r"^(?:🔒\s*)?(?:PRO\s*)?"+str(number)+r"\."),12)
            if button is None:
                record("lab-%02d"%number,"LAB_NOT_FOUND",folder=save("lab-%02d-missing"%number)); continue
            tap(button)
            time.sleep(2)
            save("lab-%02d-open"%number)
            safe_interaction()
            time.sleep(3)
            folder=save("lab-%02d-result"%number)
            record("lab-%02d"%number,outcome(folder),"Physical measurements not validated by emulator.",folder)
            # Capture lower results without choosing any subjective hardware answers.
            swipe(); save("lab-%02d-lower"%number)
            adb("shell","input","keyevent","4")
        except Exception as e: record("lab-%02d"%number,"HARNESS_OR_UI_ERROR",str(e),save("lab-%02d-error"%number))
    # Reopen after force stop, rotate, and check offline rendering.
    home(); adb("shell","settings","put","system","user_rotation","1")
    time.sleep(2); record("landscape","UI_OPENED",folder=save("landscape"))
    adb("shell","settings","put","system","user_rotation","0")
    adb("shell","svc","wifi","disable"); adb("shell","svc","data","disable")
    home(); record("offline-relaunch","UI_OPENED",folder=save("offline-relaunch"))
finally:
    try: save("final-state")
    except Exception: pass
    try: (out/"logcat.txt").write_text(adb("logcat","-b","all","-d","-v","threadtime"))
    except Exception: pass
    (out/"coverage.json").write_text(json.dumps(results,ensure_ascii=False,indent=2))
    (out/"README.txt").write_text("UI evidence only. OPENED is not diagnostic correctness. No purchases, trial consumption, account deletion, phone calls, messages or subjective hardware answers. Hardware, Google/Play transactions and inaccessible flows remain unverified. Review screenshots and logs.\n")
    crash=(out/"logcat.txt").read_text() if (out/"logcat.txt").exists() else ""
    crashed = "Process: "+PKG+"," in crash
    print("APP_CRASH_LOG_PRESENT="+str(crashed),flush=True)
    if crashed: sys.exit(2)
