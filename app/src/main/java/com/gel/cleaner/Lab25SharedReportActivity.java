package com.gel.cleaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Explicit consent bridge for Android Sharesheet bugreports.
 * This is NOT a new Lab; it only forwards a granted URI to existing Lab 25.
 * No file system scanning, shell access, root, network or raw log retention.
 */
public final class Lab25SharedReportActivity extends Activity {
    static final String ACTION_IMPORT_LAB25 = "com.gel.iDoctor.action.IMPORT_LAB25_REPORT";

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        final boolean gr = AppLang.isGreek(this);
        final Uri uri = incomingSharedUri(getIntent());
        if (uri == null) {
            finish();
            return;
        }

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(18), dp(18), dp(18), dp(16));
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.BLACK);
        background.setCornerRadius(dp(14));
        background.setStroke(dp(2), 0xFFFFD700);
        box.setBackground(background);

        TextView title = new TextView(this);
        title.setText(gr ? "LAB 25 — Ανάλυση αναφοράς Android"
                         : "LAB 25 — Android bugreport analysis");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        box.addView(title);

        TextView description = new TextView(this);
        description.setText(gr
                ? "Η αναφορά μπορεί να περιέχει προσωπικά δεδομένα. "
                  + "Η ανάλυση κρίσιμων σφαλμάτων γίνεται μόνο στη συσκευή. "
                  + "Δεν ανεβάζουμε ή αποθηκεύουμε ολόκληρο το αρχείο. "
                  + "Η γνησιότητα της αναφοράς δεν επαληθεύεται. Να συνεχίσουμε;"
                : "This report can contain personal data. Critical-error analysis "
                  + "runs only on this device. We do not upload or store the full file. "
                  + "Report authenticity cannot be verified. Continue?");
        description.setTextColor(Color.WHITE);
        description.setTextSize(14f);
        description.setPadding(0, dp(12), 0, dp(12));
        box.addView(description);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        Button cancel = new Button(this);
        cancel.setText(gr ? "ΑΚΥΡΟ" : "CANCEL");
        cancel.setTextColor(Color.WHITE);
        cancel.setBackgroundColor(0xFFAF1B1B);
        Button analyze = new Button(this);
        analyze.setText(gr ? "ΑΝΑΛΥΣΗ" : "ANALYZE");
        analyze.setTextColor(Color.BLACK);
        analyze.setBackgroundColor(0xFF39FF14);
        LinearLayout.LayoutParams left = new LinearLayout.LayoutParams(0, dp(56), 1f);
        left.setMargins(dp(3), dp(2), dp(6), 0);
        LinearLayout.LayoutParams right = new LinearLayout.LayoutParams(0, dp(56), 1f);
        right.setMargins(dp(3), dp(2), dp(3), 0);
        actions.addView(cancel, left);
        actions.addView(analyze, right);
        box.addView(actions);

        AlertDialog dialog = new AlertDialog.Builder(this,
                android.R.style.Theme_Material_Dialog_NoActionBar).create();
        dialog.setView(box);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(ignored -> finish());
        cancel.setOnClickListener(ignored -> { dialog.dismiss(); finish(); });
        analyze.setOnClickListener(ignored -> {
            Intent toLab25 = new Intent(this, ManualTestsActivity.class);
            toLab25.setAction(ACTION_IMPORT_LAB25);
            toLab25.setData(uri);
            toLab25.setClipData(ClipData.newRawUri("Android bugreport", uri));
            toLab25.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(toLab25);
            } catch (RuntimeException ignoredException) {
                // Revoked grants or missing source; never silently read other files.
            }
            dialog.dismiss();
            finish();
        });
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private Uri incomingSharedUri(Intent intent) {
        if (intent == null || !Intent.ACTION_SEND.equals(intent.getAction())) return null;
        Object stream = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Uri uri = stream instanceof Uri ? (Uri) stream : null;
        if (uri == null && intent.getClipData() != null &&
                intent.getClipData().getItemCount() == 1) {
            uri = intent.getClipData().getItemAt(0).getUri();
        }
        // Only a content URI with per-share read access, never file:// paths.
        return uri != null && "content".equalsIgnoreCase(uri.getScheme()) ? uri : null;
    }

    private int dp(float v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }
}
