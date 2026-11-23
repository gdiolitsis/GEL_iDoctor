package com.gel.cleaner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Typed, source-bound reasons for Lab 27's numerical grade.
 *
 * The same measured inputs that the Lab 27 scorer consumed determine which
 * factors reduced its score. A "factor" is not automatically a hardware fault:
 * low state of charge, device storage capacity and uptime are contextual.
 * Lab 28 only decodes the factors persisted WITH the Lab 27 certificate.
 */
final class Lab27GradeDrivers {
    enum Driver {
        CAMERA,
        THERMAL,
        BATTERY_TEMPERATURE,
        BATTERY_CHARGE,
        STORAGE_SPACE,
        STORAGE_CAPACITY,
        RAM_FREE,
        UPTIME_SHORT,
        UPTIME_LONG;

        String observation(boolean gr) {
            switch (this) {
                case CAMERA:
                    return gr ? "Lab 8: Ο χρήστης επιβεβαίωσε αποτυχία ζωντανής εικόνας κάμερας."
                              : "Lab 8: The user confirmed a camera live-preview failure.";
                case THERMAL:
                    return gr ? "Η καταγεγραμμένη θερμοκρασία μείωσε τον θερμικό δείκτη."
                              : "Recorded temperatures lowered the thermal score.";
                case BATTERY_TEMPERATURE:
                    return gr ? "Η θερμοκρασία μπαταρίας μείωσε τη βαθμολογία."
                              : "Battery temperature lowered the score.";
                case BATTERY_CHARGE:
                    return gr ? "Η χαμηλή στάθμη φόρτισης μείωσε τον δείκτη. Δεν αποδεικνύει φθορά μπαταρίας."
                              : "A low charge level reduced the score. It does not establish battery wear.";
                case STORAGE_SPACE:
                    return gr ? "Ο περιορισμένος ελεύθερος χώρος αποθήκευσης μείωσε τον δείκτη."
                              : "Limited available storage reduced the score.";
                case STORAGE_CAPACITY:
                    return gr ? "Η ονομαστική χωρητικότητα αποθήκευσης επηρέασε τον δείκτη. Δεν αποτελεί δυσλειτουργία."
                              : "Nominal storage capacity affected the index. This is not a malfunction.";
                case RAM_FREE:
                    return gr ? "Η διαθέσιμη RAM κατά τη μέτρηση μείωσε τον δείκτη. Δεν αποδεικνύει βλάβη RAM."
                              : "Available RAM at measurement time reduced the score. This does not prove RAM failure.";
                case UPTIME_SHORT:
                    return gr ? "Ο μικρός χρόνος λειτουργίας μετά την εκκίνηση μείωσε τον δείκτη. Δεν αποδεικνύει αστάθεια."
                              : "Short uptime since boot reduced the score. It does not prove instability.";
                case UPTIME_LONG:
                    return gr ? "Ο μεγάλος χρόνος λειτουργίας μείωσε τον δείκτη. Δεν αποδεικνύει αστάθεια."
                              : "Long uptime reduced the score. It does not prove instability.";
                default: throw new AssertionError(this);
            }
        }

        String recommendedCheck(boolean gr) {
            switch (this) {
                case CAMERA: return gr ? "Επανέλεγχος Lab 8 με ζωντανή εικόνα κάθε κάμερας."
                                        : "Repeat Lab 8 live-preview test for each camera.";
                case THERMAL: return gr ? "Επανέλεγχος θερμοκρασιών στο Lab 16."
                                         : "Repeat temperature readings in Lab 16.";
                case BATTERY_TEMPERATURE: return gr ? "Επανέλεγχος στο Lab 14A, με φυσιολογική θερμοκρασία συσκευής."
                                                     : "Repeat Lab 14A under normal device temperature.";
                case BATTERY_CHARGE: return gr ? "Επανάλαβε το Lab 27 με επαρκή φόρτιση, χωρίς να συμπεράνεις φθορά."
                                                : "Repeat Lab 27 with adequate charge; do not infer wear.";
                case STORAGE_SPACE: return gr ? "Επανέλεγχος διαθέσιμου χώρου στο Lab 18."
                                               : "Recheck available space with Lab 18.";
                case STORAGE_CAPACITY: return gr ? "Δεν απαιτείται επανέλεγχος βλάβης λόγω της χωρητικότητας."
                                                  : "Capacity alone does not require a fault recheck.";
                case RAM_FREE: return gr ? "Επανέλεγχος διαθεσιμότητας RAM στο Lab 19."
                                          : "Recheck RAM availability in Lab 19.";
                case UPTIME_SHORT: return gr ? "Επανάλαβε το Lab 27 μετά από κανονικό χρόνο λειτουργίας."
                                              : "Repeat Lab 27 after normal uptime.";
                case UPTIME_LONG: return gr ? "Επανάλαβε το Lab 27 μετά από φυσιολογική επανεκκίνηση, αν χρειάζεται."
                                             : "Repeat Lab 27 after a normal reboot if needed.";
                default: throw new AssertionError(this);
            }
        }

