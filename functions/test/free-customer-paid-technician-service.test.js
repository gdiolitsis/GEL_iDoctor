'use strict';

const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');

const backend = fs.readFileSync(path.join(__dirname, '..', 'index.js'), 'utf8');
const createStart = backend.indexOf('exports.createServiceSession =');
const claimStart = backend.indexOf('exports.claimServiceSession =');
const cancelStart = backend.indexOf('exports.cancelServiceSession =');

test('only a paid technician can initiate a service session', () => {
  assert.ok(createStart >= 0 && claimStart > createStart);
  const create = backend.substring(createStart, claimStart);
  assert.match(create, /paidSubscriptionUntil\(technicianUid\)/);
  assert.match(create, /permission-denied/);
  assert.match(create, /technicianUid/);
});

test('free authenticated customer only joins an existing technician invitation', () => {
  assert.ok(claimStart >= 0 && cancelStart > claimStart);
  const claim = backend.substring(claimStart, cancelStart);
  assert.match(claim, /requireAuth\(request\)/);
  assert.match(claim, /isSixDigitCode/);
  assert.match(claim, /session\.serviceCode/);
  assert.match(claim, /session\.status/);
  assert.match(claim, /paidUntil\(techPaidSnap\.data\(\)\)/);
  assert.doesNotMatch(claim, /paidSubscriptionUntil\(customerUid\)/);
  assert.doesNotMatch(claim, /customerPaidUntil/);
  assert.match(claim, /paidServiceDeadline\(\s*connectedAtMs, techPaidUntil/);
  assert.doesNotMatch(claim, /\.set\(/);
});

test('customer cannot extend the eight-hour deadline by joining again', () => {
  const claim = backend.substring(claimStart, cancelStart);
  assert.match(claim, /serviceActive\(session\)/);
  assert.match(claim, /serviceUntilMs\(session\)/);
  assert.match(claim, /serviceEndsAt:\s*Timestamp\.fromMillis\(serviceEndsAtMs\)/);
});
