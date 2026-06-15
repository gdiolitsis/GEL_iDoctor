// GDiolitsis Engine Lab (GEL) - Author & Developer
// MainActivity - STABLE FINAL
// NOTE: Always return full file ready for copy-paste (no patch-only replies).

package com.gel.cleaner;

import com.gel.cleaner.iphone.*;
import com.gel.cleaner.base.*;

import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.Manifest;
import android.content.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.TypedValue;
import android.view.*;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.gel.cleaner.UIHelpers;

import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class MainActivity extends GELAutoActivityHook
implements GELCleaner.LogCallback {

// =========================================================
// STATE
// =========================================================

private boolean welcomeShown = false;

private boolean welcomePopupActive = false;

private TextView welcomeTitle;
private TextView welcomeMessage;

private ScrollView scroll;

// =========================================================
// GUIDED OPTIMIZER — USAGE ACCESS GUARD STATE
// =========================================================
private boolean waitingForUsageAccessReturn = false;

// ==============================
// MAINACTIVITY — ADD/REPLACE THESE METHODS
// ==============================

@Override
public boolean onCreateOptionsMenu(Menu menu) {
    // ⚙ settings icon (always visible)
    menu.add(0, 1001, 0, "")
        .setIcon(R.drawable.ic_settings)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

// 🔽 CONTACT BUTTON
menu.add(0, 1002, 1, "Contact")
        .setIcon(R.drawable.ic_contact_gel)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == 1001) {
    showSettingsDialog();
    return true;
}

if (item.getItemId() == 1002) {
    showContactDeveloperDialog();
    return true;
}
    return super.onOptionsItemSelected(item);
}

// =========================================================
// PREFS
// =========================================================
private static final String PREFS = "gel_prefs";
private static final String KEY_PLATFORM = "platform_mode";

// =========================================================
// LOCALE
// =========================================================
@Override
protected void attachBaseContext(Context base) {
super.attachBaseContext(LocaleHelper.apply(base));
}

@Override
protected void onResume() {
    super.onResume();

    // Re-check Usage Access after returning from Android Settings.
    if (waitingForUsageAccessReturn) {

        waitingForUsageAccessReturn = false;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (hasUsageAccess()) {

                launchGuidedOptimizer(false);

            } else {

                showUsageAccessStillBlockedDialog();
            }

        }, 250);
    }
}

// =========================================================
// ON CREATE
// =========================================================
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    buildAppleInfoLog();

LinearLayout root = findViewById(R.id.contentRoot);

UIHelpers.applyPressEffectRecursive(getWindow().getDecorView());
    
   androidx.appcompat.widget.Toolbar toolbar =
        findViewById(R.id.gelToolbar);

setSupportActionBar(toolbar);

if (getSupportActionBar() != null) {
    getSupportActionBar().setTitle("");
}

if (getIntent() != null && getIntent().hasExtra("mini_cpu")) {
    handleMiniSignals(getIntent());
}
        
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

    NotificationChannel channel =
            new NotificationChannel(
                    "gel_default",
                    "GEL Health Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

    channel.setDescription("Device health & mini check notifications");

    NotificationManager nm =
            getSystemService(NotificationManager.class);

    if (nm != null) {
        nm.createNotificationChannel(channel);
    }
}

    scroll = findViewById(R.id.scrollRoot);

    setupLangButtons();
    setupDonate();
    setupButtons();
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

        requestPermissions(
                new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                1001
        );
    }
}

SharedPreferences sp =
        getSharedPreferences("gel_prefs", MODE_PRIVATE);

if (sp.getBoolean("pulse_enabled", false)) {
    OptimizerMiniPulseScheduler.enable(this);
}

// ================= ENTRY FLOW =================
boolean forceWelcome =
        getIntent().getBooleanExtra("force_welcome", false);

boolean skipWelcomeOnce =
        getIntent().getBooleanExtra("skip_welcome_once", false);

boolean fromMini = getIntent() != null &&
                   getIntent().hasExtra("mini_cpu");

