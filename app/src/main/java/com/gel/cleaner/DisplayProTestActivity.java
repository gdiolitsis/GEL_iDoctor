package com.gel.cleaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gel.cleaner.UIHelpers;

public class DisplayProTestActivity extends GELAutoActivityHook {

    // ============================================================
    // CONFIG
    // ============================================================
    private static final int STEP_DURATION_MS = 2500;
    private static final int LOOP_COUNT = 3;
    private static final long MAX_RUNTIME_MS = 5 * 60 * 1000;

    // ============================================================
    // GLOBAL STATE (LOCKED FLOW)
    // ============================================================
    private volatile boolean testFinished = false;
    private volatile boolean activityAlive = true;

    // ============================================================
    // STATE
    // ============================================================
    private FrameLayout root;
    private TextView hint;
    private int stepIndex = 0;
    private int loopIndex = 0;
    private long startTimeMs;

    // The override affects only this Activity, not Settings.System.
    private boolean brightnessOverrideActive = false;
    private float savedWindowBrightness =
            WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;

    private TestStep[] steps;
    private final Handler h = new Handler(Looper.getMainLooper());

    // ============================================================
    // APPLY APP LANGUAGE
    // ============================================================
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAlive = true;

        FrameLayout dummy = new FrameLayout(this);
        dummy.setBackgroundColor(Color.BLACK);
        setContentView(dummy);

        dummy.post(() -> {
            if (!isFinishing() && !isDestroyed() && activityAlive) {
                showOledWarning();
            }
        });
    }

    // ============================================================
    // TEMPORARY VISUAL-TEST BRIGHTNESS
    // ============================================================
    // Temporarily request maximum brightness for the actual visual patterns.
    // The system may still impose thermal/power restrictions on physical luminance.
    private boolean applyTemporaryDisplayBrightness() {
        if (brightnessOverrideActive) return true;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        savedWindowBrightness = params.screenBrightness;
        brightnessOverrideActive = true;
        try {
            params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            getWindow().setAttributes(params);
            return true;
        } catch (RuntimeException ignored) {
            restorePreviousDisplayBrightness();
            return false;
        }
    }

    // Restoring -1 resumes the user's existing manual/automatic system policy.
    private void restorePreviousDisplayBrightness() {
        if (!brightnessOverrideActive) return;
        brightnessOverrideActive = false;
        try {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.screenBrightness = savedWindowBrightness;
            getWindow().setAttributes(params);
        } catch (RuntimeException ignored) {
            // No global settings were modified and this window is closing.
        }
    }

    // ============================================================
    // SAFE CANCEL — THE ONLY CANCEL PATH
    // ============================================================
    private void safeCancel() {

        if (testFinished) return;
        testFinished = true;
        restorePreviousDisplayBrightness();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        try { h.removeCallbacksAndMessages(null); } catch (Throwable ignore) {}
        try { AppTTS.stop(); } catch (Throwable ignore) {}

        GELServiceLog.logInfo(
                "LAB Display Pro Test — CANCELED by user"
        );

        setResult(RESULT_CANCELED);
        finish();
    }

