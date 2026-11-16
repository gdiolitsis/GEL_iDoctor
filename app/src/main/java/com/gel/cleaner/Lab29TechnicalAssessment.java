package com.gel.cleaner;

/**
 * Immutable Lab29 -> Lab28 technical provenance contract.
 * A completed technical report is valid only for a fresh, same-boot Lab25 snapshot.
 * Re-running Lab25 invalidates an earlier Lab29 assessment.
 */
final class Lab29TechnicalAssessment {
    static final int SCHEMA = 1;
    static final String NO_RECORDED_INSTABILITY = "NO_RECORDED_INSTABILITY";
    static final String LIMITED_CRASH_HISTORY = "LIMITED_CRASH_HISTORY";
    static final String ISOLATED_EVENT = "ISOLATED_EVENT";
    static final String REVIEW_REQUIRED = "REVIEW_REQUIRED";
    static final String STRONG_INSTABILITY = "STRONG_INSTABILITY";

    private Lab29TechnicalAssessment() {}

    static String summarize(String crashLevel, String coverage) {
        if (!Lab25BuyerEvidence.isKnown(crashLevel)
                || !Lab25BuyerEvidence.isKnownCoverage(coverage)) return null;
        if (Lab25BuyerEvidence.STRONG.equals(crashLevel)) return STRONG_INSTABILITY;
        if (Lab25BuyerEvidence.REPEATED.equals(crashLevel)
                || Lab25BuyerEvidence.SERIOUS.equals(crashLevel)) return REVIEW_REQUIRED;
        if (Lab25BuyerEvidence.ISOLATED.equals(crashLevel)) return ISOLATED_EVENT;
        return Lab25BuyerEvidence.LIMITED.equals(coverage)
                ? LIMITED_CRASH_HISTORY : NO_RECORDED_INSTABILITY;
    }

    static boolean known(String level) {
        return NO_RECORDED_INSTABILITY.equals(level)
                || LIMITED_CRASH_HISTORY.equals(level)
                || ISOLATED_EVENT.equals(level)
                || REVIEW_REQUIRED.equals(level)
                || STRONG_INSTABILITY.equals(level);
    }

    static boolean isCurrent(int schema, long certWall, long certElapsed,
                             long input25Wall, long input25Elapsed,
                             long current25Wall, long current25Elapsed,
                             long nowWall, long nowElapsed) {
        return schema == SCHEMA
                && input25Wall > 0L && input25Elapsed >= 0L
                && input25Wall == current25Wall
                && input25Elapsed == current25Elapsed
                && certWall >= input25Wall
                && certElapsed >= input25Elapsed
                && LabEvidenceFreshness.isFresh(
                        schema, certWall, certElapsed, nowWall, nowElapsed);
    }
}
