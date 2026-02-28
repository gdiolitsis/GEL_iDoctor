package com.gel.cleaner;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class OptimizerMiniScheduler extends scheduler {

    // prefs
    private static final String PREFS = "gel_prefs";
    private static final String KEY_PULSE_ENABLED = "pulse_enabled";

    // thresholds (simple + honest)
    private static final int CACHE_PERCENT_ALERT = 80;

    public MiniPulseWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        Context ctx = getApplicationContext();

        // Respect user switch
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (!sp.getBoolean(KEY_PULSE_ENABLED, false)) {
            return Result.success();
        }

        // --- MINI CHECKS (must stay <1s) ---
        boolean cacheAlert = false;

        // 1) Cache > 80% (only if we can read stats on this device + permission)
        // NOTE: This is best-effort and safe. If not possible -> just skip silently.
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                StorageStatsManager ssm =
                        (StorageStatsManager) ctx.getSystemService(Context.STORAGE_STATS_SERVICE);

                if (ssm != null) {
                    // We check only our own package quickly (honest + fast).
                    // Global per-app scan would be heavy + not <1s reliably.
                    StorageStats st = ssm.queryStatsForPackage(
                            android.os.storage.StorageManager.UUID_DEFAULT,
                            ctx.getPackageName(),
                            android.os.UserHandle.getUserHandleForUid(Process.myUid())
                    );

                    if (st != null) {
                        long appBytes = st.getAppBytes();
                        long dataBytes = st.getDataBytes();
                        long cacheBytes = st.getCacheBytes();

                        long appSize = appBytes + dataBytes;
                        int percent = (appSize > 0)
                                ? (int) Math.round((cacheBytes * 100.0) / appSize)
                                : 0;

                        if (percent >= CACHE_PERCENT_ALERT) {
                            cacheAlert = true;
                        }
                    }
                }
            }
        } catch (Throwable ignore) {}

        // 2) Temperatures / crashes: we DO NOT claim app-crashes detection.
        // We can only notify “possible instability” if you later add your own safe signals.

        // If nothing interesting -> silent success
        if (!cacheAlert) return Result.success();

        // --- Notify ---
        boolean gr = AppLang.isGreek(ctx);

        String title = gr ? "GEL — Mini Έλεγχος" : "GEL — Mini Check";
        String body =
                gr
                ? "Εντοπίστηκε ένδειξη: υψηλή χρήση cache.\nΠάτησε για να δεις προτάσεις."
                : "Signal detected: high cache usage.\nTap to review recommendations.";

        // Open MainActivity on tap
        Intent i = new Intent(ctx, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // If you already have a Notification helper/channel, use it.
        // Otherwise this will work only if your app has a default channel on Android 8+.
        try {
            NotificationCompat.Builder nb = new NotificationCompat.Builder(ctx, "gel_default")
                    .setSmallIcon(android.R.drawable.stat_notify_more)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            NotificationManagerCompat.from(ctx).notify(19001, nb.build());
        } catch (Throwable ignore) {}

        return Result.success();
    }
}
