package com.gel.cleaner;

import android.content.Context;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared process-local runtime for GEL Remote Assist.
 *
 * It intentionally tracks only GELAutoActivityHook instances. This is the
 * hard app-only control boundary: remote input is never dispatched to an
 * Activity outside GEL iDoctor.
 */
public final class GELRemoteAssistRuntime {

    private static final Object LOCK = new Object();

    private static WeakReference<GELAutoActivityHook> activeActivity =
            new WeakReference<>(null);

    private static boolean webRtcInitialized = false;
    private static EglBase rootEglBase;
    private static PeerConnectionFactory peerConnectionFactory;

    private GELRemoteAssistRuntime() {}

    public static void onActivityResumed(
            GELAutoActivityHook activity
    ) {
        if (activity == null) return;

        synchronized (LOCK) {
            activeActivity =
                    new WeakReference<>(activity);
        }

        GELRemoteAssistCustomer.updateOverlay(activity);
    }

    public static void onActivityPaused(
            GELAutoActivityHook activity
    ) {
        if (activity == null) return;

        synchronized (LOCK) {
            GELAutoActivityHook current =
                    activeActivity.get();

            if (current == activity) {
                activeActivity =
                        new WeakReference<>(null);
            }
        }
    }

    public static GELAutoActivityHook getActiveActivity() {
        synchronized (LOCK) {
            GELAutoActivityHook activity =
                    activeActivity.get();

            if (activity == null ||
                    activity.isFinishing() ||
                    activity.isDestroyed()) {
                return null;
            }

            return activity;
        }
    }

    public static void ensureWebRtc(
            Context context
    ) {
        if (context == null) {
            throw new IllegalArgumentException(
                    "Context is required for WebRTC."
            );
        }

        synchronized (LOCK) {
            if (peerConnectionFactory != null &&
                    rootEglBase != null) {
                return;
            }

            Context app =
                    context.getApplicationContext();

            if (!webRtcInitialized) {
                runtimeDiag(
                        app,
                        "RUNTIME_BEFORE_INIT_OPTIONS"
                );

                PeerConnectionFactory.InitializationOptions options =
                        PeerConnectionFactory.InitializationOptions
                                .builder(app)
                                .setEnableInternalTracer(false)
                                .setNativeLibraryLoader(
                                        name -> {
                                            runtimeDiag(
                                                    app,
                                                    "RUNTIME_BEFORE_NATIVE_LOAD"
                                            );

                                            try {
                                                System.loadLibrary(
                                                        name
                                                );

                                                runtimeDiag(
                                                        app,
                                                        "RUNTIME_AFTER_NATIVE_LOAD"
                                                );

                                                return true;

                                            } catch (RuntimeException | Error t) {

                                                runtimeDiag(
                                                        app,
                                                        "RUNTIME_NATIVE_LOAD_ERROR ["
                                                                + t.getClass().getSimpleName()
                                                                + "]: "
                                                                + String.valueOf(
                                                                        t.getMessage()
                                                                )
                                                );

                                                throw t;
                                            }
                                        }
                                )
                                .createInitializationOptions();

                runtimeDiag(
                        app,
                        "RUNTIME_AFTER_INIT_OPTIONS"
                );

                runtimeDiag(
                        app,
                        "RUNTIME_BEFORE_PCF_INITIALIZE"
                );

                PeerConnectionFactory.initialize(
                        options
                );

                runtimeDiag(
                        app,
                        "RUNTIME_AFTER_PCF_INITIALIZE"
                );

                webRtcInitialized = true;
            }

            runtimeDiag(
                    app,
                    "RUNTIME_BEFORE_EGL_CREATE"
            );

            rootEglBase =
                    EglBase.create();

            runtimeDiag(
                    app,
                    "RUNTIME_AFTER_EGL_CREATE"
            );

            runtimeDiag(
                    app,
                    "RUNTIME_BEFORE_ENCODER_FACTORY"
            );

            DefaultVideoEncoderFactory encoderFactory =
                    new DefaultVideoEncoderFactory(
                            rootEglBase.getEglBaseContext(),
                            true,
                            true
                    );

            runtimeDiag(
                    app,
                    "RUNTIME_AFTER_ENCODER_FACTORY"
            );

            runtimeDiag(
                    app,
                    "RUNTIME_BEFORE_DECODER_FACTORY"
            );

            DefaultVideoDecoderFactory decoderFactory =
                    new DefaultVideoDecoderFactory(
                            rootEglBase.getEglBaseContext()
                    );

            runtimeDiag(
                    app,
                    "RUNTIME_AFTER_DECODER_FACTORY"
            );

            runtimeDiag(
                    app,
                    "RUNTIME_BEFORE_CREATE_FACTORY"
            );

            peerConnectionFactory =
                    PeerConnectionFactory
                            .builder()
                            .setVideoEncoderFactory(
                                    encoderFactory
                            )
                            .setVideoDecoderFactory(
                                    decoderFactory
                            )
                            .createPeerConnectionFactory();

            runtimeDiag(
                    app,
                    "RUNTIME_AFTER_CREATE_FACTORY"
            );
        }
    }

    private static void runtimeDiag(
            Context context,
            String stage
    ) {
        try {
            context.getSharedPreferences(
                    "gel_remote_assist_diag",
                    Context.MODE_PRIVATE
            )
                    .edit()
                    .putString(
                            "last_stage",
                            stage
                    )
                    .putLong(
                            "last_stage_time",
                            System.currentTimeMillis()
                    )
                    .commit();

            android.util.Log.e(
                    "GELRemoteAssistDiag",
                    stage
            );

        } catch (Throwable ignore) {}
    }

    public static PeerConnectionFactory getFactory(
            Context context
    ) {
        ensureWebRtc(context);

        synchronized (LOCK) {
            return peerConnectionFactory;
        }
    }

    public static EglBase.Context getEglContext(
            Context context
    ) {
        ensureWebRtc(context);

        synchronized (LOCK) {
            return rootEglBase.getEglBaseContext();
        }
    }

    /**
     * POC ICE configuration.
     *
     * STUN is enough for many networks and for local testing. A production
     * release should add authenticated TURN because some carrier / symmetric
     * NAT combinations cannot establish a direct peer path.
     */
    public static PeerConnection.RTCConfiguration createRtcConfiguration() {
        List<PeerConnection.IceServer> iceServers =
                new ArrayList<>();

        iceServers.add(
                PeerConnection.IceServer
                        .builder(
                                "stun:stun.l.google.com:19302"
                        )
                        .createIceServer()
        );

        iceServers.add(
                PeerConnection.IceServer
                        .builder(
                                "stun:stun1.l.google.com:19302"
                        )
                        .createIceServer()
        );

        PeerConnection.RTCConfiguration config =
                new PeerConnection.RTCConfiguration(
                        iceServers
                );

        config.sdpSemantics =
                PeerConnection.SdpSemantics.UNIFIED_PLAN;

        config.continualGatheringPolicy =
                PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;

        return config;
    }
}
