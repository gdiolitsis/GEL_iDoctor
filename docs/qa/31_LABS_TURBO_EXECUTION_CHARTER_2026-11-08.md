# GEL iDoctor — 31 Labs Turbo execution charter

**Date:** 2026-11-08 · **Type:** engineering plan and acceptance contract, not a proof of implementation or device validation.  
**Research baseline:** Draft PR #45 and its 31-Lab capability map.  
**Runtime baseline:** Draft PR #46 (at review: `363dece0aec9c9c032c3cfa1bfbdf7380dbda38e`).  
**First isolated follow-on:** stacked Draft PR #47 (Lab 6), based on PR #46.  
**Rollback baseline:** immutable `backup/labs-pre-evidence-20261108`, SHA `69fb2ab85d0528937dc257fc19abc6722462cfc8`.

## Mission
Maximize safely available Android public-SDK diagnostic evidence and honestly communicate the *measurement contract*: what the app directly observed, what response was stimulated, what the user reported, what is a hypothesis, and what remains unknowable without privileged/OEM/external instrumentation.

## Non-negotiable diagnostic states
Every Lab and final report must distinguish `OBSERVED` (source + units + time), `FUNCTIONAL_RESPONSE` (known stimulus and correlated measurement), `USER_REPORTED`, `INCONCLUSIVE`, `UNAVAILABLE`, `INTERRUPTED`, and `NOT_TESTED`. A hardware-health claim is allowed only after the relevant independently validated protocol, **never merely because an activity or build completed**. No zero/100 defaults for missing health data.

Minimum typed record: `labId`, `sessionId`, `runId`, Android API/OEM, `wallClock`, `elapsedRealtime`, `source/route`, observed value, units, capability/permission state, stimulus, start/end, sample count, state, uncertainty/limits, version, error reason and explicit test cancellation.

Any present evidence is scoped by Android's public APIs. Device-level failures, calibrated optical/acoustic/electrical response and OEM component authenticity may require T4 external verification.

## Priority workstreams / dependency chain
1. **Gate A — stabilize PR #46:** inspect exact-SHA compile/unit/PRO results and API30/36 emulator EL/EN artifacts. Keep running CI untouched; do not declare acceptance from a queued run.
2. **Gate B — Lab 6 PR #47:** eliminate stale counts, false digitizer failures, visual false PASS, UI overlap and missing lifecycle invalidation. CI + real LCD/OLED verification.
3. **Gate C — Labs 8, 11–13:** measurable camera, cellular, actual user-initiated call and Bluetooth peripheral challenges, with permissions, routes and test-device prerequisites.
4. **Gate D — Labs 21, 24, 26–27:** biometric authentication test and policy-constrained security/privacy/inventory evidence; never widen permissions without independent Play policy review.
5. **Gate E — deepen Labs 1–5, 7, 9, 10, 14–20, 22–23, 25, 28–29:** prioritize source switching, concurrency, lifecycle, OEM variability and negative-control regressions over extra heuristics.
6. **Gate F — Labs 30–31 integration:** versioned cross-lab source-of-truth, provenance and missing-data propagation, report re-open/restart/upgrade, customer-facing limitation language.
7. **Gate G — acceptance:** compare source-built debug APK on Android 11 (API30) and Android 16 (API36), EL/EN, multiple OEMs; physical accessories and known-fault specimens where possible. Explicit owner approval for any merge or publication.

