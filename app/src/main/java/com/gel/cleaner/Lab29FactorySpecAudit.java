package com.gel.cleaner;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * LAB 27 — OEM-published device-model specification consistency, NOT OEM part authentication.
 *
 * Data is read through iDoctorEngine: the SAME shared readers already consumed
 * by DeviceInfoInternalActivity and DeviceInfoPeripheralsActivity. No duplicate
 * hardware scanners, IMEI collection, web scraping or permission requests.
 *
 * The reference catalogue is explicit and sourced. For an unlisted SKU the
 * outcome is UNKNOWN, never a fabricated positive comparison.
 */
public final class Lab29FactorySpecAudit {
    private Lab29FactorySpecAudit() {}

    public enum Verdict { MATCH, DEVIATION, UNKNOWN }

    public static final class Row {
        public final String greek;
        public final String english;
        public final String expected;
        public final String observed;
        public final Verdict verdict;
        public final String noteGreek;
        public final String noteEnglish;

        Row(String greek, String english, String expected, String observed,
            Verdict verdict) {
            this(greek, english, expected, observed, verdict, "", "");
        }

        Row(String greek, String english, String expected, String observed,
            Verdict verdict, String noteGreek, String noteEnglish) {
            this.greek = greek;
            this.english = english;
            this.expected = expected;
            this.observed = observed;
            this.verdict = verdict;
            this.noteGreek = noteGreek;
            this.noteEnglish = noteEnglish;
        }
    }

    public static final class Report {
        public final String modelName;
        public final String officialSource;
        public final List<Row> rows;
        public final int matches;
        public final int deviations;
        public final int unknowns;
        public final boolean supportedModel;

        Report(String modelName, String officialSource, List<Row> rows,
               boolean supportedModel) {
            this.modelName = modelName;
            this.officialSource = officialSource;
            this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
            this.supportedModel = supportedModel;
            int ok = 0, diff = 0, missing = 0;
            for (Row row : rows) {
                if (row.verdict == Verdict.MATCH) ok++;
                else if (row.verdict == Verdict.DEVIATION) diff++;
                else missing++;
            }
            matches = ok;
            deviations = diff;
            unknowns = missing;
        }

        public String conclusion(boolean greek) {
            if (!supportedModel) return greek
                    ? "Δεν υπάρχει τεκμηριωμένο εργοστασιακό προφίλ για αυτόν τον κωδικό μοντέλου. Δεν πραγματοποιήθηκε σύγκριση."
                    : "No sourced factory profile is available for this exact model code. No comparison was made.";
            if (matches + deviations == 0) return greek
                    ? "Δεν συγκεντρώθηκαν αρκετά συγκρίσιμα στοιχεία. Καμία πιστοποίηση."
                    : "Not enough comparable readings were collected. No certification.";
            if (deviations > 0) return greek
                    ? "Εντοπίστηκαν " + deviations + " αναφερόμενες αποκλίσεις από τις εργοστασιακές προδιαγραφές. Χρειάζεται επιβεβαίωση πριν την αγορά."
                    : deviations + " reported specification deviations need independent review before purchase.";
            return greek
                    ? "Τα " + matches + " από τα " + matches
                        + " χαρακτηριστικά που μπόρεσε να συγκρίνει το εργαστήριο συμφωνούν με τις εργοστασιακές προδιαγραφές. "
                        + "Για τα υπόλοιπα δεν υπάρχει διαθέσιμη αυτόματη σύγκριση."
                    : "All " + matches + " characteristics automatically compared by the lab agree with the factory specifications. "
                        + "Automatic comparison is not available for the others.";
        }
    }

    /**
     * Physical ports are read exclusively from an exact-model, sourced OEM
     * specification. Android telephony features and an active eSIM are NOT
     * evidence that a physical SIM tray does or does not exist.
     */
    public static final class PhysicalProfile {
        public final String modelName;
        public final String sourceUrl;
        public final Lab27CertificationGate.Presence usb;
        public final Lab27CertificationGate.Presence headset;
        public final Lab27CertificationGate.Presence simTray;
        public final Lab27CertificationGate.Presence esim;
        public final boolean esimVoiceCallsSupported;

