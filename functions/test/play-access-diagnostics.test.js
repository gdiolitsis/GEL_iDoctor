
'use strict';
const {test} = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const ORDER='GPA.0000-0000-0000-00000';
function load({email='172626247066-compute@developer.gserviceaccount.com',scopes=['https://www.googleapis.com/auth/androidpublisher'],scopeFailure=false,catalogStatus=200,orderStatus=200,purchaseStatus=200,token='secret-purchase-token'}={}) {
 const logs=[],requests=[],exports={};
 vm.runInNewContext(fs.readFileSync(require.resolve('../play-access-diagnostics'),'utf8'),{
  exports,encodeURIComponent,console:{info:(...v)=>logs.push(v)},require:n=>({
   'firebase-functions/v2/pubsub':{onMessagePublished:(o,h)=>{assert.equal(o.retry,false);return h;}},
   'google-auth-library':{GoogleAuth:class {
    async getCredentials(){return {client_email:email};}
    async getClient(){return {
     getAccessToken:async()=>({token:'secret-access-token'}),
     getTokenInfo:async()=>{if(scopeFailure)throw Error('secret');return {scopes};},
     request:async o=>{
      requests.push(o);assert.equal(o.method,undefined);
      const order=o.url.includes('/orders/'),purchase=o.url.includes('/purchases/');
      const status=order?orderStatus:purchase?purchaseStatus:catalogStatus;
      if(status!==200)throw {response:{status,data:{error:{message:'secret',errors:[{reason:'permissionDenied'}]}}}};
      return {status,data:order?{purchaseToken:token,buyerAddress:'secret-address'}:purchase?{subscriptionState:'SUBSCRIPTION_STATE_EXPIRED'}:{private:'secret-catalog'}};
     },
    };}
   }},
  })[n],
 });
 return {logs,requests,run:json=>exports.diagnosePlayAccess({data:{message:{json}}})};
}
test('runtime diagnostic reads fixed catalogue without synthetic purchase conclusions',async()=>{
 const s=load();await s.run({run:true,url:'https://evil.invalid'});
 assert.equal(s.logs[0][1].expectedServiceAccount,true);assert.equal(s.logs[1][1].publisherScopePresent,true);
 assert.equal(s.logs[2][0],'PRO_PLAY_RUNTIME_CATALOG');assert.equal(s.requests.length,1);
 assert.equal(JSON.stringify(s.logs).includes('secret'),false);
});
test('real order probe uses returned token only in memory and never logs financial data',async()=>{
 const s=load();await s.run({run:true,orderId:ORDER});
 assert.equal(s.requests.length,3);assert.match(s.requests[2].url,/tokens\/secret-purchase-token$/);
 assert.equal(s.logs[3][1].tokenAvailable,true);assert.equal(s.logs[4][1].httpStatus,200);
 assert.equal(s.logs[4][1].state,'SUBSCRIPTION_STATE_EXPIRED');
 assert.equal(JSON.stringify(s.logs).includes('secret'),false);assert.equal(JSON.stringify(s.logs).includes(ORDER),false);
});
test('invalid order format never reaches order API; unrelated events do nothing',async()=>{
 const s=load();await s.run({run:false,orderId:ORDER});assert.equal(s.requests.length,0);
 await s.run({run:true,orderId:'../../secret'});assert.equal(s.requests.length,1);
 assert.equal(s.logs[3][1].invalidOrderFormat,true);
});
test('order rejection and missing token stop without purchase verification',async()=>{
 for(const opts of [{orderStatus:401},{token:null}]) {
  const s=load(opts);await s.run({run:true,orderId:ORDER});assert.equal(s.requests.length,2);
  assert.equal(JSON.stringify(s.logs).includes('secret'),false);
 }
});
test('identity, scope and real-purchase failures are reported without raw error leaks',async()=>{
 const s=load({email:'unexpected@example.com',scopeFailure:true,catalogStatus:403,purchaseStatus:401});
 await s.run({run:true,orderId:ORDER});assert.equal(s.logs[0][1].expectedServiceAccount,false);
 assert.equal(s.logs[1][1].scopeKnown,false);assert.equal(s.logs[2][1].httpStatus,403);
 assert.equal(s.logs[4][1].httpStatus,401);assert.equal(JSON.stringify(s.logs).includes('secret'),false);
});
