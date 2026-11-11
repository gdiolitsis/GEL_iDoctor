# GEL iDoctor — Apple mode: modern iPhone catalogue and eight-lab truthfulness gate
Date: 2026-11-11
Branch: `qa/integration-apple-modern-models-20261111`
Parent: `qa/integration-gel31-localization-20261110` at `484eb591bd2512bf101a3f950fd1852318c66d0c`.
Status: **source-level QA change only, not release acceptance.** Main and Play Store remain unchanged.

## What is grounded in official Apple source
Apple's current announced families include iPhone Duo (foldable), iPhone 18 Pro/Pro Max (no unsupported base iPhone 18), iPhone 17, 17 Pro/Pro Max, iPhone Air, iPhone 17e and iPhone 16e. These are nine separate additions to existing iPhone 11–16 legacy reference cards.

| Model | Year | Apple chip | Display | Distinguishing evidence | Manufacturer source |
|---|---|---|---|---|---|
| iPhone 16e | 2025 | A18 / 4-core GPU | 6.1-inch OLED | One physical 48MP rear camera, not a 16 base or dedicated zoom module | https://support.apple.com/en-us/122208 |
| iPhone 17 | 2025 | A19 / 5-core GPU | 6.3-inch OLED, ProMotion | Dual 48MP Fusion cameras | https://support.apple.com/el-gr/125089 |
| iPhone 17 Pro | 2025 | A19 Pro / 6-core GPU | 6.3-inch OLED, ProMotion | Three 48MP Fusion rear cameras | https://support.apple.com/el-gr/125090 |
| iPhone 17 Pro Max | 2025 | A19 Pro / 6-core GPU | 6.9-inch OLED, ProMotion | 2TB option, separate from Pro chassis; same published GPU core count | https://support.apple.com/el-gr/125091 |
| iPhone Air | 2025 | A19 Pro / 5-core GPU | 6.5-inch OLED, ProMotion | One rear 48MP camera, eSIM-only, C1 modem | https://www.apple.com/iphone-air/specs/ |
| iPhone 17e | 2026 | A19 / 4-core GPU | 6.1-inch OLED | One 48MP rear camera; C1X, not a base 17 | https://support.apple.com/el-gr/126470 |
| iPhone 18 Pro | 2026 | A20 Pro / 7-core GPU | 6.3-inch OLED, ProMotion | Variable-aperture 48MP main, separate 48MP telephoto | https://www.apple.com/iphone-18-pro/specs/ |
| iPhone 18 Pro Max | 2026 | A20 Pro / 7-core GPU | 6.9-inch OLED, ProMotion | Same published GPU core count as 18 Pro, bigger display | https://www.apple.com/iphone-18-pro/specs/ |
| iPhone Duo | 2026 | A20 Pro / 7-core GPU | Foldable 7.6-inch inner + 5.4-inch outer OLED | **Touch ID in side button**, no Face ID; two selfie positions | https://www.apple.com/iphone-duo/specs/ |

Source for first/second-generation UWB generations and regional limitations: https://support.apple.com/en-us/109512. Legacy 11–14 cards retain a verified first-gen reference, legacy 15–16 cards retain a second-gen reference, excluding the 16e. Apple model references are *not* hardware measurements.

## Explicitly unknown
- Real installed physical battery capacity (mAh), nominal voltage and battery-health percentage on the new models: **not invented**. Do not derive capacity from Apple video-playback tests or sibling devices.
- RAM on the nine new models, internal `iPhoneXX,Y` device identifiers, some region-specific SIM/radio model numbers: **not inferred**.
- Region/carrier-specific bands and available UWB features need individual-device/region confirmation.
- Exact physical fault, which chip requires repair, or health of a specific iPhone: **not derivable from selecting a catalogue model**.

## Eight Apple labs — evidence boundary review
1. **Panic Log Analyzer**: rule-based imported text, only a keyword *hypothesis* after conservative panic gate. Does not produce a hardware diagnosis.
2. **Signature Parser**: extracts observed tokens and domains; confidence not calibrated.
3. **Stability**: replaced legacy dead high/medium-confidence risk branches with an explicit **unmeasured stability** explanation. Crash type remains visible.
4. **Impact**: conditional thermal/load wording now explicitly requires corroboration, never a confirmed component defect.
5. **Frequency**: counts eligible imported panic blocks only; cannot prove independent events on the same device.
6. **Clustering**: domain labels are keyword groups, not identified failed chipsets.
7. **Recurrence**: now explicitly states a repeated imported keyword is **not** a proven recurring fault on one verified device.
8. **Service summary**: reports supported imported files and keyword hints; replaces the `/100` device-health label with physical health **not measured**.

