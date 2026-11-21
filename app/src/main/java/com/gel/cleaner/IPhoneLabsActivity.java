// GDiolitsis Engine Lab (GEL) — Author & Developer
// IPhoneLabsActivity.java — iPhone Diagnostics Labs v1.0 FINAL (LOCKED)
// Dark-Gold + Neon Green Edition — Service Grade

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import com.gel.cleaner.UIHelpers;
import com.gel.cleaner.iphone.IPSPanicParser;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IPhoneLabsActivity extends GELAutoActivityHook {
	private boolean panicGuidePopupOpen = false;
	boolean panicGuideShown;
TextView panicGuideTitle;
TextView panicGuideMessage;
	
	private CheckBox muteCheck;
    private CheckBox dontShowCheck;
    
    private ScrollView mainScroll;
    private ScrollView scroll;
    
    private Handler ui = new Handler(Looper.getMainLooper());
    
    private static final int MAX_PANIC_LOG_SIZE = 2_000_000; // ~2MB
	
	// ==========================
    // TTS ENGINE
    // ==========================
    private TextToSpeech[] tts   = new TextToSpeech[1];
    private boolean[]     ttsReady = new boolean[1];

    private final StringBuilder logHtmlBuffer = new StringBuilder();

private boolean looksCorruptedPanic(String text) {

    if (text == null) return true;

    String t = text.toLowerCase(Locale.US);

    if (t.length() < 80) return true;

    // βασικά panic signals (πολύ πιο ασφαλές)
    if (t.contains("panic(")) return false;
    if (t.contains("panicstring")) return false;
    if (t.contains("bug_type")) return false;
    if (t.contains("incident")) return false;
    if (t.contains("watchdog")) return false;
    if (t.contains("thermal")) return false;
    if (t.contains("jetsam")) return false;

    return true;
}

    // ============================================================
    // REQUEST CODES
    // ============================================================
    private static final int REQ_PANIC_LOG = 1011;

    // ============================================================
    // GEL PRO — CENTRAL ENTITLEMENT GATE
    // Same entitlement source used by ManualTestsActivity.
    // ============================================================

    private boolean isGelProActive() {
        return GelProEntitlement.isActive(this);
    }

    private boolean requireGelPro(String featureName) {
        if (isGelProActive()) return true;
        showGelProLockedDialog(featureName);
        return false;
    }

    private void showGelProLockedDialog(String featureName) {
        final boolean gr = AppLang.isGreek(this);
        final String feature = featureName == null ? "GEL PRO" : featureName;

        new AlertDialog.Builder(this)
                .setTitle(gr ? "GEL PRO — Επαγγελματική λειτουργία"
                             : "GEL PRO — Professional Feature")
                .setMessage((gr
                        ? "Η λειτουργία «" + feature + "» είναι διαθέσιμη στο GEL PRO.\n\nΣυνδρομή: 4,99 € / μήνα"
                        : "The feature “" + feature + "” is available with GEL PRO.\n\nSubscription: €4.99 / month"))
                .setNegativeButton(gr ? "Όχι τώρα" : "Not now", null)
                // Temporary until Google Play Billing purchase flow is connected.
                .setPositiveButton("GEL PRO", (d, w) -> startActivity(new android.content.Intent(this, GelProAccountActivity.class).putExtra(GelProAccountActivity.EXTRA_ACCESS_CHOICES, true)))
                .show();
    }


    // ============================================================
    // SAFETY LIMITS (avoid OOM)
    // ============================================================
    private static final int MAX_TEXT_BYTES = 3 * 1024 * 1024; // 3MB read cap
    private static final int ZIP_SCAN_CAP   = 12;              // max entries to scan

    // ============================================================
    // COLORS (MATCH MANUAL TESTS FEEL)
    // ============================================================
    private static final int COLOR_BG         = 0xFF101010;
    private static final int COLOR_WHITE      = 0xFFFFFFFF;
    private static final int COLOR_GRAY       = 0xFFCCCCCC;
    private static final int COLOR_NEON       = 0xFF00FF9C;

    // HTML colors (log lines)
    private static final String H_WHITE = "#FFFFFF";
    private static final String H_NEON  = "#00FF9C";
    private static final String H_OK    = "#88FF88";
    private static final String H_WARN  = "#FFD966";
    private static final String H_ERR   = "#FF5555";
    private static final String H_DIM   = "#B8B8B8";

    // ============================================================
    // STATE (CANONICAL)
    // ============================================================
    // UI-only simulation: do not emit demo observations to real service reports.
    private boolean demoMode = false;
    private boolean panicLogLoaded = false;
    private String  panicLogName   = null;
    private String  panicLogText   = null;

    // ============================================================
    // UI (LIKE MANUAL TESTS — LOG AREA BOTTOM)
    // ============================================================
    private TextView txtLog;
    
    private boolean appendMode = false;
    private boolean panicGuideMuted = false;
    private String  panicGuideLang  = "EN";
    private int panicLogCount = 0;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // ROOT SCROLL
mainScroll = new ScrollView(this);
mainScroll.setLayoutParams(new ScrollView.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
));
mainScroll.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
mainScroll.setFillViewport(true);

        // CONTENT ROOT
        LinearLayout root = new LinearLayout(this);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));
        root.setBackgroundColor(COLOR_BG);

        boolean gr = AppLang.isGreek(this);

// TITLE
TextView title = new TextView(this);
title.setLayoutParams(new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
));

title.setText(gr
        ? "GEL Διαγνωστικά iPhone"
        : "GEL iPhone Diagnostics"
);

title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
title.setTextColor(COLOR_WHITE);
title.setGravity(Gravity.CENTER_HORIZONTAL);
title.setIncludeFontPadding(false);
root.addView(title);

// SUBTITLE
TextView sub = new TextView(this);
sub.setLayoutParams(new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
));

sub.setText(gr
        ? "Εργαστηριακή διάγνωση iPhone μέσω αρχείων συστήματος\n"
          + "Ανάλυση logs επιπέδου service (χωρίς άμεση πρόσβαση στη συσκευή)"
        : "Laboratory diagnostics for iPhone using system files\n"
          + "Service-grade log analysis (no direct device access)"
);

sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
sub.setTextColor(0xFF39FF14);
sub.setGravity(Gravity.CENTER_HORIZONTAL);
sub.setPadding(0, dp(8), 0, dp(18));
sub.setIncludeFontPadding(false);
root.addView(sub);

// ============================================================
// LAB BUTTONS (GUARDED)
// ============================================================

// 0) DEMO BUTTON
root.addView(makeLabButton(
        gr ? "DEMO MODE — Εκτέλεση δοκιμαστικής διάγνωσης"
           : "DEMO MODE — Run diagnostic simulation",
        gr ? "Εκτελεί πλήρη διάγνωση χρησιμοποιώντας ενσωματωμένα panic logs"
           : "Runs full diagnostics using built-in panic logs",
        false,
        v -> runDemoDiagnostics()
));

// 1) Import (replace mode)
View importBtn = makeLabButton(
        gr
                ? (isGelProActive() ? "" : "🔒 ")
                  + "GEL PRO — Εισαγωγή Panic Logs (TXT / ZIP)"
                : (isGelProActive() ? "" : "🔒 ")
                  + "GEL PRO — Panic Log Import (TXT / ZIP)",
        gr ? "Αυτόματη αποσυμπίεση + φόρτωση αναφοράς"
           : "Auto unzip + load panic report",
        false,
        v -> {

            if (!requireGelPro(gr ? "Εισαγωγή Panic Logs (TXT / ZIP)" : "Panic Log Import (TXT / ZIP)")) return;

            // Do not clear existing customer evidence until a valid replacement commits.
            appendMode = false;

            openPanicLogPicker();
        }
);

setButtonTextWhite(importBtn);
root.addView(importBtn);


// 1b) Add more logs (append mode)
View appendBtn = makeLabButton(
        gr
                ? (isGelProActive() ? "" : "🔒 ")
                  + "GEL PRO — Προσθήκη επιπλέον panic logs"
                : (isGelProActive() ? "" : "🔒 ")
                  + "GEL PRO — Add more panic logs",
        gr ? "Προσθήκη logs στην τρέχουσα ανάλυση"
           : "Append logs to current analysis",
        false,
        v -> {
            if (!requireGelPro(gr ? "Προσθήκη επιπλέον panic logs" : "Add more panic logs")) return;
            appendMode = true;
            openPanicLogPicker();
        }
);

setButtonTextWhite(appendBtn);
root.addView(appendBtn);

// 1c) RUN ALL DIAGNOSTICS
root.addView(makeLabButton(
        gr ? "AUTO — Εκτέλεση όλων των εργαστηρίων"
           : "AUTO — Run All Labs",
        gr ? "Πλήρης διάγνωση όλων των panic logs"
           : "Full diagnostics across all panic logs",
        true,
        v -> runAllAppleDiagnostics()
));

// 2) Panic Log Analyzer
root.addView(makeLabButton(
        gr ? "LAB 1 - Ανάλυση Panic Logs"
           : "LAB 1 - Panic Log Analyzer",
        gr ? "Αρχικός έλεγχος crash signatures"
           : "Initial crash signature screening",
        true,
        v -> runPanicLogAnalyzer()
));

// 3) Signature Parser (guard)
root.addView(makeLabButton(
        gr ? "LAB 2 - Ανάλυση Υπογραφής Panic"
           : "LAB 2 - Panic Signature Parser",
        gr ? "Τύπος Crash • Domain • Βεβαιότητα • Τεκμηρίωση"
           : "Crash Type • Domain • Confidence • Evidence",
        true,
        v -> runPanicSignatureParser()
));

// 4) Stability (guard)
root.addView(makeLabButton(
        gr ? "LAB 3 - Αξιολόγηση Ιστορικού Συμβάντων"
           : "LAB 3 - Incident History Review",
        gr ? "Εξέταση καταγεγραμμένων συμβάντων, όχι μέτρηση σταθερότητας"
           : "Review recorded incidents, not a live stability measurement",
        true,
        v -> runStabilityLab()
));

// 5) Impact (guard)
root.addView(makeLabButton(
        gr ? "LAB 4 - Ανάλυση Επιπτώσεων"
           : "LAB 4 - Impact Analysis",
        gr ? "Κατηγορίες ενδείξεων ανά log, όχι διάγνωση εξαρτήματος"
           : "Subsystem clues per log, not component diagnosis",
        true,
        v -> runImpactLab()
));

// 6) Frequency
root.addView(makeLabButton(
        gr ? "LAB 5 - Ανάλυση Συχνότητας Panic"
           : "LAB 5 - Panic Frequency Analyzer",
        gr ? "Συχνότητα επαναλαμβανόμενων crash types"
           : "Repeated crash type frequency",
        true,
        v -> runPanicFrequencyLab()
));

// 7) Clustering
root.addView(makeLabButton(
        gr ? "LAB 6 - Ομαδοποίηση Domain Panic"
           : "LAB 6 - Panic Domain Clustering",
        gr ? "Εντοπισμός επαναλαμβανόμενου hardware domain"
           : "Detect recurring hardware domain",
        true,
        v -> runPanicClusteringLab()
));

// 8) Recurring Domain
root.addView(makeLabButton(
        gr ? "LAB 7 - Επαναλαμβανόμενο Domain"
           : "LAB 7 - Recurring Domain Detection",
        gr ? "Ανίχνευση κυρίαρχου hardware pattern"
           : "Detect dominant hardware crash pattern",
        true,
        v -> runRecurringDomainLab()
));

// 9) FINAL Service Recommendation
root.addView(makeLabButton(
        gr ? "LAB 8 - Τελική Σύσταση Service"
           : "LAB 8 - Final Service Recommendation",
        gr ? "Σύντομο τεχνικό συμπέρασμα και επόμενοι έλεγχοι"
           : "Concise evidence summary and next checks",
        true,
        v -> runFinalServiceRecommendationLab()
));

        // ============================================================
        // LOG AREA (BOTTOM) — LIKE MANUAL TESTS
        // ============================================================

    // ============================================================  
    // LOG AREA  
    // ============================================================  