if (savedInstanceState == null) {

    if (!fromMini && !skipWelcomeOnce &&
        (forceWelcome || !isWelcomeDisabled())) {

        showWelcomePopup();
    }
}

    // ================= APPLY PLATFORM UI =================
    if ("apple".equals(getSavedPlatform())) {
        applyAppleModeUI();
    } else {
        applyAndroidModeUI();
    }

    syncReturnButtonText();

    // ================= APP MANAGER =================
    View btnAppManager = findViewById(R.id.btnAppManager);
    if (btnAppManager != null) {
        btnAppManager.setOnClickListener(v -> {
            try {
                Intent i = new Intent(MainActivity.this, AppListActivity.class);
                i.putExtra("mode", "uninstall");
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(
                        MainActivity.this,
                        "Cannot open App Manager",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // ================= GUIDED OPTIMIZER =================
    View btnGuidedOptimizer = findViewById(R.id.btnGuidedOptimizer);
    if (btnGuidedOptimizer != null) {
        btnGuidedOptimizer.setOnClickListener(v ->
                openGuidedOptimizerWithPermissionGuard()
        );
    }
}

private void buildAppleInfoLog() {

    TextView msg = findViewById(R.id.txtAppleInfo);
    if (msg == null) return;

    msg.setTextColor(0xFF00FF66);
    msg.setTextSize(14f);
    msg.setLineSpacing(0f, 1.25f);
    msg.setPadding(dp(6), dp(6), dp(6), dp(6));
    // IMPORTANT:
    // Do not create nested scrolling inside the Apple information text.
    // The MainActivity ScrollView is the single scrolling authority.
    msg.setVerticalScrollBarEnabled(false);
    msg.setMovementMethod(null);
    msg.setFocusable(false);
    msg.setFocusableInTouchMode(false);
    msg.setMaxHeight(Integer.MAX_VALUE);

    boolean gr = AppLang.isGreek(this);

msg.setText(
        gr
        ? "Για την διάγνωση των συσκευών Apple αναλύουμε τα panic logs "
        + "της κάθε συσκευής, ανεξαρτήτως μοντέλου, σειράς, iPhone ή iPad.\n\n"

        + "Οι πληροφορίες συσκευών που παρουσιάζουμε αφορούν ενδεικτικά "
        + "τα τελευταία μοντέλα Apple που κυκλοφορούν στην αγορά.\n\n"

        + "Εάν δεν βρείτε την συσκευή σας στην λίστα των μοντέλων, "
        + "δεν σημαίνει ότι δεν μπορούμε να αναλύσουμε τα panic logs της.\n\n"

        + "Τα panic logs παρέχουν τις ίδιες διαγνωστικές πληροφορίες "
        + "σε οποιοδήποτε μοντέλο Apple, είτε πρόκειται για iPhone είτε για iPad."

        : "Apple diagnostics in GEL are performed by analyzing device panic logs, "
        + "regardless of model, generation, iPhone or iPad.\n\n"

        + "The device specifications we display refer indicatively "
        + "to recent Apple models available on the market.\n\n"

        + "If your specific device is not listed, it does not mean "
        + "that we cannot analyze its panic logs.\n\n"

        + "Panic logs contain the same diagnostic information "
        + "for any Apple device, whether it is an iPhone or an iPad."
);
}

private void showContactDeveloperDialog() {

    final boolean gr = AppLang.isGreek(this);

    // ================= ROOT (GEL BOX) =================
    LinearLayout box = new LinearLayout(this);
    box.setOrientation(LinearLayout.VERTICAL);
    box.setPadding(dp(18), dp(16), dp(18), dp(12));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000); // μαύρο
    bg.setCornerRadius(dp(14));
    bg.setStroke(dp(3), 0xFFFFD700); // χρυσό
    box.setBackground(bg);

    // ================= TITLE =================
    TextView title = new TextView(this);
    title.setText(gr ? "Επικοινωνία με προγραμματιστή" : "Contact Developer");
    title.setTextColor(Color.WHITE);
    title.setTextSize(16f);
    title.setTypeface(Typeface.DEFAULT_BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(10));
    box.addView(title);

    // ================= INPUT =================
    final EditText input = new EditText(this);
    input.setHint(gr ? "Γράψε μήνυμα..." : "Write your message...");
    input.setMinLines(4);
    input.setTextColor(Color.WHITE);
    input.setHintTextColor(0xFFAAAAAA);
    input.setPadding(dp(12), dp(12), dp(12), dp(12));

    GradientDrawable inputBg = new GradientDrawable();
    inputBg.setColor(0xFF000000);
    inputBg.setCornerRadius(dp(10));
    inputBg.setStroke(dp(2), 0xFFFFD700);
    input.setBackground(inputBg);

    LinearLayout.LayoutParams lpInput =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
    lpInput.setMargins(0, 0, 0, dp(12));

    box.addView(input, lpInput);

    // ================= BUTTONS =================
    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER);

    Button btnCancel = new Button(this);
    btnCancel.setText(gr ? "Άκυρο" : "Cancel");
    btnCancel.setTextColor(Color.WHITE);
    btnCancel.setAllCaps(false);
    btnCancel.setBackground(makeGelBtn(0xFFAA1111));

    Button btnSend = new Button(this);
    btnSend.setText("Send");
    btnSend.setTextColor(Color.WHITE);
    btnSend.setAllCaps(false);
    btnSend.setBackground(makeGelBtn(0xFF00FF7F));

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(0, dp(90), 1f);
    lp.setMargins(dp(6), 0, dp(6), 0);

    row.addView(btnCancel, lp);
    row.addView(btnSend, lp);

    box.addView(row);

    // ================= DIALOG =================
    AlertDialog dlg = new AlertDialog.Builder(this)
            .setView(box)
            .setCancelable(true)
            .create();

    btnCancel.setOnClickListener(v -> dlg.dismiss());

    btnSend.setOnClickListener(v -> {

        String message = input.getText().toString().trim();
if (message.isEmpty()) return;

Intent intent = new Intent(Intent.ACTION_SENDTO);
intent.setData(Uri.parse("mailto:"));

intent.putExtra(Intent.EXTRA_EMAIL,
        new String[]{"gdiolitsis@yahoo.com"});

intent.putExtra(Intent.EXTRA_SUBJECT,
        "GEL iDoctor • " + android.os.Build.MODEL);

// 🔥 BUILD FULL REPORT
String fullReport = buildSupportReport(message);

intent.putExtra(Intent.EXTRA_TEXT, fullReport);

// 🔥 OPEN EMAIL APP
try {
    startActivity(intent);
} catch (Exception e) {
    Toast.makeText(this,
            "No email app found",
            Toast.LENGTH_SHORT).show();
}

        dlg.dismiss();
    });

    dlg.show();

    // 🔥 ΚΑΝΕ ΤΟ BACKGROUND FULL TRANSPARENT (για να φανεί το box σωστά)
    if (dlg.getWindow() != null) {
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}

private String buildSupportReport(String userMsg) {

    StringBuilder sb = new StringBuilder();

    sb.append("=== GEL iDoctor Support Report ===\n\n");

// ================= USER MESSAGE =================
sb.append("----- USER MESSAGE -----\n");
sb.append(userMsg).append("\n\n");

// ================= DEVICE INFO =================
sb.append("----- DEVICE -----\n");
sb.append("Model: ").append(android.os.Build.MODEL).append("\n");
sb.append("Brand: ").append(android.os.Build.BRAND).append("\n");
sb.append("Android: ").append(android.os.Build.VERSION.RELEASE).append("\n");
sb.append("SDK: ").append(android.os.Build.VERSION.SDK_INT).append("\n\n");

// ================= APP STATE =================
sb.append("----- APP -----\n");
sb.append("Version: 1.0\n\n");

// ================= LOGS =================
sb.append("----- LOGS -----\n");

try {
    String logs = GELServiceLog.getAll();
    if (logs != null && !logs.trim().isEmpty()) {
        sb.append(logs);
    } else {
        sb.append("No logs available\n");
    }
} catch (Throwable ignore) {
    sb.append("Logs unavailable\n");
}

sb.append("\n\n----- END OF REPORT -----\n");

return sb.toString();
}

@Override
public void log(String msg, boolean isError) {
    // no UI log here
}

private void showSettingsDialog() {

    final boolean gr = AppLang.isGreek(this);

    // ---- current states ----
    SharedPreferences sp = getSharedPreferences("gel_prefs", MODE_PRIVATE);
    boolean miniEnabled = sp.getBoolean("pulse_enabled", false);

    boolean schedEnabled = OptimizerScheduler.isReminderEnabled(this);
    int schedDays = OptimizerScheduler.getReminderDays(this); // 1/7/30 (default 7)

    // ---- GEL dialog root ----
    LinearLayout box = new LinearLayout(this);
    box.setOrientation(LinearLayout.VERTICAL);
    box.setPadding(dp(18), dp(16), dp(18), dp(14));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF0B0B0B);
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(3), 0xFFFFD700);
    box.setBackground(bg);

    TextView title = new TextView(this);
    title.setText(gr ? "Ρυθμίσεις" : "Settings");
    title.setTextColor(Color.WHITE);
    title.setTypeface(Typeface.DEFAULT_BOLD);
    title.setTextSize(18f);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(10));
    box.addView(title);

    // ==============================
    // MINI CHECK (3 φορές/ημέρα 09-15-21)
    // ==============================
    final android.widget.CheckBox cbMini = new android.widget.CheckBox(this);
    cbMini.setChecked(miniEnabled);
    cbMini.setText(gr
            ? "Mini Check — 3 φορές/ημέρα (09:00 • 15:00 • 21:00)"
            : "Mini Check — 3/day (09:00 • 15:00 • 21:00)");
    cbMini.setTextColor(0xFF00FF7F);
    cbMini.setPadding(0, dp(6), 0, dp(10));
    box.addView(cbMini);

    TextView miniHint = new TextView(this);
    miniHint.setText(gr
? "Διαρκεί λιγότερο από 1 δευτερόλεπτο και στέλνει ειδοποίηση μόνο σε κρίσιμες καταστάσεις (Crash / ≥45°C / Υπερφόρτωση CPU–GPU–Cache)."
: "Runs in under 1 second and notifies only on critical conditions (Crash / ≥45°C / CPU–GPU–Cache overload).");
    miniHint.setTextColor(Color.WHITE);
    miniHint.setTextSize(13f);
    miniHint.setPadding(0, 0, 0, dp(14));
    box.addView(miniHint);

    // ==============================
    // OPTIMIZER SCHEDULER (reminder) ON/OFF + 1/7/30
    // ==============================
    TextView sep = new TextView(this);
    sep.setText("────────────────────────");
    sep.setTextColor(0xFF333333);
    sep.setGravity(Gravity.CENTER);
    sep.setPadding(0, dp(2), 0, dp(10));
    box.addView(sep);

    final android.widget.CheckBox cbSched = new android.widget.CheckBox(this);
    cbSched.setChecked(schedEnabled);
    cbSched.setText(gr
            ? "Έξυπνη Βελτιστοποίηση — Υπενθύμιση Κάθε..."
            : "Guided Optimizer — Reminder Every...");
    cbSched.setTextColor(0xFF00FF7F);
    cbSched.setPadding(0, dp(6), 0, dp(6));
    box.addView(cbSched);

    final RadioGroup rg = new RadioGroup(this);
    rg.setOrientation(LinearLayout.HORIZONTAL);
    rg.setPadding(0, dp(4), 0, dp(10));

    RadioButton r1 = new RadioButton(this);
    r1.setText(gr ? "1 Ημέρα" : "1 Day");
    r1.setTextColor(Color.WHITE);

    RadioButton r7 = new RadioButton(this);
    r7.setText(gr ? "1 Εβδομάδα" : "1 Week");
    r7.setTextColor(Color.WHITE);

    RadioButton r30 = new RadioButton(this);
    r30.setText(gr ? "1 Μήνα" : "1 Month");
    r30.setTextColor(Color.WHITE);

    rg.addView(r1);
    rg.addView(r7);
    rg.addView(r30);

    if (schedDays == 1) rg.check(r1.getId());
    else if (schedDays == 30) rg.check(r30.getId());
    else rg.check(r7.getId());

    rg.setEnabled(cbSched.isChecked());
    for (int k = 0; k < rg.getChildCount(); k++) rg.getChildAt(k).setEnabled(cbSched.isChecked());

    cbSched.setOnCheckedChangeListener((b, on) -> {
        rg.setEnabled(on);
        for (int k = 0; k < rg.getChildCount(); k++) rg.getChildAt(k).setEnabled(on);
    });

    box.addView(rg);

    // ==============================
    // BUTTONS
    // ==============================
    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER);
    row.setPadding(0, dp(18), 0, 0);

    android.widget.Button btnCancel = new android.widget.Button(this);
    btnCancel.setText(gr ? "Άκυρο" : "Cancel");
    btnCancel.setAllCaps(false);
    btnCancel.setTextColor(Color.WHITE);
    btnCancel.setBackground(makeGelBtn(0xFFAA1111)); // red

    android.widget.Button btnSave = new android.widget.Button(this);
    btnSave.setText(gr ? "Αποθήκευση" : "Save");
    btnSave.setAllCaps(false);
    btnSave.setTextColor(Color.WHITE);
    btnSave.setBackground(makeGelBtn(0xFF00FF7F)); // neon green

    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(110), 1f);
    lp.setMargins(dp(6), 0, dp(6), 0);
    row.addView(btnCancel, lp);
    row.addView(btnSave, lp);

    box.addView(row);

    AlertDialog dlg = new AlertDialog.Builder(this)
            .setView(box)
            .setCancelable(true)
            .create();

    btnCancel.setOnClickListener(v -> dlg.dismiss());

    btnSave.setOnClickListener(v -> {

        // ---- mini check save ----
        boolean newMini = cbMini.isChecked();
        sp.edit().putBoolean("pulse_enabled", newMini).apply();

        if (newMini) OptimizerMiniPulseScheduler.enable(this);
        else OptimizerMiniPulseScheduler.disable(this);

        // ---- scheduler save ----
        boolean newSched = cbSched.isChecked();
        if (newSched) {

            int days = 7;
            int checked = rg.getCheckedRadioButtonId();
            if (checked == r1.getId()) days = 1;
            else if (checked == r30.getId()) days = 30;

            OptimizerScheduler.enableReminder(this, days);

        } else {
            OptimizerScheduler.disableReminder(this);
        }

        dlg.dismiss();
    });

    dlg.show();
}

