package com.gel.cleaner;

/**
 * A Lab 14B rate is an observed charge-counter change over one bounded
 * workload, NOT a capacity SoH estimate or observed real-world endurance.
 * Read-only policy shared by Lab 17, Lab 30 and Lab 31.
 */
final class Lab14BRateEvidence {
    static final long MIN_DURATION_MS = 240_000L;
    static final long MAX_AGE_MS = 2L * 60L * 60L * 1000L;
    static final String SOURCE = "BATTERY_MANAGER_CHARGE_COUNTER_ELAPSED";

    private Lab14BRateEvidence() {}

    static boolean isUsable(int schema, boolean valid, String source,
                            long durationMs, long recordedAtMs,
                            long nowMs, float rateMahPerHour) {
        return schema == 1
                && valid
                && SOURCE.equals(source)
                && durationMs >= MIN_DURATION_MS
                && recordedAtMs > 0L
                && nowMs >= recordedAtMs
                && nowMs - recordedAtMs <= MAX_AGE_MS
                && Float.isFinite(rateMahPerHour)
                && rateMahPerHour > 0f;
    }
}
