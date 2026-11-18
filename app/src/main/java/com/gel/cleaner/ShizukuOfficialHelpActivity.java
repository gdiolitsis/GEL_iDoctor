package com.gel.cleaner;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * In-app instructions and official Shizuku GitHub releases.
 *
 * Do not show the official landing page with four different providers:
 * it was confusing for beginners and some choices failed in real-device QA.
 * Never silently install APKs, grant ADB permissions, follow an arbitrary
 * download host, or style a system-default light AlertDialog in the dark UI.
 */
public final class ShizukuOfficialHelpActivity extends GELAutoActivityHook {
    public static final String EXTRA_DOWNLOAD = "gel_shizuku_official_download";

    private static final String HOME = "https://shizuku.rikka.app";
    private static final String GITHUB_RELEASES =
            "https://github.com/RikkaApps/Shizuku/releases/latest";
    private static final String RELEASE_PATH_PREFIX = "/RikkaApps/Shizuku/releases/";

    private WebView webView;
    private TextView hint;
    private boolean isDownloadMode;
    private boolean gr;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        gr = AppLang.isGreek(this);
        isDownloadMode = getIntent() != null
                && getIntent().getBooleanExtra(EXTRA_DOWNLOAD, false);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(0xFF0A0A0A);
        layout.setPadding(dp(10), dp(8), dp(10), dp(8));

