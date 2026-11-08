package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class AccountMessageStateTest {
    @Test public void backgroundBillingErrorCannotReplaceGiftGuidance() {
        AccountMessageState state = new AccountMessageState();
        state.onActionMessage("Sign in with Google first to check gifted access.");
        state.onBillingError();
        assertEquals("Sign in with Google first to check gifted access.",
                state.currentActionMessage());
        assertTrue(state.billingUnavailable());
    }

    @Test public void emptyRerenderDoesNotEraseActionResult() {
        AccountMessageState state = new AccountMessageState();
        state.onActionMessage("No Google account is signed in.");
        state.onActionMessage("");
        assertEquals("No Google account is signed in.", state.currentActionMessage());
    }

    @Test public void transientBillingStatusCanRecoverIndependently() {
        AccountMessageState state = new AccountMessageState();
        state.onActionMessage("Gifted access verified.");
        state.onBillingError();
        state.clearBillingError();
        assertFalse(state.billingUnavailable());
        assertEquals("Gifted access verified.", state.currentActionMessage());
    }
}
