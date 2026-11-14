# GEL iDoctor — All 31 Android Labs: signed-v54 log-color parity audit

**Date:** 14 Nov 2026, QA source-edit review.  
**Installed APK reference (immutable):** signed v54 QA, versionCode 54, source commit `cf14d9814ab6d8c96725a6fd0ea52769224446ac`. **It is NOT updated by this source audit.**  
**QA source branch:** `qa/gel31-surgical-rebuild-20261113`, PR #107 (draft).  
**Protected backup:** `69fb2ab85d0528937dc257fc19abc6722462cfc8`. No edits to this ref, `main`, or Play Store.  
**Scope:** direct Manual Tests log-generation calls in `app/src/main/java/com/gel/cleaner/ManualTestsActivity.java`, benchmarked against the protected backup and the exact installed-v54 source blob. This is a **source-level color audit**, not the final runtime audit of all helper activities/popups/PDF output.

## Root cause (source verified)

| Source | `logLabelValue(...)` uses* | `logLabelOkValue(...)` uses* | Helper meaning |
|---|---:|---:|---|
| Protected backup | 125 | 314 | `logLabelValue()` delegated to **green** `logLabelOkValue()` |
| Installed signed v54 | 491 | 96 | `logLabelValue()` renders **white** (neutral) |
| QA source after targeted per-lab review | 419 | 186 | `logLabelValue()` **stays white**, verified positive facts use `logLabelOkValue()` |

*Simple name-token counts in the large Java source, **including method definitions**, used to show scale; they are **not** counts of confirmed executions, quality, changed lines or passed tests. Helper output and conditionals can affect what appears on the device.

The v54 change to `logLabelValue()` was not sufficient by itself: hundreds of lab call sites were changed to neutral, including genuine measurements. **Restoring the backup's global delegation would turn UNKNOWN / NOT TESTED / uncalibrated scores green. The owner expressly rejected that unsafe shortcut.**

## Binding GEL color contract

- **WHITE:** titles/labels; neutral explanations and conclusions; unknown, unavailable, not performed or not independently proven; uncalibrated/heuristic indices and their limitations.
- **GREEN:** numbers from an actually valid sample or platform-reported value, positively verified procedural completion, or an accurately scoped successful observation **not a warranty of device health**. Where a call has both measured and missing-data branches, make the color conditional.
- **YELLOW:** **only a true warning or need to retest**. Missing optional hardware, unmeasured capacity, no API, neutral scientific limitations are **not automatically warnings**.
- **RED:** confirmed test failure or a clearly detected problem; never an unsupported inference from missing public APIs.
- Preserve original computational methods, business rules, UI structure and cross-lab flow. Changes from this pass are confined to source logging presentation and splitting combined success+caveat text.

## Per-Lab decision ledger — one by one

