// DeviceInfoPeripheralsActivity.java — MEGA UPGRADE v30
// Auto-Path Engine 5.3 + Root v5.1 + Permission Engine v25 (Manifest-Aware + Debug v24)

package com.gel.cleaner;

// ============================================================
// JAVA / UTIL
// ============================================================
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;

// ============================================================
// ANDROID CORE
// ============================================================
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;

// ============================================================
// ANDROID UI / VIEW
// ============================================================
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.ImageFormat;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.util.Size;
import android.util.Range;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// ============================================================
// ANDROID MEDIA / AUDIO (MIC BENCH + LIVE MIC)
// ============================================================
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;

// ============================================================
// ANDROID CONNECTIVITY
// ============================================================
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

// ============================================================
// ANDROID BLUETOOTH
// ============================================================
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;

// ============================================================
// ANDROID HARDWARE
// ============================================================
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.hardware.camera2.params.StreamConfigurationMap;

// ============================================================
// ANDROID LOCATION / NFC / TELEPHONY
// ============================================================
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.telecom.TelecomManager;
import android.telephony.CellInfo;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

// ============================================================
// ANDROIDX
// ============================================================
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Set;

// ============================================================
// STATIC
// ============================================================
import static android.content.Context.MODE_PRIVATE;

public class DeviceInfoPeripheralsActivity extends GELAutoActivityHook {

// ============================================================  
// GEL Permission Request Engine v1.0 — Option B (Auto Request All)  
// ============================================================  
private static final String[] PERMISSIONS_ALL = new String[]{  
        Manifest.permission.CAMERA,  
        Manifest.permission.RECORD_AUDIO,  
        Manifest.permission.ACCESS_FINE_LOCATION,  
        Manifest.permission.ACCESS_COARSE_LOCATION,  
        Manifest.permission.BLUETOOTH_SCAN,  
        Manifest.permission.BLUETOOTH_CONNECT,  
        Manifest.permission.NEARBY_WIFI_DEVICES  
};  

private static final int REQ_CODE_GEL_PERMISSIONS = 7777;  

private void requestAllRuntimePermissions() {  

    if (Build.VERSION.SDK_INT < 23) return;  

    java.util.List<String> toRequest = new java.util.ArrayList<>();  

    for (String p : PERMISSIONS_ALL) {  
        if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {  
            toRequest.add(p);  
        }  
    }  

    if (!toRequest.isEmpty()) {  
        requestPermissions(toRequest.toArray(new String[0]), REQ_CODE_GEL_PERMISSIONS);  
    }  
}  

// ============================================================  
// MAIN CLASS FIELDS  
// ============================================================  
private static final String NEON_GREEN = "#39FF14";  
private static final String GOLD_COLOR = "#FFD700";  
private static final int LINK_BLUE     = Color.parseColor("#1E90FF");  

private boolean isRooted = false;  

private View[] allContents;
private TextView[] allIcons;

// ============================================================
// SECTION FIELDS
// ============================================================
private LinearLayout batteryContainer;
private TextView txtBatteryContent;
private TextView iconBattery;
private TextView txtBatteryModelCapacity;

private TextView txtScreenContent;
private TextView txtCameraContent;
private TextView txtConnectivityContent;  
private TextView txtLocationContent;
private TextView txtThermalContent;
private TextView txtModemContent;
private TextView txtWifiAdvancedContent;
private TextView txtAudioUnifiedContent;
private TextView txtSensorsContent;
private TextView txtBiometricsContent;
private TextView txtNfcContent;
private TextView txtGnssContent;
private TextView txtUwbContent;
private TextView txtUsbContent;
private TextView txtHapticsContent;
private TextView txtSystemFeaturesContent;
private TextView txtSecurityFlagsContent;
private TextView txtRootContent;
private TextView txtOtherPeripherals;

private TextView iconScreen;
private TextView iconCamera;
private TextView iconConnectivity;
private TextView iconLocation;
private TextView iconThermal;
private TextView iconModem;
private TextView iconWifiAdvanced;
private TextView iconAudioUnified;
private TextView iconSensors;
private TextView iconBiometrics;
private TextView iconNfc;
private TextView iconGnss;
private TextView iconUwb;
private TextView iconUsb;
private TextView iconHaptics;
private TextView iconSystemFeatures;
private TextView iconSecurityFlags;
private TextView iconRoot;
private TextView iconOther;

// ============================================================
// TELEPHONY SNAPSHOT — GEL SINGLE SOURCE OF TRUTH
// ============================================================
private static class TelephonySnapshot {

boolean airplaneOn = false;  

    int simState = TelephonyManager.SIM_STATE_UNKNOWN;  
    boolean simReady = false;  

    int serviceState = ServiceState.STATE_OUT_OF_SERVICE;  
    boolean inService = false;  

    int dataState = TelephonyManager.DATA_UNKNOWN;  
}

// ============================================================
// attachBaseContext
// ============================================================
@Override
protected void attachBaseContext(Context base) {
super.attachBaseContext(LocaleHelper.apply(base));
}

// ============================================================
//  ON CREATE — FINAL CLEAN (AUDIO INCLUDED, NO LABS)
// ============================================================
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_device_info_peripherals);
        
    UIHelpers.applyPressEffectRecursive(getWindow().getDecorView());

    // ✅ ROOT FLAG — MUST BE HERE
    isRooted = isDeviceRooted();

// ------------------------------------------------------------  
// 1️⃣  TITLE  
// ------------------------------------------------------------  
TextView title = findViewById(R.id.txtTitleDevice);  
if (title != null)  
    title.setText(getString(R.string.phone_info_peripherals));  

// ------------------------------------------------------------  
// 2️⃣  BIND VIEWS (FULL UI READY)  
// ------------------------------------------------------------  
batteryContainer        = findViewById(R.id.batteryContainer);  
txtBatteryContent       = findViewById(R.id.txtBatteryContent);  
iconBattery             = findViewById(R.id.iconBatteryToggle);  
txtBatteryModelCapacity = findViewById(R.id.txtBatteryModelCapacity);  
initBatterySection();

txtScreenContent          = findViewById(R.id.txtScreenContent);  
txtCameraContent          = findViewById(R.id.txtCameraContent);  
txtConnectivityContent    = findViewById(R.id.txtConnectivityContent);  
txtLocationContent        = findViewById(R.id.txtLocationContent);  
txtThermalContent         = findViewById(R.id.txtThermalContent);  
txtModemContent           = findViewById(R.id.txtModemContent);  
txtWifiAdvancedContent    = findViewById(R.id.txtWifiAdvancedContent);  
txtAudioUnifiedContent    = findViewById(R.id.txtAudioUnifiedContent);  

txtSensorsContent         = findViewById(R.id.txtSensorsContent);  
txtBiometricsContent      = findViewById(R.id.txtBiometricsContent);  
txtNfcContent             = findViewById(R.id.txtNfcContent);  
txtGnssContent            = findViewById(R.id.txtGnssContent);  
txtUwbContent             = findViewById(R.id.txtUwbContent);  
txtUsbContent             = findViewById(R.id.txtUsbContent);  
txtHapticsContent         = findViewById(R.id.txtHapticsContent);  
txtSystemFeaturesContent  = findViewById(R.id.txtSystemFeaturesContent);  
txtSecurityFlagsContent   = findViewById(R.id.txtSecurityFlagsContent);  
txtRootContent            = findViewById(R.id.txtRootContent);  
txtOtherPeripherals       = findViewById(R.id.txtOtherPeripheralsContent);  

iconScreen          = findViewById(R.id.iconScreenToggle);  
iconCamera          = findViewById(R.id.iconCameraToggle);  
iconConnectivity    = findViewById(R.id.iconConnectivityToggle);  
iconLocation        = findViewById(R.id.iconLocationToggle);  
iconThermal         = findViewById(R.id.iconThermalToggle);  
iconModem           = findViewById(R.id.iconModemToggle);  
iconWifiAdvanced    = findViewById(R.id.iconWifiAdvancedToggle);  
iconAudioUnified    = findViewById(R.id.iconAudioUnifiedToggle);  

iconSensors         = findViewById(R.id.iconSensorsToggle);  
iconBiometrics      = findViewById(R.id.iconBiometricsToggle);  
iconNfc             = findViewById(R.id.iconNfcToggle);  
iconGnss            = findViewById(R.id.iconGnssToggle);  
iconUwb             = findViewById(R.id.iconUwbToggle);  
iconUsb             = findViewById(R.id.iconUsbToggle);  
iconHaptics         = findViewById(R.id.iconHapticsToggle);  
iconSystemFeatures  = findViewById(R.id.iconSystemFeaturesToggle);  
iconSecurityFlags   = findViewById(R.id.iconSecurityFlagsToggle);  
iconRoot            = findViewById(R.id.iconRootToggle);  
iconOther           = findViewById(R.id.iconOtherPeripheralsToggle);  

// ------------------------------------------------------------  
// 3️⃣  MASTER ARRAYS (WITH AUDIO)  
// ------------------------------------------------------------  
allContents = new View[]{
        batteryContainer,            // ✅ Battery content = container
        txtScreenContent,
        txtCameraContent,
        txtConnectivityContent,
        txtLocationContent,
        txtThermalContent,
        txtModemContent,
        txtWifiAdvancedContent,
        txtAudioUnifiedContent,
        txtSensorsContent,
        txtBiometricsContent,
        txtNfcContent,
        txtGnssContent,
        txtUwbContent,
        txtUsbContent,
        txtHapticsContent,
        txtSystemFeaturesContent,
        txtSecurityFlagsContent,
        txtRootContent,
        txtOtherPeripherals
};

allIcons = new TextView[]{  
        iconBattery,  
        iconScreen,  
        iconCamera,  
        iconConnectivity,  
        iconLocation,  
        iconThermal,  
        iconModem,  
        iconWifiAdvanced,  
        iconAudioUnified,  
        iconSensors,  
        iconBiometrics,  
        iconNfc,  
        iconGnss,  
        iconUwb,  
        iconUsb,  
        iconHaptics,  
        iconSystemFeatures,  
        iconSecurityFlags,  
        iconRoot,  
        iconOther  
};  

// ------------------------------------------------------------  
// 4️⃣  LOAD ALL SECTION TEXTS (LIGHT ONLY)  
// ------------------------------------------------------------  
populateAllSections();  

// ------------------------------------------------------------  
// 5️⃣  PERMISSIONS  
// ------------------------------------------------------------  
requestAllRuntimePermissions();  
requestPermissions(new String[]{  
        Manifest.permission.READ_PHONE_STATE,  
        Manifest.permission.READ_SMS,  
        Manifest.permission.READ_PHONE_NUMBERS  
}, 101);  

// ============================================================
// 5️⃣ BATTERY — MANUAL SECTION (FINAL, FIXED)
// ============================================================
LinearLayout headerBattery = findViewById(R.id.headerBattery);

if (headerBattery != null && batteryContainer != null) {
    headerBattery.setOnClickListener(v -> {

        boolean isOpen = batteryContainer.getVisibility() == View.VISIBLE;

        // 🔻 Κλείσε όλα τα άλλα sections
        if (allContents != null && allIcons != null) {
            for (int i = 1; i < allContents.length; i++) {
                if (allContents[i] != null)
                    allContents[i].setVisibility(View.GONE);
                if (allIcons[i] != null)
                    allIcons[i].setText("+");
            }
        }

        if (!isOpen) {
            // 🔺 ΑΝΟΙΓΜΑ BATTERY (ΚΑΙ ΕΝΕΡΓΟΠΟΙΗΣΗ)
            batteryContainer.setVisibility(View.VISIBLE);
            batteryContainer.setClickable(true);
            batteryContainer.setFocusable(true);
            batteryContainer.setFocusableInTouchMode(true);

            iconBattery.setText("-");
        } else {
            // 🔻 ΠΛΗΡΕΣ ΚΛΕΙΣΙΜΟ BATTERY
            batteryContainer.setVisibility(View.GONE);
            iconBattery.setText("+");
        }
    });
}

// ------------------------------------------------------------  
// 7️⃣  NORMAL SECTIONS (WITH AUDIO)  
// ------------------------------------------------------------  

setupSection(findViewById(R.id.headerScreen), txtScreenContent, iconScreen);  
setupSection(findViewById(R.id.headerCamera), txtCameraContent, iconCamera);  
setupSection(findViewById(R.id.headerConnectivity), txtConnectivityContent, iconConnectivity);  
setupSection(findViewById(R.id.headerLocation), txtLocationContent, iconLocation);  
setupSection(findViewById(R.id.headerThermal), txtThermalContent, iconThermal);  
setupSection(findViewById(R.id.headerModem), txtModemContent, iconModem);  
setupSection(findViewById(R.id.headerWifiAdvanced), txtWifiAdvancedContent, iconWifiAdvanced);  
setupSection(findViewById(R.id.headerAudioUnified), txtAudioUnifiedContent, iconAudioUnified);  
setupSection(findViewById(R.id.headerSensors), txtSensorsContent, iconSensors);  
setupSection(findViewById(R.id.headerBiometrics), txtBiometricsContent, iconBiometrics);  
setupSection(findViewById(R.id.headerNfc), txtNfcContent, iconNfc);  
setupSection(findViewById(R.id.headerGnss), txtGnssContent, iconGnss);  
setupSection(findViewById(R.id.headerUwb), txtUwbContent, iconUwb);  
setupSection(findViewById(R.id.headerUsb), txtUsbContent, iconUsb);  
setupSection(findViewById(R.id.headerHaptics), txtHapticsContent, iconHaptics);  
setupSection(findViewById(R.id.headerSystemFeatures), txtSystemFeaturesContent, iconSystemFeatures);  
setupSection(findViewById(R.id.headerSecurityFlags), txtSecurityFlagsContent, iconSecurityFlags);  
setupSection(findViewById(R.id.headerRoot), txtRootContent, iconRoot);  
setupSection(findViewById(R.id.headerOtherPeripherals), txtOtherPeripherals, iconOther);

}

// 🔥 END onCreate()

private void appendAccessInstructions(StringBuilder sb, String type) {
    if (sb == null) return;

    sb.append("\n");
    sb.append("Access Info       : ");

    switch (type) {
        case "camera":
            sb.append("Camera permission required\n");
            sb.append("Settings → Apps → Permissions → Camera\n");
            break;

        case "sensors":
            sb.append("Sensor access is system managed\n");
            break;

        default:
            sb.append("Additional permissions may be required\n");
    }
}

private String thermalState(float tempC) {
    if (tempC < 30f) return "COOL";
    if (tempC < 45f) return "NORMAL";
    if (tempC < 60f) return "WARM";
    if (tempC < 75f) return "HOT";
    return "CRITICAL";
}

private String buildSection(String title, Map<String, String> data) {

    StringBuilder sb = new StringBuilder();

    if (title != null && !title.isEmpty()) {
        sb.append("\n").append(title).append(":\n");
    }

    int max = 0;

    for (String key : data.keySet()) {
        if (key.length() > max) max = key.length();
    }

    for (Map.Entry<String, String> e : data.entrySet()) {

        sb.append(String.format(
                Locale.US,
                "  %-" + max + "s : %s\n",
                e.getKey(),
                e.getValue()
        ));
    }

    return sb.toString();
}

private String formatThermalLineClean(ThermalGroupReading t) {
    if (t == null || !t.valid) return "N/A";

    return String.format(Locale.US,
            "%.1f°C (%s)",
            t.tempC,
            thermalState(t.tempC));
}

