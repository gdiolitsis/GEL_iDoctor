package com.gel.cleaner;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.ToneGenerator;

/**
 * Short opt-in audio challenge: captures a pre-tone ambient baseline and the
 * microphone response DURING controlled tone playback. Audio samples never
 * leave the method and are not written to disk or uploaded.
 */
public final class AcousticStimulusProbe {
    private AcousticStimulusProbe() {}

    private static final int SAMPLE_RATE = 16000;
    private static final int BASELINE_SAMPLES = 4000; // 250 ms
    private static final int TONE_SAMPLES = 12800; // 800 ms
    private static final int PLAYBACK_VOLUME = 60;

    private static final class Stimulus {
        final int androidTone;
        final int lowHz;
        final int highHz;
        Stimulus(int tone, int low, int high) {
            androidTone = tone;
            lowHz = low;
            highHz = high;
        }
    }

    public static final class Result {
        public final boolean attempted;
        public final boolean responseObserved;
        public final int testedTones;
        public final int observedTones;
        public final boolean clipped;
        public final String inputRoute;
        public final String reason;
        public final double weakestLowRiseDb;
        public final double weakestHighRiseDb;
        // Raw PCM counts, not calibrated sound pressure level or speaker quality.
        public final double ambientMicRms;
        public final double challengeMicRms;
        public final double challengeMicPeak;

        private Result(boolean attempted, boolean observed, int tests, int positives,
                       boolean clipped, String route, String reason, double lo, double hi,
                       double ambientRms, double challengeRms, double challengePeak) {
            this.attempted = attempted;
            this.responseObserved = observed;
            this.testedTones = tests;
            this.observedTones = positives;
            this.clipped = clipped;
            this.inputRoute = route;
            this.reason = reason;
            this.weakestLowRiseDb = lo;
            this.weakestHighRiseDb = hi;
            this.ambientMicRms = ambientRms;
            this.challengeMicRms = challengeRms;
            this.challengeMicPeak = challengePeak;
        }
    }

    public static Result run(Context context, boolean multiTone) {
        if (context == null) return unavailable("Context unavailable");
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (manager == null) return unavailable("AudioManager unavailable");
        if (manager.getStreamVolume(AudioManager.STREAM_MUSIC) <= 0) {
            return unavailable("Media stream muted; no acoustic challenge");
        }
        // Available external output devices are not proof of routing. When present,
        // this built-in-speaker challenge cannot isolate the emitter. Fail closed:
        // request a re-test after disconnection, not a speaker hardware verdict.
        String outputBlocker = externalOutputBlocker(manager);
        if (outputBlocker != null) return unavailable(outputBlocker);

        final int minBuffer = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (minBuffer <= 0) return unavailable("Microphone capture format unsupported");
        final Stimulus[] tones = multiTone
                ? new Stimulus[]{
                    new Stimulus(ToneGenerator.TONE_DTMF_1, 697, 1209),
                    new Stimulus(ToneGenerator.TONE_DTMF_3, 697, 1477),
                    new Stimulus(ToneGenerator.TONE_DTMF_6, 770, 1477),
                    new Stimulus(ToneGenerator.TONE_DTMF_9, 852, 1477)}
                : new Stimulus[]{new Stimulus(ToneGenerator.TONE_DTMF_1, 697, 1209)};

        AudioRecord record = null;
        ToneGenerator player = null;
        int tested = 0, observed = 0;
        boolean clipped = false;
        double minLow = Double.POSITIVE_INFINITY, minHigh = Double.POSITIVE_INFINITY;
        double ambientMicRms = Double.NaN, challengeMicRms = Double.NaN;
        double challengeMicPeak = Double.NaN;
        String route = "Unknown microphone route";
        String failure = "";
        try {
            record = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, Math.max(minBuffer, TONE_SAMPLES * 2));
            if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                return unavailable("Microphone AudioRecord not initialized");
            }
            player = new ToneGenerator(AudioManager.STREAM_MUSIC, PLAYBACK_VOLUME);
            record.startRecording();
            if (record.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                return unavailable("Microphone was not recording");
            }
            AudioDeviceInfo device = record.getRoutedDevice();
            if (device != null) route = "Android audio input device type " + device.getType();

