// GDiolitsis Engine Lab (GEL) — Author & Developer
// Central GEL PRO entitlement authority.
//
// Paid Google Play subscription remains owned by GelBillingManager:
//     GEL_PRO_ENTITLEMENT / active
//
// Tester entitlement is independent:
//     GEL_TESTER_ENTITLEMENT
//
// Effective PRO:
//     paid PRO || valid tester PRO
//
// Tester entitlement is granted only by Firebase callable Functions.

package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class GelProEntitlement {

    private static final String FUNCTIONS_REGION =
            "europe-west1";

    // ============================================================
    // PAID GOOGLE PLAY PRO
    // ============================================================
    private static final String PAID_PRO_PREFS =
            "GEL_PRO_ENTITLEMENT";

    private static final String PAID_PRO_ACTIVE_KEY =
            "active";

    // ============================================================
    // TESTER PRO CACHE
    // ============================================================
    private static final String TESTER_PREFS =
            "GEL_TESTER_ENTITLEMENT";

    private static final String KEY_ENABLED =
            "enabled";

    private static final String KEY_PERMANENT =
            "permanent";

    private static final String KEY_EXPIRES_AT_MS =
            "expires_at_ms";

    private static final String KEY_TYPE =
            "type";

    private static final String KEY_BOUND_UID =
            "firebase_uid";

    private GelProEntitlement() {
    }

    public interface Callback {
        void onResult(
                boolean active,
                @Nullable String message
        );
    }

    // ============================================================
    // FINAL PRO AUTHORITY
    // ============================================================

    public static boolean isActive(
            Context context
    ) {

        if (context == null) {
            return false;
        }

        return isPaidProActive(context)
                || isTesterProActive(context);
    }

    public static boolean isPaidProActive(
            Context context
    ) {

        try {
            return context
                    .getSharedPreferences(
                            PAID_PRO_PREFS,
                            Context.MODE_PRIVATE
                    )
                    .getBoolean(
                            PAID_PRO_ACTIVE_KEY,
                            false
                    );

        } catch (Throwable ignore) {
            return false;
        }
    }

    public static boolean isTesterProActive(
            Context context
    ) {

        if (context == null) {
            return false;
        }

        try {

            SharedPreferences p =
                    context.getSharedPreferences(
                            TESTER_PREFS,
                            Context.MODE_PRIVATE
                    );

            if (!p.getBoolean(KEY_ENABLED, false)) {
                return false;
            }

            String boundUid =
                    p.getString(
                            KEY_BOUND_UID,
                            ""
                    );

            if (boundUid == null
                    || boundUid.trim().isEmpty()) {

                // Legacy/unverified cache must never grant Tester PRO.
                return false;
            }

            FirebaseUser currentUser =
                    FirebaseAuth
                            .getInstance()
                            .getCurrentUser();

            // If Firebase has restored an identity, it must match.
            // If currentUser is temporarily unavailable/offline,
            // the previously server-verified cache may remain valid
            // until its own expiry.
            if (currentUser != null
                    && !boundUid.equals(currentUser.getUid())) {

                clearTesterCache(context);
                return false;
            }

            boolean permanent =
                    p.getBoolean(
                            KEY_PERMANENT,
                            false
                    );

            if (permanent) {
                return true;
            }

            long expiresAtMs =
                    p.getLong(
                            KEY_EXPIRES_AT_MS,
                            0L
                    );

            if (expiresAtMs <= 0L
                    || System.currentTimeMillis() >= expiresAtMs) {

                clearTesterCache(context);
                return false;
            }

            return true;

        } catch (Throwable ignore) {
            return false;
        }
    }

    public static boolean isTesterPermanent(
            Context context
    ) {

        if (!isTesterProActive(context)) {
            return false;
        }

        try {

            return context
                    .getSharedPreferences(
                            TESTER_PREFS,
                            Context.MODE_PRIVATE
                    )
                    .getBoolean(
                            KEY_PERMANENT,
                            false
                    );

        } catch (Throwable ignore) {
            return false;
        }
    }

    public static long getTesterExpiresAtMs(
            Context context
    ) {

        try {
            return context
                    .getSharedPreferences(
                            TESTER_PREFS,
                            Context.MODE_PRIVATE
                    )
                    .getLong(
                            KEY_EXPIRES_AT_MS,
                            0L
                    );

        } catch (Throwable ignore) {
            return 0L;
        }
    }

    public static boolean hasTesterEntitlementCache(
            Context context
    ) {

        if (context == null) {
            return false;
        }

        try {

            return context
                    .getSharedPreferences(
                            TESTER_PREFS,
                            Context.MODE_PRIVATE
                    )
                    .getBoolean(
                            KEY_ENABLED,
                            false
                    );

        } catch (Throwable ignore) {
            return false;
        }
    }

    /**
     * Startup synchronization for an already-known tester only.
     *
     * This method never creates Firebase identity for ordinary users.
     */
    public static void refreshKnownTesterFromServer(
            Context context
    ) {

        if (!hasTesterEntitlementCache(context)) {
            return;
        }

        refreshFromServer(
                context,
                null
        );
    }

    // ============================================================
    // SERVER REFRESH
    // ============================================================

    public static void refreshFromServer(
            Context context,
            @Nullable Callback callback
    ) {

        if (context == null) {

            finish(
                    callback,
                    false,
                    "Context unavailable."
            );

            return;
        }

        Context appContext =
                context.getApplicationContext();

        FirebaseUser currentUser =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser();

        if (currentUser == null) {

            // Refresh never creates a Firebase identity.
            // Explicit tester activation is the only creation path.
            finish(
                    callback,
                    isActive(appContext),
                    "Tester identity is unavailable. Activate with your tester code."
            );

            return;
        }

        FirebaseFunctions
                .getInstance(
                        FUNCTIONS_REGION
                )
                .getHttpsCallable(
                        "getTesterEntitlement"
                )
                .call()
                .addOnCompleteListener(
                        task -> {

                            if (!task.isSuccessful()
                                    || task.getResult() == null) {

                                // A backend/network failure must not revoke
                                // a still-valid local verified entitlement.
                                finish(
                                        callback,
                                        isActive(appContext),
                                        buildErrorMessage(
                                                task.getException(),
                                                "Tester entitlement sync failed."
                                        )
                                );

                                return;
                            }

                            Object raw =
                                    task
                                            .getResult()
                                            .getData();

                            if (!(raw instanceof Map)) {

                                finish(
                                        callback,
                                        isActive(appContext),
                                        "Invalid tester entitlement response."
                                );

                                return;
                            }

                            applyServerState(
                                    appContext,
                                    (Map<?, ?>) raw
                            );

                            finish(
                                    callback,
                                    isActive(appContext),
                                    null
                            );
                        }
                );
    }

    // ============================================================
    // ACTIVATION CODE
    // ============================================================

    public static void activateTesterCode(
            Context context,
            String activationCode,
            @Nullable Callback callback
    ) {

        if (context == null) {
            finish(
                    callback,
                    false,
                    "Context unavailable."
            );
            return;
        }

        String code =
                activationCode == null
                        ? ""
                        : activationCode
                                .trim()
                                .toUpperCase(
                                        Locale.ROOT
                                );

        if (!code.matches(
                "^[A-Z0-9-]{8,64}$"
        )) {

            finish(
                    callback,
                    isActive(context),
                    "Invalid activation code."
            );
            return;
        }

        Context appContext =
                context.getApplicationContext();

        ensureFirebaseIdentity(
                appContext,
                () -> {

                    Map<String, Object> request =
                            new HashMap<>();

                    request.put(
                            "code",
                            code
                    );

                    FirebaseFunctions
                            .getInstance(
                                    FUNCTIONS_REGION
                            )
                            .getHttpsCallable(
                                    "activateTesterPro"
                            )
                            .call(
                                    request
                            )
                            .addOnCompleteListener(
                                    task -> {

                                        if (!task.isSuccessful()
                                                || task.getResult() == null) {

                                            finish(
                                                    callback,
                                                    isActive(appContext),
                                                    buildErrorMessage(
                                                            task.getException(),
                                                            "Tester activation failed."
                                                    )
                                            );
                                            return;
                                        }

                                        Object raw =
                                                task
                                                        .getResult()
                                                        .getData();

                                        if (!(raw instanceof Map)) {

                                            finish(
                                                    callback,
                                                    isActive(appContext),
                                                    "Invalid activation response."
                                            );
                                            return;
                                        }

                                        Map<?, ?> data =
                                                (Map<?, ?>) raw;

                                        applyServerState(
                                                appContext,
                                                data
                                        );

                                        boolean active =
                                                isActive(appContext);

                                        finish(
                                                callback,
                                                active,
                                                active
                                                        ? "GEL PRO tester access activated."
                                                        : "Tester entitlement is not active."
                                        );
                                    }
                            );
                },
                callback
        );
    }

    // ============================================================
    // TESTER ACTIVATION UI
    // ============================================================

    public static void showActivationDialog(
            Activity activity
    ) {

        if (activity == null
                || activity.isFinishing()) {
            return;
        }

        final boolean gr =
                AppLang.isGreek(
                        activity
                );

        EditText input =
                new EditText(
                        activity
                );

        input.setSingleLine(true);

        input.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        );

        input.setHint(
                gr
                        ? "Κωδικός Tester"
                        : "Tester activation code"
        );


        // Explicit GEL dark-dialog colors.
        // Do not depend on OEM theme inference.
        input.setTextColor(Color.WHITE);

        input.setHintTextColor(
                Color.rgb(185, 185, 185)
        );

        input.setBackgroundTintList(
                ColorStateList.valueOf(
                        Color.rgb(255, 215, 0)
                )
        );

String status =
                buildStatusText(
                        activity,
                        gr
                );

        new AlertDialog.Builder(
                activity,
                androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert
        )
                .setTitle(
                        gr
                                ? "GEL iDoctor — Tester PRO"
                                : "GEL iDoctor — Tester PRO"
                )
                .setMessage(
                        status
                )
                .setView(
                        input
                )
                .setPositiveButton(
                        gr
                                ? "Ενεργοποίηση"
                                : "Activate",
                        (dialog, which) -> {

                            String code =
                                    input
                                            .getText()
                                            .toString();

                            activateTesterCode(
                                    activity,
                                    code,
                                    (active, message) ->
                                            Toast.makeText(
                                                    activity,
                                                    localizeMessage(
                                                            gr,
                                                            active,
                                                            message
                                                    ),
                                                    Toast.LENGTH_LONG
                                            ).show()
                            );
                        }
                )
                .setNeutralButton(
                        gr
                                ? "Έλεγχος"
                                : "Refresh",
                        (dialog, which) ->
                                refreshFromServer(
                                        activity,
                                        (active, message) ->
                                                Toast.makeText(
                                                        activity,
                                                        buildStatusText(
                                                                activity,
                                                                gr
                                                        ),
                                                        Toast.LENGTH_LONG
                                                ).show()
                                )
                )
                .setNegativeButton(
                        gr
                                ? "Κλείσιμο"
                                : "Close",
                        null
                )
                .show();
    }

    // ============================================================
    // FIREBASE AUTH
    // ============================================================

    private static void ensureFirebaseIdentity(
            Context context,
            Runnable onReady,
            @Nullable Callback callback
    ) {

        FirebaseAuth auth =
                FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            onReady.run();
            return;
        }

        auth
                .signInAnonymously()
                .addOnCompleteListener(
                        task -> {

                            if (!task.isSuccessful()
                                    || auth.getCurrentUser() == null) {

                                finish(
                                        callback,
                                        isActive(context),
                                        buildErrorMessage(
                                                task.getException(),
                                                "Firebase authentication failed."
                                        )
                                );
                                return;
                            }

                            onReady.run();
                        }
                );
    }

    // ============================================================
    // SERVER RESPONSE → LOCAL CACHE
    // ============================================================

    private static void applyServerState(
            Context context,
            Map<?, ?> data
    ) {

        boolean active =
                asBoolean(
                        data.get("active"),
                        false
                );

        if (!active) {
            clearTesterCache(context);
            return;
        }

        FirebaseUser currentUser =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser();

        if (currentUser == null
                || currentUser.getUid() == null
                || currentUser.getUid().trim().isEmpty()) {

            clearTesterCache(context);
            return;
        }

        String boundUid =
                currentUser.getUid();

        boolean permanent =
                asBoolean(
                        data.get("permanent"),
                        false
                );

        long expiresAtMs =
                asLong(
                        data.get("expiresAtMs"),
                        0L
                );

        String type =
                data.get("type") instanceof String
                        ? (String) data.get("type")
                        : "TESTER_PRO";

        if (!permanent
                && expiresAtMs <= System.currentTimeMillis()) {

            clearTesterCache(context);
            return;
        }

        context
                .getSharedPreferences(
                        TESTER_PREFS,
                        Context.MODE_PRIVATE
                )
                .edit()
                .putBoolean(
                        KEY_ENABLED,
                        true
                )
                .putBoolean(
                        KEY_PERMANENT,
                        permanent
                )
                .putLong(
                        KEY_EXPIRES_AT_MS,
                        expiresAtMs
                )
                .putString(
                        KEY_TYPE,
                        type
                )
                .putString(
                        KEY_BOUND_UID,
                        boundUid
                )
                .apply();
    }

    private static void clearTesterCache(
            Context context
    ) {

        try {
            context
                    .getSharedPreferences(
                            TESTER_PREFS,
                            Context.MODE_PRIVATE
                    )
                    .edit()
                    .clear()
                    .apply();

        } catch (Throwable ignore) {
        }
    }

    // ============================================================
    // STATUS
    // ============================================================

    private static String buildStatusText(
            Context context,
            boolean gr
    ) {

        if (isPaidProActive(context)) {

            return gr
                    ? "Κατάσταση: GEL PRO ενεργό μέσω Google Play."
                    : "Status: GEL PRO active through Google Play.";
        }

        if (isTesterProActive(context)) {

            if (isTesterPermanent(context)) {

                return gr
                        ? "Κατάσταση: Μόνιμο Tester PRO ενεργό."
                        : "Status: Permanent Tester PRO active.";
            }

            long expiresAt =
                    getTesterExpiresAtMs(
                            context
                    );

            long remaining =
                    Math.max(
                            0L,
                            expiresAt
                                    - System.currentTimeMillis()
                    );

            long days =
                    (long) Math.ceil(
                            remaining
                                    / 86400000.0
                    );

            return gr
                    ? "Κατάσταση: Tester PRO ενεργό. Απομένουν περίπου "
                    + days
                    + " ημέρες."
                    : "Status: Tester PRO active. Approximately "
                    + days
                    + " days remaining.";
        }

        return gr
                ? "Κατάσταση: Δεν υπάρχει ενεργό GEL PRO.\n\n"
                + "Εισάγετε τον κωδικό που σας δόθηκε για το Closed Test."
                : "Status: GEL PRO is not active.\n\n"
                + "Enter the activation code provided for the Closed Test.";
    }

    private static String localizeMessage(
            boolean gr,
            boolean active,
            @Nullable String message
    ) {

        if (active) {

            return gr
                    ? "Το Tester PRO ενεργοποιήθηκε."
                    : "Tester PRO activated.";
        }

        if (message == null
                || message.trim().isEmpty()) {

            return gr
                    ? "Η ενεργοποίηση απέτυχε."
                    : "Activation failed.";
        }

        return message;
    }

    // ============================================================
    // VALUE HELPERS
    // ============================================================

    private static boolean asBoolean(
            Object value,
            boolean fallback
    ) {

        return value instanceof Boolean
                ? (Boolean) value
                : fallback;
    }

    private static long asLong(
            Object value,
            long fallback
    ) {

        if (value instanceof Number) {

            return ((Number) value)
                    .longValue();
        }

        return fallback;
    }

    private static String buildErrorMessage(
            @Nullable Throwable throwable,
            String fallback
    ) {

        if (throwable == null) {
            return fallback;
        }

        String message =
                throwable.getMessage();

        if (message == null
                || message.trim().isEmpty()) {

            return fallback;
        }

        return message;
    }

    private static void finish(
            @Nullable Callback callback,
            boolean active,
            @Nullable String message
    ) {

        if (callback != null) {

            callback.onResult(
                    active,
                    message
            );
        }
    }
}
