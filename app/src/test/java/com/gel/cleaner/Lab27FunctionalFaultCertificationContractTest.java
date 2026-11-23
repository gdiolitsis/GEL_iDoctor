package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/** A completed but failed camera check reduces certification grade, not execution count. */
public final class Lab27FunctionalFaultCertificationContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void perCameraHumanNoIsRecordedOnlyAfterFullSurvey() throws Exception {
        String s = source();
        assertTrue(s.contains("overall.previewFailCount++;"));
        assertTrue(s.contains("s.userConfirmedPreview = Boolean.FALSE;"));
        assertTrue(s.contains("overall.previewOkCount + overall.previewFailCount == overall.total"));
        assertTrue(s.contains(".putBoolean(\"lab27_camera_preview_failed\", overall.previewFailCount > 0)"));
        assertTrue(s.contains(".putString(\"lab27_camera_device\", Build.FINGERPRINT)"));
        assertTrue(s.contains("Lab27CertificationGate.fresh("));
    }

    @Test public void completedFaultyLabIsNotMissingButReducesReportedGrade() throws Exception {
        String s = source();
        assertTrue(s.contains("lab27BasicLabsWithFindings("));
        assertTrue(s.contains("Lab27FunctionGradePolicy.apply(measuredDeviceHealthScore,"));
        assertTrue(s.contains("confirmedCameraFunctionFailure)"));
        assertTrue(s.contains("gr && \"D\".equals(snapshotGrade) ? \"Δ\""));
        assertTrue(s.contains("if (lab27RemainingBasic.isEmpty() && confirmedFunctionalFailures == 0)"));
        assertTrue(s.contains("Μέγιστος βαθμός Δ (49/100)"));
        assertTrue(s.contains("ΠΙΣΤΟΠΟΙΗΣΗ ΜΕ ΠΕΡΙΟΡΙΣΜΟΥΣ"));
        assertTrue(s.contains("if (confirmedFunctionalFailures > 0)"));
        assertTrue(s.contains(".putInt(\"lab27_cert_functional_findings\", confirmedFunctionalFailures)"));
        assertTrue(s.contains("Εργαστήρια με παρατηρήσεις"));
        assertFalse(s.contains("confirmedCameraFunctionFailure, lab27EvidenceFindings.size()"));
        assertTrue(s.contains(".putBoolean(\"lab27_cert_camera_failure\", confirmedCameraFunctionFailure)"));
        // Lab 28 reports diagnostics and lets buyers judge market listings themselves.
        assertTrue(s.contains("ΑΓΓΕΛΙΕΣ & ΦΩΤΟΓΡΑΦΙΕΣ eBay"));
        assertTrue(s.contains("Εσύ αποφασίζεις"));
        assertFalse(s.contains("Μην πληρώνεις τιμή πλήρως λειτουργικής συσκευής"));
        assertFalse(s.contains("Το 50% της αντίστοιχης τιμής"));
    }

    @Test public void incompleteTestsRemainBlockedAndNoAutomaticSellingPrice() throws Exception {
        String s = source();
        assertTrue(s.contains("if (!missing.isEmpty()) {"));
        assertTrue(s.contains("lab27PromptPhysicalConfirmation(grGate, factory);"));
        assertTrue(s.contains("if (!eligibleCert)"));
        assertTrue(s.contains("lab27OemReady(p, certFactory)"));
        assertTrue(s.contains("lab27PendingBasicLabs(p, applicableBasic)"));
        assertTrue(s.contains("lab27ObserveBasicLabWarning();"));
        assertTrue(s.contains("Το Lab 13 ολοκληρώθηκε (βασικός έλεγχος Bluetooth"));
        assertFalse(s.contains("price = price * 0.5"));
    }
}
