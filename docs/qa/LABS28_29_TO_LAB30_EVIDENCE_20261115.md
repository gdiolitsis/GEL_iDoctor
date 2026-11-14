# Labs 28–29 → Labs 30–31 — evidence and buyer privacy contract (2026-11-15)

## Branch safety

- Based on `qa/logs-evidence-only-20261114` (PR #123).
- PR #125 is a draft. The protected backup, research-only PR #45 and `main` are untouched.
- Retain the existing Android observations in Lab 29 and all measurements in Lab 28.

## Lab 28: structured Android measurements

Lab 28 records same-boot, timestamped uptime and available battery/CPU/GPU temperatures.
It keeps keyword mentions from old logs **separate from actual measurements**. No
component-health certificate, hidden failure prediction or fabricated numerical score.

Both Lab 30 and Lab 31 consume the fresh Lab 28 snapshot independently using
`LabEvidenceFreshness`, with a 24-hour and same-boot freshness check.

## Lab 29: private external IMEI guidance, never a result

**User decision:** GEL iDoctor only teaches the buyer how to check IMEI privately.

1. Open the system dialer and enter `*#06#`; read or copy all available IMEIs.
2. Open the external CheckMEND consumer report page in the system browser:
   `https://www.checkmend.com/eu/consumer/report`
3. The buyer independently fills in IMEI and requested personal data directly on
   the provider's site, pays the provider directly if desired and reads the result there.
4. No return-to-app questions or result entry. The purchaser owns their decision.

### Data-minimization requirements

- The Android app never reads, requests, receives, records, uploads, or forwards the
  external IMEI result, provider credentials, buyer personal data or payment details.
- No `CheckBox`, `EditText` or status selector in the IMEI guidance dialog.
- The external URL has **no query parameters**, so the app passes no device identifiers.
- No `SharedPreferences` state for blocked/clear/unknown result, source of check,
  documents, seller verification, purchase approval or FRP outcome.
- On opening `ManualTestsActivity`, legacy QA buyer-check preference keys are
  cleared in order to remove obsolete data from earlier experiments.
- Technical Android observations from Lab 29 (manufacturer/model/device, battery,
  basic hardware availability) still report to Labs 30 and 31, with their existing
  evidence limitations. Neither receives IMEI status.
- An external provider may collect/payment-process buyer data under its own terms;
  that occurs outside GEL.

## What Labs 30 and 31 say

Lab 30: the Lab 29 section displays Android-reported observations, and a short
neutral line that buyer IMEI checks are performed outside GEL and no result is
collected.

Lab 31: retains all technician technical measurements from Labs 28–29, and states
that external IMEI results are private and are not part of the GEL service report.

There are no verdicts, alerts, scores or warnings derived from what the external
website may have returned.

## QA acceptance

| Scenario | Expected |
|---|---|
| User runs only Lab 28 | Technical evidence in Lab 30 and 31, no IMEI results |
| User runs Lab 29 | Android characteristics; instruction dialog with external link |
| User taps external-check button | System browser opens CheckMEND page without query parameters |
| User returns to GEL | No result question, no result stored |
| User closes/presses Android Back | Dialog exits cleanly, no persisted result |
| Buyer does not make a purchase | No prompt or warning in GEL |
| Upgrade from an earlier QA build | Legacy `lab29_precheck_*` keys removed on activity creation |
| Lab 30/31/PDF | No IMEI outcomes from buyer or provider; only independent technical findings |
| Android 11/16, EL/EN | Dialog readable; buttons usable; link opens externally |
| Build | Compile/JUnit plus CI privacy-contract assertions pass |

## Release gate

This is a source change, not confirmation of an installable signed APK.
Complete exact-head compilation and unit tests, Android emulator/physical-device
acceptance, and PDF export checks before merge.
