# GEL iDoctor — Apple official iPhone service documentation

**CURRENT LINK-COVERAGE RESULT — 2026-11-21:** 30/30 live Apple iPhone Repair Manuals were checked; the normalized chapter index has **1,074/1,074 direct official Apple chapter URLs, zero Contents-only placeholders and zero unresolved source conflicts**. The older numerical descriptions below are historical progress checkpoints. The authoritative current per-model reference matrix and remaining procedure-step limitations are in [IPHONE_APPLE_MANUAL_CHAPTER_TAXONOMY_2026-11-21.md](IPHONE_APPLE_MANUAL_CHAPTER_TAXONOMY_2026-11-21.md). This is source-link completeness, **not** a claim that all 1,074 article bodies were individually reviewed or copied into the APK.

Research date: 2026-11-21. Source authority: **Apple Support only**.

## Non-negotiable evidence boundary
**These Apple publications are not an Apple list mapping panic-full, SMC, MIC2, PRS0, watchdog or I2C codes to confirmed replacement parts.** They offer symptoms, diagnostics, safe repair procedures and post-repair checks. GEL uses them as **independent procedural references**, never as additional successful-repair cases. Do not label any forum fix "Apple-confirmed" based on these manuals.

## Source files and integration
- Offline catalog: `app/src/main/assets/iphone_apple_official_guidance_v1.tsv`. **54 curated Apple-source records:** 12 exact-model repair-manual mappings, 24 symptom/diagnostic/recovery/power service references, and 18 model manuals indexed without unverified hardware identifiers. The iPhone-only symptom pages cover audio, display, camera, Face ID, Touch ID, thermal symptoms, liquid connector alerts, recovery/restore, cellular service and calls, as well as power and charging.
- Parser: `AppleOfficialServiceGuidance.java`. Requires `support.apple.com` source URLs, model-matching for manuals, and applicability for diagnostics. It does **not** accept or interpret panic codes.
- iPhone Lab 4: **official-first** presentation for all imported panic logs: exact-model Apple manual where indexed, unexpected restart instructions, supported Apple diagnostics, and a bounded number of symptom-only pages relevant to literal GEL clues. Only afterward are independent model+clue repair attempts shown, retaining successes, failures and source URLs. Topic selection is independent of forum repair actions and never a claim that Apple diagnoses the indicated component.
- iPhone Lab 8: concise case-count and event summary pointing back to Lab 4 for the Apple-first source references and individual reported outcomes, avoiding duplicate recommendations.
- Curated official and third-party evidence URLs in Labs 4 and 8 are **tap-to-open** via the user's browser, not automatically fetched. The service report retains plain source URLs; HTML logs keep neutral blue information values while GEL-derived clue IDs remain white.
- Lab 7 continues evaluating recurring panic *clues* independently; it does not treat Apple support instructions as a finding.
- The independent historic outcomes catalog remains `iphone_panic_repair_knowledge_v1.tsv`, with **26 repair attempts**, 13 first-hand/shop-reported successes, 11 failures, 1 partial/recurrent, 1 unresolved. No additional "success" rows are created from Apple manuals.

## Verified Apple troubleshooting content

### 1. Unexpected freeze/restart/shutdown
Apple's service workflow lists restart loops, freezes, unexpected shutdowns and color flashes as symptoms. It advises checking software/symptoms, whether multiple Apple apps are affected, and performing supported diagnostics (including Mobile Resource Inspector) before hardware replacement. The latest article also limits some specific logic-board replacement steps to newer models; **never assume such replacement is recommended for older phones based solely on a panic log**.
Source: https://support.apple.com/en-us/101969 (section: Troubleshooting unexpected freeze, restart, or shutdown issues).

### 2. Wired charging
Check cable, adapter, port cleanliness and wired/wireless behavior first. Apple describes inspection and reseating of internal dock and battery-flex connectors **by qualified repair personnel using the model repair manual**, then re-testing. Some current models have model-specific parts pathways. This is a *symptom-based* workflow, not an Apple explanation of `MIC2` or `SMC 0x40000`.
Source: https://support.apple.com/en-us/101969 (section: Troubleshooting wired charging issues).

### 3. Battery and wireless charging
Apple includes MRI where supported, symptom checks and post-repair testing. It explicitly warns that replacing a part may not resolve the issue. Do not call battery or wireless charging hardware defective merely because it appears in a forum panic-code mapping.
Source: https://support.apple.com/en-us/101969.

