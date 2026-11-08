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
    private Sensor orientationSensor;
    private TextView info;
    private String instructions;
    private int samples = 0;
    private final Lab7SensorEvidence.OrientationSequence sequence =
            new Lab7SensorEvidence.OrientationSequence();

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final boolean gr = AppLang.isGreek(this);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (manager != null) {
            // Prefer the OS gravity vector, which filters much of the transient
            // acceleration. Fall back to the accelerometer with timed stability.
            orientationSensor = manager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            if (orientationSensor == null) {
                orientationSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
        }
        // Do not restore previously observed samples after recreation/process death.
        // A fresh registration must generate fresh evidence.

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFF101010);
        info = new TextView(this);
        instructions = gr
                ? "Κράτησε το κινητό όρθιο (portrait) τουλάχιστον 1 δευτερόλεπτο. "
                        + "Μετά γύρισέ το αργά στο πλάι (landscape) και κράτησέ το σταθερό.\n\n"
                        + "Το κούνημα δεν θεωρείται περιστροφή."
                : "Hold the device steady in portrait for at least one second. "
                        + "Then slowly turn it to landscape and hold it steady.\n\n"
                        + "Shaking does not count as rotation.";
        info.setText(instructions);
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
                    .putExtra("reason", orientationSensor == null
                            ? "Gravity/accelerometer sensor unavailable" : "Guided test not completed")
                    .putExtra("samples", samples));
            finish();
        });
        root.addView(end);
        setContentView(root);
        if (orientationSensor == null) {
            info.setText(gr ? "Το Android δεν διαθέτει αισθητήρα βαρύτητας ή επιταχυνσιόμετρο."
                            : "Android did not expose a gravity sensor or accelerometer.");
        }
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        // No test-progress restoration: stale sensor evidence cannot survive lifecycle.
        super.onSaveInstanceState(state);
    }

    @Override protected void onResume() {
        super.onResume();
        sequence.reset();
        samples = 0;
        info.setText(instructions);
        if (manager != null && orientationSensor != null &&
                !manager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI)) {
            info.setText(AppLang.isGreek(this)
                    ? "Δεν διατίθεται ροή συμβάντων προσανατολισμού."
                    : "Orientation sensor event registration unavailable.");
        }
    }

    @Override protected void onPause() {
        if (manager != null) manager.unregisterListener(this);
        sequence.reset();
        super.onPause();
    }

    @Override public void onSensorChanged(SensorEvent event) {
        if (event == null || event.sensor != orientationSensor
                || event.values == null || event.values.length < 3) return;
        samples++;
        boolean portraitPreviouslyVerified = sequence.sawStablePortrait();
        boolean complete = sequence.observe(event.values[0], event.values[1],
                event.values[2], event.timestamp);
        if (complete) {
            setResult(RESULT_OK, new Intent()
                    .putExtra("evidence", "timed_stable_portrait_then_landscape")
                    .putExtra("orientation_source", orientationSensor.getType()
                            == Sensor.TYPE_GRAVITY ? "ANDROID_GRAVITY" : "ANDROID_ACCELEROMETER")
                    .putExtra("samples", samples));
            finish();
        } else if (!portraitPreviouslyVerified && sequence.sawStablePortrait()) {
            info.setText(AppLang.isGreek(this)
                    ? "Επιβεβαιώθηκε σταθερό portrait. Τώρα γύρισε τη συσκευή "
                            + "σε landscape και κράτησέ την σταθερά."
                    : "Stable portrait observed. Now slowly rotate to landscape "
                            + "and keep the device steady.");
        }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Event accuracy is not proof of calibrated physical orientation.
    }

    @Override public int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
