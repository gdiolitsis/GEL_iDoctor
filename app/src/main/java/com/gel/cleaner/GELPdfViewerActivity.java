// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELPdfViewerActivity — GEL-only PDF viewer for Remote Assist

package com.gel.cleaner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public final class GELPdfViewerActivity
        extends GELAutoActivityHook {

    public static final String EXTRA_PDF_URI =
            "gel_pdf_uri";

    private Uri pdfUri;

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreate(
                savedInstanceState
        );

        String rawUri =
                getIntent() != null
                        ? getIntent().getStringExtra(
                                EXTRA_PDF_URI
                        )
                        : null;

        if (rawUri == null ||
                rawUri.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "PDF URI missing.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        pdfUri =
                Uri.parse(
                        rawUri
                );

        buildScreen();
    }

    private void buildScreen() {

        final boolean gr =
                AppLang.isGreek(
                        this
                );

        LinearLayout root =
                new LinearLayout(
                        this
                );

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setBackgroundColor(
                0xFF101010
        );

        root.setPadding(
                dp(10),
                dp(10),
                dp(10),
                dp(18)
        );

        TextView title =
                new TextView(
                        this
                );

        title.setText(
                "GEL SERVICE REPORT — PDF"
        );

        title.setTextColor(
                0xFFFFD700
        );

        title.setTextSize(
                18f
        );

        title.setGravity(
                Gravity.CENTER
        );

        title.setPadding(
                0,
                dp(6),
                0,
                dp(10)
        );

        root.addView(
                title
        );

        LinearLayout actions =
                new LinearLayout(
                        this
                );

        actions.setOrientation(
                LinearLayout.HORIZONTAL
        );

        actions.setGravity(
                Gravity.CENTER
        );

        Button back =
                makeButton(
                        gr
                                ? "← ΠΙΣΩ"
                                : "← BACK"
                );

        Button share =
                makeButton(
                        gr
                                ? "ΚΟΙΝΟΠΟΙΗΣΗ"
                                : "SHARE"
                );

        LinearLayout.LayoutParams actionLp =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        actionLp.setMargins(
                dp(5),
                0,
                dp(5),
                dp(10)
        );

        actions.addView(
                back,
                actionLp
        );

        actions.addView(
                share,
                actionLp
        );

        root.addView(
                actions
        );

        ScrollView scroll =
                new ScrollView(
                        this
                );

        scroll.setFillViewport(
                true
        );

        LinearLayout pages =
                new LinearLayout(
                        this
                );

        pages.setOrientation(
                LinearLayout.VERTICAL
        );

        pages.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        pages.setPadding(
                dp(4),
                dp(4),
                dp(4),
                dp(44)
        );

        scroll.addView(
                pages,
                new ScrollView.LayoutParams(
                        ScrollView.LayoutParams.MATCH_PARENT,
                        ScrollView.LayoutParams.WRAP_CONTENT
                )
        );

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                )
        );

        setContentView(
                root
        );

        back.setOnClickListener(
                v -> getOnBackPressedDispatcher()
                        .onBackPressed()
        );

        share.setOnClickListener(
                v -> sharePdf()
        );

        renderPdf(
                pages
        );
    }

    private void renderPdf(
            LinearLayout pages
    ) {

        ParcelFileDescriptor descriptor =
                null;

        PdfRenderer renderer =
                null;

        try {

            descriptor =
                    getContentResolver()
                            .openFileDescriptor(
                                    pdfUri,
                                    "r"
                            );

            if (descriptor == null) {
                throw new IllegalStateException(
                        "Could not open PDF."
                );
            }

            renderer =
                    new PdfRenderer(
                            descriptor
                    );

            if (renderer.getPageCount() <= 0) {
                throw new IllegalStateException(
                        "PDF contains no pages."
                );
            }

            int screenWidth =
                    getResources()
                            .getDisplayMetrics()
                            .widthPixels;

            // Keep memory controlled even for long reports.
            int targetWidth =
                    Math.max(
                            320,
                            Math.min(
                                    screenWidth - dp(28),
                                    720
                            )
                    );

            for (
                    int i = 0;
                    i < renderer.getPageCount();
                    i++
            ) {

                PdfRenderer.Page page =
                        renderer.openPage(
                                i
                        );

                try {

                    float ratio =
                            page.getHeight()
                                    / (float) page.getWidth();

                    int targetHeight =
                            Math.max(
                                    1,
                                    Math.round(
                                            targetWidth * ratio
                                    )
                            );

                    Bitmap bitmap =
                            Bitmap.createBitmap(
                                    targetWidth,
                                    targetHeight,
                                    Bitmap.Config.ARGB_8888
                            );

                    bitmap.eraseColor(
                            Color.WHITE
                    );

                    page.render(
                            bitmap,
                            null,
                            null,
                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                    );

                    ImageView image =
                            new ImageView(
                                    this
                            );

                    image.setImageBitmap(
                            bitmap
                    );

                    image.setAdjustViewBounds(
                            true
                    );

                    image.setScaleType(
                            ImageView.ScaleType.FIT_CENTER
                    );

                    image.setBackgroundColor(
                            Color.WHITE
                    );

                    LinearLayout.LayoutParams imageLp =
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

                    imageLp.setMargins(
                            0,
                            0,
                            0,
                            dp(12)
                    );

                    pages.addView(
                            image,
                            imageLp
                    );

                } finally {
                    page.close();
                }
            }

        } catch (Throwable t) {

            TextView error =
                    new TextView(
                            this
                    );

            error.setText(
                    "PDF ERROR: "
                            + (
                            t.getMessage() != null
                                    ? t.getMessage()
                                    : t.getClass().getSimpleName()
                    )
            );

            error.setTextColor(
                    0xFFFF6B6B
            );

            error.setTextSize(
                    15f
            );

            error.setGravity(
                    Gravity.CENTER
            );

            error.setPadding(
                    dp(14),
                    dp(24),
                    dp(14),
                    dp(24)
            );

            pages.addView(
                    error
            );

        } finally {

            if (renderer != null) {
                try {
                    renderer.close();
                } catch (Throwable ignore) {}
            }

            if (descriptor != null) {
                try {
                    descriptor.close();
                } catch (Throwable ignore) {}
            }
        }
    }

    private void sharePdf() {

        if (pdfUri == null) {
            return;
        }

        final boolean gr =
                AppLang.isGreek(
                        this
                );

        Intent intent =
                new Intent(
                        Intent.ACTION_SEND
                );

        intent.setType(
                "application/pdf"
        );

        intent.putExtra(
                Intent.EXTRA_STREAM,
                pdfUri
        );

        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        );

        try {

            startActivity(
                    Intent.createChooser(
                            intent,
                            gr
                                    ? "Αποστολή αναφοράς μέσω..."
                                    : "Send report via..."
                    )
            );

        } catch (Throwable t) {

            Toast.makeText(
                    this,
                    gr
                            ? "Δεν υπάρχει εφαρμογή για κοινοποίηση PDF."
                            : "No app available to share PDF.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private Button makeButton(
            String text
    ) {

        Button button =
                new Button(
                        this
                );

        button.setText(
                text
        );

        button.setTextColor(
                Color.WHITE
        );

        button.setTextSize(
                14f
        );

        button.setAllCaps(
                false
        );

        button.setBackgroundResource(
                R.drawable.gel_btn_outline
        );

        button.setPadding(
                dp(8),
                dp(12),
                dp(8),
                dp(12)
        );

        return button;
    }

    public int dp(
            int value
    ) {

        return Math.round(
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}
