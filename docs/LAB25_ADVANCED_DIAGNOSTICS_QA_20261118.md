# GEL iDoctor — Advanced Diagnostic Evidence Architecture (QA draft, 18 Nov 2026)

## Principle
Do not add a laboratory. Lab 25 remains the central place for crash / ANR /
watchdog / bugreport evidence. Other existing labs can consume extra
read-only, source-attributed observations from an optional shared provider.
The standard tests MUST continue to work with no ADB, root or Shizuku.

## Phase 1, implemented in this QA PR
- Android Sharesheet ACTION_SEND receives a single content URI; the user sees
  an explicit dark GEL consent dialog, and can cancel without analysis.
- On approval, the app uses its existing non-exported ManualTestsActivity:
  first run the automatic Lab 25, then analyze the user-shared Android report
  locally through CriticalSystemCrashEvidence.parseUserReport.
- Preserve existing input bounds (32 MiB parsed characters, 450k lines,
  bounded ZIP entries and observed critical events).
- No new Lab button, no arbitrary storage searches, no root, no privilege
  escalation, no report upload or full-file retention.
- A user-shared report is UNVERIFIED provenance. Its absence of findings
  cannot override a separate positive critical-error finding.

## Phase 2, proposed NOT implemented: optional Shizuku / ADB integration
Real operating-system privileges cannot be created by an in-app consent
switch. A supported helper must be present and running; the user needs
to authorize that helper and approve specific actions. The ADB shell UID
is not root and is still restricted by Android SELinux.

The future consent entry shall be contextual (only when enhanced evidence
can benefit a selected Lab or when user chooses enhanced QA), not a mandatory
popup on every visit to the Labs. NORMAL remains the default and there must
be an obvious Skip path.

A shared EnhancedEvidenceProvider would have:
1. A closed read-only operation allowlist: never accept shell command strings
   from remote technicians, reports, network messages or user-entered text.
2. A per-session approval flag that is rechecked at each operation, with
   explicit handling of denied / disconnected Shizuku service.
3. Strict execution timeout, byte quotas, no subprocess persistence and
   no background access; no modifications to permissions or system settings.
4. Local parsing into typed values: evidence, source, timestamp, access
   scope, and confidence / unavailability. Raw logs should not be stored or
   silently uploaded, and private fields must be minimized.
5. No false conclusion from unsupported or SELinux-blocked data.
6. Gradle/API compatibility verification, security review, Android 11/13/16
   physical-device QA in Greek and English, plus Play policy review.

Candidate consumers and guardrails:
- Lab 25: accessible crash buffer, critical system events and report extracts;
  evidence must prove a crash/ANR and attribute its process.
- Lab 20: accessible boot metadata, never a claim of a historical crash.
- Lab 16: thermalservice status, not fabricated chip temperatures.
- Lab 19: aggregate meminfo/procstats, never another app's secrets.
- Lab 18: diskstats if exposed; not NAND wear or remaining lifetime.
- Labs 14A/14B/14Gamma: battery statistics for corroboration only; do not
  substitute for each lab's continuous timestamped charge/current trace.
- Lab 17 and Labs 27/28/29: consume only validated typed outputs from
  the original Lab, never guessed values.

## Scope and security
The ordinary third-party app cannot request a full Android bugreport from
BugreportManager just because the owner grants consent in a GEL popup.
The BugreportManager API requires privileged DUMP permission (or special
carrier eligibility); the user may still share an already generated report
through Android Sharesheet. Shell access cannot promise visibility into every
private / protected file or device component.

No Shizuku dependency, Android debugging enablement, permission grant,
shell command execution or production rollout is included in Phase 1.
Do not turn on any pre-Lab enhanced authorization UI until it has a working,
tested provider. Keep main, backups and Play release unchanged.
