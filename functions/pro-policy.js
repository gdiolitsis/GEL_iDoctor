'use strict';
const crypto = require('node:crypto');
const GIFT_LEASE_MS = 10 * 60 * 1000;
function giftKey(email) {
  if (typeof email !== 'string' || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
    throw new Error('A valid Google email is required.');
  }
  return crypto.createHash('sha256').update(email.trim().toLowerCase()).digest('hex');
}
function verifiedGoogleEmail(auth) {
  const token = auth && auth.token;
  if (!token || token.email_verified !== true ||
      token.firebase?.sign_in_provider !== 'google.com') return null;
  try { giftKey(token.email); return token.email.trim().toLowerCase(); }
  catch { return null; }
}
function giftResult(grant, now = Date.now()) {
  const until = grant?.expiresAt?.toMillis?.() || 0;
  const active = grant?.enabled === true && (!until || until > now);
  return {active, expiresAtMs: active ? Math.min(now + GIFT_LEASE_MS, until || Infinity) : 0};
}
function subscriptionResult(purchase, now = Date.now()) {
  const states = ['SUBSCRIPTION_STATE_ACTIVE', 'SUBSCRIPTION_STATE_IN_GRACE_PERIOD',
    'SUBSCRIPTION_STATE_CANCELED'];
  if (!purchase || !states.includes(purchase.subscriptionState)) return {active: false, expiresAtMs: 0};
  const expiry = Math.max(0, ...(purchase.lineItems || [])
    .filter(item => item.productId === 'gel_pro_monthly')
    .map(item => Date.parse(item.expiryTime) || 0));
  return {active: expiry > now, expiresAtMs: expiry > now ? Math.min(expiry, now + 86400000) : 0};
}
module.exports = {giftKey, verifiedGoogleEmail, giftResult, subscriptionResult};
