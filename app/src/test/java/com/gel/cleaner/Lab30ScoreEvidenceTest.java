package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab30ScoreEvidenceTest {
    @Test public void missingInputsCannotBeConvertedToPerfectHealth() {
        assertFalse(Lab30ScoreEvidence.hasThermalReadings(Float.NaN, Float.NaN,
                null, null, null, null));
        assertFalse(Lab30ScoreEvidence.hasThermalReadings(-1f, -1f,
                null, null, null, null));
        // 25C battery reading alone previously generated a perfect thermal score.
        assertFalse(Lab30ScoreEvidence.hasThermalReadings(25f, 25f,
                null, null, null, null));
        assertFalse(Lab30ScoreEvidence.hasThermalReadings(25f, 25f,
                Float.NaN, null, null, null));
        assertFalse(Lab30ScoreEvidence.hasBatteryReadings(Float.NaN, 75f));
        assertFalse(Lab30ScoreEvidence.hasBatteryReadings(Float.NaN, -1f));
        assertTrue(Lab30ScoreEvidence.hasThermalReadings(35f, 31f,
                35f, null, null, null));
        assertTrue(Lab30ScoreEvidence.hasThermalReadings(35f, 31f,
                null, null, 32f, null));
        assertTrue(Lab30ScoreEvidence.hasBatteryReadings(30f, 80f));
    }
    @Test public void missingStorageAndRamCannotBecomeZeroPercent() {
        assertFalse(Lab30ScoreEvidence.hasStorageReadings(0, 0));
        assertFalse(Lab30ScoreEvidence.hasStorageReadings(100, -1));
        assertFalse(Lab30ScoreEvidence.hasStorageReadings(100, 101));
        assertTrue(Lab30ScoreEvidence.hasStorageReadings(100, 0));
        assertTrue(Lab30ScoreEvidence.hasStorageReadings(100, 100));
        assertFalse(Lab30ScoreEvidence.hasRamReadings(0, 0));
        assertFalse(Lab30ScoreEvidence.hasRamReadings(100, -1));
        assertFalse(Lab30ScoreEvidence.hasRamReadings(100, 101));
        assertTrue(Lab30ScoreEvidence.hasRamReadings(100, 0));
        assertTrue(Lab30ScoreEvidence.hasRamReadings(100, 55));
        assertFalse(Lab30ScoreEvidence.hasAppInventory(0, 0));
        assertFalse(Lab30ScoreEvidence.hasAppInventory(2, 3));
        assertTrue(Lab30ScoreEvidence.hasAppInventory(2, 0));
    }

    @Test public void absentOrPartialPermissionInventoryIsNotCleanPrivacy() {
        assertFalse(Lab30ScoreEvidence.hasPrivacyCoverage(0, false));
        assertFalse(Lab30ScoreEvidence.hasPrivacyCoverage(8, true));
        assertTrue(Lab30ScoreEvidence.hasPrivacyCoverage(8, false));
    }

    @Test public void missingCoreEvidenceBlocksCombinedScores() {
        assertEquals(-1, Lab30ScoreEvidence.performanceScore(100,100,100,-1));
        assertEquals(-1, Lab30ScoreEvidence.deviceHealthScore(-1,100,100,100));
        assertEquals(-1, Lab30ScoreEvidence.deviceHealthScore(100,-1,100,100));
        assertEquals(100, Lab30ScoreEvidence.performanceScore(100,100,100,100));
        assertEquals(100, Lab30ScoreEvidence.deviceHealthScore(100,100,100,100));
    }
}
