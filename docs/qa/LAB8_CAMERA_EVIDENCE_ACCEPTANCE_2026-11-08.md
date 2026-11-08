# GEL iDoctor — Lab 8 Camera2 evidence and falsification

**Date:** 2026-11-08 · **Status:** isolated experimental implementation; no device certification.  
**Parent branch:** Draft PR #46 head `363dece0aec9c9c032c3cfa1bfbdf7380dbda38e`.  
**Scope:** Lab 8 (Camera2) evidence truthfulness, not a redesign of photo/video features. No new permissions or Play rollout.

## Source-confirmed defects
1. The five-second Camera2 stream sample originally computed `s.verdictOk` **before** the customer was permitted to answer; the resulting early hardware verdict was not a valid end-to-end outcome.
2. A subsequent `I SEE IMAGE` directly incremented `overall.previewOkCount` **without checking the number of sampled frames**, valid Y-plane pixels, whether Camera2 had successfully started, or whether sampling had finished.
3. Camera startup errors enabled a subjective YES/NO path. Combined with (2), unavailable streams could be counted as preview success.
4. The overall `Operational` / `NOT reliable` camera-subsystem classification treated partial/inaccessible/subjective tests as definitive camera hardware verdicts.
5. `Torch toggled successfully` was labeled flash hardware OK despite only checking whether Android accepted an on/off command, not whether LED light was emitted.
6. Old preview dismissal could leave Camera2 resources or callbacks active; timeout or backgrounding needed fail-closed handling.
7. Zero dropped/black frames and missing RAW capability could yield green/no-issue and inaccurate JPEG-only labels without positive evidence.

## Implemented guardrail contract
- The stream must have accepted a repeating request, finished its bounded window, produced Camera2 frames **and at least one sampled luma plane**, then have a fresh user response before the app can emit `OBSERVED_PREVIEW_WITH_USER_CONFIRMATION`.
- If the user saw no image, report `USER_REPORTED_NO_IMAGE` and withhold physical defect attribution. No frames / no usable pixels => `INCONCLUSIVE`. Startup unavailable => `UNAVAILABLE`.
- Never count a failed start as success even if a stale dialog is clicked. Late callbacks after terminal/cancel cannot validate an old session.
- On background/Back/dismiss, close Camera2 session/device/ImageReader and withhold a hardware verdict. Camera startup has a bounded timeout.
- Summary reports scoped preview evidence, user responses, unavailable and inconclusive observations separately, not an all-device camera-health certification.
- Torch reports SDK command acceptance or unavailability; no claim that photons were measured. No fabricated lens/AF/ISP quality guarantee.

## Mandatory test cases

| ID | Condition | Expected result |
|---|---|---|
| C8-01 | Camera2 repeating request fails before stream | UNAVAILABLE, no YES success, resources released |
| C8-02 | User YES with zero frames | INCONCLUSIVE |
| C8-03 | Image callbacks but no valid Y-plane data | INCONCLUSIVE |
| C8-04 | Late YES before 5-second sample completion | Ignored |
| C8-05 | Valid frames + valid Y plane + user YES | Scoped observed preview, **not** hardware certificate |
| C8-06 | Valid frames + user NO | USER_REPORTED_NO_IMAGE, root cause unknown |
| C8-07 | User closes dialog/back while Camera2 opening | INTERRUPTED; late callbacks cannot green |
| C8-08 | App backgrounded then resumed after 5 seconds | Old YES cannot certify abandoned session |
| C8-09 | Camera2 surface unavailable, no callback | Timeout => UNAVAILABLE, no indefinite hanging |
| C8-10 | Callback arrives after timeout | Device/session/reader close, no stale report |
| C8-11 | Dark room / covered lens | Low luma not proof of defective camera |
| C8-12 | Torch permission/in-use errors, torch accepted commands | Distinguish SDK command success from emitted LED light |
| C8-13 | Front/back/OEM-virtual camera IDs + Camera2 permission denied | No fabricated missing hardware conclusion |
| C8-14 | Android 11/API30 & Android 16/API36; Greek/English; two OEMs | Guided UI and export evidence remains correct |
| C8-15 | Lab 30/31 report, repeated run, app restart, partial run | No stale CAMERA OK or wrong summary from previous session |

## Release gates
- [ ] Android/backend `testDebugUnitTest` and `assembleDebug` green on the exact current branch head.
- [ ] Source-built emulator API30/36 EL/EN navigation and view-artifact review.
- [ ] Real Android 11/16, multiple OEM front/back cameras, denied permissions, rotation and Back/background behavior.
- [ ] Export into PRO/technician summary and Labs 30/31 checked for stale inferred camera verdicts.
- [ ] Owner approval before merge or publication.

**Nonclaim:** Camera2 access and a short preview sample do not establish lens sharpness, true optical resolution, sensor authenticity, autofocus calibration, physical flash emission, actual shutter timing, or all-OEM hidden camera availability.
