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
    
    private static final String PREF_ROUTING_DIALOG = "routing_dialog_hidden_";

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

private void safeStartActivity(String featureName, String... actions) {

    for (String action : actions) {
        try {
            startActivity(new Intent(action));
            return;
        } catch (Throwable ignore) {}
    }

    // Αν έχει απενεργοποιηθεί για αυτό το feature → άνοιξε κατευθείαν settings
    if (isRoutingDialogHidden(featureName)) {
        open(Settings.ACTION_SETTINGS);
        return;
    }

    showRoutingInfoDialog(featureName);
}

private void showRoutingInfoDialog(String featureName) {

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(40,40,40,40);

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);
    bg.setCornerRadius(30);
    bg.setStroke(5,0xFFFFD700);
    root.setBackground(bg);

    TextView title = new TextView(this);
    title.setText(gr ? "Διαφορετική Δομή Ρυθμίσεων"
                     : "Different Settings Structure");
    title.setTextColor(Color.WHITE);
    title.setTypeface(null, Typeface.BOLD);
    title.setTextSize(18f);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0,0,0,30);

    TextView body = new TextView(this);
body.setText(
        gr
                ? "Οι κατασκευαστές Android (Samsung, Xiaomi, Huawei κ.λπ.)\n"
                  + "τροποποιούν συχνά τη δομή των ρυθμίσεων.\n\n"
                  + "Αυτό σημαίνει ότι το ίδιο μενού μπορεί να βρίσκεται\n"
                  + "σε διαφορετική τοποθεσία ανάλογα με τη συσκευή ή την έκδοση Android.\n\n"
                  + "Η εφαρμογή προσπαθεί αυτόματα να εντοπίσει\n"
                  + "το πιο σχετικό μενού για τη λειτουργία που επέλεξες.\n\n"
                  + "Αν δεν είναι διαθέσιμο, ανοίγουμε τις γενικές ρυθμίσεις\n"
                  + "ώστε να έχεις πάντα πρόσβαση."
                : "Android manufacturers (Samsung, Xiaomi, Huawei, etc.)\n"
                  + "often modify the internal structure of system settings.\n\n"
                  + "This means the same menu may appear in different locations\n"
                  + "depending on the device or Android version.\n\n"
                  + "The app automatically attempts to locate\n"
                  + "the most relevant menu for the feature you selected.\n\n"
                  + "If a direct path is not available,\n"
                  + "general settings are opened to ensure access."
);
    body.setTextColor(0xFF00FF7F);
    body.setPadding(0,20,0,20);

    CheckBox dontShow = new CheckBox(this);
    dontShow.setText(gr ? "Να μην εμφανιστεί ξανά"
                        : "Do not show again");
    dontShow.setTextColor(Color.WHITE);

    Button ok = new Button(this);
    ok.setText("OK");
    ok.setTextColor(Color.WHITE);

    GradientDrawable d = new GradientDrawable();
    d.setColor(0xFF00C853);
    d.setStroke(5,0xFFFFD700);
    d.setCornerRadius(25);
    ok.setBackground(d);

    ok.setOnClickListener(v -> {

        if (dontShow.isChecked()) {
            setRoutingDialogHidden(featureName, true);
        }

        try {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        } catch (Throwable ignore) {}
    });

    root.addView(title);
    root.addView(body);
    root.addView(dontShow);
    root.addView(ok);

    AlertDialog dialog = new AlertDialog.Builder(this)
            .setView(root)
            .setCancelable(false)
            .create();

    if (dialog.getWindow()!=null)
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

    dialog.show();
}

private boolean isRoutingDialogHidden(String featureName) {
    return getSharedPreferences("gel_prefs", MODE_PRIVATE)
            .getBoolean(PREF_ROUTING_DIALOG + featureName, false);
}