@Override
public void onBackPressed() {
    safeCancel();
}

    @Override
    protected void onPause() {
        // Leaving the foreground invalidates the guided visual observation.
        if (!testFinished) safeCancel();
        restorePreviousDisplayBrightness();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        activityAlive = false;
        restorePreviousDisplayBrightness();
        h.removeCallbacksAndMessages(null);
        AppTTS.stop();
        super.onDestroy();
    }

    // ============================================================
    // POPUP 1 — OLED WARNING
    // ============================================================
    private void showOledWarning() {

        final boolean gr = AppLang.isGreek(this);

        final String text =
                gr
                        ? "Η δοκιμή ζητά προσωρινά μέγιστη φωτεινότητα\n"
                        + "μόνο κατά την προβολή των μοτίβων και μπορεί να\n"
                        + "καταπονήσει προσωρινά πάνελ OLED.\n\n"
                        + "Στο τέλος επανέρχεται η προηγούμενη φωτεινότητα,\n"
                        + "χωρίς αλλαγή στις ρυθμίσεις της συσκευής.\n\n"
                        + "Συνέχισε μόνο αν αποδέχεσαι τον κίνδυνο."
                        : "The test temporarily requests maximum brightness\n"
                        + "only while showing patterns and may stress OLED\n"
                        + "panels.\n\n"
                        + "Your previous brightness setting resumes afterwards;\n"
                        + "device brightness preferences are unchanged.\n\n"
                        + "Proceed only if you accept this.";

        AlertDialog.Builder b =
                new AlertDialog.Builder(this,
                        android.R.style.Theme_Material_Dialog_NoActionBar);
        b.setCancelable(false);

        LinearLayout root = buildPopupRoot(this);
        root.addView(buildHeader(
        gr ? "Δοκιμή Καταπόνησης Οθόνης" : "Display Stress Test"
));

        root.addView(buildMessage(text));
root.addView(buildMuteRow());

LinearLayout buttons = new LinearLayout(this);
buttons.setOrientation(LinearLayout.HORIZONTAL);
buttons.setGravity(Gravity.CENTER);

Button cancel = gelButton(gr ? "ΑΚΥΡΩΣΗ" : "CANCEL", 0xFFB00020);
Button start  = gelButton(gr ? "ΕΝΑΡΞΗ" : "START",  0xFF0F8A3B);

        setDualButtons(cancel, start, buttons);
        root.addView(buttons);

        b.setView(root);

        AlertDialog d = b.create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        d.show();

        com.gel.cleaner.GelButtonLayout.fitDialog(d);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (d.isShowing() && !testFinished && !AppTTS.isMuted(this)) {
        AppTTS.ensureSpeak(this, text);
    }
}, 120);

        cancel.setOnClickListener(v -> {
            d.dismiss();
            safeCancel();
        });

        start.setOnClickListener(v -> {
            AppTTS.stop();
            d.dismiss();
            initAndStart();
        });
    }

    // ============================================================
    // INIT TEST
    // ============================================================
    private void initAndStart() {

        final boolean gr = AppLang.isGreek(this);

        steps = new TestStep[]{
                new SolidStep(Color.BLACK, gr ? "Μαύρο — φωτεινά pixels" : "Black — bright pixels"),
                new SolidStep(Color.WHITE, gr ? "Λευκό — σκοτεινά σημεία" : "White — dark spots"),
                new SolidStep(Color.RED,   gr ? "Κόκκινο — burn-in"       : "Red — burn-in"),
                new SolidStep(Color.GREEN, gr ? "Πράσινο — ομοιομορφία"  : "Green — uniformity"),
                new SolidStep(Color.BLUE,  gr ? "Μπλε — ομοιομορφία"     : "Blue — uniformity"),
                new DrawableStep(DisplayPatterns.makeGradient(),
                        gr ? "Διαβάθμιση — banding" : "Gradient — banding"),
                new DrawableStep(DisplayPatterns.makeCheckerboard(),
                        gr ? "Σκακιέρα — mura" : "Checkerboard — mura"),
                new DrawableStep(DisplayPatterns.makeBurnInCycle(),
                        gr ? "Κύκλος καταπόνησης OLED" : "Burn-in stress cycle")
        };

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!applyTemporaryDisplayBrightness()) {
            GELServiceLog.logInfo(
                    "LAB Display Pro Test — temporary window brightness unavailable");
            safeCancel();
            return;
        }

        root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        hint = new TextView(this);
        // Bright yellow alone disappears on a pure-white or green panel.
        // Keep a small near-opaque black backing under the caption only.
        hint.setTextColor(Color.YELLOW);
        hint.setTypeface(null, Typeface.BOLD);
        hint.setTextSize(16f);
        hint.setPadding(dp(14), dp(10), dp(14), dp(10));
        hint.setGravity(Gravity.CENTER);
        GradientDrawable hintBackground = new GradientDrawable();
        hintBackground.setColor(0xF0000000);
        hintBackground.setCornerRadius(dp(10));
        hint.setBackground(hintBackground);
        root.addView(hint);

        Button exitBtn = new com.gel.cleaner.GelButton(this);
        exitBtn.setAllCaps(false);
        exitBtn.setText(gr ? "ΕΞΟΔΟΣ" : "EXIT");
        exitBtn.setTextColor(Color.WHITE);
        exitBtn.setTextSize(15f);

        GradientDrawable exitBg = new GradientDrawable();
        exitBg.setColor(0xFF8B0000);
        exitBg.setCornerRadius(dp(14));
        exitBg.setStroke(dp(3), 0xFFFFD700);
        exitBtn.setBackground(exitBg);

        FrameLayout.LayoutParams lpExit =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        lpExit.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lpExit.bottomMargin = dp(24);
        exitBtn.setLayoutParams(lpExit);

        exitBtn.setOnClickListener(v -> safeCancel());

        root.addView(exitBtn);
        setContentView(root);

        UIHelpers.applyPressEffectRecursive(getWindow().getDecorView());

        startTimeMs = SystemClock.elapsedRealtime();
        stepIndex = 0;
        loopIndex = 0;

        runStep();
    }

    // ============================================================
    // MAIN LOOP
    // ============================================================
    private void runStep() {

        if (testFinished || !activityAlive
                || isFinishing() || isDestroyed()) {
            return;
        }

        if (SystemClock.elapsedRealtime() - startTimeMs > MAX_RUNTIME_MS) {
            // A timed-out visual sequence was not fully presented.
            safeCancel();
            return;
        }

        if (stepIndex >= steps.length) {
            stepIndex = 0;
            loopIndex++;
            if (loopIndex >= LOOP_COUNT) {
                showFinalQuestion();
                return;
            }
        }

        TestStep s = steps[stepIndex];
        s.apply(root);

        // The caption's dark backing preserves contrast on every test colour.
        hint.setText(
                s.label + "\n\n" +
                (AppLang.isGreek(this) ? "Κύκλος " : "Cycle ") +
                (loopIndex + 1) + " / " + LOOP_COUNT
        );

        stepIndex++;
        h.postDelayed(this::runStep, STEP_DURATION_MS);
    }

    // ============================================================
    // FINAL QUESTION (NOT TERMINATION)
    // ============================================================
    private void showFinalQuestion() {

        if (testFinished || !activityAlive
                || isFinishing() || isDestroyed()) {
            return;
        }

        // Pattern presentation is complete: do not keep screen forced bright/on
        // while waiting for a user response in the final dialog.
        restorePreviousDisplayBrightness();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final boolean gr = AppLang.isGreek(this);

        final String text =
                gr
                        ? "Παρατήρησες κάποιο πρόβλημα στην οθόνη;\n\n"
                        + "• Burn-in;\n• Ζώνες χρώματος;\n• Κηλίδες / mura;\n• Ανομοιομορφία;"
                        : "Did you notice any display issues?\n\n"
                        + "• Burn-in?\n• Color banding?\n• Stains / mura?\n• Uneven brightness?";

        AlertDialog.Builder b =
                new AlertDialog.Builder(this,
                        android.R.style.Theme_Material_Dialog_NoActionBar);
        b.setCancelable(false);

        LinearLayout root = buildPopupRoot(this);
        root.addView(buildHeader(
        gr ? "Οπτικός Έλεγχος" : "Visual Inspection"
));

        SpannableString span = new SpannableString(text);
        span.setSpan(
                new ForegroundColorSpan(0xFF39FF14),
                0,
                text.indexOf("\n"),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        root.addView(buildMessage(span));
root.addView(buildMuteRow());

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER);

        Button no  = gelButton(gr ? "ΟΧΙ\nΟΚ" : "NO\nOK", 0xFF0F8A3B);
        Button yes = gelButton(gr ? "ΝΑΙ\nΠρόβλημα" : "YES\nIssue", 0xFFB00020);

        setDualButtons(no, yes, buttons);
        root.addView(buttons);

        b.setView(root);

        AlertDialog d = b.create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        d.show();

        com.gel.cleaner.GelButtonLayout.fitDialog(d);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (!testFinished && !AppTTS.isMuted(this)) {
        AppTTS.ensureSpeak(this, text);
    }
}, 120);

