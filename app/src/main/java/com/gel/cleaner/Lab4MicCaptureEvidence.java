package com.gel.cleaner;
/** Recording profiles do not prove separate microphone hardware identity. */
public final class Lab4MicCaptureEvidence {
    public enum State { UNAVAILABLE, SILENCE_OBSERVED, AUDIO_OBSERVED }
    private Lab4MicCaptureEvidence() {}
    public static State classify(boolean recordingStarted, boolean silenceDetected,
                                 double rms, double peak) {
        if (!recordingStarted || !Double.isFinite(rms) || !Double.isFinite(peak)
                || rms < 0 || peak < 0) return State.UNAVAILABLE;
        return silenceDetected || !MicCaptureSignalPolicy.aboveMinimalFloor(rms, peak)
                ? State.SILENCE_OBSERVED : State.AUDIO_OBSERVED;
    }
}
