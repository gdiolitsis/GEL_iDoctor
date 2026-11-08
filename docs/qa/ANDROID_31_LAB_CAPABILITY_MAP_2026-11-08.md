# GEL iDoctor — 31-Lab Diagnostic Capability Map

**Status:** engineering research and static code audit, **NOT** proof of functional acceptance. **Date:** 2026-11-08. **Source:** `ManualTestsActivity.java` (31 Labs), `Lab14Engine.java`, `iDoctorEngine.java`, Android Manifest (min API26, target API36). **Priority:** Lab14A is P0 and has a [separate forensic audit](LAB14A_EVIDENCE_AND_CAPABILITY_AUDIT_2026-11-08.md).

## Coverage tiers (required in UI and final report)

- **T1 — Direct platform observation:** SDK returned a reading; log units, timestamp and concrete source. Does *not* establish overall physical health.
- **T2 — Functional challenge:** we issued a reproducible stimulus and observed a corresponding outcome, with a capability/permission check.
- **T3 — Correlated diagnostic hypothesis:** multiple independent observations and a known model justify a scoped probability/risk category; **must** show limitations and validation data.
- **T4 — External technical verification:** physical testing, OEM service records, calibrated sensors, specialized equipment, authorized test identities.
- **U — Unknown/unavailable:** denied permission, no exposure, stale observation, no external accessory, insufficient signals or unsupported OEM. Never green/healthy.

## Per-Lab diagnosis + achievable expansion

