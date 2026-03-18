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

        p.cpuTempPath =
                "/sys/class/thermal/thermal_zone0/temp";

        p.gpuTempPath =
                "/sys/class/thermal/thermal_zone1/temp";

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

        p.cpuTempPath =
                "/sys/class/thermal/thermal_zone0/temp";

        p.gpuTempPath =
                "/sys/class/thermal/thermal_zone1/temp";

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

        p.cpuTempPath =
                "/sys/class/thermal/thermal_zone0/temp";

        p.gpuTempPath =
                "/sys/class/kgsl/kgsl-3d0/temp";

        return p;
    }

    // ============================================================
    // MEDIATEK PROFILE
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
        if (s == null) return "";
        return s.toLowerCase(Locale.US);
    }

}
