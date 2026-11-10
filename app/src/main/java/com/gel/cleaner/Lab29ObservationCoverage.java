package com.gel.cleaner;
/** Versioned, time-bounded Android observation; not an OEM authenticity verdict. */
public final class Lab29ObservationCoverage {
    public static final int SCHEMA = 2;
    private static final long MAX_AGE_MS = 2L * 60L * 60L * 1000L;
    private Lab29ObservationCoverage() {}
    public static boolean isFresh(int schema, long observedMs, long nowMs) {
        return schema == SCHEMA && observedMs > 0 && nowMs >= observedMs
                && nowMs - observedMs <= MAX_AGE_MS;
    }
}
