package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Lab 28 shows direct observations and never grades unseen hardware health. */
public final class Lab28DirectEvidenceReportContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
    private static String lab() throws Exception {
        String src = source();
        int a = src.indexOf("private void lab28HardwareStability() {");
        int b = src.indexOf("// LAB 28 — Helpers", a);
        assertTrue(a >= 0 && b > a);
        return src.substring(a, b);
    }

    @Test public void titleMatchesActualObservedScope() throws Exception {
        String s = source();
        assertTrue(s.contains("PRO  28. Καταγραφές λειτουργίας και μετρήσεις"));
        assertTrue(s.contains("LAB 28 — Καταγραφές λειτουργίας και μετρήσεις"));
        assertFalse(s.contains("PRO  28. Σταθερότητα Υλικού & Ακεραιότητα"));
        assertTrue(s.contains("Το εργαστήριο εμφανίζει διαθέσιμες μετρήσεις"));
    }

    @Test public void temperatureAndUptimeReadingsHaveEvidenceBasedColors() throws Exception {
        String s = lab();
        assertTrue(s.contains("android.os.SystemClock.elapsedRealtime()"));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Χρόνος από την τελευταία εκκίνηση\""));
        assertTrue(s.contains("android.os.BatteryManager.EXTRA_TEMPERATURE"));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Θερμοκρασία μπαταρίας\""));
        assertTrue(s.contains("Map<String, Float> zones = readThermalZones();"));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Θερμοκρασία CPU\""));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Θερμοκρασία GPU\""));
        assertTrue(s.contains("logLabelValue(gr ? \"Θερμοκρασία CPU\""));
        assertTrue(s.contains("logLabelValue(gr ? \"Θερμοκρασία GPU\""));
    }

    @Test public void retainedLogReaderDoesNotClaimHardwareDefect() throws Exception {
        String s = lab();
        assertTrue(s.contains("Lab28EvidenceReader.readFromGELServiceLog()"));
        assertTrue(s.contains("Το αρχείο περιέχει αναφορά επανεκκίνησης"));
        assertTrue(s.contains("Καμία σχετική αναφορά στο διαθέσιμο ιστορικό."));
        assertFalse(s.contains("logLabelWarnValue("));
        assertFalse(s.contains("Δεν τεκμηριώνεται θερμική σταθερότητα από αυτά τα δεδομένα."));
        assertFalse(s.contains("Δείκτης Συνεκτικότητας Συμπτωμάτων"));
        assertFalse(s.contains("Δείκτης κινδύνου υλικού"));
        assertTrue(s.contains("logOk(gr ? \"Το Lab 28 ολοκληρώθηκε — καταγράφηκαν διαθέσιμες μετρήσεις και αναφορές.\""));
    }

    @Test public void futureHardwareScenariosAreNotShownAsPopupFacts() throws Exception {
        String s = source();
        int a = s.indexOf("private String getLab28TextEN()");
        int b = s.indexOf("// LAB 29 —", a);
        assertTrue(a >= 0 && b > a);
        String popup = s.substring(a, b);
        assertFalse(popup.contains("solder or interconnect defects"));
        assertFalse(popup.contains("διακοπτόμενη επαφή"));
        assertTrue(popup.contains("χωρίς προβλέψεις για υποθετικές βλάβες"));
    }
}
