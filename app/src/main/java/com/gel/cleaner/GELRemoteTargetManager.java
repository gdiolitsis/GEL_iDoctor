package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Technician-side target selector.
 *
 * LOCAL mode:
 *   actions operate on this installation.
 *
 * REMOTE mode:
 *   remote-aware actions are routed to the customer device that belongs
 *   to the currently CONNECTED Firebase Service Session.
 */
public final class GELRemoteTargetManager {

    private static final String TECH_SESSION_PREFS =
            "GEL_REPAIR_SESSION";

    private static final String KEY_SESSION_ID =
            "session_id";

    private static final String KEY_FIREBASE_BACKED =
            "firebase_backed";

    private static final String KEY_SESSION_CONNECTED =
            "session_connected";

    private static final String KEY_REMOTE_MODE =
            "remote_mode_enabled";

    private static final int OVERLAY_TAG =
            0x47454C52; // "GELR"

    private GELRemoteTargetManager() {}

    public static boolean isRemoteAvailable(Context context) {

        if (context == null) return false;

        SharedPreferences p =
                context.getSharedPreferences(
                        TECH_SESSION_PREFS,
                        Context.MODE_PRIVATE
                );

        String sessionId =
                p.getString(
                        KEY_SESSION_ID,
                        null
                );

        return p.getBoolean(KEY_FIREBASE_BACKED, false)
                && p.getBoolean(KEY_SESSION_CONNECTED, false)
                && sessionId != null
                && !sessionId.trim().isEmpty();
    }

    public static boolean isRemoteMode(Context context) {

        if (!isRemoteAvailable(context)) {
            return false;
        }

        return context
                .getSharedPreferences(
                        TECH_SESSION_PREFS,
                        Context.MODE_PRIVATE
                )
                .getBoolean(
                        KEY_REMOTE_MODE,
                        false
                );
    }

    public static String getSessionId(Context context) {

        if (context == null) {
            return null;
        }

        return context
                .getSharedPreferences(
                        TECH_SESSION_PREFS,
                        Context.MODE_PRIVATE
                )
                .getString(
                        KEY_SESSION_ID,
                        null
                );
    }

    public static boolean enterRemoteMode(Context context) {

        if (!isRemoteAvailable(context)) {
            return false;
        }

        context
                .getSharedPreferences(
                        TECH_SESSION_PREFS,
                        Context.MODE_PRIVATE
                )
                .edit()
                .putBoolean(
                        KEY_REMOTE_MODE,
                        true
                )
                .apply();

        return true;
    }

    public static void exitRemoteMode(Context context) {

        if (context == null) {
            return;
        }

        context
                .getSharedPreferences(
                        TECH_SESSION_PREFS,
                        Context.MODE_PRIVATE
                )
                .edit()
                .putBoolean(
                        KEY_REMOTE_MODE,
                        false
                )
                .apply();
    }

    public static void syncAvailability(Context context) {

        if (context == null) {
            return;
        }

        if (!isRemoteAvailable(context)) {
            exitRemoteMode(context);
        }
    }

    /**
     * High-visibility warning shown on every GELAutoActivityHook Activity
     * while technician REMOTE DEVICE MODE is active.
     */
    public static void updateOverlay(Activity activity) {

        if (activity == null) {
            return;
        }

        ViewGroup content =
                activity.findViewById(
                        android.R.id.content
                );

        if (content == null) {
            return;
        }

        View existing =
                content.findViewWithTag(
                        OVERLAY_TAG
                );

        if (!isRemoteMode(activity)) {

            if (existing != null) {
                content.removeView(existing);
            }

            return;
        }

        if (existing != null) {
            return;
        }

        TextView banner =
                new TextView(activity);

        banner.setTag(
                OVERLAY_TAG
        );

        banner.setText(
                AppLang.isGreek(activity)
                        ? "REMOTE DEVICE • ΣΤΟΧΟΣ: ΠΕΛΑΤΗΣ • ΠΑΤΗΣΤΕ ΓΙΑ ΕΞΟΔΟ"
                        : "REMOTE DEVICE • CUSTOMER TARGET • TAP TO EXIT"
        );

        banner.setTextColor(
                Color.WHITE
        );

        banner.setTextSize(
                12f
        );

        banner.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        banner.setGravity(
                Gravity.CENTER
        );

        banner.setBackgroundColor(
                0xEE9A1600
        );

        banner.setOnClickListener(
                v -> {

                    exitRemoteMode(
                            activity
                    );

                    try {
                        content.removeView(
                                banner
                        );
                    } catch (Throwable ignore) {}

                    Toast.makeText(
                            activity,
                            AppLang.isGreek(activity)
                                    ? "REMOTE DEVICE MODE τερματίστηκε. Οι ενέργειες είναι ξανά LOCAL."
                                    : "REMOTE DEVICE MODE ended. Actions are LOCAL again.",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        int px =
                Math.max(
                        8,
                        (int) (
                                8f *
                                activity
                                        .getResources()
                                        .getDisplayMetrics()
                                        .density
                        )
                );

        banner.setPadding(
                px,
                px,
                px,
                px
        );

        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.BOTTOM
                );

        content.addView(
                banner,
                lp
        );
    }
}
