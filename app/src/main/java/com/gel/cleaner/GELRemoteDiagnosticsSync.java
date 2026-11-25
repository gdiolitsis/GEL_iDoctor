package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GEL Remote Diagnostics transport.
 *
 * Customer side only:
 * - Reads the already-existing GELServiceLog pipeline.
 * - Captures only new log text while a customer Service Session is CONNECTED.
 * - Sends small batches through the callable Cloud Function appendDiagnosticBatch.
 *
 * It never writes Firestore directly.
 * The backend verifies that the authenticated UID is exactly the customerUid
 * bound to the CONNECTED Service Session.
 */
public final class GELRemoteDiagnosticsSync {

    private static final String FUNCTIONS_REGION =
            "europe-west1";

    private static final String CUSTOMER_SESSION_PREFS =
            "GEL_CUSTOMER_SERVICE_SESSION";

    private static final String KEY_SESSION_ID =
            "session_id";

    private static final String KEY_CONNECTED =
            "connected";

    private static final long POLL_MS =
            1200L;

    private static final int MAX_LINES_PER_BATCH =
            20;

    private static final int MAX_LINE_CHARS =
            1800;

    private static final int MAX_BATCH_CHARS =
            12000;

    private static final Object LOCK =
            new Object();

    private static final Handler MAIN =
            new Handler(Looper.getMainLooper());

    private static final class DiagnosticLine {
        final String text;
        final String valueColor;
        final int valueStart;

        DiagnosticLine(String text, String valueColor, int valueStart) {
            this.text = text;
            this.valueColor = valueColor;
            this.valueStart = valueStart;
        }
    }

    private static final ArrayDeque<DiagnosticLine> pendingLines =
            new ArrayDeque<>();

    private static Context appContext;

    private static FirebaseAuth firebaseAuth;
    private static FirebaseFunctions firebaseFunctions;

    private static boolean running = false;
    private static boolean inFlight = false;

    private static String activeSessionId = null;

    /**
     * Exact GELServiceLog snapshot already captured into the queue.
     * This is intentionally separate from "successfully uploaded":
     * once text is captured, clearing GELServiceLog cannot lose it.
     */
    private static String lastCapturedLogText = "";

    private static long sequence = 0L;

    private GELRemoteDiagnosticsSync() {}

    public static void start(Context context) {

        if (context == null) {
            return;
        }

        synchronized (LOCK) {

            appContext =
                    context.getApplicationContext();

            if (firebaseAuth == null) {
                firebaseAuth =
                        FirebaseAuth.getInstance();
            }

            if (firebaseFunctions == null) {
                firebaseFunctions =
                        FirebaseFunctions.getInstance(
                                FUNCTIONS_REGION
                        );
            }

            boolean hadSession =
                    refreshSessionLocked();

            // New session baseline is taken immediately.
            // ManualTestsActivity calls start() near the beginning of onCreate,
            // therefore logs generated after that point are captured.
            if (hadSession &&
                    lastCapturedLogText == null) {

                lastCapturedLogText =
                        safeGetFullLog();
            }

            if (running) {
                return;
            }

            running = true;
        }

        MAIN.removeCallbacks(pollRunnable);
        MAIN.post(pollRunnable);
    }

    /**
     * Capture any final log lines before Activity lifecycle cleanup and
     * allow an already-started network upload to finish.
     */
    public static void flushNow(Context context) {

        if (context != null) {
            synchronized (LOCK) {
                appContext =
                        context.getApplicationContext();

                if (firebaseAuth == null) {
                    firebaseAuth =
                            FirebaseAuth.getInstance();
                }

                if (firebaseFunctions == null) {
                    firebaseFunctions =
                            FirebaseFunctions.getInstance(
                                    FUNCTIONS_REGION
                            );
                }
            }
        }

        synchronized (LOCK) {

            if (!refreshSessionLocked()) {
                return;
            }

            captureNewLogTextLocked();
        }

        sendNextBatchIfPossible();
    }

    /**
     * Stops periodic polling. Already captured lines remain queued and an
     * in-flight upload is not cancelled.
     */
    public static void stop(Context context) {

        flushNow(context);

        synchronized (LOCK) {
            running = false;
        }

        MAIN.removeCallbacks(pollRunnable);
    }

    private static final Runnable pollRunnable =
            new Runnable() {
                @Override
                public void run() {

                    boolean keepRunning;

                    synchronized (LOCK) {

                        if (running &&
                                refreshSessionLocked()) {

                            captureNewLogTextLocked();
                        }

                        keepRunning =
                                running;
                    }

                    sendNextBatchIfPossible();

                    if (keepRunning) {
                        MAIN.postDelayed(
                                this,
                                POLL_MS
                        );
                    }
                }
            };

    /**
     * Reads the customer-side pairing state written by
     * ConnectToTechnicianActivity.
     */
    private static boolean refreshSessionLocked() {

        if (appContext == null) {
            return false;
        }

        SharedPreferences prefs =
                appContext.getSharedPreferences(
                        CUSTOMER_SESSION_PREFS,
                        Context.MODE_PRIVATE
                );

        boolean connected =
                GELCustomerServiceAccess.isActive(appContext);

        String sessionId =
                prefs.getString(
                        KEY_SESSION_ID,
                        null
                );

        if (!connected ||
                sessionId == null ||
                sessionId.trim().isEmpty()) {
            // Once the lease ends, no stale logs may be transmitted
            // after a later session is established.
            activeSessionId = null;
            pendingLines.clear();
            lastCapturedLogText = "";
            return false;
        }

        sessionId =
                sessionId.trim();

        if (!sessionId.equals(activeSessionId)) {

            activeSessionId =
                    sessionId;

            pendingLines.clear();

            // Baseline any log that existed before this remote session became
            // the active customer session. This prevents old service logs from
            // leaking into a newly paired technician session.
            lastCapturedLogText =
                    safeGetFullLog();

            sequence = 0L;
        }

        return true;
    }

