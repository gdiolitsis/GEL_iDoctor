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

    @Test public void realUfsLifetimeBandsDoNotBecomeExactSoH() {
        GelShizukuEvidence.StorageWear w = GelShizukuEvidence.parseStorageWear("UFS|1|5|1");
        assertNotNull(w);
        assertFalse(w.hasWearAlert());
        assertEquals("UFS", w.technology);
        assertEquals("0-10% of estimated endurance used",
                GelShizukuEvidence.StorageWear.lifetimeInterval(w.lifetimeA));
        assertEquals("40-50% of estimated endurance used",
                GelShizukuEvidence.StorageWear.lifetimeInterval(w.lifetimeB));
        assertEquals("over 100% of estimated endurance used",
                GelShizukuEvidence.StorageWear.lifetimeInterval(11));
    }

    @Test public void ufsEolWarningAndReservedBytesAreGuarded() {
        assertTrue(GelShizukuEvidence.parseStorageWear("UFS|4|7|2").hasWearAlert());
        GelShizukuEvidence.StorageWear emmc =
                GelShizukuEvidence.parseStorageWear("EMMC|2|4|1");
        assertNotNull(emmc);
        assertEquals("EMMC", emmc.technology);
        assertFalse(emmc.hasWearAlert());
        assertTrue(GelShizukuEvidence.parseStorageWear("EMMC|8|9|3").hasWearAlert());
        assertTrue(GelShizukuEvidence.parseStorageWear("UFS|10|0|1").hasWearAlert());
        assertNull(GelShizukuEvidence.parseStorageWear("UFS|0|0|0"));
        assertNull(GelShizukuEvidence.parseStorageWear("UFS|255|0|1"));
        assertNull(GelShizukuEvidence.parseStorageWear("UFS|2|0|99"));
        assertNull(GelShizukuEvidence.parseStorageWear("UNAVAILABLE|UFS_HEALTH_NOT_EXPOSED_TO_SHELL"));
    }

    @Test public void onlyExplicitMemoryKillEventCountsAreAccepted() {
        assertEquals(3, GelShizukuEvidence.parseLmkCount("LMK|3"));
        assertEquals(0, GelShizukuEvidence.parseLmkCount("LMK|0"));
        assertEquals(-1, GelShizukuEvidence.parseLmkCount("UNAVAILABLE|LOG_RESTRICTED"));
        assertEquals(-1, GelShizukuEvidence.parseLmkCount("LMK|999"));
        assertEquals(-1, GelShizukuEvidence.parseLmkCount("LMK|-1"));
    }

    @Test public void sensitiveAppOpsMustContainAttributedAccessTimestamps() {
        java.util.List<GelShizukuEvidence.AppOpAccess> observed =
                GelShizukuEvidence.parseAppOps("APPOPS|SAMPLED\n"
                + "com.example.camera|CAMERA|2026-11-18 13:20:00\n"
                + "com.example.audio|RECORD_AUDIO|2026-11-18 13:22:15\n"
                + "com.fake|CAMERA|../private\n"
                + "com.fake|SMS|2026-11-18 13:22:15");
        assertEquals(2, observed.size());
        assertEquals("com.example.camera", observed.get(0).packageName);
        assertEquals("CAMERA", observed.get(0).operation);
        assertTrue(GelShizukuEvidence.parseAppOps("UNAVAILABLE|APPOPS_RESTRICTED").isEmpty());
    }

}
