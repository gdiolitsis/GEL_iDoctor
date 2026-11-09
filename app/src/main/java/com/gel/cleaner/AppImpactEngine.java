// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppImpactEngine.java — FINAL (Root-Aware • su-based • Best-Effort • Honest)
// ⚠️ Reminder: Always return the final code ready for copy-paste (no extra explanations / no questions).

package com.gel.cleaner;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class AppImpactEngine {

    private AppImpactEngine() {}

    // ============================================================
    // RESULT MODELS
    // ============================================================

    public static final class AppScore {
        public String label;
        public String pkg;
        public int uid;
        public boolean isSystem;

        // capability
        public int capabilityScore;
        public int dangerPermCount;

        public boolean hasBoot;
        public boolean hasLocation;
        public boolean hasMic;
        public boolean hasCamera;
        public boolean hasOverlay;
        public boolean hasStorage;
        public boolean hasVpnBind;
        public boolean hasPostNotif;

        public boolean bgCapable;
        public String tags; // short tag list

        // usage heuristics
        public long fgMs24h = -1L; // daily buckets may exceed the exact requested 24h
        public long dataBytesSinceBoot = -1L; // -1 unavailable; caller UID only on Android 7+
        public long estImpactScore;     // combined ranking score (NOT mAh)

        // ROOT (dumpsys best-effort via su)
        public long dumpRxBytes;        // netstats-derived (best-effort)
        public long dumpTxBytes;        // netstats-derived (best-effort)
        public long dumpEstMah;         // batterystats-derived (best-effort, may be 0)

        // helpers
        public String safeLabel() {
            return (label != null && label.trim().length() > 0) ? label : pkg;
        }
    }

    public static final class OrphanResult {
        public int orphanDirs;
        public long orphanBytes;
        public boolean attempted;
    }

    public static final class ImpactResult {
        public int totalPkgs;
        public int userApps;
        public int systemApps;

        public int bgCapable;
        public int permHeavy;

        public int bootAware;
        public int overlayLike;
        public int vpnLike;
        public int locationLike;
        public int micLike;
        public int cameraLike;
        public int storageLike;
        public int notifLike;

        public int cleanersLike;
        public int launchersLike;
        public int antivirusLike;
        public int keyboardsLike;

        public boolean usageAccessOk; // special access status, not proof of returned usage data
        public boolean packageQuerySucceeded;
        public boolean usageQuerySucceeded;
        public boolean usageRecordsObserved;
        public boolean rooted;        // device rooted flag input

        // ROOT status
        public boolean rootDumpsysOk;

        public ArrayList<AppScore> topCapabilityHeavy = new ArrayList<>();
        public ArrayList<AppScore> topDataConsumers   = new ArrayList<>();
        public ArrayList<AppScore> topBatteryExposure = new ArrayList<>();

        // ROOT top lists
        public ArrayList<AppScore> topBatteryByDumpsys = new ArrayList<>();
        public ArrayList<AppScore> topNetByDumpsys     = new ArrayList<>();

        public OrphanResult orphan = new OrphanResult();

        // verdict points
        public int riskPoints;
        public boolean appsImpactHigh;
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    public static ImpactResult analyze(Context c, boolean deviceRooted) {

        ImpactResult out = new ImpactResult();
        // Lab 26 never probes for, requests, or acts on elevated privileges.
        out.rooted = false;

        if (c == null) return out;

        final PackageManager pm = c.getPackageManager();

        // -----------------------------
        // INSTALLED APPS (SAFE)
        // -----------------------------
        List<ApplicationInfo> apps;
        try {
            apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        } catch (Throwable t) {
            return out;
        }

        if (apps == null) return out;

        out.packageQuerySucceeded = true;
        // This count is of packages VISIBLE to this app, not a complete device inventory.
        out.totalPkgs = apps.size();

        // -----------------------------
        // USAGE ACCESS (for foreground time)
        // -----------------------------
        out.usageAccessOk = hasUsageAccess(c);

        long end = System.currentTimeMillis();
        long start = end - 24L * 60L * 60L * 1000L;

        Map<String, Long> fgMsMap24h = new HashMap<>();
        if (out.usageAccessOk) {
            try {
                UsageStatsManager usm =
                        (UsageStatsManager) c.getSystemService(Context.USAGE_STATS_SERVICE);
                if (usm != null) {
                    List<UsageStats> stats =
                            usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
                    if (stats != null) {
                        out.usageQuerySucceeded = true;
                        out.usageRecordsObserved = !stats.isEmpty();
                        for (UsageStats u : stats) {
                            if (u == null) continue;
                            String pkg = u.getPackageName();
                            if (pkg == null) continue;

                            long fg = 0L;
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    fg = u.getTotalTimeVisible();
                                    if (fg <= 0) fg = u.getTotalTimeInForeground();
                                } else {
                                    fg = u.getTotalTimeInForeground();
                                }
                            } catch (Throwable ignore) {}

                            if (fg > 0) {
                                Long prev = fgMsMap24h.get(pkg);
                                fgMsMap24h.put(pkg, (prev == null ? fg : (prev + fg)));
                            }
                        }
                    }
                }
            } catch (Throwable ignore) {}
        }

        // -----------------------------
        // SCAN LOOP
        // -----------------------------
        ArrayList<AppScore> allUser = new ArrayList<>();

        for (ApplicationInfo ai : apps) {

            if (ai == null) continue;

            final String pkg = (ai.packageName != null ? ai.packageName : "unknown");
            // Package is visible only within the current Android visibility scope.

            final boolean isSystem =
                    ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ||
                    ((ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);

            if (isSystem) out.systemApps++;
            else out.userApps++;

            // label
            String labelStr;
            try {
                CharSequence cs = pm.getApplicationLabel(ai);
                labelStr = (cs != null ? cs.toString() : pkg);
            } catch (Throwable ignore) {
                labelStr = pkg;
            }

            AppScore s = new AppScore();
            s.pkg = pkg;
            s.label = labelStr;
            s.uid = ai.uid;
            s.isSystem = isSystem;

            // -----------------------------
            // PERMISSIONS (requested) — capability intelligence
            // -----------------------------
            String[] reqPerms = null;
            try {
                PackageInfo pi;
                if (Build.VERSION.SDK_INT >= 33) {
                    pi = pm.getPackageInfo(
                            pkg,
                            PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS)
                    );
                } else {
                    //noinspection deprecation
                    pi = pm.getPackageInfo(pkg, PackageManager.GET_PERMISSIONS);
                }
                if (pi != null) reqPerms = pi.requestedPermissions;
            } catch (Throwable ignore) {}

            int dangerCount = 0;

            boolean hasBoot = false;
            boolean hasLocation = false;
            boolean hasMic = false;
            boolean hasCamera = false;
            boolean hasOverlay = false;
            boolean hasStorage = false;
            boolean hasVpnBind = false;
            boolean hasPostNotif = false;

            if (reqPerms != null) {
                for (String p : reqPerms) {
                    if (p == null) continue;

                    if ("android.permission.RECEIVE_BOOT_COMPLETED".equals(p)) hasBoot = true;

                    if ("android.permission.ACCESS_FINE_LOCATION".equals(p) ||
                        "android.permission.ACCESS_COARSE_LOCATION".equals(p)) hasLocation = true;

                    if ("android.permission.RECORD_AUDIO".equals(p)) hasMic = true;
                    if ("android.permission.CAMERA".equals(p)) hasCamera = true;

                    if ("android.permission.SYSTEM_ALERT_WINDOW".equals(p)) hasOverlay = true;

                    if ("android.permission.READ_EXTERNAL_STORAGE".equals(p) ||
                        "android.permission.WRITE_EXTERNAL_STORAGE".equals(p) ||
                        "android.permission.READ_MEDIA_IMAGES".equals(p) ||
                        "android.permission.READ_MEDIA_VIDEO".equals(p) ||
                        "android.permission.READ_MEDIA_AUDIO".equals(p)) hasStorage = true;

                    if ("android.permission.BIND_VPN_SERVICE".equals(p)) hasVpnBind = true;

                    if ("android.permission.POST_NOTIFICATIONS".equals(p)) hasPostNotif = true;

                    // "danger-ish" set (best-effort / honest)
                    if ("android.permission.READ_CONTACTS".equals(p) ||
                        "android.permission.WRITE_CONTACTS".equals(p) ||
                        "android.permission.READ_CALL_LOG".equals(p) ||
                        "android.permission.WRITE_CALL_LOG".equals(p) ||
                        "android.permission.READ_SMS".equals(p) ||
                        "android.permission.SEND_SMS".equals(p) ||
                        "android.permission.RECEIVE_SMS".equals(p) ||
                        "android.permission.READ_PHONE_STATE".equals(p) ||
                        "android.permission.CALL_PHONE".equals(p) ||
                        "android.permission.ACCESS_FINE_LOCATION".equals(p) ||
                        "android.permission.RECORD_AUDIO".equals(p) ||
                        "android.permission.CAMERA".equals(p) ||
                        "android.permission.BODY_SENSORS".equals(p) ||
                        "android.permission.USE_SIP".equals(p) ||
                        "android.permission.WRITE_SETTINGS".equals(p) ||
                        "android.permission.SYSTEM_ALERT_WINDOW".equals(p)) {
                        dangerCount++;
                    }
                }
            }

            s.dangerPermCount = dangerCount;

            s.hasBoot = hasBoot;
            s.hasLocation = hasLocation;
            s.hasMic = hasMic;
            s.hasCamera = hasCamera;
            s.hasOverlay = hasOverlay;
            s.hasStorage = hasStorage;
            s.hasVpnBind = hasVpnBind;
            s.hasPostNotif = hasPostNotif;

            int capScore = 0;
            StringBuilder tags = new StringBuilder();

            if (dangerCount >= 8) { capScore += 12; tags.append("perm-heavy, "); }
            else if (dangerCount >= 5) { capScore += 8; tags.append("perm-heavy, "); }
            else if (dangerCount >= 3) { capScore += 4; }

            if (hasBoot) { capScore += 6; tags.append("boot-aware, "); }
            if (hasLocation) { capScore += 5; tags.append("location, "); }
            if (hasMic) { capScore += 5; tags.append("mic, "); }
            if (hasCamera) { capScore += 4; tags.append("camera, "); }
            if (hasOverlay) { capScore += 7; tags.append("overlay, "); }
            if (hasStorage) { capScore += 3; tags.append("storage, "); }
            if (hasVpnBind) { capScore += 6; tags.append("vpn, "); }
            if (hasPostNotif) { capScore += 2; tags.append("notifications, "); }

            boolean bg =
                    hasBoot || hasLocation || hasVpnBind || hasOverlay || hasPostNotif ||
                    dangerCount >= 5;

            s.bgCapable = bg;
            s.capabilityScore = capScore;

            // trim tags
            String tgs = tags.toString().trim();
            if (tgs.endsWith(",")) tgs = tgs.substring(0, tgs.length() - 1).trim();
            s.tags = (tgs.length() > 0 ? tgs : "capability");

            // counters (user apps only for map)
            if (!isSystem) {
                if (bg) out.bgCapable++;
                if (dangerCount >= 5) out.permHeavy++;

                if (hasBoot) out.bootAware++;
                if (hasOverlay) out.overlayLike++;
                if (hasVpnBind) out.vpnLike++;
                if (hasLocation) out.locationLike++;
                if (hasMic) out.micLike++;
                if (hasCamera) out.cameraLike++;
                if (hasStorage) out.storageLike++;
                if (hasPostNotif) out.notifLike++;

                // redundancy heuristics (pkg name)
                final String lowPkg = pkg.toLowerCase(Locale.US);
                if (lowPkg.contains("clean") || lowPkg.contains("booster") || lowPkg.contains("optimizer"))
                    out.cleanersLike++;
                if (lowPkg.contains("launcher"))
                    out.launchersLike++;
                if (lowPkg.contains("avast") || lowPkg.contains("kaspersky") || lowPkg.contains("avg") ||
                    lowPkg.contains("bitdefender") || lowPkg.contains("eset") || lowPkg.contains("norton"))
                    out.antivirusLike++;
                if (lowPkg.contains("keyboard") || lowPkg.contains("ime"))
                    out.keyboardsLike++;
            }

            // -----------------------------
            // DATA (TrafficStats, honest, since boot)
            // -----------------------------
            // TrafficStats supports only our own UID on Android 7+.
            // Do not reinterpret unsupported cross-app counters as zero traffic.
            if (ai.uid == android.os.Process.myUid()) {
                long rx = -1L, tx = -1L;
                try { rx = TrafficStats.getUidRxBytes(ai.uid); } catch (Throwable ignore) {}
                try { tx = TrafficStats.getUidTxBytes(ai.uid); } catch (Throwable ignore) {}
                s.dataBytesSinceBoot = Lab26Evidence.ownUidTrafficBytes(
                        ai.uid, android.os.Process.myUid(), rx, tx);
            } else {
                s.dataBytesSinceBoot = -1L;
            }

                        // -----------------------------
            // FG TIME 24h (requires usage access)
            // -----------------------------
            Long fg = fgMsMap24h.get(pkg);
            s.fgMs24h = Lab26Evidence.foregroundMs(
                    out.usageAccessOk, out.usageQuerySucceeded,
                    fg != null ? fg : -1L);

            // -----------------------------
            // BATTERY EXPOSURE SCORE (heuristic, NOT mAh)
            // -----------------------------
            // Historical API field retained for compatibility only.
            // Battery drain cannot be inferred from manifest declarations, traffic,
            // or foreground time without calibrated per-app energy measurement.
            long impact = 0L;
            s.estImpactScore = impact;

            if (!isSystem) {
                allUser.add(s);
                // No privileged UID mapping or cross-package collection.
            }
        }

        // -----------------------------
        // Explicitly no su, dumpsys, privileged UID probes, or private-data scans.
        out.rootDumpsysOk = false;

                // TOP LISTS (non-root)
        // -----------------------------
        ArrayList<AppScore> capSorted = new ArrayList<>(allUser);
        ArrayList<AppScore> dataSorted = new ArrayList<>(allUser);
        ArrayList<AppScore> battSorted = new ArrayList<>(allUser);

        Collections.sort(capSorted, (a, b) -> Integer.compare(b.capabilityScore, a.capabilityScore));
        Collections.sort(dataSorted, (a, b) -> Long.compare(b.dataBytesSinceBoot, a.dataBytesSinceBoot));
        // The legacy field topBatteryExposure is now a compatibility container
        // for positive OS-reported foreground observations, NOT energy.
        dataSorted.removeIf(a -> a.dataBytesSinceBoot < 0L);
        battSorted.removeIf(a -> a.fgMs24h <= 0L);
        Collections.sort(battSorted, (a, b) -> Long.compare(b.fgMs24h, a.fgMs24h));

        out.topCapabilityHeavy = takeTopFiltered(capSorted, 10);
        out.topDataConsumers   = takeTopFiltered(dataSorted, 10);
        out.topBatteryExposure = takeTopFiltered(battSorted, 10);

        // -----------------------------
        // The number of apps, declared permissions, and usage time cannot
        // establish system pressure. This engine does not publish a health verdict.
        out.orphan.attempted = false;
        out.orphan.orphanDirs = 0;
        out.orphan.orphanBytes = 0L;
        out.appsImpactHigh = false;
        out.riskPoints = 0;

                return out;
    }

    // ============================================================
    // HELPERS (Usage Access)
    // ============================================================

    private static boolean hasUsageAccess(Context c) {
        try {
            AppOpsManager appOps = (AppOpsManager) c.getSystemService(Context.APP_OPS_SERVICE);
            if (appOps == null) return false;

            int mode;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mode = appOps.unsafeCheckOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(),
                        c.getPackageName()
                );
            } else {
                //noinspection deprecation
                mode = appOps.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(),
                        c.getPackageName()
                );
            }
            return mode == AppOpsManager.MODE_ALLOWED;

        } catch (Throwable ignore) {
            return false;
        }
    }

    // ============================================================
    // HELPERS (Top filtered user apps)
    // ============================================================

    private static ArrayList<AppScore> takeTopFiltered(List<AppScore> src, int limit) {
        ArrayList<AppScore> out = new ArrayList<>();
        if (src == null) return out;

        for (AppScore s : src) {
            if (s == null) continue;
            if (s.isSystem) continue;

            String pkg = (s.pkg != null ? s.pkg : "");
            if (pkg.startsWith("com.android.")) continue;
            if (pkg.startsWith("com.google.android.")) continue;
            if ("com.android.vending".equals(pkg)) continue;

            out.add(s);
            if (out.size() >= limit) break;
        }
        return out;
    }

}
