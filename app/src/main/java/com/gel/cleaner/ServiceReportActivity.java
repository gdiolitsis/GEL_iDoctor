// GDiolitsis Engine Lab (GEL) — Author & Developer
// ServiceReportActivity — FINAL (TXT + HTML + FIXED PDF ENGINE)

package com.gel.cleaner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.gel.cleaner.UIHelpers;

import java.io.File;
import java.io.FileOutputStream;

public class ServiceReportActivity extends AppCompatActivity {

    private static final int PAGE_WIDTH  = 595;
    private static final int PAGE_HEIGHT = 842;

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

        // ==========================================================
        // BUTTON ROW
        // ==========================================================
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.VERTICAL);

        LinearLayout line = new LinearLayout(this);
        line.setOrientation(LinearLayout.HORIZONTAL);

        // TXT BUTTON
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

        // HTML BUTTON
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

        // PROGRESS
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

        // ACTIONS
        btnTxt.setOnClickListener(v -> {
            lockExportUI(true);
            exportTxtToPdf();
            lockExportUI(false);
        });

        btnHtml.setOnClickListener(v -> {
            lockExportUI(true);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                GELServiceReportPdf.export(this);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    lockExportUI(false);
                }, 1200);

            }, 100);
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
    // TXT PDF ENGINE (FIXED)
    // ==========================================================
    private void exportTxtToPdf() {

        if (GELServiceLog.isEmpty()) {
            Toast.makeText(this, "Nothing to export.", Toast.LENGTH_SHORT).show();
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

            int marginX = 32;
            int y = 80;
            int lineHeight = 18;
            int pageNum = 1;

            PdfDocument.Page page = startPage(pdf, pageNum);
            Canvas canvas = page.getCanvas();
            canvas.drawColor(Color.WHITE);

            for (String line : lines) {

                String cleanLine = (line == null) ? "" : line.trim();

                if (cleanLine.isEmpty()) {
                    y += lineHeight / 2;
                    continue;
                }

                if (y > PAGE_HEIGHT - 60) {
                    pdf.finishPage(page);
                    page = startPage(pdf, ++pageNum);
                    canvas = page.getCanvas();
                    canvas.drawColor(Color.WHITE);
                    y = 80;
                }

                drawLineWithColoredEmoji(canvas, cleanLine, marginX, y, textPaint, emojiPaint);
                y += lineHeight;
            }

            pdf.finishPage(page);

            File out = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS),
                    "GEL_Service_Report.pdf"
            );

            FileOutputStream fos = new FileOutputStream(out);
            pdf.writeTo(fos);
            fos.close();
            pdf.close();

            Toast.makeText(this, "PDF saved.", Toast.LENGTH_LONG).show();
            
        } catch (Throwable t) {
            Toast.makeText(this, "PDF error: " + t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private PdfDocument.Page startPage(PdfDocument pdf, int num) {
        return pdf.startPage(
                new PdfDocument.PageInfo.Builder(
                        PAGE_WIDTH, PAGE_HEIGHT, num).create()
        );
    }

    private void drawLineWithColoredEmoji(
            Canvas canvas,
            String line,
            int x,
            int y,
            Paint textPaint,
            Paint emojiPaint) {

        String emoji = null;
        String rest = line;

        if (line.startsWith("ℹ")) { emoji = "ℹ"; emojiPaint.setColor(0xFF1E90FF); }
        else if (line.startsWith("✔")) { emoji = "✔"; emojiPaint.setColor(0xFF00AA00); }
        else if (line.startsWith("⚠")) { emoji = "⚠"; emojiPaint.setColor(0xFFFFA500); }
        else if (line.startsWith("✖")) { emoji = "✖"; emojiPaint.setColor(0xFFCC0000); }

        int dx = x;

        if (emoji != null) {
            canvas.drawText(emoji, dx, y, emojiPaint);
            dx += 18;
            rest = line.substring(1).trim();
        }

        canvas.drawText(rest, dx, y, textPaint);
    }
}

private void updatePreview() {
    if (txtPreview == null) return;

    String data = GELServiceLog.getAll();

    if (data == null || data.trim().isEmpty()) {
        txtPreview.setText("No logs available.");
        return;
    }

    txtPreview.setText(
            data.replaceAll("\\n{3,}", "\n\n")
    );
}