no.setOnClickListener(v -> endTest(false));
yes.setOnClickListener(v -> endTest(true));
}

    // ============================================================
    // FINAL TERMINATION
    // ============================================================
    private void endTest(boolean issuesDetected) {

    if (testFinished) return;
    testFinished = true;
    restorePreviousDisplayBrightness();
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    try { h.removeCallbacksAndMessages(null); } catch (Throwable ignore) {}
    try { AppTTS.stop(); } catch (Throwable ignore) {}

    if (issuesDetected) {
        GELServiceLog.logInfo(
                "LAB Display Pro Test — COMPLETED (ISSUES DETECTED)"
        );
    } else {
        GELServiceLog.logInfo(
                "LAB Display Pro Test — COMPLETED"
        );
    }

    Intent result = new Intent();
    result.putExtra("display_issues", issuesDetected);
    result.putExtra("display_steps_completed", loopIndex >= LOOP_COUNT);

    setResult(RESULT_OK, result);
    finish();
}

// ============================================================
// UI HELPERS
// ============================================================

private LinearLayout buildPopupRoot(Context ctx) {
    LinearLayout r = new LinearLayout(ctx);
    r.setOrientation(LinearLayout.VERTICAL);
    r.setPadding(dp(24), dp(22), dp(24), dp(18));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(4), 0xFFFFD700);
    r.setBackground(bg);

    return r;
}