## All-Lab falsification register
| Lab | Domain | Highest-value evidence expansion | Required negative control / prohibition |
|---|---|---|---|
| 01 | Loudspeaker | Synthetic output + recorded microphone response; baseline, clipping, route | Unplug/BT/mute and silent stimulus must never give speaker hardware PASS |
| 02 | Frequency tones | Band-by-band mic spectral rise over prior noise floor | No calibrated SPL/frequency response without reference microphone |
| 03 | Earpiece | Actual AudioTrack request + observed route + user answer | Failed playback or wrong route => UNAVAILABLE, not earpiece FAIL |
| 04 | Microphones | MIC/CAMCORDER capture and guided loop with sample/route provenance | Exception, background, stale YES, shared physical input => no false dual-mic PASS |
| 05 | Vibration | Vibrator command with timed tactile response | No response/cancel ≠ verified motor failure |
| 06 | Display/touch | 96 visible dot events + staged visual patterns, separate user answer | Cancellation ≠ dead zones; no pixel/burn-in certification; PR #47 |
| 07 | Rotation/proximity | Gravity-or-accelerometer stable transition + FAR-NEAR-FAR | Shake or unordered sensor events cannot PASS; screen-off independently unverified |
| 08 | Camera | Actual frame + exposure/focus/flash response with user target | Camera enumeration or open success ≠ optical health; denied permission UNKNOWN |
| 09 | Other sensors | Timestamped axis-aware event series incl. Y/Z and scalar | Enumerating a sensor or no variation ≠ working/faulty; invalid timestamps rejected |
| 10 | Wi-Fi | Association, RSSI, active transport, VALIDATED and bounded probe | Cellular/VPN default network must not be reported as validated Wi-Fi |
| 11 | Cellular | Subscription registration, service/radio and network transitions | SIM absent, Wi-Fi default or radio blocked ≠ baseband defect |
| 12 | Calling | Explicit user-initiated call and guided two-way audio feedback | Never auto-dial or covertly record; no telephony result without a real call |
| 13 | Bluetooth | Permission-gated discovery and user-paired accessory GATT challenge | No accessory/permission/scan result ≠ broken Bluetooth radio |
| 14A | Battery electrical | Source-typed V/I/Q/E/T with monotonic timestamps and bounded challenge | No fabricated SoH, swelling, balance or uncalibrated ESR |
| 14B | Battery endurance | Matched-source ΔQ/elapsed and clearly separate OS estimate | Short noisy run or charging cannot become full-day endurance |
| 15 | Charging | Verified ΔQ across stable source/temperature/time window | Unstable cable/source and absent counter => inconclusive, not bad charger |
| 16 | Thermals | Explicit per-signal source and OS thermal status/headroom | A battery probe alone does not establish every chip's temperature |
| 17 | Battery summary | Fresh Lab14/15/16 evidence coverage and cross-run data version | Missing downstream source stays UNKNOWN; no synthetic 0–100 |
| 18 | Storage | 256-KiB app-private write/fsync/read SHA-256 and cleanup | Free space cannot indicate NAND wear or whole-device integrity |
| 19 | RAM | MemoryInfo and app PSS and bounded own-process observation | No system-wide defective RAM verdict or unrelated apps' memory |
| 20 | Uptime | Monotonic uptime, per-session boot evidence only where available | Short uptime ≠ damaged hardware or mandatory restart |
| 21 | Biometrics | BiometricPrompt with explicit consent and authenticated outcome | Enrollment/canAuthenticate ≠ proven sensor health |
| 22 | Patch/Play Protect | Real security patch age and user-guided official settings | No malware-free or Play Protect verified from package heuristics |
| 23 | Developer/ADB | Own-app debugger evidence plus settings caveats | Third-party Settings.Global zero cannot prove debugging OFF |
| 24 | Root/integrity | Observed indicators separated from claims of absence; policy-aware attestation | No non-root/tamper-free certification from missing root binary |
| 25 | Crashes/ANRs | Own UID historical process exit reasons API30+ | Cannot read all-device crash history or assert none |
| 26 | App visibility | PackageManager-visible apps with Android visibility limits | No complete app inventory or battery usage without permission/policy |
| 27 | Permissions | Actual accessible permission/special-access observations | No universal privacy cleanliness score; no unjustified QUERY_ALL_PACKAGES |
| 28 | Hardware anomalies | Correlate typed independent signals and time-bound deviations | Never infer PMIC/motherboard/baseband from log keyword scans |
| 29 | Parts provenance | Build and hardware-visible descriptors with OEM reference provenance | No certified original part or repair status without service evidence |
| 30 | Overall diagnosis | Evidence coverage matrix, freshness, incomplete blocks | Any required missing domain blocks GEL CERTIFIED |
| 31 | Technician report | Versioned per-lab states, route/source/time and raw limitations | No false 'no warnings' from string scans or stale caches |

## Per-lab regression gates
For each Lab include: fresh/full run, cancel, Back, background/foreground, rotation/process recreation, permission denial, unsupported API, missing values, zero/negative/unit scaling, simulated failure, overlapping sessions, source/route change, repeated run, data export, and a final PDF/PRO report comparison. Do not claim a physical test was run unless logs and device details demonstrate it.

## Allowed implementation lane
Research and code changes go only to isolated QA branches with small coherent commits and reviews. CI should run on an immutable candidate SHA, with debug artifact hash recorded. Backup branch remains immutable. An older success on another SHA does not count. **No merge into main, Play rollout, new permissions, privileged API bypass, external app data collection or calibration claim without explicit approval.**

## Operational decision
A failed or incomplete test has three separate explanations: `HARDWARE_SIGNAL_NOT_OBSERVED` (not yet hardware failure), `TEST_ENVIRONMENT_BLOCKED`, or `TEST_EXECUTION_FAILED`. Distinguish these all the way to the customer PDF, rather than combining them into a red cross.
