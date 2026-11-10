# GEL iDoctor Lab 15 — Charging-session lifecycle parity

Status: Draft source-only QA candidate based on PR #76 commit `3480124887d467c6422ea8eda9b43656abd8c893`.

## Preserved functionality
- All existing three-minute guided charging observations, charger connection monitoring, net pack charge-counter gain, Android battery temperature / thermal feedback, OS charge-time estimate, Greek/English popup, TTS mute, EXIT control and exported evidence.
- Existing `BatteryChargeEvidence.isValidChargeGain` timing and actual charge-counter rules unchanged. No invented wattage, cable/PMIC failure, battery SoH or replacement verdict.

## Source-confirmed failure paths
1. A new Lab15 attempt did not invalidate prior `lab15_counter_delta_valid`, timestamp, or charge delta; cancellation or initial snapshot failure therefore permitted Labs17/30/31 to reuse an earlier successful run.
2. Null initial battery snapshot returned while leaving `lab15Running=true` and the modal dialog open.
3. The delayed UI runnable was guarded only by global booleans, so a cancelled session could resume when another run set `lab15Running=true`.
4. EXIT logged "Lab15 finished" despite cancellation. Export was not consistently locked/restored during the three-minute guided test.
5. Lab15 did not cancel on Activity pause; late foreground-only observations could complete in the background.

## Changes
- Atomically invalidate prior Lab15 sample at accepted start using `commit()`; fail closed if unable to persist invalidation.
- Per-run generation fence, tested in `Lab15SessionGateTest`, plus guard for delayed voice playback.
- Initial snapshot unavailable -> close modal, stop and release export, no valid evidence.
- On normal completion, disconnect, cancel, above-80% early return, or Activity pause, unlock export only after terminal outcome. Activity pause invalidates the generation.
- Leave existing charging algorithm and threshold behavior unchanged.

## Acceptance matrix
- [ ] Exact-head Java/Android compile and unit tests green.
- [ ] Start with valid earlier Lab15 record, rerun then EXIT: Lab17 must report missing evidence; never re-use stale pass.
- [ ] Rerun with no battery snapshot: dialog closed and export available, without a fake success.
- [ ] Disconnect for >10s: no valid charge delta, no inherited pass.
- [ ] Run, background or rotate then restart: old delayed callbacks/TTS cannot mutate new run.
- [ ] Full three-minute charge, positive charge-counter delta: one valid recorded outcome, fresh timestamp.
- [ ] Above-80% case: no false valid charge, UI recovers.
- [ ] English and Greek Android11/API30 and Android16/API36: popup buttons, TTS, dialog, export, logs.
- [ ] Physical charger/cable and restricted BatteryManager support, real handset. Emulator cannot validate charging hardware.
- [ ] Final PDF and Labs17/30/31 reason about the exact source and run age; no battery-replacement advice.
- [ ] Reconcile diverged candidate QA branches, then obtain owner approval prior to merging or publishing.

Main, protected backup and Play were not changed. No APK generated per Lab.
