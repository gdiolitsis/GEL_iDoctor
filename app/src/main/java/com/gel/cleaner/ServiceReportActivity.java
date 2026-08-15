// GDiolitsis Engine Lab (GEL) — Author & Developer
// ServiceReportActivity — FINAL STABLE (TXT PDF + HTML PDF + MULTI-PAGE + COLORED)

package com.gel.cleaner;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
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

    // STEP 2 — billing/report entitlement bridge.
    private GelBillingManager billingManager;
    private boolean pendingSingleReportExport = false;
    private boolean useSingleReportCreditForCurrentExport = false;

    // True only when this specific export is allowed to use the saved
    // professional logo/company/technician profile.
    private boolean useProfessionalBrandingForCurrentExport = false;

    // GEL PRO — same entitlement store used by the professional labs.
    private static final String GEL_PRO_PREFS = "GEL_PRO_ENTITLEMENT";
    private static final String GEL_PRO_ACTIVE_KEY = "active";

    // Professional report personalization — STEP 1 (local profile only)
    private static final String PRO_PROFILE_PREFS = "GEL_PRO_PROFILE";
    private static final String KEY_LOGO_URI = "logo_uri";
    private static final int REQ_PICK_PRO_LOGO = 5201;

    private static final String KEY_BUSINESS_NAME = "business_name";
    private static final String KEY_TECHNICIAN_NAME = "technician_name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_CITY = "city";
    private static final String KEY_POSTAL_CODE = "postal_code";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_WEBSITE = "website";
    private static final String KEY_VAT = "vat_number";
    private static final String KEY_TAX_OFFICE = "tax_office";

    private ImageView proLogoPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gelLogo = BitmapFactory.decodeResource(getResources(), R.drawable.gel_logo);

        billingManager = new GelBillingManager(this, new GelBillingManager.Listener() {
            @Override public void onBillingReady() {}

            @Override public void onGelProActivated(boolean showCustomReportsOffer) {
                runOnUiThread(() -> recreate());
            }

            @Override public void onCustomReportsActivated() {
                runOnUiThread(() -> {
                    Toast.makeText(ServiceReportActivity.this,
                            AppLang.isGreek(ServiceReportActivity.this)
                                    ? "Η απεριόριστη εξατομίκευση αναφορών ενεργοποιήθηκε."
                                    : "Unlimited personalised reports activated.",
                            Toast.LENGTH_LONG).show();
                    recreate();
                });
            }

            @Override public void onSingleReportCreditAdded(int availableCredits) {
                runOnUiThread(() -> {
                    Toast.makeText(ServiceReportActivity.this,
                            AppLang.isGreek(ServiceReportActivity.this)
                                    ? "Η αγορά ολοκληρώθηκε. Διαθέσιμη 1 εξατομικευμένη αναφορά."
                                    : "Purchase complete. One personalised report is available.",
                            Toast.LENGTH_LONG).show();
                    if (pendingSingleReportExport) {
                        pendingSingleReportExport = false;
                        startEntitledExport();
                    }
                });
            }

            @Override public void onPurchasePending(@androidx.annotation.NonNull String productId) {
                runOnUiThread(() -> Toast.makeText(ServiceReportActivity.this,
                        AppLang.isGreek(ServiceReportActivity.this)
                                ? "Η πληρωμή είναι σε αναμονή."
                                : "Payment is pending.",
                        Toast.LENGTH_LONG).show());
            }

            @Override public void onPurchaseCancelled() {
                pendingSingleReportExport = false;
            }

            @Override public void onBillingError(@androidx.annotation.NonNull String message) {
                runOnUiThread(() -> Toast.makeText(ServiceReportActivity.this,
                        message, Toast.LENGTH_LONG).show());
            }
        });
        billingManager.start();

        ScrollView scroll = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);
        root.setBackgroundColor(0xFF101010);

        // FREE PREVIEW — the user can see what the professional report looks like.
        root.addView(buildServiceReportPreview());

        // Visible only while GEL PRO entitlement is active.
        if (isGelProActive()) {
            root.addView(buildProfessionalProfileSection());
        }

        txtPreview = new TextView(this);
        txtPreview.setTextColor(0xFFFFFFFF);
        txtPreview.setTextSize(13f);
        txtPreview.setPadding(dp(14), dp(8), dp(14), dp(18));
        updatePreview();
        root.addView(txtPreview);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.VERTICAL);

        LinearLayout line = new LinearLayout(this);
        line.setOrientation(LinearLayout.HORIZONTAL);

btnTxt = new AppCompatButton(this);
btnHtml = new AppCompatButton(this);

// 🔴 TEXT (AFTER BOTH BUTTONS CREATED)
if (AppLang.isGreek(this)) {
    btnTxt.setText("🔒 PRO  TXT PDF (Αποθήκευση & Κοινοποίηση)");
    btnHtml.setText("🔒 PRO  HTML PDF (Αποθήκευση)");
} else {
    btnTxt.setText("🔒 PRO  TXT PDF (Save & Share)");
    btnHtml.setText("🔒 PRO  HTML PDF (Save)");
}

// 🔴 TXT BUTTON STYLE
btnTxt.setAllCaps(false);
btnTxt.setTextColor(0xFFFFFFFF);
btnTxt.setBackgroundResource(R.drawable.gel_btn_outline);

LinearLayout.LayoutParams lpTxt =
        new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
lpTxt.setMargins(0, 0, 8, 0);
btnTxt.setLayoutParams(lpTxt);
btnTxt.setPadding(0, 28, 0, 28);

// 🔴 HTML BUTTON STYLE
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

