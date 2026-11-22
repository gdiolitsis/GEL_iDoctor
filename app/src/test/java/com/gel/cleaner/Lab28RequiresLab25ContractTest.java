package com.gel.cleaner;
import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** v57: actual Lab25 -> PRO Lab27 -> PRO Lab28, no bypass or forged prior report. */
public final class Lab28RequiresLab25ContractTest {
    static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
    static String section(String s,String start,String end) {
        int a=s.indexOf(start),b=s.indexOf(end,a+start.length());
        assertTrue(start,a>=0&&b>a);
        return s.substring(a,b);
    }
    @Test public void lab27ProThenLab25ThenSourceBoundTechnicalSnapshot() throws Exception {
        String s=source(), m=section(s,"private void lab30CombineFindings()","private void lab31FinalSummary()");
        int pro=m.indexOf("requireGelPro("), pre=m.indexOf("if (!lab29RequireLab25Evidence()) return;");
        int save=m.indexOf("lab29SaveTechnicalAssessmentFromLab25(p);");
        // Buyer-facing crash history is not duplicated in Lab 27, but its
        // source-bound prerequisite and technical checkpoint are retained.
        assertTrue(pro>=0&&pre>pro&&save>pre);
        assertFalse(m.contains("lab29DisplayLab25Findings(p, gr);"));
        assertTrue(m.contains("Το Lab 27 ολοκληρώθηκε"));
    }
    @Test public void lab28ProThenCertificateGateAlsoAfterConnectivity() throws Exception {
        String s=source();
        String entry=section(s,"private void lab29DeviceAuthenticity()","private void runLab29DeviceAuthenticity()");
        int pro=entry.indexOf("requireGelPro("),pre=entry.indexOf("if (!lab28RequireCertification()) return;");
        assertTrue(pro>=0&&pre>pro&&entry.indexOf("Lab29ConnectivityGate.runOrPrompt(")>pre);
        String buyer=section(s,"private void runLab29DeviceAuthenticity()","private void showLab29ExternalGuidanceDialog(boolean gr)");
        assertTrue(buyer.contains("if (!lab28RequireCertification()) return;"));
        assertTrue(buyer.contains("lab28ShowCertificationResult(gr);"));
        assertTrue(buyer.contains("showLab29ExternalGuidanceDialog(gr);"));
        assertTrue(buyer.contains("Το Lab 28 ολοκληρώθηκε"));
    }
    @Test public void currentCertificationRequiresSameBootAndUnchangedLab25Source() throws Exception {
        String s=source(),gate=section(s,"private boolean lab28HasFreshCertificationEvidence()","private void lab29DeviceAuthenticity()");
        assertTrue(gate.contains("Lab29TechnicalAssessment.isCurrent("));
        assertTrue(gate.contains("lab29_technical_source25_wall_ms"));
        assertTrue(gate.contains("lab25_obs_elapsed_ms"));
        assertTrue(gate.contains("if (lab28HasFreshCertificationEvidence()) return true;"));
        assertTrue(gate.contains("remove(\"lab29_obs_schema\")"));
        assertTrue(gate.contains("ΕΚΤΕΛΕΣΗ LAB 27"));
        assertTrue(gate.contains("lab30CombineFindings();"));
    }
    @Test public void missingLab25HasExplicitExitAndRunLab25Action() throws Exception {
        String s=source(),gate=section(s,"private boolean lab29RequireLab25Evidence()","private void lab30CombineFindings()");
        assertTrue(gate.contains("if (lab28HasFreshLab25Evidence()) return true;"));
        assertTrue(gate.contains("showLab29RequiresLab25Dialog(gr);"));
        assertTrue(gate.contains("ΕΚΤΕΛΕΣΗ LAB 25"));
        assertTrue(gate.contains("lab25CrashHistory();"));
        assertTrue(gate.contains("ΕΞΟΔΟΣ"));
        assertFalse(gate.contains("Δεν υπάρχουν κρασαρίσματα"));
    }
}