// ============================================================
// CONNECTIVITY INFO — SNAPSHOT BASED (FIXED)
// ============================================================
private String buildConnectivityInfo() {

    TelephonySnapshot s = getTelephonySnapshot();
    StringBuilder sb = new StringBuilder();

    // ------------------------------------------------
    // 🔴 BUILD VALUES (όχι append εδώ)
    // ------------------------------------------------

    String simStateStr;
    switch (s.simState) {
        case TelephonyManager.SIM_STATE_READY:
            simStateStr = "READY";
            break;
        case TelephonyManager.SIM_STATE_ABSENT:
            simStateStr = "ABSENT";
            break;
        case TelephonyManager.SIM_STATE_PIN_REQUIRED:
            simStateStr = "PIN REQUIRED";
            break;
        case TelephonyManager.SIM_STATE_PUK_REQUIRED:
            simStateStr = "PUK REQUIRED";
            break;
        case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
            simStateStr = "NETWORK LOCKED";
            break;
        default:
            simStateStr = "UNKNOWN";
            break;
    }

    String dataStateStr;
    switch (s.dataState) {
        case TelephonyManager.DATA_CONNECTED:
            dataStateStr = "CONNECTED";
            break;
        case TelephonyManager.DATA_CONNECTING:
            dataStateStr = "CONNECTING";
            break;
        case TelephonyManager.DATA_DISCONNECTED:
            dataStateStr = "DISCONNECTED";
            break;
        default:
            dataStateStr = "UNKNOWN";
            break;
    }

    // ------------------------------------------------
    // 🔴 MAP → BUILDER
    // ------------------------------------------------

    Map<String, String> conn = new LinkedHashMap<>();

    conn.put("Airplane Mode", s.airplaneOn ? "ON" : "OFF");
    conn.put("SIM State", simStateStr);
    conn.put("Mobile Service", s.inService ? "IN SERVICE" : "OUT OF SERVICE");
    conn.put("Mobile Data", dataStateStr);

    sb.append(buildSection("Connectivity", conn));

    // ------------------------------------------------
    // 🔴 WIFI + BT (μένει όπως είναι)
    // ------------------------------------------------

    sb.append(buildWifiAndBluetoothInfo());

    return sb.toString();
}

private TelephonySnapshot getTelephonySnapshot() {

    TelephonySnapshot s = new TelephonySnapshot();

    try {
        s.airplaneOn = Settings.Global.getInt(
                getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON,
                0
        ) == 1;
    } catch (Throwable ignore) {}

    TelephonyManager tm =
            (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

    if (tm != null) {

        try {
            s.simState = tm.getSimState();
            s.simReady = (s.simState == TelephonyManager.SIM_STATE_READY);
        } catch (Throwable ignore) {}

        try {
            ServiceState ss = tm.getServiceState();
            if (ss != null) {
                s.serviceState = ss.getState();
                s.inService = (s.serviceState == ServiceState.STATE_IN_SERVICE);
            }
        } catch (Throwable ignore) {}

        try {
            s.dataState = tm.getDataState();
        } catch (Throwable ignore) {}
    }

    return s;
}

private float getBatteryVoltageFiltered() {

    try {

        iDoctorEngine eng =
                iDoctorEngine.get(getApplicationContext());

        iDoctorEngine.FullSnapshot s =
                eng.readFullSnapshot();

        if (s != null && s.battery != null) {

            if (s.battery.voltageMv > 0)
                return s.battery.voltageMv / 1000f;

        }

    } catch (Throwable ignore) {}

    return Float.NaN;
}

private float getBatteryCurrentNowSafe() {

    try {

        iDoctorEngine eng =
                iDoctorEngine.get(getApplicationContext());

        iDoctorEngine.FullSnapshot s =
                eng.readFullSnapshot();

        if (s != null && s.battery != null)
            return s.battery.currentMa;

    } catch (Throwable ignore) {}

    return Float.NaN;
}

// ============================================================
//  PERMISSION CALLBACK — FINAL CLEAN VERSION
// ============================================================
@Override
public void onRequestPermissionsResult(int requestCode,
@NonNull String[] permissions,
@NonNull int[] grantResults) {
super.onRequestPermissionsResult(requestCode, permissions, grantResults);

// 🔹 GEL universal permissions  
if (requestCode == REQ_CODE_GEL_PERMISSIONS) {  
    // Δεν χρειάζεται κάτι άλλο εδώ προς το παρόν  
}  

// 🔹 TELEPHONY permissions (Active SIMs, IMSI, MSISDN)  
if (requestCode == 101) {  
    refreshModemInfo();   // Ξαναφορτώνει SIM + Modem block  
}

}

// ============================================================
// GEL Section Setup Engine — UNIVERSAL VERSION (Accordion Mode)
// Battery-Safe Edition (FINAL, FIXED — NO AUDIO)
// ============================================================
private void setupSection(View header, View content, TextView icon) {

    if (header == null || content == null || icon == null)
        return;

    // αρχική κατάσταση
    content.setVisibility(View.GONE);
    icon.setText("+"); // ΜΗΝ βάζεις unicode, μόνο ASCII

    header.setOnClickListener(v -> {

        boolean isOpen = (content.getVisibility() == View.VISIBLE);

        // ------------------------------------------------------------
        // 1️⃣ Κλείσε ΟΛΑ τα sections
        // ------------------------------------------------------------
        if (allContents != null && allIcons != null) {
            for (int i = 0; i < allContents.length; i++) {
                if (allContents[i] != null)
                    allContents[i].setVisibility(View.GONE);
                if (allIcons[i] != null)
                    allIcons[i].setText("+");
            }
        }

        // ------------------------------------------------------------
        // 2️⃣ Αν ήταν κλειστό → άνοιξέ το
        // ------------------------------------------------------------
        if (!isOpen) {
            content.setVisibility(View.VISIBLE);
            icon.setText("-");
        }
    });
}

    // ============================================================
    // ROOT CHECK (GEL Stable v5.1) — FIXED
    // ============================================================
    private boolean isDeviceRooted() {
        try {
            String[] paths = {
                    "/system/bin/su", "/system/xbin/su", "/sbin/su",
                    "/system/su", "/system/bin/.ext/.su",
                    "/system/usr/we-need-root/su-backup",
                    "/system/app/Superuser.apk", "/system/app/SuperSU.apk",
                    "/system/app/Magisk.apk", "/system/priv-app/Magisk"
            };

            for (String p : paths) {
                if (new File(p).exists()) return true;
            }

            Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", "which su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = in.readLine();
            in.close();

            return line != null && !line.trim().isEmpty();

        } catch (Throwable ignore) {
            return false;
        }
    }

    // ============================================================
    // GEL Battery Path Detector v2.0 (OEM-Smart + GitHub Safe)
    // ============================================================
    private String getBatteryPathForDisplay() {

        String manu    = Build.MANUFACTURER == null ? "" : Build.MANUFACTURER.toLowerCase(Locale.US);
        String finger  = Build.FINGERPRINT == null ? "" : Build.FINGERPRINT.toLowerCase(Locale.US);
        String display = Build.DISPLAY == null ? "" : Build.DISPLAY.toLowerCase(Locale.US);

        boolean isXiaomi  = manu.contains("xiaomi") || manu.contains("redmi") || manu.contains("poco");
        boolean isMIUI    = finger.contains("miui") || display.contains("miui");
        boolean isHyperOS = finger.contains("hyperos") || display.contains("hyperos");

        boolean isSamsung = manu.contains("samsung");
        boolean isPixel   = manu.contains("google") || finger.contains("pixel");

        boolean isOppo    = manu.contains("oppo");
        boolean isRealme  = manu.contains("realme");
        boolean isOnePlus = manu.contains("oneplus");

        boolean isVivo    = manu.contains("vivo") || manu.contains("iqoo");
        boolean isHuawei  = manu.contains("huawei") || manu.contains("honor");

        boolean isMoto    = manu.contains("motorola") || manu.contains("moto");
        boolean isSony    = manu.contains("sony");
        boolean isAsus    = manu.contains("asus");
        boolean isNokia   = manu.contains("nokia");
        boolean isLenovo  = manu.contains("lenovo");
        boolean isLG      = manu.contains("lg");
        boolean isZTE     = manu.contains("zte");
        boolean isTecno   = manu.contains("tecno");
        boolean isInfinix = manu.contains("infinix");
        boolean isMeizu   = manu.contains("meizu");
        boolean isNothing = manu.contains("nothing");

        if (isSamsung) {
            return "Settings → Battery and device care → Battery";
        }

        if (isXiaomi) {
            if (isHyperOS) return "Settings → Battery → Battery usage";
            if (isMIUI)    return "Settings → Battery & performance → Battery usage";
            return "Settings → Battery";
        }

        if (isPixel) {
            return "Settings → Battery → Battery usage";
        }

        if (isOppo || isRealme) {
            return "Settings → Battery → More settings";
        }

        if (isOnePlus) {
            return "Settings → Battery → Advanced settings";
        }

        if (isVivo) {
            return "Settings → Battery";
        }

        if (isHuawei) {
            return "Settings → Battery → App launch";
        }

        if (isMoto) {
            return "Settings → Battery";
        }

        return "Settings → Battery";
    }

    // ============================================================
    // CAMERA / BIOMETRICS / SENSORS / CONNECTIVITY / LOCATION
    // ============================================================
   
// ============================================================
// CAMERA / FULL PHOTO + VIDEO CAPABILITY MAP
// ============================================================

private String buildCameraInfo() {

    StringBuilder sb = new StringBuilder();

    try {
        CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        if (cm != null) {

            for (String id : cm.getCameraIdList()) {

                CameraCharacteristics cc = cm.getCameraCharacteristics(id);

                Map<String, String> cam = new LinkedHashMap<>();

                cam.put("Camera ID", id);

                // --------------------------------------------------
                // BASIC INFO
                // --------------------------------------------------
                Integer facing = cc.get(CameraCharacteristics.LENS_FACING);

                String facingStr = "Unknown";
                if (facing != null) {
                    if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                        facingStr = "Front";
                    } else if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                        facingStr = "Back";
                    } else if (facing == CameraCharacteristics.LENS_FACING_EXTERNAL) {
                        facingStr = "External";
                    }
                }
                cam.put("Facing", facingStr);

                Integer orientation = cc.get(CameraCharacteristics.SENSOR_ORIENTATION);
                if (orientation != null) {
                    cam.put("Orientation", orientation + "°");
                }

                float[] focals = cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                if (focals != null && focals.length > 0) {
                    cam.put("Focal", focals[0] + " mm");
                }

                float[] apertures = cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
                if (apertures != null && apertures.length > 0) {
                    cam.put("Aperture", "f/" + apertures[0]);
                }

                Boolean flashAvail = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (flashAvail != null) {
                    cam.put("Flash", flashAvail ? "Yes" : "No");
                }

                // --------------------------------------------------
                // STREAM CONFIG
                // --------------------------------------------------
                try {
                    StreamConfigurationMap map =
                            cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                    if (map != null) {

                        Size[] jpegSizes =
                                map.getOutputSizes(ImageFormat.JPEG);

                        if (jpegSizes != null && jpegSizes.length > 0) {
                            cam.put("JPEG Modes", jpegSizes.length + " sizes");
                        }

                        Size[] videoSizes =
                                map.getOutputSizes(MediaRecorder.class);

                        if (videoSizes != null && videoSizes.length > 0) {

                            Size max = videoSizes[0];

                            for (Size s : videoSizes) {
                                if (s.getWidth() * s.getHeight() >
                                        max.getWidth() * max.getHeight()) {
                                    max = s;
                                }
                            }

                            cam.put("Video Max",
                                    max.getWidth() + "x" + max.getHeight());

                            cam.put("Video Modes",
                                    videoSizes.length + " resolutions");
                        }
                    }

                } catch (Throwable ignore) {}

                // --------------------------------------------------
                // FPS
                // --------------------------------------------------
                Range<Integer>[] fpsRanges =
                        cc.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);

                if (fpsRanges != null && fpsRanges.length > 0) {

                    int min = Integer.MAX_VALUE;
                    int max = 0;

                    for (Range<Integer> r : fpsRanges) {
                        min = Math.min(min, r.getLower());
                        max = Math.max(max, r.getUpper());
                    }

                    cam.put("FPS Range", min + "–" + max + " fps");
                }

                // --------------------------------------------------
                // STABILIZATION
                // --------------------------------------------------
                int[] stab =
                        cc.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);

                cam.put("Stabilization",
                        (stab != null && stab.length > 0) ? "Yes" : "No");

                // --------------------------------------------------
                // HDR VIDEO
                // --------------------------------------------------
                boolean hdr = false;

                int[] reqCaps =
                        cc.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);

                if (reqCaps != null && Build.VERSION.SDK_INT >= 33) {
                    for (int c : reqCaps) {
                        if (c ==
                                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DYNAMIC_RANGE_TEN_BIT) {
                            hdr = true;
                            break;
                        }
                    }
                }

                cam.put("HDR Video", hdr ? "Yes" : "No");

                // --------------------------------------------------
                // CAPABILITIES
                // --------------------------------------------------
                if (reqCaps != null) {
                    cam.put("Capabilities", reqCaps.length + " flags");
                }

                // --------------------------------------------------
                // HW LEVEL
                // --------------------------------------------------
                Integer hwLevel =
                        cc.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);

                if (hwLevel != null) {

                    String level;

                    switch (hwLevel) {
                        case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                            level = "FULL"; break;
                        case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                            level = "LIMITED"; break;
                        case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                            level = "LEGACY"; break;
                        case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                            level = "LEVEL_3"; break;
                        default:
                            level = "UNKNOWN";
                    }

                    cam.put("HW Level", level);
                }

                // --------------------------------------------------
                // FINAL BUILD
                // --------------------------------------------------
                sb.append(buildSection("Camera", cam));
            }
        }

    } catch (Throwable ignore) {}

    if (sb.length() == 0) {
        sb.append("No camera data exposed by this device.\n");
    }

    appendAccessInstructions(sb, "camera");

    return sb.toString();
}

// ============================================================
//   BIOMETRICS — GEL CLEAN EDITION (API29-SAFE)
// ============================================================
private String buildBiometricsInfo() {

    PackageManager pm = getPackageManager();

    boolean hasFp   = pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
    boolean hasFace = pm.hasSystemFeature("android.hardware.biometrics.face");
    boolean hasIris = pm.hasSystemFeature("android.hardware.biometrics.iris");

    Map<String, String> bio = new LinkedHashMap<>();

    bio.put("Fingerprint", hasFp ? "Yes" : "No");
    bio.put("Face Unlock", hasFace ? "Yes" : "No");
    bio.put("Iris Scan", hasIris ? "Yes" : "No");

    // ------------------------------------------------------------
    // Under-Display Fingerprint (UDFPS)
    // ------------------------------------------------------------
    boolean udFps = false;

    try {
        udFps =
                pm.hasSystemFeature("com.motorola.hardware.fingerprint.udfps") ||
                pm.hasSystemFeature("com.samsung.hardware.fingerprint.udfps") ||
                pm.hasSystemFeature("com.google.hardware.biometrics.udfps") ||
                pm.hasSystemFeature("vendor.samsung.hardware.biometrics.fingerprint.udfps") ||
                pm.hasSystemFeature("vendor.xiaomi.hardware.fingerprint.udfps");
    } catch (Throwable ignore) {}

    if (hasFp) {
        bio.put("Under-Display FP", udFps ? "Yes" : "No");
    }

    // ------------------------------------------------------------
    // Profile
    // ------------------------------------------------------------
    int modes = (hasFp ? 1 : 0) +
                (hasFace ? 1 : 0) +
                (hasIris ? 1 : 0);

    String profile;

    if (modes == 0) {
        profile = "No biometric hardware";
    } else if (modes == 1) {
        profile = "Single biometric";
    } else {
        profile = "Multi-biometric (" + modes + ")";
    }

    bio.put("Profile", profile);

    return buildSection("Biometrics", bio);
}

