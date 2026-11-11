package com.gel.cleaner;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class Lab4MicCaptureEvidenceTest {
    @Test public void recorderFailureCannotFabricateZeroReading() {
        assertEquals(Lab4MicCaptureEvidence.State.UNAVAILABLE,
                Lab4MicCaptureEvidence.classify(false, false, 0, 0));
        assertEquals(Lab4MicCaptureEvidence.State.UNAVAILABLE,
                Lab4MicCaptureEvidence.classify(false, false, 200, 2000));
    }
    @Test public void invalidLevelsCannotBecomeEvidence() {
        assertEquals(Lab4MicCaptureEvidence.State.UNAVAILABLE,
                Lab4MicCaptureEvidence.classify(true, false, Double.NaN, 10));
        assertEquals(Lab4MicCaptureEvidence.State.UNAVAILABLE,
                Lab4MicCaptureEvidence.classify(true, false, 1, -1));
    }
    @Test public void validRecorderMayCaptureActualSilence() {
        assertEquals(Lab4MicCaptureEvidence.State.SILENCE_OBSERVED,
                Lab4MicCaptureEvidence.classify(true, true, 0, 0));
        assertEquals(Lab4MicCaptureEvidence.State.SILENCE_OBSERVED,
                Lab4MicCaptureEvidence.classify(true, false, 0, 0));
    }
    @Test public void lowLevelFloorIsConsistentWithXiaomiProfileReadings() {
        assertEquals(Lab4MicCaptureEvidence.State.SILENCE_OBSERVED,
                Lab4MicCaptureEvidence.classify(true, false, 19.99, 32));
        assertEquals(Lab4MicCaptureEvidence.State.AUDIO_OBSERVED,
                Lab4MicCaptureEvidence.classify(true, false, 20.0, 30));
        assertEquals(Lab4MicCaptureEvidence.State.AUDIO_OBSERVED,
                Lab4MicCaptureEvidence.classify(true, false, 90.2, 300));
        assertEquals(Lab4MicCaptureEvidence.State.AUDIO_OBSERVED,
                Lab4MicCaptureEvidence.classify(true, false, 151.4, 841));
    }
    @Test public void silenceAndInvalidCaptureCannotBeUpgradedByHighRms() {
        assertEquals(Lab4MicCaptureEvidence.State.SILENCE_OBSERVED,
                Lab4MicCaptureEvidence.classify(true, true, 151.4, 841));
        assertEquals(Lab4MicCaptureEvidence.State.UNAVAILABLE,
                Lab4MicCaptureEvidence.classify(false, false, 151.4, 841));
    }
    @Test public void nonzeroProfileAudioIsObservedNotPhysicalMicCertification() {
        assertEquals(Lab4MicCaptureEvidence.State.AUDIO_OBSERVED,
                Lab4MicCaptureEvidence.classify(true, false, 41.3, 1800));
    }
}
