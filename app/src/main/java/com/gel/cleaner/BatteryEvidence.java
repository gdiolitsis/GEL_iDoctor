package com.gel.cleaner;

/** Immutable, unit-typed observation. No derived battery health claims. */
public final class BatteryEvidence {
    public enum State { OBSERVED, UNAVAILABLE, INVALID }

    public final String metric;
    public final String unit;
    public final String source;
    public final State state;
    public final double value;
    public final long elapsedRealtimeNanos;
    public final String reason;

    private BatteryEvidence(String metric, String unit, String source,
                            State state, double value, long timestampNs, String reason) {
        this.metric = metric;
        this.unit = unit;
        this.source = source;
        this.state = state;
        this.value = value;
        this.elapsedRealtimeNanos = timestampNs;
        this.reason = reason;
    }

    public static BatteryEvidence observed(String metric, String unit, String source,
                                           double value, long timestampNs) {
        if (!Double.isFinite(value) || timestampNs < 0) {
            return invalid(metric, unit, source, "Non-finite value or invalid timestamp", timestampNs);
        }
        return new BatteryEvidence(metric, unit, source, State.OBSERVED, value, timestampNs, "");
    }

    public static BatteryEvidence unavailable(String metric, String unit, String source,
                                              String reason, long timestampNs) {
        return new BatteryEvidence(metric, unit, source, State.UNAVAILABLE,
                Double.NaN, timestampNs, reason);
    }

    public static BatteryEvidence invalid(String metric, String unit, String source,
                                          String reason, long timestampNs) {
        return new BatteryEvidence(metric, unit, source, State.INVALID,
                Double.NaN, timestampNs, reason);
    }

    public boolean isObserved() { return state == State.OBSERVED; }

    public static double microAmpsToMilliAmps(long microAmps) {
        return microAmps == Long.MIN_VALUE ? Double.NaN : microAmps / 1000.0d;
    }

    public static double microAmpHoursToMilliAmpHours(long microAmpHours) {
        return microAmpHours < 0 ? Double.NaN : microAmpHours / 1000.0d;
    }

    public static double nanoWattHoursToMilliWattHours(long nanoWattHours) {
        return nanoWattHours < 0 ? Double.NaN : nanoWattHours / 1000000.0d;
    }

    /** Parses a raw integer without guessing its physical unit or scaling by magnitude. */
    public static long parseSignedInteger(String raw) {
        if (raw == null || !raw.trim().matches("[+-]?[0-9]+")) {
            throw new NumberFormatException("Missing or non-integer device reading");
        }
        return Long.parseLong(raw.trim());
    }

    /** Do not synthesize SoH from cycle count, current, SOC or workload. */
    public static double capacityRatioPercent(double verifiedFullMah, double verifiedDesignMah) {
        if (!Double.isFinite(verifiedFullMah) || !Double.isFinite(verifiedDesignMah)
                || verifiedFullMah <= 0 || verifiedDesignMah <= 0) return Double.NaN;
        double ratio = 100.0d * verifiedFullMah / verifiedDesignMah;
        return ratio > 0 && ratio < 200 ? ratio : Double.NaN;
    }
}
