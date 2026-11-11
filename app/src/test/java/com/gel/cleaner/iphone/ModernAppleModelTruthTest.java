package com.gel.cleaner.iphone;

import org.junit.Test;
import static org.junit.Assert.*;

/** Manufacturer-sourced reference contract; this is not proof of physical hardware health. */
public class ModernAppleModelTruthTest {
    private AppleDeviceSpec known(String name) {
        AppleDeviceSpec spec = AppleSpecs.get(name);
        assertEquals("iphone", spec.type);
        assertEquals(name, spec.model);
        assertTrue(spec.notes.contains("https://"));
        return spec;
    }

    @Test public void officialModernLineupResolvesWithoutFallback() {
        for (String model : new String[]{
                "iPhone 16e", "iPhone 17", "iPhone 17 Pro", "iPhone 17 Pro Max",
                "iPhone Air", "iPhone 17e", "iPhone 18 Pro", "iPhone 18 Pro Max",
                "iPhone Duo"}) known(model);
    }

    @Test public void eighteenBaseHasNotBeenFabricated() {
        assertEquals("unknown", AppleSpecs.get("iPhone 18").type);
        assertEquals("unknown", AppleSpecs.get("iPhone 18 Plus").type);
        assertEquals("unknown", AppleSpecs.get("iPhone 17 Plus").type);
    }

    @Test public void duoBiometricsAndScreensAreDistinct() {
        AppleDeviceSpec d = known("iPhone Duo");
        assertEquals("A20 Pro", d.soc);
        assertEquals(7, d.gpuCores);
        assertTrue(d.hasTouchID);
        assertFalse(d.hasFaceID);
        assertEquals("Touch ID (side button)", d.biometrics);
        assertTrue(d.screen.contains("inner"));
        assertTrue(d.screen.contains("outer"));
        assertTrue(d.resolution.contains("inner"));
        assertTrue(d.resolution.contains("outer"));
    }

    @Test public void airDoesNotInheritProTelephotoOrGpu() {
        AppleDeviceSpec air = known("iPhone Air");
        AppleDeviceSpec pro = known("iPhone 17 Pro");
        assertEquals("A19 Pro", air.soc);
        assertEquals(5, air.gpuCores);
        assertEquals(6, pro.gpuCores);
        assertNull(air.cameraTele);
        assertNull(air.cameraUltraWide);
        assertEquals("eSIM only (no physical SIM)", air.simSlots);
    }

    @Test public void seventeenDisplayAndChipAreNotFromSixteen() {
        AppleDeviceSpec d = known("iPhone 17");
        assertEquals("A19", d.soc);
        assertEquals("6.3\"", d.screen);
        assertEquals("Adaptive ProMotion, up to 120 Hz", d.refreshRate);
        assertEquals(5, d.gpuCores);
    }

    @Test public void seventeenEIsNotSeventeenBase() {
        AppleDeviceSpec d = known("iPhone 17e");
        assertEquals("2026", d.year);
        assertEquals("A19", d.soc);
        assertEquals(4, d.gpuCores);
        assertNull(d.cameraUltraWide);
        assertNull(d.cameraTele);
        assertEquals("Apple C1X", d.modem);
    }

    @Test public void sixteenEHasItsOwnRealCameraAndChip() {
        AppleDeviceSpec d = known("iPhone 16e");
        assertEquals("2025", d.year);
        assertEquals("A18", d.soc);
        assertEquals(4, d.gpuCores);
        assertNull(d.cameraUltraWide);
        assertNull(d.cameraTele);
    }

    @Test public void eighteenProCamerasAndGpuFollowAppleSource() {
        for (String name : new String[]{"iPhone 18 Pro", "iPhone 18 Pro Max"}) {
            AppleDeviceSpec d = known(name);
            assertEquals("2026", d.year);
            assertEquals("A20 Pro", d.soc);
            assertEquals(7, d.gpuCores);
            assertTrue(d.cameraMain.contains("variable aperture"));
            assertTrue(d.cameraTele.contains("48 MP"));
            assertEquals("Apple C2", d.modem);
        }
    }

    @Test public void maxDoesNotInventHigherGpuThanPro() {
        assertEquals(known("iPhone 17 Pro").gpuCores, known("iPhone 17 Pro Max").gpuCores);
        assertEquals(known("iPhone 18 Pro").gpuCores, known("iPhone 18 Pro Max").gpuCores);
    }

    @Test public void unverifiedMemoryAndBatteryAreNotSyntheticMeasurements() {
        for (String name : new String[]{
                "iPhone 16e", "iPhone 17", "iPhone 17 Pro", "iPhone 17 Pro Max",
                "iPhone Air", "iPhone 17e", "iPhone 18 Pro", "iPhone 18 Pro Max",
                "iPhone Duo"}) {
            AppleDeviceSpec d = known(name);
            assertNull(name, d.ram);
            assertNull(name, d.batteryMah);
            assertNull(name, d.batteryVoltage);
            assertNull(name, d.batteryWh);
            assertFalse(name, d.hasBatterySpec());
            assertNull(name, d.getBatteryEnergyWh());
        }
    }

    @Test public void unknownBrandOrSuffixCannotInheritAnActualModel() {
        assertEquals("unknown", AppleSpecs.get("iPhone Duo Pro").type);
        assertEquals("unknown", AppleSpecs.get("iPhone Air Max").type);
        assertEquals("unknown", AppleSpecs.get("X iPhone 18 Pro repair").type);
        assertEquals("unknown", AppleSpecs.get("iPhone 18 Pro Test").type);
    }
}
