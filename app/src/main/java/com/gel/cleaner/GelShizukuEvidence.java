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