txtLog = new TextView(this);
txtLog.setTextSize(13f);
txtLog.setTextColor(0xFFEEEEEE);
txtLog.setPadding(0, dp(16), 0, dp(8));
txtLog.setMovementMethod(new ScrollingMovementMethod());
txtLog.setText(Html.fromHtml("<b>" + getString(R.string.manual_log_title) + "</b><br>"));

// disable press behaviour
txtLog.setClickable(false);
txtLog.setFocusable(false);
txtLog.setLongClickable(false);
txtLog.setSoundEffectsEnabled(false);
txtLog.setHapticFeedbackEnabled(false);
txtLog.setBackground(null);

root.addView(txtLog);
    
    appendHtml("<br>");
logLine();

// Startup guidance belongs to the UI only. Navigating into iPhone Labs
// must never append repeated headers to a customer's service report.
appendHtml(escape(gr
        ? "ℹ️ GEL iPhone Labs — έτοιμο."
        : "ℹ️ GEL iPhone Labs — ready."));

logLine();

appendHtml(escape(gr
        ? "ℹ️ Για πραγματική ανάλυση εισήγαγε panic log· για δοκιμή επίλεξε DEMO."
        : "ℹ️ Import a panic log for real analysis, or select DEMO to explore the labs."));

 // ============================================================
// EXPORT SERVICE REPORT BUTTON (iPhone Labs)
// ============================================================
Button btnExport = new com.gel.cleaner.GelButton(this);
btnExport.setText(getString(R.string.export_report_title));
btnExport.setAllCaps(false);
btnExport.setBackgroundResource(R.drawable.gel_btn_outline_selector);
btnExport.setTextColor(0xFFFFFFFF);

// Font-scale safe: allow the button to grow vertically
// instead of clipping its label at a fixed 52dp height.
btnExport.setGravity(Gravity.CENTER);
btnExport.setSingleLine(false);
btnExport.setIncludeFontPadding(true);
btnExport.setMinHeight(dp(72));
btnExport.setMinimumHeight(dp(72));
btnExport.setPadding(dp(16), dp(12), dp(16), dp(12));

LinearLayout.LayoutParams lpExp =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
lpExp.setMargins(dp(4), dp(12), dp(4), dp(20));
btnExport.setLayoutParams(lpExp);

btnExport.setOnClickListener(v -> {
    Intent i = new Intent(this, ServiceReportActivity.class);
    startActivity(i);
});

root.addView(btnExport);

mainScroll.addView(root);
setContentView(mainScroll);
scroll = mainScroll;

UIHelpers.applyPressEffectRecursive(root);

// popup AFTER layout ready
root.post(() -> {
    if (!isPanicGuideHidden()) {
        showPanicGuidePopup();
    }
});

// ==========================
// TTS INIT
// ==========================
tts[0] = new TextToSpeech(this, status -> {
    if (status == TextToSpeech.SUCCESS) {
        ttsReady[0] = true;

        if (panicGuidePopupOpen && !panicGuideMuted) {
            speakPanicGuideTTS();
        }

    } else {
        ttsReady[0] = false;
    }
});

// ============================================================
// SERVICE LOG — SECTION HEADER (iPhone Labs)
// ============================================================
// An iPhone report section is generated by actual imported diagnostic
// findings, never by merely opening this screen (or running DEMO).

} // onCreate ends here

/**
 * Show the source-reported device identity before the panic Labs.
 * Do not present one common iPhone model for files with inconsistent
 * identifiers or records that do not provide a model field.
 */
private void logPanicModelIdentity(String importedLogs, boolean gr) {
    ApplePanicModelEvidence.Result models = ApplePanicModelEvidence.analyze(importedLogs);
    if (models.identifiers.isEmpty()) return; // no model observation to report

    if (models.hasConsistentModel()) {
        String identifier = models.identifiers.keySet().iterator().next();
        logLabelOkValue(
                gr ? "Μοντέλο σύμφωνα με τα panic logs:" : "Model reported in panic logs:",
                ApplePanicModelEvidence.displayName(identifier));
    } else if (models.identifiers.size() == 1 && !models.conflictingFields) {
        String identifier = models.identifiers.keySet().iterator().next();
        logLabelValue(
                gr ? "Μοντέλο σε μέρος των αρχείων:" : "Model in some imported files:",
                ApplePanicModelEvidence.displayName(identifier) + " ("
                        + models.recordsWithModel + "/" + models.eligibleRecords + ")");
    } else {
        logLabelWarnValue(gr ? "Διαφορετικοί κωδικοί συσκευής:" : "Conflicting device identifiers:",
                android.text.TextUtils.join(", ", models.identifiers.keySet()));
    }
    logInfo(gr
            ? "Τα στοιχεία μοντέλου προέρχονται από τα αρχεία και δεν επαληθεύουν την ταυτότητα της φυσικής συσκευής."
            : "Model details come from imported files; they do not authenticate the physical handset.");
}

private String detectDeviceType(String text) {

    if (text == null) return "Unknown";

    String t = text.toLowerCase(Locale.US);

    if (t.contains("iphone")) return "iPhone";
    if (t.contains("ipad")) return "iPad";

    if (t.contains("baseband") || t.contains("commcenter"))
        return "iPhone";

    return "Apple Device";
}

