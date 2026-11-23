package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** The concise iPhone report still preserves event provenance and safe interpretations. */
public final class IPhoneDemoEvidencePresentationContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/IPhoneLabsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/IPhoneLabsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String section(String start, String end) throws Exception {
        String source = source();
        int a = source.indexOf(start);
        int b = source.indexOf(end, a + start.length());
        assertTrue("Missing or reordered iPhone section " + start, a >= 0 && b > a);
        return source.substring(a, b);
    }

    @Test public void lab1PreservesPerIncidentDatesWithoutADeviceHealthScore() throws Exception {
        String lab = section("private void runPanicLogAnalyzer() {", "private void runPanicSignatureParser() {");
        assertTrue(lab.contains("countSupportedPanicBlocks(separatePanicBlocks)"));
        assertTrue(lab.contains("for (String block : separatePanicBlocks)"));
        assertTrue(lab.contains("ApplePanicEvidencePolicy.describeOne(block)"));
        assertTrue(lab.contains("meta.timestamp.isEmpty()"));
        assertTrue(lab.contains("No reliable timestamp supplied in this record"));
        assertFalse(lab.contains("parseAndCacheSignature(panicLogText)"));
        assertFalse(lab.contains("logLabelOkValue(gr ? \"Υγεία συσκευής:\""));
        assertTrue(lab.contains("Το Lab 1 ολοκληρώθηκε."));
    }

    @Test public void lab3RendersActualPerIncidentCodeAndOfficialTopics() throws Exception {
        String lab = section("private void runImpactLab() {", "private int countSupportedPanicBlocks(");
        assertTrue(lab.contains("for (String block : blocks)"));
        assertTrue(lab.contains("parseAndCacheSignature(block)"));
        assertFalse(lab.contains("parseAndCacheSignature(panicLogText)"));
        assertTrue(lab.contains("panicEventExact(block, sigCrashType, gr)"));
        assertTrue(lab.contains("AppleOfficialServiceGuidance.conditionalTopicsForClue(finding.code)"));
        assertTrue(lab.contains("logOfficialApplePanicGuidance(model, gr, appleTopicsByModel.get(model))"));
        assertTrue(lab.contains("logApplePanicEvidence(block, gr,"));
        assertFalse(lab.contains("logLabelOkValue(gr ? \"Εξάρτημα:\""));
        assertTrue(lab.contains("Το Lab 3 ολοκληρώθηκε."));
    }

    @Test public void finalServiceReportRemainsHumanFirstAndPerRecord() throws Exception {
        String lab = section("private void runFinalServiceRecommendationLab() {",
                "private void runDemoDiagnostics() {");
        assertTrue(lab.contains("for (String block : blocks)"));
        assertTrue(lab.contains("parseAndCacheSignature(block)"));
        assertTrue(lab.contains("ApplePanicHardwareHints.examineOne(block)"));
        assertFalse(lab.contains("logApplePanicEvidence(block, gr"));
        assertFalse(lab.contains("likelyHardware"));
        assertFalse(lab.contains("likelySoftware"));
        assertTrue(lab.contains("IPhonePanicServiceSummary.interpret("));
        assertTrue(lab.contains("human.repairAdvice"));
        assertTrue(lab.contains("human.whenToSeekHelp"));
        assertTrue(lab.contains("Πότε χρειάζεται τεχνικός:"));
        assertTrue(lab.contains("Για τα Watchdog:"));
        assertTrue(lab.contains("Για το Thermal:"));
        assertTrue(lab.contains("Το Lab 6 ολοκληρώθηκε."));
    }

    @Test public void incidentsAndGelCodesAreNotPaintedAsGreenDeviceHealth() throws Exception {
        String lab2 = section("private void runPanicSignatureParser() {",
                "private void runImpactLab() {");
        String combinedLab4 = section("private void runPanicFrequencyLab() {",
                "private void runRecurringDomainLab() {");
        String finalLab6 = section("private void runFinalServiceRecommendationLab() {",
                "private void runDemoDiagnostics() {");
        assertTrue(lab2.contains("logLabelWarnValue("));
        assertTrue(combinedLab4.contains("logLabelWarnValue(gr ? \"Καταγεγραμμένος τύπος:\""));
        assertTrue(combinedLab4.contains("logLabelValue(gr ? \"Κατηγορία όρων:\""));
        assertTrue(finalLab6.contains("if (human.repeatedClueNeedsReview)"));
        assertTrue(finalLab6.contains("uniqueTechnicalCodes.add(finding.code)"));
        assertFalse(finalLab6.contains("logLabelOkValue(gr ? \"Ενδείξεις που αναγνώρισε το GEL:\""));
    }

    @Test public void demoContainsTwoIndependentFixturesAndNeverPersistsThem() throws Exception {
        String ui = source();
        assertTrue(ui.contains("ZIP FILE: PanicLog1"));
        assertTrue(ui.contains("ZIP FILE: PanicLog2"));
        assertTrue(ui.contains("watchdog timeout"));
        assertTrue(ui.contains("thermal shutdown"));
        assertTrue(ui.contains("DEMO finished: no simulated results were stored"));
        String demo = section("private void runDemoDiagnostics()", "// GUARD");
        assertTrue(demo.contains("demoMode = true"));
        assertTrue(demo.contains("demoMode = false"));
    }

    @Test public void lab3DoesNotHideRepeatedCodesOrUnrelatedRepairs() throws Exception {
        String lab = section("private void runImpactLab() {", "private int countSupportedPanicBlocks(");
        String evidence = section("private void logApplePanicEvidence(",
                "private void parseAndCacheSignature(");
        assertTrue(lab.contains("new java.util.LinkedHashSet<>()"));
        assertTrue(evidence.contains("alreadyShown != null && !alreadyShown.add(key)"));
        assertTrue(evidence.contains("IPhonePanicRepairKnowledgeBase.lookup(repairHistory,"));
        assertTrue(evidence.contains("findings.modelIdentifier, finding.code"));
        assertTrue(evidence.contains("caseRecord.localizedOutcome(gr)"));
        assertTrue(evidence.contains("caseRecord.sourceUrl"));
    }

    @Test public void lab5UsesOnlyActualDatesAndPerRecordSignatures() throws Exception {
        String lab = section("private void runRecurringDomainLab() {",
                "private void runFinalServiceRecommendationLab() {");
        assertTrue(lab.contains("ApplePanicEvidencePolicy.describeOne(block)"));
        assertTrue(lab.contains("meta.timestampEpochMillis >= 0L"));
        assertTrue(lab.contains("datedRecords.size() == total"));
        assertTrue(lab.contains("signatureCount.getOrDefault(meta.signatureKey, 0)"));
        assertTrue(lab.contains("Επαναλαμβανόμενη υπογραφή panic:"));
        assertTrue(lab.contains("Οι καταγεγραμμένες υπογραφές είναι διαφορετικές."));
        assertFalse(lab.contains("System uptime in nanoseconds"));
    }

    @Test public void lab6DoesNotRepeatDetailedCountsFromLabsFourAndFive() throws Exception {
        String lab = section("private void runFinalServiceRecommendationLab() {",
                "private void runDemoDiagnostics() {");
        assertTrue(lab.contains("IPhonePanicServiceSummary.interpret("));
        assertTrue(lab.contains("human.whatWeFound"));
        assertTrue(lab.contains("human.whatItMeans"));
        assertFalse(lab.contains("for (java.util.Map.Entry<String, Integer> e : crashCount.entrySet())"));
        assertFalse(lab.contains("for (java.util.Map.Entry<String, Integer> e : domainCount.entrySet())"));
        assertFalse(lab.contains("logLabelValue(gr ? \"Επανάληψη στο δείγμα:\""));
        assertFalse(lab.contains("Αυτό το συμπέρασμα αφορά μόνο τα δοκιμαστικά DEMO"));
    }
}
