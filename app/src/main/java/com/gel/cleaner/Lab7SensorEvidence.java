package com.gel.cleaner;

/** Non-calibrated but reproducible sensor-event checks for guided Lab 7. */
public final class Lab7SensorEvidence {
    private Lab7SensorEvidence() {}

    public enum Orientation { PORTRAIT, LANDSCAPE, OTHER_OR_UNSTABLE }
    public enum Proximity { NEAR, FAR, UNKNOWN }

    /** Gravity-aligned pose, not a generic device shake or UI autorotation. */
    public static Orientation gravityOrientation(float x, float y, float z) {
        if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z)) {
            return Orientation.OTHER_OR_UNSTABLE;
        }
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length < 7.5 || length > 12.5 || Math.abs(z) > 7) {
            return Orientation.OTHER_OR_UNSTABLE;
        }
        if (Math.abs(y) >= 6 && Math.abs(x) <= 4) return Orientation.PORTRAIT;
        if (Math.abs(x) >= 6 && Math.abs(y) <= 4) return Orientation.LANDSCAPE;
        return Orientation.OTHER_OR_UNSTABLE;
    }

    /** OEM proximity maxRange is usually the 'far' state, not a measured distance. */
    public static Proximity proximityState(float reading, float maximumRange) {
        if (!Float.isFinite(reading) || !Float.isFinite(maximumRange)
                || maximumRange <= 0 || reading < 0) return Proximity.UNKNOWN;
        float threshold = Math.max(0.1f, maximumRange * 0.05f);
        return reading >= maximumRange - threshold
                ? Proximity.FAR : Proximity.NEAR;
    }

    /**
     * Guided pose sequence: stable portrait first, then stable landscape.
     * Sensor sample count alone is not stability. Nanosecond event timestamps
     * must be monotonic and each pose must remain consistent for >=800 ms.
     * Never represents UI autorotation or calibrated gyroscope health.
     */
    public static final class OrientationSequence {
        public static final long MIN_STABLE_NS = 800_000_000L;
        public static final long MAX_SAMPLE_GAP_NS = 500_000_000L;
        private static final int MIN_SAMPLES = 5;
        private static final double MAX_VECTOR_DRIFT = 1.25d; // m/s^2

        private int stage; // 0=await portrait, 1=await landscape, 2=observed
        private Orientation candidate = Orientation.OTHER_OR_UNSTABLE;
        private long firstCandidateNs = -1L;
        private long lastEventNs = -1L;
        private int candidateSamples;
        private float baselineX, baselineY, baselineZ;

        public boolean sawStablePortrait() { return stage >= 1; }
        public boolean completed() { return stage == 2; }
        public int samplesInCurrentPose() { return candidateSamples; }

        public void reset() {
            stage = 0;
            lastEventNs = -1L;
            clearCandidate();
        }

        private void clearCandidate() {
            candidate = Orientation.OTHER_OR_UNSTABLE;
            firstCandidateNs = -1L;
            candidateSamples = 0;
        }

        public boolean observe(float x, float y, float z, long timestampNs) {
            if (completed()) return true;
            if (timestampNs <= 0 || (lastEventNs >= 0 && timestampNs <= lastEventNs)) {
                clearCandidate(); // duplicate/out-of-order sensor time is not evidence
                return false;
            }

            long gap = lastEventNs < 0 ? 0 : timestampNs - lastEventNs;
            lastEventNs = timestampNs;
            Orientation orientation = gravityOrientation(x, y, z);
            Orientation required = stage == 0 ? Orientation.PORTRAIT
                    : Orientation.LANDSCAPE;
            if (orientation != required) {
                clearCandidate();
                return false;
            }

            double drift = Math.sqrt((x - baselineX) * (x - baselineX)
                    + (y - baselineY) * (y - baselineY)
                    + (z - baselineZ) * (z - baselineZ));
            if (candidate != orientation || gap > MAX_SAMPLE_GAP_NS
                    || drift > MAX_VECTOR_DRIFT) {
                candidate = orientation;
                firstCandidateNs = timestampNs;
                candidateSamples = 1;
                baselineX = x;
                baselineY = y;
                baselineZ = z;
                return false;
            }

            candidateSamples++;
            if (candidateSamples >= MIN_SAMPLES
                    && timestampNs - firstCandidateNs >= MIN_STABLE_NS) {
                stage++;
                clearCandidate();
            }
            return completed();
        }
    }

    /**
     * Proximity must yield a chronological FAR -> NEAR -> FAR sequence.
     * Event timestamps are monotonic; one event per state is sufficient
     * for on-change proximity sensors. This does not observe display power.
     */
    public static final class ProximitySequence {
        private static final long MIN_TRANSITION_NS = 200_000_000L;
        private int stage; // 0=await far, 1=await near, 2=await far, 3=observed
        private long lastEventNs = -1L;
        private long lastTransitionNs = -1L;

        public int stage() { return stage; }
        public boolean completed() { return stage == 3; }

        public void reset() {
            stage = 0;
            lastEventNs = -1L;
            lastTransitionNs = -1L;
        }

        public boolean observe(Proximity value, long timestampNs) {
            if (completed()) return true;
            if (value == null || value == Proximity.UNKNOWN || timestampNs <= 0
                    || (lastEventNs >= 0 && timestampNs <= lastEventNs)) {
                return false;
            }
            lastEventNs = timestampNs;
            if (stage == 0 && value == Proximity.FAR) {
                stage = 1;
                lastTransitionNs = timestampNs;
            } else if (stage == 1 && value == Proximity.NEAR
                    && timestampNs - lastTransitionNs >= MIN_TRANSITION_NS) {
                stage = 2;
                lastTransitionNs = timestampNs;
            } else if (stage == 2 && value == Proximity.FAR
                    && timestampNs - lastTransitionNs >= MIN_TRANSITION_NS) {
                stage = 3;
            }
            return completed();
        }
    }
}
