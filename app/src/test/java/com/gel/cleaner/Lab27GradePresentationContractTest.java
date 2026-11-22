package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Guard the restored Lab27 rating without reverting to unsupported hardware certification. */
public final class Lab27GradePresentationContractTest {
    private static String lab27() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String source = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int start = source.indexOf("private void lab30CombineFindings()");
        int end = source.indexOf("private void lab31FinalSummary()", start);
        assertTrue(start >= 0 && end > start);
        return source.substring(start, end);
    }

    @Test public void gradeIsRestoredFromMeasuredSnapshotNotLegacyHardwareRisk() throws Exception {
        String lab = lab27();
        assertTrue(lab.contains("final String snapshotGrade = Lab30ScoreEvidence.gradeForMeasuredSnapshot(deviceHealthScore);"));
        assertTrue(lab.contains("if (snapshotGrade == null)"));
        assertTrue(lab.contains("gr ? \"Βαθμολογία συσκευής\" : \"Device grade\""));
        assertTrue(lab.contains("gr ? \"Συνολικός δείκτης\" : \"Overall index\""));
        assertTrue(lab.contains("deviceHealthScore + \"/100\""));
        assertTrue(lab.contains("ΔΕΝ ΥΠΟΛΟΓΙΣΤΗΚΕ"));
        assertFalse(lab.contains("logInfo(\"GEL Hardware Diagnostic Certification\")"));
        assertFalse(lab.contains("logLabelOkValue(\n        gr ? \"Πιστοποίηση\""));
    }

    @Test public void lab25ProvenanceAndFinalSnapshotContractRemainIntact() throws Exception {
        String lab = lab27();
        assertTrue(lab.contains("if (!lab29RequireLab25Evidence()) return;"));
        assertFalse(lab.contains("lab29DisplayLab25Findings(p, gr);"));
        assertFalse(lab.contains("Στατιστικά χρήσης εφαρμογών"));
        assertTrue(lab.contains("Κατάσταση συσκευής"));
        assertTrue(lab.contains("lab29SaveTechnicalAssessmentFromLab25(p);"));
        assertTrue(lab.contains("Το Lab 27 ολοκληρώθηκε"));
        assertTrue(lab.contains("Το Certification αξιολογείται ξεχωριστά."));
        assertTrue(lab.contains("lab27PromptPhysicalConfirmation(grGate, factory);"));
        assertTrue(lab.contains("Η βαθμολογία ξεκινά από τις μετρήσεις"));
        assertFalse(lab.contains("Όριο βαθμολογίας"));
        assertFalse(lab.contains("Τελικό Συμπέρασμα"));
    }

    @Test public void lab27ReadsLiveBatteryPercentageAndNeverUsesStaleLab14Member() throws Exception {
        String lab = lab27();
        assertTrue(lab.contains("final int lab27BatteryPercent = getBatteryPercentSafe();"));
        assertTrue(lab.contains("Lab30ScoreEvidence.hasBatteryReadings(batTemp, (float) lab27BatteryPercent)"));
        assertTrue(lab.contains("scoreBattery(batTemp, (float) lab27BatteryPercent, charging)"));
        assertTrue(lab.contains("(lab27BatteryPercent >= 0 ? fmt1((float) lab27BatteryPercent)"));
        assertFalse(lab.contains("(batteryPercent >= 0 ? fmt1((float) batteryPercent)"));
    }

    @Test public void unknownOemAttributesHaveNeutralBilingualInterpretation() throws Exception {
        String lab = lab27();
        assertTrue(lab.contains("specRow.noteGreek"));
        assertTrue(lab.contains("specRow.noteEnglish"));
        assertTrue(lab.contains("Χαρακτηριστικά χωρίς αυτόματη σύγκριση"));
        assertTrue(lab.contains("Το εργαστήριο παρουσιάζει όσα μπορεί να ελέγξει αυτόματα"));
        assertTrue(lab.contains("logLabelValue(gr ? \"Οδηγία / εξήγηση\" : \"Guidance / explanation\", explanation)"));
    }
}
