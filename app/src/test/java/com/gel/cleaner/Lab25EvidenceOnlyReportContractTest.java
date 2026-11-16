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

    @Test public void userMayChooseLocalReportButNoSilentUploadOrForcedPicker() throws Exception {
        String s = source();
        assertTrue(s.contains("lab25ChooseBugReport"));
        assertTrue(s.contains("this::lab25CrashHistory"));
        assertTrue(s.contains("this::lab25ChooseBugReport"));
        assertTrue(s.contains("Intent.ACTION_OPEN_DOCUMENT"));
        assertTrue(s.contains("CriticalSystemCrashEvidence.parseUserReport(input, roles)"));
        assertTrue(s.contains("if (requestCode == 25025)"));
        assertTrue(s.contains("Αρχείο επιλογής χρήστη. Η γνησιότητά του δεν επαληθεύτηκε."));
        String automatic = lab().substring(0, lab().indexOf("private java.util.Set<String> lab25DefaultRoles()"));
        assertFalse(automatic.contains("startActivityForResult("));
    }

    @Test public void preservedFileReadbackMustActuallyBeVerified() throws Exception {
        String s = source();
        assertTrue(s.contains("long latency = storageLatencyProbe();"));
        assertTrue(s.contains("StorageIoEvidencePolicy.readbackMatches(data, readBack, received)"));
        assertTrue(s.contains("Τα δεδομένα που διαβάστηκαν ταιριάζουν ακριβώς."));
        assertTrue(s.contains("3. Ανεξάρτητη δοκιμή αρχείου (όχι διάγνωση crash)"));
        assertTrue(s.contains("SUMMARY: CRASH_ORIGIN=UNCLEAR; HISTORY_COVERAGE="));
    }
}
