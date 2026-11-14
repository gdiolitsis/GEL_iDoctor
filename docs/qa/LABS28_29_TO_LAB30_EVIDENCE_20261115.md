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
- Optional manual checklist from buyer: external IMEI block-list status, matching identifiers, seen documentation/seller checks, and successful new-owner setup after seller account removal.
- Buyer entries carry timestamp+elapsed clock, and are associated with the current Lab 29 run. The app never reads, stores, uploads or sends IMEI numbers or seller identity.
- A reported blocked IMEI results in red hold-for-review; a fully ticked list is explicitly **buyer-reported**, not proof of ownership or lawful provenance.
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
| Buyer reports a block-list hit | Red caution even when all other items ticked |
| Buyer marks all four checks | Green **user-reported checklist complete**, no legal/OEM certificate |
| Re-run Lab 29 after prior completed checklist | Old buyer assertions cleared |
| Old preference keys from pre-2026 versions | No restored uncalibrated score |
| GR/EN, Android 11/16, landscape and back | Readable dialog; SKIP/RECORD accessible; back behaves as skip |
| Lab 30 → Lab 31 → PDF | Both Lab 28 and 29 sections present; final export retains provenance caveats |

## Verification state
- Source-level changes prepared, not a confirmed APK or device acceptance.
- Unit tests: `LabEvidenceFreshnessTest` and `UsedDevicePrecheckPolicyTest`.
- Requires exact-head Gradle compile/JUnit and Android 11/16, EL/EN emulator + physical device acceptance.
