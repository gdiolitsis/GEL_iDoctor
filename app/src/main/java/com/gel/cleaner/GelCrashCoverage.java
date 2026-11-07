package com.gel.cleaner;

/** Access denied, skipped tags and capped reads never imply zero device crashes. */
final class GelCrashCoverage {
    private final int expected;
    private int completed;
    GelCrashCoverage(int expected) { this.expected = expected; }
    void recordQuery(boolean fullyRead) { if (fullyRead) completed++; }
    boolean complete() { return expected > 0 && completed == expected; }
}