// ------------------------------------------------------------
// SENSORS — CLEAN GEL DIAGNOSTIC SUMMARY
// ------------------------------------------------------------
private String buildSensorsInfo() {

    Map<String, String> sensors = new LinkedHashMap<>();

    try {
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sm != null) {

            Sensor accelerometer   = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor gyroscope       = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            Sensor magnetometer    = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            Sensor barometer       = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);
            Sensor proximity       = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            Sensor light           = sm.getDefaultSensor(Sensor.TYPE_LIGHT);

            Sensor stepCounter     = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            Sensor stepDetector    = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

            Sensor rotationVector  = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            Sensor gameRotation    = sm.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
            Sensor gravity         = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
            Sensor linearAccel     = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

            Sensor significantMot  = sm.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
            Sensor stationaryDet   = sm.getDefaultSensor(Sensor.TYPE_STATIONARY_DETECT);
            Sensor motionDet       = sm.getDefaultSensor(Sensor.TYPE_MOTION_DETECT);

            sensors.put("Accelerometer", accelerometer != null ? "Yes" : "No");
            sensors.put("Gyroscope", gyroscope != null ? "Yes" : "No");
            sensors.put("Magnetometer", magnetometer != null ? "Yes" : "No");
            sensors.put("Barometer", barometer != null ? "Yes" : "No");
            sensors.put("Proximity", proximity != null ? "Yes" : "No");
            sensors.put("Light Sensor", light != null ? "Yes" : "No");

            sensors.put("Step Counter", stepCounter != null ? "Yes" : "No");
            sensors.put("Step Detector", stepDetector != null ? "Yes" : "No");

            sensors.put("Rotation Vector", rotationVector != null ? "Yes" : "No");
            sensors.put("Game Rotation Vector", gameRotation != null ? "Yes" : "No");
            sensors.put("Gravity Sensor", gravity != null ? "Yes" : "No");
            sensors.put("Linear Acceleration", linearAccel != null ? "Yes" : "No");

            sensors.put("Significant Motion", significantMot != null ? "Yes" : "No");
            sensors.put("Stationary Detect", stationaryDet != null ? "Yes" : "No");
            sensors.put("Motion Detect", motionDet != null ? "Yes" : "No");
        }

    } catch (Throwable ignore) {}

    // 🔴 fallback αν δεν έχει τίποτα
    if (sensors.isEmpty()) {
        return "No sensor information is exposed by this device.\n";
    }

    // 🔴 build + access info (όπως πριν)
    StringBuilder sb = new StringBuilder();
    sb.append(buildSection("Sensors", sensors));

    appendAccessInstructions(sb, "sensors");

    return sb.toString();
}

// ============================================================
// WIFI + BLUETOOTH INFO — CONNECTIVITY EXTENSION (FULL + ROOT)
// ============================================================
private String buildWifiAndBluetoothInfo() {

    Map<String, String> wifi = new LinkedHashMap<>();

    WifiManager wm = (WifiManager) getApplicationContext()
            .getSystemService(Context.WIFI_SERVICE);

    WifiInfo wi = null;

    // ============================================================
    // SAFE ACCESS
    // ============================================================
    if (wm != null) {
        try {
            wi = wm.getConnectionInfo();
        } catch (SecurityException se) {
            wifi.put("Access", "Denied (Location permission required)");
            return buildSection("Wi-Fi Details", wifi);
        } catch (Throwable t) {
            wifi.put("Access", "Unavailable");
            return buildSection("Wi-Fi Details", wifi);
        }
    }

    // ============================================================
    // MAIN DATA
    // ============================================================
    if (wi != null && wi.getNetworkId() != -1) {

        try {
            wifi.put("SSID", wi.getSSID());
        } catch (Throwable t) {
            wifi.put("SSID", "Restricted");
        }

        try { wifi.put("Link Speed", wi.getLinkSpeed() + " Mbps"); } catch (Throwable ignore) {}
        try { wifi.put("RSSI", wi.getRssi() + " dBm"); } catch (Throwable ignore) {}
        try { wifi.put("Frequency", wi.getFrequency() + " MHz"); } catch (Throwable ignore) {}

        // ------------------------------------------------------------
        // SIGNAL QUALITY
        // ------------------------------------------------------------
        try {
            int rssi = wi.getRssi();

            String quality =
                    rssi >= -50 ? "Excellent" :
                    rssi >= -60 ? "Good" :
                    rssi >= -70 ? "Fair" : "Weak";

            wifi.put("Signal Quality", quality);

        } catch (Throwable ignore) {}

        // ------------------------------------------------------------
        // BAND
        // ------------------------------------------------------------
        try {
            int freq = wi.getFrequency();

            String band =
                    freq >= 5925 ? "6 GHz (Wi-Fi 6E)" :
                    freq >= 4900 ? "5 GHz" : "2.4 GHz";

            wifi.put("Band", band);

        } catch (Throwable ignore) {}
    }

    // ============================================================
    // WIFI STANDARD (OFFICIAL + GEL INFERENCE)
    // ============================================================
    if (wi != null && Build.VERSION.SDK_INT >= 21) {
        try {

            int std = (Build.VERSION.SDK_INT >= 30)
                    ? wi.getWifiStandard()
                    : -1;

            int freq = wi.getFrequency();
            int speed = wi.getLinkSpeed();

            String stdStr = null;

            // ✅ Official
            if (std != -1) {
                switch (std) {
                    case 6: stdStr = "Wi-Fi 6 / 6E (802.11ax)"; break;
                    case 5: stdStr = "Wi-Fi 5 (802.11ac)"; break;
                    case 4: stdStr = "Wi-Fi 4 (802.11n)"; break;
                    case 1: stdStr = "802.11a"; break;
                    case 2: stdStr = "802.11b"; break;
                    case 3: stdStr = "802.11g"; break;
                }
            }

            // 🔥 GEL inference
            if (stdStr == null) {

                if (freq >= 5925) {
                    stdStr = "Wi-Fi 6E (inferred)";
                }
                else if (freq >= 4900) {

                    if (speed >= 800) {
                        stdStr = "Wi-Fi 6 (high throughput inferred)";
                    }
                    else if (speed >= 300) {
                        stdStr = "Wi-Fi 5 (802.11ac inferred)";
                    }
                    else {
                        stdStr = "Wi-Fi 5 (low throughput)";
                    }
                }
                else if (freq >= 2400) {

                    if (speed >= 150) {
                        stdStr = "Wi-Fi 4 (802.11n inferred)";
                    }
                    else {
                        stdStr = "Legacy Wi-Fi (b/g)";
                    }
                }
                else {
                    stdStr = "Unknown (device limitation)";
                }
            }

            wifi.put("Wi-Fi Standard", stdStr);

        } catch (Throwable ignore) {}
    }

    // ============================================================
    // MAC
    // ============================================================
    if (wi != null) {
        try {
            String rawMac = wi.getMacAddress();

            String mac =
                    rawMac != null && !"02:00:00:00:00:00".equals(rawMac)
                            ? rawMac
                            : (isDeviceRooted()
                                ? "Unavailable"
                                : "Masked by Android security (requires root)");

            wifi.put("MAC", mac);

        } catch (Throwable ignore) {}
    }

    // ============================================================
    // FINAL
    // ============================================================
    if (wifi.isEmpty()) {
        return "Wi-Fi information unavailable.\n";
    }

    StringBuilder sb = new StringBuilder();

sb.append(buildSection("Wi-Fi Details", wifi));
sb.append("\n");
sb.append(buildBluetoothInfo());

return sb.toString();
}

// ============================================================
// BLUETOOTH
// ============================================================
private String buildBluetoothInfo() {

    Map<String, String> bt = new LinkedHashMap<>();

    BluetoothManager bm = null;
    BluetoothAdapter ba = null;

    try {
        bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        ba = (bm != null) ? bm.getAdapter() : null;
    } catch (Throwable ignore) {}

    if (ba == null) {

        bt.put("Supported", "No");

    } else {

        bt.put("Supported", "Yes");

        boolean enabled = false;
        try { enabled = ba.isEnabled(); } catch (Throwable ignore) {}
        bt.put("Enabled", enabled ? "Yes" : "No");

        int state = BluetoothAdapter.STATE_OFF;
        try { state = ba.getState(); } catch (Throwable ignore) {}

        String stateStr =
                state == BluetoothAdapter.STATE_ON ? "On" :
                state == BluetoothAdapter.STATE_TURNING_ON ? "Turning On" :
                state == BluetoothAdapter.STATE_TURNING_OFF ? "Turning Off" : "Off";

        bt.put("State", stateStr);

        boolean le = false;
        try {
            le = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        } catch (Throwable ignore) {}

        bt.put("BLE Support", le ? "Yes" : "No");

        bt.put("Root data",
                isDeviceRooted()
                        ? "Available"
                        : "Requires root access");
    }

    return buildSection("Bluetooth", bt);
}

// ===================================================================
// MODEL CAPACITY STORAGE (SharedPreferences) — FINAL GEL EDITION
// ===================================================================
private static final String PREFS_NAME_BATTERY = "gel_prefs";
private static final String KEY_BATTERY_MODEL_CAPACITY = "battery_model_capacity";
private static final String KEY_BATTERY_DIALOG_SHOWN   = "battery_dialog_shown";

// ===================================================================
// BATTERY DATA STRUCT (ROOT-AWARE)
// ===================================================================
private static class BatteryInfo {
    int level = -1;
    int scale = -1;
    String status = "N/A";
    String chargingSource = "Unknown";
    float temperature = 0f;

    long currentChargeMah  = -1;   // Charge Counter / charge_now
    long estimatedFullMah  = -1;   // charge_full / derived
    long designFullMah     = -1;   // charge_full_design
    long cycleCount        = -1;   // root only
    long internalResistance= -1;   // root only (mΩ if available)

    String source          = "Unknown";
    boolean rootedData     = false;
}

// ===================================================================
// MODEL CAPACITY HELPERS
// ===================================================================
private long getStoredModelCapacity() {
    try {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME_BATTERY, MODE_PRIVATE);
        return sp.getLong(KEY_BATTERY_MODEL_CAPACITY, -1L);
    } catch (Throwable ignore) { return -1L; }
}

private void saveModelCapacity(long value) {
    try {
        getSharedPreferences(PREFS_NAME_BATTERY, MODE_PRIVATE)
                .edit()
                .putLong(KEY_BATTERY_MODEL_CAPACITY, value)
                .apply();
    } catch (Throwable ignore) {}
}

// ===================================================================
// NORMALIZE mAh / μAh
// ===================================================================
private long normalizeMah(long raw) {
    if (raw <= 0) return -1;
    if (raw > 200000) return raw / 1000;   // μAh → mAh
    return raw;
}

// ===================================================================
// ROOT-AWARE SYSFS READ
// ===================================================================
private long readSysLongRootAware(String path) {
    try {
        iDoctorEngine eng = iDoctorEngine.get(getApplicationContext());
        long v = eng.readSysLongSafe(path);
        if (v > 0) return v;
    } catch (Throwable ignore) {}

    try {
        Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "cat " + path});
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = br.readLine();
        br.close();
        if (line != null) {
            long v = Long.parseLong(line.replaceAll("[^0-9]", ""));
            if (v > 0) return v;
        }
    } catch (Throwable ignore) {}

    return -1;
}

// ===================================================================
// BATTERY INFO BUILDER — GEL PREMIUM + ROOT PRO
// ===================================================================
private String buildBatteryInfo() {

    iDoctorEngine eng =
            iDoctorEngine.get(getApplicationContext());

    iDoctorEngine.FullSnapshot snap =
            eng.readFullSnapshot();

    if (snap == null || snap.battery == null)
        return "Battery info not available";

    long modelCap = getStoredModelCapacity();

    StringBuilder sb = new StringBuilder();

// --------------------------------------------------
// BASIC (FINAL - GEL CLEAN)
// --------------------------------------------------

Map<String, String> basic = new LinkedHashMap<>();

basic.put("Level",
        snap.battery.level >= 0
                ? snap.battery.level + "%"
                : "N/A");

basic.put("Status",
        snap.battery.status != null && !snap.battery.status.isEmpty()
                ? snap.battery.status
                : "N/A");

basic.put("Charging source",
        snap.battery.chargingSource != null && !snap.battery.chargingSource.isEmpty()
                ? snap.battery.chargingSource
                : "N/A");

if (!Float.isNaN(snap.battery.currentMa) && snap.battery.currentMa != 0f) {
    basic.put("Current",
            String.format(Locale.US, "%.0f mA", snap.battery.currentMa));
}

// build
sb.append(buildSection("Battery — Basic", basic));

// --------------------------------------------------
// CURRENT CHARGE (STRICT)
// --------------------------------------------------

long currentCharge = -1;
String currentSource = "N/A";

boolean hasValidLevel =
        snap.battery.level > 0 && snap.battery.level <= 100;

// LEVEL 1 — real fuel gauge
if (snap.battery.chargeNowMah > 0) {

    currentCharge = snap.battery.chargeNowMah;
    currentSource = "hardware_counter";
}

// LEVEL 2 — derived from real full capacity
else if (snap.battery.chargeFullMah > 0 && hasValidLevel) {

    currentCharge =
            (long)(
                    snap.battery.chargeFullMah *
                    (snap.battery.level / 100f)
            );

    currentSource = "capacity_based";
}

// OUTPUT
Map<String, String> charge = new LinkedHashMap<>();

if (currentCharge > 0) {

    charge.put("Current charge", currentCharge + " mAh");

    charge.put("Charge source",
            currentSource != null ? currentSource : "N/A");
}

// build
if (!charge.isEmpty()) {
    sb.append(buildSection("Battery — Charge Info", charge));
}

// --------------------------------------------------
// ESTIMATED CAPACITY (REAL + GEL STRICT)
// --------------------------------------------------

long estimatedCapacity = -1;
String capacitySource = "no_counter";

// -------------------------
// PRIMARY (REAL COUNTER)
// -------------------------

boolean hasCounter =
        snap.battery.chargeNowMah > 0 &&
        hasValidLevel;

if (hasCounter) {

    estimatedCapacity =
            (long)(
                    snap.battery.chargeNowMah /
                    (snap.battery.level / 100f)
            );

    capacitySource = "counter_calculated";
}

// -------------------------
// SANITY CHECK
// -------------------------

if (estimatedCapacity > 15000 || estimatedCapacity < 500) {
    estimatedCapacity = -1;
    capacitySource = "invalid_range";
}

// -------------------------
// CONSISTENCY CHECK
// -------------------------

if (hasCounter && snap.battery.chargeFullMah > 0) {

    float expected =
            snap.battery.chargeFullMah *
            (snap.battery.level / 100f);

    float diff =
            Math.abs(expected - snap.battery.chargeNowMah);

    if (diff > expected * 0.25f) {
        estimatedCapacity = -1;
        capacitySource = "inconsistent_counter";
    }
}

// --------------------------------------------------
// 🔥 FALLBACK (STRICT HARDWARE ONLY)
// --------------------------------------------------

if (estimatedCapacity <= 0 && hasValidLevel) {

    estimatedCapacity = -1;
    capacitySource = "unavailable_no_counter";
}

// --------------------------------------------------
// OUTPUT
// --------------------------------------------------

Map<String, String> adv = new LinkedHashMap<>();

adv.put("Estimated capacity",
        estimatedCapacity > 0
                ? estimatedCapacity + " mAh"
                : "N/A");

adv.put("Capacity source",
        capacitySource != null ? capacitySource : "N/A");

// --------------------------------------------------
// DECLARED CAPACITY (model)
// --------------------------------------------------

if (modelCap > 0) {
    adv.put("Declared capacity", modelCap + " mAh");
}

// --------------------------------------------------
// VOLTAGE
// --------------------------------------------------

if (snap.battery.voltageMv > 0) {
    adv.put("Voltage",
            String.format(Locale.US, "%.3f V",
                    snap.battery.voltageMv / 1000f));
}

// --------------------------------------------------
// TEMPERATURE
// --------------------------------------------------

adv.put("Temp",
        !Float.isNaN(snap.battery.batteryTempC)
                ? String.format(Locale.US, "%.1f°C",
                        snap.battery.batteryTempC)
                : "N/A");

// --------------------------------------------------
// SOURCE (FINAL - ALWAYS SHOWN)
// --------------------------------------------------

String finalSource;

if (estimatedCapacity > 0) {
    finalSource = "counter_estimate";
} else {
    finalSource =
            snap.battery.source != null
                    ? snap.battery.source
                    : "N/A";
}

adv.put("Source", finalSource);

// build
sb.append(buildSection("Battery — Advanced", adv));

// --------------------------------------------------
// ROOT / OEM
// --------------------------------------------------

if (snap.battery.chargeDesignMah > 0
        || snap.battery.cycleCount > 0
        || snap.battery.sohPercent > 0) {

    Map<String, String> root = new LinkedHashMap<>();

    if (snap.battery.chargeDesignMah > 0) {
        root.put("Design capacity",
                snap.battery.chargeDesignMah + " mAh");
    }

    if (snap.battery.sohPercent > 0) {
        root.put("SOH",
                snap.battery.sohPercent + " %");
    }

    if (snap.battery.cycleCount > 0) {
        root.put("Cycle count",
                String.valueOf(snap.battery.cycleCount));
    }

    sb.append("\n");
    sb.append(buildSection("Root Battery Data", root));

} else {

    Map<String, String> fallback = new LinkedHashMap<>();
    fallback.put("Lifecycle", "OEM data not available");

    sb.append("\n");
    sb.append(buildSection("Battery Lifecycle", fallback));
}

// --------------------------------------------------
// 🔥 SMART USER MESSAGE (ONLY IF BLOCKED)
// --------------------------------------------------

if (snap != null &&
    snap.battery != null &&
    "RESTRICTED".equals(snap.battery.mode)) {

    sb.append("\n");
    sb.append("⚠️ This device restricts battery telemetry.\n");
    sb.append("For accurate diagnostics, set your battery capacity manually.\n");
}

return sb.toString();
}

