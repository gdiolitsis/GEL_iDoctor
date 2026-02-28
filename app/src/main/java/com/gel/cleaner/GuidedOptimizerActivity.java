// GDiolitsis Engine Lab (GEL)
// GuidedOptimizerActivity — FINAL STABLE VERSION

package com.gel.cleaner;

import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.graphics.Color;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public final class GuidedOptimizerActivity extends AppCompatActivity {

    private boolean gr;
    private int step = 0;
    
    private boolean returnedFromUsageScreen = false;
    private boolean returnedFromDnsScreen = false;
    
    private boolean isSchedulerEnabled() {
    SharedPreferences sp =
            getSharedPreferences("gel_prefs", MODE_PRIVATE);
    return sp.getBoolean("pulse_enabled", false);
}
    
private String batteryVerdict = "STABLE";
private String dataVerdict = "STABLE";
private String appsVerdict = "STABLE";

    private static final int STEP_INTRO    = 0;
    private static final int STEP_STORAGE  = 1;
    private static final int STEP_BATTERY  = 2;
    private static final int STEP_DATA     = 3;
    private static final int STEP_APPS     = 4;
    private static final int STEP_UNUSED = 5;
    private static final int STEP_CACHE    = 6;
    private static final int STEP_DNS = 7;
    private static final int STEP_QUEST    = 8;
    private static final int STEP_LABS     = 9;
    private static final int STEP_REMINDER = 10;
    private static final int STEP_MINI_REMINDER = 11;
    private static final int STEP_FINAL = 12;

    private final ArrayList<String> symptoms = new ArrayList<>();
    private boolean pulseEnabled = false;

private static final String PREFS = "gel_prefs";
private static final String KEY_PULSE_ENABLED = "pulse_enabled";
    

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // 🔥 IMMEDIATE TEST
    WorkManager.getInstance(this)
            .enqueue(
                    new OneTimeWorkRequest.Builder(OptimizerMiniScheduler.class)
                            .build()
            );
    // 🔥 ΤΕΛΟΣ IMMEDIATE TEST

    gr = AppLang.isGreek(this);
}
    // RESTORE CURRENT STEP (so we don't restart intro after returning / recreation)
    if (savedInstanceState != null) {
        step = savedInstanceState.getInt("gel_step", STEP_INTRO);
        returnedFromUsageScreen = savedInstanceState.getBoolean("gel_returned_usage", false);

        batteryVerdict = savedInstanceState.getString("gel_battery_verdict", "STABLE");
        dataVerdict    = savedInstanceState.getString("gel_data_verdict", "STABLE");
        appsVerdict    = savedInstanceState.getString("gel_apps_verdict", "STABLE");

        ArrayList<String> s = savedInstanceState.getStringArrayList("gel_symptoms");
        symptoms.clear();
        if (s != null) symptoms.addAll(s);

        // continue from where we left off
        go(step);
        return;
    }

    // FIRST LAUNCH ONLY
    go(STEP_INTRO);
}

@Override
protected void onSaveInstanceState(Bundle out) {
    super.onSaveInstanceState(out);

    out.putInt("gel_step", step);
    out.putBoolean("gel_returned_usage", returnedFromUsageScreen);

    out.putString("gel_battery_verdict", batteryVerdict);
    out.putString("gel_data_verdict", dataVerdict);
    out.putString("gel_apps_verdict", appsVerdict);

    out.putStringArrayList("gel_symptoms", new ArrayList<>(symptoms));
}

@Override
protected void onResume() {
    super.onResume();

    // If we returned from Usage Access screen, just clear the flag.
    // Do NOT restart flow here.
    if (returnedFromUsageScreen) {
        returnedFromUsageScreen = false;
    }

    // If we returned from Private DNS screen,
    // just clear the flag and stay on the same step.
    if (returnedFromDnsScreen) {
        returnedFromDnsScreen = false;
    }

    // Re-render current step (no auto-advance)
    go(step);
}

