package com.gel.cleaner;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Technician-side remote command sender.
 *
 * Commands are always validated by Cloud Functions.
 * The technician never writes Firestore directly.
 */
public final class GELRemoteCommandClient {

    private static final String FUNCTIONS_REGION =
            "europe-west1";

    private static final String SESSIONS_COLLECTION =
            "service_sessions";

    private static final long COMMAND_WATCH_TIMEOUT_MS =
            120_000L;

    public interface Callback {

        default void onQueued(String commandId) {}

        default void onStatus(
                String status,
                Map<String, Object> remoteCommand
        ) {}

        default void onCompleted(
                boolean success,
                Map<String, Object> result,
                @Nullable String message
        ) {}
    }

    private GELRemoteCommandClient() {}

    public static void send(
            Activity activity,
            String action,
            @Nullable Map<String, Object> payload,
            Callback callback
    ) {

        if (activity == null ||
                action == null ||
                action.trim().isEmpty()) {

            if (callback != null) {
                callback.onCompleted(
                        false,
                        Collections.emptyMap(),
                        "Invalid remote command."
                );
            }

            return;
        }

        if (!GELRemoteTargetManager.isRemoteMode(activity)) {

            if (callback != null) {
                callback.onCompleted(
                        false,
                        Collections.emptyMap(),
                        "Remote Device Mode is not active."
                );
            }

            return;
        }

        String sessionId =
                GELRemoteTargetManager
                        .getSessionId(
                                activity
                        );

        if (sessionId == null ||
                sessionId.trim().isEmpty()) {

            if (callback != null) {
                callback.onCompleted(
                        false,
                        Collections.emptyMap(),
                        "No connected customer Service Session."
                );
            }

            return;
        }

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "sessionId",
                sessionId
        );

        data.put(
                "action",
                action.trim()
        );

        data.put(
                "payload",
                payload != null
                        ? payload
                        : Collections.emptyMap()
        );

        FirebaseFunctions
                .getInstance(
                        FUNCTIONS_REGION
                )
                .getHttpsCallable(
                        "sendRemoteCommand"
                )
                .call(
                        data
                )
                .addOnCompleteListener(
                        activity,
                        task -> {

                            if (!task.isSuccessful() ||
                                    task.getResult() == null) {

                                String message =
                                        task.getException() != null
                                                ? task.getException().getMessage()
                                                : "Remote command failed.";

                                if (callback != null) {
                                    callback.onCompleted(
                                            false,
                                            Collections.emptyMap(),
                                            message
                                    );
                                }

                                return;
                            }

                            Object raw =
                                    task
                                            .getResult()
                                            .getData();

                            if (!(raw instanceof Map)) {

                                if (callback != null) {
                                    callback.onCompleted(
                                            false,
                                            Collections.emptyMap(),
                                            "Invalid server response."
                                    );
                                }

                                return;
                            }

                            Map<?, ?> server =
                                    (Map<?, ?>) raw;

                            Object idRaw =
                                    server.get(
                                            "commandId"
                                    );

                            if (!(idRaw instanceof String)) {

                                if (callback != null) {
                                    callback.onCompleted(
                                            false,
                                            Collections.emptyMap(),
                                            "Remote command ID missing."
                                    );
                                }

                                return;
                            }

                            String commandId =
                                    (String) idRaw;

                            if (callback != null) {
                                callback.onQueued(
                                        commandId
                                );
                            }

                            watchCommand(
                                    activity,
                                    sessionId,
                                    commandId,
                                    callback
                            );
                        }
                );
    }

    private static void watchCommand(
            Activity activity,
            String sessionId,
            String commandId,
            Callback callback
    ) {

        FirebaseFirestore firestore =
                FirebaseFirestore
                        .getInstance();

        final ListenerRegistration[] registration =
                new ListenerRegistration[1];

        final boolean[] finished =
                { false };

        Handler handler =
                new Handler(
                        Looper.getMainLooper()
                );

        Runnable timeout =
                () -> {

                    if (finished[0]) {
                        return;
                    }

                    finished[0] = true;

                    if (registration[0] != null) {
                        registration[0].remove();
                    }

                    if (callback != null) {
                        callback.onCompleted(
                                false,
                                Collections.emptyMap(),
                                "Timed out waiting for customer device."
                        );
                    }
                };

        registration[0] =
                firestore
                        .collection(
                                SESSIONS_COLLECTION
                        )
                        .document(
                                sessionId
                        )
                        .addSnapshotListener(
                                activity,
                                (snapshot, error) -> {

                                    if (finished[0]) {
                                        return;
                                    }

                                    if (error != null) {

                                        finished[0] = true;
                                        handler.removeCallbacks(timeout);

                                        if (registration[0] != null) {
                                            registration[0].remove();
                                        }

                                        if (callback != null) {
                                            callback.onCompleted(
                                                    false,
                                                    Collections.emptyMap(),
                                                    error.getMessage()
                                            );
                                        }

                                        return;
                                    }

                                    if (snapshot == null ||
                                            !snapshot.exists()) {
                                        return;
                                    }

                                    Object raw =
                                            snapshot.get(
                                                    "remoteCommand"
                                            );

                                    if (!(raw instanceof Map)) {
                                        return;
                                    }

                                    Map<?, ?> remote =
                                            (Map<?, ?>) raw;

                                    Object remoteId =
                                            remote.get(
                                                    "id"
                                            );

                                    if (!(remoteId instanceof String) ||
                                            !commandId.equals(remoteId)) {
                                        return;
                                    }

                                    String status =
                                            String.valueOf(
                                                    remote.get(
                                                            "status"
                                                    )
                                            );

                                    Map<String, Object> copy =
                                            copyStringObjectMap(
                                                    remote
                                            );

                                    if (callback != null) {
                                        callback.onStatus(
                                                status,
                                                copy
                                        );
                                    }

                                    if (!"SUCCESS".equals(status) &&
                                            !"FAILED".equals(status)) {
                                        return;
                                    }

                                    finished[0] = true;
                                    handler.removeCallbacks(timeout);

                                    if (registration[0] != null) {
                                        registration[0].remove();
                                    }

                                    Map<String, Object> result =
                                            Collections.emptyMap();

                                    Object resultRaw =
                                            remote.get(
                                                    "result"
                                            );

                                    if (resultRaw instanceof Map) {
                                        result =
                                                copyStringObjectMap(
                                                        (Map<?, ?>) resultRaw
                                                );
                                    }

                                    String message =
                                            remote.get("message") != null
                                                    ? String.valueOf(
                                                            remote.get(
                                                                    "message"
                                                            )
                                                    )
                                                    : null;

                                    if (callback != null) {
                                        callback.onCompleted(
                                                "SUCCESS".equals(status),
                                                result,
                                                message
                                        );
                                    }
                                }
                        );

        handler.postDelayed(
                timeout,
                COMMAND_WATCH_TIMEOUT_MS
        );
    }

    private static Map<String, Object> copyStringObjectMap(
            Map<?, ?> source
    ) {

        Map<String, Object> out =
                new HashMap<>();

        if (source == null) {
            return out;
        }

        for (Map.Entry<?, ?> e :
                source.entrySet()) {

            if (e.getKey() instanceof String) {
                out.put(
                        (String) e.getKey(),
                        e.getValue()
                );
            }
        }

        return out;
    }
}
