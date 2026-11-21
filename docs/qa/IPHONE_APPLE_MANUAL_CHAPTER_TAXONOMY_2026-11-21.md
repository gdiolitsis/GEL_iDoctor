# GEL iDoctor — Apple iPhone Repair Manual chapter taxonomy

**Verified on 2026-11-21.** This is the authoritative current coverage summary; chronological older snapshots are superseded by the source-linked audit report.

## Scope and audited counts

- **30 official iPhone Repair Manuals** from Apple's [iPhone models document directory](https://support.apple.com/en-us/docs/iphone).
- **1,074 individual chapter entries**, each with a direct `support.apple.com` numeric article URL extracted from the official model's table of contents. **0** entries still point only at the manual landing page.
- **54 separate official-source catalog records:** 30 model manuals and 24 official symptom/diagnostic/service documents.
- **62 independently written Greek/English short procedure-stage notes covering 10 individually read Apple chapters.** The other direct links do **not** imply their full step text was reviewed, downloaded or copied.
- **12** manuals are linked automatically to independently verified hardware IDs; **18** are directory/reference entries and require explicit commercial-model selection unless a hardware ID is later verified.
- **407** listed repair/preparation procedure headings. These are *not* 407 certified GEL panic repairs or replacements Apple recommends for a panic code.

| Phase | Official chapter links |
|---|---:|
| SAFETY | 60 |
| CHECK | 30 |
| DIAGNOSTICS | 30 |
| TROUBLESHOOT | 440 |
| REFERENCE | 107 |
| PROCEDURE | 407 |
| **Total** | **1074** |

## Source separation and display rules

The iPhone Lab 4 workflow remains **official Apple source → conditional relevant troubleshooting → separate independent repair-history cases**.

1. Link a manual to an imported panic log only if the exact internal hardware model ID is verified. A commercial-model manual without a verified ID may be shown only after an explicit model selection *for reference*, never as a machine-inferred diagnosis.
2. Present official Battery Safety and Broken Glass Safety before any physical procedure. First use Quick Checks and supported diagnostics for actual observed symptoms.
3. Display conditional official troubleshooting chapters for the indicated category. An observed `watchdog`, `thermal shutdown` or SMC clue does not mean Apple diagnosed a faulty part.
4. Show each model's available physical procedure headings as a **neutral catalog**. The official chapter link opens the complete Apple instructions, with exact illustrated tools, screws, temperatures, precautions and OS/part eligibility.
5. Apple Diagnostics for Repair has distinct OS eligibility (on-device iOS 18+, eligible secondary-device browser route iOS 17+) and must not be treated as a measurement performed by GEL. Likewise Repair Assistant applies only *after* supported part repairs.
6. The separately curated repair history includes published attempts with successes, failures and the author's original URL. It is not certified by Apple and does not become an Apple recommendation.

These model-manual chapter URLs are **authoritative links**, not copies of copyrighted manuals or stage-by-stage instructions. The user device does not download all articles or transmit imported panic logs.

## Normalization changes discovered during the source audit

