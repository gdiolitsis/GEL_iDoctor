package com.gel.cleaner;

/**
 * Lab 27 device-function grading uses measured health values and verified
 * functional failures only. The warning colors or informational findings
 * in completed labs do not prove a malfunction and never cap a grade.
 * This is neither a parts-authenticity nor used-device valuation policy.
 */
final class Lab27FunctionGradePolicy {
    static final int CAMERA_FAILURE_CAP = 49; // D band (<55)
    private Lab27FunctionGradePolicy() {}

    static int apply(int measuredScore, boolean userConfirmedCameraFailure) {
        if (measuredScore < 0 || measuredScore > 100) return -1;
        return userConfirmedCameraFailure
                ? Math.min(measuredScore, CAMERA_FAILURE_CAP) : measuredScore;
    }
}
