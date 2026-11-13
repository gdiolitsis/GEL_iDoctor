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
| UX-01 | Labs 1–2 | **NO user YES/NO/ALL/SOME listening popup.** Purely automatic test; retain original log/RMS/Peak/route safety. Fix microphone capture timing rather than replacing entire Lab. | #59, #60, #84, #85, #103, #106 | Lab 1 timing patch proposed (#106); physical validation pending |
| UX-02 | Lab 3 | Keep guided tone sequence; fix first-tone AudioTrack static write/state error and dialog closure; preserve audible instructions, route restoration. Prevent inadvertent landscape and loss of Labs 1–2 logs. | #67, #83, #84 | To reimplement on backup |
| UX-03 | Lab 4 | Preserve base/PRO functionality; do not advance if recording fails/cancels. Keep source-bound PCM and route safety; preserve original prompts, TTS and exit. | #71, #73, earlier #46 | To evaluate/apply |
| UX-04 | Lab 6 | Add EXIT to intro (Back already works). Fix invisible touch STOP caption, white-on-white step text; restore spoken guidance when unmuted. Keep 96-zone grid and all pattern steps. Preserve/restore temporary brightness and screen-on policies. | #47, #86–#88, #93–#94 | To evaluate/apply |
| UX-05 | Lab 7 | Add intro EXIT; leave working Back and TTS untouched; sensor result is not proof of in-call screen-off. Offer proximity independently if rotation unavailable, without false pass. | #75, #89, #95 | To evaluate/apply |
| UX-06 | Lab 8 | Add intro EXIT and functioning Back; do not trap user. Restore intro TTS. Dark frames alone must not be labeled camera-stream failure; preserve Camera2 samples. | #48, #92, #96, #98 | To evaluate/apply |
| UX-07 | Lab 8.1 | Restore intro TTS in Greek and English without altering underlying camera capability check. | #97 | To evaluate/apply |
| UX-08 | Lab 12 | Four unreadable/light popups: fix local text/background contrast; preserve original call-consent and all actions. | #50, #100 | To evaluate/apply |
| UX-09 | Lab 13 | Restore intro TTS and red SKIP/green CONTINUE; finish NOT_TESTED peripheral check neutrally, translate stray English statuses. | #51, #101, #102 | To evaluate/apply |
| UX-10 | Global button style | Red action = #E60000 / white bold / gold outline; green = #39FF14 / black bold / gold outline; ensure labels are legible at large fonts. Do not change diagnostic statuses based on button colors. | #90–#91 | To evaluate/apply; avoid broad intrusive refactor |
| UX-11 | Main toolbar/contact | Greek contact action and `ΑΠΟΣΤΟΛΗ` vs English `SEND`; correct accessibility labels. | #79 | To evaluate/apply |
| UX-12 | Labs group title | Correct Greek/English last section to Labs **25–31**, not 25–30. | #79 | To evaluate/apply |
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
