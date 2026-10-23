// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELAutoActivityHook.java — GEL FIXED v4.8.1 (Stable Unified Foldable Engine)
// NOTE: Full file ready for copy-paste. (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gel.cleaner.base.GELFoldableCallback;
import com.gel.cleaner.base.GELFoldableCallback.Posture;
import com.gel.cleaner.base.GELFoldableDetector;
import com.gel.cleaner.base.GELFoldableUIManager;
import com.gel.cleaner.base.GELFoldableAnimationPack;


public abstract class GELAutoActivityHook extends AppCompatActivity
        implements GELFoldableCallback {

    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;

    // Direct, R8-visible foldable engines.
    // No runtime class-name lookup is required.
    private GELFoldableOrchestrator foldOrchestrator;
    private GELFoldableAnimationPack foldAnimPack;

    private boolean lastInner = false;

    private boolean remoteLanguageReceiverRegistered = false;

    private final BroadcastReceiver remoteLanguageReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(
                        Context context,
                        Intent intent
                ) {

                    if (intent == null ||
                            !GELRemoteCommandExecutor.ACTION_REMOTE_LANGUAGE_CHANGED
                                    .equals(intent.getAction())) {
                        return;
                    }

                    recreate();
                }
            };

    // ============================================================
    // CONTEXT HOOK
    // ============================================================
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    // ============================================================
    // LIFECYCLE
    // ============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GELAutoDP.init(this);

        uiManager    = new GELFoldableUIManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        initExtraFoldableEngines();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!remoteLanguageReceiverRegistered) {

            ContextCompat.registerReceiver(
                    this,
                    remoteLanguageReceiver,
                    new IntentFilter(
                            GELRemoteCommandExecutor.ACTION_REMOTE_LANGUAGE_CHANGED
                    ),
                    ContextCompat.RECEIVER_NOT_EXPORTED
            );

            remoteLanguageReceiverRegistered = true;
        }
    }

    @Override
    protected void onStop() {

        if (remoteLanguageReceiverRegistered) {
            try {
                unregisterReceiver(
                        remoteLanguageReceiver
                );
            } catch (Throwable ignore) {}

            remoteLanguageReceiverRegistered = false;
        }

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // ============================================================
        // CUSTOMER REMOTE SESSION TRANSPORT
        // Safe no-op on technician/local installations.
        // ============================================================
        GELRemoteCommandService.ensureRunning(this);

        // ============================================================
        // GEL-ONLY REMOTE ASSIST ACTIVITY REGISTRY
        // Only GELAutoActivityHook instances can ever become customer
        // capture / input targets.
        // ============================================================
        GELRemoteAssistRuntime.onActivityResumed(this);

        // ============================================================
        // TECHNICIAN REMOTE TARGET WARNING
        // Visible on every GELAutoActivityHook screen while REMOTE mode
        // is active, so local/remote target can never be confused.
        // ============================================================
        GELRemoteTargetManager.syncAvailability(this);
        GELRemoteTargetManager.updateOverlay(this);

        if (foldDetector != null) foldDetector.start();

        if (foldAnimPack != null) {
            foldAnimPack.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (foldAnimPack != null) {
            foldAnimPack.onPause();
        }

        if (foldDetector != null) foldDetector.stop();

        GELRemoteAssistRuntime.onActivityPaused(this);

        super.onPause();
    }

    // ============================================================
    // CONFIG CHANGES
    // ============================================================
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        GELAutoDP.init(this);

        GELRemoteTargetManager.updateOverlay(this);

        if (uiManager != null) uiManager.applyUI(lastInner);

        if (foldAnimPack != null) {
            foldAnimPack.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);

        GELAutoDP.init(this);

        if (uiManager != null) uiManager.applyUI(lastInner);

        if (foldAnimPack != null) {
            foldAnimPack.onMultiWindowModeChanged(isInMultiWindowMode);
        }
    }

    // ============================================================
    // UNIFIED FOLDABLE CALLBACKS
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull Posture posture) {
        if (foldOrchestrator != null) {
            foldOrchestrator.onPostureChanged(posture);
        }

        if (foldAnimPack != null) {
            foldAnimPack.onPostureChanged(posture);
        }
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        if (isInner == lastInner) return;

        lastInner = isInner;

        if (uiManager != null) uiManager.applyUI(isInner);

        if (foldOrchestrator != null) {
            foldOrchestrator.onScreenChanged(isInner);
        }

        if (foldAnimPack != null) {
            foldAnimPack.onScreenChanged(isInner);
        }
    }

    // ============================================================
    // EXTRA ENGINES (Reflection Safe)
    // ============================================================
    private void initExtraFoldableEngines() {
        // Preserve the existing runtime behaviour, but make the
        // concrete GEL classes visible to the compiler and R8.
        foldOrchestrator = new GELFoldableOrchestrator(this);
        foldAnimPack = new GELFoldableAnimationPack(this);

        // The current orchestrator has no onCreate()/bind() API.
        // AnimationPack does, so preserve that valid lifecycle hook.
        foldAnimPack.onCreate();
    }

    // ============================================================
    // REFLECTION HELPERS
    // ============================================================



    // ============================================================
    // GLOBAL HELPERS
    // ============================================================
    public int dp(int x)     { return GELAutoDP.dp(x); }
    public float sp(float x) { return GELAutoDP.sp(x); }
    public int px(int x)     { return GELAutoDP.px(x); }
}
