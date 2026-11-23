package com.gel.cleaner;

/** Evidence from live hardware events; absence of an event never proves a defect. */
public final class EssentialHardwareEvidence {
    private EssentialHardwareEvidence() {}

    public static boolean gyroMoved(float[] v) {
        return finite3(v) && Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]) > 0.25;
    }

    public static boolean magneticFieldChanged(float[] first, float[] current) {
        if (!finite3(first) || !finite3(current)) return false;
        double dx = current[0] - first[0], dy = current[1] - first[1], dz = current[2] - first[2];
        return Math.sqrt(dx*dx + dy*dy + dz*dz) > 4.0;
    }

    public static boolean ambientLightChanged(float first, float current) {
        return Float.isFinite(first) && Float.isFinite(current)
                && first >= 0f && current >= 0f
                && Math.abs(first - current) > Math.max(3f, first * 0.25f);
    }

    public static boolean newGpsFix(long startElapsedNanos, long fixElapsedNanos,
                                    boolean mock, float accuracyMeters) {
        return startElapsedNanos > 0 && fixElapsedNanos >= startElapsedNanos
                && !mock && Float.isFinite(accuracyMeters)
                && accuracyMeters >= 0f;
    }

    private static boolean finite3(float[] v) {
        return v != null && v.length >= 3
                && Float.isFinite(v[0]) && Float.isFinite(v[1]) && Float.isFinite(v[2]);
    }
}