private String extractModelIdentifier(String text) {

    if (text == null) return "Unknown";

    Pattern p = Pattern.compile("(iphone\\d+,\\d+|ipad\\d+,\\d+)", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(text);

    if (m.find()) {
        return m.group(1);
    }

    return "Unknown";
}

private String extractKernelVersion(String text) {

    if (text == null) return "Unknown";

    Pattern p = Pattern.compile("Darwin Kernel Version ([0-9.]+)", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(text);

    if (m.find()) return m.group(1);

    return "Unknown";
}

private String extractBoardId(String text) {

    if (text == null) return "Unknown";

    Pattern p = Pattern.compile("(?:board[-_ ]?id|model)[:= ]+([A-Z0-9]+AP)", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(text);

    if (m.find()) return m.group(1);

    return "Unknown";
}

private void runAllAppleDiagnostics() {

    if (!panicLogLoaded) {
        logWarn(AppLang.isGreek(this)
                ? "Δεν έχουν φορτωθεί panic logs."
                : "No panic logs loaded.");
        return;
    }

    new Thread(() -> {

appendHtml("<br>");
logInfo(AppLang.isGreek(this)
        ? "ΕΚΤΕΛΕΣΗ ΟΛΩΝ ΤΩΝ ΕΡΓΑΣΤΗΡΙΩΝ"
        : "RUN ALL LABS");

try {

    runPanicLogAnalyzer();
    SystemClock.sleep(100);

    runPanicSignatureParser();
    SystemClock.sleep(100);

    runStabilityLab();
    SystemClock.sleep(100);

    runImpactLab();
    SystemClock.sleep(100);

    runPanicFrequencyLab();
    SystemClock.sleep(100);

    runPanicClusteringLab();
    SystemClock.sleep(100);

    runRecurringDomainLab();
    SystemClock.sleep(100);

    runFinalServiceRecommendationLab();

    int comparableLogs = countSupportedPanicBlocks(
            panicLogText.split("(?m)^===== ZIP FILE:"));
    if (comparableLogs >= 2) {
        logOk(AppLang.isGreek(this)
                ? "Ο πλήρης έλεγχος ολοκληρώθηκε."
                : "Full diagnostics completed.");
    } else {
        logOk(AppLang.isGreek(this)
                ? "Η ανάλυση του διαθέσιμου panic log ολοκληρώθηκε."
                : "Available panic log analysis completed.");
        logInfo(AppLang.isGreek(this)
                ? "Τα συγκριτικά Labs 5–7 δεν εκτελέστηκαν: απαιτούνται τουλάχιστον δύο αναγνωρισμένα panic logs."
                : "Comparative Labs 5–7 were not executed: at least two eligible panic logs are required.");
    }

} catch (Throwable t) {

    logError(AppLang.isGreek(this)
            ? "Αποτυχία εκτέλεσης ελέγχου: " + t.getMessage()
            : "Diagnostics failed: " + t.getMessage());
}

    }).start();
}

private void disablePanicGuideForever() {

    SharedPreferences prefs =
            getSharedPreferences("gel_prefs", MODE_PRIVATE);

    prefs.edit()
            .putBoolean("panic_guide_hidden", true)
            .apply();
}

private Button mkRedBtn(String t) {

    Button b = new com.gel.cleaner.GelButton(this);
    b.setText(t);
    b.setTextColor(Color.WHITE);

    GradientDrawable d = new GradientDrawable();
    d.setColor(0xFFC62828);
    d.setStroke(5,0xFFFFD700);
    d.setCornerRadius(3);

    b.setBackground(d);

    return b;
}

private String buildDemoPanicLogs() {

    String log1 =
            "===== ZIP FILE: PanicLog1 =====\n" +
            "panic(cpu 2 caller 0xfffffff01a2c3d44): Kernel panic: watchdog timeout\n" +
            "Debugger message: panic\n" +
            "OS version: iPhone OS 17.1.2 (21B101)\n" +
            "Kernel version: Darwin Kernel Version 23.1.0\n" +
            "\n" +
            "Backtrace:\n" +
            "0xfffffff01a2c3d44\n" +
            "0xfffffff01a1f8e30\n" +
            "0xfffffff01a1f8c00\n" +
            "\n" +
            "Panicked task: watchdogd\n" +
            "Boot args: -v\n" +
            "\n" +
            "System uptime in nanoseconds: 18446744073709551615\n" +
            "Last reboot reason: watchdog\n" +
            "\n" +
            "Hardware model: iPhone14,3\n" +
            "Baseband version: 3.20.01\n";

    String log2 =
            "===== ZIP FILE: PanicLog2 =====\n" +
            "{\"bug_type\":\"210\",\"timestamp\":\"2026-01-13 11:42:03.00 +0200\",\"os_version\":\"iPhone OS 17.1.2 (21B101)\",\"incident_id\":\"C7F1A1B2-3344-4D11-9E02-AAA123BBB999\"}\n" +
            "\n" +
            "{\"crashReporterKey\":\"9f3a7c2b1d\",\"deviceModel\":\"iPhone14,3\",\"process\":\"kernel\",\"process_id\":0}\n" +
            "\n" +
            "{\"panicString\":\"panic(cpu 0 caller 0xfffffff01b22aa90): thermal shutdown\"}\n" +
            "\n" +
            "{\"confidence\":\"0.67\",\"domain\":\"Thermal\",\"reason\":\"Overtemperature condition detected\"}\n" +
            "\n" +
            "{\"uptime\":\"2h34m21s\",\"shutdownCause\":\"thermal\"}\n";

    return log1 + "\n" + log2;
}

private String normalizeDomain(String domain) {

    if (domain == null) return "Unknown";

    String d = domain.toLowerCase(Locale.US);

    if (d.contains("baseband")) return "Baseband";
    if (d.contains("nand") || d.contains("storage") || d.contains("nvme") || d.contains("apfs"))
        return "Storage";
    if (d.contains("power") || d.contains("pmic") || d.contains("brownout"))
        return "Power";
    if (d.contains("thermal"))
        return "Thermal";
    if (d.contains("gpu") || d.contains("agx"))
        return "GPU";
    if (d.contains("memory") || d.contains("jetsam"))
        return "Memory";
    if (d.contains("sensor") || d.contains("camera") || d.contains("touch"))
        return "Sensors";

    return domain;
}

private boolean isPanicGuideHidden() {
    try {
        SharedPreferences prefs =
                getSharedPreferences("gel_prefs", MODE_PRIVATE);
        return prefs.getBoolean("panic_guide_hidden", false);
    } catch (Throwable ignore) {
        return false;
    }
}

private void setPanicGuideHidden(boolean hidden) {
    try {
        SharedPreferences prefs =
                getSharedPreferences("gel_prefs", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("panic_guide_hidden", hidden)
                .apply();
    } catch (Throwable ignore) {}
}

private LinearLayout buildMuteRow() {

    final boolean gr = AppLang.isGreek(this);

    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER_VERTICAL);
    row.setPadding(0, dp(8), 0, dp(10));

    muteCheck = new CheckBox(this);
    muteCheck.setChecked(AppTTS.isMuted(this));
    muteCheck.setPadding(0, 0, dp(6), 0);

    TextView label = new TextView(this);
    label.setText(gr
            ? "Σίγαση φωνητικών οδηγιών"
            : "Mute voice instructions");

    label.setTextColor(Color.WHITE);
    label.setTextSize(14f);

View.OnClickListener toggle = v -> {

    boolean newState = !AppTTS.isMuted(this);

    AppTTS.setMuted(this, newState);

    if (muteCheck.isChecked() != newState) {
        muteCheck.setChecked(newState);
    }

    if (newState) {
        try { AppTTS.stop(); } catch (Throwable ignore) {}
    }
};

    label.setOnClickListener(toggle);

    muteCheck.setOnCheckedChangeListener((button, checked) -> {

        if (checked == AppTTS.isMuted(this)) return;

        AppTTS.setMuted(this, checked);

        if (checked) {
            try { AppTTS.stop(); } catch (Throwable ignore) {}
        }
    });

    row.addView(muteCheck);
    row.addView(label);

    return row;
}

private void resetSignatureCache() {
    sigCrashType  = "Unknown";
    sigDomain     = "Unknown";
    sigConfidence = "Low";
}

@Override
protected void onPause() {
    try {
        if (tts != null && tts[0] != null) {
            tts[0].stop();   // 🔇 stop όταν φεύγουμε από την οθόνη
        }
    } catch (Throwable ignore) {}
    super.onPause();
}

@Override
protected void onDestroy() {
    super.onDestroy();
    try {
        if (tts != null && tts[0] != null) {
            tts[0].stop();
            tts[0].shutdown();
        }
    } catch (Throwable ignore) {}
}

// ============================================================
// TOAST (VISIBLE GUARD MESSAGE)
// ============================================================
private void toast(String msg) {
    try { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
    catch (Throwable ignore) {}
}

// =========================================================
// TTS - PANIC LOG IMPORT GUIDE
// =========================================================
private void speakPanicGuideTTS() {

if (!panicGuideShown) return;
if (AppTTS.isMuted(this)) return;

if (AppLang.isGreek(this)) {

AppTTS.speak(
this,
getPanicGuideTextGR()
);

} else {

AppTTS.speak(
this,
getPanicGuideTextEN()
);
}
}

// =========================================================
// PANIC GUIDE TEXT
// =========================================================
private String getPanicGuideTextEN() {
return
"To analyze iPhone stability, system logs must be imported.\n\n" +

        "Where to find them on iPhone:\n" +
        "Settings → Privacy & Security → Analytics & Improvements → Analytics Data\n\n" +

        "Look for files named:\n" +
        "• panic-full-xxxx.log\n" +
        "• panic-base-xxxx.log\n" +
        "• system-xxxx.ips\n\n" +

        "How to export:\n" +
        "Open a file → Share → Save to Files or Send via Email\n" +
        "Export all available files.\n\n" +

        "In this app:\n" +
        "Press Import and select all log files.\n" +
        "The app analyzes them together\n" +
        "to detect stability patterns.\n\n" +

        "Tip:\n" +
        "More logs improve diagnostic accuracy.";
}

private String getPanicGuideTextGR() {
return
"Για την ανάλυση σταθερότητας του iPhone, απαιτείται εισαγωγή αρχείων καταγραφής.\n\n" +

        "Πού θα τα βρεις στο iPhone:\n" +
        "Ρυθμίσεις → Απόρρητο & Ασφάλεια → Ανάλυση & Βελτιώσεις → Δεδομένα Ανάλυσης\n\n" +

        "Αναζήτησε αρχεία με ονόματα:\n" +
        "• panic-full-xxxx.log\n" +
        "• panic-base-xxxx.log\n" +
        "• system-xxxx.ips\n\n" +

        "Πώς να τα εξαγάγεις:\n" +
        "Άνοιξε το αρχείο → Κοινή χρήση → Αποθήκευση στα Αρχεία ή Αποστολή μέσω email\n" +
        "Εξήγαγε όλα τα διαθέσιμα αρχεία.\n\n" +

        "Στην εφαρμογή:\n" +
        "Πάτησε Import και επίλεξε όλα τα logs.\n" +
        "Η εφαρμογή τα αναλύει συνολικά\n" +
        "για εντοπισμό μοτίβων αστάθειας.\n\n" +

        "Συμβουλή:\n" +
        "Όσο περισσότερα logs, τόσο πιο αξιόπιστη η διάγνωση.";
}

// =========================================================
// DIMEN
// =========================================================
private int dp(float v) {
return (int) TypedValue.applyDimension(
TypedValue.COMPLEX_UNIT_DIP,
v,

getResources().getDisplayMetrics()
);
}

// ------------------------------------------------------------
// SHOW POPUP
// ------------------------------------------------------------
private void showPanicGuidePopup() {

if (panicGuideShown) return;
panicGuideShown = true;

boolean gr = AppLang.isGreek(this);

AlertDialog.Builder b =
new AlertDialog.Builder(IPhoneLabsActivity.this);

b.setCancelable(true);

// ================= ROOT =================
LinearLayout root = new LinearLayout(IPhoneLabsActivity.this);
root.setOrientation(LinearLayout.VERTICAL);
root.setPadding(dp(24), dp(22), dp(24), dp(20));

GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF000000); // Μαύρο
bg.setCornerRadius(dp(14));
bg.setStroke(dp(4), 0xFFFFD700); // Χρυσό περίγραμμα
root.setBackground(bg);

// ================= TITLE =================
panicGuideTitle = new TextView(IPhoneLabsActivity.this);
panicGuideTitle.setText(
        AppLang.isGreek(this)
                ? "PANIC LOGS — Οδηγός Εισαγωγής"
                : "PANIC LOGS — Import Guide"
);
panicGuideTitle.setTextColor(Color.WHITE);
panicGuideTitle.setTextSize(19f);
panicGuideTitle.setTypeface(null, Typeface.BOLD);
panicGuideTitle.setGravity(Gravity.CENTER);
panicGuideTitle.setPadding(0, 0, 0, dp(14));
root.addView(panicGuideTitle);

// ================= MESSAGE =================
panicGuideMessage = new TextView(IPhoneLabsActivity.this);
panicGuideMessage.setText(
        AppLang.isGreek(this)
                ? getPanicGuideTextGR()
                : getPanicGuideTextEN()
);

panicGuideMessage.setTextColor(0xFF00FF9C); // Neon green
panicGuideMessage.setTextSize(15f);
panicGuideMessage.setGravity(Gravity.CENTER);
panicGuideMessage.setLineSpacing(0f, 1.15f);
panicGuideMessage.setPadding(dp(6), 0, dp(6), dp(18));

// ενεργοποίηση scroll μέσα στο TextView
panicGuideMessage.setVerticalScrollBarEnabled(true);
panicGuideMessage.setMovementMethod(
        android.text.method.ScrollingMovementMethod.getInstance()
);

// βοηθά το scroll να λειτουργεί σε όλες τις συσκευές
panicGuideMessage.setFocusable(true);
panicGuideMessage.setFocusableInTouchMode(true);

root.addView(panicGuideMessage);

// ================= MUTE ROW =================
root.addView(buildMuteRow());

// ================= LANGUAGE SPINNER =================
Spinner langSpinner = new Spinner(IPhoneLabsActivity.this);

ArrayAdapter<String> adapter =
        new ArrayAdapter<String>(
                IPhoneLabsActivity.this,
                android.R.layout.simple_spinner_item,
                new String[]{"EN", "GR"}
        ) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTypeface(null, Typeface.BOLD);
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(Color.WHITE);
                return tv;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                tv.setTypeface(null, Typeface.BOLD);
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(Color.BLACK);
                tv.setPadding(dp(14), dp(12), dp(14), dp(12));
                return tv;
            }
        };

adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

langSpinner.setAdapter(adapter);
langSpinner.setSelection(AppLang.isGreek(this) ? 1 : 0);

langSpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(
                    AdapterView<?> parent,
                    View view,
                    int position,
                    long id
            ) {

                String newLang = (position == 0) ? "en" : "el";

                if (!newLang.equals(LocaleHelper.getLang(IPhoneLabsActivity.this))) {

                    LocaleHelper.set(IPhoneLabsActivity.this, newLang);

                    try { AppTTS.stop(); } catch (Throwable ignore) {}

                    // 🔥 Hard restart activity + force reopen PancGuide
                    Intent i = getIntent();
                    i.putExtra("force_PanicGuide", true);

                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        }
);

// ================= LANGUAGE BOX =================
LinearLayout langBox = new LinearLayout(IPhoneLabsActivity.this);
langBox.setOrientation(LinearLayout.VERTICAL);
langBox.setGravity(Gravity.CENTER);
langBox.setPadding(dp(12), dp(12), dp(12), dp(12));

GradientDrawable langBg = new GradientDrawable();
langBg.setColor(0xFF111111); // Σκούρο μαύρο
langBg.setCornerRadius(dp(10));
langBg.setStroke(dp(3), 0xFFFFD700); // Χρυσό
langBox.setBackground(langBg);

langBox.addView(langSpinner);

LinearLayout.LayoutParams lpLang =
new LinearLayout.LayoutParams(
LinearLayout.LayoutParams.WRAP_CONTENT,
LinearLayout.LayoutParams.WRAP_CONTENT
);
lpLang.gravity = Gravity.CENTER;
lpLang.setMargins(0, 0, 0, dp(18));
langBox.setLayoutParams(lpLang);

root.addView(langBox);

// ================= CHECKBOX =================
CheckBox cb = new CheckBox(this);
cb.setText(AppLang.isGreek(this)
        ? "Να μην εμφανιστεί ξανά"
        : "Do not show again");
cb.setTextColor(Color.WHITE);
cb.setGravity(Gravity.CENTER);

// margins αντί για padding
LinearLayout.LayoutParams lp =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
lp.setMargins(0, dp(8), 0, dp(16));
cb.setLayoutParams(lp);

root.addView(cb);

// ================= OK BUTTON =================
Button okBtn = new com.gel.cleaner.GelButton(IPhoneLabsActivity.this);
okBtn.setText("OK");
okBtn.setAllCaps(false);
okBtn.setTextColor(Color.WHITE);
okBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
okBtn.setTypeface(null, Typeface.BOLD);
okBtn.setGravity(Gravity.CENTER);
okBtn.setIncludeFontPadding(true);
okBtn.setSingleLine(false);
okBtn.setMinHeight(dp(72));
okBtn.setMinimumHeight(dp(72));
okBtn.setPadding(dp(16), dp(12), dp(16), dp(12));

GradientDrawable okBg = new GradientDrawable();
okBg.setColor(0xFF00E676); // Neon green
okBg.setCornerRadius(dp(12));
okBg.setStroke(dp(3), 0xFFFFD700); // Χρυσό περίγραμμα
okBtn.setBackground(okBg);

LinearLayout.LayoutParams okLp =
new LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
LinearLayout.LayoutParams.WRAP_CONTENT
);
okLp.setMargins(dp(6), dp(8), dp(6), dp(8));
okBtn.setLayoutParams(okLp);

root.addView(okBtn);

// ================= SCROLL WRAPPER =================
ScrollView scroll = new ScrollView(IPhoneLabsActivity.this);
scroll.setFillViewport(true);
scroll.addView(root);

// ================= SET VIEW =================
b.setView(scroll);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// --------------------------------------------
// STOP ALWAYS ON DISMISS - CANCEL
// --------------------------------------------
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    panicGuideShown = false;
});

d.setOnCancelListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    panicGuideShown = false;
});

// --------------------------------------------
// SPEAK ONLY WHEN DIALOG IS ACTUALLY SHOWN
// --------------------------------------------
d.setOnShowListener(dialog -> {
    if (!AppTTS.isMuted(IPhoneLabsActivity.this) && panicGuideShown) {
        speakPanicGuideTTS();
    }
});

// --------------------------------------------
// SHOW
// --------------------------------------------
d.show();
com.gel.cleaner.GelButtonLayout.fitDialog(d);

if (d.getWindow() != null) {
    d.getWindow().setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            (int)(getResources().getDisplayMetrics().heightPixels * 0.85)
    );
}

// --------------------------------------------
// OK BUTTON
// --------------------------------------------
okBtn.setOnClickListener(v -> {

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    panicGuideShown = false;

    if (cb.isChecked()) {
        disablePanicGuideForever();
    }

    d.dismiss();
});
}

