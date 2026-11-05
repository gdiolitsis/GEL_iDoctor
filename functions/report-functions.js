'use strict';
// Private account ledger. No client is allowed to write purchase balances or report grants.
const {onCall, HttpsError} = require('firebase-functions/v2/https');
const {onMessagePublished} = require('firebase-functions/v2/pubsub');
const {getFirestore} = require('firebase-admin/firestore');
const {GoogleAuth} = require('google-auth-library');
const crypto = require('node:crypto');
const {verifiedGoogleEmail, giftKey, giftResult} = require('./pro-policy');
const publisher = new GoogleAuth({scopes:['https://www.googleapis.com/auth/androidpublisher']});
const hash = value => crypto.createHash('sha256').update(value).digest('hex');
const callable = fn => onCall({timeoutSeconds:60},fn);
const SKUS = ['gel_single_report', 'gel_custom_reports'];
const PACKAGE = 'com.gel.iDoctor';
function identity(request) {
  const email = verifiedGoogleEmail(request.auth);
  if (!email) throw new HttpsError('unauthenticated', 'Sign in with Google to restore report purchases.');
  return {uid:request.auth.uid, email};
}
function reportId(request) {
  const value = request.data?.reportId;
  if (typeof value !== 'string' || !/^[a-f0-9]{64}$/.test(value)) {
    throw new HttpsError('invalid-argument', 'Invalid report identity.');
  }
  return value;
}
function attemptId(request) {
  const value = request.data?.attemptId;
  if (typeof value !== 'string' || !/^[a-f0-9-]{36}$/.test(value)) throw new HttpsError('invalid-argument','Invalid export attempt.');
  return value;
}
async function playRequest(url, method = 'GET') {
  try {
    const client = await publisher.getClient();
    return (await client.request({url, method, ...(method === 'POST' ? {data:{}} : {}), timeout:10000})).data;
  } catch (error) {
    // Never log publisher errors: their request configuration includes the token.
    if ([400,404,410].includes(error.response?.status)) throw new HttpsError('failed-precondition','Purchase is not valid.');
    throw new HttpsError('unavailable', 'Google Play verification unavailable. Retry Restore.');
  }
}
function ledger(db, uid) { return db.collection('report_accounts').doc(uid); }
function view(data = {}) { return {credits:Math.max(0, Number(data.credits)||0), unlimited:data.unlimited === true}; }
async function requirePro(db, uid, email) {
  const [paid, gift, tester] = await Promise.all([
    db.collection('pro_paid_accounts').doc(uid).get(),
    db.collection('pro_gift_accounts').doc(giftKey(email)).get(),
    db.collection('tester_entitlements').doc(uid).get(),
  ]);
  const t = tester.data() || {};
  if (Number(paid.data()?.verifiedUntil) > Date.now() || giftResult(gift.data()).active ||
      (t.enabled === true && (t.permanent === true || (t.expiresAt?.toMillis?.() || 0) > Date.now()))) return;
  throw new HttpsError('failed-precondition', 'Active PRO is required. Restore PRO first.');
}
exports.getReportEntitlements = callable(async request => {
  const {uid} = identity(request);
  const data = (await ledger(getFirestore(),uid).get()).data() || {};
  // Recheck permanent ownership on restoration as a fallback to refund notifications.
  if (data.unlimitedPurchaseToken) {
    try { await verifyForAccount(uid,'gel_custom_reports',data.unlimitedPurchaseToken); }
    catch (error) { if (error.code !== 'failed-precondition') throw error; }
  }
  return view((await ledger(getFirestore(),uid).get()).data());
});
async function verifyForAccount(uid, sku, token) {
  if (!SKUS.includes(sku) || typeof token !== 'string' || token.length < 10 || token.length > 4096) {
    throw new HttpsError('invalid-argument','Invalid report purchase.');
  }
  const url = `https://androidpublisher.googleapis.com/androidpublisher/v3/applications/${PACKAGE}/purchases/products/${sku}/tokens/${encodeURIComponent(token)}`;
  const purchase = await playRequest(url);
  const db = getFirestore();
  const receipt = db.collection('report_purchase_receipts').doc(hash(token));
  const account = ledger(db,uid);
  const owner = hash(uid);
  if (purchase.quantity != null && purchase.quantity !== 1) throw new HttpsError('failed-precondition','Multi-quantity report purchases are not supported.');
  const valid = purchase.purchaseState === 0;
  const result = await db.runTransaction(async tx => {
    const [r,a] = await Promise.all([tx.get(receipt),tx.get(account)]);
    const old = r.data(); const data = a.data() || {};
    if (old && (old.owner !== owner || old.productId !== sku)) throw new HttpsError('permission-denied','Purchase belongs to another account.');
    if (purchase.obfuscatedExternalAccountId && purchase.obfuscatedExternalAccountId !== owner) {
      throw new HttpsError('permission-denied','Purchase account does not match.');
    }
    // Do not silently import pre-ledger consumables: they may already have been spent locally.
    if (!old && sku === 'gel_single_report' && purchase.consumptionState !== 0) {
      throw new HttpsError('failed-precondition','Legacy consumed purchase requires support review.');
    }
    if (!valid) {
      if (old && old.valid !== false) {
        if (sku === 'gel_single_report') data.credits = (Number(data.credits)||0) - 1;
        else { data.unlimited = false; delete data.unlimitedPurchaseToken; }
        tx.set(account,data); tx.set(receipt,{...old,valid:false});
      }
      return {valid:false,...view(data)};
    }
    if (old?.valid === false) return {valid:false,...view(data)};
    if (!old) {
      if (sku === 'gel_single_report') data.credits = (Number(data.credits)||0) + 1;
      else { data.unlimited = true; data.unlimitedPurchaseToken = token; }
      tx.set(account,data);
      // Hash-only receipt remains after account deletion to prevent replay/re-credit.
      tx.set(receipt,{owner,accountId:uid,productId:sku,valid:true,createdAt:Date.now()});
    }
    return {valid:true,...view(data)};
  });
  if (!result.valid) throw new HttpsError('failed-precondition','Purchase is pending, cancelled or refunded.');
  // Persist fulfillment FIRST. If finalization fails, a retry uses the same receipt without crediting twice.
  if (sku === 'gel_single_report' && purchase.consumptionState === 0) await playRequest(url+':consume','POST');
  if (sku === 'gel_custom_reports' && purchase.acknowledgementState !== 1) await playRequest(url+':acknowledge','POST');
  return result;
}
exports.verifyReportPurchase = callable(async request => {
  const {uid} = identity(request);
  return verifyForAccount(uid, request.data?.productId, request.data?.purchaseToken);
});
// Configure Play RTDN to this topic. Only Google's Play notification service gets publish rights.
exports.handleReportPurchaseNotification = onMessagePublished({topic:'gel-play-report-events',retry:true,timeoutSeconds:60}, async event => {
  const message = event.data.message.json;
  if (message.packageName !== PACKAGE) return;
  if (message.testNotification) {
    // Acknowledge Play's connectivity probe without logging the payload or touching purchases.
    console.info('RTDN test notification received successfully.');
    return;
  }
  const notification = message.oneTimeProductNotification || message.voidedPurchaseNotification;
  const token = notification?.purchaseToken;
  if (typeof token !== 'string' || token.length < 10 || token.length > 4096) return;
  const receipt = await getFirestore().collection('report_purchase_receipts').doc(hash(token)).get();
  const data = receipt.data();
  if (!data?.accountId || !SKUS.includes(data.productId)) return;
  try { await verifyForAccount(data.accountId,data.productId,token); }
  catch (error) {
    if (error.code === 'unavailable') throw error; // Pub/Sub retry; no token in logs.
    if (error.code !== 'failed-precondition') throw new Error('Report notification could not be applied.');
  }
});
exports.prepareReportExport = callable(async request => {
  const {uid,email} = identity(request); const id = reportId(request); const db = getFirestore();
  await requirePro(db,uid,email);
  const mode = request.data?.mode;
  const attempt = attemptId(request);
  const reservationKey = id + ':' + attempt;
  if (!['single','unlimited'].includes(mode)) throw new HttpsError('invalid-argument','Invalid export mode.');
  const account = ledger(db,uid); const report = account.collection('reports').doc(id);
  return db.runTransaction(async tx => {
    const [a,r] = await Promise.all([tx.get(account),tx.get(report)]);
    const data = a.data() || {}; const now = Date.now();
    if (mode === 'unlimited') {
      if (!data.unlimited) throw new HttpsError('permission-denied','Unlimited personalisation not owned.');
      return {allowed:true,...view(data)};
    }
    if (r.data()?.completed === true) return {allowed:true,completed:true,...view(data)};
    const reservations = Object.fromEntries(Object.entries(data.reservations || {}).filter(([,until])=>until>now));
    if (!reservations[reservationKey] && (Number(data.credits)||0) <= Object.keys(reservations).length) {
      throw new HttpsError('resource-exhausted','No available report credits.');
    }
    reservations[reservationKey] = now + 15*60*1000;
    tx.set(account,{...data,reservations});
    tx.set(report.collection('attempts').doc(attempt),{authorisedAt:now});
    return {allowed:true,completed:false,...view(data)};
  });
});
exports.finishReportExport = callable(async request => {
  const {uid} = identity(request); const id = reportId(request); const db = getFirestore();
  const attempt = attemptId(request);
  const reservationKey = id + ':' + attempt;
  const success = request.data?.success;
  if (typeof success !== 'boolean') throw new HttpsError('invalid-argument','Export result required.');
  const account = ledger(db,uid); const report = account.collection('reports').doc(id);
  return db.runTransaction(async tx => {
    const [a,r,proof] = await Promise.all([tx.get(account),tx.get(report),tx.get(report.collection('attempts').doc(attempt))]);
    const data = a.data() || {}; const reservations = {...(data.reservations || {})};
    if (r.data()?.completed === true) {
      delete reservations[reservationKey]; tx.set(account,{...data,reservations});
      return view(data); // replay-safe completion, release a concurrent duplicate hold
    }
    if (success) {
      if (!reservations[reservationKey] && !proof.exists) throw new HttpsError('failed-precondition','No reserved report credit.');
      data.credits = (Number(data.credits)||0) - 1;
      tx.set(report,{completed:true,createdAt:Date.now()});
    }
    delete reservations[reservationKey]; tx.set(account,{...data,reservations});
    return view(data);
  });
});
