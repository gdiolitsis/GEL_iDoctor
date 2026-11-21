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
- Source index: `app/src/main/assets/iphone_apple_official_guidance_v1.tsv` (model manuals and Apple symptom/diagnostic pages, 53 source records).
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
1. Collect and validate **individual Apple chapter URLs** for every chapter beyond its manual's verified table of contents. Except for the single independently inspected `iPhone 15 Pro Max Back Glass` chapter, links intentionally take the reader to the correct manual's contents.
2. Validate service part eligibility, regional variants and iOS requirements before the app presents any model-specific post-repair workflow as usable.
3. Track updates/withdrawals and record source review dates per chapter; validate all new URLs against Apple's current published pages.
4. QA the Lab 4 UI and exported HTML/PDF in Greek and English with several logs and model-mismatch tests.

This index is a verified navigation taxonomy, **not a copy of full manual instructions** or a guarantee that Apple fixes exist for every panic code.
