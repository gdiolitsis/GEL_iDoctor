// GDiolitsis Engine Lab (GEL)
// GuidedOptimizerActivity — FINAL STABLE VERSION

package com.gel.cleaner;

import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.graphics.Color;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
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
import java.util.ArrayList;
import java.util.LinkedHashSet;

public final class GuidedOptimizerActivity extends AppCompatActivity {

    private boolean gr;
    private int step = 0;
    
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
    private static final int STEP_QUEST    = 7;
    private static final int STEP_LABS     = 8;
    private static final int STEP_REMINDER = 9;
    private static final int STEP_DONE     = 10;
    private static final int STEP_FINAL = 11;

    private final ArrayList<String> symptoms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gr = AppLang.isGreek(this);
        go(STEP_INTRO);
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

    for (AppRisk r : list) {

        if (++shown > LIMIT) break;

        // --- simple inline row ---
        TextView tv = new TextView(this);
        tv.setText("• " + r.packageName + "  (" + r.minutes + " min)");
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
    case STEP_QUEST: showQuestionnaire(); break;
    case STEP_LABS: showLabRecommendation(); break;
    case STEP_REMINDER: showReminder(); break;
    case STEP_FINAL: showFinalVerdict(); break;
    case STEP_DONE: finish(); break;
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
                        + "Πάτα «Έναρξη» για να ξεκινήσουμε."
                        : "I will guide you to the right system settings.\n\n"
                        + "The goal is to help your device run smoothly and securely.\n\n"
                        + "You make the choices — I simply steer (no autopilot 😄).\n\n"
                        + "Press “Start” to begin.",
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
                        + "και πάτησε OK για να συνεχίσουμε."
                        : "The device storage settings will open.\n\n"
                        + "Use the available cleaning tools where necessary.\n"
                        + "In most cases, clearing temporary cache, temporary data and residual files is sufficient.\n"
                        + "These actions are safe and do not remove personal data.\n\n"
                        + "WARNING: Clearing app data removes settings, saved accounts and offline content.\n"
                        + "Use it only if you fully understand the consequences.\n\n"
                        + "On some devices the app may close temporarily.\n\n"
                        + "After cleaning, reopen the app\n"
                        + "and press OK to continue.",
                () -> {

// --------------------------------------------------------
// 1️⃣ DEVICE STORAGE (PRIMARY) — extra Android safety net
// --------------------------------------------------------
try {
    Intent deviceStorage = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
    deviceStorage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(deviceStorage);
    return;
} catch (Throwable ignore) {}

// --------------------------------------------------------
// 2️⃣ GLOBAL STORAGE (SECONDARY)
// --------------------------------------------------------
try {
    Intent storage = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
    storage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(storage);
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
                progressTitle(gr ? "ΒΗΜΑ 2 — Ανάλυση Δραστηριότητας"
                                 : "STEP 2 — Activity Analysis"),
                gr
                        ? "Για να αναλύσουμε τη δραστηριότητα εφαρμογών,\n"
                        + "απαιτείται πρόσβαση Χρήσης Εφαρμογών.\n\n"
                        + "Πάτησε Ρυθμίσεις και ενεργοποίησε την άδεια για το GEL."
                        : "To analyze app activity,\n"
                        + "Usage Access permission is required.\n\n"
                        + "Press Settings and enable it for GEL.",
                () -> startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)),
                () -> go(STEP_DATA),
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

    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

    ArrayList<AppRisk> heavyApps = new ArrayList<>();
    ArrayList<AppRisk> moderateApps = new ArrayList<>();

    for (UsageStats u : stats) {

        long minutes = u.getTotalTimeInForeground() / 60000;
        if (minutes < 1) continue;

        String pkg = u.getPackageName();
        if (pkg.equals(getPackageName())) continue;

        boolean unrestricted = false;
        try {
            unrestricted = pm.isIgnoringBatteryOptimizations(pkg);
        } catch (Throwable ignore) {}

        int score;

        if (minutes >= 120) score = 3;          // HEAVY
        else if (minutes >= 45) score = 2;      // MODERATE
        else score = 1;                         // LOW

        if (unrestricted && score >= 2)
            score++; // elevate if unrestricted

        if (score >= 3)
            heavyApps.add(new AppRisk(pkg, minutes, unrestricted));
        else if (score == 2)
            moderateApps.add(new AppRisk(pkg, minutes, unrestricted));
    }

    if (heavyApps.isEmpty() && moderateApps.isEmpty()) {

        batteryVerdict = "STABLE";

        showStableDialog();
        return;
    }

    ScrollView scroll = new ScrollView(this);

    LinearLayout root = buildBaseBox(
            gr ? "Battery Intelligence Report (48 ώρες)"
               : "Battery Intelligence Report (48 hours)"
    );

    scroll.addView(root);

    String verdict =
            !heavyApps.isEmpty() ? "HEAVY"
            : "MODERATE";

    batteryVerdict = verdict;

    addEngineVerdict(root, verdict,
            heavyApps.size(),
            moderateApps.size());

    addRecommendations(root, verdict);

    if (!heavyApps.isEmpty()) {
        addSection(
                root,
                gr ? "🔥 Υψηλή Δραστηριότητα"
                   : "🔥 High Activity",
                gr ? "Εφαρμογές με σημαντική επιβάρυνση."
                   : "Apps with significant impact.",
                0xFFFF5252
        );
        limitAndAdd(root, heavyApps);
    }

    if (!moderateApps.isEmpty()) {
        addSection(
                root,
                gr ? "⚠️ Μέτρια Δραστηριότητα"
                   : "⚠️ Moderate Activity",
                gr ? "Εφαρμογές που αξίζουν έλεγχο."
                   : "Apps worth reviewing.",
                0xFFFFC107
        );
        limitAndAdd(root, moderateApps);
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
            progressTitle(gr ? "ΒΗΜΑ 2 — Ανάλυση"
                             : "STEP 2 — Analysis"),
            gr
                    ? "Engine Verdict: STABLE\n\n"
                    + "Δεν εντοπίστηκε ασυνήθιστη δραστηριότητα."
                    : "Engine Verdict: STABLE\n\n"
                    + "No abnormal activity detected.",
            null,
            () -> go(STEP_DATA),
            false
    );
}

private void showFinalVerdict() {

    LinearLayout root = buildBaseBox(
            gr ? "Τελική Αναφορά Συσκευής"
               : "Final Device Report"
    );

    String finalVerdict = resolveFinalVerdict();

    // ----------------------------
    // Section Details
    // ----------------------------

    addFinalRow(root,
            gr ? "Μπαταρία" : "Battery",
            batteryVerdict);

    addFinalRow(root,
            gr ? "Δεδομένα" : "Data",
            dataVerdict);

    addFinalRow(root,
            gr ? "Εφαρμογές" : "Apps",
            appsVerdict);

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
    // Final Status
    // ----------------------------

    TextView finalTv = new TextView(this);
    finalTv.setText(
            (gr ? "Συνολική Κατάσταση: "
                : "Overall Status: ")
            + finalVerdict
    );

    int color =
            finalVerdict.equals("HEAVY") ? 0xFFFF5252 :
            finalVerdict.equals("MODERATE") ? 0xFFFFC107 :
            0xFF00C853;

    finalTv.setTextColor(color);
    finalTv.setTextSize(18f);
    finalTv.setTypeface(null, Typeface.BOLD);
    finalTv.setPadding(0, dp(10), 0, dp(25));

    root.addView(finalTv);

    Button done = mkGreenBtn("OK");
    done.setOnClickListener(v -> finish());
    root.addView(done);

    showCustomDialog(root);
}

private String resolveFinalVerdict() {

    if ("HEAVY".equals(batteryVerdict)
            || "HEAVY".equals(dataVerdict)
            || "HEAVY".equals(appsVerdict)) {

        return "HEAVY";
    }

    if ("MODERATE".equals(batteryVerdict)
            || "MODERATE".equals(dataVerdict)
            || "MODERATE".equals(appsVerdict)) {

        return "MODERATE";
    }

    return "STABLE";
}

private void addFinalRow(LinearLayout root,
                         String label,
                         String verdict) {

    TextView tv = new TextView(this);

    int color =
            "HEAVY".equals(verdict) ? 0xFFFF5252 :
            "MODERATE".equals(verdict) ? 0xFFFFC107 :
            0xFF00C853;

    tv.setText(label + ": " + verdict);
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
            + (gr ? "Υψηλή: " : "High: ") + heavyCount + "\n"
            + (gr ? "Μέτρια: " : "Moderate: ") + moderateCount
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
                ? "Προτείνεται περιορισμός background δραστηριότητας ή απεγκατάσταση μη απαραίτητων εφαρμογών."
                : "Restrict background activity or uninstall unnecessary high-impact apps.";
    } else {
        rec = gr
                ? "Έλεγξε τις εφαρμογές μέτριας δραστηριότητας."
                : "Review moderate activity apps.";
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
                progressTitle(gr ? "ΒΗΜΑ 3 — Ανάλυση Δεδομένων" : "STEP 3 — Data Analysis"),
                gr
                        ? "Για να κάνουμε premium ανάλυση δεδομένων,\n"
                        + "χρειαζόμαστε πρόσβαση Χρήσης Εφαρμογών.\n\n"
                        + "Πάτησε Ρυθμίσεις και ενεργοποίησε την άδεια για το GEL."
                        : "To run premium data analysis,\n"
                        + "Usage Access permission is required.\n\n"
                        + "Press Settings and enable it for GEL.",
                () -> startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)),
                () -> go(STEP_APPS),
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
                    progressTitle(gr ? "ΒΗΜΑ 3 — Ανάλυση Δεδομένων" : "STEP 3 — Data Analysis"),
                    gr
                            ? "Engine Verdict: STABLE\n\n"
                            + "Δεν υπάρχουν διαθέσιμα usage στοιχεία (48 ώρες)."
                            : "Engine Verdict: STABLE\n\n"
                            + "No usage stats available (48 hours).",
                    null,
                    () -> go(STEP_APPS),
                    false
            );
            return;
        }

        PackageManager pm = getPackageManager();

        for (UsageStats u : stats) {

            if (u == null) continue;

            String pkg = u.getPackageName();
            if (pkg == null) continue;
            if (pkg.equals(getPackageName())) continue;

            long minutes = 0;
            try { minutes = u.getTotalTimeInForeground() / 60000L; }
            catch (Throwable ignore) {}

            if (minutes < 1) continue;

            long lastUsed = 0;
            try { lastUsed = u.getLastTimeUsed(); }
            catch (Throwable ignore) {}

            long hoursSinceUse = lastUsed > 0
                    ? (now - lastUsed) / (1000L * 60 * 60)
                    : 999999;

            // skip core system apps (keep consistent with your apps flow)
            try {
                ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
                boolean isSystem = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                if (isSystem) continue;
            } catch (Throwable ignore) {}

            // ----------------------------------------------------
            // HEURISTIC "DATA BEHAVIOUR" (no bytes, no lies)
            // ----------------------------------------------------
            // idea:
            // - lots of foreground time => high activity
            // - "rarely used but active": small minutes, but very recent last used (background-ish behaviour)
            boolean rarelyUsedButActive =
                    (minutes <= 5 && hoursSinceUse <= 12);

            long score =
                    (minutes * 2)
                    + (rarelyUsedButActive ? 30 : 0);

            // thresholds (tuned for 48h window)
            // HEAVY: score >= 240  (ex: 120min foreground)
            // MOD:   score >= 80
            if (score >= 240) {
                heavy.add(new DataRisk(pkg, score, minutes, hoursSinceUse, rarelyUsedButActive));
            } else if (score >= 80) {
                moderate.add(new DataRisk(pkg, score, minutes, hoursSinceUse, rarelyUsedButActive));
            }
        }

    } catch (Throwable ignore) {}

    if (heavy.isEmpty() && moderate.isEmpty()) {
        dataVerdict = "STABLE";
        showDialog(
                progressTitle(gr ? "ΒΗΜΑ 3 — Ανάλυση Δεδομένων" : "STEP 3 — Data Analysis"),
                gr
                        ? "Engine Verdict: STABLE\n\n"
                        + "Δεν εντοπίστηκαν ύποπτες ή βαριές συμπεριφορές χρήσης (48 ώρες)."
                        : "Engine Verdict: STABLE\n\n"
                        + "No suspicious or heavy usage behaviour detected (48 hours).",
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
            gr ? "Data Behaviour Intelligence (48 ώρες)"
               : "Data Behaviour Intelligence (48 hours)"
    );
    scroll.addView(root);

    final String verdict = !heavy.isEmpty() ? "HEAVY" : "MODERATE";
    dataVerdict = verdict;

    addEngineVerdictData(root, verdict, heavy.size(), moderate.size());

    TextView explain = new TextView(this);
    explain.setText(
            gr
                    ? "Αυτή είναι behavioural ανάλυση (όχι MB).\n\n"
                    + "• High Activity = πολλή χρήση εφαρμογής\n"
                    + "• 💤 Rarely Used but active = λίγη χρήση από εσένα, αλλά πρόσφατη/ύποπτη δραστηριότητα\n\n"
                    + "Πάτα σε μια εφαρμογή για ενέργειες."
                    : "This is behavioural analysis (not MB).\n\n"
                    + "• High Activity = heavy app usage\n"
                    + "• 💤 Rarely Used but active = you barely used it, but it shows recent/suspicious activity\n\n"
                    + "Tap an app for actions."
    );
    explain.setTextColor(0xFFAAAAAA);
    explain.setPadding(0, 0, 0, 28);
    root.addView(explain);

    if (!heavy.isEmpty()) {
        addSection(
                root,
                gr ? "🔥 High Activity" : "🔥 High Activity",
                gr ? "Εφαρμογές με πολύ υψηλή δραστηριότητα." : "Apps with very high activity.",
                0xFFFF5252
        );
        addDataRows(root, heavy);
    }

    if (!moderate.isEmpty()) {
        addSection(
                root,
                gr ? "⚠️ Moderate Activity" : "⚠️ Moderate Activity",
                gr ? "Εφαρμογές που αξίζουν έλεγχο." : "Apps worth reviewing.",
                0xFFFFC107
        );
        addDataRows(root, moderate);
    }

    Button okBtn = mkGreenBtn("OK");
    okBtn.setOnClickListener(v -> go(STEP_APPS));
    root.addView(okBtn);

    showCustomDialog(scroll);
}

