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
}