| # | Subject | Defensible currently observable signal | Highest-value trustworthy next test | What to avoid promising |
|---|---|---|---|---|
| **01** | Loudspeaker tone | Audio output stream requested; route/volume; optionally recorded mic response | Stimulus/response cross-correlation by frequency and latency, microphone noise-floor baseline, repeated runs (T2) | Certified speaker frequency response or sound-pressure level without calibrated acoustics |
| **02** | Speaker frequency sweep | Sweep emitted; RMS/amplitude variations of sampled microphone response | Detect statistically repeatable missing frequency bands above ambient noise, clip detection, route and capture quality (T2) | Exact speaker health/dB fidelity from an internal mic |
| **03** | Earpiece | Communication output device selected; user perception | Confirm route and user-observed earpiece sound; repeat on wired/Bluetooth disconnected state (T2) | “Earpiece works” from audio routing request alone |
| **04** | Microphone | `AudioRecord` buffer availability and nonzero microphone samples | Test input device ID/routing, background noise, peak/RMS, clipping, duration, signal vs noise and explicit user speech confirmation (T2) | Physical failure of a *specific* microphone unless input path selection is proven |
| **05** | Vibration motor | `VibratorManager`/Vibrator capability and requested vibration | Compare supported amplitude/vibration effects; timed user confirmation; note hardware capability (T2) | Measured vibration strength without an accelerometer calibration / fixture |
| **06** | Display / touch | Native touch events, multitouch pointer IDs, display resolution and frame timing | Guided full-screen grid coverage, path discontinuities, ghost touches under controlled test, display modes/refresh info, user-confirmed visual defect grid (T2) | Absolute pixel brightness, dead pixels or burn-in proven without camera/spectrometer |
| **07** | Rotation / proximity | `SensorManager` events, timestamps, registration success | Known-motion and occlusion challenge; time-to-response, event drops, sensor accuracy/availability (T2) | Sensor stability from mere enumeration or zero logs |
| **08** | Camera | Camera2 characteristics / permission, enumerated lens IDs | Open device and capture actual preview/image frames, timestamp, focus/AE/flash response where supported; manual target pattern (T2) | Optical sharpness, sensor origin or autofocus health from capability declarations |
| **09** | Sensors | Sensor list, vendor, resolution, power, event timestamps/accuracy | Per-sensor standardized movement/occlusion tests; dropout, saturation and repeatability; comparison to expected physical changes (T2) | Real-world calibration or identical sample timing for all sensor types |
| **10** | Wi-Fi | Wi-Fi enabled/link info, RSSI/frequency, active network capabilities | Separate Wi-Fi *association*, Android `NET_CAPABILITY_VALIDATED`, captive portal, DNS and bounded HTTPS reachability; use registered callbacks (T2) | Internet working solely from SSID/RSSI; guaranteed ISP throughput |
| **11** | Cellular | SIM/subscription state, radio/network registration and signal where permission/OEM allows | Track signal metrics + service transitions over a window; test cellular data connectivity on cellular network separately from Wi-Fi/VPN (T2) | Carrier-wide coverage, voice-call quality or baseband health from static signal level |
| **12** | Calling | Telephony state and user-initiated call path | Explicit user consent to guided real call, receiver/mic verification, transitions and call-quality *user feedback* (T2) | Silent/unattended outgoing calls, recording without consent, verified earpiece from device capability only |
| **13** | Bluetooth | Adapter state, scan results and permissions | Explicitly pair/connect authorized test peripheral; GATT read/write/notify turnaround, RSSI and disconnect/reconnect stability (T2) | Radio hardware diagnosis from scan availability alone or without a nearby accessory |
| **14A** | **Battery health / stress** | `BatteryManager` V/I/Q/E/level/temp; SYSFS only when readable; OEM gauges on some devices | **P0:** typed synchronized sample stream, known signal coverage, passive profile, safe bounded rest–load–rest with actual ΔI/ΔV, measured `ΔQ`, repeatability under matched SOC/temp (T1/T2). [Full audit](LAB14A_EVIDENCE_AND_CAPABILITY_AUDIT_2026-11-08.md). | **SoH 100%, calibrated wear, cell balance or swelling** from short CPU stress or discharge rate. Physical battery swelling requires inspection (T4) |
| **14B** | Battery endurance | Observed `ΔQ/\Delta t` and system prediction where available | User-controlled real-world longitudinal battery run, uncertainty, time-varying use and thermal conditions; freshness and actual sample provenance (T2) | Fixed full-day endurance from short uncharacterized extrapolation |
| **15** | Charging | Charge status, source, V/I/Q/temp and `computeChargeTimeRemaining` API28 | Under authorized charger: current-vs-SOC and temperature time series, cable/adapter swap protocol, repeated source stability (T2) | Definite USB port/cable/BMS failure without independent substitution/measurement |
| **16** | Thermal | Exposed battery/CPU/etc temp signals if readable; OS thermal status API29/headroom API30 | Per-zone provenance + physical scope; time-series trend, OS throttling and headroom correlation, safe environmental context (T1/T2) | “All sensors safe”, permanent no-overheating guarantee, one battery reading = CPU temperature |
| **17** | Combined battery reliability | Validated, fresh Lab14A/14B/15/16 records | **Versioned evidence contract** and coverage-limited report: missing component → unknown overall. Validate score weights and calibrate against known tests (T3 only after validation) | Universal 0–100 scientific certainty from arbitrary weights |
| **18** | Storage | `StatFs` total/free bytes, locally sampled I/O | T2 app-sandbox file write/read with checksum, repeated I/O latency and optional fsync; user opt-in, limited bytes, clean-up; separate capacity pressure from integrity | **P0:** NAND wear, filesystem corruption, controller failure from low free space |
| **19** | RAM | `ActivityManager.MemoryInfo`, lowMemory flag, own process PSS | Controlled in-app memory pressure and latencies; log LMK/exit only for own app, distinguish current memory capacity vs performance (T2) | System-wide lag or background-app kills based solely on free percentage |
| **20** | Uptime / reboot | `SystemClock.elapsedRealtime()`, boot timing | User-consented repeated observations across app sessions and verified boot IDs/boot-completed signals where possible; clear retention and confidence (T2) | Multiple reboots inferred from one short uptime sample |
| **21** | Screen lock / biometrics | `KeyguardManager`, `BiometricManager.canAuthenticate` + availability | With consent perform `BiometricPrompt` challenge and handle error/lockout paths; separate enrollment from successful authentication (T2) | Proving fingerprint/face hardware is defect-free from “enrolled” alone |
| **22** | Security patch / Play Protect | OS security patch string, available patch dates, Play Services existence | Calculate patch age from OS data; user-guided official Play Protect settings confirmation; note attestation limits (T1/T2) | Malware-free device, or complete Play Protect active status from `package_verifier_enable` proxy |
| **23** | Developer settings | ADB/developer options toggles that are readable | Show exact enabled settings and concrete threat model; warn if app integrity/USB debugging exposure increases risk (T1) | Treating "Developer options enabled" as proof of compromise |
| **24** | Root / integrity hints | Package/build/system property heuristics and boot verified-state where readable | Structured evidence per check; clear root-unproven vs root-observed; optional strong Play Integrity on backend if separate authorized purpose (T3) | Certifying non-rooted, non-tampered state without privileged attestation |
| **25** | Crash / ANR history | `ActivityManager.getHistoricalProcessExitReasons` API30 for **own UID**; bounded `DropBoxManager` access when available | Correlate own app exits/reasons, watchdog timeouts, Crashlytics test signal, user-initiated local performance challenge (T1/T2) | All-device app/system crash history without privilege |
| **26** | Installed apps | `PackageManager` visible apps, UsageStats (special access), per-UID traffic if exposed | Separate visible vs unknown packages, usage time vs actual resource consumption; compare permissions/access granted; Play policy review of `QUERY_ALL_PACKAGES` (T1) | Complete inventory or precise per-app battery drain from capability heuristics |
| **27** | Permissions / privacy | Runtime permission flags, package visibility, special access state | Report per-app *visible* grants, background permission and privacy dashboard links, OS limitations; use privacy-minimal data (T1) | Full privacy threat proof from manifest alone |
| **28** | Hardware instability | Logs/results *from specific verified prior Labs*, if typed and timestamped | **P0:** schema-bound event correlation (thermal vs power vs radio) with repeated independent signals; UNKNOWN when not observed. Confirm per-session timing before risk prediction (T3) | "Stable" from no matching word or runaway/board failure from text search |
| **29** | Parts / identity observations | Build, camera/display/sensor properties, battery behavior signals | Compare observed hardware descriptors with explicit trusted OEM reference from manufacturer/model and record mismatches as *unverified discrepancy* (T1/T3) | Numerical OEM originality or proof of genuine replacement without OEM docs/physical verification |
| **30** | Full diagnostic score | Aggregation of fresh validated measurements from multiple Labs | Evidence-quality map: which domains passed, not tested, blocked; refuse device certification if required domains are missing; report provenance and schema version (T3/T4) | Default 100 / GEL CERTIFIED when data are missing |
| **31** | Final technician report / PDF | Typed/verified completed findings and their source/time | **P0:** unify output of Labs 14/16/18/28/29/30, structured severity, unknowns, device/report IDs, freshness, signed or hashed report evidence if needed | False “no warnings” from English word scans; legacy OEM percentage; stale result declared current |

