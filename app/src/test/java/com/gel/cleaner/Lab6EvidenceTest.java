package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab6EvidenceTest {
    @Test public void ninetySixFreshSamplesOnlySupportGridObservation() {
        assertEquals(Lab6Evidence.Touch.GRID_SAMPLED,
                Lab6Evidence.touch(true, true, 96, 0));
    }
    @Test public void cancelledTouchIsNotDeadDigitizerEvidence() {
        assertEquals(Lab6Evidence.Touch.USER_STOPPED_INCOMPLETE,
                Lab6Evidence.touch(false, true, 96, 27));
    }
    @Test public void staleStaticOrMissingIntentEvidenceFailsClosed() {
        assertEquals(Lab6Evidence.Touch.INCONCLUSIVE,
                Lab6Evidence.touch(true, false, 96, 0));
    }
    @Test public void resultOkWithUnclearedZoneIsNotComplete() {
        assertEquals(Lab6Evidence.Touch.INCONCLUSIVE,
                Lab6Evidence.touch(true, true, 96, 1));
    }
    @Test public void invalidCountersCannotSupportCertification() {
        assertEquals(Lab6Evidence.Touch.INCONCLUSIVE,
                Lab6Evidence.touch(true, true, 0, 0));
        assertEquals(Lab6Evidence.Touch.INCONCLUSIVE,
                Lab6Evidence.touch(true, true, 96, -1));
        assertEquals(Lab6Evidence.Touch.INCONCLUSIVE,
                Lab6Evidence.touch(true, true, 96, 97));
    }
    @Test public void visualQuestionRequiresExplicitCompletedRun() {
        assertEquals(Lab6Evidence.Visual.INCONCLUSIVE,
                Lab6Evidence.visual(true, false, true, false));
        assertEquals(Lab6Evidence.Visual.INCONCLUSIVE,
                Lab6Evidence.visual(true, true, false, false));
        assertEquals(Lab6Evidence.Visual.INCONCLUSIVE,
                Lab6Evidence.visual(false, true, true, false));
    }
    @Test public void visualNoOnlyMeansUserReportedNoIssue() {
        assertEquals(Lab6Evidence.Visual.USER_REPORTED_NO_ISSUE,
                Lab6Evidence.visual(true, true, true, false));
    }
    @Test public void visualYesDoesNotSpecifyTypeOfDefect() {
        assertEquals(Lab6Evidence.Visual.USER_REPORTED_ISSUE,
                Lab6Evidence.visual(true, true, true, true));
    }
    @Test public void incompleteTouchNeverBecomesFullProtocolFromVisualYes() {
        assertFalse(Lab6Evidence.guidedProtocolComplete(
                Lab6Evidence.Touch.USER_STOPPED_INCOMPLETE,
                Lab6Evidence.Visual.USER_REPORTED_NO_ISSUE));
        assertFalse(Lab6Evidence.guidedProtocolComplete(
                Lab6Evidence.Touch.GRID_SAMPLED,
                Lab6Evidence.Visual.INCONCLUSIVE));
        assertTrue(Lab6Evidence.guidedProtocolComplete(
                Lab6Evidence.Touch.GRID_SAMPLED,
                Lab6Evidence.Visual.USER_REPORTED_ISSUE));
    }
}
