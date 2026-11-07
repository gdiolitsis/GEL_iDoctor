const {test} = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
function load({grant, response, failure} = {}) {
  const reads = [];
  const logs = [];
  const db = {collection: name => ({doc: key => ({get: async () => {
    reads.push({name,key}); return {exists: !!grant, data: () => grant};
  }})})};
  const exports = {};
  vm.runInNewContext(fs.readFileSync(require.resolve('../pro-functions'), 'utf8'), {
    exports, Date, encodeURIComponent, console:{warn:(...args)=>logs.push(args)},
    require: name => ({
      'firebase-functions/v2/https': {onCall: fn => fn, HttpsError: class extends Error {constructor(code,message){super(message);this.code=code;}}},
      'firebase-admin/firestore': {getFirestore: () => db},
      'firebase-admin/auth': {getAuth: () => ({})},
      'google-auth-library': {GoogleAuth: class {async getClient(){return {request:async () => {if(failure) throw failure; return {data:response};}};}}},
      './pro-policy': require('../pro-policy'),
    })[name],
  });
  return {handlers: exports, reads, logs};
}
const auth = {uid:'google-user',token:{email:'owner@example.com',email_verified:true,firebase:{sign_in_provider:'google.com'}}};
test('anonymous or client-spoofed gift email never causes an allowlist lookup', async () => {
  const {handlers,reads}=load({grant:{enabled:true}});
  const result=await handlers.getGiftProEntitlement({auth:{uid:'anon',token:{}},data:{email:auth.token.email}});
  assert.equal(result.active,false); assert.equal(reads.length,0);
});
test('Google verified user receives grant; removing it revokes at the next refresh', async () => {
  for(const [grant,expected] of [[{enabled:true},true],[{enabled:false},false],[null,false]]) {
    const {handlers}=load({grant});
    assert.equal((await handlers.getGiftProEntitlement({auth})).active,expected);
  }
});
test('paid verification rejects malformed token and fails closed for missing purchase',async()=>{
  const {handlers}=load({failure:{response:{status:404}}});
  await assert.rejects(handlers.verifyGelProPurchase({auth,data:{purchaseToken:'x'}}));
  assert.equal((await handlers.verifyGelProPurchase({auth,data:{purchaseToken:'valid-looking-token'}})).active,false);
});
test('publisher permission/network failures never grant PRO or leak token',async()=>{
  const {handlers}=load({failure:{response:{status:403},config:{token:'sensitive'}}});
  await assert.rejects(handlers.verifyGelProPurchase({auth,data:{purchaseToken:'valid-looking-token'}}), error => error.code==='unavailable' && !error.message.includes('sensitive'));
});

test('publisher diagnostics never include tokens or messages',async()=>{
  const failure={response:{status:403,data:{error:{status:'PERMISSION_DENIED',message:'secret-token',
    errors:[{reason:'secret-token'}]}}},code:'secret-token',config:{url:'secret-token'}};
  const {handlers,logs}=load({failure});
  await assert.rejects(handlers.verifyGelProPurchase({auth,data:{purchaseToken:'secret-token'}}));
  assert.equal(logs.length,1);
  assert.equal(logs[0][0],'PRO_PLAY_VERIFY_FAILED');
  assert.equal(logs[0][1].httpStatus,403);
  assert.equal(logs[0][1].reason,'PERMISSION_DENIED');
  assert.equal(logs[0][1].stage,'publisher');
  assert.equal(JSON.stringify(logs).includes('secret-token'),false);
});

test('deleting an app account preserves its gift for a new verified Google UID', async () => {
  const {giftKey} = require('../pro-policy');
  const giftId = giftKey(auth.token.email);
  const records = new Map([
    ['pro_gift_accounts/' + giftId, {enabled:true}],
    ['pro_paid_accounts/google-user', {verifiedUntil:Date.now()+10000}],
    ['report_accounts/google-user', {credits:2}],
    ['tester_entitlements/google-user', {}],
  ]);
  const deletedUsers = [];
  const db = {
    collection: name => ({
      doc: key => ({path:name+'/'+key,
        get:async()=>({exists:records.has(name+'/'+key),data:()=>records.get(name+'/'+key)}),
        delete:async()=>records.delete(name+'/'+key),
      }),
      where:()=>({get:async()=>({docs:[]})}),
    }),
    recursiveDelete:async ref=>records.delete(ref.path),
  };
  const handlers = {};
  vm.runInNewContext(fs.readFileSync(require.resolve('../pro-functions'),'utf8'), {
    exports:handlers, Date, encodeURIComponent, console,
    require:name=>({
      'firebase-functions/v2/https':{onCall:fn=>fn,HttpsError:class extends Error {constructor(code,message){super(message);this.code=code;}}},
      'firebase-admin/firestore':{getFirestore:()=>db},
      'firebase-admin/auth':{getAuth:()=>({deleteUser:async uid=>deletedUsers.push(uid)})},
      'google-auth-library':{GoogleAuth:class {}},
      './pro-policy':require('../pro-policy'),
      'node:crypto':require('node:crypto'),
    })[name],
  });
  const recent = {...auth,token:{...auth.token,auth_time:Date.now()/1000}};
  await assert.rejects(handlers.deleteProGoogleAccount({auth:{uid:'anon',token:{}}}));
  await assert.rejects(handlers.deleteProGoogleAccount({auth:{...recent,token:{...recent.token,auth_time:0}}}));
  assert.equal(deletedUsers.length,0);
  assert.equal((await handlers.deleteProGoogleAccount({auth:recent})).deleted,true);
  assert.deepEqual(deletedUsers,['google-user']);
  assert.equal(records.has('report_accounts/google-user'),false);
  assert.equal(records.has('pro_paid_accounts/google-user'),false);
  assert.equal(records.has('tester_entitlements/google-user'),false);
  assert.equal(records.has('pro_gift_accounts/'+giftId),true);
  const newIdentity = {...auth,uid:'new-google-uid'};
  assert.equal((await handlers.getGiftProEntitlement({auth:newIdentity})).active,true);
  assert.equal((await handlers.getGiftProEntitlement({auth:{...newIdentity,token:{...auth.token,email:'other@example.com'}}})).active,false);
  records.set('pro_gift_accounts/'+giftId,{enabled:false});
  assert.equal((await handlers.getGiftProEntitlement({auth:newIdentity})).active,false);
});
