package com.gel.cleaner;

/**
 * Battery stress evidence gate, not a diagnosis of cell collapse or fire risk.
 *
 * A short voltage sag and the following recovery share the same voltage
 * signal. Their ratio alone is NOT independent corroboration of instability.
 * Keep the existing two-indicator threshold instead of promoting the ratio
 * below 40% to a hard fault flag.
 */
public final class Lab14VoltageDropEvidence {
    private Lab14VoltageDropEvidence() {}

    public static boolean hasCorroboratedConcern(
            float sagVolts, float recoveryVolts, float dynamicResistanceOhms) {
        if (!Float.isFinite(sagVolts) || sagVolts <= 0f ||
                !Float.isFinite(recoveryVolts) || recoveryVolts < 0f ||
                !Float.isFinite(dynamicResistanceOhms) ||
                dynamicResistanceOhms <= 0f) {
            return false;
        }

        int indicators = 0;
        if (recoveryVolts < 0.035f) indicators++;
        if (sagVolts > 0.060f) indicators++;
        // This dynamic resistance indicator is not a lab-grade ESR measurement.
        if (dynamicResistanceOhms > 0.090f && dynamicResistanceOhms < 0.300f)
            indicators++;

        return indicators >= 2;
    }
}
