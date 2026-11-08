package com.gel.cleaner;

/** Keeps background billing availability separate from the active account action. */
public final class AccountMessageState {
    private String actionMessage = "";
    private boolean billingUnavailable;

    public void onActionMessage(String message) {
        if (message != null && !message.isEmpty()) actionMessage = message;
    }

    public String currentActionMessage() {
        return actionMessage;
    }

    public void onBillingError() {
        billingUnavailable = true;
    }

    public void clearBillingError() {
        billingUnavailable = false;
    }

    public boolean billingUnavailable() {
        return billingUnavailable;
    }
}
