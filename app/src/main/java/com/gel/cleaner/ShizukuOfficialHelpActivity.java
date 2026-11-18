package com.gel.cleaner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.net.Uri;

/**
 * Official Shizuku help stays INSIDE GEL. No APK is downloaded or installed
 * by GEL. External package installers, Android Settings and the Shizuku
 * manager necessarily require explicit user interaction.
 */
public final class ShizukuOfficialHelpActivity extends GELAutoActivityHook {
    public static final String EXTRA_DOWNLOAD = "gel_shizuku_official_download";
    private static final String HOME = "https://shizuku.rikka.app";
    private WebView webView;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        final boolean gr = AppLang.isGreek(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(0xFF0A0A0A);
        layout.setPadding(dp(10), dp(8), dp(10), dp(8));

        TextView title = new TextView(this);
        title.setText(gr ? "Επίσημες οδηγίες Shizuku — μέσα στο iDoctor"
                : "Official Shizuku instructions — inside iDoctor");
        title.setTextSize(18f);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView hint = new TextView(this);
        hint.setText(gr
                ? "Αν επιλέξεις εγκατάσταση από τον επίσημο ιστότοπο, "
                  + "το Android θα χρειαστεί να ανοίξει τον δικό του εγκαταστάτη. "
                  + "Επιστρέφεις εδώ με το κουμπί Πίσω."
                : "Installation from the official site requires Android's own "
                  + "package installer. Press Back to return here.");
        hint.setTextColor(0xFFE0E0E0);
        hint.setTextSize(13f);
        hint.setPadding(0, dp(8), 0, dp(8));
        layout.addView(hint);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.WHITE);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(false);
        webView.getSettings().setDomStorageEnabled(false);
        webView.getSettings().setSupportMultipleWindows(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setMixedContentMode(
                android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view,
                    WebResourceRequest request) {
                return route(request == null ? null : request.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view,
                    String url) {
                return route(url == null ? null : Uri.parse(url));
            }
        });
        layout.addView(webView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        Button back = new GelButton(this);
        back.setText(gr ? "ΕΠΙΣΤΡΟΦΗ ΣΤΟ IDOCTOR" : "BACK TO IDOCTOR");
        back.setTextColor(Color.BLACK);
        back.setBackgroundColor(0xFF39FF14);
        LinearLayout.LayoutParams footer = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(52));
        footer.topMargin = dp(8);
        layout.addView(back, footer);
        back.setOnClickListener(v -> finish());
        setContentView(layout);

        boolean download = getIntent() != null
                && getIntent().getBooleanExtra(EXTRA_DOWNLOAD, false);
        webView.loadUrl(HOME + (download ? "/download/" : "/guide/setup/"));
    }

    private boolean route(Uri uri) {
        if (uri == null) return true;
        if ("https".equalsIgnoreCase(uri.getScheme())
                && "shizuku.rikka.app".equalsIgnoreCase(uri.getHost())) {
            return false; // only the verified official host remains embedded
        }
        // No intent:, file:, javascript:, market:, tel:, or other schemes.
        if (!"https".equalsIgnoreCase(uri.getScheme())) return true;
        String host = uri.getHost();
        if (host == null || !(host.equalsIgnoreCase("github.com")
                || host.equalsIgnoreCase("apt.izzysoft.de"))) return true;
        final boolean gr = AppLang.isGreek(this);
        new AlertDialog.Builder(this)
                .setTitle(gr ? "Μετάβαση στον επίσημο πάροχο" : "Open official provider")
                .setMessage(gr
                        ? "Αυτή η λήψη απαιτεί να ανοίξει προσωρινά ο περιηγητής "
                          + "ή ο εγκαταστάτης Android. Θα επιστρέψεις στο iDoctor με Πίσω. "
                          + "Εγκατάστησε μόνο την επίσημη έκδοση."
                        : "This download requires a browser or Android package installer. "
                          + "Use Back to return to iDoctor. Install only the official release.")
                .setNegativeButton(gr ? "ΑΚΥΡΟ" : "CANCEL", (d, w) -> { })
                .setPositiveButton(gr ? "ΑΝΟΙΓΜΑ" : "OPEN", (d, w) -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, uri)
                                .addCategory(Intent.CATEGORY_BROWSABLE));
                    } catch (RuntimeException ignored) {
                        // Browser unavailable: keep instructions visible.
                    }
                }).show();
        return true;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