private GradientDrawable makeGelBtn(int solidColor) {
    GradientDrawable d = new GradientDrawable();
    d.setColor(solidColor);
    d.setCornerRadius(dp(14));
    d.setStroke(dp(3), 0xFFFFD700);
    return d;
}

@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent); // 🔥 απαραίτητο λόγω singleTask

    if (intent == null) return;

    // 🔹 Νέο mini health system
    if (intent.hasExtra("mini_cpu")) {
        handleMiniSignals(intent);
        return;
    }

    // 🔹 Παλιό gel_issue_type (αν το κρατάς ακόμα)
    if (intent.hasExtra("gel_issue_type")) {
        String issue = intent.getStringExtra("gel_issue_type");
        showSmartDiagnosticPopup(issue);
    }
}

private void handleMiniSignals(Intent intent) {

    boolean cpu = intent.getBooleanExtra("mini_cpu", false);
    boolean thermal = intent.getBooleanExtra("mini_thermal", false);
    boolean cache = intent.getBooleanExtra("mini_cache", false);
    double temp = intent.getDoubleExtra("mini_temp", 0);

    // 🚫 Crash disabled for Mini
    boolean crash = false;

    new android.os.Handler(android.os.Looper.getMainLooper())
            .post(() ->
                    showSmartMiniDiagnostic(cpu, thermal, crash, cache, temp)
            );
}

private void showSmartDiagnosticPopup(String issue) {

    final boolean gr = AppLang.isGreek(this);

    String title = gr
            ? "Ένδειξη επιβάρυνσης"
            : "Health Signal Detected";

    String message = gr
            ? "GEL iDoctor: Το σύστημα εντόπισε πιθανή επιβάρυνση.\n\nΘέλεις να γίνει έλεγχος τώρα;"
            : "GEL iDoctor: The system detected a possible load issue.\n\nRun diagnostic now?";

    new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(gr ? "Έλεγχος" : "Run Check", (d, w) -> {

                openGuidedOptimizerWithPermissionGuard();
            })
            .setNegativeButton(gr ? "Αργότερα" : "Later", null)
            .show();
}


// =========================================================
// GUIDED OPTIMIZER — USAGE ACCESS PERMISSION GUARD
// =========================================================
private void openGuidedOptimizerWithPermissionGuard() {

    if (hasUsageAccess()) {
        launchGuidedOptimizer(false);
        return;
    }

    showUsageAccessRequiredDialog();
}

// =========================================================
// CHECK USAGE ACCESS
// =========================================================
private boolean hasUsageAccess() {

    try {

        AppOpsManager appOps =
                (AppOpsManager) getSystemService(APP_OPS_SERVICE);

        if (appOps == null) {
            return false;
        }

        int mode;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            mode = appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    getPackageName()
            );

        } else {

            mode = appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    getPackageName()
            );
        }

        return mode == AppOpsManager.MODE_ALLOWED;

    } catch (Throwable ignore) {
        return false;
    }
}

// =========================================================
// USAGE ACCESS REQUIRED DIALOG — GEL STYLE
// =========================================================
private void showUsageAccessRequiredDialog() {

    final boolean gr = AppLang.isGreek(this);

    String titleText = gr
            ? "Απαιτείται Πρόσβαση Χρήσης"
            : "Usage Access Required";

    String messageText = gr
            ? "Ο πλήρης Guided Optimiser χρειάζεται την ειδική άδεια "
              + "«Πρόσβαση χρήσης», ώστε να αναλύσει σωστά τη χρήση εφαρμογών.\n\n"
              + "Εάν το Android δεν επιτρέπει την ενεργοποίηση και εμφανίζει "
              + "«Περιορισμένη ρύθμιση», άνοιξε:\n\n"
              + "Ρυθμίσεις → Εφαρμογές → GELiDOCTOR → ⋮ → "
              + "Να επιτρέπονται οι περιορισμένες ρυθμίσεις\n\n"
              + "Μετά επέστρεψε και ενεργοποίησε την Πρόσβαση χρήσης.\n\n"
              + "Μπορείς επίσης να συνεχίσεις τώρα σε Limited Mode. "
              + "Ο optimiser θα εκτελέσει μόνο τους ελέγχους που δεν απαιτούν Usage Access."
            : "The full Guided Optimizer requires the special "
              + "Usage Access permission to analyze app usage correctly.\n\n"
              + "If Android blocks the switch and displays "
              + "\"Restricted setting\", open:\n\n"
              + "Settings → Apps → GELiDOCTOR → ⋮ → "
              + "Allow restricted settings\n\n"
              + "Then return and enable Usage Access.\n\n"
              + "You may also continue now in Limited Mode. "
              + "The optimizer will run only checks that do not require Usage Access.";

    showGelPermissionDialog(
            titleText,
            messageText,
            gr ? "ΑΝΟΙΓΜΑ ΡΥΘΜΙΣΕΩΝ" : "OPEN SETTINGS",
            this::openUsageAccessSettings,
            "LIMITED MODE",
            this::showLimitedModeConfirmation,
            gr ? "ΑΚΥΡΟ" : "CANCEL"
    );
}

