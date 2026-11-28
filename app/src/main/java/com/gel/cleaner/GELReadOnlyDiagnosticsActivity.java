package com.gel.cleaner;

import android.content.SharedPreferences;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

/**
 * Technician's READ-ONLY view of diagnostic logs from the paired customer.
 * Never opens screen sharing, a touch channel or a remote command service.
 * Requires live server-defined session deadline and authenticated Firestore rules.
 */
public final class GELReadOnlyDiagnosticsActivity extends GELAutoActivityHook {
    private static final String PREFS = "GEL_REPAIR_SESSION";
    private static final String KEY_ID = "session_id";
    private static final String KEY_CONNECTED = "session_connected";
    private static final String KEY_ENDS = "service_ends_at";

    private String sessionId;
    private ListenerRegistration logsListener;
    private TextView status;
    private TextView logText;
    private final Handler timer = new Handler(Looper.getMainLooper());
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (!isLive()) {
                stopListening();
                if (status != null) {
                    status.setText(AppLang.isGreek(GELReadOnlyDiagnosticsActivity.this)
                            ? "Η οκτάωρη συνεδρία ολοκληρώθηκε. Δεν λαμβάνονται νέα δεδομένα."
                            : "Eight-hour service session expired. No new data is received.");
                    status.setTextColor(0xFFFFD700);
                }
                return;
            }
            timer.postDelayed(this, 15_000L);
        }
    };

    @Override protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        sessionId = getIntent().getStringExtra("sessionId");
        if (!isLive()) {
            showInactiveSession(AppLang.isGreek(this));
            finish();
            return;
        }

        final boolean gr = AppLang.isGreek(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(18), dp(16), dp(20));
        root.setBackgroundColor(0xFF101010);

        TextView title = new TextView(this);
        title.setText(gr ? "ΔΙΑΓΝΩΣΤΙΚΑ ΠΕΛΑΤΗ — ΜΟΝΟ ΑΝΑΓΝΩΣΗ"
                : "CUSTOMER DIAGNOSTICS — READ ONLY");
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(19f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(title);

        status = new TextView(this);
        status.setPadding(0, dp(14), 0, dp(14));
        status.setTextColor(0xFF39FF14);
        status.setText(gr ? "● Συνδεδεμένος. Αναμονή αποτελεσμάτων εργαστηρίων..."
                : "● Connected. Waiting for diagnostic results...");
        root.addView(status);

        ScrollView scroll = new ScrollView(this);
        logText = new TextView(this);
        logText.setTextColor(Color.WHITE);
        logText.setTextSize(13f);
        logText.setTextIsSelectable(true);
        logText.setPadding(dp(8), dp(8), dp(8), dp(8));
        logText.setText(gr ? "Μόλις ο πελάτης εκτελέσει εργαστήρια, τα logs θα εμφανιστούν εδώ."
                : "When the customer runs labs, their logs will appear here.");
        scroll.addView(logText);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));

        com.gel.cleaner.GelButton report = new com.gel.cleaner.GelButton(this);
        report.setAllCaps(false);
        report.setText(gr ? "ΕΞΑΓΩΓΗ SERVICE REPORT ΠΕΛΑΤΗ"
                : "EXPORT CUSTOMER SERVICE REPORT");
        GELServiceUi.styleButton(report, R.drawable.gel_btn_green_gold, Color.BLACK);
        report.setTextSize(16f);
        report.setOnClickListener(v -> {
            if (!isLive()) {
                showInactiveSession(gr);
                return;
            }
            GELServiceSubscriptionGate.requireTechnicianAccess(this, () -> {
                if (!isLive()) {
                    showInactiveSession(gr);
                    return;
                }
                Intent intent = new Intent(this, ServiceReportActivity.class);
                intent.putExtra(GELRemoteServiceReportLoader.EXTRA_SESSION_ID, sessionId);
                startActivity(intent);
            });
        });
        root.addView(report);

        com.gel.cleaner.GelButton exit = new com.gel.cleaner.GelButton(this);
        exit.setAllCaps(false);
        exit.setText(gr ? "ΕΠΙΣΤΡΟΦΗ" : "BACK");
        GELServiceUi.styleButton(exit, R.drawable.gel_btn_danger_outline, Color.WHITE);
        exit.setTextSize(16f);
        exit.setOnClickListener(v -> finish());
        LinearLayout.LayoutParams exitParams = new LinearLayout.LayoutParams(-1, -2);
        exitParams.topMargin = dp(8);
        root.addView(exit, exitParams);
        setContentView(root);
    }

    private void showInactiveSession(boolean gr) {
        Toast.makeText(this, gr ? "Δεν υπάρχει ενεργή σύνδεση πελάτη."
                : "No active customer connection.", Toast.LENGTH_LONG).show();
    }

    private boolean isLive() {
        if (sessionId == null || sessionId.trim().isEmpty()) return false;
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        return sessionId.equals(p.getString(KEY_ID, ""))
                && p.getBoolean(KEY_CONNECTED, false)
                && p.getLong(KEY_ENDS, 0L) > System.currentTimeMillis();
    }

    @Override protected void onResume() {
        super.onResume();
        if (isLive()) {
            attachLogs();
            timer.removeCallbacks(tick);
            timer.post(tick);
        } else {
            stopListening();
        }
    }

    private void attachLogs() {
        stopListening();
        logsListener = FirebaseFirestore.getInstance()
                .collection("service_sessions").document(sessionId)
                .collection("diagnostics")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .limitToLast(150)
                .addSnapshotListener((snapshot, error) -> {
                    if (!isLive()) {
                        stopListening();
                        return;
                    }
                    if (error != null) {
                        status.setText(AppLang.isGreek(this)
                                ? "Αδυναμία ανάγνωσης διαγνωστικών από το Firebase."
                                : "Cannot read diagnostic logs from Firebase.");
                        status.setTextColor(0xFFFF5555);
                        return;
                    }
                    if (snapshot == null || snapshot.isEmpty()) return;
                    SpannableStringBuilder buffer = new SpannableStringBuilder();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Object rawLines = doc.get("lines");
                        if (!(rawLines instanceof List<?>)) continue;
                        List<?> lines = (List<?>) rawLines;
                        List<?> styles = GELRemoteServiceReportLoader.diagnosticStyles(doc, lines);
                        for (int i = 0; i < lines.size(); i++) {
                            if (lines.get(i) instanceof String) {
                                GELServiceUi.appendDiagnosticRow(buffer,
                                        GELRemoteServiceReportLoader.diagnosticRow((String) lines.get(i), styles, i));
                            }
                        }
                    }
                    if (buffer.length() > 0) {
                        logText.setText(buffer);
                        status.setTextColor(0xFF39FF14);
                        status.setText(AppLang.isGreek(this)
                                ? "● Λήψη διαγνωστικών αποτελεσμάτων — μόνο ανάγνωση"
                                : "● Receiving diagnostic results — read only");
                    }
                });
    }

    private void stopListening() {
        if (logsListener != null) {
            logsListener.remove();
            logsListener = null;
        }
    }

    @Override protected void onPause() {
        timer.removeCallbacks(tick);
        stopListening();
        super.onPause();
    }
}
