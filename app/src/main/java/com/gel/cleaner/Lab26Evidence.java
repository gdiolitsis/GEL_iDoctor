package com.gel.cleaner;

/** Pure, policy-constrained evidence decisions for Lab 26. No Android dependency. */
public final class Lab26Evidence {
    private Lab26Evidence() {}

    public enum PackageCoverage { UNAVAILABLE, VISIBLE_ONLY }

    /** Even an empty successful query never proves the device has no other apps. */
    public static PackageCoverage packageCoverage(boolean querySucceeded, int visibleCount) {
        return !querySucceeded || visibleCount < 0
                ? PackageCoverage.UNAVAILABLE : PackageCoverage.VISIBLE_ONLY;
    }

    /** Android N+ UID traffic APIs expose only the calling UID. -1 means unavailable. */
    public static long ownUidTrafficBytes(int queriedUid, int callingUid, long rx, long tx) {
        if (queriedUid != callingUid || rx < 0 || tx < 0 || Long.MAX_VALUE - rx < tx) {
            return -1L;
        }
        return rx + tx;
    }

    /** Missing usage permission/query/package record is UNKNOWN, never zero use. */
    public static long foregroundMs(boolean usageAccess, boolean querySucceeded, long reportedMs) {
        if (!usageAccess || !querySucceeded || reportedMs <= 0) return -1L;
        return reportedMs;
    }

    /** Limited inventory is descriptive only, never device/battery health. */
    public static String summaryToken(boolean packageQuerySucceeded) {
        return packageQuerySucceeded ? "OBSERVED_LIMITED" : "INCONCLUSIVE";
    }
}
