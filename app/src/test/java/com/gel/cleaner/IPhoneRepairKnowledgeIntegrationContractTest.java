package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Guard the common, offline, per-incident repair evidence path in Apple Labs. */
public final class IPhoneRepairKnowledgeIntegrationContractTest {
    private static String code(String path) throws Exception {
        Path f = Paths.get("app/src/main/java/com/gel/cleaner/" + path);
        if (!Files.isRegularFile(f)) {
            f = Paths.get("src/main/java/com/gel/cleaner/" + path);
        }
        return new String(Files.readAllBytes(f), StandardCharsets.UTF_8);
    }
    private static String region(String src, String a, String b) {
        int i = src.indexOf(a);
        int j = src.indexOf(b, i + a.length());
        assertTrue(a + ": missing or reordered", i >= 0 && j > i);
        return src.substring(i, j);
    }

    @Test public void oneCachedReadIsSharedByTechnicalAndFinalLabs() throws Exception {
        String src = code("IPhoneLabsActivity.java");
        String helper = region(src, "private java.util.List<IPhonePanicRepairKnowledgeBase.RepairCase> readPanicRepairHistory()",
                "private void logApplePanicEvidence(");
        assertTrue(helper.contains("if (repairCaseCache != null) return repairCaseCache;"));
        assertTrue(helper.contains("IPhonePanicRepairKnowledgeBase.parse("));
        assertTrue(helper.contains("getAssets().open(IPhonePanicRepairKnowledgeBase.ASSET)"));
        String lab4 = region(src, "private void runImpactLab()",
                "private int countSupportedPanicBlocks(");
        assertTrue(lab4.contains("logApplePanicEvidence(block, gr,"));
        String evidence = region(src, "private void logApplePanicEvidence(",
                "private void parseAndCacheSignature(");
        assertTrue(evidence.contains("readPanicRepairHistory()"));
        assertTrue(evidence.contains("IPhonePanicRepairKnowledgeBase.lookup(repairHistory,"));
        assertTrue(evidence.contains("findings.modelIdentifier, finding.code"));
        assertTrue(evidence.contains("IPhonePanicRepairKnowledgeBase.summarize(matchingReports)"));
        assertTrue(evidence.contains("caseRecord.localizedOutcome(gr)"));
        assertTrue(evidence.contains("caseRecord.localizedNote(gr)"));
        assertTrue(evidence.contains("caseRecord.sourceUrl"));
        assertTrue(evidence.contains("not independently verified repairs"));
    }

    @Test public void finalLabReportsSourceCoverageButNeverInfersAComponent() throws Exception {
        String lab8 = region(code("IPhoneLabsActivity.java"),
                "private void runFinalServiceRecommendationLab()", "private void runDemoDiagnostics()");
        assertTrue(lab8.contains("IPhonePanicRepairKnowledgeBase.lookup(repairHistory,"));
        assertTrue(lab8.contains("hints.modelIdentifier, finding.code"));
        assertTrue(lab8.contains("relatedRepairAttempts.put(historic.id, historic)"));
        assertTrue(lab8.contains("IPhonePanicRepairKnowledgeBase.summarize(relatedRepairAttempts.values())"));
        assertTrue(lab8.contains("if (!relatedRepairAttempts.isEmpty())"));
        assertTrue(lab8.contains("Στο Lab 4 εμφανίζονται"));
        assertFalse(lab8.contains("recommendedHardwareReplacement"));
        assertFalse(lab8.contains("successProbability"));
        assertTrue(lab8.contains("if (demoMode)"));
    }

    @Test public void cannotConflateSyntheticFixturesWithSuccessfulRepairRecords() throws Exception {
        String main = code("IPhoneLabsActivity.java");
        String demo = region(main, "private void runDemoDiagnostics()", "// GUARD");
        assertTrue(demo.contains("panicLogText = buildDemoPanicLogs()"));
        assertTrue(demo.contains("demoMode = true"));
        assertTrue(demo.contains("demoMode = false"));
        assertTrue(demo.contains("runFinalServiceRecommendationLab();"));
        String kb = code("IPhonePanicRepairKnowledgeBase.java");
        assertTrue(kb.contains("if (!panicCode.equals(canonicalCode(code))) return false;"));
        assertTrue(kb.contains("identifier.trim().equalsIgnoreCase(model.trim())"));
    }
}