private void showDnsHowToDialog() {

    final boolean gr = AppLang.isGreek(this);

    LinearLayout root = buildBaseBox(
            progressTitle(gr
                    ? "ΒΗΜΑ 7 — Οδηγίες Private DNS"
                    : "STEP 7 — Private DNS Instructions")
    );

    TextView steps = new TextView(this);
    steps.setText(gr
            ? "Copy-paste έτοιμο:\n\n"
              + "Αντέγραψε το κείμενο που σου δίνω παρακάτω και πάτησε ρυθμισεις.\n"
              + "Εάν ανοίξουν οι γενικές ρυθμίσεις συσκευής,\n"
              + "ανάλογα με την συσκευή σου, ψάξε για\n\n"
              + "1) Συνδέσεις, ή Δίκτυο & Διαδίκτυο, ή Σύνδεση και Κοινοποίηση.\n\n"
              + "2) Περισσότερες ρυθμίσεις σύνδεσης, ή Προσωπικό/Ιδιωτικό DNS.\n\n"
              + "3) Όνομα παρόχου Προσωπικού/Ιδιωτικού DNS\n\n"
              + "4) Κάνε επικόλληση το κείμενο που αντέγραψες (dns.adguard.com)  → Αποθήκευση.\n\n"
              + "Όταν επιστρέψεις πάτησε ΟΚ/ΠΑΡΑΛΕΙΨΗ για να συνεχίσουμε .\n\n"
            : "Copy-paste ready:\n\n"
  + "Copy the text provided below and tap Settings.\n"
  + "If the general device settings screen opens,\n"
  + "depending on your device, look for:\n\n"
  + "1) Connections, or Network & Internet, or Connection & Sharing.\n\n"
  + "2) More connection settings, or Private DNS.\n\n"
  + "3) Private DNS provider hostname.\n\n"
  + "4) Paste the copied text (dns.adguard.com)  → Save.\n\n"
  + "When you return, press OK/SKIP to continue.\n\n"
    );
    steps.setTextColor(0xFF00FF7F);
    steps.setPadding(0, dp(14), 0, dp(18));
    root.addView(steps);

    // Hostname box (monospace look)
    TextView host = new TextView(this);
    host.setText("dns.adguard.com");
    host.setTextColor(Color.WHITE);
    host.setTextSize(18f);
    host.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
    host.setGravity(Gravity.CENTER);
    host.setPadding(dp(10), dp(12), dp(10), dp(12));

    GradientDrawable boxBg = new GradientDrawable();
    boxBg.setColor(0xFF111111);
    boxBg.setCornerRadius(dp(10));
    boxBg.setStroke(dp(3), 0xFFFFD700);
    host.setBackground(boxBg);

    root.addView(host);

    // COPY button
    Button copyBtn = mkGreenBtn(gr ? "ΑΝΤΙΓΡΑΦΗ" : "COPY");
    copyBtn.setOnClickListener(v -> {
        try {
            ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (cb != null) {
                cb.setPrimaryClip(ClipData.newPlainText("dns", "dns.adguard.com"));
                Toast.makeText(this,
                        gr ? "Αντιγράφηκε: dns.adguard.com" : "Copied: dns.adguard.com",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Throwable ignore) {}
    });
    root.addView(copyBtn);

    // OPEN SETTINGS button
    Button openBtn = mkGreenBtn(gr ? "ΑΝΟΙΓΜΑ ΡΥΘΜΙΣΕΩΝ" : "OPEN SETTINGS");
    openBtn.setOnClickListener(v -> {
        try {
            returnedFromDnsScreen = true;
            try {
    startActivity(new Intent("android.settings.PRIVATE_DNS_SETTINGS"));
} catch (Exception e) {
    startActivity(new Intent(Settings.ACTION_SETTINGS));
}
        } catch (Throwable t) {
            // αν αποτύχει, απλά προχώρα
            returnedFromDnsScreen = false;
            go(STEP_QUEST);
        }
    });
    root.addView(openBtn);

    // DONE button
    Button doneBtn = mkRedBtn(gr ? "ΕΤΟΙΜΟ" : "DONE");
    doneBtn.setOnClickListener(v -> go(STEP_QUEST));
    root.addView(doneBtn);

    showCustomDialog(root);
}

private void setPulseEnabled(boolean enabled) {
    pulseEnabled = enabled;
    getSharedPreferences(PREFS, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_PULSE_ENABLED, enabled)
            .apply();
}

private boolean isPulseEnabled() {
    return getSharedPreferences(PREFS, MODE_PRIVATE)
            .getBoolean(KEY_PULSE_ENABLED, false);
}

private void scheduleMiniPulse3xDaily() {
    try {
        androidx.work.Constraints c =
                new androidx.work.Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .build();

        androidx.work.PeriodicWorkRequest req =
                new androidx.work.PeriodicWorkRequest.Builder(
                        OptimizerMiniScheduler.class,
                        8, java.util.concurrent.TimeUnit.HOURS
                )
                .setConstraints(c)
                .addTag("gel_mini_pulse")
                .build();

        androidx.work.WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        "gel_mini_pulse",
                        androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
                        req
                );

    } catch (Throwable ignore) {}
}

private void cancelMiniPulse() {
    try {
        androidx.work.WorkManager.getInstance(this)
                .cancelUniqueWork("gel_mini_pulse");
    } catch (Throwable ignore) {}
}

// ============================================================
// ✅ SYSTEM APP FILTER (DROP SYSTEM APPS FROM GUIDED LISTS)
// Paste inside GuidedOptimizerActivity (anywhere in class scope)
// ============================================================
private boolean isSystemPkg(String pkg) {
    if (pkg == null) return true;
    try {
        ApplicationInfo ai = getPackageManager().getApplicationInfo(pkg, 0);
        return (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                || (ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
    } catch (Throwable t) {
        return true; // safest
    }
}

private int dp(int v) {
    return (int) android.util.TypedValue.applyDimension(
            android.util.TypedValue.COMPLEX_UNIT_DIP,
            v,
            getResources().getDisplayMetrics()
    );
}

private void addSection(
        LinearLayout root,
        String title,
        String body,
        int color) {

    TextView t = new TextView(this);
    t.setText(title);
    t.setTextColor(color);
    t.setTextSize(16f);
    t.setTypeface(null, android.graphics.Typeface.BOLD);
    t.setPadding(0, dp(12), 0, dp(6));
    root.addView(t);

    TextView b = new TextView(this);
    b.setText(body);
    b.setTextColor(android.graphics.Color.WHITE);
    b.setTextSize(14f);
    b.setPadding(0, 0, 0, dp(10));
    root.addView(b);
}

// ============================================================
// LIMIT + ADD (APPS UI HELPER)
// NOTE: Always return full code ready for copy-paste (no patch-only replies).
// ============================================================
private void limitAndAdd(LinearLayout root, ArrayList<AppRisk> list) {

    if (root == null || list == null || list.isEmpty()) return;

    final int LIMIT = 12;
    int shown = 0;

    PackageManager pm = getPackageManager();

    for (AppRisk r : list) {

        if (++shown > LIMIT) break;

        String label = r.packageName;

        try {
            ApplicationInfo ai = pm.getApplicationInfo(r.packageName, 0);
            CharSequence cs = pm.getApplicationLabel(ai);
            if (cs != null) label = cs.toString();
        } catch (Throwable ignore) {}

        TextView tv = new TextView(this);
        tv.setText("• " + label + "  (" + r.minutes + " min)");
        tv.setTextColor(0xFF00FF7F);
        tv.setPadding(0, dp(8), 0, dp(8));

        root.addView(tv);
    }

    if (list.size() > LIMIT) {
        TextView more = new TextView(this);
        more.setText(gr
                ? ("(+" + (list.size() - LIMIT) + " ακόμη)")
                : ("(+" + (list.size() - LIMIT) + " more)"));
        more.setTextColor(0xFFAAAAAA);
        more.setPadding(0, dp(8), 0, dp(6));
        more.setGravity(Gravity.CENTER);
        root.addView(more);
    }
}

    // ============================================================
    // SAFE SETTINGS OPEN
    // ============================================================

    private void safeStartActivity(String... actions) {
        for (String action : actions) {
            try {
                startActivity(new Intent(action));
                return;
            } catch (Throwable ignore) {}
        }
        try {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // ROUTER
    // ============================================================

    private void go(int s) {
    step = s;

    switch (step) {
        case STEP_INTRO: showIntro(); break;
        case STEP_STORAGE: showStorage(); break;
        case STEP_BATTERY: showBattery(); break;
        case STEP_DATA: showData(); break;
        case STEP_APPS: showApps(); break;
        case STEP_UNUSED: showInactiveApps(); break;
        case STEP_CACHE: showCache(); break;
        case STEP_DNS: showDnsStep(); break;   // 👈 ΑΥΤΟ ΕΛΕΙΠΕ
        case STEP_QUEST: showQuestionnaire(); break;
        case STEP_LABS: showLabRecommendation(); break;
        case STEP_REMINDER: showReminder(); break;
        case STEP_MINI_REMINDER: showMiniSchedulerPopup(); break;
        case STEP_FINAL: showFinalVerdict(); break;
    }
}

    // ============================================================
    // INTRO
    // ============================================================

    private void showIntro() {

        showDialog(
                gr ? "Έξυπνη Βελτιστοποίηση"
                        : "Smart Optimization",
                gr
                        ? "Θα σε πάω στις σωστές ρυθμίσεις της συσκευής.\n\n"
                        + "Ο στόχος είναι να κάνουμε τη συσκευή σου να λειτουργεί ομαλά και με ασφάλεια.\n\n"
                        + "Εσύ κάνεις τις επιλογές — εγώ κρατάω το τιμόνι (χωρίς να πατάω γκάζι μόνος μου 😄).\n\n"
                        + "Πάτα «Έναρξη» για να ξεκινήσουμε. \n\n"
                        : "I will guide you to the right system settings.\n\n"
                        + "The goal is to help your device run smoothly and securely.\n\n"
                        + "You make the choices — I simply steer (no autopilot 😄).\n\n"
                        + "Press “Start” to begin. \n\n",
                null,
                () -> go(STEP_STORAGE),
                true
        );
    }

    // ============================================================
    // STEP 1 — STORAGE
    // ============================================================

    private void showStorage() {
   
        showDialog(
                progressTitle(gr ? "ΒΗΜΑ 1 — Αποθήκευση" : "STEP 1 — Storage"),
                gr
                        ? "Θα ανοίξουν οι ρυθμίσεις αποθήκευσης της συσκευής.\n\n"
                        + "Χρησιμοποίησε τα διαθέσιμα εργαλεία καθαρισμού όπου χρειάζεται.\n"
                        + "Συνήθως αρκεί η εκκαθάριση προσωρινής μνήμης (cache), προσωρινών δεδομένων και κατάλοιπων αρχείων.\n"
                        + "Αυτές οι ενέργειες είναι ασφαλείς και δεν διαγράφουν προσωπικά δεδομένα.\n\n"
                        + "ΠΡΟΣΟΧΗ: Η εκκαθάριση δεδομένων εφαρμογής διαγράφει ρυθμίσεις, αποθηκευμένους λογαριασμούς και offline περιεχόμενο.\n"
                        + "Χρησιμοποίησέ την μόνο αν γνωρίζεις ακριβώς τι κάνεις.\n\n"
                        + "Σε ορισμένες συσκευές η εφαρμογή μπορεί να κλείσει προσωρινά.\n\n"
                        + "Μετά τον καθαρισμό, άνοιξε ξανά την εφαρμογή\n"
                        + "και πάτησε OK/ΠΑΡΑΛΕΙΨΗ για να συνεχίσουμε.\n\n"
                        : "The device storage settings will open.\n\n"
                        + "Use the available cleaning tools where necessary.\n"
                        + "In most cases, clearing temporary cache, temporary data and residual files is sufficient.\n"
                        + "These actions are safe and do not remove personal data.\n\n"
                        + "WARNING: Clearing app data removes settings, saved accounts and offline content.\n"
                        + "Use it only if you fully understand the consequences.\n\n"
                        + "On some devices the app may close temporarily.\n\n"
                        + "After cleaning, reopen the app\n"
                        + "and press OK/SKIP to continue.\n\n",
                () -> {

// --------------------------------------------------------
// 1️⃣ GLOBAL STORAGE (PRIMARY)
// --------------------------------------------------------
try {
    Intent storage = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
    storage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(storage);
    return;
} catch (Throwable ignore) {}

// --------------------------------------------------------
// 2️⃣ DEVICE STORAGE (SECONDARY) — extra Android safety net
// --------------------------------------------------------
try {
    Intent deviceStorage = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
    deviceStorage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(deviceStorage);
    return;
} catch (Throwable ignore) {}

// --------------------------------------------------------
// 3️⃣ OEM CLEANER (FALLBACK)
// --------------------------------------------------------
try {
    boolean launched = CleanLauncher.openDeepCleaner(this);
    if (launched) return;
} catch (Throwable ignore) {}

// --------------------------------------------------------
// 4️⃣ LAST RESORT
// --------------------------------------------------------
Toast.makeText(
        this,
        gr ? "Δεν βρέθηκε καθαριστής στη συσκευή."
           : "No compatible cleaner found.",
        Toast.LENGTH_SHORT
).show();

            },
            () -> go(STEP_BATTERY),
            false
    );
}

// ============================================================
// STEP 2 — BATTERY INTELLIGENCE ENGINE (MODERATE + HEAVY ONLY)
// ============================================================

private void showBattery() {

    if (!hasUsageAccess()) {

        batteryVerdict = "STABLE";

        showDialog(
        progressTitle(
                gr
                        ? "ΒΗΜΑ 2 — Κατανάλωση Μπαταρίας (48 ώρες)"
                        : "STEP 2 — Battery Consumption (48 hours)"
        ),
                gr
                        ? "Για να αναλύσουμε τη δραστηριότητα εφαρμογών,\n"
                        + "απαιτείται πρόσβαση Χρήσης Εφαρμογών.\n\n"
                        + "Καμία συλλογή προσωπικών δεδομένων δεν γίνεται με την παραχώρηση της Πρόσβασης Χρήσης.\n\n"
                        + "Πάτησε Ρυθμίσεις και ενεργοποίησε την άδεια για το GEL.\n\n"
                        + "Όταν επιστρέψεις, πάτησε ΟΚ/ΠΑΡΑΛΕΙΨΗ για να συνεχίσουμε.\n\n"
                        : "To analyze app activity,\n"
                        + "Usage Access permission is required.\n\n"
                        + "Press Settings and enable it for GEL.\n\n"
                         + "When you return, press OK/SKIP to continue.\n\n",
                () -> {
    try {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        returnedFromUsageScreen = true;
        startActivity(intent);
    } catch (Throwable e) {
        returnedFromUsageScreen = true;
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }
},
                () -> go(STEP_BATTERY),
                false
        );
        return;
    }

    long now = System.currentTimeMillis();
    long start = now - (48L * 60 * 60 * 1000); // 48 hours window

    UsageStatsManager usm =
        (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);

List<UsageStats> stats =
        usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                start,
                now
        );

if (stats == null || stats.isEmpty()) {
    batteryVerdict = "STABLE";
    showStableDialog();
    return;
}

// ============================================================
// ✅ BATTERY STEP — MERGE + FILTER (NO SYSTEM APPS)
// Replace your whole "MERGE FG+BG ... suspiciousApps ... heavyApps/moderateApps" block with this
// ============================================================

// 🔽 MERGE FG + BG
HashMap<String, Long> mergedFgMinutes = new HashMap<>();
HashMap<String, Long> mergedBgMinutes = new HashMap<>();

for (UsageStats u : stats) {

    if (u == null) continue;

    String pkg = u.getPackageName();
    if (pkg == null) continue;
    if (pkg.equals(getPackageName())) continue;

    // ✅ DROP SYSTEM APPS
    if (isSystemPkg(pkg)) continue;

    long fg = 0L;
    try { fg = u.getTotalTimeInForeground() / 60000L; } catch (Throwable ignore) {}

    long bg = 0L;
    try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            bg = u.getTotalTimeForegroundServiceUsed() / 60000L;
        }
    } catch (Throwable ignore) {}

    Long curFg = mergedFgMinutes.get(pkg);
    mergedFgMinutes.put(pkg, (curFg == null ? 0L : curFg) + fg);

    Long curBg = mergedBgMinutes.get(pkg);
    mergedBgMinutes.put(pkg, (curBg == null ? 0L : curBg) + bg);
}

// ✅ Only 3rd-party suspicious apps
ArrayList<AppRisk> suspiciousApps = new ArrayList<>();

for (String pkg : mergedBgMinutes.keySet()) {

    if (pkg == null) continue;
    if (pkg.equals(getPackageName())) continue;

    // ✅ DROP SYSTEM APPS (double safety)
    if (isSystemPkg(pkg)) continue;

    long fgMinutes = mergedFgMinutes.get(pkg) != null ? mergedFgMinutes.get(pkg) : 0L;
    long bgMinutes = mergedBgMinutes.get(pkg) != null ? mergedBgMinutes.get(pkg) : 0L;

    // ✅ RULE: only background without opening
    boolean userOpened = fgMinutes > 0;
    boolean bgNoOpen = (!userOpened && bgMinutes > 0);
    if (!bgNoOpen) continue;

    suspiciousApps.add(new AppRisk(pkg, bgMinutes, false));
}

PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

ArrayList<AppRisk> heavyApps = new ArrayList<>();
ArrayList<AppRisk> moderateApps = new ArrayList<>();

for (String pkg : mergedBgMinutes.keySet()) {

    if (pkg == null) continue;
    if (pkg.equals(getPackageName())) continue;

    // ✅ DROP SYSTEM APPS
    if (isSystemPkg(pkg)) continue;

    long fgMinutes = mergedFgMinutes.get(pkg) != null ? mergedFgMinutes.get(pkg) : 0L;
    long bgMinutes = mergedBgMinutes.get(pkg) != null ? mergedBgMinutes.get(pkg) : 0L;

    // ✅ RULE: only background without opening
    boolean userOpened = fgMinutes > 0;
    boolean bgNoOpen = (!userOpened && bgMinutes > 0);
    if (!bgNoOpen) continue;

    boolean unrestricted = false;
    try { unrestricted = pm != null && pm.isIgnoringBatteryOptimizations(pkg); } catch (Throwable ignore) {}

    int score;
    if (bgMinutes >= 120) score = 3;          // HEAVY
    else if (bgMinutes >= 45) score = 2;      // MODERATE
    else score = 1;                           // LOW

    if (unrestricted && score >= 2) score++;  // elevate if unrestricted

    if (score >= 3) heavyApps.add(new AppRisk(pkg, bgMinutes, unrestricted));
    else if (score == 2) moderateApps.add(new AppRisk(pkg, bgMinutes, unrestricted));
}

// ✅ STABLE
if (heavyApps.isEmpty() && moderateApps.isEmpty()) {
    batteryVerdict = "STABLE";
    showStableDialog();
    return;
}

    ScrollView scroll = new ScrollView(this);

    LinearLayout root = buildBaseBox(
        progressTitle(
                gr
                        ? "ΒΗΜΑ 2 — Κατανάλωση Μπαταρίας (48 ώρες)"
                        : "STEP 2 — Battery Consumption (48 hours)"
        )
);

scroll.addView(root);

    boolean suspiciousBattery = false;
boolean legitHeavyUse = false;

for (AppRisk r : heavyApps) {

    if (r.minutes >= 120 && r.unrestricted) {
        suspiciousBattery = true;
        break;
    }

    if (r.minutes >= 120) {
        legitHeavyUse = true;
    }
}

String verdict;

if (suspiciousBattery) {
    verdict = "HEAVY";
}
else if (legitHeavyUse || !moderateApps.isEmpty()) {
    verdict = "MODERATE";
}
else {
    verdict = "STABLE";
}

batteryVerdict = verdict;

    addEngineVerdict(root, verdict,
            heavyApps.size(),
            moderateApps.size());

    addRecommendations(root, verdict);

    if (!heavyApps.isEmpty() || !moderateApps.isEmpty()) {

    addSection(
            root,
            gr ? "⚠️ Background Δραστηριότητα"
               : "⚠️ Background Activity",
            gr ? "Εφαρμογές που έτρεξαν χωρίς να τις ανοίξεις τις τελευταίες 48 ώρες."
               : "Apps that ran without being opened in the last 48h.",
            0xFFFFC107
    );

    ArrayList<AppRisk> combined = new ArrayList<>();
    combined.addAll(heavyApps);
    combined.addAll(moderateApps);

    addBatteryAppList(root, combined);
}

    Button next = mkGreenBtn("OK");
next.setOnClickListener(v -> go(STEP_DATA));
root.addView(next);

showCustomDialog(scroll);
}

// ============================================================
// STABLE STATE
// ============================================================

private void showStableDialog() {

    showDialog(
            progressTitle(
                    gr
                            ? "ΒΗΜΑ 2 — Κατανάλωση Μπαταρίας (48 ώρες)"
                            : "STEP 2 — Battery Consumption (48 hours)"
            ),
            gr
                    ? "Τα αποτελέσματα αφορούν εφαρμογές που δεν άνοιξες τις τελευταίες 48 ώρες,\n"
                      + "αλλά παρουσίασαν δραστηριότητα στο παρασκήνιο.\n\n"
                      + "Engine Verdict: STABLE\n\n"
                      + "Δεν εντοπίστηκε δραστηριότητα εφαρμογών στο παρασκήνιο, τις τελευταίες 48 ώρες.\n\n"
                    : "Results refer to apps you did not open in the last 48 hours,\n"
                      + "but showed background activity.\n\n"
                      + "Engine Verdict: STABLE\n\n"
                      + "No background app activity detected in the last 48 hours.\n\n",
            null,
            () -> go(STEP_DATA),
            false
    );
}

private void showFinalVerdict() {

    final boolean gr = AppLang.isGreek(this);

    LinearLayout root = buildBaseBox(
            gr ? "Τελική Αναφορά Συσκευής"
               : "Final Device Report"
    );

    String finalVerdict = resolveFinalVerdict();

    String displayText;

    switch (finalVerdict) {
        case "HEAVY":
            displayText = gr
                    ? "🔴 Εντοπίστηκε Υψηλή Δραστηριότητα στο Παρασκήνιο."
                    : "🔴 High Background Activity Detected.";
            break;

        case "MODERATE":
            displayText = gr
                    ? "🟡 Εντοπίστηκε Δραστηριότητα στο Παρασκήνιο."
                    : "🟡 Background Activity Detected.";
            break;

        default:
            displayText = gr
                    ? "🟢 Δεν Εντοπίστηκε Δραστηριότητα στο Παρασκήνιο."
                    : "🟢 No Background Activity Detected.";
            break;
    }

    // ----------------------------
    // Section Details
    // ----------------------------

    addFinalRow(root,
            gr ? "Μπαταρία" : "Battery",
            batteryVerdict,
            gr);

    addFinalRow(root,
            gr ? "Δεδομένα" : "Data",
            dataVerdict,
            gr);

    addFinalRow(root,
            gr ? "Εφαρμογές" : "Apps",
            appsVerdict,
            gr);

    // ----------------------------
    // Divider
    // ----------------------------

    View div = new View(this);
    div.setBackgroundColor(0xFF333333);
    LinearLayout.LayoutParams dlp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(1));
    dlp.setMargins(0, dp(20), 0, dp(20));
    div.setLayoutParams(dlp);
    root.addView(div);

    // ----------------------------
    // Overall Status
    // ----------------------------

    TextView statusTv = new TextView(this);

    int color =
            finalVerdict.equals("HEAVY") ? 0xFFFF5252 :
            finalVerdict.equals("MODERATE") ? 0xFFFFC107 :
            0xFF00C853;

    statusTv.setText(displayText);
    statusTv.setTextColor(color);
    statusTv.setTextSize(18f);
    statusTv.setTypeface(null, Typeface.BOLD);
    statusTv.setPadding(0, dp(10), 0, dp(20));

    root.addView(statusTv);

    // Stable explanation
    if ("STABLE".equals(finalVerdict)) {

        TextView cleanMsg = new TextView(this);
        cleanMsg.setText(
                gr
                        ? "Δεν εντοπίστηκε δραστηριότητα στο παρασκήνιο τις τελευταίες 48 ώρες."
                        : "No background activity detected in the last 48 hours."
        );
        cleanMsg.setTextColor(0xFFAAAAAA);
        cleanMsg.setPadding(0, dp(6), 0, dp(18));

        root.addView(cleanMsg);
    }

    // ----------------------------
    // Scheduler Status
    // ----------------------------

    TextView schedTv = new TextView(this);
    schedTv.setText(
            gr
                    ? "Scheduler: " + (isSchedulerEnabled() ? "ΕΝΕΡΓΟΣ" : "ΑΝΕΝΕΡΓΟΣ")
                    : "Scheduler: " + (isSchedulerEnabled() ? "ENABLED" : "DISABLED")
    );
    schedTv.setTextColor(0xFFAAAAAA);
    schedTv.setPadding(0, dp(4), 0, dp(4));
    root.addView(schedTv);

    TextView miniTv = new TextView(this);
    miniTv.setText(
            gr
                    ? "Mini Scheduler: " + (isPulseEnabled() ? "ΕΝΕΡΓΟΣ" : "ΑΝΕΝΕΡΓΟΣ")
                    : "Mini Scheduler: " + (isPulseEnabled() ? "ENABLED" : "DISABLED")
    );
    miniTv.setTextColor(0xFFAAAAAA);
    miniTv.setPadding(0, dp(4), 0, dp(20));
    root.addView(miniTv);

    // ----------------------------
    // Done Button
    // ----------------------------

    Button done = mkGreenBtn(gr ? "ΟΚ" : "OK");
    done.setOnClickListener(v -> finish());
    root.addView(done);

    showCustomDialog(root);
}


private String resolveFinalVerdict() {

    int heavyCount = 0;
    int moderateCount = 0;

    if ("HEAVY".equals(batteryVerdict)) heavyCount++;
    if ("HEAVY".equals(dataVerdict)) heavyCount++;
    if ("HEAVY".equals(appsVerdict)) heavyCount++;

    if ("MODERATE".equals(batteryVerdict)) moderateCount++;
    if ("MODERATE".equals(dataVerdict)) moderateCount++;
    if ("MODERATE".equals(appsVerdict)) moderateCount++;

    if (heavyCount >= 2) {
        return "HEAVY";
    }

    if (heavyCount == 1 || moderateCount >= 1) {
        return "MODERATE";
    }

    return "STABLE";
}


private void addFinalRow(LinearLayout root,
                         String label,
                         String verdict,
                         boolean gr) {

    TextView tv = new TextView(this);

    String verdictText;

    if ("HEAVY".equals(verdict)) {
        verdictText = gr ? "Υψηλή" : "High";
    } else if ("MODERATE".equals(verdict)) {
        verdictText = gr ? "Μέτρια" : "Moderate";
    } else {
        verdictText = gr ? "Σταθερή" : "Stable";
    }

    int color =
            "HEAVY".equals(verdict) ? 0xFFFF5252 :
            "MODERATE".equals(verdict) ? 0xFFFFC107 :
            0xFF00C853;

    tv.setText(label + ": " + verdictText);
    tv.setTextColor(color);
    tv.setTextSize(16f);
    tv.setPadding(0, dp(6), 0, dp(6));

    root.addView(tv);
}

// ============================================================
// SUPPORTING STRUCTURES
// ============================================================

private static class AppRisk {
    String packageName;
    long minutes;
    boolean unrestricted;

    AppRisk(String p, long m, boolean u) {
        packageName = p;
        minutes = m;
        unrestricted = u;
    }
}

private boolean hasUsageAccess() {

    UsageStatsManager usm =
            (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);

    long now = System.currentTimeMillis();

    List<UsageStats> stats =
            usm.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    now - 1000 * 60,
                    now
            );

    return stats != null && !stats.isEmpty();
}

