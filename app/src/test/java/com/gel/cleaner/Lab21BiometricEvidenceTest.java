package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab21BiometricEvidenceTest {
    @Test public void unstartedPromptIsNotSuccess() {
        assertEquals(Lab21BiometricEvidence.Outcome.NOT_STARTED,
                new Lab21BiometricEvidence().outcome());
    }

    @Test public void nonMatchDoesNotFinishPrompt() {
        Lab21BiometricEvidence x = new Lab21BiometricEvidence();
        assertTrue(x.start());
        x.onNonMatch();
        assertTrue(x.isPending());
        assertEquals(1, x.nonMatchingAttempts());
    }

    @Test public void userCanRetryAndEventuallyAuthenticate() {
        Lab21BiometricEvidence x = new Lab21BiometricEvidence();
        assertTrue(x.start());
        x.onNonMatch();
        x.onNonMatch();
        assertTrue(x.onAuthenticated());
        assertEquals(Lab21BiometricEvidence.Outcome.ANDROID_AUTHENTICATED, x.outcome());
        assertEquals(2, x.nonMatchingAttempts());
    }

    @Test public void errorIsNotHardwareFailure() {
        Lab21BiometricEvidence x = new Lab21BiometricEvidence();
        assertTrue(x.start());
        assertTrue(x.onError(false));
        assertEquals(Lab21BiometricEvidence.Outcome.UNAVAILABLE_OR_ERROR, x.outcome());
    }

    @Test public void voluntaryCancellationIsDistinctFromError() {
        Lab21BiometricEvidence x = new Lab21BiometricEvidence();
        x.start();
        assertTrue(x.onError(true));
        assertEquals(Lab21BiometricEvidence.Outcome.USER_CANCELLED, x.outcome());
    }

    @Test public void staleSuccessAfterCancellationIsIgnored() {
        Lab21BiometricEvidence x = new Lab21BiometricEvidence();
        x.start();
        x.onError(true);
        assertFalse(x.onAuthenticated());
        assertEquals(Lab21BiometricEvidence.Outcome.USER_CANCELLED, x.outcome());
    }

    @Test public void staleErrorAfterSuccessIsIgnored() {
        Lab21BiometricEvidence x = new Lab21BiometricEvidence();
        x.start();
        x.onAuthenticated();
        assertFalse(x.onError(false));
        assertEquals(Lab21BiometricEvidence.Outcome.ANDROID_AUTHENTICATED, x.outcome());
    }

    @Test public void duplicateStartAndDuplicateSuccessAreIgnored() {
        Lab21BiometricEvidence x = new Lab21BiometricEvidence();
        assertTrue(x.start());
        assertFalse(x.start());
        assertTrue(x.onAuthenticated());
        assertFalse(x.onAuthenticated());
    }

    @Test public void noMatchAfterTerminalDoesNotAlterOutcomeOrCount() {
        Lab21BiometricEvidence x = new Lab21BiometricEvidence();
        x.start();
        x.onError(false);
        x.onNonMatch();
        assertEquals(0, x.nonMatchingAttempts());
        assertEquals(Lab21BiometricEvidence.Outcome.UNAVAILABLE_OR_ERROR, x.outcome());
    }

    @Test public void noSuccessPossibleWithoutActivePrompt() {
        Lab21BiometricEvidence x = new Lab21BiometricEvidence();
        assertFalse(x.onAuthenticated());
        assertFalse(x.onError(true));
        assertEquals(Lab21BiometricEvidence.Outcome.NOT_STARTED, x.outcome());
    }
}
