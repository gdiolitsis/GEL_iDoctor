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
                PeerConnectionFactory.InitializationOptions options =
                        PeerConnectionFactory.InitializationOptions
                                .builder(app)
                                .setEnableInternalTracer(false)
                                .createInitializationOptions();

                PeerConnectionFactory.initialize(
                        options
                );

                webRtcInitialized = true;
            }

            rootEglBase =
                    EglBase.create();

            DefaultVideoEncoderFactory encoderFactory =
                    new DefaultVideoEncoderFactory(
                            rootEglBase.getEglBaseContext(),
                            true,
                            true
                    );

            DefaultVideoDecoderFactory decoderFactory =
                    new DefaultVideoDecoderFactory(
                            rootEglBase.getEglBaseContext()
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
        }
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