private void addEngineVerdict(LinearLayout root,
                              String verdict,
                              int heavyCount,
                              int moderateCount) {

    TextView tv = new TextView(this);

    int color =
            verdict.equals("HEAVY") ? 0xFFFF5252 :
            0xFFFFC107;

    tv.setText(
        "Engine Verdict: " + verdict + "\n\n"
        + (gr ? "Υψηλή Δραστηριότητα στο παρασκήνιο: \n\n"
              : "High Background Activity: \n\n")
        + heavyCount + "\n"
        + (gr ? "Μέτρια Δραστηριότητα στο παρασκήνιο: \n\n"
              : "Moderate Background Activity: \n\n")
        + moderateCount
);

    tv.setTextColor(color);
    tv.setTextSize(15f);
    tv.setPadding(0,10,0,30);

    root.addView(tv);
}

private void addRecommendations(LinearLayout root,
                                String verdict) {

    TextView tv = new TextView(this);

    String rec;

    if (verdict.equals("HEAVY")) {
        rec = gr
                ? "Προτείνεται περιορισμός δραστηριότητας παρασκηνίου, ή απεγκατάσταση μη απαραίτητων εφαρμογών. \n\n"
                : "It is recommended to restrict background activity, or uninstall unnecessary apps. \n\n";
    } else {
        rec = gr
                ? "Έλεγξε τις παρακάτω εφαρμογές. \n\n"
                : "Review listed apps. \n\n";
    }

    tv.setText(rec);
    tv.setTextColor(0xFFAAAAAA);
    tv.setPadding(0,0,0,30);

    root.addView(tv);
}

// ============================================================
// STEP 3 — DATA INTELLIGENCE ENGINE (MODERATE + HEAVY ONLY)
// ============================================================

