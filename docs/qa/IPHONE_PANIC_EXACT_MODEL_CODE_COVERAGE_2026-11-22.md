# GEL iDoctor — exact-model panic-code historical repair coverage (22 November 2026)

**Purpose:** This is a precise inventory of the existing, *manually curated* independent repair-history records. It is not an Apple panic-code dictionary, a population study, a diagnostic confidence score or a list of confirmed fixes. The panic clue is a GEL-normalized signature; the manufacturer did not certify any of these forum outcomes.

## Current actual coverage

- Indexed published **attempts**: **26**.
- Distinct reported incidents: **21**; multiple repairs can concern the same phone.
- Distinct observed code keys: **10**.
- Exact model + code combinations: **17**.
- Attempts reported successful: **13**.
- Attempts reported failed: **11**.
- Partial/recurrent or unresolved attempts: **2**.

## Indexed pairs (a documented past attempt is NOT a recommended replacement)

| Exact iPhone identifier | Canonical panic clue | Published attempts | Distinct incidents | Reported success attempts | Reported failed attempts | Partial/unresolved | Source threads |
|---|---|---:|---:|---:|---:|---:|---:|
| `iPhone11,4` | `I2C3_SCL_STUCK_LOW` | 1 | 1 | 1 | 0 | 0 | 1 |
| `iPhone11,4` | `PRS0` | 3 | 2 | 1 | 2 | 0 | 2 |
| `iPhone11,6` | `I2C3_SCL_STUCK_LOW` | 1 | 1 | 1 | 0 | 0 | 1 |
| `iPhone11,6` | `PRS0` | 3 | 2 | 1 | 2 | 0 | 2 |
| `iPhone12,1` | `MIC2` | 7 | 5 | 5 | 2 | 0 | 3 |
| `iPhone12,5` | `AUDIO_CODEC_CS42L77` | 1 | 1 | 1 | 0 | 0 | 1 |
| `iPhone12,5` | `MIC2` | 2 | 1 | 0 | 2 | 0 | 1 |
| `iPhone12,8` | `MIC1` | 2 | 2 | 0 | 2 | 0 | 2 |
| `iPhone13,4` | `PRS0` | 1 | 1 | 1 | 0 | 0 | 1 |
| `iPhone14,3` | `SMC 0x800` | 1 | 1 | 1 | 0 | 0 | 1 |
| `iPhone14,7` | `SMC 0x400000` | 1 | 1 | 0 | 1 | 0 | 1 |
| `iPhone14,8` | `SMC 0x400000` | 1 | 1 | 0 | 1 | 0 | 1 |
| `iPhone15,2` | `SMC 0x40000` | 1 | 1 | 1 | 0 | 0 | 1 |
| `iPhone15,2` | `SMC 0x80000` | 1 | 1 | 1 | 0 | 0 | 1 |
| `iPhone15,3` | `SMC 0x80000` | 2 | 2 | 1 | 0 | 1 | 2 |
| `iPhone15,4` | `SMC 0x80000` | 1 | 1 | 0 | 1 | 0 | 1 |
| `iPhone16,1` | `SMC 0xA1` | 1 | 1 | 0 | 0 | 1 | 1 |

The same published attempt may list more than one exact hardware identifier supplied by the source; therefore summing the model-specific rows need not equal the raw number of catalog attempts. A self-reported success and an earlier failed attempt on a single device are **not** independent successes. Counts above describe attempts, not a statistical success probability.

## Precise contract in Lab 4

1. Use only the actual **exact hardware identifier** and the **same normalized panic clue**. Never borrow repair history from a different iPhone model.
2. Display original Apple source-based diagnostic guidance **before** independent repair reports.
3. Treat a missing match as **no available published repair outcome**, not proof of a healthy device or proof that a fault is irreparable.
4. The incident-level evidence engine distinguishes no result, failures-only, partial/unresolved, mixed/recurring, single self-reported success, or multiple incident-level self-reports. **None** is certified by Apple.
5. Preserve sequential failed and successful attempts in the same incident; aggregate each phone once for independent incident counts. Never turn repeats of an uploaded panic file into more independent evidence.
6. Do not compute an artificial repair success percentage, rank parts by this convenience sample, or recommend invasive repairs without independently observed Apple diagnostics and model-specific safety procedures.
7. The separately curated 101 conditional manufacturer service rules require a real, externally verified Apple test failure or persisting symptom after documented checks. A panic string never satisfies that prerequisite.

**Gap:** This catalog covers **10** normalized signatures, **not all possible Apple panic errors**; no claim is made about the number of total Apple panic codes, which is not published as one comprehensive official repair mapping. The Apple manual URL corpus (1,074 linked chapter entries / 447 distinct articles) is a different dimension of knowledge and cannot be used as a count of mapped panic codes.

**Evidence sources:** Each catalog row preserves its own source URL and first-hand or forum-report outcome, and both successful and unsuccessful attempts remain visible. Source data: `app/src/main/assets/iphone_panic_repair_knowledge_v1.tsv`; parser: `IPhonePanicRepairKnowledgeBase.java`; incident review: `IPhonePanicRepairEvidenceReview.java`; integration: iPhone Lab 4.
