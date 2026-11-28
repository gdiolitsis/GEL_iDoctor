# GEL iDoctor v55 — Firebase deployment checklist

The Android v55 mainline code is **not enough** for remote service to operate. The
Firebase project is `gel-idoctor` from `.firebaserc`; client callables use
`europe-west1`. Do **not** declare deployment complete until authenticated
Firebase deployment logs report success on the intended project.

## Correct subscription policy

- Technician: active paid GEL PRO, server-approved Google Gift FULL ACCESS, or active Tester PRO. Firebase verifies the technician's entitlement at creation and again at pairing. Gift uses the verified Google identity. Tester PRO uses the Firebase UID that redeemed the activation code.
- Customer: **no purchase required**. The customer only **accepts** a
  technician-created QR / six-digit service code. Authenticated identity and
  explicit consent to sharing diagnostic logs still apply.
- Pairing invitation expires after two hours. A connected session ends at the
  earlier of eight hours or the **technician's** entitlement expiry.
- The customer gets only session-scoped temporary diagnostic access, not a
  paid subscription or any remote-control capability.
- The technician receives customer-only logs and can use the existing
  professional-branded Service Report exporter. Gift FULL ACCESS and Tester PRO
  include personalisation while active.

## Pre-deployment

1. Confirm `firebase use gel-idoctor` or explicitly supply
   `--project gel-idoctor`. Never deploy this configuration to a different
   Firebase project.
2. With trusted **administrator** access, review and preserve a copy of
   **currently deployed** Firestore security rules in Firebase Console. Diff
   them against `firestore.rules` in this repository before replacing them.
   This repository's rules deny by default; unrelated live collections may
   need their production rules retained explicitly.
3. Ensure the project has Cloud Functions, Firestore and required APIs enabled,
   and that the deployer is authorized. Never place Firebase service account
   credentials or refresh tokens in Git, the Android APK, or the chat.
4. Verify the deployed frontend's Firebase project and region match
   `gel-idoctor` / `europe-west1`.
5. Run the following **locally or in a trusted authenticated deployment
   environment** (these commands are not executed merely by merging PRs):

   ```sh
   npm ci --prefix functions
   npm test --prefix functions
   firebase deploy --only functions,firestore:rules --project gel-idoctor
   ```

   The `firebase.json` configuration declares the Firestore rules path.
   Its default hosting configuration is **not** deployed by this command.
   Review any Firebase CLI warnings about function removals rather than
   allowing unintended changes to existing production endpoints.

## Post-deployment acceptance — mandatory before offering this feature

1. Create a session and obtain a QR/six-digit code with each eligible technician grant: paid GEL PRO, Google Gift FULL ACCESS and Tester PRO. Verify rejection for an account with no qualifying grant, an expired Tester grant and a trial-only account.
2. On a second device with **no purchase**, join using the technician's code
   and approve diagnostic log sharing. The device must **not** see a
   subscription-required popup.
3. Run Android labs on the customer device and verify the technician receives
   logs only from this customer's session, with exact value colors.
4. Produce a branded **TXT PDF and HTML PDF** with a technician-owned logo,
   using the included branding on Gift FULL ACCESS and Tester PRO. Check the
   session ID and source label. Verify that the PDF contains only the customer's
   device logs.
5. Verify customer and technician cancellation, two-hour invitation timeout,
   eight-hour session deadline, technician subscription expiry, and no remote
   screen/command access.
6. Confirm that after the session ends, only the customer's temporary
   service-granted diagnostics access expires; their free app and any
   independently owned entitlements remain intact.

## Release

- Android `main` version: v55, versionName `5.5`, versionCode `81`.
- Use the **signed v55 APK from the latest successful main build** for
  two-device testing, not an older v55 artifact from an earlier commit.
- The Google Play AAB is a separate release artifact; never submit it until
  Firebase deployment and two-device acceptance are confirmed.