// ------------------------------------------------------------
// HEADER (TITLE ONLY — NO MUTE HERE)
// ------------------------------------------------------------
private LinearLayout buildHeader(String titleText) {

    LinearLayout h = new LinearLayout(this);
    h.setOrientation(LinearLayout.VERTICAL);
    h.setPadding(0, 0, 0, dp(12));

    TextView title = new TextView(this);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);

    h.addView(title);
    return h;
}

// ------------------------------------------------------------
// MUTE ROW (CHECKBOX + LABEL — ABOVE BUTTONS)
// ------------------------------------------------------------
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
    label.setText(
            gr
                    ? "Σίγαση φωνητικών οδηγιών"
                    : "Mute voice instructions"
    );
    label.setTextColor(0xFFAAAAAA);
    label.setTextSize(14f);

    // ένα σημείο αλήθειας: AppTTS
    View.OnClickListener toggle = v -> {
        boolean newState = !AppTTS.isMuted(this);
        AppTTS.setMuted(this, newState);
        muteCheck.setChecked(newState);
    };

    row.setOnClickListener(toggle);
    label.setOnClickListener(toggle);

    muteCheck.setOnCheckedChangeListener((b, checked) -> {
        if (checked != AppTTS.isMuted(this)) {
            AppTTS.setMuted(this, checked);
        }
    });

    row.addView(muteCheck);
    row.addView(label);

    return row;
}

    private TextView buildMessage(CharSequence text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(0xFF39FF14);
        tv.setTextSize(15f);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, 0, 0, dp(16));
        return tv;
    }

    private Button gelButton(String text, int color) {
        Button b = new com.gel.cleaner.GelButton(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15f);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(3), 0xFFFFD700);
        b.setBackground(bg);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0, dp(58), 1f);
        lp.setMargins(dp(6), 0, dp(6), 0);
        b.setLayoutParams(lp);
        return b;
    }

    private void setDualButtons(Button l, Button r, LinearLayout p) {
        p.removeAllViews();
        p.addView(l);
        p.addView(r);
    }

    public int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    // ============================================================
    // STEP TYPES
    // ============================================================
    private abstract static class TestStep {
        final String label;
        TestStep(String l) { label = l; }
        abstract void apply(FrameLayout root);
    }

    private static final class SolidStep extends TestStep {
        final int color;
        SolidStep(int c, String l) { super(l); color = c; }
        @Override void apply(FrameLayout root) {
            root.setBackgroundColor(color);
        }
    }

    private static final class DrawableStep extends TestStep {
        final Drawable d;
        DrawableStep(Drawable dr, String l) { super(l); d = dr; }
        @Override void apply(FrameLayout root) {
            root.setBackground(d);
        }
    }
}
