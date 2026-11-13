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

    /** -1 means unavailable/inconsistent swap data, not zero usage. */
    public static long observedSwapUsedKb(long totalKb, long freeKb) {
        if (totalKb < 0 || freeKb < 0 || freeKb > totalKb) return -1L;
        return totalKb - freeKb;
    }

    public static Assessment assess(int availablePercent, boolean androidLowMemory) {
        if (availablePercent < 0 || availablePercent > 100) {
            return Assessment.INSUFFICIENT_DATA;
        }
        if (androidLowMemory) return Assessment.ANDROID_LOW_MEMORY;
        if (availablePercent < 15) return Assessment.LOW_AVAILABLE_RAM;
        return Assessment.NO_OS_LOW_MEMORY_SIGNAL;
    }
}
