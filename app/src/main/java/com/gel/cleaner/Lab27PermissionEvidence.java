package com.gel.cleaner;

/** Public-PackageManager permission evidence, not a privacy or malware rating. */
public final class Lab27PermissionEvidence {
    private Lab27PermissionEvidence() {}

    // Corresponds to PackageInfo.REQUESTED_PERMISSION_GRANTED (API16+).
    private static final int GRANTED_FLAG = 0x2;

    public enum GrantState { GRANTED, NOT_GRANTED, UNKNOWN }
    public enum Coverage { UNAVAILABLE, VISIBLE_PACKAGES_ONLY }

    public static Coverage coverage(boolean querySucceeded, int visibleCount) {
        return querySucceeded && visibleCount >= 0
                ? Coverage.VISIBLE_PACKAGES_ONLY : Coverage.UNAVAILABLE;
    }

    /** A missing or misaligned flags array must never be read as denied. */
    public static GrantState granted(String[] requested, int[] flags, int index) {
        if (requested == null || flags == null || index < 0
                || index >= requested.length || index >= flags.length
                || requested[index] == null) return GrantState.UNKNOWN;
        return (flags[index] & GRANTED_FLAG) != 0
                ? GrantState.GRANTED : GrantState.NOT_GRANTED;
    }

    /** Named groups only; special access/AppOps is explicitly out of scope. */
    public static String category(String p) {
        if (p == null) return null;
        switch (p) {
            case "android.permission.ACCESS_FINE_LOCATION":
            case "android.permission.ACCESS_COARSE_LOCATION":
            case "android.permission.ACCESS_BACKGROUND_LOCATION":
                return "LOCATION";
            case "android.permission.RECORD_AUDIO":
                return "MICROPHONE";
            case "android.permission.CAMERA":
                return "CAMERA";
            case "android.permission.READ_SMS":
            case "android.permission.SEND_SMS":
            case "android.permission.RECEIVE_SMS":
                return "SMS";
            case "android.permission.READ_CONTACTS":
            case "android.permission.WRITE_CONTACTS":
                return "CONTACTS";
            case "android.permission.READ_CALL_LOG":
            case "android.permission.WRITE_CALL_LOG":
            case "android.permission.CALL_PHONE":
                return "CALLS";
            case "android.permission.POST_NOTIFICATIONS":
                return "NOTIFICATIONS";
            default:
                return null;
        }
    }

    public static String summaryToken(boolean packageQuerySucceeded) {
        return packageQuerySucceeded ? "OBSERVED_LIMITED" : "INCONCLUSIVE";
    }
}
