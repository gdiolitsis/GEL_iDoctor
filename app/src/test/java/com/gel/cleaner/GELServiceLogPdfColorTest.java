package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Regressions: HTML PDF must not discard per-value colors from Android Labs.
 * Plain report text remains byte-for-byte readable by other diagnostic Labs.
 */
public final class GELServiceLogPdfColorTest {

    @Test public void labelValueColorsStayDistinctEvenForIdenticalLabels() {
        GELServiceLog.clear();
        GELServiceLog.addStyledValue("RMS Μικροφώνου: 1390", "#39FF14");
        GELServiceLog.addStyledValue("RMS Μικροφώνου: 1390", "#E6E6E6");
        GELServiceLog.addStyledValue("Peak Μικροφώνου: 3219", "#39FF14");
        GELServiceLog.addStyledValue("Ποιότητα Ανίχνευσης: MEDIUM", "#39FF14");
        GELServiceLog.addStyledValue("⚠ Θερμοκρασία: Υψηλή", "#FFD700");
        List<GELServiceLog.PdfRow> rows = GELServiceLog.getPdfRows();
        assertEquals(5, rows.size());
        assertEquals("#39FF14", rows.get(0).valueColor);
        assertEquals("#E6E6E6", rows.get(1).valueColor);
        assertEquals("#39FF14", rows.get(2).valueColor);
        assertEquals("#39FF14", rows.get(3).valueColor);
        assertEquals("#FFD700", rows.get(4).valueColor);
        assertEquals("RMS Μικροφώνου: 1390\nRMS Μικροφώνου: 1390\n"
                        + "Peak Μικροφώνου: 3219\nΠοιότητα Ανίχνευσης: MEDIUM\n"
                        + "⚠ Θερμοκρασία: Υψηλή\n", GELServiceLog.getAll());
        GELServiceLog.clear();
        assertTrue(GELServiceLog.getPdfRows().isEmpty());
    }

    @Test public void longLogTrimmingKeepsPlainRowsAndColorsInSync() {
        GELServiceLog.clear();
        for (int i = 0; i < 850; i++) {
            GELServiceLog.add("OBS-" + i + "-ABCDEFGHIJKLMNOPQRSTUVWXYZ-ABCDEFGHIJKLMNOPQRSTUVWXYZ-0123456789");
        }
        GELServiceLog.addStyledValue("Έξοδος ηχείου: Ανιχνεύθηκε ακουστικό σήμα", "#39FF14");
        StringBuilder rebuilt = new StringBuilder();
        for (GELServiceLog.PdfRow row : GELServiceLog.getPdfRows()) {
            rebuilt.append(row.text).append('\n');
        }
        assertEquals(GELServiceLog.getAll(), rebuilt.toString());
        List<GELServiceLog.PdfRow> rows = GELServiceLog.getPdfRows();
        assertEquals("#39FF14", rows.get(rows.size()-1).valueColor);
        GELServiceLog.clear();
    }

    @Test public void printableHtmlConsumesExactSourceColorsNotValueHeuristics() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ServiceReportActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ServiceReportActivity.java");
        String src = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        assertTrue(src.contains("GELServiceLog.getPdfRows()"));
        assertTrue(src.contains("line.equals(pdfRow.text.trim())"));
        assertTrue(src.contains("pdfValueCssClass(pdfRow.valueColor)"));
        assertTrue(src.contains("escapeHtml(parts[0].trim())"));
        assertTrue(src.contains("escapeHtml(parts.length > 1 ? parts[1].trim() : \"\")"));
        assertTrue(src.contains(".value-ok{color:#0b7729"));
        assertTrue(src.contains(".value-warn{color:#855700"));
        assertTrue(src.contains(".value-err{color:#aa1630"));
        assertTrue(src.contains(".value-info{color:#176293"));
        assertTrue(src.contains("if (valueCss != null && line.contains(\":\"))"));
    }
}
