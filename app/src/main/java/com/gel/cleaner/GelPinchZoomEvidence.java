package com.gel.cleaner;

/**
 * Pure-Java evidence accumulator for Lab 6 pinch gestures.
 *
 * A successful two-finger gesture requires a real change in inter-pointer
 * distance. Merely touching with two fingers, moving both fingers together,
 * or dragging with a single finger is not enough. The two directions are
 * independent and may be completed in separate gestures.
 */
public final class GelPinchZoomEvidence {
    private static final float EXPAND_RATIO = 1.22f;
    private static final float CONTRACT_RATIO = 0.82f;
    private final float minDeltaPx;
    private final float minInitialSpanPx;
    private boolean dualTouchSeen;
    private boolean zoomInSeen;
    private boolean zoomOutSeen;
    private boolean tracking;
    private float minSpan;
    private float maxSpan;

    public GelPinchZoomEvidence(float minDeltaPx, float minInitialSpanPx) {
        if (!(minDeltaPx > 0f) || !(minInitialSpanPx > 0f)
                || Float.isNaN(minDeltaPx) || Float.isNaN(minInitialSpanPx))
            throw new IllegalArgumentException("Gesture thresholds must be positive");
        this.minDeltaPx = minDeltaPx;
        this.minInitialSpanPx = minInitialSpanPx;
    }

    public void beginTwoFingerGesture(float initialSpanPx) {
        tracking = Float.isFinite(initialSpanPx) && initialSpanPx >= minInitialSpanPx;
        if (!tracking) return;
        dualTouchSeen = true;
        minSpan = initialSpanPx;
        maxSpan = initialSpanPx;
    }

    public void observeTwoFingerSpan(float spanPx) {
        if (!tracking || !Float.isFinite(spanPx) || spanPx <= 0f) return;
        // Compare against the smallest and largest values *already observed*.
        // A whole-hand translation leaves the distance unchanged.
        if (spanPx - minSpan >= minDeltaPx && spanPx >= minSpan * EXPAND_RATIO)
            zoomInSeen = true;
        if (maxSpan - spanPx >= minDeltaPx && spanPx <= maxSpan * CONTRACT_RATIO)
            zoomOutSeen = true;
        minSpan = Math.min(minSpan, spanPx);
        maxSpan = Math.max(maxSpan, spanPx);
    }

    public void endGesture() { tracking = false; }

    public boolean hasDualTouch() { return dualTouchSeen; }
    public boolean hasZoomIn() { return zoomInSeen; }
    public boolean hasZoomOut() { return zoomOutSeen; }
    public boolean isComplete() { return dualTouchSeen && zoomInSeen && zoomOutSeen; }
}
