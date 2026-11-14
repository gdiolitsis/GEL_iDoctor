package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Source-level UI evidence contract; physical-device validation remains required. */
public final class Lab19LogColorContractTest {
    private static String lab19() throws Exception {
        Path file = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(file))
            file = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        int a = source.indexOf("private void lab19RamSnapshot()");
        int b = source.indexOf("// LAB 20 —", a);
        assertTrue("Lab 19 boundaries", a >= 0 && b > a);
        return source.substring(a, b);
    }

    @Test public void noOsLowMemoryFlagIsGreenButNoHardwareHealthClaim() throws Exception {
        String t = lab19();
        int a = t.indexOf("case NO_OS_LOW_MEMORY_SIGNAL:");
        int b = t.indexOf("break;", a);
        assertTrue(a >= 0 && b > a);
        String block = t.substring(a, b);
        assertTrue(block.contains("logLabelOkValue("));
        assertTrue(block.contains("τρέχον στιγμιότυπο"));
        assertTrue(block.contains("No low-memory flag in the current snapshot"));
    }

    @Test public void highAndMediumHeuristicsAreWarningsNotGreenHealth() throws Exception {
        String t = lab19();
        assertTrue(t.contains("if (\"High\".equals(pressureGrade) || \"Medium\".equals(pressureGrade))"));
        assertTrue(t.contains("if (\"High\".equals(zramDep) || \"Medium\".equals(zramDep))"));
        assertTrue(t.contains("logLabelWarnValue("));
        assertTrue(t.contains("String swapRatioLabel = humanPressureLabel(zramDep);"));
        assertTrue(t.contains("logLabelValue(\n                        gr ? \"Ενδεικτική πίεση μνήμης\""));
    }

    @Test public void observedQuantitiesRemainGreenAndUnknownsWhite() throws Exception {
        String t = lab19();
        assertTrue(t.contains("logLabelOkValue(\n                gr ? \"Χρήση\""));
        assertTrue(t.contains("logLabelOkValue(\n                        gr ? \"Χρήση Swap\""));
        assertTrue(t.contains("logLabelOkValue(\n                        \"MemFree\""));
        assertTrue(t.contains("logLabelOkValue(\n                        \"Cached\""));
        assertTrue(t.contains("logLabelValue(\n                gr ? \"Όρια ελέγχου\""));
    }

    @Test public void rootEvidenceIsGreenButUnsupportedEffectStaysNeutral() throws Exception {
        String t = lab19();
        assertTrue(t.contains("logLabelOkValue(\n                        gr ? \"Επέκταση μνήμης\""));
        assertTrue(t.contains("logLabelOkValue(\n                        gr ? \"Cached μνήμη\""));
        assertTrue(t.contains("logLabelValue(\n                        gr ? \"Επίδραση\""));
    }
}
