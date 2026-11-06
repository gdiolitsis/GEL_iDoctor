'use strict';
const {onCall, HttpsError} = require('firebase-functions/v2/https');
const {getFirestore} = require('firebase-admin/firestore');
const {getAuth} = require('firebase-admin/auth');
const {GoogleAuth} = require('google-auth-library');
const {giftKey, verifiedGoogleEmail, giftResult, subscriptionResult} = require('./pro-policy');
const publisherAuth = new GoogleAuth({scopes: ['https://www.googleapis.com/auth/androidpublisher']});
const packageName = 'com.gel.iDoctor';
exports.getGiftProEntitlement = onCall(async request => {
  if (!request.auth) throw new HttpsError('unauthenticated', 'Sign in with Google first.');
  const email = verifiedGoogleEmail(request.auth);
  if (!email) return {active: false, expiresAtMs: 0};
  const grant = await getFirestore().collection('pro_gift_accounts').doc(giftKey(email)).get();
  return giftResult(grant.exists ? grant.data() : null);
});
exports.verifyGelProPurchase = onCall(async request => {
  if (!request.auth) throw new HttpsError('unauthenticated', 'Authentication required.');
  const token = request.data?.purchaseToken;
  if (typeof token !== 'string' || token.length < 10 || token.length > 4096) {
    throw new HttpsError('invalid-argument', 'Invalid purchase token.');
  }
  let purchase;
  let stage = 'credentials';
  try {
    const client = await publisherAuth.getClient();
    stage = 'publisher';
    const result = await client.request({
      url: `https://androidpublisher.googleapis.com/androidpublisher/v3/applications/${packageName}/purchases/subscriptionsv2/tokens/${encodeURIComponent(token)}`,
      timeout: 10000,
    });
    purchase = result.data;
  } catch (error) {
    const status = Number(error.response?.status) || 0;
    const allowedReasons = new Set(['accessNotConfigured','permissionDenied','insufficientPermissions',
      'SERVICE_DISABLED','PERMISSION_DENIED','UNAUTHENTICATED','RATE_LIMIT_EXCEEDED','QUOTA_EXCEEDED']);
    const apiError = error.response?.data?.error;
    const candidates = [apiError?.status, ...(apiError?.errors || []).map(e => e.reason),
      ...(apiError?.details || []).map(e => e.reason)];
    const reason = candidates.find(value => allowedReasons.has(value)) || 'unspecified';
    const networkCode = ['ETIMEDOUT','ECONNRESET','ENOTFOUND','EAI_AGAIN','ECONNABORTED'].includes(error.code)
      ? error.code : 'none';
    // Log only fixed categories. Never log the error, URL, message, token or account.
    console.warn('PRO_PLAY_VERIFY_FAILED', {stage,httpStatus:status,reason,networkCode});
    if ([400, 404, 410].includes(status)) return {active: false, expiresAtMs: 0};
    throw new HttpsError('unavailable', 'Google Play verification is temporarily unavailable.');
  }
  const result = subscriptionResult(purchase);
  await getFirestore().collection('pro_paid_accounts').doc(request.auth.uid)
    .set({verifiedUntil: result.expiresAtMs});
  return result;
});
exports.deleteProGoogleAccount = onCall(async request => {
  const email = verifiedGoogleEmail(request.auth);
  if (!email) throw new HttpsError('unauthenticated', 'Google sign-in required.');
  if (Date.now() / 1000 - Number(request.auth.token.auth_time || 0) > 300) {
    throw new HttpsError('failed-precondition', 'Sign in again before deleting your account.');
  }
  const db = getFirestore();
  // Removes the gift as well, so recreating the account cannot restore a deleted grant.
  await db.collection('pro_gift_accounts').doc(giftKey(email)).delete();
  const testerRef = db.collection('tester_entitlements').doc(request.auth.uid);
  const tester = await testerRef.get();
  const codeId = tester.data()?.activationCodeId;
  if (typeof codeId === 'string' && /^[a-f0-9]{64}$/.test(codeId)) {
    await db.collection('tester_activation_codes').doc(codeId)
      .collection('redemptions').doc(request.auth.uid).delete();
  }
  await testerRef.delete();
  // Remote service history is also account-associated, even though Remote Assist
  // is deferred in the initial release. Delete documents and nested diagnostics.
  for (const field of ['technicianUid', 'customerUid']) {
    const sessions = await db.collection('service_sessions').where(field, '==', request.auth.uid).get();
    for (const session of sessions.docs) await db.recursiveDelete(session.ref);
  }
  const crypto = require('node:crypto');
  const owner = crypto.createHash('sha256').update(request.auth.uid).digest('hex');
  const receipts = await db.collection('report_purchase_receipts').where('owner','==',owner).get();
  for (const receipt of receipts.docs) {
    const data = receipt.data();
    // Retain only the pseudonymous anti-replay receipt, not account references or tokens.
    await receipt.ref.set({owner:data.owner,productId:data.productId,valid:data.valid,createdAt:data.createdAt});
  }
  await db.recursiveDelete(db.collection('report_accounts').doc(request.auth.uid));
  await db.collection('pro_paid_accounts').doc(request.auth.uid).delete();
  await getAuth().deleteUser(request.auth.uid);
  return {deleted: true};
});
