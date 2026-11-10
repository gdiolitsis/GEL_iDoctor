package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab14BRateEvidenceTest {
    private static final long NOW = 10_000_000L;

    private boolean valid(long duration, long recorded, float rate) {
        return Lab14BRateEvidence.isUsable(1, true,
                Lab14BRateEvidence.SOURCE, duration, recorded, NOW, rate);
    }

    @Test public void acceptsRecentValidMeasuredRate() {
        assertTrue(valid(240_000L, NOW - 60_000L, 1560.5f));
        assertTrue(valid(300_000L, NOW - Lab14BRateEvidence.MAX_AGE_MS, 900f));
    }

    @Test public void rejectsExpiredAndFutureRecords() {
        assertFalse(valid(300_000L, NOW - Lab14BRateEvidence.MAX_AGE_MS - 1L, 900f));
        assertFalse(valid(300_000L, NOW + 1L, 900f));
        assertFalse(valid(300_000L, 0L, 900f));
    }

    @Test public void rejectsShortOrMissingDuration() {
        assertFalse(valid(239_999L, NOW - 10_000L, 900f));
        assertFalse(valid(0L, NOW - 10_000L, 900f));
    }

    @Test public void rejectsInvalidOrNonfiniteRates() {
        assertFalse(valid(300_000L, NOW - 10_000L, -1f));
        assertFalse(valid(300_000L, NOW - 10_000L, 0f));
        assertFalse(valid(300_000L, NOW - 10_000L, Float.NaN));
        assertFalse(valid(300_000L, NOW - 10_000L, Float.POSITIVE_INFINITY));
    }

    @Test public void rejectsUnmarkedOrWrongProvenance() {
        assertFalse(Lab14BRateEvidence.isUsable(0, true,
                Lab14BRateEvidence.SOURCE, 300_000L, NOW - 1000, NOW, 700f));
        assertFalse(Lab14BRateEvidence.isUsable(1, false,
                Lab14BRateEvidence.SOURCE, 300_000L, NOW - 1000, NOW, 700f));
        assertFalse(Lab14BRateEvidence.isUsable(1, true,
                "HEURISTIC_OR_LEGACY", 300_000L, NOW - 1000, NOW, 700f));
        assertFalse(Lab14BRateEvidence.isUsable(1, true,
                null, 300_000L, NOW - 1000, NOW, 700f));
    }
}
