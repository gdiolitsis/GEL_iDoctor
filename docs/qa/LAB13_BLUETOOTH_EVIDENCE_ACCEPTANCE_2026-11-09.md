# GEL iDoctor — Lab 13 Bluetooth evidence and acceptance (2026-11-09)

**Status:** isolated implementation proposal; build/emulator/real-device acceptance pending.  
**Base:** Draft PR #46 SHA `363dece0aec9c9c032c3cfa1bfbdf7380dbda38e`.  
**Isolation:** independent from Draft PR #47 (Lab6), #48 (Lab8), #49 (Lab11), #50 (Lab12). No change to `main`, manifest, Google Play, billing or pre-evidence backup.

## Source-confirmed problems
1. The old ACL receiver handled CONNECTED but **ignored ACTION_ACL_DISCONNECTED**. Its `lab13ReceiverSawDisconnection` flag was never set. This hid real link transitions between one-second polls.
2. `getProfileConnectionState(A2DP/HEADSET/GATT)` reports *aggregate local profile states*; it does not identify a particular remote accessory or prove GATT read/write, stable throughput or physical radio health. Combining arbitrary ACL events with aggregate profile states could overstate a specific peripheral's stability.
3. The monitor could wait indefinitely for a peripheral. The "60 seconds" counter was previously incremented by scheduled callbacks, not measured monotonic elapsed time.
4. `getBondedDevices` was logged with **device names and full MAC addresses** to the service report, unnecessarily exposing peripheral identifiers.
5. An aborted/backgrounded monitor could leave callbacks or a receiver active and export an incomplete test; some returns did not reenable report export.
6. A Bluetooth adapter read exception was interpreted as adapter OFF/not supported. A successful adapter state read could become a broad green verdict without functional evidence.
7. The final summary used "Stable Bluetooth connection", "Adapter stable", "Full loss" and conjectured RF/peripheral fault from insufficient aggregate observations.

## Current evidence contract
- `Lab13BluetoothEvidence.Result`: `NOT_TESTED`, `NO_ACCESSORY_OBSERVED`, `INCOMPLETE`, `ADAPTER_UNAVAILABLE`, `AGGREGATE_LINK_OBSERVED`, `AGGREGATE_LINK_CHANGES_OBSERVED`. **No hardware PASS/FAIL.**
- Collect ACL connect AND disconnect broadcasts; track simultaneous or unordered transitions as ambiguous. Combine with Android profile-state polls **only as aggregate indications**, not as a specific remote device result.
- Use monotonic `SystemClock.elapsedRealtime()` for a bounded **60-second** observation after detecting a connected link. Waiting for an accessory has a **45-second timeout**.
- Cancel or background invalidates the run, removes handler callbacks, unregisters receiver and enables report export. Late callbacks from an older generation cannot complete a newer run.
- Preserve unknown/unavailable Android adapter state instead of substituting OFF. A paired-device count does not mean live connectivity. Do not include paired names or MAC addresses in the export.
- A full observation without flagged transitions is a **scoped passive sample**, not calibrated RF stability or verified GATT transfer. A link change may involve any connected peripheral; causation is UNKNOWN.
- No automatic pairing, remote control, BLE scan, new runtime permissions, hidden APIs or battery-intensive RF stress.

## Falsification / device QA matrix

| Case | Configuration | Expected evidence |
|---|---|---|
| B13-01 | User skips external test | NOT_TESTED, no RF/device PASS |
| B13-02 | No adapter available | UNAVAILABLE or limited platform observation; no hardware diagnosis |
| B13-03 | Adapter OFF | Off/blocked, not defective |
| B13-04 | BLUETOOTH_CONNECT denied on Android 12+ | No stale monitor and no hardware FAIL |
| B13-05 | No accessory connected for 45s | NO_ACCESSORY_OBSERVED; receiver cleaned |
| B13-06 | Already-connected A2DP/HEADSET profile | Scoped aggregate link sampling starts |
| B13-07 | External ACL_CONNECTED while waiting | 60s monitor starts, but no per-device identity certification |
| B13-08 | ACL_DISCONNECTED between polling ticks | Transition recorded; not silent stable PASS |
| B13-09 | Connected + disconnected events in same one-second tick | Ambiguous transitions recorded |
| B13-10 | Two peripherals simultaneously / one drops | Aggregate counters clearly labeled; no single-device attribution |
| B13-11 | Complete 60s without transitions | AGGREGATE_LINK_OBSERVED only |
| B13-12 | Connection changes during 60s | AGGREGATE_LINK_CHANGES_OBSERVED; no automatic RF failure |
| B13-13 | Adapter disabled during monitoring | ADAPTER_UNAVAILABLE; no hardware FAIL |
| B13-14 | User exits, Back or app background at 20s | INTERRUPTED / not 60s success; report restored |
| B13-15 | Rotate/recreate Activity during monitor | Stale callback cannot mark new run complete |
| B13-16 | Re-run after early cancellation | Fresh flags/timer, no previous device connection reused |
| B13-17 | Bonded devices include personal headset names/MACs | Only count exposed in service report |
| B13-18 | Android 11/API30, Android 16/API36, Greek/English, two OEMs | Permission, UI, event delivery and export behavior verified |
| B13-19 | Headset and keyboard profile differences | GATT/ACL observations are never equated with successful payload transfer |
| B13-20 | Export Labs 30/31/PDF after NO_ACCESSORY, INTERRUPTED and observed run | No stale green Bluetooth health or fictitious peripheral certification |

## Verification gates
- [ ] `:app:testDebugUnitTest` passes for exact current SHA with the new 10 Java tests.
- [ ] `:app:assembleDebug` and PRO backend/access pass for exact same SHA.
- [ ] Source-built emulator API30/API36, EL/EN, artifact/screenshots and report.
- [ ] Real Android 11/16 with actual user-controlled Bluetooth peripherals, deliberate disconnects, airplane/off/permission, multiple simultaneous devices.
- [ ] Check consumer Labs 30/31 report logic and stale-session record propagation.
- [ ] Explicit owner review/approval before merging or Play publishing.

## Scope boundary
This branch deliberately **does not** implement an actual BLE peripheral GATT read/write/notify challenge. Doing so would require a known, user-authorized compatible target, service/characteristic contract, a permission and Android-version review and a separate test protocol. The initial objective is to prevent false claims from the existing Bluetooth monitoring code.

Android SDK references: [ACL broadcasts](https://developer.android.com/reference/android/bluetooth/BluetoothDevice) · [Bluetooth permissions](https://developer.android.com/develop/connectivity/bluetooth/bt-permissions) · [Bluetooth profile state](https://developer.android.com/reference/android/bluetooth/BluetoothAdapter)
