package com.gel.cleaner;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import static org.junit.Assert.*;

/**
 * A buyer sees measured evidence and access limits, not an "all clear" from
 * an unavailable Android history. The PRO precheck never bypasses Lab25.
 */
public final class Lab25BuyerEvidenceTest {
    private static CriticalSystemCrashEvidence.Summary parse(String logs) throws Exception {
        return CriticalSystemCrashEvidence.parseUserReport(
                new ByteArrayInputStream(logs.getBytes(StandardCharsets.UTF_8)),
                Collections.emptySet());
    }

    @Test public void requiresARealRecentSameBootLab25Run() {
        long wall = 100000000L;
        long elapsed = 72000000L;
        assertFalse(Lab25BuyerEvidence.canUse(0, wall, elapsed, wall + 1000, elapsed + 1000));
        assertTrue(Lab25BuyerEvidence.canUse(1, wall, elapsed, wall + 1000, elapsed + 1000));
        assertFalse(Lab25BuyerEvidence.canUse(1, wall, elapsed, wall + 1000, elapsed - 1));
        assertFalse(Lab25BuyerEvidence.canUse(1, wall, elapsed,
                wall + LabEvidenceFreshness.MAX_AGE_MS + 1,
                elapsed + LabEvidenceFreshness.MAX_AGE_MS + 1));
        assertFalse(Lab25BuyerEvidence.canUse(1, wall, elapsed, wall + 600000, elapsed + 1000));
    }

    @Test public void limitedHistoryCountsAsExecutionButNotAsHealthyProof() throws Exception {
        CriticalSystemCrashEvidence.Summary empty = parse("Android diagnostic text without crashes\n");
        assertEquals(Lab25BuyerEvidence.NONE, Lab25BuyerEvidence.classify(empty));
        assertTrue(Lab25BuyerEvidence.isKnownCoverage(Lab25BuyerEvidence.LIMITED));
        assertFalse(Lab25BuyerEvidence.isKnownCoverage("HEALTHY"));
        assertFalse(Lab25BuyerEvidence.isKnown("UNRESTRICTED_CLEAN"));
    }

    @Test public void isolatedRepeatedAndStrongAreDistinctBuyerConclusions() throws Exception {
        String first = "11-16 10:00:00.000 E AndroidRuntime: FATAL EXCEPTION: main\n"
                + "Process: com.android.systemui, PID: 10\n";
        String second = "11-16 10:03:00.000 E AndroidRuntime: FATAL EXCEPTION: main\n"
                + "Process: com.android.systemui, PID: 11\n";
        String third = "11-16 10:08:00.000 E AndroidRuntime: FATAL EXCEPTION: main\n"
                + "Process: com.android.systemui, PID: 12\n";
        assertEquals(Lab25BuyerEvidence.ISOLATED, Lab25BuyerEvidence.classify(parse(first)));
        assertEquals(Lab25BuyerEvidence.REPEATED, Lab25BuyerEvidence.classify(parse(first + second)));
        assertEquals(Lab25BuyerEvidence.STRONG, Lab25BuyerEvidence.classify(parse(first + second + third)));
    }

    @Test public void seriousEventIsReportedEvenIfNotRecurrent() throws Exception {
        assertEquals(Lab25BuyerEvidence.SERIOUS,
                Lab25BuyerEvidence.classify(parse(
                        "11-16 10:00:00.000 E kernel: Kernel panic - not syncing: fatal exception\n")));
    }

    @Test public void userReportCannotOverwriteStrongerEarlierEvidence() {
        assertFalse(Lab25BuyerEvidence.shouldReplace(Lab25BuyerEvidence.STRONG, Lab25BuyerEvidence.NONE));
        assertFalse(Lab25BuyerEvidence.shouldReplace(Lab25BuyerEvidence.SERIOUS, Lab25BuyerEvidence.REPEATED));
        assertTrue(Lab25BuyerEvidence.shouldReplace(Lab25BuyerEvidence.NONE, Lab25BuyerEvidence.STRONG));
        assertFalse(Lab25BuyerEvidence.shouldReplace(Lab25BuyerEvidence.NONE, "UNKNOWN"));
    }

    @Test public void invalidOrInflatedCountsCannotPassThePrepurchaseGate() {
        assertTrue(Lab25BuyerEvidence.validCounts(0, 0));
        assertTrue(Lab25BuyerEvidence.validCounts(3, 2));
        assertFalse(Lab25BuyerEvidence.validCounts(-1, -1));
        assertFalse(Lab25BuyerEvidence.validCounts(2, 3));
        assertFalse(Lab25BuyerEvidence.validCounts(129, 4));
    }
}
