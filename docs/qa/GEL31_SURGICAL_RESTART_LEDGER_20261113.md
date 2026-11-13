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

## 13 Nov 2026 — Labs 9/10 scoped source-evidence corrections (not accepted hardware QA)

- Current QA-only source commits: `12fc9e8a` Lab 9, `b6fca092` and `fb1b4a48` Lab 10. All are unmerged; no APK build re-enabled.
- **Exact baseline→current source parity:** `ManualTestsActivity.java` 33,567 → 34,084 whole-file lines (line counts are physical and include earlier scoped patches). By text comparison of the immediate pre-Lab9 head to the current head, every byte outside the Lab9/10 source sections was preserved.
- Lab 9 full method+presence helper: **214 → 216 physical lines**, **7,822 → 8,358 characters**. Entire `TYPE_ALL` inventory, individual presence checks, metadata and name-based heuristics remain; removed only unproven green/warn/red implications. The lab still does **NOT** demonstrate dynamic 5-second sensor samples or measured sensor health, a separate pending development item.
- Lab 10 section from entry through DeepScan and estimator: **509 → 513 physical lines**; no old network probe, capability check, conditional permission branch or estimator removed. The OS-only Internet check now distinguishes `NET_CAPABILITY_INTERNET` versus `NET_CAPABILITY_VALIDATED` on the *active network*, and prints its actual transport. SpeedSim is uncalibrated heuristic data, never throughput verification. Missing speed/RSSI results in unknown rather than invented measurements. Readability/EL-EN text, raw DHCP/RSSI/SSID reporting and probing remain.
- **Source-only audit counters:** main Lab9 green 10→0, yellow 6→1, red 3→0; main Lab10 green 9→0, yellow 8→4, red 6→0. Neutral information rises appropriately; the still-actionable Wi-Fi OFF / location availability / weak radio environment / DHCP-reading caution is not suppressed wholesale.
- **Do not declare functional acceptance:** no Android CI/APK, emulator or physical Xiaomi runtime was performed after these commits. Audit next: actual Lab9 live sensor sampling vs inventory, Lab10 optional permission refusal and DeepScan thread lifecycle, report/PDF, then all remaining Labs, maintaining one final consolidated QA build.

## 13 Nov 2026 — owner-directed Lab 11 Wi-Fi preparation and GEL contrast

- Commit `1ebbae2a560eceb9fbeeafc1d80b28339aa5cef7` (isolated QA, unmerged): when Wi-Fi is the **active default route**, Lab 11 now gives an optional EL/EN dark/gold GEL preparation prompt to disable Wi-Fi temporarily and leave mobile data enabled; access Wi-Fi Settings, RECHECK or SKIP. Black text on neon green buttons, white on red button, white instructions on dark background, gold border/title; dismiss/Back is neutral NOT TESTED. TTS obeys mute; no automatic network toggle or call.
- Snapshot keeps original fields and gathers default-route transport plus OS `NET_CAPABILITY_VALIDATED` separately. A claim that cellular Internet was validated requires *both* cellular default transport and OS validation. SIM READY alone, legacy service state, or Internet via Wi-Fi are not cellular confirmation.
- Byte-for-byte scope review against immediately preceding QA blob: only TelephonySnapshot field list, snapshot network gathering and Lab 11 area changed. Original Lab11 method **221 → 194 physical source lines**; new dedicated popup helper **111 physical lines** (combined bounded section 221 → 304 lines; boundary newlines counted once). The 4 original verdict outcomes were replaced by 4 evidence-specific outcomes, but SIM state, legacy service state, packet-data state and routing reporting are retained; source parity is not yet physical acceptance.
- Negative acceptance pending: Android 11/16 EL/EN (including light/dark OEM, large font), Back, SKIP, switch Wi-Fi off/on in Settings and RECHECK, SIM missing/locked, mobile data disabled, cellular with no Internet, validated cellular, active VPN, dual SIM and PDF semantics. No intermediate APK, main merge or Play publication.

- Follow-up `5f36bde8`: RECHECK now requires an actual active `TRANSPORT_CELLULAR` default route; merely turning off Wi-Fi is insufficient. The popup's unexpected dismiss path also records neutral NOT TESTED, while explicit retry suppresses the skip log. Lab 11 helper 111→118 physical lines. Still needs runtime acceptance, not an APK per commit.

## 13 Nov 2026 — Lab 13 Bluetooth continuation and careful source parity

