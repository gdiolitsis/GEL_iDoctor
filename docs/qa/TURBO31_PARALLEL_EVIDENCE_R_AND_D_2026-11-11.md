# GEL iDoctor — Turbo31: Ερευνητική απογραφή και νόμιμες παράλληλες μετρήσεις
**11 Νοεμβρίου 2026 | Status: RESEARCH, όχι υλοποιημένη λειτουργία**
Βάση σύγκρισης: PR #80 head `b069a5de739f1db9358fb14207a0653837d96abc`. Δεν αλλάζει ο κώδικας των εργαστηρίων, το main, το backup, το Firebase ή η έκδοση στο Play. Οι προτάσεις παρακάτω **δεν έχουν υλοποιηθεί ή επικυρωθεί σε φυσική συσκευή**.

## Θεμελιώδης κανόνας
Κάθε στοιχείο να διατηρεί ρητά: `LAB`, `source/API`, `OBSERVED / DERIVED / USER_REPORTED / UNAVAILABLE`, μονάδα, monotonic timestamp, διάρκεια, έκδοση Android, άδεια, συσκευή, συνθήκες φόρτισης/θερμοκρασίας, δειγματοληψία, flags υποστήριξης, περιορισμούς και σύνδεσμο με related lab. «API exists» ≠ διαθέσιμη ένδειξη ≠ καλιμπραρισμένη φυσική μέτρηση ≠ απόδειξη βλάβης. Αποτυχία/άρνηση άδειας/έλλειψη hardware/μη επαρκής διάρκεια = UNKNOWN, όχι PASS και όχι ελαττωματικό εξάρτημα.

Η πρόσβαση να μένει σε documented public Android SDK, app-private files, σύντομες συγκαταθεμένες δοκιμές και χρήσιμες παρατηρήσεις χρήστη. **ΟΧΙ** root, hidden APIs, private filesystem άλλων εφαρμογών, παράκαμψη SELinux, παράνομη τηλεπικοινωνιακή καταγραφή, κρυφές ενεργοποιήσεις κάμερας/μικροφώνου, server-side συλλογή χωρίς ενημέρωση ή αυθαίρετα hardware scores.

## Επαλήθευση υπάρχουσας κάλυψης από source
- `ManualTestsActivity.java` περιέχει τις πραγματικές `lab1SpeakerTone` … `lab31FinalSummary` διαδρομές, επιπλέον Lab 14B.
- `BatteryPublicCollector.java`: ήδη καταγράφει voltage/temp/status, cycle count όταν API≥34 και διαθέσιμο, current/charge/energy counters όταν υπάρχουν, system charging/discharge time estimate, `getCurrentThermalStatus` και `getThermalHeadroom(0)` με rate-limit ≥1 sec. **ΜΗΝ** ξαναπροτείνουμε ως καινοτομία όσα υπάρχουν ήδη.
- `SensorEvidenceProbe.java`: 5-sec bounded accel/gyro/light/proximity event sampling, cancellation, axes/timestamp/variation, χωρίς ψεύτικο healthy.
- Lab 8: έχει per-camera Camera2 preview, luma, FPS/drop metrics, latency και torch commands. Η πιθανή **προσθήκη** είναι διάκριση logical/physical camera ID και ελεγχόμενη οπτική πρόκληση, όχι κατασκευή νέου απλού preview.
- Lab 18: `StorageEvidenceProbe` ήδη κάνει μικρό app-private write + sync + checksum/read και διαγράφει το αρχείο. Δεν τεκμηριώνει NAND wear.
- Lab 25: `getHistoricalProcessExitReasons` Android 11+ ήδη καλύπτει τους πρόσφατους θανάτους **μόνο του iDoctor**, όχι όλου του τηλεφώνου.
- `SpeedTestActivity` ήδη εκτελεί πραγματικό Internet ping/down/up προς Cloudflare με δικτυακή κίνηση. Lab 10 μετρά χωριστά Wi-Fi PHY/SSID/capabilities, δεν αποδεικνύει Mbps. Μη διαγράψουμε ή διπλασιάσουμε αυτές τις λειτουργίες.
- `Lab28TemporalEvidence` ήδη συγκρίνει δύο battery voltage/temp στιγμιότυπα, απορρίπτει reboot, clock skew, ληγμένη παρατήρηση. Ένα δίδυμο στιγμιότυπο δεν είναι φυσική διάγνωση.
- Lab 21 ήδη χρησιμοποιεί `BiometricManager.canAuthenticate` και opt-in `BiometricPrompt`. Lab 24 διαχωρίζει ενδείξεις root από Verified Boot.

