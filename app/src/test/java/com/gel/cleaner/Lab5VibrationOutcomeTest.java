package com.gel.cleaner;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class Lab5VibrationOutcomeTest {
    @Test public void vibrationNotRequestedCannotBecomeAUserPass() {
        assertEquals(Lab5VibrationOutcome.Status.UNAVAILABLE,
                Lab5VibrationOutcome.classify(false, false, true, true));
    }
    @Test public void lateYesAfterPauseIsCancellation() {
        assertEquals(Lab5VibrationOutcome.Status.CANCELLED,
                Lab5VibrationOutcome.classify(true, true, true, true));
    }
    @Test public void dismissAndBackgroundAreNotSuccessfulResponses() {
        assertEquals(Lab5VibrationOutcome.Status.CANCELLED,
                Lab5VibrationOutcome.classify(true, false, false, false));
    }
    @Test public void explicitUserYesRemainsUserReportedNotHardwareProof() {
        assertEquals(Lab5VibrationOutcome.Status.USER_REPORTED_FELT,
                Lab5VibrationOutcome.classify(true, false, true, true));
    }
    @Test public void explicitUserNoCannotEstablishMotorFailure() {
        assertEquals(Lab5VibrationOutcome.Status.USER_REPORTED_NOT_FELT,
                Lab5VibrationOutcome.classify(true, false, true, false));
    }
}
