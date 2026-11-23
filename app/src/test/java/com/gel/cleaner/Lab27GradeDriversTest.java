package com.gel.cleaner;

import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/** Stored grade factors contain actual functional failures, not transient data. */
public final class Lab27GradeDriversTest {
    @Test public void withoutConfirmedFailureTheCertificateIsAPlusAndFactorsEmpty() {
        List<Lab27GradeDrivers.Driver> reasons =
                Lab27GradeDrivers.fromConfirmedFailures(false);
        assertEquals(Collections.emptyList(), reasons);
        assertEquals("", Lab27GradeDrivers.encode(reasons));
        assertTrue(Lab27GradeDrivers.validEvidence("", 100, false, 0));
    }

    @Test public void confirmedCameraFailureIsTheOnlyCurrentGradeDriver() {
        List<Lab27GradeDrivers.Driver> reasons =
                Lab27GradeDrivers.fromConfirmedFailures(true);
        assertEquals(Collections.singletonList(Lab27GradeDrivers.Driver.CAMERA), reasons);
        assertTrue(Lab27GradeDrivers.Driver.CAMERA.isConfirmedFailure());
        assertTrue(Lab27GradeDrivers.Driver.CAMERA.recommendedCheck(true).contains("Lab 8"));
        assertTrue(Lab27GradeDrivers.Driver.CAMERA.observation(false).contains("confirmed"));
        assertEquals("CAMERA", Lab27GradeDrivers.encode(reasons));
        assertEquals(reasons, Lab27GradeDrivers.decode("CAMERA"));
        assertTrue(Lab27GradeDrivers.validEvidence("CAMERA", 49, true, 1));
    }

    @Test public void oldContextualReasonsAndInconsistentGradesAreRejected() {
        assertFalse(Lab27GradeDrivers.validEvidence(null, 100, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("", 84, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("", 49, true, 1));
        assertFalse(Lab27GradeDrivers.validEvidence("CAMERA", 100, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("CAMERA", 49, true, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("RAM_FREE", 84, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("BATTERY_CHARGE", 84, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("UPTIME_SHORT", 84, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("STORAGE_SPACE", 84, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("THERMAL", 84, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("CAMERA,CAMERA", 49, true, 1));
    }
}