        boolean isConfirmedFailure() {
            return this == CAMERA;
        }
    }

    private Lab27GradeDrivers() {}

    static List<Driver> fromSnapshot(float maxThermal, float avgThermal, float batteryTemperature,
                                      int batteryPercent, boolean charging, int storageFreePercent,
                                      long storageTotalBytes, int ramFreePercent, long uptimeMs,
                                      boolean confirmedCameraFailure) {
        EnumSet<Driver> factors = EnumSet.noneOf(Driver.class);
        if (confirmedCameraFailure) factors.add(Driver.CAMERA);
        if ((Float.isFinite(maxThermal) && maxThermal >= 50f)
                || (Float.isFinite(avgThermal) && avgThermal >= 45f))
            factors.add(Driver.THERMAL);
        if (Float.isFinite(batteryTemperature) && batteryTemperature >= 40f)
            factors.add(Driver.BATTERY_TEMPERATURE);
        if (!charging && batteryPercent >= 0 && batteryPercent < 30)
            factors.add(Driver.BATTERY_CHARGE);
        if (storageFreePercent >= 0 && storageFreePercent < 20)
            factors.add(Driver.STORAGE_SPACE);
        if (storageTotalBytes > 0) {
            long sizeGiB = storageTotalBytes / (1024L * 1024L * 1024L);
            if (sizeGiB <= 64) factors.add(Driver.STORAGE_CAPACITY);
        }
        if (ramFreePercent >= 0 && ramFreePercent < 25)
            factors.add(Driver.RAM_FREE);
        if (uptimeMs >= 0 && uptimeMs < 2L * 60L * 60L * 1000L)
            factors.add(Driver.UPTIME_SHORT);
        else if (uptimeMs > 10L * 24L * 60L * 60L * 1000L)
            factors.add(Driver.UPTIME_LONG);
        return Collections.unmodifiableList(new ArrayList<>(factors));
    }

    /** Fixed, locale-independent codes; never store free-text deductions. */
    static String encode(List<Driver> factors) {
        if (factors == null || factors.isEmpty()) return "";
        StringBuilder out = new StringBuilder();
        EnumSet<Driver> unique = EnumSet.noneOf(Driver.class);
        for (Driver d : factors) {
            if (d == null || !unique.add(d)) throw new IllegalArgumentException("Invalid factor set");
            if (out.length() > 0) out.append(',');
            out.append(d.name());
        }
        return out.toString();
    }

    /** Fail closed for corrupted, unknown, duplicate or incompatible stored codes. */
    static List<Driver> decode(String encoded) {
        if (encoded == null || encoded.length() > 256)
            throw new IllegalArgumentException("Missing or oversized grade factors");
        if (encoded.isEmpty()) return Collections.emptyList();
        String[] parts = encoded.split(",", -1);
        if (parts.length > Driver.values().length)
            throw new IllegalArgumentException("Too many grade factors");
        EnumSet<Driver> seen = EnumSet.noneOf(Driver.class);
        List<Driver> factors = new ArrayList<>();
        for (String code : parts) {
            Driver d;
            try { d = Driver.valueOf(code); }
            catch (RuntimeException e) { throw new IllegalArgumentException("Unknown grade factor", e); }
            if (!seen.add(d)) throw new IllegalArgumentException("Duplicate grade factor");
            factors.add(d);
        }
        if (seen.contains(Driver.UPTIME_SHORT) && seen.contains(Driver.UPTIME_LONG))
            throw new IllegalArgumentException("Contradictory uptime factors");
        return Collections.unmodifiableList(factors);
    }

    static boolean validEvidence(String encoded, int measuredScore,
                                 boolean cameraFailure, int confirmedFunctionalFailures) {
        if (measuredScore < 0 || measuredScore > 100
                || confirmedFunctionalFailures != (cameraFailure ? 1 : 0)) return false;
        try {
            List<Driver> factors = decode(encoded);
            if (factors.contains(Driver.CAMERA) != cameraFailure) return false;
            if (measuredScore < 100 && factors.isEmpty()) return false;
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
