'use strict';

const test = require('node:test');
const assert = require('node:assert/strict');
const {SERVICE_MAX_MS, REMOTE_CONTROL_ENABLED, serviceActive,
  clampServiceEnd, serviceUntilMs} = require('../service-session-policy');

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
