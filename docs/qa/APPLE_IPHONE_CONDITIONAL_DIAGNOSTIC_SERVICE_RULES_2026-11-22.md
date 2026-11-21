# Apple official conditional diagnostic-to-service rules — 2026-11-22

## Purpose

This is a **separate, strictly conditional technical evidence ledger**, not a panic-code repair suggestion.

The GEL iDoctor model already indexes **1,074** Apple Repair Manual chapter URLs and **100** source-reviewed bilingual technical facts. Apple also publishes diagnostic-result-to-service tables. The current curated subset includes **101** independently written, source-linked manufacturer rules for **12** verified internal iPhone hardware model identifiers.

**A rule is applicable only after its exact Apple test actually produced a failure, or a particular symptom persisted after Apple's documented checks.** Importing any panic log never establishes either condition. For this reason, the new rule engine is deliberately **not called by the existing Lab 4 panic-log flow**. It is ready for future, explicitly confirmed Apple diagnostic-result capture; it does not silently prescribe physical repairs.

## Official sources

| Apple publication | What was captured | Source |
|---|---|---|
| iPhone Troubleshooting Display Issues | Failed MRI Ambient Light, Compass and Barometer tests, with model-specific service outcomes | https://support.apple.com/en-us/101966 |
| iPhone Troubleshooting Sound Issues | Failed AST 2 Receiver/Speaker and Left/Right/Front/Rear microphone tests, with model-dependent parts | https://support.apple.com/en-us/101968 |
| iPhone Troubleshooting Mechanical Issues | Persistent button issues and persistent temperature warnings after checks, only for listed device models | https://support.apple.com/en-us/101970 |

## 101 structured rules

| Area | Rules |
|---|---:|
| Light, compass and barometer sensor test failures | **33** |
| Receiver, bottom speaker and specific microphone audio test failures | **63** |
| Persistent button/switch issue after Apple's checks | **3** |
| Persistent thermal alert after Apple's checks | **2** |
| **Total** | **101** |

These rules cover the exact verified model manuals for iPhone 12, 12 Pro Max, 13, 13 Pro Max, 14, 14 Plus, 14 Pro, 14 Pro Max, 15, 15 Pro, 16 Pro and SE (3rd generation). No neighboring model or directory-only device is silently assumed.

### Two examples from the manufacturer's current tables

| Actual Apple diagnostic result | Verified model | Manufacturer's **conditional** service path |
|---|---|---|
| MRI Barometer Sensor Test — **failed** | iPhone 13 Pro Max (`iPhone14,3`) | Whole-unit service |
| MRI Barometer Sensor Test — **failed** | iPhone 15 (`iPhone15,4`) | Main microphone |
| AST 2 Bottom Mic Left — **failed** | iPhone 13 Pro Max (`iPhone14,3`) | Whole-unit service |
| AST 2 Bottom Mic Left — **failed** | iPhone 15 (`iPhone15,4`) | Main microphone |
| AST 2 Rear Mic — **failed** | iPhone 15 (`iPhone15,4`) | Back glass |

The same *observed failed test* can have **different Apple-recommended part paths for different model families**. None of the above follows from the text of a panic log, a generic symptom category or the presence of that chapter in the manual.

### Important restrictions

- **Real diagnostic provenance is required**: the input is an actual Apple MRI/AST 2 result, not a purported Android reading or an iPhone panic code. The lookup receives a separate explicit `externallyVerifiedResult` flag; `false` always returns an empty list.
- **Exact device identity is required**: an unverified hardware string or a directory-only Apple manual cannot receive a conditional rule.
- **Source URL, topic and model must match** the official chapter index. The loader rejects cross-topic and cross-model rules and duplicate contradictory service paths.
- `WHOLE_UNIT` reproduces Apple's **conditional service route** after its stated checks, not a GEL claim that the logic board is broken. Physical work must use Apple's complete current instructions and appropriate qualified service.
- `PERSISTENT_AFTER_CHECKS` is a distinct documented condition for alert/button issues; it is **not** an MRI test failure. The two conditions are not interchangeable.
- The source tables may change, and model/region variants can affect eligibility. Refresh and check the exact Apple page before any final repair quote or work.
- These actions do **not** modify, override or obscure the independent repair-history corpus, whose results remain separate from Apple's official diagnostics.

## Implementation

- Data: `app/src/main/assets/iphone_apple_conditional_service_rules_v1.tsv`.
- Validator/conditional lookup: `app/src/main/java/com/gel/cleaner/AppleIPhoneConditionalServiceRules.java`.
- Tests: `app/src/test/java/com/gel/cleaner/AppleIPhoneConditionalServiceRulesTest.java` check all 101 records, model-specific barometer/microphone differences, exact manufacturer URLs, rejection of neighboring or unknown models, source-topic mismatches, failure-versus-persistence gating, and that panic-only lookup without an actually observed result returns **no repair guidance**.
- Production Lab 4 remains **Apple official documented checks → independent repair histories**, with **no** automatic activation of the new physical-service rules.

## Next phase

Develop an explicit, auditable way to record an externally run Apple Diagnostics result *if* the user authorizes it. Only then may the app display the manufacturer's exact-model conditional follow-up along with its source and caveats. Until such a capture path exists and passes privacy, model-ID and on-device QA, these rules remain a separate offline knowledge ledger, not an automatic replacement engine.
