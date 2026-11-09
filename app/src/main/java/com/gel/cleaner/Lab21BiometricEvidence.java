package com.gel.cleaner;

/**
 * An Android BiometricPrompt session. A non-match callback is RETRYABLE and
 * never terminates an OS prompt. No code here asserts physical sensor health.
 */
final class Lab21BiometricEvidence {
    enum Outcome {
        NOT_STARTED,
        IN_PROGRESS,
        ANDROID_AUTHENTICATED,
        USER_CANCELLED,
        UNAVAILABLE_OR_ERROR
    }

    private Outcome outcome = Outcome.NOT_STARTED;
    private int nonMatchingAttempts;

    synchronized boolean start() {
        if (outcome != Outcome.NOT_STARTED) return false;
        outcome = Outcome.IN_PROGRESS;
        return true;
    }

    synchronized void onNonMatch() {
        if (outcome == Outcome.IN_PROGRESS) nonMatchingAttempts++;
    }

    synchronized boolean onAuthenticated() {
        if (outcome != Outcome.IN_PROGRESS) return false;
        outcome = Outcome.ANDROID_AUTHENTICATED;
        return true;
    }

    synchronized boolean onError(boolean userCancelled) {
        if (outcome != Outcome.IN_PROGRESS) return false;
        outcome = userCancelled
                ? Outcome.USER_CANCELLED
                : Outcome.UNAVAILABLE_OR_ERROR;
        return true;
    }

    synchronized int nonMatchingAttempts() {
        return nonMatchingAttempts;
    }

    synchronized Outcome outcome() {
        return outcome;
    }

    synchronized boolean isPending() {
        return outcome == Outcome.IN_PROGRESS;
    }
}
