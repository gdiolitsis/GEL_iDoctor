# GEL iDoctor — Surgical restart and regression ledger (13 Nov 2026)

## Source of truth and hard rules

- **Immutable original source:** commit `69fb2ab85d0528937dc257fc19abc6722462cfc8` (8 Nov 2026).
- **Verbatim source mirror:** `qa/restart-verbatim-backup-20261113`, verified 0 differences from backup.
- **This implementation branch:** `qa/gel31-surgical-rebuild-20261113` starts directly at the same commit.
- **Historical research archive:** [PR #45–#103 descriptions, immutable source refs and links](https://github.com/gdiolitsis/GEL_iDoctor/tree/archive/pr45-103-20261113/archives/PR_45-103). Those PRs remain accessible, but their code is NOT automatically trusted or imported. The PR #61 metadata endpoint returned 404.
- **Related prior isolated proposal:** [PR #106 Lab 1 timing correction](https://github.com/gdiolitsis/GEL_iDoctor/pull/106), not a substitute for physical acceptance.

**Owner development contract:** Fix exactly the known defect in the existing laboratory; preserve all unrelated tested behavior, dialogs, buttons, style, locales, TTS, navigation, audio routing, diagnostics, measured fields, logs and PDF. Repair wrong code by replacing it with better code rather than rewriting the laboratory. Baseline line count, *including clearly mapped dedicated helper code*, must be reported before and after; aim for similar method size after replacements and growth when adding genuine functions. A decrease is a stop/review trigger, not an automatic sign of improved quality. Functionality and evidence validation take precedence over code size. Avoid cosmetic boilerplate added merely to inflate line count.

**Evidence contract:** Proven Android API observation ≠ estimate ≠ user report ≠ unknown. Do not claim calibrated hardware health, component failure, battery SoH, physical output route, speaker audibility or zero device risks from unvalidated proxies. Never hide incomplete sampling behind a green "completed" result. Do not remove scientific caveats that materially change a conclusion, even while reducing repetitive boilerplate.

**Release gates:** Work in isolated QA changes. For each patch: exact old/new diff incl. helper accounting → unit/build → Android 11 phone & Android 16 tablet emulators, Greek & English → real-device Xiaomi checks → inspect report/PDF, cancellation/Back, TTS and export → owner approval → only then consider main. No Play rollout or main merge without explicit authorization. One batch APK after coherent verified changes, not an APK for every small patch.

## Verified owner-reported problems and specifically scoped work

| ID | Scope | Required surgical repair; retain old working behavior | Historical PRs | State |
|---|---|---|---|---|
| UX-01 | Labs 1–2 | **NO user YES/NO/ALL/SOME listening popup.** Purely automatic test; retain original log/RMS/Peak/route safety. Fix microphone capture timing rather than replacing entire Lab. | #59, #60, #84, #85, #103, #106 | Lab 1 synchronized timing present; Lab 2 unrequested popup removed. Source review completed; device acoustic validation pending. |
| UX-02 | Lab 3 | Keep guided tone sequence; fix first-tone AudioTrack static write/state error and dialog closure; preserve audible instructions, route restoration. Prevent inadvertent landscape and loss of Labs 1–2 logs. | #67, #83, #84 | Source patched: AudioTrack write guard, cancel-aware three-tone workflow, double-start prevention, actual original audio mode restoration and single intro TTS. Landscape/log survival still requires physical verification. |
| UX-03 | Lab 4 | Preserve base/PRO functionality; do not advance if recording fails/cancels. Keep source-bound PCM and route safety; preserve original prompts, TTS and exit. | #71, #73, earlier #46 | Source patched: BASE requires both successful AudioRecord starts; PRO validates PCM and audio I/O, rejects timeout/cancel/zero capture; WAV failure cannot reach confirmation; localization and contrast fixed. Device-route and emulator testing pending. |
| UX-04 | Lab 6 | Add EXIT to intro (Back already works). Fix invisible touch STOP caption, white-on-white step text; restore spoken guidance when unmuted. Keep 96-zone grid and all pattern steps. Preserve/restore temporary brightness and screen-on policies. | #47, #86–#88, #93–#94 | Source patched: localized STOP, fresh counts, canceled exit, partial-display coverage, contrast/TTS. Device acceptance pending; brightness lifecycle still to verify. |
| UX-05 | Lab 7 | Add intro EXIT; leave working Back and TTS untouched; sensor result is not proof of in-call screen-off. Offer proximity independently if rotation unavailable, without false pass. | #75, #89, #95 | Source patched: stable portrait→landscape postures, FAR→NEAR→FAR, separate proximity choice, neutral cancellation and truthful logs. Hardware acceptance pending. |
| UX-06 | Lab 8 | Add intro EXIT and functioning Back; preserve Camera2/TTS. **Owner-confirmed black frames occurred because the phone was placed on a black table**; this is NOT a confirmed camera defect and does not authorize changing the camera pipeline. | #48, #92, #96, #98 | Dark-frame incident explained by test environment — **no camera fix needed for that event**. Intro EXIT/Back/TTS visible in source; device verification pending. |
| UX-07 | Lab 8.1 | Restore intro TTS in Greek and English without altering underlying camera capability check. | #97 | To evaluate/apply |
| UX-08 | Lab 12 | Four unreadable/light popups: fix local text/background contrast; preserve original call-consent and all actions. | #50, #100 | Four explicit dark GEL optional-call prompts and truth-model/JUnit source added; actual call and Back/skip emulator/real-device validation pending. |
| UX-09 | Lab 13 | Restore intro TTS and red SKIP/green CONTINUE; finish NOT_TESTED peripheral check neutrally, translate stray English statuses. | #51, #101, #102 | To evaluate/apply |
| UX-10 | Global button style | Red action = #E60000 / white bold / gold outline; green = #39FF14 / black bold / gold outline; ensure labels are legible at large fonts. Do not change diagnostic statuses based on button colors. | #90–#91 | To evaluate/apply; avoid broad intrusive refactor |
| UX-11 | Main toolbar/contact | Greek contact action and `ΑΠΟΣΤΟΛΗ` vs English `SEND`; correct accessibility labels. | #79 | Present in QA branch (`MainActivity.java`: `contactLabel`, `btnSend`); real-device/APK verification pending |
| UX-12 | Labs group title | Correct Greek/English last section to Labs **25–31**, not 25–30. | #79 | Present in QA branch (`values/strings.xml`, `values-el/strings.xml`: `manual_cat_7`); APK verification pending |
| UX-13 | All Labs | Keep logs on rotation/recreation and protect active session; no landscape induced by moving phone to ear. Keep sensor physical-rotation experiment working even if UI locked portrait. | #83 | To evaluate/apply |
| UX-14 | Reports | Preserve complete PDF and Greek evidence interpretation; audit truncation and stale/missing data. | #84, #65, #79, #102 | To evaluate/apply |

## Technical diagnostic risks, by Lab

Every row begins as **REVIEW**, not as permission to wholesale copy the historical PR. Old code and auxiliary classes must be compared method-by-method before edits.

| Lab | Investigate / target | Evidence refs |
|---|---|---|
| 1 — Speaker | Original tone ends before microphone read; smallest fix is time-align recording and playback. Preserve RMS/Peak, original GUI, output checks and no confirmation popup. Next stage: separately measured tone-specific correlation with negative controls. | #59, #84–#85, #103, #106 |
| 2 — Multi-tone | Original mic analysis runs after all 4 tones; preserve 4-tone procedure and measured logs, investigate synchronized capture with actual per-tone results, clipping/no sample states. No user listening popup. | #60, #84–#85, #103 |
| 3 — Earpiece | Recheck 8kHz AudioTrack static state/ready/writes, keep guided controls and pre-call safe routing. | #67, #83–#84 |
| 4 — Microphones | Preserve MIC/CAMCORDER and PRO guided capture; distinguish failed sampling vs observed silence, avoid global audio/BT takeover, abort stale YES on failed capture. | #46, #71, #73, #85 |
| 5 — Vibration | Retain two-stage vibration, TTS and guidance; cancel on pause, prevent late YES and distinguish user report from independent hardware result. | #69, #81 |
| 6 — Display/touch | 96-zone evidence, cancel and visual reporting, independent source/answer; UX fixes listed above. | #47, #86–#88, #93–#94 |
| 7 — Rotation/proximity | Physical gravity and FAR→NEAR→FAR, independent tests, no claimed screen-off. | #75, #89, #95 |
| 8 — Cameras | Camera2 frames/luma/timestamps from actual samples, safe cancel, dark-scene handling, EXIT/Back/TTS. | #48, #92, #96, #98 |
| 9 — Sensors | Preserve full inventory and actual 5-second samples; cancel on pause and block stale callbacks; neutral unavailable is not a sensor failure. | #74, #102 |
| 10 — Wi-Fi | Preserve actual network/DNS/TCP/PHY/RSSI; never display estimated or fabricated Mbps as measured throughput. | #63, #102 |
| 11 — Cellular | Distinguish SIM READY, service, CELLULAR default route and validated data; never infer mobile data success from Wi-Fi/VPN. Provide useful user preparation. | #49, #102 |
| 12 — Calls | Preserve legal optional consent; never claim verified call solely from network flags. Four readable prompts, correct NOT_TESTED. | #50, #100 |
| 13 — Bluetooth | Monitor real accessible link events and duration, don't disclose peripheral MAC/names in reports; preserve cancellation and intro TTS. | #51, #101–#102 |
| 14A / 14B / 14Γ — Battery | Preserve genuine voltage, current, charge and thermal samples; repair wrong units, timestamp, energy/ESR/SoH confidence. No automatic "replace battery"; recommend technician evaluation where appropriate. Preserve old stress and mode controls, don't replace them with stubs. | #45–#46, #56, #76, #82, #102 |
| 15 — Charging | Preserve full timed charge test; reject stale successful record after cancel/failed rerun; safe cancel and export recovery. | #77 |
| 16 — Thermals | Respect OS severe protection; preserve raw source, temperature/headroom and reason, not false "perfect health". | #72 |
| 17 — Automatic | Preserve all original prerequisite/follow-up guidance; no fake SoH; all domain evidence freshness and missing statuses. | #46, #66, #68, #76 |
| 18 — Storage | Never infer NAND wear/health from spare space; preserve capacity and legitimate file-integrity probes. | #45–#46 |
| 19 — RAM | Preserve actual RAM snapshot and API observations; audit original behavior before any change. | #45, #81 |
| 20 — Uptime | Preserve observed uptime/reboot history, never infer guaranteed component health. | #45, #81 |
| 21 — Biometrics | Keep actual prompt, retryable non-match and OS cancellations distinct, API-version safeguards, no arbitrary device security score. | #52 |
| 22 — Patch/Play Protect | Only OS/user-observable state, never unsupported whole-device security verdict. | #45, #81 |
| 23 — Developer options | Preserve useful checks, distinguish OS capability and security inference; review restored old method before simplification. | #45, #81 |
| 24 — Root/Verified Boot | Root indicators and OS-reported Verified Boot separate; no made-up trust percentage; unavailable is unknown. | #53 |
| 25 — Crash history | Preserve real crash/exit records and unknown outcome, no unconditional hardware health rating. | #45, #81 |
| 26 — Apps | Respect PackageManager visibility and UsageStats; don't invent CPU/battery drains or malware evidence. Preserve usable inventory. | #54 |
| 27 — Permissions | Actual visible requested/granted permissions ≠ malicious use; remove arbitrary privacy score without losing inventory. | #55 |
| 28 — Stability | Preserve real observations and time deltas, no keyword-triggered PMIC/NAND physical failure conclusion. | #45–#46, #64 |
| 29 — Authenticity | Preserve OS-exposed identity/readings; reject legacy fake instability/OEM authenticity score and stale preferences. | #70 |
| 30 — Combined | Preserve original report completeness; without adequate independent coverage, no A+/100/zero hardware risk. | #46, #62, #70, #76 |
| 31 — Final summary | Preserve complete localized technician report; summarize structured available/unknown evidence and record age honestly, no falsely green "nothing wrong". | #46, #65, #76 |

## Separate Apple-mode and cross-functional source review

- Apple panic-file ZIP multi-entry reading, eligibility, false fault interpretation, phone/tablet exact model mapping and all eight Apple diagnostic displays: PR #57, #80. These are *not* Android Lab 1 replacements and must be verified separately.
- Source research of lawful additional instruments and cross-lab measurements (vibration/accelerometer; GNSS; camera physical IDs; repeated thermal/battery experiments): PR #81. Proposals, not yet proven effects.
- PR #99 concerns a separate SOKRATES document and is deliberately excluded from the GEL application repair backlog.
- Cross-cutting release, signing, PRO access, export tests, policy, locale, AppTTS lifecycle and Android 11/16 QA: PR #58, #78–#79, #96, #102.

## Baseline file-size guard — verified 13 Nov 2026

- `app/src/main/java/com/gel/cleaner/MainActivity.java`: exact backup mirror **3,102 physical lines / 91,798 characters**; PR #107 **3,104 physical lines / 92,066 characters**; net **+2 lines / +268 characters**. This is a *whole-file* measurement, **not** a per-Lab functional parity result.
- Review of PR #107 diff: the Lab 1 change aligns `ToneGenerator` playback with `MicDiagnosticEngine.run(this)`, checks `startTone()` return value, stops playback after capture, and retains cleanup in `finally`. **Physical audio validation and full helper/method accounting remain pending.**
- Do not mark any other Lab complete from the whole-file net delta. The per-method and dedicated-helper before/after counts, negative-path checks, PDF/log output, TTS and real-device evidence remain mandatory.

### Historical initial ManualTestsActivity source-parity scan (13 Nov 2026; superseded by later patches)

- Exact backup: **33,567 physical lines / 982,195 characters**; QA branch: **33,573 physical lines / 982,548 characters**; net **+6 lines / +353 characters**.
- Source-level method extraction found **72 named `lab*` methods in both revisions**. Among those, **only `lab1SpeakerTone` differs**: **314→320 physical method lines; 9,269→9,622 characters**. The other 71 matched method bodies by size, not yet by functional/runtime behavior; this scan does not cover non-`lab*` helpers, resources, or other files.
- At **that initial SHA**, the unchanged Lab 2–31 bodies had not been repaired. **This statement is historical**: later scoped Lab 2/6/7/12 fixes changed the branch, as accounted below. Do not approve a final 31-Lab APK from an initial-only scan.

## One-change-at-a-time tracking

| Patch | Source | Change | Main method before→after | Helpers before→after | Unit/Build | Emulator EL/EN | Xiaomi | Decision |
|---|---|---|---|---|---|---|---|---|
| L1-001 | Original backup / [#106](https://github.com/gdiolitsis/GEL_iDoctor/pull/106) | Correct audio recording overlap; do NOT add popups | 317→323 inclusive method-section physical lines in #106 | MicDiagnosticEngine unchanged | #106 PRO validation passed; candidate APK workflow skipped | Pending | Pending | Candidate only |

## Never assume

- A successfully compiled APK is NOT evidence that a Lab functions correctly.
- An Android emulator does NOT emulate all OEM sensors, speaker/mic acoustics, power, and physical routes.
- Changes in a historical PR description are research proposals, not automatically approved code.
- A smaller method may reflect extracted helpers; compare dependency totals before asserting loss.
- A larger method is not proof of added capability: verify feature presence, absence of regressions, accurate reports, and real device observations.


## Continuation checkpoint — source changes through `30ee5fff6737b31b65e131c88a4f3ab11c7b2a47`

**Scope only:** original source and procedures were retained wherever practicable. No `main` merge, backup modification or Play publication. Source-built CI for the checkpoint was **in progress when this ledger was updated**; it must not be reported as completed by this note.

### Exact backup → current physical size (lines / characters)

| Source | Immutable backup | Current QA |
|---|---:|---:|
| `ManualTestsActivity.java` entire file | 33,567 / 982,195 | 33,942 / 1,004,397 |
| `TouchGridTestActivity.java` entire file | 173 / 5,396 | 189 / 6,265 |
| `DisplayProTestActivity.java` entire file | 535 / 17,106 | 559 / 18,209 |
| `RotationCheckActivity.java` entire file | 159 / 4,683 | 205 / 7,511 |
| `ProximityCheckActivity.java` entire file | 152 / 4,502 | 168 / 5,556 |

| Method in `ManualTestsActivity.java` | Immutable backup | Current QA |
|---|---:|---:|
| `lab1SpeakerTone` | 314 / 9,269 | 320 / 9,622 |
| `lab2SpeakerSweep` | 191 / 5,650 | 301 / 12,379 |
| `lab6DisplayTouch` | 134 / 3,504 | 159 / 4,428 |
| `lab7RotationAndProximityManual` | 164 / 4,915 | 176 / 5,482 |
| `lab12CallFunctionInterpretation` | 114 / 4,640 | 117 / 4,789 |
| `lab13BluetoothConnectivityCheck` | 64 / 1,717 | 64 / 1,717 |
| `lab8CameraHardwareCheck` | 261 / 9,118 | 261 / 9,118 |

**Helper accounting:** Lab 6 uses the complete `TouchGridTestActivity` and `DisplayProTestActivity` shown above; Lab 7 uses complete `RotationCheckActivity` and `ProximityCheckActivity` shown above. Lab 12 now has **additional** dialog, evidence-model and call-return helpers, rather than a replacement of the 114-line passive snapshot method. The former Lab 2 `showLab2HearingQuestion` popup helper (95 lines including spacer) was deliberately removed because the owner prohibited user confirmation in the autonomous speaker tests; the four-tone sampling remains. These are counts of source text, **not tests of scientific correctness or functionality**.

### Current surgical patch ledger

| Area | Changes | Source commits | Required negative cases |
|---|---|---|---|
| Lab 2 | Removed unintended prompt, kept simultaneous RMS capture and threshold 20 | `eea2b220` | Muted media, ambient noise, denied mic, frequency mismatch; no invented tone certainties |
| Lab 6 touch | Localized END, 96-zone grid, fresh Intent result data, canceled/Back neutral, no forced Pro start | `46f73cc1`, `613045f3`, `47087e34`, `006bcb61` | 0/96 canceled, partial cancellation, full 96/96, rapid repeat, missing/stale data, logs/PDF |
| Lab 6 PRO | Visible directions on white pattern, green/black and red/white buttons, dialog Back, coverage flag after three complete cycles | `b216dcba`, `03eb27a7`, `006bcb61` | Back on intro/final, interrupted pattern, runtime limit, inaccurate no-issue pass, light/dark and large fonts |
| Lab 7 | Stable gravity orientation, separate proximity navigation, FAR→NEAR→FAR, no fabricated screen-off verdict | `d901f10b`, `ddfb48ef`, `f8b71dd5`, `30ee5fff` | Jolt, face-up, missing rotation/gravity sensor, skip-only proximity, sensor unavailable, no in-call blanking proof |
| Lab 12 | Optional four-dialog readable call flow, explicit opt-in, NO automatic outgoing calls, standalone JUnit truth model | `f2e1d510`, `7d99cf77`, `2bdb225d` | No dialer, no real call, Back/skip, one-way audio, permission unavailable, export restored |
| GEL buttons | Shared green/black legibility, red/white for Labs 6/7/8/8.1/13 | `9c605e30` | Contrast and text clipping across Android 11/16, EL/EN, large fonts |

**Gate still open:** exact-head Gradle/JUnit build, four emulator UI sweeps, real-device Xiaomi trial, exported service PDF and full regression across all 31 Labs. Do not conflate CI success with hardware/clinical-style measurement proof.

### Source-verified Lab 3/4 continuation after black-table clarification

Latest application patch `8d966118bfdb395367a1cef88255dee3ec5b5765` is scoped to the original Lab 3/4 flow and helpers. These changes are **not** new tests of speaker/microphone hardware; actual Xiaomi and emulator validation remains mandatory.

| Method, including relevant audio helpers | Original backup lines / chars | Latest QA lines / chars |
|---|---:|---:|
| `lab3EarpieceManual` | 307 / 8,746 | 307 / 9,381 |
| `playEarpieceBeep` | 32 / 888 | 39 / 1,256 |
| `resetAudioAfterLab3` | 21 / 658 | 21 / 697 |
| `lab4MicManual` | 6 / 123 | 6 / 123 |
| `lab4MicBase` | 135 / 3,927 | 160 / 5,669 |
| `lab4MicPro` | 588 / 17,709 | 625 / 20,055 |
| `playAnswerCheckWav` | 59 / 1,698 | 68 / 2,164 |
| `showAnswerCheckConfirmation` | 161 / 4,138 | 169 / 4,571 |
| `lab4ResultEarpiece` | 37 / 1,057 | 37 / 1,057 (unchanged) |
| `lab8CameraHardwareCheck` | 261 / 9,118 | 261 / 9,118 (unchanged) |

`ManualTestsActivity.java` total: original **33,567 lines / 982,195 chars**; QA source at the above application head **34,019 lines / 1,009,695 chars**. Per-method sizes include their own brace-enclosed bodies; same file has additional helpers, so do not sum these as the whole file.

- `51b52f4d`: cancel-aware Lab 3, avoids stale confirmation and duplicate audio tasks, restores the captured pre-test mode instead of force-setting MODE_NORMAL.
- `2a123561`: Lab 4 BASE records whether each AudioRecord actually started, stops PRO progression after incomplete capture or exception, and does not claim microphone hardware certification from raw RMS alone.
- `e04f45ff`: Lab 4 PRO checks AudioRecord/AudioTrack state, requires PCM data, handles partial write/read errors, reliably releases both audio objects, times out unanswered prompts neutrally, and prevents cancel→YES.
- `8d966118`: Lab 4 sampled WAV playback failures do not prompt a misleading hearing confirmation, final dialog Back is neutral; user feedback is explicitly subjective.

**Incident closed for Lab 8:** Black frames were caused by the phone resting on a black table, confirmed by owner; **no camera pipeline patch authorized or needed for that observation**.

**Still required:** exact final SHA build/CI Android 11 (EL/EN), Android 16 (EL/EN), physical Xiaomi (Lab 3 first-tone output / no landscape / logs persistence; Lab 4 BASE no permission/null sample; PRO cancellation/timeout/no input/last WAV), report/PDF localization, no external routing regression, safe restoration of device audio policies, no main merge.


### Lab 8 — owner clarification (13 Nov 2026)

During physical review, the camera produced dark/black frames because the **phone was resting on a black table**. This environmental cause is explicitly confirmed by the owner. **Do not classify these frames as hardware failure, broken camera stream, or an unresolved user-reported defect.** No software change is required to "fix" this incident. Keep Camera2 sampling intact; separately verify the existing UI/TTS/EXIT/Back controls and avoid turning low scene brightness alone into a failing diagnosis on future runs.


## 13 Nov 2026 — APK artifact cleanup and mandatory log color policy

- **Owner rule:** no additional APK at each incremental source patch. The source-built Android UI workflow is gated off for `qa/gel31-surgical-rebuild-20261113` (`b564b610`). Re-enable only when all 31 Labs and cross-cutting UX fixes are consolidated and ready for one full APK acceptance run.
- **Actual cleanup completed:** GitHub Actions [run 43421807680](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/43421807680) deleted **18** `gel-v53-source-built-debug-apk` artifacts scoped to the current experimental QA branch. Sampled successful/aborted source-build runs were rechecked and have **zero** remaining interim APK artifacts. Emulator logs/screenshots and other artifacts remain for QA evidence. The cleanup workflow itself was removed immediately afterward. The protected known-good backup, main, and releases were not changed.
- **Never treat green CI as final APK provenance:** each candidate must have an exact accepted commit, matching source SHA/hash, full JUnit/emulator and physical-device/PDF QA plus owner signoff. Earlier green candidate builds are explicitly UNAPPROVED.
- **Mandatory for every Lab 1–31:** [Color/evidence policy and 31-Lab static severity audit](GEL31_LOG_COLOR_EVIDENCE_POLICY_20261113.md). White/gray for informational / unmeasured / skipped / canceled / unsupported; green only for a scoped verified positive; yellow only for real actionable uncertainty; red only for substantiated material failure or safety concern.
- **Source changes:** `db6c094d` changed `logLabelValue` from incorrectly green to neutral white; `4dd2915c` corrected 19 callsites in Labs 4/5/6/7/12/13 without changing the diagnostic algorithms. Remaining warning/error callsites still need per-Lab semantic review; no mass removal.
- **Acceptance still open:** no full 31-Lab color semantics review, final build or real-device acceptance has been declared. This checkpoint is source and workflow hygiene only.
