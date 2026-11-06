// GDiolitsis Engine Lab (GEL) — Author & Developer
// GelBillingManager.java
// Google Play Billing Library 9.1.0
//
// Central billing layer for:
// 1) GEL PRO monthly subscription
// 2) GEL Custom Reports unlimited one-time purchase (€29.99)
// 3) GEL Single Personalised Report consumable purchase (€4.99)
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
import com.android.billingclient.api.ConsumeParams;
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
    //
    // Consumable one-time product:
    // Product ID: gel_single_report
    // ============================================================
    public static final String PRODUCT_GEL_PRO_MONTHLY = "gel_pro_monthly";
    public static final String GEL_PRO_BASE_PLAN_ID = "monthly";
    public static final String GEL_PRO_PREPAID_PLAN_ID = "prepaid-monthly";
    public static final String PRODUCT_CUSTOM_REPORTS = "gel_custom_reports";
    public static final String PRODUCT_SINGLE_REPORT = "gel_single_report";

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

    // Report credits and permanent ownership are authoritative on the server.

    private final Context appContext;
    private final Listener listener;
    private final BillingClient billingClient;

    @Nullable
    private ProductDetails gelProDetails;

    @Nullable
    private ProductDetails customReportsDetails;

    @Nullable
    private ProductDetails singleReportDetails;

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

        // Default method keeps existing Activities source-compatible in Step 1.
        // Called after a €4.99 purchase has safely created one local report credit.
        default void onSingleReportCreditAdded(int availableCredits) {}

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
                        .enablePrepaidPlans()
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
    /**
     * Effective feature entitlement:
     * paid GEL PRO OR valid Tester PRO.
     */
    public boolean isGelProActive() {
        return GelProEntitlement.isActive(appContext);
    }

    /**
     * Actual paid Google Play GEL PRO subscription only.
     * Use for billing prerequisites, never for feature access.
     */
    public boolean isPaidGelProActive() {
        return GelProEntitlement.isPaidProActive(appContext);
    }

    public boolean isCustomReportsActive() { return GelReportStore.unlimited(appContext); }
    public int getSingleReportCredits() { return GelReportStore.credits(appContext); }
    public boolean hasSingleReportCredit() { return getSingleReportCredits() > 0; }

    private boolean requireReportAccount(Activity activity) {
        if (!GelReportStore.uid().isEmpty()) return true;
        listener.onBillingError("Sign in with Google first. Report purchases are saved to your account.");
        activity.startActivity(new android.content.Intent(activity, GelProAccountActivity.class));
        return false;
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

    // ============================================================
    // QUERY GOOGLE PLAY PRODUCTS
    // ============================================================
    public void queryProducts() {
        queryGelProProduct();
        queryCustomReportsProduct();
        querySingleReportProduct();
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

    private void querySingleReportProduct() {

        if (!billingClient.isReady()) return;

        QueryProductDetailsParams.Product product =
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_SINGLE_REPORT)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build();

        QueryProductDetailsParams params =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(Collections.singletonList(product))
                        .build();

        billingClient.queryProductDetailsAsync(
                params,
                (@NonNull BillingResult billingResult,
                 @NonNull QueryProductDetailsResult result) -> {
                    if (billingResult.getResponseCode()
                            != BillingClient.BillingResponseCode.OK) {
                        singleReportDetails = null;
                        listener.onBillingError(
                                "Single Report product query failed: "
                                        + billingResult.getDebugMessage());
                        return;
                    }
                    List<ProductDetails> products = result.getProductDetailsList();
                    singleReportDetails = products.isEmpty() ? null : products.get(0);
                });
    }

    // ============================================================
    // GEL PRO PURCHASE
    // ============================================================
    public void launchGelProPurchase(@NonNull Activity activity) {
        GelProPurchaseDialog.show(activity, this);
    }

    public void launchGelProPlan(@NonNull Activity activity, boolean prepaid) {
        final String basePlan = prepaid ? GEL_PRO_PREPAID_PLAN_ID : GEL_PRO_BASE_PLAN_ID;
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
            if (basePlan.equals(offer.getBasePlanId())
                    && offer.getOfferId() == null) {

                selectedOffer = offer;
                break;
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

        if (!requireReportAccount(activity)) return;

        if (!isGelProActive()) {
            listener.onBillingError(
                    "An active paid GEL PRO subscription is required first."
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
                        .setObfuscatedAccountId(GelReportStore.accountHash())
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
    // SINGLE PERSONALISED REPORT €4.99 — CONSUMABLE ONE-TIME PURCHASE
    // Active GEL PRO access is required.
    // ============================================================
    public void launchSingleReportPurchase(@NonNull Activity activity) {

        if (!requireReportAccount(activity)) return;

        if (!isGelProActive()) {
            listener.onBillingError("Active GEL PRO access is required for personalised reports.");
            return;
        }

        if (!billingClient.isReady()) {
            listener.onBillingError("Google Play Billing is not ready.");
            return;
        }

        if (singleReportDetails == null) {
            querySingleReportProduct();
            listener.onBillingError(
                    "Single Personalised Report is not available yet. Try again in a moment.");
            return;
        }

        BillingFlowParams.ProductDetailsParams.Builder productBuilder =
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(singleReportDetails);

        List<ProductDetails.OneTimePurchaseOfferDetails> offers =
                singleReportDetails.getOneTimePurchaseOfferDetailsList();

        if (offers != null && !offers.isEmpty()) {
            String offerToken = offers.get(0).getOfferToken();
            if (offerToken != null && !offerToken.trim().isEmpty()) {
                productBuilder.setOfferToken(offerToken);
            }
        }

        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setObfuscatedAccountId(GelReportStore.accountHash())
                .setProductDetailsParamsList(
                        Collections.singletonList(productBuilder.build()))
                .build();

        BillingResult result = billingClient.launchBillingFlow(activity, flowParams);
        if (result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            listener.onBillingError(
                    "Could not open Single Report purchase: "
                            + result.getDebugMessage());
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

        if (!GelReportStore.uid().isEmpty()) GelReportStore.refresh(appContext, (ok,code,message) -> {
            if (!ok) listener.onBillingError(message);
        });
        refreshGelProSubscription();
        refreshCustomReportsPurchase();
    }

    public interface RestoreCallback { void done(boolean ok, String message); }

    /** Explicit user restore waits for server verification, including empty/error results. */
    public void restorePurchases(RestoreCallback callback) {
        final boolean gr = AppLang.isGreek(appContext);
        if (!billingClient.isReady()) {
            callback.done(false, gr ? "Το Google Play δεν είναι έτοιμο. Δοκιμάστε ξανά σε λίγο."
                    : "Google Play Billing is not ready. Please retry shortly.");
            start();
            return;
        }
        java.util.List<String> errors = java.util.Collections.synchronizedList(new java.util.ArrayList<>());
        java.util.concurrent.atomic.AtomicInteger queries = new java.util.concurrent.atomic.AtomicInteger(3);
        java.util.concurrent.atomic.AtomicInteger restored = new java.util.concurrent.atomic.AtomicInteger();
        Runnable queryDone = () -> {
            if (queries.decrementAndGet() != 0) return;
            if (!errors.isEmpty()) { callback.done(false, errors.get(0)); return; }
            callback.done(true, restored.get() > 0
                    ? (gr ? "Η επαναφορά ολοκληρώθηκε. Οι διαθέσιμες αγορές επαληθεύτηκαν."
                          : "Restore complete. Available purchases verified.")
                    : (gr ? "Ο έλεγχος ολοκληρώθηκε. Δεν βρέθηκαν ενεργές αγορές Google Play."
                          : "Restore complete. No active Google Play purchases found."));
        };
        if (GelReportStore.uid().isEmpty()) queryDone.run();
        else GelReportStore.refresh(appContext, (ok, code, message) -> {
            if (!ok) errors.add(message);
            else if (GelReportStore.unlimited(appContext) || GelReportStore.credits(appContext) > 0) restored.incrementAndGet();
            queryDone.run();
        });
        for (String type : new String[]{BillingClient.ProductType.SUBS, BillingClient.ProductType.INAPP}) {
            billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(type).build(), (result, purchases) -> {
                if (result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    errors.add("Google Play restore failed (code " + result.getResponseCode() + ").");
                    queryDone.run(); return;
                }
                java.util.List<Purchase> owned = new java.util.ArrayList<>();
                if (purchases != null) for (Purchase p : purchases) {
                    if (p.getPurchaseState() == Purchase.PurchaseState.PURCHASED
                            && (p.getProducts().contains(PRODUCT_GEL_PRO_MONTHLY)
                            || p.getProducts().contains(PRODUCT_SINGLE_REPORT)
                            || p.getProducts().contains(PRODUCT_CUSTOM_REPORTS))) owned.add(p);
                }
                if (owned.isEmpty()) {
                    if (BillingClient.ProductType.SUBS.equals(type)) setGelProActive(false);
                    queryDone.run(); return;
                }
                java.util.concurrent.atomic.AtomicInteger pending = new java.util.concurrent.atomic.AtomicInteger(owned.size());
                Runnable purchaseDone = () -> { if (pending.decrementAndGet() == 0) queryDone.run(); };
                for (Purchase p : owned) {
                    if (p.getProducts().contains(PRODUCT_GEL_PRO_MONTHLY)) {
                        GelPlayVerifier.verify(appContext, p.getPurchaseToken(), (ok, error) -> {
                            if (ok) { restored.incrementAndGet(); processVerifiedPurchase(p, false); }
                            else errors.add(error);
                            purchaseDone.run();
                        });
                    } else if (GelReportStore.uid().isEmpty()) {
                        errors.add(gr ? "Συνδεθείτε με Google για επαναφορά εξατομικευμένων αναφορών."
                                : "Sign in with Google to restore personalised reports.");
                        purchaseDone.run();
                    } else {
                        String sku = p.getProducts().contains(PRODUCT_SINGLE_REPORT) ? PRODUCT_SINGLE_REPORT : PRODUCT_CUSTOM_REPORTS;
                        GelReportStore.verify(appContext, sku, p.getPurchaseToken(), (ok, code, message) -> {
                            if (ok) restored.incrementAndGet(); else errors.add(message);
                            purchaseDone.run();
                        });
                    }
                }
            });
        }
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

                        if ((purchase.getProducts().contains(PRODUCT_CUSTOM_REPORTS)
                                || purchase.getProducts().contains(PRODUCT_SINGLE_REPORT))
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
    private void processPurchase(Purchase purchase, boolean freshPurchaseFlow) {
        boolean single = purchase.getProducts().contains(PRODUCT_SINGLE_REPORT);
        boolean custom = purchase.getProducts().contains(PRODUCT_CUSTOM_REPORTS);
        if ((single || custom) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            String sku = single ? PRODUCT_SINGLE_REPORT : PRODUCT_CUSTOM_REPORTS;
            GelReportStore.verify(appContext, sku, purchase.getPurchaseToken(), (ok,code,message) -> {
                if (!ok) { listener.onBillingError(message); return; }
                if (single) listener.onSingleReportCreditAdded(getSingleReportCredits());
                else listener.onCustomReportsActivated();
            });
            return;
        }
        if (purchase.getProducts().contains(PRODUCT_GEL_PRO_MONTHLY)
                && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            GelPlayVerifier.verify(appContext, purchase.getPurchaseToken(), (verified, error) -> {
                if (verified) processVerifiedPurchase(purchase, freshPurchaseFlow);
                else listener.onBillingError(error);
            });
        } else processVerifiedPurchase(purchase, freshPurchaseFlow);
    }

    private void processVerifiedPurchase(
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

        boolean isSingleReportPurchase =
                products.contains(
                        PRODUCT_SINGLE_REPORT
                );

        if (!isGelProPurchase
                && !isCustomReportsPurchase
                && !isSingleReportPurchase) {
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

            if (isSingleReportPurchase) {
                listener.onPurchasePending(
                        PRODUCT_SINGLE_REPORT
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

        // One-time purchases never reach the client-side entitlement/acknowledgement path.
        if (!isGelProPurchase) return;

        // The BillingClient purchase response says PURCHASED.
        // Keep entitlement state in sync with the existing app gates.
        if (isGelProPurchase) {
            setGelProActive(true);
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
