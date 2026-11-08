package com.gel.cleaner;

/** Preserves missing thermal evidence as unknown rather than perfect health. */
public final class ThermalEvidencePolicy {
    private ThermalEvidencePolicy() {}

    /** -1 means no usable readings; a hidden high sensor is still evidence. */
    public static int score(int visibleReadings, int rawScore, boolean hiddenHigh) {
        if (visibleReadings <= 0 && !hiddenHigh) return -1;
        int score = Math.max(0, Math.min(100, rawScore));
        return hiddenHigh ? Math.min(score, 75) : score;
    }

    /** A snapshot cannot demonstrate historical thermal spikes. */
    public static String pattern(int score, boolean highObserved) {
        if (score < 0) return "UNAVAILABLE";
        return highObserved ? "ELEVATED_READING_OBSERVED" : "NO_ELEVATED_READING_OBSERVED";
    }
}
