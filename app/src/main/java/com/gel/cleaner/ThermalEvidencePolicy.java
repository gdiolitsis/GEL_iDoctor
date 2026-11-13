package com.gel.cleaner;

/** Preserves missing thermal evidence as unknown rather than perfect health. */
public final class ThermalEvidencePolicy {
    private ThermalEvidencePolicy() {}

    /** A battery-only reading cannot score device thermal health. A visible
     *  internal (CPU/GPU/SoC) sensor is required for a qualified snapshot score.
     *  Unknown coverage is -1, while a separately observed hidden high sensor
     *  remains a warning in the Lab's danger flag. */
    public static int score(int internalReadings, int peripheralReadings, int rawScore, boolean hiddenHigh) {
        if (internalReadings <= 0 || peripheralReadings < 0) return -1;
        int score = Math.max(0, Math.min(100, rawScore));
        return hiddenHigh ? Math.min(score, 75) : score;
    }

    /** A snapshot cannot demonstrate historical thermal spikes. */
    public static String pattern(int score, boolean highObserved) {
        // Never hide a real critical reading or Android thermal warning
        // solely because accessible CPU/GPU coverage is insufficient.
        if (score < 0) return highObserved
                ? "THERMAL_WARNING_WITH_LIMITED_COVERAGE" : "UNAVAILABLE";
        return highObserved ? "ELEVATED_READING_OBSERVED" : "NO_ELEVATED_READING_OBSERVED";
    }
}
