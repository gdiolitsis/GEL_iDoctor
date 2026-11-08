package com.gel.cleaner;

/**
 * Pure signal-processing metrics for a bounded, synchronized DTMF microphone
 * challenge. A positive result means frequency-coincident mic response; it
 * does not identify which loudspeaker emitted the sound or certify acoustics.
 */
public final class AcousticEvidenceMath {
    private AcousticEvidenceMath() {}

    public static final class Assessment {
        public final boolean valid;
        public final boolean responseObserved;
        public final boolean clipping;
        public final double ambientRms;
        public final double challengeRms;
        public final double lowFrequencyRiseDb;
        public final double highFrequencyRiseDb;
        public final String reason;

        private Assessment(boolean valid, boolean observed, boolean clipping,
                           double ambientRms, double challengeRms,
                           double lowDb, double highDb, String reason) {
            this.valid = valid;
            this.responseObserved = observed;
            this.clipping = clipping;
            this.ambientRms = ambientRms;
            this.challengeRms = challengeRms;
            this.lowFrequencyRiseDb = lowDb;
            this.highFrequencyRiseDb = highDb;
            this.reason = reason;
        }
    }

    public static Assessment evaluate(short[] ambient, short[] challenge,
                                      int sampleRate, int lowHz, int highHz) {
        if (ambient == null || challenge == null || sampleRate < 8000
                || ambient.length < 1600 || challenge.length < 3200
                || lowHz <= 0 || highHz <= lowHz || highHz >= sampleRate / 2) {
            return new Assessment(false, false, false, Double.NaN, Double.NaN,
                    Double.NaN, Double.NaN, "Insufficient sampled audio or invalid parameters");
        }
        double ambientRms = rms(ambient);
        double toneRms = rms(challenge);
        double l0 = spectralMagnitude(ambient, sampleRate, lowHz);
        double l1 = spectralMagnitude(challenge, sampleRate, lowHz);
        double h0 = spectralMagnitude(ambient, sampleRate, highHz);
        double h1 = spectralMagnitude(challenge, sampleRate, highHz);
        double riseLow = 20d * Math.log10((l1 + 1d) / (l0 + 1d));
        double riseHigh = 20d * Math.log10((h1 + 1d) / (h0 + 1d));
        boolean clipping = clippingCount(challenge) > challenge.length / 100; // >1%
        // The baseline is measured with the stimulus OFF, same microphone and gain.
        // Never turn a missing microphone signal into a speaker hardware diagnosis.
        boolean observed = !clipping && toneRms > Math.max(80d, ambientRms * 1.2d)
                && riseLow >= 10d && riseHigh >= 10d;
        String reason = clipping ? "Recording clipped"
                : observed ? "Both generated DTMF frequencies rose above pre-tone baseline"
                : "Expected response could not be distinguished from the ambient recording";
        return new Assessment(true, observed, clipping, ambientRms, toneRms,
                riseLow, riseHigh, reason);
    }

    public static double rms(short[] data) {
        if (data == null || data.length == 0) return Double.NaN;
        double sum = 0;
        for (short value : data) sum += (double) value * value;
        return Math.sqrt(sum / data.length);
    }

    /**
     * Detrended, Hann-windowed single-frequency Fourier component, normalized
     * by window weight so different capture durations can be compared.
     */
    public static double spectralMagnitude(short[] data, int sampleRate, double frequency) {
        if (data == null || data.length < 2 || sampleRate <= 0
                || frequency <= 0 || frequency >= sampleRate / 2d) return Double.NaN;
        double mean = 0;
        for (short x : data) mean += x;
        mean /= data.length;
        double real = 0, imag = 0, weights = 0;
        for (int i = 0; i < data.length; i++) {
            double weight = 0.5d * (1d - Math.cos(2d * Math.PI * i / (data.length - 1)));
            double value = (data[i] - mean) * weight;
            double angle = -2d * Math.PI * frequency * i / sampleRate;
            real += value * Math.cos(angle);
            imag += value * Math.sin(angle);
            weights += weight;
        }
        return Math.hypot(real, imag) * 2d / weights;
    }

    private static int clippingCount(short[] data) {
        int count = 0;
        for (short value : data) {
            if (value <= -32700 || value >= 32700) count++;
        }
        return count;
    }
}
