package com.gel.cleaner;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class OptimizerMiniScheduler extends Worker {

    private static final String PREFS = "gel_prefs";
    private static final String KEY_PULSE_ENABLED = "pulse_enabled";
    private static final int CACHE_PERCENT_ALERT = 80;

    public OptimizerMiniScheduler(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

    @NonNull
    @Override
public Result doWork() {

    Context ctx = getApplicationContext();

    SharedPreferences sp =
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

    if (!sp.getBoolean(KEY_PULSE_ENABLED, false)) {
        return Result.success();
    }

    // --- Anti Spam (24h cooldown) ---
    long lastNotify = sp.getLong("last_mini_notify", 0);
    long now = System.currentTimeMillis();

    if (now - lastNotify < 24L * 60L * 60L * 1000L) {
        return Result.success();
    }

    // --- Cache check ---
    boolean cacheHigh = false;

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            StorageStatsManager ssm =
                    (StorageStatsManager) ctx.getSystemService(Context.STORAGE_STATS_SERVICE);

            if (ssm != null) {

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

                    if (percent >= 85) {
                        cacheHigh = true;
                    }
                }
            }
        }
    } catch (Throwable ignore) {}

    // --- Run probes ---
    MiniHealthProbes.Result r =
            MiniHealthProbes.run(ctx, cacheHigh);

// ----------------------
// TEMP TEST MODE
// ----------------------
r.score = 2;
// ----------------------

    if (r.score < 2) {
        return Result.success();
    }

    // --- Notify ---
    boolean gr = AppLang.isGreek(ctx);

    String title = gr
            ? "Εντοπίστηκε ένδειξη επιβάρυνσης"
            : "Device Health Signal";

    String body = gr
            ? "Παρατηρήθηκε αυξημένη δραστηριότητα στο σύστημα."
            : "Increased system load detected.";

    try {
        NotificationCompat.Builder nb =
                new NotificationCompat.Builder(ctx, "gel_default")
                        .setSmallIcon(android.R.drawable.stat_notify_more)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(ctx).notify(19001, nb.build());

        sp.edit().putLong("last_mini_notify", now).apply();

    } catch (Throwable ignore) {}

    return Result.success();
}

        boolean cacheAlert = false;

        // -------------------------------------------------
        // Cache quick check (< 1 sec logic)
        // -------------------------------------------------
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                StorageStatsManager ssm =
                        (StorageStatsManager) ctx.getSystemService(Context.STORAGE_STATS_SERVICE);

                if (ssm != null) {

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

        // -------------------------------------------------
        // Notify only if needed
        // -------------------------------------------------
        if (cacheAlert) {

            boolean gr = AppLang.isGreek(ctx);

            String title = gr
                    ? "Mini Έλεγχος"
                    : "Mini Check";

            String body = gr
                    ? "Εντοπίστηκε υψηλή χρήση cache.\nΠάτησε για προτάσεις."
                    : "High cache usage detected.\nTap for recommendations.";

            try {
                NotificationCompat.Builder nb =
                        new NotificationCompat.Builder(ctx, "gel_default")
                                .setSmallIcon(android.R.drawable.stat_notify_more)
                                .setContentTitle(title)
                                .setContentText(body)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true);

                NotificationManagerCompat.from(ctx)
                        .notify(19001, nb.build());

            } catch (Throwable ignore) {}
        }

        // -------------------------------------------------
        // 🔁 Self reschedule for same hour tomorrow
        // -------------------------------------------------
        int hour = getInputData().getInt("hour", -1);
        String workName = getInputData().getString("workName");

        if (hour != -1 && workName != null) {
            OptimizerMiniPulseScheduler.reschedule(ctx, hour, workName);
        }

        return Result.success();
    }
}
