package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * One-device, one-session, server-issued temporary PRO grant.
 * Only normal professional diagnostics are unlocked; no remote control.
 * The customer's separately purchased/earned PRO remains independent.
 */
public final class GELCustomerServiceAccess {
    private static final String PREFS = "GEL_CUSTOMER_SERVICE_SESSION";
    private static final String KEY_CONNECTED = "connected";
    private static final String KEY_SESSION_ID = "session_id";
    private static final String KEY_EXPIRES = "service_ends_at";
    private static final String KEY_UID = "service_customer_uid";
    private static final String KEY_START = "service_start_elapsed";
    private static final String KEY_DEADLINE = "service_deadline_elapsed";

    private GELCustomerServiceAccess() {}

    public static boolean grant(Context context, String sessionId,
                                long serviceEndsAtMs, long serverNowMs) {
        if (context == null || sessionId == null || sessionId.trim().isEmpty() ||
                !GELServiceSessionWindow.validGrant(serverNowMs, serviceEndsAtMs)) {
            return false;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return false;

        long start = SystemClock.elapsedRealtime();
        long remaining = serviceEndsAtMs - serverNowMs;
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString(KEY_SESSION_ID, sessionId)
                .putString(KEY_UID, user.getUid())
                .putLong(KEY_EXPIRES, serviceEndsAtMs)
                .putLong(KEY_START, start)
                .putLong(KEY_DEADLINE, start + remaining)
                .putBoolean(KEY_CONNECTED, true)
                .apply();
        return true;
    }

    public static long remainingMs(Context context) {
        if (context == null) return 0L;
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (!p.getBoolean(KEY_CONNECTED, false)) return 0L;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.getUid().equals(p.getString(KEY_UID, ""))) {
            return 0L;
        }
        return GELServiceSessionWindow.remaining(
                p.getLong(KEY_START, 0L),
                p.getLong(KEY_DEADLINE, 0L),
                SystemClock.elapsedRealtime(),
                p.getLong(KEY_EXPIRES, 0L),
                System.currentTimeMillis());
    }

    public static boolean isActive(Context context) {
        return remainingMs(context) > 0L;
    }

    public static void clear(Context context) {
        if (context != null) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit().clear().apply();
        }
    }
}
