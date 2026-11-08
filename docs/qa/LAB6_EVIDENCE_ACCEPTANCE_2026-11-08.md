# GEL iDoctor — Lab 6 display/touch evidence hardening

**Status:** implementation proposed in isolated stacked QA branch; CI and device QA **not accepted**.  
**Parent:** PR #46, base commit `363dece0aec9c9c032c3cfa1bfbdf7380dbda38e`.  
**Scope:** Lab 6 only. No manifest/permissions, main merge, release, billing or Play rollout.

## Source-confirmed defects addressed
1. `ManualTestsActivity.onActivityResult` used `TouchGridTestActivity` process-wide static counts, which can be stale across sessions or recreation.
2. A cancelled or incomplete touch grid was labeled as *unresponsive/dead digitizer zones*. Abandoning a manual test is not hardware failure evidence.
3. `RESULT_OK` on the visual activity without validated payload could be treated as no issue, and the caller could emit a green hardware-like uniformity/burn-in claim.
4. The result listed distinct burn-in, color-banding and mura symptoms from one generic YES response; none was individually established.
5. `TouchGridTestActivity` overlaid its END button on top of test cells and sampled only the primary pointer; one contact position within a cell cleared it regardless of hitting the dot.
6. Visual test timeout could advance to a subjective positive answer even if the full display sequence was not presented; pausing the visual test did not invalidate the run.

## Evidence contract
- Per-run intent carries `EXTRA_TOTAL_ZONES` and `EXTRA_REMAINING_ZONES`; full-grid observation requires `RESULT_OK`, explicit fresh fields, exactly 96 expected targets and zero remaining.
- User stop with partial counters => `USER_STOPPED_INCOMPLETE`. No dead-zone verdict. Missing/malformed/stale counters => `INCONCLUSIVE`.
- UI controls occupy a separate strip, not grid area. Individual displayed dot hits count; event handling supports all active pointers. A sampled grid tests only the visible interactive area, not the whole digitizer, ghost contacts or calibration.
- Visual result requires `RESULT_OK`, explicit user answer and `display_steps_completed=true` after all patterns. The result remains `USER_REPORTED_ISSUE` / `USER_REPORTED_NO_ISSUE` or `INCONCLUSIVE` — never an instrument-measured pixel/brightness/burn-in verdict.
- Timeout, backgrounding, cancel or missing data => `INCONCLUSIVE`; no stale YES. No overall protocol completion without both grid sampling and visual report.
- All results must appear with appropriate provenance and limitations in final exported report.

## Mandatory falsification and device test matrix

| ID | Device or simulated condition | Required result |
|---|---|---|
| D6-01 | Complete all 96 displayed dots | GRID_SAMPLED, not full-screen hardware certification |
| D6-02 | Press END with many cells untouched | USER_STOPPED_INCOMPLETE, no digitizer FAIL |
| D6-03 | Android Back / killed activity / no result payload | INCONCLUSIVE; no reused static success |
| D6-04 | RESULT_OK with 95/96, count >96 or missing field | INCONCLUSIVE |
| D6-05 | One finger and multi-pointer interactions | Actual dot locations recorded, control button never blocks bottom grid |
| D6-06 | Fast swipe across a row | Must touch displayed targets; unobserved targets remain uncleared |
| D6-07 | Rotation/recreation mid-grid, return and restart | Fresh run only; no stale previous run PASS |
| D6-08 | Finish grid, cancel visual before patterns | PARTIAL; no global screen PASS |
| D6-09 | Visual full protocol, user selects NO | USER_REPORTED_NO_ISSUE only, not OLED/burn-in proof |
| D6-10 | Visual full protocol, user selects YES | USER_REPORTED_ISSUE with no invented specific fault |
| D6-11 | RESULT_OK without display_issues or completed flag | INCONCLUSIVE |
| D6-12 | Background visual test / timed-out sequence | CANCELED/INCONCLUSIVE, no late YES |
| D6-13 | OLED and LCD; small/large; portrait/landscape; EL/EN | No inaccessible targets or layout overlap; visible warnings and controls |
| D6-14 | Gesture navigation & 3-button navigation | Grid targets remain inside touchable content; safe edge insets |
| D6-15 | Export after incomplete/full run | Matches current session, no contradictory GREEN/FAIL |

## Gates
- [ ] `:app:testDebugUnitTest` green for current branch SHA.
- [ ] `:app:assembleDebug` successful on the same SHA.
- [ ] API 30 & 36 emulator navigation, EL & EN; collect artifacts and inspect screenshots.
- [ ] Real device Android 11 and 16 on representative LCD/OLED and different OEMs; touch/visual/manual negative controls.
- [ ] Re-check cross-lab 30/31 report parsing to ensure USER_REPORTED/INCONCLUSIVE never becomes a hardware PASS.
- [ ] Owner explicitly approves any merge or Play publication.

**Nonclaim:** An ordinary Play Store app cannot infer defect-free pixel emission, color accuracy, optical uniformity or complete digitizer health from a grid and subjective answer alone.