private void showData() {

    // ✅ Needs Usage Access (for "rarely used but active" signal)
    if (!hasUsageAccess()) {
        dataVerdict = "STABLE";
        showDialog(
        progressTitle(
                gr
                        ? "ΒΗΜΑ 3 — Κατανάλωση Δεδομένων (48 ώρες)"
                        : "STEP 3 — Data Consumption (48 hours)"
        ),
        gr
                ? "Για να κάνουμε premium ανάλυση δεδομένων,\n"
                  + "χρειαζόμαστε πρόσβαση Χρήσης Εφαρμογών.\n\n"
                  + "Καμία συλλογή προσωπικών δεδομένων δεν γίνεται με την παραχώρηση της Πρόσβασης Χρήσης.\n\n"
                  + "Πάτησε Ρυθμίσεις και ενεργοποίησε την άδεια για το GEL. \n\n"
                  + "Όταν επιστρέψεις πάτησε ΕΝΑΡΞΗ για να ξεκινήσουμε. \n\n"
                : "To run premium data analysis,\n"
                  + "Usage Access permission is required.\n\n"
                  + "Press Settings and enable it for GEL. \n\n"
                  + "Whene you return press START to continue.\n\n",
        () -> startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)),
        () -> go(STEP_DATA),
        false
);
        return;
    }

    // ⏱ Window: 48 hours
    final long now = System.currentTimeMillis();
    final long start = now - (48L * 60 * 60 * 1000);

    final ArrayList<DataRisk> heavy = new ArrayList<>();
    final ArrayList<DataRisk> moderate = new ArrayList<>();

    try {

    UsageStatsManager usm =
            (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);

    List<UsageStats> stats =
            usm != null
                    ? usm.queryUsageStats(
                            UsageStatsManager.INTERVAL_DAILY,
                            start,
                            now
                    )
                    : null;

    if (stats == null || stats.isEmpty()) {
        dataVerdict = "STABLE";
        showDialog(
        progressTitle(
                gr
                        ? "ΒΗΜΑ 3 — Κατανάλωση Δεδομένων (48 ώρες)"
                        : "STEP 3 — Data Consumption (48 hours)"
        ),
        gr
                ? "Τα αποτελέσματα αφορούν εφαρμογές που δεν άνοιξες τις τελευταίες 48 ώρες,\n"
                  + "αλλά παρουσίασαν δραστηριότητα στο παρασκήνιο.\n\n"
                  + "Engine Verdict: STABLE\n\n"
                  + "Δεν υπάρχουν διαθέσιμα στοιχεία χρήσης για τις τελευταίες 48 ώρες.\n\n"
                : "Results refer to apps you did not open in the last 48 hours,\n"
                  + "but showed background activity.\n\n"
                  + "Engine Verdict: STABLE\n\n"
                  + "No usage stats available in the last 48 hours.\n\n",
        null,
        () -> go(STEP_APPS),
        false
);
return;
}

// 🔽 MERGE 48h DAILY BUCKETS
HashMap<String, Long> mergedFgMinutes = new HashMap<>();
HashMap<String, Long> mergedBgMinutes = new HashMap<>();
HashMap<String, Long> mergedLastUsed  = new HashMap<>();

for (UsageStats u : stats) {

    if (u == null) continue;

    String pkg = u.getPackageName();
    if (pkg == null) continue;

    long fg = 0L;
    try {
        fg = u.getTotalTimeInForeground() / 60000L;
    } catch (Throwable ignore) {}

    long bg = 0L;
    try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            bg = u.getTotalTimeForegroundServiceUsed() / 60000L;
        }
    } catch (Throwable ignore) {}

    long last = 0L;
    try {
        last = u.getLastTimeUsed();
    } catch (Throwable ignore) {}

    Long curFg = mergedFgMinutes.get(pkg);
    mergedFgMinutes.put(pkg, (curFg == null ? 0L : curFg) + fg);

    Long curBg = mergedBgMinutes.get(pkg);
    mergedBgMinutes.put(pkg, (curBg == null ? 0L : curBg) + bg);

    Long lastCur = mergedLastUsed.get(pkg);
    if (lastCur == null || last > lastCur) {
        mergedLastUsed.put(pkg, last);
    }
}

PackageManager pm = getPackageManager();

try {

    for (String pkg : mergedBgMinutes.keySet()) {

        if (pkg == null) continue;
        if (pkg.equals(getPackageName())) continue;

        long fgMinutes =
                mergedFgMinutes.get(pkg) != null
                        ? mergedFgMinutes.get(pkg)
                        : 0L;

        long bgMinutes =
                mergedBgMinutes.get(pkg) != null
                        ? mergedBgMinutes.get(pkg)
                        : 0L;

        // ✅ ΚΑΝΟΝΑΣ: ΜΟΝΟ background χωρίς άνοιγμα
        boolean userOpened = fgMinutes > 0;
        boolean bgNoOpen   = (!userOpened && bgMinutes > 0);
        if (!bgNoOpen) continue;

        Long lastObj = mergedLastUsed.get(pkg);
        long lastUsed = lastObj != null ? lastObj : 0L;

        long hoursSinceUse =
                lastUsed > 0
                        ? (now - lastUsed) / (1000L * 60 * 60)
                        : 999999;

        try {
            ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
            boolean isSystem =
                    (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            if (isSystem) continue;
        } catch (Throwable ignore) {}

        boolean rarelyUsedButActive =
                (bgMinutes <= 5 && hoursSinceUse <= 12);

        long score =
                (bgMinutes * 2)
                        + (rarelyUsedButActive ? 30 : 0);

        if (score >= 240) {
            heavy.add(new DataRisk(
                    pkg,
                    score,
                    bgMinutes,
                    hoursSinceUse,
                    rarelyUsedButActive
            ));
        }
        else if (score >= 80) {
            moderate.add(new DataRisk(
                    pkg,
                    score,
                    bgMinutes,
                    hoursSinceUse,
                    rarelyUsedButActive
            ));
        }
    }

} catch (Throwable ignore) {}

    if (heavy.isEmpty() && moderate.isEmpty()) {
        dataVerdict = "STABLE";
        showDialog(
        progressTitle(
                gr
                        ? "ΒΗΜΑ 3 — Κατανάλωση Δεδομένων (48 ώρες)"
                        : "STEP 3 — Data Consumption (48 hours)"
        ),
        gr
                ? "Τα αποτελέσματα αφορούν εφαρμογές που δεν άνοιξες τις τελευταίες 48 ώρες,\n"
                  + "αλλά παρουσίασαν δραστηριότητα στο παρασκήνιο.\n\n"
                  + "Engine Verdict: STABLE\n\n"
                  + "Δεν εντοπίστηκε ύποπτη ή βαριά δραστηριότητα χρήσης τις τελευταίες 48 ώρες.\n\n"
                : "Results refer to apps you did not open in the last 48 hours,\n"
                  + "but showed background activity.\n\n"
                  + "Engine Verdict: STABLE\n\n"
                  + "No suspicious or heavy usage activity detected in the last 48 hours.\n\n",
        null,
        () -> go(STEP_APPS),
        false
);
return;
}

    // Sort by SCORE desc (stable + simple)
    java.util.Comparator<DataRisk> cmp =
            (a, b) -> Long.compare(b.score, a.score);

    java.util.Collections.sort(heavy, cmp);
    java.util.Collections.sort(moderate, cmp);

    // UI
    ScrollView scroll = new ScrollView(this);
    LinearLayout root = buildBaseBox(
        progressTitle(
            gr ? "ΒΗΜΑ 3 — Κατανάλωση Δεδομένων (48 ώρες)"
   : "STEP 3 — Data Consumption (48 hours)"
        )
);
    scroll.addView(root);

    boolean suspiciousData = false;

for (DataRisk r : heavy) {

    if (r.fgMinutes <= 5 && r.hoursSinceUse <= 12) {
        suspiciousData = true;
        break;
    }
}

String verdict;

if (suspiciousData) {
    verdict = "HEAVY";
}
else if (!heavy.isEmpty() || !moderate.isEmpty()) {
    verdict = "MODERATE";
}
else {
    verdict = "STABLE";
}

dataVerdict = verdict;

    addEngineVerdictData(root, verdict, heavy.size(), moderate.size());
    
    TextView sectionTitle = new TextView(this);
sectionTitle.setText(
        gr ? "Τι σημαίνουν τα αποτελέσματα"
           : "What the results mean"
);
sectionTitle.setTextColor(0xFFFFD700); // GEL gold
sectionTitle.setTypeface(null, Typeface.BOLD);
sectionTitle.setTextSize(16f);
sectionTitle.setPadding(0, dp(12), 0, dp(10));

root.addView(sectionTitle);

    TextView explain = new TextView(this);

String fullText = gr
        ? "Η ανάλυση βασίζεται σε δραστηριότητα στο παρασκήνιο (όχι MB).\n\n"
        + "• High Activity = αυξημένη δραστηριότητα στο παρασκήνιο.\n\n"
        + "• 💤 Σπάνια χρήση αλλά ενεργή = δεν άνοιξες την εφαρμογή,\n"
        + "   αλλά παρουσίασε πρόσφατη δραστηριότητα στο παρασκήνιο.\n\n"
        + "Πάτα σε μια εφαρμογή για ενέργειες.\n\n"
        : "This analysis is based on background activity (not MB).\n\n"
        + "• High Activity = elevated background activity.\n\n"
        + "• 💤 Rarely used but active = you did not open the app,\n"
        + "   but it showed recent background activity\n\n"
        + "Tap an app for actions.\n\n";

android.text.SpannableStringBuilder sb =
        new android.text.SpannableStringBuilder(fullText);

// Highlight labels
String highLabel = gr
        ? "Υψηλή Δραστηριότητα"
        : "High Activity";
String rareLabel = gr
        ? "💤 Σπάνια χρήση αλλά ενεργή"
        : "💤 Rarely used but active";

int highStart = fullText.indexOf(highLabel);
int rareStart = fullText.indexOf(rareLabel);

if (highStart >= 0) {
    int highEnd = highStart + highLabel.length();
    sb.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            highStart, highEnd, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    sb.setSpan(new android.text.style.ForegroundColorSpan(0xFFFF5252),
            highStart, highEnd, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
}

if (rareStart >= 0) {
    int rareEnd = rareStart + rareLabel.length();
    sb.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            rareStart, rareEnd, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    sb.setSpan(new android.text.style.ForegroundColorSpan(0xFFFFC107),
            rareStart, rareEnd, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
}

explain.setText(sb);
explain.setTextColor(0xFFAAAAAA);
explain.setPadding(0, 0, 0, 28);

root.addView(explain);

    if (!heavy.isEmpty()) {
        addSection(
                root,
                gr ? "🔥 Υψηλή Δραστηριότητα" : "🔥 High Activity",
                gr ? "Εφαρμογές με πολύ υψηλή δραστηριότητα." : "Apps with very high activity.",
                0xFFFF5252
        );
        addDataRows(root, heavy);
    }

    if (!moderate.isEmpty()) {
        addSection(
                root,
                gr ? "⚠️ Εφαρμογές με Μέτρια Δραστηριότητα" : "⚠️ Moderate Activity apps",
                gr ? "Εφαρμογές που αξίζουν έλεγχο." : "Apps worth reviewing.",
                0xFFFFC107
        );
        addDataRows(root, moderate);
    }

    Button okBtn = mkGreenBtn("OK");
    okBtn.setOnClickListener(v -> go(STEP_APPS));
    root.addView(okBtn);

    showCustomDialog(scroll);

} catch (Throwable ignore) {
}

}

// ============================================================
// DATA RISK MODEL (NO BYTES, SCORE ONLY)
// ============================================================
private static class DataRisk {
    final String pkg;
    final long score;          // behavioural index
    final long fgMinutes;      // foreground minutes in 48h
    final long hoursSinceUse;  // hours in the last used
    final boolean rarelyUsedButActive;

    DataRisk(String p, long s, long fg, long h, boolean r) {
        pkg = p;
        score = s;
        fgMinutes = fg;
        hoursSinceUse = h;
        rarelyUsedButActive = r;
    }
}

// ============================================================
// UI: ENGINE VERDICT
// ============================================================
private void addEngineVerdictData(LinearLayout root,
                                  String verdict,
                                  int heavyCount,
                                  int moderateCount) {

    TextView tv = new TextView(this);

    int color =
            verdict.equals("HEAVY") ? 0xFFFF5252 :
            0xFFFFC107;

    tv.setText(
        "Engine Verdict: " + verdict + "\n\n"
        + (gr ? "Υψηλή Δραστηριότητα:\n\n"
              : "High Activity:\n\n")
        + heavyCount + "\n\n"
        + (gr ? "Μέτρια Δραστηριότητα:\n\n"
              : "Moderate Activity:\n\n")
        + moderateCount + "\n\n"
);

    tv.setTextColor(color);
    tv.setTextSize(15f);
    tv.setPadding(0, 10, 0, 22);

    root.addView(tv);

    TextView rec = new TextView(this);

rec.setText(
        gr
                ? "Μπορείς να περιορίσεις τη δραστηριότητα στο παρασκήνιο αυτών των εφαρμογών, ή να αφαιρέσεις όσες δεν χρειάζεσαι.\n\n"
                : "You can restrict background activity for these apps, or remove those you don’t need.\n\n"
);

rec.setTextColor(0xFFFFFFFF);
rec.setPadding(0, 0, 0, 26);

root.addView(rec);
}

// ============================================================
// UI: ROWS
// ============================================================
private void addDataRows(LinearLayout root, java.util.List<DataRisk> list) {

    final PackageManager pm = getPackageManager();

    int shown = 0;
    for (DataRisk r : list) {

        if (++shown > 12) break;

        String label = r.pkg;
        try {
            ApplicationInfo ai = pm.getApplicationInfo(r.pkg, 0);
            CharSequence cs = pm.getApplicationLabel(ai);
            if (cs != null) label = cs.toString();
        } catch (Throwable ignore) {}

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, 14, 0, 14);

        TextView name = new TextView(this);
        name.setText("• " + label);
        name.setTextColor(Color.WHITE);
        name.setTypeface(null, Typeface.BOLD);

        TextView meta = new TextView(this);

        String tag = r.rarelyUsedButActive
                ? (gr ? "💤 Σπάνια χρήση αλλά ενεργή \n\n" : "💤 Rarely used but active \n\n")
                : (gr ? "Υψηλή Δραστηριότητα" : "High Activity \n\n");

        meta.setText(
                (gr ? "Δείκτης: " : "Index: ") + r.score
                        + "  |  "
                        + (gr ? "Χρήση: " : "Use: ") + r.fgMinutes + (gr ? " λεπτά (48h)" : " min (48h)")
                        + "\n"
                        + (gr ? "Τελευταία χρήση: " : "Last used: ") + r.hoursSinceUse + (gr ? " ώρες πριν" : "h ago")
                        + "\n"
                        + tag
        );

        meta.setTextColor(0xFF00FF7F);
        meta.setPadding(0, 8, 0, 10);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER);

        Button details = mkBlackGoldBtn(gr ? "Λεπτομέρειες" : "Details");
        Button uninstall = mkRedBtn(gr ? "Απεγκατάσταση" : "Uninstall");

        details.setOnClickListener(v -> openAppDetails(r.pkg));
        uninstall.setOnClickListener(v -> uninstallPkg(r.pkg));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(dp(6), 0, dp(6), 0);        
        
        details.setLayoutParams(lp);
        uninstall.setLayoutParams(lp);

        btnRow.addView(details);
        btnRow.addView(uninstall);

        row.addView(name);
        row.addView(meta);
        row.addView(btnRow);

        View div = new View(this);
        div.setBackgroundColor(0xFF222222);
        LinearLayout.LayoutParams dlp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        dlp.setMargins(0, dp(14), 0, 0);
        div.setLayoutParams(dlp);

        row.addView(div);

        root.addView(row);
    }
}

