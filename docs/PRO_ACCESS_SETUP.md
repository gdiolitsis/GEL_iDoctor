# PRO access and release prerequisites

This branch is preparation, not a deployed or Play-verified release.

## Access paths

- Paid `gel_pro_monthly`: Play purchase or Restore -> Firebase callable -> Android Publisher `subscriptionsv2.get` -> matching product and valid subscription state/expiry -> local access -> acknowledgement. Pending purchases do not unlock. A verified offline lease lasts at most 24 hours and never past Play expiry.
- Gift: optional Google sign-in -> verified Firebase Google email -> private `pro_gift_accounts/{sha256(trimmed lowercase email)}` -> `enabled: true`. No payment or recurring subscription. Each lease lasts at most ten minutes; foreground activities refresh every five minutes and on entering a screen. Disabling/removing an entry stops renewal. Existing offline access ends within ten minutes under the normal device clock. A modified/rooted client is outside this cache guarantee.
- Existing tester codes remain a separate closed-test mechanism. Signing out/switching to Google clears the old tester cache. Paid purchases are independent of app Google sign-in.
- Gift access covers PRO. Separate Custom Reports / single-report purchases are not silently granted by a PRO gift.

## Required cloud configuration BEFORE shipping

1. Firebase Authentication: enable Google provider for `gel-idoctor`; configure support email. Add the Play App Signing SHA fingerprints (and debug fingerprints only for debug testing). Download the updated `app/google-services.json`. The repository's current configuration has no OAuth clients, so Google sign-in intentionally reports unavailable until replaced.
2. Enable Google Play Android Developer API. Grant the deployed functions runtime service account only the required app-scoped permission to read purchases/subscriptions for `com.gel.iDoctor`. Do not put a service-account key into the Android app or Git.
3. From `functions/`, run `npm ci`, `npm test`; deploy new callables `getGiftProEntitlement`, `verifyGelProPurchase`, `deleteProGoogleAccount` with a trusted Firebase administrator. Existing Firestore default-deny rules already prevent client reads/writes of gifts and entitlements. No public admin endpoint is provided.
4. With trusted Application Default Credentials for this project, manage gifts:

   ```sh
   cd functions
   node manage-pro-gift.js grant person@example.com
   node manage-pro-gift.js revoke person@example.com
   ```

   The admin tool writes only an email hash, enabled flag and timestamps. Do not grant accounts until the owner supplies the exact intended Google email addresses. Revoking a gift does not revoke a separately paid Play purchase.
5. Review and publish updated privacy disclosure. Update Play Data Safety for optional account identity/email/profile, purchase verification, retention and deletion; configure the external deletion-request URL to the privacy page's contact/deletion instructions. The in-app deletion action requires a recent Google sign-in, removes the gift and sign-in account, tester access and associated service sessions. It does not cancel paid Play subscriptions.
6. Test end-to-end on a Play-installed build: purchase, pending payment, cancellation with remaining paid time, expiry, restore after wipe; approved and unapproved Google accounts; switch account; revoke online/offline; wipe/reinstall with same Google account; delete account and verify it cannot automatically regain its removed gift.
7. Reserve a new version code, build/sign the release, check Play Console's current review state and upload to Closed Testing after these prerequisites pass. Do not reuse version 52 for another Play upload. No Play rollout is included in this branch.

## Button verification

All app-owned XML `Button` tags and programmatic `new Button` constructions use `GelButton`, which wraps text, preserves system font scaling and grows vertically. Explicit minima preserve touch targets. Platform/AppCompat dialog buttons receive the same fit policy after show. Image/icon controls and compound controls are deliberately unchanged. The long 30-day information popup scrolls. Speed Test now converts integer dp arguments to physical pixels rather than accidentally selecting the inherited non-density overload.

Run `./gradlew testDebugUnitTest` for Greek/English, font scales 1/1.5/2 and narrow-width text measurement checks. Physical-device visual testing is still required for OEM themes, landscape, dialog reachability and foldables; static coverage does not prove every screen visually correct.
