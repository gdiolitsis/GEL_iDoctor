package com.gel.cleaner;

/** Discharge evidence computed from one counter source and elapsed monotonic time. */
public final class BatteryDrainEvidence {
    private BatteryDrainEvidence() {}

    /**
     * Returns mAh/h, or NaN if the observed change cannot support a discharge rate.
     * Charge-counter readings must already be converted from the same Android
     * BatteryManager property (microampere-hours) into milliampere-hours.
     */
    public static double dischargeMilliAmpHoursPerHour(
            long startMah, long endMah, long elapsedMs,
            boolean startedCharging, boolean endedCharging) {
        if (startedCharging || endedCharging || elapsedMs < 240000L
                || startMah <= 0 || endMah <= 0 || endMah >= startMah) {
            return Double.NaN;
        }
        double deltaMah = (double) startMah - endMah;
        double hours = elapsedMs / 3600000.0d;
        double result = deltaMah / hours;
        // Bounds ensure plausibility, not correction: reject outliers rather than
        // clipping them into manufactured values.
        return Double.isFinite(result) && result > 0 && result <= 15000.0d
                ? result : Double.NaN;
    }
}
