package com.gel.cleaner;

/** Snapshot thresholds; no inference about crashes or application responsiveness. */
final class GelMiniPolicy {
    static final long DAY = 24L * 60 * 60 * 1000;
    static int temperatureLevel(double c) {
        if (!Double.isFinite(c)) return 0;
        return c >= 50 ? 2 : c >= 45 ? 1 : 0;
    }
    static int storageLevel(long available, long total) {
        if (available < 0 || total <= 0 || available > total) return 0;
        double ratio = available / (double) total;
        return available <= 512L * 1024 * 1024 || ratio <= .02 ? 2
                : available <= 1024L * 1024 * 1024 || ratio <= .05 ? 1 : 0;
    }
    static boolean shouldNotify(int level, int previous, long last, long now) {
        return level > 0 && (level > previous || last == 0 || now < last || now - last >= DAY);
    }
}
