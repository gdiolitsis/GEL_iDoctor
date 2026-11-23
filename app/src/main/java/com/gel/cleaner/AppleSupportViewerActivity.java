package com.gel.cleaner;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

/**
 * First-party in-app viewer for Apple troubleshooting documentation.
 * No external browser or app is launched. A single Android/system/UI back
 * closes this viewer and returns to the preserved Apple MainActivity screen.
 */
public final class AppleSupportViewerActivity extends GELAutoActivityHook {
    public static final String EXTRA_SOURCE_URL = "apple_support_source_url";
    public static final String EXTRA_MODEL = "apple_support_selected_model";

    private WebView webView;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        String url = getIntent() == null ? null
                : getIntent().getStringExtra(EXTRA_SOURCE_URL);
        if (!AppleTroubleshootingLibrary.isOfficialUrl(url)) {
            Toast.makeText(this, AppLang.isGreek(this)
                            ? "Μη επαληθευμένη διεύθυνση Apple."
                            : "Unverified Apple source URL.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final boolean gr = AppLang.isGreek(this);
        final String model = getIntent().getStringExtra(EXTRA_MODEL);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF101010);
        root.setPadding(dp(8), dp(8), dp(8), dp(8));

        TextView title = new TextView(this);
        title.setText("GEL iDoctor • " + (gr ? "Επίσημη Apple" : "Official Apple"));
        title.setTextColor(0xFFFFD700);
        title.setTextSize(18f);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(dp(4), dp(8), dp(4), dp(6));
        root.addView(title);

        Button back = new Button(this);
        back.setText(gr ? "← ΠΙΣΩ ΣΤΟ iPHONE" : "← BACK TO IPHONE");
        back.setAllCaps(false);
        back.setTextColor(0xFF00FF9C);
        back.setTextSize(16f);
        back.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF111111);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(2), 0xFFFFD700);
        back.setBackground(bg);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(54));
        buttonParams.setMargins(dp(6), 0, dp(6), dp(6));
        root.addView(back, buttonParams);
        back.setOnClickListener(v -> finish());

        TextView source = new TextView(this);
        source.setText("support.apple.com"
                + (model == null || model.trim().isEmpty() ? "" : " • " + model));
        source.setTextColor(0xFF00FF9C);
        source.setTextSize(13f);
        source.setGravity(Gravity.CENTER);
        source.setPadding(dp(2), dp(3), dp(2), dp(5));
        root.addView(source);

        ProgressBar progress = new ProgressBar(this, null,
                android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        root.addView(progress, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(3)));

        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true); // Apple Support pages can use client rendering.
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(true);
        }
        webView.setBackgroundColor(Color.WHITE);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int value) {
                progress.setProgress(value);
                progress.setVisibility(value >= 100 ? View.GONE : View.VISIBLE);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view,
                    WebResourceRequest request) {
                if (request == null || !request.isForMainFrame()) return false;
                String next = request.getUrl() == null
                        ? null : request.getUrl().toString();
                if (AppleTroubleshootingLibrary.isOfficialUrl(next)) return false;
                Toast.makeText(AppleSupportViewerActivity.this,
                        gr ? "Μόνο επίσημοι σύνδεσμοι support.apple.com ανοίγουν εδώ."
                           : "Only official support.apple.com links open here.",
                        Toast.LENGTH_LONG).show();
                return true; // No external browser, intent or untrusted host.
            }

            @Override public void onReceivedError(WebView view,
                    WebResourceRequest request, WebResourceError error) {
                if (request != null && request.isForMainFrame()) {
                    Toast.makeText(AppleSupportViewerActivity.this,
                            gr ? "Δεν φορτώθηκε η σελίδα Apple. Έλεγξε τη σύνδεση."
                               : "Apple page did not load. Check your connection.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        root.addView(webView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(root);

        // Deliberately do not navigate WebView history: one Back always
        // returns to the Apple home screen with the selected model intact.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { finish(); }
        });
        webView.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            ViewGroup parent = (ViewGroup) webView.getParent();
            if (parent != null) parent.removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
