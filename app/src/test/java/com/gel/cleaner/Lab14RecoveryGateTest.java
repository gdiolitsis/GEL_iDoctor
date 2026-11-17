package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Exact-source cooldown acceptance for Lab 14A -> 14B -> 14G and 14G -> 14B. */
public final class Lab14RecoveryGateTest {
    private static Lab14RecoveryGate.Decision decide(long elapsedMs, float baseline, float now) {
        return Lab14RecoveryGate.evaluate(200000L, 1000000L, 200000L + elapsedMs,
                1000000L + elapsedMs, baseline, now);
    }

    @Test public void noPreviousBatteryRunStartsNormally() {
        Lab14RecoveryGate.Decision d = Lab14RecoveryGate.evaluate(0L, 0L, 100L, 500L, Float.NaN, Float.NaN);
        assertEquals(Lab14RecoveryGate.Status.READY, d.status);
        assertTrue(d.mayStart());
    }

    @Test public void justFinishedRunRequiresFullFiveMinutes() {
        Lab14RecoveryGate.Decision d = decide(0L, 35f, 38f);
        assertEquals(Lab14RecoveryGate.Status.WAIT_TIME, d.status);
        assertEquals(300000L, d.remainingMs);
        assertFalse(d.mayStart());
        d = decide(299999L, 35f, 35.5f);
        assertEquals(Lab14RecoveryGate.Status.WAIT_TIME, d.status);
        assertEquals(1L, d.remainingMs);
    }

    @Test public void fiveMinutesPlusTemperatureRecoveryOpensGate() {
        assertTrue(decide(300000L, 35f, 37f).mayStart());
        assertTrue(decide(360000L, 35f, 33f).mayStart());
    }

    @Test public void aFiveMinuteWaitDoesNotReplaceThermalCheck() {
        assertEquals(Lab14RecoveryGate.Status.WAIT_TEMPERATURE, decide(600000L, 35f, 37.1f).status);
        assertEquals(Lab14RecoveryGate.Status.WAIT_TEMPERATURE, decide(600000L, 44f, 45f).status);
        assertEquals(Lab14RecoveryGate.Status.WAIT_TEMPERATURE, decide(600000L, 42f, 44.9f).status);
    }

    @Test public void unavailableThermometerRemainsUnknownNotGreen() {
        assertEquals(Lab14RecoveryGate.Status.TEMPERATURE_UNAVAILABLE,
                decide(300000L, Float.NaN, 35f).status);
        assertEquals(Lab14RecoveryGate.Status.TEMPERATURE_UNAVAILABLE,
                decide(300000L, 35f, Float.NaN).status);
    }

    @Test public void monotonicClockSurvivesProcessRestartAndWallClockDisagreementWithinTolerance() {
        // A process restart does not reset elapsedRealtime. Use it, not process uptime.
        Lab14RecoveryGate.Decision d = Lab14RecoveryGate.evaluate(
                500000L, 1000000L, 560000L, 1060000L, 34f, 35f);
        assertEquals(240000L, d.remainingMs);
        assertEquals(Lab14RecoveryGate.Status.WAIT_TIME, d.status);
    }

    @Test public void rebootUsesWallClockFallbackAndStillChecksThermal() {
        Lab14RecoveryGate.Decision d = Lab14RecoveryGate.evaluate(
                5000000L, 9000000L, 20000L, 9012000L, 35f, 35f);
        assertEquals(Lab14RecoveryGate.Status.WAIT_TIME, d.status);
        assertEquals(288000L, d.remainingMs);
        assertEquals(Lab14RecoveryGate.Status.READY,
                Lab14RecoveryGate.evaluate(5000000L, 9000000L, 70000L,
                        9400000L, 35f, 35.2f).status);
    }

    @Test public void wiringTracksStartAndStopAndGatesBothEntryPaths() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String s = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        assertTrue(s.contains("lab14RecoveryMarkStart(\"14A\", startBatteryTemp);"));
        assertTrue(s.contains("lab14RecoveryMarkStart(gamaMode ? \"14G\" : \"14B\", startTemp[0]);"));
        assertTrue(s.contains("lab14RecoveryMarkStop();"));
        assertTrue(s.contains("if (!lab14RecoveryDecision().mayStart())"));
        assertTrue(s.contains("lab14ShowRecoveryWaitDialog(gamaMode);"));
        assertTrue(s.contains("lab14ShowRecoveryWaitDialog(isLab14GamaMode);"));
        assertTrue(s.contains("getSharedPreferences(LAB14_RECOVERY_PREFS, MODE_PRIVATE)"));
        assertTrue(s.contains("refresh.postDelayed(this, 1000L);"));
        assertTrue(s.contains("lab14RecoveryDialog.dismiss();"));
    }
}