// 🔴 TXT ACTION
btnTxt.setOnClickListener(v -> beginReportExport(false));

        btnHtml.setOnClickListener(v -> beginReportExport(true));

        btnRow.addView(line);
        btnRow.addView(exportProgress);

        root.addView(btnRow);

        scroll.addView(root);
        setContentView(scroll);

        UIHelpers.applyPressEffectRecursive(getWindow().getDecorView());
    }


    // ============================================================
    // STEP 2 — REPORT BILLING / ENTITLEMENT GATE
    // ============================================================
    private boolean pendingHtmlExport = false;

    private void beginReportExport(boolean html) {
        pendingHtmlExport = html;
        useSingleReportCreditForCurrentExport = false;
        useProfessionalBrandingForCurrentExport = false;

        final boolean gelProActive =
                billingManager != null
                        ? billingManager.isGelProActive()
                        : isGelProActive();

        final boolean unlimitedPersonalisation =
                gelProActive
                        && billingManager != null
                        && billingManager.isCustomReportsActive();

        final boolean singleReportCredit =
                billingManager != null
                        && billingManager.hasSingleReportCredit();

        // --------------------------------------------------------
        // LEVEL 1 — EXPORT ACCESS
        // Service Report export is never a free feature.
        //
        // Access is granted by:
        //   • active GEL PRO, OR
        //   • one already-purchased €5 single personalised report credit.
        // --------------------------------------------------------
        if (!gelProActive && !singleReportCredit) {
            showPersonalisedReportPurchaseDialog();
            return;
        }

        // --------------------------------------------------------
        // LEVEL 2 — PERSONALISATION ACCESS
        // GEL PRO by itself gives the normal GEL Service Report.
        // Saved company/logo details are used only with:
        //   • GEL PRO + €29.99 unlimited personalisation, OR
        //   • one €5 single-report credit.
        // --------------------------------------------------------
        if (unlimitedPersonalisation) {
            useProfessionalBrandingForCurrentExport = true;
        } else if (singleReportCredit) {
            useSingleReportCreditForCurrentExport = true;
            useProfessionalBrandingForCurrentExport = true;
        }

        // The blank report is also an export, therefore it reaches this
        // point only after the paid export gate above has been passed.
        if (GELServiceLog.isEmpty()) {
            showEmptyReportOptionsDialog();
            return;
        }

        startEntitledExport();
    }

    private void startEntitledExport() {
        final boolean gelProActive =
                billingManager != null
                        ? billingManager.isGelProActive()
                        : isGelProActive();

        final boolean unlimitedPersonalisation =
                gelProActive
                        && billingManager != null
                        && billingManager.isCustomReportsActive();

        final boolean singleReportCredit =
                billingManager != null
                        && billingManager.hasSingleReportCredit();

        // Re-check the paid export entitlement immediately before export.
        if (!gelProActive && !singleReportCredit) {
            useSingleReportCreditForCurrentExport = false;
            useProfessionalBrandingForCurrentExport = false;
            showPersonalisedReportPurchaseDialog();
            return;
        }

        if (unlimitedPersonalisation) {
            useSingleReportCreditForCurrentExport = false;
            useProfessionalBrandingForCurrentExport = true;
        } else if (singleReportCredit) {
            useSingleReportCreditForCurrentExport = true;
            useProfessionalBrandingForCurrentExport = true;
        } else {
            // GEL PRO base report: export is allowed, but user branding is not.
            useSingleReportCreditForCurrentExport = false;
            useProfessionalBrandingForCurrentExport = false;
        }

        String report = validateExport();
        if (report == null) {
            useSingleReportCreditForCurrentExport = false;
            useProfessionalBrandingForCurrentExport = false;
            return;
        }

        // The system HTML print flow cannot reliably confirm that a PDF file
        // was actually saved, so a one-report credit is restricted to TXT PDF.
        if (pendingHtmlExport && useSingleReportCreditForCurrentExport) {
            useSingleReportCreditForCurrentExport = false;
            useProfessionalBrandingForCurrentExport = false;

            Toast.makeText(this,
                    AppLang.isGreek(this)
                            ? "Το credit των 5 € χρησιμοποιείται στο TXT PDF, όπου η εφαρμογή μπορεί να επιβεβαιώσει την επιτυχημένη δημιουργία του αρχείου."
                            : "The €5 credit is used with TXT PDF, where the app can verify successful file creation.",
                    Toast.LENGTH_LONG).show();

            pendingHtmlExport = false;
            return;
        }

        lockExportUI(true);

        if (pendingHtmlExport) {
            exportHtmlPdf(report);
        } else {
            exportTxtToPdf();
        }
    }

    private void consumeSingleReportCreditIfNeeded() {
        if (!useSingleReportCreditForCurrentExport) {
            useProfessionalBrandingForCurrentExport = false;
            return;
        }

        useSingleReportCreditForCurrentExport = false;
        useProfessionalBrandingForCurrentExport = false;

        if (billingManager != null) {
            boolean consumed = billingManager.consumeSingleReportCreditAfterSuccessfulExport();
            if (!consumed) {
                Toast.makeText(this,
                        AppLang.isGreek(this)
                                ? "Η αναφορά δημιουργήθηκε, αλλά δεν ήταν δυνατό να ενημερωθεί το credit."
                                : "The report was created, but the report credit could not be updated.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showPersonalisedReportPurchaseDialog() {
        final boolean gr = AppLang.isGreek(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                this, android.R.style.Theme_Material_Dialog_NoActionBar);
        LinearLayout root = buildGELPopupRoot(this);

        TextView title = new TextView(this);
        title.setText(gr ? "PERSONALISED SERVICE REPORT" : "PERSONALISED SERVICE REPORT");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView msg = new TextView(this);
        msg.setText(gr
                ? "Επίλεξε τον τρόπο πρόσβασης:\n\n"
                  + "• 5 € — μία εξατομικευμένη αναφορά. Δεν απαιτείται συνδρομή.\n\n"
                  + "• 29,99 € — απεριόριστες εξατομικευμένες αναφορές. Απαιτεί ενεργή συνδρομή GEL PRO."
                : "Choose access:\n\n"
                  + "• €5 — one personalised report. No subscription required.\n\n"
                  + "• €29.99 — unlimited personalised reports. Requires an active GEL PRO subscription.");
        msg.setTextColor(0xFF00FF9C);
        msg.setTextSize(14f);
        msg.setPadding(0, dp(14), 0, dp(8));
        root.addView(msg);

        Button single = gelButton(this,
                gr ? "1 REPORT — 5 €" : "1 REPORT — €5", 0xFF0F8A3B);
        Button unlimited = gelButton(this,
                gr ? "UNLIMITED — 29,99 €" : "UNLIMITED — €29.99", 0xFF202020);
        Button cancel = gelButton(this, gr ? "ΑΚΥΡΟ" : "CANCEL", 0xFF202020);

        root.addView(single);
        root.addView(unlimited);
        root.addView(cancel);

        builder.setView(root);
        final AlertDialog dialog = builder.create();

        single.setOnClickListener(v -> {
            dialog.dismiss();
            if (billingManager == null) return;
            pendingSingleReportExport = true;
            billingManager.launchSingleReportPurchase(this);
        });

        unlimited.setOnClickListener(v -> {
            dialog.dismiss();
            if (billingManager == null) return;

            if (!billingManager.isGelProActive()) {
                Toast.makeText(this,
                        gr ? "Για το unlimited πακέτο απαιτείται πρώτα ενεργή συνδρομή GEL PRO."
                           : "An active GEL PRO subscription is required before the unlimited package.",
                        Toast.LENGTH_LONG).show();
                billingManager.launchGelProPurchase(this);
                return;
            }

            billingManager.launchCustomReportsPurchase(this);
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int safeWidth = Math.min(dm.widthPixels - dp(20), dp(560));
            dialog.getWindow().setLayout(
                    Math.max(dp(280), safeWidth),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    // ============================================================
    // EMPTY REPORT FLOW — offer a printable blank two-page form
    // ============================================================
    private void showEmptyReportOptionsDialog() {

        final boolean gr = AppLang.isGreek(this);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );

        LinearLayout root = buildGELPopupRoot(this);

        TextView title = new TextView(this);
        title.setText(gr
                ? "ΔΕΝ ΥΠΑΡΧΟΥΝ ΑΠΟΤΕΛΕΣΜΑΤΑ ΕΡΓΑΣΤΗΡΙΩΝ"
                : "NO LAB RESULTS AVAILABLE");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(12));
        root.addView(title);

        TextView message = new TextView(this);
        message.setText(gr
                ? "Δεν έχει εκτελεστεί ακόμη κάποιο εργαστήριο, επομένως δεν υπάρχουν "
                  + "διαγνωστικά αποτελέσματα για εξαγωγή.\n\n"
                  + "Θέλεις να δημιουργήσεις μια κενή φόρμα Service Report με την πρώτη "
                  + "και την τελευταία σελίδα, ώστε να τη συμπληρώσεις χειρόγραφα;"
                : "No diagnostic lab has been run yet, so there are no diagnostic results "
                  + "to export.\n\n"
                  + "Would you like to create a blank Service Report containing the first "
                  + "and final pages, ready to be completed by hand?");
        message.setTextColor(0xFF00FF9C);
        message.setTextSize(14f);
        message.setLineSpacing(0f, 1.18f);
        message.setGravity(Gravity.CENTER_HORIZONTAL);
        message.setPadding(dp(4), 0, dp(4), dp(16));
        root.addView(message);

        Button createBlank = gelButton(
                this,
                gr ? "ΔΗΜΙΟΥΡΓΙΑ ΚΕΝΗΣ ΦΟΡΜΑΣ"
                   : "CREATE BLANK FORM",
                0xFF0F8A3B
        );

        Button cancel = gelButton(
                this,
                gr ? "ΑΚΥΡΟ" : "CANCEL",
                0xFF202020
        );

        root.addView(createBlank);
        root.addView(cancel);

        builder.setView(root);
        final AlertDialog dialog = builder.create();

        cancel.setOnClickListener(v -> dialog.dismiss());

        createBlank.setOnClickListener(v -> {
            dialog.dismiss();
            exportBlankServiceFormPdf();
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );

            DisplayMetrics dm = getResources().getDisplayMetrics();
            int safeWidth = Math.min(dm.widthPixels - dp(20), dp(560));

            dialog.getWindow().setLayout(
                    Math.max(dp(280), safeWidth),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void exportBlankServiceFormPdf() {

        lockExportUI(true);

        new Thread(() -> {
            try {
                PdfDocument pdf = new PdfDocument();

                Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setTextSize(12f);
                textPaint.setColor(Color.BLACK);

                Paint titlePaint = new Paint(textPaint);
                titlePaint.setTextSize(14f);
                titlePaint.setFakeBoldText(true);

                Paint subtitlePaint = new Paint(textPaint);
                subtitlePaint.setTextSize(11f);

                Paint sectionPaint = new Paint(textPaint);
                sectionPaint.setFakeBoldText(true);

                int x = PAGE_MARGIN;

                // ----------------------------------------------------
                // PAGE 1 — professional header + device intake + notes
                // ----------------------------------------------------
                int pageNum = 1;
                PdfDocument.Page page1 = startPage(pdf, pageNum);
                Canvas c1 = page1.getCanvas();
                c1.drawColor(Color.WHITE);

                // Blank report uses its OWN first-page renderer.
                // Do not call drawReportHeader() here, because that method is
                // also used by normal reports and already contains intake fields.
                int y = drawBlankReportFirstPage(
                        c1,
                        x,
                        40,
                        titlePaint,
                        subtitlePaint,
                        textPaint,
                        sectionPaint
                );

                drawPageFooter(c1, pageNum);
                pdf.finishPage(page1);

                // ----------------------------------------------------
                // PAGE 2 — final manual completion / signatures
                // ----------------------------------------------------
                pageNum = 2;
                PdfDocument.Page page2 = startPage(pdf, pageNum);
                Canvas c2 = page2.getCanvas();
                c2.drawColor(Color.WHITE);

                y = 70;

                c2.drawText(
                        "FINAL SERVICE REPORT / ΤΕΛΙΚΗ ΑΝΑΦΟΡΑ SERVICE",
                        x,
                        y,
                        titlePaint
                );
                y += 36;

                c2.drawText(
                        "Repair Summary / Τι επισκευάστηκε",
                        x,
                        y,
                        sectionPaint
                );
                y += 22;

                for (int i = 0; i < 6; i++) {
                    c2.drawLine(x, y, x + 500, y, textPaint);
                    y += 24;
                }

                y += 14;

                c2.drawText(
                        "Additional Notes / Επιπλέον παρατηρήσεις",
                        x,
                        y,
                        sectionPaint
                );
                y += 22;

                for (int i = 0; i < 5; i++) {
                    c2.drawLine(x, y, x + 500, y, textPaint);
                    y += 24;
                }

                y += 30;

                int rightX = x + 300;

                c2.drawText(
                        "Technician Name / Όνομα τεχνικού:",
                        x,
                        y,
                        textPaint
                );
                c2.drawLine(x, y + 15, x + 250, y + 15, textPaint);

                c2.drawText(
                        "Customer Name / Όνομα πελάτη:",
                        rightX,
                        y,
                        textPaint
                );
                c2.drawLine(
                        rightX,
                        y + 15,
                        rightX + 250,
                        y + 15,
                        textPaint
                );

                y += 42;

                c2.drawText(
                        "Signature / Υπογραφή:",
                        x,
                        y,
                        textPaint
                );
                c2.drawLine(x, y + 15, x + 250, y + 15, textPaint);

                c2.drawText(
                        "Signature / Υπογραφή:",
                        rightX,
                        y,
                        textPaint
                );
                c2.drawLine(
                        rightX,
                        y + 15,
                        rightX + 250,
                        y + 15,
                        textPaint
                );

                y += 55;

                c2.drawText(
                        "Date / Ημερομηνία:",
                        x,
                        y,
                        textPaint
                );
                c2.drawLine(
                        x + 140,
                        y + 15,
                        x + 300,
                        y + 15,
                        textPaint
                );

                drawPageFooter(c2, pageNum);
                pdf.finishPage(page2);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                pdf.writeTo(bos);
                pdf.close();

                Uri uri = savePdfToDownloads(
                        "GEL_Blank_Service_Report_V4.pdf",
                        bos.toByteArray()
                );

                if (useSingleReportCreditForCurrentExport && billingManager != null) {
                    billingManager.consumeSingleReportCreditAfterSuccessfulExport();
                    useSingleReportCreditForCurrentExport = false;
                }

                runOnUiThread(() -> {
                    sharePdf(uri);

                    String msg = AppLang.isGreek(this)
                            ? "Η κενή φόρμα Service Report δημιουργήθηκε."
                            : "Blank Service Report created.";

                    onExportSuccess(msg);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(
                            this,
                            AppLang.isGreek(this)
                                    ? "Σφάλμα κατά τη δημιουργία της κενής φόρμας."
                                    : "Error creating blank Service Report.",
                            Toast.LENGTH_LONG
                    ).show();

                    lockExportUI(false);
                });

                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePreview();
        if (proLogoPreview != null) refreshProfessionalLogoPreview();
    }


    // ============================================================
    // PROFESSIONAL REPORT PERSONALIZATION — STEP 1
    // Billing and PDF branding are intentionally NOT added yet.
    // ============================================================
    private View buildProfessionalProfileSection() {

        final boolean gr = AppLang.isGreek(this);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF151515);
        bg.setCornerRadius(dp(12));
        bg.setStroke(dp(2), 0xFFFFD700);
        card.setBackground(bg);

        LinearLayout.LayoutParams cardLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        cardLp.setMargins(0, 0, 0, dp(18));
        card.setLayoutParams(cardLp);

        TextView title = new TextView(this);
        title.setText("PROFESSIONAL REPORT PERSONALIZATION");
        title.setTextColor(0xFFFFD700);
        title.setTextSize(16f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(8));
        card.addView(title);

        TextView hint = new TextView(this);
        hint.setText(gr
                ? "Αποθήκευσε το λογότυπο και τα στοιχεία της επιχείρησής σου. "
                  + "Στο επόμενο βήμα θα χρησιμοποιούνται στα personalised PDF reports."
                : "Save your logo and business details. In the next step they will be used "
                  + "in personalised PDF reports.");
        hint.setTextColor(0xFF00FF9C);
        hint.setTextSize(13f);
        hint.setGravity(Gravity.CENTER_HORIZONTAL);
        hint.setLineSpacing(0f, 1.15f);
        hint.setPadding(0, 0, 0, dp(12));
        card.addView(hint);

        proLogoPreview = new ImageView(this);
        proLogoPreview.setAdjustViewBounds(true);
        proLogoPreview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        proLogoPreview.setBackgroundColor(0xFF0B0B0B);
        proLogoPreview.setPadding(dp(8), dp(8), dp(8), dp(8));

        LinearLayout.LayoutParams logoLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(120)
                );
        logoLp.setMargins(0, 0, 0, dp(10));
        card.addView(proLogoPreview, logoLp);

        refreshProfessionalLogoPreview();

        Button uploadLogo = gelButton(this, "UPLOAD YOUR LOGO", 0xFF202020);
        uploadLogo.setOnClickListener(v -> openProfessionalLogoPicker());
        card.addView(uploadLogo);

        Button companyDetails = gelButton(
                this,
                gr ? "ΣΤΟΙΧΕΙΑ ΕΤΑΙΡΕΙΑΣ / ΤΕΧΝΙΚΟΥ"
                   : "COMPANY / TECHNICIAN DETAILS",
                0xFF0F8A3B
        );
        companyDetails.setOnClickListener(v -> showProfessionalProfileDialog());
        card.addView(companyDetails);

        TextView status = new TextView(this);
        status.setText(buildProfessionalProfileStatus());
        status.setTextColor(Color.WHITE);
        status.setTextSize(12f);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, dp(10), 0, 0);
        card.addView(status);

        return card;
    }

    private void openProfessionalLogoPicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQ_PICK_PRO_LOGO);
        } catch (Throwable t) {
            Toast.makeText(
                    this,
                    AppLang.isGreek(this)
                            ? "Δεν ήταν δυνατό να ανοίξει η επιλογή εικόνας."
                            : "Could not open image picker.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQ_PICK_PRO_LOGO || resultCode != RESULT_OK || data == null) return;

        Uri uri = data.getData();
        if (uri == null) return;

        try {
            int flags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            try {
                getContentResolver().takePersistableUriPermission(
                        uri,
                        flags & Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            } catch (Throwable ignore) {}

            getSharedPreferences(PRO_PROFILE_PREFS, MODE_PRIVATE)
                    .edit()
                    .putString(KEY_LOGO_URI, uri.toString())
                    .apply();

            refreshProfessionalLogoPreview();

            Toast.makeText(
                    this,
                    AppLang.isGreek(this) ? "Το λογότυπο αποθηκεύτηκε." : "Logo saved.",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Throwable t) {
            Toast.makeText(
                    this,
                    AppLang.isGreek(this)
                            ? "Αποτυχία αποθήκευσης λογοτύπου."
                            : "Could not save logo.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void refreshProfessionalLogoPreview() {

        if (proLogoPreview == null) return;

        String savedUri = getSharedPreferences(PRO_PROFILE_PREFS, MODE_PRIVATE)
                .getString(KEY_LOGO_URI, null);

        if (savedUri == null || savedUri.trim().isEmpty()) {
            proLogoPreview.setImageResource(R.drawable.gel_logo);
            return;
        }

        try {
            proLogoPreview.setImageURI(Uri.parse(savedUri));
        } catch (Throwable t) {
            proLogoPreview.setImageResource(R.drawable.gel_logo);
        }
    }

    private void showProfessionalProfileDialog() {

        final boolean gr = AppLang.isGreek(this);
        final SharedPreferences sp =
                getSharedPreferences(PRO_PROFILE_PREFS, MODE_PRIVATE);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );

        LinearLayout root = buildGELPopupRoot(this);

        TextView title = new TextView(this);
        title.setText(gr
                ? "ΣΤΟΙΧΕΙΑ ΕΤΑΙΡΕΙΑΣ / ΤΕΧΝΙΚΟΥ"
                : "COMPANY / TECHNICIAN DETAILS");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(12));
        root.addView(title);

        ScrollView bodyScroll = new ScrollView(this);
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);

        EditText businessName = profileField(gr ? "Επωνυμία επιχείρησης" : "Business name",
                sp.getString(KEY_BUSINESS_NAME, ""));
        EditText technicianName = profileField(gr ? "Ονοματεπώνυμο τεχνικού / υπευθύνου" : "Technician / responsible person",
                sp.getString(KEY_TECHNICIAN_NAME, ""));
        EditText address = profileField(gr ? "Διεύθυνση" : "Address",
                sp.getString(KEY_ADDRESS, ""));
        EditText city = profileField(gr ? "Πόλη" : "City",
                sp.getString(KEY_CITY, ""));
        EditText postalCode = profileField(gr ? "Τ.Κ." : "Postal code",
                sp.getString(KEY_POSTAL_CODE, ""));
        EditText country = profileField(gr ? "Χώρα" : "Country",
                sp.getString(KEY_COUNTRY, ""));
        EditText phone = profileField(gr ? "Τηλέφωνο" : "Phone",
                sp.getString(KEY_PHONE, ""));
        phone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        EditText email = profileField("Email", sp.getString(KEY_EMAIL, ""));
        email.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText website = profileField("Website", sp.getString(KEY_WEBSITE, ""));
        EditText vat = profileField(gr ? "ΑΦΜ / VAT Number" : "VAT Number / Tax ID",
                sp.getString(KEY_VAT, ""));
        EditText taxOffice = profileField(gr ? "ΔΟΥ / Tax Office" : "Tax Office",
                sp.getString(KEY_TAX_OFFICE, ""));

        body.addView(businessName);
        body.addView(technicianName);
        body.addView(address);
        body.addView(city);
        body.addView(postalCode);
        body.addView(country);
        body.addView(phone);
        body.addView(email);
        body.addView(website);
        body.addView(vat);
        body.addView(taxOffice);

        bodyScroll.addView(body);

        LinearLayout.LayoutParams scrollLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                );
        root.addView(bodyScroll, scrollLp);

        Button save = gelButton(this, gr ? "ΑΠΟΘΗΚΕΥΣΗ" : "SAVE", 0xFF0F8A3B);
        Button cancel = gelButton(this, gr ? "ΑΚΥΡΟ" : "CANCEL", 0xFF202020);

        root.addView(save);
        root.addView(cancel);

        builder.setView(root);
        final AlertDialog dialog = builder.create();

        cancel.setOnClickListener(v -> dialog.dismiss());

        save.setOnClickListener(v -> {
            sp.edit()
                    .putString(KEY_BUSINESS_NAME, valueOf(businessName))
                    .putString(KEY_TECHNICIAN_NAME, valueOf(technicianName))
                    .putString(KEY_ADDRESS, valueOf(address))
                    .putString(KEY_CITY, valueOf(city))
                    .putString(KEY_POSTAL_CODE, valueOf(postalCode))
                    .putString(KEY_COUNTRY, valueOf(country))
                    .putString(KEY_PHONE, valueOf(phone))
                    .putString(KEY_EMAIL, valueOf(email))
                    .putString(KEY_WEBSITE, valueOf(website))
                    .putString(KEY_VAT, valueOf(vat))
                    .putString(KEY_TAX_OFFICE, valueOf(taxOffice))
                    .apply();

            Toast.makeText(
                    this,
                    gr ? "Τα επαγγελματικά στοιχεία αποθηκεύτηκαν."
                       : "Professional profile saved.",
                    Toast.LENGTH_SHORT
            ).show();

            dialog.dismiss();
            recreate();
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            DisplayMetrics dm = getResources().getDisplayMetrics();
            int safeWidth = Math.min(dm.widthPixels - dp(20), dp(560));
            int safeHeight = (int) (dm.heightPixels * 0.92f);

            dialog.getWindow().setLayout(
                    Math.max(dp(280), safeWidth),
                    safeHeight
            );
        }
    }

    private EditText profileField(String hint, String value) {

        EditText field = new EditText(this);
        field.setHint(hint);
        field.setText(value == null ? "" : value);
        field.setTextColor(Color.WHITE);
        field.setHintTextColor(0xFFAAAAAA);
        field.setTextSize(14f);
        field.setSingleLine(true);
        field.setPadding(dp(12), dp(10), dp(12), dp(10));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF0B0B0B);
        bg.setCornerRadius(dp(8));
        bg.setStroke(dp(1), 0xFFFFD700);
        field.setBackground(bg);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        lp.setMargins(0, 0, 0, dp(10));
        field.setLayoutParams(lp);

        return field;
    }

    private String valueOf(EditText field) {
        return field == null ? "" : field.getText().toString().trim();
    }

    private String buildProfessionalProfileStatus() {

        SharedPreferences sp = getSharedPreferences(PRO_PROFILE_PREFS, MODE_PRIVATE);

        boolean hasLogo = sp.getString(KEY_LOGO_URI, null) != null;
        boolean hasBusiness = !sp.getString(KEY_BUSINESS_NAME, "").trim().isEmpty();

        if (AppLang.isGreek(this)) {
            if (hasLogo && hasBusiness) return "✓ Το επαγγελματικό προφίλ είναι έτοιμο.";
            if (hasLogo || hasBusiness) return "• Το επαγγελματικό προφίλ είναι μερικώς συμπληρωμένο.";
            return "• Δεν έχουν αποθηκευτεί ακόμα στοιχεία επαγγελματικού προφίλ.";
        }

        if (hasLogo && hasBusiness) return "✓ Professional profile is ready.";
        if (hasLogo || hasBusiness) return "• Professional profile is partially completed.";
        return "• No professional profile details saved yet.";
    }


    // ============================================================
    // PROFESSIONAL PROFILE — STEP 2 PDF BRANDING HELPERS
    // ============================================================
    private SharedPreferences proProfilePrefs() {
        return getSharedPreferences(PRO_PROFILE_PREFS, MODE_PRIVATE);
    }

    private String proValue(String key) {
        return proProfilePrefs().getString(key, "").trim();
    }

    private Bitmap getProfessionalLogoBitmap() {
        String savedUri = proProfilePrefs().getString(KEY_LOGO_URI, null);

        if (savedUri != null && !savedUri.trim().isEmpty()) {
            try (java.io.InputStream in =
                         getContentResolver().openInputStream(Uri.parse(savedUri))) {
                Bitmap b = BitmapFactory.decodeStream(in);
                if (b != null) return b;
            } catch (Throwable ignore) {}
        }

        return gelLogo;
    }

    private String getProfessionalLogoDataUri() {
        Bitmap b = getProfessionalLogoBitmap();
        if (b == null) return "file:///android_res/drawable/gel_logo.png";

        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 95, out);
            String encoded = android.util.Base64.encodeToString(
                    out.toByteArray(),
                    android.util.Base64.NO_WRAP
            );
            return "data:image/png;base64," + encoded;
        } catch (Throwable ignore) {
            return "file:///android_res/drawable/gel_logo.png";
        }
    }

    private String professionalCompanyName() {
        String business = proValue(KEY_BUSINESS_NAME);
        return business.isEmpty() ? "GDiolitsis Engine Lab (GEL)" : business;
    }

    private String buildProfessionalHtmlDetails() {
        StringBuilder sb = new StringBuilder();

        appendHtmlDetail(sb, "Technician / Τεχνικός", proValue(KEY_TECHNICIAN_NAME));

        String address = proValue(KEY_ADDRESS);
        String city = proValue(KEY_CITY);
        String postal = proValue(KEY_POSTAL_CODE);
        String country = proValue(KEY_COUNTRY);

        StringBuilder addressLine = new StringBuilder();
        if (!address.isEmpty()) addressLine.append(address);
        if (!city.isEmpty()) {
            if (addressLine.length() > 0) addressLine.append(", ");
            addressLine.append(city);
        }
        if (!postal.isEmpty()) {
            if (addressLine.length() > 0) addressLine.append(" ");
            addressLine.append(postal);
        }
        if (!country.isEmpty()) {
            if (addressLine.length() > 0) addressLine.append(", ");
            addressLine.append(country);
        }
        appendHtmlDetail(sb, "Address / Διεύθυνση", addressLine.toString());

        appendHtmlDetail(sb, "Phone / Τηλέφωνο", proValue(KEY_PHONE));
        appendHtmlDetail(sb, "Email", proValue(KEY_EMAIL));
        appendHtmlDetail(sb, "Website", proValue(KEY_WEBSITE));
        appendHtmlDetail(sb, "VAT / ΑΦΜ", proValue(KEY_VAT));
        appendHtmlDetail(sb, "Tax Office / ΔΟΥ", proValue(KEY_TAX_OFFICE));

        return sb.toString();
    }

    private void appendHtmlDetail(StringBuilder sb, String label, String value) {
        if (value == null || value.trim().isEmpty()) return;
        sb.append("<div class='company-detail'><b>")
                .append(escapeHtml(label))
                .append(":</b> ")
                .append(escapeHtml(value.trim()))
                .append("</div>");
    }


    private int drawWrappedLine(
            Canvas canvas,
            String value,
            int x,
            int y,
            Paint paint,
            int maxWidth
    ) {
        if (canvas == null || paint == null || value == null) return y;

        String remaining = value.trim();
        if (remaining.isEmpty()) return y;

        final int lineHeight =
                Math.max(16, (int) Math.ceil(paint.getFontSpacing()));

        while (!remaining.isEmpty()) {

            int count = paint.breakText(
                    remaining,
                    true,
                    Math.max(80, maxWidth),
                    null
            );

            if (count <= 0) count = Math.min(1, remaining.length());

            // Prefer breaking on a space instead of cutting a word.
            if (count < remaining.length()) {
                int lastSpace = remaining.lastIndexOf(' ', count - 1);
                if (lastSpace > 0) count = lastSpace;
            }

            String line = remaining.substring(0, count).trim();

            if (!line.isEmpty()) {
                canvas.drawText(line, x, y, paint);
                y += lineHeight;
            }

            remaining = remaining.substring(count).trim();
        }

        return y;
    }

    private int drawProfessionalCompanyDetails(
            Canvas c,
            int x,
            int y,
            Paint text,
            int maxWidth
    ) {
        Paint detailPaint = new Paint(text);
        detailPaint.setTextSize(Math.max(9f, text.getTextSize() - 1f));

        String technician = proValue(KEY_TECHNICIAN_NAME);
        if (!technician.isEmpty()) {
            y = drawWrappedLine(c, "Technician / Τεχνικός: " + technician, x, y, detailPaint, maxWidth);
        }

        String address = proValue(KEY_ADDRESS);
        String city = proValue(KEY_CITY);
        String postal = proValue(KEY_POSTAL_CODE);
        String country = proValue(KEY_COUNTRY);

        StringBuilder addr = new StringBuilder();
        if (!address.isEmpty()) addr.append(address);
        if (!city.isEmpty()) {
            if (addr.length() > 0) addr.append(", ");
            addr.append(city);
        }
        if (!postal.isEmpty()) {
            if (addr.length() > 0) addr.append(" ");
            addr.append(postal);
        }
        if (!country.isEmpty()) {
            if (addr.length() > 0) addr.append(", ");
            addr.append(country);
        }
        if (addr.length() > 0) {
            y = drawWrappedLine(c, "Address / Διεύθυνση: " + addr, x, y, detailPaint, maxWidth);
        }

        String phone = proValue(KEY_PHONE);
        if (!phone.isEmpty()) {
            y = drawWrappedLine(c, "Phone / Τηλέφωνο: " + phone, x, y, detailPaint, maxWidth);
        }

        String email = proValue(KEY_EMAIL);
        if (!email.isEmpty()) {
            y = drawWrappedLine(c, "Email: " + email, x, y, detailPaint, maxWidth);
        }

        String website = proValue(KEY_WEBSITE);
        if (!website.isEmpty()) {
            y = drawWrappedLine(c, "Website: " + website, x, y, detailPaint, maxWidth);
        }

        String vat = proValue(KEY_VAT);
        if (!vat.isEmpty()) {
            y = drawWrappedLine(c, "VAT / ΑΦΜ: " + vat, x, y, detailPaint, maxWidth);
        }

        String taxOffice = proValue(KEY_TAX_OFFICE);
        if (!taxOffice.isEmpty()) {
            y = drawWrappedLine(c, "Tax Office / ΔΟΥ: " + taxOffice, x, y, detailPaint, maxWidth);
        }

        return y;
    }

    // ============================================================
    // FREE SERVICE REPORT PREVIEW
    // ============================================================
    private View buildServiceReportPreview() {

        final boolean gr = AppLang.isGreek(this);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(10), dp(14), dp(10), dp(14));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF151515);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(2), 0xFFFFD700);
        card.setBackground(bg);

        LinearLayout.LayoutParams cardLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        cardLp.setMargins(0, 0, 0, dp(18));
        card.setLayoutParams(cardLp);

        TextView title = previewText(
                gr ? "ΠΡΟΕΠΙΣΚΟΠΗΣΗ ΕΠΑΓΓΕΛΜΑΤΙΚΗΣ ΑΝΑΦΟΡΑΣ"
                   : "PROFESSIONAL REPORT PREVIEW",
                14f,
                0xFF00FF9C,
                true
        );
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(12));
        card.addView(title);

        TextView firstLabel = previewText(
                gr ? "Πρώτη σελίδα" : "First page",
                12f,
                Color.WHITE,
                true
        );
        firstLabel.setGravity(Gravity.CENTER);
        firstLabel.setPadding(0, 0, 0, dp(6));
        card.addView(firstLabel);

        ImageView firstPage = new ImageView(this);
        firstPage.setImageResource(R.drawable.gel_report_preview_first);
        firstPage.setAdjustViewBounds(true);
        firstPage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        firstPage.setContentDescription(
                gr ? "Προεπισκόπηση πρώτης σελίδας GEL Service Report"
                   : "GEL Service Report first page preview"
        );
        card.addView(firstPage, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView finalLabel = previewText(
                gr ? "Τελική σελίδα" : "Final page",
                12f,
                Color.WHITE,
                true
        );
        finalLabel.setGravity(Gravity.CENTER);
        finalLabel.setPadding(0, dp(14), 0, dp(6));
        card.addView(finalLabel);

        ImageView finalPage = new ImageView(this);
        finalPage.setImageResource(R.drawable.gel_report_preview_final);
        finalPage.setAdjustViewBounds(true);
        finalPage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        finalPage.setContentDescription(
                gr ? "Προεπισκόπηση τελικής σελίδας GEL Service Report"
                   : "GEL Service Report final page preview"
        );
        card.addView(finalPage, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView proNote = previewText(
                gr
                        ? "🔒 Αποθήκευση & εξαγωγή PDF — GEL PRO"
                        : "🔒 Save & export PDF — GEL PRO",
                14f,
                0xFFFFD700,
                true
        );
        proNote.setGravity(Gravity.CENTER);
        proNote.setPadding(0, dp(16), 0, 0);
        card.addView(proNote);

        return card;
    }

    private TextView previewText(
            String text,
            float size,
            int color,
            boolean bold
    ) {
        TextView v = new TextView(this);
        v.setText(text);
        v.setTextSize(size);
        v.setTextColor(color);
        if (bold) v.setTypeface(null, Typeface.BOLD);
        return v;
    }

    // ============================================================
    // GEL PRO — EXPORT ENTITLEMENT
    // ============================================================
    private boolean isGelProActive() {
        try {
            return getSharedPreferences(GEL_PRO_PREFS, MODE_PRIVATE)
                    .getBoolean(GEL_PRO_ACTIVE_KEY, false);
        } catch (Throwable ignore) {
            return false;
        }
    }

    private boolean requireGelProExport() {
        if (isGelProActive()) return true;

        showGelProExportDialog();
        return false;
    }

    // ============================================================
    // GEL PRO — EXPORT SERVICE REPORT POPUP
    // Same GEL black/gold language-switch pattern as the locked labs.
    // ============================================================
    private void showGelProExportDialog() {

        final boolean[] popupGreek = { AppLang.isGreek(this) };

        AlertDialog.Builder builder =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        builder.setCancelable(true);

        LinearLayout root = buildGELPopupRoot(this);

        LinearLayout header =
                buildPopupHeader(
                        this,
                        popupGreek[0]
                                ? "GEL PRO — Επαγγελματική λειτουργία"
                                : "GEL PRO — Professional Feature"
                );

        TextView headerTitle = null;
        if (header.getChildCount() > 0 &&
                header.getChildAt(0) instanceof TextView) {
            headerTitle = (TextView) header.getChildAt(0);
        }
        root.addView(header);

        LinearLayout langRow = new LinearLayout(this);
        langRow.setOrientation(LinearLayout.HORIZONTAL);
        langRow.setGravity(Gravity.CENTER);
        langRow.setPadding(0, 0, 0, dp(10));

        Button btnEl = gelButton(this, "EL", 0xFF202020);
        Button btnEn = gelButton(this, "EN", 0xFF202020);

        LinearLayout.LayoutParams elLp =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );
        elLp.setMargins(dp(2), 0, dp(4), 0);

        LinearLayout.LayoutParams enLp =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );
        enLp.setMargins(dp(4), 0, dp(2), 0);

        btnEl.setLayoutParams(elLp);
        btnEn.setLayoutParams(enLp);
        langRow.addView(btnEl);
        langRow.addView(btnEn);
        root.addView(langRow);

        ScrollView bodyScroll = new ScrollView(this);
        bodyScroll.setFillViewport(false);
        bodyScroll.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);

        LinearLayout.LayoutParams scrollLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                );
        bodyScroll.setLayoutParams(scrollLp);

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);

        TextView feature = new TextView(this);
        feature.setTextColor(Color.WHITE);
        feature.setTextSize(17f);
        feature.setTypeface(null, Typeface.BOLD);
        feature.setGravity(Gravity.CENTER);
        feature.setPadding(0, dp(4), 0, dp(12));
        body.addView(feature);

        TextView msg = new TextView(this);
        msg.setTextColor(0xFF00FF9C);
        msg.setTextSize(15f);
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        msg.setLineSpacing(0f, 1.15f);
        body.addView(msg);

        bodyScroll.addView(body);
        root.addView(bodyScroll);

        TextView price = new TextView(this);
        price.setTextColor(0xFFFFD700);
        price.setTextSize(17f);
        price.setTypeface(null, Typeface.BOLD);
        price.setGravity(Gravity.CENTER);
        price.setPadding(0, dp(10), 0, dp(8));
        root.addView(price);

        Button notNow =
                gelButton(
                        this,
                        popupGreek[0] ? "Όχι τώρα" : "Not now",
                        0xFF202020
                );
        root.addView(notNow);

        Button pro =
                gelButton(
                        this,
                        "GEL PRO",
                        0xFF0F8A3B
                );
        root.addView(pro);

        final TextView finalHeaderTitle = headerTitle;

        Runnable render = () -> {
            boolean gr = popupGreek[0];

            if (finalHeaderTitle != null) {
                finalHeaderTitle.setText(
                        gr
                                ? "GEL PRO — Επαγγελματική λειτουργία"
                                : "GEL PRO — Professional Feature"
                );
            }

            feature.setText("Export Service Report");

            msg.setText(
                    gr
                            ? "Επαγγελματική αναφορά διάγνωσης για τον πελάτη\n\n" +
                              "Δημιουργήστε ολοκληρωμένη αναφορά service με τα αποτελέσματα των διαγνωστικών ελέγχων GEL.\n\n" +
                              "✓ Επαγγελματική μορφοποίηση αναφοράς\n" +
                              "✓ Στοιχεία συσκευής και διάγνωσης\n" +
                              "✓ Συγκεντρωτικά αποτελέσματα εργαστηρίων\n" +
                              "✓ Βασικά τεχνικά ευρήματα\n" +
                              "✓ Τελική κατάσταση συσκευής\n" +
                              "✓ Ημερομηνία και στοιχεία διάγνωσης\n" +
                              "✓ Diagnostic ID\n" +
                              "✓ Αναφορά κατάλληλη για αποθήκευση, εκτύπωση ή παράδοση στον πελάτη\n\n" +
                              "Προαιρετική εξατομίκευση επαγγελματία\n" +
                              "Λογότυπο, επωνυμία και στοιχεία τεχνικού/καταστήματος.\n" +
                              "Εφάπαξ ενεργοποίηση: 29,99 €\n\n" +
                              "Η εξατομίκευση θα ενεργοποιηθεί σε επόμενο στάδιο."
                            : "Professional diagnostic report for your customer\n\n" +
                              "Create a complete service report using the results of GEL diagnostic tests.\n\n" +
                              "✓ Professional report formatting\n" +
                              "✓ Device and diagnostic information\n" +
                              "✓ Consolidated laboratory results\n" +
                              "✓ Key technical findings\n" +
                              "✓ Final device condition\n" +
                              "✓ Diagnostic date and information\n" +
                              "✓ Diagnostic ID\n" +
                              "✓ Report suitable for saving, printing or delivery to the customer\n\n" +
                              "Optional professional personalization\n" +
                              "Logo, business name and technician/shop details.\n" +
                              "One-time activation: €29.99\n\n" +
                              "Personalization will be enabled in a later stage."
            );

            price.setText(
                    gr
                            ? "Συνδρομή: 4,99 € / μήνα"
                            : "Subscription: €4.99 / month"
            );

            notNow.setText(gr ? "Όχι τώρα" : "Not now");
            pro.setText("GEL PRO");

            btnEl.setAlpha(gr ? 1.0f : 0.55f);
            btnEn.setAlpha(gr ? 0.55f : 1.0f);

            bodyScroll.post(() -> bodyScroll.scrollTo(0, 0));
        };

        btnEl.setOnClickListener(v -> {
            popupGreek[0] = true;
            render.run();
        });

        btnEn.setOnClickListener(v -> {
            popupGreek[0] = false;
            render.run();
        });

        render.run();

        builder.setView(root);
        final AlertDialog dialog = builder.create();

        notNow.setOnClickListener(v -> dialog.dismiss());

        // Temporary until Google Play Billing purchase flow is connected.
        pro.setOnClickListener(v -> dialog.dismiss());

        dialog.setOnKeyListener((d, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK &&
                    event.getAction() == KeyEvent.ACTION_UP) {
                d.dismiss();
                return true;
            }
            return false;
        });

        if (!isFinishing() && !isDestroyed()) {
            dialog.show();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT)
                );

                DisplayMetrics dm = getResources().getDisplayMetrics();

                int safeWidth =
                        Math.min(dm.widthPixels - dp(20), dp(560));

                int safeHeight =
                        (int) (dm.heightPixels * 0.92f);

                dialog.getWindow().setLayout(
                        Math.max(dp(280), safeWidth),
                        safeHeight
                );
            }
        }
    }

    private LinearLayout buildPopupHeader(Context ctx, String titleText) {
        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(0, 0, 0, dp(12));

        TextView title = new TextView(ctx);
        title.setText(titleText);
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.START);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );
        title.setLayoutParams(lp);
        header.addView(title);
        return header;
    }

    private Button gelButton(Context ctx, String text, int bgColor) {
        Button b = new Button(ctx);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15f);
        b.setTypeface(null, Typeface.BOLD);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(bgColor);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(3), 0xFFFFD700);
        b.setBackground(bg);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                );
        lp.setMargins(0, dp(10), 0, 0);
        b.setLayoutParams(lp);
        return b;
    }

    private LinearLayout buildGELPopupRoot(Context ctx) {
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(22), dp(24), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(4), 0xFFFFD700);
        root.setBackground(bg);
        return root;
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5f);
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
                showEmptyReportOptionsDialog();
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

