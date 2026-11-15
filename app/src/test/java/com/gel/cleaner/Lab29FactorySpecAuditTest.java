package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab29FactorySpecAuditTest {
    private static Lab29FactorySpecAudit.Measurements sample() {
        Lab29FactorySpecAudit.Measurements m = new Lab29FactorySpecAudit.Measurements();
        m.manufacturer = "Xiaomi";
        m.model = "23076RN8DY";
        m.abi = "arm64-v8a,armeabi-v7a";
        m.cores = 8;
        m.ramMb = 7520;
        m.storageGb = 110.2;
        m.widthPx = 1080;
        m.heightPx = 2460;
        m.activeRefreshHz = 60f;
        m.maxRefreshHz = 90f;
        m.rearCamera = true;
        m.frontCamera = true;
        m.accelerometer = true;
        m.compass = true;
        m.ambientLight = true;
        m.proximity = true;
        return m;
    }

    @Test public void matchingSupportedProfileReportsOnlyMeasuredMatches() {
        Lab29FactorySpecAudit.Report r = Lab29FactorySpecAudit.evaluate(sample());
        assertTrue(r.supportedModel);
        assertTrue(r.matches >= 10);
        assertEquals(0, r.deviations);
        assertTrue(r.unknowns > 0);
        assertTrue(r.officialSource.startsWith("https://www.mi.com/"));
        assertTrue(r.conclusion(true).contains("συγκρίσιμα"));
        assertFalse(r.conclusion(true).contains("πιστοποιείται"));
    }

    @Test public void unsupportedModelDoesNotProduceOemVerdict() {
        Lab29FactorySpecAudit.Measurements m = sample();
        m.model = "UNKNOWN-FAKE-123";
        Lab29FactorySpecAudit.Report r = Lab29FactorySpecAudit.evaluate(m);
        assertFalse(r.supportedModel);
        assertEquals(0, r.matches);
        assertEquals(0, r.deviations);
        assertTrue(r.conclusion(true).contains("Δεν υπάρχει"));
    }

    @Test public void displayResolutionMismatchIsAnObservedDeviation() {
        Lab29FactorySpecAudit.Measurements m = sample();
        m.widthPx = 720;
        m.heightPx = 1600;
        Lab29FactorySpecAudit.Report r = Lab29FactorySpecAudit.evaluate(m);
        assertEquals(1, r.deviations);
        assertTrue(r.conclusion(true).contains("αποκλίσεις"));
    }

    @Test public void lowerActiveRefreshDoesNotAccuseGenuinePanel() {
        Lab29FactorySpecAudit.Measurements m = sample();
        m.activeRefreshHz = 60f;
        m.maxRefreshHz = 60f; // energy saver may mask the 90 Hz mode
        Lab29FactorySpecAudit.Report r = Lab29FactorySpecAudit.evaluate(m);
        assertEquals(0, r.deviations);
    }

    @Test public void virtualSensorsAndUnavailableDataNeverCountAsFailures() {
        Lab29FactorySpecAudit.Measurements m = sample();
        m.proximity = false;
        m.gyroscope = false;
        m.ambientLight = false;
        m.ramMb = -1;
        m.storageGb = Double.NaN;
        Lab29FactorySpecAudit.Report r = Lab29FactorySpecAudit.evaluate(m);
        assertEquals(0, r.deviations);
        assertTrue(r.unknowns >= 5);
    }

    @Test public void marketedRamVariantsAreHandledWithoutHardFailure() {
        Lab29FactorySpecAudit.Measurements m = sample();
        m.ramMb = 3670; // 4 GB marketed
        assertEquals(0, Lab29FactorySpecAudit.evaluate(m).deviations);
        m.ramMb = 1000; // unreadable, reserved or restricted
        assertEquals(0, Lab29FactorySpecAudit.evaluate(m).deviations);
    }

    @Test public void differentManufacturerIsVisibleNotSilentlyGreen() {
        Lab29FactorySpecAudit.Measurements m = sample();
        m.manufacturer = "AnotherVendor";
        assertEquals(1, Lab29FactorySpecAudit.evaluate(m).deviations);
    }

    @Test public void noOemProfileWhenNoModelIsAvailable() {
        assertFalse(Lab29FactorySpecAudit.evaluate(
                new Lab29FactorySpecAudit.Measurements()).supportedModel);
    }
}
