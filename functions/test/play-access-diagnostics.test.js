'use strict';
const {test} = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
function load({email='172626247066-compute@developer.gserviceaccount.com',scopes=['https://www.googleapis.com/auth/androidpublisher'],scopeFailure=false,status=401,reason='permissionDenied'}={}) {
  const logs=[],requests=[];const exports={};
  vm.runInNewContext(fs.readFileSync(require.resolve('../play-access-diagnostics'),'utf8'),{
    exports,console:{info:(...v)=>logs.push(v)},require:n=>({
      'firebase-functions/v2/pubsub':{onMessagePublished:(options,handler)=>{
        assert.equal(options.retry,false);assert.equal(options.topic,'gel-play-access-diagnostics');return handler;
      }},
      'google-auth-library':{GoogleAuth:class {
        async getCredentials(){return {client_email:email};}
        async getClient(){return {
          getAccessToken:async()=>({token:'secret-access-token'}),
          getTokenInfo:async token=>{assert.equal(token,'secret-access-token');if(scopeFailure)throw Error('secret-access-token');return {scopes};},
          request:async options=>{requests.push(options);throw {response:{status,data:{error:{message:'secret-access-token',errors:[{reason}]}}}};},
        };}
      }},
    })[n],
  });
  return {logs,requests,run:json=>exports.diagnosePlayAccess({data:{message:{json}}})};
}
test('runtime probe reports identity, scope and Play denial without credentials or purchase data',async()=>{
  const s=load();await s.run({run:true,url:'https://untrusted.invalid',purchaseToken:'secret-purchase'});
  assert.equal(s.logs[0][1].expectedServiceAccount,true);
  assert.equal(s.logs[1][1].publisherScopePresent,true);
  assert.equal(s.logs[2][1].httpStatus,401);assert.equal(s.logs[2][1].reason,'permissionDenied');
  assert.equal(s.requests.length,1);assert.match(s.requests[0].url,/com.gel.iDoctor\/purchases\/subscriptionsv2\/tokens\/gel-diagnostic-invalid-token$/);
  assert.equal(s.requests[0].method,undefined);
  assert.equal(JSON.stringify(s.logs).includes('secret'),false);
});
test('probe ignores unrelated messages and reports mismatched identity and missing scope',async()=>{
  const s=load({email:'unexpected@example.com',scopes:[]});await s.run({run:false});assert.equal(s.logs.length,0);
  await s.run({run:true});assert.equal(s.logs[0][1].expectedServiceAccount,false);assert.equal(s.logs[1][1].publisherScopePresent,false);
});
test('scope introspection failure still tests Play without leaking raw errors',async()=>{
  const s=load({scopeFailure:true,status:400,reason:'private-sensitive-value'});await s.run({run:true});
  assert.equal(s.logs[1][1].scopeKnown,false);assert.equal(s.logs[2][1].httpStatus,400);
  assert.equal(s.logs[2][1].reason,'unspecified');assert.equal(JSON.stringify(s.logs).includes('secret'),false);
});