// ============================================================
// DATA RISK MODEL (NO BYTES, SCORE ONLY)
// ============================================================
private static class DataRisk {
    final String pkg;
    final long score;          // behavioural index
    final long fgMinutes;      // foreground minutes in 48h
    final long hoursSinceUse;  // hours since last used
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
            + (gr ? "High Activity: " : "High Activity: ") + heavyCount + "\n"
            + (gr ? "Moderate Activity: " : "Moderate Activity: ") + moderateCount
    );

    tv.setTextColor(color);
    tv.setTextSize(15f);
    tv.setPadding(0, 10, 0, 22);

    root.addView(tv);

    TextView rec = new TextView(this);
    rec.setText(
            verdict.equals("HEAVY")
                    ? (gr
                    ? "Πρόταση: Έλεγξε background περιορισμούς και αφαίρεσε apps που δεν χρειάζεσαι."
                    : "Recommendation: Review background limits and uninstall apps you don’t need.")
                    : (gr
                    ? "Πρόταση: Έλεγξε αν κάποιες εφαρμογές συγχρονίζουν/τρέχουν χωρίς λόγο."
                    : "Recommendation: Check if apps sync/run unnecessarily.")
    );
    rec.setTextColor(0xFFAAAAAA);
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
                ? (gr ? "💤 Σπάνια χρήση αλλά ενεργή" : "💤 Rarely used but active")
                : (gr ? "High Activity" : "High Activity");

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
    try {
        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.setData(android.net.Uri.fromParts("package", pkg, null));
        startActivity(i);
    } catch (Throwable ignore) {
        try { startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS)); } catch (Throwable ignore2) {}
    }
}

