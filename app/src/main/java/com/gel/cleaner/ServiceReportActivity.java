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

    new Thread(() -> {

        exportTxtToPdf();

        runOnUiThread(() -> lockExportUI(false));

    }).start();
});

btnHtml.setOnClickListener(v -> {

    lockExportUI(true);

    new Thread(() -> {

        GELServiceReportPdf.export(this);   // 🔥 ΜΟΝΟ ΑΥΤΟ

        runOnUiThread(() -> {
            lockExportUI(false);
            updatePreview();
        });

    }).start();
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

    runOnUiThread(() -> {
        Toast.makeText(this, "Nothing to export.", Toast.LENGTH_SHORT).show();
        updatePreview();
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

int marginX = 32;
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

// split γιατί βάζουμε extra \n
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
            
// ==================================================
// REPAIR SUMMARY
// ==================================================
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

// Lines
for (int i = 0; i < 4; i++) {
    canvas.drawLine(marginX, y, marginX + 450, y, textPaint);
    y += 22;
}

y += 10;

// Additional Notes
canvas.drawText("Additional Notes / Επιπλέον παρατηρήσεις", marginX, y, sectionTitle2);
y += 20;

for (int i = 0; i < 3; i++) {
    canvas.drawLine(marginX, y, marginX + 450, y, textPaint);
    y += 22;
}

y += 10;
            
// ==================================================
// SIGNATURE SECTION (TECH LEFT / CUSTOMER RIGHT)
// ==================================================
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

// -------- LEFT (TECHNICIAN) --------
canvas.drawText("Technician Name / Όνομα τεχνικού:", marginX, y, textPaint);
canvas.drawLine(marginX, y + 15, marginX + 250, y + 15, textPaint);

y += 30;

canvas.drawText("Signature / Υπογραφή:", marginX, y, textPaint);
canvas.drawLine(marginX, y + 15, marginX + 250, y + 15, textPaint);

// -------- RIGHT (CUSTOMER) --------
int yRight = y - 30; // ευθυγράμμιση με αρχή technician

canvas.drawText("Customer Name / Όνομα πελάτη:", rightX, yRight, textPaint);
canvas.drawLine(rightX, yRight + 15, rightX + 250, yRight + 15, textPaint);

yRight += 30;

canvas.drawText("Signature / Υπογραφή:", rightX, yRight, textPaint);
canvas.drawLine(rightX, yRight + 15, rightX + 250, yRight + 15, textPaint);

// -------- DATE (CENTER κάτω) --------
y += 50;

canvas.drawText("Date / Ημερομηνία:", marginX, y, textPaint);
canvas.drawLine(marginX + 140, y + 15, marginX + 280, y + 15, textPaint);

            drawPageFooter(canvas, pageNum);
            pdf.finishPage(page);

            File outDir = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS);

if (outDir == null) {
    outDir = getExternalFilesDir(null);
}

File out = new File(outDir, "GEL_Service_Report.pdf");

            FileOutputStream fos = new FileOutputStream(out);
            pdf.writeTo(fos);
            fos.close();
            pdf.close();

runOnUiThread(() -> {
    Toast.makeText(this, "PDF saved.", Toast.LENGTH_LONG).show();
    updatePreview();
});

} catch (Exception e) {

    runOnUiThread(() -> {
        Toast.makeText(this, "PDF ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
    });

    e.printStackTrace();
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

private int drawReportHeader(
        Canvas c,
        int x,
        int startY,
        Paint title,
        Paint subtitle,
        Paint text,
        boolean isFirstPage) {

    int y = startY;

    // LOGO
    if (gelLogo != null) {
        Bitmap scaled = Bitmap.createScaledBitmap(gelLogo, 52, 52, true);
        c.drawBitmap(scaled, x, y, null);
    }

    y += 70;

    // TITLE
    c.drawText("GEL Service Report / Αναφορά Service", x, y, title);
    y += 20;

    c.drawText("GDiolitsis Engine Lab (GEL) — Author & Developer",
            x, y, subtitle);
    y += 26;

    // BASIC INFO (μόνο πρώτη σελίδα)
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

        // ==========================================
        // DEVICE INTAKE CONDITION
        // ==========================================
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

            // OK
            c.drawRect(x, y - 9, x + boxSize, y + 1, boxPaint);
            c.drawText(" OK", x + boxSize + 4, y, text);

            // DAMAGED
            int dmgX = x + 80;
            c.drawRect(dmgX, y - 9, dmgX + boxSize, y + 1, boxPaint);
            c.drawText(" DAMAGED", dmgX + boxSize + 4, y, text);

            // label
            c.drawText("— " + s, x + 170, y, text);

            y += 16;
        }

        y += 24;

        // ==========================================
        // NOTES SECTION
        // ==========================================
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

// ==========================================================
// GEL PDF FORMAT ENGINE — FINAL
// ==========================================================

private String formatPdfLine(String line) {

    if (line == null) return "";

    line = line.trim();

    // SECTION (LAB / INFO)
    if (line.startsWith("i ")) {
        return "\n" + line + "\n";
    }

    // RESULT LINE
    if (line.startsWith("✔") || line.startsWith("⚠") || line.startsWith("✖")) {
        return "\n" + line;
    }

    // LABEL: VALUE alignment
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

}