- QA commits `5ecf789b`, `cf82713c`, `2889b675` are still isolated under PR #107. **Old exact backup whole Lab13 section** (`lab13BluetoothConnectivityCheck` through immediately before Lab 14): **1,046 physical lines / 33,485 chars**. Immediate pre-fix QA section: **1,059 lines / 34,200 chars**. Current: **1,072 lines / 35,914 chars**. All nine original Lab13 entry/helper methods remain (5 named `lab13*`, plus gate/core/monitor/abort). No wholesale lab rewrite.
- Corrected source-level severity and evidence wording; optional absent device and user cancellation are NOT TESTED, mere hardware availability never certifies Bluetooth health. Pairing list count and profile types remain; names/MAC are not written to service log/PDF.
- 60-second test now counts actual `SystemClock.elapsedRealtime()` duration, with number of collected monitor samples recorded. At fewer than **50** sampling callbacks, no positive monitoring conclusion is emitted. It remains aggregate device/profile evidence, not proof of one exact device's stability.
- `onPause`/destroy cleanup stops Handler monitor and unregisters receiver; backgrounding is neutral NOT COMPLETED. Original EXIT button, actions, TTS, background contrast and user skip behavior preserved in source.
- **Source-only validation:** exact-scoped comparison confirms Lab13 source changed along with explicit onPause/onDestroy cleanup; no other Lab algorithm was touched in these patches. CI, Android emulators, a real paired Bluetooth device and complete report/PDF acceptance **have not been run** against these commits. No APK per incremental patch; main and protected backup not edited.
- Negative scenarios for final QA: no paired device, Bluetooth OFF, missing BLUETOOTH_CONNECT permission, initial connection, external device connects midway, disconnect/reconnect, background mid-monitor, rapid rerun, timeout/insufficient sampled coverage, duplicate events, gate Back, EXIT, TTS mute, EL/EN large text, data privacy in rendered PDF.

## 13 Nov 2026 — Lab 14 battery A/B/Γ surgical evidence checkpoint

- Isolated PR #107 QA commits: `b11cb37a` (SoH persistence/recommendation), `6e5a7205` (battery engine unit conversion), `ca10c5ba` (physical evidence caveats), `9119d5bd` (final report), `58ff5104` (no fabricated B/Γ rates/time/Ω), `1b612cbb` (missing capacity-reference assignment), `d430809d` (thermal/voltage/Ω), `24623bb8` (cell/swell evidence and downstream readers), `0a4b0125` (start temperature and capacity provenance), `fd342752` (invalid run / sag color evidence). All unmerged. No APK build re-enabled.
- **Strict source sizes:** exact Nov 8 backup Lab14-to-Lab15 section **11,627 physical lines / 299,955 chars**, QA current **11,584 lines / 303,863 chars**. Lab14A core+helpers **8,543→8,520** lines; 14B/Γ shared duration section **2,610→2,590** lines. One complete helper/feature inventory against original remains required for acceptance. `iDoctorEngine.java` 3,043→3,045 lines, 100,867→101,351 chars, with targeted unit/source semantics changed, no code reduction.
- **Evidence handling:** µAh/µA→mAh/mA conversions explicit; energy µWh not confused with charge µAh. Design capacity not equated to measured full charge. Modelled capacity, current, sag, ESR proxy, cycle readings, and Ω/SoH stress score each correctly qualified; absence/insufficient data neutral. No battery replacement instruction; technician evaluation only when actual symptoms or repeatable observations justify it. Preserve safety alerts for genuinely elevated observed temperatures.
- **Technical fixes:** the 14B/Γ `baselineMah` was initialized but never populated from the already-selected `lab14bBaselineMah`, causing false missing-capacity fallback. Connected the two with provenance. Removed invented runtime by screen-size constant in absent-capacity path. Unknown Ω stays NaN, not 1.0; low sample rates no longer clamped up as though measured. Signed voltage delta and large thermal changes remain visible.
- **Cross-lab contamination:** deletes old `lab14_health_percent` at completion, no new false SoH save; technician summary labels legacy scores historical and uncalibrated. Lab 29/30 physical swelling readers no longer consume the saved `lab14_swelling_risk` indirect flag. Lab 17/29/30 legacy battery-score logic must be revisited during their own scoped audit; no implied blanket proof of battery health.
- **Release gates remain open:** Android source compile/JUnit, 11-phone /16-tablet emulators EL/EN, high/low battery conditions, invalid charge counter or absent temperature, normal/high load, cancellation/back, brightness and CPU stress cleanup, OEM-specific unit reliability, full technician report/PDF, Xiaomi safe real-world acceptance, owner signoff. No final APK yet; immutable backup/main unaffected.

- Final source-only severity review follow-up `fd342752`: charging precondition, insufficient stress load, invalid run are not battery hardware faults; legacy red Critical-sag proxy is yellow actionable caution and green Excellent-sag proxy is neutral. Across 22 explicitly named `lab14*` methods, static severity **29/30/65/14 before → 42/20/67/7 now** (neutral/green/yellow/red source callsites, not executed logs). 31-Lab policy table now reconciled at **311/307/383/82**. No APK or emulator run.


## 13 Nov 2026 — Lab 15 charging targeted source repair

