package com.gel.cleaner;

/** Pure validity gate for an observed BatteryManager charge-counter change. */
public final class BatteryChargeEvidence {
    private BatteryChargeEvidence() {}

    public static boolean isValidChargeGain(long startMah, long endMah,
                                            long elapsedMs, boolean interrupted) {
        if (interrupted || elapsedMs < 120000L || elapsedMs > 900000L
                || startMah <= 0L || endMah <= startMah) return false;
        long deltaMah = endMah - startMah;
        // Plausibility limit only; never clamp a counter into a fabricated result.
        return deltaMah > 0L && deltaMah <= 1000L;
    }
}
