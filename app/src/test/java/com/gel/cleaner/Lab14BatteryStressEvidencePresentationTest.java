package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/**
 * Lab 14A presentation contracts. Keep recorded/calculated evidence intact,
 * avoid turning a sag threshold into a diagnosis of defective battery health.
 */
public final class Lab14BatteryStressEvidencePresentationTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String section(String s, String begin, String end) {
        int a = s.indexOf(begin);
        int b = s.indexOf(end, a + begin.length());
        assertTrue("Missing section: " + begin, a >= 0 && b > a);
        return s.substring(a, b);
    }

    @Test public void capacityIsStillCalculatedFromAvailableChargeAndSoc() throws Exception {
        String s = source();
        assertTrue(s.contains("Υπολογισμένη χωρητικότητα αναφοράς"));
        assertTrue(s.contains("charge_now / στάθμη φόρτισης (πιθανή απόκλιση)"));
        assertTrue(s.contains("baselineFullMah"));
    }

    @Test public void twoAmpExperimentalResistanceProxyIsRetainedAndNeverCalledMeasuredEsr() throws Exception {
        String s = source();
        String lab = section(s, "private void lab14LogStressResult(", "private void lab14LogFinalScore(");
        assertTrue(lab.contains("Πειραματικός δείκτης ΔV/2A"));
        assertTrue(lab.contains("float rEst = finalSag * 1000f / 2.0f;"));
        assertTrue(lab.contains("υπόθεση 2 A, όχι ESR"));
    }

    @Test public void calculatedPowerIsDisplayedInMilliwattsWithoutBatteryHealthGrade() throws Exception {
        String s = source();
        String block = section(s, "// POWER CALCULATION — IN-TEST RESULT, NOT BATTERY-HEALTH GRADE",
                "// 🔴 RESISTANCE (FINAL — VALIDATED + INTERPRETED)");
        assertTrue(block.contains("Υπολογισμένη ικανότητα ισχύος κατά τη δοκιμή"));
        assertTrue(block.contains("Calculated power capability during test"));
        assertTrue(block.contains("String.format(Locale.US, \"%.0f mW\", res.powerMw)"));
        assertFalse(block.contains("powerLabel"));
        assertFalse(block.contains("(Normal)"));
    }

    @Test public void criticalIsInternalThresholdNotUserFacingBatteryHealthVerdict() throws Exception {
        String s = source();
        assertTrue(s.contains("labelSafe = \"Critical\";"));
        assertTrue(s.contains("lab14DisplayStressClass(labelSafe, gr)"));
        assertTrue(s.contains("lab14DisplayStressClass(sagLabel, gr)"));
        assertTrue(s.contains("lab14DisplayStressClass(lbl, gr)"));
        assertTrue(s.contains("Αυξημένη πτώση τάσης — επανέλεγχος"));
        assertTrue(s.contains("Αυξημένη πτώση τάσης υπό φορτίο — απαιτείται επανέλεγχος"));
        assertFalse(s.contains("Η συμπεριφορά φαίνεται φυσιολογική"));
    }

    @Test public void useGuidanceIsConditionalOnEvidenceAndWarningSymptoms() throws Exception {
        String s = source();
        String block = section(s, "private void lab14LogFinalScore(",
                "// LAB 14 — LOG SAVE");
        assertTrue(block.contains("Lab14HumanVerdict.evaluate("));
        assertTrue(block.contains("fullValidRun, limiter, collapse, smartSwelling,"));
        assertTrue(block.contains("calibration, electricalBehaviourFlag, thermalRiseC, batteryPeakC"));
        assertTrue(block.contains("if (humanVerdict.tone == Lab14HumanVerdict.Tone.POSITIVE)"));
        assertTrue(block.contains("logLabelOkValue(humanLabel, humanVerdict.message);"));
        assertTrue(block.contains("logLabelWarnValue(humanLabel, humanVerdict.message);"));
        assertTrue(block.contains("logLabelValue(humanLabel, humanVerdict.message);"));
    }

    @Test public void noAdditionalAnomalyDoesNotClaimCompletelyHealthyBattery() throws Exception {
        String s = source();
        assertTrue(s.contains("Δεν καταγράφηκε πρόσθετη ηλεκτρική ανωμαλία στις ελεγχόμενες ενδείξεις"));
        assertTrue(s.contains("No additional electrical anomaly was recorded"));
        assertFalse(s.contains("Electrical behaviour appears normal"));
    }
}
