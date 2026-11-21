# GEL iDoctor — Apple official iPhone service documentation
Research date: 2026-11-21. Source authority: **Apple Support only**.

## Non-negotiable evidence boundary
**These Apple publications are not an Apple list mapping panic-full, SMC, MIC2, PRS0, watchdog or I2C codes to confirmed replacement parts.** They offer symptoms, diagnostics, safe repair procedures and post-repair checks. GEL uses them as **independent procedural references**, never as additional successful-repair cases. Do not label any forum fix "Apple-confirmed" based on these manuals.

## Source files and integration
- Offline catalog: `app/src/main/assets/iphone_apple_official_guidance_v1.tsv`. **15 curated Apple-source records:** 9 exact-model repair-manual mappings and 6 service/diagnostic pathways, including model-limited diagnostics and post-repair completion.
- Parser: `AppleOfficialServiceGuidance.java`. Requires `support.apple.com` source URLs, model-matching for manuals, and applicability for diagnostics. It does **not** accept or interpret panic codes.
- iPhone Lab 4: per-model official manual, general restart troubleshooting and only eligible Apple diagnostics are displayed, each explicitly not tied to a panic code. Full real-world reported repair attempts appear separately, with their failures and original links.
- iPhone Lab 8: show a manual only when the imported eligible log set yields **one** recognized device identifier. With ambiguous/multiple devices, do not guess a manual. The separately curated repair outcomes remain source-matched.
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
Apple's repair-completion process is conditional on device model, part, iOS, and eligibility. Do not equate running it with proof that any panic has been fixed.
Source: https://support.apple.com/en-us/120579.

## Exact-model manuals (source index)
- iPhone 12 Pro Max (hardware `iPhone13,4`): https://support.apple.com/en-gb/101538
- iPhone 13 Pro Max (`iPhone14,3`): https://support.apple.com/en-us/100430
- iPhone 14 (`iPhone14,7`): https://support.apple.com/en-us/100308
- iPhone 14 Plus (`iPhone14,8`): https://support.apple.com/en-us/100322
- iPhone 14 Pro (`iPhone15,2`): https://support.apple.com/en-us/100370
- iPhone 14 Pro Max (`iPhone15,3`): https://support.apple.com/en-la/100357
- iPhone 15 (`iPhone15,4`): https://support.apple.com/en-us/104900
- iPhone 15 Pro (`iPhone16,1`): https://support.apple.com/en-ie/104872
- iPhone 16 Pro (`iPhone17,1`): https://support.apple.com/en-us/120803

### Newer manuals verified but not given an unverified hardware identifier
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
