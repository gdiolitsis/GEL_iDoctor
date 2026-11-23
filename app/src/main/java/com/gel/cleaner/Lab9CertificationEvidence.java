package com.gel.cleaner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Exact source-bound functional evidence from Lab 9.
 *
 * Only six live checks are certification inputs:
 * GPS, hardware Volume +/-, gyroscope, magnetometer and ambient light.
 * NFC and user-operated Power are intentionally NEVER certification inputs.
 * No reading is treated as a proven failure merely because it is absent,
 * timed out, disabled, or blocked by permissions.
 */
final class Lab9CertificationEvidence {
    static final int SCHEMA = 1;
    static final long MAX_AGE_MS = Lab27CertificationGate.MAX_AGE_MS;

    static final String GPS = "lab9_live_gps";
    static final String VOLUME_UP = "lab9_live_volume_up";
    static final String VOLUME_DOWN = "lab9_live_volume_down";
    static final String GYRO = "lab9_live_gyro";
    static final String MAGNET = "lab9_live_magnet";
    static final String LIGHT = "lab9_live_light";

    static final String VERIFIED = "VERIFIED";
    static final String USER_FAILED = "USER_FAILED";
    static final String UNSUPPORTED = "UNSUPPORTED";

    static final List<String> GRADED = Collections.unmodifiableList(
            java.util.Arrays.asList(GPS, VOLUME_UP, VOLUME_DOWN, GYRO, MAGNET, LIGHT));

    static final class Assessment {
        final List<String> pending;
        final List<String> reportedFailures;
        final List<String> unsupported;
        final int verifiedCount;

        Assessment(List<String> pending, List<String> failures,
                List<String> unsupported, int verifiedCount) {
            this.pending = Collections.unmodifiableList(new ArrayList<>(pending));
            this.reportedFailures = Collections.unmodifiableList(new ArrayList<>(failures));
            this.unsupported = Collections.unmodifiableList(new ArrayList<>(unsupported));
            this.verifiedCount = verifiedCount;
        }

        boolean complete() { return pending.isEmpty(); }
        boolean hasUserReportedFailure() { return !reportedFailures.isEmpty(); }
    }

    private Lab9CertificationEvidence() { }

    static boolean fresh(int schema, long wallMs, long elapsedMs,
            long nowWallMs, long nowElapsedMs, String recordedFingerprint,
            String actualFingerprint) {
        return schema == SCHEMA && recordedFingerprint != null
                && recordedFingerprint.equals(actualFingerprint)
                && wallMs > 0L && elapsedMs >= 0L
                && nowWallMs >= wallMs && nowElapsedMs >= elapsedMs
                && nowWallMs - wallMs <= MAX_AGE_MS
                && nowElapsedMs - elapsedMs <= MAX_AGE_MS;
    }

    /** Features are independently taken from this device, never from returned intent flags. */
    static Assessment evaluate(Map<String, String> observed, Set<String> supported) {
        List<String> pending = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        List<String> absent = new ArrayList<>();
        int verified = 0;
        for (String key : GRADED) {
            // No sensor advertised by Android: not graded or assumed defective.
            if (!supported.contains(key)) {
                absent.add(key);
                continue;
            }
            String status = observed.get(key);
            if (VERIFIED.equals(status)) {
                verified++;
            } else if (USER_FAILED.equals(status)) {
                // Explicit user confirmation after guided retest, not an automatic diagnosis.
                failures.add(key);
            } else {
                // NOT_TESTED, NOT_VERIFIED, UNSUPPORTED even though present, DISABLED
                // and PERMISSION_DENIED all block certification but lower no grade.
                pending.add(key);
            }
        }
        return new Assessment(pending, failures, absent, verified);
    }

    static Map<String,String> capture(Map<String,String> returned) {
        Map<String,String> safe = new LinkedHashMap<>();
        for (String key : GRADED) {
            String status = returned == null ? null : returned.get(key);
            safe.put(key, validStatus(status) ? status : "NOT_TESTED");
        }
        return Collections.unmodifiableMap(safe);
    }

    private static boolean validStatus(String status) {
        return VERIFIED.equals(status) || USER_FAILED.equals(status)
                || UNSUPPORTED.equals(status) || "NOT_TESTED".equals(status)
                || "NOT_VERIFIED".equals(status) || "DISABLED".equals(status)
                || "PERMISSION_DENIED".equals(status);
    }

    static Set<String> supported(boolean gps, boolean gyro, boolean magnet,
                                 boolean light) {
        LinkedHashSet<String> keys = new LinkedHashSet<>();
        if (gps) keys.add(GPS);
        keys.add(VOLUME_UP);
        keys.add(VOLUME_DOWN);
        if (gyro) keys.add(GYRO);
        if (magnet) keys.add(MAGNET);
        if (light) keys.add(LIGHT);
        return Collections.unmodifiableSet(keys);
    }

    static String displayName(String key, boolean gr) {
        if (GPS.equals(key)) return "GPS";
        if (VOLUME_UP.equals(key)) return "Volume +";
        if (VOLUME_DOWN.equals(key)) return "Volume −";
        if (GYRO.equals(key)) return gr ? "Γυροσκόπιο" : "Gyroscope";
        if (MAGNET.equals(key)) return gr ? "Πυξίδα / μαγνητόμετρο" : "Compass / magnetometer";
        if (LIGHT.equals(key)) return gr ? "Αισθητήρας φωτός" : "Ambient light sensor";
        return gr ? "Άγνωστος έλεγχος" : "Unknown check";
    }
}
