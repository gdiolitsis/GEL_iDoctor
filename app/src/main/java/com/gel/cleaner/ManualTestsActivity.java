package com.gel.cleaner;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class iDoctorEngine {
	private long lastInternalResistanceMilliOhm = -1;

public void setInternalResistanceMilliOhm(long v) {
    lastInternalResistanceMilliOhm = v;
}

public long getInternalResistanceMilliOhm() {
    return lastInternalResistanceMilliOhm;
}

    private static iDoctorEngine instance;

    private final Context ctx;

    private iDoctorDeviceProfiles.DeviceProfile profile;


    // -------------------------------------------------
    // SINGLETON GET
    // -------------------------------------------------

    public static iDoctorEngine get(Context context) {

        if (instance == null) {

            instance =
                    new iDoctorEngine(
                            context.getApplicationContext()
                    );
        }

        return instance;
    }


    // -------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------

    public iDoctorEngine(Context context) {

        this.ctx = context.getApplicationContext();

        profile =
                iDoctorDeviceProfiles.detectProfile();
    }

    // ============================================================
    // GLOBAL SNAPSHOT
    // ============================================================
    public static final class FullSnapshot {
        public boolean rooted;
        public BatterySnapshot battery = new BatterySnapshot();
        public ThermalSnapshot thermal = new ThermalSnapshot();
        public CpuSnapshot cpu = new CpuSnapshot();
        public GpuSnapshot gpu = new GpuSnapshot();
        public MemorySnapshot memory = new MemorySnapshot();
        public StorageSnapshot storage = new StorageSnapshot();
        public ScreenSnapshot screen = new ScreenSnapshot();
        public ConnectivitySnapshot connectivity = new ConnectivitySnapshot();
        public TelephonySnapshot telephony = new TelephonySnapshot();
        public AudioSnapshot audio = new AudioSnapshot();
        public CameraSnapshot camera = new CameraSnapshot();
        public SensorSnapshot sensors = new SensorSnapshot();
        public SecuritySnapshot security = new SecuritySnapshot();
        public FeatureSnapshot features = new FeatureSnapshot();
        public PeripheralSnapshot peripherals = new PeripheralSnapshot();
        public SystemSnapshot system = new SystemSnapshot();
        public AndroidSnapshot android = new AndroidSnapshot();
    }

    public FullSnapshot readFullSnapshot() {
        FullSnapshot s = new FullSnapshot();
        s.rooted = isDeviceRooted();
        s.battery = readBatterySnapshot();
        s.thermal = readThermalSnapshot();
        s.cpu = readCpuSnapshot();
        s.gpu = readGpuSnapshot();
        s.memory = readMemorySnapshot();
        s.storage = readStorageSnapshot();
        s.screen = readScreenSnapshot();
        s.connectivity = readConnectivitySnapshot();
        s.telephony = readTelephonySnapshot();
        s.audio = readAudioSnapshot();
        s.camera = readCameraSnapshot();
        s.sensors = readSensorSnapshot();
        s.security = readSecuritySnapshot();
        s.features = readFeatureSnapshot();
        s.peripherals = readPeripheralSnapshot();
        s.system = readSystemSnapshot();
        s.android = readAndroidSnapshot();
        return s;
    }

    // ============================================================
    // BATTERY
    // ============================================================
    public static final class BatterySnapshot {
    	public int sohPercent = -1;
        public int level = -1;
        public int scale = -1;
        public boolean charging = false;
        public String chargingSource = "N/A";
        public String status = "N/A";

        public float batteryTempC = Float.NaN;
        public float voltageMv = Float.NaN;
        public float currentMa = Float.NaN;

        public long chargeNowMah = -1;
        public long chargeFullMah = -1;
        public long chargeDesignMah = -1;
        public long cycleCount = -1;
        public long internalResistance = -1;

        public boolean rooted = false;
        public String source = "N/A";
    }
    
    private static final String PREFS_BATTERY = "battery_prefs";
    private static final String KEY_MODEL_CAP = "model_capacity";

    private long getStoredModelCapacity() {

    try {

        return ctx
                .getSharedPreferences(
                        PREFS_BATTERY,
                        Context.MODE_PRIVATE
                )
                .getLong(KEY_MODEL_CAP, -1);

    } catch (Throwable ignore) {}

    return -1;
}

private void saveModelCapacity(long v) {

    try {

        ctx.getSharedPreferences(
                PREFS_BATTERY,
                Context.MODE_PRIVATE
        )
        .edit()
        .putLong(KEY_MODEL_CAP, v)
        .apply();

    } catch (Throwable ignore) {}
}

    public BatterySnapshot readBatterySnapshot() {

    BatterySnapshot bi = new BatterySnapshot();

    bi.rooted = isDeviceRooted();

    if (bi.rooted) {
        debugDumpPowerSupply();
    }

    try {

        Intent i = ctx.registerReceiver(
                null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );

            if (i != null) {
                bi.level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                bi.scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        bi.status = "Charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        bi.status = "Discharging";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        bi.status = "Full";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        bi.status = "Not charging";
                        break;
                    default:
                        bi.status = "Unknown";
                        break;
                }

                int plug = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                bi.charging =
                        plug == BatteryManager.BATTERY_PLUGGED_USB
                                || plug == BatteryManager.BATTERY_PLUGGED_AC
                                || plug == BatteryManager.BATTERY_PLUGGED_WIRELESS;

                if (plug == BatteryManager.BATTERY_PLUGGED_USB) {
                    bi.chargingSource = "USB";
                } else if (plug == BatteryManager.BATTERY_PLUGGED_AC) {
                    bi.chargingSource = "AC";
                } else if (plug == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                    bi.chargingSource = "Wireless";
                } else {
                    bi.chargingSource = "Battery";
                }

                int rawTemp = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                if (rawTemp > 0) bi.batteryTempC = rawTemp / 10f;

                int volt = i.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                if (volt > 0) bi.voltageMv = volt;
            }
        } catch (Throwable ignore) { }

if (bi.rooted) {

    String[] designPaths = {

        "/sys/class/power_supply/battery/charge_full_design",
        "/sys/class/power_supply/bms/charge_full_design",
        "/sys/class/power_supply/maxfg/charge_full_design"
};

long designRaw =
        readBatteryValueMulti(designPaths);

bi.chargeDesignMah =
        normalizeMah(designRaw);

    String[] fullPaths = {

        "/sys/class/power_supply/battery/charge_full",
        "/sys/class/power_supply/bms/charge_full",
        "/sys/class/power_supply/maxfg/charge_full",
        "/sys/class/power_supply/fg/charge_full",

        profile != null
                ? profile.batteryChargeFullPath
                : null
};

// -----------------------------------------
// CHARGE FULL (fuel gauge + multi fallback)
// -----------------------------------------

long fullRaw =
        readFuelGaugeValue("charge_full");

if (fullRaw <= 0) {

    fullRaw =
            readBatteryValueMulti(
                    fullPaths
            );
}

bi.chargeFullMah =
        normalizeMah(fullRaw);

    String[] nowPaths = {

        "/sys/class/power_supply/battery/charge_now",
        "/sys/class/power_supply/bms/charge_now",
        "/sys/class/power_supply/maxfg/charge_now",
        "/sys/class/power_supply/fg/charge_now",

        profile != null
                ? profile.batteryChargeNowPath
                : null
};

// -----------------------------------------
// CHARGE NOW (fuel gauge + multi fallback)
// -----------------------------------------

long nowRaw =
        readFuelGaugeValue("charge_now");

if (nowRaw <= 0) {

    nowRaw =
            readBatteryValueMulti(
                    nowPaths
            );
}

bi.chargeNowMah =
        normalizeMah(nowRaw);

    bi.cycleCount = readBatteryCycleCountRoot();

    bi.internalResistance = readBatteryResistanceRoot();
    
    if (lastInternalResistanceMilliOhm > 0) {
    bi.internalResistance = lastInternalResistanceMilliOhm;
}

    String[] currentPaths = {

        "/sys/class/power_supply/battery/current_now",
        "/sys/class/power_supply/bms/current_now",
        "/sys/class/power_supply/maxfg/current_now",
        "/sys/class/power_supply/fg/current_now",

        profile != null
                ? profile.batteryCurrentPath
                : null
};

// -----------------------------------------
// CURRENT READ (fuel gauge + multi fallback)
// -----------------------------------------

long currentNow =
        readFuelGaugeValue("current_now");

if (currentNow == 0) {

    currentNow =
            readBatteryValueMulti(
                    currentPaths
            );
}

float currentMa =
        normalizeCurrentMa(currentNow);

if (!Float.isNaN(currentMa))
    bi.currentMa = currentMa;

    String[] voltPaths = {

        "/sys/class/power_supply/battery/voltage_now",
        "/sys/class/power_supply/bms/voltage_now",
        "/sys/class/power_supply/maxfg/voltage_now",
        "/sys/class/power_supply/fg/voltage_now",

        profile != null
                ? profile.batteryVoltagePath
                : null
};

// -----------------------------------------
// VOLTAGE READ (fuel gauge + multi fallback)
// -----------------------------------------

long voltageNow =
        readFuelGaugeValue("voltage_now");

if (voltageNow <= 0) {

    voltageNow =
            readBatteryValueMulti(
                    voltPaths
            );
}

float voltageMv =
        normalizeVoltageMv(voltageNow);

if (!Float.isNaN(voltageMv))
    bi.voltageMv = voltageMv;


// -----------------------------------------

if (bi.chargeFullMah > 0 ||
    bi.chargeDesignMah > 0) {

    bi.source = "OEM (root)";
}

}

// --------------------------------------------------
// FULL CAPACITY FALLBACK (unified)
// --------------------------------------------------

if (bi.chargeFullMah <= 0) {

    if (bi.chargeDesignMah > 0) {

        bi.chargeFullMah = bi.chargeDesignMah;
        bi.source = "design";
    }
}

// --------------------------------------------------
// CHARGE COUNTER FALLBACK (BatteryManager)
// --------------------------------------------------

if (bi.chargeNowMah <= 0) {

    try {

        BatteryManager bm =
                (BatteryManager) ctx.getSystemService(Context.BATTERY_SERVICE);

        if (bm != null) {

            long cc =
                    bm.getLongProperty(
                            BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER
                    );

            if (cc > 0) {

                bi.chargeNowMah =
                        normalizeMah(cc);

                bi.source = "BatteryManager";
            }
        }

    } catch (Throwable ignore) {}
}


// --------------------------------------------------
// COUNTER → FULL ESTIMATE (safe)
// --------------------------------------------------

if (bi.chargeFullMah <= 0 &&
    bi.chargeNowMah > 0 &&
    bi.level > 0) {

    try {

        float pct =
                bi.level / 100f;

        if (pct > 0.10f && pct <= 1f) {

            long est =
                    (long) (bi.chargeNowMah / pct);

            if (est > 500 &&
                est < 15000) {

                bi.chargeFullMah = est;
                bi.source = "counter_estimate";
            }
        }

    } catch (Throwable ignore) {}
}

// --------------------------------------------------
// DESIGN FALLBACK
// --------------------------------------------------

if (bi.chargeFullMah <= 0 &&
    bi.chargeDesignMah > 0) {

    bi.chargeFullMah = bi.chargeDesignMah;
    bi.source = "design";
}

// --------------------------------------------------
// MODEL CAPACITY (LAST FALLBACK ONLY)
// --------------------------------------------------

long modelCap =
        getStoredModelCapacity();

if (bi.chargeFullMah <= 0 &&
    modelCap > 0) {

    bi.chargeFullMah = modelCap;
    bi.source = "model_capacity";
}
        	
// -----------------------------------------
// TEMP READ (fuel gauge + multi fallback)
// -----------------------------------------

if (Float.isNaN(bi.batteryTempC) ||
    bi.batteryTempC <= 0f) {

    String[] tempPaths = {

            "/sys/class/power_supply/battery/temp",
            "/sys/class/power_supply/bms/temp",
            "/sys/class/power_supply/maxfg/temp",

            profile != null
                    ? profile.batteryTempPath
                    : null
    };

    long tempRaw =
            readFuelGaugeValue("temp");

    if (tempRaw <= 0) {

        tempRaw =
                readBatteryValueMulti(
                        tempPaths
                );
    }

    if (tempRaw > 0) {

        float tempC =
                normalizeTempC(
                        String.valueOf(tempRaw)
                );

        if (!Float.isNaN(tempC))
            bi.batteryTempC = tempC;
    }
}
        	
// --------------------------------------------------
// FALLBACK VOLTAGE / CURRENT / TEMP (no root)
// --------------------------------------------------

try {

    BatteryManager bm =
            (BatteryManager) ctx.getSystemService(Context.BATTERY_SERVICE);

    if (bm != null) {

        if (Float.isNaN(bi.voltageMv) || bi.voltageMv <= 0) {

            Intent i = ctx.registerReceiver(
                    null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            );

            if (i != null) {

                int v = i.getIntExtra(
                        BatteryManager.EXTRA_VOLTAGE,
                        -1
                );

                if (v > 0)
                    bi.voltageMv = v;
            }
        }

        if (Float.isNaN(bi.currentMa)) {

            long cur =
                    bm.getLongProperty(
                            BatteryManager.BATTERY_PROPERTY_CURRENT_NOW
                    );

            float ma = normalizeCurrentMa(cur);

            if (!Float.isNaN(ma))
                bi.currentMa = ma;
        }

    }

} catch (Throwable ignore) { }

if (bi.source == null ||
    bi.source.equals("N/A")) {

    if (bi.rooted)
        bi.source = "OEM (root)";
    else
        bi.source = "BatteryManager";
}

// --------------------------------------------------
// SOH CALC
// --------------------------------------------------

if (bi.chargeDesignMah > 0 &&
    bi.chargeFullMah > 0) {

    try {

        int soh =
                (int) Math.round(
                        (bi.chargeFullMah * 100.0)
                                / bi.chargeDesignMah
                );

        if (soh > 0 && soh < 200) {
            bi.sohPercent = soh;
        }

    } catch (Throwable ignore) {}
}

        return bi;
    }

    public boolean isChargingNowUnified() {
        return readBatterySnapshot().charging;
    }

    public Float getBatteryTempUnified() {
        BatterySnapshot s = readBatterySnapshot();
        return Float.isNaN(s.batteryTempC) ? null : s.batteryTempC;
    }

    public float getBatteryVoltageUnified() {
        return readBatterySnapshot().voltageMv;
    }

    public float getBatteryCurrentNowUnified() {
        return readBatterySnapshot().currentMa;
    }

    // ============================================================
    // THERMAL
    // ============================================================
    public static final class ThermalReading {
        public String name = "N/A";
        public String path = "N/A";
        public String source = "N/A";
        public float tempC = Float.NaN;
        public boolean valid = false;
    }

    public static final class ThermalSnapshot {
        public boolean rooted = false;

        public ThermalReading cpu = new ThermalReading();
        public ThermalReading gpu = new ThermalReading();
        public ThermalReading battery = new ThermalReading();
        public ThermalReading skin = new ThermalReading();
        public ThermalReading pmic = new ThermalReading();
        public ThermalReading charger = new ThermalReading();
        public ThermalReading modemMain = new ThermalReading();
        public ThermalReading modemAux = new ThermalReading();

        public int thermalZoneCount = 0;
        public int hardwareCoolingDeviceCount = 0;

        public List<ThermalReading> all = new ArrayList<>();
        public List<String> coolingDevices = new ArrayList<>();
    }

    public ThermalSnapshot readThermalSnapshot() {
        ThermalSnapshot out = new ThermalSnapshot();
        out.rooted = isDeviceRooted();

        List<ThermalReading> all = scanThermalZones();
        if (out.rooted) {
            all.addAll(scanHwmonTempsRootAware());
        }
        out.all = dedupeReadings(all);

        for (ThermalReading r : out.all) {
            if (r.valid) out.thermalZoneCount++;
        }

        out.cpu = selectMax(out.all, ThermalGroup.CPU);
        out.gpu = selectMax(out.all, ThermalGroup.GPU);
        out.battery = selectMax(out.all, ThermalGroup.BATTERY);
        out.skin = selectMax(out.all, ThermalGroup.SKIN);
        out.pmic = selectMax(out.all, ThermalGroup.PMIC);
        out.charger = selectMax(out.all, ThermalGroup.CHARGER);
        out.modemMain = selectMax(out.all, ThermalGroup.MODEM_MAIN);
        out.modemAux = selectMax(out.all, ThermalGroup.MODEM_AUX);
        
// ------------------------------------------------------------
// PROFILE FALLBACK (only if universal scan returned N/A)
// ------------------------------------------------------------

if (!out.cpu.valid) {
    out.cpu = readThermalFromProfilePath(
            profile != null ? profile.cpuTempPath : null,
            "CPU"
    );
}

if (!out.gpu.valid) {
    out.gpu = readThermalFromProfilePath(
            profile != null ? profile.gpuTempPath : null,
            "GPU"
    );
}

if (!out.battery.valid) {
    out.battery = readThermalFromProfilePath(
            profile != null ? profile.batteryTempPath : null,
            "Battery"
    );
}

        out.coolingDevices = scanHardwareCoolingDevices();
        out.hardwareCoolingDeviceCount = out.coolingDevices.size();

        return out;
    }

    public Float getCpuTempUnified() {
        ThermalSnapshot t = readThermalSnapshot();
        return (t.cpu != null && t.cpu.valid) ? t.cpu.tempC : null;
    }

    public Float getGpuTempUnified() {
        ThermalSnapshot t = readThermalSnapshot();
        return (t.gpu != null && t.gpu.valid) ? t.gpu.tempC : null;
    }

    public Float getSkinTempUnified() {
        ThermalSnapshot t = readThermalSnapshot();
        return (t.skin != null && t.skin.valid) ? t.skin.tempC : null;
    }

    public Float getPmicTempUnified() {
        ThermalSnapshot t = readThermalSnapshot();
        return (t.pmic != null && t.pmic.valid) ? t.pmic.tempC : null;
    }

    public Float getChargerTempUnified() {
        ThermalSnapshot t = readThermalSnapshot();
        return (t.charger != null && t.charger.valid) ? t.charger.tempC : null;
    }

    // ============================================================
    // CPU
    // ============================================================
    public static final class CpuSnapshot {
        public int cores = -1;
        public String abi = "N/A";
        public String hardware = "N/A";
        public String modelName = "N/A";
        public String governor = "N/A";

        public long currentFreqKHz = -1;
        public long minFreqKHz = -1;
        public long maxFreqKHz = -1;

        public List<CoreFreq> coreFreqs = new ArrayList<>();
        public Float cpuTempC = null;
    }

    public static final class CoreFreq {
        public int coreIndex = -1;
        public long currentFreqKHz = -1;
        public long minFreqKHz = -1;
        public long maxFreqKHz = -1;
    }

    public CpuSnapshot readCpuSnapshot() {
        CpuSnapshot s = new CpuSnapshot();

        s.cores = Runtime.getRuntime().availableProcessors();
        s.abi = joinAbis();
        s.hardware = safeNonEmpty(Build.HARDWARE, "N/A");

        String cpuinfo = readTextFile("/proc/cpuinfo", 32 * 1024);
        if (cpuinfo != null && !cpuinfo.isEmpty()) {
            String[] lines = cpuinfo.split("\n");
            for (String line : lines) {
                String low = line.toLowerCase(Locale.US);
                if (low.startsWith("model name")) {
                    String[] p = line.split(":", 2);
                    if (p.length == 2) s.modelName = p[1].trim();
                } else if (low.startsWith("hardware")) {
                    String[] p = line.split(":", 2);
                    if (p.length == 2) s.hardware = p[1].trim();
                }
            }
        }

        s.governor = safeNonEmpty(
                readSysString("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor"),
                "N/A"
        );

        s.currentFreqKHz = readSysLong("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
        s.minFreqKHz = readSysLong("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
        s.maxFreqKHz = readSysLong("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");

        for (int i = 0; i < Math.max(1, s.cores); i++) {
            CoreFreq cf = new CoreFreq();
            cf.coreIndex = i;

            String base = "/sys/devices/system/cpu/cpu" + i + "/cpufreq/";
            cf.currentFreqKHz = readSysLong(base + "scaling_cur_freq");
            cf.minFreqKHz = readSysLong(base + "cpuinfo_min_freq");
            cf.maxFreqKHz = readSysLong(base + "cpuinfo_max_freq");

            if (cf.currentFreqKHz > 0 || cf.minFreqKHz > 0 || cf.maxFreqKHz > 0) {
                s.coreFreqs.add(cf);
            }
        }

        s.cpuTempC = getCpuTempUnified();

        return s;
    }

    // ============================================================
    // GPU
    // ============================================================
    public static final class GpuSnapshot {
        public String openGlEsVersion = "N/A";
        public String eglHardware = "N/A";
        public String driver = "N/A";
        public String gpuName = "N/A";

        public long currentFreqHz = -1;
        public long minFreqHz = -1;
        public long maxFreqHz = -1;

        public Float gpuTempC = null;
    }

    public GpuSnapshot readGpuSnapshot() {
        GpuSnapshot s = new GpuSnapshot();

        try {
            ActivityManager am =
                    (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                ConfigurationInfo ci = am.getDeviceConfigurationInfo();
                if (ci != null) s.openGlEsVersion = safeNonEmpty(ci.getGlEsVersion(), "N/A");
            }
        } catch (Throwable ignore) { }

        s.eglHardware = safeNonEmpty(getProp("ro.hardware.egl"), "N/A");
        s.driver = safeNonEmpty(getProp("ro.gfx.driver.0"), "N/A");
        s.gpuName = firstNonEmpty(
                getProp("ro.hardware.vulkan"),
                getProp("ro.board.platform"),
                "N/A"
        );

        s.currentFreqHz = readSysLong("/sys/class/kgsl/kgsl-3d0/devfreq/cur_freq");
        s.minFreqHz = readSysLong("/sys/class/kgsl/kgsl-3d0/devfreq/min_freq");
        s.maxFreqHz = readSysLong("/sys/class/kgsl/kgsl-3d0/devfreq/max_freq");

        s.gpuTempC = getGpuTempUnified();

        return s;
    }

    // ============================================================
    // MEMORY
    // ============================================================
    public static final class MemorySnapshot {
        public long totalRamMb = -1;
        public long freeRamMb = -1;
        public long usedRamMb = -1;
        public boolean lowMemory = false;
        public long thresholdMb = -1;

        public long memTotalKb = -1;
        public long memFreeKb = -1;
        public long cachedKb = -1;
        public long activeKb = -1;
        public long inactiveKb = -1;
        public long swapTotalKb = -1;
        public long swapFreeKb = -1;
        public long buffersKb = -1;
    }

    public MemorySnapshot readMemorySnapshot() {
        MemorySnapshot s = new MemorySnapshot();

        try {
            ActivityManager am =
                    (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

            if (am != null) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(mi);

                s.totalRamMb = mi.totalMem / (1024 * 1024);
                s.freeRamMb = mi.availMem / (1024 * 1024);
                s.usedRamMb = s.totalRamMb - s.freeRamMb;
                s.lowMemory = mi.lowMemory;
                s.thresholdMb = mi.threshold / (1024 * 1024);
            }
        } catch (Throwable ignore) { }

        String meminfo = readTextFile("/proc/meminfo", 8 * 1024);
        if (meminfo != null && !meminfo.isEmpty()) {
            for (String line : meminfo.split("\n")) {
                if (line.startsWith("MemTotal:")) s.memTotalKb = parseKb(line);
                else if (line.startsWith("MemFree:")) s.memFreeKb = parseKb(line);
                else if (line.startsWith("Cached:")) s.cachedKb = parseKb(line);
                else if (line.startsWith("Active:")) s.activeKb = parseKb(line);
                else if (line.startsWith("Inactive:")) s.inactiveKb = parseKb(line);
                else if (line.startsWith("SwapTotal:")) s.swapTotalKb = parseKb(line);
                else if (line.startsWith("SwapFree:")) s.swapFreeKb = parseKb(line);
                else if (line.startsWith("Buffers:")) s.buffersKb = parseKb(line);
            }
        }

        return s;
    }

    // ============================================================
    // STORAGE
    // ============================================================
    public static final class StorageBlock {
        public String label = "N/A";
        public String path = "N/A";
        public long totalBytes = -1;
        public long usedBytes = -1;
        public long freeBytes = -1;
        public long totalGb = -1;
        public long usedGb = -1;
        public long freeGb = -1;
    }

    public static final class StorageSnapshot {
        public StorageBlock internal = new StorageBlock();
        public StorageBlock externalPrimary = new StorageBlock();
        public String mounts = "N/A";
        public String partitions = "N/A";
    }

    public StorageSnapshot readStorageSnapshot() {
        StorageSnapshot s = new StorageSnapshot();

        try {
            File internal = Environment.getDataDirectory();
            s.internal = buildStorageBlock("Internal", internal);

            File ext = Environment.getExternalStorageDirectory();
            if (ext != null && ext.exists()) {
                s.externalPrimary = buildStorageBlock("External (primary)", ext);
            }
        } catch (Throwable ignore) { }

        String mounts = readTextFile("/proc/mounts", 32 * 1024);
        s.mounts = mounts != null && !mounts.isEmpty() ? mounts : "N/A";

        String parts = readTextFile("/proc/partitions", 8 * 1024);
        s.partitions = parts != null && !parts.isEmpty() ? parts : "N/A";

        return s;
    }

    // ============================================================
    // SCREEN
    // ============================================================
    public static final class ScreenSnapshot {
        public int widthPx = -1;
        public int heightPx = -1;
        public int densityDpi = -1;
        public float scaledDensity = Float.NaN;

        public float refreshRateHz = Float.NaN;
        public float maxRefreshRateHz = Float.NaN;

        public boolean wideColor = false;
        public int hdrModeCount = 0;

        public String orientation = "N/A";
        public double diagonalInches = Double.NaN;
    }

    public ScreenSnapshot readScreenSnapshot() {
        ScreenSnapshot s = new ScreenSnapshot();

        try {
            WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                Display display = wm.getDefaultDisplay();
                DisplayMetrics dm = new DisplayMetrics();
                display.getRealMetrics(dm);

                s.widthPx = dm.widthPixels;
                s.heightPx = dm.heightPixels;
                s.densityDpi = dm.densityDpi;
                s.scaledDensity = dm.scaledDensity;
                s.refreshRateHz = display.getRefreshRate();

                if (Build.VERSION.SDK_INT >= 23) {
                    try {
                        Display.Mode[] modes = display.getSupportedModes();
                        float max = 0f;
                        if (modes != null) {
                            for (Display.Mode m : modes) {
                                if (m.getRefreshRate() > max) max = m.getRefreshRate();
                            }
                        }
                        s.maxRefreshRateHz = max > 0f ? max : Float.NaN;
                    } catch (Throwable ignore) { }
                }

                if (Build.VERSION.SDK_INT >= 26) {
                    try {
                        s.wideColor = display.isWideColorGamut();
                    } catch (Throwable ignore) { }
                }

                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        Display.HdrCapabilities hc = display.getHdrCapabilities();
                        int[] types = hc != null ? hc.getSupportedHdrTypes() : null;
                        s.hdrModeCount = types != null ? types.length : 0;
                    } catch (Throwable ignore) { }
                }

                try {
                    int o = ctx.getResources().getConfiguration().orientation;
                    s.orientation = (o == android.content.res.Configuration.ORIENTATION_LANDSCAPE)
                            ? "Landscape" : "Portrait";
                } catch (Throwable ignore) { }

                try {
                    double inchW = (double) s.widthPx / dm.xdpi;
                    double inchH = (double) s.heightPx / dm.ydpi;
                    s.diagonalInches = Math.sqrt(inchW * inchW + inchH * inchH);
                } catch (Throwable ignore) { }
            }
        } catch (Throwable ignore) { }

        return s;
    }

    // ============================================================
    // CONNECTIVITY
    // ============================================================
    public static final class ConnectivitySnapshot {
        public boolean wifiSupported = false;
        public boolean wifiEnabled = false;
        public String ssid = "N/A";
        public int linkSpeedMbps = -1;
        public int rssiDbm = -1;
        public int frequencyMhz = -1;
        public String wifiBand = "N/A";
        public String wifiStandard = "N/A";
        public String wifiMac = "N/A";

        public boolean bluetoothSupported = false;
        public boolean bluetoothEnabled = false;
        public String bluetoothState = "N/A";
        public boolean bleSupported = false;

        public boolean nfcSupported = false;
        public boolean nfcEnabled = false;
    }

    public ConnectivitySnapshot readConnectivitySnapshot() {
        ConnectivitySnapshot s = new ConnectivitySnapshot();

        try {
            WifiManager wm =
                    (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            PackageManager pm = ctx.getPackageManager();

            s.wifiSupported = pm.hasSystemFeature(PackageManager.FEATURE_WIFI);

            if (wm != null) {
                try {
                    s.wifiEnabled = wm.isWifiEnabled();
                } catch (Throwable ignore) { }

                try {
                    WifiInfo wi = wm.getConnectionInfo();
                    if (wi != null) {
                        s.ssid = safeNonEmpty(wi.getSSID(), "N/A");
                        s.linkSpeedMbps = wi.getLinkSpeed();
                        s.rssiDbm = wi.getRssi();
                        s.frequencyMhz = wi.getFrequency();
                        s.wifiBand = describeWifiBand(s.frequencyMhz);

                        String rawMac = wi.getMacAddress();
                        s.wifiMac =
                                rawMac != null && !"02:00:00:00:00:00".equals(rawMac)
                                        ? rawMac
                                        : "N/A";

                        if (Build.VERSION.SDK_INT >= 30) {
                            try {
                                switch (wi.getWifiStandard()) {
                                    case 6:
                                        s.wifiStandard = "Wi-Fi 6 / 6E";
                                        break;
                                    case 5:
                                        s.wifiStandard = "Wi-Fi 5";
                                        break;
                                    case 4:
                                        s.wifiStandard = "Wi-Fi 4";
                                        break;
                                    case 1:
                                        s.wifiStandard = "802.11a";
                                        break;
                                    case 2:
                                        s.wifiStandard = "802.11b";
                                        break;
                                    case 3:
                                        s.wifiStandard = "802.11g";
                                        break;
                                    default:
                                        s.wifiStandard = "Unknown";
                                        break;
                                }
                            } catch (Throwable ignore) { }
                        }
                    }
                } catch (Throwable ignore) { }
            }

            try {
                BluetoothManager bm =
                        (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter ba = bm != null ? bm.getAdapter() : null;

                if (ba != null) {
                    s.bluetoothSupported = true;
                    s.bluetoothEnabled = ba.isEnabled();

                    int state = ba.getState();
                    s.bluetoothState =
                            state == BluetoothAdapter.STATE_ON ? "On" :
                                    state == BluetoothAdapter.STATE_TURNING_ON ? "Turning On" :
                                            state == BluetoothAdapter.STATE_TURNING_OFF ? "Turning Off" :
                                                    "Off";
                }
            } catch (Throwable ignore) { }

            try {
                s.bleSupported = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
            } catch (Throwable ignore) { }

            try {
                NfcManager nm = (NfcManager) ctx.getSystemService(Context.NFC_SERVICE);
                NfcAdapter na = nm != null ? nm.getDefaultAdapter() : null;
                if (na != null) {
                    s.nfcSupported = true;
                    s.nfcEnabled = na.isEnabled();
                }
            } catch (Throwable ignore) { }

        } catch (Throwable ignore) { }

        return s;
    }

    // ============================================================
    // TELEPHONY
    // ============================================================
    public static final class SimEntry {
        public int slot = -1;
        public String carrier = "N/A";
    }

    public static final class TelephonySnapshot {
        public boolean airplaneOn = false;

        public int simState = TelephonyManager.SIM_STATE_UNKNOWN;
        public boolean simReady = false;

        public int serviceState = ServiceState.STATE_OUT_OF_SERVICE;
        public boolean inService = false;

        public int dataState = TelephonyManager.DATA_UNKNOWN;
        public String phoneType = "N/A";
        public String dataNetwork = "N/A";
        public boolean nr5gActive = false;

        public String carrier = "N/A";
        public String countryIso = "N/A";
        public String operatorCode = "N/A";

        public int signalLevel = -1;
        public boolean roaming = false;

        public int activeSimCount = 0;
        public List<SimEntry> sims = new ArrayList<>();
    }

    public TelephonySnapshot readTelephonySnapshot() {
        TelephonySnapshot s = new TelephonySnapshot();

        try {
            s.airplaneOn = Settings.Global.getInt(
                    ctx.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON,
                    0
            ) == 1;
        } catch (Throwable ignore) { }

        TelephonyManager tm =
                (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        SubscriptionManager sm =
                (SubscriptionManager) ctx.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        if (tm != null) {
            try {
                s.simState = tm.getSimState();
                s.simReady = (s.simState == TelephonyManager.SIM_STATE_READY);
            } catch (Throwable ignore) { }

            try {
                ServiceState ss = tm.getServiceState();
                if (ss != null) {
                    s.serviceState = ss.getState();
                    s.inService = (s.serviceState == ServiceState.STATE_IN_SERVICE);
                }
            } catch (Throwable ignore) { }

            try {
                s.dataState = tm.getDataState();
            } catch (Throwable ignore) { }

            try {
                switch (tm.getPhoneType()) {
                    case TelephonyManager.PHONE_TYPE_GSM:
                        s.phoneType = "GSM";
                        break;
                    case TelephonyManager.PHONE_TYPE_CDMA:
                        s.phoneType = "CDMA";
                        break;
                    case TelephonyManager.PHONE_TYPE_SIP:
                        s.phoneType = "SIP";
                        break;
                    default:
                        s.phoneType = "None";
                        break;
                }
            } catch (Throwable ignore) { }

            try {
                int net = tm.getDataNetworkType();
                s.nr5gActive = (net == TelephonyManager.NETWORK_TYPE_NR);
                s.dataNetwork = describeNetworkType(net);
            } catch (Throwable ignore) { }

            try {
                s.carrier = safeNonEmpty(tm.getNetworkOperatorName(), "Unknown");
                s.countryIso = safeNonEmpty(tm.getNetworkCountryIso(), "Unknown").toUpperCase(Locale.US);
                s.operatorCode = safeNonEmpty(tm.getNetworkOperator(), "Unknown");
            } catch (Throwable ignore) { }

            try {
                SignalStrength ss = tm.getSignalStrength();
                if (ss != null) s.signalLevel = ss.getLevel();
            } catch (Throwable ignore) { }

            try {
                s.roaming = tm.isNetworkRoaming();
            } catch (Throwable ignore) { }
        }

        try {
            List<SubscriptionInfo> subs = null;
            if (sm != null) {
                try {
                    subs = sm.getActiveSubscriptionInfoList();
                } catch (Throwable ignore) { }
            }

            if (subs != null) {
                boolean[] seen = new boolean[4];
                for (SubscriptionInfo si : subs) {
                    if (si == null) continue;

                    int slot = -1;
                    try { slot = si.getSimSlotIndex(); } catch (Throwable ignore) { }

                    if (slot >= 0 && slot < seen.length && !seen[slot]) {
                        seen[slot] = true;

                        SimEntry e = new SimEntry();
                        e.slot = slot;
                        try {
                            e.carrier = si.getCarrierName() != null
                                    ? si.getCarrierName().toString()
                                    : "Unknown";
                        } catch (Throwable ignore) { }

                        s.sims.add(e);
                    }
                }
                s.activeSimCount = s.sims.size();
            }
        } catch (Throwable ignore) { }

        return s;
    }

    // ============================================================
    // AUDIO
    // ============================================================
    public static final class AudioSnapshot {
        public String audioHal = "N/A";

        public boolean hasBuiltInMic = false;
        public boolean hasTelephonyMic = false;
        public int wiredMicCount = 0;
        public int bluetoothMicCount = 0;
        public int usbMicCount = 0;

        public boolean hasSpeakerOutput = false;
        public boolean hasWiredOutput = false;
        public boolean hasBluetoothOutput = false;
        public boolean hasUsbOutput = false;
        public boolean hasHdmiOutput = false;

        public boolean audioOutputHw = false;
    }

    public AudioSnapshot readAudioSnapshot() {
        AudioSnapshot s = new AudioSnapshot();
        s.audioHal = safeNonEmpty(getProp("ro.audio.hal.version"), "N/A");

        try {
            AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                AudioDeviceInfo[] ins = am.getDevices(AudioManager.GET_DEVICES_INPUTS);
                for (AudioDeviceInfo d : ins) {
                    switch (d.getType()) {
                        case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                            s.hasBuiltInMic = true;
                            break;
                        case AudioDeviceInfo.TYPE_TELEPHONY:
                            s.hasTelephonyMic = true;
                            break;
                        case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                        case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                            s.wiredMicCount++;
                            break;
                        case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                        case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                            s.bluetoothMicCount++;
                            break;
                        case AudioDeviceInfo.TYPE_USB_DEVICE:
                        case AudioDeviceInfo.TYPE_USB_HEADSET:
                            s.usbMicCount++;
                            break;
                    }
                }

                AudioDeviceInfo[] outs = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
                for (AudioDeviceInfo o : outs) {
                    switch (o.getType()) {
                        case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:
                            s.hasSpeakerOutput = true;
                            break;
                        case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                        case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                            s.hasWiredOutput = true;
                            break;
                        case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                        case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                            s.hasBluetoothOutput = true;
                            break;
                        case AudioDeviceInfo.TYPE_USB_DEVICE:
                        case AudioDeviceInfo.TYPE_USB_HEADSET:
                            s.hasUsbOutput = true;
                            break;
                        case AudioDeviceInfo.TYPE_HDMI:
                            s.hasHdmiOutput = true;
                            break;
                    }
                }
            }
        } catch (Throwable ignore) { }

        try {
            s.audioOutputHw = ctx.getPackageManager().hasSystemFeature("android.hardware.audio.output");
        } catch (Throwable ignore) { }

        return s;
    }

    // ============================================================
    // CAMERA
    // ============================================================
    public static final class CameraUnit {
        public String id = "N/A";
        public String facing = "Unknown";
        public Integer orientation = null;
        public Float focalMm = null;
        public Float aperture = null;
        public Boolean flash = null;

        public int jpegModeCount = 0;
        public int videoModeCount = 0;
        public String maxVideoResolution = "N/A";

        public int minFps = -1;
        public int maxFps = -1;

        public boolean stabilization = false;
        public boolean hdrVideo = false;
        public int capabilityCount = 0;
        public String hardwareLevel = "Unknown";
    }

    public static final class CameraSnapshot {
        public List<CameraUnit> cameras = new ArrayList<>();
        public int cameraCount = 0;
    }

    public CameraSnapshot readCameraSnapshot() {
        CameraSnapshot out = new CameraSnapshot();

        try {
            CameraManager cm = (CameraManager) ctx.getSystemService(Context.CAMERA_SERVICE);
            if (cm == null) return out;

            for (String id : cm.getCameraIdList()) {
                try {
                    CameraCharacteristics cc = cm.getCameraCharacteristics(id);
                    CameraUnit cu = new CameraUnit();

                    cu.id = id;

                    Integer facing = cc.get(CameraCharacteristics.LENS_FACING);
                    if (facing != null) {
                        if (facing == CameraCharacteristics.LENS_FACING_FRONT) cu.facing = "Front";
                        else if (facing == CameraCharacteristics.LENS_FACING_BACK) cu.facing = "Back";
                        else if (facing == CameraCharacteristics.LENS_FACING_EXTERNAL) cu.facing = "External";
                    }

                    cu.orientation = cc.get(CameraCharacteristics.SENSOR_ORIENTATION);

                    float[] focals = cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                    if (focals != null && focals.length > 0) cu.focalMm = focals[0];

                    float[] apertures = cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
                    if (apertures != null && apertures.length > 0) cu.aperture = apertures[0];

                    cu.flash = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);

                    int[] reqCaps = cc.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
                    cu.capabilityCount = reqCaps != null ? reqCaps.length : 0;

                    StreamConfigurationMap map =
                            cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                    if (map != null) {
                        android.util.Size[] jpegSizes =
                                map.getOutputSizes(android.graphics.ImageFormat.JPEG);
                        cu.jpegModeCount = jpegSizes != null ? jpegSizes.length : 0;

                        android.util.Size[] videoSizes =
                                map.getOutputSizes(android.media.MediaRecorder.class);

                        cu.videoModeCount = videoSizes != null ? videoSizes.length : 0;

                        if (videoSizes != null && videoSizes.length > 0) {
                            android.util.Size max = videoSizes[0];
                            for (android.util.Size s : videoSizes) {
                                if (s.getWidth() * s.getHeight() >
                                        max.getWidth() * max.getHeight()) {
                                    max = s;
                                }
                            }
                            cu.maxVideoResolution = max.getWidth() + "x" + max.getHeight();
                        }
                    }

                    android.util.Range<Integer>[] fpsRanges =
                            cc.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
                    if (fpsRanges != null && fpsRanges.length > 0) {
                        int min = Integer.MAX_VALUE;
                        int max = 0;
                        for (android.util.Range<Integer> r : fpsRanges) {
                            min = Math.min(min, r.getLower());
                            max = Math.max(max, r.getUpper());
                        }
                        cu.minFps = min;
                        cu.maxFps = max;
                    }

                    int[] stab =
                            cc.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);
                    cu.stabilization = stab != null && stab.length > 0;

                    if (reqCaps != null && Build.VERSION.SDK_INT >= 33) {
                        for (int c : reqCaps) {
                            if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DYNAMIC_RANGE_TEN_BIT) {
                                cu.hdrVideo = true;
                                break;
                            }
                        }
                    }

                    Integer hw = cc.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    if (hw != null) {
                        switch (hw) {
                            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                                cu.hardwareLevel = "FULL";
                                break;
                            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                                cu.hardwareLevel = "LIMITED";
                                break;
                            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                                cu.hardwareLevel = "LEGACY";
                                break;
                            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                                cu.hardwareLevel = "LEVEL_3";
                                break;
                            default:
                                cu.hardwareLevel = "UNKNOWN";
                                break;
                        }
                    }

                    out.cameras.add(cu);

                } catch (Throwable ignore) { }
            }

        } catch (CameraAccessException ignore) { }
        catch (Throwable ignore) { }

        out.cameraCount = out.cameras.size();
        return out;
    }

    // ============================================================
    // SENSORS
    // ============================================================
    public static final class SensorSnapshot {
        public boolean accelerometer = false;
        public boolean gyroscope = false;
        public boolean magnetometer = false;
        public boolean barometer = false;
        public boolean proximity = false;
        public boolean light = false;

        public boolean stepCounter = false;
        public boolean stepDetector = false;

        public boolean rotationVector = false;
        public boolean gameRotationVector = false;
        public boolean gravity = false;
        public boolean linearAcceleration = false;

        public boolean significantMotion = false;
        public boolean stationaryDetect = false;
        public boolean motionDetect = false;
    }

    public SensorSnapshot readSensorSnapshot() {
        SensorSnapshot s = new SensorSnapshot();

        try {
            SensorManager sm = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
            if (sm != null) {
                s.accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
                s.gyroscope = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
                s.magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null;
                s.barometer = sm.getDefaultSensor(Sensor.TYPE_PRESSURE) != null;
                s.proximity = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null;
                s.light = sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null;

                s.stepCounter = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null;
                s.stepDetector = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null;

                s.rotationVector = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null;
                s.gameRotationVector = sm.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) != null;
                s.gravity = sm.getDefaultSensor(Sensor.TYPE_GRAVITY) != null;
                s.linearAcceleration = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null;

                s.significantMotion = sm.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION) != null;

                if (Build.VERSION.SDK_INT >= 24) {
                    s.stationaryDetect = sm.getDefaultSensor(Sensor.TYPE_STATIONARY_DETECT) != null;
                    s.motionDetect = sm.getDefaultSensor(Sensor.TYPE_MOTION_DETECT) != null;
                }
            }
        } catch (Throwable ignore) { }

        return s;
    }

    // ============================================================
    // SECURITY
    // ============================================================
    public static final class SecuritySnapshot {
        public String kernel = "N/A";
        public String securityPatch = "N/A";
        public String verifiedBoot = "N/A";
        public boolean strongBox = false;
        public boolean hce = false;
        public boolean rooted = false;

        public String buildTags = "N/A";
        public String roSecure = "N/A";
        public String roDebuggable = "N/A";
        public String verityMode = "N/A";
        public String selinux = "N/A";
    }

    public SecuritySnapshot readSecuritySnapshot() {
        SecuritySnapshot s = new SecuritySnapshot();

        s.kernel = safeNonEmpty(readSysString("/proc/version"), "N/A");
        s.securityPatch = safeNonEmpty(Build.VERSION.SECURITY_PATCH, "N/A");
        s.verifiedBoot = safeNonEmpty(getProp("ro.boot.verifiedbootstate"), "N/A");
        s.rooted = isDeviceRooted();
        s.buildTags = safeNonEmpty(Build.TAGS, "N/A");
        s.roSecure = safeNonEmpty(getProp("ro.secure"), "N/A");
        s.roDebuggable = safeNonEmpty(getProp("ro.debuggable"), "N/A");
        s.verityMode = safeNonEmpty(getProp("ro.boot.veritymode"), "N/A");
        s.selinux = safeNonEmpty(getProp("ro.build.selinux"), "N/A");

        try {
            PackageManager pm = ctx.getPackageManager();
            s.strongBox = pm.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE);
            s.hce = pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);
        } catch (Throwable ignore) { }

        return s;
    }

    // ============================================================
    // FEATURES
    // ============================================================
    public static final class FeatureSnapshot {
        public int featureCount = 0;
        public List<String> features = new ArrayList<>();
    }

    public FeatureSnapshot readFeatureSnapshot() {
        FeatureSnapshot s = new FeatureSnapshot();

        try {
            FeatureInfo[] feats = ctx.getPackageManager().getSystemAvailableFeatures();
            if (feats != null) {
                s.featureCount = feats.length;
                for (FeatureInfo fi : feats) {
                    if (fi != null && fi.name != null) s.features.add(fi.name);
                }
            }
        } catch (Throwable ignore) { }

        return s;
    }

    // ============================================================
    // PERIPHERALS
    // ============================================================
    public static final class PeripheralSnapshot {
        public boolean nfcSupported = false;
        public boolean nfcEnabled = false;

        public boolean uwbSupported = false;

        public boolean vibratorPresent = false;
        public boolean amplitudeControl = false;

        public boolean irBlaster = false;
        public boolean fmRadio = false;
        public boolean hallSensor = false;
        public boolean hardwareKeyboard = false;
        public boolean wirelessCharging = false;
        public boolean tvTuner = false;
        public boolean barcodeModule = false;

        public boolean fingerprint = false;
        public boolean faceUnlock = false;
        public boolean iris = false;
        public boolean underDisplayFingerprint = false;

        public boolean gpsHw = false;
        public boolean networkLocation = false;
        public boolean passiveLocation = false;
    }

    public PeripheralSnapshot readPeripheralSnapshot() {
        PeripheralSnapshot s = new PeripheralSnapshot();

        try {
            PackageManager pm = ctx.getPackageManager();

            NfcManager nm = (NfcManager) ctx.getSystemService(Context.NFC_SERVICE);
            NfcAdapter na = nm != null ? nm.getDefaultAdapter() : null;
            s.nfcSupported = na != null;
            s.nfcEnabled = na != null && na.isEnabled();

            s.uwbSupported = pm.hasSystemFeature("android.hardware.uwb");

            android.os.Vibrator v =
                    (android.os.Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
            s.vibratorPresent = v != null;
            if (v != null && Build.VERSION.SDK_INT >= 29) {
                s.amplitudeControl = v.hasAmplitudeControl();
            }

            s.irBlaster = pm.hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR);
            s.fmRadio = pm.hasSystemFeature("android.hardware.fm");
            s.hallSensor = pm.hasSystemFeature("android.hardware.sensor.hall");
            s.hardwareKeyboard = pm.hasSystemFeature("android.hardware.keyboard");
            s.wirelessCharging = pm.hasSystemFeature("android.hardware.power.wireless_charging");
            s.tvTuner = pm.hasSystemFeature("android.hardware.tv.tuner");
            s.barcodeModule = pm.hasSystemFeature("android.hardware.barcodescanner");

            s.fingerprint = pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
            s.faceUnlock = pm.hasSystemFeature("android.hardware.biometrics.face");
            s.iris = pm.hasSystemFeature("android.hardware.biometrics.iris");

            s.underDisplayFingerprint =
                    pm.hasSystemFeature("com.motorola.hardware.fingerprint.udfps")
                            || pm.hasSystemFeature("com.samsung.hardware.fingerprint.udfps")
                            || pm.hasSystemFeature("com.google.hardware.biometrics.udfps")
                            || pm.hasSystemFeature("vendor.samsung.hardware.biometrics.fingerprint.udfps")
                            || pm.hasSystemFeature("vendor.xiaomi.hardware.fingerprint.udfps");

            s.gpsHw = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
            s.networkLocation = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK);
            s.passiveLocation = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION);

        } catch (Throwable ignore) { }

        return s;
    }

    // ============================================================
    // SYSTEM
    // ============================================================
    public static final class SystemSnapshot {
        public String manufacturer = "N/A";
        public String brand = "N/A";
        public String model = "N/A";
        public String device = "N/A";
        public String product = "N/A";
        public String hardware = "N/A";
        public String board = "N/A";
        public String bootloader = "N/A";
        public String fingerprint = "N/A";
        public String androidId = "N/A";
        public String deviceType = "N/A";
        public String region = "N/A";
        public String vendorName = "N/A";
        public String vbState = "N/A";
        public String vbDevice = "N/A";
        public String flashLock = "N/A";
    }

    public SystemSnapshot readSystemSnapshot() {
        SystemSnapshot s = new SystemSnapshot();

        s.manufacturer = safeNonEmpty(Build.MANUFACTURER, "N/A");
        s.brand = safeNonEmpty(Build.BRAND, "N/A");
        s.model = safeNonEmpty(Build.MODEL, "N/A");
        s.device = safeNonEmpty(Build.DEVICE, "N/A");
        s.product = safeNonEmpty(Build.PRODUCT, "N/A");
        s.hardware = safeNonEmpty(Build.HARDWARE, "N/A");
        s.board = safeNonEmpty(Build.BOARD, "N/A");
        s.bootloader = safeNonEmpty(Build.BOOTLOADER, "N/A");
        s.fingerprint = safeNonEmpty(Build.FINGERPRINT, "N/A");

        try {
            String id = Settings.Secure.getString(
                    ctx.getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );
            s.androidId = safeNonEmpty(id, "N/A");
        } catch (Throwable ignore) { }

        try {
            s.deviceType =
                    ctx.getResources().getConfiguration().smallestScreenWidthDp >= 600
                            ? "Tablet" : "Phone";
        } catch (Throwable ignore) { }

        s.region = safeNonEmpty(getProp("ro.product.locale.region"), "N/A");
        s.vendorName = safeNonEmpty(getProp("ro.product.vendor.name"), "N/A");
        s.vbState = safeNonEmpty(getProp("ro.boot.verifiedbootstate"), "N/A");
        s.vbDevice = safeNonEmpty(getProp("ro.boot.vbmeta.device_state"), "N/A");
        s.flashLock = safeNonEmpty(getProp("ro.boot.flash.locked"), "N/A");

        return s;
    }

    // ============================================================
    // ANDROID
    // ============================================================
    public static final class AndroidSnapshot {
        public String release = "N/A";
        public int sdk = -1;
        public String securityPatch = "N/A";
        public String buildId = "N/A";
        public String buildType = "N/A";
        public String buildTags = "N/A";
        public String incremental = "N/A";
        public String baseband = "N/A";
        public String vendorRelease = "N/A";
    }

    public AndroidSnapshot readAndroidSnapshot() {
        AndroidSnapshot s = new AndroidSnapshot();

        s.release = safeNonEmpty(Build.VERSION.RELEASE, "N/A");
        s.sdk = Build.VERSION.SDK_INT;
        s.securityPatch = safeNonEmpty(Build.VERSION.SECURITY_PATCH, "N/A");
        s.buildId = safeNonEmpty(Build.ID, "N/A");
        s.buildType = safeNonEmpty(Build.TYPE, "N/A");
        s.buildTags = safeNonEmpty(Build.TAGS, "N/A");
        s.incremental = safeNonEmpty(Build.VERSION.INCREMENTAL, "N/A");

        try {
            s.baseband = safeNonEmpty(Build.getRadioVersion(), "N/A");
        } catch (Throwable ignore) { }

        String miui = getProp("ro.miui.ui.version.name");
        if (miui != null && !miui.isEmpty()) {
            s.vendorRelease = miui;
        } else {
            s.vendorRelease = safeNonEmpty(Build.VERSION.BASE_OS, "N/A");
        }

        return s;
    }

    // ============================================================
    // RAW HELPERS
    // ============================================================
    public boolean isDeviceRooted() {
        String[] paths = {
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su",
                "/vendor/bin/su",
                "/system/su",
                "/system/bin/.ext/.su",
                "/system/usr/we-need-root/su-backup",
                "/system/app/Superuser.apk",
                "/system/app/SuperSU.apk",
                "/system/app/Magisk.apk",
                "/system/priv-app/Magisk"
        };

        for (String p : paths) {
            try {
                if (new File(p).exists()) return true;
            } catch (Throwable ignore) { }
        }

        try {
            Process p =
                    Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
            int rc = p.waitFor();
            return rc == 0;
        } catch (Throwable ignore) { }

        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", "which su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = in.readLine();
            in.close();
            return line != null && !line.trim().isEmpty();
        } catch (Throwable ignore) { }

        return false;
    }

    public String readTextFile(String path, int maxLen) {
        BufferedReader br = null;
        try {
            File f = new File(path);
            if (!f.exists()) return null;

            br = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            int read;

            while ((read = br.read(buf)) > 0 && sb.length() < maxLen) {
                sb.append(buf, 0, read);
            }
            return sb.toString();

        } catch (Throwable ignore) {
            return null;
        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception ignore) { }
        }
    }

    public String readSysString(String path) {
        BufferedReader br = null;
        try {
            File f = new File(path);
            if (!f.exists()) return null;

            br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            return line != null ? line.trim() : null;

        } catch (Throwable ignore) {
            return null;
        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception ignore) { }
        }
    }

    public long readSysLong(String path) {
        String s = readSysString(path);
        if (s == null || s.isEmpty()) return -1;
        try {
            return Long.parseLong(s.trim());
        } catch (Throwable ignore) {
            return -1;
        }
    }

    public String getProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null ? line.trim() : "";
        } catch (Throwable ignore) {
            return "";
        }
    }
    
    public long getBestFullCapacityMah() {

    BatterySnapshot b =
            readBatterySnapshot();

    long model =
            getStoredModelCapacity();

    if (model > 0)
        return model;

    if (b.chargeFullMah > 0)
        return b.chargeFullMah;

    if (b.chargeDesignMah > 0)
        return b.chargeDesignMah;

    if (b.chargeNowMah > 0 &&
        b.level > 5) {

        return (long)(
                b.chargeNowMah /
                (b.level / 100f)
        );
    }

    return -1;
}

