package com.gel.cleaner;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Customer-side GEL-only Remote Assist endpoint.
 *
 * Security boundary:
 * - requires an already CONNECTED Service Session,
 * - requires explicit customer consent for each assistId,
 * - captures only a GELAutoActivityHook Window,
 * - dispatches input only to the currently resumed GELAutoActivityHook,
 * - never uses AccessibilityService and never controls another app.
 */
public final class GELRemoteAssistCustomer {

    private static final Object LOCK =
            new Object();

    private static final Handler MAIN =
            new Handler(
                    Looper.getMainLooper()
            );

    private static final String OVERLAY_TAG =
            "GEL_REMOTE_ASSIST_CUSTOMER_OVERLAY";

    private static String sessionId;
    private static String assistId;
    private static String promptedAssistId;
    private static String pendingOfferSdp;

    private static boolean consentAccepted;
    private static boolean active;
    private static boolean remoteDescriptionSet;

    private static PeerConnection peerConnection;
    private static DataChannel controlChannel;
    private static GELRemoteAssistWindowCapturer capturer;
    private static VideoSource videoSource;
    private static VideoTrack videoTrack;

    private static Map<?, ?> latestAssistSignal;

    private static final Set<String> appliedTechnicianCandidates =
            new HashSet<>();

    private static long remoteDownTime;

    private GELRemoteAssistCustomer() {}

    public static void onSessionSignal(
            Context context,
            String connectedSessionId,
            Object rawAssist
    ) {
        if (context == null ||
                connectedSessionId == null) {
            return;
        }

        if (!(rawAssist instanceof Map)) {
            return;
        }

        Map<?, ?> assist =
                (Map<?, ?>) rawAssist;

        String incomingId =
                stringValue(
                        assist.get("id")
                );
        String status =
                stringValue(
                        assist.get("status")
                );

        if (incomingId == null ||
                status == null) {
            return;
        }

        MAIN.post(
                () -> handleSignalOnMain(
                        context.getApplicationContext(),
                        connectedSessionId,
                        incomingId,
                        status,
                        assist
                )
        );
    }

    public static void onSessionEnded() {
        MAIN.post(
                () -> cleanupLocal(
                        true
                )
        );
    }

    public static boolean isActive() {
        synchronized (LOCK) {
            return active;
        }
    }

    /**
     * Called by GELRemoteAssistRuntime whenever a GEL Activity resumes.
     * It both restores the visible safety indicator and presents any offer
     * that arrived while no GEL Activity was foregrounded.
     */
    public static void updateOverlay(
            GELAutoActivityHook activity
    ) {
        if (activity == null) return;

        MAIN.post(
                () -> {
                    updateOverlayNow(
                            activity
                    );
                    maybePromptPendingOffer(
                            activity
                    );
                }
        );
    }

    private static void handleSignalOnMain(
            Context appContext,
            String connectedSessionId,
            String incomingId,
            String status,
            Map<?, ?> assist
    ) {
        synchronized (LOCK) {
            latestAssistSignal = assist;
        }

        if ("STOPPED".equals(status) ||
                "REJECTED".equals(status) ||
                "EXPIRED".equals(status)) {

            synchronized (LOCK) {
                if (incomingId.equals(
                        assistId
                )) {
                    cleanupLocal(
                            true
                    );
                }
            }
            return;
        }

        if ("OFFERED".equals(status)) {
            Object offerRaw =
                    assist.get(
                            "offerSdp"
                    );

            String offerSdp =
                    offerRaw instanceof String
                            ? (String) offerRaw
                            : null;

            if (offerSdp == null ||
                    offerSdp.trim().isEmpty()) {
                return;
            }

            boolean newOffer;

            synchronized (LOCK) {
                newOffer =
                        assistId == null ||
                                !incomingId.equals(
                                        assistId
                                );

                if (newOffer) {
                    cleanupPeerOnlyLocked();
                    sessionId =
                            connectedSessionId;
                    assistId =
                            incomingId;
                    promptedAssistId =
                            null;
                    pendingOfferSdp =
                            offerSdp;
                    consentAccepted =
                            false;
                    active =
                            false;
                    remoteDescriptionSet =
                            false;
                    appliedTechnicianCandidates.clear();
                } else {
                    pendingOfferSdp =
                            offerSdp;
                }
            }

            GELAutoActivityHook activity =
                    GELRemoteAssistRuntime
                            .getActiveActivity();

            if (activity != null) {
                maybePromptPendingOffer(
                        activity
                );
            }

            applyTechnicianCandidatesFromLatest();
            return;
        }

        if ("ANSWERED".equals(status)) {
            synchronized (LOCK) {
                if (!incomingId.equals(
                        assistId
                )) {
                    return;
                }
            }

            applyTechnicianCandidatesFromLatest();
        }
    }