## Πίνακας και των 31 εργαστηρίων
Το «πιθανό» αποτελεί πρόταση **ανεξάρτητης επαλήθευσης**, όχι ισχυρισμό ότι λείπει κάθε σχετική λογική. Οι ήδη υλοποιημένες μετρήσεις παραμένουν.

| Lab | Υπάρχουσα αποτύπωση (πηγαίος κώδικας) | Πρόσθετη νόμιμη παράλληλη δυνατότητα | Όριο / προϋπόθεση |
|---|---|---|---|
| 1 Ηχείο | Tone + ακουστικό challenge | Συγχρονισμένο mic loopback: baseline θόρυβος → stimulus → relative RMS, clipping, διακύμανση | Συγκατάθεση RECORD_AUDIO, διαδρομή speaker, όχι dB SPL / ηχείο “healthy” |
| 2 Sweep | Frequency sweep + bounded evidence | FFT με σταθερές στάθμες/ήσυχο περιβάλλον, σχετική απόκριση ανά ζώνη/παραμόρφωση | Μη βαθμονομημένο μικρόφωνο και Android DSP, όχι απόλυτη συχνιακή απόκριση |
| 3 Ακουστικό | Audio route + καθοδηγούμενη ακρόαση | Paired earpiece vs speaker test σε ίδια ένταση και ίδια θέση | Χρήστης/route, καμία αυτόματη πιστοποίηση physical earpiece |
| 4 Μικρόφωνα | Capture base/pro, guided routes, loop evidence | Σύντομες επαναλήψεις SNR και channel availability με καταγραφή actual routed input | Εξωτερικός ήχος, privacy, mic mute, Bluetooth/USB confounders |
| 5 Δόνηση | Vibration requests + user-reported felt | **Πείραμα** γνωστού ρυθμού δόνησης + accelerometer baseline/on/off, έλεγχος συσχέτισης | Αν ο ρυθμός sensor δεν επαρκεί ή η συσκευή κινείται, UNKNOWN |
| 6 Οθόνη/αφή | Touch/display tests | Χάρτης πυκνότητας πραγματικών touch points και κάλυψης ακμών, `Display.getSupportedModes()` | Μόνο δοκιμασμένα σημεία, όχι απόδειξη τέλειου panel |
| 7 Περιστροφή/εγγύτητα | Οδηγούμενοι sensor/event checks | Τρία σταθερά orientations + near/far sequence, screen/display state ως **χωριστή** παρατήρηση | Sensor near != οθόνη off, ιδίως εκτός πραγματικής κλήσης |
| 8 Κάμερα | Camera2 IDs, preview, frame luma/FPS, RAW/torch | Logical→physical IDs (`getPhysicalCameraIds`), guided σταθερός στόχος για frame variance/exposure/focus convergence | Camera vendor may hide IDs; scene/AE/AF are confounders; no chip diagnosis |
| 9 Αισθητήρες | 5sec accel/gyro/light/proximity stream | Με challenge reference baseline + controlled 3-axis pose, cross-check accel/gravity/rotation and timestamp jitter | Κίνηση χρήστη, OEM fusion και sampling throttling |
| 10 Wi-Fi | RSSI/PHY/SSID/NetworkCapabilities | Συσχέτιση με υφιστάμενο πραγματικό SpeedTest σε **ίδιο Network ID**· προαιρετικό Wi-Fi RTT μόνο σε supported AP | PHY != Internet Mbps; data use/permission; RTT measures ranging, not throughput |
| 11 Κινητή | SIM/data/service/status public APIs | Opt-in πρόσφατη ακολουθία TelephonyCallback + explicit user test call/data | Δεν ξεχωρίζει RF hardware από παροχέα/κάλυψη, άδεια και πολυσυνδρομή |
| 12 Κλήσεις | Passive state + χρήστης/πραγματική κλήση | Ελεγχόμενη A/B με speakerphone/earpiece, outcome και timestamp | Δεν καταγράφουμε ήχο τηλεφωνικής κλήσης, απαιτείται χρήστης |
| 13 Bluetooth | Stack/profile + 60s monitoring | Προαιρετική GATT read από γνωστό διαθέσιμο εξάρτημα και reconnect baseline | Συγκατάθεση, pairing/device/OS confounders; κανένα hardware verdict από ένα failure |
| 14 Μπαταρία A/B | SOC/current/counter, cycle/temp, safe stress/endurance | Paired rest-load-recovery επαναλήψεις με slope V/current/temp και uncertainty | Μην παρουσιάσεις voltage sag ως μετρημένα mΩ/health/full capacity χωρίς calibration |
| 15 Φόρτιση | Public current/charge counter, timed charge behavior | Paired charger connected/disconnected με charge counter delta & charging flags | Μετρά καθαρή μεταβολή μπαταρίας, όχι power adapter/USB PD wattage |
| 16 Θερμική | Thermal status, headroom & derived observations | >=1s headroom trend under identical safe load and cooldown; repeatability | `getThermalHeadroom` forecast/NaN/rate-limited; όχι απόδειξη αισθητήρα |
| 17 Αυτόματος έλεγχος | Συνδυασμός υφιστάμενων εργαστηρίων | Cross-lab consistency graph: ίδιο monotonic time, κοινή συσκευή, γνωστές συνθήκες, conflicts | Χωρίς νέα data δεν παράγει νέο φυσικό συμπέρασμα |
| 18 Storage | StatFs + private-file sync/checksum | 3 μικρές επαναλήψεις, median/p95 write+sync/read σε bounded app-private file | Δεν μετρά NAND erase counts, όχι μαζικές εγγραφές/φθορά |
| 19 RAM | ActivityManager, own PSS, ZRAM hints | Pair app-scoped `onTrimMemory` and frame/UI latency under small user-opt-in workload | Device-wide RAM chip health **UNKNOWN** |
| 20 Reboot | Public uptime + sparse historical signs | Repeat boot-id/elapsed-vs-wall-clock comparison at opted-in sessions | Δεν βλέπει αξιόπιστα όλα τα power cycles ή αιτία reboot |
| 21 Βιομετρικά | `canAuthenticate` + optional biometric prompt | Repeated user-consented prompt success/error-category with timestamps | Δεν αποκαλύπτει pixel sensor / Face unlock physical health |
| 22 Updates/Play Protect | Patch levels, OS security metadata | Optional signed Play Integrity response server-side (separate feature proposal) | Play availability + API/cost, not substitute for vulnerability scanner |
| 23 Developer options | OS exposed flags, own app debuggable | Provenance note for device settings vs app flags, no inferred device-wide ADB | No hidden settings read/no surveillance |
| 24 Root/Boot | Root indicators vs Verified Boot independent | Opt-in Android Keystore key attestation validated server-side, nonce freshness | Attestation ≠ proof of zero root/malware; does not test hardware |
| 25 Crashes/ANR | Recent *own app* exit reasons + limited DropBox | Own `ApplicationExitInfo` reason/time grouped with in-app thermal timeline (with consent) | System-wide crash archive inaccessible, PII-safe log storage |
| 26 Apps | Package visibility and consented usage stats | Opt-in per-visible-package time-delta and app-launch context | Android visibility constraints, no all-app surveillance |
| 27 Permissions | Grants/flags of visible packages | Baseline/second-run changes grouped by special access, app state | Permission visibility incomplete, UNKNOWN ≠ denied/safe |
| 28 Stability | Two-snapshot V/temp temporal delta | Repeated time-aligned pair clusters, deterministic contradictions & recovery | Association ≠ causal component fault |
| 29 Authenticity | Build/vendor fields + stated uncertainty | Compare public metadata vs claimed capabilities; optional **separate** Play Integrity evidence | OEM metadata forgeable; no “original part” proof |
| 30 Technician grade | Evidence coverage/certification withheld if insufficient | Explain numerator/denominator for **test coverage only**, not physical health grade | Missing tests may not be scored as OK |
| 31 Final summary | Structured evidence states and unknowns | User-readable cross-lab contradictions, confidence source, unresolved tests | No new universal device-health percentage |

