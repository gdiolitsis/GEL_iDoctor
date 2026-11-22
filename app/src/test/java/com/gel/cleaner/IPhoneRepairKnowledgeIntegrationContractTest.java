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

        String lab4 = region(src, "private void runImpactLab()", "private int countSupportedPanicBlocks(");
        assertTrue(lab4.contains("logApplePanicEvidence(block, gr,"));
        assertTrue(lab4.contains("AppleOfficialServiceGuidance.conditionalTopicsForClue(finding.code)"));
        assertTrue(lab4.contains("topics.add(\"UNEXPECTED_RESTART\")"));
        assertTrue(lab4.contains("topics.add(\"THERMAL\")"));
        assertTrue(lab4.contains("logOfficialApplePanicGuidance(model, gr, appleTopicsByModel.get(model))"));
        assertFalse(lab4.contains("AppleOfficialServiceGuidance.topicMightBeUsefulForAction("));
        String evidence = region(src, "private void logApplePanicEvidence(",
                "private void parseAndCacheSignature(");
        assertTrue(evidence.contains("readPanicRepairHistory()"));
        assertTrue(evidence.contains("IPhonePanicRepairKnowledgeBase.lookup(repairHistory,"));
        assertTrue(evidence.contains("findings.modelIdentifier, finding.code"));
        assertTrue("Exact model and clue are mandatory",
                evidence.contains("IPhonePanicRepairEvidenceReview.assessExact(repairHistory,"));
        assertTrue(evidence.contains("reviewedHistory.display(gr)"));
        assertTrue(evidence.contains("caseRecord.localizedAction(gr)"));
        assertTrue(evidence.contains("caseRecord.localizedOutcome(gr)"));
        assertTrue(evidence.contains("caseRecord.localizedNote(gr)"));
        assertTrue(evidence.contains("caseRecord.sourceUrl"));
        assertFalse("Do not duplicate evidence summaries",
                evidence.contains("reviewedHistory.independentEvidence(gr)"));
        assertFalse(evidence.contains("action.display(gr)"));
        assertFalse(evidence.contains("Apple-certified repair"));
        assertTrue(src.contains("AppleIPhoneManualChapters.forExactModel(readAppleManualChapters(), docs, model)"));
        assertTrue(src.contains("AppleIPhoneManualChapters.troubleshootingForTopics(sections, suggestedTopics)"));
        assertTrue("Full procedure library remains in optional picker",
                src.contains("AppleIPhoneManualChapters.Phase.PROCEDURE"));
        assertTrue(src.contains("showAppleServiceChapterPicker("));
        assertFalse("Full procedure TOC must not flood routine report",
                src.contains("Κατάλογος διαθέσιμων κεφαλαίων, ΟΧΙ προτεινόμενες αντικαταστάσεις."));
    }

    @Test public void finalLabReportsSourceCoverageButNeverInfersAComponent() throws Exception {
        String src = code("IPhoneLabsActivity.java");
        String lab8 = region(src, "private void runFinalServiceRecommendationLab()",
                "private void runDemoDiagnostics()");
        assertTrue(lab8.contains("IPhonePanicRepairKnowledgeBase.lookup(repairHistory,"));
        assertTrue(lab8.contains("hints.modelIdentifier, finding.code"));
        assertTrue(lab8.contains("relatedRepairAttempts.put(historic.id, historic)"));
        assertTrue(lab8.contains("IPhonePanicRepairKnowledgeBase.summarize(relatedRepairAttempts.values())"));
        assertTrue(lab8.contains("if (!relatedRepairAttempts.isEmpty())"));
        assertTrue(lab8.contains("Οδηγίες και πηγές στο Lab 4."));
        assertFalse(lab8.contains("recommendedHardwareReplacement"));
        assertFalse(lab8.contains("successProbability"));
        assertFalse("No second verbose disclaimer for the same DEMO",
                lab8.contains("Αυτό το συμπέρασμα αφορά μόνο τα δοκιμαστικά DEMO"));
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
        assertTrue(lab7.contains("Πηγές στο Lab 4."));
        assertFalse(lab7.contains("replaceConfirmedFaultyComponent"));
    }

    @Test public void appleDocumentationIsSeparateFromRealRepairOutcomes() throws Exception {
        String source = code("IPhoneLabsActivity.java");
        String officialHelper = region(source,
                "private java.util.List<AppleOfficialServiceGuidance.Guidance> readAppleOfficialGuidance()",
                "private java.util.List<IPhonePanicRepairKnowledgeBase.RepairCase> readPanicRepairHistory()");
        assertTrue(officialHelper.contains("getAssets().open(AppleOfficialServiceGuidance.ASSET)"));
        assertTrue(officialHelper.contains("AppleOfficialServiceGuidance.modelManual(docs, model)"));
        assertTrue(officialHelper.contains("AppleOfficialServiceGuidance.genericForTopicAll(docs, topic)"));
        assertTrue(officialHelper.contains("shownLinks.add(relatedApple.sourceUrl)"));
        assertTrue(officialHelper.contains("shownLinks.size() >= 3"));
        assertTrue(officialHelper.contains("if (suggestedTopics == null || suggestedTopics.isEmpty()) return;"));
        assertTrue(officialHelper.contains("!suggestedTopics.contains(\"UNEXPECTED_RESTART\")"));
        assertFalse(officialHelper.contains("AppleOfficialServiceGuidance.forTopic(docs, model, \"POST_REPAIR\")"));
        assertFalse(officialHelper.contains("AppleIPhoneManualChapters.phase(sections, AppleIPhoneManualChapters.Phase.PROCEDURE)"));
        assertFalse(officialHelper.contains("Apple — ασφάλεια πριν από επέμβαση:"));

        String lab4 = region(source, "private void runImpactLab()",
                "private int countSupportedPanicBlocks(");
        assertTrue(lab4.contains("documentedAppleModels.add(observed.modelIdentifier)"));
        assertTrue(lab4.contains("logOfficialApplePanicGuidance(model, gr, appleTopicsByModel.get(model))"));
        assertTrue(lab4.contains("logApplePanicEvidence(block, gr,"));
        String lab8 = region(source, "private void runFinalServiceRecommendationLab()",
                "private void runDemoDiagnostics()");
        assertFalse(lab8.contains("AppleOfficialServiceGuidance.modelManual("));
        assertTrue(lab8.contains("Οδηγίες και πηγές στο Lab 4."));
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
        assertTrue(history.contains("logLabelInfoValue(gr ? \"Επισκευή / αποτέλεσμα:\""));
        assertTrue(history.contains("logLabelInfoValue(gr ? \"Πηγή περιστατικού:\""));
        assertFalse(history.contains("relatedApple.sourceUrl"));
        assertTrue(history.contains("caseRecord.localizedOutcome(gr)"));
        assertFalse(history.contains("logLabelOkValue(gr ? \"Επισκευή / αποτέλεσμα:\""));
        String lab8 = region(source, "private void runFinalServiceRecommendationLab()",
                "private void runDemoDiagnostics()");
        assertTrue(lab8.contains("logLabelInfoValue(gr ? \"Σχετικές δημοσιευμένες επισκευές:\""));
        assertTrue(lab8.contains("Οδηγίες και πηγές στο Lab 4."));
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
        assertTrue("Apple references before case histories",
                official < renderApple && renderApple < community && community < renderHistory);
        assertTrue(lab4.contains("documentedAppleModels.add(observed.modelIdentifier)"));
        assertTrue(lab4.contains("appleTopicsByModel.get(model)"));
        assertFalse("Forum repairs never decide the official selection",
                lab4.contains("historic.actionKey"));
        assertTrue(src.contains("AppleOfficialServiceGuidance.genericForTopicAll(docs, topic)"));
        assertFalse(lab4.contains("Διαδικασίες που περιλαμβάνει το εγχειρίδιο"));
        String evidence = region(src, "private void logApplePanicEvidence(",
                "private void parseAndCacheSignature(");
        assertFalse(evidence.contains("AppleOfficialServiceGuidance.forTopic("));
        assertTrue(evidence.contains("No case is indexed for this exact model and clue."));
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
    @Test public void conditionalManufacturerPartsNeverFireFromPanicOnlyLab4Flow() throws Exception {
        String activity = code("IPhoneLabsActivity.java");
        String lab4 = region(activity, "private void runImpactLab()",
                "private int countSupportedPanicBlocks(");
        assertFalse("Imported panic logs must not trigger Apple part replacement rules",
                activity.contains("AppleIPhoneConditionalServiceRules.forVerifiedObservedResult("));
        assertFalse(lab4.contains("AppleIPhoneConditionalServiceRules"));
        String rules = code("AppleIPhoneConditionalServiceRules.java");
        assertTrue(rules.contains("if (!externallyVerifiedResult"));
        assertTrue(rules.contains("model.applies(rule.hardwareId)"));
        assertTrue(rules.contains("row.requiredResult == observedResult"));
    }

}
