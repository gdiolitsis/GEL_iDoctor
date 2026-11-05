package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import java.util.Map;

/** Server-controlled gift access. Play purchases and tester codes use separate stores. */
public final class GelAccountPro {
    private static final String PREFS = "GEL_GOOGLE_GIFT_PRO";
    private GelAccountPro() {}
    public interface Callback { void done(boolean active, String message); }
    public static boolean active(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.isAnonymous() || !user.isEmailVerified()) return false;
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return user.getUid().equals(p.getString("uid", ""))
                && p.getLong("expires", 0) > System.currentTimeMillis();
    }
    public static void clear(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply();
    }
    public static void refresh(Context context, Callback callback) {
        Context app = context.getApplicationContext();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.isAnonymous()) { clear(app); callback.done(false, ""); return; }
        final String uid = user.getUid();
        FirebaseFunctions.getInstance("europe-west1").getHttpsCallable("getGiftProEntitlement")
                .call().addOnCompleteListener(task -> {
                    FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
                    if (current == null || !uid.equals(current.getUid())) { callback.done(false, ""); return; }
                    if (!task.isSuccessful() || task.getResult() == null
                            || !(task.getResult().getData() instanceof Map)) {
                        callback.done(active(app), "Connection unavailable. Please try again."); return;
                    }
                    Map<?, ?> data = (Map<?, ?>) task.getResult().getData();
                    long expiry = data.get("expiresAtMs") instanceof Number
                            ? ((Number) data.get("expiresAtMs")).longValue() : 0;
                    if (Boolean.TRUE.equals(data.get("active")) && expiry > System.currentTimeMillis()) {
                        app.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                                .putString("uid", uid).putLong("expires", expiry).apply();
                    } else clear(app);
                    callback.done(active(app), "");
                });
    }
}
