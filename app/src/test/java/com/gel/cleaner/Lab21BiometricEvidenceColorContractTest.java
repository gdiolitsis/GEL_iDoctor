package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Lab21: one lock configuration fact, native biometric evidence, no duplicates. */
public final class Lab21BiometricEvidenceColorContractTest {
    private static String lab21() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String s = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int a = s.indexOf("private void lab21ScreenLock()");
        int b = s.indexOf("// ROOT HELPERS", a);
        assertTrue(a >= 0 && b > a);
        return s.substring(a, b);
    }

    @Test public void configuredLockHasOneMeaningfulResult() throws Exception {
        String s = lab21();
        assertTrue(s.contains("Ρύθμιση κλειδώματος οθόνης"));
        assertTrue(s.contains("Ρυθμισμένο (PIN / Μοτίβο / Κωδικός)"));
        assertFalse(s.contains("Κύριο επίπεδο ασφάλειας"));
        assertFalse(s.contains("Διαπιστευτήριο συσκευής"));
        assertFalse(s.contains("Επίπεδο ευκολίας"));
    }

    @Test public void liveBiometricAvailabilityIsNotPrematurePass() throws Exception {
        String s = lab21();
        assertTrue(s.contains("BiometricManager.BIOMETRIC_SUCCESS"));
        assertTrue(s.contains("Δυνατή ταυτοποίηση Android· εκκρεμεί ζωντανή δοκιμή"));
        assertTrue(s.contains("logLabelOkValue(\n                        gr ? \"Βιομετρικά\""));
        assertTrue(s.contains("logLabelOkValue(\n                                gr ? \"Αποτέλεσμα\" : \"Result\",\n                                \"PASS\""));
        assertTrue(s.contains("Το Lab 21 ολοκληρώθηκε (επιτυχής ζωντανή βιομετρική ταυτοποίηση)."));
    }

    @Test public void unknownEncryptionAndRootUnavailableAreNotInvented() throws Exception {
        String s = lab21();
        assertTrue(s.contains("Κρυπτογράφηση μετά από επανεκκίνηση"));
        assertTrue(s.contains("Δεν ελέγχθηκε"));
        assertFalse(s.contains("gr ? \"Μη διαθέσιμη\" : \"Not available\""));
        assertTrue(s.contains("gr ? \"Λειτουργία Root\" : \"Root mode\""));
    }
}
