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
        assertTrue(s.contains("logLabelOkValue(gr ? \"Φόρτιση (έναρξη / λήξη)\""));
        assertTrue(s.contains("logLabelOkValue(\n            gr ? \"Κατανάλωση\""));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Μεταβολή θερμοκρασίας\""));
        assertTrue(s.contains("logLabelWarnValue(gr ? \"Μεταβολή θερμοκρασίας\""));
        assertTrue(s.contains("logLabelOkValue(\n            gr ? \"Μεταβολή τάσης (+πτώση/-άνοδος)\""));
        assertFalse(s.contains("logLabelOkValue(gr ? \"Διορθωμένη κατανάλωση\""));
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
    @Test public void lab14BShowsMeasurementsWithoutSyntheticBatteryPerformanceVerdict() throws Exception {
        String s = section(source(),
                "private boolean checkLab14BConditions() {",
                "// LAB 14B — PRE TEST ADVISORY");
        // A five-minute drain rate is measured; the old Ω comparison used a
        // generic 500 mAh/h reference and is not a device-health criterion.
        assertFalse(s.contains("Πειραματικός δείκτης κατανάλωσης Ω"));
        assertFalse(s.contains("Δείκτης απόδοσης μπαταρίας Ω"));
        assertFalse(s.contains("Ερμηνεία μοντέλου Ω"));
        assertFalse(s.contains("Πειραματική κλίμακα Ω"));
        assertFalse(s.contains("float referencePerHour = 500f;"));
        assertFalse(s.contains("Σχετική απόδοση"));
        assertFalse(s.contains("Συνιστάται εκτέλεση Lab 14A Pro"));
        assertFalse(s.contains("Συνιστάται πλήρης διάγνωση μέσω Lab 14A Pro"));
        assertTrue(s.contains("logLabelOkValue(\n            gr ? \"Κατανάλωση\""));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Εκτιμώμενη διάρκεια στο 100% (μοντέλο)\""));
        assertTrue(s.contains("logLabelOkValue(labelLight,"));
        assertTrue(s.contains("logLabelOkValue(labelNormal,"));
        assertTrue(s.contains("logLabelOkValue(labelHeavy,"));
        assertTrue(s.contains("promptRestoreNormalMode();"));
        assertTrue(s.contains("lab14b_consumption_per_hour"));
        assertTrue(s.contains("lab14b_estimated_hours"));
        assertTrue(s.contains("lab14b_ts"));
    }

    @Test public void gammaColorContractCoversAllAvailableValues() throws Exception {
        String full = source();
        String conditions = section(full,
                "private boolean checkLab14BConditions() {",
                "// LAB 14B — battery duration estimation");
        assertTrue(conditions.contains("isLab14GamaMode ? \"LAB 14 GAMMA\" : \"LAB 14B\""));
        assertTrue(conditions.contains("logInfo(labName + (gr"));
        assertTrue(conditions.contains("logLabelOkValue(\"Airplane mode\", \"ON\")"));
        String advisory = section(full,
                "private void showLab14BAdvisory(Runnable onContinue)",
                "// 🔥 LAB 14B — SIMPLIFIED");
        assertTrue(advisory.contains("isLab14GamaMode ? \"Η δοκιμή προσομοιώνει gaming χρήση"));
        assertTrue(advisory.contains("isLab14GamaMode ? \"This test simulates 5 minutes of gaming usage"));

        String duration = section(full,
                "private void runLab14DurationMode(boolean gamaMode)",
                "if (!isLab14GamaMode) {");
        for (String measured : new String[] {
                "Επίπεδο μπαταρίας", "Χωρητικότητα αναφοράς (14B/Γ)",
                "Start Temp", "End Temp", "Start Volt", "End Volt",
                "Φόρτιση (έναρξη / λήξη)", "Κατανάλωση",
                "Μεταβολή θερμοκρασίας", "Μεταβολή τάσης (+πτώση/-άνοδος)",
                "Εκτιμώμενη διάρκεια στο 100% (μοντέλο)", "Μπαταρία" }) {
            assertTrue("Missing GAMMA data field: " + measured,
                    conditions.contains(measured) || duration.contains(measured));
        }
        for (String colored : new String[] {
                "logLabelOkValue(\"Start Temp\"", "logLabelOkValue(\"End Temp\"",
                "logLabelOkValue(\"Start Volt\"", "logLabelOkValue(\"End Volt\"",
                "logLabelOkValue(gr ? \"Φόρτιση (έναρξη / λήξη)\"",
                "logLabelOkValue(gr ? \"Μεταβολή θερμοκρασίας\"",
                "logLabelOkValue(gr ? \"Εκτιμώμενη διάρκεια στο 100% (μοντέλο)\"",
                "logLabelOkValue(gr ? \"Μπαταρία\"",
                "logLabelOkValue(labelLight,", "logLabelOkValue(labelNormal,",
                "logLabelOkValue(labelHeavy," }) {
            assertTrue("Missing green available value: " + colored, duration.contains(colored));
        }
        // Every white value in the shared 14B/GAMMA path must be explicitly unavailable.
        java.util.regex.Matcher neutral = java.util.regex.Pattern.compile(
                "logLabelValue\\s*\\((.*?)\\);", java.util.regex.Pattern.DOTALL)
                .matcher(duration);
        int checked = 0;
        while (neutral.find()) {
            checked++;
            String call = neutral.group(1);
            assertTrue("Unexpected uncolored value: " + call,
                    call.contains("Μ/Δ") || call.contains("N/A")
                    || call.contains(", na") || call.contains("ΔΕΝ ΕΛΕΓΧΘΗΚΕ")
                    || call.contains("NOT TESTED"));
        }
        assertTrue("No unavailable-value fallback examined", checked > 0);
        assertFalse("Misspelled GAMMA label", full.contains("LAB 14 GAMA"));
        assertTrue(duration.contains("heavyMul = 0.70f"));
        assertTrue(duration.contains("lightMul = 1.15f"));
    }

}
