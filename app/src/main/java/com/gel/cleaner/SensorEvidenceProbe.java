package com.gel.cleaner;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Five-second, bounded, permission-free event observation of common sensors.
 * Enumeration or a stream of constant samples never certifies hardware health.
 */
public final class SensorEvidenceProbe {
    private SensorEvidenceProbe() {}

    public interface Callback {
        void onFinished(List<Result> results);
    }

    public static final class Result {
        public final String name;
        public final boolean advertised;
        public final boolean listenerRegistered;
        public final int events;
        public final int invalidEvents;
        public final boolean monotonicTimestamps;
        public final boolean observedVariation;
        public final String limitation;

        private Result(String name, boolean advertised, boolean registered,
                       int events, int invalidEvents, boolean monotonic,
                       boolean varied, String limitation) {
            this.name = name;
            this.advertised = advertised;
            this.listenerRegistered = registered;
            this.events = events;
            this.invalidEvents = invalidEvents;
            this.monotonicTimestamps = monotonic;
            this.observedVariation = varied;
            this.limitation = limitation;
        }
    }

    private static final class Sample {
        final String name;
        final Sensor sensor;
        boolean registered;
        int events;
        int bad;
        boolean monotonic = true;
        long previousTimestamp = -1L;
        float min = Float.POSITIVE_INFINITY;
        float max = Float.NEGATIVE_INFINITY;

        Sample(String name, Sensor sensor) {
            this.name = name;
            this.sensor = sensor;
        }

        Result result() {
            boolean varied = events > 1 && Float.isFinite(min) && Float.isFinite(max)
                    && (max - min) >= Math.max(0.02f, sensor == null ? 0f
                            : sensor.getResolution() * 2f);
            return new Result(name, sensor != null, registered,
                    events, bad, monotonic, varied,
                    sensor == null ? "Not advertised" :
                            !registered ? "Registration refused" :
                            events == 0 ? "No event during sample interval" :
                            "Event stream observed, not a calibrated functional diagnosis");
        }
    }

    /** Asynchronous and read-only. Always unregisters listeners after 5 seconds. */
    public static void sample(Context context, Callback callback) {
        if (callback == null) throw new IllegalArgumentException("callback required");
        SensorManager manager = (SensorManager) context.getSystemService(
                Context.SENSOR_SERVICE);
        Handler main = new Handler(Looper.getMainLooper());
        if (manager == null) {
            main.post(() -> callback.onFinished(new ArrayList<>()));
            return;
        }

        HandlerThread worker = new HandlerThread("GEL-Lab9-Sensors");
        worker.start();
        Handler handler = new Handler(worker.getLooper());
        Map<Integer, Sample> samples = new LinkedHashMap<>();
        int[] types = {Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE,
                Sensor.TYPE_LIGHT, Sensor.TYPE_PROXIMITY};
        String[] names = {"Accelerometer", "Gyroscope", "Ambient light", "Proximity"};

        for (int i = 0; i < types.length; i++) {
            Sensor sensor = null;
            try {
                sensor = manager.getDefaultSensor(types[i]);
            } catch (RuntimeException ignored) {}
            samples.put(types[i], new Sample(names[i], sensor));
        }

        SensorEventListener listener = new SensorEventListener() {
            @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Accuracy callback is metadata, not hardware certification.
            }

            @Override public void onSensorChanged(SensorEvent event) {
                if (event == null || event.sensor == null) return;
                Sample sample = samples.get(event.sensor.getType());
                if (sample == null || !sample.registered) return;
                if (event.values == null || event.values.length == 0 ||
                        !Float.isFinite(event.values[0]) || event.timestamp <= 0L) {
                    sample.bad++;
                    return;
                }
                if (sample.previousTimestamp >= 0
                        && event.timestamp <= sample.previousTimestamp) {
                    sample.monotonic = false;
                }
                sample.previousTimestamp = event.timestamp;
                sample.events++;
                sample.min = Math.min(sample.min, event.values[0]);
                sample.max = Math.max(sample.max, event.values[0]);
            }
        };

        for (Sample sample : samples.values()) {
            if (sample.sensor == null) continue;
            try {
                sample.registered = manager.registerListener(
                        listener, sample.sensor, SensorManager.SENSOR_DELAY_NORMAL, handler);
            } catch (RuntimeException ignored) {
                sample.registered = false;
            }
        }

        handler.postDelayed(() -> {
            try {
                manager.unregisterListener(listener);
            } catch (RuntimeException ignored) {}
            List<Result> results = new ArrayList<>();
            for (Sample sample : samples.values()) results.add(sample.result());
            worker.quitSafely();
            main.post(() -> callback.onFinished(results));
        }, 5000L);
    }
}
