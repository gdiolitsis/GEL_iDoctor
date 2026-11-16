# Lab 25 — Advanced diagnostic access feasibility (16 Nov 2026)

**Research only.** No undocumented API, ADB permission, shell command or root method is required for normal GEL iDoctor operation. Opt-in paths below apply **only to devices owned by the operator or inspected with the owner's consent**. No exploit, silent privilege escalation, weakened SELinux, hidden API enforcement changes or persistent debugging settings.

## What the Android platform actually permits

1. **Android DropBoxManager**: System Crash/ANR tags are real. Starting with API 35, the documented `getNextEntry()` requires `READ_DROPBOX_DATA` **and** `PACKAGE_USAGE_STATS`, while older targets require `READ_LOGS`. The AOSP manifest marks `READ_LOGS` and `READ_DROPBOX_DATA` `signature|privileged|development`, "not for third-party applications." Ordinary usage-access toggle alone does not grant DropBox.
2. **ApplicationExitInfo**: `ActivityManager.getHistoricalProcessExitReasons(...)` cannot enumerate the history of arbitrary system UIDs from an ordinary app; privileged `DUMP` is required to expand scope. Do not infer crash from `REASON_LOW_MEMORY`, `REASON_OTHER`, process death or a Launcher reload.
3. **Hidden/undocumented API**: `RoleManager.getRoleHolders()` is `@SystemApi @hide` and requires `MANAGE_ROLE_HOLDERS`. On PR #143 initial CI failed when it was called directly; the implementation was fixed to use public default-SMS/dialer/Home resolution instead. Reflection/JNI also face Android non-SDK API restrictions and do **not** grant underlying Binder permissions.
4. **ADB development access**: `DUMP`, `READ_LOGS`, `READ_DROPBOX_DATA` are *development* permission types in AOSP. On supported devices/versions, with app-declared permission, the user may explicitly grant a diagnostic permission through ADB for testing. This must be experimentally verified per OEM and version. The shell itself has `DUMP` in AOSP. Do not ship special permissions into the Play manifest without policy review; use a separate consent-based technician/dev flavor if proven useful.
5. **Shizuku / ADB-shell helper**: Android 11+ Shizuku supports wireless-debugging startup with user pairing (without root); it runs with **shell**, not unrestricted system/root permissions. The service stops across reboot and OEM restrictions apply. A dedicated opt-in technician interface could use shell-level `dumpsys` and `logcat` and only return parsed, redacted findings to GEL. Do not include unvetted third-party code or force Shizuku installation.
6. **User-selected bugreport (.zip/.txt)**: Already prototyped in PR #143. Can include historic logcat, dumpsys, dropbox, native tombstones, watchdog/pstore excerpts, potentially before the installation of iDoctor. Reports can also be incomplete or overwritten and contain very sensitive personal information. Keep local processing and strict summary-only export.
7. **OEM diagnostic bundles**: Xiaomi/MIUI/HyperOS and other vendors have different bugreport/log export facilities. Treat them as optional user-selected documents with provider-specific parser contracts; **never** assume the files exist or that their format is stable.
8. **Root**: With explicit permission, technician can analyze `/data/system/dropbox`, `/data/tombstones`, and available `/sys/fs/pstore/`/ramoops artifacts. **No root acquisition or bootloader unlocking inside GEL**; these would change buyer-relevant security evidence and might wipe the phone. Read only when permitted.

## Suggested opt-in technician commands (read-only)

These commands run **outside the consumer app** on an authorized device with ADB:
```bash
adb shell dumpsys dropbox
adb shell dumpsys dropbox --print system_server_crash
adb shell dumpsys dropbox --print system_server_anr
adb shell dumpsys dropbox --print system_app_crash
adb shell dumpsys dropbox --print system_app_anr
adb shell dumpsys activity exit-info
adb logcat -d -b crash
adb logcat -d -b events
adb shell getprop sys.boot.reason
adb shell getprop ro.boot.bootreason
adb bugreport
```
Availability varies; outputs can be incomplete, very large, private, or unavailable. ADB commands must not be executed silently by a product. An optional future companion should request granular user consent, show collected categories, redact before export, and disable debugging when finished.

## Evidence interpretation (used-device buyer)

| Signal | Interpretation | Confidence |
|---|---|---|
| `FATAL EXCEPTION` + process name + timestamp | Java crash of a specific critical process | Observed when report authentic |
| `ANR in <critical process>` + timestamp | Critical service unresponsive, **not** necessarily crashed | Observed when authentic |
| Native tombstone + `signal` + `pid/name/cmdline` | Native crash of identified critical executable | Observed when authentic |
| `system_server` watchdog with explicit cause | Serious system-service failure | Observed when authentic |
| `kernel panic` / pstore stack | Low-level system crash; needs timestamp/boot correlation | Observed when authentic |
| Short uptime / reboot count increment | Device rebooted; cause **unknown** | Neutral |
| Launcher icons disappeared, system UI reloaded | Symptom only; **not** proof of Android crash | Neutral |
| Single crash of HOME/SMS/System UI | Isolated documented issue, usually no hardware alarm | Neutral |
| Two independently timestamped faults of the *same* critical component | Repeated problem, warrants additional pre-purchase checks | Caution |
| Missing logs, denied API, old records evicted | Coverage unavailable, **never** "healthy zero" | Neutral |

## Decision

- Production Lab25 = honest, zero-intrusion, public-API baseline + opt-in on-device bugreport parsing.
- Next experimental mode (separate QA PR/companion) = **ADB-shell / Shizuku** with explicit consent and read-only evidence. Establish an OEM/API compatibility matrix before integrating.
- **If no evidence-bearing critical check can be verified on real devices, remove Lab25.** Keep proven unrelated 4 KB readback in a storage Lab (or separate function), and renumber with compatibility migration only after decision.

Primary references:
- https://developer.android.com/reference/android/os/DropBoxManager
- https://developer.android.com/reference/android/app/role/RoleManager
- https://developer.android.com/guide/app-compatibility/restrictions-non-sdk-interfaces
- https://android.googlesource.com/platform/frameworks/base/+/refs/heads/android16-qpr2-release/core/res/AndroidManifest.xml
- https://android.googlesource.com/platform/frameworks/base/+/android16-release/packages/Shell/AndroidManifest.xml
- https://developer.android.com/tools/logcat
- https://developer.android.com/studio/debug/bug-report
- https://source.android.com/docs/core/tests/debug/read-bug-reports
- https://source.android.com/docs/core/tests/debug
- https://shizuku.rikka.app/guide/setup/
