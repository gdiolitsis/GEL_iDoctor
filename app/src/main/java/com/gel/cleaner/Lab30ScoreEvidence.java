package com.gel.cleaner;

/** Lab 30: missing thermal/battery data must not be interpreted as a perfect score. */
public final class Lab30ScoreEvidence {
    private Lab30ScoreEvidence() {}

    public static boolean hasThermalReadings(float max, float avg) {
        return Float.isFinite(max) && Float.isFinite(avg)
                && max > 0f && avg > 0f && max <= 120f && avg <= 120f;
    }

    public static boolean hasBatteryReadings(float temp, float pct) {
        return Float.isFinite(temp) && Float.isFinite(pct)
                && temp > 0f && temp <= 100f && pct >= 0f && pct <= 100f;
    }

    public static int performanceScore(int storage, int ram, int apps, int thermal) {
        if (storage < 0 || ram < 0 || apps < 0 || thermal < 0) return -1;
        return Math.round(storage * 0.35f + ram * 0.35f
                + apps * 0.15f + thermal * 0.15f);
    }

    public static int deviceHealthScore(int thermal, int battery, int perf, int stability) {
        if (thermal < 0 || battery < 0 || perf < 0 || stability < 0) return -1;
        return Math.round(thermal * 0.25f + battery * 0.25f
                + perf * 0.30f + stability * 0.20f);
    }
}
