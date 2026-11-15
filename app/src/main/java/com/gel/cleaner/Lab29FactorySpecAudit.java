package com.gel.cleaner;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * LAB 29 — OEM-published specification comparison, NOT OEM part authentication.
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

        Row(String greek, String english, String expected, String observed,
            Verdict verdict) {
            this.greek = greek;
            this.english = english;
            this.expected = expected;
            this.observed = observed;
            this.verdict = verdict;
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
                        + " συγκρίσιμα χαρακτηριστικά συμφωνούν με τις δημοσιευμένες προδιαγραφές του μοντέλου. Τα υπόλοιπα δεν επαληθεύτηκαν."
                    : "All " + matches + " comparable characteristics agree with the published model specifications. Other characteristics were not verified.";
        }
    }

    /** Test-friendly container. All values originate from the common iDoctorEngine. */
    public static final class Measurements {
        public String manufacturer, model, abi;
        public int cores = -1, widthPx = -1, heightPx = -1, ramMb = -1;
        public double storageGb = Double.NaN;
        public float activeRefreshHz = Float.NaN, maxRefreshHz = Float.NaN;
        public boolean rearCamera, frontCamera, rearFlash;
        public boolean accelerometer, compass, ambientLight, proximity, gyroscope;
        public boolean irBlaster, fingerprint, wifi, bluetooth, gps;
    }

    /**
     * DeviceInfo Internal uses readSystem/Cpu/Memory/StorageSnapshot.
     * DeviceInfo Peripherals uses readScreen/Camera/Sensor/Peripheral/
     * ConnectivitySnapshot. LAB 29 invokes those same source methods.
     */
    public static Report capture(Context context) {
        Measurements m = new Measurements();
        if (context == null) return evaluate(m);
        iDoctorEngine engine = iDoctorEngine.get(context);

        try {
            iDoctorEngine.SystemSnapshot s = engine.readSystemSnapshot();
            m.manufacturer = s.manufacturer;
            m.model = s.model;
        } catch (Throwable ignored) { }
        try {
            iDoctorEngine.CpuSnapshot s = engine.readCpuSnapshot();
            m.cores = s.cores;
            m.abi = s.abi;
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
                }
                if ("Front".equals(unit.facing)) m.frontCamera = true;
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
        } catch (Throwable ignored) { }
        try {
            iDoctorEngine.ConnectivitySnapshot s = engine.readConnectivitySnapshot();
            m.wifi = s.wifiSupported;
            m.bluetooth = s.bluetoothSupported;
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
        add(rows, "Μοντέλο επεξεργαστή", "Chipset model", "Snapdragon 4 Gen 2",
                "", Verdict.UNKNOWN);
        add(rows, "Γραφικά GPU", "GPU model", "Qualcomm Adreno",
                "", Verdict.UNKNOWN);

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
        add(rows, "Κύρια κάμερα", "Main camera resolution", "50 MP",
                "", Verdict.UNKNOWN);
        add(rows, "Κάμερα βάθους", "Depth camera resolution", "2 MP",
                "", Verdict.UNKNOWN);
        add(rows, "Selfie", "Selfie camera resolution", "5 MP",
                "", Verdict.UNKNOWN);

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
        add(rows, "Μπάντες Wi-Fi", "Wi-Fi supported bands", "2.4 και 5 GHz / 2.4 and 5 GHz",
                "", Verdict.UNKNOWN); // Presence does not establish both bands.
        positive(rows, "Bluetooth", "Bluetooth available", "Ναι / Yes",
                m.bluetooth);
        add(rows, "Έκδοση Bluetooth", "Bluetooth version", "5.0",
                "", Verdict.UNKNOWN);
        positive(rows, "GPS", "GPS", "Ναι / Yes", m.gps);

        // NFC varies by regional SKU. USB-C, 3.5mm, battery 5000mAh and
        // FM are OEM claims but these shared readers cannot authenticate
        // the physical part/specification. Never turn absence into a fail.
        add(rows, "NFC (αγορά)", "NFC (market dependent)", "Ανάλογα την περιοχή",
                "", Verdict.UNKNOWN);
        add(rows, "Μπαταρία", "Factory battery capacity", "5000 mAh (typ.)",
                "", Verdict.UNKNOWN);
        add(rows, "Θύρα USB", "USB connector", "USB-C",
                "", Verdict.UNKNOWN);
        add(rows, "Υποδοχή 3.5 mm", "3.5 mm jack", "Ναι / Yes",
                "", Verdict.UNKNOWN);
        add(rows, "FM", "FM radio", "Ναι / Yes",
                "", Verdict.UNKNOWN);

        return new Report(model, source, rows, true);
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
