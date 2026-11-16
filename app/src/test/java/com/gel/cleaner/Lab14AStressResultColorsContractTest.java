package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Lab 14A: only values receive severity colors; calculations and evidence stay intact. */
public final class Lab14AStressResultColorsContractTest {
    private static String section() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String all = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int start = all.indexOf("private void lab14LogStressResult(");
        int end = all.indexOf("private void lab14LogFinalScore(", start);
        assertTrue(start >= 0 && end > start);
        return all.substring(start, end);
    }

    @Test public void availableLowDrainIsGreenWithoutHealthDiagnosis() throws Exception {
        String s = section();
        assertTrue(s.contains("drainRate >= 200 && drainRate <= 6000"));
        assertTrue(s.contains("drainLabel = \"Low\""));
        assertTrue(s.contains("do not interpret it as a battery-health (SoH) verdict"));
        assertTrue(s.contains("logLabelOkValue(\n                    gr ? \"Ρυθμός αποφόρτισης\""));
        assertTrue(s.contains("logLabelWarnValue(\n                 gr ? \"Ρυθμός αποφόρτισης\"")
                || s.contains("logLabelWarnValue(\n                gr ? \"Ρυθμός αποφόρτισης\""));
        assertTrue(s.contains("(res.drainMah * 3600000.0) / res.durationMs"));
    }

    @Test public void recommendationIsGreenForNormalAndYellowForCaution() throws Exception {
        String s = section();
        assertTrue(s.contains("logLabelWarnValue(recLabel, recommendation)"));
        assertTrue(s.contains("logLabelOkValue(recLabel, recommendation)"));
        assertTrue(s.contains("lab14_systemLimited[0] || \"Critical\".equals(labelSafe)"));
        assertTrue(s.contains("\"Weak\".equals(labelSafe)"));
        assertTrue(s.contains("\"Unknown\".equals(labelSafe)"));
        assertTrue(s.contains("calibrationDrift[0]"));
    }

    @Test public void summaryRespectsWarningsEvenIfCategoryLooksNormal() throws Exception {
        String s = section();
        assertTrue(s.contains("logLabelWarnValue(summaryLabel, summary)"));
        assertTrue(s.contains("logLabelOkValue(summaryLabel, summary)"));
        assertTrue(s.contains("(calibrationDrift != null && calibrationDrift.length > 0 && calibrationDrift[0]))"));
        assertTrue(s.contains("\"Excellent\".equals(labelSafe)"));
        assertTrue(s.contains("\"Good\".equals(labelSafe)"));
        assertTrue(s.contains("\"Normal\".equals(labelSafe)"));
        assertTrue(s.contains("logLabelValue(summaryLabel, summary)")); // unknown fallback neutral
    }
}
