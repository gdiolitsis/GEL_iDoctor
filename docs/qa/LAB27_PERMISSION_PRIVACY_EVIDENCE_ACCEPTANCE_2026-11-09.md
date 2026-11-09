# Lab 27 — Observable permissions and privacy evidence (2026-11-09)

**Draft source patch only. No successful CI or real-device acceptance claimed.**

## Findings in old source
- Lab 27 required Usage Access even though its permission inventory relied on PackageManager.
- Android 11+ filtered package visibility was not represented as a coverage limitation. Absence of visible packages/flags could be misread as zero risk.
- It inferred a `0–100` "privacy risk" from arbitrary per-permission weights and constant denominator `300`, ranked "top privacy offenders", and marked no flagged app as OK.
- The source could not distinguish missing/misaligned `requestedPermissionsFlags` from a properly observed denial. Special permissions, AppOps, role-holder policies, background access and Privacy Dashboard state were not independently verified.

## New evidence contract
- No Usage Access gate; only PackageManager-visible packages are considered. The count is never a complete device inventory or proof of no hidden app.
- `REQUESTED_PERMISSION_GRANTED` is interpreted only when the corresponding flags position is present; missing flag => UNKNOWN.
- Counts reflect visible **user-app** groups with observed granted permissions: location (including background), microphone, camera, SMS, contacts, calls and notifications. Each app counts at most once per group.
- An observed grant establishes a package-flag snapshot, **not** access at the instant of device use, malicious activity, privacy compromise, or real sensor capture. AppOps, one-time/grant-duration controls, accessibility and special accesses require separate review.
- User is directed to the Android Privacy Dashboard / App info for confirmation. Up to eight local illustrative app labels can appear in the Lab log; audit export/data retention before release.
- No numeric privacy score, "safe", "clean", "offender" or hardware quality claims. `PRIVACY=INCONCLUSIVE` is the service summary until downstream reporting is reconciled.
- No new permissions, package-visibility bypass, network upload, `main` merge or Play rollout. Existing manifest `QUERY_ALL_PACKAGES` requires independent Google Play policy review.

## Authored tests: 18 JUnit cases, execution pending
`app/src/test/java/com/gel/cleaner/Lab27PermissionEvidenceTest.java` exercises denied/unknown/granted flag handling, missing arrays, out-of-range, visible-only scope, exact sensitive permission groups, special access exclusion, unknown vendor values and no safety claim.

## Proposed 20-device falsification scenarios — NOT executed
| # | Scenario | Expected |
|---|---|---|
| 01 | Android 11 / EL | Visible-only coverage, no risk percentage |
| 02 | Android 16 / EN | Visible-only coverage, no safety certificate |
| 03 | Android 11 / EN | Correct visible app permission groups |
| 04 | Android 16 / EL | No English-only keyword interpretations |
| 05 | Work profile inaccessible | Hidden packages not classified as absent |
| 06 | Package query returns null | INCONCLUSIVE |
| 07 | Package query SecurityException | INCONCLUSIVE |
| 08 | Empty visible packages list | Not a privacy-clean claim |
| 09 | Requested permission without flags array | UNKNOWN, not denied |
| 10 | Short/misaligned flags array | UNKNOWN without index failure |
| 11 | Camera permission requested but denied | Not counted as observed grant |
| 12 | Camera granted but no capture activity | Grant only, no surveillance claim |
| 13 | Mic one-time permission transitions | No "permanent" access claim |
| 14 | Background location permission vs foreground | Distinct OS settings, no inferred usage |
| 15 | Overlay/accessibility special access | Explicit unsupported scope |
| 16 | Usage Access absent/revoked | Lab 27 basic observation still runs |
| 17 | Android auto-reset / app hibernation | Show current visible snapshot only |
| 18 | Cancel, Back, rotate, restart | No stale score/previous-record promotion |
| 19 | Sensitive app labels in logs/exports | Local display, no unapproved off-device transmission |
| 20 | Lab30/Lab31/PDF summary | PRIVACY=INCONCLUSIVE never becomes "safe"/GEL CERTIFIED |

## Acceptance gates
- [ ] Exact HEAD Android compile, `:app:testDebugUnitTest`, `:app:assembleDebug`, PRO/backend CI
- [ ] Source-built Android 11/API30 and 16/API36 EL/EN UI QA
- [ ] Real-device permission and work-profile negative controls across OEMs
- [ ] Lab30/31/PDF versioned downstream propagation, privacy and retention audit
- [ ] Explicit Play policy check for existing QUERY_ALL_PACKAGES, owner approval before release

References:
- https://developer.android.com/training/package-visibility
- https://developer.android.com/reference/android/content/pm/PackageInfo
- https://support.google.com/googleplay/android-developer/answer/10158779
