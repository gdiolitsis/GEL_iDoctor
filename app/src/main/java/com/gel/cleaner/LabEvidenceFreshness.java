package com.gel.cleaner;

/** Only same-boot, time-bounded evidence may be included in a later Lab 30 report. */
final class LabEvidenceFreshness {
    static final long MAX_AGE_MS = 24L * 60L * 60L * 1000L;
    private static final long CLOCK_DRIFT_TOLERANCE_MS = 5L * 60L * 1000L;

    private LabEvidenceFreshness() {}

    static boolean isFresh(int schema, long wallAtRun, long elapsedAtRun,
                           long wallNow, long elapsedNow) {
        if (schema != 1 || wallAtRun <= 0L || elapsedAtRun < 0L
                || wallNow <= 0L || elapsedNow < 0L) return false;
        long wallAge = wallNow - wallAtRun;
        long elapsedAge = elapsedNow - elapsedAtRun;
        return wallAge >= 0L && wallAge <= MAX_AGE_MS
                && elapsedAge >= 0L && elapsedAge <= MAX_AGE_MS
                && Math.abs(wallAge - elapsedAge) <= CLOCK_DRIFT_TOLERANCE_MS;
    }

    static boolean isReportedBuyerCheckFresh(
            int schema, long lab29Wall, long buyerWall, long buyerElapsed,
            long wallNow, long elapsedNow) {
        return lab29Wall > 0L && buyerWall >= lab29Wall
                && isFresh(schema, buyerWall, buyerElapsed, wallNow, elapsedNow);
    }
}
