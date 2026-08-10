// GDiolitsis Engine Lab (GEL) — Author & Developer
// GelBillingManager.java
// Google Play Billing Library 8.x
// Central billing layer for GEL PRO subscription + custom report branding.

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
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.Collections;
import java.util.List;

public final class GelBillingManager implements PurchasesUpdatedListener {

    public static final String PRODUCT_GEL_PRO_MONTHLY = "gel_pro_monthly";
    public static final String PRODUCT_CUSTOM_REPORTS  = "gel_custom_reports";

    private static final String GEL_PRO_PREFS = "GEL_PRO_ENTITLEMENT";
    private static final String GEL_PRO_ACTIVE_KEY = "active";

    private static final String GEL_CUSTOM_PREFS = "GEL_CUSTOM_REPORT_ENTITLEMENT";
    private static final String GEL_CUSTOM_ACTIVE_KEY = "active";
    private static final String GEL_CUSTOM_OFFER_SHOWN_KEY = "custom_offer_shown";

    private final Context appContext;
    private final BillingClient billingClient;
    private final Listener listener;

    private ProductDetails gelProProductDetails;
    private ProductDetails customReportsProductDetails;
    private boolean connected = false;

    public interface Listener {
        void onBillingReady();
        void onGelProActivated(boolean showCustomReportsOffer);
        void onCustomReportsActivated();
        void onPurchasePending(String productId);
        void onPurchaseCancelled();
        void onBillingError(String message);
    }

    public GelBillingManager(@NonNull Context context, @NonNull Listener listener) {
        this.appContext = context.getApplicationContext();
        this.listener = listener;

        PendingPurchasesParams pendingParams =
                PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts()
                        .build();

        billingClient = BillingClient.newBuilder(appContext)
                .setListener(this)
                .enablePendingPurchases(pendingParams)
                .enableAutoServiceReconnection()
                .build();
    }

