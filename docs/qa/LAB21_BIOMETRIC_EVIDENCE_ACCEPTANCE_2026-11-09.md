# GEL iDoctor — Lab 21 screen-lock / biometric evidence acceptance

**Date:** 2026-11-09 · **Status:** isolated Draft implementation proposal, not CI or physical acceptance.  
**Parent:** unchanged PR #46 SHA `363dece0aec9c9c032c3cfa1bfbdf7380dbda38e`.  
**Scope:** Lab21 only. No main merge, manifest permission changes, Play publishing, biometric enrollment changes, account mutations or privileged APIs.

## Confirmed source defects and Android semantics
1. Old `BiometricPrompt.onAuthenticationFailed()` immediately emitted **hardware FAIL** and released the session guard. Per Android SDK, this is a *nonterminal* unrecognized attempt; the user can retry. Subsequent success could yield conflicting PASS and FAIL in the report.
2. `onAuthenticationError` always claimed `Fallback to device credential detected`, regardless of the error code. Cancellation/lockout/OEM errors do not prove such fallback.
3. Old readiness check executed `BiometricManager.canAuthenticate(int)` under `SDK_INT >= 29` even though the overload was introduced in **API30**. On API29 the method must use the no-argument form; on API28 a BiometricPrompt exists but platform BiometricManager does not.
4. Device with available BIOMETRIC_WEAK could be marked ready, while the actual prompt required BIOMETRIC_STRONG, causing contradictory outcomes.
5. Reading `KeyguardManager.isDeviceSecure` and `isKeyguardLocked` produced unsupported claims of post-reboot data-encryption enforcement, identity-hardware correctness and an arbitrary **0–100 security score**, including penalizing simply being unlocked in the currently active app.
6. Root probe checked system Gatekeeper/lock DB/Keystore path existence, equating filesystem heuristics with real authentication-enforcement state. Root exposure is not required for a normal Play Store diagnostic app.
7. Prompt opened without a clear standalone opt-in consent gate and lifecycle callbacks lacked a single terminal-outcome token.

## New source contract
- Keyguard configuration, currently displayed keyguard and biometric readiness are reported separately, with missing-read **UNKNOWN**.
- API30+: read and select STRONG if available, otherwise WEAK, and use the corresponding `setAllowedAuthenticators` value. API29 uses the proper no-argument `canAuthenticate()`; API28 allows an explicit *attempt* only, without asserting readiness.
- Add explicit bilingual choice before invoking the Android-provided biometric UI. It never collects or stores face/fingerprint templates.
- `Lab21BiometricEvidence` state machine: `NOT_STARTED`, `IN_PROGRESS`, `ANDROID_AUTHENTICATED`, `USER_CANCELLED`, `UNAVAILABLE_OR_ERROR`; **no hardware-health verdict**.
- `onAuthenticationFailed` increments nonmatching attempts but **does not terminate**. Only successful authentication, terminal OS error or cancellation closes the session.
- Android error codes are reported as platform details; no imaginary fallback mode. Stale or duplicate callbacks are ignored with a run-generation and session-instance guard. Destroying Activity cancels its active signal.
- Remove Lab21 root filesystem checks and invented 0–100 score/post-reboot guarantees. A positive result confirms only OS authentication via the selected path and permitted modality strength.
- No new biometric permissions: existing `USE_BIOMETRIC` declaration remains unchanged.

## 20 mandatory negative / acceptance controls

| Case | Configuration/action | Required outcome |
|---|---|---|
| BIO21-01 | Android 11/API30 secure enrolled STRONG | User opt-in; successful prompt => ANDROID_AUTHENTICATED only |
| BIO21-02 | Android 16/API36 enrolled WEAK-only | Correct authenticator chosen; no forced STRONG error |
| BIO21-03 | Android 10/API29 | Legacy no-argument readiness call; no API30 method crash |
| BIO21-04 | Android 9/API28 | BiometricPrompt possible with unknown readiness; never fabricated available |
| BIO21-05 | Android 8/API26–27 | No unsupported prompt; NOT_TESTED |
| BIO21-06 | No enrolled biometric | Platform not-ready, no hardware FAIL |
| BIO21-07 | No biometric hardware | Platform not-ready, no red physical device failure claim |
| BIO21-08 | Permission denied / OEM service unavailable | UNKNOWN/UNAVAILABLE |
| BIO21-09 | Locked/unlocked while app in foreground | No arbitrary unlocked-phone risk penalty |
| BIO21-10 | Secure device reported | No false full-disk encryption or post-reboot guarantee |
| BIO21-11 | User SKIP before OS prompt | NOT_TESTED; export released |
| BIO21-12 | One unrecognized fingerprint then matching fingerprint | Retry remains active, single success result |
| BIO21-13 | Several unrecognized samples followed by cancel | USER_CANCELLED, no hardware FAIL |
| BIO21-14 | Prompt denied/cancelled/negative button | USER_CANCELLED; no fallback-to-PIN statement |
| BIO21-15 | Prompt unavailable/lockout/hardware-busy error | UNAVAILABLE_OR_ERROR, no sensor defect inference |
| BIO21-16 | Success callback then late error | One terminal result, duplicate ignored |
| BIO21-17 | Cancelled session then stale success callback | Cannot convert cancel into green authentication |
| BIO21-18 | Rotation/Activity destroy while prompt open | Cancel old CancellationSignal, ignore old callback |
| BIO21-19 | Multi-biometric OEM with face+fingerprint | Never claim specifically fingerprint or face if Android abstracts it |
| BIO21-20 | Bilingual UI, Lab30/31 and PDF export | Fresh source-scoped state; no stale 0–100 score or hardware certificate |

## Verification gates
- [ ] Exact head `:app:testDebugUnitTest`, `:app:assembleDebug`, PRO/backend checks green; 10 new unit regression cases.
- [ ] Source-built API30/API36 emulator UI QA, English/Greek; inspect reports and cancel flow. Emulator cannot validate actual physical biometric.
- [ ] Real Android 9/10/11/16 where available with consenting enrolled users and device-specific STRONG/WEAK prompts.
- [ ] Security/privacy audit: no biometric template, account identifier, persistent credential or unauthorized root file read.
- [ ] Final Labs 30/31 report propagation and legacy 0–100 field behavior; reject stale PASS or unknowns.
- [ ] Owner explicit review before any merge or Play publishing.

References: [BiometricManager API levels](https://developer.android.com/reference/android/hardware/biometrics/BiometricManager) · [BiometricPrompt callbacks](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt.AuthenticationCallback)

**Nonclaim:** One successful OS prompt is not scientific evidence of sensor modality, enrollment quality, counterfeit components, liveness guarantees, vulnerability-free firmware or device-wide security posture.
