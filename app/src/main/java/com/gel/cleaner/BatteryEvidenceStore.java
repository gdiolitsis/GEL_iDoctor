package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

/**
 * Atomic, versioned battery diagnostic evidence. Legacy lab14_* scores are
 * unverified and must never migrate as medical/scientific-style health percentages.
 * Records are app-local, omit user identifiers, and are invalidated across reboot.
 */
public final class BatteryEvidenceStore {
    private BatteryEvidenceStore() {}

    public static final int SCHEMA = 1;
    private static final String PREFS = "GEL_DIAGNOSTIC_EVIDENCE_V1";
    private static final String KEY_LAB14 = "lab14_latest";

    public static final class Record {
        public final String runId;
        public final long wallTimeMs;
        public final long elapsedTimeMs;
        public final boolean completed;
        public final boolean drainValid;
        public final boolean thermalLimited;
        public final boolean hasCurrent;
        public final boolean hasVoltage;
        public final boolean hasChargeCounter;
        public final int observedSignalCount;
        public final JSONObject evidence;

        private Record(JSONObject raw) throws JSONException {
            if (raw.getInt("schema") != SCHEMA) {
                throw new JSONException("Unsupported diagnostic schema");
            }
            runId = raw.getString("run_id");
            wallTimeMs = raw.getLong("wall_time_ms");
            elapsedTimeMs = raw.getLong("elapsed_time_ms");
            completed = raw.getBoolean("completed");
            drainValid = raw.getBoolean("drain_valid");
            thermalLimited = raw.getBoolean("thermal_limited");
            JSONObject signals = raw.getJSONObject("signals");
            evidence = signals;
            hasCurrent = isObserved(signals, "current_now_ma");
            hasVoltage = isObserved(signals, "voltage_mv");
            hasChargeCounter = isObserved(signals, "remaining_charge_mah");
            observedSignalCount = raw.getInt("observed_signal_count");
        }

        public boolean isFresh(long maxAgeMs) {
            long nowElapsed = SystemClock.elapsedRealtime();
            long nowWall = System.currentTimeMillis();
            long elapsedAge = nowElapsed - elapsedTimeMs;
            long wallAge = nowWall - wallTimeMs;
            // Same boot, no future timestamps or clocks jumping out of bounds.
            return maxAgeMs > 0 && elapsedAge >= 0 && elapsedAge <= maxAgeMs
                    && wallAge >= 0 && wallAge <= maxAgeMs;
        }

        public boolean isCompletedValidRun(long maxAgeMs) {
            return completed && drainValid && !thermalLimited && isFresh(maxAgeMs);
        }

        /** SoH remains unknown: public SDK lacks verified learned/design capacity. */
        public double validatedSoHPercent() { return Double.NaN; }

        private static boolean isObserved(JSONObject data, String key) {
            JSONObject field = data.optJSONObject(key);
            return field != null && "OBSERVED".equals(field.optString("state"));
        }
    }

    /** Write on every completion/cancellation so old passing runs never masquerade as current. */
    public static boolean recordLab14(Context context, boolean completed,
                                      boolean drainValid, boolean thermalLimited) {
        final long elapsed = SystemClock.elapsedRealtime();
        final long wallTime = System.currentTimeMillis();
        try {
            BatteryPublicCollector.Snapshot readings = BatteryPublicCollector.collect(context);
            JSONObject root = new JSONObject();
            root.put("schema", SCHEMA);
            root.put("run_id", UUID.randomUUID().toString());
            root.put("wall_time_ms", wallTime);
            root.put("elapsed_time_ms", elapsed);
            root.put("android_api", Build.VERSION.SDK_INT);
            root.put("completed", completed);
            root.put("drain_valid", drainValid);
            root.put("thermal_limited", thermalLimited);
            root.put("conclusion", "CAPACITY_SOH_NOT_DETERMINED");

            JSONObject fields = new JSONObject();
            int count = 0;
            for (Map.Entry<String, BatteryEvidence> entry : readings.measurements.entrySet()) {
                BatteryEvidence reading = entry.getValue();
                JSONObject field = new JSONObject();
                field.put("state", reading.state.name());
                field.put("unit", reading.unit);
                field.put("source", reading.source);
                field.put("elapsed_ns", reading.elapsedRealtimeNanos);
                field.put("reason", reading.reason);
                if (reading.isObserved()) {
                    field.put("value", reading.value);
                    count++;
                }
                fields.put(entry.getKey(), field);
            }
            root.put("observed_signal_count", count);
            root.put("signals", fields);
            return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                    .putString(KEY_LAB14, root.toString()).commit();
        } catch (JSONException | RuntimeException e) {
            // Corrupt records never become passing records.
            clearLab14(context);
            return false;
        }
    }

    public static Record readLatestLab14(Context context) {
        try {
            String raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getString(KEY_LAB14, null);
            if (raw == null || raw.trim().isEmpty()) return null;
            return new Record(new JSONObject(raw));
        } catch (JSONException | RuntimeException ignored) {
            return null;
        }
    }

    public static void clearLab14(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().remove(KEY_LAB14).commit();
    }
}
