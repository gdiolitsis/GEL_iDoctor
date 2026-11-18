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

    @Test public void contradictoryCounterAndCurrentDoNotProduceHealthGrade() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        for (int i = 0; i <= 300; i++)
            t.addSample(i * 1000L, 4000 - i * 4 / 3, -600f, false);
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertFalse(r.valid);
        assertEquals("COUNTER_CURRENT_CONFLICT", r.reason);
    }

    @Test public void gapsDoNotCountAsMeasuredTime() {
        Lab14DischargeTrace t = new Lab14DischargeTrace();
        for (int i = 0; i <= 300; i += 10)
            t.addSample(i * 1000L, 4000 - i / 3, -1200f, false);
        Lab14DischargeTrace.Result r = t.evaluate(300000L);
        assertFalse(r.valid);
        assertTrue(r.counterCoveragePct < 70d);
        assertTrue(r.currentCoveragePct < 70d);
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
