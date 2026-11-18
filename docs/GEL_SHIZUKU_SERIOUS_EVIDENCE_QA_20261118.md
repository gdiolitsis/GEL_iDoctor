# GEL iDoctor v60 — serious read-only Shizuku evidence (QA, 18 Nov 2026)

## Scope

Stacked on #167. No renumbering or new Lab; unchanged normal Android diagnostics, main, protected backup, signed APK and Play rollout. All new probes require a live, user-approved Shizuku ADB-shell UID 2000 session, can be declined, and are read-only fixed operation IDs.

## New evidence

| Lab | Probe | Strongest defensible interpretation |
| --- | --- | --- |
| 18 | UFS kernel health descriptor from fixed /dev/sys/block/bootdevice or enumerated /sys/bus/platform/devices UFS nodes | Controller-reported endurance-used bands A/B and pre-EOL state, **not exact NAND remaining life** |
| 19 | Bounded Android event log, explicit `lmk_kill` or `am_kill_for_memory` tags | Observed process terminations from a *sampled* period, not defective physical RAM |
| 26 | Bounded `dumpsys appops`, parsed in the ADB UserService | Attributed package / camera, microphone, location `Access` timestamps only, not permission misuse |
| 14 A/B/Γ | Existing Shizuku reported `charge_full` / `charge_full_design` | Distinguish reported full-charge estimate, nominal specification and extrapolated values |

An unsuccessful command, missing sysfs node, reserved UFS descriptor, unavailable event log or incompatible AppOps format means **unknown**, never success or a fabricated healthy state. No negative health claim based solely on lack of evidence. AppOps output may include package names and last-access times in normal Lab exports: disclosure is visible before consent.

## Shell execution safety fix

The former `waitFor()`-before-drain design could deadlock on large `dumpsys` results. The replacement runs a bounded concurrent stdout drainer, enforces a 4-second command deadline and a 384-KiB capture limit, does not execute a shell string, and forbids arbitrary user-supplied paths or commands. Exceeding limits returns `UNAVAILABLE`.

## Tests

- JVM parsing regressions for UFS lifetime 1/5/10/11, pre-EOL 1/2/3 and reserved values.
- Distinguish explicit memory-pressure kill tags from generic process restarts.
- AppOps parsing only permits validated package/op/timestamp records.
- Contract tests for fixed operation IDs, strict consent, shell-only UID, immutable lab count and nominal-capacity provenance.

## Release gate (not complete until device-tested)

1. Android Java/AIDL/manifest build and JVM tests on the *actual* stacked PR HEAD.
2. Permission grant / deny / revoke / binder death / user exit on Android 11, 13 and 16.
3. Real UFS or eMMC device: only show UFS bands when a readable, valid descriptor actually exists. eMMC support is **not** asserted.
4. AppOps on several OEMs: verify AOSP output and that the 384-KiB output/time safety cap does not invalidate too many devices. Values not exposed => no rows.
5. Sample memory kills using real Android event-log data; never convert absent tags to zero lifetime kills.
6. Check final logs and PDF color semantic parity plus no duplicate rows.
7. Repeat Labs 14, 18, 19, 25, 26; verify previous Lab 25 findings remain intact.
8. Only then consider signed QA APK; do not auto-merge to main or publish.

References:
- Android kernel sysfs-driver-ufs ABI: https://android.googlesource.com/kernel/common/+/refs/heads/android-mainline/Documentation/ABI/testing/sysfs-driver-ufs
- AOSP AppOps developer guidance: https://android.googlesource.com/platform/frameworks/base/+/refs/heads/android16-release/core/java/android/app/AppOps.md
- Android memory-pressure documentation: https://developer.android.com/topic/performance/memory/guide/system-wide-memory
