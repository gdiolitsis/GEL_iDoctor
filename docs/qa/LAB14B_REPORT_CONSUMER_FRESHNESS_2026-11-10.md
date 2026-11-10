# Lab 14B evidence consumer parity — Labs 17, 30 and 31

## Problem confirmed from source

Lab 17 already required versioned source, valid counter delta, at least 240 seconds, timestamps within two hours, and positive finite observed draw. Lab 30 and Lab 31 independently read the same preference record with **no timestamp/future check** and no positive/finite-rate check. After an old Lab14B run, a stale rate could be reused in Lab30's power-consumption warnings (Lab31 also retrieved it, though full-capacity projection is currently gated away by an unknown baseline).

No public BatteryManager reading proves full-charge capacity state-of-health, physical cell condition, real-world endurance or the need to replace a battery.

## Changes in this draft

- Add pure `Lab14BRateEvidence.isUsable` once for all three report readers: schema=1, exact known charge-counter source, marked valid, duration >= 240000ms, record age <= 2 hours and not from the future, finite positive mAh/h rate.
- Reuse the predicate in Lab17 without changing its successful evidence meaning. Apply the same predicate to Lab30 and Lab31. Stale, future, malformed, legacy and missing measurements are unavailable, never a healthy/failed physical-battery verdict.
- Preserve the real measured-rate value, raw unit and sample capture/write code. Before a newly accepted Lab14B attempt, synchronously invalidate the previous saved rate and timestamp; cancel/failure cannot borrow earlier successful evidence. Lab14 GAMA remains separate and does not erase a Lab14B record.
- Amend three misleading output strings: no false assertion of 'real-world' runtime from projection; a high measured *charge draw under tested workload* is not proof of wear and must not be shown as a red hardware failure; no claim that uncalibrated derived endurance is *measured* or below a verified universal expected value.
- Preserve Lab14A no-replacement customer guidance and the existing Lab17/30/31 structure, entry points, report export and guarded certification.
- Add pure regression tests for valid, stale/future, short, invalid/nonfinite and wrong-provenance records.

## Acceptance gates / risks

- [ ] Exact-head Java/Android compilation and Gradle unit tests.
- [ ] Android 11 and 16 Greek/English guided and report export tests.
- [ ] Device/clock-change test: 14B fresh vs expired vs wall clock moved backward, missing charge counter, interrupted run, and new-run invalidation (including restart/process death). Verify GAMA leaves a valid 14B sample untouched.
- [ ] Confirm partial/unavailable 14B never leaks a fake battery service/replacement verdict into PDF or Lab30/31.
- [ ] Review old `lab14bBaselineMah = -1` semantics; do not reactivate full-capacity extrapolation without independently measured capacity.
- [ ] Review integration of diverged QA PRs #69–#72 before stacking/merging.
- [ ] Owner approval before any merge into main or Play publication.

**No per-Lab APK, no main or backup modification.** This is a bounded source-only QA candidate, not a completed hardware validation.