- The initial flat taxonomy had **983** headings, including 30 non-clickable `Views, Parts, and Tools` grouping labels. These were replaced, depending on the actual model manual, by **four older-style** direct references (`Internal View`, `Orderable Parts`, `Screws`, `Tools`) or **three newer-style** direct references (`Internal View, Orderable Parts, and Exploded View`, `Screws`, `Tools`). No fake URL was assigned to the grouping itself.
- Some later Apple manuals repeat the exact heading `Ambient light sensor, proximity sensor, or compass issues` in both **Display** and **Mechanical**, linking to different official service articles (e.g. `101966` and `101970`). Those records now remain separate instead of arbitrarily picking one.
- For iPhone 18 Pro/Pro Max the official wording is `Vibration or haptic feedback issues`, not the older `Taptic Engine or haptic feedback issues`.
- The iPhone 14 `Repair Videos` official link was confirmed as [Apple Support 122308](https://support.apple.com/en-us/122308).
- The hidden Unicode formatting marks in a few older model headings (e.g. Camera/Speaker) are normalized only for title matching. All direct links still come from Apple's actual TOC anchors.

## Per-manual chapter coverage

| Official model manual | Phase titles | Direct Apple chapter links | Auto hardware ID? |
|---|---:|---:|---|
| [iPhone 12 Pro Max Repair Manual](https://support.apple.com/en-gb/101538) | 32 | 32 | Exact ID verified |
| [iPhone 13 Pro Max Repair Manual](https://support.apple.com/en-us/100430) | 34 | 34 | Exact ID verified |
| [iPhone 14 Repair Manual](https://support.apple.com/en-us/100308) | 34 | 34 | Exact ID verified |
| [iPhone 14 Plus Repair Manual](https://support.apple.com/en-us/100322) | 34 | 34 | Exact ID verified |
| [iPhone 14 Pro Repair Manual](https://support.apple.com/en-us/100370) | 33 | 33 | Exact ID verified |
| [iPhone 14 Pro Max Repair Manual](https://support.apple.com/en-la/100357) | 33 | 33 | Exact ID verified |
| [iPhone 15 Repair Manual](https://support.apple.com/en-us/104900) | 37 | 37 | Exact ID verified |
| [iPhone 15 Pro Repair Manual](https://support.apple.com/en-ie/104872) | 33 | 33 | Exact ID verified |
| [iPhone 16 Pro Repair Manual](https://support.apple.com/en-us/120803) | 37 | 37 | Exact ID verified |
| [iPhone 17 Pro Repair Manual](https://support.apple.com/en-us/124262) | 39 | 39 | Directory only |
| [iPhone 18 Pro Repair Manual](https://support.apple.com/el-gr/127388) | 45 | 45 | Directory only |
| [iPhone 12 Repair Manual](https://support.apple.com/en-us/100464) | 32 | 32 | Exact ID verified |
| [iPhone 13 Repair Manual](https://support.apple.com/en-us/100402) | 34 | 34 | Exact ID verified |
| [iPhone SE 3rd generation Repair Manual](https://support.apple.com/en-us/101527) | 29 | 29 | Exact ID verified |
| [iPhone 16 Repair Manual](https://support.apple.com/en-us/120652) | 37 | 37 | Directory only |
| [iPhone 17 Repair Manual](https://support.apple.com/en-us/123029) | 39 | 39 | Directory only |
| [iPhone 12 mini Repair Manual](https://support.apple.com/en-us/100452) | 31 | 31 | Directory only |
| [iPhone 12 Pro Repair Manual](https://support.apple.com/en-us/100476) | 32 | 32 | Directory only |
| [iPhone 13 mini Repair Manual](https://support.apple.com/en-us/100384) | 33 | 33 | Directory only |
| [iPhone 13 Pro Repair Manual](https://support.apple.com/en-us/100416) | 34 | 34 | Directory only |
| [iPhone 15 Plus Repair Manual](https://support.apple.com/en-us/104858) | 37 | 37 | Directory only |
| [iPhone 15 Pro Max Repair Manual](https://support.apple.com/en-us/104917) | 33 | 33 | Directory only |
| [iPhone 16 Plus Repair Manual](https://support.apple.com/en-us/120662) | 37 | 37 | Directory only |
| [iPhone 16 Pro Max Repair Manual](https://support.apple.com/en-us/120819) | 37 | 37 | Directory only |
| [iPhone 17 Pro Max Repair Manual](https://support.apple.com/en-us/124278) | 39 | 39 | Directory only |
| [iPhone Air Repair Manual](https://support.apple.com/en-us/123052) | 38 | 38 | Directory only |
| [iPhone 17e Repair Manual](https://support.apple.com/en-us/125956) | 36 | 36 | Directory only |
| [iPhone 16e Repair Manual](https://support.apple.com/en-us/121720) | 37 | 37 | Directory only |
| [iPhone 18 Pro Max Repair Manual](https://support.apple.com/en-la/127626) | 45 | 45 | Directory only |
| [iPhone Duo Repair Manual](https://support.apple.com/en-us/127682) | 43 | 43 | Directory only |

## Assets, regression tests and link-audit process

- `app/src/main/assets/iphone_apple_official_guidance_v1.tsv`: manufacturer source ledger.
- `app/src/main/assets/iphone_apple_manual_chapters_v1.tsv`: normalized chapter IDs, phase, topic, bilingual title, **direct manufacturer URL**, `DIRECT_CHAPTER` provenance marker.
- `app/src/main/assets/iphone_apple_service_steps_v1.tsv`: **62** bilingual short stage descriptions across 10 individually inspected chapters, each with a review date and its direct Apple source. The full illustrated manual is still required for physical work.
- `app/src/main/java/com/gel/cleaner/AppleIPhoneManualChapters.java` and `AppleIPhoneServiceSteps.java`: strict model/URL binding, optional audited detail display.
- `tools/iphone/sync_apple_repair_manual_urls.py`: reads **30 live Apple model-manual pages**, maps exact named TOC anchors, splits legacy/newer reference headings, preserves two separate display/mechanical sensor sources, refuses non-Apple sources and conflicting replacements.
- `docs/qa/IPHONE_APPLE_MANUAL_LINK_AUDIT_2026-11-21.md`: reproducible current source audit, per-manual counts, unresolved cases (expected zero), and remote fetch results.
- `app/src/test/java/com/gel/cleaner/AppleIPhoneManualChaptersTest.java` and `AppleIPhoneServiceStepsTest.java`: ensure complete source linkage, exact models, absence of invented part diagnoses and validated stage summaries.

## Remaining QA and ongoing work

The **direct chapter link inventory is complete for the 30 Apple Repair Manuals currently indexed**. This is **not** a claim that every Apple support page or every manual's full text is now covered.

- Continue individually reading other relevant diagnostics and procedure chapters. Only 10 chapters currently have source-reviewed bilingual stage outlines.
- Periodically refresh URLs and applicability after Apple updates its documentation, changes product support, or renames a section.
- Independently verify internal hardware IDs for the 18 directory-only manuals before model-specific automatic output.
- On a physical Android device, verify Greek/English labels, clickable official links, app navigation, and HTML/PDF exports before shipping a new APK.
- Preserve the original Apple source and a separate independent repair evidence record, including unsuccessful attempts.

