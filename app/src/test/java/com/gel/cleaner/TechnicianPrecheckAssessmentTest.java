package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TechnicianPrecheckAssessmentTest {
    @Test public void noLab29IsUnknownNotStolenOrGenuine() {
        assertEquals(TechnicianPrecheckAssessment.NOT_RUN_OR_STALE,
                TechnicianPrecheckAssessment.disposition(false, false,
                        UsedDevicePrecheckPolicy.IMEI_UNKNOWN, false, false, false));
    }
    @Test public void staleBuyerReportMustNotIssueOldBlockAlertAsCurrent() {
        assertEquals(TechnicianPrecheckAssessment.NOT_RUN_OR_STALE,
                TechnicianPrecheckAssessment.disposition(true, false,
                        UsedDevicePrecheckPolicy.IMEI_FLAGGED_REPORTED, true, true, true));
    }
    @Test public void buyerBlockedImeiOverridesAllOtherChecks() {
        assertEquals(TechnicianPrecheckAssessment.REPORTED_IMEI_BLOCK_REQUIRES_REVIEW,
                TechnicianPrecheckAssessment.disposition(true, true,
                        UsedDevicePrecheckPolicy.IMEI_FLAGGED_REPORTED, true, true, true));
    }
    @Test public void cleanBuyerReportWithoutTransferCannotBeCompleted() {
        assertEquals(TechnicianPrecheckAssessment.MANUAL_CHECKS_INCOMPLETE,
                TechnicianPrecheckAssessment.disposition(true, true,
                        UsedDevicePrecheckPolicy.IMEI_NOT_FLAGGED_REPORTED,
                        true, true, false));
    }
    @Test public void completeIsBuyerChecklistNotProofOfOwnership() {
        assertEquals(TechnicianPrecheckAssessment.BUYER_REPORTED_CHECKLIST_COMPLETE,
                TechnicianPrecheckAssessment.disposition(true, true,
                        UsedDevicePrecheckPolicy.IMEI_NOT_FLAGGED_REPORTED,
                        true, true, true));
    }
    @Test public void unknownImeiNeverYieldsComplete() {
        assertEquals(TechnicianPrecheckAssessment.MANUAL_CHECKS_INCOMPLETE,
                TechnicianPrecheckAssessment.disposition(true, true,
                        UsedDevicePrecheckPolicy.IMEI_UNKNOWN, true, true, true));
    }
}