// ============================================================
// PANIC LOG IMPORT (SAF) — FINAL CLEAN
// ============================================================
private void openPanicLogPicker() {

    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    i.addCategory(Intent.CATEGORY_OPENABLE);
    i.setType("*/*");
    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    i.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
            "text/plain",
            "application/zip",
            "application/octet-stream"
    });

    startActivityForResult(i, REQ_PANIC_LOG);

    appendHtml("<br>");
    logLine();
    logInfo(AppLang.isGreek(this)
            ? "Ζητήθηκε εισαγωγή Panic Logs (SAF)."
            : "Panic Logs import requested (SAF).");
    logLine();
}

@Override
protected void onActivityResult(int requestCode,
                                int resultCode,
                                @Nullable Intent data) {

    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode != REQ_PANIC_LOG) return;

    boolean gr = AppLang.isGreek(this);

     if (resultCode != RESULT_OK || data == null) {
        logWarn(gr
                ? "Η εισαγωγή ακυρώθηκε."
                : "Panic log import cancelled.");
        return;
    }

    try {

        List<Uri> uris = new ArrayList<>();

        if (data.getClipData() != null) {
            ClipData clip = data.getClipData();
            for (int i = 0; i < clip.getItemCount(); i++) {
                uris.add(clip.getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            uris.add(data.getData());
        }

        if (uris.isEmpty()) {
            logWarn(gr
                    ? "Δεν επιλέχθηκαν αρχεία."
                    : "No files selected.");
            return;
        }

        logOk((gr ? "Επιλέχθηκαν αρχεία: " : "Files selected: ") + uris.size());

        StringBuilder allLogs = new StringBuilder();

// append mode
if (appendMode && panicLogLoaded && panicLogText != null) {
    allLogs.append(panicLogText);
}

int loadedCount = 0;

for (Uri uri : uris) {

    String name = (uri != null) ? uri.getLastPathSegment() : "unknown";
    String safeName = (name != null) ? name : "unknown";

    InputStream is = getContentResolver().openInputStream(uri);
    if (is == null) continue;

    String text;
    try {
        // SAF providers can return opaque names: inspect the ZIP magic, too.
        BufferedInputStream source = new BufferedInputStream(is);
        source.mark(8);
        int first = source.read(), second = source.read();
        source.reset();
        boolean zipMagic = first == 'P' && second == 'K';
        text = (zipMagic || looksLikeZip(safeName))
                ? readPanicFromZip(source)
                : readTextStream(source);
    } finally {
        try { is.close(); } catch (Throwable ignore) {}
    }

    if (text == null || text.trim().isEmpty()) {

        logLabelWarnValue(
                gr ? "Κενό αρχείο:" : "Empty file:",
                safe(safeName)
        );

        continue;
    }

    if (looksCorruptedPanic(text)) {

        logLabelErrorValue(
                gr ? "Μη έγκυρο panic log:" : "Invalid panic log:",
                safe(safeName)
        );

        continue;
    }
    // An arbitrary text file (or Jetsam-only report) is not panic evidence.
    if (!ApplePanicEvidencePolicy.hasPanicEvidence(text)) {
        logLabelWarnValue(
                gr ? "Δεν βρέθηκε πραγματικό panic:" : "No supported panic evidence:",
                safe(safeName));
        continue;
    }

    if (allLogs.length() + text.length() > MAX_PANIC_LOG_SIZE) {

        // Partial imports are not valid replacements: preserve the prior session.
        throw new java.io.IOException(gr
                ? "Υπέρβαση ορίου εισαγωγής. Διατηρήθηκε η προηγούμενη ανάλυση."
                : "Import size cap exceeded. Previous analysis preserved.");
    }

    // delimiter για τα LAB parsers
    allLogs.append("\n===== ZIP FILE: ")
           .append(safe(safeName))
           .append(" =====\n");

    allLogs.append(text).append("\n");

    loadedCount++;

    logOk((gr ? "Φορτώθηκε: "
              : "Loaded: ")
            + safe(safeName));
}

        if (loadedCount == 0) {
            throw new Exception(gr
                    ? "Όλα τα αρχεία ήταν κενά."
                    : "All files were empty.");
        }

        // Count eligible entries, not ZIP containers or unsupported wrappers.
        int supportedBlocks = countSupportedPanicBlocks(
                allLogs.toString().split("(?m)^===== ZIP FILE:"));
        if (supportedBlocks == 0)
            throw new java.io.IOException("No supported panic entries");
        panicLogCount = supportedBlocks;

panicLogName = (panicLogCount == 1)
        ? (gr ? "Ένα panic log"
              : "Single panic log")
        : (gr
           ? "Πολλαπλά panic logs (" + panicLogCount + " αρχεία)"
           : "Panic log archive (" + panicLogCount + " files)");

panicLogText   = allLogs.toString();
panicLogLoaded = true;

// cache signature από όλα
parseAndCacheSignature(panicLogText);

logLine();
logOk(gr
        ? "Η εισαγωγή ολοκληρώθηκε."
        : "Import completed.");
        
String device = detectDeviceType(panicLogText);
String kernel = extractKernelVersion(panicLogText);
String board  = extractBoardId(panicLogText);

logLabelOkValue(
        gr ? "Συσκευή:" : "Device:",
        safe(device)
);

logPanicModelIdentity(panicLogText, gr);

logLabelOkValue(
        gr ? "Kernel:" : "Kernel:",
        safe(kernel)
);

// Missing Board ID is missing evidence, not a positive result.
if (board == null || board.trim().isEmpty()
        || "Unknown".equalsIgnoreCase(board.trim())
        || "N/A".equalsIgnoreCase(board.trim())) {
    logLabelValue("Board ID:", safe(board));
} else {
    logLabelOkValue("Board ID:", safe(board));
}

// 👇 νέο μήνυμα με το όνομα των logs
logLabelOkValue(
        gr ? "Φορτώθηκαν panic logs:" : "Loaded panic logs:",
        safe(panicLogName)
);

logLabelOkValue(
        gr ? "Συνολικό μέγεθος:" : "Total size:",
        panicLogText.length() + " chars"
);

logOk(gr
        ? "Έτοιμο για ανάλυση."
        : "Ready for analysis.");

logLine();

} catch (Exception e) {

     logError(gr
        ? "Αποτυχία εισαγωγής panic logs."
        : "Panic logs import failed.");

logLabelWarnValue(
        gr ? "Αιτία:" : "Reason:",
        safe(e.getMessage())
);
}
}

// ============================================================
// LAB 1 — PANIC LOG ANALYZER (Initial Screening)
// ============================================================
private void runPanicLogAnalyzer() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr 
            ? "LAB 1 — Ανάλυση Panic Logs"
            : "LAB 1 — Panic Log Analyzer");
    logLine();

    logInfo(gr
            ? "Σύγκριση λέξεων εισαγόμενου panic log με κανόνες· όχι φυσικός έλεγχος συσκευής."
            : "Rule-based screening of an imported panic log; no device hardware test.");

    int importedEvents = countSupportedPanicBlocks(panicLogText.split("(?m)^===== ZIP FILE:"));
    if (importedEvents > 1) {
        logInfo(gr
                ? "Εισήχθησαν " + importedEvents + " διαφορετικά συμβάντα. Η αρχική αναγνώριση μοτίβων δεν αποτελεί κοινή διάγνωση· τα αρχεία αναλύονται χωριστά στο LAB 2."
                : importedEvents + " distinct incidents were imported. Initial pattern matching is not a combined diagnosis; LAB 2 reviews each incident separately.");
    }
    IPSPanicParser.Result r = IPSPanicParser.analyze(this, panicLogText);

    if (r == null) {

        logWarn(gr
        ? "Δεν εντοπίστηκε γνωστή υπογραφή panic."
        : "No known panic signature matched.");

logLabelValue(
        gr ? "Τι σημαίνει αυτό:" : "What this means:",
        gr
                ? "Το εισαγόμενο αρχείο δεν αντιστοιχεί σε γνωστό μοτίβο GEL."
                : "The imported file does not match a recognized GEL pattern."
);

logLabelValue(
        gr ? "Γιατί έχει σημασία:" : "Why this matters:",
        gr
                ? "Ορισμένα crashes απαιτούν συμπεριφορική ανάλυση."
                : "Some crashes require behavioral analysis."
);

logLabelValue(
        gr ? "Επόμενο βήμα:" : "Next step:",
        gr
                ? "Το επόμενο LAB θα αναλύσει τη συμπεριφορά του crash."
                : "The next lab will analyze crash behavior."
);

logLabelValue(
        gr ? "Αρχείο που αναλύθηκε:" : "File analyzed:",
        safe(panicLogName)
);

        appendHtml("<br>");
        logOk(gr ? "Το Lab 1 ολοκληρώθηκε." : "Lab 1 finished.");
        logLine();
        return;
    }

    logLabelOkValue(
            gr ? "Ανάλυση κανόνων:" : "GEL rule analysis:",
            gr ? "Ολοκληρώθηκε η αντιστοίχιση μοτίβου GEL."
               : "GEL pattern matching completed.");
    logLabelWarnValue(
            gr ? "Ένδειξη στο αρχείο:" : "Imported-file clue:",
            gr ? "Βρέθηκε αντιστοίχιση σε μοτίβο panic· δεν έγινε έλεγχος φυσικής συσκευής."
               : "A panic pattern matched; no physical device was tested.");

    logLabelValue(
            gr ? "Αναγνωριστικό κανόνα GEL:" : "GEL rule identifier:",
            safe(r.patternId)
);
if (r.secondary != null) {
    for (com.gel.cleaner.IPSPanicParser.Result other : r.secondary) {
        if (other != null && other.patternId != null)
            logLabelValue(gr ? "Πρόσθετος κανόνας GEL:" : "Additional GEL rule:",
                    safe(other.patternId));
    }
}

logLabelValue(
        gr ? "Πιθανό Domain:" : "Domain (hint):",
        IPhonePanicHumanizer.domain(r.domain, gr)
);

logLabelValue(
        gr ? "Υπόθεση κανόνα:" : "Rule-based cause hypothesis:",
        IPhonePanicHumanizer.possibleCause(r.cause, gr)
);

// Parser rule weight does not represent measured device-fault severity.

// Confidence
if ("High".equalsIgnoreCase(r.confidence)) {
    logLabelValue(
            gr ? "Βεβαιότητα αιτίας:" : "Root cause certainty:",
            IPhonePanicHumanizer.certainty(ApplePanicEvidencePolicy.patternConfidence(), gr)
    );
}
else if ("Medium".equalsIgnoreCase(r.confidence)) {
    logLabelValue(
            gr ? "Βεβαιότητα αιτίας:" : "Root cause certainty:",
            IPhonePanicHumanizer.certainty(ApplePanicEvidencePolicy.patternConfidence(), gr)
    );
}
else {
    logLabelValue(
            gr ? "Βεβαιότητα αιτίας:" : "Root cause certainty:",
            IPhonePanicHumanizer.certainty(ApplePanicEvidencePolicy.patternConfidence(), gr)
    );
}

logLabelValue(
        gr ? "Επόμενο τεχνικό βήμα:" : "Next technician step:",
        ApplePanicEvidencePolicy.recommendation(gr)
);

logLabelOkValue(
        gr ? "Επόμενο βήμα:" : "Next step:",
        gr
                ? "Η υπογραφή θα ερμηνευθεί αναλυτικά στο LAB 2."
                : "The extracted signature will be interpreted in detail in LAB 2."
);

    appendHtml("<br>");
    logOk(gr ? "Το Lab 1 ολοκληρώθηκε." : "Lab 1 finished.");
    logLine();
}

