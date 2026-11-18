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
                else if (session.hasRememberedConsent()) session.requestFromUser();
                else showConsent();
            });
        }

        private void setNormalText() {
            action.setText(gr ? "Περισσότερες πληροφορίες για τη συσκευή (προαιρετικό)"
                    : "More device information (optional)");
        }

        private void onStatus(String status) {
            if (activity.isFinishing() || activity.isDestroyed()) return;
            if ("READY".equals(status)) {
                action.setText(gr ? "Shizuku ενεργό — πάτησε για τερματισμό"
                        : "Shizuku active — tap to end session");
                sink.onEvent("Shizuku (ADB Shell)",
                        gr ? "Εγκεκριμένη συνεδρία μόνο για ανάγνωση"
                           : "Authorized read-only session", true);
            } else if ("NORMAL_MODE".equals(status)) {
                setNormalText();
            } else if ("CONNECTING".equals(status)) {
                action.setText(gr ? "Shizuku — γίνεται σύνδεση…"
                        : "Shizuku — connecting…");
            } else if ("AWAITING_SHIZUKU_PERMISSION".equals(status)) {
                action.setText(gr ? "Shizuku — αναμονή έγκρισης…"
                        : "Shizuku — awaiting permission…");
            } else if ("DISCONNECTED".equals(status)) {
                action.setText(gr ? "Shizuku αποσυνδέθηκε — πάτησε για επανασύνδεση"
                        : "Shizuku disconnected — tap to reconnect");
                sink.onEvent("Shizuku", gr
                        ? "Η συνεδρία διεκόπη. Τα εργαστήρια λειτουργούν κανονικά."
                        : "Disconnected. Standard Labs are unaffected.", false);
            } else if ("SHIZUKU_NOT_RUNNING".equals(status)) {
                action.setText(gr ? "Shizuku ανενεργό — πάτησε για επανασύνδεση"
                        : "Shizuku not running — tap to reconnect");
                sink.onEvent("Shizuku", gr
                        ? "Εγκατάστησε/ξεκίνησε την εφαρμογή Shizuku. "
                          + "Σε Android 11+ χρησιμοποιείται η σύζευξη Ασύρματου debugging."
                        : "Install and start Shizuku first; Android 11+ supports "
                          + "wireless-debugging pairing.", false);
            } else if ("ADB_SHELL_ONLY".equals(status)) {
                action.setText(gr ? "Shizuku — απαιτείται λειτουργία ADB"
                        : "Shizuku — ADB mode required");
                sink.onEvent("Shizuku", gr
                        ? "Υποστηρίζεται μόνο ADB Shell, όχι λειτουργία root."
                        : "ADB Shell mode only; root mode is not accepted.", false);
            } else if ("DENIED".equals(status) ||
                    "PERMISSION_DENIED_PERMANENTLY".equals(status)) {
                action.setText(gr ? "Shizuku — δεν εγκρίθηκε η πρόσβαση"
                        : "Shizuku — permission denied");
                sink.onEvent("Shizuku", gr
                        ? "Δεν εγκρίθηκε πρόσβαση. Συνεχίζουμε με τα κανονικά Labs."
                        : "Permission not granted; normal Labs continue.", false);
            } else if ("PERMISSION_UNAVAILABLE".equals(status)) {
                action.setText(gr ? "Shizuku — απαιτείται νέα έγκριση"
                        : "Shizuku — permission needed");
                sink.onEvent("Shizuku", gr
                        ? "Η προηγούμενη άδεια δεν είναι πλέον διαθέσιμη. "
                          + "Πάτησε για νέα έγκριση."
                        : "Permission is no longer available. Tap to authorize again.", false);
            } else if ("SERVICE_UNAVAILABLE".equals(status) ||
                    "OUTDATED_SHIZUKU".equals(status)) {
                action.setText(gr ? "Shizuku — υπηρεσία μη διαθέσιμη"
                        : "Shizuku — service unavailable");
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
            if (session.hasRememberedConsent()) {
                session.restoreApprovedSession();
            } else {
                showConsent();
            }
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
            title.setText(gr ? "GEL — Πιο βαθύς έλεγχος"
                    : "GEL — Deeper device check");
            title.setTextColor(Color.WHITE);
            title.setTextSize(18f);
            title.setTypeface(null, Typeface.BOLD);
            panel.addView(title);

            // Clear, human-first choice. Advanced technical names do not belong
            // in the main question; an accessible privacy disclosure remains visible.
            TextView question = new TextView(activity);
            question.setText(gr
                    ? "Με εξουσιοδοτείς να ζητήσω από το Android περισσότερες πληροφορίες "
                      + "που δεν μου δίνει χωρίς τη συγκατάθεσή σου;"
                    : "May I ask Android for more information about your device "
                      + "that I cannot access without your permission?");
            question.setTextColor(Color.WHITE);
            question.setTextSize(18f);
            question.setTypeface(null, Typeface.BOLD);
            question.setPadding(0, dp(14), 0, dp(10));
            panel.addView(question);

            TextView body = new TextView(activity);
            body.setText(gr
                    ? "Είναι προαιρετικό. Μπορώ να ελέγξω πιο αναλυτικά την μπαταρία, "
                      + "τη μνήμη, την αποθήκευση και πιθανά σφάλματα. "
                      + "Ονόματα εφαρμογών και καταγεγραμμένοι χρόνοι πρόσβασης "
                      + "σε κάμερα, μικρόφωνο ή τοποθεσία μπορεί να φανούν "
                      + "στην αναφορά. Δεν αλλάζω ρυθμίσεις ή αρχεία. "
                      + "Θα χρειαστεί και έγκριση από την εφαρμογή Shizuku. "
                      + "Αν πεις όχι, όλα τα εργαστήρια συνεχίζουν κανονικά."
                    : "This is optional. I can check your battery, memory, storage "
                      + "and possible system errors in more detail. App names and "
                      + "recorded times of camera, microphone or location access "
                      + "may appear in the report. I won't change settings or files. "
                      + "The Shizuku app will also ask for permission. "
                      + "If you decline, all tests still work normally.");
            body.setTextColor(Color.WHITE);
            body.setTextSize(14f);
            body.setPadding(0, 0, 0, dp(14));
            panel.addView(body);

            LinearLayout actions = new LinearLayout(activity);
            actions.setOrientation(LinearLayout.HORIZONTAL);
            Button cancel = new com.gel.cleaner.GelButton(activity);
            cancel.setText(gr ? "ΟΧΙ" : "NO");
            cancel.setTextColor(Color.WHITE);
            // Declining an optional capability is neutral, not an error.
            cancel.setBackgroundColor(0xFF444444);
            Button allow = new com.gel.cleaner.GelButton(activity);
            allow.setText(gr ? "ΝΑΙ, ΕΓΚΡΙΝΩ" : "YES, ALLOW");
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
