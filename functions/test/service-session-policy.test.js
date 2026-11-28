'use strict';

const test = require('node:test');
const assert = require('node:assert/strict');
const {SERVICE_MAX_MS, REMOTE_CONTROL_ENABLED, serviceActive,
  clampServiceEnd, serviceUntilMs, paidUntil, giftServiceUntil, paidServiceDeadline} = require('../service-session-policy');

test('read-only technician service is exactly eight hours', () => {
  const now = Date.UTC(2026, 10, 25, 9, 0, 0);
  assert.equal(SERVICE_MAX_MS, 28_800_000);
  assert.equal(REMOTE_CONTROL_ENABLED, false);
  const deadline = clampServiceEnd(now, now);
  assert.equal(deadline, now + SERVICE_MAX_MS);
  const connected = {status: 'CONNECTED', serviceEndsAt: {toMillis: () => deadline}};
  assert.equal(serviceActive(connected, deadline - 1), true);
  assert.equal(serviceActive(connected, deadline), false);
  assert.equal(serviceActive(connected, deadline + 1), false);
  assert.equal(serviceActive({...connected, status: 'CANCELLED'}, now), false);
  assert.equal(serviceUntilMs(connected), deadline);
});

test('legacy sessions without server service deadline cannot grant access', () => {
  assert.equal(serviceActive({status: 'CONNECTED'}, Date.now()), false);
  assert.equal(serviceActive({status: 'WAITING'}, Date.now()), false);
  assert.throws(() => clampServiceEnd(0), /connection time/);
});

test('the paid entitlement reader only recognises verified subscriptions', () => {
  const now = Date.UTC(2026, 10, 25, 12, 0);
  assert.equal(paidUntil({enabled:true, permanent:true, type:'TESTER_PRO'}, now), 0);
  assert.equal(paidUntil({active:true, gift:true, expiresAt:now+1e9}, now), 0);
  assert.equal(paidUntil({active:true, trial:true, expiresAt:now+1e9}, now), 0);
  assert.equal(paidUntil({verifiedUntil:now - 1}, now), 0);
  assert.equal(paidUntil({verifiedUntil:now}, now), 0);
  assert.equal(paidUntil({verifiedUntil:now + 3600000}, now), now + 3600000);
  assert.equal(paidUntil({verifiedUntil:'invalid'}, now), 0);
});

test('free customer service ends after eight hours or technician paid expiry', () => {
  const now = Date.UTC(2026, 10, 25, 12, 0);
  const techFull = now + SERVICE_MAX_MS + 12345;
  assert.equal(paidServiceDeadline(now, techFull), now + SERVICE_MAX_MS);
  assert.equal(paidServiceDeadline(now, now + 3600000), now + 3600000);
  assert.equal(paidServiceDeadline(now, now), 0);
  assert.equal(paidServiceDeadline(now, NaN), 0);
  assert.equal(paidServiceDeadline(now, undefined), 0);
  // No customer subscription argument is accepted or needed.
  assert.equal(paidServiceDeadline(now, techFull, 0), now + SERVICE_MAX_MS);
  assert.equal(paidServiceDeadline(now, techFull, undefined), now + SERVICE_MAX_MS);
});

test('permanent verified Google Gift is eligible, revoked/expired gifts are not', () => {
  const now = Date.UTC(2026, 10, 25, 12, 0);
  assert.equal(giftServiceUntil(null, now), 0);
  assert.equal(giftServiceUntil({enabled:false}, now), 0);
  assert.equal(giftServiceUntil({enabled:true}, now), Number.MAX_SAFE_INTEGER);
  assert.equal(giftServiceUntil({enabled:true, expiresAt:{toMillis:()=>now+5000}}, now), now+5000);
  assert.equal(giftServiceUntil({enabled:true, expiresAt:{toMillis:()=>now-1}}, now), 0);
  assert.equal(paidServiceDeadline(now, giftServiceUntil({enabled:true}, now)), now+SERVICE_MAX_MS);
  assert.equal(paidServiceDeadline(now, giftServiceUntil({enabled:true,expiresAt:{toMillis:()=>now+3000}}, now)), now+3000);
});

test('active Tester PRO qualifies and its expiry caps the session', () => {
  const {testerUntil} = require('../pro-policy');
  const now = Date.now();
  assert.equal(testerUntil({enabled: true, expiresAt: {toMillis: () => now}}, now), 0);
  assert.equal(testerUntil({enabled: false, permanent: true}, now), 0);
  assert.equal(testerUntil({enabled: true}, now), 0);
  const grant = {enabled: true, expiresAt: {toMillis: () => now + 3000}};
  assert.equal(paidServiceDeadline(now, testerUntil(grant, now)), now + 3000);
  assert.equal(paidServiceDeadline(now, testerUntil({enabled: true, permanent: true}, now)),
    now + SERVICE_MAX_MS);
});