// ============================================================
// LAB 2 — PANIC SIGNATURE PARSER (Behavior Interpretation)
// ============================================================
private void runPanicSignatureParser() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 2 — Ανάλυση Υπογραφής Panic"
            : "LAB 2 — Panic Signature Parser");
    logLine();

    logInfo(gr
            ? "Καταγραφή ενδείξεων από εισαγόμενα logs· όχι απόδειξη πραγματικής βλάβης."
            : "Imported-log clues only; not evidence of confirmed physical failure.");

    // Treat each imported incident independently, never as a composite crash.
    String[] separatePanicBlocks = panicLogText.split("(?m)^===== ZIP FILE:");
    if (countSupportedPanicBlocks(separatePanicBlocks) > 1) {
        int incidentNumber = 0;
        for (String block : separatePanicBlocks) {
            if (!ApplePanicEvidencePolicy.hasPanicEvidence(block)) continue;
            incidentNumber++;
            parseAndCacheSignature(block);
            logLabelWarnValue(
                    gr ? "Συμβάν " + incidentNumber + " — τύπος:" : "Incident " + incidentNumber + " — type:",
                    IPhonePanicHumanizer.event(sigCrashType, gr));
            logLabelValue(
                    gr ? "Συμβάν " + incidentNumber + " — με απλά λόγια:" : "Incident " + incidentNumber + " — in plain language:",
                    IPhonePanicHumanizer.meaning(sigCrashType, gr));

        }
        logInfo(gr
                ? "Τα συμβάντα εξετάστηκαν χωριστά. Δεν ενώνουμε λέξεις από διαφορετικά logs για να συμπεράνουμε βλάβη."
                : "Entries were interpreted separately. Keywords from different panic files do not establish a combined fault.");
        logLabelValue(gr ? "Εξειδικευμένη ανάλυση:" : "Detailed analysis:",
                gr ? "Οι τεχνικές ενδείξεις κάθε αρχείου παρουσιάζονται στο LAB 4."
                   : "Each file's technical clues are presented in LAB 4.");
        appendHtml("<br>");
        logOk(gr ? "Το Lab 2 ολοκληρώθηκε." : "Lab 2 finished.");
        logLine();
        return;
    }
    parseAndCacheSignature(panicLogText);

    logLabelOkValue(
        gr ? "Αρχείο:" : "File:",
        safe(panicLogName)
);

// Crash type
if ("Kernel Panic".equalsIgnoreCase(sigCrashType)
        || "Watchdog / Hang".equalsIgnoreCase(sigCrashType)) {

    logLabelWarnValue(
            gr ? "Τύπος στο αρχείο:" : "Imported crash type:",
            IPhonePanicHumanizer.event(sigCrashType, gr)
    );

    logLabelWarnValue(
            gr ? "Τι καταγράφει:" : "What the file indicates:",
            gr
                    ? "Περιέχει όρους διακοπής συστήματος· η λειτουργική κατάσταση χρειάζεται επαλήθευση."
                    : "System interruption terms appear; real device operation needs corroboration."
    );
}
else {

    logLabelWarnValue(
            gr ? "Τύπος στο αρχείο:" : "Imported crash type:",
            IPhonePanicHumanizer.event(sigCrashType, gr)
    );
}

// Subsystem hint
logLabelWarnValue(
        gr ? "Πιθανό Υποσύστημα:" : "Subsystem Hint:",
        IPhonePanicHumanizer.domain(sigDomain, gr)
);

logLabelWarnValue(
        gr ? "Ερμηνεία:" : "Interpretation:",
        gr
                ? "Πιθανή εμπλοκή υποσυστήματος, όχι επιβεβαιωμένη βλάβη."
                : "Possible subsystem involvement, not a confirmed fault."
);

// Confidence
if ("High".equalsIgnoreCase(sigConfidence)) {

    logLabelWarnValue(
            gr ? "Βεβαιότητα αιτίας:" : "Root cause certainty:",
            IPhonePanicHumanizer.certainty(ApplePanicEvidencePolicy.patternConfidence(), gr)
    );
}
else if ("Medium".equalsIgnoreCase(sigConfidence)) {

    logLabelWarnValue(
            gr ? "Βεβαιότητα αιτίας:" : "Root cause certainty:",
            IPhonePanicHumanizer.certainty(ApplePanicEvidencePolicy.patternConfidence(), gr)
    );
}
else {

    logLabelWarnValue(
            gr ? "Βεβαιότητα αιτίας:" : "Root cause certainty:",
            IPhonePanicHumanizer.certainty(ApplePanicEvidencePolicy.patternConfidence(), gr)
    );
}

// Evidence
if (sigKeyEvidence != null && !sigKeyEvidence.trim().isEmpty()) {

    logLabelOkValue(
            gr ? "Βασικά Στοιχεία:" : "Key Evidence Found:",
            IPhonePanicHumanizer.evidence(sigKeyEvidence, gr)
    );
}

// Per-file sensor/SMC clue detail remains in specialist Lab 4.
logLabelValue(gr ? "Εξειδικευμένη ανάλυση:" : "Detailed analysis:",
        gr ? "Οι τεχνικές ενδείξεις ανά αρχείο παρουσιάζονται στο LAB 4."
           : "Per-file technical clues are presented in LAB 4.");

// Next step
logLabelOkValue(
        gr ? "Επόμενο βήμα:" : "Next step:",
        gr
                ? "Η ανάλυση σταθερότητας θα συνεχιστεί στο LAB 3."
                : "The extracted signature will be interpreted further in LAB 3."
);

    appendHtml("<br>");
    logOk(gr ? "Το Lab 2 ολοκληρώθηκε." : "Lab 2 finished.");
    logLine();
}

// ============================================================
// LAB 3 — INCIDENT HISTORY REVIEW (NOT A LIVE STABILITY MEASUREMENT)
// ============================================================
private void runStabilityLab() {
    if (!guardPanicLog()) return;

    final boolean gr = AppLang.isGreek(this);
    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 3 — Αξιολόγηση Ιστορικού Συμβάντων"
            : "LAB 3 — Incident History Review");
    logLine();

    // Distinct imported records must remain distinct: do not classify their
    // combined text as one physical-device instability result.
    String[] blocks = panicLogText.split("(?m)^===== ZIP FILE:");
    int total = countSupportedPanicBlocks(blocks);
    logLabelOkValue(gr ? "Καταγραφές που εξετάστηκαν:" : "Eligible records reviewed:",
            String.valueOf(total));

    // LAB 2 already names the event categories; LAB 3 documents the
    // historical record timeline without repeating the same diagnosis.
    int incident = 0;
    for (String block : blocks) {
        if (!ApplePanicEvidencePolicy.hasPanicEvidence(block)) continue;
        incident++;
        resetSignatureCache();
        parseAndCacheSignature(block);
        ApplePanicEvidencePolicy.RecordMetadata meta =
                ApplePanicEvidencePolicy.describeOne(block);
        if (!meta.timestamp.isEmpty()) {
            logLabelValue(gr ? "Συμβάν " + incident + " — ημερομηνία:" :
                    "Incident " + incident + " — recorded date:", meta.timestamp);
        }
    }

    logLabelValue(gr ? "Ερμηνεία ιστορικού:" : "History interpretation:",
            total > 1
                    ? (gr
                        ? "Οι καταγραφές εξετάζονται ανεξάρτητα· διαφορετικά συμβάντα δεν αποτελούν κοινή διάγνωση."
                        : "Records are reviewed separately; distinct events do not form a combined diagnosis.")
                    : (gr
                        ? "Εξετάστηκε η διαθέσιμη καταγραφή, χωρίς να τεκμηριώνεται συχνότητα ή επανάληψη."
                        : "The available record was reviewed; frequency or recurrence is not established."));

    logLabelValue(gr ? "Όριο ελέγχου:" : "Scope of review:",
            gr
                    ? "Η τρέχουσα σταθερότητα του iPhone δεν μετρήθηκε και δεν ταυτοποιήθηκε ελαττωματικό εξάρτημα."
                    : "Current iPhone stability was not measured and no defective component was identified.");
    logLabelValue(gr ? "Επόμενο βήμα:" : "Next step:",
            gr
                    ? "Το LAB 4 καταγράφει τις κατηγορίες ενδείξεων για κάθε συμβάν χωριστά."
                    : "LAB 4 records subsystem clues for each incident separately.");

    appendHtml("<br>");
    logOk(gr ? "Το Lab 3 ολοκληρώθηκε." : "Lab 3 finished.");
    logLine();
}

// ============================================================
// LAB 4 — INCIDENT-SCOPED SUBSYSTEM CLUES (NO COMPONENT DIAGNOSIS)
// ============================================================
private void runImpactLab() {
    if (!guardPanicLog()) return;

    final boolean gr = AppLang.isGreek(this);
    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 4 — Ανάλυση Επιπτώσεων" : "LAB 4 — Impact Analysis");
    logLine();

    logInfo(gr
            ? "Καταγεγραμμένες κατηγορίες ανά panic log· δεν πραγματοποιήθηκε φυσικός έλεγχος εξαρτημάτων."
            : "Recorded keyword categories per panic file; no physical component test was performed.");

    String[] blocks = panicLogText.split("(?m)^===== ZIP FILE:");
    int incident = 0;
    for (String block : blocks) {
        if (!ApplePanicEvidencePolicy.hasPanicEvidence(block)) continue;
        incident++;
        resetSignatureCache();
        parseAndCacheSignature(block);
        String heading = gr ? "Συμβάν " + incident : "Incident " + incident;
        // Do not hide a recurring code in a later, independent file.
        java.util.Set<String> shownCodes = new java.util.LinkedHashSet<>();
        ApplePanicEvidencePolicy.RecordMetadata meta =
                ApplePanicEvidencePolicy.describeOne(block);
        if (!meta.osVersion.isEmpty()) {
            logLabelValue(heading + (gr ? " — έκδοση iOS:" : " — iOS version:"),
                    meta.osVersion);
        }
        if (!meta.process.isEmpty()) {
            logLabelValue(heading + (gr ? " — διεργασία:" : " — process:"),
                    meta.process);
        }
        if (!meta.signatureKey.isEmpty()) {
            logLabelValue(heading + (gr ? " — υπογραφή panic:" : " — panic signature:"),
                    meta.signatureKey);
        }
        // Domain is an observed *category of terms*, NOT a identified faulty part.
        if ("Unknown".equalsIgnoreCase(sigDomain)) {
            logLabelValue(heading + " — " + (gr ? "περιοχή:" : "area:"),
                    gr ? "Δεν προέκυψε κατηγορία όρων σε αυτό το αρχείο."
                       : "No subsystem keyword category was identified in this file.");
        } else {
            logLabelValue(heading + " — " + (gr ? "κατηγορία ενδείξεων:" : "keyword category:"),
                    IPhonePanicHumanizer.domain(sigDomain, gr));
        }

        // Keep complete model-scoped technical hints in this specialist lab,
        // but never combine keywords from separate panic records.
        logApplePanicEvidence(block, gr,
                heading + (gr ? " — τεχνική ένδειξη" : " — technical clue"), shownCodes);
    }

    // One safety boundary is enough; individual GEL clues remain visible above.
    logLabelValue(gr ? "Εξάρτημα:" : "Component:",
            gr
                    ? "Δεν ταυτοποιήθηκε συγκεκριμένο ελαττωματικό εξάρτημα από τα αρχεία."
                    : "The imported files did not identify a specific defective component.");

    appendHtml("<br>");
    logOk(gr ? "Το Lab 4 ολοκληρώθηκε." : "Lab 4 finished.");
    logLine();
}

// ============================================================
// LAB 5 — PANIC FREQUENCY ANALYZER (Multi-File Correlation)
// ============================================================
private int countSupportedPanicBlocks(String[] blocks) {
    int valid = 0;
    if (blocks != null) {
        for (String block : blocks)
            if (ApplePanicEvidencePolicy.hasPanicEvidence(block)) valid++;
    }
    return valid;
}

private void runPanicFrequencyLab() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 5 — Ανάλυση Συχνότητας Panic"
            : "LAB 5 — Panic Frequency Analyzer");
    logLine();

    String[] blocks = panicLogText.split("(?m)^===== ZIP FILE:");

    if (countSupportedPanicBlocks(blocks) <= 1) {
        logInfo(gr
                ? "Το Lab 5 δεν εκτελέστηκε: απαιτούνται τουλάχιστον δύο αναγνωρισμένα panic logs για σύγκριση."
                : "Lab 5 not executed: at least two eligible panic logs are needed for comparison.");
        logLine();
        return;
    }

java.util.Map<String, Integer> crashCount = new java.util.HashMap<>();

