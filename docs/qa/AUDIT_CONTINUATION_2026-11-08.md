# GEL iDoctor v5.3 — QA continuation and release gates (2026-11-08)

**Scope:** Android 11 small phone (API 30), Android 16 larger-screen emulator (API 36), Greek and English. This is an evidence-first QA status, **not** a release certificate.

## Recovery and issue disposition

Original findings are preserved in [AUDIT_2026-11-08.md](AUDIT_2026-11-08.md). All ten have source-level candidate changes:

| ID | Symptom / risk | Candidate fix | Status |
| --- | --- | --- | --- |
| A01 | RAM free percentage asserted UI lag / app kills | #38, Lab 19 | Unit/build CI passed; source-built UI pending |
| A02 | OEM authenticity score derived from ordinary Android readings | #42, Labs 29/30 | Unit/build CI passed; source-built UI pending |
| A03 | Category label 25–30 omitted Lab 31 | #33 | Unit/build CI passed; source-built UI pending |
| A04 | Settings toolbar icon lacked accessible title | #40 | Unit/build CI passed; TalkBack/UI pending |
| A05 | Lab 20 reported NORMAL or repeated reboots without history | #36 | Unit/build CI passed; source-built UI pending |
| A06 | Lab 16 claimed safe temps even with no readings | #37 | Unit/build CI passed; source-built UI pending |
| A07 | Mini Check advertised unsupported cache/instability capabilities | #43 | Unit/build CI passed; source-built UI pending |
| A08 | Welcome OK clipped on 320×640 Android 11 | #34 | Unit/build CI passed; **small-screen screenshot proof pending** |
| A09 | Lab 30 calculated health / GEL CERTIFIED from missing measures | #39 | Unit/build CI passed; source-built UI pending |
| A10 | Asynchronous Billing error displaced Google/gift account guidance | #41 | Unit/build CI passed; account interaction proof pending |

Additional:
- #32: Android 11 nested ScrollView gesture fix and fail-closed missing-lab gate — **31/31 Labs navigation restored and recorded** on all four frozen signed-APK runs.
- #35: fail-closed WebRTC asset download alternative retaining pinned SHA-256 and Build IDs — CI passed.
- #44: combined integration **draft** based on #35. Unit/build CI passed.
- None of these PRs has been approved for release; no Play Console changes or rollout.

## Evidence obtained

### Frozen signed APK (build 3803; NOT the repaired source)

GitHub Actions [run 42473401531](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/42473401531):
- Android 11 / Greek: 31/31 labs found (14 completion text observed, 10 opened for further review, 5 permission/settings gates, 2 account gates).
- Android 11 / English: 31/31 labs found (13 completion text observed, 12 opened for review, 6 permission/settings gates).
- Android 16 / Greek: 31/31 labs found (15 completion text observed, 9 opened for review, 5 permission/settings gates, 2 account gates).
- Android 16 / English: 31/31 labs found (14 completion text observed, 11 opened for review, 6 permission/settings gates).

No app FATAL EXCEPTION was observed in the saved logcats for these four runs; this says nothing about untested paths. **Completion text is not equivalent to validated hardware correctness.**

The frozen Android 11 screenshots independently confirm that the Welcome dialog's OK button was not reachable in its old layout. That is baseline evidence, not proof that #34 fixed it.

### Combined repaired source

[Source-built candidate QA run 42475122493](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/42475122493):
- Fresh debug APK built from integration candidate source.
- `./gradlew :app:testDebugUnitTest :app:assembleDebug` **BUILD SUCCESSFUL**, including unit tests.
- APK and SHA-256/SOURCE_COMMIT evidence saved as `gel-v53-source-built-debug-apk`.
- Emulator jobs for 30/el, 30/en, 36/el, 36/en were **in progress at last check**. Do not claim UI PASS until artifacts are complete.

[Integration workflow test run 42475122506](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/42475122506) completed successfully.

## Remaining release gates (mandatory)

1. Inspect the **fresh source-built** APK emulator results, `coverage.json`, screenshots and logcat in all four environments (not just frozen APK).
2. Verify Android 11 Welcome has a visible and clickable OK with the complete scrollable language/mute/checkbox content, including Greek, English, landscape, enlarged font where possible.
3. Verify Labs 16/19/20/29/30 produce **unknown / insufficient data** instead of fabricated safe, health=100 or OEM-authenticity verdicts when readings/history are absent.
4. Verify locale correctness and Settings accessible name on Android 11 and Android 16.
5. Verify Gift/Google/PRO/Billing message sequencing without transactions, buying access or deleting accounts; use test accounts only for deeper flows.
6. For permission-dependent, hardware-dependent, account-dependent and professional diagnostic flows, do not mark PASS based on opening a screen. Record explicit unverified scope and required real-device testing.
7. Check app's actual release-variant assembly/signing/proguard behavior on an isolated QA track; debug build alone is insufficient for Play release readiness.
8. Review #35 dependency chain, integrate with deterministic commits and run final regression matrix only after corrected source is unified.
9. **Do not merge to main, publish to Play Store or perform a rollout without explicit user authorization.**

**Artifact retention:** GitHub Actions artifacts in the QA workflows are retained for 14 days. Preserve selected screenshots and result summaries in repository documentation before expiry.

**QA vocabulary:** Build CI PASS = source compiles/tests pass. UI navigation PASS = expected UI found. Hardware functional PASS = an independently verified measurement on supported hardware. These are distinct acceptance gates.
