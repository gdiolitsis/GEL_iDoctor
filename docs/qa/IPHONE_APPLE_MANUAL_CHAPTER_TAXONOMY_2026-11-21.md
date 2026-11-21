# GEL iDoctor — Official Apple iPhone Manual Chapter Index
Review date: 2026-11-21
Status: **Chapter-title index complete for the 30 official iPhone Repair Manuals in the present Apple model directory; procedure text and individual chapter deep links are not copied.**

## The source-of-truth hierarchy
1. **Apple model manual:** an official Apple Support master URL from `iphone_apple_official_guidance_v1.tsv`, mapped to a verified hardware identifier only when one is available.
2. **Safety + initial check:** official manual table-of-contents entries for Battery Safety, Broken Glass Safety and Quick Checks.
3. **Symptom troubleshooting:** exact manual topic headings such as unexpected restart, camera, display, sound, power and sensors. Being listed in an Apple manual does not establish that an imported GEL panic code diagnoses that problem.
4. **Repair procedures:** exact parts/procedure headings that Apple includes for that model. **Presence in the contents is not Apple's recommendation to perform the repair for this panic log.** Even if a model manual contains a logic-board procedure, that is not automatic justification for logic-board replacement.
5. **Post-repair checks:** from Apple's separate, conditional `Repair Assistant` documentation. Use only after an eligible repair, subject to actual model, OS and part support.

## Data structures and guardrails
- Source index: `app/src/main/assets/iphone_apple_official_guidance_v1.tsv` (model manuals and Apple symptom/diagnostic pages, 54 source records).
- New per-manual chapter index: `app/src/main/assets/iphone_apple_manual_chapters_v1.tsv` (**983** indexed chapter/part titles; 407 repair/preparation procedure headings).
- Parser: `AppleIPhoneManualChapters.java`. Validates master-manual identity and official URL, preserves chapter phase, original heading, Greek summary heading, and whether the link reaches the manual contents or a specifically verified chapter.
- Exact model selection: `AppleOfficialServiceGuidance.modelManual()` only. Directory-only manuals are browsable in the corpus but never automatically attached to a guessed hardware ID.
- UI: iPhone Lab 4 first shows manufacturer source and applicable conditional diagnostic chapters, then a neutral inventory of that model's available procedures. Only afterwards does it print the independently sourced repair-history corpus. Lab 8 remains a summary.

**Never display a replacement suggestion solely because an extracted code matches a topic.** Exact code/model forum outcomes and official Apple documentation are separate evidence categories. All Apple chapter citations open the authoritative official URL; only individually verified chapter deep links bypass the manual contents.

## Verified phase inventory
| Phase | Indexed chapter entries |
|---|---:|
| SAFETY | 60 |
| CHECK | 30 |
| DIAGNOSTICS | 30 |
| TROUBLESHOOT | 426 |
| REFERENCE | 30 |
| PROCEDURE | 407 |
| **Total** | **983** |

## Per-model problem and procedure coverage

