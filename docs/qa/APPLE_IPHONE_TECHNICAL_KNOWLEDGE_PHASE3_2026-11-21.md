# GEL iDoctor — Apple technical troubleshooting knowledge, Phase III

**Reviewed on 2026-11-21.** Keep official Apple service knowledge, independent repair outcomes and GEL panic-log interpretation as **three separate types of evidence**. This document records the exact state of the current implementation rather than implying that 1,074 separate service procedures have been read.

## Reliable denominators

| Coverage unit | Current count | What it means |
|---|---:|---|
| Official source-linked model chapter records | **1,074** | Each has its own real Apple article URL |
| Distinct Apple support article URLs in those records | **447** | Many different iPhone models and symptom sections share one Apple article |
| Actual article bodies inspected and summarized as technical findings in Phase III | **5** | Source-verified original bilingual technical triage notes |
| Curated Phase III bilingual technical findings | **42** | Symptoms, checks, diagnostics, safety, eligible escalation, verification |
| Chapter entries to which those 42 findings are applicable by exact URL+phase+topic | **190** | **Reuse of source-reviewed article sections**, not 190 article bodies individually studied |
| Earlier individually studied stage-summary chapters | **10** | 62 bilingual short stage summaries, especially physical repairs |
| Official source articles with the 10 earlier stage-summary chapters | **8** | A review-type distinction, not a count to simply add to the 5 articles |

**Do not report 190 or 1,074 as "full repair procedures reviewed."** The 190 reusable chapter mappings are source- and topic-validated. No manufacturer panic-to-defective-part interpretation is inferred.

## New implementation

- `app/src/main/assets/iphone_apple_technical_findings_v1.tsv`: 42 concise, original, Greek/English technical findings. Each row contains exact Apple URL, phase, topic, action-kind, risk class, written summary and review date.
- `app/src/main/java/com/gel/cleaner/AppleIPhoneTechnicalKnowledge.java`: strict parser. Rejects duplicate IDs, unofficial URLs, unrecognized stage/risk flags and facts not actually linked by that `source_url + phase + topic` in the master manual chapter index.
- `app/src/test/java/com/gel/cleaner/AppleIPhoneTechnicalKnowledgeTest.java`: assertions for 42 facts, 190 matching chapter records, 447 URL corpus, exact iPhone13 Pro Max and iPhone 15 model isolation, empty results for unknown hardware IDs, and no wrong-topic spillover on shared Apple articles.
- `IPhoneLabsActivity.java`: the real Lab 4 source display now includes a limited number of source-reviewed safety notes, quick checks and conditional troubleshooting findings *before* independently published repairs. Extra warnings are visible only when contextually appropriate; `DATA_LOSS` and `TECHNICIAN_ONLY` must not be rendered as confirmed device failures.
- `docs/qa/APPLE_IPHONE_TECHNICAL_REVIEW_QUEUE_2026-11-21.tsv`: complete **447-article editorial queue**, sorted by relevance and repeated use across manuals. Status `DIRECT_LINK_ONLY` means **no** claim that the full article was studied. The inventory's `model_manuals` count is how many indexed manuals reuse the article.

## Five first-party article bodies reviewed in this tranche

| Apple source and section | Current structured knowledge | Source |
|---|---|---|
| iPhone Troubleshooting Quick Checks | Non-invasive device and accessory checks, restart/update, ***restore-data-loss warning*** | https://support.apple.com/en-us/101964 |
| iPhone Battery Safety | Trained technician and workspace requirement, damaged-cell prohibition, discharge/unknown-level stop, thermal-emergency warning | https://support.apple.com/en-us/100299 |
| Apple Diagnostics for Self Service Repair | Eligible hardware, on-device iOS 18+, secondary-browser iOS 17+, Wi-Fi/console requirements, real test-result provenance and serial-number privacy | https://support.apple.com/en-us/101965 |
| iPhone Troubleshooting Battery, Charging, and Power Issues | Issue-specific findings for **six existing chapter topic scopes**: unexpected restart; legacy combined battery/power/charging; battery; no power; wired charging; wireless charging. Hardware examination always downstream from non-invasive checks and eligibility | https://support.apple.com/en-us/101969 |
| iPhone 15 Pro Max TrueDepth Camera | IR laser hazard, display/top-speaker disassembly prerequisite and Repair Assistant required after eligible replacement | https://support.apple.com/en-us/104926 |

### Example — what a watchdog log can legitimately yield

On the **verified exact model** iPhone 13 Pro Max (`iPhone14,3`), a related *unexpected restart* chapter gets the source-reviewed Apple facts bound to `https://support.apple.com/en-us/101969` topic `RESTART`:

1. Distinguish unexpected restart, loop, frozen touch and full no-power situations.
2. Check whether failures affect all preinstalled apps; isolate app/software issues before hardware.
3. For apparent non-response check whether time continues (possible Multi-Touch issue).
4. Run MRI if this actual model/iOS/device state is eligible, not simply because a panic file was imported.
5. Account for *data loss* before following any erase/restore workflow.
6. Verify real symptom recurrence and overall function after any legitimate repair.

These findings do **not** claim that the user has taken those steps, that an Apple diagnostic result exists or that a logic board/port/sensor should be replaced from an SMC or watchdog code.

### Important model-specific exceptions

- The general Apple battery/charging support article includes different *later* hardware escalation paths for different model families. Phase III deliberately **does not reproduce a generic "replace logic board" instruction** that could incorrectly transfer from iPhone 17/18 to iPhone 12/13/14.
- TrueDepth and lithium-ion battery procedures are not ordinary consumer repair suggestions. Physical work requires the complete illustrated source, supported parts and a qualified technician.
- `AppleIPhoneTechnicalKnowledge.forChapter` binds by both actual direct Apple URL and its **topic and phase**. An article about display and touch cannot pick up power or restart facts merely because it is published by Apple.
- For directory-only models with no verified internal hardware identifier, selecting an article for reading is possible through the existing explicit model-picker; the app never infers exact model from an imported panic string.

## Next editorial priorities

The highest-reuse unreviewed official articles from the 447-source queue are currently:

- **Buttons, SIM, Taptic and mechanical issues** — https://support.apple.com/en-us/101970
- **Display, Multi-Touch and sensors** — https://support.apple.com/en-us/101966
- **Speakers, microphone and audio** — https://support.apple.com/en-us/101968
- **Camera, LiDAR, TrueDepth and Face ID** — https://support.apple.com/en-us/101967 (an earlier source-reviewed short triage exists for iPhone 14 Pro within the stage-summary corpus; this article still needs a full topic-by-topic Phase III review)

Subsequently process the **model-specific repair procedures** one by one, storing precise model/variant, removed prerequisites, warnings, tools, part-specific Repair Assistant conditions, and real post-repair checks. Do **not** generate replacement instructions by extrapolating from a neighboring iPhone model.

## Still needed before shipping

A successful JVM/Android CI proves source-code and data contracts, not real-device usability. Verify Greek and English rendering, log size, clickable manufacturer URLs, overview/PDF color and link preservation, demo 1+2 behavior, source order and manual browsing on actual devices before merging PR #181 or building a production APK.
