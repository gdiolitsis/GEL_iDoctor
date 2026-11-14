package com.gel.cleaner;

/**
 * LAB 8 — translates a short Camera2 sampling run and the user's visual
 * confirmation into a scoped *preview-test* outcome, not a hardware verdict.
 *
 * Sparse dark YUV samples are only luminance observations. A dark scene,
 * exposure convergence or the start of a camera session can produce them
 * without any user-visible preview failure.
 */
final class Lab8CameraEvidencePolicy {
    enum Verdict {
        CONFIRMED_PREVIEW,
        RETEST_STREAM_EVIDENCE,
        USER_REPORTED_PREVIEW_FAILURE,
        INCOMPLETE
    }

    private Lab8CameraEvidencePolicy() {}

    /**
     * UI noise tolerance, not a calibrated camera-failure threshold.
     * Up to 3% (and at least three samples) is treated as incidental only
     * when the user confirms that the live image was visible.
     */
    static long incidentalDarkFrameLimit(long frames) {
        if (frames <= 0L) return 0L;
        return Math.max(3L, (frames * 3L + 99L) / 100L);
    }

    static boolean isIncidentalDarkSample(long blackFrames, long frames) {
        return frames > 0L && blackFrames > 0L
                && blackFrames <= incidentalDarkFrameLimit(frames);
    }

    static Verdict evaluate(
            Boolean userConfirmedPreview,
            boolean sampleCompleted,
            boolean sessionFailed,
            long frames,
            float estimatedFps,
            long droppedFrames,
            long darkFrames
    ) {
        // Only ever report a final verdict *after* the user has answered.
        if (userConfirmedPreview == null) return Verdict.INCOMPLETE;
        if (!userConfirmedPreview) return Verdict.USER_REPORTED_PREVIEW_FAILURE;
        if (!sampleCompleted || sessionFailed || frames <= 0L)
            return Verdict.INCOMPLETE;
        if (!Float.isFinite(estimatedFps) || estimatedFps < 20f
                || droppedFrames > 0L
                || darkFrames > incidentalDarkFrameLimit(frames)) {
            // Keep the user's positive visual report, but flag substantial
            // telemetry disagreement for a repeated sample; do not diagnose
            // hardware failure from these proxies.
            return Verdict.RETEST_STREAM_EVIDENCE;
        }
        return Verdict.CONFIRMED_PREVIEW;
    }
}