ByteArrayOutputStream bos = new ByteArrayOutputStream();
pdf.writeTo(bos);
pdf.close();

Uri uri = savePdfToDownloads("GEL_Service_Report.pdf", bos.toByteArray());
consumeSingleReportCreditIfNeeded();
sharePdf(uri);

            runOnUiThread(() -> {

    boolean gr = AppLang.isGreek(this);

    String successMsg = gr
            ? "Το PDF αποθηκεύτηκε στα Downloads."
            : "PDF saved to Downloads.";

    onExportSuccess(successMsg);
});

// 🔥 καθαρισμός logs ΜΕΤΑ το success
GELServiceLog.clear();

} catch (Exception e) {

    runOnUiThread(() -> {

        boolean gr = AppLang.isGreek(this);

        String errorMsg = gr
                ? "Σφάλμα κατά την εξαγωγή PDF: " + e.getMessage()
                : "PDF ERROR: " + e.getMessage();

        Toast.makeText(
                this,
                errorMsg,
                Toast.LENGTH_LONG
        ).show();

        lockExportUI(false);
    });

    e.printStackTrace();
}
    }

    // ==========================================================
    // HTML PDF ENGINE — MULTI PAGE + COLORED
    // ==========================================================
private void exportHtmlPdf(String report) {

    WebView webView = new WebView(this);
    webView.getSettings().setJavaScriptEnabled(false);

    String html = buildHtmlReport(report);

    webView.setVisibility(View.INVISIBLE);
    webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    webView.setWebViewClient(new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {

            try {

                android.print.PrintManager printManager =
                        (android.print.PrintManager) getSystemService(Context.PRINT_SERVICE);

                android.print.PrintDocumentAdapter adapter =
                        view.createPrintDocumentAdapter("GEL_Service_Report");

                android.print.PrintAttributes attributes =
                        new android.print.PrintAttributes.Builder()
                                .setMediaSize(android.print.PrintAttributes.MediaSize.ISO_A4)
                                .setResolution(new android.print.PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                                .setMinMargins(android.print.PrintAttributes.Margins.NO_MARGINS)
                                .build();

                // 🔥 αυτό κάνει όλη τη δουλειά (NO manual callbacks)
                printManager.print(
                        "GEL_Service_Report",
                        adapter,
                        attributes
                );

                runOnUiThread(() -> {

                    boolean gr = AppLang.isGreek(ServiceReportActivity.this);

                    String msg = gr
                            ? "Το HTML PDF αποθηκεύτηκε στα Downloads."
                                            : "HTML PDF saved to Downloads.";

                    Toast.makeText(
                            ServiceReportActivity.this,
                            msg,
                            Toast.LENGTH_LONG
                    ).show();

                    lockExportUI(false);
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    boolean gr = AppLang.isGreek(ServiceReportActivity.this);

                    String msg = gr
                            ? "Σφάλμα κατά την εξαγωγή PDF."
                            : "Error exporting PDF.";

                    Toast.makeText(
                            ServiceReportActivity.this,
                            msg,
                            Toast.LENGTH_LONG
                    ).show();

                    lockExportUI(false);
                });

                e.printStackTrace();
            }
        }
    });

    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
}

