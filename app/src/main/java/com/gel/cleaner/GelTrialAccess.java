package com.gel.cleaner;

import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import java.util.Collections;
import java.util.Map;

/** Short, monotonic, UID/device-bound lease; every refresh uses the fixed server expiry. */
final class GelTrialAccess {
    interface Callback { void done(boolean active, boolean eligible, String error); }
    private static String owner = "", device = "";
    private static long expiresAt;
    private static GelTrialClock clock = new GelTrialClock(0,0,0);
    static String deviceId(Context c) {
        String id = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
        return id == null ? "" : id;
    }
    private static String uid() {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        return u != null && !u.isAnonymous() && u.isEmailVerified() ? u.getUid() : "";
    }
    static synchronized boolean active(Context c) {
        return !owner.isEmpty() && owner.equals(uid()) && device.equals(deviceId(c))
                && clock.active(SystemClock.elapsedRealtime());
    }
    static synchronized long expiresAt(Context c) { return active(c) ? expiresAt : 0; }
    static synchronized void clear() { owner = ""; clock = new GelTrialClock(0,0,0); expiresAt = 0; }
    static void refresh(Context c, Callback cb) { call(c,false,cb); }
    static void start(Context c, Callback cb) { call(c,true,cb); }
    private static void call(Context c, boolean start, Callback callback) {
        final String user = uid(), id = deviceId(c);
        final long requestedAt = SystemClock.elapsedRealtime();
        if (user.isEmpty() || !id.matches("(?i)[a-f0-9]{16}") || id.matches("0+")) {
            clear(); if (callback != null) callback.done(false,false,"SIGN_IN_REQUIRED"); return;
        }
        FirebaseFunctions.getInstance("europe-west1").getHttpsCallable(start ? "startGelProTrial" : "getGelProTrial")
                .call(Collections.singletonMap("deviceId",id)).addOnCompleteListener(task -> {
                    if (!user.equals(uid()) || !id.equals(deviceId(c))) {
                        if (callback != null) callback.done(false,false,"ACCOUNT_CHANGED"); return;
                    }
                    if (!task.isSuccessful() || task.getResult() == null || !(task.getResult().getData() instanceof Map)) {
                        if (callback != null) callback.done(active(c),false,"CONNECTION"); return;
                    }
                    Map<?,?> data = (Map<?,?>) task.getResult().getData();
                    long serverNow = data.get("serverNowMs") instanceof Number ? ((Number)data.get("serverNowMs")).longValue() : 0;
                    long until = data.get("expiresAtMs") instanceof Number ? ((Number)data.get("expiresAtMs")).longValue() : 0;
                    synchronized (GelTrialAccess.class) {
                        owner = user; device = id; expiresAt = until;
                        clock = new GelTrialClock(requestedAt,serverNow,Boolean.TRUE.equals(data.get("active")) ? until : 0);
                    }
                    if (callback != null) callback.done(active(c),Boolean.TRUE.equals(data.get("eligible")),"");
                });
    }
}
