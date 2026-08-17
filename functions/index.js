const crypto = require("crypto");

const {
  initializeApp
} = require("firebase-admin/app");

const {
  getFirestore,
  Timestamp,
  FieldValue
} = require("firebase-admin/firestore");

const {
  onCall,
  HttpsError
} = require("firebase-functions/v2/https");

const {
  setGlobalOptions
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

// Remote diagnostics transport limits.
// One callable request may carry up to 25 log lines.
// This keeps Function invocations and Firestore writes controlled.
const DIAGNOSTIC_MAX_LINES_PER_BATCH = 25;
const DIAGNOSTIC_MAX_LINE_CHARS = 2000;
const DIAGNOSTIC_MAX_BATCH_CHARS = 16000;


// Remote functional-control limits.
const REMOTE_COMMAND_TTL_MS = 2 * 60 * 1000;
const REMOTE_MAX_PAYLOAD_CHARS = 4096;
const REMOTE_MAX_RESULT_CHARS = 12000;
const REMOTE_MAX_MESSAGE_CHARS = 1000;

const REMOTE_ACTIONS = new Set([
  "PING",
  "GET_IDOCTOR_SETTINGS",
  "APPLY_IDOCTOR_SETTINGS",
  "SET_LANGUAGE",
  "SET_PLATFORM",
  "CLEAN_IDOCTOR_CACHE",
  "GET_DEVICE_SUMMARY",
  "CPU_RAM_SNAPSHOT",
]);

function normalizeRemoteAction(value) {
  if (typeof value !== "string") {
    return null;
  }

  const action = value.trim().toUpperCase();

  return REMOTE_ACTIONS.has(action)
    ? action
    : null;
}

function sanitizePlainObject(value, maxChars, fieldName) {
  const obj =
    value && typeof value === "object" && !Array.isArray(value)
      ? value
      : {};

  let encoded;

  try {
    encoded = JSON.stringify(obj);
  } catch (err) {
    throw new HttpsError(
      "invalid-argument",
      `${fieldName} must be JSON-serializable.`
    );
  }

  if (encoded.length > maxChars) {
    throw new HttpsError(
      "invalid-argument",
      `${fieldName} is too large.`
    );
  }

  return obj;
}

function createCommandId() {
  return `CMD-${Date.now().toString(36).toUpperCase()}-${crypto
    .randomBytes(4)
    .toString("hex")
    .toUpperCase()}`;
}

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
  return typeof value === "string" &&
    /^\d{6}$/.test(value);
}

function normalizeSessionId(value) {
  if (typeof value !== "string") {
    return null;
  }

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
  const now =
    Date.now()
      .toString(36)
      .toUpperCase();

  const random =
    crypto
      .randomBytes(6)
      .toString("hex")
      .toUpperCase();

  return `GEL-${now}-${random}`;
}

function randomSixDigitCode() {
  return String(
    crypto.randomInt(
      100000,
      1000000
    )
  );
}

