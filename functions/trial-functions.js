'use strict';
const {onCall,HttpsError}=require('firebase-functions/v2/https');
const {getFirestore}=require('firebase-admin/firestore');
const {verifiedGoogleEmail,giftKey}=require('./pro-policy');
const {TRIAL_MS,refs,matchingUntil}=require('./trial-policy');
const handler=start=>onCall({region:'europe-west1',invoker:'public',timeoutSeconds:30},async request=>{
  const email=verifiedGoogleEmail(request.auth);
  if(!email) throw new HttpsError('unauthenticated','Verified Google sign-in required.');
  const db=getFirestore(),r=refs(db,email,request.data?.deviceId);
  if(!r) throw new HttpsError('invalid-argument','Device identity unavailable.');
  return db.runTransaction(async tx=>{
    const [a,d]=await Promise.all([tx.get(r.account),tx.get(r.device)]);
    const now=Date.now();
    const until=matchingUntil(a.data(),d.data(),email,r.key,now);
    if(until) return {active:true,eligible:false,expiresAtMs:until,serverNowMs:now};
    if(a.exists||d.exists) return {active:false,eligible:false,expiresAtMs:0,serverNowMs:now};
    if(!start) return {active:false,eligible:true,expiresAtMs:0,serverNowMs:now};
    const expiresAt=now+TRIAL_MS;
    // No UID dependency: reinstalling or recreating an Auth account cannot renew this grant.
    tx.set(r.account,{device:r.key,startedAt:now,expiresAt});
    tx.set(r.device,{account:giftKey(email),startedAt:now,expiresAt});
    return {active:true,eligible:false,expiresAtMs:expiresAt,serverNowMs:now};
  });
});
exports.startGelProTrial=handler(true);
exports.getGelProTrial=handler(false);
