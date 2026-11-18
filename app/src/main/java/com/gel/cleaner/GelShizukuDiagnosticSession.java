package com.gel.cleaner;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rikka.shizuku.Shizuku;

/**
 * Optional, foreground/manual opt-in. No app-controlled "grant" bypass.
 * The actual Shizuku server requests and tracks permission. This class only
 * starts a single, per-Activity read-only session after user interaction.
 */
public final class GelShizukuDiagnosticSession {
    public interface StatusListener { void onStatus(String status); }
    public interface EvidenceCallback { void onEvidence(String wire); }

    private static final int REQUEST_CODE = 14325;
    private static final String CONSENT_PREFS = "gel_shizuku_consent";
    private static final String APPROVED_KEY = "approved_readonly_diagnostics";
    // Initialized once in the constructor before any Shizuku callback is registered.
    // Non-final because the permission callback is declared as a field initializer.
    private SharedPreferences consent;
    private final Activity activity;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final StatusListener listener;
    private final Shizuku.UserServiceArgs args;
    private volatile IGelShizukuReadOnly remote;
    private volatile GelShizukuEvidence.Capacity cachedCapacity;
    private volatile boolean userRequested;
    private volatile boolean bound;
    private volatile boolean closed;

    /** Rebind when Shizuku finishes attaching after the Activity opens.
     * Never launch a system permission dialog merely because a binder arrived. */
    private final Shizuku.OnBinderReceivedListener binderReceived = () -> {
        if (closed || !userRequested || !hasRememberedConsent()) return;
        bindIfAlreadyPermitted();
    };

    private final Shizuku.OnBinderDeadListener binderDead = () -> {
        remote = null;
        cachedCapacity = null;
        bound = false;
        report("DISCONNECTED");
    };

    private final Shizuku.OnRequestPermissionResultListener permissionResult =
            (code, result) -> {
                if (code != REQUEST_CODE || !userRequested || closed) return;
                if (result == PackageManager.PERMISSION_GRANTED) bindIfPermitted();
                else {
                    consent.edit().putBoolean(APPROVED_KEY, false).apply();
                    userRequested = false;
                    report("DENIED");
                }
            };

