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

    private static Lab29FactorySpecAudit.Row row(
            Lab29FactorySpecAudit.Report report, String english) {
        for (Lab29FactorySpecAudit.Row r : report.rows) {
            if (english.equals(r.english)) return r;
        }
        throw new AssertionError("Missing comparison row: " + english);
    }

    @Test public void chipsetRequiresRealSocIdentifierNotGenericQualcommHardware() {
        Lab29FactorySpecAudit.Measurements m = sample();
        m.socModel = "SM4450";
        assertEquals(Lab29FactorySpecAudit.Verdict.MATCH,
                row(Lab29FactorySpecAudit.evaluate(m), "Chipset model").verdict);
        m.socModel = "unknown";
        m.cpuHardware = "qcom";
        assertEquals(Lab29FactorySpecAudit.Verdict.UNKNOWN,
                row(Lab29FactorySpecAudit.evaluate(m), "Chipset model").verdict);
        assertTrue(row(Lab29FactorySpecAudit.evaluate(m), "Chipset model")
                .observed.contains("qcom"));
    }

    @Test public void adrenoMustBeActuallyReportedAndVisibleInTheResult() {
        Lab29FactorySpecAudit.Measurements m = sample();
        m.gpuName = "Adreno (TM) 613";
        Lab29FactorySpecAudit.Row gpu = row(Lab29FactorySpecAudit.evaluate(m), "GPU model");
        assertEquals(Lab29FactorySpecAudit.Verdict.MATCH, gpu.verdict);
        assertTrue(gpu.observed.contains("Adreno"));
        m.gpuName = "qcom";
        m.gpuDriver = "N/A";
        assertEquals(Lab29FactorySpecAudit.Verdict.UNKNOWN,
                row(Lab29FactorySpecAudit.evaluate(m), "GPU model").verdict);
        assertTrue(row(Lab29FactorySpecAudit.evaluate(m), "GPU model")
                .observed.contains("qcom"));
    }

    @Test public void cameraJpegModesAreShownButDoNotCertifyBinnedSensorResolution() {
        Lab29FactorySpecAudit.Measurements m = sample();
        m.maxRearJpegPixels = 50_000_000L;
        m.maxFrontJpegPixels = 5_000_000L;
        Lab29FactorySpecAudit.Report r = Lab29FactorySpecAudit.evaluate(m);
        assertEquals(Lab29FactorySpecAudit.Verdict.MATCH,
                row(r, "Main camera resolution").verdict);
        assertEquals(Lab29FactorySpecAudit.Verdict.MATCH,
                row(r, "Selfie camera resolution").verdict);
        assertTrue(row(r, "Main camera resolution").observed.contains("max JPEG output"));
        m.maxRearJpegPixels = 12_500_000L; // 50MP sensors can expose 12.5MP binning.
        assertEquals(Lab29FactorySpecAudit.Verdict.UNKNOWN,
                row(Lab29FactorySpecAudit.evaluate(m), "Main camera resolution").verdict);
        assertEquals(Lab29FactorySpecAudit.Verdict.UNKNOWN,
                row(Lab29FactorySpecAudit.evaluate(m), "Depth camera resolution").verdict);
    }

    @Test public void onlyBothReportedWifiCapabilityBandsMakeDualBandMatch() {
        Lab29FactorySpecAudit.Measurements m = sample();
        m.wifiFrequencyMhz = 5180;
        Lab29FactorySpecAudit.Row partial = row(Lab29FactorySpecAudit.evaluate(m),
                "Wi-Fi supported bands");
        assertEquals(Lab29FactorySpecAudit.Verdict.UNKNOWN, partial.verdict);
        assertTrue(partial.observed.contains("5180"));
        m.wifi24GHzSupported = true;
        m.wifi5GHzSupported = true;
        assertEquals(Lab29FactorySpecAudit.Verdict.MATCH,
                row(Lab29FactorySpecAudit.evaluate(m), "Wi-Fi supported bands").verdict);
        m.wifi24GHzSupported = null;
        assertEquals(Lab29FactorySpecAudit.Verdict.UNKNOWN,
                row(Lab29FactorySpecAudit.evaluate(m), "Wi-Fi supported bands").verdict);
    }

    @Test public void availableNfcFmAndBatteryNominalReadingsAreExplicit() {
        Lab29FactorySpecAudit.Measurements m = sample();
        m.nfcSupported = true;
        m.fmFeatureReported = true;
        m.batteryDesignMah = 5000L;
        Lab29FactorySpecAudit.Report r = Lab29FactorySpecAudit.evaluate(m);
        assertEquals(Lab29FactorySpecAudit.Verdict.MATCH,
                row(r, "NFC (market dependent)").verdict);
        assertEquals(Lab29FactorySpecAudit.Verdict.MATCH,
                row(r, "FM radio").verdict);
        assertEquals(Lab29FactorySpecAudit.Verdict.MATCH,
                row(r, "Factory battery capacity").verdict);
        m.batteryDesignMah = 3900L;
        assertEquals(Lab29FactorySpecAudit.Verdict.UNKNOWN,
                row(Lab29FactorySpecAudit.evaluate(m), "Factory battery capacity").verdict);
        assertTrue(row(Lab29FactorySpecAudit.evaluate(m), "Factory battery capacity")
                .observed.contains("3900"));
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

    @Test public void batteryReportedFullChargeIsNotMistakenForFactoryDesign() {
        Lab29FactorySpecAudit.Measurements m = sample();
        m.batteryDesignMah = -1L;
        m.batteryChargeFullMah = 4500L;
        m.batteryChargeNowMah = 2550L;
        m.batteryLevelPercent = 57;
        Lab29FactorySpecAudit.Row r = row(Lab29FactorySpecAudit.evaluate(m),
                "Factory battery capacity");
        assertEquals(Lab29FactorySpecAudit.Verdict.UNKNOWN, r.verdict);
        assertTrue(r.observed.contains("4500 mAh"));
        assertTrue(r.observed.contains("full-charge counter"));
        assertTrue(r.noteGreek.contains("90%"));
        assertTrue(r.noteGreek.contains("δεν")); // no counterfeiting verdict
    }

    @Test public void batteryRemainingMahDividedBySocIsExplicitlyOnlyAnEstimate() {
        Lab29FactorySpecAudit.Measurements m = sample();
        m.batteryChargeNowMah = 2250L;
        m.batteryLevelPercent = 50;
        Lab29FactorySpecAudit.Row r = row(Lab29FactorySpecAudit.evaluate(m),
                "Factory battery capacity");
        assertTrue(r.observed.contains("4500 mAh"));
        assertTrue(r.observed.contains("percentage estimate"));
        assertEquals(Lab29FactorySpecAudit.Verdict.UNKNOWN, r.verdict);
        m.batteryLevelPercent = 5; // too low for extrapolation
        assertTrue(row(Lab29FactorySpecAudit.evaluate(m), "Factory battery capacity")
                .observed.isEmpty());
        m.batteryLevelPercent = 50;
        m.batteryChargeNowMah = 0L; // no physical reading
        assertTrue(row(Lab29FactorySpecAudit.evaluate(m), "Factory battery capacity")
                .observed.isEmpty());
    }

    @Test public void usbAudioJackAndFmUnknownNeverMeanAbsent() {
        Lab29FactorySpecAudit.Report r = Lab29FactorySpecAudit.evaluate(sample());
        assertEquals(Lab29FactorySpecAudit.Verdict.UNKNOWN,
                row(r, "USB connector").verdict);
        assertTrue(row(r, "USB connector").noteGreek.contains("δεν σημαίνει"));
        assertEquals(Lab29FactorySpecAudit.Verdict.UNKNOWN,
                row(r, "3.5 mm jack").verdict);
        assertTrue(row(r, "3.5 mm jack").noteEnglish.contains("not mean absent"));
        assertEquals(Lab29FactorySpecAudit.Verdict.UNKNOWN,
                row(r, "FM radio").verdict);
        assertTrue(row(r, "FM radio").noteEnglish.contains("does not prove"));
        assertEquals(0, r.deviations);
    }

}