// =========================================================
// OPEN USAGE ACCESS SETTINGS
// =========================================================
private void openUsageAccessSettings() {

    final boolean gr = AppLang.isGreek(this);

    try {

        waitingForUsageAccessReturn = true;

        Intent intent =
                new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);

        intent.setData(
                Uri.parse("package:" + getPackageName())
        );

        startActivity(intent);

    } catch (Throwable first) {

        try {

            waitingForUsageAccessReturn = true;

            startActivity(
                    new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            );

        } catch (Throwable second) {

            waitingForUsageAccessReturn = false;

            Toast.makeText(
                    this,
                    gr
                            ? "Δεν ήταν δυνατό να ανοίξουν οι ρυθμίσεις Πρόσβασης χρήσης."
                            : "Unable to open Usage Access settings.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}

// =========================================================
// USAGE ACCESS STILL BLOCKED — GEL STYLE
// =========================================================
private void showUsageAccessStillBlockedDialog() {

    final boolean gr = AppLang.isGreek(this);

    String titleText = gr
            ? "Η άδεια παραμένει κλειδωμένη"
            : "Permission is still blocked";

    String messageText = gr
            ? "Η Πρόσβαση χρήσης δεν ενεργοποιήθηκε.\n\n"
              + "Αν το Android την έχει μπλοκάρει, πήγαινε:\n\n"
              + "Ρυθμίσεις → Εφαρμογές → GELiDOCTOR → ⋮ → "
              + "Να επιτρέπονται οι περιορισμένες ρυθμίσεις\n\n"
              + "Μπορείς να ξαναδοκιμάσεις ή να συνεχίσεις σε Limited Mode."
            : "Usage Access was not enabled.\n\n"
              + "If Android blocked it, open:\n\n"
              + "Settings → Apps → GELiDOCTOR → ⋮ → "
              + "Allow restricted settings\n\n"
              + "You can try again or continue in Limited Mode.";

    showGelPermissionDialog(
            titleText,
            messageText,
            gr ? "ΞΑΝΑ ΡΥΘΜΙΣΕΙΣ" : "OPEN SETTINGS AGAIN",
            this::openUsageAccessSettings,
            "LIMITED MODE",
            this::showLimitedModeConfirmation,
            gr ? "ΑΚΥΡΟ" : "CANCEL"
    );
}

// =========================================================
// LIMITED MODE CONFIRMATION — GEL STYLE
// =========================================================
private void showLimitedModeConfirmation() {

    final boolean gr = AppLang.isGreek(this);

    String titleText = gr
            ? "Guided Optimiser — Limited Mode"
            : "Guided Optimizer — Limited Mode";

    String messageText = gr
            ? "Ο optimiser θα συνεχίσει χωρίς Πρόσβαση χρήσης.\n\n"
              + "Θα παραλειφθούν οι έλεγχοι που απαιτούν ιστορικό χρήσης εφαρμογών. "
              + "Οι υπόλοιποι διαθέσιμοι έλεγχοι θα εκτελεστούν κανονικά."
            : "The optimizer will continue without Usage Access.\n\n"
              + "Checks that require app usage history will be skipped. "
              + "All other available checks will run normally.";

    showGelPermissionDialog(
            titleText,
            messageText,
            gr ? "ΣΥΝΕΧΕΙΑ" : "CONTINUE",
            () -> launchGuidedOptimizer(true),
            null,
            null,
            gr ? "ΑΚΥΡΟ" : "CANCEL"
    );
}

// =========================================================
// GEL PERMISSION DIALOG BUILDER
// =========================================================
private void showGelPermissionDialog(
        String titleText,
        String messageText,
        String positiveText,
        Runnable positiveAction,
        String neutralText,
        Runnable neutralAction,
        String negativeText
) {

    LinearLayout outer = new LinearLayout(this);
    outer.setOrientation(LinearLayout.VERTICAL);
    outer.setPadding(dp(18), dp(18), dp(18), dp(18));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF080808);
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(3), 0xFFFFD700);
    outer.setBackground(bg);

    TextView title = new TextView(this);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(20f);
    title.setTypeface(Typeface.DEFAULT_BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(dp(8), dp(4), dp(8), dp(14));
    outer.addView(title);

    ScrollView scroll = new ScrollView(this);
    scroll.setFillViewport(false);

    TextView message = new TextView(this);
    message.setText(messageText);
    message.setTextColor(0xFF00FF9C);
    message.setTextSize(15f);
    message.setLineSpacing(0f, 1.18f);
    message.setPadding(dp(8), dp(4), dp(8), dp(14));

    scroll.addView(message);

    LinearLayout.LayoutParams scrollLp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
            );
    scrollLp.setMargins(0, 0, 0, dp(12));
    outer.addView(scroll, scrollLp);

    LinearLayout buttons = new LinearLayout(this);
    buttons.setOrientation(LinearLayout.VERTICAL);
    buttons.setPadding(0, dp(4), 0, 0);

    Button positive = new Button(this);
    positive.setText(positiveText);
    positive.setAllCaps(false);
    positive.setTextColor(0xFF00FF7F);
    positive.setTextSize(16f);
    positive.setTypeface(Typeface.DEFAULT_BOLD);
    positive.setGravity(Gravity.CENTER);
    positive.setPadding(dp(12), dp(14), dp(12), dp(14));
    positive.setMinHeight(dp(110));
    positive.setBackgroundResource(R.drawable.gel_btn_neon_outline);

    LinearLayout.LayoutParams btnLp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(110)
            );
    btnLp.setMargins(0, dp(8), 0, 0);

    buttons.addView(positive, btnLp);

    Button neutral = null;

    if (neutralText != null && neutralAction != null) {
        neutral = new Button(this);
        neutral.setText(neutralText);
        neutral.setAllCaps(false);
        neutral.setTextColor(0xFFFFD700);
        neutral.setTextSize(16f);
        neutral.setTypeface(Typeface.DEFAULT_BOLD);
        neutral.setGravity(Gravity.CENTER);
        neutral.setPadding(dp(12), dp(14), dp(12), dp(14));
        neutral.setMinHeight(dp(110));
        neutral.setBackgroundResource(R.drawable.gel_btn_outline_selector);
        buttons.addView(neutral, new LinearLayout.LayoutParams(btnLp));
    }

    Button negative = new Button(this);
    negative.setText(negativeText);
    negative.setAllCaps(false);
    negative.setTextColor(Color.WHITE);
    negative.setTextSize(16f);
    negative.setTypeface(Typeface.DEFAULT_BOLD);
    negative.setGravity(Gravity.CENTER);
    negative.setPadding(dp(12), dp(14), dp(12), dp(14));
    negative.setMinHeight(dp(110));
    negative.setBackgroundResource(R.drawable.gel_btn_outline_selector);
    buttons.addView(negative, new LinearLayout.LayoutParams(btnLp));

    outer.addView(buttons);

    AlertDialog dialog =
            new AlertDialog.Builder(this)
                    .setView(outer)
                    .setCancelable(true)
                    .create();

    positive.setOnClickListener(v -> {
        dialog.dismiss();
        if (positiveAction != null) {
            positiveAction.run();
        }
    });

    if (neutral != null) {
        Button neutralButton = neutral;
        neutralButton.setOnClickListener(v -> {
            dialog.dismiss();
            neutralAction.run();
        });
    }

    negative.setOnClickListener(v -> dialog.dismiss());

    dialog.setOnShowListener(d -> {
        Window w = dialog.getWindow();
        if (w != null) {
            w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            w.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.94f),
                    (int) (getResources().getDisplayMetrics().heightPixels * 0.82f)
            );
        }
    });

    dialog.show();

    Window w = dialog.getWindow();
    if (w != null) {
        w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        w.setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.94f),
                (int) (getResources().getDisplayMetrics().heightPixels * 0.82f)
        );
    }
}

// =========================================================
// LAUNCH GUIDED OPTIMIZER
// =========================================================
private void launchGuidedOptimizer(boolean limitedMode) {

    final boolean gr = AppLang.isGreek(this);

    try {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        GuidedOptimizerActivity.class
                );

        intent.putExtra(
                "gel_limited_mode",
                limitedMode
        );

        intent.putExtra(
                "gel_usage_access_granted",
                !limitedMode && hasUsageAccess()
        );

        startActivity(intent);

        if (limitedMode) {

            Toast.makeText(
                    this,
                    gr
                            ? "Guided Optimiser: εκτέλεση σε Limited Mode."
                            : "Guided Optimizer: running in Limited Mode.",
                    Toast.LENGTH_LONG
            ).show();
        }

    } catch (Throwable t) {

        Toast.makeText(
                this,
                gr
                        ? "Αδυναμία ανοίγματος του Guided Optimiser."
                        : "Cannot open Guided Optimizer.",
                Toast.LENGTH_SHORT
        ).show();
    }
}

