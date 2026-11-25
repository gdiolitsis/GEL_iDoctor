package com.gel.cleaner;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
            Toast.makeText(this, AppLang.isGreek(this)
                    ? "Δεν υπάρχει ενεργή σύνδεση πελάτη."
                    : "No active customer connection.", Toast.LENGTH_LONG).show();
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

        com.gel.cleaner.GelButton exit = new com.gel.cleaner.GelButton(this);
        exit.setAllCaps(false);
        exit.setText(gr ? "ΕΠΙΣΤΡΟΦΗ" : "BACK");
        exit.setTextColor(Color.WHITE);
        exit.setTextSize(16f);
        exit.setOnClickListener(v -> finish());
        root.addView(exit);
        setContentView(root);
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
                    StringBuilder buffer = new StringBuilder();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Object lines = doc.get("lines");
                        if (!(lines instanceof List<?>)) continue;
                        for (Object line : (List<?>) lines) {
                            if (line instanceof String) {
                                buffer.append((String) line).append('\n');
                            }
                        }
                    }
                    if (buffer.length() > 0) {
                        logText.setText(buffer.toString());
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
