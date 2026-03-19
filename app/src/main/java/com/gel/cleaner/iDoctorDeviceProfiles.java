// GDiolitsis Engine Lab (GEL) — Author & Developer
// iDoctorDeviceProfiles.java
// Policy-based fallback profiles (vendor / soc / path groups)
// NO estimation — NO fake — only known layouts

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
        public String batteryVoltagePath = null;

        public String cpuTempPath;
        public String gpuTempPath;

        public boolean requiresRoot = false;

    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    public static DeviceProfile detectProfile() {

        String manufacturer = safe(Build.MANUFACTURER);
        String brand        = safe(Build.BRAND);
        String hardware     = safe(Build.HARDWARE);
        String device       = safe(Build.DEVICE);

        // ------------------------------
        // Xiaomi policy
        // ------------------------------

        if (manufacturer.contains("xiaomi")
                || brand.contains("xiaomi")
                || brand.contains("redmi")
                || brand.contains("poco")) {

            return profileXiaomi();
        }

        // ------------------------------
        // Samsung policy
        // ------------------------------

        if (manufacturer.contains("samsung")) {

            return profileSamsung();
        }

        // ------------------------------
        // Qualcomm policy
        // ------------------------------

        if (hardware.contains("qcom")
                || hardware.contains("qualcomm")) {

            return profileQcom();
        }

        // ------------------------------
        // MediaTek policy
        // ------------------------------

        if (hardware.contains("mt")
                || hardware.contains("mediatek")) {

            return profileMTK();
        }

        // ------------------------------
        // no known profile
        // ------------------------------

        return null;
    }

    // ============================================================
    // XIAOMI PROFILE
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

    // NEW ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    p.batteryCyclePath =
            "/sys/class/power_supply/battery/cycle_count";

    p.batteryResistancePath = null;

    p.batteryVoltagePath =
            "/sys/class/power_supply/battery/voltage_now";

    p.batteryCurrentPath =
            "/sys/class/power_supply/battery/current_now";

    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    return p;
}

    // ============================================================
    // SAMSUNG PROFILE
    // ============================================================

    private static DeviceProfile profileSamsung() {

    DeviceProfile p = new DeviceProfile();

    p.name = "SAMSUNG";

    p.batteryChargeFullPath =
            "/sys/class/power_supply/battery/batt_full_capacity";

    p.batteryChargeNowPath =
            "/sys/class/power_supply/battery/charge_now";

    p.batteryTempPath =
            "/sys/class/power_supply/battery/batt_temp";

    // NEW

    p.batteryCyclePath =
            "/sys/class/power_supply/battery/batt_cycle_count";

    p.batteryResistancePath = null;

    p.batteryVoltagePath =
            "/sys/class/power_supply/battery/voltage_now";

    p.batteryCurrentPath =
            "/sys/class/power_supply/battery/current_now";

    return p;
}

    // ============================================================
    // QUALCOMM PROFILE
    // ============================================================

    private static DeviceProfile profileQcom() {

    DeviceProfile p = new DeviceProfile();

    p.name = "QCOM";

    p.batteryChargeFullPath =
            "/sys/class/power_supply/bms/charge_full";

    p.batteryChargeNowPath =
            "/sys/class/power_supply/bms/charge_now";

    p.batteryTempPath =
            "/sys/class/power_supply/battery/temp";

    // NEW

    p.batteryCyclePath =
            "/sys/class/power_supply/bms/cycle_count";

    p.batteryResistancePath = null;

    p.batteryVoltagePath =
            "/sys/class/power_supply/bms/voltage_now";

    p.batteryCurrentPath =
            "/sys/class/power_supply/bms/current_now";

    return p;
}

    // ============================================================
    // MEDIATEK PROFILE
    // ============================================================

    private static DeviceProfile profileMTK() {

    DeviceProfile p = new DeviceProfile();

    p.name = "MTK";

    // charge

    p.batteryChargeFullPath =
            "/sys/class/power_supply/battery/charge_full";

    p.batteryChargeNowPath =
            "/sys/class/power_supply/battery/charge_now";

    // temp

    p.batteryTempPath =
            "/sys/class/power_supply/battery/temp";

    // cycle

    p.batteryCyclePath =
            "/sys/class/power_supply/battery/cycle_count";

    // resistance (rare on MTK)

    p.batteryResistancePath = null;

    // voltage

    p.batteryVoltagePath =
            "/sys/class/power_supply/battery/voltage_now";

    // current

    p.batteryCurrentPath =
            "/sys/class/power_supply/battery/current_now";

    // thermal fallback disabled

    p.cpuTempPath = null;
    p.gpuTempPath = null;

    return p;
}

    // ============================================================
    // HELPERS
    // ============================================================
    
// ============================================================
// PROFILE RESOLVER
// ============================================================

public static DeviceProfile resolveProfile() {

    String man = android.os.Build.MANUFACTURER;
    String brand = android.os.Build.BRAND;
    String hw = android.os.Build.HARDWARE;
    String board = android.os.Build.BOARD;

    if (man == null) man = "";
    if (brand == null) brand = "";
    if (hw == null) hw = "";
    if (board == null) board = "";

    man = man.toLowerCase();
    brand = brand.toLowerCase();
    hw = hw.toLowerCase();
    board = board.toLowerCase();

    // Xiaomi / Redmi / Poco

    if (man.contains("xiaomi")
            || brand.contains("xiaomi")
            || brand.contains("redmi")
            || brand.contains("poco")) {

        return profileXiaomi();
    }

    // Samsung

    if (man.contains("samsung")
            || brand.contains("samsung")) {

        return profileSamsung();
    }

    // MediaTek

    if (hw.contains("mt")
            || hw.contains("mediatek")
            || board.contains("mt")) {

        return profileMTK();
    }

    // Qualcomm

    if (hw.contains("qcom")
            || hw.contains("msm")
            || hw.contains("sdm")) {

        return profileQcom();
    }

    return null;
}

    private static String safe(String s) {
        if (s == null) return "";
        return s.toLowerCase(Locale.US);
    }

}
