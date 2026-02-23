// GDiolitsis Engine Lab (GEL) — Author & Developer
// GuidedOptimizerActivity — FINAL COMPLETE OPTIMIZER FLOW

package com.gel.cleaner;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public final class GuidedOptimizerActivity extends AppCompatActivity {

    private boolean gr;
    private int step = 0;

    private static final int STEP_STORAGE  = 0;
    private static final int STEP_BATTERY  = 1;
    private static final int STEP_DATA     = 2;
    private static final int STEP_APPS     = 3;
    private static final int STEP_CACHE    = 4;
    private static final int STEP_QUEST    = 5;
    private static final int STEP_LABS     = 6;
    private static final int STEP_REMINDER = 7;
    private static final int STEP_DONE     = 8;

    private final ArrayList<String> symptoms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gr = AppLang.isGreek(this);
        go(STEP_STORAGE);
    }

    private void go(int s) {
        step = s;

        switch (step) {

            case STEP_STORAGE:
                showStepDialog(
                        gr ? "ΒΗΜΑ 1 — Αποθήκευση" : "STEP 1 — Storage",
                        gr
                                ? "Έλεγχος αποθήκευσης.\n\n"
                                  + "Διέγραψε προσωρινά ή περιττά αρχεία.\n"
                                  + "Πάτα OK όταν επιστρέψεις."
                                : "Check storage.\n\n"
                                  + "Remove temporary or unnecessary files.\n"
                                  + "Press OK when back.",
                        () -> open(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)
                );
                break;

            case STEP_BATTERY:
                showStepDialog(
                        gr ? "ΒΗΜΑ 2 — Μπαταρία" : "STEP 2 — Battery",
                        gr
                                ? "Έλεγχος κατανάλωσης μπαταρίας.\n"
                                  + "Δες ποιες εφαρμογές καταναλώνουν πολύ."
                                : "Check battery usage.\n"
                                  + "Review high usage apps.",
                        () -> open(Settings.ACTION_BATTERY_SAVER_SETTINGS)
                );
                break;

            case STEP_DATA:
                showStepDialog(
                        gr ? "ΒΗΜΑ 3 — Δεδομένα" : "STEP 3 — Data Usage",
                        gr
                                ? "Έλεγχος χρήσης δεδομένων."
                                : "Check data usage.",
                        () -> open(Settings.ACTION_DATA_USAGE_SETTINGS)
                );
                break;

            case STEP_APPS:
                showStepDialog(
                        gr ? "ΒΗΜΑ 4 — Εφαρμογές" : "STEP 4 — Apps",
                        gr
                                ? "Έλεγχος δικαιωμάτων και background apps."
                                : "Check permissions and background apps.",
                        () -> open(Settings.ACTION_APPLICATION_SETTINGS)
                );
                break;

            case STEP_CACHE:
                showStepDialog(
                        gr ? "ΒΗΜΑ 5 — Cache" : "STEP 5 — Cache",
                        gr
                                ? "Θα ανοίξει η λίστα εφαρμογών\n"
                                  + "με ταξινόμηση Μεγαλύτερη Cache."
                                : "App list will open sorted by Largest Cache.",
                        this::openLargestCache
                );
                break;

            case STEP_QUEST:
                showQuestionnaire();
                break;

            case STEP_LABS:
                showLabRecommendation();
                break;

            case STEP_REMINDER:
                showReminder();
                break;

            case STEP_DONE:
                finish();
                break;
        }
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
    // QUESTIONNAIRE
    // ============================================================

    private void showQuestionnaire() {

        LinearLayout root = buildBaseBox(
                gr ? "Δήλωσε ό,τι παρατηρείς" : "Select observed issues"
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

        showDialog(root);
    }

    // ============================================================
    // LAB RECOMMENDATION
    // ============================================================

    private void showLabRecommendation() {

        LinearLayout root = buildBaseBox(
                gr ? "Προτεινόμενα Εργαστήρια" : "Recommended Labs"
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

        showDialog(root);
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
                gr ? "Θέλεις υπενθύμιση;" : "Enable reminder?"
        );

        Button daily = mkGreenBtn(gr?"1 Ημέρα":"Daily");
        Button weekly = mkGreenBtn(gr?"1 Εβδομάδα":"Weekly");
        Button monthly = mkRedBtn(gr?"1 Μήνας":"Monthly");

        daily.setOnClickListener(v -> { OptimizerScheduler.enableReminder(this,1); finish(); });
        weekly.setOnClickListener(v -> { OptimizerScheduler.enableReminder(this,7); finish(); });
        monthly.setOnClickListener(v -> { OptimizerScheduler.enableReminder(this,30); finish(); });

        root.addView(daily);
        root.addView(weekly);
        root.addView(monthly);

        showDialog(root);
    }

    // ============================================================
    // GEL POPUP STYLE
    // ============================================================

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

    private CheckBox mkCheck(String t) {
        CheckBox c = new CheckBox(this);
        c.setText(t);
        c.setTextColor(Color.WHITE);
        return c;
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

    private void showDialog(View v) {
    AlertDialog d = new AlertDialog.Builder(this)
            .setView(v)
            .setCancelable(false)
            .create();

    if (d.getWindow()!=null)
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    d.show();
}

// 👇 ΒΑΖΕΙΣ ΕΔΩ ΤΗ ΜΕΘΟΔΟ
private void showStepDialog(String titleText, String bodyText, Runnable openAction) {

    LinearLayout root = buildBaseBox(titleText);

    TextView body = new TextView(this);
    body.setText(bodyText);
    body.setTextColor(Color.WHITE);
    body.setPadding(0,20,0,20);
    root.addView(body);

    Button openBtn = mkGreenBtn(gr ? "Άνοιγμα" : "Open");
    Button okBtn = mkRedBtn("OK");

    openBtn.setOnClickListener(v -> openAction.run());
    okBtn.setOnClickListener(v -> go(step + 1));

    root.addView(openBtn);
    root.addView(okBtn);

    showDialog(root);
}
}
