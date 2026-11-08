package com.gel.cleaner;

/** Non-calibrated but reproducible sensor-event checks for guided Lab 7. */
public final class Lab7SensorEvidence {
    private Lab7SensorEvidence() {}

    public enum Orientation { PORTRAIT, LANDSCAPE, OTHER_OR_UNSTABLE }
    public enum Proximity { NEAR, FAR, UNKNOWN }

    /** Gravity-aligned pose, not a generic device shake or UI autorotation. */
    public static Orientation gravityOrientation(float x, float y, float z) {
        if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z)) {
            return Orientation.OTHER_OR_UNSTABLE;
        }
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length < 7.5 || length > 12.5 || Math.abs(z) > 7) {
            return Orientation.OTHER_OR_UNSTABLE;
        }
        if (Math.abs(y) >= 6 && Math.abs(x) <= 4) return Orientation.PORTRAIT;
        if (Math.abs(x) >= 6 && Math.abs(y) <= 4) return Orientation.LANDSCAPE;
        return Orientation.OTHER_OR_UNSTABLE;
    }

    /** OEM proximity maxRange is usually the 'far' state, not a measured distance. */
    public static Proximity proximityState(float reading, float maximumRange) {
        if (!Float.isFinite(reading) || !Float.isFinite(maximumRange)
                || maximumRange <= 0 || reading < 0) return Proximity.UNKNOWN;
        float threshold = Math.max(0.1f, maximumRange * 0.05f);
        return reading >= maximumRange - threshold
                ? Proximity.FAR : Proximity.NEAR;
    }
}
