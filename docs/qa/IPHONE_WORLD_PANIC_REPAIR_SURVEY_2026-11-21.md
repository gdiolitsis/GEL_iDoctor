# GEL iDoctor — Worldwide iPhone Panic / Repair Outcome Research
**Research checkpoint:** 2026-11-21  
**Scope:** global publicly available technical references, controlled hardware-disconnection experiments, and first-hand repair reports. **Not an exhaustive survey of every known panic signature or every repaired iPhone.**

## 1. Distinguish source types
A panic **signature/code**, a **model-specific hardware-path mapping**, a **proposed troubleshooting repair**, and a **reported successful repair** are different things. Do not combine them into a single confirmed-fix count.

- **Public rule repositories**: Check Panic Full methodology claims 139 diagnostic rules; PanicDecode advertises 280 codes. These represent the products' published rule/catalog counts, **not verified successful repairs**.
  - https://checkpanicfull.com/methodology
  - https://panicdecode.com/
- **Controlled 2025 experiment**: DFRWS / *Forensic Science International: Digital Investigation*, Volume 54 (2025), Article 301980. Authors induced faults on iPhone 11, 13 Pro, 14 Plus, 14 Pro, 15 Pro by disconnecting modules and studied restoration with functioning modules. This establishes test-specific mechanisms, not a universal code-to-part rule or general repair success probability.
  - https://dfrws.org/wp-content/uploads/2025/10/A-study-on-the-recovery-of-damaged-iPhone-h_2025_Forensic-Science-Internatio.pdf
- **Community mapping and troubleshooting**:
  - https://www.ifixit.com/Wiki/iPhone_Kernel_Panics
  - https://www.ifixit.com/Wiki/iPhone_SMC_Panic_Assertion_Failed
  - https://www.ipadrehab.com/article.cfm?ArticleNumber=49
- **Actual repair-attempt reports**: linked per attempt and original thread in `app/src/main/assets/iphone_panic_repair_knowledge_v1.tsv`.

## 2. Curated source-linked outcome sample
Current checkpoint: **24 separate repair attempts**, sourced from public case reports.

| Reported outcome | Attempts |
| --- | ---: |
| First-hand or shop-reported resolution | 12 |
| First-hand or shop-reported failure | 10 |
| Partial improvement / recurring identical panic | 1 |
| Outcome unresolved or follow-up inadequate | 1 |
| **Total** | **24** |

These attempts cover **10 distinct indexed code/signature keys** and **13 model identifiers** (including two identifiers for XS Max). **24 attempts are not 24 independent devices:** several attempts belong to one owner or workshop incident, as grouped by `incident_id`. **No population-level repair success rate may be computed** from these selected, often self-reported internet cases.

## 3. Notable observed repairs (reported, not independently certified)
- iPhone 11, `MIC2`: power-button flex replacement, or reseating a loose connector; another owner reported success with a different charging-port flex. Outcomes and chronology remain case-specific. https://www.ifixit.com/Answers/View/730301/ ; https://www.ifixit.com/Answers/View/805260/ ; https://www.ifixit.com/Answers/View/645543/
- iPhone 13 Pro Max, `SMC 0x800`: charging-port flex replacement reportedly ended restarts. https://www.ifixit.com/Answers/View/810046/
- iPhone 14 Pro, `SMC 0x80000`: front proximity flex replacement reportedly resolved the issue. https://www.ifixit.com/Answers/View/799078/
- iPhone 14 Pro Max, `SMC 0x80000`: shop report of front sensor flex replacement restoring operation. https://forum.nopowerfix.com/t/iphone-14-pro-max-pink-screen-and-restarts-front-flex-fixed/28
- iPhone 14 Pro, `SMC 0x40000` (decimal 262144): charging-port replacement reportedly stopped restarts. https://www.ifixit.com/Answers/View/953710/
- iPhone 12 Pro Max, `PRS0`: technician-reported resolution after substituting a working donor charging port assembly. https://www.ifixit.com/Answers/View/651841/
- iPhone 11 Pro Max, explicit AppleCS42L77Audio failure signature: shop report of recovery after replacing the front sensor flex, *not* the apparently named audio codec. https://forum.nopowerfix.com/t/iphone-11-pro-max-boot-loop-after-screen-swap-front-flex-fixed/27
- iPhone XS Max, later **explicit `I2C3 SCL stuck low`** reported resolved after a display replacement, following earlier unsuccessful attempts; **do not generalize this as display replacement for any `PRS0` or generic I2C code**. https://www.ifixit.com/Answers/View/941226/

