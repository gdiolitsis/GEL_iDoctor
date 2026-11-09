# GEL iDoctor — Lab 12 call evidence and real-device acceptance

**Date:** 2026-11-09 · **Status:** isolated Draft PR implementation; unit/emulator/real-device acceptance pending.  
**Parent:** Draft PR #46 at `363dece0aec9c9c032c3cfa1bfbdf7380dbda38e`.  
**Scope:** Lab 12 voice-call truthfulness and opt-in user-guided dialer flow. No manifest/permission, `main`, Play, billing or release changes. Original backup remains immutable.

## Source-level problems confirmed
1. Prior Lab 12 reported "Internet available" from the generic default network's `NET_CAPABILITY_INTERNET` attribute, which is not a validated Internet connection, not IMS voice registration, and may be Wi-Fi/VPN rather than cellular.
2. A missing, denied or null `getServiceState()` could be presented as an observed *not in service* because shared snapshot `inService=false` was not paired with a valid-read flag.
3. `SIM_STATE_READY`, legacy service registration and generic Internet capability did **not** verify any outgoing/incoming call, audibility, microphone direction or earpiece route. The old activity explicitly never dialed, yet exported an apparently completed lab.
4. No guided real-call observation channel existed, leaving no way to capture explicit, limited two-way user feedback.

## Implemented evidence contract
- Fresh, Lab12-specific passive observations: airplane mode `ON/OFF/UNKNOWN`, Android device voice capability `REPORTED/NOT REPORTED/UNAVAILABLE`, SIM state (default profile where exposed), legacy telephony `IN_SERVICE/NOT_REPORTED/UNKNOWN`.
- `isDeviceVoiceCapable()` on API35+, `isVoiceCapable()` on earlier supported APIs. Voice capability is **not proof** of an active voice subscription.
- No inference that generic Internet, even `VALIDATED` Wi-Fi/VPN, proves IMS/VoLTE/VoWiFi or two-way call operation.
- Optional explicit action opens an **empty ACTION_DIAL UI** with no phone number. The user alone chooses recipient and initiates or declines the call. No `ACTION_CALL`, `CALL_PHONE`, call logs, call-state tracking, number collection, microphone recording, contact access, or call automation.
- After returning, user must affirm a **real completed call**, then separately report whether they heard the other party and whether the other party heard them.
- Results remain strictly `NOT_TESTED`, `INCONCLUSIVE`, `USER_REPORTED_TWO_WAY_AUDIO` or `USER_REPORTED_ONE_WAY_OR_AUDIO_ISSUE`. No automatic hardware PASS/FAIL; cannot independently identify earpiece, physical microphones, radio, modem or call type.
- Back, skip, dialer launch failure, missing/partial answers, rotation/session recreation and other interruptions may not generate confirmed two-way audio.
- Actual carrier charges may apply; use a consenting known person, not emergency numbers. No data about that person's identity or conversation is stored.

## Acceptance / falsification matrix

| ID | Scenario | Required outcome |
|---|---|---|
| C12-01 | SIM ready, legacy service reported, no call performed | Passive observations only, no voice-call PASS |
| C12-02 | Wi-Fi/VPN validated with no cellular service | Never infer IMS/voice-call success |
| C12-03 | No READ_PHONE_STATE, security exception, null ServiceState | UNKNOWN, not observed no-service |
| C12-04 | Airplane mode ON | Limited observation, no handset defect |
| C12-05 | Voice capability not advertised on tablet/data-only device | No voice-capability claim or fabricated modem defect |
| C12-06 | Dual SIM/eSIM with different subscription service | Label default exposed state, never claim every subscription tested |
| C12-07 | Press SKIP or dismiss initial guide | NOT_TESTED, export enabled |
| C12-08 | ACTION_DIAL unhandled/blocked | NOT_TESTED, no crash or auto-call |
| C12-09 | Open empty dialer, return without calling | NOT_TESTED after explicit answer |
| C12-10 | Return from dialer with RESULT_CANCELED despite an actual call | Ignore result code; explicit user confirmation only |
| C12-11 | User affirms a real call but no full two-direction answers | INCONCLUSIVE |
| C12-12 | Real call, user reports hearing and being heard | USER_REPORTED_TWO_WAY_AUDIO, not hardware certification |
| C12-13 | One-way, muffled or no audio reported | USER_REPORTED_ONE_WAY_OR_AUDIO_ISSUE; no auto hardware failure |
| C12-14 | Route via speaker, wired or Bluetooth headset | Report still user-perceived call; never claim physical earpiece/mic identity |
| C12-15 | Back/rotation/background while guide/dialer is open | No stale YES and no machine-certified call |
| C12-16 | Android 11/API30, Android 16/API36, Greek/English, multiple OEM dialers | Test UI, callback, report flow and graceful fallbacks |
| C12-17 | Rerun and Lab30/31/PDF export after partial/cancel/completion | Show current scoped findings, no recycled green call verdict |

## Mandatory gates
- [ ] Current SHA Android compile, unit tests, PRO backend and `assembleDebug` success.
- [ ] Source-built emulator UI QA API30/API36 in Greek and English.
- [ ] Two real devices (Android 11 and 16): consented real-call user feedback, permissions denied, SIM/eSIM/airplane-mode, empty dialer, background/Back/rotation/kill.
- [ ] Independent cross-Lab3/4 audio route and Lab11 telephony observations and final Lab30/31 report audit.
- [ ] Privacy/Play review (no permissions added; consider local report only).
- [ ] Explicit owner approval before any merge or Play publication.

**Nonclaim:** This cannot independently establish that the user really placed a call, which carrier route was used, whether IMS/VoLTE was registered, which physical transducer was active, or whether a device component is defective. Corroboration needs authorized system/network evidence or a physical telecom test.