// ===================================================================
// REFRESH VIEW
// ===================================================================
private void refreshBatteryInfoView() {
    try {
        if (txtBatteryContent != null) {
            txtBatteryContent.setText(buildBatteryInfo());
        }
        refreshBatteryButton();
    } catch (Throwable ignore) {}
}

// ===================================================================
// REFRESH BUTTON LABEL
// ===================================================================
private void refreshBatteryButton() {
    TextView btn = findViewById(R.id.txtBatteryModelCapacity);
    if (btn != null) {
        long cap = getStoredModelCapacity();
        btn.setText(cap > 0
                ? "Set model capacity (" + cap + " mAh)"
                : "Set model capacity");
    }
}

// ===================================================================
// INIT BATTERY SECTION (DIAGNOSTIC MODE)
// ===================================================================
private void initBatterySection() {

    txtBatteryContent = findViewById(R.id.txtBatteryContent);
    TextView btnCapacity = findViewById(R.id.txtBatteryModelCapacity);

    refreshBatteryInfoView();

    if (btnCapacity != null) {
        btnCapacity.setOnClickListener(v -> showBatteryCapacityDialog());
    }

    maybeShowBatteryCapacityDialogOnce();
}

// ===================================================================
// POPUP ONLY ONCE
// ===================================================================
private void maybeShowBatteryCapacityDialogOnce() {
    try {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME_BATTERY, MODE_PRIVATE);
        if (!sp.getBoolean(KEY_BATTERY_DIALOG_SHOWN, false) &&
            sp.getLong(KEY_BATTERY_MODEL_CAPACITY, -1L) <= 0) {

            sp.edit().putBoolean(KEY_BATTERY_DIALOG_SHOWN, true).apply();
            runOnUiThread(this::showBatteryCapacityDialog);
        }
    } catch (Throwable ignore) {}
}

// ===================================================================
// POPUP — FINAL
// ===================================================================
private void showBatteryCapacityDialog() {

    runOnUiThread(() -> {
        try {

            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle(getString(R.string.battery_popup_title));
            b.setMessage(getString(R.string.battery_popup_msg));

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint(getString(R.string.battery_popup_hint));

            long current = getStoredModelCapacity();
            if (current > 0) {
                input.setText(String.valueOf(current));
                input.setSelection(input.getText().length());
            }

            b.setView(input);

            b.setPositiveButton(getString(R.string.battery_popup_ok),
                    (dialog, which) -> {
                        String txt = input.getText().toString().trim();
                        if (!txt.isEmpty()) {
                            try {
                                long val = Long.parseLong(txt);
                                if (val > 0) {
                                    saveModelCapacity(val);
                                    refreshBatteryInfoView();
                                }
                            } catch (Throwable ignore) {}
                        }
                    });

            b.setNegativeButton(getString(R.string.battery_popup_cancel), null);

            AlertDialog dialog = b.create();
            dialog.getWindow().setBackgroundDrawableResource(
                    R.drawable.gel_dialog_battery_full_black
            );
            dialog.show();

        } catch (Throwable ignore) {}
    });
}
    
 private String buildUwbInfo() {

    boolean supported =
            getPackageManager().hasSystemFeature("android.hardware.uwb");

    Map<String, String> uwb = new LinkedHashMap<>();

    uwb.put("Supported", supported ? "Yes" : "No");

    uwb.put("Advanced",
            "Fine-grain ranging diagnostics, requires root access.");

    return buildSection("UWB", uwb);
}

 // ============================================================
// HAPTICS — SAFE EDITION (API 29–34)
// ============================================================
private String buildHapticsInfo() {

    Map<String, String> haptics = new LinkedHashMap<>();

    Vibrator v =
            (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    if (v == null) {

        haptics.put("Vibration Engine", "Not available");
        return buildSection("Haptics", haptics);
    }

    if (Build.VERSION.SDK_INT >= 29) {

        if (v.hasAmplitudeControl()) {
            haptics.put("Vibration Engine", "Amplitude Control");
        } else {
            haptics.put("Vibration Engine", "Basic Engine");
        }

    } else {

        haptics.put("Vibration Engine", "Legacy Engine");
    }

    haptics.put("Advanced",
            "Low-level haptic patterns require root/kernel access.");

    return buildSection("Haptics", haptics);
}

// ============================================================
// GNSS / LOCATION — GEL CLEAN EDITION (API-SAFE)
// ============================================================
private String buildGnssInfo() {

    StringBuilder sb = new StringBuilder();

    try {
        PackageManager pm = getPackageManager();
        LocationManager lm =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // ---------------------------------------------------
        // PROVIDERS STATUS
        // ---------------------------------------------------
        Map<String, String> providers = new LinkedHashMap<>();

        if (lm != null) {
            boolean gps = false;
            boolean net = false;

            try { gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER); } catch (Throwable ignore) {}
            try { net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER); } catch (Throwable ignore) {}

            providers.put("GPS Provider", gps ? "Enabled" : "Disabled");
            providers.put("Network Location", net ? "Enabled" : "Disabled");
        }

        if (!providers.isEmpty()) {
            sb.append(buildSection("Location Providers", providers));
        }

        // ---------------------------------------------------
        // CONSTELLATIONS
        // ---------------------------------------------------
        Map<String, String> constellations = new LinkedHashMap<>();

        constellations.put("GPS",
                pm.hasSystemFeature("android.hardware.location.gps") ? "Yes" : "No");

        constellations.put("GLONASS",
                pm.hasSystemFeature("android.hardware.location.glonass") ? "Yes" : "No");

        constellations.put("Galileo",
                pm.hasSystemFeature("android.hardware.location.galileo") ? "Yes" : "No");

        constellations.put("BeiDou",
                pm.hasSystemFeature("android.hardware.location.beidou") ? "Yes" : "No");

        constellations.put("QZSS",
                pm.hasSystemFeature("android.hardware.location.qzss") ? "Yes" : "No");

        constellations.put("SBAS",
                pm.hasSystemFeature("android.hardware.location.sbas") ? "Yes" : "No");

        constellations.put("NavIC / IRNSS",
                pm.hasSystemFeature("android.hardware.location.irnss") ? "Yes" : "No");

        sb.append("\n");
        sb.append(buildSection("Constellations", constellations));

        // ---------------------------------------------------
        // CAPABILITIES
        // ---------------------------------------------------
        Map<String, String> caps = new LinkedHashMap<>();

        boolean raw =
                pm.hasSystemFeature("android.hardware.location.gnss.raw_measurement");

        boolean batch =
                pm.hasSystemFeature("android.hardware.location.gnss.batch");

        caps.put("Raw Measurements", raw ? "Yes" : "No");
        caps.put("GNSS Batching", batch ? "Yes" : "No");

        sb.append("\n");
        sb.append(buildSection("Capabilities", caps));

        // ---------------------------------------------------
        // NMEA
        // ---------------------------------------------------
        Map<String, String> nmea = new LinkedHashMap<>();

        nmea.put("NMEA Support", lm != null ? "Yes" : "No");

        sb.append("\n");
        sb.append(buildSection("NMEA", nmea));

    } catch (Throwable ignore) {

        return "GNSS information is not exposed on this device.\n";
    }

    return sb.toString();
}
      
// ============================================================
// USB / OTG / POWER / ROLE ENGINE — GEL API29-SAFE EDITION
// ============================================================
private String buildUsbInfo() {

    StringBuilder sb = new StringBuilder();

    PackageManager pm = getPackageManager();

    // ------------------------------------------------------------
    // BASIC SUPPORT FLAGS
    // ------------------------------------------------------------
    Map<String, String> basic = new LinkedHashMap<>();

    boolean otg = pm.hasSystemFeature("android.hardware.usb.host");
    boolean acc = pm.hasSystemFeature("android.hardware.usb.accessory");

    basic.put("OTG Support", otg ? "Yes" : "No");
    basic.put("Accessory Mode", acc ? "Yes" : "No");

    sb.append(buildSection("USB Basics", basic));

    // ------------------------------------------------------------
    // USB MANAGER
    // ------------------------------------------------------------
    UsbManager um = (UsbManager) getSystemService(Context.USB_SERVICE);

    if (um == null) {
        Map<String, String> err = new LinkedHashMap<>();
        err.put("Status", "USB Manager unavailable");
        sb.append("\n");
        sb.append(buildSection("USB Status", err));
        return sb.toString();
    }

    // ------------------------------------------------------------
    // CONNECTED DEVICES
    // ------------------------------------------------------------
    try {
        HashMap<String, UsbDevice> devs = um.getDeviceList();

        if (devs != null && !devs.isEmpty()) {

            sb.append("\nConnected USB Devices:\n");

            for (UsbDevice d : devs.values()) {

                Map<String, String> dev = new LinkedHashMap<>();

                dev.put("Device", d.getDeviceName());
                dev.put("Vendor ID", String.valueOf(d.getVendorId()));
                dev.put("Product ID", String.valueOf(d.getProductId()));
                dev.put("Class/Subclass",
                        d.getDeviceClass() + "/" + d.getDeviceSubclass());
                dev.put("Interfaces",
                        String.valueOf(d.getInterfaceCount()));
                dev.put("USB Speed",
                        "Not exposed by public API");

                sb.append(buildSection("USB Device", dev));
            }

        } else {

            Map<String, String> none = new LinkedHashMap<>();
            none.put("Connected Devices", "None");

            sb.append("\n");
            sb.append(buildSection("USB Devices", none));
        }

    } catch (Throwable ignore) {

        Map<String, String> err = new LinkedHashMap<>();
        err.put("Connected Devices", "Error reading USB devices");

        sb.append("\n");
        sb.append(buildSection("USB Devices", err));
    }

    // ------------------------------------------------------------
    // ROLE / MODE
    // ------------------------------------------------------------
    Map<String, String> role = new LinkedHashMap<>();
    role.put("USB Role", "Vendor HAL not exposed");

    sb.append("\n");
    sb.append(buildSection("Mode / Role", role));

    // ------------------------------------------------------------
    // POWER PROFILE
    // ------------------------------------------------------------
    Map<String, String> power = new LinkedHashMap<>();

    try {
        IntentFilter ifil = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batt = registerReceiver(null, ifil);

        if (batt != null) {

            int source = batt.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

            String srcLabel =
                    (source == BatteryManager.BATTERY_PLUGGED_USB) ? "USB"
                    : (source == BatteryManager.BATTERY_PLUGGED_AC) ? "AC"
                    : (source == BatteryManager.BATTERY_PLUGGED_WIRELESS) ? "Wireless"
                    : "Unplugged";

            power.put("Charge Source", srcLabel);

            int volt = batt.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            power.put("Voltage (mV)", String.valueOf(volt));
        }

    } catch (Throwable ignore) {
        power.put("Power Info", "Error");
    }

    // ------------------------------------------------------------
    // CURRENT (ROOT)
    // ------------------------------------------------------------
    Integer mA = getRootChargeCurrentMilliAmps();

    if (mA != null) {
        power.put("Charge (mA)", String.valueOf(mA));
    } else {
        power.put("Charge (mA)", "N/A (requires root access)");
    }

    sb.append("\n");
    sb.append(buildSection("Power Profile", power));

    // ------------------------------------------------------------
    // FINAL NOTE
    // ------------------------------------------------------------
    Map<String, String> note = new LinkedHashMap<>();

    note.put("Advanced",
            "USB descriptors, role switching and power negotiation require root access.");

    sb.append("\n");
    sb.append(buildSection("Notes", note));

    return sb.toString();
}

// ============================================================
// ROOT HELPERS — CHARGE CURRENT (µA → mA)
// ============================================================
private Integer getRootChargeCurrentMilliAmps() {
    try {
        if (!isRootAvailable()) return null;

        String[] paths = new String[] {
                "/sys/class/power_supply/battery/current_now",
                "/sys/class/power_supply/battery/input_current_now",
                "/sys/class/power_supply/usb/current_now",
                "/sys/class/power_supply/usb/input_current_now",
                "/sys/class/power_supply/main/current_now",
                "/sys/class/power_supply/ac/current_now",
                "/sys/class/power_supply/charger/current_now"
        };

        for (String p : paths) {
            String out = suCatFirstLine(p);
            Integer uA = parseIntSafe(out);
            if (uA != null) {
                return (int) Math.round(uA / 1000.0); // µA → mA
            }
        }
        return null;

    } catch (Throwable ignore) {
        return null;
    }
}

private boolean isRootAvailable() {
    try {
        Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = br.readLine();
        br.close();
        p.waitFor();
        return line != null && line.contains("uid=0");
    } catch (Throwable t) {
        return false;
    }
}

private String suCatFirstLine(String path) {
    try {
        Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "cat " + path});
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = br.readLine();
        br.close();
        p.waitFor();
        return (line == null || line.trim().isEmpty()) ? null : line.trim();
    } catch (Throwable t) {
        return null;
    }
}

