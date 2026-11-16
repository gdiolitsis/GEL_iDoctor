package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Prevent the completed Lab 14A / missing Lab 17 prerequisite regression. */
public final class Lab17StressEvidencePrerequisiteContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
    private static String block(String s, String from, String to) {
        int a = s.indexOf(from), b = s.indexOf(to, a + from.length());
        assertTrue("Missing code block " + from, a >= 0 && b > a);
        return s.substring(a, b);
    }

    @Test public void completedStressIsStoredInItsActualPreferenceNamespace() throws Exception {
        String s = source();
        String save = block(s, "private void lab14LogSave(", "private void lab14CleanupUI()");
        assertTrue(save.contains("putLong(\"lab14_last_ts\", System.currentTimeMillis())"));
        assertTrue(save.contains("putLong(\"lab14_valid_stress_ts\""));
        assertTrue(save.contains("validDrain ? System.currentTimeMillis() : 0L"));
        assertTrue(save.contains("putString(\"lab14_stress_response_class\""));
        assertTrue(save.contains("putFloat(\"lab14_stress_sag_volts\""));
        assertTrue(save.contains("putFloat(\"lab14_stress_drain_mah_per_hour\""));
        String invocation = block(s, "// SAVE\n", "// RUN COUNT\n");
        assertTrue(invocation.contains("getSharedPreferences(LAB14_PREFS, MODE_PRIVATE)"));
        assertTrue(invocation.contains("lab14LogSave("));
    }

    @Test public void lab17RequiresCompletionNotMissingSohHealthPercent() throws Exception {
        String run = block(source(), "private void lab17RunAuto()", "private void lab17_showPopup(");
        assertTrue(run.contains("final SharedPreferences lab14Records = getSharedPreferences(LAB14_PREFS, MODE_PRIVATE)"));
        assertTrue(run.contains("lab14Records.getLong(\"lab14_last_ts\", 0L)"));
        assertTrue(run.contains("lab14Records.getLong(\"lab14_valid_stress_ts\", 0L)"));
        assertTrue(run.contains("lab14Records.contains(\"lab14_valid_stress_ts\")"));
        assertTrue(run.contains("final boolean has14 =\n        ts14 > 0L;"));
        assertTrue(run.contains("has14 && ts14 <= now && (now - ts14) <= WINDOW_MS"));
        assertFalse(run.contains("(lab14Health >= 0f && ts14 > 0L)"));
    }

    @Test public void lab17UsesEvidencedResultWithoutFakingCompositeSoh() throws Exception {
        String run = block(source(), "private void lab17RunAuto()", "private void lab17_showPopup(");
        int entry = run.indexOf("if (fresh14) {");
        int earlyReturn = run.indexOf("        return;\n    }", entry);
        int oldModel = run.indexOf("int baseScore = Math.round(");
        assertTrue("Evidence-only branch must precede legacy score", entry >= 0 && earlyReturn > entry && oldModel > earlyReturn);
        String evidence = run.substring(entry, earlyReturn);
        assertTrue(evidence.contains("Lab 14A — δοκιμή καταπόνησης"));
        assertTrue(evidence.contains("Lab 15 — φόρτιση"));
        assertTrue(evidence.contains("Lab 16 — θερμική κατάσταση"));
        assertTrue(evidence.contains("Δεν υπολογίζει ποσοστό υγείας μπαταρίας (SoH)"));
        assertTrue(evidence.contains("lab17_evidence_only_ts"));
        assertFalse(evidence.contains("lab14Health * 0.50f"));
        assertTrue(run.contains("lab14Records.getBoolean(\"lab14_unstable_measurement\", false)"));
        assertTrue(run.contains("lab14Records.getBoolean(\"lab14_collapse_risk\", false)"));
    }
    @Test public void lab17CombinesDirectObservationsWithNoWeightedScores() throws Exception {
        String s = source();
        String lab15 = block(s, "private void lab15ChargingSystemSmart()", "private void lab16ThermalSnapshot()");
        assertTrue(lab15.contains("putString(\"lab15_rate_band\""));
        assertTrue(lab15.contains("putFloat(\"lab15_net_rate_mah_per_min\""));
        assertTrue(lab15.contains("putBoolean(\"lab15_flap_observed\""));
        assertTrue(lab15.contains("putBoolean(\"lab15_overtemp_observed\""));
        assertTrue(lab15.contains("putLong(\"lab15_ts\", System.currentTimeMillis())"));
        String lab16 = block(s, "private void lab16ThermalSnapshot()", "private void lab17RunAuto()");
        assertTrue(lab16.contains("putFloat(\"lab16_peak_temp\", peakTemp)"));
        assertTrue(lab16.contains("putBoolean(\"lab16_thermal_warning\""));
        assertTrue(lab16.contains("putBoolean(\"lab16_evidence_available\""));

        String run = block(s, "private void lab17RunAuto()", "private void lab17_showPopup(");
        assertTrue(run.contains("ts15 > 0L; // completed charging observation"));
        assertTrue(run.contains("ts16 > 0L; // completed thermal snapshot"));
        assertFalse(run.contains("(lab15Charge >= 0 && ts15 > 0L)"));
        assertFalse(run.contains("(lab16Thermal >= 0 && ts16 > 0L)"));
        int entry = run.indexOf("if (fresh14) {");
        int end = run.indexOf("        return;\n    }", entry);
        assertTrue(entry >= 0 && end > entry);
        String evidence = run.substring(entry, end);
        assertTrue(evidence.contains("lab14SagVolts"));
        assertTrue(evidence.contains("lab14DrainMahPerHour"));
        assertTrue(evidence.contains("lab15NetRate"));
        assertTrue(evidence.contains("lab15ObservedOvertemp"));
        assertTrue(evidence.contains("lab15ObservedDisconnect"));
        assertTrue(evidence.contains("lab16PeakTemp"));
        assertTrue(evidence.contains("lab16OsThermalStatus"));
        assertTrue(evidence.contains("chargeConcern && thermalConcern"));
        assertTrue(evidence.contains("!completeCoverage"));
        assertTrue(evidence.contains("Συνδυαστικό συμπέρασμα"));
        assertTrue(evidence.contains("άλλο συμβατό καλώδιο/φορτιστή"));
        assertFalse(evidence.contains("lab14Health * 0.50f"));
        assertFalse(evidence.contains("lab15Charge * 0.25f"));
        assertFalse(evidence.contains("lab16Thermal * 0.25f"));
        assertFalse(evidence.contains("baseScore"));
        assertFalse(evidence.contains("penaltyExtra"));
    }
}
