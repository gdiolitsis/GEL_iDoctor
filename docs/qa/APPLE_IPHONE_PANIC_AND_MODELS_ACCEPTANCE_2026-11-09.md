# iPhone/iPad model catalogue and panic-log QA — source audit and acceptance

Date: 2026-11-09. **Draft engineering branch; not release-ready.** This is a code-level audit of the Android host application's Apple mode, not validation of iOS internals, Apple hardware, or a real-device sample.

## Scope inspected
- `IPhoneLabsActivity.java`: SAF import, per-file ZIP extraction, Labs 1–8, simulated demo, repeated-log grouping and report output.
- `IPSPanicParser.java` core and `iphone/IPSPanicParser.java` proxy.
- `assets/ips_panic_patterns.json` seven rules.
- `AppleSpecs.java`, `iPhoneSpecs.java`, `iPadSpecs.java`, `MainActivity.java`, `AppleSpecProvider.java`.
- Source catalogue only contained detailed iPhone 11–16 families (and omitted some variants from the picker); later official Apple models were absent. Apple 2026 iPhone 18 Pro/Max and Apple 2025 iPhone 17 are already announced, see sources below.

## P0 findings and corrections
1. The core signature pack asserts `HIGH` confidence, component failure and instructions such as **"Logic board storage repair required"** purely when all configured strings appear in imported text; no model-specific OEM verification, ground truth or specificity calibration. **Correction:** matched text remains an **unverified hypothesis**, with no automatic replacement/board-repair advice. The JSON legacy pack is retained as keyword data pending expert curated rules.
2. Lab8 invented a 0–100 device stability score by arbitrary subtraction from 100, and then inferred hardware/software faults from domain recurrence. **Correction:** retire the score and any single-log diagnosis, present observed imported log count and unverified subsystem hints only.
3. ZIP import used `BufferedInputStream(zis)` then closed it in `readTextStream`, closing the enclosing `ZipInputStream` before later entries could be scanned. Also large text entries were silently truncated; `ZipInputStream` entry counts could be misread and content URI names are not trustworthy ZIP extensions. **Correction:** do not close borrowed streams, enforce size caps with explicit error, use ZIP magic and handle each ZIP entry; never turn partial text into a confident result.
4. Generic terms like `thermal`, `jetsam` or `incident` were treated as validated panic logs. **Correction:** require recognized panic structure or crash bug_type 210, and mark unsupported files clearly.
5. Lab5/6/7 counted wrapper/header fragments as real crash samples, and repeated keyword domains could produce red hardware alarms. **Correction:** count only eligible panic blocks and label recurrence as observation, not a component verdict.
6. `iPhoneSpecs.get` used permissive `contains("iphone 16")` matching, so `iPhone 16e` could falsely inherit **iPhone 16** hardware specifications. `iPadSpecs` mapped *any* "iPad mini" to mini 6. **Correction:** exact model SKU mapping, unknown on unsupported variants; add clearly selected newer official model names to the UI without fabricating non-verified detailed specs. Locale-independent normalization and an explicit warning for unknown model cards.
7. Intro incorrectly claimed that panic logs carry identical diagnostic information across all iPhone/iPad generations. **Correction:** explain log structure and coverage vary by iOS version, model and available evidence.

## Remaining limitations / release blockers
- **Not yet validated against a curated set of real iOS `.ips` files.** Parser may under-match legitimate logs or encounter new Apple log schemas; absence of a match must not imply no problem.
- The old seven-pattern asset file remains uncalibrated. Preserve identifiers as research hints only until experts vet signatures per model, iOS version and component.
- iPhone model reference cards, battery specifications, radio chipsets, SIM variants, supported regional bands and other per-model fields **still require official source-by-source verification**. Do not represent the catalogue as fully updated.
- A recent model may be selectable with **no verified detailed spec record**; the UI must explicitly disclose this instead of borrowing another model.
- The report export path and long-term GELServiceLog storage require review for stale runs and accidental exposure of user-provided log identifiers. The APK does not inspect an iPhone directly.
- No evidence-grade confidence % or component-repair decision from a kernel log alone. True electrical faults/swelling/overheating still warrant qualified physical evaluation.
- Current APK integration with all Android draft PRs remains a separate process; nothing merged into `main`.

## Regression suite — 28 authored unit tests (NOT yet confirmed green)
- `ApplePanicEvidencePolicyTest`: 15 cases, including panic string, bug_type 210 vs other bug types, heat-only/jetsam-only false positives, missing, fake confidence, safe bilingual advice and explicit provenance limits.
- `AppleModelLookupEvidenceTest`: 13 cases, exact iPhone16/Pro/Plus mapping, iPhone16e/17/18 unknown rather than incorrect older specs, iPad mini7 not mini6, mini6 exact, case/locale/series/null.

## Device/emulator acceptance plan — 24 scenarios NOT executed
| # | Scenario | Expected |
|---|---|---|
|1|One real `.ips` panic report|Recognized or explicitly unsupported, no invented root cause|
|2|Header JSON bug_type 210 and panicString|Parsed as panic evidence|
|3|Unrelated jetsam log|Not certified as panic|
|4|Unrelated thermal event text|Not certified as panic|
|5|Non-210 bug_type|No false panic|
|6|Random large text with "storage"|No NAND failure|
|7|Matched NAND strings|Keyword hypothesis; no mandatory board repair|
|8|Matched PMIC strings|No automatic battery/PMIC replacement|
|9|Matched RAM strings|No certified RAM IC fault|
|10|Unknown/new panic signature|INCONCLUSIVE, not "device healthy"|
|11|One imported file|No false multi-log recurrence|
|12|ZIP with 3 valid entries|All 3 visible and counted|
|13|ZIP with valid/invalid mix|Only supported entries counted|
|14|ZIP over max entries|Explicit failure; not silent partial success|
|15|Oversized `.ips` or ZIP entry|Bounded read, explicit failure|
|16|Content URI without .zip extension|Recognize ZIP magic|
|17|Cancelled SAF import|No stale new-run diagnosis|
|18|Repeated import/append mode|No duplicate phantom samples|
|19|Two devices' files in one import|Do not claim one physical device diagnosed|
|20|iPhone 16e selection|Do not show iPhone 16 base specs|
|21|iPad mini 7 selection|Do not show mini 6 specs|
|22|iPhone 17/18 official model selection|Show unsupported detailed-spec warning|
|23|Android 11/16 emulator EL and EN|No crash, correct language and PRO entitlement|
|24|Export to PDF/service report|No unsupported board repair, health /100 or hidden unknown state|

## Acceptance
- [ ] Candidate exact SHA compile, `testDebugUnitTest`, source-built QA APK artifact.
- [ ] API30/API36 UI runs in Greek/English and import behavior (including a multi-entry ZIP).
- [ ] Real anonymized Apple panic fixtures across multiple iOS generations and log types.
- [ ] Independent accuracy review of every model metadata row.
- [ ] Privacy, export, storage and empty/partial/restart checks.
- [ ] Owner approval prior to merging into main or publication.

Official manufacturer model references:
- https://www.apple.com/newsroom/2026/09/apple-debuts-iphone-18-pro-and-iphone-18-pro-max/
- https://support.apple.com/el-gr/125089
- https://www.apple.com/iphone/compare/
