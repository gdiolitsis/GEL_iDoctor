# GEL iDoctor — v54 signed QA APK | Real-device acceptance and findings

**Tracking opened:** 2026-11-13 (Europe/Athens)  
**Single immutable candidate under test:** `GEL_iDoctor_v54_signed_QA.apk`, `versionCode=54`, `versionName=5.3-QA1`, `applicationId=com.gel.iDoctor`.  
**Candidate source:** commit `cf14d9814ab6d8c96725a6fd0ea52769224446ac` on QA-only branch `qa/gel31-surgical-rebuild-20261113`.  
**Build and QA run:** https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/43507250439  
**Signed artifact:** `gel-v54-signed-source-qa-apk` (artifact ID `14076198267`); contains SHA256SUMS and SOURCE_COMMIT. The candidate APK bytes were not independently uploaded back into this conversation, so do not claim an independent local binary hash for v54.  
**Signing gate:** PASS in GitHub Actions: the configured keystore certificate matches the user's installed GitHub-sourced signed v53 APK (certificate SHA-256 `73:B2:C3:61:44:A1:C8:14:C9:2F:EA:0E:E7:1A:6B:E4:08:C2:F1:11:DB:20:BD:DE:A2:4F:95:AD:95:8B:09:64`); align/sign/verify/build/artifact upload all PASS. v53 was **not** uploaded to Play Console.  
**Real device:** Xiaomi `23076RN8DY`, Android 13 (API 33), as reported in the user's exported service PDF.  
**User-reported installation:** The owner installed v54 early over the previous GitHub-sourced build, before the entire emulator matrix was complete; this is a *reported successful install*, not yet an independent ADB signature/upgrade test. Do not infer Google PRO entitlement for v54 from v53.  
**Source branch:** Draft PR #107, https://github.com/gdiolitsis/GEL_iDoctor/pull/107. `main`, immutable backup and Play release unchanged.

## Scope and evidence hygiene

This log records **only observations attributable to this v54 candidate**; observations from older APKs must not be silently carried forward. Each entry distinguishes **REAL_DEVICE_OBSERVED**, **PDF_OBSERVED**, **SOURCE_AUDIT**, **CI_OBSERVED**, and **DESIGN_AS_INTENDED**. Do not confuse an emulator audit selector failure with a proven real-device regression.

**Evidence received in chat on 2026-11-13:** four owner screenshots covering the Apple DEMO 8-Lab UI from LAB 1 through LAB 8 (Xiaomi at approximately 23:48); owner-provided `GEL_Service_Report (1).pdf` with two pages dated 13 Nov 2026, 23:41:40, showing duplicated Apple ready/init content and otherwise blank technical report fields. The screenshots/PDF were **not uploaded to this public GitHub repository** (privacy and data provenance). This GitHub record preserves textual findings and source/run pointers, not the user's original media.

**Test actually executed:** Apple diagnostic **DEMO** on the Android Xiaomi, using exactly two synthetic preinstalled Apple panic samples from `IPhoneLabsActivity.buildDemoPanicLogs()`. **No actual customer's iPhone logs were imported; no actual iPhone hardware was tested.** None of the 31 Android Labs were executed by the owner in this test and their absence from this PDF is not a defect.

## Positive observations — verified only within the stated scope

