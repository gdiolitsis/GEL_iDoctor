package com.gel.cleaner;

import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.SystemClock;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Bounded voice-communication input -> voice-call output loopback.
 * No audio is persisted or transmitted. A successful loop verifies only that
 * samples were copied through Android's selected paths, not a specific mic,
 * earpiece, carrier call or acoustically calibrated result.
 */
public final class VoiceLoopEvidenceProbe {
    private VoiceLoopEvidenceProbe() {}
    private static final int RATE = 16000;
    private static final long DURATION_MS = 5000;

    public static final class Result {
        public final boolean attempted;
        public final long elapsedMs;
        public final int bytesCaptured;
        public final int bytesWritten;
        public final int peakAbsSample;
        public final int routedInputType;
        public final int routedOutputType;
        public final String reason;

        private Result(boolean attempted, long elapsedMs, int captured,
                       int written, int peak, int inputType, int outputType, String reason) {
            this.attempted = attempted;
            this.elapsedMs = elapsedMs;
            this.bytesCaptured = captured;
            this.bytesWritten = written;
            this.peakAbsSample = peak;
            this.routedInputType = inputType;
            this.routedOutputType = outputType;
            this.reason = reason;
        }
    }

    public static Result run(AtomicBoolean cancelled) {
        if (cancelled == null) return unavailable("Cancellation flag missing");
        AudioRecord recorder = null;
        AudioTrack track = null;
        long start = 0L;
        int captured = 0, written = 0, peak = 0;
        int inputType = -1, outputType = -1;
        try {
            int inMin = AudioRecord.getMinBufferSize(RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            int outMin = AudioTrack.getMinBufferSize(RATE,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            if (inMin <= 0 || outMin <= 0)
                return unavailable("Unsupported capture/playback configuration");

            int size = Math.max(4096, Math.max(inMin, outMin));
            recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                    RATE, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, size);
            track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, RATE,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    size, AudioTrack.MODE_STREAM);
            if (recorder.getState() != AudioRecord.STATE_INITIALIZED
                    || track.getState() != AudioTrack.STATE_INITIALIZED) {
                return unavailable("Audio input/output initialization failed");
            }

            recorder.startRecording();
            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                return unavailable("Microphone recording not started");
            }
            track.play();
            if (track.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
                return unavailable("Voice-call playback did not start");
            }

            // Read the actual OS-selected route while both streams are active;
            // null is UNKNOWN, never proof of a missing physical component.
            AudioDeviceInfo input = recorder.getRoutedDevice();
            AudioDeviceInfo output = track.getRoutedDevice();
            if (input != null) inputType = input.getType();
            if (output != null) outputType = output.getType();

            byte[] buffer = new byte[size];
            start = SystemClock.elapsedRealtime();
            while (!cancelled.get() && SystemClock.elapsedRealtime() - start < DURATION_MS) {
                int n = recorder.read(buffer, 0, buffer.length);
                if (n <= 0) break;
                captured += n;
                // PCM16 little-endian; samples are observations, not mic identity.
                for (int i = 0; i + 1 < n; i += 2) {
                    int sample = (short) ((buffer[i] & 0xff)
                            | (buffer[i + 1] << 8));
                    peak = Math.max(peak, Math.abs(sample));
                }
                int offset = 0;
                while (offset < n && !cancelled.get()) {
                    int count = track.write(buffer, offset, n - offset);
                    if (count <= 0) break;
                    offset += count;
                }
                written += offset;
                if (offset != n) break;
            }
            long elapsed = SystemClock.elapsedRealtime() - start;
            if (inputType < 0) {
                AudioDeviceInfo input = recorder.getRoutedDevice();
                if (input != null) inputType = input.getType();
            }
            if (outputType < 0) {
                AudioDeviceInfo output = track.getRoutedDevice();
                if (output != null) outputType = output.getType();
            }
            boolean complete = !cancelled.get()
                    && AudioChallengeEvidence.loopAttempted(elapsed, captured, written, peak);
            return new Result(complete, elapsed, captured, written, peak,
                    inputType, outputType, cancelled.get() ? "User cancelled voice loop"
                    : complete ? "Android audio sample loop completed; physical route unverified"
                    : "Insufficient microphone-to-output samples, duration or nonzero signal");
        } catch (SecurityException | IllegalStateException | IllegalArgumentException e) {
            return new Result(false, start == 0 ? 0 : SystemClock.elapsedRealtime()-start,
                    captured, written, peak, inputType, outputType,
                    "Voice loop unavailable: "
                            + e.getClass().getSimpleName());
        } finally {
            if (recorder != null) {
                try { recorder.stop(); } catch (RuntimeException ignored) {}
                try { recorder.release(); } catch (RuntimeException ignored) {}
            }
            if (track != null) {
                try { track.stop(); } catch (RuntimeException ignored) {}
                try { track.release(); } catch (RuntimeException ignored) {}
            }
        }
    }

    private static Result unavailable(String reason) {
        return new Result(false, 0, 0, 0, 0, -1, -1, reason);
    }
}
