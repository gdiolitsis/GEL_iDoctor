package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class AcousticProbeNarrativeTest {
    @Test public void lab1PositiveIsObservationNotCertifiedSpeaker() {
        String greek = AcousticProbeNarrative.lab1Conclusion(true, true, true);
        assertTrue(greek.contains("ΘΕΤΙΚΗ ΑΚΟΥΣΤΙΚΗ ΕΝΔΕΙΞΗ"));
        assertTrue(greek.contains("μικρόφωνο"));
        assertTrue(greek.contains("Δεν πιστοποιείται"));
        assertFalse(greek.contains("όλα λειτουργούν καλά"));
    }

    @Test public void lab2TwoOfFourIsNotACompleteSuccess() {
        String text = AcousticProbeNarrative.lab2Conclusion(true, 2, 4, true);
        assertTrue(text.contains("ΜΕΡΙΚΗ ΑΚΟΥΣΤΙΚΗ ΕΝΔΕΙΞΗ"));
        assertTrue(text.contains("2/4"));
        assertFalse(text.contains("ΘΕΤΙΚΗ ΑΚΟΥΣΤΙΚΗ ΕΝΔΕΙΞΗ"));
    }

    @Test public void lab2FourOfFourStillCannotCertifySpeakerHealth() {
        String text = AcousticProbeNarrative.lab2Conclusion(true, 4, 4, true);
        assertTrue(text.contains("4/4"));
        assertTrue(text.contains("Δεν πιστοποιείται"));
    }

    @Test public void unavailableAndIncompleteAreDistinct() {
        assertTrue(AcousticProbeNarrative.lab1Conclusion(false, false, true)
                .startsWith("ΜΗ ΔΙΑΘΕΣΙΜΟ"));
        assertTrue(AcousticProbeNarrative.lab2Conclusion(true, 1, 3, true)
                .startsWith("ΜΗ ΟΛΟΚΛΗΡΩΜΕΝΗ"));
        assertTrue(AcousticProbeNarrative.lab2Conclusion(false, 0, 0, true)
                .startsWith("ΜΗ ΔΙΑΘΕΣΙΜΟ"));
    }

    @Test public void translatedKnownProbeReasonIsActualGreekSentence() {
        String greek = AcousticProbeNarrative.reason(
                "Time-aligned generated frequencies observed at microphone", true);
        assertTrue(greek.contains("συχνότητες"));
        assertFalse(greek.contains("Time-aligned"));
        assertTrue(AcousticProbeNarrative.reason(
                "No repeatable synchronized tone above microphone baseline", true)
                .contains("Δεν ανιχνεύθηκε"));
        assertTrue(AcousticProbeNarrative.reason(
                "Unrecognized vendor DSP error", true).contains("Δεν"));
    }

    @Test public void microphoneRouteAndSampleCountStayGrounded() {
        assertEquals("Είσοδος μικροφώνου Android — τύπος συσκευής 15",
                AcousticProbeNarrative.route("Android audio input device type 15", true));
        assertEquals("Android audio input device type 15",
                AcousticProbeNarrative.route("Android audio input device type 15", false));
        assertTrue(AcousticProbeNarrative.lab2Conclusion(true, 0, 4, true).contains("0/4"));
    }
}