    private static void maybePromptPendingOffer(
            GELAutoActivityHook activity
    ) {
        final String currentSession;
        final String currentAssist;
        final String offer;

        synchronized (LOCK) {
            if (activity == null ||
                    assistId == null ||
                    pendingOfferSdp == null ||
                    consentAccepted ||
                    assistId.equals(
                            promptedAssistId
                    )) {
                return;
            }

            promptedAssistId =
                    assistId;
            currentSession =
                    sessionId;
            currentAssist =
                    assistId;
            offer =
                    pendingOfferSdp;
        }

        boolean gr =
                AppLang.isGreek(
                        activity
                );

        AlertDialog dialog =
                new AlertDialog.Builder(
                        activity
                )
                        .setTitle(
                                gr
                                        ? "GEL Remote Assist"
                                        : "GEL Remote Assist"
                        )
                        .setMessage(
                                gr
                                        ? "Ο τεχνικός ζητά προσωρινό απομακρυσμένο έλεγχο ΜΟΝΟ μέσα στο GEL iDoctor.\n\nΘα βλέπει την εφαρμογή GEL και θα μπορεί να πατά, να κάνει κύλιση και να ανοίγει τα Labs της εφαρμογής. Δεν αποκτά έλεγχο άλλων εφαρμογών ή των ρυθμίσεων Android."
                                        : "The technician requests temporary remote control ONLY inside GEL iDoctor.\n\nThey will see the GEL app and can tap, scroll and open GEL Labs. This does not grant control of other apps or Android settings."
                        )
                        .setCancelable(
                                false
                        )
                        .setNegativeButton(
                                gr
                                        ? "Απόρριψη"
                                        : "Decline",
                                (d, which) -> rejectOffer(
                                        activity,
                                        currentSession,
                                        currentAssist
                                )
                        )
                        .setPositiveButton(
                                gr
                                        ? "Να επιτραπεί"
                                        : "Allow",
                                (d, which) -> acceptOffer(
                                        activity,
                                        currentSession,
                                        currentAssist,
                                        offer
                                )
                        )
                        .create();

        try {
            dialog.show();
        } catch (Throwable t) {
            synchronized (LOCK) {
                if (currentAssist.equals(
                        promptedAssistId
                )) {
                    promptedAssistId =
                            null;
                }
            }
        }
    }