- Isolated PR #107 QA commits: `04f78f4` (rerun/cancel evidence and self-contained counter source), `52ff9c9` (active-charge timer, safe UI severity and physical interpretation), `cdee724` (real high-temperature indication without fabricated ΔT). **No merge to main; Nov 8 immutable backup untouched. No APK generated for these changes.**
- Exact original Nov 8 method-group inventory: **precheck 246 physical lines / 4,932 chars → QA 253 / 5,194; user-abort 39 / 963 → QA 40 / 1,049; full Lab15 smart 180-second method 713 / 20,285 → QA 740 / 22,902**. The original 180s observation, six progress segments, dark/gold dialog, Exit, Android TTS/mute row, cleanup and export flow remain present in source. Method signature and all three entry/helpers retained.
- Corrected lifecycle: declining or cancelling precheck no longer permanently bypasses it on the next attempt; new **accepted start** invalidates stale saved Lab15 score/flags/timestamp; user cancellation, battery precondition, battery read failure, and charging disconnection do not produce a completed healthy result. Export control is restored on failure paths without implying a successful test.
- Corrected evidence: the Lab15 rate uses two comparable `iDoctorEngine.BatterySnapshot.chargeNowMah` samples from the same declared source, captured at actual charging start and completion; no `lab14DeltaMah` reuse. Shows **net stored charge gain (mAh/min), not charger input watts**. Invalid, non-increasing or source-mismatched charge counters yield unknown, not false poor charger diagnosis or false good status. The 180s countdown and rate exclude observed disconnection intervals; >10s unplug stops the test.
- Interpretation/visuals: removed automatic single-test PMIC / USB port fault attribution. Weak net accumulation is a **qualified caution**, not a red hardware fault. Waiting and user-preparation states are neutral white; verified observations may be green. Genuine observed elevated battery temperature remains red. Missing thermal data does not generate a green normal result, `NaN°C`, or invented ΔT. Static source inventory of Lab15 smart method: potential red error-log callsites **6 before → 2 after**, not executed-log counts.
- **Static source assertions passed** for old-feature presence, saved-result invalidation, no Lab14 charge-delta mixing, precheck reset, abort semantics, unplugged time exclusion, unknown-counter treatment, and thermal ΔT evidence. This is *not* Java compilation or device acceptance.
- **Remaining acceptance gates:** Gradle compile, JUnit negative cases (cancel/precheck/back, no charger, >10s unplug/reconnect, old valid then failed rerun, invalid/mismatched counters, hot/no-temp), Android 11 phone / Android 16 tablet emulator EL/EN, physical Xiaomi 3-minute charge session and varying cable/charger, TTS/UI accessibility, report/PDF and Lab17 reader consistency. The historical Lab15 charge score and fixed net-rate bands are heuristic and must not be presented as measured charging wattage or battery health. No final APK, Play release, or main merge without owner approval.


## 13 Nov 2026 — Labs 28–31 source-only surgical continuation

**QA branch:** `qa/gel31-surgical-rebuild-20261113`. Starting point was the existing post-Lab27 head `5adc999b`. Historical protected backup and `main` remain untouched; **no APK was deliberately built, no merge/Play rollout**. This section reports source changes, NOT runtime/Gradle acceptance.

| Scope | QA commit | Before → after source section (physical lines) | Surgical result and important limitation |
|---|---|---:|---|
| Lab28 Hardware stability and its log reader | `052559f` | 645 → 638 | Kept technician popup, symptom/source fields, the old heuristic breakdown and support helpers; demoted unproven greens; stopped keyword-only PMIC/baseband/NAND/motherboard-repair/future-fault attribution; removed stale Lab14-to-PMIC inference; filtered generic and negated log mentions. **Unstructured text remains a weak triage source, NOT real multi-lab evidence, and residual heuristic/status text needs runtime review.** |
| Lab29 OEM authenticity | `4c073e4` | 320 → 302 | Retained battery level/voltage, refresh, camera, sensor inventory, Build fingerprint/board and UI. Unknown display refresh now NaN (not invented 60 Hz). Old unaudited cross-Lab numerical instability score and saved authenticity score cleared. Data are observations only; no OEM certification. |
| Lab30 combined summary | `7596efd` | 1,591 → 1,589 | Preserved the full historical score calculation, sections, source snapshots, run counter, report ID, UI and summary layout, but prevents internal 0–100 estimates from being promoted to a component-health certificate or verified-device status. Trend keys based on unsupported risk/SoH figures invalidated; raw readings displayed as neutral and legacy risk components suppressed pending typed, fresh, measured sources. No claim this redesign validates the old scoring methodology. |
| Lab31 final report | `a6cfb62` | 319 → 262 | Preserved actual `enableSingleExportButton()` and original PDF/report path; replaces naive English `warning/error` keyword mining with exact `⚠`/`✖` entries; explicitly says that other `logLabelWarnValue()` severities cannot be reconstructed from plain log text, and absent tagged alerts do NOT prove device health. Labels historical Lab29 scores unverified, imposes source/age gate on Lab14B endurance. Rewritten evidence-summary logic accounts for most line reduction; acceptance pending. |
| Lab31 alert rendering follow-up | `55651a4` | 1-line semantic correction | Explicit alert entries now append an actual line feed, so distinct messages are not collapsed into backslash-n text. |
| Compile-blocking syntax repair in Lab14 | `1cef605` | −1 line | An unrelated leftover ternary fragment after the Lab14 weak-load `logLabelValue` caused javac `illegal start of expression` at original line 19002. Removed **only** the orphaned fragment; did not change the weak-load condition, stop/cleanup or log message. This failure was observed in GitHub Actions `PRO access and button validation` run `43486868710`, not an inferred bug. |

**Verification status:** The source-level invariants checked that each change touched its intended method/reader block, with no adjacent Lab body rewrite. These checks are not Java compilation or proof of runtime correctness. At the time of the above changes, GitHub Actions for intermediate heads included a failed compile (Lab14 orphan fragment) and skipped source-built emulator runs. Newest exact-head Gradle compilation/unit tests, Android 11 phone and Android 16 tablet emulator UI in Greek and English, physical Xiaomi checks, logs→PDF consistency, TTS/Back/cancel, PRO gating and large-font/red-white/green-black popup audits **remain open**. Do not report a green build or 31-Lab release acceptance until the corresponding exact-head evidence is inspected.

