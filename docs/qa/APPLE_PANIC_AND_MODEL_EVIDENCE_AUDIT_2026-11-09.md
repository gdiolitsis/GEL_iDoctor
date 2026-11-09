# iPhone and iPad — pre-APK panic evidence and model catalogue audit (2026-11-09)

This review is an isolated **Draft QA proposal**, not complete acceptance. The Android application can analyze user-imported Apple logs; it cannot directly probe the iPhone hardware, certify a repair, or calculate total device health.

## Confirmed source findings and corrective scope
- `IPSPanicParser` loaded JSON patterns marked HIGH/CRITICAL and read the static "cause" and mandatory repair recommendations as if calibrated. They were AND-matched substrings, without specificity, version evidence, controlled validation or technician confirmation. New output explicitly says *unverified pattern hypothesis*, no automated replacement orders, and no false severity confidence.
- `IPhoneLabsActivity` used a heuristic 0–100 stability score and multi-file "high hardware risk" guesses. Lab 8 now reports valid imported log counts, per-domain hints, quality limits and clinician/technician correlation with **no numeric device health score**.
- Plaintext parser accepted arbitrary `thermal`, `jetsam` and `incident` as valid panic. Explicit `panicString`, kernel panic signature or bug_type 210 are required by a conservative gate. Other iOS telemetry may be separately supported later, not silently treated as panic.
- ZIP import used to close the active stream after the first entry, preventing reliable multi-entry scans. New reader is non-closing per entry, bounded per-entry, total output and number of scanned entries; ZIP header is also detected independently of provider filename.
- iPhone model resolver previously matched substrings; `iPhone 16e` could inherit the iPhone 16's wrong chipset, screen, battery and cameras. The resolver now uses exact model keys. Unverified/unsupported variants resolve as **Unknown**, never to another model's specs.
- The visible model picker includes Apple-listed newer generations; model selection does **not** imply full specs are verified. Unknown catalogue entries must show an explicit limitation. Apple catalogue references: https://www.apple.com/iphone/compare/ and https://support.apple.com/el-gr/108044 .
- Updated misleading generic statement that all iPhone/iPad models expose equivalent panic evidence: format, content and signatures vary by iOS and hardware.

## Physical and file-based release gates (NOT executed)
1. Fresh Android 11/API30 and Android 16/API36 installs, Greek and English.
2. Select every menu model; verify only a matching spec is displayed, or **unknown** (no fallback to other SKU).
3. Verify iPhone 16, 16 Plus, 16 Pro, 16 Pro Max, 16e; older 11–15 model variants; unknown 17/18 until verified details are entered.
4. A real panic-full .ips, kernel `panicString`, bug_type 210, older plain .log, and unsupported Jetsam / unrelated log.
5. ZIP containing two valid files plus a benign text file; ZIP name without filename extension; ZIP with directory entries, entry-count cap, zip-bomb size cap and unreadable archive; cancellation and content URI errors.
6. Multiple duplicates and different devices must not be treated as proof of recurrence on the same handset.
7. Actual dates, iOS model IDs, block boundaries and provenance, including imported unknown/unsupported files.
8. Full 8-Lab flow: no fabricated "HIGH confidence of faulty NAND/RAM/PMIC", no obligatory replacement or uncalibrated 100/100 stability, and no claims of tested Apple hardware.
9. Demo must stay visibly labeled demo and never leak as customer evidence or PDF.
10. Service report/PDF and GEL PRO access: no stale result promotion, no silent permission bypass, privacy-safe handling of incident/device identifiers.
11. Exact-HEAD Gradle test/build and emulator UI matrix, followed by user-led real test on imported panic logs.

## Review limitations
Data-dependent, device-specific Apple repair correlations require curated manufacturer/technician-validated cases; this change does not assert a medically or technically validated false-positive rate. Older hard-coded model hardware specifications still require independent model-by-model primary-source verification.
No production merge, main, Play release, signing credentials, permission changes or protected backup changed.
