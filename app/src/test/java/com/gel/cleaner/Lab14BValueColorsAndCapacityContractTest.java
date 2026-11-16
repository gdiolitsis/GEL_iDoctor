package com.gel.cleaner;

import org.junit.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

/** Scope: Lab 14B/14Γ display colors only; preserve independent capacity math. */
public final class Lab14BValueColorsAndCapacityContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
    private static String section(String s, String begin, String end) {
        int a = s.indexOf(begin);
        int b = s.indexOf(end, a + begin.length());
        assertTrue("Missing section boundaries", a >= 0 && b > a);
        return s.substring(a, b);
    }
    @Test public void batteryReferenceMathIsNotReplacedOrUnified() throws Exception {
        String s = source();
        assertTrue(s.contains("(long) (start.chargeNowMah / percent)"));
        assertTrue(s.contains("(long) (snap0.chargeNowMah / (pct / 100.0f))"));
        assertFalse(s.contains("lab14CommonReferenceCapacityMah"));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Χωρητικότητα αναφοράς (14B/Γ)\""));
        assertTrue(s.contains("EXTRAPOLATED_FROM_LEVEL"));
    }
    @Test public void eligibleMeasurementsShowColorInLab14BAndUnknownStaysNeutral() throws Exception {
        String s = section(source(),
                "private boolean checkLab14BConditions() {",
                "// LAB 14B — PRE TEST ADVISORY");
        assertTrue(s.contains("logLabelOkValue(gr ? \"Επίπεδο μπαταρίας\""));
        assertTrue(s.contains("logLabelWarnValue(gr ? \"Επίπεδο μπαταρίας\""));
        assertTrue(s.contains("logLabelOkValue(\"Airplane mode\", \"ON\")"));
        assertTrue(s.contains("logLabelWarnValue(\"Airplane mode\", \"OFF\")"));
        assertTrue(s.contains("logLabelOkValue(\"Start Temp\", String.valueOf(startTemp[0]))"));
        assertTrue(s.contains("logLabelOkValue(\"End Temp\", String.valueOf(endTemp[0]))"));
        assertTrue(s.contains("logLabelOkValue(\"Start Volt\", String.valueOf(startVolt[0]))"));
        assertTrue(s.contains("logLabelOkValue(\"End Volt\", String.valueOf(endVolt[0]))"));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Διορθωμένη κατανάλωση\""));
        assertTrue(s.contains("logLabelOkValue(\n            gr ? \"Κατανάλωση\""));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Άνοδος θερμοκρασίας\""));
        assertTrue(s.contains("logLabelWarnValue(gr ? \"Άνοδος θερμοκρασίας\""));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Μεταβολή τάσης (+πτώση/-άνοδος)\""));
        assertTrue(s.contains("logLabelValue(gr ? \"Διορθωμένη κατανάλωση\""));
        assertTrue(s.contains("gr ? \"Μ/Δ\" : \"N/A\""));
    }
    @Test public void enduranceAndRemainingTimeAreColoredOnlyWhenCalculated() throws Exception {
        String s = section(source(),
                "private boolean checkLab14BConditions() {",
                "// LAB 14B — PRE TEST ADVISORY");
        assertTrue(s.contains("logLabelOkValue(gr ? \"Εκτιμώμενη διάρκεια στο 100% (μοντέλο)\""));
        assertTrue(s.contains("logLabelOkValue(labelLight,"));
        assertTrue(s.contains("logLabelOkValue(labelNormal,"));
        assertTrue(s.contains("logLabelOkValue(labelHeavy,"));
        assertTrue(s.contains("logLabelValue(labelLight, na)"));
        assertTrue(s.contains("logLabelValue(labelNormal, na)"));
        assertTrue(s.contains("logLabelValue(labelHeavy, na)"));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Μπαταρία\""));
    }
    @Test public void poorOmegaPerformanceIsAmberAndNotBatteryHealthVerdict() throws Exception {
        String s = section(source(),
                "private boolean checkLab14BConditions() {",
                "// LAB 14B — PRE TEST ADVISORY");
        assertTrue(s.contains("if (omega < 0.85f)"));
        assertTrue(s.contains("logLabelWarnValue(gr ? \"Δείκτης απόδοσης μπαταρίας Ω\""));
        assertTrue(s.contains("logLabelWarnValue(gr ? \"Σχετική απόδοση\""));
        assertTrue(s.contains("logLabelWarnValue(gr ? \"Συμπέρασμα\""));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Εκτιμώμενος ρυθμός κατανάλωσης\""));
        assertTrue(s.contains("String referenceSource")); // capacity origin explanatory only
        assertTrue(s.contains("Πειραματικός δείκτης κατανάλωσης Ω — όχι αξιολόγηση υγείας μπαταρίας"));
        assertTrue(s.contains("float referencePerHour = 500f;"));
        assertTrue(s.contains("float omega ="));
    }
}
