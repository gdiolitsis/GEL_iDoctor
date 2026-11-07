'use strict';
const {test} = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const crypto = require('node:crypto');
const auth = {uid:'owner',token:{email:'owner@example.com',email_verified:true,firebase:{sign_in_provider:'google.com'}}};
const other = {uid:'other',token:{...auth.token,email:'other@example.com'}};
const reportId = 'a'.repeat(64), another = 'b'.repeat(64);
const attemptId = '11111111-1111-1111-1111-111111111111';
const attempt2 = '22222222-2222-2222-2222-222222222222';
function setup() {
  const docs = new Map(); const calls=[]; const logs=[];
  let purchase={purchaseState:0,consumptionState:0,acknowledgementState:0};
  let failFinalize=false; let chain=Promise.resolve();
  const ref = path => ({path,collection:name=>collection(path+'/'+name),get:async()=>snap(path)});
  const collection = path => ({doc:id=>ref(path+'/'+id)});
  const snap=path=>({exists:docs.has(path),data:()=>{
    const data=docs.get(path);
    // Firestore timestamps retain their toMillis method in real snapshots.
    if (data?.expiresAt?.toMillis) return {...structuredClone({...data,expiresAt:undefined}),expiresAt:data.expiresAt};
    return structuredClone(data);
  }});
  const db={collection,runTransaction:fn=>{
    const run=chain.then(async()=>{
      const writes=new Map();
      const tx={get:async r=>snap(r.path),set:(r,data)=>writes.set(r.path,structuredClone(data))};
      const value=await fn(tx);for(const [k,v] of writes)docs.set(k,v);return value;
    });chain=run.catch(()=>{});return run;
  }};
  const exports={};
  vm.runInNewContext(fs.readFileSync(require.resolve('../report-functions'),'utf8'),{
    exports,Date,encodeURIComponent,console:{info:(...args)=>logs.push(args)},
    require:name=>({
      'firebase-functions/v2/https':{onCall:(options,fn)=>fn||options,HttpsError:class extends Error{constructor(code,message){super(message);this.code=code;}}},
      'firebase-admin/firestore':{getFirestore:()=>db},
      'firebase-functions/v2/pubsub':{onMessagePublished:(options,fn)=>{
        assert.match(options.topic,/^[A-Za-z][A-Za-z0-9._~+%-]{2,254}$/);
        assert.equal(options.topic.startsWith('goog'),false,'Pub/Sub reserves the goog prefix');
        return fn;
      }},
      'node:crypto':crypto,'./pro-policy':require('../pro-policy'),'./trial-policy':require('../trial-policy'),
      'google-auth-library':{GoogleAuth:class{async getClient(){return {request:async options=>{
        calls.push(options);if(options.method==='POST'&&failFinalize)throw {response:{status:503}};
        return {data:purchase};
      }}}}},
    })[name],
  });
  const req=(data={},who=auth)=>({auth:who,data});
  const buy=(sku='gel_single_report',token='purchase-token-123',who=auth)=>exports.verifyReportPurchase(req({productId:sku,purchaseToken:token},who));
  const prepare=(id=reportId,attempt=attemptId,mode='single')=>exports.prepareReportExport(req({reportId:id,attemptId:attempt,mode}));
  const finish=(success,id=reportId,attempt=attemptId)=>exports.finishReportExport(req({reportId:id,attemptId:attempt,success}));
  const balance=()=>exports.getReportEntitlements(req());
  docs.set('pro_paid_accounts/owner',{verifiedUntil:Date.now()+60000});
  return {exports,docs,calls,logs,req,buy,prepare,finish,balance,setPurchase:p=>{purchase=p;},failFinalize:()=>{failFinalize=true;}};
}
test('Play connectivity probe logs acknowledgement without payload, purchase verification or ledger writes',async()=>{
  const s=setup(); const before=[...s.docs];
  const message={packageName:'com.gel.iDoctor',testNotification:{version:'1.0'},purchaseToken:'must-not-be-logged'};
  await s.exports.handleReportPurchaseNotification({data:{message:{json:message}}});
  assert.deepEqual(s.logs,[['RTDN test notification received successfully.']]);
  assert.deepEqual([...s.docs],before);
  assert.equal(s.calls.length,0);
  await s.exports.handleReportPurchaseNotification({data:{message:{json:{...message,packageName:'another.app'}}}});
  assert.equal(s.logs.length,1);
});
test('permanent gift restores full access without purchases or spending report credits',async()=>{
  const s=setup(); const key=require('../pro-policy').giftKey(auth.token.email);
  s.docs.delete('pro_paid_accounts/owner');
  s.docs.set('pro_gift_accounts/'+key,{enabled:true});
  s.docs.set('report_accounts/owner',{credits:2});
  const before=structuredClone([...s.docs]);
  assert.equal((await s.balance()).unlimited,true);
  for (const mode of ['single','unlimited']) {
    const result=await s.prepare(reportId,attemptId,mode);
    assert.equal(result.allowed,true);assert.equal(result.unlimited,true);
    await s.finish(true);
  }
  assert.equal((await s.prepare(another,attempt2,'unlimited')).allowed,true);
  assert.equal((await s.balance()).credits,2);
  assert.deepEqual([...s.docs],before);
  assert.equal(s.calls.length,0);
});
test('gift full access uses verified non-Gmail identity and cannot be spoofed by client data',async()=>{
  const s=setup(); const email='gift-recipient@hotmail.com';
  const key=require('../pro-policy').giftKey(email);
  s.docs.set('pro_gift_accounts/'+key,{enabled:true});
  const who={uid:'hotmail-owner',token:{...auth.token,email}};
  assert.equal((await s.exports.getReportEntitlements(s.req({},who))).unlimited,true);
  assert.equal((await s.exports.getReportEntitlements(s.req({email},other))).unlimited,false);
  await assert.rejects(s.exports.getReportEntitlements(s.req({}, {uid:'anonymous',token:{email,email_verified:true,firebase:{sign_in_provider:'anonymous'}}})),e=>e.code==='unauthenticated');
});
test('revoked or expired gift never becomes permanent purchased report ownership',async()=>{
  for (const grant of [{enabled:false},{enabled:true,expiresAt:{toMillis:()=>Date.now()-1}}]) {
    const s=setup(); const path='pro_gift_accounts/'+require('../pro-policy').giftKey(auth.token.email);
    s.docs.set(path,{enabled:true});assert.equal((await s.balance()).unlimited,true);
    s.docs.set(path,grant);assert.equal((await s.balance()).unlimited,false);
    await assert.rejects(s.prepare(reportId,attemptId,'unlimited'),e=>e.code==='permission-denied');
    assert.equal(s.docs.has('report_accounts/owner'),false);
  }
});
test('gift removal preserves independently purchased unlimited ownership',async()=>{
  const s=setup();await s.buy('gel_custom_reports');
  const path='pro_gift_accounts/'+require('../pro-policy').giftKey(auth.token.email);
  s.docs.set(path,{enabled:true});assert.equal((await s.balance()).unlimited,true);
  s.docs.delete(path);assert.equal((await s.balance()).unlimited,true);
});
test('purchase grant survives restore and duplicate/concurrent token submissions credit once',async()=>{
  const s=setup();await Promise.all([s.buy(),s.buy(),s.buy()]);
  assert.equal((await s.balance()).credits,1);
  // A new client asks the same account ledger, without local preferences or purchase history.
  assert.equal((await s.exports.getReportEntitlements(s.req())).credits,1);
  await assert.rejects(s.buy('gel_single_report','purchase-token-123',other),e=>e.code==='permission-denied');
});
test('pending/cancelled/wrong owner/unknown product and anonymous requests never grant',async()=>{
  for(const state of [1,2]){const s=setup();s.setPurchase({purchaseState:state,consumptionState:0});await assert.rejects(s.buy());assert.equal((await s.balance()).credits,0);}
  const s=setup();s.setPurchase({purchaseState:0,consumptionState:0,obfuscatedExternalAccountId:'wrong-owner'});
  await assert.rejects(s.buy(),e=>e.code==='permission-denied');
  await assert.rejects(s.buy('unknown'),e=>e.code==='invalid-argument');
  await assert.rejects(s.exports.getReportEntitlements({auth:{uid:'anon',token:{}}}),e=>e.code==='unauthenticated');
});
test('fulfillment failure persists the receipt; retries cannot mint another credit',async()=>{
  const s=setup();s.failFinalize();await assert.rejects(s.buy(),e=>e.code==='unavailable');
  await assert.rejects(s.buy(),e=>e.code==='unavailable');assert.equal((await s.balance()).credits,1);
});
test('legacy consumed tokens are not imported as fresh unspent credits',async()=>{
  const s=setup();s.setPurchase({purchaseState:0,consumptionState:1});await assert.rejects(s.buy());
  assert.equal((await s.balance()).credits,0);
});
test('failed/cancelled export releases a reservation without spending; completion is idempotent',async()=>{
  const s=setup();await s.buy();await s.prepare();assert.equal((await s.balance()).credits,1);
  await s.finish(false);assert.equal((await s.balance()).credits,1);
  await s.prepare();await Promise.all([s.finish(true),s.finish(true)]);
  assert.equal((await s.balance()).credits,0);
  assert.equal((await s.prepare()).completed,true);
  await assert.rejects(s.prepare(another),e=>e.code==='resource-exhausted');
});
test('one credit cannot reserve two reports or two simultaneous export attempts',async()=>{
  const s=setup();await s.buy();
  const results=await Promise.allSettled([s.prepare(),s.prepare(another,attempt2)]);
  assert.equal(results.filter(r=>r.status==='fulfilled').length,1);
  await assert.rejects(s.prepare(reportId,attempt2),e=>e.code==='resource-exhausted');
  // Cancelling a different attempt cannot release the original hold.
  await s.finish(false,reportId,attempt2);
  await assert.rejects(s.prepare(another,attempt2));
  await s.finish(true);assert.equal((await s.balance()).credits,0);
});
test('PRO expiry denies export but keeps unlimited ownership for later renewal',async()=>{
  const s=setup();await s.buy('gel_custom_reports');s.docs.set('pro_paid_accounts/owner',{verifiedUntil:0});
  await assert.rejects(s.prepare(reportId,attemptId,'unlimited'),e=>e.code==='failed-precondition');
  assert.equal((await s.balance()).unlimited,true);
  s.docs.set('pro_paid_accounts/owner',{verifiedUntil:Date.now()+60000});
  assert.equal((await s.prepare(reportId,attemptId,'unlimited')).allowed,true);
});
test('refund recheck revokes one credit exactly once and never consumes a rejected purchase',async()=>{
  const s=setup();await s.buy();s.setPurchase({purchaseState:1,consumptionState:1});
  const before=s.calls.filter(c=>c.method==='POST').length;
  await assert.rejects(s.buy());await assert.rejects(s.buy());assert.equal((await s.balance()).credits,0);
  assert.equal(s.calls.filter(c=>c.method==='POST').length,before);
});
test('delayed successful completion reconciles after expired reservation cleanup',async()=>{
  const s=setup();await s.buy();await s.prepare();
  const data=s.docs.get('report_accounts/owner');data.reservations={};s.docs.set('report_accounts/owner',data);
  await s.finish(true);assert.equal((await s.balance()).credits,0);
  await s.finish(true);assert.equal((await s.balance()).credits,0);
  await assert.rejects(s.finish(true,another,attempt2),e=>e.code==='failed-precondition');
});
test('Play refund notifications revoke consumed credits idempotently',async()=>{
  const s=setup();await s.buy();s.setPurchase({purchaseState:1,consumptionState:1});
  const event={data:{message:{json:{packageName:'com.gel.iDoctor',voidedPurchaseNotification:{purchaseToken:'purchase-token-123'}}}}};
  await s.exports.handleReportPurchaseNotification(event);
  await s.exports.handleReportPurchaseNotification(event);
  assert.equal((await s.balance()).credits,0);
});
test('restore rechecks and revokes refunded unlimited ownership',async()=>{
  const s=setup();await s.buy('gel_custom_reports');s.setPurchase({purchaseState:1,consumptionState:0});
  assert.equal((await s.balance()).unlimited,false);
  await assert.rejects(s.prepare(reportId,attemptId,'unlimited'),e=>e.code==='permission-denied');
});

