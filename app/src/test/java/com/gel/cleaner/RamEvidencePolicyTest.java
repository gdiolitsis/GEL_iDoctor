package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class RamEvidencePolicyTest {
    @Test public void lowRamPercentDoesNotProveLagOrAppKills() {
        assertEquals(RamEvidencePolicy.Assessment.LOW_AVAILABLE_RAM,
                RamEvidencePolicy.assess(5, false));
        assertEquals(RamEvidencePolicy.Assessment.NO_OS_LOW_MEMORY_SIGNAL,
                RamEvidencePolicy.assess(25, false));
    }

    @Test public void androidLowMemorySignalTakesPriority() {
        assertEquals(RamEvidencePolicy.Assessment.ANDROID_LOW_MEMORY,
                RamEvidencePolicy.assess(35, true));
    }

    @Test public void missingOrInconsistentSwapCannotBeReportedAsZero() {
        assertEquals(-1L, RamEvidencePolicy.observedSwapUsedKb(-1, -1));
        assertEquals(-1L, RamEvidencePolicy.observedSwapUsedKb(1024, -1));
        assertEquals(-1L, RamEvidencePolicy.observedSwapUsedKb(1024, 2048));
        assertEquals(0L, RamEvidencePolicy.observedSwapUsedKb(0, 0));
        assertEquals(0L, RamEvidencePolicy.observedSwapUsedKb(1024, 1024));
        assertEquals(768L, RamEvidencePolicy.observedSwapUsedKb(1024, 256));
    }

    @Test public void invalidPercentIsNotHealthy() {
        assertEquals(RamEvidencePolicy.Assessment.INSUFFICIENT_DATA,
                RamEvidencePolicy.assess(-1, false));
        assertEquals(RamEvidencePolicy.Assessment.INSUFFICIENT_DATA,
                RamEvidencePolicy.assess(101, false));
    }
}