private void showSmartMiniDiagnostic(
        boolean cpu,
        boolean thermal,
        boolean crash,
        boolean cache,
        double temp
) {

    Intent i = new Intent(this, OptimizerDiagnosticActivity.class);

    i.putExtra("mini_cpu", cpu);
    i.putExtra("mini_thermal", thermal);
    i.putExtra("mini_crash", crash);
    i.putExtra("mini_cache", cache);
    i.putExtra("mini_temp", temp);

    startActivity(i);
}

@Override
public void onRequestPermissionsResult(int requestCode,
                                       @NonNull String[] permissions,
                                       @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == 1001) {
        // nothing else needed for now
    }
}

@Override
protected void onPause() {
    super.onPause();
    try { AppTTS.stop(); } catch (Throwable ignore) {}
}

private void hardRestart() {
    Intent i = getIntent();
    i.putExtra("force_welcome", true);
    finish();
    startActivity(i);
}

// =========================================================
// HELPERS
// =========================================================

private LinearLayout buildMuteRow() {
    final boolean gr = AppLang.isGreek(this);

    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER_VERTICAL);
    row.setPadding(0, dp(8), 0, dp(16));

    CheckBox muteCheck = new CheckBox(this);
    muteCheck.setChecked(AppTTS.isMuted(this));
    muteCheck.setPadding(0, 0, dp(6), 0);

    TextView label = new TextView(this);
    label.setText(gr ? "Σίγαση φωνητικών οδηγιών"
                     : "Mute voice instructions");
    label.setTextColor(Color.WHITE);
    label.setTextSize(14f);

    View.OnClickListener toggle = v -> {
        boolean newState = !AppTTS.isMuted(this);
        AppTTS.setMuted(this, newState);
        muteCheck.setChecked(newState);
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

private void syncReturnButtonText() {
    Button b = findViewById(R.id.btnReturnAndroid);
    if (b != null) {
        if ("apple".equals(getSavedPlatform())) {
            b.setText(getString(R.string.return_android));
        } else {
            b.setText(getString(R.string.return_apple));
        }
    }
}

private boolean isWelcomeDisabled() {
return getSharedPreferences(PREFS, MODE_PRIVATE)
.getBoolean("welcome_disabled", false);
}

private void disableWelcomeForever() {
getSharedPreferences(PREFS, MODE_PRIVATE)
.edit()
.putBoolean("welcome_disabled", true)
.apply();
}

private void savePlatform(String mode) {
getSharedPreferences(PREFS, MODE_PRIVATE)
.edit().putString(KEY_PLATFORM, mode).apply();
}

private String getSavedPlatform() {
return getSharedPreferences(PREFS, MODE_PRIVATE)
.getString(KEY_PLATFORM, "android");
}

private boolean isAppleMode() {
return "apple".equals(getSavedPlatform());
}

private AlertDialog.Builder buildNeonDialog() {
AlertDialog.Builder b = new AlertDialog.Builder(this);
return b;
}

private ArrayAdapter<String> neonAdapter(String[] names) {

    return new ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            names
    ) {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = (TextView) super.getView(position, convertView, parent);
            tv.setTextColor(0xFF00FF9C); // neon green
            tv.setTypeface(null, Typeface.BOLD);
            return tv;
        }
    };
}

// =========================================================
// TTS - WELCOME
// =========================================================
private void speakWelcomeTTS() {

if (!welcomeShown) return;
if (AppTTS.isMuted(this)) return;

if (AppLang.isGreek(this)) {

AppTTS.speak(
this,
getWelcomeTextGR()
);

} else {

AppTTS.speak(
this,
getWelcomeTextEN()
);
}
}

// =========================================================
// WELCOME TEXT
// =========================================================
private String getWelcomeTextEN() {
return
"Although this is an Android application, " +
"it is the only tool on the market that can also help you " +
"understand problems on Apple devices.\n\n" +
"By importing panic logs from your iPhone or iPad, " +
"we analyze what really happened inside your device.\n\n" +
"You will understand:\n" +
"• what your panic logs mean.\n" +
"• what caused the issue,\n" +
"• and how you can solve it.\n\n" +
"Choose what you want to explore:\n" +
"your Android device or another Apple device.";
}

private String getWelcomeTextGR() {
return
"Παρότι αυτή είναι εφαρμογή Android, " +
"είναι το μοναδικό εργαλείο στην αγορά που μπορεί να σε βοηθήσει " +
"να καταλάβεις προβλήματα και σε συσκευές Apple.\n\n" +
"Με την εισαγωγή panic logs από iPhone ή iPad, " +
"αναλύουμε τι συνέβη πραγματικά μέσα στη συσκευή σου.\n\n" +
"Θα καταλάβεις:\n" +
"• τι σημαίνουν τα panic logs.\n" +
"• τι προκάλεσε το πρόβλημα,\n" +
"• και πώς μπορείς να το λύσεις.\n\n" +
"Διάλεξε τι θέλεις να εξερευνήσεις:\n" +
"τη συσκευή Android σου ή μια άλλη συσκευή Apple.";
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
private void showWelcomePopup() {

if (welcomeShown) return;
welcomeShown = true;

boolean gr = AppLang.isGreek(this);

AlertDialog.Builder b =
new AlertDialog.Builder(MainActivity.this);

b.setCancelable(true);

// ================= ROOT =================
LinearLayout root = new LinearLayout(MainActivity.this);
root.setOrientation(LinearLayout.VERTICAL);
root.setPadding(dp(24), dp(22), dp(24), dp(20));

GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF000000); // Μαύρο
bg.setCornerRadius(dp(14));
bg.setStroke(dp(4), 0xFFFFD700); // Χρυσό περίγραμμα
root.setBackground(bg);

// ================= TITLE =================
welcomeTitle = new TextView(MainActivity.this);
welcomeTitle.setText(
AppLang.isGreek(this) ? "ΚΑΛΩΣ ΗΡΘΑΤΕ" : "WELCOME"
);
welcomeTitle.setTextColor(Color.WHITE);
welcomeTitle.setTextSize(19f);
welcomeTitle.setTypeface(null, Typeface.BOLD);
welcomeTitle.setGravity(Gravity.CENTER);
welcomeTitle.setPadding(0, 0, 0, dp(14));
root.addView(welcomeTitle);

// ================= MESSAGE =================
welcomeMessage = new TextView(MainActivity.this);
welcomeMessage.setText(
AppLang.isGreek(this)
? getWelcomeTextGR()
: getWelcomeTextEN()
);

welcomeMessage.setTextColor(0xFF00FF9C); // Neon green
welcomeMessage.setTextSize(15f);
welcomeMessage.setGravity(Gravity.CENTER);
welcomeMessage.setLineSpacing(0f, 1.15f);
welcomeMessage.setPadding(dp(6), 0, dp(6), dp(18));

welcomeMessage.setVerticalScrollBarEnabled(true);
welcomeMessage.setMovementMethod(
        android.text.method.ScrollingMovementMethod.getInstance()
);

welcomeMessage.setFocusable(true);
welcomeMessage.setFocusableInTouchMode(true);

root.addView(welcomeMessage);

// ================= MUTE ROW =================
root.addView(buildMuteRow());

// ================= LANGUAGE SPINNER =================
Spinner langSpinner = new Spinner(MainActivity.this);

ArrayAdapter<String> adapter =
        new ArrayAdapter<String>(
                MainActivity.this,
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

                if (!newLang.equals(LocaleHelper.getLang(MainActivity.this))) {

                    LocaleHelper.set(MainActivity.this, newLang);

                    try { AppTTS.stop(); } catch (Throwable ignore) {}

                    // 🔥 Hard restart activity + force reopen welcome
                    Intent i = getIntent();
                    i.putExtra("force_welcome", true);

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
LinearLayout langBox = new LinearLayout(MainActivity.this);
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
cb.setPadding(0, dp(8), 0, dp(16));
root.addView(cb);

// ================= OK BUTTON =================
Button okBtn = new Button(MainActivity.this);
okBtn.setText("OK");
okBtn.setAllCaps(false);
okBtn.setTextColor(Color.WHITE);
okBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f);
okBtn.setTypeface(null, Typeface.BOLD);

GradientDrawable okBg = new GradientDrawable();
okBg.setColor(0xFF00E676); // Neon green
okBg.setCornerRadius(dp(12));
okBg.setStroke(dp(3), 0xFFFFD700); // Χρυσό περίγραμμα
okBtn.setBackground(okBg);

LinearLayout.LayoutParams okLp =
new LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
dp(140)
);
okLp.setMargins(dp(6), dp(6), dp(6), 0);
okBtn.setLayoutParams(okLp);

root.addView(okBtn);

// ================= SET VIEW =================
b.setView(root);
final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// 🔴 FLAG (μία φορά μόνο)
welcomePopupActive = true;

d.setOnDismissListener(dialog -> {
    welcomePopupActive = false;
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    welcomeShown = false;
});

d.setOnCancelListener(dialog -> {
    welcomePopupActive = false;
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    welcomeShown = false;
});

d.setOnShowListener(dialog -> {

    Window w = d.getWindow();
    if (w != null) {
        w.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int)(getResources().getDisplayMetrics().heightPixels * 0.85)
        );
    }

    // 🔴 SAFE TTS
    new Handler(Looper.getMainLooper()).postDelayed(() -> {

        if (!welcomePopupActive) return;
        if (AppTTS.isMuted(MainActivity.this)) return;
        if (!welcomeShown) return;

        speakWelcomeTTS();

    }, 120);
});

