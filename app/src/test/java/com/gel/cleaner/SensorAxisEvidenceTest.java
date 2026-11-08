package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/** Lab 9 falsification cases: changing one axis is evidence, not hardware health. */
public class SensorAxisEvidenceTest {
    @Test public void yAxisOnlyVariationIsObserved() {
        SensorAxisEvidence evidence = new SensorAxisEvidence(3);
        assertTrue(evidence.observe(new float[]{9.8f, 0f, 0f}, 1_000_000_000L));
        assertTrue(evidence.observe(new float[]{9.8f, 1.0f, 0f}, 1_200_000_000L));
        assertEquals(2, evidence.validEvents());
        assertTrue(evidence.observedVariation(0.001f));
        assertEquals("Y", evidence.variedAxes(0.001f));
    }

    @Test public void zAxisOnlyVariationIsObserved() {
        SensorAxisEvidence evidence = new SensorAxisEvidence(3);
        assertTrue(evidence.observe(new float[]{0f, 0f, 0f}, 1_000_000_000L));
        assertTrue(evidence.observe(new float[]{0f, 0f, -0.25f}, 1_200_000_000L));
        assertTrue(evidence.observedVariation(0.001f));
        assertEquals("Z", evidence.variedAxes(0.001f));
    }

    @Test public void stationaryStreamIsNotFabricatedIntoMovement() {
        SensorAxisEvidence evidence = new SensorAxisEvidence(3);
        assertTrue(evidence.observe(new float[]{0f, 9.8f, 0f}, 1_000_000_000L));
        assertTrue(evidence.observe(new float[]{0f, 9.8f, 0f}, 1_100_000_000L));
        assertEquals(2, evidence.validEvents());
        assertEquals(0, evidence.invalidEvents());
        assertFalse(evidence.observedVariation(0f)); // not a physical-failure conclusion
        assertEquals("", evidence.variedAxes(0f));
    }

    @Test public void nonfiniteSecondaryAxisAndShortVectorAreInvalid() {
        SensorAxisEvidence evidence = new SensorAxisEvidence(3);
        assertTrue(evidence.observe(new float[]{1f, 2f, 3f}, 1_000_000_000L));
        assertFalse(evidence.observe(new float[]{1f, Float.NaN, 3f}, 1_100_000_000L));
        assertFalse(evidence.observe(new float[]{1f, 2f, Float.POSITIVE_INFINITY},
                1_200_000_000L));
        assertFalse(evidence.observe(new float[]{1f}, 1_300_000_000L));
        assertFalse(evidence.observe(null, 1_400_000_000L));
        assertEquals(1, evidence.validEvents());
        assertEquals(4, evidence.invalidEvents());
        assertFalse(evidence.observedVariation(0f));
    }

    @Test public void staleAndDuplicatedTimestampsDoNotPolluteExtrema() {
        SensorAxisEvidence evidence = new SensorAxisEvidence(3);
        assertTrue(evidence.observe(new float[]{0f, 0f, 0f}, 1_000_000_000L));
        assertTrue(evidence.observe(new float[]{0f, 0f, 0f}, 1_100_000_000L));
        assertFalse(evidence.observe(new float[]{0f, 100f, 0f}, 1_100_000_000L));
        assertFalse(evidence.observe(new float[]{0f, 100f, 0f}, 1_050_000_000L));
        assertTrue(evidence.observe(new float[]{0f, 0f, 0f}, 1_200_000_000L));
        assertEquals(3, evidence.validEvents());
        assertEquals(2, evidence.invalidEvents());
        assertFalse(evidence.timestampsMonotonic());
        assertFalse(evidence.observedVariation(0f));
    }

    @Test public void lightAndProximityUseSingleAxis() {
        SensorAxisEvidence light = new SensorAxisEvidence(1);
        assertTrue(light.observe(new float[]{12f}, 1_000_000_000L));
        assertTrue(light.observe(new float[]{18f}, 1_200_000_000L));
        assertTrue(light.observedVariation(0.1f));
        assertEquals("VALUE", light.variedAxes(0.1f));

        SensorAxisEvidence proximity = new SensorAxisEvidence(1);
        assertTrue(proximity.observe(new float[]{5f}, 1_000_000_000L));
        assertTrue(proximity.observe(new float[]{0f}, 1_400_000_000L));
        assertTrue(proximity.observedVariation(0.1f));
        assertEquals("VALUE", proximity.variedAxes(0.1f));
    }

    @Test public void smallJitterBelowResolutionBoundIsNotVariation() {
        SensorAxisEvidence evidence = new SensorAxisEvidence(3);
        assertTrue(evidence.observe(new float[]{0f, 0f, 0f}, 1_000_000_000L));
        assertTrue(evidence.observe(new float[]{0.025f, 0f, 0f}, 1_100_000_000L));
        assertFalse(evidence.observedVariation(0.02f)); // threshold = 0.04
        assertTrue(evidence.observe(new float[]{0.1f, 0f, 0f}, 1_200_000_000L));
        assertTrue(evidence.observedVariation(0.02f));
    }

    @Test public void noEventsOrOnlyMalformedEventsNeverSuggestVariation() {
        SensorAxisEvidence evidence = new SensorAxisEvidence(3);
        assertFalse(evidence.observedVariation(Float.NaN));
        assertFalse(evidence.observe(new float[]{1f, 2f, 3f}, 0L));
        assertFalse(evidence.observe(new float[]{1f, 2f, 3f}, -1L));
        assertFalse(evidence.observe(new float[]{Float.NaN, 2f, 3f}, 1_000_000_000L));
        assertEquals(0, evidence.validEvents());
        assertEquals(3, evidence.invalidEvents());
        assertFalse(evidence.observedVariation(0f));
    }

    @Test public void signedMeasurementsAreValidAndUnknownDimensionalityRejected() {
        SensorAxisEvidence gyro = new SensorAxisEvidence(3);
        assertTrue(gyro.observe(new float[]{-0.25f, 0f, 0f}, 1_000_000_000L));
        assertTrue(gyro.observe(new float[]{0.25f, 0f, 0f}, 1_200_000_000L));
        assertTrue(gyro.observedVariation(0f));
        try {
            new SensorAxisEvidence(2);
            fail("Unsupported number of dimensions accepted");
        } catch (IllegalArgumentException expected) {
            // Only single-axis and 3-axis sensors are currently sampled.
        }
    }
}
