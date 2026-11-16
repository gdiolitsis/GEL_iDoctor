package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Stage-1 renumbering contract: PRO Lab 27 cannot complete without Lab 25. */
public final class Lab28RequiresLab25ContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String segment(String s, String begin, String end) {
        int a = s.indexOf(begin);
        int b = s.indexOf(end, a + begin.length());
        assertTrue("Missing section: " + begin, a >= 0 && b > a);
        return s.substring(a, b);
    }

    @Test public void proEntitlementRemainsFirstAndLab25IsRequiredBeforeConnectivity() throws Exception {
        String s = source();
        String gate = segment(s, "private void lab29DeviceAuthenticity() {",
                "private void runLab29DeviceAuthenticity() {");
        int entitlement = gate.indexOf("requireGelPro(");
        int prerequisite = gate.indexOf("if (!lab28RequireExecutedLab25()) return;");
        int connectivity = gate.indexOf("Lab29ConnectivityGate.runOrPrompt(");
        assertTrue(entitlement >= 0);
        assertTrue(prerequisite > entitlement);
        assertTrue(connectivity > prerequisite);
    }

    @Test public void deferredRunnerRechecksPrerequisiteAndShowsBuyerEvidence() throws Exception {
        String s = source();
        String run = segment(s, "private void runLab29DeviceAuthenticity() {",
                "private void showLab29ExternalGuidanceDialog(boolean gr)");
        int recheck = run.indexOf("if (!lab28RequireExecutedLab25()) return;");
        int report = run.indexOf("lab28ShowLab25BuyerAssessment(gr);");
        int completion = run.indexOf("Το Lab 27 ολοκληρώθηκε");
        assertTrue(recheck >= 0 && report > recheck);
        assertTrue(completion > report);
        assertTrue(run.contains("showLab29ExternalGuidanceDialog(gr);"));
    }

    @Test public void lab25RunPersistsOnlyVerifiedSummaryAndRestrictedCoverage() throws Exception {
        String s = source();
        String lab = segment(s, "private void lab25CrashHistory() {",
                "} // END lab25CrashHistory()");
        assertTrue(lab.contains("lab25SaveBuyerEvidence(history,"));
        assertTrue(lab.contains("Lab25BuyerEvidence.LIMITED"));
        assertTrue(lab.contains("Lab25BuyerEvidence.ACCESSIBLE"));
        String persistence = segment(s, "private void lab25SaveBuyerEvidence(",
                "private void lab28ShowLab25BuyerAssessment(");
        assertTrue(persistence.contains("putInt(\"lab25_obs_schema\""));
        assertTrue(persistence.contains("putLong(\"lab25_obs_wall_ms\""));
        assertTrue(persistence.contains("putLong(\"lab25_obs_elapsed_ms\""));
        assertTrue(persistence.contains("putString(\"lab25_obs_coverage\""));
        assertTrue(persistence.contains("putString(\"lab25_obs_level\""));
        assertTrue(persistence.contains("putInt(\"lab25_obs_independent\""));
        assertTrue(persistence.contains("if (!lab28HasFreshLab25Evidence()) return;"));
        assertFalse(persistence.contains("putString(\"lab25_raw_stack_trace\""));
    }

    @Test public void blockedRunExplainsNextActionWithoutOpeningImeICheck() throws Exception {
        String s = source();
        String gate = segment(s, "private boolean lab28RequireExecutedLab25() {",
                "private void showLab28RequiresLab25Dialog(boolean gr)");
        assertTrue(gate.contains("if (lab28HasFreshLab25Evidence()) return true;"));
        assertTrue(gate.contains(".remove(\"lab29_obs_schema\")"));
        assertTrue(gate.contains("Εκτέλεσε το Lab 25"));
        assertTrue(gate.contains("showLab28RequiresLab25Dialog(gr);"));
        assertTrue(gate.contains("return false;"));
        String dialog = segment(s, "private void showLab28RequiresLab25Dialog(boolean gr)",
                "// Store only categorical evidence");
        assertTrue(dialog.contains("ΕΚΤΕΛΕΣΗ LAB 25"));
        assertTrue(dialog.contains("lab25CrashHistory();"));
        assertTrue(dialog.contains("lab29DialogButton(gr ? \"ΕΞΟΔΟΣ\" : \"EXIT\", true)"));
        assertTrue(dialog.contains("bg.setColor(0xFF101010);"));
        assertTrue(dialog.contains("message.setTextColor(0xFF39FF14);"));
    }

    @Test public void buyerIsWarnedAndNoHistoryNeverMeansNoCrashes() throws Exception {
        String s = source();
        String report = segment(s, "private void lab28ShowLab25BuyerAssessment(boolean gr)",
                "private void lab29DeviceAuthenticity() {");
        assertTrue(report.contains("ΑΝΕΠΑΡΚΗ ΣΤΟΙΧΕΙΑ ΙΣΤΟΡΙΚΟΥ"));
        assertTrue(report.contains("ΙΣΧΥΡΕΣ ΕΝΔΕΙΞΕΙΣ ΕΠΑΝΑΛΑΜΒΑΝΟΜΕΝΗΣ ΑΣΤΑΘΕΙΑΣ"));
        assertTrue(report.contains("Συνιστάται πρόσθετος έλεγχος πριν την αγορά."));
        assertTrue(report.contains("Αναφορά χρήστη (μη επαληθευμένη γνησιότητα)"));
        assertTrue(report.contains("Αυτό ΔΕΝ αποδεικνύει"));
        assertFalse(report.contains("Δεν υπάρχουν κρασαρίσματα"));
    }
}
