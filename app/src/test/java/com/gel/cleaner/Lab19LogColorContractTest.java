package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** New policy: actual Android RAM signals and kernel measurements, no synthetic pressure grades. */
public final class Lab19LogColorContractTest {
    private static String lab19() throws Exception {
        Path file = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(file))
            file = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        int a = source.indexOf("private void lab19RamSnapshot()");
        int b = source.indexOf("// LAB 20 —", a);
        assertTrue("Lab19 boundaries", a >= 0 && b > a);
        return source.substring(a, b);
    }

    @Test public void androidLowMemoryFlagIsSoleAutomaticPressureWarning() throws Exception {
        String t = lab19();
        assertTrue(t.contains("if (mi.lowMemory) {"));
        assertTrue(t.contains("Το Android αναφέρει κατάσταση χαμηλής μνήμης"));
        assertTrue(t.contains("Δεν δηλώθηκε κατάσταση low-memory στο τρέχον στιγμιότυπο"));
        assertTrue(t.contains("logLabelWarnValue("));
        assertTrue(t.contains("logLabelOkValue("));
        assertFalse(t.contains("if (\"High\".equals(pressureGrade)"));
        assertFalse(t.contains("if (\"High\".equals(zramDep)"));
    }

    @Test public void measuredValuesAreShownWithoutSpeculativeGrades() throws Exception {
        String t = lab19();
        assertTrue(t.contains("humanBytes(used)"));
        assertTrue(t.contains("if (mi.threshold > 0L)"));
        assertTrue(t.contains("Android low-memory threshold"));
        assertTrue(t.contains("snap.memAvailableKb > 0"));
        assertTrue(t.contains("MemAvailable (kernel)"));
        assertTrue(t.contains("RamEvidencePolicy.observedSwapUsedKb"));
        assertTrue(t.contains("Swap σε χρήση"));
        assertTrue(t.contains("MemFree"));
        assertTrue(t.contains("Cached"));
        assertFalse(t.contains("Ενδεικτική πίεση μνήμης"));
        assertFalse(t.contains("Ενδεικτική αναλογία Swap/RAM"));
    }

    @Test public void absenceOfOptionalKernelDataDoesNotCreateWarning() throws Exception {
        String t = lab19();
        assertTrue(t.contains("No low-level observation: omit it from the human report."));
        assertTrue(t.contains("Optional kernel observations unavailable: no diagnostic entry."));
        assertFalse(t.contains("Δείκτης πίεσης"));
    }

    @Test public void rootObservationsDoNotCertifyHealth() throws Exception {
        String t = lab19();
        assertTrue(t.contains("Επέκταση μνήμης"));
        assertTrue(t.contains("Cached μνήμη"));
        assertTrue(t.contains("Επίδραση"));
    }
}