| ID | Status / evidence | Observation |
|---|---|---|
| V54-OK-01 | REAL_DEVICE_OBSERVED | Owner reports v54 installed on Xiaomi. App opened and was operable for the Apple DEMO sequence. Exact in-place package-manager audit not yet provided. |
| V54-OK-02 | REAL_DEVICE_OBSERVED | On-screen Apple DEMO ran through LABs 1–8 and displayed end-of-DEMO notice without a reported crash. This verifies this user path executed, **not** each Lab's diagnostic correctness. |
| V54-OK-03 | REAL_DEVICE_OBSERVED + SOURCE_AUDIT | Distinct counters in LAB 5 showed one `Watchdog / Hang` and one `Thermal Shutdown / Throttle` among the two synthetic entries. The counts agree with the event keywords. |
| V54-OK-04 | REAL_DEVICE_OBSERVED + SOURCE_AUDIT | The UI explicitly distinguishes `UNVERIFIED` root-cause certainty and `NOT measured device health`; it warns that imported logs do not prove physical damage. This is desirable; the actual inference remains under review. |
| V54-OK-05 | PDF_OBSERVED + DESIGN_AS_INTENDED | Simulated diagnostic results do **not** appear as genuine service measurements in the normal PDF; demoMode guards the service logger. Isolation is intentional and protects real customer records. |
| V54-OK-06 | CI_OBSERVED | Signed release QA APK produced with certificate equality check, `versionCode=54` and verified package ID; artifact uploaded. These are **build/signing gates**, not full UI acceptance. |

## Open findings for THIS v54 APK

