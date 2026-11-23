package com.gel.cleaner;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public final class Lab27GradeDriversTest {
    private float[] temperatures(float battery, float cpu, float gpu,
                                 float charger, float pmic, float surface) {
        return new float[]{battery, cpu, gpu, charger, pmic, surface};
    }

    @Test public void normalAndElevatedScreeningReadingsDoNotReduceCertification() {
        // WATCH is intentionally NOT the HIGH band for any of these sectors.
        List<Lab27GradeDrivers.Driver> factors = Lab27GradeDrivers.fromOperationalSnapshot(
                false, Lab16ThermalCorrelation.OS_MODERATE,
                temperatures(45f, 78f, 80f, 52f, 70f, 43f), false, false);
        assertTrue(factors.isEmpty());
        assertEquals(100, Lab27FunctionGradePolicy.apply(factors));
        assertEquals("A+", Lab30ScoreEvidence.gradeForMeasuredSnapshot(
                Lab27FunctionGradePolicy.apply(factors)));
        assertTrue(Lab27GradeDrivers.validEvidence("", 100, false, 0));
    }

    @Test public void veryHighZoneTemperatureTriggersTargetedCGradeRecheck() {
        List<Lab27GradeDrivers.Driver> drivers = Lab27GradeDrivers.fromOperationalSnapshot(
                false, -1, temperatures(48f, 90f, Float.NaN, 30f, 60f, 30f),
                false, false);
        assertTrue(drivers.contains(Lab27GradeDrivers.Driver.THERMAL_HIGH_BATTERY));
        assertTrue(drivers.contains(Lab27GradeDrivers.Driver.THERMAL_HIGH_CPU));
        assertEquals(65, Lab27FunctionGradePolicy.apply(drivers));
        assertEquals("C", Lab30ScoreEvidence.gradeForMeasuredSnapshot(
                Lab27FunctionGradePolicy.apply(drivers)));
        assertEquals(0, Lab27GradeDrivers.confirmedFailures(drivers));
        assertTrue(Lab27GradeDrivers.Driver.THERMAL_HIGH_BATTERY
                .recommendedCheck(true).contains("Lab 16"));
        assertFalse(Lab27GradeDrivers.Driver.THERMAL_HIGH_CPU.isConfirmedFailure());
    }

    @Test public void actualSevereAndroidThermalProtectionCapsGradeAtD() {
        List<Lab27GradeDrivers.Driver> drivers = Lab27GradeDrivers.fromOperationalSnapshot(
                false, Lab16ThermalCorrelation.OS_SEVERE, null, false, false);
        assertEquals(49, Lab27FunctionGradePolicy.apply(drivers));
        assertTrue(drivers.contains(Lab27GradeDrivers.Driver.THERMAL_OS_SEVERE));
        assertEquals(1, Lab27GradeDrivers.confirmedFailures(drivers));
        assertTrue(Lab27GradeDrivers.Driver.THERMAL_OS_SEVERE.isConfirmedFailure());
    }

    @Test public void lab15ObservedChargingProblemsAffectGradeButNotBatteryPercentage() {
        List<Lab27GradeDrivers.Driver> hot = Lab27GradeDrivers.fromOperationalSnapshot(
                false, -1, null, true, false);
        List<Lab27GradeDrivers.Driver> flap = Lab27GradeDrivers.fromOperationalSnapshot(
                false, -1, null, false, true);
        assertEquals(80, Lab27FunctionGradePolicy.apply(hot));
        assertEquals(80, Lab27FunctionGradePolicy.apply(flap));
        assertTrue(hot.contains(Lab27GradeDrivers.Driver.CHARGING_OVERHEAT));
        assertTrue(flap.contains(Lab27GradeDrivers.Driver.CHARGING_DISCONNECT));
        assertFalse(Lab27GradeDrivers.Driver.CHARGING_OVERHEAT.isConfirmedFailure());
        assertTrue(Lab27GradeDrivers.Driver.CHARGING_DISCONNECT.recommendedCheck(true)
                .contains("Lab 15"));
        assertEquals("B", Lab30ScoreEvidence.gradeForMeasuredSnapshot(80));
    }

    @Test public void worstVerifiedConcernSetsGradeWithoutCumulativeInventedPenalty() {
        List<Lab27GradeDrivers.Driver> drivers = Lab27GradeDrivers.fromOperationalSnapshot(
                true, Lab16ThermalCorrelation.OS_SEVERE,
                temperatures(49f, 95f, 94f, 66f, 87f, 49f), true, true);
        assertEquals(49, Lab27FunctionGradePolicy.apply(drivers));
        assertEquals(2, Lab27GradeDrivers.confirmedFailures(drivers));
        assertEquals(drivers, Lab27GradeDrivers.decode(Lab27GradeDrivers.encode(drivers)));
        assertTrue(Lab27GradeDrivers.validEvidence(
                Lab27GradeDrivers.encode(drivers), 49, true, 2));
    }

    @Test public void mismatchedOrLegacyReasonCodesDoNotValidateCertificate() {
        assertFalse(Lab27GradeDrivers.validEvidence(null, 100, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("", 80, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("RAM_FREE", 80, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("BATTERY_CHARGE", 80, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("STORAGE_SPACE", 80, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("UPTIME_SHORT", 80, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("THERMAL_HIGH_CPU", 100, false, 0));
        assertFalse(Lab27GradeDrivers.validEvidence("CAMERA", 49, false, 1));
        assertFalse(Lab27GradeDrivers.validEvidence("CAMERA,CAMERA", 49, true, 1));
        assertEquals(-1, Lab27GradeDrivers.countDrivers("UNKNOWN"));
        assertEquals(2, Lab27GradeDrivers.countDrivers(
                "THERMAL_HIGH_BATTERY,CHARGING_OVERHEAT"));
    }

    @Test public void sameBootAndFreshEvidenceAreRequired() {
        long nowWall = 1700000000000L, nowElapsed = 36000000L;
        assertTrue(Lab27GradeDrivers.freshEvidence(1,
                nowWall - 1000, nowElapsed - 1000, nowWall, nowElapsed));
        assertFalse(Lab27GradeDrivers.freshEvidence(0,
                nowWall - 1000, nowElapsed - 1000, nowWall, nowElapsed));
        assertFalse(Lab27GradeDrivers.freshEvidence(1,
                nowWall - 13L * 3600000L, nowElapsed - 1000, nowWall, nowElapsed));
        assertFalse(Lab27GradeDrivers.freshEvidence(1,
                nowWall - 1000, nowElapsed + 1000, nowWall, nowElapsed));
        assertFalse(Lab27GradeDrivers.freshEvidence(1,
                nowWall + 1000, nowElapsed - 1000, nowWall, nowElapsed));
    }
}
