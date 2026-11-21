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
        assertTrue(src.contains("logLabel(label, value, \"#E6E6E6\")")); // unavailable and explanatory values
        assertTrue(src.contains("logLabel(label, value, \"#87CEFA\", GelDiagnosticSeverity.Level.NEUTRAL)")); // observed informational values
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

    @Test public void thermalValuesUseLab16SensorSpecificColorSemantics() throws Exception {
        String src = activity();
        int start = src.indexOf("private boolean lab27ThermalIsElevated(");
        int end = src.indexOf("private void lab27DisplayFactorySpecs(", start);
        assertTrue(start >= 0 && end > start);
        String thermal = src.substring(start, end);
        assertTrue(thermal.contains("Lab16ThermalCorrelation.level(sector, celsius)"));
        assertTrue(thermal.contains("Lab16ThermalCorrelation.Level.ELEVATED"));
        assertTrue(thermal.contains("Lab16ThermalCorrelation.Level.HIGH"));
        assertTrue(thermal.contains("logLabelWarnValue(label, value)"));
        assertTrue(thermal.contains("logLabelOkValue(label, value)"));
        assertTrue(thermal.contains("logLabelValue(label, AppLang.isGreek(this)"));
        String lab = src.substring(src.indexOf("private void lab30CombineFindings()"), start);
        assertTrue(lab.contains("lab27LogThermalValue(\"CPU\""));
        assertTrue(lab.contains("lab27LogThermalValue(\"GPU\""));
        assertTrue(lab.contains("lab27LogThermalValue(\"PMIC\""));
        assertTrue(lab.contains("lab27LogThermalValue(gr ? \"Μπαταρία\""));
    }

    @Test public void oemMetricsAndMissingDataHaveSeparateColorSemantics() throws Exception {
        String src = activity();
        int start = src.indexOf("private void lab27DisplayFactorySpecs(boolean gr)");
        int end = src.indexOf("private StorageSnapshot readStorageSnapshot()", start);
        assertTrue(start >= 0 && end > start);
        String factory = src.substring(start, end);
        assertTrue(factory.contains("logLabelOkValue(rowName, observation"));
        assertTrue(factory.contains("logLabelErrorValue(rowName, observation"));
        assertTrue(factory.contains("logLabelInfoValue(rowName,"));
        assertTrue(factory.contains("logLabelOkValue(gr ? \"Συμφωνίες προδιαγραφών\""));
        assertTrue(factory.contains("logLabelErrorValue(gr ? \"Αποκλίσεις προδιαγραφών\""));
        assertTrue(factory.contains("logLabelOkValue(gr ? \"Αποκλίσεις προδιαγραφών\""));
        assertTrue(factory.contains("logLabelInfoValue(gr ? \"Χαρακτηριστικά χωρίς αυτόματη σύγκριση\""));
        assertTrue(factory.contains("logLabelInfoValue(gr ? \"Πηγή κατασκευαστή\""));
    }

    @Test public void measuredLab27ValuesHaveInformationalColorsNotFalseGreenWarnings() throws Exception {
        String src = activity();
        int start = src.indexOf("private void lab30CombineFindings()");
        int end = src.indexOf("private void lab31FinalSummary()", start);
        assertTrue(start >= 0 && end > start);
        String lab = src.substring(start, end);
        assertTrue(lab.contains("lab27LogScoreValue(gr ? \"Κατάσταση\" : \"Status\", batteryScore, batteryFlag, gr);"));
        assertTrue(lab.contains("lab27LogScoreValue(gr ? \"Κατάσταση\" : \"Status\", securityScore, securityFlag, gr);"));
        assertTrue(lab.contains("lab27LogScoreValue(gr ? \"Απόδοση\" : \"Performance\", performanceScore, \"⚪\", gr);"));
        assertTrue(lab.contains("lab27LogScoreValue(gr ? \"Ασφάλεια\" : \"Security\", securityScore, securityFlag, gr);"));
        assertTrue(lab.contains("lab27LogScoreValue(gr ? \"Ιδιωτικότητα\" : \"Privacy\", privacyScore, privacyFlag, gr);"));
        assertTrue(lab.contains("logLabelInfoValue(gr ? \"Μετρήσεις\" : \"Counts\""));
        assertTrue(lab.contains("logLabelInfoValue(gr ? \"Ενημέρωση ασφαλείας\" : \"Patch level\""));
        assertTrue(lab.contains("logLabelInfoValue(gr ? \"Χρόνος διάγνωσης\""));
        assertTrue(lab.contains("logLabelInfoValue(gr ? \"ID διάγνωσης\""));
        assertTrue(lab.contains("logLabelInfoValue(gr ? \"Στοιχεία\" : \"State\", batteryReadings)"));
        // A computed numerical index is not verified component health.
        assertTrue(lab.contains("private void lab27LogScoreValue(String label, int score, String flag, boolean gr)"));
        assertTrue(lab.contains("logLabelInfoValue(label, flag + \" (\" + score + \")\")"));
        assertTrue(lab.contains("logLabelValue(label, gr ? \"ΑΝΕΠΑΡΚΗ ΔΕΔΟΜΕΝΑ\""));
        // Unknown/limits remain neutral; verified agreement remains green.
        assertTrue(lab.contains("logLabelValue(gr ? \"Οδηγία / εξήγηση\""));
        assertTrue(lab.contains("logLabelOkValue(rowName, observation"));
        assertTrue(lab.contains("logLabelErrorValue(rowName, observation"));
    }
}