**Structural follow-up:** The old plain-text GELServiceLog contains no machine-readable severity for `logLabelWarnValue()`. A separate, backward-compatible typed diagnostic event model (severity, source, lab, time, coverage, finding confidence) is recommended before interpreting whole-device risk or asserting 'no warnings'. This is a design item, not implemented by this patch.


### Continuation — Android compile green and button palette parity

- At source SHA `1cef605d6c649fd418980443ff148d9b46aada77`, GitHub Actions `PRO access and button validation` run [43487783011](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/43487783011) **succeeded**. Its job log explicitly shows `:app:compileDebugJavaWithJavac`, `:app:testDebugUnitTest`, and `BUILD SUCCESSFUL in 47s`. This covers Labs 28–31 plus the single-line Lab14 compile fix; subsequent documentation-only `fec0971` did not alter application source.
- Scoped follow-up `81fbd86`: two older Lab14 B/Γ YES/NO dialogs changed from dark red/green and white labels to vivid red `#E60000`/white and neon green `#39FF14`/black; gold outline, TTS, callbacks and test behavior untouched. Clears competing background tints and uses bold labels.
- Source-wide targeted palette follow-up `a636293`: remaining 11 usages of dark red `0xFF8B0000`, 2 of dark green `0xFF0B5F3B`, and 6 of dark green `0xFF0B5D1E` were confirmed as button backgrounds or GEL button parameters and replaced with red `0xFFE60000` and neon green `0xFF39FF14` respectively. Corresponding direct green-button labels changed to black/bold; reusable GEL button helpers clear inherited tint. No callback, diagnostic algorithm, or permission change intended.
- **Release gate remains open:** the successful Gradle/JUnit result above predates the latest two visual-only commits, so those commits need their own exact-head CI and actual Android 11/16 EL/EN screenshot inspection, large-font contrast, negative/cancellation tests, and physical Xiaomi review. No intermediate APK was manually requested, downloaded or submitted, and `main`/protected backup remain unchanged.

## Lab 16/17 cross-lab thermal warning gate (13 Nov)

**Scope and provenance:** isolated QA branch only; original 8 Nov commit `69fb2ab85d0528937dc257fc19abc6722462cfc8`, `main`, Play and signed APK unchanged. Scoped changes: `0c782213` (pure-Java evidence policy), `e13c3533` (JUnit negative tests), `1c07fa3c` (Lab16 source conclusion), `d0bbc17f` (Lab16 severe-status score cap), `6072ca3e` (Lab17 thermal-danger display and correlation gate).

- **Whole original→QA method sections:** Lab16 `lab16ThermalSnapshot` **222→278 physical lines / 7,134→10,959 chars**; Lab17 `lab17RunAuto` through following Lab18 header **952→953 physical lines / 31,823→33,266 chars**. The first count includes all previously accepted-in-source scoped Lab16 changes, not just today's +25-line intervention. Adjacent dedicated Lab16 helper block was **210→210 lines / 5,659→5,659 chars**; source preservation was explicitly checked.
- **Immediate pre-change→post-change:** Lab16 method section **253→278 lines**, plus later one-line argument swap (no line delta); Lab17 method section **946→953 lines**. Source outside these respective bounded sections remained byte-for-byte identical for each application edit. New pure Java helper `supportsScopedNormal` adds **10 lines**; JUnit test method adds **10 lines**.
- **Corrected verdict:** zero visible sensors remains neutral, except a simultaneous Android severe thermal state is warned even without temperature access; battery/peripheral-only readings without accessible CPU/GPU may not get a green status; 45–54.9°C or OS MODERATE is caution, ≥55°C or OS SEVERE is at least caution, and the independent explicit OS severe warning is preserved. These thresholds are inherited heuristic presentation limits, not calibrated component-health boundaries.
- **Lab16→17 continuity:** `thermalDanger` is passed to the prior score-cap policy so a severe OS condition cannot leave an unqualified index of 100. Lab17 renders thermal danger as a warning even when the residual score would otherwise meet Good/Excellent thresholds; the `batteryBadButThermalOk` proxy branch is blocked when Lab16 observed danger. Original snapshot collection, all log fields and stored result keys, PDF generation, navigation, TTS and other Labs were left untouched by these edits.
- **Testing/evidence:** pure Java JUnit adds missing/battery-only, NaN, >45°C, Android-limited and genuine visible-internal-temperature cases. Android Actions `PRO access and button validation` run [43490264529](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/43490264529) succeeded at the earlier `1c07fa3c` source head. **The final exact-head CI after subsequent Lab16+Lab17 edits remains to be checked**; emulator UI, actual Xiaomi thermal behavior, structured logs and exported PDF have **not** been accepted. One consolidated full QA APK at the end; never claim these edits certify battery, CPU or device health.


### Lab 17 final combined-result presentation follow-up

