# GEL iDoctor v55 — paid read-only Service Session

This update is isolated from the v54 Google Play closed-testing bundle.

## Subscription gates

- The technician's **Connect to Customer** and the customer's **Connect to Technician** both require an active **paid Google Play GEL PRO subscription**.
- Without a paid subscription, show an explicit Greek/English subscription-required popup and a route to the PRO subscription/restore screen.
- Tester codes, administrator gifts, and free trials remain usable for their existing purposes, but **do not authorize technician/customer pairing**.
- The backend also checks each Firebase UID's active, server-verified paid subscription; knowledge of the QR, deep link, or 6-digit code cannot bypass the gate.
- The customer's existing paid access is independent of the connection. **Do not lock the full app or cancel their personal subscription when the service session ends.**

## Clock and permissions

- QR/service-code **pairing window: 2 hours**.
- After confirmed pairing, service duration is **at most 8 hours** from the server-side connection time. If either paid subscription is verified to expire earlier, the service expires earlier.
- An immutable `serviceEndsAt` timestamp is in Firestore; diagnostics upload and Firestore diagnostics reads reject access after it.
- Customer-side monotonic and wall-clock lease checks stop session-granted access without waiting for a Firestore update. Either participant can invoke `expireServiceSession` to transition the server document to `EXPIRED`.
- Customer or technician may also terminate early.

## Stage-one remote boundary

- Show pairing actions on both Android devices.
- The technician sees a **read-only** view of diagnostics uploaded from customer labs.
- No screen capture, video streaming, touch dispatch, remote settings changes, or remote command execution. All seven remote backend callable entry points are disabled; the Android remote command foreground service remains excluded from the manifest.
- No permanent PRO is issued to the customer device.

## Deployment order (mandatory)

1. Run `npm ci --prefix functions && npm test --prefix functions` and `./gradlew testDebugUnitTest`.
2. Deploy the revised Cloud Functions `functions/index.js` **and** `firestore.rules` to the intended Firebase project, using authorized administrator credentials, before offering the paired feature to customers.
3. Test two real devices with active Play subscriptions, expired/no subscription in each role, tester-code-only access, QR + manual code, customer cancellation, technician cancellation, and the 8-hour deadline.
4. Only after step 3, publish v55 with versionCode 81. Do not reuse the v54 (code 80) Play upload.
5. Privacy/data disclosure should accurately describe the collection and transfer of diagnostic log content to the paired technician.
