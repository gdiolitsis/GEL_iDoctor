'use strict';

const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');

const backend = fs.readFileSync(path.join(__dirname, '..', 'index.js'), 'utf8');
const createStart = backend.indexOf('exports.createServiceSession =');
const claimStart = backend.indexOf('exports.claimServiceSession =');
const cancelStart = backend.indexOf('exports.cancelServiceSession =');

test('paid, Gift and Tester technician access is checked on the server at session creation', () => {
  assert.ok(createStart >= 0 && claimStart > createStart);
  const grant = backend.substring(backend.indexOf('async function technicianServiceGrant'), createStart);
  assert.match(grant, /db\.collection\(TESTER_ENTITLEMENT_COLLECTION\)\.doc\(uid\)\.get\(\)/);
  assert.match(grant, /testerUntil\(tester\.data\(\)\)/);
  const create = backend.substring(createStart, claimStart);
  assert.match(create, /technicianServiceGrant\(request\.auth\)/);
  assert.match(create, /technicianGiftKey: serviceGrant\.giftHash/);
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
  assert.match(claim, /giftServiceUntil\(techGiftSnap\.data\(\)\)/);
  assert.match(claim, /session\.technicianGiftKey/);
  assert.match(claim, /tx\.get\(db\.collection\(TESTER_ENTITLEMENT_COLLECTION\)\.doc\(session\.technicianUid\)\)/);
  assert.match(claim, /testerUntil\(techTesterSnap\.data\(\)\)/);
  assert.doesNotMatch(claim, /paidSubscriptionUntil\(customerUid\)/);
  assert.doesNotMatch(claim, /customerPaidUntil/);
  assert.match(claim, /paidServiceDeadline\(\s*connectedAtMs, techEntitlementUntil/);
  assert.doesNotMatch(claim, /\.set\(/);
});

test('customer cannot extend the eight-hour deadline by joining again', () => {
  const claim = backend.substring(claimStart, cancelStart);
  assert.match(claim, /serviceActive\(session\)/);
  assert.match(claim, /serviceUntilMs\(session\)/);
  assert.match(claim, /serviceEndsAt:\s*Timestamp\.fromMillis\(serviceEndsAtMs\)/);
});
