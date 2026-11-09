# Lab 26 — Visible apps & usage evidence (2026-11-09)

Status: **Draft implementation; CI and physical tests NOT yet accepted**.

## Measurement contract
- Android PackageManager only returns applications visible under current package visibility, device profile and Play policy. Even a zero count is `VISIBLE_ONLY`, not a complete inventory.
- `UsageStatsManager` is optional special access; lack of access does **not** block the basic package view. Returned daily buckets can extend outside requested 24h; they are not exact wall-clock 24h usage.
- On Android 7+, `TrafficStats.getUidRxBytes/getUidTxBytes` reports only the **calling UID**. Other UIDs return `UNSUPPORTED`; no fabricated cross-app zero usage.
- No per-app battery consumption, CPU pressure, malware, installed app integrity or OEM authenticity is measured. Manifest-declared capabilities are **not** proof of grant or active use.
- No `su`, root prompts, `dumpsys`, private app-data directories, or elevated system access from this lab. Lab 24 remains separate.
- Use `OBSERVED_LIMITED` for observed visible packages and `INCONCLUSIVE` for failed package queries, never `NORMAL` or high/low health verdict.
- No change to `AndroidManifest.xml`, new permissions, main, release or Play Console. Existing `QUERY_ALL_PACKAGES` must undergo independent policy review **before** release.

## Pure Java regression tests — 16 authored, execution pending
`app/src/test/java/com/gel/cleaner/Lab26EvidenceTest.java`:
unavailable package queries; empty and nonempty visible-only inventory; negative count; own UID traffic; other UID denied; unsupported RX/TX; overflow; real zero for own UID; Usage Access blocked; usage query failed; missing record; valid foreground observation; inconclusive summary; visible-only, no certification.

## Physical and end-to-end falsification matrix — 20 proposed, NOT performed
| # | Scenario | Expected evidence result |
|---|---|---|
| 01 | Android 11, normal user profile, Greek locale | Visible-only coverage label, no device completeness claim |
| 02 | Android 16, normal user profile, English locale | Same contract, no app health verdict |
| 03 | Android 11 with 0 discoverable user packages | Zero visible apps is **not** zero installed apps |
| 04 | Separate work profile with hidden business apps | Cross-profile omission explicitly unknown |
| 05 | Android package query exception | INCONCLUSIVE, not normal/completed PASS |
| 06 | Usage Access never granted | Basic inventory works; usage UNAVAILABLE |
| 07 | Usage Access revoked during test | No stale foreground measurements |
| 08 | Usage query returns null/locked profile | UNAVAILABLE, not 0 minutes |
| 09 | Usage query returns empty list | UNAVAILABLE, not "no activity" |
| 10 | Daily aggregation spans previous day | No exact "last 24h" assertion |
| 11 | Same app appears in multiple buckets | Label as aggregate, not exact session time |
| 12 | Cross-app UID TrafficStats returns UNSUPPORTED | No 0-byte claim for other app |
| 13 | Own UID with true zero traffic | Zero allowed only for accessible own UID |
| 14 | Own UID with positive RX/TX | Report since-boot bytes with caveats |
| 15 | UID counter overflow/invalid sentinel | Reject and mark unavailable |
| 16 | App declares camera/mic but grants denied | "Declared" only, never "using camera/mic" |
| 17 | Many background-capable app manifests, little actual use | No pressure/malware/battery FAIL |
| 18 | Device with developer/root indicators | Lab 26 does not trigger `su` or root prompt |
| 19 | Cancel/Back/background/rotation, rerun | No prior-run stale result is promoted |
| 20 | Labs 30/31 and PDF export | `APPS_IMPACT=INCONCLUSIVE` cannot be converted to normal/certified |

## Acceptance gates
- [ ] Latest exact-head `:app:testDebugUnitTest`, `:app:assembleDebug`, PRO/backend checks
- [ ] Source-built emulator Android 11/API30 and Android 16/API36, Greek/English
- [ ] Real-device Android 11/16, multiple OEMs, work profile and visibility edge cases
- [ ] Labs 30/31/PDF propagation, legacy records and privacy-sensitive logging review
- [ ] `QUERY_ALL_PACKAGES` Google Play policy review and explicit owner release approval

References: https://developer.android.com/training/package-visibility
https://developer.android.com/reference/android/net/TrafficStats
https://developer.android.com/reference/android/app/usage/UsageStatsManager
https://support.google.com/googleplay/android-developer/answer/10158779
