// GDiolitsis Engine Lab (GEL) — Author & Developer
// GelWidgetLiveService.java — Temporary 3-minute LIVE CPU/RAM monitor

package com.gel.cleaner;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public final class GelWidgetLiveService extends Service {

    public static final String ACTION_START_LIVE =
            "com.gel.cleaner.ACTION_START_WIDGET_LIVE";

    public static final String ACTION_STOP_LIVE =
            "com.gel.cleaner.ACTION_STOP_WIDGET_LIVE";

    private static final String CHANNEL_ID =
            "gel_widget_live";

    private static final int NOTIFICATION_ID =
            82010;

    private static final long LIVE_DURATION_MS =
            3L * 60L * 1000L;

    private static final long CPU_RAM_INTERVAL_MS =
            2000L;

    private static final long STATIC_INTERVAL_MS =
            10000L;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private long liveStartedAt = 0L;
    private long lastStaticRefreshAt = 0L;

    private boolean running = false;

    private GelWidgetMetrics.Snapshot staticSnapshot;

    private final Runnable liveTick =
            new Runnable() {

                @Override
                public void run() {

                    if (!running) {
                        return;
                    }

                    long now =
                            SystemClock.elapsedRealtime();

                    if (now - liveStartedAt
                            >= LIVE_DURATION_MS) {

                        stopLiveMode();
                        return;
                    }

                    try {

                        if (staticSnapshot == null
                                || now - lastStaticRefreshAt
                                >= STATIC_INTERVAL_MS) {

                            staticSnapshot =
                                    GelWidgetMetrics.capture(
                                            GelWidgetLiveService.this
                                    );

                            lastStaticRefreshAt = now;
                        }

                        GelWidgetMetrics.CpuRamSnapshot cpuRam =
                                GelWidgetMetrics.captureCpuRam(
                                        GelWidgetLiveService.this
                                );

                        GelDeviceMonitorWidget.updateLiveCpuRam(
                                GelWidgetLiveService.this,
                                cpuRam,
                                staticSnapshot
                        );

                        updateNotification(cpuRam);

                    } catch (Throwable ignore) {}

                    handler.postDelayed(
                            this,
                            CPU_RAM_INTERVAL_MS
                    );
                }
            };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId
    ) {

        String action =
                intent != null
                        ? intent.getAction()
                        : null;

        if (ACTION_STOP_LIVE.equals(action)) {

            stopLiveMode();
            return START_NOT_STICKY;
        }

        startLiveMode();

        return START_NOT_STICKY;
    }

    private void startLiveMode() {

        if (running) {
            return;
        }

        running = true;
        liveStartedAt =
                SystemClock.elapsedRealtime();

        lastStaticRefreshAt = 0L;
        staticSnapshot = null;

        GelDeviceMonitorWidget.setLiveActive(
                this,
                true
        );

        Notification notification =
                buildNotification(
                        null,
                        true
                );

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.Q) {

            startForeground(
                    NOTIFICATION_ID,
                    notification,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                            ? ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
                            : 0
            );

        } else {

            startForeground(
                    NOTIFICATION_ID,
                    notification
            );
        }

        handler.removeCallbacks(liveTick);
        handler.post(liveTick);
    }

    private void stopLiveMode() {

        running = false;

        handler.removeCallbacksAndMessages(null);

        GelDeviceMonitorWidget.setLiveActive(
                this,
                false
        );

        try {
            GelDeviceMonitorWidget.updateAllWidgets(this);
        } catch (Throwable ignore) {}

        stopForeground(true);
        stopSelf();
    }

    private void updateNotification(
            GelWidgetMetrics.CpuRamSnapshot snapshot
    ) {

        NotificationManager manager =
                (NotificationManager) getSystemService(
                        NOTIFICATION_SERVICE
                );

        if (manager == null) {
            return;
        }

        manager.notify(
                NOTIFICATION_ID,
                buildNotification(
                        snapshot,
                        false
                )
        );
    }

    private Notification buildNotification(
            GelWidgetMetrics.CpuRamSnapshot snapshot,
            boolean starting
    ) {

        boolean gr =
                AppLang.isGreek(this);

        String content;

        if (starting || snapshot == null) {

            content = gr
                    ? "Εκκίνηση προσωρινής ζωντανής παρακολούθησης..."
                    : "Starting temporary live monitoring...";

        } else {

            content =
                    "CPU "
                            + snapshot.cpu
                            + "  ·  RAM "
                            + snapshot.ram;
        }

        Intent openIntent =
                new Intent(
                        this,
                        CpuRamLiveActivity.class
                );

        PendingIntent openPending =
                PendingIntent.getActivity(
                        this,
                        82011,
                        openIntent,
                        pendingFlags()
                );

        Intent stopIntent =
                new Intent(
                        this,
                        GelWidgetLiveService.class
                );

        stopIntent.setAction(
                ACTION_STOP_LIVE
        );

        PendingIntent stopPending =
                PendingIntent.getService(
                        this,
                        82012,
                        stopIntent,
                        pendingFlags()
                );

        return new NotificationCompat.Builder(
                this,
                CHANNEL_ID
        )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(
                        gr
                                ? "GEL Widget — LIVE CPU/RAM"
                                : "GEL Widget — LIVE CPU/RAM"
                )
                .setContentText(content)
                .setContentIntent(openPending)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setCategory(
                        NotificationCompat.CATEGORY_SERVICE
                )
                .setPriority(
                        NotificationCompat.PRIORITY_LOW
                )
                .addAction(
                        0,
                        gr ? "ΔΙΑΚΟΠΗ LIVE" : "STOP LIVE",
                        stopPending
                )
                .build();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT
                < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "GEL Widget Live Monitor",
                        NotificationManager.IMPORTANCE_LOW
                );

        channel.setDescription(
                "Temporary live CPU and RAM widget monitoring"
        );

        channel.setSound(null, null);
        channel.enableVibration(false);

        NotificationManager manager =
                getSystemService(
                        NotificationManager.class
                );

        if (manager != null) {
            manager.createNotificationChannel(
                    channel
            );
        }
    }

    private int pendingFlags() {

        int flags =
                PendingIntent.FLAG_UPDATE_CURRENT;

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        return flags;
    }

    @Override
    public void onTimeout(
            int startId,
            int foregroundServiceType
    ) {

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            stopLiveMode();
        }
    }

    @Override
    public void onDestroy() {

        running = false;
        handler.removeCallbacksAndMessages(null);

        GelDeviceMonitorWidget.setLiveActive(
                this,
                false
        );

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
