// GDiolitsis Engine Lab (GEL) — v21.1 FINAL
// CPU/RAM LIVE — Shared CpuStatBridge Edition

package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import androidx.annotation.Nullable;

public class CpuRamLiveActivity extends GELAutoActivityHook {

    private TextView txtLive;
    private volatile boolean running = true;
    private boolean remoteMode = false;
    private boolean remoteInFlight = false;
    private int remoteCounter = 1;
    private String latestRemoteCoreText = null;
    private final Handler remoteHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        UIHelpers.applyPressEffectRecursive(
                getWindow().getDecorView()
        );

        remoteMode = GELRemoteTargetManager.isRemoteMode(this);

        txtLive = findViewById(R.id.txtLiveInfo);

        Button btnCore = findViewById(R.id.btnCoreMonitor);

        btnCore.setOnClickListener(v -> {
            if (remoteMode) {
                // Do not accidentally open CoreMonitorActivity locally because
                // that would show the technician CPU. CoreMonitorActivity will
                // be made remote-aware in the next step.
                Toast.makeText(
                        this,
                        AppLang.isGreek(this)
                                ? "Το GEL Cores Monitor χρειάζεται το δικό του remote update. Τα CPU/RAM δεδομένα που βλέπετε εδώ είναι ήδη του πελάτη."
                                : "GEL Cores Monitor needs its own remote update. The CPU/RAM data on this screen already belongs to the customer.",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            startActivity(
                    new Intent(
                            this,
                            CoreMonitorActivity.class
                    )
            );
        });

        if (remoteMode) {
            startRemoteLoop();
        } else {
            startLoop();
        }
    }

    @Override
    protected void onDestroy() {
        running = false;
        remoteHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    // ============================================================
    // REMOTE CPU / RAM LIVE
    // ============================================================
    private void startRemoteLoop() {
        txtLive.setText(
                AppLang.isGreek(this)
                        ? "Σύνδεση με CPU/RAM της συσκευής πελάτη..."
                        : "Connecting to customer CPU/RAM..."
        );
        remoteHandler.post(this::requestRemoteSnapshot);
    }

    private void requestRemoteSnapshot() {
        if (!running || !remoteMode) return;
        if (remoteInFlight) return;

        remoteInFlight = true;

        GELRemoteCommandClient.send(
                this,
                "CPU_RAM_SNAPSHOT",
                null,
                new GELRemoteCommandClient.Callback() {
                    @Override
                    public void onCompleted(
                            boolean success,
                            Map<String, Object> result,
                            String message
                    ) {
                        remoteInFlight = false;

                        if (!running || !remoteMode) return;

                        if (success) {
                            renderRemoteSnapshot(result);
                        } else {
                            String error = message != null && !message.trim().isEmpty()
                                    ? message
                                    : (AppLang.isGreek(CpuRamLiveActivity.this)
                                        ? "Αποτυχία remote CPU/RAM μέτρησης."
                                        : "Remote CPU/RAM measurement failed.");
                            txtLive.setText(error);
                        }

                        // Never overlap commands. The next request starts only
                        // after the previous one reached a terminal state.
                        remoteHandler.postDelayed(
                                CpuRamLiveActivity.this::requestRemoteSnapshot,
                                1400L
                        );
                    }
                }
        );
    }

    private void renderRemoteSnapshot(Map<String, Object> result) {
        long totalBytes = asLong(result, "ramTotalBytes", -1L);
        long availableBytes = asLong(result, "ramAvailableBytes", -1L);
        long usedMb = (totalBytes > 0 && availableBytes >= 0)
                ? Math.max(0L, totalBytes - availableBytes) / (1024L * 1024L)
                : -1L;
        long totalMb = totalBytes > 0 ? totalBytes / (1024L * 1024L) : -1L;

        int cpuPercent = (int) asLong(result, "cpuPercent", -1L);
        double tempC = asDouble(result, "temperatureC", Double.NaN);

        String cpuText = cpuPercent >= 0 ? cpuPercent + "%" : "N/A";
        String tempText = !Double.isNaN(tempC)
                ? String.format(java.util.Locale.US, "%.1f°C", tempC)
                : "N/A";
        String ramText = usedMb >= 0 && totalMb > 0
                ? usedMb + " / " + totalMb + " MB"
                : "N/A";

        String html =
                "Live " + remoteCounter +
                "<br><br>CPU: <font color='#00FF66'>" + cpuText + "</font>" +
                "<br>TEMP: <font color='#00FF66'>" + tempText + "</font>" +
                "<br>RAM: <font color='#00FF66'>" + ramText + "</font>";

        txtLive.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));

        Object cores = result != null ? result.get("coreFrequenciesMHz") : null;
        latestRemoteCoreText = cores != null ? String.valueOf(cores) : null;

        remoteCounter++;
        if (remoteCounter > 999) remoteCounter = 1;
    }