    public void start() {
        if (billingClient.isReady()) {
            connected = true;
            refreshPurchases();
            queryProducts();
            listener.onBillingReady();
            return;
        }

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    connected = true;
                    refreshPurchases();
                    queryProducts();
                    listener.onBillingReady();
                } else {
                    connected = false;
                    listener.onBillingError("Google Play Billing setup failed: " + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                connected = false;
            }
        });
    }

    public void close() {
        try { billingClient.endConnection(); } catch (Throwable ignore) {}
        connected = false;
    }

    public boolean isReady() {
        return connected && billingClient.isReady();
    }

    public boolean isGelProActive() {
        try {
            return appContext.getSharedPreferences(GEL_PRO_PREFS, Context.MODE_PRIVATE)
                    .getBoolean(GEL_PRO_ACTIVE_KEY, false);
        } catch (Throwable ignore) {
            return false;
        }
    }

    public boolean isCustomReportsActive() {
        try {
            return appContext.getSharedPreferences(GEL_CUSTOM_PREFS, Context.MODE_PRIVATE)
                    .getBoolean(GEL_CUSTOM_ACTIVE_KEY, false);
        } catch (Throwable ignore) {
            return false;
        }
    }

    public boolean wasCustomReportsOfferShown() {
        try {
            return appContext.getSharedPreferences(GEL_CUSTOM_PREFS, Context.MODE_PRIVATE)
                    .getBoolean(GEL_CUSTOM_OFFER_SHOWN_KEY, false);
        } catch (Throwable ignore) {
            return false;
        }
    }

    public void markCustomReportsOfferShown() {
        try {
            appContext.getSharedPreferences(GEL_CUSTOM_PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(GEL_CUSTOM_OFFER_SHOWN_KEY, true)
                    .apply();
        } catch (Throwable ignore) {}
    }

    private void setGelProActive(boolean active) {
        appContext.getSharedPreferences(GEL_PRO_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(GEL_PRO_ACTIVE_KEY, active)
                .apply();
    }

    private void setCustomReportsActive(boolean active) {
        appContext.getSharedPreferences(GEL_CUSTOM_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(GEL_CUSTOM_ACTIVE_KEY, active)
                .apply();
    }

    private void queryProducts() {
        queryGelProProduct();
        queryCustomReportsProduct();
    }

    private void queryGelProProduct() {
        QueryProductDetailsParams.Product product = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_GEL_PRO_MONTHLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build();

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(Collections.singletonList(product))
                .build();

        billingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult,
                                                 @NonNull QueryProductDetailsResult result) {
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    listener.onBillingError("GEL PRO product query failed: " + billingResult.getDebugMessage());
                    return;
                }
                List<ProductDetails> list = result.getProductDetailsList();
                gelProProductDetails = (list != null && !list.isEmpty()) ? list.get(0) : null;
            }
        });
    }

    private void queryCustomReportsProduct() {
        QueryProductDetailsParams.Product product = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_CUSTOM_REPORTS)
                .setProductType(BillingClient.ProductType.INAPP)
                .build();

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(Collections.singletonList(product))
                .build();

        billingClient.queryProductDetailsAsync(params, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult,
                                                 @NonNull QueryProductDetailsResult result) {
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    listener.onBillingError("Custom Reports product query failed: " + billingResult.getDebugMessage());
                    return;
                }
                List<ProductDetails> list = result.getProductDetailsList();
                customReportsProductDetails = (list != null && !list.isEmpty()) ? list.get(0) : null;
            }
        });
    }

    public void launchGelProPurchase(@NonNull Activity activity) {
        if (!billingClient.isReady()) {
            listener.onBillingError("Google Play Billing is not ready.");
            return;
        }
        if (gelProProductDetails == null) {
            queryGelProProduct();
            listener.onBillingError("GEL PRO product is not ready yet. Try again in a moment.");
            return;
        }

        List<ProductDetails.SubscriptionOfferDetails> offers = gelProProductDetails.getSubscriptionOfferDetails();
        if (offers == null || offers.isEmpty()) {
            listener.onBillingError("No eligible GEL PRO subscription offer was returned by Google Play.");
            return;
        }

        String offerToken = offers.get(0).getOfferToken();

        BillingFlowParams.ProductDetailsParams productParams =
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(gelProProductDetails)
                        .setOfferToken(offerToken)
                        .build();

        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(Collections.singletonList(productParams))
                .build();

        BillingResult launchResult = billingClient.launchBillingFlow(activity, flowParams);
        if (launchResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            listener.onBillingError("Could not open GEL PRO purchase: " + launchResult.getDebugMessage());
        }
    }

    public void launchCustomReportsPurchase(@NonNull Activity activity) {
        if (!isGelProActive()) {
            listener.onBillingError("An active GEL PRO subscription is required first.");
            return;
        }
        if (!billingClient.isReady()) {
            listener.onBillingError("Google Play Billing is not ready.");
            return;
        }
        if (customReportsProductDetails == null) {
            queryCustomReportsProduct();
            listener.onBillingError("Custom Reports product is not ready yet. Try again in a moment.");
            return;
        }

        BillingFlowParams.ProductDetailsParams.Builder productBuilder =
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(customReportsProductDetails);

        List<ProductDetails.OneTimePurchaseOfferDetails> oneTimeOffers =
                customReportsProductDetails.getOneTimePurchaseOfferDetailsList();

        if (oneTimeOffers != null && !oneTimeOffers.isEmpty()) {
            String offerToken = oneTimeOffers.get(0).getOfferToken();
            if (offerToken != null && !offerToken.trim().isEmpty()) {
                productBuilder.setOfferToken(offerToken);
            }
        }

        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(Collections.singletonList(productBuilder.build()))
                .build();

        BillingResult launchResult = billingClient.launchBillingFlow(activity, flowParams);
        if (launchResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            listener.onBillingError("Could not open Custom Reports purchase: " + launchResult.getDebugMessage());
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult,
                                   @Nullable List<Purchase> purchases) {
        int code = billingResult.getResponseCode();

        if (code == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                processPurchase(purchase, true);
            }
        } else if (code == BillingClient.BillingResponseCode.USER_CANCELED) {
            listener.onPurchaseCancelled();
        } else {
            listener.onBillingError("Purchase failed: " + billingResult.getDebugMessage());
        }
    }

    public void refreshPurchases() {
        refreshSubscription();
        refreshOneTimePurchase();
    }

    private void refreshSubscription() {
        QueryPurchasesParams params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build();

        billingClient.queryPurchasesAsync(params, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) return;

            boolean proFound = false;
            if (purchases != null) {
                for (Purchase purchase : purchases) {
                    if (purchase.getProducts().contains(PRODUCT_GEL_PRO_MONTHLY)
                            && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        proFound = true;
                        processPurchase(purchase, false);
                    }
                }
            }

            if (!proFound) setGelProActive(false);
        });
    }

    private void refreshOneTimePurchase() {
        QueryPurchasesParams params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build();

        billingClient.queryPurchasesAsync(params, (billingResult, purchases) -> {
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) return;
            if (purchases == null) return;

            for (Purchase purchase : purchases) {
                if (purchase.getProducts().contains(PRODUCT_CUSTOM_REPORTS)
                        && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    processPurchase(purchase, false);
                }
            }
        });
    }

    private void processPurchase(@NonNull Purchase purchase, boolean fromFreshPurchaseFlow) {
        List<String> products = purchase.getProducts();

        boolean containsPro = products.contains(PRODUCT_GEL_PRO_MONTHLY);
        boolean containsCustom = products.contains(PRODUCT_CUSTOM_REPORTS);

        if (!containsPro && !containsCustom) return;

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            if (containsPro) listener.onPurchasePending(PRODUCT_GEL_PRO_MONTHLY);
            if (containsCustom) listener.onPurchasePending(PRODUCT_CUSTOM_REPORTS);
            return;
        }

        if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) return;

        if (containsPro) setGelProActive(true);
        if (containsCustom) setCustomReportsActive(true);

        if (!purchase.isAcknowledged()) {
            AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();

            billingClient.acknowledgePurchase(params, billingResult -> {
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    listener.onBillingError("Purchase acknowledgement failed: " + billingResult.getDebugMessage());
                    return;
                }
                notifyEntitlementActivated(containsPro, containsCustom, fromFreshPurchaseFlow);
            });
        } else {
            notifyEntitlementActivated(containsPro, containsCustom, fromFreshPurchaseFlow);
        }
    }

    private void notifyEntitlementActivated(boolean containsPro,
                                            boolean containsCustom,
                                            boolean fromFreshPurchaseFlow) {
        if (containsPro) {
            boolean shouldShowCustomOffer = fromFreshPurchaseFlow
                    && !isCustomReportsActive()
                    && !wasCustomReportsOfferShown();

            listener.onGelProActivated(shouldShowCustomOffer);
        }

        if (containsCustom) {
            listener.onCustomReportsActivated();
        }
    }
}
