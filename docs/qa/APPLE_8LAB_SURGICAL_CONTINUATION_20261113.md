# GEL iDoctor — Apple 8-Lab surgical continuation, 13 Nov 2026

**Scope:** QA-only changes in [PR #107](https://github.com/gdiolitsis/GEL_iDoctor/pull/107), branch `qa/gel31-surgical-rebuild-20261113`. Historical [Apple PR #80](https://github.com/gdiolitsis/GEL_iDoctor/pull/80) is research and prior source reference, **not** a wholesale cherry-pick. Immutable 8 Nov backup `69fb2ab85d0528937dc257fc19abc6722462cfc8` and `main` are unchanged.

**Core safety contract:** Imported iOS logs are user-selected evidence, not direct iPhone hardware inspection. Keywords, crash patterns, and model catalogues cannot establish a failed component or calibrated health score. Real capture/physical symptom correlation and complete reports are still required.

## Confirmed source changes (NOT final device acceptance)

1. **Navigation:** Apple family declaration now opens iPhone/iPad pickers via real button listeners and displays localized title; no old model picker controls removed. Commit `7c82c01`.
2. **Import rollback:** canceled or failed SAF selection, oversize file, or failed append no longer destroys prior validated panic data. Old valid session stays intact until successful replacement. Commit `60f767b`.
3. **Per-file signatures:** Added `ApplePanicEvidencePolicy` (18 JUnit test methods) and replaced global cross-file token matching with eligible block-scoped matching in `IPSPanicParser`. Commits `102acab`, `a89ecb3`, `3d19a90`.
4. **Safe ZIP ingestion:** Added bounded `ApplePanicArchiveReader` with eight existing regression cases from prior QA; handles multiple entries, unreadable/unrelated records, oversized entries including skipped ones, and combined size. Content-provider ZIP magic is recognized even without a .zip extension. Accepted panic *blocks* are counted instead of containers in the import and Labs 5/6. Commits `6db27f1`, `c9ef8b4`, `e37f745`.
5. **DEMO isolation:** DEMO runs only UI-mode, suppresses eight shared service-log sinks and restores real imported text, name, count, parsing state and append mode in `finally`, even on failure. All eight original Lab invocations retained. Commit `dbcb47d`.
6. **Modern manufacturer cards:** Source-backed nine 2025–2026 models added (16e; 17/17 Pro/17 Pro Max; Air; 17e; 18 Pro/Pro Max; Duo) while keeping all **23** existing legacy iPhone MAP keys. Exact-only lookup prevents unknown variants inheriting sibling specs. Duo's Touch ID and dual displays represented; absent measured battery/RAM values remain unknown. Main Apple picker and internal/peripheral cards now display manufacturer facts instead of Pro-suffix guesses. Commits `3601aaa`, `538e190`, `6eef49e`, `19a4375`, `8ecb130`. Three catalogue test files with 43 authored `@Test` methods copied from prior QA; these require exact-head execution.
7. **Labs 1–2:** Parser's multi-match wrapper used to leave flat `patternId/domain/cause/severity/confidence` null even when `primary` was populated. Added compatible flattening and two regression tests; original secondary matches retained. Labels distinguish rule weight and unverified root cause from physical severity. Commits `ec96dc6`, `570a7ef`, `354d46d`.
8. **Labs 3, 4, 7 and 8:** Surgical wording/status corrections preserve original classification, count, domain map, crash map, score formula (as an explicitly **uncalibrated keyword index**), human guidance, full summary and export path. No claim of physical NAND/PMIC/board diagnosis, automatic repair, measured stability or risk-free device. Lab7/8 count only eligible blocks. Commits `a2d014d`, `ecd870c`, `b9ba628`, `194ad22`.

## Exact method parity against immutable backup

Counts are **brace-delimited main method source lines / characters** (not whole Lab dependency graphs), verified by source scan after the above edits. Comparable exported helpers are shown separately below.

| Apple scope | Original lines / chars | Surgical QA lines / chars |
|---|---:|---:|
| 1 — Panic Log Analyzer | 131 / 3311 | 131 / 3673 |
| 2 — Signature Parser | 104 / 2565 | 104 / 2795 |
| 3 — Stability | 98 / 2914 | 98 / 3261 |
| 4 — Impact | 94 / 2751 | 94 / 2898 |
| 5 — Frequency | 50 / 1202 | 47 / 1269 |
| 6 — Clustering | 62 / 1373 | 52 / 1455 |
| 7 — Recurrence | 105 / 2839 | 105 / 3141 |
| 8 — Service Summary | 375 / 10572 | 362 / 11894 |
| DEMO (outside 8 Labs) | 81 / 2153 | 59 / 2599 |
| ZIP import method | 49 / 1205 | 5 / 264 |

**Whole `IPhoneLabsActivity.java`:** baseline **3052 / 88859** → QA **2971 / 92523** lines / characters. The ZIP entry method shrink is an **extraction**, not a dropped feature: old `readPanicFromZip` 49 lines, new adapter 5 lines **plus** separate ~79-line `ApplePanicArchiveReader` and eight tests. DEMO lost repeated sleeps/background-thread scaffolding but retained every Lab call, adding finally restoration and sink gates. The Apple Lab 8 from historic PR #80 had been reduced to about 53 source-section lines; this surgical branch **did not import that abbreviated method**: legacy Lab8 remains 362 main-method lines with counters, domain breakdown and evidence-scope verdict edits.

## Model provenance
- iPhone Duo: https://www.apple.com/iphone-duo/specs/ (including Touch ID in side key; dual OLED)
- iPhone 18 Pro/Max: https://www.apple.com/iphone-18-pro/specs/
- Earlier model-specific Apple manufacturer citations remain in `iPhoneSpecs.java`.
- **Reference card ≠ device measurement**. Unsupported RAM, physical battery capacity/SoH, chip failure, OEM part condition and region-level assumptions are not filled in.

## Automated evidence and release gates

- **Intermediate evidence:** GitHub `PRO access and button validation` [run 43500649782](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/43500649782) was successful at earlier DEMO head `dbcb47d` (Android/resources/unit/backend job), **before** later model catalogue and Lab1–8 source changes.
- **Latest exact-head CI:** must be inspected for Java compile, `testDebugUnitTest` and tests added above. At recording time the latest workflow is still in progress; success is not assumed.
- **Emulator:** Android 11 phone / Android 16 tablet in Greek and English, including **Apple-specific** UI navigation, model cards, long fonts, Back, PRO lock, picker cancellation, DEMO isolation, all eight Lab buttons and service-log provenance. The regular Android emulator jobs are intentionally gated/skipped during intermediate source changes.
- **Imported fixtures:** actual anonymized iOS panic .ips/.log from multiple iOS releases; one/multiple entries, ZIP bomb and limits, opaque content provider filenames, duplicate events, non-panic files, bad encoding, cancellations and crash-retry; no fabricated "distinct crashes" attribution.
- **Report/PDF:** compare every textual status and count with original input and shared GELServiceLog, including demo exclusion and no unverified hardware verdict, and verify both iPhone and iPad cards.
- **Device boundary:** no actual iPhone hardware telemetry is obtained by this Android host app. Physical-device symptoms and technician corroboration require independent real-world acceptance.
- **No final signed APK, `main` merge, or Play release without the owner's explicit approval.** One consolidated QA APK after coherent verification, never an APK per patch.

**Resume point:** Verify latest exact SHA CI and Apple-specific UI audit, review remaining renderer/provenance semantics, run real panic fixtures and PDF evidence, then advance to one full Android+Apple QA APK. Re-read this file before another session if the chat is interrupted.

## CI finding and isolated repairs — 13 Nov 2026

- GitHub [PRO/access run 43501964709](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/43501964709) compiled Java but **failed 3 of 127 unit tests**, specifically `AppleCatalogueLookupTest.rejectsUnknownPadMiniGeneration`, `AppleModelLookupEvidenceTest.mini7DoesNotInheritMini6Hardware` and `AppleModelLookupEvidenceTest.localeAgnosticInTurkish`. These are real source regressions exposed by Apple model identity tests, not false-positive tests to disable.
- Surgical fix `3482142`: replace overbroad `iPadSpecs.get` condition `m.contains("ipad mini")` with `m.equals("ipad mini 6")`, so an unrecognized "iPad mini 7" cannot inherit mini 6 physical specifications. Exact one-line diff matches historical QA's vetted fix.
- Surgical fix `c223f19`: `AppleSpecs.normalize` now uses `toLowerCase(Locale.ROOT)`, not the user's locale, avoiding Turkish-I model lookup failure. Exact one-line diff.
- New code head for verification: `c223f1955d7b4b6391854f6bab26d0a0353ada0f`; its [CI run 43502376077](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/43502376077) was **in progress when this checkpoint was written**. Do not claim all tests passed until actual conclusion inspected.
- **Do not merge to main or build a signed candidate on failures.** The intermediate candidate Android emulator workflow remains deliberately skipped until the consolidated source is acceptance-ready.


**Latest resolved CI evidence:** The exact final **application source** at SHA `c223f1955d7b4b6391854f6bab26d0a0353ada0f` passed GitHub [PRO/access run 43502376077](https://github.com/gdiolitsis/GEL_iDoctor/actions/runs/43502376077): `:app:compileDebugJavaWithJavac`, `:app:processDebugResources`, `:app:testDebugUnitTest`, and `BUILD SUCCESSFUL in 40s` (30 Gradle tasks executed). This confirms the three preceding model-regression failures were resolved in that source snapshot; no change to Java/Android production source occurred in subsequent documentation-only commits. Apple-specific Android 11/16 EL/EN UI, real anonymized .ips/ZIP import, physical symptom corroboration and PDF report consistency remain **UNVERIFIED**, as does a final signed APK.

## Final Apple source-color and UI-harness follow-up

- Surgical Lab 5/6 fix `24ec3da`: previously **a repeated category in imported logs was rendered RED**, and a singleton was rendered GREEN, implying unsupported measured fault/health status. Original crash/domain counting and parser paths are preserved, but each observed category/count is now **neutral information**. Missing multiple eligible blocks is neutral, not a hardware warning. Source-size table above refreshed against this exact QA change; runtime PDF/UI semantics still pending verification.
- Apple-only disposable-emulator script `tools/ci/apple_ui_audit.py` was added via `1f8a6f2` and included conditionally in `full_ui_audit.sh` via `4a465ab`. The workflow now sets `AUDIT_APPLE_MODE=1` via `29b0c10` **but the intermediate branch APK build gate remains OFF**. A final consolidated APK + API30/API36 EL/EN emulator run will be needed to produce screenshot/coverage proof; no such evidence has been claimed here.
- The **latest production source SHA has advanced** to `24ec3da3802e70cbff2959f157b1c68509e667ce` after previously successful `c223f19` Java tests. Therefore the old green CI does not by itself certify the latest Lab5/6 edit; inspect the new exact-head Actions run before signoff. 
