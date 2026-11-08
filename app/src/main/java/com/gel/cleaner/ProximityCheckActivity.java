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
    private boolean nearSeen = false;
    private boolean farSeen = false;
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
        if (state != null) {
            nearSeen = state.getBoolean("near_seen");
            farSeen = state.getBoolean("far_seen");
            nearEvents = state.getInt("near_events");
            farEvents = state.getInt("far_events");
        }

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFF101010);
        info = new TextView(this);
        info.setText(gr
                ? "Απομάκρυνε το χέρι από τον αισθητήρα και μετά κάλυψέ τον αργά. "
                        + "Στη συνέχεια αποκάλυψέ τον.\n\n"
                        + "Η οθόνη δεν χρειάζεται να σβήσει: μετράμε γεγονότα κοντά/μακριά."
                : "First uncover, then cover, then uncover the proximity area.\n\n"
                        + "The screen does not need to turn off: we measure near/far events.");
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
                    .putExtra("near", nearSeen)
                    .putExtra("far", farSeen));
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
        outState.putBoolean("near_seen", nearSeen);
        outState.putBoolean("far_seen", farSeen);
        outState.putInt("near_events", nearEvents);
        outState.putInt("far_events", farEvents);
        super.onSaveInstanceState(outState);
    }

    @Override protected void onResume() {
        super.onResume();
        if (manager != null && proximity != null &&
                !manager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_UI)) {
            info.setText(AppLang.isGreek(this)
                    ? "Δεν είναι διαθέσιμα συμβάντα αισθητήρα εγγύτητας."
                    : "Proximity event registration unavailable.");
        }
    }

    @Override protected void onPause() {
        if (manager != null) manager.unregisterListener(this);
        super.onPause();
    }

    @Override public void onSensorChanged(SensorEvent event) {
        if (event == null || event.sensor == null
                || event.sensor.getType() != Sensor.TYPE_PROXIMITY
                || event.values == null || event.values.length == 0
                || proximity == null) return;
        Lab7SensorEvidence.Proximity state = Lab7SensorEvidence.proximityState(
                event.values[0], proximity.getMaximumRange());
        if (state == Lab7SensorEvidence.Proximity.UNKNOWN) return;
        if (state == Lab7SensorEvidence.Proximity.NEAR) {
            nearEvents++;
            if (nearEvents >= 2) nearSeen = true;
        } else {
            farEvents++;
            if (farEvents >= 2) farSeen = true;
        }
        if (nearSeen && farSeen) {
            setResult(RESULT_OK, new Intent()
                    .putExtra("evidence", "near_far_sensor_event_transition")
                    .putExtra("near", true).putExtra("far", true));
            finish();
        } else {
            info.setText(AppLang.isGreek(this)
                    ? "Πραγματικά συμβάντα Android:\nΚοντά: " + nearEvents
                            + "\nΜακριά: " + farEvents
                            + "\nΚάλυψε και αποκάλυψε τον αισθητήρα."
                    : "Observed Android sensor events:\nNear: " + nearEvents
                            + "\nFar: " + farEvents
                            + "\nCover and uncover the sensor.");
        }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Physical distance calibration is outside app-level observation.
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
