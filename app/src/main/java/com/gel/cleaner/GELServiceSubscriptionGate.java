package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

/** Guard technician service with an eligible PRO entitlement. */
public final class GELServiceSubscriptionGate {
    private GELServiceSubscriptionGate() {}

    static boolean hasTechnicianAccess(Context context) {
        return GelProEntitlement.isPaidProActive(context)
                || GelAccountPro.active(context)
                || GelProEntitlement.isTesterProActive(context);
    }

    /**
     * Paid PRO, verified Google Gift FULL ACCESS and active Tester PRO
     * qualify for technician service. The customer and free-trial grants
     * are limited to their existing features.
     *
     * If a Google gift's local short cache expired, refresh online before
     * rejecting it. The server independently checks the gift on creation.
     */
    public static void requireTechnicianAccess(Activity activity, Runnable allowed) {
        if (activity == null || activity.isFinishing() || allowed == null) return;
        if (hasTechnicianAccess(activity)) {
            allowed.run();
            return;
        }
        com.google.firebase.auth.FirebaseUser user =
                com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.isAnonymous() && user.isEmailVerified()) {
            GelAccountPro.refresh(activity, (active, message) ->
                    activity.runOnUiThread(() -> {
                        if (activity.isFinishing() || activity.isDestroyed()) return;
                        if (hasTechnicianAccess(activity)) {
                            allowed.run();
                        } else {
                            showRequired(activity);
                        }
                    }));
            return;
        }
        showRequired(activity);
    }

    public static void showRequired(Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        final boolean gr = AppLang.isGreek(activity);
        AlertDialog dialog = new AlertDialog.Builder(activity,
                androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle(gr ? "ΑΠΑΙΤΕΙΤΑΙ ΕΝΕΡΓΗ ΠΡΟΣΒΑΣΗ PRO" : "ACTIVE PRO ACCESS REQUIRED")
                .setMessage(gr
                        ? "Για τη συνεδρία τεχνικού απαιτείται ενεργή συνδρομή GEL PRO "
                          + "ή εγκεκριμένο Google Gift FULL ACCESS ή ενεργό Tester PRO. "
                          + "Ο πελάτης συνδέεται δωρεάν με QR ή κωδικό."
                        : "Technician Service Sessions require active GEL PRO, approved Google Gift FULL ACCESS, "
                          + "or active Tester PRO. The customer joins for free with QR or code.")
                .setNegativeButton(gr ? "Όχι τώρα" : "Not now", null)
                .setPositiveButton(gr ? "Έλεγχος FULL ACCESS / PRO" : "Check FULL ACCESS / PRO",
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
