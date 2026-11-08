package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class AudioChallengeEvidenceTest {
    @Test public void playbackFailureCannotBeUserCertified() {
        assertEquals(AudioChallengeEvidence.Result.UNAVAILABLE,
                AudioChallengeEvidence.earpiece(false, true));
    }

    @Test public void playbackRequestWithoutUserConfirmationNotHealthy() {
        assertEquals(AudioChallengeEvidence.Result.INCONCLUSIVE,
                AudioChallengeEvidence.earpiece(true, false));
    }

    @Test public void confirmedEarpieceRemainsUserReportedOnly() {
        assertEquals(AudioChallengeEvidence.Result.USER_REPORTED_RESPONSE,
                AudioChallengeEvidence.earpiece(true, true));
    }

    @Test public void loopFailureCannotBeUserCertified() {
        assertEquals(AudioChallengeEvidence.Result.UNAVAILABLE,
                AudioChallengeEvidence.microphoneLoop(false, true));
    }

    @Test public void loopWithNoConfirmationIsInconclusive() {
        assertEquals(AudioChallengeEvidence.Result.INCONCLUSIVE,
                AudioChallengeEvidence.microphoneLoop(true, false));
    }

    @Test public void successfulLoopAndHumanHearingRemainUserReported() {
        assertEquals(AudioChallengeEvidence.Result.USER_REPORTED_RESPONSE,
                AudioChallengeEvidence.microphoneLoop(true, true));
    }

    @Test public void emptyOrZeroSampleLoopIsNotCompleted() {
        assertFalse(AudioChallengeEvidence.loopAttempted(5000, 160000, 160000, 0));
        assertFalse(AudioChallengeEvidence.loopAttempted(5000, 160000, 0, 100));
        assertFalse(AudioChallengeEvidence.loopAttempted(5000, 0, 160000, 100));
    }

    @Test public void shortLoopIsNotCompleted() {
        assertFalse(AudioChallengeEvidence.loopAttempted(3000, 160000, 160000, 100));
        assertFalse(AudioChallengeEvidence.loopAttempted(5000, 1000, 1000, 100));
        assertTrue(AudioChallengeEvidence.loopAttempted(4900, 64000, 64000, 100));
    }
}
