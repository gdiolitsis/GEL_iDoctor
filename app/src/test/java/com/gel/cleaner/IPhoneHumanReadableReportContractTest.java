package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public final class IPhoneHumanReadableReportContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/IPhoneLabsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/IPhoneLabsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String lab8() throws Exception {
        String s = source();
        int a = s.indexOf("private void runFinalServiceRecommendationLab() {");
        int b = s.indexOf("private void runDemoDiagnostics() {", a);
        assertTrue(a >= 0 && b > a);
        return s.substring(a, b);
    }

    @Test public void finalServiceSummaryNeverGradesUnmeasuredDeviceHealth() throws Exception {
        String text = lab8();
        assertFalse(text.contains("score"));
        assertFalse(text.contains("/100"));
        assertTrue(text.contains("Τι δεν έχει επιβεβαιωθεί:"));
        assertTrue(text.contains("Πότε χρειάζεται τεχνικός:"));
        assertTrue(text.contains("IPhonePanicHumanizer.meaning(e.getKey(), gr)"));
    }

    @Test public void incidentLabelsAreProducedFromObservedTypesNotInventedHealth() throws Exception {
        String s = source();
        assertTrue(s.contains("IPhonePanicHumanizer.event(sigCrashType, gr)"));
        assertTrue(s.contains("IPhonePanicHumanizer.domain(sigDomain, gr)"));
        assertTrue(s.contains("Συμβάν \" + incidentNumber + \" — με απλά λόγια:"));
        assertTrue(s.contains("Τα συμβάντα εξετάστηκαν χωριστά"));
    }

    @Test public void demoInputContainsTwoSeparatePanicRecords() throws Exception {
        String s = source();
        int a = s.indexOf("private String buildDemoPanicLogs() {");
        int b = s.indexOf("private String normalizeDomain(", a);
        assertTrue(a >= 0 && b > a);
        String demo = s.substring(a, b);
        assertTrue(demo.contains("ZIP FILE: PanicLog1"));
        assertTrue(demo.contains("ZIP FILE: PanicLog2"));
        assertTrue(demo.contains("watchdog timeout"));
        assertTrue(demo.contains("thermal shutdown"));
    }
}
