# GEL iDoctor — Lab 24 Root / Verified Boot: evidence and falsification

**Date:** 2026-11-09. **Status:** isolated Draft implementation, acceptance pending.  
**Parent:** PR #46 @ `363dece0aec9c9c032c3cfa1bfbdf7380dbda38e`.  
**Isolation:** no main, Play, permissions, user-account actions, root access, background scanning or protected API modifications.

## Verified code problems and scope
Old Lab24 fused rooted-device file probes, limited package sightings and boot metadata into a fake full-device 0–100 security/root confidence score. Absence of common `su` binaries is not evidence of a clean device; installed manager apps are neither proof of effective root privileges nor universally visible to third-party apps. Build `test-keys` or readable `ro.boot.*` properties are not cryptographic attestation. Moreover, the initial Lab24 branch did not define the three read/probe methods it called; this was corrected before opening this Draft PR.

Current implementation:
- Counts only explicitly visible `su` paths and *targeted*, Android-filtered manager-package lookups. Never stores individual application names, MACs or a complete inventory. A missing lookup is inconclusive.
- Reads three exact allowlisted OS property names using unprivileged `/system/bin/getprop`, no shell interpolation and no `su` invocation. Each process has a bounded 350-ms wait; timeout, denial and absent property all remain UNKNOWN.
- `Lab24BootEvidence` recognizes reported green/locked, yellow/nonstandard trust, orange or red/unverified, contradictory lock properties, and UNKNOWN. Output is explicitly **OS_REPORTED_ONLY** — it does not certify tamper-free hardware, an unlocked device, a secure operating system or root status.
- No hidden `SystemProperties` API or attempt to bypass Android sandbox/Play policies. No security-grade score or automatic risk-percentage claim.
- High-assurance boot integrity needs trusted, authorized, verifier-side attestation and a defined threat model; the app does not claim to possess it.

## Mandatory falsification / acceptance matrix

| ID | Condition | Expected |
|---|---|---|
| B24-01 | All props unreadable or empty | UNKNOWN, not boot verified |
| B24-02 | Only green state visible, no lock state | UNKNOWN |
| B24-03 | Green + locked from allowed property | OS reports locked/green, clearly **not attested** |
| B24-04 | Yellow + locked | Nonstandard trust chain indicator, no root conclusion |
| B24-05 | Orange + unlocked | OS-reported unlocked/unverified; not proof of active root |
| B24-06 | Red + missing lock properties | OS-reported verification concern, not a measured exploit |
| B24-07 | Green + unlocked | Contradictory state; no successful health score |
| B24-08 | Locked property plus flash.locked=0 | Inconsistent, external verification needed |
| B24-09 | Arbitrary OEM/custom properties | UNKNOWN / source limit |
| B24-10 | Normal Play Store app has no su-path access | Absence not root-free certificate |
| B24-11 | Known manager package visible | Only a limited indicator, never effective root claim |
| B24-12 | Installed manager hidden by Android package visibility | No mistaken proof of absence |
| B24-13 | getprop executable missing, denied, slow or exits nonzero | UNKNOWN, no crash / unbounded hang |
| B24-14 | No READ_LOGS, privileged/root permissions or OEM trust APIs | Android's protection remains respected |
| B24-15 | Rooted test phone with root hiding | False negative not presented as clean |
| B24-16 | Stock OEM locked phone | Does not claim 100% safety/tamper-free status |
| B24-17 | Android 11/API30, Android 16/API36, EL/EN | Correct view and neutral export |
| B24-18 | Repeat run, reboot, Back/rotation, app restart | No stale green or previous-device proof |
| B24-19 | Labs 30/31/PDF export and legacy score | UNKNOWN/OS_REPORTED_ONLY never upgrade to hardware-certified score |
| B24-20 | Exact SHA CI, source-built APK and QA | Gate remains pending until observed green |

## Mandatory gates
- [ ] `:app:testDebugUnitTest`, `:app:assembleDebug`, PRO/backend exact HEAD SUCCESS.
- [ ] Four API30/API36 EL/EN emulator UI jobs with artifact review.
- [ ] Real devices (locked stock, unlocked OEM/test device if legally owned), permission limits and timeouts.
- [ ] Security/privacy/Play review: no hidden API, privilege escalation, broad package access or false trust badges.
- [ ] Labs 30/31 and technician/customer PDF confirmation.
- [ ] Explicit owner authorization for merge, release or any Play publication.

**Important distinction:** A tested source-built APK does not establish whether real device firmware is trustworthy. Every report should preserve observation provenance and unsupported claims.
