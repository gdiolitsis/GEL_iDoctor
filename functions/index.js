/**
 * GDiolitsis Engine Lab (GEL) — iDoctor
 * Firebase Cloud Functions — Technician / Customer pairing backend
 */

const crypto = require("crypto");

const { initializeApp } = require("firebase-admin/app");
const {
  getFirestore,
  Timestamp,
  FieldValue,
} = require("firebase-admin/firestore");

const {
  onCall,
  HttpsError,
} = require("firebase-functions/v2/https");

const {
  setGlobalOptions,
} = require("firebase-functions/v2");

initializeApp();

const db = getFirestore();

setGlobalOptions({
  region: "europe-west1",
  minInstances: 0,
  maxInstances: 3,
  concurrency: 20,
  memory: "256MiB",
  timeoutSeconds: 15,
});

const COLLECTION = "service_sessions";
const PAIRING_WINDOW_MS = 2 * 60 * 60 * 1000;

function requireAuth(request) {
  if (!request.auth || !request.auth.uid) {
    throw new HttpsError(
      "unauthenticated",
      "Authentication is required."
    );
  }

  return request.auth.uid;
}

function isSixDigitCode(value) {
  return typeof value === "string" && /^\d{6}$/.test(value);
}

function normalizeSessionId(value) {
  if (typeof value !== "string") return null;

  const trimmed = value.trim();

  if (
    trimmed.length < 8 ||
    trimmed.length > 100 ||
    !/^GEL-[A-Z0-9-]+$/i.test(trimmed)
  ) {
    return null;
  }

  return trimmed;
}

function createSessionId() {
  const now = Date.now().toString(36).toUpperCase();
  const random = crypto.randomBytes(6).toString("hex").toUpperCase();

  return `GEL-${now}-${random}`;
}

function randomSixDigitCode() {
  return String(crypto.randomInt(100000, 1000000));
}

async function generateUniqueServiceCode() {
  for (let attempt = 0; attempt < 12; attempt++) {
    const code = randomSixDigitCode();

    const existing = await db
      .collection(COLLECTION)
      .where("serviceCode", "==", code)
      .limit(1)
      .get();

    if (existing.empty) {
      return code;
    }
  }

  throw new HttpsError(
    "resource-exhausted",
    "Unable to allocate a unique service code. Please try again."
  );
}

exports.createServiceSession = onCall(async (request) => {
  const technicianUid = requireAuth(request);

  const createdAtMs = Date.now();
  const expiresAtMs = createdAtMs + PAIRING_WINDOW_MS;

  const sessionId = createSessionId();
  const serviceCode = await generateUniqueServiceCode();

  const ref = db
    .collection(COLLECTION)
    .doc(sessionId);

  await ref.set({
    version: 1,
    sessionId,
    serviceCode,
    technicianUid,
    customerUid: null,
    status: "WAITING",
    createdAt: Timestamp.fromMillis(createdAtMs),
    expiresAt: Timestamp.fromMillis(expiresAtMs),
    connectedAt: null,
    completedAt: null,
    deviceInfo: null,
    updatedAt: FieldValue.serverTimestamp(),
  });

  return {
    ok: true,
    sessionId,
    serviceCode,
    expiresAt: expiresAtMs,
    status: "WAITING",
  };
});

exports.claimServiceSession = onCall(async (request) => {
  const customerUid = requireAuth(request);

  const data = request.data || {};

  const serviceCode =
    typeof data.code === "string"
      ? data.code.trim()
      : "";

  if (!isSixDigitCode(serviceCode)) {
    throw new HttpsError(
      "invalid-argument",
      "A valid 6-digit Service Code is required."
    );
  }

  const requestedSessionId =
    normalizeSessionId(data.sessionId);

  let ref = null;

  if (requestedSessionId) {
    ref = db
      .collection(COLLECTION)
      .doc(requestedSessionId);
  } else {
    const match = await db
      .collection(COLLECTION)
      .where("serviceCode", "==", serviceCode)
      .limit(1)
      .get();

    if (match.empty) {
      throw new HttpsError(
        "not-found",
        "No active Service Session was found for this code."
      );
    }

    ref = match.docs[0].ref;
  }

  const result = await db.runTransaction(async (tx) => {
    const snap = await tx.get(ref);

    if (!snap.exists) {
      throw new HttpsError(
        "not-found",
        "Service Session not found."
      );
    }

    const session = snap.data();

    if (session.serviceCode !== serviceCode) {
      throw new HttpsError(
        "permission-denied",
        "The Service Code does not match this session."
      );
    }

    const expiresAtMs =
      session.expiresAt &&
      typeof session.expiresAt.toMillis === "function"
        ? session.expiresAt.toMillis()
        : 0;

    if (!expiresAtMs || Date.now() >= expiresAtMs) {
      throw new HttpsError(
        "deadline-exceeded",
        "The pairing code has expired."
      );
    }

    if (session.status !== "WAITING") {
      if (
        session.status === "CONNECTED" &&
        session.customerUid === customerUid
      ) {
        return {
          sessionId: snap.id,
          serviceCode,
          expiresAt: expiresAtMs,
          technicianUid: session.technicianUid,
          status: "CONNECTED",
        };
      }

      throw new HttpsError(
        "failed-precondition",
        "This Service Session is no longer available for pairing."
      );
    }

    if (session.customerUid) {
      throw new HttpsError(
        "already-exists",
        "A customer device is already connected to this session."
      );
    }

    if (session.technicianUid === customerUid) {
      throw new HttpsError(
        "failed-precondition",
        "Technician and customer must use different app identities."
      );
    }

    tx.update(ref, {
      customerUid,
      status: "CONNECTED",
      connectedAt: FieldValue.serverTimestamp(),
      updatedAt: FieldValue.serverTimestamp(),
    });

    return {
      sessionId: snap.id,
      serviceCode,
      expiresAt: expiresAtMs,
      technicianUid: session.technicianUid,
      status: "CONNECTED",
    };
  });

  return {
    ok: true,
    ...result,
  };
});