// ============================================================
// ACTIONS
// ============================================================

private void openAppDetails(String pkg) {

    // 1️⃣ Main App Info (always works)
    try {
        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.setData(android.net.Uri.fromParts("package", pkg, null));
        startActivity(i);
        return;
    } catch (Throwable ignore) {}

    // 2️⃣ Fallback
    try {
        startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
    } catch (Throwable ignore2) {}
}

private void uninstallPkg(String pkg) {
    try {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", pkg, null));
        startActivity(intent);
    } catch (Throwable ignore) {
    }
}

private void openBatterySettings(String pkg) {

    // 1️⃣ Try direct app battery screen (OEM dependent)
    try {
        Intent i = new Intent("android.settings.APP_BATTERY_SETTINGS");
        i.putExtra("package_name", pkg);
        startActivity(i);
        return;
    } catch (Throwable ignore) {}

    // 2️⃣ Fallback → general battery settings
    try {
        startActivity(new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS));
        return;
    } catch (Throwable ignore2) {}

    // 3️⃣ Last fallback → app info
    openAppDetails(pkg);
}

// ============================================================
// STEP 4 — APPS INTELLIGENCE ENGINE (MODERATE + HEAVY ONLY)
// ============================================================

private void showApps() {

    long now = System.currentTimeMillis();
    long start = now - (48L * 60 * 60 * 1000);

    ArrayList<AppAppRisk> heavy = new ArrayList<>();
    ArrayList<AppAppRisk> moderate = new ArrayList<>();

    try {

        UsageStatsManager usm =
                (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);

        List<UsageStats> stats =
        usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                start,
                now
        );

HashMap<String, Long> mergedFgMinutes = new HashMap<>();
HashMap<String, Long> mergedBgMinutes = new HashMap<>();
HashMap<String, Long> mergedLastUsed  = new HashMap<>();

if (stats != null) {
    for (UsageStats u : stats) {

        if (u == null) continue;

        String pkg = u.getPackageName();
        if (pkg == null) continue;

        long fgMins = 0L;
try {
    fgMins = u.getTotalTimeInForeground() / 60000L;
} catch (Throwable ignore) {}

long bgMins = 0L;
try {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        bgMins = u.getTotalTimeForegroundServiceUsed() / 60000L;
    }
} catch (Throwable ignore) {}

long last = 0L;
try {
    last = u.getLastTimeUsed();
} catch (Throwable ignore) {}

Long curFg = mergedFgMinutes.get(pkg);
mergedFgMinutes.put(pkg, (curFg == null ? 0L : curFg) + fgMins);

Long curBg = mergedBgMinutes.get(pkg);
mergedBgMinutes.put(pkg, (curBg == null ? 0L : curBg) + bgMins);

Long lastCur = mergedLastUsed.get(pkg);
if (lastCur == null || last > lastCur) {
    mergedLastUsed.put(pkg, last);
        }
    }
}

if (stats == null || stats.isEmpty()) {
    showAppsStable();
    return;
}

if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
    showAppsStable();
    return;
}

PackageManager pm = getPackageManager();

for (String pkg : mergedBgMinutes.keySet()) {

    if (pkg == null) continue;
    if (pkg.equals(getPackageName())) continue;

    long fgMinutes =
            mergedFgMinutes.containsKey(pkg)
                    ? mergedFgMinutes.get(pkg)
                    : 0L;

    long bgMinutes =
            mergedBgMinutes.containsKey(pkg)
                    ? mergedBgMinutes.get(pkg)
                    : 0L;

    Long lastObj = mergedLastUsed.get(pkg);
    long lastUsed = lastObj != null ? lastObj : 0L;

    long hoursSinceUse =
            lastUsed > 0
                    ? (now - lastUsed) / (1000L * 60 * 60)
                    : 999999;

    // ✅ ΜΟΝΟ background χωρίς άνοιγμα
    boolean userOpened = fgMinutes > 0;
    boolean bgNoOpen = (!userOpened && bgMinutes > 0);
    if (!bgNoOpen) continue;

    try {

        ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
        boolean isSystem =
                (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

        if (isSystem) continue;

        String badge;
        int level;

        if (bgMinutes >= 60) {
            badge = gr ? "🟥 Background χωρίς άνοιγμα \n\n"
                       : "🟥 Background without opening \n\n";
            level = 3;
        } else {
            badge = gr ? "🟨 Δραστηριότητα παρασκηνίου χωρίς άνοιγμα \n\n"
                       : "🟨 Background activity without opening \n\n";
            level = 2;
        }

        AppAppRisk r =
                new AppAppRisk(pkg, fgMinutes, bgMinutes, hoursSinceUse, badge);

        if (level >= 3) heavy.add(r);
        else moderate.add(r);

    } catch (Throwable ignore) {}
}

// ✅ STABLE
if (heavy.isEmpty() && moderate.isEmpty()) {
    showAppsStable();
    return;
}

    ScrollView scroll = new ScrollView(this);

    LinearLayout root = buildBaseBox(
        progressTitle(
                gr
                        ? "ΒΗΜΑ 4 — Δραστηριότητα Εφαρμογών (48 ώρες)"
                        : "STEP 4 — App Activity (48 hours)"
        )
);

scroll.addView(root);

// 🔎 Explanation
TextView explain = new TextView(this);
explain.setText(
        gr
                ? "Τα αποτελέσματα αφορούν εφαρμογές που έτρεξαν στο παρασκήνιο\n"
                  + "χωρίς να τις ανοίξεις τις τελευταίες 48 ώρες. \n\n"
                : "Results refer to apps that ran in the background\n"
                  + "without you opening them in the last 48 hours. \n\n"
);

explain.setTextColor(0xFFFFFFFF);  // λευκό
explain.setPadding(0, dp(8), 0, dp(18));

root.addView(explain);

// ----------------------------------------------------
// SMART VERDICT ENGINE (USER-AWARE)
// ----------------------------------------------------

String verdict;

if (!heavy.isEmpty()) {
    verdict = "HEAVY";
} else if (!moderate.isEmpty()) {
    verdict = "MODERATE";
} else {
    verdict = "STABLE";
}

appsVerdict = verdict;
addAppsVerdict(root, verdict, heavy.size(), moderate.size());

if (!heavy.isEmpty()) {
    addSection(
            root,
            gr ? "🔥 Υψηλή Δραστηριότητα"
               : "🔥 High Activity",
            "",
            0xFFFF5252
    );
    addAppList(root, heavy);
}

if (!moderate.isEmpty()) {
    addSection(
            root,
            gr ? "⚠️ Μέτρια Δραστηριότητα"
               : "⚠️ Moderate Activity",
            "",
            0xFFFFC107
    );
    addAppList(root, moderate);
}

    Button next = mkGreenBtn("OK");
next.setOnClickListener(v -> go(STEP_UNUSED));
root.addView(next);

showCustomDialog(scroll);

} catch (Throwable ignore) {
}

}

// ----------------------------------------------------
// STEP 6 - UNUSED APPS
// ----------------------------------------------------

