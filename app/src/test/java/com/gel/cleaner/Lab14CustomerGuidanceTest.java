package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab14CustomerGuidanceTest {
    @Test public void observedSagFromReportIsRetained() {
        assertEquals(Lab14CustomerGuidance.Evidence.LOAD_VOLTAGE_RESPONSE_OBSERVED,
                Lab14CustomerGuidance.observation(3.926f, 3.759f));
    }
    @Test public void sagReadingKeepsPhysicalUnits() {
        assertTrue(Lab14CustomerGuidance.voltageReading(0.167f, true).contains("0.167 V"));
    }
    @Test public void missingBeforeIsUnknown() {
        assertEquals(Lab14CustomerGuidance.Evidence.UNAVAILABLE,
                Lab14CustomerGuidance.observation(Float.NaN, 3.8f));
    }
    @Test public void missingUnderLoadIsUnknown() {
        assertEquals(Lab14CustomerGuidance.Evidence.UNAVAILABLE,
                Lab14CustomerGuidance.observation(3.9f, Float.NaN));
    }
    @Test public void outOfRangeVoltageIsUnknown() {
        assertEquals(Lab14CustomerGuidance.Evidence.UNAVAILABLE,
                Lab14CustomerGuidance.observation(11f, 3.7f));
    }
    @Test public void reverseVoltageIsNotSag() {
        assertEquals(Lab14CustomerGuidance.Evidence.UNAVAILABLE,
                Lab14CustomerGuidance.observation(3.8f, 3.9f));
    }
    @Test public void implausibleSagIsUnavailable() {
        assertEquals(Lab14CustomerGuidance.Evidence.UNAVAILABLE,
                Lab14CustomerGuidance.observation(4.7f, 3.0f));
    }
    @Test public void validSagIsNotAFullBatteryHealthVerdict() {
        String label = Lab14CustomerGuidance.evidenceLabel(true, false, true);
        assertTrue(label.contains("ΠΑΡΑΤΗΡΟΥΜΕΝΗ"));
        assertFalse(label.contains("Critical"));
    }
    @Test public void systemLimitedCannotHaveHealthVerdict() {
        assertEquals("INSUFFICIENT EVIDENCE",
                Lab14CustomerGuidance.evidenceLabel(false, true, true));
    }
    @Test public void noSagCannotHaveHealthVerdict() {
        assertEquals("INSUFFICIENT EVIDENCE",
                Lab14CustomerGuidance.evidenceLabel(false, false, false));
    }
    @Test public void greekAdvicePreservesTechnicianCheck() {
        assertTrue(Lab14CustomerGuidance.recommendation(true, false, true).contains("τεχνικός έλεγχος"));
    }
    @Test public void englishAdvicePreservesTechnicianCheck() {
        assertTrue(Lab14CustomerGuidance.recommendation(false, false, true).contains("technician inspection"));
    }
    @Test public void bothLanguagesAvoidReplacementProposal() {
        for (boolean gr : new boolean[]{true, false}) {
            for (boolean limited : new boolean[]{true, false}) {
                for (boolean measured : new boolean[]{true, false}) {
                    String advice = Lab14CustomerGuidance.recommendation(gr, limited, measured);
                    assertFalse(advice.contains("αντικατάστα"));
                    assertFalse(advice.toLowerCase(java.util.Locale.US).contains("replace"));
                }
            }
        }
    }
    @Test public void recordedSagNeverClaimsMeasuredCapacityLoss() {
        assertTrue(Lab14CustomerGuidance.recommendation(true, false, true)
                .contains("δεν επιβεβαιώθηκε"));
    }
    @Test public void fallbackReadingRejectsInfinity() {
        assertTrue(Lab14CustomerGuidance.voltageReading(Float.POSITIVE_INFINITY, false)
                .contains("No reliable"));
    }
    @Test public void technicianOrPhysicalCheckNeededForCells() {
        assertTrue(Lab14CustomerGuidance.unverifiedCellCondition(true).contains("φυσικός έλεγχος"));
    }
    @Test public void noAutoReplacementStateExists() {
        assertFalse(Lab14CustomerGuidance.permitsReplacementRecommendation());
    }
}