public int getBatterySOH() {

    BatterySnapshot b =
            readBatterySnapshot();

    long design =
            b.chargeDesignMah;

    long full =
            getBestFullCapacityMah();

    if (design <= 0 ||
        full <= 0)
        return -1;

    return (int)Math.round(
            (full * 100.0) /
            design
    );
}

    // ============================================================
    // ROOT-AWARE HELPERS
    // ============================================================
    public String readSysTextRootAware(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String s = br.readLine();
            br.close();
            if (s != null) return s.trim();
        } catch (Throwable ignore) { }

        Process p = null;
        BufferedReader br = null;
        try {
            p = Runtime.getRuntime().exec(new String[]{"su", "-c", "cat " + path});
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s = br.readLine();
            if (s != null) return s.trim();
        } catch (Throwable ignore) {
        } finally {
            try { if (br != null) br.close(); } catch (Throwable ignore) { }
            try { if (p != null) p.destroy(); } catch (Throwable ignore) { }
        }

        return "";
    }

    public long readSysLongRootAware(String path) {
        String s = readSysTextRootAware(path);
        if (s == null || s.isEmpty()) return -1;

        try {
            return Long.parseLong(s.replaceAll("[^0-9\\-]", ""));
        } catch (Throwable ignore) {
            return -1;
        }
    }

    // ============================================================
    // INTERNAL HELPERS
    // ============================================================
    private enum ThermalGroup {
        CPU, GPU, BATTERY, SKIN, PMIC, CHARGER, MODEM_MAIN, MODEM_AUX
    }

    private List<ThermalReading> scanThermalZones() {
        List<ThermalReading> out = new ArrayList<>();

        File dir = new File("/sys/class/thermal");
        File[] files = dir.listFiles();
        if (files == null) return out;

        for (File f : files) {
            try {
                if (!f.isDirectory()) continue;
                if (!f.getName().startsWith("thermal_zone")) continue;

                String base = f.getAbsolutePath();
                String type = readSysTextRootAware(base + "/type");
                String temp = readSysTextRootAware(base + "/temp");

                float c = normalizeTempC(temp);
                if (Float.isNaN(c)) continue;

                ThermalReading tr = new ThermalReading();
                tr.name = sanitizeName(type, f.getName());
                tr.path = base + "/temp";
                tr.source = "thermal_zone";
                tr.tempC = c;
                tr.valid = true;
                out.add(tr);

            } catch (Throwable ignore) { }
        }

        return out;
    }

    private List<ThermalReading> scanHwmonTempsRootAware() {
        List<ThermalReading> out = new ArrayList<>();

        File hwmonRoot = new File("/sys/class/hwmon");
        File[] hwmons = hwmonRoot.listFiles();
        if (hwmons == null) return out;

        for (File hw : hwmons) {
            try {
                if (!hw.isDirectory()) continue;

                String hwName = readSysTextRootAware(hw.getAbsolutePath() + "/name");
                File[] children = hw.listFiles();
                if (children == null) continue;

                for (File child : children) {
                    try {
                        String n = child.getName();
                        if (!n.startsWith("temp") || !n.endsWith("_input")) continue;

                        String idx = n.substring(4, n.length() - 6);
                        String label = readSysTextRootAware(hw.getAbsolutePath() + "/temp" + idx + "_label");
                        String raw = readSysTextRootAware(child.getAbsolutePath());

                        float c = normalizeTempC(raw);
                        if (Float.isNaN(c)) continue;

                        ThermalReading tr = new ThermalReading();
                        tr.name = sanitizeName(joinNonEmpty(hwName, label), child.getName());
                        tr.path = child.getAbsolutePath();
                        tr.source = "hwmon";
                        tr.tempC = c;
                        tr.valid = true;

                        out.add(tr);

                    } catch (Throwable ignore) { }
                }
            } catch (Throwable ignore) { }
        }

        return out;
    }

    private List<ThermalReading> dedupeReadings(List<ThermalReading> in) {
        List<ThermalReading> out = new ArrayList<>();

        for (ThermalReading r : in) {
            if (r == null || !r.valid) continue;

            boolean exists = false;
            for (ThermalReading x : out) {
                if (safeEq(x.path, r.path)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) out.add(r);
        }

        return out;
    }

    private ThermalReading selectMax(List<ThermalReading> all, ThermalGroup g) {
        ThermalReading best = new ThermalReading();
        float max = Float.NEGATIVE_INFINITY;

        for (ThermalReading r : all) {
            if (r == null || !r.valid) continue;
            if (!belongsToGroup(r.name, g)) continue;

            if (r.tempC > max) {
                max = r.tempC;
                best = r;
            }
        }

        if (!best.valid) return new ThermalReading();
        return best;
    }

    private boolean belongsToGroup(String rawName, ThermalGroup g) {
        String n = normalizeToken(rawName);

        switch (g) {
            case CPU:
                return isCpuName(n);
            case GPU:
                return isGpuName(n);
            case BATTERY:
                return isBatteryName(n);
            case SKIN:
                return isSkinName(n);
            case PMIC:
                return isPmicName(n);
            case CHARGER:
                return isChargerName(n);
            case MODEM_MAIN:
                return isModemMainName(n);
            case MODEM_AUX:
                return isModemAuxName(n);
            default:
                return false;
        }
    }

    private boolean isCpuName(String n) {
        if (isGpuName(n) || isBatteryName(n) || isSkinName(n) || isPowerName(n)) return false;
        return containsAny(n,
                "cpu", "cluster", "big", "little", "ap",
                "applicationprocessor", "cpu-therm", "cpu_therm",
                "mtktscpu", "msm_therm", "soc", "soc-therm",
                "soc_therm", "tsens", "silver", "gold", "prime"
        );
    }

    private boolean isGpuName(String n) {
        return containsAny(n, "gpu", "kgsl", "gfx", "mali", "adreno", "gpuss");
    }

    private boolean isBatteryName(String n) {
        return containsAny(n, "battery", "batt", "battherm", "batterytherm", "bms");
    }

    private boolean isSkinName(String n) {
        return containsAny(n, "skin", "case", "surface", "shell", "backlight");
    }

    private boolean isPmicName(String n) {
        return containsAny(n, "pmic", "pm8998", "pm8150", "pmx", "pm7250", "pm6450", "ibat", "bcl");
    }

    private boolean isChargerName(String n) {
        return containsAny(n, "charger", "chg", "usbtherm", "chargepump", "charge");
    }

    private boolean isModemMainName(String n) {
        return containsAny(n,
                "modem", "mdm", "mdmss", "rftherm", "rf", "modempa",
                "pa_therm", "pa0", "pa1", "pa2", "modemcfg"
        );
    }

    private boolean isModemAuxName(String n) {
        return containsAny(n, "modem1", "mdm1", "mdm2", "rf1", "modemsub", "sub1modemcfg");
    }

    private boolean isPowerName(String n) {
        return containsAny(n,
                "charger", "usb", "display", "panel", "flash", "wlan", "wifi", "modem"
        );
    }

    private List<String> scanHardwareCoolingDevices() {
        List<String> out = new ArrayList<>();

        File thermalDir = new File("/sys/class/thermal");
        File[] cools = null;

        try {
            if (thermalDir.exists() && thermalDir.isDirectory()) {
                cools = thermalDir.listFiles(f -> f.getName().startsWith("cooling_device"));
            }
        } catch (Throwable ignore) { }

        if (cools != null) {
            for (File c : cools) {
                try {
                    String type = readSysTextRootAware(c.getAbsolutePath() + "/type");
                    if (isHardwareCoolingDevice(type)) {
                        out.add(c.getName() + " → " + type);
                    }
                } catch (Throwable ignore) { }
            }
        }

        return out;
    }

    private boolean isHardwareCoolingDevice(String rawType) {
        if (rawType == null) return false;
        String t = rawType.toLowerCase(Locale.US);

        if (t.contains("fan")) return true;
        if (t.contains("cooling_fan")) return true;
        if (t.contains("blower")) return true;
        if (t.contains("pump")) return true;
        if (t.contains("heatsink")) return true;
        if (t.contains("radiator")) return true;
        if (t.contains("cooling_module")) return true;

        if (t.contains("skin")) return false;
        if (t.contains("hotspot")) return false;
        if (t.contains("virtual")) return false;

        return false;
    }

    private StorageBlock buildStorageBlock(String label, File path) {
        StorageBlock b = new StorageBlock();
        b.label = label;

        if (path == null) return b;

        try {
            StatFs stat = new StatFs(path.getAbsolutePath());

            long blockSize;
            long totalBlocks;
            long availBlocks;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
                availBlocks = stat.getAvailableBlocksLong();
            } else {
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
                availBlocks = stat.getAvailableBlocks();
            }

            long totalBytes = blockSize * totalBlocks;
            long freeBytes = blockSize * availBlocks;
            long usedBytes = totalBytes - freeBytes;

            b.path = path.getAbsolutePath();
            b.totalBytes = totalBytes;
            b.freeBytes = freeBytes;
            b.usedBytes = usedBytes;

            b.totalGb = totalBytes / (1024L * 1024L * 1024L);
            b.freeGb = freeBytes / (1024L * 1024L * 1024L);
            b.usedGb = usedBytes / (1024L * 1024L * 1024L);

        } catch (Throwable ignore) { }

        return b;
    }

    private long readBatteryCycleCountRoot() {

    String[] paths = {

        "/sys/class/power_supply/battery/cycle_count",
        "/sys/class/power_supply/bms/cycle_count",
        "/sys/class/power_supply/maxfg/cycle_count",
        "/sys/class/power_supply/fg/cycle_count",

        profile != null
                ? profile.batteryCyclePath
                : null
};

    for (String p : paths) {

        if (p == null)
            continue;

        long v =
                readSysLongRootAware(p);

        if (v > 0)
            return v;
    }

    return -1;
}

    private long readBatteryResistanceRoot() {

    String[] paths = {
            "/sys/class/power_supply/battery/resistance",
            "/sys/class/power_supply/battery/resistance_now",
            "/sys/class/power_supply/bms/resistance",
            "/sys/class/power_supply/maxfg/resistance"
    };

    for (String p : paths) {
        long v = readSysLongRootAware(p);
        if (v > 0) return v;
    }

    // PROFILE FALLBACK
    if (profile != null && profile.batteryResistancePath != null) {

        long v = readSysLongRootAware(profile.batteryResistancePath);

        if (v > 0) return v;
    }

    return -1;
}

    private long normalizeMah(long raw) {
        if (raw <= 0) return -1;
        if (raw > 200000) return raw / 1000;
        return raw;
    }

    private float normalizeCurrentMa(long raw) {
        if (raw == Long.MIN_VALUE || raw == 0) return Float.NaN;

        float current = (float) raw;
        if (Math.abs(raw) > 10000L) current = current / 1000f;

        if (Math.abs(current) > 100000f) return Float.NaN;
        return current;
    }

    private float normalizeVoltageMv(long raw) {
        if (raw <= 0) return Float.NaN;
        if (raw > 100000L) return raw / 1000f;
        return raw;
    }

    private float normalizeTempC(String raw) {
        if (raw == null) return Float.NaN;

        String s = raw.trim();
        if (s.isEmpty()) return Float.NaN;

        try {
            float v = Float.parseFloat(s);

            if (Math.abs(v) >= 1000f) v = v / 1000f;
            else if (Math.abs(v) > 200f) v = v / 10f;

            if (v < -30f || v > 150f) return Float.NaN;
            return v;

        } catch (Throwable ignore) {
            return Float.NaN;
        }
    }

    private boolean containsAny(String n, String... keys) {
        if (n == null) return false;
        for (String k : keys) {
            if (n.contains(k.replace("-", "").replace("_", ""))) return true;
        }
        return false;
    }

    private String normalizeToken(String s) {
        if (s == null) return "";
        return s.toLowerCase(Locale.US)
                .replace(" ", "")
                .replace("_", "")
                .replace("-", "")
                .replace("/", "");
    }

    private String sanitizeName(String preferred, String fallback) {
        String p = preferred != null ? preferred.trim() : "";
        if (!p.isEmpty()) return p;
        return fallback != null ? fallback : "N/A";
    }

    private String joinNonEmpty(String a, String b) {
        String x = a != null ? a.trim() : "";
        String y = b != null ? b.trim() : "";
        if (!x.isEmpty() && !y.isEmpty()) return x + " / " + y;
        if (!x.isEmpty()) return x;
        if (!y.isEmpty()) return y;
        return "";
    }

    private boolean safeEq(String a, String b) {
        if (a == null) return b == null;
        return a.equals(b);
    }

    private String joinAbis() {
        StringBuilder abi = new StringBuilder();
        try {
            if (Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0) {
                for (int i = 0; i < Build.SUPPORTED_ABIS.length; i++) {
                    if (i > 0) abi.append(", ");
                    abi.append(Build.SUPPORTED_ABIS[i]);
                }
            } else {
                abi.append(Build.CPU_ABI);
            }
        } catch (Throwable ignore) {
            return "N/A";
        }
        return abi.toString();
    }

    private String safeNonEmpty(String s, String fallback) {
        return (s == null || s.trim().isEmpty()) ? fallback : s.trim();
    }

    private String firstNonEmpty(String... values) {
        if (values == null) return "N/A";
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) return v.trim();
        }
        return "N/A";
    }

    private long parseKb(String rawLine) {
        try {
            return Long.parseLong(rawLine.replaceAll("[^0-9]", ""));
        } catch (Throwable ignore) {
            return 0;
        }
    }

    private String describeWifiBand(int freq) {
        if (freq >= 2400 && freq < 2500) return "2.4 GHz";
        if (freq >= 4900 && freq < 5900) return "5 GHz";
        if (freq >= 5925 && freq < 7125) return "6 GHz";
        return "Unknown";
    }

    private String describeNetworkType(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "2G (GPRS)";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "2G (EDGE)";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "3G (UMTS)";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "3G (HSDPA)";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "3G (HSUPA)";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "3G (HSPA)";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G LTE";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G NR";
            default:
                return "Unknown";
        }
    }
    
