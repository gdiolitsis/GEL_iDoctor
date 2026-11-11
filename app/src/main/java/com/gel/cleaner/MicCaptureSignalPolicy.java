package com.gel.cleaner;

/**
 * Minimal PCM gate for microphone profile observations.
 *
 * A value above this floor means measurable signal energy, not verified
 * speech, a particular physical microphone, or a noise-free recording.
 * Calibrated acoustic detection needs a controlled stimulus or noise baseline.
 */
public final class MicCaptureSignalPolicy {
    public static final double MIN_RMS_PCM = 20.0;

    private MicCaptureSignalPolicy() {}

    public static boolean aboveMinimalFloor(double rms, double peak) {
        return Double.isFinite(rms) && Double.isFinite(peak)
                && rms >= MIN_RMS_PCM && peak > 0.0;
    }
}