private void setRoutingDialogHidden(String featureName, boolean value) {
    getSharedPreferences("gel_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean(PREF_ROUTING_DIALOG + featureName, value)
            .apply();
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
// STEP 2 — BATTERY (SUB-FLOW)
// ============================================================

private void showBattery() {
    batterySubStep = 0;
    showBatteryFlow();
}

private void showBatteryFlow() {

    final String title =
            progressTitle(gr ? "ΒΗΜΑ 2 — Μπαταρία"
                             : "STEP 2 — Battery");

    final String body;

    switch (batterySubStep) {

        case 0:
            body = gr
                    ? "Υπο-Βήμα 1/3\n\n"
                    + "Θα ανοίξουν οι ρυθμίσεις χρήσης μπαταρίας.\n\n"
                    + "Έλεγξε:\n"
                    + "• Ποιες εφαρμογές καταναλώνουν ασυνήθιστα υψηλή ενέργεια\n"
                    + "• Αν κάποια εφαρμογή εμφανίζεται συνεχώς ενεργή\n\n"
                    + "Πάτησε «Ρυθμίσεις» και μετά επέστρεψε για «OK»."
                    : "Sub-Step 1/3\n\n"
                    + "Battery usage settings will open.\n\n"
                    + "Check:\n"
                    + "• Apps with unusually high power consumption\n"
                    + "• Apps constantly active\n\n"
                    + "Press “Settings”, then return and press “OK”.";
            break;

        case 1:
            body = gr
                    ? "Υπο-Βήμα 2/3\n\n"
                    + "Θα ανοίξουμε τις ρυθμίσεις εξοικονόμησης ενέργειας.\n\n"
                    + "Έλεγξε:\n"
                    + "• Αν υπάρχει λειτουργία εξοικονόμησης ενεργή\n"
                    + "• Αν χρειάζεται περιορισμός background δραστηριότητας\n\n"
                    + "Πάτησε «Ρυθμίσεις», μετά «OK»."
                    : "Sub-Step 2/3\n\n"
                    + "Battery saver settings will open.\n\n"
                    + "Check:\n"
                    + "• Whether battery saver is active\n"
                    + "• Background activity restrictions\n\n"
                    + "Press “Settings”, then “OK”.";
            break;

        default:
            body = gr
                    ? "Υπο-Βήμα 3/3\n\n"
                    + "Τελικός έλεγχος: Βελτιστοποίηση μπαταρίας εφαρμογών.\n\n"
                    + "Έλεγξε ποιες εφαρμογές είναι «Χωρίς περιορισμό».\n"
                    + "Απόφυγε αλλαγές σε βασικές εφαρμογές συστήματος.\n\n"
                    + "Πάτησε «Ρυθμίσεις», μετά «OK» για να συνεχίσουμε."
                    : "Sub-Step 3/3\n\n"
                    + "Final check: App battery optimization.\n\n"
                    + "Review apps marked as “Unrestricted”.\n"
                    + "Avoid modifying core system apps.\n\n"
                    + "Press “Settings”, then “OK” to continue.";
            break;
    }

    showDialog(
            title,
            body,
            this::openBatterySubStep,
            this::advanceBatterySubStep,
            false
    );
}

private void advanceBatterySubStep() {
    batterySubStep++;
    if (batterySubStep < 3) showBatteryFlow();
    else go(STEP_DATA);
}

private void openBatterySubStep() {

    switch (batterySubStep) {

        case 0:
            safeStartActivity("battery_usage",
                    "android.settings.BATTERY_USAGE_SETTINGS",
                    Settings.ACTION_SETTINGS
            );
            break;

        case 1:
            safeStartActivity("battery_saver",
                    Settings.ACTION_BATTERY_SAVER_SETTINGS,
                    Settings.ACTION_SETTINGS
            );
            break;

        default:
            safeStartActivity("battery_optimization",
                    "android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS",
                    Settings.ACTION_SETTINGS
            );
            break;
    }
}

    // ============================================================
// STEP 3 — DATA (SUB-FLOW)
// ============================================================

private void showData() {
    dataSubStep = 0;
    showDataFlow();
}

private void showDataFlow() {

    final String title = progressTitle(gr ? "ΒΗΜΑ 3 — Δεδομένα" : "STEP 3 — Data Usage");

    final String body;
    switch (dataSubStep) {

        case 0:
            body = gr
                    ? "Υπο-Βήμα 1/3\n\n"
                    + "Θα ανοίξουν οι ρυθμίσεις χρήσης δεδομένων.\n\n"
                    + "Έλεγξε:\n"
                    + "• Ποιες εφαρμογές καταναλώνουν ασυνήθιστα πολλά δεδομένα\n"
                    + "• Αν υπάρχει υπερβολική χρήση στο παρασκήνιο\n\n"
                    + "Πάτησε «Ρυθμίσεις» για να ανοίξουμε το μενού.\n"
                    + "Μετά επέστρεψε και πάτα «OK»."
                    : "Sub-Step 1/3\n\n"
                    + "Data usage settings will open.\n\n"
                    + "Check:\n"
                    + "• Apps with unusually high data consumption\n"
                    + "• Excessive background data usage\n\n"
                    + "Press “Settings” to open the menu.\n"
                    + "Then come back and press “OK”.";
            break;

        case 1:
            body = gr
                    ? "Υπο-Βήμα 2/3\n\n"
                    + "Θα ανοίξουμε εναλλακτικό μενού δικτύου (διαφέρει ανά ROM).\n\n"
                    + "Έλεγξε:\n"
                    + "• Περιορισμούς δεδομένων ανά εφαρμογή\n"
                    + "• Background data\n\n"
                    + "Πάτησε «Ρυθμίσεις» και μετά «OK»."
                    : "Sub-Step 2/3\n\n"
                    + "We will open an alternative network menu (varies by ROM).\n\n"
                    + "Check:\n"
                    + "• Per-app data restrictions\n"
                    + "• Background data\n\n"
                    + "Press “Settings”, then “OK”.";
            break;

        default:
            body = gr
                    ? "Υπο-Βήμα 3/3\n\n"
                    + "Τελευταίο πέρασμα: γενικές ασύρματες ρυθμίσεις.\n\n"
                    + "Απέφυγε τον περιορισμό βασικών υπηρεσιών συστήματος ή ασφάλειας.\n\n"
                    + "Πάτησε «Ρυθμίσεις» και μετά «OK» για να συνεχίσουμε."
                    : "Sub-Step 3/3\n\n"
                    + "Final pass: general wireless settings.\n\n"
                    + "Avoid restricting core system or security services.\n\n"
                    + "Press “Settings”, then “OK” to continue.";
            break;
    }

    showDialog(
            title,
            body,
            this::openDataSubStep,
            this::advanceDataSubStep,
            false
    );
}

private void advanceDataSubStep() {
    dataSubStep++;
    if (dataSubStep < 3) showDataFlow();
    else go(STEP_APPS);
}

private void openDataSubStep() {
    switch (dataSubStep) {

        case 0:
            safeStartActivity("data_usage",
                    Settings.ACTION_DATA_USAGE_SETTINGS,
                    "android.settings.DATA_USAGE_SETTINGS",
                    Settings.ACTION_WIRELESS_SETTINGS,
                    Settings.ACTION_SETTINGS
            );
            break;

        case 1:
            safeStartActivity("network_operator",
                    "android.settings.NETWORK_OPERATOR_SETTINGS",
                    Settings.ACTION_WIRELESS_SETTINGS,
                    Settings.ACTION_SETTINGS
            );
            break;

        default:
            safeStartActivity("wireless",
                    Settings.ACTION_WIRELESS_SETTINGS,
                    Settings.ACTION_SETTINGS
            );
            break;
    }
}

    // ============================================================
// STEP 4 — APPS (SUB-FLOW)
// ============================================================

private void showApps() {
    appsSubStep = 0;
    showAppsFlow();
}

private void showAppsFlow() {

    final String title = progressTitle(gr ? "ΒΗΜΑ 4 — Εφαρμογές" : "STEP 4 — Apps");

    final String body;
    switch (appsSubStep) {

        case 0:
            body = gr
                    ? "Υπο-Βήμα 1/3\n\n"
                    + "Θα ανοίξουν οι ρυθμίσεις εφαρμογών.\n\n"
                    + "Έλεγξε:\n"
                    + "• Ποιες εφαρμογές τρέχουν συχνά στο παρασκήνιο\n"
                    + "• Ποιες δεν χρησιμοποιείς\n\n"
                    + "Πάτησε «Ρυθμίσεις», μετά επέστρεψε και πάτα «OK»."
                    : "Sub-Step 1/3\n\n"
                    + "App settings will open.\n\n"
                    + "Check:\n"
                    + "• Apps frequently running in the background\n"
                    + "• Apps you rarely use\n\n"
                    + "Press “Settings”, then come back and press “OK”.";
            break;

        case 1:
            body = gr
                    ? "Υπο-Βήμα 2/3\n\n"
                    + "Θα ανοίξουμε ρυθμίσεις βελτιστοποίησης μπαταρίας.\n\n"
                    + "Έλεγξε εφαρμογές που είναι «χωρίς περιορισμό» στο παρασκήνιο.\n"
                    + "Μην πειράξεις βασικές εφαρμογές συστήματος.\n\n"
                    + "Πάτησε «Ρυθμίσεις», μετά «OK»."
                    : "Sub-Step 2/3\n\n"
                    + "Battery optimization settings will open.\n\n"
                    + "Check apps that are “unrestricted” in the background.\n"
                    + "Avoid changing core system apps.\n\n"
                    + "Press “Settings”, then “OK”.";
            break;

        default:
            body = gr
                    ? "Υπο-Βήμα 3/3\n\n"
                    + "Τελικός έλεγχος: δικαιώματα & ειδοποιήσεις.\n\n"
                    + "Δες αν κάποια εφαρμογή έχει άδειες που δεν χρειάζεται\n"
                    + "(κάμερα, μικρόφωνο, τοποθεσία) ή σε «ξυπνάει» με ειδοποιήσεις.\n\n"
                    + "Πάτησε «Ρυθμίσεις», μετά «OK» για να συνεχίσουμε."
                    : "Sub-Step 3/3\n\n"
                    + "Final check: permissions & notifications.\n\n"
                    + "Review apps with unnecessary permissions\n"
                    + "(camera, microphone, location) or noisy notifications.\n\n"
                    + "Press “Settings”, then “OK” to continue.";
            break;
    }

    showDialog(
            title,
            body,
            this::openAppsSubStep,
            this::advanceAppsSubStep,
            false
    );
}

private void advanceAppsSubStep() {
    appsSubStep++;
    if (appsSubStep < 3) showAppsFlow();
    else go(STEP_CACHE);
}

private void openAppsSubStep() {
    switch (appsSubStep) {

        case 0:
            safeStartActivity("apps_list",
                    Settings.ACTION_APPLICATION_SETTINGS,
                    "android.settings.MANAGE_APPLICATIONS_SETTINGS",
                    Settings.ACTION_SETTINGS
            );
            break;

        case 1:
            safeStartActivity("battery_optimizations",
                    "android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS",
                    Settings.ACTION_BATTERY_SAVER_SETTINGS,
                    Settings.ACTION_SETTINGS
            );
            break;

        default:
            // Δεν μπορούμε να ανοίξουμε permissions για συγκεκριμένο app χωρίς package,
            // οπότε πάμε σε app settings και ο χρήστης διαλέγει app.
            safeStartActivity("app_permissions",
                    Settings.ACTION_APPLICATION_SETTINGS,
                    Settings.ACTION_SETTINGS
            );
            break;
    }
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

Button daily = mkGreenBtn(gr?"1 Ημέρα":"Daily");
Button weekly = mkGreenBtn(gr?"1 Εβδομάδα":"Weekly");
Button monthly = mkGreenBtn(gr?"1 Μήνας":"Monthly");
Button skip = mkRedBtn(gr?"Παράλειψη":"Skip");

daily.setOnClickListener(v -> {
    OptimizerScheduler.enableReminder(this,1);
    finish();
});

weekly.setOnClickListener(v -> {
    OptimizerScheduler.enableReminder(this,7);
    finish();
});

monthly.setOnClickListener(v -> {
    OptimizerScheduler.enableReminder(this,30);
    finish();
});

skip.setOnClickListener(v -> finish());

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
