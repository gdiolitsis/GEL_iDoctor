package com.gel.cleaner;

import org.junit.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

/** Six measured sectors, 15 potential pairs, independent OS thermal state. */
public final class Lab16ThermalCorrelationTest {
    private static float[] temps(float battery, float cpu, float gpu,
                                 float charger, float pmic, float surface) {
        return new float[] { battery, cpu, gpu, charger, pmic, surface };
    }

    @Test public void ownerScreenshotAllThermalReadingsStayNormal() {
        Lab16ThermalCorrelation.Summary d = Lab16ThermalCorrelation.evaluate(
                temps(33f,42.3f,38.6f,36.5f,Float.NaN,Float.NaN),0,false);
        assertEquals(4,d.measuredCount);
        assertEquals(0,d.elevatedCount);
        assertEquals(0,d.elevatedPairs.size());
        assertFalse(d.hasWarning());
        assertTrue(d.hasChipReading());
        assertEquals(Lab16ThermalCorrelation.Level.NORMAL,
                d.levels[Lab16ThermalCorrelation.CPU]);
    }

    @Test public void cpuPlusChargingZoneIsCorrelatedWithoutAssumedCausality() {
        Lab16ThermalCorrelation.Summary d = Lab16ThermalCorrelation.evaluate(
                temps(33f,77f,42f,53f,Float.NaN,Float.NaN),0,false);
        assertEquals(2,d.elevatedCount);
        assertEquals(1,d.elevatedPairs.size());
        assertEquals("CPU + Περιοχή φόρτισης", d.elevatedPairs.get(0).names(true));
        assertTrue(d.elevatedPairs.get(0).details(true).contains("Δεν αποδεικνύεται αιτιότητα"));
        assertTrue(d.combinedMessage(true).contains("CPU 77.0°C"));
        assertTrue(d.combinedMessage(true).contains("Περιοχή φόρτισης 53.0°C"));
    }

    @Test public void batteryAndChargerPairDescribesCoOccurrenceNotProofOfHardwareFault() {
        Lab16ThermalCorrelation.Summary d = Lab16ThermalCorrelation.evaluate(
                temps(44f,43f,38f,56f,Float.NaN,Float.NaN),1,false);
        assertEquals(1,d.elevatedPairs.size());
        assertTrue(d.elevatedPairs.get(0).details(true).contains("θέρμανση μπαταρίας"));
        assertFalse(d.elevatedPairs.get(0).details(true).contains("αποδείχθηκε"));
    }

    @Test public void allSixElevatedProducesExactlyAllFifteenUniquePairs() {
        Lab16ThermalCorrelation.Summary d = Lab16ThermalCorrelation.evaluate(
                temps(43f,76f,79f,55f,67f,44f),0,false);
        assertEquals(6,d.elevatedCount);
        assertEquals(15,d.elevatedPairs.size());
        Set<String> unique = new HashSet<>();
        for (Lab16ThermalCorrelation.Pair p : d.elevatedPairs) {
            assertTrue(p.first < p.second);
            assertTrue(unique.add(p.first+"-"+p.second));
        }
        assertTrue(unique.contains("1-3")); // CPU + charger
        assertTrue(unique.contains("3-4")); // charger + PMIC
        assertTrue(unique.contains("0-5")); // battery + surface
        for (Lab16ThermalCorrelation.Pair pair : d.elevatedPairs) {
            assertFalse("Missing domain-specific explanation for " + pair.names(true),
                    pair.details(true).contains("Δύο θερμικοί τομείς"));
            assertFalse("English pair fallback for " + pair.names(false),
                    pair.details(false).contains("Two thermal areas"));
        }
    }