private Integer parseIntSafe(String s) {
    try {
        if (s == null) return null;
        s = s.replaceAll("[^0-9\\-+]", "");
        if (s.isEmpty()) return null;
        return Integer.parseInt(s);
    } catch (Throwable t) {
        return null;
    }
}

// ============================================================
// Other Peripherals — CLEAN GEL EDITION (UNIFIED)
// ============================================================
private String buildOtherPeripheralsInfo() {

    PackageManager pm = getPackageManager();

    Map<String, String> data = new LinkedHashMap<>();

    boolean ir       = pm.hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR);
    boolean fm       = pm.hasSystemFeature("android.hardware.fm");
    boolean hall     = pm.hasSystemFeature("android.hardware.sensor.hall");
    boolean hwkbd    = pm.hasSystemFeature("android.hardware.keyboard");
    boolean wireless = pm.hasSystemFeature("android.hardware.power.wireless_charging");
    boolean tv       = pm.hasSystemFeature("android.hardware.tv.tuner");
    boolean barcode  = pm.hasSystemFeature("android.hardware.barcodescanner");

    data.put("IR Blaster", ir ? "Yes" : "No");
    data.put("FM Radio", fm ? "Yes" : "No");
    data.put("Hall Sensor", hall ? "Yes" : "No");
    data.put("HW Keyboard", hwkbd ? "Yes" : "No");
    data.put("Wireless Charging", wireless ? "Yes" : "No");
    data.put("TV Tuner", tv ? "Yes" : "No");
    data.put("Barcode Module", barcode ? "Yes" : "No");

    // 🔴 main section
    StringBuilder sb = new StringBuilder();
    sb.append(buildSection("Other Peripherals", data));

    // 🔴 advanced note (κρατάμε το original behavior)
    Map<String, String> adv = new LinkedHashMap<>();
    adv.put("Advanced",
            "Extended peripheral diagnostics require root access.");

    sb.append("\n");
    sb.append(buildSection("Notes", adv));

    return sb.toString();
}

// ============================================================================
// AUDIO SYSTEM — CLEAN PERIPHERALS BLOCK (NO TESTS, NO HEAVY OPS)
// Speakers / Microphones / HAL / Extended
// GEL — Play Store Safe, Zero Lag
// ============================================================================

// ============================================================================
// 1) MICROPHONES — DETECTION ONLY (UNIFIED GEL)
// ============================================================================
private String buildMicsInfo() {

    StringBuilder sb = new StringBuilder();

    boolean hasBuiltin = false, hasTele = false, hasWired = false, hasBT = false, hasUSB = false;
    int wired = 0, bt = 0, usb = 0;

    try {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {

            AudioDeviceInfo[] devs = am.getDevices(AudioManager.GET_DEVICES_INPUTS);

            for (AudioDeviceInfo d : devs) {

                switch (d.getType()) {

                    case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                        sb.append("• Built-in Microphone\n")
                          .append("   Role          : ")
                          .append(hasBuiltin ? "noise-cancel mic" : "Primary microphone")
                          .append("\n   Present       : Yes\n\n");
                        hasBuiltin = true;
                        break;

                    case AudioDeviceInfo.TYPE_TELEPHONY:
                        sb.append("• Telephony Microphone\n")
                          .append("   Role          : Dedicated voice call\n")
                          .append("   Present       : Yes\n\n");
                        hasTele = true;
                        break;

                    case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                    case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                        wired++; hasWired = true;
                        break;

                    case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                    case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                        bt++; hasBT = true;
                        break;

                    case AudioDeviceInfo.TYPE_USB_DEVICE:
                    case AudioDeviceInfo.TYPE_USB_HEADSET:
                        usb++; hasUSB = true;
                        break;
                }
            }
        }
    } catch (Throwable ignore) {}

    // ============================================================
    // 🔥 SUMMARY (UNIFIED)
    // ============================================================
    Map<String, String> summary = new LinkedHashMap<>();

    summary.put("Built-in Mic", hasBuiltin ? "Yes" : "No");
    summary.put("Telephony Mic", hasTele ? "Yes" : "No");
    summary.put("Wired Mics", hasWired ? "Yes (" + wired + ")" : "No");
    summary.put("Bluetooth Mics", hasBT ? "Yes (" + bt + ")" : "No");
    summary.put("USB Mics", hasUSB ? "Yes (" + usb + ")" : "No");

    sb.append(buildSection("Microphones — Summary", summary));

    return sb.toString();
}

// ============================================================================
// 2) AUDIO OUTPUTS / HAL — DETECTION ONLY (UNIFIED GEL)
// ============================================================================
private String buildAudioHalInfo() {

    StringBuilder sb = new StringBuilder();

    // ============================================================
    // HAL INFO
    // ============================================================
    String hal = getProp("ro.audio.hal.version");

    Map<String, String> halMap = new LinkedHashMap<>();
    halMap.put("Audio HAL",
            (hal != null && !hal.isEmpty())
                    ? hal
                    : "Not exposed");

    sb.append(buildSection("Audio HAL", halMap));

    // ============================================================
    // OUTPUT DETECTION
    // ============================================================
    boolean speaker=false, wired=false, bt=false, usb=false, hdmi=false;

    try {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {

            AudioDeviceInfo[] outs = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

            for (AudioDeviceInfo o : outs) {
                switch (o.getType()) {
                    case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER: speaker=true; break;
                    case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                    case AudioDeviceInfo.TYPE_WIRED_HEADSET:   wired=true;   break;
                    case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                    case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:   bt=true;      break;
                    case AudioDeviceInfo.TYPE_USB_DEVICE:
                    case AudioDeviceInfo.TYPE_USB_HEADSET:     usb=true;     break;
                    case AudioDeviceInfo.TYPE_HDMI:            hdmi=true;    break;
                }
            }
        }
    } catch (Throwable ignore) {}

    // ============================================================
    // 🔥 SUMMARY (UNIFIED)
    // ============================================================
    Map<String, String> summary = new LinkedHashMap<>();

    summary.put("Speaker Output", speaker ? "Yes" : "No");
    summary.put("Wired Output", wired ? "Yes" : "No");
    summary.put("Bluetooth Output", bt ? "Yes" : "No");
    summary.put("USB Output", usb ? "Yes" : "No");
    summary.put("HDMI Output", hdmi ? "Yes" : "No");

    sb.append("\n");
    sb.append(buildSection("Audio Outputs — Summary", summary));

    return sb.toString();
}

// ============================================================================
// 3) AUDIO EXTENDED — SAFE FLAGS (UNIFIED)
// ============================================================================
private String buildAudioExtendedInfo() {

    Map<String, String> data = new LinkedHashMap<>();

    try {
        boolean hw = getPackageManager()
                .hasSystemFeature("android.hardware.audio.output");

        data.put("Audio Output HW", hw ? "Yes" : "No");

    } catch (Throwable ignore) {}

    return buildSection("Extended Audio Paths", data);
}

// ============================================================================
// 4) UNIFIED AUDIO BLOCK — PERIPHERALS VIEW (GEL CLEAN)
// ============================================================================
private String buildAudioUnifiedInfo() {

    StringBuilder sb = new StringBuilder();

    sb.append(buildMicsInfo());
    sb.append("\n");

    sb.append(buildAudioHalInfo());
    sb.append("\n");

    sb.append(buildAudioExtendedInfo());

    return sb.toString();
}

// ============================================================
// Root Info (UNIFIED GEL — CLEAN + STRUCTURED)
// ============================================================
private String buildRootInfo() {

    StringBuilder sb = new StringBuilder();

    // ============================================================
    // BASIC
    // ============================================================
    Map<String, String> basic = new LinkedHashMap<>();

    basic.put("Root Access Mode",
            isRooted
                    ? "Rooted device (superuser access detected)"
                    : "Non-rooted device (standard access)");

    basic.put("Build Tags", Build.TAGS);

    String secure = getProp("ro.secure");
    if (secure != null && !secure.isEmpty()) {
        basic.put("ro.secure", secure);
    }

    String dbg = getProp("ro.debuggable");
    if (dbg != null && !dbg.isEmpty()) {
        basic.put("ro.debuggable", dbg);
    }

    String verity = getProp("ro.boot.veritymode");
    if (verity != null && !verity.isEmpty()) {
        basic.put("Verity Mode", verity);
    }

    String selinux = getProp("ro.build.selinux");
    if (selinux != null && !selinux.isEmpty()) {
        basic.put("SELinux", selinux);
    }

    sb.append(buildSection("Root — Basic", basic));

    // ============================================================
    // FUSION LAYER
    // ============================================================
    Map<String, String> fusion = new LinkedHashMap<>();

    fusion.put("Fusion Layer",
            isRooted
                    ? "Running with root access; extended diagnostics are enabled where supported."
                    : "Standard Android permission model.");

    sb.append("\n");
    sb.append(buildSection("Root — Access Model", fusion));

    // ============================================================
    // ROOT MODE
    // ============================================================
    if (isRooted) {

        // -----------------------------
        // EXTENDED STATUS
        // -----------------------------
        Map<String, String> ext = new LinkedHashMap<>();
        ext.put("Status", "Enabled (root)");

        sb.append("\n");
        sb.append(buildSection("Extended Diagnostics", ext));

        // -----------------------------
        // ROOT INDICATORS
        // -----------------------------
        Map<String, String> indicators = new LinkedHashMap<>();

        String[] paths = {
                "/system/bin/su", "/system/xbin/su", "/sbin/su",
                "/system/su", "/system/bin/.ext/.su",
                "/system/usr/we-need-root/su-backup",
                "/system/app/Superuser.apk", "/system/app/SuperSU.apk"
        };

        boolean any = false;
        int i = 1;

        for (String p : paths) {
            if (new File(p).exists()) {
                indicators.put("Path " + i, p);
                any = true;
                i++;
            }
        }

        if (!any) {
            indicators.put("Paths", "(no common su paths detected)");
        }

        sb.append("\n");
        sb.append(buildSection("Root Indicators", indicators));

        // -----------------------------
        // VENDOR DIAGNOSTICS
        // -----------------------------
        Map<String, String> vendor = new LinkedHashMap<>();

        String[] vendorDiag = {
                "/dev/diag", "/dev/diag_qti",
                "/efs/imei/.msl", "/efs/imei/key_str", "/efs/FactoryApp",
                "/vendor/etc/diag_mdlog", "/system/vendor/bin/diag_mdlog",
                "/system/bin/mtk_agpsd", "/system/bin/mtk_engineering", "/system/bin/emdlogger"
        };

        boolean foundVendor = false;
        int v = 1;

        for (String p : vendorDiag) {
            try {
                if (new File(p).exists()) {
                    vendor.put("Path " + v, p);
                    foundVendor = true;
                    v++;
                }
            } catch (Throwable ignore) {}
        }

        if (!foundVendor) {
            vendor.put("Status",
                    "Not exposed to third-party apps; availability depends on OEM tooling.");
        }

        sb.append("\n");
        sb.append(buildSection("Vendor Diagnostics", vendor));

    } else {

        // -----------------------------
        // NON ROOT INFO
        // -----------------------------
        Map<String, String> limited = new LinkedHashMap<>();

        limited.put("Root Status", "Device is not rooted");
        limited.put("Limitations",
                "Advanced subsystem tables and low-level hardware diagnostics require root access.");

        sb.append("\n");
        sb.append(buildSection("Root Limitations", limited));
    }

    return sb.toString();
}

    //==========================
    // NEW MEGA-UPGRADE SECTIONS (1–12)
    // ============================================================

// ===================================================================
// 1. THERMAL ENGINE / COOLING — UNIVERSAL HARDWARE EDITION (STRING MODE)
// ===================================================================

// Helper struct για να κρατάμε μια "καλύτερη" θερμοκρασία ανά ομάδα
private static class ThermalGroupReading {
    String rawName;   // π.χ. "battery_therm"
    float  tempC;     // σε βαθμούς C
    boolean valid;

    ThermalGroupReading() {
        this.valid = false;
    }

    void updateIfBetter(String name, float valueC) {
        if (!isValidTemp(valueC)) return;
        if (!valid || valueC > tempC) {
            valid   = true;
            tempC   = valueC;
            rawName = name;
        }
    }
}

// Safety check για θερμοκρασίες
private static boolean isValidTemp(float c) {
    return (c > -50f && c < 200f);
}

// ---------------------------------------------------------------
// MAPPING: thermal zone "type" → λογική ομάδα (REAL hardware only)
// ---------------------------------------------------------------
private static final String[][] THERMAL_GROUP_PATTERNS = new String[][]{
        {
            "BatteryMain",
            "battery", "batt", "batt_therm", "battery_therm",
            "fuelgauge", "bms", "bms_therm"
        },
        {
            "BatteryShell",
            "skin", "skin-therm", "case", "case-therm",
            "batt_skin", "battery_skin", "rear_case", "shell",
            "backlight_therm", "backlight", "camera"
        },
        {
            "PMIC",
            "pmic", "pm8998", "pm8150", "pmx",
            "pmic-therm", "pmic_therm",
            "pm7250", "pm7250b", "pm6450",
            "bcl", "ibat"
        },
        {
            "Charger",
            "charger", "chg", "usb", "usb-therm",
            "usb_conn_therm", "bq", "charge-therm", "charge_pump"
        },
        {
            "ModemMain",
            "modem", "mdm", "mdmss", "xbl_modem",
            "modempa", "rf-therm", "rf",
            "modem-cfg", "sub1-modem-cfg",
            "pa_therm", "pa0_therm", "pa1_therm", "pa2_therm",
            "pa0", "pa1", "pa2"
        },
        {
            "ModemAux",
            "modem1", "mdm1", "mdm2",
            "xbl_modem1", "rf1",
            "mdmss-1", "mdmss-2",
            "sub1-modem-cfg", "modem_sub",
            "modem1_pa", "rf_sub"
        }
};

// Summary struct
private static class ThermalSummary {
    int zoneCount;          // μόνο REAL hardware zones
    int coolingDeviceCount; // μόνο REAL hardware cooling devices
}

// ---------------------------------------------------------------
// Thermal scan
// ---------------------------------------------------------------
private ThermalSummary scanThermalHardware(
        ThermalGroupReading batteryMain,
        ThermalGroupReading batteryShell,
        ThermalGroupReading pmic,
        ThermalGroupReading charger,
        ThermalGroupReading modemMain,
        ThermalGroupReading modemAux
) {
    ThermalSummary summary = new ThermalSummary();

    File thermalDir = new File("/sys/class/thermal");
    File[] zones = null;
    File[] cools = null;

    try {
        if (thermalDir.exists() && thermalDir.isDirectory()) {
            zones = thermalDir.listFiles(f -> f.getName().startsWith("thermal_zone"));
            cools = thermalDir.listFiles(f -> f.getName().startsWith("cooling_device"));
        }
    } catch (Throwable ignore) { }

    summary.zoneCount          = 0;
    summary.coolingDeviceCount = 0;

    // REAL hardware thermal zones
    if (zones != null) {
        for (File z : zones) {
            try {
                String base  = z.getAbsolutePath();
                String type  = readFirstLineSafe(new File(base, "type"));
                long   milli = readLongSafe(new File(base, "temp"));
                float  c     = Float.NaN;

                if (milli == Long.MIN_VALUE) {
                    try {
                        c = Float.parseFloat(readFirstLineSafe(new File(base, "temp")));
                    } catch (Throwable ignore) {}
                } else {
                    c = milli / 1000f;
                }

                if (!isValidTemp(c)) continue;
                String group = mapTypeToGroup(type);
                if (group == null) continue;

                // μετράμε μόνο ζώνες που τελικά ανήκουν σε hardware group
                summary.zoneCount++;

                switch (group) {
                    case "BatteryMain":  batteryMain.updateIfBetter(type, c); break;
                    case "BatteryShell": batteryShell.updateIfBetter(type, c); break;
                    case "PMIC":         pmic.updateIfBetter(type, c); break;
                    case "Charger":      charger.updateIfBetter(type, c); break;
                    case "ModemMain":    modemMain.updateIfBetter(type, c); break;
                    case "ModemAux":     modemAux.updateIfBetter(type, c); break;
                }

            } catch (Throwable ignore) { }
        }
    }

    // REAL hardware cooling devices (fan / blower / pump / heatsink)
    if (cools != null) {
        for (File c : cools) {
            try {
                String type = readFirstLineSafe(new File(c.getAbsolutePath(), "type"));
                if (isHardwareCoolingDevice(type)) {
                    summary.coolingDeviceCount++;
                }
            } catch (Throwable ignore) {}
        }
    }

    return summary;
}

