package com.gel.cleaner;

import org.junit.Test;

import static org.junit.Assert.*;

/** A gesture must change the separation of TWO active pointers to count. */
public final class GelPinchZoomEvidenceTest {
    private GelPinchZoomEvidence fresh() {
        return new GelPinchZoomEvidence(42f, 30f);
    }

    @Test public void twoStationaryFingersAreNotPinchEvidence() {
        GelPinchZoomEvidence e = fresh();
        e.beginTwoFingerGesture(110f);
        e.observeTwoFingerSpan(110f);
        e.observeTwoFingerSpan(113f);
        e.endGesture();
        assertTrue(e.hasDualTouch());
        assertFalse(e.hasZoomIn());
        assertFalse(e.hasZoomOut());
        assertFalse(e.isComplete());
    }

    @Test public void onlyOneDirectionDoesNotGenerateFullPass() {
        GelPinchZoomEvidence e = fresh();
        e.beginTwoFingerGesture(100f);
        e.observeTwoFingerSpan(144f);
        e.endGesture();
        assertTrue(e.hasZoomIn());
        assertFalse(e.hasZoomOut());
        assertFalse(e.isComplete());
    }

    @Test public void separateTwoFingerGesturesCanCompleteBothDirections() {
        GelPinchZoomEvidence e = fresh();
        e.beginTwoFingerGesture(100f);
        e.observeTwoFingerSpan(145f);
        e.endGesture();
        e.beginTwoFingerGesture(200f);
        e.observeTwoFingerSpan(150f);
        e.endGesture();
        assertTrue(e.hasZoomIn());
        assertTrue(e.hasZoomOut());
        assertTrue(e.isComplete());
    }

    @Test public void expansionThenContractionInOneGestureCounts() {
        GelPinchZoomEvidence e = fresh();
        e.beginTwoFingerGesture(100f);
        e.observeTwoFingerSpan(158f);
        e.observeTwoFingerSpan(109f);
        assertTrue(e.isComplete());
    }

    @Test public void smallJitterDoesNotProduceFalsePositive() {
        GelPinchZoomEvidence e = fresh();
        e.beginTwoFingerGesture(120f);
        for (float span : new float[]{116f, 130f, 109f, 126f, 119f, 135f})
            e.observeTwoFingerSpan(span);
        assertFalse(e.isComplete());
        assertFalse(e.hasZoomIn());
        assertFalse(e.hasZoomOut());
    }

    @Test public void tooCloseOrInvalidInitialTouchCannotStartEvidence() {
        GelPinchZoomEvidence e = fresh();
        e.beginTwoFingerGesture(4f);
        e.observeTwoFingerSpan(145f);
        e.endGesture();
        e.beginTwoFingerGesture(Float.NaN);
        e.observeTwoFingerSpan(180f);
        assertFalse(e.hasDualTouch());
        assertFalse(e.isComplete());
    }

    @Test public void afterEndingGestureMovementCannotContribute() {
        GelPinchZoomEvidence e = fresh();
        e.beginTwoFingerGesture(110f);
        e.endGesture();
        e.observeTwoFingerSpan(200f);
        assertFalse(e.hasZoomIn());
        assertFalse(e.hasZoomOut());
    }

    @Test public void translationOrNegligibleSpanChangesNeverPass() {
        GelPinchZoomEvidence e = fresh();
        e.beginTwoFingerGesture(120f);
        e.observeTwoFingerSpan(120f);
        e.observeTwoFingerSpan(126f);
        e.observeTwoFingerSpan(115f);
        assertFalse(e.isComplete());
    }
}
