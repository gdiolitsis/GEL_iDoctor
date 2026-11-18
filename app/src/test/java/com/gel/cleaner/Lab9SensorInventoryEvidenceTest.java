package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public final class Lab9SensorInventoryEvidenceTest {
    @Test public void xiaomiRedmiLargeAreaDetectVendorMustBeRecognized() {
        assertTrue(Lab9SensorInventoryEvidence.isLargeAreaTouch(
                "Touch Sensor", "Xiaomi Large Area Detect"));
        assertTrue(Lab9SensorInventoryEvidence.isLargeAreaTouch(
                "Touch Sensor", "Xiaomi Large Area Detect".toLowerCase()));
    }

    @Test public void existingLiteralNameStillMatches() {
        assertTrue(Lab9SensorInventoryEvidence.isLargeAreaTouch(
                "Large Area Touch Sensor", "Xiaomi"));
        assertTrue(Lab9SensorInventoryEvidence.isLargeAreaTouch(
                "Touch Sensor", "Large Touch controller"));
    }

    @Test public void genericTouchOrLargeAreaCannotProduceFalseGreen() {
        assertFalse(Lab9SensorInventoryEvidence.isLargeAreaTouch("Touch Sensor", "Xiaomi"));
        assertFalse(Lab9SensorInventoryEvidence.isLargeAreaTouch("Proximity Sensor", "Large Area Detect"));
        assertFalse(Lab9SensorInventoryEvidence.isLargeAreaTouch("Touch Sensor", null));
        assertFalse(Lab9SensorInventoryEvidence.isLargeAreaTouch(null, null));
        assertFalse(Lab9SensorInventoryEvidence.isLargeAreaTouch("virtual_gyroscope", "Xiaomi"));
    }
}
