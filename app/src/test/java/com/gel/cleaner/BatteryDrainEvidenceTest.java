package com.gel.cleaner;

import static org.junit.Assert.*;
import org.junit.Test;

/** Charge-counter + monotonic-time regression tests for Lab 14B. */
public final class BatteryDrainEvidenceTest {
    @Test public void measuresActualFiveMinuteDischargeRate() {
        assertEquals(120.0, BatteryDrainEvidence.dischargeMilliAmpHoursPerHour(
                4200L, 4190L, 300_000L, false, false), 1e-8);
    }
    @Test public void rejectsChargingAndRisingChargeCounter() {
        assertTrue(Double.isNaN(BatteryDrainEvidence.dischargeMilliAmpHoursPerHour(
                4200L, 4190L, 300_000L, true, false)));
        assertTrue(Double.isNaN(BatteryDrainEvidence.dischargeMilliAmpHoursPerHour(
                4200L, 4190L, 300_000L, false, true)));
        assertTrue(Double.isNaN(BatteryDrainEvidence.dischargeMilliAmpHoursPerHour(
                4190L, 4200L, 300_000L, false, false)));
    }
    @Test public void rejectsMissingOrUnchangedCounters() {
        assertTrue(Double.isNaN(BatteryDrainEvidence.dischargeMilliAmpHoursPerHour(
                -1L, 4100L, 300_000L, false, false)));
        assertTrue(Double.isNaN(BatteryDrainEvidence.dischargeMilliAmpHoursPerHour(
                4200L, 4200L, 300_000L, false, false)));
    }
    @Test public void rejectsShortAndImpossibleSessionsWithoutManufacturingRates() {
        assertTrue(Double.isNaN(BatteryDrainEvidence.dischargeMilliAmpHoursPerHour(
                4200L, 4150L, 10_000L, false, false)));
        assertTrue(Double.isNaN(BatteryDrainEvidence.dischargeMilliAmpHoursPerHour(
                4200L, 100L, 300_000L, false, false)));
    }
}
