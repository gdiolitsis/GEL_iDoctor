package com.gel.cleaner;

import java.util.Locale;

/**
 * Public-facing interpretation of two *uncalibrated* Lab 14 ratios.
 * The input numbers remain available; never present them as battery-cell
 * internal resistance, battery SOH, energy efficiency or fault probability.
 */
public final class Lab14DerivedRatioPolicy {
    private Lab14DerivedRatioPolicy() {}

    /**
     * The legacy value is calculated as voltage sag divided by |I_now|.
     * It is NOT a controlled current-step resistance (Delta V / Delta I).
     */
    public static boolean usableVoltageCurrentProxyMilliOhm(double value) {
        return Double.isFinite(value) && value >= 1.0d && value <= 300.0d;
    }

    public static String describeVoltageCurrentProxy(double value, boolean greek) {
        if (!usableVoltageCurrentProxyMilliOhm(value))
            return greek ? "ΜΗ ΔΙΑΘΕΣΙΜΟ — δεν προέκυψε αξιόπιστος λόγος."
                         : "UNAVAILABLE — no usable ratio.";
        return String.format(Locale.US, "%.1f mΩ — %s", value,
                greek
                        ? "πειραματικός λόγος ΔV/|I|, όχι μετρημένη αντίσταση κυψελών/SoH"
                        : "exploratory ΔV/|I| ratio, not measured battery-cell resistance/SoH");
    }

    /**
     * Legacy numerator is consumption rate (mAh/h), denominator voltage
     * sag (V). The ratio's units are mAh/(h·V), not mAh/V and definitely
     * not electrical energy conversion efficiency.
     */
    public static boolean usableDischargeToSagRatio(double value) {
        return Double.isFinite(value) && value > 10.0d && value < 5000.0d;
    }

    public static String describeDischargeToSagRatio(double value, boolean greek) {
        if (!usableDischargeToSagRatio(value))
            return greek ? "ΜΗ ΔΙΑΘΕΣΙΜΟ — ανεπαρκή συγχρονισμένα στοιχεία."
                         : "UNAVAILABLE — insufficient aligned observations.";
        return String.format(Locale.US, "%.0f mAh/(h·V) — %s", value,
                greek
                        ? "μη βαθμονομημένος δείκτης, ΟΧΙ ενεργειακή απόδοση"
                        : "uncalibrated exploratory index, NOT energy efficiency");
    }

    /** Real current-step DCIR must not be reported without an aligned
     * before/after current difference, irrespective of voltage ratio. */
    public static boolean hasCurrentStepEvidence(boolean synchronizedSamples,
                                                  double initialCurrentAmp,
                                                  double loadedCurrentAmp) {
        return synchronizedSamples
                && Double.isFinite(initialCurrentAmp)
                && Double.isFinite(loadedCurrentAmp)
                && Math.abs(loadedCurrentAmp - initialCurrentAmp) >= 0.1d;
    }
}
