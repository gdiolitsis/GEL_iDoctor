package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Regression contract: an unavailable reboot history is informational, not OK. */
public final class Lab20RebootHistoryColorContractTest {
    private static String source() throws Exception {
        Path file = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(file))
            file = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    @Test public void historyValueUsesCyanAndNeutralReportSeverity() throws Exception {
        String s = source();
        assertTrue(s.contains("private void logLabelInfoValue(String label, String value)"));
        assertTrue(s.contains("logLabel(label, value, \"#87CEFA\", GelDiagnosticSeverity.Level.NEUTRAL);"));
    }

    @Test public void bothHistoryPathsUseInfoColorOnly() throws Exception {
        String s = source();
        int a = s.indexOf("private void lab20UptimeHints()");
        int b = s.indexOf("// LAB 21 —", a);
        assertTrue(a >= 0 && b > a);
        String lab = s.substring(a,b);
        assertEquals(2, lab.split("logLabelInfoValue\\(", -1).length - 1);
        assertTrue(lab.contains("ΜΗ ΔΙΑΘΕΣΙΜΟ ΙΣΤΟΡΙΚΟ"));
        assertTrue(lab.contains("Δεν συλλέχθηκε επαληθευμένο ιστορικό επανεκκινήσεων"));
        assertTrue(lab.contains("RebootHistoryEvidence.classify(-1, 0L)"));
        assertTrue(lab.contains("logLabelOkValue(\"Uptime\", upStr);"));
    }
}