    private final ServiceConnection connection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder binder) {
            if (closed || !userRequested) return;
            remote = IGelShizukuReadOnly.Stub.asInterface(binder);
            if (!isReady()) {
                remote = null;
                bound = false;
                report("PERMISSION_UNAVAILABLE");
                return;
            }
            report("READY");
            // Cache only the small battery-capacity record; no background
            // crash or personal-log harvesting.
            readAsync(GelShizukuEvidence.CAPACITY,
                    wire -> cachedCapacity = GelShizukuEvidence.parseCapacity(wire));
        }

        @Override public void onServiceDisconnected(ComponentName name) {
            remote = null;
            cachedCapacity = null;
            bound = false;
            report("DISCONNECTED");
        }
    };

    public GelShizukuDiagnosticSession(Activity activity, StatusListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.consent = activity.getSharedPreferences(CONSENT_PREFS, Context.MODE_PRIVATE);
        args = new Shizuku.UserServiceArgs(
                new ComponentName(activity, GelShizukuReadOnlyService.class))
                .tag("gel-idoctor-readonly-diagnostics-v1")
                .processNameSuffix("gel_evidence")
                .version(1)
                .daemon(false);
        Shizuku.addBinderDeadListener(binderDead);
        Shizuku.addRequestPermissionResultListener(permissionResult);
        Shizuku.addBinderReceivedListenerSticky(binderReceived);
    }

    /** Local consent records a conscious tap on GEL's YES control.
     * Shizuku's own permission is always checked independently. */
    public void requestFromUser() {
        if (closed) return;
        consent.edit().putBoolean(APPROVED_KEY, true).apply();
        userRequested = true;
        try {
            if (!Shizuku.pingBinder()) {
                report("SHIZUKU_NOT_RUNNING");
                return;
            }
            if (Shizuku.isPreV11() || Shizuku.getVersion() < 13) {
                report("OUTDATED_SHIZUKU");
                return;
            }
            // Deliberately decline root/Sui identity: this QA service is
            // designed and reviewed only for ADB shell uid 2000.
            if (Shizuku.getUid() != 2000) {
                report("ADB_SHELL_ONLY");
                return;
            }
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                bindIfPermitted();
                return;
            }
            if (Shizuku.shouldShowRequestPermissionRationale()) {
                report("PERMISSION_DENIED_PERMANENTLY");
                return;
            }
            report("AWAITING_SHIZUKU_PERMISSION");
            Shizuku.requestPermission(REQUEST_CODE);
        } catch (RuntimeException failure) {
            report("SHIZUKU_NOT_RUNNING");
        }
    }

    public boolean hasRememberedConsent() {
        return consent.getBoolean(APPROVED_KEY, false);
    }

    /** Open an already-approved session after screen recreation.
     * This method MUST NOT call requestPermission or bypass Shizuku's grant. */
    public void restoreApprovedSession() {
        if (closed || !hasRememberedConsent()) return;
        userRequested = true;
        if (!Shizuku.pingBinder()) {
            report("SHIZUKU_NOT_RUNNING");
            return; // binderReceived will retry if the server becomes ready
        }
        bindIfAlreadyPermitted();
    }

    private void bindIfAlreadyPermitted() {
        if (closed || !userRequested || !hasRememberedConsent()) return;
        try {
            if (!Shizuku.pingBinder()) {
                report("SHIZUKU_NOT_RUNNING");
                return;
            }
            if (Shizuku.isPreV11() || Shizuku.getVersion() < 13) {
                report("OUTDATED_SHIZUKU");
                return;
            }
            if (Shizuku.getUid() != 2000) {
                report("ADB_SHELL_ONLY");
                return;
            }
            if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                report("PERMISSION_UNAVAILABLE");
                return;
            }
            bindIfPermitted(); // grant verified; never requests new permission
        } catch (RuntimeException failure) {
            report("SERVICE_UNAVAILABLE");
        }
    }

    private void bindIfPermitted() {
        if (closed || !userRequested || bound) return;
        try {
            if (!Shizuku.pingBinder() || Shizuku.getUid() != 2000 ||
                    Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                report("PERMISSION_UNAVAILABLE");
                return;
            }
            bound = true;
            report("CONNECTING");
            Shizuku.bindUserService(args, connection);
        } catch (RuntimeException failure) {
            bound = false;
            remote = null;
            report("SERVICE_UNAVAILABLE");
        }
    }

    public boolean isReady() {
        try {
            return !closed && userRequested && remote != null && Shizuku.pingBinder()
                    && Shizuku.getUid() == 2000
                    && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    /** Returns null when not actually available. Never returns cached data
     * after revocation, disconnection or stopping the Lab screen. */
    public GelShizukuEvidence.Capacity getCapacity() {
        return isReady() ? cachedCapacity : null;
    }

    public void readAsync(int operation, EvidenceCallback callback) {
        if (closed || !isReady()) {
            main.post(() -> { if (!closed) callback.onEvidence("UNAVAILABLE|NO_APPROVED_SESSION"); });
            return;
        }
        if (operation < GelShizukuEvidence.CAPACITY ||
                operation > GelShizukuEvidence.APP_OPS) {
            main.post(() -> { if (!closed) callback.onEvidence("UNAVAILABLE|UNKNOWN_OPERATION"); });
            return;
        }
        worker.execute(() -> {
            String value = "UNAVAILABLE|SESSION_LOST";
            try {
                // Re-evaluate permission BEFORE each privileged request.
                if (isReady()) {
                    IGelShizukuReadOnly live = remote;
                    if (live != null) value = live.probe(operation);
                }
            } catch (RemoteException | RuntimeException ignored) {
                value = "UNAVAILABLE|SHELL_PROBE_FAILED";
            }
            final String finalValue = value;
            main.post(() -> {
                if (!closed && userRequested) callback.onEvidence(finalValue);
            });
        });
    }

    /** Explicit STOP is opt-out; ordinary Activity close is NOT a revocation. */
    public void stop() {
        endSession(true);
    }

    private void endSession(boolean forgetLocalConsent) {
        if (forgetLocalConsent) {
            consent.edit().putBoolean(APPROVED_KEY, false).apply();
        }
        userRequested = false;
        remote = null;
        cachedCapacity = null;
        if (bound) {
            try {
                Shizuku.unbindUserService(args, connection, true);
            } catch (RuntimeException ignored) { }
        }
        bound = false;
        report("NORMAL_MODE");
    }

    public void close() {
        if (closed) return;
        endSession(false); // preserve explicit approval for next screen visit
        closed = true;
        try {
            Shizuku.removeBinderDeadListener(binderDead);
            Shizuku.removeBinderReceivedListener(binderReceived);
            Shizuku.removeRequestPermissionResultListener(permissionResult);
        } catch (RuntimeException ignored) { }
        worker.shutdownNow();
    }

    private void report(String status) {
        main.post(() -> {
            if (!closed && listener != null) listener.onStatus(status);
        });
    }
}
