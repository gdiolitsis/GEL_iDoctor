package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/** Lab14A color-only regression: leave measured evidence and severity limits intact. */
public final class Lab14ARepeatabilityThermalColorsContractTest {
    private static String source() throws Exception {
        Path file = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(file))
            file = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    private static String section(String all, String first, String last) {
        int start = all.indexOf(first);
        int end = all.indexOf(last, start + first.length());
        assertTrue("Lab14A section missing", start >= 0 && end > start);
        return all.substring(start, end);
    }

    @Test public void goodRepeatabilityIsGreenAndVariationRemainsInformational() throws Exception {
        String all = source();
        String block = section(all,
                "// 🔴 OPTIONAL DIAGNOSTIC EXTRA (NO GATING)",
                "// 🔴 STRUCTURE VALIDATION (FINAL — CONSISTENT)");
        assertTrue(all.contains("Math.abs(sag1 - sag2) < 0.08f"));
        assertTrue(block.contains("logLabelOkValue(repeatabilityLabel, gr ? \"Καλή\" : \"Good\")"));
        assertTrue(block.contains("logLabelInfoValue(repeatabilityLabel, gr ? \"Μεταβλητή\" : \"Variable\")"));
        assertFalse(block.contains("logLabelValue(\n            gr ? \"Επαναληψιμότητα\""));
    }

    @Test public void moderateThermalRiseIsGreenWithLargeRiseStillYellow() throws Exception {
        String block = section(source(),
                "// 🔴 THERMAL (FINAL — VALIDATED + INTERPRETED)",
                "// 🔴 DRAIN (FINAL — VALIDATED + INTERPRETED)");
        assertTrue(block.contains("if (deltaTemp >= 6.5f) {\n\n            logLabelWarnValue("));
        assertTrue(block.contains("} else if (deltaTemp >= 2.5f) {\n\n            // Moderate rise"));
        assertTrue(block.contains("logLabelOkValue(\n                    gr ? \"Θερμική μεταβολή\""));
        assertFalse(block.contains("logLabelValue(\n                    gr ? \"Θερμική μεταβολή\""));
        assertTrue(block.contains("if (lab14TempPeak >= 45f) {"));
        assertTrue(block.contains("logLabelWarnValue(gr ? \"Μέγιστη θερμοκρασία μπαταρίας\""));
    }
}
