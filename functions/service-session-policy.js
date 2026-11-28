'use strict';

// Service duration is independent from the 2-hour QR pairing window.
// The customer may use session-granted PRO only until this server deadline.
const SERVICE_MAX_MS = 8 * 60 * 60 * 1000;
const REMOTE_CONTROL_ENABLED = false;

function millis(value) {
  if (value && typeof value.toMillis === 'function') return value.toMillis();
  return Number.isFinite(value) ? value : 0;
}

function serviceUntilMs(session) {
  return millis(session && session.serviceEndsAt);
}

function serviceActive(session, now = Date.now()) {
  return !!session && session.status === 'CONNECTED' &&
      serviceUntilMs(session) > now;
}

function clampServiceEnd(connectedAtMs, now = Date.now()) {
  if (!Number.isFinite(connectedAtMs) || connectedAtMs <= 0) {
    throw new Error('A server-authenticated connection time is required.');
  }
  return Math.min(connectedAtMs + SERVICE_MAX_MS, now + SERVICE_MAX_MS);
}

// Paid Play entitlement is verified independently of Gift and Tester grants.
function paidUntil(account, now = Date.now()) {
  const until = Number(account?.verifiedUntil) || 0;
  return Number.isFinite(until) && until > now ? until : 0;
}

// An administrator-issued FULL ACCESS gift is an equally valid technician
// entitlement when bound to the verified Google sign-in email. A revoked or
// expired gift grants no technician service. A permanent gift never expires.
function giftServiceUntil(grant, now = Date.now()) {
  if (grant?.enabled !== true) return 0;
  const expiry = grant?.expiresAt?.toMillis?.() || 0;
  return expiry === 0 ? Number.MAX_SAFE_INTEGER :
    (Number.isFinite(expiry) && expiry > now ? expiry : 0);
}

// Customer access is a free, session-scoped grant. Only the technician
// needs an active technician entitlement to initiate and maintain service.
function paidServiceDeadline(connectedAtMs, technicianUntil) {
  if (!Number.isFinite(connectedAtMs) || connectedAtMs <= 0) return 0;
  if (!Number.isFinite(technicianUntil) || technicianUntil <= connectedAtMs) return 0;
  return Math.min(connectedAtMs + SERVICE_MAX_MS, technicianUntil);
}

module.exports = {
  SERVICE_MAX_MS, REMOTE_CONTROL_ENABLED, serviceUntilMs,
  paidUntil, giftServiceUntil, paidServiceDeadline,
  serviceActive, clampServiceEnd,
};