private void uninstallPkg(String pkg) {
    try {
        Intent i = new Intent(Intent.ACTION_DELETE);
        i.setData(android.net.Uri.parse("package:" + pkg));
        startActivity(i);
    } catch (Throwable ignore) {
        openAppDetails(pkg);
    }
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

        if (stats == null || stats.isEmpty()) {
            showAppsStable();
            return;
        }

        PackageManager pm = getPackageManager();

        for (UsageStats u : stats) {

            String pkg = u.getPackageName();
            if (pkg == null) continue;
            if (pkg.equals(getPackageName())) continue;

            long minutes = u.getTotalTimeInForeground() / 60000;
            long lastUsed = u.getLastTimeUsed();
            long hoursSinceUse = (now - lastUsed) / (1000 * 60 * 60);

            int launches = 0;

            if (minutes < 1 && launches < 3) continue;

            try {

                ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
                boolean isSystem =
                        (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

                if (isSystem) continue;

                // -------------------------------
                // CLASSIFICATION LOGIC
                // -------------------------------
                String badge;
                int level;

                if (minutes >= 120 && hoursSinceUse <= 6) {
                    badge = "🟥 Always Active";
                    level = 3;
                }
                else if (minutes < 10 && launches >= 15) {
                    badge = "💤 Rarely used but wakes often";
                    level = 2;
                }
                else if (minutes >= 45) {
                    badge = "🟨 Background Risk";
                    level = 2;
                }
                else {
                    continue;
                }

                AppAppRisk r = new AppAppRisk(
                        pkg,
                        minutes,
                        hoursSinceUse,
                        launches,
                        badge
                );

                if (level >= 3)
                    heavy.add(r);
                else
                    moderate.add(r);

            } catch (Throwable ignore) {}
        }

    } catch (Throwable ignore) {}

    if (heavy.isEmpty() && moderate.isEmpty()) {
        showAppsStable();
        return;
    }

    ScrollView scroll = new ScrollView(this);

    LinearLayout root = buildBaseBox(
            gr ? "Apps Intelligence Report (48 ώρες)"
               : "Apps Intelligence Report (48 hours)"
    );

    scroll.addView(root);

    String verdict =
            !heavy.isEmpty() ? "HEAVY"
            : "MODERATE";

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
    next.setOnClickListener(v -> go(STEP_CACHE));
    root.addView(next);

    showCustomDialog(scroll);
}

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

        List<UsageStats> stats =
                usm.queryUsageStats(
                        UsageStatsManager.INTERVAL_DAILY,
                        threshold,
                        now
                );

        if (stats == null || stats.isEmpty()) {
            go(STEP_CACHE);
            return;
        }

        PackageManager pm = getPackageManager();

        for (UsageStats u : stats) {

            String pkg = u.getPackageName();
            if (pkg == null) continue;
            if (pkg.equals(getPackageName())) continue;

            long lastUsed = u.getLastTimeUsed();
            long days =
                    (now - lastUsed) / (1000L * 60 * 60 * 24);

            int launches = 0;
            
            if (days < 30) continue;
            if (launches > 5) continue;

            try {
                ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);

                boolean isSystem =
                        (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

                if (isSystem) continue;

                unused.add(new UnusedApp(pkg, days));

            } catch (Throwable ignore) {}
        }

    } catch (Throwable ignore) {}

    if (unused.isEmpty()) {
        go(STEP_CACHE);
        return;
    }

    ScrollView scroll = new ScrollView(this);

    LinearLayout root = buildBaseBox(
            gr ? "🕒 Εφαρμογές που δεν χρησιμοποιούνται"
               : "🕒 Unused Applications"
    );

    scroll.addView(root);

    TextView info = new TextView(this);
    info.setText(
            gr
                    ? "Εφαρμογές που δεν έχουν χρησιμοποιηθεί >30 ημέρες.\n"
                    + "Ενδέχεται να πιάνουν χώρο ή δικαιώματα."
                    : "Apps not used for over 30 days.\n"
                    + "They may occupy storage or hold permissions."
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
                (gr ? "Τελευταία χρήση: " : "Last used: ")
                + r.days
                + (gr ? " ημέρες πριν" : " days ago")
                + "\n💤 Inactive"
        );
        meta.setTextColor(0xFFFFC107);
        meta.setPadding(0, 6, 0, 10);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);

        Button uninstall = mkRedBtn(gr ? "Απεγκατάσταση" : "Uninstall");
        Button details = mkBlackGoldBtn(gr ? "Λεπτομέρειες" : "View Details");

        uninstall.setOnClickListener(v -> uninstallPkg(r.pkg));
        details.setOnClickListener(v -> openAppDetails(r.pkg));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
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

    Button next = mkGreenBtn("OK");
    next.setOnClickListener(v -> go(STEP_CACHE));
    root.addView(next);

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
    final long minutes;
    final long hoursSinceUse;
    final int launches;
    final String badge;

    AppAppRisk(String p, long m, long h, int l, String b) {
        pkg = p;
        minutes = m;
        hoursSinceUse = h;
        launches = l;
        badge = b;
    }
}

