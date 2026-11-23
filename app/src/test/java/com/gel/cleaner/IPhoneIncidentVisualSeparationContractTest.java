package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Regression: independent panic incidents, accurate thermal text, log scroll behavior. */
public final class IPhoneIncidentVisualSeparationContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/IPhoneLabsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/IPhoneLabsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String part(String a, String b) throws Exception {
        String s = source();
        int i = s.indexOf(a), j = s.indexOf(b, i + a.length());
        assertTrue("Bad iPhone lab boundaries: " + a + " / " + b, i >= 0 && j > i);
        return s.substring(i, j);
    }

    @Test public void lab1NeverCombinesIndependentPanicFileRuleIds() throws Exception {
        String lab = part("private void runPanicLogAnalyzer() {",
                "private void runPanicSignatureParser() {");
        assertTrue(lab.contains("for (String block : separatePanicBlocks)"));
        assertTrue(lab.contains("IPSPanicParser.analyze(this, block)"));
        assertFalse(lab.contains("IPSPanicParser.analyze(this, panicLogText)"));
        assertTrue(lab.contains("logPanicIncidentHeader(incidentNumber,"));
        assertTrue(lab.contains("Αναγνωριστικό κανόνα GEL (όχι Apple):"));
        assertTrue(lab.contains("Επιπλέον κανόνας GEL στο ίδιο συμβάν:"));
        assertFalse(lab.contains("Πρόσθετος κανόνας GEL:"));
    }

    @Test public void exactThermalShutdownIsNotRewrittenAsThrottle() throws Exception {
        String helper = part("private String panicEventExact(",
                "// ============================================================\n// LAB 1");
        assertTrue(helper.contains("thermal shutdown"));
        assertTrue(helper.contains("Θερμική διακοπή λειτουργίας (Thermal Shutdown)"));
        assertTrue(helper.contains("Το panic log καταγράφει θερμική διακοπή λειτουργίας (Thermal Shutdown)."));
        assertTrue(helper.contains("IPhonePanicHumanizer.event(type, gr)")); // other event types preserved
        String lab2 = part("private void runPanicSignatureParser() {",
                "private void runImpactLab() {");
        assertTrue(lab2.contains("panicEventExact(block, sigCrashType, gr)"));
        assertTrue(lab2.contains("panicEventMeaningExact(block, sigCrashType, gr)"));
    }

    @Test public void incidentHeadingsAppearInAllRelevantLabs() throws Exception {
        String s = source();
        assertTrue(s.contains("ΣΥΜΒΑΝ "));
        assertTrue(s.contains("color='#FFD700'"));
        assertTrue(s.contains("GELServiceLog.logInfo(heading)")); // PDF/text provenance
        assertTrue(part("private void runPanicLogAnalyzer() {", "private void runPanicSignatureParser() {")
                .contains("logPanicIncidentHeader("));
        assertTrue(part("private void runPanicSignatureParser() {", "private void runImpactLab() {")
                .contains("logPanicIncidentHeader("));
        assertTrue(part("private void runImpactLab() {", "private int countSupportedPanicBlocks(")
                .contains("logPanicIncidentHeader("));
        assertTrue(part("private void runFinalServiceRecommendationLab() {", "private void runDemoDiagnostics() {")
                .contains("logPanicIncidentHeader("));
    }

    @Test public void lab6AdviceIsYellowNotUnverifiedGreenOrFailureRed() throws Exception {
        String lab = part("private void runFinalServiceRecommendationLab() {",
                "private void runDemoDiagnostics() {");
        assertTrue(lab.contains("logLabelWarnValue(gr ? \"Προτεινόμενος έλεγχος:\""));
        assertTrue(lab.contains("logLabelWarnValue(gr ? \"Πότε χρειάζεται τεχνικός:\""));
        assertTrue(lab.contains("logLabelWarnValue(gr ? \"Για τα Watchdog:\""));
        assertTrue(lab.contains("logLabelWarnValue(gr ? \"Για το Thermal:\""));
        assertTrue(lab.contains("logLabelValue(gr ? \"Χρειάζεται επισκευή;\""));
        assertFalse(lab.contains("logLabelOkValue(gr ? \"Χρειάζεται επισκευή;\""));
    }

    @Test public void linkEnabledLogTextCannotReceiveRecursiveTouchShrink() throws Exception {
        String s = source();
        assertTrue(s.contains("UIHelpers.applyPressEffectRecursive(root);"));
        assertTrue(s.contains("txtLog.setOnTouchListener(null);"));
        assertTrue(s.contains("txtLog.setScaleX(1f);"));
        assertTrue(s.contains("txtLog.setScaleY(1f);"));
        assertTrue(s.contains("txtLog.setMovementMethod(android.text.method.LinkMovementMethod.getInstance())"));
        assertTrue(s.indexOf("txtLog.setOnTouchListener(null);") > s.indexOf("UIHelpers.applyPressEffectRecursive(root);"));
    }
}
