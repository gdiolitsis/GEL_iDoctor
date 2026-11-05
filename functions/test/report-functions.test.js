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
  const docs = new Map(); const calls=[];
  let purchase={purchaseState:0,consumptionState:0,acknowledgementState:0};
  let failFinalize=false; let chain=Promise.resolve();
  const ref = path => ({path,collection:name=>collection(path+'/'+name),get:async()=>snap(path)});
  const collection = path => ({doc:id=>ref(path+'/'+id)});
  const snap=path=>({exists:docs.has(path),data:()=>structuredClone(docs.get(path))});
  const db={collection,runTransaction:fn=>{
    const run=chain.then(async()=>{
      const writes=new Map();
      const tx={get:async r=>snap(r.path),set:(r,data)=>writes.set(r.path,structuredClone(data))};
      const value=await fn(tx);for(const [k,v] of writes)docs.set(k,v);return value;
    });chain=run.catch(()=>{});return run;
  }};
  const exports={};
  vm.runInNewContext(fs.readFileSync(require.resolve('../report-functions'),'utf8'),{
    exports,Date,encodeURIComponent,
    require:name=>({
      'firebase-functions/v2/https':{onCall:(options,fn)=>fn||options,HttpsError:class extends Error{constructor(code,message){super(message);this.code=code;}}},
      'firebase-admin/firestore':{getFirestore:()=>db},
      'firebase-functions/v2/pubsub':{onMessagePublished:(topic,fn)=>fn},
      'node:crypto':crypto,'./pro-policy':require('../pro-policy'),
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
  return {exports,docs,calls,req,buy,prepare,finish,balance,setPurchase:p=>{purchase=p;},failFinalize:()=>{failFinalize=true;}};
}
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
