package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/** Complete-time coverage and consistency rules for Lab14A/B/Gamma. */
public final class Lab14DischargeTraceTest {
    @Test public void fullFiveMinutesProducesMeasuredRate() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        for (int i = 0; i <= 300; i++)
            t.addSample(i * 1000L, 4000 - i / 3, -1200f, false);
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertTrue(r.reason, r.valid);
        assertEquals("CHARGE_COUNTER", r.source);
        assertEquals(1200d, r.rateMahPerHour, 0.1d);
        assertTrue(r.counterCoveragePct >= 99d);
    }

    @Test public void last135SecondsCannotRepresentFiveMinutes() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        for (int i = 165; i <= 300; i++)
            t.addSample(i * 1000L, 4000 - i / 3, -1200f, false);
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertFalse(r.valid);
        assertEquals("INCOMPLETE_DURATION", r.reason);
    }

    @Test public void missingCounterCanUseTimeWeightedCurrent() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        for (int i = 0; i <= 300; i++)
            t.addSample(i * 1000L, -1L, -740f, false);
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertTrue(r.reason, r.valid);
        assertEquals("CURRENT_INTEGRATION", r.source);
        assertEquals(740d, r.rateMahPerHour, 0.01d);
    }

    @Test public void counterWinsOverNoisyCurrentWithExplicitConflictFlag() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        for (int i = 0; i <= 300; i++)
            t.addSample(i * 1000L, 4000 - i * 4 / 3, -600f, false);
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertTrue(r.reason, r.valid);
        assertEquals("CHARGE_COUNTER", r.source);
        assertEquals(4800d, r.rateMahPerHour, 0.01d);
        assertEquals("COUNTER_CURRENT_DISAGREEMENT", r.reason);
    }

    @Test public void sparseIntermediateSamplesNeverInvalidateGoodEndpoints() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        for (int i = 0; i <= 300; i += 10)
            t.addSample(i * 1000L, 4000 - i / 3, -1200f, false);
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertTrue(r.reason, r.valid);
        assertEquals("CHARGE_COUNTER", r.source);
        assertEquals(1200d, r.rateMahPerHour, 0.01d);
        assertTrue(r.counterCoveragePct < 70d);
        assertTrue(r.currentCoveragePct < 70d);
    }

    @Test public void onlyTwoEndpointSamplesAreSufficientForMeasuredDelta() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        t.addSample(10000L, 3438L, Float.NaN, false);
        t.addSample(310000L, 3328L, Float.NaN, false);
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertTrue(r.reason, r.valid);
        assertEquals("CHARGE_COUNTER", r.source);
        assertEquals(1320d, r.rateMahPerHour, 0.01d);
        assertEquals(0d, r.currentCoveragePct, 0.01d);
    }

    @Test public void fiveMinuteEverydayAndGamingProfilesShareEndpointRule() {
        for (String profile : new String[]{"14B", "14G"}) {
            Lab14DischargeTrace t = new Lab14DischargeTrace();
            t.addSample(1200L, 3267L, -900f, false);
            t.addSample(301200L, 3187L, -1300f, false);
            Lab14DischargeTrace.Result r = t.evaluate(300000L);
            assertTrue(profile + ": " + r.reason, r.valid);
            assertEquals(profile, "CHARGE_COUNTER", r.source);
            assertEquals(profile, 960d, r.rateMahPerHour, 0.01d);
        }
    }

    @Test public void firstFourMinutesCannotCertifyFiveMinuteTest() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        for (int i = 0; i <= 240; i++)
            t.addSample(i * 1000L, 4000 - i / 3, -1200f, false);
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertFalse("An 80%-length trace must not count as the full test", r.valid);
        assertEquals("INCOMPLETE_DURATION", r.reason);
    }

    @Test public void longIntermediateGapDoesNotEraseMeasuredEndpointDischarge() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        for (int i = 0; i <= 300; i++)
            if (i < 130 || i > 170)
                t.addSample(i * 1000L, 4000 - i / 3, -1200f, false);
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertTrue(r.reason, r.valid);
        assertEquals("CHARGE_COUNTER", r.source);
        assertTrue(r.currentCoveragePct < 90d);
        assertTrue(r.counterCoveragePct < 90d);
    }

    @Test public void chargingDuringTestRejectsEndpoints() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        t.addSample(0L, 3438L, -800f, false);
        t.addSample(150000L, 3400L, 350f, true);
        t.addSample(300000L, 3350L, -800f, false);
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertFalse(r.valid);
        assertEquals("CHARGING_DURING_TEST", r.reason);
    }

    @Test public void upwardCounterDiscontinuityRejectsEndpoints() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        t.addSample(0L, 3438L, -1000f, false);
        t.addSample(1000L, 3455L, -1000f, false);
        t.addSample(300000L, 3338L, -1000f, false);
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertFalse(r.valid);
        assertEquals("UNSTABLE_COUNTER", r.reason);
    }

    @Test public void nearCompleteTraceAllowsBriefMissedPoll() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        for (int i = 0; i <= 300; i++) {
            if (i == 151) continue;
            t.addSample(i * 1000L, 4000 - i / 3, -1200f, false);
        }
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertTrue(r.reason, r.valid);
        assertTrue(r.counterCoveragePct >= 90d);
    }

    @Test public void chargerInsertionInvalidatesDischargeEvidence() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        for (int i = 0; i <= 300; i++)
            t.addSample(i * 1000L, 4000 - i / 3, -1200f, i == 100);
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertFalse(r.valid);
        assertEquals("CHARGING_DURING_TEST", r.reason);
    }
}
