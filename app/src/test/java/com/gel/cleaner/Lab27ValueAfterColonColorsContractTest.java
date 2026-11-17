package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.junit.Assert.*;

/** Lab 27: each colon-separated value must be styled for UI and both PDF exports. */
public final class Lab27ValueAfterColonColorsContractTest {
    private static String activity() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void centralizedLabelRendererColorsOnlyTheValueAndStoresItsPdfColor() throws Exception {
        String src = activity();
        int start = src.indexOf("private void logLabel(String label, String value, String color,");
        int end = src.indexOf("private void logInfo(String msg)", start);
        assertTrue(start >= 0 && end > start);
        String renderer = src.substring(start, end);
        assertTrue(renderer.contains("escape(label) + \": \" +"));
        assertTrue(renderer.contains("\"<font color='\" + color + \"'>\" + escape(value) + \"</font>\""));
        assertTrue(renderer.contains("GELServiceLog.addStyledValue("));
        assertTrue(src.contains("logLabel(label, value, \"#39FF14\")")); // confirmed match
        assertTrue(src.contains("logLabel(label, value, \"#E6E6E6\")")); // unknown/neutral
        assertTrue(src.contains("logLabel(label, value, \"#FFD700\"")); // warning
        assertTrue(src.contains("logLabel(label, value, \"#FF5555\"")); // deviation
    }

    @Test public void allLab27ContentValuesUseTypedLabelRowsNotBareHtml() throws Exception {
        String src = activity();
        int start = src.indexOf("private void lab30CombineFindings()");
        int end = src.indexOf("private void lab31FinalSummary()", start);
        assertTrue(start >= 0 && end > start);
        String lab = src.substring(start, end);
        // Direct appendHtml is only used for blank separators. Labels and readings
        // must go through logLabel...Value, which tracks the PDF value color.
        Matcher html = Pattern.compile("appendHtml\\s*\\(([^;]+)\\);").matcher(lab);
        int separators = 0;
        while (html.find()) {
            assertTrue("Raw HTML value bypassed color metadata: " + html.group(1),
                    html.group(1).trim().equals("\"<br>\""));
            separators++;
        }
        assertTrue(separators >= 10);
        assertFalse(lab.contains("GELServiceLog.add("));
        assertTrue(lab.contains("logLabelValue("));
        assertTrue(lab.contains("logLabelOkValue("));
        assertTrue(lab.contains("logLabelWarnValue("));
        assertTrue(lab.contains("logLabelErrorValue("));
    }

    @Test public void oemMetricsAndMissingDataHaveSeparateColorSemantics() throws Exception {
        String src = activity();
        int start = src.indexOf("private void lab27DisplayFactorySpecs(boolean gr)");
        int end = src.indexOf("// LAB 27 INTERNAL AUTO HELPERS", start);
        assertTrue(start >= 0);
        String factory = src.substring(start, end > start ? end : src.length());
        assertTrue(factory.contains("logLabelOkValue(rowName, observation"));
        assertTrue(factory.contains("logLabelErrorValue(rowName, observation"));
        assertTrue(factory.contains("logLabelValue(rowName,"));
        assertTrue(factory.contains("logLabelOkValue(gr ? \"Συμφωνίες προδιαγραφών\""));
        assertTrue(factory.contains("logLabelErrorValue(gr ? \"Αποκλίσεις προδιαγραφών\""));
        assertTrue(factory.contains("logLabelOkValue(gr ? \"Αποκλίσεις προδιαγραφών\""));
        assertTrue(factory.contains("logLabelValue(gr ? \"Μη επαληθευμένα χαρακτηριστικά\""));
        assertTrue(factory.contains("logLabelValue(gr ? \"Πηγή κατασκευαστή\""));
    }
}
