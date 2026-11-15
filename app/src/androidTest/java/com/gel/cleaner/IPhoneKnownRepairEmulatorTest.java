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
                            "public-repair-case.txt");
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
            boolean specificSensorNamed = output.contains("MIC2")
                    && (output.contains("πίσω μικρόφωνο")
                        || output.contains("rear microphone"));
            boolean powerFlexIdentified =
                    output.toLowerCase(java.util.Locale.ROOT).contains("power-button flex")
                    || output.contains("καλωδιοταινία κουμπιού");
            Log.i(TAG, "GENERAL_WATCHDOG_RECOGNIZED=true");
            Log.i(TAG, "SPECIFIC_MIC2_RECOGNIZED=" + specificSensorNamed);
            assertTrue("Concrete mic2 sensor not shown to user: " + output,
                    specificSensorNamed);
            assertTrue("Source-backed iPhone 11 flex area not shown: " + output,
                    powerFlexIdentified);
            Log.i(TAG, "CONFIRMED_REPAIR_PART_IDENTIFIED=" + powerFlexIdentified);
            Log.i(TAG, "CASE_SOURCE=https://www.ifixit.com/Answers/View/805260/");
            Log.i(TAG, "CASE_TYPE=ABRIDGED_RECONSTRUCTION_NOT_ORIGINAL_FULL_LOG");
            assertFalse("Lab 8 must not invent a measured health score",
                    output.contains("75/100"));
        }
    }
    /**
     * Test the same full 8-Lab Android Activity against five independent panic
     * signature reconstructions. Every case has an explicit expected result.
     * Does not alter PRO entitlement or use a customer device.
     */
    @Test
    public void multiplePublishedSignatureFamiliesReachAllEightLabs() throws Exception {
        final String[][] cases = {
                {
                    "14-plus-wireless",
                    "iPhone14,8",
                    "SMC PANIC - ASSERTION FAILED; SMC BSC failure; "
                        + "S.sensor array 0 - 4 is 0x0, 0x400000, 0x0, 0x0, 0x0",
                    "SMC 0x400000", "πηνίο ασύρματης φόρτισης", "wireless charging coil"
                },
                {
                    "14-pro-same-code-not-mapped",
                    "iPhone15,2",
                    "SMC PANIC - ASSERTION FAILED; SMC BSC failure; "
                        + "S.sensor array 0 - 4 is 0x0, 0x400000, 0x0, 0x0, 0x0",
                    "SMC 0x400000", "δεν υπάρχει ασφαλής", "no sufficiently reliable"
                },
                {
                    "13-pro-charge-port",
                    "iPhone14,2",
                    "SMC PANIC - ASSERTION FAILED; SMC BSC failure; "
                        + "S.sensor array 0 - 4 is 0x0, 0x800, 0x0, 0x0, 0x0",
                    "SMC 0x800", "θύρα φόρτισης", "charging port"
                },
                {
                    "16-pro-decimal-code",
                    "iPhone17,1",
                    "SMC PANIC - ASSERTION FAILED; SMC BSC failure; "
                        + "S.sensor array 0 - 4 is 0, 3145728, 0, 0, 0",
                    "SMC 0x300000", "θύρα φόρτισης", "charging port"
                },
                {
                    "watchdog-no-missing-sensor",
                    "iPhone12,1",
                    "userspace watchdog timeout: no successful checkins from SpringBoard",
                    "WATCHDOG_NO_MISSING_SENSOR", "χωρίς συγκεκριμένο ελλείποντα αισθητήρα", "without a specific missing sensor"
                }
        };
        for (String[] row : cases) {
            Context context = ApplicationProvider.getApplicationContext();
            context.getSharedPreferences("gel_prefs", Context.MODE_PRIVATE)
                    .edit().putBoolean("panic_guide_hidden", true).commit();
            String excerpt = "{\"product\":\"" + row[1] + "\",\"panicString\":\""
                    + "panic(cpu 0 caller 0x0): " + row[2] + "\"}";
            try (ActivityScenario<IPhoneLabsActivity> scenario =
                    ActivityScenario.launch(IPhoneLabsActivity.class)) {
                scenario.onActivity(activity -> {
                    try {
                        TextView view = (TextView) getPrivateField(activity, "txtLog");
                        view.setText("");
                        setPrivateField(activity, "panicLogText", excerpt);
                        setPrivateField(activity, "panicLogLoaded", true);
                        setPrivateField(activity, "panicLogName", "qa-source-backed.txt");
                        setPrivateField(activity, "panicLogCount", 1);
                        Method method = activity.getClass()
                                .getDeclaredMethod("runAllAppleDiagnostics");
                        method.setAccessible(true);
                        method.invoke(activity);
                    } catch (Exception e) {
                        throw new AssertionError("Could not start all 8 Labs for " + row[0], e);
                    }
                });
                AtomicReference<String> logText = new AtomicReference<>("");
                boolean finished = false;
                for (int attempt = 0; attempt < 70; attempt++) {
                    scenario.onActivity(activity -> {
                        try {
                            TextView view = (TextView) getPrivateField(activity, "txtLog");
                            logText.set(view.getText().toString());
                        } catch (Exception e) {
                            throw new AssertionError(e);
                        }
                    });
                    String output = logText.get();
                    if (output.contains("Lab 8 ολοκληρώθηκε")
                            || output.contains("Lab 8 finished")) {
                        finished = true;
                        break;
                    }
                    Thread.sleep(300);
                }
                String result = logText.get();
                assertTrue("8 Labs never finished for " + row[0], finished);
                assertTrue("Missing specific evidence " + row[3] + " for " + row[0]
                                + " / output=" + result, result.contains(row[3]));
                assertTrue("Missing safe area interpretation for " + row[0]
                                + " / output=" + result, (result.contains(row[4]) || result.contains(row[5])));
                assertFalse("Uncalibrated health percentage reappeared",
                        result.contains("75/100"));
                if (row[0].equals("watchdog-no-missing-sensor")) {
                    assertFalse("Invented missing mic2 code on bare watchdog",
                            result.contains("MIC2"));
                }
                Log.i(TAG, "MATRIX_CASE=" + row[0] + " RESULT=PASS CODE=" + row[3]);
            }
        }
    }

}
