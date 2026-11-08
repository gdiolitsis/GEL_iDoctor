package com.gel.cleaner;

/** Evidence-first classification. Uptime alone is not reboot history. */
public final class RebootHistoryEvidence {
    public enum Verdict {
        INSUFFICIENT_DATA,
        REPEATED_OBSERVED,
        NO_REPEATED_EVENTS_OBSERVED
    }

    private RebootHistoryEvidence() {}

    /**
     * @param observedReboots number of verified reboot events in a known window;
     *                        -1 when no historical event data has been collected
     * @param windowMs duration of that verified observation window
     */
    public static Verdict classify(int observedReboots, long windowMs) {
        if (observedReboots < 0 || windowMs <= 0L) {
            return Verdict.INSUFFICIENT_DATA;
        }
        return observedReboots >= 2
                ? Verdict.REPEATED_OBSERVED
                : Verdict.NO_REPEATED_EVENTS_OBSERVED;
    }
}
