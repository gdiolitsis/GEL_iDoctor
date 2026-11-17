package com.gel.cleaner;

/**
 * Minimum recovery interval and battery-temperature baseline check between
 * Lab 14A, 14B and 14 GAMMA runs. This is a measurement-quality gate, not a
 * battery safety or device-health certification.
 */
public final class Lab14RecoveryGate {
    private Lab14RecoveryGate() {}

    public static final long MIN_REST_MS = 5L * 60L * 1000L;
    public static final float MAX_ABOVE_BASELINE_C = 2.0f;
    public static final float MAX_START_TEMP_C = 45.0f;
    private static final long BOOT_WALL_TOLERANCE_MS = 120000L;

    public enum Status {
        READY, WAIT_TIME, WAIT_TEMPERATURE, TEMPERATURE_UNAVAILABLE
    }

    public static final class Decision {
        public final Status status;
        public final long remainingMs;
        public final float baselineC;
        public final float currentC;

        private Decision(Status status, long remainingMs, float baselineC, float currentC) {
            this.status = status;
            this.remainingMs = remainingMs;
            this.baselineC = baselineC;
            this.currentC = currentC;
        }

        public boolean mayStart() {
            return status == Status.READY;
        }
    }

    public static boolean validBatteryTemp(float c) {
        return Float.isFinite(c) && c >= -20f && c < 85f;
    }

    public static Decision evaluate(long stopElapsedMs, long stopWallMs,
                                    long nowElapsedMs, long nowWallMs,
                                    float baselineC, float currentC) {
        if (stopElapsedMs <= 0L || stopWallMs <= 0L) {
            return new Decision(Status.READY, 0L, baselineC, currentC);
        }

        // Elapsed real time survives app restarts and cannot be changed by
        // wall-clock adjustments. After reboot, use the persisted wall time.
        long ageMs;
        long thenBootWall = stopWallMs - stopElapsedMs;
        long nowBootWall = nowWallMs - nowElapsedMs;
        boolean sameBoot = nowElapsedMs >= stopElapsedMs
                && Math.abs(nowBootWall - thenBootWall) <= BOOT_WALL_TOLERANCE_MS;
        if (sameBoot) {
            ageMs = nowElapsedMs - stopElapsedMs;
        } else {
            ageMs = Math.max(0L, nowWallMs - stopWallMs);
        }

        long remaining = Math.max(0L, MIN_REST_MS - ageMs);
        if (remaining > 0L) {
            return new Decision(Status.WAIT_TIME, remaining, baselineC, currentC);
        }

        // Do not report "thermally stable" when the baseline or sensor is
        // unavailable. Prompt for retry; never silently invent a temperature.
        if (!validBatteryTemp(baselineC) || !validBatteryTemp(currentC)) {
            return new Decision(Status.TEMPERATURE_UNAVAILABLE, 0L, baselineC, currentC);
        }

        // Cooling below baseline is acceptable; only residual heat matters.
        if (currentC > baselineC + MAX_ABOVE_BASELINE_C
                || currentC >= MAX_START_TEMP_C) {
            return new Decision(Status.WAIT_TEMPERATURE, 0L, baselineC, currentC);
        }

        return new Decision(Status.READY, 0L, baselineC, currentC);
    }
}
