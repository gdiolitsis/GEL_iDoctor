package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UsedDevicePrecheckPolicyTest {
    @Test public void uncheckedIsNeverApproved() {
        assertEquals(UsedDevicePrecheckPolicy.INCOMPLETE,
                UsedDevicePrecheckPolicy.evaluate(UsedDevicePrecheckPolicy.IMEI_UNKNOWN,
                        false, false, false));
    }
    @Test public void blacklistClearAloneIsInsufficient() {
        assertEquals(UsedDevicePrecheckPolicy.INCOMPLETE,
                UsedDevicePrecheckPolicy.evaluate(UsedDevicePrecheckPolicy.IMEI_NOT_FLAGGED_REPORTED,
                        false, false, false));
    }
    @Test public void allFourChecksAreOnlyUserReported() {
        assertEquals(UsedDevicePrecheckPolicy.USER_REPORTED_CHECKLIST_COMPLETE,
                UsedDevicePrecheckPolicy.evaluate(UsedDevicePrecheckPolicy.IMEI_NOT_FLAGGED_REPORTED,
                        true, true, true));
    }
    @Test public void blockListHitOverridesAllChecks() {
        assertEquals(UsedDevicePrecheckPolicy.HOLD_FOR_REVIEW,
                UsedDevicePrecheckPolicy.evaluate(UsedDevicePrecheckPolicy.IMEI_FLAGGED_REPORTED,
                        true, true, true));
    }
    @Test public void frpUnresolvedIsNotComplete() {
        assertEquals(UsedDevicePrecheckPolicy.INCOMPLETE,
                UsedDevicePrecheckPolicy.evaluate(UsedDevicePrecheckPolicy.IMEI_NOT_FLAGGED_REPORTED,
                        true, true, false));
    }
    @Test public void unknownImeiCannotPassEvenWithDocuments() {
        assertEquals(UsedDevicePrecheckPolicy.INCOMPLETE,
                UsedDevicePrecheckPolicy.evaluate(UsedDevicePrecheckPolicy.IMEI_UNKNOWN,
                        true, true, true));
    }
    @Test public void invalidImeiValueDefaultsToIncomplete() {
        assertEquals(UsedDevicePrecheckPolicy.INCOMPLETE,
                UsedDevicePrecheckPolicy.evaluate(-1, true, true, true));
    }
    @Test public void missingOfficialSourceCannotMakeChecklistComplete() {
        assertEquals(UsedDevicePrecheckPolicy.INCOMPLETE,
                UsedDevicePrecheckPolicy.evaluateWithOfficialSource(
                        UsedDevicePrecheckPolicy.IMEI_NOT_FLAGGED_REPORTED,
                        UsedDevicePrecheckPolicy.OFFICIAL_SOURCE_UNSPECIFIED,
                        true, true, true));
    }
    @Test public void recordedOfficialGsmaResultCanBeUsed() {
        assertEquals(UsedDevicePrecheckPolicy.USER_REPORTED_CHECKLIST_COMPLETE,
                UsedDevicePrecheckPolicy.evaluateWithOfficialSource(
                        UsedDevicePrecheckPolicy.IMEI_NOT_FLAGGED_REPORTED,
                        UsedDevicePrecheckPolicy.OFFICIAL_SOURCE_GSMA_REPORTED,
                        true, true, true));
    }

}
