# GEL iDoctor — Implemented Shizuku UserService (QA, 18 Nov 2026)

## What is actually implemented
This QA branch integrates official Shizuku 13.1.5 `api` and `provider`, a
non-daemon AIDL UserService, explicit per-Labs-screen consent and Shizuku's
own permission workflow. No extra Lab is added. All 29 original Labs still
run normally if Shizuku is absent, stopped, outdated, refused or disconnected.

**ADB shell only (UID 2000).** Root/Shizuku UID 0 is deliberately declined.
The service cannot receive free-form shell commands, paths, app package names
or network addresses. It implements a closed read-only operation allowlist
with bounded output/time and no raw-log return or storage.

| Operation | Actual probe | Consumer | Interpretation |
|---|---|---|---|
| CAPACITY | fixed `power_supply` charge_full and charge_full_design paths, explicit µAh-to-mAh | Labs 14A/14B/Γ | source-labelled reported gauge / design capacity, not a laboratory SoH measurement |
| CRASH | fixed `logcat -d -b crash -t 200`, summarized **inside shell process** | Lab 25 | specific attributed critical events; sampled buffer, not full history |
| THERMAL | `dumpsys thermalservice` status | Lab 16 | Android throttling severity, not per-chip temperature |
| MEMORY | `dumpsys meminfo` aggregate K values | Lab 19 | supplementary summary, not RAM health |
| BOOT | `getprop ro.boot.bootreason` | Lab 20 | boot metadata, never automatic crash diagnosis |

Existing Lab 25 buyer evidence may be supplemented only with confirmed
critical events from the shell crash buffer, with a separate, partial-history
source type. An empty shell buffer cannot erase a stronger prior finding.

## Safety, consent, scope
- Visible, optional Shizuku control BEFORE the existing Lab groups.
- Dark Greek/English popup. Normal Labs are the safe default.
- Shizuku's independent permission is mandatory.
- Permission and server UID rechecked on every privileged probe.
- Clear session on Activity destruction, revocation or binder disconnect.
- No remote technician controls, file modifications, broad folder access,
  `su`, persistent daemon, private-directory harvest or automatic log uploads.
- No `DUMP` permission, root or privilege escalation.
- Keep main branch, protected backup and normal release unchanged.

## Validation requirements before promotion
- GitHub Android Java compile, all JVM unit tests and manifest processing.
- Test actual Shizuku request/bind/unbind in Android 11, Android 13 and Android
  16, with varying OEM security policies and both languages.
- Check missing/corrupt/inconsistent battery capacity on actual hardware.
- Confirm Android system crash buffer parser receives only attributable events,
  does not overstate scope and handles access denial correctly.
- Revalidate runtime ZIP sharing from PR #166 in end-to-end physical-device QA.
- Review bundled Shizuku dependencies, Play policy and lifecycle handling.
- Do not label production ready until those checks are complete.

## Hardware limitations
ADB Shizuku cannot guarantee access to any vendor private sysfs node or
battery BMS estimate. Missing charge_full remains unknown rather than being
silently replaced with design capacity or a SOC guess. Many devices expose
calibrated gauge estimates, not independently validated capacity tests.
