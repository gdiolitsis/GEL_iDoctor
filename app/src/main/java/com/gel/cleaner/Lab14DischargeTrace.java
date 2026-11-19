package com.gel.cleaner;

/**
 * Battery evidence from a complete, timestamped discharge session.
 * Primary evidence is the initial and final CHARGE_COUNTER readings,
 * timestamped across the whole completed test. Intermediate polling is optional
 * consistency evidence; gaps never invalidate otherwise valid endpoints.
 * Time-weighted CURRENT_NOW is only a fallback when sufficient current samples
 * really cover the session. Never select the largest of competing estimates.
 * No Android dependencies: independently testable.
 */
public final class Lab14DischargeTrace {
    private static final long MAX_GAP_MS = 2500L;
    private long firstMs = -1L;
    private long lastMs = -1L;
    private long lastRequestMs = -1L;
    private long firstChargeMs = -1L;
    private long lastChargeMs = -1L;
    private long firstChargeMah = -1L;
    private long lastChargeMah = -1L;
    private long counterCoveredMs = 0L;
    private long currentCoveredMs = 0L;
    private double currentMaMs = 0d;
    private float lastCurrentMa = Float.NaN;
    private long lastCurrentMs = -1L;
    private int counterSamples = 0;
    private int currentSamples = 0;
    private boolean chargingObserved = false;
    private boolean counterUnstable = false;

    /** Used by the fast UI refresh loop; at most one sensor poll per second. */
    public synchronized boolean shouldSample(long nowMs) {
        if (lastRequestMs >= 0 && nowMs - lastRequestMs < 950L) return false;
        lastRequestMs = nowMs;
        return true;
    }

    public synchronized void addSample(long nowMs, long chargeMah, float currentMa,
                                        boolean charging) {
        if (nowMs < 0 || (lastMs >= 0 && nowMs <= lastMs)) return;
        if (firstMs < 0) firstMs = nowMs;
        lastMs = nowMs;
        if (charging) {
            chargingObserved = true;
            lastCurrentMa = Float.NaN;
            lastCurrentMs = -1L;
            return;
        }
        if (chargeMah > 0L && chargeMah < 100000L) {
            if (lastChargeMs >= 0) {
                long dt = nowMs - lastChargeMs;
                if (dt > 0 && dt <= MAX_GAP_MS) {
                    counterCoveredMs += dt;
                    // More than 5 mAh increase while unplugged: unstable counter.
                    if (chargeMah > lastChargeMah + 5L) counterUnstable = true;
                    // A 100 mAh step in <= 2.5s is a discontinuity, not discharge.
                    if (lastChargeMah - chargeMah > 100L) counterUnstable = true;
                }
            }
            if (firstChargeMs < 0) {
                firstChargeMs = nowMs;
                firstChargeMah = chargeMah;
            }
            lastChargeMs = nowMs;
            lastChargeMah = chargeMah;
            counterSamples++;
        } else {
            lastChargeMs = -1L; // break the coverage interval on missing data
        }

        float absCurrent = Math.abs(currentMa);
        boolean currentValid = !Float.isNaN(absCurrent) && !Float.isInfinite(absCurrent)
                && absCurrent >= 50f && absCurrent <= 12000f;
        if (currentValid) {
            if (lastCurrentMs >= 0L && !Float.isNaN(lastCurrentMa)) {
                long dt = nowMs - lastCurrentMs;
                if (dt > 0 && dt <= MAX_GAP_MS) {
                    currentCoveredMs += dt;
                    currentMaMs += (lastCurrentMa + absCurrent) * 0.5d * dt;
                }
            }
            lastCurrentMs = nowMs;
            lastCurrentMa = absCurrent;
            currentSamples++;
        } else {
            lastCurrentMs = -1L;
            lastCurrentMa = Float.NaN;
        }
    }

    public synchronized Result evaluate(long requiredMs) {
        if (firstMs < 0 || lastMs < firstMs || requiredMs < 20000L)
            return Result.unavailable("NO_FULL_SESSION");
        double counterPct = 100d * Math.min(1d, counterCoveredMs / (double) requiredMs);
        double currentPct = 100d * Math.min(1d, currentCoveredMs / (double) requiredMs);
        if (chargingObserved) return new Result(false, Double.NaN,
                "NONE", counterPct, currentPct, "CHARGING_DURING_TEST");
        if (lastMs - firstMs < requiredMs * 0.95d)
            return new Result(false, Double.NaN,
                "NONE", counterPct, currentPct, "INCOMPLETE_DURATION");

        // The hardware charge counter measures state of charge (mAh).
        // Start/end readings describe the entire run even with no intermediate polls.
        // Only accept the same run, plausible monotonic discharge, and near-full timing.
        double counterRate = Double.NaN;
        if (!counterUnstable && firstChargeMs >= 0L && lastChargeMs > firstChargeMs
                && lastChargeMs - firstChargeMs >= requiredMs * 0.95d
                && firstChargeMah > lastChargeMah) {
            counterRate = (firstChargeMah - lastChargeMah) * 3600000d
                    / (lastChargeMs - firstChargeMs);
            if (counterRate < 10d || counterRate > 12000d) counterRate = Double.NaN;
        }

        // Optional fallback: integrate independently observed current only if its
        // measured time coverage is genuinely high (not extrapolated across gaps).
        int minimumSamples = Math.max(12, (int) (requiredMs / 10000L));
        double currentRate = Double.NaN;
        if (currentSamples >= minimumSamples && currentPct >= 90d && currentCoveredMs > 0L) {
            currentRate = currentMaMs / currentCoveredMs;
            if (currentRate < 50d || currentRate > 12000d) currentRate = Double.NaN;
        }

        if (!Double.isNaN(counterRate)) {
            // Large conflict warns of a bad instantaneous current sensor.
            // Never override a coherent start/end charge counter with current estimates.
            String reason = "OK";
            if (!Double.isNaN(currentRate)) {
                double ratio = Math.max(counterRate, currentRate)
                        / Math.min(counterRate, currentRate);
                if (ratio > 2.5d) reason = "COUNTER_CURRENT_DISAGREEMENT";
            }
            return new Result(true, counterRate, "CHARGE_COUNTER",
                    counterPct, currentPct, reason);
        }

        if (!Double.isNaN(currentRate))
            return new Result(true, currentRate, "CURRENT_INTEGRATION",
                    counterPct, currentPct, "OK_CURRENT_ONLY");

        return new Result(false, Double.NaN, "NONE", counterPct, currentPct,
                counterUnstable ? "UNSTABLE_COUNTER" : "INSUFFICIENT_DATA");
    }

    public static final class Result {
        public final boolean valid;
        public final double rateMahPerHour;
        public final String source;
        public final double counterCoveragePct;
        public final double currentCoveragePct;
        public final String reason;

        private Result(boolean valid, double rateMahPerHour, String source,
                       double counterCoveragePct, double currentCoveragePct, String reason) {
            this.valid = valid;
            this.rateMahPerHour = rateMahPerHour;
            this.source = source;
            this.counterCoveragePct = counterCoveragePct;
            this.currentCoveragePct = currentCoveragePct;
            this.reason = reason;
        }

        public static Result unavailable(String reason) {
            return new Result(false, Double.NaN, "NONE", 0d, 0d, reason);
        }
    }
}
