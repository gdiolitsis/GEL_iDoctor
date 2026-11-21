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
        assertTrue(lab4.contains("AppleOfficialServiceGuidance.topicMightBeUsefulForAction("));
        assertTrue(lab4.contains("logOfficialApplePanicGuidance(model, gr, appleTopicsByModel.get(model))"));
        assertTrue(src.contains("μόνο αν υπάρχει σχετικό σύμπτωμα"));
        assertFalse(evidence.contains("Apple-certified repair"));
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

    @Test public void recurrenceLabUsesTheSameModelCodeAndRealPerBlockClues() throws Exception {
        String lab7 = region(code("IPhoneLabsActivity.java"),
                "private void runRecurringDomainLab()",
                "private void runFinalServiceRecommendationLab()");
        assertTrue(lab7.contains("ApplePanicHardwareHints.examineOne(block)"));
        assertTrue(lab7.contains("seenInThisBlock.add(exactKey)"));
        assertTrue(lab7.contains("clue.getValue() < 2"));
        assertTrue(lab7.contains("IPhonePanicRepairKnowledgeBase.lookup(readPanicRepairHistory(), model, code)"));
        assertTrue(lab7.contains("IPhonePanicRepairKnowledgeBase.summarize(history)"));
        assertTrue(lab7.contains("Δείτε το Lab 4."));
        assertFalse(lab7.contains("replaceConfirmedFaultyComponent"));
    }

    @Test public void appleDocumentationIsSeparateFromRealRepairOutcomes() throws Exception {
        String source = code("IPhoneLabsActivity.java");
        String officialHelper = region(source,
                "private java.util.List<AppleOfficialServiceGuidance.Guidance> readAppleOfficialGuidance()",
                "private java.util.List<IPhonePanicRepairKnowledgeBase.RepairCase> readPanicRepairHistory()");
        assertTrue(officialHelper.contains("getAssets().open(AppleOfficialServiceGuidance.ASSET)"));
        assertTrue(officialHelper.contains("AppleOfficialServiceGuidance.modelManual(docs, model)"));
        assertTrue(officialHelper.contains("AppleOfficialServiceGuidance.genericForTopic(docs, \"UNEXPECTED_RESTART\")"));
        assertTrue(officialHelper.contains("AppleOfficialServiceGuidance.forTopic(docs, model, \"APPLE_DIAGNOSTICS\")"));
        assertTrue(officialHelper.contains("Η καταχωρισμένη επίσημη τεκμηρίωση δεν αποδίδει"));
        String lab4 = region(source, "private void runImpactLab()",
                "private int countSupportedPanicBlocks(");
        assertTrue(lab4.contains("documentedAppleModels.add(observed.modelIdentifier)"));
        assertTrue(lab4.contains("logOfficialApplePanicGuidance(model, gr, appleTopicsByModel.get(model))"));
        assertTrue(lab4.contains("logApplePanicEvidence(block, gr,"));
        String lab8 = region(source, "private void runFinalServiceRecommendationLab()",
                "private void runDemoDiagnostics()");
        assertFalse(lab8.contains("AppleOfficialServiceGuidance.modelManual("));
        assertTrue(lab8.contains("Δείτε το Lab 4: πρώτα η επίσημη Apple"));
        assertFalse(lab8.contains("Apple confirmed panic code"));
    }

    @Test public void officialGuidanceAndForumHistoryUseNeutralColoredServiceLogValues() throws Exception {
        String source = code("IPhoneLabsActivity.java");
        String formatter = region(source, "private void logLabelInfoValue(",
                "private void logLabelOkValue(");
        assertTrue(formatter.contains("<font color='#87CEFA'>"));
        assertTrue(formatter.contains("GELServiceLog.iPhoneInfoValue(l, v, \"#87CEFA\")"));
        assertTrue(formatter.contains("if (!demoMode)"));
        assertTrue(formatter.contains("linkifyEvidenceSources(v)"));
        assertTrue(source.contains("LinkMovementMethod.getInstance()"));
        assertTrue(source.contains("txtLog.setLinksClickable(true)"));
        assertTrue(source.contains("txtLog.setClickable(true)"));
        String links = region(source, "private String linkifyEvidenceSources(",
                "private void logLabelInfoValue(");
        assertTrue(links.contains("support"));
        assertTrue(links.contains("ifixit"));
        assertTrue(links.contains("nopowerfix"));
        assertTrue(links.contains("html.append(\"<a href='\""));
        assertTrue(links.contains("escape(text.substring(previous, links.start()))"));

        String history = region(source, "private void logApplePanicEvidence(",
                "private void parseAndCacheSignature(");
        assertTrue(history.contains("logLabelInfoValue(gr ? \"Εργασία και έκβαση:\""));
        assertTrue(history.contains("logLabelInfoValue(gr ? \"Πηγή περιστατικού:\""));
        assertFalse(history.contains("relatedApple.sourceUrl"));
        assertTrue(history.contains("caseRecord.localizedOutcome(gr)"));
        assertFalse(history.contains("logLabelOkValue(gr ? \"Εργασία και έκβαση:\""));

        String lab8 = region(source, "private void runFinalServiceRecommendationLab()",
                "private void runDemoDiagnostics()");
        assertTrue(lab8.contains("logLabelInfoValue(gr ? \"Σχετικές δημοσιευμένες επισκευές:\""));
        assertFalse(lab8.contains("AppleOfficialServiceGuidance.forTopic(officialDocs,"));
        assertTrue(lab8.contains("Δείτε το Lab 4: πρώτα η επίσημη Apple"));
        assertFalse(lab8.contains("Apple-certified panic repair"));
    }

    @Test public void officialAppleAlwaysPrecedesIndependentRepairsAcrossAllImportedFiles() throws Exception {
        String src = code("IPhoneLabsActivity.java");
        String lab4 = region(src, "private void runImpactLab()",
                "private int countSupportedPanicBlocks(");
        int official = lab4.indexOf("1. ΕΠΙΣΗΜΕΣ ΟΔΗΓΙΕΣ APPLE");
        int renderApple = lab4.indexOf("logOfficialApplePanicGuidance(model, gr,");
        int community = lab4.indexOf("2. ΔΙΕΘΝΗΣ ΕΜΠΕΙΡΙΑ ΕΠΙΣΚΕΥΩΝ");
        int renderHistory = lab4.indexOf("logApplePanicEvidence(block, gr,");
        assertTrue("Apple section must be first", official >= 0);
        assertTrue("Apple references must be displayed before any case histories",
                official < renderApple && renderApple < community && community < renderHistory);
        assertTrue(lab4.contains("documentedAppleModels.add(observed.modelIdentifier)"));
        assertTrue(lab4.contains("appleTopicsByModel.get(model)"));
        assertTrue(lab4.contains("Independent reports for the same model and clue"));
        String evidence = region(src, "private void logApplePanicEvidence(",
                "private void parseAndCacheSignature(");
        assertFalse("Official instructions should never be hidden inside a forum history block",
                evidence.contains("AppleOfficialServiceGuidance.forTopic("));
        assertTrue(evidence.contains("No source-linked matching case is currently indexed"));
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
