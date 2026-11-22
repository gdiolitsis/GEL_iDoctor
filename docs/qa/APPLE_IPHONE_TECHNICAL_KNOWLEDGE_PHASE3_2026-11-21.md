# GEL iDoctor — Apple iPhone technical knowledge, Phase III

**Latest audit: 2026-11-22.** A 1,074-item link catalog is not the same as individually reading 1,074 Apple repair chapters. This file separates those dimensions and identifies exactly which first-party content was reviewed.

## Verified coverage

| Metric | Count | Meaning |
|---|---:|---|
| Official iPhone Repair Manuals indexed | **30** | Apple's current model-manual directory in our catalog |
| Direct Apple chapter links | **1,074 / 1,074** | Link/table-of-contents completeness; not full article-text review |
| Distinct source article URLs | **447** | Manufacturer articles are shared by model manuals and by symptom |
| First-party articles with source-reviewed technical notes | **12** | Separately opened/read official text |
| Original bilingual technical findings | **120** | Each has source URL, exact scope, category, risk, kind, and review date |
| Chapter rows to which those reviewed findings apply | **520** | Identical source URL **plus exact topic and phase**; not 520 individually studied articles |
| Individually source-reviewed stage-summary chapters | **10** | Existing 62 bilingual short stage descriptions, with model-specific prerequisites |
| Distinct article URLs with physical/diagnostic stage summaries | **8** | Review status is separate from the newer technical-note track |

These are distinct denominators. One official generic article about mechanical issues links to 153 separate manual-topic rows; a source-reviewed fact for that article must **not** become a claim that 153 physical procedures were independently verified.

## Evidence model and safeguards

- **First-party source**: manufacturer page and exact title URL from `iphone_apple_manual_chapters_v1.tsv`.
- **A reviewed factual summary**: `iphone_apple_technical_findings_v1.tsv`, with `fact_id`, `source_url`, `phase`, `topic`, `kind`, `risk`, Greek text, English text, and review date.
- **Matching engine**: `AppleIPhoneTechnicalKnowledge.java` rejects records not supported by a matching published URL + topic + chapter phase; an exact internal hardware ID must be verified for automatic model-specific display.
- **What is not an outcome**: neither the presence of an Apple troubleshooting chapter nor a GEL panic code establishes the result of Apple MRI/Audio/Multi-Touch diagnostics, a failed sensor, or a required part replacement.
- **Safety**: technician-only steps and battery/laser hazards remain labeled; restore/erase procedures carry a data-loss caution; the full illustrated Apple manual is required for physical repairs.
- **Independent repair evidence**: forum case histories and their successes/failures remain separate from manufacturer's instructions in Lab 4.

## Source-reviewed Phase III articles (twelve)

| Official source | Applicable chapter headings | Structured findings | Manufacturer link |
|---|---|---:|---|
| Troubleshooting Quick Checks | Initial device checks | 4 | https://support.apple.com/en-us/101964 |
| Battery Safety | Lithium-ion battery cautions | 4 | https://support.apple.com/en-us/100299 |
| Apple Diagnostics for Self Service Repair | Hardware/OS eligibility, actual test execution, privacy | 5 | https://support.apple.com/en-us/101965 |
| Battery, Charging and Power Troubleshooting | Unexpected restarts, combined battery/power, battery, no power, wired and wireless charging | 26 | https://support.apple.com/en-us/101969 |
| iPhone 15 Pro Max TrueDepth Camera procedure | Laser warning, prerequisites, after-repair activation | 3 | https://support.apple.com/en-us/104926 |
| Display Issues | Display/image, Multi-Touch/Haptic Touch, display-related sensors | 19 | https://support.apple.com/en-us/101966 |
| Sound Issues | Speaker, ringer, microphone tests and part eligibility warnings | 9 | https://support.apple.com/en-us/101968 |
| Mechanical Issues | Button/switch, physical-SIM tray, alerts, vibration/haptic, Touch ID, mechanical sensor chapters | 30 | https://support.apple.com/en-us/101970 |
| Camera Issues | Rear camera/LiDAR and front camera/Face ID symptom checks and eligible diagnostics | 8 | https://support.apple.com/en-us/101967 |
| iPhone 14 Pro Battery | Required display/bottom-speaker/Taptic removal, adhesive-strip stop rule, optional service completion | 4 | https://support.apple.com/en-us/100381 |
| iPhone 15 Battery | Back-glass access, camera optional, mmWave variant caution, failed-removal stop rule, reassembly checks | 4 | https://support.apple.com/en-us/104907 |
| iPhone 15 Pro Max Back Glass | Sanding and glass safety requirements, heated-removal equipment, damaged-battery stop rule and post-repair checks | 4 | https://support.apple.com/en-us/104929 |
| **Total** | | **120** | |

### New display and touch knowledge (source 101966)

