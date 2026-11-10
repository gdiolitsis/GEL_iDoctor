package com.gel.cleaner;

/** Bounded comparison of two Android-reported voltage/temperature snapshots.
 * No power-controller or device-health inference is permitted from two samples.
 */
public final class Lab28TemporalEvidence {
    public enum State { NO_REFERENCE, TOO_SOON, EXPIRED_OR_REBOOTED,
                        NO_COMMON_SIGNALS, OBSERVED_PAIR }

    public static final class Result {
        public final State state;
        public final long elapsedMs;
        public final double voltageDeltaMv;
        public final double tempDeltaC;

        private Result(State state, long elapsedMs,
                       double voltageDeltaMv, double tempDeltaC) {
            this.state = state;
            this.elapsedMs = elapsedMs;
            this.voltageDeltaMv = voltageDeltaMv;
            this.tempDeltaC = tempDeltaC;
        }
    }

    public static Result compare(long beforeElapsed, long beforeWall,
                                 long nowElapsed, long nowWall,
                                 double beforeVoltageMv, double nowVoltageMv,
                                 double beforeTempC, double nowTempC) {
        if (beforeElapsed < 0 || beforeWall <= 0 || nowElapsed < 0 || nowWall <= 0) {
            return result(State.NO_REFERENCE, 0);
        }
        long elapsed = nowElapsed - beforeElapsed;
        long wall = nowWall - beforeWall;
        // Reject reboot, wall-clock skew, stale records, and invalid order.
        if (elapsed < 0 || wall < 0 || Math.abs(wall - elapsed) > 120000L
                || elapsed > 7200000L) {
            return result(State.EXPIRED_OR_REBOOTED, elapsed);
        }
        if (elapsed < 30000L) return result(State.TOO_SOON, elapsed);
        boolean hasVoltage = finiteInRange(beforeVoltageMv, 1000, 20000)
                && finiteInRange(nowVoltageMv, 1000, 20000);
        boolean hasTemp = finiteInRange(beforeTempC, -40, 120)
                && finiteInRange(nowTempC, -40, 120);
        if (!hasVoltage && !hasTemp) return result(State.NO_COMMON_SIGNALS, elapsed);
        return new Result(State.OBSERVED_PAIR, elapsed,
                hasVoltage ? nowVoltageMv - beforeVoltageMv : Double.NaN,
                hasTemp ? nowTempC - beforeTempC : Double.NaN);
    }

    private static boolean finiteInRange(double v, double min, double max) {
        return Double.isFinite(v) && v >= min && v <= max;
    }
    private static Result result(State state, long elapsedMs) {
        return new Result(state, elapsedMs, Double.NaN, Double.NaN);
    }
}
