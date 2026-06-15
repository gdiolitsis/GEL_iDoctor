// GDiolitsis Engine Lab (GEL) — Author & Developer
// GelDeviceMonitorWidget.java — Lightweight Manual Refresh Widget

package com.gel.cleaner;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

public final class GelDeviceMonitorWidget extends AppWidgetProvider {

    public static final String ACTION_REFRESH =
            "com.gel.cleaner.ACTION_GEL_WIDGET_REFRESH";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {

            String action = intent.getAction();

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
        }

        super.onReceive(context, intent);
    }

    public static void updateAllWidgets(Context context) {

        AppWidgetManager manager =
                AppWidgetManager.getInstance(context);

        ComponentName provider =
                new ComponentName(
                        context,
                        GelDeviceMonitorWidget.class
                );

        int[] ids = manager.getAppWidgetIds(provider);

        if (ids == null || ids.length == 0) return;

        GelWidgetMetrics.Snapshot snapshot =
                GelWidgetMetrics.capture(context);

        for (int id : ids) {
            updateWidget(context, manager, id, snapshot);
        }
    }

    private static void updateWidget(
            Context context,
            AppWidgetManager manager,
            int appWidgetId,
            GelWidgetMetrics.Snapshot snapshot
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
                (gr ? "Μέτρηση: " : "Updated: ") + snapshot.updated
        );

        views.setTextViewText(
                R.id.widget_refresh,
                gr ? "↻ ΑΝΑΝΕΩΣΗ" : "↻ REFRESH"
        );

        Intent refreshIntent =
                new Intent(context, GelDeviceMonitorWidget.class);

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
                new Intent(context, CpuRamLiveActivity.class);

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

        manager.updateAppWidget(appWidgetId, views);
    }

    private static int pendingFlags() {

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        return flags;
    }
}
