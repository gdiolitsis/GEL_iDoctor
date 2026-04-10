// GDiolitsis Engine Lab (GEL) — Author & Developer
// FINAL — HTML → PDF ENGINE (COLORED • MULTI-PAGE • STABLE)

package com.gel.cleaner;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class GELServiceReportPdf {

    private GELServiceReportPdf() {}

    // ============================================================
    // ENTRY (FIXED — MAIN THREAD SAFE)
    // ============================================================
    public static void export(Context ctx) {

        if (ctx == null) return;

        new Handler(Looper.getMainLooper()).post(() -> {

            String raw = GELServiceLog.getAll();

            if (raw == null || raw.trim().isEmpty()) {
                Toast.makeText(ctx, "No service data to export.", Toast.LENGTH_LONG).show();
                return;
            }

            String html = buildHtml(raw);

            WebView wv = new WebView(ctx);
            wv.setVisibility(View.GONE);
            wv.getSettings().setJavaScriptEnabled(false);

            wv.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {

                    view.postDelayed(() -> {
                        try {
                            createPdf(ctx, view);
                        } catch (Throwable t) {
                            Toast.makeText(ctx,
                                    "PDF error: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        } finally {
                            cleanup(view);
                        }
                    }, 200);
                }
            });

            wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        });
    }

    // ============================================================
    // HTML BUILDER
    // ============================================================
    private static String buildHtml(String raw) {

        StringBuilder body = new StringBuilder();
        String[] lines = raw.split("\\n");

        for (String line : lines) {

            String safe = line
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");

            if (line.startsWith("✔"))
                body.append("<div style='color:#00FF88;'>").append(safe).append("</div>");
            else if (line.startsWith("⚠"))
                body.append("<div style='color:#FFA500;'>").append(safe).append("</div>");
            else if (line.startsWith("✖"))
                body.append("<div style='color:#FF4444;'>").append(safe).append("</div>");
            else if (line.startsWith("ℹ"))
                body.append("<div style='color:#33AAFF;'>").append(safe).append("</div>");
            else if (line.startsWith("i "))
                body.append("<h2 style='color:#7FC8FF;'>").append(safe).append("</h2>");
            else
                body.append("<div>").append(safe).append("</div>");
        }

        return "<!DOCTYPE html><html><head>" +
                "<meta charset='utf-8'/>" +
                "<style>" +
                "body{background:#FFFFFF;color:#111;font-family:monospace;font-size:12px;margin:24px;}" +
                "h1{color:#FFD700;font-size:22px;}" +
                "</style></head><body>" +

                "<h1>Service Diagnostic Report</h1>" +
                "<div>GDiolitsis Engine Lab (GEL)</div>" +
                "<div>" +
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()) +
                "</div><hr>" +

                body +

                "<hr>Signature: ___________________________" +
                "</body></html>";
    }

    // ============================================================
    // PDF CORE
    // ============================================================
    private static void createPdf(Context ctx, WebView wv) throws Exception {

        final int W = 595;
        final int H = 842;

        wv.measure(
                View.MeasureSpec.makeMeasureSpec(W, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        wv.layout(0, 0, W, wv.getMeasuredHeight());

        int contentHeight = wv.getMeasuredHeight();

        PdfDocument pdf = new PdfDocument();

        int y = 0;
        int page = 1;

        while (y < contentHeight) {

            PdfDocument.Page p = pdf.startPage(
                    new PdfDocument.PageInfo.Builder(W, H, page).create()
            );

            Canvas c = p.getCanvas();

            c.save();
            c.translate(0, -y);
            wv.draw(c);
            c.restore();

            pdf.finishPage(p);

            y += H;
            page++;
        }

        String name = "GEL_Report_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) +
                ".pdf";

        save(ctx, name, pdf);

        pdf.close();

        Toast.makeText(ctx, "PDF saved: " + name, Toast.LENGTH_LONG).show();
    }

    // ============================================================
    // SAVE
    // ============================================================
    @Nullable
    private static Uri save(Context ctx, String fileName, PdfDocument pdf) throws Exception {

        OutputStream os = null;

        try {
            if (Build.VERSION.SDK_INT >= 29) {

                ContentValues cv = new ContentValues();
                cv.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                cv.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                cv.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                ContentResolver cr = ctx.getContentResolver();
                Uri uri = cr.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv);

                os = cr.openOutputStream(uri);
                pdf.writeTo(os);

                return uri;

            } else {

                File dir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);

                if (!dir.exists()) dir.mkdirs();

                File file = new File(dir, fileName);

                os = new FileOutputStream(file);
                pdf.writeTo(os);
            }

        } finally {
            if (os != null) os.close();
        }

        return null;
    }

    // ============================================================
    // CLEANUP
    // ============================================================
    private static void cleanup(WebView v) {
        try {
            v.destroy();
        } catch (Throwable ignore) {}
    }
}