// ============================================================
// STABLE
// ============================================================

private void showAppsStable() {

    showDialog(
            progressTitle(gr ? "ΒΗΜΑ 4 — Εφαρμογές"
                             : "STEP 4 — Apps"),
            gr
                    ? "Engine Verdict: STABLE\n\n"
                    + "Δεν βρέθηκαν εφαρμογές με υπερβολική δραστηριότητα."
                    : "Engine Verdict: STABLE\n\n"
                    + "No apps with abnormal activity detected.",
            null,
            () -> go(STEP_CACHE),
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
            0xFFFFC107;

    tv.setText(
            "Engine Verdict: " + verdict + "\n\n"
            + (gr ? "High Activity: " : "High Activity: ") + heavy + "\n"
            + (gr ? "Moderate Activity: " : "Moderate Activity: ") + moderate
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

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0,14,0,14);

        TextView name = new TextView(this);
        name.setText("• " + label);
        name.setTextColor(Color.WHITE);
        name.setTypeface(null, Typeface.BOLD);

        TextView meta = new TextView(this);
        meta.setText(
        (gr ? "Χρήση: " : "Usage: ")
        + r.minutes
        + (gr ? " λεπτά (48h)" : " min (48h)")
        + "  |  "
        + (gr ? "Τελευταία χρήση: " : "Last used: ")
        + r.hoursSinceUse + "h"
        + "  |  "
        + (gr ? "Εκκινήσεις: " : "Launches: ")
        + r.launches
        + "\n"
        + r.badge
);
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
        btnRow.addView(uninstall);

        row.addView(name);
        row.addView(meta);
        row.addView(btnRow);

        root.addView(row);
    }
}

    // ============================================================
    // STEP 5 — CACHE
    // ============================================================

    private void showCache() {

        showDialog(
                progressTitle(gr ? "ΒΗΜΑ 5 — Cache" : "STEP 5 — Cache"),
                gr
                        ? "Θα ανοίξει η λίστα εφαρμογών ταξινομημένη κατά «Μεγαλύτερη % Cache».\n\n"
                        + "Καθάρισε εφαρμογές με μεγάλη προσωρινή μνήμη — ή και όλες.\n"
                        + "Στην πρώτη ομάδα θα δεις τις εφαρμογές που έχεις εγκαταστήσει.\n"
                        + "Στη δεύτερη ομάδα θα δεις τις εφαρμογές συστήματος.\n"
                        + "Η εκκαθάριση cache είναι ασφαλής και δεν διαγράφει προσωπικά δεδομένα.\n\n"
                        + "Απόφυγε την εκκαθάριση δεδομένων εκτός αν γνωρίζεις τις συνέπειες.\n\n"
                        + "Πάτησε OK όταν ολοκληρώσεις."
                        : "The app list will open sorted by “Largest Cache”.\n\n"
                        + "Clear apps with large temporary cache — or all of them if needed.\n"
                        + "In the first group you will see apps you have installed.\n"
                        + "In the second group you will see system applications.\n"
                        + "Clearing cache is safe and does not remove personal data.\n\n"
                        + "Avoid clearing app data unless you understand the consequences.\n\n"
                        + "Press OK when finished.",
                () -> {
                try {
                    Intent i = new Intent(this, AppListActivity.class);
                    i.putExtra("mode", "cache");
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(
                            this,
                            gr ? "Δεν ήταν δυνατό να ανοίξει ο καθαριστής cache."
                               : "Unable to open cache cleaner.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            },
            () -> go(STEP_QUEST),
            false
    );
}

    // ============================================================
    // QUESTIONNAIRE
    // ============================================================

    private void showQuestionnaire() {

        LinearLayout root = buildBaseBox(
                gr ? "Πρόσεξες τελευταία κάτι που σε προβλημάτισε στη συσκευή σου;"
   : "Have you noticed anything unusual on your device recently?"
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
        CheckBox wifi = mkCheck(gr?"WiFi αστάθεια":"WiFi instability");

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

        addActionButtons(root,
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
                },
                () -> go(STEP_LABS)
        );

        showCustomDialog(root);
    }

    // ============================================================
    // LAB RECOMMENDATION
    // ============================================================

    private void showLabRecommendation() {

        LinearLayout root = buildBaseBox(
                gr ? "Για να ελέγξεις όσα μας ανέφερες, σου προτείνουμε να τρέξεις τα παρακάτω διαγνωστικά Εργαστήρια"
   : "Based on what you reported, we recommend running the following diagnostic Labs"
        );

        TextView tv = new TextView(this);
        tv.setText(buildTechnicalRecommendationText(symptoms));
        tv.setTextColor(0xFF00FF7F);
        tv.setPadding(0,20,0,20);

        root.addView(tv);

        addActionButtons(root,
                () -> startActivity(new Intent(this, ManualTestsActivity.class)),
                () -> go(STEP_REMINDER)
        );

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
            gr ? "Αν έμεινες ευχαριστημένος/η από το αποτέλεσμα, θα ήθελες να σου υπενθυμίζουμε τακτικά να κάνουμε την ίδια επιθεώρηση στη συσκευή σου;"
               : "If you're satisfied with the results, would you like regular reminders to run the same device inspection?"
    );

    Button daily = mkGreenBtn(gr ? "1 Ημέρα" : "Daily");
    Button weekly = mkGreenBtn(gr ? "1 Εβδομάδα" : "Weekly");
    Button monthly = mkGreenBtn(gr ? "1 Μήνας" : "Monthly");
    Button skip = mkRedBtn(gr ? "Παράλειψη" : "Skip");

    daily.setOnClickListener(v -> {
    OptimizerScheduler.enableReminder(this,1);
    go(STEP_FINAL);
});

weekly.setOnClickListener(v -> {
    OptimizerScheduler.enableReminder(this,7);
    go(STEP_FINAL);
});

monthly.setOnClickListener(v -> {
    OptimizerScheduler.enableReminder(this,30);
    go(STEP_FINAL);
});

skip.setOnClickListener(v -> go(STEP_FINAL));

    root.addView(daily);
    root.addView(weekly);
    root.addView(monthly);
    root.addView(skip);

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
    // DIALOG ENGINE
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
        tvBody.setPadding(0,20,0,20);
        root.addView(tvBody);

        if (settingsAction != null) {
            Button settingsBtn = mkBlackGoldBtn(gr?"Ρυθμίσεις":"Settings");
            settingsBtn.setOnClickListener(v -> settingsAction.run());
            root.addView(settingsBtn);
        }

        Button okBtn = mkGreenBtn(isIntro ? (gr?"Έναρξη":"Start") : "OK");
        okBtn.setOnClickListener(v -> okAction.run());
        root.addView(okBtn);

        Button exitBtn = mkRedBtn(gr?"Έξοδος":"Exit");
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

    private LinearLayout buildBaseBox(String titleText) {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40,40,40,40);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF000000);
        bg.setCornerRadius(30);
        bg.setStroke(5,0xFFFFD700);
        root.setBackground(bg);

        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextColor(Color.WHITE);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextSize(18f);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0,0,0,30);

        root.addView(title);
        return root;
    }

    private void showCustomDialog(View v) {
        AlertDialog d = new AlertDialog.Builder(this)
                .setView(v)
                .setCancelable(false)
                .create();

        if (d.getWindow()!=null)
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        d.show();
    }

    private void addActionButtons(LinearLayout root, Runnable ok, Runnable skip) {

        Button okBtn = mkGreenBtn("OK");
        Button skipBtn = mkRedBtn(gr?"Παράλειψη":"Skip");

        okBtn.setOnClickListener(v -> ok.run());
        skipBtn.setOnClickListener(v -> skip.run());

        root.addView(okBtn);
        root.addView(skipBtn);
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

    private String progressTitle(String title) {
        int total = 5;
        int current = step;
        return title + " (" + current + "/" + total + ")";
    }
}
