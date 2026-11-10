# GEL iDoctor — 31 Turbo Labs: final APK and main promotion gates

Date: 2026-11-10. Status: **QA candidate only**; this document is not a certification.

## Source baseline

- Last integrated 31-Lab source-built QA APK: commit `43e6ed7da368cb45ac30d6c78fb121125dda7b3f`, draft PR #78.
- Integrated historical corrections: PRs #45–65 as applicable, and reprocessed QA changes including #69–77; PR #66 is alternative work and should not be blindly merged on top of PR #68.
- Protected `main` and `backup/labs-pre-evidence-20261108`: not touched by this localization PR.

## Additional source-confirmed Greek UI defects closed here

1. Home toolbar Contact menu icon was hard-coded to English `Contact`, with no localized content description; it is now `Επικοινωνία` / `Contact` with matching accessibility label.
2. Contact dialog SEND was mixed case `Αποστολή` despite requested uppercase style; now `ΑΠΟΣΤΟΛΗ` / `SEND`. This does not trigger a send, it only displays the control.
3. No-installed-email-client error was always English; now has Greek and English messages.
4. Greek group labels contained English `Manual Labs`, and group seven `Advanced / Logs`; all seven headings now use `Εργαστήρια`, with group seven `Προχωρημένα / Καταγραφές — Εργαστήρια 25–31`. The English resources keep `Manual Labs`.
5. The old audit accepted *both* 25–30 and 25–31, allowing a wrong final heading to be green. Now section selectors require 25–31 and recognize either language. Regression tests verify all seven source labels and localized toolbar text; on-device UI audit verifies `Επικοινωνία` and `ΑΠΟΣΤΟΛΗ` / `Contact` and `SEND` without tapping send.

## Promotion decision requirements

- [ ] Exact candidate commit: `:app:testDebugUnitTest` and `:app:assembleDebug` GREEN. Preserve APK/source SHA-256 and provenance.
- [ ] Four UI QA jobs: Android 11/API30 (el/en), Android 16/API36 (el/en) complete and pass. Review screenshot, accessibility, coverage.json and logcat; an emulator run marked "success" is **not** a proof of physical diagnostic correctness.
- [ ] All 31 labs demonstrably reachable with visible sections, buttons, dialogs, cancel/finish path, logs/export, Greek/English; incomplete/locked flows clearly reported. Re-run tests for any discovered regressions and do not silently waive missing sections.
- [ ] Differential legacy-vs-new behavior review of 31 labs, including helper dependencies, restoring genuine lost measurements, buttons, prompts and reports; intentional removal of fabricated scores must be documented.
- [ ] Cross-lab 14A/14B/15/17/29/30/31 and PDF: measured vs estimated vs unknown; no advice to replace a battery based on uncorroborated voltage sag or incomplete diagnostic evidence.
- [ ] Confirm diverged PR #69–72 are integrated and no additional isolated code fixes are lost.
- [ ] Review signing/version code/application ID, install vs upgrade semantics (Play-signed v51 cannot necessarily be upgraded in-place with debug APK). Preserve user data.
- [ ] Human acceptance of residual device-dependent tests. Real microphone, screen, charging, haptics, proximity and camera require hardware; an emulator cannot prove their physical health.
- [ ] Only **after all applicable gates**: promote the verified candidate to `main` with traceable integration, then build a fresh APK from the **exact new main SHA** and check its checksum and package identity. No Play rollout without a distinct explicit release decision.

## Anti-overclaim policy

A GitHub green compilation proves source compatibility and tests that actually ran, **not** that all 31 labs are bug-free or every real device passes. A previous build's artifact does not retroactively contain later changes. PR #78 APK must not be described as containing this localization PR until a new build completes.
