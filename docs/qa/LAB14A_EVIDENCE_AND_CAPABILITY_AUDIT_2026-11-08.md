# GEL iDoctor — Lab 14A forensic audit and capability expansion

**Date:** 2026-11-08 · **Source examined:** `ManualTestsActivity.java`, `Lab14Engine.java`, `iDoctorEngine.java` from `qa/lab-measurement-contracts-20261108` (based on QA integration SHA `69fb2ab85d0528937dc257fc19abc6722462cfc8`). **Type:** source-level audit against Android public API contracts, NOT validated hardware outcomes. **Priority: P0.**

## Executive finding

Lab 14A can become a **strong, evidence-rich battery electrical-behaviour lab**, but **a short stress test is not a calibrated measurement of full-charge capacity, State of Health (SoH), cell balance, or swelling**. Those require specific fuel-gauge capabilities, matched observations, long-window tests, OEM service data or a physical inspection. The current implementation often presents subjective scoring as battery "health %" despite missing those prerequisites.

### Confirmed source-level defects / false-claim risks (14 reviewed items)

| ID | Severity | Code reference (approx. line) | Finding | Acceptance requirement |
|---|---|---|---|---|
| B14-01 | **P0** | `Lab14Engine.java:243–256` | `computeConfidence()` returns a **placeholder** `ConfidenceResult(percent=50, validRuns=1)`, not a statistical estimate. "50%" is not measurement confidence. | Replace with typed quality tiers based on sample validity, timestamp alignment, coverage, signal-to-noise, repeatability. No invented numeric probability. |
| B14-02 | **P0** | `Lab14Engine.java:269–299` | `computeAging()` labels battery quality by **mAh/hour draw**: more load/consumption can be mislabeled as battery wear even if the battery is healthy. | Retire current aging index; use cross-run matched operating-condition comparisons and true capacity reference when available; otherwise "wear not determined." |
| B14-03 | **P0** | `ManualTestsActivity.java:15889–16015` | `health=100f` initially; cycle count, a snapshot peak temperature, estimated resistance and inferred swelling subtract arbitrary percentages. This is **not** measured battery capacity SoH. | Never call this a calibrated SoH percent. Display observations and scoped risk indicators separately; true SoH only from independently validated learned full-charge capacity versus verified original design capacity, or an actual characterized discharge under controlled conditions. |
| B14-04 | **P0** | `iDoctorEngine.java:402–431` | When learned `chargeFullMah` is unavailable, code may set it equal to `chargeDesignMah`, which can then generate **100% SoH by construction**. | Keep unknown full-charge capacity as unknown; never use design capacity as a stand-in for measured present capacity. Track provenance per field. |
| B14-05 | **P0** | `ManualTestsActivity.java:13985–14068,16594–16618,25084–25102,30766–30785,32826–32963` | Lab 14A saves `lab14_last_ts` and `lab14_health_percent` in `lab14_prefs`, whereas Labs 17/30/31 read `GEL_DIAG` and `lab14_health_score`. No matching `putFloat("lab14_health_score")` was seen in the audited activity; cross-lab result provenance/availability may be **broken or stale**. | Unify versioned schema, authoritative store, timestamp, device/session identity, validity flag and migration. Test full run → Lab17 → Lab30 → Lab31, including old installs and partial runs. |
| B14-06 | HIGH | `iDoctorEngine.java:370–396,2606–2610` | The same `normalizeMah()` is applied to `charge_now` **and** `energy_now` sysfs paths. The latter is energy, not electric charge; unit conversion cannot simply divide by 1000. | Use distinct typed `µAh/mAh` and `µWh/mWh` fields. Convert energy to charge only with a simultaneous, justified voltage measurement and explicit uncertainty. |
| B14-07 | HIGH | `ManualTestsActivity.java:15383–15408` | If current is unavailable, it is replaced using average drain divided by elapsed time, then counted as `hasCurrent`. That value is **average discharge rate**, not a simultaneous current sample for `R≈ΔV/ΔI`. | Keep instantaneous, averaged and charge-counter-derived currents separate; gate impedance calculations on synchronized actual current steps and battery voltage responses. |
| B14-08 | HIGH | `ManualTestsActivity.java:15580–15685,15969–15989` | "Smart swelling" is inferred from voltage sag, current, thermal response and impedance. These may be nonspecific electrical anomalies; **swelling is physical geometry** and cannot be verified through these metrics. | Rename to "unusual electrical/thermal behaviour; physical battery inspection advised." Never claim swelling present/absent from software. |
| B14-09 | HIGH | `ManualTestsActivity.java:1010–1036` | Sales dialog promises "cell balance and electrical behavior." Generic public Android readings expose pack-level information, **not individual cell voltage/balance**. | Show cell balance only when per-cell, correctly labeled and verified data is available; otherwise "cannot assess cell balance." |
| B14-10 | HIGH | `Lab14Engine.java:306–312` | `saveDrainValue` and `saveRun` are **no-op stubs**. The Activity independently stores some counters but the engine API implies functionality not present. | Consolidate persistence via one tested repository and run record; verify reproducibility on restart. |
| B14-11 | HIGH | `iDoctorEngine.java:440–464` | `charge_now` availability alone yields `FULL_ACCESS/HIGH` despite potentially missing design/full capacity, current, voltage, cycle count and thermal data. | Rate **coverage per metric**, not a single "full access" label. |
| B14-12 | HIGH | `ManualTestsActivity.java:16608–16618` | A completed run can save heuristic `lab14_health_percent` without proof of learned/design capacity. Cached historical value may survive bad runs without an explicit invalidation policy. | Distinguish good measurement snapshot from validated SoH estimate; never preserve a prior "healthy" score as if current after incomplete runs; version the record and require freshness. |
| B14-13 | HIGH | `iDoctorEngine.java:732–735,484–499,543–640` | `readBatterySnapshotLab()` calls `resolveBatteryModeOnce()` but returns the generic `readBatterySnapshot()`, not the advertised OEM-locked/BatteryManager-locked path. The documented source-lock guarantee therefore needs investigation and a code-level consistency test. | Route via one explicit selected reader or remove the lock claim; record source per field and reject source switching within a run unless all metrics are rebaselined. |
| B14-14 | HIGH | `iDoctorEngine.java:266–318,332–362,543–575` | On ordinary non-OEM reads the public Android `EXTRA_CYCLE_COUNT` introduced at API34 is not consumed, even though the engine's battery snapshot has a cycle-count field. This leaves useful available evidence uncollected on supporting devices. | Read `EXTRA_CYCLE_COUNT` with API gate and unsupported handling, never invent zero cycles, and label reported vs unreported values. |

