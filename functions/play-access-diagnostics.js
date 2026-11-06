'use strict';
const {onMessagePublished} = require('firebase-functions/v2/pubsub');
const {GoogleAuth} = require('google-auth-library');
const SCOPE = 'https://www.googleapis.com/auth/androidpublisher';
const EXPECTED = '172626247066-compute@developer.gserviceaccount.com';
// IAM-protected Pub/Sub trigger: no public HTTP endpoint, user input URLs or purchase tokens.
exports.diagnosePlayAccess = onMessagePublished({
  topic:'gel-play-access-diagnostics',retry:false,timeoutSeconds:60,maxInstances:1,
}, async event => {
  if (event.data.message.json?.run !== true) return;
  const auth = new GoogleAuth({scopes:[SCOPE]});
  let client;
  try {
    client = await auth.getClient();
    const credentials = await auth.getCredentials();
    console.info('PRO_PLAY_RUNTIME_IDENTITY', {
      identityKnown:typeof credentials.client_email === 'string',
      expectedServiceAccount:credentials.client_email === EXPECTED,
    });
  } catch {
    console.info('PRO_PLAY_RUNTIME_IDENTITY', {identityKnown:false,credentialsFailed:true});
    return;
  }
  try {
    const access = await client.getAccessToken();
    const info = await client.getTokenInfo(access.token);
    console.info('PRO_PLAY_RUNTIME_SCOPE', {
      scopeKnown:true,publisherScopePresent:Array.isArray(info.scopes) && info.scopes.includes(SCOPE),
    });
  } catch {
    console.info('PRO_PLAY_RUNTIME_SCOPE', {scopeKnown:false});
  }
  for (const [label,url] of [
    ['PRO_PLAY_RUNTIME_CATALOG','https://androidpublisher.googleapis.com/androidpublisher/v3/applications/com.gel.iDoctor/subscriptions/gel_pro_monthly'],
    ['PRO_PLAY_RUNTIME_PROBE','https://androidpublisher.googleapis.com/androidpublisher/v3/applications/com.gel.iDoctor/purchases/subscriptionsv2/tokens/gel-diagnostic-invalid-token'],
  ]) {
  try {
    // Fixed nonexistent token, through the same API as production verification.
    // This cannot buy, acknowledge, cancel or grant anything, and writes no ledger data.
    const result = await client.request({
      url,
      timeout:10000,
    });
    console.info(label, {httpStatus:result.status || 200});
  } catch (error) {
    const allowed = new Set(['permissionDenied','insufficientPermissions','accessNotConfigured',
      'invalidToken','purchaseTokenMismatch','subscriptionNotFound','PERMISSION_DENIED','SERVICE_DISABLED','UNAUTHENTICATED']);
    const api = error.response?.data?.error;
    const reasons = [...(api?.errors || []).map(e=>e.reason), ...(api?.details || []).map(e=>e.reason),api?.status];
    console.info(label, {
      httpStatus:Number(error.response?.status)||0,
      reason:reasons.find(r=>allowed.has(r)) || 'unspecified',
    });
  }
  }
});
