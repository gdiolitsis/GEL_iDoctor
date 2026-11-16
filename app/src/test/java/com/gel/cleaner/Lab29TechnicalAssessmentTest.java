package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/** Staged Lab25 -> present Lab29 source-bound technical snapshot. */
public final class Lab29TechnicalAssessmentTest {
    @Test public void distinguishesNeutralCoverageFromObservedCrashes() {
        assertEquals(Lab29TechnicalAssessment.LIMITED_CRASH_HISTORY,
                Lab29TechnicalAssessment.summarize(
                        Lab25BuyerEvidence.NONE, Lab25BuyerEvidence.LIMITED));
        assertEquals(Lab29TechnicalAssessment.NO_RECORDED_INSTABILITY,
                Lab29TechnicalAssessment.summarize(
                        Lab25BuyerEvidence.NONE, Lab25BuyerEvidence.ACCESSIBLE));
        assertEquals(Lab29TechnicalAssessment.ISOLATED_EVENT,
                Lab29TechnicalAssessment.summarize(
                        Lab25BuyerEvidence.ISOLATED, Lab25BuyerEvidence.LIMITED));
        assertNull(Lab29TechnicalAssessment.summarize("HEALTHY", Lab25BuyerEvidence.LIMITED));
    }

    @Test public void repeatedAndSeriousIncidentsRequireReview() {
        assertEquals(Lab29TechnicalAssessment.REVIEW_REQUIRED,
                Lab29TechnicalAssessment.summarize(
                        Lab25BuyerEvidence.REPEATED, Lab25BuyerEvidence.ACCESSIBLE));
        assertEquals(Lab29TechnicalAssessment.REVIEW_REQUIRED,
                Lab29TechnicalAssessment.summarize(
                        Lab25BuyerEvidence.SERIOUS, Lab25BuyerEvidence.LIMITED));
        assertEquals(Lab29TechnicalAssessment.STRONG_INSTABILITY,
                Lab29TechnicalAssessment.summarize(
                        Lab25BuyerEvidence.STRONG, Lab25BuyerEvidence.USER_REPORT));
    }

    @Test public void sameBootAndUnchangedSourceAreRequired() {
        long nowWall = 100000000L, nowElapsed = 60000000L;
        long certWall = nowWall - 1000L, certElapsed = nowElapsed - 1000L;
        long sourceWall = nowWall - 4000L, sourceElapsed = nowElapsed - 4000L;
        assertTrue(Lab29TechnicalAssessment.isCurrent(
                1, certWall, certElapsed, sourceWall, sourceElapsed,
                sourceWall, sourceElapsed, nowWall, nowElapsed));
        assertFalse(Lab29TechnicalAssessment.isCurrent(
                0, certWall, certElapsed, sourceWall, sourceElapsed,
                sourceWall, sourceElapsed, nowWall, nowElapsed));
        assertFalse(Lab29TechnicalAssessment.isCurrent(
                1, certWall, certElapsed, sourceWall, sourceElapsed,
                sourceWall + 1L, sourceElapsed + 1L, nowWall, nowElapsed));
        assertFalse(Lab29TechnicalAssessment.isCurrent(
                1, certWall, certElapsed, sourceWall, sourceElapsed,
                sourceWall, sourceElapsed, nowWall, 50L));
    }

    @Test public void onlyKnownAssessmentStatesAreAccepted() {
        assertTrue(Lab29TechnicalAssessment.known(Lab29TechnicalAssessment.STRONG_INSTABILITY));
        assertTrue(Lab29TechnicalAssessment.known(Lab29TechnicalAssessment.LIMITED_CRASH_HISTORY));
        assertFalse(Lab29TechnicalAssessment.known("ALL_HARDWARE_CERTIFIED"));
        assertFalse(Lab29TechnicalAssessment.known(null));
    }
}
