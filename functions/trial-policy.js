'use strict';
const crypto = require('node:crypto');
const {giftKey} = require('./pro-policy');
const TRIAL_MS = 60 * 60 * 1000;
function deviceKey(value) {
  if (typeof value !== 'string' || !/^[a-f0-9]{16}$/i.test(value) || /^0+$/.test(value)) return null;
  return crypto.createHash('sha256').update('gel-trial-device:' + value.toLowerCase()).digest('hex');
}
function refs(db,email,deviceId) {
  const key = deviceKey(deviceId);
  return key ? {account:db.collection('pro_trial_accounts').doc(giftKey(email)),
    device:db.collection('pro_trial_devices').doc(key),key} : null;
}
function matchingUntil(account,device,email,key,now) {
  if (!account || !device || account.device !== key || device.account !== giftKey(email) ||
      account.startedAt !== device.startedAt || account.expiresAt !== device.expiresAt ||
      !Number.isFinite(account.startedAt) || !Number.isFinite(account.expiresAt) ||
      account.startedAt > now || account.expiresAt - account.startedAt !== TRIAL_MS) return 0;
  return account.expiresAt > now ? account.expiresAt : 0;
}
async function trialUntil(db,email,deviceId,now=Date.now()) {
  const r=refs(db,email,deviceId); if(!r) return 0;
  const [a,d]=await Promise.all([r.account.get(),r.device.get()]);
  return matchingUntil(a.data(),d.data(),email,r.key,now);
}
module.exports={TRIAL_MS,deviceKey,refs,matchingUntil,trialUntil};
