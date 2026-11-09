package com.gel.cleaner;

import org.junit.Test;
import java.util.Random;
import static org.junit.Assert.*;

public class AcousticEvidenceMathTest {
    private static final int RATE = 16000;

    private static short[] signal(int samples, int lowHz, int highHz,
                                  int amplitude, int noiseAmplitude, int seed) {
        short[] out = new short[samples];
        Random random = new Random(seed);
        for (int i = 0; i < samples; i++) {
            double wave = amplitude * Math.sin(2d * Math.PI * lowHz * i / RATE)
                    + amplitude * Math.sin(2d * Math.PI * highHz * i / RATE);
            double noise = (random.nextDouble() * 2 - 1) * noiseAmplitude;
            out[i] = (short) Math.round(Math.max(-32768, Math.min(32767, wave + noise)));
        }
        return out;
    }

    @Test public void simultaneousDtMfFrequenciesRiseAboveAmbient() {
        short[] ambient = signal(4000, 697, 1209, 0, 100, 17);
        short[] playback = signal(12800, 697, 1209, 7000, 100, 19);
        AcousticEvidenceMath.Assessment result =
                AcousticEvidenceMath.evaluate(ambient, playback, RATE, 697, 1209);
        assertTrue(result.reason, result.valid);
        assertTrue(result.reason, result.responseObserved);
        assertFalse(result.clipping);
        assertTrue(result.lowFrequencyRiseDb > 10);
        assertTrue(result.highFrequencyRiseDb > 10);
    }

    @Test public void ambientNoiseWithoutPlayedSignalIsNotSpeakerEvidence() {
        short[] ambient = signal(4000, 697, 1209, 0, 450, 3);
        short[] later = signal(12800, 697, 1209, 0, 450, 5);
        AcousticEvidenceMath.Assessment result =
                AcousticEvidenceMath.evaluate(ambient, later, RATE, 697, 1209);
        assertTrue(result.valid);
        assertFalse(result.responseObserved);
    }

    @Test public void staleUnrelatedFrequencyDoesNotPass() {
        short[] ambient = signal(4000, 697, 1209, 0, 120, 2);
        short[] unrelated = signal(12800, 500, 1800, 8000, 120, 8);
        AcousticEvidenceMath.Assessment result =
                AcousticEvidenceMath.evaluate(ambient, unrelated, RATE, 697, 1209);
        assertFalse(result.responseObserved);
    }

    @Test public void clippingMeansInconclusiveEvenWithMatchingTone() {
        short[] ambient = signal(4000, 697, 1209, 0, 20, 1);
        short[] clipped = new short[12800];
        for (int i = 0; i < clipped.length; i++) {
            clipped[i] = (short) ((i % 2 == 0) ? 32767 : -32768);
        }
        AcousticEvidenceMath.Assessment result =
                AcousticEvidenceMath.evaluate(ambient, clipped, RATE, 697, 1209);
        assertTrue(result.clipping);
        assertFalse(result.responseObserved);
    }

    @Test public void unavailableRecordingMustNotBeHealthy() {
        AcousticEvidenceMath.Assessment result =
                AcousticEvidenceMath.evaluate(null, null, RATE, 697, 1209);
        assertFalse(result.valid);
        assertFalse(result.responseObserved);
    }
    @Test public void allFourDtMfPairsCanBeIndependentlyObserved() {
        int[][] pairs = {{697, 1209}, {697, 1477}, {770, 1477}, {852, 1477}};
        for (int i = 0; i < pairs.length; i++) {
            int low = pairs[i][0], high = pairs[i][1];
            short[] ambient = signal(4000, low, high, 0, 80, 30 + i);
            short[] challenge = signal(12800, low, high, 5000, 80, 40 + i);
            AcousticEvidenceMath.Assessment result =
                    AcousticEvidenceMath.evaluate(ambient, challenge, RATE, low, high);
            assertTrue("Pair " + i + ": " + result.reason, result.valid);
            assertTrue("Pair " + i + ": " + result.reason, result.responseObserved);
            assertFalse(result.clipping);
        }
    }

}