private void showInactiveApps() {

    if (!hasUsageAccess()) {
        go(STEP_CACHE);
        return;
    }

    long now = System.currentTimeMillis();
    long threshold = now - (30L * 24 * 60 * 60 * 1000); // 30 days

    ArrayList<UnusedApp> unused = new ArrayList<>();

    try {

        UsageStatsManager usm =
                (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);

        PackageManager pm = getPackageManager();

// ----------------------------------------------------
// 1️⃣ Build lastUsedMap from UsageStats (max lastTimeUsed per pkg)
// ----------------------------------------------------
HashMap<String, Long> lastUsedMap = new HashMap<>();

List<UsageStats> stats =
        usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                threshold,
                now
        );

if (stats != null) {
    for (UsageStats u : stats) {

        if (u == null) continue;

        String pkg = u.getPackageName();
        if (pkg == null) continue;
        if (pkg.equals(getPackageName())) continue;

        long last = 0L;
        try { last = u.getLastTimeUsed(); } catch (Throwable ignore) {}

        if (last > 0L) {
            Long cur = lastUsedMap.get(pkg);
            if (cur == null || last > cur) {
                lastUsedMap.put(pkg, last);
            }
        }
    }
    }

// ----------------------------------------------------
// 2️⃣ Iterate ALL installed apps
// ----------------------------------------------------
List<ApplicationInfo> installed =
        pm.getInstalledApplications(0);

for (ApplicationInfo ai : installed) {

    String pkg = ai.packageName;
    if (pkg == null) continue;
    if (pkg.equals(getPackageName())) continue;

    // skip system apps
    boolean isSystem =
            (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    if (isSystem) continue;

    long lastUsed =
            lastUsedMap.containsKey(pkg)
                    ? lastUsedMap.get(pkg)
                    : 0L;

    long installTime = 0L;
    try {
        installTime = pm.getPackageInfo(pkg, 0).firstInstallTime;
    } catch (Throwable ignore) {}

    long basis = 0L;

if (lastUsed > 0L) {
    basis = lastUsed;
} else if (installTime > 0L && installTime <= now) {
    basis = installTime;
}

// Αν δεν έχουμε έγκυρη βάση → skip
if (basis <= 0L || basis > now) continue;

    // Αν δεν έχουμε ούτε usage ούτε install time → skip
    if (basis <= 0L) continue;

    long daysSinceUse = (now - basis) / (1000L * 60 * 60 * 24);

    if (daysSinceUse >= 30) {
        unused.add(new UnusedApp(pkg, daysSinceUse));
    }
}

    } catch (Throwable ignore) {}

    if (unused.isEmpty()) {
        go(STEP_CACHE);
        return;
    }
    
    java.util.Collections.sort(
        unused,
        (a, b) -> Long.compare(b.days, a.days)
);

    // ----------------------------------------------------
    // UI
    // ----------------------------------------------------
    ScrollView scroll = new ScrollView(this);

    LinearLayout root = buildBaseBox(
            progressTitle(
    gr ? "ΒΗΜΑ 5 — Αδρανείς Εφαρμογές (30 ημέρες)"
       : "STEP 5 — Unused Applications (30 days)"
)
    );

    scroll.addView(root);

    TextView info = new TextView(this);
info.setText(
        gr
                ? "Εφαρμογές που δεν έχουν χρησιμοποιηθεί >30 ημέρες.\n"
                  + "Ενδέχεται να πιάνουν χώρο ή δικαιώματα.\n\n"
                  + "Συνιστάται η απεγκατάσταση όσων δεν χρειάζεσαι. \n\n"
                : "Apps not used for over 30 days.\n"
                  + "They may occupy storage or hold permissions.\n\n"
                  + "It is recommended to uninstall those you don’t need. \n\n"
);
info.setTextColor(0xFFAAAAAA);
info.setPadding(0, 0, 0, 25);
root.addView(info);

    PackageManager pm = getPackageManager();

    for (UnusedApp r : unused) {

        String label = r.pkg;
        try {
            ApplicationInfo ai = pm.getApplicationInfo(r.pkg, 0);
            CharSequence cs = pm.getApplicationLabel(ai);
            if (cs != null) label = cs.toString();
        } catch (Throwable ignore) {}

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, 15, 0, 15);

        TextView name = new TextView(this);
        name.setText("• " + label);
        name.setTextColor(Color.WHITE);
        name.setTypeface(null, Typeface.BOLD);

        TextView meta = new TextView(this);
        meta.setText(
                (gr ? "Χωρίς χρήση για "
                    : "Unused for ")
                + r.days
                + (gr ? " ημέρες"
                    : " days")
        );
        meta.setTextColor(0xFFFFC107);
        meta.setPadding(0, 6, 0, 10);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);

        Button uninstall = mkRedBtn(gr ? "Απεγκατάσταση" : "Uninstall");
        Button details = mkBlackGoldBtn(gr ? "Λεπτομέρειες" : "Details");

        uninstall.setOnClickListener(v -> uninstallPkg(r.pkg));
        details.setOnClickListener(v -> openAppDetails(r.pkg));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );
        lp.setMargins(dp(6), 0, dp(6), 0);

        uninstall.setLayoutParams(lp);
        details.setLayoutParams(lp);

        btnRow.addView(details);
        btnRow.addView(uninstall);

        row.addView(name);
        row.addView(meta);
        row.addView(btnRow);

        root.addView(row);
    }

addActionButtons(
    root,
    () -> go(STEP_CACHE)
);

    showCustomDialog(scroll);
}

private static class UnusedApp {
    final String pkg;
    final long days;

    UnusedApp(String p, long d) {
        pkg = p;
        days = d;
    }
}

// ============================================================
// APPS MODEL
// ============================================================

private static class AppAppRisk {
    final String pkg;
    final long fgMinutes;   // user opened (foreground UI)
    final long bgMinutes;   // background via Foreground Service (Android 10+)
    final long hoursSinceUse;
    final String badge;

    AppAppRisk(String p, long fg, long bg, long h, String b) {
        pkg = p;
        fgMinutes = fg;
        bgMinutes = bg;
        hoursSinceUse = h;
        badge = b;
    }
}

// ============================================================
// STABLE
// ============================================================

private void showAppsStable() {

    showDialog(
            progressTitle(gr ? "ΒΗΜΑ 4 — Δραστηριότητα Εφαρμογών στο παρασκήνιο, τις τελευταίες 48 ώρες"
                    : "STEP 4 — App Background Activity in the last 48 hours)"),
            gr
                    ? "🟢 Engine Verdict: STABLE\n\n"
                    + "Καμμία εφαρμογή δεν είχε δραστηριότητα στο παρασκήνιο,\n"
                    + "τις τελευταίες 48 ώρες. \n\n"
                    : "🟢 Engine Verdict: STABLE\n\n"
                    + "No app showed background activity\n"
                    + "in the last 48 hours. \n\n",
            null,
            () -> go(STEP_UNUSED),
            false
    );
}

// ============================================================
// VERDICT
// ============================================================

private void addAppsVerdict(LinearLayout root,
                            String verdict,
                            int heavy,
                            int moderate) {

    TextView tv = new TextView(this);

    int color =
        verdict.equals("HEAVY") ? 0xFFFF5252 :
        verdict.equals("MODERATE") ? 0xFFFFC107 :
        0xFF00C853;

    tv.setText(
        "Engine Verdict: " + verdict + "\n\n"
        + (gr ? "Υψηλή Δραστηριότητα στο παρασκήνιο:\n\n"
              : "High Background Activity:\n\n")
        + heavy + "\n\n"
        + (gr ? "Μέτρια Δραστηριότητα στο παρασκήνιο:\n\n"
              : "Moderate Background Activity:\n\n")
        + moderate + "\n\n"
);

    tv.setTextColor(color);
    tv.setTextSize(15f);
    tv.setPadding(0,10,0,25);

    root.addView(tv);
}

// ============================================================
// LIST ROWS
// ============================================================

private void addAppList(LinearLayout root,
                        List<AppAppRisk> list) {

    PackageManager pm = getPackageManager();

    int shown = 0;

    for (AppAppRisk r : list) {

        if (++shown > 12) break;

        String label = r.pkg;

        try {
            ApplicationInfo ai = pm.getApplicationInfo(r.pkg, 0);
            label = pm.getApplicationLabel(ai).toString();
        } catch (Throwable ignore) {}
        
        boolean isSystem = false;
try {
    ApplicationInfo ai = pm.getApplicationInfo(r.pkg, 0);
    isSystem = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
} catch (Throwable ignore) {}

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0,14,0,14);

        TextView name = new TextView(this);
        name.setText("• " + label);
        name.setTextColor(Color.WHITE);
        name.setTypeface(null, Typeface.BOLD);

        TextView meta = new TextView(this);
meta.setText(
        (gr ? "Δραστηριότητα στο παρασκήνιο: \n\n"
            : "Background Activity: \n\n")
        + r.bgMinutes
        + (gr ? " λεπτά (48h)" : " min (48h)")
        + "  |  "
        + (gr ? "Τελευταία χρήση: "
              : "Last used: ")
        + r.hoursSinceUse + "h"
        + "\n"
        + r.badge
);

if (isSystem) {
    meta.append(gr
        ? "  |  ⚙️ Εφαρμογή Συστήματος."
        : "  |  ⚙️ System App.");
}

        meta.setTextColor(0xFF00FF7F);
        meta.setPadding(0,6,0,12);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER);

        Button details = mkBlackGoldBtn(gr ? "Λεπτομέρειες" : "Details");
        Button uninstall = mkRedBtn(gr ? "Απεγκατάσταση" : "Uninstall");
       
        details.setOnClickListener(v -> openAppDetails(r.pkg));
        uninstall.setOnClickListener(v -> uninstallPkg(r.pkg));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f);
        lp.setMargins(dp(6),0,dp(6),0);

        details.setLayoutParams(lp);
        uninstall.setLayoutParams(lp);

        btnRow.addView(details);

if (!isSystem) {
    btnRow.addView(uninstall);
}

        row.addView(name);
        row.addView(meta);
        row.addView(btnRow);

        root.addView(row);
    }
}

private void addBatteryAppList(LinearLayout root,
                               List<AppRisk> list) {

    PackageManager pm = getPackageManager();

    int shown = 0;

    for (AppRisk r : list) {

        if (r == null || r.packageName == null) continue;

        // ✅ ΜΗΝ εμφανίζεις system apps
        if (isSystemPkg(r.packageName)) continue;

        if (++shown > 12) break;

        String label = r.packageName;

        try {
            ApplicationInfo ai = pm.getApplicationInfo(r.packageName, 0);
            CharSequence cs = pm.getApplicationLabel(ai);
            if (cs != null) label = cs.toString();
        } catch (Throwable ignore) {}

        if (++shown > 12) break;

        try {
            ApplicationInfo ai = pm.getApplicationInfo(r.packageName, 0);
            CharSequence cs = pm.getApplicationLabel(ai);
            if (cs != null) label = cs.toString();
        } catch (Throwable ignore) {}
        
        boolean isSystem = false;
try {
    ApplicationInfo ai = pm.getApplicationInfo(r.packageName, 0);
    isSystem = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
} catch (Throwable ignore) {}

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, 14, 0, 14);

        TextView name = new TextView(this);
        name.setText("• " + label);
        name.setTextColor(Color.WHITE);
        name.setTypeface(null, Typeface.BOLD);

        TextView meta = new TextView(this);

meta.setText(
        (gr ? "Χρήση: \n\n" : "Usage: \n\n")
                + r.minutes
                + (gr ? " λεπτά (48h)" : " min (48h)")
                + (r.unrestricted
                ? (gr ? "  |  ⚠️ Χωρίς περιορισμό μπαταρίας"
                      : "  |  ⚠️ Battery unrestricted")
                : "")
);

meta.setTextColor(r.unrestricted ? 0xFFFFC107 : 0xFF00FF7F);
meta.setPadding(0, 6, 0, 12);
        
        if (isSystem) {
    meta.append(gr
        ? "  |  ⚙️ Εφαρμογή Συστήματος."
        : "  |  ⚙️ System App.");
}

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER);

        Button details = mkBlackGoldBtn(gr ? "Λεπτομέρειες" : "Details");
        Button uninstall = mkRedBtn(gr ? "Απεγκατάσταση" : "Uninstall");

        details.setOnClickListener(v -> openAppDetails(r.packageName));
        uninstall.setOnClickListener(v -> uninstallPkg(r.packageName));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );
        lp.setMargins(dp(6), 0, dp(6), 0);

        details.setLayoutParams(lp);
        uninstall.setLayoutParams(lp);

        btnRow.addView(details);

