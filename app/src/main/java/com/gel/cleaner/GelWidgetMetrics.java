// GDiolitsis Engine Lab (GEL) — Author & Developer
// GelWidgetMetrics.java — One-shot lightweight measurement layer

package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class GelWidgetMetrics {

    private GelWidgetMetrics() {}

    public static final class Snapshot {

        public final String cpu;
        public final String ram;
        public final String battery;
        public final String thermal;
        public final String storage;
        public final String updated;

        Snapshot(
                String cpu,
                String ram,
                String battery,
                String thermal,
                String storage,
                String updated
        ) {
            this.cpu = cpu;
            this.ram = ram;
            this.battery = battery;
            this.thermal = thermal;
            this.storage = storage;
            this.updated = updated;
        }
    }

    public static Snapshot capture(Context context) {

        Context app = context.getApplicationContext();

        String cpu = readCpuUsage();
        String ram = readRam(app);
        String battery = readBattery(app);
        String thermal = readPreferredThermal();
        String storage = readStorage();

        String updated =
                new SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                ).format(new Date());

        return new Snapshot(
                cpu,
                ram,
                battery,
                thermal,
                storage,
                updated
        );
    }

    // ============================================================
    // CPU — SAME NATIVE ENGINE AS CpuRamLiveActivity
    // ============================================================
    // ============================================================
    // CPU — SHARED NATIVE ENGINE
    // ============================================================
    private static String readCpuUsage() {

        int percent =
                CpuStatBridge.readCpuPercent();

        return percent >= 0
                ? percent + "%"
                : "N/A";
    }

    private static String readRam(Context context) {

        try {

            ActivityManager manager =
                    (ActivityManager) context.getSystemService(
                            Context.ACTIVITY_SERVICE
                    );

            if (manager == null) return "N/A";

            ActivityManager.MemoryInfo info =
                    new ActivityManager.MemoryInfo();

            manager.getMemoryInfo(info);

            if (info.totalMem <= 0L) return "N/A";

            long used = info.totalMem - info.availMem;

            double percent =
                    100d * (double) used
                            / (double) info.totalMem;

            percent = Math.max(0d, Math.min(100d, percent));

            return Math.round(percent) + "%";

        } catch (Throwable ignore) {
            return "N/A";
        }
    }

    private static String readBattery(Context context) {

        try {

            Intent battery =
                    context.registerReceiver(
                            null,
                            new IntentFilter(
                                    Intent.ACTION_BATTERY_CHANGED
                            )
                    );

            if (battery == null) return "N/A";

            int level =
                    battery.getIntExtra(
                            BatteryManager.EXTRA_LEVEL,
                            -1
                    );

            int scale =
                    battery.getIntExtra(
                            BatteryManager.EXTRA_SCALE,
                            100
                    );

            int tempRaw =
                    battery.getIntExtra(
                            BatteryManager.EXTRA_TEMPERATURE,
                            Integer.MIN_VALUE
                    );

            String levelText = "N/A";

            if (level >= 0 && scale > 0) {
                int percent =
                        Math.round(
                                100f * (float) level
                                        / (float) scale
                        );
                levelText = percent + "%";
            }

            String tempText = "N/A";

            if (tempRaw != Integer.MIN_VALUE) {

                double c = tempRaw / 10d;

                if (c > -20d && c < 100d) {
                    tempText =
                            String.format(
                                    Locale.US,
                                    "%.1f°C",
                                    c
                            );
                }
            }

            return levelText + " · " + tempText;

        } catch (Throwable ignore) {
            return "N/A";
        }
    }

    private static String readPreferredThermal() {

        File root = new File("/sys/class/thermal");

        File[] zones =
                root.listFiles(
                        file ->
                                file != null
                                        && file.getName().startsWith(
                                                "thermal_zone"
                                        )
                );

        if (zones == null || zones.length == 0) {
            return "N/A";
        }

        double bestTemp = Double.NaN;
        int bestPriority = Integer.MIN_VALUE;

        for (File zone : zones) {

            try {

                String type =
                        readSmallText(
                                new File(zone, "type")
                        ).toLowerCase(Locale.US);

                if (type.isEmpty()) continue;

                if (type.contains("battery")
                        || type.contains("batt")
                        || type.contains("skin")
                        || type.contains("charger")
                        || type.contains("usb")
                        || type.contains("gpu")) {
                    continue;
                }

                int priority = thermalPriority(type);
                if (priority < 0) continue;

                String raw =
                        readSmallText(
                                new File(zone, "temp")
                        );

                if (raw.isEmpty()) continue;

                double value = Double.parseDouble(raw.trim());

                if (Math.abs(value) > 1000d) value /= 1000d;
                else if (Math.abs(value) > 200d) value /= 10d;

                if (value < 0d || value > 125d) continue;

                if (priority > bestPriority) {
                    bestPriority = priority;
                    bestTemp = value;
                }

            } catch (Throwable ignore) {}
        }

        if (!Double.isFinite(bestTemp)) return "N/A";

        return String.format(
                Locale.US,
                "%.1f°C",
                bestTemp
        );
    }

    private static int thermalPriority(String type) {

        if (type.contains("cpu")) return 100;
        if (type.contains("soc")) return 95;
        if (type.contains("ap_thermal")) return 90;
        if (type.contains("ap-thermal")) return 90;
        if (type.contains("apthermal")) return 90;
        if (type.contains("tsens")) return 80;
        if (type.contains("mtktscpu")) return 100;
        if (type.contains("cluster")) return 75;
        if (type.contains("big")) return 70;
        if (type.contains("little")) return 65;

        return -1;
    }

    private static String readSmallText(File file) {

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(file)
                     )) {

            String value = reader.readLine();

            return value == null ? "" : value.trim();

        } catch (Throwable ignore) {
            return "";
        }
    }

    private static String readStorage() {

        try {

            StatFs stat =
                    new StatFs(
                            Environment
                                    .getDataDirectory()
                                    .getAbsolutePath()
                    );

            long bytes = stat.getAvailableBytes();

            if (bytes < 0L) return "N/A";

            double gb =
                    bytes / 1024d / 1024d / 1024d;

            if (gb >= 10d) {
                return Math.round(gb) + " GB";
            }

            return String.format(
                    Locale.US,
                    "%.1f GB",
                    gb
            );

        } catch (Throwable ignore) {
            return "N/A";
        }
    }
}
