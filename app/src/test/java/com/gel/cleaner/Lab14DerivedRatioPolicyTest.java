package com.gel.cleaner;

import org.junit.Test;

import static org.junit.Assert.*;

public class Lab14DerivedRatioPolicyTest {
    @Test public void legacyProxyCannotBeCertifiedResistance() {
        String english = Lab14DerivedRatioPolicy.describeVoltageCurrentProxy(45.0, false);
        String greek = Lab14DerivedRatioPolicy.describeVoltageCurrentProxy(45.0, true);
        assertTrue(english.contains("ΔV/|I|"));
        assertTrue(english.contains("NOT measured battery-cell resistance/SoH") ||
                english.contains("not measured battery-cell resistance/SoH"));
        assertTrue(greek.contains("όχι μετρημένη αντίσταση"));
        for (String quality : new String[]{"Excellent", "Good", "Normal", "High"})
            assertFalse(quality, english.contains(quality));
    }

    @Test public void invalidProxyNeverTurnsIntoZeroOrGood() {
        for (double value : new double[]{Double.NaN, Double.POSITIVE_INFINITY,
                -100.0, 0.0, 0.5, 301.0}) {
            assertFalse(Lab14DerivedRatioPolicy.usableVoltageCurrentProxyMilliOhm(value));
            assertTrue(Lab14DerivedRatioPolicy.describeVoltageCurrentProxy(value, false)
                    .startsWith("UNAVAILABLE"));
        }
    }

    @Test public void exploratoryDrainVoltageRatioNeverClaimsEfficiency() {
        String result = Lab14DerivedRatioPolicy.describeDischargeToSagRatio(1200.0, false);
        assertTrue(result.contains("mAh/(h·V)"));
        assertTrue(result.contains("NOT energy efficiency"));
        assertFalse(result.contains("Efficient"));
        assertFalse(result.contains("Normal"));
        assertFalse(result.contains("Inefficient"));
    }

    @Test public void dischargeRatioMissingInputDoesNotBecomeNormalEfficiency() {
        for (double value : new double[]{Double.NaN, Double.NEGATIVE_INFINITY,
                -1.0, 0.0, 5000.0}) {
            assertFalse(Lab14DerivedRatioPolicy.usableDischargeToSagRatio(value));
            assertTrue(Lab14DerivedRatioPolicy.describeDischargeToSagRatio(value, true)
                    .startsWith("ΜΗ ΔΙΑΘΕΣΙΜΟ"));
        }
    }

    @Test public void physicallyRelevantCurrentStepRequiresTwoAlignedReadings() {
        assertFalse(Lab14DerivedRatioPolicy.hasCurrentStepEvidence(false, -0.5, -1.5));
        assertFalse(Lab14DerivedRatioPolicy.hasCurrentStepEvidence(true, -1.5, -1.5));
        assertFalse(Lab14DerivedRatioPolicy.hasCurrentStepEvidence(true, Double.NaN, -1.5));
        assertFalse(Lab14DerivedRatioPolicy.hasCurrentStepEvidence(true, -0.5, Double.NaN));
        assertTrue(Lab14DerivedRatioPolicy.hasCurrentStepEvidence(true, -0.5, -1.5));
    }
}