if (!isSystem) {
    btnRow.addView(uninstall);
}

        row.addView(name);
        row.addView(meta);
        row.addView(btnRow);

        View div = new View(this);
        div.setBackgroundColor(0xFF222222);
        LinearLayout.LayoutParams dlp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                );
        dlp.setMargins(0, dp(14), 0, 0);
        div.setLayoutParams(dlp);

        row.addView(div);

        root.addView(row);
    }
}

    // ============================================================
    // STEP 6 — CACHE
    // ============================================================

    private void showCache() {

        showDialog(
                progressTitle(gr ? "ΒΗΜΑ 6 — Cache" : "STEP 6 — Cache"),
                gr
                        ? "Θα ανοίξει η λίστα εφαρμογών. Πάτησε ταξινόμηση κατά «Μεγαλύτερη % Cache».\n\n"
                        + "Καθάρισε εφαρμογές με μεγάλη προσωρινή μνήμη — ή και όλες. \n\n"
                        + "Στην πρώτη ομάδα θα δεις τις εφαρμογές που έχεις εγκαταστήσει. \n\n"
                        + "Στη δεύτερη ομάδα θα δεις τις εφαρμογές συστήματος. \n\n"
                        + "Η εκκαθάριση cache είναι ασφαλής και δεν διαγράφει προσωπικά δεδομένα.\n"
                        + "Απόφυγε την εκκαθάριση δεδομένων εκτός αν γνωρίζεις τις συνέπειες.\n\n"
                        + "Πάτησε OK/ΠΑΡΑΛΕΙΨΗ όταν ολοκληρώσεις για να συνεχίσουμε.\n\n"
                        : "The app list will open. Tap to sort by “Largest % Cache”.\n\n"
                        + "Clear apps with large temporary cache — or all of them if needed. \n\n"
                        + "In the first group you will see apps you installed. \n\n"
                        + "In the second group you will see system apps. \n\n"
                        + "Clearing cache is safe and does not remove personal data.\n"
                        + "Avoid clearing app data unless you understand the consequences.\n\n"
                        + "Press OK/SKIP when finished to continue.\n\n",
                () -> {
    try {
        Intent i = new Intent(this, AppListActivity.class);
        i.putExtra("mode", "cache");
        startActivity(i);
    } catch (Exception e) {
        Toast.makeText(
                this,
                gr ? "Δεν ήταν δυνατό να ανοίξει ο καθαριστής cache. \n\n"
                   : "Unable to open cache cleaner. \n\n",
                Toast.LENGTH_SHORT
        ).show();
    }
},
() -> go(STEP_DNS),
false
    );
}

private void showDnsStep() {

    final boolean gr = AppLang.isGreek(this);

    LinearLayout root = buildBaseBox(
            progressTitle(gr
                    ? "ΒΗΜΑ 7 — Μπλοκάρισμα Διαφημίσεων"
                    : "STEP 7 — Block Advertisements")
    );

    TextView body = new TextView(this);
    body.setText(gr
            ? "Θέλεις να ρυθμίσουμε τη συσκευή σου ώστε να μπλοκάρει "
             + "τις διαφημίσεις από άλλες εφαρμογές και το ίντερνετ;\n"
             + "χωρίς να χρειαστεί εγκατάσταση άλλης εφαρμογής;\n\n"
             + "Θα βελτιωθεί πολύ η περιήγηση στις ιστοσελίδες, "
             + "αφού θα μπλοκάρονται οι διαφημίσεις και τα αναδυόμενα παράθυρα. \n\n"
            : "Would you like to configure your device to block ads "
             + "from other applications and the internet?\n"
             + "without installing any additional application?\n\n"
             + "Browsing will improve significantly, "
             + "as advertisements and pop-up windows will be blocked. \n\n"
    );

    body.setTextColor(0xFF00FF7F);
    body.setPadding(0, dp(16), 0, dp(20));
    root.addView(body);

    Button yesBtn = mkGreenBtn(gr ? "ΝΑΙ" : "YES");
    yesBtn.setOnClickListener(v -> showDnsHowToDialog());

    Button noBtn = mkRedBtn(gr ? "ΟΧΙ" : "NO");
    noBtn.setOnClickListener(v -> go(STEP_QUEST));

    root.addView(yesBtn);
    root.addView(noBtn);

    showCustomDialog(root);
}

    // ============================================================
    // QUESTIONNAIRE
    // ============================================================

    private void showQuestionnaire() {

        LinearLayout root = buildBaseBox(
                gr ? "Πρόσεξες τελευταία κάτι που σε προβλημάτισε στη συσκευή σου; \n\n"
   : "Have you noticed anything unusual on your device recently? \n\n"
        );

        CheckBox heat = mkCheck(gr?"Υψηλή θερμοκρασία":"High temperature");
        CheckBox crash = mkCheck(gr?"Κρασαρίσματα":"Crashes");
        CheckBox lag = mkCheck(gr?"Κολλάει":"Lag");
        CheckBox charge = mkCheck(gr?"Αργή φόρτιση":"Slow charging");
        CheckBox data = mkCheck(gr?"Internet issues":"Internet issues");
        CheckBox camera = mkCheck(gr?"Κακά χρώματα κάμερας":"Bad camera colors");
        CheckBox bluetooth = mkCheck(gr?"Bluetooth πρόβλημα":"Bluetooth issues");
        CheckBox sound = mkCheck(gr?"Πρόβλημα ήχου":"Sound issues");
        CheckBox boot = mkCheck(gr?"Αργή εκκίνηση":"Slow boot");
        CheckBox wifi = mkCheck(gr?"WiFi αστάθεια":"WiFi instability \n\n");

        root.addView(heat);
        root.addView(crash);
        root.addView(lag);
        root.addView(charge);
        root.addView(data);
        root.addView(camera);
        root.addView(bluetooth);
        root.addView(sound);
        root.addView(boot);
        root.addView(wifi);

        addActionButtons(
        root,
        () -> {
            symptoms.clear();
            if (heat.isChecked()) symptoms.add("heat");
            if (crash.isChecked()) symptoms.add("crash");
            if (lag.isChecked()) symptoms.add("lag");
            if (charge.isChecked()) symptoms.add("charge");
            if (data.isChecked()) symptoms.add("data");
            if (camera.isChecked()) symptoms.add("camera");
            if (bluetooth.isChecked()) symptoms.add("bluetooth");
            if (sound.isChecked()) symptoms.add("sound");
            if (boot.isChecked()) symptoms.add("boot");
            if (wifi.isChecked()) symptoms.add("wifi");
            go(STEP_LABS);
        }
);

        showCustomDialog(root);
    }

// ============================================================
// LAB RECOMMENDATION (FIXED FLOW: Labs / OK / Exit)
// ============================================================
private void showLabRecommendation() {

    if (symptoms == null || symptoms.isEmpty()) {
        go(STEP_REMINDER);
        return;
    }

    LinearLayout root = buildBaseBox(
            gr
                    ? "Για να ελέγξεις όσα μας ανέφερες, σου προτείνουμε να τρέξεις τα παρακάτω διαγνωστικά Εργαστήρια \n\n"
                    : "Based on what you reported, we recommend running the following diagnostic Labs \n\n"
    );

    TextView tv = new TextView(this);
    tv.setText(buildTechnicalRecommendationText(symptoms));
    tv.setTextColor(0xFF00FF7F);
    tv.setPadding(0, dp(20), 0, dp(20));
    root.addView(tv);

    // ------------------------------------------------------------
    // 1) RUN LABS (BLACK / NEON GREEN) — same as Settings buttons
    // ------------------------------------------------------------
    Button labsBtn = mkBlackGoldBtn(gr ? "Εκτέλεση Εργαστηρίων" : "Run Labs");
    labsBtn.setOnClickListener(v -> {
        try {
            startActivity(new Intent(this, ManualTestsActivity.class));
        } catch (Throwable t) {
            Toast.makeText(
                    this,
                    gr ? "Δεν ήταν δυνατό να ανοίξουν τα εργαστήρια. \n\n"
                       : "Unable to open labs. \n\n",
                    Toast.LENGTH_SHORT
            ).show();
        }
    });
    root.addView(labsBtn);

    // ------------------------------------------------------------
    // 2) OK (GREEN) — continue to next step (NOT labs)
    // ------------------------------------------------------------
    Button okBtn = mkGreenBtn(okSkipLabel(false));
    okBtn.setOnClickListener(v -> go(STEP_REMINDER));
     root.addView(okBtn);

    // ------------------------------------------------------------
    // 3) EXIT (RED)
    // ------------------------------------------------------------
    Button exitBtn = mkRedBtn(gr ? "Έξοδος" : "Exit");
    exitBtn.setOnClickListener(v -> {
        Toast.makeText(
                this,
                gr ? "Η βελτιστοποίηση διακόπηκε."
                   : "Optimization cancelled.",
                Toast.LENGTH_SHORT
        ).show();
        finish();
    });
    root.addView(exitBtn);

    showCustomDialog(root);
}

    private String buildTechnicalRecommendationText(ArrayList<String> s) {

    java.util.LinkedHashSet<String> labs = new java.util.LinkedHashSet<>();

    if (s.contains("heat")) {
        labs.add(gr
                ? "LAB 16 — Θερμικός έλεγχος"
                : "LAB 16 — Thermal diagnostics");
        labs.add(gr
                ? "LAB 14 — Έλεγχος μπαταρίας"
                : "LAB 14 — Battery health analysis");
    }

    if (s.contains("charge")) {
        labs.add(gr
                ? "LAB 15 — Έλεγχος φόρτισης"
                : "LAB 15 — Charging diagnostics");
        labs.add(gr
                ? "LAB 14 — Έλεγχος μπαταρίας"
                : "LAB 14 — Battery health analysis");
    }

    if (s.contains("lag")) {
        labs.add(gr
                ? "LAB 19 — Απόδοση συστήματος"
                : "LAB 19 — System performance analysis");
        labs.add(gr
                ? "LAB 26 — Ανάλυση επιπτώσεων εφαρμογών"
                : "LAB 26 — Installed apps impact analysis");
    }

    if (s.contains("crash")) {
        labs.add(gr
                ? "LAB 25 — Ανάλυση κρασαρισμάτων"
                : "LAB 25 — Crash intelligence analysis");
        labs.add(gr
                ? "LAB 30 — Τελική τεχνική αναφορά"
                : "LAB 30 — Final technical report");
    }

    if (s.contains("data") || s.contains("wifi")) {
        labs.add(gr
                ? "LAB 26 — Δίκτυο & background χρήση"
                : "LAB 26 — Network & background activity analysis");
    }

    if (s.contains("camera")) {
        labs.add(gr
                ? "LAB 8 — Διαγνωστικός έλεγχος κάμερας"
                : "LAB 8 — Camera diagnostics");
    }

    if (s.contains("bluetooth")) {
        labs.add(gr
                ? "LAB 5 — Έλεγχος Bluetooth"
                : "LAB 5 — Bluetooth diagnostics");
    }

    if (s.contains("sound")) {
        labs.add(gr
                ? "LAB 1–4 — Διαγνωστικά ήχου"
                : "LAB 1–4 — Audio diagnostics");
    }

    if (s.contains("boot")) {
        labs.add(gr
                ? "LAB 19 — Εκκίνηση & Απόδοση"
                : "LAB 19 — Boot & performance analysis");
    }

    labs.add(gr
            ? "LAB 29 — Τελική σύνοψη υγείας"
            : "LAB 29 — Final health summary");

    StringBuilder sb = new StringBuilder();

    sb.append(gr
            ? "Προτείνονται τα εξής εργαστήρια:\n\n"
            : "Recommended labs:\n\n");

    for (String l : labs) {
        sb.append("• ").append(l).append("\n");
    }

    return sb.toString();
}

    // ============================================================
    // REMINDER
    // ============================================================

    private void showReminder() {

    LinearLayout root = buildBaseBox(
            gr ? "Αν έμεινες ευχαριστημένος/η από το αποτέλεσμα, θα ήθελες να σου υπενθυμίζουμε τακτικά, να κάνουμε την ίδια επιθεώρηση στη συσκευή σου; \n\n"
               : "If you're satisfied with the results, would you like regular reminders, to run the same device inspection? \n\n"
    );

    Button daily = mkGreenBtn(gr ? "1 Ημέρα" : "Daily");
    Button weekly = mkGreenBtn(gr ? "1 Εβδομάδα" : "Weekly");
    Button monthly = mkGreenBtn(gr ? "1 Μήνας" : "Monthly");
    Button skip = mkRedBtn(gr ? "Παράλειψη" : "Skip");

    daily.setOnClickListener(v -> {
    OptimizerScheduler.enableReminder(this,1);
    go(STEP_MINI_REMINDER);
});

weekly.setOnClickListener(v -> {
    OptimizerScheduler.enableReminder(this,7);
    go(STEP_MINI_REMINDER);
});

monthly.setOnClickListener(v -> {
    OptimizerScheduler.enableReminder(this,30);
    go(STEP_MINI_REMINDER);
});

skip.setOnClickListener(v -> go(STEP_MINI_REMINDER));

    root.addView(daily);
    root.addView(weekly);
    root.addView(monthly);
    root.addView(skip);

    showCustomDialog(root);
}

