package com.gel.cleaner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Structured evidence consumed by Lab 27 and passed unchanged to Lab 28.
 * Lab 16's HIGH temperature bands are conservative screening thresholds,
 * NOT manufacturer limits or proof of defective components. An Android SEVERE
 * status separately establishes active system thermal protection.
 *
 * Lab 15's net rate is context-sensitive and is not a fault/grade driver.
 * Battery charge percentage, free RAM/storage and uptime are NEVER drivers.
 */
final class Lab27GradeDrivers {
    static final int EVIDENCE_SCHEMA = 1;
    static final long MAX_EVIDENCE_AGE_MS = 12L * 60L * 60L * 1000L;

    enum Driver {
        CAMERA,
        THERMAL_OS_SEVERE,
        THERMAL_HIGH_BATTERY,
        THERMAL_HIGH_CPU,
        THERMAL_HIGH_GPU,
        THERMAL_HIGH_CHARGER,
        THERMAL_HIGH_PMIC,
        THERMAL_HIGH_SURFACE,
        CHARGING_OVERHEAT,
        CHARGING_DISCONNECT;

        boolean isConfirmedFailure() {
            return this == CAMERA || this == THERMAL_OS_SEVERE;
        }

        boolean isHighTemperatureReading() {
            return name().startsWith("THERMAL_HIGH_");
        }

        String observation(boolean gr) {
            switch (this) {
                case CAMERA:
                    return gr ? "Lab 8: Επιβεβαιωμένη αποτυχία ζωντανής εικόνας κάμερας."
                              : "Lab 8: Confirmed live camera-preview failure.";
                case THERMAL_OS_SEVERE:
                    return gr ? "Lab 16: Το Android ανέφερε σοβαρή θερμική προστασία που περιορίζει τη λειτουργία."
                              : "Lab 16: Android reported severe thermal protection affecting operation.";
                case CHARGING_OVERHEAT:
                    return gr ? "Lab 15: Καταγράφηκε θερμοκρασία μπαταρίας τουλάχιστον 45°C κατά τη φόρτιση. Η αιτία παραμένει άγνωστη."
                              : "Lab 15: Battery reached at least 45°C during charging; the cause is unknown.";
                case CHARGING_DISCONNECT:
                    return gr ? "Lab 15: Καταγράφηκαν διακοπές σύνδεσης φόρτισης. Δεν έχει απομονωθεί η αιτία."
                              : "Lab 15: Charging connection interruptions were observed; cause not isolated.";
                default:
                    int sector = sector();
                    return (gr ? "Lab 16: Πολύ υψηλή ένδειξη θερμοκρασίας — "
                               : "Lab 16: Very high temperature reading — ")
                            + Lab16ThermalCorrelation.label(sector, gr)
                            + (gr ? ". Πρόκειται για όριο διαγνωστικού ελέγχου, όχι πιστοποιημένο όριο κατασκευαστή."
                                  : ". This is a diagnostic screening band, not a verified OEM limit.");
            }
        }

        String recommendedCheck(boolean gr) {
            switch (this) {
                case CAMERA:
                    return gr ? "Επανάλαβε το Lab 8 με ζωντανή εικόνα από κάθε κάμερα."
                              : "Repeat Lab 8 live-preview checks for each camera.";
                case THERMAL_OS_SEVERE:
                    return gr ? "Διακοπή έντονης χρήσης/φόρτισης, ψύξη της συσκευής και επανέλεγχος Lab 16. Αν επιμένει, τεχνικός έλεγχος."
                              : "Stop heavy use/charging, allow the device to cool, then repeat Lab 16. Seek service if persistent.";
                case CHARGING_OVERHEAT:
                    return gr ? "Επανέλεγχος Lab 15 με αξιόπιστο συμβατό φορτιστή/καλώδιο όταν κρυώσει η συσκευή· αν επαναληφθεί, τεχνικός έλεγχος."
                              : "Repeat Lab 15 with a reliable compatible charger/cable after cooling; seek service if repeated.";
                case CHARGING_DISCONNECT:
                    return gr ? "Επανέλεγχος Lab 15 με άλλο συμβατό φορτιστή/καλώδιο και οπτική εξέταση θύρας, χωρίς συμπέρασμα βλάβης θύρας."
                              : "Repeat Lab 15 using another compatible charger/cable and inspect the port; do not presume port failure.";
                default:
                    return gr ? "Επανέλαβε το Lab 16 μετά από ψύξη και με φυσιολογικό φορτίο. Αν παραμένει πολύ υψηλή, ζήτησε τεχνικό έλεγχο."
                              : "Repeat Lab 16 after cooling under normal load. Seek service if the very high reading persists.";
            }
        }

