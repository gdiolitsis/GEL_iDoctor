package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Guards the Activity-bound PrintManager path when GEL applies a locale wrapper. */
public final class ServiceReportHtmlPdfPrintContextContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ServiceReportActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ServiceReportActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void originalBaseContextIsCapturedBeforeLocaleWrapping() throws Exception {
        String t = source();
        int start = t.indexOf("protected void attachBaseContext(Context base)");
        int saved = t.indexOf("originalActivityPrintContext = base;", start);
        int delegated = t.indexOf("super.attachBaseContext(base);", start);
        assertTrue("ServiceReportActivity must retain original Android Activity context",
                start >= 0 && saved > start && delegated > saved);
    }

    @Test public void printManagerUsesOriginalActivityContextNotLocalizedContext() throws Exception {
        String t = source();
        int start = t.indexOf("private void exportHtmlPdf(String report)");
        int end = t.indexOf("private void cancelReportAuthorisationTimeout()", start);
        assertTrue("HTML PDF method boundaries", start >= 0 && end > start);
        String print = t.substring(start, end);
        assertTrue(print.contains("final Context printContext = originalActivityPrintContext;"));
        assertTrue(print.contains("printContext.getSystemService(Context.PRINT_SERVICE)"));
        assertFalse("A PrintManager from LocaleHelper's configured context cannot print",
                print.contains("(android.print.PrintManager) getSystemService(Context.PRINT_SERVICE)"));
        assertTrue(print.contains("view.createPrintDocumentAdapter(\"GEL_Service_Report\")"));
        assertTrue(print.contains("android.print.PrintAttributes.MediaSize.ISO_A4"));
        assertTrue(print.contains("pm.print(\"GEL_Service_Report\", adapter, attrs)"));
    }

    @Test public void exportStillHandlesWebViewFailureAndDoesNotClaimPdfSaved() throws Exception {
        String t = source();
        assertTrue(t.contains("webView.setWebViewClient(new WebViewClient()"));
        assertTrue(t.contains("htmlPrintHandler.postDelayed(htmlPrintTimeout, 20000L)"));
        assertTrue(t.contains("cleanupHtmlPrintWebView()"));
        assertTrue(t.contains("lockExportUI(false)"));
        assertTrue(t.contains("Επιλέξτε «Αποθήκευση ως PDF»"));
    }

    @Test public void returningFromPrintingOffersToShareOnlyAUserSelectedPdf() throws Exception {
        String t = source();
        int onResume = t.indexOf("protected void onResume()");
        int prompt = t.indexOf("showShareSavedHtmlPdf();", onResume);
        int print = t.indexOf("pm.print(\"GEL_Service_Report\", adapter, attrs)");
        int offer = t.indexOf("offerHtmlShareOnReturn = true;", print);
        assertTrue(onResume >= 0 && prompt > onResume && prompt < print && offer > print);
        assertTrue(t.contains("savedInstanceState.getBoolean(STATE_OFFER_HTML_SHARE)"));
        assertTrue(t.contains("new ActivityResultContracts.OpenDocument()"));
        assertTrue(t.contains("if (uri != null) sharePdf(uri);"));
        assertTrue(t.contains("savedReportPdfPicker.launch(new String[]{\"application/pdf\"})"));
        assertTrue(t.contains("ClipData.newRawUri(\"GEL Service Report\", uri)"));
    }

    @Test public void emptyLocalReportOffersFileSharingBeforeReportPurchase() throws Exception {
        String t = source();
        int start = t.indexOf("private void beginReportExport(boolean html)");
        int emptyOptions = t.indexOf("showEmptyReportOptionsDialog(true);", start);
        int purchase = t.indexOf("showPersonalisedReportPurchaseDialog();", start);
        assertTrue(start >= 0 && emptyOptions > start && emptyOptions < purchase);
        int dialog = t.indexOf("private void showEmptyReportOptionsDialog(boolean needsBlankFormSetup)");
        int share = t.indexOf("shareSaved.setOnClickListener", dialog);
        int release = t.indexOf("finishSingleReport(false);", share);
        int pick = t.indexOf("pickSavedReportPdf();", share);
        assertTrue(dialog >= 0 && share > dialog && release > share && pick > release);
    }
}
