package com.gel.cleaner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

/** Guard both Service Session roles with an actual paid Play PRO subscription. */
public final class GELServiceSubscriptionGate {
    private GELServiceSubscriptionGate() {}

    public static boolean requirePaidSubscription(Activity activity) {
        if (activity == null || activity.isFinishing()) return false;
        if (GelProEntitlement.isPaidProActive(activity)) return true;
        showRequired(activity);
        return false;
    }

    public static void showRequired(Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        final boolean gr = AppLang.isGreek(activity);
        AlertDialog dialog = new AlertDialog.Builder(activity,
                androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle(gr ? "ΑΠΑΙΤΕΙΤΑΙ ΣΥΝΔΡΟΜΗ GEL PRO" : "GEL PRO SUBSCRIPTION REQUIRED")
                .setMessage(gr
                        ? "Η σύνδεση τεχνικού–πελάτη είναι διαθέσιμη μόνο με ενεργή συνδρομή GEL PRO. "
                          + "Η πρόσβαση Tester, Gift ή η δοκιμαστική πρόσβαση δεν αρκούν για αυτή τη λειτουργία. "
                          + "Μπορείτε να ενεργοποιήσετε ή να επαναφέρετε τη συνδρομή σας."
                        : "Technician–customer connection requires an active paid GEL PRO subscription. "
                          + "Tester, Gift and Trial access do not enable this service. "
                          + "You can subscribe or restore your existing Play purchase.")
                .setNegativeButton(gr ? "Όχι τώρα" : "Not now", null)
                .setPositiveButton(gr ? "Συνδρομή GEL PRO" : "GEL PRO subscription",
                        (d, w) -> activity.startActivity(
                                new Intent(activity, GelProAccountActivity.class)))
                .create();
        dialog.setOnShowListener(d -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xFF101010));
            }
            TextView message = dialog.findViewById(android.R.id.message);
            if (message != null) message.setTextColor(Color.WHITE);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFFFFD700);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFF39FF14);
        });
        dialog.show();
    }
}
