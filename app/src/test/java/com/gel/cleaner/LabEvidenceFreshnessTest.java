package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class LabEvidenceFreshnessTest {
    private static final long DAY = LabEvidenceFreshness.MAX_AGE_MS;
    @Test public void acceptsSameBootRecentMeasurements() {
        assertTrue(LabEvidenceFreshness.isFresh(1, 1000000L, 100000L,
                1060000L, 160000L));
    }
    @Test public void rejectsUnknownLegacySchema() {
        assertFalse(LabEvidenceFreshness.isFresh(0, 1000000L, 100000L,
                1060000L, 160000L));
    }
    @Test public void rejectsMissingReadings() {
        assertFalse(LabEvidenceFreshness.isFresh(1, 0L, 0L, 1060000L, 160000L));
    }
    @Test public void rejectsStaleReading() {
        assertFalse(LabEvidenceFreshness.isFresh(1, 1000000L, 100000L,
                1000000L + DAY + 1L, 100000L + DAY + 1L));
    }
    @Test public void rejectsRebootEvenIfWallTimeMatches() {
        assertFalse(LabEvidenceFreshness.isFresh(1, 1000000L, 200000L,
                1060000L, 10000L));
    }
    @Test public void rejectsClockDisagreement() {
        assertFalse(LabEvidenceFreshness.isFresh(1, 1000000L, 100000L,
                1600000L, 160000L));
    }
    @Test public void buyerChecklistRequiresSameRunAndFreshTime() {
        assertTrue(LabEvidenceFreshness.isReportedBuyerCheckFresh(
                1, 1000000L, 1001000L, 100000L, 1061000L, 160000L));
        assertFalse(LabEvidenceFreshness.isReportedBuyerCheckFresh(
                1, 1100000L, 1001000L, 100000L, 1160000L, 160000L));
    }
}