## High-ROI experiments — recommended order
**P0: Regression-free acceptance**. Run unchanged signed QA APK on API30 and API36 in both languages, app+Apple navigation. Preserve screenshots/XML/logcat; classify PASS / blocked by emulator or permissions / real functional failure. Pair with owner physical-device test. No full release promotion on emulator green alone.

**P1: Lab 5 vibration + Lab 9 accelerometer controlled challenge.** Exact reproducible stimulus, timestamped baseline 2–5 sec, repeated bounded on/off, hardware amplitude support and accelerometer sampling frequency recorded. Reject device motion / lack of sensor / too few samples. Outcome: `correlated_signal_observed`, `not_observed`, `inconclusive`; never “vibrator broken”. Controlled real-device proof required, firmware may filter high frequency.

**P1: Lab 8 logical vs physical camera mapping.** No extra permissions beyond approved CAMERA. Read logical capability, enumerate `getPhysicalCameraIds()` only when supported, show hidden/not-exposed if empty, cross-check lenses already in Camera2 capability report. No open-as-independent claim on API29+ unless ID advertised.

**P1: Lab 9 GNSS optional (new sub-test, not implied by accelerometer).** With explicit location permission, 30-second *foreground* outdoor session: GPS on/off, total visible satellites, usedInFix, median antenna `getCn0DbHz`, median first-fix time if available, number of fresh samples; do not log raw coordinates by default. This is a radio-positioning **observation under particular sky conditions**, not a GNSS antenna fault diagnosis.

