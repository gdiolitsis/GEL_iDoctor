package com.gel.cleaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A single optional consent control BEFORE the numbered Labs.
 * Does not add a Lab or interrupt normal diagnostics. The Shizuku server
 * itself provides the independent operating-system permission prompt.
 */
public final class GelShizukuUi {
    public interface EventSink {
        void onEvent(String label, String detail, boolean verifiedOk);
    }

    private GelShizukuUi() { }

    public static final class Widget {
        private final Activity activity;
        private final boolean gr;
        private final Button action;
        private final GelShizukuDiagnosticSession session;
        private final EventSink sink;
        // This Activity entry offers enhanced diagnostics at most once.
        // Reopening the manual control remains possible after a decline.
        private boolean entryOfferShown;

        public Widget(Activity activity, LinearLayout container, boolean gr, EventSink sink) {
            this.activity = activity;
            this.gr = gr;
            this.sink = sink;
            action = new com.gel.cleaner.GelButton(activity);
            action.setAllCaps(false);
            action.setTextColor(Color.WHITE);
            action.setBackgroundResource(R.drawable.gel_btn_outline_selector);
            action.setMinHeight(dp(58));
            action.setPadding(dp(12), dp(10), dp(12), dp(10));
            setNormalText();
            container.addView(action);
            session = new GelShizukuDiagnosticSession(activity, this::onStatus);
            action.setOnClickListener(v -> {
                if (session.isReady()) session.stop();
                else showConsent();
            });
        }

        private void setNormalText() {
            action.setText(gr ? "Διευρυμένος έλεγχος Shizuku — ΠΡΟΑΙΡΕΤΙΚΟ"
                    : "Enhanced diagnostics with Shizuku — OPTIONAL");
        }

        private void onStatus(String status) {
            if (activity.isFinishing() || activity.isDestroyed()) return;
            if ("READY".equals(status)) {
                action.setText(gr ? "Shizuku ενεργό — πάτησε για τερματισμό"
                        : "Shizuku active — tap to end session");
                sink.onEvent("Shizuku (ADB Shell)",
                        gr ? "Εγκεκριμένη συνεδρία μόνο για ανάγνωση"
                           : "Authorized read-only session", true);
            } else if ("NORMAL_MODE".equals(status) || "DISCONNECTED".equals(status)) {
                setNormalText();
                if ("DISCONNECTED".equals(status))
                    sink.onEvent("Shizuku", gr
                            ? "Η συνεδρία διεκόπη. Τα εργαστήρια λειτουργούν κανονικά."
                            : "Disconnected. Standard Labs are unaffected.", false);
            } else if ("SHIZUKU_NOT_RUNNING".equals(status)) {
                sink.onEvent("Shizuku", gr
                        ? "Εγκατάστησε/ξεκίνησε την εφαρμογή Shizuku. "
                          + "Σε Android 11+ χρησιμοποιείται η σύζευξη Ασύρματου debugging."
                        : "Install and start Shizuku first; Android 11+ supports "
                          + "wireless-debugging pairing.", false);
            } else if ("ADB_SHELL_ONLY".equals(status)) {
                sink.onEvent("Shizuku", gr
                        ? "Υποστηρίζεται μόνο ADB Shell, όχι λειτουργία root."
                        : "ADB Shell mode only; root mode is not accepted.", false);
            } else if ("DENIED".equals(status) ||
                    "PERMISSION_DENIED_PERMANENTLY".equals(status)) {
                sink.onEvent("Shizuku", gr
                        ? "Δεν εγκρίθηκε πρόσβαση. Συνεχίζουμε με τα κανονικά Labs."
                        : "Permission not granted; normal Labs continue.", false);
            } else if ("SERVICE_UNAVAILABLE".equals(status) ||
                    "OUTDATED_SHIZUKU".equals(status)) {
                sink.onEvent("Shizuku", gr
                        ? "Μη διαθέσιμη συμβατή υπηρεσία. Χρήση κανονικών Android APIs."
                        : "Compatible service unavailable; Android APIs remain usable.", false);
            }
        }

