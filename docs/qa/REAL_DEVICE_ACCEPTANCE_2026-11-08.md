# GEL iDoctor v5.3 — real-device and PRO acceptance gates

**Date:** 2026-11-08 · **Owner decision:** No merge, production rollout or Play Console publication until explicit approval.

This protocol complements the automated GitHub Actions runs. It is **not** evidence that the checks have passed. Do not use the term `PASS` without saved screenshots/logs, a real device model/OS, an expected result and an independently observed result.

## Device / build coverage

| Profile | Minimum setup | What it proves |
| --- | --- | --- |
| P1 | Android 11 small-display handset; 320×640-class viewport | First-run Welcome, bottom controls, scroll reachability |
| P2 | Android 16 tablet or larger-screen Android device | Responsive layout, landscape, tablets |
| P3 | Physical Android phone with working CPU/thermal sensors and battery readings | Genuine measured diagnostics, no fabricated 100 |
| P4 | Physical Android phone lacking some sensor permissions/readings | Unknown/insufficient evidence presentation |
| P5 | Authorized professional tester account with legitimately enabled PRO/gift entitlement | PRO screens, service-report outputs; no real purchase |
| P6 | Device with TalkBack + enlarged font/display scale | Focus order, accessible names, target size and clipping |

Log app version, source commit SHA, APK SHA-256, release vs debug variant, locale, device model, Android version, display settings and permissions for **each** test session. Never substitute an earlier signed APK's evidence for current corrected source.

## Acceptance scenarios

### Initial setup and navigation
- **UI-01 — Welcome / A08:** fresh install and forced re-open; inspect both EL/EN. Title, full explanatory text, mute toggle, language picker, “Do not show again”, and **OK** remain reachable at 320×640 with large font. Scroll text; press OK; confirm modal actually disappears and correct main screen responds. Repeat landscape. **FAIL** if only the button hierarchy exists but tap has no effect.
- **UI-02 — Settings / A04:** toolbar action has localized accessible name ("Ρυθμίσεις"/"Settings") announced by TalkBack; tap opens Settings; Save persists changes. No overlay swallowing taps.
- **UI-03 — L10n / A03/A11:** final category is “Manual Labs 25–31”, Lab 31 available; Contact send is “Αποστολή” in Greek and “Send” in English. No cropped control or text.
- **UI-04 — Navigation:** all 31 Lab titles reachable, including Lab 10–31 on small phone, in both languages; screenshots of top and bottom of list. A menu opening is *navigation PASS*, not a functional Lab PASS.

### Evidence-safe diagnostics
- **DIAG-01 — Thermal / A06:** with no accessible thermal readings, Lab 16 must display INSUFFICIENT DATA/ΑΝΕΠΑΡΚΗ ΔΕΔΟΜΕΝΑ, **not** safe, 100, or full sensor coverage. With battery-only temperature, the device-wide thermal score must remain unavailable; battery temperature can be described strictly as battery reading. With an actual readable internal temperature sensor, record which sensors are present and verify displayed raw values; do not infer full coverage.
- **DIAG-02 — RAM / A01:** check actual Android lowMemory signal and available RAM against output. A free-RAM percentage does not justify measured UI lag, background app kills or “healthy performance”; no such assertions without measured events.
- **DIAG-03 — Reboot history / A05:** power on recently; Lab 20 may show uptime but must **not** claim multiple reboots. Without real recorded reboot events, history must be unavailable—not NORMAL or ABNORMAL.
- **DIAG-04 — OEM parts / A02:** record camera/display/battery/board observations. Lab 29 must not claim an OEM original/replacement origin or show numerical authenticity confidence absent independent OEM data. Lab 30 must not import any legacy authenticity score.
- **DIAG-05 — Composite scores / A09:** remove/block critical thermal and battery signals (where test environment permits). Lab 30 must mark component and total scores unknown and **must not** produce “GEL CERTIFIED” or a positive health verdict from missing evidence. Test separate valid input path; compare calculations to policy's weighted formulas.
- **DIAG-06 — Mini Check / A07:** compare the actual periodic schedule and notifications to Settings and Guided Optimizer descriptions. Do not promise 1-second checks, unimplemented cache monitoring, crash detection or continuous monitoring. On Android, record actual delayed schedule behavior.

### Account and billing (authorized test identities only)
- **PRO-01 — Async Billing / A10:** while requesting gifted access or Google sign-in, induce a Play Billing disconnect in the authorized test environment. An error notice must be separate and must not erase the result/instructions of the active action; no internal exception text displayed.
- **PRO-02 — Entitlement:** only accounts pre-authorized for professional testing. Verify locked/open LAB 28/30 screens, restore behavior where relevant, and export/report access under active and inactive entitlements. Do **not** make purchases or consume trials as part of unattended QA.
- **PRO-03 — Destructive controls:** no automated Sign out, Delete account, account unlink or subscription action. Verify visual presence only unless the tester explicitly initiates a separate approved manual case.

### Reliability / privacy
- **REL-01:** verify permission-denied, offline, app restart, background/foreground, rotation and low-memory cases. Record app-specific crash/ANR logs; do not claim “crash free” for unexercised scenarios.
- **REL-02:** inspect service reports for unverified positive diagnosis, exposed personal data, wrong timestamps/locale, or stale measurements.
- **REL-03:** verify final **release variant** build, signing, minification and symbols under a test-only path. A debug APK PASS is not equivalent to Play-ready release acceptance.

## Evidence and disposition

For each case use this row format:

| ID | Device/API/locale | Build SHA | Expected | Observed | Evidence (screenshot + log) | Status |
| --- | --- | --- | --- | --- | --- | --- |
| UI-01 | _fill_ | _fill_ | OK visible and dismisses | _fill_ | _link_ | NOT RUN |

Allowed statuses: `NOT RUN`, `PASS`, `FAIL`, `BLOCKED—NO HARDWARE`, `BLOCKED—NO AUTHORIZED PRO`. **Unobserved = NOT RUN; blocked ≠ passed.**

## Release decision rules

1. Android 11/16 × Greek/English **source-built candidate** CI complete with no unresolved crash and all mandatory Welcome/Settings gates passing.
2. No open severity-high diagnostic false-positive or dangerous behavior; regressions explicitly exercised on matching release candidate.
3. Hardware-dependent cases either PASSED on real devices or marked blocked and removed from unsupported product promises, with user sign-off for any residual limitation.
4. Authorized PRO flow tested without destructive actions or financial transactions.
5. Final versioned, signed release build audited and Play Console compliance separately reviewed.
6. Owner grants **explicit permission** before merge to `main`, Play submission, or rollout.

**Related:** [Recovered findings](AUDIT_2026-11-08.md) · [CI continuation](AUDIT_CONTINUATION_2026-11-08.md) · [Draft integration PR #44](https://github.com/gdiolitsis/GEL_iDoctor/pull/44).