private String buildHtmlReport(String report) {

    String[] lines = report.split("\\n");
    StringBuilder body = new StringBuilder();

    // =========================================================
    // HEADER (PRODUCTION GEL STYLE)
    // =========================================================
    body.append("<div class='header'>");

    // LEFT
    body.append("<div class='header-left'>");

    if (useProfessionalBrandingForCurrentExport) {
        body.append("<img class='logo' src='")
                .append(getProfessionalLogoDataUri())
                .append("'/>");

        body.append("<div class='header-text'>");
        body.append("<div class='title'>")
                .append(escapeHtml(professionalCompanyName()))
                .append("</div>");
        body.append("<div class='subtitle'>GEL Service Report / Αναφορά Service</div>");
        body.append(buildProfessionalHtmlDetails());
        body.append("</div>");

    } else {
        body.append("<img class='logo' src='file:///android_res/drawable/gel_logo.png'/>");
        body.append("<div class='header-text'>");
        body.append("<div class='title'>GDiolitsis Engine Lab (GEL)</div>");
        body.append("<div class='subtitle'>GEL Service Report / Αναφορά Service</div>");
        body.append("</div>");
    }

    body.append("</div>");

    // RIGHT
    body.append("<div class='header-meta'>");

    body.append("Date / Ημερομηνία:<br>");
    body.append(escapeHtml(java.text.DateFormat.getDateTimeInstance().format(new java.util.Date())));
    body.append("<br><br>");

    body.append("Device / Συσκευή:<br>");
    body.append(escapeHtml(android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL));
    body.append("<br><br>");

    body.append("Android / Έκδοση:<br>");
    body.append(escapeHtml(android.os.Build.VERSION.RELEASE + " (API " + android.os.Build.VERSION.SDK_INT + ")"));

    body.append("</div>");
    body.append("</div>");

    // =========================================================
    // BODY LOGS
    // =========================================================
    for (String raw : lines) {

        String line = raw == null ? "" : raw.trim();

        if (line.isEmpty()) {
            body.append("<div class='space'></div>");
            continue;
        }

        String escaped = escapeHtml(line);

        if (line.startsWith("✔")) {
            body.append("<div class='ok'>").append(escaped).append("</div>");
        }
        else if (line.startsWith("⚠")) {
            body.append("<div class='warn'>").append(escaped).append("</div>");
        }
        else if (line.startsWith("✖")) {
            body.append("<div class='err'>").append(escaped).append("</div>");
        }
        else if (line.startsWith("ℹ") || line.startsWith("i ")) {
            body.append("<div class='info'>").append(escaped).append("</div>");
        }
        else if (line.contains(":")) {

            String[] parts = line.split(":", 2);

            String label = escapeHtml(parts[0].trim());
            String value = parts.length > 1 ? escapeHtml(parts[1].trim()) : "";

            body.append("<div class='row'><span class='label'>")
                    .append(label)
                    .append(":</span> <span class='value'>")
                    .append(value)
                    .append("</span></div>");
        }
        else {
            body.append("<div class='plain'>").append(escaped).append("</div>");
        }
    }

    // =========================================================
    // SECTIONS
    // =========================================================
    body.append("<div class='section-title'>Repair Summary / Τι επισκευάστηκε</div>");
    body.append("<div class='line'></div><div class='line'></div><div class='line'></div><div class='line'></div>");

    body.append("<div class='section-title'>Additional Notes / Επιπλέον παρατηρήσεις</div>");
    body.append("<div class='line'></div><div class='line'></div><div class='line'></div>");

    // =========================================================
    // SIGNATURES
    // =========================================================
    body.append("<div class='signatures'>");

    // NAMES
    body.append("<div class='sig-row'>");

    body.append("<div class='sig-block'>");
    body.append("<div class='sig-line'></div>");
    body.append("<div class='sig-label'>Technician Name / Όνομα τεχνικού</div>");
    body.append("</div>");

    body.append("<div class='sig-block'>");
    body.append("<div class='sig-line'></div>");
    body.append("<div class='sig-label'>Customer Name / Όνομα πελάτη</div>");
    body.append("</div>");

    body.append("</div>");

    // SIGNATURES
    body.append("<div class='sig-row'>");

    body.append("<div class='sig-block'>");
    body.append("<div class='sig-line'></div>");
    body.append("<div class='sig-label'>Signature / Υπογραφή</div>");
    body.append("</div>");

    body.append("<div class='sig-block'>");
    body.append("<div class='sig-line'></div>");
    body.append("<div class='sig-label'>Signature / Υπογραφή</div>");
    body.append("</div>");

    body.append("</div>");

    // DATE
    body.append("<div class='sig-block-full'>");
    body.append("<div class='sig-line'></div>");
    body.append("<div class='sig-label'>Date / Ημερομηνία</div>");
    body.append("</div>");

    body.append("</div>");

    // =========================================================
    // FINAL HTML
    // =========================================================
    return "<html><head><meta charset='utf-8'/>"
            + "<style>"

            + "body{font-family:monospace;background:#ffffff;color:#111;padding:24px;margin:0;font-size:13px;line-height:1.45;}"

            // HEADER
            + ".header{display:flex;justify-content:space-between;align-items:center;margin-bottom:18px;padding-bottom:12px;border-bottom:2px solid #d4af37;}"
            + ".header-left{display:flex;align-items:center;gap:14px;}"
            + ".logo{width:52px;height:52px;}"
            + ".header-text{display:flex;flex-direction:column;}"
            + ".title{font-size:18px;font-weight:700;color:#000;margin-bottom:4px;}"
            + ".company-detail{font-size:10px;color:#222;line-height:1.35;margin-top:2px;}.subtitle{font-size:12px;color:#444;}"
            + ".header-meta{font-size:11px;color:#222;text-align:right;line-height:1.4;}"

            // LOGS
            + ".ok{color:#0b8f2d;font-weight:700;margin:6px 0;}"
            + ".warn{color:#d98200;font-weight:700;margin:6px 0;}"
            + ".err{color:#b00020;font-weight:700;margin:6px 0;}"
            + ".info{color:#1565c0;font-weight:700;margin:6px 0;}"
            + ".plain{margin:4px 0;white-space:pre-wrap;}"
            + ".row{margin:4px 0;}"
            + ".label{font-weight:700;color:#000;}"
            + ".value{color:#222;}"
            + ".space{height:10px;}"

            // SECTIONS
            + ".section-title{margin-top:22px;margin-bottom:10px;font-weight:700;font-size:15px;}"
            + ".line{height:22px;border-bottom:1px solid #333;}"

            // SIGNATURES
            + ".signatures{margin-top:40px;}"
            + ".sig-row{display:flex;justify-content:space-between;gap:40px;margin-bottom:40px;}"
            + ".sig-block{width:45%;}"
            + ".sig-block-full{width:60%;margin-top:30px;}"
            + ".sig-line{border-bottom:2px solid #000;height:40px;margin-bottom:6px;}"
            + ".sig-label{font-size:11px;color:#333;}"

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

    File outDir = new File(getExternalFilesDir(null), "GEL_Reports");

    if (!outDir.exists()) {
        outDir.mkdirs();
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


    private int drawBlankReportFirstPage(
            Canvas c,
            int x,
            int startY,
            Paint title,
            Paint subtitle,
            Paint text,
            Paint sectionTitle
    ) {
        int y = startY;

        // Page-1 identity.
        // Saved professional branding is a separate paid entitlement.
        if (useProfessionalBrandingForCurrentExport) {
            Bitmap professionalLogo = getProfessionalLogoBitmap();
            if (professionalLogo != null) {
                Bitmap scaled = Bitmap.createScaledBitmap(professionalLogo, 58, 58, true);
                c.drawBitmap(scaled, x, y, null);
            }

            y += 76;

            c.drawText(professionalCompanyName(), x, y, title);
            y += 20;

            c.drawText("GEL Service Report / Αναφορά Service", x, y, subtitle);
            y += 20;

            y = drawProfessionalCompanyDetails(
                    c,
                    x,
                    y,
                    text,
                    Math.max(240, c.getWidth() - (x * 2))
            );

            y += 8;

        } else {
            if (gelLogo != null) {
                Bitmap scaled = Bitmap.createScaledBitmap(gelLogo, 58, 58, true);
                c.drawBitmap(scaled, x, y, null);
            }

            y += 76;

            c.drawText("GDiolitsis Engine Lab (GEL)", x, y, title);
            y += 20;

            c.drawText("GEL Service Report / Αναφορά Service", x, y, subtitle);
            y += 28;
        }

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

        // ONE AND ONLY intake block for the blank report.
        c.drawText(
                "Device intake condition / Κατάσταση παραλαβής συσκευής",
                x,
                y,
                sectionTitle
        );
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
        boxPaint.setColor(Color.BLACK);

        for (String item : intakeLines) {
            int boxSize = 10;

            c.drawRect(x, y - 9, x + boxSize, y + 1, boxPaint);
            c.drawText(" OK", x + boxSize + 4, y, text);

            int dmgX = x + 80;
            c.drawRect(dmgX, y - 9, dmgX + boxSize, y + 1, boxPaint);
            c.drawText(" DAMAGED", dmgX + boxSize + 4, y, text);

            c.drawText("— " + item, x + 170, y, text);
            y += 16;
        }

        y += 24;

        c.drawText("Notes / Παρατηρήσεις:", x, y, sectionTitle);
        y += 20;

        for (int i = 0; i < 4; i++) {
            c.drawLine(x, y, x + 500, y, text);
            y += 22;
        }

        return y;
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

        if (isFirstPage) {
            // Full identity appears ONLY on page 1.
            // Professional user branding is used only when separately entitled.
            if (useProfessionalBrandingForCurrentExport) {
                Bitmap professionalLogo = getProfessionalLogoBitmap();
                if (professionalLogo != null) {
                    Bitmap scaled = Bitmap.createScaledBitmap(professionalLogo, 58, 58, true);
                    c.drawBitmap(scaled, x, y, null);
                }

                y += 76;

                c.drawText(professionalCompanyName(), x, y, title);
                y += 20;

                c.drawText("GEL Service Report / Αναφορά Service", x, y, subtitle);
                y += 20;

                y = drawProfessionalCompanyDetails(
                        c,
                        x,
                        y,
                        text,
                        Math.max(240, c.getWidth() - (x * 2))
                );

                y += 8;

            } else {
                if (gelLogo != null) {
                    Bitmap scaled = Bitmap.createScaledBitmap(gelLogo, 58, 58, true);
                    c.drawBitmap(scaled, x, y, null);
                }

                y += 76;

                c.drawText("GDiolitsis Engine Lab (GEL)", x, y, title);
                y += 20;

                c.drawText("GEL Service Report / Αναφορά Service", x, y, subtitle);
                y += 28;
            }

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

            showEmptyReportOptionsDialog();
            return null;
        }

        return report;
    }
    
    private Uri savePdfToDownloads(String fileName, byte[] data) throws Exception {

    ContentValues values = new ContentValues();
    values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
    values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
    values.put(MediaStore.Downloads.IS_PENDING, 1);

    ContentResolver resolver = getContentResolver();

    Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

    if (uri == null) {
        throw new Exception("MediaStore insert failed");
    }

    OutputStream out = resolver.openOutputStream(uri);

    if (out == null) {
        throw new Exception("OutputStream null");
    }

    out.write(data);
    out.flush();
    out.close();

    values.clear();
    values.put(MediaStore.Downloads.IS_PENDING, 0);
    resolver.update(uri, values, null, null);

    // 🔥 FORCE VISIBILITY (για όλες τις συσκευές)
    try {
        MediaScannerConnection.scanFile(
                this,
                new String[]{fileName},
                new String[]{"application/pdf"},
                null
        );
    } catch (Throwable ignore) {}

    return uri;
}

private void sharePdf(Uri uri) {
	
	boolean gr = AppLang.isGreek(this);

    if (uri == null) return;

    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("application/pdf");
    intent.putExtra(Intent.EXTRA_STREAM, uri);
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

    try {
    startActivity(Intent.createChooser(intent,
            gr ? "Αποστολή αναφοράς μέσω..." : "Send report via..."));
} catch (Throwable t) {
        Toast.makeText(this, "No app available to share PDF", Toast.LENGTH_SHORT).show();
    }
}


    @Override
    protected void onDestroy() {
        if (billingManager != null) {
            billingManager.close();
            billingManager = null;
        }
        super.onDestroy();
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
