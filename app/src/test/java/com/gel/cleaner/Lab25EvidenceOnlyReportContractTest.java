package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/** Lab25 reports evidence, not app churn or an invented stability certificate. */
public final class Lab25EvidenceOnlyReportContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String lab() throws Exception {
        String whole = source();
        int a = whole.indexOf("private void lab25CrashHistory() {");
        int b = whole.indexOf("// SMALL helper", a);
        assertTrue(a >= 0 && b > a);
        return whole.substring(a, b);
    }

    @Test public void onlyCriticalProcessesAndRealErrorStatesAreIncluded() throws Exception {
        String s = lab();
        assertTrue(s.contains("lab25DefaultRoles()"));
        assertTrue(s.contains("CriticalSystemCrashEvidence.currentError("));
        assertTrue(s.contains("ActivityManager.ProcessErrorStateInfo.CRASHED"));
        assertTrue(s.contains("ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING"));
        assertFalse(s.contains("Δεν εμφανίζονται σφάλματα εφαρμογών αυτή τη στιγμή."));
        assertTrue(s.contains("Δεν είναι πλήρης έλεγχος του Android."));
    }

    @Test public void privilegedSystemHistoryIsNeverAssumedComplete() throws Exception {
        String s = lab();
        assertTrue(s.contains("CriticalSystemCrashEvidence.dropBoxEvent("));
        assertTrue(s.contains("coverage.recordQuery(complete);"));
        assertTrue(s.contains("if (!coverage.complete() && !history.events.isEmpty())"));
        assertTrue(s.contains("Το Android δεν έδωσε πλήρες ιστορικό."));
        assertFalse(s.contains("if (crashes == 0)"));
        assertFalse(s.contains("if (system == 0)"));
        assertFalse(s.contains("Δείκτης κινδύνου σταθερότητας"));
        assertFalse(s.contains("system_server_wtf"));
    }

    @Test public void rareEventsAreNeutralAndRecurrenceAloneNeedsCheck() throws Exception {
        String s = lab();
        assertTrue(s.contains("if (s.hasVerifiedRecurrence())"));
        assertTrue(s.contains("if (s.hasStrongInstabilityIndications())"));
        assertTrue(s.contains("ΙΣΧΥΡΕΣ ΕΝΔΕΙΞΕΙΣ ΕΠΑΝΑΛΑΜΒΑΝΟΜΕΝΗΣ ΑΣΤΑΘΕΙΑΣ"));
        assertTrue(s.contains("logLabelWarnValue(gr ? \"ΠΡΟΕΙΔΟΠΟΙΗΣΗ ΑΓΟΡΑΣΤΗ\""));
        assertTrue(s.contains("Συνιστάται να μη γίνει αγορά χωρίς πρόσθετο έλεγχο."));
        assertTrue(s.contains("Ανεξάρτητα χρονολογημένα επεισόδια"));
        assertTrue(s.contains("if (s.hasSeriousSystemIncident())"));
        assertTrue(s.contains("Μεμονωμένο ή μη χρονολογημένο συμβάν"));
        assertTrue(s.contains("Δεν αποδεικνύεται βλάβη υλικού."));
        assertTrue(s.contains("Το άδειο ιστορικό δεν είναι πιστοποιητικό υγείας."));
    }

    @Test public void onlyAutomaticLab25ButtonIsShownAndOptionalParserRemainsInternal() throws Exception {
        String s = source();
        assertTrue(s.contains("this::lab25CrashHistory"));
        assertFalse(s.contains("this::lab25ChooseBugReport"));
        assertFalse(s.contains("↳ Προαιρετικά: Ανάλυση αναφοράς Android"));
        assertFalse(s.contains("↳ Optional: Analyze Android bugreport"));
        assertFalse(s.contains("Με το ξεχωριστό κουμπί μπορείτε να επιλέξετε δική σας"));
        assertFalse(s.contains("Use the separate button to select your Android bugreport"));
        // No hidden picker entrypoint or stale onActivityResult route.
        // Preserve only the explicitly shared Android bugreport parser.
        assertFalse(s.contains("private void lab25ChooseBugReport()"));
        assertFalse(s.contains("Intent.ACTION_OPEN_DOCUMENT"));
        assertTrue(s.contains("CriticalSystemCrashEvidence.parseUserReport(input, roles)"));
        assertFalse(s.contains("if (requestCode == 25025)"));
        assertTrue(s.contains("Lab25SharedReportActivity.ACTION_IMPORT_LAB25"));
        assertTrue(s.contains("lab25ReadBugReport(sharedReport)"));
        String automatic = lab().substring(0, lab().indexOf("private java.util.Set<String> lab25DefaultRoles()"));
        assertFalse(automatic.contains("startActivityForResult("));
    }

    @Test public void fileReadbackBelongsToLab18AndNotToCrashHistory() throws Exception {
        String s = source();
        int start18 = s.indexOf("private void lab18StorageSnapshot()");
        int end18 = s.indexOf("private void lab19RamSnapshot()", start18);
        int start25 = s.indexOf("private void lab25CrashHistory()");
        int end25 = s.indexOf("private java.util.Set<String> lab25DefaultRoles()", start25);
        assertTrue(start18 >= 0 && end18 > start18);
        assertTrue(start25 >= 0 && end25 > start25);
        String storage = s.substring(start18, end18);
        String critical = s.substring(start25, end25);
        assertTrue(storage.contains("long storageProbeLatency = storageLatencyProbe();"));
        assertTrue(storage.contains("Επιτυχής εγγραφή και ανάγνωση αρχείου 4 KB."));
        assertTrue(storage.contains("Τα δεδομένα που διαβάστηκαν ταιριάζουν ακριβώς."));
        assertTrue(storage.contains("Δεν μετρήθηκε φθορά NAND ή υγεία ελεγκτή."));
        assertFalse(critical.contains("storageLatencyProbe()"));
        assertFalse(critical.contains("Ανεξάρτητη δοκιμή αρχείου"));
        assertFalse(critical.contains("Δοκιμή αποθήκευσης"));
        assertTrue(s.contains("StorageIoEvidencePolicy.readbackMatches(data, readBack, received)"));
        assertTrue(critical.contains("SUMMARY: CRASH_ORIGIN=UNCLEAR; HISTORY_COVERAGE="));
    }
}
