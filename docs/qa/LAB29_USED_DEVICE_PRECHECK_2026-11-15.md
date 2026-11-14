# Lab 29 — Used-device pre-purchase screening (2026-11-15)

## Goal
Replace the misleading OEM-authenticity claim with a useful, evidence-limited **pre-purchase screening** for second-hand Android phones. Preserve existing hardware observations and the Lab 28 symptom signals. No replacement of working Labs 1–28.

## Sources and limits
- Android-reported manufacturer/model/device/board/build fingerprint: **reported identity only**, not independent model certification or evidence of original parts.
- Battery charge, voltage, display refresh, camera and basic sensor presence: **observations**, not proof of component health or OEM provenance.
- The app does **not** read or store IMEI, serials, owner names, ownership documents, or GSMA results automatically. Android 10+ restricts third-party IMEI access.
- Buyer can display all available IMEIs with `*#06#` and obtain an authorized external lookup. GSMA Device Check may require a registered account; the linked GSMA page is informational, not an automated validation.
- A clean blacklist result **does not prove ownership**; a positive result must be resolved with the carrier. Neither result alone establishes legitimacy.
- FRP/new-owner activation requires seller cooperation; factory reset wipes data and the installed application. Confirm only after appropriate backup, consent, and activation.

## Workflow
1. Run Lab 29: show system identity and hardware observations with truthful evidence bounds; preserve Lab 28 signals for existing Labs 30/31.
2. In a dark/gold scrollable buyer dialog, report one of: unknown, all IMEIs not flagged, or a reported blocked IMEI (exclusive choices).
3. Optionally mark matching identifiers, checked purchase/seller documents, and accounts removed + successful fresh device setup.
4. Persist only check-state/triage, no personally identifying content. Re-running Lab 29 resets old buyer assertions.
5. Report `HOLD_FOR_REVIEW` on a claimed blocked IMEI (red); otherwise `USER_REPORTED_CHECKLIST_COMPLETE` only when **all four** external items were marked (green, explicitly buyer-reported); all other cases `INCOMPLETE` (neutral white).
6. Labs 30 and 31 consume this triage as **buyer-reported**, never as a certified OEM authenticity score or legal ownership declaration.

## Acceptance scenarios
| Scenario | Expected |
|---|---|
| Run with no manual checks, SKIP | Android observations logged; IMEI/ownership/FRP unknown |
| Not flagged but missing documents | Incomplete; no green purchase verdict |
| All four manual checks | Checklist complete **by buyer report**; no legal certification |
| Flagged IMEI, even if other checks selected | Red hold for carrier review; never okay |
| Conflicting "flagged" and "not flagged" selections | UI keeps them mutually exclusive |
| Dual-SIM device | Wording calls for *all* IMEIs |
| Repeat Lab 29 after previously completed checklist | Previous buyer status cleared |
| Battery reported 28% / 3.86 V | Green raw values, no claim of healthy/genuine battery |
| Camera/sensor present | Detection only, not a functional or OEM test |
| Lab 31 after Lab 29 without manual assertions | Says external purchase checks pending, not "Lab 29 missing" |
| GR/EN, Android 11/16, portrait/landscape dialog | Verify readable dark/gold dialog, scroll, skip/record, and back navigation |
| PDF report | Verify buyer statement text is exported accurately |

## Verification
Unit tests: `UsedDevicePrecheckPolicyTest` cover unknown, partial, complete, flagged override, FRP pending, invalid IMEI status.
Build and on-device/Emulator UI acceptance still required; do not mark as passed on the strength of source inspection alone.

## Reference documentation
- Android privacy restrictions: https://developer.android.com/about/versions/10/privacy/changes
- Android FRP: https://support.google.com/android/answer/9459346
- GSMA FAQ: https://devicecheck.gsma.com/rtlapp/faqs/