| Lab | Direct log source review | QA source action / rationale |
|---|---|---|
| **1** Speaker | Reviewed, existing confirmed sound levels already green | **Keep** functioning positive styles; no broad rewrite |
| **2** Frequency/audio | Ambient RMS, four sampled tone RMS and 4/4 count had white values | **Patched:** measured values green when valid, unconfirmed/not measured neutral; unchanged frequency-validation limitations |
| **3** Earpiece | No indiscriminate color conversion; separately recorded premature closing and tone playback defect | **Keep pending runtime/root-cause work**, tracked in `V54-ANDROID-01` |
| **4** Mic | Bottom/top RMS and peak values lost green | **Patched:** values green **only if respective capture was completed**; error/cancel remains neutral |
| **5** Vibration | Existing completion/user-confirmation paths retained | **No color-only patch**; success must remain user-reported, not hardware certificate |
| **6** Display/touch | Full pattern cycle and user report lost success colors | **Patched:** actual pattern completion and no-anomaly user report green; *only observation* caveat remains white. Separate 90%-black OLED overlay fix already prepared in `DisplayProTestActivity` |
| **7** Rotation/proximity | Measured change and recorded sensor transitions white | **Patched:** specific verified observation green; portrait/in-call limitations white |
| **8** Camera | Measured focal length, preview resolution, luma range and camera counts neutralized; absent optional depth/flash shown as warnings | **Patched:** reported measurements green, absence of optional features neutral. **No changes to suspected black-frame algorithm**, which has separate defect `V54-ANDROID-09` |
| **9** Sensor inventory | All counts and Android-reported sensor entries white | **Patched:** count, enumerated sensor identifiers and present-known sensor entries green as **registry observations only**; functional response and health explicitly remain untested/white |
| **10** Wi-Fi/deep scan | Band, PHY speed, RSSI, observed probes and completed DeepScan incorrectly neutralized | **Patched:** valid readings and successful probes green; `UNKNOWN`, unreachable/filtered ports and uncalibrated SpeedSim remain neutral |
| **11** Mobile data/SIM | Android-reported ready-SIM value white | **Patched:** ready observation green, missing/not verified neutral |
| **12** Call/voice | SIM-ready and legacy service-state positive values white | **Patched:** *only* OS-reported ready/in-service indicators green; actual call not magically verified |
| **13** Bluetooth | Enabled state and monitored evidence count/adapter stability lost green | **Patched:** only known successful facts green, zero recorded disconnects green; unknown/NOT TESTED white |
| **14** Battery stress | Valid cycle count and experimental voltage/sag samples neutralized | **Patched:** cycle count green only if reported, valid numerical samples green; uncalibrated electrical proxies, SoH and unavailable values remain neutral |
| **15** Charging | Main lab already uses explicit positive and warning/error helpers, no neutral `logLabelValue` call in central Lab15 method region | **No unnecessary rewrite** |
| **16** Thermal | Recorded thermal sensor temperature lines and normal peak white | **Patched:** actual readings green when within the source's existing normal threshold; yellow/red for existing elevated thresholds; unknown retains neutral handling |
| **17** Automatic battery reliability | Remaining plain values describe uncalibrated legacy index or non-certifying interpretation | **Keep neutral**; existing verified flows retain separate color calls |
| **18** Storage | Valid storage used/free snapshot all white | **Patched:** actual capacity snapshot green, uncalibrated pressure/fault indices white |
| **19** RAM | Valid RAM usage, observed MemFree/Cached/Swap all white | **Patched:** actual captured values green, heuristic memory-pressure interpretation neutral |
| **20** Uptime | Current elapsed time and successful recorded observation lost green | **Patched:** Android-measured uptime and factual record success green; reboot history claims unverified/white |
| **21** Biometrics/lock | Neutral fields largely untested/unknown security layers; existing real authentication success path has explicit positive logging | **Keep neutral for unproven hardware/security claims** |
| **22** Patch level/Play indicators | Android-reported patch and detected settings-module facts neutralized | **Patched:** OS-reported facts green, *Play Protect active status not certified*, missing/unreadable values white |
| **23** Developer options/ADB | USB and Developer Options actually OFF shown plain white | **Patched:** observed OFF settings green, ON warning remains separate, unknown unreadable settings neutral |
| **24** Root/Verified Boot | Source report deliberately separates unverified root/boot claims | **Keep neutral** unless a genuinely confirmed result is available; no blanket green security verdict |
| **25** Crash logs | Neutral items describe incomplete inaccessible history and unavailable confidence | **Keep neutral**; no inventing green crash-free certificate |
| **26** Apps impact | Actual enumerated app/category counts white | **Patched:** visible package counts green; heuristic risk/capability ranking and limited visibility neutral |
| **27** Permission inventory | Real visible-app and granted-permission counts white | **Patched:** factual counts green; internal permission-weight scores and privacy judgment stay neutral |
| **28** Interconnect/stability | Plain lines are missing evidence, unsupported PMIC/baseband claims and uncalibrated symptom indices | **Keep neutral**, explicit actual warnings retain warning handling |
| **29** Authenticity/parts | Valid battery percentage/voltage, display Hz and available camera/core sensors white | **Patched:** measured/reported facts green **in valid-data branches only**; OEM authenticity still UNKNOWN |
| **30** Technician summary | Raw thermal, RAM, storage and uptime values white alongside uncalibrated composite scores | **Patched:** available observed numerical values green with guards; all model scores, cause/confidence and unvalidated health verdicts stay neutral |
| **31** Final evidence summary | All plain entries are limitations, non-certified evidence, or uncalibrated estimates | **Keep neutral**; never green-label physical battery SoH or OEM integrity from unvalidated data |

**Important:** `Lab 8.1` camera capabilities is a sublab of Lab 8 and was separately inspected: numeric camera inventory now green; subjective “very good photo quality” claims deliberately left neutral **pending accuracy correction**. The `Lab 22` source is OS Security Patch + Play Protect (not NFC despite obsolete header comment at file top); manual-source mapping was verified at the actual method rather than relying on the stale introductory index.

## Surgical commit trail

- `de8147f` — Labs 2, 4; both-mic and tone RMS.
- `43914d4` — Labs 6, 7; green verified facts and separate neutral caveats.
- `f5f1edc` — Labs 8/8.1, 9; camera/sensor measures, absent option neutral.
- `e6f289c` — Labs 9, 10; present sensors and known network readings.
- `fc18e9f` — Labs 11, 12, 13.
- `6a82215` — Labs 14, 16.
- `68b001f` — Labs 18–20.
- `914f0dc` — Labs 26, 27.
- `770fb03` — Lab 29.
- `5fa1d76` — Lab 30.
- `3f866b8` — Labs 22, 23.

**CI cost gate:** `5796744` explicitly **disabled automatic signed-APK/emulator builds per intermediate QA source commit**. Re-enable only for a single consolidated signed debug/QA candidate after final code review, exact-HEAD compile+unit tests and review of outstanding functional defects.

## Remaining acceptance gates — do NOT mark this CLOSED prematurely

1. **Latest application-source Java compile/unit test CI: PASS.** [GitHub run 43535670305](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/43535670305), triggered from source commit `3f866b8b5eab664a956c86b416220ced9b8687ea`, completed `:app:compileDebugJavaWithJavac`, `:app:testDebugUnitTest`, `BUILD SUCCESSFUL in 40s`. Later GitHub changes are documentation-only. This passes source compilation and unit tests, **not** visual acceptance of 31 Labs.
2. **Cross-branch tests:** compare the protected backup and new source for each Lab's method+helper dependencies, no regression in original calculations, TTS, dialogs, Exit, user controls, popups and locale.
3. **Physical screenshot parity:** Android 13 Xiaomi, both Greek and English; verify WHITE labels/GREEN measured values and neutral WHITE unknowns across **all 31 Labs**. The previous v54 APK remains unchanged during testing.
4. **PDF path:** on-screen color changes are not automatically equivalent to PDF color preservation. TXT clipping and HTML PDF export failure remain independent open issues.
5. **Diagnostic correctness:** camera black-frame false positives, Apple panic classifier and other previously documented issues need independent fixes; color correctness does **not** close them.

**Disposition:** targeted QA source improvements are prepared, **not deployed, merged, published or proven on-device**. All 31 Labs have been classified for direct central logging; further subactivity/helper scans and final UI verification remain required.
