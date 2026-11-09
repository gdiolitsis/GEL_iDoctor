package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab24BootEvidenceTest {
    @Test public void missingPropsAreUnknown() {
        assertEquals(Lab24BootEvidence.Result.UNKNOWN,
                Lab24BootEvidence.classify(null, null, null));
    }
    @Test public void incompleteLockStateIsNotGreenHardwareCertificate() {
        assertEquals(Lab24BootEvidence.Result.UNKNOWN,
                Lab24BootEvidence.classify("green", null, null));
    }
    @Test public void greenAndLockedAreOnlyOsReported() {
        assertEquals(Lab24BootEvidence.Result.OS_REPORTS_LOCKED_VERIFIED,
                Lab24BootEvidence.classify("GREEN", "LOCKED", "1"));
    }
    @Test public void yellowIsNonstandardTrustChainNotRootProof() {
        assertEquals(Lab24BootEvidence.Result.OS_REPORTS_NONSTANDARD_TRUST,
                Lab24BootEvidence.classify("yellow", "locked", "1"));
    }
    @Test public void orangeAndUnlockedReportPotentialUnverifiedBoot() {
        assertEquals(Lab24BootEvidence.Result.OS_REPORTS_UNLOCKED_OR_UNVERIFIED,
                Lab24BootEvidence.classify("orange", "unlocked", "0"));
    }
    @Test public void redIsOsReportedBootConcernOnly() {
        assertEquals(Lab24BootEvidence.Result.OS_REPORTS_UNLOCKED_OR_UNVERIFIED,
                Lab24BootEvidence.classify("red", null, null));
    }
    @Test public void greenAndUnlockedAreInconsistentNotClean() {
        assertEquals(Lab24BootEvidence.Result.OS_PROPERTIES_INCONSISTENT,
                Lab24BootEvidence.classify("green", "unlocked", "0"));
    }
    @Test public void contradictoryLockPropsAreInconsistent() {
        assertEquals(Lab24BootEvidence.Result.OS_PROPERTIES_INCONSISTENT,
                Lab24BootEvidence.classify(null, "locked", "0"));
        assertEquals(Lab24BootEvidence.Result.OS_PROPERTIES_INCONSISTENT,
                Lab24BootEvidence.classify(null, "unlocked", "1"));
    }
    @Test public void unknownVendorPropertyNeverMeansVerified() {
        assertEquals(Lab24BootEvidence.Result.UNKNOWN,
                Lab24BootEvidence.classify("custom", "custom", "custom"));
    }
    @Test public void normalizationDoesNotGrantUnknownConfidence() {
        assertEquals(Lab24BootEvidence.Result.OS_REPORTS_LOCKED_VERIFIED,
                Lab24BootEvidence.classify("  Green ", "locked", "1"));
        assertEquals(Lab24BootEvidence.Result.UNKNOWN,
                Lab24BootEvidence.classify("", "", ""));
    }
}