    private long asLong(Map<String, Object> map, String key, long fallback) {
        if (map == null) return fallback;
        Object raw = map.get(key);
        return raw instanceof Number ? ((Number) raw).longValue() : fallback;
    }

    private double asDouble(Map<String, Object> map, String key, double fallback) {
        if (map == null) return fallback;
        Object raw = map.get(key);
        return raw instanceof Number ? ((Number) raw).doubleValue() : fallback;
    }

    private void startLoop() {

        new Thread(() -> {

            int counter = 1;

            while (running) {

                int cpuPercent =
                        CpuStatBridge.readCpuPercent();

                String cpuText =
                        cpuPercent >= 0
                                ? cpuPercent + "%"
                                : "N/A";

                String cpu =
                        "<font color='#00FF66'>"
                                + cpuText
                                + "</font>";

                String temp =
                        "<font color='#00FF66'>"
                                + readCpuTemp()
                                + "</font>";

                String ramRaw = readRamUsage();

                String ram;

                if ("N/A".equals(ramRaw)) {

                    ram =
                            "<font color='#00FF66'>N/A</font>";

                } else {

                    String[] parts =
                            ramRaw.split(" ");

                    if (parts.length >= 3) {

                        String used =
                                "<font color='#00FF66'>"
                                        + parts[0]
                                        + "</font>";

                        ram =
                                used
                                        + " "
                                        + parts[1]
                                        + " "
                                        + parts[2];

                    } else {

                        ram =
                                "<font color='#00FF66'>"
                                        + ramRaw
                                        + "</font>";
                    }
                }

                String html =
                        "Live "
                                + counter
                                + "<br><br>"
                                + "CPU: "
                                + cpu
                                + "<br>"
                                + "TEMP: "
                                + temp
                                + "<br>"
                                + "RAM: "
                                + ram;

                runOnUiThread(() ->
                        txtLive.setText(
                                Html.fromHtml(
                                        html,
                                        Html.FROM_HTML_MODE_LEGACY
                                )
                        )
                );

                counter++;

                if (counter > 999) {
                    counter = 1;
                }

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

        }, "GEL-CPU-RAM-Live").start();
    }

    private String readCpuTemp() {

        try {

            Intent intent =
                    registerReceiver(
                            null,
                            new IntentFilter(
                                    Intent.ACTION_BATTERY_CHANGED
                            )
                    );

            if (intent == null) {
                return "N/A";
            }

            int rawTemperature =
                    intent.getIntExtra(
                            BatteryManager.EXTRA_TEMPERATURE,
                            -1
                    );

            if (rawTemperature > 0) {
                return (rawTemperature / 10f) + "°C";
            }

            return "N/A";

        } catch (Throwable ignore) {
            return "N/A";
        }
    }

    private String readRamUsage() {

        try {

            ActivityManager manager =
                    (ActivityManager) getSystemService(
                            Context.ACTIVITY_SERVICE
                    );

            if (manager == null) {
                return "N/A";
            }

            ActivityManager.MemoryInfo info =
                    new ActivityManager.MemoryInfo();

            manager.getMemoryInfo(info);

            long total =
                    info.totalMem
                            / (1024L * 1024L);

            long free =
                    info.availMem
                            / (1024L * 1024L);

            long used = total - free;

            return used
                    + " / "
                    + total
                    + " MB";

        } catch (Throwable ignore) {
            return "N/A";
        }
    }
}
