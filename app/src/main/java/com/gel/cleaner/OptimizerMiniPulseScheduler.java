package com.gel.cleaner;

import android.content.Context;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class OptimizerMiniPulseScheduler {
    public static void enable(Context context) {
        androidx.core.app.NotificationManagerCompat.from(context).cancel(19001);
        WorkManager wm = WorkManager.getInstance(context.getApplicationContext());
        for (int hour : new int[]{9,15,21}) {
            wm.cancelUniqueWork(String.format(java.util.Locale.ROOT, "mini_pulse_%02d", hour));
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime next = now.withHour(hour).withMinute(0).withSecond(0).withNano(0);
            if (!next.isAfter(now)) next = next.plusDays(1);
            PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                    OptimizerMiniScheduler.class, 24, TimeUnit.HOURS)
                    .setInitialDelay(Duration.between(now, next).toMillis(), TimeUnit.MILLISECONDS).build();
            // KEEP preserves existing execution history when MainActivity reopens.
            wm.enqueueUniquePeriodicWork("mini_health_v2_" + hour, ExistingPeriodicWorkPolicy.KEEP, request);
        }
    }
    public static void disable(Context context) {
        WorkManager wm = WorkManager.getInstance(context.getApplicationContext());
        for (int hour : new int[]{9,15,21}) {
            wm.cancelUniqueWork(String.format(java.util.Locale.ROOT, "mini_pulse_%02d", hour));
            wm.cancelUniqueWork("mini_health_v2_" + hour);
        }
        for (int id = 19101; id <= 19104; id++) androidx.core.app.NotificationManagerCompat.from(context).cancel(id);
    }
    /** Compatibility for old callers; migrate to periodic work. */
    public static void reschedule(Context context, int hour, String workName) { enable(context); }
}