    private static void rejectOffer(
            GELAutoActivityHook activity,
            String currentSession,
            String currentAssist
    ) {
        GELRemoteAssistSignaling.answer(
                currentSession,
                currentAssist,
                false,
                null,
                "Customer declined GEL Remote Assist.",
                (success, data, message) -> {
                    MAIN.post(
                            () -> {
                                cleanupLocal(
                                        true
                                );

                                Toast.makeText(
                                        activity,
                                        AppLang.isGreek(activity)
                                                ? "Το GEL Remote Assist απορρίφθηκε."
                                                : "GEL Remote Assist declined.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                    );
                }
        );
    }

    private static void acceptOffer(
            GELAutoActivityHook activity,
            String currentSession,
            String currentAssist,
            String offerSdp
    ) {
        synchronized (LOCK) {
            if (!currentAssist.equals(
                    assistId
            )) {
                return;
            }

            consentAccepted =
                    true;
        }

        Toast.makeText(
                activity,
                AppLang.isGreek(activity)
                        ? "Έναρξη GEL Remote Assist..."
                        : "Starting GEL Remote Assist...",
                Toast.LENGTH_SHORT
        ).show();

        try {
            startPeerAsCustomer(
                    activity,
                    currentSession,
                    currentAssist,
                    offerSdp
            );
        } catch (Throwable t) {
            failAcceptedOffer(
                    activity,
                    currentSession,
                    currentAssist,
                    messageOf(t)
            );
        }
    }

    private static void startPeerAsCustomer(
            GELAutoActivityHook activity,
            String currentSession,
            String currentAssist,
            String offerSdp
    ) {
        PeerConnectionFactory factory =
                GELRemoteAssistRuntime
                        .getFactory(
                                activity
                        );

        PeerConnection peer =
                factory.createPeerConnection(
                        GELRemoteAssistRuntime
                                .createRtcConfiguration(),
                        new CustomerPeerObserver(
                                activity.getApplicationContext(),
                                currentSession,
                                currentAssist
                        )
                );

        if (peer == null) {
            throw new IllegalStateException(
                    "Unable to create WebRTC peer connection."
            );
        }

        GELRemoteAssistWindowCapturer localCapturer =
                new GELRemoteAssistWindowCapturer();

        VideoSource localSource =
                factory.createVideoSource(
                        true
                );

        localCapturer.initialize(
                null,
                activity.getApplicationContext(),
                localSource.getCapturerObserver()
        );

        VideoTrack localTrack =
                factory.createVideoTrack(
                        "GELRA_VIDEO",
                        localSource
                );

        peer.addTrack(
                localTrack,
                Collections.singletonList(
                        "gel-remote-assist"
                )
        );

        synchronized (LOCK) {
            if (!currentAssist.equals(
                    assistId
            )) {
                peer.dispose();
                localCapturer.dispose();
                localSource.dispose();
                localTrack.dispose();
                return;
            }

            peerConnection = peer;
            capturer = localCapturer;
            videoSource = localSource;
            videoTrack = localTrack;
        }

        SessionDescription offer =
                new SessionDescription(
                        SessionDescription.Type.OFFER,
                        offerSdp
                );

        peer.setRemoteDescription(
                new SimpleSdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        synchronized (LOCK) {
                            if (!currentAssist.equals(
                                    assistId
                            )) {
                                return;
                            }

                            remoteDescriptionSet =
                                    true;
                        }

                        applyTechnicianCandidatesFromLatest();

                        peer.createAnswer(
                                new SimpleSdpObserver() {
                                    @Override
                                    public void onCreateSuccess(
                                            SessionDescription answer
                                    ) {
                                        setCustomerLocalAnswer(
                                                activity,
                                                currentSession,
                                                currentAssist,
                                                peer,
                                                localCapturer,
                                                answer
                                        );
                                    }

                                    @Override
                                    public void onCreateFailure(
                                            String error
                                    ) {
                                        failAcceptedOffer(
                                                activity,
                                                currentSession,
                                                currentAssist,
                                                error
                                        );
                                    }
                                },
                                new MediaConstraints()
                        );
                    }

                    @Override
                    public void onSetFailure(
                            String error
                    ) {
                        failAcceptedOffer(
                                activity,
                                currentSession,
                                currentAssist,
                                error
                        );
                    }
                },
                offer
        );
    }

    private static void setCustomerLocalAnswer(
            GELAutoActivityHook activity,
            String currentSession,
            String currentAssist,
            PeerConnection peer,
            GELRemoteAssistWindowCapturer localCapturer,
            SessionDescription answer
    ) {
        peer.setLocalDescription(
                new SimpleSdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        SessionDescription localAnswer =
                                peer.getLocalDescription();

                        if (localAnswer == null ||
                                localAnswer.description == null ||
                                localAnswer.description.trim().isEmpty()) {

                            failAcceptedOffer(
                                    activity,
                                    currentSession,
                                    currentAssist,
                                    "Customer local SDP answer is missing."
                            );
                            return;
                        }

                        GELRemoteAssistSignaling.answer(
                                currentSession,
                                currentAssist,
                                true,
                                localAnswer.description,
                                "Customer accepted GEL-only Remote Assist.",
                                (success, data, message) -> {
                                    MAIN.post(
                                            () -> {
                                                if (!success) {
                                                    failAcceptedOffer(
                                                            activity,
                                                            currentSession,
                                                            currentAssist,
                                                            message
                                                    );
                                                    return;
                                                }

                                                synchronized (LOCK) {
                                                    if (!currentAssist.equals(
                                                            assistId
                                                    )) {
                                                        return;
                                                    }

                                                    active =
                                                            true;
                                                    pendingOfferSdp =
                                                            null;
                                                }

                                                try {
                                                    localCapturer.startCapture(
                                                            540,
                                                            960,
                                                            8
                                                    );
                                                } catch (Throwable t) {
                                                    failAcceptedOffer(
                                                            activity,
                                                            currentSession,
                                                            currentAssist,
                                                            messageOf(t)
                                                    );
                                                    return;
                                                }

                                                GELAutoActivityHook current =
                                                        GELRemoteAssistRuntime
                                                                .getActiveActivity();

                                                if (current != null) {
                                                    updateOverlayNow(
                                                            current
                                                    );
                                                }
                                            }
                                    );
                                }
                        );
                    }

                    @Override
                    public void onSetFailure(
                            String error
                    ) {
                        failAcceptedOffer(
                                activity,
                                currentSession,
                                currentAssist,
                                error
                        );
                    }
                },
                answer
        );
    }

    private static void failAcceptedOffer(
            GELAutoActivityHook activity,
            String currentSession,
            String currentAssist,
            String reason
    ) {
        String safeReason =
                reason != null &&
                        !reason.trim().isEmpty()
                        ? reason.trim()
                        : "Remote Assist setup failed.";

        GELRemoteAssistSignaling.answer(
                currentSession,
                currentAssist,
                false,
                null,
                safeReason,
                null
        );

        MAIN.post(
                () -> {
                    cleanupLocal(
                            true
                    );

                    Toast.makeText(
                            activity,
                            "GEL Remote Assist: " +
                                    safeReason,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );
    }

    private static void applyTechnicianCandidatesFromLatest() {
        final PeerConnection peer;
        final Map<?, ?> assist;
        final boolean canApply;

        synchronized (LOCK) {
            peer = peerConnection;
            assist = latestAssistSignal;
            canApply =
                    remoteDescriptionSet;
        }

        if (peer == null ||
                assist == null ||
                !canApply) {
            return;
        }

        Object raw =
                assist.get(
                        "technicianCandidates"
                );

        if (!(raw instanceof List)) {
            return;
        }

        for (Object item :
                (List<?>) raw) {

            if (!(item instanceof Map)) {
                continue;
            }

            Map<?, ?> candidateMap =
                    (Map<?, ?>) item;

            String sdp =
                    stringValue(
                            candidateMap.get(
                                    "candidate"
                            )
                    );

            String mid =
                    stringValue(
                            candidateMap.get(
                                    "sdpMid"
                            )
                    );

            int line =
                    intValue(
                            candidateMap.get(
                                    "sdpMLineIndex"
                            ),
                            0
                    );

            if (sdp == null ||
                    sdp.trim().isEmpty()) {
                continue;
            }

            String key =
                    (mid != null ? mid : "") +
                            "|" + line +
                            "|" + sdp;

            synchronized (LOCK) {
                if (appliedTechnicianCandidates.contains(
                        key
                )) {
                    continue;
                }

                appliedTechnicianCandidates.add(
                        key
                );
            }

            try {
                peer.addIceCandidate(
                        new IceCandidate(
                                mid != null
                                        ? mid
                                        : "",
                                line,
                                sdp
                        )
                );
            } catch (Throwable ignore) {}
        }
    }

    private static void attachControlChannel(
            DataChannel channel
    ) {
        if (channel == null) return;

        synchronized (LOCK) {
            controlChannel =
                    channel;
        }

        channel.registerObserver(
                new DataChannel.Observer() {
                    @Override
                    public void onBufferedAmountChange(
                            long previousAmount
                    ) {}

                    @Override
                    public void onStateChange() {}

                    @Override
                    public void onMessage(
                            DataChannel.Buffer buffer
                    ) {
                        if (buffer == null ||
                                buffer.binary) {
                            return;
                        }

                        try {
                            ByteBuffer bytes =
                                    buffer.data.slice();

                            byte[] raw =
                                    new byte[
                                            bytes.remaining()
                                    ];

                            bytes.get(
                                    raw
                            );

                            String json =
                                    new String(
                                            raw,
                                            StandardCharsets.UTF_8
                                    );

                            handleControlMessage(
                                    json
                            );
                        } catch (Throwable ignore) {}
                    }
                }
        );
    }

    private static void handleControlMessage(
            String jsonText
    ) {
        if (jsonText == null ||
                jsonText.length() > 2048) {
            return;
        }

        try {
            JSONObject json =
                    new JSONObject(
                            jsonText
                    );

            String type =
                    json.optString(
                            "t",
                            ""
                    );

            if ("touch".equals(type)) {
                int action =
                        json.optInt(
                                "a",
                                -1
                        );

                double x =
                        json.optDouble(
                                "x",
                                Double.NaN
                        );

                double y =
                        json.optDouble(
                                "y",
                                Double.NaN
                        );

                dispatchRemoteTouch(
                        action,
                        x,
                        y
                );

            } else if ("back".equals(type)) {
                dispatchRemoteBack();
            }
        } catch (Throwable ignore) {}
    }

    private static void dispatchRemoteTouch(
            int action,
            double normalizedX,
            double normalizedY
    ) {
        if (!isActive() ||
                !Double.isFinite(normalizedX) ||
                !Double.isFinite(normalizedY) ||
                normalizedX < 0.0d ||
                normalizedX > 1.0d ||
                normalizedY < 0.0d ||
                normalizedY > 1.0d ||
                !isSupportedTouchAction(action)) {
            return;
        }

        MAIN.post(
                () -> {
                    GELAutoActivityHook activity =
                            GELRemoteAssistRuntime
                                    .getActiveActivity();

                    if (activity == null ||
                            !isActive()) {
                        return;
                    }

                    View content =
                            activity.findViewById(
                                    android.R.id.content
                            );

                    if (content == null) {
                        return;
                    }

                    int width =
                            content.getWidth();
                    int height =
                            content.getHeight();

                    if (width <= 0 ||
                            height <= 0) {
                        return;
                    }

                    int[] contentLocation =
                            new int[2];

                    content.getLocationInWindow(
                            contentLocation
                    );

                    float contentLeft =
                            contentLocation[0];

                    float contentTop =
                            contentLocation[1];

                    long now =
                            SystemClock.uptimeMillis();

                    if (action ==
                            MotionEvent.ACTION_DOWN) {
                        remoteDownTime =
                                now;
                    } else if (remoteDownTime == 0L) {
                        return;
                    }

                    MotionEvent event =
                            MotionEvent.obtain(
                                    remoteDownTime,
                                    now,
                                    action,
                                    contentLeft +
                                            (float) (
                                                    normalizedX *
                                                            width
                                            ),
                                    contentTop +
                                            (float) (
                                                    normalizedY *
                                                            height
                                            ),
                                    0
                            );

                    try {
                        activity.dispatchTouchEvent(
                                event
                        );
                    } finally {
                        event.recycle();
                    }

                    if (action ==
                            MotionEvent.ACTION_UP ||
                            action ==
                                    MotionEvent.ACTION_CANCEL) {
                        remoteDownTime =
                                0L;
                    }
                }
        );
    }

    private static void dispatchRemoteBack() {
        if (!isActive()) {
            return;
        }

        MAIN.post(
                () -> {
                    GELAutoActivityHook activity =
                            GELRemoteAssistRuntime
                                    .getActiveActivity();

                    if (activity == null ||
                            !isActive()) {
                        return;
                    }

                    activity
                            .getOnBackPressedDispatcher()
                            .onBackPressed();
                }
        );
    }

    private static boolean isSupportedTouchAction(
            int action
    ) {
        return action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_MOVE ||
                action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL;
    }

    private static void updateOverlayNow(
            GELAutoActivityHook activity
    ) {
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

        if (!isActive()) {
            if (existing != null) {
                try {
                    content.removeView(
                            existing
                    );
                } catch (Throwable ignore) {}
            }
            return;
        }

        if (existing != null) {
            return;
        }

        TextView banner =
                new TextView(
                        activity
                );

        banner.setTag(
                OVERLAY_TAG
        );
        banner.setText(
                AppLang.isGreek(activity)
                        ? "REMOTE • ΠΑΤΗΣΤΕ ΓΙΑ STOP"
                        : "REMOTE • TAP TO STOP"
        );
        banner.setTextColor(
                0xFFFFFFFF
        );
        banner.setTextSize(
                10f
        );
        banner.setGravity(
                Gravity.CENTER
        );
        banner.setBackgroundColor(
                0xEE9A1600
        );

        int pad =
                Math.max(
                        5,
                        Math.round(
                                5f *
                                        activity
                                                .getResources()
                                                .getDisplayMetrics()
                                                .density
                        )
                );

        banner.setPadding(
                pad,
                pad,
                pad,
                pad
        );

        banner.setOnClickListener(
                v -> requestStopByCustomer(
                        activity
                )
        );

        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP | Gravity.END
                );

        int margin =
                Math.max(
                        6,
                        Math.round(
                                6f *
                                        activity
                                                .getResources()
                                                .getDisplayMetrics()
                                                .density
                        )
                );

        int topInset =
                0;

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.M) {

            android.view.WindowInsets insets =
                    content.getRootWindowInsets();

            if (insets != null) {
                topInset =
                        insets.getStableInsetTop();
            }
        }

        lp.setMargins(
                margin,
                topInset + margin,
                margin,
                margin
        );

        content.addView(
                banner,
                lp
        );
    }

    private static void requestStopByCustomer(
            GELAutoActivityHook activity
    ) {
        final String currentSession;
        final String currentAssist;

        synchronized (LOCK) {
            currentSession =
                    sessionId;
            currentAssist =
                    assistId;
        }

        if (currentSession == null ||
                currentAssist == null) {
            cleanupLocal(
                    true
            );
            return;
        }

        cleanupLocal(
                true
        );

        GELRemoteAssistSignaling.stop(
                currentSession,
                currentAssist,
                "Customer stopped GEL Remote Assist.",
                null
        );

        Toast.makeText(
                activity,
                AppLang.isGreek(activity)
                        ? "Το GEL Remote Assist τερματίστηκε."
                        : "GEL Remote Assist stopped.",
                Toast.LENGTH_SHORT
        ).show();
    }

    private static void cleanupLocal(
            boolean clearIdentity
    ) {
        GELAutoActivityHook activity =
                GELRemoteAssistRuntime
                        .getActiveActivity();

        synchronized (LOCK) {
            cleanupPeerOnlyLocked();

            active =
                    false;
            consentAccepted =
                    false;
            pendingOfferSdp =
                    null;
            promptedAssistId =
                    null;
            latestAssistSignal =
                    null;
            remoteDescriptionSet =
                    false;
            appliedTechnicianCandidates.clear();
            remoteDownTime =
                    0L;

            if (clearIdentity) {
                sessionId =
                        null;
                assistId =
                        null;
            }
        }

        if (activity != null) {
            updateOverlayNow(
                    activity
            );
        }
    }

    private static void cleanupPeerOnlyLocked() {
        if (controlChannel != null) {
            try {
                controlChannel.close();
            } catch (Throwable ignore) {}
            try {
                controlChannel.dispose();
            } catch (Throwable ignore) {}
            controlChannel =
                    null;
        }

        if (capturer != null) {
            try {
                capturer.stopCapture();
            } catch (Throwable ignore) {}
            try {
                capturer.dispose();
            } catch (Throwable ignore) {}
            capturer =
                    null;
        }

        if (videoTrack != null) {
            try {
                videoTrack.dispose();
            } catch (Throwable ignore) {}
            videoTrack =
                    null;
        }

        if (videoSource != null) {
            try {
                videoSource.dispose();
            } catch (Throwable ignore) {}
            videoSource =
                    null;
        }

        if (peerConnection != null) {
            try {
                peerConnection.close();
            } catch (Throwable ignore) {}
            try {
                peerConnection.dispose();
            } catch (Throwable ignore) {}
            peerConnection =
                    null;
        }
    }

    private static final class CustomerPeerObserver
            implements PeerConnection.Observer {

        private final Context appContext;
        private final String connectedSession;
        private final String connectedAssist;

        CustomerPeerObserver(
                Context context,
                String session,
                String assist
        ) {
            appContext =
                    context.getApplicationContext();
            connectedSession =
                    session;
            connectedAssist =
                    assist;
        }

        @Override
        public void onSignalingChange(
                PeerConnection.SignalingState signalingState
        ) {}

        @Override
        public void onIceConnectionChange(
                PeerConnection.IceConnectionState iceConnectionState
        ) {
            if (iceConnectionState ==
                    PeerConnection.IceConnectionState.FAILED) {

                GELRemoteAssistSignaling.stop(
                        connectedSession,
                        connectedAssist,
                        "Customer WebRTC connection failed.",
                        null
                );

                MAIN.post(
                        () -> {
                            synchronized (LOCK) {
                                if (connectedAssist.equals(
                                        assistId
                                )) {
                                    cleanupLocal(
                                            true
                                    );
                                }
                            }
                        }
                );
            } else if (iceConnectionState ==
                    PeerConnection.IceConnectionState.CLOSED) {

                MAIN.post(
                        () -> {
                            synchronized (LOCK) {
                                if (connectedAssist.equals(
                                        assistId
                                )) {
                                    cleanupLocal(
                                            true
                                    );
                                }
                            }
                        }
                );
            }
        }

        @Override
        public void onIceConnectionReceivingChange(
                boolean receiving
        ) {}

        @Override
        public void onIceGatheringChange(
                PeerConnection.IceGatheringState iceGatheringState
        ) {}

        @Override
        public void onIceCandidate(
                IceCandidate candidate
        ) {
            GELRemoteAssistSignaling.addCandidate(
                    connectedSession,
                    connectedAssist,
                    candidate,
                    null
            );
        }

        @Override
        public void onIceCandidatesRemoved(
                IceCandidate[] iceCandidates
        ) {}

        @Override
        public void onAddStream(
                MediaStream mediaStream
        ) {}

        @Override
        public void onRemoveStream(
                MediaStream mediaStream
        ) {}

        @Override
        public void onDataChannel(
                DataChannel dataChannel
        ) {
            MAIN.post(
                    () -> attachControlChannel(
                            dataChannel
                    )
            );
        }

        @Override
        public void onRenegotiationNeeded() {}

        @Override
        public void onAddTrack(
                RtpReceiver receiver,
                MediaStream[] mediaStreams
        ) {}

        @Override
        public void onTrack(
                RtpTransceiver transceiver
        ) {}
    }

    private abstract static class SimpleSdpObserver
            implements SdpObserver {

        @Override
        public void onCreateSuccess(
                SessionDescription sessionDescription
        ) {}

        @Override
        public void onSetSuccess() {}

        @Override
        public void onCreateFailure(
                String error
        ) {}

        @Override
        public void onSetFailure(
                String error
        ) {}
    }

    private static String stringValue(
            Object value
    ) {
        if (value == null) {
            return null;
        }

        String text =
                String.valueOf(
                        value
                ).trim();

        return text.isEmpty()
                ? null
                : text;
    }

    private static int intValue(
            Object value,
            int fallback
    ) {
        if (value instanceof Number) {
            return ((Number) value)
                    .intValue();
        }

        try {
            return Integer.parseInt(
                    String.valueOf(
                            value
                    )
            );
        } catch (Throwable ignore) {
            return fallback;
        }
    }

    private static String messageOf(
            Throwable t
    ) {
        if (t == null) {
            return "Remote Assist setup failed.";
        }

        String message =
                t.getMessage();

        return message != null &&
                !message.trim().isEmpty()
                ? message.trim()
                : t.getClass()
                        .getSimpleName();
    }
}
