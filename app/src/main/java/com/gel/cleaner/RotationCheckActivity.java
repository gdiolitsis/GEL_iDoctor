package com.gel.cleaner;

import android.content.Intent;
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
 * Lab 7 guided functional challenge, not a calibration or hardware certificate.
 * A shake is not rotation: require two distinct stable gravity orientations.
 */
public class RotationCheckActivity extends GELAutoActivityHook
        implements SensorEventListener {
    private SensorManager manager;
    private Sensor accelerometer;
    private TextView info;
    private int samples = 0;
    private int consecutive = 0;
    private Lab7SensorEvidence.Orientation pending =
            Lab7SensorEvidence.Orientation.OTHER_OR_UNSTABLE;
    private Lab7SensorEvidence.Orientation stable =
            Lab7SensorEvidence.Orientation.OTHER_OR_UNSTABLE;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final boolean gr = AppLang.isGreek(this);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (manager != null) {
            accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (state != null) {
            samples = state.getInt("samples", 0);
            consecutive = state.getInt("consecutive", 0);
            try {
                pending = Lab7SensorEvidence.Orientation.valueOf(
                        state.getString("pending", "OTHER_OR_UNSTABLE"));
                stable = Lab7SensorEvidence.Orientation.valueOf(
                        state.getString("stable", "OTHER_OR_UNSTABLE"));
            } catch (IllegalArgumentException ignored) {
                pending = Lab7SensorEvidence.Orientation.OTHER_OR_UNSTABLE;
                stable = Lab7SensorEvidence.Orientation.OTHER_OR_UNSTABLE;
            }
        }

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFF101010);
        info = new TextView(this);
        info.setText(gr
                ? "Κράτησε το κινητό πρώτα όρθιο (portrait) και μετά γύρισέ το "
                        + "αργά στο πλάι (landscape).\n\n"
                        + "Απαιτούνται σταθερά δείγματα επιταχυνσιομέτρου, όχι απλό κούνημα."
                : "Hold the phone in portrait, then slowly rotate it to landscape.\n\n"
                        + "Stable accelerometer orientation events are required, not a shake.");
        info.setTextSize(18f);
        info.setTextColor(Color.WHITE);
        info.setGravity(Gravity.CENTER);
        info.setPadding(dp(16), dp(16), dp(16), dp(16));
        FrameLayout.LayoutParams infoLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        infoLp.gravity = Gravity.CENTER;
        root.addView(info, infoLp);

        Button end = new GelButton(this);
        end.setText(gr ? "ΛΗΞΗ ΔΟΚΙΜΗΣ" : "END TEST");
        end.setAllCaps(false);
        end.setTextColor(Color.WHITE);
        end.setTextSize(15f);
        end.setTypeface(null, Typeface.BOLD);
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(0xFF8B0000);
        shape.setCornerRadius(dp(14));
        shape.setStroke(dp(3), 0xFFFFD700);
        end.setBackground(shape);
        FrameLayout.LayoutParams endLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, dp(56));
        endLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        endLp.leftMargin = dp(24);
        endLp.rightMargin = dp(24);
        endLp.bottomMargin = dp(24);
        end.setLayoutParams(endLp);
        end.setOnClickListener(v -> {
            setResult(RESULT_CANCELED, new Intent()
                    .putExtra("reason", accelerometer == null
                            ? "Accelerometer not advertised" : "Guided test not completed")
                    .putExtra("samples", samples));
            finish();
        });
        root.addView(end);
        setContentView(root);
        if (accelerometer == null) {
            info.setText(gr ? "Το Android δεν διαθέτει επιταχυνσιόμετρο για αυτόν τον έλεγχο."
                            : "Android did not expose an accelerometer for this test.");
        }
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        state.putInt("samples", samples);
        state.putInt("consecutive", consecutive);
        state.putString("pending", pending.name());
        state.putString("stable", stable.name());
        super.onSaveInstanceState(state);
    }

    @Override protected void onResume() {
        super.onResume();
        if (manager != null && accelerometer != null &&
                !manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)) {
            info.setText(AppLang.isGreek(this)
                    ? "Δεν έγινε καταχώριση ακροατή επιταχυνσιομέτρου."
                    : "Accelerometer event registration unavailable.");
        }
    }

    @Override protected void onPause() {
        if (manager != null) manager.unregisterListener(this);
        super.onPause();
    }

    @Override public void onSensorChanged(SensorEvent event) {
        if (event == null || event.sensor.getType() != Sensor.TYPE_ACCELEROMETER
                || event.values == null || event.values.length < 3) return;
        samples++;
        Lab7SensorEvidence.Orientation next = Lab7SensorEvidence.gravityOrientation(
                event.values[0], event.values[1], event.values[2]);
        if (next == Lab7SensorEvidence.Orientation.OTHER_OR_UNSTABLE) {
            pending = next;
            consecutive = 0;
            return;
        }
        if (next != pending) {
            pending = next;
            consecutive = 1;
        } else {
            consecutive++;
        }
        if (consecutive < 3 || next == stable) return;
        if (stable == Lab7SensorEvidence.Orientation.OTHER_OR_UNSTABLE) {
            stable = next;
            info.setText(AppLang.isGreek(this)
                    ? "Πρώτη σταθερή θέση: " + stable.name()
                            + "\nΠερίστρεψε αργά στη δεύτερη θέση."
                    : "Stable initial orientation: " + stable.name()
                            + "\nSlowly rotate to the other orientation.");
        } else {
            setResult(RESULT_OK, new Intent()
                    .putExtra("evidence", "stable_portrait_landscape_transition")
                    .putExtra("samples", samples));
            finish();
        }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Event accuracy is not proof of calibrated physical orientation.
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
