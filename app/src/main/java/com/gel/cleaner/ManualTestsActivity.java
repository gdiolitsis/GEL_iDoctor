// ============================================================
// ManualTestsActivity
// GEL Manual Diagnostics
// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
//
// LAB MAP (Manual Diagnostics Overview)
//
// LAB 1   — Speaker Tone Test
// LAB 2   — Speaker Frequency Sweep
// LAB 3   — Earpiece Audio Test
// LAB 4   — Microphone Recording Test (Bottom / Top)
//
// LAB 5   — Panic Frequency Analyzer
// LAB 6   — Sensors Basic Check
// LAB 7   — Sensors Motion Analysis
// LAB 8   — Camera Hardware & Path Integrity
//
// LAB 9   — Flash / Torch Test
// LAB 10  — Display & Pixel Integrity
// LAB 11  — Wi-Fi SSID Detection
// LAB 12  — Network Connectivity Test
//
// LAB 13  — Bluetooth Stack Test
// LAB 14  — Battery Health Stress Test
// LAB 15  — Charging System Analysis
// LAB 16  — Thermal Behaviour Analysis
//
// LAB 17  — CPU Stress & Stability
// LAB 18  — Storage I/O Test
// LAB 19  — RAM Pressure Detection
// LAB 20  — Reboot Pattern Analysis
//
// LAB 21  — GPS Location Fix Test
// LAB 22  — NFC Hardware Check
// LAB 23  — Audio Routing Diagnostics
// LAB 24  — Root Detection & Security Risk
//
// LAB 25  — Crash Log Detection
// LAB 26  — System Integrity Scan
// LAB 27  — Power Management Behaviour
// LAB 28  — Background Activity Check
//
// LAB 29  — Service Log Aggregation
// LAB 30  — Final Technician Summary
//
// ============================================================
// NOTE
// This activity contains all manual diagnostics used by
// GEL service mode. Each LAB produces structured logs
// consumed by LAB 30 for the final technician report.
// ============================================================

package com.gel.cleaner;

// ============================================================
// ANDROID — CORE
// ============================================================

import android.app.AppOpsManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.RippleDrawable;
import android.content.res.ColorStateList;
import android.Manifest;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfDocument;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.LocationManager;
import android.Manifest;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioRecord;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.opengl.*;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Process;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.provider.Settings;
import android.provider.MediaStore;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.util.Range;
import android.util.Size;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.VideoView;
import android.widget.TextView;
import android.widget.Toast;

import android.speech.tts.TextToSpeech;
import android.widget.CheckBox;

// ============================================================
// ANDROIDX
// ============================================================
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.gel.cleaner.UIHelpers;
import com.gel.cleaner.iDoctorEngine;

// ============================================================
// JAVA — IO / NET
// ============================================================
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Supplier;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Random;

// ============================================================
// JAVA — UTIL
// ============================================================
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

public class ManualTestsActivity extends GELAutoActivityHook {
	private AlertDialog progressDialog;
	
	private enum DrainMode {

    COUNTER,
    FULLCAP,
    FUEL_GAUGE,
    BATTERY_MANAGER,
    FALLBACK

}

private ScrollView labsScroll;
private ScrollView logScroll;
private Button btnLogToggle;
private Button btnExport;

// ============================================================
// LOG PANEL STATE
// 0 = normal, 1 = full screen
// ============================================================
private int logPanelState = 0;

private int startPercent = -1;
private long startMahThread = -1;

private boolean lab14FastPhase = false;
private long lab14FastStartTime = 0;
private int lab14FastDurationSec = 45;

private long lab14EndTime = 0;

private TextView lab14LiveStats;

private int lab14WeakLoadCounter = 0;

private int lab14RestartAttempts = 0;

private boolean lab14BoostActive = false;

private int lab14GpuIntensity = 0;

private final List<Long> lab14ChargeSamples = new ArrayList<>();

private long lab14MinCharge = Long.MAX_VALUE;
private long lab14MaxCharge = Long.MIN_VALUE;

private long lab14DeltaMah = 0;

private long lab14LastGpuAdjustTs = 0L;
private int lab14GpuMinLevel = 1;
private int lab14GpuMaxLevel = 4;
private boolean lab14WeakLoad = false;
private boolean inHardPhase = false;

private int lab14CpuThreadsCurrent = 0;
private long lab14LastCpuAdjustTs = 0L;

private long lab14LastLiveLogTs = 0;

private int lastDisplayedSecond = 0;
private long lastSnapshotTs = 0;

private String lastLiveStatus = "";

private int lab14LimiterScore = 0;
private boolean lab14LimiterLatched = false;

private float lab14CpuFreqPeak = 0f;

private GLSurfaceView gpuView;

private int cores = Runtime.getRuntime().availableProcessors();

private long lab14B_startMah = -1L;
private long lab14B_endMah = -1L;
double rateSum = 0;
int rateSamples = 0;

public static class Lab14Result {

    public boolean validDrain;
    public boolean systemLimited;

    public double drainPercentPerHour;
    public double mahPerHour;

    public long drainMah;
    public long durationMs;

    public float tempStart;
    public float tempEnd;

    public float powerMw;

    public boolean batteryBehaviourWarning;

    public String label; 
    
    public float sagAvg = Float.NaN;
    public float finalSag = Float.NaN;
}

private long lab14LastLimiterCheck = 0;
private long lab14LastBoostTs = 0;
private long lab14LastRebalanceTs = 0;
private final Object fastPhaseLock = new Object();
private long vLoad2Time = 0;

private static boolean DEBUG_MODE = false; // production clean logs
private static class PrivacySnapshot {

    // υπάρχοντα (μην τα πειράξεις)
    boolean micActive;
    boolean cameraActive;
    boolean locationActive;
    boolean vpnActive;

    // 🔴 ΠΡΟΣΘΗΚΗ (απαραίτητα για compile)
    int totalUserAppsChecked = 0;

    int userAppsWithLocation = 0;
    int userAppsWithMic = 0;
    int userAppsWithCamera = 0;
    int userAppsWithSms = 0;
}

// μέσα στο iDoctorEngine.PrivacySnapshot
int totalUserAppsChecked = 0;

private String lab14LastLabel = "Unknown";

float[] voltageUnderLoad = { Float.NaN };

float vSag1 = Float.NaN;
float vSag2 = Float.NaN;

// --------------------------------------------------
// LAB14 derived metrics
// --------------------------------------------------

final float[] powerStabilityFactor = { Float.NaN };

final boolean[] cellImbalanceRisk = { false };

double drainPercentPerHour = 0d;

// ============================================================
// LAB14 SHARED STATE
// ============================================================

private TextView counterText;

private int durationSec;

private boolean gr;

private long startMah;
private long baselineFullMah;
private long t0;
private int batteryPercent;
private long cycles;

private float lab14TempPeak = Float.NaN;

// ==========================================================
// 🔥 LAB 14 — DETECTION FIELDS (GEL CORE)
// ==========================================================

// CPU
private int[] freqStart;

// Thermal
private float tempStart;

// Voltage
private float voltStart;

// Current (estimate ή sensor)
private float currentStart;

// Performance (optional αλλά χρήσιμο)
private long perfStart;

private int lab14OptimalThreads = 1;

private long lab14RecoveryTimeMs = 0;

private GLSurfaceView lab14GLView;
private Lab14GpuRenderer lab14GpuRenderer;

private LinearLayout lab14FastBar;
private LinearLayout lab14MainBar;

// ===============================
// LAB14 extra metrics (needed by analysis)
// ===============================

private float currentStability = Float.NaN;
private float recoveryRatio = Float.NaN;
private float energyEfficiency = Float.NaN;

private int swellingScore = 0;

private boolean isLab14BMode = false;

private boolean lab14SoftPhaseStarted = false;

private long lab14bBaselineMah = -1L;

// ============================================================
// BATTERY STRESS DIAGNOSTIC STATE (shared between labs)
// ============================================================

final float[] vStart = {Float.NaN};
final float[] vLoad1 = {Float.NaN};
final float[] vRecover = {Float.NaN};
final float[] vLoad2 = {Float.NaN};

float sag1 = Float.NaN;
float sag2 = Float.NaN;
float sagAvg = Float.NaN;
float finalSag = Float.NaN;

final float[] voltageRecovery = {Float.NaN};
final float[] voltageRecoverySpeed = {Float.NaN};
final float[] voltageStability = {Float.NaN};

final float[] internalResistance = {Float.NaN};
float estimatedESR = Float.NaN;

final float[] thermalImpedance = {Float.NaN};

final boolean[] collapseRisk = {false};
final boolean[] batteryFailureRisk = {false};
final boolean[] calibrationDrift = { false };

final float[] batterySOH = {Float.NaN};

    // ============================================================
    // PERMISSION ENGINE (UNIVERSAL)
    // ============================================================
    private final int REQ_CORE_PERMS = 5000;
    private Runnable pendingAfterPermission = null;

    private final int REQ_LAB6_TOUCH = 6006;
    private final int REQ_LAB6_COLOR = 6007;
    private final int REQ_LAB13_BT_CONNECT = 1313;
    private static final int REQ_LAB12_DIALER = 12120;
    private boolean lab12DialerPending = false;

    private AlertDialog lab14RunningDialog;
    
    private volatile boolean lab14FastDone = false;
    private volatile boolean earlyPhase = false;
    
    final boolean[] lab14_systemLimited = { false };
    
    private volatile boolean __cpuBurn = false;
    
    boolean wearSignals = false;
    boolean controllerRisk = false;
    private boolean batteryBehaviourWarning = false;
    
    boolean validDrain = false;

    float voltageStart = Float.NaN;

     long drainMah = 0;

     double mahPerHour = -1;
    
    private Lab14Engine lab14Engine;
    
private volatile boolean lab14Cancelled = false;
private boolean lab14PopupShown = false;
private boolean lab14AdvisoryShown = false;
private volatile boolean lab14MainPhase = false;

// ============================================================
// LAB 14 — VOLTAGE PULSE STATE
// ============================================================
private volatile boolean lab14VoltagePulseRunning = false;
private volatile long lab14LastVoltagePulseElapsedSec = -9999L;
private volatile boolean lab14PreHardVoltagePulseDone = false;

// ============================================================
// LAB 14 — VOLTAGE BEHAVIOR TRACKING
// ============================================================
private volatile float lab14FirstHardVoltage = Float.NaN;
private volatile float lab14MidHardVoltage = Float.NaN;
private volatile float lab14LateHardVoltage = Float.NaN;
private volatile float lab14LowestHardVoltage = Float.NaN;
private volatile float lab14LastHardVoltage = Float.NaN;

// ============================================================
// LAB 14 — SELECTED STRESS THREADS
// ============================================================
private volatile int lab14SelectedStressThreads = 0;

private AlertDialog lab14Dialog;

private int __oldBrightness = -1;

private float lab14HealthPercent = Float.NaN;
private String lab14HealthLabel = "Unknown";
    
// ------------------------------------------------
// LAB14 state (must be fields for lambda/thread)
// ------------------------------------------------

private Lab14Engine.ConfidenceResult lab14Conf = null;

private int lab14AgingIndex = -1;

private String lab14AgingInterp = "N/A";

private boolean lab14BatteryBehaviourWarning = false;

private boolean lab15PopupShown = false;

    
    private final Runnable lab14VibrationLoop = new Runnable() {
    @Override
    public void run() {

        try {

            if (!lab14Running || lab14Cancelled) {
                return; // stop clean
            }

            Vibrator vib =
                    (Vibrator) getSystemService(VIBRATOR_SERVICE);

            if (vib != null && vib.hasVibrator()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vib.vibrate(
                            VibrationEffect.createOneShot(
                                    120, // πιο αισθητό
                                    VibrationEffect.DEFAULT_AMPLITUDE
                            )
                    );
                } else {
                    vib.vibrate(120);
                }
            }

        } catch (Throwable ignore) {}

        // 🔥 πιο γρήγορο loop (αλλιώς φαίνεται νεκρό)
        ui.postDelayed(this, 400);
    }
};

// ------------------------------------------------------------
// LAB14 GPU STRESS
// ------------------------------------------------------------
    private volatile boolean lab14GpuRunning = false;
    private Thread lab14GpuThread = null;
    private AlertDialog activeDialog;
    private String pendingTtsText;
    
    private boolean lab6ProCanceled = false;
    // Only true when the CURRENT run returned a fresh 96-zone grid result.
    // Never infer this from TouchGridTestActivity's legacy static fields.
    private boolean lab6TouchGridObserved = false;
    
// ============================================================
// BATTERY SCORE STATE
// ============================================================
private int batteryScore = 100;
private boolean[] variabilityDetected = { false };
    
// ============================================================  
// LAB 14 — FLAGS / UI STATE (REQUIRED)  
// ============================================================  
private volatile boolean lab14Running = false;  
private TextView lab14DotsView;  
  
private TextView lab14ProgressText;  
private LinearLayout lab14ProgressBar;  
private final int LAB14_TOTAL_SECONDS = 5 * 60; // 300 sec hard lock  
private VideoView lab14StressVideo;

private int lastSelectedStressDurationSec = 60;

// REQUIRED — used by LAB 14 / drain logic  
long currentChargeMah = -1;  

// capacity estimation  
long estimatedFullMah = -1;  

// charging state (CRITICAL for LAB 14 / 15)  
boolean charging = false;  

String source = "Unknown";

private static class BatteryInfo {

    int level = -1;
    float temperature = Float.NaN;
    String status = "Unknown";

    long currentChargeMah = -1;

    boolean charging = false;
    String source = "Unknown";
    long estimatedFullMah = -1;
}

// 🔴 SPIKE DISTURBANCE TRACKING
private float lab14SpikeMah = 0f;
private float lab14PrevCurrent = Float.NaN;
private float lab14CurrentEMA = Float.NaN;
private long lab14SpikeLastTs = 0L;

private int lab14SpikeCount = 0;
private float lab14MaxSpikeExcess = 0f;

private int pendingRestartLab14Mode = 0;
// 0 = none
// 1 = LAB 14B
// 2 = LAB 14 GAMA

private int lab14TempSource = -1;
private boolean lab14UseSnapshotTemp = false;

private boolean isLab14GamaMode = false;

private AlertDialog batterySuiteDialog;

private Handler usageHandler;
private final Object lab14Lock = new Object();
private boolean lab14SessionActive = false;
private int lab14SessionId = 0;

private volatile boolean lab14ForceStop = false;

// ============================================================  
// Battery stress internals  
// ============================================================  
private volatile boolean cpuBurnRunning = false;  
private final List<Thread> cpuBurnThreads = new ArrayList<>();  
private float oldWindowBrightness = -2f; // sentinel  
private boolean oldKeepScreenOn = false;  

    // ============================================================
    // LAB 8.1 — STATE (CLASS FIELDS)
    // ============================================================
    private ArrayList<Lab8Cam> lab8CamsFor81 = null;
    private CameraManager lab8CmFor81 = null;
private AlertDialog lab8ActiveDialog = null;
    private final Map<String, Integer> lab8CameraLogAnchors = new HashMap<>();

    // ============================================================
    // LAB 13 — BLUETOOTH RECEIVER (FINAL / AUTHORITATIVE)
    // ============================================================
    private final BroadcastReceiver lab13BtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!lab13Running && !lab13WaitingForExternal) return;
            if (intent == null) return;
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                lab13ReceiverSawConnection = true;
                lab13HadAnyConnection = true;
                if (lab13WaitingForExternal && !lab13MonitoringStarted) {
                    lab13WaitingForExternal = false;
                    if (lab13StatusText != null) {
                        lab13StatusText.setText(
                                AppLang.isGreek(context)
                                    ? "Παρατηρήθηκε Bluetooth ACL σύνδεση — αρχίζει η δειγματοληψία"
                                    : "Bluetooth ACL connection observed — sampling begins");
                    }
                    startLab13Monitor60s();
                }
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                // The old receiver never set this flag. ACL events are
                // aggregate and may involve a DIFFERENT paired device.
                if (lab13Running) lab13ReceiverSawDisconnection = true;
            }
        }
    };

    // ✅ Activity field (NOT inside receiver)
    private boolean lab13WaitTtsPlayed = false;

    // ============================================================
    // GLOBAL TTS (for labs that need shared access)
    // ============================================================
    private TextToSpeech[] tts = new TextToSpeech[1];
    private boolean[] ttsReady = { false };

    // ============================================================
    // GLOBAL TTS PREF — WRAPPER TO AppTTS (SINGLE AUTHORITY)
    // ============================================================
    private void loadTtsMuted() {
        // handled centrally by AppTTS
    }

    private boolean isTtsMuted() {
        return AppTTS.isMuted(this);
    }

    private void setTtsMuted(boolean muted) {
        AppTTS.setMuted(this, muted);
    }

    // ============================================================
    // GLOBAL PREFS ALIAS (used by labs + helpers)
    // ============================================================
    private SharedPreferences p;

    // ============================================================
    // GEL DIAG — GLOBAL PREFS (CLASS LEVEL)
    // ============================================================
    private SharedPreferences prefs;

    // ============================================================
    // GEL PRO — CENTRAL ENTITLEMENT GATE
    // Single source of truth for professional features.
    // Google Play Billing will update "active" after verified purchase.
    // ============================================================

    private boolean isGelProActive() {
        return GelProEntitlement.isActive(this);
    }

    private boolean requireGelPro(String featureName) {
        if (isGelProActive()) {
            return true;
        }

        showGelProLockedDialog(featureName);
        return false;
    }

    private void showGelProLockedDialog(String featureName) {

        final boolean[] popupGreek = { AppLang.isGreek(this) };

        final boolean isLab28 =
                featureName != null && featureName.contains("28");

        final boolean isLab30 =
                featureName != null && featureName.contains("30");

        final String featureEl;
        final String featureEn;

        if (isLab28) {
            featureEl = "LAB 28 — Σταθερότητα Υλικού & Ακεραιότητα Διασυνδέσεων";
            featureEn = "LAB 28 — Hardware Stability & Interconnect Integrity";
        } else if (isLab30) {
            featureEl = "LAB 30 — GEL Device Certification";
            featureEn = "LAB 30 — GEL Device Certification";
        } else {
            featureEl = "LAB 14A — Υγεία και Καταπόνηση Μπαταρίας";
            featureEn = "LAB 14A — Battery Health and Stress Diagnosis";
        }

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(true);

        LinearLayout root = buildGELPopupRoot(this);
        root.setOrientation(LinearLayout.VERTICAL);

        LinearLayout header =
                buildPopupHeader(
                        this,
                        popupGreek[0]
                                ? "GEL PRO — Επαγγελματική λειτουργία"
                                : "GEL PRO — Professional Feature"
                );

        TextView headerTitle = null;
        if (header.getChildCount() > 0 &&
                header.getChildAt(0) instanceof TextView) {
            headerTitle = (TextView) header.getChildAt(0);
        }
        root.addView(header);

        LinearLayout langRow = new LinearLayout(this);
        langRow.setOrientation(LinearLayout.HORIZONTAL);
        langRow.setGravity(Gravity.CENTER);
        langRow.setPadding(0, 0, 0, dp(10));

        Button btnEl = gelButton(this, "EL", 0xFF202020);
        Button btnEn = gelButton(this, "EN", 0xFF202020);

        LinearLayout.LayoutParams langLp1 =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );
        langLp1.setMargins(dp(2), 0, dp(4), 0);

        LinearLayout.LayoutParams langLp2 =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );
        langLp2.setMargins(dp(4), 0, dp(2), 0);

        btnEl.setLayoutParams(langLp1);
        btnEn.setLayoutParams(langLp2);

        langRow.addView(btnEl);
        langRow.addView(btnEn);
        root.addView(langRow);

        ScrollView contentScroll = new ScrollView(this);
        contentScroll.setFillViewport(false);
        contentScroll.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);

        LinearLayout.LayoutParams scrollLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                );
        contentScroll.setLayoutParams(scrollLp);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, 0, 0, dp(6));

        TextView feature = new TextView(this);
        feature.setTextColor(Color.WHITE);
        feature.setTextSize(16f);
        feature.setTypeface(null, Typeface.BOLD);
        feature.setGravity(Gravity.CENTER_HORIZONTAL);
        feature.setPadding(0, dp(4), 0, dp(12));
        content.addView(feature);

        TextView msg = new TextView(this);
        msg.setTextColor(0xFF00FF9C);
        msg.setTextSize(15f);
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        msg.setLineSpacing(0f, 1.15f);
        content.addView(msg);

        contentScroll.addView(content);
        root.addView(contentScroll);

        TextView price = new TextView(this);
        price.setTextColor(0xFFFFD700);
        price.setTextSize(17f);
        price.setTypeface(null, Typeface.BOLD);
        price.setGravity(Gravity.CENTER);
        price.setPadding(0, dp(10), 0, dp(8));
        root.addView(price);

        Button notNowBtn =
                gelButton(
                        this,
                        popupGreek[0] ? "Όχι τώρα" : "Not now",
                        0xFF202020
                );
        root.addView(notNowBtn);

        Button proBtn =
                gelButton(
                        this,
                        "GEL PRO",
                        0xFF0F8A3B
                );
        root.addView(proBtn);

        final TextView finalHeaderTitle = headerTitle;

        Runnable renderLanguage = () -> {

            final boolean gr = popupGreek[0];

            if (finalHeaderTitle != null) {
                finalHeaderTitle.setText(
                        gr
                                ? "GEL PRO — Επαγγελματική λειτουργία"
                                : "GEL PRO — Professional Feature"
                );
            }

            feature.setText(gr ? featureEl : featureEn);

            if (isLab28) {
                msg.setText(
                        gr
                                ? "Προηγμένη ανάλυση συμπτωμάτων για τεχνικούς\n\n" +
                                  "Το LAB 28 συσχετίζει ευρήματα από τα διαγνωστικά της συσκευής και ελέγχει για μοτίβα αστάθειας όπως:\n\n" +
                                  "✓ Τυχαίες επανεκκινήσεις ή ξαφνικά resets\n" +
                                  "✓ Αστάθεια δικτύου και απώλειες σήματος\n" +
                                  "✓ Διακοπτόμενη ή ασταθή συμπεριφορά αισθητήρων\n" +
                                  "✓ Ασυνήθιστες θερμικές μεταβολές\n" +
                                  "✓ Αστάθεια φόρτισης και power glitches\n" +
                                  "✓ Ενδείξεις αστάθειας PMIC / fuel-gauge\n" +
                                  "✓ Ενδείξεις πιθανής αστάθειας baseband\n" +
                                  "✓ Πιθανά μοτίβα διακοπτόμενης επαφής ή διασύνδεσης\n" +
                                  "✓ Συσχέτιση πολλαπλών πηγών διαγνωστικών δεδομένων\n\n" +
                                  "Τελική εκτίμηση σταθερότητας υλικού\n\n" +
                                  "Το LAB 28 είναι εργαλείο τεχνικού προελέγχου βάσει συμπτωμάτων. Δεν επιβεβαιώνει από μόνο του βλάβη κόλλησης ή συγκεκριμένη βλάβη hardware."
                                : "Advanced symptom analysis for technicians\n\n" +
                                  "LAB 28 correlates diagnostic findings across the device and checks for instability patterns such as:\n\n" +
                                  "✓ Random reboots or sudden resets\n" +
                                  "✓ Network and signal instability\n" +
                                  "✓ Intermittent sensor behaviour\n" +
                                  "✓ Abnormal thermal patterns\n" +
                                  "✓ Charging instability and power glitches\n" +
                                  "✓ PMIC / fuel-gauge instability indicators\n" +
                                  "✓ Possible baseband instability indicators\n" +
                                  "✓ Patterns consistent with intermittent contact or interconnect issues\n" +
                                  "✓ Correlation of multiple diagnostic evidence sources\n\n" +
                                  "Final hardware stability assessment\n\n" +
                                  "LAB 28 is a symptom-based technician triage tool. It does not by itself confirm solder defects or a specific hardware fault."
                );

            } else if (isLab30) {
                msg.setText(
                        gr
                                ? "Ολοκληρωμένη αξιολόγηση και πιστοποίηση συσκευής\n\n" +
                                  "Το LAB 30 συνδυάζει τα διαγνωστικά δεδομένα της συσκευής και δημιουργεί συνολική επαγγελματική αξιολόγηση:\n\n" +
                                  "✓ Συνολική βαθμολογία υγείας συσκευής\n" +
                                  "✓ Αξιολόγηση κατάστασης μπαταρίας\n" +
                                  "✓ Έλεγχος θερμικής συμπεριφοράς\n" +
                                  "✓ Αξιολόγηση RAM και αποθηκευτικού χώρου\n" +
                                  "✓ Ανάλυση σταθερότητας hardware\n" +
                                  "✓ Αξιολόγηση απόδοσης συσκευής\n" +
                                  "✓ Έλεγχος ασφάλειας και Root indicators\n" +
                                  "✓ Αξιολόγηση ιδιωτικότητας\n" +
                                  "✓ Hardware Risk Score\n" +
                                  "✓ Συσχέτιση αποτελεσμάτων από πολλαπλά GEL Labs\n" +
                                  "✓ Έλεγχος συνέπειας και ιστορικής τάσης διαγνώσεων\n" +
                                  "✓ Τελικό διαγνωστικό συμπέρασμα\n\n" +
                                  "GEL Device Classification\n" +
                                  "GEL CERTIFIED • GEL VERIFIED • INSPECTION ADVISED\n\n" +
                                  "GEL Hardware Diagnostic Certification\n" +
                                  "με ημερομηνία/ώρα και μοναδικό Diagnostic ID."
                                : "Comprehensive device assessment and certification\n\n" +
                                  "LAB 30 combines diagnostic data across the device to generate a professional overall assessment:\n\n" +
                                  "✓ Overall device health score\n" +
                                  "✓ Battery condition assessment\n" +
                                  "✓ Thermal behavior evaluation\n" +
                                  "✓ RAM and storage assessment\n" +
                                  "✓ Hardware stability analysis\n" +
                                  "✓ Device performance score\n" +
                                  "✓ Security and Root indicator assessment\n" +
                                  "✓ Privacy assessment\n" +
                                  "✓ Hardware Risk Score\n" +
                                  "✓ Correlation of results across multiple GEL Labs\n" +
                                  "✓ Diagnostic consistency and historical trend analysis\n" +
                                  "✓ Final diagnostic verdict\n\n" +
                                  "GEL Device Classification\n" +
                                  "GEL CERTIFIED • GEL VERIFIED • INSPECTION ADVISED\n\n" +
                                  "GEL Hardware Diagnostic Certification\n" +
                                  "with diagnostic timestamp and unique Diagnostic ID."
                );

            } else {
                msg.setText(
                        gr
                                ? "Επαγγελματική διάγνωση μπαταρίας\n\n" +
                                  "Το LAB 14A πραγματοποιεί προηγμένο stress test και αξιολογεί:\n\n" +
                                  "✓ Πτώση τάσης υπό φορτίο (Voltage Sag)\n" +
                                  "✓ Δυναμική / εσωτερική αντίσταση μπαταρίας\n" +
                                  "✓ Ανάκαμψη τάσης μετά από φορτίο\n" +
                                  "✓ Ικανότητα παροχής ισχύος\n" +
                                  "✓ Θερμική συμπεριφορά υπό καταπόνηση\n" +
                                  "✓ Ισορροπία και ηλεκτρική συμπεριφορά κυψελών\n" +
                                  "✓ Ενδείξεις Voltage Collapse\n" +
                                  "✓ Ενδείξεις διόγκωσης / αστάθειας\n" +
                                  "✓ Calibration Drift\n" +
                                  "✓ Σύγκριση επαναλαμβανόμενων διαγνωστικών μετρήσεων\n\n" +
                                  "Τελική αξιολόγηση κατάστασης μπαταρίας"
                                : "Professional battery diagnostics\n\n" +
                                  "LAB 14A performs an advanced stress test and evaluates:\n\n" +
                                  "✓ Voltage sag under load\n" +
                                  "✓ Dynamic / internal battery resistance\n" +
                                  "✓ Voltage recovery after load\n" +
                                  "✓ Power delivery capability\n" +
                                  "✓ Thermal behavior under stress\n" +
                                  "✓ Cell balance and electrical behavior\n" +
                                  "✓ Voltage collapse indicators\n" +
                                  "✓ Swelling / instability indicators\n" +
                                  "✓ Calibration drift\n" +
                                  "✓ Comparison of repeated diagnostic measurements\n\n" +
                                  "Final battery condition assessment"
                );
            }

            price.setText(
                    gr
                            ? "Συνδρομή: 4,99 € / μήνα"
                            : "Subscription: €4.99 / month"
            );

            notNowBtn.setText(gr ? "Όχι τώρα" : "Not now");
            proBtn.setText("GEL PRO");

            btnEl.setAlpha(gr ? 1.0f : 0.55f);
            btnEn.setAlpha(gr ? 0.55f : 1.0f);

            contentScroll.post(() -> contentScroll.scrollTo(0, 0));
        };

        btnEl.setOnClickListener(v -> {
            popupGreek[0] = true;
            renderLanguage.run();
        });

        btnEn.setOnClickListener(v -> {
            popupGreek[0] = false;
            renderLanguage.run();
        });

        renderLanguage.run();

        b.setView(root);

        final AlertDialog d = b.create();

        notNowBtn.setOnClickListener(v -> d.dismiss());

        // Temporary until Google Play Billing purchase flow is connected.
        proBtn.setOnClickListener(v -> { d.dismiss(); startActivity(new android.content.Intent(this, GelProAccountActivity.class).putExtra(GelProAccountActivity.EXTRA_ACCESS_CHOICES, true)); });

        d.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK &&
                    event.getAction() == KeyEvent.ACTION_UP) {
                dialog.dismiss();
                return true;
            }
            return false;
        });

        if (!isFinishing() && !isDestroyed()) {

            d.show();
            com.gel.cleaner.GelButtonLayout.fitDialog(d);

            if (d.getWindow() != null) {

                d.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT)
                );

                DisplayMetrics dm = getResources().getDisplayMetrics();

                int safeWidth =
                        Math.min(
                                dm.widthPixels - dp(20),
                                dp(560)
                        );

                int safeHeight =
                        (int) (dm.heightPixels * 0.92f);

                d.getWindow().setLayout(
                        Math.max(dp(280), safeWidth),
                        safeHeight
                );
            }
        }
    }

    // ============================================================
    // LAB 3 — STATE (CLASS LEVEL)
    // ============================================================
    private volatile boolean lab3WaitingUser = false;
    private volatile int lab3RoutedOutputType = -1;
    private int lab3OldMode = AudioManager.MODE_NORMAL;
    private boolean lab3OldSpeaker = false;
    private boolean lab3OldMicMute = false;

    private volatile boolean lab4HumanFallbackUsed = false;
    // Serialize Labs 1/2 tone challenges and prevent incomplete PDF export.
    private final AtomicBoolean lab12AudioBusy = new AtomicBoolean(false);
    private final AtomicBoolean lab5VibrationPending = new AtomicBoolean(false);
    private final AtomicBoolean lab3GuidedPending = new AtomicBoolean(false);
    private final AtomicBoolean lab4GuidedPending = new AtomicBoolean(false);
    private final AtomicBoolean lab4Cancelled = new AtomicBoolean(false);
    private volatile boolean lab3Cancelled = false;
    private volatile AlertDialog lab3ActiveDialog;
    private volatile AlertDialog lab4ActiveDialog;
    private volatile boolean lab4AudioStateSaved = false;
    private int lab4OriginalMode = AudioManager.MODE_NORMAL;
    private boolean lab4OriginalSpeaker = false;
    private boolean lab4OriginalMute = false;

    // ============================================================
    // SERVICE LOG SESSION FLAG (CRITICAL)
    // ============================================================
    private boolean serviceLogInit = false;

    // ============================================================
    // GLOBAL FINAL SCORE FIELDS (used by Lab 29 PDF Report)
    // ============================================================
    private String lastScoreHealth      = "N/A";
    private String lastScorePerformance = "N/A";
    private String lastScoreSecurity    = "N/A";
    private String lastScorePrivacy     = "N/A";
    private String lastFinalVerdict     = "N/A";

// ============================================================
// LAB 13 — STATE / FIELDS (FINAL)
// ============================================================

// runtime state
private volatile boolean lab13Running = false;
private volatile boolean lab13MonitoringStarted = false;
private volatile boolean lab13HadAnyConnection = false;
private volatile boolean lab13AssumedConnected = false;
private boolean lab13LastConnected = false;

// counters
private int lab13DisconnectEvents = 0;
private int lab13ReconnectEvents  = 0;
private int lab13Seconds = 0;
private long lab13StartMs = 0L;

// flags
private boolean lab13SkipExternalTest = false;

// bluetooth handles
private BluetoothManager lab13Bm;
private BluetoothAdapter lab13Ba;

// UI (monitor dialog)
private AlertDialog lab13Dialog;
private TextView lab13StatusText;
private TextView lab13CounterText;
private TextView lab13DotsView;
private LinearLayout lab13ProgressBar;

// handler
private final Handler lab13Handler =
        new Handler(Looper.getMainLooper());
        
// ============================================================
// LAB 13 — HARD SYNC FLAGS
// ============================================================
private volatile boolean lab13ReceiverSawConnection = false;
private volatile boolean lab13ReceiverSawDisconnection = false;
private volatile boolean lab13WaitingForExternal = false;
private boolean lab13AdapterObservedThroughout = true;
private int lab13AmbiguousTransitions = 0;
private int lab13MonitorGeneration = 0;

// ============================================================
// LAB 15 — FLAGS (DO NOT MOVE)
// ============================================================

private volatile boolean lab15Running  = false;
private volatile boolean lab15Finished = false;

private volatile boolean lab15FlapUnstable = false;
private volatile boolean lab15OverTempDuringCharge = false;

private AlertDialog lab15Dialog;
private TextView lab15StatusText;
private LinearLayout lab15ProgressBar;
private Button lab15ExitBtn;
private TextView lab15CounterText;

// LAB 15 — Thermal Correlation
private float lab15batTempStart = Float.NaN;
private float lab15batTempPeak  = Float.NaN;
private float lab15batTempEnd   = Float.NaN;
// LAB 15 / LAB 16 thermal aliases (keep legacy names)
private float startBatteryTemp = Float.NaN;
private float endBatteryTemp   = Float.NaN;
// LAB 15 — Charging strength state (MUST be fields)
private boolean lab15_strengthKnown = false;
private boolean lab15_strengthWeak  = false;
private boolean lab15_systemLimited = false;

private static final int LAB15_TOTAL_SECONDS = 180;

// ============================================================  
// TELEPHONY SNAPSHOT — Passive system probe (no side effects)  
// ============================================================  
private static class TelephonySnapshot {
    boolean airplaneOn = false;
    int simState = TelephonyManager.SIM_STATE_UNKNOWN;
    boolean simReady = false;
    boolean simStateKnown = false;
    int serviceState = ServiceState.STATE_OUT_OF_SERVICE; // legacy consumer only
    boolean inService = false;
    boolean serviceKnown = false;
    int dataState = TelephonyManager.DATA_UNKNOWN;
    boolean dataStateKnown = false;
    boolean signalLevelKnown = false;
    int signalLevel = -1; // Android coarse 0..4, NOT measured dBm or modem health

    // Legacy is "INTERNET capability advertised", NOT validated reachability.
    // Lab 11 must never use this field to judge cellular connectivity.
    boolean hasInternet = false;

    boolean defaultNetworkPresent = false;
    boolean defaultCapsKnown = false;
    boolean defaultInternetAdvertised = false;
    boolean defaultValidated = false;
    boolean defaultCellular = false;
    boolean defaultWifi = false;
    boolean defaultVpn = false;
    boolean defaultCaptivePortal = false;
}

// ================= SNAPSHOTS CONTAINERS =================  

private static class StorageSnapshot {  
    long totalBytes, freeBytes, usedBytes;  
    int pctFree;  
}  

private static class AppsSnapshot {  
    int userApps, systemApps, totalApps;  
}  

private static class RamSnapshot {  
    long totalBytes, freeBytes;  
    int pctFree;  
}  

private static class SecuritySnapshot {  
    boolean lockSecure;  
    boolean adbUsbOn;  
    boolean adbWifiOn;  
    boolean devOptionsOn;  
    boolean rootSuspected;  
    boolean testKeys;  
    String securityPatch;  
}  

// ============================================================  
// CORE UI  
// ============================================================  
private ScrollView scroll;  
private TextView txtLog;  
private Handler ui;  

// ============================================================  
// SECTION STATE TRACKING (AUTO-CLOSE GROUPS)  
// ============================================================  
private final List<LinearLayout> allSectionBodies  = new ArrayList<>();  
private final List<Button>       allSectionHeaders = new ArrayList<>();  

// ============================================================  
// Lab 10 location permission internals  
// ============================================================  
private static final int REQ_LOCATION_LAB10 = 11012;  
private Runnable pendingLab10AfterPermission = null;  

/* =========================================================  
 * FIX: APPLY SAVED LANGUAGE TO THIS ACTIVITY  
 * ========================================================= */  
@Override  
protected void attachBaseContext(Context base) {  
    super.attachBaseContext(LocaleHelper.apply(base));  
}  

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // ============================================================
    // REMOTE DIAGNOSTICS — CUSTOMER SIDE
    // Starts only when this installation has a CONNECTED customer
    // Service Session. Technician installations remain unaffected.
    // ============================================================
    GELRemoteDiagnosticsSync.start(this);

    lab14Engine = new Lab14Engine(this);

    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        if (isFinishing() || isDestroyed()) return;
    }, 120);

    prefs = getSharedPreferences("GEL_DIAG", MODE_PRIVATE);
    p = prefs;
    ui = new Handler(Looper.getMainLooper());

    initTTS();

// ============================================================
// ROOT (VERTICAL - NO OVERLAY)
// ============================================================
LinearLayout root = new LinearLayout(this);
root.setOrientation(LinearLayout.VERTICAL);
setContentView(root);

// ============================================================
// LABS (TOP - SCROLLABLE, FLEX)
// ============================================================
labsScroll = new ScrollView(this);
labsScroll.setFillViewport(true);

LinearLayout.LayoutParams labsParams =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f // 🔥 παίρνει όλο τον διαθέσιμο χώρο
        );
labsScroll.setLayoutParams(labsParams);

LinearLayout labsContainer = new LinearLayout(this);
labsContainer.setOrientation(LinearLayout.VERTICAL);
labsContainer.setPadding(dp(16), dp(16), dp(16), dp(16));

labsScroll.addView(labsContainer);

// ============================================================
// LOG AREA (BOTTOM - INDEPENDENT SCROLL)
// ============================================================
logScroll = new ScrollView(this);
logScroll.setFillViewport(true);
logScroll.setBackgroundColor(0xEE000000);

LinearLayout.LayoutParams logParams =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(250) // σταθερό ύψος, μεγάλο και scrollable
        );
logScroll.setLayoutParams(logParams);

LinearLayout logContainer = new LinearLayout(this);
logContainer.setOrientation(LinearLayout.VERTICAL);

txtLog = new TextView(this);
txtLog.setTextSize(13f);
txtLog.setTextColor(0xFFFFFFFF);
txtLog.setPadding(dp(12), dp(12), dp(12), dp(12));
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    txtLog.setText(Html.fromHtml(
            "<b>" + getString(R.string.manual_log_title) + "</b><br>",
            Html.FROM_HTML_MODE_LEGACY
    ));
} else {
    txtLog.setText(Html.fromHtml(
            "<b>" + getString(R.string.manual_log_title) + "</b><br>"
    ));
}

logContainer.addView(txtLog);
logScroll.addView(logContainer);

// ============================================================
// LOG PANEL TOGGLE (ARROWS — NO SWIPE)
// ============================================================
btnLogToggle = new com.gel.cleaner.GelButton(this);
btnLogToggle.setText("LOGS ▲");
btnLogToggle.setAllCaps(false);
btnLogToggle.setTextColor(0xFFFFFFFF);
btnLogToggle.setBackgroundResource(R.drawable.gel_btn_outline_selector);
btnLogToggle.setPadding(dp(16), dp(8), dp(16), dp(8));

LinearLayout.LayoutParams logToggleParams =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

logToggleParams.setMargins(dp(12), dp(6), dp(12), dp(4));
btnLogToggle.setLayoutParams(logToggleParams);

btnLogToggle.setOnClickListener(v -> toggleLogPanel());

// ============================================================
// EXPORT BUTTON (BOTTOM)
// ============================================================
btnExport = new com.gel.cleaner.GelButton(this);
btnExport.setText(getString(R.string.export_report_title));
btnExport.setAllCaps(false);
btnExport.setTextColor(0xFFFFFFFF);
btnExport.setBackgroundResource(R.drawable.gel_btn_outline_selector);
btnExport.setGravity(Gravity.CENTER);
btnExport.setSingleLine(false);
btnExport.setIncludeFontPadding(true);
btnExport.setMinHeight(dp(72));
btnExport.setMinimumHeight(dp(72));
btnExport.setPadding(dp(16), dp(14), dp(16), dp(14));

LinearLayout.LayoutParams btnParams =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

btnParams.setMargins(dp(12), dp(8), dp(12), dp(12));
btnExport.setLayoutParams(btnParams);

btnExport.setOnClickListener(v -> {
    startActivity(new Intent(this, DiagnosisMenuActivity.class));
});

// ============================================================
// ADD ORDER (CRITICAL)
// ============================================================
root.addView(labsScroll);
root.addView(btnLogToggle);
root.addView(logScroll);
root.addView(btnExport);

// ============================================================
// INIT
// ============================================================
scrollLogToBottom();
UIHelpers.applyPressEffectRecursive(getWindow().getDecorView());

final boolean gr = AppLang.isGreek(this);

// 🔴 DEFAULT STATE — NORMAL LOG PANEL
logScroll.post(() -> setLogPanelState(0));

    // ============================================================
    // TITLE
    // ============================================================
    TextView title = new TextView(this);
    title.setText(getString(R.string.manual_hospital_title));
    title.setTextSize(20f);
    title.setTextColor(Color.WHITE);
    title.setGravity(Gravity.CENTER_HORIZONTAL);
    title.setPadding(0, 0, 0, dp(6));
    labsContainer.addView(title);

    TextView sub = new TextView(this);
    sub.setText(getString(R.string.manual_hospital_sub));
    sub.setTextSize(13f);
    sub.setTextColor(0xFF39FF14);
    sub.setGravity(Gravity.CENTER_HORIZONTAL);
    sub.setPadding(0, 0, 0, dp(12));
    labsContainer.addView(sub);

    lab14DotsView = new TextView(this);
    lab14DotsView.setText("•");
    lab14DotsView.setTextSize(22f);
    lab14DotsView.setTextColor(0xFF39FF14);
    lab14DotsView.setPadding(0, dp(6), 0, dp(10));
    lab14DotsView.setGravity(Gravity.CENTER_HORIZONTAL);
    labsContainer.addView(lab14DotsView);

    // ============================================================
    // SECTION 1: AUDIO & VIBRATION — LABS 1-5
    // ============================================================
    LinearLayout body1 = makeSectionBody();
    Button header1 = makeSectionHeader(getString(R.string.manual_cat_1), body1);
    labsContainer.addView(header1);
    labsContainer.addView(body1);

    body1.addView(makeTestButton(
            gr ? "1. Δοκιμή Τόνου Ηχείου" : "1. Speaker Tone Test",
            this::lab1SpeakerTone));

    body1.addView(makeTestButton(
            gr ? "2. Έλεγχος Συχνοτήτων Ηχείου" : "2. Speaker Frequency Sweep Test",
            this::lab2SpeakerSweep));

    body1.addView(makeTestButton(
            gr ? "3. Έλεγχος Ακουστικού Κλήσης" : "3. Earpiece Call Check",
            this::lab3EarpieceManual));

    body1.addView(makeTestButton(
            gr ? "4. Έλεγχος Ποιότητας Κλήσης Μικροφώνου / Ακουστικού"
               : "4. Microphone / Earpiece Call Quality Check",
            this::lab4MicManual));

    body1.addView(makeTestButton(
            gr ? "5. Δοκιμή Δόνησης" : "5. Vibration Motor Test",
            this::lab5Vibration));

    // ============================================================
    // SECTION 2: DISPLAY & SENSORS — LABS 6-9
    // ============================================================
    LinearLayout body2 = makeSectionBody();
    Button header2 = makeSectionHeader(getString(R.string.manual_cat_2), body2);
    labsContainer.addView(header2);
    labsContainer.addView(body2);

    body2.addView(makeTestButton(
            gr ? "6. Έλεγχος Οθόνης / Αφής" : "6. Display / Touch Inspection",
            this::lab6DisplayTouch));

    body2.addView(makeTestButton(
            gr ? "7. Ελεγχος Περιστροφής & Αισθητήρα Εγγύτητας"
               : "7. Rotation & Proximity Sensors Check",
            this::lab7RotationAndProximityManual));

    body2.addView(makeTestButton(
            gr ? "8. Ελεγχος Hardware Καμερας & Preview Path"
               : "8. Camera Hardware & Preview Path Check",
            this::lab8CameraHardwareCheck));

    body2.addView(makeTestButton(
            gr ? "9. Έλεγχος Αισθητήρων" : "9. Sensors Check",
            this::lab9SensorsCheck));

    // ============================================================
    // SECTION 3: WIRELESS & CONNECTIVITY — LABS 10-13
    // ============================================================
    LinearLayout body3 = makeSectionBody();
    Button header3 = makeSectionHeader(getString(R.string.manual_cat_3), body3);
    labsContainer.addView(header3);
    labsContainer.addView(body3);

    body3.addView(makeTestButton(
            gr ? "10. Έλεγχος Wi-Fi" : "10. Wi-Fi Connection Check",
            this::lab10WifiConnectivityCheck));

    body3.addView(makeTestButton(
            gr ? "11. Διάγνωση Δικτύου Κινητού" : "11. Mobile Network Diagnostic",
            this::lab11MobileDataDiagnostic));

    body3.addView(makeTestButton(
            gr ? "12. Ανάλυση Τηλεφωνικής Λειτουργίας"
               : "12. Telephony Function Analysis",
            this::lab12CallFunctionInterpretation));

    body3.addView(makeTestButton(
            gr ? "13. Έλεγχος Σύνδεσης Bluetooth" : "13. Bluetooth Connectivity Check",
            this::lab13BluetoothConnectivityCheck));

    // ============================================================
    // SECTION 4: BATTERY & THERMAL — LABS 14-17
    // ============================================================
    LinearLayout body4 = makeSectionBody();
    Button header4 = makeSectionHeader(getString(R.string.manual_cat_4), body4);
    labsContainer.addView(header4);
    labsContainer.addView(body4);
    
    body4.addView(makeTestButtonRedGold(
       gr ? "14. Σουίτα Διάγνωσης Μπαταρίας"
          : "14. Battery Diagnostics Suite",
       this::showBatterySuiteMenu));

    body4.addView(makeTestButton(
            gr ? "15. Διαγνωστικός Έλεγχος Συστήματος Φόρτισης (Smart)"
               : "15. Charging System Diagnostic (Smart)",
            this::lab15ChargingSystemSmart));

    body4.addView(makeTestButton(
            gr ? "16. Στιγμιότυπο Θερμικών Αισθητήρων"
               : "16. Thermal Sensors Snapshot",
            this::lab16ThermalSnapshot));

    body4.addView(makeTestButtonGreenGold(
            gr ? "17. Ευφυής Ανάλυση Υγείας Συστήματος"
               : "17. Intelligent System Health Analysis",
            this::lab17RunAuto));

    // ============================================================
    // SECTION 5: STORAGE & PERFORMANCE — LABS 18-20
    // ============================================================
    LinearLayout body5 = makeSectionBody();
    Button header5 = makeSectionHeader(getString(R.string.manual_cat_5), body5);
    labsContainer.addView(header5);
    labsContainer.addView(body5);

    body5.addView(makeTestButton(
            gr ? "18. Έλεγχος Υγείας Αποθηκευτικού Χώρου"
               : "18. Storage Health Inspection",
            this::lab18StorageSnapshot));

    body5.addView(makeTestButton(
            gr ? "19. Ανάλυση Πίεσης Μνήμης & Σταθερότητας"
               : "19. Memory Pressure & Stability Analysis",
            this::lab19RamSnapshot));

    body5.addView(makeTestButton(
            gr ? "20. Ανάλυση Uptime & Προτύπων Επανεκκίνησης"
               : "20. Uptime & Reboot Pattern Analysis",
            this::lab20UptimeHints));

    // ============================================================
    // SECTION 6: SECURITY & SYSTEM HEALTH — LABS 21-24
    // ============================================================
    LinearLayout body6 = makeSectionBody();
    Button header6 = makeSectionHeader(getString(R.string.manual_cat_6), body6);
    labsContainer.addView(header6);
    labsContainer.addView(body6);

    body6.addView(makeTestButton(
            gr ? "21. Κλείδωμα Οθόνης / Βιομετρικά"
               : "21. Screen Lock / Biometrics",
            this::lab21ScreenLock));

    body6.addView(makeTestButton(
            gr ? "22. Έλεγχος Ενημέρωσης Ασφαλείας"
               : "22. Security Patch Check",
            this::lab22SecurityPatchAndPlayProtect));

    body6.addView(makeTestButton(
            gr ? "23. Κίνδυνος από Επιλογές Προγραμματιστή"
               : "23. Developer Options Risk",
            this::lab23DeveloperOptionsRisk));

    body6.addView(makeTestButton(
            gr ? "24. Ένδειξη Root / Ξεκλείδωτου Bootloader"
               : "24. Root / Bootloader Suspicion",
            this::lab24RootSuspicion));

    // ============================================================
    // SECTION 7: ADVANCED / LOGS — LABS 25-31
    // ============================================================
    LinearLayout body7 = makeSectionBody();
    Button header7 = makeSectionHeader(getString(R.string.manual_cat_7), body7);
    labsContainer.addView(header7);
    labsContainer.addView(body7);

    body7.addView(makeTestButton(
            gr ? "25. Ιστορικό Κρασαρισμάτων / Παγώματος"
               : "25. Crash / Freeze History",
            this::lab25CrashHistory));

    body7.addView(makeTestButton(
            gr ? "26. Ανάλυση Επιπτώσεων Εγκατεστημένων Εφαρμογών"
               : "26. Installed Applications Impact Analysis",
            this::lab26AppsFootprint));

    body7.addView(makeTestButton(
            gr ? "27. Δικαιώματα Εφαρμογών & Απόρρητο"
               : "27. App Permissions & Privacy",
            this::lab27PermissionsPrivacy));

    body7.addView(makeTestButton(
            gr
                    ? (isGelProActive() ? "" : "🔒 ")
                      + "PRO  28. Σταθερότητα Υλικού & Ακεραιότητα Διασυνδέσεων\nΥποψία Κόλλησης / (Βάσει Συμπτωμάτων)"
                    : (isGelProActive() ? "" : "🔒 ")
                      + "PRO  28. Hardware Stability & Interconnect Integrity\nSolder / (SYMPTOM-BASED)",
            this::lab28HardwareStability));

    body7.addView(makeTestButton(
            gr ? "29. Παρατηρήσεις Hardware\nΗ γνησιότητα απαιτεί εξωτερικό έλεγχο"
               : "29. Hardware Observations\nOEM origin requires external verification",
            this::lab29DeviceAuthenticity));

    body7.addView(makeTestButton(
            gr
                    ? (isGelProActive() ? "" : "🔒 ")
                      + "PRO  30. GEL Device Certification"
                    : (isGelProActive() ? "" : "🔒 ")
                      + "PRO  30. GEL Device Certification",
            this::lab30CombineFindings));

    body7.addView(makeTestButton(
            gr ? "31. Τελική Τεχνική Αναφορά"
               : "31. FINAL TECH SUMMARY",
            this::lab31FinalSummary));

    if (!serviceLogInit) {
        GELServiceLog.section(
                gr
                        ? "Χειροκίνητοι Έλεγχοι Android — Διαγνωστικά Υλικού"
                        : "Android Manual Tests — Hardware Diagnostics"
        );

        logLine();

        logInfo(
                gr
                        ? "Έναρξη χειροκίνητων διαγνωστικών ελέγχων συσκευής."
                        : getString(R.string.manual_log_desc)
        );

        serviceLogInit = true;
    }
}  // onCreate ENDS HERE

@Override
protected void onResume() {
    super.onResume();

    // Resume customer-side remote diagnostic transport.
    GELRemoteDiagnosticsSync.start(this);

    if (pendingRestartLab14Mode != 0) {

        // 🔴 ΜΗΝ ξεκινήσεις αν ήδη τρέχει test
        if (lab14Running) return;

        int mode = pendingRestartLab14Mode;
        pendingRestartLab14Mode = 0;

        boolean airplaneOn = false;

        try {
            airplaneOn =
                    Settings.Global.getInt(
                            getContentResolver(),
                            Settings.Global.AIRPLANE_MODE_ON,
                            0
                    ) == 1;
        } catch (Throwable ignore) {}

        if (!airplaneOn) return;

        // 🔴 μικρό delay για να "ηρεμήσει" το lifecycle
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (mode == 1) {
                lab14BBatteryDurationTest();
            } else if (mode == 2) {
                lab14GamingDurationTest();
            }

        }, 250);
    }
}

// ============================================================
// CPU STRESS HELPERS (REQUIRED BY OTHER LABS) NOT for 14B
// ============================================================

private final List<Thread> lab14CpuThreads = new ArrayList<>();

private void startCpuBurnLimitedThreads(int threads) {

    // 🔴 ALLOW LAB14 + LAB14B
    if (!lab14Running || lab14Cancelled) return;

    final int cores =
            Runtime.getRuntime().availableProcessors();

    if (threads <= 0) {
        threads = Math.max(2, cores - 1);
    }

    // 🔴 HARD CLAMP
    threads = Math.max(2, Math.min(threads, cores));

    // 🔴 SAFE RESET
    stopCpuBurn();
    lab14CpuThreads.clear();

    lab14CpuThreadsCurrent = threads;

    if (DEBUG_MODE) {
        appendLog("CPU",
                "START BURN threads=" + threads);
    }

    for (int i = 0; i < threads; i++) {

        Thread t = new Thread(() -> {

            try {

                // 🔴 THREAD-LOCAL BUFFER
                byte[] buffer =
                        new byte[1024 * 1024];

                long acc = 0;

                while (lab14Running &&
                       !lab14Cancelled &&
                       !Thread.currentThread().isInterrupted()) {

                    // =================================================
                    // 🔥 HEAVY MIXED LOAD
                    // CPU + MEMORY + CACHE + FP
                    // =================================================
                    for (int j = 0;
                         j < buffer.length;
                         j++) {

                        buffer[j] =
                                (byte) ((j * 31)
                                ^ (acc & 0xFF));

                        acc += buffer[j];

                        acc ^= (acc << 13);
                        acc ^= (acc >> 7);
                        acc ^= (acc << 17);

                        // 🔴 PERIODIC FP PRESSURE
                        if ((j & 1023) == 0) {

                            double z =
                                    Math.sqrt(
                                            Math.abs(acc)
                                    ) *
                                    Math.sin(acc) *
                                    Math.cos(acc);

                            if (Double.isNaN(z) ||
                                Double.isInfinite(z) ||
                                z > 1e9) {

                                acc = 0;
                            }
                        }
                    }

                    // 🔴 SMALL YIELD PREVENTS DEADLOCKS
                    Thread.yield();
                }

            } catch (Throwable ignore) {}

        }, "LAB14_CPU_" + i);

        t.setPriority(Thread.MAX_PRIORITY);

        lab14CpuThreads.add(t);
        t.start();
    }
}

private void stopCpuBurn() {

    for (Thread t : lab14CpuThreads) {
        try {
            t.interrupt();
        } catch (Throwable ignore) {}
    }

    lab14CpuThreads.clear();
}

private void stopGpuStress() {

    runOnUiThread(() -> {

        try {

            if (lab14GLView != null) {
                lab14GLView.onPause();

                ViewGroup p =
                    (ViewGroup) lab14GLView.getParent();

                if (p != null) {
                    p.removeView(lab14GLView);
                }

                lab14GLView = null;
            }

        } catch(Throwable ignore){}
    });
}

private void toggleLogPanel() {
    setLogPanelState(logPanelState == 0 ? 1 : 0);
}

private void setLogPanelState(int state) {
    logPanelState = state == 1 ? 1 : 0;

    if (logPanelState == 1) {
        showLogsFullScreen();
    } else {
        showLabsAndLogs();
    }

    if (btnLogToggle != null) {
        btnLogToggle.setText(logPanelState == 1 ? "LOGS ▼" : "LOGS ▲");
    }
}

private void showLogsFullScreen() {
    if (labsScroll != null) {
        labsScroll.setVisibility(View.GONE);
    }

    if (logScroll != null) {
        LinearLayout.LayoutParams lp =
                (LinearLayout.LayoutParams) logScroll.getLayoutParams();

        lp.height = 0;
        lp.weight = 1f;
        logScroll.setLayoutParams(lp);
    }
}

private void showLabsAndLogs() {
    if (labsScroll != null) {
        labsScroll.setVisibility(View.VISIBLE);
    }

    if (logScroll != null) {
        LinearLayout.LayoutParams lp =
                (LinearLayout.LayoutParams) logScroll.getLayoutParams();

        lp.height = dp(100);
        lp.weight = 0f;
        logScroll.setLayoutParams(lp);
    }
}

private void updateExportPosition(int logHeightDp) {
    if (btnExport == null) return;

    ViewGroup.MarginLayoutParams lp =
            (ViewGroup.MarginLayoutParams) btnExport.getLayoutParams();

    lp.bottomMargin = dp(logHeightDp + 12);
    btnExport.setLayoutParams(lp);
}

private void appendLog(String txt) {
    runOnUiThread(() -> {
        if (txtLog == null) return;

        txtLog.append(txt);
        txtLog.append("\n");

        // 🔥 GLOBAL LOG PIPELINE
        GELServiceLog.add(txt);

        if (logScroll != null) {
            logScroll.post(() ->
                    logScroll.fullScroll(View.FOCUS_DOWN));
        }
    });
}

private void appendLog(String tag, String msg) {
    appendLog("[" + tag + "] " + msg);
}

private void appendStyledLog(CharSequence text) {
    runOnUiThread(() -> {
        if (txtLog == null || text == null) return;

        txtLog.append(text);
        txtLog.append("\n");

        // 🔥 PDF SYNC
        GELServiceLog.add(text.toString());

        if (logScroll != null) {
            logScroll.post(() ->
                    logScroll.fullScroll(View.FOCUS_DOWN));
        }
    });
}

private String safe(String s) {
    return s == null ? "" : s.replace("<", "&lt;").replace(">", "&gt;");
}

private void expandLogPanel() {
    if (logScroll == null) return;

    runOnUiThread(() -> {
        try {
            FrameLayout.LayoutParams lp =
                    (FrameLayout.LayoutParams) logScroll.getLayoutParams();

            lp.height = dp(300); // μεγάλο log panel
            lp.gravity = Gravity.BOTTOM;

            logScroll.setLayoutParams(lp);

        } catch (Throwable ignore) {}
    });
}

private void collapseLogPanel() {
    if (logScroll == null) return;

    try {
        FrameLayout.LayoutParams lp =
                (FrameLayout.LayoutParams) logScroll.getLayoutParams();

        lp.height = dp(120); // μικρό panel
        lp.gravity = Gravity.BOTTOM;

        logScroll.setLayoutParams(lp);

    } catch (Throwable ignore) {}
}

private void scrollLogToBottom() {
    if (logScroll == null) return;

    logScroll.post(() -> {
        try {
            logScroll.fullScroll(View.FOCUS_DOWN);
        } catch (Throwable ignore) {}
    });
}

 // ============================================================
// BATTERY VOLTAGE HELPER (SAFE)
// ============================================================
private float getBatteryVoltageSafe() {

    try {

        iDoctorEngine engine =
                iDoctorEngine.get(ManualTestsActivity.this);

        float mv =
                engine.readBatteryVoltageMvStable(3, 10);

        if (!Float.isNaN(mv) && mv > 0f) {
            return mv / 1000f;
        }

    } catch (Throwable ignore) {}

    return Float.NaN;
}

// ------------------------------------------------------------
// BATTERY PERCENT (SAFE)
// ------------------------------------------------------------

// ⚠ NOT FOR LAB14 (uses ACTION_BATTERY_CHANGED)

private int getBatteryPercentSafe() {

    try {

        IntentFilter iFilter =
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        Intent batteryStatus =
                registerReceiver(null, iFilter);

        if (batteryStatus == null)
            return -1;

        int level =
                batteryStatus.getIntExtra(
                        BatteryManager.EXTRA_LEVEL,
                        -1
                );

        int scale =
                batteryStatus.getIntExtra(
                        BatteryManager.EXTRA_SCALE,
                        -1
                );

        if (level < 0 || scale <= 0)
            return -1;

        return (int) ((level / (float) scale) * 100f);

    } catch (Throwable ignore) {
        return -1;
    }
}

// ============================================================
// BATTERY VOLTAGE FILTER (3-SAMPLE AVERAGE)
// ============================================================
private float getBatteryVoltageFiltered() {

    // LAB14 FAST SAG MODE — raw sysfs first
    try {

        float v = getBatteryVoltageFast();

        if (!Float.isNaN(v) && v > 2.5f && v < 5.5f) {
            return v;
        }

    } catch (Throwable ignore) {}

    // fallback only
    try {

        iDoctorEngine engine =
                iDoctorEngine.get(ManualTestsActivity.this);

        float mv =
                engine.readBatteryVoltageMvStable(1, 1);

        if (!Float.isNaN(mv) && mv > 3000f && mv < 5000f) {
            return mv / 1000f;
        }

    } catch (Throwable ignore) {}

    return Float.NaN;
}

// ------------------------------------------------------------
// NORMALIZE mAh / Î¼Ah (shared)
// ------------------------------------------------------------
private long normalizeMah(long raw) {
if (raw <= 0) return -1;
if (raw > 200000) return raw / 1000;
return raw;                          // already mAh
}

// ------------------------------------------------------------
// Battery temperature via iDoctorEngine (SAFE)
// ------------------------------------------------------------
private float getBatteryTempEngineSafe() {

    try {

        iDoctorEngine eng = iDoctorEngine.get(this);

        Float t = eng.getBatteryTempUnified();

        if (t == null) return Float.NaN;

        if (Float.isNaN(t)) return Float.NaN;

        if (t <= 0f) return Float.NaN;

        if (t > 100f) return Float.NaN;

        return t;

    } catch (Throwable e) {

        return Float.NaN;
    }
}

// ------------------------------------------------------------
// Battery temperature — SAFE
// ------------------------------------------------------------

// ⚠ NOT FOR LAB14 (fallback source)

private float getBatteryTemperature() {

    try {

        iDoctorEngine eng = iDoctorEngine.get(this);

        Float t = eng.getBatteryTempUnified();

        if (t != null && !Float.isNaN(t))
            return t;

    } catch (Throwable ignore) {}

    // fallback μόνο αν engine δεν δώσει τιμή

    try {

        Intent i = registerReceiver(
                null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );

        if (i == null) return Float.NaN;

        int raw =
                i.getIntExtra(
                        BatteryManager.EXTRA_TEMPERATURE,
                        -1
                );

        if (raw <= 0) return Float.NaN;

        return raw / 10f;

    } catch (Throwable ignore) {

        return Float.NaN;
    }
}

// ------------------------------------------------------------
// Battery % — SAFE
// ------------------------------------------------------------

// ⚠ NOT FOR LAB14 CORE (use lab14BatteryPercent)

private float getCurrentBatteryPercent() {

    try {

        iDoctorEngine eng = iDoctorEngine.get(this);

        iDoctorEngine.BatterySnapshot b =
                eng.readBatterySnapshot();

        if (b != null && b.level >= 0 && b.scale > 0) {

            return (b.level * 100f) / (float) b.scale;
        }

    } catch (Throwable ignore) {}

    // fallback μόνο αν engine δεν δώσει τιμή

    try {

        Intent i = registerReceiver(
                null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );

        if (i == null) return Float.NaN;

        int level =
                i.getIntExtra(
                        BatteryManager.EXTRA_LEVEL,
                        -1
                );

        int scale =
                i.getIntExtra(
                        BatteryManager.EXTRA_SCALE,
                        -1
                );

        if (level < 0 || scale <= 0)
            return Float.NaN;

        return (level * 100f) / (float) scale;

    } catch (Throwable ignore) {

        return Float.NaN;
    }
}

// ------------------------------------------------------------
// BatteryInfo snapshot — SAFE (BatteryManager properties)
// ------------------------------------------------------------

// ⚠ NOT FOR LAB14 (BatteryManager fallback)

private BatteryInfo getBatteryInfo() {

    BatteryInfo bi = new BatteryInfo();

    try {

        iDoctorEngine engine =
                iDoctorEngine.get(ManualTestsActivity.this);

        bi.charging =
                engine.isChargingNowUnified();

        iDoctorEngine.BatterySnapshot snap =
                engine.readBatterySnapshotLab();

        float chargeMah = Float.NaN;
        float fullMah = Float.NaN;

        if (snap != null) {

            chargeMah = snap.chargeNowMah;
            fullMah   = snap.chargeFullMah;

        }

        if (!Float.isNaN(chargeMah) && chargeMah > 0) {
            bi.currentChargeMah = Math.round(chargeMah);
        } else {
            bi.currentChargeMah = -1;
        }

        if (!Float.isNaN(fullMah) && fullMah > 0) {
            bi.estimatedFullMah = Math.round(fullMah);
        } else {
            bi.estimatedFullMah = -1;
        }

        bi.source = "iDoctorEngineLocked";

        if (bi.currentChargeMah > 0)
            return bi;

    } catch (Throwable ignore) {}

    // ----------------------------------------
    // fallback → BatteryManager (LAST)
    // ----------------------------------------

    bi.charging = isDeviceCharging();
    bi.source = "BatteryManager";

    try {

        BatteryManager bm =
                (BatteryManager) getSystemService(BATTERY_SERVICE);

        if (bm == null) {

            bi.currentChargeMah = -1;
            bi.estimatedFullMah = -1;
            bi.source = "BatteryManager:N/A";
            return bi;
        }

        long cc_uAh =
                bm.getLongProperty(
                        BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER
                );

        bi.currentChargeMah = normalizeMah(cc_uAh);

        bi.estimatedFullMah = -1;

        if (bi.currentChargeMah <= 0)
            bi.currentChargeMah = -1;

    } catch (Throwable t) {

        bi.currentChargeMah = -1;
        bi.estimatedFullMah = -1;
        bi.source = "BatteryManager:ERROR";
    }

    return bi;
}

// ============================================================
// onPause
// ============================================================
@Override
protected void onPause() {

    super.onPause();

    // =========================
    // GUIDED AUDIO: stop on background, but never restore stale Lab 3
    // settings when the diagnostic was not running.
    // =========================
    lab3Cancelled = true;
    if (lab3GuidedPending.getAndSet(false)) {
        try {
            lab3WaitingUser = false;
            stopLab3Tone();
            restoreLab3Audio();
            enableSingleExportButton();
        } catch (RuntimeException ignored) {}
    }
    if (lab4GuidedPending.get()) {
        lab4Cancelled.set(true);
        try { AppTTS.stop(); } catch (RuntimeException ignored) {}
        finishLab4Guided();
    }
    // Orphaned prompts must not remain usable after the Activity pauses.
    try {
        AlertDialog dialog = lab3ActiveDialog;
        lab3ActiveDialog = null;
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    } catch (RuntimeException ignored) {}
    try {
        AlertDialog dialog = lab4ActiveDialog;
        lab4ActiveDialog = null;
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    } catch (RuntimeException ignored) {}

    // Camera2 guided preview cannot be certified by an answer on resume.
    try {
        AlertDialog cameraDialog = lab8ActiveDialog;
        lab8ActiveDialog = null;
        if (cameraDialog != null && cameraDialog.isShowing()) cameraDialog.dismiss();
    } catch (RuntimeException ignored) {}

    // Lab13 external monitoring is foreground-only; background/rotation
    // must not later report an old partial sample as a 60-second success.
    if (lab13Running || lab13WaitingForExternal) {
        abortLab13ByUser();
    }

    // =========================
    // LAB 14 / 14B
    // =========================
    try {

        if (lab14Running) {

            logWarn(AppLang.isGreek(this)
                    ? "Το τεστ διακόπηκε (background)"
                    : "Test aborted (background)");

            lab14Cancelled = true;
            lab14Running = false;
        }

        counterText = null;

        lab14StopAllStress();
        lab14CleanupUI();

    } catch (Throwable ignore) {}

    // =========================
    // TTS
    // =========================
    try {
        if (tts != null && tts[0] != null) {
            tts[0].stop();
        }
    } catch (Throwable ignore) {}
}

// ============================================================
// onDestroy
// ============================================================
@Override
protected void onDestroy() {

    try {
        if (ui != null) {
            ui.removeCallbacksAndMessages(null);
        }
    } catch (Throwable ignore) {}

    lab13Running = false;
    lab13WaitingForExternal = false;
    lab13MonitorGeneration++;
    lab13Handler.removeCallbacksAndMessages(null);
    lab21RunGeneration++;
    lab21Running = false;
    lab21ActiveEvidence = null;
    android.os.CancellationSignal oldBioSignal = lab21Cancellation;
    lab21Cancellation = null;
    if (oldBioSignal != null) {
        try { oldBioSignal.cancel(); } catch (RuntimeException ignored) {}
    }

    try {
        unregisterReceiver(lab13BtReceiver);
    } catch (Throwable ignore) {}

    try {
        if (lab14DotsView != null) {
            lab14DotsView.removeCallbacks(null);
        }
    } catch (Throwable ignore) {}

    // =========================
    // LAB 14 / 14B (CRITICAL)
    // =========================
    try {
        lab14Cancelled = true;
        lab14Running = false;
        lab14StopAllStress();
    } catch (Throwable ignore) {}

    try {
        counterText = null;
        lab14CleanupUI();
    } catch (Throwable ignore) {}

    // =========================
    // TTS
    // =========================
    try {
        if (tts != null && tts[0] != null) {
            tts[0].stop();
            tts[0].shutdown();
        }
    } catch (Throwable ignore) {}
    
    // Capture any final diagnostics before the local service log is cleared.
    GELRemoteDiagnosticsSync.flushNow(this);
    GELRemoteDiagnosticsSync.stop(this);

    GELServiceLog.clear();

    super.onDestroy();
}

// ============================================================
// BACK
// ============================================================
@Override
public void onBackPressed() {

    try {
        lab14Cancelled = true;
        lab14Running = false;
        counterText = null;

        lab14StopAllStress();
        restoreBrightnessAndKeepOn();
        lab14CleanupUI();

    } catch (Throwable ignore) {}

    super.onBackPressed();
}

@Override
protected void onStop() {

    // Capture final visible diagnostic lines before this Activity stops.
    // onResume() will restart the transport when the customer returns.
    GELRemoteDiagnosticsSync.flushNow(this);
    GELRemoteDiagnosticsSync.stop(this);

    super.onStop();

    if (lab14Running) {
        lab14Cancelled = true;
        lab14Running = false;

        lab14StopAllStress();

        try { lab14CleanupUI(); } catch (Throwable ignore) {}
    }

    // 🔥 RESET LOGS ΜΟΝΟ ΟΤΑΝ ΦΕΥΓΕΙΣ
    if (isFinishing()) {
        GELServiceLog.clear();
    }
}

// ============================================================
// GLOBAL TTS INIT — ONE TIME ONLY (SAFE)
// ============================================================
private void initTTS() {

    if (tts[0] != null) return;

    tts[0] = new TextToSpeech(this, status -> {

        if (status == TextToSpeech.SUCCESS && tts[0] != null) {

            Locale locale = AppLang.isGreek(this)
                    ? new Locale("el", "GR")
                    : Locale.US;

            int res = tts[0].setLanguage(locale);

            if (res == TextToSpeech.LANG_MISSING_DATA
                    || res == TextToSpeech.LANG_NOT_SUPPORTED) {

                tts[0].setLanguage(Locale.US);
            }

            ttsReady[0] = true;

            if (pendingTtsText != null) {
                tts[0].speak(
                        pendingTtsText,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "GEL_TTS_PENDING"
                );
                pendingTtsText = null;
            }
        }
    });
}

// ============================================================  
// GEL legacy aliases (LOCKED)  
// ============================================================  
private void logYellow(String msg) { logWarn(msg); }  
private void logGreen(String msg)  { logOk(msg); }  
private void logRed(String msg)    { logError(msg); }  

private void logSection(String msg) {  
logInfo(msg); 

}

// ============================================================  
// UI HELPERS (GEL LOCKED)  
// ============================================================  
private LinearLayout makeSectionBody() {  
    LinearLayout body = new LinearLayout(this);  
    body.setOrientation(LinearLayout.VERTICAL);  
    body.setVisibility(View.GONE);  
    body.setPadding(0, dp(4), 0, dp(4));  

    allSectionBodies.add(body);  
    return body;  
}  

private Button makeSectionHeader(String text, LinearLayout bodyToToggle) {
    Button b = new com.gel.cleaner.GelButton(this);
    allSectionHeaders.add(b);

    b.setText(text);
    b.setAllCaps(false);
    b.setTextSize(15f);
    b.setTextColor(0xFF39FF14);
    b.setBackgroundResource(R.drawable.gel_btn_outline_selector);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
    lp.setMargins(0, dp(6), 0, dp(4));
    b.setLayoutParams(lp);
    b.setGravity(Gravity.CENTER);

    b.setOnClickListener(v -> {

        if (bodyToToggle == null) return; // safety

        boolean willOpen = bodyToToggle.getVisibility() != View.VISIBLE;

        // close ALL
        for (LinearLayout body : allSectionBodies) {
            body.setVisibility(View.GONE);
        }

        if (willOpen) {
            bodyToToggle.setVisibility(View.VISIBLE);

            // ✅ SAFE SCROLL
if (labsScroll != null) {
    labsScroll.post(() -> {
        try {
            int y = b.getTop() - dp(12);
            if (y < 0) y = 0;
            labsScroll.smoothScrollTo(0, y);
        } catch (Throwable ignore) {}
    });
}
    }
});

return b;
}

private Button makeTestButton(String text, Runnable action) {

    Button b = new com.gel.cleaner.GelButton(this);

    b.setText(text);
    b.setAllCaps(false);
    b.setTextSize(14f);
    b.setTextColor(0xFFFFFFFF);
    b.setBackgroundResource(R.drawable.gel_btn_outline_selector);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    lp.setMargins(0, dp(4), 0, dp(4));
    b.setLayoutParams(lp);

    b.setMinHeight(dp(48));

    b.setSingleLine(false);
    b.setMaxLines(2);
    b.setEllipsize(null);

    b.setGravity(Gravity.CENTER);
    b.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

    b.setOnClickListener(v -> {
        if (action != null) action.run();   // 👈 safe
    });

    return b;
}

private Button makeTestButtonRedGold(String text, Runnable action) {  
Button b = new com.gel.cleaner.GelButton(this);
b.setText(text);  
b.setAllCaps(false);  
b.setTextSize(14f);  
b.setTextColor(0xFFFFFFFF);  
b.setTypeface(null, Typeface.BOLD);  

GradientDrawable bg = new GradientDrawable();  
bg.setColor(0xFF8B0000);  
bg.setCornerRadius(dp(10));  
bg.setStroke(dp(3), 0xFFFFD700);  
b.setBackground(bg);  

LinearLayout.LayoutParams lp =  
        new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.MATCH_PARENT,  
                dp(52)  
        );  
lp.setMargins(0, dp(6), 0, dp(6));  
b.setLayoutParams(lp);  
b.setGravity(Gravity.CENTER);  

b.setOnClickListener(v -> action.run());  
return b;

}

// ============================================================
// WIFI / NETWORK HELPERS — REQUIRED
// ============================================================

private String cleanSsid(String raw) {
if (raw == null) return "Unknown";

raw = raw.trim();  

if (raw.equalsIgnoreCase("<unknown ssid>") ||  
    raw.equalsIgnoreCase("unknown ssid"))  
    return "Unknown";  

if (raw.startsWith("\"") && raw.endsWith("\"") && raw.length() > 1)  
    return raw.substring(1, raw.length() - 1);  

return raw;

}

private String ipToStr(int ip) {
return (ip & 0xFF) + "." +
((ip >> 8) & 0xFF) + "." +
((ip >> 16) & 0xFF) + "." +
((ip >> 24) & 0xFF);
}

// ============================================================
// LAB 3 — User Confirmation Dialog (Earpiece)
// FINAL — GEL Dark/Gold + Neon Green + TTS + Mute
// ============================================================
private void askUserEarpieceConfirmation(boolean playbackRequested) {
    if (!playbackRequested) {
        logLabelWarnValue("Lab 3 audio playback",
                "Not verified: playback failed; user confirmation withheld");
        lab3GuidedPending.set(false);
        enableSingleExportButton();
        return;
    }

    runOnUiThread(() -> {
        if (lab3Cancelled || !lab3GuidedPending.get()) return;
        if (lab3WaitingUser) return;
        lab3WaitingUser = true;

        final boolean gr = AppLang.isGreek(this);

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        ManualTestsActivity.this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(false);

        // ==========================
        // ROOT
        // ==========================
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(28), dp(24), dp(28), dp(22));
        root.setMinimumWidth(dp(300));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(4), 0xFFFFD700);
        root.setBackground(bg);

        // ==========================
        // TITLE (WHITE)
        // ==========================
        TextView title = new TextView(this);
        title.setText(gr ? "LAB 3 — Επιβεβαίωση" : "LAB 3 — Confirmation");
        title.setTextColor(Color.WHITE);
        title.setTextSize(17f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(14));
        root.addView(title);

        // ==========================
        // MESSAGE (NEON GREEN)
        // ==========================
        TextView msg = new TextView(this);
        msg.setText(
                gr
                        ? "Άκουσες καθαρά τους ήχους\nαπό το ακουστικό;"
                        : "Did you hear the tones\nclearly from the earpiece?"
        );
        msg.setTextColor(0xFF39FF14); // GEL neon green
        msg.setTextSize(15f);
        msg.setGravity(Gravity.CENTER);
        msg.setLineSpacing(0f, 1.2f);
        msg.setPadding(0, 0, 0, dp(18));
        root.addView(msg);

// ==========================
// MUTE ROW (UNIFIED — AppTTS HELPER)
// ==========================
root.addView(buildMuteRow());

        // ---------- BUTTON ROW ----------
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams btnLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        btnLp.setMargins(dp(12), dp(8), dp(12), dp(8));

        // ---------- NO ----------
        Button noBtn = new com.gel.cleaner.GelButton(this);
        noBtn.setText(gr ? "ΟΧΙ" : "NO");
        noBtn.setAllCaps(false);
        noBtn.setTextColor(Color.WHITE);

        GradientDrawable noBg = new GradientDrawable();
        noBg.setColor(0xFF8B0000);
        noBg.setCornerRadius(dp(10));
        noBg.setStroke(dp(3), 0xFFFFD700);
        noBtn.setBackground(noBg);
        noBtn.setLayoutParams(btnLp);

        // ---------- YES ----------
        Button yesBtn = new com.gel.cleaner.GelButton(this);
        yesBtn.setText(gr ? "ΝΑΙ" : "YES");
        yesBtn.setAllCaps(false);
        yesBtn.setTextColor(Color.WHITE);

        GradientDrawable yesBg = new GradientDrawable();
        yesBg.setColor(0xFF0B5F3B);
        yesBg.setCornerRadius(dp(10));
        yesBg.setStroke(dp(3), 0xFFFFD700);
        yesBtn.setBackground(yesBg);
        yesBtn.setLayoutParams(btnLp);

// ---------- ADD ----------
btnRow.addView(noBtn);
btnRow.addView(yesBtn);
root.addView(btnRow);

b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();
lab3ActiveDialog = d;

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// ------------------------------------------------------------
// STOP TTS ON ANY DISMISS
// ------------------------------------------------------------
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// ------------------------------------------------------------
// BACK KEY — STOP TTS + RESTORE AUDIO
// ------------------------------------------------------------
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK
            && event.getAction() == KeyEvent.ACTION_UP) {

        try { AppTTS.stop(); } catch (Throwable ignore) {}

        lab3WaitingUser = false;
        restoreLab3Audio();
        lab3GuidedPending.set(false);
        enableSingleExportButton();
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(d);

    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        if (d.isShowing()
                && !isFinishing()
                && !isDestroyed()
                && !AppTTS.isMuted(this)) {

            AppTTS.ensureSpeak(
                    this,
                    gr
                            ? "Άκουσες καθαρά τους ήχους από το ακουστικό;"
                            : "Did you hear the tones clearly from the earpiece?"
            );
        }
    }, 400);
} else {
    lab3WaitingUser = false;
    lab3GuidedPending.set(false);
    enableSingleExportButton();
    return;
}

// ------------------------------------------------------------
// YES ACTION (PASS)
// ------------------------------------------------------------
yesBtn.setOnClickListener(v -> {
    if (lab3Cancelled || !lab3GuidedPending.get()) {
        d.dismiss();
        return;
    }
    try { AppTTS.stop(); } catch (Throwable ignore) {}

    lab3WaitingUser = false;

    logLabelOkValue(
            gr ? "Αποτέλεσμα" : "Result",
            gr
                    ? "Ο χρήστης δήλωσε ότι άκουσε καθαρά τον ήχο· φυσικό ακουστικό και έξοδος μη πιστοποιημένα"
                    : "USER_REPORTED: heard tone; physical earpiece/output not independently verified"
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 3 ολοκληρώθηκε." : "Lab 3 finished.");
    logLine();

    restoreLab3Audio();
    lab3GuidedPending.set(false);
    enableSingleExportButton();
    d.dismiss();
});

// ------------------------------------------------------------
// NO ACTION (FAIL)
// ------------------------------------------------------------
noBtn.setOnClickListener(v -> {
    if (lab3Cancelled || !lab3GuidedPending.get()) {
        d.dismiss();
        return;
    }
    try { AppTTS.stop(); } catch (Throwable ignore) {}

    lab3WaitingUser = false;

    logLabelWarnValue(
            gr ? "LAB 3 — Ακουστικό" : "LAB 3 — Earpiece",
            gr
                    ? "USER_REPORTED: ο χρήστης δεν άκουσε τους τόνους· αιτία άγνωστη"
                    : "USER_REPORTED: tones were not heard; cause unverified"
    );

    logLabelWarnValue(
            gr ? "Πιθανό πρόβλημα" : "Possible issue",
            gr
                    ? "Πιθανή βλάβη ακουστικού ή πρόβλημα δρομολόγησης ήχου"
                    : "Earpiece failure or audio routing problem"
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 3 ολοκληρώθηκε." : "Lab 3 finished.");
    logLine();

    restoreLab3Audio();
    lab3GuidedPending.set(false);
    enableSingleExportButton();
    d.dismiss();
});

    });
} 

// ============================================================
// LAB 3 — STATE / HELPERS (LOCKED)
// ============================================================

private void routeToCallEarpiece() {
    try {
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am == null) return;

        am.stopBluetoothSco();
        am.setBluetoothScoOn(false);
        am.setSpeakerphoneOn(false);
        am.setMicrophoneMute(false);
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
    } catch (Throwable ignore) {}
}

private void routeToEarpiecePlayback() {
    try {
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am == null) return;

        am.stopBluetoothSco();
        am.setBluetoothScoOn(false);
        am.setSpeakerphoneOn(false);
        am.setMode(AudioManager.MODE_NORMAL);
    } catch (Throwable ignore) {}
}

private ToneGenerator lab3Tone;

/**
 * HARD restore for LAB 3
 * One single source of truth.
 * Used on success / cancel / exception.
 */
 
private void restoreLab3Audio() {
    try {
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (am == null) return;   // 🔒 safety

        resetAudioAfterLab3(
                am,
                lab3OldMode,
                lab3OldSpeaker,
                lab3OldMicMute
        );

    } catch (Throwable ignore) {}
}

/**
 * Plays a short earpiece beep using VOICE_CALL stream.
 * Earpiece-only, OEM safe.
 */
private boolean playEarpieceBeep() {

    int sampleRate = 8000;
    int durationMs = 400;
    int samples = sampleRate * durationMs / 1000;

    short[] buffer = new short[samples];
    double freq = 1000.0;

    for (int i = 0; i < samples; i++) {
        buffer[i] = (short)
                (Math.sin(2 * Math.PI * i * freq / sampleRate) * 32767);
    }

    AudioTrack track = null;
    try {
        track = new AudioTrack(
                AudioManager.STREAM_VOICE_CALL,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffer.length * 2,
                AudioTrack.MODE_STATIC
        );
        if (track.getState() != AudioTrack.STATE_INITIALIZED) return false;
        int written = track.write(buffer, 0, buffer.length);
        if (written != buffer.length) return false;
        track.play();
        if (track.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) return false;
        AudioDeviceInfo routed = track.getRoutedDevice();
        if (routed != null) lab3RoutedOutputType = routed.getType();
        SystemClock.sleep(durationMs + 100);
        // Playback API accepted samples; does NOT confirm audible hardware route.
        return true;
    } catch (RuntimeException e) {
        return false;
    } finally {
        if (track != null) {
            try { track.stop(); } catch (Throwable ignore) {}
            try { track.release(); } catch (Throwable ignore) {}
        }
    }
}

/**
 * Optional tone stop helper (defensive).
 */
private void stopLab3Tone() {
    try {
        if (lab3Tone != null) {
            lab3Tone.stopTone();
            lab3Tone.release();
        }
    } catch (Throwable ignore) {}
    lab3Tone = null;
}

// ============================================================
// LAB 3 — HARD AUDIO RESET (SINGLE SOURCE OF TRUTH)
// ============================================================
private void resetAudioAfterLab3(
        AudioManager am,
        int oldMode,
        boolean oldSpeaker,
        boolean oldMicMute
) {
    if (am == null) return;

    try {
        try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
        try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}

        // Force clean baseline
        try { am.setMode(oldMode); } catch (Throwable ignore) {}
        try { am.setSpeakerphoneOn(oldSpeaker); } catch (Throwable ignore) {}
        try { am.setMicrophoneMute(oldMicMute); } catch (Throwable ignore) {}

        SystemClock.sleep(120);

    } catch (Throwable ignore) {}
}

// ============================================================
// HARD AUDIO NORMALIZE — BEFORE MIC CAPTURE (MANDATORY)
// ============================================================
private void hardNormalizeAudioForMic() {

    try {
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (am == null) return;

        try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
        try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}

        try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
        try { am.setSpeakerphoneOn(false); } catch (Throwable ignore) {}

        // ?? ΤΟ ΣΗΜΑΝΤΙΚΟ
        try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}

        SystemClock.sleep(300);

    } catch (Throwable ignore) {}
}

// ============================================================
// HELPERS REQUIRED BY LAB 4 PRO (STRICT – DO NOT TOUCH)
// ============================================================

private AlertDialog buildInfoDialog(
        String titleText,
        String messageText,
        AtomicBoolean cancelled,
        AtomicReference<AlertDialog> dialogRef
) {
    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(26), dp(24), dp(26), dp(22));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    TextView title = new TextView(this);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(17f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(14));
    root.addView(title);

    TextView msg = new TextView(this);
    msg.setText(messageText);
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(14.5f);
    msg.setGravity(Gravity.CENTER);
    msg.setPadding(0, 0, 0, dp(16));
    root.addView(msg);

    Button exit = new com.gel.cleaner.GelButton(this);
    exit.setAllCaps(false);
    exit.setText(AppLang.isGreek(this) ? "ΕΞΟΔΟΣ ΤΕΣΤ" : "EXIT TEST");
    exit.setTextColor(Color.WHITE);

    GradientDrawable exitBg = new GradientDrawable();
    exitBg.setColor(0xFF8B0000);
    exitBg.setCornerRadius(dp(10));
    exitBg.setStroke(dp(3), 0xFFFFD700);
    exit.setBackground(exitBg);

    exit.setOnClickListener(v -> {
        cancelled.set(true);
        try { AppTTS.stop(); } catch (Throwable ignore) {}
        try {
            AlertDialog d = dialogRef.get();
            if (d != null) d.dismiss();
        } catch (Throwable ignore) {}
    });

    root.addView(exit);

b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// 🔴 ΣΗΜΑΝΤΙΚΟ — ΔΗΛΩΝΟΥΜΕ ΤΟ DIALOG ΣΤΟ REF
dialogRef.set(d);

// Stop TTS on ANY dismiss
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// Back key handling
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        cancelled.set(true);
        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(d);
}

return d;
}

private void forceSpeaker(AudioManager am) {
    if (am == null) return;
    try {
        am.stopBluetoothSco();
        am.setBluetoothScoOn(false);
        am.setMicrophoneMute(false);
        am.setMode(AudioManager.MODE_NORMAL);
        am.setSpeakerphoneOn(true);
        SystemClock.sleep(120);
    } catch (Throwable ignore) {}
}

private void dismiss(AtomicReference<AlertDialog> ref) {
    try {
        AlertDialog d = ref.get();
        if (d != null) d.dismiss();
    } catch (Throwable ignore) {}
}

private int getWorkingMicSource() {
    SharedPreferences sp = getSharedPreferences("gel_audio_profile", MODE_PRIVATE);
    return sp.getInt("mic_source", MediaRecorder.AudioSource.VOICE_COMMUNICATION);
}

// ============================================================
// HUMAN VOICE DETECTION — FULLY SELF-CONTAINED (NO ENGINE)
// Adaptive AudioSource scan — WORKS ON REAL DEVICES
// ============================================================

// ====================================================
// AUDIO SOURCES — CLASS LEVEL (LOCKED)
// ====================================================
private static final int[] AUDIO_SOURCES = new int[] {
        MediaRecorder.AudioSource.VOICE_COMMUNICATION,
        MediaRecorder.AudioSource.VOICE_RECOGNITION,
        MediaRecorder.AudioSource.MIC,
        MediaRecorder.AudioSource.DEFAULT,
        MediaRecorder.AudioSource.CAMCORDER,
        MediaRecorder.AudioSource.UNPROCESSED
};

private boolean detectHumanVoiceAdaptive(boolean gr) {

    final int SR = 44100;
    final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    final int STEP_MS = 100;
    final long WINDOW_MS = 5000;

    for (int source : AUDIO_SOURCES) {

        AudioRecord ar = null;

        try {
            int minBuf = AudioRecord.getMinBufferSize(SR, CHANNEL, FORMAT);
            if (minBuf <= 0) continue;

            ar = new AudioRecord(
                    source,
                    SR,
                    CHANNEL,
                    FORMAT,
                    minBuf * 2
            );

            if (ar.getState() != AudioRecord.STATE_INITIALIZED) continue;

            ar.startRecording();
SystemClock.sleep(250);

// 👇 ΠΡΩΤΑ buffer
short[] buf = new short[1024];
            
// =============================
// BASELINE — SILENCE
// =============================
long noiseSum = 0;
int noiseFrames = 0;

for (int i = 0; i < 5; i++) {
    int n = ar.read(buf, 0, buf.length);
    if (n <= 0) continue;

    long sumSq = 0;
    for (int j = 0; j < n; j++) {
        int v = Math.abs(buf[j]);
        sumSq += (long) v * v;
    }

    double rms = Math.sqrt((double) sumSq / n);
    noiseSum += rms;
    noiseFrames++;

    SystemClock.sleep(100);
}

double noiseFloor = noiseFrames > 0
        ? noiseSum / noiseFrames
        : 0;

            long until = SystemClock.uptimeMillis() + WINDOW_MS;
            long voicedMs = 0;

            while (SystemClock.uptimeMillis() < until) {

                int n = ar.read(buf, 0, buf.length);
                if (n <= 0) {
                    SystemClock.sleep(STEP_MS);
                    continue;
                }

                long sumSq = 0;
                int peak = 0;

                for (int i = 0; i < n; i++) {
                    int v = Math.abs(buf[i]);
                    peak = Math.max(peak, v);
                    sumSq += (long) v * v;
                }

                double rms = Math.sqrt((double) sumSq / n);

                boolean rmsOk  = rms > noiseFloor * 2.2;
boolean peakOk = peak > 2500;

                if (rmsOk && peakOk) {
                    voicedMs += STEP_MS;
                } else {
                    voicedMs = Math.max(0, voicedMs - STEP_MS); // decay
                }

                if (voicedMs >= 800) {

                    saveWorkingMicSource(source);

                    logOk(gr
                            ? "Φωνή ανιχνεύθηκε."
                            : "Voice detected.");

                    logInfo(gr
                            ? "Πηγή ήχου: " + source
                            : "Audio source: " + source);

                    return true;
                }

                SystemClock.sleep(STEP_MS);
            }

        } catch (Throwable ignore) {

        } finally {
            try {
                if (ar != null) {
                    ar.stop();
                    ar.release();
                }
            } catch (Throwable ignore) {}
        }
    }

    logLabelErrorValue(
            gr ? "Κατάσταση" : "Status",
            gr
                    ? "Δεν ανιχνεύθηκε ανθρώπινη φωνή με καμία πηγή."
                    : "Human voice not detected with any audio source."
    );

    return false;
}

// ============================================================
// PERSISTENCE
// ============================================================
private void saveWorkingMicSource(int source) {
    getSharedPreferences("gel_audio_profile", MODE_PRIVATE)
            .edit()
            .putInt("mic_source", source)
            .apply();
}

// ============================================================
// LAB 8.1 — HUMAN SUMMARY HELPERS
// ============================================================

private static class CameraHumanSummary {
    String photoQuality;          // "9 MP photos (very good)"
    String professionalPhotos;    // "RAW uncompressed photos supported"
    String videoQuality;          // "4K (very high)" / "Full HD (high)"
    String videoSmoothness;
    String slowMotion;
    String stabilization;
    String manualMode;
    String flash;
    String realLifeUse;
    String verdict;
}

private CameraHumanSummary buildHumanSummary(CameraCharacteristics cc) {

    CameraHumanSummary h = new CameraHumanSummary();

    // ------------------------------------------------------------
    // CAPS
    // ------------------------------------------------------------
    boolean hasRaw = false;
    boolean manual = false;

    int[] caps = cc.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
    if (caps != null) {
        for (int c : caps) {
            if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW)
                hasRaw = true;
            if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR)
                manual = true;
        }
    }

    h.manualMode = manual ? "Supported" : "Not supported";

    // ------------------------------------------------------------
    // PHOTO QUALITY (MP)
    // ------------------------------------------------------------
    StreamConfigurationMap map =
            cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

    Size maxPhoto = null;
    if (map != null) {
        Size[] photos = map.getOutputSizes(ImageFormat.JPEG);
        if (photos != null && photos.length > 0) {
            maxPhoto = photos[0];
            for (Size s : photos) {
                long a = (long) s.getWidth() * s.getHeight();
                long b = (long) maxPhoto.getWidth() * maxPhoto.getHeight();
                if (a > b) maxPhoto = s;
            }
        }
    }

    if (maxPhoto != null) {
        int mp = (maxPhoto.getWidth() * maxPhoto.getHeight()) / 1_000_000;
        h.photoQuality = mp + " MP photos (very good)";
    } else {
        h.photoQuality = "Standard photos";
    }

// ------------------------------------------------------------
// PROFESSIONAL PHOTOS (RAW)
// ------------------------------------------------------------
final boolean gr = AppLang.isGreek(this);

h.professionalPhotos = hasRaw
        ? (gr
            ? "Υποστηρίζεται λήψη RAW (ασυμπίεστων) φωτογραφιών"
            : "RAW (uncompressed) photo capture supported")
        : (gr
            ? "Δεν υποστηρίζεται RAW (μόνο JPEG)"
            : "RAW not supported (JPEG only)");

    // ------------------------------------------------------------
    // VIDEO QUALITY
    // ------------------------------------------------------------
    int maxWidth = 0;
    if (map != null) {
        Size[] vids = map.getOutputSizes(MediaRecorder.class);
        if (vids != null) {
            for (Size s : vids)
                maxWidth = Math.max(maxWidth, s.getWidth());
        }
    }

    if (maxWidth >= 3840)
        h.videoQuality = "4K (very high)";
    else if (maxWidth >= 1920)
        h.videoQuality = "Full HD (high)";
    else
        h.videoQuality = "HD (basic)";

// ------------------------------------------------------------
// FPS / SMOOTHNESS / SLOW MOTION
// ------------------------------------------------------------
int maxFps = 0;
Range<Integer>[] fpsRanges =
        cc.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);

if (fpsRanges != null) {
    for (Range<Integer> r : fpsRanges) {
        if (r != null && r.getUpper() != null)
            maxFps = Math.max(maxFps, r.getUpper());
    }
}

if (maxFps >= 120) {

    h.videoSmoothness = gr
            ? "Πολύ ομαλή κίνηση (έως " + maxFps + " FPS)"
            : "Very smooth motion (up to " + maxFps + " FPS)";

    h.slowMotion = gr
            ? "Υποστηρίζεται αργή κίνηση (Slow Motion)"
            : "Slow motion supported";

} else if (maxFps >= 60) {

    h.videoSmoothness = gr
            ? "Ομαλή κίνηση (έως " + maxFps + " FPS)"
            : "Smooth motion (up to " + maxFps + " FPS)";

    h.slowMotion = gr
            ? "Περιορισμένη υποστήριξη slow motion"
            : "Limited slow motion support";

} else if (maxFps >= 30) {

    h.videoSmoothness = gr
            ? "Κανονική ομαλότητα βίντεο (30 FPS)"
            : "Standard smoothness (30 FPS)";

    h.slowMotion = gr
            ? "Δεν υποστηρίζεται slow motion"
            : "Slow motion not supported";

} else {

    h.videoSmoothness = gr
            ? "Βασική απόδοση βίντεο"
            : "Basic video performance";

    h.slowMotion = gr
            ? "Δεν υποστηρίζεται slow motion"
            : "Slow motion not supported";
}

// ------------------------------------------------------------
// STABILIZATION
// ------------------------------------------------------------
boolean stab = false;
int[] stabModes =
        cc.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);

if (stabModes != null) {
    for (int m : stabModes) {
        if (m == CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON) {
            stab = true;
            break;
        }
    }
}

h.stabilization = stab
        ? (gr ? "Υποστηρίζεται ηλεκτρονική σταθεροποίηση (EIS)"
              : "Electronic stabilization (EIS) supported")
        : (gr ? "Δεν υποστηρίζεται σταθεροποίηση βίντεο"
              : "Video stabilization not supported");

// ------------------------------------------------------------
// FLASH
// ------------------------------------------------------------
Boolean flashAvail = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);

h.flash = Boolean.TRUE.equals(flashAvail)
        ? (gr ? "Διαθέσιμο φλας"
              : "Flash available")
        : (gr ? "Δεν υπάρχει φλας"
              : "Flash not available");

// ------------------------------------------------------------
// REAL LIFE USE
// ------------------------------------------------------------
if (maxFps >= 60 && stab) {

    h.realLifeUse = gr
            ? "Κατάλληλη για καθημερινή χρήση και σκηνές με κίνηση."
            : "Suitable for everyday use and moving scenes.";

} else if (maxFps >= 30) {

    h.realLifeUse = gr
            ? "Κατάλληλη για καθημερινή χρήση και κοινωνικά δίκτυα."
            : "Suitable for daily use and social media.";

} else {

    h.realLifeUse = gr
            ? "Βασική χρήση χωρίς απαιτήσεις."
            : "Basic usage only.";
}

// ------------------------------------------------------------
// FINAL VERDICT
// ------------------------------------------------------------
if (hasRaw && maxFps >= 60) {

    h.verdict = gr
            ? "Καλή κάμερα για καθημερινή χρήση και λήψεις RAW. "
              + "Δεν προορίζεται για επαγγελματική παραγωγή βίντεο."
            : "Good camera for daily use and RAW photography. "
              + "Not intended for professional video production.";

} else {

    h.verdict = gr
            ? "Επαρκής κάμερα για βασική καθημερινή χρήση."
            : "Decent camera for basic daily use.";
}

return h;
}

// ============================================================
// TELEPHONY SNAPSHOT (SAFE / INFO ONLY)
// ============================================================
private TelephonySnapshot getTelephonySnapshot() {

TelephonySnapshot s = new TelephonySnapshot();  

try {  
    s.airplaneOn = Settings.Global.getInt(  
            getContentResolver(),  
            Settings.Global.AIRPLANE_MODE_ON, 0  
    ) == 1;  
} catch (Throwable ignored) {}  

TelephonyManager tm =  
        (TelephonyManager) getSystemService(TELEPHONY_SERVICE);  

if (tm != null) {  
    try {  
        s.simState = tm.getSimState();
        s.simStateKnown = (s.simState != TelephonyManager.SIM_STATE_UNKNOWN);
        s.simReady = s.simStateKnown
                && s.simState == TelephonyManager.SIM_STATE_READY;  
    } catch (Throwable ignored) {}  

    try {  
        ServiceState ss = tm.getServiceState();  
        if (ss != null) {  
            s.serviceState = ss.getState();
            s.serviceKnown = true;
            s.inService =  
                    (s.serviceState == ServiceState.STATE_IN_SERVICE);  
        }  
    } catch (Throwable ignored) {}  

    try {  
        s.dataState = tm.getDataState();
        s.dataStateKnown = s.dataState != TelephonyManager.DATA_UNKNOWN;  
    } catch (Throwable ignored) {}  
    // Cached/OS-reported signal level on API28+. Absence is not zero signal.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        try {
            android.telephony.SignalStrength strength = tm.getSignalStrength();
            if (strength != null) {
                int level = strength.getLevel();
                if (level >= 0 && level <= 4) {
                    s.signalLevel = level;
                    s.signalLevelKnown = true;
                }
            }
        } catch (RuntimeException ignored) {}
    }

}  

ConnectivityManager cm =  
        (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);  

if (cm != null) {  
    try {  
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  
            Network n = cm.getActiveNetwork();
            s.defaultNetworkPresent = n != null;
            NetworkCapabilities caps = n == null ? null
                    : cm.getNetworkCapabilities(n);
            s.defaultCapsKnown = caps != null;
            if (caps != null) {
                s.defaultInternetAdvertised = caps.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET);
                s.defaultValidated = caps.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                s.defaultCellular = caps.hasTransport(
                        NetworkCapabilities.TRANSPORT_CELLULAR);
                s.defaultWifi = caps.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI);
                s.defaultVpn = caps.hasTransport(
                        NetworkCapabilities.TRANSPORT_VPN);
                s.defaultCaptivePortal = caps.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL);
                // Keep legacy meaning for Lab 12. Its own scope will be
                // addressed separately; do not silently reinterpret it here.
                s.hasInternet = s.defaultInternetAdvertised;
            }  
        }  
    } catch (Throwable ignored) {}  
}  

return s;

}

// ============================================================
// LOGGING — GEL CANONICAL (UI + SERVICE REPORT)
// ============================================================
private void appendHtml(String html) {

    final String safeHtml = (html == null) ? "" : html;

    ui.post(() -> {

        if (txtLog == null) return;

        CharSequence cur = txtLog.getText();

        String clean = safeHtml.trim();

        // αποφυγή διπλών <br>
        if (clean.equals("<br>")) {
            clean = "<br>";
        } else if (!clean.endsWith("<br>")) {
            clean = clean + "<br>";
        }

        CharSequence add = Html.fromHtml(clean);

        txtLog.setText(TextUtils.concat(cur, add));

        if (logScroll != null) {
            logScroll.post(() -> {
                try {
                    logScroll.fullScroll(View.FOCUS_DOWN);
                } catch (Throwable ignore) {}
            });
        }
    });
}

// =====================================================
// LOG METHODS — FINAL (UI + PDF SYNC)
// =====================================================

// ---------------- CORE ----------------
private void logLabel(String label, String value, String color) {

    appendHtml(
            escape(label) + ": " +
            "<font color='" + color + "'>" + escape(value) + "</font>"
    );

    // 🔥 PDF
    GELServiceLog.add(label + ": " + value);
}

// ---------------- SIMPLE ----------------
private void logInfo(String msg) {
    if (msg == null) return;

    appendHtml("• " + escape(msg));
    GELServiceLog.add("ℹ " + msg);

    expandLogPanel();
    scrollLogToBottom();
}

private void logOk(String msg) {
    if (msg == null) return;

    appendHtml("<font color='#39FF14'>✔ " + escape(msg) + "</font>");
    GELServiceLog.add("✔ " + msg);

    expandLogPanel();
    scrollLogToBottom();
}

private void logWarn(String msg) {
    if (msg == null) return;

    appendHtml("<font color='#FFD966'>⚠ " + escape(msg) + "</font>");
    GELServiceLog.add("⚠ " + msg);

    expandLogPanel();
    scrollLogToBottom();
}

private void logError(String msg) {
    if (msg == null) return;

    appendHtml("<font color='#FF5555'>✖ " + escape(msg) + "</font>");
    GELServiceLog.add("✖ " + msg);

    expandLogPanel();
    scrollLogToBottom();
}

private void logLine() {
    String line = "--------------------------------------------------";

    appendHtml(line);
    GELServiceLog.add(line);

    scrollLogToBottom();
}

// ---------------- LABEL VALUE ----------------
private void logLabelOkValue(String label, String value) {
    logLabel(label, value, "#39FF14");
    expandLogPanel();
    scrollLogToBottom();
}

private void logLabelWarnValue(String label, String value) {
    logLabel(label, value, "#FFD700");
    expandLogPanel();
    scrollLogToBottom();
}

private void logLabelErrorValue(String label, String value) {
    logLabel(label, value, "#FF5555");
    expandLogPanel();
    scrollLogToBottom();
}

// ---------------- COMPATIBILITY ----------------
private void logLabelValue(String label, String value) {
    logLabelOkValue(label, value);
}

// ---------------- OVERLOADS ----------------
private void logOk(String label, String value) {
    logLabelOkValue(label, value);
}

private void logWarn(String label, String value) {
    logLabelWarnValue(label, value);
}

private void logError(String label, String value) {
    logLabelErrorValue(label, value);
}

// ------------------------------------------------------------
// SAFE ESCAPE FOR UI ONLY (SERVICE LOG STORES RAW TEXT)
// ------------------------------------------------------------

public int dp(int v) {
float d = getResources().getDisplayMetrics().density;
return (int) (v * d + 0.5f);
}

private View space(int w) {
    View v = new View(this);
    v.setLayoutParams(new LinearLayout.LayoutParams(w, 1));
    return v;
}

// ============================================================
// FORMAT HELPERS
// ============================================================
private String humanBytes(long bytes) {
if (bytes <= 0) return "0 B";
float kb = bytes / 1024f;
if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb);
float mb = kb / 1024f;
if (mb < 1024) return String.format(Locale.US, "%.1f MB", mb);
float gb = mb / 1024f;
return String.format(Locale.US, "%.2f GB", gb);
}

private String formatUptime(long ms) {
long s = ms / 1000;
long d = s / (24 * 3600);
s %= (24 * 3600);
long h = s / 3600;
s %= 3600;
long m = s / 60;
return String.format(Locale.US, "%dd %dh %dm", d, h, m);
}

// ============================================================
// NETWORK HELPERS — USED BY LAB 10
// ============================================================

private float tcpLatencyMs(String host, int port, int timeoutMs) {
long t0 = SystemClock.elapsedRealtime();
Socket s = new Socket();
try {
s.connect(new InetSocketAddress(host, port), timeoutMs);
long t1 = SystemClock.elapsedRealtime();
return (t1 - t0);
} catch (Exception e) {
return -1f;
} finally {
try { s.close(); } catch (Exception ignored) {}
}
}

private float dnsResolveMs(String host) {
long t0 = SystemClock.elapsedRealtime();
try {
InetAddress.getByName(host);
long t1 = SystemClock.elapsedRealtime();
return (t1 - t0);
} catch (Exception e) {
return -1f;
}
}

// ------------------------------------------------------------
// Charging detection — SAFE (plugged based)
// ------------------------------------------------------------
private boolean isDeviceCharging() {

    try {

        iDoctorEngine eng = iDoctorEngine.get(this);

        return eng.isChargingNowUnified();

    } catch (Throwable ignore) {}

    // fallback μόνο αν engine δεν δώσει τιμή

    try {

        Intent i = registerReceiver(
                null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );

        if (i == null) return false;

        int plugged =
                i.getIntExtra(
                        BatteryManager.EXTRA_PLUGGED,
                        0
                );

        return plugged == BatteryManager.BATTERY_PLUGGED_AC
                || plugged == BatteryManager.BATTERY_PLUGGED_USB
                || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;

    } catch (Throwable ignore) {

        return false;
    }
}

// ============================================================
// THERMAL HELPERS — System thermal zones (no libs, best-effort)
// Used by CPU/GPU/Skin/PMIC temp readers
// ============================================================
private Map<String, Float> readThermalZones() {

Map<String, Float> out = new HashMap<>();  

try {  
    File base = new File("/sys/class/thermal");  
    File[] zones = base.listFiles(new FileFilter() {  
        @Override public boolean accept(File f) {  
            return f != null && f.isDirectory() && f.getName().startsWith("thermal_zone");  
        }  
    });  

    if (zones == null) return out;  

    for (File z : zones) {  
        try {  
            String type = safeReadOneLine(new File(z, "type"));  
            String temp = safeReadOneLine(new File(z, "temp"));  

            if (type == null || temp == null) continue;  

            type = type.trim().toLowerCase(Locale.US);  
            temp = temp.trim();  

            // temp is usually in millidegrees (e.g. 42000), sometimes in degrees (42)  
            float t;  
            try {  
                long v = Long.parseLong(temp.replaceAll("[^0-9\\-]", ""));  
                t = (Math.abs(v) >= 1000) ? (v / 1000f) : (float) v;  
            } catch (Throwable ignore) {  
                continue;  
            }  

            // keep best (highest) reading if duplicate type keys appear  
            if (!out.containsKey(type) || out.get(type) < t) out.put(type, t);  

        } catch (Throwable ignore) {}  
    }  

} catch (Throwable ignore) {}  

return out;

}

private Float pickZone(Map<String, Float> zones, String... keys) {
if (zones == null || zones.isEmpty() || keys == null || keys.length == 0) return null;

// normalize search keys  
List<String> k = new ArrayList<>();  
for (String s : keys) {  
    if (s != null && !s.trim().isEmpty()) k.add(s.trim().toLowerCase(Locale.US));  
}  
if (k.isEmpty()) return null;  

// best match strategy: first key hit in type string  
Float best = null;  

for (Map.Entry<String, Float> e : zones.entrySet()) {  
    String type = e.getKey();  
    Float val = e.getValue();  
    if (type == null || val == null) continue;  

    for (String kk : k) {  
        if (type.contains(kk)) {  
            // prefer higher temp (more indicative of active hotspot)  
            if (best == null || val > best) best = val;  
            break;  
        }  
    }  
}  

return best;

}

private String safeReadOneLine(File f) {
BufferedReader br = null;
try {
br = new BufferedReader(new FileReader(f));
return br.readLine();
} catch (Throwable t) {
return null;
} finally {
try { if (br != null) br.close(); } catch (Throwable ignore) {}
}
}

// ------------------------------------------------------------
// LAB 15 thermal correlation — BILINGUAL (LABEL WHITE, VALUES GREEN)
// ------------------------------------------------------------
private void logLab15ThermalCorrelation(
        float batTempStart,
        float batTempPeak,
        float batTempEnd
) {

    final boolean gr = AppLang.isGreek(this);

    String label = gr
            ? "Θερμική συσχέτιση (κατά τη φόρτιση): "
            : "Thermal correlation (during charging): ";

    String values = String.format(
            Locale.US,
            gr
                    ? "αρχή %.1f°C → μέγιστο %.1f°C → τέλος %.1f°C"
                    : "start %.1f°C → peak %.1f°C → end %.1f°C",
            batTempStart,
            (Float.isNaN(batTempPeak) ? batTempEnd : batTempPeak),
            batTempEnd
    );

    // fallback: no UI
    if (txtLog == null) {
        logInfo(label + values);
        return;
    }

    // UI — label white, values green
    SpannableString sp = new SpannableString(label + values);

    // label = white
    sp.setSpan(
            new ForegroundColorSpan(0xFFFFFFFF),
            0,
            label.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    );

// values = green
sp.setSpan(
        new ForegroundColorSpan(0xFF39FF14),
        label.length(),
        sp.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
);

// ✅ ONE PIPELINE
appendStyledLog(sp);
}

// ------------------------------------------------------------
// Health checkbox map — BILINGUAL (LAB 14/17 use)
// ------------------------------------------------------------
private void printHealthCheckboxMap(String decision) {

    final boolean gr = AppLang.isGreek(this);

    String d = (decision == null) ? "" : decision.trim();

    logLine();

    boolean strong = "Strong".equalsIgnoreCase(d);
    boolean normal = "Normal".equalsIgnoreCase(d);
    boolean weak   = "Weak".equalsIgnoreCase(d);

    String strongTxt = gr ? "Ισχυρή" : "Strong";
    String normalTxt = gr ? "Κανονική" : "Normal";
    String weakTxt   = gr ? "Αδύναμη"  : "Weak";

    appendHtml((strong ? "✔ " : "• ") +
            "<font color='#FFFFFF'>" + strongTxt + "</font>");

    appendHtml((normal ? "✔ " : "• ") +
            "<font color='#FFFFFF'>" + normalTxt + "</font>");

    appendHtml((weak ? "✔ " : "• ") +
            "<font color='#FFFFFF'>" + weakTxt + "</font>");

    if (strong)
        logOk(gr ? "Χάρτης Υγείας: Ισχυρή" : "Health Map: Strong");
    else if (normal)
        logWarn(gr ? "Χάρτης Υγείας: Κανονική" : "Health Map: Normal");
    else if (weak)
        logError(gr ? "Χάρτης Υγείας: Αδύναμη" : "Health Map: Weak");
    else
        logInfo(gr ? "Χάρτης Υγείας: Πληροφοριακό"
                   : "Health Map: Informational");
}

// ============================================================
// LAB 15 — CONDITION CHECK POPUP (GEL STYLE)
// ============================================================
private void showLab15ConditionCheck(Runnable startAction) {

    final boolean gr = AppLang.isGreek(this);

    int percent = getBatteryPercentSafe();

    boolean badBat =
            percent < 20 || percent > 80;

    boolean ok =
            !badBat;


    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    b.setCancelable(true);

    LinearLayout root = buildGELPopupRoot(this);


    // HEADER

    root.addView(
            buildPopupHeader(
                    this,
                    gr
                            ? "Έλεγχος συνθηκών LAB 15"
                            : "LAB 15 Condition Check"
            )
    );


    // INFO TEXT

    TextView info = new TextView(this);

    info.setText(
        gr
                ? "Για την εκτέλεση του τεστ, απαιτείται\n"
                + "η μπαταρία να είναι φορτισμένη μεταξύ 20% – 80%\n"
                : "To run this test, the battery level must be\n\n"
                + "between 20% – 80%\n"
);

    info.setTextColor(0xFF39FF14);
    info.setTextSize(14f);
    info.setLineSpacing(0f, 1.2f);
    info.setPadding(0, dp(8), 0, dp(6));

    root.addView(info);


    StringBuilder warn = new StringBuilder();
    boolean hasWarn = false;


    // WARN

    if (badBat) {

        hasWarn = true;

        warn.append(
                gr
                        ? "• Η μπαταρία πρέπει να είναι μεταξύ 20% και 80%\n"
                        : "• Battery must be between 20% and 80%\n"
        );
    }


    if (!hasWarn) {

        warn.append(
                gr
                        ? "Οι συνθήκες είναι κατάλληλες"
                        : "Conditions are OK"
        );
    }


    // =========================
    // SPANNABLE
    // =========================

    SpannableStringBuilder sb = new SpannableStringBuilder();

    int white = 0xFFFFFFFF;
    int green = 0xFF39FF14;
    int red   = 0xFFFF4444;

    int start;


    // Battery

    start = sb.length();
    sb.append(gr ? "Μπαταρία: " : "Battery: ");
    sb.setSpan(new ForegroundColorSpan(white), start, sb.length(), 0);

    start = sb.length();
    sb.append(percent + "%\n");

    sb.setSpan(
            new ForegroundColorSpan(
                    badBat ? red : green
            ),
            start,
            sb.length(),
            0
    );


    // WARN

    start = sb.length();

    sb.append("\n");
    sb.append(warn.toString());

    sb.setSpan(
            new ForegroundColorSpan(
                    hasWarn ? red : green
            ),
            start,
            sb.length(),
            0
    );


    TextView msg = new TextView(this);

    msg.setText(sb);
    msg.setTextSize(14.5f);
    msg.setLineSpacing(0f, 1.2f);

    root.addView(msg);


    root.addView(buildMuteRow());


    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);


    Button cancel =
            gelButton(
                    this,
                    gr ? "Ακύρωση" : "Cancel",
                    0xFF8B0000
            );

    Button go =
            gelButton(
                    this,
                    gr ? "Συνέχεια" : "Continue",
                    0xFF0B5D1E
            );


    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    0,
                    dp(48),
                    1
            );

    lp.setMargins(dp(6), dp(18), dp(6), 0);

    cancel.setLayoutParams(lp);
    go.setLayoutParams(lp);

    row.addView(cancel);

    if (ok)
        row.addView(go);

    root.addView(row);


    b.setView(root);

    AlertDialog dlg = b.create();

    if (dlg.getWindow() != null)
        dlg.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );

    dlg.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(dlg);


    final String speakTextFinal =
            (gr
                    ? "Έλεγχος συνθηκών. "
                    + "Απαιτείται μπαταρία μεταξύ 20 και 80 τοις εκατό. "
                    : "Condition check. "
                    + "Battery must be between 20 and 80 percent. ")
            + sb.toString();


    new Handler(Looper.getMainLooper()).postDelayed(() -> {

        if (dlg.isShowing()
                && !AppTTS.isMuted(this)) {

            AppTTS.ensureSpeak(
                    this,
                    speakTextFinal
            );
        }

    }, 120);


    dlg.setOnDismissListener(d -> AppTTS.stop());
    
    dlg.setOnCancelListener(d -> AppTTS.stop());


    cancel.setOnClickListener(v -> {

        AppTTS.stop();
        dlg.dismiss();

    });


    go.setOnClickListener(v -> {

        AppTTS.stop();
        dlg.dismiss();

        if (startAction != null)
            startAction.run();

    });
}

// ------------------------------------------------------------
// LAB 15 USER ABORT — required by Exit button
// (safe: stops flags + dismisses dialog; does NOT nuke all handler callbacks)
// ------------------------------------------------------------
private void abortLab15ByUser() {

    final boolean gr = AppLang.isGreek(this);

    ui.post(() -> {

        if (!lab15Running) {
            try {
                if (lab15Dialog != null && lab15Dialog.isShowing())
                    lab15Dialog.dismiss();
            } catch (Throwable ignore) {}

            lab15Dialog = null;
            return;
        }

        logWarn(gr
                ? "Το LAB 15 ακυρώθηκε από τον χρήστη."
                : "LAB 15 cancelled by user.");

        lab15Running = false;
        lab15Finished = true;

        try {
            if (lab15Dialog != null && lab15Dialog.isShowing())
                lab15Dialog.dismiss();
        } catch (Throwable ignore) {}

        lab15Dialog = null;

        appendHtml("<br>");
        logOk(gr ? "Το Lab 15 ολοκληρώθηκε." : "Lab 15 finished.");
        logLine();
        enableSingleExportButton();
    });
}

// ------------------------------------------------------------
// TEMP FORMATTER (USED BY LAB 15 / LAB 16 LOGS)
// ------------------------------------------------------------
private String formatTemp(float temp) {
if (Float.isNaN(temp)) return "N/A";
return String.format(Locale.US, "%.1f°C", temp);
}

// ------------------------------------------------------------
// HTML / LOG SAFE ESCAPE
// ------------------------------------------------------------
private String escape(String s) {
    if (s == null) return "";

    return s
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
}

// ============================================================
// LAB 16 — INTERNAL + PERIPHERALS THERMAL HELPERS
// GEL LOCKED • HUMAN-READABLE • COMPACT MODE
// ============================================================

// ------------------------------------------------------------
// DATA MODEL
// ------------------------------------------------------------
private static class ThermalEntry {
final String label;
final float temp;

ThermalEntry(String label, float temp) {  
    this.label = label;  
    this.temp  = temp;  
}

}

// ------------------------------------------------------------
// INTERNAL THERMALS (CORE CHIPS ONLY)
// What user actually understands & cares about
// ------------------------------------------------------------
private List<ThermalEntry> buildThermalInternal() {

List<ThermalEntry> out = new ArrayList<>();  

try {  
    float batt = getBatteryTemperature();  
    if (batt > 0)  
        out.add(new ThermalEntry("Battery", batt));  

    Float cpu = readCpuTempSafe();  
    if (cpu != null && cpu > 0)  
        out.add(new ThermalEntry("CPU", cpu));  

    Float gpu = readGpuTempSafe();  
    if (gpu != null && gpu > 0)  
        out.add(new ThermalEntry("GPU", gpu));  

} catch (Throwable ignore) {}  

return out;

}

// ------------------------------------------------------------
// PERIPHERALS — CRITICAL ONLY (NOT EVERYTHING)
// System-protection relevant sensors
// ------------------------------------------------------------
private List<ThermalEntry> buildThermalPeripheralsCritical() {

List<ThermalEntry> out = new ArrayList<>();  

try {  
    File dir = new File("/sys/class/thermal");  
    File[] zones = dir.listFiles(f -> f.getName().startsWith("thermal_zone"));  
    if (zones == null) return out;  

    for (File z : zones) {  
        try {  
            String type = readSys(z, "type");  
            String temp = readSys(z, "temp");  
            if (type == null || temp == null) continue;  

            float c = Float.parseFloat(temp.trim()) / 1000f;  
            if (c <= 0 || c > 120) continue;  

            String t = type.toLowerCase(Locale.US);  

            if (t.contains("pmic"))  
                out.add(new ThermalEntry("PMIC", c));  
            else if (t.contains("charger") || t.contains("usb"))  
                out.add(new ThermalEntry("Charger", c));  
            else if (t.contains("skin") || t.contains("shell"))  
                out.add(new ThermalEntry("Device surface", c));  

        } catch (Throwable ignore) {}  
    }  
} catch (Throwable ignore) {}  

return out;

}

// ------------------------------------------------------------
// GEL STYLE OUTPUT — ONE LINE PER SENSOR (BILINGUAL)
// Label = white (log channel)
// Value = colored by severity
// ------------------------------------------------------------
private void logTempInline(String label, float c) {

    final boolean gr = AppLang.isGreek(this);

    String base = String.format(
            Locale.US,
            "%s: %.1f°C",
            label,
            c
    );

    if (c < 45f) {

        logOk(base + (gr ? " (ΦΥΣΙΟΛΟΓΙΚΗ)" : " (NORMAL)"));

    }
    else if (c < 55f) {

        logWarn(base + (gr ? " (ΑΥΞΗΜΕΝΗ)" : " (WARM)"));

    }
    else {

        logError(base + (gr ? " (ΥΠΕΡΘΕΡΜΑΝΣΗ)" : " (HOT)"));
    }
}

// ------------------------------------------------------------
// LAB 16 — Hidden / Non-displayed thermal safety check
// ------------------------------------------------------------
private boolean detectHiddenThermalAnomaly(float thresholdC) {

    try {

        File dir = new File("/sys/class/thermal");
        File[] zones = dir.listFiles(f -> f.getName().startsWith("thermal_zone"));
        if (zones == null) return false;

        for (File z : zones) {

            try {

                String type = readSys(z, "type");
                String temp = readSys(z, "temp");

                if (type == null || temp == null) continue;

                float c = Float.parseFloat(temp.trim()) / 1000f;

                if (c <= 0 || c > 120) continue;

                String t = type.toLowerCase(Locale.US);

                if (t.contains("battery") ||
                        t.contains("cpu") ||
                        t.contains("gpu")) {
                    continue;
                }

                if (c >= thresholdC) {
                    return true;
                }

            } catch (Throwable ignore) {
            }
        }

    } catch (Throwable ignore) {
    }

    return false;
}

// ============================================================
// LAB 17 — SAFE HELPERS (REQUIRED)
// Put in helpers section (same class), not inside lab17RunAuto()
// ============================================================

// True if LAB15 concluded that charging is being limited by system protection logic
private boolean isLab15ChargingPathSystemLimited() {
try {

return p.getBoolean("lab15_system_limited", false);  
} catch (Throwable t) {  
    return false;  
}

}

// Last known label (STRONG/NORMAL/MODERATE/WEAK) saved by LAB15
private String getLastLab15StrengthLabel() {
try {

return p.getString("lab15_strength_label", null);  
} catch (Throwable t) {  
    return null;  
}

}

// ============================================================
// REQUIRED HELPERS — LAB 15 / 16 / 17
// SAFE STUBS • SHARED PREF BASED • GEL EDITION
// ============================================================

// ---------------- LAB 15 ----------------
private int getLastLab15ChargeScore() {
try {

return p.getInt("lab15_charge_score", -1);  
} catch (Throwable t) {  
    return -1;  
}

}

private boolean hasValidLab15() {
return getLastLab15ChargeScore() >= 0;
}

// ---------------- LAB 16 ----------------
private int getLastLab16ThermalScore() {
try {

return p.getInt("lab16_thermal_score", -1);  
} catch (Throwable t) {  
    return -1;  
}

}

private boolean hasValidLab16() {
return getLastLab16ThermalScore() >= 0;
}

// ---------------- COOLING (SAFE DEFAULTS) ----------------
private boolean hasHardwareCoolingDevices() {
    // Most smartphones use passive thermal dissipation (no active cooling)
    return false;
}

private String buildHardwareCoolingReport() {

    final boolean gr = AppLang.isGreek(this);

    return gr
            ? "Δεν εντοπίστηκαν ενεργά συστήματα ψύξης. Η συσκευή χρησιμοποιεί παθητική θερμική απαγωγή."
            : "No active hardware cooling devices detected. The device relies on passive thermal dissipation.";
}

// ============================================================
// LAB 17: Premium Green-Gold Button (LOCKED)
// ============================================================
private Button makeTestButtonGreenGold(String text, Runnable action) {

Button btn = new com.gel.cleaner.GelButton(this);
btn.setText(text);  
btn.setAllCaps(false);  
btn.setTextColor(0xFF8B0000); // Red text  
btn.setTextSize(15f);  
btn.setTypeface(null, Typeface.BOLD);  
btn.setElevation(dp(3)); // premium shadow  

// -------------------------------  
// NORMAL STATE  
// -------------------------------  
GradientDrawable normalBg = new GradientDrawable();  
normalBg.setColor(0xFF00FF6A);          // GREEN NEON  
normalBg.setCornerRadius(dp(10));  
normalBg.setStroke(dp(3), 0xFFFFD700);  // GOLD BORDER  

// -------------------------------  
// PRESSED STATE  
// -------------------------------  
GradientDrawable pressedBg = new GradientDrawable();  
pressedBg.setColor(0xFF00CC55);          // darker green (pressed)  
pressedBg.setCornerRadius(dp(10));  
pressedBg.setStroke(dp(3), 0xFFFFD700);  

// -------------------------------  
// DISABLED STATE  
// -------------------------------  
GradientDrawable disabledBg = new GradientDrawable();  
disabledBg.setColor(0xFF1E3A2A);          // muted green  
disabledBg.setCornerRadius(dp(10));  
disabledBg.setStroke(dp(2), 0xFFBFAE60);  // faded gold  

StateListDrawable states = new StateListDrawable();  
states.addState(new int[]{-android.R.attr.state_enabled}, disabledBg);  
states.addState(new int[]{android.R.attr.state_pressed}, pressedBg);  
states.addState(new int[]{}, normalBg);  
btn.setBackground(states);

// -------------------------------  
// RIPPLE (Modern Android Feel)  
// -------------------------------  
RippleDrawable ripple = new RippleDrawable(  
        ColorStateList.valueOf(0x40FFFFFF), // soft white ripple  
        states,  
        null  
);  

btn.setBackground(ripple);  

LinearLayout.LayoutParams lp =  
        new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.MATCH_PARENT,  
                dp(54)  
        );  
lp.setMargins(0, dp(8), 0, dp(8));  
btn.setLayoutParams(lp);  

btn.setOnClickListener(v -> action.run());  

return btn;

}

private String readSys(File dir, String name) {
try (BufferedReader br =
new BufferedReader(new FileReader(new File(dir, name)))) {
return br.readLine();
} catch (Throwable ignore) {
return null;
}
}

// ============================================================
// GEL — HELPERS FOR LAB 18 / 19 / 21/ 26
// PRODUCTION • ROOT-AWARE • HUMAN-ORIENTED
// ============================================================

// ------------------------------------------------------------
// ROOT DETECTION (SAFE, NO LIES)
// ------------------------------------------------------------
private boolean isDeviceRooted() {
try {
String[] paths = {
"/system/bin/su",
"/system/xbin/su",
"/sbin/su",
"/system/su",
"/vendor/bin/su"
};
for (String p : paths) {
if (new File(p).exists()) return true;
}
} catch (Throwable ignore) {}
return false;
}

// ============================================================
// LAB 18 — STORAGE HEALTH HELPERS
// ============================================================

// Heuristic ONLY — real NAND wear is not exposed on consumer devices
private boolean detectStorageWearSignals() {
try {
StatFs s = new StatFs(Environment.getDataDirectory().getAbsolutePath());
long total = s.getBlockCountLong();
long free  = s.getAvailableBlocksLong();
if (total <= 0) return false;

int pctFree = (int) ((free * 100L) / total);  

    return pctFree < 5;  
} catch (Throwable t) {  
    return false;  
}

}

// ============================================================
// LAB 19 — MEMORY HELPERS (SELF-CONTAINED)
// No external dependencies
// ============================================================

private static class MemSnapshot {
    long memFreeKb;
    long cachedKb;
    long swapTotalKb;
    long swapFreeKb;
}

// ------------------------------------------------------------
// read /proc/meminfo without helper dependencies
// ------------------------------------------------------------
private MemSnapshot readMemSnapshotSafe() {
    MemSnapshot m = new MemSnapshot();

    BufferedReader br = null;
    try {
        File f = new File("/proc/meminfo");
        if (!f.exists()) return m;

        br = new BufferedReader(new FileReader(f));
        String line;

        while ((line = br.readLine()) != null) {

            if (line.startsWith("MemFree:"))
                m.memFreeKb = extractKb(line);

            else if (line.startsWith("Cached:"))
                m.cachedKb = extractKb(line);

            else if (line.startsWith("SwapTotal:"))
                m.swapTotalKb = extractKb(line);

            else if (line.startsWith("SwapFree:"))
                m.swapFreeKb = extractKb(line);
        }

    } catch (Throwable ignore) {
    } finally {
        try {
            if (br != null) br.close();
        } catch (Exception ignored) {}
    }

    return m;
}

// ------------------------------------------------------------
// extract number from "XXXX kB"
// ------------------------------------------------------------
private long extractKb(String line) {
    try {
        // keep only digits
        String n = line.replaceAll("[^0-9]", "");
        return Long.parseLong(n);
    } catch (Throwable t) {
        return 0;
    }
}

// ------------------------------------------------------------
// MEMORY PRESSURE LEVEL
// ------------------------------------------------------------
private String pressureLevel(long memFreeKb, long cachedKb, long swapUsedKb) {

    boolean lowFree   = memFreeKb < (150 * 1024);   // <150MB
    boolean midFree   = memFreeKb < (300 * 1024);   // <300MB
    boolean heavySwap = swapUsedKb > (512 * 1024);  // >512MB
    boolean midSwap   = swapUsedKb > (256 * 1024);  // >256MB

    if (lowFree && heavySwap) return "High";
    if (midFree || midSwap)   return "Medium";
    return "Low";
}

// ------------------------------------------------------------
// ZRAM SWAP DEPENDENCY
// ------------------------------------------------------------
private String zramDependency(long swapUsedKb, long totalMemBytes) {

    long swapUsedMb = swapUsedKb / 1024;
    long totalMb    = totalMemBytes / (1024 * 1024);

    if (swapUsedMb > (totalMb / 4)) return "High";     // >25% of RAM
    if (swapUsedMb > (totalMb / 8)) return "Medium";   // >12.5%
    return "Low";
}

// ------------------------------------------------------------
// HUMAN LABEL (BILINGUAL)
// ------------------------------------------------------------
private String humanPressureLabel(String level) {

    final boolean gr = AppLang.isGreek(this);

    if ("High".equalsIgnoreCase(level)) {
        return gr ? "Υψηλή" : "High";
    }

    if ("Medium".equalsIgnoreCase(level)) {
        return gr ? "Μέτρια" : "Moderate";
    }

    return gr ? "Χαμηλή" : "Low";
}

// ============================================================
// LAB 26 — APPS IMPACT HELPERS
// ============================================================

private boolean isSystemApp(ApplicationInfo ai) {
return (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
}

private long getAppInstalledSizeSafe(String pkg) {
try {
PackageManager pm = getPackageManager();
ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
File apk = new File(ai.sourceDir);
return apk.exists() ? apk.length() : -1;
} catch (Throwable t) {
return -1;
}
}

private boolean hasUsageAccess() {
    try {
        android.app.AppOpsManager appOps =
                (android.app.AppOpsManager) getSystemService(android.content.Context.APP_OPS_SERVICE);

        if (appOps == null) return false;

        int mode;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            mode = appOps.unsafeCheckOpNoThrow(
                    android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    getPackageName()
            );
        } else {
            mode = appOps.checkOpNoThrow(
                    android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    getPackageName()
            );
        }

        return mode == android.app.AppOpsManager.MODE_ALLOWED;

    } catch (Throwable ignore) {
        return false;
    }
}

// ============================================================
// LAB 19 — RAM / MEMORY HELPERS (ROOT AWARE)
// ============================================================

private boolean isZramActiveSafe() {
try {
return new File("/sys/block/zram0").exists();
} catch (Throwable t) {
return false;
}
}

private boolean isSwapActiveSafe() {
try {
BufferedReader br = new BufferedReader(new FileReader("/proc/swaps"));
int lines = 0;
while (br.readLine() != null) lines++;
br.close();
return lines > 1; // header + entries
} catch (Throwable t) {
return false;
}
}

private long readCachedMemoryKbSafe() {
try {
BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"));
String line;
while ((line = br.readLine()) != null) {
if (line.startsWith("Cached:")) {
br.close();
return Long.parseLong(line.replaceAll("\\D+", ""));
}
}
br.close();
} catch (Throwable ignore) {}
return -1;
}

// ============================================================
// LAB 20 — UPTIME / REBOOT / PRESSURE HELPERS
// ============================================================

// Reads kernel OOM kill counter (heuristic pressure signal)
private int readLowMemoryKillCountSafe() {
try {
BufferedReader br = new BufferedReader(new FileReader("/proc/vmstat"));
String line;
while ((line = br.readLine()) != null) {
if (line.startsWith("oom_kill")) {
br.close();
return Integer.parseInt(line.replaceAll("\\D+", ""));
}
}
br.close();
} catch (Throwable ignore) {}
return -1;
}

// ============================================================
// SAFETY STUBS — Stability detectors
// (production-safe, no side effects)
// ============================================================

private boolean detectRecentReboots() {
    try {
        // TODO: future implementation (DropBox / uptime diff)
        return false;
    } catch (Throwable t) {
        return false;
    }
}

private boolean detectSignalInstability() {
    try {
        // TODO: future implementation (Telephony / ServiceState history)
        return false;
    } catch (Throwable t) {
        return false;
    }
}

private boolean detectSensorInstability() {
    try {
        // TODO: future implementation (SensorManager error rates)
        return false;
    } catch (Throwable t) {
        return false;
    }
}

private boolean detectThermalSpikes() {
    try {
        // TODO: future implementation (thermal zones delta scan)
        return false;
    } catch (Throwable t) {
        return false;
    }
}

private boolean detectPowerInstability() {
    try {
        // TODO: future implementation (battery + power HAL hints)
        return false;
    } catch (Throwable t) {
        return false;
    }
}

// ------------------------------------------------------------
// MUTE ROW (UNIFIED — AppTTS HELPER)
// ------------------------------------------------------------
private LinearLayout buildMuteRow() {

    final boolean gr = AppLang.isGreek(this);

    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER_VERTICAL);
    row.setPadding(0, dp(8), 0, dp(16));

    CheckBox muteCheck = new CheckBox(this);
    muteCheck.setChecked(AppTTS.isMuted(this));
    muteCheck.setPadding(0, 0, dp(6), 0);

    TextView label = new TextView(this);
    label.setText(
            gr ? "Σίγαση φωνητικών οδηγιών"
               : "Mute voice instructions"
    );
    label.setTextColor(Color.WHITE);
    label.setTextSize(14f);

    // --------------------------------------------------------
    // TOGGLE (ROW + LABEL CLICK)
    // --------------------------------------------------------
    View.OnClickListener toggle = v -> {

        boolean newState = !AppTTS.isMuted(this);

        AppTTS.setMuted(this, newState);
        muteCheck.setChecked(newState);

        // 🔇 Immediate hard stop when muting
        if (newState) {
            try { AppTTS.stop(); } catch (Throwable ignore) {}
        }
    };

    row.setOnClickListener(toggle);
    label.setOnClickListener(toggle);

    // --------------------------------------------------------
    // CHECKBOX DIRECT CHANGE
    // --------------------------------------------------------
    muteCheck.setOnCheckedChangeListener((button, checked) -> {

        if (checked == AppTTS.isMuted(this)) return;

        AppTTS.setMuted(this, checked);

        if (checked) {
            try { AppTTS.stop(); } catch (Throwable ignore) {}
        }
    });

    row.addView(muteCheck);
    row.addView(label);

    return row;
}

// ============================================================
// POPUP HEADER + TITLE (NO MUTE BUTTON HERE)
// ============================================================
private LinearLayout buildPopupHeader(Context ctx, String titleText) {

    LinearLayout header = new LinearLayout(ctx);
    header.setOrientation(LinearLayout.HORIZONTAL);
    header.setGravity(Gravity.CENTER_VERTICAL);
    header.setPadding(0, 0, 0, dp(12));

    TextView title = new TextView(ctx);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.START);

    LinearLayout.LayoutParams lpTitle =
            new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            );
    title.setLayoutParams(lpTitle);

    header.addView(title);
    return header;
}

// ============================================================
// GEL BUTTON — STANDARD (GREEN / GOLD)
// ============================================================
private Button gelButton(Context ctx, String text, int bgColor) {

    Button b = new com.gel.cleaner.GelButton(ctx);
    b.setText(text);
    b.setAllCaps(false);
    b.setTextColor(Color.WHITE);
    b.setTextSize(15f);
    b.setTypeface(null, Typeface.BOLD);

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(bgColor);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);

    b.setBackground(bg);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(52)
            );
    lp.setMargins(0, dp(10), 0, 0);
    b.setLayoutParams(lp);

    return b;
}

// ============================================================
// GEL POPUP ROOT — BLACK + GOLD (UNIFIED)
// ============================================================
private LinearLayout buildGELPopupRoot(Context ctx) {

    LinearLayout root = new LinearLayout(ctx);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(
            dp(24),  // left
            dp(22),  // top
            dp(24),  // right
            dp(18)   // bottom
    );

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);        // GEL black
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(4), 0xFFFFD700); // GEL gold
    root.setBackground(bg);

    return root;
}

// ============================================================
// TTS — speakOnce helper (safe)
// ============================================================
private void speakOnce(String text) {
try {
if (text == null) return;
if (AppTTS.isMuted(this)) return;
AppTTS.ensureSpeak(this, text);
} catch (Throwable ignore) {}
}

// ============================================================
// USAGE ACCESS
// ============================================================

private void checkUsageAccessGate() {

    if (!hasUsageAccess()) {
        showUsageAccessDialog();
    }
}

// ============================================================

private void showUsageAccessDialog() {

    if (hasUsageAccess()) return;

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(22), dp(24), dp(20));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    TextView title = new TextView(this);
    title.setText(gr
            ? "ΑΠΑΙΤΕΙΤΑΙ ΠΡΟΣΒΑΣΗ ΧΡΗΣΗΣ"
            : "USAGE ACCESS REQUIRED");
    title.setTextColor(Color.WHITE);
    title.setTextSize(19f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(14));
    root.addView(title);

    TextView msg = new TextView(this);

    final String messageText =
            gr
                    ? "Καμία συλλογή προσωπικών δεδομένων δεν γίνεται με την παραχώρηση της Πρόσβασης Χρήσης.\n\n"
                      + "Θα μεταφερθείς στις Ρυθμίσεις."
                    : "Usage Access is required for certain analysis features.\n\n"
                      + "No personal data is collected.\n\n"
                      + "You will be redirected to Settings.";

    msg.setText(messageText);
    msg.setTextColor(0xFF00FF9C);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    msg.setLineSpacing(0f, 1.15f);
    msg.setPadding(dp(6), 0, dp(6), dp(20));
    root.addView(msg);

    root.addView(buildMuteRow());

    LinearLayout btnRow = new LinearLayout(this);
    btnRow.setOrientation(LinearLayout.HORIZONTAL);
    btnRow.setGravity(Gravity.CENTER);

    LinearLayout.LayoutParams btnLp =
            new LinearLayout.LayoutParams(0, dp(48), 1f);
    btnLp.setMargins(dp(6), 4, dp(6), 4);

    Button continueBtn = new com.gel.cleaner.GelButton(this);
    continueBtn.setText(gr ? "ΣΥΝΕΧΕΙΑ" : "CONTINUE");
    continueBtn.setAllCaps(false);
    continueBtn.setTextColor(Color.WHITE);
    continueBtn.setTextSize(16f);
    continueBtn.setTypeface(null, Typeface.BOLD);
    continueBtn.setLayoutParams(btnLp);

    GradientDrawable contBg = new GradientDrawable();
    contBg.setColor(0xFF00E676);
    contBg.setCornerRadius(dp(10));
    contBg.setStroke(dp(3), 0xFFFFD700);
    continueBtn.setBackground(contBg);

    Button skipBtn = new com.gel.cleaner.GelButton(this);
    skipBtn.setText(gr ? "ΠΑΡΑΛΕΙΨΗ" : "SKIP");
    skipBtn.setAllCaps(false);
    skipBtn.setTextColor(Color.WHITE);
    skipBtn.setTextSize(16f);
    skipBtn.setTypeface(null, Typeface.BOLD);
    skipBtn.setLayoutParams(btnLp);

    GradientDrawable skipBg = new GradientDrawable();
    skipBg.setColor(0xFFC62828);
    skipBg.setCornerRadius(dp(10));
    skipBg.setStroke(dp(3), 0xFFFFD700);
    skipBtn.setBackground(skipBg);

    btnRow.addView(skipBtn);
    btnRow.addView(continueBtn);
    root.addView(btnRow);

    b.setView(root);
    b.setCancelable(false);

    AlertDialog d = b.create();

    if (d.getWindow() != null) {
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    d.setOnDismissListener(dialog -> {
        try { AppTTS.stop(); } catch (Throwable ignore) {}
    });

    d.setOnCancelListener(dialog -> {
        try { AppTTS.stop(); } catch (Throwable ignore) {}
    });

    d.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(d);

    root.postDelayed(() -> {
        try {
            if (!AppTTS.isMuted(this)) {
                AppTTS.speak(this, messageText);
            }
        } catch (Throwable ignore) {}
    }, 220);

    continueBtn.setOnClickListener(v -> {

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    d.dismiss();

    try {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    } catch (Throwable e) {
        // Fallback
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }
});

skipBtn.setOnClickListener(v -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();
});

}

// ============================================================
// LAB 28 — TECHNICIAN POPUP (FINAL / CHECKBOX MUTE)
// ============================================================
private void showLab28Popup() {

    runOnUiThread(() -> {

        final boolean gr = AppLang.isGreek(this);

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(true);

        // ==========================
        // ROOT (GEL HELPER)
        // ==========================
        LinearLayout root = buildGELPopupRoot(this);

        // ==========================
        // HEADER (TITLE ONLY)
        // ==========================
        LinearLayout header = buildPopupHeader(
        this,
        gr
                ? "LAB 28 — Τεχνική Ανάλυση"
                : "LAB 28 — Technician Analysis"
);
        root.addView(header);

        // ==========================
        // MESSAGE
        // ==========================
        final String text = gr ? getLab28TextGR() : getLab28TextEN();

        TextView msg = new TextView(this);
        msg.setText(text);
        msg.setTextColor(0xFF00FF9C);
        msg.setTextSize(15f);
        msg.setLineSpacing(0f, 1.15f);
        msg.setPadding(0, 0, 0, dp(8));
        root.addView(msg);
        
        // ==========================
        // MUTE ROW (CHECKBOX)
        // ==========================
        root.addView(buildMuteRow());


        // ==========================
        // OK BUTTON
        // ==========================
        Button okBtn = new com.gel.cleaner.GelButton(this);
        okBtn.setText("OK");
        okBtn.setAllCaps(false);
        okBtn.setTextColor(Color.WHITE);
        okBtn.setTextSize(15f);

        GradientDrawable okBg = new GradientDrawable();
        okBg.setColor(0xFF0F8A3B);
        okBg.setCornerRadius(dp(10));
        okBg.setStroke(dp(3), 0xFFFFD700);
        okBtn.setBackground(okBg);

        LinearLayout.LayoutParams lpOk =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                );
        lpOk.setMargins(0, dp(16), 0, 0);
        okBtn.setLayoutParams(lpOk);

        root.addView(okBtn);

        // ==========================
        // DIALOG
        // ==========================
        
        b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(d);
}

        // ==========================
        // SPEAK (ONLY IF NOT MUTED)
        // ==========================
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (d.isShowing() && !AppTTS.isMuted(this)) {
                AppTTS.ensureSpeak(this, text);
            }
        }, 120);

        okBtn.setOnClickListener(v -> {
            AppTTS.stop();
            d.dismiss();
        });
    });
}

// ============================================================
// TEXT HELPERS — LAB 28
// ============================================================

private String getLab28TextEN() {
    return
        "For improved diagnostic accuracy, it is recommended to run all labs, before this test. " +
        "This lab, performs symptom-based analysis only. " +
        "It does not diagnose hardware faults, and does not confirm solder or interconnect defects. " +
        "Results, may indicate behavioral patterns, consistent with intermittent contact issues, " +
        "such as, unstable operation, random reboots, or signal drops. " +
        "Use this lab, strictly as a triage tool, and not as a final hardware diagnosis.";
}

private String getLab28TextGR() {
    return
        "Για βελτιωμένη διαγνωστική ακρίβεια, συνιστάται η εκτέλεση όλων των labs, πριν από αυτό το τεστ. " +
        "Το lab αυτό, πραγματοποιεί αποκλειστικά ανάλυση, βασισμένη σε συμπτώματα. " +
        "Δεν διαγιγνώσκει βλάβες υλικού, και δεν επιβεβαιώνει προβλήματα κόλλησης ή διασύνδεσης. " +
        "Τα αποτελέσματα, μπορεί να υποδεικνύουν πρότυπα συμπεριφοράς, συμβατά με διακοπτόμενη επαφή. " +
        "όπως, ασταθή λειτουργία, τυχαίες επανεκκινήσεις, ή απώλειες σήματος. " +
        "Χρησιμοποίησε το lab, αυστηρά ως εργαλείο προελέγχου, και όχι ως τελική διάγνωση υλικού.";
}

// ============================================================
// LAB 29 — Device Authenticity & Repair Analysis
// SERVICE LEVEL — COMPONENT & BOARD INSPECTION (LOGIC BASED)
// ============================================================
private void lab29AuthenticityCheck() {
    final boolean gr = AppLang.isGreek(this);
    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 29 — Παρατηρούμενη ταυτότητα υλικού / ανταλλακτικών"
               : "LAB 29 — Observed hardware and parts identity");
    logLine();

    // Public Android build descriptors describe the reported device profile.
    // They do not attest to individual replacement parts or prior repairs.
    logLabelValue(gr ? "Κατασκευαστής που δηλώνει το Android" : "Android-reported manufacturer",
            String.valueOf(Build.MANUFACTURER));
    logLabelValue(gr ? "Μάρκα / μοντέλο Android" : "Android brand / model",
            String.valueOf(Build.BRAND) + " / " + String.valueOf(Build.MODEL));
    logLabelValue(gr ? "Κωδικό όνομα συσκευής" : "Device codename",
            String.valueOf(Build.DEVICE));
    logLabelValue(gr ? "Έκδοση συστήματος" : "OS version",
            String.valueOf(Build.VERSION.RELEASE) + " (API " + Build.VERSION.SDK_INT + ")");
    logLabelValue(gr ? "Patch date (δηλωμένη)" : "OS-reported security patch",
            String.valueOf(Build.VERSION.SECURITY_PATCH));

    // Enumeration is not provenance verification: a camera/sensor being exposed
    // by Android cannot prove which OEM part was physically installed.
    try {
        android.hardware.camera2.CameraManager camera =
                (android.hardware.camera2.CameraManager) getSystemService(CAMERA_SERVICE);
        if (camera != null) {
            logLabelValue(gr ? "Ορατά camera IDs" : "Exposed camera IDs",
                    String.valueOf(camera.getCameraIdList().length));
        } else {
            logLabelWarnValue(gr ? "Camera API" : "Camera API",
                    gr ? "Δεν διατίθεται" : "Unavailable");
        }
    } catch (Exception unavailable) {
        logLabelWarnValue(gr ? "Camera API" : "Camera API",
                gr ? "Μη διαθέσιμη πλήρης απογραφή" : "Inventory unavailable or restricted");
    }

    try {
        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = manager == null ? null : manager.getSensorList(Sensor.TYPE_ALL);
        if (sensors != null) {
            logLabelValue(gr ? "Αναφερόμενοι αισθητήρες" : "Android-exposed sensors",
                    String.valueOf(sensors.size()));
        } else {
            logLabelWarnValue(gr ? "Αισθητήρες" : "Sensors",
                    gr ? "Δεν διατίθεται απογραφή" : "Inventory unavailable");
        }
    } catch (RuntimeException unavailable) {
        logLabelWarnValue(gr ? "Αισθητήρες" : "Sensors",
                gr ? "Δεν διατίθεται απογραφή" : "Inventory unavailable");
    }

    BatteryEvidence cycleCount = BatteryPublicCollector.collect(this).get("cycle_count");
    if (cycleCount.isObserved()) {
        logLabelValue(gr ? "Κύκλοι μπαταρίας (δηλωμένοι)" : "Battery cycles (reported)",
                String.format(Locale.US, "%.0f", cycleCount.value));
    } else {
        logLabelValue(gr ? "Κύκλοι μπαταρίας" : "Battery cycle count",
                gr ? "Δεν παρέχεται από το Android/OEM" : "Not exposed by Android/OEM");
    }

    appendHtml("<br>");
    logLabelWarnValue(gr ? "Γνησιότητα μπαταρίας" : "Battery originality",
            gr ? "ΜΗ ΕΠΑΛΗΘΕΥΣΙΜΗ χωρίς έμπιστη ταυτοποίηση OEM/ανταλλακτικού"
               : "UNVERIFIABLE without trusted OEM/part identity evidence");
    logLabelWarnValue(gr ? "Αντικατάσταση οθόνης / κάμερας" : "Screen / camera replacement",
            gr ? "Δεν προκύπτει από αναφερόμενα hardware IDs ή συμπτώματα"
               : "Cannot be established from reported hardware IDs or symptoms");
    logLabelWarnValue(gr ? "Προηγούμενη επισκευή μητρικής" : "Prior motherboard repair",
            gr ? "Δεν μπορεί να διαγνωστεί από public Android APIs ή logs"
               : "Cannot be determined from public Android APIs or logs");
    logLabelWarnValue(gr ? "Δείκτης γνησιότητας" : "Authenticity percentage",
            gr ? "Δεν υπολογίζεται· δεν υπάρχει έγκυρο πρότυπο αναφοράς"
               : "Not calculated; no validated reference baseline");
    logLabelValue(gr ? "Επαλήθευση από τεχνικό" : "External verification",
            gr ? "Έλεγχος πραγματικών ανταλλακτικών, επίσημο ιστορικό επισκευών "
                   + "ή επιθεώρηση από εξουσιοδοτημένο τεχνικό."
               : "Verify installed parts against trusted OEM service evidence "
                   + "or authorized physical inspection.");

    GELServiceLog.info("SUMMARY: PART_AUTHENTICITY=UNKNOWN; "
            + "REPAIR_HISTORY=NOT_AVAILABLE; SOURCE=ANDROID_PUBLIC_API");
    appendHtml("<br>");
    logOk(gr ? "Το Lab 29 ολοκληρώθηκε με ρητούς περιορισμούς."
             : "Lab 29 finished with explicit evidence limits.");
    logLine();
}

// ============================================================
// SPEAKER OUTPUT EVALUATION — UNIFIED (LAB 1 / LAB 2)
// ============================================================
private enum SpeakerOutputState {
    NO_OUTPUT,     // No acoustic output detected
    LOW_SIGNAL,    // Output detected but weak / low confidence
    OK             // Normal speaker output
}

private SpeakerOutputState evaluateSpeakerOutput(
        MicDiagnosticEngine.Result r
) {
    if (r == null)
        return SpeakerOutputState.NO_OUTPUT;

    if (r.rms <= 0 && r.peak <= 0)
        return SpeakerOutputState.NO_OUTPUT;

    // Low quality signal (still valid output)
    if ("LOW".equalsIgnoreCase(r.confidence)
            || "WEAK".equalsIgnoreCase(r.confidence))
        return SpeakerOutputState.LOW_SIGNAL;

    return SpeakerOutputState.OK;
}

// ============================================================
// AUDIO OUTPUT CONTEXT — LAB 1 SUPPORT (BILINGUAL)
// ============================================================
private static class AudioOutputContext {

    boolean volumeMuted;
    boolean volumeLow;

    boolean bluetoothRouted;
    boolean wiredRouted;

    int volume;
    int maxVolume;

    String explain(boolean gr) {

        if (volumeMuted) {
            return gr
                    ? "Η ένταση πολυμέσων είναι στο μηδέν (0%)."
                    : "Media volume is muted (0%).";
        }

        if (bluetoothRouted) {
            return gr
                    ? "Ο ήχος δρομολογείται σε συσκευή Bluetooth."
                    : "Audio is routed to a Bluetooth device.";
        }

        if (wiredRouted) {
            return gr
                    ? "Ο ήχος δρομολογείται σε ενσύρματα ακουστικά ή USB audio."
                    : "Audio is routed to a wired headset or USB audio device.";
        }

        if (volumeLow) {
            return gr
                    ? "Η ένταση πολυμέσων είναι πολύ χαμηλή."
                    : "Media volume is very low.";
        }

        return gr
                ? "Η δρομολόγηση ήχου και η ένταση φαίνονται φυσιολογικές."
                : "Audio output routing and volume appear normal.";
    }
}

// ------------------------------------------------------------
// GET AUDIO OUTPUT CONTEXT
// ------------------------------------------------------------
private AudioOutputContext getAudioOutputContext() {

    AudioOutputContext c = new AudioOutputContext();

    AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
    if (am == null) return c;

    c.volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
    c.maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    c.volumeMuted = (c.volume == 0);
    c.volumeLow   = (c.volume > 0 && c.volume < (c.maxVolume * 0.6f));

    c.bluetoothRouted =
            am.isBluetoothA2dpOn() ||
            am.isBluetoothScoOn();

    c.wiredRouted = am.isWiredHeadsetOn();

    return c;
}

// ============================
// MIC CAPTURE (LOCAL HELPER)
// ============================
private static final class MicQuickResult {
    final int rms;
    final int peak;
    MicQuickResult(int rms, int peak) { this.rms = rms; this.peak = peak; }
}

private MicQuickResult micCaptureOnceMs(int ms) {

    // Permission gate (mandatory)
    try {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED) {
            return new MicQuickResult(0, 0);
        }
    } catch (Throwable ignore) {
        // if anything weird, fail closed
        return new MicQuickResult(0, 0);
    }

    final int sr = 44100;
    final int ch = AudioFormat.CHANNEL_IN_MONO;
    final int fmt = AudioFormat.ENCODING_PCM_16BIT;

    int min = AudioRecord.getMinBufferSize(sr, ch, fmt);
    if (min <= 0) return new MicQuickResult(0, 0);

    AudioRecord ar = null;
    try {
        ar = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sr, ch, fmt,
                min * 2
        );

        short[] buf = new short[Math.max(256, min)];
        ar.startRecording();

        long until = SystemClock.uptimeMillis() + Math.max(250, ms);

        long sumSq = 0;
        long nSamp = 0;
        int peak = 0;

        while (SystemClock.uptimeMillis() < until) {
            int n = ar.read(buf, 0, buf.length);
            if (n > 0) {
                for (int i = 0; i < n; i++) {
                    int v = Math.abs(buf[i]);
                    if (v > peak) peak = v;
                    sumSq += (long) v * (long) v;
                    nSamp++;
                }
            }
        }

        if (nSamp <= 0) return new MicQuickResult(0, 0);

        int rms = (int) Math.sqrt((double) sumSq / (double) nSamp);
        return new MicQuickResult(rms, peak);

    } catch (Throwable t) {
        return new MicQuickResult(0, 0);
    } finally {
        try {
            if (ar != null) {
                try { ar.stop(); } catch (Throwable ignore) {}
                try { ar.release(); } catch (Throwable ignore) {}
            }
        } catch (Throwable ignore) {}
    }
}

// ============================================================
// PERMISSION GUARD HELPERS — GEL EDITION
// Clean single-line permission guards for each LAB
// ============================================================

// ------------------------------------------------------------
// MICROPHONE (RECORD_AUDIO)
// ------------------------------------------------------------
private boolean guardMic(Runnable afterGranted) {
    return ensurePermissions(
            new String[]{Manifest.permission.RECORD_AUDIO},
            afterGranted
    );
}

// ------------------------------------------------------------
// CAMERA (CAMERA)
// ------------------------------------------------------------
private boolean guardCamera(Runnable afterGranted) {
    return ensurePermissions(
            new String[]{Manifest.permission.CAMERA},
            afterGranted
    );
}

// ------------------------------------------------------------
// LOCATION (FINE + COARSE)
// ------------------------------------------------------------
private boolean guardLocation(Runnable afterGranted) {
    return ensurePermissions(
            new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },
            afterGranted
    );
}

// ------------------------------------------------------------
// PHONE STATE (READ_PHONE_STATE)
// ------------------------------------------------------------
private boolean guardPhoneState(Runnable afterGranted) {
    return ensurePermissions(
            new String[]{Manifest.permission.READ_PHONE_STATE},
            afterGranted
    );
}

// ------------------------------------------------------------
// STORAGE READ (Android ≤ 12)
// ------------------------------------------------------------
private boolean guardStorageRead(Runnable afterGranted) {
    return ensurePermissions(
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            afterGranted
    );
}

// ------------------------------------------------------------
// STORAGE WRITE (Android ≤ 10)
// ------------------------------------------------------------
private boolean guardStorageWrite(Runnable afterGranted) {
    return ensurePermissions(
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
            afterGranted
    );
}

// ------------------------------------------------------------
// USAGE ACCESS (SPECIAL PERMISSION — NOT RUNTIME)
// ------------------------------------------------------------
private boolean guardUsageAccess() {

    if (hasUsageAccess()) {
        return true;
    }

    showUsageAccessDialog();
    return false;
}

// ============================================================
// LABS 1-5: AUDIO & VIBRATION
// ============================================================

// ============================================================
// LAB 1 - Speaker Tone Test (AUTO) — WITH AUDIO PATH CHECK
// ============================================================
private void lab1SpeakerTone() {
    if (!guardMic(this::lab1SpeakerTone)) return;
    if (lab3GuidedPending.get() || lab4GuidedPending.get()) {
        logWarn(AppLang.isGreek(this)
                ? "Άλλος έλεγχος ήχου σε εξέλιξη· περίμενε να ολοκληρωθεί."
                : "Another guided audio test is active; finish it first.");
        return;
    }
    if (!lab12AudioBusy.compareAndSet(false, true)) {
        logWarn(AppLang.isGreek(this)
                ? "Άλλη ακουστική δοκιμή εκτελείται· το αποτέλεσμα παραμένει μη επαληθευμένο."
                : "Another acoustic challenge is active; no new result was collected.");
        return;
    }
    runOnUiThread(() -> {
        if (btnExport != null) {
            btnExport.setEnabled(false);
            btnExport.setAlpha(0.5f);
        }
    });

    final boolean gr = AppLang.isGreek(this);
    appendHtml("<br>");
    logLine();
    logSection(gr ? "LAB 1 — Ηχείο: συγχρονισμένη δοκιμή τόνου"
                  : "LAB 1 — Speaker: synchronized tone challenge");
    logLine();

    new Thread(() -> {
        try {
        AcousticStimulusProbe.Result result = AcousticStimulusProbe.run(this, false);
        if (result.attempted && result.responseObserved) {
            logLabelOkValue(gr ? "Σύμπτωση ερεθίσματος–απόκρισης"
                               : "Stimulus/response correlation",
                    gr ? "Ο τόνος DTMF 697/1209 Hz καταγράφηκε κατά την αναπαραγωγή"
                       : "DTMF 697/1209 Hz rose during synchronized playback");
        } else {
            logLabelWarnValue(gr ? "Αποτέλεσμα ελέγχου" : "Probe result",
                    gr ? "ΜΗ ΕΠΙΒΕΒΑΙΩΜΕΝΟ — όχι διάγνωση χαλασμένου ηχείου"
                       : "INCONCLUSIVE — not evidence of speaker hardware failure");
        }
        logLabelValue(gr ? "Αριθμός επαληθευμένων τόνων" : "Correlated tones",
                result.observedTones + "/" + result.testedTones);
        if (result.testedTones > 0) {
            logLabelValue(gr ? "Άνοδος στο φάσμα (σε σχέση με περιβάλλον)"
                             : "Spectral rise vs ambient",
                    String.format(Locale.US, "%.1f / %.1f dB",
                            result.weakestLowRiseDb, result.weakestHighRiseDb));
        }
        // Restore user-useful microphone amplitude information from the CURRENT
        // synchronized capture, not the legacy post-tone unrelated recording.
        // Raw 16-bit PCM levels are not calibrated dB SPL or a speaker verdict.
        if (result.testedTones > 0) {
            if (!Double.isNaN(result.ambientMicRms) && !Double.isNaN(result.challengeMicRms)) {
                logLabelValue(gr ? "RMS μικροφώνου — πριν τον τόνο" : "Mic RMS — ambient",
                        String.format(Locale.US, "%.1f (PCM)", result.ambientMicRms));
                logLabelValue(gr ? "RMS μικροφώνου — κατά τον τόνο" : "Mic RMS — challenge",
                        String.format(Locale.US, "%.1f (PCM)", result.challengeMicRms));
            }
            if (!Double.isNaN(result.challengeMicPeak)) {
                logLabelValue(gr ? "Peak μικροφώνου — κατά τον τόνο" : "Mic peak — challenge",
                        String.format(Locale.US, "%.0f (PCM)", result.challengeMicPeak));
            }
            logLabelValue(gr ? "Ποιότητα δείγματος" : "Capture quality",
                    result.clipped
                            ? (gr ? "Παραμόρφωση clipping — επανάλαβε με χαμηλότερη ένταση"
                                  : "Clipping — repeat at a lower volume")
                            : (gr ? "Δεν ανιχνεύθηκε clipping στο δείγμα"
                                  : "No clipping detected in sample"));
        }
        logLabelValue(gr ? "Διαδρομή μικροφώνου" : "Mic input route", result.inputRoute);
        logLabelValue(gr ? "Μέθοδος" : "Method",
                "AudioRecord during ToneGenerator, baseline OFF / challenge ON");
        logLabelValue(gr ? "Αιτιολόγηση" : "Reason", result.reason);
        if (!result.responseObserved) {
            logLabelWarnValue(gr ? "Προτεινόμενη ενέργεια" : "Recommended action",
                    gr ? "Έλεγξε την ένταση, αποσύνδεσε Bluetooth/ακουστικά/USB ήχο, "
                            + "δοκίμασε σε ήσυχο περιβάλλον και επανάλαβε. "
                            + "Η απουσία απόκρισης δεν τεκμηριώνει βλάβη ηχείου."
                       : "Check volume, disconnect Bluetooth/headphones/USB audio, "
                            + "retry in a quiet room. Missing response does not prove speaker failure.");
        }
        logLabelWarnValue(gr ? "Περιορισμός" : "Limitations",
                gr ? "Ο ενσωματωμένος μικροφωνικός έλεγχος δεν πιστοποιεί ποιο "
                       + "ηχείο ακούστηκε ή την ακουστική ποιότητά του."
                   : "Internal microphone cannot certify which speaker emitted "
                       + "the tone or its calibrated acoustic performance.");
        appendHtml("<br>");
        if (result.attempted) {
            logOk(gr ? "Η δειγματοληψία Lab 1 ολοκληρώθηκε." : "Lab 1 sampling completed.");
        } else {
            logLabelWarnValue(gr ? "Κατάσταση Lab 1" : "Lab 1 status",
                    gr ? "ΜΗ ΔΙΑΘΕΣΙΜΟ — η ακουστική δοκιμή δεν εκτελέστηκε"
                       : "UNAVAILABLE — acoustic challenge was not performed");
        }
        logLine();
        } catch (RuntimeException e) {
            logLabelWarnValue("Acoustic probe", "UNAVAILABLE: "
                    + e.getClass().getSimpleName());
        } finally {
            lab12AudioBusy.set(false);
            runOnUiThread(this::enableSingleExportButton);
        }
    }, "GEL-Lab1-AudioEvidence").start();
}

// ============================================================
// LAB 2 — MULTI-TONE CORRELATED AUDIO TEST (NOT CALIBRATED SWEEP)
// ============================================================
private void lab2SpeakerSweep() {
    if (!guardMic(this::lab2SpeakerSweep)) return;
    if (lab3GuidedPending.get() || lab4GuidedPending.get()) {
        logWarn(AppLang.isGreek(this)
                ? "Άλλος έλεγχος ήχου σε εξέλιξη· περίμενε να ολοκληρωθεί."
                : "Another guided audio test is active; finish it first.");
        return;
    }
    if (!lab12AudioBusy.compareAndSet(false, true)) {
        logWarn(AppLang.isGreek(this)
                ? "Άλλη ακουστική δοκιμή εκτελείται· το αποτέλεσμα παραμένει μη επαληθευμένο."
                : "Another acoustic challenge is active; no new result was collected.");
        return;
    }
    runOnUiThread(() -> {
        if (btnExport != null) {
            btnExport.setEnabled(false);
            btnExport.setAlpha(0.5f);
        }
    });

    final boolean gr = AppLang.isGreek(this);
    appendHtml("<br>");
    logLine();
    logSection(gr ? "LAB 2 — Ηχείο: πολυσυχνοτικός συγχρονισμένος έλεγχος"
                  : "LAB 2 — Speaker: synchronized multi-tone check");
    logLine();

    new Thread(() -> {
        try {
        AcousticStimulusProbe.Result result = AcousticStimulusProbe.run(this, true);
        logLabelValue(gr ? "Σύγχρονοι τόνοι" : "Timed tone responses",
                result.observedTones + " / " + result.testedTones + " (4 requested)");
        AcousticLab2Outcome.Status outcome = AcousticLab2Outcome.classify(
                result.attempted, result.responseObserved, result.testedTones);
        switch (outcome) {
            case OBSERVED:
                logLabelOkValue(gr ? "Ακουστικός συσχετισμός" : "Acoustic correlation",
                        gr ? "Ανιχνεύθηκαν και τα 4 ζεύγη DTMF κατά την αναπαραγωγή"
                           : "Both DTMF frequencies observed in all 4 synchronized challenges");
                break;
            case INCONCLUSIVE:
                logLabelWarnValue(gr ? "Ακουστικός συσχετισμός" : "Acoustic correlation",
                        gr ? "ΜΗ ΕΠΙΒΕΒΑΙΩΜΕΝΟΣ — 4/4 δείγματα, χωρίς πλήρη συσχέτιση"
                           : "INCONCLUSIVE — 4/4 samples, without full correlation");
                break;
            case PARTIAL:
                logLabelWarnValue(gr ? "Κατάσταση Lab 2" : "Lab 2 status",
                        gr ? "ΜΕΡΙΚΗ ΔΕΙΓΜΑΤΟΛΗΨΙΑ — δεν ολοκληρώθηκαν και οι 4 τόνοι"
                           : "PARTIAL — all 4 tone captures were not completed");
                break;
            case UNAVAILABLE:
                logLabelWarnValue(gr ? "Κατάσταση Lab 2" : "Lab 2 status",
                        gr ? "ΜΗ ΔΙΑΘΕΣΙΜΟ — δεν λήφθηκε έγκυρο δείγμα τόνου"
                           : "UNAVAILABLE — no valid tone sample was captured");
                break;
        }
        if (result.testedTones > 0) {
            logLabelValue(gr ? "Ελάχιστη φασματική μεταβολή" : "Minimum spectral rise",
                    String.format(Locale.US, "%.1f / %.1f dB",
                            result.weakestLowRiseDb, result.weakestHighRiseDb));
        }
        if (result.testedTones > 0) {
            // Current synchronized microphone capture, never a calibrated dB SPL value.
            // Probe records RMS from the LAST completed tone and peak over ALL completed tones.
            if (Double.isFinite(result.ambientMicRms)) {
                logLabelValue(gr ? "RMS μικροφώνου — πριν τον τελευταίο τόνο"
                                 : "Mic RMS — before last sampled tone",
                        String.format(Locale.US, "%.1f (PCM)", result.ambientMicRms));
            }
            if (Double.isFinite(result.challengeMicRms)) {
                logLabelValue(gr ? "RMS μικροφώνου — κατά τον τελευταίο τόνο"
                                 : "Mic RMS — during last sampled tone",
                        String.format(Locale.US, "%.1f (PCM)", result.challengeMicRms));
            }
            if (Double.isFinite(result.challengeMicPeak)) {
                logLabelValue(gr ? "Μέγιστο Peak μικροφώνου — ολοκληρωμένοι τόνοι"
                                 : "Mic peak — across captured tones",
                        String.format(Locale.US, "%.0f (PCM)", result.challengeMicPeak));
            }
            if (result.clipped) {
                logLabelWarnValue(gr ? "Ποιότητα δείγματος" : "Capture integrity",
                        gr ? "Clipping — επανάλαβε σε χαμηλότερη ένταση"
                           : "Clipping — repeat at lower volume");
            } else {
                logLabelValue(gr ? "Ποιότητα δείγματος" : "Capture integrity",
                        gr ? "Δεν ανιχνεύθηκε clipping στα ληφθέντα δείγματα"
                           : "No clipping detected in captured samples");
            }
        }
        logLabelValue(gr ? "Διαδρομή εισόδου" : "Mic input route", result.inputRoute);
        logLabelValue(gr ? "Κατάσταση δείγματος" : "Sample status", result.reason);
        logLabelWarnValue(gr ? "Περιορισμός" : "Limitations",
                gr ? "Οι 4 τόνοι DTMF δεν αποτελούν σαρωτή πλήρους ακουστικού φάσματος, "
                        + "ούτε αποδεικνύουν την ποιότητα ή την προέλευση του ηχείου."
                   : "Four DTMF challenges are not a calibrated frequency-response sweep "
                        + "and cannot identify an individual speaker or certify fidelity.");
        appendHtml("<br>");
        if (outcome != AcousticLab2Outcome.Status.OBSERVED) {
            logLabelWarnValue(gr ? "Προτεινόμενη ενέργεια" : "Recommended action",
                    gr ? "Έλεγξε ένταση, άδεια μικροφώνου και συνδέσεις Bluetooth/USB/ακουστικών. "
                            + "Επανάλαβε σε ήσυχο περιβάλλον. Δεν τεκμηριώνεται βλάβη ηχείου."
                       : "Check media volume, microphone permission and Bluetooth/USB/headphone connections. "
                            + "Retry in a quiet room. This does not establish a speaker defect.");
        }
        if (outcome == AcousticLab2Outcome.Status.OBSERVED
                || outcome == AcousticLab2Outcome.Status.INCONCLUSIVE) {
            logLabelValue(gr ? "Ολοκλήρωση δειγματοληψίας" : "Sampling completion",
                    gr ? "Ολοκληρώθηκαν 4/4 συγχρονισμένοι τόνοι — χωρίς πιστοποίηση hardware"
                       : "All 4 synchronized tones captured — hardware not certified");
        } else {
            logLabelWarnValue(gr ? "Ολοκλήρωση δειγματοληψίας" : "Sampling completion",
                    gr ? "Η ακουστική δοκιμή δεν ολοκληρώθηκε· δεν υπάρχει τελική διάγνωση"
                       : "Acoustic challenge incomplete; no final diagnostic verdict");
        }
        logLine();
        } catch (RuntimeException e) {
            logLabelWarnValue("Acoustic probe", "UNAVAILABLE: "
                    + e.getClass().getSimpleName());
        } finally {
            lab12AudioBusy.set(false);
            runOnUiThread(this::enableSingleExportButton);
        }
    }, "GEL-Lab2-AudioEvidence").start();
}

/* ============================================================
   LAB 3 — Earpiece Audio Path Check (MANUAL)
   Custom GEL Dialog — START → tones → confirmation
   ============================================================ */
private void lab3EarpieceManual() {

    final boolean gr = AppLang.isGreek(this);
    if (lab12AudioBusy.get() || lab4GuidedPending.get()) {
        logWarn(gr ? "Άλλη ακουστική δοκιμή είναι ενεργή."
                   : "Another audio challenge is in progress.");
        return;
    }
    if (!lab3GuidedPending.compareAndSet(false, true)) {
        logWarn(gr ? "Έλεγχος ακουστικού σε εξέλιξη."
                   : "Earpiece challenge is already active.");
        return;
    }
    lab3RoutedOutputType = -1;
    lab3Cancelled = false;
    runOnUiThread(() -> {
        if (btnExport != null) {
            btnExport.setEnabled(false);
            btnExport.setAlpha(0.5f);
        }
    });

    appendHtml("<br>");
    logLine();
    logSection(
            gr
                    ? "LAB 3 — Έλεγχος Διαδρομής Ήχου Ακουστικού"
                    : "LAB 3 — Earpiece Audio Path Check"
    );
    logLine();

    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    if (am == null) {
        logError(
                gr
                        ? "Ο AudioManager δεν είναι διαθέσιμος."
                        : "AudioManager is unavailable."
        );
        lab3GuidedPending.set(false);
        enableSingleExportButton();
        return;
    }

    // ------------------------------------------------------------
    // SAVE AUDIO STATE
    // ------------------------------------------------------------
    lab3OldMode = am.getMode();
    lab3OldSpeaker = am.isSpeakerphoneOn();
    lab3OldMicMute = am.isMicrophoneMute();

    logInfo(
            gr
                    ? "Αποθήκευση τρέχουσας κατάστασης ήχου."
                    : "Saving audio state."
    );

    logInfo(
            gr
                    ? "Προετοιμασία δρομολόγησης προς το ακουστικό."
                    : "Preparing earpiece routing."
    );

    try {
        am.stopBluetoothSco();
        am.setBluetoothScoOn(false);
        am.setSpeakerphoneOn(false);
        am.setMicrophoneMute(false); // 🔴 ΑΠΑΡΑΙΤΗΤΟ
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);

    } catch (Throwable t) {

        logError(
                gr
                        ? "Αποτυχία δρομολόγησης ήχου."
                        : "Audio routing failed."
        );

        restoreLab3Audio(); // restore on failed setup
        lab3GuidedPending.set(false);
        enableSingleExportButton();
        return;
    }

    SystemClock.sleep(250);

    runOnUiThread(() -> {

        final String titleText = gr
                ? "LAB 3 — Έλεγχος ακουστικού"
                : "LAB 3 — Earpiece Audio Test";

        final String bodyText = gr
                ? "Τοποθέτησε το ακουστικό του τηλεφώνου στο αυτί σου.\n"
                  + "Πάτησε έναρξη για να ξεκινήσει ο έλεγχος."
                : "Put the phone earpiece to your ear.\n"
                  + "Press start to begin the test.";

        final String ttsText = bodyText; // ΜΙΑ πηγή αλήθειας

        // ------------------------------------------------------------
        // DIALOG
        // ------------------------------------------------------------
        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(26), dp(24), dp(26), dp(22));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(3), 0xFFFFD700);
        root.setBackground(bg);

        // TITLE
        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextColor(Color.WHITE);
        title.setTextSize(17f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(14));
        root.addView(title);

        // MESSAGE
        TextView msg = new TextView(this);
        msg.setText(bodyText);
        msg.setTextColor(0xFF39FF14);
        msg.setTextSize(14.5f);
        msg.setGravity(Gravity.CENTER);
        msg.setLineSpacing(1.1f, 1.15f);
        msg.setPadding(0, 0, 0, dp(18));
        root.addView(msg);
        
// ---------------------------
// MUTE ROW
// ---------------------------
root.addView(buildMuteRow());

// ---------------------------
// BUTTON ROW (EXIT + START)
// ---------------------------
LinearLayout btnRow = new LinearLayout(this);
btnRow.setOrientation(LinearLayout.HORIZONTAL);
btnRow.setGravity(Gravity.CENTER);

LinearLayout.LayoutParams btnLp =
        new LinearLayout.LayoutParams(
                0,
                dp(48),
                1f
        );
btnLp.setMargins(dp(6), dp(6), dp(6), dp(6));

// ---------- EXIT ----------
Button exitBtn = new com.gel.cleaner.GelButton(this);
exitBtn.setText(gr ? "ΕΞΟΔΟΣ" : "EXIT");
exitBtn.setAllCaps(false);
exitBtn.setTextSize(14f);
exitBtn.setTextColor(Color.WHITE);

GradientDrawable exitBg = new GradientDrawable();
exitBg.setColor(0xFF8B0000);
exitBg.setCornerRadius(dp(10));
exitBg.setStroke(dp(3), 0xFFFFD700);
exitBtn.setBackground(exitBg);
exitBtn.setLayoutParams(btnLp);

// ---------- START ----------
Button start = new com.gel.cleaner.GelButton(this);
start.setText(gr ? "ΕΝΑΡΞΗ" : "START");
start.setAllCaps(false);
start.setTextSize(14f);
start.setTextColor(Color.BLACK);

GradientDrawable startBg = new GradientDrawable();
startBg.setColor(0xFF39FF14);
startBg.setCornerRadius(dp(10));
startBg.setStroke(dp(3), 0xFFFFD700);
start.setBackground(startBg);
start.setLayoutParams(btnLp);

btnRow.addView(exitBtn);
btnRow.addView(start);
root.addView(btnRow);

b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();
lab3ActiveDialog = d;

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// Dismissing before START is a cancellation; restore the original audio mode.
final AtomicBoolean lab3Started = new AtomicBoolean(false);
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    if (!lab3Started.get() && lab3GuidedPending.get()) {
        restoreLab3Audio();
        lab3GuidedPending.set(false);
        enableSingleExportButton();
    }
});

// BACK
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        appendHtml("<br>");
        logWarn(gr ? "Η δοκιμή ακυρώθηκε από τον χρήστη."
                   : "Test canceled by user.");
        logLine();

        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(d);

    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        if (d.isShowing()
                && !isFinishing()
                && !isDestroyed()
                && !AppTTS.isMuted(this)) {

            AppTTS.ensureSpeak(this, bodyText);
        }
    }, 500);
}

// ---------------------------
// ACTIONS
// ---------------------------

exitBtn.setOnClickListener(v -> {

    appendHtml("<br>");
    logWarn(gr ? "Η δοκιμή ακυρώθηκε από τον χρήστη."
               : "Test canceled by user.");
    logLine();

    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();

    runOnUiThread(this::enableSingleExportButton);
});

// ------------------------------------------------------------
// START ACTION: one run only, no premature audio restoration.
// ------------------------------------------------------------
start.setOnClickListener(v -> {
    if (lab3Cancelled || !lab3GuidedPending.get()) return;
    if (!lab3Started.compareAndSet(false, true)) return;
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();

    new Thread(() -> {
        int acceptedPlaybackRequests = 0;
        try {

            logInfo(gr
                    ? "Αναπαραγωγή δοκιμαστικών τόνων ακουστικού."
                    : "Playing earpiece test tones.");

            for (int i = 1; i <= 3; i++) {
                if (lab3Cancelled || !lab3GuidedPending.get())
                    throw new IllegalStateException("Lab 3 canceled by lifecycle");
                logInfo(gr
                        ? "Τόνος " + i + " / 3"
                        : "Tone " + i + " / 3");

                if (!playEarpieceBeep()) {
                    throw new IllegalStateException("Android playback request rejected");
                }
                acceptedPlaybackRequests++;
                SystemClock.sleep(650);
            }

            logInfo(gr
                    ? "Το Android δέχτηκε τρεις αναπαραγωγές· η ακουστική έξοδος δεν επιβεβαιώθηκε αυτόματα."
                    : "Android accepted three playback requests; audibility was not automatically verified.");

        } catch (Throwable t) {

            logError(gr
                    ? "Αποτυχία αναπαραγωγής τόνων ακουστικού."
                    : "Earpiece tone playback failed.");

            logLabelWarnValue(
                    gr ? "Πιθανή αιτία" : "Possible cause",
                    gr
                            ? "Αποτυχία δρομολόγησης ήχου, περιορισμός συστήματος ή μη διαθέσιμη έξοδος ακουστικού."
                            : "Audio routing failure, system-level restriction or unavailable earpiece output."
            );

        } finally {

            // onPause already restored audio when the session was cancelled.
            // A late worker must never override another app's route again.
            if (!lab3Cancelled && lab3GuidedPending.get()) {
                resetAudioAfterLab3(am, lab3OldMode, lab3OldSpeaker, lab3OldMicMute);
            }

            final boolean allRequestsAccepted = acceptedPlaybackRequests == 3;
            final int observedRoute = lab3RoutedOutputType;
            runOnUiThread(() -> {
                logLabelValue(gr ? "Διαδρομή εξόδου Android" : "Android output routing",
                        observedRoute < 0 ? (gr ? "Άγνωστη" : "Unknown")
                        : observedRoute == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE
                        ? "Android TYPE_BUILTIN_EARPIECE"
                        : "Android device type " + observedRoute + " — not verified earpiece");
                if (isFinishing() || isDestroyed() || lab3Cancelled
                        || !lab3GuidedPending.get()) {
                    lab3GuidedPending.set(false);
                    return;
                }
                if (allRequestsAccepted) {
                    askUserEarpieceConfirmation(true);
                } else {
                    logLabelWarnValue(gr ? "LAB 3 — Αποτέλεσμα" : "LAB 3 — Result",
                            gr ? "Μη επαληθευμένο: αποτυχία αναπαραγωγής ή διαδρομής ήχου"
                               : "UNAVAILABLE: playback failed; no earpiece hardware verdict");
                    lab3GuidedPending.set(false);
                    enableSingleExportButton();
                }
            });
        }

    }).start();
});

// ------------------------------------------------------------
// TTS INTRO — DIALOG BOUND (GLOBAL MUTE SAFE)
// ------------------------------------------------------------
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (d.isShowing() && !AppTTS.isMuted(this)) {
        AppTTS.ensureSpeak(this, ttsText);
    }
}, 120);

});   // <-- ΚΛΕΙΝΕΙ ΤΟ runOnUiThread
}   

/* ============================================================
   LAB 4 — Microphone Recording Check (BOTTOM + TOP)
   BASE — FINAL • CLEAN • ISOLATED
   ============================================================ */

/**
 * A guided acoustic check spans multiple workers and UI callbacks. Restore the
 * saved OS audio settings only at the terminal result, cancel or unavailable.
 */
private void finishLab4Guided() {
    if (!lab4GuidedPending.getAndSet(false)) return;
    lab4Cancelled.set(true);
    if (lab4AudioStateSaved) {
        lab4AudioStateSaved = false;
        try {
            AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (am != null) {
                try { am.setSpeakerphoneOn(lab4OriginalSpeaker); }
                catch (RuntimeException ignored) {}
                try { am.setMicrophoneMute(lab4OriginalMute); }
                catch (RuntimeException ignored) {}
                try { am.setMode(lab4OriginalMode); }
                catch (RuntimeException ignored) {}
            }
        } catch (RuntimeException ignored) {}
    }
    enableSingleExportButton();
}

private void lab4MicManual() {
    if (!guardMic(this::lab4MicManual)) return;
    if (lab12AudioBusy.get() || lab3GuidedPending.get()) {
        logWarn(AppLang.isGreek(this) ? "Άλλη δοκιμή ήχου είναι ενεργή."
                                     : "Another audio test is already active.");
        return;
    }
    if (!lab4GuidedPending.compareAndSet(false, true)) {
        logWarn(AppLang.isGreek(this) ? "Έλεγχος μικροφώνου ήδη σε εξέλιξη."
                                     : "Microphone check already in progress.");
        return;
    }
    runOnUiThread(() -> {
        if (btnExport != null) {
            btnExport.setEnabled(false);
            btnExport.setAlpha(0.5f);
        }
    });
    // Never commandeer an active call or another app's communication route.
    AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
    if (am == null || am.getMode() != AudioManager.MODE_NORMAL) {
        logLabelWarnValue(AppLang.isGreek(this) ? "Διαδρομή ήχου" : "Audio state",
                AppLang.isGreek(this)
                        ? "Μη διαθέσιμο: ο ήχος χρησιμοποιείται ή δεν διατίθεται. Επανάλαβε μετά την κλήση."
                        : "UNAVAILABLE: audio in use or unavailable; retry after the call.");
        finishLab4Guided();
        return;
    }
    lab4Cancelled.set(false);
    lab4OriginalMode = am.getMode();
    lab4OriginalSpeaker = am.isSpeakerphoneOn();
    lab4OriginalMute = am.isMicrophoneMute();
    lab4AudioStateSaved = true;
    lab4MicBase(this::lab4MicPro);
}

private void lab4MicBase(Runnable onFinished) {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logSection(
            gr
                    ? "LAB 4 — Προφίλ μικροφώνου και ακουστική δοκιμή"
                    : "LAB 4 — Recording profiles and guided audio test"
    );
    logLine();

    new Thread(() -> {

        boolean bottomOk = false;
        boolean topOk = false;
        boolean baseCompleted = false;

        int bottomRms = 0, bottomPeak = 0;
        int topRms = 0, topPeak = 0;
        int bottomInputRoute = -1;
        int topInputRoute = -1;

        try {
            if (!lab4GuidedPending.get() || lab4Cancelled.get()) {
                return; // Previous session was cancelled before collection began.
            }

            // ====================================================
            // AUTO CHECK — BOTTOM MIC
            // ====================================================
            appendHtml("<br>");
            logInfo(gr ? "Καταγραφή Android MIC profile:"
                       : "Android MIC source-profile capture:");
            logLine();

            hardNormalizeAudioForMic();
            MicDiagnosticEngine.Result bottom =
                    MicDiagnosticEngine.run(this, MicDiagnosticEngine.MicType.BOTTOM);
            if (!lab4GuidedPending.get() || lab4Cancelled.get()) return;

            if (bottom != null) {
                bottomRms  = (int) bottom.rms;
                bottomPeak = (int) bottom.peak;
                bottomInputRoute = bottom.routedInputDeviceId;
                bottomOk = bottom.audioRecordStarted && !bottom.silenceDetected;
            }

            logLabelValue("MIC profile RMS",  String.valueOf(bottomRms));
            logLabelValue("MIC profile Peak", String.valueOf(bottomPeak));

            logLabelValue(gr ? "Android διαδρομή MIC profile" : "Android MIC input route",
                    bottomInputRoute >= 0 ? "route id " + bottomInputRoute
                            : (gr ? "Μη διαθέσιμη" : "Unavailable"));

            // ====================================================
            // AUTO CHECK — TOP MIC
            // ====================================================
            appendHtml("<br>");
            logInfo(gr ? "Καταγραφή Android CAMCORDER profile:"
                       : "Android CAMCORDER source-profile capture:");
            logLine();

            hardNormalizeAudioForMic();
            MicDiagnosticEngine.Result top =
                    MicDiagnosticEngine.run(this, MicDiagnosticEngine.MicType.TOP);
            if (!lab4GuidedPending.get() || lab4Cancelled.get()) return;

            if (top != null) {
                topRms  = (int) top.rms;
                topPeak = (int) top.peak;
                topInputRoute = top.routedInputDeviceId;
                topOk = top.audioRecordStarted && !top.silenceDetected;
            }

            logLabelValue("CAMCORDER profile RMS", String.valueOf(topRms));
            logLabelValue("CAMCORDER profile Peak", String.valueOf(topPeak));

            logLabelValue(gr ? "Android διαδρομή CAMCORDER profile"
                             : "Android CAMCORDER input route",
                    topInputRoute >= 0 ? "route id " + topInputRoute
                            : (gr ? "Μη διαθέσιμη" : "Unavailable"));

// ====================================================
// FINAL BASE VERDICT — SOURCE PROFILE, NOT MICROPHONE IDENTITY
// ====================================================
logLabelWarnValue(gr ? "Αξιοπιστία ταυτοποίησης" : "Physical input identification",
        gr ? "Το AudioSource.CAMCORDER δεν εγγυάται το πάνω μικρόφωνο. "
                 + "Η ίδια φυσική είσοδος μπορεί να εξυπηρετεί και τα δύο προφίλ."
           : "CAMCORDER is not guaranteed to use the top microphone. "
                 + "Both profiles may route to the same physical input.");
if (bottomInputRoute >= 0 && bottomInputRoute == topInputRoute) {
    logLabelValue(gr ? "Συσχέτιση διαδρομών" : "Route correlation",
            gr ? "Το Android επέστρεψε το ίδιο device ID και στις δύο λήψεις."
               : "Android returned the same input device ID for both recordings.");
}
// ====================================================

// ====================================================
appendHtml("<br>");
logInfo(gr ? "Καταγραφή προφίλ εισόδου (όχι πιστοποίηση φυσικών μικροφώνων):"
           : "Input source observations (not per-microphone hardware certification):");
logLine();

if (bottomOk && topOk) {

    logLabelOkValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Ελήφθη ήχος και στα δύο Android recording profiles· "
                      + "δεν ταυτοποιήθηκαν δύο φυσικά μικρόφωνα"
               : "Audio captured under both Android recording profiles; "
                      + "two separate hardware microphones not established"
    );

} else if (bottomOk || topOk) {

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Ήχος καταγράφηκε μόνο με ένα προφίλ· όχι απόδειξη βλάβης άλλου μικροφώνου"
               : "Only one input profile captured audio; not proof of a failed second mic"
    );

} else {

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr
                    ? "Κανένα προφίλ δεν έδωσε επαρκές δείγμα στις τρέχουσες συνθήκες."
                    : "Neither source profile yielded sufficient audio in this environment."
    );

    logWarn(
            gr
                    ? "Πιθανός περιορισμός firmware ή δικαιωμάτων."
                    : "Possible firmware or permission restriction."
    );

    logOk(
            gr
                    ? "Συνιστάται έλεγχος μέσω πραγματικής κλήσης."
                    : "Testing via a real call is recommended."
    );
}
            // Collection flow reached a terminal diagnostic statement, even
            // when measurements were inconclusive. Failure/exception is different.
            baseCompleted = true;

        } catch (RuntimeException e) {
            logLabelWarnValue(gr ? "Καταγραφή Lab 4" : "Lab 4 capture",
                    (gr ? "Μη διαθέσιμη / διακοπή: " : "UNAVAILABLE / interrupted: ")
                            + e.getClass().getSimpleName());
        } finally {
            appendHtml("<br>");
            logInfo(gr ? "Το Lab 4 BASE τερματίστηκε." : "Lab 4 BASE ended.");
            logLine();

            if (baseCompleted && lab4GuidedPending.get()
                    && !lab4Cancelled.get() && onFinished != null) {
                runOnUiThread(onFinished);
            } else {
                finishLab4Guided();
            }
        }

    }).start();
}

/* ============================================================
   LAB 4 PRO — CALL QUALITY VERIFICATION (FINAL • LOCKED)
   ============================================================ */

private volatile boolean lastAnswerHeardClearly = false;

private void lab4MicPro() {
    if (!lab4GuidedPending.get() || lab4Cancelled.get()) return;
    final boolean gr = AppLang.isGreek(this);

    new Thread(() -> {
    final AtomicBoolean handedToStage2 = new AtomicBoolean(false);
    try {

// ====================================================
// STAGE 1 — Guided microphone audio path observation
// ====================================================

// 🔊 Force call audio path
AudioManager amCall = (AudioManager) getSystemService(AUDIO_SERVICE);
if (amCall != null) {
    try { amCall.stopBluetoothSco(); } catch (Throwable ignore) {}
    try { amCall.setBluetoothScoOn(false); } catch (Throwable ignore) {}
    try { amCall.setSpeakerphoneOn(false); } catch (Throwable ignore) {}
    try { amCall.setMicrophoneMute(false); } catch (Throwable ignore) {}
    try { amCall.setMode(AudioManager.MODE_IN_COMMUNICATION); } catch (Throwable ignore) {}
}

AtomicBoolean cancelled = lab4Cancelled;
AtomicBoolean started = new AtomicBoolean(false);
AtomicReference<AlertDialog> dialogRef = new AtomicReference<>();

// ==========================
// POPUP 1 — INSTRUCTION
// ==========================
runOnUiThread(() -> {

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(26), dp(24), dp(26), dp(22));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    TextView msg = new TextView(this);
    msg.setText(gr
            ? "Μίλησε στο κάτω μικρόφωνο, και άκου, αν η φωνή σου ακούγεται καθαρά, από το ακουστικό."
            : "Speak into the bottom microphone, and check, if your voice is clearly heard, from the earpiece.");
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    msg.setPadding(0, 0, 0, dp(18));
    root.addView(msg);

    root.addView(buildMuteRow());

    LinearLayout btnRow = new LinearLayout(this);
    btnRow.setOrientation(LinearLayout.HORIZONTAL);
    btnRow.setGravity(Gravity.CENTER);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
    lp.setMargins(dp(12), dp(8), dp(12), dp(8));

    Button exitBtn = new com.gel.cleaner.GelButton(this);
    exitBtn.setText("EXIT");
    exitBtn.setTextColor(Color.WHITE);
    exitBtn.setAllCaps(false);

    GradientDrawable exitBg = new GradientDrawable();
    exitBg.setColor(0xFF8B0000);
    exitBg.setCornerRadius(dp(10));
    exitBg.setStroke(dp(3), 0xFFFFD700);
    exitBtn.setBackground(exitBg);
    exitBtn.setLayoutParams(lp);

    Button startBtn = new com.gel.cleaner.GelButton(this);
    startBtn.setText("START");
    startBtn.setTextColor(Color.WHITE);
    startBtn.setAllCaps(false);

    GradientDrawable startBg = new GradientDrawable();
    startBg.setColor(0xFF0B5F3B);
    startBg.setCornerRadius(dp(10));
    startBg.setStroke(dp(3), 0xFFFFD700);
    startBtn.setBackground(startBg);
    startBtn.setLayoutParams(lp);

    btnRow.addView(exitBtn);
    btnRow.addView(startBtn);
    root.addView(btnRow);

    b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();
    lab4ActiveDialog = d;

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// STOP TTS on ANY dismiss
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// BACK key protection
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        cancelled.set(true);
        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

// IMPORTANT (μόνο στο LAB4PRO που έχει dialogRef)
dialogRef.set(d);

exitBtn.setOnClickListener(v -> {
    cancelled.set(true);
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();
});

startBtn.setOnClickListener(v -> {
    started.set(true);
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(d);
}

    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        AppTTS.ensureSpeak(
                this,
                gr
                        ? "Μίλησε στο κάτω μικρόφωνο, και άκου, αν η φωνή σου ακούγεται καθαρά, από το ακουστικό."
            : "Speak into the bottom microphone, and check, if your voice is clearly heard, from the earpiece.");
            
    }, 500);
});

// ==========================
// WAIT FOR START
// ==========================
long waitStart = SystemClock.uptimeMillis() + 10000;
while (!started.get() && !cancelled.get()
        && SystemClock.uptimeMillis() < waitStart) {
    SystemClock.sleep(80);
}

if (cancelled.get() || !started.get()) {
    finishLab4Guided();
    appendHtml("<br>");

    logWarn(gr
            ? "LAB 4 PRO — Ακυρώθηκε ή δεν ξεκίνησε έγκαιρα"
            : "LAB 4 PRO — Cancelled or start not confirmed");
    logLine();

    runOnUiThread(this::enableSingleExportButton);
    return;
}

// ==========================
// LIVE AUDIO PATH PROBE: captured and written bytes, no physical mic identity.
// ==========================
VoiceLoopEvidenceProbe.Result loopEvidence =
        VoiceLoopEvidenceProbe.run(cancelled);
logLabelValue(gr ? "Δείγματα ζωντανής διαδρομής" : "Voice-loop sample evidence",
        loopEvidence.bytesCaptured + " captured bytes / "
                + loopEvidence.bytesWritten + " written bytes; "
                + loopEvidence.elapsedMs + " ms");
logLabelValue(gr ? "Πραγματική διαδρομή Android" : "Actual Android routed devices",
        "input type " + (loopEvidence.routedInputType < 0
                ? "UNKNOWN" : loopEvidence.routedInputType)
                + " / output type " + (loopEvidence.routedOutputType < 0
                ? "UNKNOWN" : loopEvidence.routedOutputType)
                + " (not physical-health proof)");
if (!loopEvidence.attempted || cancelled.get()) {
    finishLab4Guided();
    logLabelWarnValue(gr ? "Αποτέλεσμα διαδρομής" : "Audio path result",
            gr ? "ΜΗ ΕΠΑΛΗΘΕΥΜΕΝΟ — δεν εκτελέστηκε επαρκής ακουστική επανάληψη: "
                    + loopEvidence.reason
               : "UNAVAILABLE — no sufficiently observed voice loop: "
                    + loopEvidence.reason);
    runOnUiThread(this::enableSingleExportButton);
    return; // Never offer a 'YES' confirmation when no valid loop ran.
}

// ==========================
// POPUP 2 — CONFIRMATION (SAFE VERSION)
// ==========================
AtomicBoolean heardClearly = new AtomicBoolean(false);
AtomicBoolean answered = new AtomicBoolean(false);

runOnUiThread(() -> {

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(26), dp(24), dp(26), dp(22));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    TextView msg = new TextView(this);
    msg.setText(gr
            ? "Άκουσες καθαρά τη φωνή σου από το ακουστικό;"
            : "Did you hear your voice clearly from the earpiece?");
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    msg.setPadding(0, 0, 0, dp(18));
    root.addView(msg);

    // ---------- BUTTON ROW ----------
    LinearLayout btnRow = new LinearLayout(this);
    btnRow.setOrientation(LinearLayout.HORIZONTAL);
    btnRow.setGravity(Gravity.CENTER);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
    lp.setMargins(dp(12), dp(8), dp(12), dp(8));

    Button noBtn = new com.gel.cleaner.GelButton(this);
    noBtn.setText(gr ? "ΟΧΙ" : "NO");
    noBtn.setAllCaps(false);
    noBtn.setTextColor(Color.WHITE);

    GradientDrawable noBg = new GradientDrawable();
    noBg.setColor(0xFF8B0000);
    noBg.setCornerRadius(dp(10));
    noBg.setStroke(dp(3), 0xFFFFD700);
    noBtn.setBackground(noBg);
    noBtn.setLayoutParams(lp);

    Button yesBtn = new com.gel.cleaner.GelButton(this);
    yesBtn.setText(gr ? "ΝΑΙ" : "YES");
    yesBtn.setAllCaps(false);
    yesBtn.setTextColor(Color.WHITE);

    GradientDrawable yesBg = new GradientDrawable();
    yesBg.setColor(0xFF0B5F3B);
    yesBg.setCornerRadius(dp(10));
    yesBg.setStroke(dp(3), 0xFFFFD700);
    yesBtn.setBackground(yesBg);
    yesBtn.setLayoutParams(lp);

    btnRow.addView(noBtn);
    btnRow.addView(yesBtn);
    root.addView(btnRow);

    b.setView(root);

    final AlertDialog d = b.create();
    lab4ActiveDialog = d;

    if (d.getWindow() != null) {
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    // STOP TTS on any dismiss
    d.setOnDismissListener(dialog -> {
dialogRef.set(null);
        try { AppTTS.stop(); } catch (Throwable ignore) {}

        if (!answered.get()) {
            cancelled.set(true);
            answered.set(true);
        }
    });

    // BACK protection
    d.setOnKeyListener((dialog, keyCode, event) -> {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
            event.getAction() == KeyEvent.ACTION_UP) {

            cancelled.set(true);
            answered.set(true);

            try { AppTTS.stop(); } catch (Throwable ignore) {}
            dialog.dismiss();
            return true;
        }
        return false;
    });

    if (!isFinishing() && !isDestroyed()) {
        d.show();
        com.gel.cleaner.GelButtonLayout.fitDialog(d);

        // 🔊 SAFE TTS after attach
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (d.isShowing() && !AppTTS.isMuted(this)) {
                AppTTS.ensureSpeak(
                        this,
                        gr
                                ? "Άκουσες καθαρά τη φωνή σου από το ακουστικό;"
                                : "Did you hear your voice clearly from the earpiece?"
                );
            }
        }, 400);
    }

    noBtn.setOnClickListener(v -> {
        heardClearly.set(false);
        answered.set(true);
        try { AppTTS.stop(); } catch (Throwable ignore) {}
        d.dismiss();
    });

    yesBtn.setOnClickListener(v -> {
        heardClearly.set(true);
        answered.set(true);
        try { AppTTS.stop(); } catch (Throwable ignore) {}
        d.dismiss();
    });
});

// ==========================
// WAIT (SAFE — NO DEADLOCK)
// ==========================
while (!answered.get() && !cancelled.get()) {
    SystemClock.sleep(80);
}

if (cancelled.get() || isFinishing() || isDestroyed()) {
    finishLab4Guided();
    runOnUiThread(this::enableSingleExportButton);
    return;
}

// -----------------------------------------
// UI STABILIZATION BEFORE STAGE 2
// -----------------------------------------
try { AppTTS.stop(); } catch (Throwable ignore) {}

SystemClock.sleep(350);   // αφήνουμε το UI να "κάτσει"

// ====================================================
// RESULT LOGGING (USER CONFIRMATION BASED)
// ====================================================
appendHtml("<br>");
logInfo(gr
        ? "LAB 4 PRO — Ακουστική επανάληψη διαδρομής Android (επιβεβαίωση χρήστη)"
        : "LAB 4 PRO — Android voice path loopback (user-reported)");
logLine();

if (AudioChallengeEvidence.microphoneLoop(loopEvidence.attempted, heardClearly.get())
        == AudioChallengeEvidence.Result.USER_REPORTED_RESPONSE) {

    logLabelOkValue(
            gr ? "Ακουστική επανάληψη (δήλωση χρήστη)" : "Voice loop (user report)",
            gr
                    ? "USER_REPORTED: ακούστηκε η φωνή στη διαδρομή Android· "
                      + "δεν πιστοποιείται το κάτω μικρόφωνο, ακουστικό ή πραγματική κλήση."
                    : "USER_REPORTED: loop heard through Android audio path; "
                      + "no physical bottom mic, earpiece or carrier-call certification."
    );

    logLabelWarnValue(
            gr ? "Όρια επαλήθευσης" : "Verification limits",
            gr
                    ? "Οι φυσικές είσοδοι, η δρομολόγηση OEM και η ποιότητα κλήσης παραμένουν μη επαληθευμένες."
                    : "Physical inputs, OEM routing and actual call quality remain unverified."
    );

} else {

    logLabelWarnValue(
        gr ? "Αποτέλεσμα" : "Result",
        gr
                ? "Η ακουστική επανάληψη, δεν επιβεβαιώθηκε από τον χρήστη."
                : "Acoustic loop, was not confirmed by the user."
);

logWarn(
        gr
                ? "Σε ορισμένες συσκευές, η συμπεριφορά μπορεί να επηρεάζεται, από τις ρυθμίσεις ήχου, "
                  + "περιορισμούς firmware, ή αυτόματη καταστολή ηχούς."
                : "On some devices, behavior may be influenced, by audio settings, "
                  + "firmware restrictions, or echo cancellation mechanisms."
);

logOk(
        gr
                ? "Συνιστάται επιβεβαίωση μέσω πραγματικής τηλεφωνικής κλήσης."
                : "Verification via a real phone call is recommended."
);
}

logLine();

// ====================================================
// STAGE 2 — USER INSTRUCTION (SPEAKER)
// ====================================================

// ?? Πάμε καθαρά σε speaker για οδηγία
try {
    if (amCall != null) {
        try { amCall.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
        try { amCall.setSpeakerphoneOn(true); } catch (Throwable ignore) {}
    }
} catch (Throwable ignore) {}

handedToStage2.set(true);

// ==========================
// SHOW DIALOG (UI THREAD)
// ==========================
runOnUiThread(() -> {
    if (lab4Cancelled.get() || !lab4GuidedPending.get()) return;

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(26), dp(24), dp(26), dp(22));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    TextView msg = new TextView(this);
    msg.setText(gr
            ? "Βάλε το ακουστικό στο αυτί σου."
            : "Place the earpiece on your ear.");
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    root.addView(msg);

    b.setView(root);

    final AlertDialog d = b.create();
    lab4ActiveDialog = d;
    dialogRef.set(d);

    if (d.getWindow() != null) {
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    if (!isFinishing() && !isDestroyed()) {

        d.show();
        com.gel.cleaner.GelButtonLayout.fitDialog(d);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (!isFinishing() && !isDestroyed()
                    && !AppTTS.isMuted(this)) {

                AppTTS.ensureSpeak(
                        this,
                        gr
                                ? "Βάλε το ακουστικό στο αυτί σου."
                                : "Place the earpiece on your ear."
                );
            }

            // Περιμένουμε να ξεκινήσει
            new Thread(() -> {

                long startWait = SystemClock.uptimeMillis() + 1500;
                while (!AppTTS.isSpeaking()
                        && SystemClock.uptimeMillis() < startWait) {
                    SystemClock.sleep(50);
                }

                long maxWait = SystemClock.uptimeMillis() + 4000;
                while (AppTTS.isSpeaking()
                        && SystemClock.uptimeMillis() < maxWait) {
                    SystemClock.sleep(80);
                }

                SystemClock.sleep(250);

                runOnUiThread(() -> {
                    try {
                        if (d.isShowing()) d.dismiss();
                    } catch (Throwable ignore) {}
                });

                if (isFinishing() || isDestroyed() || lab4Cancelled.get()
                        || !lab4GuidedPending.get()) {
                    finishLab4Guided();
                    return;
                }
                routeToCallEarpiece();
                playAnswerCheckWav();

            }).start();

        }, 500);
    } else {
        finishLab4Guided();
        enableSingleExportButton();
    }
});

        } catch (Throwable t) {
            finishLab4Guided();
            runOnUiThread(this::enableSingleExportButton);

            appendHtml("<br>");

            logLabelWarnValue(
                    gr ? "Διακοπή" : "Interrupted",
                    gr
                            ? "Το LAB 4 PRO δεν ολοκληρώθηκε κανονικά."
                            : "LAB 4 PRO did not complete normally."
            );

            logLabelWarnValue(
        gr ? "Πιθανές αιτίες" : "Possible causes",
        gr
                ? "Περιορισμός δρομολόγησης, δικαιωμάτων ή Android/OEM· δεν επιβεβαιώθηκε φυσική βλάβη."
                : "Routing, permission or Android/OEM limitation; no physical failure established."
);

            appendHtml("<br>");
            logOk(gr ? "Το Lab 4 ολοκληρώθηκε." : "Lab 4 finished.");
            logLine();

} finally {
    // Do not reset audio during the asynchronous Stage 2 handoff.
    if (!handedToStage2.get()) {
        try { AppTTS.stop(); } catch (RuntimeException ignored) {}
        finishLab4Guided();
    }
}

}).start();
}
    
// ============================================================
// 🎵 PLAY VOICE WAV — AUTO LANGUAGE (EARPIECE ONLY • LOCKED)
// ============================================================
private void playAnswerCheckWav() {
    final boolean gr = AppLang.isGreek(this);
    if (isFinishing() || isDestroyed() || lab4Cancelled.get()
            || !lab4GuidedPending.get()) {
        finishLab4Guided();
        return;
    }

    AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
    if (am != null) {
        try { am.stopBluetoothSco(); } catch (RuntimeException ignored) {}
        try { am.setBluetoothScoOn(false); } catch (RuntimeException ignored) {}
        try { am.setSpeakerphoneOn(false); } catch (RuntimeException ignored) {}
        try { am.setMode(AudioManager.MODE_IN_COMMUNICATION); }
        catch (RuntimeException ignored) {}
    }

    MediaPlayer mp = null;
    boolean playbackRequested = false;
    try {
        int resId = gr ? R.raw.answercheck_el : R.raw.answercheck_en;
        mp = new MediaPlayer();
        try (AssetFileDescriptor afd = getResources().openRawResourceFd(resId)) {
            if (afd == null) throw new IllegalStateException("Sample audio unavailable");
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        }
        mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        mp.prepare();
        mp.start();
        if (!mp.isPlaying()) throw new IllegalStateException("Audio playback did not start");
        playbackRequested = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            AudioDeviceInfo routed = mp.getRoutedDevice();
            logLabelValue(gr ? "Διαδρομή δείγματος φωνής" : "Recorded voice playback route",
                    routed == null ? "UNKNOWN (Android)"
                    : routed.getType() == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE
                      ? "Android TYPE_BUILTIN_EARPIECE"
                      : "Android device type " + routed.getType()
                        + " — not verified built-in earpiece");
        } else {
            logLabelValue(gr ? "Διαδρομή δείγματος φωνής" : "Recorded voice playback route",
                    "UNAVAILABLE on API < 28; Android playback accepted only");
        }
        int durationMs = mp.getDuration();
        SystemClock.sleep(durationMs > 0 && durationMs <= 15000 ? durationMs : 1800);
    } catch (Exception ex) {
        logLabelWarnValue(gr ? "Δείγμα ακουστικού" : "Earpiece sample",
                (gr ? "Μη διαθέσιμη αναπαραγωγή: " : "Playback unavailable: ")
                        + ex.getClass().getSimpleName());
    } finally {
        if (mp != null) {
            try { mp.stop(); } catch (RuntimeException ignored) {}
            try { mp.release(); } catch (RuntimeException ignored) {}
        }
    }

    // Android playback acceptance cannot identify the actual physical output.
    if (!playbackRequested || isFinishing() || isDestroyed()
            || lab4Cancelled.get() || !lab4GuidedPending.get()) {
        finishLab4Guided();
        runOnUiThread(this::enableSingleExportButton);
        return;
    }
    showAnswerCheckConfirmation(this::lab4ResultEarpiece);
}

// ============================================================
// STAGE 4 — USER-REPORTED EARPIECE AUDIBILITY
// ============================================================
private void showAnswerCheckConfirmation(Runnable onAnswered) {
    if (lab4Cancelled.get() || !lab4GuidedPending.get()) return;
    final boolean gr = AppLang.isGreek(this);
    lastAnswerHeardClearly = false;
    final AtomicBoolean answerProvided = new AtomicBoolean(false);

    // 🔊 Speaker mode
    AudioManager am =
            (AudioManager) getSystemService(AUDIO_SERVICE);

    if (am != null) {
        try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
        try { am.setSpeakerphoneOn(true); } catch (Throwable ignore) {}
    }

    runOnUiThread(() -> {

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );

        b.setCancelable(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(26), dp(24), dp(26), dp(22));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF000000);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(3), 0xFFFFD700);
        root.setBackground(bg);

        TextView msg = new TextView(this);

        final String text =
                gr
                        ? "Άκουσες το ηχογραφημένο δείγμα ΠΡΙΝ από αυτή την ερώτηση; Μη μετρήσεις τη φωνητική οδηγία."
                        : "Did you hear the recorded sample BEFORE this question? Do not count the spoken prompt.";

        msg.setText(text);
        msg.setTextColor(0xFF39FF14);
        msg.setTextSize(15f);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, 0, 0, dp(18));

        root.addView(msg);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        lp.setMargins(dp(12), dp(8), dp(12), dp(8));

        Button noBtn = new com.gel.cleaner.GelButton(this);
        noBtn.setText(gr ? "ΟΧΙ" : "NO");
        noBtn.setAllCaps(false);
        noBtn.setTextColor(Color.WHITE);

        GradientDrawable noBg = new GradientDrawable();
        noBg.setColor(0xFF8B0000);
        noBg.setCornerRadius(dp(10));
        noBg.setStroke(dp(3), 0xFFFFD700);

        noBtn.setBackground(noBg);
        noBtn.setLayoutParams(lp);

        Button yesBtn = new com.gel.cleaner.GelButton(this);
        yesBtn.setText(gr ? "ΝΑΙ" : "YES");
        yesBtn.setAllCaps(false);
        yesBtn.setTextColor(Color.WHITE);

        GradientDrawable yesBg = new GradientDrawable();
        yesBg.setColor(0xFF0B5F3B);
        yesBg.setCornerRadius(dp(10));
        yesBg.setStroke(dp(3), 0xFFFFD700);

        yesBtn.setBackground(yesBg);
        yesBtn.setLayoutParams(lp);

        btnRow.addView(noBtn);
        btnRow.addView(yesBtn);

        root.addView(btnRow);

        b.setView(root);

        final AlertDialog d = b.create();
    lab4ActiveDialog = d;

        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        d.setOnDismissListener(dialog -> {
            try { AppTTS.stop(); } catch (Throwable ignore) {}
            if (!answerProvided.get() && lab4GuidedPending.get()) {
                finishLab4Guided();
                enableSingleExportButton();
                logLabelWarnValue(gr ? "Αποτέλεσμα ακουστικού" : "Earpiece result",
                        gr ? "ΑΚΥΡΩΘΗΚΕ — καμία απάντηση χρήστη"
                           : "CANCELLED — no user response");
            }
        });

        if (!isFinishing() && !isDestroyed()) {
            d.show();
            com.gel.cleaner.GelButtonLayout.fitDialog(d);
        } else {
            finishLab4Guided();
            enableSingleExportButton();
            return;
        }

// 🔊 TTS (safe)
new Handler(Looper.getMainLooper()).postDelayed(() -> {

    if (!d.isShowing()) return;

    AppTTS.stop();   // ← ΠΡΟΣΘΗΚΗ

    if (!AppTTS.isMuted(ManualTestsActivity.this)) {

        AppTTS.ensureSpeak(
                ManualTestsActivity.this,
                text
        );
    }

}, 300);

// NO
noBtn.setOnClickListener(v -> {

    if (!d.isShowing()) return;

    answerProvided.set(true);
    lastAnswerHeardClearly = false;

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    d.dismiss();

    if (onAnswered != null) {
        onAnswered.run();
    }
});

// YES
yesBtn.setOnClickListener(v -> {

    if (!d.isShowing()) return;

    answerProvided.set(true);
    lastAnswerHeardClearly = true;

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    d.dismiss();

    if (onAnswered != null) {
        onAnswered.run();
    }
});

});
}

private void lab4ResultEarpiece() {
    if (lab4Cancelled.get() || !lab4GuidedPending.get()) return;
    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logInfo(gr ? "LAB 4 PRO — Ποιότητα συνομιλίας ακουστικού"
               : "LAB 4 PRO — Earpiece Call quality");
    logLine();

    if (lastAnswerHeardClearly) {

        logLabelOkValue(
                gr ? "Αποτέλεσμα" : "Result",
                gr
                        ? "USER_REPORTED: ακούστηκε ήχος στη διαδρομή επικοινωνίας· η φυσική έξοδος δεν ταυτοποιήθηκε."
                        : "USER_REPORTED: audio heard on communication route; physical output unverified."
        );

    } else {

        logLabelWarnValue(
                gr ? "Αποτέλεσμα" : "Result",
                gr
                        ? "Σύμφωνα με τη δήλωση χρήστη, ο ήχος δεν ήταν καθαρός."
                        : "According to the user's declaration, the earpiece audio was not clear."
        );
    }

    logLine();

    appendHtml("<br>");
    logOk(gr ? "Το Lab 4 ολοκληρώθηκε."
             : "Lab 4 finished.");
    logLine();

    finishLab4Guided();
    runOnUiThread(this::enableSingleExportButton);
}

/* ============================================================
   LAB 5 — Vibration Motor Test
   UNIVERSAL OEM-SAFE TEST + USER CONFIRM
   ============================================================ */
private void lab5Vibration() {

    final boolean gr = AppLang.isGreek(this);
    if (!lab5VibrationPending.compareAndSet(false, true)) {
        logWarn(gr ? "Η δοκιμή δόνησης είναι ήδη σε εξέλιξη."
                   : "Vibration test is already in progress.");
        return;
    }
    runOnUiThread(() -> {
        if (btnExport != null) {
            btnExport.setEnabled(false);
            btnExport.setAlpha(0.5f);
        }
    });

    appendHtml("<br>");
    logLine();
    logSection(
            gr
                    ? "LAB 5 — Διαγνωστικός Έλεγχος Μηχανισμού Δόνησης"
                    : "LAB 5 — Vibration Motor Test"
    );
    logLine();

    final AtomicBoolean userConfirmed = new AtomicBoolean(false);
    final AtomicBoolean userResponded = new AtomicBoolean(false);

    new Thread(() -> {

        Vibrator vibrator = null;

        try {

            // =====================================================
            // UNIVERSAL VIBRATOR SOURCE
            // Android 12+ → VibratorManager
            // Older Android → legacy Vibrator service
            // =====================================================
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                VibratorManager vm =
                        (VibratorManager) getSystemService(
                                Context.VIBRATOR_MANAGER_SERVICE
                        );

                if (vm != null) {
                    vibrator = vm.getDefaultVibrator();
                }

            } else {

                vibrator =
                        (Vibrator) getSystemService(
                                Context.VIBRATOR_SERVICE
                        );
            }

            if (vibrator == null || !vibrator.hasVibrator()) {

                logLabelWarnValue(
                        gr ? "Διαθεσιμότητα Vibrator API" : "Vibrator API availability",
                        gr ? "Το Android δεν ανέφερε διαθέσιμο μοτέρ· φυσική βλάβη μη επαληθευμένη"
                           : "Android exposed no vibrator; physical hardware failure unverified"
                );
                return;
            }

            appendHtml("<br>");
            logInfo(
                    gr
                            ? "Έλεγχος ρυθμίσεων συστήματος:"
                            : "System settings check:"
            );
            logLine();

            // =====================================================
            // 1 — DO NOT DISTURB
            // =====================================================
            try {
                NotificationManager nm =
                        (NotificationManager) getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

                if (nm != null &&
                        nm.getCurrentInterruptionFilter()
                                != NotificationManager.INTERRUPTION_FILTER_ALL) {

                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr
                                    ? "Ενεργή λειτουργία Μην Ενοχλείτε."
                                    : "Do Not Disturb mode is active."
                    );
                }
            } catch (Throwable ignore) {}

            // =====================================================
            // 2 — BATTERY SAVER
            // =====================================================
            try {
                PowerManager pm =
                        (PowerManager) getSystemService(
                                Context.POWER_SERVICE
                        );

                if (pm != null && pm.isPowerSaveMode()) {

                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr
                                    ? "Ενεργή λειτουργία εξοικονόμησης ενέργειας μπαταρίας."
                                    : "Battery saver mode is active."
                    );
                }
            } catch (Throwable ignore) {}

            // =====================================================
            // 3 — SILENT MODE
            // =====================================================
            try {
                AudioManager am =
                        (AudioManager) getSystemService(
                                Context.AUDIO_SERVICE
                        );

                if (am != null &&
                        am.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {

                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr
                                    ? "Η συσκευή βρίσκεται σε αθόρυβη λειτουργία."
                                    : "Device is in Silent mode."
                    );
                }
            } catch (Throwable ignore) {}

            // =====================================================
            // 4 — HAPTIC FEEDBACK
            // Informational only: a diagnostic vibration should
            // still be attempted even when touch haptics are off.
            // =====================================================
            try {
                int haptic = Settings.System.getInt(
                        getContentResolver(),
                        Settings.System.HAPTIC_FEEDBACK_ENABLED
                );

                if (haptic == 0) {
                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr
                                    ? "Η απτική ανάδραση είναι απενεργοποιημένη."
                                    : "Haptic feedback is disabled."
                    );
                }
            } catch (Throwable ignore) {}

            // =====================================================
            // 5 — VIBRATE WHEN RINGING
            // =====================================================
            try {
                int vibrate = Settings.System.getInt(
                        getContentResolver(),
                        "vibrate_when_ringing"
                );

                if (vibrate == 0) {
                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr
                                    ? "Η δόνηση κατά την κλήση είναι απενεργοποιημένη."
                                    : "Vibrate on ring is disabled."
                    );
                }
            } catch (Throwable ignore) {}

            logLine();

            // =====================================================
            // OEM-SAFE DIAGNOSTIC VIBRATION
            // Uses sonification/alarm attributes so HyperOS and
            // other aggressive OEM firmware do not treat it as
            // a weak touch-haptic event.
            // =====================================================
            logInfo(
                    gr
                            ? "Ισχυρό διαγνωστικό μοτίβο δόνησης"
                            : "Strong diagnostic vibration pattern"
            );

            AudioAttributes vibrationAttributes =
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(
                                    AudioAttributes.CONTENT_TYPE_SONIFICATION
                            )
                            .build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                VibrationEffect diagnosticEffect;

                if (vibrator.hasAmplitudeControl()) {

                    diagnosticEffect = VibrationEffect.createWaveform(
                            new long[]{0, 700, 180, 700, 180, 1200},
                            new int[]{0, 255, 0, 255, 0, 255},
                            -1
                    );

                } else {

                    diagnosticEffect = VibrationEffect.createWaveform(
                            new long[]{0, 700, 180, 700, 180, 1200},
                            -1
                    );
                }

                vibrator.vibrate(
                        diagnosticEffect,
                        vibrationAttributes
                );

            } else {

                vibrator.vibrate(
                        new long[]{0, 700, 180, 700, 180, 1200},
                        -1
                );
            }

            SystemClock.sleep(3300);

            // =====================================================
            // SECOND FALLBACK PULSE
            // Some OEMs suppress the first pattern after service
            // wake-up but accept a direct one-shot immediately after.
            // =====================================================
            try {

                vibrator.cancel();
                SystemClock.sleep(180);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                    1300,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                            ),
                            vibrationAttributes
                    );

                } else {
                    vibrator.vibrate(1300);
                }

                SystemClock.sleep(1450);

            } catch (Throwable ignore) {}

            // =====================================================
            // USER CONFIRMATION
            // =====================================================
            final AtomicBoolean answered = new AtomicBoolean(false);

            runOnUiThread(() -> {

                AlertDialog.Builder b =
                        new AlertDialog.Builder(
                                this,
                                android.R.style.Theme_Material_Dialog_NoActionBar
                        );
                b.setCancelable(false);

                LinearLayout root = new LinearLayout(this);
                root.setOrientation(LinearLayout.VERTICAL);
                root.setPadding(dp(26), dp(24), dp(26), dp(22));

                GradientDrawable bg = new GradientDrawable();
                bg.setColor(0xFF000000);
                bg.setCornerRadius(dp(10));
                bg.setStroke(dp(3), 0xFFFFD700);
                root.setBackground(bg);

                TextView msg = new TextView(this);
                msg.setText(
                        gr
                                ? "Ένιωσες καθαρά τη δόνηση;"
                                : "Did you clearly feel the vibration?"
                );
                msg.setTextColor(0xFF39FF14);
                msg.setTextSize(15f);
                msg.setGravity(Gravity.CENTER);
                msg.setPadding(0, 0, 0, dp(18));
                root.addView(msg);

                root.addView(buildMuteRow());

                LinearLayout btnRow = new LinearLayout(this);
                btnRow.setOrientation(LinearLayout.HORIZONTAL);
                btnRow.setGravity(Gravity.CENTER);

                LinearLayout.LayoutParams btnLp =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                btnLp.setMargins(dp(12), dp(8), dp(12), dp(8));

                Button noBtn = new com.gel.cleaner.GelButton(this);
                noBtn.setText(gr ? "ΟΧΙ" : "NO");
                noBtn.setAllCaps(false);
                noBtn.setTextColor(Color.WHITE);

                GradientDrawable noBg = new GradientDrawable();
                noBg.setColor(0xFF8B0000);
                noBg.setCornerRadius(dp(10));
                noBg.setStroke(dp(3), 0xFFFFD700);
                noBtn.setBackground(noBg);
                noBtn.setLayoutParams(btnLp);

                Button yesBtn = new com.gel.cleaner.GelButton(this);
                yesBtn.setText(gr ? "ΝΑΙ" : "YES");
                yesBtn.setAllCaps(false);
                yesBtn.setTextColor(Color.WHITE);

                GradientDrawable yesBg = new GradientDrawable();
                yesBg.setColor(0xFF0B5F3B);
                yesBg.setCornerRadius(dp(10));
                yesBg.setStroke(dp(3), 0xFFFFD700);
                yesBtn.setBackground(yesBg);
                yesBtn.setLayoutParams(btnLp);

                btnRow.addView(noBtn);
                btnRow.addView(yesBtn);
                root.addView(btnRow);

                b.setView(root);

                final AlertDialog d = b.create();

                if (d.getWindow() != null) {
                    d.getWindow().setBackgroundDrawable(
                            new ColorDrawable(Color.TRANSPARENT)
                    );
                }

                d.setOnDismissListener(dialog -> {
                    try { AppTTS.stop(); } catch (Throwable ignore) {}
                });

                d.setOnKeyListener((dialog, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK &&
                            event.getAction() == KeyEvent.ACTION_UP) {

                        try { AppTTS.stop(); } catch (Throwable ignore) {}
                        answered.set(true);
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                });

                if (!isFinishing() && !isDestroyed()) {
                    d.show();
                    com.gel.cleaner.GelButtonLayout.fitDialog(d);
                } else {
                    answered.set(true);
                    return;
                }

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (d.isShowing() && !AppTTS.isMuted(this)) {
                        AppTTS.ensureSpeak(
                                this,
                                gr
                                        ? "Ένιωσες καθαρά τη δόνηση;"
                                        : "Did you clearly feel the vibration?"
                        );
                    }
                }, 400);

                noBtn.setOnClickListener(v -> {
                    userConfirmed.set(false);
                    userResponded.set(true);
                    answered.set(true);
                    d.dismiss();
                });

                yesBtn.setOnClickListener(v -> {
                    userConfirmed.set(true);
                    userResponded.set(true);
                    answered.set(true);
                    d.dismiss();
                });
            });

            // No artificial 10-second failure.
            // Wait until the technician answers or the Activity closes.
            while (!answered.get() &&
                    !isFinishing() &&
                    !isDestroyed()) {

                SystemClock.sleep(80);
            }

            appendHtml("<br>");
            logLine();

            if (!userResponded.get()) {
                logLabelWarnValue(gr ? "Αποτέλεσμα" : "Result",
                        gr ? "Η δοκιμή ακυρώθηκε· δεν υπάρχει απάντηση χρήστη"
                           : "CANCELLED: no user observation was provided");
            } else if (userConfirmed.get()) {

                logLabelOkValue(
                        gr ? "Αποτέλεσμα (δήλωση χρήστη)" : "Result (user report)",
                        gr
                                ? "USER_REPORTED: ο χρήστης ένιωσε τη δόνηση. Δεν μετρήθηκε φυσική ένταση."
                                : "USER_REPORTED: vibration was felt; physical intensity was not measured."
                );

            } else {

                logLabelWarnValue(
                        gr ? "Αποτέλεσμα" : "Result",
                        gr
                                ? "USER_REPORTED: δεν έγινε αισθητή δόνηση· η αιτία παραμένει άγνωστη."
                                : "USER_REPORTED: vibration not felt; cause remains unknown."
                );

                logLabelWarnValue(
                        gr ? "Πιθανές αιτίες" : "Possible causes",
                        gr
                                ? "Απενεργοποιημένες ρυθμίσεις δόνησης, περιορισμός firmware, "
                                  + "ή πιθανή μηχανική φθορά."
                                : "Disabled vibration settings, firmware restriction, "
                                  + "or possible mechanical wear."
                );

                logOk(
                        gr
                                ? "Συνιστάται επιβεβαίωση μέσω πραγματικής κλήσης ή δοκιμής ειδοποίησης."
                                : "Verification via a real call or notification test is recommended."
                );
            }

        } catch (Throwable t) {

            logError(
                    gr
                            ? "Η δοκιμή δόνησης απέτυχε"
                            : "Vibration test failed"
            );

            logLabelWarnValue(
                    gr ? "Πιθανή αιτία" : "Possible cause",
                    gr
                            ? "Εκτέλεση μη διαθέσιμη· ρυθμίσεις, δικαιώματα ή περιορισμοί Android/OEM άγνωστοι."
                            : "Probe unavailable; Android/OEM settings, permissions or routing unverified."
            );

        } finally {

            try {
                if (vibrator != null) {
                    vibrator.cancel();
                }
            } catch (Throwable ignore) {}

            appendHtml("<br>");
            logOk(
                    gr
                            ? "Το Lab 5 ολοκληρώθηκε."
                            : "Lab 5 finished."
            );
            logLine();

            lab5VibrationPending.set(false);
            runOnUiThread(this::enableSingleExportButton);
        }

    }, "GEL_LAB5_VIBRATION").start();
}

// ============================================================
// LABS 6 — 9: DISPLAY & SENSORS
// ============================================================

// ============================================================
// LAB 6 — Display Touch (POPUP + MUTE + TTS + GR/EN)
// FINAL — LIFECYCLE SAFE
// ============================================================
private void lab6DisplayTouch() {
    lab6TouchGridObserved = false;

    final boolean gr = AppLang.isGreek(this);

    final String title =
            gr ? "Έλεγχος Οθόνης Αφής" : "Display Touch Test";

    final String message =
            gr
                    ? "Άγγιξε όλα τα σημεία στην οθόνη, για να ολοκληρωθεί το τεστ αφής.\n\n"
                    + "Το τεστ ελέγχει, αν υπάρχουν νεκρές, ή μη αποκρινόμενες περιοχές."
                    : "Touch all dots on the screen, to complete the touch test.\n\n"
                    + "This test checks, for unresponsive, or dead touch areas.";

// ---------------------------
// POPUP
// ---------------------------
AlertDialog.Builder b =
        new AlertDialog.Builder(
                this,
                android.R.style.Theme_Material_Dialog_NoActionBar
        );
b.setCancelable(false);

LinearLayout root = new LinearLayout(this);
root.setOrientation(LinearLayout.VERTICAL);
root.setPadding(32, 28, 32, 24);

GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF101010);
bg.setCornerRadius(10);
bg.setStroke(4, 0xFFFFD700);
root.setBackground(bg);

// ---------------------------
// TITLE
// ---------------------------
TextView titleView = new TextView(this);
titleView.setText(title);
titleView.setTextColor(Color.WHITE);
titleView.setTextSize(18f);
titleView.setTypeface(null, Typeface.BOLD);
titleView.setGravity(Gravity.CENTER);
titleView.setPadding(0, 0, 0, dp(14));

root.addView(titleView);

// ---------------------------
// MUTE ROW (CHECKBOX)
// ---------------------------
root.addView(buildMuteRow());

// ---------------------------
// MESSAGE
// ---------------------------
TextView tvMsg = new TextView(this);
tvMsg.setText(message);
tvMsg.setTextColor(0xFF39FF14);
tvMsg.setTextSize(15f);
tvMsg.setGravity(Gravity.CENTER);
tvMsg.setPadding(0, 0, 0, 32);
root.addView(tvMsg);

// ---------------------------
// START BUTTON
// ---------------------------
Button startBtn = new com.gel.cleaner.GelButton(this);
startBtn.setAllCaps(false);
startBtn.setText(gr ? "ΕΝΑΡΞΗ ΤΕΣΤ" : "START TEST");
startBtn.setTextColor(Color.WHITE);
startBtn.setTextSize(16f);

GradientDrawable startBg = new GradientDrawable();
startBg.setColor(0xFF0F8A3B);
startBg.setCornerRadius(10);
startBg.setStroke(3, 0xFFFFD700);
startBtn.setBackground(startBg);

LinearLayout.LayoutParams lpStart =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                120
        );
startBtn.setLayoutParams(lpStart);

root.addView(startBtn);

b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// Σταμάτα TTS όταν κλείσει
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// Κάλυψη BACK
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(d);
}

// ---------------------------
// ACTION
// ---------------------------
startBtn.setOnClickListener(v -> {
    AppTTS.stop();
    d.dismiss();

    if (btnExport != null) {
        btnExport.setEnabled(false);
        btnExport.setAlpha(0.5f);
    }
    startActivityForResult(
            new Intent(this, TouchGridTestActivity.class),
            REQ_LAB6_TOUCH
    );
});

}

// ============================================================
// LAB 7 — Rotation + Proximity Sensors (MANUAL • MODERN)
// ============================================================
private void lab7RotationAndProximityManual() {

    runOnUiThread(() -> {

        final boolean gr = AppLang.isGreek(this);

        final String titleText =
                gr
                        ? "LAB 7 — Αισθητήρες Περιστροφής & Εγγύτητας"
                        : "LAB 7 — Rotation & Proximity Sensors";

        final String messageText =
        gr
                ? "Βήμα 1:\n"
                  + "Περιστρέψτε αργά τη συσκευή.\n"
                  + "Η δοκιμή ελέγχει σταθερή αλλαγή προσανατολισμού, όχι περιστροφή UI.\n\n"
                  + "Βήμα 2:\n"
                  + "Καλύψτε με το χέρι σας τον αισθητήρα εγγύτητας, "
                  + "στο επάνω μέρος της οθόνης, στην περιοχή ειδοποιήσεων.\n"
                  + "Αποκάλυψε και κάλυψε τον αισθητήρα. Η οθόνη δεν χρειάζεται να σβήσει."
                : "Step 1:\n"
                  + "Rotate the device slowly.\n"
                  + "The test detects stable orientation changes, not UI rotation.\n\n"
                  + "Step 2:\n"
                  + "Cover the proximity sensor with your hand, "
                  + "at the top of the screen, to the notification area.\n"
                  + "Uncover and cover the sensor. The screen does not need to turn off.";

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(20), dp(24), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(4), 0xFFFFD700);
        root.setBackground(bg);

        // ---------------------------
        // TITLE
        // ---------------------------
        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(12));
        root.addView(title);

// ---------------------------
// MESSAGE (NEON GREEN EXCEPT "Βήμα X")
// ---------------------------
SpannableString span = new SpannableString(messageText);

int neonGreen = 0xFF39FF14;

// Βήμα 1
int step1Start = messageText.indexOf("Βήμα 1:");
int step2Start = messageText.indexOf("Βήμα 2:");

if (step1Start != -1 && step2Start != -1) {
    span.setSpan(
            new ForegroundColorSpan(neonGreen),
            step1Start + "Βήμα 1:".length(),
            step2Start,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    );

    span.setSpan(
            new ForegroundColorSpan(neonGreen),
            step2Start + "Βήμα 2:".length(),
            messageText.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    );
}

TextView msg = new TextView(this);
msg.setText(span);
msg.setTextColor(0xFFFFFFFF); // default για "Βήμα"
msg.setTextSize(15f);
msg.setGravity(Gravity.CENTER);
msg.setLineSpacing(0f, 1.15f);

root.addView(msg);

        // ---------------------------
        // MUTE ROW (STANDARD GEL)
        // ---------------------------
        root.addView(buildMuteRow());

        // ---------------------------
        // START BUTTON
        // ---------------------------
        Button start = gelButton(
                this,
                gr ? "ΕΝΑΡΞΗ ΤΕΣΤ" : "START TEST",
                0xFF39FF14
        );
        root.addView(start);

        b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// ΣΤΑΜΑΤΑ TTS ΟΠΟΤΕ ΚΛΕΙΣΕΙ
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// ΚΑΛΥΨΗ BACK BUTTON
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(d);
}

        // ---------------------------
        // TTS (ONLY IF NOT MUTED)
        // ---------------------------
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (d.isShowing() && !AppTTS.isMuted(this)) {
                AppTTS.ensureSpeak(this, messageText);
            }
        }, 120);

        // ---------------------------
        // ACTION
        // ---------------------------
        start.setOnClickListener(v -> {
            AppTTS.stop();
            d.dismiss();

            startActivityForResult(
                    new Intent(this, RotationCheckActivity.class),
                    7007
            );
        });
    });
}

// ============================================================
// LAB 8 — Camera Hardware & Path Integrity (FULL TECH MODE)
// • All cameras (front/back/extra)
// • Preview path per camera (user confirmation)
// • Torch test where available
// • Frame stream sampling (FPS / drops / black frames / luma stats)
// • Pipeline latency estimate (sensor timestamp  arrival)
// • RAW support check (and optional RAW stream probe if supported)
// ============================================================

private void lab8CameraHardwareCheck() {

    if (!guardCamera(this::lab8CameraHardwareCheck)) return;

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logSection(gr
            ? "LAB 8 — Έλεγχος Υλικού Κάμερας & Ακεραιότητας Διαδρομής"
            : "LAB 8 — Camera Hardware & Path Integrity");
    logLine();

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        logWarn(gr
                ? "Το Camera2 δεν υποστηρίζεται σε αυτήν την έκδοση Android."
                : "Camera2 not supported on this Android version.");
        logOk(gr
                ? "Fallback: άνοιγμα εφαρμογής κάμερας (βασικός έλεγχος)."
                : "Fallback: opening system camera app (basic check).");
        try {
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 9009);
        } catch (Throwable t) {
            logError(gr
                    ? "Αποτυχία εκκίνησης εφαρμογής κάμερας."
                    : "Failed to launch camera app.");
            logWarn(gr
                    ? "Η εφαρμογή κάμερας μπορεί να λείπει ή να είναι μπλοκαρισμένη."
                    : "Camera app may be missing or blocked.");

            appendHtml("<br>");
            logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
            logLine();
            enableSingleExportButton();
        }
        return;
    }

    final PackageManager pm = getPackageManager();
    final boolean hasAnyCamera =
            pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);

    if (!hasAnyCamera) {
        logError(gr
                ? "Δεν εντοπίστηκε υλικό κάμερας στη συσκευή."
                : "No camera hardware detected on this device.");
        appendHtml("<br>");
        logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    final CameraManager cm =
            (CameraManager) getSystemService(Context.CAMERA_SERVICE);

    if (cm == null) {
        logError(gr
                ? "Το CameraManager δεν είναι διαθέσιμο."
                : "CameraManager unavailable.");
        appendHtml("<br>");
        logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    // Permission check (Android 6+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    2001
            );
            return;
        }
    }

    // ------------------------------------------------------------
    // Collect camera IDs
    // ------------------------------------------------------------
    final String[] ids;
    try {
        ids = cm.getCameraIdList();
    } catch (Throwable t) {
        logError(gr
                ? "Αποτυχία καταγραφής camera IDs."
                : "Failed to enumerate cameras.");
        appendHtml("<br>");
        logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    if (ids == null || ids.length == 0) {
        logError(gr
                ? "Δεν βρέθηκαν προσβάσιμα camera IDs."
                : "No accessible camera IDs found.");
        appendHtml("<br>");
        logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    logLabelOkValue(
            gr ? "Υποσύστημα Κάμερας" : "Camera subsystem",
            gr ? "Εντοπίστηκε" : "Detected"
    );

    logLabelOkValue(
            gr ? "Σύνολο camera IDs" : "Total camera IDs",
            String.valueOf(ids.length)
    );

    // ------------------------------------------------------------
    // Build per-camera descriptors
    // ------------------------------------------------------------
    final ArrayList<Lab8Cam> cams = new ArrayList<>();

    for (String id : ids) {
        try {
            CameraCharacteristics cc =
                    cm.getCameraCharacteristics(id);

            Integer facing =
                    cc.get(CameraCharacteristics.LENS_FACING);

            Float focal =
                    cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS) != null
                            && cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS).length > 0
                            ? cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0]
                            : null;

            Boolean flash =
                    cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);

            int[] caps =
                    cc.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);

            boolean hasRaw = false;
            boolean hasManual = false;
            boolean hasDepth = false;

            if (caps != null) {
                for (int c : caps) {
                    if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW)
                        hasRaw = true;
                    if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR)
                        hasManual = true;
                    if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT)
                        hasDepth = true;
                }
            }

            StreamConfigurationMap map =
                    cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            Size previewSize = null;
            if (map != null) {
                Size[] outs =
                        map.getOutputSizes(SurfaceTexture.class);
                if (outs != null && outs.length > 0) {
                    previewSize = outs[0];
                    for (Size s : outs) {
                        if (s.getWidth() <= 1920 && s.getHeight() <= 1080) {
                            previewSize = s;
                            break;
                        }
                    }
                }
            }

            String facingStr = gr ? "ΑΓΝΩΣΤΟ" : "UNKNOWN";
            if (facing != null) {
                if (facing == CameraCharacteristics.LENS_FACING_BACK)
                    facingStr = gr ? "ΠΙΣΩ" : "BACK";
                else if (facing == CameraCharacteristics.LENS_FACING_FRONT)
                    facingStr = gr ? "ΜΠΡΟΣΤΑ" : "FRONT";
                else if (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                    facingStr = gr ? "ΕΞΩΤΕΡΙΚΗ" : "EXTERNAL";
            }

            Lab8Cam c = new Lab8Cam();
            c.id = id;
            c.facing = facingStr;
            c.hasFlash = Boolean.TRUE.equals(flash);
            c.hasRaw = hasRaw;
            c.hasManual = hasManual;
            c.hasDepth = hasDepth;
            c.focal = focal;
            c.preview = previewSize;

            cams.add(c);

        } catch (Throwable t) {
            logWarn(gr
                    ? "Camera ID " + id + " — αποτυχία ανάγνωσης χαρακτηριστικών"
                    : "Camera ID " + id + " — Characteristics read failed");
        }
    }

    if (cams.isEmpty()) {
        logError(gr
                ? "Δεν βρέθηκαν αξιοποιήσιμες περιγραφές καμερών."
                : "No usable camera descriptors.");
        appendHtml("<br>");
        logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    logInfo(gr
            ? "Σύνοψη δυνατοτήτων καμερών:"
            : "Camera capabilities summary:");

    logLabelOkValue(
            gr ? "Προσβάσιμες κάμερες που εντοπίστηκαν"
               : "Accessible cameras detected",
            String.valueOf(cams.size())
    );

    logInfo(
            gr
                    ? "Το LAB 8 θα ελέγξει όλες τις κάμερες που το Android "
                      + "εκθέτει στην εφαρμογή."
                    : "LAB 8 will test every camera exposed by Android "
                      + "to this application."
    );

    logWarn(
            gr
                    ? "Ορισμένοι κατασκευαστές μπορεί να μην εκθέτουν ξεχωριστά "
                      + "βοηθητικούς φακούς, όπως macro, depth ή telephoto. "
                      + "Η μη εμφάνισή τους δεν θεωρείται ένδειξη βλάβης."
                    : "Some manufacturers may not expose auxiliary lenses separately, "
                      + "such as macro, depth or telephoto cameras. "
                      + "Their absence from this list is not treated as a hardware fault."
    );

    logLine();
    appendHtml("<br>");

// ------------------------------------------------------------
// Run test sequence (one camera at a time)
// ------------------------------------------------------------
final int[] idx = {0};

final Lab8Overall overall = new Lab8Overall();
overall.total = cams.size();

// Save state for LAB 8.1
lab8CamsFor81 = cams;
lab8CmFor81 = cm;

runOnUiThread(() -> showLab8IntroAndStart(cams, idx, cm, overall));
}

// ============================================================
// LAB 8 — Intro dialog (TTS + MUTE + GR/EN)
// ============================================================
private void showLab8IntroAndStart(
        ArrayList<Lab8Cam> cams,
        int[] idx,
        CameraManager cm,
        Lab8Overall overall
) {

    final boolean gr = AppLang.isGreek(this);

    final String titleText =
            gr ? "LAB 8 — Έλεγχος Καμερών (Πλήρης)"
               : "LAB 8 — Camera Lab (Full)";

    final String messageText =
            gr
                    ? "Αυτό το τεστ θα ελέγξει ΟΛΕΣ τις προσβάσιμες κάμερες, μία-μία.\n\n"
                      + "Για κάθε κάμερα:\n"
                      + "• Θα ανοίξει ζωντανή προεπισκόπηση.\n"
                      + "• Θα μετρηθεί η ροή καρέ.\n"
                      + "• Θα εκτιμηθεί η καθυστέρηση pipeline.\n"
                      + "• Θα ενεργοποιηθεί το φλας, όπου υπάρχει.\n\n"
                      + "Μετά από κάθε κάμερα, θα σου ζητηθεί επιβεβαίωση."
                    : "This lab, will test ALL cameras, one by one.\n\n"
                      + "For each camera:\n"
                      + "• Live preview will open.\n"
                      + "• Frame stream will be sampled.\n"
                      + "• Pipeline latency, will be estimated\n"
                      + "• Flash will be toggled, where available\n\n"
                      + "After each camera, you will be asked to confirm.";

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    ManualTestsActivity.this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(20), dp(24), dp(18));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(4), 0xFFFFD700);
    root.setBackground(bg);

    // ---------------------------
    // TITLE
    // ---------------------------
    TextView title = new TextView(this);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

// ---------------------------
// MESSAGE (NEON GREEN)
// ---------------------------
TextView msg = new TextView(this);
msg.setText(messageText);
msg.setTextColor(0xFF39FF14); // NEON GREEN
msg.setTextSize(15f);
msg.setGravity(Gravity.CENTER);
msg.setLineSpacing(0f, 1.15f);
root.addView(msg);

    // ---------------------------
    // MUTE ROW (ABOVE START)
    // ---------------------------
    root.addView(buildMuteRow());

    // ---------------------------
    // START BUTTON
    // ---------------------------
    Button start = new com.gel.cleaner.GelButton(this);
    start.setText(gr ? "ΕΝΑΡΞΗ ΤΕΣΤ" : "START TEST");
    start.setAllCaps(false);
    start.setTextColor(Color.WHITE);

    GradientDrawable startBg = new GradientDrawable();
    startBg.setColor(0xFF39FF14);
    startBg.setCornerRadius(dp(10));
    startBg.setStroke(dp(3), 0xFFFFD700);
    start.setBackground(startBg);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(56)
            );
    lp.setMargins(0, dp(14), 0, 0);
    start.setLayoutParams(lp);
    root.addView(start);

    b.setView(root);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// STOP TTS όταν κλείσει
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// START BUTTON
start.setOnClickListener(v -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();
    lab8RunNextCamera(cams, idx, cm, overall);
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(d);
}

}  

// ============================================================
// LAB 8 — Run next camera
// ============================================================
private void lab8RunNextCamera(
        ArrayList<Lab8Cam> cams,
        int[] idx,
        CameraManager cm,
        Lab8Overall overall
) {

    final boolean gr = AppLang.isGreek(this);

    // ====================================================
    // ALL CAMERAS DONE — FINAL SUMMARY + VERDICT
    // ====================================================
    if (idx[0] >= cams.size()) {

        logLine();
        logInfo(gr ? "ΣΥΝΟΨΗ LAB 8:" : "LAB 8 summary:");
        logLine();

        logLabelValue(
                gr ? "Προσβάσιμες κάμερες που ελέγχθηκαν"
                   : "Accessible cameras tested",
                String.valueOf(overall.previewOkCount + overall.previewFailCount + overall.previewUnavailableCount + overall.previewInconclusiveCount)
        );

        logInfo(
                gr
                        ? "Έγινε προσπάθεια ελέγχου των προσβάσιμων καμερών· "
                          + "η πλήρης κάλυψη εξαρτάται από τα πραγματικά καρέ."
                        : "Accessible cameras were attempted; coverage depends "
                          + "on frames actually sampled."
        );

        logWarn(
                gr
                        ? "Τυχόν κρυφές βοηθητικές κάμερες που δεν εκτίθενται "
                          + "από τον κατασκευαστή δεν αξιολογούνται ως αποτυχία."
                        : "Any hidden auxiliary cameras not exposed by the manufacturer "
                          + "are not evaluated as a failure."
        );

        if (overall.previewOkCount == overall.total && overall.total > 0)
            logLabelOkValue(
                    gr ? "Προεπισκόπηση OK" : "Preview OK",
                    overall.previewOkCount + "/" + overall.total
            );
        else
            logLabelWarnValue(
                    gr ? "Προεπισκόπηση OK" : "Preview OK",
                    overall.previewOkCount + "/" + overall.total
            );

        logLabelValue(
                gr ? "Ο χρήστης δεν είδε εικόνα" : "User reported no image",
                String.valueOf(overall.previewFailCount)
        );
        logLabelValue(
                gr ? "Camera2 μη διαθέσιμη" : "Camera2 unavailable",
                String.valueOf(overall.previewUnavailableCount)
        );
        logLabelValue(
                gr ? "Ανεπαρκή στοιχεία" : "Inconclusive observations",
                String.valueOf(overall.previewInconclusiveCount)
        );

        logLabelValue(gr ? "Εντολές Torch που έγιναν δεκτές" : "Torch commands accepted",
                String.valueOf(overall.torchOkCount));
        logLabelValue(gr ? "Εντολές Torch μη διαθέσιμες" : "Torch commands unavailable",
                String.valueOf(overall.torchFailCount));
        logInfo(gr ? "Αποδοχή εντολής δεν αποδεικνύει εκπομπή φωτός από LED."
                   : "A successful command does not prove physical LED emission.");

        if (overall.streamIssueCount == 0)
            logLabelValue(
                    gr ? "Επισημάνσεις ροής καρέ" : "Frame stream flags",
                    gr ? "Δεν καταγράφηκαν στο διαθέσιμο δείγμα" : "None flagged in sampled evidence"
            );
        else
            logLabelWarnValue(
                    gr ? "Προβλήματα ροής καρέ" : "Frame stream issues",
                    String.valueOf(overall.streamIssueCount)
            );

        // ====================================================
        // FINAL VERDICT
        // ====================================================
        boolean cameraSubsystemOk =
                overall.total > 0 &&
                overall.previewFailCount == 0 &&
                overall.previewOkCount == overall.total;

        if (cameraSubsystemOk) {

            logLabelOkValue(
                    gr ? "Παρατηρήσεις προεπισκόπησης" : "Guided preview observations",
                    gr ? "Επαρκείς για το περιορισμένο πρωτόκολλο" : "Sufficient for scoped preview protocol"
            );

            if (overall.streamIssueCount == 0)
                logLabelOkValue(
                        gr ? "Σταθερότητα ροής" : "Live stream stability",
                        "OK"
                );
            else
                logLabelWarnValue(
                        gr ? "Σταθερότητα ροής" : "Live stream stability",
                        gr ? "Μικρές ανωμαλίες" : "Minor anomalies detected"
                );

            logLabelValue(gr ? "Torch — κατάσταση" : "Torch — status",
                    gr ? "Παρατηρήθηκαν μόνο αποτελέσματα εντολών Android"
                       : "Only Android command outcomes observed");

            logOk(
                    gr
                            ? "Η συσκευή πληροί τα κριτήρια για αξιολόγηση δυνατοτήτων κάμερας."
                            : "Your device meets the criteria to evaluate camera capabilities."
            );

            logInfo(
                    gr
                            ? "Στο επόμενο βήμα αναλύουμε δυνατότητες φωτογραφίας & βίντεο."
                            : "Next step: analyze photo & video capabilities."
            );

            appendHtml("<br>");
            logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
            logLine();

            runOnUiThread(this::showLab8_1Prompt);
            return;

        } else {

            logLabelErrorValue(
                    gr ? "Παρατηρήσεις προεπισκόπησης" : "Guided preview observations",
                    gr ? "ΜΕΡΙΚΕΣ / ΜΗ ΕΠΑΛΗΘΕΥΜΕΝΕΣ" : "PARTIAL / UNVERIFIED"
            );

            logError(
                    gr
                            ? "Δεν συλλέχθηκαν επαρκείς ενδείξεις από όλες τις κάμερες. Δεν τεκμηριώνεται βλάβη."
                            : "Not all cameras yielded sufficient evidence. Hardware failure is not established."
            );

            appendHtml("<br>");
            logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
            logLine();

            enableSingleExportButton();
            return;
        }
    }

    // ====================================================
    // NEXT CAMERA
    // ====================================================
    final Lab8Cam cam = cams.get(idx[0]);
    idx[0]++;

    logSection("LAB 8 — Camera ID " + cam.id + " (" + cam.facing + ")");
    logLine();

    if (cam.hasManual)
        logLabelOkValue(
                gr ? "Χειροκίνητος αισθητήρας" : "Manual sensor",
                "YES"
        );
    else
        logLabelWarnValue(
                gr ? "Χειροκίνητος αισθητήρας" : "Manual sensor",
                "NO"
        );

    if (cam.hasDepth)
        logLabelOkValue(
                gr ? "Αισθητήρας βάθους" : "Depth output",
                "YES"
        );
    else
        logLabelWarnValue(
                gr ? "Αισθητήρας βάθους" : "Depth output",
                "NO"
        );

    if (cam.focal != null)
        logLabelValue(
                gr ? "Εστιακή απόσταση" : "Focal length",
                String.format(Locale.US, "%.2f mm", cam.focal)
        );

    if (cam.preview != null)
        logLabelValue(
                gr ? "Ανάλυση προεπισκόπησης" : "Preview size",
                cam.preview.getWidth() + " x " + cam.preview.getHeight()
        );

    logLine();

    if (cam.hasFlash) {
        lab8TryTorchToggle(cam.id, cam, overall);
    } else {
        logLabelWarnValue(
                gr ? "Φλας" : "Flash",
                gr ? "Δεν υπάρχει" : "Not available"
        );
    }

    runOnUiThread(() ->
            lab8ShowPreviewDialogForCamera(
                    cam,
                    cm,
                    overall,
                    () -> lab8RunNextCamera(cams, idx, cm, overall)
            )
    );
}

// ============================================================
// LAB 8 — Torch toggle
// ============================================================
private void lab8TryTorchToggle(String camId, Lab8Cam cam, Lab8Overall overall) {
    try {
        CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (cm == null) {
            logLabelWarnValue("Flash", "Test skipped (CameraManager unavailable)");
            overall.torchFailCount++;
            return;
        }

        cm.setTorchMode(camId, true);
        SystemClock.sleep(250);
        cm.setTorchMode(camId, false);

        logLabelValue("Flash", "Android accepted torch on/off commands — light not measured");
        overall.torchOkCount++;

    } catch (Throwable t) {
        logLabelWarnValue("Flash", "Torch command unavailable in current conditions");
        logWarn("Permission, camera-in-use, device policy and OEM limits remain possible.");
        overall.torchFailCount++;
    }
}

// ============================================================
// LAB 8 — Preview dialog + stream sampling (TTS + MUTE + GR/EN)
// ============================================================
private void lab8ShowPreviewDialogForCamera(
        Lab8Cam cam,
        CameraManager cm,
        Lab8Overall overall,
        Runnable onDone
) {

    final boolean gr = AppLang.isGreek(this);

    final String titleText =
            gr
                    ? "Προεπισκόπηση Κάμερας — " + cam.facing + " (ID " + cam.id + ")"
                    : "Camera Preview — " + cam.facing + " (ID " + cam.id + ")";

    final String messageText =
            gr
                    ? "Περίμενε περίπου 5 δευτερόλεπτα, όσο γίνεται δειγματοληψία καρέ.\n\n"
                      + "Στη συνέχεια απάντησε:\n"
                      + "Είδες ζωντανή εικόνα από την κάμερα;"
                    : "Please wait about 5 seconds, while frames are sampled.\n\n"
                      + "Then answer:\n"
                      + "Did you see live image from the camera?";

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    ManualTestsActivity.this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(18), dp(16), dp(18), dp(14));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(4), 0xFFFFD700);
    root.setBackground(bg);

    // ---------------------------
    // TITLE
    // ---------------------------
    TextView title = new TextView(this);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(16f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(10));
    root.addView(title);

    // ---------------------------
    // MESSAGE 
    // ---------------------------

TextView hint = new TextView(this);
hint.setText(messageText);
hint.setTextColor(0xFF39FF14); // NEON GREEN
hint.setTextSize(14f);
hint.setGravity(Gravity.CENTER);
hint.setPadding(0, 0, 0, dp(10));
hint.setLineSpacing(0f, 1.15f);
root.addView(hint);

    // ---------------------------
    // PREVIEW (TextureView)
    // ---------------------------
    final TextureView tv = new TextureView(this);
    LinearLayout.LayoutParams lpTv =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(280)
            );
    tv.setLayoutParams(lpTv);
    root.addView(tv);

    // ---------------------------
    // MUTE ROW (ABOVE YES / NO)
    // ---------------------------
    root.addView(buildMuteRow());

    // Buttons row
    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER);
    row.setPadding(0, dp(12), 0, 0);

    Button yes = new com.gel.cleaner.GelButton(this);
    yes.setText(gr ? "ΒΛΕΠΩ ΕΙΚΟΝΑ" : "I SEE IMAGE");
    yes.setAllCaps(false);
    yes.setTextColor(0xFFFFFFFF);
    GradientDrawable yesBg = new GradientDrawable();
    yesBg.setColor(0xFF0B5F3B);
    yesBg.setCornerRadius(dp(10));
    yesBg.setStroke(dp(3), 0xFFFFD700);
    yes.setBackground(yesBg);

    Button no = new com.gel.cleaner.GelButton(this);
    no.setText(gr ? "ΔΕΝ ΒΛΕΠΩ ΕΙΚΟΝΑ" : "NO IMAGE");
    no.setAllCaps(false);
    no.setTextColor(0xFFFFFFFF);
    GradientDrawable noBg = new GradientDrawable();
    noBg.setColor(0xFF8B0000);
    noBg.setCornerRadius(dp(10));
    noBg.setStroke(dp(3), 0xFFFFD700);
    no.setBackground(noBg);

    LinearLayout.LayoutParams lpB =
            new LinearLayout.LayoutParams(0, dp(56), 1f);
    lpB.setMargins(0, 0, dp(8), 0);
    yes.setLayoutParams(lpB);

    LinearLayout.LayoutParams lpB2 =
            new LinearLayout.LayoutParams(0, dp(56), 1f);
    lpB2.setMargins(dp(8), 0, 0, 0);
    no.setLayoutParams(lpB2);

    row.addView(yes);
    row.addView(no);
    root.addView(row);

    b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// Keep the active preview separately so backgrounding can invalidate it.
lab8ActiveDialog = d;
// Σταμάτα TTS ΟΠΟΤΕ κλείσει
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    if (lab8ActiveDialog == d) lab8ActiveDialog = null;
    Lab8Session abandoned = cam.runtimeSession;
    if (abandoned != null && !abandoned.terminal) {
        // BACK, background or dismiss cannot leave an orphaned Camera2 resource.
        abandoned.cancelled = true;
        abandoned.terminal = true;
        lab8CloseSession(abandoned);
        logLabelWarnValue(gr ? "Προεπισκόπηση" : "Preview",
                gr ? "Διακόπηκε — κανένα συμπέρασμα υλικού"
                   : "Interrupted — no camera hardware verdict");
        enableSingleExportButton();
    }
});

// Κάλυψη BACK button
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(d);
}
    
// ---------------------------
// TTS (ONLY IF NOT MUTED)
// ---------------------------
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (d.isShowing() && !AppTTS.isMuted(this)) {
        AppTTS.ensureSpeak(this, messageText);
    }
}, 120);

    // Disable buttons until sampling done (avoid instant wrong click)
    yes.setEnabled(false);
    no.setEnabled(false);

    final Lab8Session s = new Lab8Session();
    s.camId = cam.id;
    s.cm = cm;
    s.textureView = tv;
    s.cam = cam;
    
cam.runtimeSession = s;

    final AtomicBoolean finished = new AtomicBoolean(false);

    Runnable finishAndNext = () -> {
        if (finished.getAndSet(true)) return;
        s.terminal = true;
        try { lab8CloseSession(s); } catch (Throwable ignore) {}
        try { d.dismiss(); } catch (Throwable ignore) {}
        onDone.run();
    };

    // After sampling window, enable buttons
    Runnable enableButtons = () -> {
        if (finished.get() || s.terminal || s.cancelled || !s.samplingComplete) return;
        yes.setEnabled(true);
        no.setEnabled(true);
    };

    Runnable failAndNext = () -> {
        if (finished.get() || s.cancelled || s.terminal) return;
        overall.previewUnavailableCount++;
        overall.streamIssueCount++;
        logLabelWarnValue(gr ? "Camera2" : "Camera2",
                gr ? "Μη διαθέσιμη δοκιμή — όχι διάγνωση βλάβης"
                   : "Capture unavailable — no hardware-failure verdict");
        finishAndNext.run();
    };

    yes.setOnClickListener(v -> {
        if (finished.get() || !s.samplingComplete || s.cancelled) return;
        AppTTS.stop();
        s.userConfirmedPreview = Boolean.TRUE;
        lab8FinalizePreviewOutcome(s, overall);
        finishAndNext.run();
    });

    no.setOnClickListener(v -> {
        if (finished.get() || !s.samplingComplete || s.cancelled) return;
        AppTTS.stop();
        s.userConfirmedPreview = Boolean.FALSE;
        lab8FinalizePreviewOutcome(s, overall);
        finishAndNext.run();
    });

    // A missing surface/slow OEM callback must not leave this dialog alive
    // indefinitely. Timeout is test unavailability, not hardware failure.
    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        if (!finished.get() && !s.samplingComplete && !s.terminal) {
            failAndNext.run();
        }
    }, 20000);

    // Start camera when texture is ready
    if (tv.isAvailable()) {
        lab8StartCamera2Session(s, overall, enableButtons, failAndNext);
    } else {
        tv.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override public void onSurfaceTextureAvailable(SurfaceTexture st, int w, int h) {
                if (finished.get()) return;
                lab8StartCamera2Session(s, overall, enableButtons, failAndNext);
            }
            @Override public void onSurfaceTextureSizeChanged(SurfaceTexture st, int w, int h) {}
            @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture st) { return true; }
            @Override public void onSurfaceTextureUpdated(SurfaceTexture st) {}
        });
    }
}

// ============================================================
// LAB 8 — Start Camera2 preview + stream sampling
// ============================================================
private void lab8StartCamera2Session(
        Lab8Session s,
        Lab8Overall overall,
        Runnable onSamplingDoneEnableButtons,
        Runnable onFail
) {

    final boolean gr = AppLang.isGreek(this);
    
    try {
        // Choose preview size
        Size ps = (s.cam != null && s.cam.preview != null) ? s.cam.preview : new Size(1280, 720);

        SurfaceTexture st = s.textureView.getSurfaceTexture();
        if (st == null) {
            logLabelErrorValue("Preview", "SurfaceTexture unavailable");
            onFail.run();
            return;
        }
        st.setDefaultBufferSize(ps.getWidth(), ps.getHeight());
        final Surface previewSurface = new Surface(st);

        // ImageReader for stream sampling (YUV)
        s.reader = ImageReader.newInstance(
                Math.min(ps.getWidth(), 1280),
                Math.min(ps.getHeight(), 720),
                ImageFormat.YUV_420_888,
                2
        );

        s.sampleStartMs = SystemClock.elapsedRealtime();
        s.frames = 0;
        s.blackFrames = 0;
        s.droppedFrames = 0;
        s.sumLuma = 0;
        s.sumLuma2 = 0;
        s.minLuma = 999;
        s.maxLuma = -1;
        s.latencySumMs = 0;
        s.latencyCount = 0;
        s.lastFrameTsNs = 0;

        s.reader.setOnImageAvailableListener(reader -> {
            Image img = null;
            try {
                img = reader.acquireLatestImage();
                if (img == null) return;

                long nowNs = SystemClock.elapsedRealtimeNanos();
                s.frames++;

                // Estimate drop/jitter (very simple)
                if (s.lastFrameTsNs != 0) {
                    long dtNs = nowNs - s.lastFrameTsNs;
                    if (dtNs > 200_000_000L) s.droppedFrames++;
                }
                s.lastFrameTsNs = nowNs;

                // Basic frame analysis: sample luma plane sparsely
                Image.Plane[] planes = img.getPlanes();
                if (planes != null && planes.length > 0 && planes[0] != null) {
                    ByteBuffer y = planes[0].getBuffer();
                    int rowStride = planes[0].getRowStride();
                    int w = img.getWidth();
                    int h = img.getHeight();

                    int stepX = Math.max(8, w / 64);
                    int stepY = Math.max(8, h / 48);

                    long sum = 0;
                    long sum2 = 0;
                    int count = 0;
                    int localMin = 999;
                    int localMax = -1;

                    for (int yy = 0; yy < h; yy += stepY) {
                        int row = yy * rowStride;
                        for (int xx = 0; xx < w; xx += stepX) {
                            int idx = row + xx;
                            if (idx < 0 || idx >= y.limit()) continue;
                            int v = y.get(idx) & 0xFF;
                            sum += v;
                            sum2 += (long) v * (long) v;
                            count++;
                            if (v < localMin) localMin = v;
                            if (v > localMax) localMax = v;
                        }
                    }

                    if (count > 0) {
                        s.validLumaFrames++;
                        int mean = (int) (sum / count);
                        s.sumLuma += sum;
                        s.sumLuma2 += sum2;
                        if (localMin < s.minLuma) s.minLuma = localMin;
                        if (localMax > s.maxLuma) s.maxLuma = localMax;

                        if (mean < 8 && localMax < 20) s.blackFrames++;
                    }
                }

                long sensorNs = img.getTimestamp(); // best-effort
                if (sensorNs > 0) {
                    long latMs = (nowNs - sensorNs) / 1_000_000L;
                    if (latMs >= 0 && latMs < 2000) {
                        s.latencySumMs += latMs;
                        s.latencyCount++;
                    }
                }

            } catch (Throwable ignore) {
            } finally {
                try { if (img != null) img.close(); } catch (Throwable ignore2) {}
            }
        }, new Handler(Looper.getMainLooper()));

// Open camera device
s.cm.openCamera(s.camId, new CameraDevice.StateCallback() {

    @Override
    public void onOpened(CameraDevice camera) {
        if (s.cancelled || s.terminal) {
            try { camera.close(); } catch (RuntimeException ignored) {}
            return;
        }
        s.device = camera;

        try {
            ArrayList<Surface> outs = new ArrayList<>();
            outs.add(previewSurface);
            outs.add(s.reader.getSurface());

            camera.createCaptureSession(
                    outs,
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            if (s.cancelled || s.terminal) {
                                try { session.close(); } catch (RuntimeException ignored) {}
                                return;
                            }
                            s.session = session;

                            try {
                                CaptureRequest.Builder rb =
                                        camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                                rb.addTarget(previewSurface);
                                rb.addTarget(s.reader.getSurface());

                                try {
                                    CameraCharacteristics cc =
                                            s.cm.getCameraCharacteristics(s.camId);
                                    Range<Integer>[] ranges =
                                            cc.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);

                                    if (ranges != null && ranges.length > 0) {
                                        Range<Integer> best = ranges[0];
                                        for (Range<Integer> r : ranges) {
                                            if (r.getUpper() >= 30 && r.getLower() >= 15) {
                                                best = r;
                                                break;
                                            }
                                        }
                                        rb.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, best);
                                    }
                                } catch (Throwable ignore) {}

                                rb.set(CaptureRequest.CONTROL_MODE,
                                        CaptureRequest.CONTROL_MODE_AUTO);
                                rb.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                session.setRepeatingRequest(
                                        rb.build(),
                                        null,
                                        new Handler(Looper.getMainLooper())
                                );
                                s.repeatingRequestStarted = true;

                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    if (s.terminal || s.cancelled) return;
                                    try {
                                        lab8StopAndReportSample(s, overall);
                                        s.samplingComplete = true;
                                    } catch (RuntimeException e) {
                                        onFail.run();
                                        return;
                                    }
                                    onSamplingDoneEnableButtons.run();
                                }, 5000);

                            } catch (Throwable t) {
                                logLabelErrorValue(
                                        "Preview",
                                        gr
                                                ? "Αποτυχία εκκίνησης επαναλαμβανόμενου αιτήματος"
                                                : "Failed to start repeating request"
                                );
                                onFail.run();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            logLabelErrorValue(
                                    "Preview",
                                    gr
                                            ? "Αποτυχία διαμόρφωσης capture session"
                                            : "Capture session configuration failed"
                            );
                            onFail.run();
                        }

                    },
                    new Handler(Looper.getMainLooper())
            );

        } catch (Throwable t) {
            logLabelErrorValue(
                    "Preview",
                    gr
                            ? "Αποτυχία δημιουργίας session"
                            : "Session creation failed"
            );
            onFail.run();
        }
    }

    @Override
    public void onDisconnected(CameraDevice camera) {
        logLabelWarnValue(
                "Preview",
                gr
                        ? "Η κάμερα αποσυνδέθηκε κατά τη δειγματοληψία"
                        : "Camera disconnected during sampling"
        );
        onFail.run();
    }

    @Override
    public void onError(CameraDevice camera, int error) {
        logLabelErrorValue(
                "Camera open",
                gr
                        ? "Σφάλμα ανοίγματος κάμερας (κωδικός " + error + ")"
                        : "Camera open error (code " + error + ")"
        );
        onFail.run();
    }

}, new Handler(Looper.getMainLooper()));

} catch (Throwable t) {
    logLabelErrorValue(
            "Camera2",
            gr
                    ? "Αποτυχία εκκίνησης Camera2 session"
                    : "Session start failed"
    );
    onFail.run();
}
}

// ============================================================
// LAB 8 — Stop + report stream sample
// ============================================================
private void lab8StopAndReportSample(Lab8Session s, Lab8Overall overall) {

    final boolean gr = AppLang.isGreek(this);

    // ------------------------------------------------------------
    // Camera runtime results (AFTER sampling)
    // ------------------------------------------------------------
    long durMs = Math.max(1, SystemClock.elapsedRealtime() - s.sampleStartMs);
    float fps = (s.frames * 1000f) / durMs;

    // Stream sampling
    logLabelValue(
            gr ? "Δειγματοληψία ροής" : "Stream sampling",
            "5s"
    );

    if (s.frames > 0)
        logLabelOkValue(
                gr ? "Καρέ" : "Frames",
                String.valueOf(s.frames)
        );
    else
        logLabelWarnValue(
                gr ? "Καρέ" : "Frames",
                gr ? "0 — άγνωστη λειτουργία ροής" : "0 — no stream evidence"
        );

    if (fps >= 20f)
        logLabelOkValue(
                gr ? "FPS (εκτίμηση)" : "FPS (estimated)",
                String.format(Locale.US, "%.1f", fps)
        );
    else
        logLabelWarnValue(
                gr ? "FPS (εκτίμηση)" : "FPS (estimated)",
                String.format(Locale.US, "%.1f", fps)
        );

    if (s.frames > 0 && s.droppedFrames == 0)
        logLabelValue(
                gr ? "Απώλειες καρέ / timeouts" : "Frame drops / timeouts",
                "0"
        );
    else
        logLabelWarnValue(
                gr ? "Απώλειες καρέ / timeouts" : "Frame drops / timeouts",
                String.valueOf(s.droppedFrames)
        );

    if (s.frames > 0 && s.blackFrames == 0)
        logLabelValue(
                gr ? "Μαύρα καρέ (ύποπτα)" : "Black frames (suspected)",
                "0"
        );
    else {
        logLabelWarnValue(
                gr ? "Μαύρα καρέ (ύποπτα)" : "Black frames (suspected)",
                String.valueOf(s.blackFrames)
        );
        overall.streamIssueCount++;
    }

    logLabelValue(
            gr ? "Εύρος φωτεινότητας (min / max)" : "Luma range (min / max)",
            s.minLuma + " / " + s.maxLuma
    );

    if (s.latencyCount > 0) {
        long avg = s.latencySumMs / Math.max(1, s.latencyCount);

        if (avg <= 250)
            logLabelOkValue(
                    gr ? "Καθυστέρηση pipeline (μ.ο. ms)" : "Pipeline latency (avg ms)",
                    String.valueOf(avg)
            );
        else
            logLabelWarnValue(
                    gr ? "Καθυστέρηση pipeline (μ.ο. ms)" : "Pipeline latency (avg ms)",
                    String.valueOf(avg)
            );
    } else {
        logLabelWarnValue(
                gr ? "Καθυστέρηση pipeline (μ.ο. ms)" : "Pipeline latency (avg ms)",
                gr ? "Μη διαθέσιμο" : "Not available"
        );
    }

    if (s.cam != null && s.cam.hasRaw)
        logLabelOkValue(
                gr ? "Υποστήριξη RAW" : "RAW support",
                gr
                        ? "ΝΑΙ — επαγγελματικές ασυμπίεστες φωτογραφίες"
                        : "YES — professional uncompressed photos"
        );
    else
        logLabelWarnValue(
                gr ? "Υποστήριξη RAW" : "RAW support",
                gr
                        ? "RAW δεν δηλώθηκε — άλλες μορφές εξόδου πιθανές"
                        : "RAW not advertised; alternate output formats may be available"
        );

    logLabelValue(gr ? "Διαγνωστική εκτίμηση" : "Diagnostic assessment",
            gr ? "Αναμονή επιβεβαίωσης χρήστη — χωρίς συμπέρασμα hardware"
               : "Awaiting human confirmation — no hardware verdict");
    logLine();
    appendHtml("<br>");
} // ✅ ΤΕΛΟΣ lab8StopAndReportSample (αυτό έλειπε)

// ============================================================
// Only the finished stream AND the subsequent user response can create a
// scoped functional observation. It does not certify camera physical health.
private void lab8FinalizePreviewOutcome(Lab8Session s, Lab8Overall overall) {
    final boolean gr = AppLang.isGreek(this);
    Lab8Evidence.Result r = Lab8Evidence.classify(
            s.repeatingRequestStarted, s.samplingComplete,
            s.frames, s.validLumaFrames, s.userConfirmedPreview);
    if (r == Lab8Evidence.Result.OBSERVED_PREVIEW_WITH_USER_CONFIRMATION) {
        overall.previewOkCount++;
        s.verdictOk = true;
        logLabelValue(gr ? "Προεπισκόπηση κάμερας" : "Camera preview",
                gr ? "Καρέ καταγράφηκαν και ο χρήστης είδε ζωντανή εικόνα"
                   : "Frames sampled and user saw live preview");
        logLabelValue(gr ? "Όρια" : "Limit",
                gr ? "Δεν πιστοποιείται φακός, autofocus, ISP ή οπτική ποιότητα"
                   : "Lens, autofocus, ISP and optical quality not certified");
    } else if (r == Lab8Evidence.Result.USER_REPORTED_NO_IMAGE) {
        overall.previewFailCount++;
        logLabelWarnValue(gr ? "Απάντηση χρήστη" : "User report",
                gr ? "Δεν είδε εικόνα — άγνωστη αιτία"
                   : "Did not see image — unverified cause");
    } else if (r == Lab8Evidence.Result.UNAVAILABLE) {
        overall.previewUnavailableCount++;
        logLabelWarnValue(gr ? "Προεπισκόπηση" : "Preview",
                gr ? "ΜΗ ΔΙΑΘΕΣΙΜΗ — χωρίς διάγνωση βλάβης"
                   : "UNAVAILABLE — no hardware verdict");
    } else {
        overall.previewInconclusiveCount++;
        logLabelWarnValue(gr ? "Προεπισκόπηση" : "Preview",
                gr ? "ΑΝΕΠΑΡΚΗ ΣΤΟΙΧΕΙΑ — χωρίς διάγνωση βλάβης"
                   : "INCONCLUSIVE — no hardware verdict");
    }
}

// LAB 8 — Close session safely
// ============================================================
private void lab8CloseSession(Lab8Session s) {
    try { if (s.session != null) s.session.close(); } catch (Throwable ignore) {}
    try { if (s.device != null) s.device.close(); } catch (Throwable ignore) {}
    try { if (s.reader != null) s.reader.close(); } catch (Throwable ignore) {}
    s.session = null;
    s.device = null;
    s.reader = null;
}

// ============================================================
// LAB 8 — Structs
// ============================================================
private static class Lab8Cam {
    String id;
    String facing;
    boolean hasFlash;
    boolean hasRaw;
    boolean hasManual;
    boolean hasDepth;
    Float focal;
    Size preview;

    Lab8Session runtimeSession;
}

private static class Lab8Overall {
    int total;
    int previewOkCount;
    int previewFailCount; // user said NO IMAGE; not evidence of broken hardware
    int previewUnavailableCount;
    int previewInconclusiveCount;
    int torchOkCount;
    int torchFailCount;
    int streamIssueCount;
}

private static class Lab8Session {
    String camId;
    CameraManager cm;
    TextureView textureView;
    Lab8Cam cam;

    CameraDevice device;
    CameraCaptureSession session;
    ImageReader reader;

    long sampleStartMs;
    volatile long frames;
    volatile long validLumaFrames;
    volatile boolean repeatingRequestStarted;
    volatile boolean samplingComplete;
    volatile boolean terminal;
    volatile boolean cancelled;
    long blackFrames;
    long droppedFrames;

    long sumLuma;
    long sumLuma2;
    int minLuma = 999;
    int maxLuma = -1;

    long latencySumMs;
    int latencyCount;

    long lastFrameTsNs;

    Boolean userConfirmedPreview = null;
    boolean verdictOk = false;
}

// ============================================================
// LAB 8.1 — PROMPT (FINAL + TTS + MUTE + GR/EN)
// ============================================================
private void showLab8_1Prompt() {

    runOnUiThread(() -> {

        final boolean gr = AppLang.isGreek(this);

        final String titleText =
                gr
                        ? "Ανάλυση Δυνατοτήτων Κάμερας"
                        : "Camera Capabilities Analysis";

        final String messageText =
        gr
                ? "Το LAB 8.1 εξηγεί, τι μπορεί πραγματικά να κάνει η κάμερά σου,\n"
                  + "με απλούς όρους.\n\n"
                  + "• Ποιότητα φωτογραφίας,\n"
                  + "• Ανάλυση & ομαλότητα βίντεο,\n"
                  + "• Επαγγελματικές δυνατότητες (RAW).\n\n"
                : "LAB 8.1 explains, what your camera can actually do,\n"
                  + "in simple terms.\n\n"
                  + "• Photo quality,\n"
                  + "• Video resolution & smoothness,\n"
                  + "• Professional features (RAW).\n\n";
                        

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        ManualTestsActivity.this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(18), dp(20), dp(16));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(4), 0xFFFFD700);
        root.setBackground(bg);

        // ---------------------------
        // TITLE
        // ---------------------------
        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextColor(Color.WHITE);
        title.setTextSize(17f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(10));
        root.addView(title);

        // ---------------------------
        // MESSAGE
        // ---------------------------
        TextView msg = new TextView(this);
msg.setText(messageText);
msg.setTextColor(0xFF39FF14); // NEON GREEN
msg.setTextSize(14f);
msg.setGravity(Gravity.CENTER);
msg.setLineSpacing(0f, 1.15f);
root.addView(msg);

        // ---------------------------
        // MUTE ROW (ABOVE BUTTON)
        // ---------------------------
        root.addView(buildMuteRow());

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setPadding(0, dp(14), 0, 0);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0, dp(54), 1f);
        lp.setMargins(dp(6), 0, dp(6), 0);

        Button yes = new com.gel.cleaner.GelButton(this);
        yes.setText(gr ? "ΣΥΝΕΧΕΙΑ" : "CONTINUE");
        yes.setAllCaps(false);
        yes.setTextColor(Color.WHITE);
        yes.setLayoutParams(lp);

        GradientDrawable yesBg = new GradientDrawable();
        yesBg.setColor(0xFF0B5F3B);
        yesBg.setCornerRadius(dp(10));
        yesBg.setStroke(dp(3), 0xFFFFD700);
        yes.setBackground(yesBg);

        Button no = new com.gel.cleaner.GelButton(this);
        no.setText(gr ? "ΠΑΡΑΛΕΙΨΗ" : "SKIP");
        no.setAllCaps(false);
        no.setTextColor(Color.WHITE);
        no.setLayoutParams(lp);

        GradientDrawable noBg = new GradientDrawable();
        noBg.setColor(0xFF8B0000);
        noBg.setCornerRadius(dp(10));
        noBg.setStroke(dp(3), 0xFFFFD700);
        no.setBackground(noBg);

        buttons.addView(yes);
        buttons.addView(no);
        root.addView(buttons);

        b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// ΣΤΑΜΑΤΑ TTS ΟΠΟΤΕ ΚΛΕΙΣΕΙ
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// ΚΑΛΥΨΗ BACK BUTTON
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK
            && event.getAction() == KeyEvent.ACTION_UP) {

        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(d);
}
        
        yes.setOnClickListener(v -> {
    AppTTS.stop();
    d.dismiss();
    startLab8_1CameraCapabilities();
});

no.setOnClickListener(v -> {
    AppTTS.stop();
    d.dismiss();
    logWarn(gr
        ? "Το LAB 8.1 παραλείφθηκε από τον χρήστη."
        : "LAB 8.1 skipped by user.");
    
    logLine();
    logOk(gr ? "Το Lab 8.1 ολοκληρώθηκε." : "Lab 8.1 finished.");
    logLine();
    enableSingleExportButton();
});
    });
}

// ============================================================
// LAB 8.1 — CAPABILITIES MAP (HUMAN FRIENDLY)
// ============================================================
private void startLab8_1CameraCapabilities() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logSection(gr
            ? "LAB 8.1 — Δυνατότητες Κάμερας"
            : "LAB 8.1 — Camera Capabilities");
    logLine();

    if (lab8CmFor81 == null || lab8CamsFor81 == null || lab8CamsFor81.isEmpty()) {

        logLabelErrorValue(
                "LAB 8.1",
                gr ? "Λείπει το context καμερών" : "Missing camera context"
        );

        logOk(gr
                ? "Παρακαλώ εκτέλεσε ξανά το LAB 8."
                : "Please re-run LAB 8.");

        logLine();
        enableSingleExportButton();
        return;
    }

    logInfo(gr
            ? "Αυτή η ενότητα εξηγεί τις δυνατότητες της κάμερας με απλά λόγια."
            : "This section explains camera abilities in plain language.");

    logLabelValue(
            gr ? "Κάμερες που ανιχνεύθηκαν" : "Cameras detected",
            String.valueOf(lab8CamsFor81.size())
    );

    logLine();

    for (Lab8Cam cam : lab8CamsFor81) {
        lab8_1DumpOneCameraCapabilities(lab8CmFor81, cam);
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 8.1 ολοκληρώθηκε." : "Lab 8.1 finished.");
    logLine();
    enableSingleExportButton();
}

// ============================================================
// LAB 8.1 — ONE CAMERA (HUMAN OUTPUT)
// ============================================================
private void lab8_1DumpOneCameraCapabilities(CameraManager cm, Lab8Cam cam) {

    final boolean gr = AppLang.isGreek(this);

    if (cm == null || cam == null || cam.id == null) return;

    appendHtml("<br>");
    logSection((gr ? "Κάμερα " : "Camera ") + cam.facing);
    logLine();

    CameraCharacteristics cc;
    try {
        cc = cm.getCameraCharacteristics(cam.id);
    } catch (Throwable t) {
        logLabelErrorValue(
                gr ? "Πληροφορίες κάμερας" : "Camera info",
                gr ? "Μη διαθέσιμες" : "Unavailable"
        );
        logLine();
        return;
    }

    // ------------------------------------------------------------
    // HUMAN FINAL VERDICT
    // ------------------------------------------------------------
    CameraHumanSummary h = buildHumanSummary(cc);

    logInfo(gr ? "ΤΕΛΙΚΟ ΑΝΘΡΩΠΙΝΟ ΣΥΜΠΕΡΑΣΜΑ" : "FINAL HUMAN VERDICT");
    logLine();

    logLabelValue(gr ? "Ποιότητα φωτογραφίας" : "Photo quality", h.photoQuality);
    logLabelValue(gr ? "Επαγγελματικές φωτογραφίες" : "Professional photos", h.professionalPhotos);
    logLabelValue(gr ? "Ποιότητα βίντεο" : "Video quality", h.videoQuality);
    logLabelValue(gr ? "Ομαλότητα βίντεο" : "Video smoothness", h.videoSmoothness);
    logLabelValue(gr ? "Αργή κίνηση (slow motion)" : "Slow motion", h.slowMotion);
    logLabelValue(gr ? "Σταθεροποίηση" : "Stabilization", h.stabilization);
    logLabelValue(gr ? "Χειροκίνητη λειτουργία" : "Manual mode", h.manualMode);
    logLabelValue(gr ? "Φλας" : "Flash", h.flash);
    logLabelValue(gr ? "Χρήση στην πράξη" : "Real life use", h.realLifeUse);

    logLine();
    logLabelOkValue(gr ? "Συμπέρασμα" : "Verdict", h.verdict);
    logLine();
}

// ============================================================
// LAB 8.1 — Helpers (NO NESTED METHODS)
// ============================================================
private Size lab8_1MaxSize(Size[] sizes) {
    if (sizes == null || sizes.length == 0) return null;
    Size best = sizes[0];
    for (Size s : sizes) {
        if (s == null) continue;
        long a = (long) s.getWidth() * (long) s.getHeight();
        long b = (long) best.getWidth() * (long) best.getHeight();
        if (a > b) best = s;
    }
    return best;
}

private String lab8_1FpsRangesToString(Range<Integer>[] rs) {
    if (rs == null || rs.length == 0) return "N/A";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < rs.length; i++) {
        Range<Integer> r = rs[i];
        if (r == null) continue;
        if (sb.length() > 0) sb.append(", ");
        sb.append(r.getLower()).append("–").append(r.getUpper());
    }
    return (sb.length() == 0) ? "N/A" : sb.toString();
}

private String lab8_1FormatList(int[] fmts, boolean hasRaw) {
    if (fmts == null || fmts.length == 0) return "N/A";
    // keep it readable, not a dump of 50 formats
    boolean hasJpeg = false, hasYuv = false, hasPrivate = false;
    for (int f : fmts) {
        if (f == ImageFormat.JPEG) hasJpeg = true;
        if (f == ImageFormat.YUV_420_888) hasYuv = true;
        if (f == ImageFormat.PRIVATE) hasPrivate = true;
    }
    StringBuilder sb = new StringBuilder();
    if (hasJpeg) sb.append("JPEG");
    if (hasYuv) { if (sb.length() > 0) sb.append(", "); sb.append("YUV_420_888"); }
    if (hasRaw) { if (sb.length() > 0) sb.append(", "); sb.append("RAW_SENSOR"); }
    if (hasPrivate) { if (sb.length() > 0) sb.append(", "); sb.append("PRIVATE"); }
    return (sb.length() == 0) ? "Available (many)" : sb.toString();
}

private String lab8_1AfModesToString(int[] modes) {
    StringBuilder sb = new StringBuilder();
    for (int m : modes) {
        String s = null;
        if (m == CaptureRequest.CONTROL_AF_MODE_OFF) s = "OFF";
        else if (m == CaptureRequest.CONTROL_AF_MODE_AUTO) s = "AUTO";
        else if (m == CaptureRequest.CONTROL_AF_MODE_MACRO) s = "MACRO";
        else if (m == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO) s = "CONTINUOUS_VIDEO";
        else if (m == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE) s = "CONTINUOUS_PICTURE";
        else if (m == CaptureRequest.CONTROL_AF_MODE_EDOF) s = "EDOF";
        else s = "MODE_" + m;

        if (sb.length() > 0) sb.append(", ");
        sb.append(s);
    }
    return (sb.length() == 0) ? "N/A" : sb.toString();
}

private String lab8_1AeModesToString(int[] modes) {
    StringBuilder sb = new StringBuilder();
    for (int m : modes) {
        String s;
        if (m == CaptureRequest.CONTROL_AE_MODE_OFF) s = "OFF";
        else if (m == CaptureRequest.CONTROL_AE_MODE_ON) s = "ON";
        else if (m == CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH) s = "ON_AUTO_FLASH";
        else if (m == CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH) s = "ON_ALWAYS_FLASH";
        else if (m == CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE) s = "ON_REDEYE";
        else s = "MODE_" + m;

        if (sb.length() > 0) sb.append(", ");
        sb.append(s);
    }
    return (sb.length() == 0) ? "N/A" : sb.toString();
}

private String lab8_1AwbModesToString(int[] modes) {
    StringBuilder sb = new StringBuilder();
    for (int m : modes) {
        String s;
        if (m == CaptureRequest.CONTROL_AWB_MODE_OFF) s = "OFF";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_AUTO) s = "AUTO";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT) s = "INCANDESCENT";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT) s = "FLUORESCENT";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_WARM_FLUORESCENT) s = "WARM_FLUORESCENT";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT) s = "DAYLIGHT";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT) s = "CLOUDY";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_TWILIGHT) s = "TWILIGHT";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_SHADE) s = "SHADE";
        else s = "MODE_" + m;

        if (sb.length() > 0) sb.append(", ");
        sb.append(s);
    }
    return (sb.length() == 0) ? "N/A" : sb.toString();
}

private String lab8_1VideoStabToString(int[] modes) {
    StringBuilder sb = new StringBuilder();
    for (int m : modes) {
        String s;
        if (m == CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_OFF) s = "OFF";
        else if (m == CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON) s = "ON";
        else s = "MODE_" + m;

        if (sb.length() > 0) sb.append(", ");
        sb.append(s);
    }
    return (sb.length() == 0) ? "N/A" : sb.toString();
}

/* ============================================================
LAB 9 — Sensors Check (LABEL / VALUE MODE)
============================================================ */
private void lab9SensorsCheck() {

    final boolean gr = AppLang.isGreek(this);
    // Do not allow exporting the report before this asynchronous lab finishes.
    boolean samplingScheduled = false;
    ui.post(() -> {
        if (btnExport != null) {
            btnExport.setEnabled(false);
            btnExport.setAlpha(0.5f);
        }
    });

    appendHtml("<br>");
    logLine();
    logSection(gr
            ? "LAB 9 — Αισθητήρες (Παρουσία & Πλήρης Ανάλυση)"
            : "LAB 9 — Sensors Presence & Full Analysis");
    logLine();

    try {

        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sm == null) {
            logLabelErrorValue(
                    gr ? "SensorManager" : "SensorManager",
                    gr ? "Μη διαθέσιμο (πρόβλημα framework)" : "Not available (framework issue)"
            );
            return;
        }

        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
        int total = (sensors == null ? 0 : sensors.size());

        logLabelOkValue(
                gr ? "Σύνολο αισθητήρων" : "Total sensors reported",
                String.valueOf(total)
        );

        // ------------------------------------------------------------
        // QUICK PRESENCE CHECK
        // ------------------------------------------------------------
        checkSensor(sm, Sensor.TYPE_ACCELEROMETER, gr ? "Επιταχυνσιόμετρο" : "Accelerometer");
        checkSensor(sm, Sensor.TYPE_GYROSCOPE, gr ? "Γυροσκόπιο" : "Gyroscope");
        checkSensor(sm, Sensor.TYPE_MAGNETIC_FIELD, gr ? "Μαγνητόμετρο / Πυξίδα" : "Magnetometer / Compass");
        checkSensor(sm, Sensor.TYPE_LIGHT, gr ? "Αισθητήρας φωτός" : "Ambient Light");
        checkSensor(sm, Sensor.TYPE_PROXIMITY, gr ? "Εγγύτητας" : "Proximity");

        if (sensors == null || sensors.isEmpty()) {
            logLabelErrorValue(
                    gr ? "Λίστα αισθητήρων" : "Sensor list",
                    gr ? "Δεν αναφέρθηκαν αισθητήρες από το σύστημα" : "No sensors reported by the system"
            );
            return;
        }

        logLine();

        // ------------------------------------------------------------
        // RAW SENSOR LIST
        // ------------------------------------------------------------
        for (Sensor s : sensors) {
            logOk(
                    gr ? "Αισθητήρας" : "Sensor",
                    "type=" + s.getType()
                            + " | name=" + s.getName()
                            + " | vendor=" + s.getVendor()
            );
        }

        // ------------------------------------------------------------
        // INTERPRETATION LOGIC
        // ------------------------------------------------------------
        boolean hasVirtualGyro = false;
        int alsCount = 0;
        boolean hasSAR = false;
        boolean hasPickup = false;
        boolean hasLargeTouch = false;
        boolean hasGameRotation = false;

        for (Sensor s : sensors) {
            String name   = s.getName()   != null ? s.getName().toLowerCase(Locale.US)   : "";

            if (name.contains("virtual") && name.contains("gyro"))
                hasVirtualGyro = true;

            if (name.contains("ambient") && name.contains("light"))
                alsCount++;

            if (name.contains("sar") || name.contains("rf"))
                hasSAR = true;

            if (name.contains("pickup"))
                hasPickup = true;

            if (name.contains("touch") && name.contains("large"))
                hasLargeTouch = true;

            if (name.contains("game") && name.contains("rotation"))
                hasGameRotation = true;
        }

        // ------------------------------------------------------------
        // SENSOR INTERPRETATION SUMMARY — ONE LINE PER ITEM
        // ------------------------------------------------------------
        logLine();

        // OEM-provided names are metadata, not proof of physical components,
        // calibration or implemented system features. Use neutral labels.
        logLabelValue(gr ? "Εικονικό γυροσκόπιο (όνομα)" : "Virtual gyro (name)",
                hasVirtualGyro
                        ? (gr ? "Όνομα με virtual/gyro· φυσικό chip μη επαληθευμένο"
                              : "Virtual/gyro descriptor; physical chip unverified")
                        : (gr ? "Δεν αναφέρθηκε τέτοιο όνομα· τύπος hardware άγνωστος"
                              : "No such descriptor; physical hardware type unknown"));

        logLabelValue(gr ? "Αισθητήρες φωτός (ονόματα)" : "Ambient light (names)",
                alsCount >= 2
                        ? (gr ? "Πολλαπλά σχετικά ονόματα· θέση και λειτουργία μη επαληθευμένες"
                              : "Multiple related names; location/function unverified")
                        : alsCount == 1
                        ? (gr ? "Ένα σχετικό όνομα· δεν αποκλείονται άλλοι αισθητήρες"
                              : "One matching name; additional sensors not ruled out")
                        : (gr ? "Δεν εντοπίστηκε όνομα ALS· δεν αποδεικνύεται απουσία"
                              : "No ALS-like name matched; absence not established"));

        logLabelValue(gr ? "Αισθητήρες SAR/RF (ονόματα)" : "SAR/RF (names)",
                hasSAR
                        ? (gr ? "Εντοπίστηκε σχετικό όνομα· RF λειτουργία μη ελεγμένη"
                              : "Related name reported; RF function untested")
                        : (gr ? "Δεν εντοπίστηκε σχετικό όνομα· όχι απόδειξη απουσίας"
                              : "No related name matched; absence unverified"));

        logLabelValue(gr ? "Pickup (όνομα)" : "Pickup (name)",
                hasPickup
                        ? (gr ? "Σχετικό όνομα· δεν ελέγχθηκε το lift-to-wake"
                              : "Related descriptor; lift-to-wake not tested")
                        : (gr ? "Δεν αναφέρθηκε σχετικό όνομα"
                              : "No matching descriptor reported"));

        logLabelValue(gr ? "Large-area touch (όνομα)" : "Large-area touch (name)",
                hasLargeTouch
                        ? (gr ? "Σχετικό όνομα· palm rejection/ακρίβεια μη ελεγμένα"
                              : "Related descriptor; palm rejection/accuracy untested")
                        : (gr ? "Δεν αναφέρθηκε σχετικό όνομα"
                              : "No matching descriptor reported"));

        logLabelValue(gr ? "Game rotation vector (όνομα)" : "Game rotation (name)",
                hasGameRotation
                        ? (gr ? "Σχετικό όνομα· δεν έγινε λειτουργική επαλήθευση"
                              : "Related descriptor; functionality not tested")
                        : (gr ? "Δεν αναφέρθηκε σχετικό όνομα"
                              : "No matching descriptor reported"));

        // Enumeration proves only that Android advertises a sensor.
        // It is not a live functional challenge or hardware-health certificate.
        logLabelWarnValue(
                gr ? "Συνολική εκτίμηση" : "Overall assessment",
                gr
                        ? "Καταγράφηκαν οι αναφερόμενοι αισθητήρες. Η λειτουργία τους "
                                + "δεν επαληθεύεται μόνο από τη λίστα."
                        : "Android-reported sensors were enumerated. Their operation "
                                + "cannot be verified from enumeration alone."
        );

        logInfo(gr
                ? "Κίνησε απαλά το κινητό και κάλυψε/αποκάλυψε τον proximity "
                        + "για 5 δευτερόλεπτα. Η απουσία αλλαγής δεν σημαίνει βλάβη."
                : "Gently move the device and cover/uncover the proximity area "
                        + "for 5 seconds. No observed change does not prove failure.");
        SensorEvidenceProbe.sample(this, samples -> {
            if (isFinishing() || isDestroyed()) return;
            appendHtml("<br>");
            logInfo(gr ? "Συμβάντα αισθητήρων (5 δευτ.)"
                       : "Sensor event observations (5 seconds)");
            for (SensorEvidenceProbe.Result sample : samples) {
                if (!sample.advertised || !sample.listenerRegistered) {
                    logLabelWarnValue(sample.name,
                            gr ? "Δεν διατέθηκε ροή συμβάντων"
                               : "Event stream not available");
                } else if (sample.events == 0) {
                    logLabelWarnValue(sample.name,
                            gr ? "Μηδέν συμβάντα στο δείγμα — όχι τεκμήριο φυσικής βλάβης"
                               : "No events during window — not proof of physical failure");
                } else {
                    logLabelValue(sample.name,
                            sample.events + " events"
                            + (sample.observedVariation
                                    ? (gr ? " | μεταβολή: " : " | varied: ") + sample.variedAxes
                                    : (gr ? " | δεν παρατηρήθηκε μεταβολή"
                                          : " | no variation observed"))
                            + (sample.monotonicTimestamps ? ""
                                    : (gr ? " | ανωμαλία χρονικής σειράς"
                                          : " | timestamp order anomaly")));
                }
                if (sample.invalidEvents > 0) {
                    logLabelWarnValue(gr ? "Μη έγκυρα δείγματα" : "Invalid samples",
                            sample.name + ": " + sample.invalidEvents);
                }
            }
            logLabelWarnValue(gr ? "Όρια καταγραφής" : "Sampling limitation",
                    gr ? "Περιορισμένη χρονικά καταγραφή API, όχι βαθμονόμηση ή "
                            + "πιστοποίηση λειτουργίας όλων των αισθητήρων."
                       : "Time-bounded API observation, not calibration or "
                            + "whole-device sensor-health certification.");
            enableSingleExportButton();
        });
        samplingScheduled = true;

    } catch (Throwable e) {
        logError(gr ? "Σφάλμα ανάλυσης αισθητήρων" : "Sensors analysis error", e.getMessage());
    } finally {
        appendHtml("<br>");
        logInfo(gr ? "Η απογραφή Lab 9 ολοκληρώθηκε· ακολουθεί το δείγμα συμβάντων."
                   : "Lab 9 inventory completed; event sample follows.");
        logLine();
        // Early returns and setup errors have no asynchronous observation pending.
        if (!samplingScheduled) enableSingleExportButton();
    }
}

/* ============================================================
Helper — Sensor Presence
============================================================ */
private void checkSensor(SensorManager sm, int type, String name) {

    final boolean gr = AppLang.isGreek(this);

    boolean ok = sm.getDefaultSensor(type) != null;

    if (ok) {
        logLabelOkValue(
                name,
                gr ? "Διαθέσιμος" : "Available"
        );
    } else {
        logLabelWarnValue(
                name,
                gr
                        ? "Δεν αναφέρθηκε (ενδέχεται περιορισμένες ή μη διαθέσιμες λειτουργίες)"
                        : "Not reported (dependent features may be limited or missing)"
        );
    }
}

// ============================================================
// LAB 10: Wi-Fi Connectivity Check (Wi-Fi + Internet + Exposure)
// ============================================================
private void lab10WifiConnectivityCheck() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 10 — Έλεγχος Συνδεσιμότητας Wi-Fi" : "LAB 10 — Wi-Fi Link Connectivity Check");
    logLine();

    WifiManager wm =
            (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

    if (wm == null) {
        logError(gr ? "WifiManager δεν είναι διαθέσιμο." : "WifiManager not available.");
        return;
    }

    if (!wm.isWifiEnabled()) {
        logWarn(gr ? "Το Wi-Fi είναι ΚΛΕΙΣΤΟ — άνοιξέ το και ξαναδοκίμασε." : "Wi-Fi is OFF — please enable and retry.");
        return;
    }

    // ------------------------------------------------------------
    // 1) Location permission (SSID policy)
    // ------------------------------------------------------------
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        boolean fineGranted =
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        boolean coarseGranted =
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (!fineGranted && !coarseGranted) {

            pendingLab10AfterPermission = this::lab10WifiConnectivityCheck;

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQ_LOCATION_LAB10
            );
            return;
        }

        try {
            LocationManager lm =
                    (LocationManager) getSystemService(LOCATION_SERVICE);

            boolean gpsOn =
                    lm != null &&
                            (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                                    || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

            if (!gpsOn) {
                logWarn(gr ? "Οι Υπηρεσίες Τοποθεσίας είναι OFF. Το SSID μπορεί να φαίνεται UNKNOWN."
                           : "Location services are OFF. SSID may be UNKNOWN.");
            }

        } catch (Throwable e) {
            logWarn((gr ? "Έλεγχος Location απέτυχε: " : "Location services check failed: ") + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // 2) Wi-Fi snapshot
    // ------------------------------------------------------------
    WifiInfo info = wm.getConnectionInfo();
    if (info == null) {
        logLabelErrorValue(gr ? "Wi-Fi" : "Wi-Fi", gr ? "Δεν υπάρχουν στοιχεία σύνδεσης" : "Connection info not available");
        return;
    }

    String ssid  = cleanSsid(info.getSSID());
    String bssid = info.getBSSID();
    int rssi     = info.getRssi();
    int speed    = info.getLinkSpeed();

    int freqMhz = 0;
    try { freqMhz = info.getFrequency(); } catch (Throwable ignore) {}

    String band = freqMhz >= 5925 && freqMhz <= 7125 ? "6 GHz"
            : freqMhz >= 4900 && freqMhz < 5925 ? "5 GHz"
            : freqMhz >= 2400 && freqMhz < 2500 ? "2.4 GHz"
            : (gr ? "Άγνωστη" : "Unknown");

    // ---------------- IDENTIFIERS ----------------
    logLabelValue(gr ? "SSID" : "SSID", ssid);

    if (bssid != null)
        logLabelValue(gr ? "BSSID" : "BSSID", bssid);

    // ---------------- BAND ----------------
    logLabelOkValue(
            gr ? "Μπάντα" : "Band",
            band + (freqMhz > 0 ? " (" + freqMhz + " MHz)" : "")
    );

    // ---------------- LINK SPEED ----------------
    if (speed >= 150) {
        logLabelOkValue(gr ? "Διαπραγματευμένη ταχύτητα Wi-Fi (όχι Internet)" : "Negotiated Wi-Fi PHY rate (not Internet throughput)", speed + " Mbps");
    } else if (speed >= 54) {
        logLabelWarnValue(gr ? "Διαπραγματευμένη ταχύτητα Wi-Fi (όχι Internet)" : "Negotiated Wi-Fi PHY rate (not Internet throughput)", speed + " Mbps");
    } else {
        logLabelErrorValue(gr ? "Διαπραγματευμένη ταχύτητα Wi-Fi (όχι Internet)" : "Negotiated Wi-Fi PHY rate (not Internet throughput)", speed + " Mbps");
    }

    // ---------------- SIGNAL (RSSI) ----------------
    if (rssi >= -60) {
        logLabelOkValue(gr ? "Ισχύς Σήματος" : "Signal strength", rssi + " dBm");
    } else if (rssi >= -75) {
        logLabelWarnValue(gr ? "Ισχύς Σήματος" : "Signal strength", rssi + " dBm");
    } else {
        logLabelErrorValue(gr ? "Ισχύς Σήματος" : "Signal strength", rssi + " dBm");
    }

    // SSID status — single line
    if ("Unknown".equalsIgnoreCase(ssid)) {
        logLabelWarnValue(gr ? "SSID" : "SSID", gr ? "Κρυφό από Android policy απορρήτου" : "Hidden by Android privacy policy");
    } else {
        logLabelOkValue(gr ? "SSID" : "SSID", gr ? "Ανάγνωση OK" : "Read OK");
    }

    // Signal quality — single line
    if (rssi > -65)
        logLabelOkValue(gr ? "Σήμα Wi-Fi" : "Wi-Fi signal", gr ? "Ισχυρό" : "Strong");
    else if (rssi > -80)
        logLabelWarnValue(gr ? "Σήμα Wi-Fi" : "Wi-Fi signal", gr ? "Μέτριο" : "Moderate");
    else
        logLabelErrorValue(gr ? "Σήμα Wi-Fi" : "Wi-Fi signal", gr ? "Αδύναμο" : "Weak");

    // ------------------------------------------------------------
    // 3) DHCP / LAN info — unified label/value format
    // ------------------------------------------------------------
    try {
        DhcpInfo dh = wm.getDhcpInfo();

        if (dh != null) {
            logLabelOkValue(gr ? "IP" : "IP",           ipToStr(dh.ipAddress));
            logLabelOkValue(gr ? "Gateway" : "Gateway", ipToStr(dh.gateway));
            logLabelOkValue(gr ? "DNS1" : "DNS1",       ipToStr(dh.dns1));
            logLabelOkValue(gr ? "DNS2" : "DNS2",       ipToStr(dh.dns2));
        } else {
            logLabelWarnValue(gr ? "DHCP" : "DHCP", gr ? "Δεν υπάρχουν στοιχεία" : "Info not available");
        }

    } catch (Throwable e) {
        logLabelErrorValue(gr ? "DHCP" : "DHCP", (gr ? "Αποτυχία ανάγνωσης: " : "Read failed: ") + e.getMessage());
    }

    // ------------------------------------------------------------
    // 4) DeepScan + Internet + Exposure
    // ------------------------------------------------------------
    runWifiDeepScan(wm);
}
@Override
public void onRequestPermissionsResult(
        int requestCode,
        String[] permissions,
        int[] grantResults) {

    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    final boolean gr = AppLang.isGreek(this);

    // =========================
    // CORE PERMISSIONS
    // =========================
    if (requestCode == REQ_CORE_PERMS) {

        boolean allGranted = true;

        if (grantResults.length == 0) {
            allGranted = false;
        } else {
            for (int r : grantResults) {
                if (r != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
        }

        if (allGranted) {

            logOk(gr ? "Δόθηκαν τα απαιτούμενα permissions." : "Required permissions granted.");

            if (pendingAfterPermission != null) {
                Runnable action = pendingAfterPermission;
                pendingAfterPermission = null;
                action.run();
            }

        } else {

            logLabelErrorValue(
                    gr ? "Permissions" : "Permissions",
                    gr ? "Αρνήθηκαν τα απαιτούμενα permissions" : "Required permissions denied"
            );

            pendingAfterPermission = null;
        }

        return;
    }

    // =========================
    // LAB 10 - LOCATION (WiFi SSID)
    // =========================
    if (requestCode == REQ_LOCATION_LAB10) {

        boolean granted = false;
        if (grantResults != null && grantResults.length > 0) {
            // accepted if ANY requested location perm granted
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }
        }

        if (granted && pendingLab10AfterPermission != null) {

            Runnable action = pendingLab10AfterPermission;
            pendingLab10AfterPermission = null;
            action.run();

        } else {

            logLabelErrorValue(
                    gr ? "Άδεια Τοποθεσίας" : "Location Permission",
                    gr ? "Αρνήθηκε" : "Denied"
            );

            pendingLab10AfterPermission = null;
        }

        return;
    }

    // =========================
    // LAB 13 BLUETOOTH
    // =========================
    if (requestCode == REQ_LAB13_BT_CONNECT) {

        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            lab13Running = true;
            lab13BluetoothConnectivityCheck();

        } else {

            lab13Running = false;

            logLabelErrorValue(
                    gr ? "Άδεια Bluetooth" : "Bluetooth Permission",
                    gr ? "Αρνήθηκε" : "Denied"
            );

            appendHtml("<br>");
            logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
            logLine();
        }

        return;
    }
}

// ============================================================
// LAB 10 — DEEPSCAN v3.0 (Internet + Exposure included)
// ============================================================
private void runWifiDeepScan(WifiManager wm) {

    new Thread(() -> {

        final boolean gr = AppLang.isGreek(ManualTestsActivity.this);

        try {
            logLine();
            logOk(gr ? "Network DeepScan v3.0 ξεκίνησε..." : "Network DeepScan v3.0 started...");

            String gatewayStr = null;
            try {
                DhcpInfo dh = wm.getDhcpInfo();
                if (dh != null)
                    gatewayStr = ipToStr(dh.gateway);
            } catch (Throwable ignored) {}

            // ----------------------------------------------------
            // NETWORK DEEP SCAN — unified label/value format
            // ----------------------------------------------------

            // 1) Internet ping
            float pingMs = tcpLatencyMs("8.8.8.8", 53, 1500);
            if (pingMs > 0)
                logLabelOkValue(gr ? "Ping 8.8.8.8" : "Ping 8.8.8.8", String.format(Locale.US, "%.1f ms", pingMs));
            else
                logLabelWarnValue(gr ? "Ping 8.8.8.8" : "Ping 8.8.8.8", gr ? "Απέτυχε" : "Failed");

            // 2) DNS resolve
            float dnsMs = dnsResolveMs("google.com");
            if (dnsMs > 0)
                logLabelOkValue(gr ? "DNS google.com" : "DNS google.com", String.format(Locale.US, "%.0f ms", dnsMs));
            else
                logLabelWarnValue(gr ? "DNS google.com" : "DNS google.com", gr ? "Απέτυχε" : "Resolve failed");

            // 3) Gateway ping
            if (gatewayStr != null) {
                float gwMs = tcpLatencyMs(gatewayStr, 80, 1200);
                if (gwMs > 0)
                    logLabelOkValue(gr ? "Ping Gateway" : "Gateway ping", String.format(Locale.US, "%.1f ms", gwMs));
                else
                    logLabelWarnValue(gr ? "Ping Gateway" : "Gateway ping", gr ? "Απέτυχε" : "Failed");
            } else {
                logLabelWarnValue(gr ? "Gateway" : "Gateway", gr ? "Δεν εντοπίστηκε" : "Not detected");
            }

            // 4) SpeedSim legacy was not a data-transfer measurement. Preserve
            // real Android PHY/RSSI evidence, never invent a 72/5 Mbps speed.
            WifiInfo info = wm.getConnectionInfo();
            WifiLinkEvidence linkEvidence = WifiLinkEvidence.fromAndroid(
                    info != null ? info.getLinkSpeed() : -1,
                    info != null ? info.getRssi() : 127);
            if (linkEvidence.hasPhyRate()) {
                logLabelValue(gr ? "Διαπραγματευμένος ρυθμός Wi-Fi"
                                 : "Negotiated Wi-Fi link rate",
                        linkEvidence.phyRateMbps + " Mbps — "
                                + (gr ? "αναφερόμενο PHY, όχι πραγματικό Internet"
                                      : "Android-reported PHY, not Internet throughput"));
            } else {
                logLabelWarnValue(gr ? "Ρυθμός Wi-Fi" : "Wi-Fi link rate",
                        gr ? "Μη διαθέσιμος / μη έγκυρος" : "Unavailable / invalid");
            }
            if (linkEvidence.hasRssi()) {
                logLabelValue(gr ? "Ισχύς συνδέσμου (Android)" : "Link RSSI (Android)",
                        linkEvidence.rssiDbm + " dBm");
            }
            logLabelWarnValue(gr ? "SpeedSim — εκτίμηση ταχύτητας Internet"
                                 : "SpeedSim — Internet throughput estimate",
                    gr ? "ΔΕΝ ΜΕΤΡΗΘΗΚΕ — PHY/RSSI δεν πιστοποιούν ρυθμό μεταφοράς δεδομένων"
                       : "NOT MEASURED — PHY/RSSI cannot certify data transfer throughput");

            // 5) OS-level default-network validation, transport and captive portal.
            // NET_CAPABILITY_INTERNET means the network is configured for internet;
            // only NET_CAPABILITY_VALIDATED is Android's validation observation.
            // This does not independently prove a working HTTPS transaction.
            try {
                ConnectivityManager cm = (ConnectivityManager)
                        getSystemService(CONNECTIVITY_SERVICE);
                if (cm == null) {
                    logLabelWarnValue(gr ? "Συνδεσιμότητα" : "Connectivity",
                            gr ? "Υπηρεσία μη διαθέσιμη" : "Service unavailable");
                } else {
                    Network activeNetwork = cm.getActiveNetwork();
                    NetworkCapabilities caps = activeNetwork != null
                            ? cm.getNetworkCapabilities(activeNetwork) : null;
                    if (caps == null) {
                        logLabelWarnValue(gr ? "Προεπιλεγμένη διαδρομή δικτύου"
                                              : "Active default network",
                                gr ? "Άγνωστη / χωρίς στοιχεία" : "Unknown / no capabilities");
                    } else {
                        boolean viaWifi =
                                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                        boolean viaCellular =
                                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                        boolean viaVpn =
                                caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
                        boolean configuredForInternet = caps.hasCapability(
                                NetworkCapabilities.NET_CAPABILITY_INTERNET);
                        boolean osValidated = caps.hasCapability(
                                NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                        boolean captive = caps.hasCapability(
                                NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL);
                        String transport = viaVpn ? "VPN"
                                : viaWifi ? "Wi-Fi"
                                : viaCellular ? (gr ? "Κινητό δίκτυο" : "Cellular")
                                : (gr ? "Άλλο / άγνωστο" : "Other / unknown");

                        logLabelValue(gr ? "Ενεργή προεπιλεγμένη διαδρομή"
                                         : "Active default network transport", transport);
                        if (viaCellular || viaVpn || !viaWifi) {
                            logLabelWarnValue(gr ? "Περιορισμός δοκιμής Wi-Fi"
                                                  : "Wi-Fi test attribution",
                                    gr ? "Η προεπιλεγμένη διαδρομή δεν είναι επαληθευμένα απευθείας Wi-Fi· "
                                            + "οι εξωτερικές δοκιμές μπορεί να χρησιμοποιούν άλλο δίκτυο."
                                       : "Default route is not verified as direct Wi-Fi; "
                                            + "external probes may use another network.");
                        }
                        if (captive) {
                            logLabelWarnValue(gr ? "Captive portal" : "Captive portal",
                                    gr ? "Το Android αναφέρει πιθανή πύλη σύνδεσης"
                                       : "Android reports a captive portal");
                        }
                        if (osValidated) {
                            logLabelOkValue(gr ? "Επικύρωση δικτύου από Android"
                                               : "Android network validation",
                                    gr ? "Επικυρωμένη προεπιλεγμένη σύνδεση"
                                       : "Default network validated by Android");
                        } else if (configuredForInternet) {
                            logLabelWarnValue(gr ? "Επικύρωση δικτύου από Android"
                                                  : "Android network validation",
                                    gr ? "Δηλωμένη δυνατότητα Internet, όχι επικυρωμένη"
                                       : "Internet capability declared, not OS validated");
                        } else {
                            logLabelWarnValue(gr ? "Επικύρωση δικτύου από Android"
                                                  : "Android network validation",
                                    gr ? "Δεν τεκμηριώνεται επικυρωμένη πρόσβαση Internet"
                                       : "No OS-validated internet access evidenced");
                        }
                    }
                }
            } catch (RuntimeException unavailable) {
                logLabelWarnValue(gr ? "Δικτυακός έλεγχος Android" : "Android network check",
                        gr ? "Η υπηρεσία δεν επέστρεψε διαθέσιμα στοιχεία"
                           : "OS network capabilities unavailable");
            }

            logLabelValue(gr ? "Όριο ελέγχου DeepScan" : "DeepScan limitation",
                    gr ? "Τα TCP/DNS αιτήματα ελέγχουν συγκεκριμένες υπηρεσίες και διαδρομές."
                            + " Η αποτυχία TCP/53 δεν αποδεικνύει βλάβη Wi-Fi."
                       : "TCP/DNS probes measure specific endpoints and routes."
                            + " A blocked TCP/53 port does not prove Wi-Fi failure.");
            logLabelValue(gr ? "DeepScan" : "DeepScan",
                    gr ? "Ο έλεγχος ολοκληρώθηκε με περιορισμούς" : "Check completed with limitations");

            // ====================================================
            // NETWORK / PRIVACY EXPOSURE
            // ====================================================
            try {
                logLine();
                logInfo(gr ? "Αποτύπωση Έκθεσης Δικτύου & Ιδιωτικότητας"
           : "Network / Privacy Exposure Snapshot");
                logInfo(gr
        ? "(Έλεγχος δηλωμένων δυνατοτήτων — χωρίς επιθεώρηση traffic)"
        : "(Capabilities only — no traffic inspection)");

                PackageManager pm2 = getPackageManager();
                ApplicationInfo ai = getApplicationInfo();

                // INTERNET PERMISSION
                boolean hasInternetPerm =
                        pm2.checkPermission(
                                Manifest.permission.INTERNET,
                                ai.packageName
                        ) == PackageManager.PERMISSION_GRANTED;

                logLabelValue(
                        gr ? "Δυνατότητα Internet" : "Internet capability",
                        hasInternetPerm
                                ? (gr ? "Άδεια INTERNET δηλωμένη" : "INTERNET permission declared")
                                : (gr ? "Δεν δηλώθηκε άδεια INTERNET" : "No INTERNET permission declared")
                );

                // CLEARTEXT TRAFFIC
                boolean cleartextAllowed = true;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cleartextAllowed =
                                android.security.NetworkSecurityPolicy
                                        .getInstance()
                                        .isCleartextTrafficPermitted();
                    }
                } catch (Throwable ignore) {}

                if (cleartextAllowed)
                    logLabelWarnValue(
    gr ? "Cleartext traffic (μη κρυπτογραφημένη μεταφορά δεδομένων)"
       : "Cleartext traffic",
    gr ? "Επιτρέπεται από το Network Security Policy"
       : "Allowed by network security policy"
);
                else
                    logLabelOkValue(
                            gr ? "Cleartext traffic (μη κρυπτογραφημένη μεταφορά δεδομένων)" : "Cleartext traffic",
                            gr ? "Δεν επιτρέπεται (enforced encryption)" : "Not allowed (encrypted traffic enforced)"
                    );

                // BACKGROUND NETWORK (BOOT)
                boolean bgPossible =
                        pm2.checkPermission(
                                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                                ai.packageName
                        ) == PackageManager.PERMISSION_GRANTED;

                logLabelValue(
                        gr ? "Δυνατότητα εκτέλεσης δικτύου στο παρασκήνιο" : "Background network capability",
                        bgPossible
                                ? (gr ? "Δηλωμένο RECEIVE_BOOT_COMPLETED (εκκίνηση στο boot)"
              : "RECEIVE_BOOT_COMPLETED declared")
        : (gr ? "Δεν υπάρχει BOOT RECEIVER δηλωμένο"
              : "No BOOT-TIME RECEIVER declared")
);

                logLabelOkValue(
        gr ? "Συνολική Εκτίμηση" : "Assessment",
        gr ? "Ολοκληρώθηκε η αποτύπωση έκθεσης δικτύου & ιδιωτικότητας"
           : "Network / privacy exposure snapshot completed"
);

} catch (Throwable e) {
    logLabelWarnValue(
            gr ? "Έκθεση Δικτύου" : "Network exposure",
            (gr ? "Μη διαθέσιμο: " : "Snapshot unavailable: ")
                    + (e.getMessage() != null ? e.getMessage()
                                              : (gr ? "Άγνωστο σφάλμα"
                                                    : "Unknown error"))
    );
}

            appendHtml("<br>");
            logOk(gr ? "Το Lab 10 ολοκληρώθηκε." : "Lab 10 finished.");
            logLine();

        } catch (Throwable e) {

            logLine();
            logInfo(gr ? "DeepScan" : "DeepScan");

            logLabelErrorValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Απέτυχε" : "Failed"
            );

            logLabelWarnValue(
                    gr ? "Αιτία" : "Reason",
                    e.getMessage() != null ? e.getMessage() : (gr ? "Άγνωστο σφάλμα" : "Unknown error")
            );
        }

    }).start();
}

// ============================================================
// LAB 11 — Mobile Data Diagnostic
// ============================================================
private void lab11MobileDataDiagnostic() {
    final boolean gr = AppLang.isGreek(this);
    appendHtml("<br>");
    logLine();
    logSection(gr ? "LAB 11 — Κινητό Δίκτυο: Παρατηρήσεις Android"
                  : "LAB 11 — Mobile Network: Android observations");
    logLine();

    // A passive snapshot of the current DEFAULT subscription and network,
    // not a physical modem diagnostic or a forced cellular connection.
    TelephonySnapshot s = getTelephonySnapshot();

    logLabelValue(gr ? "Λειτουργία πτήσης" : "Airplane mode",
            s.airplaneOn ? (gr ? "Ενεργή" : "Enabled")
                         : (gr ? "Ανενεργή" : "Disabled"));
    if (s.airplaneOn) {
        logLabelWarnValue(gr ? "Έλεγχος κυψελωτού δικτύου" : "Cellular assessment",
                gr ? "ΠΕΡΙΟΡΙΣΜΕΝΟΣ — λειτουργία πτήσης, όχι ένδειξη βλάβης"
                   : "BLOCKED by airplane mode — not a hardware defect");
    }

    // SIM not ready can mean absent, locked, unavailable or OEM/eSIM behavior.
    // None of these proves a failed baseband/modem.
    String simDescription;
    if (!s.simStateKnown) {
        simDescription = gr ? "ΑΓΝΩΣΤΗ / ΜΗ ΔΙΑΘΕΣΙΜΗ" : "UNKNOWN / UNAVAILABLE";
    } else {
        switch (s.simState) {
            case TelephonyManager.SIM_STATE_READY:
                simDescription = gr ? "ΕΤΟΙΜΗ (προεπιλεγμένη SIM)" : "READY (default SIM)";
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                simDescription = gr ? "ΑΠΟΥΣΑ (στο εκτεθειμένο προφίλ)"
                                    : "ABSENT (reported subscription)";
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                simDescription = gr ? "ΑΠΑΙΤΕΙΤΑΙ PIN" : "PIN REQUIRED";
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                simDescription = gr ? "ΑΠΑΙΤΕΙΤΑΙ PUK" : "PUK REQUIRED";
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                simDescription = gr ? "ΚΛΕΙΔΩΜΕΝΗ ΣΤΟ ΔΙΚΤΥΟ" : "NETWORK LOCKED";
                break;
            default:
                simDescription = gr ? "ΜΗ ΕΤΟΙΜΗ / ΜΕΡΙΚΗ" : "NOT READY / PARTIAL";
        }
    }
    logLabelValue(gr ? "SIM / προεπιλεγμένη συνδρομή" : "SIM / default subscription",
            simDescription);

    if (s.serviceKnown) {
        logLabelValue(gr ? "Κατάσταση υπηρεσίας (legacy)" : "Service state (legacy)",
                s.inService ? (gr ? "Αναφέρθηκε IN SERVICE" : "Reported IN SERVICE")
                            : (gr ? "Δεν αναφέρθηκε IN SERVICE" : "Not reported IN SERVICE"));
        if (!s.inService) {
            logInfo(gr ? "Δεν αποδεικνύεται βλάβη modem ούτε συνολική απουσία IMS/VoLTE."
                       : "Does not prove modem failure or absence of IMS/VoLTE.");
        }
    } else {
        logLabelWarnValue(gr ? "Κατάσταση υπηρεσίας" : "Service state",
                gr ? "ΑΓΝΩΣΤΗ — άδεια, API, συνδρομή ή OEM"
                   : "UNKNOWN — permission, API, subscription or OEM limits");
    }

    String dataLabel;
    if (!s.dataStateKnown) {
        dataLabel = gr ? "ΑΓΝΩΣΤΗ" : "UNKNOWN";
    } else {
        switch (s.dataState) {
            case TelephonyManager.DATA_CONNECTED:
                dataLabel = gr ? "Αναφέρεται CONNECTED" : "Reported CONNECTED";
                break;
            case TelephonyManager.DATA_CONNECTING:
                dataLabel = gr ? "Αναφέρεται CONNECTING" : "Reported CONNECTING";
                break;
            case TelephonyManager.DATA_DISCONNECTED:
                dataLabel = gr ? "Αναφέρεται DISCONNECTED" : "Reported DISCONNECTED";
                break;
            default:
                dataLabel = gr ? "Άλλη / μη καθορισμένη" : "Other / unspecified";
        }
    }
    logLabelValue(gr ? "Κατάσταση δεδομένων (Telephony)" : "Telephony data state",
            dataLabel);
    logInfo(gr ? "DISCONNECTED δεν σημαίνει κατ' ανάγκη ότι ο χρήστης απενεργοποίησε τα δεδομένα κινητής."
               : "DISCONNECTED does not imply the user switched mobile data off.");

    if (s.signalLevelKnown) {
        logLabelValue(gr ? "Σήμα (ένδειξη Android)" : "Signal (Android-reported)",
                s.signalLevel + " / 4");
        logInfo(gr ? "Πρόκειται για εκτίμηση του Android, όχι βαθμονομημένη μέτρηση dBm ή τεστ υγείας RF."
                   : "This is an OS-reported estimate, not calibrated dBm or RF hardware health.");
    } else {
        logLabelValue(gr ? "Σήμα" : "Signal",
                gr ? "ΑΓΝΩΣΤΟ / μη εκτεθειμένο" : "UNKNOWN / not exposed");
    }

    Lab11NetworkEvidence.DefaultRoute route = Lab11NetworkEvidence.classifyDefault(
            s.defaultNetworkPresent, s.defaultCapsKnown,
            s.defaultInternetAdvertised, s.defaultValidated,
            s.defaultCellular, s.defaultWifi, s.defaultVpn);
    final String routeLabel;
    switch (route) {
        case CELLULAR_VALIDATED:
            routeLabel = gr ? "CELLULAR — VALIDATED από το Android"
                            : "CELLULAR — Android VALIDATED";
            break;
        case CELLULAR_NOT_VALIDATED:
            routeLabel = gr ? "CELLULAR — μη επαληθευμένο Internet"
                            : "CELLULAR — Internet not validated";
            break;
        case WIFI_VALIDATED:
            routeLabel = gr ? "Wi-Fi — VALIDATED, όχι απόδειξη κινητής"
                            : "Wi-Fi — VALIDATED, not cellular evidence";
            break;
        case WIFI_NOT_VALIDATED:
            routeLabel = gr ? "Wi-Fi — Internet μη επαληθευμένο"
                            : "Wi-Fi — Internet not validated";
            break;
        case VPN_VALIDATED_UNATTRIBUTED:
            routeLabel = gr ? "VPN — VALIDATED, υποκείμενο δίκτυο άγνωστο"
                            : "VPN — VALIDATED, underlying transport unverified";
            break;
        case VPN_NOT_VALIDATED_UNATTRIBUTED:
            routeLabel = gr ? "VPN — Internet μη επαληθευμένο"
                            : "VPN — Internet not validated";
            break;
        case NO_DEFAULT_NETWORK:
            routeLabel = gr ? "Κανένα ενεργό προεπιλεγμένο δίκτυο"
                            : "No active default network";
            break;
        case CAPABILITIES_UNAVAILABLE:
            routeLabel = gr ? "Μη διαθέσιμες δυνατότητες δρομολόγησης"
                            : "Default network capabilities unavailable";
            break;
        case OTHER_VALIDATED:
            routeLabel = gr ? "Άλλη / μικτή διαδρομή — VALIDATED"
                            : "Other/mixed transport — VALIDATED";
            break;
        default:
            routeLabel = gr ? "Άλλη / μικτή διαδρομή — μη επαληθευμένη"
                            : "Other/mixed transport — not validated";
    }
    logLabelValue(gr ? "Ενεργή διαδρομή Internet" : "Active default Internet route",
            routeLabel);

    if (s.defaultCapsKnown) {
        logLabelValue(gr ? "Capability INTERNET" : "INTERNET capability",
                s.defaultInternetAdvertised ? "ADVERTISED" : "NOT ADVERTISED");
        logLabelValue(gr ? "Android validation" : "Android validation",
                s.defaultValidated ? "VALIDATED" : "NOT VALIDATED");
        if (s.defaultCaptivePortal) {
            logLabelWarnValue(gr ? "Πύλη σύνδεσης" : "Captive portal",
                    gr ? "Αναφέρθηκε από το Android" : "Reported by Android");
        }
    }

    appendHtml("<br>");
    if (Lab11NetworkEvidence.provesCurrentDefaultCellularValidation(route)
            && !s.airplaneOn) {
        logLabelValue(gr ? "Συμπέρασμα Lab 11" : "Lab 11 conclusion",
                gr ? "ΠΑΡΑΤΗΡΗΘΗΚΕ: ενεργό CELLULAR με επιβεβαίωση Internet από το Android"
                   : "OBSERVED: active CELLULAR default with Android-validated Internet");
        logInfo(gr ? "Δεν πιστοποιεί κάλυψη παρόχου, RF ισχύ, σταθερότητα ή υγεία modem."
                   : "Does not certify carrier coverage, radio strength, stability or modem health.");
    } else {
        logLabelWarnValue(gr ? "Συμπέρασμα Lab 11" : "Lab 11 conclusion",
                gr ? "Κινητή σύνδεση ΜΗ ΕΠΑΛΗΘΕΥΜΕΝΗ στην τρέχουσα διαδρομή — όχι βλάβη modem"
                   : "CELLULAR NOT VERIFIED on current default route — not a modem fault");
        logInfo(gr ? "Wi-Fi, VPN, απουσία SIM, άδειες ή επιλογή διαδρομής μπορούν να περιορίσουν τον έλεγχο."
                   : "Wi-Fi, VPN, SIM, permissions or routing can limit this observation.");
    }

    logLabelValue(gr ? "Εμβέλεια δοκιμής" : "Test scope",
            gr ? "Στιγμιότυπο τρέχουσας κατάστασης — όχι μετρημένη σταθερότητα χρόνου"
               : "Current-state snapshot — not a time-series stability test");
    logInfo(gr ? "Για ακριβή έλεγχο δεδομένων κινητής απαιτείται επιβεβαίωση χωρίς Wi-Fi/VPN και νέα μέτρηση."
               : "For a direct cellular test, verify without Wi-Fi/VPN and repeat the measurement.");
    logLine();
    enableSingleExportButton();
}

// ============================================================
// // LAB 12 — Call Function Interpretation (Laboratory)
// ============================================================
private void lab12CallFunctionInterpretation() {
    final boolean gr = AppLang.isGreek(this);
    if (lab12DialerPending) {
        logLabelWarnValue(gr ? "Lab 12" : "Lab 12",
                gr ? "Περιμένει επιστροφή από τον τηλεφωνητή — χωρίς νέο αποτέλεσμα"
                   : "Awaiting dialer return — no new conclusion");
        return;
    }

    appendHtml("<br>");
    logLine();
    logSection(gr ? "LAB 12 — Παρατηρήσεις τηλεφωνίας και προαιρετική πραγματική κλήση"
                  : "LAB 12 — Telephony observations and optional real call");
    logLine();

    // Independent Lab 12 snapshot: do not borrow Lab 11's legacy hasInternet
    // flag. Internet routing / Wi-Fi / VPN are NOT IMS or calling evidence.
    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    Boolean airplaneOn = null;
    Boolean voiceCapable = null;
    Boolean serviceInService = null;
    Integer simState = null;

    try {
        int airplaneValue = Settings.Global.getInt(getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, -1);
        airplaneOn = airplaneValue == -1 ? null : airplaneValue == 1;
    } catch (RuntimeException ignored) {
        // No default false: an unavailable OS setting remains UNKNOWN.
    }

    if (tm != null) {
        try {
            voiceCapable = Build.VERSION.SDK_INT >= 35
                    ? tm.isDeviceVoiceCapable() : tm.isVoiceCapable();
        } catch (RuntimeException ignored) {}
        try {
            simState = tm.getSimState();
        } catch (RuntimeException ignored) {}
        try {
            ServiceState ss = tm.getServiceState();
            if (ss != null) {
                serviceInService = ss.getState() == ServiceState.STATE_IN_SERVICE;
            }
        } catch (RuntimeException ignored) {
            // Permission, inactive subscription, unavailable radio/OEM.
        }
    }

    Lab12CallEvidence.Passive passive = Lab12CallEvidence.passive(
            tm != null, airplaneOn, voiceCapable, serviceInService);
    logLabelValue(gr ? "Λειτουργία πτήσης" : "Airplane mode",
            airplaneOn == null ? (gr ? "ΑΓΝΩΣΤΗ" : "UNKNOWN")
                    : airplaneOn ? (gr ? "ΕΝΕΡΓΗ" : "ON")
                                 : (gr ? "ΑΝΕΝΕΡΓΗ" : "OFF"));
    logLabelValue(gr ? "Υποστήριξη φωνής συσκευής (Android)" : "Device voice capability",
            voiceCapable == null ? (gr ? "ΜΗ ΔΙΑΘΕΣΙΜΗ" : "UNAVAILABLE")
                    : voiceCapable ? (gr ? "ΑΝΑΦΕΡΕΤΑΙ" : "REPORTED")
                                   : (gr ? "ΔΕΝ ΑΝΑΦΕΡΕΤΑΙ" : "NOT REPORTED"));
    logInfo(gr ? "Η γενική δυνατότητα κλήσεων δεν πιστοποιεί ενεργή συνδρομή, VoLTE/IMS ή επιτυχημένη κλήση."
               : "Device capability does not prove active voice subscription, VoLTE/IMS or a successful call.");

    if (simState != null) {
        String simLabel;
        switch (simState) {
            case TelephonyManager.SIM_STATE_READY:
                simLabel = gr ? "READY (προεπιλεγμένη)" : "READY (default)";
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                simLabel = gr ? "ABSENT (εκτεθειμένο προφίλ)" : "ABSENT (reported profile)";
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                simLabel = gr ? "Απαιτείται PIN" : "PIN required";
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                simLabel = gr ? "Απαιτείται PUK" : "PUK required";
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                simLabel = gr ? "Κλειδωμένη στο δίκτυο" : "Network locked";
                break;
            default:
                simLabel = gr ? "Άγνωστη / μη έτοιμη" : "Unknown / not ready";
        }
        logLabelValue(gr ? "SIM" : "SIM", simLabel);
    } else {
        logLabelValue("SIM", gr ? "ΑΓΝΩΣΤΗ / μη διαθέσιμη" : "UNKNOWN / unavailable");
    }

    if (serviceInService == null) {
        logLabelWarnValue(gr ? "Υπηρεσία φωνής (legacy)" : "Voice service (legacy)",
                gr ? "ΑΓΝΩΣΤΗ — η κατάσταση δεν ήταν προσβάσιμη"
                   : "UNKNOWN — service-state observation unavailable");
    } else {
        logLabelValue(gr ? "Υπηρεσία φωνής (legacy)" : "Voice service (legacy)",
                serviceInService ? (gr ? "Αναφέρθηκε IN SERVICE" : "Reported IN SERVICE")
                                 : (gr ? "Δεν αναφέρθηκε IN SERVICE" : "Not reported IN SERVICE"));
    }
    logLabelValue(gr ? "Συμπέρασμα Android" : "Android observation",
            lab12PassiveStatusLabel(passive, gr));
    logInfo(gr ? "Δεν εξετάστηκε πραγματική κλήση ούτε αν λειτουργεί το ακουστικό ή το μικρόφωνο."
               : "No actual voice call, receiver or microphone has been verified.");
    logInfo(gr ? "Το Internet, ακόμη και όταν είναι VALIDATED μέσω Wi-Fi, δεν αποδεικνύει IMS/VoWiFi."
               : "Internet, including VALIDATED Wi-Fi, does not prove IMS/VoWiFi voice service.");
    logLine();

    // Optional, explicit user action only; ACTION_DIAL does not place a call,
    // access call logs or record audio. No phone number is provided or stored.
    new AlertDialog.Builder(this)
            .setTitle(gr ? "LAB 12 — Πραγματική δοκιμή κλήσης (προαιρετικά)"
                         : "LAB 12 — Optional live-call check")
            .setMessage(gr
                    ? "Θέλεις να δοκιμάσεις μια πραγματική κλήση με γνωστό σου άτομο; "
                      + "Θα ανοίξει ΜΟΝΟ ο τηλεφωνητής χωρίς αριθμό. "
                      + "Εσύ επιλέγεις αν θα καλέσεις. Μην χρησιμοποιήσεις αριθμούς έκτακτης ανάγκης. "
                      + "Ενδέχεται να ισχύουν χρεώσεις. Δεν καταγράφουμε ήχο, αριθμό ή περιεχόμενο. "
                      + "Μετά την επιστροφή θα αναφέρεις αν ακούγατε ο ένας τον άλλον."
                    : "Would you like to test a real call with a consenting person? "
                      + "Only an EMPTY dialer opens. You decide whether to place a call. "
                      + "Never use emergency numbers for testing. Carrier charges may apply. "
                      + "No audio, phone number or conversation is recorded. "
                      + "On return, report whether both parties could hear each other.")
            .setPositiveButton(gr ? "ΑΝΟΙΓΜΑ ΤΗΛΕΦΩΝΟΥ" : "OPEN DIALER",
                    (dialog, which) -> lab12OpenDialer())
            .setNegativeButton(gr ? "ΠΑΡΑΛΕΙΨΗ" : "SKIP",
                    (dialog, which) -> lab12ReportGuided(
                            Lab12CallEvidence.Guided.NOT_TESTED))
            .setOnCancelListener(dialog -> lab12ReportGuided(
                    Lab12CallEvidence.Guided.NOT_TESTED))
            .show();
}

private String lab12PassiveStatusLabel(Lab12CallEvidence.Passive status, boolean gr) {
    switch (status) {
        case TELEPHONY_UNAVAILABLE:
            return gr ? "Μη διαθέσιμη υπηρεσία Telephony — όχι συμπέρασμα βλάβης"
                      : "Telephony API unavailable — no hardware fault inferred";
        case AIRPLANE_MODE_REPORTED:
            return gr ? "Λειτουργία πτήσης — περιορισμένη παρατήρηση"
                      : "Airplane mode — observation limited";
        case VOICE_CAPABILITY_NOT_REPORTED:
            return gr ? "Η δυνατότητα φωνητικών κλήσεων δεν δηλώθηκε"
                      : "Voice-call capability not advertised";
        case SERVICE_REPORTED_IN_SERVICE:
            return gr ? "Android: υπηρεσία αναφέρεται ως ενεργή, ΟΧΙ επαλήθευση κλήσης"
                      : "Android service reported active, NOT call verification";
        case SERVICE_NOT_REPORTED_IN_SERVICE:
            return gr ? "Android: δεν αναφέρθηκε legacy υπηρεσία — IMS άγνωστο"
                      : "Legacy service not reported — IMS unknown";
        default:
            return gr ? "Κατάσταση υπηρεσίας ΑΓΝΩΣΤΗ — δεν επαληθεύτηκε κλήση"
                      : "Service status UNKNOWN — call not verified";
    }
}

private void lab12OpenDialer() {
    final boolean gr = AppLang.isGreek(this);
    if (lab12DialerPending) return;
    lab12DialerPending = true;
    if (btnExport != null) {
        btnExport.setEnabled(false);
        btnExport.setAlpha(0.5f);
    }
    try {
        // ACTION_DIAL with NO telephone URI, no ACTION_CALL and no CALL_PHONE.
        // ACTION_DIAL resultCode is never proof a call was made.
        startActivityForResult(new Intent(Intent.ACTION_DIAL), REQ_LAB12_DIALER);
    } catch (RuntimeException e) {
        lab12DialerPending = false;
        logLabelWarnValue(gr ? "Τηλεφωνητής" : "Dialer",
                gr ? "Δεν άνοιξε — χωρίς επαλήθευση πραγματικής κλήσης"
                   : "Could not open — no live-call verification");
        lab12ReportGuided(Lab12CallEvidence.Guided.NOT_TESTED);
    }
}

private void lab12AfterDialerReturn() {
    final boolean gr = AppLang.isGreek(this);
    if (isFinishing() || isDestroyed()) {
        lab12ReportGuided(Lab12CallEvidence.Guided.INCONCLUSIVE);
        return;
    }
    // A dialer return does NOT prove the user placed or finished a call.
    new AlertDialog.Builder(this)
            .setTitle(gr ? "LAB 12 — Επιβεβαίωση από χρήστη" : "LAB 12 — User confirmation")
            .setMessage(gr ? "Πραγματοποιήθηκε και ολοκληρώθηκε πραγματική κλήση; "
                            + "Αν άνοιξες απλώς τον τηλεφωνητή, πάτησε ΟΧΙ."
                           : "Did you actually make and finish a real call? "
                            + "If you merely opened the dialer, choose NO.")
            .setPositiveButton(gr ? "ΝΑΙ" : "YES",
                    (dialog, which) -> lab12AskHearOtherParty())
            .setNegativeButton(gr ? "ΟΧΙ" : "NO",
                    (dialog, which) -> lab12ReportGuided(
                            Lab12CallEvidence.userReport(true, false, null, null)))
            .setOnCancelListener(dialog -> lab12ReportGuided(
                    Lab12CallEvidence.Guided.INCONCLUSIVE))
            .show();
}

private void lab12AskHearOtherParty() {
    final boolean gr = AppLang.isGreek(this);
    new AlertDialog.Builder(this)
            .setTitle(gr ? "LAB 12 — Ήχος λήψης" : "LAB 12 — Receive audio")
            .setMessage(gr ? "Κατά την πραγματική κλήση ΑΚΟΥΓΕΣ καθαρά τον συνομιλητή σου; "
                            + "Η επιλεγμένη διαδρομή (ακουστικό, Bluetooth ή ηχείο) δεν έχει πιστοποιηθεί."
                           : "During the real call, could YOU hear the other person clearly? "
                            + "The exact route (earpiece, Bluetooth or speaker) was not verified.")
            .setPositiveButton(gr ? "ΝΑΙ" : "YES",
                    (dialog, which) -> lab12AskOtherPartyHeard(true))
            .setNegativeButton(gr ? "ΟΧΙ" : "NO",
                    (dialog, which) -> lab12AskOtherPartyHeard(false))
            .setOnCancelListener(dialog -> lab12ReportGuided(
                    Lab12CallEvidence.Guided.INCONCLUSIVE))
            .show();
}

private void lab12AskOtherPartyHeard(boolean heardOther) {
    final boolean gr = AppLang.isGreek(this);
    new AlertDialog.Builder(this)
            .setTitle(gr ? "LAB 12 — Ήχος μετάδοσης" : "LAB 12 — Transmit audio")
            .setMessage(gr ? "Ο συνομιλητής σου επιβεβαίωσε ότι ΑΚΟΥΓΕ εσένα; "
                            + "Απάντησε μόνο βάσει πραγματικής συνομιλίας."
                           : "Did the other person confirm they could HEAR you? "
                            + "Answer only based on the actual conversation.")
            .setPositiveButton(gr ? "ΝΑΙ" : "YES", (dialog, which) ->
                    lab12ReportGuided(Lab12CallEvidence.userReport(
                            true, true, heardOther, true)))
            .setNegativeButton(gr ? "ΟΧΙ" : "NO", (dialog, which) ->
                    lab12ReportGuided(Lab12CallEvidence.userReport(
                            true, true, heardOther, false)))
            .setOnCancelListener(dialog -> lab12ReportGuided(
                    Lab12CallEvidence.Guided.INCONCLUSIVE))
            .show();
}

private void lab12ReportGuided(Lab12CallEvidence.Guided outcome) {
    final boolean gr = AppLang.isGreek(this);
    lab12DialerPending = false;
    appendHtml("<br>");
    switch (outcome) {
        case USER_REPORTED_TWO_WAY_AUDIO:
            logLabelValue(gr ? "Αποτέλεσμα κλήσης (αναφορά χρήστη)"
                             : "Call result (user report)",
                    gr ? "Ο χρήστης ανέφερε αμφίδρομο ήχο σε πραγματική κλήση"
                       : "User reported two-way audio during a real call");
            logInfo(gr ? "Δεν έγινε αυτόματη επιβεβαίωση κλήσης ή ταυτοποίηση ακουστικού/μικροφώνου."
                       : "Call execution and specific earpiece/microphone hardware were not independently verified.");
            break;
        case USER_REPORTED_ONE_WAY_OR_AUDIO_ISSUE:
            logLabelWarnValue(gr ? "Αποτέλεσμα κλήσης (αναφορά χρήστη)"
                                 : "Call result (user report)",
                    gr ? "Αναφέρθηκε δυσκολία αμφίδρομης επικοινωνίας — όχι αποδεδειγμένη βλάβη συσκευής"
                       : "Two-way communication problem reported — no confirmed device fault");
            break;
        case INCONCLUSIVE:
            logLabelWarnValue(gr ? "Δοκιμή κλήσης" : "Call check",
                    gr ? "Ανεπαρκής ή διακοπείσα επιβεβαίωση — ΑΓΝΩΣΤΟ"
                       : "Incomplete or interrupted confirmation — INCONCLUSIVE");
            break;
        default:
            logLabelValue(gr ? "Δοκιμή πραγματικής κλήσης" : "Actual call test",
                    gr ? "ΔΕΝ ΠΡΑΓΜΑΤΟΠΟΙΗΘΗΚΕ / ΔΕΝ ΕΠΑΛΗΘΕΥΤΗΚΕ"
                       : "NOT PERFORMED / NOT VERIFIED");
    }
    logInfo(gr ? "Το Lab 12 δεν καταγράφει τηλεφωνική κλήση, ήχο, αριθμούς ή δεδομένα συνομιλίας."
               : "Lab 12 does not record call events, audio, numbers or conversation content.");
    logLine();
    enableSingleExportButton();
}

// ============================================================
// // LAB 13 — Bluetooth Connectivity Check
// POPUP + WAIT FOR DEVICE + 60s MONITOR + DIAGNOSIS
// (FINAL — STRUCTURED / NO NESTED METHODS / READY COPY-PASTE)
// ============================================================

private void lab13BluetoothConnectivityCheck() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!ensurePermissions(
                new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                this::lab13BluetoothConnectivityCheck
        )) return;
    }

    final boolean gr = AppLang.isGreek(this);

    BluetoothManager bm = null;
    BluetoothAdapter ba = null;

    try {
        bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        ba = (bm != null) ? bm.getAdapter() : null;
    } catch (Throwable ignore) {}

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 13 — Έλεγχος Συνδεσιμότητας Bluetooth"
            : "LAB 13 — Bluetooth Connectivity Check");
    logLine();

    if (ba == null) {
        logLabelWarnValue(gr ? "Bluetooth adapter" : "Bluetooth adapter",
                gr ? "ΜΗ ΔΙΑΘΕΣΙΜΟΣ — δεν αποδεικνύεται βλάβη"
                   : "UNAVAILABLE — no hardware-failure verdict");
        enableSingleExportButton();
        return;
    }

    Boolean enabled = null;
    try { enabled = ba.isEnabled(); } catch (RuntimeException ignored) {}

    if (!Boolean.TRUE.equals(enabled)) {
        logLabelWarnValue(gr ? "Bluetooth adapter" : "Bluetooth adapter",
                enabled == null
                        ? (gr ? "Άγνωστη κατάσταση — άδεια/OEM" : "Unknown — permission/OEM")
                        : (gr ? "ΑΝΕΝΕΡΓΟ — ενεργοποίησέ το για τον έλεγχο"
                              : "OFF — enable Bluetooth for testing"));
        enableSingleExportButton();
        return;
    }

    logLabelValue(gr ? "Bluetooth adapter (Android)" : "Bluetooth adapter (Android)",
            gr ? "ΕΝΕΡΓΟ — όχι πιστοποίηση RF" : "ON — not RF hardware certification");

    // RESET STATE
    lab13Bm = bm;
    lab13Ba = ba;

    lab13Running = false;
    lab13MonitoringStarted = false;
    lab13HadAnyConnection = false;
    lab13AssumedConnected = false;

    lab13DisconnectEvents = 0;
    lab13ReconnectEvents  = 0;

    showLab13GatePopup();
}

// ============================================================
// LAB 13 — GATE POPUP (Skip / Continue) — MODERN
// AppLang + AppTTS + GEL UI
// ============================================================
private void showLab13GatePopup() {

    final boolean gr = AppLang.isGreek(this);

    final String titleText =
            gr
                    ? "LAB 13 — Έλεγχος Εξωτερικής Συσκευηςής Bluetooth"
                    : "LAB 13 — External Bluetooth Device Check";

    final String messageText =
            gr
                    ? "Σύνδεσε ΜΙΑ εξωτερική συσκευη Bluetooth.\n\n"
                      + "π.χ. ακουστικά, σύστημα αυτοκινήτου, πληκτρολόγιο.\n\n"
                      + "Το τεστ, αξιολογεί τη σταθερότητα της σύνδεσης Bluetooth.\n\n"
                      + "Αν δεν έχεις συνδεδεμένη εξωτερική συσκευή,\n"
                      + "μπορείς να παραλείψεις αυτό το βήμα,\n"
                      + "και να συνεχίσεις με τον έλεγχο του Bluetooth του συστήματος."
                    : "Connect ONE external Bluetooth device.\n\n"
                      + "e.g. headphones, car kit, keyboard.\n\n"
                      + "This test, evaluates Bluetooth connection stability.\n\n"
                      + "If no external device is connected,\n"
                      + "you may skip this step,\n"
                      + "and continue with the system Bluetooth check.";

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(true);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(20), dp(24), dp(18));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(4), 0xFFFFD700);
    root.setBackground(bg);

    // ---------------------------
    // TITLE (WHITE)
    // ---------------------------
    TextView title = new TextView(this);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

    // ---------------------------
    // MESSAGE (NEON GREEN)
    // ---------------------------
    TextView msg = new TextView(this);
    msg.setText(messageText);
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    msg.setLineSpacing(0f, 1.15f);
    root.addView(msg);

    // ---------------------------
    // MUTE ROW (ABOVE BUTTONS)
    // ---------------------------
    root.addView(buildMuteRow());

    // ---------------------------
    // BUTTONS
    // ---------------------------
    LinearLayout buttons = new LinearLayout(this);
    buttons.setOrientation(LinearLayout.HORIZONTAL);
    buttons.setPadding(0, dp(14), 0, 0);

    Button skip = gelButton(
        this,
        gr ? "ΠΑΡΑΛΕΙΨΗ" : "SKIP",
        0xFF444444
);

    Button cont = gelButton(
        this,
        gr ? "ΣΥΝΕΧΕΙΑ" : "CONTINUE",
        0xFF0F8A3B
);

    LinearLayout.LayoutParams lp =
        new LinearLayout.LayoutParams(0, dp(52), 1f);

lp.setMargins(0, 0, dp(8), 0);
skip.setLayoutParams(lp);

LinearLayout.LayoutParams lp2 =
        new LinearLayout.LayoutParams(0, dp(52), 1f);

lp2.setMargins(dp(8), 0, 0, 0);
cont.setLayoutParams(lp2);

buttons.addView(skip);
buttons.addView(cont);

    root.addView(buttons);

    b.setView(root);

    final AlertDialog gate = b.create();
    if (gate.getWindow() != null) {
        gate.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    gate.setOnCancelListener(dialog -> {
        logLabelValue(gr ? "Εξωτερική δοκιμή Bluetooth" : "Bluetooth external test",
                "NOT_TESTED — cancelled before monitoring");
        enableSingleExportButton();
    });
    gate.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(gate);

    // ---------------------------
    // ACTIONS
    // ---------------------------
    skip.setOnClickListener(v -> {
        AppTTS.stop();
        lab13SkipExternalTest = true;
        gate.dismiss();
        runLab13BluetoothCheckCore();   // system-only
    });

    cont.setOnClickListener(v -> {
        AppTTS.stop();
        lab13SkipExternalTest = false;
        gate.dismiss();
        runLab13BluetoothCheckCore();   // full test
    });
}

// ============================================================
// CORE — FULL LAB 13 (LOG + UI + WAIT + MONITOR + DIAGNOSIS)
// ============================================================
private void runLab13BluetoothCheckCore() {

    final boolean gr = AppLang.isGreek(this);

    // ---------- GET BT
    lab13Bm = null;
    lab13Ba = null;

    try {
        lab13Bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        lab13Ba = (lab13Bm != null) ? lab13Bm.getAdapter() : null;
    } catch (Throwable e) {
        logError(gr
                ? "Αποτυχία πρόσβασης BluetoothManager: " + (e.getMessage() != null ? e.getMessage() : "")
                : "BluetoothManager access failed: " + (e.getMessage() != null ? e.getMessage() : ""));
        logLine();

        appendHtml("<br>");
        logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    // BASIC SUPPORT
    if (lab13Ba == null) {
        logLabelWarnValue(gr ? "Bluetooth adapter" : "Bluetooth adapter",
                gr ? "ΜΗ ΔΙΑΘΕΣΙΜΟΣ — η αιτία είναι άγνωστη"
                   : "UNAVAILABLE — underlying cause unknown");
        logLine();

        appendHtml("<br>");
        logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    logOk(gr ? "Το Bluetooth υποστηρίζεται." : "Bluetooth supported.");

    Boolean enabled = null;
    try { enabled = lab13Ba.isEnabled(); } catch (RuntimeException ignored) {}

    logLabelValue(
            gr ? "Ενεργό" : "Enabled",
            enabled == null ? "UNKNOWN" : enabled ? (gr ? "Ναι" : "Yes") : (gr ? "Όχι" : "No")
    );

    int state = -1;
    try { state = lab13Ba.getState(); } catch (RuntimeException ignored) {}

    String stateStr;
    if (state == BluetoothAdapter.STATE_ON) {
        stateStr = gr ? "ΑΝΟΙΧΤΟ" : "ON";
    } else if (state == BluetoothAdapter.STATE_TURNING_ON) {
        stateStr = gr ? "ΑΝΟΙΓΕΙ" : "TURNING ON";
    } else if (state == BluetoothAdapter.STATE_TURNING_OFF) {
        stateStr = gr ? "ΚΛΕΙΝΕΙ" : "TURNING OFF";
    } else if (state == BluetoothAdapter.STATE_OFF) {
        stateStr = gr ? "ΚΛΕΙΣΤΟ" : "OFF";
    } else {
        stateStr = "UNKNOWN";
    }

    logLabelValue(
            gr ? "Κατάσταση" : "State",
            stateStr
    );

    boolean le = false;
    try {
        le = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    } catch (Throwable ignore) {}

    logLabelValue(
            gr ? "Υποστήριξη BLE" : "BLE Support",
            le ? (gr ? "Ναι" : "Yes") : (gr ? "Όχι" : "No")
    );

    final boolean adapterEnabledAtStart = Boolean.TRUE.equals(enabled);
    if (!adapterEnabledAtStart) {
        logLabelWarnValue(gr ? "Bluetooth adapter" : "Bluetooth adapter",
                enabled == null ? (gr ? "ΑΓΝΩΣΤΟ — άδεια ή OEM"
                                      : "UNKNOWN — permission or OEM")
                                : (gr ? "ΚΛΕΙΣΤΟ — ενεργοποίησέ το"
                                      : "OFF — enable Bluetooth"));
        logLine();

        appendHtml("<br>");
        logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    // Privacy-preserving pairing count only; no peripheral names or MACs in report.
    try {
        Set<BluetoothDevice> bonded = lab13Ba.getBondedDevices();
        logLabelValue(gr ? "Πλήθος συζευγμένων" : "Paired device count",
                bonded == null ? "UNKNOWN" : String.valueOf(bonded.size()));
        logInfo(gr ? "Σύζευξη δεν αποδεικνύει πραγματική σύνδεση."
                   : "Pairing is not proof of a live connection.");
    } catch (RuntimeException e) {
        logLabelWarnValue(gr ? "Σύζευξη" : "Pairing", "UNAVAILABLE");
    }

    // ------------------------------------------------------------
    // SYSTEM-ONLY MODE (Skip external device test)
    // ------------------------------------------------------------
    if (lab13SkipExternalTest) {
        logWarn(gr
                ? "Το τεστ εξωτερικής Bluetooth συσκευής παραλείφθηκε από τον χρήστη."
                : "External Bluetooth device test skipped by user.");
        logLabelValue(gr ? "Εξωτερικό Bluetooth" : "External Bluetooth",
                "NOT_TESTED — no peripheral connection verified");

        appendHtml("<br>");
        logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    // ---------- RESET RUN STATE
    lab13Running = false;
    lab13Seconds = 0;
    lab13StartMs = 0L;

    lab13HadAnyConnection = false;
    lab13LastConnected = false;

    lab13DisconnectEvents = 0;
    lab13ReconnectEvents = 0;
    lab13AmbiguousTransitions = 0;
    lab13AdapterObservedThroughout = true;
    lab13ReceiverSawConnection = false;
    lab13ReceiverSawDisconnection = false;
    lab13MonitoringStarted = false;
    lab13WaitingForExternal = true;
    if (btnExport != null) {
        btnExport.setEnabled(false);
        btnExport.setAlpha(0.5f);
    }
    final int generation = ++lab13MonitorGeneration;
    try { unregisterReceiver(lab13BtReceiver); } catch (RuntimeException ignored) {}

// ------------------------------------------------------------
// REGISTER BLUETOOTH RECEIVER (LAB 13)
// ------------------------------------------------------------
IntentFilter f = new IntentFilter();
f.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
f.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
try {
    registerReceiver(lab13BtReceiver, f);
} catch (RuntimeException e) {
    lab13WaitingForExternal = false;
    logLabelWarnValue(gr ? "Εγγραφή συμβάντων" : "Event receiver",
            gr ? "ΜΗ ΔΙΑΘΕΣΙΜΗ — δεν γίνεται διάγνωση" : "UNAVAILABLE — no verdict");
    enableSingleExportButton();
    return;
}

// ------------------------------------------------------------
// UI — GEL DARK GOLD MONITOR DIALOG (MODERN)
// ------------------------------------------------------------

final String titleText =
        gr
                ? "LAB 13 — Παρακολούθηση Σταθερότητας Bluetooth"
                : "LAB 13 — Bluetooth Stability Monitor";

final String messageText =
        gr
                ? "Σύνδεσε ΜΙΑ εξωτερική συσκευή Bluetooth.\n\n"
                  + "Κράτησέ την συνδεδεμένη, για τουλάχιστον 1 λεπτό.\n"
                  + "Μην αποσυνδέσεις τη συσκευή κατά τη διάρκεια του τεστ.\n\n"
                  + "Κράτησε τη συσκευή Bluetooth σε απόσταση\n"
                  + "έως 10 μέτρα από το τηλέφωνο.\n"
                  + "Μην απομακρυνθείς κατά την παρακολούθηση."
                : "Connect ONE external Bluetooth device.\n\n"
                  + "Keep it connected for at least one minute.\n"
                  + "Do not disconnect during the test.\n\n"
                  + "Keep the Bluetooth device within\n"
                  + "10 meters from the phone.\n"
                  + "Do not move away during monitoring.";

AlertDialog.Builder b =
        new AlertDialog.Builder(
                this,
                android.R.style.Theme_Material_Dialog_NoActionBar
        );
b.setCancelable(false);

LinearLayout root = new LinearLayout(this);
root.setOrientation(LinearLayout.VERTICAL);
root.setPadding(dp(24), dp(20), dp(24), dp(18));

GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF101010);
bg.setCornerRadius(dp(10));
bg.setStroke(dp(4), 0xFFFFD700);
root.setBackground(bg);

// ---------------------------
// TITLE (WHITE)
// ---------------------------
TextView title = new TextView(this);
title.setText(titleText);
title.setTextColor(Color.WHITE);
title.setTextSize(18f);
title.setTypeface(null, Typeface.BOLD);
title.setGravity(Gravity.CENTER);
title.setPadding(0, 0, 0, dp(12));
root.addView(title);

// ---------------------------
// MESSAGE (NEON GREEN)
// ---------------------------
TextView msg = new TextView(this);
msg.setText(messageText);
msg.setTextColor(0xFF39FF14);
msg.setTextSize(15f);
msg.setGravity(Gravity.CENTER);
msg.setLineSpacing(0f, 1.15f);
root.addView(msg);

// ---------------------------
// STATUS TEXT
// ---------------------------
lab13StatusText = new TextView(this);
lab13StatusText.setText(
        gr
                ? "Αναμονή για σταθερή σύνδεση Bluetooth…"
                : "Waiting for stable Bluetooth connection…"
);
lab13StatusText.setTextColor(0xFFAAAAAA);
lab13StatusText.setTextSize(15f);
lab13StatusText.setGravity(Gravity.CENTER);
lab13StatusText.setPadding(0, dp(10), 0, 0);
root.addView(lab13StatusText);

// ---------------------------
// DOTS (NEON)
// ---------------------------
lab13DotsView = new TextView(this);
lab13DotsView.setText("•••");
lab13DotsView.setTextColor(0xFF39FF14);
lab13DotsView.setTextSize(22f);
lab13DotsView.setGravity(Gravity.CENTER);
root.addView(lab13DotsView);

// ---------------------------
// COUNTER
// ---------------------------
lab13CounterText = new TextView(this);
lab13CounterText.setText(
        gr
                ? "Παρακολούθηση: 0 / 60 δευτ."
                : "Monitoring: 0 / 60 sec"
);
lab13CounterText.setTextColor(0xFF39FF14);
lab13CounterText.setGravity(Gravity.CENTER);
root.addView(lab13CounterText);

// ---------------------------
// PROGRESS BAR (SEGMENTS)
// ---------------------------
lab13ProgressBar = new LinearLayout(this);
lab13ProgressBar.setOrientation(LinearLayout.HORIZONTAL);
lab13ProgressBar.setGravity(Gravity.CENTER);
lab13ProgressBar.setPadding(0, dp(10), 0, 0);

for (int i = 0; i < 6; i++) {
    View seg = new View(this);
    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(0, dp(10), 1f);
    lp.setMargins(dp(3), 0, dp(3), 0);
    seg.setLayoutParams(lp);
    seg.setBackgroundColor(0xFF333333);
    lab13ProgressBar.addView(seg);
}
root.addView(lab13ProgressBar);

// ---------------------------
// MUTE ROW (GLOBAL APP TTS)
// ---------------------------
root.addView(buildMuteRow());

// ---------------------------
// EXIT BUTTON
// ---------------------------
Button exitBtn = gelButton(this, gr ? "ΕΞΟΔΟΣ ΤΕΣΤ" : "EXIT TEST",
        0xFF8B0000
);
LinearLayout.LayoutParams lpExit =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
        );
lpExit.setMargins(0, dp(14), 0, 0);
exitBtn.setLayoutParams(lpExit);

exitBtn.setOnClickListener(v -> {
    AppTTS.stop();
    abortLab13ByUser();
});
root.addView(exitBtn);

b.setView(root);

lab13Dialog = b.create();
if (lab13Dialog.getWindow() != null) {
    lab13Dialog.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

lab13Dialog.show();
com.gel.cleaner.GelButtonLayout.fitDialog(lab13Dialog);

// ---------------------------
// TTS (ONLY IF NOT MUTED)
// ---------------------------
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (lab13Dialog != null && lab13Dialog.isShowing() && !AppTTS.isMuted(this)) {
        AppTTS.ensureSpeak(this, messageText);
    }
}, 120);

    // ------------------------------------------------------------
    // RESET MONITOR FLAGS (NEW RUN)
    // ------------------------------------------------------------
    lab13MonitoringStarted = false;
    lab13HadAnyConnection = false;
    lab13LastConnected = false;

// ------------------------------------------------------------
// ANDROID 12+ PERMISSION — MUST NOT STOP FLOW WHEN ALREADY GRANTED
// (FIX: remove unreachable code)
// ------------------------------------------------------------
if (Build.VERSION.SDK_INT >= 31) {
    if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED) {

        logLabelWarnValue(gr ? "BLUETOOTH_CONNECT" : "BLUETOOTH_CONNECT",
                gr ? "Δεν επιτράπηκε η δοκιμή" : "Permission not granted");
        abortLab13ByUser();
        return;
    }
}

// ------------------------------------------------------------
// SNAPSHOT CHECK — already connected device (AFTER UI READY)
// ------------------------------------------------------------
if (lab13IsAnyExternalConnected()) {

    lab13HadAnyConnection = true;

    if (lab13StatusText != null) {
        lab13StatusText.setText(
                gr
                        ? "Εξωτερική συσκευή ήδη συνδεδεμένη. Εκκίνηση παρακολούθησης..."
                        : "External device already connected. Starting stability monitor..."
        );
    }

    startLab13Monitor60s();
    return;
}

// ------------------------------------------------------------
// WAIT FOR EXTERNAL DEVICE — RECEIVER-BASED (MODERN)
// ------------------------------------------------------------
if (!lab13MonitoringStarted && lab13StatusText != null) {
    lab13StatusText.setText(
            gr
                    ? "Αναμονή για εξωτερική συσκευή Bluetooth…"
                    : "Waiting for an external Bluetooth device…"
    );
}

if (lab13CounterText != null) {
    lab13CounterText.setText(gr ? "Αναμονή περιφερειακού: έως 45 δευτερόλεπτα"
                                 : "Waiting for accessory: up to 45 seconds");
}
lab13Handler.postDelayed(() -> {
    if (lab13MonitorGeneration != generation
            || !lab13WaitingForExternal || lab13MonitoringStarted) return;
    lab13WaitingForExternal = false;
    logLabelWarnValue(gr ? "Περιφερειακό" : "Accessory",
            gr ? "Δεν παρατηρήθηκε σε 45s — όχι βλάβη Bluetooth"
               : "Not observed within 45s — not hardware failure");
    lab13FinishAndReport(adapterEnabledAtStart);
}, 45000L);
}

// ============================================================
// MONITOR LOOP (60s) — polls connected devices + detects flips
// ============================================================
private void startLab13Monitor60s() {

    if (lab13MonitoringStarted) return;
    lab13WaitingForExternal = false;
    lab13MonitoringStarted = true;

    lab13Running = true;
    lab13StartMs = SystemClock.elapsedRealtime();
    lab13Seconds = 0;

    lab13DisconnectEvents = 0;
    lab13ReconnectEvents = 0;
    lab13AmbiguousTransitions = 0;
    lab13AdapterObservedThroughout = true;
    final int generation = lab13MonitorGeneration;

    boolean connectedNow = lab13IsAnyExternalConnected();

    // ------------------------------------------------------------
    // HARD SYNC — receiver + snapshot (INITIAL)
    // ------------------------------------------------------------
    if (lab13ReceiverSawConnection && !connectedNow) {
        connectedNow = true;
    }

    if (lab13ReceiverSawDisconnection && connectedNow) {
        connectedNow = false;
    }

    lab13ReceiverSawConnection = false;
    lab13ReceiverSawDisconnection = false;

    lab13LastConnected = connectedNow;
    if (connectedNow) lab13HadAnyConnection = true;

    if (lab13StatusText != null) {
    lab13StatusText.setText(
            AppLang.isGreek(this)
                    ? "Παρακολούθηση σταθερότητας Bluetooth…"
                    : "Monitoring Bluetooth stability…"
    );
}

    if (lab13CounterText != null) {
        lab13CounterText.setText("Monitoring: 0 / 60 sec");
    }

    try { lab13Handler.removeCallbacksAndMessages(null); } catch (Throwable ignore) {}

    lab13Handler.post(new Runnable() {
        int dotPhase = 0;

        @Override
        public void run() {

            if (!lab13Running || lab13MonitorGeneration != generation) return;

            boolean adapterStable = false;
            try {
                adapterStable =
                        lab13Ba != null &&
                        lab13Ba.isEnabled() &&
                        lab13Ba.getState() == BluetoothAdapter.STATE_ON;
            } catch (RuntimeException ignored) {}
            if (!adapterStable) lab13AdapterObservedThroughout = false;

            boolean connected = lab13IsAnyExternalConnected();

            // ------------------------------------------------------------
            // HARD SYNC — receiver + snapshot (EACH TICK)
            // ------------------------------------------------------------
            // Priority: explicit events > snapshot
            if (lab13ReceiverSawConnection && lab13ReceiverSawDisconnection) {
                // Two ACL transitions in the same polling interval:
                // event ordering and the affected remote device are UNKNOWN.
                lab13AmbiguousTransitions++;
                lab13HadAnyConnection = true;
            } else if (lab13ReceiverSawConnection) {
                connected = true;
            } else if (lab13ReceiverSawDisconnection) {
                connected = false;
            }

            // clear flags each tick
            lab13ReceiverSawConnection = false;
            lab13ReceiverSawDisconnection = false;

            if (connected) {
                lab13HadAnyConnection = true;
            }

            // ------------------------------------------------------------
            // TRANSITION LOGIC (CORRECT)
            // ------------------------------------------------------------
            if (!lab13LastConnected && connected && lab13Seconds > 0) {
                lab13ReconnectEvents++;
            }

            if (lab13LastConnected && !connected) {
                lab13DisconnectEvents++;
            }

            lab13LastConnected = connected;

            // ------------------------------------------------------------
            // TIME
            // ------------------------------------------------------------
            lab13Seconds = Lab13BluetoothEvidence.boundedSeconds(
                    SystemClock.elapsedRealtime() - lab13StartMs, 60);

            if (lab13CounterText != null) {
                lab13CounterText.setText(
                        "Monitoring: " + lab13Seconds + " / 60 sec"
                );
            }

            // ------------------------------------------------------------
            // DOTS
            // ------------------------------------------------------------
            dotPhase = (dotPhase + 1) % 4;
            if (lab13DotsView != null) {
                lab13DotsView.setText(
                        dotPhase == 1 ? "••" :
                        dotPhase == 2 ? "•••" : "•"
                );
            }

            // ------------------------------------------------------------
            // PROGRESS BAR
            // ------------------------------------------------------------
            lab13UpdateProgressSegments(lab13Seconds);

// ------------------------------------------------------------
// STATUS TEXT (COLOR-CODED)
// ------------------------------------------------------------
if (lab13StatusText != null) {

    final boolean gr = AppLang.isGreek(ManualTestsActivity.this);

    if (!adapterStable) {

        lab13StatusText.setText(
                gr ? "Ο Bluetooth adapter δεν είναι σταθερός."
                   : "Bluetooth adapter not stable."
        );
        lab13StatusText.setTextColor(0xFFFFD966); // yellow (warning)

    } else if (connected) {

        lab13StatusText.setText(
                gr ? "Εξωτερική συσκευή συνδεδεμένη — παρακολούθηση σταθερότητας..."
                   : "External device connected — monitoring stability..."
        );
        lab13StatusText.setTextColor(0xFF39FF14); // GEL green (OK)

    } else if (lab13HadAnyConnection) {

        lab13StatusText.setText(
                gr ? "Η εξωτερική συσκευή δεν είναι προσωρινά διαθέσιμη."
                   : "External device temporarily unavailable."
        );
        lab13StatusText.setTextColor(0xFFFFD966); // yellow (warning)

    } else {

        lab13StatusText.setText(
                gr ? "Αναμονή για εξωτερική συσκευή Bluetooth..."
                   : "Waiting for an external Bluetooth device..."
        );
        lab13StatusText.setTextColor(0xFFFFD966); // yellow (info/wait)
    }
}

            // ------------------------------------------------------------
            // FINISH
            // ------------------------------------------------------------
            if (lab13Seconds >= 60) {
                lab13Running = false;
                lab13FinishAndReport(adapterStable && lab13AdapterObservedThroughout);
                return;
            }

            lab13Handler.postDelayed(this, 1000);
        }
    });
}

// ============================================================
// CONNECTED DEVICES — SNAPSHOT (STABLE)
// ============================================================
private boolean lab13IsAnyExternalConnected() {

    if (lab13Ba == null) return false;

    try {
        return lab13Ba.getProfileConnectionState(BluetoothProfile.A2DP)
                    == BluetoothProfile.STATE_CONNECTED
            || lab13Ba.getProfileConnectionState(BluetoothProfile.HEADSET)
                    == BluetoothProfile.STATE_CONNECTED
            || lab13Ba.getProfileConnectionState(BluetoothProfile.GATT)
                    == BluetoothProfile.STATE_CONNECTED;
    } catch (Throwable ignore) {}

    return false;
}

// ============================================================
// UI — progress segments
// ============================================================
private void lab13UpdateProgressSegments(int seconds) {
    if (lab13ProgressBar == null) return;

    int filled = Math.min(6, seconds / 10); // 0..6
    for (int i = 0; i < lab13ProgressBar.getChildCount(); i++) {
        View seg = lab13ProgressBar.getChildAt(i);
        if (seg == null) continue;
        if (i < filled) seg.setBackgroundColor(0xFF39FF14);   // GEL green
        else seg.setBackgroundColor(0xFF333333);
    }
}

// ============================================================
// FINISH — close dialog + structured diagnosis (GEL LOGIC)
// ============================================================
private void lab13FinishAndReport(boolean adapterStable) {
    final boolean gr = AppLang.isGreek(this);
    boolean hadMonitor = lab13MonitoringStarted;
    long observedMs = hadMonitor
            ? Math.max(0L, SystemClock.elapsedRealtime() - lab13StartMs) : 0L;
    Lab13BluetoothEvidence.Result outcome = Lab13BluetoothEvidence.classify(
            lab13SkipExternalTest, hadMonitor,
            adapterStable && lab13AdapterObservedThroughout,
            lab13HadAnyConnection, observedMs, 60000L,
            lab13DisconnectEvents, lab13ReconnectEvents,
            lab13AmbiguousTransitions);

    lab13Running = false;
    lab13WaitingForExternal = false;
    lab13MonitoringStarted = false;
    lab13MonitorGeneration++;
    lab13Handler.removeCallbacksAndMessages(null);
    try { unregisterReceiver(lab13BtReceiver); } catch (RuntimeException ignored) {}
    try {
        if (lab13Dialog != null && lab13Dialog.isShowing()) lab13Dialog.dismiss();
    } catch (RuntimeException ignored) {}
    lab13Dialog = null;

    appendHtml("<br>");
    logLine();
    logSection(gr ? "LAB 13 — Τεκμήρια σύνδεσης Bluetooth"
                  : "LAB 13 — Bluetooth link observations");
    logLine();

    logLabelValue(gr ? "Περίοδος παρακολούθησης" : "Observed monitoring window",
            hadMonitor ? (observedMs / 1000L) + " s" : (gr ? "Δεν ξεκίνησε" : "Not started"));
    logLabelValue(gr ? "ACL / profile μεταβολές (συνολικά)" : "Aggregate ACL/profile transitions",
            gr ? ("Αποσυνδέσεις " + lab13DisconnectEvents
                    + ", επανασυνδέσεις " + lab13ReconnectEvents
                    + ", ασαφή γεγονότα " + lab13AmbiguousTransitions)
               : ("Disconnections " + lab13DisconnectEvents
                    + ", reconnections " + lab13ReconnectEvents
                    + ", ambiguous events " + lab13AmbiguousTransitions));
    logInfo(gr ? "Τα γεγονότα ACL ή οι καταστάσεις profile αφορούν πιθανώς διαφορετικές συσκευές."
               : "ACL broadcasts and profile status may refer to different remote devices.");

    switch (outcome) {
        case AGGREGATE_LINK_OBSERVED:
            logLabelValue(gr ? "Αποτέλεσμα" : "Result",
                    gr ? "ΠΑΡΑΤΗΡΗΘΗΚΕ σύνδεση σε πλήρες δείγμα 60s"
                       : "Aggregate link observed over a full 60s sample");
            logInfo(gr ? "Δεν επιβεβαιώθηκε συγκεκριμένη συσκευή ή μεταφορά δεδομένων GATT."
                       : "A specific peripheral and GATT data transfer were not verified.");
            break;
        case AGGREGATE_LINK_CHANGES_OBSERVED:
            logLabelWarnValue(gr ? "Αποτέλεσμα" : "Result",
                    gr ? "Παρατηρήθηκαν μεταβολές σύνδεσης — άγνωστη η αιτία"
                       : "Aggregate link changes observed — cause unknown");
            break;
        case ADAPTER_UNAVAILABLE:
            logLabelWarnValue(gr ? "Αποτέλεσμα" : "Result",
                    gr ? "Ο adapter δεν ήταν συνεχώς διαθέσιμος — δοκιμή ανεπαρκής"
                       : "Adapter not available throughout — incomplete observation");
            break;
        case INCOMPLETE:
            logLabelWarnValue(gr ? "Αποτέλεσμα" : "Result",
                    gr ? "ΔΕΙΓΜΑ ΜΗ ΟΛΟΚΛΗΡΩΜΕΝΟ — όχι PASS ή FAIL"
                       : "INCOMPLETE WINDOW — no hardware PASS or FAIL");
            break;
        case NOT_TESTED:
            logLabelValue(gr ? "Αποτέλεσμα" : "Result",
                    gr ? "NOT_TESTED — παραλείφθηκε λειτουργική δοκιμή"
                       : "NOT_TESTED — functional challenge skipped");
            break;
        default:
            logLabelWarnValue(gr ? "Αποτέλεσμα" : "Result",
                    gr ? "Δεν παρατηρήθηκε εξωτερική σύνδεση — όχι βλάβη Bluetooth"
                       : "No external link observed — not a Bluetooth hardware failure");
    }
    logLabelValue(gr ? "Διαγνωστικό όριο" : "Diagnostic limit",
            gr ? "Δεν αποδεικνύεται σταθερότητα RF, συγκεκριμένο περιφερειακό ή GATT read/write"
               : "RF health, specific peripheral identity and GATT read/write remain unverified");
    logInfo(gr ? "Το Lab 13 κατέγραψε διαθέσιμες ενδείξεις Android, όχι οριστική διάγνωση hardware."
               : "Lab 13 captured available Android indicators, not a hardware diagnosis.");
    enableSingleExportButton();
}

// ============================================================
// // PROFILE NAME (small internal helper)
// ============================================================
private String lab13ProfileName(int p) {
    if (p == BluetoothProfile.A2DP) return "A2DP";
    if (p == BluetoothProfile.HEADSET) return "HEADSET";
    if (p == BluetoothProfile.GATT) return "GATT";
    return "PROFILE(" + p + ")";
}

// ============================================================
// ABORT HOOK
// ============================================================
private void abortLab13ByUser() {
    final boolean gr = AppLang.isGreek(this);
    boolean hadSession = lab13Running || lab13WaitingForExternal
            || lab13MonitoringStarted;
    lab13Running = false;
    lab13WaitingForExternal = false;
    lab13MonitoringStarted = false;
    lab13MonitorGeneration++;
    lab13Handler.removeCallbacksAndMessages(null);
    try { unregisterReceiver(lab13BtReceiver); } catch (RuntimeException ignored) {}
    try {
        if (lab13Dialog != null && lab13Dialog.isShowing()) lab13Dialog.dismiss();
    } catch (RuntimeException ignored) {}
    lab13Dialog = null;
    try { AppTTS.stop(); } catch (RuntimeException ignored) {}

    if (hadSession) {
        appendHtml("<br>");
        logLabelWarnValue(gr ? "LAB 13 — Δοκιμή" : "LAB 13 — Test",
                gr ? "ΔΙΑΚΟΠΗ / ΑΚΥΡΩΣΗ — χωρίς συμπέρασμα hardware"
                   : "INTERRUPTED / CANCELLED — no hardware verdict");
        logLine();
    }
    enableSingleExportButton();
}

// ============================================================
// // LAB 14 — BATTERY DIAGNOSTICS SUITE
// ============================================================
private void showBatterySuiteMenu() {

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    LinearLayout root =
            buildGELPopupRoot(this);

    root.addView(
            buildPopupHeader(
                    this,
                    gr
                    ? "Σουίτα Διάγνωσης Μπαταρίας"
                    : "Battery Diagnostics Suite"
            )
    );

    root.addView(
            makeTestButton(
                    gr
                    ? (isGelProActive() ? "" : "🔒 ")
                      + "PRO  14A. Διάγνωση Υγείας και Καταπόνησης Μπαταρίας"
                    : (isGelProActive() ? "" : "🔒 ")
                      + "PRO  14A. Battery Health and Stress Diagnosis",
                    this::lab14BatteryHealthStressTest
            )
    );

    root.addView(
            makeTestButton(
                    gr
                    ? "14B. Προσομοίωση διάρκειας μπαταρίας καθημερινής χρήσης"
                    : "14B. Daily Battery Endurance Simulation",
                    this::lab14BBatteryDurationTest
            )
    );

    root.addView(
            makeTestButton(
                    gr
                    ? "14 GAMA. Προσομοίωση διάρκειας μπαταρίας σε Gaming"
                    : "14 GAMA. Gaming Battery Endurance Simulation",
                    this::lab14GamingDurationTest
            )
    );

    b.setView(root);

batterySuiteDialog = b.create();

if (batterySuiteDialog.getWindow() != null) {
    batterySuiteDialog.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

batterySuiteDialog.show();
com.gel.cleaner.GelButtonLayout.fitDialog(batterySuiteDialog);
}

// ============================================================
// LAB 14 — ENTRY
// ============================================================

private void lab14BatteryHealthStressTest() {

    final boolean grLocal = AppLang.isGreek(this);
    if (!requireGelPro(
            grLocal ? "LAB 14A — Υγεία και Καταπόνηση Μπαταρίας"
                    : "LAB 14A — Battery Health and Stress Diagnosis")) {
        return;
    }

    showLab14ConditionCheck(() -> {

        if (!lab14AdvisoryShown) {

            lab14AdvisoryShown = true;

            showLab14PreTestAdvisory(() -> {
                lab14BatteryHealthStressTest_REAL();
            });

        } else {


            lab14BatteryHealthStressTest_REAL();
        }
    });
}

// ============================================================
// LAB 14 — Battery health stress test
// ============================================================
private void lab14BatteryHealthStressTest_REAL() {

    final iDoctorEngine idoctor =
            iDoctorEngine.get(ManualTestsActivity.this);

    gr = AppLang.isGreek(this);

    final Lab14Engine lab14Engine =
            new Lab14Engine(this);

    // --------------------------------------------------
    // 🔴 HARD ENTRY GUARD
    // --------------------------------------------------
    if (lab14Running) return;

    // --------------------------------------------------
    // 🔴 CHECK CHARGING (FIRST - CRITICAL)
    // --------------------------------------------------
    if (isChargingNowSafe()) {

        logError(gr
                ? "Η δοκιμή απαιτεί να μην φορτίζει η συσκευή."
                : "Device must NOT be charging.");

        return;
    }

// --------------------------------------------------
// 🔴 GLOBAL RESET (ONE SOURCE OF TRUTH)
// --------------------------------------------------
validDrain = false;

lab14Cancelled = false;
lab14FastDone = false;

// 🔴 voltage pulse reset
lab14VoltagePulseRunning = false;
lab14LastVoltagePulseElapsedSec = -9999L;
lab14PreHardVoltagePulseDone = false;

lab14FirstHardVoltage = Float.NaN;
lab14MidHardVoltage = Float.NaN;
lab14LateHardVoltage = Float.NaN;
lab14LowestHardVoltage = Float.NaN;
lab14LastHardVoltage = Float.NaN;

lab14SelectedStressThreads = 0;

lab14LimiterScore = 0;
lab14LimiterLatched = false;
lab14CpuFreqPeak = 0;

lab14_systemLimited[0] = false;

// 🔴 runtime
lab14WeakLoadCounter = 0;

// 🔴 buffers
lab14ChargeSamples.clear();
lab14MinCharge = Long.MAX_VALUE;
lab14MaxCharge = 0L;

// 🔴 risks
collapseRisk[0] = false;

// 🔴 SAG / VOLTAGE
sag1 = Float.NaN;
sag2 = Float.NaN;
sagAvg = Float.NaN;

vStart[0] = Float.NaN;
vLoad1[0] = Float.NaN;
vRecover[0] = Float.NaN;
vLoad2[0] = Float.NaN;

voltageUnderLoad[0] = Float.NaN;

voltageRecovery[0] = Float.NaN;
voltageRecoverySpeed[0] = Float.NaN;
voltageStability[0] = Float.NaN;

// 🔴 core metrics
internalResistance[0] = Float.NaN;
thermalImpedance[0] = Float.NaN;

// 🔴 temperature
startBatteryTemp = Float.NaN;
endBatteryTemp = Float.NaN;

// 🔴 result state
lab14Conf = null;
lab14AgingIndex = -1;
lab14AgingInterp = "N/A";
lab14BatteryBehaviourWarning = false;

// 🔴 CENTRAL DIAGNOSTIC RESET — activates existing helper, prevents stale ghost state
resetBatteryDiagnostics();
// Invalidate previous battery evidence before a new run (including cancellation).
BatteryEvidenceStore.clearLab14(ManualTestsActivity.this);

    // --------------------------------------------------
    // 🔴 BASELINE TEMP (IDLE)
    // --------------------------------------------------
    try {
        Float temp0 = idoctor.getBatteryTempUnified();

        if (temp0 != null &&
            !Float.isNaN(temp0) &&
            temp0 > 0f &&
            temp0 < 85f) {

            startBatteryTemp = temp0;
        }

    } catch (Throwable t) {
        logError("BASE TEMP FAIL: " + t.getMessage());
    }

// ---------------------------------------
// 🔴 ENGINE
// ---------------------------------------
try {

    durationSec = LAB14_TOTAL_SECONDS;
    lastSelectedStressDurationSec = durationSec;

    // --------------------------------------------------
// 🔴 ZERO-RISK CALIBRATION
// --------------------------------------------------
if (!isLab14BMode && lab14OptimalThreads <= 0) {

    calibrateLoadZeroRisk();

    // 🔴 NEVER ALLOW WEAK LOAD
    int minThreads =
            Math.max(
                    4,
                    Runtime.getRuntime().availableProcessors() - 2
            );

    if (lab14OptimalThreads < minThreads) {
        lab14OptimalThreads = minThreads;
    }

    appendLog(
            "CALIB",
            "final threads=" + lab14OptimalThreads +
            " | minFloor=" + minThreads
    );
}

    // ------------------------------------------------------------
    // 🔴 INITIAL SNAPSHOT
    // ------------------------------------------------------------
    iDoctorEngine.BatterySnapshot start =
            idoctor.readBatterySnapshotLab();

    if (start == null) {
        logError(gr
                ? "Αποτυχία ανάγνωσης δεδομένων μπαταρίας"
                : "Battery snapshot failed");
        return;
    }

    if (start.chargeNowMah <= 0) {
        logError(gr
                ? "Μη διαθέσιμο charge counter"
                : "Charge counter unavailable");
        return;
    }

    if (Float.isNaN(start.batteryTempC)) {
        logWarn(gr
                ? "Μη διαθέσιμη θερμοκρασία μπαταρίας"
                : "Battery temperature unavailable");
    }

    // 🔴 BASE VALUES
    startMah = start.chargeNowMah;
    cycles = start.cycleCount;

    Float tStart0 = idoctor.getBatteryTempUnified();
    tempStart = (tStart0 != null) ? tStart0 : Float.NaN;
    lab14TempPeak = tempStart;

    if (Float.isNaN(tempStart) || tempStart <= 0f) {
        tempStart = start.batteryTempC;
    }

    batteryPercent = Math.max(0, getBatteryPercentSafe());

    // --------------------------------------------------
    // 🔴 BASELINE CAPACITY
    // --------------------------------------------------
    baselineFullMah = -1;

    if (start.chargeFullMah > 0) {
        baselineFullMah = start.chargeFullMah;

    } else if (start.chargeDesignMah > 0) {
        baselineFullMah = start.chargeDesignMah;

    } else if (start.chargeNowMah > 0 && batteryPercent > 5) {

        float percent = batteryPercent / 100.0f;

        if (percent > 0.01f) {
            baselineFullMah =
                    (long) (start.chargeNowMah / percent);
        }
    }

    boolean rooted = idoctor.isDeviceRooted();
    Float cpuTempStart = readCpuTempSafe();
    Float gpuTempStart = readGpuTempSafe();

    // --------------------------------------------------
    // 🔴 APPLY SCREEN / ENV
    // --------------------------------------------------
    applyMaxBrightnessAndKeepOn();
    sleepSilently(300);

    // --------------------------------------------------
    // 🔴 BASELINE VOLTAGE
    // --------------------------------------------------
    voltageStart = readStableBatteryVoltage();

    if (Float.isNaN(voltageStart) || voltageStart <= 0f) {
        voltageStart = getBatteryVoltageFiltered();
    }

    if (Float.isNaN(voltageStart) || voltageStart <= 0f) {

        float vStartMv =
                idoctor.readBatteryVoltageMvStable(5, 20);

        if (!Float.isNaN(vStartMv) &&
            vStartMv > 2500f &&
            vStartMv < 5500f) {

            voltageStart = vStartMv / 1000f;

        } else {
            voltageStart = Float.NaN;
        }
    }

// ------------------------------------------------------------
// 🔴 HEADER LOGS - START CONDITIONS
// ------------------------------------------------------------
appendHtml("<br>");
logLine();

logInfo(gr
        ? "LAB 14 — Δοκιμή Καταπόνησης & Υγείας Μπαταρίας"
        : "LAB 14 — Battery Health Stress Test");

logLine();

final int cores = Runtime.getRuntime().availableProcessors();

logLabelOkValue(
        gr ? "Λειτουργία" : "Mode",
        rooted
                ? (gr ? "Προηγμένη (Root access)" : "Advanced (Rooted)")
                : (gr ? "Τυπική (Χωρίς Root)" : "Standard (Unrooted)")
);

logLabelOkValue(
        gr ? "Διάρκεια δοκιμής" : "Duration",
        durationSec + (gr
                ? " δευτ. (εργαστηριακή λειτουργία)"
                : " sec (laboratory mode)")
);

logLabelOkValue(
        gr ? "Προφίλ καταπόνησης" : "Stress profile",
        "Fast phase + adaptive load + CPU/GPU + memory + bandwidth + vibration + video"
);

logLabelOkValue(
        gr ? "Αρχικές συνθήκες" : "Start conditions",
        String.format(
                Locale.US,
                gr
                        ? "φόρτιση=%d mAh, ποσοστό=%d%%, κατάσταση=Αποφόρτιση, θερμοκρασία=%.1f°C"
                        : "charge=%d mAh, level=%d%%, status=Discharging, temp=%.1f°C",
                startMah,
                Math.max(0, batteryPercent),
                Float.isNaN(tempStart) ? 0f : tempStart
        )
);

if (baselineFullMah > 0) {
    logLabelOkValue(
            gr ? "Αναφερόμενη πλήρης χωρητικότητα" : "Battery capacity baseline",
            baselineFullMah + (gr
                    ? " mAh"
                    : " mAh")
    );
} else {
    logLabelWarnValue(
            gr ? "Αναφερόμενη πλήρης χωρητικότητα" : "Battery capacity baseline",
            gr
                    ? "Μη διαθέσιμη"
                    : "N/A"
    );
}

logLabelOkValue(
        gr ? "Κύκλοι φόρτισης" : "Cycle count",
        cycles > 0
                ? String.valueOf(cycles)
                : (gr ? "Μη διαθέσιμο" : "N/A")
);

// 🔴 REAL THREADS — SINGLE SOURCE OF TRUTH
int startThreads =
        isLab14BMode
                ? cores
                : (
                    lab14OptimalThreads >= 2
                            ? lab14OptimalThreads
                            : Math.max(4, cores - 1)
                );

lab14SelectedStressThreads = startThreads;

int cpuThreadsToUse =
        startThreads;

logLabelOkValue(
        gr ? "Νήματα καταπόνησης CPU" : "CPU stress threads",
        cpuThreadsToUse + (gr
                ? " (πυρήνες=" + cores + ")"
                : " (cores=" + cores + ")")
);

// 🔴 CPU TEMP
if (cpuTempStart != null && !Float.isNaN(cpuTempStart)) {

    logLabelOkValue(
            gr ? "Θερμοκρασία CPU (έναρξη)" : "CPU temperature (start)",
            String.format(Locale.US, "%.1f°C", cpuTempStart)
    );

} else {

    logLabelWarnValue(
            gr ? "Θερμοκρασία CPU (έναρξη)" : "CPU temperature (start)",
            gr ? "Μη διαθέσιμη" : "N/A"
    );
}

// 🔴 GPU TEMP
if (gpuTempStart != null && !Float.isNaN(gpuTempStart)) {

    logLabelOkValue(
            gr ? "Θερμοκρασία GPU (έναρξη)" : "GPU temperature (start)",
            String.format(Locale.US, "%.1f°C", gpuTempStart)
    );

} else {

    logLabelWarnValue(
            gr ? "Θερμοκρασία GPU (έναρξη)" : "GPU temperature (start)",
            gr ? "Μη διαθέσιμη" : "N/A"
    );
}

logLabelOkValue(
        gr ? "Κατάσταση οθόνης" : "Screen state",
        gr
                ? "Μέγιστη φωτεινότητα + ενεργό keep screen on"
                : "Max brightness + keep screen on"
);

logLabelOkValue(
        gr ? "Θερμικά πεδία" : "Thermal domains",
        "CPU / GPU / SKIN / PMIC / BATT"
);

logLine();

// ------------------------------------------------------------
// 🔴 MAIN DIALOG + STATE
// ------------------------------------------------------------
lab14Running = true;
lab14BoostActive = false;
lab14SoftPhaseStarted = false;

t0 = SystemClock.elapsedRealtime();
lab14EndTime = t0 + (durationSec * 1000L);

if (DEBUG_MODE) appendLog("ENGINE", "START");

// ------------------------------------------------------------
// 🔴 START ENGINE
// ------------------------------------------------------------
lab14Engine.startDrainSession();

// --------------------------------------------------
// 🔴 REAL STRESS START (RESTORED)
// --------------------------------------------------

lab14CpuThreadsCurrent = startThreads;

startCpuBurnLimitedThreads(startThreads);

try {
    startGpuStress();
} catch (Throwable ignore) {}

try {
    startMemoryStress();
} catch (Throwable ignore) {}

if (DEBUG_MODE) {
    appendLog("LOAD",
            "START threads=" + startThreads +
            " | boost=" + lab14BoostActive +
            " | soft=" + lab14SoftPhaseStarted);
}

// 🔴 vibration loop
ui.removeCallbacks(lab14VibrationLoop);
ui.post(lab14VibrationLoop);

// ------------------------------------------------------------
// 🔴 START UI (THIS DRIVES EVERYTHING)
// ------------------------------------------------------------
startLab14SharedUI(durationSec, gr);

// ❌ ΔΕΝ καλείς:
// startLab14ProgressLoop()
// (το κάνει ήδη το UI)

// ------------------------------------------------------------

} catch (Throwable t) {

    try {
        ui.removeCallbacks(lab14VibrationLoop);
    } catch (Throwable ignore) {}

    lab14StopAllStress();

    lab14Cancelled = true;
    lab14Running = false;
    lab14PopupShown = false;
    lab14AdvisoryShown = false;

    lab14BoostActive = false;
    lab14SoftPhaseStarted = false;

    logError(gr
            ? "Σφάλμα LAB 14"
            : "LAB 14 error");
}
}

// ============================================================
// LAB 14 — LOG STRESS RESULT (HEADER)
// ============================================================
private void lab14LogStressResult(
        Lab14Result res,
        float sagAvg,
        float voltageStart,
        float[] voltageRecovery,
        float[] voltageRecoverySpeed,
        float voltageStability,
        float[] internalResistance,
        float estimatedESR,
        float thermalImpedance,
        float energyEfficiency,
        long startMah,
        long endMah,
        boolean[] lab14_systemLimited,
        boolean[] collapseRisk,
        boolean smartSwelling,
        boolean[] calibrationDrift
) {

    appendHtml("<br>");
    logLine();

    logInfo(gr
            ? "LAB 14 — Αποτέλεσμα καταπόνησης"
            : "LAB 14 — Stress result");

    logLine();

// =====================================================
// 🔴 LIMITER → HARD EXIT (FINAL)
// =====================================================
if (lab14_systemLimited[0]) {

    logLabelWarnValue(
            gr ? "Πτώση τάσης" : "Voltage sag",
            gr ? "Δεν μετρήθηκε (περιορισμός συστήματος)"
               : "Not measurable (system limiter)"
    );

    logLabelWarnValue(
            gr ? "Αξιοπιστία" : "Reliability",
            gr ? "Η μέτρηση περιορίστηκε από BMS"
               : "Measurement limited by BMS"
    );

    logLabelWarnValue(
            gr ? "Αποτέλεσμα" : "Result",
            gr
                    ? "Η συσκευή περιόρισε το φορτίο — δεν είναι δυνατή αξιόπιστη εκτίμηση."
                    : "Device limited the load — reliable estimation is not possible."
    );

    lab14LastLabel = "Limited";
    return;
}


// =====================================================
// 🔴 FINAL SAG (SINGLE SOURCE — STABLE)
// =====================================================
float finalSag = Float.NaN;

if (!Float.isNaN(voltageStart)) {

    // PRIMARY
    if (voltageUnderLoad != null &&
        voltageUnderLoad.length > 0 &&
        !Float.isNaN(voltageUnderLoad[0])) {

        float tmp = voltageStart - voltageUnderLoad[0];

        if (tmp > 0.005f && tmp < 1.0f) {
            finalSag = tmp;
        }
    }

    // FALLBACK
    if (Float.isNaN(finalSag) &&
        vLoad2 != null &&
        vLoad2.length > 0 &&
        !Float.isNaN(vLoad2[0])) {

        float tmp = voltageStart - vLoad2[0];

        if (tmp > 0.005f && tmp < 1.0f) {
            finalSag = tmp;
        }
    }
}

// ====================================================
// 🔴 FINAL SAG USAGE
// ====================================================
if (DEBUG_MODE) {

    logLine();
    logWarn("DEBUG FINAL SAG");

    logWarn("voltageStart=" + voltageStart);

    logWarn("voltageUnderLoad=" +
            (voltageUnderLoad != null &&
             voltageUnderLoad.length > 0
                    ? voltageUnderLoad[0]
                    : Float.NaN));

    logWarn("sagAvg=" + sagAvg);
    logWarn("finalSag=" + finalSag);

    logLine();
}

String labelSafe =
        (res.label != null && !res.label.isEmpty())
                ? res.label
                : "Unknown";


// 🔴 slightly lower threshold (15mV)
boolean sagValid =
        !Float.isNaN(finalSag) &&
        finalSag >= 0.015f &&
        finalSag < 1.0f;


if (!sagValid) {

    logLabelWarnValue(
            gr ? "Πτώση τάσης υπό φορτίο"
               : "Voltage sag under load",
            gr ? "Μη μετρήσιμη"
               : "Not measurable"
    );

} else {

    // =================================================
    // 🔴 SINGLE SAG CLASSIFIER — USER-FRIENDLY FINAL
    // =================================================
    // Keep the same user-visible label across:
    // - quick stress output
    // - stress result
    // - final result
    // Small sag gets the benefit of the doubt so we do not
    // alarm users for a healthy low-voltage-drop battery.
    if (finalSag < 0.045f) {
        labelSafe = "Excellent";
    }
    else if (finalSag < 0.080f) {
        labelSafe = "Normal";
    }
    else if (finalSag < 0.120f) {
        labelSafe = "Weak";
    }
    else {
        labelSafe = "Critical";
    }

    res.label = labelSafe;
    lab14LastLabel = labelSafe;

    // The physical delta remains visible; legacy Excellent/Weak/Critical
    // are heuristic bins and must not be shown as battery-health results.
    String sagText = Lab14CustomerGuidance.voltageReading(finalSag, gr);


    if ("Critical".equals(labelSafe)) {

        // A single sag threshold is not a calibrated cell-failure alarm.
        logLabelWarnValue(
                gr ? "Πτώση τάσης υπό φορτίο"
                   : "Voltage sag under load",
                sagText
        );

    } else if ("Weak".equals(labelSafe)) {

        logLabelWarnValue(
                gr ? "Πτώση τάσης υπό φορτίο"
                   : "Voltage sag under load",
                sagText
        );

    } else if ("Normal".equals(labelSafe)) {

        logLabelValue(
                gr ? "Πτώση τάσης υπό φορτίο"
                   : "Voltage sag under load",
                sagText
        );

    } else {

        // Excellent
        logLabelOkValue(
                gr ? "Πτώση τάσης υπό φορτίο"
                   : "Voltage sag under load",
                sagText
        );
    }
}

// =====================================================
// 🔴 POWER (FINAL — DYNAMIC + SAFE)
// =====================================================
boolean powerValid =
        !Float.isNaN(res.powerMw) &&
        res.powerMw >= 1500f &&   // 🔴 real load threshold
        res.powerMw < 15000f;     // 🔴 sanity cap

if (powerValid) {

    String powerLabel;

    if (res.powerMw >= 7000f) {
        powerLabel = "High";
    } else if (res.powerMw >= 3500f) {
        powerLabel = "Normal";
    } else {
        powerLabel = "Low";
    }

    String powerTextLocal = String.format(
            Locale.US,
            "%.0f mW (%s)",
            res.powerMw,
            powerLabel
    );

    if ("High".equals(powerLabel)) {

        logLabelOkValue(
                gr ? "Ικανότητα ισχύος" : "Power capability",
                powerTextLocal
        );

    } else if ("Normal".equals(powerLabel)) {

        logLabelValue(
                gr ? "Ικανότητα ισχύος" : "Power capability",
                powerTextLocal
        );

    } else {

        logLabelWarnValue(
                gr ? "Ικανότητα ισχύος" : "Power capability",
                powerTextLocal
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Ικανότητα ισχύος" : "Power capability",
            gr
                    ? "Μη αξιόπιστη μέτρηση"
                    : "Unreliable measurement"
    );
}

// =====================================================
// 🔴 RESISTANCE (FINAL — VALIDATED + INTERPRETED)
// =====================================================
boolean rValid =
        internalResistance != null &&
        internalResistance.length > 0 &&
        !Float.isNaN(internalResistance[0]);

if (rValid) {

    float rMilli = internalResistance[0] * 1000f;

    if (rMilli >= 1f && rMilli <= 300f) {

        String rLabel;

        if (rMilli < 50f) {
            rLabel = "Excellent";
        } else if (rMilli < 90f) {
            rLabel = "Good";
        } else if (rMilli < 140f) {
            rLabel = "Normal";
        } else {
            rLabel = "High";
        }

        String text = String.format(
                Locale.US,
                "%.1f mΩ (%s)",
                rMilli,
                rLabel
        );

        if ("Excellent".equals(rLabel) ||
            "Good".equals(rLabel)) {

            logLabelOkValue(
                gr ? "Αντίσταση υπό φορτίο"
                   : "Dynamic resistance",
                text
            );

        } else if ("Normal".equals(rLabel)) {

            logLabelValue(
                gr ? "Αντίσταση υπό φορτίο"
                   : "Dynamic resistance",
                text
            );

        } else {

            logLabelWarnValue(
                gr ? "Αντίσταση υπό φορτίο"
                   : "Dynamic resistance",
                text
            );
        }

    } else {

        logLabelWarnValue(
                gr ? "Αντίσταση υπό φορτίο"
                   : "Dynamic resistance",
                gr ? "Μη αξιόπιστη μέτρηση"
                   : "Unreliable measurement"
        );
    }

 } else {
    // The previous fixed-2A denominator was not a measured current step.
    // Dynamic R = DeltaV / DeltaI requires aligned, observed current values.
    logLabelWarnValue(
            gr ? "Δυναμική αντίσταση" : "Dynamic resistance",
            gr ? "Δεν προσδιορίζεται χωρίς συγχρονισμένη μεταβολή ρεύματος"
               : "Not established without synchronized current change"
    );
}

// =====================================================
// =====================================================
// VOLTAGE RECOVERY (FINAL + FALLBACK)
// =====================================================

if (voltageRecovery != null &&
    voltageRecovery.length > 0 &&
    !Float.isNaN(voltageRecovery[0])) {

    float rec = voltageRecovery[0];

    if (rec >= 0.002f && rec <= 1.0f) {

        String recLabel;

        if (rec >= 0.080f) {
            recLabel = "Strong";
        } else if (rec >= 0.040f) {
            recLabel = "Normal";
        } else {
            recLabel = "Weak";
        }

        String text = String.format(
                Locale.US,
                "%.3f V (%s)",
                rec,
                recLabel
        );

        if ("Strong".equals(recLabel)) {

            logLabelOkValue(
                    gr ? "Ανάκαμψη τάσης"
                       : "Voltage recovery",
                    text
            );

        } else if ("Normal".equals(recLabel)) {

            logLabelValue(
                    gr ? "Ανάκαμψη τάσης"
                       : "Voltage recovery",
                    text
            );

        } else {

            logLabelWarnValue(
                    gr ? "Ανάκαμψη τάσης"
                       : "Voltage recovery",
                    text
            );
        }

    } else {

        logLabelWarnValue(
                gr ? "Ανάκαμψη τάσης"
                   : "Voltage recovery",
                gr ? "Μη αξιόπιστη μέτρηση"
                   : "Unreliable measurement"
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Ανάκαμψη τάσης"
               : "Voltage recovery",
            "N/A"
    );
}

// 🔴 RECOVERY SPEED
boolean speedValid =
        voltageRecoverySpeed != null &&
        voltageRecoverySpeed.length > 0 &&
        !Float.isNaN(voltageRecoverySpeed[0]);

if (speedValid) {

    float spd = voltageRecoverySpeed[0];

    if (spd > 0.0001f && spd < 1.0f) {

        logLabelValue(
                gr ? "Ταχύτητα ανάκαμψης"
                   : "Recovery speed",
                String.format(Locale.US, "%.4f V/sec", spd)
        );

    } else {

        logLabelWarnValue(
                gr ? "Ταχύτητα ανάκαμψης"
                   : "Recovery speed",
                gr ? "Μη αξιόπιστη μέτρηση"
                   : "Unreliable measurement"
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Ταχύτητα ανάκαμψης"
               : "Recovery speed",
            gr ? "Μη διαθέσιμη"
               : "N/A"
    );
}

// =====================================================
// 🔴 THERMAL (FINAL — VALIDATED + INTERPRETED)
// =====================================================
float deltaTemp = Float.NaN;

boolean tempValid =
        !Float.isNaN(res.tempStart) &&
        !Float.isNaN(res.tempEnd);

if (tempValid) {

    float dt = res.tempEnd - res.tempStart;

    // 🔴 sanity filter
    if (dt > -2f && dt < 20f) {

        deltaTemp = dt;

        String tempLabel;

        if (deltaTemp < 2.5f) {
            tempLabel = "Normal";
        } else if (deltaTemp < 6.5f) {
            tempLabel = "Warm";
        } else {
            tempLabel = "High";
        }

        String tempTextLocal = String.format(
                Locale.US,
                "%.1f°C (%s)",
                deltaTemp,
                tempLabel
        );

        if ("High".equals(tempLabel)) {

            logLabelWarnValue(
                    gr ? "Θερμική μεταβολή"
                       : "Thermal change",
                    tempTextLocal
            );

        } else if ("Warm".equals(tempLabel)) {

            logLabelValue(
                    gr ? "Θερμική μεταβολή"
                       : "Thermal change",
                    tempTextLocal
            );

        } else {

            logLabelOkValue(
                    gr ? "Θερμική μεταβολή"
                       : "Thermal change",
                    tempTextLocal
            );
        }

    } else {

        logLabelWarnValue(
                gr ? "Θερμική μεταβολή"
                   : "Thermal change",
                gr ? "Μη αξιόπιστη μέτρηση"
                   : "Unreliable measurement"
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Θερμική μεταβολή"
               : "Thermal change",
            gr ? "Μη διαθέσιμη"
               : "N/A"
    );
}

// =====================================================
// 🔴 DRAIN (FINAL — VALIDATED + INTERPRETED)
// =====================================================
double drainRate = Double.NaN;

boolean drainValid =
        res.validDrain &&
        res.durationMs > 0 &&
        res.drainMah > 0;

if (drainValid) {

    drainRate =
            (res.drainMah * 3600000.0) / res.durationMs;

    // 🔴 sanity range
    if (drainRate >= 200 && drainRate <= 6000) {

        String drainLabel;

        if (drainRate >= 3000) {
            drainLabel = "High";
        } else if (drainRate >= 1500) {
            drainLabel = "Normal";
        } else {
            drainLabel = "Low";
        }

        String text = String.format(
                Locale.US,
                "%.0f mAh/h (%s)",
                drainRate,
                drainLabel
        );

        if ("High".equals(drainLabel)) {

            logLabelOkValue(
                    gr ? "Ρυθμός αποφόρτισης"
                       : "Drain rate",
                    text
            );

        } else if ("Normal".equals(drainLabel)) {

            logLabelValue(
                    gr ? "Ρυθμός αποφόρτισης"
                       : "Drain rate",
                    text
            );

        } else {

            logLabelWarnValue(
                    gr ? "Ρυθμός αποφόρτισης"
                       : "Drain rate",
                    text
            );
        }

    } else {

        logLabelWarnValue(
                gr ? "Ρυθμός αποφόρτισης"
                   : "Drain rate",
                gr ? "Μη αξιόπιστη μέτρηση"
                   : "Unreliable measurement"
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Ρυθμός αποφόρτισης"
               : "Drain rate",
            gr ? "Μη διαθέσιμο"
               : "N/A"
    );
}

// =====================================================
// 🔴 ENERGY (FINAL — VALIDATED + INTERPRETED)
// =====================================================
boolean energyValid =
        !Float.isNaN(energyEfficiency) &&
        energyEfficiency > 10f &&
        energyEfficiency < 5000f;

if (energyValid) {

    String energyLabel;

    if (energyEfficiency >= 1000f) {
        energyLabel = "Efficient";
    } else if (energyEfficiency >= 500f) {
        energyLabel = "Normal";
    } else {
        energyLabel = "Inefficient";
    }

    String text = String.format(
            Locale.US,
            "%.0f mAh/V (%s)",
            energyEfficiency,
            energyLabel
    );

    if ("Efficient".equals(energyLabel)) {

        logLabelOkValue(
                gr ? "Ενεργειακή αποδοτικότητα"
                   : "Energy efficiency",
                text
        );

    } else if ("Normal".equals(energyLabel)) {

        logLabelValue(
                gr ? "Ενεργειακή αποδοτικότητα"
                   : "Energy efficiency",
                text
        );

    } else {

        logLabelWarnValue(
                gr ? "Ενεργειακή αποδοτικότητα"
                   : "Energy efficiency",
                text
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Ενεργειακή αποδοτικότητα"
               : "Energy efficiency",
            gr ? "Μη διαθέσιμη / μη αξιόπιστη"
               : "N/A / unreliable"
    );
}

// =====================================================
// CUSTOMER GUIDANCE: OBSERVATION, NOT CAPACITY LOSS CERTIFICATION
// =====================================================
boolean observedSag = sagValid && !lab14_systemLimited[0];
String recommendation = Lab14CustomerGuidance.recommendation(
        gr, lab14_systemLimited[0], observedSag);
logLabelWarnValue(gr ? "Σύσταση" : "Recommendation", recommendation);
logLabelWarnValue(gr ? "Περιορισμός διάγνωσης" : "Diagnostic limitation",
        gr ? "Η στιγμιαία πτώση τάσης δεν μετρά την πλήρη χωρητικότητα, το SoH ή το αν απαιτείται επέμβαση."
           : "Short-duration voltage sag does not measure full-charge capacity, SoH or necessity of repair.");
logLabelWarnValue(gr ? "Σύνοψη" : "Summary",
        Lab14CustomerGuidance.evidenceLabel(gr, lab14_systemLimited[0], observedSag));
logLabelValue(gr ? "Ερμηνεία ένδειξης" : "Evidence interpretation",
        Lab14CustomerGuidance.electricalWarning(gr, observedSag));
}

// ============================================================
// ============================================================
// LAB 14 — LOG FINAL RESULT (FINAL — SAFE + CONSISTENT)
// ============================================================
private void lab14LogFinalScore(
        boolean gr,
        String label,
        float powerMw,
        boolean[] collapseRisk,
        boolean smartSwelling,
        boolean[] calibrationDrift,
        boolean[] lab14_systemLimited,
        boolean hasMeasuredVoltageSag
) {
    appendHtml("<br>");
    logLine();
    logInfo(gr ? "Τελικό αποτέλεσμα" : "Final result");
    logLine();

    boolean limited = lab14_systemLimited != null
            && lab14_systemLimited.length > 0 && lab14_systemLimited[0];
    // An internal grade is not evidence. Use the actual measured-sag gate.
    boolean observed = !limited && hasMeasuredVoltageSag;

    logLabelWarnValue(gr ? "Ηλεκτρική απόκριση" : "Electrical response",
            Lab14CustomerGuidance.evidenceLabel(gr, limited, observed));
    logLabelWarnValue(gr ? "Όρια συμπεράσματος" : "Interpretation limit",
            gr ? "Η δοκιμή δεν πιστοποιεί απώλεια χωρητικότητας, φθορά κυψελών ή ανάγκη επέμβασης."
               : "This test does not certify capacity loss, cell wear, or need for intervention.");

    if (collapseRisk != null && collapseRisk.length > 0 && collapseRisk[0]) {
        logLabelWarnValue(gr ? "Σύνθετες ενδείξεις φορτίου" : "Load-response flags",
                gr ? "Καταγράφηκε ασυνήθιστος συνδυασμός σημάτων· απαιτείται επανάληψη και τεχνική αξιολόγηση."
                   : "Unusual combination of signals observed; repeat test and seek technician assessment.");
    } else {
        logLabelValue(gr ? "Κατάρρευση τάσης" : "Voltage collapse",
                gr ? "Δεν αξιολογείται ως κίνδυνος από αυτή τη δοκιμή"
                   : "Failure risk not assessed by this test");
    }
    logLabelWarnValue(gr ? "Διόγκωση / ακεραιότητα κυψελών"
                         : "Swelling / cell integrity",
            Lab14CustomerGuidance.unverifiedCellCondition(gr));
    logLabelWarnValue(gr ? "Βαθμονόμηση" : "Calibration",
            gr ? "Δεν επιβεβαιώνεται ανεξάρτητα από την παρούσα δοκιμή."
               : "Not independently verified by this test.");
}

// ============================================================
// ============================================================
// LAB 14 — LOG SAVE (FINAL — SAFE + STRICT)
// ============================================================
private void lab14LogSave(
        boolean gr,
        SharedPreferences p,
        boolean[] variabilityDetected,
        boolean[] collapseRisk,
        boolean smartSwelling,
        boolean[] calibrationDrift,
        boolean batteryAuthenticitySuspicion,
        boolean partial,
        boolean[] lab14_systemLimited
) {

    // 🔴 SAFE FLAGS
    boolean variability =
            variabilityDetected != null &&
            variabilityDetected.length > 0 &&
            variabilityDetected[0];

    boolean collapse =
            collapseRisk != null &&
            collapseRisk.length > 0 &&
            collapseRisk[0];

    boolean calibration =
            calibrationDrift != null &&
            calibrationDrift.length > 0 &&
            calibrationDrift[0];

    boolean limiter =
            lab14_systemLimited != null &&
            lab14_systemLimited.length > 0 &&
            lab14_systemLimited[0];

    // ------------------------------------------------
    // 🔴 SAVE CONDITION (STRICT)
    // ------------------------------------------------
    boolean canSave =
            p != null &&
            !partial &&
            !limiter;

    if (canSave) {

        try {

            p.edit()
                    .putBoolean("lab14_unstable_measurement", variability)
                    .putBoolean("lab14_collapse_risk", collapse)
                    .putBoolean("lab14_swelling_risk", smartSwelling)
                    .putBoolean("lab14_calibration_drift", calibration)
                    .putBoolean("lab14_battery_auth_suspect", batteryAuthenticitySuspicion)
                    .putLong("lab14_last_ts", System.currentTimeMillis())
                    .apply();

            logLabelOkValue(
                    gr ? "Αποθήκευση αποτελέσματος"
                            : "Result storage",
                    gr ? "Το αποτέλεσμα αποθηκεύτηκε"
                            : "Result stored"
            );

        } catch (Throwable t) {

            logLabelWarnValue(
                    gr ? "Αποθήκευση αποτελέσματος"
                            : "Result storage",
                    gr ? "Σφάλμα αποθήκευσης"
                            : "Storage error"
            );
        }

    } else {

        logWarn(gr
                ? "Η εκτέλεση δεν αποθηκεύτηκε (partial / limiter / invalid run)."
                : "Run not saved (partial / limiter / invalid run).");

        logLabelWarnValue(
                gr ? "Αποθήκευση αποτελέσματος"
                        : "Result storage",
                gr ? "Δεν αποθηκεύτηκε"
                        : "Not stored"
        );
    }
}

// ============================================================
// LAB 14 — UI CLEANUP HELPER (FINAL — SAFE UI THREAD)
// ============================================================
private void lab14CleanupUI() {

    try {
        if (ui != null) {
            ui.removeCallbacks(lab14VibrationLoop);
        }
    } catch (Throwable ignore) {}

    runOnUiThread(() -> {

        // ----------------------------------------------------
        // 🔴 VIDEO CLEANUP
        // ----------------------------------------------------
        try {
            if (lab14StressVideo != null) {

                try { lab14StressVideo.stopPlayback(); } catch (Throwable ignore) {}
                try { lab14StressVideo.suspend(); } catch (Throwable ignore) {}

                try {
                    ViewParent parent = lab14StressVideo.getParent();
                    if (parent instanceof ViewGroup) {
                        ((ViewGroup) parent).removeView(lab14StressVideo);
                    }
                } catch (Throwable ignore) {}

                try {
                    lab14StressVideo.setOnPreparedListener(null);
                    lab14StressVideo.setOnCompletionListener(null);
                    lab14StressVideo.setOnErrorListener(null);
                } catch (Throwable ignore) {}

                lab14StressVideo = null;
            }
        } catch (Throwable ignore) {}

        // ----------------------------------------------------
        // 🔴 DIALOG CLEANUP
        // ----------------------------------------------------
        try {
            if (lab14Dialog != null) {
                try { lab14Dialog.dismiss(); } catch (Throwable ignore) {}
            }
        } catch (Throwable ignore) {}

        lab14Dialog = null;
    });
}

// ============================================================
// CPU FREQ READ (FINAL — MULTI CORE + SAFE)
// ============================================================
private long readCpuFreq() {

    long maxFreq = -1;

    try {

        File cpuDir = new File("/sys/devices/system/cpu/");

        File[] cores = cpuDir.listFiles((dir, name) ->
                name != null && name.matches("cpu[0-9]+"));

        if (cores == null || cores.length == 0) {
            return -1;
        }

        for (File core : cores) {

            File f = new File(core, "cpufreq/scaling_cur_freq");

            if (!f.exists()) continue;

            BufferedReader br = null;

            try {

                br = new BufferedReader(new FileReader(f));
                String s = br.readLine();

                if (s == null) continue;

                long freq = Long.parseLong(s.trim());

                // 🔴 sanity (Hz)
                if (freq > 100_000 && freq < 10_000_000) {
                    if (freq > maxFreq) {
                        maxFreq = freq;
                    }
                }

            } catch (Throwable ignore) {

            } finally {
                try { if (br != null) br.close(); } catch (Throwable ignore) {}
            }
        }

    } catch (Throwable ignore) {}

    return maxFreq;
}

// ============================================================
// CPU TEMP READ (FINAL — MULTI ZONE + FILTERED)
// ============================================================
private float readCpuTempSafe2() {

    float best = Float.NaN;

    try {

        File base = new File("/sys/class/thermal/");
        File[] zones = base.listFiles((dir, name) ->
                name != null && name.startsWith("thermal_zone"));

        if (zones == null) return Float.NaN;

        for (File z : zones) {

            try {

                // 🔴 TYPE CHECK
                File typeFile = new File(z, "type");
                if (!typeFile.exists()) continue;

                String type = readLineSafe(typeFile);
                if (type == null) continue;

                String t = type.toLowerCase();

                // 🔴 accept CPU-like zones only
                if (!(t.contains("cpu") ||
                      t.contains("soc") ||
                      t.contains("big") ||
                      t.contains("little"))) {
                    continue;
                }

                // 🔴 TEMP READ
                File tempFile = new File(z, "temp");
                if (!tempFile.exists()) continue;

                String s = readLineSafe(tempFile);
                if (s == null) continue;

                float val = Float.parseFloat(s.trim()) / 1000f;

                // 🔴 SANITY FILTER
                if (!Float.isNaN(val) && val > 20f && val < 110f) {

                    if (Float.isNaN(best) || val > best) {
                        best = val; // 🔥 take hottest CPU zone
                    }
                }

            } catch (Throwable ignore) {}
        }

    } catch (Throwable ignore) {}

    return best;
}

// 🔴 HELPER
private String readLineSafe(File f) {
    BufferedReader br = null;
    try {
        br = new BufferedReader(new FileReader(f));
        return br.readLine();
    } catch (Throwable ignore) {
        return null;
    } finally {
        try { if (br != null) br.close(); } catch (Throwable ignore) {}
    }
}

// ============================================================
// RUN COUNTER (FINAL — SAFE + CLAMPED)
// ============================================================
private void incLab14RunCount(boolean valid) {

    if (!valid) return;

    try {

        SharedPreferences sp =
                getSharedPreferences(LAB14_PREFS, MODE_PRIVATE);

        int runs = sp.getInt(KEY_LAB14_RUNS, 0);

        // 🔴 SANITY CLAMP
        if (runs < 0 || runs > 1_000_000) {
            runs = 0;
        }

        int newRuns = runs + 1;

        boolean ok = sp.edit()
                .putInt(KEY_LAB14_RUNS, newRuns)
                .commit(); // 🔴 sync write (important here)

        if (!ok) {
            logWarn("RUN_COUNTER_WRITE_FAIL");
        }

    } catch (Throwable t) {

        if (DEBUG_MODE) {
            logWarn("RUN_COUNTER_ERROR: " + t.getMessage());
        }
    }
}

// ============================================================
// CHARGING STATE (FINAL — STRICT + RELIABLE)
// ============================================================
private boolean isChargingNowSafe() {

    // ----------------------------------------------------
    // 🔴 PRIMARY (ENGINE)
    // ----------------------------------------------------
    try {

        iDoctorEngine idoctor =
                iDoctorEngine.get(ManualTestsActivity.this);

        Boolean charging = idoctor.isChargingNowUnified();

        if (charging != null) {
            return charging;
        }

    } catch (Throwable ignore) {}

    // ----------------------------------------------------
    // 🔴 FALLBACK (SYSTEM INTENT — CORRECT)
    // ----------------------------------------------------
    try {

        IntentFilter f =
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        Intent i = registerReceiver(null, f);

        if (i == null) return false;

        int status =
                i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        // 🔴 TRUE charging states
        return status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;

    } catch (Throwable ignore) {}

    // ----------------------------------------------------
    // 🔴 SAFE DEFAULT
    // ----------------------------------------------------
    return false;
}

// ============================================================
// LAB 14 — HARD STOP ALL STRESS (FINAL — ALWAYS CLEAN)
// ============================================================
private void lab14StopAllStress() {

    // 🔴 ALWAYS STOP (no guard)
    lab14Running = false;

    try {

        // ----------------------------------------------------
        // 🔴 STOP HANDLER
        // ----------------------------------------------------
        if (ui != null) {
            ui.removeCallbacks(lab14VibrationLoop);
        }

        // ----------------------------------------------------
        // 🔴 CPU / MEMORY / GPU
        // ----------------------------------------------------
        try { stopCpuBurn(); } catch (Throwable ignore) {}
        try { stopMemoryStress(); } catch (Throwable ignore) {}
        try { stopMemoryBandwidthStress(); } catch (Throwable ignore) {}
        try { stopGpuStress(); } catch (Throwable ignore) {}

        // ----------------------------------------------------
        // 🔴 VIBRATION
        // ----------------------------------------------------
        try { stopVibrationStress(); } catch (Throwable ignore) {}

        try {
            Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vib != null) vib.cancel();
        } catch (Throwable ignore) {}

        // ----------------------------------------------------
        // 🔴 UI CLEANUP (UI THREAD)
        // ----------------------------------------------------
        runOnUiThread(() -> {

            // 🔴 VIDEO FULL CLEAN
            try {
                if (lab14StressVideo != null) {

                    try { lab14StressVideo.stopPlayback(); } catch (Throwable ignore) {}
                    try { lab14StressVideo.suspend(); } catch (Throwable ignore) {}

                    try {
                        ViewParent parent = lab14StressVideo.getParent();
                        if (parent instanceof ViewGroup) {
                            ((ViewGroup) parent).removeView(lab14StressVideo);
                        }
                    } catch (Throwable ignore) {}

                    try {
                        lab14StressVideo.setOnPreparedListener(null);
                        lab14StressVideo.setOnCompletionListener(null);
                        lab14StressVideo.setOnErrorListener(null);
                    } catch (Throwable ignore) {}

                    lab14StressVideo = null;
                }
            } catch (Throwable ignore) {}

        });

        // ----------------------------------------------------
        // 🔴 SCREEN RESTORE
        // ----------------------------------------------------
        try { restoreBrightnessAndKeepOn(); } catch (Throwable ignore) {}

    } catch (Throwable ignore) {}
}

// ------------------------------------------------------------
// BATTERY CHARGING STATE HELPER (FINAL — UNIFIED)
// ------------------------------------------------------------
private boolean isCharging() {

    // ----------------------------------------------------
    // 🔴 PRIMARY (ENGINE — VALIDATED)
    // ----------------------------------------------------
    try {

        iDoctorEngine idoctor = iDoctorEngine.get(this);

        Boolean charging = idoctor.isChargingNowUnified();

        if (charging != null) {
            return charging;
        }

    } catch (Throwable ignore) {
        // fallback
    }

    // ----------------------------------------------------
    // 🔴 FALLBACK (SYSTEM INTENT — CORRECT)
    // ----------------------------------------------------
    try {

        IntentFilter f =
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        Intent i = registerReceiver(null, f);

        if (i == null) return false;

        int status =
                i.getIntExtra(
                        BatteryManager.EXTRA_STATUS,
                        -1
                );

        return status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;

    } catch (Throwable ignore) {}

    // ----------------------------------------------------
    // 🔴 SAFE DEFAULT
    // ----------------------------------------------------
    return false;
}

// ============================================================
// LAB 14 — LIMITER DETECTION (FINAL — CORRECTED)
// ============================================================
private boolean lab14DetectLimiter(
        float vStart,
        float vLoad,
        long startMah,
        long endMah,
        float tempStart,
        float tempEnd,
        float currentNow,
        long dtMs
) {

    if (dtMs < 20000) return false;

    // ----------------------------------------------------
    // 🔴 SAG (LOCAL ONLY)
    // ----------------------------------------------------
    float sag = Float.NaN;

    if (!Float.isNaN(vStart) && !Float.isNaN(vLoad)) {

        float tmp = vStart - vLoad;

        if (tmp > 0.005f && tmp < 1.0f) {
            sag = tmp;
        }
    }

    // ----------------------------------------------------
    // 🔴 DRAIN → NORMALIZED (mAh/h)
    // ----------------------------------------------------
    double drainPerHour = Double.NaN;

    if (startMah > 0 && endMah > 0 && dtMs > 0) {

        long d = startMah - endMah;

        if (d >= 0 && d < 10000) {
            drainPerHour = (d * 3600000.0) / dtMs;
        }
    }

    // ----------------------------------------------------
    // 🔴 TEMP RISE
    // ----------------------------------------------------
    float tempRise = Float.NaN;

    if (!Float.isNaN(tempStart) && !Float.isNaN(tempEnd)) {

        float t = tempEnd - tempStart;

        if (t > -5f && t < 30f) {
            tempRise = t;
        }
    }

    // ----------------------------------------------------
    // 🔴 CURRENT
    // ----------------------------------------------------
    float current = Float.NaN;

    if (!Float.isNaN(currentNow)) {

        float c = Math.abs(currentNow);

        if (c > 50f && c < 15000f) {
            current = c;
        }
    }

    // ----------------------------------------------------
    // 🔴 NORMALIZATION
    // ----------------------------------------------------
    if (!Float.isNaN(sag) && sag < 0.003f) {
        sag = 0f;
    }

    if (!Float.isNaN(tempRise) && tempRise < 0.3f) {
        tempRise = 0f;
    }

    // ----------------------------------------------------
    // 🔴 FLAGS (BALANCED)
    // ----------------------------------------------------
    boolean lowSag =
            !Float.isNaN(sag) &&
            sag < 0.012f;

    boolean lowDrain =
        !Double.isNaN(drainPerHour) &&
        drainPerHour < 1000;

    boolean lowTemp =
        !Float.isNaN(tempRise) &&
        tempRise < 1.0f;

    boolean lowCurrent =
        !Float.isNaN(current) &&
        current < 150f;

    // ----------------------------------------------------
    // 🔴 HARD LIMIT (ALL 4)
    // ----------------------------------------------------
    if (lowSag && lowDrain && lowCurrent && lowTemp) {
        return true;
    }

    // ----------------------------------------------------
    // 🔴 STRONG SIGNAL (3/4)
    // ----------------------------------------------------
int flags = 0;

if (lowSag) flags += 2;       // 🔴 ΚΥΡΙΟ ΣΗΜΑ
if (lowCurrent) flags += 1;
if (lowDrain) flags += 1;
if (lowTemp) flags += 1;

    if (flags >= 3) {
        return true;
    }

    // ----------------------------------------------------
    // 🔴 LONG RUN CASE
    // ----------------------------------------------------
    if (dtMs > 30000 &&
        lowSag &&
        lowCurrent &&
        lowTemp) {

        return true;
    }

    return false;
}

 void lab14PostLoadAnalysis(

        Lab14Engine engine,
        boolean gr,
        long startMah,
        long baselineFullMah,
        long t0,
        float voltageStart,
        int batteryPercent,
        long cycles,
        float tempStart
) {

float currentNow = Float.NaN;
float powerMilliWatt = Float.NaN;
double drainPercentPerHour = 0;

Lab14Result res = new Lab14Result();

final iDoctorEngine idoctor =
        iDoctorEngine.get(ManualTestsActivity.this);

try {

// 🔴 CANCEL CHECK
if (lab14Cancelled) {

    lab14FastDone = true;
    lab14FastPhase = false;

    lab14StopAllStress(); // 🔴 IMPORTANT
    lab14Running = false;

    return;
}

// 🔴 FINAL SNAPSHOT
iDoctorEngine.BatterySnapshot snapEnd =
        idoctor.readBatterySnapshotLab();

if (snapEnd == null || snapEnd.chargeNowMah <= 0) {

    logError(gr
            ? "Αποτυχία τελικής ανάγνωσης μπαταρίας."
            : "Final battery snapshot failed.");

    lab14StopAllStress();

    try {
        counterText = null;
        lab14CleanupUI();
    } catch (Throwable ignore) {}

    lab14Running = false;

    return;
}

// 🔴 CURRENT (CRITICAL)
try {
    currentNow = lab14Current(); // unified helper

    if (Float.isNaN(currentNow) && snapEnd.currentMa != 0f) {
        currentNow = snapEnd.currentMa;
    }

} catch (Throwable ignore) {}

// 🔴 FALLBACK CURRENT (LAST RESORT)
if (Float.isNaN(currentNow)) {

    try {
        float alt = idoctor.getBatteryCurrentNowUnified();

        if (!Float.isNaN(alt) &&
            Math.abs(alt) > 50f &&
            Math.abs(alt) < 15000f) {

            currentNow = alt;
        }

    } catch (Throwable ignore) {}
}

// 🔴 VOLTAGE FALLBACK SAFETY
if (Float.isNaN(voltageUnderLoad[0])) {

    try {
        float v = lab14Voltage();

        if (!Float.isNaN(v)) {
            voltageUnderLoad[0] = v;
        }

    } catch (Throwable ignore) {}
}

// =====================================================
// 🔴 SAG PIPELINE (FINAL — UNIFIED + MIN-BASED)
// =====================================================

// 🔴 FIND BEST LOAD VOLTAGE (LOWEST = TRUE LOAD)
float bestLoad = Float.NaN;

// voltageUnderLoad
if (voltageUnderLoad != null &&
    voltageUnderLoad.length > 0 &&
    !Float.isNaN(voltageUnderLoad[0])) {

    bestLoad = voltageUnderLoad[0];
}

// vLoad1
if (vLoad1 != null &&
    vLoad1.length > 0 &&
    !Float.isNaN(vLoad1[0])) {

    if (Float.isNaN(bestLoad) || vLoad1[0] < bestLoad) {
        bestLoad = vLoad1[0];
    }
}

// vLoad2
if (vLoad2 != null &&
    vLoad2.length > 0 &&
    !Float.isNaN(vLoad2[0])) {

    if (Float.isNaN(bestLoad) || vLoad2[0] < bestLoad) {
        bestLoad = vLoad2[0];
    }
}

// ----------------------------------------------------
// 🔴 FINAL SAG (TRUE VALUE)
// ----------------------------------------------------
float finalSag = Float.NaN;

if (!Float.isNaN(voltageStart) &&
    !Float.isNaN(bestLoad)) {

    float tmp = voltageStart - bestLoad;

    if (tmp > 0.005f && tmp < 1.0f) {
        finalSag = tmp;
    }
}

// ----------------------------------------------------
// 🔴 SAG1 / SAG2 (FOR CONSISTENCY ONLY)
// ----------------------------------------------------
float sag1 = Float.NaN;
float sag2 = Float.NaN;

if (!Float.isNaN(voltageStart)) {

    if (vLoad1 != null && vLoad1.length > 0 && !Float.isNaN(vLoad1[0])) {
        float tmp = voltageStart - vLoad1[0];
        if (tmp > 0.002f && tmp < 1.0f) sag1 = tmp;
    }

    if (vLoad2 != null && vLoad2.length > 0 && !Float.isNaN(vLoad2[0])) {
        float tmp = voltageStart - vLoad2[0];
        if (tmp > 0.002f && tmp < 1.0f) sag2 = tmp;
    }
}

// ----------------------------------------------------
// 🔴 SAG AVG (CONSISTENCY METRIC ONLY)
// ----------------------------------------------------
float sagAvg = Float.NaN;

if (!Float.isNaN(sag1) && !Float.isNaN(sag2)) {
    sagAvg = (sag1 + sag2) / 2f;
} else if (!Float.isNaN(sag1)) {
    sagAvg = sag1;
} else if (!Float.isNaN(sag2)) {
    sagAvg = sag2;
}

// =====================================================
// 🔴 VALIDATION (RELAXED PRODUCTION FINAL)
// =====================================================

// optional repeatability info (informational only)
boolean repeatabilityGood =
        !Float.isNaN(sag1) &&
        !Float.isNaN(sag2) &&
        Math.abs(sag1 - sag2) < 0.08f;

// 🔴 FINAL RELIABILITY (single-source physics)
boolean hasReliableSag =
        !Float.isNaN(finalSag) &&
        finalSag >= 0.015f &&
        finalSag < 1.0f;

// ----------------------------------------------------
// 🔴 SOFT ELECTRICAL WARNING — CONTINUE FULL REPORT
// ----------------------------------------------------
if (!hasReliableSag) {

    logLine();

    logWarn(gr
            ? "Ασθενές ή μη σαφές σήμα sag"
            : "Weak or unclear sag signal");

    logWarn(gr
            ? "Δεν είναι δυνατή πλήρης ηλεκτρική ανάλυση μπαταρίας"
            : "Full electrical battery analysis is not possible");

    logWarn(gr
            ? "Συνεχίζουμε με θερμικά / drain / power / confidence δεδομένα."
            : "Continuing with thermal / drain / power / confidence data.");

    logLine();

    res.label = "Insufficient electrical data";
    lab14LastLabel = res.label;

    logLabelWarnValue(
            gr ? "Πτώση τάσης υπό φορτίο"
               : "Voltage sag under load",
            gr ? "Μη μετρήσιμο voltage sag"
               : "No measurable voltage sag"
    );

} else {

    // ------------------------------------------------
    // 🔴 FINAL SAG OUTPUT
    // ------------------------------------------------
    String sagLabel;

    if (finalSag >= 0.12f) {

        sagLabel = "Critical";
        res.label = "Critical";

    } else if (finalSag >= 0.08f) {

        sagLabel = "Weak";
        res.label = "Weak";

    } else if (finalSag >= 0.045f) {

        sagLabel = "Normal";
        res.label = "Normal";

    } else {

        sagLabel = "Excellent";
        res.label = "Excellent";
    }

    lab14LastLabel = res.label;

    // Do not stamp the measured sag with an uncalibrated health grade.
    String sagText = Lab14CustomerGuidance.voltageReading(finalSag, gr);

    if ("Critical".equals(sagLabel)) {

        // Elevated *observation* merits review, not a certified emergency.
        logLabelWarnValue(
                gr ? "Πτώση τάσης υπό φορτίο"
                   : "Voltage sag under load",
                sagText
        );

    } else if ("Weak".equals(sagLabel)) {

        logLabelWarnValue(
                gr ? "Πτώση τάσης υπό φορτίο"
                   : "Voltage sag under load",
                sagText
        );

    } else {

        logLabelOkValue(
                gr ? "Πτώση τάσης υπό φορτίο"
                   : "Voltage sag under load",
                sagText
        );
    }
}

// ----------------------------------------------------
// 🔴 OPTIONAL DIAGNOSTIC EXTRA (NO GATING)
// ----------------------------------------------------
if (!Float.isNaN(sag1)) {

    logLabelValue(
            "Sag 1",
            String.format(Locale.US,"%.3f V", sag1)
    );
}

if (!Float.isNaN(sag2)) {

    logLabelValue(
            "Sag 2",
            String.format(Locale.US,"%.3f V", sag2)
    );
}

if (!Float.isNaN(sagAvg)) {

    logLabelValue(
            "Sag Avg",
            String.format(Locale.US,"%.3f V", sagAvg)
    );
}

if (!Float.isNaN(sag1) && !Float.isNaN(sag2)) {

    logLabelValue(
            gr ? "Επαναληψιμότητα"
               : "Repeatability",
            repeatabilityGood
                    ? (gr ? "Καλή" : "Good")
                    : (gr ? "Μεταβλητή" : "Variable")
    );
}

// =====================================================
// 🔴 STRUCTURE VALIDATION (FINAL — CONSISTENT)
// =====================================================

// 🔴 VALID STRUCTURE = έχουμε start + κάποιο load sample
boolean hasAnyLoad =
        (voltageUnderLoad != null &&
         voltageUnderLoad.length > 0 &&
         !Float.isNaN(voltageUnderLoad[0]))
    || (vLoad1 != null &&
        vLoad1.length > 0 &&
        !Float.isNaN(vLoad1[0]))
    || (vLoad2 != null &&
        vLoad2.length > 0 &&
        !Float.isNaN(vLoad2[0]));

boolean invalidStructure =
        Float.isNaN(voltageStart) ||
        !hasAnyLoad;

if (invalidStructure) {

    logLine();

    logWarn(gr
            ? "Μη έγκυρα δεδομένα"
            : "Invalid data");

    logWarn(gr
            ? "Λείπουν βασικά στοιχεία τάσης"
            : "Missing core voltage data");

    logLine();

    res.label = "Insufficient data";
    lab14LastLabel = res.label;

    runOnUiThread(() -> {

        logLabelWarnValue(
                gr ? "Τελικό αποτέλεσμα" : "Final verdict",
                gr ? "Ανεπαρκή δεδομένα"
                   : "Insufficient data"
        );

        logLabelWarnValue(
                gr ? "Κατάσταση μπαταρίας" : "Battery status",
                gr ? "Δεν είναι δυνατή η εκτίμηση"
                   : "Estimation not possible"
        );
    });

    // 🔴 PHASE END (CRITICAL)
    lab14FastDone = true;
    lab14FastPhase = false;
    lab14MainPhase = false;

    // 🔴 STOP
    lab14StopAllStress();

    // 🔴 UI CLEAN
    try {
        counterText = null;
        lab14CleanupUI();
    } catch (Throwable ignore) {}

    restoreBrightnessAndKeepOn();

    // 🔴 FINAL STATE (NO CANCEL RESET)
    lab14Running = false;
    lab14PopupShown = false;
    lab14AdvisoryShown = false;
    lab14BoostActive = false;
    lab14SoftPhaseStarted = false;

    return;
}

// ----------------------------------------------------
// DATA (FINAL — CORRECTED)
// ----------------------------------------------------

// 🔴 END CHARGE (PRIMARY)
final long endMah = snapEnd.chargeNowMah;

// 🔴 PRIMARY DRAIN (MOST RELIABLE)
long directDrainMah = -1;

if (startMah > 0 && endMah > 0) {

    long d = startMah - endMah;

    if (d > 0 && d < 10000) {
        directDrainMah = d;
    }
}

// 🔴 SAMPLING DRAIN (SECONDARY)
lab14DeltaMah = 0;

if (lab14MinCharge < Long.MAX_VALUE &&
    lab14MaxCharge > 0 &&
    lab14MaxCharge > lab14MinCharge) {

    lab14DeltaMah = lab14MaxCharge - lab14MinCharge;
}

// 🔴 FINAL DRAIN SELECTION
long finalDrainMah = -1;

if (directDrainMah >= 0) {
    finalDrainMah = directDrainMah;
} else if (lab14DeltaMah > 0) {
    finalDrainMah = lab14DeltaMah;
}

if (DEBUG_MODE) {
    appendLog("DRAIN_DEBUG",
            "startMah=" + startMah +
            " | endMah=" + endMah +
            " | direct=" + directDrainMah +
            " | sampled=" + lab14DeltaMah +
            " | final=" + finalDrainMah +
            " | min=" + lab14MinCharge +
            " | max=" + lab14MaxCharge);
}

// ----------------------------------------------------
// 🔴 TEMPERATURE END
// ----------------------------------------------------
Float tObj = idoctor.getBatteryTempUnified();

float tempEnd = (tObj != null) ? tObj : Float.NaN;

if (Float.isNaN(tempEnd) || tempEnd <= 0f) {
    tempEnd = snapEnd.batteryTempC;
}

// ----------------------------------------------------
// 🔴 THERMAL METRICS (DUAL)
// ----------------------------------------------------

// peak stress (important for limiter / stress behaviour)
float thermalPeakDelta = Float.NaN;

if (!Float.isNaN(tempStart) &&
    !Float.isNaN(lab14TempPeak)) {

    thermalPeakDelta = lab14TempPeak - tempStart;
}

// real end delta (important for final reporting)
float thermalEndDelta = Float.NaN;

if (!Float.isNaN(tempStart) &&
    !Float.isNaN(tempEnd)) {

    thermalEndDelta = tempEnd - tempStart;
}

// =====================================================
// 🔴 TIME BASE (CRITICAL FIX)
// =====================================================
long dtMs =
        Math.max(1, SystemClock.elapsedRealtime() - t0);

if (dtMs < 1000) dtMs = 1000;

// ----------------------------------------------------
// 🔴 SYNC REAL-TIME LIMITER LATCH
// ----------------------------------------------------
if (lab14LimiterLatched) {
    lab14_systemLimited[0] = true;
}

// ----------------------------------------------------
// ENGINE DRAIN
// ----------------------------------------------------
Lab14Engine.DrainSession drainSession =
        engine.endDrainSession();

Lab14Engine.DrainResult drainResult =
        engine.computeDrain(drainSession);

// ====================================================
// DRAIN (ENGINE-BASED)
// ====================================================
long drainMah = 0;
double mahPerHour = 0;
validDrain = false;

// ----------------------------------------------------
// 🔴 1. DIRECT DRAIN (BEST SIGNAL)
// ----------------------------------------------------
long directDrain = -1;

if (startMah > 0 && endMah > 0) {

    long d = startMah - endMah;

    if (d >= 0 && d < 10000) {
        directDrain = d;
    }
}

// ----------------------------------------------------
// 🔴 2. ENGINE DRAIN
// ----------------------------------------------------
if (drainResult != null && drainResult.valid) {

    drainMah = (long) Math.max(0, Math.round(drainResult.drainMah));
    mahPerHour = Math.max(0, drainResult.mahPerHour);
    validDrain = drainMah > 0 && mahPerHour > 0;
}

// ----------------------------------------------------
// 🔴 3. SAMPLING VALIDATION
// ----------------------------------------------------
boolean validCounter =
        lab14ChargeSamples.size() >= 6 &&
        lab14DeltaMah >= 3;

boolean frozen = lab14ChargeSamples.size() <= 1;

if (lab14ChargeSamples.size() > 1) {

    long first = lab14ChargeSamples.get(0);

    for (int i = 1; i < lab14ChargeSamples.size(); i++) {
        if (!lab14ChargeSamples.get(i).equals(first)) {
            frozen = false;
            break;
        }
    }
}

boolean samplingValid =
        validCounter &&
        !frozen &&
        lab14DeltaMah > 0;

// ----------------------------------------------------
// 🔴 FINAL SELECTION (BEST CREDIBLE SIGNAL)
// ----------------------------------------------------

double directRate = Double.NaN;
double sampleRate = Double.NaN;
double engineRate =
        (validDrain && mahPerHour > 0)
                ? mahPerHour
                : Double.NaN;

if (directDrain > 0 && dtMs > 0) {
    directRate =
            (directDrain * 3600000.0) / dtMs;
}

if (samplingValid && lab14DeltaMah > 0 && dtMs > 0) {
    sampleRate =
            (lab14DeltaMah * 3600000.0) / dtMs;
}

// 🔴 TAKE MAX CREDIBLE
double bestRate = 0;

if (!Double.isNaN(directRate))
    bestRate = Math.max(bestRate, directRate);

if (!Double.isNaN(engineRate))
    bestRate = Math.max(bestRate, engineRate);

if (!Double.isNaN(sampleRate))
    bestRate = Math.max(bestRate, sampleRate);

if (bestRate > 0) {

    mahPerHour = bestRate;

    drainMah = Math.round(
            (float)(bestRate * dtMs / 3600000.0)
    );

    validDrain = true;
}

// ----------------------------------------------------
// 🔴 PERCENT
// ----------------------------------------------------
if (baselineFullMah > 0 && mahPerHour > 0) {
    drainPercentPerHour =
            (mahPerHour / baselineFullMah) * 100.0;
}

// ----------------------------------------------------
// 🔴 SAFETY CLAMP
// ----------------------------------------------------
if (drainMah < 0) drainMah = 0;

if (mahPerHour > 20000) {
    drainMah = 0;
    mahPerHour = 0;
    drainPercentPerHour = 0;
    validDrain = false;
}

// =====================================================
// 🔴 POWER CALC (FINAL — ACCURATE)
// =====================================================

// 🔴 CURRENT (prefer normalized)
float estimatedCurrentMa = Float.NaN;

if (mahPerHour >= 150) { // όχι >0
    estimatedCurrentMa = (float)mahPerHour;
}

if (Float.isNaN(estimatedCurrentMa)) {
    estimatedCurrentMa =
            estimateDynamicCurrentMilliAmp(
                    drainMah,
                    dtMs
            );
}

if (Float.isNaN(estimatedCurrentMa) &&
    !Float.isNaN(currentNow) &&
    Math.abs(currentNow) > 50f) {

    estimatedCurrentMa = Math.abs(currentNow);
}

// 🔴 FIND BEST LOAD VOLTAGE (LOWEST)
float vForPower = Float.NaN;

if (voltageUnderLoad != null &&
    voltageUnderLoad.length > 0 &&
    !Float.isNaN(voltageUnderLoad[0])) {

    vForPower = voltageUnderLoad[0];
}

if (vLoad1 != null &&
    vLoad1.length > 0 &&
    !Float.isNaN(vLoad1[0])) {

    if (Float.isNaN(vForPower) || vLoad1[0] < vForPower) {
        vForPower = vLoad1[0];
    }
}

if (vLoad2 != null &&
    vLoad2.length > 0 &&
    !Float.isNaN(vLoad2[0])) {

    if (Float.isNaN(vForPower) || vLoad2[0] < vForPower) {
        vForPower = vLoad2[0];
    }
}

// 🔴 POWER CALC
if (!Float.isNaN(vForPower) &&
    !Float.isNaN(estimatedCurrentMa) &&
    estimatedCurrentMa > 50f &&
    estimatedCurrentMa < 15000f) {

    powerMilliWatt = vForPower * estimatedCurrentMa;
}

// ====================================================
// VOLTAGE RECOVERY (FINAL — CORRECTED)
// ====================================================

// 🔴 FIND BEST LOAD (LOWEST)
float vLoadRef = Float.NaN;

if (voltageUnderLoad != null &&
    voltageUnderLoad.length > 0 &&
    !Float.isNaN(voltageUnderLoad[0])) {

    vLoadRef = voltageUnderLoad[0];
}

if (vLoad1 != null &&
    vLoad1.length > 0 &&
    !Float.isNaN(vLoad1[0])) {

    if (Float.isNaN(vLoadRef) || vLoad1[0] < vLoadRef) {
        vLoadRef = vLoad1[0];
    }
}

if (vLoad2 != null &&
    vLoad2.length > 0 &&
    !Float.isNaN(vLoad2[0])) {

    if (Float.isNaN(vLoadRef) || vLoad2[0] < vLoadRef) {
        vLoadRef = vLoad2[0];
    }
}

// ----------------------------------------------------
// 🔴 RECOVERY CALC
// ----------------------------------------------------
if (!Float.isNaN(vLoadRef) && !lab14Cancelled) {
	
	try {
    Thread.sleep(1200);
} catch(Throwable ignore){}

    float vrMv =
            idoctor.readBatteryVoltageMvStable(5, 20);

    if (!Float.isNaN(vrMv) && vrMv > 3000f && vrMv < 5000f) {

        float vr = vrMv / 1000f;

        float rec = vr - vLoadRef;

        // 🔴 VALID RANGE (reject spikes)
        if (rec > 0.010f && rec < 0.35f) {

            // 🔴 KEEP BEST (max recovery)
            if (Float.isNaN(voltageRecovery[0]) ||
                rec > voltageRecovery[0]) {

                voltageRecovery[0] = rec;
            }
        }
    }
}

// ====================================================
// ELECTRICAL ANALYSIS (FINAL CLEAN)
// ====================================================

// ----------------------------------------------------
// 🔴 FINAL SAG (ONLY SOURCE)
// ----------------------------------------------------

if (!Float.isNaN(voltageStart) &&
    !Float.isNaN(voltageUnderLoad[0])) {

    float tmp = voltageStart - voltageUnderLoad[0];

    if (tmp >= 0.02f && tmp < 1.0f) {   // 🔴 CRITICAL FIX
        finalSag = tmp;
    } else {
        finalSag = Float.NaN;
    }
}

// 🔴 SAG VALID FLAG (GLOBAL USE)
boolean hasSag =
        !Float.isNaN(finalSag) &&
        finalSag >= 0.02f &&
        finalSag < 1.0f;


// ----------------------------------------------------
// 🔴 CURRENT
// ----------------------------------------------------

currentNow = idoctor.readBatteryCurrentMaStable(5, 20);

// A discharge rate is not an instantaneous current sample.
// Missing hardware current stays unknown; it must never validate ESR.

// 🔴 CURRENT VALID FLAG
boolean hasCurrent =
        !Float.isNaN(currentNow) &&
        Math.abs(currentNow) >= 100f &&
        Math.abs(currentNow) < 8000f;
        
boolean strongElectricalSignal =
        hasSag &&
        (hasCurrent || validDrain);
      
// ----------------------------------------------------
// 🔴 MAIN CHECK (STRICT)
// ----------------------------------------------------

boolean lowDrain =
        validDrain &&
        drainMah >= 0 &&
        drainMah < 4;

if (hasSag && hasCurrent) {

    float sagCheck = finalSag;
    float currentAbs = Math.abs(currentNow);

    boolean lowSag =
        sagCheck < 0.04f;

    boolean lowCurrent =
            currentAbs < 100f;

// ----------------------------------------------------
// 🔴 MULTI-FLAG LOGIC (ANTI FALSE POSITIVES)
// ----------------------------------------------------
int limiterFlags = 0;

if (lowSag) limiterFlags++;
if (lowCurrent) limiterFlags++;
if (lowDrain) limiterFlags++;

// ----------------------------------------------------
// 🔴 FINAL TRIGGER (STRICT)
// ----------------------------------------------------
if (!lab14_systemLimited[0] &&
    limiterFlags >= 2 &&
    dtMs > 25000) {

    lab14_systemLimited[0] =
            lab14DetectLimiter(
                    voltageStart,
                    voltageUnderLoad[0],
                    startMah,
                    endMah,
                    tempStart,
                    tempEnd,
                    currentNow,
                    dtMs
            );
}


// ----------------------------------------------------
// 🔴 HARD CORRECTION AFTER LIMITER CONFIRM
// ----------------------------------------------------
if (lab14_systemLimited[0]) {
    validDrain = false;
    drainMah = 0;
    mahPerHour = 0;
    drainPercentPerHour = 0;
}
}

// ----------------------------------------------------
// 🔴 SAG FILTER (SINGLE SOURCE)
// ----------------------------------------------------
float sagFiltered = Float.NaN;

if (hasSag) {
    sagFiltered = finalSag;
}

if (!Float.isNaN(sagFiltered) &&
    sagFiltered < 0.02f) {   // 🔴 ALIGN WITH hasSag
    sagFiltered = Float.NaN;
}


// ----------------------------------------------------
// 🔴 ESR CALCULATION (CLEAN)
// ----------------------------------------------------
if (!lab14_systemLimited[0] &&
    hasSag &&
    hasCurrent &&
    !Float.isNaN(sagFiltered)) {

    float currentAmp =
            Math.abs(currentNow) / 1000f;

    if (currentAmp > 0.10f &&
        currentAmp < 6f) {

        float esr =
                sagFiltered / currentAmp;

        if (esr >= 0.015f &&
            esr <= 0.300f) {

            internalResistance[0] = esr;

            long irMilli =
                    (long)(esr * 1000f);

            idoctor.setInternalResistanceMilliOhm(
                    irMilli
            );

        } else {

            internalResistance[0] = Float.NaN;
            idoctor.setInternalResistanceMilliOhm(0);
        }
    }
}

// ----------------------------------------------------
// THERMAL IMPEDANCE
// ----------------------------------------------------

thermalImpedance[0] = Float.NaN;

if (!lab14_systemLimited[0] &&
    !Float.isNaN(tempStart) &&
    !Float.isNaN(lab14TempPeak) &&
    !Float.isNaN(currentNow)) {

    float currentAmp =
            Math.abs(currentNow) / 1000f;

    float tempRise =
            Math.max(0f, lab14TempPeak - tempStart);

    if (currentAmp > 0.3f &&
        currentAmp < 8f &&
        tempRise > 0.3f &&
        tempRise < 40f) {

        float ti =
                tempRise / currentAmp;

        if (ti > 0.1f &&
            ti < 35f) {

            thermalImpedance[0] = ti;
        }
    }
}

// ----------------------------------------------------
// ADVANCED DETECTORS
// ----------------------------------------------------

int swellingScore = 0;

// ----------------------------------------------------
// RECOVERY RATIO CHECK
// ----------------------------------------------------

float recoveryRatio = Float.NaN;

if (!Float.isNaN(voltageRecovery[0]) &&
    hasSag &&
    finalSag > 0f) {

    recoveryRatio = voltageRecovery[0] / finalSag;
}

// ----------------------------------------------------
// COLLAPSE DETECTOR (FINAL)
// ----------------------------------------------------

collapseRisk[0] = false;

if (!lab14_systemLimited[0] &&
    hasSag &&
    !Float.isNaN(voltageRecovery[0]) &&
    !Float.isNaN(internalResistance[0])) {

    boolean weakRecovery =
            voltageRecovery[0] < 0.035f; // λίγο πιο αυστηρό

    boolean heavySag =
            finalSag > 0.06f; // όχι 0.01

    boolean highESR =
            internalResistance[0] > 0.09f &&
            internalResistance[0] < 0.30f;

    int flags = 0;

    if (weakRecovery) flags++;
    if (heavySag) flags++;
    if (highESR) flags++;

    if (flags >= 2) {
        collapseRisk[0] = true;
    }
}

// ----------------------------------------------------
// ESR + TEMP RISE (FINAL)
// ----------------------------------------------------

if (!lab14_systemLimited[0] &&
    !Float.isNaN(internalResistance[0]) &&
    !Float.isNaN(tempStart) &&
    !Float.isNaN(lab14TempPeak)) {

    float tempRise =
            lab14TempPeak - tempStart;

    boolean highESR =
            internalResistance[0] > 0.12f &&
            internalResistance[0] < 0.30f;

    boolean highThermal =
            tempRise > 4f &&
            tempRise < 25f;

    if (highESR && highThermal) {
        swellingScore++;
    }
}

// ----------------------------------------------------
// THERMAL IMPEDANCE (FINAL)
// ----------------------------------------------------

if (!Float.isNaN(thermalImpedance[0]) &&
    !lab14_systemLimited[0]) {

    // moderate thermal impedance signal
    if (thermalImpedance[0] > 0.35f)
        swellingScore++;

    // strong thermal impedance signal
    if (thermalImpedance[0] > 0.60f)
        swellingScore++;
}


// ----------------------------------------------------
// HIGH TEMP UNDER LOAD CHECK (FINAL)
// ----------------------------------------------------

float tempNowEngine = getBatteryTempEngineSafe();

if (!Float.isNaN(tempNowEngine) &&
    tempNowEngine > 0f) {

    if (Float.isNaN(tempStart)) {
        tempStart = tempNowEngine;
    }

    if (Float.isNaN(lab14TempPeak) ||
        tempNowEngine > lab14TempPeak) {

        lab14TempPeak = tempNowEngine;
    }

    float tempRise =
            lab14TempPeak - tempStart;

    if (!Float.isNaN(tempStart) &&
        !Float.isNaN(lab14TempPeak) &&
        !Float.isNaN(internalResistance[0]) &&
        internalResistance[0] > 0f &&
        !lab14_systemLimited[0]) {

        // anomalous heat despite low ESR
        if (tempRise > 6f &&
            internalResistance[0] < 0.12f) {

            swellingScore++;
        }
    }
}

// ----------------------------------------------------
// BEHAVIOUR (FINAL - FIXED)
// ----------------------------------------------------

if (!Float.isNaN(internalResistance[0]) &&
    !Float.isNaN(voltageRecovery[0]) &&
    internalResistance[0] > 0.01f &&
    internalResistance[0] < 0.40f &&
    voltageRecovery[0] >= 0.002f &&
    voltageRecovery[0] < 0.5f &&
    validDrain &&
    !lab14_systemLimited[0]) {

    boolean highR =
            internalResistance[0] > 0.22f;

    boolean weakRec =
            voltageRecovery[0] < 0.06f;

    if (highR && weakRec) {
        lab14BatteryBehaviourWarning = true;
    }
}

// ----------------------------------------------------
// COLLAPSE VIA RECOVERY RATIO (FINAL)
// ----------------------------------------------------

if (!Float.isNaN(voltageRecovery[0]) &&
    voltageRecovery[0] >= 0.002f &&   // 🔴 reject noise
    hasSag &&
    finalSag >= 0.02f &&              // 🔴 strong sag only
    finalSag < 1.0f &&                // 🔴 sanity upper bound
    validDrain &&
    !lab14_systemLimited[0]) {

    recoveryRatio =
            voltageRecovery[0] / finalSag;

    if (!Float.isNaN(recoveryRatio) &&
        recoveryRatio >= 0f &&
        recoveryRatio < 0.4f) {

        collapseRisk[0] = true;
    }
}

// ----------------------------------------------------
// SAVE RUN / CONFIDENCE (FINAL)
// ----------------------------------------------------

if (validDrain &&
    !lab14_systemLimited[0] &&
    !Double.isNaN(mahPerHour) &&
    mahPerHour > 0f &&
    mahPerHour < 10000f) {   // 🔴 sanity cap

    // Recorded after run completion by BatteryEvidenceStore.recordLab14().
    // No mid-run pseudo-persistence or phantom confidence statistics.
}

// ----------------------------------------------------
// CONFIDENCE
// ----------------------------------------------------

Lab14Engine.ConfidenceResult newConf =
        engine.computeConfidence();

if (newConf != null) {
    lab14Conf = newConf;
}

// ----------------------------------------------------
// VARIABILITY FLAGS
// ----------------------------------------------------

if (!validDrain) {
    variabilityDetected[0] = true;
}

if (lab14Conf != null &&
    lab14Conf.percent >= 0f &&
    lab14Conf.percent <= 100f &&
    lab14Conf.percent < 50f) {

    variabilityDetected[0] = true;

    logWarn(gr
            ? "Χαμηλή αξιοπιστία μέτρησης μπαταρίας"
            : "Low battery measurement confidence");
}

// ----------------------------------------------------
// AGING (FINAL - HARDENED)
// ----------------------------------------------------

Lab14Engine.AgingResult aging = null;

boolean agingInputOk =
        validDrain &&
        !lab14_systemLimited[0] &&
        lab14Conf != null &&
        !Double.isNaN(mahPerHour) &&
        mahPerHour > 0 &&
        !Float.isNaN(tempStart) &&
        !Float.isNaN(lab14TempPeak);

if (agingInputOk) {

    float tempRise =
            Math.max(0f, lab14TempPeak - tempStart);

    boolean tempOk =
            lab14TempPeak < 58f &&
            tempRise < 18f;

    boolean drainOk =
            mahPerHour >= 20 &&
            mahPerHour < 6000;

    boolean cycleOk =
            cycles <= 0 || cycles < 3000;

    boolean confOk =
            lab14Conf.percent >= 0f &&
            lab14Conf.percent <= 100f &&
            lab14Conf.percent >= 45f;

    if (tempOk &&
        drainOk &&
        cycleOk &&
        confOk) {

        aging =
                engine.computeAging(
                        mahPerHour,
                        lab14Conf,
                        cycles,
                        tempStart,
                        lab14TempPeak
                );
    }
}

// ----------------------------------------------------
// INDEX
// ----------------------------------------------------

if (aging != null &&
    aging.index >= 0) {

    lab14AgingIndex =
            Math.max(0, Math.min(100, aging.index));

}

// ----------------------------------------------------
// INTERPRETATION
// ----------------------------------------------------

if (aging != null &&
    lab14AgingIndex >= 0 &&
    lab14Conf != null &&
    lab14Conf.percent >= 45 &&
    lab14Conf.percent <= 100 &&
    validDrain &&
    !lab14_systemLimited[0]) {

    if (lab14AgingIndex < 20)
        lab14AgingInterp = "Excellent";
    else if (lab14AgingIndex < 40)
        lab14AgingInterp = "Good";
    else if (lab14AgingIndex < 60)
        lab14AgingInterp = "Moderate";
    else if (lab14AgingIndex < 80)
        lab14AgingInterp = "High";
    else
        lab14AgingInterp = "Severe";

} else if (lab14_systemLimited[0]) {

    lab14AgingIndex = -1;
    lab14AgingInterp = "System limited";

} else if (!validDrain) {

    lab14AgingIndex = -1;
    lab14AgingInterp = "Invalid drain";

} else if (lab14Conf != null &&
           (lab14Conf.percent < 45 ||
            lab14Conf.percent > 100)) {

    lab14AgingIndex = -1;
    lab14AgingInterp = "Low confidence";

} else {

    lab14AgingIndex = -1;
    lab14AgingInterp = "Insufficient data";
}

// ----------------------------------------------------
// BATTERY HEALTH: FAIL CLOSED UNTIL CAPACITY IS INDEPENDENTLY VERIFIED
// ----------------------------------------------------
// Electrical response and cycle count are observations, not calibrated SoH.
// Physical swelling cannot be determined by Android software.
boolean smartSwelling = false;
lab14HealthPercent = Float.NaN;
lab14HealthLabel = "Not measured";
// All available measured V/I/Q/T/SoC evidence remains visible in the test log.

// ----------------------------------------------------
// ------------------------------------------------
// DATA QUALITY WARNING (TRIMMED)
// ------------------------------------------------

boolean lowConfidence =
        (lab14Conf == null ||
         lab14Conf.percent < 50f ||
         lab14Conf.percent > 100f);

boolean limited =
        lab14_systemLimited[0];

boolean badDrain =
        !validDrain;

if (badDrain ||
    limited ||
    lowConfidence) {

    logWarn(
        gr
        ? "Ανεπαρκή ή χαμηλής ποιότητας δεδομένα για πλήρη ανάλυση"
        : "Insufficient or low-quality data for full analysis"
    );

    variabilityDetected[0] = true;
}

// ----------------------------------------------------
// FINAL SCORE (TRIMMED)
// ----------------------------------------------------

int finalScore = 100;


// ----------------------------
// SAG (PRIMARY)
// ----------------------------
if (hasSag && !lab14_systemLimited[0]) {

    if (finalSag >= 0.25f)
        finalScore -= 25;
    else if (finalSag >= 0.18f)
        finalScore -= 18;
    else if (finalSag >= 0.10f)
        finalScore -= 10;
    else if (finalSag >= 0.06f)
        finalScore -= 5;
}


// ----------------------------
// DRAIN
// ----------------------------
if (validDrain &&
    !Double.isNaN(drainPercentPerHour)) {

    if (drainPercentPerHour >= 50)
        finalScore -= 15;
    else if (drainPercentPerHour >= 35)
        finalScore -= 8;
}


// ----------------------------
// TEMP RISE ONLY
// ----------------------------
if (!Float.isNaN(tempStart) &&
    !Float.isNaN(lab14TempPeak)) {

    float rise =
        Math.max(0f, lab14TempPeak-tempStart);

    if (rise >= 12f)
        finalScore -= 12;
    else if (rise >= 8f)
        finalScore -= 6;
}


// ----------------------------
// ESR
// ----------------------------
if (!Float.isNaN(internalResistance[0])) {

    if (internalResistance[0] >= 0.25f)
        finalScore -= 12;
    else if (internalResistance[0] >= 0.18f)
        finalScore -= 6;
}


// ----------------------------
// COLLAPSE FLAG
// ----------------------------
if (collapseRisk[0]) {
    finalScore -= 8;
}


// ----------------------------
// SYSTEM LIMITED -> confidence hit
// ----------------------------
if (lab14_systemLimited[0]) {
    finalScore -= 10;
}


// clamp
finalScore =
    Math.max(
       0,
       Math.min(100, finalScore)
    );

// =====================================================
// BATTERY TRUTH (TRIMMED FINAL)
// =====================================================

String batteryTruth;


// hard fail
if (!validDrain || lab14_systemLimited[0]) {

    batteryTruth = "Unknown";

} else {

    if (!hasSag || finalSag < 0.02f) {

        batteryTruth = "Low confidence";

    } else {

        if (finalSag <= 0.05f)
            batteryTruth = "Excellent";
        else if (finalSag <= 0.10f)
            batteryTruth = "Good";
        else if (finalSag <= 0.18f)
            batteryTruth = "Normal";
        else if (finalSag <= 0.30f)
            batteryTruth = "Weak";
        else
            batteryTruth = "Critical";
    }


    if (!"Unknown".equals(batteryTruth)) {

        int downgradeSteps = 0;


        // ESR
        if (!Float.isNaN(internalResistance[0])) {

            if (internalResistance[0] > 0.25f)
                downgradeSteps += 2;
            else if (internalResistance[0] > 0.18f)
                downgradeSteps += 1;
        }


        // Recovery
        if (!Float.isNaN(voltageRecovery[0])) {

            if (voltageRecovery[0] < 0.02f)
                downgradeSteps += 2;
            else if (voltageRecovery[0] < 0.04f)
                downgradeSteps += 1;
        }


        // Collapse risk
        if (collapseRisk[0]) {
            downgradeSteps += 2;
        }


        // controlled downgrade
        for (int i = 0; i < downgradeSteps; i++) {
            batteryTruth =
                downgradeBatteryLevel(
                    batteryTruth
                );
        }
    }
}

// =====================================================
// 🔴 FINAL ASSIGN (SAFE)
// =====================================================

res.label =
    (batteryTruth != null)
        ? batteryTruth
        : "Unknown";

lab14LastLabel = res.label;


// ----------------------------
// CAPACITY SANITY
// ----------------------------
boolean unrealisticCapacity = false;

if (baselineFullMah > 100 &&
    drainMah > 0 &&
    validDrain &&
    !lab14_systemLimited[0]) {

    if (drainMah > baselineFullMah * 0.5f) {
        unrealisticCapacity = true;
    }
}

final boolean unrealCapFinal = unrealisticCapacity;


// ----------------------------
// POPULATE RESULT
// ----------------------------
res.validDrain = validDrain;
res.systemLimited = lab14_systemLimited[0];

res.drainPercentPerHour = drainPercentPerHour;
res.mahPerHour = mahPerHour;

res.drainMah = drainMah;
res.durationMs = dtMs;

res.tempStart = tempStart;
res.tempEnd   = tempEnd;

res.powerMw = powerMilliWatt;

res.batteryBehaviourWarning =
        lab14BatteryBehaviourWarning;

final long baselineFullFinal = baselineFullMah;
final long drainMahFinal = drainMah;


// ----------------------------
// UI WARNINGS
// ----------------------------
runOnUiThread(() -> {

    if (validDrain &&
        !lab14_systemLimited[0] &&
        drainMahFinal > 600) {

        logLabelWarnValue(
            gr ? "Ανωμαλία μέτρησης κατανάλωσης"
               : "Drain measurement anomaly",
            gr
              ? "Μη ρεαλιστική τιμή κατανάλωσης."
              : "Unrealistic battery drain detected."
        );
    }


    if (validDrain &&
        !lab14_systemLimited[0] &&
        baselineFullFinal > 100 &&
        drainMahFinal > 0) {

        if (unrealCapFinal) {

            logLabelWarnValue(
                gr
                ? "Έλεγχος δηλωμένης χωρητικότητας"
                : "Declared capacity check",

                gr
                ? "Η δηλωμένη χωρητικότητα πιθανόν δεν είναι ρεαλιστική."
                : "Declared battery capacity may be unrealistic."
            );

        } else {

            logLabelOkValue(
                gr
                ? "Έλεγχος δηλωμένης χωρητικότητας"
                : "Declared capacity check",

                gr
                ? "Η δηλωμένη χωρητικότητα φαίνεται ρεαλιστική."
                : "Declared battery capacity appears realistic."
            );
        }
    }

});

// ------------------------------------------------
// FAST STRESS (READ-ONLY INFO)
// ------------------------------------------------

if (hasSag &&
    validDrain &&
    !lab14_systemLimited[0]) {

    String sagText =
        String.format(
            Locale.US,
            "%.3f V",
            finalSag
        );

    logLabelValue(
        gr ? "Γρήγορη δοκιμή καταπόνησης"
           : "Fast stress test",
        "Sag=" + sagText
    );


    if (finalSag > 0.18f) {

        logLabelWarnValue(
            gr ? "Διάγνωση":"Diagnosis",
            gr
              ? "Έντονη παρατηρούμενη πτώση τάσης — συνιστάται επανάληψη και τεχνικός έλεγχος"
              : "Large observed voltage drop — repeat testing and technician inspection"
        );

    } else if (finalSag > 0.10f) {

        logLabelWarnValue(
            gr ? "Διάγνωση":"Diagnosis",
            gr
              ? "Αυξημένη παρατηρούμενη πτώση τάσης — η πιθανή γήρανση δεν επιβεβαιώνεται"
              : "Elevated observed voltage sag — battery aging is not confirmed"
        );

    } else {

        logLabelOkValue(
            gr ? "Διάγνωση":"Diagnosis",
            gr
              ? "Δεν εντοπίστηκε ανωμαλία"
              : "No abnormal sag detected"
        );
    }

} else {

    logLabelWarnValue(
        gr ? "Γρήγορη δοκιμή καταπόνησης"
           : "Fast stress test",
        gr
          ? "Ανεπαρκή δεδομένα"
          : "Insufficient data"
    );
}
                           
// =====================================================
// CELL / STRUCTURAL CONDITION REQUIRES PHYSICAL OR OEM EVIDENCE
// =====================================================
logLabelWarnValue(gr ? "Δομική ακεραιότητα μπαταρίας"
                     : "Battery structural integrity",
        Lab14CustomerGuidance.unverifiedCellCondition(gr));
logLabelWarnValue(gr ? "Ισορροπία κυψελών μπαταρίας"
                     : "Battery cell balance",
        Lab14CustomerGuidance.unverifiedCellCondition(gr));

// =====================================================
// =====================================================
// BATTERY BEHAVIOUR (FINAL SAFE)
// =====================================================

if (!hasSag || !validDrain || lab14_systemLimited[0]) {

    logLabelWarnValue(
            gr ? "Συμπεριφορά μπαταρίας"
               : "Battery behaviour",
            gr
                    ? "Ανεπαρκή δεδομένα"
                    : "Insufficient data"
    );

} else if (res.batteryBehaviourWarning) {

    logLabelWarnValue(
            gr ? "Συμπεριφορά μπαταρίας"
               : "Battery behaviour",
            gr
                    ? "Ασυνήθιστη ηλεκτρική συμπεριφορά κατά τη φόρτιση / εκφόρτιση"
                    : "Abnormal electrical behaviour detected during load / recovery"
    );

} else {

    logLabelOkValue(
            gr ? "Συμπεριφορά μπαταρίας"
               : "Battery behaviour",
            gr
                    ? "Η συμπεριφορά φαίνεται φυσιολογική"
                    : "Electrical behaviour appears normal"
    );
}

// ------------------------------------------------
// PARTIAL / FULL MODE DECISION
// ------------------------------------------------

lowConfidence =
        (lab14Conf == null ||
         lab14Conf.percent < 50f ||
         lab14Conf.percent > 100f);

boolean partial =
        !validDrain ||
        dtMs < 20000 ||
        lowConfidence ||
        (drainMahFinal < 3 && !lab14_systemLimited[0]);

if (partial) {

    lab14LogPartialMode(
            gr,
            lab14_systemLimited,
            lab14Conf
    );

    if (lab14_systemLimited[0]) {
        logWarn(gr
                ? "Η μέτρηση έγινε με περιορισμό από το σύστημα. Το αποτέλεσμα είναι ενδεικτικό."
                : "System limiter detected. Result is indicative.");
    }
}

// ------------------------------------------------
// HEALTH
// ------------------------------------------------

// 🔴 FULL STRESS RESULT SUMMARY
float lab14EnergyEfficiency = Float.NaN;

if (validDrain &&
    mahPerHour > 0 &&
    !Float.isNaN(finalSag) &&
    finalSag >= 0.015f &&
    finalSag < 1.0f) {

    lab14EnergyEfficiency =
            (float) (mahPerHour / finalSag);
}

lab14LogVoltageBehaviorSummary(
        gr,
        voltageStart
);

lab14LogStressResult(
        res,
        sagAvg,
        voltageStart,
        voltageRecovery,
        voltageRecoverySpeed,
        voltageStability[0],
        internalResistance,
        Float.NaN,
        thermalImpedance[0],
        lab14EnergyEfficiency,
        startMah,
        endMah,
        lab14_systemLimited,
        collapseRisk,
        smartSwelling,
        calibrationDrift
);

// FINAL SCORE
lab14LogFinalScore(
        gr,
        res.label,
        res.powerMw,
        collapseRisk,
        smartSwelling,
        calibrationDrift,
        lab14_systemLimited,
        hasSag
);

// ------------------------------------------------
// SAVE
// ------------------------------------------------

SharedPreferences p = getSharedPreferences(LAB14_PREFS, MODE_PRIVATE);

lab14LogSave(
        gr,
        p,
        variabilityDetected,
        collapseRisk,
        smartSwelling,
        calibrationDrift,
        false,
        partial,
        lab14_systemLimited
);

// Never persist a heuristic SoH or preserve a prior fabricated health score.
// Clear legacy battery-score keys in BOTH historical preference stores.
p.edit().remove("lab14_health_percent").remove("lab14_health_score")
        .remove("lab14_aging_index").apply();
getSharedPreferences("GEL_DIAG", MODE_PRIVATE).edit()
        .remove("lab14_health_percent").remove("lab14_health_score")
        .remove("lab14_aging_index").apply();

// One atomic, versioned observation record for Labs 17, 30 and 31.
boolean lab14EvidenceStored = BatteryEvidenceStore.recordLab14(
        ManualTestsActivity.this, !partial, validDrain, lab14_systemLimited[0]);
if (!lab14EvidenceStored) {
    logWarn(gr ? "Τα διαγνωστικά τεκμήρια δεν αποθηκεύτηκαν."
            : "Diagnostic evidence could not be stored.");
}

// ----------------------------------------------------
// // RUN COUNT
boolean countableRun =
        validDrain &&
        !lab14_systemLimited[0];

if (countableRun)
    incLab14RunCount(true);


// RELIABILITY
lab14LogReliabilitySummary(
    gr,
    lab14_systemLimited,
    lab14Conf
);

// ------------------------------------------------
// STOP (NORMAL EXIT)
// ------------------------------------------------

lab14StopAllStress();

try {
    counterText = null;
    lab14CleanupUI();
} catch (Throwable ignore) {}

restoreBrightnessAndKeepOn();

// 🔴 SUCCESS EXIT (not cancelled)
lab14Running = false;
lab14PopupShown = false;
lab14AdvisoryShown = false;

} catch (Throwable t) {

    logError("LAB14 CRASH: " + t.getMessage());

    lab14StopAllStress();

    try {
        counterText = null;
        lab14CleanupUI();
    } catch (Throwable ignore) {}

    restoreBrightnessAndKeepOn();

    // 🔴 ONLY crash/abort marks cancelled
    lab14Cancelled = true;
    lab14Running = false;
    lab14PopupShown = false;
    lab14AdvisoryShown = false;

}

}

// ============================================================
// STABLE VOLTAGE READ (FINAL - filtered + stable)
// ============================================================
private float readStableBatteryVoltage() {

    iDoctorEngine idoctor =
            iDoctorEngine.get(ManualTestsActivity.this);

    float[] values = new float[3];
    int ok = 0;

    for (int i = 0; i < 3; i++) {

        float vMv =
                idoctor.readBatteryVoltageMvStable(2, 10);

        if (!Float.isNaN(vMv) && vMv > 3000f && vMv < 5000f) {

            values[ok] = vMv / 1000f;
            ok++;
        }

        if (i < 2) {
            SystemClock.sleep(180);
        }
    }

    if (ok == 0) return Float.NaN;

    // ------------------------------------------------
    // 🔴 SINGLE VALUE
    // ------------------------------------------------
    if (ok == 1) {
        return values[0];
    }

    // ------------------------------------------------
    // 🔴 MEDIAN FILTER (ANTI SPIKE)
    // ------------------------------------------------
    if (ok >= 2) {

        // simple sort (max 3 στοιχεία → cheap)
        for (int i = 0; i < ok - 1; i++) {
            for (int j = i + 1; j < ok; j++) {
                if (values[j] < values[i]) {
                    float tmp = values[i];
                    values[i] = values[j];
                    values[j] = tmp;
                }
            }
        }

        float median = values[ok / 2];

        // ------------------------------------------------
        // 🔴 CONSISTENCY CHECK
        // ------------------------------------------------
        float min = values[0];
        float max = values[ok - 1];

        float spread = max - min;

        // reject unstable readings
        if (spread > 0.15f) {
            return Float.NaN;
        }

        return median;
    }

    return Float.NaN;
}

// ============================================================
// FAST THREAD (FINAL - SAFE STATE + CLEAN STOP)
// ============================================================
private void startLab14FastThread() {

    new Thread(() -> {

        boolean movedToMain = false;

        try {

            final iDoctorEngine idoctor =
                    iDoctorEngine.get(ManualTestsActivity.this);

            // 🔴 ATOMIC STATE SET
            synchronized (this) {
                lab14FastDone = false;
                lab14FastPhase = true;
                lab14MainPhase = false;
            }

            lab14FastStartTime =
                    SystemClock.elapsedRealtime();

            // ------------------------------------------------
            // 🔴 EARLY EXIT
            // ------------------------------------------------
            if (lab14Cancelled || !lab14Running) {

                setFastPhaseStopped();
                stopAllStressSafe();
                return;
            }

            final float[] powerMilliWattRef = { Float.NaN };
            final float[] estimatedCurrentMaRef = { Float.NaN };

// =====================================================
// 🔴 REAL 45s FAST PHASE LOOP (STABLE TIMING)
// =====================================================

long fastStart = SystemClock.elapsedRealtime();
long nextTick = fastStart;

while (lab14Running && !lab14Cancelled) {

    long now = SystemClock.elapsedRealtime();
    long elapsed = (now - fastStart) / 1000;

    if (DEBUG_MODE) {
        appendLog("LOAD",
                "threads=" + lab14CpuThreadsCurrent +
                " | running=" + lab14Running);
    }

    // 🔴 FAST UI BAR
    runOnUiThread(() -> {

        int safeElapsed =
                Math.max(0, Math.min(45, (int) elapsed));

        if (counterText != null) {

            counterText.setText(
                    gr
                            ? "Προθέρμανση " + safeElapsed + " / 45"
                            : "Warm-up " + safeElapsed + " / 45"
            );
        }

        updateProgressBar(safeElapsed, 45);

    });

    // 🔴 SAMPLE
runFastVoltageSampling(
        idoctor,
        vStart,
        vLoad1,
        vRecover,
        vLoad2
);

// ----------------------------------------------------
// 🔴 LIVE CHARGE SAMPLING
// ----------------------------------------------------
iDoctorEngine.BatterySnapshot snapNow =
        iDoctorEngine.get(ManualTestsActivity.this)
                .readBatterySnapshotLab();

if (snapNow != null &&
    snapNow.chargeNowMah > 0 &&
    snapNow.chargeNowMah < 100000) {

    long nowMah = snapNow.chargeNowMah;

    lab14ChargeSamples.add(nowMah);

    if (nowMah < lab14MinCharge) {
        lab14MinCharge = nowMah;
    }

    if (nowMah > lab14MaxCharge) {
        lab14MaxCharge = nowMah;
    }
}

    // 🔴 EXIT
    if (elapsed >= 45) {
        break;
    }

    // 🔴 FIXED INTERVAL
    nextTick += 1000;

    long sleepTime =
            nextTick - SystemClock.elapsedRealtime();

    if (sleepTime > 0) {

        try {
            Thread.sleep(sleepTime);
        } catch (Throwable ignore) {}
    }
}

// =====================================================
// 🔴 VOLTAGE RECOVERY (FIXED TIMING)
// =====================================================
if (!Float.isNaN(vRecover[0]) &&
    !Float.isNaN(vLoad2[0]) &&
    vLoad2Time > 0) {

    float recMs =
            SystemClock.elapsedRealtime() - vLoad2Time;

    if (recMs > 50f && recMs < 5000f) { // 🔴 clamp (important)

        lab14RecoveryTimeMs = (long) recMs;

        float recDelta = vRecover[0] - vLoad2[0];

        if (recDelta > 0.002f && recDelta < 0.5f) {

            voltageRecovery[0] = recDelta;

            voltageRecoverySpeed[0] =
                    recDelta / Math.max(0.05f, recMs / 1000f);
        }
    }
}

// =====================================================
// 🔴 VOLTAGE STABILITY (FINAL - SAFE)
// =====================================================
if (!Float.isNaN(vStart[0]) &&
    !Float.isNaN(vLoad2[0]) &&
    !Float.isNaN(vRecover[0])) {

    float totalDrop = vStart[0] - vLoad2[0];

    // 🔴 HARD FILTER (ignore tiny sag)
    if (totalDrop >= 0.01f && totalDrop < 1.0f) {

        float recovered = vRecover[0] - vLoad2[0];

        if (recovered >= 0f && recovered < 0.5f) {

            float ratio = recovered / totalDrop;

            if (!Float.isNaN(ratio)) {

                voltageStability[0] =
                        Math.max(0f, Math.min(100f, ratio * 100f));
            }
        }

    } else {

        // 🔴 tiny sag = perfect stability
        if (totalDrop > 0f && totalDrop < 0.01f) {
            voltageStability[0] = 100f;
        } else {
            voltageStability[0] = Float.NaN;
        }
    }
}

// =====================================================
// 🔴 CANCEL / EARLY STOP (FINAL)
// =====================================================
if (lab14Cancelled || !lab14Running) {

    setFastPhaseStopped();      // 🔴 atomic state
    stopAllStressSafe();        // 🔴 unified stop

    runOnUiThread(() -> {
        try {
            if (!isFinishing() &&
                lab14Dialog != null &&
                lab14Dialog.isShowing()) {

                lab14Dialog.dismiss();
                lab14Dialog = null;
            }
        } catch (Throwable ignore) {}
    });

    return;
}

// =====================================================
// 🔴 CLEAN FAST LOAD BEFORE MAIN (FINAL)
// =====================================================

stopFastStressSafe();

// μικρό adaptive settle (όχι τυφλό sleep)
long settleStart = SystemClock.elapsedRealtime();
while (SystemClock.elapsedRealtime() - settleStart < 250) {

    if (lab14Cancelled || !lab14Running) break;

    try { Thread.sleep(50); } catch (Throwable ignore) {}
}

// =====================================================
// 🔴 TRANSITION TO MAIN PHASE (ATOMIC)
// =====================================================
synchronized (this) {
    lab14FastDone = true;
    lab14FastPhase = false;
    lab14MainPhase = true;
}
movedToMain = true;

runOnUiThread(() -> {
    try {

        if (lab14Cancelled) return;

        resetLab14Bar();

        if (counterText != null) {
            counterText.setText(
                    gr
                            ? "Έναρξη κύριας φάσης..."
                            : "Starting main phase..."
            );
        }

        startLab14MainStress();

    } catch (Throwable t) {

        logError("LAB14 MAIN START FAIL: " + t.getMessage());

        stopAllStressSafe();

        synchronized (this) {
            lab14FastDone = true;
            lab14FastPhase = false;
            lab14MainPhase = false;
            lab14Running = false;
        }

        safeDismissDialog();
    }
});

} catch (Throwable t) {

    logError("LAB14 FAST CRASH: " + t.getMessage());

    stopAllStressSafe();

    synchronized (this) {
        lab14FastDone = true;
        lab14FastPhase = false;
        lab14MainPhase = false;
        lab14Running = false;
    }

    safeDismissDialog();

} finally {

    synchronized (this) {
        lab14FastDone = true;
        lab14FastPhase = false;

        // 🔴 ΜΗΝ σκοτώσεις main αν ξεκίνησε
        if (!movedToMain) {
            lab14MainPhase = false;
        }
    }
}

    }).start();
}

// ============================================================
// MAIN STRESS (FINAL)
// ============================================================
private void startLab14MainStress() {

    if (!lab14Running || lab14Cancelled) return;

    // 🔴 ATOMIC STATE
    synchronized (this) {
        lab14FastPhase = false;
        lab14MainPhase = true;
    }

    // 🔴 TIMER
    t0 = SystemClock.elapsedRealtime();
    lab14EndTime = t0 + (durationSec * 1000L);

    // 🔴 UI FEEDBACK
    if (counterText != null) {
        counterText.setText(
                gr ? "Κύρια καταπόνηση..." : "Main stress phase..."
        );
    }

    // ---------------------------------------------------------
    // 🔴 CLEAN FAST LOAD ONLY
    // ---------------------------------------------------------
    stopFastStressSafe();

    long settleStart = SystemClock.elapsedRealtime();

    while (SystemClock.elapsedRealtime() - settleStart < 200) {

        if (!lab14Running || lab14Cancelled) return;

        try {
            Thread.sleep(50);
        } catch (Throwable ignore) {}
    }

// =========================================================
// 🔥 CPU STRESS
// =========================================================
new Thread(() -> {

    try {

        if (!lab14Running || lab14Cancelled) return;

        final int cores = Runtime.getRuntime().availableProcessors();

        if (!isLab14BMode) {

            // 🔴 LAB14 ONLY — TRUE FULL LOAD (ALL CORES)
            lab14CpuThreadsCurrent = cores;

            startCpuBurn_C_Mode(); // 🔥 FULL POWER

        } else {

            // =====================================================
            // 🧪 LAB14B MODE — LOCKED, DO NOT CHANGE
            // =====================================================
            if (inHardPhase) {

                startCpuBurnLimitedThreads(Math.max(cores - 1, 4));

            } else {

                int softThreads = Math.max(2, cores / 2);
startCpuBurnLimitedThreads(softThreads);
            }
        }

    } catch (Throwable ignore) {}

}).start();

    // =========================================================
    // 🔴 UI ONLY
    // =========================================================
    runOnUiThread(() -> {

        try {

            if (!lab14Running || lab14Cancelled) return;

            applyMaxBrightnessAndKeepOn();

            try {

                if (lab14StressVideo != null) {

                    lab14StressVideo.setVideoURI(
                            Uri.parse(
                                    "android.resource://"
                                            + getPackageName()
                                            + "/"
                                            + R.raw.battery_stress_loop
                            )
                    );

                    lab14StressVideo.setOnPreparedListener(mp -> {
                        mp.setLooping(true);
                        mp.setVolume(0f, 0f);

                        if (lab14Running && !lab14Cancelled) {
                            lab14StressVideo.start();
                        }
                    });
                }

            } catch (Throwable ignore) {}

        } catch (Throwable ignore) {}
    });

    // =========================================================
    // 🔥 LAB14 EXTRA LOAD ONLY
    // =========================================================
    if (!isLab14BMode) {

        new Thread(() -> {

            try {

                if (!lab14Running || lab14Cancelled) return;

                startGpuStressLevel(5);
                startMemoryStress();

                try { startMemoryBandwidthStress(); } catch (Throwable ignore) {}
                try { startVibrationStress(); } catch (Throwable ignore) {}

            } catch (Throwable ignore) {}

        }).start();
    }
}

// =========================================================
// 🔴 PROGRESS LOOP (FINAL SAFE)
// =========================================================
private void startLab14ProgressLoop() {

    if (counterText == null) return;

    ui.post(new Runnable() {

        @Override
        public void run() {
        	
        if (lab14FastPhase) {
    ui.postDelayed(this, 1000);
    return;
}

            if (counterText == null || lab14Cancelled || !lab14Running) {
                ui.removeCallbacks(this);
                return;
            }

            long now = SystemClock.elapsedRealtime();
            int elapsed = (int) ((now - t0) / 1000);

            // 🔴 FINAL END
            if (elapsed >= durationSec) {

                ui.removeCallbacks(this);
                lab14Running = false;

                try { lab14StopAllStress(); } catch (Throwable ignore) {}

                runOnUiThread(() -> {
                    try {
                        if (lab14Dialog != null && lab14Dialog.isShowing()) {
                            lab14Dialog.dismiss();
                            lab14Dialog = null;
                        }
                    } catch (Throwable ignore) {}
                });

                final Lab14Engine engine =
                        new Lab14Engine(ManualTestsActivity.this);

                lab14PostLoadAnalysis(
                        engine,
                        gr,
                        startMah,
                        baselineFullMah,
                        t0,
                        voltageStart,
                        batteryPercent,
                        cycles,
                        tempStart
                );

                return;
            }

            // 🔥 THERMAL TRACK
            try {
                iDoctorEngine idoctor =
                        iDoctorEngine.get(ManualTestsActivity.this);

                Float tObj = idoctor.getBatteryTempUnified();

                float tNow = (tObj != null) ? tObj : Float.NaN;

                if (!Float.isNaN(tNow) &&
                        tNow > 10f &&
                        tNow < 80f) {

                    if (Float.isNaN(lab14TempPeak) || tNow > lab14TempPeak) {
                        lab14TempPeak = tNow;
                    }
                }

            } catch (Throwable ignore) {}
            
// ----------------------------------------------------
// 🔴 LIVE CHARGE SAMPLING — MAIN PHASE
// ----------------------------------------------------
try {

    iDoctorEngine.BatterySnapshot snapNow =
            iDoctorEngine.get(ManualTestsActivity.this)
                    .readBatterySnapshotLab();

    if (snapNow != null &&
        snapNow.chargeNowMah > 0 &&
        snapNow.chargeNowMah < 100000) {

        long nowMah = snapNow.chargeNowMah;

        lab14ChargeSamples.add(nowMah);

        if (nowMah < lab14MinCharge) {
            lab14MinCharge = nowMah;
        }

        if (nowMah > lab14MaxCharge) {
            lab14MaxCharge = nowMah;
        }
    }

} catch (Throwable ignore) {}

            // 🔴 LIVE
            updateLab14LiveStats();

            // 🔴 UI
            if (counterText != null) {

                int safeElapsed =
                        Math.max(0, Math.min(durationSec, elapsed));

                counterText.setText(
                        gr
                                ? "Καταπόνηση " + safeElapsed + " / " + durationSec
                                : "Stress " + safeElapsed + " / " + durationSec
                );
            }

            updateProgressBar(
                    Math.max(0, Math.min(durationSec, elapsed)),
                    durationSec
            );

            ui.postDelayed(this, 1000);
        }
    });
}

private void resetLab14Bar() {

    if (lab14MainBar == null) return;

    final int count = lab14MainBar.getChildCount();
    if (count == 0) return; // 🔴 SAFETY

    for (int i = 0; i < count; i++) {

        View v = lab14MainBar.getChildAt(i);

        if (v != null) { // 🔴 NPE GUARD
            v.setBackgroundColor(0xFF333333);
        }
    }
}

// ============================================================
// RESET BATTERY DIAGNOSTIC STATE (FULL CLEAN)
// ============================================================
private void resetBatteryDiagnostics() {

    // -------------------------
    // 🔴 VOLTAGE STAGES
    // -------------------------
    vStart[0] = Float.NaN;
    vLoad1[0] = Float.NaN;
    vRecover[0] = Float.NaN;
    vLoad2[0] = Float.NaN;

    // -------------------------
    // 🔴 SAG
    // -------------------------
    sag1 = Float.NaN;
    sag2 = Float.NaN;
    sagAvg = Float.NaN;
    finalSag = Float.NaN;

    // -------------------------
    // 🔴 VOLTAGE METRICS
    // -------------------------
    voltageUnderLoad[0] = Float.NaN;
    voltageRecovery[0] = Float.NaN;
    voltageRecoverySpeed[0] = Float.NaN;
    voltageStability[0] = Float.NaN;

    // -------------------------
    // 🔴 ELECTRICAL CORE
    // -------------------------
    internalResistance[0] = Float.NaN;
    estimatedESR = Float.NaN;
    thermalImpedance[0] = Float.NaN;

    // -------------------------
    // 🔴 HEALTH
    // -------------------------
    batterySOH[0] = Float.NaN;
    lab14HealthPercent = Float.NaN;
    lab14HealthLabel = null;

    // -------------------------
    // 🔴 FLAGS
    // -------------------------
    collapseRisk[0] = false;
    batteryFailureRisk[0] = false;
    lab14BatteryBehaviourWarning = false;

    // -------------------------
    // 🔴 DRAIN / FLOW
    // -------------------------
    validDrain = false;
    drainMah = 0;
    mahPerHour = 0;
    drainPercentPerHour = 0;

    // -------------------------
    // 🔴 SNAPSHOT TRACKING
    // -------------------------
    lab14ChargeSamples.clear();
    lab14MinCharge = Long.MAX_VALUE;
    lab14MaxCharge = 0L;

    lastSnapshotTs = 0;
}

// ============================================================
// LAB14 — BATTERY % (LOCKED SOURCE - STABLE)
// ============================================================
private float lab14BatteryPercent() {

    try {

        iDoctorEngine idoctor =
                iDoctorEngine.get(ManualTestsActivity.this);

        iDoctorEngine.BatterySnapshot snap =
                idoctor.readBatterySnapshotLab();

        if (snap != null &&
            snap.level >= 0 &&
            snap.scale > 0 &&
            snap.scale <= 1000) { // 🔴 HARD FILTER

            float pct =
                    (snap.level * 100f) /
                    (float) snap.scale;

            // 🔴 CLAMP (safety)
            if (pct >= 0f && pct <= 100f) {
                return pct;
            }
        }

    } catch (Throwable ignore) {}

    return Float.NaN;
}

private void startLab14SharedUI(long durationSec, boolean gr) {

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    ManualTestsActivity.this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(20), dp(24), dp(18));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(4), 0xFFFFD700);
    root.setBackground(bg);

    // -------------------------
    // 🔴 TITLE
    // -------------------------
    TextView title = new TextView(this);
    title.setText(
            gr
                    ? "LAB 14 — Δοκιμή Καταπόνησης Υγείας Μπαταρίας"
                    : "LAB 14 — Battery Health Stress Test"
    );
    title.setTextColor(Color.WHITE);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

    // -------------------------
    // 🔴 STATUS
    // -------------------------
    final TextView statusText = new TextView(this);
    statusText.setText(
            gr
                    ? "Η δοκιμή βρίσκεται σε εξέλιξη…"
                    : "Stress test running…"
    );
    statusText.setTextColor(0xFF39FF14);
    statusText.setTextSize(15f);
    statusText.setGravity(Gravity.CENTER);
    root.addView(statusText);

    // -------------------------
    // 🔴 DOTS
    // -------------------------
    lab14DotsView = new TextView(this);
    lab14DotsView.setText("•");
    lab14DotsView.setTextColor(0xFF39FF14);
    lab14DotsView.setTextSize(22f);
    lab14DotsView.setGravity(Gravity.CENTER);
    root.addView(lab14DotsView);

    // -------------------------
    // 🔴 COUNTER
    // -------------------------
    counterText = new TextView(this);
    counterText.setText(
            gr
                    ? "Πρόοδος Stress Test: 0 / " + durationSec + " δευτ."
                    : "Stress Test Progress: 0 / " + durationSec + " sec"
    );
    counterText.setTextColor(0xFF39FF14);
    counterText.setGravity(Gravity.CENTER);
    root.addView(counterText);

    // -------------------------
    // 🔴 LIVE STATS
    // -------------------------
    lab14LiveStats = new TextView(this);
    lab14LiveStats.setTextColor(0xFF39FF14);
    lab14LiveStats.setTextSize(12f);
    lab14LiveStats.setPadding(0, dp(10), 0, dp(6));
    lab14LiveStats.setText(gr ? "Αρχικοποίηση..." : "Initializing...");
    root.addView(lab14LiveStats);

    // -------------------------
    // 🔴 VIDEO
    // -------------------------
    lab14StressVideo = new VideoView(this);

    LinearLayout videoHolder = new LinearLayout(this);
    videoHolder.setOrientation(LinearLayout.VERTICAL);
    videoHolder.setGravity(Gravity.CENTER);

    LinearLayout.LayoutParams vLp =
            new LinearLayout.LayoutParams(dp(220), dp(120));
    vLp.gravity = Gravity.CENTER;

    lab14StressVideo.setLayoutParams(vLp);
    videoHolder.addView(lab14StressVideo);
    root.addView(videoHolder);

    // -------------------------
    // 🔴 PROGRESS BAR
    // -------------------------
    lab14MainBar = new LinearLayout(this);
    lab14MainBar.setOrientation(LinearLayout.HORIZONTAL);

    for (int i = 0; i < 12; i++) {

        View seg = new View(this);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0, dp(10), 1f);

        lp.setMargins(dp(2), 0, dp(2), 0);

        seg.setLayoutParams(lp);
        seg.setBackgroundColor(0xFF333333);

        lab14MainBar.addView(seg);
    }

    root.addView(lab14MainBar);

    // -------------------------
    // 🔴 EXIT BUTTON
    // -------------------------
    Button exitBtn =
            gelButton(
                    this,
                    gr ? "Έξοδος τεστ" : "Exit test",
                    0xFF8B0000
            );

    LinearLayout.LayoutParams btnLp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(52)
            );

    btnLp.setMargins(0, dp(14), 0, 0);
    exitBtn.setLayoutParams(btnLp);

    exitBtn.setOnClickListener(v -> {

        lab14Cancelled = true;
        lab14Running = false;
        lab14FastPhase = false;
        lab14MainPhase = false;

        try { lab14StopAllStress(); } catch (Throwable ignore) {}

        try { lab14CleanupUI(); } catch (Throwable ignore) {}
    });

    root.addView(exitBtn);

    // -------------------------
    // 🔴 DIALOG
    // -------------------------
    b.setView(root);
    lab14Dialog = b.create();

    if (lab14Dialog.getWindow() != null) {
        lab14Dialog.getWindow()
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    if (!isLab14BMode) {
        lab14Dialog.show();
        com.gel.cleaner.GelButtonLayout.fitDialog(lab14Dialog);
    }

    // =====================================================
    // 🔴 CRITICAL: INIT BEFORE THREADS
    // =====================================================
    if (!isLab14BMode) {

        // 🔴 RESET already done BEFORE stress start in lab14BatteryHealthStressTest_REAL()
        // Do NOT reset here; it can wipe live limiter / thermal state after load has started.

        // 🔴 STATE GUARANTEE
        lab14Running = true;
        lab14Cancelled = false;

        // 🔴 START FLOWS
        startLab14FastThread();
        startLab14ProgressLoop();
    }
}

// =====================================================
// LAB 14 — DYNAMIC CURRENT ESTIMATOR (STABLE)
// Προτεραιότητα:
// 1) BatteryManager CURRENT_NOW (median-of-3 + unit detect)
// 2) Fallback: average current από charge delta / time
// Επιστρέφει mA (absolute)
// =====================================================
private float estimateDynamicCurrentMilliAmp(
        long drainMah,
        long durationMs
) {
    float currentMa = Float.NaN;

    // -------------------------------------------------
    // 1) CURRENT_NOW (median-of-3)
    // -------------------------------------------------
    try {
        BatteryManager bm =
                (BatteryManager) getSystemService(BATTERY_SERVICE);

        if (bm != null) {

            float[] samples = new float[3];
            int ok = 0;

            for (int i = 0; i < 3; i++) {

                int raw = bm.getIntProperty(
                        BatteryManager.BATTERY_PROPERTY_CURRENT_NOW
                );

                if (raw != Integer.MIN_VALUE && raw != 0) {

                    float v = Math.abs((float) raw);

                    // 🔴 UNIT DETECT
                    // >100k → μA (microamp)
                    // αλλιώς → mA
                    if (v > 100000f) {
                        v = v / 1000f; // μA → mA
                    }

                    // 🔴 HARD FILTER
                    if (v >= 50f && v <= 8000f) {
                        samples[ok++] = v;
                    }
                }

                if (i < 2) {
                    SystemClock.sleep(120);
                }
            }

            // 🔴 MEDIAN (anti-spike)
            if (ok == 3) {
                java.util.Arrays.sort(samples);
                currentMa = samples[1];
            } else if (ok == 2) {
                currentMa = (samples[0] + samples[1]) / 2f;
            } else if (ok == 1) {
                currentMa = samples[0];
            }
        }

    } catch (Throwable ignore) {}

    // -------------------------------------------------
    // 2) FALLBACK (drain / time)
    // -------------------------------------------------
    if (Float.isNaN(currentMa)) {

        if (drainMah > 0 && durationMs > 0) {

            float est =
                    (float) (drainMah * 3600000.0 / durationMs);

            // 🔴 SAME FILTER (consistency!)
            if (est >= 50f && est <= 8000f) {
                currentMa = est;
            }
        }
    }

    return currentMa;
}

// =====================================================
// SMART SWELLING DETECTION (FIXED)
// =====================================================
private boolean detectSwellingSmart(
        float sag,
        float tempDelta,
        float estimatedCurrentMa,
        float rdynMilli, // 🔴 mΩ explicitly
        double drainPercentPerHour
) {

    int score = 0;

    // 1. High sag
    if (!Float.isNaN(sag) && sag > 0.12f)
        score++;

    // 2. Thermal rise
    if (!Float.isNaN(tempDelta) && tempDelta > 8f)
        score++;

    // 3. Resistance (mΩ)
    if (!Float.isNaN(rdynMilli) && rdynMilli > 120f)
        score++;

    // 4. Drain instability
    if (!Double.isNaN(drainPercentPerHour) &&
        drainPercentPerHour > 40.0) {
        score++;
    }

    // 5. High current stress
    if (!Float.isNaN(estimatedCurrentMa) &&
        estimatedCurrentMa > 2500f)
        score++;

    return score >= 3;
}

private void updateLab14LiveStats() {

// 🔴 HARD STOP FAILSAFE (CRITICAL)
long now = SystemClock.elapsedRealtime();
int elapsed = (int) ((now - t0) / 1000);

// 🔴 HARD STOP FAILSAFE (ONLY ONCE)
if (isLab14BMode && lab14Running && elapsed >= 300 && !lab14Cancelled) {

    lab14Cancelled = true;
    lab14Running = false;
    
    try { stopCpuBurn(); } catch (Throwable ignore) {}
    try { stopGpuStress(); } catch (Throwable ignore) {}
    try { stopMemoryStress(); } catch (Throwable ignore) {}
    try { stopMemoryBandwidthStress(); } catch (Throwable ignore) {}
    try { stopVibrationStress(); } catch (Throwable ignore) {}

    try {
        if (lab14StressVideo != null) {
            lab14StressVideo.stopPlayback();
        }
    } catch (Throwable ignore) {}

    return; // 🔴 CRITICAL → μην συνεχίσει loop
}

	final int cores = Runtime.getRuntime().availableProcessors();

    try {

    iDoctorEngine idoctor =
            iDoctorEngine.get(ManualTestsActivity.this);

float cpuTemp = Float.NaN;

try {
    Float tmp = readCpuTempSafe();
    if (tmp != null) {
        cpuTemp = tmp;
    }
} catch (Throwable ignore) {}

// ----------------------------------------------------
// 🔴 TIME FLAGS (SOFT ONLY)
// ----------------------------------------------------
boolean earlyPhase = elapsed < 25;

inHardPhase = false;

// ----------------------------------------------------
// 🔴 ENGINE SIGNALS (CLEAN — SINGLE SOURCE)
// ----------------------------------------------------

// 🔴 CURRENT
float currentMa = lab14Current();

// ----------------------------------------------------
// 🔴 CONTINUOUS HARD-STRESS VOLTAGE SAMPLING + LOG
// ----------------------------------------------------
if (!isLab14BMode && lab14Running && !lab14Cancelled) {

    try {

        float vNow = Float.NaN;

        // 🔴 PRIMARY — LAB14 voltage reader
        try {
            vNow = lab14Voltage();
        } catch (Throwable ignore) {}

        // 🔴 FALLBACK — filtered system voltage
        if (Float.isNaN(vNow) || vNow <= 0f) {
            try {
                vNow = getBatteryVoltageFiltered();
            } catch (Throwable ignore) {}
        }

        if (!Float.isNaN(vNow) && vNow > 3.0f && vNow < 5.0f) {
        	
// ----------------------------------------------------
// 🔴 VOLTAGE BEHAVIOR TRACKING — REAL HARD SAG AWARE
// ----------------------------------------------------
lab14LastHardVoltage = vNow;

// 🔴 lowest hard voltage — always track
if (Float.isNaN(lab14LowestHardVoltage) ||
        vNow < lab14LowestHardVoltage) {

    lab14LowestHardVoltage = vNow;
}

// 🔴 first REAL hard-load voltage
// Do not capture flat/cached voltage equal to start.
// Capture only when voltage actually drops from baseline.
if (Float.isNaN(lab14FirstHardVoltage) &&
        !Float.isNaN(voltageStart) &&
        vNow < voltageStart - 0.010f) {

    lab14FirstHardVoltage = vNow;
}

// 🔴 fallback: if no clear drop was detected, capture after 70 sec
if (Float.isNaN(lab14FirstHardVoltage) &&
        elapsed >= 70) {

    lab14FirstHardVoltage = vNow;
}

// 🔴 mid-run voltage — capture once around middle
if (Float.isNaN(lab14MidHardVoltage) &&
        elapsed >= (durationSec / 2)) {

    lab14MidHardVoltage = vNow;
}

// 🔴 late-run voltage — capture once near end
if (Float.isNaN(lab14LateHardVoltage) &&
        elapsed >= (durationSec - 45)) {

    lab14LateHardVoltage = vNow;
}

            // 🔴 keep lowest real hard-stress voltage
            if (Float.isNaN(voltageUnderLoad[0]) ||
                    vNow < voltageUnderLoad[0]) {

                voltageUnderLoad[0] = vNow;
            }

            // 🔴 refresh main-load samples during hard stress
            if (elapsed >= 5 && elapsed < 20) {

                if (Float.isNaN(vLoad1[0]) || vNow < vLoad1[0]) {
                    vLoad1[0] = vNow;
                }

            } else if (elapsed >= 20) {

                if (Float.isNaN(vLoad2[0]) || vNow < vLoad2[0]) {
                    vLoad2[0] = vNow;
                }
            }

            // 🔴 debug proof that hard-stress voltage is sampled
            if (DEBUG_MODE && elapsed % 5 == 0) {

                logWarn(
                        "MAIN_VOLTAGE | elapsed=" + elapsed +
                        " | now=" + String.format(Locale.US, "%.3f", vNow) +
                        " | load1=" + String.format(Locale.US, "%.3f", vLoad1[0]) +
                        " | load2=" + String.format(Locale.US, "%.3f", vLoad2[0]) +
                        " | underLoad=" + String.format(Locale.US, "%.3f", voltageUnderLoad[0])
                );
            }
        }

    } catch (Throwable ignore) {}
}

// ----------------------------------------------------
// 🔴 PRE-HARD VOLTAGE PULSE — ONCE BEFORE HARD PHASE
// ----------------------------------------------------
if (!isLab14BMode &&
        lab14Running &&
        !lab14Cancelled &&
        lab14FastPhase &&
        !lab14MainPhase &&
        !lab14PreHardVoltagePulseDone &&
        elapsed >= 25) {

    lab14PreHardVoltagePulseDone = true;

    lab14RunPreHardVoltagePulse(elapsed);
}

// ----------------------------------------------------
// 🔴 MICRO-RELAX VOLTAGE PULSE
// ----------------------------------------------------
if (!isLab14BMode &&
        lab14Running &&
        !lab14Cancelled &&
        elapsed >= 60 &&
        elapsed < durationSec - 20 &&
        !lab14VoltagePulseRunning &&
        elapsed - lab14LastVoltagePulseElapsedSec >= 55) {

    lab14LastVoltagePulseElapsedSec = elapsed;

    lab14RunVoltagePulse(elapsed);
}

// 🔴 BATTERY TEMP (helper ήδη filtered)
float batTemp = lab14BatteryTemp();

// 🔴 BATTERY %
float battPct = (float) getBatteryPercentSafe();
if (battPct < 0f || battPct > 100f) {
    battPct = Float.NaN;
}

// 🔴 CHARGING
boolean charging = isChargingNow();

// 🔴 BATTERY SCORE
int batteryScore = scoreBattery(batTemp, battPct, charging);

// ----------------------------------------------------
// 🔴 THERMAL DELTA (STRICT)
// ----------------------------------------------------
float thermalDelta = Float.NaN;

if (!Float.isNaN(startBatteryTemp) &&
    !Float.isNaN(batTemp)) {

    float delta = batTemp - startBatteryTemp;

    // 🔴 HARD FILTER (kill noise + spikes)
    if (Math.abs(delta) >= 0.5f &&   // ignore sensor jitter
        Math.abs(delta) <= 20f) {    // unrealistic spike cut

        thermalDelta = delta;
    }
}

// ----------------------------------------------------
// 🔴 DRAIN (FIXED - REAL CONSUMPTION)
// ----------------------------------------------------
long drainNow = 0L;

if (isLab14BMode) {

    iDoctorEngine.BatterySnapshot snapNow =
            iDoctorEngine.get(ManualTestsActivity.this)
                    .readBatterySnapshotLab();

    if (snapNow != null &&
        snapNow.chargeNowMah > 0 &&
        startMah > 0) {

        long diff = startMah - snapNow.chargeNowMah;

        if (diff > 0 && diff < 10000) { // 🔴 HARD FILTER
            drainNow = diff;
        }
    }

} else {

    // 🔴 NORMAL MODE → use FIRST vs LAST (όχι max/min)
    if (!lab14ChargeSamples.isEmpty() &&
        startMah > 0) {

        long last =
                lab14ChargeSamples.get(
                        lab14ChargeSamples.size() - 1
                );

        long diff = startMah - last;

        if (diff > 0 && diff < 10000) { // 🔴 HARD FILTER
            drainNow = diff;
        }
    }
}

// ----------------------------------------------------
// 🔴 RATE (FIXED - CONSISTENT)
// ----------------------------------------------------
double drainPerHour = Double.NaN;

// 🔴 REQUIRE MIN WINDOW
if (elapsed >= 10) {

    if (drainNow > 0) {

        double rate =
                (drainNow * 3600.0) / elapsed;

        // 🔴 HARD FILTER
        if (rate >= 10 && rate <= 8000) {
            drainPerHour = rate;
        }

    } else {

        // 🔴 FALLBACK → current (converted properly)
float currentFallback = lab14Current();

if (!Float.isNaN(currentFallback) &&
    Math.abs(currentFallback) >= 50f &&
    Math.abs(currentFallback) <= 8000f) {

    drainPerHour = Math.abs(currentFallback);
}
    }
}

    // ----------------------------------------------------
// 🔴 COLLECT AVERAGE (FIXED - STABLE)
// ----------------------------------------------------
if (!Double.isNaN(drainPerHour) && drainPerHour > 0) {

    // 🔴 HARD FILTER (same as engine)
    if (drainPerHour >= 10 && drainPerHour <= 5000) {

        // 🔴 OPTIONAL: ignore early unstable phase
        if (elapsed >= 10) {

            rateSum += drainPerHour;
            rateSamples++;
        }
    }
}

// ----------------------------------------------------
// 🔴 DEVICE CLASS (ROBUST)
// ----------------------------------------------------

long totalRamMb = -1L;

try {
    ActivityManager am =
            (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

    if (am != null) {
        ActivityManager.MemoryInfo mi =
                new ActivityManager.MemoryInfo();

        am.getMemoryInfo(mi);

        if (mi.totalMem > 0) {
            totalRamMb = mi.totalMem / (1024L * 1024L);
        }
    }

} catch (Throwable ignore) {}

// 🔴 FAILSAFE RAM (αν δεν πάρουμε τιμή)
if (totalRamMb < 0) {
    totalRamMb = 2048; // safe fallback (mid-low device)
}

int deviceClass;

// 🔴 HIGH-END
if (cores >= 8 && totalRamMb >= 6000) {
    deviceClass = 2;

// 🔴 MID
} else if (cores >= 6 && totalRamMb >= 3500) {
    deviceClass = 1;

// 🔴 LOW
} else {
    deviceClass = 0;
}

// ----------------------------------------------------
// 🔴 THRESHOLDS (BALANCED)
// ----------------------------------------------------
double currentMin;
float thermalMin;
double drainMin;
int scoreAbortThreshold;

switch (deviceClass) {

    // 🔴 HIGH-END
    case 2:
        currentMin = 180;     // πιο απαιτητικό load
        thermalMin = 1.0f;    // θέλεις πραγματική αύξηση
        drainMin = 18;
        scoreAbortThreshold = 7;
        break;

    // 🔴 MID
    case 1:
        currentMin = 140;
        thermalMin = 0.7f;
        drainMin = 12;
        scoreAbortThreshold = 6;
        break;

    // 🔴 LOW-END
    default:
        currentMin = 100;
        thermalMin = 0.5f;
        drainMin = 8;
        scoreAbortThreshold = 5; // 🔴 πιο αυστηρό
        break;
}

// ----------------------------------------------------
// 🔴 LOAD SIGNALS (FIXED — STABLE)
// ----------------------------------------------------
double absMa = Math.abs(currentMa);

// 🔴 CURRENT LOAD
boolean currentLoad =
        !Double.isNaN(currentMa) &&
        absMa >= currentMin &&
        absMa <= 8000; // 🔴 HARD CAP

// 🔴 THERMAL LOAD
boolean thermalLoad =
        !Float.isNaN(thermalDelta) &&
        thermalDelta >= thermalMin &&
        thermalDelta <= 20f; // 🔴 πιο ρεαλιστικό όριο

// 🔴 DRAIN LOAD
boolean drainLoad =
        !Double.isNaN(drainPerHour) &&
        drainPerHour >= drainMin &&
        drainPerHour <= 5000; // 🔴 spike protection

// ----------------------------------------------------
// 🔴 SCORE (BALANCED)
// ----------------------------------------------------
int loadScore = 0;

// 🔴 CPU = PRIMARY (but not enough alone)
boolean cpuFull =
        lab14CpuThreadsCurrent >= Math.max(2, cores - 1);

// 🔴 electrical signals consistency
boolean electricalLoad =
        currentLoad || drainLoad;

// 🔴 CPU contribution (μόνο αν υπάρχει πραγματικό load)
if (cpuFull && electricalLoad) {
    loadScore += 2;
}

// 🔴 CURRENT (strong indicator)
if (currentLoad) loadScore += 2;

// 🔴 THERMAL (real-world validation)
if (thermalLoad) loadScore += 2;

// 🔴 DRAIN (long-term validation)
if (drainLoad) loadScore += 1;

// ----------------------------------------------------
// 🔴 LIMITER DETECTION (STABLE + TIME SAFE)
// ----------------------------------------------------
boolean limiterNow =
        detectRealTimeLimiter(
                cpuFull,
                currentMa,
                drainPerHour,
                thermalDelta,
                charging
        );

// 🔴 TIME FILTER (min interval 1s)
long nowLimiter = SystemClock.elapsedRealtime();

if (nowLimiter - lab14LastLimiterCheck > 1000) {

    lab14LastLimiterCheck = nowLimiter;

    if (limiterNow) {
        lab14LimiterScore = Math.min(10, lab14LimiterScore + 1);
    } else {
        lab14LimiterScore = Math.max(0, lab14LimiterScore - 1);
    }
}

// 🔴 LATCH (one-shot)
if (lab14LimiterScore >= 4 && !lab14LimiterLatched) {

    lab14LimiterLatched = true;
    lab14_systemLimited[0] = true;

    appendLog("LIMITER", "BMS/system limiting confirmed");
}

// ----------------------------------------------------
// 🔴 FINAL FLAGS (FIXED)
// ----------------------------------------------------

// 🔴 REAL LOAD (needs physics, όχι μόνο CPU)
boolean realLoad =
        (loadScore >= 3) ||
        (cpuFull && electricalLoad);

// ----------------------------------------------------
// 🔴 WEAK LOAD
// ----------------------------------------------------
boolean weakLoad;

int halfCores = Math.max(2, cores / 2);

if (isLab14BMode) {

    if (inHardPhase) {

        // 🔥 HARD PHASE → NEVER WEAK
        weakLoad = false;

    } else {

        // SOFT phase
        weakLoad =
                (loadScore <= 1 &&
                 lab14CpuThreadsCurrent < halfCores &&
                 !electricalLoad);
    }

} else {

    weakLoad =
            (!earlyPhase &&
             loadScore <= 1 &&
             lab14CpuThreadsCurrent < (cores - 1) &&
             !electricalLoad);
}

lab14WeakLoad = weakLoad;

// ----------------------------------------------------
// 🔴 LAB14 ABORT (FINAL — SAFE)
// ----------------------------------------------------
if (!isLab14BMode &&
    inHardPhase &&
    elapsed >= 25 &&
    lab14WeakLoadCounter >= scoreAbortThreshold + 2) {

    logError(gr
        ? "Ανεπαρκές φορτίο — το test ακυρώθηκε"
        : "Insufficient load — test aborted");

    // 🔴 STATE
    lab14Cancelled = true;
    lab14Running = false;
    lab14FastPhase = false;
    lab14MainPhase = false;

    // 🔴 FULL STOP
    try { stopCpuBurn(); } catch (Throwable ignore) {}
    try { stopGpuStress(); } catch (Throwable ignore) {}
    try { stopMemoryStress(); } catch (Throwable ignore) {}
    try { stopMemoryBandwidthStress(); } catch (Throwable ignore) {}
    try { stopVibrationStress(); } catch (Throwable ignore) {}

    try {
        if (lab14StressVideo != null) {
            lab14StressVideo.stopPlayback();
        }
    } catch (Throwable ignore) {}

    try { restoreBrightnessAndKeepOn(); } catch (Throwable ignore) {}

    // 🔴 UI SAFE CLEANUP
    runOnUiThread(() -> {
        try {
            lab14CleanupUI();
        } catch (Throwable ignore) {}
    });

    return;
}

// ----------------------------------------------------
// 🔴 CPU FREQUENCY BOOST (FIXED — REAL LOAD)
// ----------------------------------------------------
long freq = readCpuFreq();

cpuFull = false;

// 🔴 DECAY PEAK (slow adaptation)
if (lab14CpuFreqPeak > 0) {
    lab14CpuFreqPeak *= 0.995f; // slow decay
}

if (freq > 0) {

    // 🔴 UPDATE PEAK
    if (freq > lab14CpuFreqPeak) {
        lab14CpuFreqPeak = freq;
    }

    // 🔴 RELATIVE CHECK
    boolean freqHigh =
            lab14CpuFreqPeak > 0 &&
            freq >= lab14CpuFreqPeak * 0.85f;

    // 🔴 REQUIRE THREAD ACTIVITY (IMPORTANT)
    boolean threadsActive =
            lab14CpuThreadsCurrent >= Math.max(2, cores - 1);

    cpuFull = freqHigh && threadsActive;
}

// 🔴 HARD OVERRIDE (only if real load)
if (cpuFull && (currentLoad || drainLoad)) {
    loadScore = Math.max(loadScore, 3);
}

// ----------------------------------------------------
// 🔴 STATUS (FIXED)
// ----------------------------------------------------

String status;

// 🔴 PRIORITY ORDER (MOST IMPORTANT FIRST)
if (lab14LimiterLatched) {

    status = "LIMITED ⚠⚠"; // confirmed limiter

} else if (lab14_systemLimited[0]) {

    status = "LIMITED ⚠"; // transient limiter

} else if (!lab14Running) {

    status = "STOPPED";

} else if (isLab14BMode) {

    status = (loadScore >= 2)
            ? "SOFT LOAD 🌿"
            : "WEAK LOAD ⚠";

} else if (lab14FastPhase) {

    status = "WARMING UP...";

} else if (loadScore >= 3) {

    status = "HIGH LOAD 🔥";

} else if (loadScore >= 2) {

    status = "NORMAL LOAD";

} else {

    status = "WEAK LOAD ⚠";
}

// ----------------------------------------------------
// 🔴 UI (FINAL FIXED)
// ----------------------------------------------------
try {

    SpannableStringBuilder sb = new SpannableStringBuilder();

    int white = 0xFFFFFFFF;
    int neon  = 0xFF39FF14;
    int red   = 0xFFFF4444;
    int orange = 0xFFFFA500;
    int cyan = 0xFF00BFFF;

    int start;

    // CPU
    start = sb.length();
    sb.append("CPU: ");
    sb.setSpan(new ForegroundColorSpan(white), start, sb.length(), 0);

    start = sb.length();
    sb.append(Float.isNaN(cpuTemp)
            ? "N/A\n"
            : String.format(Locale.US, "%.1f°C\n", cpuTemp));
    sb.setSpan(new ForegroundColorSpan(neon), start, sb.length(), 0);

    // BAT
    start = sb.length();
    sb.append("BAT: ");
    sb.setSpan(new ForegroundColorSpan(white), start, sb.length(), 0);

    start = sb.length();
    sb.append(Float.isNaN(batTemp)
            ? "N/A\n"
            : String.format(Locale.US, "%.1f°C\n", batTemp));
    sb.setSpan(new ForegroundColorSpan(neon), start, sb.length(), 0);

    // Consumption (FIX: no raw cast)
    start = sb.length();
    sb.append("Consumption: ");
    sb.setSpan(new ForegroundColorSpan(white), start, sb.length(), 0);

    start = sb.length();
    sb.append(Double.isNaN(drainPerHour)
            ? "N/A\n"
            : String.format(Locale.US, "%.0f mAh/h\n", drainPerHour));
    sb.setSpan(new ForegroundColorSpan(neon), start, sb.length(), 0);

    // STATUS
    start = sb.length();
    sb.append("Status: ");
    sb.setSpan(new ForegroundColorSpan(white), start, sb.length(), 0);

    start = sb.length();
    sb.append(status);

    int statusColor;

// 🔴 FIX: robust matching (όχι equals)
if (status != null && status.startsWith("HIGH")) {
    statusColor = neon;
} else if (status != null && status.startsWith("NORMAL")) {
    statusColor = cyan;
} else if (status != null && status.startsWith("WARM")) {
    statusColor = orange;
} else if (status != null && status.startsWith("LIMITED")) {
    statusColor = red;
} else if (status != null && status.startsWith("WEAK")) {
    statusColor = red;
} else {
    statusColor = white;
}

sb.setSpan(new ForegroundColorSpan(statusColor), start, sb.length(), 0);

lab14LiveStats.setText(sb);

} catch (Throwable t) {

    logError("LAB14 UI ERROR: " + t.getMessage());

}

} catch (Throwable t) {

    logError("LAB14 LIVE CRASH: " + t.getMessage());

}
}

private static class Lab14GpuRenderer implements GLSurfaceView.Renderer {

    private volatile boolean running = true;
    private volatile int intensity = 1;

    public void stop() {
        running = false;
    }

    public void setIntensity(int i) {
        intensity = Math.max(1, Math.min(4, i));
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 0f, 0f, 1f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        if (!running) return;

        float t = (SystemClock.elapsedRealtime() % 10000L) / 1000f;

        for (int i = 0; i < intensity; i++) {
            GLES20.glClearColor(
                    (float)Math.sin(t + i),
                    (float)Math.cos(t + i),
                    (float)Math.sin(t * 0.5f),
                    1f
            );
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        }
    }
}

    private int loadShader(int type, String code) {

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        return shader;
    }

private boolean detectLab14SystemLimiter(
        int elapsed,
        double currentMa,
        double drainPerHour,
        float batTempNow,
        boolean cpuFull,
        int loadScore
) {

    // 🔴 ONLY LAB14B + MIN TIME
    if (!isLab14BMode) return false;
    if (elapsed < 35) return false;

    // 🔴 REQUIRE REAL LOAD (CRITICAL)
    boolean realLoad =
            cpuFull || loadScore >= 3;

    if (!realLoad) return false;

    // 🔴 CURRENT
    boolean lowCurrent =
            !Double.isNaN(currentMa) &&
            Math.abs(currentMa) < 120.0;

    // 🔴 DRAIN
    boolean lowDrain =
            !Double.isNaN(drainPerHour) &&
            drainPerHour < 8.0;

    // 🔴 THERMAL (realistic)
    float delta = (!Float.isNaN(startBatteryTemp) &&
                   !Float.isNaN(batTempNow))
            ? (batTempNow - startBatteryTemp)
            : Float.NaN;

    boolean lowThermalRise =
            !Float.isNaN(delta) &&
            delta < 0.8f;

    // 🔴 MULTI-SIGNAL (όχι όλα απαραίτητα)
    int flags = 0;
    if (lowCurrent) flags++;
    if (lowDrain) flags++;
    if (lowThermalRise) flags++;

    return flags >= 2;
}

private void calibrateLoadZeroRisk() {

    if (lab14Running || lab14Cancelled) return;

    final int cores = Runtime.getRuntime().availableProcessors();
    long totalRamMb = -1L;

    try {
        ActivityManager am =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        if (am != null) {
            ActivityManager.MemoryInfo mi =
                    new ActivityManager.MemoryInfo();

            am.getMemoryInfo(mi);

            if (mi.totalMem > 0) {
                totalRamMb = mi.totalMem / (1024L * 1024L);
            }
        }
    } catch (Throwable ignore) {}

    // 🔴 FAILSAFE RAM
    if (totalRamMb < 0) {
        totalRamMb = 2048; // safe default
    }

    int threads;

    // 🔴 STRICT CLASSIFICATION (AND όχι OR)
    if (cores <= 4 || totalRamMb < 3000) {

        threads = 2; // low

    } else if (cores <= 6 && totalRamMb < 5000) {

        threads = 3; // mid

    } else if (cores <= 8 && totalRamMb < 8000) {

        threads = 4; // upper mid

    } else {

        threads = 5; // flagship safe cap
    }

    // 🔴 HARD CLAMP
    threads = Math.max(2, Math.min(5, threads));

    lab14OptimalThreads = threads;

    appendLog("CALIB",
            "Zero-risk threads=" + threads +
            " | cores=" + cores +
            " | ram=" + totalRamMb + "MB");
}

private void startGpuStressLevel(int level) {

    if (lab14GpuRenderer != null) {
        lab14GpuRenderer.setIntensity(level);
        return;
    }

    lab14GpuRenderer = new Lab14GpuRenderer();
    lab14GpuRenderer.setIntensity(level);

    gpuView = new GLSurfaceView(this);
    gpuView.setEGLContextClientVersion(2);
    gpuView.setRenderer(lab14GpuRenderer);
    gpuView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

    // invisible αλλά ενεργό
    addContentView(
            gpuView,
            new ViewGroup.LayoutParams(1, 1)
    );
}

private void rebalanceLab14CpuLive(
        boolean weakLoad,
        float thermalDelta,
        boolean systemLimited
) {

    // 🔴 LAB14 → ΔΕΝ αγγίζεται ΠΟΤΕ
    if (!isLab14BMode) return;

    if (!lab14Running || lab14Cancelled) return;

    long now = SystemClock.elapsedRealtime();

    // 🔴 cooldown
    if (now - lab14LastCpuAdjustTs < 6000) return;

    final int cores =
            Runtime.getRuntime().availableProcessors();

    int oldThreads =
            lab14CpuThreadsCurrent > 0
                    ? lab14CpuThreadsCurrent
                    : Math.max(4, cores - 1);

    int newThreads = oldThreads;

    boolean validThermal =
            !Float.isNaN(thermalDelta);

    boolean lowThermal =
            validThermal && thermalDelta < 3f;

    boolean midThermal =
            validThermal &&
            thermalDelta >= 3f &&
            thermalDelta <= 7f;

    boolean highThermal =
            validThermal && thermalDelta > 7f;

    // ----------------------------------------------------
    // 🔴 HARD PHASE PROTECTION
    // ----------------------------------------------------
    int minThreads;

    if (inHardPhase) {

        minThreads = Math.max(4, cores / 2);

    } else {

        minThreads = Math.max(2, cores / 4);
    }

    // ----------------------------------------------------
    // 🔴 SAFETY
    // ----------------------------------------------------
    if (systemLimited || highThermal) {

        newThreads = oldThreads - 1;

    }
    // ----------------------------------------------------
    // 🔴 BOOST
    // ----------------------------------------------------
    else if (weakLoad &&
             lab14WeakLoadCounter >= 5 &&
             lowThermal) {

        newThreads = oldThreads + 1;

    }
    // ----------------------------------------------------
    // 🔴 MID ZONE
    // ----------------------------------------------------
    else if (midThermal) {

        newThreads = oldThreads;
    }

    // ----------------------------------------------------
    // 🔴 CLAMP
    // ----------------------------------------------------
    newThreads =
            Math.max(1,
            Math.min(cores, newThreads));

    newThreads =
            Math.max(newThreads, minThreads);

    if (newThreads == oldThreads) return;

    // ----------------------------------------------------
    // 🔴 APPLY
    // ----------------------------------------------------
    lab14CpuThreadsCurrent = newThreads;
    lab14LastCpuAdjustTs = now;

    try {

        stopCpuBurn();
        startCpuBurnLimitedThreads(newThreads);

    } catch (Throwable ignore) {}

    appendLog("CPU",
            "threads " + oldThreads +
            " → " + newThreads +
            " ΔT=" +
            (validThermal
                    ? String.format(
                            java.util.Locale.US,
                            "%.2f",
                            thermalDelta)
                    : "N/A"));
}

private boolean detectRealTimeLimiter(
        boolean cpuFull,
        double currentMa,
        double drainPerHour,
        float thermalDelta,
        boolean charging
) {

    if (charging) return false;
    if (!cpuFull) return false;

    // 🔴 REQUIRE REAL LOAD SIGNAL
    boolean hasElectricalLoad =
            (!Double.isNaN(currentMa) && Math.abs(currentMa) >= 120) ||
            (!Double.isNaN(drainPerHour) && drainPerHour >= 200);

    if (!hasElectricalLoad) return false;

    // 🔴 CURRENT
    boolean lowCurrent =
            !Double.isNaN(currentMa) &&
            Math.abs(currentMa) < 180;

    // 🔴 DRAIN
    boolean lowDrain =
            !Double.isNaN(drainPerHour) &&
            drainPerHour > 0 &&
            drainPerHour < 300;

    // 🔴 THERMAL (more realistic)
    boolean lowThermal =
            !Float.isNaN(thermalDelta) &&
            thermalDelta >= 0f &&
            thermalDelta < 1.2f;

    int hits = 0;
    if (lowCurrent) hits++;
    if (lowDrain) hits++;
    if (lowThermal) hits++;

    return hits >= 2;
}

// =====================================================
// 🔴 SAFE SLEEP
// =====================================================
private void sleepSilently(long ms) {
    try {
        Thread.sleep(ms);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); // 🔴 CRITICAL
    } catch (Throwable ignore) {}
}

// =====================================================
// 🔴 VOLTAGE HELPER (SAFE)
// =====================================================
private float voltageOrNaN(float v) {

    // 🔴 preserve NaN (CRITICAL)
    if (Float.isNaN(v)) return Float.NaN;

    // 🔴 sanity range (battery voltage only)
    if (v < 2.5f || v > 5.5f) return Float.NaN;

    return v;
}

// =====================================================
// 🔴 MEMORY BANDWIDTH STRESS (FIXED)
// =====================================================
private volatile boolean memBandwidthRunning = false;
private Thread memBandwidthThread;

private void startMemoryBandwidthStress() {

    stopMemoryBandwidthStress();

    memBandwidthRunning = true;

    memBandwidthThread = new Thread(() -> {

        try {

            final int size = 32 * 1024 * 1024; // 🔴 32MB (cache bust)
            byte[] src = new byte[size];
            byte[] dst = new byte[size];

            // 🔴 init
            for (int i = 0; i < size; i++) {
                src[i] = (byte) (i);
            }

            while (memBandwidthRunning &&
                   lab14Running &&
                   !lab14Cancelled &&
                   !Thread.currentThread().isInterrupted()) {

                // 🔴 REAL bandwidth copy
                System.arraycopy(src, 0, dst, 0, size);
                System.arraycopy(dst, 0, src, 0, size);

            }

        } catch (Throwable t) {
            logError("MEM BW ERROR: " + t.getMessage());
        }

    }, "LAB14_MEM_BW");

    // 🔴 πιο ρεαλιστικό scheduling
    memBandwidthThread.setPriority(Thread.NORM_PRIORITY);

    memBandwidthThread.start();
}

private void stopMemoryBandwidthStress() {

    memBandwidthRunning = false;

    if (memBandwidthThread != null) {
        try {
            memBandwidthThread.interrupt();
        } catch (Throwable ignore) {}
        memBandwidthThread = null;
    }
}

// =====================================================
// 🔴 VIBRATION STRESS (FIXED)
// =====================================================
private volatile boolean vibrationRunning = false;
private android.os.Vibrator vibrator;
private Thread vibrationThread;

private void startVibrationStress() {

    stopVibrationStress();

    try {

        vibrator = (android.os.Vibrator)
                getSystemService(VIBRATOR_SERVICE);

        if (vibrator == null || !vibrator.hasVibrator())
            return;

        vibrationRunning = true;

        vibrationThread = new Thread(() -> {

            try {

                while (vibrationRunning &&
                       lab14Running &&
                       !lab14Cancelled &&
                       !Thread.currentThread().isInterrupted()) {

                    if (android.os.Build.VERSION.SDK_INT >= 26) {

                        vibrator.vibrate(
                                android.os.VibrationEffect.createOneShot(
                                        200,
                                        android.os.VibrationEffect.DEFAULT_AMPLITUDE
                                )
                        );

                    } else {

                        vibrator.vibrate(200);
                    }

                    sleepSilently(250); // 🔴 uses interrupt-safe helper
                }

            } catch (Throwable t) {
                logError("VIBRATION ERROR: " + t.getMessage());
            }

        }, "LAB14_VIB");

        vibrationThread.setPriority(Thread.NORM_PRIORITY);
        vibrationThread.start();

    } catch (Throwable t) {
        logError("VIB INIT ERROR: " + t.getMessage());
    }
}

private void stopVibrationStress() {

    vibrationRunning = false;

    if (vibrationThread != null) {
        try {
            vibrationThread.interrupt();
        } catch (Throwable ignore) {}
        vibrationThread = null;
    }

    try {
        if (vibrator != null) {
            vibrator.cancel();
        }
    } catch (Throwable ignore) {}
}

private String downgradeBatteryLevel(String level) {

    if (level == null) return "Unknown";

    String l = level.trim();

    switch (l) {
        case "Excellent":
        case "EXCELLENT":
        case "excellent":
            return "Good";

        case "Good":
        case "GOOD":
        case "good":
            return "Normal";

        case "Normal":
        case "NORMAL":
        case "normal":
            return "Weak";

        case "Weak":
        case "WEAK":
        case "weak":
            return "Critical";

        case "Critical":
        case "CRITICAL":
        case "critical":
            return "Critical";

        default:
            return "Unknown"; // 🔴 safer fallback
    }
}

// =====================================================
// 🔴 SAG CLASSIFIER (FIXED)
// =====================================================
private String classifySag(float sag) {

    if (Float.isNaN(sag)) return "Unknown";

    if (sag <= 0.05f) return "Excellent";
    if (sag <= 0.10f) return "Good";
    if (sag <= 0.18f) return "Normal";
    if (sag <= 0.30f) return "Weak";

    return "Critical";
}

// =====================================================
// 🔴 LABEL DOWNGRADE (FINAL SAFE)
// =====================================================
private String downgrade(String label) {

    if (label == null) return "Unknown";

    switch (label.trim().toLowerCase(Locale.US)) {

        case "excellent": return "Good";
        case "good":      return "Normal";
        case "normal":    return "Weak";
        case "weak":      return "Critical";
        case "critical":  return "Critical";

        default:          return "Unknown"; // 🔴 safe fallback
    }
}

// =====================================================
// 🔴 BATTERY TRUTH DOWNGRADE (FINAL SAFE)
// =====================================================
private String downgradeBatteryTruth(String label) {

    if (label == null) return "Unknown";

    switch (label.trim().toLowerCase(Locale.US)) {

        case "excellent": return "Good";
        case "good":      return "Normal";
        case "normal":    return "Weak";
        case "weak":      return "Critical";
        case "critical":  return "Critical";

        default:          return "Unknown"; // 🔴 safe fallback
    }
}

// =====================================================
// 🔴 BATTERY TRUTH COLOR MODE (FINAL SAFE)
// =====================================================
private int getBatteryTruthColorMode(String label) {

    if (label == null) return 0;

    switch (label.trim().toLowerCase(Locale.US)) {

        case "excellent":
        case "good":
        case "normal":
            return 1; // green

        case "weak":
            return 2; // warn

        case "critical":
            return 3; // error

        default:
            return 0; // neutral
    }
}

// =====================================================
// 🔴 STOP FAST STRESS (SAFE COMPLETE)
// =====================================================
private void stopFastStressSafe() {

    try { stopCpuBurn(); } 
    catch (Throwable t) { logError("STOP CPU FAIL: " + t.getMessage()); }

    try { stopMemoryStress(); } 
    catch (Throwable t) { logError("STOP MEM FAIL: " + t.getMessage()); }

    try { stopMemoryBandwidthStress(); } 
    catch (Throwable t) { logError("STOP MEM BW FAIL: " + t.getMessage()); }

    try { stopGpuStress(); } 
    catch (Throwable t) { logError("STOP GPU FAIL: " + t.getMessage()); }

    try { stopVibrationStress(); } 
    catch (Throwable t) { logError("STOP VIB FAIL: " + t.getMessage()); }
}

// =====================================================
// 🔴 RESET LAB14 RUNTIME STATE (FULL SAFE)
// =====================================================
private void resetLab14RuntimeState() {

    // 🔴 limiter / load
    lab14LimiterScore = 0;
    lab14LimiterLatched = false;
    lab14_systemLimited[0] = false;

    // 🔴 load control
    lab14WeakLoadCounter = 0;
    lab14BoostActive = false;
    lab14RestartAttempts = 0;

    // 🔴 phases
    lab14FastPhase = false;
    lab14MainPhase = false;
    lab14FastDone = false;

    // 🔴 timing
    lab14LastCpuAdjustTs = 0L;
    lab14LastGpuAdjustTs = 0L;
    lab14LastLiveLogTs = 0L;

    // 🔴 thermal
    lab14TempPeak = Float.NaN;

    // 🔴 active risks only
    collapseRisk[0] = false;

    // 🔴 runtime warnings
    lab14BatteryBehaviourWarning = false;
}

// =====================================================
// 🔴 PRIVACY SNAPSHOT CONVERTER (SAFE)
// =====================================================
private iDoctorEngine.PrivacySnapshot convertToEnginePrivacy(PrivacySnapshot p) {

    iDoctorEngine.PrivacySnapshot out =
            new iDoctorEngine.PrivacySnapshot();

    if (p == null) return out;

    // 🔴 safe copy (no negatives)
    out.userAppsWithLocation = Math.max(0, p.userAppsWithLocation);
    out.userAppsWithMic      = Math.max(0, p.userAppsWithMic);
    out.userAppsWithCamera   = Math.max(0, p.userAppsWithCamera);
    out.userAppsWithSms      = Math.max(0, p.userAppsWithSms);

    return out;
}

// =====================================================
// 🔴 UPDATE PROGRESS BAR (SAFE)
// =====================================================
private void updateProgressBar(int value, int max) {

    if (lab14MainBar == null) return;

    int segCount = lab14MainBar.getChildCount();
    if (segCount <= 0) return;

    // 🔴 SAFE GUARDS
    if (max <= 0) max = 1;
    if (value < 0) value = 0;

    float ratio = Math.min(1f, value / (float) max);

    // 🔴 Fill only completed segments.
    // With 12 segments over 300 sec, each segment fills after 25 sec,
    // not immediately when the test starts.
    int active = Math.min(segCount,
            (int) Math.floor(ratio * segCount));

    for (int i = 0; i < segCount; i++) {

        View seg = lab14MainBar.getChildAt(i);

        if (seg != null) {
            seg.setBackgroundColor(
                    i < active ? 0xFF39FF14 : 0xFF333333
            );
        }
    }
}

// ============================================================
// 🔴 FAST VOLTAGE SAMPLING (REFINED)
// ============================================================
private void runFastVoltageSampling(
        iDoctorEngine idoctor,
        float[] vStart,
        float[] vLoad1,
        float[] vRecover,
        float[] vLoad2
) {
    if (idoctor == null) return;
    if (vStart == null || vStart.length == 0) return;
    if (vLoad1 == null || vLoad1.length == 0) return;
    if (vRecover == null || vRecover.length == 0) return;
    if (vLoad2 == null || vLoad2.length == 0) return;
    if (!lab14Running || lab14Cancelled) return;

    try {

        // 🔴 HELPER (RAW-first)
        final Supplier<Float> readVoltage = () -> {

    float v = Float.NaN;

    // 🔴 PRIMARY: raw fast voltage_now
    try {

        File f = new File(
                "/sys/class/power_supply/battery/voltage_now"
        );

        if (f.exists()) {

            BufferedReader br =
                    new BufferedReader(new FileReader(f));

            String s = br.readLine();

            br.close();

            if (s != null) {

                float raw =
                        Float.parseFloat(s.trim());

                float rawV;

                // µV → V
                if (raw > 1000000f) {
                    rawV = raw / 1000000f;

                // mV → V fallback
                } else if (raw > 1000f) {
                    rawV = raw / 1000f;

                } else {
                    rawV = raw;
                }

                if (rawV > 3.0f && rawV < 5.0f) {
                    v = rawV;
                }
            }
        }

    } catch (Throwable ignore) {}

    // 🔴 FALLBACK: Battery Intent only if raw failed
    if (Float.isNaN(v)) {

        try {

            IntentFilter ifilter =
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

            Intent batteryStatus =
                    registerReceiver(null, ifilter);

            if (batteryStatus != null) {

                int mv =
                        batteryStatus.getIntExtra("voltage", -1);

                if (mv > 3000 && mv < 5000) {
                    v = mv / 1000f;
                }
            }

        } catch (Throwable ignore) {}
    }

    return (!Float.isNaN(v) &&
            v > 3.0f &&
            v < 5.0f)
            ? v
            : Float.NaN;
};

// ----------------------------------------------------
// 🔴 START (baseline χωρίς kill load)
// ----------------------------------------------------
if (Float.isNaN(vStart[0])) {

    if (!sleepSilentlySafe(120)) return;
    if (!lab14Running || lab14Cancelled) return;

    float vs = readVoltage.get();

    if (!Float.isNaN(vs)) {
        vStart[0] = vs;
    }
}

// ----------------------------------------------------
// 🔴 LOAD 1 (BURST MIN-SAMPLING)
// ----------------------------------------------------
if (!sleepSilentlySafe(180)) return;
if (!lab14Running || lab14Cancelled) return;

float load1 = Float.NaN;

for (int i = 0; i < 12; i++) {

    float v = readVoltage.get();

    if (!Float.isNaN(v)) {

        // 🔴 LOCAL LOWEST
        if (Float.isNaN(load1) || v < load1) {
            load1 = v;
        }

        // 🔴 GLOBAL LOWEST (MOST IMPORTANT)
        if (Float.isNaN(voltageUnderLoad[0]) ||
            v < voltageUnderLoad[0]) {

            voltageUnderLoad[0] = v;
        }
    }

    SystemClock.sleep(90);
}

if (!Float.isNaN(load1)) {

    if (Float.isNaN(vLoad1[0]) || load1 < vLoad1[0]) {
        vLoad1[0] = load1;
    }
}

// ----------------------------------------------------
// 🔴 RECOVERY (BURST MAX-SAMPLING)
// ----------------------------------------------------
if (!sleepSilentlySafe(220)) return;
if (!lab14Running || lab14Cancelled) return;

float rec = Float.NaN;

for (int i = 0; i < 12; i++) {

    float v = readVoltage.get();

    if (!Float.isNaN(v)) {

        // 🔴 KEEP HIGHEST VOLTAGE (TRUE RECOVERY)
        if (Float.isNaN(rec) || v > rec) {
            rec = v;
        }
    }

    SystemClock.sleep(90);
}

if (!Float.isNaN(rec)) {

    float refLoad =
            !Float.isNaN(vLoad1[0])
                    ? vLoad1[0]
                    : Float.NaN;

    if (Float.isNaN(refLoad) || rec >= refLoad) {

        if (Float.isNaN(vRecover[0]) || rec > vRecover[0]) {
            vRecover[0] = rec;
        }
    }
}

// ----------------------------------------------------
// 🔴 LOAD 2 (BURST MIN-SAMPLING)
// ----------------------------------------------------
if (!sleepSilentlySafe(220)) return;
if (!lab14Running || lab14Cancelled) return;

float load2 = Float.NaN;

for (int i = 0; i < 12; i++) {

    float v = readVoltage.get();

    if (!Float.isNaN(v)) {

        // 🔴 LOCAL LOWEST
        if (Float.isNaN(load2) || v < load2) {
            load2 = v;
        }

        // 🔴 GLOBAL LOWEST (MOST IMPORTANT)
        if (Float.isNaN(voltageUnderLoad[0]) ||
            v < voltageUnderLoad[0]) {

            voltageUnderLoad[0] = v;
        }
    }

    SystemClock.sleep(90);
}

if (!Float.isNaN(load2)) {

    if (Float.isNaN(vLoad2[0]) || load2 < vLoad2[0]) {
        vLoad2[0] = load2;
    }

    vLoad2Time = SystemClock.elapsedRealtime();
}

        // ----------------------------------------------------
        // 🔴 DEBUG
        // ----------------------------------------------------
        if (DEBUG_MODE) {
            logWarn(
                    "FAST_VOLTAGE | start=" + vStart[0]
                    + " | load1=" + vLoad1[0]
                    + " | load2=" + vLoad2[0]
                    + " | recover=" + vRecover[0]
                    + " | underLoad=" + voltageUnderLoad[0]
            );
        }

    } catch (Throwable ignore) {}
}

// =====================================================
// 🔴 STOP ALL STRESS (SAFE FINAL)
// =====================================================
private void stopAllStressSafe() {

    // 🔴 STOP FLAG FIRST (CRITICAL)
    lab14Running = false;

    try { stopCpuBurn(); } 
    catch (Throwable t) { logError("STOP CPU FAIL: " + t.getMessage()); }

    try { stopMemoryStress(); } 
    catch (Throwable t) { logError("STOP MEM FAIL: " + t.getMessage()); }

    try { stopGpuStress(); } 
    catch (Throwable t) { logError("STOP GPU FAIL: " + t.getMessage()); }

    try { stopMemoryBandwidthStress(); } 
    catch (Throwable t) { logError("STOP MEM BW FAIL: " + t.getMessage()); }

    try { stopVibrationStress(); } 
    catch (Throwable t) { logError("STOP VIB FAIL: " + t.getMessage()); }
}

// =====================================================
// 🔴 FAST PHASE STOP (SAFE)
// =====================================================
private void setFastPhaseStopped() {
    synchronized (fastPhaseLock) {

        lab14FastDone = true;
        lab14FastPhase = false;

        // 🔴 ΜΗΝ πειράζεις το main phase εδώ
        // αυτό ελέγχεται στο transition logic
    }
}

// ============================================================
// 🔴 LAB14 — BATTERY TEMP (FINAL SAFE)
// ============================================================
private float lab14BatteryTemp() {

    try {

        iDoctorEngine idoctor =
                iDoctorEngine.get(ManualTestsActivity.this);

        Float tObj = idoctor.getBatteryTempUnified();
        if (tObj == null) return Float.NaN;

        float t = tObj;

        // 🔴 HARD INVALID FILTER
        if (Float.isNaN(t) || t <= 0f || t > 85f)
            return Float.NaN;

        // 🔴 REALISTIC RANGE (battery physics)
        if (t < 5f) return Float.NaN;   // sensor glitch / unrealistic

        return t;

    } catch (Throwable t) {
        logError("BAT TEMP FAIL: " + t.getMessage());
    }

    return Float.NaN;
}

// ============================================================
// 🔴 LAB14 — VOLTAGE (FINAL SAFE)
// ============================================================
private float lab14Voltage() {

    try {

        iDoctorEngine idoctor =
                iDoctorEngine.get(ManualTestsActivity.this);

        float mv =
                idoctor.readBatteryVoltageMvStable(3, 10);

        // 🔴 PRIMARY FILTER (realistic battery range)
        if (!Float.isNaN(mv) && mv > 2500f && mv < 5500f) {
            return mv / 1000f;
        }

        // 🔴 FALLBACK (retry with softer sampling)
        mv = idoctor.readBatteryVoltageMvStable(5, 20);

        if (!Float.isNaN(mv) && mv > 2500f && mv < 5500f) {
            return mv / 1000f;
        }

    } catch (Throwable t) {
        logError("BAT VOLT FAIL: " + t.getMessage());
    }

    return Float.NaN;
}

// ============================================================
// LAB14 — SNAPSHOT (FINAL SAFE)
// ============================================================
private iDoctorEngine.BatterySnapshot lab14Snapshot() {

    try {

        iDoctorEngine idoctor =
                iDoctorEngine.get(ManualTestsActivity.this);

        iDoctorEngine.BatterySnapshot s =
                idoctor.readBatterySnapshotLab();

        if (s != null &&
            s.voltageMv > 2500 &&
            s.voltageMv < 5500) {

            return s;
        }

    } catch (Throwable t) {
        logError("LAB14 SNAPSHOT FAIL: " + t.getMessage());
    }

    return null;
}

// ============================================================
// LAB14 — CURRENT (FINAL SAFE)
// ============================================================
private float lab14Current() {

    try {

        iDoctorEngine idoctor =
                iDoctorEngine.get(ManualTestsActivity.this);

        float ma = idoctor.getBatteryCurrentNowUnified();

        // 🔴 PRIMARY FILTER
        if (!Float.isNaN(ma)) {

            float abs = Math.abs(ma);

            if (abs >= 50f && abs <= 12000f) {
                return ma;
            }
        }

        // 🔴 FALLBACK (retry once)
        ma = idoctor.getBatteryCurrentNowUnified();

        if (!Float.isNaN(ma)) {

            float abs = Math.abs(ma);

            if (abs >= 50f && abs <= 12000f) {
                return ma;
            }
        }

    } catch (Throwable t) {
        logError("BAT CURRENT FAIL: " + t.getMessage());
    }

    // 🔴 FALLBACK — activates BatteryManager CURRENT_NOW helper
    try {
        float fallback = getBatteryCurrentNowSafe();

        if (!Float.isNaN(fallback)) {
            float abs = Math.abs(fallback);

            if (abs >= 50f && abs <= 12000f) {
                return fallback;
            }
        }

    } catch (Throwable ignore) {}

    return Float.NaN;
}

private void safeDismissDialog() {
    runOnUiThread(() -> {
        try {
            if (lab14Dialog != null && lab14Dialog.isShowing()) {
                lab14Dialog.dismiss();
                lab14Dialog = null;
            }
        } catch (Throwable ignore) {}
    });
}

private boolean sleepSilentlySafe(long ms) {
    long end = SystemClock.elapsedRealtime() + ms;

    while (SystemClock.elapsedRealtime() < end) {
        if (!lab14Running || lab14Cancelled) return false;

        try {
            Thread.sleep(50);
        } catch (Throwable ignore) {}
    }

    return true;
}

// ============================================================
// LAB 14 — CANCEL HELPER
// ============================================================
private void lab14CancelStress() {

    lab14Cancelled = true;

    try { stopCpuBurn(); } catch (Throwable ignore) {}
    try { stopMemoryStress(); } catch (Throwable ignore) {}
    try { stopGpuStress(); } catch (Throwable ignore) {}

    try {
        restoreBrightnessAndKeepOn();   // ✅ ΠΡΟΣΘΗΚΗ
    } catch (Throwable ignore) {}

    try {
    	counterText = null;
        lab14CleanupUI();
    } catch (Throwable ignore) {}

}

// ============================================================
// LAB 14 — DIALOG BUILDER
// ============================================================
private AlertDialog.Builder lab14DialogBuilder() {

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    b.setCancelable(false);

    return b;
}

// ============================================================
// LAB 14 — CONDITION CHECK POPUP (GEL STYLE + TTS + HELPERS)
// ============================================================
private void showLab14ConditionCheck(Runnable startAction) {

    final boolean gr = AppLang.isGreek(this);

    int percent = (int) getCurrentBatteryPercent();
    float tempC = getBatteryTemperature();
    boolean chargingNow = isDeviceCharging();
    float cpuTemp = readCpuTempSafe();

    boolean badBat = percent < 30 || percent > 90;
    boolean badCpu = !Float.isNaN(cpuTemp) && cpuTemp >= 60f;
    boolean badTemp = !Float.isNaN(tempC) && tempC >= 39f;
    boolean cpuOk = Float.isNaN(cpuTemp) || cpuTemp < 60f;

    final boolean canStart =
            !badBat &&
            !chargingNow &&
            cpuOk &&
            !badTemp;

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    b.setCancelable(true);

    LinearLayout root = buildGELPopupRoot(this);

    // HEADER
    root.addView(
            buildPopupHeader(
                    this,
                    gr
                            ? "Έλεγχος συνθηκών LAB 14"
                            : "LAB 14 Condition Check"
            )
    );

    // INFO
    TextView info = new TextView(this);
    info.setText(
            gr
                    ? "Για την εκτέλεση του τεστ απαιτούνται:\n\n"
                    + "1) Μπαταρία μεταξύ 30% – 90%\n"
                    + "2) Θερμοκρασία μπαταρίας κάτω από 39°C\n"
                    + "3) Θερμοκρασία CPU κάτω από 60°C\n"
                    + "4) Η συσκευή να μην φορτίζεται\n"
                    : "Requirements for this test:\n\n"
                    + "1) Battery between 30% – 90%\n"
                    + "2) Battery temperature below 39°C\n"
                    + "3) CPU temperature below 60°C\n"
                    + "4) Device must not be charging\n"
    );
    info.setTextColor(0xFF39FF14);
    info.setTextSize(14f);
    info.setPadding(0, dp(8), 0, dp(6));
    root.addView(info);

    // WARN TEXT
    StringBuilder warn = new StringBuilder();
    boolean hasWarn = false;

    if (badBat) {
        hasWarn = true;
        warn.append(gr
                ? "• Η μπαταρία πρέπει να είναι μεταξύ 30% και 90%\n"
                : "• Battery must be between 30% and 90%\n");
    }

    if (chargingNow) {
        hasWarn = true;
        warn.append(gr
                ? "• Η συσκευή δεν πρέπει να φορτίζει\n"
                : "• Device must not be charging\n");
    }

    if (!Float.isNaN(cpuTemp) && badCpu) {
        hasWarn = true;
        warn.append(gr
                ? "• Υψηλή θερμοκρασία CPU\n"
                : "• High CPU temperature\n");
    }

    if (badTemp) {
        hasWarn = true;
        warn.append(gr
                ? "• Υψηλή θερμοκρασία μπαταρίας\n"
                : "• High battery temperature\n");
    }

    if (!hasWarn) {
        warn.append(gr
                ? "Οι συνθήκες είναι κατάλληλες"
                : "Conditions are OK");
    }

    TextView warnView = new TextView(this);
    warnView.setText(warn.toString());
    warnView.setTextColor(hasWarn ? 0xFFFF4444 : 0xFF39FF14);
    warnView.setTextSize(13f);
    root.addView(warnView);

    // BUTTON ROW
LinearLayout row = new LinearLayout(this);
row.setOrientation(LinearLayout.HORIZONTAL);

Button cancel =
        gelButton(this, gr ? "Ακύρωση" : "Cancel", 0xFF8B0000);

Button start =
        gelButton(this, gr ? "Έναρξη" : "Start", 0xFF0B5D1E);

// 🔴 LAYOUT LOGIC
if (canStart) {

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(0, dp(48), 1);

    lp.setMargins(dp(6), dp(18), dp(6), 0);

    cancel.setLayoutParams(lp);
    start.setLayoutParams(lp);

    row.addView(cancel);
    row.addView(start);

} else {

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(48)
            );

    lp.setMargins(dp(6), dp(18), dp(6), 0);

    cancel.setLayoutParams(lp);

    row.addView(cancel);
}

root.addView(row);

b.setView(root);

final AlertDialog dlg = b.create();

if (dlg.getWindow() != null) {
    dlg.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

dlg.show();
com.gel.cleaner.GelButtonLayout.fitDialog(dlg);

    // 🔴 TTS (ORDER FIXED)
new Handler(Looper.getMainLooper()).postDelayed(() -> {

    if (!dlg.isShowing() || AppTTS.isMuted(this)) return;

    AppTTS.stop(); // prevent overlap

    StringBuilder speak = new StringBuilder();

    // INTRO
    speak.append(gr
            ? "Έλεγχος συνθηκών για το LAB 14. "
            : "Condition check for LAB 14. ");

    // 1️⃣ BATTERY LEVEL
    speak.append(gr ? "Επίπεδο μπαταρίας " : "Battery level ");
    speak.append(percent).append("%. ");

    if (badBat) {
        speak.append(gr
                ? "Εκτός προτεινόμενου εύρους 30 έως 90 τοις εκατό. "
                : "Outside recommended range 30 to 90 percent. ");
    } else {
        speak.append(gr
                ? "Εντός αποδεκτού εύρους. "
                : "Within acceptable range. ");
    }

    // 2️⃣ BATTERY TEMP
    if (!Float.isNaN(tempC)) {

        speak.append(gr
                ? "Θερμοκρασία μπαταρίας "
                : "Battery temperature ");

        speak.append(String.format(Locale.US, "%.1f", tempC)).append(" βαθμοί. ");

        if (badTemp) {
            speak.append(gr
                    ? "Υψηλή θερμοκρασία. "
                    : "Temperature is high. ");
        } else {
            speak.append(gr
                    ? "Κανονική θερμοκρασία. "
                    : "Temperature is normal. ");
        }
    }

    // 3️⃣ CPU TEMP
    if (!Float.isNaN(cpuTemp)) {

        speak.append(gr
                ? "Θερμοκρασία CPU "
                : "CPU temperature ");

        speak.append(String.format(Locale.US, "%.1f", cpuTemp)).append(" βαθμοί. ");

        if (badCpu) {
            speak.append(gr
                    ? "Υψηλή θερμοκρασία. "
                    : "Temperature is high. ");
        } else {
            speak.append(gr
                    ? "Εντός ορίων. "
                    : "Within limits. ");
        }
    }

    // 4️⃣ CHARGING
    if (chargingNow) {
        speak.append(gr
                ? "Η συσκευή φορτίζει. Αυτό δεν επιτρέπεται. "
                : "Device is charging. This is not allowed. ");
    } else {
        speak.append(gr
                ? "Η συσκευή δεν φορτίζει. "
                : "Device is not charging. ");
    }

    // FINAL
    if (canStart) {
        speak.append(gr
                ? "Όλες οι συνθήκες είναι κατάλληλες. Μπορείτε να ξεκινήσετε το τεστ."
                : "All conditions are satisfied. You may start the test.");
    } else {
        speak.append(gr
                ? "Οι συνθήκες δεν είναι κατάλληλες για ασφαλή εκτέλεση του τεστ."
                : "Conditions are not suitable for safe test execution.");
    }

    AppTTS.ensureSpeak(this, speak.toString());

}, 200);

    // ACTIONS
    cancel.setOnClickListener(v -> {
        AppTTS.stop();
        dlg.dismiss();
    });

    start.setEnabled(canStart);

    start.setOnClickListener(v -> {

        if (canStart) {

            AppTTS.stop();
            dlg.dismiss();

            if (startAction != null) {
                startAction.run();
            }

        } else {

            logWarn(gr
                    ? "Δεν πληρούνται οι συνθήκες για LAB 14"
                    : "Conditions not met for LAB 14");
        }
    });

    dlg.setOnCancelListener(d -> AppTTS.stop());
    dlg.setOnDismissListener(d -> AppTTS.stop()); // 🔥 CRITICAL

    dlg.setOnKeyListener((dialog, keyCode, event) -> {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
            event.getAction() == KeyEvent.ACTION_UP) {
            AppTTS.stop();
        }
        return false;
    });
}

// ============================================================
// LAB 14 HELPERS — PRE-TEST ADVISORY POPUP (HELPERS + AppTTS)
// ============================================================
private void showLab14PreTestAdvisory(Runnable onContinue) {

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(true);

    LinearLayout root = buildGELPopupRoot(this);

    // HEADER (TITLE ONLY)
    root.addView(
            buildPopupHeader(
                    this,
                    gr
                            ? "Δοκιμή Καταπόνησης Μπαταρίας — Προειδοποίηση"
                            : "Battery Stress Test — Pre-Test Check"
            )
    );

    final String text =
            gr
                    ? "Για μεγαλύτερη διαγνωστική ακρίβεια, συνιστάται, το τεστ "
                      + "να εκτελείται μετά από επανεκκίνηση της συσκευής.\n\n"
                      + "Μπορείς να συνεχίσεις χωρίς επανεκκίνηση, όμως, "
                      + "πρόσφατη έντονη χρήση, μπορεί να επηρεάσει τα αποτελέσματα.\n\n"
                      + "Μην χρησιμοποιήσεις τη συσκευή, για τα επόμενα 5 λεπτά."
                    : "For best diagnostic accuracy, it is recommended to run this test, "
                      + "after a system restart.\n\n"
                      + "You may continue without restarting, but recent heavy usage, "
                      + "can affect the results.\n\n"
                      + "Do not use your device for the next 5 minutes.";

    TextView msg = new TextView(this);
    msg.setText(text);
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(14.5f);
    msg.setLineSpacing(0f, 1.2f);
    root.addView(msg);

    // MUTE ROW (CHECKBOX)
    root.addView(buildMuteRow());

    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.VERTICAL);

    Button btnRestart = gelButton(
            this,
            gr ? "Έξοδος για επανεκκίνηση" : "Exit for restart",
            0xFF8B0000
    );

    Button btnContinue = gelButton(
            this,
            gr ? "Συνέχεια παρόλα αυτά"
               : "Continue anyway",
            0xFF0B5D1E
    );

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(52)
            );

    lp.setMargins(0, dp(14), 0, 0);

    btnRestart.setLayoutParams(lp);
    btnContinue.setLayoutParams(lp);

    row.addView(btnRestart);
    row.addView(btnContinue);

    root.addView(row);

    b.setView(root);

    AlertDialog dlg = b.create();

    if (dlg.getWindow() != null) {
        dlg.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    dlg.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(dlg);

    // 🔊 TTS
    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        if (dlg.isShowing() && !AppTTS.isMuted(this)) {
            AppTTS.ensureSpeak(this, text);
        }
    }, 120);

    // CONTINUE
    btnContinue.setOnClickListener(v -> {

        AppTTS.stop();

        int percent = getBatteryPercentSafe();
        float temp = getBatteryTemperature();
        float cpu = readCpuTempSafe();
        boolean charging = isDeviceCharging();

        boolean conditionsOk =
                percent >= 30 && percent <= 90
                && (Float.isNaN(temp) || temp < 39f)
                && (Float.isNaN(cpu) || cpu < 60f)
                && !charging;

        if (!conditionsOk) {

            logWarn(AppLang.isGreek(this)
                    ? "Οι συνθήκες δεν είναι κατάλληλες — το test μπλοκαρίστηκε"
                    : "Conditions not valid — test blocked");

            // reset flags
            lab14Running = false;
            lab14PopupShown = false;
            lab14AdvisoryShown = false;

            return;
        }

        dlg.dismiss();

        if (onContinue != null) {
            onContinue.run();
        }
    });

    // RESTART
    btnRestart.setOnClickListener(v -> {

        AppTTS.stop();

        try {
            dlg.dismiss();
        } catch (Throwable ignore) {}

        try {
            finishAffinity();
        } catch (Throwable ignore) {}

        try {
            System.exit(0);
        } catch (Throwable ignore) {}
    });
}

// ------------------------------------------------------------
// Brightness + keep screen on (LAB 14 stress)
// ------------------------------------------------------------

private void applyMaxBrightnessAndKeepOn() {
try {
WindowManager.LayoutParams lp = getWindow().getAttributes();

if (__oldBrightness < 0) {  
        __oldBrightness = Settings.System.getInt(  
                getContentResolver(),  
                Settings.System.SCREEN_BRIGHTNESS,  
                128  
        );  
    }  

    lp.screenBrightness = 1.0f;  
    getWindow().setAttributes(lp);  
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  

} catch (Throwable ignore) {}

}

private void restoreBrightnessAndKeepOn() {
try {
WindowManager.LayoutParams lp = getWindow().getAttributes();

if (__oldBrightness >= 0) {  
        lp.screenBrightness = __oldBrightness / 255f;  
        getWindow().setAttributes(lp);  
    }  

    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  

} catch (Throwable ignore) {}

}

// ============================================================
// LAB 14 — PARTIAL DIAGNOSIS (SYSTEM LIMITED / INVALID RUN)
// ============================================================
private void lab14LogPartialMode(
        boolean gr,
        boolean[] lab14_systemLimited,
        Lab14Engine.ConfidenceResult conf
) {

    if (!lab14_systemLimited[0] && validDrain) return;

    appendHtml("<br>");
    logLine();

    logWarn(gr
            ? "Περιορισμένη διάγνωση"
            : "Limited diagnostic mode");

    logLine();

    if (lab14_systemLimited[0]) {

        logWarn(gr
                ? "Ανιχνεύθηκε περιορισμός ρεύματος από το σύστημα (BMS / thermal / vendor limiter)."
                : "System current limiting detected (BMS / thermal / vendor limiter).");

    }

    if (!validDrain) {

        logWarn(gr
                ? "Η κατανάλωση δεν ήταν επαρκής για πλήρη ανάλυση."
                : "Drain rate not sufficient for full analysis.");

    }

    if (conf != null && conf.percent < 60) {

        logWarn(gr
                ? "Η στατιστική αξιοπιστία είναι χαμηλή."
                : "Statistical confidence is low.");

    }

    logInfo(gr
            ? "Η πλήρης εκτίμηση υγείας μπαταρίας δεν είναι διαθέσιμη σε αυτό το run."
            : "Full battery health estimation not available for this run.");

    logInfo(gr
            ? "Τα ηλεκτρικά και θερμικά ευρήματα παραμένουν ενδεικτικά."
            : "Electrical and thermal findings remain indicative.");

    logInfo(gr
            ? "Συνιστάται επανάληψη με φόρτιση 30–90%, χωρίς φόρτιση και σε φυσιολογική θερμοκρασία."
            : "Repeat test at 30–90% battery, not charging, normal temperature.");

    logLine();
}

// ------------------------------------------------------------
// GPU COMPUTE STRESS (LAB14)
// ------------------------------------------------------------
private void startGpuStress() {

    runOnUiThread(() -> {

        // 🔁 αν υπάρχει ήδη → απλά resume
        if (lab14GLView != null) {
            try { lab14GLView.onResume(); } catch (Throwable ignore) {}
            return;
        }

        lab14GLView = new GLSurfaceView(this);
        lab14GLView.setEGLContextClientVersion(2);

        lab14GpuRenderer = new Lab14GpuRenderer();
        lab14GLView.setRenderer(lab14GpuRenderer);

        lab14GLView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        FrameLayout root = findViewById(android.R.id.content);

        root.addView(
                lab14GLView,
                new FrameLayout.LayoutParams(1, 1) // 🔥 invisible αλλά GPU active
        );

        try { lab14GLView.onResume(); } catch (Throwable ignore) {}
    });
}

// ===================================================================
// LAB 14 — CONFIDENCE SCORE (%)
// Variance-based reliability indicator
// ===================================================================
private static final String LAB14_PREFS = "lab14_prefs";
private static final String KEY_LAB14_RUNS = "lab14_run_count";
private static final String KEY_LAB14_LAST_DRAIN_1 = "lab14_drain_1";
private static final String KEY_LAB14_LAST_DRAIN_2 = "lab14_drain_2";
private static final String KEY_LAB14_LAST_DRAIN_3 = "lab14_drain_3";

private void lab14LogReliabilitySummary(
        boolean gr,
        boolean[] lab14_systemLimited,
        Lab14Engine.ConfidenceResult conf
) {

    int runs = getLab14RunCount();

    appendHtml("<br>");
    
    logOk(gr
            ? "Αξιοπιστία διάγνωσης"
            : "Diagnostic reliability");

    logLine();


// =====================================================
// LIMITER DETECTED
// =====================================================

if (lab14_systemLimited[0]) {

    logLabelWarnValue(
            gr ? "Περιορισμός συστήματος"
               : "System limited",
            gr
                    ? "Εντοπίστηκε περιορισμός BMS"
                    : "BMS current limiting detected"
    );

    logWarn(gr
            ? "Η μέτρηση έγινε με περιορισμό από το σύστημα. Το αποτέλεσμα είναι ενδεικτικό."
            : "Measurement affected by system limiter. Result is indicative.");

}

    // =====================================================
    // CURRENT RUN STATUS
    // =====================================================

    boolean validRun = validDrain && !lab14_systemLimited[0];

    if (validRun) {

    logLabelOkValue(
            gr ? "Έγκυρη εκτέλεση"
               : "Valid run",
            gr ? "Ναι" : "Yes"
    );

} else {

    logLabelErrorValue(
            gr ? "Έγκυρη εκτέλεση"
               : "Valid run",
            gr ? "Όχι" : "No"
    );

}

// =====================================================
// LIMITER
// =====================================================

if (lab14_systemLimited[0]) {

    logLabelWarnValue(
            gr ? "Περιορισμός συστήματος"
               : "System limited",
            gr
                    ? "Το BMS περιόρισε την κατανάλωση"
                    : "BMS current limiting detected"
    );

    logWarn(gr
            ? "Η μέτρηση έγινε με περιορισμό από το σύστημα. Το αποτέλεσμα είναι ενδεικτικό."
            : "System limiter detected. Result is indicative.");

}

// =====================================================
// STORED RUNS (always show)
// =====================================================

logLabelOkValue(
        gr ? "Καταγεγραμμένες έγκυρες εκτελέσεις"
           : "Stored valid runs",
        String.valueOf(runs)
);

    // =====================================================
    // CONSISTENCY
    // =====================================================

    int consistency = -1;
int validRunsForConsistency = 0;
Lab14Engine.ConfidenceTier tier = null;

if (conf != null) {
    consistency = conf.percent;
    validRunsForConsistency = conf.validRuns;
    tier = conf.tier;
}

    // =====================================================
// CONSISTENCY / TIER (only if >=2 runs)
// =====================================================

if (consistency >= 0 && validRunsForConsistency >= 2) {

    String tierLabel = "";

    if (tier != null) {

        switch (tier) {

            case PRELIMINARY:
                tierLabel = gr ? "Προκαταρκτική" : "Preliminary";
                break;

            case MEDIUM:
                tierLabel = gr ? "Μεσαία" : "Medium";
                break;

            case HIGH:
                tierLabel = gr ? "Υψηλή" : "High";
                break;

            default:
                tierLabel = gr ? "Άγνωστη" : "Unknown";
        }
    }

}

logLine();


// =====================================================
// CURRENT RUN INVALID
// =====================================================

if (!validRun) {

    logLabelWarnValue(
            gr ? "Εμπιστοσύνη" : "Confidence",
            gr
                    ? "Ενδεικτική (τρέχουσα εκτέλεση μη έγκυρη)"
                    : "Indicative (current run not valid)"
    );

    logWarn(gr
            ? "Η τρέχουσα εκτέλεση δεν καταχωρήθηκε ως έγκυρη."
            : "Current run not valid.");

    if (runs < 3) {

        logWarn(gr
                ? "Απαιτούνται 3 έγκυρες εκτελέσεις."
                : "3 valid runs required.");

    }

    return;
}


// =====================================================
// SKIP RUN COUNT IF CURRENT RUN LIMITED
// =====================================================

if (lab14_systemLimited[0]) {

    logWarn(gr
            ? "Η εκτέλεση επηρεάστηκε από limiter."
            : "Run affected by limiter.");
}


// =====================================================
// RUN COUNT CONFIDENCE
// =====================================================

if (runs <= 0) {

    logLabelWarnValue(
            gr ? "Εμπιστοσύνη" : "Confidence",
            gr ? "Δεν υπάρχει ακόμη έγκυρη εκτέλεση"
               : "No valid run yet"
    );

    logWarn(gr
            ? "Απαιτούνται 3 έγκυρες εκτελέσεις, σε διαφορετικές ημέρες με παρόμοιες συνθήκες."
            : "3 valid runs required, on different days under similar conditions.");

    return;
}


else if (runs == 1) {

    logLabelWarnValue(
            gr ? "Εμπιστοσύνη" : "Confidence",
            gr ? "Προκαταρκτική (1 έγκυρη εκτέλεση)"
               : "Preliminary (1 valid run)"
    );

    logWarn(gr
            ? "Απαιτούνται ακόμα 2 έγκυρες εκτελέσεις, σε διαφορετικές ημέρες με παρόμοιες συνθήκες."
            : "2 more valid runs required, on different days under similar conditions.");

    return;
}


else if (runs == 2) {

    logLabelWarnValue(
            gr ? "Εμπιστοσύνη" : "Confidence",
            gr ? "Μεσαία (2 έγκυρες εκτελέσεις)"
               : "Medium (2 valid runs)"
    );

    logWarn(gr
            ? "Απαιτείται 1 ακόμα έγκυρη εκτελεση, σε διαφορετική ημέρα με παρόμοιες συνθήκες."
            : "1 more valid run required, on different day under similar conditions.");

    return;
}


// =====================================================
// CONSISTENCY INFO
// =====================================================

if (consistency >= 70) {

    logLabelOkValue(
            gr ? "Πληροφορία" : "Info",
            gr ? "Οι μετρήσεις είναι συνεπείς"
               : "Measurements are consistent"
    );

    logOk(gr
            ? "Οι εκτελέσεις έγιναν σε παρόμοιες συνθήκες."
            : "Runs were performed under similar conditions.");

}
else if (consistency >= 50) {

    logLabelOkValue(
            gr ? "Πληροφορία" : "Info",
            gr ? "Υπάρχουν μικρές διαφορές μεταξύ εκτελέσεων"
               : "Minor variation between runs"
    );

    logOk(gr
            ? "Μικρές αποκλίσεις θεωρούνται φυσιολογικές."
            : "Small variations are normal.");

}
else {

    logLabelWarnValue(
            gr ? "Πληροφορία" : "Info",
            gr ? "Μεγάλες αποκλίσεις μεταξύ εκτελέσεων"
               : "Large deviation between runs"
    );

    logWarn(gr
            ? "Οι εκτελέσεις έγιναν σε διαφορετικές συνθήκες με μεγάλες αποκλίσεις."
            : "Runs were performed under different conditions with large deviation.");

}
}

private int getLab14RunCount() {

    try {

        return getSharedPreferences(
                LAB14_PREFS,
                MODE_PRIVATE
        ).getInt(KEY_LAB14_RUNS, 0);

    } catch (Throwable ignore) {

        return 0;

    }
}

// ------------------------------------------------------------
// CPU / GPU thermal helpers (SAFE, READ-ONLY)
// ------------------------------------------------------------
private Float readCpuTempSafe() {

    try {

        Map<String, Float> zones = readThermalZones();

        Float t = pickZone(
                zones,
                "cpu",
                "soc",
                "ap",
                "cluster",
                "little",
                "big"
        );

        if (t == null) return null;

        // reject impossible values
        if (t < 10f || t > 90f) return null;

        return t;

    } catch (Throwable ignore) {}

    // 🔴 FALLBACK — activates sysfs multi-zone CPU reader
    try {
        float alt = readCpuTempSafe2();

        if (!Float.isNaN(alt) &&
            alt >= 10f &&
            alt <= 110f) {

            return alt;
        }

    } catch (Throwable ignore) {}

    return null;
}

private Float readGpuTempSafe() {

    try {

        Map<String, Float> zones = readThermalZones();

        Float t = pickZone(
                zones,
                "gpu",
                "kgsl",
                "gfx"
        );

        if (t == null) return null;

        if (t < 10f || t > 90f) return null;

        return t;

    } catch (Throwable ignore) {}

    return null;
}

// ------------------------------------------------------------
// CPU stress (controlled) — used by LAB 14/17
// ------------------------------------------------------------

// ============================================================
// 🔥 CPU BURN C_MODE (STABLE + CONTROLLED)
// ============================================================

private final List<Thread> cpuThreads = new ArrayList<>();

private void startCpuBurn_C_Mode() {

    stopCpuBurn();

    cpuBurnRunning = true;

    final int maxCores =
            Runtime.getRuntime().availableProcessors();

    lab14CpuThreadsCurrent = maxCores;

    for (int i = 0; i < maxCores; i++) {

        Thread t = new Thread(() -> {

            try {

                long spin = 0;

                while (cpuBurnRunning &&
                       lab14Running &&
                       !lab14Cancelled &&
                       !Thread.currentThread().isInterrupted()) {

                    // 🔥 CHAOTIC MIXED LOAD
                    for (int j = 1; j < 40000; j++) {

                        double a = Math.sqrt(j * 13.37);
                        double b = Math.sin(j);
                        double c = Math.cos(j * 0.5);
                        double d = Math.tan((j % 45) + 1);

                        spin += (long)
                                ((a * b * c) + d);

                        // 🔴 memory pressure
                        if ((j & 255) == 0) {

                            byte[] tmp = new byte[32 * 1024];

                            tmp[0] = (byte) j;
                            tmp[tmp.length - 1] = (byte) spin;
                        }
                    }

                    // 🔴 anti optimizer
                    if (spin > Long.MAX_VALUE / 4) {
                        spin = 0;
                    }

                    // 🔴 micro burst jitter
                    if ((spin & 7) == 0) {
                        Thread.yield();
                    }
                }

            } catch (Throwable ignore) {}

        }, "LAB14_CMODE_" + i);

        t.setPriority(Thread.MAX_PRIORITY);

        cpuThreads.add(t);
        t.start();
    }
}

private void startMainStressPhase(
        int durationSec,
        long t0,
        TextView dotsView,
        TextView counterText,
        LinearLayout progressBar
) {

    // αυτή η μέθοδος υπάρχει μόνο για να καλέσει
    // το ήδη υπάρχον main stress logic

    // το πραγματικό stress τρέχει στο UI handler
    // που έχεις ήδη παρακάτω

}

// ============================================================
// MEMORY BANDWIDTH STRESS (LOW HEAT LOAD)
// ============================================================
private Thread memStressThread;

private void startMemoryStress() {

    final boolean gr = AppLang.isGreek(this);

    memStressThread = new Thread(() -> {

        try {

            // 🔴 bandwidth-oriented load (cleaner than Random)
            final int size = 24 * 1024 * 1024; // 24MB λίγο πιο δυνατό
            final byte[] buf1 = new byte[size];
            final byte[] buf2 = new byte[size];

            // deterministic init (όχι Random CPU noise)
            for (int i = 0; i < size; i++) {
                buf1[i] = (byte)(i ^ 0x5A);
            }

            while (lab14Running &&
                   !lab14Cancelled &&
                   !Thread.currentThread().isInterrupted()) {

                // charging guard
                if (isCharging()) {

                    runOnUiThread(() -> {

                        logLine();

                        logError(gr
                                ? "Ανιχνεύθηκε φόρτιση κατά τη διάρκεια της δοκιμής."
                                : "Charging detected during test.");

                        logWarn(gr
                                ? "Αποσύνδεσε τον φορτιστή και εκτέλεσε το τεστ από την αρχή."
                                : "Disconnect charger and run the test again.");

                        logLine();
                    });

                    return;
                }

                // ------------------------------------------------
                // WRITE (stride to pressure memory bus)
                // ------------------------------------------------
                for (int i = 0; i < size; i += 64) {
                    buf1[i] ^= (byte)(i + 31);
                }

                // ------------------------------------------------
                // COPY (main bandwidth load)
                // ------------------------------------------------
                System.arraycopy(buf1, 0, buf2, 0, size);

                // ------------------------------------------------
                // READ (cache-busting stride)
                // ------------------------------------------------
                long checksum = 0;

                for (int i = 0; i < size; i += 64) {
                    checksum += buf2[i];
                }

                // anti-optimization
                if (checksum == Long.MIN_VALUE) {
                    if (DEBUG_MODE) appendLog("MEM","keep alive");
                }

                // tiny yield → avoids ugly scheduler monopolizing
                Thread.yield();
            }

        } catch (Throwable ignore) {}

    }, "LAB14_MEM_STRESS");

    // not MAX → less fake thermal inflation
    memStressThread.setPriority(Thread.NORM_PRIORITY);

    memStressThread.start();
}

private void stopMemoryStress() {

    try {
        if (memStressThread != null) {
            memStressThread.interrupt();
            memStressThread = null;
        }
    } catch (Throwable ignore) {}

}

// ============================================================
// BATTERY CURRENT HELPER
// ============================================================
private float getBatteryCurrentNowSafe() {

    try {

        iDoctorEngine eng = iDoctorEngine.get(this);

        float ma = eng.getBatteryCurrentNowUnified();

        if (!Float.isNaN(ma))
            return ma;

    } catch (Throwable ignore) {}

    // fallback μόνο αν engine δεν δώσει τιμή

    try {

        BatteryManager bm =
                (BatteryManager) getSystemService(BATTERY_SERVICE);

        if (bm == null)
            return Float.NaN;

        long raw =
                bm.getLongProperty(
                        BatteryManager.BATTERY_PROPERTY_CURRENT_NOW
                );

        if (raw == Long.MIN_VALUE || raw == 0L)
            return Float.NaN;

        return (float) raw;   // µA

    } catch (Throwable ignore) {

        return Float.NaN;
    }
}

// ---------------- LAB 14 ----------------
private float getLastLab14HealthScore() {

    try {

        SharedPreferences p =
                getSharedPreferences(
                        LAB14_PREFS,
                        MODE_PRIVATE
                );

        return p.getFloat(
                "lab14_health_score",
                -1f
        );

    } catch (Throwable t) {

        return -1f;
    }
}

private int getLastLab14AgingIndex() {
try {

return p.getInt("lab14_aging_index", -1);
} catch (Throwable t) {
return -1;
}

}

private boolean hasValidLab14() {
return getLastLab14HealthScore() >= 0;
}


private boolean ensurePermissions(String[] permissions, Runnable afterGranted) {

    List<String> missing = new ArrayList<>();

    for (String p : permissions) {
        if (ContextCompat.checkSelfPermission(this, p)
                != PackageManager.PERMISSION_GRANTED) {
            missing.add(p);
        }
    }

    if (missing.isEmpty()) {
        return true;
    }

    pendingAfterPermission = afterGranted;

    ActivityCompat.requestPermissions(
            this,
            missing.toArray(new String[0]),
            REQ_CORE_PERMS
    );

    return false;
}

// ============================================================
// LAB 14B — CONDITIONS CHECK (STRAITJACKET MODE)
// ============================================================
private boolean checkLab14BConditions() {

    final boolean gr = AppLang.isGreek(this);

    int percent = (int) getCurrentBatteryPercent();

    boolean chargingNow = false;
    boolean airplaneMode = false;

    try {

        chargingNow = isDeviceCharging();

        airplaneMode =
                Settings.Global.getInt(
                        getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON,
                        0
                ) == 1;

    } catch (Throwable ignore) {}

    appendHtml("<br>");
    logLine();

    logInfo(gr
            ? "LAB 14B - Έλεγχος συνθηκών δοκιμής"
            : "LAB 14B - Test conditions check");

    logLine();

    // ------------------------------------------------------------
    // CHARGING (HARD REQUIREMENT)
    // ------------------------------------------------------------
    if (chargingNow) {

        logWarn(gr
                ? "Η συσκευή φορτίζει."
                : "Device is charging.");

        logWarn(gr
                ? "Αποσυνδέστε τον φορτιστή και δοκιμάστε ξανά."
                : "Disconnect charger and retry.");

        return false;
    }

    // ------------------------------------------------------------
    // BATTERY LEVEL
    // ------------------------------------------------------------
    logLabelValue(
            gr ? "Επίπεδο μπαταρίας" : "Battery level",
            percent + "%"
    );

    if (percent < 30) {

        logWarn(gr
                ? "Το LAB 14B απαιτεί τουλάχιστον 30% μπαταρία."
                : "LAB 14B requires at least 30% battery.");

        logWarn(gr
                ? "Κάτω από αυτό το επίπεδο μπορεί να επέμβει system protection."
                : "Below this level system protection may interfere.");

        return false;
    }

// ------------------------------------------------------------
// AIRPLANE MODE
// ------------------------------------------------------------
logLabelValue(
        "Airplane mode",
        airplaneMode ? "ON" : "OFF"
);

    if (!airplaneMode) {

    showAirplaneModePrompt();

    return false;
}

    // ------------------------------------------------------------
    // PASS
    // ------------------------------------------------------------
    logOk(gr
            ? "Οι συνθήκες είναι σταθερές. Έναρξη δοκιμής."
            : "Conditions stable. Starting test.");

    logLine();

    return true;
}

// ============================================================
// LAB 14B — battery duration estimation
// ============================================================
private void lab14BBatteryDurationTest() {
    runLab14DurationMode(false);
}

// ============================================================
// LAB 14 GAMA
// ============================================================
private void lab14GamingDurationTest() {
    runLab14DurationMode(true);
}

private void runLab14DurationMode(boolean gamaMode) {

    final boolean gr = AppLang.isGreek(this);
    
     boolean wasGama = gamaMode;
    
// ------------------------------------------------------------
// 🔴 HARD RESET BEFORE START (CRITICAL FIX — FINAL)
// ------------------------------------------------------------
synchronized (lab14Lock) {

    // 🔴 kill previous session
    lab14ForceStop = true;   // 🔴 kill old threads
    lab14SessionActive = false;
    lab14SessionId++;

    try { stopCpuBurn(); } catch (Throwable ignore) {}
    try { stopMemoryStress(); } catch (Throwable ignore) {}
    try { stopGpuStress(); } catch (Throwable ignore) {}
    try { stopMemoryBandwidthStress(); } catch (Throwable ignore) {}
    try { stopVibrationStress(); } catch (Throwable ignore) {}

    try {
        if (usageHandler != null) {
            usageHandler.removeCallbacksAndMessages(null);
            usageHandler = null;
        }
    } catch (Throwable ignore) {}

    try {
        if (lab14Dialog != null && lab14Dialog.isShowing()) {
            lab14Dialog.dismiss();
            lab14Dialog = null;
        }
    } catch (Throwable ignore) {}

    try {
        if (batterySuiteDialog != null && batterySuiteDialog.isShowing()) {
            batterySuiteDialog.dismiss();
            batterySuiteDialog = null;
        }
    } catch (Throwable ignore) {}

    lab14Running = false;
    lab14Cancelled = false;
    isLab14BMode = false;
    isLab14GamaMode = false;
}

isLab14BMode = !gamaMode;
isLab14GamaMode = gamaMode;

// ------------------------------------------------------------
// 🔴 START FLOW
// ------------------------------------------------------------
showLab14BAdvisory(() -> {

    appendHtml("<br>");

    // 🔴 new session id (IMPORTANT)
    final int mySessionId;

synchronized (lab14Lock) {

    lab14ForceStop = false;   // 🔴 allow new threads

    lab14SessionActive = true;
    lab14SessionId++;

    mySessionId = lab14SessionId;
}

        final iDoctorEngine idoctor =
                iDoctorEngine.get(ManualTestsActivity.this);

        final long[] startMah = new long[]{ -1L };
        final long[] endMah = new long[]{ -1L };

        final long[] baselineMah = new long[]{-1L};

        final float[] startVolt = new float[]{ Float.NaN };
        final float[] endVolt = new float[]{ Float.NaN };

        final float[] startTemp = new float[]{ Float.NaN };
        final float[] endTemp = new float[]{ Float.NaN };

        iDoctorEngine.BatterySnapshot snap0 =
                idoctor.readBatterySnapshotLab();

        if (snap0 == null || snap0.chargeNowMah <= 0) {

            logError(gr
                    ? "Αποτυχία αρχικής ανάγνωσης μπαταρίας"
                    : "Initial battery snapshot failed");

            lab14Running = false;
            isLab14BMode = false;
            return;
        }

startMah[0] = snap0.chargeNowMah;

// ------------------------------------------------
// VERIFIED FULL CAPACITY — UNKNOWN UNDER PUBLIC SDK
// ------------------------------------------------
// Remaining charge divided by SOC is NOT an independent full-charge capacity.
lab14bBaselineMah = -1L;
baselineMah[0] = -1L;
logLabelWarnValue(gr ? "Πλήρης χωρητικότητα" : "Full-charge capacity",
        gr ? "Δεν επαληθεύεται από δημόσιο Android API"
           : "Not verifiable from public Android APIs");

// ------------------------------------------------
// // 🔴 START TEMP (LOCK SOURCE)
// ------------------------------------------------
float tApi = getBatteryTemperature();
float tSnap = snap0 != null ? snap0.batteryTempC : Float.NaN;

if (!Float.isNaN(tSnap) && tSnap > 0f) {

    lab14UseSnapshotTemp = true;
    startTemp[0] = tSnap;

} else {

    lab14UseSnapshotTemp = false;
    startTemp[0] = tApi;
}

// ------------------------------------------------
// 🔴 START VOLT (OK όπως είναι)
// ------------------------------------------------
startVolt[0] = getBatteryVoltageFiltered();

isLab14BMode = !gamaMode;
isLab14GamaMode = gamaMode;
lab14Cancelled = false;
lab14Running = true;

// 🔴 FORCE SOFT PROFILE (CRITICAL)
lab14BoostActive = false;
lab14SoftPhaseStarted = true;
inHardPhase = false;
lab14FastPhase = false;
lab14MainPhase = false;

// 🔴 RESET RATE AVERAGE
rateSum = 0;
rateSamples = 0;

// ---------------------------------------------
// 🔴 SPIKE 
// ---------------------------------------------

lab14SpikeMah = 0f;
lab14PrevCurrent = Float.NaN;
lab14CurrentEMA = Float.NaN;
lab14SpikeLastTs = 0L;
lab14SpikeCount = 0;
lab14MaxSpikeExcess = 0f;

// ---------------------------------------------

startLab14DurationPopup(
    300,
    gamaMode
);

logLine();

logOk(
    gr
    ? (wasGama
        ? "LAB 14 GAMA ξεκίνησε"
        : "LAB 14B ξεκίνησε")
    : (wasGama
        ? "LAB 14 GAMA started"
        : "LAB 14B started")
);

logLabelValue(
    gr ? "Λειτουργία" : "Mode",
    gr
    ? (wasGama
        ? "Προσομοίωση gaming χρήσης (5 λεπτά)"
        : "Προσομοίωση καθημερινής χρήσης (5 λεπτά)")
    : (wasGama
        ? "Gaming usage simulation (5 minutes)"
        : "Daily usage simulation (5 minutes)")
);

try {
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
} catch (Throwable ignore) {}

// --------------------------------------------------------
// 🌿 REAL DAILY-USAGE LOAD (LOCKED SOFT PROFILE)
// --------------------------------------------------------

// ποτέ full burn στο 14B
try { stopCpuBurn(); } catch (Throwable ignore) {}
try { stopMemoryStress(); } catch (Throwable ignore) {}
try { stopGpuStress(); } catch (Throwable ignore) {}
try { stopMemoryBandwidthStress(); } catch (Throwable ignore) {}
try { stopVibrationStress(); } catch (Throwable ignore) {}

// --------------------------------------------------------
// 🎬 VIDEO SETUP (CONTINUOUS LIGHT BACKGROUND LOAD)
// --------------------------------------------------------
try {

    if (lab14StressVideo != null) {

        lab14StressVideo.setVideoURI(
                Uri.parse(
                        "android.resource://"
                                + getPackageName()
                                + "/"
                                + R.raw.battery_stress_loop
                )
        );

        lab14StressVideo.setOnPreparedListener(mp -> {

            mp.setLooping(true);
            mp.setVolume(0f,0f);

            try {
                lab14StressVideo.start();
            } catch (Throwable ignore) {}

        });
    }

} catch (Throwable ignore) {}

// --------------------------------------------------------
// 🌿 HUMAN USAGE SIMULATION (CONTROLLED REAL USE)
// --------------------------------------------------------

usageHandler = new Handler(Looper.getMainLooper());

final long startTs = SystemClock.elapsedRealtime();

final Runnable usageLoop = new Runnable() {
    @Override
    public void run() {

        if (!lab14SessionActive || mySessionId != lab14SessionId) {
            return;
        }

        if (!lab14Running || lab14Cancelled) return;

        long elapsed =
                SystemClock.elapsedRealtime() - startTs;

        long nextDelay = 500;

        // 🔴 restore CPU burst mode

// 🔴 GAMING DETERMINISTIC LOAD (NO RANDOM)
try {

    long cycle = (elapsed / 1000) % 20; // 20s loop

    if (isLab14GamaMode) {

        // ------------------------------------------------
        // 🎮 REALISTIC GAMING PROFILE
        // ------------------------------------------------
        nextDelay = 300; // tighter loop for gaming load

        if (cycle < 5) {

            // GPU-heavy render phase
            simulateGpuBurst();
            simulateGpuBurst();
            simulateGpuBurst();

        } else if (cycle < 10) {

            // CPU + GPU combined load
            simulateShortCpuBurst();
            simulateShortCpuBurst(); // 🔴 BOOST CPU
            simulateGpuBurst();
            simulateGpuBurst();
            simulateGpuBurst();

        } else if (cycle < 15) {

            // sustained CPU pressure
            simulateShortCpuBurst();
            simulateShortCpuBurst();
            simulateShortCpuBurst();
            simulateGpuBurst();
            simulateGpuBurst();

        } else {

            // short cooldown, but not idle
            simulateGpuBurst();
        }

    } else {

        // ------------------------------------------------
        // 🌿 DAILY PROFILE
        // ------------------------------------------------
        nextDelay = 500;

        if (cycle < 5) {

            // idle

        } else if (cycle < 9) {

            simulateUiInteraction();

        } else if (cycle < 13) {

            simulateShortCpuBurst();

        } else if (cycle < 17) {

            simulateUiInteraction();
            simulateShortCpuBurst();

        } else {

            // idle
        }
    }

} catch (Throwable ignore) {}
        
// 🔴 LIVE VOLTAGE SAMPLE (SMART DETECTION)
float vNow = getBatteryVoltageFiltered();

if (!Float.isNaN(vNow) && vNow > 0f) {

    if (Float.isNaN(vStart[0])) {
        vStart[0] = vNow;
    }

    float dropFromStart =
            Float.isNaN(vStart[0]) ? 0f : (vStart[0] - vNow);

    if (Float.isNaN(vLoad1[0]) &&
        dropFromStart > 0.015f) {

        vLoad1[0] = vNow;
    }

    if (Float.isNaN(voltageUnderLoad[0]) &&
        !Float.isNaN(vLoad1[0]) &&
        dropFromStart > 0.030f) {

        voltageUnderLoad[0] = vNow;
    }

    if (Float.isNaN(vLoad2[0]) &&
        !Float.isNaN(voltageUnderLoad[0]) &&
        dropFromStart > 0.045f) {

        vLoad2[0] = vNow;
    }
}

// ---------------------------------------------
// 🔴 SPIKE DISTURBANCE DETECTOR
// ---------------------------------------------
try {

    float iNow =
            (float) Math.abs(lab14Current());

    long ts =
            SystemClock.elapsedRealtime();

    if (!Float.isNaN(iNow) && iNow > 50f) {

        if (Float.isNaN(lab14CurrentEMA)) {
            lab14CurrentEMA = iNow;
        }

        // EMA baseline
        lab14CurrentEMA =
                0.90f * lab14CurrentEMA +
                0.10f * iNow;

        if (lab14SpikeLastTs > 0) {

            float dtSec =
                    (ts - lab14SpikeLastTs) / 1000f;

            float spikeExcess =
                    iNow - lab14CurrentEMA;

            // 🔴 dynamic disturbance detection
            boolean mildSpike =
                    spikeExcess > 250f &&
                    iNow > lab14CurrentEMA * 1.50f;

            boolean strongSpike =
                    spikeExcess > 500f &&
                    iNow > lab14CurrentEMA * 1.80f;

            if (mildSpike || strongSpike) {

                lab14SpikeCount++;

                if (spikeExcess > lab14MaxSpikeExcess) {
                    lab14MaxSpikeExcess = spikeExcess;
                }

                lab14SpikeMah +=
                        (spikeExcess * dtSec) / 3600f;
            }
        }

        lab14SpikeLastTs = ts;
        lab14PrevCurrent = iNow;
    }

} catch (Throwable ignore) {}

// ---------------------------------------------

usageHandler.postDelayed(this, nextDelay);
    }
};

// start continuous video baseline
try {
    if (lab14StressVideo != null) {
        lab14StressVideo.start();
    }
} catch (Throwable ignore) {}

// start loop
usageHandler.post(usageLoop);

// --------------------------------------------------------
// AFTER 300s -> FINAL SNAPSHOT + ANALYSIS
// --------------------------------------------------------

new Handler(Looper.getMainLooper()).postDelayed(() -> {

    try {
        if (!lab14SessionActive || mySessionId != lab14SessionId || lab14Cancelled) {
            logWarn(gr ? "Το Lab 14B ακυρώθηκε ή αντικαταστάθηκε"
                       : "Lab 14B cancelled or superseded");
            return;
        }
        stopFastStressSafe();

        iDoctorEngine.BatterySnapshot snapEnd =
                idoctor.readBatterySnapshotLab();

        if (snapEnd == null || snapEnd.chargeNowMah <= 0) {

            logError(gr
                    ? "Αποτυχία τελικής ανάγνωσης"
                    : "Final snapshot failed");

            return;
        }
        endMah[0] = snapEnd.chargeNowMah;
        final long measuredElapsedMs = SystemClock.elapsedRealtime() - startTs;

// ------------------------------------------------
// 🔴 TEMPERATURE (FUSION - FINAL)
// ------------------------------------------------
float temp;

if (lab14UseSnapshotTemp) {
    temp = snapEnd.batteryTempC;
} else {
    temp = getBatteryTemperature();
}

// fallback
if (Float.isNaN(temp) || temp <= 0f) {
    temp = snapEnd.batteryTempC;
}

endTemp[0] = temp;

// ------------------------------------------------
// 🔴 VOLTAGE (FUSION - FINAL, NO SNAPSHOT)
// ------------------------------------------------
float volt = readVoltMulti(snapEnd);

// fallback (no snapshot voltage)
if (Float.isNaN(volt) || volt <= 0f) {
    volt = getBatteryVoltageFiltered();
}

// stuck sensor detection
if (!Float.isNaN(startVolt[0]) &&
    Math.abs(volt - startVolt[0]) < 0.002f) {

    float sysVolt = readSysVolt();

    if (!Float.isNaN(sysVolt) && sysVolt > 0f) {
        volt = sysVolt;
    }
}

endVolt[0] = volt;

// ------------------------------------------------
// 🔴 DEBUG
// ------------------------------------------------
logLabelValue("Start Temp", String.valueOf(startTemp[0]));
logLabelValue("End Temp", String.valueOf(endTemp[0]));

logLabelValue("Start Volt", String.valueOf(startVolt[0]));
logLabelValue("End Volt", String.valueOf(endVolt[0]));

// ------------------------------------------------
// REAL DISCHARGE DELTA / MONOTONIC DURATION
// ------------------------------------------------
// Reject missing/stationary/charging counters; never substitute a heuristic.
final double observedRate = BatteryDrainEvidence.dischargeMilliAmpHoursPerHour(
        startMah[0], endMah[0], measuredElapsedMs, snap0.charging, snapEnd.charging);
boolean fakeCounter = Double.isNaN(observedRate);
float perHour = fakeCounter ? Float.NaN : (float) observedRate;
float correctedPerHour = perHour; // historical local name; no arbitrary correction
float estimatedHours = Float.NaN;
logLabelValue(gr ? "Διάρκεια δείγματος" : "Sample duration",
        String.format(Locale.US, "%.1f min", measuredElapsedMs / 60000.0d));
if (fakeCounter) {
    logLabelWarnValue(gr ? "Ρυθμός εκφόρτισης" : "Discharge rate",
            gr ? "Μη διαθέσιμος: άγνωστη ή μη έγκυρη μεταβολή φορτίου"
               : "Unavailable: missing or invalid counter delta");
} else {
    logLabelValue(gr ? "Μεταβολή φορτίου" : "Charge counter delta",
            (startMah[0] - endMah[0]) + " mAh");
    logLabelValue(gr ? "Μέση κατανάλωση στο συγκεκριμένο τεστ" : "Average draw in this test",
            String.format(Locale.US, "%.1f mAh/h", observedRate));
}

// ------------------------------------------------
// 🔴 THERMAL / VOLTAGE (FINAL CLEAN VERSION)
// ------------------------------------------------
float tempRise = Float.NaN;
float voltDrop = Float.NaN;

// ------------------------
// 🔴 TEMPERATURE
// ------------------------
if (!Float.isNaN(startTemp[0]) && !Float.isNaN(endTemp[0])) {

    tempRise = endTemp[0] - startTemp[0];

    // clamp negatives (sensor glitch / refresh lag)
    if (tempRise < 0f) {
        tempRise = 0f;
    }

    // ignore tiny sensor noise
    if (Math.abs(tempRise) < 0.1f) {
        tempRise = 0f;
    }

    // 🔴 extra safety: unrealistic jump protection
    if (tempRise > 15f) {
        tempRise = 15f;
    }
}

// ------------------------
// 🔴 VOLTAGE
// ------------------------
if (!Float.isNaN(startVolt[0]) && !Float.isNaN(endVolt[0])) {

    voltDrop = Math.abs(startVolt[0] - endVolt[0]);

    // ignore ADC / rounding noise
    if (voltDrop < 0.005f) {
        voltDrop = 0f;
    }

    // 🔴 clamp unrealistic spikes
    if (voltDrop > 0.5f) {
        voltDrop = 0.5f;
    }
}

appendHtml("<br>");

logOk(gr
        ? "Αποτελέσματα κατανάλωσης"
        : "Battery usage results");

logLine();

if (fakeCounter) {

    logWarn(gr
        ? "Ο αισθητήρας charge counter φαίνεται μη αξιόπιστος — γίνεται χρήση εκτίμησης βάσει ρεύματος"
        : "Charge counter appears unreliable — switched to current-based estimation");
}

// 🔴 SAFE OUTPUT
if (!Float.isNaN(perHour)) {

    logLabelValue(
            gr ? "Κατανάλωση" : "Consumption",
            String.format(Locale.US, "%.0f mAh/h", perHour)
    );

    // ----------------------------------------
    // SPIKE / RUN STABILITY
    // ----------------------------------------
    if (lab14SpikeMah > 0.8f) {

        logLabelValue(
                gr
                ? "Έξτρα κατανάλωση από απότομα φορτία"
                : "Extra consumption from load spikes",
                String.format(Locale.US, "%.1f mAh", lab14SpikeMah)
        );

        logWarn(gr
                ? "Παρατηρήθηκαν απότομα φορτία..."
                : "Transient load spikes detected...");

    } else {

        logOk(gr
                ? "Δεν ανιχνεύθηκαν απότομα φορτία."
                : "No spikes detected.");
    }

} else {

    logWarn(
        gr
        ? "Αδυναμία υπολογισμού κατανάλωσης"
        : "Unable to calculate consumption"
    );
}

// 🔴 THERMAL
if (!Float.isNaN(tempRise)) {

    String tempStr =
            (tempRise < 0.1f)
            ? "≈0.0°C"
            : String.format(Locale.US, "%.1f°C", tempRise);

    logLabelValue(
            gr ? "Άνοδος θερμοκρασίας" : "Temperature rise",
            tempStr
    );
}

// 🔴 VOLTAGE
if (!Float.isNaN(voltDrop)) {
    logLabelValue(
            gr ? "Πτώση τάσης" : "Voltage drop",
            String.format(Locale.US, "%.3f V", voltDrop)
    );
}

appendHtml("<br>");

logOk(gr
        ? "Εκτίμηση πλήρους διάρκειας μπαταρίας"
        : "Estimated full battery duration");

logLine();

float finalHours = Float.NaN;
estimatedHours = Float.NaN;
logLabelWarnValue(
        gr ? "Διάρκεια από πλήρη φόρτιση" : "Full-charge duration",
        gr ? "ΜΗ ΔΙΑΘΕΣΙΜΗ: λείπει επαληθευμένη πλήρης χωρητικότητα"
           : "UNAVAILABLE: verified full-charge capacity missing");
BatteryEvidence osPrediction = BatteryPublicCollector.collect(
        ManualTestsActivity.this).get("os_estimated_discharge_time_ms");
if (osPrediction.isObserved() && osPrediction.value > 0) {
    logLabelValue(gr ? "Πρόβλεψη υπολοίπου Android" : "Android remaining-time estimate",
            String.format(Locale.US, "%.1f h (OS estimate)",
                    osPrediction.value / 3600000.0d));
} else {
    logLabelValue(gr ? "Πρόβλεψη Android" : "Android prediction",
            gr ? "Μη διαθέσιμη" : "Unavailable");
}

appendHtml("<br>");

logOk(
        wasGama
        ? (gr ? "Σενάρια διάρκειας gaming"
              : "Gaming endurance scenarios")
        : (gr ? "Σενάρια διάρκειας χρήσης"
              : "Usage endurance scenarios")
);

logLine();

// ------------------------------------------------------------
// 🔴 USAGE / GAMING SCENARIOS
// ------------------------------------------------------------
String labelLight =
        wasGama
        ? (gr ? "Ελαφρύ gaming" : "Light gaming")
        : (gr ? "Με ελαφριά χρήση" : "Under light usage");

String labelNormal =
        wasGama
        ? (gr ? "Τυπικό gaming" : "Typical gaming")
        : (gr ? "Με κανονική χρήση" : "Under normal usage");

String labelHeavy =
        wasGama
        ? (gr ? "Βαρύ 3D gaming" : "Heavy 3D gaming")
        : (gr ? "Με βαριά χρήση" : "Under heavy usage");

// ------------------------------------------------------------
// 🔴 USAGE / GAMING MULTIPLIERS (BALANCED PHYSICS)
// ------------------------------------------------------------
float lightMul;
float normalMul = 1.00f;
float heavyMul;

if (isLab14GamaMode) {
    // Gaming = πιο στενό εύρος (βαριά χρήση by default)
    lightMul = 1.15f;   // ελαφρύ gaming (menus, idle scenes)
    heavyMul = 0.70f;   // βαρύ 3D (GPU stress)
} else {
    // Daily = μεγαλύτερο εύρος χρήσης
    lightMul = 1.40f;   // standby / light usage
    heavyMul = 0.60f;   // heavy usage (apps, multitasking)
}

// ------------------------------------------------------------
// 🔴 RESULTS INIT (FROM CONSUMPTION)
// ------------------------------------------------------------
float lightUsage = Float.NaN;
float normalUsage = Float.NaN;
float heavyUsage = Float.NaN;

// 🔴 BASE HOURS (FINAL SOURCE)
float baseHours = finalHours;

// clamp (optional but recommended)
if (!Float.isNaN(baseHours)) {
    baseHours = Math.max(0.5f, Math.min(48f, baseHours));
}

if (!Float.isNaN(baseHours) && baseHours > 0f) {

    lightUsage = baseHours * lightMul;
    normalUsage = baseHours * normalMul;
    heavyUsage = baseHours * heavyMul;

    logLabelValue(labelLight,
            String.format(Locale.US, "%.1f %s",
                    lightUsage, gr ? "ώρες" : "hours"));

    logLabelValue(labelNormal,
            String.format(Locale.US, "%.1f %s",
                    normalUsage, gr ? "ώρες" : "hours"));

    logLabelValue(labelHeavy,
            String.format(Locale.US, "%.1f %s",
                    heavyUsage, gr ? "ώρες" : "hours"));

} else {

    String na = gr ? "Μ/Δ" : "N/A";

    logLabelValue(labelLight, na);
    logLabelValue(labelNormal, na);
    logLabelValue(labelHeavy, na);
}

// ------------------------------------------------------------
// 🔴 REMAINING TIME (FROM CONSUMPTION)
// ------------------------------------------------------------
float battPct = (float) getBatteryPercentSafe();

if (Float.isNaN(battPct)) battPct = 0f;
battPct = Math.max(0f, Math.min(100f, battPct));

float lightRemaining = Float.NaN;
float normalRemaining = Float.NaN;
float heavyRemaining = Float.NaN;

if (!Float.isNaN(baseHours) && baseHours > 0f && battPct > 0f) {

    float factor = battPct / 100f;

    lightRemaining = Math.max(0f, baseHours * lightMul * factor);
    normalRemaining = Math.max(0f, baseHours * normalMul * factor);
    heavyRemaining = Math.max(0f, baseHours * heavyMul * factor);

    appendHtml("<br>");
}

logOk(gr
        ? "Εκτίμηση υπόλοιπου χρόνου"
        : "Remaining time estimation");

logLine();

logLabelValue(
        gr ? "Μπαταρία" : "Battery",
        String.format(Locale.US, "%.0f%%", battPct)
);

String na = gr ? "Μ/Δ" : "N/A";

logLabelValue(
        labelLight,
        Float.isNaN(lightRemaining)
                ? na
                : String.format(Locale.US, "%.1f %s",
                        lightRemaining, gr ? "ώρες" : "hours")
);

logLabelValue(
        labelNormal,
        Float.isNaN(normalRemaining)
                ? na
                : String.format(Locale.US, "%.1f %s",
                        normalRemaining, gr ? "ώρες" : "hours")
);

logLabelValue(
        labelHeavy,
        Float.isNaN(heavyRemaining)
                ? na
                : String.format(Locale.US, "%.1f %s",
                        heavyRemaining, gr ? "ώρες" : "hours")
);

if (!isLab14GamaMode) {
    
// ------------------------------------------------
// BATTERY PERFORMANCE — EVIDENCE BOUND, NOT AN OEM-HEALTH SCORE
// ------------------------------------------------------------
// A universal 500 mAh/h 'expected' reference, an arbitrary screen-size
// multiplier and a clamped omega index cannot establish battery health.
appendHtml("<br>");
logInfo(gr ? "Όρια αξιολόγησης διάρκειας"
           : "Endurance assessment limitations");
logLabelValue(gr ? "Μέσος ρυθμός κατανάλωσης" : "Average charge usage rate",
        Float.isNaN(correctedPerHour)
                ? (gr ? "Μη διαθέσιμος" : "Unavailable")
                : String.format(Locale.US, "%.1f mAh/h — συγκεκριμένος φόρτος",
                        correctedPerHour));
logLabelWarnValue(gr ? "Υγεία μπαταρίας / ενεργειακή απόδοση"
                     : "Battery health / efficiency",
        gr ? "ΔΕΝ ΥΠΟΛΟΓΙΖΕΤΑΙ από τεχνητό δείκτη ή σύγκριση με αυθαίρετη βάση."
           : "NOT DERIVED from an artificial score or universal reference draw.");
logLabelValue(gr ? "Επόμενη αξιόπιστη μέτρηση" : "Next verifiable test",
        gr ? "Επαναληπτική εκφόρτιση σε ίδιες συνθήκες, με πραγματική μεταβολή charge counter."
           : "Repeat the discharge test under matched conditions with valid charge-counter deltas.");

promptRestoreNormalMode();

// ------------------------------------------------------------
// 🔴 SAFE VALUES (FIXED)
// ------------------------------------------------------------
float safePerHour =
        Float.isNaN(correctedPerHour) ? -1f : correctedPerHour;

float safeEstimated = -1f; // full-capacity endurance not measured

float safeLight =
        Float.isNaN(lightRemaining) ? -1f : lightRemaining;

float safeNormal =
        Float.isNaN(normalRemaining) ? -1f : normalRemaining;

float safeHeavy =
        Float.isNaN(heavyRemaining) ? -1f : heavyRemaining;

// 🔴 FIX: αυτά έλειπαν
float safeRemLight = safeLight;
float safeRemNormal = safeNormal;
float safeRemHeavy = safeHeavy;

// ------------------------------------------------------------
// 🔴 SAVE LAB 14B RESULTS (CRITICAL)
// ------------------------------------------------------------
try {

    getSharedPreferences("GEL_DIAG", MODE_PRIVATE)
            .edit()
            .putInt("lab14b_evidence_schema", 1)
            .putString("lab14b_rate_source", "BATTERY_MANAGER_CHARGE_COUNTER_ELAPSED")
            .putLong("lab14b_sample_duration_ms", measuredElapsedMs)
            .putBoolean("lab14b_rate_valid", safePerHour > 0f && !fakeCounter)
            .putFloat("lab14b_consumption_per_hour", safePerHour)
            .putFloat("lab14b_estimated_hours", safeEstimated)
            .putFloat("lab14b_light_hours", safeLight)
            .putFloat("lab14b_normal_hours", safeNormal)
            .putFloat("lab14b_heavy_hours", safeHeavy)
            .putFloat("lab14b_remaining_light", safeRemLight)
            .putFloat("lab14b_remaining_normal", safeRemNormal)
            .putFloat("lab14b_remaining_heavy", safeRemHeavy)
            .putLong("lab14b_ts", System.currentTimeMillis())
            .apply();

} catch (Throwable ignore) {}

// 🔴 ΚΛΕΙΝΕΙ ΤΟ if (!isLab14GamaMode)
}

} catch (Throwable t) {

    logError(gr
            ? "Σφάλμα ανάλυσης"
            : "Analysis error");

} finally {

    synchronized (lab14Lock) {
        lab14SessionActive = false;
    }

    try { stopCpuBurn(); } catch (Throwable ignore) {}
    try { stopMemoryStress(); } catch (Throwable ignore) {}
    try { stopGpuStress(); } catch (Throwable ignore) {}
    try { restoreBrightnessAndKeepOn(); } catch (Throwable ignore) {}

    try {
        if (usageHandler != null) {
            usageHandler.removeCallbacksAndMessages(null);
            usageHandler = null;
        }
    } catch (Throwable ignore) {}

    lab14Cancelled = false;
    lab14Running = false;
    isLab14BMode = false;
    isLab14GamaMode = false;

    appendHtml("<br>");

    logOk(
        gr
        ? (wasGama
            ? "Το Lab 14 GAMA ολοκληρώθηκε."
            : "Το Lab 14B ολοκληρώθηκε.")
        : (wasGama
            ? "Lab 14 GAMA finished."
            : "Lab 14B finished.")
    );

    try {
        if (batterySuiteDialog != null && batterySuiteDialog.isShowing()) {
            batterySuiteDialog.dismiss();
            batterySuiteDialog = null;
        }
    } catch (Throwable ignore) {}

    runOnUiThread(() -> {
        try {
            if (lab14Dialog != null && lab14Dialog.isShowing()) {
                lab14Dialog.dismiss();
                lab14Dialog = null;
            }
        } catch (Throwable ignore) {}
    });
}

}, 300000L);

}); // showLab14BAdvisory

} // runLab14DurationMode

// ============================================================
// LAB 14B — PRE TEST ADVISORY (STABILIZED MODE)
// ============================================================
private void showLab14BAdvisory(Runnable onContinue) {

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    b.setCancelable(true);

    LinearLayout root = buildGELPopupRoot(this);

    root.addView(
            buildPopupHeader(
                    this,
                    gr
                    ? "Έλεγχος κατανάλωσης και διάρκειας μπαταρίας"
                    : "Battery usage and duration test"
            )
    );

    final String text =
            gr
            ? "Η δοκιμή προσομοιώνει καθημερινή χρήση για 5 λεπτά "
              + "ώστε να εκτιμηθούν κατανάλωση, διάρκεια και συμπεριφορά μπαταρίας.\n\n"
              + "Προϋποθέσεις:\n"
              + "• Μπαταρία τουλάχιστον 30%\n"
              + "• Η συσκευή να μην φορτίζει\n"
              + "• Airplane Mode ενεργό για σταθερότητα runs."

            : "This test simulates 5 minutes of real device usage "
              + "to estimate battery consumption, endurance "
              + "and battery behavior.\n\n"
              + "Requirements:\n"
              + "• Battery at least 30%\n"
              + "• Device not charging\n"
              + "• Airplane Mode enabled for run stability.";

    int percent = getBatteryPercentSafe();

    boolean chargingNow = false;
    boolean airplaneMode = false;

    try {
        chargingNow = isDeviceCharging();

        airplaneMode =
                Settings.Global.getInt(
                        getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON,
                        0
                ) == 1;

    } catch (Throwable ignore) {}

    boolean chargingOk = !chargingNow;

    SpannableStringBuilder sb =
            new SpannableStringBuilder();

    int green = 0xFF39FF14;
    int red   = 0xFFFF4444;

    int start = sb.length();

    sb.append(text).append("\n\n");

    sb.setSpan(
            new ForegroundColorSpan(green),
            start,
            sb.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    );

    // ------------------------------------------------
    // BATTERY STATUS
    // ------------------------------------------------
    start = sb.length();

    String batteryStatus;
    int batteryColor;

    if (percent < 30) {

        batteryStatus =
                gr
                ? "✖ Μπαταρία: " + percent +
                  "% (Απαιτείται τουλάχιστον 30%)"
                : "✖ Battery: " + percent +
                  "% (Minimum 30% required)";

        batteryColor = red;

    } else {

        batteryStatus =
                gr
                ? "✓ Μπαταρία: " + percent + "%"
                : "✓ Battery: " + percent + "%";

        batteryColor = green;
    }

    sb.append(batteryStatus).append("\n");

    sb.setSpan(
            new ForegroundColorSpan(
                    batteryColor
            ),
            start,
            sb.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    );

    if (percent < 30) {

        start = sb.length();

        sb.append("\n");

        sb.append(
                gr
                ? "Κάτω από 30% μπορεί να επέμβει system protection."
                : "Below 30% system protection may interfere."
        );

        sb.setSpan(
                new ForegroundColorSpan(red),
                start,
                sb.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }

    // ------------------------------------------------
    // CHARGING STATUS
    // ------------------------------------------------
    sb.append("\n\n");

    start = sb.length();

    String chargingStatus =
            chargingOk
            ? (gr
               ? "✓ Η συσκευή δεν φορτίζεται"
               : "✓ Device not charging")
            : (gr
               ? "✖ Η συσκευή φορτίζει"
               : "✖ Device is charging");

    sb.append(chargingStatus);

    sb.setSpan(
            new ForegroundColorSpan(
                    chargingOk ? green : red
            ),
            start,
            sb.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    );

    // ------------------------------------------------
    // AIRPLANE MODE STATUS
    // ------------------------------------------------
    sb.append("\n");

    start = sb.length();

    String airStatus =
            airplaneMode
            ? (gr
               ? "✓ Airplane Mode ενεργό"
               : "✓ Airplane Mode enabled")
            : (gr
               ? "✖ Airplane Mode ανενεργό"
               : "✖ Airplane Mode disabled");

    sb.append(airStatus);

    sb.setSpan(
            new ForegroundColorSpan(
                    airplaneMode ? green : red
            ),
            start,
            sb.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    );

    TextView msg = new TextView(this);
    msg.setText(sb);
    msg.setTextSize(14.5f);
    msg.setLineSpacing(0f,1.2f);

    root.addView(msg);

    root.addView(buildMuteRow());

    // ------------------------------------------------
    // BUTTON
    // ------------------------------------------------
    boolean allowContinue =
        chargingOk &&
        percent >= 30;

    LinearLayout row =
            new LinearLayout(this);

    row.setOrientation(
            LinearLayout.VERTICAL
    );

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    lp.setMargins(
            0, dp(14),0,0
    );

    Button btnAction;

    if (allowContinue) {

        btnAction =
                gelButton(
                        this,
                        gr
                        ? "Έναρξη 5λεπτου τεστ"
                        : "Start 5-minute test",
                        0xFF0B5D1E
                );

    } else {

        btnAction =
                gelButton(
                        this,
                        gr ? "Έξοδος" : "Exit",
                        0xFF8B0000
                );
    }

    btnAction.setLayoutParams(lp);

    row.addView(btnAction);
    root.addView(row);

    b.setView(root);

    AlertDialog dlg = b.create();

    if (dlg.getWindow()!=null){
        dlg.getWindow().setBackgroundDrawable(
                new ColorDrawable(
                        Color.TRANSPARENT
                )
        );
    }

    dlg.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(dlg);

    dlg.setOnCancelListener(
            d -> AppTTS.stop()
    );

    dlg.setOnDismissListener(
            d -> AppTTS.stop()
    );

    // ------------------------------------------------
    // TTS
    // ------------------------------------------------
    new Handler(
            Looper.getMainLooper()
    ).postDelayed(() -> {

        if (!dlg.isShowing())
            return;

        AppTTS.stop();

        if (!AppTTS.isMuted(this)) {

            String speakText =
                    text + ". "
                    + batteryStatus + ". "
                    + chargingStatus + ". "
                    + airStatus;

            AppTTS.ensureSpeak(
                    ManualTestsActivity.this,
                    speakText
            );
        }

    },120L);

    // ------------------------------------------------
    // CLICK
    // ------------------------------------------------
    btnAction.setOnClickListener(v -> {

        AppTTS.stop();

        dlg.dismiss();

        if (!allowContinue)
            return;

        if (onContinue != null) {

            if (!checkLab14BConditions()) {

                logWarn(gr
                        ? "Οι συνθήκες άλλαξαν — το τεστ ακυρώθηκε"
                        : "Conditions changed — test aborted");

                return;
            }

            onContinue.run();
        }
    });
}

// ==========================================================
// 🔥 LAB 14B — SIMPLIFIED (BATTERY USAGE ONLY)
// ==========================================================

private void startLab14DurationPopup(long durationSec, boolean gamaMode) {

    boolean wasGama = gamaMode;

    // ------------------------------------------------------------
    // 🔴 CLOSE PREVIOUS POPUP (CRITICAL)
    // ------------------------------------------------------------
    try {
        if (lab14Dialog != null && lab14Dialog.isShowing()) {
            lab14Dialog.dismiss();
            lab14Dialog = null;
        }
    } catch (Throwable ignore) {}

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    ManualTestsActivity.this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(20), dp(24), dp(28));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(4), 0xFFFFD700);
    root.setBackground(bg);

    // TITLE
    TextView title = new TextView(this);
    title.setText(
    gr
    ? (wasGama
        ? "LAB 14 GAMA — Διάρκεια μπαταρίας σε Gaming "
        : "LAB 14B — Διάρκεια μπαταρίας σε καθημερινή χρήση")
    : (wasGama
        ? "LAB 14 GAMA — Gaming battery endurance"
        : "LAB 14B — Daily battery usage & duration")
);
    title.setTextColor(Color.WHITE);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

    // STATUS
TextView statusText = new TextView(this);

statusText.setText(
    gr
    ? (wasGama
        ? "Προσομοίωση φόρτου gaming..."
        : "Προσομοίωση καθημερινής χρήσης...")
    : (wasGama
        ? "Gaming stress simulation..."
        : "Daily usage simulation...")
);
    statusText.setTextColor(0xFF39FF14);
    statusText.setGravity(Gravity.CENTER);
    root.addView(statusText);

    // COUNTER
    counterText = new TextView(this);
    counterText.setText("0 / " + durationSec);
    counterText.setTextColor(0xFF39FF14);
    counterText.setGravity(Gravity.CENTER);
    root.addView(counterText);

    // LIVE STATS
    lab14LiveStats = new TextView(this);
    lab14LiveStats.setTextColor(0xFF39FF14);
    lab14LiveStats.setTextSize(12f);
    lab14LiveStats.setPadding(0, dp(10), 0, dp(6));
    lab14LiveStats.setText("Initializing...");
    root.addView(lab14LiveStats);

    // VIDEO
    lab14StressVideo = new VideoView(this);
    lab14StressVideo.setZOrderOnTop(true);
    LinearLayout.LayoutParams vLp =
            new LinearLayout.LayoutParams(dp(220), dp(120));
    vLp.gravity = Gravity.CENTER;
    lab14StressVideo.setLayoutParams(vLp);
    root.addView(lab14StressVideo);

    // BAR
    lab14MainBar = new LinearLayout(this);
    lab14MainBar.setOrientation(LinearLayout.HORIZONTAL);

    for (int i = 0; i < 12; i++) {
        View seg = new View(this);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0, dp(10), 1f);
        lp.setMargins(dp(2), 0, dp(2), 0);
        seg.setLayoutParams(lp);
        seg.setBackgroundColor(0xFF333333);
        lab14MainBar.addView(seg);
    }

    root.addView(lab14MainBar);

    // EXIT
    Button exitBtn = gelButton(
            this,
            gr ? "Έξοδος τεστ" : "Exit test",
            0xFF8B0000
    );

    exitBtn.setOnClickListener(v -> {
        lab14Cancelled = true;
        lab14Running = false;
        lab14StopAllStress();
        try { lab14CleanupUI(); } catch (Throwable ignore) {}
    });

    root.addView(exitBtn);

    b.setView(root);

    lab14Dialog = b.create();

    if (lab14Dialog.getWindow() != null) {
        lab14Dialog.getWindow()
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    // ?? RESET STATE
    lab14Running = true;
    lab14Cancelled = false;

    lab14Dialog.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(lab14Dialog);

startLab14BProgressLoop(
    statusText,
    durationSec,
    gr
);

if (lab14Dialog.getWindow() != null) {

    int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92);
    int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.85);

    lab14Dialog.getWindow().setLayout(width, height);
}

    // 🔥 VIDEO
    lab14StressVideo.post(() -> {
        try {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.battery_stress_loop);
            lab14StressVideo.setVideoURI(uri);
            lab14StressVideo.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                lab14StressVideo.start();
            });
        } catch (Throwable ignore) {}
    });
}

private void startLab14BProgressLoop(TextView statusText, long durationSec, boolean gr) {

    t0 = SystemClock.elapsedRealtime();

    ui.post(new Runnable() {

        @Override
        public void run() {

            try {

                if (!lab14Running || lab14Cancelled) {
                    return;
                }

                long now = SystemClock.elapsedRealtime();

                // 🔴 REAL TIME
                int elapsed = (int) ((now - t0) / 1000);

                // 🔴 CLAMP
                if (elapsed < 0) elapsed = 0;
                if (elapsed > durationSec) elapsed = (int) durationSec;

                // 🔴 STATUS
statusText.setText(
    gr
    ? (isLab14GamaMode
        ? "Προσομοίωση gaming χρήσης"
        : "Προσομοίωση καθημερινής χρήσης")
    : (isLab14GamaMode
        ? "Gaming usage simulation"
        : "Daily usage simulation")
);

                // 🔴 COUNTER
                if (counterText != null) {
                    counterText.setText(elapsed + " / " + durationSec);
                }

                // 🔴 PROGRESS BAR
                if (lab14MainBar != null) {

                    int segCount = lab14MainBar.getChildCount();

                    float ratio = Math.min(1f, elapsed / (float) durationSec);

                    int active = (int) Math.floor(ratio * segCount);

                    for (int i = 0; i < segCount; i++) {

                        View seg = lab14MainBar.getChildAt(i);

                        if (seg != null) {
                            seg.setBackgroundColor(
                                    i < active ? 0xFF39FF14 : 0xFF333333
                            );
                        }
                    }
                }

                updateLab14LiveStats();

                if (elapsed >= durationSec) {

    lab14Running = false;

    ui.post(() -> {

        lab14StopAllStress();

        try {
            if (lab14Dialog != null && lab14Dialog.isShowing()) {
                lab14Dialog.dismiss();
            }
        } catch (Throwable ignore) {}

    });

    return;
}

            } catch (Throwable t) {

            }

            // 🔴 FAST REFRESH
            if (lab14Running && !lab14Cancelled) {
                ui.postDelayed(this, 200);
            }
        }
    });
}

private void simulateUiInteraction() {

    // Controlled light interaction.
    // Keep background video running continuously.
    // Intentionally no video pause here.

    try {

        // tiny UI-like workload only
        Math.sqrt(Math.random());

    } catch (Throwable ignore) {}
}

private void simulateShortCpuBurst() {
    new Thread(() -> {

        if (lab14ForceStop) return;

        long t = SystemClock.elapsedRealtime();

        while (SystemClock.elapsedRealtime() - t < 1500) {

            if (lab14ForceStop) return;

            Math.sqrt(Math.random());
        }

    }).start();
}

private float getScreenSizeInches() {
    DisplayMetrics dm = getResources().getDisplayMetrics();
    float x = (dm.widthPixels / dm.xdpi);
    float y = (dm.heightPixels / dm.ydpi);
    return (float)Math.sqrt(x*x + y*y);
}

// ============================================================
// RESTORE NORMAL MODE PROMPT
// ============================================================
private void promptRestoreNormalMode() {

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(
            dp(24),
            dp(22),
            dp(24),
            dp(24)
    );

    GradientDrawable bg =
            new GradientDrawable();

    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(12));
    bg.setStroke(
            dp(4),
            0xFFFFD700
    );

    root.setBackground(bg);

    // TITLE (white)
    TextView title =
            new TextView(this);

    title.setText(
            gr
            ? "Επαναφορά Συσκευής"
            : "Restore Device"
    );

    title.setTextColor(Color.WHITE);
    title.setTextSize(20f);
    title.setTypeface(
            null,
            Typeface.BOLD
    );

    root.addView(title);

    // MESSAGE (green)
    TextView msg =
            new TextView(this);

    msg.setText(
            gr
            ? "\nΘέλεις να επαναφέρουμε τη συσκευή σου "
              + "απενεργοποιώντας το Airplane Mode "
              + "και επαναφέροντας τις κανονικές συνδέσεις;"
            : "\nDisable Airplane Mode now "
              + "and restore normal device connections?"
    );

    msg.setTextColor(
            0xFF39FF14
    );

    msg.setTextSize(16f);

    root.addView(msg);

    // BUTTON ROW
    LinearLayout row =
            new LinearLayout(this);

    row.setOrientation(
            LinearLayout.HORIZONTAL
    );

    row.setPadding(
            0,
            dp(24),
            0,
            0
    );

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
            );

    lp.setMargins(
            dp(6),0,dp(6),0
    );

    // NO button
    Button noBtn = new com.gel.cleaner.GelButton(this);
    noBtn.setText(gr ? "ΟΧΙ" : "NO");
    noBtn.setTextColor(Color.WHITE);
    noBtn.setAllCaps(false);

    GradientDrawable noBg =
            new GradientDrawable();

    noBg.setColor(0xFF8B0000);
    noBg.setCornerRadius(dp(10));
    noBg.setStroke(
            dp(3),
            0xFFFFD700
    );

    noBtn.setBackground(noBg);
    noBtn.setLayoutParams(lp);

    // YES button
    Button yesBtn = new com.gel.cleaner.GelButton(this);
    yesBtn.setText(gr ? "ΝΑΙ" : "YES");
    yesBtn.setTextColor(Color.WHITE);
    yesBtn.setAllCaps(false);

    GradientDrawable yesBg =
            new GradientDrawable();

    yesBg.setColor(0xFF0B5F3B);
    yesBg.setCornerRadius(dp(10));
    yesBg.setStroke(
            dp(3),
            0xFFFFD700
    );

    yesBtn.setBackground(yesBg);
    yesBtn.setLayoutParams(lp);

    row.addView(noBtn);
    row.addView(yesBtn);

    root.addView(row);

    b.setView(root);

    final AlertDialog dlg =
            b.create();

    if (dlg.getWindow()!=null) {
        dlg.getWindow().setBackgroundDrawable(
                new ColorDrawable(
                        Color.TRANSPARENT
                )
        );
    }

    noBtn.setOnClickListener(v ->
            dlg.dismiss()
    );

    yesBtn.setOnClickListener(v -> {

        dlg.dismiss();

        try {

            startActivity(
                    new Intent(
                            Settings.ACTION_AIRPLANE_MODE_SETTINGS
                    )
            );

        } catch (Throwable e) {

            try {

                startActivity(
                        new Intent(
                                Settings.ACTION_SETTINGS
                        )
                );

            } catch (Throwable ignore) {}
        }
    });

    dlg.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(dlg);
}
private void showAirplaneModePrompt() {

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    LinearLayout root =
            new LinearLayout(this);

    root.setOrientation(
            LinearLayout.VERTICAL
    );

    root.setPadding(
            dp(24),
            dp(22),
            dp(24),
            dp(24)
    );

    GradientDrawable bg =
            new GradientDrawable();

    bg.setColor(0xFF101010);      // black
    bg.setCornerRadius(dp(12));
    bg.setStroke(dp(4),0xFFFFD700); // gold

    root.setBackground(bg);

    TextView title =
            new TextView(this);

    title.setText(
            gr
            ? "Απαιτείται Airplane Mode"
            : "Airplane Mode Required"
    );

    title.setTextColor(Color.WHITE);
    title.setTextSize(20f);
    title.setTypeface(null,Typeface.BOLD);

    root.addView(title);

    TextView msg =
            new TextView(this);

    msg.setText(
            gr
            ? "\nΓια σταθερότητα των runs ενεργοποιήστε τώρα Airplane Mode."
            : "\nEnable Airplane Mode now for stable runs."
    );

    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(16f);

    root.addView(msg);

    // buttons row
    LinearLayout row =
            new LinearLayout(this);

    row.setOrientation(
            LinearLayout.HORIZONTAL
    );

    row.setPadding(
            0,dp(24),0,0
    );

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
            );

    lp.setMargins(
            dp(6),0,dp(6),0
    );

    // NO red
    Button noBtn = new com.gel.cleaner.GelButton(this);
    noBtn.setText(gr?"ΟΧΙ":"NO");
    noBtn.setTextColor(Color.WHITE);
    noBtn.setAllCaps(false);

    GradientDrawable noBg =
            new GradientDrawable();

    noBg.setColor(0xFF8B0000);
    noBg.setCornerRadius(dp(10));
    noBg.setStroke(dp(3),0xFFFFD700);

    noBtn.setBackground(noBg);
    noBtn.setLayoutParams(lp);

    // YES green
    Button yesBtn = new com.gel.cleaner.GelButton(this);
    yesBtn.setText(gr?"ΝΑΙ":"YES");
    yesBtn.setTextColor(Color.WHITE);
    yesBtn.setAllCaps(false);

    GradientDrawable yesBg =
            new GradientDrawable();

    yesBg.setColor(0xFF0B5F3B);
    yesBg.setCornerRadius(dp(10));
    yesBg.setStroke(dp(3),0xFFFFD700);

    yesBtn.setBackground(yesBg);
    yesBtn.setLayoutParams(lp);

    row.addView(noBtn);
    row.addView(yesBtn);

    root.addView(row);

    b.setView(root);

    final AlertDialog dlg =
            b.create();

    if (dlg.getWindow()!=null){
        dlg.getWindow().setBackgroundDrawable(
                new ColorDrawable(
                        Color.TRANSPARENT
                )
        );
    }

    noBtn.setOnClickListener(v ->
            dlg.dismiss()
    );

yesBtn.setOnClickListener(v -> {

    dlg.dismiss();

    // auto resume flag
    pendingRestartLab14Mode = isLab14GamaMode ? 2 : 1;
// 1 = 14B
// 2 = GAMA

    try {

        startActivity(
            new Intent(
                Settings.ACTION_AIRPLANE_MODE_SETTINGS
            )
        );

    } catch(Throwable ignore){}
});

    dlg.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(dlg);
}

private void simulateGpuBurst() {

    new Thread(() -> {

        if (lab14ForceStop) return;

        long t = SystemClock.elapsedRealtime();

        while (SystemClock.elapsedRealtime() - t < 3000) {

            if (lab14ForceStop) return;

            Math.sin(Math.random());
            Math.sqrt(Math.random());
            Math.tan(Math.random());
        }

    }).start();
}

private void simulateGpuBaseline() {
    try {
        if (lab14StressVideo != null && !lab14StressVideo.isPlaying()) {
            lab14StressVideo.start();
        }
    } catch (Throwable ignore) {}
}

private void simulateMemoryStressLite() {

    try {

        int size = 256 * 1024; // ~256KB

        byte[] block = new byte[size];

        for (int i = 0; i < block.length; i += 64) {
            block[i] = (byte) (i % 128);
        }

    } catch (Throwable ignore) {}
}

private float readTempMulti(iDoctorEngine.BatterySnapshot snap) {

    float t1 = getBatteryTemperature(); // current method
    float t2 = snap != null ? snap.batteryTempC : Float.NaN;
    float t3 = readSysTemp();

    // priority: sysfs > snapshot > api
    if (!Float.isNaN(t3) && t3 > 0f) return t3;
    if (!Float.isNaN(t2) && t2 > 0f) return t2;
    return t1;
}

private float readVoltMulti(iDoctorEngine.BatterySnapshot snap) {

    float v1 = getBatteryVoltageFiltered();
    float v2 = readSysVolt();

    if (!Float.isNaN(v2) && v2 > 0f) return v2;
    return v1;
}

private float readSysTemp() {
    try {
        String path = "/sys/class/power_supply/battery/temp";
        BufferedReader br = new BufferedReader(new FileReader(path));
        float v = Float.parseFloat(br.readLine());
        br.close();

        return v / 10f; // usually deci-degrees
    } catch (Throwable e) {
        return Float.NaN;
    }
}

private float readSysVolt() {
    try {
        String path = "/sys/class/power_supply/battery/voltage_now";
        BufferedReader br = new BufferedReader(new FileReader(path));
        float v = Float.parseFloat(br.readLine());
        br.close();

        return v / 1000000f; // μV → V
    } catch (Throwable e) {
        return Float.NaN;
    }
}

private float getBatteryVoltageFast() {

    try {

        File f = new File(
            "/sys/class/power_supply/battery/voltage_now"
        );

        if (!f.exists()) {
            return Float.NaN;
        }

        BufferedReader br =
                new BufferedReader(new FileReader(f));

        String s = br.readLine();

        br.close();

        if (s == null) {
            return Float.NaN;
        }

        float uv = Float.parseFloat(s.trim());

        // μV -> V
        float v = uv / 1000000f;

        if (v > 2.5f && v < 5.5f) {
            return v;
        }

    } catch (Throwable ignore) {}

    return Float.NaN;
}

// ============================================================
// LAB 14 — TRANSITION VOLTAGE PULSE
// ============================================================
private void lab14RunVoltagePulse(final int elapsedSec) {

    if (lab14VoltagePulseRunning) return;

    lab14VoltagePulseRunning = true;

    new Thread(() -> {

        float before = Float.NaN;
        float relax = Float.NaN;
        float after = Float.NaN;

        final int cores =
        Runtime.getRuntime().availableProcessors();

final int restoreThreads =
        lab14SelectedStressThreads > 0
                ? lab14SelectedStressThreads
                : (
                    lab14OptimalThreads >= 2
                            ? lab14OptimalThreads
                            : Math.max(4, cores - 1)
                );

        try {

            // ------------------------------------------------
            // 1) READ CURRENT HARD-LOAD VOLTAGE
            // ------------------------------------------------
            try {
                before = lab14Voltage();
            } catch (Throwable ignore) {}

            if (!Float.isNaN(before) && before > 3.0f && before < 5.0f) {

                if (Float.isNaN(voltageUnderLoad[0]) ||
                        before < voltageUnderLoad[0]) {

                    voltageUnderLoad[0] = before;
                }

                if (Float.isNaN(vLoad2[0]) || before < vLoad2[0]) {
                    vLoad2[0] = before;
                }
            }

            // ------------------------------------------------
            // 2) FULL CONTROLLED LOAD DROP
            //    This mimics FAST → HARD phase transition.
            // ------------------------------------------------
            try { stopCpuBurn(); } catch (Throwable ignore) {}
            try { stopMemoryStress(); } catch (Throwable ignore) {}
            try { stopMemoryBandwidthStress(); } catch (Throwable ignore) {}
            try { stopGpuStress(); } catch (Throwable ignore) {}
            try { stopVibrationStress(); } catch (Throwable ignore) {}

            try {
                ui.removeCallbacks(lab14VibrationLoop);
            } catch (Throwable ignore) {}

            // 🔴 give BMS / Android time to publish a new voltage state
            try {
                Thread.sleep(3000);
            } catch (Throwable ignore) {}

            // ------------------------------------------------
            // 3) READ RELAX / RECOVERY VOLTAGE
            // ------------------------------------------------
            try {
                relax = lab14Voltage();
            } catch (Throwable ignore) {}

            if (!Float.isNaN(relax) && relax > 3.0f && relax < 5.0f) {

                vRecover[0] = relax;

// ------------------------------------------------
// 🔴 LOCAL PULSE RECOVERY — REAL ONLY
// ------------------------------------------------
if (!Float.isNaN(before)) {

    float localRec =
            relax - before;

    if (localRec > 0.002f && localRec < 1.0f) {

        voltageRecovery[0] = localRec;
        voltageRecoverySpeed[0] = localRec / 3.0f;
    }
}
            }

            // ------------------------------------------------
            // 4) RESTART FULL HARD LOAD
            // ------------------------------------------------
            if (lab14Running && !lab14Cancelled) {

                try {
                    startCpuBurnLimitedThreads(restoreThreads);
                    lab14CpuThreadsCurrent = restoreThreads;
                } catch (Throwable ignore) {}

                try { startGpuStress(); } catch (Throwable ignore) {}
                try { startMemoryStress(); } catch (Throwable ignore) {}
                try { startMemoryBandwidthStress(); } catch (Throwable ignore) {}

                try {
                    ui.removeCallbacks(lab14VibrationLoop);
                    ui.post(lab14VibrationLoop);
                } catch (Throwable ignore) {}
            }

            // 🔴 wait after reload — BMS/Android may publish voltage with delay
try {
    Thread.sleep(10000);
} catch (Throwable ignore) {}

            // ------------------------------------------------
            // 5) READ NEW HARD-LOAD VOLTAGE
            // ------------------------------------------------
            try {
                after = lab14Voltage();
            } catch (Throwable ignore) {}

            if (!Float.isNaN(after) && after > 3.0f && after < 5.0f) {

                if (Float.isNaN(voltageUnderLoad[0]) ||
                        after < voltageUnderLoad[0]) {

                    voltageUnderLoad[0] = after;
                }

                if (Float.isNaN(vLoad2[0]) || after < vLoad2[0]) {
                    vLoad2[0] = after;
                }
            }

            // ------------------------------------------------
            // 6) LOG TRANSITION RESULT
            // ------------------------------------------------
            final float fBefore = before;
            final float fRelax = relax;
            final float fAfter = after;
            final float fUnder = voltageUnderLoad[0];
            final float fRecovery =
        (!Float.isNaN(before) && !Float.isNaN(relax))
                ? (relax - before)
                : Float.NaN;

            if (DEBUG_MODE) {
                runOnUiThread(() -> {

                    logWarn(
                            "TRANSITION_PULSE | elapsed=" + elapsedSec +
                                    " | before=" + String.format(Locale.US, "%.3f", fBefore) +
                                    " | relax=" + String.format(Locale.US, "%.3f", fRelax) +
                                    " | after=" + String.format(Locale.US, "%.3f", fAfter) +
                                    " | underLoad=" + String.format(Locale.US, "%.3f", fUnder) +
                                    " | recovery=" + String.format(Locale.US, "%.3f", fRecovery)
                    );
                });
            }

        } catch (Throwable ignore) {

        } finally {

            lab14VoltagePulseRunning = false;
        }

    }, "LAB14_TRANSITION_PULSE").start();
}

// ============================================================
// LAB 14 — PRE-HARD TRANSITION VOLTAGE PULSE
// ============================================================
private void lab14RunPreHardVoltagePulse(final int elapsedSec) {

    new Thread(() -> {

        float before = Float.NaN;
        float relax = Float.NaN;
        float after = Float.NaN;

        final int cores =
        Runtime.getRuntime().availableProcessors();

final int restoreThreads =
        lab14SelectedStressThreads > 0
                ? lab14SelectedStressThreads
                : (
                    lab14OptimalThreads >= 2
                            ? lab14OptimalThreads
                            : Math.max(4, cores - 1)
                );

        try {

            // ------------------------------------------------
            // 1) READ FAST-PHASE LOAD VOLTAGE
            // ------------------------------------------------
            try {
                before = lab14Voltage();
            } catch (Throwable ignore) {}

            if (!Float.isNaN(before) && before > 3.0f && before < 5.0f) {

                if (Float.isNaN(vLoad1[0]) || before < vLoad1[0]) {
                    vLoad1[0] = before;
                }
            }

            // ------------------------------------------------
            // 2) CONTROLLED PRE-HARD LOAD DROP
            // ------------------------------------------------
            try { stopCpuBurn(); } catch (Throwable ignore) {}
            try { stopMemoryStress(); } catch (Throwable ignore) {}
            try { stopMemoryBandwidthStress(); } catch (Throwable ignore) {}
            try { stopGpuStress(); } catch (Throwable ignore) {}
            try { stopVibrationStress(); } catch (Throwable ignore) {}

            try {
                ui.removeCallbacks(lab14VibrationLoop);
            } catch (Throwable ignore) {}

            // 🔴 let BMS / Android refresh before hard phase
            try {
                Thread.sleep(2500);
            } catch (Throwable ignore) {}

            // ------------------------------------------------
            // 3) READ PRE-HARD RECOVERY
            // ------------------------------------------------
            try {
                relax = lab14Voltage();
            } catch (Throwable ignore) {}

            if (!Float.isNaN(relax) && relax > 3.0f && relax < 5.0f) {

                vRecover[0] = relax;

                if (!Float.isNaN(before)) {

                    float rec =
                            relax - before;

                    if (rec > 0.002f && rec < 1.0f) {
                        voltageRecovery[0] = rec;
                        voltageRecoverySpeed[0] = rec / 2.5f;
                    }
                }
            }

            // ------------------------------------------------
            // 4) RESTORE FAST LOAD UNTIL HARD PHASE TAKES OVER
            // ------------------------------------------------
            if (lab14Running && !lab14Cancelled) {

                try {
                    startCpuBurnLimitedThreads(restoreThreads);
                    lab14CpuThreadsCurrent = restoreThreads;
                } catch (Throwable ignore) {}

                try { startGpuStress(); } catch (Throwable ignore) {}
                try { startMemoryStress(); } catch (Throwable ignore) {}
                try { startMemoryBandwidthStress(); } catch (Throwable ignore) {}

                try {
                    ui.removeCallbacks(lab14VibrationLoop);
                    ui.post(lab14VibrationLoop);
                } catch (Throwable ignore) {}
            }

            try {
                Thread.sleep(2500);
            } catch (Throwable ignore) {}

            // ------------------------------------------------
            // 5) READ AFTER PRE-HARD RELOAD
            // ------------------------------------------------
            try {
                after = lab14Voltage();
            } catch (Throwable ignore) {}

            if (!Float.isNaN(after) && after > 3.0f && after < 5.0f) {

                if (Float.isNaN(vLoad1[0]) || after < vLoad1[0]) {
                    vLoad1[0] = after;
                }
            }

            final float fBefore = before;
            final float fRelax = relax;
            final float fAfter = after;

            if (DEBUG_MODE) {
                runOnUiThread(() -> {

                    logWarn(
                            "PRE_HARD_TRANSITION | elapsed=" + elapsedSec +
                                    " | before=" + String.format(Locale.US, "%.3f", fBefore) +
                                    " | relax=" + String.format(Locale.US, "%.3f", fRelax) +
                                    " | after=" + String.format(Locale.US, "%.3f", fAfter)
                    );
                });
            }

        } catch (Throwable ignore) {}
    }, "LAB14_PRE_HARD_TRANSITION").start();
}

// ============================================================
// LAB 14 — VOLTAGE BEHAVIOR SUMMARY
// ============================================================
private void lab14LogVoltageBehaviorSummary(
        boolean gr,
        float voltageStart
) {

    appendHtml("<br>");
    logLine();

    logInfo(gr
            ? "LAB 14 — Συμπεριφορά τάσης"
            : "LAB 14 — Voltage behavior");

    logLine();

    if (Float.isNaN(voltageStart) ||
            Float.isNaN(lab14FirstHardVoltage) ||
            Float.isNaN(lab14LowestHardVoltage)) {

        logLabelWarnValue(
                gr ? "Συμπεριφορά τάσης" : "Voltage behavior",
                gr ? "Ανεπαρκή δεδομένα" : "Insufficient data"
        );

        return;
    }

    float initialDelta =
            voltageStart - lab14FirstHardVoltage;

    float worstDelta =
            voltageStart - lab14LowestHardVoltage;

    boolean voltageSettlingDetected =
            lab14FirstHardVoltage > voltageStart + 0.010f;

    float initialSag =
            Math.max(0f, initialDelta);

    float worstSag =
            Math.max(0f, worstDelta);

    logLabelValue(
            gr ? "Τάση έναρξης" : "Start voltage",
            String.format(Locale.US, "%.3f V", voltageStart)
    );

    logLabelValue(
            gr ? "Πρώτη τάση hard stress" : "First hard-load voltage",
            String.format(Locale.US, "%.3f V", lab14FirstHardVoltage)
    );

    if (!Float.isNaN(lab14MidHardVoltage)) {
        logLabelValue(
                gr ? "Μέση τάση hard stress" : "Mid-run hard voltage",
                String.format(Locale.US, "%.3f V", lab14MidHardVoltage)
        );
    }

    if (!Float.isNaN(lab14LateHardVoltage)) {
        logLabelValue(
                gr ? "Τελική τάση hard stress" : "Late-run hard voltage",
                String.format(Locale.US, "%.3f V", lab14LateHardVoltage)
        );
    }

    logLabelValue(
            gr ? "Χαμηλότερη τάση" : "Lowest hard-load voltage",
            String.format(Locale.US, "%.3f V", lab14LowestHardVoltage)
    );

    if (voltageSettlingDetected) {

        logLabelWarnValue(
                gr ? "Αρχική πτώση" : "Initial hard sag",
                gr
                        ? "Δεν υπολογίζεται — εντοπίστηκε αρχική σταθεροποίηση τάσης"
                        : "Not calculated — initial voltage settling detected"
        );

        logLabelValue(
                gr ? "Αρχική μεταβολή τάσης" : "Initial voltage change",
                String.format(
                        Locale.US,
                        "+%.3f V",
                        lab14FirstHardVoltage - voltageStart
                )
        );

    } else {

        logLabelValue(
                gr ? "Αρχική πτώση" : "Initial hard sag",
                String.format(Locale.US, "%.3f V", initialSag)
        );
    }

    logLabelValue(
            gr ? "Μέγιστη πτώση" : "Worst sag",
            String.format(Locale.US, "%.3f V", worstSag)
    );

    String behavior;

    if (voltageSettlingDetected) {

        behavior = gr
                ? "Αρχική σταθεροποίηση τάσης — πιθανή πρόσφατη φόρτιση, προηγούμενο φορτίο ή BMS refresh"
                : "Initial voltage settling — possible recent charging, previous load, or BMS refresh";

    } else if (!Float.isNaN(lab14LateHardVoltage) &&
            lab14LateHardVoltage > lab14LowestHardVoltage + 0.015f) {

        behavior = gr
                ? "Πρώιμη πτώση με μερική σταθεροποίηση / ανάκαμψη"
                : "Early sag with partial stabilization / rebound";

    } else if (!Float.isNaN(lab14LateHardVoltage) &&
            lab14LateHardVoltage < lab14FirstHardVoltage - 0.020f) {

        behavior = gr
                ? "Προοδευτική πτώση τάσης κατά τη διάρκεια του stress"
                : "Progressive voltage sag during stress";

    } else {

        behavior = gr
                ? "Σχετικά σταθερή τάση υπό φορτίο μετά την αρχική πτώση"
                : "Relatively stable voltage under load after initial drop";
    }

    if (voltageSettlingDetected) {

        logLabelWarnValue(
                gr ? "Συμπεριφορά" : "Behavior",
                behavior
        );

    } else {

        logLabelOkValue(
                gr ? "Συμπεριφορά" : "Behavior",
                behavior
        );
    }
}

//=============================================================
// LAB 15 - Charging System Diagnostic (SMART)
// FINAL / LOCKED — NO PATCHES — NO SIDE EFFECTS
//=============================================================
private void lab15ChargingSystemSmart() {

    final boolean gr = AppLang.isGreek(this);

    int percent = getBatteryPercentSafe();

    boolean badBat =
            percent < 20 || percent > 80;


    if (!lab15Running && !lab15PopupShown) {

        lab15PopupShown = true;

        showLab15ConditionCheck(() -> {
            lab15ChargingSystemSmart();
        });

        return;
    }

    if (lab15Running) {
        logWarn(gr
                ? "Το LAB 15 εκτελείται ήδη."
                : "LAB 15 already running.");
        return;
    }
    
    appendHtml("<br>");
logLine();

logInfo(
        gr
                ? "LAB 15 — Διαγνωστικός έλεγχος συστήματος φόρτισης"
                : "LAB 15 — Charging system diagnostic"
);

logLine();

// ================= FLAGS RESET =================

lab15Running  = true;
lab15Finished = false;
lab15FlapUnstable = false;
lab15OverTempDuringCharge = false;

lab15batTempStart = Float.NaN;
lab15batTempPeak  = Float.NaN;
lab15batTempEnd   = Float.NaN;

// reset LAB 15 charging strength state (FIELDS)
lab15_strengthKnown = false;
lab15_strengthWeak  = false;
lab15_systemLimited = false;

// ================= DIALOG =================

AlertDialog.Builder b =
new AlertDialog.Builder(
ManualTestsActivity.this,
android.R.style.Theme_Material_Dialog_NoActionBar
);
b.setCancelable(false);

// ============================================================
// GEL DARK + GOLD POPUP BACKGROUND LAB 15
// ============================================================
LinearLayout root = new LinearLayout(this);
root.setOrientation(LinearLayout.VERTICAL);
root.setPadding(dp(24), dp(20), dp(24), dp(18));

GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF101010);           // GEL dark black
bg.setCornerRadius(dp(10));       // smooth premium corners
bg.setStroke(dp(4), 0xFFFFD700);  // GOLD border
root.setBackground(bg);

// ============================================================
// LAB 15 — CHARGING MONITOR POPUP (GEL STYLE)
// ============================================================

// ---------------------------
// TITLE (WHITE)
// ---------------------------
TextView title = new TextView(this);
title.setText(
        gr
                ? "LAB 15 — Έλεγχος Φόρτισης Συσκευής"
                : "LAB 15 — Charging Behavior Test"
);
title.setTextColor(Color.WHITE);
title.setTextSize(18f);
title.setTypeface(null, Typeface.BOLD);
title.setGravity(Gravity.CENTER);
title.setPadding(0, 0, 0, dp(12));
root.addView(title);

// ---------------------------
// MAIN MESSAGE (NEON GREEN)
// ---------------------------

int batteryLevel = getBatteryPercentSafe();

if (batteryLevel > 80) {

    logLabelWarnValue(
            gr ? "Δοκιμή φόρτισης" : "Charging test",
            gr ? "Παραλείφθηκε — η μπαταρία είναι πάνω από 80%"
               : "Skipped — battery above 80%"
    );

    logLabelWarnValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Η δοκιμή φόρτισης είναι αξιόπιστη μόνο μεταξύ 20% και 80%."
                    : "Charging diagnostics are reliable only between 20% and 80% battery level."
    );

    lab15Running = false;
    lab15Finished = true;
    return;
    
    }

final String text =
        gr
                ? "Σύνδεσε τον φορτιστή στη θύρα φόρτισης της συσκευής.\n\n"
                  + "Το σύστημα θα παρακολουθεί τη συμπεριφορά φόρτισης\n"
                  + "για τα επόμενα 3 λεπτά.\n\n"
                  + "Κράτησε τη συσκευή συνδεδεμένη\n"
                  + "καθ’ όλη τη διάρκεια του τεστ."
                : "Connect the charger to the device’s charging port.\n\n"
                  + "The system will monitor charging behavior\n"
                  + "for the next 3 minutes.\n\n"
                  + "Please keep the device connected\n"
                  + "during the entire test.";
                  
TextView msg = new TextView(this);
msg.setText(text);
msg.setTextColor(0xFF39FF14);
msg.setTextSize(15f);
msg.setGravity(Gravity.CENTER);
msg.setLineSpacing(0f, 1.2f);
root.addView(msg);

// ---------------------------
// STATUS TEXT (RED)
// ---------------------------
lab15StatusText = new TextView(this);
lab15StatusText.setText(
        gr
                ? "Αναμονή για σύνδεση φορτιστή…"
                : "Waiting for charging connection…"
);
lab15StatusText.setTextColor(0xFFFF4444);
lab15StatusText.setTextSize(15f);
lab15StatusText.setGravity(Gravity.CENTER);
lab15StatusText.setPadding(0, dp(10), 0, 0);
root.addView(lab15StatusText);

// ---------------------------
// DOTS (NEON)
// ---------------------------
final TextView dotsView = new TextView(this);
dotsView.setText("•");
dotsView.setTextColor(0xFF39FF14);
dotsView.setTextSize(22f);
dotsView.setGravity(Gravity.CENTER);
root.addView(dotsView);

// ---------------------------
// COUNTER (NEON)
// ---------------------------
lab15CounterText = new TextView(this);
lab15CounterText.setText(
        gr
                ? "Πρόοδος: 0 / 180 δευτ."
                : "Progress: 0 / 180 sec"
);
lab15CounterText.setTextColor(0xFF39FF14);
lab15CounterText.setGravity(Gravity.CENTER);
root.addView(lab15CounterText);

// ---------------------------
// PROGRESS BAR (SEGMENTS)
// ---------------------------
lab15ProgressBar = new LinearLayout(this);
lab15ProgressBar.setOrientation(LinearLayout.HORIZONTAL);
lab15ProgressBar.setGravity(Gravity.CENTER);
lab15ProgressBar.setPadding(0, dp(8), 0, 0);

for (int i = 0; i < 6; i++) {
    View seg = new View(this);
    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(0, dp(10), 1f);
    lp.setMargins(dp(3), 0, dp(3), 0);
    seg.setLayoutParams(lp);
    seg.setBackgroundColor(0xFF333333);
    lab15ProgressBar.addView(seg);
}
root.addView(lab15ProgressBar);

// ---------------------------
// MUTE ROW (GLOBAL APP TTS)
// ---------------------------
root.addView(buildMuteRow());

// ==========================
// TTS — GLOBAL ENGINE
// ==========================
new Handler(Looper.getMainLooper()).postDelayed(() -> {

    if (!AppTTS.isMuted(this)) {

        AppTTS.ensureSpeak(this, text);

    }

}, 120);

// ============================================================
// EXIT BUTTON (LAB 15 — GEL STYLE)
// ============================================================

Button exitBtn = new com.gel.cleaner.GelButton(this);
exitBtn.setText(
        gr
                ? "Έξοδος τεστ"
                : "Exit test"
);
exitBtn.setAllCaps(false);
exitBtn.setTextColor(Color.WHITE);
exitBtn.setTypeface(null, Typeface.BOLD);

GradientDrawable exitBg = new GradientDrawable();
exitBg.setColor(0xFF8B0000);          // dark red
exitBg.setCornerRadius(dp(10));
exitBg.setStroke(dp(3), 0xFFFFD700);  // gold border
exitBtn.setBackground(exitBg);

LinearLayout.LayoutParams lpExit =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
        );
lpExit.setMargins(0, dp(14), 0, 0);
exitBtn.setLayoutParams(lpExit);

// ------------------------------------------------------------
// EXIT ACTION — STOP TTS (NO SHUTDOWN)
// ------------------------------------------------------------
exitBtn.setOnClickListener(v -> {

    // stop voice immediately (GLOBAL)
    try {
        AppTTS.stop();
    } catch (Throwable ignore) {}

    abortLab15ByUser();
});

// add LAST
root.addView(exitBtn);

// ============================================================
// SHOW DIALOG
// ============================================================

b.setView(root);
lab15Dialog = b.create();

if (lab15Dialog.getWindow() != null) {
    lab15Dialog.getWindow()
            .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
}

lab15Dialog.show();
com.gel.cleaner.GelButtonLayout.fitDialog(lab15Dialog);

iDoctorEngine eng = iDoctorEngine.get(this);

iDoctorEngine.BatterySnapshot snapStart =
        eng.readBatterySnapshot();

if (snapStart == null) {
    logError("Battery snapshot failed");
    return;
}

// ================= CORE LOOP =================

final long[] startTs = { -1 };
final boolean[] wasCharging = { false };
final long[] unplugTs = { -1 };
final String[] dotFrames = { "•", "• •", "• • •" };

// Snapshot at actual charging onset, not dialog open.
final long[] startMah = { -1L };

ui.post(new Runnable() {

    int dotStep = 0;
    int lastSeg = -1;

    @Override
    public void run() {

        if (!lab15Running || lab15Finished || isFinishing()) {
    ui.removeCallbacks(this);
    return;
}

        boolean chargingNow = isDeviceCharging();
        long now = SystemClock.elapsedRealtime();

        dotsView.setText(dotFrames[dotStep++ % dotFrames.length]);

// ------------------------------------------------------------
// CHARGING STATE TRACKING (robust)
// ------------------------------------------------------------
if (chargingNow) {

    // reset unplug timer
    unplugTs[0] = -1;

    if (!wasCharging[0]) {

        wasCharging[0] = true;
        startTs[0] = now;
        iDoctorEngine.BatterySnapshot chargeStart = eng.readBatterySnapshotLab();
        startMah[0] = chargeStart != null ? chargeStart.chargeNowMah : -1L;

        lab15batTempStart = getBatteryTemperature();
        lab15batTempPeak  = lab15batTempStart;

        lab15StatusText.setText(gr
                ? "Ανιχνεύθηκε κατάσταση φόρτισης."
                : "Charging state detected.");
        lab15StatusText.setTextColor(0xFF39FF14);

        logOk(gr
                ? "Κατάσταση φόρτισης ανιχνεύθηκε."
                : "Charging state detected.");
    }

} else if (wasCharging[0]) {

    if (unplugTs[0] < 0) {
        unplugTs[0] = now;
    }

    long unplugMs = now - unplugTs[0];

    // ignore short USB renegotiation glitches
    if (unplugMs < 2000) {
        if (lab15Running && !lab15Finished)
            ui.postDelayed(this, 500);
        return;
    }

    // real disconnect
    if (unplugMs >= 10000) {

        lab15FlapUnstable = true;
        lab15Finished = true;
        lab15Running  = false;

        lab15StatusText.setText(gr
                ? "Η φόρτιση διακόπηκε."
                : "Charging disconnected.");
        lab15StatusText.setTextColor(0xFFFF4444);

        logError(gr
                ? "Ο φορτιστής αποσυνδέθηκε για περισσότερο από 10 δευτερόλεπτα."
                : "Charger disconnected for more than 10 seconds.");
        logError(gr
                ? "Η δοκιμή φόρτισης ακυρώθηκε."
                : "Charging test aborted.");

        try {
            if (lab15Dialog != null && lab15Dialog.isShowing())
                lab15Dialog.dismiss();
        } catch (Throwable ignore) {}

        lab15Dialog = null;
        return;
    }
}

// temp peak tracking while charging
if (chargingNow) {
    float t = getBatteryTemperature();
    if (t > 0) {
        if (Float.isNaN(lab15batTempPeak) || t > lab15batTempPeak)
            lab15batTempPeak = t;
        if (t >= 45f) lab15OverTempDuringCharge = true;
    }
}

if (startTs[0] < 0) {
    ui.postDelayed(this, 500);
    return;
}

int elapsed = (int) ((now - startTs[0]) / 1000);
int shown   = Math.min(elapsed, LAB15_TOTAL_SECONDS);

lab15CounterText.setText(
        gr
                ? "Πρόοδος: " + shown + " / " + LAB15_TOTAL_SECONDS + " δευτ."
                : "Progress: " + shown + " / " + LAB15_TOTAL_SECONDS + " sec"
);

int seg = elapsed / 30;
if (seg != lastSeg) {
    lastSeg = seg;
    for (int i = 0; i < lab15ProgressBar.getChildCount(); i++) {
        lab15ProgressBar.getChildAt(i)
                .setBackgroundColor(i < seg ? 0xFF39FF14 : 0xFF333333);
    }
}

if (elapsed < LAB15_TOTAL_SECONDS) {
    if (lab15Running && !lab15Finished)
        ui.postDelayed(this, 1000);
    return;
}

// ================= FINAL =================

lab15Finished = true;
lab15Running  = false;

float tempNow = getBatteryTemperature();

if (!Float.isNaN(tempNow)) {
    lab15batTempEnd = tempNow;
}

// fallback αν δεν πήραμε end
if (Float.isNaN(lab15batTempEnd)) {
    lab15batTempEnd = lab15batTempPeak;
}

startBatteryTemp = lab15batTempStart;
endBatteryTemp   = lab15batTempEnd;

final float startBatteryTempFinal = startBatteryTemp;
final float endBatteryTempFinal   = getBatteryTempEngineSafe();

// ------------------------------------------------------------
// Battery temperature + thermal correlation
// ------------------------------------------------------------

logLabelOkValue(
        gr ? "Τελική θερμοκρασία μπαταρίας" : "End battery temperature",
        String.format(Locale.US, "%.1f°C", lab15batTempEnd)
);

// ------------------------------------------------------------
// Thermal correlation analysis
// ------------------------------------------------------------

logLab15ThermalCorrelation(
        lab15batTempStart,
        lab15batTempPeak,
        lab15batTempEnd
);

// ------------------------------------------------------------
// Thermal verdict
// ------------------------------------------------------------

float tempRef = lab15batTempEnd;

if (!Float.isNaN(lab15batTempPeak)) {
    tempRef = lab15batTempPeak;
}

float dtCharge =
        tempRef - lab15batTempStart;

if (lab15OverTempDuringCharge) {

    logLabelErrorValue(
            gr ? "Θερμοκρασία κατά την φορτιση"
               : "Temperature during charging",
            String.format(
                    Locale.US,
                    gr
                            ? "ΥΨΗΛΗ (ΔT +%.1f°C) — Ανιχνεύθηκε αυξημένη θερμοκρασία"
                            : "HOT (ΔT +%.1f°C) — Elevated temperature detected",
                    Math.max(0f, dtCharge)
            )
    );

} else {

    logLabelOkValue(
            gr ? "Θερμοκρασία κατά την φορτιση"
               : "Temperature during charging",
            String.format(
                    Locale.US,
                    gr
                            ? "OK (ΔT +%.1f°C) — Φυσιολογική θερμική συμπεριφορά"
                            : "OK (ΔT +%.1f°C) — Normal thermal behavior",
                    Math.max(0f, dtCharge)
            )
    );
}

// ------------------------------------------------------------
// Charging connection stability
// ------------------------------------------------------------

if (lab15FlapUnstable) {

    logLabelErrorValue(
            gr ? "Σταθερότητα σύνδεσης φόρτισης" : "Charging connection stability",
            gr
                    ? "Ασταθής — εντοπίστηκε επαναλαμβανόμενο plug/unplug"
                    : "Unstable — plug/unplug behavior detected"
    );

} else {

    logLabelOkValue(
            gr ? "Σταθερότητα σύνδεσης φόρτισης" : "Charging connection stability",
            gr
                    ? "Σταθερή — δεν ανιχνεύθηκε μη φυσιολογική επανασύνδεση"
                    : "Stable — no abnormal reconnect behavior"
    );
}

// ------------------------------------------------------------
// CHARGING COUNTER OBSERVATION — SAME SOURCE AND SESSION
// ------------------------------------------------------------
// Net pack charge gain is not charger input power or a PMIC/cable test.
iDoctorEngine.BatterySnapshot chargeEnd = eng.readBatterySnapshotLab();
long endMah = chargeEnd != null ? chargeEnd.chargeNowMah : -1L;
long observedMs = startTs[0] > 0 ? SystemClock.elapsedRealtime() - startTs[0] : -1L;
long deltaMah = startMah[0] > 0 && endMah > startMah[0] ? endMah - startMah[0] : -1L;
boolean counterValid = BatteryChargeEvidence.isValidChargeGain(
        startMah[0], endMah, observedMs, lab15FlapUnstable);
lab15_strengthKnown = counterValid;
lab15_strengthWeak = false; // A single charging session cannot validate charge health.
if (counterValid) {
    double minutes = observedMs / 60000.0d;
    logLabelValue(gr ? "Παρατηρημένη αύξηση φορτίου" : "Observed net battery charge gain",
            String.format(Locale.US, "+%d mAh in %.1f min (%.2f mAh/min)",
                    deltaMah, minutes, deltaMah / minutes));
} else {
    logLabelWarnValue(gr ? "Αύξηση φορτίου" : "Charge-counter delta",
            gr ? "ΜΗ ΔΙΑΘΕΣΙΜΗ — μη έγκυρη ή ανεπαρκής παρατήρηση"
               : "UNAVAILABLE — missing or insufficient observation");
}
BatteryEvidence osChargeTime = BatteryPublicCollector.collect(
        ManualTestsActivity.this).get("os_estimated_charge_time_ms");
if (osChargeTime.isObserved() && osChargeTime.value >= 0) {
    logLabelValue(gr ? "Εκτίμηση χρόνου φόρτισης Android" : "Android charge-time estimate",
            String.format(Locale.US, "%.1f min (OS estimate)",
                    osChargeTime.value / 60000.0d));
} else {
    logLabelValue(gr ? "Εκτίμηση Android" : "Android charge-time prediction",
            gr ? "Δεν είναι διαθέσιμη" : "Unavailable");
}
logLabelWarnValue(gr ? "Περιορισμός" : "Limit",
        gr ? "Η καθαρή μεταβολή φορτίου δεν αποδεικνύει βλάβη καλωδίου, "
               + "θύρας φόρτισης ή PMIC."
           : "Net battery charge gain does not establish cable, port or PMIC failure.");

// ------------------------------------------------------------
// // LAB 15 VERDICT — ONLY OBSERVED EVENTS
// ------------------------------------------------------------
appendHtml("<br>");
logInfo(gr ? "Τεκμηριωμένα ευρήματα φόρτισης" : "Observed charging findings");
if (lab15FlapUnstable) {
    logLabelWarnValue(gr ? "Σύνδεση" : "Connection",
            gr ? "Καταγράφηκε διακοπή κατά τη φόρτιση"
               : "Charging interruption observed");
}
if (lab15OverTempDuringCharge) {
    logLabelWarnValue(gr ? "Θερμοκρασία" : "Temperature",
            gr ? "Αναφέρθηκε υψηλή θερμοκρασία κατά τη φόρτιση"
               : "Elevated temperature observed while charging");
}
if (!lab15FlapUnstable && !lab15OverTempDuringCharge && counterValid) {
    logLabelValue(gr ? "Εκτέλεση δοκιμής" : "Test outcome",
            gr ? "Καταγράφηκε καθαρή αύξηση φορτίου χωρίς συμβάν των ελεγχόμενων τύπων"
               : "Net charge gain observed without the monitored event types");
} else if (!lab15FlapUnstable && !lab15OverTempDuringCharge) {
    logLabelWarnValue(gr ? "Εκτέλεση δοκιμής" : "Test outcome",
            gr ? "ΑΝΕΠΑΡΚΗ ΣΤΟΙΧΕΙΑ" : "INSUFFICIENT EVIDENCE");
}
logLabelWarnValue(gr ? "Τεχνικό συμπέρασμα" : "Diagnostic limit",
        gr ? "Απαιτείται ξεχωριστός έλεγχος φορτιστή/καλωδίου/θύρας/PMIC"
           : "Separate charger/cable/port/PMIC testing is required");
GELServiceLog.info("SUMMARY: CHARGING_OBSERVATION="
        + (lab15FlapUnstable ? "INTERRUPTED"
                : counterValid ? "NET_CHARGE_GAIN_OBSERVED" : "UNKNOWN"));
appendHtml("<br>");
logOk(gr ? "Το Lab 15 ολοκληρώθηκε με περιορισμούς."
         : "Lab 15 finished with stated limitations.");

// ------------------------------------------------------------
// // STORE RESULT FOR LAB 17 (LAB 15 OUTPUT)
// ------------------------------------------------------------
try {

    int chargeScore = -1; // A universal health percent is unsupported.

    p.edit()
            .putInt("lab15_evidence_schema", 1)
            .putBoolean("lab15_counter_delta_valid", counterValid)
            .putLong("lab15_observed_delta_mah", counterValid ? deltaMah : -1L)
            .putLong("lab15_observed_duration_ms", observedMs)
            .putInt("lab15_charge_score", chargeScore)
            .putBoolean("lab15_system_limited", lab15_systemLimited)
            .putBoolean("lab15_overtemp", lab15OverTempDuringCharge)
            .putString(
                    "lab15_strength_label",
                    counterValid ? "OBSERVED_CHARGE_GAIN" : "UNKNOWN"
            )
            .putLong("lab15_ts", System.currentTimeMillis())
            .apply();

} catch (Throwable ignore) {}

// ------------------------------------------------------------
// CLEAN EXIT — CLOSE POPUP
// ------------------------------------------------------------
try {
    if (lab15Dialog != null && lab15Dialog.isShowing())
        lab15Dialog.dismiss();
} catch (Throwable ignore) {}

lab15Dialog = null;

        } // END run()

    }); // END ui.post()

} // END lab15ChargingSystemSmart()

// ============================================================
// LAB 16 — Thermal Snapshot
// FINAL — COMPACT — GEL LOCKED
// ============================================================
private void lab16ThermalSnapshot() {

    final boolean gr = AppLang.isGreek(this);

    SharedPreferences p = getSharedPreferences("GEL_DIAG", MODE_PRIVATE);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 16 — Θερμικό Στιγμιότυπο"
            : "LAB 16 — Thermal Snapshot");
    logLine();

    List<ThermalEntry> internal     = buildThermalInternal();
    List<ThermalEntry> peripherals  = buildThermalPeripheralsCritical();

    float  peakTemp = -1f;
    String peakSrc  = "N/A";
    final int observedCount = internal.size() + peripherals.size();

    // ------------------------------------------------------------
    // BASIC + CRITICAL THERMALS
    // ------------------------------------------------------------
    logInfo(gr ? "Θερμικοί αισθητήρες:" : "Thermal sensors:");

    for (ThermalEntry t : internal) {

        logLabelOkValue(
                t.label,
                String.format(Locale.US, "%.1f°C", t.temp)
        );

        if (t.temp > peakTemp) {
            peakTemp = t.temp;
            peakSrc  = t.label;
        }
    }

    for (ThermalEntry t : peripherals) {

        logLabelOkValue(
                t.label,
                String.format(Locale.US, "%.1f°C", t.temp)
        );

        if (t.temp > peakTemp) {
            peakTemp = t.temp;
            peakSrc  = t.label;
        }
    }

    // Android thermal manager data complement OEM-exposed sensors; they are not
    // interchangeable physical quantities or temperatures of all components.
    BatteryPublicCollector.Snapshot osBattery =
            BatteryPublicCollector.collect(this);
    BatteryEvidence osThermalStatus = osBattery.get("thermal_status_enum");
    BatteryEvidence osThermalHeadroom = osBattery.get("thermal_headroom");
    BatteryEvidence osBatteryTemp = osBattery.get("temperature_c");

    appendHtml("<br>");
    logInfo(gr ? "Πρόσθετα στοιχεία θερμικής προστασίας Android:"
               : "Additional Android thermal-protection evidence:");
    if (osThermalStatus.isObserved()) {
        logLabelValue(
                gr ? "Κατάσταση θερμικής προστασίας λειτουργικού"
                   : "OS thermal status",
                String.format(Locale.US, "Android enum %.0f", osThermalStatus.value));
        if (osThermalStatus.value >= android.os.PowerManager.THERMAL_STATUS_SEVERE) {
            logLabelWarnValue(gr ? "Προστασία από υπερθέρμανση"
                                  : "Thermal protection",
                    gr ? "Το Android αναφέρει σοβαρό θερμικό περιορισμό"
                       : "Android reports severe system thermal stress");
        }
    } else {
        logLabelWarnValue(gr ? "Θερμική κατάσταση Android" : "Android thermal status",
                gr ? "Δεν διατίθεται" : "Unavailable");
    }
    if (osThermalHeadroom.isObserved()) {
        logLabelValue(gr ? "Πρόβλεψη θερμικού περιθωρίου"
                         : "OS thermal headroom prediction",
                String.format(Locale.US, "%.3f (OS estimate)", osThermalHeadroom.value));
    } else {
        logLabelValue(gr ? "Θερμικό περιθώριο" : "Thermal headroom",
                gr ? "Δεν διατίθεται ή περιορίστηκε από το Android"
                   : "Unavailable or rate-limited by Android");
    }
    if (osBatteryTemp.isObserved()) {
        logLabelValue(gr ? "Θερμοκρασία μπαταρίας — δημόσιο API"
                         : "Battery temperature — public API",
                String.format(Locale.US, "%.1f °C", osBatteryTemp.value));
    }
    logLabelWarnValue(gr ? "Περιορισμός" : "Limitation",
            gr ? "Η θερμική κατάσταση του Android δεν ταυτοποιεί βλάβη μπαταρίας ή συγκεκριμένου αισθητήρα."
               : "OS-wide thermal status does not isolate a battery or sensor hardware defect.");

    appendHtml("<br>");

    // ------------------------------------------------------------
    // SUMMARY
    // ------------------------------------------------------------
    boolean danger = peakTemp >= 55f;

    logInfo(gr ? "Θερμική σύνοψη:" : "Thermal summary:");
    logLine();

    if (observedCount == 0) {
        logLabelWarnValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Δεν υπάρχουν διαθέσιμες θερμικές μετρήσεις"
                   : "No thermal readings available"
        );
    } else if (danger) {
        logLabelWarnValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Ανιχνεύθηκε αυξημένη θερμοκρασία σε διαθέσιμο αισθητήρα"
                   : "Elevated reading detected from an available sensor"
        );
        logLabelWarnValue(
                gr ? "Αντίδραση συστήματος" : "System response",
                gr ? "Ενδέχεται να ενεργοποιηθεί θερμική προστασία"
                   : "Thermal protection may activate"
        );
    } else {
        logLabelOkValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Δεν εντοπίστηκε υψηλή ένδειξη στους διαθέσιμους αισθητήρες"
                   : "No high reading among available sensors"
        );
    }
    logLabelWarnValue(
            gr ? "Κάλυψη" : "Coverage",
            (gr ? "Μετρήσεις από " : "Readings from ") + observedCount
                    + (gr ? " διαθέσιμους αισθητήρες· δεν πιστοποιείται πλήρης κάλυψη"
                          : " available sensors; full hardware coverage is not verified")
    );

    // ------------------------------------------------------------
    // PEAK TEMPERATURE
    // ------------------------------------------------------------
    if (peakTemp > 0) {

        String peakText = String.format(
                Locale.US,
                "%.1f°C %s %s",
                peakTemp,
                gr ? "στο" : "at",
                peakSrc
        );

        if (peakTemp >= 55f) {

            logLabelErrorValue(
                    gr ? "Μέγιστη θερμοκρασία" : "Peak temperature",
                    peakText
            );

        } else if (peakTemp >= 45f) {

            logLabelWarnValue(
                    gr ? "Μέγιστη θερμοκρασία" : "Peak temperature",
                    peakText
            );

        } else {

            logLabelOkValue(
                    gr ? "Μέγιστη θερμοκρασία" : "Peak temperature",
                    peakText
            );
        }
    }

    // ------------------------------------------------------------
    // HIDDEN THERMAL SAFETY CHECK
    // ------------------------------------------------------------
    boolean hiddenRisk = detectHiddenThermalAnomaly(55f);

    if (hiddenRisk) {

        logLabelWarnValue(
                gr ? "Κρυφοί αισθητήρες" : "Hidden sensors",
                gr
                        ? "Ανιχνεύθηκε αυξημένη θερμοκρασία (μη εμφανιζόμενα στοιχεία)"
                        : "Elevated temperature detected (non-displayed components)"
        );

        logLabelWarnValue(
                gr ? "Κίνδυνος" : "Risk",
                gr
                        ? "Ενδέχεται να ενεργοποιηθούν μηχανισμοί θερμικής προστασίας"
                        : "Thermal protection mechanisms may activate"
        );

    } else {
        logLabelWarnValue(
                gr ? "Κρυφοί αισθητήρες" : "Hidden sensors",
                gr ? "Δεν τεκμηριώνεται κάλυψη μη προσβάσιμων αισθητήρων"
                   : "Unexposed sensors cannot be assessed"
        );
    }

    // ------------------------------------------------------------
    // THERMAL SCORE
    // ------------------------------------------------------------
    int thermalScore = 100;
    boolean thermalDanger = false;

    for (ThermalEntry t : internal) {
        if (t.temp >= 55f) {
            thermalScore -= 25;
            thermalDanger = true;
        } else if (t.temp >= 45f) {
            thermalScore -= 10;
        }
    }

    for (ThermalEntry t : peripherals) {
        if (t.temp >= 55f) {
            thermalScore -= 25;
            thermalDanger = true;
        } else if (t.temp >= 45f) {
            thermalScore -= 10;
        }
    }

    thermalDanger = thermalDanger || hiddenRisk
            || (osThermalStatus.isObserved()
                && osThermalStatus.value >= android.os.PowerManager.THERMAL_STATUS_SEVERE);
    thermalScore = ThermalEvidencePolicy.score(internal.size(), peripherals.size(), thermalScore, hiddenRisk);
    // This is a contextual, uncalibrated observation score; OS thermal stress
    // is recorded separately and cannot certify physical sensor health.

    try {
        p.edit()
                .putInt("lab16_thermal_score", thermalScore)
                .putBoolean("lab16_thermal_danger", thermalDanger)
                .putFloat("lab16_peak_temp", peakTemp)
                .putString("lab16_peak_source", peakSrc)
                .putLong("lab16_last_ts", System.currentTimeMillis())
                .apply();
    } catch (Throwable ignore) {}

    if (thermalScore < 0) {
        logLabelWarnValue(
                gr ? "Αξιολόγηση θερμικής συμπεριφοράς" : "Thermal behaviour rating",
                gr ? "ΑΝΕΠΑΡΚΗ ΔΕΔΟΜΕΝΑ" : "INSUFFICIENT DATA"
        );
    } else {
        String tLabel = thermalScore >= 80
                ? (gr ? "Εντός παρατηρημένων ορίων" : "Within observed limits")
                : thermalScore >= 60
                ? (gr ? "Μέτρια" : "Moderate")
                : (gr ? "Υψηλή" : "High");
        logLabelOkValue(
                gr ? "Αξιολόγηση θερμικής συμπεριφοράς" : "Thermal behaviour rating",
                String.format(Locale.US, "%s (%d)", tLabel, thermalScore)
        );
    }
    GELServiceLog.info(
            "SUMMARY: THERMAL_PATTERN="
                    + ThermalEvidencePolicy.pattern(thermalScore, thermalDanger)
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 16 ολοκληρώθηκε." : "Lab 16 finished.");
    logLine();
}

// ============================================================
// LAB 17 — GEL Auto Battery Reliability Evaluation
// INTELLIGENCE EDITION • STRICT FRESHNESS (<= 2 HOURS)
// ============================================================
private void lab17RunAuto() {
    final boolean gr = AppLang.isGreek(this);
    final long windowMs = 2L * 60L * 60L * 1000L;
    final long now = System.currentTimeMillis();
    SharedPreferences p = getSharedPreferences("GEL_DIAG", MODE_PRIVATE);

    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 17 — Συνδυαστική Αναφορά Τεκμηρίων Μπαταρίας"
               : "LAB 17 — Combined Battery Evidence Review");
    logLine();

    BatteryEvidenceStore.Record lab14 = BatteryEvidenceStore.readLatestLab14(this);
    boolean has14 = lab14 != null && lab14.isCompletedValidRun(windowMs);
    if (has14) {
        logLabelValue("Lab 14A",
                lab14.observedSignalCount
                        + (gr ? " παρατηρημένα δημόσια σήματα" : " observed public API signals")
                        + " | run=" + lab14.runId);
    } else {
        logLabelWarnValue("Lab 14A",
                gr ? "Δεν υπάρχει πρόσφατη, ολοκληρωμένη έγκυρη δοκιμή"
                   : "No fresh, completed and valid test evidence");
    }

    boolean source14b = p.getInt("lab14b_evidence_schema", 0) == 1
            && "BATTERY_MANAGER_CHARGE_COUNTER_ELAPSED".equals(
                    p.getString("lab14b_rate_source", ""))
            && p.getBoolean("lab14b_rate_valid", false);
    long ts14b = p.getLong("lab14b_ts", 0L);
    long duration14b = p.getLong("lab14b_sample_duration_ms", 0L);
    float draw14b = p.getFloat("lab14b_consumption_per_hour", -1f);
    boolean has14b = source14b && duration14b >= 240000L
            && ts14b > 0L && now >= ts14b && now - ts14b <= windowMs
            && Float.isFinite(draw14b) && draw14b > 0f;
    if (has14b) {
        logLabelValue("Lab 14B", String.format(Locale.US, "%.1f mAh/h | %.1f min",
                draw14b, duration14b / 60000.0d));
        logLabelWarnValue(gr ? "Ερμηνεία εκφόρτισης" : "Discharge interpretation",
                gr ? "Μέση κατανάλωση στο δοκιμαστικό φορτίο, όχι υγεία μπαταρίας"
                   : "Measured average draw in test workload, not battery health");
    } else {
        logLabelWarnValue("Lab 14B",
                gr ? "Δεν υπάρχει πρόσφατο έγκυρο δείγμα charge counter"
                   : "No fresh validated charge-counter sample");
    }

    long ts15 = p.getLong("lab15_ts", 0L);
    long sample15 = p.getLong("lab15_observed_duration_ms", 0L);
    long delta15 = p.getLong("lab15_observed_delta_mah", -1L);
    boolean has15 = p.getInt("lab15_evidence_schema", 0) == 1
            && p.getBoolean("lab15_counter_delta_valid", false)
            && ts15 > 0L && now >= ts15 && now - ts15 <= windowMs
            && sample15 >= 120000L && delta15 > 0L;
    if (has15) {
        logLabelValue("Lab 15", "+" + delta15 + " mAh"
                + String.format(Locale.US, " | %.1f min", sample15 / 60000.0d));
        logLabelWarnValue(gr ? "Ερμηνεία φόρτισης" : "Charge interpretation",
                gr ? "Αύξηση φορτίου συστοιχίας, όχι πιστοποίηση καλωδίου/PMIC"
                   : "Net pack charge gain, not charger/PMIC certification");
    } else {
        logLabelWarnValue("Lab 15",
                gr ? "Δεν υπάρχει πρόσφατο έγκυρο δείγμα φόρτισης"
                   : "No fresh validated charging sample");
    }

    long ts16 = p.getLong("lab16_last_ts", 0L);
    float peak16 = p.getFloat("lab16_peak_temp", Float.NaN);
    boolean has16 = ts16 > 0L && now >= ts16 && now - ts16 <= windowMs
            && Float.isFinite(peak16) && peak16 >= -20f && peak16 <= 120f;
    if (has16) {
        String source16 = p.getString("lab16_peak_source", "unknown");
        logLabelValue("Lab 16",
                String.format(Locale.US, "Reported peak %.1f °C | %s",
                        peak16, source16));
        logLabelWarnValue(gr ? "Θερμική κάλυψη" : "Thermal coverage",
                gr ? "Η μέγιστη ορατή ένδειξη δεν εγγυάται πρόσβαση σε όλα τα θερμικά στοιχεία"
                   : "Highest exposed reading does not prove whole-device thermal coverage");
    } else {
        logLabelWarnValue("Lab 16",
                gr ? "Δεν υπάρχει πρόσφατη επαληθεύσιμη θερμική ένδειξη"
                   : "No fresh, source-bound thermal reading");
    }

    BatteryPublicCollector.Snapshot live = BatteryPublicCollector.collect(this);
    for (String key : new String[]{"soc_percent", "voltage_mv", "current_now_ma",
            "remaining_charge_mah", "remaining_energy_mwh", "temperature_c",
            "cycle_count", "thermal_status_enum", "thermal_headroom",
            "os_estimated_discharge_time_ms"}) {
        BatteryEvidence item = live.get(key);
        if (item.isObserved()) {
            logLabelValue(key, String.format(Locale.US, "%.2f %s (%s)",
                    item.value, item.unit, item.source));
        } else {
            logLabelValue(key, gr ? "Μη διαθέσιμο" : "Unavailable");
        }
    }

    int availableDomains = (has14 ? 1 : 0) + (has14b ? 1 : 0)
            + (has15 ? 1 : 0) + (has16 ? 1 : 0);
    appendHtml("<br>");
    logInfo(gr ? "Συμπέρασμα επάρκειας τεκμηρίων"
               : "Diagnostic evidence coverage conclusion");
    logLabelValue(gr ? "Πρόσφατες ενότητες" : "Fresh evidence domains",
            availableDomains + "/4");
    if (availableDomains == 0) {
        logLabelWarnValue(gr ? "Διαθεσιμότητα" : "Availability",
                gr ? "ΑΝΕΠΑΡΚΗ ΔΕΔΟΜΕΝΑ — δεν επιτρέπεται συνολική διάγνωση"
                   : "INSUFFICIENT DATA — combined diagnosis unavailable");
    } else {
        logLabelValue(gr ? "Διαθεσιμότητα" : "Availability",
                gr ? "Υπάρχουν μερικά τεκμηριωμένα σήματα προς τεχνική αξιολόγηση"
                   : "Scoped observations available for technician review");
    }
    logLabelWarnValue(gr ? "Υγεία/γήρανση μπαταρίας" : "Battery capacity health / aging",
            gr ? "ΜΗ ΕΠΑΛΗΘΕΥΣΙΜΗ — καμία συνδυαστική βαθμολογία 0–100"
               : "NOT VERIFIED — no numerical 0–100 health score");
    logLabelWarnValue(gr ? "Πιστοποίηση" : "Certification",
            gr ? "Η αναφορά δεν πιστοποιεί φυσική κατάσταση μπαταρίας ή συσκευής"
               : "This review does not certify physical battery or device condition");

    // Legacy synthetic scores are not carried forward, including on upgrades.
    p.edit()
            .remove("lab17_final_score")
            .remove("lab17_category")
            .putInt("lab17_evidence_schema", 1)
            .putInt("lab17_available_domains", availableDomains)
            .putLong("lab17_ts", now)
            .apply();
    logOk(gr ? "Το Lab 17 ολοκλήρωσε την αναφορά τεκμηρίων."
             : "Lab 17 evidence report completed.");
    logLine();
} // ===== END lab17RunAuto()


// ============================================================
// LAB 17 — POPUP (GEL DARK + GOLD)
// AppLang + AppTTS + GLOBAL MUTE
// ============================================================
private void lab17_showPopup(String titleText, String msgText) {

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(true);

    // ROOT
    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(20), dp(24), dp(20));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    // TITLE
    TextView title = new TextView(this);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(17f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

    // MESSAGE (HTML + colors)
    TextView msg = new TextView(this);
    msg.setText(
            Html.fromHtml(
                    msgText,
                    Html.FROM_HTML_MODE_LEGACY
            )
    );
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(14.5f);
    msg.setLineSpacing(0f, 1.2f);
    msg.setGravity(Gravity.CENTER);
    msg.setPadding(0, 0, 0, dp(18));
    root.addView(msg);

    // MUTE ROW
    root.addView(buildMuteRow());

    // EXIT BUTTON
    Button exit = gelButton(
            this,
            gr ? "Έξοδος" : "Exit",
            0xFF8B0000
    );

    LinearLayout.LayoutParams lpExit =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(52)
            );
    lpExit.setMargins(0, dp(10), 0, 0);
    exit.setLayoutParams(lpExit);
    root.addView(exit);

    // BUILD DIALOG
    b.setView(root);
    AlertDialog popup = b.create();

    if (popup.getWindow() != null) {
        popup.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    popup.show();
    com.gel.cleaner.GelButtonLayout.fitDialog(popup);

// 🔴 FIX: STOP TTS ON BACK / DISMISS
popup.setOnDismissListener(d -> AppTTS.stop());
popup.setOnCancelListener(d -> AppTTS.stop());

// TTS
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (popup.isShowing() && !AppTTS.isMuted(this)) {

        String speakText =
                    gr
                            ? "Δεν πληρούνται οι προϋποθέσεις για το LAB δεκαεπτά. "
                              + "Απαιτούνται πρόσφατα αποτελέσματα από τα LAB δεκατέσσερα άλφα, "
                              + " δεκαπέντε και δεκαέξι."
                            : "Requirements for LAB seventeen are not met. "
                              + "Recent results from LAB fourteen alpha, "
                              + "LAB fifteen and LAB sixteen are required.";

            AppTTS.ensureSpeak(this, speakText);
        }
    }, 120);

    // EXIT ACTION
    exit.setOnClickListener(v -> {
        AppTTS.stop();
        try { popup.dismiss(); } catch (Throwable ignore) {}
    });
}


// ============================================================
// LAB 17 — AGE FORMATTER
// ============================================================
private String lab17_age(long deltaMs) {
    if (deltaMs < 0) deltaMs = 0;

    long sec = deltaMs / 1000L;
    long min = sec / 60L;
    long hr = min / 60L;

    if (hr > 0) {
        long rm = min % 60L;
        return hr + "h " + rm + "m ago";
    }

    if (min > 0) return min + "m ago";

    return Math.max(0, sec) + "s ago";
}

// ============================================================
// LABS 18 - 21: STORAGE & PERFORMANCE
// ============================================================

// ============================================================
// LAB 18 — STORAGE HEALTH INSPECTION
// FINAL • HUMAN READABLE • ROOT AWARE • GEL LOCKED
// ============================================================
private void lab18StorageSnapshot() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 18 — Έλεγχος Υγείας Εσωτερικού Αποθηκευτικού Χώρου"
            : "LAB 18 — Internal Storage Health Inspection");
    logLine();

    try {

        StatFs s = new StatFs(Environment.getDataDirectory().getAbsolutePath());

        long blockSize = s.getBlockSizeLong();
        long total     = s.getBlockCountLong() * blockSize;
        long free      = s.getAvailableBlocksLong() * blockSize;
        long used      = total - free;

        int pctFree = (int) ((free * 100L) / Math.max(1L, total));
        int pctUsed = 100 - pctFree;

        // ------------------------------------------------------------
        // BASIC SNAPSHOT
        // ------------------------------------------------------------
        logInfo(gr ? "Χρήση αποθηκευτικού χώρου:" : "Storage usage:");
        logLabelOkValue(
                gr ? "Χρήση" : "Usage",
                humanBytes(used) + (gr ? " χρησιμοποιούνται / " : " used / ") +
                humanBytes(total) +
                (gr
                        ? " (ελεύθερα " + humanBytes(free) + ", " + pctFree + "%)"
                        : " (free " + humanBytes(free) + ", " + pctFree + "%)")
        );

        // ------------------------------------------------------------
        // PRESSURE LEVEL (HUMAN SCALE)
        // ------------------------------------------------------------
        boolean critical = pctFree < 7;
        boolean pressure = pctFree < 15;

        appendHtml("<br>");
        logInfo(gr
                ? "Αξιολόγηση πίεσης αποθηκευτικού χώρου:"
                : "Storage pressure assessment:");
                logLine();

        if (critical) {

            logLabelErrorValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Κρίσιμα χαμηλός διαθέσιμος χώρος"
                       : "Critically low storage"
            );
            logLabelErrorValue(
                    gr ? "Επίπτωση" : "Impact",
                    gr ? "Η σταθερότητα του συστήματος μπορεί να επηρεαστεί"
                       : "System stability may be affected"
            );
            logLabelWarnValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "Πιθανά κρασαρίσματα εφαρμογών, αποτυχία ενημερώσεων ή επιβράδυνση UI"
                       : "Apps may crash, updates may fail, UI may slow down"
            );

        } else if (pressure) {

            logLabelWarnValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Ο αποθηκευτικός χώρος βρίσκεται υπό πίεση"
                       : "Storage under pressure"
            );
            logLabelWarnValue(
                    gr ? "Επίπτωση" : "Impact",
                    gr ? "Το σύστημα μπορεί να επιβραδύνεται σε λειτουργίες αρχείων"
                       : "System may feel slower during file operations"
            );

        } else {

            logLabelOkValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Υγιές επίπεδο αποθηκευτικού χώρου για καθημερινή χρήση"
                       : "Healthy storage level for daily usage"
            );
        }

        // ------------------------------------------------------------
        // ------------------------------------------------------------
        // PHYSICAL STORAGE WEAR IS NOT OBSERVABLE FROM FREE-SPACE PRESSURE.
        // Kernel/firmware NAND erase counts are OEM-specific and generally
        // unavailable to ordinary apps. Never turn a capacity percent into
        // a flash-health, filesystem-corruption or controller-failure score.
        // ------------------------------------------------------------
        appendHtml("<br>");
        logLabelWarnValue(
                gr ? "Φυσική φθορά NAND / Controller"
                   : "NAND / controller physical wear",
                gr ? "ΜΗ ΕΠΑΛΗΘΕΥΣΙΜΗ από τον διαθέσιμο χώρο"
                   : "NOT VERIFIABLE from storage capacity readings");

        // Separate a real, bounded functional I/O test from physical wear.
        // Run off the UI thread; use only the app's private cache directory.
        final java.io.File lab18CacheDir = getCacheDir();
        new Thread(() -> {
            StorageEvidenceProbe.Result probe = StorageEvidenceProbe.run(lab18CacheDir);
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                if (probe.completed && probe.checksumMatched) {
                    logLabelOkValue(
                            gr ? "Έλεγχος αρχείων εφαρμογής" : "App-file integrity check",
                            (gr ? "Επιτυχής ανάγνωση/εγγραφή " : "Read/write verified for ")
                                    + (probe.bytes / 1024) + " KiB"
                                    + " | write+sync " + probe.writeAndSyncMs + " ms"
                                    + " | read " + probe.readMs + " ms");
                } else {
                    logLabelWarnValue(
                            gr ? "Έλεγχος αρχείων εφαρμογής" : "App-file integrity check",
                            gr ? "Μη επιβεβαιωμένος: " + probe.reason
                               : "Not verified: " + probe.reason);
                }
                if (!probe.cleanedUp) {
                    logLabelWarnValue(
                            gr ? "Προσωρινό αρχείο" : "Temporary test file",
                            gr ? "Αποτυχία αυτόματης διαγραφής" : "Cleanup failed");
                }
                logLabelValue(
                        gr ? "Περιορισμός ελέγχου" : "Test limitation",
                        gr ? "Το checksum ελέγχει μόνο το προσωρινό αρχείο εφαρμογής, όχι φθορά NAND."
                           : "The checksum checks an app-private file, not NAND lifetime.");
                logOk(gr ? "Ο λειτουργικός έλεγχος Lab 18 ολοκληρώθηκε."
                         : "Lab 18 functional I/O check finished.");
            });
        }, "GEL-Lab18-IO").start();

        // ------------------------------------------------------------
        // FINAL HUMAN SUMMARY
        // ------------------------------------------------------------
        appendHtml("<br>");
        logInfo(gr ? "Σύνοψη αποθηκευτικού χώρου:" : "Storage summary:");
        logLine();

        if (critical) {
            logLabelErrorValue(
                    gr ? "Ενέργεια" : "Action",
                    gr
                            ? "Συνιστάται άμεσος καθαρισμός"
                            : "Immediate cleanup strongly recommended"
            );
        } else if (pressure) {
            logLabelWarnValue(
                    gr ? "Ενέργεια" : "Action",
                    gr
                            ? "Συνιστάται καθαρισμός για αποκατάσταση απόδοσης"
                            : "Cleanup recommended to restore performance"
            );
        } else {
            logLabelOkValue(
                    gr ? "Ενέργεια" : "Action",
                    gr ? "Δεν απαιτείται ενέργεια"
                       : "No action required"
            );
        }

        appendHtml("<br>");
        logInfo(gr ? "Η αποτύπωση χώρου ολοκληρώθηκε· εκτελείται έλεγχος checksum."
                   : "Space snapshot finished; bounded checksum check running.");
        logLine();

    } catch (Throwable ignore) {
        // silent
    }
}

// ============================================================
// LAB 19 — Live RAM Health Snapshot
// FINAL — HUMAN • REAL-TIME • ROOT-AWARE • NO GUESSING
//
//  Instant snapshot (not stress / not forecast)
//  Explains what the system is doing NOW
//  Root-aware (extra insight, never fake)
//  No cleaning myths, no placebo claims
// ============================================================
private void lab19RamSnapshot() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 19 — Ζωντανό Στιγμιότυπο Υγείας RAM"
            : "LAB 19 — Live RAM Health Snapshot");
    logLine();

    try {

        ActivityManager am =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        if (am == null) {
            logLabelErrorValue(
                    gr ? "Υπηρεσία" : "Service",
                    gr ? "Η υπηρεσία μνήμης δεν είναι διαθέσιμη"
                       : "Memory service not available"
            );
            return;
        }

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        // availMem includes reclaimable memory; it is NOT literally unused RAM.
        long total = mi.totalMem;
        long free  = mi.availMem;
        if (total <= 0L || free < 0L || free > total) {
            logLabelWarnValue(gr ? "Ποιότητα στοιχείων" : "Evidence validity",
                    gr ? "Το Android επέστρεψε μη έγκυρη χωρητικότητα RAM."
                       : "Android returned an invalid RAM capacity snapshot.");
            return;
        }
        long used = total - free; // approximate non-available, NOT proven active app use
        int pctFree = (int) (free * 100.0d / total);

        // ------------------------------------------------------------
        // BASIC SNAPSHOT
        // ------------------------------------------------------------
        logInfo(gr ? "Τρέχουσα χρήση RAM:" : "Current RAM usage:");
        logLabelOkValue(
                gr ? "Μη διαθέσιμη / δεσμευμένη (προσέγγιση)" : "Non-available/committed (approx.)",
                humanBytes(used) +
                        (gr ? " κατά προσέγγιση / " : " approximate / ") +
                        humanBytes(total) +
                        (gr
                                ? " (διαθέσιμα " + humanBytes(free) + ", " + pctFree + "%)"
                                : " (available " + humanBytes(free) + ", " + pctFree + "%)")
        );

        // ------------------------------------------------------------
        // PUBLIC ANDROID MEMORY PRESSURE THRESHOLD & OWN-APP FOOTPRINT
        // ------------------------------------------------------------
        logLabelValue(gr ? "Πραγματικά διαθέσιμη RAM" : "OS-available RAM",
                humanBytes(free) + " (" + pctFree + "%)");
        if (mi.threshold > 0L) {
            logLabelValue(gr ? "Όριο low-memory του Android" : "Android low-memory threshold",
                    humanBytes(mi.threshold));
            long margin = free - mi.threshold;
            logLabelValue(gr ? "Περιθώριο πάνω από το όριο" : "Headroom above OS threshold",
                    margin >= 0 ? humanBytes(margin)
                            : (gr ? "Κάτω κατά " : "Below by ") + humanBytes(-margin));
        } else {
            logLabelWarnValue(gr ? "Όριο low-memory" : "Low-memory threshold",
                    gr ? "Δεν είναι διαθέσιμο" : "Unavailable");
        }
        if (Build.VERSION.SDK_INT >= 34 && mi.advertisedMem > 0L) {
            logLabelValue(gr ? "RAM που αναφέρει το Android για το μοντέλο"
                             : "Android-advertised RAM",
                    humanBytes(mi.advertisedMem));
        }

        // Debug.getMemoryInfo is scoped to our OWN process.
        try {
            android.os.Debug.MemoryInfo ownMemory = new android.os.Debug.MemoryInfo();
            android.os.Debug.getMemoryInfo(ownMemory);
            int pssKb = ownMemory.getTotalPss();
            if (pssKb >= 0) {
                logLabelValue(gr ? "Μνήμη διεργασίας GEL iDoctor (PSS)"
                                 : "GEL iDoctor process PSS",
                        humanBytes(pssKb * 1024L));
            }
            String privateJavaKb = ownMemory.getMemoryStat("summary.java-heap");
            if (privateJavaKb != null) {
                try {
                    long kb = Long.parseLong(privateJavaKb);
                    if (kb >= 0) {
                        logLabelValue(gr ? "Java heap της εφαρμογής" : "App Java heap",
                                humanBytes(kb * 1024L));
                    }
                } catch (NumberFormatException ignored) {
                    // Unparseable provider value is unknown, not zero.
                }
            }
        } catch (RuntimeException ignored) {
            logLabelWarnValue(gr ? "Μνήμη iDoctor" : "iDoctor memory",
                    gr ? "Δεν ήταν διαθέσιμη" : "Unavailable");
        }
        logLabelWarnValue(gr ? "Ερμηνεία RAM" : "RAM interpretation",
                gr ? "Η διαθέσιμη μνήμη και η χρήση της εφαρμογής δεν είναι έλεγχος "
                       + "αστοχίας φυσικών κυψελών RAM."
                   : "Available RAM and own-process footprint do not test physical RAM-cell integrity.");

        // ------------------------------------------------------------
        // HUMAN INTERPRETATION
        // ------------------------------------------------------------
        appendHtml("<br>");
        logInfo(gr ? "Αξιολόγηση πίεσης RAM:" : "RAM pressure assessment:");
        logLine();

        RamEvidencePolicy.Assessment ramAssessment =
                RamEvidencePolicy.assess(pctFree, mi.lowMemory);
        switch (ramAssessment) {
            case ANDROID_LOW_MEMORY:
                logLabelWarnValue(
                        gr ? "Κατάσταση" : "Status",
                        gr ? "Το Android αναφέρει κατάσταση χαμηλής μνήμης"
                           : "Android reports a low-memory condition"
                );
                break;
            case LOW_AVAILABLE_RAM:
                logLabelWarnValue(
                        gr ? "Κατάσταση" : "Status",
                        gr ? "Μικρό διαθέσιμο περιθώριο RAM — χωρίς σήμα low-memory από Android"
                           : "Low available RAM headroom — no Android low-memory signal"
                );
                break;
            case NO_OS_LOW_MEMORY_SIGNAL:
                logLabelOkValue(
                        gr ? "Κατάσταση" : "Status",
                        gr ? "Δεν αναφέρθηκε κατάσταση low-memory από Android"
                           : "Android has not reported a low-memory condition"
                );
                break;
            default:
                logLabelWarnValue(
                        gr ? "Κατάσταση" : "Status",
                        gr ? "Μη επαρκή στοιχεία για πίεση RAM"
                           : "Insufficient RAM pressure evidence"
                );
        }
        logLabelWarnValue(
                gr ? "Όρια ελέγχου" : "Assessment limits",
                gr ? "Δεν μετρήθηκαν καθυστέρηση UI, κλεισίματα εφαρμογών ή ποιότητα multitasking"
                   : "UI lag, app terminations and multitasking quality were not measured"
        );

        // ------------------------------------------------------------
        // MEMORY PRESSURE INDICATORS (LOW-LEVEL)
        // ------------------------------------------------------------
        try {

            MemSnapshot snap = readMemSnapshotSafe();

            long swapUsedKb = 0;
            if (snap.swapTotalKb > 0 && snap.swapFreeKb >= 0) {
                swapUsedKb = Math.max(0, snap.swapTotalKb - snap.swapFreeKb);
            }

            String pressureLevel =
                    pressureLevel(
                            snap.memFreeKb,
                            snap.cachedKb,
                            swapUsedKb
                    );

            String pressureHuman =
                    humanPressureLabel(pressureLevel);

            String zramDep =
                    zramDependency(swapUsedKb, total);

            appendHtml("<br>");
            logInfo(gr
                    ? "Δείκτες πίεσης μνήμης:"
                    : "Memory pressure indicators:");
                    logLine();

            logLabelOkValue(
                    gr ? "Επίπεδο πίεσης" : "Pressure level",
                    pressureHuman
            );

            logLabelOkValue(
                    "ZRAM / Swap dependency",
                    zramDep
            );

            if (swapUsedKb > 0) {
                logLabelWarnValue(
                        gr ? "Χρήση Swap" : "Swap used",
                        humanBytes(swapUsedKb * 1024L)
                );
            }

            if (snap.memFreeKb > 0) {
                logLabelOkValue(
                        "MemFree",
                        humanBytes(snap.memFreeKb * 1024L)
                );
            }

            if (snap.cachedKb > 0) {
                logLabelOkValue(
                        "Cached",
                        humanBytes(snap.cachedKb * 1024L) +
                                (gr ? " (επανακτήσιμη)" : " (reclaimable)")
                );
            }

        } catch (Throwable ignore) {}

        // ------------------------------------------------------------
        // ANDROID LOW-MEMORY SIGNAL
        // ------------------------------------------------------------
        if (mi.lowMemory) {

            appendHtml("<br>");
            logLabelWarnValue(
                    gr ? "Σήμα Android" : "Android signal",
                    gr ? "Αναφέρθηκε κατάσταση low-memory"
                       : "Low-memory state reported"
            );
            logLabelWarnValue(
                    gr ? "Αντίδραση συστήματος" : "System response",
                    gr ? "Ενεργοί μηχανισμοί προστασίας μνήμης"
                       : "Memory protection mechanisms active"
            );
        }

        // ------------------------------------------------------------
        // ROOT-AWARE INTELLIGENCE
        // ------------------------------------------------------------
        boolean rooted = isDeviceRooted();

        if (rooted) {

            appendHtml("<br>");
            logInfo(gr
                    ? "Προχωρημένη ανάλυση RAM (root access):"
                    : "Advanced RAM analysis (root access):");
                    logLine();

            boolean zramActive = isZramActiveSafe();
            boolean swapActive = isSwapActiveSafe();

            if (zramActive || swapActive) {

                logLabelWarnValue(
                        gr ? "Επέκταση μνήμης" : "Memory extension",
                        gr ? "Εντοπίστηκε συμπίεση / swap"
                           : "Compression / swap detected"
                );
                logLabelOkValue(
                        gr ? "Επίδραση" : "Effect",
                        gr
                                ? "Βελτιώνει τη σταθερότητα αλλά μπορεί να μειώσει την απόδοση"
                                : "Improves stability but may reduce performance"
                );

            } else {

                logLabelOkValue(
                        gr ? "Επέκταση μνήμης" : "Memory extension",
                        gr
                                ? "Δεν εντοπίστηκε swap ή συμπίεση"
                                : "No swap or compression detected"
                );
            }

            long cachedKb = readCachedMemoryKbSafe();
            if (cachedKb > 0) {
                logLabelOkValue(
                        gr ? "Cached μνήμη" : "Cached memory",
                        humanBytes(cachedKb * 1024L) +
                                (gr ? " (επανακτήσιμη από το σύστημα)"
                                   : " (reclaimable by system)")
                );
            }
        }

    } catch (Throwable t) {

        logLabelErrorValue(
                gr ? "Στιγμιότυπο RAM" : "RAM snapshot",
                gr ? "Αποτυχία ανάγνωσης κατάστασης μνήμης"
                   : "Failed to read memory state"
        );
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 19 ολοκληρώθηκε." : "Lab 19 finished.");
    logLine();
}

// ============================================================
// LAB 20 — Uptime & Reboot Intelligence
// FINAL — HUMAN • ROOT-AWARE • NO BULLSHIT
// ============================================================
private void lab20UptimeHints() {

    final boolean gr = AppLang.isGreek(this);


    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 20 — Χρόνος Λειτουργίας Συστήματος & Συμπεριφορά Επανεκκινήσεων"
            : "LAB 20 — System Uptime & Reboot Behaviour");
    logLine();

    try {

        long upMs = SystemClock.elapsedRealtime();
        String upStr = formatUptime(upMs);

        logInfo(gr ? "Χρόνος λειτουργίας συστήματος:" : "System uptime:");
        logLabelOkValue("Uptime", upStr);

        boolean veryRecentReboot =
                upMs < 2L * 60L * 60L * 1000L;        // < 2 hours
        boolean veryLongUptime =
                upMs > 7L * 24L * 60L * 60L * 1000L; // > 7 days
        boolean extremeUptime =
                upMs > 14L * 24L * 60L * 60L * 1000L;

        // ----------------------------------------------------
        // HUMAN INTERPRETATION (NON-ROOT)
        // ----------------------------------------------------
        appendHtml("<br>");
        logInfo(gr ? "Αξιολόγηση uptime:" : "Uptime assessment:");
        logLine();

        if (veryRecentReboot) {

            logLabelWarnValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Η τρέχουσα εκκίνηση έγινε τις τελευταίες 2 ώρες"
                       : "Current boot began within the last 2 hours"
            );
            logLabelWarnValue(
                    gr ? "Επίπτωση" : "Impact",
                    gr ? "Δεν γνωρίζουμε αν η επανεκκίνηση ήταν προγραμματισμένη"
                       : "Cannot determine whether the restart was planned"
            );
            logLabelOkValue(
                    gr ? "Σημείωση" : "Note",
                    gr
                            ? "Δεν διατίθεται ιστορικό επανεκκινήσεων από μία μέτρηση uptime"
                            : "A single uptime reading is not a reboot history"
            );

        } else if (veryLongUptime) {

            logLabelWarnValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Μεγάλος χρόνος λειτουργίας"
                       : "Long uptime detected"
            );
            logLabelWarnValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "Το μεγάλο uptime από μόνο του δεν αποδεικνύει διαρροή μνήμης ή βλάβη"
                       : "Long uptime alone does not prove memory leaks or hardware defects"
            );

            if (extremeUptime) {

                logLabelErrorValue(
                        gr ? "Σοβαρότητα" : "Severity",
                        gr
                                ? "Χρόνος λειτουργίας πάνω από 14 ημέρες"
                                : "Uptime exceeds 14 days"
                );
                logLabelErrorValue(
                        gr ? "Σύσταση" : "Recommendation",
                        gr
                                ? "Επανεκκίνηση μόνο εφόσον υπάρχει σχετικό λειτουργικό σύμπτωμα"
                                : "Restart only if there is a related functional symptom"
                );

            } else {

                logLabelOkValue(
                        gr ? "Σύσταση" : "Recommendation",
                        gr
                                ? "Δεν απαιτείται επανεκκίνηση με κριτήριο μόνο το uptime"
                                : "Uptime alone does not justify a restart"
                );
            }

        } else {

            logLabelOkValue(
                    gr ? "Κατάσταση" : "Status",
                    gr
                            ? "Χρόνος λειτουργίας καταγράφηκε (χωρίς διάγνωση υγείας)"
                            : "Uptime observed (no device-health inference)"
            );
        }

        // ----------------------------------------------------
        // ROOT-AWARE INTELLIGENCE
        // ----------------------------------------------------
        if (isDeviceRooted()) {

            appendHtml("<br>");
            logInfo(gr
                    ? "Προχωρημένα σήματα uptime (root access):"
                    : "Advanced uptime signals (root access):");
                    logLine();

            long observedLowMemoryKills = readLowMemoryKillCountSafe();
            boolean pressureCounterAvailable = observedLowMemoryKills >= 0L;
            boolean lowMemoryPressure = pressureCounterAvailable
                    && observedLowMemoryKills < 5L;

            // Root access alone does not provide reboot history. A short
            // uptime proves only that the device booted recently.
            logLabelWarnValue(
                    gr ? "Ιστορικό επανεκκινήσεων" : "Reboot history",
                    gr ? "Δεν υπάρχουν επαληθευμένα συμβάντα επανεκκινήσεων"
                       : "No verified reboot event history available"
            );

            if (!pressureCounterAvailable) {
                logLabelWarnValue(gr ? "Πίεση μνήμης" : "Memory pressure",
                        gr ? "Μετρητής μη διαθέσιμος — δεν τεκμηριώνεται κατάσταση"
                           : "Counter unavailable — no condition established");
            } else if (!lowMemoryPressure) {

                logLabelWarnValue(
                        gr ? "Πίεση μνήμης" : "Memory pressure",
                        gr
                                ? "Εντοπίστηκαν συμβάντα πίεσης στο παρασκήνιο"
                                : "Background pressure events detected"
                );
                logLabelWarnValue(
                        gr ? "Συμπεριφορά συστήματος" : "System behaviour",
                        gr
                                ? "Αναφέρθηκαν συμβάντα· η αιτία τους δεν έχει ταυτοποιηθεί"
                                : "Events were reported; their cause is not established"
                );

            } else {

                logLabelOkValue(
                        gr ? "Πίεση μνήμης" : "Memory pressure",
                        gr
                                ? "Δεν εντοπίστηκαν σημαντικά σήματα πίεσης"
                                : "No significant pressure signals detected"
                );
            }

            logLabelWarnValue(
                    gr ? "Ερμηνεία" : "Interpretation",
                    gr ? "Το uptime δεν αποδεικνύει συχνότητα επανεκκινήσεων"
                       : "Uptime does not establish reboot frequency"
            );
        }

    } catch (Throwable t) {

        logLabelErrorValue(
                gr ? "Ανάλυση uptime" : "Uptime analysis",
                gr
                        ? "Αποτυχία αξιολόγησης χρόνου λειτουργίας"
                        : "Failed to evaluate system uptime"
        );
    }

// ----------------------------------------------------
// SUMMARY (Structured / Color-coded)
// ----------------------------------------------------
appendHtml("<br>");
logInfo(gr ? "Σύνοψη επανεκκινήσεων" : "Reboot summary");
logLine();

// No historical reboot-event series is collected by this Lab.
// Never translate "no evidence" into a NORMAL verdict.
RebootHistoryEvidence.Verdict rebootVerdict =
        RebootHistoryEvidence.classify(-1, 0L);
if (rebootVerdict == RebootHistoryEvidence.Verdict.INSUFFICIENT_DATA) {
    logLabelWarnValue(
            "REBOOT_PATTERN",
            gr ? "ΜΗ ΔΙΑΘΕΣΙΜΟ ΙΣΤΟΡΙΚΟ" : "HISTORY UNAVAILABLE"
    );
} else {
    // Reserved for future verified, timestamped reboot histories.
    logLabelWarnValue(
            "REBOOT_PATTERN",
            rebootVerdict == RebootHistoryEvidence.Verdict.REPEATED_OBSERVED
                    ? (gr ? "ΕΠΑΝΑΛΑΜΒΑΝΟΜΕΝΕΣ" : "REPEATED")
                    : (gr ? "ΔΕΝ ΕΠΑΛΗΘΕΥΤΗΚΑΝ ΕΠΑΝΑΛΑΜΒΑΝΟΜΕΝΕΣ" : "NO REPEATED EVENTS OBSERVED")
    );
}
GELServiceLog.info("SUMMARY: REBOOT_PATTERN=" + rebootVerdict.name());

    appendHtml("<br>");
    logOk(gr ? "Το Lab 20 ολοκληρώθηκε." : "Lab 20 finished.");
    logLine();
}

// ============================================================
// LABS 21 — 24 SECURITY & SYSTEM HEALTH
// ============================================================

// ============================================================
// LAB 21 — Screen Lock / Biometrics LIVE + Root-Aware
// REAL • USER-DRIVEN • NO LIES • POLICY + INFRA CHECK (ROOT)
// ============================================================
private boolean lab21Running = false;
private int lab21RunGeneration = 0;
private Lab21BiometricEvidence lab21ActiveEvidence = null;
private android.os.CancellationSignal lab21Cancellation = null;

private void lab21ScreenLock() {
    final boolean gr = AppLang.isGreek(this);
    if (lab21Running) {
        logLabelWarnValue(gr ? "LAB 21" : "LAB 21",
                gr ? "Υπάρχει ενεργός έλεγχος" : "A guided test is already active");
        return;
    }
    lab21Running = true;
    final int runId = ++lab21RunGeneration;
    if (btnExport != null) {
        btnExport.setEnabled(false);
        btnExport.setAlpha(0.5f);
    }

    appendHtml("<br>");
    logLine();
    logSection(gr ? "LAB 21 — Κλείδωμα και βιομετρικά: αποδεικτικά Android"
                  : "LAB 21 — Screen lock and biometrics: Android evidence");
    logLine();

    Boolean deviceSecure = null;
    Boolean currentlyKeyguardLocked = null;
    try {
        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (km != null) {
            deviceSecure = km.isDeviceSecure();
            currentlyKeyguardLocked = km.isKeyguardLocked();
        }
    } catch (RuntimeException ignored) {
        // A failed read remains UNKNOWN; never pretend the lock is disabled.
    }

    if (deviceSecure == null) {
        logLabelWarnValue(gr ? "Κλείδωμα συσκευής" : "Device lock",
                gr ? "ΑΓΝΩΣΤΟ / μη προσβάσιμο" : "UNKNOWN / unavailable");
    } else {
        logLabelValue(gr ? "Κλείδωμα συσκευής" : "Device lock",
                deviceSecure ? (gr ? "Δηλώθηκε ασφαλές κλείδωμα"
                                   : "Android reports secure lock configured")
                             : (gr ? "Δεν δηλώθηκε ασφαλές κλείδωμα"
                                   : "Secure lock not configured by Android"));
    }
    if (currentlyKeyguardLocked != null) {
        logLabelValue(gr ? "Keyguard τώρα" : "Current keyguard",
                currentlyKeyguardLocked ? (gr ? "ΕΝΕΡΓΟ" : "ACTIVE")
                                       : (gr ? "Δεν εμφανίζεται" : "Not showing"));
        logInfo(gr ? "Το ξεκλείδωτο τηλέφωνο όσο χρησιμοποιείται η εφαρμογή δεν σημαίνει κενό ασφάλειας."
                   : "An unlocked phone while this app is running is not evidence of a security defect.");
    }
    logInfo(gr ? "Το ασφαλές κλείδωμα δεν αποδεικνύει κρυπτογράφηση, Gatekeeper ή προστασία μετά από επανεκκίνηση."
               : "Secure-lock configuration does not prove encryption, Gatekeeper or post-reboot enforcement.");

    boolean ready = false;
    boolean readinessKnown = false;
    int allowedAuthenticator = 0; // Used only on API30+.
    if (Build.VERSION.SDK_INT >= 30) {
        try {
            android.hardware.biometrics.BiometricManager bm =
                    getSystemService(android.hardware.biometrics.BiometricManager.class);
            if (bm != null) {
                int strong = bm.canAuthenticate(
                        android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG);
                int weak = bm.canAuthenticate(
                        android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK);
                readinessKnown = true;
                if (strong == android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS) {
                    ready = true;
                    allowedAuthenticator =
                        android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
                    logLabelValue(gr ? "Βιομετρική ετοιμότητα" : "Biometric readiness",
                            gr ? "Android: STRONG διαθέσιμο για prompt"
                               : "Android: STRONG authenticator ready");
                } else if (weak == android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS) {
                    ready = true;
                    allowedAuthenticator =
                        android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK;
                    logLabelValue(gr ? "Βιομετρική ετοιμότητα" : "Biometric readiness",
                            gr ? "Android: WEAK διαθέσιμο για prompt"
                               : "Android: WEAK authenticator ready");
                } else {
                    logLabelWarnValue(gr ? "Βιομετρική ετοιμότητα" : "Biometric readiness",
                            gr ? "Μη διαθέσιμη τώρα (Android codes " + strong + "/" + weak + ")"
                               : "Not ready (Android codes " + strong + "/" + weak + ")");
                }
            }
        } catch (RuntimeException ignored) {}
    } else if (Build.VERSION.SDK_INT == 29) {
        try {
            android.hardware.biometrics.BiometricManager bm =
                    getSystemService(android.hardware.biometrics.BiometricManager.class);
            if (bm != null) {
                int status = bm.canAuthenticate(); // API29; no int overload until API30.
                readinessKnown = true;
                ready = status ==
                        android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS;
                logLabelValue(gr ? "Βιομετρική ετοιμότητα" : "Biometric readiness",
                        ready ? (gr ? "Android: διαθέσιμη" : "Android: ready")
                              : (gr ? "Μη έτοιμη (κωδικός " + status + ")"
                                    : "Not ready (code " + status + ")"));
            }
        } catch (RuntimeException ignored) {}
    } else if (Build.VERSION.SDK_INT == 28) {
        logLabelValue(gr ? "Βιομετρική ετοιμότητα" : "Biometric readiness",
                gr ? "API28: δεν υπάρχει διαθέσιμο BiometricManager — δοκιμή κατόπιν επιλογής"
                   : "API28: no BiometricManager — opt-in prompt available");
        ready = true; // Only permission to TRY a system prompt, not evidence of readiness.
    } else {
        readinessKnown = true;
        logLabelWarnValue(gr ? "BiometricPrompt" : "BiometricPrompt",
                gr ? "Μη διαθέσιμο πριν το Android 9" : "Unavailable below Android 9");
    }

    if (Build.VERSION.SDK_INT < 28 || (readinessKnown && !ready)) {
        logLabelValue(gr ? "Πραγματική ταυτοποίηση" : "Live authentication",
                "NOT_TESTED — unavailable / not enrolled / not ready");
        lab21FinishWithoutPrompt(runId);
        return;
    }
    if (Boolean.FALSE.equals(deviceSecure)) {
        logLabelValue(gr ? "Πραγματική ταυτοποίηση" : "Live authentication",
                "NOT_TESTED — secure device credential not configured");
        lab21FinishWithoutPrompt(runId);
        return;
    }

    final int authenticator = allowedAuthenticator;
    new AlertDialog.Builder(this)
            .setTitle(gr ? "LAB 21 — Πραγματικό BiometricPrompt"
                         : "LAB 21 — Guided BiometricPrompt")
            .setMessage(gr
                    ? "Θέλεις να δοκιμάσεις τη βιομετρική ταυτοποίηση μέσω του Android; "
                      + "Δεν αποθηκεύουμε βιομετρικά στοιχεία. "
                      + "Μία επιτυχής ταυτοποίηση επιβεβαιώνει μόνο αυτό το λειτουργικό μονοπάτι, "
                      + "όχι κάθε αισθητήρα ή όλη την ασφάλεια της συσκευής."
                    : "Test Android biometric authentication now? We never collect or store "
                      + "fingerprints or face data. A successful prompt confirms only this "
                      + "OS authentication path, not every sensor or overall device security.")
            .setPositiveButton(gr ? "ΕΝΑΡΞΗ" : "START", (d, which) ->
                    lab21StartPrompt(runId, authenticator))
            .setNegativeButton(gr ? "ΠΑΡΑΛΕΙΨΗ" : "SKIP", (d, which) -> {
                logLabelValue(gr ? "Πραγματική ταυτοποίηση" : "Live authentication",
                        "NOT_TESTED — user skipped");
                lab21FinishWithoutPrompt(runId);
            })
            .setOnCancelListener(d -> {
                logLabelValue(gr ? "Πραγματική ταυτοποίηση" : "Live authentication",
                        "NOT_TESTED — cancelled before prompt");
                lab21FinishWithoutPrompt(runId);
            })
            .show();
}

private void lab21StartPrompt(final int runId, final int authenticator) {
    final boolean gr = AppLang.isGreek(this);
    if (!lab21Running || lab21RunGeneration != runId
            || isFinishing() || isDestroyed()) return;

    final Lab21BiometricEvidence evidence = new Lab21BiometricEvidence();
    if (!evidence.start()) return;
    lab21ActiveEvidence = evidence;
    final android.os.CancellationSignal cancellation =
            new android.os.CancellationSignal();
    lab21Cancellation = cancellation;

    try {
        final Executor executor = getMainExecutor();
        android.hardware.biometrics.BiometricPrompt.Builder builder =
                new android.hardware.biometrics.BiometricPrompt.Builder(this)
                        .setTitle(gr ? "LAB 21 — Επιβεβαίωση μέσω Android"
                                     : "LAB 21 — Android biometric verification")
                        .setSubtitle(gr ? "Χρησιμοποίησε το επιλεγμένο βιομετρικό"
                                        : "Use your enrolled biometric")
                        .setDescription(gr ? "Χωρίς αποθήκευση βιομετρικών δεδομένων"
                                           : "No biometric template is stored")
                        .setNegativeButton(gr ? "Ακύρωση" : "Cancel", executor, (d, which) -> {
                            if (lab21IsCurrent(runId, evidence) && evidence.onError(true)) {
                                lab21FinishPrompt(runId, evidence, null);
                            }
                        });
        if (Build.VERSION.SDK_INT >= 30 && authenticator != 0) {
            builder.setAllowedAuthenticators(authenticator);
        }
        android.hardware.biometrics.BiometricPrompt.AuthenticationCallback cb =
                new android.hardware.biometrics.BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationFailed() {
                        // NON-TERMINAL. Android permits the user to retry.
                        if (lab21IsCurrent(runId, evidence)) evidence.onNonMatch();
                    }

                    @Override
                    public void onAuthenticationSucceeded(
                            android.hardware.biometrics.BiometricPrompt.AuthenticationResult result) {
                        if (lab21IsCurrent(runId, evidence) && evidence.onAuthenticated()) {
                            lab21FinishPrompt(runId, evidence, null);
                        }
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        boolean userCancelled =
                                errorCode == android.hardware.biometrics.BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED
                                || errorCode == android.hardware.biometrics.BiometricPrompt.BIOMETRIC_ERROR_CANCELED;
                        if (lab21IsCurrent(runId, evidence)
                                && evidence.onError(userCancelled)) {
                            lab21FinishPrompt(runId, evidence,
                                    "Android BiometricPrompt error code " + errorCode);
                        }
                    }
                };
        builder.build().authenticate(cancellation, executor, cb);
    } catch (RuntimeException e) {
        if (lab21IsCurrent(runId, evidence) && evidence.onError(false)) {
            lab21FinishPrompt(runId, evidence,
                    "BiometricPrompt launch unavailable: " + e.getClass().getSimpleName());
        }
    }
}

private boolean lab21IsCurrent(int runId, Lab21BiometricEvidence evidence) {
    return lab21Running && lab21RunGeneration == runId
            && lab21ActiveEvidence == evidence
            && !isFinishing() && !isDestroyed();
}

private void lab21FinishPrompt(int runId, Lab21BiometricEvidence evidence,
                               String platformError) {
    if (!lab21IsCurrent(runId, evidence) || evidence.isPending()) return;
    final boolean gr = AppLang.isGreek(this);
    appendHtml("<br>");
    Lab21BiometricEvidence.Outcome outcome = evidence.outcome();
    switch (outcome) {
        case ANDROID_AUTHENTICATED:
            logLabelValue(gr ? "Επιβεβαίωση Android" : "Android authentication",
                    gr ? "ΠΑΡΑΤΗΡΗΘΗΚΕ επιτυχής βιομετρική ταυτοποίηση"
                       : "OBSERVED successful OS biometric authentication");
            logInfo(gr ? "Δεν πιστοποιούνται όλοι οι αισθητήρες, η ακρίβεια ή η ακεραιότητα υλικού."
                       : "Does not certify every sensor, accuracy or physical integrity.");
            break;
        case USER_CANCELLED:
            logLabelWarnValue(gr ? "Βιομετρική δοκιμή" : "Biometric challenge",
                    gr ? "ΑΚΥΡΩΘΗΚΕ — χωρίς διάγνωση βλάβης"
                       : "CANCELLED — no hardware verdict");
            break;
        default:
            logLabelWarnValue(gr ? "Βιομετρική δοκιμή" : "Biometric challenge",
                    gr ? "ΜΗ ΔΙΑΘΕΣΙΜΗ / ΣΦΑΛΜΑ — όχι αστοχία αισθητήρα"
                       : "UNAVAILABLE / ERROR — not evidence of a defective sensor");
    }
    logLabelValue(gr ? "Απόπειρες χωρίς αναγνώριση" : "Non-matching attempts",
            String.valueOf(evidence.nonMatchingAttempts()));
    if (platformError != null) {
        logLabelValue(gr ? "Πληροφορία Android" : "Android detail", platformError);
    }
    lab21ActiveEvidence = null;
    lab21Cancellation = null;
    lab21FinishWithoutPrompt(runId);
}

private void lab21FinishWithoutPrompt(int runId) {
    if (lab21RunGeneration != runId) return;
    lab21Running = false;
    lab21RunGeneration++;
    logInfo(AppLang.isGreek(this)
            ? "Το Lab 21 ολοκλήρωσε την καταγραφή των διαθέσιμων στοιχείων Android."
            : "Lab 21 finished recording the available Android evidence.");
    enableSingleExportButton();
}

// ============================================================
// // ROOT HELPERS — minimal, safe, no assumptions
// ============================================================
private boolean isRootAvailable() {
try {
if (new java.io.File("/system/xbin/su").exists()) return true;
if (new java.io.File("/system/bin/su").exists())  return true;
if (new java.io.File("/sbin/su").exists())        return true;
if (new java.io.File("/su/bin/su").exists())      return true;

String out = runSu("id");  
    return out != null && out.toLowerCase(java.util.Locale.US).contains("uid=0");  
} catch (Throwable ignore) {  
    return false;  
}

}

private boolean rootPathExists(String path) {
String cmd = "[ -e '" + path + "' ] && echo OK || echo NO";
String out = runSu(cmd);
return out != null && out.contains("OK");
}

private boolean rootGlobExists(String glob) {
String cmd = "ls " + glob + " 1>/dev/null 2>/dev/null && echo OK || echo NO";
String out = runSu(cmd);
return out != null && out.contains("OK");
}

private String runSu(String command) {
java.io.BufferedReader br = null;
try {
java.lang.Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
StringBuilder sb = new StringBuilder();
String line;
while ((line = br.readLine()) != null) {
if (sb.length() > 0) sb.append("\n");
sb.append(line);
}
try { p.waitFor(); } catch (Throwable ignore) {}
String s = sb.toString().trim();
return s.isEmpty() ? null : s;
} catch (Throwable ignore) {
return null;
} finally {
try { if (br != null) br.close(); } catch (Throwable ignore) {}
}
}

/* ============================================================
LAB 22 — Security Patch + Play Protect (Realtime)
============================================================ */
private void lab22SecurityPatchAndPlayProtect() {
	
	final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr 
            ? "LAB 22 — Ενημέρωση Ασφαλείας + Play Protect (Σε πραγματικό χρόνο)"
            : "LAB 22 — Security Patch + Play Protect (Realtime)");
    logLine();

// ------------------------------------------------------------
// 1) Security Patch Level (raw)
// ------------------------------------------------------------
String patch = null;

try {
    patch = android.os.Build.VERSION.SECURITY_PATCH;

    logInfo(gr ? "Επίπεδο ενημέρωσης ασφαλείας"
               : "Security patch level");

    if (patch != null && !patch.isEmpty()) {

        logLabelOkValue(
                gr ? "Αναφέρεται" : "Reported",
                patch
        );

    } else {

        logLabelWarnValue(
                gr ? "Αναφέρεται" : "Reported",
                gr ? "Δεν παρέχεται από το σύστημα"
                   : "Not provided by system"
        );
    }

} catch (Throwable e) {

    logLabelWarnValue(
            gr ? "Ανάγνωση patch" : "Patch read",
            (gr ? "Αποτυχία (" : "Failed (") + e.getMessage() + ")"
    );
}

// ------------------------------------------------------------
// 2) Patch Freshness Intelligence (AGE + RISK)
// ------------------------------------------------------------
try {
    if (patch != null && !patch.isEmpty()) {

        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setLenient(false);

        long patchTime = sdf.parse(patch).getTime();
        long now = System.currentTimeMillis();

        long diffDays   = (now - patchTime) / (1000L * 60 * 60 * 24);
        long diffMonths = diffDays / 30;

        logInfo(gr ? "Ηλικία ενημέρωσης ασφαλείας"
                   : "Patch age");

        logLabelOkValue(
                gr ? "Εκτίμηση" : "Estimated",
                gr ? diffMonths + " μήνες"
                   : diffMonths + " months"
        );

        logInfo(gr ? "Κατάσταση ενημέρωσης"
                   : "Patch status");

        if (diffDays < 0) {
            logLabelWarnValue(gr ? "Ημερομηνία patch" : "Security patch date",
                    gr ? "Μελλοντική ή μη συμβατή με το ρολόι συσκευής — όχι αξιολογήσιμη"
                       : "Future relative to device clock — cannot assess age");
        } else if (diffMonths <= 3) {

            logLabelOkValue(
                    gr ? "Ηλικία patch" : "Patch age category",
                    gr ? "ΠΡΟΣΦΑΤΗ δηλωμένη ημερομηνία — δεν έγινε ανάλυση ευπαθειών"
                       : "Recent reported date — vulnerabilities were NOT scanned"
            );

        } else if (diffMonths <= 6) {

            logLabelWarnValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "ΜΕΤΡΙΩΣ ΠΑΛΙΑ"
                       : "MODERATELY OUTDATED"
            );

        } else {

            logLabelErrorValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "ΠΑΛΙΑ (λείπουν πρόσφατες διορθώσεις ασφαλείας)"
                       : "OUTDATED (missing recent security fixes)"
            );
        }
    }
} catch (Throwable e) {

    logLabelWarnValue(
            gr ? "Ανάλυση ηλικίας ενημέρωσης" : "Patch age analysis",
            (gr ? "Αποτυχία (" : "Failed (") + e.getMessage() + ")"
    );
}

// ------------------------------------------------------------
// 3) Play Protect — NOT CERTIFIABLE BY A NORMAL THIRD-PARTY APP
// ------------------------------------------------------------
// package_verifier_enable is not an authoritative public Play Protect API.
// Google Play Services package visibility is also restricted on modern Android;
// failure to query that package is not proof it is uninstalled.
logInfo("Google Play Protect");
logLabelWarnValue(gr ? "Κατάσταση προστασίας" : "Protection status",
        gr ? "ΜΗ ΕΠΑΛΗΘΕΥΣΙΜΗ μέσω δημόσιου SDK από το GEL iDoctor"
           : "NOT VERIFIABLE by GEL iDoctor via the public Android SDK");
logLabelWarnValue(gr ? "Έλεγχος για malware" : "Malware-free status",
        gr ? "Δεν μπορεί να πιστοποιηθεί από αυτή την εφαρμογή"
           : "Cannot be certified by this app");
logLabelValue(gr ? "Επαλήθευση" : "Verification",
        gr ? "Άνοιξε Google Play Store > Play Protect και έλεγξε την οθόνη "
               + "ρυθμίσεων / πρόσφατης σάρωσης."
           : "Open Google Play Store > Play Protect and review its own "
               + "settings and recent scan status.");

// ------------------------------------------------------------
// 4) Trust Boundary Clarification
// ------------------------------------------------------------
logLine();
logInfo(gr ? "Πεδίο ασφάλειας"
           : "Security scope");

logLabelWarnValue(
        "Play Protect",
        gr ? "Η λειτουργία υπάρχει στο οικοσύστημα Google αλλά το iDoctor "
               + "δεν μπορεί να επιβεβαιώσει την τρέχουσα κατάστασή της."
           : "Google provides the feature, but iDoctor cannot independently "
               + "verify its current enabled/scanned state."
);

logLabelWarnValue(
        gr ? "Περιορισμός" : "Limitation",
        gr ? "ΔΕΝ επιδιορθώνει ευπάθειες συστήματος ή σφάλματα firmware"
           : "Does NOT patch system vulnerabilities or firmware flaws"
);

// ------------------------------------------------------------
// 5) Manual Guidance (Technician)
// ------------------------------------------------------------
logLine();
logInfo(gr ? "Χειροκίνητη επαλήθευση"
           : "Manual verification");

logLabelOkValue(
        gr ? "Έλεγχος 1" : "Check 1",
        gr ? "Ρυθμίσεις > Πληροφορίες τηλεφώνου > Έκδοση Android > Επίπεδο ενημέρωσης ασφαλείας"
           : "Settings > About phone > Android version > Security patch level"
);

logLabelWarnValue(
        gr ? "Σημείωση" : "Note",
        gr ? "Πολύ παλιά επίπεδα ενημέρωσης αυξάνουν την έκθεση σε exploits"
           : "Very old patch levels increase exploit exposure"
);

logLabelOkValue(
        gr ? "Έλεγχος 2" : "Check 2",
        gr ? "Google Play Store > Play Protect > Έλεγχος ότι η σάρωση είναι ενεργή"
           : "Google Play Store > Play Protect > Verify scanning enabled"
);

appendHtml("<br>");
logOk(gr ? "Το Lab 22 ολοκληρώθηκε." : "Lab 22 finished.");
logLine();

}

// ============================================================
// LAB 23 — Developer Options / ADB Risk Note + UI BUBBLES + AUTO-FIX HINTS
// GEL Security v3.1 (Realtime Snapshot)
// ============================================================
// ============================================================
// 1) USB DEBUGGING FLAG
// ============================================================

private void lab23DeveloperOptionsRisk() {
    final boolean gr = AppLang.isGreek(this);
    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 23 — Παρατηρήσεις debugging & επιλογών προγραμματιστή"
               : "LAB 23 — Developer and debugging evidence");
    logLine();

    // Android's documented Settings.Global.ADB_ENABLED and
    // DEVELOPMENT_SETTINGS_ENABLED report 0 to ordinary third-party apps.
    // 0 MUST NOT be used to claim device debugging is off.
    logLabelWarnValue(gr ? "USB debugging ολόκληρης συσκευής"
                         : "Device-wide USB debugging",
            gr ? "ΜΗ ΕΠΑΛΗΘΕΥΣΙΜΟ: η ανάγνωση ADB_ENABLED περιορίζεται από το Android"
               : "UNVERIFIABLE: Android restricts ADB_ENABLED for third-party apps");
    logLabelWarnValue(gr ? "Επιλογές προγραμματιστή"
                         : "Developer options",
            gr ? "ΜΗ ΕΠΑΛΗΘΕΥΣΙΜΕΣ από το DEVELOPMENT_SETTINGS_ENABLED τρίτων εφαρμογών"
               : "UNVERIFIABLE via third-party DEVELOPMENT_SETTINGS_ENABLED");
    logLabelWarnValue(gr ? "Wireless ADB / pairing"
                         : "Wireless ADB / pairing",
            gr ? "ΜΗ ΕΠΑΛΗΘΕΥΣΙΜΟ: localhost TCP θύρες δεν πιστοποιούν απομακρυσμένο ADB"
               : "UNVERIFIABLE: open localhost TCP ports cannot establish wireless ADB");

    // A direct, restricted signal that we CAN inspect is our OWN application
    // process and APK flag. This is not a device compromise assessment.
    boolean ownAppDebuggable =
            (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    boolean currentProcessDebugged = android.os.Debug.isDebuggerConnected();
    boolean waitingForDebugger = android.os.Debug.waitingForDebugger();
    logLabelValue(gr ? "Build του GEL iDoctor" : "GEL iDoctor build",
            ownAppDebuggable ? "DEBUGGABLE application" : "NON-DEBUGGABLE application");
    logLabelValue(gr ? "Debugger στο iDoctor αυτή τη στιγμή"
                     : "Debugger attached to iDoctor process now",
            currentProcessDebugged ? "OBSERVED" : "NOT OBSERVED AT THIS MOMENT");
    logLabelValue(gr ? "Η διεργασία iDoctor περιμένει debugger"
                     : "iDoctor process waiting for debugger",
            waitingForDebugger ? "YES" : "NO");

    logLabelWarnValue(gr ? "Δείκτης κινδύνου συσκευής" : "Device security risk score",
            gr ? "Δεν υπολογίζεται — απουσιάζουν επαληθεύσιμα στοιχεία"
               : "Not calculated — device-wide evidence is unavailable");
    logLabelWarnValue(gr ? "Ερμηνεία" : "Interpretation",
            gr ? "Η ενεργοποίηση εργαλείων ανάπτυξης δεν ισοδυναμεί από μόνη "
                   + "της με κακόβουλη επέμβαση. Η απουσία debugger στο iDoctor "
                   + "δεν αποδεικνύει ότι το ADB είναι κλειστό."
               : "Developer tools alone do not establish compromise. No debugger "
                   + "attached to iDoctor does not prove ADB is disabled.");

    logInfo(gr ? "Χειροκίνητη επαλήθευση από τον κάτοχο"
               : "User-authorized manual verification");
    logLabelValue(gr ? "Ρυθμίσεις" : "Settings",
            gr ? "Σύστημα > Επιλογές προγραμματιστή > USB debugging / Ασύρματο debugging. "
                   + "Επαλήθευση στην οθόνη της ίδιας της συσκευής."
               : "System > Developer options > USB debugging / Wireless debugging. "
                   + "Confirm directly in device settings.");
    GELServiceLog.info("SUMMARY: DEVICE_ADB_STATUS=UNKNOWN; "
            + "DEVELOPER_OPTIONS=UNKNOWN; APP_DEBUGGABLE=" + ownAppDebuggable);
    appendHtml("<br>");
    logOk(gr ? "Το Lab 23 ολοκληρώθηκε με σαφή όρια πρόσβασης."
             : "Lab 23 finished with explicit platform access limits.");
    logLine();
}

// ============================================================
// UI BUBBLES (GEL)
// ============================================================
private String bubble(boolean on) {
    return on ? "[ON]" : "[OFF]";
}

private String riskBubble(int risk) {
    if (risk <= 10) return "[LOW]";
    if (risk <= 30) return "[MEDIUM]";
    if (risk <= 60) return "[HIGH]";
    return "[CRITICAL]";
}

// ============================================================
// HELPERS — PORT CHECK (LOCALHOST)
// ============================================================
private boolean isPortOpen(int port, int timeoutMs) {
Socket s = null;
try {
s = new Socket();
s.connect(new InetSocketAddress("127.0.0.1", port), timeoutMs);
return true;
} catch (Exception e) {
return false;
} finally {
if (s != null) try { s.close(); } catch (Exception ignored) {}
}
}

// Scan pairing port range 7460-7490 (best-effort)
private boolean scanPairingPortRange() {
for (int p = 7460; p <= 7490; p++) {
if (isPortOpen(p, 80)) return true;
}
return false;
}

// ============================================================
// LAB 24 — Root / Bootloader Suspicion Checklist (FULL AUTO + RISK SCORE)
// GEL Universal Edition — NO external libs
// ============================================================
private void lab24RootSuspicion() {
    final boolean gr = AppLang.isGreek(this);
    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 24 — Παρατηρούμενες ενδείξεις root / verified boot"
               : "LAB 24 — Observed root / verified-boot indicators");
    logLine();

    // Visible su paths are positive, limited observations. Their absence
    // (especially with Android sandboxing and root hiding) is NOT proof of
    // unmodified firmware. Never attempt 'su -c id' silently or ask for root.
    String[] suCandidates = {
            "/system/bin/su", "/system/xbin/su", "/sbin/su",
            "/su/bin/su", "/vendor/bin/su", "/odm/bin/su"
    };
    int observedSuPaths = 0;
    for (String path : suCandidates) {
        if (!lab24_fileExists(path)) continue;
        observedSuPaths++;
        logLabelWarnValue(gr ? "Ορατό εκτελέσιμο ίχνος su" : "Visible su-path indicator",
                path + " (existence only; capabilities not tested)");
    }
    if (observedSuPaths == 0) {
        logLabelWarnValue(gr ? "su path coverage" : "su path coverage",
                gr ? "Δεν είναι ορατά στα συνηθισμένα paths· root ΔΕΝ αποκλείεται"
                   : "No listed paths visible; root is NOT ruled out");
    }

    // An installed manager application alone does not confer root access.
    // Package visibility may hide even installed packages from this process.
    String[] rootManagerPackages = {
            "com.topjohnwu.magisk",
            "eu.chainfire.supersu",
            "com.kingroot.kinguser",
            "com.kingo.root"
    };
    int visibleManagerCount = 0;
    for (String pkg : rootManagerPackages) {
        // Android 11+ package-visibility filters can hide installed apps.
        // A successful targeted lookup only proves visibility, not root.
        try {
            getPackageManager().getPackageInfo(pkg, 0);
            visibleManagerCount++;
        } catch (PackageManager.NameNotFoundException ignored) {
            // Not visible is NOT evidence the app is absent.
        } catch (RuntimeException ignored) {
            // Permission/OEM restrictions => unknown.
        }
    }
    if (visibleManagerCount > 0) {
        logLabelWarnValue(gr ? "Ορατές εφαρμογές διαχείρισης" : "Visible manager packages",
                gr ? (visibleManagerCount + " ενδείξεις — όχι απόδειξη root")
                   : (visibleManagerCount + " hints — not proof of root"));
    }
    // Names of the user's installed packages are deliberately not exported.
    if (visibleManagerCount == 0) {
        logLabelValue(gr ? "Root manager apps" : "Root manager apps",
                gr ? "Δεν αναφέρθηκαν στα ορατά packages — όχι έλεγχος απουσίας"
                   : "None among visible packages — not an absence proof");
    }

    String tags = Build.TAGS == null ? "" : Build.TAGS;
    logLabelValue("Build.TAGS",
            tags.isEmpty() ? (gr ? "Άγνωστο" : "Unknown") : tags);
    if (tags.contains("test-keys")) {
        logLabelWarnValue(gr ? "Περιορισμός build tags" : "Build tags limitation",
                gr ? "Τα test-keys δηλώνουν build metadata, όχι υποχρεωτικά root"
                   : "test-keys is build metadata, not a root-access proof");
    }

    String bootState = lab24_getProp("ro.boot.verifiedbootstate");
    String deviceState = lab24_getProp("ro.boot.vbmeta.device_state");
    String flashLocked = lab24_getProp("ro.boot.flash.locked");
    logLabelValue(gr ? "OS-reported Verified Boot" : "OS-reported Verified Boot",
            bootState != null && !bootState.trim().isEmpty()
                    ? bootState : (gr ? "Μη διαθέσιμο" : "Unavailable"));
    logLabelValue(gr ? "OS-reported vbmeta state" : "OS-reported vbmeta state",
            deviceState != null && !deviceState.trim().isEmpty()
                    ? deviceState : (gr ? "Μη διαθέσιμο" : "Unavailable"));
    logLabelValue(gr ? "OS-reported flash locked" : "OS-reported flash locked",
            flashLocked != null && !flashLocked.trim().isEmpty()
                    ? flashLocked : (gr ? "Μη διαθέσιμο" : "Unavailable"));

    Lab24BootEvidence.Result bootEvidence =
            Lab24BootEvidence.classify(bootState, deviceState, flashLocked);
    switch (bootEvidence) {
        case OS_REPORTS_LOCKED_VERIFIED:
            logLabelValue(gr ? "Ενδείξεις Verified Boot" : "Verified Boot indicators",
                    gr ? "Το Android αναφέρει locked/green — ΔΕΝ είναι attestation"
                       : "Android reports locked/green — NOT cryptographic attestation");
            break;
        case OS_REPORTS_NONSTANDARD_TRUST:
            logLabelWarnValue(gr ? "Ενδείξεις Verified Boot" : "Verified Boot indicators",
                    gr ? "Μη τυπική αλυσίδα εμπιστοσύνης κατά το Android"
                       : "Android reports a non-standard trust chain");
            break;
        case OS_REPORTS_UNLOCKED_OR_UNVERIFIED:
            logLabelWarnValue(gr ? "Ενδείξεις Verified Boot" : "Verified Boot indicators",
                    gr ? "Το Android αναφέρει unlocked ή αποτυχία επαλήθευσης — root άγνωστο"
                       : "Android reports unlocked/verification concern — root UNKNOWN");
            break;
        case OS_PROPERTIES_INCONSISTENT:
            logLabelWarnValue(gr ? "Ενδείξεις Verified Boot" : "Verified Boot indicators",
                    gr ? "ΑΝΤΙΦΑΤΙΚΑ boot properties — απαιτείται εξωτερική επαλήθευση"
                       : "CONFLICTING boot properties — independent verification required");
            break;
        default:
            logLabelWarnValue(gr ? "Ενδείξεις Verified Boot" : "Verified Boot indicators",
                    gr ? "ΑΓΝΩΣΤΕΣ — δεν τεκμηριώνεται unlocked ή locked"
                       : "UNKNOWN — neither locked nor unlocked proven");
    }
    logInfo(gr ? "Τα system properties μπορεί να απουσιάζουν, να είναι OEM-specific ή να τροποποιούνται."
               : "System properties may be missing, OEM-specific or altered.");

    logLabelWarnValue(gr ? "Root / ακεραιότητα συσκευής"
                         : "Device root/integrity verification",
            gr ? "ΜΗ ΕΠΑΛΗΘΕΥΣΙΜΗ πλήρως από εφαρμογή τρίτου χωρίς έμπιστη attestation"
               : "NOT fully verifiable by a third-party app without trusted attestation");
    logLabelWarnValue(gr ? "Ποσοστιαία ασφάλεια" : "Safety percentage",
            gr ? "Δεν υπολογίζεται από path, package ή boot-animation heuristics"
               : "Not computed from path, package or boot-animation heuristics");
    logLabelValue(gr ? "Επόμενος έλεγχος" : "Next verification",
            gr ? "Για υψηλή βεβαιότητα απαιτείται εξουσιοδοτημένη επιβεβαίωση "
                   + "verified boot / Play Integrity με κατάλληλο backend."
               : "High-assurance verification requires authorized verified-boot "
                   + "or Play Integrity evidence with an appropriate trusted backend.");

    GELServiceLog.info("SUMMARY: ROOT_STATE=UNKNOWN; "
            + "VERIFIED_BOOT=OS_REPORTED_ONLY; ROOT_PATHS_OBSERVED=" + observedSuPaths);
    appendHtml("<br>");
    logOk(gr ? "Το Lab 24 ολοκληρώθηκε με τεκμηριωμένα όρια."
             : "Lab 24 completed with documented evidence limits.");
    logLine();
}

// Unprivileged, bounded public filesystem check. Sandbox restrictions can
// hide paths and a missing su path does NOT establish lack of root.
private boolean lab24_fileExists(String path) {
    try {
        return path != null && new java.io.File(path).exists();
    } catch (SecurityException ignored) {
        return false;
    }
}

// No hidden Android SystemProperties API or root command. Reads only
// three explicitly allowlisted OS-reporting keys. Missing/blocked output
// returns UNKNOWN, never "green" or "locked" by default.
private String lab24_getProp(String key) {
    if (!("ro.boot.verifiedbootstate".equals(key)
            || "ro.boot.vbmeta.device_state".equals(key)
            || "ro.boot.flash.locked".equals(key))) {
        return null;
    }
    java.lang.Process process = null;
    try {
        process = new ProcessBuilder("/system/bin/getprop", key)
                .redirectErrorStream(true).start();
        boolean ended = process.waitFor(
                350L, java.util.concurrent.TimeUnit.MILLISECONDS);
        if (!ended || process.exitValue() != 0) return null;
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream(),
                        java.nio.charset.StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            return line == null || line.trim().isEmpty() ? null : line.trim();
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return null;
    } catch (java.io.IOException | RuntimeException ignored) {
        return null;
    } finally {
        if (process != null) {
            try { process.destroy(); } catch (RuntimeException ignored) {}
        }
    }
}

// ============================================================
// LAB 24 — INTERNAL HELPERS
// ============================================================
// LABS 25 — 30: ADVANCED / LOGS
// ============================================================

// ============================================================
// LAB 25 — GEL Crash Intelligence v5.0 (FULL AUTO EDITION)
// ============================================================
private void lab25CrashHistory() {
    final boolean gr = AppLang.isGreek(this);
    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 25 — Διαθέσιμα στοιχεία crash / παγώματος"
            : "LAB 25 — Available crash / ANR evidence");
    logLine();

    logLabelWarnValue(gr ? "Κάλυψη" : "Coverage", gr
            ? "Το Android περιορίζει την πρόσβαση. Ο έλεγχος δεν αποτελεί πλήρες ιστορικό όλων των εφαρμογών ή πιστοποίηση σταθερότητας."
            : "Android restricts access. This is not a complete history of all apps or a stability certification.");

    // This API may be restricted to our UID. It is not a historical query.
    try {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (am == null) throw new IllegalStateException("Service unavailable");
        List<ActivityManager.ProcessErrorStateInfo> errors = am.getProcessesInErrorState();
        int visible = 0;
        if (errors != null) for (ActivityManager.ProcessErrorStateInfo error : errors) {
            if (error == null) continue;
            visible++;
            logLabelWarnValue(error.condition == ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING
                    ? "ANR" : "ERROR", safeStr(error.processName) + " — " + safeStr(error.shortMsg));
        }
        logInfo(gr
                ? "Τρέχον στιγμιότυπο: " + visible + " ορατές εγγραφές. Η πρόσβαση μπορεί να περιορίζεται στο iDoctor· δεν είναι ιστορικό της συσκευής."
                : "Current snapshot: " + visible + " visible records. Access may be limited to iDoctor; this is not device history.");
    } catch (RuntimeException unavailable) {
        logLabelWarnValue(gr ? "Τρέχον στιγμιότυπο" : "Current snapshot",
                gr ? "Μη διαθέσιμα δεδομένα." : "Data unavailable.");
    }

    // Android API 30+: reliable *own-package* historical process-exit reasons.
    // This is not an all-app, system/kernel or hardware-failure history.
    if (Build.VERSION.SDK_INT >= 30) {
        try {
            ActivityManager ownManager =
                    (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<android.app.ApplicationExitInfo> exits = ownManager == null
                    ? null : ownManager.getHistoricalProcessExitReasons(
                            getPackageName(), 0, 32);
            if (exits != null) {
                int appJavaCrashes = 0, appNativeCrashes = 0;
                int appAnrs = 0, appLowMemory = 0;
                for (android.app.ApplicationExitInfo exit : exits) {
                    if (exit == null) continue;
                    switch (exit.getReason()) {
                        case android.app.ApplicationExitInfo.REASON_CRASH:
                            appJavaCrashes++;
                            break;
                        case android.app.ApplicationExitInfo.REASON_CRASH_NATIVE:
                            appNativeCrashes++;
                            break;
                        case android.app.ApplicationExitInfo.REASON_ANR:
                            appAnrs++;
                            break;
                        case android.app.ApplicationExitInfo.REASON_LOW_MEMORY:
                            appLowMemory++;
                            break;
                        default:
                            break;
                    }
                }
                logLabelValue(gr ? "Ιστορικό τερματισμών iDoctor" : "iDoctor exits retained",
                        exits.size() + (gr ? " εγγραφές (όριο 32)" : " records (max 32)"));
                logLabelValue(gr ? "Java / native crash (iDoctor)" : "Java / native crashes (iDoctor)",
                        appJavaCrashes + " / " + appNativeCrashes);
                logLabelValue(gr ? "ANR / χαμηλή μνήμη (iDoctor)" : "ANR / low-memory exits (iDoctor)",
                        appAnrs + " / " + appLowMemory);
                logLabelWarnValue(gr ? "Περιορισμός ιστορικού" : "History limitation",
                        gr ? "Μόνο πρόσφατοι καταγεγραμμένοι τερματισμοί της εφαρμογής, όχι όλης της συσκευής."
                           : "Only retained process exits of this app, not all-device history.");
            } else {
                logLabelWarnValue(gr ? "Ιστορικό iDoctor" : "iDoctor process exit history",
                        gr ? "Μη διαθέσιμο" : "Unavailable");
            }
        } catch (RuntimeException ignored) {
            logLabelWarnValue(gr ? "Ιστορικό iDoctor" : "iDoctor process exit history",
                    gr ? "Πρόσβαση μη διαθέσιμη" : "Access unavailable");
        }
    } else {
        logLabelValue(gr ? "Ιστορικό τερματισμών εφαρμογής" : "App process exit history",
                gr ? "Απαιτεί Android 11 (API 30) ή νεότερο."
                   : "Requires Android 11 (API 30) or later.");
    }

    String[] tags = {"system_app_crash", "data_app_crash", "system_app_anr",
            "data_app_anr", "system_server_crash", "system_server_wtf", "system_server_anr"};
    GelCrashCoverage coverage = new GelCrashCoverage(tags.length);
    List<String> records = new ArrayList<>();
    int crashes = 0, anrs = 0, system = 0;
    boolean allowed = checkSelfPermission("android.permission.READ_LOGS")
            == android.content.pm.PackageManager.PERMISSION_GRANTED;
    if (!allowed && checkSelfPermission("android.permission.READ_DROPBOX_DATA")
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        android.app.AppOpsManager ops = (android.app.AppOpsManager) getSystemService(APP_OPS_SERVICE);
        try {
            allowed = ops != null && ops.checkOpNoThrow(android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName()) == android.app.AppOpsManager.MODE_ALLOWED;
        } catch (RuntimeException ignored) { allowed = false; }
    }
    DropBoxManager dropbox = (DropBoxManager) getSystemService(DROPBOX_SERVICE);
    if (allowed && dropbox != null) for (String tag : tags) {
        boolean complete = false;
        try {
            long since = 0;
            int scanned = 0;
            while (true) {
                DropBoxManager.Entry entry = dropbox.getNextEntry(tag, since);
                if (entry == null) { complete = true; break; }
                try {
                    long timestamp = entry.getTimeMillis();
                    // Bounded work; a truncated query is never reported as complete.
                    if (scanned >= 50 || timestamp <= since) break;
                    scanned++;
                    since = timestamp;
                    if (tag.startsWith("system_server")) system++;
                    else if (tag.endsWith("_anr")) anrs++;
                    else crashes++;
                    records.add(tag + " — " + java.text.DateFormat.getDateTimeInstance()
                            .format(new java.util.Date(timestamp)) + ": " + readDropBoxEntry(entry));
                } finally {
                    entry.close();
                }
            }
        } catch (RuntimeException unavailable) {
            // Permission failures and partial reads must not become healthy zero counts.
        }
        coverage.recordQuery(complete);
    }

    appendHtml("<br>");
    logInfo(gr ? "Διαθέσιμο ιστορικό Android DropBox" : "Available Android DropBox history");
    logLine();
    String unavailable = gr ? "Μη διαθέσιμα δεδομένα — ανεπαρκής πρόσβαση ή ελλιπής ανάγνωση."
            : "Data unavailable — insufficient access or incomplete reading.";
    if (coverage.complete()) {
        logInfo(gr ? "Συμβάντα crash στις διαθέσιμες καταγραφές: " + crashes
                : "Crash events in available records: " + crashes);
        logInfo("ANR: " + anrs);
        logInfo(gr ? "Συμβάντα συστήματος: " + system : "System events: " + system);
        logLabelWarnValue(gr ? "Περιορισμός" : "Limitation", gr
                ? "Οι καταγραφές μπορεί να έχουν διαγραφεί ή να μην τηρούνται από τον κατασκευαστή. Μηδενικές καταγραφές δεν αποδεικνύουν απουσία crash."
                : "Records may have been removed or not retained by the manufacturer. Zero records do not prove absence of crashes.");
    } else {
        logLabelWarnValue(gr ? "Σύνοψη ιστορικού" : "History summary", unavailable);
        if (!records.isEmpty()) logInfo(gr
                ? "Ακολουθούν μόνο οι εγγραφές που ήταν προσβάσιμες· δεν αποτελούν πλήρες σύνολο."
                : "Only accessible records follow; these are not a complete total.");
    }
    for (String record : records)
        logLabelWarnValue(gr ? "Προσβάσιμη καταγραφή" : "Accessible record", record);
    logLabelWarnValue(gr ? "Δείκτης κινδύνου σταθερότητας" : "Stability risk score", gr
            ? "Δεν υπολογίζεται: οι διαθέσιμες καταγραφές δεν επαρκούν για αξιόπιστη συνολική βαθμολογία."
            : "Not calculated: available records do not support a reliable overall score.");

    appendHtml("<br>");
    logInfo(gr ? "Στιγμιαία δοκιμή I/O αποθηκευτικού" : "Storage I/O spot test");
    long latency = storageLatencyProbe();
    if (latency < 0) logLabelWarnValue(gr ? "Μέτρηση" : "Reading",
            gr ? "Μη διαθέσιμα δεδομένα." : "Data unavailable.");
    else {
        logInfo("I/O: " + latency + " ms");
        logInfo(gr
                ? "Μεμονωμένη μέτρηση αρχείου της εφαρμογής. Δεν αποδεικνύει φθορά NAND ή συνολική σταθερότητα συσκευής."
                : "A single measurement of an app file. It does not establish NAND wear or overall device stability.");
    }
    GELServiceLog.info("SUMMARY: CRASH_ORIGIN=UNCLEAR; HISTORY_COVERAGE="
            + (coverage.complete() ? "AVAILABLE_RECORDS_ONLY" : "UNAVAILABLE_OR_PARTIAL"));
    appendHtml("<br>");
    logInfo(gr ? "Το Lab 25 ολοκληρώθηκε με τους παραπάνω περιορισμούς."
            : "Lab 25 finished with the limitations above.");
    logLine();
} // END lab25CrashHistory()

// ============================================================
// SMALL helper
// Reads first lines of DropBox entry safely
// ============================================================
private String readDropBoxEntry(DropBoxManager.Entry ent) {

    if (ent == null) return "(no text)";

    try (InputStream is = ent.getInputStream()) {

        if (is == null) return "(no text)";

        BufferedReader br =
                new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();
        String line;
        int lines = 0;

        while ((line = br.readLine()) != null && lines < 10) {

            if (line.trim().isEmpty()) continue;

            sb.append(line.trim()).append(" ");
            lines++;
        }

        String txt = sb.toString().trim();

        if (txt.length() > 160)
            txt = txt.substring(0, 160) + "...";

        return txt.isEmpty() ? "(no text)" : txt;

    } catch (Throwable ignored) {
        return "(unreadable)";
    }
}

private String safeStr(String s) {
return (s == null || s.trim().isEmpty()) ? "(no data)" : s;
}

// ============================================================
// LAB 25 — CRASH PATTERN ANALYZER (HEURISTIC)
// Distinguishes software vs possible hardware patterns
// ============================================================
private int analyzeCrashPattern(List<String> details,
                                List<String> findings) {

    int hwScore = 0;

    if (details == null || details.isEmpty())
        return 0;

    for (String d : details) {

        if (d == null) continue;

        String t = d.toLowerCase(Locale.US);

        // ----------------------------------------------------
        // Kernel / low level
        // ----------------------------------------------------
        if (t.contains("kernel panic") ||
            t.contains("fatal signal 11") ||
            t.contains("segmentation fault")) {

            hwScore += 15;
            findings.add("Low-level fault signature detected");
        }

        // ----------------------------------------------------
        // memory corruption hints
        // ----------------------------------------------------
        if (t.contains("memory corruption") ||
            t.contains("heap corruption") ||
            t.contains("out of memory") ||
            t.contains("ashmem")) {

            hwScore += 10;
            findings.add("Memory instability indicators");
        }

        // ----------------------------------------------------
        // storage / fs corruption
        // ----------------------------------------------------
        if (t.contains("i/o error") ||
            t.contains("filesystem corruption") ||
            t.contains("ext4 error") ||
            t.contains("f2fs error")) {

            hwScore += 20;
            findings.add("Filesystem / storage error pattern");
        }

        // ----------------------------------------------------
        // system_server crashes
        // ----------------------------------------------------
        if (t.contains("system_server")) {

            hwScore += 8;
            findings.add("system_server crash detected");
        }

        // ----------------------------------------------------
        // repeated native crash patterns
        // ----------------------------------------------------
        if (t.contains("libc.so") ||
            t.contains("abort message")) {

            hwScore += 6;
        }
    }

    return Math.min(hwScore, 100);
}

// ============================================================
// STORAGE LATENCY PROBE (micro benchmark)
// ============================================================
private long storageLatencyProbe() {

    File f = null;
    FileOutputStream fos = null;
    FileInputStream fis = null;

    try {

        byte[] data = new byte[4096];
        new java.util.Random().nextBytes(data);

        f = new File(getCacheDir(), "gel_io_probe.tmp");

        // warmup write
        try {
            FileOutputStream warm = new FileOutputStream(f);
            warm.write(data);
            warm.close();
        } catch (Throwable ignore) {}

        long t0 = System.nanoTime();

        fos = new FileOutputStream(f);
        fos.write(data);
        fos.flush();
        fos.getFD().sync();

        fis = new FileInputStream(f);
        fis.read(data);

        long t1 = System.nanoTime();

        return (t1 - t0) / 1_000_000;

    } catch (Throwable ignore) {

        return -1;

    } finally {

        try { if (fos != null) fos.close(); } catch (Throwable ignore) {}
        try { if (fis != null) fis.close(); } catch (Throwable ignore) {}
        try { if (f != null) f.delete(); } catch (Throwable ignore) {}
    }
}

// ============================================================
// LAB 26 — Installed Applications Impact Analysis (FINAL v2 • Full Bilingual • Engine-backed)
// ============================================================

private void lab26AppsFootprint() {
    final boolean gr = AppLang.isGreek(this);
    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 26 — Ορατές εφαρμογές και στοιχεία χρήσης"
               : "LAB 26 — Visible apps and usage evidence");
    logLine();

    // Usage access is optional. No root, all-app or battery health claim.
    AppImpactEngine.ImpactResult r;
    try {
        r = AppImpactEngine.analyze(this, false);
    } catch (Throwable t) {
        logLabelWarnValue(gr ? "Κατάσταση" : "Status",
                gr ? "INCONCLUSIVE — η συλλογή απέτυχε"
                   : "INCONCLUSIVE — collection failed");
        GELServiceLog.info("SUMMARY: APPS_IMPACT=INCONCLUSIVE");
        return;
    }
    if (r == null || Lab26Evidence.packageCoverage(
            r.packageQuerySucceeded, r.totalPkgs)
            == Lab26Evidence.PackageCoverage.UNAVAILABLE) {
        logLabelWarnValue(gr ? "Ορατότητα εφαρμογών" : "App visibility",
                gr ? "UNAVAILABLE — δεν επιστράφηκε αξιόπιστη λίστα"
                   : "UNAVAILABLE — no reliable package list");
        GELServiceLog.info("SUMMARY: APPS_IMPACT=INCONCLUSIVE");
        return;
    }

    logLabelWarnValue(gr ? "Κάλυψη" : "Coverage",
            gr ? "OBSERVED_LIMITED — μόνο πακέτα ορατά στην εφαρμογή"
               : "OBSERVED_LIMITED — only packages visible to this app");
    logLabelValue(gr ? "Ορατά πακέτα" : "Visible packages",
            (gr ? "Σύνολο: " : "Total: ") + r.totalPkgs
            + (gr ? " | Χρήστη: " : " | User: ") + r.userApps
            + (gr ? " | Συστήματος: " : " | System: ") + r.systemApps);
    logLabelWarnValue(gr ? "Όριο Android" : "Android limitation",
            gr ? "Δεν τεκμηριώνεται πλήρης κατάλογος εφαρμογών ή μηδενικές κρυφές εφαρμογές."
               : "No complete inventory or absence of hidden packages is established.");

    appendHtml("<br>");
    logInfo(gr ? "Δηλωμένες δυνατότητες ορατών εφαρμογών"
               : "Declared capabilities of visible apps");
    logLine();
    logLabelValue(gr ? "Πιθανό background / Πολλές δηλώσεις"
                     : "Background potential / Multiple declarations",
            r.bgCapable + " / " + r.permHeavy);
    logLabelValue("Boot / Location / Mic / Camera",
            r.bootAware + " / " + r.locationLike + " / "
            + r.micLike + " / " + r.cameraLike);
    logLabelValue("Overlay / VPN / Storage / Notifications",
            r.overlayLike + " / " + r.vpnLike + " / "
            + r.storageLike + " / " + r.notifLike);
    logLabelWarnValue(gr ? "Ερμηνεία" : "Interpretation",
            gr ? "Οι δηλώσεις manifest δεν αποδεικνύουν ενεργή χρήση, φόρτο CPU ή κατανάλωση ενέργειας."
               : "Manifest declarations do not prove active use, CPU load or energy consumption.");

    appendHtml("<br>");
    logInfo(gr ? "Χρόνος χρήσης από Android" : "Android-reported usage time");
    logLine();
    if (!r.usageAccessOk) {
        logLabelWarnValue("Usage Access",
                gr ? "Δεν έχει δοθεί — η βασική αποτύπωση παραμένει διαθέσιμη"
                   : "Not granted — basic observation remains available");
    } else if (!r.usageQuerySucceeded || !r.usageRecordsObserved) {
        logLabelWarnValue(gr ? "Στοιχεία" : "Evidence",
                gr ? "UNAVAILABLE — δεν υπάρχουν αξιοποιήσιμα στατιστικά"
                   : "UNAVAILABLE — no usable usage records");
    } else if (r.topBatteryExposure == null || r.topBatteryExposure.isEmpty()) {
        logLabelWarnValue(gr ? "Χρήση" : "Usage",
                gr ? "Δεν βρέθηκαν θετικές παρατηρήσεις σε ορατές εφαρμογές"
                   : "No positive observations for visible apps");
    } else {
        int n = Math.min(10, r.topBatteryExposure.size());
        for (int i = 0; i < n; i++) {
            AppImpactEngine.AppScore app = r.topBatteryExposure.get(i);
            if (app == null || app.fgMs24h <= 0) continue;
            logLabelValue(app.safeLabel(),
                    (gr ? "Ημερήσια διαστήματα Android: " : "Android daily buckets: ")
                    + (app.fgMs24h / 60000L) + (gr ? " λεπτά" : " min"));
        }
    }
    logLabelWarnValue(gr ? "Χρονικό όριο" : "Time-window limit",
            gr ? "Τα ημερήσια διαστήματα μπορεί να ξεπερνούν το ζητούμενο 24ωρο."
               : "Daily aggregation may extend beyond the requested 24-hour window.");

    appendHtml("<br>");
    logInfo(gr ? "Κίνηση δικτύου" : "Network traffic");
    logLine();
    if (r.topDataConsumers == null || r.topDataConsumers.isEmpty()) {
        logLabelWarnValue(gr ? "Άλλες εφαρμογές" : "Other apps",
                gr ? "UNAVAILABLE — το Android 7+ αποκρύπτει τα TrafficStats άλλων UID"
                   : "UNAVAILABLE — Android 7+ hides other UIDs' TrafficStats");
    } else {
        for (AppImpactEngine.AppScore app : r.topDataConsumers) {
            if (app == null || app.dataBytesSinceBoot < 0) continue;
            logLabelValue(app.safeLabel(), humanBytes(app.dataBytesSinceBoot)
                    + (gr ? " (ίδιο UID, από boot)" : " (own UID, since boot)"));
        }
    }

    appendHtml("<br>");
    logInfo(gr ? "Διαγνωστικό συμπέρασμα" : "Diagnostic conclusion");
    logLine();
    logLabelWarnValue(gr ? "Ετυμηγορία" : "Verdict",
            gr ? "OBSERVED_LIMITED — δεν μετρήθηκε κατανάλωση μπαταρίας, πραγματικός φόρτος ή ασφάλεια εφαρμογών"
               : "OBSERVED_LIMITED — battery drain, actual load and app safety were not measured");
    GELServiceLog.info("SUMMARY: APPS_IMPACT=INCONCLUSIVE");
    logInfo(gr ? "Το Lab 26 ολοκληρώθηκε με ρητά όρια κάλυψης."
               : "Lab 26 completed with explicit coverage limits.");
    logLine();
}

// ============================================================
// ROOT HELPER — BEST EFFORT DIRECTORY SIZE (SAFE)
// ============================================================
private long dirSizeBestEffortRootFast(File root) {

    if (root == null) return 0L;

    try {
        if (!root.exists() || !root.isDirectory()) return 0L;
    } catch (Throwable ignore) {
        return 0L;
    }

    final long HARD_LIMIT = 500_000_000L; // 500MB safety cap

    long total = 0L;
    ArrayDeque<File> stack = new ArrayDeque<>();
    stack.push(root);

    while (!stack.isEmpty()) {

        File dir = stack.pop();
        if (dir == null) continue;

        File[] files;
        try {
            files = dir.listFiles();
        } catch (Throwable ignore) {
            continue;
        }

        if (files == null) continue;

        for (File f : files) {

            if (f == null) continue;

            try {
                if (f.isFile()) {

                    long len = f.length();
                    if (len > 0) {
                        total += len;
                        if (total >= HARD_LIMIT) return total;
                    }

                } else if (f.isDirectory()) {

                    stack.push(f);
                }
            } catch (Throwable ignore) {}
        }
    }

    return total;
}

// ============================================================
// LAB 27 — App Permissions & Privacy (FULL AUTO + RISK SCORE)
// ============================================================
private void lab27PermissionsPrivacy() {
    final boolean gr = AppLang.isGreek(this);
    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 27 — Παρατηρήσιμες χορηγημένες άδειες εφαρμογών"
               : "LAB 27 — Observable granted app permissions");
    logLine();

    // PackageManager permission flags do not require Usage Access.
    // They are not a malware check, AppOps audit, or whole-device inventory.
    PackageManager pm = getPackageManager();
    if (pm == null) {
        logLabelWarnValue(gr ? "Κατάσταση" : "Status",
                gr ? "INCONCLUSIVE — PackageManager μη διαθέσιμο"
                   : "INCONCLUSIVE — PackageManager unavailable");
        GELServiceLog.info("SUMMARY: PRIVACY=INCONCLUSIVE");
        return;
    }

    List<android.content.pm.PackageInfo> packages;
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packages = pm.getInstalledPackages(
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS));
        } else {
            //noinspection deprecation
            packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        }
    } catch (Throwable t) {
        logLabelWarnValue(gr ? "Κατάσταση" : "Status",
                gr ? "INCONCLUSIVE — δεν επιτρέπεται η ανάγνωση πληροφοριών πακέτων"
                   : "INCONCLUSIVE — package information query failed");
        GELServiceLog.info("SUMMARY: PRIVACY=INCONCLUSIVE");
        return;
    }

    if (packages == null || Lab27PermissionEvidence.coverage(true, packages.size())
            == Lab27PermissionEvidence.Coverage.UNAVAILABLE) {
        logLabelWarnValue(gr ? "Κατάσταση" : "Status",
                gr ? "UNAVAILABLE — δεν επιστράφηκε λίστα πακέτων"
                   : "UNAVAILABLE — package list not returned");
        GELServiceLog.info("SUMMARY: PRIVACY=INCONCLUSIVE");
        return;
    }

    String[] groups = { "LOCATION", "MICROPHONE", "CAMERA", "SMS",
            "CONTACTS", "CALLS", "NOTIFICATIONS" };
    Map<String, Integer> grantsByGroup = new HashMap<>();
    for (String key : groups) grantsByGroup.put(key, 0);

    int visiblePackages = 0;
    int visibleUserApps = 0;
    int userAppsWithObservedGrants = 0;
    int unknownFlagPositions = 0;
    List<String> localExamples = new ArrayList<>();

    for (android.content.pm.PackageInfo p : packages) {
        if (p == null || p.packageName == null) continue;
        visiblePackages++;

        ApplicationInfo ai = p.applicationInfo;
        boolean system = ai != null &&
                (ai.flags & (ApplicationInfo.FLAG_SYSTEM
                        | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0;
        if (system) continue;
        visibleUserApps++;

        String[] requested = p.requestedPermissions;
        int[] flags = p.requestedPermissionsFlags;
        if (requested == null) continue;

        List<String> appGrantedGroups = new ArrayList<>();
        for (int i = 0; i < requested.length; i++) {
            String group = Lab27PermissionEvidence.category(requested[i]);
            if (group == null) continue;

            Lab27PermissionEvidence.GrantState state =
                    Lab27PermissionEvidence.granted(requested, flags, i);
            if (state == Lab27PermissionEvidence.GrantState.UNKNOWN) {
                unknownFlagPositions++;
            } else if (state == Lab27PermissionEvidence.GrantState.GRANTED
                    && !appGrantedGroups.contains(group)) {
                appGrantedGroups.add(group);
            }
        }

        if (!appGrantedGroups.isEmpty()) {
            userAppsWithObservedGrants++;
            for (String group : appGrantedGroups) {
                grantsByGroup.put(group, grantsByGroup.get(group) + 1);
            }
            if (localExamples.size() < 8) {
                localExamples.add(safeLabel(pm, p.packageName)
                        + " — " + String.join(", ", appGrantedGroups));
            }
        }
    }

    logLabelWarnValue(gr ? "Κάλυψη" : "Coverage",
            gr ? "OBSERVED_LIMITED — μόνο εφαρμογές ορατές στο PackageManager"
               : "OBSERVED_LIMITED — PackageManager-visible apps only");
    logLabelValue(gr ? "Ορατά πακέτα / εφαρμογές χρήστη"
                     : "Visible packages / user apps",
            visiblePackages + " / " + visibleUserApps);
    logLabelValue(gr ? "Εφαρμογές με ορατές χορηγήσεις"
                     : "Apps with observed grants",
            String.valueOf(userAppsWithObservedGrants));

    appendHtml("<br>");
    logInfo(gr ? "Παρατηρήσεις χορηγημένων αδειών"
               : "Observed granted permission groups");
    logLine();
    for (String group : groups) {
        logLabelValue(group, String.valueOf(grantsByGroup.get(group)));
    }
    if (unknownFlagPositions > 0) {
        logLabelWarnValue(gr ? "Ελλιπή flags δικαιωμάτων"
                             : "Unavailable permission flags",
                String.valueOf(unknownFlagPositions)
                + (gr ? " — UNKNOWN, όχι αρνητικές ή καθαρές"
                      : " — UNKNOWN, not denied or safe"));
    }

    if (!localExamples.isEmpty()) {
        appendHtml("<br>");
        logInfo(gr ? "Παραδείγματα ορατών χορηγήσεων (έως 8)"
                   : "Examples of visible grants (up to 8)");
        logLine();
        for (String example : localExamples) {
            logLabelValue(gr ? "Εφαρμογή" : "App", example);
        }
    }

    appendHtml("<br>");
    logInfo(gr ? "Όρια ιδιωτικότητας" : "Privacy limitations");
    logLine();
    logLabelWarnValue(gr ? "Ειδικές προσβάσεις" : "Special access",
            gr ? "AppOps, Προσβασιμότητα, overlays, ειδοποιήσεις και πρόσβαση χρήσης δεν επαληθεύονται πλήρως εδώ."
               : "AppOps, Accessibility, overlays, notification access and Usage Access are not fully verified here.");
    logLabelWarnValue(gr ? "Ερμηνεία" : "Interpretation",
            gr ? "Η χορήγηση άδειας δεν συνεπάγεται κακόβουλη συμπεριφορά. Η μη εμφάνιση άδειας δεν πιστοποιεί ασφαλή συσκευή."
               : "A granted permission does not imply malicious behavior. Missing observations do not certify safety.");
    logLabelWarnValue(gr ? "Έλεγχος χρήστη" : "User review",
            gr ? "Επιβεβαίωσε τις πραγματικές ρυθμίσεις στο Android Privacy Dashboard και στις πληροφορίες εφαρμογής."
               : "Confirm actual controls in Android Privacy Dashboard and App info settings.");
    logLabelWarnValue(gr ? "Αποτέλεσμα" : "Result",
            gr ? "OBSERVED_LIMITED — κανένα ποσοστό ασφάλειας ή καθαρότητας ιδιωτικότητας"
               : "OBSERVED_LIMITED — no privacy safety or cleanliness percentage");
    GELServiceLog.info("SUMMARY: PRIVACY=INCONCLUSIVE");
    logInfo(gr ? "Το Lab 27 ολοκληρώθηκε με περιορισμένη αποδεικτική κάλυψη."
               : "Lab 27 completed with limited evidence coverage.");
    logLine();
}

// ============================================================
// INTERNAL helpers for Lab 27 (keep inside same lab block)
// ============================================================

private boolean isGrantedFlag(int[] flags, int i) {
try {
if (flags == null || i < 0 || i >= flags.length) return false;
return (flags[i] & android.content.pm.PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0;
} catch (Exception e) {
return false;
}
}

private String safeLabel(PackageManager pm, String pkg) {
try {
ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
CharSequence cs = pm.getApplicationLabel(ai);
return cs != null ? cs.toString() : pkg;
} catch (Exception e) {
return pkg;
}
}

// Weight per dangerous/sensitive permission
private int permissionWeight(String p) {
if (p == null) return 0;

// VERY HIGH RISK
if (p.equals(Manifest.permission.READ_SMS)) return 25;
if (p.equals(Manifest.permission.RECEIVE_SMS)) return 20;
if (p.equals(Manifest.permission.SEND_SMS)) return 25;
if (p.equals(Manifest.permission.READ_CALL_LOG)) return 25;
if (p.equals(Manifest.permission.WRITE_CALL_LOG)) return 25;
if (p.equals(Manifest.permission.CALL_PHONE)) return 15;

// HIGH RISK
if (p.equals(Manifest.permission.RECORD_AUDIO)) return 20;
if (p.equals(Manifest.permission.CAMERA)) return 18;
if (p.equals(Manifest.permission.ACCESS_FINE_LOCATION)) return 18;
if (p.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) return 12;
if (p.equals(Manifest.permission.READ_CONTACTS)) return 15;
if (p.equals(Manifest.permission.WRITE_CONTACTS)) return 15;
if (p.equals(Manifest.permission.GET_ACCOUNTS)) return 10;

// STORAGE (legacy)
if (p.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) return 10;
if (p.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) return 12;

// BACKGROUND / SUSPICIOUS
if (p.equals(Manifest.permission.REQUEST_INSTALL_PACKAGES)) return 20;
if (p.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) return 15;
if (p.equals(Manifest.permission.PACKAGE_USAGE_STATS)) return 15;
if (p.equals(Manifest.permission.BIND_ACCESSIBILITY_SERVICE)) return 25;

return 0;

}

private String shortPerm(String p) {
if (p == null) return "";
int i = p.lastIndexOf('.');
return (i >= 0 && i < p.length() - 1) ? p.substring(i + 1) : p;
}

// ============================================================
// LAB 28 — Hardware Stability & Interconnect / humidity Integrity
// TECHNICIAN MODE — SYMPTOM-BASED TRIAGE ONLY
// ============================================================
private void lab28HardwareStability() {
    final boolean gr = AppLang.isGreek(this);
    if (!requireGelPro(gr
            ? "LAB 28 — Σταθερότητα Υλικού & Ακεραιότητα Διασυνδέσεων"
            : "LAB 28 — Hardware Stability & Interconnect Integrity")) {
        return;
    }

    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 28 — ΠΑΡΑΤΗΡΗΣΕΙΣ ΣΤΑΘΕΡΟΤΗΤΑΣ (ΤΕΚΜΗΡΙΩΜΕΝΕΣ)"
               : "LAB 28 — HARDWARE STABILITY OBSERVATIONS (EVIDENCE-BOUND)");
    logLine();

    // A log line containing 'overheat', 'reboot' or 'failure' is not proof that
    // the symptom happened. This lab uses direct OS observations only.
    BatteryPublicCollector.Snapshot sample = BatteryPublicCollector.collect(this);
    BatteryEvidence status = sample.get("thermal_status_enum");
    BatteryEvidence headroom = sample.get("thermal_headroom");
    BatteryEvidence temp = sample.get("temperature_c");
    BatteryEvidence voltage = sample.get("voltage_mv");
    BatteryEvidence current = sample.get("current_now_ma");
    BatteryEvidence cycles = sample.get("cycle_count");

    logLabelValue(
            gr ? "Πηγή δεδομένων" : "Data source",
            "Android public BatteryManager / PowerManager APIs");

    if (status.isObserved()) {
        logLabelValue(gr ? "Θερμική κατάσταση λειτουργικού" : "OS thermal status",
                String.format(Locale.US, "%.0f (Android status enum)", status.value));
    } else {
        logLabelWarnValue(gr ? "Θερμική κατάσταση" : "Thermal status",
                gr ? "Μη διαθέσιμη μέτρηση" : "Reading unavailable");
    }

    if (headroom.isObserved()) {
        logLabelValue(gr ? "Θερμικό περιθώριο συστήματος" : "Thermal headroom",
                String.format(Locale.US, "%.3f (Android estimate)", headroom.value));
    } else {
        logLabelValue(gr ? "Θερμικό περιθώριο" : "Thermal headroom",
                gr ? "Μη διαθέσιμο / περιορισμός συχνότητας" : "Unavailable/rate-limited");
    }

    if (temp.isObserved()) {
        logLabelValue(gr ? "Αναφερόμενη θερμοκρασία μπαταρίας" : "Reported battery temperature",
                String.format(Locale.US, "%.1f °C", temp.value));
    }

    if (voltage.isObserved()) {
        logLabelValue(gr ? "Αναφερόμενη τάση συστοιχίας" : "Reported pack voltage",
                String.format(Locale.US, "%.0f mV", voltage.value));
    }

    if (current.isObserved()) {
        logLabelValue(gr ? "Αναφερόμενο καθαρό ρεύμα" : "Reported net battery current",
                String.format(Locale.US, "%.2f mA", current.value));
    }

    if (cycles.isObserved()) {
        logLabelValue(gr ? "Αναφερόμενοι κύκλοι" : "Reported charge cycles",
                String.format(Locale.US, "%.0f", cycles.value));
    }

    // Longitudinal, locally stored public-API readings. This is a bounded pair
    // of OS-reported values, NOT a measurement of PMIC, NAND or motherboard health.
    final long nowElapsed = SystemClock.elapsedRealtime();
    final long nowWall = System.currentTimeMillis();
    SharedPreferences lab28History =
            getSharedPreferences("GEL_LAB28_PUBLIC_EVIDENCE_V1", MODE_PRIVATE);
    double currentVoltage = voltage.isObserved() ? voltage.value : Double.NaN;
    double currentTemp = temp.isObserved() ? temp.value : Double.NaN;
    Lab28TemporalEvidence.Result paired = Lab28TemporalEvidence.compare(
            lab28History.getLong("elapsed_ms", -1L),
            lab28History.getLong("wall_ms", -1L),
            nowElapsed, nowWall,
            lab28History.contains("voltage_mv")
                    ? lab28History.getFloat("voltage_mv", Float.NaN) : Double.NaN,
            currentVoltage,
            lab28History.contains("temp_c")
                    ? lab28History.getFloat("temp_c", Float.NaN) : Double.NaN,
            currentTemp);
    if (paired.state == Lab28TemporalEvidence.State.OBSERVED_PAIR) {
        logLabelValue(gr ? "Διαδοχικές παρατηρήσεις Android"
                         : "Sequential Android observations",
                String.format(Locale.US, "%.1f min apart", paired.elapsedMs / 60000d));
        if (Double.isFinite(paired.voltageDeltaMv)) {
            logLabelValue(gr ? "Μεταβολή τάσης (αναφερόμενη)"
                             : "Reported voltage difference",
                    String.format(Locale.US, "%+.0f mV", paired.voltageDeltaMv));
        }
        if (Double.isFinite(paired.tempDeltaC)) {
            logLabelValue(gr ? "Μεταβολή θερμοκρασίας (αναφερόμενη)"
                             : "Reported temperature difference",
                    String.format(Locale.US, "%+.1f °C", paired.tempDeltaC));
        }
        logLabelWarnValue(gr ? "Όριο χρονικής συσχέτισης" : "Temporal evidence limit",
                gr ? "Δύο δείγματα χωρίς ελεγχόμενο φορτίο· καμία απόδειξη βλάβης PMIC/μητρικής."
                   : "Two readings without controlled load; no PMIC/board fault established.");
        GELServiceLog.info("SUMMARY: LAB28_SEQUENTIAL_OS_READINGS=OBSERVED; "
                + "SOURCE=BATTERY_PUBLIC_API; HARDWARE_CAUSE=UNVERIFIED");
    } else {
        logLabelWarnValue(gr ? "Χρονική σύγκριση" : "Temporal comparison",
                gr ? "ΜΗ ΕΠΑΡΚΗΣ — απαιτείται δεύτερη έγκυρη παρατήρηση "
                        + "30 δευτ.–2 ώρες αργότερα στην ίδια εκκίνηση."
                   : "INSUFFICIENT — retry with a second valid observation "
                        + "30 seconds–2 hours later in the same boot.");
        GELServiceLog.info("SUMMARY: LAB28_SEQUENTIAL_OS_READINGS=INCONCLUSIVE; "
                + "SOURCE=BATTERY_PUBLIC_API");
    }
    // Do not replace a valid baseline with a rapid duplicate or an empty read.
    if (Double.isFinite(currentVoltage) || Double.isFinite(currentTemp)) {
        if (paired.state != Lab28TemporalEvidence.State.TOO_SOON) {
            SharedPreferences.Editor save = lab28History.edit()
                    .putLong("elapsed_ms", nowElapsed)
                    .putLong("wall_ms", nowWall);
            if (Double.isFinite(currentVoltage)) {
                save.putFloat("voltage_mv", (float) currentVoltage);
            } else {
                save.remove("voltage_mv");
            }
            if (Double.isFinite(currentTemp)) {
                save.putFloat("temp_c", (float) currentTemp);
            } else {
                save.remove("temp_c");
            }
            save.apply();
        }
    }

    BatteryEvidenceStore.Record batteryRecord = BatteryEvidenceStore.readLatestLab14(this);
    if (batteryRecord != null && batteryRecord.isFresh(2L * 60L * 60L * 1000L)) {
        logLabelValue(gr ? "Lab 14A — πρόσφατα τεκμήρια" : "Lab 14A — recent evidence",
                batteryRecord.observedSignalCount +
                        (gr ? " απευθείας σήματα" : " directly observed signals"));
    } else {
        logLabelValue(gr ? "Συσχετισμένο ιστορικό" : "Correlated history",
                gr ? "Δεν υπάρχει πρόσφατη επαληθευμένη σειρά συμβάντων"
                   : "No recent corroborated event series");
    }

    logLabelWarnValue(
            gr ? "Τεχνικό συμπέρασμα" : "Technical conclusion",
            gr ? "Δεν επαληθεύονται PMIC, baseband, βλάβη μητρικής, NAND ή επισκευή "
                    + "από μία αποτύπωση ή λέξεις σε logs."
               : "PMIC, baseband, motherboard, NAND failure or prior repair cannot "
                    + "be verified from a snapshot or log keywords.");

    logLabelValue(
            gr ? "Αξιολόγηση" : "Assessment",
            gr ? "ΑΠΑΙΤΟΥΝΤΑΙ ΕΠΑΝΑΛΑΜΒΑΝΟΜΕΝΑ, ΣΥΓΧΡΟΝΙΣΜΕΝΑ ΣΗΜΑΤΑ"
               : "REPEATED, TIME-ALIGNED EVIDENCE REQUIRED");
    logOk(gr ? "Το Lab 28 κατέγραψε διαθέσιμες παρατηρήσεις."
             : "Lab 28 recorded available observations.");
    logLine();
}

// ============================================================
// LAB 28 — Helpers
// ============================================================

private static class Lab28Evidence {
    boolean thermalSpikes;
    boolean chargingGlitch;
    boolean radioInstability;
    boolean sensorFlaps;
    boolean rebootPattern;

    boolean appsHeavyImpact;
    boolean thermalOnlyDuringCharging;

    String crashPattern; // SOFTWARE, MIXED, UNKNOWN
}

private static class Lab28EvidenceReader {

    static Lab28Evidence readFromGELServiceLog() {

        Lab28Evidence ev = new Lab28Evidence();
        ev.crashPattern = "UNKNOWN";

        String log;
        try {
            log = GELServiceLog.getAll();
        } catch (Throwable t) {
            return ev;
        }

        if (log == null || log.trim().isEmpty())
            return ev;

        final String L = log.toLowerCase(Locale.US);

        ev.thermalSpikes = containsAny(L,
                "thermal spike","thermal spikes","abnormal thermal",
                "overheat","overheating","temperature spike","temp spike","thermal behavior");

        ev.thermalOnlyDuringCharging =
                ev.thermalSpikes && containsAny(L,
                        "while charging","during charging","charging only","only while charging");

        ev.chargingGlitch = containsAny(L,
                "charging glitch","power glitch","charging instability",
                "usb disconnect","disconnect while charging","charger unstable");

        ev.radioInstability = containsAny(L,
                "radio instability","network instability","signal drop","no service",
                "wifi disconnect","internet access");

        ev.sensorFlaps = containsAny(L,
                "sensor instability","intermittent readings",
                "proximity","rotation","auto-rotate","sensor unavailable");

        ev.rebootPattern = containsAny(L,
                "random reboots","sudden resets","abnormal reboot",
                "unexpected reboot","uptime");

        boolean crashMention = containsAny(L,
                "crash","anr","freeze","app not responding","fatal exception");
        if (crashMention) ev.crashPattern = "SOFTWARE";

        ev.appsHeavyImpact = containsAny(L,
                "installed applications impact analysis",
                "heavy apps","high app impact","background apps");

        return ev;
    }

    private static boolean containsAny(String hay, String... needles) {
        if (hay == null || hay.isEmpty() || needles == null) return false;
        for (String n : needles) {
            if (n != null && hay.contains(n)) return true;
        }
        return false;
    }
}

// ============================================================
// LAB 29 — Device Authenticity & Parts Integrity (SAFE MODE)
// Only confirmed signals — no heuristics
// ============================================================
private void lab29DeviceAuthenticity() {

    final boolean gr = AppLang.isGreek(this);

    SharedPreferences p =
            getSharedPreferences("GEL_DIAG", MODE_PRIVATE);

    boolean lab14CollapseRisk =
            p.getBoolean("lab14_collapse_risk", false);

    boolean lab14SwellingSuspected =
        p.getBoolean("lab14_swelling_risk", false);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 29 — Παρατηρήσεις Hardware (όχι πιστοποίηση γνησιότητας)"
            : "LAB 29 — Hardware observations (not OEM certification)");
    logInfo(gr
        ? "SAFE MODE — εμφανίζονται μόνο επιβεβαιωμένα ευρήματα"
        : "SAFE MODE — only confirmed findings are reported");
    logLine();

    appendHtml("<br>");

if (!isDeviceRooted()) {

    logLabelValue(
            gr ? "Advanced verification"
               : "Advanced verification",
            gr
                    ? "Ορισμένοι πρόσθετοι έλεγχοι δεν είναι διαθέσιμοι χωρίς root"
                    : "Some additional checks are unavailable without root"
    );
}

    // Android platform observations do not authenticate OEM replacement parts.

    // ============================================================
    // BATTERY
    // ============================================================

    appendHtml("<br>");
    logInfo(gr ? "Έλεγχος μπαταρίας" : "Battery check");
    logLine();

    int batteryLevel = getBatteryPercentSafe();
    float voltage = getBatteryVoltageFiltered();

    if (batteryLevel < 0 ||
    Float.isNaN(voltage) ||
    voltage < 2.5f ||
    voltage > 5.5f) {

        logLabelWarnValue(
                gr ? "Μπαταρία" : "Battery",
                gr
                        ? "Ασυνήθιστες τιμές μέτρησης"
                        : "Abnormal measurement values"
        );

    } else {

        logLabelOkValue(
                gr ? "Μπαταρία" : "Battery",
                gr
                        ? "Μετρήσεις φυσιολογικές"
                        : "Measurements normal"
        );
    }

    // ============================================================
    // DISPLAY
    // ============================================================

    appendHtml("<br>");
    logInfo(gr ? "Έλεγχος οθόνης" : "Display check");
    logLine();

    Display display = getWindowManager().getDefaultDisplay();

    float refreshRate = 60f;

    if (display != null)
        refreshRate = display.getRefreshRate();

    if (refreshRate < 30f) {

        logLabelWarnValue(
                gr ? "Οθόνη" : "Display",
                gr
                        ? "Ασυνήθιστος ρυθμός ανανέωσης"
                        : "Unusual refresh rate"
        );

    } else {

        logLabelOkValue(
                gr ? "Οθόνη" : "Display",
                String.format(Locale.US,
                        gr
                                ? "Ρυθμός: %.1f Hz"
                                : "Rate: %.1f Hz",
                        refreshRate)
        );
    }

    // ============================================================
    // CAMERA
    // ============================================================

    appendHtml("<br>");
    logInfo(gr ? "Έλεγχος κάμερας" : "Camera check");
    logLine();

    boolean cameraAvailable =
            getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_ANY);

    if (!cameraAvailable) {

        logLabelWarnValue(
                gr ? "Κάμερα" : "Camera",
                gr
                        ? "Δεν εντοπίστηκε"
                        : "Not detected"
        );

    } else {

        logLabelOkValue(
                gr ? "Κάμερα" : "Camera",
                gr
                        ? "Εντοπίστηκε"
                        : "Detected"
        );
    }

    // ============================================================
    // SENSORS (SAFE)
    // ============================================================

    appendHtml("<br>");
    logInfo(gr ? "Έλεγχος αισθητήρων" : "Sensor check");
    logLine();

    try {

        SensorManager sm =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sm != null) {

            Sensor proximity =
                    sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);

            Sensor light =
                    sm.getDefaultSensor(Sensor.TYPE_LIGHT);

            Sensor accel =
                    sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            boolean missing = false;

            if (proximity == null) {
                missing = true;
                logLabelWarnValue(
                        "Proximity",
                        gr ? "Δεν εντοπίστηκε"
                                : "Not detected"
                );
            }

            if (light == null) {
                missing = true;
                logLabelWarnValue(
                        "Light",
                        gr ? "Δεν εντοπίστηκε"
                                : "Not detected"
                );
            }

            if (accel == null) {
                missing = true;
                logLabelWarnValue(
                        "Accelerometer",
                        gr ? "Δεν εντοπίστηκε"
                                : "Not detected"
                );
            }

            if (!missing) {

                logLabelOkValue(
                        gr ? "Αισθητήρες"
                                : "Sensors",
                        gr
                                ? "Βασικοί αισθητήρες διαθέσιμοι"
                                : "Core sensors available"
                );
            }
        }

    } catch (Throwable ignore) {}

    // ============================================================
    // FINGERPRINT
    // ============================================================

    appendHtml("<br>");
    logInfo(gr ? "Ταυτότητα συστήματος" : "System identity");
    logLine();

    String fingerprint = Build.FINGERPRINT;
    String board = Build.BOARD;
    String hardware = Build.HARDWARE;

    if (fingerprint == null
            || fingerprint.length() < 10
            || fingerprint.contains("unknown")) {

        logLabelWarnValue(
                gr ? "Fingerprint"
                        : "Fingerprint",
                gr
                        ? "Ασυνήθιστη τιμή"
                        : "Unusual value"
        );

    } else {

        logLabelOkValue(
                "Fingerprint",
                fingerprint
        );
    }

    logLabelValue(
            "Board",
            board
    );

    logLabelValue(
            "Hardware",
            hardware
    );

    // ============================================================
    // FLAGS FROM OTHER LABS
    // ============================================================

    boolean sensorFlaps =
            p.getBoolean("lab28_sensor_flaps", false);

    boolean radioInstability =
            p.getBoolean("lab28_radio_instability", false);

    boolean thermalSpike =
            p.getBoolean("lab28_thermal_spike", false);

    boolean rebootPattern =
            p.getBoolean("lab28_reboot_pattern", false);

    boolean instabilityPattern =
            p.getBoolean("lab28_instability_pattern", false);

int instabilityScore = 0;

if (sensorFlaps) instabilityScore += 20;
if (radioInstability) instabilityScore += 20;
if (thermalSpike) instabilityScore += 15;
if (rebootPattern) instabilityScore += 15;
if (instabilityPattern) instabilityScore += 20;

if (lab14CollapseRisk || lab14SwellingSuspected)
    instabilityScore += 10;

    appendHtml("<br>");

    // ============================================================
    // FINAL
    // ============================================================

    appendHtml("<br>");
    logLine();

// ============================================================
// SAVE LAB 29 RESULTS — SINGLE SOURCE FOR LAB 30 / LAB 31
// ============================================================
boolean instabilityDetected = instabilityScore >= 20;

p.edit()

        .remove("lab29_authenticity_score")
        .putBoolean("lab29_provenance_unverified", true)
        .putInt("lab29_instability_score", instabilityScore)
        .putBoolean("lab29_instability_pattern", instabilityDetected)
        .putLong("lab29_ts", System.currentTimeMillis())
        .apply();

logLabelWarnValue(
        gr ? "Γνησιότητα ανταλλακτικών" : "Replacement part authenticity",
        gr ? "ΔΕΝ ΕΠΑΛΗΘΕΥΕΤΑΙ από ενδείξεις Android. Απαιτείται τεκμηρίωση OEM ή φυσική επιθεώρηση."
           : "NOT VERIFIABLE from Android readings. OEM documentation or physical inspection required."
);
logLabelValue(
        gr ? "Εύρος ελέγχου" : "Assessment scope",
        gr ? "Διατίθενται μόνο παρατηρήσεις hardware και ενδείξεις αστάθειας."
           : "Hardware observations and instability indicators only."
);

    appendHtml("<br>");
    logOk(gr
            ? "Το Lab 29 ολοκληρώθηκε."
            : "Lab 29 finished.");
    logLine();
}

// ============================================================
// LAB 30 — Auto Final Diagnosis Summary (GEL Universal AUTO Edition)
// Combines Thermals + Battery + Storage + RAM + Apps + Uptime +
// Security + Privacy + Root + Stability into final scores.
// NOTE (GEL RULE): Whole block ready for copy-paste.
// ============================================================
private void lab30CombineFindings() {

final boolean grGate = AppLang.isGreek(this);

if (!requireGelPro(
        grGate ? "LAB 30 — GEL Device Certification"
               : "LAB 30 — GEL Device Certification")) {
    return;
}

SharedPreferences p =
        getSharedPreferences("GEL_DIAG", MODE_PRIVATE);
        
// Legacy storage-risk heuristics are not validated controller failure evidence.
boolean silentCorruptionRisk = false;

// battery data

BatteryEvidenceStore.Record lab14ReportRecord =
        BatteryEvidenceStore.readLatestLab14(this);
// Whole-device grading needs independently verified coverage of every claimed
// hardware domain. Public API snapshots preserve useful *scoped* evidence,
// but cannot currently establish physical NAND, radio/sensor and PMIC integrity.
boolean validatedCapacity = lab14ReportRecord != null
        && lab14ReportRecord.isCompletedValidRun(2L * 60L * 60L * 1000L)
        && !Double.isNaN(lab14ReportRecord.validatedSoHPercent());
boolean hardwareGradeSupported = Lab30HardwareCoverage.canGrade(
        validatedCapacity, false /* physical storage */,
        false /* radio / sensors */, false /* time-aligned power */);
// Legacy heuristics must never masquerade as verified physical battery health.
float finalScore = Float.NaN;
boolean lab14CollapseRisk = false;
boolean lab14SwellingSuspected = false;
        
// ------------------------------------------------------------
// 🔴 LAB14B (CLEAN READ)
// ------------------------------------------------------------

// 🔴 RAW
float lab14bConsumptionPerHour =
        p.getInt("lab14b_evidence_schema", 0) == 1
        && p.getBoolean("lab14b_rate_valid", false)
        && "BATTERY_MANAGER_CHARGE_COUNTER_ELAPSED".equals(
                p.getString("lab14b_rate_source", ""))
        && p.getLong("lab14b_sample_duration_ms", 0L) >= 240000L
        ? p.getFloat("lab14b_consumption_per_hour", -1f) : -1f;

long ts14b =
        p.getLong("lab14b_ts", 0L);

// ------------------------------------------------------------
// 🔴 DERIVED (RUNTIME)
// ------------------------------------------------------------
float lab14bEstimatedHours = -1f;
float lab14bRemainingNormal = -1f;

if (lab14bConsumptionPerHour > 0f && lab14bBaselineMah > 0f) {

    lab14bEstimatedHours =
            lab14bBaselineMah / lab14bConsumptionPerHour;

    float battPct = (float) getBatteryPercentSafe();

    if (battPct > 0f) {
        float factor = battPct / 100f;

        lab14bRemainingNormal =
                lab14bEstimatedHours * factor; // 🔴 no normalMul
    }

} else {
    // Historical estimates are not proven; do not reuse after migration.
    lab14bEstimatedHours = -1f;
    lab14bRemainingNormal = -1f;
}

// stability flags
// Keyword-only symptom extraction is unvalidated hardware failure evidence.
boolean pmicInstability = false;
boolean sensorBusInstability = false;
boolean sensorFlaps = false;

// storage / controller
// Free space and unstructured logs do not reveal NAND wear/controller health.
boolean nandRisk = false;
boolean controllerRisk = false;

// thermal
// A legacy boolean is not a verified thermal runaway event.
boolean thermalRunawayRisk = false;

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 30 — Αυτόματη Τελική Σύνοψη Διάγνωσης (ΠΛΗΡΩΣ ΑΥΤΟΜΑΤΗ)"
            : "LAB 30 — Auto Final Diagnosis Summary (FULL AUTO)");
    logLine();

    // ============================================================
    // USAGE ACCESS — REQUIRED
    // ============================================================
    if (!hasUsageAccess()) {
        showUsageAccessDialog();
        return;
    }
    
// ------------------------------------------------------------
// 1) THERMALS (from zones + battery temp)
// ------------------------------------------------------------
Map<String, Float> zones = null;
try { zones = readThermalZones(); } catch (Throwable ignored) {}

float batTemp = lab14BatteryTemp();

if (Float.isNaN(batTemp) || batTemp <= 0f || batTemp > 100f) {
    batTemp = Float.NaN;
}

Float cpu  = null, gpu = null, skin = null, pmic = null;
if (zones != null && !zones.isEmpty()) {
cpu  = pickZone(zones, "cpu", "cpu-therm", "big", "little", "tsens", "mtktscpu");
gpu  = pickZone(zones, "gpu", "gpu-therm", "gpuss", "mtkgpu");
skin = pickZone(zones, "skin", "xo-therm", "shell", "surface");
pmic = pickZone(zones, "pmic", "pmic-therm", "power-thermal", "charger", "chg");
}

float maxThermal = maxOf(cpu, gpu, skin, pmic, batTemp);
float avgThermal = avgOf(cpu, gpu, skin, pmic, batTemp);

int thermalScore = Lab30ScoreEvidence.hasThermalReadings(maxThermal, avgThermal, cpu, gpu, skin, pmic)
        ? scoreThermals(maxThermal, avgThermal) : -1;
String thermalFlag = thermalScore < 0 ? "⚪" : colorFlagFromScore(thermalScore);

// ------------------------------------------------------------
// 2) BATTERY HEALTH (light auto inference — FIXED)
// ------------------------------------------------------------
boolean charging = isChargingNow();

// Temperature and SOC cannot establish battery capacity health.
int batteryScore = -1;

String batteryFlag = batteryScore < 0 ? "⚪" : colorFlagFromScore(batteryScore);

// ------------------------------------------------------------
// 🔴 PRIMARY: CONSUMPTION-BASED FLAGS
// ------------------------------------------------------------
boolean batteryConsumptionHigh =
        lab14bConsumptionPerHour > 1200f;

boolean batteryConsumptionVeryHigh =
        lab14bConsumptionPerHour > 1500f;

// ------------------------------------------------------------
// 🔴 SECONDARY: HOURS (DERIVED — ONLY IF VALID)
// ------------------------------------------------------------
float runtimeHours = -1f;

if (lab14bConsumptionPerHour > 0f && lab14bBaselineMah > 0f) {
    runtimeHours = lab14bBaselineMah / lab14bConsumptionPerHour;
} else {
    runtimeHours = lab14bEstimatedHours; // fallback
}

// ------------------------------------------------------------
// 🔴 RUNTIME CLASSIFICATION (SAFE)
// ------------------------------------------------------------
boolean batteryRuntimeWeak =
        runtimeHours > 0f && runtimeHours < 4f;

boolean batteryRuntimeModerate =
        runtimeHours >= 4f && runtimeHours < 6f;

boolean batteryRuntimeGood =
        runtimeHours >= 6f;

// ------------------------------------------------------------
// 3) STORAGE HEALTH
// ------------------------------------------------------------
StorageSnapshot st = readStorageSnapshot();
int storageScore = scoreStorage(st.pctFree, st.totalBytes);
String storageFlag = colorFlagFromScore(storageScore);

// ------------------------------------------------------------
// 4) APPS FOOTPRINT
// ------------------------------------------------------------
AppsSnapshot ap = readAppsSnapshot();
int appsScore = scoreApps(ap.userApps, ap.totalApps);
String appsFlag = colorFlagFromScore(appsScore);

// ------------------------------------------------------------
// 5) RAM HEALTH
// ------------------------------------------------------------
RamSnapshot rm = readRamSnapshot();
int ramScore = scoreRam(rm.pctFree);
String ramFlag = colorFlagFromScore(ramScore);

// ------------------------------------------------------------
// 6) UPTIME / STABILITY
// ------------------------------------------------------------
long upMs = SystemClock.elapsedRealtime();
int stabilityScore = scoreStability(upMs);
String stabilityFlag = colorFlagFromScore(stabilityScore);

// ------------------------------------------------------------
// 7) SECURITY (lockscreen + patch + adb/dev + root)
// ------------------------------------------------------------
SecuritySnapshot sec = readSecuritySnapshot();
int securityScore = scoreSecurity(sec);
String securityFlag = colorFlagFromScore(securityScore);

// ------------------------------------------------------------
// 8) PRIVACY (dangerous granted perms to user apps)
// ------------------------------------------------------------
PrivacySnapshot pr = readPrivacySnapshot();
int privacyScore = scorePrivacy(convertToEnginePrivacy(pr));
String privacyFlag = colorFlagFromScore(privacyScore);

// ------------------------------------------------------------
// 9) AUTHENTICITY / REPAIR INDICATORS (LAB 29)
// ------------------------------------------------------------

boolean lab29ObservationsAvailable =
        p.getBoolean("lab29_provenance_unverified", false);

int lab29InstabilityScore =
        p.getInt("lab29_instability_score", 0);

boolean lab29InstabilityPattern =
        p.getBoolean("lab29_instability_pattern", false);

appendHtml("<br>");
logInfo(gr
        ? "Αυθεντικότητα / Ακεραιότητα συσκευής"
        : "Device authenticity / integrity");
logLine();

if (lab29ObservationsAvailable) {
    logLabelWarnValue(
            gr ? "Γνησιότητα εξαρτημάτων" : "Component authenticity",
            gr ? "ΔΕΝ ΕΠΑΛΗΘΕΥΕΤΑΙ από διαθέσιμα σήματα Android"
               : "NOT VERIFIABLE from available Android signals"
    );
    logLabelValue(
            gr ? "Στοιχεία Lab 29" : "Lab 29 observations",
            gr ? "Παρατηρήσεις διαθέσιμες· η προέλευση OEM απαιτεί ανεξάρτητο έλεγχο"
               : "Observations available; OEM origin requires independent verification"
    );
} else {
    logLabelWarnValue(
            gr ? "Στοιχεία Lab 29" : "Lab 29 observations",
            gr ? "Δεν υπάρχουν αποτελέσματα νέας μορφής· εκτελέστε το Lab 29"
               : "No current-format findings; run Lab 29"
    );
}

if (lab29InstabilityPattern) {

    logLabelWarnValue(
            gr ? "Μοτίβο αστάθειας" : "Instability pattern",
            lab29InstabilityScore + "/100"
    );
}

// ------------------------------------------------------------
// UNIFIED HARDWARE RISK SCORE
// ------------------------------------------------------------
int hardwareRiskScore = 0;

// battery risks (UPDATED)

if (lab14CollapseRisk)
    hardwareRiskScore += 20;

if (lab14SwellingSuspected)
    hardwareRiskScore += 10;

// storage risks
if (nandRisk)
    hardwareRiskScore += 20;

if (controllerRisk)
    hardwareRiskScore += 15;

if (silentCorruptionRisk)
    hardwareRiskScore += 15;

// motherboard stability
if (pmicInstability)
    hardwareRiskScore += 10;

if (sensorBusInstability)
    hardwareRiskScore += 10;

if (thermalRunawayRisk)
    hardwareRiskScore += 10;
    
if (lab29InstabilityPattern)
    hardwareRiskScore += 10;
    
// hard cap — score is always 0..100
hardwareRiskScore = Math.max(
        0,
        Math.min(100, hardwareRiskScore)
);

if (hardwareGradeSupported) {
    logLabelValue(gr ? "Συνολικός δείκτης κινδύνου hardware" : "Unified hardware risk score",
            hardwareRiskScore + "/100");
} else {
    logLabelWarnValue(gr ? "Συνολικός κίνδυνος hardware" : "Overall hardware risk",
            gr ? "ΜΗ ΑΞΙΟΛΟΓΗΜΕΝΟΣ — ανεπαρκής κάλυψη φυσικών υποσυστημάτων"
               : "NOT ASSESSED — insufficient physical hardware coverage");
}

// ------------------------------------------------------------
// AUTOMATIC FAULT CLASSIFICATION ENGINE
// ------------------------------------------------------------
logLine();
logInfo(gr
        ? "Αυτόματη αξιολόγηση hardware:"
        : "Automatic hardware assessment:");

boolean faultDetected = false;

// ------------------------------------------------------------
// BATTERY (UPDATED)
// ------------------------------------------------------------
if (lab14CollapseRisk || finalScore < 60 || lab14SwellingSuspected) {

    faultDetected = true;

    String msg;

    if (lab14CollapseRisk || finalScore < 60) {

        msg = gr
                ? "Πιθανή υποβάθμιση μπαταρίας."
                : "Possible battery degradation detected.";

    } else {

        msg = gr
                ? "Πιθανές ενδείξεις φθοράς μπαταρίας."
                : "Possible signs of battery wear.";
    }

    logLabelWarnValue(
            gr ? "Μπαταρία" : "Battery",
            msg
    );
}

// ------------------------------------------------------------
// ANALYSIS
// ------------------------------------------------------------

if (batteryRuntimeWeak) {

    logLabelWarnValue(
            gr ? "Αυτονομία" : "Battery runtime",
            gr
                    ? "Χαμηλή διάρκεια σε πραγματική χρήση"
                    : "Low real-world battery duration"
    );

    if ((batteryScore >= 0 && batteryScore < 60) || lab14CollapseRisk) {

        logLabelWarnValue(
                gr ? "Πιθανή αιτία" : "Possible cause",
                gr
                        ? "Φθορά ή μειωμένη χωρητικότητα μπαταρίας"
                        : "Battery degradation or reduced capacity"
        );
    }

    else if (thermalRunawayRisk) {

        logLabelWarnValue(
                gr ? "Πιθανή αιτία" : "Possible cause",
                gr
                        ? "Θερμική καταπόνηση"
                        : "Thermal stress"
        );
    }
}

// ------------------------------------------------------------

if (batteryConsumptionVeryHigh) {

    logLabelErrorValue(
            gr ? "Κατανάλωση" : "Consumption",
            gr
                    ? "Πολύ υψηλή κατανάλωση ενέργειας"
                    : "Very high power consumption"
    );

    if (pmicInstability) {

        logLabelWarnValue(
                gr ? "Πιθανή αιτία" : "Possible cause",
                gr
                        ? "Αστάθεια PMIC"
                        : "PMIC instability"
        );
    }
}

// ------------------------------------------------------------

if (batteryRuntimeModerate && batteryConsumptionHigh) {

    logLabelWarnValue(
            gr ? "Συμπεριφορά" : "Behaviour",
            gr
                    ? "Μέτρια αυτονομία με αυξημένη κατανάλωση"
                    : "Moderate runtime with elevated consumption"
    );
}

// ------------------------------------------------------------
// STORAGE
// ------------------------------------------------------------
if (nandRisk || controllerRisk || silentCorruptionRisk) {

    faultDetected = true;

    logLabelWarnValue(
            gr ? "Αποθηκευτικός χώρος"
               : "Storage",
            gr
                    ? "Ενδείξεις πιθανής φθοράς NAND ή controller."
                    : "Possible NAND or storage controller degradation."
    );
}

// ------------------------------------------------------------
// THERMAL
// ------------------------------------------------------------
if (thermalRunawayRisk) {

    faultDetected = true;

    logLabelWarnValue(
            gr ? "Θερμική συμπεριφορά"
               : "Thermal behaviour",
            gr
                    ? "Εντοπίστηκε μοτίβο θερμικής αστάθειας."
                    : "Thermal instability pattern detected."
    );
}

// ------------------------------------------------------------
// PMIC / POWER
// ------------------------------------------------------------
if (pmicInstability) {

    faultDetected = true;

    logLabelWarnValue(
            gr ? "Τροφοδοσία συστήματος"
               : "Power management",
            gr
                    ? "Πιθανή αστάθεια PMIC."
                    : "Possible PMIC instability."
    );
}

// ------------------------------------------------------------
// SENSOR BUS
// ------------------------------------------------------------
if (sensorBusInstability) {

    faultDetected = true;

    logLabelWarnValue(
            gr ? "Αισθητήρες"
               : "Sensors",
            gr
                    ? "Πιθανή αστάθεια διαύλου αισθητήρων."
                    : "Possible sensor bus instability."
    );
}

// ------------------------------------------------------------
// LAB 29 INTEGRITY / INSTABILITY
// ------------------------------------------------------------
if (lab29InstabilityPattern) {

    faultDetected = true;

    logLabelWarnValue(
            gr ? "Ακεραιότητα συσκευής"
               : "Device integrity",
            gr
                    ? "Το LAB 29 εντόπισε επαναλαμβανόμενο μοτίβο αστάθειας."
                    : "LAB 29 detected a repeated instability pattern."
    );
}

// ------------------------------------------------------------
// FINAL CLASSIFICATION
// ------------------------------------------------------------
if (!faultDetected) {
    logLabelWarnValue(gr ? "Συμπέρασμα" : "Conclusion",
            gr ? "Ανεπαρκή τεκμήρια για αποκλεισμό βλάβης hardware."
               : "Insufficient evidence to rule out a hardware fault.");
} else {

    logLabelWarnValue(
            gr ? "Σύσταση τεχνικού"
               : "Technician recommendation",
            gr
                    ? "Συνιστάται περαιτέρω έλεγχος ή παρακολούθηση της συσκευής."
                    : "Further monitoring or service inspection recommended."
    );
}

// ------------------------------------------------------------
// PROBABLE ROOT CAUSE ENGINE
// ------------------------------------------------------------
logLine();
logInfo(gr
        ? "Πιθανή βασική αιτία προβλήματος:"
        : "Probable root cause analysis:");

String rootCause = gr ? "Δεν εντοπίστηκε σαφής αιτία"
                      : "No clear root cause detected";

int rootConfidence = 0;

// ------------------------------------------------------------
// BATTERY ROOT CAUSE (UPDATED)
// ------------------------------------------------------------
if (lab14CollapseRisk || finalScore < 60 || lab14SwellingSuspected) {

    rootCause = gr
            ? "Υποβάθμιση μπαταρίας"
            : "Battery degradation";

    // ?? BASE CONFIDENCE
    rootConfidence = 70;

    // 🔴 STRONG SIGNALS
    if (lab14CollapseRisk)
        rootConfidence += 15;

    if (finalScore < 50)
        rootConfidence += 10;

    // 🟡 WEAK SIGNAL (swelling)
    if (lab14SwellingSuspected)
        rootConfidence += 5;

    // cap
    if (rootConfidence > 95)
        rootConfidence = 95;
}

// ------------------------------------------------------------
// STORAGE ROOT CAUSE
// ------------------------------------------------------------
else if (nandRisk || controllerRisk || silentCorruptionRisk) {

    rootCause = gr
            ? "Φθορά NAND ή controller αποθήκευσης"
            : "NAND or storage controller degradation";

    rootConfidence = 75;

    if (silentCorruptionRisk)
        rootConfidence += 10;
}

// ------------------------------------------------------------
// THERMAL ROOT CAUSE
// ------------------------------------------------------------
else if (thermalRunawayRisk) {

    rootCause = gr
            ? "Θερμική αστάθεια hardware"
            : "Hardware thermal instability";

    rootConfidence = 70;
}

// ------------------------------------------------------------
// POWER / PMIC ROOT CAUSE
// ------------------------------------------------------------
else if (pmicInstability) {

    rootCause = gr
            ? "Αστάθεια κυκλώματος τροφοδοσίας (PMIC)"
            : "Power management instability (PMIC)";

    rootConfidence = 65;
}

// ------------------------------------------------------------
// SENSOR BUS ROOT CAUSE
// ------------------------------------------------------------
else if (sensorBusInstability) {

    rootCause = gr
            ? "Αστάθεια διαύλου αισθητήρων"
            : "Sensor bus instability";

    rootConfidence = 60;
}

// ------------------------------------------------------------
// OUTPUT
// ------------------------------------------------------------
logLabelValue(
        gr ? "Πιθανή αιτία"
           : "Most likely cause",
        rootCause
);

if (rootConfidence > 0) {

    String confLabel;

    if (rootConfidence >= 75)
        confLabel = gr ? "Υψηλή" : "High";
    else if (rootConfidence >= 40)
        confLabel = gr ? "Μέτρια" : "Moderate";
    else
        confLabel = gr ? "Χαμηλή" : "Low";

    logLabelValue(
            gr ? "Βεβαιότητα"
               : "Confidence",
            confLabel + " (" + rootConfidence + ")"
    );

} else {

    logLabelWarnValue(gr ? "Κατάσταση" : "Status",
            gr ? "Δεν τεκμηριώθηκε συγκεκριμένη αιτία, όχι απόδειξη απουσίας βλάβης."
               : "No specific cause established; absence of fault not proven.");
}

int adjustedRisk = Math.max(0, hardwareRiskScore - 5);
int reliabilityScore = hardwareGradeSupported ? 100 - adjustedRisk : -1;

// ------------------------------------------------------------
// CONFIDENCE-WEIGHTED ROOT CAUSE
// ------------------------------------------------------------
if (rootConfidence > 0 && hardwareGradeSupported) {

    int weightedConfidence =
            (int) Math.round(
                    rootConfidence *
                    (reliabilityScore / 100.0)
            );

    String confLabel;

    if (weightedConfidence >= 75)
        confLabel = gr ? "Υψηλή" : "High";
    else if (weightedConfidence >= 40)
        confLabel = gr ? "Μέτρια" : "Moderate";
    else
        confLabel = gr ? "Χαμηλή" : "Low";

    logLabelValue(
            gr ? "Σταθμισμένη βεβαιότητα"
               : "Weighted confidence",
            confLabel + " (" + weightedConfidence + ")"
    );

    if (weightedConfidence >= 80) {

        logLabelOkValue(
                gr ? "Ερμηνεία"
                   : "Interpretation",
                gr
                        ? "Η εκτίμηση αιτίας θεωρείται αξιόπιστη."
                        : "Root cause estimation considered reliable."
        );

    } else if (weightedConfidence >= 60) {

        logLabelWarnValue(
                gr ? "Ερμηνεία"
                   : "Interpretation",
                gr
                        ? "Η αιτία είναι πιθανή αλλά απαιτεί επιβεβαίωση."
                        : "Root cause is plausible but requires confirmation."
        );

    } else {

        logLabelWarnValue(
                gr ? "Ερμηνεία"
                   : "Interpretation",
                gr
                        ? "Χαμηλή βεβαιότητα λόγω περιορισμένων δεδομένων."
                        : "Low confidence due to limited diagnostic history."
        );

    }
}

// ------------------------------------------------------------
// FINAL HARDWARE STATUS
// ------------------------------------------------------------
if (!hardwareGradeSupported) {
    logLabelWarnValue(gr ? "Κατάσταση συσκευής" : "Device hardware status",
            gr ? "ΜΗ ΕΠΑΛΗΘΕΥΜΕΝΗ — απουσιάζουν επαρκή τεκμήρια"
               : "UNVERIFIED — insufficient physical evidence");
} else if (hardwareRiskScore >= 60) {

    logLabelErrorValue(
            gr ? "Κατάσταση συσκευής"
               : "Device hardware status",
            gr
                    ? "Υψηλός κίνδυνος hardware αστοχίας."
                    : "High hardware failure risk detected."
    );

} else if (hardwareRiskScore >= 30) {

    logLabelWarnValue(
            gr ? "Κατάσταση συσκευής"
               : "Device hardware status",
            gr
                    ? "Μέτριος κίνδυνος hardware υποβάθμισης."
                    : "Moderate hardware degradation risk."
    );

} else {

    logLabelOkValue(
            gr ? "Κατάσταση συσκευής"
               : "Device hardware status",
            gr
                    ? "Η συσκευή φαίνεται hardware-stable."
                    : "Device hardware appears stable."
    );
}

// ------------------------------------------------------------
// FALSE POSITIVE GUARD ENGINE (UPDATED)
// ------------------------------------------------------------
int riskSignals = 0;

// BATTERY
if (lab14CollapseRisk) {
    riskSignals++;
}
else if (lab14SwellingSuspected &&
        (nandRisk || controllerRisk || thermalRunawayRisk || pmicInstability || sensorBusInstability)) {
    riskSignals++;
}

// STORAGE
if (nandRisk || controllerRisk) riskSignals++;

// THERMAL
if (thermalRunawayRisk) riskSignals++;

// PMIC
if (pmicInstability) riskSignals++;

// SENSOR BUS
if (sensorBusInstability) riskSignals++;

// LAB 29 INSTABILITY
if (lab29InstabilityPattern) riskSignals++;

if (riskSignals == 1) {

    logLabelWarnValue(
            gr ? "Σημείωση αξιολόγησης"
               : "Diagnostic note",
            gr
                    ? "Εντοπίστηκε μόνο ένας μεμονωμένος δείκτης κινδύνου."
                    : "Only a single risk indicator detected."
    );

    logLabelOkValue(
            gr ? "Ερμηνεία"
               : "Interpretation",
            gr
                    ? "Απαιτείται επανάληψη τεστ πριν εξαχθεί συμπέρασμα."
                    : "Repeat diagnostics before concluding."
    );

}

// ------------------------------------------------------------
// DEVICE RELIABILITY RATING
// ------------------------------------------------------------
logLine();
logInfo(gr
        ? "Συνολική αξιολόγηση αξιοπιστίας συσκευής:"
        : "Device reliability rating:");

String rating;
String ratingDesc;

if (!hardwareGradeSupported) {
    rating = "NOT RATED";
    ratingDesc = gr ? "Ανεπαρκή τεκμήρια για βαθμό αξιοπιστίας hardware"
                    : "Insufficient evidence for whole-device hardware reliability grade";
} else if (reliabilityScore >= 92) {

    rating = "A+";
    ratingDesc = gr
            ? "Εξαιρετική αξιοπιστία hardware"
            : "Excellent hardware reliability";

}
else if (reliabilityScore >= 85) {

    rating = "A";
    ratingDesc = gr
            ? "Πολύ καλή κατάσταση hardware"
            : "Very good hardware condition";

}
else if (reliabilityScore >= 70) {

    rating = "B";
    ratingDesc = gr
            ? "Καλή κατάσταση με φυσιολογική φθορά"
            : "Good condition with normal wear";

}
else if (reliabilityScore >= 55) {

    rating = "C";
    ratingDesc = gr
            ? "Μέτρια φθορά hardware"
            : "Moderate hardware wear";

}
else {

    rating = "D";
    ratingDesc = gr
            ? "Αυξημένος κίνδυνος hardware αστοχίας"
            : "Elevated hardware failure risk";

}

logLabelValue(
        gr ? "Βαθμολογία συσκευής"
           : "Device rating",
        rating
);

logLabelValue(
        gr ? "Ερμηνεία"
           : "Interpretation",
        ratingDesc
);

// ------------------------------------------------------------
// GEL HARDWARE INTEGRITY CERTIFICATE
// ------------------------------------------------------------
appendHtml("<br>");
logLine();
logInfo(gr
        ? "Πιστοποιητικό Ακεραιότητας Υλικού GEL"
        : "GEL Hardware Integrity Certificate");
logLine();

boolean certificateWarning = false;

// 🔴 collapse = immediate warning
if (lab14CollapseRisk)
    certificateWarning = true;

// 🟡 swelling = μόνο αν συνοδεύεται από risk
if (lab14SwellingSuspected && hardwareRiskScore >= 40)
    certificateWarning = true;

// 🔥 system-wide condition
if (hardwareRiskScore >= 60)
    certificateWarning = true;

// ------------------------------------------------------------
// CERTIFICATE RESULT
// ------------------------------------------------------------
if (!hardwareGradeSupported || batteryScore < 0 || lab14ReportRecord == null ||
        !lab14ReportRecord.isCompletedValidRun(2L * 60L * 60L * 1000L) ||
        Double.isNaN(lab14ReportRecord.validatedSoHPercent())) {
    logLabelWarnValue(
            gr ? "Κατάσταση Πιστοποιητικού" : "Certificate status",
            gr ? "ΑΝΕΠΑΡΚΗ ΕΠΑΛΗΘΕΥΜΕΝΑ ΣΤΟΙΧΕΙΑ — δεν εκδίδεται πιστοποίηση"
               : "INSUFFICIENT VERIFIED EVIDENCE — certification withheld");
} else if (!certificateWarning && reliabilityScore >= 85) {

    logLabelOkValue(
            gr ? "Κατάσταση Πιστοποιητικού"
               : "Certificate status",
            gr
                    ? "Η συσκευή πέρασε όλους τους ελέγχους ακεραιότητας."
                    : "Device passed all integrity checks."
    );

    logLabelOkValue(
            gr ? "Κατηγορία"
               : "Category",
            "GEL VERIFIED DEVICE"
    );

} else if (!certificateWarning) {

    logLabelWarnValue(
            gr ? "Κατάσταση Πιστοποιητικού"
               : "Certificate status",
            gr
                    ? "Η συσκευή είναι λειτουργική αλλά παρουσιάζει ενδείξεις φυσιολογικής φθοράς."
                    : "Device functional but shows normal wear indicators."
    );

    logLabelValue(
            gr ? "Κατηγορία"
               : "Category",
            "GEL VERIFIED (USED DEVICE)"
    );

} else {

    logLabelWarnValue(
        gr ? "Κατάσταση Πιστοποιητικού"
           : "Certificate status",
        gr
                ? "Εντοπίστηκαν αποκλίσεις ακεραιότητας ή ενδείξεις αστάθειας hardware."
                : "Integrity deviations or hardware instability indicators detected."
);

    logLabelValue(
            gr ? "Κατηγορία"
               : "Category",
            "GEL INSPECTION RECOMMENDED"
    );

}

// ------------------------------------------------------------
// NOTE
// ------------------------------------------------------------
logLabelValue(
        gr ? "Σημείωση"
           : "Note",
        gr
                ? "Η αξιολόγηση βασίζεται σε διαγνωστική ανάλυση hardware και στατιστική συνέπεια μετρήσεων."
                : "Public Android observations do not certify whole-device hardware integrity."
);

// ------------------------------------------------------------
// TECHNICIAN NOTE
// ------------------------------------------------------------
if ("A+".equals(rating) || "A".equals(rating)) {

    logLabelOkValue(
            gr ? "Συμπέρασμα τεχνικού"
               : "Technician note",
            gr
                    ? "Η συσκευή εμφανίζει πολύ καλή σταθερότητα hardware."
                    : "Device hardware appears stable."
    );

}
else if ("B".equals(rating)) {

    logLabelWarnValue(
            gr ? "Συμπέρασμα τεχνικού"
               : "Technician note",
            gr
                    ? "Παρατηρείται φυσιολογική φθορά."
                    : "Normal wear detected."
    );

}
else {
    logLabelWarnValue(gr ? "Συμπέρασμα τεχνικού" : "Technician note",
            !hardwareGradeSupported
                    ? (gr ? "Ανεπαρκή τεκμήρια για συμπέρασμα σταθερότητας hardware."
                          : "Insufficient evidence for hardware stability verdict.")
                    : (gr ? "Συνιστάται παρακολούθηση ή τεχνικός έλεγχος."
                          : "Monitoring or service inspection recommended."));
}

// ------------------------------------------------------------
// 10) FINAL SCORES
// ------------------------------------------------------------
int performanceScore = Lab30ScoreEvidence.performanceScore(
        storageScore, ramScore, appsScore, thermalScore);
int deviceHealthScore = Lab30ScoreEvidence.deviceHealthScore(
        thermalScore, batteryScore, performanceScore, stabilityScore);
// Without battery-health evidence, overall device health cannot be certified.
if (batteryScore < 0) deviceHealthScore = -1;

// ------------------------------------------------------------
// PRINT DETAILS
// ------------------------------------------------------------

appendHtml("<br>");
logInfo(gr ? "Αυτόματη Ανάλυση" : "AUTO Breakdown");
logLine();

// ================= THERMALS =================
appendHtml("<br>");
logInfo(gr ? "Θερμικά" : "Thermals");
logLine();

logLabelOkValue(
        gr ? "Κατάσταση" : "Status",
        thermalScore < 0
                ? (gr ? "ΑΝΕΠΑΡΚΗ ΔΕΔΟΜΕΝΑ" : "INSUFFICIENT DATA")
                : thermalFlag + " (" + thermalScore + ")"
);

if (zones == null || zones.isEmpty()) {

    logLabelWarnValue(
            gr ? "Ζώνες" : "Zones",
            gr
                    ? (Float.isNaN(batTemp) ? "Δεν υπάρχουν διαθέσιμες θερμικές μετρήσεις"
                                            : "Μόνο θερμοκρασία μπαταρίας (" + fmt1(batTemp) + "°C)")
                    : (Float.isNaN(batTemp) ? "No thermal readings available"
                                            : "Battery temperature only (" + fmt1(batTemp) + "°C)")
    );

} else {

    logLabelOkValue(gr ? "Ζώνες" : "Zones", String.valueOf(zones.size()));
    logLabelOkValue(gr ? "Μέγιστη" : "Max", fmt1(maxThermal) + "°C");
    logLabelOkValue(gr ? "Μέση" : "Average", fmt1(avgThermal) + "°C");

    if (cpu  != null) logLabelOkValue("CPU",  fmt1(cpu)  + "°C");
    if (gpu  != null) logLabelOkValue("GPU",  fmt1(gpu)  + "°C");
    if (pmic != null) logLabelOkValue("PMIC", fmt1(pmic) + "°C");
    if (skin != null) logLabelOkValue(gr ? "Επιφάνεια" : "Skin", fmt1(skin) + "°C");

    logLabelOkValue(gr ? "Μπαταρία" : "Battery", fmt1(batTemp) + "°C");
}

appendHtml("<br>");
logInfo(gr ? "Μπαταρία" : "Battery");
logLine();

logLabelOkValue(
        gr ? "Κατάσταση" : "Status",
        batteryScore < 0
                ? (gr ? "ΑΝΕΠΑΡΚΗ ΔΕΔΟΜΕΝΑ" : "INSUFFICIENT DATA")
                : batteryFlag + " (" + batteryScore + ")"
);

logLabelOkValue(
        gr ? "Στοιχεία" : "State",
        (gr ? "Επίπεδο=" : "Level=") +
        (batteryPercent >= 0 ? fmt1((float) batteryPercent) + "%" : (gr ? "Άγνωστο" : "Unknown")) +
        " | Temp=" + fmt1(batTemp) + "°C" +
        " | " + (gr ? "Φόρτιση=" : "Charging=") +
        (charging ? (gr ? "Ναι" : "Yes") : (gr ? "Όχι" : "No"))
);

appendHtml("<br>");
logInfo(gr ? "Αποθήκευση" : "Storage");
logLine();

logLabelOkValue(
        gr ? "Κατάσταση" : "Status",
        storageFlag + " (" + storageScore + ")"
);

logLabelOkValue(
        gr ? "Χρήση" : "Usage",
        (gr ? "Ελεύθερο=" : "Free=") + st.pctFree + "% | " +
        (gr ? "Χρησιμοποιείται=" : "Used=") +
        humanBytes(st.usedBytes) + " / " + humanBytes(st.totalBytes)
);

appendHtml("<br>");
logInfo(gr ? "Αποτύπωμα Εφαρμογών" : "Apps footprint");
logLine();

logLabelOkValue(
        gr ? "Κατάσταση" : "Status",
        appsFlag + " (" + appsScore + ")"
);

logLabelOkValue(
        gr ? "Μετρήσεις" : "Counts",
        "User=" + ap.userApps +
        " | System=" + ap.systemApps +
        " | Total=" + ap.totalApps
);

appendHtml("<br>");
logInfo("RAM");
logLine();

logLabelOkValue(
        gr ? "Κατάσταση" : "Status",
        ramFlag + " (" + ramScore + ")"
);

logLabelOkValue(
        gr ? "Ελεύθερη" : "Free",
        rm.pctFree + "% (" +
        humanBytes(rm.freeBytes) + " / " + humanBytes(rm.totalBytes) + ")"
);

appendHtml("<br>");
logInfo(gr ? "Σταθερότητα / Χρόνος λειτουργίας" : "Stability / Uptime");
logLine();

logLabelOkValue(
        gr ? "Κατάσταση" : "Status",
        stabilityFlag + " (" + stabilityScore + ")"
);

logLabelOkValue(
        gr ? "Χρόνος λειτουργίας" : "Uptime",
        formatUptime(upMs)
);

if (upMs < 2 * 60 * 60 * 1000L) {
    logLabelWarnValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Πρόσφατη επανεκκίνηση (<2h) — πιθανή απόκρυψη αστάθειας"
                    : "Recent reboot (<2h) — instability may be masked"
    );
} else if (upMs > 7L * 24L * 60L * 60L * 1000L) {
    logLabelWarnValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Μεγάλος χρόνος λειτουργίας (>7 ημέρες) — συνιστάται επανεκκίνηση πριν από έλεγχο"
                    : "Long uptime (>7 days) — reboot recommended before deep servicing"
    );
}

appendHtml("<br>");
logInfo(gr ? "Ασφάλεια" : "Security");
logLine();

logLabelOkValue(
        gr ? "Κατάσταση" : "Status",
        securityFlag + " (" + securityScore + ")"
);

logLabelOkValue(
        gr ? "Κλείδωμα συσκευής" : "Secure lock",
        String.valueOf(sec.lockSecure)
);

logLabelOkValue(
        gr ? "Ενημέρωση ασφαλείας" : "Patch level",
        sec.securityPatch == null
                ? (gr ? "Άγνωστο" : "Unknown")
                : sec.securityPatch
);

logLabelOkValue(
        "ADB / Dev",
        "USB=" + sec.adbUsbOn +
        " | Wi-Fi=" + sec.adbWifiOn +
        " | DevOptions=" + sec.devOptionsOn
);

if (sec.rootSuspected)
    logLabelWarnValue(gr ? "Root" : "Root", gr ? "Εντοπίστηκαν ενδείξεις root" : "Suspicion flags detected");

if (sec.testKeys)
    logLabelWarnValue(gr ? "Build" : "Build", gr ? "Υπογεγραμμένο με test-keys (κίνδυνος custom ROM)" : "Signed with test-keys (custom ROM risk)");

appendHtml("<br>");
logInfo(gr ? "Ιδιωτικότητα" : "Privacy");
logLine();

logLabelOkValue(
        gr ? "Κατάσταση" : "Status",
        privacyFlag + " (" + privacyScore + ")"
);

logLabelOkValue(
        gr ? "Επικίνδυνες άδειες" : "Dangerous permissions",
        "Location=" + pr.userAppsWithLocation +
        " | Mic=" + pr.userAppsWithMic +
        " | Camera=" + pr.userAppsWithCamera +
        " | SMS=" + pr.userAppsWithSms
);

// ------------------------------------------------------------
// FINAL VERDICT
// ------------------------------------------------------------

appendHtml("<br>");
logLine();

logInfo(gr
        ? "ΤΕΛΙΚΗ ΔΙΑΓΝΩΣΗ ΣΥΣΚΕΥΗΣ"
        : "FINAL DEVICE DIAGNOSIS");

logLine();

appendHtml("<br>");
logInfo(gr ? "Τελικές Βαθμολογίες" : "FINAL Scores");
logLine();

logLabelOkValue(
        gr ? "Υγεία συσκευής" : "Device health",
        deviceHealthScore < 0
                ? (gr ? "ΑΝΕΠΑΡΚΗ ΔΕΔΟΜΕΝΑ" : "INSUFFICIENT DATA")
                : colorFlagFromScore(deviceHealthScore) + " (" + deviceHealthScore + ")"
);

String deviceClass;

if (deviceHealthScore < 0)
    deviceClass = "INSUFFICIENT DATA";
else if (deviceHealthScore >= 90)
    deviceClass = "GEL CERTIFIED";
else if (deviceHealthScore >= 75)
    deviceClass = "GEL VERIFIED";
else
    deviceClass = "INSPECTION ADVISED";

logLabelValue(
        gr ? "Κατηγορία συσκευής"
           : "Device classification",
        deviceClass
);

logLabelOkValue(
        gr ? "Απόδοση" : "Performance",
        performanceScore < 0
                ? (gr ? "ΑΝΕΠΑΡΚΗ ΔΕΔΟΜΕΝΑ" : "INSUFFICIENT DATA")
                : colorFlagFromScore(performanceScore) + " (" + performanceScore + ")"
);

logLabelOkValue(
        gr ? "Ασφάλεια" : "Security",
        securityFlag
                + " (" + securityScore + ")"
);

logLabelOkValue(
        gr ? "Ιδιωτικότητα" : "Privacy",
        privacyFlag
                + " (" + privacyScore + ")"
);

String verdict =
        finalVerdict(
                deviceHealthScore,
                securityScore,
                privacyScore,
                performanceScore
        );

appendHtml("<br>");
logInfo(gr ? "Τελικό Συμπέρασμα" : "Final verdict");
logLine();

if (deviceHealthScore < 0) {
    logLabelWarnValue(
            gr ? "Αποτέλεσμα" : "Result", verdict
    );
} else if (deviceHealthScore >= 80) {

    if (securityScore < 55 || privacyScore < 55) {
        logLabelWarnValue(
                gr ? "Αποτέλεσμα" : "Result",
                verdict
        );
    } else {
        logLabelOkValue(
                gr ? "Αποτέλεσμα" : "Result",
                verdict
        );
    }

} else if (deviceHealthScore >= 55) {

    logLabelWarnValue(
            gr ? "Αποτέλεσμα" : "Result",
            verdict
    );

} else {

    logLabelErrorValue(
            gr ? "Αποτέλεσμα" : "Result",
            verdict
    );
}

// ------------------------------------------------------------
// HARDWARE TREND ENGINE
// ------------------------------------------------------------
SharedPreferences history =
        getSharedPreferences("gel_hw_history", MODE_PRIVATE);

int prevRisk = history.getInt("hw_last_risk", -1);
float prevBattery = history.getFloat("hw_last_battery_health", -1f);
int prevAging = history.getInt("hw_last_aging_index", -1);

boolean trendDetected = false;

if (hardwareGradeSupported && prevRisk >= 0) {

    int riskDiff = hardwareRiskScore - prevRisk;

    if (riskDiff >= 15)
        trendDetected = true;

    if (prevBattery > 0 && !Float.isNaN(finalScore) &&
            (prevBattery - finalScore) > 8)
        trendDetected = true;

    logLabelValue(
            gr ? "Σύγκριση προηγούμενης διάγνωσης"
               : "Previous diagnosis comparison",
            gr ? "Διαφορά δείκτη: " + riskDiff
               : "Risk difference: " + riskDiff
    );
}

if (trendDetected) {

    logLabelWarnValue(
            gr ? "Τάση υποβάθμισης hardware"
               : "Hardware degradation trend",
            gr
                    ? "Τα δεδομένα δείχνουν πιθανή επιδείνωση της συσκευής."
                    : "Device metrics show a degradation trend."
    );

} else {

    logLabelWarnValue(gr ? "Τάση hardware" : "Hardware trend",
            gr ? "ΜΗ ΕΠΑΛΗΘΕΥΜΕΝΗ — οι προηγούμενες εκτελέσεις δεν αποδεικνύουν σταθερότητα."
               : "UNVERIFIED — run history cannot establish hardware stability.");
}

// ------------------------------------------------------------
// RUN COUNTER + RELIABILITY INDEX
// ------------------------------------------------------------

int runCount = history.getInt("hw_run_count", 0);
runCount++;

history.edit().putInt("hw_run_count", runCount).apply();

logLabelValue(gr ? "Αριθμός προηγούμενων αναφορών" : "Recorded report runs",
        String.valueOf(runCount));
logLabelWarnValue(gr ? "Αξιοπιστία διάγνωσης" : "Diagnostic reliability",
        gr ? "ΜΗ ΑΞΙΟΛΟΓΗΜΕΝΗ — το πλήθος εκτελέσεων δεν αποδεικνύει ακρίβεια."
           : "NOT ASSESSED — number of runs does not establish accuracy.");

if (runCount < 3) {

    logLabelWarnValue(
            gr ? "Σημείωση αξιοπιστίας"
               : "Reliability note",
            gr
                    ? "Η διάγνωση βασίζεται σε περιορισμένο ιστορικό."
                    : "Diagnosis based on limited history."
    );

} else if (runCount >= 7) {

    logLabelWarnValue(gr ? "Στατιστική αξιοπιστία" : "Statistical reliability",
            gr ? "Η επανάληψη χωρίς ανεξάρτητη επαλήθευση δεν πιστοποιεί υλικό."
               : "Repeated runs without independent validation do not certify hardware.");
}

// ------------------------------------------------------------
// STORE HARDWARE HEALTH SNAPSHOT
// ------------------------------------------------------------

history.edit()
        .remove("hw_last_risk")
        .remove("hw_last_battery_health")
        .putBoolean("hw_last_nand_risk", nandRisk)
        .putBoolean("hw_last_controller_risk", controllerRisk)
        .putBoolean("hw_last_thermal_risk", thermalRunawayRisk)
        .putLong("hw_last_timestamp", System.currentTimeMillis())
        .apply();

logLabelOkValue(
        gr ? "Ιστορικό hardware"
           : "Hardware history",
        gr ? "Το snapshot αποθηκεύτηκε"
           : "Run count retained; unverified legacy hardware scores cleared"
);

// ------------------------------------------------------------
// DIAGNOSTIC CONSISTENCY ENGINE
// ------------------------------------------------------------
logLine();
logInfo(gr
        ? "Έλεγχος συνέπειας διαγνωστικών αποτελεσμάτων:"
        : "Diagnostic consistency check:");

boolean diagnosticConflict = false;
int conflictScore = 0;

// Battery instability — stored LAB 14 result
if (lab14CollapseRisk) {

    diagnosticConflict = true;
    conflictScore += 30;

    logLabelWarnValue(
            gr ? "Αστάθεια κυψελών"
               : "Cell instability",
            gr
                    ? "Εντοπίστηκε πιθανή αστάθεια κυψελών μπαταρίας."
                    : "Possible battery cell instability detected."
    );
}

// Storage healthy but controller risk
if (!nandRisk && controllerRisk) {
    diagnosticConflict = true;
    conflictScore += 25;
}

// Thermal normal but PMIC instability
if (!thermalRunawayRisk && pmicInstability) {
    diagnosticConflict = true;
    conflictScore += 20;
}

// Sensors stable but sensor bus instability
if (!sensorFlaps && sensorBusInstability) {
    diagnosticConflict = true;
    conflictScore += 20;
}

logLabelValue(
        gr ? "Δείκτης ασυνέπειας διάγνωσης"
           : "Diagnostic inconsistency index",
        conflictScore + "/100"
);

if (diagnosticConflict) {

    logLabelWarnValue(
            gr ? "Συνέπεια διάγνωσης"
               : "Diagnostic consistency",
            gr
                    ? "Εντοπίστηκαν αντικρουόμενα διαγνωστικά ευρήματα."
                    : "Conflicting diagnostic indicators detected."
    );

    logLabelWarnValue(
            gr ? "Σύσταση"
               : "Recommendation",
            gr
                    ? "Συνιστάται επανάληψη τεστ για επιβεβαίωση."
                    : "Repeat diagnostics to confirm results."
    );

} else {

    logLabelWarnValue(gr ? "Συνέπεια διάγνωσης" : "Diagnostic consistency",
            gr ? "Δεν τεκμηριώθηκε αντίφαση· η πληρότητα των δεδομένων είναι άγνωστη."
               : "No contradiction established; evidence completeness is unknown.");
}

// ------------------------------------------------------------
// GEL DIAGNOSTIC CERTIFICATION
// ------------------------------------------------------------
logLine();
logInfo("GEL Hardware Diagnostic Certification");

// Device rating
logLabelValue(
        gr ? "Βαθμολογία συσκευής"
           : "Device rating",
        rating
);

// Hardware risk
logLabelValue(gr ? "Δείκτης κινδύνου hardware" : "Hardware risk score",
        hardwareGradeSupported ? hardwareRiskScore + "/100"
                               : (gr ? "ΜΗ ΔΙΑΘΕΣΙΜΟΣ" : "UNAVAILABLE"));

// Timestamp
String ts = new java.text.SimpleDateFormat(
        "yyyy-MM-dd HH:mm",
        java.util.Locale.US
).format(new java.util.Date());

logLabelValue(
        gr ? "Χρόνος διάγνωσης"
           : "Diagnostic timestamp",
        ts
);

// ------------------------------------------------------------
// GEL DIAGNOSTIC REPORT ID
// ------------------------------------------------------------
String reportSource =
        Build.MANUFACTURER +
        Build.MODEL +
        ts +
        hardwareRiskScore +
        deviceHealthScore +
        performanceScore;

String reportId;

try {

    java.security.MessageDigest md =
            java.security.MessageDigest.getInstance("SHA-256");

    byte[] hash =
            md.digest(reportSource.getBytes(java.nio.charset.StandardCharsets.UTF_8));

    StringBuilder hex = new StringBuilder();

    for (int i = 0; i < 12; i++) {
        hex.append(String.format("%02X", hash[i]));
    }

    reportId = "GEL-" + hex.toString();

} catch (Throwable e) {

    reportId = "GEL-" + System.currentTimeMillis();

}

logLabelValue(
        gr ? "ID διάγνωσης"
           : "Diagnostic ID",
        reportId
);

// Certification note
logLabelWarnValue(gr ? "Πιστοποίηση" : "Certification",
        gr ? "ΔΕΝ ΕΚΔΙΔΕΤΑΙ — αναφορά περιορισμένων τεκμηρίων."
           : "NOT ISSUED — scoped evidence report, not physical hardware attestation.");

appendHtml("<br>");
logInfo(gr ? "Η αναφορά τεκμηρίων ολοκληρώθηκε χωρίς πιστοποίηση."
           : "Evidence report completed without hardware certification.");
logLine();

appendHtml("<br>");
logOk(gr ? "Το Lab 30 ολοκληρώθηκε." : "Lab 30 finished.");
logLine();
}

// ============================================================
// ======= LAB 30 INTERNAL AUTO HELPERS (SAFE, NO IMPORTS) =====
// ============================================================

private StorageSnapshot readStorageSnapshot() {
StorageSnapshot s = new StorageSnapshot();
try {
StatFs fs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
s.totalBytes = fs.getBlockCountLong() * fs.getBlockSizeLong();
s.freeBytes  = fs.getAvailableBlocksLong() * fs.getBlockSizeLong();
s.usedBytes  = s.totalBytes - s.freeBytes;
s.pctFree = (s.totalBytes > 0) ? (int)((s.freeBytes * 100L) / s.totalBytes) : 0;
} catch (Throwable ignored) {}
return s;
}

private AppsSnapshot readAppsSnapshot() {
AppsSnapshot a = new AppsSnapshot();
try {
PackageManager pm = getPackageManager();
List<ApplicationInfo> apps = pm.getInstalledApplications(0);
if (apps != null) {
a.totalApps = apps.size();
for (ApplicationInfo ai : apps) {
if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) a.systemApps++;
else a.userApps++;
}
}
} catch (Throwable ignored) {}
return a;
}

private RamSnapshot readRamSnapshot() {
RamSnapshot r = new RamSnapshot();
try {
ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
if (am != null) {
am.getMemoryInfo(mi);
r.totalBytes = mi.totalMem;
r.freeBytes  = mi.availMem;
r.pctFree = (r.totalBytes > 0) ? (int)((r.freeBytes * 100L) / r.totalBytes) : 0;
}
} catch (Throwable ignored) {}
return r;
}

private SecuritySnapshot readSecuritySnapshot() {
SecuritySnapshot s = new SecuritySnapshot();

// lock secure
try {
android.app.KeyguardManager km =
(android.app.KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
if (km != null) {
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) s.lockSecure = km.isDeviceSecure();
else s.lockSecure = km.isKeyguardSecure();
}
} catch (Throwable ignored) {}

// patch level
try {
s.securityPatch = Build.VERSION.SECURITY_PATCH;
} catch (Throwable ignored) {}

// ADB / dev options
try {
s.adbUsbOn = Settings.Global.getInt(getContentResolver(),
Settings.Global.ADB_ENABLED, 0) == 1;
} catch (Throwable ignored) {}
try {
s.devOptionsOn = Settings.Global.getInt(getContentResolver(),
Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1;
} catch (Throwable ignored) {}

// ADB Wi-Fi (port property)

try {

    String adbPort =
            System.getProperty("service.adb.tcp.port");

    if (adbPort != null && adbPort.matches("\\d+")) {

        int p = Integer.parseInt(adbPort);

        s.adbWifiOn = (p > 0);
    }

} catch (Throwable ignored) {}

// Root suspicion (no root needed)
s.rootSuspected = detectRootFast();

// test-keys check
try {
String tags = Build.TAGS;
s.testKeys = (tags != null && tags.contains("test-keys"));
} catch (Throwable ignored) {}

return s;

}

private boolean detectRootFast() {
try {
// SU paths
String[] paths = {
"/system/bin/su", "/system/xbin/su", "/sbin/su",
"/system/app/Superuser.apk",
"/data/adb/magisk", "/vendor/bin/su"
};
for (String p : paths) if (new File(p).exists()) return true;

// Magisk / SuperSU packages
PackageManager pm = getPackageManager();
String[] pkgs = {
"com.topjohnwu.magisk",
"eu.chainfire.supersu",
"com.noshufou.android.su",
"com.koushikdutta.superuser"
};
for (String pkg : pkgs) {
try {
pm.getPackageInfo(pkg, 0);
return true;
} catch (Throwable ignored) {}
}
} catch (Throwable ignored) {}
return false;

}

private PrivacySnapshot readPrivacySnapshot() {
PrivacySnapshot p = new PrivacySnapshot();
try {
PackageManager pm = getPackageManager();
List<android.content.pm.PackageInfo> packs =
pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

if (packs == null) return p;

for (android.content.pm.PackageInfo pi : packs) {    
    if (pi == null || pi.applicationInfo == null) continue;    
    ApplicationInfo ai = pi.applicationInfo;    

    // skip system apps    
    if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;    

    p.totalUserAppsChecked++;    

    String[] req = pi.requestedPermissions;    
    int[] flags = pi.requestedPermissionsFlags;    
    if (req == null || flags == null) continue;    

    boolean loc = false, mic = false, cam = false, sms = false;    

    for (int i = 0; i < req.length; i++) {    
        boolean granted = (flags[i] & android.content.pm.PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0;    
        if (!granted) continue;    
        String perm = req[i];    

        if (perm == null) continue;    
        if (perm.contains("ACCESS_FINE_LOCATION") || perm.contains("ACCESS_COARSE_LOCATION"))    
            loc = true;    
        if (perm.contains("RECORD_AUDIO"))    
            mic = true;    
        if (perm.contains("CAMERA"))    
            cam = true;    
        if (perm.contains("READ_SMS") || perm.contains("RECEIVE_SMS") || perm.contains("SEND_SMS"))    
            sms = true;    
    }    

    if (loc) p.userAppsWithLocation++;    
    if (mic) p.userAppsWithMic++;    
    if (cam) p.userAppsWithCamera++;    
    if (sms) p.userAppsWithSms++;    
}

} catch (Throwable ignored) {}
return p;

}

// ------------------------- SCORING --------------------------

private int scoreThermals(float maxT, float avgT) {
int s = 100;
if (maxT >= 70) s -= 60;
else if (maxT >= 60) s -= 40;
else if (maxT >= 50) s -= 20;

if (avgT >= 55) s -= 25;
else if (avgT >= 45) s -= 10;

return clampScore(s);

}

private int scoreBattery(float batTemp, float battPct, boolean charging) {

    int s = 100;

    // 🔴 TEMP (safe)
    if (!Float.isNaN(batTemp)) {

        if (batTemp >= 55f) s -= 55;
        else if (batTemp >= 45f) s -= 30;
        else if (batTemp >= 40f) s -= 15;
    }

    // 🔴 PERCENT (μόνο αν valid ΚΑΙ δεν φορτίζει)
if (!charging && !Float.isNaN(battPct)) {

    if (battPct < 15f) s -= 25;
    else if (battPct < 30f) s -= 10;
}

    return clampScore(s);
}

private int scoreStorage(int pctFree, long totalBytes) {

    int s = 100;

    // 🔴 invalid data guard
    if (pctFree < 0 || pctFree > 100) {
        return 50; // neutral fallback
    }

    // 🔴 free space impact
    if (pctFree < 5) {
        s -= 60;
    } else if (pctFree < 10) {
        s -= 40;
    } else if (pctFree < 15) {
        s -= 25;
    } else if (pctFree < 20) {
        s -= 10;
    }

    // 🔴 very small storage penalty (low-end devices)
    if (totalBytes > 0) {

        long gb = totalBytes / (1024L * 1024L * 1024L);

        if (gb <= 32) {
            s -= 10;
        } else if (gb <= 64) {
            s -= 5;
        }
    }

    return clampScore(s);
}

private int scoreApps(int userApps, int totalApps) {
int s = 100;
if (userApps > 140) s -= 50;
else if (userApps > 110) s -= 35;
else if (userApps > 80) s -= 20;
else if (userApps > 60) s -= 10;

if (totalApps > 220) s -= 10;
return clampScore(s);

}

private int scoreRam(int pctFree) {
int s = 100;
if (pctFree < 8) s -= 60;
else if (pctFree < 12) s -= 40;
else if (pctFree < 18) s -= 20;
else if (pctFree < 25) s -= 10;
return clampScore(s);
}

private int scoreStability(long upMs) {
int s = 100;
if (upMs < 30 * 60 * 1000L) s -= 50;          // <30min uptime
else if (upMs < 2 * 60 * 60 * 1000L) s -= 25; // <2h
else if (upMs > 10L * 24L * 60L * 60L * 1000L) s -= 10; // >10d
return clampScore(s);
}

private int scoreSecurity(SecuritySnapshot sec) {
int s = 100;

if (!sec.lockSecure) s -= 30;

// old patch
if (sec.securityPatch != null && sec.securityPatch.length() >= 4) {
// rough heuristic: if patch year < current year-2 => penalty
try {
int y = Integer.parseInt(sec.securityPatch.substring(0, 4));
int curY = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
if (y <= curY - 3) s -= 30;
else if (y == curY - 2) s -= 15;
} catch (Throwable ignored) {}
} else {
s -= 5; // unknown
}

if (sec.adbUsbOn) s -= 25;
if (sec.adbWifiOn) s -= 35;
if (sec.devOptionsOn) s -= 10;

if (sec.rootSuspected) s -= 40;
if (sec.testKeys) s -= 15;

return clampScore(s);

}

private int scorePrivacy(iDoctorEngine.PrivacySnapshot pr) {
int s = 100;

// weighted dangerous perms on user apps
int risk = 0;
risk += pr.userAppsWithLocation * 2;
risk += pr.userAppsWithMic * 3;
risk += pr.userAppsWithCamera * 3;
risk += pr.userAppsWithSms * 4;

if (risk > 80) s -= 60;
else if (risk > 50) s -= 40;
else if (risk > 25) s -= 20;
else if (risk > 10) s -= 10;

return clampScore(s);

}

// ------------------------- UTIL ----------------------------

private boolean isChargingNow() {

    try {

        iDoctorEngine eng = iDoctorEngine.get(this);

        return eng.isChargingNowUnified();

    } catch (Throwable ignore) {}

    // fallback μόνο αν engine αποτύχει

    try {

        Intent i = registerReceiver(
                null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );

        int status = (i != null)
                ? i.getIntExtra(
                        BatteryManager.EXTRA_STATUS,
                        -1
                )
                : -1;

        return status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;

    } catch (Throwable ignored) {

        return false;
    }
}

private float maxOf(Float a, Float b, Float c, Float d, float e) {
float m = e;
if (a != null && a > m) m = a;
if (b != null && b > m) m = b;
if (c != null && c > m) m = c;
if (d != null && d > m) m = d;
return m;
}

private float avgOf(Float a, Float b, Float c, Float d, float e) {
float sum = e;
int n = 1;
if (a != null) { sum += a; n++; }
if (b != null) { sum += b; n++; }
if (c != null) { sum += c; n++; }
if (d != null) { sum += d; n++; }
return sum / n;
}

private int clampScore(int s) {
if (s < 0) return 0;
if (s > 100) return 100;
return s;
}

private String colorFlagFromScore(int s) {

    if (s >= 80) return "🟢";
    if (s >= 55) return "🟡";
    return "🔴";
}

private String finalVerdict(int health, int sec, int priv, int perf) {

    final boolean gr = AppLang.isGreek(this);
    if (health < 0 || perf < 0) {
        return gr ? "Ανεπαρκή δεδομένα για συνολική αξιολόγηση ή πιστοποίηση συσκευής."
                  : "Insufficient measurements for a device health verdict or certification.";
    }

// ============================================================
// LEVEL 1 — HEALTHY / NORMAL
// ============================================================
if (health >= 80) {

    if (sec < 55 || priv < 55) {
        return gr
                ? "Κατάσταση συσκευής: ΥΓΙΗΣ.\n" +
                  "Προσοχή: εντοπίστηκαν ζητήματα ιδιωτικότητας ή ασφάλειας.\n" +
                  "Συνιστάται έλεγχος από τον χρήστη."
                : "Device condition: HEALTHY.\n" +
                  "Attention: privacy or security risks detected.\n" +
                  "User review is recommended.";
    }

    return gr
            ? "Κατάσταση συσκευής: ΥΓΙΗΣ.\n" +
              "Δεν απαιτείται τεχνική παρέμβαση."
            : "Device condition: HEALTHY.\n" +
              "No servicing required.";
}

// ============================================================
// LEVEL 2 — OBSERVATION (UNCERTAIN CAUSE)
// ============================================================
if (health >= 55) {

    if (sec < 55 || priv < 55) {
        return gr
                ? "Κατάσταση συσκευής: ΜΕΤΡΙΑ ΥΠΟΒΑΘΜΙΣΗ.\n" +
                  "Προσοχή: εντοπίστηκαν ζητήματα ιδιωτικότητας ή ασφάλειας.\n" +
                  "Συνιστάται έλεγχος από τον χρήστη."
                : "Device condition: MODERATE DEGRADATION.\n" +
                  "Attention: privacy or security risks detected.\n" +
                  "User review is recommended.";
    }

    return gr
            ? "Κατάσταση συσκευής: ΜΕΤΡΙΑ ΥΠΟΒΑΘΜΙΣΗ.\n" +
              "Συνιστάται περαιτέρω παρακολούθηση."
            : "Device condition: MODERATE DEGRADATION.\n" +
              "Further monitoring is recommended.";
}

// ============================================================
// LEVEL 3 — UNATTRIBUTED INSTABILITY
// (Evidence-based — no hardware accusation)
// ============================================================
return gr
        ? "Κατάσταση συσκευής: ΕΝΤΟΠΙΣΤΗΚΕ ΑΣΤΑΘΕΙΑ.\n" +
          "Παρατηρείται υποβάθμιση συστήματος χωρίς επιβεβαιωμένη αιτία λογισμικού.\n" +
          "Η αιτία δεν έχει επιβεβαιωθεί.\n" +
          "Κατηγοριοποίηση: Μη αποδοθείσα αστάθεια συστήματος.\n" +
          "Συνιστώνται περαιτέρω διαγνωστικοί έλεγχοι."
        : "Device condition: INSTABILITY DETECTED.\n" +
          "System degradation observed without a confirmed software cause.\n" +
          "Cause is not confirmed.\n" +
          "Classification: Unattributed system instability.\n" +
          "Further diagnostics are recommended.";

}

private String fmt1(float v) {
return String.format(Locale.US, "%.1f", v);
}

// ============================================================
// LAB 31 — FINAL TECHNICIAN SUMMARY (READ-ONLY)
// Does NOT modify GELServiceLog — only reads it.
// Exports via ServiceReportActivity.
// ============================================================
private void lab31FinalSummary() {

    final boolean gr = AppLang.isGreek(this);

    SharedPreferences p =
            getSharedPreferences("GEL_DIAG", MODE_PRIVATE);

// ------------------------------------------------------------
// LAB14B READ (CLEAN + DERIVED)
// ------------------------------------------------------------

// 🔴 RAW (source of truth)
float lab14bConsumptionPerHour =
        p.getInt("lab14b_evidence_schema", 0) == 1
        && p.getBoolean("lab14b_rate_valid", false)
        && "BATTERY_MANAGER_CHARGE_COUNTER_ELAPSED".equals(
                p.getString("lab14b_rate_source", ""))
        && p.getLong("lab14b_sample_duration_ms", 0L) >= 240000L
        ? p.getFloat("lab14b_consumption_per_hour", -1f) : -1f;

long ts14b =
        p.getLong("lab14b_ts", 0L);

// ------------------------------------------------------------
// 🔴 DERIVED (RUNTIME)
// ------------------------------------------------------------
float lab14bEstimatedHours = -1f;
float lab14bRemainingNormal = -1f;

if (lab14bConsumptionPerHour > 0f && lab14bBaselineMah > 0f) {

    lab14bEstimatedHours =
            lab14bBaselineMah / lab14bConsumptionPerHour;

    float battPct = (float) getBatteryPercentSafe();

    if (battPct > 0f) {

        float factor = battPct / 100f;

        lab14bRemainingNormal =
                lab14bEstimatedHours * factor; // ✅ FIX
    }

} else {

    // Legacy extrapolations have no verified full-charge baseline.
    lab14bEstimatedHours = -1f;
    lab14bRemainingNormal = -1f;
}

// ------------------------------------------------------------
// 🔴 LOG HEADER
// ------------------------------------------------------------
appendHtml("<br>");
logLine();

logInfo(gr
        ? "LAB 31 — ΤΕΛΙΚΗ ΤΕΧΝΙΚΗ ΑΝΑΦΟΡΑ (ΜΟΝΟ ΑΝΑΓΝΩΣΗ)"
        : "LAB 31 — FINAL TECHNICAL REPORT (READ-ONLY)");

logLine();

    // ------------------------------------------------------------
    // 1) READ FULL LOG
    // ------------------------------------------------------------
    String fullLog = GELServiceLog.getAll();

    if (fullLog.trim().isEmpty()) {
        logWarn(gr
                ? "Δεν βρέθηκαν διαγνωστικά δεδομένα. Εκτελέστε πρώτα τα Manual Tests."
                : "No diagnostic data found. Please run Manual Tests first.");
        return;
    }

    // ------------------------------------------------------------
    // 2) DETECT WARNINGS / ERRORS
    // ------------------------------------------------------------
    String[] lines = fullLog.split("\n");
    StringBuilder warnings = new StringBuilder();

    for (String l : lines) {

        if (l == null) continue;

        String low = l.toLowerCase(Locale.US);

        if (low.contains("warning") || low.contains("error")) {
            warnings.append(l).append("\n");
        }
    }

    // ------------------------------------------------------------
    // 3) PRINT WARNINGS SUMMARY
    // ------------------------------------------------------------
    appendHtml("<br>");
    logInfo(gr ? "Σύνοψη διαγνωστικών" : "Diagnostics summary");
    logLine();

    boolean issuesDetected = warnings.length() > 0;

    if (!issuesDetected) {
        // Keyword matching is not structured evidence and cannot certify a clean device.
        logLabelWarnValue(
                gr ? "Κατάσταση συστήματος" : "System status",
                gr ? "Δεν επιβεβαιώνεται απουσία προβλημάτων από μη δομημένα logs"
                   : "Absence of faults cannot be certified from unstructured logs"
        );

    } else {

        logLabelWarnValue(
                gr ? "Κατάσταση συστήματος" : "System status",
                gr
                        ? "Εντοπίστηκαν προειδοποιήσεις / σφάλματα"
                        : "Warnings / errors detected"
        );

        for (String w : warnings.toString().split("\n")) {

            if (w != null && !w.trim().isEmpty()) {

                logLabelWarnValue(
                        gr ? "Ζήτημα" : "Issue",
                        w.trim()
                );
            }
        }
    }

// ------------------------------------------------------------
// 4) BATTERY FINDINGS — INFORMATIONAL ONLY
// LAB 30 remains the single source of truth for device scoring.
// ------------------------------------------------------------
BatteryEvidenceStore.Record lab14Report =
        BatteryEvidenceStore.readLatestLab14(this);
// No uncalibrated cached health score can be accepted as capacity evidence.
float batteryScore = -1f;
float batteryHealth = -1f;
if (lab14Report != null) {
    logLabelValue(gr ? "Lab 14A — καταγεγραμμένα σήματα" : "Lab 14A — recorded signals",
            lab14Report.observedSignalCount + (gr ? " διαθέσιμα" : " observed"));
    logLabelWarnValue(gr ? "Υγεία χωρητικότητας" : "Battery capacity health",
            gr ? "Δεν υπάρχει επαληθευμένο ποσοστό SoH για αυτή τη συσκευή"
               : "No verified capacity SoH percentage for this device");
}
        
// ------------------------------------------------------------
// TECHNICIAN FINDINGS — NO DEVICE RE-SCORING
// ------------------------------------------------------------
appendHtml("<br>");
logInfo(gr
        ? "Ευρήματα τεχνικού ελέγχου"
        : "Technician findings");
logLine();

// ------------------------------------------------------------
// BATTERY FINDING
// ------------------------------------------------------------
float finalBatteryHealth = -1f;

if (batteryHealth >= 0f && batteryHealth <= 100f) {
    finalBatteryHealth = batteryHealth;
} else if (batteryScore >= 0f && batteryScore <= 100f) {
    finalBatteryHealth = batteryScore;
}

if (finalBatteryHealth >= 0f) {

    if (finalBatteryHealth >= 80f) {

        logLabelOkValue(
                gr ? "Κατάσταση μπαταρίας" : "Battery condition",
                gr
                        ? "Δεν εντοπίστηκαν σημαντικές ενδείξεις φθοράς"
                        : "No significant battery wear indicators detected"
        );

    } else if (finalBatteryHealth >= 60f) {

        logLabelWarnValue(
                gr ? "Κατάσταση μπαταρίας" : "Battery condition",
                gr
                        ? "Εντοπίστηκαν ενδείξεις μέτριας υποβάθμισης"
                        : "Moderate degradation indicators detected"
        );

    } else {

        logLabelWarnValue(
                gr ? "Κατάσταση μπαταρίας" : "Battery condition",
                gr
                        ? "Εντοπίστηκαν σημαντικές ενδείξεις φθοράς"
                        : "Significant battery wear indicators detected"
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Κατάσταση μπαταρίας" : "Battery condition",
            gr
                    ? "Δεν υπάρχουν επαρκή δεδομένα από το LAB 14"
                    : "Insufficient LAB 14 data"
    );
}

// ------------------------------------------------------------
// BATTERY ENDURANCE FINDING — INFORMATIONAL ONLY
// ------------------------------------------------------------
if (lab14bConsumptionPerHour > 0f &&
    lab14bEstimatedHours > 0f) {

    String enduranceText = String.format(
            Locale.US,
            gr
                    ? "%.1f ώρες στο 100%% | %.0f mAh/h"
                    : "%.1f hours at 100%% | %.0f mAh/h",
            lab14bEstimatedHours,
            lab14bConsumptionPerHour
    );

    if (lab14bEstimatedHours >= 7f) {

        logLabelOkValue(
                gr ? "Εκτιμώμενη αυτονομία" : "Estimated endurance",
                enduranceText
        );

    } else if (lab14bEstimatedHours >= 5f) {

        logLabelWarnValue(
                gr ? "Εκτιμώμενη αυτονομία" : "Estimated endurance",
                enduranceText
        );

    } else {

        logLabelWarnValue(
                gr ? "Εκτιμώμενη αυτονομία" : "Estimated endurance",
                enduranceText
        );

        logLabelWarnValue(
                gr ? "Τεχνικό εύρημα" : "Technician finding",
                gr
                        ? "Η μετρημένη αυτονομία είναι χαμηλότερη από το αναμενόμενο."
                        : "Measured endurance is lower than expected."
        );
    }

    float currentPercent =
            (float) getBatteryPercentSafe();

    if (lab14bRemainingNormal > 0f &&
        currentPercent > 0f) {

        logLabelValue(
                gr ? "Υπόλοιπο κανονικής χρήσης"
                   : "Remaining normal use",
                String.format(
                        Locale.US,
                        gr
                                ? "%.1f ώρες στο %.0f%%"
                                : "%.1f hours at %.0f%%",
                        lab14bRemainingNormal,
                        currentPercent
                )
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Εκτιμώμενη αυτονομία"
               : "Estimated endurance",
            gr
                    ? "Δεν υπάρχουν έγκυρα δεδομένα από το LAB 14B"
                    : "No valid LAB 14B endurance data"
    );
}
   
// ------------------------------------------------------------
// DEVICE INTEGRITY / INSTABILITY FINDINGS
// LAB 29 confirmed measurements — no manipulation accusation
// ------------------------------------------------------------
int lab29AuthenticityScore =
        p.getInt("lab29_authenticity_score", -1);

int lab29InstabilityScore =
        p.getInt("lab29_instability_score", 0);

boolean hwInstability =
        p.getBoolean("lab29_instability_pattern", false);

appendHtml("<br>");

// ------------------------------------------------------------
// AUTHENTICITY / INTEGRITY RESULT
// ------------------------------------------------------------
if (lab29AuthenticityScore >= 0) {

    if (lab29AuthenticityScore >= 90) {

        logLabelOkValue(
                gr ? "Ακεραιότητα συσκευής"
                   : "Device integrity",
                lab29AuthenticityScore + "/100"
        );

    } else if (lab29AuthenticityScore >= 70) {

        logLabelWarnValue(
                gr ? "Ακεραιότητα συσκευής"
                   : "Device integrity",
                lab29AuthenticityScore + "/100 — " +
                        (gr
                                ? "Απαιτείται επανέλεγχος αποκλίσεων"
                                : "Detected deviations require verification")
        );

    } else {

        logLabelWarnValue(
                gr ? "Ακεραιότητα συσκευής"
                   : "Device integrity",
                lab29AuthenticityScore + "/100 — " +
                        (gr
                                ? "Εντοπίστηκαν σημαντικές αποκλίσεις"
                                : "Significant integrity deviations detected")
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Ακεραιότητα συσκευής"
               : "Device integrity",
            gr
                    ? "Δεν υπάρχουν διαθέσιμα αποτελέσματα από το LAB 29"
                    : "No LAB 29 results available"
    );
}

// ------------------------------------------------------------
// INSTABILITY FINDING
// ------------------------------------------------------------
if (hwInstability) {

    logLabelWarnValue(
            gr ? "Μοτίβο αστάθειας"
               : "Instability pattern",
            lab29InstabilityScore + "/100 — " +
                    (gr
                            ? "Απαιτείται τεχνικός επανέλεγχος"
                            : "Technician verification recommended")
    );

} else {

    logLabelOkValue(
            gr ? "Μοτίβο αστάθειας"
               : "Instability pattern",
            gr
                    ? "Δεν εντοπίστηκε"
                    : "Not detected"
    );
}

    // ------------------------------------------------------------
    // 8) EXPORT NOTE
    // ------------------------------------------------------------
    appendHtml("<br>");

    logLabelOkValue(
            gr ? "Εξαγωγή"
                    : "Export",
            gr
                    ? "Χρησιμοποιήστε το κουμπί παρακάτω για δημιουργία επίσημου PDF report"
                    : "Use the button below to generate the official PDF report"
    );

    // enable export button
    enableSingleExportButton();

    appendHtml("<br>");
    logOk(gr
            ? "Το Lab 31 ολοκληρώθηκε."
            : "Lab 31 finished.");
    logLine();
}

// ============================================================
// ENABLE EXISTING EXPORT BUTTON — No duplicates!
// ============================================================
private void enableSingleExportButton() {

    ui.post(() -> {
        if (btnExport == null) return;
        if (lab12AudioBusy.get() || lab5VibrationPending.get()
                || lab3GuidedPending.get() || lab4GuidedPending.get()) {
            // Other callbacks cannot export an incomplete guided sample.
            btnExport.setEnabled(false);
            btnExport.setAlpha(0.5f);
            return;
        }
        btnExport.setEnabled(true);
        btnExport.setAlpha(1f);
    });

}

/* ============================================================
Earpiece test tone — 220Hz (CALL PATH SAFE)
============================================================ */
private void playEarpieceTestTone220Hz(int durationMs) {
try {
int sampleRate = 8000;
int samples = (int) ((durationMs / 1000f) * sampleRate);
if (samples <= 0) samples = sampleRate / 2;

short[] buffer = new short[samples];  
    double freq = 220.0;  

    for (int i = 0; i < samples; i++) {  
        double t = i / (double) sampleRate;  
        buffer[i] = (short) (Math.sin(2 * Math.PI * freq * t) * 9000);  
    }  

    AudioTrack track;  

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  
        track = new AudioTrack(  
                new AudioAttributes.Builder()  
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)  
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)  
                        .build(),  
                new AudioFormat.Builder()  
                        .setSampleRate(sampleRate)  
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)  
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)  
                        .build(),  
                buffer.length * 2,  
                AudioTrack.MODE_STATIC,  
                AudioManager.AUDIO_SESSION_ID_GENERATE  
        );  
    } else {  
        track = new AudioTrack(  
                AudioManager.STREAM_VOICE_CALL,  
                sampleRate,  
                AudioFormat.CHANNEL_OUT_MONO,  
                AudioFormat.ENCODING_PCM_16BIT,  
                buffer.length * 2,  
                AudioTrack.MODE_STATIC  
        );  
    }  

    track.write(buffer, 0, buffer.length);  
    track.play();  

    SystemClock.sleep(durationMs + 80);  

    try { track.stop(); } catch (Throwable ignored) {}  
    try { track.release(); } catch (Throwable ignored) {}  

} catch (Throwable ignored) {}

}

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // ACTION_DIAL does not report whether a call was made. Only ask the user.
    if (requestCode == REQ_LAB12_DIALER) {
        if (lab12DialerPending) {
            lab12DialerPending = false;
            lab12AfterDialerReturn();
        }
        return;
    }

// ============================================================
// LAB 6 — TOUCH GRID
// ============================================================

final boolean gr = AppLang.isGreek(this);

if (requestCode == REQ_LAB6_TOUCH) {

    // Never use old process-static counts as evidence for this result.
    boolean fresh = data != null
            && data.hasExtra(TouchGridTestActivity.EXTRA_TOTAL_ZONES)
            && data.hasExtra(TouchGridTestActivity.EXTRA_REMAINING_ZONES);
    int total = fresh
            ? data.getIntExtra(TouchGridTestActivity.EXTRA_TOTAL_ZONES, -1) : -1;
    int remaining = fresh
            ? data.getIntExtra(TouchGridTestActivity.EXTRA_REMAINING_ZONES, -1) : -1;
    Lab6Evidence.Touch touch = Lab6Evidence.touch(
            resultCode == RESULT_OK, fresh, total, remaining);
    lab6TouchGridObserved = touch == Lab6Evidence.Touch.GRID_SAMPLED;

    appendHtml("<br>");
    logLine();
    logSection(gr ? "LAB 6 — Οθόνη / Αφή" : "LAB 6 — Display / Touch");
    logLine();

    if (touch == Lab6Evidence.Touch.GRID_SAMPLED) {
        logLabelOkValue(gr ? "Δειγματοληψία αφής" : "Touch grid sampling",
                gr ? "Καταγράφηκαν και οι 96 στόχοι της ορατής περιοχής"
                   : "All 96 visible-area targets registered touch events");
        logLabelValue(gr ? "Περιορισμός" : "Limitation",
                gr ? "Δεν επαληθεύτηκαν ολόκληρος ο digitizer, ghost touches ή ακρίβεια αφής"
                   : "Not a full digitizer, ghost-touch or touch-accuracy certification");
    } else if (touch == Lab6Evidence.Touch.USER_STOPPED_INCOMPLETE) {
        logLabelWarnValue(gr ? "Δοκιμή αφής" : "Touch sampling",
                gr ? "Διακόπηκε από τον χρήστη — χωρίς διάγνωση βλάβης"
                   : "Stopped by user — no digitizer fault verdict");
        logLabelValue(gr ? "Στόχοι που καταγράφηκαν" : "Targets observed",
                (total - remaining) + " / " + total);
    } else {
        logLabelWarnValue(gr ? "Δοκιμή αφής" : "Touch sampling",
                gr ? "Ανεπαρκή/μη έγκυρα δεδομένα — ΑΓΝΩΣΤΟ"
                   : "Missing or invalid evidence — INCONCLUSIVE");
    }

    // Visual inspection remains optional and separate. It cannot repair
    // an aborted touch grid into a successful touchscreen verdict.
    logLabelValue(gr ? "Επόμενο βήμα" : "Next step",
            gr ? "Προαιρετική οπτική επιθεώρηση (USER_REPORTED)"
               : "Optional guided visual inspection (USER_REPORTED)");
    startActivityForResult(
            new Intent(this, DisplayProTestActivity.class),
            REQ_LAB6_COLOR);
    return;
}

// ============================================================
// LAB 6 PRO — DISPLAY COLOR / UNIFORMITY / ARTIFACTS
// ============================================================
if (requestCode == REQ_LAB6_COLOR) {

    boolean hasAnswer = data != null && data.hasExtra("display_issues");
    boolean fullPresentation = data != null
            && data.getBooleanExtra("display_steps_completed", false);
    boolean reportedIssue = data != null
            && data.getBooleanExtra("display_issues", false);
    Lab6Evidence.Visual visual = Lab6Evidence.visual(
            resultCode == RESULT_OK, hasAnswer, fullPresentation, reportedIssue);

    appendHtml("<br>");
    logLine();
    logSection(gr ? "LAB 6 PRO — Οπτική επιθεώρηση"
                  : "LAB 6 PRO — Visual inspection");
    logLine();

    if (visual == Lab6Evidence.Visual.USER_REPORTED_NO_ISSUE) {
        logLabelValue(gr ? "Απάντηση χρήστη" : "User response",
                gr ? "Δεν ανέφερε ορατές ανωμαλίες στο παρουσιασμένο μοτίβο"
                   : "No visible artifacts reported during the presented patterns");
        logLabelValue(gr ? "Περιορισμός" : "Limitation",
                gr ? "Δεν μετρήθηκαν φωτεινότητα, burn-in ή χρωματική ακρίβεια"
                   : "Brightness, burn-in and color accuracy were not instrumentally measured");
    } else if (visual == Lab6Evidence.Visual.USER_REPORTED_ISSUE) {
        logLabelWarnValue(gr ? "Απάντηση χρήστη" : "User response",
                gr ? "Αναφέρθηκε οπτική ανωμαλία — τύπος και αιτία μη επαληθευμένα"
                   : "Visible anomaly reported — type and cause unverified");
    } else {
        logLabelWarnValue(gr ? "Οπτική επιθεώρηση" : "Visual inspection",
                gr ? "Ακυρώθηκε, διακόπηκε ή δεν επιστράφηκε έγκυρη απάντηση"
                   : "Cancelled, interrupted or missing a valid guided answer");
    }

    appendHtml("<br>");
    logSection(gr ? "LAB 6 — Συνολικό αποτέλεσμα" : "LAB 6 — Overall result");
    logLine();

    Lab6Evidence.Touch touch = lab6TouchGridObserved
            ? Lab6Evidence.Touch.GRID_SAMPLED
            : Lab6Evidence.Touch.INCONCLUSIVE;
    if (Lab6Evidence.guidedProtocolComplete(touch, visual)) {
        logLabelValue(gr ? "Πρωτόκολλο" : "Guided protocol",
                gr ? "Ολοκληρώθηκε πλέγμα αφής και καταγράφηκε υποκειμενική απάντηση"
                   : "Touch grid sampled and subjective visual answer recorded");
        logLabelValue(gr ? "Διαγνωστικό όριο" : "Diagnostic limit",
                gr ? "Δεν αποτελεί συνολική πιστοποίηση υγείας οθόνης"
                   : "Does not certify full display hardware health");
    } else {
        logLabelWarnValue(gr ? "Πρωτόκολλο" : "Guided protocol",
                gr ? "ΜΕΡΙΚΟ / ΜΗ ΕΠΑΛΗΘΕΥΜΕΝΟ — όχι hardware PASS ή FAIL"
                   : "PARTIAL / UNVERIFIED — no hardware PASS or FAIL");
    }

    enableSingleExportButton();
    return;
}

// ============================================================
// LAB 7 — Rotation + Proximity Sensors
// ============================================================

if (requestCode == 7007) {

    // Successful Activity result alone is not diagnostic evidence.
    String orientationEvidence = data == null ? null : data.getStringExtra("evidence");
    String orientationSource = data == null ? null : data.getStringExtra("orientation_source");
    boolean sourceSupported = "ANDROID_GRAVITY".equals(orientationSource)
            || "ANDROID_ACCELEROMETER".equals(orientationSource);
    boolean poseVerified = resultCode == RESULT_OK && sourceSupported
            && "timed_stable_portrait_then_landscape".equals(orientationEvidence);

    appendHtml("<br>");
    logLine();
    logSection(gr
            ? "LAB 7 — Περιστροφή & Αισθητήρας Εγγύτητας"
            : "LAB 7 — Rotation & Proximity Sensors");
    logLine();

    if (poseVerified) {

        logLabelOkValue(
                gr ? "Πηγή αισθητήρα προσανατολισμού" : "Orientation sensor source",
                "ANDROID_GRAVITY".equals(orientationSource)
                        ? (gr ? "Αισθητήρας βαρύτητας Android" : "Android gravity sensor")
                        : (gr ? "Επιταχυνσιόμετρο Android" : "Android accelerometer")
        );

        logLabelOkValue(
                gr ? "Αλλαγή προσανατολισμού" : "Orientation change",
                gr ? "Επιβεβαιώθηκε" : "Confirmed"
        );

        logLabelOkValue(
                gr ? "Αισθητήρες κίνησης" : "Motion sensors",
                gr ? "Παρατηρήθηκε μετάβαση σταθερών στάσεων" : "Stable pose transition observed"
        );

        logLabelOkValue(
                gr ? "Επόμενο βήμα" : "Next step",
                gr ? "Έλεγχος αισθητήρα εγγύτητας" : "Proximity sensor test"
        );

        // AUTO-START PROXIMITY TEST
        startActivityForResult(
                new Intent(this, ProximityCheckActivity.class),
                8008
        );
        return;

    } else {

        logLabelWarnValue(
                gr ? "Ανίχνευση περιστροφής" : "Rotation detection",
                gr ? "Μη επαληθευμένο" : "Not verified"
        );

        logLabelWarnValue(
                gr ? "Πιθανή αιτία" : "Possible cause",
                gr
                        ? "Δεν τεκμηριώθηκε δεύτερη σταθερή στάση· όχι διάγνωση βλάβης"
                        : "No second stable orientation confirmed; no hardware fault established"
        );

        appendHtml("<br>");
        logOk(gr
                ? "Το Lab 7 ολοκληρώθηκε (περιστροφή μη επιτυχής)."
                : "Lab 7 finished (rotation incomplete).");
        logLine();

        enableSingleExportButton();
        return;
    }
}

// ============================================================
// LAB 7 — PROXIMITY SENSOR
// ============================================================

if (requestCode == 8008) {

    boolean proximityVerified = resultCode == RESULT_OK && data != null
            && "timestamped_far_near_far_proximity_sequence".equals(
                    data.getStringExtra("evidence"));

    if (proximityVerified) {

        logLabelOkValue(
                gr ? "Αισθητήρας εγγύτητας" : "Proximity sensor",
                gr ? "Ανταποκρίθηκε σωστά" : "Responded correctly"
        );

        logLabelOkValue(
                gr ? "Ανίχνευση Κοντά / Μακριά" : "Near / Far detection",
                gr ? "Επιβεβαιώθηκε" : "Confirmed"
        );

        logLabelWarnValue(
                gr ? "Συμπεριφορά οθόνης" : "Screen behavior",
                gr
                        ? "Δεν μετρήθηκε η κατάσταση οθόνης· δεν επιβεβαιώθηκε σβήσιμο σε κλήση"
                        : "Display power was not measured; call-screen blanking is unverified"
        );

    } else {

        logLabelWarnValue(
                gr ? "Αισθητήρας εγγύτητας" : "Proximity sensor",
                gr ? "Μη επαληθευμένη απόκριση" : "Response not verified"
        );

        logLabelWarnValue(
                gr ? "Πιθανή αιτία" : "Possible cause",
                gr
                        ? "Η μεταβολή κοντά/μακριά δεν επαληθεύτηκε· άγνωστη αιτία"
                        : "Near/far event transition unverified; cause unknown"
        );
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 7 ολοκληρώθηκε." : "Lab 7 finished.");
    logLine();

    enableSingleExportButton();
    return;
}
}

// ============================================================
// END OF CLASS
// ============================================================
}