// ============================================================
// PROFILE-AWARE THERMAL FALLBACK
// ============================================================

private ThermalReading readThermalFromProfilePath(
        String profilePath,
        String label
) {

    ThermalReading tr = new ThermalReading();

    if (profile == null || profilePath == null || profilePath.trim().isEmpty())
        return tr;

    String raw = readSysTextRootAware(profilePath);
    float c = normalizeTempC(raw);

    if (Float.isNaN(c))
        return tr;

    tr.name = label != null ? label : "Profile Thermal";
    tr.path = profilePath;
    tr.source = "device_profile";
    tr.tempC = c;
    tr.valid = true;

    return tr;
}

// ============================================================
// PROFILE-AWARE BATTERY PATH
// ============================================================

private String readBatteryPathProfileAware(
        String universalPath,
        String profilePath
) {

    String v = readSysTextRootAware(universalPath);

    if (v != null && !v.isEmpty())
        return v;

    if (profile != null && profilePath != null) {

        v = readSysTextRootAware(profilePath);

        if (v != null && !v.isEmpty())
            return v;
    }

    return null;
}
    
// ============================================================
// PROFILE-AWARE READ
// ============================================================

private String readPathWithProfile(
        String universal,
        String profilePath
) {

    String v = readSysString(universal);

    if (v != null && !v.isEmpty())
        return v;

    if (profile != null && profilePath != null) {

        v = readSysString(profilePath);

        if (v != null && !v.isEmpty())
            return v;
    }

    return null;
}

