// GDiolitsis Engine Lab (GEL) — Author & Developer
// GelBillingManager.java
// Google Play Billing Library 9.1.0
//
// Central billing layer for:
// 1) GEL PRO monthly subscription
// 2) GEL Custom Reports one-time purchase
//
// IMPORTANT:
// - Create the SAME product IDs in Google Play Console.
// - GEL PRO uses the existing app entitlement:
//      SharedPreferences "GEL_PRO_ENTITLEMENT" / boolean "active"
// - Custom Reports uses:
//      SharedPreferences "GEL_CUSTOM_REPORT_ENTITLEMENT" / boolean "active"

package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.Collections;
import java.util.List;

public final class GelBillingManager implements PurchasesUpdatedListener {

    // ============================================================
    // GOOGLE PLAY CONSOLE IDs
    // ============================================================
    // Subscription product:
    // Product ID: gel_pro_monthly
    //
    // Recommended base plan:
    // Base plan ID: monthly
    //
    // One-time product:
    // Product ID: gel_custom_reports
    // ============================================================
    public static final String PRODUCT_GEL_PRO_MONTHLY = "gel_pro_monthly";
    public static final String GEL_PRO_BASE_PLAN_ID = "monthly";
    public static final String PRODUCT_CUSTOM_REPORTS = "gel_custom_reports";

    // ============================================================
    // EXISTING GEL PRO ENTITLEMENT — DO NOT RENAME
    // ============================================================
    private static final String GEL_PRO_PREFS = "GEL_PRO_ENTITLEMENT";
    private static final String GEL_PRO_ACTIVE_KEY = "active";

    // ============================================================
    // CUSTOM REPORT ENTITLEMENT
    // ============================================================
    private static final String GEL_CUSTOM_PREFS =
            "GEL_CUSTOM_REPORT_ENTITLEMENT";

    private static final String GEL_CUSTOM_ACTIVE_KEY = "active";

    // Prevent the €29.99 offer from being shown repeatedly.
    private static final String GEL_CUSTOM_OFFER_SHOWN_KEY =
            "custom_offer_shown";

    private final Context appContext;
    private final Listener listener;
    private final BillingClient billingClient;

    @Nullable
    private ProductDetails gelProDetails;

    @Nullable
    private ProductDetails customReportsDetails;

    private boolean billingReady = false;

    // ============================================================
    // CALLBACKS TO THE ACTIVITY
    // ============================================================
    public interface Listener {

        // Billing connected and ProductDetails queries have been started.
        void onBillingReady();

        // Called after GEL PRO becomes PURCHASED and acknowledged.
        // true = show the "Custom Reports €29.99?" question now.
        void onGelProActivated(boolean showCustomReportsOffer);

        // Called after the one-time Custom Reports purchase is PURCHASED
        // and acknowledged.
        void onCustomReportsActivated();

        // Google Play reports a pending transaction.
        void onPurchasePending(@NonNull String productId);

        // User closed/cancelled the Play purchase sheet.
        void onPurchaseCancelled();

        // Billing error or missing Play product.
        void onBillingError(@NonNull String message);
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    public GelBillingManager(
            @NonNull Context context,
            @NonNull Listener listener
    ) {
        this.appContext = context.getApplicationContext();
        this.listener = listener;

        PendingPurchasesParams pendingPurchasesParams =
                PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts()
                        .build();

        this.billingClient =
                BillingClient.newBuilder(appContext)
                        .setListener(this)
                        .enablePendingPurchases(pendingPurchasesParams)
                        .enableAutoServiceReconnection()
                        .build();
    }

    // ============================================================
    // START / STOP
    // ============================================================
    public void start() {

        if (billingClient.isReady()) {
            billingReady = true;

            // Restore current entitlements from Google Play.
            refreshPurchases();

            // Load products/prices/offers.
            queryProducts();

            listener.onBillingReady();
            return;
        }

        billingClient.startConnection(
                new BillingClientStateListener() {

                    @Override
                    public void onBillingSetupFinished(
                            @NonNull BillingResult billingResult
                    ) {
                        if (billingResult.getResponseCode()
                                == BillingClient.BillingResponseCode.OK) {

                            billingReady = true;

                            refreshPurchases();
                            queryProducts();

                            listener.onBillingReady();

                        } else {

                            billingReady = false;

                            listener.onBillingError(
                                    "Google Play Billing setup failed: "
                                            + billingResult.getDebugMessage()
                            );
                        }
                    }

                    @Override
                    public void onBillingServiceDisconnected() {
                        // enableAutoServiceReconnection() handles reconnects.
                        billingReady = false;
                    }
                }
        );
    }