**P1: Lab 16 & 14 controlled safe load/recovery trend.** Reuse existing PowerManager thermal headroom and battery current/voltage, no new privileged API. Sample at most 1Hz headroom, timestamp/units, stop on high thermal status, low battery, interruptions, user cancel. Compare *within same device and workload* across repeated runs, never infer manufactured capacity, physical NAND or battery cell resistance.

**P2: Lab 18 storage repeatability.** Reuse scoped StorageEvidenceProbe, three capped probes with median/p95 and explicit environment/low-storage rejection. No broad file access, no stress loops that wear storage.

**P2: Lab 17/28/30/31 evidence provenance fusion.** Build read-only analysis of existing structured summaries with deterministic rules: no causality unless independent evidence corroborates, stale samples rejected, false cert blocked, user-reported not machine-measured. This may improve *interpretation*, not create physical measurements.

**Optional only with separate consent/security review**: Wi-Fi RTT (compatible hardware/AP, fine location) and remote Play Integrity / Keystore attestation; different user permission and privacy impacts. Not release prerequisites.

## Verification gates before adding anything to runtime
1. Capture old vs proposed exact method+dependency diff, UI buttons, routing, locale, permission manifests and report output, backed by commit.
2. Gate new feature behind explicit user opt-in; zero added default permissions unless necessary and approved; no hidden/privileged calls.
3. Unit tests: normal, missing API, missing permission, no sensor, NaN, duplicate/stale/time-skew, cancellation, lifecycle, timeout, low battery/thermal cutoff, second-run mismatch, repeated UI clicks, API 30/36.
4. Verify own-file safe cleanup, no harmful loads, CPU/battery drain bounded, no sensitive raw logs forwarded outside the device.
5. Emulator confirms navigation/states; physical phone/tablet confirms actual physical experiment; only then display derived conclusions with provenance.
6. Run both languages and relevant font sizes. Rebuild on QA branch; do not merge to main until owner-approved full acceptance.
7. Explicit final status: **RESEARCHED ≠ IMPLEMENTED ≠ AUTOMATED-TESTED ≠ REAL-DEVICE-VALIDATED**.

