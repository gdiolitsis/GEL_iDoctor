package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Public-SDK battery and thermal facts, with a distinct observation for each field.
 * No root, shell, hidden API, OEM unit guesses, health scoring or private data collection.
 * Each observation has its own read timestamp, physical unit and provenance.
 */
public final class BatteryPublicCollector {
    private BatteryPublicCollector() {}

    private static long lastHeadroomReadNs = -1L;

    public static final class Snapshot {
        public final long collectedAtElapsedNanos;
        public final Map<String, BatteryEvidence> measurements;

        private Snapshot(long collectedAtElapsedNanos, Map<String, BatteryEvidence> data) {
            this.collectedAtElapsedNanos = collectedAtElapsedNanos;
            this.measurements = Collections.unmodifiableMap(new LinkedHashMap<>(data));
        }

        public BatteryEvidence get(String key) {
            BatteryEvidence reading = measurements.get(key);
            return reading != null ? reading
                    : BatteryEvidence.unavailable(key, "", "ANDROID_PUBLIC_API",
                            "Field not collected", collectedAtElapsedNanos);
        }

        public boolean has(String key) { return get(key).isObserved(); }
    }

    public static Snapshot collect(Context context) {
        Map<String, BatteryEvidence> data = new LinkedHashMap<>();
        long startedNs = SystemClock.elapsedRealtimeNanos();

        Intent battery = null;
        try {
            battery = context.registerReceiver(null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        } catch (RuntimeException ignored) {
            // Missing broadcast does not imply a defective battery.
        }
        if (battery != null) {
            final String origin = "ACTION_BATTERY_CHANGED";
            int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, Integer.MIN_VALUE);
            int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, Integer.MIN_VALUE);
            if (level >= 0 && scale > 0 && level <= scale) {
                observed(data, "soc_percent", "%", origin, 100.0d * level / scale);
            } else {
                unavailable(data, "soc_percent", "%", origin, "No valid level and scale");
            }
            int voltage = battery.getIntExtra(BatteryManager.EXTRA_VOLTAGE, Integer.MIN_VALUE);
            putInt(data, "voltage_mv", "mV", origin, voltage, voltage > 0);
            int temperature = battery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Integer.MIN_VALUE);
            if (temperature != Integer.MIN_VALUE && temperature >= -400 && temperature <= 1200) {
                observed(data, "temperature_c", "C", origin, temperature / 10.0d);
            } else {
                unavailable(data, "temperature_c", "C", origin, "No plausible temperature");
            }
            int health = battery.getIntExtra(BatteryManager.EXTRA_HEALTH, Integer.MIN_VALUE);
            putInt(data, "android_battery_health_enum", "enum", origin, health, health > 0);
            int status = battery.getIntExtra(BatteryManager.EXTRA_STATUS, Integer.MIN_VALUE);
            putInt(data, "battery_status_enum", "enum", origin, status, status > 0);
            int plugged = battery.getIntExtra(BatteryManager.EXTRA_PLUGGED, Integer.MIN_VALUE);
            putInt(data, "plugged_enum", "enum", origin, plugged, plugged >= 0);
            if (Build.VERSION.SDK_INT >= 34) {
                int cycles = battery.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, Integer.MIN_VALUE);
                putInt(data, "cycle_count", "count", origin, cycles, cycles >= 0);
                int chargeStatus = battery.getIntExtra(
                        BatteryManager.EXTRA_CHARGING_STATUS, Integer.MIN_VALUE);
                putInt(data, "charging_status_enum", "enum", origin, chargeStatus,
                        chargeStatus >= 0);
            } else {
                unavailable(data, "cycle_count", "count", origin, "Requires Android API 34");
                unavailable(data, "charging_status_enum", "enum", origin,
                        "Requires Android API 34");
            }
            if (Build.VERSION.SDK_INT >= 36) {
                int capacityLevel = battery.getIntExtra(
                        BatteryManager.EXTRA_CAPACITY_LEVEL, Integer.MIN_VALUE);
                putInt(data, "capacity_level_enum", "enum", origin, capacityLevel,
                        capacityLevel >= 0);
            } else {
                unavailable(data, "capacity_level_enum", "enum", origin,
                        "Requires Android API 36");
            }
        } else {
            for (String key : new String[]{"soc_percent", "voltage_mv", "temperature_c",
                    "android_battery_health_enum", "battery_status_enum", "plugged_enum",
                    "cycle_count", "charging_status_enum", "capacity_level_enum"}) {
                unavailable(data, key, "", "ACTION_BATTERY_CHANGED", "Broadcast unavailable");
            }
        }

        BatteryManager bm = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        if (bm != null) {
            readCurrent(data, bm, BatteryManager.BATTERY_PROPERTY_CURRENT_NOW,
                    "current_now_ma");
            readCurrent(data, bm, BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE,
                    "current_average_ma");
            try {
                long raw = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                double value = BatteryEvidence.microAmpHoursToMilliAmpHours(raw);
                putFinite(data, "remaining_charge_mah", "mAh", "BatteryManager.CHARGE_COUNTER",
                        value);
            } catch (RuntimeException ignored) {
                unavailable(data, "remaining_charge_mah", "mAh", "BatteryManager",
                        "Property unavailable");
            }
            try {
                long raw = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
                double value = BatteryEvidence.nanoWattHoursToMilliWattHours(raw);
                putFinite(data, "remaining_energy_mwh", "mWh", "BatteryManager.ENERGY_COUNTER",
                        value);
            } catch (RuntimeException ignored) {
                unavailable(data, "remaining_energy_mwh", "mWh", "BatteryManager",
                        "Property unavailable");
            }
            try {
                int soc = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                putInt(data, "property_soc_percent", "%", "BatteryManager.CAPACITY",
                        soc, soc >= 0 && soc <= 100);
            } catch (RuntimeException ignored) {
                unavailable(data, "property_soc_percent", "%", "BatteryManager",
                        "Property unavailable");
            }
            if (Build.VERSION.SDK_INT >= 28) {
                try {
                    long ms = bm.computeChargeTimeRemaining();
                    putFinite(data, "os_estimated_charge_time_ms", "ms",
                            "BatteryManager.computeChargeTimeRemaining",
                            ms >= 0 ? ms : Double.NaN);
                } catch (RuntimeException ignored) {
                    unavailable(data, "os_estimated_charge_time_ms", "ms", "BatteryManager",
                            "Prediction unavailable");
                }
            } else {
                unavailable(data, "os_estimated_charge_time_ms", "ms", "BatteryManager",
                        "Requires Android API 28");
            }
        } else {
            for (String key : new String[]{"current_now_ma", "current_average_ma",
                    "remaining_charge_mah", "remaining_energy_mwh",
                    "property_soc_percent", "os_estimated_charge_time_ms"}) {
                unavailable(data, key, "", "BatteryManager", "Service unavailable");
            }
        }

        PowerManager power = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (power != null) {
            observed(data, "battery_saver", "boolean", "PowerManager",
                    power.isPowerSaveMode() ? 1 : 0);
            if (Build.VERSION.SDK_INT >= 29) {
                try {
                    observed(data, "thermal_status_enum", "enum", "PowerManager",
                            power.getCurrentThermalStatus());
                } catch (RuntimeException ignored) {
                    unavailable(data, "thermal_status_enum", "enum", "PowerManager",
                            "Thermal status unavailable");
                }
            } else {
                unavailable(data, "thermal_status_enum", "enum", "PowerManager",
                        "Requires Android API 29");
            }
            if (Build.VERSION.SDK_INT >= 30) {
                readThermalHeadroomAtMost1Hz(power, data);
            } else {
                unavailable(data, "thermal_headroom", "fraction", "PowerManager",
                        "Requires Android API 30");
            }
            if (Build.VERSION.SDK_INT >= 31) {
                try {
                    Duration prediction = power.getBatteryDischargePrediction();
                    putFinite(data, "os_estimated_discharge_time_ms", "ms",
                            "PowerManager.getBatteryDischargePrediction",
                            prediction != null && !prediction.isNegative()
                                    ? prediction.toMillis() : Double.NaN);
                } catch (RuntimeException | ArithmeticException ignored) {
                    unavailable(data, "os_estimated_discharge_time_ms", "ms", "PowerManager",
                            "Prediction unavailable");
                }
            } else {
                unavailable(data, "os_estimated_discharge_time_ms", "ms", "PowerManager",
                        "Requires Android API 31");
            }
        } else {
            for (String key : new String[]{"battery_saver", "thermal_status_enum",
                    "thermal_headroom", "os_estimated_discharge_time_ms"}) {
                unavailable(data, key, "", "PowerManager", "Service unavailable");
            }
        }
        return new Snapshot(startedNs, data);
    }

    private static synchronized void readThermalHeadroomAtMost1Hz(
            PowerManager power, Map<String, BatteryEvidence> data) {
        long nowNs = SystemClock.elapsedRealtimeNanos();
        if (lastHeadroomReadNs >= 0 && nowNs >= lastHeadroomReadNs
                && nowNs - lastHeadroomReadNs < 1000000000L) {
            unavailable(data, "thermal_headroom", "fraction", "PowerManager",
                    "Rate-limited to one reading per second");
            return;
        }
        lastHeadroomReadNs = nowNs;
        try {
            float headroom = power.getThermalHeadroom(0);
            putFinite(data, "thermal_headroom", "fraction", "PowerManager", headroom);
        } catch (RuntimeException ignored) {
            unavailable(data, "thermal_headroom", "fraction", "PowerManager",
                    "Thermal headroom unavailable");
        }
    }

    private static void readCurrent(Map<String, BatteryEvidence> data, BatteryManager bm,
                                    int property, String key) {
        try {
            long raw = bm.getLongProperty(property);
            putFinite(data, key, "mA", "BatteryManager",
                    BatteryEvidence.microAmpsToMilliAmps(raw));
        } catch (RuntimeException ignored) {
            unavailable(data, key, "mA", "BatteryManager", "Property unavailable");
        }
    }

    private static void putInt(Map<String, BatteryEvidence> data, String key, String unit,
                               String source, int value, boolean valid) {
        if (valid) observed(data, key, unit, source, value);
        else unavailable(data, key, unit, source, "Missing/unsupported value");
    }

    private static void putFinite(Map<String, BatteryEvidence> data, String key, String unit,
                                  String source, double value) {
        if (Double.isFinite(value)) observed(data, key, unit, source, value);
        else unavailable(data, key, unit, source, "Missing/unsupported value");
    }

    private static void observed(Map<String, BatteryEvidence> data, String key,
                                 String unit, String source, double value) {
        data.put(key, BatteryEvidence.observed(key, unit, source,
                value, SystemClock.elapsedRealtimeNanos()));
    }

    private static void unavailable(Map<String, BatteryEvidence> data, String key,
                                    String unit, String source, String reason) {
        data.put(key, BatteryEvidence.unavailable(key, unit, source,
                reason, SystemClock.elapsedRealtimeNanos()));
    }
}
