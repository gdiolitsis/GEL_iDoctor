'use strict';

const test = require('node:test');
const assert = require('node:assert/strict');
const {SERVICE_MAX_MS, REMOTE_CONTROL_ENABLED, serviceActive,
  clampServiceEnd, serviceUntilMs, paidUntil, paidServiceDeadline} = require('../service-session-policy');

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

test('only server-verified paid subscriptions qualify; tester, trial and gift do not', () => {
  const now = Date.UTC(2026, 10, 25, 12, 0);
  assert.equal(paidUntil({enabled:true, permanent:true, type:'TESTER_PRO'}, now), 0);
  assert.equal(paidUntil({active:true, gift:true, expiresAt:now+1e9}, now), 0);
  assert.equal(paidUntil({active:true, trial:true, expiresAt:now+1e9}, now), 0);
  assert.equal(paidUntil({verifiedUntil:now - 1}, now), 0);
  assert.equal(paidUntil({verifiedUntil:now}, now), 0);
  assert.equal(paidUntil({verifiedUntil:now + 3600000}, now), now + 3600000);
  assert.equal(paidUntil({verifiedUntil:'invalid'}, now), 0);
});

test('service ends at eight-hour ceiling or earlier paid subscription expiry', () => {
  const now = Date.UTC(2026, 10, 25, 12, 0);
  const full = now + SERVICE_MAX_MS + 12345;
  assert.equal(paidServiceDeadline(now, full, full), now + SERVICE_MAX_MS);
  assert.equal(paidServiceDeadline(now, now+3600000, full), now+3600000);
  assert.equal(paidServiceDeadline(now, full, now+1800000), now+1800000);
  assert.equal(paidServiceDeadline(now, now, full), 0);
  assert.equal(paidServiceDeadline(now, full, now), 0);
  assert.equal(paidServiceDeadline(now, NaN, full), 0);
});