test('trial exports are free only before expiry and never become purchased ownership',async()=>{
 const s=setup(),deviceId='0123456789abcdef';
 const {deviceKey,TRIAL_MS}=require('../trial-policy'),{giftKey}=require('../pro-policy');
 const startedAt=Date.now()-1000,expiresAt=startedAt+TRIAL_MS,key=deviceKey(deviceId),email=auth.token.email;
 s.docs.delete('pro_paid_accounts/owner');
 s.docs.set('pro_trial_accounts/'+giftKey(email),{device:key,startedAt,expiresAt});
 s.docs.set('pro_trial_devices/'+key,{account:giftKey(email),startedAt,expiresAt});
 const data={deviceId,reportId,attemptId,mode:'unlimited'};
 assert.equal((await s.exports.prepareReportExport(s.req(data))).allowed,true);
 assert.equal((await s.balance()).unlimited,false);
 assert.notEqual(s.docs.get('report_accounts/owner')?.unlimited,true);
 for(const name of ['pro_trial_accounts/'+giftKey(email),'pro_trial_devices/'+key]){
   const d=s.docs.get(name);d.startedAt-=TRIAL_MS;d.expiresAt-=TRIAL_MS;
 }
 await s.exports.finishReportExport(s.req({...data,success:true}));
 assert.equal(s.docs.get('report_accounts/owner')?.credits,undefined);
 assert.equal(s.docs.get('report_accounts/owner/reports/'+reportId)?.completed,undefined);
 await assert.rejects(s.exports.prepareReportExport(s.req({...data,attemptId:attempt2})));
});
