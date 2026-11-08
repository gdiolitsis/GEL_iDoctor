package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class RebootHistoryEvidenceTest {
    @Test public void missingRebootHistoryIsNotNormal() {
        assertEquals(RebootHistoryEvidence.Verdict.INSUFFICIENT_DATA,
                RebootHistoryEvidence.classify(-1, 0L));
        assertEquals(RebootHistoryEvidence.Verdict.INSUFFICIENT_DATA,
                RebootHistoryEvidence.classify(0, 0L));
    }

    @Test public void onlyVerifiedEventHistoryCanBeClassified() {
        long day = 24L * 60 * 60 * 1000;
        assertEquals(RebootHistoryEvidence.Verdict.NO_REPEATED_EVENTS_OBSERVED,
                RebootHistoryEvidence.classify(0, day));
        assertEquals(RebootHistoryEvidence.Verdict.NO_REPEATED_EVENTS_OBSERVED,
                RebootHistoryEvidence.classify(1, day));
        assertEquals(RebootHistoryEvidence.Verdict.REPEATED_OBSERVED,
                RebootHistoryEvidence.classify(2, day));
    }
}