- Commit `0343edc`: the original `if (!overallDeviceConcern)` branch previously emitted **two green assertions**: “No critical issues detected” and “Internal chips and critical peripherals checked”. Those claims exceeded the accessible 14/15/16 observations and the uncalibrated score. Both are replaced by neutral, scoped text that does not certify device health; the old conditions, numeric algorithm, preferences, UI path, PDF collection and auxiliary methods remain untouched.
- The Lab17 bounded source section changes **953→952 physical lines** for this follow-up (exact Nov 8 backup section: **952 lines**). A size match is not by itself functional evidence; direct source-scope comparison confirmed the remainder of the file was identical for this edit.
- **Remaining in Lab17:** separately evaluate legacy heuristic health/aging causal language and possibly misleading composite reasons; do not mistake this narrow correction for acceptance of the whole method. Exact-current-head Android CI, emulator, physical device and service PDF acceptance remain open.


## Lab 18 — capacity pressure is not NAND wear (13 Nov)

- Source correction `facb1a58` remains QA-only: the legacy `detectStorageWearSignals()` helper **only** calculated `StatFs.availableBlocks / totalBlocks < 5%`. It never read NAND wear, controller faults or corruption. Renamed to `detectCriticalFreeSpacePressure()`, including the single caller and local source variable; the calculation and threshold remain identical. No device files or hidden APIs are read, even when the existing branch detects root.
- Original 8 Nov `lab18StorageSnapshot` bounded method: **332 physical lines / 9,841 chars**; current QA: **314 lines / 10,064 chars**. The dedicated storage helper section remains **22→22 physical lines / 592→611 chars**. Today's exact edit changed **0 method/helper lines**, only identifiers, comments and result labels. Whole-file scope outside Lab18 plus its storage-pressure helper unchanged.
- Source-level LCS audit against original backup: **254 unchanged**, **78 deleted and 60 added** textual lines within Lab18 (net −18). Deletions replace scientifically unsupported green claims (“NAND intact”, “controller stable”, “no filesystem/silent corruption”) and causal hardware-fault language with explicit unmeasured states. No StatFs sampling, available-space math, root conditional, original A–D numeric calculations, low-capacity prompts or completion/error handling was removed in today's patch. Reduced source size is therefore attributable to replacement of unsupported result text/boilerplate, not a missing measurement path; actual functional QA remains pending.
- The four *legacy numerical indices* still reflect combinations of free-space percentages rather than independent NAND/controller/file-integrity tests. Their labels say **free-space pressure only** and no longer describe NAND health, controller instability, filesystem corruption or silent data corruption. Separate actual file-checksum read/write probes would be needed to claim file integrity. No such new measurement has been implemented here.
- **Open gates:** exact-head Gradle/JUnit and Android 11/16 EL/EN, root/nonroot paths, 4.9%/5%/7%/12%/15% free-space boundaries, StatFs errors, export/PDF, user-facing Greek/English rendering and Xiaomi inspection. No APK creation, main merge or Play publication.


## Labs 19–27 — surgical continuation and exact-head CI (13 Nov 2026)

**Scope:** all patches below are in draft PR #107 at `qa/gel31-surgical-rebuild-20261113`. Exact original backup is still `69fb2ab85d0528937dc257fc19abc6722462cfc8`. No edits to `main`, protected backup, Play releases or Google Play Console; no intermediate QA APK generated.

### Surgical source changes in this continuation

| Lab | Scoped commits | Outcome and retained functions |
|---|---|---|
| **19 — RAM** | `f306944`, `7902c4b`, `4d88fd2`, `473e426` | `MemSnapshot` now marks missing mem/swap values as unavailable (−1), not measured zero; malformed numeric parsing also stays unknown. Only comparable `SwapTotal` and `SwapFree` can produce used-swap values; incomplete counters cannot yield a “Low” derived pressure grade. `ActivityManager.MemoryInfo`, original optional `/proc/meminfo` reads, root ZRAM/swap checks and UI remain. `RamEvidencePolicyTest` adds negative cases for missing, inconsistent, complete and genuine-zero swap counts. |
| **20 — Uptime** | no new change this continuation | Confirmed the existing current source records `elapsedRealtime()`, does not reconstruct a reboot-event history from one uptime sample and treats inaccessible `oom_kill` counts as unknown. Earlier corrections remain subject to runtime QA; no unrelated modifications. |
| **21 — Lock/Biometrics** | `6f239d9` | An unavailable/failed Keyguard read no longer means “NO PIN / no secure lock”; it is unknown, and native biometric prompting is not started on unverified state. Original biometric API checks, success/non-match/error/cancel callbacks, root infrastructure probes and user-facing path were retained. Actual live biometric/Back/lifecycle QA pending. |
| **22 — Security patch** | `ee742c6` | Patch-age decision thresholds now use actual elapsed days (≤90 and ≤180) instead of floored `days/30` display-months; avoids classifying 119 days as within 3 months. Existing Play Protect accessibility caveats and manual verification remain. |
| **23 — Developer options** | `39d5b18` | Missing `Settings.Global.ADB_ENABLED` or `DEVELOPMENT_SETTINGS_ENABLED` flags are **UNKNOWN** rather than default OFF. Port 5555 and pairing probes, guarded asynchronous scan and existing guidance remain. Readable flags are still configuration evidence, not ADB-exposure certification. |
| **24 — Root / Verified Boot** | no new change this continuation | Inspected current source: root indicators and bootloader/Verified Boot are separately reported; old fabricated security-percentage verdict remains suppressed. Original indicators and helper routines remain but require source/physical-path QA before acceptance. |
| **25 — Crash/I/O** | `33f0d1a`, `36fb5e7`, `c578607` | Original crash, ANR, DropBox coverage and app-cache 4 KB I/O test retained. A spot I/O sample now requires a full byte-for-byte readback after `FileDescriptor.sync()` before returning latency; short reads or mismatches become unknown, not success. New `StorageIoEvidencePolicyTest` checks matched, partial, corrupted, null and empty cases. This is a single app-private file round-trip, **not NAND health**. |
| **26 — Apps footprint** | `59f8b5b` | **Removed only the blocking Usage Access preflight**: PackageManager-visible app inventory/capability signals can be inspected without UsageStats permission, while foreground time remains unavailable without it. Reports package visibility limitations and unknown percentage/ranking if no user-app coverage, rather than dividing by fictitious 1 or declaring “NORMAL”. Original AppImpactEngine and all Top Apps / traffic / heuristics remain. |
| **27 — Privacy permissions** | `62a9c93` | **Removed only the blocking Usage Access preflight** for independent visible PackageManager permission enumeration. Explicitly counts visible packages including excluded system apps and user apps actually scanned. Missing/incomplete grants/visibility and empty findings do not prove a risk-free device; retained declared/granted permission scan, weighted ordering, top apps and full details. |

