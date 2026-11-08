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
 * Lab 7 proximity near/far event test. It does NOT test screen power state:
 * Android is not required to turn off the display outside an active call.
 */
public class ProximityCheckActivity extends GELAutoActivityHook
        implements SensorEventListener {
    private SensorManager manager;
    private Sensor proximity;
    private TextView info;
    private String instructions;
    private final Lab7SensorEvidence.ProximitySequence sequence =
            new Lab7SensorEvidence.ProximitySequence();
    private int nearEvents = 0;
    private int farEvents = 0;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        final boolean gr = AppLang.isGreek(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (manager != null) {
            proximity = manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFF101010);
        info = new TextView(this);
        instructions = gr
                ? "Ξεκίνα με τον αισθητήρα ακάλυπτο (ΜΑΚΡΙΑ). Μετά κάλυψέ τον "
                        + "(ΚΟΝΤΑ) και αποκάλυψέ τον ξανά (ΜΑΚΡΙΑ), με μικρές παύσεις.\n\n"
                        + "Ελέγχουμε μόνο τα συμβάντα εγγύτητας, ΟΧΙ αν σβήνει η οθόνη."
                : "Start with the proximity area uncovered (FAR). Then cover it "
                        + "(NEAR) and uncover it again (FAR), with short pauses.\n\n"
                        + "This tests proximity events, NOT whether the screen turns off.";
        info.setText(instructions);
        info.setTextSize(17f);
        info.setTextColor(Color.WHITE);
        info.setGravity(Gravity.CENTER);
        info.setPadding(dp(16), dp(16), dp(16), dp(16));
        FrameLayout.LayoutParams textLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        textLp.gravity = Gravity.CENTER;
        root.addView(info, textLp);

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
                    .putExtra("reason", proximity == null
                            ? "Proximity sensor not advertised" : "Guided test incomplete")
                    .putExtra("near_events", nearEvents)
                    .putExtra("far_events", farEvents));
            finish();
        });
        root.addView(end);
        setContentView(root);
        if (proximity == null) {
            info.setText(gr ? "Δεν υπάρχει προσβάσιμος αισθητήρας εγγύτητας."
                            : "No Android-exposed proximity sensor available.");
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        // No partial result may survive activity recreation.
        super.onSaveInstanceState(outState);
    }

    @Override protected void onResume() {
        super.onResume();
        sequence.reset();
        nearEvents = 0;
        farEvents = 0;
        info.setText(instructions);
        if (manager != null && proximity != null &&
                !manager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_UI)) {
            info.setText(AppLang.isGreek(this)
                    ? "Δεν είναι διαθέσιμα συμβάντα αισθητήρα εγγύτητας."
                    : "Proximity event registration unavailable.");
        }
        if (proximity == null) {
            info.setText(AppLang.isGreek(this)
                    ? "Δεν υπάρχει προσβάσιμος αισθητήρας εγγύτητας."
                    : "No Android-exposed proximity sensor available.");
        }
    }

    @Override protected void onPause() {
        if (manager != null) manager.unregisterListener(this);
        sequence.reset();
        super.onPause();
    }

    @Override public void onSensorChanged(SensorEvent event) {
        if (event == null || event.sensor != proximity
                || event.values == null || event.values.length == 0
                || proximity == null) return;
        Lab7SensorEvidence.Proximity observed = Lab7SensorEvidence.proximityState(
                event.values[0], proximity.getMaximumRange());
        if (observed == Lab7SensorEvidence.Proximity.UNKNOWN || event.timestamp <= 0L) return;
        if (observed == Lab7SensorEvidence.Proximity.NEAR) nearEvents++;
        else farEvents++;

        if (sequence.observe(observed, event.timestamp)) {
            setResult(RESULT_OK, new Intent()
                    .putExtra("evidence", "timestamped_far_near_far_proximity_sequence")
                    .putExtra("near_events", nearEvents)
                    .putExtra("far_events", farEvents)
                    .putExtra("screen_power_verified", false));
            finish();
            return;
        }

        final boolean gr = AppLang.isGreek(this);
        String step = sequence.stage() == 0
                ? (gr ? "Αρχικά κράτησε τον αισθητήρα ακάλυπτο (ΜΑΚΡΙΑ)."
                      : "First keep the sensor uncovered (FAR).")
                : sequence.stage() == 1
                ? (gr ? "Τώρα κάλυψε τον αισθητήρα (ΚΟΝΤΑ)."
                      : "Now cover the proximity area (NEAR).")
                : (gr ? "Τώρα αποκάλυψε ξανά τον αισθητήρα (ΜΑΚΡΙΑ)."
                      : "Now uncover the proximity area again (FAR).");
        info.setText((gr ? "Συμβάντα Android — Κοντά: " : "Android events — Near: ")
                + nearEvents + (gr ? ", Μακριά: " : ", Far: ") + farEvents
                + "\n" + step
                + (gr ? "\nΤο σβήσιμο της οθόνης ΔΕΝ ελέγχεται."
                      : "\nDisplay turn-off is NOT tested."));
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Physical distance calibration is outside app-level observation.
    }

    @Override public int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
