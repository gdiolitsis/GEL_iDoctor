package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab3StaticAudioTrackPolicyTest {
    @Test public void staticTrackNoDataBeforeWriteIsNotAnInitializationFailure() {
        assertTrue(Lab3StaticAudioTrackPolicy.canAcceptStaticWrite(
                Lab3StaticAudioTrackPolicy.STATE_NO_STATIC_DATA));
        assertTrue(Lab3StaticAudioTrackPolicy.canAcceptStaticWrite(
                Lab3StaticAudioTrackPolicy.STATE_INITIALIZED));
        assertFalse(Lab3StaticAudioTrackPolicy.canAcceptStaticWrite(
                Lab3StaticAudioTrackPolicy.STATE_UNINITIALIZED));
    }

    @Test public void trackMustBeInitializedAndFullyWrittenBeforePlay() {
        assertTrue(Lab3StaticAudioTrackPolicy.canRequestPlayback(
                Lab3StaticAudioTrackPolicy.STATE_INITIALIZED, 3200, 3200));
        assertFalse(Lab3StaticAudioTrackPolicy.canRequestPlayback(
                Lab3StaticAudioTrackPolicy.STATE_NO_STATIC_DATA, 3200, 3200));
        assertFalse(Lab3StaticAudioTrackPolicy.canRequestPlayback(
                Lab3StaticAudioTrackPolicy.STATE_INITIALIZED, 1599, 3200));
        assertFalse(Lab3StaticAudioTrackPolicy.canRequestPlayback(
                Lab3StaticAudioTrackPolicy.STATE_INITIALIZED, 0, 0));
    }

    @Test public void playbackFailuresAreExplainedInGreekAndEnglish() {
        for (String code : new String[]{
                "NOT_CREATED", "WRITE_INCOMPLETE", "NOT_READY_AFTER_WRITE",
                "PLAY_EXCEPTION", "SESSION_CANCELLED", "UNKNOWN"}) {
            assertNotNull(Lab3StaticAudioTrackPolicy.failureExplanation(code, true));
            assertFalse(Lab3StaticAudioTrackPolicy.failureExplanation(code, true).isEmpty());
            assertNotNull(Lab3StaticAudioTrackPolicy.failureExplanation(code, false));
        }
        assertTrue(Lab3StaticAudioTrackPolicy.failureExplanation("NOT_CREATED", true)
                .contains("Android"));
    }
}
