# GEL iDoctor — Apple official iPhone service documentation
Research date: 2026-11-21. Source authority: **Apple Support only**.

## Non-negotiable evidence boundary
**These Apple publications are not an Apple list mapping panic-full, SMC, MIC2, PRS0, watchdog or I2C codes to confirmed replacement parts.** They offer symptoms, diagnostics, safe repair procedures and post-repair checks. GEL uses them as **independent procedural references**, never as additional successful-repair cases. Do not label any forum fix "Apple-confirmed" based on these manuals.

## Source files and integration
- Offline catalog: `app/src/main/assets/iphone_apple_official_guidance_v1.tsv`. **39 curated Apple-source records:** 12 exact-model repair-manual mappings, 23 symptom/diagnostic/recovery/power service references, and 4 model manuals indexed without unverified hardware identifiers. The iPhone-only symptom pages cover audio, display, camera, Face ID, Touch ID, thermal symptoms, liquid connector alerts, recovery/restore, cellular service and calls, as well as power and charging.
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

### Newly indexed official Apple procedures (22 new records)
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
This is a **curated, useful first tranche, not an exhaustive mirror or archive of all Apple articles**. Apple updates manuals, diagnostics eligibility and service pathways; many articles are about unrelated end-user features. To expand safely, register each new official page with a stable source ID, exact URL, symptom/model scope, short independently authored bilingual explanation, review date and any OS/part restrictions. Never copy whole copyrighted manuals into the APK. The original official Apple URL is always authoritative.

The standalone iPhone 17/18 manual references are directory-only until model identifiers are independently checked; a model number cannot be invented to make a source appear as a matched repair procedure. Specific SMC/MIC/PRS0 codes are GEL clues: no Apple-published proof of code-to-part equivalence was identified in this indexed material.

### Review / regression requirements
- Preserve official Apple first, forum repairs second in Lab 4, even for multiple ZIP incidents.
- A newly discovered independent repair must not change which source is displayed as an Apple recommendation.
- Unknown models can get only generic conditional procedures, not arbitrary exact-model manuals or unsupported diagnostics.
- Existing local offline catalog remains usable without internet; source links open by tap with connectivity.
- Fail CI if core official guidance topic coverage or source integrity regresses.
- Recheck all source URLs and applicability periodically; a domain prefix check alone is not a guarantee that an article has not been edited or withdrawn.