## What Android can actually give us

| Signal | Public access / minimum API | Physical meaning | Limits and evidence quality |
|---|---|---|---|
| State of charge | `ACTION_BATTERY_CHANGED` level/scale; `BATTERY_PROPERTY_CAPACITY` API21 | Estimated **remaining charge percentage**, **not SoH** | Gauge quantization, filtering, OEM variation; 100% indicated charge does not imply 100% original capacity. |
| Battery voltage | `EXTRA_VOLTAGE` mV | Reported pack voltage | Asynchronous, device-dependent update rates; not individual cell voltages. |
| Battery temperature | `EXTRA_TEMPERATURE` in tenths of °C | Reported pack/embedded-sensor temperature | One sensor ≠ all cells; may be stale/unavailable. |
| Current NOW | `BATTERY_PROPERTY_CURRENT_NOW` in µA | Signed instantaneous **net current** into/out of battery | Positive charging, negative discharging; some devices unsupported; targetSdk 36 unsupported sentinel is `Long.MIN_VALUE`. Sampling lag and noise must be measured. |
| Current AVERAGE | `BATTERY_PROPERTY_CURRENT_AVERAGE` in µA | Averaged net current | Device/OEM-defined averaging window, **not** an instantaneous current step. |
| Remaining charge counter | `BATTERY_PROPERTY_CHARGE_COUNTER` in µAh | Charge stored **at the current state**, not full capacity | Can measure `ΔQ` over controlled intervals if available/stable. Cannot infer full-charge capacity from a short run alone. |
| Remaining energy counter | `BATTERY_PROPERTY_ENERGY_COUNTER` in nWh | Stored electrical energy (if available) | Not the same units as charge; do not normalize as mAh. |
| Android battery-health status | `EXTRA_HEALTH` | Enum such as GOOD/OVERHEAT/DEAD | **Not** % capacity SoH. GOOD is not proof of a new/healthy battery. |
| Cycles | `EXTRA_CYCLE_COUNT` API34, when populated; OEM/sysfs optionally | Reported charge-cycle count | May be omitted or OEM-specific; cycles do not alone determine current usable capacity. |
| Charging status and source | `EXTRA_STATUS`, `EXTRA_PLUGGED`; `EXTRA_CHARGING_STATUS` API34 | Charging/plug state and charging policy when exposed | Charging rate depends on adapter, cable, temperature, SOC, BMS; cannot isolate failed port/charger from one reading. |
| Predicted time-to-full | `BatteryManager.computeChargeTimeRemaining()` API28 | System's estimate while charging | Returns -1 when unsupported; **not** measured battery quality. |
| Predicted discharge duration | `PowerManager.getBatteryDischargePrediction()` API31 | OS estimate of remaining battery life | Returns null in unavailable/charging cases; **not** battery wear. |
| Thermal throttling/status | `PowerManager.getCurrentThermalStatus()` API29 | System-wide thermal stress/throttling classification | Cannot attribute throttle solely to battery/BMS. |
| Thermal headroom | `PowerManager.getThermalHeadroom(int)` API30 | Forecast of proximity to severe thermal throttling | Can return NaN; forecast/OEM variability; no direct per-cell temperature. |
| OEM/sysfs full/design capacity | `/sys/class/power_supply/.../charge_full[_design]` when readable | Device-specific learned/rated capacity | Not public guaranteed access; SELinux/privacy/root and unit/provenance changes. **Never** bypass platform security. |
| Electrical response to controlled load | App-created reproducible workload + monotonic timestamped V/I/T/Q | Within-session sag, temperature slope, repeatability | *App-created workload* is not an instrumented constant-current load. ESR approximate only when real ΔI and ΔV are synchronized and robust. |

