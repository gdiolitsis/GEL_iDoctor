package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import java.util.HashMap;
import java.util.Map;

/** Server is authoritative. Local state is only a UID-bound display cache and retry outbox. */
final class GelReportStore {
    interface Callback { void done(boolean ok, String code, String message); }
    private static FirebaseFunctions functions() { return FirebaseFunctions.getInstance("europe-west1"); }
    static String uid() {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        return u != null && !u.isAnonymous() && u.isEmailVerified() ? u.getUid() : "";
    }
    static String accountHash() {
        try {
            byte[] bytes = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(uid().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder();
            for (byte b : bytes) out.append(String.format(java.util.Locale.ROOT,"%02x", b & 255));
            return out.toString();
        } catch (Exception impossible) { throw new IllegalStateException(impossible); }
    }
    static SharedPreferences prefs(Context c) { return c.getSharedPreferences("GEL_REPORT_SERVER_" + uid(), 0); }
    static int credits(Context c) { return uid().isEmpty() ? 0 : prefs(c).getInt("credits", 0); }
    static boolean unlimited(Context c) { return !uid().isEmpty() && prefs(c).getBoolean("unlimited", false); }
    private static void call(Context c, String name, Map<String,Object> args, Callback callback) {
        final String owner = uid();
        if (owner.isEmpty()) { callback.done(false,"UNAUTHENTICATED","Sign in with Google to restore report purchases."); return; }
        functions().getHttpsCallable(name).call(args).addOnCompleteListener(task -> {
            if (!owner.equals(uid())) { callback.done(false,"UNAUTHENTICATED","Account changed. Please retry."); return; }
            if (!task.isSuccessful() || task.getResult() == null || !(task.getResult().getData() instanceof Map)) {
                Exception e = task.getException();
                String code = e instanceof FirebaseFunctionsException
                        ? ((FirebaseFunctionsException)e).getCode().name() : "UNAVAILABLE";
                callback.done(false,code,"Report verification unavailable (" + code + "). Please retry Restore."); return;
            }
            Map<?,?> data = (Map<?,?>) task.getResult().getData();
            if (data.get("credits") instanceof Number) prefs(c).edit()
                    .putInt("credits", Math.max(0,((Number)data.get("credits")).intValue()))
                    .putBoolean("unlimited", Boolean.TRUE.equals(data.get("unlimited"))).apply();
            callback.done(true,"", "");
        });
    }
    static void refresh(Context c, Callback callback) {
        retryCompletions(c);
        call(c,"getReportEntitlements",new HashMap<>(),callback);
    }
    static void verify(Context c, String product, String token, Callback callback) {
        Map<String,Object> args = new HashMap<>(); args.put("productId",product); args.put("purchaseToken",token);
        call(c,"verifyReportPurchase",args,callback);
    }
    static void prepare(Context c, String report, String attempt, boolean unlimited, Callback callback) {
        Map<String,Object> args = args(report,attempt,true); args.put("mode",unlimited ? "unlimited" : "single");
        call(c,"prepareReportExport",args,callback);
    }
    private static Map<String,Object> args(String report, String attempt, boolean success) {
        Map<String,Object> args = new HashMap<>(); args.put("reportId",report); args.put("attemptId",attempt); args.put("success",success); return args;
    }
    static void finish(Context c, String report, String attempt, String owner, boolean success) {
        if (owner.isEmpty() || report.isEmpty() || attempt.isEmpty()) return;
        final SharedPreferences outbox = c.getSharedPreferences("GEL_REPORT_SERVER_" + owner, 0);
        final String key = "finish_" + report + ":" + attempt;
        if (success && !outbox.edit().putBoolean(key,true).commit()) return;
        if (!owner.equals(uid())) return;
        call(c,"finishReportExport",args(report,attempt,success),(ok,code,message) -> {
            if (ok && success) outbox.edit().remove(key).apply();
        });
    }
    static void retryCompletions(Context c) {
        if (uid().isEmpty()) return;
        for (String key : prefs(c).getAll().keySet()) if (key.startsWith("finish_")) {
            String[] parts = key.substring(7).split(":",2);
            if (parts.length == 2) finish(c,parts[0],parts[1],uid(),true);
        }
    }
}
