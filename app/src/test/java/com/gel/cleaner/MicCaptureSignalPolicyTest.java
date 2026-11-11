package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class MicCaptureSignalPolicyTest {
    @Test public void nearZeroIsRejectedAndFloorIsInclusive() {
        assertFalse(MicCaptureSignalPolicy.aboveMinimalFloor(0, 0));
        assertFalse(MicCaptureSignalPolicy.aboveMinimalFloor(0.01, 1));
        assertFalse(MicCaptureSignalPolicy.aboveMinimalFloor(19.99, 30));
        assertTrue(MicCaptureSignalPolicy.aboveMinimalFloor(20.0, 30));
    }
    @Test public void acceptedLowAmplitudeIsNotDiscarded() {
        assertTrue(MicCaptureSignalPolicy.aboveMinimalFloor(90.2, 300));
        assertTrue(MicCaptureSignalPolicy.aboveMinimalFloor(151.4, 841));
    }
    @Test public void invalidRmsAndPeakDoNotCreateSignalEvidence() {
        assertFalse(MicCaptureSignalPolicy.aboveMinimalFloor(Double.NaN, 100));
        assertFalse(MicCaptureSignalPolicy.aboveMinimalFloor(Double.POSITIVE_INFINITY, 100));
        assertFalse(MicCaptureSignalPolicy.aboveMinimalFloor(20, Double.NaN));
        assertFalse(MicCaptureSignalPolicy.aboveMinimalFloor(20, 0));
    }
}