| ID | Area / priority | Evidence classification | Finding, expected outcome and status |
|---|---|---|---|
| **V54-APPLE-01** | LAB 1 / P1 | REAL_DEVICE_OBSERVED + SOURCE_AUDIT | Both synthetic files contain meaningful panic text, but the signature pack returns no known match; screenshot shows “Δεν εντοπίστηκε γνωστή υπογραφή panic”. The pattern JSON requires extra terms not present in these samples. Also “Το log είναι έγκυρο” is stated without structural validation. **Open — improve recognized-vs-unknown classification without inventing cause.** |
| **V54-APPLE-02** | LAB 2 / P1 | REAL_DEVICE_OBSERVED + SOURCE_AUDIT | Parsing the **concatenated** two-entry text produces a single `Watchdog / Hang` crash type and `Baseband / Cellular` domain. `Baseband version: 3.20.01` is routine metadata in the first synthetic file; it is **not** independent evidence of a baseband fault. Second thermal entry loses primary identity in this summary. **Open — per-entry parsing and context-sensitive subsystem evidence.** |
| **V54-APPLE-03** | LAB 3 / P2 | REAL_DEVICE_OBSERVED + SOURCE_AUDIT | “Physical stability not measured” / `UNVERIFIED` correctly expresses limitations, but the displayed crash type originates from the whole combined text rather than an explicit per-record assessment. **Open — preserve caution; report both independent events.** |
| **V54-APPLE-04** | LAB 4 / P1 | REAL_DEVICE_OBSERVED + SOURCE_AUDIT | Reports possible `Baseband / Cellular` involvement using a global string presence check. Correct behavior: metadata alone is not evidence of subsystem malfunction; thermal evidence must remain separate. **Open — prevent misleading hardware hints.** |
| **V54-APPLE-05** | LAB 5 / pass with limitation | REAL_DEVICE_OBSERVED + SOURCE_AUDIT | Two eligible synthetic blocks; `Thermal Shutdown / Throttle: 1`, `Watchdog / Hang: 1` match. “Frequency” here is a sample count, **not** chronological crash rate or recurrence. **Observed count accepted; naming/limits under review.** |
| **V54-APPLE-06** | LAB 6 / P1 | REAL_DEVICE_OBSERVED + SOURCE_AUDIT | Shows `Baseband: 1`, `Thermal: 1`. Counts follow current parser logic, but Baseband category rests on version metadata. **Open — context-aware domain assignment.** |
| **V54-APPLE-07** | LAB 7 / P1 | REAL_DEVICE_OBSERVED + SOURCE_AUDIT | Shows `Baseband (1/2)`, “Ποσοστό επανάληψης: 50%” and text asserting repeated keyword category. The two categories each occur only **once**; 1/2 is a category share, **not a repeat**. There is no verified same-device recurrence. **Open — separate share, duplicates, and longitudinal recurrence.** |
| **V54-APPLE-08** | LAB 8 / P1 | REAL_DEVICE_OBSERVED + SOURCE_AUDIT | Displays `45/100` explicitly labelled an **uncalibrated keyword index** (not physical health), but its formula relies on an unsupported Baseband grouping and treats a one-of-two category share as a repeated high-risk domain. The numeric result is consistent with current weighted formula, **not scientifically validated**. Final summary still says “Επαναλαμβάνεται κατηγορία όρων”. **Open — repair evidence semantics before treating score as meaningful.** |
| **V54-APPLE-09** | Demo-fixture quality / P2 | SOURCE_AUDIT | Synthetic PanicLog1 has `System uptime in nanoseconds: 18446744073709551615`, an implausible interval as a straightforward device uptime (~585 years). Fixture ought to exercise realistic as well as adversarial inputs. **Open — fixture realism/test coverage; do not hide edge-case test.** |
| **V54-REPORT-01** | PDF demo export / P2 (feature gap) | REAL_DEVICE_OBSERVED + PDF_OBSERVED + DESIGN_AS_INTENDED | User ran all 8 Apple DEMO Labs on-screen; normal service PDF does **not** list synthetic Lab findings. Current DEMO logger deliberately excludes them. **Not a lost-data defect:** desired separate DEMO-only PDF with conspicuous “SIMULATION — NOT CUSTOMER DIAGNOSIS”, distinct from real service history; final behavior awaits product decision. |
| **V54-REPORT-02** | Shared log / P2 | PDF_OBSERVED + SOURCE_AUDIT | Actual normal PDF contains two copies of “GEL iPhone Labs — έτοιμο”, initial import instruction and iPhone section heading; no real panic was imported. Initial screen/activity readiness is sent into global `GELServiceLog` on opening and accumulates across visits. **Open — prevent duplicate operational boilerplate contaminating diagnostic report.** |
| **V54-REPORT-03** | UI/status semantics / P2 | REAL_DEVICE_OBSERVED + PDF_OBSERVED + SOURCE_AUDIT | “Εισήγαγε panic log για να ξεκινήσει η ανάλυση” uses green `✔` / `logOk`. This is a **prompt**, not a successfully verified test. **Open — use informational, neutral severity.** |
| **V54-REPORT-04** | Localization / P3 | REAL_DEVICE_OBSERVED | Mixed Greek + untranslated tokens `UNVERIFIED`, `NOT measured device health`, `Watchdog / Hang`, etc. Some are precise technical literals; assess per term and localize explanatory narrative, retaining canonical tokens where needed. **Open — language consistency audit.** |
| **V54-CI-01** | Android 16 EN emulator / P1 investigation | CI_OBSERVED | Source-built v54 emulator run `43507250439` FAILED Apple UI audit with `APPLE_UI_QA_FAILURES=["apple-demo-ui"]` while other main-UI checks reported no app crash. **Open — distinguish automation navigation/selector issue from actual DEMO screen failure.** |
| **V54-CI-02** | Android 11 EN emulator / P1 investigation | CI_OBSERVED | Same run FAILED Apple UI audit `APPLE_UI_QA_FAILURES=["enter-apple-mode"]` despite main UI opening without reported crash. **Open — inspect captured emulator evidence before assigning application root cause.** |
| **V54-CI-03** | Android 11/16 EL emulator / incomplete (cancelled) | CI_OBSERVED | At initial filing the Greek jobs were in progress; final status of that attempt: **both CANCELLED**, not PASS. The reason for cancellation has not been assigned to an app defect. Full Greek acceptance must be rerun on the exact same candidate when appropriate. |

