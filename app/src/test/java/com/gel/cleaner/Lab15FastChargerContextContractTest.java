package com.gel.cleaner;

import org.junit.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

/**
 * QA contract for user-reported charger context. User answer is not a measured
 * charging protocol and must never override observed heat/disconnect evidence.
 */
public final class Lab15FastChargerContextContractTest {
    private static String source() throws Exception {
        Path path = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(path))
            path = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static String section(String source, String begin, String end) {
        int first = source.indexOf(begin);
        assertTrue("Missing section: " + begin, first >= 0);
        int last = source.indexOf(end, first + begin.length());
        assertTrue("Missing section end: " + end, last > first);
        return source.substring(first, last);
    }

    @Test public void pretestOffersAllThreeAnswersWithoutInventingFastChargeProof() throws Exception {
        String s = section(source(), "private void showLab15ConditionCheck(", "private void abortLab15ByUser()");
        assertTrue(s.contains("Ο φορτιστής που θα χρησιμοποιήσεις είναι ταχυφορτιστής;"));
        assertTrue(s.contains("\"ΝΑΙ\""));
        assertTrue(s.contains("\"ΟΧΙ\""));
        assertTrue(s.contains("\"ΔΕΝ ΓΝΩΡΙΖΩ\""));
        assertTrue(s.contains("chargerChoices.check(fastUnknownId)"));
        assertTrue(s.contains("lab15ChargerType = selected == fastYesId ? \"YES\""));
        assertTrue(s.contains("selected == fastNoId ? \"NO\" : \"UNKNOWN\""));
        assertTrue(s.contains("lab15ChargerType = \"UNKNOWN\""));
        assertTrue(s.contains("buildGELPopupRoot(this)"));
        assertTrue(s.contains("Επίλεξε: Ναι, Όχι ή Δεν γνωρίζω."));
    }

    @Test public void lab15OnlyPersistsAnswerForCurrentCompletedRun() throws Exception {
        String s = section(source(), "private void lab15ChargingSystemSmart()", "private void lab16ThermalSnapshot()");
        assertTrue(s.contains(".remove(\"lab15_charger_type\")"));
        assertTrue(s.contains(".putString(\"lab15_charger_type\", lab15ChargerType)"));
        assertTrue(s.contains("Ταχυφορτιστής (δήλωση χρήστη)"));
        assertTrue(s.contains("χωρίς τεχνική επαλήθευση"));
        assertTrue(s.contains("long deltaMah = endInfo.chargeNowMah - startMah[0];"));
        assertTrue(s.contains("double mahPerMin = (minutes > 0) ? (deltaMah / minutes) : -1;"));
        assertTrue(s.contains("if (mahPerMin >= 20.0)"));
        assertTrue(s.contains("} else if (mahPerMin >= 10.0)"));
        assertTrue(s.contains("} else if (mahPerMin >= 5.0)"));
    }

    @Test public void unknownChargeRateMustNotSuppressHeatOrConnectionWarnings() throws Exception {
        String s = section(source(), "// FINAL LAB 15 DECISION", "// SUMMARY FLAG");
        assertTrue(s.contains("!lab15_strengthKnown && !lab15OverTempDuringCharge && !lab15FlapUnstable"));
        assertTrue(s.indexOf("else if (lab15OverTempDuringCharge)")
                < s.indexOf("else if (lab15_strengthWeak)"));
        assertTrue(s.contains("Διακόψτε τη φόρτιση και αφήστε τη συσκευή να κρυώσει"));
        assertTrue(s.contains("η αιτία δεν αποδεικνύεται από τη θερμοκρασία"));
        assertTrue(s.contains("else if (lab15FlapUnstable)"));
    }

    @Test public void lowRateAdviceUsesReportedContextWithoutAssumingFault() throws Exception {
        String s = section(source(), "// FINAL LAB 15 DECISION", "// SUMMARY FLAG");
        assertTrue(s.contains("if (\"YES\".equals(lab15ChargerType))"));
        assertTrue(s.contains("else if (\"NO\".equals(lab15ChargerType))"));
        assertTrue(s.contains("else {"));
        assertTrue(s.contains("δεν επιβεβαιώθηκε ενεργή ταχυφόρτιση"));
        assertTrue(s.contains("ο χαμηλός ρυθμός δεν σημαίνει βλάβη"));
        assertTrue(s.contains("Αν η συσκευή υποστηρίζει ταχεία φόρτιση"));
    }

    @Test public void lab15OwnsChargerAnswerAndStoresOnlyItsInterpretationForLab17() throws Exception {
        String all = source();
        String lab15 = section(all, "private void lab15ChargingSystemSmart()", "private void lab16ThermalSnapshot()");
        String lab17 = section(all, "private void lab17RunAuto()", "// START LAB 17");

        assertTrue(lab15.contains("Lab17ChargingContext.evaluate("));
        assertTrue(lab15.contains("gr, lab15ChargerType, lab15RateBand"));
        assertTrue(lab15.contains("Lab17ChargingContext.resultCode("));
        assertTrue(lab15.contains(".putString(\"lab15_diagnostic_outcome_v1\", lab15Outcome)"));
        assertTrue(lab15.contains(".remove(\"lab15_diagnostic_outcome_v1\")"));
        assertTrue(lab15.contains(".putBoolean(\"lab15_system_limited\", lab15_systemLimited)"));

        assertTrue(lab17.contains("p.getString(\"lab15_diagnostic_outcome_v1\", null)"));
        assertTrue(lab17.contains("Lab17ChargingContext.fromLab15Result("));
        assertTrue(lab17.contains("Lab 15 — αποτέλεσμα φόρτισης"));
        assertTrue(lab17.contains("chargeEvaluation.chargingConcern"));
        assertTrue(lab17.contains("!chargeEvaluation.unknownChargerWithSlowRate"));
        assertTrue(lab17.contains("lab15ObservedOvertemp"));
        assertTrue(lab17.contains("lab15ObservedDisconnect"));
        assertTrue(lab17.contains("lab15SystemLimited"));

        // The original answer and detailed charger troubleshooting stay in 15.
        assertFalse(lab17.contains("lab15_charger_type"));
        assertFalse(lab17.contains("lab15ChargerType"));
        assertFalse(lab17.contains("lab15ChargerReported"));
        assertFalse(lab17.contains("ταχυφορτιστή"));
        assertFalse(lab17.contains("fast charger"));
        assertFalse(lab17.contains("Προτεινόμενος έλεγχος"));
        assertFalse(lab17.contains("Recommended check"));
        assertFalse(lab17.contains("chargeEvaluation.description"));
    }
}