d.show();

if (d.getWindow() != null) {
    d.getWindow().setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            (int) (getResources().getDisplayMetrics().heightPixels * 0.85)
    );
}

// --------------------------------------------
// OK BUTTON
// --------------------------------------------
okBtn.setOnClickListener(v -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}

    welcomeShown = false;

    if (cb.isChecked()) {
        disableWelcomeForever();
    }

    d.dismiss();
    showPlatformSelectPopup();
});
}

// =========================================================
// PLATFORM SELECT - FINAL, CLEAN
// =========================================================
private void showPlatformSelectPopup() {

boolean gr = AppLang.isGreek(this);

AlertDialog.Builder b =
new AlertDialog.Builder(
MainActivity.this,
android.R.style.Theme_Material_Dialog_NoActionBar
);

LinearLayout root = new LinearLayout(this);
root.setOrientation(LinearLayout.VERTICAL);
root.setGravity(Gravity.CENTER_HORIZONTAL);
root.setPadding(dp(24), dp(20), dp(24), dp(18));

GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF101010);
bg.setCornerRadius(dp(10));
bg.setStroke(dp(3), 0xFFFFD700);
root.setBackground(bg);

// ================= TITLE =================
TextView t = new TextView(this);
t.setText(gr ? "ΕΠΙΛΟΓΗ ΣΥΣΚΕΥΗΣ" : "SELECT DEVICE");
t.setTextColor(Color.WHITE);
t.setTextSize(20f);
t.setTypeface(null, Typeface.BOLD);
t.setGravity(Gravity.CENTER);
t.setPadding(0, 0, 0, dp(38));
root.addView(t);

// ================= ANDROID BUTTON =================
TextView androidBtn = new TextView(this);
androidBtn.setText(gr
? "🤖  Η ANDROID ΣΥΣΚΕΥΗ ΜΟΥ"
: "🤖  MY ANDROID DEVICE");
androidBtn.setTextColor(Color.WHITE);
androidBtn.setTextSize(18f);
androidBtn.setTypeface(null, Typeface.BOLD);
androidBtn.setGravity(Gravity.CENTER);
androidBtn.setPadding(0, dp(20), 0, dp(20));

GradientDrawable bgAndroid = new GradientDrawable();
bgAndroid.setColor(0xFF3DDC84);
bgAndroid.setCornerRadius(dp(12));
bgAndroid.setStroke(dp(3), 0xFFFFD700);
androidBtn.setBackground(bgAndroid);

LinearLayout.LayoutParams lpBtn =
new LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
dp(150)
);
lpBtn.setMargins(dp(8), dp(10), dp(8), 0);
androidBtn.setLayoutParams(lpBtn);

// ================= APPLE BUTTON =================
TextView appleBtn = new TextView(this);
appleBtn.setText(gr
? "🍎  ΑΛΛΗ ΣΥΣΚΕΥΗ APPLE"
: "🍎  OTHER APPLE DEVICE");
appleBtn.setTextColor(Color.WHITE);
appleBtn.setTextSize(18f);
appleBtn.setTypeface(null, Typeface.BOLD);
appleBtn.setGravity(Gravity.CENTER);
appleBtn.setPadding(0, dp(20), 0, dp(20));

GradientDrawable bgApple = new GradientDrawable();
bgApple.setColor(0xFF1C1C1E);
bgApple.setCornerRadius(dp(12));
bgApple.setStroke(dp(3), 0xFFFFD700);
appleBtn.setBackground(bgApple);

LinearLayout.LayoutParams lpBtn2 =
new LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
dp(150)
);
lpBtn2.setMargins(dp(8), dp(14), dp(8), 0);
appleBtn.setLayoutParams(lpBtn2);

root.addView(androidBtn);
root.addView(appleBtn);

// ================= SET VIEW =================
b.setView(root);

AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// --------------------------------------------
// STATE
// --------------------------------------------
welcomeShown = true;

// --------------------------------------------
// STOP ON DISMISS
// --------------------------------------------
d.setOnDismissListener(dialog -> {
try { AppTTS.stop(); } catch (Throwable ignore) {}
welcomeShown = false;
});

// --------------------------------------------
// STOP ON BACK (CANCEL)
// --------------------------------------------
d.setOnCancelListener(dialog -> {
try { AppTTS.stop(); } catch (Throwable ignore) {}
welcomeShown = false;
});

// --------------------------------------------
// SHOW
// --------------------------------------------
d.show();

// --------------------------------------------
// WINDOW LAYOUT AFTER SHOW
// --------------------------------------------
Window w = d.getWindow();
if (w != null) {
w.setLayout(
ViewGroup.LayoutParams.MATCH_PARENT,
ViewGroup.LayoutParams.WRAP_CONTENT
);
w.getDecorView().setPadding(dp(16), 0, dp(16), 0);
}

// --------------------------------------------
// ANDROID BUTTON
// --------------------------------------------
androidBtn.setOnClickListener(v -> {

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    welcomeShown = false;

    savePlatform("android");

    d.dismiss();

    recreate();
});

// --------------------------------------------
// APPLE BUTTON
// --------------------------------------------
appleBtn.setOnClickListener(v -> {

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    welcomeShown = false;

    savePlatform("apple");

    d.dismiss();

    recreate();
});
}

// =========================================================
// APPLE ENTRY POINT
// =========================================================
private void openAppleInternalPeripherals() {
applyAppleModeUI();
}

// =========================================================
// ANDROID MODE UI FILTER
// =========================================================
private void applyAndroidModeUI() {

hide(R.id.btnAppleDeviceDeclaration);
hide(R.id.appleInfoPanel);
hide(R.id.btnAppleInfoContinue);

show(R.id.section_system);
show(R.id.section_clean);
show(R.id.section_junk);
show(R.id.section_performance);

show(R.id.btnCpuRamLive);
show(R.id.btnInternetSpeedTest);
show(R.id.btnCleanAll);
show(R.id.btnBrowserCache);
show(R.id.btnAppCache);
show(R.id.btnAppManager);
show(R.id.btnGuidedOptimizer);

show(R.id.btnDonate);
show(R.id.btnPhoneInfoInternal);
show(R.id.btnPhoneInfoPeripherals);
show(R.id.btnDiagnostics);

// ANDROID DIAGNOSTICS - LOCALIZED + RESET STYLE
View diagBtn = findViewById(R.id.btnDiagnostics);
if (diagBtn instanceof TextView) {
TextView tv = (TextView) diagBtn;
tv.setText(R.string.diagnostics_android);
tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
}
}

