package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Presentation-only regression for v57 iPhone demo and real imported panic logs. */
public final class IPhoneEvidenceClarityContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/IPhoneLabsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/IPhoneLabsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
    private static String between(String src, String a, String b) {
        int start = src.indexOf(a), end = src.indexOf(b, start + a.length());
        assertTrue("Missing method boundary: " + a, start >= 0 && end > start);
        return src.substring(start, end);
    }
    @Test public void welcomeMessageIsNeutralAndOffersRealLogsOrDemo() throws Exception {
        String src = source();
        assertTrue(src.contains("Για πραγματική ανάλυση εισήγαγε panic log· για δοκιμή επίλεξε DEMO."));
        assertTrue(src.contains("Import a panic log for real analysis, or select DEMO"));
        assertFalse(src.contains("Εισήγαγε panic log για να ξεκινήσει η ανάλυση."));
        assertTrue(src.contains("ZIP FILE: PanicLog1"));
        assertTrue(src.contains("ZIP FILE: PanicLog2"));
        assertTrue(src.contains("DEMO finished: no simulated results were stored"));
    }
    @Test public void lab1SeparatesSuccessfulPatternMatchFromIncidentAndLabelsRuleId() throws Exception {
        String lab = between(source(), "private void runPanicLogAnalyzer()", "private void runPanicSignatureParser()");
        assertTrue(lab.contains("GEL pattern matching completed."));
        assertTrue(lab.contains("logLabelOkValue("));
        assertFalse(lab.contains("Βεβαιότητα αιτίας:"));
        assertTrue(lab.contains("Αναγνωριστικό κανόνα GEL (όχι Apple):"));
        assertTrue(lab.contains("Επιπλέον κανόνας GEL στο ίδιο συμβάν:"));
        assertFalse(lab.contains("\"Pattern ID:\""));
        assertFalse(lab.contains("Βάρος κανόνα"));
        assertFalse(lab.contains("Rule weight (not device severity)"));
        assertFalse(lab.contains("Το log είναι έγκυρο"));
        assertTrue(lab.contains("IPSPanicParser.analyze(this, block)"));
        assertFalse(lab.contains("IPSPanicParser.analyze(this, panicLogText)"));
        assertTrue(lab.contains("logPanicIncidentHeader(incidentNumber,"));
    }
    @Test public void lab2IsEventOverviewAndLab3HasIndependentExpertEvidence() throws Exception {
        String src = source();
        String lab2 = between(src, "private void runPanicSignatureParser()", "private void runImpactLab()");
        String lab4 = between(src, "private void runImpactLab()", "private int countSupportedPanicBlocks(");
        assertTrue(lab2.contains("parseAndCacheSignature(block)"));
        assertTrue(lab2.contains("Ερμηνεία υπογραφής:"));
        assertTrue(lab2.contains("Υπογραφή αρχείου:"));
        assertTrue(lab2.contains("panicEventMeaningExact(block, sigCrashType, gr)"));
        assertFalse(lab2.contains("Βεβαιότητα αιτίας:"));
        assertFalse(lab2.contains("logApplePanicEvidence("));
        assertTrue(lab4.contains("parseAndCacheSignature(block)"));
        assertTrue(lab4.contains("logPanicIncidentHeader(incident, \"\", gr)"));
        assertTrue(lab4.contains("logApplePanicEvidence(block, gr,"));
        assertFalse(lab4.contains("logLabelOkValue(gr ? \"Εξάρτημα:\""));
        assertTrue(lab4.contains("Το Lab 3 ολοκληρώθηκε."));
    }
    @Test public void gelDerivedCodesAreWhiteNotReportedAsExactAppleCodes() throws Exception {
        String src = source();
        String helper = between(src, "private void logApplePanicEvidence(", "private void parseAndCacheSignature(");
        assertTrue(helper.contains("ApplePanicHardwareHints.examineOne(block)"));
        assertTrue(helper.contains("logLabelValue(heading + (gr ? \" — ένδειξη GEL:\""));
        assertFalse(helper.contains("logLabelOkValue(heading + \":\", finding.code)"));
        assertTrue(helper.contains("alreadyShown != null && !alreadyShown.add(key)"));
        String lab8 = between(src, "private void runFinalServiceRecommendationLab()", "private void runDemoDiagnostics()");
        assertFalse(lab8.contains("ApplePanicHardwareHints.examineOne(block)"));
        assertFalse(lab8.contains("Ενδείξεις που αναγνώρισε το GEL:"));
        assertFalse(lab8.contains("uniqueTechnicalCodes.add(finding.code)"));
        assertFalse(lab8.contains("Ακριβείς τεχνικοί κωδικοί:"));
        assertFalse(lab8.contains("logLabelOkValue(gr ? \"Ενδείξεις που αναγνώρισε το GEL:\""));
        assertTrue(lab8.contains("if (human.repeatedClueNeedsReview)"));
        assertTrue(lab8.contains("Το Lab 6 ολοκληρώθηκε."));
    }
}
