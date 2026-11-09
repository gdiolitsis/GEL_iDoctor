package com.gel.cleaner;

/**
 * Lab 6 evidence contract. A touchscreen grid is a bounded *functional sample*,
 * not a calibrated or exhaustive digitizer diagnostic. Visual findings are only
 * user reports; missing/aborted observations can never become hardware verdicts.
 */
final class Lab6Evidence {
    static final int GRID_COLUMNS = 8;
    static final int GRID_ROWS = 12;
    static final int TOTAL_ZONES = GRID_COLUMNS * GRID_ROWS;

    enum Touch {
        GRID_SAMPLED, USER_STOPPED_INCOMPLETE, INCONCLUSIVE
    }

    enum Visual {
        USER_REPORTED_NO_ISSUE, USER_REPORTED_ISSUE, INCONCLUSIVE
    }

    private Lab6Evidence() {}

    static Touch touch(boolean resultOk, boolean hasFreshCountEvidence,
                       int total, int remaining) {
        if (!hasFreshCountEvidence || total != TOTAL_ZONES
                || remaining < 0 || remaining > total) {
            return Touch.INCONCLUSIVE;
        }
        if (resultOk) {
            return remaining == 0 ? Touch.GRID_SAMPLED : Touch.INCONCLUSIVE;
        }
        // The user explicitly stopped this run, not proof of a faulty cell.
        return Touch.USER_STOPPED_INCOMPLETE;
    }

    static Visual visual(boolean resultOk, boolean hasExplicitAnswer,
                         boolean presentationCompleted, boolean reportedIssue) {
        if (!resultOk || !hasExplicitAnswer || !presentationCompleted) {
            return Visual.INCONCLUSIVE;
        }
        return reportedIssue
                ? Visual.USER_REPORTED_ISSUE
                : Visual.USER_REPORTED_NO_ISSUE;
    }

    static boolean guidedProtocolComplete(Touch touch, Visual visual) {
        return touch == Touch.GRID_SAMPLED && visual != Visual.INCONCLUSIVE;
    }
}
