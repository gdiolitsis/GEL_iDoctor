# Labs 28 + 29 → Lab 30 — structured evidence contract (2026-11-15)

## Branch ancestry
- Parent: `qa/logs-evidence-only-20261114` (PR #123; current evidence-only Lab 28)
- New integration branch: `qa/integration-labs28-29-report30-20261115`
- Carefully ported **only the used-device buyer checklist** of Lab 29 from PR #124 without replacing the recent Lab 28 or 29 evidence-only code.
- Keep `main`, protected backup and research-only PR #45 unchanged.

## What Lab 30 receives
### Lab 28 — System records and measurements
- Versioned, wall-clock + elapsed-realtime-timestamped same-boot snapshot of uptime.
- Battery temperature (where valid) and CPU/GPU thermal-zone readings (when available).
- A **separate, unverified keyword-mention mask** from accessible historical logs, labeled non-diagnostic. Never turn a keyword like `uptime` or `proximity` into a confirmed fault, PMIC/NAND failure, motherboard repair, or health score.
- Missing and expired data remain **unknown**, not green.

### Lab 29 — Used-device pre-purchase check
- Versioned, same-boot Android-reported manufacturer/model/device identifier; not independently authenticated.
- Optional manual checklist: record the result from an **official GSMA Device Check or mobile provider source**, matching identifiers, purchase paperwork and successful device handover; the app does not directly query any official registry.
- Buyer entries carry timestamp+elapsed clock, and are associated with the current Lab 29 run. The app never reads, stores, uploads or sends IMEI numbers or seller identity.
- The operator records the consulted official source, and the app shows **ΑΠΟΚΛΕΙΣΜΕΝΟΣ / ΜΗ ΑΠΟΚΛΕΙΣΜΕΝΟΣ / ΜΗ ΕΠΙΒΕΒΑΙΩΜΕΝΟΣ**. Missing source means **ΜΗ ΕΠΙΒΕΒΑΙΩΜΕΝΟΣ**.
- The previous `lab29_authenticity_score` and `lab29_instability_score` are invalidated and **must not** influence Lab 30.

## QA matrix
| Scenario | Required result in Lab 30 |
|---|---|
| Neither Lab 28 nor 29 run | Separate neutral/unknown sections for both |
| Only Lab 28 run | Fresh uptime and available raw temperatures; Lab 29 unknown |
| Lab 28 lacks CPU/GPU readings | No invented CPU/GPU values |
| Lab 28 log mentions reboot or proximity | Neutral, unverified mentions; no hardware-fault claim |
| Lab 28 repeated on same boot | New snapshot replaces prior values; missing sensors do not inherit old data |
| System reboot / clock jump / >24 h old Lab 28 | Missing/stale section, not positive |
| Lab 29 runs and buyer skips checklist | Android model/observations only; buyer claims unknown |
| Buyer declares a clear lookup but no FRP handover | Incomplete; not approved |
| Operator records officially sourced blocked result | Red IMEI blocked even when all other items ticked |
| Buyer records all checks and names official IMEI source | Green completed checklist; explicit official source |
| Re-run Lab 29 after prior completed checklist | Old buyer assertions cleared |
| Old preference keys from pre-2026 versions | No restored uncalibrated score |
| GR/EN, Android 11/16, landscape and back | Readable dialog; SKIP/RECORD accessible; back behaves as skip |
| Lab 30 → Lab 31 → PDF | Both Lab 28 and 29 sections present; final export retains provenance caveats |

## Lab 31 — Technician's independent intake assessment
The existing Lab 31 now receives both Lab 28 and Lab 29 evidence directly from the
same versioned, same-boot preferences used by Lab 30; it is not inferred from
previous final-report prose. `Lab31ReportEvidencePolicy` continues filtering
old Lab 31 summaries and the existing PDF export button is unchanged.

Three independent records, never rolled into a speculative combined score:

1. **Technical measurements (Lab 28):** recorded uptime and available battery,
   CPU and GPU temperatures; unverified log keyword mentions marked neutral,
   without future-fault or repair-history attribution.
2. **Device/OEM identity (Lab 29):** Android-reported manufacturer/model/device,
   identified explicitly as self-reported and **NOT OEM part certification**.
3. **IMEI source and device handover (Lab 29):** operator-recorded multi-IMEI external
   official-source result, identifier comparison, paperwork and fresh activation/FRP.
   A reported blocked IMEI triggers **CONFIRM SOURCE RESULT**. The report shows only the operator-recorded official-source outcome.
   Independently authentic devices can have disputed provenance.

### Additional technician acceptance cases
| Situation | Lab 31 expected |
|---|---|
| Lab 28 not run, Lab 29 run | Lab 28 explicitly unknown, Lab 29 shown |
| Lab 29 not run, Lab 28 run | Raw Lab 28 readings, ownership/OEM status unknown |
| Recorded official-source IMEI blocked | Red **ΑΠΟΚΛΕΙΣΜΕΝΟΣ**, source shown |
| Recorded official-source IMEI not flagged | Green **ΜΗ ΑΠΟΚΛΕΙΣΜΕΝΟΣ**, source shown |
| Official source missing | IMEI not verified, regardless of other checkboxes |
| Device identity corresponds to a known manufacturer | OEM authenticity still unverified |
| Phone restarted after Labs 28–29 | Both sections stale/unknown, not silently reused |
| Repeated Lab 31 + export | No stale Lab 31 warnings carried into new summary; technician details captured in GELServiceLog for export |
| CPU/GPU temperature high | Raw numeric readings neutral, not false healthy/OK status |

The pure `TechnicianPrecheckAssessment` policy and JUnit tests cover missing,
stale, partial, clean and reported-block outcomes; this is service-intake triage,
**never a substitute for a source-backed IMEI check or OEM assessment**.

## Output wording policy
Only report the official-source status actually entered by the operator:
**IMEI BLOCKED / NOT BLOCKED / UNVERIFIED**, with source shown.
Avoid irrelevant conjectures and do not claim live registry verification.
No unsupported extrapolations about different providers or hypothetical future events.

## Verification state
- Source-level changes prepared, not a confirmed APK or device acceptance.
- Unit tests: `LabEvidenceFreshnessTest` and `UsedDevicePrecheckPolicyTest`.
- Requires exact-head Gradle compile/JUnit and Android 11/16, EL/EN emulator + physical device acceptance.
