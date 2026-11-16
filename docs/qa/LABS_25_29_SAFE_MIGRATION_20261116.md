# SAFE MIGRATION — 30 -> 29 Labs, certification before buyer precheck (2026-11-16)

## Owner-approved final user-facing order
- 25 — Critical Android crash history (keep)
- 26 — Application inventory (keep)
- **27 PRO — GEL Device Certification** (was 29; **technical assessment**)
- **28 PRO — Used Device Pre-Purchase Check** (was 28; **buyer/transfer assessment**)
- **29 — Final Technical Report** (was 30)
- Old **27 — System records and measurements** is removed as a standalone lab **only after** every valuable reading and dependent report field has an equivalent grounded source.

**Do not renumber method names or preferences mechanically.** Legacy Java helpers currently have names such as `lab28HardwareStability` (old 27), `lab29DeviceAuthenticity` (old 28), `lab30CombineFindings` (old 29), and `lab31FinalSummary` (old 30). Source dependencies and report labels must be migrated deliberately, not by global search/replace.

## Required evidence flow
1. Lab 25 collects actual recorded crash/ANR/watchdog evidence, with source, independent-incident count, coverage and timestamps. Its inaccessibility must never be reported as "no crashes".
2. New **Lab 27 Certification** checks that Lab 25 was executed during the same boot and within the freshness window. It combines **actual technical results from earlier labs**, including Lab25's substantiated instability risk; it MUST NOT use buyer/IMEI/ownership inputs from Lab 28. It stores a new, versioned, timestamped, source-bound *technical assessment* snapshot.
3. New **Lab 28 Pre-Purchase** requires a current technical assessment from Lab27, then offers independent IMEI/ownership/transfer/FRP-related checks. It presents evidence scope faithfully. It must not invent IMEI blacklist or FRP outcomes from the fact that an external website was merely opened, and must not assert legal ownership without external verification. The buyer's ability to complete setup after authorized reset must be verified in a real transfer process, never guessed by GEL.
4. New **Lab 29 Final Report** summarizes verified observations, remaining limitations (if material), the technical assessment and the pre-purchase outcome only when each is actually available. It must never label 100% uptime as 100% stability, or present unrelated scores as certified health.

## Dependency migration / deletion gate
- Old Lab27's method `lab28HardwareStability()` writes `lab28_obs_schema`, `lab28_obs_wall_ms`, `lab28_obs_elapsed_ms`, `lab28_obs_uptime_ms`, `lab28_obs_battery_c`, `lab28_obs_cpu_c`, `lab28_obs_gpu_c`, and unverified mention mask. These have downstream consumers in **old Lab29 / new Lab27** `lab30CombineFindings()` and **old Lab30 / new Lab29** `lab31FinalSummary()`.
- Replace old27 consumer values with accurate direct Android / existing earlier lab readings; unverified keyword mentions and heuristic scores must **not** be promoted to technical evidence.
- Preserve unrelated working functions, e.g. popups, lab28 thermal helper logic, only while referenced. Delete orphan old27 logic solely after a callsite inventory.
- Migrate all EL/EN user-visible labels, completion, category **25–29**, FAQ/manual references if they exist, report export sections, legacy numbered test contracts, PDF/HTML summaries, and PR acceptance scripts.
- An old snapshot from the previous lab order must be recognized as stale and never pass the new provenance gate. Keep stable old preference keys only when their meaning has not changed.

## Controlled implementation: checkpoints, not one massive edit
A. **Baseline / inventory / freeze:** separate QA branch based on PR #143; protected backup and `main` unchanged; map all readers/writers of old27, old28, old29 and old30 and expected behavior.
B. **Evidence handoff:** Lab25 -> new27 technical assessment, with schema, source, timestamp, missing-data rules and tests.
C. **Buyer gate:** Lab27 -> new28; remove old direct Lab25 -> Lab28 flow; preserve existing PRO access and external IMEI site; gate missing/stale technical report.
D. **Remove redundant old27 and renumber UI/reports:** migrate surviving direct measurements, remove button and only then obsolete method. New ordering 25,26,27 certification,28 pre-purchase,29 final report. Do not erase old27 source before dependent consumers are replaced.
E. **Validation:** compile and unit tests on each checkpoint, targeted EL/EN UI, emulator Android 11/13/16, verify normal/isolated/repeated/strong Lab25 event, limited coverage, cross-boot stale results, offline IMEI, lock check, no false certification; verify PDF/export and compare with protected baseline.

## Stop/go policy
- Keep this PR **draft** and do not merge until CI is green and user-facing QA passes.
- Existing inherited Lab22 and Lab24 contract test failures must be resolved separately and never concealed.
- If a checkpoint fails, revert ONLY the relevant checkpoint instead of wholesale overwriting source methods. Never delete proven functionality to make a test pass.


## v57 consolidation (2026-11-17)
PR144's original checkpoint was followed by PR145's completed 29-lab UI migration and PR146's certification-before-buyer source handoff. This v57 candidate combines their final implementation and updates the interim tests to the final contract. Source-only gates are not a claim of on-device certification. Signed exact-head QA and physical-device validation remain prerequisites to release.
