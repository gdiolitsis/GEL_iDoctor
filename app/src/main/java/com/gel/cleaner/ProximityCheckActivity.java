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
import android.os.SystemClock;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * ============================================================
 * LAB 8 — Proximity Sensor Check
 * FINAL — PURE TEST ONLY (NO LOGS • NO TTS • NO POPUPS)
 * ============================================================
 */
public class ProximityCheckActivity extends GELAutoActivityHook
        implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximity;

    // Require observed FAR -> NEAR -> FAR, never infer screen blackout.
    private int stage = 0;
    private long stageChangeMs = 0L;
    private static final long MIN_TRANSITION_MS = 120L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFF101010);

        // ======================
        // INFO TEXT
        // ======================
        TextView info = new TextView(this);
        info.setText(AppLang.isGreek(this)
                ? "Άφησε πρώτα ελεύθερο τον αισθητήρα.\n"
                  + "Μετά κάλυψε την περιοχή του ακουστικού και απομάκρυνε ξανά το χέρι."
                : "First leave the sensor uncovered.\n"
                  + "Then cover the earpiece sensor area and uncover it again.");
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
        stage = 0;
        stageChangeMs = 0L;
        if (sensorManager != null && proximity != null) {
            sensorManager.registerListener(
                    this,
                    proximity,
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
        if (event == null || event.values.length == 0
                || event.sensor != proximity || isFinishing()) return;
        float value = event.values[0];
        float maxRange = proximity.getMaximumRange();
        if (!Float.isFinite(value) || !Float.isFinite(maxRange)
                || maxRange <= 0f || value < 0f) return;

        boolean near = value < maxRange - Math.min(0.1f, maxRange * 0.05f);
        long nowMs = SystemClock.elapsedRealtime();
        if (stage == 0) {
            if (!near) {
                stage = 1;
                stageChangeMs = nowMs;
            }
        } else if (stage == 1) {
            if (near && nowMs - stageChangeMs >= MIN_TRANSITION_MS) {
                stage = 2;
                stageChangeMs = nowMs;
            }
        } else if (stage == 2 && !near
                && nowMs - stageChangeMs >= MIN_TRANSITION_MS) {
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
