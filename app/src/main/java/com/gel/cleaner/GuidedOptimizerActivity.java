// GDiolitsis Engine Lab (GEL)
// GuidedOptimizerActivity — FINAL COMPLETE PUBLIC VERSION

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public final class GuidedOptimizerActivity extends AppCompatActivity {

    private boolean gr;
    private int step = 0;

    private static final int STEP_INTRO    = 0;
    private static final int STEP_STORAGE  = 1;
    private static final int STEP_BATTERY  = 2;
    private static final int STEP_DATA     = 3;
    private static final int STEP_APPS     = 4;
    private static final int STEP_CACHE    = 5;
    private static final int STEP_QUEST    = 6;
    private static final int STEP_LABS     = 7;
    private static final int STEP_REMINDER = 8;
    private static final int STEP_DONE     = 9;

    private final ArrayList<String> symptoms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gr = AppLang.isGreek(this);
        go(STEP_INTRO);
    }

    // ============================================================
    // ROUTER
    // ============================================================

    private void go(int s) {
        step = s;

        switch (step) {

            case STEP_INTRO:
                showIntro();
                break;

            case STEP_STORAGE:
                showStorage();
                break;

            case STEP_BATTERY:
                showBattery();
                break;

            case STEP_DATA:
                showData();
                break;

            case STEP_APPS:
                showApps();
                break;

            case STEP_CACHE:
                showCache();
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
                progressTitle(gr ? "ΒΗΜΑ 1 — Αποθήκευση"
                                 : "STEP 1 — Storage"),
                gr
                        ? "Θα ανοίξουν οι ρυθμίσεις αποθήκευσης της συσκευής.\n\n"
                        + "Χρησιμοποίησε τα διαθέσιμα εργαλεία καθαρισμού όπου χρειάζεται.\n"
                        + "Συνήθως αρκεί η εκκαθάριση προσωρινής μνήμης (cache), προσωρινών δεδομένων και κατάλοιπων αρχείων.\n"
                        + "Αυτές οι ενέργειες είναι ασφαλείς και δεν διαγράφουν προσωπικά δεδομένα.\n\n"
                        + "ΠΡΟΣΟΧΗ: Η εκκαθάριση δεδομένων εφαρμογής διαγράφει ρυθμίσεις, αποθηκευμένους λογαριασμούς και offline περιεχόμενο.\n"
                        + "Χρησιμοποίησέ την μόνο αν γνωρίζεις ακριβώς τι κάνεις.\n\n"
                        + "Σε ορισμένες συσκευές η εφαρμογή μπορεί να κλείσει προσωρινά.\n\n"
                        + "Μετά τον καθαρισμό, άνοιξε ξανά την εφαρμογή\n"
                        + "και πάτησε «OK» για να συνεχίσουμε."
                        : "The device storage settings will open.\n\n"
                        + "Use the available cleaning tools where necessary.\n"
                        + "In most cases, clearing temporary cache, temporary data and residual files is sufficient.\n"
                        + "These actions are safe and do not remove personal data.\n\n"
                        + "WARNING: Clearing app data removes settings, saved accounts and offline content.\n"
                        + "Use it only if you fully understand the consequences.\n\n"
                        + "On some devices the app may close temporarily.\n\n"
                        + "After cleaning, reopen the app\n"
                        + "and press “OK” to continue.",
                this::openStorageSettings,
                () -> go(STEP_BATTERY),
                false
        );
    }

    // ============================================================
    // STEP 2 — BATTERY
    // ============================================================

    private void showBattery() {

        showDialog(
                progressTitle(gr ? "ΒΗΜΑ 2 — Μπαταρία"
                                 : "STEP 2 — Battery"),
                gr
                        ? "Θα ανοίξουν οι ρυθμίσεις μπαταρίας.\n\n"
                        + "Έλεγξε:\n"
                        + "• Ποιες εφαρμογές καταναλώνουν ασυνήθιστα υψηλή ενέργεια\n"
                        + "• Αν κάποια εφαρμογή λειτουργεί συνεχώς στο παρασκήνιο\n"
                        + "• Αν χρειάζεται περιορισμός δραστηριότητας σε μη απαραίτητες εφαρμογές\n\n"
                        + "Απόφυγε την απενεργοποίηση βασικών εφαρμογών συστήματος.\n\n"
                        + "Πάτησε «OK» όταν επιστρέψεις."
                        : "Battery settings will open.\n\n"
                        + "Check:\n"
                        + "• Apps with unusually high power consumption\n"
                        + "• Apps constantly running in the background\n"
                        + "• Whether non-essential apps need activity restrictions\n\n"
                        + "Avoid disabling core system applications.\n\n"
                        + "Press “OK” when you return.",
                this::openBatteryUsage,
                () -> go(STEP_DATA),
                false
        );
    }

    // ============================================================
    // STEP 3 — DATA
    // ============================================================

    private void showData() {

        showDialog(
                progressTitle(gr ? "ΒΗΜΑ 3 — Δεδομένα"
                                 : "STEP 3 — Data Usage"),
                gr
                        ? "Θα ανοίξουν οι ρυθμίσεις χρήσης δεδομένων.\n\n"
                        + "Έλεγξε:\n"
                        + "• Ποιες εφαρμογές καταναλώνουν ασυνήθιστα πολλά δεδομένα\n"
                        + "• Αν υπάρχει υπερβολική χρήση στο παρασκήνιο\n"
                        + "• Αν χρειάζεται περιορισμός δεδομένων σε μη απαραίτητες εφαρμογές\n\n"
                        + "Απέφυγε τον περιορισμό βασικών εφαρμογών συστήματος ή υπηρεσιών ασφαλείας.\n\n"
                        + "Πάτησε «OK» όταν επιστρέψεις."
                        : "Data usage settings will open.\n\n"
                        + "Check:\n"
                        + "• Apps with unusually high data consumption\n"
                        + "• Excessive background data usage\n"
                        + "• Whether non-essential apps need data restrictions\n\n"
                        + "Avoid restricting core system apps or security services.\n\n"
                        + "Press “OK” when you return.",
                this::openDataUsage,
                () -> go(STEP_APPS),
                false
        );
    }

    // ============================================================
    // STEP 4 — APPS
    // ============================================================

    private void showApps() {

        showDialog(
                progressTitle(gr ? "ΒΗΜΑ 4 — Εφαρμογές"
                                 : "STEP 4 — Apps"),
                gr
                        ? "Θα ανοίξουν οι ρυθμίσεις εφαρμογών.\n\n"
                        + "Έλεγξε:\n"
                        + "• Ποιες εφαρμογές εκτελούνται συχνά στο παρασκήνιο\n"
                        + "• Τα δικαιώματα που δεν είναι απαραίτητα (κάμερα, μικρόφωνο, τοποθεσία κ.λπ.)\n"
                        + "• Ειδοποιήσεις που ενεργοποιούν συνεχώς τη συσκευή\n\n"
                        + "Απέφυγε την απενεργοποίηση βασικών εφαρμογών συστήματος.\n\n"
                        + "Πάτησε «OK» όταν επιστρέψεις."
                        : "App settings will open.\n\n"
                        + "Check:\n"
                        + "• Apps frequently running in the background\n"
                        + "• Unnecessary permissions (camera, microphone, location, etc.)\n"
                        + "• Notifications that constantly wake the device\n\n"
                        + "Avoid disabling essential system applications.\n\n"
                        + "Press “OK” when you return.",
                () -> open(Settings.ACTION_APPLICATION_SETTINGS),
                () -> go(STEP_CACHE),
                false
        );
    }

    // ============================================================
    // STEP 5 — CACHE
    // ============================================================

    private void showCache() {

        showDialog(
                progressTitle(gr ? "ΒΗΜΑ 5 — Cache"
                                 : "STEP 5 — Cache"),
                gr
                        ? "Θα ανοίξει η λίστα εφαρμογών ταξινομημένη κατά «Μεγαλύτερη Cache».\n\n"
                        + "Καθάρισε εφαρμογές με μεγάλη προσωρινή μνήμη — ή και όλες.\n"
                        + "Στην πρώτη ομάδα θα δεις τις εφαρμογές που έχεις εγκαταστήσει.\n"
                        + "Στη δεύτερη ομάδα θα δεις τις εφαρμογές συστήματος.\n"
                        + "Η εκκαθάριση cache είναι ασφαλής και δεν διαγράφει προσωπικά δεδομένα.\n\n"
                        + "Απόφυγε την εκκαθάριση δεδομένων εκτός αν γνωρίζεις τις συνέπειες.\n\n"
                        + "Πάτησε «OK» όταν ολοκληρώσεις."
                        : "The app list will open sorted by “Largest Cache”.\n\n"
                        + "Clear apps with large temporary cache — or all of them if needed.\n"
                        + "In the first group you will see apps you have installed.\n"
                        + "In the second group you will see system applications.\n"
                        + "Clearing cache is safe and does not remove personal data.\n\n"
                        + "Avoid clearing app data unless you understand the consequences.\n\n"
                        + "Press “OK” when finished.",
                this::openLargestCache,
                () -> go(STEP_QUEST),
                false
        );
    }

    // ============================================================
    // QUESTIONNAIRE
    // ============================================================

    private void showQuestionnaire() {

        LinearLayout root = buildBaseBox(
                progressTitle(gr ? "Δήλωσε ό,τι παρατηρείς"
                                 : "Select observed issues")
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
                progressTitle(gr ? "Προτεινόμενα Εργαστήρια"
                                 : "Recommended Labs")
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
            labs.add(gr ? "LAB 16 — Θερμικός έλεγχος"
                        : "LAB 16 — Thermal diagnostics");
            labs.add(gr ? "LAB 14 — Έλεγχος μπαταρίας"
                        : "LAB 14 — Battery health analysis");
        }

        if (s.contains("charge")) {
            labs.add(gr ? "LAB 15 — Έλεγχος φόρτισης"
                        : "LAB 15 — Charging diagnostics");
            labs.add(gr ? "LAB 14 — Έλεγχος μπαταρίας"
                        : "LAB 14 — Battery health analysis");
        }

        if (s.contains("lag")) {
            labs.add(gr ? "LAB 19 — Απόδοση συστήματος"
                        : "LAB 19 — System performance analysis");
            labs.add(gr ? "LAB 26 — Ανάλυση επιπτώσεων εφαρμογών"
                        : "LAB 26 — Installed apps impact analysis");
        }

        if (s.contains("crash")) {
            labs.add(gr ? "LAB 25 — Ανάλυση κρασαρισμάτων"
                        : "LAB 25 — Crash intelligence analysis");
            labs.add(gr ? "LAB 30 — Τελική τεχνική αναφορά"
                        : "LAB 30 — Final technical report");
        }

        if (s.contains("data") || s.contains("wifi")) {
            labs.add(gr ? "LAB 26 — Δίκτυο & background χρήση"
                        : "LAB 26 — Network & background activity analysis");
        }

        if (s.contains("camera")) {
            labs.add(gr ? "LAB 8 — Διαγνωστικός έλεγχος κάμερας"
                        : "LAB 8 — Camera diagnostics");
        }

        if (s.contains("bluetooth")) {
            labs.add(gr ? "LAB 5 — Έλεγχος Bluetooth"
                        : "LAB 5 — Bluetooth diagnostics");
        }

        if (s.contains("sound")) {
            labs.add(gr ? "LAB 1–4 — Διαγνωστικά ήχου"
                        : "LAB 1–4 — Audio diagnostics");
        }

        if (s.contains("boot")) {
            labs.add(gr ? "LAB 19 — Εκκίνηση & Απόδοση"
                        : "LAB 19 — Boot & performance analysis");
        }

        labs.add(gr ? "LAB 29 — Τελική σύνοψη υγείας"
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
                progressTitle(gr ? "Θέλεις υπενθύμιση;"
                                 : "Enable reminder?")
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

        showCustomDialog(root);
    }

    // ============================================================
    // SETTINGS FALLBACKS
    // ============================================================

    private void openStorageSettings() {
        try { startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)); return; } catch (Throwable ignore) {}
        try { startActivity(new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS)); return; } catch (Throwable ignore) {}
        open(Settings.ACTION_SETTINGS);
    }

    private void openBatteryUsage() {
        try { startActivity(new Intent(Settings.ACTION_BATTERY_USAGE_SETTINGS)); return; } catch (Throwable ignore) {}
        try { startActivity(new Intent(Settings.ACTION_POWER_USAGE_SUMMARY)); return; } catch (Throwable ignore) {}
        open(Settings.ACTION_SETTINGS);
    }

    private void openDataUsage() {
        try { startActivity(new Intent(Settings.ACTION_DATA_USAGE_SETTINGS)); return; } catch (Throwable ignore) {}
        try { startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)); return; } catch (Throwable ignore) {}
        open(Settings.ACTION_SETTINGS);
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
        tvBody.setTextColor(Color.WHITE);
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