    private static String safeGetFullLog() {

        try {
            String text =
                    GELServiceLog.getAll();

            return text != null
                    ? text
                    : "";

        } catch (Throwable ignore) {
            return "";
        }
    }

    /**
     * GELServiceLog is append-oriented during a diagnostic run.
     * If it was cleared/replaced, the new text is treated as a fresh stream.
     */
    private static void captureNewLogTextLocked() {

        if (activeSessionId == null) {
            return;
        }

        String current =
                safeGetFullLog();

        if (current.isEmpty()) {

            if (!lastCapturedLogText.isEmpty()) {
                lastCapturedLogText = "";
            }

            return;
        }

        String delta;

        if (current.startsWith(lastCapturedLogText)) {

            delta =
                    current.substring(
                            lastCapturedLogText.length()
                    );

        } else {

            // GELServiceLog was cleared/rebuilt.
            delta =
                    current;
        }

        lastCapturedLogText =
                current;

        if (delta.isEmpty()) {
            return;
        }

        String[] lines =
                delta.split("\\r?\\n");

        // Source colors are captured only when the exact original PDF row
        // matches the transmitted log line. Never guess a color by text.
        List<GELServiceLog.PdfRow> sourceRows = GELServiceLog.getPdfRows();
        ArrayList<DiagnosticLine> captured = new ArrayList<>();
        for (String raw : lines) {
            if (raw == null || raw.trim().isEmpty()) continue;
            String line = raw.trim();
            if (line.length() > MAX_LINE_CHARS) {
                line = line.substring(0, MAX_LINE_CHARS);
            }
            captured.add(new DiagnosticLine(line, null, -1));
        }
        int row = sourceRows.size() - 1;
        for (int k = captured.size() - 1; k >= 0; k--) {
            while (row >= 0 && sourceRows.get(row).text.trim().isEmpty()) row--;
            if (row < 0) break;
            DiagnosticLine item = captured.get(k);
            GELServiceLog.PdfRow evidence = sourceRows.get(row);
            // A truncated or mismatched line must be neutral.
            if (item.text.equals(evidence.text.trim())) {
                captured.set(k, new DiagnosticLine(item.text,
                        evidence.valueColor, evidence.valueStart));
                row--;
            } else {
                // An unmatched tail is not enough evidence to color any
                // earlier line in this capture.
                break;
            }
        }
        pendingLines.addAll(captured);
    }

    private static void sendNextBatchIfPossible() {

        final String sendSessionId;
        final List<String> batch;
        final List<Map<String, Object>> styles;

        synchronized (LOCK) {

            if (inFlight ||
                    activeSessionId == null ||
                    !GELCustomerServiceAccess.isActive(appContext) ||
                    pendingLines.isEmpty() ||
                    firebaseAuth == null ||
                    firebaseFunctions == null) {

                return;
            }

            FirebaseUser user =
                    firebaseAuth.getCurrentUser();

            // Do NOT create a new anonymous identity here.
            // The backend session is bound to the UID that claimed it.
            if (user == null) {
                return;
            }

            batch = new ArrayList<>();
            styles = new ArrayList<>();
            int chars = 0;

            for (DiagnosticLine entry : pendingLines) {
                String line = entry.text;
                if (line == null || line.isEmpty()) continue;
                if (batch.size() >= MAX_LINES_PER_BATCH) break;
                if (!batch.isEmpty() && chars + line.length() > MAX_BATCH_CHARS) break;
                batch.add(line);
                Map<String, Object> style = new HashMap<>();
                style.put("valueColor", entry.valueColor != null
                        ? entry.valueColor : "");
                style.put("valueStart", entry.valueStart);
                styles.add(style);
                chars += line.length();
            }

            if (batch.isEmpty()) {
                return;
            }

            sendSessionId =
                    activeSessionId;

            inFlight = true;
        }

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "sessionId",
                sendSessionId
        );

        data.put(
                "lines",
                batch
        );
        data.put("styles", styles);

        data.put(
                "sequence",
                sequence++
        );

        data.put(
                "clientTimestamp",
                System.currentTimeMillis()
        );

        firebaseFunctions
                .getHttpsCallable(
                        "appendDiagnosticBatch"
                )
                .call(
                        data
                )
                .addOnCompleteListener(
                        task -> {

                            boolean shouldContinue;

                            synchronized (LOCK) {

                                inFlight = false;

                                if (task.isSuccessful() &&
                                        sendSessionId.equals(
                                                activeSessionId
                                        )) {

                                    int removeCount =
                                            batch.size();

                                    while (removeCount > 0 &&
                                            !pendingLines.isEmpty()) {

                                        pendingLines.removeFirst();
                                        removeCount--;
                                    }
                                }

                                shouldContinue =
                                        task.isSuccessful() &&
                                                !pendingLines.isEmpty();
                            }

                            if (shouldContinue) {

                                MAIN.post(
                                        GELRemoteDiagnosticsSync::
                                                sendNextBatchIfPossible
                                );

                            } else if (!task.isSuccessful()) {

                                boolean retry;

                                synchronized (LOCK) {
                                    retry =
                                            running;
                                }

                                if (retry) {
                                    MAIN.postDelayed(
                                            GELRemoteDiagnosticsSync::
                                                    sendNextBatchIfPossible,
                                            3000L
                                    );
                                }
                            }
                        }
                );
    }
}