private void showMiniSchedulerPopup() {

    final boolean gr = AppLang.isGreek(this);

    LinearLayout root = buildBaseBox(
        gr ? "Mini Έλεγχος στο Παρασκήνιο"
           : "Mini Background Check"
    );

    TextView body = new TextView(this);
    body.setText(gr
            ? "Θέλεις 3 φορές την ημέρα να κάνουμε έναν mini έλεγχο στο κινητό σου στο παρασκήνιο;\n\n"
              + "Κάθε mini check θα διαρκεί λιγότερο από 1 δευτερόλεπτο.\n\n"
              + "Θα παρακολουθούμε ενδείξεις όπως:\n"
              + "• Υψηλή θερμοκρασία (όταν είναι διαθέσιμο)\n"
              + "• Υπερβολική cache (> 80%)\n"
              + "• Ύποπτη αστάθεια συστήματος (best-effort)\n\n"
              + "Αν βρούμε κάτι, θα σου εμφανίσουμε ειδοποίηση με προτάσεις."
            : "Would you like us to run a mini background check 3 times per day?\n\n"
              + "Each mini check lasts under 1 second.\n\n"
              + "We monitor signals such as:\n"
              + "• High temperature (when available)\n"
              + "• Excessive cache (> 80%)\n"
              + "• Possible system instability (best-effort)\n\n"
              + "If something is detected, we will notify you with recommendations."
    );

    body.setTextColor(0xFF00FF7F);
    body.setPadding(0, dp(16), 0, dp(20));
    root.addView(body);

    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
    lp.setMargins(dp(8), 0, dp(8), 0);

    Button noBtn = mkRedBtn(gr ? "ΟΧΙ" : "NO");
    noBtn.setLayoutParams(lp);

    Button yesBtn = mkGreenBtn(gr ? "ΝΑΙ" : "YES");
    yesBtn.setLayoutParams(lp);

    row.addView(noBtn);
    row.addView(yesBtn);

    root.addView(row);

    noBtn.setOnClickListener(v -> {
        setPulseEnabled(false);
        cancelMiniPulse();
        go(STEP_FINAL);  // συνεχίζουμε στο Questionnaire
    });

    yesBtn.setOnClickListener(v -> {
        setPulseEnabled(true);
        scheduleMiniPulse3xDaily();
        go(STEP_FINAL);  // συνεχίζουμε στο Questionnaire
    });

    showCustomDialog(root);
}

    // ============================================================
    // SETTINGS FALLBACKS
    // ============================================================

    private void openStorageSettings() {
    safeStartActivity(
            gr ? "Αποθήκευση" : "Storage",
            Settings.ACTION_INTERNAL_STORAGE_SETTINGS,
            Settings.ACTION_MEMORY_CARD_SETTINGS
    );
}

    private void openBatteryUsage() {
    safeStartActivity(
            gr ? "Μπαταρία" : "Battery",
            "android.settings.BATTERY_USAGE_SETTINGS",
            "android.settings.POWER_USAGE_SUMMARY",
            Settings.ACTION_BATTERY_SAVER_SETTINGS
    );
}

    private void openDataUsage() {
    safeStartActivity(
            gr ? "Δεδομένα" : "Data Usage",
            "android.settings.DATA_USAGE_SETTINGS",
            Settings.ACTION_WIRELESS_SETTINGS
    );
}

    private void open(String action) {
        try { startActivity(new Intent(action)); } catch (Throwable ignore) {}
    }

    private void openLargestCache() {
        Intent i = new Intent(this, AppListActivity.class);
        i.putExtra("auto_largest_cache", true);
        startActivity(i);
    }

// ============================================================
// CENTRAL LABEL (OK / SKIP) — used by ALL steps
// ============================================================
private String okSkipLabel(boolean isIntro) {
    if (isIntro) return (gr ? "Έναρξη" : "Start");
    return (gr ? "OK / ΠΑΡΑΛΕΙΨΗ" : "OK / SKIP");
}

// ============================================================
// DIALOG ENGINE (UPDATED)
// ============================================================
private void showDialog(String title,
                        String body,
                        Runnable settingsAction,
                        Runnable okAction,
                        boolean isIntro) {

    LinearLayout root = buildBaseBox(title);

    TextView tvBody = new TextView(this);
    tvBody.setText(body);
    tvBody.setTextColor(0xFF00FF7F);
    tvBody.setPadding(0, 20, 0, 20);
    root.addView(tvBody);

    if (settingsAction != null) {
        Button settingsBtn = mkBlackGoldBtn(gr ? "Ρυθμίσεις" : "Settings");
        settingsBtn.setOnClickListener(v -> settingsAction.run());
        root.addView(settingsBtn);
    }

    // ✅ CENTRAL OK LABEL
    Button okBtn = mkGreenBtn(okSkipLabel(isIntro));
    okBtn.setOnClickListener(v -> okAction.run());
    root.addView(okBtn);

    Button exitBtn = mkRedBtn(gr ? "Έξοδος" : "Exit");
    exitBtn.setOnClickListener(v -> {
        Toast.makeText(this,
                gr ? "Η βελτιστοποίηση διακόπηκε."
                   : "Optimization cancelled.",
                Toast.LENGTH_SHORT).show();
        finish();
    });
    root.addView(exitBtn);

    showCustomDialog(root);
}

// ============================================================
// ACTION BUTTONS — OK/SKIP + EXIT (MATCHES showDialog)
// ============================================================
private void addActionButtons(LinearLayout root, Runnable okAction) {

    Button okBtn = mkGreenBtn(okSkipLabel(false));
    okBtn.setOnClickListener(v -> okAction.run());
    root.addView(okBtn);

    Button exitBtn = mkRedBtn(gr ? "Έξοδος" : "Exit");
    exitBtn.setOnClickListener(v -> {
        Toast.makeText(
                this,
                gr ? "Η βελτιστοποίηση διακόπηκε."
                   : "Optimization cancelled.",
                Toast.LENGTH_SHORT
        ).show();
        finish();
    });
    root.addView(exitBtn);
}

    private Button mkGreenBtn(String t) {
        Button b = new Button(this);
        b.setText(t);
        b.setTextColor(Color.WHITE);
        GradientDrawable d = new GradientDrawable();
        d.setColor(0xFF00C853);
        d.setStroke(5,0xFFFFD700);
        d.setCornerRadius(25);
        b.setBackground(d);
        return b;
    }

    private Button mkRedBtn(String t) {
        Button b = new Button(this);
        b.setText(t);
        b.setTextColor(Color.WHITE);
        GradientDrawable d = new GradientDrawable();
        d.setColor(0xFFC62828);
        d.setStroke(5,0xFFFFD700);
        d.setCornerRadius(25);
        b.setBackground(d);
        return b;
    }

    private Button mkBlackGoldBtn(String t) {
        Button b = new Button(this);
        b.setText(t);
        b.setTextColor(0xFF00FF7F);
        GradientDrawable d = new GradientDrawable();
        d.setColor(0xFF000000);
        d.setStroke(5,0xFFFFD700);
        d.setCornerRadius(25);
        b.setBackground(d);
        return b;
    }

    private CheckBox mkCheck(String t) {
        CheckBox c = new CheckBox(this);
        c.setText(t);
        c.setTextColor(Color.WHITE);
        return c;
    }
    
    // ============================================================
// BASE DIALOG BOX (GEL STYLE)
// ============================================================
private LinearLayout buildBaseBox(String title) {

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(20), dp(20), dp(20), dp(20));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF111111);
    bg.setCornerRadius(dp(16));
    bg.setStroke(dp(2), 0xFFFFD700);
    root.setBackground(bg);

    TextView tvTitle = new TextView(this);
    tvTitle.setText(title);
    tvTitle.setTextColor(0xFFFFD700);
    tvTitle.setTextSize(18f);
    tvTitle.setTypeface(null, Typeface.BOLD);
    tvTitle.setPadding(0, 0, 0, dp(16));

    root.addView(tvTitle);

    return root;
}

// ============================================================
// CUSTOM GEL DIALOG
// ============================================================
private void showCustomDialog(View v) {

    AlertDialog dialog = new AlertDialog.Builder(this)
            .setView(v)
            .setCancelable(false)
            .create();

    dialog.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );

    dialog.show();
}

    private String progressTitle(String title) {
        int total = 7;
        int current = step;
        return title + " (" + current + "/" + total + ")";
    }
}