        PhysicalProfile(String modelName, String sourceUrl,
                Lab27CertificationGate.Presence usb,
                Lab27CertificationGate.Presence headset,
                Lab27CertificationGate.Presence simTray,
                Lab27CertificationGate.Presence esim,
                boolean esimVoiceCallsSupported) {
            this.modelName = modelName;
            this.sourceUrl = sourceUrl;
            this.usb = usb;
            this.headset = headset;
            this.simTray = simTray;
            this.esim = esim;
            this.esimVoiceCallsSupported = esimVoiceCallsSupported;
        }

        public boolean complete() {
            // A physical attribute not explicitly described by the OEM is
            // omitted from the popup, never inferred absent or required.
            return sourceUrl != null && !sourceUrl.isEmpty()
                    && (Lab27CertificationGate.shouldAsk(usb)
                        || Lab27CertificationGate.shouldAsk(headset)
                        || Lab27CertificationGate.shouldAsk(simTray));
        }
    }

    public static PhysicalProfile physicalProfile(Report report) {
        if (report == null || !report.supportedModel || report.officialSource.isEmpty())
            return new PhysicalProfile("", "", Lab27CertificationGate.Presence.UNKNOWN,
                    Lab27CertificationGate.Presence.UNKNOWN,
                    Lab27CertificationGate.Presence.UNKNOWN,
                    Lab27CertificationGate.Presence.UNKNOWN, false);
        Lab27CertificationGate.Presence esim =
                exactPresence(report.rows, "eSIM", "Ναι / Yes");
        boolean voice = "Ναι / Yes".equals(exactExpected(
                report.rows, "eSIM voice calls"));
        return new PhysicalProfile(report.modelName, report.officialSource,
                exactPresence(report.rows, "USB connector", "USB-C"),
                exactPresence(report.rows, "3.5 mm jack", "Ναι / Yes"),
                exactPresence(report.rows, "SIM tray",
                        "SIM1 + Hybrid (SIM or MicroSD)"),
                esim, voice);
    }

    private static Lab27CertificationGate.Presence exactPresence(
            List<Row> rows, String label, String presentExpected) {
        for (Row row : rows) {
            if (!label.equals(row.english)) continue;
            if (presentExpected.equals(row.expected))
                return Lab27CertificationGate.Presence.PRESENT;
            if ("Όχι / No".equals(row.expected))
                return Lab27CertificationGate.Presence.ABSENT;
            break;
        }
        return Lab27CertificationGate.Presence.UNKNOWN;
    }

    private static String exactExpected(List<Row> rows, String label) {
        for (Row row : rows) {
            if (label.equals(row.english)) return row.expected;
        }
        return "";
    }

    /** Test-friendly container. All values originate from the common iDoctorEngine. */
    public static final class Measurements {
        public String manufacturer, model, abi;
        // Existing iDoctorEngine fields also displayed by the Internal/Peripherals screens.
        public String socModel, cpuModel, cpuHardware, board;
        public String gpuName, gpuEglHardware, gpuDriver;
        public int cores = -1, widthPx = -1, heightPx = -1, ramMb = -1;
        public long maxRearJpegPixels = -1L, maxFrontJpegPixels = -1L;
        public long batteryDesignMah = -1L;
        public long batteryChargeNowMah = -1L;
        public long batteryChargeFullMah = -1L;
        public int batteryLevelPercent = -1;
        public int wifiFrequencyMhz = -1;
        public Boolean wifi24GHzSupported = null, wifi5GHzSupported = null;
        public boolean nfcSupported, fmFeatureReported;
        public double storageGb = Double.NaN;
        public float activeRefreshHz = Float.NaN, maxRefreshHz = Float.NaN;
        public boolean rearCamera, frontCamera, rearFlash;
        public boolean accelerometer, compass, ambientLight, proximity, gyroscope;
        public boolean irBlaster, fingerprint, wifi, bluetooth, gps;
    }