        /** Automatically offer once when the owner opens the Labs screen. */
        public void offerOnEntry() {
            if (entryOfferShown) return;
            entryOfferShown = true;
            if (activity.isFinishing() || activity.isDestroyed() || session.isReady()) return;
            showConsent();
        }

        private void showConsent() {
            LinearLayout panel = new LinearLayout(activity);
            panel.setOrientation(LinearLayout.VERTICAL);
            panel.setPadding(dp(18), dp(18), dp(18), dp(18));
            GradientDrawable back = new GradientDrawable();
            back.setColor(Color.BLACK);
            back.setCornerRadius(dp(14));
            back.setStroke(dp(2), 0xFFFFD700);
            panel.setBackground(back);

            TextView title = new TextView(activity);
            title.setText(gr ? "GEL — Εξουσιοδότηση Shizuku"
                    : "GEL — Shizuku authorization");
            title.setTextColor(Color.WHITE);
            title.setTextSize(18f);
            title.setTypeface(null, Typeface.BOLD);
            panel.addView(title);

            TextView body = new TextView(activity);
            body.setText(gr
                    ? "Προαιρετική, μόνο για ανάγνωση, πρόσβαση ADB Shell σε χωρητικότητα "
                      + "μπαταρίας, UFS/NAND, κρίσιμα σφάλματα, θερμική κατάσταση, "
                      + "πίεση RAM, λόγο εκκίνησης και χρόνους AppOps. "
                      + "Οι διαθέσιμοι χρόνοι και τα ονόματα εφαρμογών ενδέχεται "
                      + "να εμφανίζονται στα logs/PDF της διάγνωσης. "
                      + "Χωρίς root, αλλαγές συστήματος ή πλήρη καταγραφή logs. "
                      + "Απαιτείται και ξεχωριστή έγκριση από το Shizuku. "
                      + "Τα 29 Labs λειτουργούν κανονικά χωρίς αυτό."
                    : "Optional read-only ADB Shell access to battery capacity, "
                      + "UFS/NAND wear indicators, critical crashes, thermal state, "
                      + "RAM-pressure events, boot reason and AppOps access times. "
                      + "App names and access times may appear in diagnostic "
                      + "logs/PDF exports. No root, system changes or full-log capture. "
                      + "Shizuku requests separate permission. All 29 Labs work without it.");
            body.setTextColor(Color.WHITE);
            body.setTextSize(14f);
            body.setPadding(0, dp(14), 0, dp(14));
            panel.addView(body);

            LinearLayout actions = new LinearLayout(activity);
            actions.setOrientation(LinearLayout.HORIZONTAL);
            Button cancel = new com.gel.cleaner.GelButton(activity);
            cancel.setText(gr ? "ΧΩΡΙΣ SHIZUKU" : "NORMAL LABS");
            cancel.setTextColor(Color.WHITE);
            cancel.setBackgroundColor(0xFFBB2222);
            Button allow = new com.gel.cleaner.GelButton(activity);
            allow.setText(gr ? "ΕΓΚΡΙΣΗ" : "AUTHORIZE");
            allow.setTextColor(Color.BLACK);
            allow.setBackgroundColor(0xFF39FF14);
            actions.addView(cancel, new LinearLayout.LayoutParams(0, dp(60), 1));
            actions.addView(allow, new LinearLayout.LayoutParams(0, dp(60), 1));
            panel.addView(actions);

            AlertDialog dialog = new AlertDialog.Builder(activity,
                    android.R.style.Theme_Material_Dialog_NoActionBar)
                    .setView(panel).create();
            dialog.setCanceledOnTouchOutside(false);
            cancel.setOnClickListener(v -> dialog.dismiss());
            allow.setOnClickListener(v -> {
                dialog.dismiss();
                session.requestFromUser();
            });
            dialog.show();
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));
        }

        public boolean isReady() { return session.isReady(); }
        public GelShizukuEvidence.Capacity getCapacity() {
            return session.getCapacity();
        }
        public void readAsync(int operation, GelShizukuDiagnosticSession.EvidenceCallback callback) {
            session.readAsync(operation, callback);
        }
        public void close() { session.close(); }

        private int dp(float size) {
            return (int) (size * activity.getResources().getDisplayMetrics().density + 0.5f);
        }
    }
}
