'use strict';
const {test}=require('node:test'),assert=require('node:assert/strict'),vm=require('node:vm'),fs=require('node:fs');
const {TRIAL_MS,deviceKey}=require('../trial-policy');
const {giftKey}=require('../pro-policy');
const email='trial@example.com',device='0123456789abcdef',otherDevice='fedcba9876543210';
function setup(){
  const docs=new Map();let chain=Promise.resolve();
  const snapshot=path=>({exists:docs.has(path),data:()=>docs.get(path)});
  const ref=path=>({path,get:async()=>snapshot(path)});
  const db={collection:name=>({doc:key=>ref(name+'/'+key)}),runTransaction:fn=>{
    const run=chain.then(async()=>{const writes=[];
      const result=await fn({get:async r=>snapshot(r.path),set:(r,d)=>writes.push([r.path,d])});
      for(const [p,d]of writes)docs.set(p,d);return result;
    });chain=run.catch(()=>{});return run;
  }};
  const handlers={};vm.runInNewContext(fs.readFileSync(require.resolve('../trial-functions'),'utf8'),{
    exports:handlers,Date,require:name=>({
      'firebase-functions/v2/https':{onCall:(opts,fn)=>fn,HttpsError:class extends Error{constructor(code,msg){super(msg);this.code=code;}}},
      'firebase-admin/firestore':{getFirestore:()=>db},'./pro-policy':require('../pro-policy'),'./trial-policy':require('../trial-policy'),
    })[name],
  });
  const request=(id=device,mail=email,uid='first-uid')=>({auth:{uid,token:{email:mail,email_verified:true,firebase:{sign_in_provider:'google.com'}}},data:{deviceId:id}});
  return {docs,handlers,request};
}
test('trial is a fixed server hour; refresh and concurrent activation cannot extend it',async()=>{
 const s=setup();const req=s.request();
 const available=await s.handlers.getGelProTrial(req);assert.equal(available.eligible,true);assert.equal(s.docs.size,0);
 const [a,b]=await Promise.all([s.handlers.startGelProTrial(req),s.handlers.startGelProTrial(req)]);
 assert.equal(a.active,true);assert.equal(a.expiresAtMs-a.serverNowMs,TRIAL_MS);
 assert.equal(b.expiresAtMs,a.expiresAtMs);assert.equal(s.docs.size,2);
 assert.equal((await s.handlers.getGelProTrial(req)).expiresAtMs,a.expiresAtMs);
});
test('reinstall/new Auth UID resumes remaining time; another account/device cannot claim again',async()=>{
 const s=setup();const a=await s.handlers.startGelProTrial(s.request());
 assert.equal((await s.handlers.startGelProTrial(s.request(device,email,'recreated-uid'))).expiresAtMs,a.expiresAtMs);
 assert.equal((await s.handlers.startGelProTrial(s.request(otherDevice))).active,false);
 assert.equal((await s.handlers.startGelProTrial(s.request(device,'other@example.com'))).eligible,false);
 assert.equal(s.docs.size,2);
});
test('expired trial remains consumed and does not reset after account recreation',async()=>{
 const s=setup();await s.handlers.startGelProTrial(s.request());
 for(const d of s.docs.values()){d.startedAt-=TRIAL_MS+1;d.expiresAt-=TRIAL_MS+1;}
 const result=await s.handlers.startGelProTrial(s.request(device,email,'new-uid'));
 assert.equal(result.active,false);assert.equal(result.eligible,false);
});
test('anonymous, spoofed email, missing device and malformed device cannot start a trial',async()=>{
 const s=setup();
 await assert.rejects(s.handlers.startGelProTrial({auth:{uid:'anon',token:{}},data:{email,deviceId:device}}));
 for(const id of ['', 'fake','0'.repeat(16)])await assert.rejects(s.handlers.startGelProTrial(s.request(id)));
 assert.equal(s.docs.size,0);
});
test('mismatched trial records fail closed',async()=>{
 const s=setup();await s.handlers.startGelProTrial(s.request());
 s.docs.get('pro_trial_devices/'+deviceKey(device)).account=giftKey('another@example.com');
 assert.equal((await s.handlers.getGelProTrial(s.request())).active,false);
});