async function generateUniqueServiceCode() {
  for (
    let attempt = 0;
    attempt < 12;
    attempt++
  ) {
    const code =
      randomSixDigitCode();

    const existing =
      await db
        .collection(COLLECTION)
        .where(
          "serviceCode",
          "==",
          code
        )
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

// ============================================================
// TECHNICIAN — CREATE SERVICE SESSION
// ============================================================
exports.createServiceSession =
  onCall(
    async (request) => {
      const technicianUid =
        requireAuth(request);

      const createdAtMs =
        Date.now();

      const expiresAtMs =
        createdAtMs +
        PAIRING_WINDOW_MS;

      const sessionId =
        createSessionId();

      const serviceCode =
        await generateUniqueServiceCode();

      const ref =
        db
          .collection(COLLECTION)
          .doc(sessionId);

      await ref.set({
        version: 1,
        sessionId,
        serviceCode,
        technicianUid,
        customerUid: null,
        status: "WAITING",

        createdAt:
          Timestamp.fromMillis(
            createdAtMs
          ),

        expiresAt:
          Timestamp.fromMillis(
            expiresAtMs
          ),

        connectedAt: null,
        completedAt: null,
        deviceInfo: null,

        updatedAt:
          FieldValue.serverTimestamp(),
      });

      return {
        ok: true,
        sessionId,
        serviceCode,
        expiresAt: expiresAtMs,
        status: "WAITING",
      };
    }
  );

// ============================================================
// CUSTOMER — CLAIM SERVICE SESSION
// ============================================================
exports.claimServiceSession =
  onCall(
    async (request) => {
      const customerUid =
        requireAuth(request);

      const data =
        request.data || {};

      const serviceCode =
        typeof data.code === "string"
          ? data.code.trim()
          : "";

      if (
        !isSixDigitCode(
          serviceCode
        )
      ) {
        throw new HttpsError(
          "invalid-argument",
          "A valid 6-digit Service Code is required."
        );
      }

      const requestedSessionId =
        normalizeSessionId(
          data.sessionId
        );

      let ref = null;

      if (requestedSessionId) {
        ref =
          db
            .collection(COLLECTION)
            .doc(
              requestedSessionId
            );
      } else {
        const match =
          await db
            .collection(COLLECTION)
            .where(
              "serviceCode",
              "==",
              serviceCode
            )
            .limit(1)
            .get();

        if (match.empty) {
          throw new HttpsError(
            "not-found",
            "No active Service Session was found for this code."
          );
        }

        ref =
          match.docs[0].ref;
      }

      const result =
        await db.runTransaction(
          async (tx) => {
            const snap =
              await tx.get(ref);

            if (!snap.exists) {
              throw new HttpsError(
                "not-found",
                "Service Session not found."
              );
            }

            const session =
              snap.data();

            if (
              session.serviceCode !==
              serviceCode
            ) {
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

            if (
              !expiresAtMs ||
              Date.now() >=
                expiresAtMs
            ) {
              throw new HttpsError(
                "deadline-exceeded",
                "The pairing code has expired."
              );
            }

            if (
              session.status !==
              "WAITING"
            ) {
              if (
                session.status ===
                  "CONNECTED" &&
                session.customerUid ===
                  customerUid
              ) {
                return {
                  sessionId:
                    snap.id,

                  serviceCode,

                  expiresAt:
                    expiresAtMs,

                  technicianUid:
                    session.technicianUid,

                  status:
                    "CONNECTED",
                };
              }

              throw new HttpsError(
                "failed-precondition",
                "This Service Session is no longer available for pairing."
              );
            }

            if (
              session.customerUid
            ) {
              throw new HttpsError(
                "already-exists",
                "A customer device is already connected to this session."
              );
            }

            if (
              session.technicianUid ===
              customerUid
            ) {
              throw new HttpsError(
                "failed-precondition",
                "Technician and customer must use different app identities."
              );
            }

            tx.update(
              ref,
              {
                customerUid,
                status:
                  "CONNECTED",

                connectedAt:
                  FieldValue.serverTimestamp(),

                updatedAt:
                  FieldValue.serverTimestamp(),
              }
            );

            return {
              sessionId:
                snap.id,

              serviceCode,

              expiresAt:
                expiresAtMs,

              technicianUid:
                session.technicianUid,

              status:
                "CONNECTED",
            };
          }
        );

      return {
        ok: true,
        ...result,
      };
    }
  );

// ============================================================
// CUSTOMER — REMOTE DIAGNOSTICS LOG BATCH
//
// The customer app never writes Firestore directly.
// It sends a small validated batch through this callable Function.
// The Admin SDK writes the batch under:
//
// service_sessions/{sessionId}/diagnostics/{batchId}
//
// A technician can then listen to the diagnostics subcollection
// in real time.
//
// IMPORTANT:
// - Only the customerUid already bound to the CONNECTED session
//   may upload logs.
// - The active pairing-code expiry does not terminate a CONNECTED
//   Service Session.
// - Logs are transport data only; they do not execute commands.
// ============================================================
exports.appendDiagnosticBatch =
  onCall(
    async (request) => {
      const customerUid =
        requireAuth(request);

      const data =
        request.data || {};

      const sessionId =
        normalizeSessionId(
          data.sessionId
        );

      if (!sessionId) {
        throw new HttpsError(
          "invalid-argument",
          "A valid Service Session ID is required."
        );
      }

      if (
        !Array.isArray(
          data.lines
        )
      ) {
        throw new HttpsError(
          "invalid-argument",
          "Diagnostic lines must be an array."
        );
      }

      if (
        data.lines.length < 1 ||
        data.lines.length >
          DIAGNOSTIC_MAX_LINES_PER_BATCH
      ) {
        throw new HttpsError(
          "invalid-argument",
          `A diagnostic batch must contain 1-${DIAGNOSTIC_MAX_LINES_PER_BATCH} lines.`
        );
      }

      const sanitizedLines = [];
      let totalChars = 0;

      for (
        const raw of data.lines
      ) {
        if (
          typeof raw !==
          "string"
        ) {
          continue;
        }

        // Preserve useful spacing inside the line, but remove
        // embedded CR/LF so each array item remains one log line.
        let line =
          raw
            .replace(
              /[\r\n]+/g,
              " "
            )
            .trim();

        if (!line) {
          continue;
        }

        if (
          line.length >
          DIAGNOSTIC_MAX_LINE_CHARS
        ) {
          line =
            line.substring(
              0,
              DIAGNOSTIC_MAX_LINE_CHARS
            );
        }

        if (
          totalChars +
            line.length >
          DIAGNOSTIC_MAX_BATCH_CHARS
        ) {
          break;
        }

        sanitizedLines.push(
          line
        );

        totalChars +=
          line.length;
      }

      if (
        sanitizedLines.length <
        1
      ) {
        throw new HttpsError(
          "invalid-argument",
          "The diagnostic batch contains no usable log lines."
        );
      }

      const sequenceRaw =
        Number(
          data.sequence
        );

      const sequence =
        Number.isSafeInteger(
          sequenceRaw
        ) &&
        sequenceRaw >= 0
          ? sequenceRaw
          : 0;

      const clientTimestampRaw =
        Number(
          data.clientTimestamp
        );

      const clientTimestamp =
        Number.isFinite(
          clientTimestampRaw
        ) &&
        clientTimestampRaw > 0
          ? Math.floor(
              clientTimestampRaw
            )
          : null;

      const sessionRef =
        db
          .collection(COLLECTION)
          .doc(sessionId);

      const sessionSnap =
        await sessionRef.get();

      if (
        !sessionSnap.exists
      ) {
        throw new HttpsError(
          "not-found",
          "Service Session not found."
        );
      }

      const session =
        sessionSnap.data();

      if (
        session.status !==
        "CONNECTED"
      ) {
        throw new HttpsError(
          "failed-precondition",
          "Diagnostic upload requires a CONNECTED Service Session."
        );
      }

      if (
        session.customerUid !==
        customerUid
      ) {
        throw new HttpsError(
          "permission-denied",
          "This device is not the customer device assigned to the Service Session."
        );
      }

      const batchRef =
        sessionRef
          .collection(
            "diagnostics"
          )
          .doc();

      await batchRef.set({
        version: 1,
        type:
          "LOG_BATCH",

        lines:
          sanitizedLines,

        customerUid,

        sequence,

        clientTimestamp,

        createdAt:
          FieldValue.serverTimestamp(),
      });

      await sessionRef.update({
        lastDiagnosticAt:
          FieldValue.serverTimestamp(),

        updatedAt:
          FieldValue.serverTimestamp(),
      });

      return {
        ok: true,
        batchId:
          batchRef.id,
        acceptedLines:
          sanitizedLines.length,
        sequence,
      };
    }
  );
// ============================================================
// TECHNICIAN — SEND ALLOWLISTED REMOTE COMMAND
//
// Writes a single active command into the parent Service Session.
// This intentionally reuses the parent document because the current
// Firestore rules already allow both assigned parties to read it.
//
// PENDING -> RUNNING -> SUCCESS / FAILED
// ============================================================
exports.sendRemoteCommand =
  onCall(
    async (request) => {
      const technicianUid =
        requireAuth(request);

      const data =
        request.data || {};

      const sessionId =
        normalizeSessionId(
          data.sessionId
        );

      const action =
        normalizeRemoteAction(
          data.action
        );

      if (!sessionId) {
        throw new HttpsError(
          "invalid-argument",
          "A valid Service Session ID is required."
        );
      }

      if (!action) {
        throw new HttpsError(
          "invalid-argument",
          "Unsupported remote action."
        );
      }

      const payload =
        sanitizePlainObject(
          data.payload,
          REMOTE_MAX_PAYLOAD_CHARS,
          "Remote command payload"
        );

      const sessionRef =
        db
          .collection(COLLECTION)
          .doc(sessionId);

      const commandId =
        createCommandId();

      const nowMs =
        Date.now();

      const expiresAtMs =
        nowMs +
        REMOTE_COMMAND_TTL_MS;

      await db.runTransaction(
        async (tx) => {
          const snap =
            await tx.get(
              sessionRef
            );

          if (!snap.exists) {
            throw new HttpsError(
              "not-found",
              "Service Session not found."
            );
          }

          const session =
            snap.data();

          if (
            session.technicianUid !==
            technicianUid
          ) {
            throw new HttpsError(
              "permission-denied",
              "This Service Session does not belong to this technician."
            );
          }

          if (
            session.status !==
            "CONNECTED" ||
            !session.customerUid
          ) {
            throw new HttpsError(
              "failed-precondition",
              "Remote commands require a CONNECTED customer device."
            );
          }

          const previous =
            session.remoteCommand;

          if (
            previous &&
            (
              previous.status === "PENDING" ||
              previous.status === "RUNNING"
            )
          ) {
            const previousExpiresMs =
              previous.expiresAt &&
              typeof previous.expiresAt.toMillis === "function"
                ? previous.expiresAt.toMillis()
                : 0;

            if (
              previousExpiresMs >
              nowMs
            ) {
              throw new HttpsError(
                "resource-exhausted",
                "Another remote command is still active."
              );
            }
          }

          tx.update(
            sessionRef,
            {
              remoteCommand: {
                version: 1,
                id: commandId,
                action,
                payload,
                status: "PENDING",
                technicianUid,
                customerUid:
                  session.customerUid,
                issuedAt:
                  Timestamp.fromMillis(
                    nowMs
                  ),
                expiresAt:
                  Timestamp.fromMillis(
                    expiresAtMs
                  ),
                startedAt: null,
                completedAt: null,
                message: null,
                result: null,
              },

              lastRemoteCommandAt:
                FieldValue.serverTimestamp(),

              updatedAt:
                FieldValue.serverTimestamp(),
            }
          );
        }
      );

      return {
        ok: true,
        sessionId,
        commandId,
        action,
        status: "PENDING",
        expiresAt:
          expiresAtMs,
      };
    }
  );


// ============================================================
// CUSTOMER — CLAIM REMOTE COMMAND
//
// The transaction changes PENDING -> RUNNING before execution,
// preventing duplicate execution from repeated Firestore snapshots.
// ============================================================
exports.claimRemoteCommand =
  onCall(
    async (request) => {
      const customerUid =
        requireAuth(request);

      const data =
        request.data || {};

      const sessionId =
        normalizeSessionId(
          data.sessionId
        );

      const commandId =
        typeof data.commandId === "string"
          ? data.commandId.trim()
          : "";

      if (
        !sessionId ||
        !/^CMD-[A-Z0-9-]+$/i.test(commandId)
      ) {
        throw new HttpsError(
          "invalid-argument",
          "Valid sessionId and commandId are required."
        );
      }

      const sessionRef =
        db
          .collection(COLLECTION)
          .doc(sessionId);

      const result =
        await db.runTransaction(
          async (tx) => {
            const snap =
              await tx.get(
                sessionRef
              );

            if (!snap.exists) {
              throw new HttpsError(
                "not-found",
                "Service Session not found."
              );
            }

            const session =
              snap.data();

            if (
              session.status !==
              "CONNECTED"
            ) {
              throw new HttpsError(
                "failed-precondition",
                "Service Session is not CONNECTED."
              );
            }

            if (
              session.customerUid !==
              customerUid
            ) {
              throw new HttpsError(
                "permission-denied",
                "This device is not the customer device assigned to this session."
              );
            }

            const command =
              session.remoteCommand;

            if (
              !command ||
              command.id !==
              commandId
            ) {
              throw new HttpsError(
                "not-found",
                "Remote command not found."
              );
            }

            if (
              command.status !==
              "PENDING"
            ) {
              throw new HttpsError(
                "failed-precondition",
                "Remote command is no longer pending."
              );
            }

            const expiresAtMs =
              command.expiresAt &&
              typeof command.expiresAt.toMillis === "function"
                ? command.expiresAt.toMillis()
                : 0;

            if (
              !expiresAtMs ||
              Date.now() >=
              expiresAtMs
            ) {
              throw new HttpsError(
                "deadline-exceeded",
                "Remote command expired."
              );
            }

            tx.update(
              sessionRef,
              {
                "remoteCommand.status":
                  "RUNNING",

                "remoteCommand.startedAt":
                  FieldValue.serverTimestamp(),

                updatedAt:
                  FieldValue.serverTimestamp(),
              }
            );

            return {
              action:
                command.action,
              payload:
                command.payload || {},
            };
          }
        );

      return {
        ok: true,
        sessionId,
        commandId,
        status: "RUNNING",
        ...result,
      };
    }
  );


// ============================================================
// CUSTOMER — COMPLETE REMOTE COMMAND
//
// Only the assigned customerUid can complete the command that it claimed.
// The final command is archived under commands/{commandId} for audit history.
// ============================================================
exports.completeRemoteCommand =
  onCall(
    async (request) => {
      const customerUid =
        requireAuth(request);

      const data =
        request.data || {};

      const sessionId =
        normalizeSessionId(
          data.sessionId
        );

      const commandId =
        typeof data.commandId === "string"
          ? data.commandId.trim()
          : "";

      const terminalStatus =
        data.status === "SUCCESS"
          ? "SUCCESS"
          : (
            data.status === "FAILED"
              ? "FAILED"
              : null
          );

      if (
        !sessionId ||
        !/^CMD-[A-Z0-9-]+$/i.test(commandId) ||
        !terminalStatus
      ) {
        throw new HttpsError(
          "invalid-argument",
          "Valid sessionId, commandId and terminal status are required."
        );
      }

      let message =
        typeof data.message === "string"
          ? data.message.trim()
          : "";

      if (
        message.length >
        REMOTE_MAX_MESSAGE_CHARS
      ) {
        message =
          message.substring(
            0,
            REMOTE_MAX_MESSAGE_CHARS
          );
      }

      const resultPayload =
        sanitizePlainObject(
          data.result,
          REMOTE_MAX_RESULT_CHARS,
          "Remote command result"
        );

      const sessionRef =
        db
          .collection(COLLECTION)
          .doc(sessionId);

      const finalCommand =
        await db.runTransaction(
          async (tx) => {
            const snap =
              await tx.get(
                sessionRef
              );

            if (!snap.exists) {
              throw new HttpsError(
                "not-found",
                "Service Session not found."
              );
            }

            const session =
              snap.data();

            if (
              session.customerUid !==
              customerUid
            ) {
              throw new HttpsError(
                "permission-denied",
                "This device is not the customer device assigned to this session."
              );
            }

            const command =
              session.remoteCommand;

            if (
              !command ||
              command.id !==
              commandId
            ) {
              throw new HttpsError(
                "not-found",
                "Remote command not found."
              );
            }

            if (
              command.status !==
              "RUNNING"
            ) {
              throw new HttpsError(
                "failed-precondition",
                "Remote command is not RUNNING."
              );
            }

            const completedAt =
              Timestamp.fromMillis(
                Date.now()
              );

            const updatedCommand = {
              ...command,
              status:
                terminalStatus,
              completedAt,
              message,
              result:
                resultPayload,
            };

            tx.update(
              sessionRef,
              {
                remoteCommand:
                  updatedCommand,

                lastRemoteCommandCompletedAt:
                  FieldValue.serverTimestamp(),

                updatedAt:
                  FieldValue.serverTimestamp(),
              }
            );

            return updatedCommand;
          }
        );

      await sessionRef
        .collection("commands")
        .doc(commandId)
        .set({
          ...finalCommand,
          archivedAt:
            FieldValue.serverTimestamp(),
        });

      return {
        ok: true,
        sessionId,
        commandId,
        status:
          terminalStatus,
      };
    }
  );
