# GEL iDoctor — iPhone Panic Repair Knowledge Base (v1)
**Started:** 2026-11-21  
**Status:** Curated initial seed, not exhaustive and not a validated repair classifier.

## Goal
For a given **exact iPhone model identifier and panic-log clue**, present publicly documented **repair attempts and their observed outcomes**, including unsuccessful repairs. The goal is to support expert judgment, not to instruct customers to replace a component automatically.

The legacy `app/src/test/resources/iphone_panic_corpus.tsv` contains 49 **reconstructed QA panic examples**. Those cases are not repair outcomes. Keep the two datasets strictly separate.

## Files
- `app/src/main/assets/iphone_panic_repair_knowledge_v1.tsv`: offline, sourced historical repair cases (one row per repair **attempt**, not one row per device).
- `app/src/main/java/com/gel/cleaner/IPhonePanicRepairKnowledgeBase.java`: strict UTF-8 parser, normalized code matching and exact-device lookup.
- `app/src/test/java/com/gel/cleaner/IPhonePanicRepairKnowledgeBaseTest.java`: source integrity, mixed outcomes, model isolation and unknown-code negative controls.
- `IPhoneLabsActivity.java` / **iPhone LAB 4**: matching published outcomes are shown beneath the GEL technical clue, with actions, outcomes and source URLs in both languages. No live lookup or panic upload is performed.

## Entry fields
`case_id`, `incident_id`, `models`, `panic_code`, `action_key`, `outcome`,
`reported_date`, `source_url`, `action_gr`, `action_en`,
`observation_gr`, `observation_en`.

- `incident_id` groups sequential attempts on the same handset, preventing misleading counting as independent successful devices.
- `models` accepts semicolon-separated exact Apple hardware identifiers, **not generation-wide guesses**.
- `outcome`: `SELF_REPORTED_SUCCESS`, `SELF_REPORTED_FAILURE` or `UNRESOLVED`. Self-report does not prove causality or repair durability.
- `reported_date` may be `unknown` when the exact date of the reported outcome cannot be determined. Never fabricate a date.
- `source_url` must identify an accessible original report. No raw external panic logs, crash UUIDs, names, Apple IDs, IMEIs or user data are stored.
- SMC decimal values are normalized into hexadecimal (e.g. 262144 = `0x40000`) for same-model comparison. Sensor names alone do not identify the physical part across every generation.

## Initial independent sources and verified **reporting**, not certified repairs
1. iPhone 11 `mic2`, a user reported working operation after replacing power-button flex:
   https://www.ifixit.com/Answers/View/730301/
2. iPhone 11 `mic2`, a user discovered a loose power-button flex and reported working operation after reseating it:
   https://www.ifixit.com/Answers/View/805260/
3. iPhone 11 `mic2`, the original questioner reported one unsuccessful charging-flex replacement then successful operation with another. Additional participants reported other outcomes; do not conflate them with the original device:
   https://www.ifixit.com/Answers/View/645543/
4. iPhone XS Max `PRS0`, initial charging-port replacement unsuccessful, later donor part reported successful:
   https://www.ifixit.com/Answers/View/795864/
5. iPhone 14 Pro `SMC 0x40000` (decimal 262144), charging-port flex replacement reported successful:
   https://www.ifixit.com/Answers/View/953710/
6. iPhone SE (2020) `mic1`, charging-port replacement did not end reboot loop even though microphone function returned:
   https://www.ifixit.com/Answers/View/866821/
7. iPhone 15 `SMC 0x80000`, donor charging-port flex replacement did not resolve the error:
   https://www.ifixit.com/Answers/View/874876/
8. iPhone 14 `SMC 0x400000`, wireless-charging flex replacement did not resolve reboots after water exposure:
   https://www.ifixit.com/Answers/View/844316/
9. iPhone XS Max `PRS0` / later I2C3 signal, multiple charging-port attempts did not resolve loop; a later *different* I2C clue was followed by a reported display replacement success. **Do not attribute the screen repair to PRS0.**
   https://www.ifixit.com/Answers/View/941226/

## Worldwide survey checkpoint (21 November 2026)
The baseline seed has now expanded to **24 source-linked repair attempts** (12 self-reported successes, 10 self-reported failures, one partial/recurrent case, one unresolved case), spanning 10 indexed signature/code keys and 13 model identifiers. These are not independent phones or proof of repair probability. The global scope, additional linked sources, source conflicts and methodology are documented separately in [the world survey](IPHONE_WORLD_PANIC_REPAIR_SURVEY_2026-11-21.md). **The 49 reconstructed QA fixtures are still distinct from the outcome database.**

## Quality rules
1. **Success needs explicit first-hand post-repair feedback** in the cited thread; a selected/accepted answer is not enough.
2. **Failure is evidence too**. Keep unsuccessful swaps, changed signatures, partial recovery and unresolved cases.
3. A single case with multiple attempts is **one device**, not several independent confirmations. Never publish a "success rate" from a convenience sample of forum cases.
4. Avoid model-agnostic hardware recommendations. Example: `SMC 0x40000` can represent different components or no supported mapping on another model.
5. Do not mislabel a suspected connector, a repair proposal or a controlled sensor-disconnection test as a successful customer repair.
6. Distinguish **fixed symptoms**, **confirmed absence of future panic events** and **independent component validation**. Current seed has only self-reported outcomes, not independent follow-up.
7. If there is no exact-model, exact-code entry, show *no repair recommendation*. The GEL clue and existing conservative explanation still work.
8. Use original public URLs, short factual summaries, and editorial paraphrases; do not ingest copyrighted full forum replies.
9. Keep regular research updates versioned and review-source-driven; do not scrape third-party websites automatically without checking their terms and rights.

## Future expansion, not yet implemented
- Broaden research across repair forums, iFixit and repair-shop case studies; validate each reported outcome manually before adding.
- Add date/model/iOS/physical-damage filters and provenance statuses for post-repair panic-log comparisons.
- Optional expert review workflow allowing redacted cases with consent and a second independently checked repair outcome.
- Review model-specific ambiguity with conflicting sources before enabling ranked suggestions. Rankings must not imply measured diagnostic accuracy.
