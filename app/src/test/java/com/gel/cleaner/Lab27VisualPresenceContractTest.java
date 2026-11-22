package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/** v70 QA: OEM visual presence is not a duplicate functional laboratory test. */
public final class Lab27VisualPresenceContractTest {
    private static String activity() throws Exception {
        Path file = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(file))
            file = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    private static String section(String text, String from, String to) {
        int start = text.indexOf(from);
        int end = text.indexOf(to, start + from.length());
        assertTrue("Source section missing: " + from, start >= 0 && end > start);
        return text.substring(start, end);
    }

    @Test public void physicalPopupRequiresOnlyVisiblePorts() throws Exception {
        String popup = section(activity(), "private void lab27PromptPhysicalConfirmation(",
                "private void lab30CombineFindings()");
        assertTrue(popup.contains("Θύρα USB-C: υπάρχει (οπτική επιβεβαίωση)."));
        assertTrue(popup.contains("Υποδοχή 3,5 mm: υπάρχει (οπτική επιβεβαίωση)."));
        assertTrue(popup.contains("Φυσική θήκη SIM: υπάρχει (οπτική επιβεβαίωση)."));
        assertTrue(popup.contains("USB-C port is physically present (visual confirmation)."));
        assertTrue(popup.contains("3.5 mm jack is physically present (visual confirmation)."));
        assertTrue(popup.contains("Physical SIM tray is present (visual confirmation)."));
        assertFalse(popup.contains("δοκίμασα επιτυχή φόρτιση με καλώδιο"));
        assertFalse(popup.contains("δοκίμασα ήχο με ακουστικά"));
        assertFalse(popup.contains("I tested audio through wired headphones"));
        assertTrue(popup.contains("Το Lab 15 αξιολογεί ήδη τη φόρτιση"));
        assertTrue(popup.contains("Δεν απαιτούνται ενσύρματα ακουστικά"));
        assertTrue(popup.contains("Το Lab 12 καταγράφει στοιχεία SIM/eSIM"));
        assertTrue(popup.contains("Lab27CertificationGate.physicalChecksComplete("));
        assertTrue(popup.contains("Lab27CertificationGate.physicalMatchesOem("));
        assertTrue(popup.contains("lab27UserPreflightAuthorized = true;"));
    }

    @Test public void factoryReportDistinguishesAutomaticAndHumanEvidence() throws Exception {
        String report = section(activity(), "private void lab27DisplayFactorySpecs(boolean gr)",
                "private StorageSnapshot readStorageSnapshot()");
        assertTrue(report.contains("\"USB connector\".equals(specRow.english)"));
        assertTrue(report.contains("\"3.5 mm jack\".equals(specRow.english)"));
        assertTrue(report.contains("\"SIM tray\".equals(specRow.english)"));
        assertTrue(report.contains("specRow.verdict == Lab29FactorySpecAudit.Verdict.UNKNOWN"));
        assertTrue(report.contains("Σύμφωνα με τη δήλωση χρήστη: "));
        assertTrue(report.contains("According to user declaration: "));
        assertTrue(report.contains("verifiedByUser++"));
        assertTrue(report.contains("mismatchedByUser++"));
        assertTrue(report.contains("factory.unknowns - verifiedByUser - mismatchedByUser"));
        assertTrue(report.contains("Χαρακτηριστικά χωρίς διαθέσιμη σύγκριση"));
        assertTrue(report.contains("logLabelOkValue(rowName, value)"));
        assertTrue(report.contains("logLabelErrorValue(rowName, value)"));
        assertTrue(report.contains("continue; // Do not also say this port is unverified."));
    }

    @Test public void oldCertificatesCannotBypassVisualDeclaration() {
        assertEquals(4, Lab27CertificationGate.SCHEMA);
        assertTrue(Lab27CertificationGate.physicalChecksComplete(
                true, true, false, true, false));
        assertTrue(Lab27CertificationGate.physicalMatchesOem(
                Lab27CertificationGate.Presence.PRESENT,
                Lab27CertificationGate.Presence.PRESENT,
                Lab27CertificationGate.Presence.PRESENT,
                true, true, false, true, false));
        assertFalse(Lab27CertificationGate.physicalMatchesOem(
                Lab27CertificationGate.Presence.PRESENT,
                Lab27CertificationGate.Presence.PRESENT,
                Lab27CertificationGate.Presence.PRESENT,
                true, false, true, true, false));
    }
}
