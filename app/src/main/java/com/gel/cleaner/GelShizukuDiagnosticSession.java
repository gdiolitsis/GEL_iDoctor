package com.gel.cleaner;

import android.app.Activity;
import android.content.ComponentName;
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
                else report("DENIED");
            };

    private final ServiceConnection connection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder binder) {
            if (closed || !userRequested) return;
            remote = IGelShizukuReadOnly.Stub.asInterface(binder);
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
        args = new Shizuku.UserServiceArgs(
                new ComponentName(activity, GelShizukuReadOnlyService.class))
                .tag("gel-idoctor-readonly-diagnostics-v1")
                .processNameSuffix("gel_evidence")
                .version(1)
                .daemon(false);
        Shizuku.addBinderDeadListener(binderDead);
        Shizuku.addRequestPermissionResultListener(permissionResult);
    }

    public void requestFromUser() {
        if (closed) return;
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
                operation > GelShizukuEvidence.BOOT) {
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

    public void stop() {
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
        stop();
        closed = true;
        try {
            Shizuku.removeBinderDeadListener(binderDead);
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
