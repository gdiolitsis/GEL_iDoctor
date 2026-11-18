package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Release gate: source-built QA v61 must contain both the consented Shizuku
 * implementation and independent two-finger gesture verification, not just
 * a bumped versionCode on a stale APK.
 */
public final class Version61SourceProvenanceTest {
    @Test public void candidateContainsRealShizukuService() {
        assertNotNull(GelShizukuReadOnlyService.class);
        assertNotNull(GelShizukuDiagnosticSession.class);
        assertNotNull(GelShizukuEvidence.class);
    }

    @Test public void candidateContainsNewLab6PinchEvidence() {
        GelPinchZoomEvidence pinch = new GelPinchZoomEvidence(42f, 30f);
        pinch.beginTwoFingerGesture(100f);
        pinch.observeTwoFingerSpan(150f);
        pinch.endGesture();
        pinch.beginTwoFingerGesture(160f);
        pinch.observeTwoFingerSpan(100f);
        assertTrue(pinch.hasDualTouch());
        assertTrue(pinch.hasZoomIn());
        assertTrue(pinch.hasZoomOut());
        assertTrue(pinch.isComplete());
    }
}