### 4. Diagnostics availability
Apple's 2026 Recovery Assistant diagnostics-mode article lists **iPhone 14 models and later**, requires Wi-Fi, and describes tests including component presence (Mobile Resource Inspector), audio/microphones, display, Multi-Touch, Face ID and camera. The lab cannot execute these iPhone tests from an Android import; it only provides a reference for testing the actual device. Older devices do not receive this specific Recovery Assistant diagnostics entry automatically in the GEL catalog.
Source: https://support.apple.com/en-us/101944.

### 5. Repair Assistant / calibration
The current Apple publication (5 November 2026) lists eligible battery, display and camera repair/calibration pathways for supported iPhone 12/13/14/15/16 models, among others. The actual eligible part, iOS version and calibration requirements matter. GEL lists the process **only as an optional post-repair step**, never as evidence that a panic has been fixed.
Source: https://support.apple.com/en-us/120579.

## Exact-model manuals (source index)
- iPhone 12 (`iPhone13,2`): https://support.apple.com/en-us/100464
- iPhone 12 Pro Max (hardware `iPhone13,4`): https://support.apple.com/en-gb/101538
- iPhone 13 (`iPhone14,5`): https://support.apple.com/en-us/100402
- iPhone 13 Pro Max (`iPhone14,3`): https://support.apple.com/en-us/100430
- iPhone SE (3rd generation, `iPhone14,6`): https://support.apple.com/en-us/101527
- iPhone 14 (`iPhone14,7`): https://support.apple.com/en-us/100308
- iPhone 14 Plus (`iPhone14,8`): https://support.apple.com/en-us/100322
- iPhone 14 Pro (`iPhone15,2`): https://support.apple.com/en-us/100370
- iPhone 14 Pro Max (`iPhone15,3`): https://support.apple.com/en-la/100357
- iPhone 15 (`iPhone15,4`): https://support.apple.com/en-us/104900
- iPhone 15 Pro (`iPhone16,1`): https://support.apple.com/en-ie/104872
- iPhone 16 Pro (`iPhone17,1`): https://support.apple.com/en-us/120803

### Newer manuals verified but not given an unverified hardware identifier
- iPhone 16 (source index only): https://support.apple.com/en-us/120652
- iPhone 17 (source index only): https://support.apple.com/en-us/123029
- iPhone 17 Pro (source index only): https://support.apple.com/en-us/124262
- iPhone 18 Pro (Greek manual, source index only): https://support.apple.com/el-gr/127388

Do not assign an `iPhone##,#` identifier or apply a Pro guide to another variation without independent confirmation. For iPhone 11, iPhone 11 Pro Max, XS Max and iPhone SE (2020) **the current catalog has no verified exact-model Apple repair manual**; GEL can still cite the generic restart troubleshooting guide, but must not fabricate a corresponding model manual.

## Quality gates
1. Official sources are never counted as successful hardware-repair incidents.
2. Use manufacturer guidance by exact supported model and observed symptom, not by panic code.
3. Work that opens the handset requires trained service personnel, the appropriate model manual and battery-safety precautions.
4. English and Greek UI must always label Apple guidance separately from third-party reports.
5. No silent internet access by GEL iDoctor; manuals are **links**, not full copyrighted text copied into app assets.
6. Missing model/manual is a coverage limit, not a fault or authenticity concern.

## Coverage expansion — 2026-11-21

### Previously indexed official Apple procedures (22 new records in earlier expansion)
- No power / black display: https://support.apple.com/en-us/116940
- Wired charging user troubleshooting: https://support.apple.com/en-us/108805
- Microphones: https://support.apple.com/en-us/101600
- Speaker/receiver: https://support.apple.com/en-us/118432
- Camera/flash: https://support.apple.com/en-us/102514
- Face ID / TrueDepth symptoms: https://support.apple.com/en-us/118243
- Touch / intermittent touchscreen: https://support.apple.com/en-us/102567
- Temperature management: https://support.apple.com/en-us/118431
- Liquid connector detection: https://support.apple.com/en-us/102643
- Recovery after iOS update / restore problems: https://support.apple.com/en-us/118106
- Restore screen: https://support.apple.com/en-us/108969
- Update/restore error codes: https://support.apple.com/en-us/109057
- SOS / No Service / Searching: https://support.apple.com/en-us/120000
- Cellular calls fail: https://support.apple.com/en-us/118427
- Touch ID: https://support.apple.com/en-us/101612
- Recovery on eligible iOS 27 systems (index only until OS eligibility is confirmed): https://support.apple.com/en-us/128061
- General power/charging/restart troubleshooting hub: https://support.apple.com/en-us/101969
- Exact-model manuals: iPhone 12, iPhone 13, iPhone SE (3rd generation).
- Directory-only manuals: iPhone 16, iPhone 17.

