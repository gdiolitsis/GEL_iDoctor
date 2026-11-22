package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Source contract for preserving iPhone panic evidence and DEMO isolation. */
public final class IPhoneLab8HumanPresentationContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/IPhoneLabsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/IPhoneLabsActivity.java");
        return new String(Files.readAllBytes(p),StandardCharsets.UTF_8);
    }

    private static String part(String input, String begin, String end) {
        int i=input.indexOf(begin), j=input.indexOf(end,i+begin.length());
        assertTrue(begin+" / "+end,i>=0&&j>i);
        return input.substring(i,j);
    }

    @Test public void humanResultAppearsBeforeEvidenceDetailsAndIsNeverGreenCertification() throws Exception {
        String lab8=part(source(), "private void runFinalServiceRecommendationLab()", "private void runDemoDiagnostics()");
        int human=lab8.indexOf("Συνολικό συμπέρασμα — με απλά λόγια");
        int codes=lab8.indexOf("Ενδείξεις που αναγνώρισε το GEL:");
        assertTrue(human>=0 && codes>human);
        assertFalse(lab8.contains("for (java.util.Map.Entry<String, Integer> e : crashCount.entrySet())"));
        assertTrue(lab8.contains("IPhonePanicServiceSummary.interpret("));
        assertTrue(lab8.contains("human.repairAdvice"));
        assertTrue(lab8.contains("human.whatItMeans"));
        assertTrue(lab8.contains("human.whenToSeekHelp"));
        assertTrue(lab8.contains("logLabelValue(gr ? \"Χρειάζεται επισκευή;\""));
        assertTrue(lab8.contains("if (human.repeatedClueNeedsReview)"));
        assertFalse(lab8.contains("logLabelOkValue(gr ? \"Χρειάζεται επισκευή;\""));
    }

    @Test public void noHardwareDiagnosisInventedByMergingTwoRecords() throws Exception {
        String lab8=part(source(), "private void runFinalServiceRecommendationLab()", "private void runDemoDiagnostics()");
        assertTrue(lab8.contains("ApplePanicEvidencePolicy.hasPanicEvidence(block)"));
        assertTrue(lab8.contains("parseAndCacheSignature(block)"));
        assertTrue(lab8.contains("ApplePanicHardwareHints.examineOne(block)"));
        assertTrue(lab8.contains("uniqueTechnicalCodes.add(finding.code)"));
        assertTrue(lab8.contains("repeatedCategory"));
        assertTrue(lab8.contains("domainCount.getOrDefault"));
        assertFalse(lab8.contains("logLabelOkValue(gr ? \"Ενδείξεις που αναγνώρισε το GEL:\""));
    }

    @Test public void demoIsExplicitAndNeverPersistedAsCustomerEvidence() throws Exception {
        String src=source();
        String lab8=part(src, "private void runFinalServiceRecommendationLab()", "private void runDemoDiagnostics()");
        assertFalse(lab8.contains("Αυτό το συμπέρασμα αφορά μόνο τα δοκιμαστικά DEMO"));
        String demo=part(src, "private void runDemoDiagnostics()", "// GUARD");
        assertTrue(demo.contains("demoMode = true;"));
        assertTrue(demo.contains("runFinalServiceRecommendationLab();"));
        assertTrue(demo.contains("κανένα δοκιμαστικό αποτέλεσμα δεν αποθηκεύτηκε"));
        assertTrue(demo.contains("demoMode = false;"));
    }

    @Test public void oneLogComparisonLabsStayUnchanged() throws Exception {
        String src=source();
        assertTrue(part(src, "private void runPanicFrequencyLab()", "private void runPanicClusteringLab()")
                .contains("Το Lab 5 δεν εκτελέστηκε:"));
        assertTrue(part(src, "private void runPanicClusteringLab()", "private void runRecurringDomainLab()")
                .contains("Το Lab 6 δεν εκτελέστηκε:"));
        assertTrue(part(src, "private void runRecurringDomainLab()", "private void runFinalServiceRecommendationLab()")
                .contains("Το Lab 7 δεν εκτελέστηκε:"));
    }
}
