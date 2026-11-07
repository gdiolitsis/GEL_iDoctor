# PRO access and release prerequisites

This branch is preparation, not a deployed or Play-verified release.

## Access paths

- Paid `gel_pro_monthly`: Play purchase or Restore -> Firebase callable -> Android Publisher `subscriptionsv2.get` -> matching product and valid subscription state/expiry -> local access -> acknowledgement. Pending purchases do not unlock. A verified offline lease lasts at most 24 hours and never past Play expiry.
- Gift: optional Google sign-in -> verified Firebase Google email -> private `pro_gift_accounts/{sha256(trimmed lowercase email)}` -> `enabled: true`. No payment or recurring subscription. Each lease lasts at most ten minutes; foreground activities refresh every five minutes and on entering a screen. Disabling/removing an entry stops renewal. Existing offline access ends within ten minutes under the normal device clock. A modified/rooted client is outside this cache guarantee.
- Existing tester codes remain a separate closed-test mechanism. Signing out/switching to Google clears the old tester cache. Paid purchases are independent of app Google sign-in.
- Every enabled gift grants FULL ACCESS: all PRO features and unlimited personalised service reports with the recipient’s own logo. Omit `expiresAt` for permanent access. This applies to existing and future gift entries; no subscription, report purchase or credit is required. Sign in with the approved Google account inside the app. Downloading from Play alone does not reveal that identity to the app.

## Required cloud configuration BEFORE shipping

1. Firebase Authentication: enable Google provider for `gel-idoctor`; configure support email. Add the Play App Signing SHA fingerprints (and debug fingerprints only for debug testing). Download the updated `app/google-services.json`. The configuration now includes the supplied Play SHA-1 and Android/web OAuth clients. Live sign-in still requires a new Play-installed build.
2. Enable Google Play Android Developer API. Grant the deployed functions runtime service account only the required app-scoped permission to read purchases/subscriptions for `com.gel.iDoctor`. Do not put a service-account key into the Android app or Git.
3. From `functions/`, run `npm ci`, `npm test`; deploy new callables `getGiftProEntitlement`, `verifyGelProPurchase`, `deleteProGoogleAccount` with a trusted Firebase administrator. Existing Firestore default-deny rules already prevent client reads/writes of gifts and entitlements. No public admin endpoint is provided.
4. With trusted Application Default Credentials for this project, manage gifts:

   ```sh
   cd functions
   node manage-pro-gift.js grant person@example.com
   node manage-pro-gift.js revoke person@example.com
   ```

   The admin tool writes only an email hash, enabled flag and timestamps. Do not grant accounts until the owner supplies the exact intended Google email addresses. Revoking a gift does not revoke a separately paid Play purchase.
5. Review and publish updated privacy disclosure. Update Play Data Safety for optional account identity/email/profile, purchase verification, retention and deletion; configure the external deletion-request URL to the privacy page's contact/deletion instructions. The in-app deletion action requires a recent Google sign-in, removes the sign-in account, tester access and associated service sessions. Developer-issued gift eligibility is retained for the same verified Google identity; signing in again restores an enabled gift. Only an administrator can revoke a gift. It does not cancel paid Play subscriptions.
6. Test end-to-end on a Play-installed build: purchase, pending payment, cancellation with remaining paid time, expiry, restore after wipe; approved and unapproved Google accounts; switch account; revoke online/offline; wipe/reinstall with same Google account; delete account and verify associated data are removed, then sign in with a new Auth UID for the same Google identity and verify the enabled gift is restored. Other Google identities and administrator-revoked gifts must remain locked.
7. Reserve a new version code, build/sign the release, check Play Console's current review state and upload to Closed Testing after these prerequisites pass. Do not reuse version 52 for another Play upload. No Play rollout is included in this branch.

## Button verification

All app-owned XML `Button` tags and programmatic `new Button` constructions use `GelButton`, which wraps text, preserves system font scaling and grows vertically. Explicit minima preserve touch targets. Platform/AppCompat dialog buttons receive the same fit policy after show. Image/icon controls and compound controls are deliberately unchanged. The long 30-day information popup scrolls. Speed Test now converts integer dp arguments to physical pixels rather than accidentally selecting the inherited non-density overload.

Run `./gradlew testDebugUnitTest` for Greek/English, font scales 1/1.5/2 and narrow-width text measurement checks. Physical-device visual testing is still required for OEM themes, landscape, dialog reachability and foldables; static coverage does not prove every screen visually correct.

## Dependency security maintenance (2026-11-05)

Firebase Admin 14.5.0, Firebase Functions 7.4.0 and Google Auth Library 11.3.3 replace the vulnerable dependency chains. The legacy Firestore API package still uses google-gax 5.0.8, so rimraf is overridden to 6.1.4 (patched glob/minimatch chain). A top-level rimraf override is required: the version-scoped parent override was accepted by npm 11/12 but made npm 10 reject the lockfile during Cloud Build. CI installs the lockfile, audits at moderate severity and loads the Functions module under Node 22 with npm 10.9.4 before running the access tests.


## Monthly access and report personalisation

Both base plans belong to subscription `gel_pro_monthly`: `monthly` is auto-renewing P1M; `prepaid-monthly` is prepaid P1M without automatic renewal. Configure both at EUR 4.99 in Play Console before testing. The app selects the exact base plan and never substitutes another renewal type. Do not use a fixed 30/31-day expiry: server verification uses Play expiryTime for either plan.

