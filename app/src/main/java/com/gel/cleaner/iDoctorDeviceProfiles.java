package com.gel.cleaner;

import android.os.Build;
import java.util.Locale;

public class iDoctorDeviceProfiles {

    // ============================================================
    // PROFILE OBJECT
    // ============================================================

    public static class DeviceProfile {

        public String name = "GENERIC";

        public String batteryChargeFullPath;
        public String batteryChargeNowPath;
        public String batteryTempPath;
        public String batteryCyclePath;
        public String batteryResistancePath;

        public String batteryVoltagePath;
        public String batteryCurrentPath;

        public String cpuTempPath;
        public String gpuTempPath;

        public boolean requiresRoot = false;
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    public static DeviceProfile detectProfile() {

    String manufacturer = safe(Build.MANUFACTURER);
    String brand = safe(Build.BRAND);
    String hardware = safe(Build.HARDWARE);

    if (manufacturer.contains("xiaomi")
            || brand.contains("redmi")
            || brand.contains("poco")) {

        return profileXiaomi();
    }

    if (manufacturer.contains("samsung")) {

        return profileSamsung();
    }

    if (hardware.contains("qcom")
            || hardware.contains("qualcomm")
            || hardware.contains("msm")
            || hardware.contains("sdm")) {

        return profileQcom();
    }

    if (hardware.contains("mt")
            || hardware.contains("mediatek")) {

        return profileMTK();
    }

    return profileGeneric();   // ✅ ΜΟΝΟ ΕΔΩ
}

    private static DeviceProfile profileGeneric() {

    DeviceProfile p = new DeviceProfile();

    p.name = "GENERIC";

    p.batteryChargeFullPath =
            "/sys/class/power_supply/battery/charge_full";

    p.batteryChargeNowPath =
            "/sys/class/power_supply/battery/charge_now";

    p.batteryTempPath =
            "/sys/class/power_supply/battery/temp";

    p.batteryVoltagePath =
            "/sys/class/power_supply/battery/voltage_now";

    p.batteryCurrentPath =
            "/sys/class/power_supply/battery/current_now";

    p.batteryCyclePath =
            "/sys/class/power_supply/battery/cycle_count";

    p.cpuTempPath =
            "/sys/class/thermal/thermal_zone0/temp";

    p.gpuTempPath =
            "/sys/class/thermal/thermal_zone1/temp";

    return p;
}

    // ============================================================
    // XIAOMI
    // ============================================================

    private static DeviceProfile profileXiaomi() {

        DeviceProfile p = new DeviceProfile();

        p.name = "XIAOMI";

        p.batteryChargeFullPath =
                "/sys/class/power_supply/battery/charge_full";

        p.batteryChargeNowPath =
                "/sys/class/power_supply/battery/charge_now";

        p.batteryTempPath =
                "/sys/class/power_supply/battery/temp";

        p.batteryCyclePath =
                "/sys/class/power_supply/battery/cycle_count";

        p.batteryResistancePath = null;

        // SAFE DEFAULTS

        p.batteryVoltagePath =
                "/sys/class/power_supply/battery/voltage_now";

        p.batteryCurrentPath =
                "/sys/class/power_supply/battery/current_now";

        // thermal fallback

        p.cpuTempPath =
                "/sys/class/thermal/thermal_zone0/temp";

        p.gpuTempPath =
                "/sys/class/thermal/thermal_zone1/temp";

        return p;
    }

    // ============================================================
    // SAMSUNG
    // ============================================================

    private static DeviceProfile profileSamsung() {

        DeviceProfile p = new DeviceProfile();

        p.name = "SAMSUNG";

        p.batteryChargeFullPath =
        "/sys/class/power_supply/battery/charge_full";

        p.batteryChargeNowPath =
                "/sys/class/power_supply/battery/charge_now";

        p.batteryTempPath =
                "/sys/class/power_supply/battery/batt_temp";

        p.batteryCyclePath =
                "/sys/class/power_supply/battery/batt_cycle_count";

        p.batteryResistancePath = null;

        p.batteryVoltagePath =
                "/sys/class/power_supply/battery/voltage_now";

        p.batteryCurrentPath =
                "/sys/class/power_supply/battery/current_now";

        p.cpuTempPath =
                "/sys/class/thermal/thermal_zone0/temp";

        p.gpuTempPath =
                "/sys/class/thermal/thermal_zone1/temp";

        return p;
    }

    // ============================================================
    // QCOM
    // ============================================================

    private static DeviceProfile profileQcom() {

        DeviceProfile p = new DeviceProfile();

        p.name = "QCOM";

        p.batteryChargeFullPath =
                "/sys/class/power_supply/battery/charge_full";

        p.batteryChargeNowPath =
                "/sys/class/power_supply/battery/charge_now";

        p.batteryTempPath =
                "/sys/class/power_supply/battery/temp";

        p.batteryCyclePath =
                "/sys/class/power_supply/battery/cycle_count";

        p.batteryResistancePath = null;

        // IMPORTANT:
        // do NOT use only bms

        p.batteryVoltagePath =
                "/sys/class/power_supply/battery/voltage_now";

        p.batteryCurrentPath =
                "/sys/class/power_supply/battery/current_now";

        p.cpuTempPath =
                "/sys/class/thermal/thermal_zone0/temp";

        p.gpuTempPath =
                "/sys/class/thermal/thermal_zone1/temp";

        return p;
    }

    // ============================================================
    // MEDIATEK
    // ============================================================

    private static DeviceProfile profileMTK() {

        DeviceProfile p = new DeviceProfile();

        p.name = "MTK";

        p.batteryChargeFullPath =
                "/sys/class/power_supply/battery/charge_full";

        p.batteryChargeNowPath =
                "/sys/class/power_supply/battery/charge_now";

        p.batteryTempPath =
                "/sys/class/power_supply/battery/temp";

        p.batteryCyclePath =
                "/sys/class/power_supply/battery/cycle_count";

        p.batteryResistancePath = null;

        p.batteryVoltagePath =
                "/sys/class/power_supply/battery/voltage_now";

        p.batteryCurrentPath =
                "/sys/class/power_supply/battery/current_now";

        p.cpuTempPath =
                "/sys/class/thermal/thermal_zone0/temp";

        p.gpuTempPath =
                "/sys/class/thermal/thermal_zone1/temp";

        return p;
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private static String safe(String s) {

        if (s == null)
            return "";

        return s.toLowerCase(Locale.US);
    }
}