## Authoritative Android documentation (checked 2026-11-11)
- BatteryManager (cycle count API34, energy, charge, current): https://developer.android.com/reference/android/os/BatteryManager
- PowerManager (thermal headroom API30, ~1Hz recommendation, thermal status): https://developer.android.com/reference/android/os/PowerManager
- Display.getSupportedModes: https://developer.android.com/reference/android/view/Display
- CameraCharacteristics.getPhysicalCameraIds: https://developer.android.com/reference/android/hardware/camera2/CameraCharacteristics
- GnssStatus (C/N0 and usedInFix): https://developer.android.com/reference/android/location/GnssStatus
- ActivityManager.getHistoricalProcessExitReasons: https://developer.android.com/reference/android/app/ActivityManager
- ApplicationExitInfo (own-app exit reasons): https://developer.android.com/reference/android/app/ApplicationExitInfo
- Wifi RTT (802.11mc and permissions): https://developer.android.com/reference/android/net/wifi/rtt/package-summary
- BiometricManager.canAuthenticate: https://developer.android.com/reference/android/hardware/biometrics/BiometricManager

## Decision
**Δεν έχουμε εξαντλήσει όλες τις νόμιμες έμμεσες μεθόδους**. Υπάρχουν επιπλέον εφικτά πειράματα και μεθοδολογικά κέρδη, αλλά πολλά από τα API-based primitives έχουν ήδη υλοποιηθεί. Η σωστή πορεία δεν είναι να προσθέσουμε εντυπωσιακά labels ή άκριτα ποσοστά: είναι διαδοχικές μικρές, ελεγχόμενες προσθήκες με ασφαλή διακοπή και cross-lab επαλήθευση — *31 turbo labs, όχι 31 placebo labs*.


## P0 source-integrity findings from deeper Lab 14 / Lab 30 review
**Lab 14 — uncalibrated physical interpretation in a live output path.**
- `ManualTestsActivity.lab14LogStressResult` shows a qualitative `Excellent/Good/Normal/High` “Dynamic resistance” at 1–300 mΩ from `internalResistance[0]`. In the current upstream calculation, `internalResistance[0]` is assigned from `sagFiltered / abs(currentNow)`, **not** the required paired `ΔV/ΔI`. A measured current step is not established. The existing user-facing quality grades therefore cannot be treated as a calibrated battery-cell ESR/SoH finding. Keep voltage/current observations, remove unsupported physical-quality verdicts.
- `lab14EnergyEfficiency = mahPerHour / finalSag`; output name “Energy efficiency” and “Efficient/Normal/Inefficient” thresholds 500/1000 are unsupported. Its actual units are `mAh/(h·V)`, not `mAh/V`, and not thermodynamic/charger efficiency. A derived ratio can remain as an experimental readout only if transparently labeled non-calibrated or removed from the customer-facing conclusion with its source preserved.
- Reference: https://www.ni.com/docs/sq-MK/bundle/battery-cell-quality-toolkit/page/direct-current-internal-resistance.html (DCIR = ΔV/ΔI under a controlled current pulse); https://developer.android.com/reference/android/os/BatteryManager (CURRENT_NOW is instantaneous *net* battery current, not current-step delta).
**Lab 30 — dormant legacy heuristics, not active verified diagnosis.**
- At method entry, `finalScore = NaN`, `lab14CollapseRisk`, `lab14SwellingSuspected`, `nandRisk`, `controllerRisk`, `pmicInstability`, `sensorBusInstability`, and `thermalRunawayRisk` are initialized false; `hardwareGradeSupported` calls `Lab30HardwareCoverage.canGrade(validatedCapacity, false, false, false)` and therefore cannot grant a whole-device grade.
- Old root-cause branches still contain unsupported “Battery degradation”, “NAND/controller”, “PMIC” and weighted confidence 60–95; under the current literal gates they are **dormant**. They must not be re-enabled unless real independent, calibrated evidence exists. Regression tests should assert `UNKNOWN` for uncorroborated device-wide claims.
**Action:** Isolate Lab14 physics-label correction in its own Draft QA PR with old/new method diff and tests. Do **not** amend the already-signed APK in place or merge to main as part of a research-only PR.