| Manual | Symptom headings | Listed repair/preparation procedures | URL scope |
|---|---:|---:|---|
| [iPhone 12 Pro Max Repair Manual](https://support.apple.com/en-gb/101538) | 14 | 10 | Official manual contents |
| [iPhone 13 Pro Max Repair Manual](https://support.apple.com/en-us/100430) | 14 | 12 | Official manual contents |
| [iPhone 14 Repair Manual](https://support.apple.com/en-us/100308) | 14 | 12 | Official manual contents |
| [iPhone 14 Plus Repair Manual](https://support.apple.com/en-us/100322) | 14 | 12 | Official manual contents |
| [iPhone 14 Pro Repair Manual](https://support.apple.com/en-us/100370) | 14 | 11 | Official manual contents |
| [iPhone 14 Pro Max Repair Manual](https://support.apple.com/en-la/100357) | 14 | 11 | Official manual contents |
| [iPhone 15 Repair Manual](https://support.apple.com/en-us/104900) | 14 | 14 | Official manual contents |
| [iPhone 15 Pro Repair Manual](https://support.apple.com/en-ie/104872) | 14 | 11 | Official manual contents |
| [iPhone 16 Pro Repair Manual](https://support.apple.com/en-us/120803) | 14 | 15 | Official manual contents |
| [iPhone 17 Pro Repair Manual](https://support.apple.com/en-us/124262) | 15 | 16 | Official manual contents |
| [iPhone 18 Pro Repair Manual](https://support.apple.com/el-gr/127388) | 15 | 22 | Official manual contents |
| [iPhone 12 Repair Manual](https://support.apple.com/en-us/100464) | 14 | 10 | Official manual contents |
| [iPhone 13 Repair Manual](https://support.apple.com/en-us/100402) | 14 | 12 | Official manual contents |
| [iPhone SE 3rd generation Repair Manual](https://support.apple.com/en-us/101527) | 14 | 7 | Official manual contents |
| [iPhone 16 Repair Manual](https://support.apple.com/en-us/120652) | 14 | 15 | Official manual contents |
| [iPhone 17 Repair Manual](https://support.apple.com/en-us/123029) | 15 | 16 | Official manual contents |
| [iPhone 12 mini Repair Manual](https://support.apple.com/en-us/100452) | 14 | 9 | Official manual contents |
| [iPhone 12 Pro Repair Manual](https://support.apple.com/en-us/100476) | 14 | 10 | Official manual contents |
| [iPhone 13 mini Repair Manual](https://support.apple.com/en-us/100384) | 14 | 11 | Official manual contents |
| [iPhone 13 Pro Repair Manual](https://support.apple.com/en-us/100416) | 14 | 12 | Official manual contents |
| [iPhone 15 Plus Repair Manual](https://support.apple.com/en-us/104858) | 14 | 14 | Official manual contents |
| [iPhone 15 Pro Max Repair Manual](https://support.apple.com/en-us/104917) | 14 | 11 | 1 direct chapter + manual contents |
| [iPhone 16 Plus Repair Manual](https://support.apple.com/en-us/120662) | 14 | 15 | Official manual contents |
| [iPhone 16 Pro Max Repair Manual](https://support.apple.com/en-us/120819) | 14 | 15 | Official manual contents |
| [iPhone 17 Pro Max Repair Manual](https://support.apple.com/en-us/124278) | 15 | 16 | Official manual contents |
| [iPhone Air Repair Manual](https://support.apple.com/en-us/123052) | 15 | 15 | Official manual contents |
| [iPhone 17e Repair Manual](https://support.apple.com/en-us/125956) | 14 | 14 | Official manual contents |
| [iPhone 16e Repair Manual](https://support.apple.com/en-us/121720) | 14 | 15 | Official manual contents |
| [iPhone 18 Pro Max Repair Manual](https://support.apple.com/en-la/127626) | 15 | 22 | Official manual contents |
| [iPhone Duo Repair Manual](https://support.apple.com/en-us/127682) | 14 | 22 | Official manual contents |

## Meaning of the chapter phases
- `SAFETY`: battery and broken-glass precautions before physical work.
- `CHECK`: rapid initial checks; do not assume they were performed by GEL.
- `DIAGNOSTICS`: Apple Diagnostics navigation, subject to separate eligibility checks; not a result measured through Android.
- `TROUBLESHOOT`: an official symptom topic, used only conditionally if that issue is observed; the Apple title does **not** imply a defective part.
- `REFERENCE`: official internal layout, parts, screws, tools and preparation.
- `PROCEDURE`: an actual chapter heading describing work that may be performed by a qualified technician, after diagnosis and model/variant verification.

## Concrete model differences preserved
- iPhone 12/13/14/15/16 manuals commonly group battery, power and wired charging; iPhone 17/18 manuals split them into separate troubleshooting entries.
- iPhone 15 Pro / Pro Max have a Back Glass procedure but lack a standalone USB-C Connector chapter in the listed procedures, whereas iPhone 15 and 15 Plus list one.
- iPhone 18 Pro/Pro Max include eSIM/pSIM-specific microphone, speaker, connector, housing and mmWave logic-board procedures, which cannot be generalized to other models.
- iPhone Duo has its own Hinge Issues, Apple Pencil, Cellular, dual-battery, dual-display and input/output board entries; these must never appear as device-specific findings on conventional iPhones.

## Remaining research, not silently implied complete
1. Collect and validate **individual Apple chapter URLs** for every remaining chapter beyond its verified table of contents. **93 direct chapter URLs** are now backed by explicit Apple manual links for iPhone 14 Pro, iPhone 15 Pro Max, and iPhone 17; **890** entries still safely open their own model manual's contents.
2. Validate service part eligibility, regional variants and iOS requirements before the app presents any model-specific post-repair workflow as usable.
3. Track updates/withdrawals and record source review dates per chapter; validate all new URLs against Apple's current published pages.
4. QA the Lab 4 UI and exported HTML/PDF in Greek and English with several logs and model-mismatch tests.

This index is a verified navigation taxonomy, **not a copy of full manual instructions** or a guarantee that Apple fixes exist for every panic code.

## Direct chapter + source-reviewed step layer — 2026-11-21

### Newly implemented
- **93 / 983** chapter entries now point to their actual chapter pages rather than only a general manual Contents page. These were extracted from the clickable official Apple manual Contents of iPhone 14 Pro (exact hardware ID `iPhone15,2`), iPhone 15 Pro Max and iPhone 17. This is link evidence, not proof that the individual chapter text has been fully audited.
- Asset `iphone_apple_service_steps_v1.tsv`: **56 bilingual original short-stage summaries across 9 individually read official chapters**. No source-uncited or generated generic replacement steps are allowed. The full official page remains mandatory for physical service.
- Parser `AppleIPhoneServiceSteps.java` cross-checks every row against a `DIRECT_CHAPTER` record, the exact chapter URL, stage type, contiguous order, review date, and essential safety/preparation/removal/reassembly/verification stages for physical procedures. An unreviewed chapter can still have an authoritative direct link, but **cannot** falsely acquire a step-by-step summary.
- iPhone Lab 4 now prints **individual official chapter links** (or explicit Contents-only fallback) for diagnostic topics and all repair procedure headings. Users can optionally select `APPLE — Αναλυτικά επιβεβαιωμένα βήματα` to read concise source-reviewed stages in a dark dialog and open the full official Apple page in their browser. It is not a ninth lab and never silently authorizes a hardware repair.
- Automatic step-card selection only for a verified hardware-model ID. Directory-only manual variants require explicit user model selection. Links are never matched from a guessed model based on SMC/MIC/PRS0 clues.

### Source-reviewed step chapter samples
| Model / official chapter | Apple page | Summary stages |
|---|---|---:|
| iPhone 14 Pro — SIM Tray (only physical-SIM variants; no US iPhone 14 SIM tray) | https://support.apple.com/en-us/100375 | 6 |
| iPhone 14 Pro — Apple Diagnostics for Self Service Repair | https://support.apple.com/en-us/101965 | 6 |
| iPhone 14 Pro — Camera or LiDAR troubleshooting | https://support.apple.com/en-us/101967 | 7 |
| iPhone 14 Pro — TrueDepth/Face ID troubleshooting | https://support.apple.com/en-us/101967 | 7 |
| iPhone 15 Pro Max — camera removal/reassembly | https://support.apple.com/en-us/104924 | 6 |
| iPhone 15 Pro Max — TrueDepth camera removal/reassembly | https://support.apple.com/en-us/104926 | 6 |
| iPhone 15 Pro Max — back glass removal/reassembly | https://support.apple.com/en-us/104929 | 6 |
| iPhone 15 Pro Max — Apple Diagnostics for Self Service Repair | https://support.apple.com/en-us/101965 | 6 |
| iPhone 17 — back glass removal/reassembly | https://support.apple.com/en-us/123038 | 6 |

### Model-specific safety rules from Apple that must remain visible
- TrueDepth camera includes infrared laser components. The iPhone 15 Pro Max manual warns not to disassemble the TrueDepth assembly and states that Repair Assistant **is required** after replacement to enable security functions.
- The iPhone 15 Pro Max back-glass chapter includes model-specific broken-glass preparation/sanding, heating/adhesive and flex-cable protection, and repair verification; stop for damaged battery. Repair Assistant may be recommended after completing the work.
- The iPhone 14 Pro SIM tray page explicitly states that US-purchased iPhone 14 models do not have physical SIM trays.
- Apple's current direct diagnostics and browser console modes have distinct requirements. The online secondary-device workflow requires an eligible model, iOS 17+ and Wi-Fi; the on-device workflow requires eligible iPhone 12+ and iOS 18+. A serial number may be entered on Apple's own diagnostic website but GEL neither requests nor uploads it.

### Remaining significant work
1. **890 entries** still need direct chapter URLs; most also require a read of the actual procedure, not only a title. Until checked, they remain `MANUAL_CONTENTS`.
2. **974 chapter entries** still lack source-reviewed stage summaries; some are tool, safety or reference sections where staged repair instructions may not apply. Do not mechanically generate steps for them.
3. Add explicit, reliable model-ID binding to the 18 directory-only manuals before any model-specific automatic presentation. Explicit user navigation remains possible.
4. Verify user interaction, tap-to-open links, Greek/English rendering and HTML/PDF export with real panic logs before shipping.
5. Periodically re-open Apple pages for changes; Apple-authored procedures may change without a stable versioned URL.

These short stages are **navigation and interpretation**, not a copy of the full illustrated model repair manual. Critical numeric screw torque, adhesive temperatures and drawings remain exclusively at the source URL, and may vary by region/model.

## Phase II implementation status (2026-11-21)

- **93 directly linked chapters** sourced from Apple's own contents navigation for iPhone 14 Pro, iPhone 15 Pro Max and iPhone 17. Link discovery is distinct from manual-text review; the rest retain their own official table-of-contents links.
- **56 original Greek/English procedural stage notes across 9 individually reviewed chapters** (physical repairs and Apple's diagnostic workflows), stored in `iphone_apple_service_steps_v1.tsv`. Read with `AppleIPhoneServiceSteps.java`; notes cannot be assigned to an unrelated manual or to a non-direct source.
- **New optional Lab 4 navigation:** select audited Apple chapter after recognizing an exact supported hardware model, or explicitly select the commercial device model for reference when the exact ID is not established. The dark dialog shows reviewed stage summaries and opens the manufacturer's illustrated procedure. This is **not** a recommendation to replace the part named in a panic log.
- **Source-corrected Apple Diagnostics:** the distinct on-device iOS 18+ and secondary-browser iOS 17+ routes from official Apple Support 101965 are now independently indexed. The device's actual OS, tests and results cannot be inferred from imported panic logs.
- **53 is superseded by 54 official-source records** because the Apple Diagnostics modes reference is now additionally indexed. Historical independent repair counts remain unchanged.
