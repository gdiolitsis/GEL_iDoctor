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

    @Test public void lab17ReadsSameRunAndHonorsCoolingFirst() throws Exception {
        String s = section(source(), "private void lab17RunAuto()", "// START LAB 17");
        assertTrue(s.contains("p.getString(\"lab15_charger_type\", \"UNKNOWN\")"));
        assertTrue(s.contains("Lab 15 — ταχυφορτιστής (δήλωση)"));
        assertTrue(s.contains("NO (user reported)"));
        int hot = s.indexOf("if (lab15ObservedOvertemp) {\n                chargeAdvice");
        int low = s.indexOf("} else if (chargeRateWarn) {\n                if (\"YES\"");
        assertTrue("Cooling/heat must take precedence over fast-charger suggestion", hot > 0 && low > hot);
        assertTrue(s.contains("δεν αποδεικνύεται η αιτία"));
        assertTrue(s.contains("δεν αποδεικνύει βλάβη"));
    }
}
