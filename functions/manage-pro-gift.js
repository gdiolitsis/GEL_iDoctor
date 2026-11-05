#!/usr/bin/env node
'use strict';
// Run only in a trusted administrator environment with Application Default Credentials.
const {initializeApp, applicationDefault} = require('firebase-admin/app');
const {getFirestore, FieldValue} = require('firebase-admin/firestore');
const {giftKey} = require('./pro-policy');
const [action, email, ...extra] = process.argv.slice(2);
if (!['grant', 'revoke'].includes(action) || !email || extra.length) {
  console.error('Usage: node manage-pro-gift.js grant|revoke GOOGLE_EMAIL'); process.exit(2);
}
const key = giftKey(email);
initializeApp({credential: applicationDefault(), projectId: 'gel-idoctor'});
getFirestore().collection('pro_gift_accounts').doc(key).set({
  enabled: action === 'grant', updatedAt: FieldValue.serverTimestamp(), expiresAt: null,
}).then(() => console.log(action === 'grant' ? 'PRO gift granted.' : 'PRO gift revoked.'))
  .catch(() => { console.error('Admin write failed. Check project and credentials.'); process.exitCode = 1; });
