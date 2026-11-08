package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab30ScoreEvidenceTest {
    @Test public void missingInputsCannotBeConvertedToPerfectHealth() {
        assertFalse(Lab30ScoreEvidence.hasThermalReadings(Float.NaN, Float.NaN));
        assertFalse(Lab30ScoreEvidence.hasThermalReadings(-1f, -1f));
        assertFalse(Lab30ScoreEvidence.hasBatteryReadings(Float.NaN, 75f));
        assertFalse(Lab30ScoreEvidence.hasBatteryReadings(Float.NaN, -1f));
        assertTrue(Lab30ScoreEvidence.hasThermalReadings(35f, 31f));
        assertTrue(Lab30ScoreEvidence.hasBatteryReadings(30f, 80f));
    }
    @Test public void missingCoreEvidenceBlocksCombinedScores() {
        assertEquals(-1, Lab30ScoreEvidence.performanceScore(100,100,100,-1));
        assertEquals(-1, Lab30ScoreEvidence.deviceHealthScore(-1,100,100,100));
        assertEquals(-1, Lab30ScoreEvidence.deviceHealthScore(100,-1,100,100));
        assertEquals(100, Lab30ScoreEvidence.performanceScore(100,100,100,100));
        assertEquals(100, Lab30ScoreEvidence.deviceHealthScore(100,100,100,100));
    }
}
