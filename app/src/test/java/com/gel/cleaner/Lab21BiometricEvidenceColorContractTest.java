package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Source-level evidence/color contract; device biometric test still required. */
public final class Lab21BiometricEvidenceColorContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String s = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int a = s.indexOf("private void lab21ScreenLock()");
        int b = s.indexOf("// ROOT HELPERS", a);
        assertTrue(a >= 0 && b > a);
        return s.substring(a, b);
    }

    @Test public void deviceCredentialAndBiometricCapabilityHaveGuardedGreenValues() throws Exception {
        String s = source();
        assertTrue(s.contains("if (secureKnown && secure) {"));
        assertTrue(s.contains("logLabelOkValue(\n            gr ? \"Κύριο επίπεδο ασφάλειας\""));
        assertTrue(s.contains("if (biometricSupported) {\n    logLabelOkValue("));
        assertTrue(s.contains("logLabelOkValue(\n            gr ? \"Επίπεδο ευκολίας\""));
        assertTrue(s.contains("if (r == android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS)"));
        assertTrue(s.contains("logLabelOkValue(\n                        gr ? \"Βιομετρικά\""));
    }

    @Test public void encryptionUnknownAndUnlockedStateStayNeutral() throws Exception {
        String s = source();
        assertTrue(s.contains("logLabelValue(\n            gr ? \"Κρυπτογράφηση μετά από επανεκκίνηση\""));
        assertTrue(s.contains("logLabelValue(\n                        gr ? \"Κατάσταση\" : \"State\""));
        assertTrue(s.contains("gr ? \"Μη διαθέσιμη\" : \"Not available\""));
        assertTrue(s.contains("gr ? \"Δεν ελέγχθηκε\" : \"Not assessed\""));
    }

    @Test public void actualBiometricPassRemainsGreenWhileFutureScopeIsNeutral() throws Exception {
        String s = source();
        assertTrue(s.contains("logLabelOkValue(\n                                gr ? \"Αποτέλεσμα\" : \"Result\",\n                                \"PASS\""));
        assertTrue(s.contains("logLabelValue(\n                gr ? \"Έκταση ελέγχου\" : \"Test scope\""));
        assertTrue(s.contains("Το Lab 21 ολοκληρώθηκε (επιτυχής ζωντανή βιομετρική ταυτοποίηση)."));
        assertTrue(s.contains("logLabelValue(\n                                gr ? \"Σημείωση OEM\""));
    }
}