    /**
     * DeviceInfo Internal uses readSystem/Cpu/Memory/StorageSnapshot.
     * DeviceInfo Peripherals uses readScreen/Camera/Sensor/Peripheral/
     * ConnectivitySnapshot. LAB 27 invokes those same source methods.
     */
    public static Report capture(Context context) {
        Measurements m = new Measurements();
        if (context == null) return evaluate(m);
        iDoctorEngine engine = iDoctorEngine.get(context);

        try {
            iDoctorEngine.SystemSnapshot s = engine.readSystemSnapshot();
            m.manufacturer = s.manufacturer;
            m.model = s.model;
            m.board = s.board;
        } catch (Throwable ignored) { }
        try {
            iDoctorEngine.CpuSnapshot s = engine.readCpuSnapshot();
            m.cores = s.cores;
            m.abi = s.abi;
            m.socModel = s.socModel;
            m.cpuModel = s.modelName;
            m.cpuHardware = s.hardware;
        } catch (Throwable ignored) { }
        try {
            iDoctorEngine.GpuSnapshot s = engine.readGpuSnapshot();
            m.gpuName = s.gpuName;
            m.gpuEglHardware = s.eglHardware;
            m.gpuDriver = s.driver;
        } catch (Throwable ignored) { }
        try {
            // Design capacity comes from the device's telemetry, never from
            // the model's published reference profile (no circular match).
            iDoctorEngine.BatterySnapshot battery = engine.readBatterySnapshot();
            m.batteryDesignMah = battery.chargeDesignMah;
            m.batteryChargeNowMah = battery.chargeNowMah;
            m.batteryChargeFullMah = battery.chargeFullMah;
            if (battery.level >= 0 && battery.scale > 0) {
                m.batteryLevelPercent = (int) Math.round(100d * battery.level / battery.scale);
            } else if (battery.level >= 0 && battery.level <= 100) {
                m.batteryLevelPercent = battery.level;
            }
        } catch (Throwable ignored) { }
        try {
            iDoctorEngine.MemorySnapshot s = engine.readMemorySnapshot();
            m.ramMb = (int) Math.min(Integer.MAX_VALUE, s.totalRamMb);
        } catch (Throwable ignored) { }
        try {
            iDoctorEngine.StorageSnapshot s = engine.readStorageSnapshot();
            if (s.internal != null && s.internal.totalBytes > 0) {
                m.storageGb = s.internal.totalBytes / 1_000_000_000d;
            }
        } catch (Throwable ignored) { }
        try {
            iDoctorEngine.ScreenSnapshot s = engine.readScreenSnapshot();
            m.widthPx = s.widthPx;
            m.heightPx = s.heightPx;
            m.activeRefreshHz = s.refreshRateHz;
            m.maxRefreshHz = s.maxRefreshRateHz;
        } catch (Throwable ignored) { }
        try {
            iDoctorEngine.CameraSnapshot s = engine.readCameraSnapshot();
            for (iDoctorEngine.CameraUnit unit : s.cameras) {
                if ("Back".equals(unit.facing)) {
                    m.rearCamera = true;
                    if (Boolean.TRUE.equals(unit.flash)) m.rearFlash = true;
                    if (unit.maxJpegPixels > m.maxRearJpegPixels) {
                        m.maxRearJpegPixels = unit.maxJpegPixels;
                    }
                }
                if ("Front".equals(unit.facing)) {
                    m.frontCamera = true;
                    if (unit.maxJpegPixels > m.maxFrontJpegPixels) {
                        m.maxFrontJpegPixels = unit.maxJpegPixels;
                    }
                }
            }
        } catch (Throwable ignored) { }
        try {
            iDoctorEngine.SensorSnapshot s = engine.readSensorSnapshot();
            m.accelerometer = s.accelerometer;
            m.compass = s.magnetometer;
            m.ambientLight = s.light;
            m.proximity = s.proximity;
            m.gyroscope = s.gyroscope;
        } catch (Throwable ignored) { }
        try {
            iDoctorEngine.PeripheralSnapshot s = engine.readPeripheralSnapshot();
            m.irBlaster = s.irBlaster;
            m.fingerprint = s.fingerprint;
            m.gps = s.gpsHw;
            m.nfcSupported = s.nfcSupported;
            m.fmFeatureReported = s.fmRadio;
        } catch (Throwable ignored) { }
        try {
            iDoctorEngine.ConnectivitySnapshot s = engine.readConnectivitySnapshot();
            m.wifi = s.wifiSupported;
            m.bluetooth = s.bluetoothSupported;
            m.wifiFrequencyMhz = s.frequencyMhz;
            m.wifi24GHzSupported = s.wifi24GHzSupported;
            m.wifi5GHzSupported = s.wifi5GHzSupported;
        } catch (Throwable ignored) { }
        return evaluate(m);
    }

