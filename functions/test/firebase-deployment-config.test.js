'use strict';

const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');

const root = path.join(__dirname, '..', '..');

test('v55 Firebase config explicitly deploys the Firestore deny-by-default rules', () => {
  const config = JSON.parse(fs.readFileSync(path.join(root, 'firebase.json'), 'utf8'));
  const projects = JSON.parse(fs.readFileSync(path.join(root, '.firebaserc'), 'utf8'));
  assert.equal(projects.projects.default, 'gel-idoctor');
  assert.equal(config.functions.source, 'functions');
  assert.equal(config.functions.runtime, 'nodejs22');
  assert.equal(config.firestore.rules, 'firestore.rules');
  const rules = fs.readFileSync(path.join(root, config.firestore.rules), 'utf8');
  assert.match(rules, /match \/service_sessions\/\{sessionId\}/);
  assert.match(rules, /match \/diagnostics\/\{diagnosticId\}/);
  assert.match(rules, /serviceEndsAt/);
  assert.match(rules, /allow create, update, delete: if false/);
  assert.match(rules, /allow write: if false/);
  assert.match(rules, /match \/\{document=\*\*\}/);
  assert.match(rules, /allow read, write: if false/);
});
