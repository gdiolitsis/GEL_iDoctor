package com.gel.cleaner;
import android.os.BatteryManager;
import org.junit.Test;
import static org.junit.Assert.*;
public class GelMiniSnapshotTest {
    @Test public void unavailableReadingsAreNotACompleteHealthyCheck() {
        GelMiniSnapshot s = new GelMiniSnapshot();
        assertFalse(s.complete());
        s.temperature = 30;
        s.ramKnown = true;
        assertFalse(s.complete());
        s.storageKnown = true;
        assertTrue(s.complete());
        // Optional battery health is not treated as a fault when unavailable.
        assertEquals(0, s.healthLevel());
    }
    @Test public void batteryHealthOnlyReportsExplicitSystemWarnings() {
        GelMiniSnapshot s = new GelMiniSnapshot();
        s.health = BatteryManager.BATTERY_HEALTH_GOOD;
        assertEquals(0, s.healthLevel());
        s.health = BatteryManager.BATTERY_HEALTH_OVERHEAT;
        assertEquals(1, s.healthLevel());
        s.health = BatteryManager.BATTERY_HEALTH_DEAD;
        assertEquals(1, s.healthLevel());
        s.health = BatteryManager.BATTERY_HEALTH_COLD;
        assertEquals(1, s.healthLevel());
    }
}
