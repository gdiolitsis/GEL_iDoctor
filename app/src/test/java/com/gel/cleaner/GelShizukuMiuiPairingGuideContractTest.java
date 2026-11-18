package com.gel.cleaner;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/** Regression for Xiaomi/MIUI Shizuku pairing guidance in GEL. */
public final class GelShizukuMiuiPairingGuideContractTest {
    private static String ui() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/GelShizukuUi.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/GelShizukuUi.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void targetedOnlyToXiaomiFamilyOnAndroid11Plus() throws Exception {
        String s = ui();
        assertTrue(s.contains("Build.MANUFACTURER"));
        assertTrue(s.contains("Build.BRAND"));
        assertTrue(s.contains("\"xiaomi\".equalsIgnoreCase(maker)"));
        assertTrue(s.contains("\"redmi\".equalsIgnoreCase(brand)"));
        assertTrue(s.contains("\"poco\".equalsIgnoreCase(brand)"));
        assertTrue(s.contains("if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R\n                    && isXiaomiFamilyDevice())"));
        assertTrue(s.contains("addXiaomiPairingHelp(panel)"));
    }

    @Test public void normalGuideExplainsSixDigitCodeAndNotification() throws Exception {
        String s = ui();
        assertTrue(s.contains("Άνοιξε την ίδια τη ρύθμιση"));
        assertTrue(s.contains("Σύζευξη συσκευής με κωδικό"));
        assertTrue(s.contains("6ψήφιο κωδικό στην ειδοποίηση"));
        assertTrue(s.contains("Pair device with pairing code"));
        assertTrue(s.contains("six-digit code in Shizuku's notification"));
    }

    @Test public void optionalMiuiHelpIsCollapsedAndBilingual() throws Exception {
        String s = ui();
        assertTrue(s.contains("help.setVisibility(View.GONE)"));
        assertTrue(s.contains("help.setVisibility(expand ? View.VISIBLE : View.GONE)"));
        assertTrue(s.contains("ΔΕΣ ΛΥΣΕΙΣ ΣΥΖΕΥΞΗΣ"));
        assertTrue(s.contains("PAIRING TROUBLESHOOTING"));
        assertTrue(s.contains("ΑΠΟΚΡΥΨΗ ΟΔΗΓΙΩΝ"));
        assertTrue(s.contains("MIUI / HyperOS"));
        assertTrue(s.contains("Shizuku notifications and local-network"));
    }

    @Test public void safeTargetedTroubleshootingDoesNotMandatePermissionChanges() throws Exception {
        String s = ui();
        // Java source wraps this visible phrase across adjacent string literals.
        assertTrue(s.contains("Χωρίς "));
        assertTrue(s.contains("περιορισμούς"));
        assertTrue(s.contains("Notification shade"));
        assertTrue(s.contains("USB debugging (Security options)"));
        assertTrue(s.contains("ΜΟΝΟ αν το Shizuku αναφέρει περιορισμένη άδεια"));
        assertTrue(s.contains("ONLY if Shizuku reports limited ADB permission"));
        assertTrue(s.contains("Το iDoctor δεν αλλάζει καμία ρύθμιση."));
        assertTrue(s.contains("iDoctor does not change any setting."));
        assertFalse(s.contains("Settings.Secure.put"));
        assertFalse(s.contains("WRITE_SECURE_SETTINGS"));
    }

    @Test public void preserveHumanIntroAndRealSecurityChecks() throws Exception {
        String s = ui();
        assertTrue(s.contains("Προηγμένος έλεγχος μέσω SHIZUKU"));
        assertTrue(s.contains("and follow its setup instructions.\", false, 0xFF39FF14)"));
        assertTrue(s.contains("Shizuku ενεργό — πάτησε για τερματισμό"));
        assertTrue(s.contains("session.requestFromUser()"));
        assertTrue(s.contains("ΣΥΝΕΧΕΙΑ ΧΩΡΙΣ SHIZUKU"));
    }
}
