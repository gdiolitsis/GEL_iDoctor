package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/**
 * Protects the owner-approved reporting rule:
 * observed measurements and justified actions, never speculative hardware
 * diagnoses or generic unmeasured catastrophe warnings.
 */
public final class LabsEvidenceOnlyReportContractTest {
    private static String source() throws Exception {
        Path file = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(file)) {
            file = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        }
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    private static String section(String from, String to) throws Exception {
        String s = source();
        int a = s.indexOf(from);
        int b = s.indexOf(to, a + from.length());
        assertTrue("Missing section start: " + from, a >= 0);
        assertTrue("Missing section end: " + to, b > a);
        return s.substring(a, b);
    }

    @Test public void audioLabsDescribeObservedOutputsInsteadOfHypotheticalDamage() throws Exception {
        String one = section("private void lab1SpeakerTone()", "private void lab2SpeakerSweep()");
        assertTrue(one.contains("Ελέγξτε την ένταση και την επιλεγμένη έξοδο ήχου"));
        assertFalse(one.contains("Πιθανή βλάβη ηχείου"));
        assertFalse(one.contains("DSP φιλτράρισμα ή πιθανή βλάβη"));

        String two = section("private void lab2SpeakerSweep()", "private void lab3EarpieceManual()");
        assertTrue(two.contains("Καταγράφηκε αύξηση της στάθμης ήχου."));
        assertFalse(two.contains("Δεν αποτελεί απόλυτη απόδειξη σωστής συχνότητας."));

        String four = section("private void lab4MicManual()", "private void lab5Vibration()");
        assertTrue(four.contains("Ο χρήστης επιβεβαίωσε καθαρή αναπαραγωγή"));
        assertFalse(four.contains("προβλημα στο μικρόφωνο του συνομιλητη"));
    }

    @Test public void storageAndThermalResultsRemainFactual() throws Exception {
        String fourteen = section("private void lab14BatteryHealthStressTest()", "private void lab15ChargingSystemSmart()");
        assertTrue(fourteen.contains("Μετρήθηκε έντονη πτώση τάσης κατά την καταπόνηση"));
        assertFalse(fourteen.contains("Έντονη πτώση τάσης — πιθανή φθορά κυψελών"));

        String sixteen = section("private void lab16ThermalSnapshot()", "private void lab17RunAuto()");
        assertTrue(sixteen.contains("Καταγράφηκε αυξημένη θερμοκρασία"));
        assertFalse(sixteen.contains("Ενδέχεται να ενεργοποιηθούν μηχανισμοί θερμικής προστασίας"));

        String eighteen = section("private void lab18StorageSnapshot()", "private void lab19RamSnapshot()");
        assertTrue(eighteen.contains("Επαρκής ελεύθερος χώρος κατά τη μέτρηση"));
        assertFalse(eighteen.contains("Πιθανά κρασαρίσματα εφαρμογών"));
    }

    @Test public void rootAndSecurityResultsAreScopedToWhatWasChecked() throws Exception {
        String twentyThree = section("private void lab23DeveloperOptionsRisk()", "// UI BUBBLES (GEL)");
        assertTrue(twentyThree.contains("ο προχωρημένος έλεγχος μέσω καλωδίου είναι απενεργοποιημένος"));
        assertFalse(twentyThree.contains("δεν μπορεί να πιστοποιήσει τη συνολική ασφάλεια της συσκευής"));

        String twentyFour = section("private void lab24RootSuspicion()", "// LAB 24 — INTERNAL HELPERS");
        assertTrue(twentyFour.contains("Τα αποτελέσματα είναι ενθαρρυντικά."));
        assertTrue(twentyFour.contains("boolean suExec = lab24_canExecSu();"));
        assertTrue(twentyFour.contains("logLabelOkValue("));
        assertFalse(twentyFour.contains("logInfo(gr ? \"Δεν εκδόθηκε αυθαίρετος συνολικός δείκτης ασφάλειας.\""));
    }

    @Test public void summaryDoesNotExposeHeuristicHardwarePredictionsAsWarnings() throws Exception {
        String twentyEight = section("private void lab28HardwareStability()", "// LAB 28 — Helpers");
        assertTrue(twentyEight.contains("Το αρχείο περιέχει αναφορά επανεκκίνησης"));
        assertFalse(twentyEight.contains("logLabelWarnValue("));

        String thirty = section("private void lab30CombineFindings()", "private void lab31FinalSummary()");
        assertTrue(thirty.contains("LAB 30 — Αυτόματη Σύνοψη Μετρήσεων"));
        assertFalse(thirty.contains("logInfo(gr\n        ? \"Πιθανή βασική αιτία προβλήματος:\""));
        assertFalse(thirty.contains("logLabelWarnValue(gr ? \"Root\" : \"Root\""));
        assertTrue(thirty.contains("Το Lab 30 ολοκληρώθηκε"));
    }

    @Test public void finalReportSummarizesRecordedResultsWithoutImaginaryProblems() throws Exception {
        String thirtyOne = section("private void lab31FinalSummary()", "// ENABLE EXISTING EXPORT BUTTON");
        assertTrue(thirtyOne.contains("Δεν υπάρχουν καταγεγραμμένες προειδοποιήσεις ή σφάλματα στο διαθέσιμο αρχείο."));
        assertTrue(thirtyOne.contains("Η αναφορά συγκέντρωσε τα διαθέσιμα αποτελέσματα"));
        assertFalse(thirtyOne.contains("η αναφορά δεν πιστοποιεί υγιές hardware"));
        assertTrue(thirtyOne.contains("enableSingleExportButton()"));
    }
}
