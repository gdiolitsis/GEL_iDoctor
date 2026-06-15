// GDiolitsis Engine Lab (GEL) — Author & Developer
// CpuStatBridge.java — Shared native CPU engine

package com.gel.cleaner;

public final class CpuStatBridge {

    static {
        System.loadLibrary("cpustat");
    }

    private CpuStatBridge() {}

    public static native int getCpuUsageNative();

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
}
