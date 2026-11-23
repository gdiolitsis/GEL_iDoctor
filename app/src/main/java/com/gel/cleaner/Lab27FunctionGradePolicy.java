package com.gel.cleaner;

/**
 * The GEL Device Certification grade is based on documented failures of
 * functions actually tested by the required labs, not circumstantial readings
 * such as remaining battery charge, free RAM/storage, uptime or ambient heat.
 *
 * Call only after all required labs, OEM cross-check and physical user checks.
 * Further proven functional-failure categories must be explicitly integrated,
 * never inferred from yellow logs or transient score heuristics.
 */
final class Lab27FunctionGradePolicy {
    static final int CAMERA_FAILURE_CAP = 49; // D band (<55)
    private Lab27FunctionGradePolicy() {}

    static int apply(boolean confirmedCameraFunctionFailure) {
        return confirmedCameraFunctionFailure ? CAMERA_FAILURE_CAP : 100;
    }
}
