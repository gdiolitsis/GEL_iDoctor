// GDiolitsis Engine Lab (GEL) — v21.1 FINAL
// CPU/RAM LIVE — Shared CpuStatBridge Edition

package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CpuRamLiveActivity extends AppCompatActivity {

    private TextView txtLive;
    private volatile boolean running = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        UIHelpers.applyPressEffectRecursive(
                getWindow().getDecorView()
        );

        txtLive = findViewById(R.id.txtLiveInfo);

        Button btnCore = findViewById(R.id.btnCoreMonitor);

        btnCore.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                CoreMonitorActivity.class
                        )
                )
        );

        startLoop();
    }

    @Override
    protected void onDestroy() {
        running = false;
        super.onDestroy();
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
