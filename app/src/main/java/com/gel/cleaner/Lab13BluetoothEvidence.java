package com.gel.cleaner;

/**
 * Lab13 evidence contract: passive Android-level Bluetooth indicators.
 *
 * A general ACL broadcast or local profile state may reflect ANY connected
 * peripheral, not a chosen/identified GATT device. This class deliberately
 * does not offer a physical hardware or RF stability PASS/FAIL result.
 */
final class Lab13BluetoothEvidence {
    enum Result {
        NOT_TESTED,
        NO_ACCESSORY_OBSERVED,
        INCOMPLETE,
        ADAPTER_UNAVAILABLE,
        AGGREGATE_LINK_OBSERVED,
        AGGREGATE_LINK_CHANGES_OBSERVED
    }
    private Lab13BluetoothEvidence() {}

    static Result classify(boolean userSkipped, boolean monitorStarted,
                           boolean adapterAvailableThroughout,
                           boolean anyLinkObserved, long elapsedMs,
                           long plannedWindowMs, int disconnections,
                           int reconnections, int ambiguousTransitions) {
        if (userSkipped) return Result.NOT_TESTED;
        if (monitorStarted && !adapterAvailableThroughout) {
            return Result.ADAPTER_UNAVAILABLE;
        }
        if (!anyLinkObserved) return Result.NO_ACCESSORY_OBSERVED;
        if (!monitorStarted || plannedWindowMs <= 0
                || elapsedMs < plannedWindowMs
                || disconnections < 0 || reconnections < 0
                || ambiguousTransitions < 0) {
            return Result.INCOMPLETE;
        }
        if (disconnections > 0 || reconnections > 0
                || ambiguousTransitions > 0) {
            return Result.AGGREGATE_LINK_CHANGES_OBSERVED;
        }
        return Result.AGGREGATE_LINK_OBSERVED;
    }

    static int boundedSeconds(long elapsedMs, int durationSeconds) {
        if (elapsedMs <= 0 || durationSeconds <= 0) return 0;
        return (int) Math.min(durationSeconds,
                elapsedMs / 1000L);
    }
}