## 4. Failed or incomplete fixes (equally important)
- iPhone 11 Pro Max `MIC2`: two power-flex replacements, one new and one donor, failed in same water-damaged device. https://www.ifixit.com/Answers/View/770966/
- iPhone SE 2020 `MIC1`: charging-port replacement restored microphone functionality but **not** restart stability in one report; another reported continued restarts. https://www.ifixit.com/Answers/View/866821/ ; https://www.ifixit.com/Answers/View/903085/
- iPhone 15 `SMC 0x80000`: charging-flex from another handset did not clear panic. https://www.ifixit.com/Answers/View/874876/
- iPhone 14 family `SMC 0x400000`: wireless-charging replacements did not necessarily stop restarts in fluid-damaged devices. https://www.ifixit.com/Answers/View/844316/ ; https://www.ifixit.com/Answers/View/851432/
- iPhone 15 Pro `SMC 0xA1`: a battery replacement improved symptoms, but the owner said the panic occasionally recurred. **Category is PARTIAL_OR_RECURRING**, not success. https://www.ifixit.com/Answers/View/943079/

## 5. Conflict and model-isolation policy
1. **Same code, different models:** bitmasks like `SMC 0x40000`, `0x80000`, `0x100000` have model-dependent meanings and may refer to different paths, combinations of modules, or unsupported matches. Require exact `iPhone##,#` identifiers.
2. **Conflicting 14 Plus `0x500000`:** community references have assigned a battery-path meaning while controlled experiments observed multiple disconnected modules. No recommended specific part while conflict remains unresolved.
3. **Disputed 15 Pro bitmasks:** publication sources vary, including the experimental `0x100000` USB-flex association. An unreplicated reference is not a diagnosis.
4. **Generic driver/module text:** strings for AOP, I2C, ANS2/NVMe, DCP, SEP and baseband need specific panic conditions. Naming a driver doesn't prove that chip is faulty.
5. **Missing reports:** zero repair reports for a key **does not** prove no fix exists. Show only confirmed data coverage.
6. **Failure recording:** initial failed attempt and later successful attempt on one handset must remain linked to one `incident_id`.
7. **No success percentages** without representative denominators, validated post-repair logs and independent follow-up.

## 6. Implementation and limits
The new offline `IPhonePanicRepairKnowledgeBase` is a provenance-preserving research aid. iPhone Lab 4 may display exact-model and exact-code matched historic case reports, outcome types and links, **not an automatic component replacement instruction**. The existing 49 source-inspired synthetic panic fixtures are separate QA artifacts, **not 49 proven repairs**. No customer panic log is uploaded or automatically harvested. If exact code/model is unknown, the application retains the neutral GEL hardware clue, not a fabricated repair.

## 7. Research expansion priorities
Next evidence-collection cycles should cover more independent cases and systems: iPhone X–12 `mic1/mic2/prs0/TG0B/TG0V`; 13–16 family SMC arrays in both decimal and hex; AOP, audio, GPU/DCP, NAND, baseband and charging-path events. Each report requires (1) actual model, (2) raw excerpted condition rather than whole private log, (3) repair performed, (4) previous failed attempts, (5) post-repair reboot observation and follow-up window, (6) original publication link/date. Unknowns and conflicts must be included, not inferred away.
