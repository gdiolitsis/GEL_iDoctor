// GDiolitsis Engine Lab (GEL) — Author & Developer
// ServiceReportActivity — FINAL STABLE (TXT PDF + HTML PDF + MULTI-PAGE + COLORED)

package com.gel.cleaner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.File;
import java.io.FileOutputStream;

public class ServiceReportActivity extends AppCompatActivity {

    private static final int PAGE_WIDTH = 595;
    private static final int PAGE_HEIGHT = 842;
    private static final int PAGE_MARGIN = 32;
    private static final int FOOTER_SPACE = 36;

    private TextView txtPreview;
    private Bitmap gelLogo;

    private AppCompatButton btnTxt;
    private AppCompatButton btnHtml;
    private ProgressBar exportProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gelLogo = BitmapFactory.decodeResource(getResources(), R.drawable.gel_logo);

        ScrollView scroll = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);
        root.setBackgroundColor(0xFF101010);

        txtPreview = new TextView(this);
        txtPreview.setTextColor(0xFFFFFFFF);
        txtPreview.setTextSize(13f);
        updatePreview();
        root.addView(txtPreview);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.VERTICAL);

        LinearLayout line = new LinearLayout(this);
        line.setOrientation(LinearLayout.HORIZONTAL);

        btnTxt = new AppCompatButton(this);
        btnTxt.setText("TXT PDF");
        btnTxt.setAllCaps(false);
        btnTxt.setTextColor(0xFFFFFFFF);
        btnTxt.setBackgroundResource(R.drawable.gel_btn_outline);

        LinearLayout.LayoutParams lpTxt =
                new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        lpTxt.setMargins(0, 0, 8, 0);
        btnTxt.setLayoutParams(lpTxt);
        btnTxt.setPadding(0, 28, 0, 28);

        btnHtml = new AppCompatButton(this);
        btnHtml.setText("HTML PDF");
        btnHtml.setAllCaps(false);
        btnHtml.setTextColor(0xFFFFFFFF);
        btnHtml.setBackgroundResource(R.drawable.gel_btn_outline);

        LinearLayout.LayoutParams lpHtml =
                new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        lpHtml.setMargins(8, 0, 0, 0);
        btnHtml.setLayoutParams(lpHtml);
        btnHtml.setPadding(0, 28, 0, 28);

        line.addView(btnTxt);
        line.addView(btnHtml);

        exportProgress = new ProgressBar(this);
        exportProgress.setIndeterminate(true);
        exportProgress.setVisibility(View.GONE);

        LinearLayout.LayoutParams lpProg =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        lpProg.topMargin = 20;
        lpProg.gravity = Gravity.CENTER;
        exportProgress.setLayoutParams(lpProg);

        btnTxt.setOnClickListener(v -> {
            String report = validateExport();
            if (report == null) return;

            if (GELServiceLog.isEmpty()) {
                Toast.makeText(this,
                        "Nothing to export. Run a lab first.",
                        Toast.LENGTH_SHORT
                ).show();
                updatePreview();
                lockExportUI(false);
                return;
            }

            lockExportUI(true);
            exportTxtToPdf();
        });

        btnHtml.setOnClickListener(v -> {
            String report = validateExport();
            if (report == null) return;

            if (GELServiceLog.isEmpty()) {
                Toast.makeText(this,
                        "Nothing to export. Run a lab first.",
                        Toast.LENGTH_LONG
                ).show();
                lockExportUI(false);
                return;
            }

            lockExportUI(true);
            exportHtmlPdf(report);
        });

        btnRow.addView(line);
        btnRow.addView(exportProgress);

        root.addView(btnRow);

        scroll.addView(root);
        setContentView(scroll);

        UIHelpers.applyPressEffectRecursive(getWindow().getDecorView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePreview();
    }

    private void lockExportUI(boolean lock) {
        if (btnTxt != null) {
            btnTxt.setEnabled(!lock);
            btnTxt.setAlpha(lock ? 0.5f : 1f);
        }

        if (btnHtml != null) {
            btnHtml.setEnabled(!lock);
            btnHtml.setAlpha(lock ? 0.5f : 1f);
        }

        if (exportProgress != null) {
            exportProgress.setVisibility(lock ? View.VISIBLE : View.GONE);
        }
    }

    // ==========================================================
    // TXT PDF ENGINE
    // ==========================================================
    private void exportTxtToPdf() {

        if (GELServiceLog.isEmpty()) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Nothing to export.", Toast.LENGTH_SHORT).show();
                updatePreview();
                lockExportUI(false);
            });
            return;
        }

        try {
            String text = GELServiceLog.getAll();
            String[] lines = text.split("\\n+");

            PdfDocument pdf = new PdfDocument();

            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(12f);
            textPaint.setColor(Color.BLACK);

            Paint emojiPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            emojiPaint.setTextSize(12f);

            int marginX = PAGE_MARGIN;
            int lineHeight = 18;
            int pageNum = 1;

            PdfDocument.Page page = startPage(pdf, pageNum);
            Canvas canvas = page.getCanvas();
            canvas.drawColor(Color.WHITE);

            Paint titlePaint = new Paint(textPaint);
            titlePaint.setTextSize(14f);
            titlePaint.setFakeBoldText(true);

            Paint subtitlePaint = new Paint(textPaint);
            subtitlePaint.setTextSize(11f);

            int y = drawReportHeader(
                    canvas,
                    marginX,
                    40,
                    titlePaint,
                    subtitlePaint,
                    textPaint,
                    true
            );

            for (String line : lines) {
                String cleanLine = (line == null) ? "" : line.trim();

                if (cleanLine.isEmpty()) {
                    y += lineHeight / 2;
                    continue;
                }

                if (y > PAGE_HEIGHT - 60) {
                    drawPageFooter(canvas, pageNum);
                    pdf.finishPage(page);
                    page = startPage(pdf, ++pageNum);
                    canvas = page.getCanvas();
                    canvas.drawColor(Color.WHITE);
                    y = drawReportHeader(
                            canvas,
                            marginX,
                            40,
                            titlePaint,
                            subtitlePaint,
                            textPaint,
                            false
                    );
                }

                String formatted = formatPdfLine(cleanLine);
                String[] subLines = formatted.split("\\n");

                for (String sub : subLines) {
                    if (sub.trim().isEmpty()) {
                        y += lineHeight / 2;
                        continue;
                    }

                    if (y > PAGE_HEIGHT - 60) {
                        drawPageFooter(canvas, pageNum);
                        pdf.finishPage(page);

                        page = startPage(pdf, ++pageNum);
                        canvas = page.getCanvas();
                        canvas.drawColor(Color.WHITE);

                        y = drawReportHeader(
                                canvas,
                                marginX,
                                40,
                                titlePaint,
                                subtitlePaint,
                                textPaint,
                                false
                        );
                    }

                    drawLineWithColoredEmoji(canvas, sub, marginX, y, textPaint, emojiPaint);
                    y += lineHeight;
                }
            }

            if (y > PAGE_HEIGHT - 180) {
                drawPageFooter(canvas, pageNum);
                pdf.finishPage(page);

                page = startPage(pdf, ++pageNum);
                canvas = page.getCanvas();
                canvas.drawColor(Color.WHITE);

                y = drawReportHeader(
                        canvas,
                        marginX,
                        40,
                        titlePaint,
                        subtitlePaint,
                        textPaint,
                        false
                );
            }

            Paint sectionTitle2 = new Paint(textPaint);
            sectionTitle2.setFakeBoldText(true);

            y += 30;

            canvas.drawText("Repair Summary / Τι επισκευάστηκε", marginX, y, sectionTitle2);
            y += 20;

            for (int i = 0; i < 4; i++) {
                canvas.drawLine(marginX, y, marginX + 450, y, textPaint);
                y += 22;
            }

            y += 10;

            canvas.drawText("Additional Notes / Επιπλέον παρατηρήσεις", marginX, y, sectionTitle2);
            y += 20;

            for (int i = 0; i < 3; i++) {
                canvas.drawLine(marginX, y, marginX + 450, y, textPaint);
                y += 22;
            }

            y += 10;

            if (y > PAGE_HEIGHT - 160) {
                drawPageFooter(canvas, pageNum);
                pdf.finishPage(page);

                page = startPage(pdf, ++pageNum);
                canvas = page.getCanvas();
                canvas.drawColor(Color.WHITE);

                y = drawReportHeader(
                        canvas,
                        marginX,
                        40,
                        titlePaint,
                        subtitlePaint,
                        textPaint,
                        false
                );
            }

            y += 40;

            int rightX = marginX + 300;

            canvas.drawText("Technician Name / Όνομα τεχνικού:", marginX, y, textPaint);
            canvas.drawLine(marginX, y + 15, marginX + 250, y + 15, textPaint);

            y += 30;

            canvas.drawText("Signature / Υπογραφή:", marginX, y, textPaint);
            canvas.drawLine(marginX, y + 15, marginX + 250, y + 15, textPaint);

            int yRight = y - 30;

            canvas.drawText("Customer Name / Όνομα πελάτη:", rightX, yRight, textPaint);
            canvas.drawLine(rightX, yRight + 15, rightX + 250, yRight + 15, textPaint);

            yRight += 30;

            canvas.drawText("Signature / Υπογραφή:", rightX, yRight, textPaint);
            canvas.drawLine(rightX, yRight + 15, rightX + 250, yRight + 15, textPaint);

            y += 50;

            canvas.drawText("Date / Ημερομηνία:", marginX, y, textPaint);
            canvas.drawLine(marginX + 140, y + 15, marginX + 280, y + 15, textPaint);

            drawPageFooter(canvas, pageNum);
            pdf.finishPage(page);

            File out = getOutputFile("GEL_Service_Report.pdf");
            FileOutputStream fos = new FileOutputStream(out);
            pdf.writeTo(fos);
            fos.close();
            pdf.close();

            runOnUiThread(() -> onExportSuccess("PDF saved."));
            GELServiceLog.clear();
        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(this, "PDF ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                lockExportUI(false);
            });
            e.printStackTrace();
        }
    }

    // ==========================================================
    // HTML PDF ENGINE — MULTI PAGE + COLORED
    // ==========================================================
    private void exportHtmlPdf(String report) {

        if (report == null || report.trim().isEmpty()) {
            Toast.makeText(this, "Nothing to export.", Toast.LENGTH_SHORT).show();
            lockExportUI(false);
            return;
        }

        try {
            final WebView webView = new WebView(this);
            webView.setBackgroundColor(Color.WHITE);
            webView.getSettings().setJavaScriptEnabled(false);
            webView.getSettings().setAllowFileAccess(false);
            webView.getSettings().setDomStorageEnabled(false);

            int contentWidth = PAGE_WIDTH - (PAGE_MARGIN * 2);
            webView.layout(0, 0, contentWidth, PAGE_HEIGHT);

            String html = buildHtmlReport(report);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    view.postDelayed(() -> {
                        try {
                            int contentHeightPx = (int) Math.ceil(view.getContentHeight() * view.getScale());
                            if (contentHeightPx <= 0) {
                                throw new IllegalStateException("HTML content height is zero");
                            }

                            PdfDocument pdf = new PdfDocument();

                            int usablePageHeight = PAGE_HEIGHT - FOOTER_SPACE;
                            int currentTop = 0;
                            int pageNum = 1;

                            while (currentTop < contentHeightPx) {
                                PdfDocument.Page page = pdf.startPage(
                                        new PdfDocument.PageInfo.Builder(
                                                PAGE_WIDTH,
                                                PAGE_HEIGHT,
                                                pageNum
                                        ).create()
                                );

                                Canvas canvas = page.getCanvas();
                                canvas.drawColor(Color.WHITE);

                                canvas.save();
                                canvas.translate(PAGE_MARGIN, -currentTop);
                                view.draw(canvas);
                                canvas.restore();

                                drawPageFooter(canvas, pageNum);

                                pdf.finishPage(page);

                                currentTop += usablePageHeight;
                                pageNum++;
                            }

                            File out = getOutputFile("GEL_Service_Report_HTML.pdf");
                            FileOutputStream fos = new FileOutputStream(out);
                            pdf.writeTo(fos);
                            fos.close();
                            pdf.close();

                            runOnUiThread(() -> onExportSuccess("HTML PDF saved."));
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(ServiceReportActivity.this,
                                        "HTML PDF ERROR: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                                lockExportUI(false);
                            });
                            e.printStackTrace();
                        } finally {
                            try {
                                webView.destroy();
                            } catch (Throwable ignore) {}
                        }
                    }, 500);
                }
            });

            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);

        } catch (Exception e) {
            Toast.makeText(this,
                    "HTML ENGINE ERROR: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            lockExportUI(false);
            e.printStackTrace();
        }
    }

    private String buildHtmlReport(String report) {
        String[] lines = report.split("\\n");

        StringBuilder body = new StringBuilder();

        body.append("<div class='header'>");
        body.append("<div class='title'>GEL Service Report / Αναφορά Service</div>");
        body.append("<div class='subtitle'>GDiolitsis Engine Lab (GEL) — Author & Developer</div>");
        body.append("<div class='meta'>Date / Ημερομηνία: ")
                .append(escapeHtml(java.text.DateFormat.getDateTimeInstance().format(new java.util.Date())))
                .append("</div>");
        body.append("<div class='meta'>Device / Συσκευή: ")
                .append(escapeHtml(android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL))
                .append("</div>");
        body.append("<div class='meta'>Android / Έκδοση: ")
                .append(escapeHtml(android.os.Build.VERSION.RELEASE + " (API " + android.os.Build.VERSION.SDK_INT + ")"))
                .append("</div>");
        body.append("</div>");

        for (String raw : lines) {
            String line = raw == null ? "" : raw.trim();
            if (line.isEmpty()) {
                body.append("<div class='space'></div>");
                continue;
            }

            String escaped = escapeHtml(line);

            if (line.startsWith("✔")) {
                body.append("<div class='ok'>").append(escaped).append("</div>");
            } else if (line.startsWith("⚠")) {
                body.append("<div class='warn'>").append(escaped).append("</div>");
            } else if (line.startsWith("✖")) {
                body.append("<div class='err'>").append(escaped).append("</div>");
            } else if (line.startsWith("ℹ") || line.startsWith("i ")) {
                body.append("<div class='info'>").append(escaped).append("</div>");
            } else if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                String label = escapeHtml(parts[0].trim());
                String value = parts.length > 1 ? escapeHtml(parts[1].trim()) : "";
                body.append("<div class='row'><span class='label'>")
                        .append(label)
                        .append(":</span> <span class='value'>")
                        .append(value)
                        .append("</span></div>");
            } else {
                body.append("<div class='plain'>").append(escaped).append("</div>");
            }
        }

        body.append("<div class='section-title'>Repair Summary / Τι επισκευάστηκε</div>");
        body.append("<div class='line'></div><div class='line'></div><div class='line'></div><div class='line'></div>");

        body.append("<div class='section-title'>Additional Notes / Επιπλέον παρατηρήσεις</div>");
        body.append("<div class='line'></div><div class='line'></div><div class='line'></div>");

        body.append("<div class='signatures'>");
        body.append("<div class='sig-block'><div class='sig-label'>Technician Name / Όνομα τεχνικού:</div><div class='sig-line'></div></div>");
        body.append("<div class='sig-block'><div class='sig-label'>Customer Name / Όνομα πελάτη:</div><div class='sig-line'></div></div>");
        body.append("<div class='sig-block'><div class='sig-label'>Signature / Υπογραφή:</div><div class='sig-line'></div></div>");
        body.append("<div class='sig-block'><div class='sig-label'>Signature / Υπογραφή:</div><div class='sig-line'></div></div>");
        body.append("<div class='sig-block-full'><div class='sig-label'>Date / Ημερομηνία:</div><div class='sig-line'></div></div>");
        body.append("</div>");

        return "<html><head><meta charset='utf-8'/>"
                + "<style>"
                + "body{font-family:monospace;background:#ffffff;color:#111111;padding:24px;margin:0;font-size:13px;line-height:1.45;}"
                + ".header{margin-bottom:18px;padding-bottom:12px;border-bottom:2px solid #d6d6d6;}"
                + ".title{font-size:20px;font-weight:700;color:#000000;margin-bottom:6px;}"
                + ".subtitle{font-size:12px;color:#444444;margin-bottom:10px;}"
                + ".meta{font-size:12px;color:#222222;margin-bottom:4px;}"
                + ".ok{color:#0b8f2d;font-weight:700;margin:6px 0;}"
                + ".warn{color:#d98200;font-weight:700;margin:6px 0;}"
                + ".err{color:#b00020;font-weight:700;margin:6px 0;}"
                + ".info{color:#1565c0;font-weight:700;margin:6px 0;}"
                + ".plain{color:#111111;margin:4px 0;white-space:pre-wrap;word-wrap:break-word;}"
                + ".row{margin:4px 0;white-space:pre-wrap;word-wrap:break-word;}"
                + ".label{font-weight:700;color:#000000;}"
                + ".value{color:#222222;}"
                + ".space{height:10px;}"
                + ".section-title{margin-top:22px;margin-bottom:10px;font-weight:700;font-size:15px;color:#000000;}"
                + ".line{height:22px;border-bottom:1px solid #333333;}"
                + ".signatures{margin-top:26px;}"
                + ".sig-block{display:inline-block;width:48%;vertical-align:top;margin-bottom:20px;}"
                + ".sig-block-full{width:100%;margin-top:8px;}"
                + ".sig-label{font-weight:700;color:#000000;margin-bottom:10px;}"
                + ".sig-line{height:18px;border-bottom:1px solid #333333;}"
                + "</style></head><body>"
                + body
                + "</body></html>";
    }

    private void onExportSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        lockExportUI(false);
        updatePreview();
        finish();
    }

    private File getOutputFile(String name) {
        File outDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
        );

        if (outDir == null) {
            outDir = getExternalFilesDir(null);
        }

        return new File(outDir, name);
    }

    private PdfDocument.Page startPage(PdfDocument pdf, int num) {
        return pdf.startPage(
                new PdfDocument.PageInfo.Builder(
                        PAGE_WIDTH, PAGE_HEIGHT, num
                ).create()
        );
    }

    private void drawLineWithColoredEmoji(
            Canvas canvas,
            String line,
            int x,
            int y,
            Paint textPaint,
            Paint emojiPaint
    ) {
        String emoji = null;
        String rest = line;

        if (line.startsWith("ℹ")) {
            emoji = "ℹ";
            emojiPaint.setColor(0xFF1E90FF);
        } else if (line.startsWith("✔")) {
            emoji = "✔";
            emojiPaint.setColor(0xFF00AA00);
        } else if (line.startsWith("⚠")) {
            emoji = "⚠";
            emojiPaint.setColor(0xFFFFA500);
        } else if (line.startsWith("✖")) {
            emoji = "✖";
            emojiPaint.setColor(0xFFCC0000);
        }

        int dx = x;

        if (emoji != null) {
            canvas.drawText(emoji, dx, y, emojiPaint);
            dx += 18;
            rest = line.substring(1).trim();
        }

        canvas.drawText(rest, dx, y, textPaint);
    }

    private void updatePreview() {
        if (txtPreview == null) return;

        String data = GELServiceLog.getAll();

        if (data == null || data.trim().isEmpty()) {
            txtPreview.setText("No logs available.");
            return;
        }

        txtPreview.setText(data.replaceAll("\\n{3,}", "\n\n"));
    }

    private int drawReportHeader(
            Canvas c,
            int x,
            int startY,
            Paint title,
            Paint subtitle,
            Paint text,
            boolean isFirstPage
    ) {
        int y = startY;

        if (gelLogo != null) {
            Bitmap scaled = Bitmap.createScaledBitmap(gelLogo, 52, 52, true);
            c.drawBitmap(scaled, x, y, null);
        }

        y += 70;

        c.drawText("GEL Service Report / Αναφορά Service", x, y, title);
        y += 20;

        c.drawText("GDiolitsis Engine Lab (GEL) — Author & Developer", x, y, subtitle);
        y += 26;

        if (isFirstPage) {
            String dateLine = "Date / Ημερομηνία: " +
                    java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

            String deviceLine = "Device / Συσκευή: " +
                    android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;

            String osLine = "Android / Έκδοση: " +
                    android.os.Build.VERSION.RELEASE +
                    " (API " + android.os.Build.VERSION.SDK_INT + ")";

            c.drawText(dateLine, x, y, text);
            y += 16;

            c.drawText(deviceLine, x, y, text);
            y += 16;

            c.drawText(osLine, x, y, text);
            y += 22;

            Paint sectionTitle = new Paint(text);
            sectionTitle.setFakeBoldText(true);

            c.drawText("Device intake condition / Κατάσταση παραλαβής συσκευής", x, y, sectionTitle);
            y += 18;

            String[] intakeLines = new String[]{
                    "Screen (cracked/scratched) / Οθόνη (σπασμένη/γρατζουνισμένη)",
                    "Back cover / Πίσω καπάκι",
                    "Frame / Πλαίσιο",
                    "Camera lens / Φακός κάμερας",
                    "Charging port / Θύρα φόρτισης",
                    "Buttons / Κουμπιά",
                    "Speaker / Microphone / Ηχείο / Μικρόφωνο",
                    "Water signs / Ενδείξεις υγρασίας",
                    "Battery condition / Κατάσταση μπαταρίας"
            };

            Paint boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            boxPaint.setStyle(Paint.Style.STROKE);
            boxPaint.setStrokeWidth(1.5f);

            for (String s : intakeLines) {
                int boxSize = 10;

                c.drawRect(x, y - 9, x + boxSize, y + 1, boxPaint);
                c.drawText(" OK", x + boxSize + 4, y, text);

                int dmgX = x + 80;
                c.drawRect(dmgX, y - 9, dmgX + boxSize, y + 1, boxPaint);
                c.drawText(" DAMAGED", dmgX + boxSize + 4, y, text);

                c.drawText("— " + s, x + 170, y, text);
                y += 16;
            }

            y += 24;

            c.drawText("Notes / Παρατηρήσεις:", x, y, sectionTitle);
            y += 20;

            for (int i = 0; i < 3; i++) {
                c.drawLine(x, y, x + 450, y, text);
                y += 20;
            }

            y += 10;
        }

        return y;
    }

    private void drawPageFooter(Canvas canvas, int pageNum) {
        Paint footerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        footerPaint.setTextSize(10f);
        footerPaint.setColor(Color.DKGRAY);
        footerPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(
                "GEL Service Report — Page " + pageNum,
                PAGE_WIDTH / 2f,
                PAGE_HEIGHT - 20,
                footerPaint
        );
    }

    private String formatPdfLine(String line) {
        if (line == null) return "";

        line = line.trim();

        if (line.startsWith("i ")) {
            return "\n" + line + "\n";
        }

        if (line.startsWith("✔") || line.startsWith("⚠") || line.startsWith("✖")) {
            return "\n" + line;
        }

        if (line.contains(":")) {
            String[] parts = line.split(":", 2);
            String label = parts[0].trim();
            String value = parts.length > 1 ? parts[1].trim() : "";
            return padRight(label, 28) + ": " + value;
        }

        return line;
    }

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    private String validateExport() {
        String report = txtPreview.getText().toString();

        if (report == null
                || report.trim().isEmpty()
                || report.contains("No logs available")) {

            if (exportProgress != null) {
                exportProgress.setVisibility(View.GONE);
            }

            Toast.makeText(this,
                    "Nothing to export. Run a lab first.",
                    Toast.LENGTH_LONG
            ).show();

            return null;
        }

        return report;
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
