package com.gel.cleaner;
import org.junit.Test;
import static org.junit.Assert.*;

public class SensorRunGateTest {
    @Test public void completedSampleCannotBeReplayed() {
        SensorRunGate gate = new SensorRunGate();
        long token = gate.begin();
        assertTrue(gate.complete(token));
        assertFalse(gate.complete(token));
    }

    @Test public void backgroundCancelsStaleSuccess() {
        SensorRunGate gate = new SensorRunGate();
        long token = gate.begin();
        gate.cancel();
        assertFalse(gate.complete(token));
        assertFalse(gate.isActive());
    }

    @Test public void oldCallbackCannotCompleteNewSampling() {
        SensorRunGate gate = new SensorRunGate();
        long stale = gate.begin();
        gate.cancel();
        long current = gate.begin();
        assertTrue(current > stale);
        assertFalse(gate.complete(stale));
        assertTrue(gate.complete(current));
    }

    @Test public void duplicateStartCannotScheduleConcurrentProbes() {
        SensorRunGate gate = new SensorRunGate();
        assertTrue(gate.begin() > 0L);
        assertEquals(-1L, gate.begin());
    }
}
