package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** LAB 26 must report inventory, requested capabilities and real readings only. */
public final class Lab26EvidenceOnlyReportContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String lab() throws Exception {
        String s = source();
        int a = s.indexOf("private void lab26AppsFootprint() {");
        int b = s.indexOf("// ROOT HELPER — BEST EFFORT DIRECTORY SIZE", a);
        assertTrue(a >= 0 && b > a);
        return s.substring(a, b);
    }

    @Test public void accurateMenuAndHeader() throws Exception {
        String s = source();
        assertTrue(s.contains("26. Απογραφή εφαρμογών και στοιχεία χρήσης"));
        String lab = lab();
        assertTrue(lab.contains("LAB 26 — Απογραφή εφαρμογών και στοιχεία χρήσης"));
        assertFalse(lab.contains("LAB 26 — Ανάλυση Επιπτώσεων"));
    }

    @Test public void installedInventoryAndRequestedCapabilityCountsAreGreen() throws Exception {
        String s = lab();
        assertTrue(s.contains("AppImpactEngine.analyze(this, isDeviceRooted())"));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Σύνολο εφαρμογών\""));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Εφαρμογές χρήστη\""));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Εφαρμογές συστήματος\""));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Μικρόφωνο\""));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Κάμερα\""));
        assertTrue(s.contains("άδειες που ζητούν οι εφαρμογές, όχι πραγματική χρήση"));
    }

    @Test public void actualTrafficIsShownButNoImaginaryBatteryUsage() throws Exception {
        String s = lab();
        assertTrue(s.contains("if (app == null || app.dataBytesSinceBoot < 1024L) continue;"));
        assertTrue(s.contains("logLabelOkValue(app.safeLabel(), humanBytes(app.dataBytesSinceBoot));"));
        assertTrue(s.contains("if (!r.usageAccessOk) {"));
        assertTrue(s.contains("Δεν είναι ενεργή η πρόσβαση σε στατιστικά χρήσης."));
        assertTrue(s.contains("if (app == null || app.fgMs24h <= 0L) continue;"));
        assertFalse(s.contains("Δείκτης Επιρροής"));
        assertFalse(s.contains("Battery exposure"));
        assertFalse(s.contains("Επικίνδυνες Άδειες"));
        assertFalse(s.contains("r.riskPoints"));
        assertFalse(s.contains("a.estImpactScore"));
        assertFalse(s.contains("Ενδεικτική κατάταξη δυνατοτήτων"));
        assertFalse(s.contains("logInfo(a.pkg);"));
    }

    @Test public void completionIsGreenAndUnavailableDataRemainsNeutral() throws Exception {
        String s = lab();
        assertTrue(s.contains("logLabelValue(gr ? \"Στοιχεία χρήσης\""));
        assertTrue(s.contains("Το Lab 26 ολοκληρώθηκε — καταγράφηκαν οι διαθέσιμες εφαρμογές και μετρήσεις."));
        assertTrue(s.contains("logOk(gr ? \"Το Lab 26 ολοκληρώθηκε"));
        assertTrue(s.contains("SUMMARY: APPS_INVENTORY_VISIBLE="));
    }
}
