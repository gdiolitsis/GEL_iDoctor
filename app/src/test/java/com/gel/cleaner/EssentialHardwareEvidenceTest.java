package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public final class EssentialHardwareEvidenceTest {
    @Test public void gyroRequiresActualAngularSpeed() {
        assertFalse(EssentialHardwareEvidence.gyroMoved(new float[]{0, 0, 0}));
        assertFalse(EssentialHardwareEvidence.gyroMoved(new float[]{0.05f, 0.03f, 0.01f}));
        assertTrue(EssentialHardwareEvidence.gyroMoved(new float[]{0.4f, 0, 0}));
        assertFalse(EssentialHardwareEvidence.gyroMoved(new float[]{Float.NaN, 0, 0}));
    }

    @Test public void magnetometerRequiresMeasuredAxisChange() {
        assertFalse(EssentialHardwareEvidence.magneticFieldChanged(new float[]{21, 9, -12},
                new float[]{22, 8, -12}));
        assertTrue(EssentialHardwareEvidence.magneticFieldChanged(new float[]{21, 9, -12},
                new float[]{10, 12, 4}));
        assertFalse(EssentialHardwareEvidence.magneticFieldChanged(null, new float[]{1, 2, 3}));
    }

    @Test public void lightRequiresRealLuxChange() {
        assertFalse(EssentialHardwareEvidence.ambientLightChanged(50, 52));
        assertTrue(EssentialHardwareEvidence.ambientLightChanged(50, 100));
        assertTrue(EssentialHardwareEvidence.ambientLightChanged(3, 15));
        assertFalse(EssentialHardwareEvidence.ambientLightChanged(Float.NaN, 10));
    }

    @Test public void gpsRequiresFreshNonMockValidFix() {
        assertTrue(EssentialHardwareEvidence.newGpsFix(1000, 1100, false, 25f));
        assertFalse(EssentialHardwareEvidence.newGpsFix(1000, 900, false, 25f));
        assertFalse(EssentialHardwareEvidence.newGpsFix(1000, 1200, true, 25f));
        assertFalse(EssentialHardwareEvidence.newGpsFix(1000, 1200, false, Float.NaN));
        assertFalse(EssentialHardwareEvidence.newGpsFix(0, 1200, false, 25f));
    }
}