// ============================================================
// FUEL GAUGE RESOLVER
// ============================================================

private String fuelGaugeNode = null;

private String resolveFuelGaugeNode() {

    if (fuelGaugeNode != null)
        return fuelGaugeNode;

    String[] nodes = {

            "/sys/class/power_supply/battery",
            "/sys/class/power_supply/bms",
            "/sys/class/power_supply/maxfg",
            "/sys/class/power_supply/fg"
    };

    for (String n : nodes) {

        try {

            File f = new File(n + "/voltage_now");

            if (f.exists()) {

                long v =
                        readSysLongRootAware(
                                n + "/voltage_now"
                        );

                if (v > 0) {

                    fuelGaugeNode = n;
                    return fuelGaugeNode;
                }
            }

        } catch (Throwable ignore) {}
    }

    return null;
}

// ============================================================
// SYSFS POWER SUPPLY SCAN
// ============================================================

private String[] cachedPowerNodes;

private String[] getPowerSupplyNodes() {

    if (cachedPowerNodes != null)
        return cachedPowerNodes;

    try {

        File dir =
                new File("/sys/class/power_supply");

        File[] list = dir.listFiles();

        if (list == null)
            return null;

        ArrayList<String> nodes =
                new ArrayList<>();

        for (File f : list) {

            if (f == null)
                continue;

            String p = f.getAbsolutePath();

            nodes.add(p);
        }

        cachedPowerNodes =
                nodes.toArray(new String[0]);

        return cachedPowerNodes;

    } catch (Throwable ignore) {}

    return null;
}

