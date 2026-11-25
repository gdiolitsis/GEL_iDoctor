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

// Only server-verified paid Google Play access qualifies, not Tester/Gift/Trial.
function paidUntil(account, now = Date.now()) {
  const until = Number(account?.verifiedUntil) || 0;
  return Number.isFinite(until) && until > now ? until : 0;
}

function paidServiceDeadline(connectedAtMs, technicianUntil, customerUntil) {
  if (!Number.isFinite(connectedAtMs) || connectedAtMs <= 0) return 0;
  if (!Number.isFinite(technicianUntil) || technicianUntil <= connectedAtMs ||
      !Number.isFinite(customerUntil) || customerUntil <= connectedAtMs) return 0;
  return Math.min(connectedAtMs + SERVICE_MAX_MS, technicianUntil, customerUntil);
}

module.exports = {
  SERVICE_MAX_MS, REMOTE_CONTROL_ENABLED, serviceUntilMs,
  paidUntil, paidServiceDeadline,
  serviceActive, clampServiceEnd,
};