    @Test public void noSectorIsInferredFromMissingData() {
        float[] raw=Lab16ThermalCorrelation.emptyTemperatures();
        Lab16ThermalCorrelation.capture(raw,"CPU",83f);
        Lab16ThermalCorrelation.capture(raw,"Charger",Float.NaN);
        Lab16ThermalCorrelation.capture(raw,"Unknown sensor",95f);
        Lab16ThermalCorrelation.Summary d=Lab16ThermalCorrelation.evaluate(raw,-1,false);
        assertEquals(1,d.measuredCount);
        assertEquals(1,d.elevatedCount);
        assertEquals(0,d.elevatedPairs.size());
        assertEquals(Lab16ThermalCorrelation.Level.UNKNOWN,
                d.levels[Lab16ThermalCorrelation.CHARGER]);
        assertFalse(d.combinedMessage(true).contains("Περιοχή φόρτισης"));
    }

    @Test public void alreadyReportedThermalZonesAreNeverWarnedAsAdditionalHiddenSources() {
        for (String name : new String[] {
                "battery", "batt_therm", "cpu_thermal", "GPU-therm",
                "pmic", "usb_port", "charger", "skin", "shell"
        }) {
            assertTrue(name, Lab16ThermalCorrelation.alreadyDisplayedZoneType(name));
        }
        assertFalse(Lab16ThermalCorrelation.alreadyDisplayedZoneType("modem"));
        assertFalse(Lab16ThermalCorrelation.alreadyDisplayedZoneType("soc_misc"));
        assertFalse(Lab16ThermalCorrelation.alreadyDisplayedZoneType(null));
    }

    @Test public void duplicateVendorZonesKeepHottestAvailableReading() {
        float[] t=Lab16ThermalCorrelation.emptyTemperatures();
        Lab16ThermalCorrelation.capture(t,"Charger",47f);
        Lab16ThermalCorrelation.capture(t,"Charger",56f);
        Lab16ThermalCorrelation.capture(t,"Charger",51f);
        assertEquals(56f,t[Lab16ThermalCorrelation.CHARGER],0f);
    }

    @Test public void batterySurfaceAndChipAreNotSubjectToOneUniversal45CRule() {
        assertEquals(Lab16ThermalCorrelation.Level.ELEVATED,
                Lab16ThermalCorrelation.level(Lab16ThermalCorrelation.BATTERY,44f));
        assertEquals(Lab16ThermalCorrelation.Level.NORMAL,
                Lab16ThermalCorrelation.level(Lab16ThermalCorrelation.CPU,60f));
        assertEquals(Lab16ThermalCorrelation.Level.NORMAL,
                Lab16ThermalCorrelation.level(Lab16ThermalCorrelation.GPU,65f));
        assertEquals(Lab16ThermalCorrelation.Level.ELEVATED,
                Lab16ThermalCorrelation.level(Lab16ThermalCorrelation.SURFACE,44f));
    }

    @Test public void onlyOsSevereClaimsMeasuredSystemRestriction() {
        float[] noValues=Lab16ThermalCorrelation.emptyTemperatures();
        Lab16ThermalCorrelation.Summary none=Lab16ThermalCorrelation.evaluate(noValues,-1,false);
        assertFalse(none.hasWarning());
        assertFalse(none.hasChipReading());
        Lab16ThermalCorrelation.Summary moderate=Lab16ThermalCorrelation.evaluate(noValues,2,false);
        assertTrue(moderate.hasWarning());
        assertFalse(moderate.hasSevereAndroidLimit());
        assertTrue(moderate.combinedMessage(true).contains("ενεργό θερμικό περιορισμό"));
        Lab16ThermalCorrelation.Summary severe=Lab16ThermalCorrelation.evaluate(noValues,3,false);
        assertTrue(severe.hasSevereAndroidLimit());
        assertTrue(severe.combinedMessage(true).contains("καταγεγραμμένη επίπτωση στη λειτουργία"));
    }

    @Test public void hiddenHotZoneStillWarningsWithNoNamedSensors() {
        Lab16ThermalCorrelation.Summary d=Lab16ThermalCorrelation.evaluate(
                Lab16ThermalCorrelation.emptyTemperatures(),-1,true);
        assertTrue(d.hasWarning());
        assertEquals(0,d.elevatedPairs.size());
        assertTrue(d.combinedMessage(true).contains("άλλη προσβάσιμη θερμική ζώνη"));
    }
}
