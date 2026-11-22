package com.gel.cleaner;

/**
 * Grade cap for completed functional tests. This is a diagnostic policy,
 * not a determination of the price of a used device or of parts authenticity.
 */
final class Lab27FunctionGradePolicy {
    static final int CAMERA_FAILURE_CAP = 49; // D band (<55)
    static final int OTHER_FINDINGS_CAP = 84; // B band (<85)
    private Lab27FunctionGradePolicy() {}

    static int apply(int measuredScore, boolean userConfirmedCameraFailure,
                     int completedLabsWithFindings) {
        if (measuredScore < 0 || measuredScore > 100) return -1;
        if (userConfirmedCameraFailure) return Math.min(measuredScore, CAMERA_FAILURE_CAP);
        if (completedLabsWithFindings > 0) return Math.min(measuredScore, OTHER_FINDINGS_CAP);
        return measuredScore;
    }
}