private String mapTypeToGroup(String rawType) {
    if (rawType == null) return null;
    String t = rawType.toLowerCase(Locale.US);

    for (String[] entry : THERMAL_GROUP_PATTERNS) {
        String label = entry[0];
        for (int i = 1; i < entry.length; i++) {
            if (t.contains(entry[i])) return label;
        }
    }
    return null;
}

// ---------------------------------------------------------------
// Cooling device filter (REAL hardware only)
// ---------------------------------------------------------------
private boolean isHardwareCoolingDevice(String rawType) {
    if (rawType == null) return false;
    String t = rawType.toLowerCase(Locale.US);

    if (t.contains("fan"))            return true;
    if (t.contains("cooling_fan"))    return true;
    if (t.contains("blower"))         return true;
    if (t.contains("pump"))           return true;
    if (t.contains("heatsink"))       return true;
    if (t.contains("radiator"))       return true;
    if (t.contains("cooling_module")) return true;

    if (t.contains("skin"))    return false;
    if (t.contains("hotspot")) return false;
    if (t.contains("virtual")) return false;

    return false;
}

private void appendHardwareCoolingDevices(StringBuilder sb) {

    File thermalDir = new File("/sys/class/thermal");
    File[] cools = null;

    try {
        if (thermalDir.exists() && thermalDir.isDirectory()) {
            cools = thermalDir.listFiles(f -> f.getName().startsWith("cooling_device"));
        }
    } catch (Throwable ignore) {}

    Map<String, String> devices = new LinkedHashMap<>();

    int shown = 0;

    if (cools != null) {
        for (File c : cools) {
            if (shown >= 5) break;

            try {
                String type = readFirstLineSafe(new File(c.getAbsolutePath(), "type"));
                if (!isHardwareCoolingDevice(type)) continue;

                devices.put(
                        c.getName(),
                        type != null ? type : "Unknown"
                );

                shown++;

            } catch (Throwable ignore) {}
        }
    }

    // --------------------------------------------------
    // EMPTY CASE
    // --------------------------------------------------
    if (devices.isEmpty()) {

        devices.put(
                "Cooling",
                "(no hardware cooling devices found) (this device uses passive cooling only)"
        );
    }

    // --------------------------------------------------
    // BUILD SECTION
    // --------------------------------------------------
    sb.append(buildSection("Cooling Devices", devices));
}

// ---------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------
private String readFirstLineSafe(File file) {
    if (file == null || !file.exists()) return "";
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line = br.readLine();
        return (line != null) ? line.trim() : "";
    } catch (Throwable ignore) {
        return "";
    }
}

private long readLongSafe(File file) {
    if (file == null || !file.exists()) return Long.MIN_VALUE;
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line = br.readLine();
        if (line == null || line.trim().isEmpty()) return Long.MIN_VALUE;
        return Long.parseLong(line.trim());
    } catch (Throwable ignore) {
        return Long.MIN_VALUE;
    }
}

// ---------------------------------------------------------------
// Labels & formatting
// ---------------------------------------------------------------
private String classifyTempLabel(float c) {
    if (!isValidTemp(c)) return "(Unknown)";
    if (c < 30f)  return "(Cool)";
    if (c < 40f)  return "(Normal)";
    if (c < 50f)  return "(Warm)";
    return "(⚠ Critical)";
}

private String formatThermalLine(String label, ThermalGroupReading r) {
    if (r == null || !r.valid)
        return String.format(Locale.US, "%-17s: N/A\n", label);

    return String.format(Locale.US, "%-17s: %.1f°C %s\n",
            label, r.tempC, classifyTempLabel(r.tempC));
}

// ---------------------------------------------------------------
// Xiaomi / POCO / Redmi Detection + Fallbacks
// ---------------------------------------------------------------
private boolean isXiaomiFamilyDevice() {
    String manu   = (Build.MANUFACTURER == null ? "" : Build.MANUFACTURER).toLowerCase();
    String brand  = (Build.BRAND == null ? "" : Build.BRAND).toLowerCase();
    String finger = (Build.FINGERPRINT == null ? "" : Build.FINGERPRINT).toLowerCase();

    return manu.contains("xiaomi") || manu.contains("redmi") || manu.contains("poco")
            || brand.contains("xiaomi") || brand.contains("redmi") || brand.contains("poco")
            || finger.contains("xiaomi") || finger.contains("redmi") || finger.contains("poco")
            || finger.contains("hyperos");
}

private float findTempByTypeKeywords(String... keywords) {
    if (keywords == null || keywords.length == 0) return Float.NaN;

    File[] zones = new File("/sys/class/thermal")
            .listFiles(f -> f.getName().startsWith("thermal_zone"));

    if (zones == null) return Float.NaN;

    float best = Float.NaN;

    for (File z : zones) {
        try {
            String type = readFirstLineSafe(new File(z, "type")).toLowerCase(Locale.US);
            boolean match = false;
            for (String k : keywords) {
                if (type.contains(k.toLowerCase(Locale.US))) { match = true; break; }
            }
            if (!match) continue;

            long milli = readLongSafe(new File(z, "temp"));
            float c;

            if (milli == Long.MIN_VALUE)
                c = Float.parseFloat(readFirstLineSafe(new File(z, "temp")));
            else
                c = milli / 1000f;

            if (!isValidTemp(c)) continue;

            if (Float.isNaN(best) || c > best) best = c;

        } catch (Throwable ignore) {}
    }

    return best;
}

private float readBatteryTempFallback() {
    String[] paths = {
            "/sys/class/power_supply/battery/temp",
            "/sys/class/power_supply/bms/temp",
            "/sys/class/power_supply/maxfg/temp"
    };

    for (String p : paths) {
        try {
            long v = readLongSafe(new File(p));
            if (v == Long.MIN_VALUE) continue;

            float c = (v > 1000f ? v / 1000f : v);
            if (isValidTemp(c)) return c;

        } catch (Throwable ignore) {}
    }
    return Float.NaN;
}

// ---------------------------------------------------------------
// OEM fallback completion
// ---------------------------------------------------------------
private void applyThermalFallbacks(
        ThermalGroupReading batteryMain,
        ThermalGroupReading batteryShell,
        ThermalGroupReading pmic,
        ThermalGroupReading charger,
        ThermalGroupReading modemMain,
        ThermalGroupReading modemAux
) {
    boolean isXiaomi = isXiaomiFamilyDevice();

    // Battery Main
    if (!batteryMain.valid) {
        float c = findTempByTypeKeywords("battery", "batt_therm", "battery_therm", "bms");
        if (!isValidTemp(c)) c = readBatteryTempFallback();
        if (isValidTemp(c)) batteryMain.updateIfBetter("fallback:battery", c);
    }

    if (isXiaomi && !batteryMain.valid) {
        float c = findTempByTypeKeywords(
                "batt_temp", "bat_therm", "battery-main",
                "battery_board", "batman"
        );
        if (!isValidTemp(c)) c = readBatteryTempFallback();
        if (isValidTemp(c)) batteryMain.updateIfBetter("xiaomi:battery", c);
    }

    // Battery Shell
    if (!batteryShell.valid) {
        float c = findTempByTypeKeywords(
                "batt_shell", "battery_shell", "shell_therm",
                "case-therm", "skin", "backlight_therm", "backlight"
        );
        if (!isValidTemp(c)) c = findTempByTypeKeywords(
                "rear_case", "back_cover", "batt_surface", "camera"
        );
        if (isValidTemp(c)) batteryShell.updateIfBetter("fallback:battery_shell", c);
    }

    if (isXiaomi && !batteryShell.valid) {
        float c = findTempByTypeKeywords(
                "batt_skin", "batt_surface",
                "back_cover", "rear_case",
                "backlight_therm", "camera"
        );
        if (isValidTemp(c)) batteryShell.updateIfBetter("xiaomi:battery_shell", c);
    }

    // PMIC
    if (!pmic.valid) {
        float c = findTempByTypeKeywords(
                "pmic", "pmic_therm", "pmic-tz",
                "pm8998", "pm660", "pm7250", "pm7250b", "pm6450"
        );
        if (!isValidTemp(c)) c = findTempByTypeKeywords("bcl", "ibat");
        if (isValidTemp(c)) pmic.updateIfBetter("fallback:pmic", c);
    }

    if (isXiaomi && !pmic.valid) {
        float c = findTempByTypeKeywords(
                "pm6150l_tz", "pm8350", "pm7250b_tz",
                "pm7250b-ibat", "pm7250b-bcl"
        );
        if (isValidTemp(c)) pmic.updateIfBetter("xiaomi:pmic", c);
    }

    // Charger
    if (!charger.valid) {
        float c = findTempByTypeKeywords("charger", "chg", "usb-therm", "charge-temp");
        if (!isValidTemp(c)) c = findTempByTypeKeywords("charge_pump", "cp_therm", "usb_conn_therm");
        if (!isValidTemp(c)) c = readBatteryTempFallback();
        if (isValidTemp(c)) charger.updateIfBetter("fallback:charger", c);
    }

    // Modem main
    if (!modemMain.valid) {
        float c = findTempByTypeKeywords(
                "modem", "mdm", "mdmss", "mdmss-3", "mdmss-2",
                "rf-therm", "modempa", "pa_therm", "pa0_therm", "pa1_therm", "pa2_therm",
                "modem-cfg"
        );
        if (isValidTemp(c)) modemMain.updateIfBetter("fallback:modem_main", c);
    }

    if (isXiaomi && !modemMain.valid) {
        float c = findTempByTypeKeywords(
                "xo_therm_modem", "modem_pa", "modem_pa_0",
                "mdmss-3", "mdmss-2", "mdmss-1", "pa0", "pa1", "pa2"
        );
        if (isValidTemp(c)) modemMain.updateIfBetter("xiaomi:modem_main", c);
    }

    // Modem aux
    if (!modemAux.valid) {
        float c = findTempByTypeKeywords(
                "modem1", "mdm2", "xbl_modem1", "rf1",
                "mdmss-1", "mdmss-2", "sub1-modem-cfg"
        );
        if (isValidTemp(c)) modemAux.updateIfBetter("fallback:modem_aux", c);
    }

    if (isXiaomi && !modemAux.valid) {
        float c = findTempByTypeKeywords(
                "modem_sub", "modem1_pa", "rf_sub",
                "mdmss-1", "mdmss-2", "sub1-modem-cfg"
        );
        if (isValidTemp(c)) modemAux.updateIfBetter("xiaomi:modem_aux", c);
    }
}

// ===================================================================
// FINAL BUILDER — CLEAN OUTPUT (REAL HARDWARE SUMMARY + TABLE)
// ===================================================================
private String buildThermalInfo() {

    StringBuilder sb = new StringBuilder();

    // ============================================================
    // HARDWARE READINGS
    // ============================================================
    ThermalGroupReading batteryMain  = new ThermalGroupReading();
    ThermalGroupReading batteryShell = new ThermalGroupReading();
    ThermalGroupReading pmic         = new ThermalGroupReading();
    ThermalGroupReading charger      = new ThermalGroupReading();
    ThermalGroupReading modemMain    = new ThermalGroupReading();
    ThermalGroupReading modemAux     = new ThermalGroupReading();

    ThermalSummary summary = scanThermalHardware(
            batteryMain, batteryShell, pmic, charger, modemMain, modemAux
    );

    applyThermalFallbacks(
            batteryMain, batteryShell, pmic, charger, modemMain, modemAux
    );

    // ============================================================
    // 🔥 TOP SUMMARY (UNIFIED)
    // ============================================================
    if (summary != null &&
        (summary.zoneCount > 0 || summary.coolingDeviceCount > 0)) {

        Map<String, String> top = new LinkedHashMap<>();

        top.put("Thermal Zones",
                String.valueOf(summary.zoneCount));

        if (summary.coolingDeviceCount == 0) {
            top.put("Cooling Devices",
                    "0 (This device uses passive cooling only)");
        } else {
            top.put("Cooling Devices",
                    String.valueOf(summary.coolingDeviceCount));
        }

        sb.append(buildSection("Thermal Summary", top));
        sb.append("\n");
    }

    // ============================================================
    // 🔥 HARDWARE THERMAL SYSTEMS
    // ============================================================
    Map<String, String> thermals = new LinkedHashMap<>();

    thermals.put("Main Modem",      formatThermalLineClean(modemMain));
    thermals.put("Secondary Modem", formatThermalLineClean(modemAux));
    thermals.put("Main Battery",    formatThermalLineClean(batteryMain));
    thermals.put("Battery Shell",   formatThermalLineClean(batteryShell));
    thermals.put("Charger Thermal", formatThermalLineClean(charger));
    thermals.put("PMIC Thermal",    formatThermalLineClean(pmic));

    sb.append(buildSection("Hardware Thermal Systems", thermals));
    sb.append("\n");

    // ============================================================
    // 🔥 COOLING DEVICES (already unified)
    // ============================================================
    appendHardwareCoolingDevices(sb);

    return sb.toString();
}

