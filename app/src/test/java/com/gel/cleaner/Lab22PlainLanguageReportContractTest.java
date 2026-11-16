package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Guard the owner-approved Lab 22 report and evidence boundaries for Google Verify Apps. */
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

    @Test public void ownerApprovedGreekReportAndVerifiedScopeArePreserved() throws Exception {
        String s = section();
        String[] exact = {
            "Η συσκευή ενδέχεται να μην περιλαμβάνει πρόσφατες διορθώσεις ασφαλείας. Συνιστάται να ελέγξετε αν υπάρχει νεότερη ενημέρωση.",
            "2. Προστασία από κακόβουλες εφαρμογές",
            "Δεν ήταν δυνατή η επιβεβαίωση της κατάστασης",
            "Η μη διαθέσιμη απάντηση δεν σημαίνει ότι η προστασία είναι ανενεργή.",
            "Ελέγχθηκε η ρύθμιση επαλήθευσης, όχι η παρουσία κακόβουλου λογισμικού. Δεν πραγματοποιήθηκε σάρωση.",
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
        // The revised Lab22 asks the actual Google service about Verify Apps.
        // Missing service results remain neutral; enabled/disabled are distinct.
        assertTrue(s.contains("SafetyNet.getClient(this)"));
        assertTrue(s.contains(".isVerifyAppsEnabled()"));
        assertTrue(s.contains("if (!task.isSuccessful() || task.getResult() == null)"));
        assertTrue(s.contains("if (enabled) {"));
        assertTrue(s.contains("Ενεργή (επιβεβαιώθηκε από υπηρεσίες Google)"));
        assertTrue(s.contains("Ανενεργή — ενεργοποιήστε τη σάρωση στο Play Protect"));
        int unavailable = s.indexOf("final Runnable verifyAppsUnavailable");
        int request = s.indexOf("    try {", unavailable);
        assertTrue(unavailable >= 0 && request > unavailable);
        assertTrue(s.substring(unavailable, request).contains("logLabelValue("));
        assertFalse(s.substring(unavailable, request).contains("logLabelOkValue("));
        assertFalse(s.substring(unavailable, request).contains("logLabelWarnValue("));
        assertFalse("A settings screen cannot certify Play Protect", s.contains("package_verifier_enable"));
        // Online AOSP bulletin values can be green, but no malware
        // scan or verified system-wide protection may be inferred.
        assertTrue(s.contains("AndroidSecurityBulletinChecker.fetchLatestPublishedPatchLevel()"));
        assertTrue(s.contains("Δεν πραγματοποιήθηκε σάρωση."));
        assertFalse("No Play Protect failure is established by unknown status", s.contains("logLabelErrorValue("));
    }

    @Test public void completionIsNotConfusedWithVerifiedProtection() throws Exception {
        String s = section();
        assertTrue(s.contains("logOk(gr\n            ? \"Το Lab 22 ολοκληρώθηκε"));
        assertTrue(s.contains("Δεν πραγματοποιήθηκε σάρωση."));
    }
}
