package com.gel.cleaner;

/**
 * Evidence-limited pre-purchase triage. Only human-reported external checks enter this
 * policy: no IMEI is read, stored, transmitted, or inferred from ordinary Android APIs.
 * A clear blacklist lookup is NOT proof of title or lawful ownership.
 */
final class UsedDevicePrecheckPolicy {
    // Operator must identify which authoritative source was consulted.
    // This is an operator record, not an automated official registry connection.
    static final int OFFICIAL_SOURCE_UNSPECIFIED = 0;
    static final int OFFICIAL_SOURCE_GSMA_REPORTED = 1;
    static final int OFFICIAL_SOURCE_PROVIDER_REPORTED = 2;
    static final int IMEI_UNKNOWN = 0;
    static final int IMEI_NOT_FLAGGED_REPORTED = 1;
    static final int IMEI_FLAGGED_REPORTED = 2;

    static final int INCOMPLETE = 0;
    static final int USER_REPORTED_CHECKLIST_COMPLETE = 1;
    static final int HOLD_FOR_REVIEW = 2;

    private UsedDevicePrecheckPolicy() { }

    static int evaluateWithOfficialSource(int imeiStatus, int officialSource,
                                          boolean identifiersMatch,
                                          boolean ownershipDocumentsSeen,
                                          boolean ownerAccountsRemovedAndSetupPassed) {
        if (officialSource != OFFICIAL_SOURCE_GSMA_REPORTED
                && officialSource != OFFICIAL_SOURCE_PROVIDER_REPORTED)
            return INCOMPLETE;
        return evaluate(imeiStatus, identifiersMatch,
                ownershipDocumentsSeen, ownerAccountsRemovedAndSetupPassed);
    }

    static int evaluate(int imeiStatus,
                        boolean identifiersMatch,
                        boolean ownershipDocumentsSeen,
                        boolean ownerAccountsRemovedAndSetupPassed) {
        // A declared block-list hit must never be outweighed by other checkmarks.
        if (imeiStatus == IMEI_FLAGGED_REPORTED) {
            return HOLD_FOR_REVIEW;
        }
        if (imeiStatus == IMEI_NOT_FLAGGED_REPORTED
                && identifiersMatch
                && ownershipDocumentsSeen
                && ownerAccountsRemovedAndSetupPassed) {
            return USER_REPORTED_CHECKLIST_COMPLETE;
        }
        return INCOMPLETE;
    }
}