## User real-device audit — App cache list selection semantics (11 Nov 2026)
**User screenshots:** installed `454` (102 user / 352 system), cache-mode `Visible=215`; after user-only selection `Selected=36`, system-only `Selected=179`, both `Selected=215`. The counts are self-consistent: 36+179=215. **Do not call this a proven selection-counting bug.**

**Source:** `AppListActivity.java` on PR #80 head. `loadAllApps()` loads `getInstalledApplications(0)` and fills cache via `StorageStatsManager.queryStatsForPackage`, leaving `e.cacheBytes=-1` on failure/unavailability. `applyFiltersAndSort()` explicitly removes every entry with `e.cacheBytes <= 0` in cache mode when Usage Access exists; this conflates confirmed zero and unknown (-1). `btnSelectUsers` selects only non-system items in the current `visible` list. `btnSelectSystem` selects only system items in `visible`. `updateStats` counts all apps in the installed totals but counts selected/visible in a different filtered population.

**Confirmed design discrepancy:** The UI reads “User Apps: 102” while “Select user apps” ticks only the 36 cache-positive, visible entries. The other 66 are *not proven zero cache*: they may be zero or unresolved. Neither device's actual app-list permissions nor the precise distribution is established by screenshots. A positive cache size is not authorization to remove it programmatically; guided cleaning may open Android Settings.

**Recommended correction (not implemented, do not change runtime until tracked acceptance):**
1. Distinguish `KNOWN_POSITIVE_CACHE`, `KNOWN_ZERO_CACHE`, `CACHE_UNKNOWN`, `NOT_SUPPORTED`, `NO_USAGE_ACCESS`, `READ_ERROR` for each package with provenance. Query failures must not be silently reduced to zero.
2. Present totals separately: installed user/system, eligible cache-positive user/system, zero-cache, unknown, **selected among eligible**; translate labels in EL/EN. Show cache-unknown packages with honest “Not available”, not hide them as clean.
3. “Select user apps” should select all **eligible user apps visible in the current scope**; button label must state this accurately, e.g. “Επιλογή εφαρμογών χρήστη με μετρημένη cache”. Zero-cache apps are not default cleanup targets; unknown apps can be opened individually in Android Settings with explicit user action (no hidden bulk-clean claim).
4. Ensure selection state does not change unexpectedly when searching/collapsing a group or reloading stats. `Selected` and `Visible` must use the same documented scope; preserve a clear selected-in-filter vs selected-in-all indication if needed.
5. Default exclude risky system apps from proposed cleanup, avoid root/privileged paths, no automatic deletion of app data. No blind `ACTION_MANAGE_STORAGE` or broad bulk erasure. Test Android 11/16 with zero/unknown/positive cache, permissions denied, search, group collapsed, app returning from Settings, both locales and real device.
6. User says there is a **separate second bug** not yet described; await its evidence before deciding if a combined UI patch is sensible.

**Status:** SOURCE-EXPLAINED, UX-ISSUE LOGGED, **NO APP CODE MODIFIED**. The owner's 36-user vs 102-installed observation is a legitimate discoverability/wording issue, not sufficient evidence of failure to select eligible apps. Official API: https://developer.android.com/reference/android/app/usage/StorageStatsManager and https://developer.android.com/reference/android/app/usage/StorageStats.
