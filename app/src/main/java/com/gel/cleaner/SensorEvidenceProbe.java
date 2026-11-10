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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Five-second, bounded, permission-free event observation of common sensors.
 * Enumeration or a stream of constant samples never certifies hardware health.
 */
public final class SensorEvidenceProbe {
    private SensorEvidenceProbe() {}

    public interface Callback {
        void onFinished(List<Result> results);
    }

    /** Cancel active observation and suppress callbacks after pause/destroy. */
    public static final class Handle {
        private final AtomicBoolean cancelled = new AtomicBoolean(false);
        private volatile Runnable close;
        private Handle() {}

        public void cancel() {
            if (cancelled.compareAndSet(false, true)) {
                Runnable operation = close;
                if (operation != null) operation.run();
            }
        }
    }

    public static final class Result {
        public final String name;
        public final boolean advertised;
        public final boolean listenerRegistered;
        public final int events;
        public final int invalidEvents;
        public final boolean monotonicTimestamps;
        public final boolean observedVariation;
        public final String variedAxes;
        public final String limitation;

        private Result(String name, boolean advertised, boolean registered,
                       int events, int invalidEvents, boolean monotonic,
                       boolean varied, String axes, String limitation) {
            this.name = name;
            this.advertised = advertised;
            this.listenerRegistered = registered;
            this.events = events;
            this.invalidEvents = invalidEvents;
            this.monotonicTimestamps = monotonic;
            this.observedVariation = varied;
            this.variedAxes = axes;
            this.limitation = limitation;
        }
    }

    private static final class Sample {
        final String name;
        final Sensor sensor;
        final SensorAxisEvidence evidence;
        volatile boolean registered;

        Sample(String name, Sensor sensor, int requiredAxes) {
            this.name = name;
            this.sensor = sensor;
            this.evidence = new SensorAxisEvidence(requiredAxes);
        }

        Result result() {
            int valid = evidence.validEvents();
            float resolution = sensor == null ? 0f : sensor.getResolution();
            String variedAxes = evidence.variedAxes(resolution);
            return new Result(name, sensor != null, registered,
                    valid, evidence.invalidEvents(), evidence.timestampsMonotonic(),
                    !variedAxes.isEmpty(), variedAxes,
                    sensor == null ? "Not advertised" :
                            !registered ? "Registration refused" :
                            valid == 0 ? "No valid events during sample interval" :
                            "Only sensor-event variation observed; no calibration or hardware diagnosis");
        }
    }

    /** Legacy fire-and-forget entrypoint retained for compatibility. */
    public static void sample(Context context, Callback callback) {
        sampleCancelable(context, callback);
    }

    /** Cancellable five-second observation; never invokes callback after cancel. */
    public static Handle sampleCancelable(Context context, Callback callback) {
        if (callback == null) throw new IllegalArgumentException("callback required");
        Handle handle = new Handle();
        SensorManager manager = (SensorManager) context.getSystemService(
                Context.SENSOR_SERVICE);
        Handler main = new Handler(Looper.getMainLooper());
        if (manager == null) {
            main.post(() -> {
                if (!handle.cancelled.get()) callback.onFinished(new ArrayList<>());
            });
            return handle;
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
            int requiredAxes = types[i] == Sensor.TYPE_ACCELEROMETER
                    || types[i] == Sensor.TYPE_GYROSCOPE ? 3 : 1;
            samples.put(types[i], new Sample(names[i], sensor, requiredAxes));
        }

        SensorEventListener listener = new SensorEventListener() {
            @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Accuracy callback is metadata, not hardware certification.
            }

            @Override public void onSensorChanged(SensorEvent event) {
                if (event == null || event.sensor == null) return;
                Sample sample = samples.get(event.sensor.getType());
                if (sample == null || !sample.registered) return;
                // All three accelerometer/gyroscope axes must be valid. Light
                // and proximity expose one value. No physical health verdict.
                sample.evidence.observe(event.values, event.timestamp);
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

        handle.close = () -> handler.post(() -> {
            try { manager.unregisterListener(listener); }
            catch (RuntimeException ignored) {}
            worker.quitSafely();
        });
        handler.postDelayed(() -> {
            try { manager.unregisterListener(listener); }
            catch (RuntimeException ignored) {}
            List<Result> results = new ArrayList<>();
            if (!handle.cancelled.get()) {
                for (Sample sample : samples.values()) results.add(sample.result());
            }
            worker.quitSafely();
            main.post(() -> {
                if (!handle.cancelled.get()) callback.onFinished(results);
            });
        }, 5000L);
        return handle;
    }
}
