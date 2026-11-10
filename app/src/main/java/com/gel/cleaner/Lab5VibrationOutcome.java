package com.gel.cleaner;

/** A vibration API request is not proof that physical vibration occurred. */
public final class Lab5VibrationOutcome {
    public enum Status { UNAVAILABLE, CANCELLED, USER_REPORTED_FELT,
                         USER_REPORTED_NOT_FELT }
    private Lab5VibrationOutcome() {}

    public static Status classify(boolean vibrationRequested, boolean cancelled,
                                  boolean userResponded, boolean userFelt) {
        if (!vibrationRequested) return Status.UNAVAILABLE;
        if (cancelled || !userResponded) return Status.CANCELLED;
        return userFelt ? Status.USER_REPORTED_FELT : Status.USER_REPORTED_NOT_FELT;
    }
}
