package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Lab 25: a checked result is not a speculative hardware diagnosis. */
public final class Lab25EvidenceOnlyReportContractTest {
    private static String lab() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String whole = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int a = whole.indexOf("private void lab25CrashHistory() {");
        int b = whole.indexOf("// SMALL helper", a);
        assertTrue(a >= 0 && b > a);
        return whole.substring(a, b);
    }

    @Test public void onlyActuallyReturnedCurrentSnapshotCanShowZeroErrors() throws Exception {
        String s = lab();
        assertTrue(s.contains("if (errors == null) {"));
        assertTrue(s.contains("Το Android δεν επέστρεψε στοιχεία για αυτή τη μέτρηση."));
        assertTrue(s.contains("if (visible == 0) {"));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Τρέχουσα κατάσταση\""));
        assertTrue(s.contains("Δεν εμφανίζονται σφάλματα εφαρμογών αυτή τη στιγμή."));
        assertTrue(s.contains("logLabelWarnValue(\n                        error.condition"));
    }

    @Test public void unavailableHistoryIsNeutralNotAFailureOrZeroCrashes() throws Exception {
        String s = lab();
        assertTrue(s.contains("GelCrashCoverage coverage = new GelCrashCoverage(tags.length);"));
        assertTrue(s.contains("coverage.recordQuery(complete);"));
        assertTrue(s.contains("if (coverage.complete()) {"));
        assertTrue(s.contains("logLabelValue(gr ? \"Διαθέσιμο ιστορικό\""));
        assertTrue(s.contains("Το Android δεν παρείχε πλήρες ιστορικό στην εφαρμογή."));
        assertFalse(s.contains("Δείκτης κινδύνου σταθερότητας"));
        assertFalse(s.contains("Η πρόσβαση μπορεί να περιορίζεται"));
    }

    @Test public void actualEventsGetWarningsAndZeroConfirmedCountsAreGreen() throws Exception {
        String s = lab();
        assertTrue(s.contains("if (crashes == 0)"));
        assertTrue(s.contains("if (anrs == 0)"));
        assertTrue(s.contains("if (system == 0)"));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Σφάλματα εφαρμογών\""));
        assertTrue(s.contains("logLabelWarnValue(gr ? \"Καταγεγραμμένο συμβάν\""));
    }

    @Test public void verifiedFourKbReadWriteAndLatencyAreGreen() throws Exception {
        String s = lab();
        assertTrue(s.contains("long latency = storageLatencyProbe();"));
        assertTrue(s.contains("if (latency < 0) {"));
        assertTrue(s.contains("logLabelWarnValue(gr ? \"Δοκιμή αποθήκευσης\""));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Δοκιμή αποθήκευσης\""));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Έλεγχος δεδομένων\""));
        assertTrue(s.contains("Τα δεδομένα που διαβάστηκαν ταιριάζουν ακριβώς."));
        assertTrue(s.contains("Λιγότερο από 1 ms"));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Χρόνος δοκιμής\""));
        assertTrue(s.contains("Το Lab 25 ολοκληρώθηκε — ελέγχθηκαν τα διαθέσιμα συμβάντα και η αποθήκευση."));
        assertTrue(s.contains("GELServiceLog.info(\"SUMMARY: CRASH_ORIGIN=UNCLEAR"));
    }
}