// =========================================================
// APPLE MODE UI FILTER
// =========================================================
private void applyAppleModeUI() {

hide(R.id.section_system);
hide(R.id.section_clean);
hide(R.id.section_junk);
hide(R.id.section_performance);

hide(R.id.btnCpuRamLive);
hide(R.id.btnInternetSpeedTest);
hide(R.id.btnCleanAll);
hide(R.id.btnBrowserCache);
hide(R.id.btnAppCache);
hide(R.id.btnAppManager);
hide(R.id.btnGuidedOptimizer);

show(R.id.btnDonate);
show(R.id.btnPhoneInfoInternal);
show(R.id.btnPhoneInfoPeripherals);
show(R.id.btnDiagnostics);
show(R.id.btnAppleDeviceDeclaration);
show(R.id.appleInfoPanel);
show(R.id.btnAppleInfoContinue);

// APPLE DIAGNOSTICS - LOCALIZED + EMPHASIZED
View v = findViewById(R.id.btnDiagnostics);
if (v instanceof TextView) {
TextView tv = (TextView) v;
tv.setText(R.string.diagnostics_apple);
tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
}
}

private void hide(int id){
View v = findViewById(id);
if (v != null) v.setVisibility(View.GONE);
}

private void show(int id){
View v = findViewById(id);
if (v != null) v.setVisibility(View.VISIBLE);
}

// =========================================================
// LANGUAGE SYSTEM
// =========================================================
private void setupLangButtons() {
View bGR = findViewById(R.id.btnLangGR);
View bEN = findViewById(R.id.btnLangEN);

if (bGR != null) bGR.setOnClickListener(v -> changeLang("el"));
if (bEN != null) bEN.setOnClickListener(v -> changeLang("en"));
}

private void changeLang(String code) {

    if (code.equals(LocaleHelper.getLang(this))) return;

    LocaleHelper.set(this, code);

    Intent i = getIntent();
    i.putExtra("skip_welcome_once", true);

    finish();
    startActivity(i);
}

// =========================================================
// DONATE
// =========================================================
private void setupDonate() {

    View b = findViewById(R.id.btnDonate);

    if (b != null) {

        b.setOnClickListener(v -> {

            try {

                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=gdiolitsis@yahoo.com&currency_code=EUR")
                ));

            } catch (Exception e) {

                Toast.makeText(
                        this,
                        "Cannot open browser",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}

// =========================================================
// BUTTONS - PLATFORM AWARE
// =========================================================
private void setupButtons() {

bind(R.id.btnAppleDeviceDeclaration,
this::showAppleDeviceDeclarationPopup);

// ==========================
// APPLE INFO — OK / CONTINUE TO LABS
// ==========================
bind(R.id.btnAppleInfoContinue, () -> {
    startActivity(new Intent(
            this,
            IPhoneLabsActivity.class
    ));
});

// ==========================
//  INTERNAL INFO
// ==========================
bind(R.id.btnPhoneInfoInternal, () -> {
if (isAppleMode()) {
startActivity(new Intent(
this,
AppleDeviceInfoInternalActivity.class
));
} else {
startActivity(new Intent(
this,
DeviceInfoInternalActivity.class
));
}
});

// ==========================
//  PERIPHERALS INFO
// ==========================
bind(R.id.btnPhoneInfoPeripherals, () -> {
if (isAppleMode()) {
startActivity(new Intent(
this,
AppleDeviceInfoPeripheralsActivity.class
));
} else {
startActivity(new Intent(
this,
DeviceInfoPeripheralsActivity.class
));
}
});

// ==========================
// ⚙️ ΥΠΟΛΟΙΠΑ ΚΟΥΜΠΙΑ
// ==========================
bind(R.id.btnCpuRamLive,
() -> startActivity(new Intent(this, CpuRamLiveActivity.class)));

bind(R.id.btnInternetSpeedTest,
this::openInternetSpeedTest);

bind(R.id.btnCleanAll,
() -> GELCleaner.deepClean(this,this));

bind(R.id.btnBrowserCache,
this::showBrowserPicker);

View appCache = findViewById(R.id.btnAppCache);
if (appCache != null) {

appCache.setOnClickListener(v -> {
try {

Intent i = new Intent(this, AppListActivity.class);
i.putExtra("mode", "cache"); // CACHE MODE
startActivity(i);

} catch (Exception e) {
Toast.makeText(this, "Cannot open App List", Toast.LENGTH_SHORT).show();
}
});
}

bind(R.id.btnDiagnostics, () -> {
startActivity(new Intent(
this,
DiagnosisMenuActivity.class
));
});

    // ==========================
    // RETURN PLATFORM  👇 ΒΑΛΕ ΤΟ ΕΔΩ
    // ==========================
    bind(R.id.btnReturnAndroid, () -> {

        if (isAppleMode()) {
            savePlatform("android");
        } else {
            savePlatform("apple");
        }

        recreate();
    });
}

// =========================================================
// INTERNET SPEED TEST
// =========================================================
private void openInternetSpeedTest() {
    try {
        startActivity(new Intent(
                MainActivity.this,
                SpeedTestActivity.class
        ));
    } catch (Throwable t) {
        Toast.makeText(
                MainActivity.this,
                AppLang.isGreek(this)
                        ? "Αδυναμία ανοίγματος του ελέγχου ταχύτητας."
                        : "Cannot open Internet Speed Test.",
                Toast.LENGTH_SHORT
        ).show();
    }
}

// =========================================================
// BIND HELPER
// =========================================================
private void bind(int id, Runnable fn){
View b = findViewById(id);
if (b != null){
b.setOnClickListener(v -> {
try { fn.run(); }
catch(Throwable t){
Toast.makeText(this,
"Action failed: "+t.getMessage(),
Toast.LENGTH_SHORT).show();
}
});
}
}

// =========================================================
//  APPLE DEVICE DECLARATION
// =========================================================
private void showAppleDeviceDeclarationPopup() {

AlertDialog.Builder b =
new AlertDialog.Builder(this,
android.R.style.Theme_Material_Dialog_Alert);

LinearLayout root = new LinearLayout(this);
root.setOrientation(LinearLayout.VERTICAL);
root.setPadding(dp(20), dp(20), dp(20), dp(20));

GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF000000);
bg.setCornerRadius(dp(10));
bg.setStroke(dp(3), 0xFFFFD700);
root.setBackground(bg);

TextView title = new TextView(this);
title.setText("Select your Apple device");
title.setTextColor(Color.WHITE);
title.setTextSize(20f);
title.setTypeface(null, Typeface.BOLD);
title.setGravity(Gravity.CENTER);
title.setPadding(0, 0, 0, dp(16));
root.addView(title);

// ==========================
// ðŸ“± iPHONE BUTTON
// ==========================
Button iphoneBtn = new Button(this);
iphoneBtn.setIncludeFontPadding(false);
iphoneBtn.setText("📱  iPHONE");
iphoneBtn.setAllCaps(false);
iphoneBtn.setTextColor(Color.WHITE);
iphoneBtn.setTextSize(16f);

GradientDrawable iphoneBg = new GradientDrawable();
iphoneBg.setColor(0xFF000000);
iphoneBg.setCornerRadius(dp(10));
iphoneBg.setStroke(dp(3), 0xFFFFD700);
iphoneBtn.setBackground(iphoneBg);

LinearLayout.LayoutParams lpIphone =
new LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
dp(150)
);
lpIphone.setMargins(0, dp(12), 0, 0);
iphoneBtn.setLayoutParams(lpIphone);
iphoneBtn.setPadding(dp(16), dp(14), dp(16), dp(14));

// ==========================
// ðŸ“² iPAD BUTTON
// ==========================
Button ipadBtn = new Button(this);
ipadBtn.setIncludeFontPadding(false);
ipadBtn.setText("📲  iPAD");
ipadBtn.setAllCaps(false);
ipadBtn.setTextColor(Color.WHITE);
ipadBtn.setTextSize(16f);

GradientDrawable ipadBg = new GradientDrawable();
ipadBg.setColor(0xFF000000);
ipadBg.setCornerRadius(dp(10));
ipadBg.setStroke(dp(3), 0xFFFFD700);
ipadBtn.setBackground(ipadBg);

LinearLayout.LayoutParams lpIpad =
new LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
dp(150)
);
lpIpad.setMargins(0, dp(12), 0, 0);
ipadBtn.setLayoutParams(lpIpad);
ipadBtn.setPadding(dp(16), dp(14), dp(16), dp(14));

// ==========================
// ADD TO BOX
// ==========================
root.addView(iphoneBtn);
root.addView(ipadBtn);

b.setView(root);
AlertDialog d = b.create();
if (d.getWindow() != null)
d.getWindow().setBackgroundDrawable(
new ColorDrawable(Color.TRANSPARENT));

// --------------------------------------------
// SHOW
// --------------------------------------------
d.show();
}

