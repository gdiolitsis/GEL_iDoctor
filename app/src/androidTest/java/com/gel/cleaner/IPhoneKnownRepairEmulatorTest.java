package com.gel.cleaner;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Android emulator test for the ACTUAL production iPhone Labs engine.
 *
 * Public repair case: https://www.ifixit.com/Answers/View/805260/
 * An iPhone 11 exhibited 3-minute restarts and the log identified mic2.
 * The owner reported that reseating a loose power-button flex restored operation.
 *
 * Input below is a SHORT, PRIVACY-SAFE RECONSTRUCTION of the key published
 * diagnostic tokens, NOT the complete original file and NOT an iPhone emulator.
 * The test neither changes entitlement nor opens the customer report.
 */
@RunWith(AndroidJUnit4.class)
public class IPhoneKnownRepairEmulatorTest {
    private static final String TAG = "GEL_IPHONE_CASE";
    private static final String DOCUMENTED_REPAIR_CASE =
            "panic(cpu 1 caller 0x00000000): userspace watchdog timeout: "
            + "no successful checkins from thermalmonitord\n"
            + "service returned not alive: Missing sensor(s): mic2\n"
            + "service: thermalmonitord, no successful checkins in 196 seconds\n"
            + "OS version: iPhone OS 17 beta\n"
            + "Hardware model: iPhone12,1\n";

    private static void setPrivateField(Object target, String name, Object value)
            throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static Object getPrivateField(Object target, String name)
            throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    @Test
    public void importedCaseRunsAllEightLabsAndReportsSpecificity() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences("gel_prefs", Context.MODE_PRIVATE)
                .edit().putBoolean("panic_guide_hidden", true).commit();

        try (ActivityScenario<IPhoneLabsActivity> scenario =
                ActivityScenario.launch(IPhoneLabsActivity.class)) {
            scenario.onActivity(activity -> {
                try {
                    setPrivateField(activity, "panicLogText", DOCUMENTED_REPAIR_CASE);
                    setPrivateField(activity, "panicLogLoaded", true);
                    setPrivateField(activity, "panicLogName",
                            "public-repair-case-mic2-reconstruction.txt");
                    setPrivateField(activity, "panicLogCount", 1);
                    Method method = activity.getClass()
                            .getDeclaredMethod("runAllAppleDiagnostics");
                    method.setAccessible(true);
                    method.invoke(activity);
                } catch (Exception e) {
                    throw new AssertionError("Could not execute actual iPhone Labs", e);
                }
            });

            AtomicReference<String> logText = new AtomicReference<>("");
            boolean allEightDone = false;
            for (int attempt = 0; attempt < 40; attempt++) {
                scenario.onActivity(activity -> {
                    try {
                        TextView log = (TextView) getPrivateField(activity, "txtLog");
                        logText.set(log.getText().toString());
                    } catch (Exception e) {
                        throw new AssertionError("Cannot read actual lab output", e);
                    }
                });
                String output = logText.get();
                if (output.contains("Lab 8 ολοκληρώθηκε")
                        || output.contains("Lab 8 finished")) {
                    allEightDone = true;
                    break;
                }
                Thread.sleep(500);
            }
            String output = logText.get();
            assertTrue("Eight-lab run did not complete: " + output, allEightDone);
            assertTrue("General Watchdog panic not recognized: " + output,
                    output.contains("Watchdog"));

            // Separate classification of generic event and part-level diagnosis.
            // The current nine JSON rules may identify Watchdog without the mic2
            // sensor or its physical flex; that is a meaningful NEGATIVE result.
            boolean specificSensorNamed = output.contains("mic2");
            boolean powerFlexIdentified =
                    output.toLowerCase(java.util.Locale.ROOT).contains("power-button flex")
                    || output.contains("καλωδιοταινία κουμπιού");
            Log.i(TAG, "GENERAL_WATCHDOG_RECOGNIZED=true");
            Log.i(TAG, "SPECIFIC_MIC2_RECOGNIZED=" + specificSensorNamed);
            Log.i(TAG, "CONFIRMED_REPAIR_PART_IDENTIFIED=" + powerFlexIdentified);
            Log.i(TAG, "CASE_SOURCE=https://www.ifixit.com/Answers/View/805260/");
            Log.i(TAG, "CASE_TYPE=ABRIDGED_RECONSTRUCTION_NOT_ORIGINAL_FULL_LOG");
            assertFalse("Lab 8 must not invent a measured health score",
                    output.contains("75/100"));
        }
    }
}
