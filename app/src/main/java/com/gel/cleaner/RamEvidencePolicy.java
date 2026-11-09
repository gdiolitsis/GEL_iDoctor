package com.gel.cleaner;

/** Separates OS low-memory signals from free-RAM percentage observations. */
public final class RamEvidencePolicy {
    public enum Assessment {
        INSUFFICIENT_DATA,
        ANDROID_LOW_MEMORY,
        LOW_AVAILABLE_RAM,
        NO_OS_LOW_MEMORY_SIGNAL
    }

    private RamEvidencePolicy() {}

    public static Assessment assess(int availablePercent, boolean androidLowMemory) {
        if (availablePercent < 0 || availablePercent > 100) {
            return Assessment.INSUFFICIENT_DATA;
        }
        if (androidLowMemory) return Assessment.ANDROID_LOW_MEMORY;
        if (availablePercent < 15) return Assessment.LOW_AVAILABLE_RAM;
        return Assessment.NO_OS_LOW_MEMORY_SIGNAL;
    }
}