**Exact immutable backup vs QA source section inventory** (bounded each from its `labN` method entry to the next lab entry, thus includes intervening helpers/section headings; these are **not isolated main-method line counts**):

| Lab | Backup lines/chars | QA lines/chars |
|---|---:|---:|
| 19 | 245 / 8,801 | 259 / 9,927 |
| 20 | 208 / 7,760 | 196 / 7,778 |
| 21 | 656 / 22,817 | 612 / 22,603 |
| 22 | 262 / 7,805 | 271 / 8,788 |
| 23 | 335 / 8,590 | 284 / 9,394 |
| 24 | 634 / 17,599 | 631 / 19,039 |
| 25 | 293 / 11,874 | 302 / 12,356 |
| 26 | 415 / 13,372 | 430 / 14,501 |
| 27 | 370 / 11,693 | 330 / 11,988 |

**Dedicated helper inventory:** Lab19 `MemSnapshot/readMemSnapshotSafe/extractKb/pressureLevel/zramDependency/humanPressureLabel` remains **107→107 source lines** (missing-data semantics changed in place); Lab19 root RAM helper block **37→37 lines**, byte-identical to backup; Lab20 OOM counter helper **17→17 lines**, byte-identical. Two new small pure-Java evidence classes/tests explicitly extend functionality without replacing these helpers. The historic reductions in Labs 20/21/23/24/27 largely reflect replacing unsupported colored scores, duplicated verdict text and API claims with qualified evidence; nonetheless source size is **not** a functional acceptance test and every original button/prompt/diagnostic path still needs runtime verification.

**Source invariants:** 8/8 targeted static assertions passed at QA head `62a9c93ce8466edcf112de3a03514150c229ac4c`, checking Lab19 missing swap/parse, Lab21 Keyguard and all three biometric callbacks, Lab22 cutoff, Lab23 unknown flags, Lab25 readback, Lab26/27 no forced UsageStats gate with original inventory kept. These are source assertions, not UI/hardware validation.

**CI:** `PRO access and button validation` [run 43494206800](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/43494206800) completed **successfully** on that exact source SHA. Job log explicitly confirms `:app:compileDebugJavaWithJavac`, `:app:testDebugUnitTest`, and `BUILD SUCCESSFUL in 37s`. Earlier changed source checkpoints also passed. The source-built Android emulator workflow was intentionally **skipped**, according to the agreed one-APK-at-end rule.

**Still open:** Android 11 phone / Android 16 tablet (Greek/English), Lab21 real biometric prompt and cancellation/lifecycle, Lab19 malformed/partial OEM meminfo, Lab22 absent/future patches, Lab23 settings unreadable and async ports, Lab25 cache readback failure, Labs26/27 denied Usage Access and Android package visibility, exporter/PDF captions/log severity, Xiaomi owner acceptance, and full 31-Lab regression. A successful compile or JUnit run does not certify any of those runtime results.

## Labs 28–31 — final source-parity review (13 Nov 2026)

The QA branch remains isolated from `main` and protected 8 November backup `69fb2ab85d0528937dc257fc19abc6722462cfc8`. This is **source review, not device acceptance**. The current code keeps the original measurement paths wherever legitimate and states unknown where evidence is unavailable.

### Lab 28 — stop report-feedback false positives

- **Root cause:** `Lab28EvidenceReader.readFromGELServiceLog()` scanned plain current-process GEL log lines for English keyword mentions (for example `random reboots` or `thermal spike`). The lab itself wrote phrases such as “Random reboots or sudden resets detected”, so a subsequent run could recycle its own prior output as a fresh observed symptom.
- **Surgical fix:** new pure Java `Lab28LogEvidencePolicy.withoutLab28Reports()` removes prior Lab28 sections and its current open header before parsing. If a previous Lab28 run ended unexpectedly, the helper resumes collecting at the next recognizably labeled Lab 1–31 section. This **does not authenticate external log mentions**; the analyst output was relabeled in Greek and English as **unverified mentions**, not observed hardware failures.
- **Tests:** `Lab28LogEvidencePolicyTest` covers completed English Lab28 reports, open/current Greek sections, preserve-non-Lab28 evidence, empty input, and interrupted Lab28 followed by Lab30. Raw GELServiceLog collection, positive-vs-negative keyword filtering, `showLab28Popup()`, TTS/mute/Back/OK, thermal zone sampling, symptom tallies, technician notes and completion path were retained. Actual repeated real-device operation and PDF verification remain open.
- **Changes:** `3329ba90` new source policy; `60f4f38c` and `95fca01d` tests + corrected Java line separators; `a4381330` main-source reader/output correction; `d47751ab` + `432ca58c` interrupted-report recovery/tests; `11a5762e` bilingual unverified-symptom wording.

