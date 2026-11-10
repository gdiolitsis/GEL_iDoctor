package com.gel.cleaner;
import org.junit.Test;
import static org.junit.Assert.*;

public class Lab28TemporalEvidenceTest {
    @Test public void firstObservationDoesNotGenerateRiskScore() {
        Lab28TemporalEvidence.Result x = Lab28TemporalEvidence.compare(
                -1, -1, 100000, 1000000, 4000, 3980, 31, 32);
        assertEquals(Lab28TemporalEvidence.State.NO_REFERENCE, x.state);
        assertTrue(Double.isNaN(x.voltageDeltaMv));
    }
    @Test public void twentySecondsIsTooSoon() {
        Lab28TemporalEvidence.Result x = Lab28TemporalEvidence.compare(
                100000, 1000000, 120000, 1020000, 4000, 3950, 31, 32);
        assertEquals(Lab28TemporalEvidence.State.TOO_SOON, x.state);
    }
    @Test public void pairKeepsRealSignAndUnits() {
        Lab28TemporalEvidence.Result x = Lab28TemporalEvidence.compare(
                100000, 1000000, 160000, 1060000, 4050, 3950, 31, 33);
        assertEquals(Lab28TemporalEvidence.State.OBSERVED_PAIR, x.state);
        assertEquals(-100, x.voltageDeltaMv, 0.0001);
        assertEquals(2, x.tempDeltaC, 0.0001);
        assertEquals(60000L, x.elapsedMs);
    }
    @Test public void missingVoltageCanRetainTemperaturePair() {
        Lab28TemporalEvidence.Result x = Lab28TemporalEvidence.compare(
                100000, 1000000, 160000, 1060000, Double.NaN, 4000, 28, 30);
        assertEquals(Lab28TemporalEvidence.State.OBSERVED_PAIR, x.state);
        assertTrue(Double.isNaN(x.voltageDeltaMv));
        assertEquals(2, x.tempDeltaC, 0.0001);
    }
    @Test public void noCommonSignalIsInconclusive() {
        Lab28TemporalEvidence.Result x = Lab28TemporalEvidence.compare(
                100000, 1000000, 160000, 1060000,
                Double.NaN, 4100, Double.NaN, 32);
        assertEquals(Lab28TemporalEvidence.State.NO_COMMON_SIGNALS, x.state);
    }
    @Test public void rebootOrClockJumpInvalidatesHistory() {
        assertEquals(Lab28TemporalEvidence.State.EXPIRED_OR_REBOOTED,
                Lab28TemporalEvidence.compare(
                        100000, 1000000, 50000, 1060000,
                        4050, 3950, 31, 33).state);
        assertEquals(Lab28TemporalEvidence.State.EXPIRED_OR_REBOOTED,
                Lab28TemporalEvidence.compare(
                        100000, 1000000, 160000, 6000000,
                        4050, 3950, 31, 33).state);
    }
    @Test public void stalePairDoesNotBecomeCurrentEvidence() {
        assertEquals(Lab28TemporalEvidence.State.EXPIRED_OR_REBOOTED,
                Lab28TemporalEvidence.compare(
                        100000, 1000000, 7400001, 8300001,
                        4050, 3950, 31, 33).state);
    }
}