    public static Report evaluate(Measurements m) {
        List<Row> rows = new ArrayList<>();
        if (m == null || m.model == null
                || !m.model.trim().equalsIgnoreCase("23076RN8DY")) {
            return new Report("", "", rows, false);
        }

        // Xiaomi Redmi 12 5G, exact European model identifier 23076RN8DY.
        // Published reference:
        // https://www.mi.com/global/product/redmi-12-5g/specs/
        // https://www.mi.com/global/support/faq/details/KA-42526/
        // SKU mapping must be separately validated before adding more models.
        final String model = "Xiaomi Redmi 12 5G (23076RN8DY)";
        final String source = "https://www.mi.com/global/product/redmi-12-5g/specs/";

        String make = safe(m.manufacturer);
        add(rows, "Κατασκευαστής", "Manufacturer", "Xiaomi", make,
                make.isEmpty() ? Verdict.UNKNOWN :
                (make.equalsIgnoreCase("Xiaomi") ? Verdict.MATCH : Verdict.DEVIATION));
        add(rows, "Κωδικός μοντέλου", "Model code", "23076RN8DY", safe(m.model),
                Verdict.MATCH);

        add(rows, "Πυρήνες CPU", "CPU cores", "8 (2+6)",
                m.cores > 0 ? Integer.toString(m.cores) : "",
                m.cores == 8 ? Verdict.MATCH :
                        (m.cores > 8 ? Verdict.DEVIATION : Verdict.UNKNOWN));
        add(rows, "Αρχιτεκτονική CPU", "CPU architecture", "64-bit ARM",
                safe(m.abi),
                safe(m.abi).contains("arm64-v8a") ? Verdict.MATCH : Verdict.UNKNOWN);
        // Official Qualcomm product brief maps SM4450 to Snapdragon 4 Gen 2.
        // "qcom" and generic board properties alone do NOT prove this SoC.
        String cpuEvidence = observed(m.socModel, m.cpuModel, m.cpuHardware, m.board);
        boolean known4450 = containsText(cpuEvidence, "SM4450")
                || containsText(cpuEvidence, "Snapdragon 4 Gen 2");
        add(rows, "Μοντέλο επεξεργαστή", "Chipset model", "Snapdragon 4 Gen 2",
                cpuEvidence, known4450 ? Verdict.MATCH : Verdict.UNKNOWN);

        // GPU model is often exposed as EGL/Vulkan platform metadata only.
        // Accept an explicit Adreno identifier; NEVER infer it from "qcom".
        String gpuEvidence = observed(m.gpuName, m.gpuEglHardware, m.gpuDriver);
        add(rows, "Γραφικά GPU", "GPU model", "Qualcomm Adreno",
                gpuEvidence, containsText(gpuEvidence, "Adreno")
                        ? Verdict.MATCH : Verdict.UNKNOWN);

        String ram = m.ramMb > 0 ? m.ramMb + " MiB (system available)" : "";
        // Kernel-visible RAM is below marketed capacity due to reserved memory.
        boolean ramFits4 = m.ramMb >= 3150 && m.ramMb <= 4500;
        boolean ramFits8 = m.ramMb >= 6300 && m.ramMb <= 8500;
        add(rows, "Φυσική RAM", "Physical RAM", "4 ή 8 GB (OEM variants)", ram,
                (ramFits4 || ramFits8) ? Verdict.MATCH : Verdict.UNKNOWN);
        // /data filesystem capacity is smaller than advertised flash capacity.
        // A plausible class match is informational, not a UFS/flash certification.
        boolean storageFits128 = m.storageGb >= 75d && m.storageGb <= 145d;
        boolean storageFits256 = m.storageGb >= 155d && m.storageGb <= 290d;
        add(rows, "Κατηγορία αποθήκευσης", "Storage capacity class",
                "128 ή 256 GB (OEM variants)",
                Double.isFinite(m.storageGb)
                        ? String.format(Locale.US, "%.1f GB usable partition", m.storageGb) : "",
                (storageFits128 || storageFits256) ? Verdict.MATCH : Verdict.UNKNOWN);
        add(rows, "Τύπος αποθήκευσης", "Storage technology",
                "UFS 2.2", "", Verdict.UNKNOWN);

        boolean hasPixels = m.widthPx > 0 && m.heightPx > 0;
        boolean resolutionMatches = (m.widthPx == 1080 && m.heightPx == 2460)
                || (m.widthPx == 2460 && m.heightPx == 1080);
        add(rows, "Ανάλυση οθόνης", "Display resolution", "2460 × 1080",
                hasPixels ? m.widthPx + " × " + m.heightPx : "",
                !hasPixels ? Verdict.UNKNOWN :
                        resolutionMatches ? Verdict.MATCH : Verdict.DEVIATION);

        // Current mode can be 30/48/60 Hz on a genuine 90 Hz panel.
        float hz = m.activeRefreshHz;
        boolean allowedHz = near(hz, 30f) || near(hz, 48f)
                || near(hz, 60f) || near(hz, 90f);
        add(rows, "Ενεργός ρυθμός", "Active refresh rate",
                "30 / 48 / 60 / 90 Hz", hz > 0 ? fmtHz(hz) : "",
                !(hz > 0) ? Verdict.UNKNOWN :
                        allowedHz ? Verdict.MATCH : Verdict.UNKNOWN);
        float maxHz = m.maxRefreshHz;
        add(rows, "Διαθέσιμο μέγιστο Hz", "Exposed maximum refresh",
                "έως 90 Hz", maxHz > 0 ? fmtHz(maxHz) : "",
                near(maxHz, 90f) ? Verdict.MATCH : Verdict.UNKNOWN);

        positive(rows, "Πίσω κάμερα", "Rear camera", "Ναι / Yes",
                m.rearCamera);
        positive(rows, "Μπροστινή κάμερα", "Front camera", "Ναι / Yes",
                m.frontCamera);
        positive(rows, "Φλας πίσω κάμερας", "Rear camera flash",
                "Ναι / Yes", m.rearFlash);
        // Camera2 reports JPEG OUTPUT modes. A 12.5 MP binned JPEG does not
        // disprove a genuine 50 MP sensor; output sizes don't certify the sensor.
        rows.add(new Row("Κύρια κάμερα", "Main camera resolution", "50 MP",
                jpegObservation(m.maxRearJpegPixels),
                withinJpegMp(m.maxRearJpegPixels, 48d, 52.5d)
                        ? Verdict.MATCH : Verdict.UNKNOWN,
                "Το εργαστήριο καταγράφει τη μέγιστη διαθέσιμη ανάλυση JPEG, όχι "
                    + "την ονομαστική ανάλυση του αισθητήρα. Ελέγξτε αν η εφαρμογή "
                    + "κάμερας προσφέρει λειτουργία 50 MP.",
                "The lab reads the maximum exposed JPEG output size, not the "
                    + "sensor's nominal resolution. Check whether the camera app offers a 50 MP mode."));
        add(rows, "Κάμερα βάθους", "Depth camera resolution", "2 MP",
                "", Verdict.UNKNOWN); // Camera2 does not label a lens as "depth 2 MP".
        add(rows, "Selfie", "Selfie camera resolution", "5 MP",
                jpegObservation(m.maxFrontJpegPixels),
                withinJpegMp(m.maxFrontJpegPixels, 4.5d, 5.5d)
                        ? Verdict.MATCH : Verdict.UNKNOWN);

        positive(rows, "Επιταχυνσιόμετρο", "Accelerometer",
                "Ναι / Yes", m.accelerometer);
        positive(rows, "Μαγνητικό πεδίο", "Magnetic compass",
                "Ναι / Yes", m.compass);
        positive(rows, "Φωτεινότητα", "Light sensor",
                "Ναι / Yes (may be virtual)", m.ambientLight);
        positive(rows, "Εγγύτητα", "Proximity sensor",
                "Ναι / Yes (may be virtual)", m.proximity);
        positive(rows, "Γυροσκόπιο", "Gyroscope",
                "Ναι / Yes (virtual supported)", m.gyroscope);

        positive(rows, "Υπέρυθρες", "IR blaster", "Ναι / Yes", m.irBlaster);
        positive(rows, "Δακτυλικό αποτύπωμα", "Fingerprint",
                "Ναι / Yes", m.fingerprint);
        positive(rows, "Παρουσία Wi-Fi", "Wi-Fi present", "Ναι / Yes",
                m.wifi);
        // These official WifiManager capability APIs independently check each band.
        // Current connection frequency alone only proves the active band.
        boolean dualBandConfirmed = Boolean.TRUE.equals(m.wifi24GHzSupported)
                && Boolean.TRUE.equals(m.wifi5GHzSupported);
        add(rows, "Μπάντες Wi-Fi", "Wi-Fi supported bands", "2.4 και 5 GHz / 2.4 and 5 GHz",
                wifiObservation(m), dualBandConfirmed ? Verdict.MATCH : Verdict.UNKNOWN);
        positive(rows, "Bluetooth", "Bluetooth available", "Ναι / Yes",
                m.bluetooth);
        add(rows, "Έκδοση Bluetooth", "Bluetooth version", "5.0",
                "", Verdict.UNKNOWN);
        positive(rows, "GPS", "GPS", "Ναι / Yes", m.gps);

        // NFC varies by regional SKU. USB-C, 3.5mm, battery 5000mAh and
        // FM are OEM claims but these shared readers cannot authenticate
        // the physical part/specification. Never turn absence into a fail.
        add(rows, "NFC (αγορά)", "NFC (market dependent)", "Ανάλογα την περιοχή",
                m.nfcSupported ? "NFC adapter reported" : "",
                m.nfcSupported ? Verdict.MATCH : Verdict.UNKNOWN);
        // OEM design capacity and estimated TODAY's full-charge capacity are
        // different quantities. An aged original cell can be below 5000 mAh.
        // Android chargeFull is a reported estimate, not a physical lab assay.
        // Otherwise use chargeNow / SOC only when both are available and plausible.
        long estimatedFull = -1L;
        String estimateSource = "";
        if (m.batteryChargeFullMah >= 1500L && m.batteryChargeFullMah <= 9000L) {
            estimatedFull = m.batteryChargeFullMah;
            estimateSource = "Android full-charge counter";
        } else if (m.batteryChargeNowMah > 0L
                && m.batteryLevelPercent >= 20 && m.batteryLevelPercent <= 90) {
            long extrapolated = Math.round(100d * m.batteryChargeNowMah / m.batteryLevelPercent);
            if (extrapolated >= 1500L && extrapolated <= 9000L
                    && m.batteryChargeNowMah <= extrapolated) {
                estimatedFull = extrapolated;
                estimateSource = "charge counter / charge percentage estimate";
            }
        }
        String batteryObserved = m.batteryDesignMah > 0
                ? m.batteryDesignMah + " mAh (Android reported design capacity)" : "";
        if (estimatedFull > 0L) {
            if (!batteryObserved.isEmpty()) batteryObserved += " | ";
            batteryObserved += estimatedFull + " mAh (" + estimateSource + ")";
        }
        String batteryNoteGr = estimatedFull > 0L
                ? "Η τρέχουσα χωρητικότητα πλήρους φόρτισης αναφέρεται/εκτιμάται σε "
                    + estimatedFull + " mAh (~" + Math.round(estimatedFull * 100d / 5000d)
                    + "% της εργοστασιακής τιμής 5.000 mAh). Διαφορά μπορεί να σχετίζεται "
                    + "με φυσιολογική φθορά, βαθμονόμηση ή αβεβαιότητα τηλεμετρίας. "
                    + "Δεν αποτελεί μέτρηση πιστοποίησης ούτε απόδειξη μη αυθεντικής μπαταρίας."
                : "Δεν υπάρχει αξιόπιστη τρέχουσα εκτίμηση πλήρους φόρτισης από Android. "
                    + "Αυτό δεν αποδεικνύει απόκλιση ούτε απουσία αυθεντικής μπαταρίας.";
        String batteryNoteEn = estimatedFull > 0L
                ? "Current full-charge capacity is reported/estimated at " + estimatedFull
                    + " mAh (~" + Math.round(estimatedFull * 100d / 5000d)
                    + "% of the published 5000 mAh typical rating). Differences may reflect "
                    + "normal ageing, calibration or telemetry uncertainty. This is not a "
                    + "certified capacity test and does not prove a non-genuine battery."
                : "No reliable current full-charge estimate is exposed by Android. "
                    + "This does not establish a deviation or a non-genuine battery.";
        rows.add(new Row("Μπαταρία", "Factory battery capacity", "5000 mAh (typ.)",
                batteryObserved,
                m.batteryDesignMah >= 4700L && m.batteryDesignMah <= 5300L
                    ? Verdict.MATCH : Verdict.UNKNOWN,
                batteryNoteGr, batteryNoteEn));
        rows.add(new Row("Θύρα USB", "USB connector", "USB-C",
                "", Verdict.UNKNOWN,
                "Το εργαστήριο δεν μπορεί να ελέγξει τον φυσικό τύπο της θύρας USB. "
                    + "Ελέγξτε αν η συσκευή διαθέτει θύρα USB-C, σύμφωνα με τις εργοστασιακές προδιαγραφές.",
                "The lab cannot inspect the physical USB connector. "
                    + "Check whether the device has a USB-C port, as specified by the manufacturer."));
        rows.add(new Row("Υποδοχή 3.5 mm", "3.5 mm jack", "Ναι / Yes",
                "", Verdict.UNKNOWN,
                "Το εργαστήριο δεν μπορεί να επιθεωρήσει την υποδοχή ακουστικών. "
                    + "Ελέγξτε αν υπάρχει υποδοχή 3,5 mm, σύμφωνα με τις προδιαγραφές.",
                "The lab cannot physically inspect the headphone jack. "
                    + "Check whether the device has a 3.5 mm socket as specified."));
        // Exact OEM model sheet: SIM1 + Hybrid (SIM or microSD), a real
        // physical tray, irrespective of active subscriptions or eSIM.
        // https://www.mi.com/global/product/redmi-12-5g/specs/
        rows.add(new Row("Θήκη SIM", "SIM tray", "SIM1 + Hybrid (SIM or MicroSD)",
                "", Verdict.UNKNOWN,
                "Η Xiaomi τεκμηριώνει φυσική θήκη SIM1 + υβριδική SIM/microSD. "
                    + "Ελέγξτε οπτικά τη θήκη. Η χρήση eSIM δεν αποδεικνύει "
                    + "ότι λείπει η εργοστασιακή θήκη SIM.",
                "Xiaomi documents a physical SIM1 + hybrid SIM/microSD tray. "
                    + "Inspect the physical tray. eSIM use cannot establish "
                    + "absence of the OEM physical SIM slot."));
        rows.add(new Row("FM", "FM radio", "Ναι / Yes",
                m.fmFeatureReported ? "Android FM feature declared" : "",
                m.fmFeatureReported ? Verdict.MATCH : Verdict.UNKNOWN,
                "Το εργαστήριο δεν μπορεί να δοκιμάσει τη λήψη ραδιοφώνου FM. "
                    + "Εφόσον υπάρχει διαθέσιμη εφαρμογή Ραδιόφωνο, δοκιμάστε τη λήψη ενός σταθμού.",
                "The lab cannot test FM radio reception. "
                    + "If a Radio app is available, try receiving a station."));

        return new Report(model, source, rows, true);
    }

