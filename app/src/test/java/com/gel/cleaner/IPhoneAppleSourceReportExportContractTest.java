package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/**
 * The same real, provenance-tagged iPhone lab lines must survive both PDF
 * engines. This is static wiring QA, not a claim that Android printed a PDF.
 */
public final class IPhoneAppleSourceReportExportContractTest {
    private static String source(String filename) throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/" + filename);
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/" + filename);
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void originalAppleUrlsAndPublishedReportsReachServiceLog() throws Exception {
        String labs = source("IPhoneLabsActivity.java");
        assertTrue(labs.contains("GELServiceLog.iPhoneInfoValue(l, v, \"#87CEFA\")"));
        assertTrue(labs.contains("relatedApple.sourceUrl"));
        assertTrue(labs.contains("caseRecord.sourceUrl"));
        assertTrue(labs.contains("IPhonePanicRepairEvidenceReview.assessExact("));
        assertTrue(labs.contains("getAssets().open(IPhonePanicRepairKnowledgeBase.ASSET)"));
        assertTrue(labs.contains("Intent(this, ServiceReportActivity.class)"));
    }

    @Test public void HTMLPrintRendersOnlyEscapedAndLinkifiedSourceValues() throws Exception {
        String report = source("ServiceReportActivity.java");
        assertTrue(report.contains("private String buildHtmlReport(String report)"));
        assertTrue(report.contains("GELServiceLog.getPdfRows()"));
        assertTrue(report.contains("GELReportSourceLinks.escapeAndLinkify(line)"));
        assertTrue(report.contains("GELReportSourceLinks.escapeAndLinkify(line.substring(pdfRow.valueStart))"));
        assertTrue(report.contains("GELReportSourceLinks.escapeAndLinkify(parts[1].trim())"));
        assertTrue(report.contains(".source-link{color:inherit;text-decoration:underline;"));
        assertTrue(report.contains("htmlPrintRequested = true"));
        assertFalse(report.contains("setJavaScriptEnabled(true)"));
    }

    @Test public void TXTPrintMeasuresWidthAndReappliesOriginalValueColors() throws Exception {
        String report = source("ServiceReportActivity.java");
        // Both remote and local reports use the same PDF renderer.
        // The local path must still read the original GELServiceLog.
        assertTrue(report.contains("String text = reportSourceText();"));
        assertTrue(report.contains("GELServiceLog.getAll();"));
        assertTrue(report.contains("private String reportSourceText()"));
        assertTrue(report.contains("GELServiceLog.getPdfRows()"));
        assertTrue(report.contains("GELPdfMeasuredLineWrap.split(sub,"));
        assertTrue(report.contains("piece -> textPaint.measureText(piece)"));
        assertTrue(report.contains("sourceValueAt - fragment.start"));
        assertTrue(report.contains("fragment.end > sourceValueAt"));
        assertTrue(report.contains("drawLineWithColoredEmoji(canvas, fragment.text"));
        assertTrue(report.contains("PAGE_WIDTH - 2f * marginX - 18f"));
        assertTrue(report.contains("pdfValuePrintColor(matchedRow.valueColor)"));
    }

    @Test public void PrintOrSaveIsNotIncorrectlyReportedAsCompleted() throws Exception {
        String report = source("ServiceReportActivity.java");
        assertTrue(report.contains("Choose 'Save as PDF' in the print dialog."));
        assertTrue(report.contains("The platform PrintManager opens an Android print/save UI"));
        assertFalse(report.contains("HTML PDF saved automatically"));
    }
}