    public void close() {
        billingReady = false;

        try {
            billingClient.endConnection();
        } catch (Throwable ignore) {}
    }

    public boolean isReady() {
        return billingReady && billingClient.isReady();
    }

    // ============================================================
    // LOCAL ENTITLEMENT READERS
    // ============================================================
    public boolean isGelProActive() {
        
        // TEST BUILD: unlock GEL PRO without changing stored purchase state.
        if (BuildConfig.DEBUG) return true;
try {
            return appContext
                    .getSharedPreferences(
                            GEL_PRO_PREFS,
                            Context.MODE_PRIVATE
                    )
                    .getBoolean(
                            GEL_PRO_ACTIVE_KEY,
                            false
                    );
        } catch (Throwable ignore) {
            return false;
        }
    }

    public boolean isCustomReportsActive() {
        
        // TEST BUILD: unlock Professional Personalization entitlement.
        if (BuildConfig.DEBUG) return true;
try {
            return appContext
                    .getSharedPreferences(
                            GEL_CUSTOM_PREFS,
                            Context.MODE_PRIVATE
                    )
                    .getBoolean(
                            GEL_CUSTOM_ACTIVE_KEY,
                            false
                    );
        } catch (Throwable ignore) {
            return false;
        }
    }

    public boolean wasCustomReportsOfferShown() {
        try {
            return appContext
                    .getSharedPreferences(
                            GEL_CUSTOM_PREFS,
                            Context.MODE_PRIVATE
                    )
                    .getBoolean(
                            GEL_CUSTOM_OFFER_SHOWN_KEY,
                            false
                    );
        } catch (Throwable ignore) {
            return false;
        }
    }

