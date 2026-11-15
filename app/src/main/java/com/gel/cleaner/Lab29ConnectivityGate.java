package com.gel.cleaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Lab 29 connection advisory, not a claim that OEM web lookup is implemented.
 * The current sourced profile can still be compared offline.
 */
public final class Lab29ConnectivityGate {
    private Lab29ConnectivityGate() {}

    static boolean isUsableNetwork(boolean active, boolean internet, boolean validated) {
        return active && internet && validated;
    }

    public static boolean hasValidatedInternet(Context context) {
        if (context == null) return false;
        try {
            ConnectivityManager manager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager == null) return false;
            Network network = manager.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(network);
            return capabilities != null
                    && isUsableNetwork(
                            true,
                            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET),
                            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED));
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    public static void runOrPrompt(Activity activity, Runnable runLab) {
        if (activity == null || runLab == null || activity.isFinishing()) return;
        if (hasValidatedInternet(activity)) {
            runLab.run();
            return;
        }
        final boolean greek = AppLang.isGreek(activity);
        final float scale = activity.getResources().getDisplayMetrics().density;
        final int pad = Math.round(20f * scale);

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad, pad, pad);
        GradientDrawable background = new GradientDrawable();
        background.setColor(0xFF101010);
        background.setCornerRadius(12f * scale);
        background.setStroke(Math.max(1, Math.round(2f * scale)), 0xFFFFD700);
        root.setBackground(background);

        TextView title = new TextView(activity);
        title.setText(greek ? "LAB 29 — Απαιτείται διαδίκτυο" : "LAB 29 — Internet connection");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView message = new TextView(activity);
        message.setText(greek
                ? "Για διαδικτυακή αναζήτηση των επίσημων εργοστασιακών προδιαγραφών χρειάζεται ενεργό Wi-Fi ή δεδομένα κινητής τηλεφωνίας.\n\n"
                    + "Στην παρούσα δοκιμαστική έκδοση δεν έχει ακόμη ενεργοποιηθεί η αυτόματη διαδικτυακή αναζήτηση μοντέλων. Μπορείς όμως να συνεχίσεις με τις διαθέσιμες, ήδη τεκμηριωμένες προδιαγραφές και τους τοπικούς ελέγχους."
                : "Online search for official factory specifications requires active Wi-Fi or mobile data.\n\n"
                    + "Automatic online model lookup is not yet enabled in this test build. You can continue using available, already sourced specifications and local checks.");
        message.setTextColor(Color.WHITE);
        message.setTextSize(14f);
        message.setPadding(0, Math.round(12f * scale), 0, Math.round(12f * scale));
        ScrollView messageScroll = new ScrollView(activity);
        messageScroll.setFillViewport(false);
        messageScroll.addView(message);
        root.addView(messageScroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Math.min(Math.round(220f * scale),
                        activity.getResources().getDisplayMetrics().heightPixels / 3)));

        Button retry = makeButton(activity, greek ? "ΕΠΑΝΕΛΕΓΧΟΣ ΣΥΝΔΕΣΗΣ" : "RECHECK CONNECTION");
        Button offline = makeButton(activity, greek ? "ΣΥΝΕΧΕΙΑ ΧΩΡΙΣ ΙΝΤΕΡΝΕΤ" : "CONTINUE OFFLINE");
        Button cancel = makeButton(activity, greek ? "ΑΚΥΡΩΣΗ" : "CANCEL");
        root.addView(retry, buttonParams(scale));
        root.addView(offline, buttonParams(scale));
        root.addView(cancel, buttonParams(scale));

        AlertDialog dialog = new AlertDialog.Builder(
                activity, android.R.style.Theme_Material_Dialog_NoActionBar)
                .setView(root)
                .create();
        retry.setOnClickListener(view -> {
            if (hasValidatedInternet(activity)) {
                dialog.dismiss();
                runLab.run();
            } else {
                Toast.makeText(activity,
                        greek ? "Δεν επιβεβαιώθηκε σύνδεση στο διαδίκτυο."
                              : "Internet connection is not validated.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        offline.setOnClickListener(view -> {
            dialog.dismiss();
            runLab.run();
        });
        cancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private static LinearLayout.LayoutParams buttonParams(float scale) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Math.round(48f * scale));
        params.topMargin = Math.round(7f * scale);
        return params;
    }

    private static Button makeButton(Activity activity, String text) {
        Button button = new GelButton(activity);
        button.setAllCaps(false);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(13f);
        button.setBackgroundResource(R.drawable.gel_btn_outline_selector);
        return button;
    }
}