            for (Stimulus stimulus : tones) {
                short[] ambient = readSamples(record, BASELINE_SAMPLES);
                if (ambient == null) {
                    failure = "Ambient microphone capture incomplete";
                    break;
                }
                if (!player.startTone(stimulus.androidTone, 850)) {
                    failure = "Tone playback request rejected";
                    break;
                }
                short[] during;
                try {
                    // Must capture WHILE generated tone is playing.
                    during = readSamples(record, TONE_SAMPLES);
                } finally {
                    player.stopTone();
                }
                if (during == null) {
                    failure = "Stimulus microphone capture incomplete";
                    break;
                }
                AcousticEvidenceMath.Assessment score = AcousticEvidenceMath.evaluate(
                        ambient, during, SAMPLE_RATE, stimulus.lowHz, stimulus.highHz);
                if (!score.valid) {
                    failure = score.reason;
                    break;
                }
                tested++;
                ambientMicRms = score.ambientRms;
                challengeMicRms = score.challengeRms;
                double peak = 0d;
                for (short sample : during) peak = Math.max(peak, Math.abs((int) sample));
                challengeMicPeak = Double.isNaN(challengeMicPeak)
                        ? peak : Math.max(challengeMicPeak, peak);
                if (score.responseObserved) observed++;
                clipped |= score.clipping;
                minLow = Math.min(minLow, score.lowFrequencyRiseDb);
                minHigh = Math.min(minHigh, score.highFrequencyRiseDb);
            }
        } catch (RuntimeException e) {
            failure = "Audio route, permission or capture unavailable: "
                    + e.getClass().getSimpleName();
        } finally {
            if (record != null) {
                try { record.stop(); } catch (RuntimeException ignored) {}
                record.release();
            }
            if (player != null) player.release();
        }
        boolean complete = failure.isEmpty() && tested == tones.length;
        boolean positive = complete && observed == tones.length;
        String reason = !failure.isEmpty() ? failure
                : positive ? "Time-aligned generated frequencies observed at microphone"
                : clipped ? "Microphone clipped; repeat at lower level"
                : "No repeatable synchronized tone above microphone baseline";
        return new Result(complete, positive, tested, observed, clipped, route,
                reason, tested > 0 ? minLow : Double.NaN,
                tested > 0 ? minHigh : Double.NaN,
                ambientMicRms, challengeMicRms, challengeMicPeak);
    }

    /**
     * A connected external output is not necessarily the currently routed output.
     * Nevertheless, we cannot attribute a microphone response to the internal
     * loudspeaker while an alternate output is available to the media stream.
     * This is a conservative preflight, not a physical output-route measurement.
     */
    private static String externalOutputBlocker(AudioManager manager) {
        final AudioDeviceInfo[] outputs;
        try {
            outputs = manager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        } catch (RuntimeException e) {
            return "Cannot inspect available audio outputs: " + e.getClass().getSimpleName();
        }
        if (outputs == null || outputs.length == 0) {
            return "Audio output devices unavailable; built-in speaker route unverified";
        }
        for (AudioDeviceInfo device : outputs) {
            if (device == null) continue;
            switch (device.getType()) {
                case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                case AudioDeviceInfo.TYPE_LINE_ANALOG:
                case AudioDeviceInfo.TYPE_LINE_DIGITAL:
                case AudioDeviceInfo.TYPE_USB_DEVICE:
                case AudioDeviceInfo.TYPE_USB_ACCESSORY:
                case AudioDeviceInfo.TYPE_USB_HEADSET:
                case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                case AudioDeviceInfo.TYPE_BLE_HEADSET:
                case AudioDeviceInfo.TYPE_BLE_SPEAKER:
                case AudioDeviceInfo.TYPE_HDMI:
                case AudioDeviceInfo.TYPE_HDMI_ARC:
                case AudioDeviceInfo.TYPE_HDMI_EARC:
                case AudioDeviceInfo.TYPE_HEARING_AID:
                    return "External audio output available (type " + device.getType()
                            + "); disconnect external output and rerun speaker test";
                default:
                    break;
            }
        }
        return null;
    }

    private static short[] readSamples(AudioRecord record, int samples) {
        short[] buffer = new short[samples];
        int offset = 0, emptyReads = 0;
        while (offset < samples && !Thread.currentThread().isInterrupted()) {
            int n = record.read(buffer, offset, samples - offset, AudioRecord.READ_BLOCKING);
            if (n < 0) return null;
            if (n == 0) {
                if (++emptyReads > 3) return null;
                continue;
            }
            emptyReads = 0;
            offset += n;
        }
        return offset == samples ? buffer : null;
    }

    private static Result unavailable(String reason) {
        return new Result(false, false, 0, 0, false, "Unknown",
                reason, Double.NaN, Double.NaN,
                Double.NaN, Double.NaN, Double.NaN);
    }
}