        private int sector() {
            switch (this) {
                case THERMAL_HIGH_BATTERY: return Lab16ThermalCorrelation.BATTERY;
                case THERMAL_HIGH_CPU: return Lab16ThermalCorrelation.CPU;
                case THERMAL_HIGH_GPU: return Lab16ThermalCorrelation.GPU;
                case THERMAL_HIGH_CHARGER: return Lab16ThermalCorrelation.CHARGER;
                case THERMAL_HIGH_PMIC: return Lab16ThermalCorrelation.PMIC;
                case THERMAL_HIGH_SURFACE: return Lab16ThermalCorrelation.SURFACE;
                default: throw new IllegalStateException("Not a thermal sector: " + this);
            }
        }
    }

    private static final Driver[] HIGH_DRIVERS = {
            Driver.THERMAL_HIGH_BATTERY, Driver.THERMAL_HIGH_CPU,
            Driver.THERMAL_HIGH_GPU, Driver.THERMAL_HIGH_CHARGER,
            Driver.THERMAL_HIGH_PMIC, Driver.THERMAL_HIGH_SURFACE
    };

    private Lab27GradeDrivers() {}

    /** Timestamp and monotonic clock must both match one current device boot. */
    static boolean freshEvidence(int schema, long wallMs, long elapsedMs,
                                 long nowWallMs, long nowElapsedMs) {
        return schema == EVIDENCE_SCHEMA && wallMs > 0L && elapsedMs >= 0L
                && nowWallMs >= wallMs && nowElapsedMs >= elapsedMs
                && nowWallMs - wallMs <= MAX_EVIDENCE_AGE_MS
                && nowElapsedMs - elapsedMs <= MAX_EVIDENCE_AGE_MS;
    }

    static List<Driver> fromOperationalSnapshot(
            boolean confirmedCameraFailure, int osThermalStatus, float[] sectorTemps,
            boolean chargingOverheat, boolean chargingDisconnect) {
        EnumSet<Driver> values = EnumSet.noneOf(Driver.class);
        if (confirmedCameraFailure) values.add(Driver.CAMERA);
        if (osThermalStatus >= Lab16ThermalCorrelation.OS_SEVERE)
            values.add(Driver.THERMAL_OS_SEVERE);
        if (sectorTemps != null && sectorTemps.length == Lab16ThermalCorrelation.N) {
            for (int i = 0; i < sectorTemps.length; i++) {
                if (Lab16ThermalCorrelation.level(i, sectorTemps[i])
                        == Lab16ThermalCorrelation.Level.HIGH)
                    values.add(HIGH_DRIVERS[i]);
            }
        }
        if (chargingOverheat) values.add(Driver.CHARGING_OVERHEAT);
        if (chargingDisconnect) values.add(Driver.CHARGING_DISCONNECT);
        return Collections.unmodifiableList(new ArrayList<>(values));
    }

    static int confirmedFailures(List<Driver> drivers) {
        int count = 0;
        for (Driver driver : drivers) if (driver.isConfirmedFailure()) count++;
        return count;
    }

    static String encode(List<Driver> drivers) {
        if (drivers == null) throw new IllegalArgumentException("Missing drivers");
        EnumSet<Driver> unique = EnumSet.noneOf(Driver.class);
        for (Driver d : drivers) {
            if (d == null || !unique.add(d)) throw new IllegalArgumentException("Invalid drivers");
        }
        StringBuilder s = new StringBuilder();
        for (Driver d : unique) {
            if (s.length() > 0) s.append(',');
            s.append(d.name());
        }
        return s.toString();
    }

    static List<Driver> decode(String stored) {
        if (stored == null || stored.length() > 512)
            throw new IllegalArgumentException("Missing or oversized grade evidence");
        if (stored.isEmpty()) return Collections.emptyList();
        String[] tokens = stored.split(",", -1);
        EnumSet<Driver> drivers = EnumSet.noneOf(Driver.class);
        for (String token : tokens) {
            Driver item;
            try { item = Driver.valueOf(token); }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown grade driver", e);
            }
            if (!drivers.add(item)) throw new IllegalArgumentException("Duplicate driver");
        }
        if (!encode(new ArrayList<>(drivers)).equals(stored))
            throw new IllegalArgumentException("Noncanonical grade evidence");
        return Collections.unmodifiableList(new ArrayList<>(drivers));
    }

    static boolean validEvidence(String encoded, int score,
                                 boolean cameraFailure, int confirmedFailureCount) {
        try {
            List<Driver> drivers = decode(encoded);
            return drivers.contains(Driver.CAMERA) == cameraFailure
                    && confirmedFailures(drivers) == confirmedFailureCount
                    && score == Lab27FunctionGradePolicy.apply(drivers);
        } catch (IllegalArgumentException invalid) {
            return false;
        }
    }
}
