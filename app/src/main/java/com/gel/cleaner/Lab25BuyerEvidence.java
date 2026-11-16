package com.gel.cleaner;

/**
 * Evidence contract between Lab 25 and the PRO used-device check (Lab 28).
 *
 * Completion of a diagnostic is NOT proof that Android exposed crash history.
 * Only a fresh, same-boot Lab 25 result satisfies the prerequisite, and every
 * buyer-facing conclusion must retain its evidence coverage limitations.
 */
final class Lab25BuyerEvidence {
    static final int SCHEMA = 1;

    static final String LIMITED = "LIMITED";
    static final String ACCESSIBLE = "ACCESSIBLE_RECORDS_ONLY";
    static final String USER_REPORT = "USER_SELECTED_REPORT";

    static final String NONE = "NO_CRITICAL_RECORDS";
    static final String ISOLATED = "ISOLATED";
    static final String REPEATED = "REPEATED";
    static final String SERIOUS = "SERIOUS_SYSTEM_EVENT";
    static final String STRONG = "STRONG_INSTABILITY";

    private Lab25BuyerEvidence() {}

    static boolean canUse(int schema, long wallMs, long elapsedMs,
                          long nowWallMs, long nowElapsedMs) {
        return LabEvidenceFreshness.isFresh(
                schema, wallMs, elapsedMs, nowWallMs, nowElapsedMs);
    }

    static String classify(CriticalSystemCrashEvidence.Summary report) {
        if (report == null || !report.read) return null;
        if (report.hasStrongInstabilityIndications()) return STRONG;
        if (report.hasSeriousSystemIncident()) return SERIOUS;
        if (report.hasVerifiedRecurrence()) return REPEATED;
        if (!report.events.isEmpty()) return ISOLATED;
        return NONE;
    }

    static boolean isKnown(String result) {
        return NONE.equals(result) || ISOLATED.equals(result)
                || REPEATED.equals(result) || SERIOUS.equals(result)
                || STRONG.equals(result);
    }

    static boolean isKnownCoverage(String coverage) {
        return LIMITED.equals(coverage) || ACCESSIBLE.equals(coverage)
                || USER_REPORT.equals(coverage);
    }

    static int rank(String result) {
        if (STRONG.equals(result)) return 5;
        if (SERIOUS.equals(result)) return 4;
        if (REPEATED.equals(result)) return 3;
        if (ISOLATED.equals(result)) return 2;
        if (NONE.equals(result)) return 1;
        return 0;
    }

    // Importing a clean/unrelated report cannot erase previously found failures.
    static boolean shouldReplace(String previous, String imported) {
        return rank(imported) > rank(previous);
    }

    static boolean validCounts(int entries, int independent) {
        return entries >= 0 && entries <= 128
                && independent >= 0 && independent <= entries;
    }
}
