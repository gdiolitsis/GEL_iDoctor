package com.gel.cleaner;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Technician-side full remote control surface for GEL iDoctor only.
 *
 * The rendered frames come directly from the customer's active GEL Window.
 * Touch coordinates are normalized and sent over a WebRTC DataChannel; the
 * customer accepts them only while a GELAutoActivityHook is foregrounded.
 */
public final class GELRemoteAssistActivity extends AppCompatActivity {

    private static final String SESSIONS_COLLECTION =
            "service_sessions";

    private static final long MOVE_SEND_INTERVAL_MS =
            33L;

    private final Handler main =
            new Handler(
                    Looper.getMainLooper()
            );

    private boolean gr;
    private String sessionId;
    private String assistId;

    private TextView statusText;
    private FrameLayout videoContainer;
    private SurfaceViewRenderer renderer;
    private View touchLayer;
    private Button backButton;
    private Button stopButton;

    private int remoteVideoWidth;
    private int remoteVideoHeight;
    private boolean touchSequenceActive;
    private long lastMoveSentAt;

    private PeerConnection peerConnection;
    private DataChannel controlChannel;
    private VideoTrack remoteVideoTrack;

    private boolean remoteDescriptionSet;
    private boolean stopping;

    private ListenerRegistration sessionListener;

    private final List<IceCandidate> pendingLocalCandidates =
            new ArrayList<>();

    private final Set<String> appliedCustomerCandidates =
            new HashSet<>();

    @Override
    protected void attachBaseContext(
            Context base
    ) {
        super.attachBaseContext(
                LocaleHelper.apply(
                        base
                )
        );
    }

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreate(
                savedInstanceState
        );

        gr =
                AppLang.isGreek(
                        this
                );

        if (!GELRemoteTargetManager
                .isRemoteMode(
                        this
                )) {

            Toast.makeText(
                    this,
                    gr
                            ? "Το Remote Device Mode δεν είναι ενεργό."
                            : "Remote Device Mode is not active.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        sessionId =
                GELRemoteTargetManager
                        .getSessionId(
                                this
                        );

        if (sessionId == null ||
                sessionId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    gr
                            ? "Δεν υπάρχει ενεργό Service Session."
                            : "No active Service Session.",
                    Toast.LENGTH_LONG
            ).show();

            GELRemoteTargetManager
                    .exitRemoteMode(
                            this
                    );
            finish();
            return;
        }

        try {
            buildScreen();
        } catch (Throwable t) {

            String error =
                    t.getClass().getSimpleName()
                            + ": "
                            + messageOf(t);

            android.util.Log.e(
                    "GELRemoteAssist",
                    "Remote Assist UI/WebRTC initialization failed",
                    t
            );

            Toast.makeText(
                    this,
                    "REMOTE ASSIST INIT ERROR\n" + error,
                    Toast.LENGTH_LONG
            ).show();

            GELRemoteTargetManager
                    .exitRemoteMode(
                            this
                    );

            finish();
            return;
        }

        getOnBackPressedDispatcher()
                .addCallback(
                        this,
                        new OnBackPressedCallback(
                                true
                        ) {
                            @Override
                            public void handleOnBackPressed() {
                                stopByTechnician(
                                        gr
                                                ? "Ο τεχνικός έκλεισε το GEL Remote Assist."
                                                : "Technician closed GEL Remote Assist."
                                );
                            }
                        }
                );

        startNegotiation();
    }

