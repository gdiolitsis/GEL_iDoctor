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

    @Test public void measuredDeviceGradesRetainOriginalBandsWithoutGuessingMissingData() {
        assertNull(Lab30ScoreEvidence.gradeForMeasuredSnapshot(-1));
        assertNull(Lab30ScoreEvidence.gradeForMeasuredSnapshot(101));
        assertEquals("D", Lab30ScoreEvidence.gradeForMeasuredSnapshot(0));
        assertEquals("D", Lab30ScoreEvidence.gradeForMeasuredSnapshot(54));
        assertEquals("C", Lab30ScoreEvidence.gradeForMeasuredSnapshot(55));
        assertEquals("C", Lab30ScoreEvidence.gradeForMeasuredSnapshot(69));
        assertEquals("B", Lab30ScoreEvidence.gradeForMeasuredSnapshot(70));
        assertEquals("B", Lab30ScoreEvidence.gradeForMeasuredSnapshot(84));
        assertEquals("A", Lab30ScoreEvidence.gradeForMeasuredSnapshot(85));
        assertEquals("A", Lab30ScoreEvidence.gradeForMeasuredSnapshot(91));
        assertEquals("A+", Lab30ScoreEvidence.gradeForMeasuredSnapshot(92));
        assertEquals("A+", Lab30ScoreEvidence.gradeForMeasuredSnapshot(100));
    }

    @Test public void certificationPerformanceIgnoresAppInventory() {
        assertEquals(100, Lab30ScoreEvidence.functionalPerformanceScore(100,100,100));
        assertEquals(-1, Lab30ScoreEvidence.functionalPerformanceScore(-1,100,100));
        assertEquals(-1, Lab30ScoreEvidence.functionalPerformanceScore(100,-1,100));
        assertEquals(-1, Lab30ScoreEvidence.functionalPerformanceScore(100,100,-1));
        assertEquals(0, Lab30ScoreEvidence.functionalPerformanceScore(0,0,0));
        // 15% app weighting from the historical score is deliberately excluded.
        assertEquals(82, Lab30ScoreEvidence.functionalPerformanceScore(100,100,0));
    }

    @Test public void missingCoreEvidenceBlocksCombinedScores() {
        assertEquals(-1, Lab30ScoreEvidence.performanceScore(100,100,100,-1));
        assertEquals(-1, Lab30ScoreEvidence.deviceHealthScore(-1,100,100,100));
        assertEquals(-1, Lab30ScoreEvidence.deviceHealthScore(100,-1,100,100));
        assertEquals(100, Lab30ScoreEvidence.performanceScore(100,100,100,100));
        assertEquals(100, Lab30ScoreEvidence.deviceHealthScore(100,100,100,100));
    }
}
