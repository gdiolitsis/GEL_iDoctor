package com.gel.cleaner;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AlertDialog;
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
            showInvalidSourceDialog();
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

        // An OEM-themed Button may draw the red surface but suppress its text.
        // Use an explicit GEL TextView action so its white label is visible
        // independently of manufacturer button/text-appearance themes.
        TextView back = new TextView(this);
        final String returnLabel = gr
                ? "← ΕΠΙΣΤΡΟΦΗ ΣΤΗΝ ΑΡΧΙΚΗ APPLE"
                : "← RETURN TO APPLE HOME";
        back.setText(returnLabel);
        back.setContentDescription(returnLabel);
        back.setTextColor(Color.WHITE);
        back.setTextSize(16f);
        back.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        back.setGravity(Gravity.CENTER);
        back.setIncludeFontPadding(true);
        back.setPadding(dp(10), dp(6), dp(10), dp(6));
        back.setMinHeight(dp(54));
        back.setClickable(true);
        back.setFocusable(true);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFFB71C1C);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(2), 0xFFFF5252);
        back.setBackground(bg);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(dp(6), 0, dp(6), dp(6));
        root.addView(back, buttonParams);
        back.setOnClickListener(v -> returnToAppleHome());

        TextView source = new TextView(this);
        source.setText("support.apple.com"
                + (model == null || model.trim().isEmpty() ? "" : " • " + model));
        source.setTextColor(0xFF00FF9C);
        source.setTextSize(13f);
        source.setGravity(Gravity.CENTER);
        source.setPadding(dp(2), dp(3), dp(2), dp(5));
        root.addView(source);

        // All loading/navigation warnings stay inside the GEL screen;
        // no light OEM Toast or external browser.
        TextView status = new TextView(this);
        status.setTextColor(0xFFFF7777);
        status.setTextSize(14f);
        status.setGravity(Gravity.CENTER);
        status.setVisibility(View.GONE);
        status.setPadding(dp(8), dp(6), dp(8), dp(8));
        root.addView(status);

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
                status.setText(gr
                        ? "Μόνο επίσημοι σύνδεσμοι support.apple.com ανοίγουν εδώ."
                        : "Only official support.apple.com links open here.");
                status.setVisibility(View.VISIBLE);
                return true; // No external browser, intent or untrusted host.
            }

            @Override public void onReceivedError(WebView view,
                    WebResourceRequest request, WebResourceError error) {
                if (request != null && request.isForMainFrame()) {
                    status.setText(gr
                            ? "Δεν φορτώθηκε η σελίδα Apple. Έλεγξε τη σύνδεση."
                            : "Apple page did not load. Check your connection.");
                    status.setVisibility(View.VISIBLE);
                }
            }
        });

        root.addView(webView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(root);

        // Deliberately do not navigate WebView history: one Back always
        // returns to the Apple home screen with the selected model intact.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { returnToAppleHome(); }
        });
        webView.loadUrl(url);
    }

    private void showInvalidSourceDialog() {
        boolean gr = AppLang.isGreek(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(16), dp(16), dp(16));
        GradientDrawable panel = new GradientDrawable();
        panel.setColor(0xFF080808);
        panel.setCornerRadius(dp(12));
        panel.setStroke(dp(2), 0xFFFFD700);
        box.setBackground(panel);

        TextView message = new TextView(this);
        message.setText(gr ? "Ο σύνδεσμος δεν είναι επαληθευμένη πηγή Apple."
                           : "The link is not a verified Apple source.");
        message.setTextColor(Color.WHITE);
        message.setTextSize(16f);
        message.setPadding(dp(8), dp(8), dp(8), dp(14));
        box.addView(message);

        TextView exit = new TextView(this);
        exit.setText(gr ? "ΕΞΟΔΟΣ" : "EXIT");
        exit.setContentDescription(gr ? "ΕΞΟΔΟΣ" : "EXIT");
        exit.setTextColor(Color.WHITE);
        exit.setTextSize(16f);
        exit.setGravity(Gravity.CENTER);
        exit.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        exit.setPadding(dp(10), dp(8), dp(10), dp(8));
        exit.setMinHeight(dp(54));
        exit.setClickable(true);
        exit.setFocusable(true);
        GradientDrawable red = new GradientDrawable();
        red.setColor(0xFFB71C1C);
        red.setCornerRadius(dp(10));
        red.setStroke(dp(2), 0xFFFF5252);
        exit.setBackground(red);
        box.addView(exit, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        AlertDialog dialog = new AlertDialog.Builder(this).setView(box).create();
        exit.setOnClickListener(v -> { dialog.dismiss(); finish(); });
        dialog.setOnCancelListener(ignored -> finish());
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        GelButtonLayout.fitDialog(dialog);
    }

    private void returnToAppleHome() {
        // Works whether the source was the main Apple library or Lab 3.
        // Never lose the saved iPhone model or send the user to a browser.
        Intent home = new Intent(this, MainActivity.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        home.putExtra("skip_welcome_once", true);
        startActivity(home);
        finish();
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
