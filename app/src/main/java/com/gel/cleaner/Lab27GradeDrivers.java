package com.gel.cleaner;

import java.util.Collections;
import java.util.List;

/**
 * Grade drivers in Lab 27 are ONLY proven operational failures.
 * Contextual observations such as low charge, free RAM, storage space,
 * uptime, battery temperature or a momentary thermal reading are excluded.
 * Lab 28 decodes these stored reasons and suggests a targeted recheck.
 */
final class Lab27GradeDrivers {
    enum Driver {
        CAMERA;

        String observation(boolean gr) {
            return gr
                    ? "Lab 8: Ο χρήστης επιβεβαίωσε αποτυχία ζωντανής εικόνας κάμερας."
                    : "Lab 8: The user confirmed a camera live-preview failure.";
        }

        String recommendedCheck(boolean gr) {
            return gr ? "Επανέλεγχος Lab 8 με ζωντανή εικόνα κάθε κάμερας."
                      : "Repeat Lab 8 live-preview test for each camera.";
        }

        boolean isConfirmedFailure() { return true; }
    }

    private Lab27GradeDrivers() {}

    static List<Driver> fromConfirmedFailures(boolean confirmedCameraFailure) {
        return confirmedCameraFailure
                ? Collections.singletonList(Driver.CAMERA)
                : Collections.emptyList();
    }

    static String encode(List<Driver> factors) {
        if (factors == null) throw new IllegalArgumentException("Missing factors");
        if (factors.isEmpty()) return "";
        if (factors.size() == 1 && factors.get(0) == Driver.CAMERA) return "CAMERA";
        throw new IllegalArgumentException("Unknown or duplicated failure factors");
    }

    static List<Driver> decode(String encoded) {
        if ("".equals(encoded)) return Collections.emptyList();
        if ("CAMERA".equals(encoded)) return Collections.singletonList(Driver.CAMERA);
        throw new IllegalArgumentException("Invalid functional failure evidence");
    }

    static boolean validEvidence(String encoded, int gradeScore,
                                 boolean cameraFailure, int confirmedFunctionalFailures) {
        if (encoded == null || gradeScore != Lab27FunctionGradePolicy.apply(cameraFailure)
                || confirmedFunctionalFailures != (cameraFailure ? 1 : 0)) {
            return false;
        }
        try {
            return decode(encoded).equals(fromConfirmedFailures(cameraFailure));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
