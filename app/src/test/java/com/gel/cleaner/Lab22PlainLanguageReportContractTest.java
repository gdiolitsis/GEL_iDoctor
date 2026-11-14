package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Guard the exact Greek Lab 22 user-facing report and honest evidence boundaries. */
public final class Lab22PlainLanguageReportContractTest {
    private static String section() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) {
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        }
        String src = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int start = src.indexOf("private void lab22SecurityPatchAndPlayProtect()");
        int end = src.indexOf("// LAB 23 — Developer Options", start);
        assertTrue(start >= 0 && end > start);
        return src.substring(start, end);
    }

    @Test public void exactOwnerApprovedGreekReportIsPreserved() throws Exception {
        String s = section();
        String[] exact = {
            "Η συσκευή ενδέχεται να μην περιλαμβάνει πρόσφατες διορθώσεις ασφαλείας. Συνιστάται να ελέγξετε αν υπάρχει νεότερη ενημέρωση.",
            "2. Προστασία από κακόβουλες εφαρμογές",
            "Δεν επιβεβαιώθηκε η κατάστασή του.",
            "Εντοπίστηκε πρόσβαση σε σχετικές ρυθμίσεις, αλλά δεν γνωρίζουμε αν η προστασία είναι ενεργή. Δεν πραγματοποιήθηκε σάρωση από το εργαστήριο.",
            "3. Τι πρέπει να κάνετε",
            "Ελέγξτε αν υπάρχει νεότερη ενημέρωση Android.",
            "Ανοίξτε Play Store → Προφίλ → Play Protect → Ρυθμίσεις και βεβαιωθείτε ότι η σάρωση εφαρμογών είναι ενεργή.",
            "Το Lab 22 ολοκληρώθηκε — καταγράφηκαν τα διαθέσιμα στοιχεία ασφάλειας."
        };
        for (String phrase : exact) {
            assertTrue("Missing exact approved phrase: " + phrase, s.contains(phrase));
        }
    }

    @Test public void updateAgeUsesDeviceDateAndUnknownPlayProtectStaysNeutral() throws Exception {
        String s = section();
        assertTrue(s.contains("android.os.Build.VERSION.SECURITY_PATCH"));
        assertTrue(s.contains("new SimpleDateFormat(\"dd/MM/yyyy\", Locale.US)"));
        assertTrue(s.contains("long approximateMonths = ageDays / 30L;"));
        assertTrue(s.contains("logLabelWarnValue("));
        assertTrue(s.contains("settingsIntent.resolveActivity(pm) != null"));
        assertTrue(s.contains("logLabelValue(\n            \"Play Protect\""));
        assertFalse("A settings screen cannot certify Play Protect", s.contains("package_verifier_enable"));
        assertFalse("No automatic Play Protect success can be reported", s.contains("logLabelOkValue("));
        assertFalse("No Play Protect failure is established by unknown status", s.contains("logLabelErrorValue("));
    }

    @Test public void completionIsNotConfusedWithVerifiedProtection() throws Exception {
        String s = section();
        assertTrue(s.contains("logOk(gr\n            ? \"Το Lab 22 ολοκληρώθηκε"));
        assertTrue(s.contains("Δεν πραγματοποιήθηκε σάρωση από το εργαστήριο."));
    }
}