## Source parity / regression guards
- Existing `IPSPanicParser` and Apple evidence policy retained; no broad deletion of parsing or import logic.
- Existing ZIP read with bounded per-entry, combined and entry-count limits retained.
- Existing eight lab buttons, PRO gating, demo controls, export/report entry points retained; edited only specific evidence wording in Labs 3/4/7/8.
- `AppleDeviceInfoInternalActivity` no longer displays speculative CPU/GPU/RAM tier or thermal advantage from a name suffix.
- `AppleDeviceInfoPeripheralsActivity` no longer fabricates ProMotion, telephoto, speaker tuning, thermal or UWB fields from the Pro suffix; source-backed UWB remains on legacy and modern models.
- `ModernAppleModelTruthTest` and existing catalogue/identity JUnit tests validate 9 new SKUs, Duo Touch ID, same-gen GPU core equality, unverified battery and RAM, rejection of invented iPhone 18 and 17 Plus.
- Old `iPhone 16e` or `iPhone 18 Pro` tests that asserted `unknown` are updated because the cards now have verifiable manufacturer-level specs.

## Acceptance still REQUIRED before promoting to main
- [ ] Exact-head Java compilation and `:app:testDebugUnitTest`, inspect test count and failures.
- [ ] Four emulator QA runs: Android 11/16, Greek/English, and review their screenshot/coverage data.
- [ ] Visually open all nine modern device reference cards in Android host app, inspect both Internal and Peripherals panels at different font scales.
- [ ] Exercise all eight Apple labs with real anonymized iOS .ips fixtures from multiple iOS versions; unknown panic signatures must be marked inconclusive.
- [ ] Test ZIP with two genuine .ips, non-panic logs, oversized and entry-count limits, cancel/error/stale result paths.
- [ ] Verify model-specific iPhone 18 Pro and Duo exports/PDF: no health scores, unsupported chip replacement advice or accidental logs exposure.
- [ ] Compare historical source/code paths, including pre-existing iPhone/iPad cards, to ensure no regressions.
- [ ] Human acceptance, then approved promotion to main; separately fix Google OAuth certificate registration for final signed APK.

**No real iPhone or iPad hardware was instrumented by this Android-host QA change.** Imported logs are observations and depend on schema and completeness, not calibrated electrical tests.


## Exact source-method parity check against PR #79 QA base (2026-11-11)

The values below are **method characters**, including braces and whitespace, not full functional bytecode. A shorter method is not automatically loss of functionality.

| Method (IPhoneLabsActivity) | Before | After | Reason |
|---|---:|---:|---|
| runAllAppleDiagnostics | 1,130 | 1,130 | Unchanged |
| runPanicLogAnalyzer / Lab 1 | 3,439 | 3,439 | Unchanged |
| runPanicSignatureParser / Lab 2 | 2,565 | 2,565 | Unchanged |
| runStabilityLab / Lab 3 | 2,932 | 1,485 | Replaced dead HIGH/MED confidence branches; all imported patterns are UNVERIFIED under existing policy; crash classification and limitations retained |
| runImpactLab / Lab 4 | 2,776 | 2,850 | Clarifies lack of physical proof |
| runPanicFrequencyLab / Lab 5 | 1,232 | 1,232 | Unchanged |
| runPanicClusteringLab / Lab 6 | 1,402 | 1,402 | Unchanged |
| runRecurringDomainLab / Lab 7 | 2,892 | 2,974 | Repetition != physical recurrence |
| runFinalServiceRecommendationLab / Lab 8 | 2,208 | 2,259 | No numeric physical-health claim |
| runDemoDiagnostics | 2,153 | 2,599 | Explicit UI-only results, snapshot/restore of real import, report sinks guarded |
| onActivityResult | 4,938 | 5,245 | Cancel/error rollback, exact supported-entry count |
| openPanicLogPicker | 586 | 586 | Unchanged SAF selection mechanism |
| readTextStream | 754 | 754 | Unchanged non-ZIP decoding |
| readPanicFromZip | 1,504 | 343 | Moved bounded multi-entry streaming to **ApplePanicArchiveReader.java**, with standalone JUnit regression tests |

The entire IPhoneLabsActivity file changed from 2,699 to 2,573 lines. The delta does **not** describe a loss of labs: all eight methods and their onCreate buttons remain present. The new ZIP reader lives in a separate class, so total code must account for that helper. No blanket rewrite of historical Apple parsing libraries occurred.

Further blocking tests remain necessary: current real iOS panic fixtures, screenshots after expanding details, report/PDF provenance and the full four-emulator Apple-specific UI matrix. A successful compilation cannot prove an imported crash's real hardware root cause.