1. Apple distinguishes black/blank image, no power, flicker, pixel anomalies, brightness issues and missing True Tone; the cause must be isolated by actual checks.
2. Remove obstructing case or protector and clean with device powered down, disconnected and appropriate cloth.
3. If an image appears only after charging, investigate the charging path; if the device remains off, investigate power separately. MRI and Display Pixel Anomalies diagnostics are available only when supported.
4. For Multi-Touch, distinguish unresponsive regions from erratic response; perform the Apple Multi-Touch suite when eligible.
5. A proximity sensor test during an actual call covers and uncovers the top of the screen. Compass interference can be environmental or magnetic.
6. Apple lists replacements only **after** the specific diagnostic failure and by exact model. A panic log does not establish an Ambient Light, Proximity, Compass or Barometer MRI failure.

### New speaker and microphone knowledge (source 101968)

1. Separate upper/lower speaker complaints, ringer, audio from calls, recording and specific microphone paths.
2. Check volume, Ring/Silent versus Action Button, obstructing cases and safe dry-brush cleaning of protective meshes.
3. Perform MRI and the proper Audio diagnostic suite when available. A live call from another phone can help test whether speech is clear on speakerphone.
4. The original Apple table distinguishes Receiver, Speaker, Bottom Mic left/right, Front Mic and Rear Mic tests and has **different eligible replacements for different models**.
5. Do not generalize a replacement (e.g. a microphone module versus a full-unit service path) from a failure on another iPhone or a panic-code keyword.
6. After any repair verify actual speaker, ringer and microphone function in the original failing use case.

### New buttons, SIM, alerts, sensors and haptics knowledge (source 101970)

1. Persistent heat alert after the phone returns to a 0–35 °C normal environment is different from an occasional legitimate temperature warning. If a phone overheated, switch it off and allow safe cooling before continuing.
2. Distinguish physical button damage and accidental pressing from absent haptic feedback; remove case, clean and test with dry fingers, then use supported MRI.
3. **Physical SIM tray** instructions are strictly limited to phones that truly have one. Some iPhone 14 and later US models are eSIM-only.
4. A SIM alert may concern carrier account/authorization, PIN, wrong-sized tray or damaged tray; it does not prove baseband hardware failure.
5. For vibration, Apple checks ringing and silent call scenarios before escalation to physical inspection.
6. Touch ID advice is only applicable to devices actually equipped with a Home/Touch ID mechanism. Failure to read only certain fingers is not automatically a service issue.
7. Sensor symptoms and MRI outcomes vary; an ordinary compass/proximity observation must not be presented as an Apple service-test failure.

## Integration in iPhone Lab 4

`IPhoneLabsActivity.java` now uses `readAppleTechnicalFacts()` and `logSourceReviewedAppleFacts()`:

- The **official manufacturer section appears before independently reported repairs**, preserving provenance.
- Before physical work, a small source-reviewed safety and Quick Checks summary is shown.
- A matched official symptom topic includes a limited number of relevant technical facts (rather than all 120 notes). The exact-model official chapter URL remains beside the summary.
- `DATA_LOSS` and `TECHNICIAN_ONLY` notes retain their caution text and do not become red error verdicts.
- Generic article 101970 may support multiple different chapters; notes are selected by **source URL + phase + topic**, not by article alone.

## November 22 model-specific service review and complete structural index

- All **447 of 447** unique manufacturer article pages were independently fetched and their **document structure** inspected successfully. The original **1,074** chapter links all map to one of those articles. See `docs/qa/APPLE_SOURCE_ARTICLE_STRUCTURE_AUDIT_2026-11-22.md` for the raw automated structural audit.
- Structure extraction records headings, safety/tools/removal/reassembly/diagnostics and post-repair mentions. It is explicitly **not** detailed manufacturer instruction review or automatic certification.
- The iPhone 14 Pro battery article requires prior display, lower speaker and Taptic Engine removal; a failed three-strip adhesive removal leads to a **stop-repair** rule.
- The iPhone 15 battery procedure instead begins from back-glass removal, with camera removal **optional** and an mmWave variant-specific precaution.
- The iPhone 15 Pro Max back-glass chapter includes broken-glass and sanding safety, specialist heated tools, and a **stop-repair** rule if the battery is physically damaged.
- In those particular battery/back-glass chapters, `Repair Assistant` is **recommended**, not mandatory. The separately reviewed TrueDepth iPhone 15 Pro Max procedure requires it for enabling security features. Never collapse those distinct conditions into one generic instruction.
- The raw Apple manual index includes cross-model shared troubleshooting headings. Automatic exact-model reporting now filters **Touch ID** from verified Face ID iPhones and **Face ID** from the verified Touch ID iPhone SE (3rd generation); explicit source inventory browsing still retains those official links.

## Editorial status for all 447 distinct articles

See `docs/qa/APPLE_IPHONE_TECHNICAL_REVIEW_QUEUE_2026-11-21.tsv`, updated **2026-11-22**:

- Twelve manufacturer article bodies now have source-reviewed technical findings.
- Ten model-specific chapters have older, source-reviewed stage summaries.
- **432 distinct manufacturer articles have neither of these two reviewed-detail statuses**, so their chapter links remain direct authoritative navigation only.

