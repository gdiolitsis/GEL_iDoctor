package com.gel.cleaner;
import org.junit.Test;
import static org.junit.Assert.*;

public class GelMiniPolicyTest {
    @Test public void temperatureBoundariesAndMissingReading() {
        assertEquals(0, GelMiniPolicy.temperatureLevel(Double.NaN));
        assertEquals(0, GelMiniPolicy.temperatureLevel(44.9));
        assertEquals(1, GelMiniPolicy.temperatureLevel(45));
        assertEquals(2, GelMiniPolicy.temperatureLevel(50));
    }
    @Test public void storageUsesAbsoluteAndRelativePressureWithoutInventingMissingData() {
        long gb = 1024L * 1024 * 1024;
        assertEquals(0, GelMiniPolicy.storageLevel(-1, 100 * gb));
        assertEquals(0, GelMiniPolicy.storageLevel(0, 0));
        assertEquals(0, GelMiniPolicy.storageLevel(101 * gb, 100 * gb));
        assertEquals(0, GelMiniPolicy.storageLevel(10 * gb, 100 * gb));
        assertEquals(1, GelMiniPolicy.storageLevel(5 * gb, 100 * gb));
        assertEquals(2, GelMiniPolicy.storageLevel(2 * gb, 100 * gb));
        assertEquals(1, GelMiniPolicy.storageLevel(gb, 8 * gb));
        assertEquals(2, GelMiniPolicy.storageLevel(gb / 2, 8 * gb));
    }
    @Test public void repeatSuppressionRecoveryEscalationAndIndependentCooldown() {
        long now = 2 * GelMiniPolicy.DAY;
        assertTrue(GelMiniPolicy.shouldNotify(1, 0, 0, now));
        assertFalse(GelMiniPolicy.shouldNotify(1, 1, now - 1000, now));
        assertTrue(GelMiniPolicy.shouldNotify(2, 1, now - 1000, now));
        assertTrue(GelMiniPolicy.shouldNotify(1, 0, now - 1000, now));
        assertTrue(GelMiniPolicy.shouldNotify(1, 1, now - GelMiniPolicy.DAY, now));
        assertFalse(GelMiniPolicy.shouldNotify(0, 1, 0, now));
    }
}
