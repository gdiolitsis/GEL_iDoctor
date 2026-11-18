package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/** Shizuku evidence never silently turns estimates or unavailable data into facts. */
public final class GelShizukuEvidenceTest {
    @Test public void genuineGaugeFullAndDesignRemainDistinct() {
        GelShizukuEvidence.Capacity c = GelShizukuEvidence.parseCapacity(
                "CAPACITY|4350|5000|BATTERY|BMS");
        assertNotNull(c);
        assertEquals(4350, c.fullMah);
        assertEquals(5000, c.designMah);
        assertEquals(87, c.estimatedSoHPercent());
    }
    @Test public void onlyDesignCannotProveCurrentFullCapacity() {
        GelShizukuEvidence.Capacity c = GelShizukuEvidence.parseCapacity(
                "CAPACITY|0|5000||BATTERY");
        assertNotNull(c);
        assertFalse(c.hasReportedFull());
        assertEquals(-1, c.estimatedSoHPercent());
    }
    @Test public void missingAndImplausibleCapacityRejected() {
        assertNull(GelShizukuEvidence.parseCapacity("UNAVAILABLE|BLOCKED"));
        assertNull(GelShizukuEvidence.parseCapacity("CAPACITY|50000|5000|BMS|BMS"));
        assertNull(GelShizukuEvidence.parseCapacity("CAPACITY|4350|5000|/data/private|BMS"));
        assertNull(GelShizukuEvidence.parseCapacity("CAPACITY|0|0||"));
    }
    @Test public void deniedCrashAccessIsNotEmptySuccess() {
        assertFalse(GelShizukuEvidence.parseCrash("UNAVAILABLE|RESTRICTED").read);
        GelShizukuEvidence.CrashScan s =
                GelShizukuEvidence.parseCrash("CRASH|READ");
        assertTrue(s.read);
        assertTrue(s.events.isEmpty());
    }
    @Test public void rejectMalformedCrashEventAndPreserveValidOnes() {
        GelShizukuEvidence.CrashScan s = GelShizukuEvidence.parseCrash(
                "CRASH|READ\nsystem_server|WATCHDOG|11-18 12:20:00\n"
              + "bad/path|CRASH|\ncom.android.systemui|CRASH|11-18 12:22:00");
        assertEquals(2, s.events.size());
        assertEquals("system_server", s.events.get(0).process);
    }
    @Test public void thermalAndBootMetadataAreNotHealthScores() {
        assertEquals(3, GelShizukuEvidence.parseThermalStatus("THERMAL_STATUS|3"));
        assertEquals(-1, GelShizukuEvidence.parseThermalStatus("UNAVAILABLE|RESTRICTED"));
        assertEquals("watchdog", GelShizukuEvidence.parseBootReason("BOOT|watchdog"));
        assertNull(GelShizukuEvidence.parseBootReason("BOOT|../../private"));
    }
}
