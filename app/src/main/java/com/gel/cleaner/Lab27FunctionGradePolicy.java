package com.gel.cleaner;

import java.util.List;

/**
 * Evidence-based device-operation grade for an already eligible certificate.
 * Temporary charge percentage, free RAM/storage and uptime do not participate.
 * An elevated temperature (WATCH band) alone does not reduce this grade.
 *
 * A HIGH zone is a conservative screening band, not an OEM safety limit and
 * not proof of a failed component. Severe Android thermal protection is an
 * actually reported operational limitation. Charging issues are reported with
 * retest instructions without attributing them to the phone or charger.
 */
final class Lab27FunctionGradePolicy {
    static final int CAMERA_FAILURE_CAP = 49;       // D
    static final int LAB9_REPORTED_FAILURE_CAP = 49; // D: user-confirmed functional failure
    static final int SEVERE_THERMAL_CAP = 49;        // D: OS already protecting
    static final int HIGH_THERMAL_CAP = 65;          // C: serious reading, recheck
    static final int CHARGING_CONCERN_CAP = 80;      // B: observed charging concern

    private Lab27FunctionGradePolicy() {}

    static int apply(List<Lab27GradeDrivers.Driver> drivers) {
        if (drivers == null) return -1;
        int score = 100;
        for (Lab27GradeDrivers.Driver reason : drivers) {
            if (reason == null) return -1;
            switch (reason) {
                case CAMERA:
                    score = Math.min(score, CAMERA_FAILURE_CAP);
                    break;
                case THERMAL_OS_SEVERE:
                    score = Math.min(score, SEVERE_THERMAL_CAP);
                    break;
                case CHARGING_OVERHEAT:
                case CHARGING_DISCONNECT:
                    score = Math.min(score, CHARGING_CONCERN_CAP);
                    break;
                default:
                    if (reason.isLab9UserReportedFailure()) {
                        score = Math.min(score, LAB9_REPORTED_FAILURE_CAP);
                        break;
                    }
                    if (reason.isHighTemperatureReading())
                        score = Math.min(score, HIGH_THERMAL_CAP);
                    else return -1;
            }
        }
        return score;
    }

    /** Backwards-compatible focused scorer for a separately proven camera fault. */
    static int apply(boolean confirmedCameraFailure) {
        return apply(Lab27GradeDrivers.fromOperationalSnapshot(
                confirmedCameraFailure, -1, null, false, false));
    }
}