for (String block : blocks) {

    if (!ApplePanicEvidencePolicy.hasPanicEvidence(block))
        continue;

    resetSignatureCache();
    parseAndCacheSignature(block);

        String key = sigCrashType;
        crashCount.put(key, crashCount.getOrDefault(key, 0) + 1);
    }

    logInfo(gr ? "Πλήθος τύπων στα εισαγόμενα αρχεία:" : "Imported crash-type counts:");

for (String k : crashCount.keySet()) {

    if ("Unknown".equalsIgnoreCase(k) || "Unknown / Generic".equalsIgnoreCase(k)) {
        logInfo(IPhonePanicHumanizer.event(k, gr) + ": " + crashCount.get(k));
    } else {
        logLabelWarnValue(gr ? "Καταγεγραμμένος τύπος:" : "Observed type:",
                IPhonePanicHumanizer.event(k, gr) + ": " + crashCount.get(k));
    }

}

    appendHtml("<br>");
    logOk(gr ? "Το Lab 5 ολοκληρώθηκε." : "Lab 5 finished.");
    logLine();
}

// ============================================================
// LAB 6 — PANIC DOMAIN CLUSTERING
// ============================================================
private void runPanicClusteringLab() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 6 — Ομαδοποίηση Domain Panic"
            : "LAB 6 — Panic Domain Clustering");
    logLine();

String[] blocks = panicLogText.split("(?m)^===== ZIP FILE:");

if (countSupportedPanicBlocks(blocks) <= 1) {
    logInfo(gr
            ? "Το Lab 6 δεν εκτελέστηκε: απαιτούνται τουλάχιστον δύο αναγνωρισμένα panic logs για ομαδοποίηση."
            : "Lab 6 not executed: at least two eligible panic logs are needed for clustering.");
    logLine();
    return;
}

java.util.Map<String, Integer> domainCount = new java.util.HashMap<>();

for (String block : blocks) {

    if (!ApplePanicEvidencePolicy.hasPanicEvidence(block))
        continue;

    resetSignatureCache();
    parseAndCacheSignature(block);

    String key = normalizeDomain(sigDomain);

    domainCount.put(key, domainCount.getOrDefault(key, 0) + 1);
}

    logInfo(gr ? "Πλήθος κατηγοριών όρων (όχι βλάβες):"
               : "Keyword-category counts (not hardware failures):");

for (String k : domainCount.keySet()) {

    int count = domainCount.get(k);

    // The same keyword category in two imported records is not a
    // confirmed hardware fault or a positive measured device-health result.
    if ("Unknown".equalsIgnoreCase(k)) {
        logInfo(IPhonePanicHumanizer.domain(k, gr) + ": " + count);
    } else {
        logLabelValue(gr ? "Κατηγορία όρων:" : "Keyword category:",
                IPhonePanicHumanizer.domain(k, gr) + ": " + count);
    }
}

    appendHtml("<br>");
    logOk(gr ? "Το Lab 6 ολοκληρώθηκε." : "Lab 6 finished.");
    logLine();
}

// ============================================================
// LAB 7 — RECURRING DOMAIN DETECTION (Pattern Scoring)
// ============================================================
private void runRecurringDomainLab() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 7 — Ανίχνευση Επαναλαμβανόμενου Domain"
            : "LAB 7 — Recurring Domain Detection");
    logLine();

    String[] blocks = panicLogText.split("(?m)^===== ZIP FILE:");

    java.util.Map<String, Integer> domainCount = new java.util.HashMap<>();
    java.util.Map<String, Integer> signatureCount = new java.util.LinkedHashMap<>();
    java.util.List<Long> datedRecords = new java.util.ArrayList<>();
    int recordsWithDates = 0;
    int recordsWithSignatures = 0;
    int total = 0;

    for (String block : blocks) {

        if (!ApplePanicEvidencePolicy.hasPanicEvidence(block))
            continue;

        resetSignatureCache();
        parseAndCacheSignature(block);

        total++;

        String key = normalizeDomain(sigDomain);
        domainCount.put(key, domainCount.getOrDefault(key, 0) + 1);
        ApplePanicEvidencePolicy.RecordMetadata meta =
                ApplePanicEvidencePolicy.describeOne(block);
        if (!meta.signatureKey.isEmpty()) {
            signatureCount.put(meta.signatureKey,
                    signatureCount.getOrDefault(meta.signatureKey, 0) + 1);
            recordsWithSignatures++;
        }
        if (!meta.timestamp.isEmpty()) recordsWithDates++;
        if (meta.timestampEpochMillis >= 0L) datedRecords.add(meta.timestampEpochMillis);
    }

    if (total <= 1) {
        logInfo(gr
                ? "Το Lab 7 δεν εκτελέστηκε: απαιτούνται τουλάχιστον δύο αναγνωρισμένα panic logs για έλεγχο επανάληψης."
                : "Lab 7 not executed: at least two eligible panic logs are needed to check repetition.");
        logLine();
        return;
    }

    // Βρες dominant
    String dominant = null;
    int max = 0;

    for (String d : domainCount.keySet()) {
        int c = domainCount.get(d);
        if (c > max) {
            max = c;
            dominant = d;
        }
    }

    double ratio = (double) max / (double) total;
    int percent = (int) (ratio * 100);

    logLabelOkValue(
            gr ? "Συνολικά logs:" : "Total logs:",
            String.valueOf(total)
    );

    if (max >= 2) {
        logLabelValue(
                gr ? "Επαναλαμβανόμενη κατηγορία:" : "Repeated keyword category:",
                IPhonePanicHumanizer.domain(dominant, gr) + " (" + max + "/" + total + ")"
        );
        logLabelOkValue(
                gr ? "Μερίδιο στο εισαγόμενο δείγμα:" : "Share in imported sample:",
                percent + "%"
        );
    } else {
        logInfo(gr
                ? "Δεν υπάρχει επαναλαμβανόμενη κατηγορία: κάθε κατηγορία εμφανίστηκε μόνο μία φορά."
                : "No repeated category: each keyword category occurred only once.");
    }

    // Historical timing is comparable only when ALL entries have absolute
    // timestamps with explicit UTC offsets. Never invent a date for a file.
    if (datedRecords.size() == total) {
        java.util.Collections.sort(datedRecords);
        long elapsed = datedRecords.get(datedRecords.size() - 1) - datedRecords.get(0);
        logLabelValue(gr ? "Χρονική απόσταση καταγραφών:" : "Time span between records:",
                String.format(Locale.US, "%.1f", elapsed / 3600000d)
                + (gr ? " ώρες" : " hours"));
    } else if (recordsWithDates > 0) {
        logLabelValue(gr ? "Χρονολογική κάλυψη:" : "Date coverage:",
                recordsWithDates + "/" + total
                        + (gr ? " αρχεία περιλαμβάνουν ημερομηνία· σύγκριση χρόνου μόνο με πλήρη στοιχεία."
                              : " records have a date; chronological comparison requires complete timestamps."));
    }
    int repeatedSignatures = 0;
    for (java.util.Map.Entry<String, Integer> sig : signatureCount.entrySet()) {
        if (sig.getValue() > 1) {
            repeatedSignatures++;
            logLabelValue(gr ? "Επαναλαμβανόμενη υπογραφή panic:" : "Recurring panic signature:",
                    sig.getKey() + " (" + sig.getValue() + "/" + total + ")");
        }
    }
    if (recordsWithSignatures > 1 && repeatedSignatures == 0) {
        logLabelValue(gr ? "Υπογραφές panic:" : "Panic signatures:",
                gr ? "Οι καταγεγραμμένες υπογραφές είναι διαφορετικές."
                   : "The observed panic signatures are distinct.");
    }

    // -------------------------------
    // PATTERN INTERPRETATION
    // -------------------------------
    if (max >= 2 && ratio >= 0.5) {

        if (isHighRiskDomain(dominant)) {
            logInfo(gr
                    ? "Επαναλήφθηκε κατηγορία λέξεων υλικού σε εισαγόμενα αρχεία· δεν πιστοποιείται βλάβη."
                    : "Hardware-related keywords repeat across imports; no hardware fault is established.");
        } else {
            logInfo(gr
                    ? "Επαναλαμβάνονται όροι της ίδιας κατηγορίας στα εισαγόμενα αρχεία."
                    : "A keyword category recurs among imported entries.");
        }

        logWarn(gr
                ? "Η επανάληψη λέξεων δεν αποδεικνύει επανεμφάνιση βλάβης στην ίδια συσκευή."
                : "Repeated keywords do not prove a recurring fault on one verified device.");

    } else if (max >= 2) {

        logInfo(gr
                ? "Μερική επανάληψη κατηγορίας όρων στα εισαγόμενα αρχεία."
                : "Partial keyword-category repetition in imported entries.");

    } else {

        logInfo(gr
                ? "Δεν επαναλήφθηκε κατηγορία όρων στο παρόν δείγμα· η υγεία συσκευής παραμένει άγνωστη."
                : "No category repeats in this imported sample; device health remains unknown.");
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 7 ολοκληρώθηκε." : "Lab 7 finished.");
    logLine();
}

