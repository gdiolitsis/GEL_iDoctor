package com.gel.cleaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Shared entry point: the selected renewal type must match an exact Play base plan. */
public final class GelProPurchaseDialog {
    private GelProPurchaseDialog() {}
    public static void show(Activity activity, GelBillingManager billing) {
        show(activity, billing, null);
    }
    public static void show(Activity activity, GelBillingManager billing, Runnable onCancelled) {
        show(activity, billing, AppLang.isGreek(activity), onCancelled);
    }
    private static void show(Activity activity, GelBillingManager billing, boolean gr, Runnable onCancelled) {
        LinearLayout box = new LinearLayout(activity);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = Math.round(20 * activity.getResources().getDisplayMetrics().density);
        box.setPadding(pad, pad, pad, pad);
        box.setBackgroundColor(Color.rgb(16,16,16));
        TextView message = new TextView(activity);
        message.setTextColor(0xFF00FF9C);
        message.setTextSize(17);
        message.setText(gr
            ? "GEL PRO — Πρόσβαση ενός μήνα\n\nΌλα τα εργαστήρια και απεριόριστες αναφορές με λογότυπο GEL. Η εξατομίκευση αγοράζεται χωριστά και απαιτεί ενεργό PRO.\n\nΗ διάρκεια ακολουθεί τον μήνα του Google Play. Η αυτόματη ανανέωση χρεώνεται κάθε μήνα μέχρι ακύρωσης."
            : "GEL PRO — One-month access\n\nAll labs and unlimited reports with the GEL logo. Personalisation is purchased separately and requires active PRO.\n\nDuration follows the Google Play month. Auto-renewal charges every month until cancelled.");
        box.addView(message);
        ScrollView scroll = new ScrollView(activity); scroll.addView(box);
        AlertDialog dialog = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_NoActionBar).setView(scroll).create();
        GelButton language = button(activity, box, gr ? "English" : "Ελληνικά");
        language.setOnClickListener(v -> { dialog.dismiss(); show(activity, billing, !gr, onCancelled); });
        GelButton trial = button(activity, box, gr ? "Δωρεάν δοκιμή FULL ACCESS — 1 ώρα" : "Free FULL ACCESS trial — 1 hour");
        trial.setOnClickListener(v -> {
            dialog.dismiss();
            activity.startActivity(new android.content.Intent(activity, GelProAccountActivity.class)
                    .putExtra(GelProAccountActivity.EXTRA_TRIAL, true));
        });
        GelButton prepaid = button(activity, box, gr ? "1 μήνας — 4,99€ · Χωρίς αυτόματη ανανέωση" : "1 month — €4.99 · No auto-renewal");
        GelButton monthly = button(activity, box, gr ? "4,99€/μήνα · Με αυτόματη ανανέωση" : "€4.99/month · Auto-renewing");
        prepaid.setOnClickListener(v -> { if (billing != null) { dialog.dismiss(); billing.launchGelProPlan(activity, true); } });
        monthly.setOnClickListener(v -> { if (billing != null) { dialog.dismiss(); billing.launchGelProPlan(activity, false); } });
        button(activity, box, gr ? "Όχι τώρα" : "Not now").setOnClickListener(v -> {
            if (onCancelled != null) onCancelled.run();
            dialog.dismiss();
        });
        dialog.setOnCancelListener(d -> { if (onCancelled != null) onCancelled.run(); });
        dialog.show(); GelButtonLayout.fitDialog(dialog);
    }
    private static GelButton button(Activity activity, LinearLayout box, String label) {
        GelButton button = new GelButton(activity); button.setText(label); button.setAllCaps(false);
        button.setTextColor(Color.WHITE);
        button.setTextSize(16);
        button.setTypeface(null, android.graphics.Typeface.BOLD);
        button.setSupportBackgroundTintList(null);
        button.setBackgroundTintList(null);
        android.graphics.drawable.GradientDrawable background = new android.graphics.drawable.GradientDrawable();
        background.setColor(0xFF101010);
        background.setCornerRadius(12 * activity.getResources().getDisplayMetrics().density);
        background.setStroke(Math.round(2 * activity.getResources().getDisplayMetrics().density), 0xFFFFD700);
        button.setBackground(background);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = Math.round(12 * activity.getResources().getDisplayMetrics().density);
        box.addView(button, params); return button;
    }
}