## Technical capability inventory by access class

### Immediately available with public APIs and no invasive system access
- Battery V/T/SOC/health-*enum*/status/plug, current NOW & AVERAGE and charge/energy counters **only where supported**; cycle count API34+, capacity level API36+, charging status API34+.
- System thermal status API29+, thermal headroom API30+ (NaN/unsupported possible), OS battery-life prediction API31+.
- Own process memory/exit history, device uptime, storage free bytes, network capabilities and validation, camera characteristics and image frames with user permission, visible sensors with event metadata.
- App-generated controlled read/write checksums inside its own sandbox; user-driven audio/vibration/touch/camera tests.
- Security patch date, certain settings, biometric availability/enrollment and opt-in prompt confirmation.

### Conditional on permissions, a real accessory or special access
- Mic/camera, telephony, nearby Bluetooth and Wi-Fi as required by the OS; user-initiated calls; paired Bluetooth GATT peripheral; network measurements within chosen transport.
- Usage access, package visibility, app inventory: Android background/app privacy rules and **Google Play policy**. Manifest includes `QUERY_ALL_PACKAGES` — retain only if the application's actual core function satisfies the policy and scope it carefully.
- OEM sysfs fuel-gauge statistics, learned capacity, cycle counts, controller/error counters: some devices export them, others don't; root alone does not establish units or correctness.
- Historical trends require user-approved persistence, a series of genuinely matched operating conditions, and protection against data reuse after hardware replacement.

### Not generally observable by an ordinary Play Store app
- Independent per-cell voltage/balance, physical swelling, calibrated display brightness/speaker dB without external instruments, genuine battery manufacturer/part origin, full NAND erase count/lifetime, all-device crashes and kernel errors, full thermal zones on every OEM, absolute real internal resistance.
- Platform attestation is not equivalent to OEM replacement-part certification. Hidden privileged APIs/permissions cannot be assumed accessible and should not be exploited.

## Proposed implementation roadmap (independent PRs; no Play rollout)

1. **P0 Truthfulness:** C01 Lab18 storage wear false proxy; C02 Lab28 log-keyword false signals; C03–C04 Lab31 legacy integrity % and warning detection; **Lab14A B14-01–B14-05** fake confidence/aging/SoH and broken result storage. All should fail closed first.
2. **P0 Common Evidence Layer:** value, units, API/OEM source, timestamp, Android version, device/session ID, permission, sampling window, validity and reason unavailable; centralized typed diagnostics and versioned persistence. Consumer Labs 17/28/30/31 ingest only validated records.
3. **P1 Lab14A passive probe:** collect maximal safe public battery/thermal signal set and per-property coverage before active stress; add tests for impossible values/units/unsupported sentinel; compare with OEM full-design readings only if valid.
4. **P1 Functional QA:** one stimulated test plus measurable response for audio/camera/sensors/network/Bluetooth; use bounded sample sizes and real accessory/user interaction.
5. **P2 Calibrated advanced:** only after published reference dataset of real devices (genuine known healthy/aged batteries, known hardware faults, different OEMs), estimate meaningful risk/uncertainty. Display heuristic index as experimental until validated.
6. **Release gates:** Android 11/16 EL/EN source-built candidate + authorized PRO, real-device evidence, safety, explicit review, Play policy review. Do not merge or publish without owner approval.

## Official Android references

- BatteryManager: https://developer.android.com/reference/android/os/BatteryManager
- PowerManager: https://developer.android.com/reference/android/os/PowerManager
- Camera2: https://developer.android.com/reference/android/hardware/camera2/CameraCharacteristics
- SensorManager: https://developer.android.com/reference/android/hardware/SensorManager
- ConnectivityManager: https://developer.android.com/reference/android/net/ConnectivityManager
- BiometricManager: https://developer.android.com/reference/android/hardware/biometrics/BiometricManager
- ActivityManager: https://developer.android.com/reference/android/app/ActivityManager
- Android package visibility: https://developer.android.com/training/package-visibility/declaring

**Every entry above is a *candidate capability*, not a promise that the current iDoctor already performs it or that it is available on every Android device.**
