package com.gel.cleaner;

/** A bounded lease measured by elapsed realtime, never by the changeable wall clock. */
final class GelTrialClock {
    private final long requestedAt, duration;
    GelTrialClock(long requestedAt, long serverNow, long expiry) {
        this.requestedAt = requestedAt;
        duration = serverNow > 0 && expiry > serverNow ? Math.min(60000, expiry-serverNow) : 0;
    }
    boolean active(long elapsedNow) {
        return elapsedNow >= requestedAt && elapsedNow-requestedAt < duration;
    }
}
