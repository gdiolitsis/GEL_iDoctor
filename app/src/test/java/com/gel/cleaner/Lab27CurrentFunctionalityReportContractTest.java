package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Lab 27 must describe tested functionality today, not buyer history or weak heuristics. */
public final class Lab27CurrentFunctionalityReportContractTest {
    private static String report() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String source = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int start = source.indexOf("private void lab30CombineFindings() {");
        int end = source.indexOf("private boolean lab27ThermalIsElevated(", start);
        assertTrue(start >= 0 && end > start);
        return source.substring(start, end);
    }

    @Test public void positiveFunctionalStatusRequiresLabAndOemUserPreflight() throws Exception {
        String s = report();
        assertTrue(s.contains("if (!lab27UserPreflightAuthorized) {"));
        assertTrue(s.contains("lab27PromptPhysicalConfirmation(grGate, factory);"));
        assertTrue(s.contains("if (!lab27OemReady(p, currentFactory)"));
        assertTrue(s.contains("lab27PendingBasicLabs(p, lab27CheckedBasic)"));
        assertTrue(s.contains("lab27RemainingBasic.isEmpty()"));
        assertTrue(s.contains("Καλή λειτουργία στους ελεγμένους τομείς"));
        assertTrue(s.contains("Ο βαθμός βασίζεται σε επιβεβαιωμένες λειτουργικές αστοχίες"));
        assertTrue(s.contains("logLabelOkValue(\n            gr ? \"Κατάσταση συσκευής\""));
    }

    @Test public void buyerHistoryAndUnsupportedCommentaryStayOutOfReport() throws Exception {
        String s = report();
        assertFalse(s.contains("lab29DisplayLab25Findings(p, gr);"));
        assertFalse(s.contains("Στατιστικά χρήσης εφαρμογών"));
        assertFalse(s.contains("Δεν επιβεβαιώθηκε ανεξάρτητα η κατάσταση εξαρτημάτων"));
        assertFalse(s.contains("Η αναφορά είναι πληροφοριακή"));
        assertFalse(s.contains("Ιστορικό Lab 25\" : \"Lab 25 history"));
        assertFalse(s.contains("Όριο βαθμολογίας"));
        assertFalse(s.contains("Τελικό Συμπέρασμα"));
        assertFalse(s.contains("Τάση hardware"));
        assertFalse(s.contains("Ιστορικό hardware"));
        assertFalse(s.contains("Έλεγχος συνέπειας διαγνωστικών αποτελεσμάτων"));
        assertFalse(s.contains("Αποτύπωμα Εφαρμογών"));
        assertTrue(s.contains("lab29SaveTechnicalAssessmentFromLab25(p);"));
        assertTrue(s.contains("if (!lab29RequireLab25Evidence()) return;"));
    }

    @Test public void gradeExcludesAppCountsButKeepsTimestampReportIdAndCertification() throws Exception {
        String s = report();
        assertFalse(s.contains("Lab30ScoreEvidence.functionalPerformanceScore("));
        assertTrue(s.contains("Lab27FunctionGradePolicy.apply(lab27GradeDrivers)"));
        assertFalse(s.contains("Lab30ScoreEvidence.performanceScore("));
        assertTrue(s.contains("Lab30ScoreEvidence.gradeForMeasuredSnapshot(deviceHealthScore)"));
        assertTrue(s.contains("Χρόνος διάγνωσης"));
        assertTrue(s.contains("ID διάγνωσης"));
        assertTrue(s.contains("Περιορισμοί αναφοράς"));
        assertTrue(s.contains("GEL Device Certification"));
        assertTrue(s.contains("Lab27CertificationGate.eligible("));
        assertTrue(s.contains("lab27_cert_schema"));
    }
}
