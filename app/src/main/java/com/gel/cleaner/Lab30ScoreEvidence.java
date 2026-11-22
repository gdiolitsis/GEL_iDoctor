package com.gel.cleaner;

/** Lab 30: missing thermal/battery data must not be interpreted as a perfect score. */
public final class Lab30ScoreEvidence {
    private Lab30ScoreEvidence() {}

    /** Battery temperature is not independent CPU/GPU/board thermal coverage.
     *  Lab 30 must not certify health based on battery-only observations.
     */
    public static boolean hasThermalReadings(float max, float avg,
                                              Float cpu, Float gpu, Float skin, Float pmic) {
        if (!Float.isFinite(max) || !Float.isFinite(avg)
                || max <= 0f || avg <= 0f || max > 120f || avg > 120f) {
            return false;
        }
        return validZone(cpu) || validZone(gpu) || validZone(skin) || validZone(pmic);
    }

    private static boolean validZone(Float value) {
        return value != null && Float.isFinite(value)
                && value > 0f && value <= 120f;
    }

    public static boolean hasBatteryReadings(float temp, float pct) {
        return Float.isFinite(temp) && Float.isFinite(pct)
                && temp > 0f && temp <= 100f && pct >= 0f && pct <= 100f;
    }

    /** A failed/hidden StatFs snapshot is unknown, not 0% free space. */
    public static boolean hasStorageReadings(long totalBytes, long availableBytes) {
        return totalBytes > 0L && availableBytes >= 0L && availableBytes <= totalBytes;
    }

    /** A missing ActivityManager reading is unknown, not 0% available RAM. */
    public static boolean hasRamReadings(long totalBytes, long availableBytes) {
        return totalBytes > 0L && availableBytes >= 0L && availableBytes <= totalBytes;
    }

    /** Visibility-limited inventory cannot be used without any visible packages. */
    public static boolean hasAppInventory(int totalPackages, int userPackages) {
        return totalPackages > 0 && userPackages >= 0 && userPackages <= totalPackages;
    }

    /** A permission sample with missing grants/counters is not full privacy coverage. */
    public static boolean hasPrivacyCoverage(int visibleUserApps, boolean incomplete) {
        return visibleUserApps > 0 && !incomplete;
    }

    /**
     * Human-readable grade for the available diagnostic snapshot (0..100).
     * This is NOT a component-health certificate, a battery SoH measurement,
     * or the legacy risk-only 'reliabilityScore'.
     * Missing inputs (-1) must never produce a reassuring A+.
     */
    public static String gradeForMeasuredSnapshot(int score) {
        if (score < 0 || score > 100) return null;
        if (score >= 92) return "A+";
        if (score >= 85) return "A";
        if (score >= 70) return "B";
        if (score >= 55) return "C";
        return "D";
    }

    /**
     * Certification-facing performance: storage, RAM and thermal observations only.
     * App counts and usage access are unrelated to tested hardware functionality.
     * Reweight the verified original components without inventing an app score.
     */
    public static int functionalPerformanceScore(int storage, int ram, int thermal) {
        if (storage < 0 || ram < 0 || thermal < 0) return -1;
        return Math.round(storage * (7f / 17f)
                + ram * (7f / 17f) + thermal * (3f / 17f));
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
