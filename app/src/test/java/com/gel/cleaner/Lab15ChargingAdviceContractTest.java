package com.gel.cleaner;

import org.junit.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

/** Guard Lab 15 evidence-limited, accessory-first verdicts and unchanged sampling. */
public final class Lab15ChargingAdviceContractTest {
    private static String lab15() throws Exception {
        Path path = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(path))
            path = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String source = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        int from = source.indexOf("private void lab15ChargingSystemSmart()");
        int to = source.indexOf("private void lab16ThermalSnapshot()", from);
        assertTrue("Lab 15 boundaries missing", from >= 0 && to > from);
        return source.substring(from, to);
    }

    @Test public void moderateOrLowRatePromptsCableAndChargerBeforeService() throws Exception {
        String s = lab15();
        int verdict = s.indexOf("// FINAL LAB 15 DECISION");
        String decision = s.substring(verdict, s.indexOf("// SUMMARY FLAG", verdict));
        assertTrue(decision.contains("else if (lab15_strengthWeak)"));
        assertTrue(decision.contains("Μέτριος ή χαμηλός καθαρός ρυθμός"));
        assertTrue(decision.contains("δεν αποδεικνύει βλάβη της συσκευής"));
        assertTrue(decision.contains("Αν η συσκευή υποστηρίζει ταχεία φόρτιση"));
        assertTrue(decision.contains("συμβατό ταχυφορτιστή και κατάλληλο καλώδιο"));
        assertTrue(decision.contains("Επανάλαβε χωρίς έντονη χρήση"));
        String weakRateCase = decision.substring(decision.indexOf("else if (lab15_strengthWeak)"));
        assertTrue(weakRateCase.indexOf("συμβατό ταχυφορτιστή") >= 0);
        assertTrue(weakRateCase.indexOf("συμβατό ταχυφορτιστή") <
                   weakRateCase.indexOf("ζήτησε τεχνικό έλεγχο"));
        assertTrue(weakRateCase.contains("ιδανικά σε παρόμοια στάθμη φόρτισης και θερμοκρασία"));
        assertTrue(decision.contains("Ο καθαρός ρυθμός δεν είναι η ισχύς του φορτιστή"));
        assertFalse(decision.contains("Συνιστάται περαιτέρω έλεγχος ή επανάληψη δοκιμής"));
    }

    @Test public void overheatingAndDisconnectionsHaveDistinctGuidance() throws Exception {
        String s = lab15();
        int verdict = s.indexOf("// FINAL LAB 15 DECISION");
        String decision = s.substring(verdict, s.indexOf("// SUMMARY FLAG", verdict));
        assertTrue(decision.contains("else if (lab15OverTempDuringCharge)"));
        assertTrue(decision.contains("Διακόψτε τη φόρτιση και αφήστε τη συσκευή να κρυώσει"));
        assertTrue(decision.contains("else if (lab15FlapUnstable)"));
        assertTrue(decision.contains("ελέγξτε οπτικά τη θύρα"));
        assertTrue(decision.contains("δεν εντοπίστηκε η αιτία"));
        assertTrue(decision.contains("if (!lab15_strengthKnown)"));
        assertTrue(decision.contains("else {"));
        assertTrue(decision.contains("Δεν παρατηρήθηκε πρόβλημα στο τρίλεπτο τεστ"));
    }

    @Test public void measuredRateThresholdsAndSourceArePreserved() throws Exception {
        String s = lab15();
        assertTrue(s.contains("long deltaMah = endInfo.chargeNowMah - startMah[0];"));
        assertTrue(s.contains("double minutes = dtMs / 60000.0;"));
        assertTrue(s.contains("double mahPerMin = (minutes > 0) ? (deltaMah / minutes) : -1;"));
        assertTrue(s.contains("if (mahPerMin >= 20.0)"));
        assertTrue(s.contains("} else if (mahPerMin >= 10.0)"));
        assertTrue(s.contains("} else if (mahPerMin >= 5.0)"));
        assertTrue(s.contains("logLabelOkValue("));
        assertTrue(s.contains("logLabelWarnValue("));
        assertTrue(s.contains("String.format("));
    }
}
