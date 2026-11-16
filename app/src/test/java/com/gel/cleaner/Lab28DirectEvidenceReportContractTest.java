package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** First 30->29 migration stage: removed Lab27 must not leave stale findings. */
public final class Lab28DirectEvidenceReportContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void obsoleteLab27IsUnreachableAndItsTextParserIsGone() throws Exception {
        String s = source();
        assertFalse(s.contains("private void lab28HardwareStability()"));
        assertFalse(s.contains("this::lab28HardwareStability"));
        assertFalse(s.contains("private void showLab28Popup()"));
        assertFalse(s.contains("private static class Lab28EvidenceReader"));
        assertFalse(s.contains("27. Καταγραφές λειτουργίας και μετρήσεις"));
        assertFalse(s.contains("Λέξεις στο ιστορικό (μη επαληθευμένες)"));
    }

    @Test public void survivingLabsAndIndependentEvidenceArePreserved() throws Exception {
        String s = source();
        assertTrue(s.contains("this::lab25CrashHistory"));
        assertTrue(s.contains("this::lab26AppsFootprint"));
        assertTrue(s.contains("this::lab29DeviceAuthenticity"));
        assertTrue(s.contains("this::lab30CombineFindings"));
        assertTrue(s.contains("this::lab31FinalSummary"));
        assertTrue(s.contains("Map<String, Float> zones = null;"));
        assertTrue(s.contains("readThermalZones()"));
        assertTrue(s.contains("lab14BatteryTemp()"));
        assertTrue(s.contains("Lab29FactorySpecAudit.capture(this)"));
    }

    @Test public void noStaleOld27SnapshotInSurvivingSummaries() throws Exception {
        String s = source();
        int a = s.indexOf("private void lab30CombineFindings()");
        int b = s.indexOf("private void lab31FinalSummary()", a);
        assertTrue(a >= 0 && b > a);
        String cert = s.substring(a, b);
        String report = s.substring(b);
        assertFalse(cert.contains("p.getInt(\"lab28_obs_schema\""));
        assertFalse(cert.contains("p.getFloat(\"lab28_obs_cpu_c\""));
        assertFalse(cert.contains("p.getInt(\"lab28_obs_mention_mask\""));
        assertFalse(report.contains("p.getInt(\"lab28_obs_mention_mask\""));
        assertFalse(report.contains("LAB 27 — System records and measurements"));
    }

    @Test public void menuHasFiveRealLabsInLastGroupAndProRemains() throws Exception {
        String s = source();
        int a = s.indexOf("25. Έλεγχος κρίσιμων σφαλμάτων Android");
        int b = s.indexOf("if (!serviceLogInit)", a);
        assertTrue(a > 0 && b > a);
        String menu = s.substring(a, b);
        assertTrue(menu.contains("26. Απογραφή εφαρμογών και στοιχεία χρήσης"));
        assertTrue(menu.contains("PRO  28. Προέλεγχος Μεταχειρισμένης"));
        assertTrue(menu.contains("PRO  27. GEL Device Certification"));
        assertTrue(menu.contains("29. Τελική Τεχνική Αναφορά"));
        assertFalse(menu.contains("30. Τελική Τεχνική Αναφορά"));
    }
}
