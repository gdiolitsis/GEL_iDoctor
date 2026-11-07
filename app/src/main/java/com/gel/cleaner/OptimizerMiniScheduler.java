package com.gel.cleaner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.format.Formatter;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/** Periodic snapshots, never continuous surveillance or third-party crash detection. */
public class OptimizerMiniScheduler extends Worker {
    public OptimizerMiniScheduler(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull @Override public Result doWork() {
        Context ctx = getApplicationContext();
        SharedPreferences sp = ctx.getSharedPreferences("gel_prefs", Context.MODE_PRIVATE);
        if (!sp.getBoolean("pulse_enabled", false)) return Result.success();
        GelMiniSnapshot s = GelMiniSnapshot.read(ctx);
        long now = System.currentTimeMillis();
        SharedPreferences.Editor edit = sp.edit().putLong("mini_last_attempt", now)
                .putBoolean("mini_last_complete", s.complete());
        if (s.complete()) edit.putLong("mini_last_success", now);
        edit.apply();
        boolean gr = AppLang.isGreek(ctx);
        check(ctx, sp, "temperature", Double.isFinite(s.temperature), GelMiniPolicy.temperatureLevel(s.temperature),
                gr ? "Θερμοκρασία μπαταρίας: " + s.temperature + "°C. Διακόψτε βαριές εργασίες. Αν φορτίζει, αποσυνδέστε τον φορτιστή και αφήστε τη συσκευή να κρυώσει."
                   : "Battery temperature: " + s.temperature + "°C. Stop demanding tasks. If charging, unplug and let the device cool.", gr, now, 19101);
        check(ctx, sp, "ram", s.ramKnown, s.lowMemory ? 1 : 0,
                gr ? "Το Android αναφέρει χαμηλή διαθέσιμη RAM (" + Formatter.formatFileSize(ctx, s.availableRam) + "). Αυτό μπορεί να επηρεάζει την ανταπόκριση. Αποθηκεύστε την εργασία σας και κλείστε εφαρμογές ή καρτέλες που δεν χρειάζεστε."
                   : "Android reports low available RAM (" + Formatter.formatFileSize(ctx, s.availableRam) + "). This may affect responsiveness. Save your work and close apps or tabs you do not need.", gr, now, 19102);
        check(ctx, sp, "storage", s.storageKnown, GelMiniPolicy.storageLevel(s.availableStorage, s.totalStorage),
                gr ? "Περιορισμένος διαθέσιμος εσωτερικός χώρος: " + Formatter.formatFileSize(ctx, s.availableStorage) + ". Ελέγξτε Downloads, μεγάλα αρχεία και εφαρμογές που δεν χρησιμοποιείτε. Κρατήστε αντίγραφα πριν διαγράψετε αρχεία."
                   : "Low available internal storage: " + Formatter.formatFileSize(ctx, s.availableStorage) + ". Review Downloads, large files and unused apps. Back up files before deleting them.", gr, now, 19103);
        check(ctx, sp, "health", s.health != android.os.BatteryManager.BATTERY_HEALTH_UNKNOWN,
                s.healthLevel(), gr ? "Το Android αναφέρει προειδοποίηση μπαταρίας: " + healthText(s.health, true) + ". Ελέγξτε τη μπαταρία στις Ρυθμίσεις. Αν επιμένει, απευθυνθείτε σε τεχνικό."
                    : "Android reports a battery health warning: " + healthText(s.health, false) + ". Check Battery in Settings. If it persists, contact a technician.", gr, now, 19104);
        return Result.success();
    }

    private static String healthText(int health, boolean gr) {
        switch (health) {
            case android.os.BatteryManager.BATTERY_HEALTH_OVERHEAT: return gr ? "υπερθέρμανση" : "overheating";
            case android.os.BatteryManager.BATTERY_HEALTH_COLD: return gr ? "πολύ χαμηλή θερμοκρασία" : "too cold";
            case android.os.BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: return gr ? "υπέρταση" : "overvoltage";
            case android.os.BatteryManager.BATTERY_HEALTH_DEAD: return gr ? "αστοχία" : "battery failure";
            default: return gr ? "μη καθορισμένη αστοχία" : "unspecified failure";
        }
    }

    private static synchronized void check(Context ctx, SharedPreferences sp, String key, boolean known, int level,
                       String body, boolean gr, long now, int id) {
        if (!known) return; // Missing data never clears a warning or counts as a healthy reading.
        String prefix = "mini_v2_" + key;
        if (level == 0) {
            sp.edit().putInt(prefix + "_level", 0).apply();
            NotificationManagerCompat.from(ctx).cancel(id);
            return;
        }
        if (!GelMiniPolicy.shouldNotify(level, sp.getInt(prefix + "_level", 0),
                sp.getLong(prefix + "_notified", 0), now)) return;
        try {
            NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null || !NotificationManagerCompat.from(ctx).areNotificationsEnabled()) return;
            if (Build.VERSION.SDK_INT >= 26) {
                manager.createNotificationChannel(new NotificationChannel("gel_mini_health",
                        gr ? "Mini Check — Υγεία συσκευής" : "Mini Check — Device health", NotificationManager.IMPORTANCE_DEFAULT));
                if (manager.getNotificationChannel("gel_mini_health").getImportance() == NotificationManager.IMPORTANCE_NONE) return;
            }
            if (!sp.getBoolean("pulse_enabled", false)) return;
            PendingIntent open = PendingIntent.getActivity(ctx, id,
                    new Intent(ctx, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            NotificationManagerCompat.from(ctx).notify(id, new NotificationCompat.Builder(ctx, "gel_mini_health")
                    .setSmallIcon(android.R.drawable.stat_notify_more)
                    .setContentTitle(gr ? "GEL iDoctor: Ένδειξη Mini Check" : "GEL iDoctor: Mini Check finding")
                    .setContentText(body).setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setContentIntent(open).setAutoCancel(true).build());
            sp.edit().putInt(prefix + "_level", level).putLong(prefix + "_notified", now).apply();
        } catch (RuntimeException ignored) {
            // Permission denied: do not consume the cooldown.
        }
    }
}
