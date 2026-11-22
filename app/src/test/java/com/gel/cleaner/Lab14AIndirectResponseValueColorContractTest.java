package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/** Lab 14A: color only the measured/observed value, without changing calculations. */
public final class Lab14AIndirectResponseValueColorContractTest {
    private static String source() throws Exception {
        Path file = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(file))
            file = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    private static String region(String source, String begin, String end) {
        int start = source.indexOf(begin);
        int stop = source.indexOf(end, start + begin.length());
        assertTrue("Lab 14A region missing", start >= 0 && stop > start);
        return source.substring(start, stop);
    }

    @Test public void normalIndirectVoltageResponsesAreGreenAndElevatedSagWarns() throws Exception {
        String part = region(source(),
                "// 🔴 STRUCTURAL INTEGRITY (FINAL — SAG BASED ONLY)",
                "// BATTERY BEHAVIOUR (FINAL SAFE)");
        assertTrue(part.contains("if (finalSag < 0.05f)"));
        assertTrue(part.contains("else if (finalSag < 0.12f)"));
        assertEquals("Both small and moderate drops are classified non-warning",
                2, part.split("logLabelOkValue\\(", -1).length - 1);
        assertTrue(part.contains("logLabelWarnValue(\n                gr ? \"Έμμεση απόκριση τάσης\""));
        assertTrue(part.contains("Μέτρια πτώση τάσης κατά τη δοκιμή"));
        assertFalse(part.contains("logLabelValue(\n                gr ? \"Έμμεση απόκριση τάσης\""));
    }

    @Test public void healthyBatteryBehaviorIsGreenAndWarningRemainsYellow() throws Exception {
        String part = region(source(),
                "// BATTERY BEHAVIOUR (FINAL SAFE)",
                "// PARTIAL / FULL MODE DECISION");
        assertTrue(part.contains("if (!hasSag || !validDrain || lab14_systemLimited[0])"));
        assertTrue(part.contains("else if (res.batteryBehaviourWarning)"));
        assertTrue(part.contains("logLabelOkValue(\n            gr ? \"Συμπεριφορά μπαταρίας\""));
        assertTrue(part.contains("logLabelWarnValue(\n            gr ? \"Συμπεριφορά μπαταρίας\""));
        assertFalse(part.contains("logLabelValue(\n            gr ? \"Συμπεριφορά μπαταρίας\""));
    }

    @Test public void voltageBehaviorSummaryKeepsPositiveAndWarningColors() throws Exception {
        String part = region(source(),
                "private void lab14LogVoltageBehaviorSummary(",
                "// LAB 15 - Charging System Diagnostic (SMART)");
        assertTrue(part.contains("logLabelOkValue(gr ? \"Συμπεριφορά\""));
        assertTrue(part.contains("logLabelWarnValue(gr ? \"Συμπεριφορά\""));
        assertTrue(part.contains("Πρώιμη πτώση με μερική σταθεροποίηση / ανάκαμψη"));
    }
}
