package com.gel.cleaner;

/** Startup restoration must not show purchase offers or error popups. */
class GelProBillingListener implements GelBillingManager.Listener {
    public void onBillingReady() {}
    public void onGelProActivated(boolean offer) {}
    public void onCustomReportsActivated() {}
    public void onPurchasePending(String productId) {}
    public void onPurchaseCancelled() {}
    public void onBillingError(String message) {}
}
