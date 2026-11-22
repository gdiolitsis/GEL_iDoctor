# GEL iDoctor — provenance-first panic diagnosis and repair-history reliability (2026-11-22)

**Scope:** Improvement to actual iPhone Lab 4 offline knowledge, exact-model lookup, source verification and historical-outcome evaluation. This is **not** a claim of complete coverage of every Apple panic code, Apple-certified solutions, or an autonomously run Apple Diagnostics session.

## What the actual evidence says

- `iphone_panic_repair_knowledge_v1.tsv` currently contains **26 individually source-linked attempted repairs** grouped into **21 reported phone incidents**, spanning **10 distinct normalized panic signatures**, **13 specific model identifiers** and **17 model–signature pairs**. Multiple supported model identifiers in a source may share the same historical attempt; do not sum pair rows as globally independent repairs.
- Outcomes are **self-reported**: **13 reported successes**, **11 reported failures**, **1 partial/recurrent** and **1 unresolved** attempt. These are **not** verified repairs or valid population success percentages.
- The manufacturer technical layer separately includes **1,074 direct links**, **447 unique Apple article sources**, **120 reviewed bilingual diagnostic/technical findings from 12 source articles**, **315 automatically extracted service-procedure metadata articles** linked to **407 model chapter rows**, **62 original short model-specific repair stages from 10 individually reviewed chapters**, and **101 exact-model-and-externally-verified-test-specific conditional service rules**. These denominators **cannot be added together as distinct proven code fixes**.
- The Apple official diagnostics and complete model manual remain the first-party manufacturer reference. Community SMC mapping is a source-linked **hypothesis**, not Apple's verdict. An imported panic alone does not show that any MRI/AST2 diagnostics were actually performed.

## New code now used by Lab 4

`IPhonePanicRepairEvidenceReview.java` groups **attempts** inside their actual **incident ID** and, independently, their **action key**. It explicitly distinguishes:

| Status | What is supported |
|---|---|
| `NO_MATCH` | No source-linked exact-model, exact-clue historical case |
| `UNRESOLVED_OR_PARTIAL_ONLY` | No successful reported recovery, only incomplete/partial |
| `ONLY_REPORTED_FAILURES` | All current matching reports are unsuccessful |
| `MIXED_OR_RECURRING_OUTCOMES` | A source reports mixed results, recurrence, or success/failure disagreement |
| `ONE_SELF_REPORTED_SUCCESS` | One distinct device reports recovery; no independent verification |
| `MULTIPLE_SELF_REPORTED_SUCCESSES` | Different devices report success; never a guaranteed result |

**Important:** A single phone with one failed part followed by success from a second part contributes **one** incident. If that same action failed and then reportedly succeeded on a second attempt, the action history is marked as **mixed** for that incident, not as independently confirmed success. Re-uploading the same panic does **not** duplicate history. Actions are listed alphabetically, **never ranked by success rate or apparent likelihood**.

The Lab 4 historical block now displays the status, number of independent reported incidents, separate action-level historical outcomes and original source URLs. If no case matches, it says so explicitly, without asserting that the device is healthy.

## Parser and source guard

`IPhonePanicRepairKnowledgeBase.java` rejects: duplicate case IDs, malformed/non-Apple hardware identifiers, missing actions or observations, unsupported outcomes, fake/non-HTTPS source URLs, and spoofed domains such as `www.ifixit.com.evil.example`. The current offline source allowlist includes the manually inspected iFixit Answer threads and the named NoPowerFix public repair forum. Extending to another provider requires a deliberate source review, not automatic crawling.

## Public community SMC table: a clue, never an official Apple diagnostic

The independently authored iFixit SMC reference (<https://www.ifixit.com/Wiki/iPhone_SMC_Panic_Assertion_Failed>) lists these **iPhone 15 (iPhone15,4)** *possible* associations used by `ApplePanicHardwareHints.java`:

| Observed SMC value | Source-associated subsystem (not a confirmed fault) |
|---|---|
| `0xA1` | Battery communication |
| `0x80000` | Charging-port assembly |
| `0x100000` | Front sensor assembly |
| `0x200000` | Wireless charging |

They are **not** automatically transferred to the iPhone 14 Pro (which has a different SMC mapping), the 15 Pro family (where other evidence conflicts) or an unknown model. On an uncertain or composite sensor code, the app must preserve an explicit unknown/hypothesis state.

The iFixit overview (<https://www.ifixit.com/Wiki/iPhone_Kernel_Panics>) explicitly cautions that panic interpretation is not an exact science and the same panic may have different physical causes. The Apple official diagnostics entry point is <https://support.apple.com/en-us/101944> (device/OS and regional eligibility may change); a documented Apple MRI/AST2 outcome is **not** implied by log data.

## Test coverage in this PR

- `IPhonePanicRepairEvidenceReviewTest.java`: ten tests on real and synthetic source-linked case trails, repeated uploaded logs, same-phone sequential repairs, mixed repairs, partial recurrence, exact-model negatives and multiple independent but still self-reported successes.
- `IPhonePanicRepairKnowledgeBaseTest.java`: additional negative tests for spoofed HTTPS source domains and guessed rather than exact iPhone IDs.
- `IPhoneSmcCommunityHintIsolationTest.java`: exact-device code associations, different-generation isolation, unknown-model ambiguity, and no confirmed-replacement wording.
- `IPhoneRepairKnowledgeIntegrationContractTest.java`: checks that Lab 4 calls the new incident-level review but **never** automatically calls conditional Apple part replacement based on a panic alone.
- Workflow `.github/workflows/qa-iphone-repair-knowledge.yml` includes source and test changes to execute Android Java compilation and the full JVM suite on this review branch.

## Still out of scope / before public release

- **The number of total Apple panic codes is unknown; the 10 historical keys are not exhaustive.**
- No Apple support document was found that maps every iPhone panic signature to a guaranteed hardware fix.
- Additional incident reports must be manually assessed for exact model, exact code, action, unsuccessful attempts, subsequent recurrence and actual end-user follow-up; a claim of reported repair success is not proof of causal component failure.
- Real iPhone Diagnostics output is an **external source**, never something simulated by the Android app.
- Greek/English physical-device QA, demo-log verification, long-log readability, link handling and HTML/PDF report fidelity remain required.
- Changes remain in **draft PR #181**; main and the protected recovery backup are untouched.

See the exact 17-pair evidence inventory: `docs/qa/IPHONE_PANIC_EXACT_MODEL_CODE_COVERAGE_2026-11-22.md`.
