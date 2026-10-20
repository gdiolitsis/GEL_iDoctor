// GDiolitsis Engine Lab (GEL) — Author & Developer
// CpuStatBridge.java
//
// Pure-Java CPU Triple Engine.
// Replaces the former project-owned JNI/CMake cpustat library.
//
// Compatibility encoding:
//   RAW:     0–100
//   FREQ:    1000–1100
//   THERMAL: 2000–2100
//   FAIL:    -1

package com.gel.cleaner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;

public final class CpuStatBridge {

    private static long lastIdle = -1L;
    private static long lastTotal = -1L;

    private CpuStatBridge() {}

    // Kept under its historical name so callers remain source-compatible.
    public static synchronized int getCpuUsageNative() {

        int percent = readCpuRaw();

        if (percent >= 0) {
            return clamp(percent, 0, 100);
        }

        percent = readCpuFreq();

        if (percent >= 0) {
            return 1000 + clamp(percent, 0, 100);
        }

        percent = readCpuThermal();

        if (percent >= 0) {
            return 2000 + clamp(percent, 0, 100);
        }

        return -1;
    }

    public static int readCpuPercent() {

        try {

            int value = getCpuUsageNative();

            if (value >= 0 && value <= 100) {
                return value;
            }

            if (value >= 1000 && value <= 1100) {
                return value - 1000;
            }

            if (value >= 2000 && value <= 2100) {
                return value - 2000;
            }

            return -1;

        } catch (Throwable ignore) {
            return -1;
        }
    }

    // ============================================================
    // 1) /proc/stat
    // ============================================================

    private static int readCpuRaw() {

        String line = readFirstLine("/proc/stat");

        if (line == null) {
            return -1;
        }

        String[] parts =
                line.trim().split("\\s+");

        if (parts.length < 5
                || !"cpu".equals(parts[0])) {
            return -1;
        }

        try {

            long user = part(parts, 1);
            long nice = part(parts, 2);
            long system = part(parts, 3);
            long idle = part(parts, 4);
            long iowait = part(parts, 5);
            long irq = part(parts, 6);
            long softirq = part(parts, 7);

            long idleAll =
                    idle + iowait;

            long total =
                    user
                    + nice
                    + system
                    + idle
                    + iowait
                    + irq
                    + softirq;

            if (lastIdle < 0L
                    || lastTotal < 0L) {

                lastIdle = idleAll;
                lastTotal = total;

                return 0;
            }

            long diffIdle =
                    idleAll - lastIdle;

            long diffTotal =
                    total - lastTotal;

            lastIdle = idleAll;
            lastTotal = total;

            // Preserve the original anti-stale gate.
            if (diffTotal < 50L) {
                return -1;
            }

            if (diffTotal <= 0L) {
                return -1;
            }

            long used =
                    diffTotal - diffIdle;

            if (used < 0L) {
                used = 0L;
            }

            return clamp(
                    (int) ((used * 100L) / diffTotal),
                    0,
                    100
            );

        } catch (Throwable ignore) {
            return -1;
        }
    }

    // ============================================================
    // 2) cpufreq
    // ============================================================

    private static int readCpuFreq() {

        long accumulator = 0L;
        int valid = 0;

        for (int index = 0; index < 64; index++) {

            String base =
                    "/sys/devices/system/cpu/cpu"
                            + index
                            + "/cpufreq/";

            File dir = new File(base);

            if (!dir.exists()) {
                continue;
            }

            long current =
                    readLong(
                            base + "scaling_cur_freq"
                    );

            long maximum =
                    readLong(
                            base + "cpuinfo_max_freq"
                    );

            if (current <= 0L
                    || maximum <= 0L) {
                continue;
            }

            long value =
                    (current * 100L)
                            / maximum;

            accumulator +=
                    Math.max(
                            0L,
                            Math.min(
                                    100L,
                                    value
                            )
                    );

            valid++;
        }

        if (valid == 0) {
            return -1;
        }

        return clamp(
                (int) (accumulator / valid),
                0,
                100
        );
    }

    // ============================================================
    // 3) thermal fallback
    // ============================================================

    private static int readCpuThermal() {

        long temperature = -1L;

        for (int index = 0; index < 64; index++) {

            String base =
                    "/sys/class/thermal/thermal_zone"
                            + index
                            + "/";

            String type =
                    readFirstLine(
                            base + "type"
                    );

            if (type == null) {
                continue;
            }

            String normalized =
                    type.trim()
                            .toLowerCase(
                                    Locale.ROOT
                            );

            if (!(normalized.contains("cpu")
                    || normalized.contains("soc")
                    || normalized.contains("ap"))) {
                continue;
            }

            temperature =
                    readLong(
                            base + "temp"
                    );

            if (temperature > 0L) {
                break;
            }
        }

        if (temperature <= 0L) {
            return -1;
        }

        double celsius =
                temperature > 1000L
                        ? temperature / 1000.0
                        : temperature;

        double percent;

        if (celsius <= 30.0) {

            percent = 0.0;

        } else if (celsius >= 90.0) {

            percent = 100.0;

        } else {

            percent =
                    (celsius - 30.0)
                            * 100.0
                            / 60.0;
        }

        return clamp(
                (int) Math.round(percent),
                0,
                100
        );
    }

    private static String readFirstLine(
            String path
    ) {

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new FileReader(path)
                        )
        ) {

            return reader.readLine();

        } catch (Throwable ignore) {
            return null;
        }
    }

    private static long readLong(
            String path
    ) {

        String line =
                readFirstLine(path);

        if (line == null) {
            return -1L;
        }

        try {

            return Long.parseLong(
                    line.trim()
            );

        } catch (Throwable ignore) {
            return -1L;
        }
    }

    private static long part(
            String[] values,
            int index
    ) {

        if (index >= values.length) {
            return 0L;
        }

        return Long.parseLong(
                values[index]
        );
    }

    private static int clamp(
            int value,
            int minimum,
            int maximum
    ) {

        return Math.max(
                minimum,
                Math.min(
                        maximum,
                        value
                )
        );
    }
}
