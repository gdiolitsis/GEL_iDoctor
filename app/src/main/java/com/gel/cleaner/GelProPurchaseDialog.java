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
        show(activity, billing, AppLang.isGreek(activity));
    }
    private static void show(Activity activity, GelBillingManager billing, boolean gr) {
        LinearLayout box = new LinearLayout(activity);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = Math.round(20 * activity.getResources().getDisplayMetrics().density);
        box.setPadding(pad, pad, pad, pad);
        box.setBackgroundColor(Color.rgb(16,16,16));
        TextView message = new TextView(activity);
        message.setTextColor(0xFF00FF9C);
        message.setTextSize(17);
        message.setText(gr
            ? "GEL PRO — Πλήρης πρόσβαση\n\nΌλα τα εργαστήρια και απεριόριστες αναφορές με λογότυπο GEL. Η εξατομίκευση αγοράζεται χωριστά και απαιτεί ενεργό PRO.\n\nΗ διάρκεια ακολουθεί τον μήνα του Google Play. Η αυτόματη ανανέωση χρεώνεται κάθε μήνα μέχρι ακύρωσης."
            : "GEL PRO — Full access\n\nAll labs and unlimited reports with the GEL logo. Personalisation is purchased separately and requires active PRO.\n\nDuration follows the Google Play month. Auto-renewal charges every month until cancelled.");
        box.addView(message);
        ScrollView scroll = new ScrollView(activity); scroll.addView(box);
        AlertDialog dialog = new AlertDialog.Builder(activity).setView(scroll).create();
        GelButton language = button(activity, box, gr ? "English" : "Ελληνικά");
        language.setOnClickListener(v -> { dialog.dismiss(); show(activity, billing, !gr); });
        GelButton prepaid = button(activity, box, gr ? "1 μήνας — 4,99€ · Χωρίς αυτόματη ανανέωση" : "1 month — €4.99 · No auto-renewal");
        GelButton monthly = button(activity, box, gr ? "4,99€/μήνα · Με αυτόματη ανανέωση" : "€4.99/month · Auto-renewing");
        prepaid.setOnClickListener(v -> { if (billing != null) { dialog.dismiss(); billing.launchGelProPlan(activity, true); } });
        monthly.setOnClickListener(v -> { if (billing != null) { dialog.dismiss(); billing.launchGelProPlan(activity, false); } });
        button(activity, box, gr ? "Όχι τώρα" : "Not now").setOnClickListener(v -> dialog.dismiss());
        dialog.show(); GelButtonLayout.fitDialog(dialog);
    }
    private static GelButton button(Activity activity, LinearLayout box, String label) {
        GelButton button = new GelButton(activity); button.setText(label); button.setAllCaps(false);
        box.addView(button, new LinearLayout.LayoutParams(-1, -2)); return button;
    }
}
