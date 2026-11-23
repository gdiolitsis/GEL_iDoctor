package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/** Each iPhone Lab adds evidence instead of repeating the preceding report. */
public final class IPhoneSixLabNoDuplicateOutputContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/IPhoneLabsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/IPhoneLabsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
    private static String section(String s, String a, String b) {
        int start = s.indexOf(a), end = s.indexOf(b, start + a.length());
        assertTrue("Missing section: " + a, start >= 0 && end > start);
        return s.substring(start, end);
    }

    @Test public void lab1RetainsOnePerRecordIdentityAndDate() throws Exception {
        String lab = section(source(), "private void runPanicLogAnalyzer()", "private void runPanicSignatureParser()");
        assertTrue(lab.contains("IPSPanicParser.analyze(this, block)"));
        assertTrue(lab.contains("meta.timestamp"));
        assertFalse(lab.contains("ApplePanicEvidencePolicy.recommendation(gr)"));
    }

    @Test public void lab2AddsSignatureAndEvidenceInsteadOfRepeatedEventType() throws Exception {
        String lab = section(source(), "private void runPanicSignatureParser()", "private void runImpactLab()");
        assertTrue(lab.contains("for (String block : blocks)"));
        assertTrue(lab.contains("meta.signatureKey"));
        assertTrue(lab.contains("Ερμηνεία υπογραφής:"));
        assertTrue(lab.contains("Στοιχεία που εντοπίστηκαν:"));
        assertFalse(lab.contains("Συμβάν \" + incidentNumber + \" — τύπος:"));
        assertFalse(lab.contains("panicEventExact(block, sigCrashType, gr)"));
    }

    @Test public void lab3KeepsAppleFirstAndFullRepairSourcesWithoutEventRelabeling() throws Exception {
        String lab = section(source(), "private void runImpactLab()", "private int countSupportedPanicBlocks(");
        assertTrue(lab.contains("logOfficialApplePanicGuidance(model, gr, appleTopicsByModel.get(model))"));
        assertTrue(lab.contains("logApplePanicEvidence(block, gr,"));
        assertTrue(lab.contains("logPanicIncidentHeader(incident, \"\", gr)"));
        assertFalse(lab.contains("— τύπος:"));
    }

    @Test public void lab4OnlyCountsAndLab5OnlyIdentifiesRecurrence() throws Exception {
        String s = source();
        String four = section(s, "private void runPanicFrequencyLab()", "private void runRecurringDomainLab()");
        String five = section(s, "private void runRecurringDomainLab()", "private void runFinalServiceRecommendationLab()");
        assertTrue(four.contains("crashCount.put(type,"));
        assertTrue(four.contains("domainCount.put(domain,"));
        assertTrue(five.contains("signatureCount.getOrDefault(meta.signatureKey, 0)"));
        assertTrue(five.contains("seenInThisBlock.add(exactKey)"));
        assertFalse(five.contains("Επαναλαμβανόμενη κατηγορία:"));
        assertFalse(five.contains("domainCount.put(key,"));
    }

    @Test public void lab6ProducesServiceConclusionNotSecondEventReport() throws Exception {
        String lab = section(source(), "private void runFinalServiceRecommendationLab()", "private void runDemoDiagnostics()");
        assertTrue(lab.contains("IPhonePanicServiceSummary.interpret("));
        assertTrue(lab.contains("human.repairAdvice"));
        assertTrue(lab.contains("human.whenToSeekHelp"));
        assertTrue(lab.contains("Οδηγίες και πηγές στο Lab 3."));
        assertFalse(lab.contains("logPanicIncidentHeader("));
        assertFalse(lab.contains("uniqueTechnicalCodes.add("));
        assertFalse(lab.contains("relatedRepairAttempts"));
        assertFalse(lab.contains("Καταγεγραμμένος τύπος:"));
    }
}
