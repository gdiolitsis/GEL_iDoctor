package com.gel.cleaner;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/**
 * Customer report provenance: the export never silently falls back to the
 * local technician's log. Professional branding remains on the existing
 * authorized TXT/HTML PDF route.
 */
public final class GELRemoteCustomerReportContractTest {
    private static String source(String file) throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/", file);
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/", file);
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void remoteReportUsesIndependentServerSource() throws Exception {
        String loader = source("GELRemoteServiceReportLoader.java");
        assertTrue(loader.contains("get(Source.SERVER)"));
        assertTrue(loader.contains("session.getString(\"technicianUid\")"));
        assertTrue(loader.contains("session.getTimestamp(\"serviceEndsAt\")"));
        assertTrue(loader.contains("service_ends_at"));
        assertTrue(loader.contains("SERVICE REPORT — CUSTOMER DEVICE"));
        assertTrue(loader.contains("Service Session ID: "));
        assertFalse(loader.contains("GELServiceLog.getAll()"));
        assertFalse(loader.contains("GELServiceLog.clear()"));
        assertTrue(loader.contains("result.batches + docs.size() > MAX_BATCHES"));
    }

    @Test public void txtAndHtmlPdfUseSameSourceAndNoCrossDeviceLogs() throws Exception {
        String report = source("ServiceReportActivity.java");
        assertTrue(report.contains("GELRemoteServiceReportLoader.fetch(this, remoteSessionId"));
        assertTrue(report.contains("String text = reportSourceText();"));
        assertTrue(report.contains("String data = reportSourceText();"));
        assertEquals(2, report.split("java.util.List<GELServiceLog.PdfRow> pdfRows = reportSourceRows\\(\\);", -1).length-1);
        assertTrue(report.contains("if (remoteSessionId == null) GELServiceLog.clear();"));
        assertTrue(report.contains("if (!remoteReportAllowed())"));
        assertTrue(report.contains("getProfessionalLogoDataUri()"));
        assertTrue(report.contains("getProfessionalLogoBitmap()"));
        assertTrue(report.contains("GelReportStore.prepare(this, identity, attempt, choice == 2"));
        assertTrue(report.contains("reportIdentity()"));
    }

    @Test public void onlyExplicitCustomerStyleMetadataIsTransferred() throws Exception {
        String client = source("GELRemoteDiagnosticsSync.java");
        String pdfRows = source("GELServiceLog.java");
        assertTrue(client.contains("GELServiceLog.getPdfRows()"));
        assertTrue(client.contains("item.text.equals(evidence.text.trim())"));
        assertTrue(client.contains("data.put(\"styles\", styles)"));
        assertTrue(pdfRows.contains("detachedPdfRow("));
        assertTrue(pdfRows.contains("new PdfRow(safeText, safeColor, safeStart)"));
    }

    @Test public void technicianCanOpenCustomerReportFromReadOnlyViewer() throws Exception {
        String viewer = source("GELReadOnlyDiagnosticsActivity.java");
        assertTrue(viewer.contains("ΕΞΑΓΩΓΗ SERVICE REPORT ΠΕΛΑΤΗ"));
        assertTrue(viewer.contains("GELRemoteServiceReportLoader.EXTRA_SESSION_ID"));
        assertTrue(viewer.contains("ServiceReportActivity.class"));
        assertFalse(viewer.contains("GELRemoteAssistActivity.class"));
    }

    @Test public void customerMustConsentBeforeDiagnosticSharing() throws Exception {
        String pairing = source("ConnectToTechnicianActivity.java");
        assertTrue(pairing.contains("ΣΥΝΔΕΣΗ & ΚΟΙΝΟΠΟΙΗΣΗ"));
        assertTrue(pairing.contains("beginClaimServiceSession(sessionId, serviceCode, qrExpiresAt)"));
        assertTrue(pairing.contains("δεν επιτρέπεται απομακρυσμένος χειρισμός") ||
                pairing.contains("Δεν επιτρέπεται απομακρυσμένος χειρισμός"));
    }

    @Test public void sessionExpiryIsStillHardGateForReportExports() throws Exception {
        String loader = source("GELRemoteServiceReportLoader.java");
        String export = source("ServiceReportActivity.java");
        assertTrue(loader.contains("sessionValid(context, sessionId)"));
        assertTrue(export.contains("private boolean remoteReportAllowed()"));
        assertTrue(export.contains("private void startEntitledExport()"));
        assertTrue(export.contains("private void exportTxtToPdf()"));
        assertTrue(export.contains("private void exportHtmlPdf(String report)"));
    }
}
