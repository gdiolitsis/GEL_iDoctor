package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab12CallEvidenceTest {
    @Test public void noTelephonyApiNeverBecomesHealthy() {
        assertEquals(Lab12CallEvidence.Passive.TELEPHONY_UNAVAILABLE,
                Lab12CallEvidence.passive(false, null, null, null));
    }

    @Test public void airplaneModeIsNotBrokenAntenna() {
        assertEquals(Lab12CallEvidence.Passive.AIRPLANE_MODE_REPORTED,
                Lab12CallEvidence.passive(true, true, true, false));
    }

    @Test public void voiceCapabilityAbsentIsNotHardwareFailure() {
        assertEquals(Lab12CallEvidence.Passive.VOICE_CAPABILITY_NOT_REPORTED,
                Lab12CallEvidence.passive(true, false, false, null));
    }

    @Test public void missingPermissionOrNullServiceIsUnknown() {
        assertEquals(Lab12CallEvidence.Passive.SERVICE_UNKNOWN,
                Lab12CallEvidence.passive(true, null, true, null));
    }

    @Test public void serviceReportedDoesNotCertifyActualCall() {
        assertEquals(Lab12CallEvidence.Passive.SERVICE_REPORTED_IN_SERVICE,
                Lab12CallEvidence.passive(true, false, true, true));
        assertEquals(Lab12CallEvidence.Guided.NOT_TESTED,
                Lab12CallEvidence.userReport(false, null, null, null));
    }

    @Test public void noServiceDoesNotProveVoiceHardwareFault() {
        assertEquals(Lab12CallEvidence.Passive.SERVICE_NOT_REPORTED_IN_SERVICE,
                Lab12CallEvidence.passive(true, false, true, false));
    }

    @Test public void dialerOpenIsNotCompletedRealCall() {
        assertEquals(Lab12CallEvidence.Guided.INCONCLUSIVE,
                Lab12CallEvidence.userReport(true, null, null, null));
    }

    @Test public void userDidNotMakeCallIsNotTested() {
        assertEquals(Lab12CallEvidence.Guided.NOT_TESTED,
                Lab12CallEvidence.userReport(true, false, null, null));
    }

    @Test public void partialHearingFeedbackIsNotTwoWay() {
        assertEquals(Lab12CallEvidence.Guided.INCONCLUSIVE,
                Lab12CallEvidence.userReport(true, true, true, null));
    }

    @Test public void yesYesIsUserReportedOnly() {
        assertEquals(Lab12CallEvidence.Guided.USER_REPORTED_TWO_WAY_AUDIO,
                Lab12CallEvidence.userReport(true, true, true, true));
    }

    @Test public void anyOneWayProblemRemainsUserReportedIssueOnly() {
        assertEquals(Lab12CallEvidence.Guided.USER_REPORTED_ONE_WAY_OR_AUDIO_ISSUE,
                Lab12CallEvidence.userReport(true, true, false, true));
        assertEquals(Lab12CallEvidence.Guided.USER_REPORTED_ONE_WAY_OR_AUDIO_ISSUE,
                Lab12CallEvidence.userReport(true, true, true, false));
        assertEquals(Lab12CallEvidence.Guided.USER_REPORTED_ONE_WAY_OR_AUDIO_ISSUE,
                Lab12CallEvidence.userReport(true, true, false, false));
    }

    @Test public void noDialerMeansNotTestedEvenWithStalePositiveAnswers() {
        assertEquals(Lab12CallEvidence.Guided.NOT_TESTED,
                Lab12CallEvidence.userReport(false, true, true, true));
    }
}