// ============================================================
// Screen / HDR / Refresh + Accurate Diagonal (UNIFIED GEL)
// ============================================================
private String buildScreenInfo() {

    Map<String, String> data = new LinkedHashMap<>();

    try {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {

            Display display = wm.getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            display.getRealMetrics(dm);

            int w = dm.widthPixels;
            int h = dm.heightPixels;
            int dpi = dm.densityDpi;

            // --------------------------------------------------
            // BASIC
            // --------------------------------------------------
            data.put("Resolution", w + " x " + h + " px");
            data.put("Density (DPI)", String.valueOf(dpi));
            data.put("Scaled Density", String.valueOf(dm.scaledDensity));

            // --------------------------------------------------
            // REFRESH
            // --------------------------------------------------
            float refresh = display.getRefreshRate();
            data.put("Refresh Rate", String.format(Locale.US, "%.1f Hz", refresh));

            if (Build.VERSION.SDK_INT >= 30) {
                float maxR = 0f;
                try {
                    Display.Mode[] modes = display.getSupportedModes();
                    for (Display.Mode m : modes) {
                        if (m.getRefreshRate() > maxR) {
                            maxR = m.getRefreshRate();
                        }
                    }
                } catch (Throwable ignore) {}

                if (maxR > 0f) {
                    data.put("Max Refresh", String.format(Locale.US, "%.1f Hz", maxR));
                }
            }

            // --------------------------------------------------
            // COLOR / HDR
            // --------------------------------------------------
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    boolean wide = display.isWideColorGamut();
                    data.put("Wide Color", wide ? "Yes" : "No");
                } catch (Throwable ignore) {}
            }

            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    Display.HdrCapabilities hc = display.getHdrCapabilities();
                    int[] types = hc.getSupportedHdrTypes();

                    data.put("HDR Modes",
                            (types == null || types.length == 0)
                                    ? "None"
                                    : types.length + " modes");

                } catch (Throwable ignore) {}
            }

            // --------------------------------------------------
            // ORIENTATION
            // --------------------------------------------------
            try {
                Configuration cfg = getResources().getConfiguration();
                data.put("Orientation",
                        cfg.orientation == Configuration.ORIENTATION_LANDSCAPE
                                ? "Landscape"
                                : "Portrait");
            } catch (Throwable ignore) {}

            // --------------------------------------------------
            // DIAGONAL
            // --------------------------------------------------
            try {
                double inchW = (double) w / dm.xdpi;
                double inchH = (double) h / dm.ydpi;
                double diag = Math.sqrt(inchW * inchW + inchH * inchH);

                data.put("Screen Size",
                        String.format(Locale.US, "%.2f\"", diag));
            } catch (Throwable ignore) {}
        }

    } catch (Throwable ignore) {}

    // --------------------------------------------------
    // BUILD MAIN SECTION
    // --------------------------------------------------
    StringBuilder sb = new StringBuilder();
    sb.append(buildSection("Screen", data));

    // --------------------------------------------------
    // ADVANCED NOTE
    // --------------------------------------------------
    Map<String, String> adv = new LinkedHashMap<>();
    adv.put("Advanced",
            "Panel ID, HBM tables and OEM tone-mapping require root access.");

    sb.append("\n");
    sb.append(buildSection("Notes", adv));

    return sb.toString();
}

// ============================================================================
// 3.  TELEPHONY / MODEM — ULTRA STABLE GEL EDITION (UNIFIED)
// ============================================================================
private String buildModemInfo() {

    Locale locale = Locale.US;

    TelephonyManager tm = null;
    SubscriptionManager sm = null;

    try { tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); } catch (Throwable ignore) {}
    try { sm = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE); } catch (Throwable ignore) {}

    Map<String, String> data = new LinkedHashMap<>();

    // ------------------------------------------------------------
    // PHONE TYPE
    // ------------------------------------------------------------
    String phoneTypeStr = "Unknown";
    try {
        if (tm != null) {
            switch (tm.getPhoneType()) {
                case TelephonyManager.PHONE_TYPE_GSM:  phoneTypeStr = "GSM";  break;
                case TelephonyManager.PHONE_TYPE_CDMA: phoneTypeStr = "CDMA"; break;
                case TelephonyManager.PHONE_TYPE_SIP:  phoneTypeStr = "SIP";  break;
                default: phoneTypeStr = "None"; break;
            }
        }
    } catch (Throwable ignore) {}

    data.put("Phone Type", phoneTypeStr);

    // ------------------------------------------------------------
    // DATA NETWORK
    // ------------------------------------------------------------
    try {
        int net = (tm != null)
                ? tm.getDataNetworkType()
                : TelephonyManager.NETWORK_TYPE_UNKNOWN;

        String netName =
                (net == TelephonyManager.NETWORK_TYPE_NR)  ? "5G NR"  :
                (net == TelephonyManager.NETWORK_TYPE_LTE) ? "4G LTE" :
                "Unknown";

        data.put("Data Network", netName);
        data.put("5G (NR) Active",
                (net == TelephonyManager.NETWORK_TYPE_NR) ? "Yes" : "No");

    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // CARRIER / COUNTRY / OPERATOR
    // ------------------------------------------------------------
    try {
        String carrier = (tm != null) ? tm.getNetworkOperatorName() : null;
        String iso     = (tm != null) ? tm.getNetworkCountryIso()   : null;
        String opCode  = (tm != null) ? tm.getNetworkOperator()     : null;

        if (iso == null || iso.trim().isEmpty()) {
            iso = Locale.getDefault().getCountry();
        }

        data.put("Carrier",
                (carrier != null && !carrier.isEmpty()) ? carrier : "Unknown");

        data.put("Country ISO",
                (iso != null) ? iso.toUpperCase(locale) : "Unknown");

        data.put("Operator Code",
                (opCode != null && !opCode.isEmpty()) ? opCode : "Unknown");

    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // SIGNAL STRENGTH
    // ------------------------------------------------------------
    try {
        if (tm != null) {
            SignalStrength ss = tm.getSignalStrength();
            if (ss != null) {
                data.put("Signal Strength",
                        ss.getLevel() + "/4");
            }
        }
    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // ROAMING
    // ------------------------------------------------------------
    try {
        boolean roaming = tm != null && tm.isNetworkRoaming();
        data.put("Roaming", roaming ? "Yes" : "No");
    } catch (Throwable ignore) {}

// ========================================================================
// ACTIVE SIMS + FALLBACK (UNIFIED)
// ========================================================================
try {
    List<SubscriptionInfo> subs = null;

    if (sm != null) {
        try { subs = sm.getActiveSubscriptionInfoList(); } catch (Throwable ignore) {}
    }

    if ((subs == null || subs.isEmpty()) && sm != null) {
        try {
            Method m = sm.getClass().getMethod("getAvailableSubscriptionInfoList");
            Object result = m.invoke(sm);
            if (result instanceof List) subs = (List<SubscriptionInfo>) result;
        } catch (Throwable ignore) {}
    }

    if (subs == null || subs.isEmpty()) {
        try {
            SubscriptionManager alt = SubscriptionManager.from(this);
            if (alt != null) subs = alt.getActiveSubscriptionInfoList();
        } catch (Throwable ignore) {}
    }

    List<SimpleSimEntry> simpleList = new ArrayList<>();

    if (subs == null || subs.isEmpty()) {
        try {
            for (int slot = 0; slot < 2; slot++) {
                int simState = tm.getSimState(slot);

                if (simState == TelephonyManager.SIM_STATE_READY ||
                    simState == TelephonyManager.SIM_STATE_NETWORK_LOCKED ||
                    simState == TelephonyManager.SIM_STATE_PIN_REQUIRED ||
                    simState == TelephonyManager.SIM_STATE_PUK_REQUIRED) {

                    String name = null;
                    try { name = tm.getSimOperatorName(); } catch (Throwable ignore) {}
                    if (name == null || name.trim().isEmpty()) name = "Unknown";

                    simpleList.add(new SimpleSimEntry(slot, name));
                }
            }
        } catch (Throwable ignore) {}
    }

    int count = 0;

    if (subs != null && !subs.isEmpty()) {
        boolean[] seen = new boolean[2];
        for (SubscriptionInfo si : subs) {
            try {
                int slot = si.getSimSlotIndex();
                if (slot >= 0 && slot <= 1 && !seen[slot]) {
                    seen[slot] = true;
                    count++;
                }
            } catch (Throwable ignore) {}
        }
    } else if (!simpleList.isEmpty()) {
        count = simpleList.size();
    }

    // 🔥 COUNT
    data.put("Active SIMs",
            (count == 0 ? "N/A" : String.valueOf(count)));

    // 🔥 DETAILS
    if (subs != null && !subs.isEmpty()) {

        boolean[] printed = new boolean[2];

        for (SubscriptionInfo si : subs) {
            try {
                int slot = si.getSimSlotIndex();
                if (slot < 0 || slot > 1 || printed[slot]) continue;

                printed[slot] = true;

                String name = (si.getCarrierName() != null)
                        ? si.getCarrierName().toString()
                        : "Unknown";

                data.put("SIM Slot " + (slot + 1), name);

            } catch (Throwable ignore) {}
        }

    } else {

        for (SimpleSimEntry e : simpleList) {
            data.put("SIM Slot " + (e.slot + 1), e.carrier);
        }
    }

} catch (Throwable ignore) {}

// ------------------------------------------------------------
// ADVANCED MODEM TABLES
// ------------------------------------------------------------
data.put("Advanced",
        isRooted
                ? "Advanced modem tables are available on this device."
                : "Advanced modem tables require root access.");
                
                return buildSection("Telephony / Modem", data);
}

// ============================================================================
// 4. Wi-Fi Advanced — GEL Ultra Stable Edition (UNIFIED)
// ============================================================================
private String buildWifiAdvancedInfo() {

    Map<String, String> data = new LinkedHashMap<>();
    Locale locale = Locale.US;

    try {
        WifiManager wm = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        PackageManager pm = getPackageManager();
        TelephonyManager tm =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (wm != null) {

            // ------------------------------------------------------------
            // HARDWARE
            // ------------------------------------------------------------
            boolean wifiHw = pm.hasSystemFeature(PackageManager.FEATURE_WIFI);
            data.put("Wi-Fi HW", wifiHw ? "Present" : "Missing");

            // ------------------------------------------------------------
            // BANDS
            // ------------------------------------------------------------
            boolean band24 = pm.hasSystemFeature(PackageManager.FEATURE_WIFI);
            data.put("2.4 GHz Support", band24 ? "Yes" : "No");

            boolean band5 = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT);
            data.put("5 GHz Support", band5 ? "Yes" : "No");

            if (Build.VERSION.SDK_INT >= 30) {
                try {
                    data.put("6 GHz Support",
                            wm.is6GHzBandSupported() ? "Yes" : "No");
                } catch (Throwable ignore) {}
            }

            // ------------------------------------------------------------
            // SECURITY
            // ------------------------------------------------------------
            if (Build.VERSION.SDK_INT >= 29) {
                try {
                    data.put("WPA3 SAE",
                            wm.isWpa3SaeSupported() ? "Yes" : "No");
                } catch (Throwable ignore) {}

                try {
                    data.put("WPA3 Suite-B",
                            wm.isWpa3SuiteBSupported() ? "Yes" : "No");
                } catch (Throwable ignore) {}
            }

            // ------------------------------------------------------------
            // RTT
            // ------------------------------------------------------------
            if (Build.VERSION.SDK_INT >= 28) {
                boolean rtt = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_RTT);
                data.put("Wi-Fi RTT", (rtt ? "Yes" : "No") + " (Indoor distance)");
            }

            // ------------------------------------------------------------
            // AWARE
            // ------------------------------------------------------------
            if (Build.VERSION.SDK_INT >= 26) {
                boolean aware = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE);
                data.put("Wi-Fi Aware", (aware ? "Yes" : "No") + " (Device proximity)");
            }

            // ------------------------------------------------------------
            // DPP
            // ------------------------------------------------------------
            if (Build.VERSION.SDK_INT >= 29) {
                boolean dpp = pm.hasSystemFeature("android.hardware.wifi.dpp");
                data.put("Easy Connect", dpp ? "Yes" : "No");
            }

            // ------------------------------------------------------------
            // PASSPOINT
            // ------------------------------------------------------------
            if (Build.VERSION.SDK_INT >= 26) {
                boolean pass = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_PASSPOINT);
                data.put("Passpoint (HS2)", pass ? "Yes" : "No");
            }

            // ------------------------------------------------------------
            // DIRECT
            // ------------------------------------------------------------
            data.put("Wi-Fi Direct",
                    pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)
                            ? "Yes" : "No");

            // ------------------------------------------------------------
            // POWER
            // ------------------------------------------------------------
            try {
                data.put("Scan Always On",
                        wm.isScanAlwaysAvailable() ? "Yes" : "No");
            } catch (Throwable ignore) {}

            // ------------------------------------------------------------
            // COUNTRY CODE (TRIPLE FALLBACK)
            // ------------------------------------------------------------
            String cc = null;

            try {
                if (tm != null) cc = tm.getNetworkCountryIso();
            } catch (Throwable ignore) {}

            if ((cc == null || cc.isEmpty()) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    Method m = WifiManager.class.getMethod("getCountryCode");
                    Object val = m.invoke(wm);
                    if (val instanceof String) cc = (String) val;
                } catch (Throwable ignore) {}
            }

            if (cc == null || cc.isEmpty()) {
                cc = Locale.getDefault().getCountry();
            }

            data.put("Country Code",
                    (cc != null && !cc.isEmpty())
                            ? cc.toUpperCase(locale)
                            : "Unknown");
        }

    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // BUILD MAIN
    // ------------------------------------------------------------
    StringBuilder sb = new StringBuilder();
    sb.append(buildSection("Wi-Fi Advanced", data));

    // ------------------------------------------------------------
    // ADVANCED NOTE
    // ------------------------------------------------------------
    Map<String, String> note = new LinkedHashMap<>();
    note.put("Advanced",
            "Regulatory region, DFS radar tables, TX power and per-band limits require root access.");

    sb.append("\n");
    sb.append(buildSection("Notes", note));

    return sb.toString();
}

// ============================
// 5. System Feature Matrix
// ============================
private String buildSystemFeaturesInfo() {

    StringBuilder sb = new StringBuilder();

    try {
        PackageManager pm = getPackageManager();
        FeatureInfo[] feats = pm.getSystemAvailableFeatures();

        if (feats == null || feats.length == 0) {
            return "5. System Feature Matrix\n\nNo system-declared features exposed by PackageManager.\n";
        }

        // ------------------------------------------------------------
        // SUMMARY
        // ------------------------------------------------------------
        Map<String, String> summary = new LinkedHashMap<>();
        summary.put("Feature Count", String.valueOf(feats.length));

        sb.append(buildSection("5. System Feature Matrix", summary));
        sb.append("\n");

        // ------------------------------------------------------------
        // FEATURE LIST (RAW — CORRECT CHOICE)
        // ------------------------------------------------------------
        sb.append("Declared Features\n");
        sb.append("--------------------------------\n");

        for (FeatureInfo fi : feats) {
            if (fi == null || fi.name == null) continue;
            sb.append("• ").append(fi.name).append("\n");
        }

        // ------------------------------------------------------------
        // NOTE
        // ------------------------------------------------------------
        Map<String, String> note = new LinkedHashMap<>();
        note.put("Note",
                "This list represents system-declared capabilities (PackageManager features). " +
                "It does NOT guarantee actual hardware presence, availability or performance.");

        sb.append("\n");
        sb.append(buildSection("Notes", note));

    } catch (Throwable t) {

        return "5. System Feature Matrix\n\nSystem feature matrix is not accessible on this device.\n";
    }

    return sb.toString();
}
 
