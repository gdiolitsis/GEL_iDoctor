package com.gel.cleaner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public final class Lab27GradeDriversTest {
    private static final long GIB = 1024L * 1024L * 1024L;

    @Test public void healthyRedmiSnapshotHasNoGradeDeductions() {
        List<Lab27GradeDrivers.Driver> factors =
                Lab27GradeDrivers.fromSnapshot(
                        38.9f, 35.5f, 32f, 92, false,
                        65, 107L * GIB, 33, 3L * 24L * 3600L * 1000L, false);
        assertTrue(factors.isEmpty());
        assertEquals("", Lab27GradeDrivers.encode(factors));
        assertTrue(Lab27GradeDrivers.validEvidence("", 100, false, 0));
    }

    @Test public void recordsExactlyTheCategoriesWhichScoringPenalizes() {
        List<Lab27GradeDrivers.Driver> factors =
                Lab27GradeDrivers.fromSnapshot(
                        61f, 47f, 45f, 12, false,
                        9, 31L * GIB, 11, 15L * 60L * 1000L, true);
        assertEquals(Arrays.asList(
                Lab27GradeDrivers.Driver.CAMERA,
                Lab27GradeDrivers.Driver.THERMAL,
                Lab27GradeDrivers.Driver.BATTERY_TEMPERATURE,
                Lab27GradeDrivers.Driver.BATTERY_CHARGE,
                Lab27GradeDrivers.Driver.STORAGE_SPACE,
                Lab27GradeDrivers.Driver.STORAGE_CAPACITY,
                Lab27GradeDrivers.Driver.RAM_FREE,
                Lab27GradeDrivers.Driver.UPTIME_SHORT), factors);
        String snapshot = Lab27GradeDrivers.encode(factors);
        assertEquals(factors, Lab27GradeDrivers.decode(snapshot));
        assertTrue(Lab27GradeDrivers.validEvidence(snapshot, 49, true, 1));
        assertTrue(factors.get(0).isConfirmedFailure());
        for (Lab27GradeDrivers.Driver factor : factors.subList(1, factors.size())) {
            assertFalse(factor.isConfirmedFailure());
            assertFalse(factor.observation(true).trim().isEmpty());
            assertFalse(factor.recommendedCheck(true).trim().isEmpty());
        }
    }

    @Test public void contextualLowChargeOrUptimeIsNotReportedAsProvenHardwareFailure() {
        List<Lab27GradeDrivers.Driver> factors =
                Lab27GradeDrivers.fromSnapshot(
                        38f, 35f, 32f, 20, false,
                        64, 100L * GIB, 44, 11L * 24L * 3600L * 1000L, false);
        assertTrue(factors.contains(Lab27GradeDrivers.Driver.BATTERY_CHARGE));
        assertTrue(factors.contains(Lab27GradeDrivers.Driver.UPTIME_LONG));
        assertFalse(factors.contains(Lab27GradeDrivers.Driver.CAMERA));
        assertTrue(Lab27GradeDrivers.Driver.BATTERY_CHARGE.observation(true)
                .contains("Δεν αποδεικνύει φθορά"));
        assertTrue(Lab27GradeDrivers.Driver.UPTIME_LONG.observation(false)
                .contains("does not prove instability"));
    }

    @Test public void invalidOrOldCertificatesCannotClaimNewGradingReasons() {
        assertFalse(Lab27GradeDrivers.validEvidence(null, 84, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("", 84, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("CAMERA", 49, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("RAM_FREE", 49, true, 1));
        assertFalse(Lab27GradeDrivers.validEvidence("CAMERA", 49, true, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("CAMERA,CAMERA", 49, true, 1));
        assertFalse(Lab27GradeDrivers.validEvidence("UPTIME_SHORT,UPTIME_LONG", 90, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("UNKNOWN", 90, false, 0));
        assertTrue(Lab27GradeDrivers.validEvidence("RAM_FREE", 89, false, 0));
        assertTrue(Lab27GradeDrivers.validEvidence("CAMERA", 49, true, 1));
        assertFalse(Lab27GradeDrivers.validEvidence("", -1, false, 0));
        assertEquals(Collections.emptyList(), Lab27GradeDrivers.decode(""));
    }
}
