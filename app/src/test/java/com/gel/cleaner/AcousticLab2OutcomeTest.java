package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AcousticLab2OutcomeTest {
    @Test public void fourCorrelatedTonesAreObserved() {
        assertEquals(AcousticLab2Outcome.Status.OBSERVED,
                AcousticLab2Outcome.classify(true, true, 4));
    }

    @Test public void completeSamplingWithoutCorrelationIsInconclusive() {
        assertEquals(AcousticLab2Outcome.Status.INCONCLUSIVE,
                AcousticLab2Outcome.classify(true, false, 4));
    }

    @Test public void noValidSamplesCannotBeCalledComplete() {
        assertEquals(AcousticLab2Outcome.Status.UNAVAILABLE,
                AcousticLab2Outcome.classify(false, false, 0));
        assertEquals(AcousticLab2Outcome.Status.UNAVAILABLE,
                AcousticLab2Outcome.classify(false, true, 0));
    }

    @Test public void partialCaptureIsNotACompletedTest() {
        assertEquals(AcousticLab2Outcome.Status.PARTIAL,
                AcousticLab2Outcome.classify(false, false, 2));
        assertEquals(AcousticLab2Outcome.Status.PARTIAL,
                AcousticLab2Outcome.classify(false, true, 3));
    }

    @Test public void inconsistentCaptureStatusDoesNotProduceFalsePass() {
        assertEquals(AcousticLab2Outcome.Status.PARTIAL,
                AcousticLab2Outcome.classify(true, true, 3));
        assertEquals(AcousticLab2Outcome.Status.PARTIAL,
                AcousticLab2Outcome.classify(false, true, 4));
        assertEquals(AcousticLab2Outcome.Status.UNAVAILABLE,
                AcousticLab2Outcome.classify(true, true, 5));
    }
}
