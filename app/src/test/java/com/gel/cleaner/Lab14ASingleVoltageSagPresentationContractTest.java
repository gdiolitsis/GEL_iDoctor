package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Lab14A: one evidence-gated voltage-sag row, value-only color, no guessed 2A proxy. */
public final class Lab14ASingleVoltageSagPresentationContractTest {
    private static String src() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String between(String text, String start, String end) {
        int a = text.indexOf(start);
        int b = text.indexOf(end, a + start.length());
        assertTrue("Missing section " + start, a >= 0 && b > a);
        return text.substring(a, b);
    }

    @Test public void valueIsColoredByTheMeasuredStressCategory() throws Exception {
        String result = between(src(), "private void lab14LogStressResult(",
                "private void lab14LogFinalScore(");
        assertTrue(result.contains("if (!sagValid)"));
        assertTrue(result.contains("logLabelValue(gr ? \"Πτώση τάσης υπό φορτίο\""));
        assertTrue(result.contains("gr ? \"Μη μετρήσιμη\" : \"Not measurable\""));
        assertTrue(result.contains("\"Critical\".equals(labelSafe) || \"Weak\".equals(labelSafe)"));
        assertTrue(result.contains("logLabelWarnValue(gr ? \"Πτώση τάσης υπό φορτίο\""));
        assertTrue(result.contains("logLabelOkValue(gr ? \"Πτώση τάσης υπό φορτίο\""));
        assertTrue(result.contains("finalSag < 0.045f"));
        assertTrue(result.contains("finalSag < 0.080f"));
        assertTrue(result.contains("finalSag < 0.120f"));
    }

    @Test public void aSecondVoltageSagOutputIsNeverEmittedBeforeTheFinalResult() throws Exception {
        String full = src();
        String live = between(full, "// 🔴 SOFT ELECTRICAL WARNING",
                "// 🔴 OPTIONAL DIAGNOSTIC EXTRA");
        assertFalse(live.contains("logLabelValue(\n                gr ? \"Πτώση τάσης"));
        assertFalse(live.contains("gr ? \"Πτώση τάσης υπό φορτίο\""));
        assertFalse(live.contains("Voltage sag (experimental)"));
        assertTrue(live.contains("lab14LogStressResult()"));
        assertTrue(full.contains("lab14LogStressResult(\n"));
    }

    @Test public void removeHypotheticalResistanceOnlyNotRealMeasurements() throws Exception {
        String full = src();
        String result = between(full, "private void lab14LogStressResult(",
                "private void lab14LogFinalScore(");
        assertFalse(full.contains("Πειραματικός δείκτης ΔV/2A"));
        assertFalse(full.contains("Experimental ΔV/2A proxy"));
        assertFalse(full.contains("Experimental voltage-response proxy (not ESR)"));
        assertFalse(result.contains("rEst = finalSag"));
        assertTrue(result.contains("if (rValid)"));
        assertTrue(result.contains("Υπολογισμένη ικανότητα ισχύος κατά τη δοκιμή"));
        assertTrue(result.contains("Ανάκαμψη τάσης"));
    }
}