The official [Camera Issues](https://support.apple.com/en-us/101967) article was successfully inspected on 2026-11-22 after an earlier temporary retrieval error. Eight new concise topic-scoped Greek/English findings cover rear camera/LiDAR and front camera/Face ID checks. Its model-specific replacement tables have **not** been converted into unconditional panic-based service recommendations.

Continue with official model-specific battery/display/camera/speaker/sensor service chapters only after inspecting each article and its conditions. Do not copy full copyrighted manuals into the APK.

## Supplemental Apple conditional test-to-part knowledge — 2026-11-22

A separate manufacturer evidence layer now stores **101 explicitly conditional test-result-to-service rules**, covering the 12 iPhone models whose internal hardware IDs have already been independently verified in the catalog. Source tables: [Display Issues](https://support.apple.com/en-us/101966), [Sound Issues](https://support.apple.com/en-us/101968) and [Mechanical Issues](https://support.apple.com/en-us/101970).

- **33** source-linked sensor MRI failure rules.
- **63** source-linked AST 2 audio/microphone failure rules.
- **3** persistent button/switch issue rules after Apple checks.
- **2** persistent-temperature-alert rules after Apple checks.

**These 101 are not part of the 120 factual summaries or the 62 existing short repair-stage descriptions, and none is an observed failure in a real device.** A model- and test-specific repair path is available *only* through the guarded `AppleIPhoneConditionalServiceRules.forVerifiedObservedResult(..., externallyVerifiedResult=true)` lookup after the exact Apple test was actually run and its outcome documented. The existing Lab 4 panic-log code does not call this engine.

Full model/result matrix and evidence conditions: `docs/qa/APPLE_IPHONE_CONDITIONAL_DIAGNOSTIC_SERVICE_RULES_2026-11-22.md`. This is an important manufacturer-specific evidence advance: the *same failed test* can imply different service paths by model. It never means that a panic log diagnosed the part.

## Official 315-article procedure metadata, now included in the Lab 4 APK assets

**Verified on 2026-11-22:** A separate public-Apple source audit fetched **315/315 distinct service/preparation article HTML pages** (mapped to **407/407 chapter rows**) without retrieval errors. This is *metadata extraction*, not full technician review of their illustrated instructions.

- `tools/iphone/extract_apple_procedure_requirements.py` audits exact first-party service pages and extracts **source-named prerequisite parts, source-named tools and textual references to Repair Assistant**. It never copies the repair article body, makes hardware diagnoses, or asserts that a missing extracted list means no prerequisite exists.
- `docs/qa/APPLE_PROCEDURE_REQUIREMENTS_AUDIT_2026-11-22.tsv` and associated Markdown report retain explicit provenance `AUTOMATED_LABELS_ONLY_NOT_FULL_SERVICE_REVIEW`: **303** articles with named tool lists, **99** with named prior-removal part lists.
- `app/src/main/assets/iphone_apple_procedure_requirements_v1.tsv` embeds the **exact 315 audited source records** for offline reference.
- `AppleIPhoneProcedureRequirements.java` strictly validates every record against all original 407 direct-chapter references and their model manual IDs. No adjacent model inherits a different URL. The data is still **not** a 315-item expert-reviewed repair plan.
- The existing optional `APPLE — Πηγές, εργαλεία και ελεγμένα στάδια` navigator within iPhone Lab 4 now browses **all 407 source-linked procedures**, displaying *verified original article URL and source-extracted labels* in Greek/English. The ten independently reviewed procedure chapters retain their **62 original curated bilingual stage summaries**, visibly distinguished as reviewed; other chapters are clearly marked **source/tools only**.
- Keyword classification found **47** manufacturer article mentions in the "required" class, **89** in "recommended" and **179** without a detected mention. The unattended keyword algorithm can misread context; the UI always labels these as **automatic wording requiring confirmation in the full Apple source**. Individually reviewed technical notes for iPhone14Pro Battery, iPhone15 Battery, iPhone15ProMax Back Glass and TrueDepth preserve their personally source-reviewed separate distinction between **recommended** and **required**.
- Source-validated anchor examples: iPhone 14 Pro Battery requires prior removal of Display, Bottom speaker, Taptic Engine; iPhone 15 Battery requires Back glass with Camera optional; iPhone 15 Pro Max TrueDepth requires Display and Top speaker. Only Apple Diagnostics results actually performed may justify any conditional repair path, never a panic log.
- A separate `tools/iphone/verify_apple_repair_source_coverage.py` verifies the **447 article structure audit**, source-extracted procedure metadata, 120 human-curated facts, 62 staged summaries and exact model chapter relationships without external network calls.

**Remaining editorial limitation:** All 447 source article *structures* and all 315 procedure article *tool/prerequisite metadata* were checked. Complete expert reading and bilingual reconstruction of **all physical removal/reassembly instructions** has NOT been done; it would require individual source review and appropriate safety checks. Original Apple full illustrated articles are available from the directly linked Lab 4 sources. Real Android device and HTML/PDF exports have not yet been validated.

## Validation boundaries

The JVM/Android CI verifies Java compilation and unit constraints, including 120 fact records, 520 matching chapters and non-leakage between topics on shared Apple pages. It is **not** an on-device UI or HTML/PDF export test; those are still required before merging the draft PR.