    /** Avoid treating missing/unreadable device fields as positive matches. */
    private static String observed(String... fields) {
        StringBuilder out = new StringBuilder();
        if (fields == null) return "";
        for (String field : fields) {
            String v = safe(field);
            if (v.isEmpty() || "unknown".equalsIgnoreCase(v)) continue;
            if (out.length() > 0) out.append(" | ");
            out.append(v);
        }
        return out.toString();
    }

    private static boolean containsText(String value, String needle) {
        return value != null && value.toLowerCase(Locale.US)
                .contains(needle.toLowerCase(Locale.US));
    }

    private static boolean withinJpegMp(long pixels, double min, double max) {
        if (pixels <= 0L) return false;
        double mp = pixels / 1_000_000d;
        return mp >= min && mp <= max;
    }

    private static String jpegObservation(long pixels) {
        return pixels > 0L
                ? String.format(Locale.US, "%.1f MP (max JPEG output / μέγιστο JPEG)",
                        pixels / 1_000_000d)
                : "";
    }

    private static String wifiObservation(Measurements m) {
        StringBuilder b = new StringBuilder();
        if (m.wifi24GHzSupported != null) {
            b.append("2.4 GHz capability: ").append(m.wifi24GHzSupported ? "yes" : "not reported");
        }
        if (m.wifi5GHzSupported != null) {
            if (b.length() > 0) b.append(" | ");
            b.append("5 GHz capability: ").append(m.wifi5GHzSupported ? "yes" : "not reported");
        }
        if (m.wifiFrequencyMhz >= 2400 && m.wifiFrequencyMhz <= 7100) {
            if (b.length() > 0) b.append(" | ");
            b.append("Active connection: ").append(m.wifiFrequencyMhz).append(" MHz");
        }
        return b.toString();
    }

    private static boolean near(float a, float b) {
        return Float.isFinite(a) && Math.abs(a - b) <= 1f;
    }

    private static String fmtHz(float hz) {
        return String.format(Locale.US, "%.1f Hz", hz);
    }

    private static String safe(String x) {
        return x == null || "N/A".equalsIgnoreCase(x.trim()) ? "" : x.trim();
    }

    private static void positive(List<Row> rows, String gr, String en,
                                 String expected, boolean supported) {
        add(rows, gr, en, expected, supported ? "Yes" : "",
                supported ? Verdict.MATCH : Verdict.UNKNOWN);
    }

    private static void add(List<Row> rows, String gr, String en, String expected,
                            String observed, Verdict verdict) {
        rows.add(new Row(gr, en, expected, observed, verdict));
    }
}
