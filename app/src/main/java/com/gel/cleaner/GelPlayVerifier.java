package com.gel.cleaner;

import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import java.util.Collections;
import java.util.Map;

final class GelPlayVerifier {
    interface Callback { void done(boolean verified, String error); }
    static void verify(Context context, String token, Callback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            // Identity creation occurs only after an explicit Play purchase/restore.
            auth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) verify(context, token, callback);
                else callback.done(false, "Could not connect to purchase verification.");
            });
            return;
        }
        FirebaseFunctions.getInstance("europe-west1").getHttpsCallable("verifyGelProPurchase")
                .call(Collections.singletonMap("purchaseToken", token)).addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null
                            || !(task.getResult().getData() instanceof Map)) {
                        callback.done(false, "Purchase verification unavailable. Please retry Restore."); return;
                    }
                    Map<?, ?> data = (Map<?, ?>) task.getResult().getData();
                    long expiry = data.get("expiresAtMs") instanceof Number
                            ? ((Number) data.get("expiresAtMs")).longValue() : 0;
                    boolean active = Boolean.TRUE.equals(data.get("active"))
                            && expiry > System.currentTimeMillis();
                    context.getSharedPreferences("GEL_PRO_ENTITLEMENT", Context.MODE_PRIVATE)
                            .edit().putBoolean("active", active).putLong("verified_until", active ? expiry : 0).apply();
                    callback.done(active, active ? null : "Google Play reports no active PRO subscription.");
                });
    }
}