// ============================================================
// LAB 8 — FINAL SERVICE SUMMARY (EVIDENCE ONLY; CONCISE)
// ============================================================
private void runFinalServiceRecommendationLab() {
    if (!guardPanicLog()) return;

    final boolean gr = AppLang.isGreek(this);
    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 8 — Τελική Σύσταση Service"
               : "LAB 8 — Final Service Recommendation");
    logLine();

    // Every file contributes at most one incident. Unlike a combined scan,
    // this cannot synthesize a watchdog+thermal failure across separate logs.
    String[] blocks = panicLogText.split("(?m)^===== ZIP FILE:");
    java.util.Map<String, Integer> crashCount = new java.util.TreeMap<>();
    java.util.Map<String, Integer> domainCount = new java.util.TreeMap<>();
    java.util.Set<String> uniqueTechnicalCodes = new java.util.LinkedHashSet<>();
    int total = 0;
    boolean watchdogObserved = false;
    boolean thermalObserved = false;
    int watchdogFiles = 0;
    int thermalFiles = 0;

    for (String block : blocks) {
        if (!ApplePanicEvidencePolicy.hasPanicEvidence(block)) continue;
        resetSignatureCache();
        parseAndCacheSignature(block);
        total++;
        crashCount.put(sigCrashType, crashCount.getOrDefault(sigCrashType, 0) + 1);
        String domain = normalizeDomain(sigDomain);
        if (!"Unknown".equalsIgnoreCase(domain)) {
            domainCount.put(domain, domainCount.getOrDefault(domain, 0) + 1);
        }
        boolean isWatchdog = "Watchdog / Hang".equals(sigCrashType)
                || "Userspace Watchdog Timeout".equals(sigCrashType)
                || "Baseband Watchdog".equals(sigCrashType);
        boolean isThermal = "Thermal Shutdown / Throttle".equals(sigCrashType);
        watchdogObserved |= isWatchdog;
        thermalObserved |= isThermal;
        if (isWatchdog) watchdogFiles++;
        if (isThermal) thermalFiles++;

        // GEL clue IDs derived by rules from individual imported records,
        // not necessarily literal Apple diagnostic codes or hardware faults.
        ApplePanicHardwareHints.Assessment hints = ApplePanicHardwareHints.examineOne(block);
        for (ApplePanicHardwareHints.Finding finding : hints.findings) {
            uniqueTechnicalCodes.add(finding.code);
        }
    }

    if (total == 0) {
        logInfo(gr ? "Δεν βρέθηκαν αναγνωρίσιμα panic logs."
                   : "No eligible panic logs were found.");
        appendHtml("<br>");
        logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
        logLine();
        return;
    }

    // Human-first summary. This interprets only imported historical records.
    // Never turn absence of repeated clues into proof of device health.
    boolean repeatedCategory = false;
    for (Integer count : domainCount.values()) {
        if (count != null && count >= 2) repeatedCategory = true;
    }
    IPhonePanicServiceSummary.Result human = IPhonePanicServiceSummary.interpret(
            gr, total, watchdogFiles, thermalFiles, repeatedCategory, !domainCount.isEmpty());
    appendHtml("<br>");
    logInfo(gr ? "Συνολικό συμπέρασμα — με απλά λόγια"
               : "Overall conclusion — in plain language");
    logLabelValue(gr ? "Τι βρήκαμε:" : "What we found:", human.whatWeFound);
    if (human.repeatedClueNeedsReview) {
        logLabelWarnValue(gr ? "Τι σημαίνει για εσένα:" : "What it means for you:",
                human.whatItMeans);
    } else {
        logLabelValue(gr ? "Τι σημαίνει για εσένα:" : "What it means for you:",
                human.whatItMeans);
    }
    logLabelValue(gr ? "Χρειάζεται επισκευή;" : "Is a repair needed?",
            human.repairAdvice);
    logLabelValue(gr ? "Πότε χρειάζεται τεχνικός:" : "When to seek service:",
            human.whenToSeekHelp);
    if (demoMode) {
        logInfo(gr
                ? "Αυτό το συμπέρασμα αφορά μόνο τα δοκιμαστικά DEMO αρχεία, όχι πραγματική συσκευή."
                : "This conclusion applies to DEMO files only, not a real device.");
    }

    appendHtml("<br>");
    logInfo(gr ? "Αναλυτικά ευρήματα των αρχείων" : "Detailed findings from the files");
    logLabelOkValue(gr ? "Αρχεία συμβάντων που αναλύθηκαν:"
                       : "Incident files reviewed:", String.valueOf(total));

    // A recorded interruption warrants caution, never a green health verdict.
    for (java.util.Map.Entry<String, Integer> e : crashCount.entrySet()) {
        String observedType = IPhonePanicHumanizer.event(e.getKey(), gr)
                + " — " + e.getValue();
        if ("Unknown".equalsIgnoreCase(e.getKey())
                || "Unknown / Generic".equalsIgnoreCase(e.getKey())) {
            logLabelValue(gr ? "Είδος συμβάντος στο αρχείο:" : "Recorded event type:",
                    observedType);
        } else {
            logLabelWarnValue(gr ? "Είδος συμβάντος στο αρχείο:" : "Recorded event type:",
                    observedType);
        }
        logLabelValue(gr ? "Τι σημαίνει:" : "What it means:",
                IPhonePanicHumanizer.meaning(e.getKey(), gr));
    }

    // Identified keyword categories are not damaged physical components.
    for (java.util.Map.Entry<String, Integer> e : domainCount.entrySet()) {
        logLabelValue(gr ? "Κατηγορία ενδείξεων:" : "Observed keyword category:",
                IPhonePanicHumanizer.domain(e.getKey(), gr) + " — " + e.getValue());
    }

    logLabelValue(gr ? "Επανάληψη στο δείγμα:" : "Recurrence in sample:",
            repeatedCategory
                    ? (gr ? "Υπάρχει κοινή κατηγορία όρων σε διαφορετικά αρχεία· όχι αποδεδειγμένη επανάληψη βλάβης."
                          : "A keyword category appears in multiple records, not a confirmed recurring fault.")
                    : (gr ? "Δεν επαναλήφθηκε η ίδια γνωστή κατηγορία ενδείξεων στα εισαγόμενα αρχεία."
                          : "No known keyword category recurred in the imported files."));

    if (!uniqueTechnicalCodes.isEmpty()) {
        logLabelValue(gr ? "Ενδείξεις που αναγνώρισε το GEL:" : "GEL-recognized clues:",
                android.text.TextUtils.join(", ", uniqueTechnicalCodes));
        logInfo(gr
                ? "Οι ενδείξεις προέρχονται από κανόνες GEL που εφαρμόστηκαν στα αρχεία· δεν είναι απαραίτητα κωδικοί Apple ή αποδεδειγμένες βλάβες."
                : "These clues are generated by GEL rules applied to the files, not necessarily Apple codes or verified defects.");
    }

    // One actionable service path, grounded only in event types actually observed.
    logLabelValue(gr ? "Προτεινόμενος έλεγχος:" : "Next technical check:",
            gr
                    ? "Συσχετίστε ημερομηνίες, έκδοση iOS, νέα panic logs και πραγματικά συμπτώματα."
                    : "Correlate incident dates, iOS version, further panic logs and observed symptoms.");
    if (watchdogObserved) {
        logLabelValue(gr ? "Για τα Watchdog:" : "For watchdog records:",
                gr ? "Ελέγξτε αν υπήρξαν μη αποκρίσεις ή επανεκκινήσεις στις αντίστοιχες ημερομηνίες."
                   : "Check for unresponsive behavior or restarts on the recorded dates.");
    }
    if (thermalObserved) {
        logLabelValue(gr ? "Για το Thermal:" : "For thermal records:",
                gr ? "Ελέγξτε τις συνθήκες χρήσης και τυχόν πραγματικά θερμικά συμπτώματα· θερμοκρασία δεν μετρήθηκε."
                   : "Review usage conditions and real thermal symptoms; no temperature was measured.");
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
    logLine();
}

private void runDemoDiagnostics() {
    // Called from a user click on the main thread. The in-memory demo text
    // and its eight bounded parsers are not a real imported iPhone report.
    final String previousText = panicLogText;
    final String previousName = panicLogName;
    final boolean previousLoaded = panicLogLoaded;
    final int previousCount = panicLogCount;
    final boolean previousAppend = appendMode;
    final String previousCrashType = sigCrashType;
    final String previousDomain = sigDomain;
    final String previousConfidence = sigConfidence;
    final String previousKeyEvidence = sigKeyEvidence;
    final boolean gr = AppLang.isGreek(this);
    demoMode = true;
    try {
        logLine();
        logInfo(gr
                ? "DEMO MODE — ΠΡΟΣΟΜΟΙΩΣΗ APPLE (όχι δεδομένα πελάτη)"
                : "DEMO MODE — APPLE SIMULATION (not customer evidence)");
        logLine();

        panicLogText = buildDemoPanicLogs();
        panicLogLoaded = true;
        panicLogName = gr ? "Δοκιμαστικά panic logs" : "Simulated panic logs";
        panicLogCount = countSupportedPanicBlocks(
                panicLogText.split("(?m)^===== ZIP FILE:"));
        logPanicModelIdentity(panicLogText, gr);
        logOk(gr ? "Φορτώθηκαν δοκιμαστικά δεδομένα μόνο για εμφάνιση."
                 : "Simulation data loaded for on-screen demonstration only.");

        runPanicLogAnalyzer();
        runPanicSignatureParser();
        runStabilityLab();
        runImpactLab();
        runPanicFrequencyLab();
        runPanicClusteringLab();
        runRecurringDomainLab();
        runFinalServiceRecommendationLab();
        logWarn(gr
                ? "Τέλος DEMO: κανένα δοκιμαστικό αποτέλεσμα δεν αποθηκεύτηκε στην αναφορά service."
                : "DEMO finished: no simulated results were stored in the service report.");
    } catch (Throwable t) {
        logLabelErrorValue(gr ? "Αποτυχία demo:" : "Demo failed:", safe(t.getMessage()));
    } finally {
        // Restore the prior real import (if any); a demo never grants the
        // ability to run real LABs without previously imported customer logs.
        panicLogText = previousText;
        panicLogLoaded = previousLoaded;
        panicLogName = previousName;
        panicLogCount = previousCount;
        appendMode = previousAppend;
        sigCrashType = previousCrashType;
        sigDomain = previousDomain;
        sigConfidence = previousConfidence;
        sigKeyEvidence = previousKeyEvidence;
        demoMode = false;
    }
}

// ============================================================
// GUARD
// ============================================================
private boolean guardPanicLog() {

    boolean gr = AppLang.isGreek(this);

    if (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty()) {

        toast(gr
                ? "Φόρτωσε πρώτα panic log."
                : "Load panic log first.");

        logWarn(gr
                ? "Φόρτωσε πρώτα panic log."
                : "Load Panic Log first.");

        return false;
    }

    return true;
}

// ============================================================
// ZIP/TEXT READERS
// ============================================================
private String readTextStream(InputStream is) throws Exception {

    BufferedInputStream bis = new BufferedInputStream(is);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    byte[] buf = new byte[4096];
    int read;
    int total = 0;

    while ((read = bis.read(buf)) != -1) {
        total += read;
        if (total > MAX_TEXT_BYTES)
            throw new java.io.IOException("Imported text exceeds size cap; no partial evidence accepted");
        bos.write(buf, 0, read);
    }

    try { bis.close(); } catch (Throwable ignore) {}

    byte[] bytes = bos.toByteArray();

    String s = new String(bytes, Charset.forName("UTF-8"));
    if (looksGarbled(s)) s = new String(bytes, Charset.forName("ISO-8859-1"));

    return s;
}

private String readPanicFromZip(InputStream is) throws Exception {
    // Count every entry, bound decompression and keep the parent ZIP stream alive.
    return ApplePanicArchiveReader.read(is, ZIP_SCAN_CAP,
            MAX_TEXT_BYTES, MAX_PANIC_LOG_SIZE).text;
}

// ============================================================
// ============================================================
// SIGNATURE PARSER STATE (CANONICAL - DO NOT LOCALIZE)
// ============================================================
private static final String CRASH_UNKNOWN = "Unknown";
private static final String CONF_LOW      = "Low";

private String sigCrashType   = CRASH_UNKNOWN;
private String sigDomain      = CRASH_UNKNOWN;
private String sigConfidence  = CONF_LOW;
private String sigKeyEvidence = "";

/**
 * Print only evidence actually present within this panic block.
 * A repeated code is shown once per summary and is never counted as a
 * confirmed failure or as a measured repair probability.
 */
private void logApplePanicEvidence(String block, boolean gr, String heading,
        java.util.Set<String> alreadyShown) {
    ApplePanicHardwareHints.Assessment findings =
            ApplePanicHardwareHints.examineOne(block);
    int rendered = 0;
    for (ApplePanicHardwareHints.Finding finding : findings.findings) {
        String key = findings.modelIdentifier + "|" + finding.code;
        if (alreadyShown != null && !alreadyShown.add(key)) continue;
        if (rendered++ >= 12) {
            logInfo(gr
                    ? "Επιπλέον ευρήματα παραλείφθηκαν από τη σύντομη προβολή."
                    : "Additional findings omitted from the short display.");
            break;
        }
        logLabelValue(heading + (gr ? " — ένδειξη GEL:" : " — GEL clue:"), finding.code);
        if (!"Unknown".equals(findings.modelIdentifier)) {
            logLabelValue(gr ? "Κωδικός συσκευής:" : "Device identifier:",
                    findings.modelIdentifier);
        }
        logLabelValue(gr ? "Τι μπορεί να αφορά:" : "What it may concern:",
                finding.area(gr));
        logLabelValue(gr ? "Ασφαλές επόμενο βήμα:" : "Safe next check:",
                finding.action(gr));
    }
    if (rendered > 0 && alreadyShown == null) {
        logInfo(gr
                ? "Οι ενδείξεις αποδίδονται από κανόνες GEL βάσει των αρχείων, όχι από πιστοποίηση βλάβης ή επισκευής."
                : "These are GEL rule-based clues from imported files, not verified component failures or repairs.");
    }
}