### Lab 29 — factual capabilities, no fake OEM authenticity

- Reviewed existing changes against backup: reported battery level/voltage, display's actual refresh rate, camera feature availability, and sensor presence are retained. The old fallback “60 Hz” without a reading is gone. OEM replacement-part authenticity is explicitly **not verifiable** through these checks.
- Stale `lab29_authenticity_score`, `lab29_instability_score`, and unverified instability flags are removed/invalidated on the Lab29 result path. The old separate private `lab29AuthenticityCheck()` source definition remains present; no separate patch or execution-path claim was made in this continuation. UI, language, logs, service report still require runtime acceptance.

### Lab 30 — no hardware-health certificate from partial indices

- Existing cross-lab heuristic accumulation and source readers remain available. A narrow source patch `9f2e323e` corrects two unsupported conclusions: lack of verified contradictions is **not** full diagnostic consistency; `gel_hw_history` now stores only a timestamp and clears legacy incomparable hardware values, so it must not claim “Hardware snapshot stored”.
- Remaining model-based projections, internal scores and legacy unused helpers are **not calibrated hardware certification**. Explicit visual/PDF audit of severity and missing evidence remains mandatory.

### Lab 31 — restored feature-parity accounting for large reduction

- Existing final summary uses a pre-report snapshot of `GELServiceLog`, preserves the original Lab14B endurance projection only with evidence freshness/known capacity, explicitly marks SoH as not measured, marks old Lab29 OEM/instability fields unverified, and preserves the original `enableSingleExportButton()` path.
- The reduction from backup is due to replacement of legacy *unproven positive/negative conclusions* (keyword-derived “no warnings”, fake green battery SoH/capacity, unverified OEM authenticity/stability) with a scoped evidence summary. **This does not itself prove GUI or report-output parity.** The remaining source recognises explicit `⚠` and `✖` lines only; many `logLabelWarnValue()` label/value events do not carry typed severity into the plain report. This is an **open report-accuracy gate**, not a completed feature; do not turn absence of such markers into “PASS”.
- As of this review no separate Lab29/31 production method was modified.

### Exact original-backup vs QA size inventory

Counts use source slices with named anchors; therefore these are *source section lines* and may include trailing comments, not necessarily just executable lines:

| Section | Backup lines / chars | QA lines / chars | Functional preservation note |
|---|---:|---:|---|
| Lab 28 **main method** | 565 / 16,738 | 544 / 17,821 | Popup, sampled thermal zones, scoring and notes retained; ambiguous labels corrected |
| Lab 28 **nested helpers** | 81 / 2,680 | 98 / 3,693 | Reader retained and augmented with filtering; new independent policy + tests are additional files |
| Lab 29 **main method** | 320 / 9,055 | 310 / 10,081 | Actual Android capabilities and output remained; false OEM claims removed |
| Lab 30 **main method** | 1,591 / 44,456 | 1,597 / 46,844 | Combined engine and read paths remain; two final claims neutralized |
| Lab 30 **auxiliary methods** | 477 / 12,798 | 486 / 13,243 | Retained; formal runtime feature coverage still required |
| Lab 31 **main method** | 386 / 11,473 | 265 / 10,481 | LCS audit: 168 common lines, 218 original removed, 97 added; removed fake health/authenticity/risk assertions, preserved log snapshot, honest endurance, and single export control |

Ten static source-preservation assertions were inspected at this stage for Lab28 log filtering/popup/thermal reader, Lab29 independent reported signals and no fabricated OEM score, Lab30 neutral coverage/snapshot, and Lab31 SoH/explicit severity/export. **Static comparison is not a substitute for regression QA.**

### Gates remaining (all 31 Labs)

1. Exact *latest production-source SHA* compile, Java unit tests, backend checks; then one consolidated source-built QA APK (not one APK per patch).
2. Android 11 phone / Android 16 tablet, EL/EN layout (no white-on-white dialogs), TTS, mute, Exit/Back/cancel and re-entry after every lab; Lab28 repeated-session test to rule out self-generated false symptoms.
3. Xiaomi physical runs and neutral/green/yellow/red evidence mapping; confirm real sensors, thermal state, optional permissions/denials, late callbacks and interrupted tests.
4. Exported service/PDF full fidelity: reported data vs estimates vs unavailable, no missed warnings because label-based severity lacks typed metadata, no absent-data “all clear”.
5. Owner review before any `main` merge, signed tester APK or Play publication. Draft PR #107 stays unmerged.

### Post-audit report-severity and repeat-summary corrections

