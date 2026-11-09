# Lab 14A — Customer-facing voltage evidence and repair guidance

**Draft correction: unvalidated until exact-HEAD build, source-built UI, PDF and physical device gates pass.** Date 2026-11-09.

## Trigger: historical 8 Nov service report (previous APK)
The uploaded GEL Service Report included a single stress run with OS-reported start voltage 3.926 V, minimum under load 3.759 V and computed sag 0.167 V, recovery 0.033 V, plus a reported 7.0°C thermal rise. The old UI labeled the observed delta "Critical" and recommended "immediate technician inspection or battery replacement"; used an **invented 2.0 A current denominator** for ~84 mΩ, inferred cell integrity and balance, and called "voltage collapse HIGH RISK" based on heuristics. One run cannot establish cell aging, tested true full-charge capacity (SoH), physical swelling or a replacement decision.

**Operator intent:** preserve actual observed signals, encourage a technician inspection and repeated comparable measurements, but **never ask the customer to replace a battery merely on the basis of a short app stress test**. Do not discard a defensible abnormal observation or say the battery is proven healthy.

## Implemented source changes in this PR
- Pure Java `Lab14CustomerGuidance`: bounded observed sag, explicit unavailable, bilingual recommendations, no automatic replacement proposal and no unsupported claim of measured capacity loss.
- Lab14 stress and quick output retains numeric `V` observation but no uncalibrated `Critical/Weak/Excellent` *battery health* label attached to it; elevated observation remains a visible warning.
- Replace "immediate technician service or replacement" with repeat under comparable conditions plus **technician assessment**; distinguish from actual full-capacity confirmation.
- The final state is **observed voltage response** or **insufficient evidence**. Old internal heuristic labels remain internal pending separate classifier redesign and must not be used as equipment certificate.
- Remove fake fallback `R = voltageSag/2.0A` when synchronized current difference is missing; do not show mΩ "estimated" from this placeholder.
- Cell structural integrity/balance are explicitly **unverified** by ordinary Android; physical/OEM evidence required. Collapse-related signals trigger follow-up rather than independent failure-risk certification.
- No forced charge/discharge cycle recommendation or fabricated capacity score in customer-facing Lab14A advice.
- No modification to main, manifest, permissions, billing, PRO settings, Play, or other lab PRs.

## Pure-Java tests: 17 authored, not yet executed
`Lab14CustomerGuidanceTest`: measured 167 mV; proper units; NaN/unavailable inputs; unrealistic/reverse values; observed-vs-certified labels; system-limited; no observation; Greek and English technician guidance; no replacement language across all state combinations; no claim of confirmed capacity loss; infinity rejection; unavailable cell condition; no auto replacement gate.

## End-to-end falsification plan — 16 scenarios, NOT executed
| # | Case | Required customer-visible behavior |
|---|---|---|
| 1 | Reproduce historical 3.926 → 3.759 V | Displays 0.167 V observed; no `Critical battery` |
| 2 | 0.167 V with an otherwise normally functioning phone | Technician/retest, not replacement |
| 3 | One run only | No confirmed long-term capacity loss or SoH |
| 4 | Repeated matched sag readings | Show measurements, do not infer SoH without capacity evidence |
| 5 | Missing instantaneous current | No fake 84 mΩ from fixed 2 A |
| 6 | Valid synchronized current + voltage | Display scoped dynamic R only when current-step validation passes |
| 7 | Charging source changes mid-run | Invalidate interpretation |
| 8 | OS limiter or thermal stop | INSUFFICIENT EVIDENCE, not hardware FAIL |
| 9 | NaN or unsupported voltage | No diagnostic claim |
| 10 | Android 11/API30 Greek UI/PDF | Consistent advisory and units |
| 11 | Android 11/API30 English UI/PDF | Consistent advisory and units |
| 12 | Android 16/API36 Greek UI/PDF | Consistent advisory and units |
| 13 | Android 16/API36 English UI/PDF | Consistent advisory and units |
| 14 | Strong load response, no cell-level sensors | Cell balance/structural claims remain unverified |
| 15 | Cancel/background/repeat/reboot | No stale prior measurement promoted as fresh |
| 16 | Labs17/30/31 and PDF migration | No conflicting `Critical`, `100/100`, `no data` or device certification from the same partial evidence |

## Safety and review requirements
- A user reporting actual swelling, smoke, persistent excessive heat, leaks or unexpected shutdowns needs **prompt qualified evaluation and safe-handling guidance**, separate from a software-only sag classification. Do not suppress independently confirmed severe safety problems.
- A technician can confirm actual capacity only via appropriate OEM/gauge data or characterized controlled capacity testing; current SOC and charge-counter snapshots are not enough.
- No high-stress repeat demand when thermal limits or operator discomfort exists. Tests must be bounded and stoppable.
- Upstream full source review must ensure no legacy customer strings elsewhere still call replacement; derived Labs 17/30/31 and PDF acceptance is mandatory before merge.

## Acceptance checklist
- [ ] Latest exact HEAD `:app:testDebugUnitTest` and `:app:assembleDebug`
- [ ] PRO/backend and source-built UI QA Android11/16 EL/EN
- [ ] Real-device controlled trials and non-invasive safety review
- [ ] End-to-end report and migration (Labs17/30/31) and wording scan in both languages
- [ ] Explicit owner approval before merge or Play rollout

Reference: https://developer.android.com/reference/android/os/BatteryManager