    private void buildScreen() {
        LinearLayout root =
                new LinearLayout(
                        this
                );

        root.setOrientation(
                LinearLayout.VERTICAL
        );
        root.setBackgroundColor(
                0xFF080808
        );

        int pad =
                dp(
                        12
                );

        TextView title =
                new TextView(
                        this
                );

        title.setText(
                "GEL REMOTE ASSIST"
        );
        title.setTextColor(
                0xFFFFD700
        );
        title.setTextSize(
                20f
        );
        title.setGravity(
                Gravity.CENTER
        );
        title.setPadding(
                pad,
                pad,
                pad,
                dp(6)
        );

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        statusText =
                new TextView(
                        this
                );

        statusText.setText(
                gr
                        ? "Προετοιμασία ασφαλούς GEL-only σύνδεσης..."
                        : "Preparing secure GEL-only connection..."
        );
        statusText.setTextColor(
                Color.WHITE
        );
        statusText.setTextSize(
                13f
        );
        statusText.setGravity(
                Gravity.CENTER
        );
        statusText.setPadding(
                pad,
                0,
                pad,
                dp(8)
        );

        root.addView(
                statusText,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        videoContainer =
                new FrameLayout(
                        this
                );
        videoContainer.setBackgroundColor(
                Color.BLACK
        );

        renderer =
                new SurfaceViewRenderer(
                        this
                );

        renderer.init(
                GELRemoteAssistRuntime
                        .getEglContext(
                                this
                        ),
                new RendererCommon.RendererEvents() {
                    @Override
                    public void onFirstFrameRendered() {
                        main.post(
                                () -> setStatus(
                                        gr
                                                ? "● LIVE — GEL iDoctor πελάτη"
                                                : "● LIVE — Customer GEL iDoctor",
                                        0xFF39FF14
                                )
                        );
                    }

                    @Override
                    public void onFrameResolutionChanged(
                            int videoWidth,
                            int videoHeight,
                            int rotation
                    ) {
                        main.post(
                                () -> {
                                    if (rotation == 90 ||
                                            rotation == 270) {
                                        remoteVideoWidth =
                                                videoHeight;
                                        remoteVideoHeight =
                                                videoWidth;
                                    } else {
                                        remoteVideoWidth =
                                                videoWidth;
                                        remoteVideoHeight =
                                                videoHeight;
                                    }
                                }
                        );
                    }
                }
        );

        renderer.setScalingType(
                RendererCommon.ScalingType.SCALE_ASPECT_FIT
        );
        renderer.setMirror(
                false
        );
        renderer.setEnableHardwareScaler(
                true
        );

        videoContainer.addView(
                renderer,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        touchLayer =
                new View(
                        this
                );

        touchLayer.setBackgroundColor(
                Color.TRANSPARENT
        );
        touchLayer.setClickable(
                true
        );
        touchLayer.setFocusable(
                true
        );
        touchLayer.setOnTouchListener(
                (v, event) -> handleTechnicianTouch(
                        event
                )
        );

        videoContainer.addView(
                touchLayer,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        LinearLayout.LayoutParams videoLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                );

        videoLp.setMargins(
                dp(8),
                0,
                dp(8),
                dp(8)
        );

        root.addView(
                videoContainer,
                videoLp
        );

        LinearLayout controls =
                new LinearLayout(
                        this
                );

        controls.setOrientation(
                LinearLayout.HORIZONTAL
        );
        controls.setGravity(
                Gravity.CENTER
        );
        controls.setPadding(
                dp(8),
                0,
                dp(8),
                dp(12)
        );

        backButton =
                makeControlButton(
                        gr
                                ? "← ΠΙΣΩ"
                                : "← BACK"
                );

        stopButton =
                makeControlButton(
                        gr
                                ? "ΤΕΡΜΑΤΙΣΜΟΣ"
                                : "STOP"
                );

        backButton.setEnabled(
                false
        );

        backButton.setOnClickListener(
                v -> sendBack()
        );

        stopButton.setOnClickListener(
                v -> stopByTechnician(
                        "Technician stopped GEL Remote Assist."
                )
        );

        LinearLayout.LayoutParams buttonLp =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        buttonLp.setMargins(
                dp(5),
                0,
                dp(5),
                0
        );

        controls.addView(
                backButton,
                buttonLp
        );
        controls.addView(
                stopButton,
                buttonLp
        );

        root.addView(
                controls,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        setContentView(
                root
        );
    }

    private void startNegotiation() {
        setStatus(
                gr
                        ? "Δημιουργία WebRTC session..."
                        : "Creating WebRTC session...",
                0xFFFFD700
        );

        try {
            PeerConnectionFactory factory =
                    GELRemoteAssistRuntime
                            .getFactory(
                                    this
                            );

            peerConnection =
                    factory.createPeerConnection(
                            GELRemoteAssistRuntime
                                    .createRtcConfiguration(),
                            new TechnicianPeerObserver()
                    );

            if (peerConnection == null) {
                throw new IllegalStateException(
                        "Unable to create WebRTC peer connection."
                );
            }

            peerConnection.addTransceiver(
                    MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO,
                    new RtpTransceiver.RtpTransceiverInit(
                            RtpTransceiver.RtpTransceiverDirection.RECV_ONLY
                    )
            );

            DataChannel.Init init =
                    new DataChannel.Init();

            init.ordered =
                    true;

            controlChannel =
                    peerConnection.createDataChannel(
                            "gel-control",
                            init
                    );

            if (controlChannel != null) {
                controlChannel.registerObserver(
                        new DataChannel.Observer() {
                            @Override
                            public void onBufferedAmountChange(
                                    long previousAmount
                            ) {}

                            @Override
                            public void onStateChange() {
                                main.post(
                                        GELRemoteAssistActivity.this::refreshControlAvailability
                                );
                            }

                            @Override
                            public void onMessage(
                                    DataChannel.Buffer buffer
                            ) {}
                        }
                );
            }

            peerConnection.createOffer(
                    new SimpleSdpObserver() {
                        @Override
                        public void onCreateSuccess(
                                SessionDescription offer
                        ) {
                            setTechnicianLocalOffer(
                                    offer
                            );
                        }

                        @Override
                        public void onCreateFailure(
                                String error
                        ) {
                            failAndClose(
                                    error
                            );
                        }
                    },
                    new MediaConstraints()
            );

        } catch (Throwable t) {
            failAndClose(
                    messageOf(t)
            );
        }
    }

    private void setTechnicianLocalOffer(
            SessionDescription offer
    ) {
        PeerConnection peer =
                peerConnection;

        if (peer == null) {
            failAndClose(
                    "WebRTC peer connection disappeared."
            );
            return;
        }

        peer.setLocalDescription(
                new SimpleSdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        sendOfferToCustomer(
                                offer.description
                        );
                    }

                    @Override
                    public void onSetFailure(
                            String error
                    ) {
                        failAndClose(
                                error
                        );
                    }
                },
                offer
        );
    }

    private void sendOfferToCustomer(
            String offerSdp
    ) {
        setStatus(
                gr
                        ? "Αναμονή έγκρισης από τον πελάτη..."
                        : "Waiting for customer approval...",
                0xFFFFD700
        );

        GELRemoteAssistSignaling.start(
                sessionId,
                offerSdp,
                (success, data, message) -> main.post(
                        () -> {
                            if (!success) {
                                failAndClose(
                                        message
                                );
                                return;
                            }

                            Object idRaw =
                                    data.get(
                                            "assistId"
                                    );

                            if (!(idRaw instanceof String) ||
                                    ((String) idRaw)
                                            .trim()
                                            .isEmpty()) {
                                failAndClose(
                                        "Remote Assist ID missing."
                                );
                                return;
                            }

                            assistId =
                                    ((String) idRaw)
                                            .trim();

                            attachSessionListener();
                            flushPendingLocalCandidates();
                        }
                )
        );
    }

    private void attachSessionListener() {
        if (sessionListener != null ||
                sessionId == null ||
                assistId == null) {
            return;
        }

        sessionListener =
                FirebaseFirestore
                        .getInstance()
                        .collection(
                                SESSIONS_COLLECTION
                        )
                        .document(
                                sessionId
                        )
                        .addSnapshotListener(
                                this,
                                (snapshot, error) -> {
                                    if (stopping) {
                                        return;
                                    }

                                    if (error != null) {
                                        setStatus(
                                                gr
                                                        ? "Σφάλμα signaling: " + error.getMessage()
                                                        : "Signaling error: " + error.getMessage(),
                                                0xFFFF6B6B
                                        );
                                        return;
                                    }

                                    if (snapshot == null ||
                                            !snapshot.exists()) {
                                        failAndClose(
                                                "Service Session ended."
                                        );
                                        return;
                                    }

                                    String sessionStatus =
                                            snapshot.getString(
                                                    "status"
                                            );

                                    if (!"CONNECTED".equals(
                                            sessionStatus
                                    )) {
                                        failAndClose(
                                                "Service Session is no longer CONNECTED."
                                        );
                                        return;
                                    }

                                    handleAssistSignal(
                                            snapshot.get(
                                                    "remoteAssist"
                                            )
                                    );
                                }
                        );
    }

    private void handleAssistSignal(
            Object rawAssist
    ) {
        if (!(rawAssist instanceof Map) ||
                assistId == null) {
            return;
        }

        Map<?, ?> assist =
                (Map<?, ?>) rawAssist;

        String incomingId =
                stringValue(
                        assist.get(
                                "id"
                        )
                );

        if (!assistId.equals(
                incomingId
        )) {
            return;
        }

        String status =
                stringValue(
                        assist.get(
                                "status"
                        )
                );

        if ("REJECTED".equals(status)) {
            String message =
                    stringValue(
                            assist.get(
                                    "message"
                            )
                    );

            failAndClose(
                    message != null
                            ? message
                            : "Customer declined GEL Remote Assist."
            );
            return;
        }

        if ("STOPPED".equals(status) ||
                "EXPIRED".equals(status)) {
            String message =
                    stringValue(
                            assist.get(
                                    "message"
                            )
                    );

            failAndClose(
                    message != null
                            ? message
                            : "GEL Remote Assist stopped."
            );
            return;
        }

        if ("ANSWERED".equals(status)) {
            String answerSdp =
                    stringValue(
                            assist.get(
                                    "answerSdp"
                            )
                    );

            if (!remoteDescriptionSet &&
                    answerSdp != null) {
                setCustomerAnswer(
                        answerSdp,
                        assist
                );
            } else {
                applyCustomerCandidates(
                        assist
                );
            }
        }
    }

    private void setCustomerAnswer(
            String answerSdp,
            Map<?, ?> assist
    ) {
        PeerConnection peer =
                peerConnection;

        if (peer == null) {
            return;
        }

        peer.setRemoteDescription(
                new SimpleSdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        remoteDescriptionSet =
                                true;

                        setStatus(
                                gr
                                        ? "Ο πελάτης ενέκρινε. Σύνδεση WebRTC..."
                                        : "Customer approved. Connecting WebRTC...",
                                0xFFFFD700
                        );

                        applyCustomerCandidates(
                                assist
                        );
                    }

                    @Override
                    public void onSetFailure(
                            String error
                    ) {
                        failAndClose(
                                error
                        );
                    }
                },
                new SessionDescription(
                        SessionDescription.Type.ANSWER,
                        answerSdp
                )
        );
    }

    private void applyCustomerCandidates(
            Map<?, ?> assist
    ) {
        if (!remoteDescriptionSet ||
                peerConnection == null ||
                assist == null) {
            return;
        }

        Object raw =
                assist.get(
                        "customerCandidates"
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

            if (!appliedCustomerCandidates.add(
                    key
            )) {
                continue;
            }

            try {
                peerConnection.addIceCandidate(
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

    private void queueOrSendLocalCandidate(
            IceCandidate candidate
    ) {
        if (candidate == null ||
                stopping) {
            return;
        }

        if (assistId == null) {
            pendingLocalCandidates.add(
                    candidate
            );
            return;
        }

        GELRemoteAssistSignaling.addCandidate(
                sessionId,
                assistId,
                candidate,
                null
        );
    }

    private void flushPendingLocalCandidates() {
        if (assistId == null ||
                pendingLocalCandidates.isEmpty()) {
            return;
        }

        List<IceCandidate> copy =
                new ArrayList<>(
                        pendingLocalCandidates
                );

        pendingLocalCandidates.clear();

        for (IceCandidate candidate : copy) {
            GELRemoteAssistSignaling.addCandidate(
                    sessionId,
                    assistId,
                    candidate,
                    null
            );
        }
    }

    private boolean handleTechnicianTouch(
            MotionEvent event
    ) {
        if (event == null) {
            return true;
        }

        int action =
                event.getActionMasked();

        if (action != MotionEvent.ACTION_DOWN &&
                action != MotionEvent.ACTION_MOVE &&
                action != MotionEvent.ACTION_UP &&
                action != MotionEvent.ACTION_CANCEL) {
            return true;
        }

        if (!canSendControl()) {
            touchSequenceActive =
                    false;
            return true;
        }

        double[] normalized =
                normalizedVideoPoint(
                        event.getX(),
                        event.getY()
                );

        if (action == MotionEvent.ACTION_DOWN) {
            touchSequenceActive =
                    normalized != null;
            lastMoveSentAt =
                    0L;

            if (!touchSequenceActive) {
                return true;
            }
        } else if (!touchSequenceActive) {
            return true;
        }

        if (action == MotionEvent.ACTION_MOVE) {
            long now =
                    android.os.SystemClock.uptimeMillis();

            if (now - lastMoveSentAt <
                    MOVE_SEND_INTERVAL_MS) {
                return true;
            }

            lastMoveSentAt =
                    now;
        }

        if (normalized == null) {
            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_CANCEL) {
                sendTouch(
                        MotionEvent.ACTION_CANCEL,
                        0.0d,
                        0.0d
                );
                touchSequenceActive =
                        false;
            }
            return true;
        }

        sendTouch(
                action,
                normalized[0],
                normalized[1]
        );

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL) {
            touchSequenceActive =
                    false;
        }

        return true;
    }

    private double[] normalizedVideoPoint(
            float x,
            float y
    ) {
        if (touchLayer == null ||
                remoteVideoWidth <= 0 ||
                remoteVideoHeight <= 0) {
            return null;
        }

        int viewWidth =
                touchLayer.getWidth();
        int viewHeight =
                touchLayer.getHeight();

        if (viewWidth <= 0 ||
                viewHeight <= 0) {
            return null;
        }

        double scale =
                Math.min(
                        viewWidth /
                                (double) remoteVideoWidth,
                        viewHeight /
                                (double) remoteVideoHeight
                );

        double displayWidth =
                remoteVideoWidth *
                        scale;
        double displayHeight =
                remoteVideoHeight *
                        scale;

        double left =
                (viewWidth -
                        displayWidth) /
                        2.0d;
        double top =
                (viewHeight -
                        displayHeight) /
                        2.0d;

        if (x < left ||
                x > left + displayWidth ||
                y < top ||
                y > top + displayHeight) {
            return null;
        }

        return new double[]{
                clamp01(
                        (x - left) /
                                displayWidth
                ),
                clamp01(
                        (y - top) /
                                displayHeight
                )
        };
    }

    private void sendTouch(
            int action,
            double normalizedX,
            double normalizedY
    ) {
        try {
            JSONObject json =
                    new JSONObject();

            json.put(
                    "t",
                    "touch"
            );
            json.put(
                    "a",
                    action
            );
            json.put(
                    "x",
                    normalizedX
            );
            json.put(
                    "y",
                    normalizedY
            );

            sendControlJson(
                    json.toString()
            );
        } catch (Throwable ignore) {}
    }

    private void sendBack() {
        if (!canSendControl()) {
            return;
        }

        sendControlJson(
                "{\"t\":\"back\"}"
        );
    }

    private void sendControlJson(
            String json
    ) {
        DataChannel channel =
                controlChannel;

        if (channel == null ||
                channel.state() !=
                        DataChannel.State.OPEN ||
                json == null) {
            return;
        }

        byte[] bytes =
                json.getBytes(
                        StandardCharsets.UTF_8
                );

        if (bytes.length > 2048) {
            return;
        }

        try {
            channel.send(
                    new DataChannel.Buffer(
                            ByteBuffer.wrap(
                                    bytes
                            ),
                            false
                    )
            );
        } catch (Throwable ignore) {}
    }

    private boolean canSendControl() {
        return !stopping &&
                controlChannel != null &&
                controlChannel.state() ==
                        DataChannel.State.OPEN &&
                remoteVideoWidth > 0 &&
                remoteVideoHeight > 0;
    }

    private void refreshControlAvailability() {
        boolean enabled =
                canSendControl();

        if (backButton != null) {
            backButton.setEnabled(
                    enabled
            );
        }
    }

    private void attachRemoteVideoTrack(
            @Nullable MediaStreamTrack track
    ) {
        if (!(track instanceof VideoTrack)) {
            return;
        }

        VideoTrack newTrack =
                (VideoTrack) track;

        if (remoteVideoTrack ==
                newTrack) {
            return;
        }

        if (remoteVideoTrack != null &&
                renderer != null) {
            try {
                remoteVideoTrack.removeSink(
                        renderer
                );
            } catch (Throwable ignore) {}
        }

        remoteVideoTrack =
                newTrack;

        if (renderer != null) {
            newTrack.addSink(
                    renderer
            );
        }
    }

    private void stopByTechnician(
            String message
    ) {
        if (stopping) {
            return;
        }

        stopping =
                true;

        String currentAssist =
                assistId;
        String currentSession =
                sessionId;

        cleanupPeer();

        GELRemoteTargetManager
                .exitRemoteMode(
                        this
                );

        if (currentSession != null &&
                currentAssist != null) {

            GELRemoteAssistSignaling.stop(
                    currentSession,
                    currentAssist,
                    message,
                    (success, data, error) -> main.post(
                            this::finish
                    )
            );
        } else {
            finish();
        }
    }

    private void failAndClose(
            @Nullable String error
    ) {
        if (stopping) {
            return;
        }

        stopping =
                true;

        String currentSession =
                sessionId;
        String currentAssist =
                assistId;

        String message =
                error != null &&
                        !error.trim().isEmpty()
                        ? error.trim()
                        : "GEL Remote Assist failed.";

        setStatus(
                message,
                0xFFFF6B6B
        );

        cleanupPeer();

        if (currentSession != null &&
                currentAssist != null) {
            GELRemoteAssistSignaling.stop(
                    currentSession,
                    currentAssist,
                    message,
                    null
            );
        }

        GELRemoteTargetManager
                .exitRemoteMode(
                        this
                );

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();

        main.postDelayed(
                this::finish,
                900L
        );
    }

    private void cleanupPeer() {
        if (sessionListener != null) {
            try {
                sessionListener.remove();
            } catch (Throwable ignore) {}
            sessionListener =
                    null;
        }

        if (remoteVideoTrack != null &&
                renderer != null) {
            try {
                remoteVideoTrack.removeSink(
                        renderer
                );
            } catch (Throwable ignore) {}
            remoteVideoTrack =
                    null;
        }

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

        pendingLocalCandidates.clear();
        appliedCustomerCandidates.clear();
        remoteDescriptionSet =
                false;
        touchSequenceActive =
                false;

        refreshControlAvailability();
    }

    @Override
    protected void onDestroy() {
        if (!stopping &&
                assistId != null &&
                sessionId != null) {

            GELRemoteAssistSignaling.stop(
                    sessionId,
                    assistId,
                    "Technician Remote Assist Activity closed.",
                    null
            );
        }

        cleanupPeer();

        if (renderer != null) {
            try {
                renderer.release();
            } catch (Throwable ignore) {}
            renderer =
                    null;
        }

        super.onDestroy();
    }

    private void setStatus(
            String text,
            int color
    ) {
        if (statusText == null) {
            return;
        }

        statusText.setText(
                text
        );
        statusText.setTextColor(
                color
        );
    }

    private Button makeControlButton(
            String text
    ) {
        Button button =
                new Button(
                        this
                );

        button.setText(
                text
        );
        button.setAllCaps(
                false
        );
        button.setTextColor(
                Color.WHITE
        );
        button.setBackgroundResource(
                R.drawable.gel_btn_outline_selector
        );

        return button;
    }

    private int dp(
            int value
    ) {
        return Math.round(
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }

    private static double clamp01(
            double value
    ) {
        return Math.max(
                0.0d,
                Math.min(
                        1.0d,
                        value
                )
        );
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
            return "GEL Remote Assist failed.";
        }

        String message =
                t.getMessage();

        return message != null &&
                !message.trim().isEmpty()
                ? message.trim()
                : t.getClass()
                        .getSimpleName();
    }

    private final class TechnicianPeerObserver
            implements PeerConnection.Observer {

        @Override
        public void onSignalingChange(
                PeerConnection.SignalingState signalingState
        ) {}

        @Override
        public void onIceConnectionChange(
                PeerConnection.IceConnectionState iceConnectionState
        ) {
            main.post(
                    () -> {
                        if (stopping) {
                            return;
                        }

                        if (iceConnectionState ==
                                PeerConnection.IceConnectionState.CONNECTED ||
                                iceConnectionState ==
                                        PeerConnection.IceConnectionState.COMPLETED) {

                            setStatus(
                                    gr
                                            ? "WebRTC συνδέθηκε — αναμονή εικόνας..."
                                            : "WebRTC connected — waiting for video...",
                                    0xFF39FF14
                            );

                        } else if (iceConnectionState ==
                                PeerConnection.IceConnectionState.FAILED) {

                            failAndClose(
                                    gr
                                            ? "Αποτυχία WebRTC ICE. Θα χρειαστεί TURN για αυτό το δίκτυο."
                                            : "WebRTC ICE failed. This network may require TURN."
                            );
                        }
                    }
            );
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
            main.post(
                    () -> queueOrSendLocalCandidate(
                            candidate
                    )
            );
        }

        @Override
        public void onIceCandidatesRemoved(
                IceCandidate[] iceCandidates
        ) {}

        @Override
        public void onAddStream(
                MediaStream mediaStream
        ) {
            if (mediaStream == null ||
                    mediaStream.videoTracks == null ||
                    mediaStream.videoTracks.isEmpty()) {
                return;
            }

            main.post(
                    () -> attachRemoteVideoTrack(
                            mediaStream.videoTracks.get(
                                    0
                            )
                    )
            );
        }

        @Override
        public void onRemoveStream(
                MediaStream mediaStream
        ) {}

        @Override
        public void onDataChannel(
                DataChannel dataChannel
        ) {}

        @Override
        public void onRenegotiationNeeded() {}

        @Override
        public void onAddTrack(
                RtpReceiver receiver,
                MediaStream[] mediaStreams
        ) {
            if (receiver == null) {
                return;
            }

            main.post(
                    () -> attachRemoteVideoTrack(
                            receiver.track()
                    )
            );
        }

        @Override
        public void onTrack(
                RtpTransceiver transceiver
        ) {
            if (transceiver == null ||
                    transceiver.getReceiver() == null) {
                return;
            }

            main.post(
                    () -> attachRemoteVideoTrack(
                            transceiver
                                    .getReceiver()
                                    .track()
                    )
            );
        }
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
}
