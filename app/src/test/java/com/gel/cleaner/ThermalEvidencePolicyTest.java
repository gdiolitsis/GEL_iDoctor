package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ThermalEvidencePolicyTest {
    @Test public void absentMeasurementsMustNotProducePerfectHealth() {
        assertEquals(-1, ThermalEvidencePolicy.score(0, 100, false));
        assertEquals("UNAVAILABLE", ThermalEvidencePolicy.pattern(-1, false));
    }

    @Test public void visibleReadingsCanBeScoredButDoNotProveFullCoverage() {
        assertEquals(100, ThermalEvidencePolicy.score(1, 100, false));
        assertEquals(80, ThermalEvidencePolicy.score(2, 80, false));
        assertEquals("NO_ELEVATED_READING_OBSERVED",
                ThermalEvidencePolicy.pattern(100, false));
    }

    @Test public void hiddenHighSensorRemainsAWarningEvenIfNotDisplayed() {
        assertEquals(75, ThermalEvidencePolicy.score(0, 100, true));
        assertEquals(50, ThermalEvidencePolicy.score(2, 50, true));
        assertEquals("ELEVATED_READING_OBSERVED",
                ThermalEvidencePolicy.pattern(75, true));
    }
}
