package com.gel.cleaner;

import androidx.annotation.Nullable;

import com.google.firebase.functions.FirebaseFunctions;

import org.webrtc.IceCandidate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Thin Firebase callable wrapper used only for Remote Assist signaling.
 * Live video and control events never pass through Firebase.
 */
public final class GELRemoteAssistSignaling {

    private static final String FUNCTIONS_REGION =
            "europe-west1";

    public interface Callback {
        void onComplete(
                boolean success,
                Map<String, Object> data,
                @Nullable String message
        );
    }

    private GELRemoteAssistSignaling() {}

    public static void start(
            String sessionId,
            String offerSdp,
            Callback callback
    ) {
        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "sessionId",
                sessionId
        );
        data.put(
                "offerSdp",
                offerSdp
        );

        call(
                "startRemoteAssist",
                data,
                callback
        );
    }

    public static void answer(
            String sessionId,
            String assistId,
            boolean accepted,
            @Nullable String answerSdp,
            @Nullable String message,
            Callback callback
    ) {
        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "sessionId",
                sessionId
        );
        data.put(
                "assistId",
                assistId
        );
        data.put(
                "accepted",
                accepted
        );

        if (answerSdp != null) {
            data.put(
                    "answerSdp",
                    answerSdp
            );
        }

        if (message != null) {
            data.put(
                    "message",
                    message
            );
        }

        call(
                "answerRemoteAssist",
                data,
                callback
        );
    }

    public static void addCandidate(
            String sessionId,
            String assistId,
            IceCandidate candidate,
            Callback callback
    ) {
        if (candidate == null) {
            if (callback != null) {
                callback.onComplete(
                        false,
                        Collections.emptyMap(),
                        "ICE candidate is missing."
                );
            }
            return;
        }

        Map<String, Object> candidateMap =
                new HashMap<>();

        candidateMap.put(
                "sdpMid",
                candidate.sdpMid != null
                        ? candidate.sdpMid
                        : ""
        );

        candidateMap.put(
                "sdpMLineIndex",
                candidate.sdpMLineIndex
        );

        candidateMap.put(
                "candidate",
                candidate.sdp != null
                        ? candidate.sdp
                        : ""
        );

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "sessionId",
                sessionId
        );
        data.put(
                "assistId",
                assistId
        );
        data.put(
                "candidate",
                candidateMap
        );

        call(
                "addRemoteAssistIceCandidate",
                data,
                callback
        );
    }

    public static void stop(
            String sessionId,
            String assistId,
            @Nullable String message,
            Callback callback
    ) {
        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "sessionId",
                sessionId
        );
        data.put(
                "assistId",
                assistId
        );

        if (message != null) {
            data.put(
                    "message",
                    message
            );
        }

        call(
                "stopRemoteAssist",
                data,
                callback
        );
    }

    private static void call(
            String function,
            Map<String, Object> data,
            Callback callback
    ) {
        FirebaseFunctions
                .getInstance(
                        FUNCTIONS_REGION
                )
                .getHttpsCallable(
                        function
                )
                .call(
                        data
                )
                .addOnCompleteListener(
                        task -> {
                            if (!task.isSuccessful() ||
                                    task.getResult() == null) {

                                String message =
                                        task.getException() != null
                                                ? task.getException().getMessage()
                                                : "Remote Assist signaling failed.";

                                if (callback != null) {
                                    callback.onComplete(
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

                            Map<String, Object> result =
                                    new HashMap<>();

                            if (raw instanceof Map) {
                                for (Map.Entry<?, ?> entry :
                                        ((Map<?, ?>) raw)
                                                .entrySet()) {

                                    if (entry.getKey()
                                            instanceof String) {

                                        result.put(
                                                (String) entry.getKey(),
                                                entry.getValue()
                                        );
                                    }
                                }
                            }

                            if (callback != null) {
                                callback.onComplete(
                                        true,
                                        result,
                                        null
                                );
                            }
                        }
                );
    }
}
