package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab7SensorEvidenceTest {
    @Test public void gravityPoseDistinguishesLandscapeFromPortrait() {
        assertEquals(Lab7SensorEvidence.Orientation.PORTRAIT,
                Lab7SensorEvidence.gravityOrientation(0.3f, 9.6f, 0.6f));
        assertEquals(Lab7SensorEvidence.Orientation.LANDSCAPE,
                Lab7SensorEvidence.gravityOrientation(9.4f, 0.4f, 0.8f));
    }

    @Test public void shakeAndFlatFaceUpAreNotScreenRotations() {
        assertEquals(Lab7SensorEvidence.Orientation.OTHER_OR_UNSTABLE,
                Lab7SensorEvidence.gravityOrientation(20f, 0f, 0f));
        assertEquals(Lab7SensorEvidence.Orientation.OTHER_OR_UNSTABLE,
                Lab7SensorEvidence.gravityOrientation(0f, 0f, 9.8f));
        assertEquals(Lab7SensorEvidence.Orientation.OTHER_OR_UNSTABLE,
                Lab7SensorEvidence.gravityOrientation(Float.NaN, 9.8f, 0f));
    }

    @Test public void proximityCanBeBinaryOrMaximumRange() {
        assertEquals(Lab7SensorEvidence.Proximity.NEAR,
                Lab7SensorEvidence.proximityState(0.0f, 5.0f));
        assertEquals(Lab7SensorEvidence.Proximity.FAR,
                Lab7SensorEvidence.proximityState(5.0f, 5.0f));
        assertEquals(Lab7SensorEvidence.Proximity.NEAR,
                Lab7SensorEvidence.proximityState(1f, 10f));
        assertEquals(Lab7SensorEvidence.Proximity.FAR,
                Lab7SensorEvidence.proximityState(10f, 10f));
    }

    @Test public void unknownProximityNeverReturnsHealthyFarState() {
        assertEquals(Lab7SensorEvidence.Proximity.UNKNOWN,
                Lab7SensorEvidence.proximityState(Float.NaN, 5f));
        assertEquals(Lab7SensorEvidence.Proximity.UNKNOWN,
                Lab7SensorEvidence.proximityState(0f, 0f));
        assertEquals(Lab7SensorEvidence.Proximity.UNKNOWN,
                Lab7SensorEvidence.proximityState(-1f, 5f));
    }

    @Test public void orientationRequiresTimedStablePortraitThenLandscape() {
        Lab7SensorEvidence.OrientationSequence seq =
                new Lab7SensorEvidence.OrientationSequence();
        long start = 1_000_000_000L;
        for (int i = 0; i < 5; i++) {
            assertFalse(seq.observe(0.2f, 9.7f, 0.1f, start + i * 220_000_000L));
        }
        assertTrue(seq.sawStablePortrait());
        for (int i = 0; i < 4; i++) {
            assertFalse(seq.observe(9.7f, 0.2f, 0.1f,
                    start + 1_200_000_000L + i * 220_000_000L));
        }
        assertTrue(seq.observe(9.7f, 0.2f, 0.1f, start + 2_080_000_000L));
        assertTrue(seq.completed());
    }

    @Test public void shakeOrLandscapeFirstCannotBypassPortrait() {
        Lab7SensorEvidence.OrientationSequence seq =
                new Lab7SensorEvidence.OrientationSequence();
        long start = 1_000_000_000L;
        for (int i = 0; i < 8; i++) {
            assertFalse(seq.observe(9.8f, 0f, 0f, start + i * 200_000_000L));
        }
        assertFalse(seq.sawStablePortrait());
        for (int i = 0; i < 6; i++) {
            assertFalse(seq.observe(i % 2 == 0 ? 20f : 0f, 0f, 0f,
                    start + 2_000_000_000L + i * 200_000_000L));
        }
        assertFalse(seq.completed());
    }

    @Test public void orientationNeedsElapsedTimeNotThreeQuickEvents() {
        Lab7SensorEvidence.OrientationSequence seq =
                new Lab7SensorEvidence.OrientationSequence();
        for (int i = 0; i < 10; i++) {
            assertFalse(seq.observe(0f, 9.8f, 0f, 1_000_000_000L + i * 10_000_000L));
        }
        assertFalse(seq.sawStablePortrait());
        seq.reset();
        for (int i = 0; i < 5; i++) {
            assertFalse(seq.observe(0f, 9.8f, 0f,
                    2_000_000_000L + i * 220_000_000L));
        }
        assertTrue(seq.sawStablePortrait());
        seq.reset();
        assertFalse(seq.sawStablePortrait());
        assertFalse(seq.completed());
    }

    @Test public void orientationGapsAndJitterInvalidateStability() {
        Lab7SensorEvidence.OrientationSequence seq =
                new Lab7SensorEvidence.OrientationSequence();
        assertFalse(seq.observe(0f, 9.8f, 0f, 1_000_000_000L));
        assertFalse(seq.observe(0f, 9.8f, 0f, 1_100_000_000L));
        assertFalse(seq.observe(0f, 9.8f, 0f, 1_100_000_000L)); // duplicate time
        assertFalse(seq.observe(0f, 9.8f, 0f, 2_000_000_000L)); // long gap
        assertFalse(seq.observe(2f, 9.0f, 0f, 2_200_000_000L)); // unstable vector
        assertFalse(seq.sawStablePortrait());
    }

    @Test public void proximityRequiresOrderedFarNearFarEvents() {
        Lab7SensorEvidence.ProximitySequence seq =
                new Lab7SensorEvidence.ProximitySequence();
        assertFalse(seq.observe(Lab7SensorEvidence.Proximity.NEAR, 1_000_000_000L));
        assertEquals(0, seq.stage());
        assertFalse(seq.observe(Lab7SensorEvidence.Proximity.FAR, 1_300_000_000L));
        assertFalse(seq.observe(Lab7SensorEvidence.Proximity.FAR, 1_600_000_000L));
        assertEquals(1, seq.stage());
        assertFalse(seq.observe(Lab7SensorEvidence.Proximity.NEAR, 2_000_000_000L));
        assertEquals(2, seq.stage());
        assertTrue(seq.observe(Lab7SensorEvidence.Proximity.FAR, 2_400_000_000L));
        assertTrue(seq.completed());
    }

    @Test public void proximityNoPassFromFastBounceOrDuplicateTimestamp() {
        Lab7SensorEvidence.ProximitySequence seq =
                new Lab7SensorEvidence.ProximitySequence();
        assertFalse(seq.observe(Lab7SensorEvidence.Proximity.FAR, 1_000_000_000L));
        assertFalse(seq.observe(Lab7SensorEvidence.Proximity.NEAR, 1_050_000_000L));
        assertEquals(1, seq.stage()); // too-fast transition ignored
        assertFalse(seq.observe(Lab7SensorEvidence.Proximity.UNKNOWN, 1_400_000_000L));
        assertFalse(seq.observe(Lab7SensorEvidence.Proximity.NEAR, 1_500_000_000L));
        assertFalse(seq.observe(Lab7SensorEvidence.Proximity.FAR, 1_500_000_000L));
        assertEquals(2, seq.stage());
        assertFalse(seq.observe(Lab7SensorEvidence.Proximity.FAR, 1_550_000_000L));
        assertFalse(seq.completed());
        seq.reset();
        assertFalse(seq.completed());
        assertEquals(0, seq.stage());
    }
}
