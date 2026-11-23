package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

/** Six independent roles; no deletion of source evidence or false hardware verdicts. */
public final class IPhoneSixLabStructureContractTest {
    private static String code(String file) throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner", file);
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner", file);
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
    private static String region(String s, String from, String to) {
        int a = s.indexOf(from), b = s.indexOf(to, a + from.length());
        assertTrue("Missing or inverted boundary: " + from, a >= 0 && b > a);
        return s.substring(a, b);
    }
    private static int count(String s, String needle) {
        int n = 0, p = -1;
        while ((p = s.indexOf(needle, p + 1)) >= 0) n++;
        return n;
    }

    @Test public void onlySixNumberedButtonsRemain() throws Exception {
        String src=code("IPhoneLabsActivity.java");
        String menu=region(src, "// 2) Panic Log Analyzer", "// LOG AREA (BOTTOM)");
        Matcher m = Pattern.compile("gr \\? \"LAB (\\d+) -").matcher(menu);
        Set<String> numbers=new LinkedHashSet<>();
        while (m.find()) numbers.add(m.group(1));
        assertEquals(6, numbers.size());
        assertEquals("[1, 2, 3, 4, 5, 6]", numbers.toString());
        for (int i=1;i<=6;i++) assertEquals(1,count(menu,"gr ? \"LAB "+i+" -"));
        assertFalse(src.contains("private void runStabilityLab()"));
        assertFalse(src.contains("private void runPanicClusteringLab()"));
        assertFalse(menu.contains("LAB 7"));
        assertFalse(menu.contains("LAB 8"));
    }

    @Test public void lab1RetainsExactPerRecordHistoryAndNeverInventsDates() throws Exception {
        String src=code("IPhoneLabsActivity.java");
        String lab=region(src,"private void runPanicLogAnalyzer()",
                "private void runPanicSignatureParser()");
        assertTrue(lab.contains("countSupportedPanicBlocks(separatePanicBlocks)"));
        assertTrue(lab.contains("logPanicIncidentHeader(incidentNumber,"));
        assertTrue(lab.contains("ApplePanicEvidencePolicy.describeOne(block)"));
        assertTrue(lab.contains("meta.timestamp.isEmpty()"));
        assertTrue(lab.contains("meta.timestamp"));
        assertTrue(lab.contains("No reliable timestamp supplied in this record"));
        assertFalse(lab.contains("parseAndCacheSignature(panicLogText)"));
    }

    @Test public void combinedLab4CountsBothDimensionsInSameEligibleRecordPass() throws Exception {
        String lab=region(code("IPhoneLabsActivity.java"),
                "private void runPanicFrequencyLab()", "private void runRecurringDomainLab()");
        assertEquals(1,count(lab,"for (String block : blocks)"));
        assertTrue(lab.contains("ApplePanicEvidencePolicy.hasPanicEvidence(block)"));
        assertTrue(lab.contains("parseAndCacheSignature(block)"));
        assertTrue(lab.contains("crashCount.put(type,"));
        assertTrue(lab.contains("domainCount.put(domain,"));
        assertTrue(lab.contains("Καταγεγραμμένος τύπος:"));
        assertTrue(lab.contains("Κατηγορία όρων:"));
        assertTrue(lab.contains("Το Lab 4 δεν εκτελέστηκε:"));
        assertTrue(lab.contains("Το Lab 4 ολοκληρώθηκε."));
        assertFalse(lab.contains("hardware failed"));
    }

    @Test public void recurrenceStillExaminesIndependentExactCluesAndDates() throws Exception {
        String lab=region(code("IPhoneLabsActivity.java"),
                "private void runRecurringDomainLab()",
                "private void runFinalServiceRecommendationLab()");
        assertTrue(lab.contains("meta.timestampEpochMillis >= 0L"));
        assertTrue(lab.contains("datedRecords.size() == total"));
        assertTrue(lab.contains("signatureCount.getOrDefault(meta.signatureKey, 0)"));
        assertTrue(lab.contains("seenInThisBlock.add(exactKey)"));
        assertTrue(lab.contains("IPhonePanicRepairKnowledgeBase.lookup(readPanicRepairHistory(), model, code)"));
        assertTrue(lab.contains("Το Lab 5 ολοκληρώθηκε."));
        assertTrue(lab.contains("Πηγές στο Lab 3."));
    }

    @Test public void autoDemoAndPdfPathHaveSameSixLabs() throws Exception {
        String src=code("IPhoneLabsActivity.java");
        String auto=region(src,"private void runAllAppleDiagnostics()",
                "private void disablePanicGuideForever()");
        String demo=region(src,"private void runDemoDiagnostics()", "// GUARD");
        String[] required={"runPanicLogAnalyzer();","runPanicSignatureParser();",
                "runImpactLab();","runPanicFrequencyLab();",
                "runRecurringDomainLab();","runFinalServiceRecommendationLab();"};
        for (String method:required) {
            assertEquals(1,count(auto,method));
            assertEquals(1,count(demo,method));
        }
        assertFalse(auto.contains("runStabilityLab()"));
        assertFalse(auto.contains("runPanicClusteringLab()"));
        assertFalse(demo.contains("runStabilityLab()"));
        assertFalse(demo.contains("runPanicClusteringLab()"));
        assertTrue(auto.contains("Labs 4–5"));
        assertTrue(src.contains("Οδηγίες και πηγές στο Lab 3."));
        assertTrue(src.contains("Intent(this, ServiceReportActivity.class)"));
        String report=code("ServiceReportActivity.java");
        assertTrue(report.contains("GELServiceLog.getPdfRows()"));
        assertTrue(demo.contains("demoMode = false"));
    }
}
