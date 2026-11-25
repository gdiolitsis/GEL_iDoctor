package com.gel.cleaner;

/** Pure, testable eight-hour service lease arithmetic. Never extends an issued lease. */
public final class GELServiceSessionWindow {
    public static final long MAX_MS = 8L * 60L * 60L * 1000L;

    private GELServiceSessionWindow() {}

    public static boolean validGrant(long serverNow, long endsAt) {
        return serverNow > 0L && endsAt > serverNow &&
                endsAt - serverNow <= MAX_MS;
    }

    public static long remaining(long startElapsed, long deadlineElapsed,
                                 long currentElapsed, long endsAtWall, long currentWall) {
        // A reboot invalidates an old monotonic lease; do not trust only wall time.
        if (startElapsed <= 0L || deadlineElapsed <= startElapsed ||
                currentElapsed < startElapsed || currentElapsed >= deadlineElapsed ||
                endsAtWall <= currentWall) {
            return 0L;
        }
        return Math.max(0L, Math.min(deadlineElapsed - currentElapsed,
                endsAtWall - currentWall));
    }
}
