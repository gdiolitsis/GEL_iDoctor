package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public final class BatteryChargeEvidenceTest {
    @Test public void acceptsOwnThreeMinuteChargeGain() {
        assertTrue(BatteryChargeEvidence.isValidChargeGain(
                2100L, 2190L, 180_000L, false));
    }

    @Test public void rejectsUnknownOrUnchangedGaugeAndDischarge() {
        assertFalse(BatteryChargeEvidence.isValidChargeGain(
                -1L, 2200L, 180_000L, false));
        assertFalse(BatteryChargeEvidence.isValidChargeGain(
                2100L, 2100L, 180_000L, false));
        assertFalse(BatteryChargeEvidence.isValidChargeGain(
                2100L, 2050L, 180_000L, false));
    }

    @Test public void rejectsSessionInterruptionAndTimingOutsideProtocol() {
        assertFalse(BatteryChargeEvidence.isValidChargeGain(
                2100L, 2190L, 180_000L, true));
        assertFalse(BatteryChargeEvidence.isValidChargeGain(
                2100L, 2190L, 30_000L, false));
        assertFalse(BatteryChargeEvidence.isValidChargeGain(
                2100L, 2190L, 1_800_000L, false));
    }

    @Test public void rejectsPhysicallyImplausibleGaugeJumpWithoutClamping() {
        assertFalse(BatteryChargeEvidence.isValidChargeGain(
                2100L, 4200L, 180_000L, false));
    }
}