### What 'all official publications' means operationally
This is a **curated iPhone service corpus, not an exhaustive mirror of all Apple support articles**. It now includes the 30 distinct iPhone Repair Manuals listed in Apple's current iPhone model directory, but does not claim to cover every archived manual or user-guide topic. Apple updates manuals, diagnostics eligibility and service pathways; many articles are about unrelated end-user features. To expand safely, register each new official page with a stable source ID, exact URL, symptom/model scope, short independently authored bilingual explanation, review date and any OS/part restrictions. Never copy whole copyrighted manuals into the APK. The original official Apple URL is always authoritative.

The standalone iPhone 17/18 manual references are directory-only until model identifiers are independently checked; a model number cannot be invented to make a source appear as a matched repair procedure. Specific SMC/MIC/PRS0 codes are GEL clues: no Apple-published proof of code-to-part equivalence was identified in this indexed material.

### Review / regression requirements
- Preserve official Apple first, forum repairs second in Lab 4, even for multiple ZIP incidents.
- A newly discovered independent repair must not change which source is displayed as an Apple recommendation.
- Unknown models can get only generic conditional procedures, not arbitrary exact-model manuals or unsupported diagnostics.
- Existing local offline catalog remains usable without internet; source links open by tap with connectivity.
- Fail CI if core official guidance topic coverage or source integrity regresses.
- Recheck all source URLs and applicability periodically; a domain prefix check alone is not a guarantee that an article has not been edited or withdrawn.

## Per-manual chapter taxonomy — 2026-11-21

A structured offline index now contains **983 Apple Repair Manual contents entries** for the 30 model manuals: 60 safety, 30 quick checks, 30 diagnostics, 426 symptom-troubleshooting headings, 30 reference/tool views and 407 repair/preparation procedure headings.

- Asset: `app/src/main/assets/iphone_apple_manual_chapters_v1.tsv`.
- Parser: `AppleIPhoneManualChapters.java` (strict source/manual matching, phase classification and exact-model-only auto lookup).
- iPhone Lab 4: conditionally lists relevant official troubleshooting headings, and separately lists all repair procedure chapter titles for the identified exact model **without recommending a replacement**. A relevant, eligible post-repair Apple workflow is cited separately.
- Full taxonomy and audit matrix: `docs/qa/IPHONE_APPLE_MANUAL_CHAPTER_TAXONOMY_2026-11-21.md`.

The new chapter catalog is navigational **metadata**, not a downloaded/copyrighted copy of Apple's manuals, and its master manual URL is not falsely called a chapter deep link. Individual direct chapter URLs are recorded only when they have separately been verified. The original manufacturer page remains authoritative.

## Complete current iPhone Repair Manual index — 2026-11-21

Official master directory: https://support.apple.com/en-us/docs/iphone

The Apple iPhone product directory currently lists 30 models/variants with dedicated **Repair Manual** links in the iPhone 12-to-iPhone 18 Pro / Duo family. Older model pages (for example, the SE 2nd generation) may have an *iPhone User Guide* but no dedicated Apple Repair Manual. **Do not invent a Repair Manual for an older device.**

The complete list below is part of the offline TSV catalog. Each URL is an Apple-owned Repair Manual link, not a copied manual. The linked Apple page remains the source of truth for diagrams, procedures, safety warnings, eligibility and revisions.

