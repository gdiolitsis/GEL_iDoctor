package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab26EvidenceTest {
    @Test public void failedPackageQueryIsUnavailable() {
        assertEquals(Lab26Evidence.PackageCoverage.UNAVAILABLE,
                Lab26Evidence.packageCoverage(false, 12));
    }
    @Test public void emptySuccessfulQueryIsVisibleOnlyNotClean() {
        assertEquals(Lab26Evidence.PackageCoverage.VISIBLE_ONLY,
                Lab26Evidence.packageCoverage(true, 0));
    }
    @Test public void positiveVisibleCountNeverProvesFullInventory() {
        assertEquals(Lab26Evidence.PackageCoverage.VISIBLE_ONLY,
                Lab26Evidence.packageCoverage(true, 120));
    }
    @Test public void corruptNegativeVisibleCountIsUnavailable() {
        assertEquals(Lab26Evidence.PackageCoverage.UNAVAILABLE,
                Lab26Evidence.packageCoverage(true, -1));
    }
    @Test public void ownUidTrafficCanBeObserved() {
        assertEquals(30L, Lab26Evidence.ownUidTrafficBytes(123, 123, 10, 20));
    }
    @Test public void otherUidTrafficIsUnavailableEvenWithNumbers() {
        assertEquals(-1L, Lab26Evidence.ownUidTrafficBytes(124, 123, 10, 20));
    }
    @Test public void unsupportedRxIsNotZeroTraffic() {
        assertEquals(-1L, Lab26Evidence.ownUidTrafficBytes(123, 123, -1, 0));
    }
    @Test public void unsupportedTxIsNotZeroTraffic() {
        assertEquals(-1L, Lab26Evidence.ownUidTrafficBytes(123, 123, 0, -1));
    }
    @Test public void overflowIsRejected() {
        assertEquals(-1L, Lab26Evidence.ownUidTrafficBytes(123, 123, Long.MAX_VALUE, 1));
    }
    @Test public void zeroOwnTrafficIsValidObservation() {
        assertEquals(0L, Lab26Evidence.ownUidTrafficBytes(123, 123, 0, 0));
    }
    @Test public void missingUsageAccessIsUnknown() {
        assertEquals(-1L, Lab26Evidence.foregroundMs(false, true, 1000));
    }
    @Test public void failedUsageQueryIsUnknown() {
        assertEquals(-1L, Lab26Evidence.foregroundMs(true, false, 1000));
    }
    @Test public void absentAppUsageRecordIsUnknownNotNoUse() {
        assertEquals(-1L, Lab26Evidence.foregroundMs(true, true, 0));
    }
    @Test public void positiveUsageObservationRetainsTime() {
        assertEquals(60000L, Lab26Evidence.foregroundMs(true, true, 60000));
    }
    @Test public void incompleteCoverageCannotYieldNormal() {
        assertEquals("INCONCLUSIVE", Lab26Evidence.summaryToken(false));
    }
    @Test public void visibleCoverageIsNeverHealthCertificate() {
        assertEquals("OBSERVED_LIMITED", Lab26Evidence.summaryToken(true));
    }
}