        TextView title = new TextView(this);
        title.setText(isDownloadMode
                ? (gr ? "Λήψη Shizuku — επίσημο GitHub"
                      : "Download Shizuku — official GitHub")
                : (gr ? "Οδηγίες Shizuku — μέσα στο iDoctor"
                      : "Shizuku setup guide — inside iDoctor"));
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        hint = new TextView(this);
        hint.setText(isDownloadMode
                ? (gr ? "Στην τελευταία επίσημη έκδοση πάτησε Assets "
                          + "και διάλεξε το αρχείο Shizuku .apk. "
                          + "Για την εγκατάσταση θα ανοίξει ο περιηγητής "
                          + "και ο εγκαταστάτης του Android. Με το Πίσω "
                          + "επιστρέφεις στον οδηγό του iDoctor."
                      : "In the latest official release, expand Assets and "
                          + "select the Shizuku .apk file. Download and installation "
                          + "require your browser and Android's installer. "
                          + "Press Back to return to the iDoctor guide.")
                : (gr ? "Αυτές είναι οι επίσημες οδηγίες. "
                          + "Για τις ρυθμίσεις Android επιστρέφεις στον οδηγό του iDoctor."
                      : "These are the official instructions. Return to "
                          + "the iDoctor guide to open Android Settings."));
        hint.setTextColor(Color.WHITE);
        hint.setTextSize(14f);
        hint.setPadding(0, dp(8), 0, dp(8));
        layout.addView(hint);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.WHITE); // website content, not a permission dialog
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(false);
        webView.getSettings().setDomStorageEnabled(false);
        webView.getSettings().setSupportMultipleWindows(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setMixedContentMode(
                android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        webView.getSettings().setSafeBrowsingEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view,
                    WebResourceRequest request) {
                return route(request == null ? null : request.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return route(url == null ? null : Uri.parse(url));
            }
            @Override public void onReceivedError(WebView view,
                    WebResourceRequest request, android.webkit.WebResourceError error) {
                if (request != null && request.isForMainFrame() && hint != null) {
                    hint.setText(gr
                            ? "Δεν φορτώθηκε η σελίδα. Χρησιμοποίησε το κουμπί "
                              + "«Άνοιγμα στον περιηγητή» ή δοκίμασε αργότερα."
                            : "Page unavailable. Use Open in browser below or try again.");
                }
            }
        });
        webView.setDownloadListener((url, agent, disposition, type, length) ->
                onWebDownload(url == null ? null : Uri.parse(url)));
        layout.addView(webView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        if (isDownloadMode) {
            Button browser = new GelButton(this);
            browser.setAllCaps(false);
            browser.setText(gr ? "ΑΝΟΙΓΜΑ ΕΠΙΣΗΜΟΥ GITHUB ΣΤΟΝ ΠΕΡΙΗΓΗΤΗ"
                    : "OPEN OFFICIAL GITHUB IN BROWSER");
            browser.setTextColor(Color.WHITE);
            browser.setBackgroundResource(R.drawable.gel_btn_outline_selector);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(52));
            lp.topMargin = dp(6);
            layout.addView(browser, lp);
            browser.setOnClickListener(v -> openExternal(Uri.parse(GITHUB_RELEASES)));
        }

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

        // Skip Google Play / Coolapk / extra repository choices entirely.
        webView.loadUrl(isDownloadMode ? GITHUB_RELEASES : HOME + "/guide/setup/");
    }

    private boolean isHttps(Uri uri) {
        return uri != null && "https".equalsIgnoreCase(uri.getScheme())
                && uri.getUserInfo() == null && uri.getPort() == -1;
    }

    private boolean isTrustedGithubRelease(Uri uri) {
        if (!isHttps(uri) || !"github.com".equalsIgnoreCase(uri.getHost()))
            return false;
        String path = uri.getPath();
        return path != null
                && (path.equalsIgnoreCase("/RikkaApps/Shizuku/releases")
                    || path.toLowerCase(java.util.Locale.US).startsWith(
                            RELEASE_PATH_PREFIX.toLowerCase(java.util.Locale.US)));
    }

    private boolean isTrustedApkLink(Uri uri) {
        if (!isTrustedGithubRelease(uri)) return false;
        String path = uri.getPath();
        return path != null
                && path.toLowerCase(java.util.Locale.US).startsWith(
                        (RELEASE_PATH_PREFIX + "download/").toLowerCase(
                                java.util.Locale.US))
                && path.toLowerCase(java.util.Locale.US).endsWith(".apk");
    }

    private boolean route(Uri uri) {
        if (uri == null) return true;
        if (isTrustedApkLink(uri)) {
            showDownloadConfirmation(uri);
            return true;
        }
        if (isHttps(uri) && "shizuku.rikka.app".equalsIgnoreCase(uri.getHost()))
            return false;
        if (isTrustedGithubRelease(uri))
            return false; // newest official GitHub release stays inside iDoctor
        return true; // never follow third-party APK hosts or unsafe URI schemes
    }

    private void onWebDownload(Uri uri) {
        if (isTrustedApkLink(uri)) {
            showDownloadConfirmation(uri);
        } else if (isDownloadMode && hint != null) {
            hint.setText(gr
                    ? "Για την ασφαλή λήψη άνοιξε την επίσημη έκδοση "
                      + "στον περιηγητή από το κουμπί παρακάτω."
                    : "To download safely, use the official GitHub browser button below.");
        }
    }

    /** Black/gold GEL confirmation replaces the unreadable native white modal.
     * Only verified official GitHub Shizuku APK links reach this method. */
    private void showDownloadConfirmation(Uri officialApk) {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(18), dp(16), dp(18), dp(16));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.BLACK);
        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(2), 0xFFFFD700);
        panel.setBackground(bg);

        TextView title = new TextView(this);
        title.setText(gr ? "Επίσημη λήψη Shizuku" : "Official Shizuku download");
        title.setTextSize(19f);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        panel.addView(title);

        TextView message = new TextView(this);
        message.setText(gr
                ? "Θα ανοίξει ο περιηγητής για το αρχείο Shizuku APK "
                  + "της επίσημης έκδοσης GitHub. "
                  + "Η εγκατάσταση απαιτεί δική σου έγκριση στο Android. "
                  + "Με το Πίσω επιστρέφεις στο iDoctor."
                : "Your browser will open the official Shizuku APK on GitHub. "
                  + "You must approve installation in Android. "
                  + "Press Back to return to iDoctor.");
        message.setTextSize(15f);
        message.setTextColor(Color.WHITE);
        message.setPadding(0, dp(14), 0, dp(14));
        panel.addView(message);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        Button cancel = new GelButton(this);
        cancel.setText(gr ? "ΑΚΥΡΟ" : "CANCEL");
        cancel.setTextColor(Color.WHITE);
        cancel.setBackgroundColor(0xFF444444);
        Button open = new GelButton(this);
        open.setText(gr ? "ΛΗΨΗ" : "DOWNLOAD");
        open.setTextColor(Color.BLACK);
        open.setBackgroundColor(0xFF39FF14);
        buttons.addView(cancel, new LinearLayout.LayoutParams(0, dp(54), 1f));
        buttons.addView(open, new LinearLayout.LayoutParams(0, dp(54), 1f));
        panel.addView(buttons);

        AlertDialog dialog = new AlertDialog.Builder(this,
                android.R.style.Theme_Material_Dialog_NoActionBar)
                .setView(panel).create();
        cancel.setOnClickListener(v -> dialog.dismiss());
        open.setOnClickListener(v -> {
            dialog.dismiss();
            openExternal(officialApk);
        });
        dialog.show();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
    }

    private void openExternal(Uri uri) {
        if (!isTrustedGithubRelease(uri)) return;
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri)
                    .addCategory(Intent.CATEGORY_BROWSABLE));
        } catch (ActivityNotFoundException | SecurityException unavailable) {
            if (hint != null)
                hint.setText(gr ? "Δεν βρέθηκε εφαρμογή περιήγησης για τη λήψη."
                        : "No browser available for the download.");
        }
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
