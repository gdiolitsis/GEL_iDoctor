package com.gel.cleaner;

/**
 * Evidence-scoped restoration of the old Lab 30 device grade (A+..D).
 *
 * This is an internal summary of four *observed* measurement categories, not
 * battery State of Health, physical-part integrity or OEM certification.
 * Missing inputs cannot be converted to a perfect or passing grade. A serious
 * independent crash-history finding cannot be hidden by a high snapshot score.
 */
public final class Lab27DeviceRating {
    private Lab27DeviceRating() {}

    public static final class Result {
        public final int score;
        public final String grade;
        public final boolean hasEnoughEvidence;
        public final boolean independentWarning;

        private Result(int score, String grade, boolean complete, boolean warning) {
            this.score = score;
            this.grade = grade;
            this.hasEnoughEvidence = complete;
            this.independentWarning = warning;
        }
    }

    public static Result evaluate(int thermal, int batterySnapshot, int performance,
                                  int uptimeSnapshot, boolean validStorage,
                                  boolean validRam, boolean validAppInventory,
                                  boolean independentCriticalHistory) {
        // Performance combines RAM, storage and app inventory; it is not
        // evidence of physical NAND condition, repair history or app defects.
        boolean complete = validStorage && validRam && validAppInventory;
        int composite = complete
                ? Lab30ScoreEvidence.deviceHealthScore(
                        thermal, batterySnapshot, performance, uptimeSnapshot)
                : -1;
        if (composite < 0 || composite > 100) {
            return new Result(-1, "", false, independentCriticalHistory);
        }

        // Letter boundaries preserved from the protected Lab 30 backup.
        String grade = composite >= 92 ? "A+"
                : composite >= 85 ? "A"
                : composite >= 70 ? "B"
                : composite >= 55 ? "C" : "D";

        // A strong, independently observed event must not be concealed by A+.
        // Retain the numeric snapshot as context; withhold the grade.
        return new Result(composite, independentCriticalHistory ? "" : grade,
                true, independentCriticalHistory);
    }
}