| iPhone model | Apple Repair Manual | Lookup status |
|---|---|---|
| iPhone 12 | https://support.apple.com/en-us/100464 | Exact hardware ID indexed |
| iPhone 12 mini | https://support.apple.com/en-us/100452 | Directory-only |
| iPhone 12 Pro | https://support.apple.com/en-us/100476 | Directory-only |
| iPhone 12 Pro Max | https://support.apple.com/en-gb/101538 | Exact hardware ID indexed |
| iPhone 13 | https://support.apple.com/en-us/100402 | Exact hardware ID indexed |
| iPhone 13 mini | https://support.apple.com/en-us/100384 | Directory-only |
| iPhone 13 Pro | https://support.apple.com/en-us/100416 | Directory-only |
| iPhone 13 Pro Max | https://support.apple.com/en-us/100430 | Exact hardware ID indexed |
| iPhone SE (3rd generation) | https://support.apple.com/en-us/101527 | Exact hardware ID indexed |
| iPhone 14 | https://support.apple.com/en-us/100308 | Exact hardware ID indexed |
| iPhone 14 Plus | https://support.apple.com/en-us/100322 | Exact hardware ID indexed |
| iPhone 14 Pro | https://support.apple.com/en-us/100370 | Exact hardware ID indexed |
| iPhone 14 Pro Max | https://support.apple.com/en-la/100357 | Exact hardware ID indexed |
| iPhone 15 | https://support.apple.com/en-us/104900 | Exact hardware ID indexed |
| iPhone 15 Plus | https://support.apple.com/en-us/104858 | Directory-only |
| iPhone 15 Pro | https://support.apple.com/en-ie/104872 | Exact hardware ID indexed |
| iPhone 15 Pro Max | https://support.apple.com/en-us/104917 | Directory-only |
| iPhone 16 | https://support.apple.com/en-us/120652 | Directory-only |
| iPhone 16 Plus | https://support.apple.com/en-us/120662 | Directory-only |
| iPhone 16 Pro | https://support.apple.com/en-us/120803 | Exact hardware ID indexed |
| iPhone 16 Pro Max | https://support.apple.com/en-us/120819 | Directory-only |
| iPhone 16e | https://support.apple.com/en-us/121720 | Directory-only |
| iPhone 17 | https://support.apple.com/en-us/123029 | Directory-only |
| iPhone 17 Pro | https://support.apple.com/en-us/124262 | Directory-only |
| iPhone 17 Pro Max | https://support.apple.com/en-us/124278 | Directory-only |
| iPhone Air | https://support.apple.com/en-us/123052 | Directory-only |
| iPhone 17e | https://support.apple.com/en-us/125956 | Directory-only |
| iPhone 18 Pro | https://support.apple.com/el-gr/127388 | Directory-only |
| iPhone 18 Pro Max | https://support.apple.com/en-la/127626 | Directory-only |
| iPhone Duo | https://support.apple.com/en-us/127682 | Directory-only |

**Count:** 30 repair manuals (12 matched to previously verified internal hardware IDs, 18 directory-only) + 24 independent Apple symptom/diagnostic/support records = **54 official source records**. There is no full-text manual duplication in the APK.

**Do not elevate a directory-only manual to an exact-model match without verified device hardware identifiers.** A raw panic log that lacks the model identifier should receive only the applicable generic Apple troubleshooting procedure, not a guessed variant's manual.

### Procedure structure to index in later refinements
Official manuals usually separate Safety, Quick Checks, Diagnostics, Battery/Charging/Power, Camera, Display/Touch, Mechanical/Sensors, Sound, Parts/Tools and Repair Procedures. The scope differs by model: for example, the iPhone Duo adds hinge and dual-battery topics while other iPhones do not. Our next layer should index **official chapters by manual and symptom**, recording source link, device/OS applicability, review date and original short summaries rather than bundling full copyrighted chapter text. This layer is not yet complete.

### Important limitations
- The index is complete **for the 30 dedicated Repair Manual links currently shown in the Apple iPhone model directory**, not for every Apple support publication or revision ever released.
- A page linked through Apple's own product directory is authoritative for its official provenance. Every URL should still be rechecked periodically for redirects, revision changes and accessibility.
- Existing Apple authoring is never proof of GEL panic-code-to-part mapping; the international case database remains a separate trust category.
- No automatic live Apple Support scraping occurs on the user device.

## Source-verified Apple Diagnostics Repair eligibility (2026-11-21)

The current official Apple Diagnostics for Self Service Repair / Repair document is [Apple Support 101965](https://support.apple.com/en-us/101965). It distinguishes (a) *on-device* diagnostics for eligible iPhone 12+ with iOS **18 or later** and Wi-Fi, from (b) *secondary-device browser* diagnostics for eligible iPhone 12+ with iOS **17 or later** and Wi-Fi plus a separate internet-enabled console. This source is indexed separately under `APPLE_DIAGNOSTICS_REPAIR` for exact verified iPhone 12+ hardware IDs, because the previous article `APPLE_DIAGNOSTICS` has a narrower eligibility scope and must not be substituted as the sole Apple reference.

GEL does not run these Apple tests from Android, store the Apple serial number, claim a measured result, or suggest that reading a panic log is equivalent to Apple hardware diagnostics.
