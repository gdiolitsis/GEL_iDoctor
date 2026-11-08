package com.gel.cleaner;

/**
 * Evidence contract shared by guided audio tests. A successful write/play call
 * is only a playback REQUEST; a user-confirmed audible result remains
 * USER_REPORTED, and never identifies a particular physical speaker or mic.
 */
public final class AudioChallengeEvidence {
    private AudioChallengeEvidence() {}

    public enum Result { UNAVAILABLE, INCONCLUSIVE, USER_REPORTED_RESPONSE }

    public static Result earpiece(boolean playbackRequested,
                                  boolean userHeardClearly) {
        if (!playbackRequested) return Result.UNAVAILABLE;
        return userHeardClearly ? Result.USER_REPORTED_RESPONSE : Result.INCONCLUSIVE;
    }

    public static Result microphoneLoop(boolean recordedAndWritten,
                                        boolean userHeardClearly) {
        if (!recordedAndWritten) return Result.UNAVAILABLE;
        return userHeardClearly ? Result.USER_REPORTED_RESPONSE : Result.INCONCLUSIVE;
    }

    /** A recording/write path must have processed real bytes for a meaningful window. */
    public static boolean loopAttempted(long elapsedMs, int bytesRecorded,
                                        int bytesWritten, int peakAbsSample) {
        return elapsedMs >= 4000 && bytesRecorded >= 32000
                && bytesWritten >= 32000 && peakAbsSample > 0;
    }
}
