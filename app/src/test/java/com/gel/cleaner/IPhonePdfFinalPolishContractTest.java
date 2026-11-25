package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Source-level regression protection for v58 iPhone single-log PDF QA. */
public final class IPhonePdfFinalPolishContractTest {
    private static String source(String filename) throws Exception {
        Path p=Paths.get("app/src/main/java/com/gel/cleaner/"+filename);
        if (!Files.isRegularFile(p)) p=Paths.get("src/main/java/com/gel/cleaner/"+filename);
        return new String(Files.readAllBytes(p),StandardCharsets.UTF_8);
    }
    private static String part(String s,String a,String b) {
        int i=s.indexOf(a),j=s.indexOf(b,i+a.length());
        assertTrue("Missing boundaries: "+a,i>=0&&j>i);
        return s.substring(i,j);
    }

    @Test public void unknownBoardIsNeutralButKnownBoardStillPositive() throws Exception {
        String i=source("IPhoneLabsActivity.java");
        String importSection=part(i,"private void openPanicLogPicker()","// LAB 1 — PANIC LOG ANALYZER");
        assertTrue(importSection.contains("\"Unknown\".equalsIgnoreCase(board.trim())"));
        assertTrue(importSection.contains("logLabelValue(\"Board ID:\", safe(board))"));
        assertTrue(importSection.contains("logLabelOkValue(\"Board ID:\", safe(board))"));
        assertTrue(importSection.contains("if (board == null || board.trim().isEmpty()"));
    }

    @Test public void oneRecordCannotBeCalledFullComparison() throws Exception {
        String i=source("IPhoneLabsActivity.java");
        String all=part(i,"private void runAllAppleDiagnostics()","private void disablePanicGuideForever()");
        assertTrue(all.contains("if (comparableLogs >= 2)"));
        assertTrue(all.contains("Η ανάλυση του διαθέσιμου panic log ολοκληρώθηκε."));
        assertTrue(all.contains("Τα συγκριτικά Labs 4–5 δεν εκτελέστηκαν"));
        assertTrue(all.contains("Ο πλήρης έλεγχος ολοκληρώθηκε."));
        assertTrue(all.contains("runFinalServiceRecommendationLab();"));
    }

    @Test public void labsFourAndFiveExplicitlyReportInsufficientSample() throws Exception {
        String i=source("IPhoneLabsActivity.java");
        String combined=part(i,"private void runPanicFrequencyLab()","private void runRecurringDomainLab()");
        assertTrue(combined.contains("Το Lab 4 δεν εκτελέστηκε:"));
        assertTrue(combined.contains("countSupportedPanicBlocks(blocks) <= 1"));
        assertTrue(combined.contains("crashCount.put(type,"));
        assertTrue(combined.contains("domainCount.put(domain,"));
        String recurring=part(i,"private void runRecurringDomainLab()",
                "private void runFinalServiceRecommendationLab()");
        assertTrue(recurring.contains("Το Lab 5 δεν εκτελέστηκε:"));
        assertTrue(i.contains("DEMO finished: no simulated results were stored"));
    }

    @Test public void reportIdentifiesHostInsteadOfClaimingToBeIPhone() throws Exception {
        String s=source("ServiceReportActivity.java");
        assertTrue(s.contains("Host / Συσκευή GEL:<br>"));
        assertTrue(s.contains("Host Android / Android GEL:<br>"));
        // Local source retains Host labeling; remote source explicitly
        // identifies that host as the technician's device.
        assertTrue(s.contains("String deviceLine = (remoteSessionId == null"));
        assertTrue(s.contains("\"Host / Συσκευή GEL: \""));
        assertTrue(s.contains("\"Technician host / Συσκευή τεχνικού: \""));
        assertTrue(s.contains("String osLine = \"Host Android / Android GEL: \""));
        assertFalse(s.contains("body.append(\"Device / Συσκευή:<br>\")"));
    }

    @Test public void printSignaturesAreCompactAndStayTogether() throws Exception {
        String s=source("ServiceReportActivity.java");
        assertTrue(s.contains(".signatures{margin-top:14px;break-inside:avoid;page-break-inside:avoid;}"));
        assertTrue(s.contains(".sig-row{display:flex;justify-content:space-between;gap:40px;margin-bottom:14px;break-inside:avoid;page-break-inside:avoid;}"));
        assertTrue(s.contains(".sig-line{border-bottom:2px solid #000;height:24px;margin-bottom:6px;}"));
        assertTrue(s.contains(".sig-block-full{width:60%;margin-top:12px;break-inside:avoid;page-break-inside:avoid;}"));
        assertTrue(s.contains(".line{height:18px;border-bottom:1px solid #333;}"));
        assertTrue(s.contains("Technician Name / Όνομα τεχνικού"));
        assertTrue(s.contains("Customer Name / Όνομα πελάτη"));
        assertTrue(s.contains("Date / Ημερομηνία"));
    }
}
