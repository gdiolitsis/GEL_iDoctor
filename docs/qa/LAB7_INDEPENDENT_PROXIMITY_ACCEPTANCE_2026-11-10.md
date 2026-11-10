# Lab 7 — Independent rotation and proximity evidence

Status: Phase A source/parity candidate; **not approved for merge, Play, or device-health certification**.

Baseline: PR #74 exact head `3ac78db9599b6ca595aace6a9c4e441ced39fa74`; research context from PR #46 and PR #45. The restored `main` and `backup/labs-pre-evidence-20261108` are not changed.

## Legacy-vs-candidate and preservation

- Retain the existing stable portrait-to-landscape timed gravity/accelerometer sequence and its sensor-source provenance; shaking is not verified rotation.
- Retain FAR -> NEAR -> FAR proximity sequence with event timestamps. Neither API result asserts a real call screen-off event.
- Preserve original guided Greek/English instructions, mute/TTS, orientation flow, the proximity activity and report export.
- Source defect: `ManualTestsActivity.onActivityResult(7007)` returned and enabled export on an unverified/cancelled orientation result, preventing the independent `8008` proximity test.
- Candidate: after a verified orientation, start proximity as before. After an unverified orientation, explain the limitation and offer **TEST PROXIMITY** or **FINISH**, without converting an aborted rotation into a physical hardware failure.
- Reset session-local orientation evidence on each new guided run. An activity recreation may conservatively lose this observation; it must not accidentally carry over a prior green result.
- The final report distinguishes BOTH_OBSERVED / ORIENTATION_ONLY / PROXIMITY_ONLY / NONE_OBSERVED. Incomplete evidence remains PARTIAL/UNVERIFIED rather than a whole-Lab success.
- Report export stays disabled while an active guided phase or the proceed/finish decision is pending, and is restored after the terminal outcome.

## Required acceptance tests

1. Both phases verified: BOTH_OBSERVED, evidence-scoped conclusion, no hardware-health certificate.
2. Orientation aborted or unavailable, choose proximity, FAR/NEAR/FAR verified: PROXIMITY_ONLY, orientation still unverified.
3. Orientation aborted, choose FINISH: no proximity claim; partial exported result.
4. Orientation verified, proximity aborted: ORIENTATION_ONLY; no overall success.
5. Both unavailable/cancelled: NONE_OBSERVED, no false FAIL diagnosis.
6. Rotation test interrupted/backgrounded or activity recreated: no stale previous-session success.
7. Greek and English: dialog text, control availability, TTS/mute and report export.
8. Real Android 11/16 phones: shaking; slow rotation; sensor missing; proximity covered/uncovered; Back and gesture navigation.
9. Check Lab 30/31 and PDF export consumer handling of PARTIAL/UNVERIFIED; avoid green device verdicts.

## Gates

- [ ] Exact head `:app:testDebugUnitTest` and `:app:assembleDebug` succeed.
- [ ] Emulator UI review API 30/36 Greek and English; screenshots and event logs.
- [ ] Physical-device QA on real hardware; actual screen blanking during a call is a separate measurement.
- [ ] Cross-lab export/consumers checked.
- [ ] Before integration: reconcile the diverged PR #69/#70/#71/#72 branches; do not assume PR #74 contains their fixes.
- [ ] Explicit owner approval for any merge into `main` or Play rollout.

No per-Lab APK or Play publication requested. Existing diagnostic functionality is not intentionally removed.
