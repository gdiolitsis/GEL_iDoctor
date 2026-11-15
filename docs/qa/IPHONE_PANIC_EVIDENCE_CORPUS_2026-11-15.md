# GEL iDoctor — iPhone panic evidence corpus (QA, 2026-11-15)

## Scope and status
- **49 short, sanitized RECONSTRUCTED examples** in app/src/test/resources/iphone_panic_corpus.tsv.
- They are **NOT 49 original Apple diagnostic files, and NOT 49 proven repairs**.
- Cases are divided into controlled experimental signatures, public repair reports, community knowledge, format variants, known ambiguities, and negative controls.
- Fixture-based regression tests do not establish real-world diagnostic sensitivity, specificity or probability of hardware failure.
- No customer identifiers, serials, IMEIs, crash UUIDs, raw third-party panic-full files, addresses, or photographs are stored.
- These minimal excerpts are authored from public signatures, not copied complete panic logs.

## Evidence sources
1. Controlled module-disconnection experiment: Song et al., Forensic Science International: Digital Investigation 54 (2025), 301980:
   https://dfrws.org/wp-content/uploads/2025/10/A-study-on-the-recovery-of-damaged-iPhone-h_2025_Forensic-Science-Internatio.pdf
   Table 5 links disconnected iPhone 11 / 13 Pro / 14 Plus / 14 Pro / 15 Pro modules to observed codes. These are experimental results on those devices, not universal fault codes.
2. Community reference: https://www.ifixit.com/Wiki/iPhone_Kernel_Panics
   Covers missing sensors, watchdog, AOP, I2C, ANS2, SEP, graphics.
3. Model-specific SMC reference: https://www.ifixit.com/Wiki/iPhone_SMC_Panic_Assertion_Failed
   Covers sensor arrays, decimal and hex values, model-dependent mappings, multi-sensor codes.
4. Self-reported repaired iPhone 11 mic2 case:
   https://www.ifixit.com/Answers/View/805260/
   Power-button flex reseated according to the owner; not independently verified by GEL.
5. Other public troubleshooting cases:
   https://www.ifixit.com/Answers/View/795864/
   https://www.ifixit.com/Answers/View/866821/
   https://www.ifixit.com/Answers/View/844316/
   These may contain proposed or unsuccessful repair attempts, not verified successful diagnoses.
6. Hardware identifiers: https://lirumlabs.com/devices/

## Conservative capabilities
- Explicit missing-sensor tokens: mic1, mic2, prs0, TG0B and TG0V, including multiple values from one incident.
- Only iPhone 11 (iPhone12,1) currently receives model-specific mic1 / mic2 / prs0 physical candidate hints; otherwise the logical sensor is reported without a part replacement instruction.
- SMC sensor arrays in hexadecimal or decimal, with restricted model-scoped mappings (13 family, 14/Plus, 14 Pro, selected 15 Pro and 16 Pro codes).
- Generic clues for AOP, I2C, ANS2 / NVMe, SEP ROM, DCP, explicit baseband faults, watchdog without missing sensor and thermal shutdown.
- Unknown model/code or conflicting source => preserve the code and state that no safe component mapping is available.
- Different imported panic files are assessed independently. Never combine words from different files to make a positive diagnosis.

## Known source conflicts
- Some published 14 Plus guidance assigns SMC 0x500000 to a battery path, while a controlled module-disconnection study observed combined disconnected modules for the same value. We intentionally leave the component unknown.
- Public 15 Pro SMC interpretations vary for some codes. Do not force a part-level diagnosis.
- Nothing in a panic log proves device authenticity, current condition, or prior repair success.

## Test evidence
- ApplePanicHardwareHintsCorpusTest: the 49 source-informed fixtures and explicit negatives.
- ApplePanicEvidencePolicyTest: eligibility and cross-file false-positive prevention.
- IPhoneKnownRepairEmulatorTest: the actual 8 Labs on selected fixtures in a disposable Android 16 emulator; subscriber entitlement is not changed.
- Workflow: .github/workflows/qa-source-built-emulator.yml (job iphone-repair-case).

## Research next milestone
Only consented and appropriately redacted **full** panic logs, paired with independent post-repair outcomes, can support future model-level precision or part-level claims. Also retain failed repairs and ambiguous cases as negatives rather than discarding them.

## Meaning of green
A green diagnostic label means **the specific token was observed in the imported file**. It does not prove a defective part, a confirmed repair or a validated failure probability.