**Critical cross-lab report accuracy defect (source fix):** formerly, `ManualTestsActivity.logLabelWarnValue/logLabelErrorValue` colored the HTML display yellow/red while the shared `logLabel` wrote an untyped `Label: Value` line to `GELServiceLog`. Thus Lab31's explicit-prefix parser could miss numerous warnings/errors in PDF/plain logs. New `GelDiagnosticSeverity.Level` and `plainLine` are used by one overloaded `logLabel` source method; warning and error wrappers now write **exactly one** `⚠ Label: Value` or `✖ Label: Value` entry. The original neutral/green label output, HTML text coloring, UI scrolling, popup navigation and raw measurement values are untouched. This explicitly closes the *missing severity marker in newly generated labeled events* at source level; historical logs from old builds remain untyped.

**Lab 31 self-feedback:** on a second summary run, old Lab31-generated warning labels can feed back into its own next `fullLog` snapshot and multiply pseudo-findings. New `Lab31ReportEvidencePolicy.withoutPriorSummaries` removes the previous Lab31 blocks (including interrupted ones until a new lab header) **before** running original evidence detection. Non-Lab31 warning/error events remain; current Lab31 already captures the pre-report snapshot. Unit tests cover ordinary warnings, repeated summaries, interrupted Greek summary followed by new Lab15, and empty input. The evidence summary language now explains typed label support and historical/missing-data limitations rather than claiming all labels are untyped.

**Commits:** `69cb5226` severity helper, `5b7b1361` unit tests, `8b1a8b2f` single-line typed log integration; `ce8acc89` Lab31 explanatory text; `8072c0e5` repeated-report filter, `d45a41cc` tests, `5007a336` Lab31 integration.

**Updated exact source section counts:** Lab28 main **565→544 lines**, nested helpers **81→98**, Lab29 main **320→310**, Lab30 main **1591→1597**, Lab30 helpers **477→486**, Lab31 main **386→267**. Standalone new policies + test sources are separately added files (not accounted for inside these section counts). Lab31 lines increased by 2 only for filtering old final summaries; there is no removed export logic.

**Static assertions:** 9/9 targeted source checks passed for typed warn/error prefixes, preserved neutral logging, one GELServiceLog write, Lab31 excluding prior reports with export retained, Lab28 self-filter, Lab29 unknown fallback, and Lab30 non-certification wording. Exact-head CI and UI/emulator/physical/PDF checks remain required. No main merge, APK signing or Play rollout has been performed.


## 13 Nov 2026 — Apple eight-Lab surgical carryover

The existing QA branch now contains independent, bounded Apple-mode source repairs and official model references, **without importing the shortened historic PR #80 Lab8 method**. Full per-method baseline→QA parity, helper extraction, commit trail, linked test evidence, and honest acceptance status are documented in [APPLE_8LAB_SURGICAL_CONTINUATION_20261113.md](APPLE_8LAB_SURGICAL_CONTINUATION_20261113.md). Apple model cards remain reference data, not measured hardware health. Intermediate CI passed after DEMO isolation; current exact-head build/emulator/real iOS fixtures and exported service report verification are still required. No signed APK, main merge or Play rollout.


### Apple 8-Lab CI recovery checkpoint — 13 Nov 2026

The Apple source-only continuation in [APPLE_8LAB_SURGICAL_CONTINUATION_20261113.md](APPLE_8LAB_SURGICAL_CONTINUATION_20261113.md) preserved all eight Lab methods, source scopes and ZIP/helper inventory. Newly imported tests detected three actual old Apple model resolver regressions (unknown iPad mini 7 incorrectly becoming mini 6; Turkish-locale iPhone identification). Both root causes were corrected by one-line surgical edits `3482142` and `c223f19`. The exact newest **application source** SHA `c223f1955d7b4b6391854f6bab26d0a0353ada0f` passed [GitHub Actions run 43502376077](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/43502376077): Java compilation and `testDebugUnitTest` succeeded. Subsequent commits only document the checkpoint. This is **source/CI acceptance only**; Apple real iOS fixture evidence, Android host emulator EL/EN UI, signed candidate APK, and PDF/Xiaomi user acceptance are still mandatory. `main`, Play and protected backup remain unchanged.


### Apple Lab5–6 semantic color + final emulator harness (13 Nov)

- Last **application-source** edit `24ec3da3802e70cbff2959f157b1c68509e667ce`: Apple Labs 5–6 still retain actual eligible panic counts and domains, but render repetition as neutral observed **imported keyword categories** rather than red hardware failure or green device health. Prior `c223f19` compiler/JUnit green does not include this newer color edit.
- The Apple-specific `tools/ci/apple_ui_audit.py` and conditional `AUDIT_APPLE_MODE=1` integration are present for the **one final source-built QA APK** and the API 30/36 EL/EN emulator matrix. The source-built branch gate is still intentionally OFF so no interim APK artifacts are produced. No Apple emulator screenshots, physical iOS file acceptance, or PDF fidelity are claimed.
- Source/helper parity, exact commits, and known CI requirements are in [APPLE_8LAB_SURGICAL_CONTINUATION_20261113.md](APPLE_8LAB_SURGICAL_CONTINUATION_20261113.md). Current Java/Android source CI [43502939219](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/43502939219) needs its actual final result checked. No merge, Play rollout or protected backup change.
