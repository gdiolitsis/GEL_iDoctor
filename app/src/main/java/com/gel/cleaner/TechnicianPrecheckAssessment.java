package com.gel.cleaner;

/**
 * Technician disposition based on available buyer statements, NOT a legal verdict.
 * OEM provenance and theft are separate unanswered questions.
 */
final class TechnicianPrecheckAssessment {
    static final int NOT_RUN_OR_STALE = 0;
    static final int MANUAL_CHECKS_INCOMPLETE = 1;
    static final int BUYER_REPORTED_CHECKLIST_COMPLETE = 2;
    static final int REPORTED_IMEI_BLOCK_REQUIRES_REVIEW = 3;

    private TechnicianPrecheckAssessment() {}

    static int disposition(boolean lab29Fresh, boolean buyerChecksFresh,
                           int reportedImeiStatus, boolean identifiersMatch,
                           boolean documentsSeen, boolean transferDone) {
        if (!lab29Fresh || !buyerChecksFresh) return NOT_RUN_OR_STALE;
        if (reportedImeiStatus == UsedDevicePrecheckPolicy.IMEI_FLAGGED_REPORTED)
            return REPORTED_IMEI_BLOCK_REQUIRES_REVIEW;
        return UsedDevicePrecheckPolicy.evaluate(reportedImeiStatus, identifiersMatch,
                        documentsSeen, transferDone)
                == UsedDevicePrecheckPolicy.USER_REPORTED_CHECKLIST_COMPLETE
                ? BUYER_REPORTED_CHECKLIST_COMPLETE : MANUAL_CHECKS_INCOMPLETE;
    }
}