**Source ground truth:** [IPhoneLabsActivity.java](https://github.com/gdiolitsis/GEL_iDoctor/blob/cf14d9814ab6d8c96725a6fd0ea52769224446ac/app/src/main/java/com/gel/cleaner/IPhoneLabsActivity.java), [ApplePanicEvidencePolicy.java](https://github.com/gdiolitsis/GEL_iDoctor/blob/cf14d9814ab6d8c96725a6fd0ea52769224446ac/app/src/main/java/com/gel/cleaner/ApplePanicEvidencePolicy.java), [IPSPanicParser.java](https://github.com/gdiolitsis/GEL_iDoctor/blob/cf14d9814ab6d8c96725a6fd0ea52769224446ac/app/src/main/java/com/gel/cleaner/IPSPanicParser.java), [ips_panic_patterns.json](https://github.com/gdiolitsis/GEL_iDoctor/blob/cf14d9814ab6d8c96725a6fd0ea52769224446ac/app/src/main/assets/ips_panic_patterns.json), [service report source](https://github.com/gdiolitsis/GEL_iDoctor/blob/cf14d9814ab6d8c96725a6fd0ea52769224446ac/app/src/main/java/com/gel/cleaner/ServiceReportActivity.java).

## Interpretive baseline: two built-in DEMO entries

**Synthetic entry A — PanicLog1:** explicit `panic(cpu...)`, `Kernel panic: watchdog timeout`, `watchdogd`, a `Baseband version` metadata field; does **not** prove cellular/modem fault.  
**Synthetic entry B — PanicLog2:** `bug_type=210`, `panicString: panic(cpu...): thermal shutdown`, overtemperature and thermal shutdown cause.  
**Supported comparison:** two *different* event types; one watchdog, one thermal. No repeated event type or verified same-device temporal history; no calibrated root-cause certainty.  
**Unsupported claims to avoid:** 50% “recurrence” from one-of-two, baseband hardware failure because its firmware version appears, device health from 45/100, repair/replacement directive without independent evidence.

## Open real-device acceptance scope

- Android Labs 1–31: **not tested by owner during this Apple-DEMO-only session**; do not mark missing PDF lab data as a regression.
- Actual anonymized iPhone/iPad panic text/IPS/ZIP files: **not yet tested**. Confirm strict per-file parsing, support/unsupported distinction, no cross-file token joining.
- Actual PDF of real (non-DEMO) diagnostic evidence: **not yet evaluated in this session**.
- Portrait/back/exit/TTS across 31 Android Labs, UI color contrast/popups, Android upgrade with preserved app data: **not fully accepted** on this candidate.
- Sign-in/PRO entitlement on v54: **not independently retested by this evidence**.

## QA operating protocol from now on

1. **Version pin:** Every new screenshot, log or exported PDF is registered under this *specific v54 signed QA candidate* until the owner confirms installing a different build. Later patches to the branch do not change the already-installed APK.
2. **Capture:** Record issue ID, screen/Lab, steps, expected vs observed, device/OS, evidence type, diagnostic harm/severity and status. Mark owner-observed separately from source-derived hypothesis.
3. **Scope:** Do not “fix” an issue merely based on syntax or anecdote. Preserve functional old code and extracted helpers; add regression tests for confirmed logic errors; no wholesale rewrite.
4. **Lifecycle:** `OPEN → SOURCE_FIX_READY → CI_PASS → EMULATOR_PASS → REAL_DEVICE_PASS → CLOSED`. A green build alone never closes an issue.
5. **No silent deployment:** No main merge, no new user APK, no Google Play rollout, no account change. Accumulate acceptance findings first; repairs only after review/owner instruction.
6. **Privacy:** Keep user-provided screenshots/reports in chat; do not silently publish them to a public repository.

**This document is an acceptance register, not a claim that v54 is QA-complete.**

### Final status of original emulator attempt 43507250439 (updated)

- Android 16 / English: **FAIL** (Apple UI audit: `apple-demo-ui`); main UI tests had no detected app crash.
- Android 11 / English: **FAIL** (Apple UI audit: `enter-apple-mode`); main UI tests had no detected app crash.
- Android 11 / Greek: **CANCELLED** (no valid final verdict).
- Android 16 / Greek: **CANCELLED** (no valid final verdict).
- The build-and-sign job: **PASS**. **The combined emulator matrix did NOT PASS.** Neither English audit failure is yet proven to be a real-device UI bug; inspect navigation/automation evidence before deciding. Do not treat canceled Greek runs as success.
