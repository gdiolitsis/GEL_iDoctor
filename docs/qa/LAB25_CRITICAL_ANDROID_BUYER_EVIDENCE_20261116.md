# Lab 25 — Critical Android crash evidence / Buyer disclosure (QA, 2026-11-16)

## Product decision
Lab 25 is **provisional**. It is retained only if it can substantiate useful failures of critical phone services for the owner or a prospective purchaser of a used Android phone. A cosmetic home-screen reload, ordinary app crash, or isolated historical failure must not trigger an alarming stability/hardware verdict. If real-device acceptance finds no actionable evidence, remove Lab 25 rather than maintaining a decorative lab.

## What it measures
1. **Current process error snapshot:** only available `ActivityManager.getProcessesInErrorState()` entries, checked against critical process names/installed role holders. Android may show only the caller UID; no visible error does *not* mean all system processes are healthy.
2. **Privileged Android DropBox records (only if readable):** `system_server_crash`, `system_server_anr`, `system_app_crash`, `system_app_anr`, `data_app_crash`, `data_app_anr`. A generic system-app tag alone is not proof; it must contain the actual critical process identity. No privileged permissions are requested or bypassed.
3. **Optional, consent-based Android bugreport ZIP/TXT:** file selected using Android's document picker, parsed locally without extracting its raw content to disk, logging stack traces, or sending reports anywhere. Only strong `FATAL EXCEPTION + Process` and `ANR in <process>` evidence is retained. The selected file's origin is **not independently authenticated**.
4. Preserve the independent four-kilobyte file write/readback check, which does **not** establish NAND health and is not a reason to keep the crash lab.

Critical services: actual default HOME, SMS, DIALER (via Android roles and public fallback APIs), System UI, `system_server`, telephony and Settings. Arbitrary installed apps are excluded. A third-party default SMS or dialer may be critical despite not being a pre-installed system app.

## Display rules
- No records / no permissions: **neutral/white**. Never an all-clear.
- One dated or undated fault: **neutral/white**, with scope and no hardware-failure claim.
- Repeated, independently timestamped entries for the same critical process: **yellow**, recommend additional validation before a used-device purchase. Not red or a hardware diagnosis.
- Green: only a directly confirmed successful file round-trip, not the absence of crashes.
- Raw bugreport content may contain private data. Never include it in GEL logs, exports, uploads or analytics.

## Acceptance / removal gate
- Compile/test Android 11, 13, 16; EL/EN; test picker cancellation, ordinary app crash exclusion, RoleManager-default app detection, optional text/ZIP import, no internet upload, malformed/oversized report, non-crash launcher reload, single crash, repeated timestamped incidents.
- Validate end-to-end exported service report contains only component/type and source limitation, not raw user data.
- Confirm that the optional import gives genuinely useful, reproducible, correctly attributed critical events on a real device. If no usable critical information is obtainable for an ordinary buyer, remove Lab 25 and its menu entry **after** the QA decision, preserve the existing file test by relocating it if necessary, then renumber remaining labs in one controlled pass. Never renumber based on assumptions.
- PR remains unmerged until acceptance; main and protected backup untouched.

Official APIs: https://developer.android.com/reference/android/os/DropBoxManager ; https://developer.android.com/reference/android/app/ActivityManager ; https://developer.android.com/studio/debug/bug-report .
