package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab8EvidenceTest {
    @Test public void userYesWithoutStartedCameraCannotPass() {
        assertEquals(Lab8Evidence.Result.UNAVAILABLE,
                Lab8Evidence.classify(false, false, 0, 0, true));
    }

    @Test public void userYesWithoutFramesIsInconclusive() {
        assertEquals(Lab8Evidence.Result.INCONCLUSIVE,
                Lab8Evidence.classify(true, true, 0, 0, true));
    }

    @Test public void userYesWithoutLumaEvidenceIsInconclusive() {
        assertEquals(Lab8Evidence.Result.INCONCLUSIVE,
                Lab8Evidence.classify(true, true, 30, 0, true));
    }

    @Test public void noPrematureVerdictBeforeSamplingWindowEnds() {
        assertEquals(Lab8Evidence.Result.INCONCLUSIVE,
                Lab8Evidence.classify(true, false, 40, 39, true));
    }

    @Test public void missingHumanResponseIsNotCameraPass() {
        assertEquals(Lab8Evidence.Result.INCONCLUSIVE,
                Lab8Evidence.classify(true, true, 40, 39, null));
    }

    @Test public void observedFramesAndUserYesAreScopedPreviewEvidence() {
        assertEquals(Lab8Evidence.Result.OBSERVED_PREVIEW_WITH_USER_CONFIRMATION,
                Lab8Evidence.classify(true, true, 40, 39, true));
    }

    @Test public void userNoDoesNotProveHardwareFailure() {
        assertEquals(Lab8Evidence.Result.USER_REPORTED_NO_IMAGE,
                Lab8Evidence.classify(true, true, 40, 39, false));
    }

    @Test public void impossibleCountsFailClosed() {
        assertEquals(Lab8Evidence.Result.INCONCLUSIVE,
                Lab8Evidence.classify(true, true, 1, 3, true));
        assertEquals(Lab8Evidence.Result.INCONCLUSIVE,
                Lab8Evidence.classify(true, true, -1, 0, true));
    }

    @Test public void isolatedSuccessfulTrackDoesNotCertifyOptics() {
        // No hardware-health or lens/AF certification appears in Result.
        assertEquals(4, Lab8Evidence.Result.values().length);
    }
}
