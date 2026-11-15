package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab8CameraEvidencePolicyTest {
    @Test public void rearCamera_134FramesZeroDark_userConfirmed_isGreen() {
        assertEquals(Lab8CameraEvidencePolicy.Verdict.CONFIRMED_PREVIEW,
                Lab8CameraEvidencePolicy.evaluate(
                        true, true, false, 134L, 24.1f, 0L, 0L));
    }

    @Test public void frontCamera_139FramesThreeDark_userConfirmed_isGreen() {
        assertTrue(Lab8CameraEvidencePolicy.isIncidentalDarkSample(3L, 139L));
        assertEquals(Lab8CameraEvidencePolicy.Verdict.CONFIRMED_PREVIEW,
                Lab8CameraEvidencePolicy.evaluate(
                        true, true, false, 139L, 25.6f, 0L, 3L));
    }

    @Test public void beforeUserResponse_neverProducesGreen() {
        assertEquals(Lab8CameraEvidencePolicy.Verdict.INCOMPLETE,
                Lab8CameraEvidencePolicy.evaluate(
                        null, true, false, 139L, 25.6f, 0L, 0L));
    }

    @Test public void userSawNoPreview_overridesGoodCounters() {
        assertEquals(Lab8CameraEvidencePolicy.Verdict.USER_REPORTED_PREVIEW_FAILURE,
                Lab8CameraEvidencePolicy.evaluate(
                        false, true, false, 139L, 25.6f, 0L, 0L));
    }

    @Test public void sessionFailure_preventsFalseGreenEvenIfUserSaysYes() {
        assertEquals(Lab8CameraEvidencePolicy.Verdict.INCOMPLETE,
                Lab8CameraEvidencePolicy.evaluate(
                        true, false, true, 0L, 0f, 0L, 0L));
    }

    @Test public void substantialDarkSamplesRequireRetestWithoutHardwareVerdict() {
        assertEquals(Lab8CameraEvidencePolicy.Verdict.RETEST_STREAM_EVIDENCE,
                Lab8CameraEvidencePolicy.evaluate(
                        true, true, false, 139L, 25.6f, 0L, 16L));
    }

    @Test public void significantFrameGapsRequireRetest() {
        assertEquals(Lab8CameraEvidencePolicy.Verdict.RETEST_STREAM_EVIDENCE,
                Lab8CameraEvidencePolicy.evaluate(
                        true, true, false, 139L, 25.6f, 2L, 0L));
    }

    @Test public void veryLowFpsRequiresRetest() {
        assertEquals(Lab8CameraEvidencePolicy.Verdict.RETEST_STREAM_EVIDENCE,
                Lab8CameraEvidencePolicy.evaluate(
                        true, true, false, 45L, 9.0f, 0L, 0L));
    }
}