// ============================
// 6. SELinux / Security Flags
// ============================
private String buildSecurityFlagsInfo() {

    Map<String, String> data = new LinkedHashMap<>();

    try {

        // ------------------------------------------------------------
        // KERNEL
        // ------------------------------------------------------------
        String kernel = readSysString("/proc/version");
        if (kernel != null && !kernel.isEmpty()) {
            data.put("Kernel", kernel);
        }

        // ------------------------------------------------------------
        // SECURITY PATCH
        // ------------------------------------------------------------
        String patch = Build.VERSION.SECURITY_PATCH;
        if (patch != null && !patch.isEmpty()) {
            data.put("Security Patch", patch);
        }

        // ------------------------------------------------------------
        // VERIFIED BOOT
        // ------------------------------------------------------------
        String vbState = getProp("ro.boot.verifiedbootstate");
        if (vbState != null && !vbState.isEmpty()) {
            data.put("Verified Boot", vbState);
        }

        // ------------------------------------------------------------
        // STRONGBOX
        // ------------------------------------------------------------
        boolean strongBox =
                getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_STRONGBOX_KEYSTORE);

        data.put("StrongBox", strongBox ? "Yes" : "No");

        // ------------------------------------------------------------
        // HCE / NFC
        // ------------------------------------------------------------
        boolean hce =
                getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);

        data.put("HCE / Secure NFC", hce ? "Yes" : "No");

    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // BUILD MAIN
    // ------------------------------------------------------------
    StringBuilder sb = new StringBuilder();
    sb.append(buildSection("6. SELinux / Security Flags", data));

    // ------------------------------------------------------------
    // ADVANCED NOTE
    // ------------------------------------------------------------
    Map<String, String> note = new LinkedHashMap<>();
    note.put("Advanced",
            "SELinux policy details and keymaster internals are hardware-isolated and not accessible to apps; inspection requires root or kernel-level access.");

    sb.append("\n");
    sb.append(buildSection("Notes", note));

    return sb.toString();
}

    // ============================================================
    // HELPERS (ROOT / SYSFS)
    // ============================================================
    private String readTextFile(String path, int maxLen) {
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
            } catch (Exception ignored) { }
        }
    }

    private String readSysString(String path) {
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
            } catch (Exception ignored) { }
        }
    }

     private String getProp(String key) {
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

// ============================================================
// SET TEXT FOR ALL SECTIONS — COMPLETE & FIXED (FINAL)
// ============================================================
private void populateAllSections() {

    // =====================
    // BATTERY (CORE HARDWARE)
    // =====================
    String bat = buildBatteryInfo();
    set(R.id.txtBatteryContent, bat);
    applyNeonValues(findViewById(R.id.txtBatteryContent), bat);

    // =====================
    // SCREEN
    // =====================
    String scr = buildScreenInfo();
    set(R.id.txtScreenContent, scr);
    applyNeonValues(findViewById(R.id.txtScreenContent), scr);

    // =====================
    // CAMERA
    // =====================
    String cam = buildCameraInfo();
    set(R.id.txtCameraContent, cam);
    applyNeonValues(findViewById(R.id.txtCameraContent), cam);

    // =====================
    // CONNECTIVITY
    // =====================
    String con = buildConnectivityInfo();
    set(R.id.txtConnectivityContent, con);
    applyNeonValues(findViewById(R.id.txtConnectivityContent), con);

    // =====================
    // LOCATION
    // =====================
    String loc = getLocationCapabilities();
set(R.id.txtLocationContent, loc);
applyNeonValues(findViewById(R.id.txtLocationContent), loc);

    // =====================
    // THERMAL
    // =====================
    String th = buildThermalInfo();
    set(R.id.txtThermalContent, th);
    applyNeonValues(findViewById(R.id.txtThermalContent), th);

    // =====================
    // MODEM
    // =====================
    String mod = buildModemInfo();
    set(R.id.txtModemContent, mod);
    applyNeonValues(findViewById(R.id.txtModemContent), mod);

    // =====================
    // WIFI ADVANCED
    // =====================
    String wifi = buildWifiAdvancedInfo();
    set(R.id.txtWifiAdvancedContent, wifi);
    applyNeonValues(findViewById(R.id.txtWifiAdvancedContent), wifi);

    // =====================
    // AUDIO (UNIFIED)
    // =====================
    String aud = buildAudioUnifiedInfo();
    set(R.id.txtAudioUnifiedContent, aud);
    applyNeonValues(findViewById(R.id.txtAudioUnifiedContent), aud);

    // =====================
    // SENSORS
    // =====================
    String sen = buildSensorsInfo();
    set(R.id.txtSensorsContent, sen);
    applyNeonValues(findViewById(R.id.txtSensorsContent), sen);

    // =====================
    // BIOMETRICS
    // =====================
    String bio = buildBiometricsInfo();
    set(R.id.txtBiometricsContent, bio);
    applyNeonValues(findViewById(R.id.txtBiometricsContent), bio);

    // =====================
    // NFC
    // =====================
    String nfc = getNfcBasicInfo();
set(R.id.txtNfcContent, nfc);
applyNeonValues(findViewById(R.id.txtNfcContent), nfc);

    // =====================
    // GNSS
    // =====================
    String gnss = buildGnssInfo();
    set(R.id.txtGnssContent, gnss);
    applyNeonValues(findViewById(R.id.txtGnssContent), gnss);

    // =====================
    // UWB
    // =====================
    String uwb = buildUwbInfo();
    set(R.id.txtUwbContent, uwb);
    applyNeonValues(findViewById(R.id.txtUwbContent), uwb);

    // =====================
    // USB
    // =====================
    String usb = buildUsbInfo();
    set(R.id.txtUsbContent, usb);
    applyNeonValues(findViewById(R.id.txtUsbContent), usb);

    // =====================
    // HAPTICS
    // =====================
    String hap = buildHapticsInfo();
    set(R.id.txtHapticsContent, hap);
    applyNeonValues(findViewById(R.id.txtHapticsContent), hap);

    // =====================
    // SYSTEM FEATURES
    // =====================
    String sys = buildSystemFeaturesInfo();
    set(R.id.txtSystemFeaturesContent, sys);
    applyNeonValues(findViewById(R.id.txtSystemFeaturesContent), sys);

    // =====================
    // SECURITY FLAGS
    // =====================
    String sec = buildSecurityFlagsInfo();
    set(R.id.txtSecurityFlagsContent, sec);
    applyNeonValues(findViewById(R.id.txtSecurityFlagsContent), sec);

    // =====================
    // ROOT
    // =====================
    String root = buildRootInfo();
    set(R.id.txtRootContent, root);
    applyNeonValues(findViewById(R.id.txtRootContent), root);

    // =====================
    // OTHER PERIPHERALS
    // =====================
    String oth = buildOtherPeripheralsInfo();
    set(R.id.txtOtherPeripheralsContent, oth);
    applyNeonValues(findViewById(R.id.txtOtherPeripheralsContent), oth);
}

// ============================================================
// GEL Permission Debug Mode v24 — FULL BLOCK (Logcat only)
// ============================================================
    private void showPermissionDebugInfo() {

        StringBuilder dbg = new StringBuilder();
        dbg.append("=== GEL Permission Debug Mode v24 ===\n\n");

        dbg.append("CAMERA            : ")
                .append(checkSelfPermission(Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                .append("\n");

        dbg.append("MICROPHONE        : ")
                .append(checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                        PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                .append("\n");

        dbg.append("LOCATION (FINE)   : ")
                .append(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                .append("\n");

        dbg.append("LOCATION (COARSE) : ")
                .append(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                .append("\n");

        if (Build.VERSION.SDK_INT >= 31) {

            dbg.append("BLUETOOTH SCAN    : ")
                    .append(checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) ==
                            PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                    .append("\n");

            dbg.append("BLUETOOTH CONNECT : ")
                    .append(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) ==
                            PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                    .append("\n");

            dbg.append("NEARBY DEVICES    : ")
                    .append(checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES) ==
                            PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                    .append("\n");
        } else {
            dbg.append("BLUETOOTH         : AUTO-ALLOWED (API<31)\n");
            dbg.append("NEARBY DEVICES    : AUTO-ALLOWED (API<31)\n");
        }

        dbg.append("NFC               : NO PERMISSION NEEDED\n");

        android.util.Log.e("GEL-PERMS", dbg.toString());
    }

    // ============================================================
    // SET METHOD — helper for onStart()
    // ============================================================
    private void set(int id, String txt) {
        TextView t = findViewById(id);
        if (t == null) return;
        applyNeonValues(t, txt);
    }

    // ============================================================
    // APPLY NEON VALUES + OEM GOLD + CLICKABLE PATHS
    // ============================================================
    private void applyNeonValues(TextView tv, String text) {
        if (text == null) {
            tv.setText("");
            return;
        }

        int neon = Color.parseColor(NEON_GREEN);
        int gold = Color.parseColor(GOLD_COLOR);
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);

        int start = 0;
        int len   = text.length();

        while (start < len) {
            int colon = text.indexOf(':', start);
            if (colon == -1) break;

            int lineEnd = text.indexOf('\n', colon);
            if (lineEnd == -1) lineEnd = len;

            int valueStart = colon + 1;
            while (valueStart < lineEnd && Character.isWhitespace(text.charAt(valueStart))) {
                valueStart++;
            }

            if (valueStart < lineEnd) {
                ssb.setSpan(
                        new ForegroundColorSpan(neon),
                        valueStart,
                        lineEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }

            start = lineEnd + 1;
        }

        int idxX = text.indexOf("Xiaomi");
        while (idxX != -1) {
            int end = idxX + "Xiaomi".length();
            ssb.setSpan(
                    new ForegroundColorSpan(gold),
                    idxX,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            idxX = text.indexOf("Xiaomi", end);
        }

        String os = "Open Settings";
        int idxOS = text.indexOf(os);
        if (idxOS != -1) {
            ssb.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    idxOS,
                    idxOS + os.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        boolean hasPath = false;
        int idx = text.indexOf("Settings →");

        while (idx != -1) {
            int end = text.indexOf('\n', idx);
            if (end == -1) end = len;

            final String pathText = text.substring(idx, end);

            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    handleSettingsClick(widget.getContext(), pathText);
                }
            }, idx, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            ssb.setSpan(
                    new ForegroundColorSpan(LINK_BLUE),
                    idx,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            hasPath = true;
            idx = text.indexOf("Settings →", end);
        }

        if (hasPath) {
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setHighlightColor(Color.TRANSPARENT);
        }

        tv.setText(ssb);
    }
    
// ===================================================================
// HELPERS — alignment + indent  (REQUIRED for Battery Builder)
// ===================================================================
private String padKey(String key) {
    return String.format(Locale.US, "%-22s", key);
}

private String indent(String text, int spaces) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < spaces; i++) sb.append(' ');
    sb.append(text);
    return sb.toString();
}

// ============================================================================
// MODEM HELPERS — REQUIRED FOR buildModemInfo()
// ============================================================================
private String padKeyModem(String key) {
    final int width = 20;
    if (key == null) return "";
    if (key.length() >= width) return key;
    StringBuilder sb = new StringBuilder(key);
    while (sb.length() < width) sb.append(' ');
    return sb.toString();
}

private String maskSensitive(String value) {
    if (value == null) return "N/A";
    String v = value.trim();
    if (v.length() <= 4) return "****";
    int keepStart = 4;
    int keepEnd = 2;
    String start = v.substring(0, Math.min(keepStart, v.length()));
    String end   = v.substring(Math.max(v.length() - keepEnd, keepStart));
    StringBuilder mid = new StringBuilder();
    for (int i = 0; i < v.length() - start.length() - end.length(); i++) {
        mid.append('*');
    }
    return start + mid + end;
}

// ============================================================================
// NFC BASIC INFO — REQUIRED FOR populateAllSections()
// ============================================================================
private String getNfcBasicInfo() {
    try {
        NfcManager nfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        if (nfcManager != null) {
            NfcAdapter adapter = nfcManager.getDefaultAdapter();
            if (adapter != null) {
                return "NFC Supported : Yes\nNFC Enabled   : " + (adapter.isEnabled() ? "Yes" : "No");
            }
        }
        return "NFC Supported : No";
    } catch (Throwable ignore) {
        return "NFC Supported : Unknown";
    }
}

// ============================================================================
// LOCATION CAPABILITIES — REQUIRED FOR populateAllSections()
// ============================================================================
// ============================================================================
// LOCATION CAPABILITIES — REQUIRED FOR populateAllSections()
// ============================================================================
private String getLocationCapabilities() {

    Map<String, String> data = new LinkedHashMap<>();

    try {
        PackageManager pm = getPackageManager();

        data.put("GPS HW",
                pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) ? "Yes" : "No");

        data.put("Network Location",
                pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK) ? "Yes" : "No");

        data.put("Passive Provider",
                pm.hasSystemFeature(PackageManager.FEATURE_LOCATION) ? "Yes" : "No");

    } catch (Throwable ignore) {
        return "Location Capabilities\n\nUnknown";
    }

    // ------------------------------------------------------------
    // BUILD MAIN
    // ------------------------------------------------------------
    StringBuilder sb = new StringBuilder();
    sb.append(buildSection("Location Capabilities", data));

    // ------------------------------------------------------------
    // ADVANCED NOTE
    // ------------------------------------------------------------
    Map<String, String> note = new LinkedHashMap<>();
    note.put("Advanced",
            "AGNSS, LPP, SUPL and carrier-assisted fixes require root access.");

    sb.append("\n");
    sb.append(buildSection("Notes", note));

    return sb.toString();
}

// ============================================================================
// 3. TELEPHONY / MODEM — UI REFRESH (ONE BLOCK, ONE TEXTVIEW)
// ============================================================================
private void refreshModemInfo() {
    try {
        TextView modemView = findViewById(R.id.txtModemContent);
        if (modemView != null) {
            String info = buildModemInfo();
            modemView.setText(info);
            modemView.setVisibility(View.VISIBLE);
            applyNeonValues(modemView, info);
        }
    } catch (Throwable ignore) {}
}

// ============================================================================
// LOCAL CLASS — Xiaomi SimpleSimEntry fallback
// ============================================================================
private static class SimpleSimEntry {
    int slot;
    String carrier;

    SimpleSimEntry(int s, String c) {
        slot = s;
        carrier = c;
    }
}

// ============================================================
// GEL UI HELPERS — REQUIRED
// ============================================================

private void setNeonSectionText(TextView tv, String text) {
    if (tv == null) return;
    tv.setText(text);
}

private String buildAccessInfo(String type) {

    Map<String, String> data = new LinkedHashMap<>();

    switch (type) {

        case "camera":
            data.put("Access", "Camera permission required");
            data.put("Path", "Settings → Apps → Permissions → Camera");
            break;

        case "sensors":
            data.put("Access", "Sensor access is system managed");
            break;

        default:
            data.put("Access", "Additional permissions may be required");
    }

    return buildSection("Access Info", data);
}

private void handleSettingsClick(Context ctx, String path) {
    try {
        Intent i = new Intent(Settings.ACTION_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
    } catch (Throwable ignore) {}
}

private void animateCollapse(TextView v) {
    if (v == null) return;
    v.setVisibility(View.GONE);
}

private CharSequence applyNeonToValues(String text) {

    SpannableStringBuilder ssb = new SpannableStringBuilder(text);

    String[] lines = text.split("\n", -1);

    int offset = 0;
    boolean previousLabelOnly = false;

    for (String line : lines) {

        int len = line.length();

        if (len > 0) {

            int colonIdx = line.indexOf(':');

            if (colonIdx >= 0) {

                if (colonIdx == len - 1) {

                    previousLabelOnly = true;

                } else {

                    int valueStart = offset + colonIdx + 1;

                    while (valueStart < offset + len &&
                           Character.isWhitespace(line.charAt(valueStart - offset))) {
                        valueStart++;
                    }

                    int valueEnd = offset + len;

                    if (valueStart < valueEnd) {

                        ssb.setSpan(
                                new ForegroundColorSpan(getValueColor(line)),
                                valueStart,
                                valueEnd,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                    }

                    previousLabelOnly = false;
                }

            } else if (previousLabelOnly) {

                ssb.setSpan(
                        new ForegroundColorSpan(getValueColor(line)),
                        offset,
                        offset + len,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                previousLabelOnly = false;
            }

        } else {
            previousLabelOnly = false;
        }

        offset += len + 1;
    }

    return ssb;
}

private int getValueColor(String line) {

    String l = line.toLowerCase(Locale.US);

    if (l.contains("blocked") ||
        l.contains("restricted") ||
        l.contains("no_counter") ||
        l.contains("unavailable")) {

        return Color.parseColor("#FF3B30"); // 🔴
    }

    if (l.contains("warning") ||
        l.contains("medium")) {

        return Color.parseColor("#FF9500"); // 🟡
    }

    return Color.parseColor("#39FF14"); // 🟢
}

// 🔥 END OF CLASS
}
