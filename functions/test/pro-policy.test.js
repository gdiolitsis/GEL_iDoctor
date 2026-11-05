const {test} = require('node:test');
const assert = require('node:assert/strict');
const {giftKey, verifiedGoogleEmail, giftResult, subscriptionResult} = require('../pro-policy');
const now = Date.parse('2026-11-05T12:00:00Z');
test('gift identity uses verified Google token, never client-supplied email', () => {
  assert.equal(verifiedGoogleEmail({token:{email:'a@example.com',email_verified:true,firebase:{sign_in_provider:'password'}}}),null);
  assert.equal(verifiedGoogleEmail({token:{email:'a@example.com',email_verified:false,firebase:{sign_in_provider:'google.com'}}}),null);
  assert.equal(verifiedGoogleEmail({token:{email:'A@Example.com',email_verified:true,firebase:{sign_in_provider:'google.com'}}}),'a@example.com');
  assert.equal(giftKey(' A@Example.com '),giftKey('a@example.com'));
});
test('revoked, missing and expired gifts fail closed; permanent grant has bounded offline lease', () => {
  for (const grant of [null, {}, {enabled:false}, {enabled:true,expiresAt:{toMillis:()=>now-1}}]) {
    assert.equal(giftResult(grant,now).active,false);
  }
  assert.deepEqual(giftResult({enabled:true},now),{active:true,expiresAtMs:now+600000});
});
test('paid pending, expired, paused, on hold and wrong product never unlock', () => {
  const item={productId:'gel_pro_monthly',expiryTime:new Date(now+3600000).toISOString()};
  for (const state of ['PENDING','ON_HOLD','PAUSED','EXPIRED','PENDING_PURCHASE_CANCELED']) {
    assert.equal(subscriptionResult({subscriptionState:'SUBSCRIPTION_STATE_'+state,lineItems:[item]},now).active,false);
  }
  assert.equal(subscriptionResult({subscriptionState:'SUBSCRIPTION_STATE_ACTIVE',lineItems:[{...item,productId:'other'}]},now).active,false);
});
test('active, grace and cancelled-but-not-expired subscriptions unlock until expiry', () => {
  for (const state of ['ACTIVE','IN_GRACE_PERIOD','CANCELED']) {
    const result=subscriptionResult({subscriptionState:'SUBSCRIPTION_STATE_'+state,lineItems:[{productId:'gel_pro_monthly',expiryTime:new Date(now+1000).toISOString()}]},now);
    assert.deepEqual(result,{active:true,expiresAtMs:now+1000});
  }
});
test('prepaid month uses Play expiry across February, never an invented 31-day period', () => {
  const start = Date.parse('2027-02-01T12:00:00Z');
  const expiry = Date.parse('2027-03-01T12:00:00Z');
  const purchase = {subscriptionState:'SUBSCRIPTION_STATE_ACTIVE',lineItems:[{
    productId:'gel_pro_monthly',expiryTime:new Date(expiry).toISOString(),
    prepaidPlan:{},offerDetails:{basePlanId:'prepaid-monthly'}
  }]};
  assert.equal(subscriptionResult(purchase,start).active,true);
  assert.deepEqual(subscriptionResult(purchase,expiry-1000),{active:true,expiresAtMs:expiry});
  assert.deepEqual(subscriptionResult(purchase,expiry),{active:false,expiresAtMs:0});
});
