package com.gel.cleaner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Narrow, source-labelled evidence contract shared by existing GEL labs.
 * Never convert an unavailable value to "healthy" or an estimated SoH.
 */
public final class GelShizukuEvidence {
    private GelShizukuEvidence() { }

    public static final int CAPACITY = 1;
    public static final int CRASH = 2;
    public static final int THERMAL = 3;
    public static final int MEMORY = 4;
    public static final int BOOT = 5;
    public static final int UFS = 6;
    public static final int LOW_MEMORY = 7;
    public static final int APP_OPS = 8;

    public static final class Capacity {
        public final long fullMah;     // fuel gauge's full-charge estimate
        public final long designMah;   // nominal design capacity, not current health
        public final String fullSource;
        public final String designSource;

        Capacity(long fullMah, long designMah, String fullSource, String designSource) {
            this.fullMah = fullMah;
            this.designMah = designMah;
            this.fullSource = fullSource;
            this.designSource = designSource;
        }

        public boolean hasReportedFull() { return fullMah > 0; }
        public boolean hasReportedDesign() { return designMah > 0; }
        public int estimatedSoHPercent() {
            if (!hasReportedFull() || !hasReportedDesign()) return -1;
            double pct = 100d * fullMah / (double) designMah;
            // An out-of-range gauge estimate is not a valid health certificate.
            return pct >= 20d && pct <= 120d ? (int) Math.round(pct) : -1;
        }
    }


    /** UFS health descriptor bytes: not measured NAND throughput or an exact
     * remaining lifespan. 1-10 are lifetime-used deciles; 11 means >100%.
     * EOL 1 normal, 2 warning, 3 urgent; 0 unknown. */
    public static final class UfsWear {
        public final int lifetimeA, lifetimeB, preEol;
        private UfsWear(int a, int b, int e) {
            lifetimeA = a;
            lifetimeB = b;
            preEol = e;
        }
        public boolean hasEol() { return preEol >= 1 && preEol <= 3; }
        public boolean hasLifetime() { return lifetimeA > 0 || lifetimeB > 0; }
        public boolean hasWearAlert() {
            return preEol >= 2 || lifetimeA >= 10 || lifetimeB >= 10;
        }
        public static String lifetimeInterval(int level) {
            if (level == 0) return "unknown";
            if (level == 11) return "over 100% of estimated endurance used";
            return ((level - 1) * 10) + "-" + (level * 10)
                    + "% of estimated endurance used";
        }
    }

    public static UfsWear parseUfs(String wire) {
        if (wire == null || !wire.matches("UFS\\|[0-9]{1,3}\\|[0-9]{1,3}\\|[0-9]{1,3}"))
            return null;
        String[] parts = wire.split("\\|");
        try {
            int a = Integer.parseInt(parts[1]);
            int b = Integer.parseInt(parts[2]);
            int e = Integer.parseInt(parts[3]);
            if (a < 0 || a > 11 || b < 0 || b > 11 || e < 0 || e > 3
                    || (a == 0 && b == 0 && e == 0)) return null;
            return new UfsWear(a, b, e);
        } catch (RuntimeException invalid) {
            return null;
        }
    }

    /** Count of explicit LMK tags in the latest limited events-log sample.
     * Zero is NOT proof of no previous memory-driven process terminations. */
    public static int parseLmkCount(String wire) {
        if (wire == null || !wire.matches("LMK\\|[0-9]{1,3}")) return -1;
        try {
            int count = Integer.parseInt(wire.substring(4));
            return count <= 350 ? count : -1;
        } catch (NumberFormatException invalid) {
            return -1;
        }
    }

    public static final class AppOpAccess {
        public final String packageName, operation, timestamp;
        private AppOpAccess(String pkg, String op, String at) {
            packageName = pkg;
            operation = op;
            timestamp = at;
        }
    }

    /** A sampled AppOps *Access* timestamp (not a grant, reject or malware
     * finding). Only explicit, validated operation+package+timestamp rows. */
    public static List<AppOpAccess> parseAppOps(String wire) {
        if (wire == null) return Collections.emptyList();
        String[] lines = wire.split("\\n", -1);
        if (lines.length < 2 || !"APPOPS|SAMPLED".equals(lines[0]))
            return Collections.emptyList();
        List<AppOpAccess> accesses = new ArrayList<>();
        for (int i = 1; i < lines.length && i <= 20; i++) {
            String[] f = lines[i].split("\\|", -1);
            if (f.length != 3
                    || !f[0].matches("[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+")
                    || !f[1].matches("CAMERA|RECORD_AUDIO|FINE_LOCATION|COARSE_LOCATION")
                    || !f[2].matches("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"))
                continue;
            accesses.add(new AppOpAccess(f[0], f[1], f[2]));
        }
        return Collections.unmodifiableList(accesses);
    }

    public static Capacity parseCapacity(String wire) {
        if (wire == null) return null;
        String[] fields = wire.split("\\|", -1);
        if (fields.length != 5 || !"CAPACITY".equals(fields[0])) return null;
        try {
            long full = Long.parseLong(fields[1]);
            long design = Long.parseLong(fields[2]);
            if (!validMah(full) || !validMah(design) || full + design == 0)
                return null;
            if (!validSource(fields[3], full) || !validSource(fields[4], design))
                return null;
            return new Capacity(full, design, fields[3], fields[4]);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static boolean validMah(long value) {
        return value == 0 || (value >= 100 && value <= 30000);
    }

    private static boolean validSource(String source, long mah) {
        if (mah == 0) return source.isEmpty();
        return source.equals("BATTERY") || source.equals("BMS") || source.equals("FG");
    }

    public static final class CrashEvent {
        public final String process;
        public final String type;
        public final String when;
        CrashEvent(String process, String type, String when) {
            this.process = process;
            this.type = type;
            this.when = when;
        }
    }

    public static final class CrashScan {
        public final boolean read; // false means inaccessible, not zero crashes
        public final List<CrashEvent> events;
        private CrashScan(boolean read, List<CrashEvent> events) {
            this.read = read;
            this.events = Collections.unmodifiableList(events);
        }
    }

    public static CrashScan parseCrash(String wire) {
        if (wire == null || !wire.startsWith("CRASH|READ")) {
            return new CrashScan(false, Collections.emptyList());
        }
        String[] lines = wire.split("\\n", -1);
        if (!"CRASH|READ".equals(lines[0])) {
            return new CrashScan(false, Collections.emptyList());
        }
        List<CrashEvent> events = new ArrayList<>();
        for (int i = 1; i < lines.length && i <= 8; i++) {
            String[] f = lines[i].split("\\|", -1);
            if (f.length != 3) continue;
            if (!f[0].matches("[A-Za-z0-9_.:]{1,110}")) continue;
            if (!f[1].matches("CRASH|ANR|NATIVE_CRASH|WATCHDOG|KERNEL_PANIC"))
                continue;
            if (f[2].length() > 80 || f[2].indexOf('\r') >= 0) continue;
            events.add(new CrashEvent(f[0], f[1], f[2]));
        }
        return new CrashScan(true, events);
    }

    public static int parseThermalStatus(String wire) {
        if (wire == null || !wire.matches("THERMAL_STATUS\\|[0-6]")) return -1;
        return wire.charAt(wire.length() - 1) - '0';
    }

    public static String parseBootReason(String wire) {
        if (wire == null || !wire.matches("BOOT\\|[a-zA-Z0-9_,.:-]{1,80}"))
            return null;
        return wire.substring(5);
    }
}