// =========================================================
//  MODEL PICKER - GEL STYLE (FINAL)
// =========================================================
private void showAppleModelPicker(String type) {

String[] models = "iphone".equals(type)
? new String[]{

// =====================
// iPhone 16 Series
// =====================
"iPhone 16",
"iPhone 16 Pro",
"iPhone 16 Pro Max",

// =====================
// iPhone 15 Series
// =====================
"iPhone 15",
"iPhone 15 Pro",
"iPhone 15 Pro Max",

// =====================
// iPhone 14 Series
// =====================
"iPhone 14",
"iPhone 14 Pro",
"iPhone 14 Pro Max",

// =====================
// iPhone 13 Series
// =====================
"iPhone 13",
"iPhone 13 Pro",
"iPhone 13 Pro Max",

// =====================
// iPhone 12 Series
// =====================
"iPhone 12",
"iPhone 12 Pro",
"iPhone 12 Pro Max",

// =====================
// iPhone 11 Series
// =====================
"iPhone 11",
"iPhone 11 Pro",
"iPhone 11 Pro Max"
}
: new String[]{

// =====================
// iPad Pro (M4)
// =====================
"iPad Pro 11 (M4)",
"iPad Pro 13 (M4)",

// =====================
// iPad Pro (M2)
// =====================
"iPad Pro 11 (M2)",
"iPad Pro 12.9 (M2)",

// =====================
// iPad Pro (M1)
// =====================
"iPad Pro 11 (M1)",
"iPad Pro 12.9 (M1)",

// =====================
// iPad Air
// =====================
"iPad Air 13 (M2)",
"iPad Air 11 (M2)",
"iPad Air (M1)",

// =====================
// iPad mini
// =====================
"iPad mini 6"
};

AlertDialog.Builder b =
new AlertDialog.Builder(this,
android.R.style.Theme_Material_Dialog_Alert);

LinearLayout root = new LinearLayout(this);
root.setOrientation(LinearLayout.VERTICAL);
root.setPadding(dp(18), dp(18), dp(18), dp(18));

GradientDrawable bg = new GradientDrawable();
bg.setColor(Color.BLACK);
bg.setCornerRadius(dp(10));
bg.setStroke(dp(3), 0xFFFFD700);
root.setBackground(bg);

TextView title = new TextView(this);
title.setText("Select Apple Model");
title.setTextColor(Color.WHITE);

root.addView(title);

ListView list = new ListView(this);
list.setDivider(null);
list.setDividerHeight(0);

ArrayAdapter < String > adapter =
new ArrayAdapter < String > (
this,
android.R.layout.simple_list_item_1,
models
) {
@Override
public View getView(int position, View convertView, ViewGroup parent) {
TextView tv = (TextView) super.getView(position, convertView, parent);
tv.setTextColor(0xFF00FF9C);
tv.setTextSize(16f);
tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
tv.setPadding(dp(14), dp(14), dp(14), dp(14));
tv.setBackground(null);
return tv;
}
};

list.setAdapter(adapter);
root.addView(list);
b.setView(root);

AlertDialog d = b.create();
if (d.getWindow() != null)
d.getWindow().setBackgroundDrawable(
new ColorDrawable(Color.TRANSPARENT));

d.show();

// =========================
// ACTION
// =========================
list.setOnItemClickListener((parent, view, position, id) -> {

String rawModel = models[position];
String normalizedModel = normalizeAppleModel(rawModel);

saveAppleDevice(type, normalizedModel);

TextView btn = findViewById(R.id.btnAppleDeviceDeclaration);
if (btn != null) {
btn.setText("🍎 " + type.toUpperCase(Locale.US)
+ " — " + rawModel);
}

Toast.makeText(
this,
"Selected: " + rawModel,
Toast.LENGTH_SHORT
).show();

d.dismiss();
});
}

// =========================================================
// NORMALIZE APPLE MODEL - MATCH iPadSpecs / AppleSpecs
// =========================================================
private String normalizeAppleModel(String raw) {

if (raw == null) return null;

String m = raw.trim();

// iPad Pro
if (m.equals("iPad Pro 11 (M2)")) return "iPad Pro 11 M2";
if (m.equals("iPad Pro 12.9 (M2)")) return "iPad Pro 12.9 M2";
if (m.equals("iPad Pro 11 (M1)")) return "iPad Pro 11 M1";
if (m.equals("iPad Pro 12.9 (M1)")) return "iPad Pro 12.9 M1";

// iPad Air
if (m.equals("iPad Air 11 (M2)")) return "iPad Air 11 M2";
if (m.equals("iPad Air 13 (M2)")) return "iPad Air 13 M2";
if (m.equals("iPad Air (M1)")) return "iPad Air M1";

// iPad mini
if (m.equals("iPad mini 6")) return "iPad mini 6";

// iPhones are already correct
return m;
}

// =========================================================
// SAVE SELECTION (LOCKED KEYS)
// =========================================================
private void saveAppleDevice(String type, String model) {

getSharedPreferences(PREFS, MODE_PRIVATE)
.edit()
.putString("apple_type", type)
.putString("apple_model", model)
.apply();
}

// =========================================================
// BROWSER PICKER - DYNAMIC (REAL BROWSERS ONLY)
// =========================================================
private void showBrowserPicker() {

PackageManager pm = getPackageManager();

// -----------------------------------------------------
// FIND REAL BROWSERS
// -----------------------------------------------------
Map < String, String > apps = new LinkedHashMap<>();

Intent browserIntent = new Intent(Intent.ACTION_MAIN);
browserIntent.addCategory(Intent.CATEGORY_APP_BROWSER);

List < ResolveInfo > browsers =
pm.queryIntentActivities(browserIntent, 0);

if (browsers != null) {
for (ResolveInfo ri : browsers) {

if (ri.activityInfo == null) continue;

String pkg = ri.activityInfo.packageName;
CharSequence label = ri.loadLabel(pm);

if (pkg == null || label == null) continue;

// verify http support
Intent httpTest = new Intent(Intent.ACTION_VIEW,
Uri.parse("http://www.example.com"));
httpTest.setPackage(pkg);

List < ResolveInfo > httpHandlers =
pm.queryIntentActivities(httpTest, 0);

if (httpHandlers == null || httpHandlers.isEmpty())
continue;

apps.put(label.toString(), pkg);
}
}

// -----------------------------------------------------
// HANDLE RESULTS
// -----------------------------------------------------
if (apps.isEmpty()) {
Toast.makeText(this, "No browsers found.", Toast.LENGTH_SHORT).show();
return;
}

if (apps.size() == 1) {
openAppInfo(apps.values().iterator().next());
return;
}

String[] names = apps.keySet().toArray(new String[0]);

// -----------------------------------------------------
// POPUP
// -----------------------------------------------------
AlertDialog.Builder builder = buildNeonDialog();

TextView title = new TextView(this);
title.setText("Select Browser");
title.setTextColor(0xFFFFFFFF);
title.setTextSize(18f);
title.setTypeface(null, Typeface.BOLD);
title.setGravity(Gravity.CENTER);
title.setPadding(dp(16), dp(14), dp(16), dp(10));

title.setLayoutParams(
new LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
LinearLayout.LayoutParams.WRAP_CONTENT
)
);

builder.setCustomTitle(title);

builder.setAdapter(neonAdapter(names), (d, w) -> {
String pkg = apps.get(names[w]);
openAppInfo(pkg);
});

AlertDialog dialog = builder.create();
dialog.show();

Window window = dialog.getWindow();
if (window != null) {
GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF000000);
bg.setCornerRadius(dp(10));
bg.setStroke(dp(3), 0xFFFFD700);
window.setBackgroundDrawable(bg);
}
}

// =========================================================
// OPEN APP INFO (for Browser Picker)
// =========================================================
private void openAppInfo(String pkg) {
try {
Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
i.setData(Uri.parse("package:" + pkg));
startActivity(i);
} catch (Exception e) {
Toast.makeText(this, "Cannot open App Info", Toast.LENGTH_SHORT).show();
}
}

}