// ============================================================
// MULTI PATH BATTERY READ
// ============================================================

private long readBatteryValueMulti(String[] paths) {

    if (paths == null)
        return -1;

    for (String p : paths) {

        if (p == null)
            continue;

        long v = readSysLongRootAware(p);

        if (v > 0)
            return v;
    }

    return -1;
}

// ============================================================
// AUTO READ FROM ANY POWER NODE
// ============================================================

private long readBatteryValueAuto(String file) {

    try {

        File dir =
                new File("/sys/class/power_supply");

        File[] list = dir.listFiles();

        if (list == null)
            return -1;

        for (File f : list) {

            if (f == null)
                continue;

            try {

                String path =
                        f.getAbsolutePath()
                        + "/" + file;

                long v =
                        readSysLongRootAware(path);

                if (v > 0)
                    return v;

            } catch (Throwable ignore) {}
        }

    } catch (Throwable ignore) {}

    return -1;
}

// ============================================================
// FUEL GAUGE READ (node + auto scan)
// ============================================================

private long readFuelGaugeValue(String file) {

    String node =
            resolveFuelGaugeNode();

    if (node != null) {

        long v =
                readSysLongRootAware(
                        node + "/" + file
                );

        if (v > 0)
            return v;
    }

    // fallback → scan all nodes

    long auto =
            readBatteryValueAuto(file);

    if (auto > 0)
        return auto;

    return -1;
}

// ============================================================
// DEBUG POWER SUPPLY DUMP
// ============================================================

public void debugDumpPowerSupply() {

    try {

        String[] nodes =
                getPowerSupplyNodes();

        if (nodes == null)
            return;

        for (String n : nodes) {

            log("PS NODE: " + n);

            String[] files = {

                    "voltage_now",
                    "current_now",
                    "charge_now",
                    "charge_full",
                    "temp",
                    "cycle_count",
                    "capacity",
                    "status"
            };

            for (String f : files) {

                try {

                    long v =
                            readSysLongRootAware(
                                    n + "/" + f
                            );

                    log("   " + f + " = " + v);

                } catch (Throwable ignore) {}
            }
        }

    } catch (Throwable ignore) {}
}

private void log(String s) {
    android.util.Log.d("iDoctorEngine", s);
}

}
