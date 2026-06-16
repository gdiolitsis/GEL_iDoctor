// GDiolitsis Engine Lab (GEL) — Author & Developer
// GelDeviceMonitorWidget.java — LIGHT + TEMPORARY LIVE MODE

package com.gel.cleaner;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

public final class GelDeviceMonitorWidget extends AppWidgetProvider {

    public static final String ACTION_REFRESH =
            "com.gel.cleaner.ACTION_GEL_WIDGET_REFRESH";

    public static final String ACTION_TOGGLE_LIVE =
            "com.gel.cleaner.ACTION_GEL_WIDGET_TOGGLE_LIVE";

    public static final String PREFS =
            "gel_widget_live_prefs";

    public static final String KEY_LIVE_ACTIVE =
            "live_active";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null) {
            super.onReceive(context, null);
            return;
        }

        String action = intent.getAction();

        if (ACTION_TOGGLE_LIVE.equals(action)) {

            if (isLiveActive(context)) {
                stopLiveMode(context);
            } else {
                startLiveMode(context);
            }

            return;
        }

        if (ACTION_REFRESH.equals(action)
                || AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)
                || AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(action)) {

            final PendingResult pendingResult = goAsync();
            final Context appContext = context.getApplicationContext();

            new Thread(() -> {
                try {
                    updateAllWidgets(appContext);
                } finally {
                    pendingResult.finish();
                }
            }, "GEL-Widget-Refresh").start();

            return;
        }

        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)
                || AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)) {

            if (!hasAnyWidgets(context)) {
                stopLiveMode(context);
            }
        }

        super.onReceive(context, intent);
    }

    public static boolean isLiveActive(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_LIVE_ACTIVE, false);
    }

    public static void setLiveActive(
            Context context,
            boolean active
    ) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_LIVE_ACTIVE, active)
                .apply();
    }

    private static boolean hasAnyWidgets(Context context) {

        AppWidgetManager manager =
                AppWidgetManager.getInstance(context);

        ComponentName provider =
                new ComponentName(
                        context,
                        GelDeviceMonitorWidget.class
                );

        int[] ids = manager.getAppWidgetIds(provider);

        return ids != null && ids.length > 0;
    }

    private static void startLiveMode(Context context) {

        try {

            Intent serviceIntent =
                    new Intent(
                            context,
                            GelWidgetLiveService.class
                    );

            serviceIntent.setAction(
                    GelWidgetLiveService.ACTION_START_LIVE
            );

            ContextCompat.startForegroundService(
                    context,
                    serviceIntent
            );

        } catch (Throwable ignore) {

            setLiveActive(context, false);
            updateAllWidgets(context);
        }
    }

    private static void stopLiveMode(Context context) {

        try {

            Intent serviceIntent =
                    new Intent(
                            context,
                            GelWidgetLiveService.class
                    );

            serviceIntent.setAction(
                    GelWidgetLiveService.ACTION_STOP_LIVE
            );

            context.startService(serviceIntent);

        } catch (Throwable ignore) {

            try {
                context.stopService(
                        new Intent(
                                context,
                                GelWidgetLiveService.class
                        )
                );
            } catch (Throwable ignoredAgain) {}
        }

        setLiveActive(context, false);
        updateAllWidgets(context);
    }

    public static void updateAllWidgets(Context context) {

        GelWidgetMetrics.Snapshot snapshot =
                GelWidgetMetrics.capture(context);

        updateAllWidgets(
                context,
                snapshot,
                isLiveActive(context)
        );
    }

    public static void updateAllWidgets(
            Context context,
            GelWidgetMetrics.Snapshot snapshot,
            boolean liveActive
    ) {

        AppWidgetManager manager =
                AppWidgetManager.getInstance(context);

        ComponentName provider =
                new ComponentName(
                        context,
                        GelDeviceMonitorWidget.class
                );

        int[] ids = manager.getAppWidgetIds(provider);

        if (ids == null || ids.length == 0) {
            return;
        }

        for (int id : ids) {
            updateWidget(
                    context,
                    manager,
                    id,
                    snapshot,
                    liveActive
            );
        }
    }

    public static void updateLiveCpuRam(
            Context context,
            GelWidgetMetrics.CpuRamSnapshot cpuRam,
            GelWidgetMetrics.Snapshot staticSnapshot
    ) {

        if (cpuRam == null || staticSnapshot == null) {
            return;
        }

        GelWidgetMetrics.Snapshot merged =
                new GelWidgetMetrics.Snapshot(
                        cpuRam.cpu,
                        cpuRam.ram,
                        staticSnapshot.battery,
                        staticSnapshot.thermal,
                        staticSnapshot.storage,
                        cpuRam.updated
                );

        updateAllWidgets(
                context,
                merged,
                true
        );
    }

    private static void updateWidget(
            Context context,
            AppWidgetManager manager,
            int appWidgetId,
            GelWidgetMetrics.Snapshot snapshot,
            boolean liveActive
    ) {

        RemoteViews views =
                new RemoteViews(
                        context.getPackageName(),
                        R.layout.widget_gel_device_monitor
                );

        boolean gr = AppLang.isGreek(context);

        views.setTextViewText(
                R.id.widget_cpu_value,
                snapshot.cpu
        );

        views.setTextViewText(
                R.id.widget_ram_value,
                snapshot.ram
        );

        views.setTextViewText(
                R.id.widget_battery_label,
                gr ? "ΜΠΑΤΑΡΙΑ  " : "BATTERY  "
        );

        views.setTextViewText(
                R.id.widget_battery_value,
                snapshot.battery
        );

        views.setTextViewText(
                R.id.widget_thermal_label,
                gr ? "ΘΕΡΜΙΚΟ  " : "THERMAL  "
        );

        views.setTextViewText(
                R.id.widget_thermal_value,
                snapshot.thermal
        );

        views.setTextViewText(
                R.id.widget_storage_label,
                gr ? "ΕΛΕΥΘΕΡΟΣ ΧΩΡΟΣ  " : "STORAGE FREE  "
        );

        views.setTextViewText(
                R.id.widget_storage_value,
                snapshot.storage
        );

        views.setTextViewText(
                R.id.widget_updated,
                liveActive
                        ? ((gr ? "LIVE: " : "LIVE: ") + snapshot.updated)
                        : ((gr ? "Μέτρηση: " : "Updated: ") + snapshot.updated)
        );

        views.setTextViewText(
                R.id.widget_live,
                liveActive
                        ? (gr ? "■ STOP LIVE" : "■ STOP LIVE")
                        : (gr ? "● LIVE 3′" : "● LIVE 3′")
        );

        views.setTextViewText(
                R.id.widget_refresh,
                gr ? "↻ ΑΝΑΝΕΩΣΗ" : "↻ REFRESH"
        );

        Intent liveIntent =
                new Intent(
                        context,
                        GelDeviceMonitorWidget.class
                );

        liveIntent.setAction(ACTION_TOGGLE_LIVE);

        PendingIntent livePending =
                PendingIntent.getBroadcast(
                        context,
                        82000,
                        liveIntent,
                        pendingFlags()
                );

        views.setOnClickPendingIntent(
                R.id.widget_live,
                livePending
        );

        Intent refreshIntent =
                new Intent(
                        context,
                        GelDeviceMonitorWidget.class
                );

        refreshIntent.setAction(ACTION_REFRESH);

        PendingIntent refreshPending =
                PendingIntent.getBroadcast(
                        context,
                        82001,
                        refreshIntent,
                        pendingFlags()
                );

        views.setOnClickPendingIntent(
                R.id.widget_refresh,
                refreshPending
        );

        Intent openIntent =
                new Intent(
                        context,
                        CpuRamLiveActivity.class
                );

        openIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent openPending =
                PendingIntent.getActivity(
                        context,
                        82002,
                        openIntent,
                        pendingFlags()
                );

        views.setOnClickPendingIntent(
                R.id.widget_root,
                openPending
        );

        manager.updateAppWidget(
                appWidgetId,
                views
        );
    }

    private static int pendingFlags() {

        int flags =
                PendingIntent.FLAG_UPDATE_CURRENT;

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        return flags;
    }
}
