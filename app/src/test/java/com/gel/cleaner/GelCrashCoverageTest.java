package com.gel.cleaner;
import org.junit.Test;
import static org.junit.Assert.*;
public class GelCrashCoverageTest {
    @Test public void deniedOrSkippedQueriesNeverMeanZeroCrashes() {
        assertFalse(new GelCrashCoverage(7).complete());
        assertFalse(new GelCrashCoverage(0).complete());
    }
    @Test public void partialOrCappedReadsAreNotComplete() {
        GelCrashCoverage c = new GelCrashCoverage(7);
        for (int i = 0; i < 6; i++) c.recordQuery(true);
        c.recordQuery(false);
        assertFalse(c.complete());
    }
    @Test public void allSuccessfulEmptyQueriesCanReportAvailableRecordCounts() {
        GelCrashCoverage c = new GelCrashCoverage(7);
        for (int i = 0; i < 7; i++) c.recordQuery(true);
        assertTrue(c.complete());
    }
}