Official references:
- https://developer.android.com/reference/android/os/BatteryManager
- https://developer.android.com/reference/android/os/PowerManager
- https://source.android.com/docs/core/perf/health

## Proposed Lab 14A architecture

**Phase 0 — fail-closed data contract (before more features)**

Implement immutable `BatteryEvidenceSample`: `sessionId`, `elapsedRealtimeNanos`, `wallClock`, `deviceModel`, `apiLevel`, `field/value/units/source`, `readSuccess`, `ageMs`, `supported`, `confidenceClass`. Maintain distinct types for voltage (mV), current (µA), charge (µAh), energy (nWh), temperature (0.1°C), level (%), cycle count. **Unknown must be explicit**; no 0 or 100 default. For each run, persist evidence quality and coverage, not just a composite number. Use fresh timestamps and migrate old results as `LEGACY_UNVERIFIED`.

**Phase 1 — passive, hardware-safe high-value profile**

1. Read public battery status, health **enum**, level/scale, voltage, temperature, plug, current NOW and AVERAGE, charge counter and energy counter; capture reason if unavailable.
2. API34+: read cycle count/charging status when exposed; API36+: capacity-level status when exposed.
3. API29+: record `PowerManager` thermal status, API30+ headroom where meaningful, API31+ OS battery-discharge prediction on battery.
4. Observe battery drain trends with **timestamped repeated samples** at stable SOC, light activity, stable connectivity/brightness. Label `observed over X minutes under condition Y`.
5. Record signal noise/variance, missingness, and reporting frequency; **do not over-sample** (some APIs update slowly).
6. Optional: report fuel gauge *internal consistency* (SOC vs `Q_now` derivative, voltage/current update cadence, charge/discharge sign). Flag untrustworthy gauge, not bad battery.

**Phase 2 — controlled and safety-gated electrical comparison**

