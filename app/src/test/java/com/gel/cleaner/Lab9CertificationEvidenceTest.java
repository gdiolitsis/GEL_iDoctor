package com.gel.cleaner;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public final class Lab9CertificationEvidenceTest {
    private static Set<String> allSupported() {
        return Lab9CertificationEvidence.supported(true, true, true, true);
    }
    private static Map<String,String> allPassed() {
        Map<String,String> statuses = new LinkedHashMap<>();
        for (String key : Lab9CertificationEvidence.GRADED)
            statuses.put(key, Lab9CertificationEvidence.VERIFIED);
        return statuses;
    }

    @Test public void onlySixInputsAndNeitherNfcNorPowerCanAffectGrade() {
        assertEquals(6, Lab9CertificationEvidence.GRADED.size());
        assertFalse(Lab9CertificationEvidence.GRADED.contains("lab9_live_nfc"));
        assertFalse(Lab9CertificationEvidence.GRADED.contains("lab9_live_power_user"));
        Map<String,String> input = allPassed();
        input.put("lab9_live_nfc", Lab9CertificationEvidence.USER_FAILED);
        input.put("lab9_live_power_user", Lab9CertificationEvidence.USER_FAILED);
        assertTrue(Lab9CertificationEvidence.evaluate(input, allSupported()).complete());
        assertFalse(Lab9CertificationEvidence.evaluate(input, allSupported())
                .hasUserReportedFailure());
        assertEquals(6, Lab9CertificationEvidence.evaluate(input, allSupported()).verifiedCount);
        assertEquals(6, Lab9CertificationEvidence.capture(input).size());
    }

    @Test public void incompleteIsNotAProvenFailureAndPreventsCertificate() {
        Map<String,String> input = allPassed();
        input.put(Lab9CertificationEvidence.MAGNET, "NOT_VERIFIED");
        input.put(Lab9CertificationEvidence.LIGHT, "PERMISSION_DENIED");
        Lab9CertificationEvidence.Assessment result =
                Lab9CertificationEvidence.evaluate(input, allSupported());
        assertFalse(result.complete());
        assertEquals(2, result.pending.size());
        assertTrue(result.reportedFailures.isEmpty());
        assertEquals(4, result.verifiedCount);
    }

    @Test public void disabledOrMissingEvidenceCannotBecomeGreenOrRed() {
        Map<String,String> input = allPassed();
        input.put(Lab9CertificationEvidence.GPS, "DISABLED");
        input.put(Lab9CertificationEvidence.GYRO, "UNSUPPORTED");
        Lab9CertificationEvidence.Assessment result =
                Lab9CertificationEvidence.evaluate(input, allSupported());
        assertFalse(result.complete());
        assertTrue(result.pending.contains(Lab9CertificationEvidence.GPS));
        assertTrue(result.pending.contains(Lab9CertificationEvidence.GYRO));
        assertFalse(result.hasUserReportedFailure());
    }

    @Test public void genuinelyUnsupportedSensorsAreNotMissingOrGraded() {
        Map<String,String> input = allPassed();
        input.put(Lab9CertificationEvidence.GPS, "NOT_TESTED");
        input.put(Lab9CertificationEvidence.GYRO, "UNSUPPORTED");
        input.put(Lab9CertificationEvidence.MAGNET, "NOT_TESTED");
        input.put(Lab9CertificationEvidence.LIGHT, "NOT_VERIFIED");
        Lab9CertificationEvidence.Assessment result =
                Lab9CertificationEvidence.evaluate(input,
                        Lab9CertificationEvidence.supported(false, false, false, false));
        assertTrue(result.complete());
        assertEquals(4, result.unsupported.size());
        assertEquals(2, result.verifiedCount);
        assertTrue(result.reportedFailures.isEmpty());
    }

    @Test public void specificDeclaredFailureCountsWithSourceAndCapsScore() {
        Map<String,String> input = allPassed();
        input.put(Lab9CertificationEvidence.GYRO, Lab9CertificationEvidence.USER_FAILED);
        Lab9CertificationEvidence.Assessment result =
                Lab9CertificationEvidence.evaluate(input, allSupported());
        assertTrue(result.complete());
        assertEquals(Collections.singletonList(Lab9CertificationEvidence.GYRO),
                result.reportedFailures);
        List<Lab27GradeDrivers.Driver> drivers =
                Lab27GradeDrivers.withLab9ReportedFailures(
                        Lab27GradeDrivers.fromOperationalSnapshot(false, -1, null, false, false),
                        result.reportedFailures);
        assertTrue(drivers.contains(Lab27GradeDrivers.Driver.LAB9_USER_REPORTED_GYRO_FAILURE));
        assertEquals(1, Lab27GradeDrivers.confirmedFailures(drivers));
        assertEquals(49, Lab27FunctionGradePolicy.apply(drivers));
        String encoded = Lab27GradeDrivers.encode(drivers);
        assertTrue(Lab27GradeDrivers.validEvidence(encoded, 49, false, 1));
        assertTrue(Lab27GradeDrivers.lab9FailureDriversMatch(encoded, result.reportedFailures));
        assertFalse(Lab27GradeDrivers.lab9FailureDriversMatch(encoded,
                Collections.singletonList(Lab9CertificationEvidence.LIGHT)));
    }

    @Test public void singleUnresponsiveKeyCannotBeMisrepresentedAsFailureDriver() {
        Map<String,String> input = allPassed();
        input.put(Lab9CertificationEvidence.GYRO, "NOT_VERIFIED");
        Lab9CertificationEvidence.Assessment result =
                Lab9CertificationEvidence.evaluate(input, allSupported());
        assertEquals(Collections.emptyList(), result.reportedFailures);
        assertFalse(result.complete());
        assertEquals(100, Lab27FunctionGradePolicy.apply(
                Lab27GradeDrivers.withLab9ReportedFailures(
                        Collections.<Lab27GradeDrivers.Driver>emptyList(),
                        result.reportedFailures)));
    }

    @Test public void signedEvidenceRequiresFreshSameBootAndFingerprint() {
        long wall=200000L, elapsed=50000L;
        assertTrue(Lab9CertificationEvidence.fresh(Lab9CertificationEvidence.SCHEMA,
                wall, elapsed, wall+1000, elapsed+1000, "deviceA", "deviceA"));
        assertFalse(Lab9CertificationEvidence.fresh(0,
                wall, elapsed, wall+1000, elapsed+1000, "deviceA", "deviceA"));
        assertFalse(Lab9CertificationEvidence.fresh(Lab9CertificationEvidence.SCHEMA,
                wall, elapsed, wall+1000, elapsed+1000, "deviceA", "deviceB"));
        assertFalse(Lab9CertificationEvidence.fresh(Lab9CertificationEvidence.SCHEMA,
                wall, elapsed, wall+1000, 1000, "deviceA", "deviceA"));
        assertFalse(Lab9CertificationEvidence.fresh(Lab9CertificationEvidence.SCHEMA,
                wall, elapsed, wall+Lab9CertificationEvidence.MAX_AGE_MS+1,
                elapsed+1000, "deviceA", "deviceA"));
    }
}
