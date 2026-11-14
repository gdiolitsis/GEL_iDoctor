package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Source contract for human-readable, evidence-based Lab 23 logs. */
public final class Lab23PlainLanguageAccessContractTest {
    private static String lab() throws Exception {
        Path path = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(path))
            path = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String full = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        int first = full.indexOf("private void lab23DeveloperOptionsRisk()");
        int last = full.indexOf("// UI BUBBLES (GEL)", first);
        assertTrue(first >= 0 && last > first);
        return full.substring(first, last);
    }

    @Test public void cableStatusIsReportedWithEvidenceAppropriateColor() throws Exception {
        String s = lab();
        assertTrue(s.contains("Settings.Global.ADB_ENABLED"));
        assertTrue(s.contains("Settings.Global.DEVELOPMENT_SETTINGS_ENABLED"));
        assertTrue(s.contains("if (usbState == 0) {\n        logLabelOkValue("));
        assertTrue(s.contains("} else if (usbState == 1) {\n        logLabelWarnValue("));
        assertTrue(s.contains("Ενεργοποιημένες — αυτό από μόνο του δεν σημαίνει κίνδυνο"));
        assertTrue(s.contains("Δεν ήταν δυνατό να διαβαστεί η ρύθμιση"));
        assertFalse(s.contains("risk += "));
    }

    @Test public void developerOptionsAreNeverTreatedAsProofOfCompromise() throws Exception {
        String s = lab();
        assertTrue(s.contains("Μπορείτε να είστε ήσυχοι για αυτό που επιβεβαιώσαμε"));
        assertTrue(s.contains("Το ότι έχετε ενεργές τις Επιλογές προγραμματιστή δεν σημαίνει ότι κάποιος έχει παραβιάσει το κινητό σας."));
        assertTrue(s.contains("Αυτό δεν σημαίνει ότι το κινητό έχει παραβιαστεί."));
        assertTrue(s.contains("Αν δεν χρησιμοποιείτε ασύρματο έλεγχο από υπολογιστή, διατηρήστε την αντίστοιχη ρύθμιση απενεργοποιημένη."));
    }

    @Test public void localProbesRemainButDoNotMasqueradeAsSecurityFindings() throws Exception {
        String s = lab();
        assertTrue(s.contains("isPortOpen(5555, 200)"));
        assertTrue(s.contains("isPortOpen(3700, 80)"));
        assertTrue(s.contains("isPortOpen(7460, 80)"));
        assertTrue(s.contains("scanPairingPortRange()"));
        assertTrue(s.contains("android.util.Log.d(\"GEL-Lab23\""));
        assertTrue(s.contains("runOnUiThread(() -> {"));
        assertTrue(s.contains("Δεν μπορεί να επιβεβαιωθεί από αυτό το εργαστήριο"));
        assertFalse(s.contains("Τοπική TCP θύρα"));
        assertFalse(s.contains("Τοπικές θύρες"));
        assertFalse(s.contains("Ποσοστό ασφάλειας"));
        assertTrue(s.contains("Ανοίξτε Ρυθμίσεις → Επιλογές προγραμματιστή → Ασύρματη αποσφαλμάτωση."));
        assertTrue(s.contains("Το Lab 23 ολοκληρώθηκε — ελέγχθηκαν οι διαθέσιμες ρυθμίσεις πρόσβασης."));
    }
}
