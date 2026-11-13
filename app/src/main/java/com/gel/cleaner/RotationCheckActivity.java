package com.gel.cleaner;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * ============================================================
 * LAB 7 — Rotation Check
 * FINAL — PURE TEST ONLY (NO LOGS • NO TTS • NO POPUPS)
 * ============================================================
 */
public class RotationCheckActivity extends GELAutoActivityHook
        implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    // A sudden jolt is not a sustained portrait-to-landscape posture change.
    // Prefer the gravity sensor where Android exposes it; accelerometer is a fallback.
    private boolean portraitConfirmed = false;
    private int portraitSamples = 0;
    private int landscapeSamples = 0;
    private static final int STABLE_SAMPLES = 3;
    private static final float MIN_AXIS_GRAVITY = 6.5f;
    private static final float MAX_FACE_UP_GRAVITY = 6.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            if (accelerometer == null) {
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
        }

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFF101010);

        // ======================
        // INFO TEXT
        // ======================
        TextView info = new TextView(this);
        info.setText(AppLang.isGreek(this)
                ? "Κράτησε τη συσκευή όρθια και μετά γύρισέ την αργά στο πλάι.\n"
                  + "Η εικόνα παραμένει κατακόρυφη. Δεν αρκεί ένα απότομο τίναγμα."
                : "Hold the device upright, then turn it slowly sideways.\n"
                  + "The screen stays portrait. A sudden jolt is not enough.");
        info.setTextSize(18f);
        info.setTextColor(Color.WHITE);
        info.setGravity(Gravity.CENTER);
        info.setPadding(dp(16), dp(16), dp(16), dp(16));

        FrameLayout.LayoutParams infoLp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        infoLp.gravity = Gravity.CENTER;
        root.addView(info, infoLp);

        // ======================
        // END TEST BUTTON
        // ======================
        Button end = new com.gel.cleaner.GelButton(this);
        end.setText(AppLang.isGreek(this) ? "ΤΕΛΟΣ ΤΕΣΤ" : "END TEST");
        end.setAllCaps(false);
        end.setTextColor(Color.WHITE);
        end.setTextSize(15f);
        end.setTypeface(null, Typeface.BOLD);

        GradientDrawable redBtn = new GradientDrawable();
        redBtn.setColor(0xFFE60000);
        redBtn.setCornerRadius(dp(14));
        redBtn.setStroke(dp(3), 0xFFFFD700);
        end.setBackground(redBtn);

        FrameLayout.LayoutParams endLp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        dp(56)
                );
        endLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        endLp.leftMargin = dp(24);
        endLp.rightMargin = dp(24);
        endLp.bottomMargin = dp(24);
        end.setLayoutParams(endLp);

        end.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        root.addView(end);
        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        portraitConfirmed = false;
        portraitSamples = 0;
        landscapeSamples = 0;
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_UI
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null || event.values.length < 3
                || event.sensor != accelerometer || isFinishing()) return;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z)) return;

        boolean upright = Math.abs(z) < MAX_FACE_UP_GRAVITY;
        boolean portrait = upright && Math.abs(y) > MIN_AXIS_GRAVITY
                && Math.abs(y) > 1.35f * Math.abs(x);
        boolean landscape = upright && Math.abs(x) > MIN_AXIS_GRAVITY
                && Math.abs(x) > 1.35f * Math.abs(y);

        if (!portraitConfirmed) {
            portraitSamples = portrait ? portraitSamples + 1 : 0;
            if (portraitSamples >= STABLE_SAMPLES) {
                portraitConfirmed = true;
                landscapeSamples = 0;
            }
            return;
        }
        landscapeSamples = landscape ? landscapeSamples + 1 : 0;
        if (landscapeSamples >= STABLE_SAMPLES) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not used
    }

    public int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (v * d + 0.5f);
    }
}