    public void markCustomReportsOfferShown() {
        try {
            appContext
                    .getSharedPreferences(
                            GEL_CUSTOM_PREFS,
                            Context.MODE_PRIVATE
                    )
                    .edit()
                    .putBoolean(
                            GEL_CUSTOM_OFFER_SHOWN_KEY,
                            true
                    )
                    .apply();
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // LOCAL ENTITLEMENT WRITERS
    // ============================================================
    private void setGelProActive(boolean active) {
        try {
            appContext
                    .getSharedPreferences(
                            GEL_PRO_PREFS,
                            Context.MODE_PRIVATE
                    )
                    .edit()
                    .putBoolean(
                            GEL_PRO_ACTIVE_KEY,
                            active
                    )
                    .apply();
        } catch (Throwable ignore) {}
    }

    private void setCustomReportsActive(boolean active) {
        try {
            appContext
                    .getSharedPreferences(
                            GEL_CUSTOM_PREFS,
                            Context.MODE_PRIVATE
                    )
                    .edit()
                    .putBoolean(
                            GEL_CUSTOM_ACTIVE_KEY,
                            active
                    )
                    .apply();
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // QUERY GOOGLE PLAY PRODUCTS
    // ============================================================
    public void queryProducts() {
        queryGelProProduct();
        queryCustomReportsProduct();
    }

    private void queryGelProProduct() {

        if (!billingClient.isReady()) {
            return;
        }

        QueryProductDetailsParams.Product product =
                QueryProductDetailsParams.Product
                        .newBuilder()
                        .setProductId(PRODUCT_GEL_PRO_MONTHLY)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build();

        QueryProductDetailsParams params =
                QueryProductDetailsParams
                        .newBuilder()
                        .setProductList(
                                Collections.singletonList(product)
                        )
                        .build();

        billingClient.queryProductDetailsAsync(
                params,
                (
                        @NonNull BillingResult billingResult,
                        @NonNull QueryProductDetailsResult result
                ) -> {

                    if (billingResult.getResponseCode()
                            != BillingClient.BillingResponseCode.OK) {

                        gelProDetails = null;

                        listener.onBillingError(
                                "GEL PRO product query failed: "
                                        + billingResult.getDebugMessage()
                        );
                        return;
                    }

                    List<ProductDetails> products =
                            result.getProductDetailsList();

                    gelProDetails =
                            products.isEmpty()
                                    ? null
                                    : products.get(0);
                }
        );
    }

    private void queryCustomReportsProduct() {

        if (!billingClient.isReady()) {
            return;
        }

        QueryProductDetailsParams.Product product =
                QueryProductDetailsParams.Product
                        .newBuilder()
                        .setProductId(PRODUCT_CUSTOM_REPORTS)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build();

        QueryProductDetailsParams params =
                QueryProductDetailsParams
                        .newBuilder()
                        .setProductList(
                                Collections.singletonList(product)
                        )
                        .build();

        billingClient.queryProductDetailsAsync(
                params,
                (
                        @NonNull BillingResult billingResult,
                        @NonNull QueryProductDetailsResult result
                ) -> {

                    if (billingResult.getResponseCode()
                            != BillingClient.BillingResponseCode.OK) {

                        customReportsDetails = null;

                        listener.onBillingError(
                                "Custom Reports product query failed: "
                                        + billingResult.getDebugMessage()
                        );
                        return;
                    }

                    List<ProductDetails> products =
                            result.getProductDetailsList();

                    customReportsDetails =
                            products.isEmpty()
                                    ? null
                                    : products.get(0);
                }
        );
    }

    // ============================================================
    // GEL PRO PURCHASE
    // ============================================================
    public void launchGelProPurchase(
            @NonNull Activity activity
    ) {

        if (!billingClient.isReady()) {
            listener.onBillingError(
                    "Google Play Billing is not ready."
            );
            return;
        }

        if (gelProDetails == null) {
            queryGelProProduct();

            listener.onBillingError(
                    "GEL PRO is not available yet. Try again in a moment."
            );
            return;
        }

        List<ProductDetails.SubscriptionOfferDetails> offers =
                gelProDetails.getSubscriptionOfferDetails();

        if (offers == null || offers.isEmpty()) {
            listener.onBillingError(
                    "Google Play returned no eligible GEL PRO subscription plan."
            );
            return;
        }

        ProductDetails.SubscriptionOfferDetails selectedOffer = null;

        // 1) Prefer our exact monthly base plan.
        for (ProductDetails.SubscriptionOfferDetails offer : offers) {
            if (GEL_PRO_BASE_PLAN_ID.equals(offer.getBasePlanId())
                    && offer.getOfferId() == null) {

                selectedOffer = offer;
                break;
            }
        }

        // 2) If Play Console uses a different base-plan id,
        // choose a normal non-promotional base plan.
        if (selectedOffer == null) {
            for (ProductDetails.SubscriptionOfferDetails offer : offers) {
                if (offer.getOfferId() == null) {
                    selectedOffer = offer;
                    break;
                }
            }
        }

        if (selectedOffer == null) {
            listener.onBillingError(
                    "No standard GEL PRO base plan is eligible for this account."
            );
            return;
        }

        BillingFlowParams.ProductDetailsParams productParams =
                BillingFlowParams.ProductDetailsParams
                        .newBuilder()
                        .setProductDetails(gelProDetails)
                        .setOfferToken(
                                selectedOffer.getOfferToken()
                        )
                        .build();

        BillingFlowParams flowParams =
                BillingFlowParams
                        .newBuilder()
                        .setProductDetailsParamsList(
                                Collections.singletonList(
                                        productParams
                                )
                        )
                        .build();

        BillingResult result =
                billingClient.launchBillingFlow(
                        activity,
                        flowParams
                );

        if (result.getResponseCode()
                != BillingClient.BillingResponseCode.OK) {

            listener.onBillingError(
                    "Could not open GEL PRO purchase: "
                            + result.getDebugMessage()
            );
        }
    }

    // ============================================================
    // CUSTOM REPORTS €29.99 — ONE-TIME PURCHASE
    // ============================================================
    public void launchCustomReportsPurchase(
            @NonNull Activity activity
    ) {

        if (!isGelProActive()) {
            listener.onBillingError(
                    "An active GEL PRO subscription is required first."
            );
            return;
        }

        if (!billingClient.isReady()) {
            listener.onBillingError(
                    "Google Play Billing is not ready."
            );
            return;
        }

        if (customReportsDetails == null) {
            queryCustomReportsProduct();

            listener.onBillingError(
                    "Custom Reports are not available yet. Try again in a moment."
            );
            return;
        }

        BillingFlowParams.ProductDetailsParams.Builder productBuilder =
                BillingFlowParams.ProductDetailsParams
                        .newBuilder()
                        .setProductDetails(customReportsDetails);

        // Billing 9 supports multiple purchase options/offers
        // for one-time products.
        List<ProductDetails.OneTimePurchaseOfferDetails> offers =
                customReportsDetails
                        .getOneTimePurchaseOfferDetailsList();

        if (offers != null && !offers.isEmpty()) {

            ProductDetails.OneTimePurchaseOfferDetails offer =
                    offers.get(0);

            String offerToken = offer.getOfferToken();

            if (offerToken != null
                    && !offerToken.trim().isEmpty()) {

                productBuilder.setOfferToken(offerToken);
            }
        }

        BillingFlowParams flowParams =
                BillingFlowParams
                        .newBuilder()
                        .setProductDetailsParamsList(
                                Collections.singletonList(
                                        productBuilder.build()
                                )
                        )
                        .build();

        BillingResult result =
                billingClient.launchBillingFlow(
                        activity,
                        flowParams
                );

        if (result.getResponseCode()
                != BillingClient.BillingResponseCode.OK) {

            listener.onBillingError(
                    "Could not open Custom Reports purchase: "
                            + result.getDebugMessage()
            );
        }
    }

    // ============================================================
    // GOOGLE PLAY PURCHASE CALLBACK
    // ============================================================
    @Override
    public void onPurchasesUpdated(
            @NonNull BillingResult billingResult,
            @Nullable List<Purchase> purchases
    ) {

        int responseCode =
                billingResult.getResponseCode();

        if (responseCode
                == BillingClient.BillingResponseCode.OK) {

            if (purchases == null) {
                return;
            }

            for (Purchase purchase : purchases) {
                processPurchase(
                        purchase,
                        true
                );
            }

            return;
        }

        if (responseCode
                == BillingClient.BillingResponseCode.USER_CANCELED) {

            listener.onPurchaseCancelled();
            return;
        }

        listener.onBillingError(
                "Purchase failed: "
                        + billingResult.getDebugMessage()
        );
    }

    // ============================================================
    // RESTORE / REFRESH CURRENT GOOGLE PLAY PURCHASES
    // ============================================================
    public void refreshPurchases() {

        if (!billingClient.isReady()) {
            return;
        }

        refreshGelProSubscription();
        refreshCustomReportsPurchase();
    }

    private void refreshGelProSubscription() {

        QueryPurchasesParams params =
                QueryPurchasesParams
                        .newBuilder()
                        .setProductType(
                                BillingClient.ProductType.SUBS
                        )
                        .build();

        billingClient.queryPurchasesAsync(
                params,
                (billingResult, purchases) -> {

                    if (billingResult.getResponseCode()
                            != BillingClient.BillingResponseCode.OK) {

                        // Important:
                        // Do NOT revoke entitlement on a transient Play error.
                        return;
                    }

                    boolean activeGelProFound = false;

                    if (purchases != null) {

                        for (Purchase purchase : purchases) {

                            if (purchase
                                    .getProducts()
                                    .contains(PRODUCT_GEL_PRO_MONTHLY)
                                    && purchase.getPurchaseState()
                                    == Purchase.PurchaseState.PURCHASED) {

                                activeGelProFound = true;

                                processPurchase(
                                        purchase,
                                        false
                                );
                            }
                        }
                    }

                    // queryPurchasesAsync(SUBS) returns currently owned/
                    // active subscriptions. If the query succeeds and GEL PRO
                    // is not present, the local subscription entitlement
                    // must not remain unlocked.
                    if (!activeGelProFound) {
                        setGelProActive(false);
                    }
                }
        );
    }

    private void refreshCustomReportsPurchase() {

        QueryPurchasesParams params =
                QueryPurchasesParams
                        .newBuilder()
                        .setProductType(
                                BillingClient.ProductType.INAPP
                        )
                        .build();

        billingClient.queryPurchasesAsync(
                params,
                (billingResult, purchases) -> {

                    if (billingResult.getResponseCode()
                            != BillingClient.BillingResponseCode.OK) {

                        return;
                    }

                    if (purchases == null) {
                        return;
                    }

                    for (Purchase purchase : purchases) {

                        if (purchase
                                .getProducts()
                                .contains(PRODUCT_CUSTOM_REPORTS)
                                && purchase.getPurchaseState()
                                == Purchase.PurchaseState.PURCHASED) {

                            processPurchase(
                                    purchase,
                                    false
                            );
                        }
                    }
                }
        );
    }

    // ============================================================
    // CENTRAL PURCHASE PROCESSOR
    // ============================================================
    private void processPurchase(
            @NonNull Purchase purchase,
            boolean freshPurchaseFlow
    ) {

        List<String> products =
                purchase.getProducts();

        boolean isGelProPurchase =
                products.contains(
                        PRODUCT_GEL_PRO_MONTHLY
                );

        boolean isCustomReportsPurchase =
                products.contains(
                        PRODUCT_CUSTOM_REPORTS
                );

        if (!isGelProPurchase
                && !isCustomReportsPurchase) {
            return;
        }

        // --------------------------------------------------------
        // PENDING
        // Never unlock while payment is pending.
        // --------------------------------------------------------
        if (purchase.getPurchaseState()
                == Purchase.PurchaseState.PENDING) {

            if (isGelProPurchase) {
                listener.onPurchasePending(
                        PRODUCT_GEL_PRO_MONTHLY
                );
            }

            if (isCustomReportsPurchase) {
                listener.onPurchasePending(
                        PRODUCT_CUSTOM_REPORTS
                );
            }

            return;
        }

        // --------------------------------------------------------
        // PURCHASED ONLY
        // --------------------------------------------------------
        if (purchase.getPurchaseState()
                != Purchase.PurchaseState.PURCHASED) {
            return;
        }

        // The BillingClient purchase response says PURCHASED.
        // Keep entitlement state in sync with the existing app gates.
        if (isGelProPurchase) {
            setGelProActive(true);
        }

        if (isCustomReportsPurchase) {
            setCustomReportsActive(true);
        }

        // --------------------------------------------------------
        // ACKNOWLEDGE
        // --------------------------------------------------------
        if (!purchase.isAcknowledged()) {

            AcknowledgePurchaseParams acknowledgeParams =
                    AcknowledgePurchaseParams
                            .newBuilder()
                            .setPurchaseToken(
                                    purchase.getPurchaseToken()
                            )
                            .build();

            billingClient.acknowledgePurchase(
                    acknowledgeParams,
                    billingResult -> {

                        if (billingResult.getResponseCode()
                                != BillingClient.BillingResponseCode.OK) {

                            listener.onBillingError(
                                    "Purchase acknowledgement failed: "
                                            + billingResult.getDebugMessage()
                            );
                            return;
                        }

                        notifyActivated(
                                isGelProPurchase,
                                isCustomReportsPurchase,
                                freshPurchaseFlow
                        );
                    }
            );

        } else {

            notifyActivated(
                    isGelProPurchase,
                    isCustomReportsPurchase,
                    freshPurchaseFlow
            );
        }
    }

    // ============================================================
    // ACTIVITY NOTIFICATION
    // ============================================================
    private void notifyActivated(
            boolean gelPro,
            boolean customReports,
            boolean freshPurchaseFlow
    ) {

        if (gelPro) {

            boolean shouldOfferCustomReports =
                    freshPurchaseFlow
                            && !isCustomReportsActive()
                            && !wasCustomReportsOfferShown();

            listener.onGelProActivated(
                    shouldOfferCustomReports
            );
        }

        if (customReports) {
            listener.onCustomReportsActivated();
        }
    }
}
