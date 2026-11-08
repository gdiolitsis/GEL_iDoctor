package com.gel.cleaner;

/**
 * Pure, bounded per-axis statistics for public Android sensor events.
 * Observed change is NOT proof of sensor calibration, accuracy or hardware health.
 * Call only from the sensor event handler thread.
 */
public final class SensorAxisEvidence {
    private static final double MIN_OBSERVABLE_SPAN = 0.02d;
    private final int axes;
    private final double[] minimum;
    private final double[] maximum;
    private int validEvents;
    private int invalidEvents;
    private boolean monotonicTimestamps = true;
    private long lastTimestampNs = -1L;

    public SensorAxisEvidence(int axes) {
        if (axes != 1 && axes != 3) {
            throw new IllegalArgumentException("Expected one or three observed axes");
        }
        this.axes = axes;
        minimum = new double[axes];
        maximum = new double[axes];
        for (int i = 0; i < axes; i++) {
            minimum[i] = Double.POSITIVE_INFINITY;
            maximum[i] = Double.NEGATIVE_INFINITY;
        }
    }

    /**
     * A sample is valid only if all required axes and a strictly newer sensor
     * timestamp exist. Malformed events never update extrema or evidence counts.
     * The SDK's SensorEvent.timestamp uses monotonic elapsed-realtime nanoseconds.
     */
    public boolean observe(float[] values, long timestampNs) {
        if (timestampNs <= 0L || values == null || values.length < axes) {
            invalidEvents++;
            return false;
        }
        for (int i = 0; i < axes; i++) {
            if (!Float.isFinite(values[i])) {
                invalidEvents++;
                return false;
            }
        }
        if (lastTimestampNs >= 0L && timestampNs <= lastTimestampNs) {
            monotonicTimestamps = false;
            invalidEvents++;
            return false;
        }
        lastTimestampNs = timestampNs;
        validEvents++;
        for (int i = 0; i < axes; i++) {
            minimum[i] = Math.min(minimum[i], values[i]);
            maximum[i] = Math.max(maximum[i], values[i]);
        }
        return true;
    }

    public int validEvents() { return validEvents; }
    public int invalidEvents() { return invalidEvents; }
    public boolean timestampsMonotonic() { return monotonicTimestamps; }

    /**
     * A change on ANY required axis is recorded. The resolution-derived bound
     * reduces sub-resolution jitter but is not an OEM-calibrated noise model.
     * No change during a short passive window is NOT a device-failure verdict.
     */
    public boolean observedVariation(float sensorResolution) {
        if (validEvents < 2) return false;
        double resolution = Float.isFinite(sensorResolution) && sensorResolution > 0f
                ? (double) sensorResolution : 0d;
        double threshold = Math.max(MIN_OBSERVABLE_SPAN, resolution * 2d);
        for (int i = 0; i < axes; i++) {
            if (maximum[i] - minimum[i] >= threshold) return true;
        }
        return false;
    }
}
