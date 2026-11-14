package com.gel.cleaner;

/**
 * Technician disposition based on available buyer statements, NOT a legal verdict.
 * OEM authenticity and the officially recorded IMEI block status are separate checks.
 */
final class TechnicianPrecheckAssessment {
    static final int NOT_RUN_OR_STALE = 0;
    static final int MANUAL_CHECKS_INCOMPLETE = 1;
    static final int BUYER_REPORTED_CHECKLIST_COMPLETE = 2;
    static final int REPORTED_IMEI_BLOCK_REQUIRES_REVIEW = 3;

    private TechnicianPrecheckAssessment() {}

    static int disposition(boolean lab29Fresh, boolean buyerChecksFresh,
                           int officialSource, int reportedImeiStatus, boolean identifiersMatch,
                           boolean documentsSeen, boolean transferDone) {
        if (!lab29Fresh || !buyerChecksFresh) return NOT_RUN_OR_STALE;
        if (officialSource != UsedDevicePrecheckPolicy.OFFICIAL_SOURCE_GSMA_REPORTED
                && officialSource != UsedDevicePrecheckPolicy.OFFICIAL_SOURCE_PROVIDER_REPORTED)
            return MANUAL_CHECKS_INCOMPLETE;
        if (reportedImeiStatus == UsedDevicePrecheckPolicy.IMEI_FLAGGED_REPORTED)
            return REPORTED_IMEI_BLOCK_REQUIRES_REVIEW;
        return UsedDevicePrecheckPolicy.evaluateWithOfficialSource(reportedImeiStatus,
                        officialSource, identifiersMatch,
                        documentsSeen, transferDone)
                == UsedDevicePrecheckPolicy.USER_REPORTED_CHECKLIST_COMPLETE
                ? BUYER_REPORTED_CHECKLIST_COMPLETE : MANUAL_CHECKS_INCOMPLETE;
    }
}
