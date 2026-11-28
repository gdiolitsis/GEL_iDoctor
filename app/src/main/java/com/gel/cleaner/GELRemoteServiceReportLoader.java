package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Independent, server-fetched customer report source.
 * Never writes to or reads the technician device's local GELServiceLog.
 * Fails closed on missing data, an expired session, incomplete pages or
 * oversized diagnostics rather than exporting a partial/misidentified PDF.
 */
public final class GELRemoteServiceReportLoader {
    public static final String EXTRA_SESSION_ID = "gel_customer_report_session_id";
    private static final String PREFS = "GEL_REPAIR_SESSION";
    private static final int PAGE_BATCHES = 100;
    private static final int MAX_BATCHES = 1500;
    private static final int MAX_CHARS = 1_500_000;
    private static final int MAX_LINES = 40_000;

    public interface Callback {
        void completed(Report result, String error);
    }

    public static final class Report {
        public final String sessionId;
        public final String text;
        public final List<GELServiceLog.PdfRow> rows;

        private Report(String sessionId, String text, List<GELServiceLog.PdfRow> rows) {
            this.sessionId = sessionId;
            this.text = text;
            this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
        }
    }

    private GELRemoteServiceReportLoader() {}

    public static boolean sessionValid(Context context, String sessionId) {
        if (context == null || sessionId == null || sessionId.trim().isEmpty()
                || !GELServiceSubscriptionGate.hasTechnicianAccess(context)) return false;
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sessionId.equals(p.getString("session_id", ""))
                && p.getBoolean("session_connected", false)
                && p.getLong("service_ends_at", 0L) > System.currentTimeMillis();
    }

    public static void fetch(Context context, String sessionId, Callback callback) {
        if (!sessionValid(context, sessionId)) {
            callback.completed(null, "The technician Service Session is no longer active.");
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.completed(null, "Firebase authentication unavailable.");
            return;
        }
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("service_sessions").document(sessionId).get(Source.SERVER)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null ||
                            !task.getResult().exists()) {
                        callback.completed(null, "Cannot verify the customer Service Session.");
                        return;
                    }
                    DocumentSnapshot session = task.getResult();
                    Timestamp deadline = session.getTimestamp("serviceEndsAt");
                    if (!"CONNECTED".equals(session.getString("status")) ||
                            deadline == null ||
                            deadline.toDate().getTime() <= System.currentTimeMillis() ||
                            !user.getUid().equals(session.getString("technicianUid")) ||
                            session.getString("customerUid") == null ||
                            !sessionValid(context, sessionId)) {
                        callback.completed(null, "The customer session or technician identity is invalid.");
                        return;
                    }
                    Query base = firestore.collection("service_sessions")
                            .document(sessionId).collection("diagnostics")
                            .orderBy("createdAt", Query.Direction.ASCENDING);
                    Accumulator accumulator = new Accumulator(sessionId);
                    fetchPage(context, sessionId, base, null, accumulator, callback);
                });
    }

    private static void fetchPage(Context context, String sessionId, Query base,
                                  DocumentSnapshot cursor, Accumulator result,
                                  Callback callback) {
        if (!sessionValid(context, sessionId)) {
            callback.completed(null, "Service session expired during report loading.");
            return;
        }
        Query page = cursor == null ? base.limit(PAGE_BATCHES)
                : base.startAfter(cursor).limit(PAGE_BATCHES);
        page.get(Source.SERVER).addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null ||
                    !sessionValid(context, sessionId)) {
                callback.completed(null, "Failed to load all customer diagnostics from the server.");
                return;
            }
            QuerySnapshot snapshot = task.getResult();
            List<DocumentSnapshot> docs = snapshot.getDocuments();
            if (result.batches + docs.size() > MAX_BATCHES) {
                callback.completed(null, "Customer diagnostics exceed the safe report size limit.");
                return;
            }
            for (DocumentSnapshot doc : docs) {
                if (!result.append(doc)) {
                    callback.completed(null, "Customer report data is incomplete or exceeds the safe report size limit.");
                    return;
                }
            }
            if (docs.size() == PAGE_BATCHES) {
                fetchPage(context, sessionId, base, docs.get(docs.size() - 1),
                        result, callback);
                return;
            }
            if (result.logLines == 0) {
                callback.completed(null, "No customer diagnostic measurements have arrived yet.");
                return;
            }
            callback.completed(result.build(), null);
        });
    }

    static List<?> diagnosticStyles(DocumentSnapshot doc, List<?> lines) {
        Object raw = doc.get("styles");
        if (raw instanceof List<?> && ((List<?>) raw).size() == lines.size()) {
            return (List<?>) raw;
        }
        return Collections.emptyList();
    }

    static GELServiceLog.PdfRow diagnosticRow(String line, List<?> styles, int index) {
        if (index < styles.size() && styles.get(index) instanceof Map<?, ?>) {
            Map<?, ?> style = (Map<?, ?>) styles.get(index);
            Object color = style.get("valueColor");
            Object offset = style.get("valueStart");
            return GELServiceLog.detachedPdfRow(line,
                    color instanceof String ? (String) color : null,
                    offset instanceof Number ? ((Number) offset).intValue() : -1);
        }
        return GELServiceLog.detachedPdfRow(line, null, -1);
    }

    private static final class Accumulator {
        final String sessionId;
        final StringBuilder text = new StringBuilder();
        final ArrayList<GELServiceLog.PdfRow> rows = new ArrayList<>();
        int logLines;
        int batches;

        Accumulator(String sessionId) {
            this.sessionId = sessionId;
            addRow("SERVICE REPORT — CUSTOMER DEVICE / ΣΥΣΚΕΥΗ ΠΕΛΑΤΗ", null, -1);
            addRow("Service Session ID: " + sessionId, null, -1);
            addRow("Source / Πηγή: GEL iDoctor customer device diagnostics", null, -1);
        }

        private void addRow(String line, String color, int valueStart) {
            addRow(GELServiceLog.detachedPdfRow(line, color, valueStart));
        }

        private void addRow(GELServiceLog.PdfRow row) {
            rows.add(row);
            text.append(row.text).append('\n');
        }

        boolean append(@NonNull DocumentSnapshot doc) {
            Object rawLines = doc.get("lines");
            if (!(rawLines instanceof List<?>)) return false;
            List<?> lines = (List<?>) rawLines;
            List<?> styles = diagnosticStyles(doc, lines);
            for (int k = 0; k < lines.size(); k++) {
                Object raw = lines.get(k);
                if (!(raw instanceof String)) return false;
                String line = ((String) raw).trim();
                if (line.isEmpty()) continue;
                if (text.length() + line.length() > MAX_CHARS ||
                        logLines + 1 > MAX_LINES) return false;
                addRow(diagnosticRow(line, styles, k));
                logLines++;
            }
            batches++;
            return true;
        }

        Report build() {
            return new Report(sessionId, text.toString(), rows);
        }
    }
}
