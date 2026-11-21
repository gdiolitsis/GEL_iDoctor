package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/**
 * UI/report source contracts: panic log evidence must not be merged, treated as
 * tested hardware, or represented as a successful device-health measurement.
 */
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
        assertTrue("Missing or reordered iPhone lab section " + start, a >= 0 && b > a);
        return source.substring(a, b);
    }

    @Test public void lab3ReviewsEventsInsteadOfClaimingMeasuredStability() throws Exception {
        String ui = source();
        assertTrue(ui.contains("LAB 3 - Αξιολόγηση Ιστορικού Συμβάντων"));
        String lab = section("private void runStabilityLab() {", "private void runImpactLab() {");
        assertTrue(lab.contains("countSupportedPanicBlocks(blocks)"));
        assertTrue(lab.contains("for (String block : blocks)"));
        assertTrue(lab.contains("parseAndCacheSignature(block)"));
        assertFalse(lab.contains("parseAndCacheSignature(panicLogText)"));
        assertTrue(lab.contains("Η τρέχουσα σταθερότητα του iPhone δεν μετρήθηκε"));
        assertTrue(lab.contains("Οι καταγραφές εξετάζονται ανεξάρτητα"));
        assertTrue(lab.contains("Το Lab 3 ολοκληρώθηκε."));
    }

    @Test public void lab4ClearlySeparatesKnownKeywordDomainFromUnknownPart() throws Exception {
        String lab = section("private void runImpactLab() {", "private int countSupportedPanicBlocks(");
        assertTrue(lab.contains("for (String block : blocks)"));
        assertTrue(lab.contains("parseAndCacheSignature(block)"));
        assertFalse(lab.contains("parseAndCacheSignature(panicLogText)"));
        assertTrue(lab.contains("IPhonePanicHumanizer.domain(sigDomain, gr)"));
        assertTrue(lab.contains("Δεν ταυτοποιήθηκε συγκεκριμένο ελαττωματικό εξάρτημα"));
        assertTrue(lab.contains("logApplePanicEvidence(block, gr,"));
        assertTrue(lab.contains("Το Lab 4 ολοκληρώθηκε."));
    }

    @Test public void finalServiceReportIsConciseEvidenceOnlyAndPerRecord() throws Exception {
        String lab = section("private void runFinalServiceRecommendationLab() {",
                "private void runDemoDiagnostics() {");
        assertTrue(lab.contains("for (String block : blocks)"));
        assertTrue(lab.contains("parseAndCacheSignature(block)"));
        assertTrue(lab.contains("ApplePanicHardwareHints.examineOne(block)"));
        assertFalse(lab.contains("logApplePanicEvidence(block, gr"));
        assertFalse(lab.contains("likelyHardware"));
        assertFalse(lab.contains("likelySoftware"));
        assertFalse(lab.contains("panicLogText.toLowerCase()"));
        assertTrue(lab.contains("IPhonePanicHumanizer.meaning(e.getKey(), gr)"));
        assertTrue(lab.contains("IPhonePanicServiceSummary.interpret("));
        assertTrue(lab.contains("human.repairAdvice"));
        assertTrue(lab.contains("human.whenToSeekHelp"));
        assertTrue(lab.contains("Πότε χρειάζεται τεχνικός:"));
        assertTrue(lab.contains("Για τα Watchdog:"));
        assertTrue(lab.contains("Για το Thermal:"));
        assertTrue(lab.contains("Το Lab 8 ολοκληρώθηκε."));
    }

    @Test public void recordedIncidentsAreNotPaintedAsGreenDeviceHealth() throws Exception {
        String source = source();
        String lab2 = section("private void runPanicSignatureParser() {",
                "private void runStabilityLab() {");
        String lab5 = section("private void runPanicFrequencyLab() {",
                "private void runPanicClusteringLab() {");
        String lab6 = section("private void runPanicClusteringLab() {",
                "private void runRecurringDomainLab() {");
        String lab8 = section("private void runFinalServiceRecommendationLab() {",
                "private void runDemoDiagnostics() {");

        assertTrue(lab2.contains("logLabelWarnValue("));
        assertTrue(lab5.contains("logLabelWarnValue(gr ? \"Καταγεγραμμένος τύπος:\""));
        assertTrue(lab6.contains("logLabelValue(gr ? \"Κατηγορία όρων:\""));
        assertTrue(lab8.contains("logLabelWarnValue(gr ? \"Είδος συμβάντος στο αρχείο:\""));
        assertTrue(lab8.contains("logLabelOkValue(gr ? \"Αρχεία συμβάντων που αναλύθηκαν:\""));
    }

    @Test public void demoStillContainsTwoIndependentFixturesAndNeverPersistsThem() throws Exception {
        String text = source();
        assertTrue(text.contains("ZIP FILE: PanicLog1"));
        assertTrue(text.contains("ZIP FILE: PanicLog2"));
        assertTrue(text.contains("watchdog timeout"));
        assertTrue(text.contains("thermal shutdown"));
        assertTrue(text.contains("DEMO finished: no simulated results were stored"));
    }

    @Test public void lab4ExplainsObservedFieldsAndDoesNotHideAReappearingCode() throws Exception {
        String lab = section("private void runImpactLab() {",
                "private int countSupportedPanicBlocks(");
        assertTrue(lab.contains("ApplePanicEvidencePolicy.describeOne(block)"));
        assertTrue(lab.contains("meta.osVersion"));
        assertTrue(lab.contains("meta.process"));
        assertTrue(lab.contains("meta.signatureKey"));
        assertTrue(lab.contains("— υπογραφή panic:"));
        assertTrue(lab.contains("shownCodes = new java.util.LinkedHashSet<>()"));
        assertTrue(lab.contains("logApplePanicEvidence(block, gr,"));
    }

    @Test public void lab7UsesOnlyActualRecordDatesAndSeparateSignatures() throws Exception {
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

    @Test public void lab8DoesNotRepeatDomainAndRecurrenceDetailsFromLabs6And7() throws Exception {
        String lab = section("private void runFinalServiceRecommendationLab() {",
                "private void runDemoDiagnostics() {");
        assertTrue(lab.contains("IPhonePanicServiceSummary.interpret("));
        assertTrue(lab.contains("human.whatWeFound"));
        assertTrue(lab.contains("human.whatItMeans"));
        assertFalse(lab.contains("for (java.util.Map.Entry<String, Integer> e : domainCount.entrySet())"));
        assertFalse(lab.contains("logLabelValue(gr ? \"Επανάληψη στο δείγμα:\""));
        assertTrue(lab.contains("if (demoMode)"));
    }
}
