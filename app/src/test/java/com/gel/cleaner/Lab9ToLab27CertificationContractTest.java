package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/** The finished inventory alone cannot award a functional certificate. */
public final class Lab9ToLab27CertificationContractTest {
    private static String file(String name) throws Exception {
        Path p=Paths.get("app/src/main/java/com/gel/cleaner", name);
        if (!Files.isRegularFile(p)) p=Paths.get("src/main/java/com/gel/cleaner", name);
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
    private static String between(String s,String from,String to) {
        int a=s.indexOf(from),b=s.indexOf(to,a+from.length());
        assertTrue("Missing source boundary: "+from, a>=0 && b>a);
        return s.substring(a,b);
    }

    @Test public void inventoryStartsByInvalidatingOldCertificationAndLab9Snapshot() throws Exception {
        String main=file("ManualTestsActivity.java");
        String inventory=between(main,"private void lab9SensorsCheck()",
                "private void showLab9LiveChecksPrompt()");
        assertTrue(inventory.contains(".remove(\"lab9_cert_schema\")"));
        assertTrue(inventory.contains(".remove(\"lab27_cert_schema\")"));
        assertTrue(inventory.contains("for (String key : Lab9CertificationEvidence.GRADED)"));
    }

    @Test public void onlyRealActivityResultIsCapturedAndNfcPowerNeverSaved() throws Exception {
        String main=file("ManualTestsActivity.java");
        assertTrue(main.contains("if (resultCode == RESULT_OK && data != null)\n        lab9StoreFunctionalEvidence(data);"));
        String producer=between(main,"private void lab9StoreFunctionalEvidence(Intent data)",
                "private void lab9LogCompletedAtEnd()");
        assertTrue(producer.contains("for (String key : Lab9CertificationEvidence.GRADED)"));
        assertTrue(producer.contains("putString(\"lab9_cert_status_\""));
        assertTrue(producer.contains("SystemClock.elapsedRealtime()"));
        assertTrue(producer.contains("Build.FINGERPRINT"));
        assertFalse(producer.contains("EssentialHardwareChecksActivity.EXTRA_NFC"));
        assertFalse(producer.contains("EssentialHardwareChecksActivity.EXTRA_POWER"));
        assertTrue(main.contains("lab9LogCompletedAtEnd();"));
    }

    @Test public void gateRequiresLab9NotOnlyGreenInventoryLine() throws Exception {
        String main=file("ManualTestsActivity.java");
        String gate=between(main, "private java.util.List<String> lab27PendingBasicLabs(",
                "private java.util.List<String> lab27BasicLabsWithFindings(");
        assertTrue(gate.contains("applicable.contains(\"9\")"));
        assertTrue(gate.contains("lab27HasCompleteLab9Evidence(p)"));
        assertTrue(gate.contains("pending.add(\"9\")"));
        assertTrue(main.contains("missing.contains(\"9\") ?"));
        assertTrue(main.contains("lab27Lab9MissingDetail(before, grGate)"));
    }

    @Test public void scoreAndBuyerCertificateDependOnSameTypedLab9Failures() throws Exception {
        String main=file("ManualTestsActivity.java");
        assertTrue(main.contains("Lab27GradeDrivers.withLab9ReportedFailures("));
        assertTrue(main.contains("lab9FunctionalEvidence.reportedFailures"));
        assertTrue(main.contains("Lab27FunctionGradePolicy.apply(lab27GradeDrivers)"));
        assertTrue(main.contains("driver.isLab9UserReportedFailure()"));
        assertTrue(main.contains(".putLong(\"lab27_cert_lab9_source_wall\","));
        assertTrue(main.contains("lab27_cert_lab9_source_wall"));
        assertTrue(main.contains("Lab27GradeDrivers.lab9FailureDriversMatch(driverCodes,"));
        assertTrue(main.contains("lab27Lab9Assessment(p)"));
        assertTrue(main.contains("lab27HasCompleteLab9Evidence(p)"));
    }

    @Test public void powerAndNfcCannotBecomeGradeDrivers() throws Exception {
        String checks=file("EssentialHardwareChecksActivity.java");
        String grade=file("Lab27GradeDrivers.java");
        String evidence=file("Lab9CertificationEvidence.java");
        assertTrue(checks.contains("private void reportUnresponsiveFunction()"));
        assertTrue(checks.contains("for (String key : Lab9CertificationEvidence.GRADED)"));
        assertTrue(checks.contains("set(key, USER_FAILED)"));
        assertTrue(checks.contains("required = (EXTRA_GPS.equals(key))"));
        assertTrue(checks.contains("GPS_TIME_MS"));
        assertFalse(evidence.contains("GRADED = Collections.unmodifiableList(\n            java.util.Arrays.asList(NFC"));
        assertFalse(grade.contains("LAB9_USER_REPORTED_NFC_FAILURE"));
        assertFalse(grade.contains("LAB9_USER_REPORTED_POWER_FAILURE"));
    }
}
