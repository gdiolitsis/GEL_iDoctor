# GEL iDoctor — Lab 11 cellular evidence contract / acceptance

**2026-11-08 · Isolated draft implementation.** Parent: PR #46 (`363dece0aec9c9c032c3cfa1bfbdf7380dbda38e`). Independent of Lab 6 PR #47 and Camera PR #48. No main, Play rollout, permissions or production changes.

## Source-confirmed false-claim risk
The old Lab11 declared "Network is operating normally" with `hasSim && (inService || hasInternet)`. `hasInternet` was defined from Android's *active default network's* `NET_CAPABILITY_INTERNET` regardless of transport, meaning Wi-Fi or VPN could imply healthy **cellular** connectivity. `INTERNET` indicates provisioning, not an Android-confirmed Internet path (`VALIDATED`). A `DATA_DISCONNECTED` state did not prove mobile data had been disabled. A failed/null `getServiceState()` read could be presented as actual out-of-service because the snapshot's initial value was `OUT_OF_SERVICE`.

## Proposed correction in this branch
- Keep SIM `READY/ABSENT/LOCKED/UNKNOWN` distinct from cellular service observations, packet data states and internet routing.
- Explicitly carry `simStateKnown`, `serviceKnown`, `dataStateKnown` and Android-reported cached signal level **when available**. Missing permission/API/OEM signal is `UNKNOWN`, not `0` or a false modem failure.
- Capture default `NetworkCapabilities` `INTERNET`, `VALIDATED`, `TRANSPORT_CELLULAR`, `TRANSPORT_WIFI`, `TRANSPORT_VPN`, `CAPTIVE_PORTAL`; this is a passive snapshot of the **active default**. A validated Wi-Fi or VPN route does **not** validate the cellular radio. Mixed transport is unattributed.
- Report a scoped positive observation only for active, non-VPN, cellular default route with advertised Internet and Android validation; this is **not** a certificate of modem integrity, real RF power or carrier coverage.
- `DATA_DISCONNECTED` alone must not be interpreted as the user's data-off setting or a broken SIM/baseband.
- Preserve `TelephonySnapshot.hasInternet` legacy semantics for unrelated Lab12 consumers pending a *separate* Lab12 review; don't silently alter the call lab in a cellular-only PR.
- No automatic radio network request/activation, no network probe through user's metered SIM, no new runtime permissions.

## 15 falsification and device-acceptance controls

| Case | Stimulus / configuration | Expected evidence |
|---|---|---|
| L11-01 | Wi-Fi validated, SIM ready, cellular data disconnected | Wi-Fi observed; **no cellular OK** |
| L11-02 | Cellular only, validated active default | Observed validated cellular default; no modem hardware certificate |
| L11-03 | Cellular active default, INTERNET but not VALIDATED | Not validated; no false "Internet works" |
| L11-04 | Captive portal on default route | Portal context shown; no false validation |
| L11-05 | VPN validated over unknown Wi-Fi/mobile underlay | VPN validated; cellular provenance unknown |
| L11-06 | VPN not validated | VPN path not validated; no modem diagnosis |
| L11-07 | Both cellular/Wi-Fi transport flags on default | Mixed/unattributed, not direct-cellular certification |
| L11-08 | No default active network | No default; cell hardware remains UNKNOWN |
| L11-09 | Active default but capabilities unavailable | UNKNOWN, not disconnected due to failed modem |
| L11-10 | `getServiceState()` denied/null/error | Service UNKNOWN, not OUT_OF_SERVICE |
| L11-11 | SIM absent/PIN/PUK/network lock/eSIM variance | Distinct states; never physical modem FAIL |
| L11-12 | Airplane mode and/or no SIM | Test blocked/limited, not antenna failure |
| L11-13 | Telephony data state DISCONNECTED with Wi-Fi validated | No unsupported "mobile data disabled" inference |
| L11-14 | Android 11/API30 & Android 16/API36, EL/EN, dual SIM/eSIM, 2 OEMs | Sources/labels/permission failures and signal 0..4 handled |
| L11-15 | Re-run after Wi-Fi/cellular/VPN switch, export Lab30/31 | Current route, current report, no stale green cellular verdict |

## Release gates
- [ ] Latest exact-head SHA Android/backend unit CI, source-built APK, PRO validation green.
- [ ] Source-built emulator UI test matrix API30/API36 EL/EN; inspect screenshots/reports, not only workflow title.
- [ ] Real device cellular/Wi-Fi/VPN/dual-SIM/airplane-mode runs; record observed source, API level and subscription configuration.
- [ ] Cross-lab Lab12, Lab30/31 report truthfulness and legacy migration review.
- [ ] No change to Play permissions/claims without independent policy review.
- [ ] Explicit owner authorization before merge or publication.

**Nonclaim:** This does not test mobile data through a separately bound cellular `Network` while Wi-Fi is the default. It cannot establish a faulty or healthy baseband, RF front end, antenna, carrier, VoLTE/IMS or physical SIM reader. A separate, consented future time-window test may be considered after OEM/permission/roaming/battery-cost review.

Official Android SDK basis: https://developer.android.com/reference/android/net/NetworkCapabilities and https://developer.android.com/develop/connectivity/network-ops/reading-network-state and https://developer.android.com/reference/android/telephony/TelephonyManager