Capture aligned **rest → modest load → rest** traces with actual current/voltage from hardware, battery SOC, thermal status, device power mode, foreground load characteristics, consistent screen/network conditions. Use an adaptive, **bounded** workload: stop on severe thermal status, overheating, user cancellation, charging plug change, unrealistic signal, unacceptable battery level or OEM/system throttling; never disable safety governors. Reject measurements missing current-step evidence. If valid, report `apparent pack-level dynamic resistance under this test`, with intervals and reproducibility, **not** verified cell internal resistance.

**Phase 3 — better repeatable conclusions**

- Compare repeated runs **under matched SOC, temperature and test workload**, with drift and uncertainty displayed.
- Use charging/discharging `ΔQ` only if monotonic charge counter is reliable, with documented range/window; no extrapolation to full capacity absent validated model.
- Where OEM fuel gauge publishes trustworthy `charge_full` and `charge_full_design`, label source and calculate `reported remaining capacity ratio` (not independent laboratory certification).
- Allow `user-provided rated capacity` only as explicitly entered manufacturer figure; it is not a measurement.
- Device health certification can only be issued when required essential components have valid, fresh evidence; otherwise `INSUFFICIENT DATA`.

## Truthfulness and safety constraints

- **Not measurable from ordinary Android APIs:** actual cell balance (unless exposed per-cell data), battery swelling, physical connector damage, chemical aging, calibrated full-cycle usable capacity, internal electrolyte health, guaranteed BMS limit attribution, and provenance/originality of battery parts.
- **Do not call** an app-computed output “battery SoH 97%” merely because voltage sag is small, SOC is 100%, reported `EXTRA_HEALTH=GOOD`, or discharge current is low.
- Thermal and power limits: background/foreground changes, charger insertion, thermally throttled CPU and device temperature destroy comparability. Unexpected values should **invalidate** the measurement, never silently be filtered into success.
- The 31-Lab summary and PDF must carry evidence coverage, source/time, limitations, unknowns and result schema version. Remove all legacy speculative authenticity/swelling/certification percentages.
- All permissions must remain user-approved, store local data minimally, avoid collecting unrelated personal data, and never require root/privileged bypass for standard operation.

## Reproducible validation matrix (mandatory before merging)

| Test | Input/condition | Expected outcome |
|---|---|---|
| BAT-01 | `CURRENT_NOW=Long.MIN_VALUE`; valid voltage only | Current unknown; no resistance/SoH percentage |
| BAT-02 | Battery charge 100%, no verified learned full capacity | SoH unknown, not 100% |
| BAT-03 | Model design capacity known, learned capacity unavailable | SoH unknown, never substitute design for learned full |
| BAT-04 | Short workload at 150/500/1200 mA, all else equal | Higher draw **must not** by itself be labeled older/degraded battery |
| BAT-05 | Only one run; no historical variance | Consistency/confidence not numeric 50% placebo |
| BAT-06 | `energy_now` in µWh while `charge_now` absent | Energy remains energy; must not be normalized directly into mAh |
| BAT-07 | Current measured several seconds after V-sag | Dynamic resistance unavailable: non-aligned samples |
| BAT-08 | Large temperature increase or system thermal status severe | Abort controlled test safely and label invalid; never recommend higher stress |
| BAT-09 | Missing per-cell voltage data | Cell balance unverified |
| BAT-10 | Electrical irregularity without physical inspection | No physical swelling diagnosis |
| BAT-11 | Completed Lab14A, then 17/30/31, app restart and upgrade from old preferences | All downstream consumers show same **versioned**, fresh record; no stale green/100 |
| BAT-12 | Root/sysfs absent; API 26/30/34/36, both locales | Degrade gracefully; show correct Android public data only |
| BAT-13 | Repeat test under mismatched SOC/temp/brightness | Separate runs as **non-comparable**, do not pronounce degradation trend |
| BAT-14 | Healthy battery and heavy CPU load vs aged battery and light CPU load | Workload-dependent draw never misclassified as battery State of Health |

## No silent merge/release

This document describes a proposed architecture and **identified implementation risks**. It does **not** claim the improvements are coded, unit-tested, or device-validated. Implementation should occur in a separate guarded PR and run through the Android 11/16 + EL/EN QA matrix; real battery laboratory correlation remains a distinct acceptance gate. **No main merge / Play rollout without explicit permission.**