private void parseAndCacheSignature(String text) {

    // reset state
    sigCrashType   = CRASH_UNKNOWN;
    sigDomain      = CRASH_UNKNOWN;
    sigConfidence  = CONF_LOW;
    sigKeyEvidence = "";

    if (text == null || text.trim().isEmpty()) return;

    // Never merge separate incidents into one combined panic signature.
    if (text.contains("===== ZIP FILE:")
            && countSupportedPanicBlocks(text.split("(?m)^===== ZIP FILE:")) > 1) {
        sigCrashType = "Multiple separate panic entries";
        sigDomain = "Mixed evidence — per-entry evaluation required";
        sigConfidence = CONF_LOW;
        sigKeyEvidence = "Multiple independent panic entries";
        return;
    }

        String low = text.toLowerCase(Locale.US);

        boolean isWatchdog   = low.contains("watchdog") || low.contains("0x8badf00d");
        boolean isKernelPanic= low.contains("panic(") || low.contains("panic cpu") || low.contains("panicstring");
        boolean isJetsam     = low.contains("jetsam") || low.contains("memorystatus") || low.contains("highwater");
        boolean isThermal    = low.contains("thermal") && (low.contains("shutdown") || low.contains("throttle"));
        boolean isI2C        = low.contains("i2c") || low.contains("bus error");
        boolean isNand       = low.contains("nand") || low.contains("apfs") || low.contains("nvme") || low.contains("storage");
        boolean isBaseband   = ApplePanicEvidencePolicy.hasExplicitBasebandFaultHint(text);
        boolean isPower      = low.contains("power") && (low.contains("pmu") || low.contains("brownout") || low.contains("sudden"));
        boolean isGpu        = low.contains("gpu") || low.contains("agx") || low.contains("metal");
        boolean isSensor     = low.contains("sensor") || low.contains("mic") || low.contains("camera") || low.contains("touch");
        boolean isIPS =
        low.contains("bug_type") ||
        low.contains("incident") ||
        low.contains("termination") ||
        low.contains("exception");
        boolean isUserSpaceWatchdog =
        low.contains("bug_type\": \"210") ||
        low.contains("bug_type\":210") ||
        low.contains("bug_type: 210");
        boolean isNVME = low.contains("nvme") || low.contains("apfs_vfsop");
        boolean isWatchdogBaseband = isBaseband && isWatchdog;

        if (isWatchdog) sigCrashType = "Watchdog / Hang";
        else if (isUserSpaceWatchdog) sigCrashType = "Userspace Watchdog Timeout";
        else if (isJetsam) sigCrashType = "Jetsam / Memory Pressure";
        else if (isThermal) sigCrashType = "Thermal Shutdown / Throttle";
        else if (isKernelPanic) sigCrashType = "Kernel Panic";
        else if (isWatchdogBaseband) sigCrashType = "Baseband Watchdog";
        else if (isIPS) sigCrashType = "iOS Crash Report";
        else sigCrashType = "Unknown / Generic";

        if (isBaseband) sigDomain = "Baseband / Cellular";
        else if (isNVME || isNand) sigDomain = "Storage / NAND / FS";
        else if (isGpu) sigDomain = "GPU / Graphics";
        else if (isI2C) sigDomain = "I2C / Peripheral Bus";
        else if (isPower || low.contains("brownout") || low.contains("pmu") || low.contains("pwr")) sigDomain = "Power / PMIC";
        else if (isThermal) sigDomain = "Thermal / Cooling";
        else if (isJetsam) sigDomain = "Memory / OS Pressure";
        else if (isSensor) sigDomain = "Sensors / I/O";
        else if (isKernelPanic) sigDomain = "Kernel / OS Core";
        else if (isWatchdogBaseband) sigDomain = "Baseband / Cellular";
        else sigDomain = "Unknown";

        int points = 0;
        StringBuilder ev = new StringBuilder();

if (isWatchdog)              { points += 30; evAppend(ev, "watchdog"); }
if (low.contains("panicstring")) { points += 30; evAppend(ev, "panicString"); }
if (low.contains("bug_type"))    { points += 20; evAppend(ev, "bug_type"); }
if (low.contains("incident"))    { points += 20; evAppend(ev, "incident"); }
if (low.contains("panic cpu"))   { points += 20; evAppend(ev, "panic cpu"); }
if (low.contains("0x8badf00d"))  { points += 25; evAppend(ev, "0x8badf00d"); }

if (isWatchdogBaseband) {
    points += 30;
    evAppend(ev, "baseband watchdog");
}
else if (isBaseband) {
    points += 20;
    evAppend(ev, "baseband");
}

if (isNand || isNVME)        { points += 25; evAppend(ev, "storage/nvme"); }
if (isGpu)                   { points += 20; evAppend(ev, "gpu/agx"); }
if (isThermal)               { points += 20; evAppend(ev, "thermal"); }
if (isJetsam)                { points += 20; evAppend(ev, "jetsam"); }

        if (points >= 70) sigConfidence = "High";
        else if (points >= 40) sigConfidence = "Medium";
        else sigConfidence = "Low";

        sigKeyEvidence = ev.toString();
        if (sigKeyEvidence.endsWith(", ")) sigKeyEvidence = sigKeyEvidence.substring(0, sigKeyEvidence.length() - 2);
    }

    private void evAppend(StringBuilder ev, String token) {
        if (ev == null) return;
        ev.append(token).append(", ");
    }

    // ============================================================
    // UI HELPER — BUTTON (GUARDED CLICK)
    // ============================================================
    private View makeLabButton(
        String title,
        String subtitle,
        boolean requiresPanicLog,
        View.OnClickListener realClick
) {
    LinearLayout container = new LinearLayout(this);
    container.setOrientation(LinearLayout.VERTICAL);
    
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    );
    lp.setMargins(0, dp(10), 0, dp(10));
    container.setLayoutParams(lp);

    container.setPadding(dp(16), dp(16), dp(16), dp(16));
    container.setBackgroundResource(R.drawable.gel_btn_outline_selector);
    container.setClickable(true);
    container.setFocusable(true);

    TextView t = new TextView(this);
t.setText(title);
t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

// PRIMARY BUTTON COLORS
if (title.contains("AUTO") || title.contains("Εκτέλεση όλων")) {

    // RUN ALL
    t.setTextColor(0xFFFFD700); // gold

}
else if (title.contains("DEMO")) {

    // DEMO MODE
    t.setTextColor(0xFFFF4444); // red

}
else {

    // NORMAL LABS
    t.setTextColor(COLOR_NEON);
}

t.setIncludeFontPadding(false);
t.setClickable(false);
t.setFocusable(false);

    TextView s = new TextView(this);
    s.setText(subtitle);
    s.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
    s.setTextColor(COLOR_WHITE);
    s.setPadding(0, dp(6), 0, 0);
    s.setClickable(false);
    s.setFocusable(false);

    container.addView(t);
    container.addView(s);

    // guarded click
container.setOnClickListener(v -> {

    if (requiresPanicLog &&
        (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty())) {

        boolean gr = AppLang.isGreek(this);

        String msg = gr
                ? "Φόρτωσε πρώτα panic log."
                : "Load panic log first.";

        toast(msg);
        logWarn(msg);
        return;
    }

    if (realClick != null) {
        realClick.onClick(v);
    }
});

    return container;
}

private void setButtonTextWhite(View container) {

    if (!(container instanceof ViewGroup)) return;

    ViewGroup vg = (ViewGroup) container;

    for (int i = 0; i < vg.getChildCount(); i++) {
        View child = vg.getChildAt(i);

        if (child instanceof TextView) {
            ((TextView) child).setTextColor(0xFFFFFFFF);
        }

        if (child instanceof ViewGroup) {
            setButtonTextWhite(child);
        }
    }
}

// ============================================================
// LOGGING — GEL CANONICAL (UI + SERVICE REPORT)
// ============================================================

private static final int MAX_LOG_BUFFER = 250_000; // προστασία από UI lag

private void appendHtml(String html) {
    ui.post(() -> {
        CharSequence cur = txtLog.getText();
        CharSequence add = Html.fromHtml(html + "<br>");
        txtLog.setText(TextUtils.concat(cur, add));
        scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
    });
}

private void logInfo(String msg) {
    String clean = safe(msg);
    String s = "ℹ️ " + clean;
    appendHtml(escape(s));
    if (!demoMode) GELServiceLog.logInfo(clean);
}

private void logOk(String msg) {
    String clean = safe(msg);
    String s = "✔ " + clean;
    appendHtml("<font color='#39FF14'>" + escape(s) + "</font>");
    if (!demoMode) GELServiceLog.logOk(clean);
}

private void logWarn(String msg) {
    String clean = safe(msg);
    String s = "⚠ " + clean;
    appendHtml("<font color='#FFD966'>" + escape(s) + "</font>");
    if (!demoMode) GELServiceLog.logWarn(clean);
}

private void logError(String msg) {
    String clean = safe(msg);
    String s = "✖ " + clean;
    appendHtml("<font color='#FF5555'>" + escape(s) + "</font>");
    if (!demoMode) GELServiceLog.logError(clean);
}

private void logLine() {
    appendHtml("--------------------------------------------------");
    if (!demoMode) GELServiceLog.logLine();
}

// ============================================================
// LABEL + VALUE HELPERS (WHITE LABEL + COLORED VALUE)
// ============================================================
// Neutral factual, unavailable or unverified values must remain white.
private void logLabelValue(String label, String value) {
    String l = safe(label);
    String v = safe(value);
    appendHtml("<font color='#FFFFFF'>" + escape(l) + "</font> " +
            "<font color='#FFFFFF'>" + escape(v) + "</font>");
    if (!demoMode) GELServiceLog.iPhoneInfoValue(l, v, "#FFFFFF");
}

private void logLabelOkValue(String label, String value) {
    String l = safe(label);
    String v = safe(value);

    appendHtml(
            "<font color='#FFFFFF'>" + escape(l) + "</font> " +
            "<font color='#39FF14'>" + escape(v) + "</font>"
    );

    if (!demoMode) GELServiceLog.iPhoneInfoValue(l, v, "#39FF14");
}

private void logLabelWarnValue(String label, String value) {
    String l = safe(label);
    String v = safe(value);

    appendHtml(
            "<font color='#FFFFFF'>" + escape(l) + "</font> " +
            "<font color='#FFD966'>" + escape(v) + "</font>"
    );

    if (!demoMode) GELServiceLog.iPhoneWarnValue(l, v, "#FFD966");
}

private void logLabelErrorValue(String label, String value) {
    String l = safe(label);
    String v = safe(value);

    appendHtml(
            "<font color='#FFFFFF'>" + escape(l) + "</font> " +
            "<font color='#FF5555'>" + escape(v) + "</font>"
    );

    if (!demoMode) GELServiceLog.iPhoneErrorValue(l, v, "#FF5555");
}

// ------------------------------------------------------------
// UI APPENDER
// ------------------------------------------------------------

private String stripHtml(String s) {
    if (s == null) return "";
    return s.replace("<br>", "\n").replaceAll("<[^>]*>", "");
}

private String escape(String s) {
    if (s == null) return "";
    return s.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
}

// ============================================================
// SEMANTIC HELPERS (NO COLORS — LOG METHODS DECIDE)
// ============================================================

private boolean isHighConfidence(String conf) {
    return conf != null && "High".equalsIgnoreCase(conf);
}

private boolean isMediumConfidence(String conf) {
    return conf != null && "Medium".equalsIgnoreCase(conf);
}

private boolean isCriticalSeverity(String sev) {
    if (sev == null) return false;
    String s = sev.toLowerCase(Locale.US);
    return s.contains("critical") || s.contains("high");
}

private boolean isMediumSeverity(String sev) {
    if (sev == null) return false;
    String s = sev.toLowerCase(Locale.US);
    return s.contains("medium") || s.contains("warn");
}

private boolean isCriticalCrash(String crash) {
    if (crash == null) return false;
    return crash.contains("Kernel Panic")
            || crash.contains("Watchdog");
}

private boolean isWarningCrash(String crash) {
    if (crash == null) return false;
    return crash.contains("Thermal")
            || crash.contains("Jetsam");
}

private boolean isHighRiskDomain(String domain) {
    if (domain == null) return false;
    return domain.contains("Power")
            || domain.contains("Baseband")
            || domain.contains("Storage");
}

private boolean isWarningDomain(String domain) {
    if (domain == null) return false;
    return domain.contains("Thermal")
            || domain.contains("Memory")
            || domain.contains("GPU");
}

private String safe(String s) {
    return (s == null || s.trim().isEmpty()) ? "unknown" : s;
}

    // ============================================================
    // HELPERS (dp/sp + I/O)
    // ============================================================
    
    public float sp(float v) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, v, getResources().getDisplayMetrics());
    }

    private boolean looksLikeZip(String name) {
        if (name == null) return false;
        String low = name.toLowerCase(Locale.US);
        return low.endsWith(".zip") || low.contains(".zip");
    }

    private boolean looksGarbled(String s) {
        if (s == null || s.isEmpty()) return false;
        int bad = 0;
        int lim = Math.min(s.length(), 4000);
        for (int i = 0; i < lim; i++) {
            if (s.charAt(i) == '\uFFFD') bad++;
        }
        return bad > 10;
    }

    // (kept for compatibility with other blocks you might paste later)
    private boolean textContainsAny(String text, String... keys) {
        if (text == null || keys == null) return false;
        String low = text.toLowerCase(Locale.US);
        for (String k : keys) {
            if (k == null) continue;
            if (low.contains(k.toLowerCase(Locale.US))) return true;
        }
        return false;
    }
}
