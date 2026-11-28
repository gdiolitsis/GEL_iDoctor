# GEL iDoctor v55 — paid, Gift FULL ACCESS or Tester PRO technician, free customer read-only Service Session

This update is isolated from the v54 Google Play closed-testing bundle.

## Subscription gates

- **Only the technician** must hold an active paid GEL PRO subscription **or administrator-approved Google Gift FULL ACCESS or active Tester PRO** to **create** a Service Session (with QR or six-digit code).
- **The customer joins for free** by scanning the technician's invitation QR or entering the code. The customer's **Connect to Technician** screen is acceptance-only: no button or callable to create a session.
- The entitlement-required popup appears **only on the technician's creation path**. Paid GEL PRO, approved Google Gift FULL ACCESS and active Tester PRO authorize technician sessions.
- The server checks the creating technician's paid GEL PRO, verified Google Gift FULL ACCESS or Tester PRO on creation **and** customer pairing. Firebase authentication, session code, identity, consent and deadlines still apply to the customer; knowing the code never creates a new session.
- A successfully connected customer receives a **temporary session-scoped diagnostic PRO lease**; it expires when the eight-hour session ends, and does not create a paid subscription.
- The customer's independently paid or otherwise legitimate access remains unaffected. **Never lock the entire customer app or cancel an independently purchased subscription when service ends.**

## Clock and permissions

- QR/service-code **pairing window: 2 hours**.
- After confirmed pairing, service duration is **at most 8 hours** from the server-side connection time. If the **technician's** qualifying PRO entitlement expires earlier, the session ends earlier. The customer's billing status does not affect pairing or the deadline.
- An immutable `serviceEndsAt` timestamp is in Firestore; diagnostics upload and Firestore diagnostics reads reject access after it.
- Customer-side monotonic and wall-clock lease checks stop session-granted access without waiting for a Firestore update. Either participant can invoke `expireServiceSession` to transition the server document to `EXPIRED`.
- Customer or technician may also terminate early.

## Stage-one remote boundary

- Show pairing actions on both Android devices.
- The technician sees a **read-only** view of diagnostics uploaded from customer labs.
- The technician has **EXPORT CUSTOMER SERVICE REPORT**, which opens the existing
  TXT/HTML PDF export with a remote-only customer session source.
- The complete available diagnostic batches are fetched directly from Firestore
  using server reads, with pagination, identity checks and explicit size bounds.
  If a batch fails or the source is too large, the export refuses to issue a
  misleading partial PDF.
- PDF provenance explicitly names the customer source and session ID.
  The Android host shown on the report is labeled **technician host**, never
  assumed to be the customer's phone model.
- Color values come only from the customer-side `GELServiceLog.PdfRow`
  metadata, transferred as validated plain data alongside the log lines.
  The server never infers failures from color or thresholds.
- Local technician logs are never mixed into remote reports or erased by them.
- Professional branding uses the **existing** logo/business profile and
  single-report/unlimited-Custom-Reports authorization. A basic GEL PRO
  subscription alone does not waive the current separate personalization cost.
- The customer sees an explicit consent dialog before sending diagnostic logs
  to the paired technician; the consent does not authorize remote screen control.

- No screen capture, video streaming, touch dispatch, remote settings changes, or remote command execution. All seven remote backend callable entry points are disabled; the Android remote command foreground service remains excluded from the manifest.
- No permanent PRO is issued to the customer device.

## Deployment order (mandatory)

1. Run `npm ci --prefix functions && npm test --prefix functions` and `./gradlew testDebugUnitTest`.
2. Deploy the revised Cloud Functions `functions/index.js` **and** `firestore.rules` to the intended Firebase project, using authorized administrator credentials, before offering the paired feature to customers.
3. Test with a **paid GEL PRO, approved Google Gift FULL ACCESS or active Tester PRO technician and a free customer**, QR + manual code, explicit customer sharing consent, technician entitlement enforcement (including a direct call without a qualifying grant), expired technician entitlement, customer cancellation, technician cancellation, eight-hour deadline, live log color fidelity, technician-logo TXT and HTML PDF, and prevention of cross-device report mixing.
4. Only after step 3, publish v55 with versionCode 81. Do not reuse the v54 (code 80) Play upload.
5. Privacy/data disclosure should accurately describe the collection and transfer of diagnostic log content to the paired technician.
