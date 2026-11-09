package com.gel.cleaner;

/**
 * Lab 2 outcome classification independent of Android UI and hardware.
 * "Captured all" means complete sampling, not a verified loudspeaker.
 */
public final class AcousticLab2Outcome {
    public static final int REQUIRED_TONES = 4;

    public enum Status { OBSERVED, INCONCLUSIVE, PARTIAL, UNAVAILABLE }

    private AcousticLab2Outcome() {}

    public static Status classify(boolean capturedAll, boolean responseObserved,
                                  int capturedTones) {
        if (capturedTones <= 0 || capturedTones > REQUIRED_TONES) {
            return Status.UNAVAILABLE;
        }
        if (!capturedAll || capturedTones != REQUIRED_TONES) {
            return Status.PARTIAL;
        }
        return responseObserved ? Status.OBSERVED : Status.INCONCLUSIVE;
    }
}