Active PRO includes all labs and unlimited GEL-branded report exports. `gel_single_report` is a EUR 4.99 consumable for one personalised report; `gel_custom_reports` is a EUR 29.99 non-consumable for unlimited personalisation while PRO is active. Both report purchases require active PRO. Existing unlimited ownership is retained when PRO expires. Gift access includes unlimited personalisation without payment. Tester PRO permits feature access but does not itself grant unlimited personalisation. Gift report access is computed from the private grant on each request, never stored as purchased ownership; removal of a gift preserves separately purchased rights.

A successful single report export records its content/profile identity alongside the credit debit. Retrying the same content/profile does not debit again; changed content/profile is another report. Failed/cancelled exports do not invoke the debit. Single credits currently use the direct PDF export; the Android HTML print flow cannot confirm successful file creation and remains excluded from credit consumption. Credits, report fingerprints and unlimited ownership are authoritative in private Firestore account documents. Google sign-in is required for report purchases and personalisation; restoring the same account recovers the ledger after reinstall. Local legacy balances are never auto-imported, since consumed Play purchases cannot prove an unspent balance. Review any real legacy buyer manually before migration.

Manual trial: prepaid purchase/expiry/restore, recurring purchase/cancellation/expiry, no-PRO export denied even with credits, GEL-only branding with PRO, single purchase success/cancel/error, successful PDF retry without extra debit, changed report requires another credit, unlimited restore after PRO expires and is renewed, Greek/English at font scale 2 and narrow display. No Play rollout until these checks and product configuration are completed.


## Account-backed report ledger deployment

Deploy `verifyGelProPurchase` and `deleteProGoogleAccount` again, plus `getReportEntitlements`, `verifyReportPurchase`, `prepareReportExport`, `finishReportExport`, and `handleReportPurchaseNotification`. All report callables require verified Google authentication. Paid PRO verification writes a bounded server lease; gift/tester access is checked from private grants. Firestore default-deny keeps all report collections private.

Server fulfillment verifies `purchases.products.get`, binds the token hash to one account, grants in a Firestore transaction before calling Play consume/acknowledge, and returns the same balance on retry. Only quantity 1 is supported: disable multi-quantity for both products. New report purchases pass a SHA-256 account identifier. Consumed legacy purchases without a ledger are rejected rather than credited twice.

Single exports reserve one available credit for 15 minutes; cancellation or failure releases it without a debit. Successful file creation queues an authenticated completion on the device; retries and concurrent completion are idempotent. Expired holds release capacity after an interrupted process. An authorisation proof allows a delayed successful completion to reconcile later, including after another credit was used; any resulting debt blocks further new reports until settled. Exported fingerprints are retained on the server, so unchanged reports can be authorised again without another credit. Report contents, logos and client details are not uploaded to this ledger.

Configure Play real-time developer notifications to `projects/gel-idoctor/topics/gel-play-report-events`, including one-time product and voided-purchase notifications. Grant Publisher on this topic only to Google's documented Play notification service account (`google-play-developer-notifications@system.gserviceaccount.com`), not the public. The handler re-verifies Play state and revokes refunded ownership/credits idempotently. Restore also rechecks unlimited ownership. End-to-end notification delivery is a required live check.

Account deletion removes the ledger, tokens, report hashes and paid lease. Minimal token-hash/owner-hash/product/status receipts remain to prevent replay of already fulfilled purchases; no email, raw token or account reference remains in those receipts. Deleting the account forfeits its report credits/grants and does not cancel the separate Play subscription.

Deployment alone does not configure Play products, Publisher permissions, topic publishing or Data Safety. Confirm all in the live project before charging users. Do not publish a Play release as part of setup.

## One-hour FULL ACCESS trial

Deploy startGelProTrial, getGelProTrial, prepareReportExport and finishReportExport,
plus Hosting for the privacy disclosure. The new callables have a public invoker;
verified Firebase Google authentication is enforced inside the handlers.
Private pro_trial_accounts and pro_trial_devices collections are covered by default-deny
Firestore rules. Store only hashed email/device keys and server start/expiry timestamps.
Do not delete these eligibility records during normal app-account deletion.

One fixed 60-minute trial per verified Google identity AND Android device identifier.
Concurrent starts, reinstall, a new Firebase UID and another account on the same device
cannot create another trial for the same identifiers. Active trials resume their original expiry.
The client obtains at most a 60-second monotonic lease and refreshes about every 30 seconds
while its activity is resumed. Process restart requires server refresh. Internet is required.
Trial personalised exports do not consume credits or create permanent purchased ownership.
An export authorised before expiry may complete after expiry without billing a credit.

ANDROID_ID is scoped to Android profile and signing key: factory reset, another Android
profile, upload-key APK versus Play-signing-key APK, or modified clients can evade device-only
matching. The verified-email limit remains independent. This is not hardware attestation;
no promise of absolute prevention is made. Consider Play Integrity/Device Recall when
available before advertising stronger device recognition. Shared/second-hand device disputes
require developer support; no public reset endpoint is provided.

Update Play Data Safety disclosures for trial device identifiers and server-retained
anti-abuse eligibility before publishing. No card or automatic subscription is created.
Test fresh/consumed account and device, concurrent start, cancel sign-in, expiry, clock changes,
offline, process restart, account deletion/recreation, credit preservation and trial PDF export.
