package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/** Regression cases for source-level Lab 14A telemetry defects B14-01..16. */
public class BatteryEvidenceTest {
    @Test public void currentMicroAmpsAreAlwaysConvertedBy1000() {
        assertEquals(5.0, BatteryEvidence.microAmpsToMilliAmps(5000L), 0.0);
        assertEquals(-350.0, BatteryEvidence.microAmpsToMilliAmps(-350000L), 0.0);
        assertEquals(0.0, BatteryEvidence.microAmpsToMilliAmps(0L), 0.0);
    }

    @Test public void unsupportedBatteryManagerCurrentStaysUnknown() {
        assertTrue(Double.isNaN(BatteryEvidence.microAmpsToMilliAmps(Long.MIN_VALUE)));
    }

    @Test public void chargeAndEnergyUnitsNeverMix() {
        assertEquals(4200.0,
                BatteryEvidence.microAmpHoursToMilliAmpHours(4200000L), 0.0);
        assertEquals(15000.0,
                BatteryEvidence.nanoWattHoursToMilliWattHours(15000000000L), 0.0);
        assertTrue(Double.isNaN(BatteryEvidence.microAmpHoursToMilliAmpHours(-1L)));
        assertTrue(Double.isNaN(BatteryEvidence.nanoWattHoursToMilliWattHours(-1L)));
    }

    @Test public void rawSysfsParsingDoesNotScaleOrDropNegativeCurrent() {
        assertEquals(-350000L, BatteryEvidence.parseSignedInteger("-350000"));
        assertEquals(3850000L, BatteryEvidence.parseSignedInteger("3850000\n"));
        assertEquals(0L, BatteryEvidence.parseSignedInteger("0"));
    }

    @Test(expected = NumberFormatException.class)
    public void malformedRawSysfsValueFailsClosed() {
        BatteryEvidence.parseSignedInteger("3850mV");
    }

    @Test public void unavailableIsNotObservedOrAZero() {
        BatteryEvidence value = BatteryEvidence.unavailable(
                "capacity", "mAh", "BatteryManager", "Unsupported", 1L);
        assertFalse(value.isObserved());
        assertTrue(Double.isNaN(value.value));
        assertEquals(BatteryEvidence.State.UNAVAILABLE, value.state);
    }

    @Test public void nonFiniteValuesAreInvalid() {
        BatteryEvidence value = BatteryEvidence.observed(
                "current", "mA", "BatteryManager", Double.NaN, 1L);
        assertEquals(BatteryEvidence.State.INVALID, value.state);
    }

    @Test public void sohNeedsBothVerifiedCapacityInputs() {
        assertTrue(Double.isNaN(
                BatteryEvidence.capacityRatioPercent(Double.NaN, 5000)));
        assertTrue(Double.isNaN(
                BatteryEvidence.capacityRatioPercent(5000, 0)));
        assertEquals(86.0,
                BatteryEvidence.capacityRatioPercent(4300, 5000), 0.001);
        assertTrue(Double.isNaN(
                BatteryEvidence.capacityRatioPercent(15000, 5000)));
    }
}
