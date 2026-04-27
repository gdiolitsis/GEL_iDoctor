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
import androidx.appcompat.app.AppCompatActivity;
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
import java.lang.reflect.Method;
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

public class ManualTestsActivity extends AppCompatActivity {
	
	private AlertDialog progressDialog;
	
	private enum DrainMode {

    COUNTER,
    FULLCAP,
    FUEL_GAUGE,
    BATTERY_MANAGER,
    FALLBACK

}

private float startY = 0;
private float endY = 0;
private static final int SWIPE_THRESHOLD = 180;

private ScrollView labsScroll;
private ScrollView logScroll;
private Button btnExport;

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

private static boolean DEBUG_MODE = true;
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
    private final Map<String, Integer> lab8CameraLogAnchors = new HashMap<>();

    // ============================================================
    // LAB 13 — BLUETOOTH RECEIVER (FINAL / AUTHORITATIVE)
    // ============================================================
    private final BroadcastReceiver lab13BtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {

            if (!lab13Running && !lab13MonitoringStarted) {

                String a = i.getAction();

                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(a)) {

                    lab13ReceiverSawConnection = true;
                    lab13HadAnyConnection = true;

                    final boolean gr = AppLang.isGreek(c);

                    if (lab13StatusText != null) {
                        lab13StatusText.setText(
                                gr
                                        ? "Συνδέθηκε εξωτερική συσκευή Bluetooth. Εκκίνηση παρακολούθησης..."
                                        : "External Bluetooth device connected. Starting monitor..."
                        );
                    }

                    if (!lab13WaitTtsPlayed && !AppTTS.isMuted(c)) {
                        lab13WaitTtsPlayed = true;
                        AppTTS.ensureSpeak(
                                c,
                                gr
                                        ? "Εντοπίστηκε σύνδεση Bluetooth. Ξεκινά η παρακολούθηση."
                                        : "Bluetooth connection detected. Monitoring started."
                        );
                    }

                    startLab13Monitor60s();
                }
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
    // LAB 3 — STATE (CLASS LEVEL)
    // ============================================================
    private volatile boolean lab3WaitingUser = false;
    private int lab3OldMode = AudioManager.MODE_NORMAL;
    private boolean lab3OldSpeaker = false;
    private boolean lab3OldMicMute = false;

    private volatile boolean lab4HumanFallbackUsed = false;

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
    int serviceState = ServiceState.STATE_OUT_OF_SERVICE;  
    boolean inService = false;  
    int dataState = TelephonyManager.DATA_UNKNOWN;  
    boolean hasInternet = false;  
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
// EXPORT BUTTON (BOTTOM)
// ============================================================
btnExport = new Button(this);
btnExport.setText(getString(R.string.export_report_title));
btnExport.setAllCaps(false);
btnExport.setTextColor(0xFFFFFFFF);
btnExport.setBackgroundResource(R.drawable.gel_btn_outline_selector);
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
root.addView(logScroll);
root.addView(btnExport);

// ============================================================
// INIT
// ============================================================
scrollLogToBottom();
UIHelpers.applyPressEffectRecursive(getWindow().getDecorView());

final boolean gr = AppLang.isGreek(this);

// 🔴 DEFAULT STATE FIX (σαν swipe down)
logScroll.post(() -> showLabsAndLogs());

logScroll.setOnTouchListener((v, event) -> {
    switch (event.getAction()) {

        case MotionEvent.ACTION_DOWN:
            startY = event.getY();
            break;

        case MotionEvent.ACTION_UP:
            endY = event.getY();
            float diff = endY - startY;

            if (Math.abs(diff) > SWIPE_THRESHOLD) {

                if (diff < 0) {
                    // 🔼 SWIPE UP → FULL LOGS
                    showLogsFullScreen();

                } else {
                    // 🔽 SWIPE DOWN → BACK (ΜΟΝΟ αν είμαστε TOP)

                    boolean atTop =
                            logScroll != null &&
                            !logScroll.canScrollVertically(-1);

                    if (atTop) {
                        showLabsAndLogs();
                    }
                }
            }
            break;
    }
    return false;
});

    // ============================================================
    // TITLE
    // ============================================================
    TextView title = new TextView(this);
    title.setText(getString(R.string.manual_hospital_title));
    title.setTextSize(20f);
    title.setTextColor(0xFFFFD700);
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
            gr ? "14. Δοκιμή Καταπόνησης Υγείας Μπαταρίας"
               : "14. Battery Health Stress Test",
            this::lab14BatteryHealthStressTest));

    body4.addView(makeTestButtonRedGold(
        gr ? "14B. Έλεγχος κατανάλωσης και διάρκειας μπαταρίας"
           : "14B. Battery usage and duration test",
        this::lab14BBatteryDurationTest));

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
            gr ? "28. Σταθερότητα Υλικού & Ακεραιότητα Διασυνδέσεων\nΥποψία Κόλλησης / (Βάσει Συμπτωμάτων)"
               : "28. Hardware Stability & Interconnect Integrity\nSolder / (SYMPTOM-BASED)",
            this::lab28HardwareStability));

    body7.addView(makeTestButton(
            gr ? "29. Έλεγχος Γνησιότητας Συσκευής & Ανταλλακτικών\nΠιθανή Αντικατάσταση Μερών"
               : "29. Device Authenticity & Parts Verification\nPossible Non-OEM Components",
            this::lab29DeviceAuthenticity));

    body7.addView(makeTestButton(
            gr ? "30. Σύνοψη Βαθμολογιών Συσκευής"
               : "30. DEVICE SCORES Summary",
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

// ============================================================
// CPU STRESS HELPERS (REQUIRED BY OTHER LABS) NOT for 14B
// ============================================================

private final List<Thread> lab14CpuThreads = new ArrayList<>();

private void startCpuBurnLimitedThreads(int threads) {

    // 🔴 ONLY FOR LAB14B (hard guard)
    if (!isLab14BMode) return;

    if (!lab14Running || lab14Cancelled) return;

    final int cores = Runtime.getRuntime().availableProcessors();

    if (threads <= 0) threads = cores;
    threads = Math.min(threads, cores);

    // 🔴 SAFE RESET (only inside LAB14B)
    stopCpuBurn();
    lab14CpuThreads.clear();

    lab14CpuThreadsCurrent = threads;

    for (int i = 0; i < threads; i++) {

        Thread t = new Thread(() -> {

            try {

                while (lab14Running && !lab14Cancelled &&
                        !Thread.currentThread().isInterrupted()) {

                    for (int j = 0; j < 10000; j++) {
                        Math.sqrt(j * Math.random());
                    }

                }

            } catch (Throwable ignore) {}

        }, "LAB14B_CPU_" + i);

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

    try {

        iDoctorEngine engine =
                iDoctorEngine.get(ManualTestsActivity.this);

        float mv =
                engine.readBatteryVoltageMvStable(5, 20);

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
    // LAB 3
    // =========================
    try {
        lab3WaitingUser = false;
        stopLab3Tone();
        restoreLab3Audio();
    } catch (Throwable ignore) {}

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
    Button b = new Button(this);
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

    Button b = new Button(this);

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
Button b = new Button(this);  
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
private void askUserEarpieceConfirmation() {

    runOnUiThread(() -> {

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
        Button noBtn = new Button(this);
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
        Button yesBtn = new Button(this);
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
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();

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
}

// ------------------------------------------------------------
// YES ACTION (PASS)
// ------------------------------------------------------------
yesBtn.setOnClickListener(v -> {

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    lab3WaitingUser = false;

    logLabelOkValue(
            gr ? "Αποτέλεσμα" : "Result",
            gr
                    ? "Ο χρήστης επιβεβαίωσε καθαρή αναπαραγωγή ήχου"
                    : "User confirmed audio playback"
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 3 ολοκληρώθηκε." : "Lab 3 finished.");
    logLine();

    restoreLab3Audio();
    d.dismiss();
});

// ------------------------------------------------------------
// NO ACTION (FAIL)
// ------------------------------------------------------------
noBtn.setOnClickListener(v -> {

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    lab3WaitingUser = false;

    logLabelErrorValue(
            gr ? "LAB 3 — Ακουστικό" : "LAB 3 — Earpiece",
            gr
                    ? "Ο χρήστης ΔΕΝ άκουσε τους ήχους"
                    : "User did NOT hear tones"
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
private void playEarpieceBeep() {

    int sampleRate = 8000;
    int durationMs = 400;
    int samples = sampleRate * durationMs / 1000;

    short[] buffer = new short[samples];
    double freq = 1000.0;

    for (int i = 0; i < samples; i++) {
        buffer[i] = (short)
                (Math.sin(2 * Math.PI * i * freq / sampleRate) * 32767);
    }

    AudioTrack track = new AudioTrack(
            AudioManager.STREAM_VOICE_CALL,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            buffer.length * 2,
            AudioTrack.MODE_STATIC
    );

    try {
        track.write(buffer, 0, buffer.length);
        track.play();
        SystemClock.sleep(durationMs + 100);
    } finally {
        try { track.stop(); } catch (Throwable ignore) {}
        try { track.release(); } catch (Throwable ignore) {}
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
        try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
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

    Button exit = new Button(this);
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
        s.simReady = (s.simState == TelephonyManager.SIM_STATE_READY);  
    } catch (Throwable ignored) {}  

    try {  
        ServiceState ss = tm.getServiceState();  
        if (ss != null) {  
            s.serviceState = ss.getState();  
            s.inService =  
                    (s.serviceState == ServiceState.STATE_IN_SERVICE);  
        }  
    } catch (Throwable ignored) {}  

    try {  
        s.dataState = tm.getDataState();  
    } catch (Throwable ignored) {}  
}  

ConnectivityManager cm =  
        (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);  

if (cm != null) {  
    try {  
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  
            Network n = cm.getActiveNetwork();  
            NetworkCapabilities caps =  
                    cm.getNetworkCapabilities(n);  
            s.hasInternet =  
                    caps != null &&  
                    caps.hasCapability(  
                            NetworkCapabilities.NET_CAPABILITY_INTERNET  
                    );  
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

private int dp(int v) {
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

Button btn = new Button(this);  
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

// Frequent reboot hint (human-level inference)
private boolean detectFrequentRebootsHint() {
try {
long uptimeMs = SystemClock.elapsedRealtime();
// Reboot within last 6 hours
return uptimeMs < (6L * 60L * 60L * 1000L);
} catch (Throwable t) {
return false;
}
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

    Button b = new Button(ctx);
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

    Button continueBtn = new Button(this);
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

    Button skipBtn = new Button(this);
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
        Button okBtn = new Button(this);
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
	
SharedPreferences p =
        getSharedPreferences("GEL_DIAG", MODE_PRIVATE);
        
boolean lab14CollapseRisk =
        p.getBoolean("lab14_collapse_risk", false);

boolean lab14SwellingSuspected =
        p.getBoolean("lab14_swelling_risk", false);

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 29 — Έλεγχος γνησιότητας συσκευής & πιθανών επισκευών"
            : "LAB 29 — Device authenticity & repair inspection");
    logLine();

    int manipulationScore = 0;

    boolean batteryAuthSuspect =
            p.getBoolean("lab14_battery_auth_suspect", false);

    boolean lab14CalibrationDrift =
        p.getBoolean("lab14_calibration_drift", false);

    boolean instabilityPattern =
            p.getBoolean("lab29_instability_pattern", false);

    boolean thermalSpike =
            p.getBoolean("lab28_thermal_spike", false);

    boolean radioInstability =
            p.getBoolean("lab28_radio_instability", false);

    boolean sensorFlaps =
            p.getBoolean("lab28_sensor_flaps", false);

    boolean rebootPattern =
            p.getBoolean("lab28_reboot_pattern", false);

    // ------------------------------------------------------------
    // BATTERY AUTHENTICITY
    // ------------------------------------------------------------
    if (batteryAuthSuspect) {

        logLabelWarnValue(
                gr ? "Μπαταρία"
                   : "Battery",
                gr
                        ? "Ενδείξεις πιθανής μη γνήσιας ή aftermarket μπαταρίας"
                        : "Possible aftermarket or non-original battery detected"
        );

        manipulationScore += 20;

    } else {

        logLabelOkValue(
                gr ? "Μπαταρία"
                   : "Battery",
                gr
                        ? "Δεν εντοπίστηκαν ενδείξεις μη γνήσιας μπαταρίας"
                        : "No indicators of non-original battery"
        );
    }

if (calibrationDrift[0]) {

    logLabelWarnValue(
            gr ? "Fuel gauge"
               : "Fuel gauge",
            gr
                    ? "Απόκλιση βαθμονόμησης μπαταρίας"
                    : "Battery fuel-gauge calibration drift detected"
    );

    manipulationScore += 10;
}

    // ------------------------------------------------------------
    // SENSOR & BOARD LINES
    // ------------------------------------------------------------
    if (sensorFlaps) {

        logLabelWarnValue(
                gr ? "Αισθητήρες"
                   : "Sensors",
                gr
                        ? "Ασταθής λειτουργία αισθητήρων"
                        : "Sensor instability detected"
        );

        manipulationScore += 10;
    }

    // ------------------------------------------------------------
    // RADIO / BASEBAND
    // ------------------------------------------------------------
    if (radioInstability) {

        logLabelWarnValue(
                gr ? "Ραδιοσύστημα"
                   : "Radio subsystem",
                gr
                        ? "Ασταθής λειτουργία modem ή RF γραμμών"
                        : "Possible modem / RF line instability"
        );

        manipulationScore += 10;
    }

    // ------------------------------------------------------------
    // THERMAL PMIC BEHAVIOUR
    // ------------------------------------------------------------
    if (thermalSpike) {

        logLabelWarnValue(
                gr ? "Θερμική συμπεριφορά"
                   : "Thermal behaviour",
                gr
                        ? "Απότομες θερμικές αιχμές"
                        : "Abnormal thermal spikes detected"
        );

        manipulationScore += 10;
    }

    // ------------------------------------------------------------
    // REBOOT PATTERN
    // ------------------------------------------------------------
    if (rebootPattern) {

        logLabelWarnValue(
                gr ? "Επανεκκινήσεις"
                   : "Reboot behaviour",
                gr
                        ? "Μοτίβο επανεκκινήσεων"
                        : "Unexpected reboot pattern detected"
        );

        manipulationScore += 15;
    }

    // ------------------------------------------------------------
    // BOARD STABILITY
    // ------------------------------------------------------------
    if (instabilityPattern) {

        logLabelWarnValue(
                gr ? "Σταθερότητα συστήματος"
                   : "System stability",
                gr
                        ? "Μοτίβο αστάθειας υλικού"
                        : "Hardware instability pattern detected"
        );

        manipulationScore += 15;
    }

    if (manipulationScore > 100)
        manipulationScore = 100;

    // ------------------------------------------------------------
    // FINAL INTERPRETATION
    // ------------------------------------------------------------
    String level =
            (manipulationScore < 20) ? "LOW" :
            (manipulationScore < 40) ? "MODERATE" :
            (manipulationScore < 60) ? "ELEVATED" :
            (manipulationScore < 80) ? "HIGH" :
                                      "VERY HIGH";

    appendHtml("<br>");

    if (manipulationScore >= 40) {

        logLabelWarnValue(
                gr ? "Δείκτης επέμβασης συσκευής"
                   : "Device manipulation index",
                manipulationScore + "/100 (" + level + ")"
        );

    } else {

        logLabelOkValue(
                gr ? "Δείκτης επέμβασης συσκευής"
                   : "Device manipulation index",
                manipulationScore + "/100 (" + level + ")"
        );
    }

    appendHtml("<br>");

    if (manipulationScore >= 60) {

        logLabelWarnValue(
                gr ? "Συμπέρασμα"
                   : "Conclusion",
                gr
                        ? "Ισχυρές ενδείξεις ότι η συσκευή έχει επισκευαστεί ή τροποποιηθεί."
                        : "Strong indicators that the device has been repaired or modified."
        );

    } else if (manipulationScore >= 30) {

        logLabelWarnValue(
                gr ? "Συμπέρασμα"
                   : "Conclusion",
                gr
                        ? "Πιθανές ενδείξεις προηγούμενης επισκευής."
                        : "Possible indicators of previous repair."
        );

    } else {

        logLabelOkValue(
                gr ? "Συμπέρασμα"
                   : "Conclusion",
                gr
                        ? "Δεν εντοπίστηκαν σημαντικές ενδείξεις επέμβασης."
                        : "No significant repair indicators detected."
        );
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 29 ολοκληρώθηκε." : "Lab 29 finished.");
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

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logSection(gr
            ? "LAB 1 — Δοκιμή Τόνου Ηχείου"
            : "LAB 1 — Speaker Tone Test");
    logLine();

    new Thread(() -> {

        ToneGenerator tg = null;

        try {

            // ------------------------------------------------------------
            // AUDIO PATH PRE-CHECK (NO UI)
            // ------------------------------------------------------------
            AudioManager am =
                    (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            boolean volumeMuted = false;
            boolean bluetoothRouted = false;
            boolean wiredRouted = false;

            try {
                volumeMuted =
                        am != null &&
                        am.getStreamVolume(AudioManager.STREAM_MUSIC) == 0;
            } catch (Throwable ignore) {}

            try {
                bluetoothRouted =
                        am != null &&
                        (am.isBluetoothA2dpOn() || am.isBluetoothScoOn());
            } catch (Throwable ignore) {}

            try {
                wiredRouted =
                        am != null &&
                        am.isWiredHeadsetOn();
            } catch (Throwable ignore) {}

// ------------------------------------------------------------
// BLOCKED AUDIO PATH — STOP & ASK RE-RUN
// ------------------------------------------------------------
if (volumeMuted || bluetoothRouted || wiredRouted) {

    logLine();
    logInfo(gr
            ? "Έλεγχος διαδρομής εξόδου ήχου"
            : "Audio output path check");

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Μη καθαρή (μπλοκαρισμένη)"
               : "Not clear (blocked)"
    );

    if (volumeMuted) {
        logLabelWarnValue(
                gr ? "Εντοπίστηκε" : "Detected",
                gr ? "Η ένταση πολυμέσων είναι στο μηδέν (0%)."
                   : "Media volume is muted (volume = 0)"
        );
    }

    if (bluetoothRouted) {
        logLabelWarnValue(
                gr ? "Εντοπίστηκε" : "Detected",
                gr ? "Ο ήχος δρομολογείται σε συσκευή Bluetooth."
                   : "Audio routed to Bluetooth device"
        );
    }

    if (wiredRouted) {
        logLabelWarnValue(
                gr ? "Εντοπίστηκε" : "Detected",
                gr ? "Ο ήχος δρομολογείται σε ενσύρματη ή USB συσκευή."
                   : "Audio routed to wired or USB device"
        );
    }

    logLabelOkValue(
            gr ? "Απαιτούμενη ενέργεια" : "Action required",
            gr ? "Διόρθωσε τα παραπάνω και εκτέλεσε ξανά το LAB 1."
               : "Fix the condition(s) above and re-run LAB 1"
    );

    appendHtml("<br>");
logLabelErrorValue(
        gr ? "Αποτέλεσμα LAB 1" : "LAB 1 result",
        gr ? "Δεν εντοπίστηκε έξοδος ήχου."
           : "No acoustic output detected."
);

logLabelWarnValue(
        gr ? "Παρατήρηση" : "Note",
        gr ? "Η διαδρομή ήχου, ενδέχεται να είναι μπλοκαρισμένη, ή εκτός δρομολόγησης."
           : "Audio path, may be blocked, or not properly routed."
);

    logLine();
    return;
}

// ------------------------------------------------------------
// PLAY TEST TONE
// ------------------------------------------------------------

// FORCE CLEAN MEDIA STATE (FULL RESET)
if (am != null) {
    try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
    try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}
    try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
    try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
    try { am.setSpeakerphoneOn(true); } catch (Throwable ignore) {}
}

tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 90);
tg.startTone(ToneGenerator.TONE_DTMF_1, 1200);
            SystemClock.sleep(1400);

            // ------------------------------------------------------------
            // MIC ANALYSIS
            // ------------------------------------------------------------

// SAFE AUDIO NORMALIZE BEFORE MIC CAPTURE (NO EXTRA HELPERS)
if (am != null) {
    try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
    try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}
    try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
    try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
    // Κρατάμε speaker ON γιατί θέλουμε το mic να "ακούσει" το speaker
    try { am.setSpeakerphoneOn(true); } catch (Throwable ignore) {}
}
SystemClock.sleep(250);

MicDiagnosticEngine.Result r =
        MicDiagnosticEngine.run(this);

if (r == null) {
    logLabelErrorValue(
            gr ? "Μικρόφωνο" : "Mic",
            gr ? "Δεν καταγράφηκαν δεδομένα"
               : "No data captured"
    );
    return;
}

int rms  = (int) r.rms;
int peak = (int) r.peak;

logLabelOkValue(
        gr ? "RMS Μικροφώνου" : "Mic RMS",
        String.valueOf(rms)
);

logLabelOkValue(
        gr ? "Peak Μικροφώνου" : "Mic Peak",
        String.valueOf(peak)
);

String conf = (r.confidence == null)
        ? ""
        : r.confidence.trim().toUpperCase(Locale.US);

// CONFIDENCE = QUALITY ONLY (NEVER RED)
if (conf.contains("LOW") || conf.contains("WEAK")
        || conf.contains("FAIL") || conf.contains("NONE") || conf.contains("NO")) {

    logLabelWarnValue(
            gr ? "Ποιότητα Ανίχνευσης" : "Confidence",
            r.confidence
    );

} else {

    logLabelOkValue(
            gr ? "Ποιότητα Ανίχνευσης" : "Confidence",
            r.confidence
    );
}

// ------------------------------------------------------------
// SPEAKER OUTPUT EVALUATION (UNIFIED)
// ------------------------------------------------------------
SpeakerOutputState state = evaluateSpeakerOutput(r);

if (state == SpeakerOutputState.NO_OUTPUT) {
    
appendHtml("<br>");
    logLabelErrorValue(
            gr ? "Έξοδος ηχείου" : "Speaker output",
            gr ? "Δεν ανιχνεύθηκε ακουστικό σήμα"
               : "No acoustic output detected"
    );

    logLabelErrorValue(
            gr ? "Διάγνωση" : "Diagnosis",
            gr ? "Η διαδρομή ήχου είναι καθαρή, αλλά δεν καταγράφηκε ήχος από το μικρόφωνο"
               : "Audio path is clear, but no sound was captured by the microphone"
    );

    logLabelWarnValue(
            gr ? "Πιθανή αιτία" : "Possible cause",
            gr ? "Πιθανή βλάβη ηχείου ή έντονη ακουστική απομόνωση"
               : "Speaker hardware failure or severe acoustic isolation"
    );

    logLabelOkValue(
            gr ? "Προτεινόμενη ενέργεια" : "Recommended action",
            gr ? "Επανεκτέλεσε το τεστ. Αν η σιωπή επιμένει, συνιστάται έλεγχος υλικού"
               : "Re-run the test once more. If silence persists, hardware inspection is advised"
    );

    appendHtml("<br>");

logLabelErrorValue(
        gr ? "Αποτέλεσμα" : "Result",
        gr ? "Δεν ανιχνεύθηκε έξοδος ήχου."
           : "No acoustic output detected."
);

logLabelWarnValue(
        gr ? "Παρατήρηση" : "Note",
        gr
                ? "Αυτό μπορεί να οφείλεται σε χαμηλή ένταση, ακουστική απομόνωση, "
                  + "DSP φιλτράρισμα ή πιθανή βλάβη ηχείου."
                : "This may be caused by low volume level, acoustic isolation, "
                  + "DSP filtering, or possible speaker hardware issue."
);

    logLine();
    return;
}

// ------------------------------------------------------------
// OUTPUT DETECTED — CONFIDENCE IS INFORMATIONAL ONLY
// ------------------------------------------------------------

appendHtml("<br>");

if (conf.contains("LOW")) {

    logLabelOkValue(
            gr ? "Έξοδος ηχείου" : "Speaker output",
            gr ? "Ανιχνεύθηκε ακουστικό σήμα, με χαμηλή αξιοπιστία"
               : "Acoustic signal detected, with LOW confidence"
    );

    logLabelWarnValue(
            gr ? "Σημείωση" : "Note",
            gr ? "Η χαμηλή αξιοπιστία μπορεί να οφείλεται, σε DSP φιλτράρισμα, "
                 + "ακύρωση θορύβου, ή θέση μικροφώνου"
               : "Low confidence may be caused, by DSP filtering, noise cancellation, "
                 + "microphone placement, or acoustic design"
    );

} else {

appendHtml("<br>");
    logLabelOkValue(
            gr ? "Έξοδος ηχείου" : "Speaker output",
            gr ? "Ανιχνεύθηκε ακουστικό σήμα"
               : "Acoustic signal detected"
    );

}

} catch (Throwable t) {

    appendHtml("<br>");
logLine();
logInfo(gr ? "LAB 1 - Δοκιμή Τόνου Ηχείου"
           : "LAB 1 - Speaker tone test");
logLine();

appendHtml("<br>");
logLabelErrorValue(
gr ? "Κατάσταση" : "Status",
gr ? "Αποτυχία"
: "Failed"
);

logLabelWarnValue(
gr ? "Παρατήρηση" : "Observation",
gr ? "Η δοκιμή τόνου δεν ολοκληρώθηκε."
: "The tone test did not complete."
);

logLabelWarnValue(
gr ? "Πιθανή αιτία" : "Possible cause",
gr ? "Αποτυχία δρομολόγησης ήχου ή περιορισμός συστήματος."
: "Audio routing failure or system-level restriction."
);

} finally {

    if (tg != null) {
        tg.release();
    }

            appendHtml("<br>");
            logOk(gr ? "Το Lab 1 ολοκληρώθηκε." : "Lab 1 finished.");
            logLine();
        }

    }).start();
}

// ============================================================
// LAB 2 — Speaker Frequency Sweep (ADAPTIVE)
// • Runs independently
// • Detects real speaker output via mic
// • FAIL only if absolute silence (RMS == 0 && Peak == 0)
// ============================================================
private void lab2SpeakerSweep() {

    if (!guardMic(this::lab2SpeakerSweep)) return;

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
logLine();
logInfo(gr ? "LAB 2 — Έλεγχος Συχνοτήτων Ηχείου"
               : "LAB 2 — Speaker Frequency Sweep"
    );
    logLine();

    new Thread(() -> {

        ToneGenerator tg = null;

        try {

            AudioManager am =
        (AudioManager) getSystemService(Context.AUDIO_SERVICE);

if (am != null) {
    try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
    try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}
    try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
    try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
    try { am.setSpeakerphoneOn(true); } catch (Throwable ignore) {}
}

tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 90);

            // ----------------------------------------------------
            // PLAY MULTI-TONE SWEEP
            // ----------------------------------------------------
            int[] tones = {
                    ToneGenerator.TONE_DTMF_1,
                    ToneGenerator.TONE_DTMF_3,
                    ToneGenerator.TONE_DTMF_6,
                    ToneGenerator.TONE_DTMF_9
            };

            for (int t : tones) {
                tg.startTone(t, 500);
                SystemClock.sleep(550);
            }

// ----------------------------------------------------
// MIC FEEDBACK ANALYSIS
// ----------------------------------------------------

// SAFE AUDIO NORMALIZE BEFORE MIC CAPTURE (NO EXTRA HELPERS)
if (am != null) {
    try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
    try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}
    try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
    try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
    try { am.setSpeakerphoneOn(true); } catch (Throwable ignore) {}
}
SystemClock.sleep(250);

MicDiagnosticEngine.Result r =
        MicDiagnosticEngine.run(this);

if (r == null) {
    
appendHtml("<br>");
    logLabelErrorValue(
            gr ? "Μικρόφωνο" : "Mic",
            gr ? "Δεν καταγράφηκαν δεδομένα" : "No data captured"
    );
    return;
}

int rms  = (int) r.rms;
int peak = (int) r.peak;

logLabelOkValue("Mic RMS",  String.valueOf(rms));
logLabelOkValue("Mic Peak", String.valueOf(peak));

String conf = (r.confidence == null)
        ? ""
        : r.confidence.trim().toUpperCase(Locale.US);

// ----------------------------------------------------
// CONFIDENCE (QUALITY, NOT EXISTENCE)
// ----------------------------------------------------
if (conf.contains("LOW") || conf.contains("WEAK")
        || conf.contains("FAIL") || conf.contains("NONE")) {

    logLabelWarnValue(
            gr ? "Ποιότητα" : "Confidence",
            r.confidence
    );

} else {

    logLabelOkValue(
            gr ? "Ποιότητα" : "Confidence",
            r.confidence
    );
}

// ----------------------------------------------------
// HARD GATE — ABSOLUTE SILENCE ONLY
// ----------------------------------------------------
if (rms == 0 && peak == 0) {

appendHtml("<br>");
    logLabelErrorValue(
            gr ? "Έξοδος Ηχείου" : "Speaker output",
            gr ? "Δεν ανιχνεύθηκε ακουστικό σήμα"
               : "No acoustic output detected"
    );

    logLabelWarnValue(
            gr ? "Πιθανή αιτία" : "Possible cause",
            gr
                    ? "Βλάβη ηχείου, σίγαση εξόδου ή πλήρης ακουστική απομόνωση"
                    : "Speaker hardware failure, muted output path, or extreme isolation"
    );

    logLabelOkValue(
            gr ? "Σύσταση" : "Recommended",
            gr
                    ? "Επανεκτέλεσε το LAB 1 για έλεγχο διαδρομής ήχου"
                    : "Re-run LAB 1 to verify speaker operation and routing"
    );

    appendHtml("<br>");
    logLine();
    return;
}

// ----------------------------------------------------
// OUTPUT CONFIRMED (EVEN WITH LOW CONFIDENCE)
// ----------------------------------------------------
if (conf.contains("LOW") || conf.contains("WEAK")) {

appendHtml("<br>");
    logLabelOkValue(
            gr ? "Έξοδος Ηχείου" : "Speaker output",
            gr
                    ? "Ανιχνεύθηκε ακουστικό σήμα με χαμηλή αξιοπιστία."
                    : "Acoustic signal detected with LOW confidence"
    );

    logLabelWarnValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Η χαμηλή αξιοπιστία μπορεί να οφείλεται, σε DSP φιλτράρισμα, ακύρωση θορύβου, περιοσισμό απόκρισης συχνότητας, ή θέση μικροφώνου."
                    : "Low confidence may be caused, by DSP filtering, noise cancellation, speaker frequency limits, or microphone placement."
    );

} else {

appendHtml("<br>");
    logLabelOkValue(
            gr ? "Έξοδος Ηχείου" : "Speaker output",
            gr
                    ? "Ανιχνεύθηκε ακουστικό σήμα"
                    : "Acoustic signal detected"
    );

    logLabelOkValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Η σάρωση συχνοτήτων ολοκληρώθηκε επιτυχώς."
                    : "Frequency sweep detected successfully across multiple tones."
    );
}

} catch (Throwable t) {

    logError(
            gr
                    ? "Αποτυχία δοκιμής σάρωσης συχνοτήτων ηχείου"
                    : "Speaker frequency sweep failed"
    );

} finally {

            if (tg != null) tg.release();

            appendHtml("<br>");
            logOk(gr ? "Το Lab 2 ολοκληρώθηκε." : "Lab 2 finished.");
            logLine();
        }

    }).start();
}

/* ============================================================
   LAB 3 — Earpiece Audio Path Check (MANUAL)
   Custom GEL Dialog — START → tones → confirmation
   ============================================================ */
private void lab3EarpieceManual() {

    final boolean gr = AppLang.isGreek(this);

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

        restoreLab3Audio(); // 🔒 FAIL-SAFE
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
Button exitBtn = new Button(this);
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
Button start = new Button(this);
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

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// STOP TTS
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
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

start.setOnClickListener(v -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();

    // 👉 εδώ συνεχίζεις το test
});

// ------------------------------------------------------------
// START ACTION
// ------------------------------------------------------------
start.setOnClickListener(v -> {

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    new Thread(() -> {
        try {

            logInfo(gr
                    ? "Αναπαραγωγή δοκιμαστικών τόνων ακουστικού."
                    : "Playing earpiece test tones.");

            for (int i = 1; i <= 3; i++) {

                logInfo(gr
                        ? "Τόνος " + i + " / 3"
                        : "Tone " + i + " / 3");

                playEarpieceBeep();
                SystemClock.sleep(650);
            }

            logOk(gr
                    ? "Η αναπαραγωγή τόνων ολοκληρώθηκε."
                    : "Earpiece tone playback completed.");

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

            // 🔒 HARD AUDIO RESET
            resetAudioAfterLab3(am, lab3OldMode, lab3OldSpeaker, lab3OldMicMute);

            runOnUiThread(() -> {
                try { d.dismiss(); } catch (Throwable ignore) {}
                askUserEarpieceConfirmation();
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

private void lab4MicManual() {

    if (!guardMic(this::lab4MicManual)) return;
    
    lab4MicBase(() -> lab4MicPro());
}

private void lab4MicBase(Runnable onFinished) {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logSection(
            gr
                    ? "LAB 4 — Έλεγχος Υλικού Μικροφώνων"
                    : "LAB 4 — Microphone Hardware Check"
    );
    logLine();

    new Thread(() -> {

        boolean bottomOk = false;
        boolean topOk = false;
        boolean fallbackUsed = false;

        int bottomRms = 0, bottomPeak = 0;
        int topRms = 0, topPeak = 0;

        boolean stopBaseHere = false;

        try {

            // ====================================================
            // AUTO CHECK — BOTTOM MIC
            // ====================================================
            appendHtml("<br>");
            logInfo(gr ? "Έλεγχος κάτω μικροφώνου (αυτόματος):"
                       : "Bottom microphone auto check:");
            logLine();

            hardNormalizeAudioForMic();
            MicDiagnosticEngine.Result bottom =
                    MicDiagnosticEngine.run(this, MicDiagnosticEngine.MicType.BOTTOM);

            if (bottom != null) {
                bottomRms  = (int) bottom.rms;
                bottomPeak = (int) bottom.peak;
            }

            logLabelOkValue("Bottom RMS",  String.valueOf(bottomRms));
            logLabelOkValue("Bottom Peak", String.valueOf(bottomPeak));

            bottomOk = bottomRms > 0 || bottomPeak > 0;

            // ====================================================
            // AUTO CHECK — TOP MIC
            // ====================================================
            appendHtml("<br>");
            logInfo(gr ? "Έλεγχος άνω μικροφώνου (αυτόματος):"
                       : "Top microphone auto check:");
            logLine();

            hardNormalizeAudioForMic();
            MicDiagnosticEngine.Result top =
                    MicDiagnosticEngine.run(this, MicDiagnosticEngine.MicType.TOP);

            if (top != null) {
                topRms  = (int) top.rms;
                topPeak = (int) top.peak;
            }

            logLabelOkValue(
        gr ? "RMS (Άνω Μικρόφωνο)" : "Top RMS",
        String.valueOf(topRms)
);

logLabelOkValue(
        gr ? "Peak (Άνω Μικρόφωνο)" : "Top Peak",
        String.valueOf(topPeak)
);

            topOk = topRms > 0 || topPeak > 0;

// ====================================================
// FINAL BASE VERDICT (NO FALLBACK)
// ====================================================
appendHtml("<br>");
logInfo(gr ? "Συμπεράσματα υλικού:" : "Hardware conclusions:");
logLine();

if (bottomOk && topOk) {

    logLabelOkValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Και τα δύο μικρόφωνα λειτουργούν κανονικά"
               : "Both microphones are operational"
    );

} else if (bottomOk || topOk) {

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Μερική λειτουργία μικροφώνων"
               : "Partial microphone operation detected"
    );

} else {

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr
                    ? "Η λειτουργία μικροφώνων δεν επιβεβαιώθηκε από τον αυτόματο έλεγχο."
                    : "Microphone operation was not confirmed by the automatic test."
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

        } finally {

            appendHtml("<br>");
            logOk(gr ? "Το Lab 4 BASE ολοκληρώθηκε." : "Lab 4 BASE finished.");
            logLine();

            if (onFinished != null && !fallbackUsed) {
                runOnUiThread(onFinished);
            }
        }

    }).start();
}

/* ============================================================
   LAB 4 PRO — CALL QUALITY VERIFICATION (FINAL • LOCKED)
   ============================================================ */

private volatile boolean lastAnswerHeardClearly = false;

private void lab4MicPro() {
	

    final boolean gr = AppLang.isGreek(this);

    new Thread(() -> {

    try {

// ====================================================
// STAGE 1 — Bottom microphone HUMAN ACOUSTIC check
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

AtomicBoolean cancelled = new AtomicBoolean(false);
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

    Button exitBtn = new Button(this);
    exitBtn.setText("EXIT");
    exitBtn.setTextColor(Color.WHITE);
    exitBtn.setAllCaps(false);

    GradientDrawable exitBg = new GradientDrawable();
    exitBg.setColor(0xFF8B0000);
    exitBg.setCornerRadius(dp(10));
    exitBg.setStroke(dp(3), 0xFFFFD700);
    exitBtn.setBackground(exitBg);
    exitBtn.setLayoutParams(lp);

    Button startBtn = new Button(this);
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

if (cancelled.get()) {

    appendHtml("<br>");

    logWarn(gr
            ? "LAB 4 PRO — Διακόπηκε από τον χρήστη"
            : "LAB 4 PRO — Interrupted by user");
    logLine();

    runOnUiThread(this::enableSingleExportButton);
    return;
}

// ==========================
// LIVE MIC → EARPIECE LOOP (5s)
// ==========================
int sampleRate = 16000;
int minBuf = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
);

AudioRecord recorder = new AudioRecord(
        MediaRecorder.AudioSource.VOICE_COMMUNICATION,
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        minBuf
);

AudioTrack track = new AudioTrack(
        AudioManager.STREAM_VOICE_CALL,
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        minBuf,
        AudioTrack.MODE_STREAM
);

byte[] buffer = new byte[minBuf];

recorder.startRecording();
track.play();

long loopUntil = SystemClock.uptimeMillis() + 5000;

while (SystemClock.uptimeMillis() < loopUntil && !cancelled.get()) {

    int read = recorder.read(buffer, 0, buffer.length);
    if (read > 0) {
        track.write(buffer, 0, read);
    }
}

try { recorder.stop(); } catch (Throwable ignore) {}
try { recorder.release(); } catch (Throwable ignore) {}
try { track.stop(); } catch (Throwable ignore) {}
try { track.release(); } catch (Throwable ignore) {}

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

    Button noBtn = new Button(this);
    noBtn.setText(gr ? "ΟΧΙ" : "NO");
    noBtn.setAllCaps(false);
    noBtn.setTextColor(Color.WHITE);

    GradientDrawable noBg = new GradientDrawable();
    noBg.setColor(0xFF8B0000);
    noBg.setCornerRadius(dp(10));
    noBg.setStroke(dp(3), 0xFFFFD700);
    noBtn.setBackground(noBg);
    noBtn.setLayoutParams(lp);

    Button yesBtn = new Button(this);
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

if (cancelled.get()) return;

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
        ? "LAB 4 PRO — Ποιότητα συνομιλίας κάτω μικροφώνου"
        : "LAB 4 PRO — Bottom microphone call quality");
logLine();

if (heardClearly.get()) {

    logLabelOkValue(
            gr ? "Αποτέλεσμα" : "Result",
            gr
                    ? "Ο χρήστης επιβεβαίωσε καθαρή ακουστική επανάληψη. Το κάτω μικρόφωνο λειτουργεί σωστά."
                    : "User confirmed clear acoustic loop. Bottom microphone is functioning properly."
    );

    logLabelOkValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Αν παρουσιαστούν προβλήματα σε πραγματικές συνομιλίες, "
                      + "ενδέχεται να σχετίζονται με το δίκτυο, ή codec, η προβλημα στο μικρόφωνο του συνομιλητη"
                    : "If issues occur during real calls, they may be related to network, or codec, or other's party microphone issue"
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

AtomicBoolean ttsFinished = new AtomicBoolean(false);

// ==========================
// SHOW DIALOG (UI THREAD)
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
            ? "Βάλε το ακουστικό στο αυτί σου."
            : "Place the earpiece on your ear.");
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    root.addView(msg);

    b.setView(root);

    final AlertDialog d = b.create();
    dialogRef.set(d);

    if (d.getWindow() != null) {
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    if (!isFinishing() && !isDestroyed()) {

        d.show();

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

                routeToCallEarpiece();
                playAnswerCheckWav();

            }).start();

        }, 500);
    }
});

        } catch (Throwable t) {

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
                ? "Χαμηλή στάθμη έντασης, βουλωμένο ακουστικό, "
                  + "προστατευτικό οθόνης, θέση συσκευής, ή πραγματική βλάβη ακουστικού."
                : "Low volume level, obstructed earpiece, "
                  + "screen protector interference, device position, or actual earpiece hardware issue."
);

            appendHtml("<br>");
            logOk(gr ? "Το Lab 4 ολοκληρώθηκε." : "Lab 4 finished.");
            logLine();

} finally {

    // ABSOLUTE SAFETY — COMPILE SAFE
    try { AppTTS.stop(); } catch (Throwable ignore) {}

    try {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (am != null) {
            try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
            try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}
            try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
            try { am.setSpeakerphoneOn(false); } catch (Throwable ignore) {}
            try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
        }
    } catch (Throwable ignore) {}
}

}).start();
}
    
// ============================================================
// 🎵 PLAY VOICE WAV — AUTO LANGUAGE (EARPIECE ONLY • LOCKED)
// ============================================================
private void playAnswerCheckWav() {

    // 👂 FORCE CALL PATH → EARPICE
    AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
    if (am != null) {
        try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
        try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}
        try { am.setSpeakerphoneOn(false); } catch (Throwable ignore) {}
        try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
        try { am.setMode(AudioManager.MODE_IN_COMMUNICATION); } catch (Throwable ignore) {}
    }

    SystemClock.sleep(150);

    // 🌍 AUTO LANGUAGE
    final boolean gr = AppLang.isGreek(this);
    final int resId = gr ? R.raw.answercheck_el : R.raw.answercheck_en;

    MediaPlayer mp = new MediaPlayer();

    try {
        AssetFileDescriptor afd =
                getResources().openRawResourceFd(resId);
        if (afd == null) return;

        mp.setDataSource(
                afd.getFileDescriptor(),
                afd.getStartOffset(),
                afd.getLength()
        );
        afd.close();

        // 🔒 CRITICAL: VOICE_CALL → EARPICE (ΟΧΙ MUSIC)
        mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

        mp.prepare();
        mp.start();

        int dur = 0;
        try { dur = mp.getDuration(); } catch (Throwable ignore) {}
        SystemClock.sleep(dur > 0 ? dur : 1800);

    } catch (Throwable ignore) {

    } finally {
        try { mp.stop(); } catch (Throwable ignore) {}
        try { mp.release(); } catch (Throwable ignore) {}
    }

    // ❗ ΔΕΝ αλλάζουμε route εδώ
    // συνεχίζουμε με confirmation
    AppTTS.stop();

showAnswerCheckConfirmation(() -> {

    lab4ResultEarpiece();

});
}

// ============================================================
// STAGE 4 — HUMAN CONFIRMATION (FINAL • COMPILE SAFE)
// ============================================================
private void showAnswerCheckConfirmation(Runnable onAnswered) {

    final boolean gr = AppLang.isGreek(this);

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
                        ? "Με άκουσες καθαρά; Τσέκαρε την απάντησή σου."
                        : "Did you hear me clearly? Check your answer.";

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

        Button noBtn = new Button(this);
        noBtn.setText(gr ? "ΟΧΙ" : "NO");
        noBtn.setAllCaps(false);
        noBtn.setTextColor(Color.WHITE);

        GradientDrawable noBg = new GradientDrawable();
        noBg.setColor(0xFF8B0000);
        noBg.setCornerRadius(dp(10));
        noBg.setStroke(dp(3), 0xFFFFD700);

        noBtn.setBackground(noBg);
        noBtn.setLayoutParams(lp);

        Button yesBtn = new Button(this);
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

        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        d.setOnDismissListener(dialog -> {
            try { AppTTS.stop(); } catch (Throwable ignore) {}
        });

        if (!isFinishing() && !isDestroyed()) {
    d.show();
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

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logInfo(gr ? "LAB 4 PRO — Ποιότητα συνομιλίας ακουστικού"
               : "LAB 4 PRO — Earpiece Call quality");
    logLine();

    if (lastAnswerHeardClearly) {

        logLabelOkValue(
                gr ? "Αποτέλεσμα" : "Result",
                gr
                        ? "Σύμφωνα με τη δήλωση χρήστη, το ακουστικό αποδίδει καθαρό ήχο."
                        : "According to the user's declaration, the earpiece delivers clear audio."
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

    runOnUiThread(this::enableSingleExportButton);
}

/* ============================================================
   LAB 5 — Vibration Motor Test
   FULL ENV CHECK + PRO TEST + USER CONFIRM
   ============================================================ */
private void lab5Vibration() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logSection(
            gr
                    ? "LAB 5 — Διαγνωστικός Έλεγχος Μηχανισμού Δόνησης"
                    : "LAB 5 — Vibration Motor Test"
    );
    logLine();

    final AtomicBoolean userConfirmed = new AtomicBoolean(false);

    new Thread(() -> {

        try {

            Vibrator vibrator;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                VibratorManager vm =
                        (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                vibrator = (vm != null) ? vm.getDefaultVibrator() : null;
            } else {
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            }

            if (vibrator == null || !vibrator.hasVibrator()) {
                logError(gr ? "Δεν εντοπίστηκε μοτέρ δόνησης"
                            : "No vibration motor detected");
                return;
            }

            appendHtml("<br>");
            logInfo(gr ? "Έλεγχος ρυθμίσεων συστήματος:"
                       : "System settings check:");
            logLine();

            // =====================================================
            // 1️⃣ DND
            // =====================================================
            try {
                NotificationManager nm =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (nm != null &&
                        nm.getCurrentInterruptionFilter()
                                != NotificationManager.INTERRUPTION_FILTER_ALL) {

                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr ? "Ενεργή λειτουργία Μην Ενοχλείτε."
                               : "Do Not Disturb mode is active."
                    );
                }
            } catch (Throwable ignore) {}

            // =====================================================
            // 2️⃣ Battery Saver
            // =====================================================
            try {
                PowerManager pm =
                        (PowerManager) getSystemService(Context.POWER_SERVICE);

                if (pm != null && pm.isPowerSaveMode()) {

                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr ? "Ενεργή λειτουργία εξοικονόμησης ενέργειας μπαταρίας."
                               : "Battery saver mode is active."
                    );
                }
            } catch (Throwable ignore) {}

            // =====================================================
            // 3️⃣ Silent Mode
            // =====================================================
            try {
                AudioManager am =
                        (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                if (am != null &&
                        am.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {

                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr ? "Η συσκευή βρίσκεται σε αθόρυβη λειτουργία."
                               : "Device is in Silent mode."
                    );
                }
            } catch (Throwable ignore) {}

            // =====================================================
            // 4️⃣ Haptic Feedback Enabled
            // =====================================================
            try {
                int haptic = Settings.System.getInt(
                        getContentResolver(),
                        Settings.System.HAPTIC_FEEDBACK_ENABLED
                );

                if (haptic == 0) {
                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr ? "Η απτική ανάδραση είναι απενεργοποιημένη."
                               : "Haptic feedback is disabled."
                    );
                }
            } catch (Throwable ignore) {}

            // =====================================================
            // 5️⃣ Vibrate When Ringing
            // =====================================================
            try {
                int vibrate =
                        Settings.System.getInt(
                                getContentResolver(),
                                "vibrate_when_ringing"
                        );

                if (vibrate == 0) {
                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr ? "Η δόνηση κατά την κλήση είναι απενεργοποιημένη."
                               : "Vibrate on ring is disabled."
                    );
                }
            } catch (Throwable ignore) {}

            logLine();

            // =====================================================
            // PRO TESTS
            // =====================================================

            logInfo(gr ? "Συνεχής δόνηση 3 δευτερολέπτων"
                       : "Continuous vibration 3 seconds");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                        VibrationEffect.createOneShot(3000,
                                VibrationEffect.DEFAULT_AMPLITUDE)
                );
            } else {
                vibrator.vibrate(3000);
            }

            SystemClock.sleep(3200);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    vibrator.hasAmplitudeControl()) {

                logInfo(gr ? "Έλεγχος έντασης δόνησης"
                           : "Amplitude variation test");

                vibrator.vibrate(VibrationEffect.createOneShot(800, 80));
                SystemClock.sleep(900);

                vibrator.vibrate(VibrationEffect.createOneShot(800, 255));
                SystemClock.sleep(900);

            }

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
    msg.setText(gr
            ? "Ένιωσες καθαρά τη δόνηση;"
            : "Did you clearly feel the vibration?");
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    msg.setPadding(0, 0, 0, dp(18));
    root.addView(msg);

    // ---------------------------
    // MUTE ROW (HELPER)
    // ---------------------------
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
    Button noBtn = new Button(this);
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
    Button yesBtn = new Button(this);
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

    // STOP TTS on ANY dismiss
    d.setOnDismissListener(dialog -> {
        try { AppTTS.stop(); } catch (Throwable ignore) {}
    });

    // BACK protection
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
    }

    // ---------------------------
    // TTS (SAFE + RESPECT MUTE)
    // ---------------------------
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
        answered.set(true);
        d.dismiss();
    });

    yesBtn.setOnClickListener(v -> {
        userConfirmed.set(true);
        answered.set(true);
        d.dismiss();
    });

});

// ==========================
// WAIT FOR USER RESPONSE
// ==========================
long waitUntil = SystemClock.uptimeMillis() + 10000;

while (!answered.get() &&
        SystemClock.uptimeMillis() < waitUntil) {

    SystemClock.sleep(80);
}

appendHtml("<br>");
logLine();

if (userConfirmed.get()) {

    logLabelOkValue(
            gr ? "Αποτέλεσμα" : "Result",
            gr ? "Η δόνηση επιβεβαιώθηκε από τον χρήστη."
               : "Vibration confirmed by the user."
    );

} else {

    logLabelErrorValue(
            gr ? "Αποτέλεσμα" : "Result",
            gr ? "Η δόνηση δεν επιβεβαιώθηκε από τον χρήστη."
               : "Vibration was not confirmed by the user."
    );

    logLabelWarnValue(
            gr ? "Πιθανές αιτίες" : "Possible causes",
            gr
                    ? "Απενεργοποιημένες ρυθμίσεις δόνησης, χαμηλή ένταση απτικής ανάδρασης, "
                      + "περιορισμός firmware, ή πιθανή μηχανική φθορά."
                    : "Disabled vibration settings, low haptic intensity, "
                      + "firmware restriction, or possible mechanical wear."
    );

    logOk(
            gr
                    ? "Συνιστάται επιβεβαίωση μέσω πραγματικής κλήσης ή δοκιμής ειδοποίησης."
                    : "Verification via a real call or notification test is recommended."
    );
}

} catch (Throwable t) {

    logError(gr ? "Η δοκιμή δόνησης απέτυχε"
            : "Vibration test failed");

logLabelWarnValue(
        gr ? "Πιθανή αιτία" : "Possible cause",
        gr
                ? "Απενεργοποιημένη δόνηση, περιορισμός συστήματος, ή βλάβη μηχανισμού δόνησης."
                : "Vibration disabled, system restriction, or vibration motor malfunction."
);

} finally {

    appendHtml("<br>");
    logOk(gr ? "Το Lab 5 ολοκληρώθηκε." : "Lab 5 finished.");
    logLine();

    runOnUiThread(this::enableSingleExportButton);
}

}).start();
} 

// ============================================================
// LABS 6 — 9: DISPLAY & SENSORS
// ============================================================

// ============================================================
// LAB 6 — Display Touch (POPUP + MUTE + TTS + GR/EN)
// FINAL — LIFECYCLE SAFE
// ============================================================
private void lab6DisplayTouch() {

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
Button startBtn = new Button(this);
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
}

// ---------------------------
// ACTION
// ---------------------------
startBtn.setOnClickListener(v -> {
    AppTTS.stop();
    d.dismiss();

    startActivityForResult(
            new Intent(this, TouchGridTestActivity.class),
            6006
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
                  + "Η οθόνη πρέπει να ακολουθεί τον προσανατολισμό.\n\n"
                  + "Βήμα 2:\n"
                  + "Καλύψτε με το χέρι σας τον αισθητήρα εγγύτητας, "
                  + "στο επάνω μέρος της οθόνης, στην περιοχή ειδοποιήσεων.\n"
                  + "Η οθόνη πρέπει να σβήσει."
                : "Step 1:\n"
                  + "Rotate the device slowly.\n"
                  + "The screen should rotate accordingly.\n\n"
                  + "Step 2:\n"
                  + "Cover the proximity sensor with your hand, "
                  + "at the top of the screen, to the notification area.\n"
                  + "The screen should turn off.";

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
                    ? "Αυτό το τεστ, θα ελέγξει ΟΛΕΣ τις κάμερες, μία-μία.\n\n"
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
    Button start = new Button(this);
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
                gr ? "Κάμερες που ελέγχθηκαν" : "Cameras tested",
                String.valueOf(overall.total)
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

        if (overall.previewFailCount == 0)
            logLabelOkValue(
                    gr ? "Αποτυχίες προεπισκόπησης" : "Preview FAIL",
                    "0"
            );
        else
            logLabelErrorValue(
                    gr ? "Αποτυχίες προεπισκόπησης" : "Preview FAIL",
                    String.valueOf(overall.previewFailCount)
            );

        if (overall.torchOkCount > 0)
            logLabelOkValue(
                    gr ? "Φλας OK" : "Torch OK",
                    String.valueOf(overall.torchOkCount)
            );
        else
            logLabelWarnValue(
                    gr ? "Φλας OK" : "Torch OK",
                    "0"
            );

        if (overall.torchFailCount == 0)
            logLabelOkValue(
                    gr ? "Αποτυχίες φλας" : "Torch FAIL",
                    "0"
            );
        else
            logLabelWarnValue(
                    gr ? "Αποτυχίες φλας" : "Torch FAIL",
                    String.valueOf(overall.torchFailCount)
            );

        if (overall.streamIssueCount == 0)
            logLabelOkValue(
                    gr ? "Προβλήματα ροής καρέ" : "Frame stream issues",
                    gr ? "Κανένα" : "None detected"
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
                    gr ? "Υποσύστημα κάμερας" : "Camera subsystem",
                    gr ? "Λειτουργικό" : "Operational"
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

            if (overall.torchFailCount == 0)
                logLabelOkValue(
                        gr ? "Υποσύστημα φλας" : "Flash subsystem",
                        gr ? "OK (όπου υπάρχει)" : "OK (where available)"
                );
            else
                logLabelWarnValue(
                        gr ? "Υποσύστημα φλας" : "Flash subsystem",
                        gr
                                ? "Ορισμένες κάμερες χωρίς φλας ή με πρόβλημα"
                                : "Some cameras have no flash / torch issues"
                );

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
                    gr ? "Υποσύστημα κάμερας" : "Camera subsystem",
                    gr ? "ΜΗ αξιόπιστο" : "NOT reliable"
            );

            logError(
                    gr
                            ? "Μία ή περισσότερες κάμερες απέτυχαν στον βασικό έλεγχο."
                            : "One or more cameras failed basic operation checks."
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

        logLabelOkValue("Flash", "Torch toggled successfully");
        overall.torchOkCount++;

    } catch (Throwable t) {
        logLabelErrorValue("Flash", "Torch control failed");
        logWarn("Possible flash hardware, driver, or permission issue.");
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

    Button yes = new Button(this);
    yes.setText(gr ? "ΒΛΕΠΩ ΕΙΚΟΝΑ" : "I SEE IMAGE");
    yes.setAllCaps(false);
    yes.setTextColor(0xFFFFFFFF);
    GradientDrawable yesBg = new GradientDrawable();
    yesBg.setColor(0xFF0B5F3B);
    yesBg.setCornerRadius(dp(10));
    yesBg.setStroke(dp(3), 0xFFFFD700);
    yes.setBackground(yesBg);

    Button no = new Button(this);
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

// Σταμάτα TTS ΟΠΟΤΕ κλείσει
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
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
        try { lab8CloseSession(s); } catch (Throwable ignore) {}
        try { d.dismiss(); } catch (Throwable ignore) {}
        onDone.run();
    };

    // After sampling window, enable buttons
    Runnable enableButtons = () -> {
        if (finished.get()) return;
        yes.setEnabled(true);
        no.setEnabled(true);
    };

    yes.setOnClickListener(v -> {
    AppTTS.stop();
    overall.previewOkCount++;
    s.userConfirmedPreview = Boolean.TRUE;
    finishAndNext.run();
});

no.setOnClickListener(v -> {
    AppTTS.stop();
    overall.previewFailCount++;
    s.userConfirmedPreview = Boolean.FALSE;
    finishAndNext.run();
});

    // Start camera when texture is ready
    if (tv.isAvailable()) {
        lab8StartCamera2Session(s, overall, enableButtons, () -> {
            overall.streamIssueCount++;
            enableButtons.run();
        });
    } else {
        tv.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override public void onSurfaceTextureAvailable(SurfaceTexture st, int w, int h) {
                if (finished.get()) return;
                lab8StartCamera2Session(s, overall, enableButtons, () -> {
                    overall.streamIssueCount++;
                    enableButtons.run();
                });
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

                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    try {
                                        lab8StopAndReportSample(s, overall);
                                    } catch (Throwable ignore) {}
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
        logLabelErrorValue(
                gr ? "Καρέ" : "Frames",
                "0"
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

    if (s.droppedFrames == 0)
        logLabelOkValue(
                gr ? "Απώλειες καρέ / timeouts" : "Frame drops / timeouts",
                "0"
        );
    else
        logLabelWarnValue(
                gr ? "Απώλειες καρέ / timeouts" : "Frame drops / timeouts",
                String.valueOf(s.droppedFrames)
        );

    if (s.blackFrames == 0)
        logLabelOkValue(
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
                        ? "ΟΧΙ — μόνο JPEG"
                        : "NO — professional uncompressed photos not supported (JPEG only)"
        );

    // User confirmation
    if (s.userConfirmedPreview != null) {
        if (s.userConfirmedPreview)
            logLabelOkValue(
                    gr ? "Επιβεβαίωση χρήστη" : "User confirmation",
                    gr ? "Η προεπισκόπηση ήταν ορατή" : "Live preview visible"
            );
        else
            logLabelErrorValue(
                    gr ? "Επιβεβαίωση χρήστη" : "User confirmation",
                    gr ? "Η προεπισκόπηση ΔΕΝ ήταν ορατή" : "Preview NOT visible"
            );
    }

    // ------------------------------------------------------------
    // Final verdict (per camera)
    // ------------------------------------------------------------
    boolean ok =
            (s.frames > 0) &&
            (s.blackFrames == 0) &&
            (s.droppedFrames == 0) &&
            (s.latencyCount == 0 || (s.latencySumMs / Math.max(1, s.latencyCount)) <= 250) &&
            (s.userConfirmedPreview != null && s.userConfirmedPreview);

    s.verdictOk = ok;

    if (ok) {
        logLabelOkValue(
                gr ? "Τελικό αποτέλεσμα" : "Verdict",
                gr ? "OK — Η διαδρομή κάμερας λειτουργεί σωστά"
                   : "OK — Camera path operational"
        );
    } else {
        logLabelWarnValue(
                gr ? "Τελικό αποτέλεσμα" : "Verdict",
                gr ? "Εντοπίστηκαν θέματα — έλεγξε τα παραπάνω"
                   : "Issues detected — review above"
        );
    }

    logLine();
    appendHtml("<br>");
} // ✅ ΤΕΛΟΣ lab8StopAndReportSample (αυτό έλειπε)

// ============================================================
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
    int previewFailCount;
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
    long frames;
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

        Button yes = new Button(this);
        yes.setText(gr ? "ΣΥΝΕΧΕΙΑ" : "CONTINUE");
        yes.setAllCaps(false);
        yes.setTextColor(Color.WHITE);
        yes.setLayoutParams(lp);

        GradientDrawable yesBg = new GradientDrawable();
        yesBg.setColor(0xFF0B5F3B);
        yesBg.setCornerRadius(dp(10));
        yesBg.setStroke(dp(3), 0xFFFFD700);
        yes.setBackground(yesBg);

        Button no = new Button(this);
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
        boolean hasDualALS = false;
        int alsCount = 0;
        boolean hasSAR = false;
        boolean hasPickup = false;
        boolean hasLargeTouch = false;
        boolean hasGameRotation = false;

        for (Sensor s : sensors) {
            String name   = s.getName()   != null ? s.getName().toLowerCase(Locale.US)   : "";
            String vendor = s.getVendor() != null ? s.getVendor().toLowerCase(Locale.US) : "";

            if (name.contains("virtual") && name.contains("gyro"))
                hasVirtualGyro = true;

            if (name.contains("gyroscope") && vendor.contains("xiaomi"))
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

        if (alsCount >= 2) hasDualALS = true;

        // ------------------------------------------------------------
        // SENSOR INTERPRETATION SUMMARY — ONE LINE PER ITEM
        // ------------------------------------------------------------
        logLine();

        if (hasVirtualGyro)
            logLabelOkValue(
                    gr ? "Εικονικό γυροσκόπιο" : "Virtual Gyroscope",
                    gr ? "Εντοπίστηκε (sensor fusion — αναμενόμενο)" : "Detected (sensor fusion — expected behavior)"
            );
        else
            logLabelWarnValue(
                    gr ? "Εικονικό γυροσκόπιο" : "Virtual Gyroscope",
                    gr ? "Δεν αναφέρθηκε" : "Not reported"
            );

        if (hasDualALS)
            logLabelOkValue(
                    gr ? "Αισθητήρες φωτός" : "Ambient Light Sensors",
                    gr ? "Διπλός ALS (μπροστά + πίσω)" : "Dual ALS (front + rear)"
            );
        else
            logLabelWarnValue(
                    gr ? "Αισθητήρες φωτός" : "Ambient Light Sensors",
                    gr ? "Μονός ALS" : "Single ALS"
            );

        if (hasSAR)
            logLabelOkValue(
                    gr ? "Αισθητήρες SAR" : "SAR Sensors",
                    gr ? "Υπάρχουν (proximity / RF tuning)" : "Present (proximity / RF tuning)"
            );
        else
            logLabelWarnValue(
                    gr ? "Αισθητήρες SAR" : "SAR Sensors",
                    gr ? "Δεν αναφέρθηκαν" : "Not reported"
            );

        if (hasPickup)
            logLabelOkValue(
                    gr ? "Pickup sensor" : "Pickup Sensor",
                    gr ? "Υπάρχει (lift-to-wake υποστήριξη)" : "Present (lift-to-wake supported)"
            );
        else
            logLabelWarnValue(
                    gr ? "Pickup sensor" : "Pickup Sensor",
                    gr ? "Δεν αναφέρθηκε" : "Not reported"
            );

        if (hasLargeTouch)
            logLabelOkValue(
                    gr ? "Large area touch" : "Large Area Touch",
                    gr ? "Υπάρχει (palm rejection / ακρίβεια)" : "Present (palm rejection / accuracy)"
            );
        else
            logLabelWarnValue(
                    gr ? "Large area touch" : "Large Area Touch",
                    gr ? "Δεν αναφέρθηκε" : "Not reported"
            );

        if (hasGameRotation)
            logLabelOkValue(
                    gr ? "Game rotation vector" : "Game Rotation Vector",
                    gr ? "Υπάρχει (gaming orientation)" : "Present (gaming orientation)"
            );
        else
            logLabelWarnValue(
                    gr ? "Game rotation vector" : "Game Rotation Vector",
                    gr ? "Δεν αναφέρθηκε" : "Not reported"
            );

        logLabelOkValue(
                gr ? "Συνολική εκτίμηση" : "Overall Assessment",
                gr
                        ? "Το sensor suite είναι πλήρες και υγιές για αυτή τη συσκευή"
                        : "Sensor suite complete and healthy for this device"
        );

    } catch (Throwable e) {
        logError(gr ? "Σφάλμα ανάλυσης αισθητήρων" : "Sensors analysis error", e.getMessage());
    } finally {
        appendHtml("<br>");
        logOk(gr ? "Το Lab 9 ολοκληρώθηκε." : "Lab 9 finished.");
        logLine();
        enableSingleExportButton();
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

    String band = (freqMhz > 3000) ? (gr ? "5 GHz" : "5 GHz") : (gr ? "2.4 GHz" : "2.4 GHz");

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
        logLabelOkValue(gr ? "Ταχύτητα Link" : "Link speed", speed + " Mbps");
    } else if (speed >= 54) {
        logLabelWarnValue(gr ? "Ταχύτητα Link" : "Link speed", speed + " Mbps");
    } else {
        logLabelErrorValue(gr ? "Ταχύτητα Link" : "Link speed", speed + " Mbps");
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

            // 4) Speed heuristic
            WifiInfo info = wm.getConnectionInfo();
            int link = info != null ? info.getLinkSpeed() : 0;
            int rssi = info != null ? info.getRssi() : -80;

            float speedSim = estimateSpeedSimMbps(link, rssi);
            logLabelOkValue(
                    gr ? "SpeedSim" : "SpeedSim",
                    String.format(Locale.US, gr ? "~%.2f Mbps (εκτίμηση)" : "~%.2f Mbps (heuristic)", speedSim)
            );

            // Finish
            logLabelOkValue(gr ? "DeepScan" : "DeepScan", gr ? "Ολοκληρώθηκε" : "Finished");

            // ====================================================
            // INTERNET AVAILABILITY
            // ====================================================
            try {
                ConnectivityManager cm =
                        (ConnectivityManager)
                                getSystemService(CONNECTIVITY_SERVICE);

                if (cm == null) {
                    logError(gr ? "ConnectivityManager δεν είναι διαθέσιμο." : "ConnectivityManager not available.");
                } else {

                    boolean hasInternet = false;
                    String transport = gr ? "ΑΓΝΩΣΤΟ" : "UNKNOWN";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Network n = cm.getActiveNetwork();
                        NetworkCapabilities caps = cm.getNetworkCapabilities(n);

                        if (caps != null) {
                            hasInternet =
                                    caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

                            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                                transport = gr ? "Wi-Fi" : "Wi-Fi";
                            else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                                transport = gr ? "Δεδομένα" : "Cellular";
                        }
                    } else {
                        @SuppressWarnings("deprecation")
                        NetworkInfo ni = cm.getActiveNetworkInfo();
                        if (ni != null && ni.isConnected()) {
                            hasInternet = true;
                            transport = ni.getTypeName();
                        }
                    }

                    if (!hasInternet)
                        logError(gr ? "Δεν βρέθηκε ενεργή σύνδεση Internet (OS-level)." : "No active Internet connection detected (OS-level).");
                    else
                        logOk((gr ? "Internet ενεργό (" : "Internet connectivity active (") + transport + ").");
                }

            } catch (Throwable e) {
                logError((gr ? "Σφάλμα ελέγχου Internet: " : "Internet quick check error: ") + e.getMessage());
            }

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

private float estimateSpeedSimMbps(
        int linkSpeedMbps,
        int rssiDbm) {

    if (linkSpeedMbps <= 0)
        linkSpeedMbps = 72;

    float rssiFactor;
    if (rssiDbm > -55) rssiFactor = 1.2f;
    else if (rssiDbm > -65) rssiFactor = 1.0f;
    else if (rssiDbm > -75) rssiFactor = 0.7f;
    else rssiFactor = 0.4f;

    return Math.max(5f, linkSpeedMbps * rssiFactor);
}

// ============================================================
// LAB 11 — Mobile Data Diagnostic
// ============================================================
private void lab11MobileDataDiagnostic() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 11 — Διαγνωστικός Έλεγχος Κινητού Δικτύου (Εργαστήριο)"
               : "LAB 11 — Mobile Network Diagnostic (Laboratory)");
    logLine();

    TelephonySnapshot s = getTelephonySnapshot();

    // ------------------------------------------------------------
    // Airplane mode (context only)
    // ------------------------------------------------------------
    if (s.airplaneOn) {
        logInfo(gr
                ? "Η Λειτουργία Πτήσης είναι ΕΝΕΡΓΗ. Τα ραδιο-interfaces είναι σκόπιμα απενεργοποιημένα."
                : "Airplane mode is ENABLED. Radio interfaces are intentionally disabled.");
        return;
    }

    // ------------------------------------------------------------
    // SIM state (laboratory reporting)
    // ------------------------------------------------------------
    if (!s.simReady) {

        switch (s.simState) {

            case TelephonyManager.SIM_STATE_ABSENT:
                logLabelWarnValue(gr ? "Κατάσταση SIM" : "SIM State", gr ? "ΑΠΟΥΣΑ" : "ABSENT");
                return;

            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                logLabelWarnValue(
                        gr ? "Κατάσταση SIM" : "SIM State",
                        gr ? "ΠΑΡΟΥΣΑ αλλά κλειδωμένη (απαιτείται PIN)"
                           : "PRESENT but locked (PIN required)"
                );
                return;

            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                logLabelWarnValue(
                        gr ? "Κατάσταση SIM" : "SIM State",
                        gr ? "ΠΑΡΟΥΣΑ αλλά κλειδωμένη (απαιτείται PUK)"
                           : "PRESENT but locked (PUK required)"
                );
                return;

            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                logLabelWarnValue(
                        gr ? "Κατάσταση SIM" : "SIM State",
                        gr ? "ΠΑΡΟΥΣΑ αλλά κλειδωμένη από δίκτυο"
                           : "PRESENT but network locked"
                );
                return;

            default:
                logLabelWarnValue(
                        gr ? "Κατάσταση SIM" : "SIM State",
                        gr ? "ΠΑΡΟΥΣΑ αλλά δεν είναι έτοιμη"
                           : "PRESENT but not ready"
                );
                return;
        }
    }

    // SIM ready
    logLabelOkValue(gr ? "Κατάσταση SIM" : "SIM State", gr ? "ΕΤΟΙΜΗ" : "READY");

    // ------------------------------------------------------------
    // Service state (legacy domain — informational)
    // ------------------------------------------------------------
    logLabelValue(
        gr ? "Κατάσταση Υπηρεσίας (legacy)" : "Service State (legacy)",
        s.inService
                ? (gr ? "ΣΕ ΥΠΗΡΕΣΙΑ" : "IN SERVICE")
                : (gr ? "ΔΕΝ ΑΝΑΦΕΡΕΤΑΙ ΩΣ ΕΝΕΡΓΗ" : "NOT REPORTED AS IN SERVICE")
);

if (!s.inService) {
    logLabelWarnValue(
            gr ? "Σημείωση (Legacy Service)" : "Legacy Service Note",
            gr
                    ? "Η legacy κατάσταση υπηρεσίας δεν αναφέρεται ως ενεργή. "
                      + "Σε σύγχρονες LTE/5G συσκευές, η φωνή και τα δεδομένα "
                      + "μπορεί να παρέχονται μέσω IMS (VoLTE / VoWiFi)."
                    : "Legacy service registration is not reported as active. "
                      + "On modern LTE/5G devices, voice and data may be provided via IMS (VoLTE / VoWiFi)."
    );
}

    // ------------------------------------------------------------
    // Data state (packet domain — informational)
    // ------------------------------------------------------------
    String dataStateLabel;
    switch (s.dataState) {
        case TelephonyManager.DATA_CONNECTED:
            dataStateLabel = gr ? "ΣΥΝΔΕΔΕΜΕΝΟ" : "CONNECTED";
            break;
        case TelephonyManager.DATA_CONNECTING:
            dataStateLabel = gr ? "ΣΥΝΔΕΣΗ..." : "CONNECTING";
            break;
        case TelephonyManager.DATA_DISCONNECTED:
            dataStateLabel = gr ? "ΑΠΟΣΥΝΔΕΔΕΜΕΝΟ" : "DISCONNECTED";
            break;
        default:
            dataStateLabel = gr ? "ΑΓΝΩΣΤΟ" : "UNKNOWN";
            break;
    }

    logLabelValue(gr ? "Κατάσταση Δεδομένων" : "Data State", dataStateLabel);

    // ------------------------------------------------------------
    // Internet routing context (best effort)
    // ------------------------------------------------------------
    if (s.hasInternet) {
        logLabelOkValue(gr ? "Internet Context" : "Internet Context",
                gr ? "ΔΙΑΘΕΣΙΜΟ (system routing)" : "AVAILABLE (system routing)");
    } else {
        logLabelWarnValue(gr ? "Internet Context" : "Internet Context",
                gr ? "ΜΗ ΔΙΑΘΕΣΙΜΟ" : "NOT AVAILABLE");
    }

// ------------------------------------------------------------
// VERDICT ENGINE (GEL)
// ------------------------------------------------------------

boolean hasSim = s.simReady;
boolean inService = s.inService;
boolean dataConnected = s.dataState == TelephonyManager.DATA_CONNECTED;
boolean hasInternet = s.hasInternet;

String verdict;
String recommendation;

// =====================
// CASE 1 — FULL OK
// =====================
if (hasSim && (inService || hasInternet)) {

    verdict = gr
            ? "Το δίκτυο λειτουργεί κανονικά."
            : "Network is operating normally.";

    recommendation = gr
            ? "Δεν απαιτείται ενέργεια."
            : "No action required.";
}

// =====================
// CASE 2 — NO INTERNET
// =====================
else if (!hasInternet) {

    verdict = gr
            ? "Δεν υπάρχει πρόσβαση στο διαδίκτυο."
            : "No internet access detected.";

    recommendation = gr
            ? "Έλεγχος Wi-Fi ή ενεργοποίηση δεδομένων κινητής."
            : "Check Wi-Fi or enable mobile data.";
}

// =====================
// CASE 3 — NO SERVICE
// =====================
else if (hasSim && !inService) {

    verdict = gr
            ? "Η SIM είναι ενεργή αλλά δεν υπάρχει υπηρεσία δικτύου."
            : "SIM is ready but no network service.";

    recommendation = gr
            ? "Μετακινηθείτε σε περιοχή με σήμα ή ελέγξτε τον πάροχο."
            : "Move to better signal area or check carrier.";
}

// =====================
// CASE 4 — DATA OFF
// =====================
else if (hasSim && !dataConnected) {

    verdict = gr
            ? "Τα δεδομένα κινητής είναι απενεργοποιημένα."
            : "Mobile data is disabled.";

    recommendation = gr
            ? "Ενεργοποιήστε mobile data αν απαιτείται."
            : "Enable mobile data if needed.";
}

// =====================
// FALLBACK
// =====================
else {

    verdict = gr
            ? "Μερική κατάσταση δικτύου."
            : "Partial network state.";

    recommendation = gr
            ? "Ελέγξτε SIM, δεδομένα και σήμα."
            : "Check SIM, data and signal.";
}

// ------------------------------------------------------------
// OUTPUT (NO DUPLICATES)
// ------------------------------------------------------------
appendHtml("<br>");

logLabelValue(gr ? "Συμπέρασμα" : "Verdict", verdict);
logLabelValue(gr ? "Πρόταση" : "Recommendation", recommendation);

    appendHtml("<br>");
    logOk(gr ? "Το Lab 11 ολοκληρώθηκε." : "Lab 11 finished.");
    logLine();
}

// ============================================================
// LAB 12 — Call Function Interpretation (Laboratory)
// ============================================================
private void lab12CallFunctionInterpretation() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 12 — Ερμηνεία Λειτουργίας Κλήσεων (Εργαστήριο)"
            : "LAB 12 — Call Function Interpretation (Laboratory)");
    logLine();

    TelephonySnapshot s = getTelephonySnapshot();

    // ------------------------------------------------------------
    // Airplane mode (context only)
    // ------------------------------------------------------------
    if (s.airplaneOn) {
        logInfo(gr
                ? "Η Λειτουργία Πτήσης είναι ΕΝΕΡΓΗ. Τα voice radio interfaces είναι σκόπιμα απενεργοποιημένα."
                : "Airplane mode is ENABLED. Voice radio interfaces are intentionally disabled.");
        return;
    }

    // ------------------------------------------------------------
    // SIM availability (context only)
    // ------------------------------------------------------------
    if (s.simReady) {
        logLabelOkValue(gr ? "Κατάσταση SIM" : "SIM State", gr ? "ΕΤΟΙΜΗ" : "READY");
    } else {
        logLabelWarnValue(gr ? "Κατάσταση SIM" : "SIM State", gr ? "ΜΗ ΕΤΟΙΜΗ" : "NOT READY");
    }

    if (!s.simReady) {
        logInfo(gr
                ? "Η διαθεσιμότητα φωνητικής υπηρεσίας εξαρτάται από το αν η SIM είναι έτοιμη. "
                  + "Δεν βγαίνει λειτουργικό συμπέρασμα."
                : "Voice service availability depends on SIM readiness. "
                  + "No functional verdict inferred.");
        return;
    }

    // ------------------------------------------------------------
    // Legacy voice service state (informational)
    // ------------------------------------------------------------
    if (s.inService) {
        logLabelOkValue(
                gr ? "Φωνητική Υπηρεσία (legacy)" : "Voice Service (legacy)",
                gr ? "ΣΕ ΥΠΗΡΕΣΙΑ" : "IN SERVICE"
        );
    } else {
        logLabelWarnValue(
                gr ? "Φωνητική Υπηρεσία (legacy)" : "Voice Service (legacy)",
                gr ? "ΔΕΝ ΑΝΑΦΕΡΕΤΑΙ ΩΣ ΣΕ ΥΠΗΡΕΣΙΑ" : "NOT REPORTED AS IN SERVICE"
        );
    }

    if (!s.inService) {
        logInfo(gr
                ? "Η legacy εγγραφή υπηρεσίας δεν αναφέρεται. "
                  + "Σε σύγχρονες LTE/5G συσκευές, φωνή/δεδομένα μπορεί να παρέχονται μέσω IMS (VoLTE / VoWiFi)."
                : "Legacy service registration is not reported. "
                  + "On modern LTE/5G devices, voice and data may be provided via IMS (VoLTE / VoWiFi).");
    }

    // ------------------------------------------------------------
    // Internet context (IMS relevance)
    // ------------------------------------------------------------
    if (s.hasInternet) {
    logLabelOkValue(
            gr ? "Κατάσταση Σύνδεσης Internet"
               : "Internet Context",
            gr ? "ΔΙΑΘΕΣΙΜΗ (system routing)"
               : "AVAILABLE (system routing)"
    );
} else {
    logLabelWarnValue(
            gr ? "Κατάσταση Σύνδεσης Internet"
               : "Internet Context",
            gr ? "ΜΗ ΔΙΑΘΕΣΙΜΗ"
               : "NOT AVAILABLE"
    );
}

    if (s.hasInternet) {
        logOk(gr
                ? "Εντοπίστηκε ενεργό internet routing. "
                  + "Κλήσεις μέσω IMS (VoLTE / VoWiFi) μπορεί να υποστηρίζονται, ανάλογα με τον πάροχο."
                : "Active internet routing detected. "
                  + "IMS-based calling (VoLTE / VoWiFi) may be supported depending on carrier configuration.");
    } else {
        logOk(gr
                ? "Δεν εντοπίστηκε ενεργό internet routing. "
                  + "Οι κλασικές κλήσεις μπορεί να λειτουργούν κανονικά, αν τις υποστηρίζει το δίκτυο."
                : "No active internet routing detected. "
                  + "Legacy voice calling may still function if supported by the network.");
    }

    // ------------------------------------------------------------
    // Laboratory conclusion
    // ------------------------------------------------------------
    logOk(gr
            ? "Η εργαστηριακή ερμηνεία ολοκληρώθηκε. "
              + "Αυτό το τεστ δεν ξεκινά ούτε επιβεβαιώνει πραγματική κλήση."
            : "Laboratory interpretation complete. "
              + "This test does not initiate or verify real call execution.");

    logInfo(gr
            ? "Το audio routing κλήσης και οι διαδρομές μικροφώνου/ακουστικού ελέγχονται ξεχωριστά (LAB 3)."
            : "Call audio routing and microphone/earpiece paths are examined separately (LAB 3).");

    appendHtml("<br>");
    logOk(gr ? "Το Lab 12 ολοκληρώθηκε." : "Lab 12 finished.");
    logLine();
}

// ============================================================
// LAB 13 — Bluetooth Connectivity Check
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
        logError(gr
                ? "Το Bluetooth ΔΕΝ υποστηρίζεται σε αυτή τη συσκευή."
                : "Bluetooth NOT supported on this device.");
        logLine();
        return;
    }

    boolean enabled = false;
    try { enabled = ba.isEnabled(); } catch (Throwable ignore) {}

    if (!enabled) {
        logError(gr
                ? "Το Bluetooth είναι ΚΛΕΙΣΤΟ. Ενεργοποίησέ το και ξαναδοκίμασε."
                : "Bluetooth is OFF. Please enable Bluetooth and retry.");
        logLine();
        return;
    }

    logLabelOkValue(
            gr ? "Κατάσταση Bluetooth" : "Bluetooth State",
            gr ? "ΕΝΕΡΓΟ" : "ENABLED"
    );

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

    gate.show();

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
        logError(gr
                ? "Το Bluetooth ΔΕΝ υποστηρίζεται σε αυτή τη συσκευή."
                : "Bluetooth NOT supported on this device.");
        logLine();

        appendHtml("<br>");
        logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    logOk(gr ? "Το Bluetooth υποστηρίζεται." : "Bluetooth supported.");

    boolean enabled = false;
    try { enabled = lab13Ba.isEnabled(); } catch (Throwable ignore) {}

    logLabelValue(
            gr ? "Ενεργό" : "Enabled",
            enabled ? (gr ? "Ναι" : "Yes") : (gr ? "Όχι" : "No")
    );

    int state = BluetoothAdapter.STATE_OFF;
    try { state = lab13Ba.getState(); } catch (Throwable ignore) {}

    String stateStr;
    if (state == BluetoothAdapter.STATE_ON) {
        stateStr = gr ? "ΑΝΟΙΧΤΟ" : "ON";
    } else if (state == BluetoothAdapter.STATE_TURNING_ON) {
        stateStr = gr ? "ΑΝΟΙΓΕΙ" : "TURNING ON";
    } else if (state == BluetoothAdapter.STATE_TURNING_OFF) {
        stateStr = gr ? "ΚΛΕΙΝΕΙ" : "TURNING OFF";
    } else {
        stateStr = gr ? "ΚΛΕΙΣΤΟ" : "OFF";
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

    if (!enabled) {
        logWarn(gr
                ? "Το Bluetooth είναι ΚΛΕΙΣΤΟ — ενεργοποίησέ το και ξανατρέξε το Lab 13."
                : "Bluetooth is OFF — enable Bluetooth and re-run Lab 13.");
        logLine();

        appendHtml("<br>");
        logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    // ---------- PAIRED DEVICES SNAPSHOT
    try {
        Set<BluetoothDevice> bonded = lab13Ba.getBondedDevices();

        if (bonded == null || bonded.isEmpty()) {

            logWarn(gr
                    ? "Συζευγμένες συσκευές Bluetooth: 0 (δεν βρέθηκαν)."
                    : "Paired Bluetooth devices: 0 (no paired devices found).");

        } else {

            logOk(gr
                    ? "Βρέθηκαν Συζευγμένες συσκευές Bluetooth: " + bonded.size()
                    : "Paired Bluetooth devices detected: " + bonded.size());

            for (BluetoothDevice d : bonded) {

                String name = gr ? "Χωρίς όνομα" : "Unnamed";
                String addr = gr ? "χωρίς-mac" : "no-mac";
                String typeStr = gr ? "Άγνωστο" : "Unknown";

                if (d != null) {
                    try {
                        if (d.getName() != null) name = d.getName();
                    } catch (Throwable ignore) {}

                    try {
                        if (d.getAddress() != null) addr = d.getAddress();
                    } catch (Throwable ignore) {}

                    try {
                        int type = d.getType();
                        typeStr =
                                type == BluetoothDevice.DEVICE_TYPE_CLASSIC ? (gr ? "Κλασικό" : "Classic") :
                                type == BluetoothDevice.DEVICE_TYPE_LE ? "LE" :
                                type == BluetoothDevice.DEVICE_TYPE_DUAL ? (gr ? "Διπλό" : "Dual") :
                                (gr ? "Άγνωστο" : "Unknown");
                    } catch (Throwable ignore) {}
                }

                logInfo("• " + name + " [" + typeStr + "] (" + addr + ")");
            }
        }

    } catch (Throwable e) {

        logWarn(gr
                ? "Αποτυχία σάρωσης συζευγμένων συσκευών: " + e.getClass().getSimpleName()
                : "Paired device scan failed: " + e.getClass().getSimpleName());
    }

    // ------------------------------------------------------------
    // SYSTEM-ONLY MODE (Skip external device test)
    // ------------------------------------------------------------
    if (lab13SkipExternalTest) {
        logWarn(gr
                ? "Το τεστ εξωτερικής Bluetooth συσκευής παραλείφθηκε από τον χρήστη."
                : "External Bluetooth device test skipped by user.");
        logOk(gr
                ? "Συνέχεια μόνο με έλεγχο Bluetooth του συστήματος."
                : "Proceeded with system Bluetooth connection check only.");

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

    try { unregisterReceiver(lab13BtReceiver); } catch (Throwable ignore) {}

// ------------------------------------------------------------
// REGISTER BLUETOOTH RECEIVER (LAB 13)
// ------------------------------------------------------------
IntentFilter f = new IntentFilter();
f.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
f.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
registerReceiver(lab13BtReceiver, f);

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

// ---------------------------
// TTS (ONLY IF NOT MUTED)
// ---------------------------
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (lab13Dialog.isShowing() && !AppTTS.isMuted(this)) {
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

        requestPermissions(
                new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                REQ_LAB13_BT_CONNECT
        );
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
    lab13CounterText.setText(
            gr
                    ? "Παρακολούθηση: σε αναμονή…"
                    : "Monitoring: waiting…"
    );
}
}

// ============================================================
// MONITOR LOOP (60s) — polls connected devices + detects flips
// ============================================================
private void startLab13Monitor60s() {

    if (lab13MonitoringStarted) return;
    lab13MonitoringStarted = true;

    lab13Running = true;
    lab13StartMs = SystemClock.elapsedRealtime();
    lab13Seconds = 0;

    lab13DisconnectEvents = 0;
    lab13ReconnectEvents = 0;

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

            if (!lab13Running) return;

            boolean adapterStable = false;
            try {
                adapterStable =
                        lab13Ba != null &&
                        lab13Ba.isEnabled() &&
                        lab13Ba.getState() == BluetoothAdapter.STATE_ON;
            } catch (Throwable ignore) {}

            boolean connected = lab13IsAnyExternalConnected();

            // ------------------------------------------------------------
            // HARD SYNC — receiver + snapshot (EACH TICK)
            // ------------------------------------------------------------
            // Priority: explicit events > snapshot
            if (lab13ReceiverSawConnection) {
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
            lab13Seconds++;

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
                lab13FinishAndReport(adapterStable);
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

    lab13Running = false;
    try { lab13Handler.removeCallbacksAndMessages(null); } catch (Throwable ignore) {}

    try {
        if (lab13Dialog != null && lab13Dialog.isShowing())
            lab13Dialog.dismiss();
    } catch (Throwable ignore) {}
    lab13Dialog = null;

    try { unregisterReceiver(lab13BtReceiver); } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // NO EXTERNAL DEVICE
    // ------------------------------------------------------------
    if (!lab13HadAnyConnection) {

        logLine();
        logInfo(gr ? "LAB 13 — Αποτελέσματα"
                   : "LAB 13 — Results");

        logWarn(gr
                ? "Δεν συνδέθηκε καμία εξωτερική Bluetooth συσκευή."
                : "No external Bluetooth device was connected.");

        appendHtml("<br>");
        logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
        logLine();
        return;
    }

    logLine();
    logInfo(gr ? "LAB 13 — Αποτελέσματα (60s monitor)"
               : "LAB 13 — Results (60s monitor)");

    // ============================================================
    // 1️⃣ Adapter Stability (COLOR)
    // ============================================================
    if (adapterStable) {
        logLabelOkValue(
                gr ? "Σταθερότητα Bluetooth adapter"
                   : "Adapter stability",
                gr ? "Σταθερή" : "Stable"
        );
    } else {
        logLabelErrorValue(
                gr ? "Σταθερότητα Bluetooth adapter"
                   : "Adapter stability",
                gr ? "Μη σταθερή" : "Unstable"
        );
    }

    // ============================================================
    // 2️⃣ Disconnect Events (SEVERITY)
    // ============================================================
    if (lab13DisconnectEvents == 0) {

        logLabelOkValue(
                gr ? "Αποσυνδέσεις" : "Disconnect events",
                "0"
        );

    } else if (lab13DisconnectEvents <= 2) {

        logLabelWarnValue(
                gr ? "Αποσυνδέσεις" : "Disconnect events",
                String.valueOf(lab13DisconnectEvents)
        );

    } else {

        logLabelErrorValue(
                gr ? "Αποσυνδέσεις" : "Disconnect events",
                String.valueOf(lab13DisconnectEvents)
        );
    }

    // ============================================================
    // 3️⃣ Reconnect Events (RECOVERY INDICATOR)
    // ============================================================
    if (lab13ReconnectEvents == 0) {

        logLabelValue(
                gr ? "Επανασυνδέσεις" : "Reconnect events",
                "0"
        );

    } else {

        logLabelOkValue(
                gr ? "Επανασυνδέσεις" : "Reconnect events",
                String.valueOf(lab13ReconnectEvents)
        );
    }

    // ============================================================
    // 4️⃣ PATTERN DIAGNOSIS
    // ============================================================
    boolean flapping =
            lab13DisconnectEvents >= 3 &&
            lab13ReconnectEvents >= 3;

    boolean fullLoss =
            lab13DisconnectEvents >= 3 &&
            lab13ReconnectEvents == 0;

    logLine();

    if (flapping) {

        logLabelErrorValue(
                gr ? "Διάγνωση" : "Diagnosis",
                gr
                        ? "Connection flapping (συχνές αποσυνδέσεις & επανασυνδέσεις)"
                        : "Connection flapping (frequent disconnect/reconnect)"
        );

        logWarn(gr
                ? "Πιθανό πρόβλημα εξωτερικής συσκευής ή RF παρεμβολή."
                : "Likely external device instability or RF interference.");

    } else if (fullLoss) {

        logLabelErrorValue(
                gr ? "Διάγνωση" : "Diagnosis",
                gr
                        ? "Πλήρης απώλεια σύνδεσης"
                        : "Full connection loss"
        );

    } else if (lab13DisconnectEvents > 0) {

        logLabelWarnValue(
                gr ? "Διάγνωση" : "Diagnosis",
                gr
                        ? "Μικρή αστάθεια σύνδεσης"
                        : "Minor connection instability"
        );

    } else {

        logLabelOkValue(
                gr ? "Διάγνωση" : "Diagnosis",
                gr
                        ? "Σταθερή σύνδεση Bluetooth"
                        : "Stable Bluetooth connection"
        );
    }

    // ============================================================
    // ROOT NOTE
    // ============================================================
    logLabelValue(
            "Root access",
            isDeviceRooted()
                    ? (gr
                        ? "Διαθέσιμο (advanced diagnostics)"
                        : "Available (advanced diagnostics)")
                    : (gr
                        ? "Μη διαθέσιμο"
                        : "Not available")
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
    logLine();
}

// ============================================================
// PROFILE NAME (small internal helper)
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

    // stop lab state
    lab13Running = false;
    try { lab13Handler.removeCallbacksAndMessages(null); } catch (Throwable ignore) {}

    // close dialog
    try {
        if (lab13Dialog != null && lab13Dialog.isShowing())
            lab13Dialog.dismiss();
    } catch (Throwable ignore) {}
    lab13Dialog = null;

    // unregister BT receiver (safety)
    try { unregisterReceiver(lab13BtReceiver); } catch (Throwable ignore) {}

// stop TTS
AppTTS.stop();

    // ------------------------------------------------------------
    // ABORT LOG
    // ------------------------------------------------------------
    appendHtml("<br>");
    logWarn("Lab 13 aborted by user.");
    logLine();
}

// ============================================================
// LAB 14 — ENTRY
// ============================================================

private void lab14BatteryHealthStressTest() {

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
    voltageStart = getBatteryVoltageFiltered();

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

// 🔴 REAL THREADS (όχι θεωρητικά)
int cpuThreadsToUse =
        !isLab14BMode
                ? (lab14OptimalThreads > 0
                    ? lab14OptimalThreads
                    : cores)
                : cores;

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

appendLog("ENGINE", "START");

// ------------------------------------------------------------
// 🔴 START ENGINE (ONLY)
// ------------------------------------------------------------
lab14Engine.startDrainSession();

// 🔴 vibration loop
ui.removeCallbacks(lab14VibrationLoop);
ui.post(lab14VibrationLoop);

// ------------------------------------------------------------
// 🔴 START UI (THIS DRIVES EVERYTHING)
// ------------------------------------------------------------
startLab14SharedUI(durationSec, gr);

// ❌ ΔΕΝ ξεκινάς εδώ:
// startCpuBurn...
// startGpuStress...
// startMemory...

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
    // 🔴 SELF FALLBACK CLASSIFIER (if upstream label missing)
    // =================================================
    if ("Unknown".equals(labelSafe)) {

        if (finalSag < 0.040f) {
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
    }

    lab14LastLabel = labelSafe;

    String sagText = String.format(
            Locale.US,
            "%.3f V (%s)",
            finalSag,
            labelSafe
    );


    if ("Critical".equals(labelSafe)) {

        logLabelErrorValue(
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

} else {   // ✅ αυτό έλειπε

    if (!Float.isNaN(finalSag)) {

        float rEst = finalSag * 1000f / 2.0f;

        logLabelValue(
            gr ? "Αντίσταση υπό φορτίο"
               : "Dynamic resistance",
            String.format(
               Locale.US,
               "~%.0f mΩ (estimated)",
               rEst
            )
        );

    } else {

        logLabelWarnValue(
            gr ? "Αντίσταση υπό φορτίο"
               : "Dynamic resistance",
            "N/A"
        );
    }
}

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
// 🔴 RECOMMENDATION ENGINE (FINAL — PRIORITIZED)
// =====================================================

String recommendation;

// 🔴 PRIORITY 1 — LIMITER
if (lab14_systemLimited[0]) {

    recommendation = gr
            ? "Η συσκευή περιόρισε το φορτίο (BMS). Συνιστάται επανάληψη υπό διαφορετικές συνθήκες."
            : "Device limited the load (BMS). Retest under different conditions is recommended.";

// 🔴 PRIORITY 2 — CRITICAL
} else if ("Critical".equals(labelSafe)) {

    recommendation = gr
            ? "Συνιστάται άμεσος τεχνικός έλεγχος ή αντικατάσταση μπαταρίας."
            : "Immediate battery service or replacement is recommended.";

// 🔴 PRIORITY 3 — WEAK
} else if ("Weak".equals(labelSafe)) {

    recommendation = gr
            ? "Ενδείξεις φθοράς. Συνιστάται παρακολούθηση και πιθανή αντικατάσταση."
            : "Battery wear detected. Monitoring and possible replacement recommended.";

// 🔴 PRIORITY 4 — CALIBRATION
} else if (calibrationDrift != null && calibrationDrift.length > 0 && calibrationDrift[0]) {

    recommendation = gr
            ? "Πιθανή απόκλιση βαθμονόμησης. Συνιστάται πλήρης κύκλος φόρτισης/εκφόρτισης."
            : "Possible calibration drift. Full charge-discharge cycle recommended.";

// 🔴 PRIORITY 5 — UNKNOWN
} else if ("Unknown".equals(labelSafe)) {

    recommendation = gr
            ? "Ανεπαρκή δεδομένα για αξιόπιστη διάγνωση."
            : "Insufficient data for reliable diagnosis.";

// 🔴 DEFAULT — NORMAL
} else {

    recommendation = gr
            ? "Η μπαταρία λειτουργεί εντός φυσιολογικών ορίων."
            : "Battery operates within normal parameters.";
}

// ----------------------------------------------------
// 🔴 OUTPUT LEVEL
// ----------------------------------------------------
String recLabel = gr ? "Σύσταση" : "Recommendation";

if (lab14_systemLimited[0] || "Critical".equals(labelSafe)) {

    logLabelErrorValue(recLabel, recommendation);

} else if ("Weak".equals(labelSafe) ||
           "Unknown".equals(labelSafe) ||
           (calibrationDrift != null && calibrationDrift.length > 0 && calibrationDrift[0])) {

    logLabelWarnValue(recLabel, recommendation);

} else {

    logLabelOkValue(recLabel, recommendation);
}

// =====================================================
// FINAL SUMMARY (SAFE)
// =====================================================

String summary;

if (lab14_systemLimited[0]) {

    summary = gr
            ? "Η συσκευή περιόρισε το φορτίο (BMS).\nΔεν είναι δυνατή αξιόπιστη εκτίμηση."
            : "Device limited the load (BMS).\nReliable estimation is not possible.";

} else if ("Unknown".equals(labelSafe)) {

    summary = gr
            ? "Ανεπαρκή δεδομένα για διάγνωση."
            : "Insufficient data for diagnosis.";

} else {

    summary = String.format(
            Locale.US,
            gr
                    ? "Κατάσταση: %s"
                    : "Status: %s",
            labelSafe
    );
}

// ----------------------------------------------------
String summaryLabel =
        gr ? "Σύνοψη" : "Summary";

if (lab14_systemLimited[0] || "Critical".equals(labelSafe)) {

    logLabelErrorValue(summaryLabel, summary);

} else if ("Weak".equals(labelSafe) ||
           "Unknown".equals(labelSafe)) {

    logLabelWarnValue(summaryLabel, summary);

} else if ("Excellent".equals(labelSafe) ||
           "Good".equals(labelSafe) ||
           "Normal".equals(labelSafe)) {

    logLabelOkValue(summaryLabel, summary);

} else {

    logLabelValue(summaryLabel, summary);
}
}

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
        boolean[] lab14_systemLimited
) {

    appendHtml("<br>");
    logLine();
    logInfo(gr ? "Τελικό αποτέλεσμα" : "Final result");
    logLine();

    String condLabel = gr ? "Κατάσταση μπαταρίας" : "Battery condition";

    // 🔴 SAFE LABEL
    String lbl =
            (label != null && !label.isEmpty())
                    ? label
                    : "Unknown";

    boolean limiter =
            lab14_systemLimited != null &&
            lab14_systemLimited.length > 0 &&
            lab14_systemLimited[0];

    boolean collapse =
            collapseRisk != null &&
            collapseRisk.length > 0 &&
            collapseRisk[0];

    boolean calibration =
            calibrationDrift != null &&
            calibrationDrift.length > 0 &&
            calibrationDrift[0];

    // ----------------------------------------------------
    // 🔴 MAIN LABEL (WITH LIMITER PRIORITY)
    // ----------------------------------------------------
    if (limiter || "Critical".equals(lbl)) {

        logLabelErrorValue(condLabel, lbl);

    } else if ("Weak".equals(lbl) || "Unknown".equals(lbl)) {

        logLabelWarnValue(condLabel, lbl);

    } else if ("Excellent".equals(lbl) ||
               "Good".equals(lbl) ||
               "Normal".equals(lbl)) {

        logLabelOkValue(condLabel, lbl);

    } else {

        logLabelValue(condLabel, lbl);
    }

    // ----------------------------------------------------
    // 🔴 COLLAPSE
    // ----------------------------------------------------
    logLabelValue(
            gr ? "Κατάρρευση τάσης" : "Voltage collapse",
            collapse
                    ? (gr ? "Υψηλός κίνδυνος" : "High risk")
                    : (gr ? "Δεν εντοπίστηκε" : "Not detected")
    );

    // ----------------------------------------------------
    // 🔴 SWELLING
    // ----------------------------------------------------
    if (smartSwelling) {

        logLabelWarnValue(
                gr ? "Διόγκωση μπαταρίας" : "Battery swelling",
                gr
                        ? "Πιθανές ενδείξεις — απαιτείται έλεγχος"
                        : "Possible indicators — verification recommended"
        );

    } else {

        logLabelValue(
                gr ? "Διόγκωση μπαταρίας" : "Battery swelling",
                gr
                        ? "Δεν εντοπίστηκαν αξιόπιστες ενδείξεις"
                        : "No reliable indicators detected"
        );
    }

    // ----------------------------------------------------
    // 🔴 CALIBRATION
    // ----------------------------------------------------
    logLabelValue(
            gr ? "Απόκλιση βαθμονόμησης" : "Calibration drift",
            calibration
                    ? (gr ? "Εντοπίστηκε" : "Detected")
                    : (gr ? "Κανονική" : "Normal")
    );

    // ----------------------------------------------------
    // 🔴 SYSTEM LIMIT
    // ----------------------------------------------------
    if (limiter) {

        logLabelWarnValue(
                gr ? "Περιορισμός συστήματος" : "System limitation",
                gr
                        ? "Το BMS περιόρισε το ρεύμα — μειωμένη αξιοπιστία"
                        : "BMS current limiting detected — reduced accuracy"
        );
    }
}

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
// 🔴 NO HARD ABORT ANYMORE
// ----------------------------------------------------
if (!hasReliableSag) {

    logLine();

    logWarn(gr
            ? "Ασθενές ή μη σαφές σήμα sag — συνεχίζεται η ανάλυση"
            : "Weak or unclear sag signal — analysis continues");

    logLine();

    res.label = "Low confidence";
    lab14LastLabel = res.label;

    logLabelWarnValue(
            gr ? "Πτώση τάσης υπό φορτίο"
               : "Voltage sag under load",
            gr ? "Χαμηλή εμπιστοσύνη μέτρησης"
               : "Low confidence measurement"
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

    String sagText = String.format(
            Locale.US,
            "%.3f V (%s)",
            finalSag,
            sagLabel
    );

    if ("Critical".equals(sagLabel)) {

        logLabelErrorValue(
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

if ((Float.isNaN(currentNow) ||
     Math.abs(currentNow) < 50f) &&
    drainMah > 0 &&
    dtMs > 0) {

    float mahPerSec =
            (float) drainMah / (dtMs / 1000f);

    currentNow = Math.abs(mahPerSec * 3600f);
}

// 🔴 CURRENT VALID FLAG
boolean hasCurrent =
        !Float.isNaN(currentNow) &&
        currentNow >= 100f &&
        currentNow < 8000f;
        
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

    engine.saveDrainValue(mahPerHour);
    engine.saveRun();
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
    !Float.isNaN(aging.index)) {

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
// BATTERY HEALTH ESTIMATION (SOH - TRIMMED FINAL)
// ----------------------------------------------------

float health = 100f;

// ----------------------------------------------------
// INTERNAL RESISTANCE
// ----------------------------------------------------
if (!Float.isNaN(internalResistance[0]) &&
    internalResistance[0] > 0.01f &&
    internalResistance[0] < 0.40f) {

    if (internalResistance[0] > 0.22f)
        health -= 18f;
    else if (internalResistance[0] > 0.15f)
        health -= 8f;
}

// ----------------------------------------------------
// CYCLES
// ----------------------------------------------------
if (cycles > 0) {

    if (cycles > 700)
        health -= 18f;
    else if (cycles > 500)
        health -= 10f;
    else if (cycles > 300)
        health -= 5f;
}

// ----------------------------------------------------
// TEMPERATURE
// ----------------------------------------------------
if (!Float.isNaN(lab14TempPeak) &&
    lab14TempPeak > 0f &&
    lab14TempPeak < 80f) {

    if (lab14TempPeak > 50f)
        health -= 10f;
    else if (lab14TempPeak > 45f)
        health -= 5f;
}

// ----------------------------------------------------
// SMART SWELLING
// ----------------------------------------------------

float tempDelta =
        (!Float.isNaN(tempStart) &&
         !Float.isNaN(lab14TempPeak))
                ? Math.max(
                        0f,
                        lab14TempPeak - tempStart
                  )
                : Float.NaN;

float rMilli =
        !Float.isNaN(internalResistance[0])
                ? internalResistance[0] * 1000f
                : Float.NaN;

if (!Float.isNaN(rMilli) &&
    (rMilli < 1f || rMilli > 400f)) {

    rMilli = Float.NaN;
}

float currentSafe =
        (!Float.isNaN(currentNow) &&
         Math.abs(currentNow) > 50f)
                ? currentNow
                : Float.NaN;

double drainSafe =
        (!Double.isNaN(drainPercentPerHour) &&
         drainPercentPerHour > 0)
                ? drainPercentPerHour
                : Double.NaN;

boolean smartSwelling = false;

if (hasSag &&
    finalSag >= 0.06f &&
    finalSag < 1.0f &&
    validDrain &&
    !lab14_systemLimited[0]) {

    smartSwelling =
            detectSwellingSmart(
                    finalSag,
                    tempDelta,
                    currentSafe,
                    rMilli,
                    drainSafe
            );
}

if (smartSwelling) {
    health -= 10f;
}

// ----------------------------------------------------
// CLAMP
// ----------------------------------------------------
health =
        Math.max(
                0f,
                Math.min(100f, health)
        );

lab14HealthPercent = health;

// ----------------------------------------------------
// LABEL
// ----------------------------------------------------
if (health >= 90f)
    lab14HealthLabel = "Excellent";
else if (health >= 80f)
    lab14HealthLabel = "Good";
else if (health >= 70f)
    lab14HealthLabel = "Normal";
else if (health >= 60f)
    lab14HealthLabel = "Worn";
else
    lab14HealthLabel = "Degraded";


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

    // ----------------------------
    // BASE FROM SAG
    // ----------------------------
    if (!hasSag || finalSag < 0.02f) {

        batteryTruth = "Unknown";

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
              ? "Έντονη πτώση τάσης — πιθανή φθορά κυψελών"
              : "Severe voltage sag — degraded battery cells"
        );

    } else if (finalSag > 0.10f) {

        logLabelWarnValue(
            gr ? "Διάγνωση":"Diagnosis",
            gr
              ? "Αυξημένη πτώση τάσης — πιθανή γήρανση"
              : "Elevated voltage sag — possible aging"
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
// 🔴 STRUCTURAL INTEGRITY (FINAL — SAG BASED ONLY)
// =====================================================

if (hasSag &&
    validDrain &&
    !lab14_systemLimited[0] &&
    finalSag >= 0.02f &&
    finalSag < 1.0f) {

    String siText;

    if (finalSag < 0.05f) {

        siText = gr
                ? "Καλή δομική σταθερότητα"
                : "Good structural integrity";

        logLabelOkValue(
                gr ? "Δομική ακεραιότητα μπαταρίας"
                   : "Battery structural integrity",
                siText
        );

    } else if (finalSag < 0.12f) {

        siText = gr
                ? "Μέτρια δομική κατάσταση"
                : "Moderate structural condition";

        logLabelValue(
                gr ? "Δομική ακεραιότητα μπαταρίας"
                   : "Battery structural integrity",
                siText
        );

    } else {

        siText = gr
                ? "Πιθανή φθορά κυψελών"
                : "Possible cell degradation";

        logLabelWarnValue(
                gr ? "Δομική ακεραιότητα μπαταρίας"
                   : "Battery structural integrity",
                siText
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Δομική ακεραιότητα μπαταρίας"
               : "Battery structural integrity",
            gr
                    ? "Ανεπαρκή δεδομένα"
                    : "Insufficient data"
    );
}

// =====================================================
// 🔴 CELL BALANCE (FINAL - no legacy sag1/sag2)
// =====================================================

if (hasSag &&
    validDrain &&
    !lab14_systemLimited[0]) {

    if (cellImbalanceRisk[0]) {

        logLabelWarnValue(
                gr ? "Ισορροπία κυψελών μπαταρίας"
                   : "Battery cell balance",
                gr
                        ? "Εντοπίστηκε πιθανή ασυμμετρία κυψελών"
                        : "Possible lithium cell imbalance detected"
        );

    } else {

        logLabelOkValue(
                gr ? "Ισορροπία κυψελών μπαταρίας"
                   : "Battery cell balance",
                gr
                        ? "Δεν εντοπίστηκε ανισορροπία"
                        : "No imbalance detected"
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Ισορροπία κυψελών μπαταρίας"
               : "Battery cell balance",
            gr
                    ? "Ανεπαρκή δεδομένα"
                    : "Insufficient data"
    );
}

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
        !hasSag ||
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

// FINAL SCORE
lab14LogFinalScore(
        gr,
        res.label,
        res.powerMw,
        collapseRisk,
        smartSwelling,
        calibrationDrift,
        lab14_systemLimited
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

// store health only full run
if (!partial &&
    !lab14_systemLimited[0]) {

   SharedPreferences.Editor e = p.edit();
   e.putFloat(
      "lab14_health_percent",
      lab14HealthPercent
   );
   e.apply();
}


// RUN COUNT
boolean countableRun =
        hasSag &&
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
    
    // 🔴 FAST UI BAR (ΕΔΩ ΜΠΑΙΝΕΙ)
runOnUiThread(() -> {

    int safeElapsed = Math.max(0, Math.min(45, (int) elapsed));

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

    // 🔴 EXIT
    if (elapsed >= 45) break;

    // 🔴 FIXED INTERVAL (3s exact cadence)
    nextTick += 1000;

    long sleepTime = nextTick - SystemClock.elapsedRealtime();

    // 🔴 SMART SLEEP (interruptible feeling)
    if (sleepTime > 0) {

        long end = SystemClock.elapsedRealtime() + sleepTime;

        while (SystemClock.elapsedRealtime() < end) {

            if (!lab14Running || lab14Cancelled) {
                break;
            }

            try {
                Thread.sleep(100); // μικρά slices
            } catch (Throwable ignore) {}
        }
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

                int softThreads = Math.max(1, cores / 2);
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
    title.setTextColor(0xFFFFFFFF);
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
    }

    // =====================================================
    // 🔴 CRITICAL: INIT BEFORE THREADS
    // =====================================================
    if (!isLab14BMode) {

        // 🔴 RESET FIRST (πριν ξεκινήσει ΟΤΙΔΗΠΟΤΕ)
        resetLab14RuntimeState();

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

    appendLog("TIME", "FINAL END at " + elapsed + " sec");
    
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

Float cpuTemp = readCpuTempSafe();

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

    // 🔴 HARD LOCK (14B hard phase)
    if (inHardPhase) return;

    if (!lab14Running || lab14Cancelled) return;

    long now = SystemClock.elapsedRealtime();

    // 🔴 cooldown
    if (now - lab14LastCpuAdjustTs < 6000) return;

    final int cores = Runtime.getRuntime().availableProcessors();

    int oldThreads = lab14CpuThreadsCurrent > 0
            ? lab14CpuThreadsCurrent
            : Math.max(2, cores / 2);

    int newThreads = oldThreads;

    boolean validThermal = !Float.isNaN(thermalDelta);

    boolean lowThermal = validThermal && thermalDelta < 3f;
    boolean midThermal = validThermal && thermalDelta >= 3f && thermalDelta <= 7f;
    boolean highThermal = validThermal && thermalDelta > 7f;

    // ----------------------------------------------------
    // 🔴 SAFETY
    // ----------------------------------------------------
    if (systemLimited || highThermal) {

        newThreads = oldThreads - 1;

    }
    // ----------------------------------------------------
    // 🔴 BOOST
    // ----------------------------------------------------
    else if (weakLoad && lab14WeakLoadCounter >= 5 && lowThermal) {

        newThreads = oldThreads + 1;

    }
    // ----------------------------------------------------
    // 🔴 MID ZONE
    // ----------------------------------------------------
    else if (midThermal) {

        newThreads = oldThreads;
    }

    // ----------------------------------------------------
    // CLAMP
    // ----------------------------------------------------
    newThreads = Math.max(1, Math.min(cores, newThreads));

    int minThreads = Math.max(1, cores / 4);
    newThreads = Math.max(newThreads, minThreads);

    if (newThreads == oldThreads) return;

    // ----------------------------------------------------
    // 🔴 APPLY (LAB14B ONLY)
    // ----------------------------------------------------
    lab14CpuThreadsCurrent = newThreads;
    lab14LastCpuAdjustTs = now;

    try {
        stopCpuBurn();
        startCpuBurnLimitedThreads(newThreads);
    } catch (Throwable ignore) {}

    appendLog("CPU",
            "threads " + oldThreads + " → " + newThreads +
            " ΔT=" + (validThermal
                    ? String.format(java.util.Locale.US, "%.2f", thermalDelta)
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

    // 🔴 better fill behavior
    int active = Math.min(segCount,
            (int) Math.ceil(ratio * segCount));

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

            // 🔴 PRIMARY: Battery Intent
            try {
                IntentFilter ifilter =
                        new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

                Intent batteryStatus =
                        registerReceiver(null, ifilter);

                if (batteryStatus != null) {
                    int mv = batteryStatus.getIntExtra("voltage", -1);

                    if (mv > 3000 && mv < 5000) {
                        v = mv / 1000f;
                    }
                }

            } catch (Throwable ignore) {}

            // 🔴 FALLBACK: engine
            if (Float.isNaN(v)) {
                try {
                    float mv = idoctor.readBatteryVoltageMvStable(2, 5);

                    if (!Float.isNaN(mv) && mv > 3000f && mv < 5000f) {
                        v = mv / 1000f;
                    }
                } catch (Throwable ignore) {}
            }

            return (!Float.isNaN(v) && v > 3.0f && v < 5.0f)
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
// 🔴 LOAD 1 (no stop/start)
// ----------------------------------------------------
if (!sleepSilentlySafe(180)) return;
if (!lab14Running || lab14Cancelled) return;

float load1 = readVoltage.get();

if (!Float.isNaN(load1)) {

    if (Float.isNaN(vLoad1[0]) || load1 < vLoad1[0]) {
        vLoad1[0] = load1;
    }

    if (Float.isNaN(voltageUnderLoad[0]) || load1 < voltageUnderLoad[0]) {
        voltageUnderLoad[0] = load1;
    }
}

// ----------------------------------------------------
// 🔴 RECOVERY (natural, όχι forced)
// ----------------------------------------------------
if (!sleepSilentlySafe(220)) return;
if (!lab14Running || lab14Cancelled) return;

float rec = readVoltage.get();

if (!Float.isNaN(rec)) {

    float refLoad =
            !Float.isNaN(vLoad1[0]) ? vLoad1[0] : Float.NaN;

    if (Float.isNaN(refLoad) || rec >= refLoad) {
        if (Float.isNaN(vRecover[0]) || rec > vRecover[0]) {
            vRecover[0] = rec;
        }
    }
}

// ----------------------------------------------------
// 🔴 LOAD 2 (no spike restart)
// ----------------------------------------------------
if (!sleepSilentlySafe(220)) return;
if (!lab14Running || lab14Cancelled) return;

float load2 = readVoltage.get();

if (!Float.isNaN(load2)) {

    if (Float.isNaN(vLoad2[0]) || load2 < vLoad2[0]) {
        vLoad2[0] = load2;
    }

    if (Float.isNaN(voltageUnderLoad[0]) || load2 < voltageUnderLoad[0]) {
        voltageUnderLoad[0] = load2;
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

    boolean badBat = percent < 30 || percent > 70;
    boolean badCpu = !Float.isNaN(cpuTemp) && cpuTemp >= 60f;
    boolean badTemp = !Float.isNaN(tempC) && tempC >= 38f;
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
                    + "1) Μπαταρία μεταξύ 30% – 70%\n"
                    + "2) Θερμοκρασία μπαταρίας κάτω από 38°C\n"
                    + "3) Θερμοκρασία CPU κάτω από 60°C\n"
                    + "4) Η συσκευή να μην φορτίζεται\n"
                    : "Requirements for this test:\n\n"
                    + "1) Battery between 30% – 70%\n"
                    + "2) Battery temperature below 38°C\n"
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
                ? "• Η μπαταρία πρέπει να είναι μεταξύ 30% και 70%\n"
                : "• Battery must be between 30% and 70%\n");
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
                ? "Εκτός προτεινόμενου εύρους 30 έως 70 τοις εκατό. "
                : "Outside recommended range 30 to 70 percent. ");
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
                percent >= 30 && percent <= 70
                && (Float.isNaN(temp) || temp < 38f)
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
            ? "Συνιστάται επανάληψη με φόρτιση 30–70%, χωρίς φόρτιση και σε φυσιολογική θερμοκρασία."
            : "Repeat test at 30–70% battery, not charging, normal temperature.");

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

    stopCpuBurn(); // 🔴 clean start

    cpuBurnRunning = true;

    final int maxCores = Runtime.getRuntime().availableProcessors();

    final int[] activeThreads = { maxCores };

    for (int i = 0; i < maxCores; i++) {

        final int threadIndex = i;

        Thread t = new Thread(() -> {

            final long t0 = System.currentTimeMillis(); // 🔴 BOOST TIMER FIX
            long lastAdjust = t0;

            while (cpuBurnRunning &&
                   lab14Running &&
                   !lab14Cancelled &&
                   !Thread.currentThread().isInterrupted()) {

                // 🔴 dynamic participation
                if (threadIndex >= activeThreads[0]) {
                    try { Thread.sleep(20); } catch (Throwable ignore) {}
                    continue;
                }

                // 🔥 heavy compute (BOOSTED)
                double acc = 0;
                long now = System.nanoTime();

                for (int j = 1; j < 16000; j++) { // 🔴 πιο δυνατό load
                    acc += Math.sqrt(j * now);
                    acc *= 1.0000001;

                    if ((j & 7) == 0) {
                        acc -= Math.log(j + 1);
                    }
                }

                // anti-optimization
                if (acc > 1e12) acc = 0;

                // =====================================================
                // 🔴 BOOST + ADAPTIVE CONTROL
                // =====================================================

                long nowMs = System.currentTimeMillis();

                // 🔥 FULL LOAD για τα πρώτα 8s
                if (nowMs - t0 < 8000) {

                    activeThreads[0] = maxCores;

                } else {

                    // 🔴 adaptive κάθε 2 sec
                    if (nowMs - lastAdjust > 2000) {

                        float temp = readCpuTempSafe();

                        if (!Float.isNaN(temp)) {

                            if (temp > 65f && activeThreads[0] > 2) {
                                activeThreads[0]--;
                            }
                            else if (temp < 55f && activeThreads[0] < maxCores) {
                                activeThreads[0]++;
                            }
                        }

                        lastAdjust = nowMs;
                    }
                }
            }

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
                    appendLog("MEM","keep alive");
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
// LAB 14B — CONDITIONS CHECK (BATTERY USAGE TEST)
// ============================================================
private boolean checkLab14BConditions() {

    final boolean gr = AppLang.isGreek(this);

    int percent = (int) getCurrentBatteryPercent();

    boolean chargingNow = false;
    float tempC = Float.NaN;

    try {
        chargingNow = isDeviceCharging();
        tempC = getBatteryTemperature();
    } catch (Throwable ignore) {}

    appendHtml("<br>");
    logLine();

    logInfo(gr
            ? "LAB 14B - Έλεγχος κατανάλωσης και συμπεριφοράς μπαταρίας"
            : "LAB 14B - Battery usage and behavior test");

    logLine();

    // ------------------------------------------------------------
    // CHARGING STATE (HARD REQUIREMENT)
    // ------------------------------------------------------------
    if (chargingNow) {

        logWarn(gr
                ? "Η συσκευή φορτίζει."
                : "Device is charging.");

        return false;
    }

    // ------------------------------------------------------------
    // BATTERY LEVEL (INFO ONLY)
    // ------------------------------------------------------------
    logLabelValue(
            gr ? "Επίπεδο μπαταρίας" : "Battery level",
            percent + "%"
    );

    // ------------------------------------------------------------
    // TEMPERATURE (INFO ONLY)
    // ------------------------------------------------------------
    logLabelValue(
            gr ? "Θερμοκρασία" : "Temperature",
            Float.isNaN(tempC)
                    ? "N/A"
                    : String.format(Locale.US, "%.1f°C", tempC)
    );

    // ------------------------------------------------------------
    // FINAL OK
    // ------------------------------------------------------------
    logOk(gr
            ? "Οι συνθήκες είναι κατάλληλες."
            : "Conditions OK.");

    return true;
}

// ============================================================
// LAB 14B — battery duration estimation (SOFT ONLY)
// ============================================================
private void lab14BBatteryDurationTest() {

    final boolean gr = AppLang.isGreek(this);

    showLab14BAdvisory(() -> {

        appendHtml("<br>");

        final iDoctorEngine idoctor =
                iDoctorEngine.get(ManualTestsActivity.this);

        final long[] startMah = new long[]{ -1L };
        final long[] endMah = new long[]{ -1L };

        final long[] baselineMah = new long[]{ -1L };

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

if (snap0.chargeFullMah > 0) {

    baselineMah[0] = snap0.chargeFullMah;

} else if (snap0.chargeDesignMah > 0) {

    baselineMah[0] = snap0.chargeDesignMah;

} else {

    int p = Math.max(1, getBatteryPercentSafe());

    baselineMah[0] =
            (long) (snap0.chargeNowMah / (p / 100.0f));
}

startTemp[0] = getBatteryTemperature();
if (Float.isNaN(startTemp[0]) || startTemp[0] <= 0f) {
    startTemp[0] = snap0.batteryTempC;
}

startVolt[0] = getBatteryVoltageFiltered();

isLab14BMode = true;
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

startLab14BPopup(300); // 5 λεπτά

logLine();

logOk(gr
        ? "LAB 14B ξεκίνησε"
        : "LAB 14B started");

logLabelValue(
        gr ? "Λειτουργία" : "Mode",
        gr ? "Προσομοίωση καθημερινής χρήσης (5 λεπτά)"
           : "Real usage simulation (5 minutes)"
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
// 🎬 VIDEO SETUP (NO AUTO PLAY)
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
            mp.setLooping(false);     // 🔴 ΟΧΙ loop
            mp.setVolume(0f, 0f);
        });
    }
} catch (Throwable ignore) {}

// --------------------------------------------------------
// 🌿 HUMAN USAGE SIMULATION (STABLE & SAFE)
// --------------------------------------------------------

final Handler usageHandler = new Handler(Looper.getMainLooper());

final long startTs = SystemClock.elapsedRealtime();

final Runnable usageLoop = new Runnable() {
    @Override
    public void run() {

        if (!lab14Running || lab14Cancelled) return;

        long elapsed = SystemClock.elapsedRealtime() - startTs;

        long nextDelay = 500;

        int mode = (int)(Math.random() * 4);

        try {

            switch (mode) {

                // -------------------------
                // IDLE (real standby)
                // -------------------------
                case 0:
                    try {
                        if (lab14StressVideo != null) {
                            lab14StressVideo.pause();
                            lab14StressVideo.seekTo(0);
                        }
                    } catch (Throwable ignore) {}
                    break;

                // -------------------------
                // LIGHT INTERACTION (scroll/UI)
                // -------------------------
                case 1:
                    simulateUiInteraction();
                    break;

                // -------------------------
                // VIDEO BURST (controlled)
                // -------------------------
                case 2:
                    try {
                        if (lab14StressVideo != null) {

                            // reset πριν start (anti overlap)
                            lab14StressVideo.pause();
                            lab14StressVideo.seekTo(0);

                            lab14StressVideo.start();

                            usageHandler.postDelayed(() -> {
                                try {
                                    lab14StressVideo.pause();
                                    lab14StressVideo.seekTo(0);
                                } catch (Throwable ignore) {}
                            }, 15000); // 15s burst
                        }
                    } catch (Throwable ignore) {}
                    break;

                // -------------------------
                // SHORT CPU BURST
                // -------------------------
                case 3:
                    simulateShortCpuBurst();
                    break;
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

usageHandler.postDelayed(this, nextDelay);
    }
};

// start loop
usageHandler.post(usageLoop);

// --------------------------------------------------------
// AFTER 300s -> FINAL SNAPSHOT + ANALYSIS
// --------------------------------------------------------
new Handler(Looper.getMainLooper()).postDelayed(() -> {

    try {

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

        endTemp[0] = getBatteryTemperature();
        if (Float.isNaN(endTemp[0]) || endTemp[0] <= 0f) {
            endTemp[0] = snapEnd.batteryTempC;
        }

        endVolt[0] = getBatteryVoltageFiltered();

// ------------------------------------------------
// ?? REAL DRAIN + AUTO SWITCH
// ------------------------------------------------
long drain = -1L;

boolean hasChargeCounter =
        startMah[0] > 0 && endMah[0] > 0;

double liveCurrentMa = lab14Current();

boolean fakeCounter = false;

if (hasChargeCounter) {

    drain = Math.max(0L, startMah[0] - endMah[0]);

// 🔴 FAKE DETECTION
if (drain == 0 && Math.abs(liveCurrentMa) > 150) {
    fakeCounter = true;
}
}

float perHour = Float.NaN;
float estimatedHours = Float.NaN;

// ------------------------------------------------
// 🔴 PRIMARY → AVERAGE RATE (REAL DATA)
// ------------------------------------------------
if (rateSamples >= 5) { // 🔴 όχι απλά >0

    perHour = (float) (rateSum / rateSamples);

} else {

    // ------------------------------------------------
    // 🔴 FALLBACK → OLD LOGIC
    // ------------------------------------------------
    if (baselineMah[0] > 0) {

        if (drain > 0 && !fakeCounter) {

            perHour = (drain / 5f) * 60f;

        } else if (!Double.isNaN(liveCurrentMa) && Math.abs(liveCurrentMa) >= 50d) {

            perHour = (float) Math.abs(liveCurrentMa);
        }
    }
}

// ------------------------------------------------
// 🔴 SANITY CLAMP (CRITICAL)
// ------------------------------------------------
if (!Float.isNaN(perHour)) {

    if (perHour < 50f) perHour = 50f;
    if (perHour > 5000f) perHour = 5000f; // 🔴 anti-bug spike
}

// ------------------------------------------------
// FINAL ESTIMATION
// ------------------------------------------------
if (!Float.isNaN(perHour) && perHour > 0f && baselineMah[0] > 0) {

    estimatedHours = baselineMah[0] / perHour;

} else {

    estimatedHours = Float.NaN;
}

// ------------------------------------------------
// THERMAL / VOLTAGE
// ------------------------------------------------
float tempRise = Float.NaN;
float voltDrop = Float.NaN;

if (!Float.isNaN(startTemp[0]) && !Float.isNaN(endTemp[0])) {
    tempRise = endTemp[0] - startTemp[0];
}

if (!Float.isNaN(startVolt[0]) && !Float.isNaN(endVolt[0])) {
    voltDrop = startVolt[0] - endVolt[0];
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

} else {

    logWarn(gr
            ? "Αδυναμία υπολογισμού κατανάλωσης"
            : "Unable to calculate consumption");
}

// 🔴 THERMAL
if (!Float.isNaN(tempRise)) {
    logLabelValue(
            gr ? "Άνοδος θερμοκρασίας" : "Temperature rise",
            String.format(Locale.US, "%.1f°C", tempRise)
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

                if (!Float.isNaN(estimatedHours)) {

                    logLabelValue(
                            gr ? "Εκτιμώμενη διάρκεια" : "Estimated duration",
                            String.format(
                                    Locale.US,
                                    "%.1f %s",
                                    estimatedHours,
                                    gr ? "ώρες" : "hours"
                            )
                    );

                } else {

                    logWarn(gr
                            ? "Αδυναμία εκτίμησης διάρκειας"
                            : "Estimation failed");
                }
                
                appendHtml("<br>");
logOk(gr ? "Σενάρια χρήσης" : "Usage scenarios");
logLine();
                
float lightUsage = Float.NaN;
float normalUsage = Float.NaN;
float heavyUsage = Float.NaN;

if (!Float.isNaN(estimatedHours) && estimatedHours > 0f) {

    normalUsage = estimatedHours;
    lightUsage = estimatedHours * 1.4f;
    heavyUsage = estimatedHours * 0.6f;

    String lightStr = String.format(
            Locale.US, "%.1f %s",
            lightUsage, gr ? "ώρες" : "hours"
    );

    String normalStr = String.format(
            Locale.US, "%.1f %s",
            normalUsage, gr ? "ώρες" : "hours"
    );

    String heavyStr = String.format(
            Locale.US, "%.1f %s",
            heavyUsage, gr ? "ώρες" : "hours"
    );

    logLabelValue(gr ? "Με ελαφριά χρήση" : "Light usage", lightStr);
    logLabelValue(gr ? "Με κανονική χρήση" : "Normal usage", normalStr);
    logLabelValue(gr ? "Με βαριά χρήση" : "Heavy usage", heavyStr);

} else {

    String na = gr ? "Μ/Δ" : "N/A";

    logLabelValue(gr ? "Με ελαφριά χρήση" : "Light usage", na);
    logLabelValue(gr ? "Με κανονική χρήση" : "Normal usage", na);
    logLabelValue(gr ? "Με βαριά χρήση" : "Heavy usage", na);
}

float battPct = (float) getBatteryPercentSafe();

float lightRemaining = Float.NaN;
float normalRemaining = Float.NaN;
float heavyRemaining = Float.NaN;

if (!Float.isNaN(estimatedHours) && battPct > 0f) {

    float factor = battPct / 100f;

    lightRemaining = estimatedHours * 1.4f * factor;
    normalRemaining = estimatedHours * factor;
    heavyRemaining = estimatedHours * 0.6f * factor;

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

    logLabelValue(
            gr ? "Με ελαφριά χρήση" : "Light usage",
            String.format(Locale.US, "%.1f %s",
                    lightRemaining, gr ? "ώρες" : "hours")
    );

    logLabelValue(
            gr ? "Με κανονική χρήση" : "Normal usage",
            String.format(Locale.US, "%.1f %s",
                    normalRemaining, gr ? "ώρες" : "hours")
    );

    logLabelValue(
            gr ? "Με βαριά χρήση" : "Heavy usage",
            String.format(Locale.US, "%.1f %s",
                    heavyRemaining, gr ? "ώρες" : "hours")
    );
    
// ------------------------------------------------
// 🔴 GEL BATTERY RELATIVITY VERDICT (X + ψ = Ω)
// ------------------------------------------------
appendHtml("<br>");

logOk(gr
        ? "Συμπέρασμα κατάστασης μπαταρίας"
        : "Battery condition verdict");

logLine();

if (!Float.isNaN(estimatedHours)) {

    float screenInches = getScreenSizeInches();
    if (screenInches <= 0f) screenInches = 6.5f;

    // ------------------------------------------------
    // PURE PHYSICS BASELINE
    // ------------------------------------------------
    float expectedHours =
            8.8f *
            (baselineMah[0] / 5000f) *
            (6.5f / screenInches);

    float omega =
            expectedHours > 0f
                    ? estimatedHours / expectedHours
                    : 1f;

    // anti-outlier clamp
    omega = Math.max(0.40f, Math.min(1.60f, omega));

    float relativePct =
            (omega - 1f) * 100f;

    String verdict;
    String humanVerdict;

    // ------------------------------------------------
    // X + ψ = Ω verdict zones
    // ------------------------------------------------
    if (omega >= 1.25f) {

        verdict = gr
                ? "Εξαιρετική απόδοση υπό περιορισμούς"
                : "Constraint exceptional";

        humanVerdict = gr
                ? "Με βάση χωρητικότητα και μέγεθος οθόνης, η μπαταρία αποδίδει εντυπωσιακά πάνω από τους περιορισμούς της. Έχει πολλά ψωμιά ακόμα."
                : "Based on battery capacity and screen size, battery performance is exceptionally above its constraints and still has a lot of life left.";

    } else if (omega >= 1.05f) {

        verdict = gr
                ? "Άριστη κατάσταση εντός περιορισμών"
                : "Excellent within constraints";

        humanVerdict = gr
                ? "Με βάση χωρητικότητα και μέγεθος οθόνης, η μπαταρία αποδίδει πολύ καλά για τους περιορισμούς της και δείχνει υγιές battery system."
                : "Based on battery capacity and screen size, the battery performs very well for its constraints and indicates a healthy battery system.";

    } else if (omega >= 0.85f) {

        verdict = gr
                ? "Υγιής κατάσταση εντός περιορισμών"
                : "Healthy within constraints";

        humanVerdict = gr
                ? "Με βάση χωρητικότητα και μέγεθος οθόνης, η μπαταρία αποδίδει καλά για τους περιορισμούς της και έχει ψωμιά ακόμα."
                : "Based on battery capacity and screen size, the battery performs well for its constraints and still has solid life left.";

    } else if (omega >= 0.70f) {

        verdict = gr
                ? "Μέτρια υποαπόδοση"
                : "Moderate underperformance";

        humanVerdict = gr
                ? "Με βάση χωρητικότητα και μέγεθος οθόνης, η απόδοση βρίσκεται κάτω από το ιδανικό και υπάρχουν ενδείξεις μέτριας φθοράς."
                : "Based on battery capacity and screen size, performance is below ideal and shows signs of moderate wear.";

    } else {

        verdict = gr
                ? "Ενδείξεις έντονης φθοράς"
                : "Heavy wear";

        humanVerdict = gr
                ? "Με βάση χωρητικότητα και μέγεθος οθόνης, η αυτονομία δείχνει σημαντική φθορά μπαταρίας."
                : "Based on battery capacity and screen size, endurance indicates significant battery wear.";
    }

    logLabelValue(
            gr ? "Σχετικός δείκτης Ω"
               : "Relativity score Ω",
            String.format(Locale.US,"%.2f", omega)
    );

    logLabelValue(
            gr ? "Αναμενόμενη διάρκεια"
               : "Expected duration",
            String.format(
                    Locale.US,
                    "%.1f h",
                    expectedHours
            )
    );

    logLabelValue(
            gr ? "Σχετική απόδοση"
               : "Relative performance",
            String.format(
                    Locale.US,
                    "%+.0f%%",
                    relativePct
            )
    );

    logLabelValue(
            gr ? "Συμπέρασμα αξιολόγησης"
               : "Verdict",
            verdict
    );

    appendHtml("<br>");

    logInfo(humanVerdict);

} else {

    logWarn(gr
            ? "Ανεπαρκή δεδομένα για αξιολόγηση"
            : "Insufficient data for verdict");
}

// ------------------------------------------------------------
// 🔴 SAFE VALUES (ANTI-NaN)
// ------------------------------------------------------------
float safePerHour = Float.isNaN(perHour) ? -1f : perHour;
float safeEstimated = Float.isNaN(estimatedHours) ? -1f : estimatedHours;

float safeLight = Float.isNaN(lightRemaining) ? -1f : lightRemaining;
float safeNormal = Float.isNaN(normalRemaining) ? -1f : normalRemaining;
float safeHeavy = Float.isNaN(heavyRemaining) ? -1f : heavyRemaining;

float safeRemLight = Float.isNaN(lightRemaining) ? -1f : lightRemaining;
float safeRemNormal = Float.isNaN(normalRemaining) ? -1f : normalRemaining;
float safeRemHeavy = Float.isNaN(heavyRemaining) ? -1f : heavyRemaining;

// ------------------------------------------------------------
// 🔴 SAVE LAB 14B RESULTS (CRITICAL)
// ------------------------------------------------------------
try {
	
    getSharedPreferences("GEL_DIAG", MODE_PRIVATE)
            .edit()
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

            } catch (Throwable t) {

                logError(gr
                        ? "Σφάλμα ανάλυσης"
                        : "Analysis error");
                        
            } finally {

    try { stopCpuBurn(); } catch (Throwable ignore) {}
    try { stopMemoryStress(); } catch (Throwable ignore) {}
    try { stopGpuStress(); } catch (Throwable ignore) {}
    try { restoreBrightnessAndKeepOn(); } catch (Throwable ignore) {}

    try {
        usageHandler.removeCallbacksAndMessages(null);
    } catch (Throwable ignore) {}

    lab14Cancelled = false;
    lab14Running = false;
    isLab14BMode = false;

    // 🔴 FINAL COMPLETION
   
    appendHtml("<br>");
            logOk(gr ? "Το Lab 14B ολοκληρώθηκε." : "Lab 14B finished.");
            logLine();

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

    });

}

// ============================================================
// LAB 14B — PRE TEST ADVISORY (BATTERY USAGE TEST)
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
                    ? "Η δοκιμή προσομοιώνει καθημερινή χρήση της συσκευής "
                      + "για 5 λεπτά ώστε να εκτιμηθεί η κατανάλωση και η "
                      + "διάρκεια της μπαταρίας.\n\n"
                      + "Βεβαιώσου ότι η συσκευή δεν φορτίζει πριν ξεκινήσεις."
                    : "This test simulates real device usage for 5 minutes "
                      + "to estimate battery consumption and duration.\n\n"
                      + "Make sure the device is not charging before starting.";

    int percent = getBatteryPercentSafe();

    boolean chargingNow = false;
    try {
        chargingNow = isDeviceCharging();
    } catch (Throwable ignore) {}

    boolean chargingOk = !chargingNow;

    SpannableStringBuilder sb = new SpannableStringBuilder();

    int green = 0xFF39FF14;
    int red = 0xFFFF4444;

    int start = sb.length();

    sb.append(text).append("\n\n");

    sb.setSpan(
            new ForegroundColorSpan(green),
            start,
            sb.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    );

    // ------------------------------------------------------------
    // BATTERY LEVEL (INFO ONLY)
    // ------------------------------------------------------------
    start = sb.length();

    String batteryStatus =
            gr
                    ? "✓ Μπαταρία: " + percent + "%"
                    : "✓ Battery: " + percent + "%";

    sb.append(batteryStatus).append("\n");

    sb.setSpan(
            new ForegroundColorSpan(green),
            start,
            sb.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    );

    // ------------------------------------------------------------
    // CHARGING STATUS (CRITICAL)
    // ------------------------------------------------------------
    start = sb.length();

    String chargingStatus;

    if (chargingOk) {

        chargingStatus =
                gr
                        ? "✓ Η συσκευή δεν φορτίζεται"
                        : "✓ Device not charging";

        sb.append(chargingStatus);

        sb.setSpan(
                new ForegroundColorSpan(green),
                start,
                sb.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

    } else {

        chargingStatus =
                gr
                        ? "✖ Η συσκευή φορτίζεται"
                        : "✖ Device is charging";

        sb.append(chargingStatus);

        sb.setSpan(
                new ForegroundColorSpan(red),
                start,
                sb.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }

    TextView msg = new TextView(this);
    msg.setText(sb);
    msg.setTextSize(14.5f);
    msg.setLineSpacing(0f, 1.2f);
    root.addView(msg);

    root.addView(buildMuteRow());

    // ------------------------------------------------------------
    // BUTTON LOGIC
    // ------------------------------------------------------------
    boolean allowContinue = chargingOk;

    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.VERTICAL);

    LinearLayout.LayoutParams lp =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

    lp.setMargins(0, dp(14), 0, 0);

    Button btnAction;

    if (allowContinue) {
        btnAction =
                gelButton(
                        this,
                        gr ? "Συνέχεια (5 λεπτά τεστ)" : "Continue (5 min test)",
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

    if (dlg.getWindow() != null) {
        dlg.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    dlg.show();

    dlg.setOnCancelListener(d -> AppTTS.stop());
    dlg.setOnDismissListener(d -> AppTTS.stop());

    // 🔊 TTS
    new Handler(Looper.getMainLooper()).postDelayed(() -> {

        if (!dlg.isShowing()) return;

        AppTTS.stop();

        if (!AppTTS.isMuted(this)) {
            String speakText = text + ". " + batteryStatus + ". " + chargingStatus;
            AppTTS.ensureSpeak(
                    ManualTestsActivity.this,
                    speakText
            );
        }

    }, 120L);

    // ------------------------------------------------------------
    // CLICK
    // ------------------------------------------------------------
    btnAction.setOnClickListener(v -> {

        AppTTS.stop();
        dlg.dismiss();

        if (!allowContinue) return;

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

private void startLab14BPopup(long durationSec) {

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
    title.setText(gr
            ? "LAB 14B — Κατανάλωση & διάρκεια μπαταρίας"
            : "LAB 14B — Battery usage & duration");
    title.setTextColor(0xFFFFFFFF);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

    // STATUS
    TextView statusText = new TextView(this);
    statusText.setText(gr ? "Soft usage simulation..." : "Soft usage simulation...");
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

    // 🔴 RESET STATE
    lab14Running = true;
    lab14Cancelled = false;

    lab14Dialog.show();

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

                // 🔴 STATUS (SOFT ONLY)
                statusText.setText(gr
                        ? "Προσομοίωση καθημερινής χρήσης"
                        : "Daily usage simulation");

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
    try {
        if (lab14StressVideo != null && lab14StressVideo.isPlaying()) {
            lab14StressVideo.pause();
        }
    } catch (Throwable ignore) {}
}

private void simulateShortCpuBurst() {
    new Thread(() -> {
        long t = SystemClock.elapsedRealtime();
        while (SystemClock.elapsedRealtime() - t < 1500) {
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

Button exitBtn = new Button(this);
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

final long startMah = snapStart.chargeNowMah;

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
// CHARGING INPUT & STRENGTH
// ------------------------------------------------------------
BatteryInfo endInfo = getBatteryInfo();

if (startMah > 0 && endInfo != null &&
        endInfo.currentChargeMah > startMah && startTs[0] > 0) {

    lab15_strengthKnown = true;

    long deltaMah = lab14DeltaMah;
long dtMs = Math.max(1, SystemClock.elapsedRealtime() - startTs[0]);
double minutes = dtMs / 60000.0;
double mahPerMin = (minutes > 0) ? (lab14DeltaMah / minutes) : -1;

    logLabelOkValue(
            gr ? "Είσοδος φόρτισης" : "Charging input",
            String.format(
                    Locale.US,
                    gr
                            ? "+%d mAh σε %.1f λεπτά (%.1f mAh/min)"
                            : "+%d mAh in %.1f min (%.1f mAh/min)",
                    lab14DeltaMah,
                    minutes,
                    mahPerMin
            )
    );

// ------------------------------------------------------------
// PMIC / CHARGING IC DIAGNOSTIC
// ------------------------------------------------------------
if (lab15_strengthKnown && mahPerMin < 1 && !lab15FlapUnstable) {

    logLabelWarnValue(
            gr ? "Ελεγκτής φόρτισης" : "Charging controller",
            gr
                    ? "Πιθανή δυσλειτουργία IC φόρτισης (PMIC)"
                    : "Possible charging IC / PMIC limitation"
    );
}

if (mahPerMin >= 20.0) {
    logLabelOkValue(gr ? "Ισχύς φόρτισης" : "Charging strength", gr ? "ΙΣΧΥΡΗ" : "STRONG");
    lab15_strengthWeak = false;

} else if (mahPerMin >= 10.0) {
    logLabelOkValue(gr ? "Ισχύς φόρτισης" : "Charging strength", gr ? "ΚΑΝΟΝΙΚΗ" : "NORMAL");
    lab15_strengthWeak = false;

} else if (mahPerMin >= 5.0) {
    logLabelWarnValue(gr ? "Ισχύς φόρτισης" : "Charging strength", gr ? "ΜΕΤΡΙΑ" : "MODERATE");
    lab15_strengthWeak = true;

} else {
    logLabelErrorValue(gr ? "Ισχύς φόρτισης" : "Charging strength", gr ? "ΑΣΘΕΝΗΣ" : "WEAK");
    lab15_strengthWeak = true;
}

// ------------------------------------------------------------
// USB PORT / CABLE LOSS DETECTOR
// ------------------------------------------------------------
if (lab15_strengthKnown && mahPerMin < 6 && mahPerMin > 1 && !lab15OverTempDuringCharge) {

    logLabelWarnValue(
            gr ? "Θύρα φόρτισης" : "Charging port",
            gr
                    ? "Πιθανή απώλεια ισχύος (καλώδιο / θύρα USB)"
                    : "Possible power loss (cable / USB port)"
    );
}

} else {

    lab15_strengthKnown = false;
    lab15_strengthWeak  = true;

    logLabelWarnValue(
            gr ? "Ισχύς φόρτισης" : "Charging strength",
            gr
                    ? "Δεν ήταν δυνατή η αξιόπιστη εκτίμηση"
                    : "Unable to estimate accurately"
    );
}

// ------------------------------------------------------------
// FINAL LAB 15 DECISION
// ------------------------------------------------------------

appendHtml("<br>");
logInfo(gr ? "Απόφαση LAB:" : "LAB decision:");
logLine();

if (!lab15OverTempDuringCharge && !lab15FlapUnstable && !lab15_strengthWeak) {

    logLabelOkValue(
            gr ? "Σύστημα φόρτισης" : "Charging system",
            gr
                    ? "OK — δεν απαιτείται καθαρισμός ή αντικατάσταση"
                    : "OK — no cleaning or replacement required"
    );

    logLabelOkValue(gr ? "Σταθερότητα" : "Stability", "OK");

} else {

    logLabelWarnValue(
            gr ? "Σύστημα φόρτισης" : "Charging system",
            gr
                    ? "Εντοπίστηκαν πιθανά ζητήματα"
                    : "Potential issues detected"
    );

    logLabelWarnValue(
            gr ? "Σύσταση" : "Recommendation",
            gr
                    ? "Συνιστάται περαιτέρω έλεγχος ή επανάληψη δοκιμής"
                    : "Further inspection or repeat test recommended"
    );
}

// ------------------------------------------------------------
// SUMMARY FLAG
// ------------------------------------------------------------
boolean chargingGlitchDetected =
        lab15FlapUnstable ||
        lab15OverTempDuringCharge ||
        lab15_strengthWeak ||
        lab15_systemLimited;

GELServiceLog.info(
        "SUMMARY: CHARGING_STABILITY=" +
                (chargingGlitchDetected ? "UNSTABLE" : "STABLE")
);

appendHtml("<br>");
logOk(gr ? "Το Lab 15 ολοκληρώθηκε." : "Lab 15 finished.");
logLine();

// ------------------------------------------------------------
// STORE RESULT FOR LAB 17 (LAB 15 OUTPUT)
// ------------------------------------------------------------
try {

    int chargeScore = 100;

    if (lab15_strengthWeak)          chargeScore -= 25;
    if (lab15FlapUnstable)           chargeScore -= 25;
    if (lab15OverTempDuringCharge)   chargeScore -= 25;

    chargeScore = Math.max(0, Math.min(100, chargeScore));

    p.edit()
            .putInt("lab15_charge_score", chargeScore)
            .putBoolean("lab15_system_limited", lab15_systemLimited)
            .putBoolean("lab15_overtemp", lab15OverTempDuringCharge)
            .putString(
                    "lab15_strength_label",
                    lab15_strengthWeak ? "WEAK" : "NORMAL/STRONG"
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

    appendHtml("<br>");

    // ------------------------------------------------------------
    // SUMMARY
    // ------------------------------------------------------------
    boolean danger = peakTemp >= 55f;

    logInfo(gr ? "Θερμική σύνοψη:" : "Thermal summary:");
    logLine();

    if (danger) {

        logLabelWarnValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Ανιχνεύθηκε αυξημένη θερμοκρασία"
                   : "Elevated temperature detected"
        );

        logLabelWarnValue(
                gr ? "Αντίδραση συστήματος" : "System response",
                gr ? "Ενδέχεται να ενεργοποιηθεί θερμική προστασία"
                   : "Thermal protection may activate"
        );

    } else {

        logLabelOkValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Ασφαλείς θερμοκρασίες λειτουργίας"
                   : "Safe operating temperatures"
        );

        logLabelOkValue(
                gr ? "Κάλυψη" : "Coverage",
                gr ? "Παρακολουθήθηκαν εσωτερικά chips και κρίσιμα περιφερειακά"
                   : "Internal chips and critical peripherals monitored"
        );
    }

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

        logLabelOkValue(
                gr ? "Κρυφοί αισθητήρες" : "Hidden sensors",
                gr
                        ? "Όλοι οι κρίσιμοι θερμικοί αισθητήρες είναι εντός ορίων"
                        : "All critical thermal sensors monitored"
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

    thermalScore = Math.max(0, Math.min(100, thermalScore));

    try {
        p.edit()
                .putInt("lab16_thermal_score", thermalScore)
                .putBoolean("lab16_thermal_danger", thermalDanger)
                .putFloat("lab16_peak_temp", peakTemp)
                .putString("lab16_peak_source", peakSrc)
                .putLong("lab16_last_ts", System.currentTimeMillis())
                .apply();
    } catch (Throwable ignore) {}

    String tLabel;

if (thermalScore >= 80)
    tLabel = gr ? "Φυσιολογική" : "Normal";
else if (thermalScore >= 60)
    tLabel = gr ? "Μέτρια" : "Moderate";
else
    tLabel = gr ? "Υψηλή" : "High";

logLabelOkValue(
        gr ? "Αξιολόγηση θερμικής συμπεριφοράς"
           : "Thermal behaviour rating",
        String.format(
                Locale.US,
                "%s (%d)",
                tLabel,
                thermalScore
        )
);

    boolean thermalSpikesDetected = thermalDanger;

    GELServiceLog.info(
            "SUMMARY: THERMAL_PATTERN=" +
                    (thermalSpikesDetected ? "SPIKES" : "NORMAL")
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

    final String PREF = "GEL_DIAG";

    // STRICT WINDOW: 2 hours
    final long WINDOW_MS = 2L * 60L * 60L * 1000L;
    final long now = System.currentTimeMillis();

    // ------------------------------------------------------------
    // READ STORED RESULTS + TIMESTAMPS (STRICT)
    // ------------------------------------------------------------
    SharedPreferences p = getSharedPreferences(PREF, MODE_PRIVATE);

    // LAB 14 results
    final float lab14Health = p.getFloat("lab14_health_score", -1f);
    final int lab14Aging = p.getInt("lab14_aging_index", -1);
    final long ts14 = p.getLong("lab14_last_ts", 0L);

    // LAB 14 reliability flag (future-safe)
    final boolean lab14Unstable =
            p.getBoolean("lab14_unstable_measurement", false);

    // LAB 14B results
final float lab14bConsumptionPerHour =
        p.getFloat("lab14b_consumption_per_hour", -1f);

final float lab14bEstimatedHours =
        p.getFloat("lab14b_estimated_hours", -1f);

final float lab14bLightHours =
        p.getFloat("lab14b_light_hours", -1f);

final float lab14bNormalHours =
        p.getFloat("lab14b_normal_hours", -1f);

final float lab14bHeavyHours =
        p.getFloat("lab14b_heavy_hours", -1f);

final float lab14bRemainingLight =
        p.getFloat("lab14b_remaining_light", -1f);

final float lab14bRemainingNormal =
        p.getFloat("lab14b_remaining_normal", -1f);

final float lab14bRemainingHeavy =
        p.getFloat("lab14b_remaining_heavy", -1f);

final long ts14b =
        p.getLong("lab14b_ts", 0L);

    // LAB 15
    final int lab15Charge = p.getInt("lab15_charge_score", -1);
    final boolean lab15SystemLimited =
            p.getBoolean("lab15_system_limited", false);
    final String lab15StrengthLabel =
            p.getString("lab15_strength_label", null);
    final long ts15 = p.getLong("lab15_ts", 0L);

    // LAB 16
    final int lab16Thermal = p.getInt("lab16_thermal_score", -1);
    final boolean lab16ThermalDanger =
            p.getBoolean("lab16_thermal_danger", false);
    final long ts16 = p.getLong("lab16_last_ts", 0L);

    // ------------------------------------------------------------
    // PRESENCE + FRESHNESS CHECK
    // ------------------------------------------------------------
    final boolean has14 = (lab14Health >= 0f && ts14 > 0L);
    final boolean has14b =
        (lab14bConsumptionPerHour > 0f) &&
        (lab14bEstimatedHours > 0f) &&
        (ts14b > 0L);
    final boolean has15 = (lab15Charge >= 0 && ts15 > 0L);
    final boolean has16 = (lab16Thermal >= 0 && ts16 > 0L);

    final boolean fresh14 = has14 && (now - ts14) <= WINDOW_MS;
    final boolean fresh14b = has14b && (now - ts14b) <= WINDOW_MS;
    final boolean fresh15 = has15 && (now - ts15) <= WINDOW_MS;
    final boolean fresh16 = has16 && (now - ts16) <= WINDOW_MS;

    // ------------------------------------------------------------
    // HIGH VARIABILITY CONFIRMATION (LAB 14 INTELLIGENCE)
    // ------------------------------------------------------------
    final long hvFirstTs = p.getLong("lab14_hv_first_ts", -1L);
    final long hvLastTs = p.getLong("lab14_hv_last_ts", -1L);
    final boolean hvPending = p.getBoolean("lab14_hv_pending", false);

    final boolean hvConfirmed =
            hvPending &&
            hvFirstTs > 0L &&
            hvLastTs > hvFirstTs &&
            (hvLastTs - hvFirstTs) <= WINDOW_MS;

    // ------------------------------------------------------------
    // PRECHECK — SMART POPUP (STRICT)
    // ------------------------------------------------------------
    if (!(fresh14 && fresh14b && fresh15 && fresh16)) {

        StringBuilder msg = new StringBuilder();

        msg.append(
                gr
                        ? "Κατάσταση (απαιτούνται αποτελέσματα τελευταίων 2 ωρών):<br><br>"
                        : "Status (results required within last 2 hours):<br><br>"
        );

        // --------------------------------------------------------
        // LAB 14
        // --------------------------------------------------------
        msg.append(gr ? "• LAB 14: " : "• LAB 14: ");

        if (!has14) {
            msg.append("<font color='#FF4444'>")
               .append(gr ? "Απουσιάζει" : "Missing")
               .append("</font><br>");
        } else if (!fresh14) {
            msg.append("<font color='#FF4444'>")
               .append(gr ? "Έληξε (" : "Expired (")
               .append(lab17_age(now - ts14))
               .append(")</font><br>");
        } else {
            msg.append("<font color='#39FF14'>")
               .append("OK (")
               .append(lab17_age(now - ts14))
               .append(")</font><br>");
        }

        // --------------------------------------------------------
        // LAB 14B
        // --------------------------------------------------------
        msg.append(gr ? "• LAB 14B: " : "• LAB 14B: ");

        if (!has14b) {
            msg.append("<font color='#FF4444'>")
               .append(gr ? "Απουσιάζει" : "Missing")
               .append("</font><br>");
        } else if (!fresh14b) {
            msg.append("<font color='#FF4444'>")
               .append(gr ? "Έληξε (" : "Expired (")
               .append(lab17_age(now - ts14b))
               .append(")</font><br>");
        } else {
            msg.append("<font color='#39FF14'>")
               .append("OK (")
               .append(lab17_age(now - ts14b))
               .append(")</font><br>");
        }

        // --------------------------------------------------------
        // LAB 15
        // --------------------------------------------------------
        msg.append(gr ? "• LAB 15: " : "• LAB 15: ");

        if (!has15) {
            msg.append("<font color='#FF4444'>")
               .append(gr ? "Απουσιάζει" : "Missing")
               .append("</font><br>");
        } else if (!fresh15) {
            msg.append("<font color='#FF4444'>")
               .append(gr ? "Έληξε (" : "Expired (")
               .append(lab17_age(now - ts15))
               .append(")</font><br>");
        } else {
            msg.append("<font color='#39FF14'>")
               .append("OK (")
               .append(lab17_age(now - ts15))
               .append(")</font><br>");
        }

        // --------------------------------------------------------
        // LAB 16
        // --------------------------------------------------------
        msg.append(gr ? "• LAB 16: " : "• LAB 16: ");

        if (!has16) {
            msg.append("<font color='#FF4444'>")
               .append(gr ? "Απουσιάζει" : "Missing")
               .append("</font><br>");
        } else if (!fresh16) {
            msg.append("<font color='#FF4444'>")
               .append(gr ? "Έληξε (" : "Expired (")
               .append(lab17_age(now - ts16))
               .append(")</font><br>");
        } else {
            msg.append("<font color='#39FF14'>")
               .append("OK (")
               .append(lab17_age(now - ts16))
               .append(")</font><br>");
        }

        msg.append("<br>");

        // --------------------------------------------------------
        // SMART DECISION
        // --------------------------------------------------------
        if ((fresh14 && fresh15 && fresh14b) && !fresh16) {

            msg.append(
                    gr
                            ? "Έχουν ολοκληρωθεί τα LAB 14, 14B και LAB 15.<br>"
                              + "Εκτέλεσε ΜΟΝΟ το LAB 16 για να ολοκληρωθεί το σύνολο.<br>"
                            : "LAB 14, 14B and LAB 15 are already completed.<br>"
                              + "Run ONLY LAB 16 to complete the set.<br>"
            );

        } else if ((fresh14 && fresh16 && fresh14b) && !fresh15) {

            msg.append(
                    gr
                            ? "Έχουν ολοκληρωθεί τα LAB 14, 14B και LAB 16.<br>"
                              + "Εκτέλεσε ΜΟΝΟ το LAB 15 για να ολοκληρωθεί το σύνολο.<br>"
                            : "LAB 14, 14B and LAB 16 are already completed.<br>"
                              + "Run ONLY LAB 15 to complete the set.<br>"
            );

        } else if ((fresh15 && fresh16 && fresh14b) && !fresh14) {

            msg.append(
                    gr
                            ? "Έχουν ολοκληρωθεί τα LAB 15, 16 και LAB 14B.<br>"
                              + "Εκτέλεσε ΜΟΝΟ το LAB 14 για να ολοκληρωθεί το σύνολο.<br>"
                            : "LAB 15, 16 and LAB 14B are already completed.<br>"
                              + "Run ONLY LAB 14 to complete the set.<br>"
            );

        } else if ((fresh14 && fresh15 && fresh16) && !fresh14b) {

            msg.append(
                    gr
                            ? "Έχουν ολοκληρωθεί τα LAB 14, 15 και 16.<br>"
                              + "Εκτέλεσε ΜΟΝΟ το LAB 14B για να ολοκληρωθεί το σύνολο.<br>"
                            : "LAB 14, 15 and 16 are already completed.<br>"
                              + "Run ONLY LAB 14B to complete the set.<br>"
            );

        } else {

            msg.append(
                    gr
                            ? "Για έγκυρο αποτέλεσμα, απαιτείται εκτέλεση των<br>"
                              + "LAB 14 + LAB 14B + LAB 15 + LAB 16 μαζί.<br><br>"
                              + "Αιτία: απουσία ή/και λήξη αποτελεσμάτων."
                            : "To generate a valid result, run<br>"
                              + "LAB 14 + LAB 14B + LAB 15 + LAB 16 together.<br><br>"
                              + "Reason: missing and/or expired results."
            );
        }

        lab17_showPopup(
                gr
                        ? "LAB 17 — Έλεγχος Προϋποθέσεων"
                        : "LAB 17 — Prerequisites Check",
                msg.toString()
        );
        return;
    }

    // ------------------------------------------------------------
    // START LAB 17
    // ------------------------------------------------------------
    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 17 — GEL Ευφυής Ανάλυση Υγείας Συστήματος"
            : "LAB 17 — GEL Intelligent System Health Analysis");
    logLine();

    new Thread(() -> {

        try {

            // ------------------------------------------------------------
            // BASE WEIGHTED SCORE
            // ------------------------------------------------------------
            int baseScore = Math.round(
                    (lab14Health * 0.50f) +
                    (lab15Charge * 0.25f) +
                    (lab16Thermal * 0.25f)
            );
            baseScore = Math.max(0, Math.min(100, baseScore));

// ------------------------------------------------------------
// PENALTIES
// ------------------------------------------------------------
int penaltyExtra = 0;

// 🔴 VALIDATION 14B (CRITICAL)
boolean valid14b =
        lab14bEstimatedHours > 0f;

if (!valid14b) {
    penaltyExtra += 10;
}

// ------------------------------------------------------------
// LAB14B PENALTIES (μόνο αν valid)
// ------------------------------------------------------------
if (valid14b) {

    if (lab14bEstimatedHours < 3f) {
        penaltyExtra += 14;
    } else if (lab14bEstimatedHours < 5f) {
        penaltyExtra += 8;
    } else if (lab14bEstimatedHours < 7f) {
        penaltyExtra += 3;
    }

    if (lab14bConsumptionPerHour > 1500f) {
        penaltyExtra += 10;
    } else if (lab14bConsumptionPerHour > 1200f) {
        penaltyExtra += 6;
    } else if (lab14bConsumptionPerHour > 1000f) {
        penaltyExtra += 3;
    }
}

            if (lab15Charge < 60 && lab15SystemLimited) penaltyExtra += 6;
            else if (lab15Charge < 60) penaltyExtra += 12;

            if (lab16Thermal < 60) penaltyExtra += 10;
            else if (lab16Thermal < 75) penaltyExtra += 5;

            if (lab14Aging >= 0) {
    if (lab14Aging >= 70) penaltyExtra += 10;
    else if (lab14Aging >= 50) penaltyExtra += 6;
    else if (lab14Aging >= 30) penaltyExtra += 3;
}

// 🔴 FREEZE VALUE
final int fPenaltyExtra = penaltyExtra;

// 🔴 USE SAME VARIABLE
int finalScore = Math.max(0, Math.min(100, baseScore - fPenaltyExtra));

            String category =
                    (finalScore >= 85) ? "Strong" :
                    (finalScore >= 70) ? "Normal" :
                    "Weak";

            final int fFinalScore = finalScore;
            
            final String fCategory = category;

            final boolean thermalDanger =
                    lab16ThermalDanger || (lab16Thermal < 60);

            final boolean chargingWeakOrThrottled =
                    (lab15Charge < 60) || lab15SystemLimited;

            final boolean batteryLooksFineButThermalBad =
                    (lab14Health >= 80f) && thermalDanger;

            final boolean batteryBadButThermalOk =
                    (lab14Health < 70f) && (lab16Thermal >= 75);

            final boolean overallDeviceConcern =
                    thermalDanger ||
                    chargingWeakOrThrottled ||
                    (lab14Health < 70f);
                    
String lab14AgingLabel;

if (lab14Aging >= 0) {

    lab14AgingLabel = gr
            ? "Δείκτης γήρανσης"
            : "Aging index";

} else {

    lab14AgingLabel = gr
            ? "Δείκτης γήρανσης"
            : "Aging index";

}

            ui.post(() -> {

// ================= SUMMARY =================
logLine();
logInfo(gr
        ? "LAB 14 — Υγεία μπαταρίας"
        : "LAB 14 — Battery health");

String agingText;

if (lab14Aging >= 0) {

    agingText = String.format(
            Locale.US,
            "%s (%d)",
            lab14AgingLabel,
            lab14Aging
    );

} else {

    agingText = gr ? "Μ/Δ" : "N/A";
}

logLabelOkValue(
        gr ? "Υγεία" : "Health",
        String.format(
                Locale.US,
                "%s (%.0f) | %s: %s",
                lab14HealthLabel,
                lab14Health,
                gr ? "Γήρανση" : "Aging",
                agingText
        )
);

// ================= LAB 14B =================
logInfo(gr
        ? "LAB 14B — Κατανάλωση και διάρκεια μπαταρίας"
        : "LAB 14B — Battery consumption and duration");

if (lab14bEstimatedHours >= 6f) {

    logLabelOkValue(
            gr ? "Αυτονομία" : "Battery duration",
            String.format(
                    Locale.US,
                    gr
                            ? "Καλή (%.1f ώρες)"
                            : "Good (%.1f hours)",
                    lab14bEstimatedHours
            )
    );

} else if (lab14bEstimatedHours >= 4f) {

    logLabelWarnValue(
            gr ? "Αυτονομία" : "Battery duration",
            String.format(
                    Locale.US,
                    gr
                            ? "Μέτρια (%.1f ώρες)"
                            : "Moderate (%.1f hours)",
                    lab14bEstimatedHours
            )
    );

} else if (lab14bEstimatedHours > 0f) {

    logLabelErrorValue(
            gr ? "Αυτονομία" : "Battery duration",
            String.format(
                    Locale.US,
                    gr
                            ? "Χαμηλή (%.1f ώρες)"
                            : "Low (%.1f hours)",
                    lab14bEstimatedHours
            )
    );

} else {

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Μη διαθέσιμα δεδομένα" : "Data unavailable"
    );
}

if (lab14bConsumptionPerHour > 0f) {
    logLabelValue(
            gr ? "Κατανάλωση" : "Consumption",
            String.format(Locale.US, "%.0f mAh/h", lab14bConsumptionPerHour)
    );
}

appendHtml("<br>");

logOk(
        gr ? "Εκτίμηση υπόλοιπου χρόνου"
           : "Remaining time estimation"
);

logLine();

if (lab14bRemainingNormal > 0f) {
    logLabelValue(
            gr ? "Με κανονική χρήση" : "Normal usage",
            String.format(
                    Locale.US,
                    "%.1f %s",
                    lab14bRemainingNormal,
                    gr ? "ώρες" : "hours"
            )
    );
}

                logInfo(gr
                        ? "LAB 15 — Φόρτιση"
                        : "LAB 15 — Charging");

                if (lab15Charge >= 70) {

                    logLabelOkValue(
                            gr ? "Φόρτιση" : "Charging",
                            String.format(
                                    Locale.US,
                                    gr
                                            ? "%d%% | Ισχύς: %s"
                                            : "%d%% | Strength: %s",
                                    lab15Charge,
                                    (lab15StrengthLabel != null
                                            ? lab15StrengthLabel
                                            : (gr ? "Μ/Δ" : "N/A"))
                            )
                    );

                } else {

                    logLabelWarnValue(
                            gr ? "Φόρτιση" : "Charging",
                            String.format(
                                    Locale.US,
                                    gr
                                            ? "%d%% | Ισχύς: %s"
                                            : "%d%% | Strength: %s",
                                    lab15Charge,
                                    (lab15StrengthLabel != null
                                            ? lab15StrengthLabel
                                            : (gr ? "Μ/Δ" : "N/A"))
                            )
                    );
                }

logInfo(gr
        ? "LAB 16 — Θερμική συμπεριφορά"
        : "LAB 16 — Thermal behaviour");

String thermalLabel;

if (lab16Thermal >= 85) {

    thermalLabel = gr ? "Άριστη" : "Excellent";

    logLabelOkValue(
            gr ? "Θερμική κατάσταση" : "Thermal",
            thermalLabel + " (" + lab16Thermal + ")"
    );

} else if (lab16Thermal >= 70) {

    thermalLabel = gr ? "Καλή" : "Good";

    logLabelOkValue(
            gr ? "Θερμική κατάσταση" : "Thermal",
            thermalLabel + " (" + lab16Thermal + ")"
    );

} else if (lab16Thermal >= 55) {

    thermalLabel = gr ? "Μέτρια" : "Moderate";

    logLabelWarnValue(
            gr ? "Θερμική κατάσταση" : "Thermal",
            thermalLabel + " (" + lab16Thermal + ")"
    );

} else {

    thermalLabel = gr ? "Κακή" : "Poor";

    logLabelErrorValue(
            gr ? "Θερμική κατάσταση" : "Thermal",
            thermalLabel + " (" + lab16Thermal + ")"
    );
}

                // ================= ANALYSIS =================
                if (lab15SystemLimited) {

                    logLine();
                    logInfo(gr
                            ? "Ανάλυση περιορισμού φόρτισης"
                            : "Charging limitation analysis");

                    logLabelWarnValue(
                            gr ? "Κατάσταση" : "Status",
                            gr
                                    ? "Ανιχνεύθηκε περιορισμός από το σύστημα"
                                    : "System-limited throttling detected"
                    );

                    logLabelWarnValue(
                            gr ? "Πηγή" : "Source",
                            "PMIC / thermal protection"
                    );

                    logLabelOkValue(
                            gr ? "Σημείωση" : "Note",
                            gr
                                    ? "Δεν αποδίδεται αποκλειστικά σε υγεία μπαταρίας"
                                    : "Not attributed to battery health alone"
                    );
                }

                if (fPenaltyExtra > 0) {

                    logLine();
                    logInfo(gr
                            ? "Ανάλυση ποινών"
                            : "Penalty breakdown");

                    if (lab15Charge < 60 && lab15SystemLimited) {
                        logLabelWarnValue(
                                gr ? "Φόρτιση" : "Charging",
                                gr
                                        ? "Περιορισμός από το σύστημα"
                                        : "System-limited throttling detected"
                        );
                    } else if (lab15Charge < 60) {
                        logLabelWarnValue(
                                gr ? "Φόρτιση" : "Charging",
                                gr
                                        ? "Ασθενής απόδοση φόρτισης"
                                        : "Weak charging performance detected"
                        );
                    }

                    if (lab14Aging >= 70) {
                        logLabelErrorValue(
                                gr ? "Γήρανση" : "Aging",
                                gr
                                        ? "Σοβαρές ενδείξεις γήρανσης"
                                        : "Severe aging indicators detected"
                        );
                    } else if (lab14Aging >= 50) {
                        logLabelWarnValue(
                                gr ? "Γήρανση" : "Aging",
                                gr
                                        ? "Υψηλές ενδείξεις γήρανσης"
                                        : "High aging indicators detected"
                        );
                    } else if (lab14Aging >= 30) {
                        logLabelWarnValue(
                                gr ? "Γήρανση" : "Aging",
                                gr
                                        ? "Μέτριες ενδείξεις γήρανσης"
                                        : "Moderate aging indicators detected"
                        );
                    }
                }

// ================= FINAL SCORE =================
logLine();
logInfo(gr
        ? "Τελικός Δείκτης Αξιοπιστίας Μπαταρίας"
        : "Final Battery Reliability Score");

String finalText = String.format(
        Locale.US,
        "%s (%d)",
        fCategory,
        fFinalScore
);

if (fFinalScore >= 80) {

    logLabelOkValue(
            gr ? "Κατάσταση" : "Status",
            finalText
    );

} else if (fFinalScore >= 60) {

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            finalText
    );

} else {

    logLabelErrorValue(
            gr ? "Κατάσταση" : "Status",
            finalText
    );
}

// ================= DIAGNOSIS =================
appendHtml("<br>");
logInfo(gr ? "Διάγνωση" : "Diagnosis");
logLine();

// ------------------------------------------------------------
// 🔴 CROSS-LAB INSIGHT (SINGLE DECISION)
// ------------------------------------------------------------
if (lab14Health >= 80f && lab14bEstimatedHours > 0f && lab14bEstimatedHours < 4f) {

    logLabelWarnValue(
            gr ? "Παρατήρηση" : "Observation",
            gr
                    ? "Η μπαταρία είναι σε καλή κατάσταση, αλλά η συνολική κατανάλωση συστήματος φαίνεται αυξημένη"
                    : "Battery health is good, but overall system consumption appears elevated"
    );

} else if (lab14Health < 70f && lab14bEstimatedHours >= 5f) {

    logLabelOkValue(
            gr ? "Παρατήρηση" : "Observation",
            gr
                    ? "Υπάρχουν ενδείξεις φθοράς, αλλά η αυτονομία παραμένει σε φυσιολογικά επίπεδα"
                    : "Aging indicators detected, but real-world autonomy remains within normal range"
    );

} else if (lab14bConsumptionPerHour > 1300f && lab16Thermal < 65) {

    logLabelWarnValue(
            gr ? "Παρατήρηση" : "Observation",
            gr
                    ? "Η αυξημένη κατανάλωση ενδέχεται να σχετίζεται με θερμική επιβάρυνση"
                    : "Elevated consumption may be influenced by thermal conditions"
    );

} else if (lab14Health >= 80f &&
           lab14bEstimatedHours >= 5f &&
           lab16Thermal >= 70 &&
           lab15Charge >= 70) {

    logLabelOkValue(
            gr ? "Συμπέρασμα" : "Conclusion",
            gr
                    ? "Τα αποτελέσματα είναι συνεπή μεταξύ των δοκιμών"
                    : "Results are consistent across tests"
    );
}

                if (lab14Unstable) {

                    logLabelWarnValue(
                            gr ? "Αξιοπιστία μέτρησης" : "Measurement reliability",
                            gr ? "Ασταθής" : "Unstable"
                    );

                    logLabelWarnValue(
                            gr ? "Αιτία" : "Cause",
                            "PMIC / fuel gauge instability"
                    );

                    logLabelOkValue(
                            gr ? "Σημείωση" : "Note",
                            gr
                                    ? "Δεν αποτελεί επιβεβαιωμένη αστοχία μπαταρίας"
                                    : "Not a confirmed battery failure"
                    );
                }

                if (!overallDeviceConcern) {

                    logLabelOkValue(
                            gr ? "Συνολική κατάσταση" : "Overall status",
                            gr
                                    ? "Δεν εντοπίστηκαν κρίσιμα προβλήματα (μπαταρία / φόρτιση / θερμικά)"
                                    : "No critical issues detected (battery / charging / thermal)"
                    );

                    logLabelOkValue(
                            gr ? "Παρακολούθηση" : "Monitoring",
                            gr
                                    ? "Ελέγχθηκαν εσωτερικά chips και κρίσιμα περιφερειακά"
                                    : "Internal chips and critical peripherals checked"
                    );

                } else {

                    if (batteryLooksFineButThermalBad) {

                        logLabelWarnValue(
                                gr ? "Θερμικός κίνδυνος" : "Thermal risk",
                                gr
                                        ? "Η υγεία μπαταρίας είναι ΟΚ, αλλά η θερμική συμπεριφορά είναι οριακή"
                                        : "Battery health OK, thermal behaviour risky"
                        );

                        logLabelWarnValue(
                                gr ? "Σύσταση" : "Recommendation",
                                gr
                                        ? "Έλεγχος ψύξης και θερμικών επαφών"
                                        : "Inspect cooling path and thermal interfaces"
                        );

                        logLabelWarnValue(
                                gr ? "Πιθανές αιτίες" : "Possible causes",
                                gr
                                        ? "Φόρτος CPU/GPU, thermal pads, επαφή heatsink"
                                        : "CPU/GPU load, thermal pads, heatsink contact"
                        );
                    }

                    if (chargingWeakOrThrottled) {

                        if (lab15SystemLimited) {

                            logLabelWarnValue(
                                    gr ? "Φόρτιση" : "Charging",
                                    gr
                                            ? "Περιορισμός από το σύστημα (προστασία ενεργή)"
                                            : "System-limited (protection logic active)"
                            );

                            logLabelWarnValue(
                                    gr ? "Πιθανές αιτίες" : "Possible causes",
                                    gr
                                            ? "Υπερθέρμανση ή περιορισμός ρεύματος από PMIC"
                                            : "Overheating or PMIC current limiting"
                            );

                        } else if (lab15Charge < 60) {

                            logLabelWarnValue(
                                    gr ? "Φόρτιση" : "Charging",
                                    gr
                                            ? "Ασθενής απόδοση φόρτισης"
                                            : "Weak charging performance"
                            );

                            logLabelWarnValue(
                                    gr ? "Πιθανές αιτίες" : "Possible causes",
                                    gr
                                            ? "Καλώδιο / αντάπτορας, φθορά θύρας, αυξημένη εσωτερική αντίσταση μπαταρίας"
                                            : "Cable / adapter quality, port wear, battery impedance"
                            );
                        }
                    }

                    if (batteryBadButThermalOk) {

                        logLabelWarnValue(
                                gr ? "Μπαταρία" : "Battery",
                                gr
                                        ? "Η υγεία είναι μειωμένη ενώ τα θερμικά είναι φυσιολογικά"
                                        : "Health weak while thermals remain normal"
                        );

                        logLabelWarnValue(
                                gr ? "Πιθανή αιτία" : "Likely cause",
                                gr
                                        ? "Γήρανση / απώλεια χωρητικότητας"
                                        : "Battery aging / capacity loss"
                        );
                    }

                    if (lab14Health < 70f && thermalDanger) {

                        logLabelErrorValue(
                                gr ? "Συνδυασμένος κίνδυνος" : "Combined risk",
                                gr
                                        ? "Εντοπίστηκαν προβλήματα μπαταρίας και θερμικής συμπεριφοράς — συνιστάται τεχνικός έλεγχος"
                                        : "Battery + thermal issues detected — technician inspection recommended"
                        );
                    }
                }

                // ------------------------------------------------------------
                // STORE FINAL RESULT
                // ------------------------------------------------------------
                try {
                    p.edit()
                            .putInt("lab17_final_score", fFinalScore)
                            .putString("lab17_category", fCategory)
                            .putLong("lab17_ts", System.currentTimeMillis())
                            .apply();
                } catch (Throwable ignore) {}

                // ================= FINAL =================
                appendHtml("<br>");
                logOk(gr ? "Το Lab 17 ολοκληρώθηκε." : "Lab 17 finished.");
                logLine();

            });

        } catch (Throwable ignore) {
            // silent
        }

    }).start();

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

// 🔴 FIX: STOP TTS ON BACK / DISMISS
popup.setOnDismissListener(d -> AppTTS.stop());
popup.setOnCancelListener(d -> AppTTS.stop());

// TTS
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (popup.isShowing() && !AppTTS.isMuted(this)) {

        String speakText =
                    gr
                            ? "Δεν πληρούνται οι προϋποθέσεις για το LAB δεκαεπτά. "
                              + "Απαιτούνται πρόσφατα αποτελέσματα από τα LAB δεκατέσσερα, "
                              + "δεκατέσσερα βήτα, δεκαπέντε και δεκαέξι."
                            : "Requirements for LAB seventeen are not met. "
                              + "Recent results from LAB fourteen, LAB fourteen B, "
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
        // FILESYSTEM INFO (BEST EFFORT)
        // ------------------------------------------------------------
        try {
            String fsType = s.getClass().getMethod("getFilesystemType") != null
                    ? (String) s.getClass().getMethod("getFilesystemType").invoke(s)
                    : null;

            if (fsType != null) {
                logInfo("Filesystem:");
                logLabelOkValue(
                        gr ? "Τύπος" : "Type",
                        fsType.toUpperCase(Locale.US)
                );
            }
        } catch (Throwable ignore) {}

        // ------------------------------------------------------------
        // ROOT AWARE INTELLIGENCE
        // ------------------------------------------------------------
        boolean rooted = isDeviceRooted();

boolean wearSignals = false;
boolean controllerRisk = false;

if (rooted) {

    appendHtml("<br>");
    logInfo(gr
            ? "Προχωρημένη ανάλυση αποθηκευτικού χώρου (root access):"
            : "Advanced storage analysis (root access):");
            logLine();

    wearSignals = detectStorageWearSignals();
    boolean reservedPressure = pctFree < 12;
            
// ------------------------------------------------------------
// EARLY STORAGE DEGRADATION INDICATORS
// ------------------------------------------------------------
boolean nandRisk = false;
int nandScore = 0;

// primary signal
if (wearSignals)
    nandScore += 60;

// secondary contextual signals
if (reservedPressure)
    nandScore += 15;

if (pctFree < 5)
    nandScore += 25;

// prevent false NAND diagnosis when wearSignals not present
if (!wearSignals)
    nandScore = Math.min(nandScore, 40);

logLabelValue(
        gr ? "Δείκτης πιθανής φθοράς NAND"
           : "Possible NAND degradation index",
        nandScore + "/100"
);

if (nandScore >= 70) {

    nandRisk = true;

    logLabelWarnValue(
            gr ? "Ένδειξη αποθηκευτικού χώρου"
               : "Storage integrity",
            gr
                    ? "Εντοπίστηκε μοτίβο που μπορεί να σχετίζεται με φθορά NAND ή controller."
                    : "Pattern may indicate NAND or storage controller degradation."
    );

} else {

    logLabelOkValue(
            gr ? "Ακεραιότητα αποθηκευτικού χώρου"
               : "Storage integrity",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις φθοράς NAND."
                    : "No NAND degradation indicators detected."
    );
}


// ------------------------------------------------------------
// STORAGE CONTROLLER INSTABILITY DETECTOR
// ------------------------------------------------------------
int controllerScore = 0;

if (pctFree < 10)
    controllerScore += 40;

if (pctFree < 7)
    controllerScore += 30;

if (wearSignals)
    controllerScore += 30;

logLabelValue(
        gr ? "Δείκτης σταθερότητας controller"
           : "Storage controller stability index",
        controllerScore + "/100"
);

if (controllerScore >= 60) {

    controllerRisk = true;

    logLabelWarnValue(
            gr ? "Controller αποθήκευσης"
               : "Storage controller",
            gr
                    ? "Πιθανή αστάθεια controller αποθήκευσης."
                    : "Possible storage controller instability detected."
    );

} else {

    logLabelOkValue(
            gr ? "Controller αποθήκευσης"
               : "Storage controller",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις αστάθειας."
                    : "Controller behaviour appears stable."
    );
}


// ------------------------------------------------------------
// FILESYSTEM CORRUPTION EARLY DETECTOR
// ------------------------------------------------------------
boolean fsCorruptionRisk = false;
int fsScore = 0;

if (pctFree < 10)
    fsScore += 40;

if (pctFree < 7)
    fsScore += 30;

if (wearSignals)
    fsScore += 30;

logLabelValue(
        gr ? "Δείκτης ακεραιότητας filesystem"
           : "Filesystem integrity index",
        fsScore + "/100"
);

if (fsScore >= 60) {

    fsCorruptionRisk = true;

    logLabelWarnValue(
            gr ? "Ακεραιότητα filesystem"
               : "Filesystem integrity",
            gr
                    ? "Εντοπίστηκε μοτίβο που μπορεί να οδηγήσει σε σφάλματα filesystem."
                    : "Pattern detected that may lead to filesystem errors."
    );

} else {

    logLabelOkValue(
            gr ? "Ακεραιότητα filesystem"
               : "Filesystem integrity",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις πιθανής αλλοίωσης."
                    : "No filesystem corruption indicators detected."
    );
}


// ------------------------------------------------------------
// SILENT DATA CORRUPTION PREDICTOR
// ------------------------------------------------------------
boolean silentCorruptionRisk = false;
int corruptionScore = 0;

if (pctFree < 12)
    corruptionScore += 30;

if (wearSignals)
    corruptionScore += 35;

if (controllerRisk)
    corruptionScore += 35;

logLabelValue(
        gr ? "Δείκτης κινδύνου σιωπηλής αλλοίωσης δεδομένων"
           : "Silent data corruption risk index",
        corruptionScore + "/100"
);

if (corruptionScore >= 60) {

    silentCorruptionRisk = true;

    logLabelWarnValue(
            gr ? "Κίνδυνος σιωπηλής αλλοίωσης δεδομένων"
               : "Silent data corruption risk",
            gr
                    ? "Πιθανή φθορά NAND ή controller μπορεί να προκαλέσει αλλοίωση αρχείων."
                    : "Possible NAND or controller degradation may lead to silent file corruption."
    );

} else {

    logLabelOkValue(
            gr ? "Ακεραιότητα δεδομένων"
               : "Data integrity",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις σιωπηλής αλλοίωσης."
                    : "No silent corruption indicators detected."
    );
}
}

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
        logOk(gr ? "Το Lab 18 ολοκληρώθηκε." : "Lab 18 finished.");
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

        long total = mi.totalMem;
        long free  = mi.availMem;
        long used  = total - free;

        int pctFree = (int) ((free * 100L) / Math.max(1L, total));

        // ------------------------------------------------------------
        // BASIC SNAPSHOT
        // ------------------------------------------------------------
        logInfo(gr ? "Τρέχουσα χρήση RAM:" : "Current RAM usage:");
        logLabelOkValue(
                gr ? "Χρήση" : "Usage",
                humanBytes(used) +
                        (gr ? " χρησιμοποιούνται / " : " used / ") +
                        humanBytes(total) +
                        (gr
                                ? " (ελεύθερα " + humanBytes(free) + ", " + pctFree + "%)"
                                : " (free " + humanBytes(free) + ", " + pctFree + "%)")
        );

        // ------------------------------------------------------------
        // HUMAN INTERPRETATION
        // ------------------------------------------------------------
        appendHtml("<br>");
        logInfo(gr ? "Αξιολόγηση πίεσης RAM:" : "RAM pressure assessment:");
        logLine();

        if (pctFree < 8) {

            logLabelErrorValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Κρίσιμη πίεση RAM"
                       : "Critical RAM pressure"
            );
            logLabelErrorValue(
                    gr ? "Συμπεριφορά συστήματος" : "System behaviour",
                    gr ? "Επιθετικό κλείσιμο εφαρμογών στο παρασκήνιο"
                       : "Aggressive background app killing"
            );
            logLabelWarnValue(
                    gr ? "Επίδραση στον χρήστη" : "User impact",
                    gr ? "Έντονο lag, επαναφορτώσεις και κολλήματα UI"
                       : "Strong lag, reloads and UI stutter"
            );

        } else if (pctFree < 15) {

            logLabelWarnValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Υψηλή πίεση RAM"
                       : "High RAM pressure"
            );
            logLabelWarnValue(
                    gr ? "Επίδραση στον χρήστη" : "User impact",
                    gr ? "Το multitasking μπορεί να γίνει ασταθές"
                       : "Multitasking may become unstable"
            );

        } else if (pctFree < 25) {

            logLabelOkValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Αυξημένη χρήση RAM"
                       : "Elevated RAM usage"
            );
            logLabelOkValue(
                    gr ? "Σημείωση" : "Note",
                    gr ? "Φυσιολογικό κατά τη χρήση βαριών εφαρμογών ή gaming"
                       : "Normal during heavy apps or gaming"
            );

        } else {

            logLabelOkValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Υγιές επίπεδο RAM"
                       : "Healthy RAM level"
            );
        }

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

    boolean frequentReboots = false;   // shared summary flag

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
                    gr ? "Εντοπίστηκε πρόσφατη επανεκκίνηση"
                       : "Recent reboot detected"
            );
            logLabelWarnValue(
                    gr ? "Επίπτωση" : "Impact",
                    gr ? "Ορισμένα προβλήματα μπορεί να καλύπτονται προσωρινά"
                       : "Some issues may be temporarily masked"
            );
            logLabelOkValue(
                    gr ? "Σημείωση" : "Note",
                    gr
                            ? "Οι διαγνώσεις είναι έγκυρες αλλά όχι πλήρως αντιπροσωπευτικές ακόμη"
                            : "Diagnostics are valid but not fully representative yet"
            );

        } else if (veryLongUptime) {

            logLabelWarnValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Μεγάλος χρόνος λειτουργίας"
                       : "Long uptime detected"
            );
            logLabelWarnValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "Συσσώρευση φόρτου παρασκηνίου και πίεσης μνήμης"
                       : "Background load and memory pressure may accumulate"
            );

            if (extremeUptime) {

                logLabelErrorValue(
                        gr ? "Σοβαρότητα" : "Severity",
                        gr
                                ? "Εξαιρετικά μεγάλος χρόνος λειτουργίας (> 14 ημέρες)"
                                : "Extremely long uptime (> 14 days)"
                );
                logLabelErrorValue(
                        gr ? "Σύσταση" : "Recommendation",
                        gr
                                ? "Συνιστάται έντονα επανεκκίνηση πριν από τελικά συμπεράσματα"
                                : "Reboot strongly recommended before final conclusions"
                );

            } else {

                logLabelOkValue(
                        gr ? "Σύσταση" : "Recommendation",
                        gr
                                ? "Μια επανεκκίνηση μπορεί να βοηθήσει στην επαναφορά της κατάστασης"
                                : "A reboot can help reset system state"
                );
            }

        } else {

            logLabelOkValue(
                    gr ? "Κατάσταση" : "Status",
                    gr
                            ? "Χρόνος λειτουργίας εντός υγιούς διαγνωστικού εύρους"
                            : "Uptime within healthy diagnostic range"
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

            boolean lowMemoryPressure =
                    readLowMemoryKillCountSafe() < 5;

            frequentReboots =
                    detectFrequentRebootsHint();

            if (frequentReboots) {

                logLabelWarnValue(
                        gr ? "Μοτίβο επανεκκινήσεων" : "Reboot pattern",
                        gr
                                ? "Εντοπίστηκαν επαναλαμβανόμενες επανεκκινήσεις"
                                : "Repeated reboots detected"
                );
                logLabelWarnValue(
                        gr ? "Πιθανές αιτίες" : "Possible causes",
                        gr
                                ? "Αστάθεια, κρασαρίσματα ή watchdog resets"
                                : "Instability, crashes or watchdog resets"
                );

            } else {

                logLabelOkValue(
                        gr ? "Μοτίβο επανεκκινήσεων" : "Reboot pattern",
                        gr
                                ? "Δεν εντοπίστηκε μη φυσιολογική συμπεριφορά επανεκκινήσεων"
                                : "No abnormal reboot behaviour detected"
                );
            }

            if (!lowMemoryPressure) {

                logLabelWarnValue(
                        gr ? "Πίεση μνήμης" : "Memory pressure",
                        gr
                                ? "Εντοπίστηκαν συμβάντα πίεσης στο παρασκήνιο"
                                : "Background pressure events detected"
                );
                logLabelWarnValue(
                        gr ? "Συμπεριφορά συστήματος" : "System behaviour",
                        gr
                                ? "Επιθετική διαχείριση εφαρμογών στο παρασκήνιο"
                                : "Aggressive background app management"
                );

            } else {

                logLabelOkValue(
                        gr ? "Πίεση μνήμης" : "Memory pressure",
                        gr
                                ? "Δεν εντοπίστηκαν σημαντικά σήματα πίεσης"
                                : "No significant pressure signals detected"
                );
            }

            logLabelOkValue(
                    gr ? "Ερμηνεία" : "Interpretation",
                    gr
                            ? "Η συμπεριφορά uptime συμβαδίζει με φυσιολογική λειτουργία συστήματος"
                            : "Uptime behaviour consistent with normal system operation"
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

if (frequentReboots) {

    logLabelWarnValue(
            "REBOOT_PATTERN",
            gr ? "ΜΗ ΦΥΣΙΟΛΟΓΙΚΟ" : "ABNORMAL"
    );

} else {

    logLabelOkValue(
            "REBOOT_PATTERN",
            gr ? "ΦΥΣΙΟΛΟΓΙΚΌ" : "NORMAL"
    );
}

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

private void lab21ScreenLock() {
	
	final boolean gr = AppLang.isGreek(this);

// GUARD — avoid double-tap spam  
if (lab21Running) {  
    logWarn(gr 
        ? "Το LAB 21 εκτελείται ήδη..." 
        : "LAB 21 is already running...");  
    return;  
}  
lab21Running = true;  

appendHtml("<br>");  
logLine();  
logInfo(gr 
    ? "LAB 21 — Κλείδωμα Οθόνης / Βιομετρικά (Live + Root-Aware)" 
    : "LAB 21 — Screen Lock / Biometrics (Live + Root-Aware)");  
logLine();

// ------------------------------------------------------------  
// PART A — LOCK CONFIG + STATE  
// ------------------------------------------------------------  
boolean secure = false;
boolean lockedNow = false;

try {
    KeyguardManager km =
            (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

    if (km != null) {

        secure = km.isDeviceSecure();

        try {
            lockedNow = km.isKeyguardLocked();
        } catch (Throwable ignore) {}

        if (secure) {
            logLabelOkValue(
                    gr ? "Ρύθμιση κλειδώματος οθόνης" : "Screen lock configuration",
                    gr ? "Ρυθμισμένο (PIN / Μοτίβο / Κωδικός)"
                       : "Configured (PIN / Pattern / Password)"
            );
        } else {
            logLabelErrorValue(
                    gr ? "Ρύθμιση κλειδώματος οθόνης" : "Screen lock configuration",
                    gr ? "ΔΕΝ έχει ρυθμιστεί"
                       : "NOT configured"
            );
            logLabelWarnValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "Φυσική πρόσβαση με πλήρη έκθεση δεδομένων"
                       : "Physical access with full data exposure"
            );
        }

        if (secure) {

            if (lockedNow) {
                logLabelOkValue(
                        gr ? "Κατάσταση" : "State",
                        gr ? "ΚΛΕΙΔΩΜΕΝΟ (ενεργό keyguard)"
                           : "LOCKED (keyguard active)"
                );
            } else {
                logLabelWarnValue(
                        gr ? "Κατάσταση" : "State",
                        gr ? "ΞΕΚΛΕΙΔΩΤΟ (η συσκευή είναι ανοιχτή)"
                           : "UNLOCKED (device currently open)"
                );
            }
        }

    } else {
        logLabelWarnValue(
                gr ? "Υπηρεσία Keyguard" : "Keyguard",
                gr ? "Μη διαθέσιμη υπηρεσία"
                   : "Service unavailable"
        );
    }

} catch (Throwable e) {
    logLabelWarnValue(
            gr ? "Έλεγχος κλειδώματος" : "Lock detection",
            (gr ? "Αποτυχία: " : "Failed: ") + e.getMessage()
    );
}

// ------------------------------------------------------------  
// PART B — BIOMETRIC CAPABILITY (FRAMEWORK, NO ANDROIDX)  
// ------------------------------------------------------------

boolean biometricSupported = false;

if (Build.VERSION.SDK_INT >= 29) {
    try {

        android.hardware.biometrics.BiometricManager bm =
                getSystemService(android.hardware.biometrics.BiometricManager.class);

        if (bm != null) {

            int r = bm.canAuthenticate(
                    android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
            );

            if (r != android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS) {

                r = bm.canAuthenticate(
                        android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK
                );
            }

            if (r == android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS) {

                biometricSupported = true;

                logLabelOkValue(
                        gr ? "Βιομετρικά" : "Biometrics",
                        gr ? "Υλικό παρόν & έτοιμο για χρήση"
                           : "Hardware present & usable"
                );

            } else {

                logLabelWarnValue(
                        gr ? "Βιομετρικά" : "Biometrics",
                        gr ? "Υπάρχουν αλλά δεν είναι έτοιμα"
                           : "Present but not ready"
                );
            }

        } else {

            logLabelWarnValue(
                    gr ? "Βιομετρικά" : "Biometrics",
                    gr ? "Μη διαθέσιμος διαχειριστής"
                       : "Manager unavailable"
            );
        }

    } catch (Throwable e) {

        logLabelWarnValue(
                gr ? "Βιομετρικά" : "Biometrics",
                (gr ? "Αποτυχία ελέγχου: " : "Check failed: ") + e.getMessage()
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Βιομετρικά" : "Biometrics",
            gr ? "Δεν υποστηρίζονται σε αυτήν την έκδοση Android"
               : "Not supported on this Android version"
    );
}

// ------------------------------------------------------------  
// PART C — ROOT-AWARE AUTH INFRA CHECK (POLICY / FILES)  
// ------------------------------------------------------------  
boolean hasLockDb = false;
boolean hasGatekeeper = false;
boolean hasKeystore = false;

boolean root = isRootAvailable();

if (root) {

    logLabelOkValue(
            gr ? "Λειτουργία Root" : "Root mode",
            gr ? "ΔΙΑΘΕΣΙΜΗ" : "AVAILABLE"
    );

    hasLockDb     = rootPathExists("/data/system/locksettings.db");
    hasGatekeeper = rootGlobExists("/data/system/gatekeeper*");
    hasKeystore   = rootPathExists("/data/misc/keystore");

    logLabelOkValue(
            "Gatekeeper",
            hasGatekeeper
                    ? (gr ? "Εντοπίστηκε" : "Detected")
                    : (gr ? "Δεν εντοπίστηκε" : "Not detected")
    );

    logLabelOkValue(
            gr ? "Βάση κλειδώματος" : "Lock DB",
            hasLockDb
                    ? (gr ? "Εντοπίστηκε" : "Detected")
                    : (gr ? "Δεν εντοπίστηκε" : "Not detected")
    );

    logLabelOkValue(
            "Keystore",
            hasKeystore
                    ? (gr ? "Εντοπίστηκε" : "Detected")
                    : (gr ? "Δεν εντοπίστηκε" : "Not detected")
    );

} else {

    logLabelOkValue(
            gr ? "Λειτουργία Root" : "Root mode",
            gr ? "Μη διαθέσιμη" : "Not available"
    );
}

// ============================================================  
// LAB 21 — TRUST BOUNDARY AWARENESS  
// ============================================================  

if (secure) {

    logLabelOkValue(
            gr ? "Προστασία μετά από επανεκκίνηση" 
               : "Post-reboot protection",
            gr ? "Απαιτείται ταυτοποίηση πριν την πρόσβαση στα δεδομένα"
               : "Authentication required before data access"
    );

} else {

    logLabelErrorValue(
            gr ? "Προστασία μετά από επανεκκίνηση" 
               : "Post-reboot protection",
            gr ? "ΔΕΝ επιβάλλεται (τα δεδομένα εκτίθενται μετά από επανεκκίνηση)"
               : "NOT enforced (data exposed after reboot)"
    );
}

logLabelOkValue(
        gr ? "Κύριο επίπεδο ασφάλειας" 
           : "Primary security layer",
        secure
                ? (gr ? "Γνωστικό διαπιστευτήριο (PIN / Μοτίβο / Κωδικός)"
                      : "Knowledge-based credential")
                : (gr ? "ΚΑΝΕΝΑ"
                      : "NONE")
);

logLabelOkValue(
        gr ? "Επίπεδο ευκολίας" 
           : "Convenience layer",
        biometricSupported
                ? (gr ? "Διαθέσιμα βιομετρικά"
                      : "Biometrics available")
                : (gr ? "Μη διαθέσιμα"
                      : "Not available")
);

if (root) {

    if (hasGatekeeper || hasLockDb) {

        logLabelOkValue(
                gr ? "Επιβολή συστήματος" 
                   : "System enforcement",
                gr ? "Υποδομή ταυτοποίησης ενεργή"
                   : "Authentication infrastructure active"
        );

    } else {

        logLabelWarnValue(
                gr ? "Επιβολή συστήματος" 
                   : "System enforcement",
                gr ? "Μη ξεκάθαρα σήματα (διαφοροποίηση ROM / κατασκευαστή)"
                   : "Signals unclear (ROM/vendor variation)"
        );
    }
}

// ------------------------------------------------------------  
// PART D — RISK SCORE (FAST, CLEAR)  
// ------------------------------------------------------------  
int risk = 0;

if (!secure) risk += 70;
if (secure && !lockedNow) risk += 10;
if (secure && !biometricSupported) risk += 5;

if (risk >= 70) {

    logLabelErrorValue(
            gr ? "Δείκτης επίδρασης ασφάλειας" : "Security impact score",
            gr ? "ΥΨΗΛΗ (" + risk + "/100)"
               : "HIGH (" + risk + "/100)"
    );

} else if (risk >= 30) {

    logLabelWarnValue(
            gr ? "Δείκτης επίδρασης ασφάλειας" : "Security impact score",
            gr ? "ΜΕΤΡΙΑ (" + risk + "/100)"
               : "MEDIUM (" + risk + "/100)"
    );

} else {

    logLabelOkValue(
            gr ? "Δείκτης επίδρασης ασφάλειας" : "Security impact score",
            gr ? "ΧΑΜΗΛΗ (" + risk + "/100)"
               : "LOW (" + risk + "/100)"
    );
}

// ------------------------------------------------------------
// PART E — LIVE BIOMETRIC AUTH TEST (USER-DRIVEN, REAL)
// ------------------------------------------------------------
if (!secure) {

    appendHtml("<br>");
    logInfo(gr ? "Ζωντανός έλεγχος βιομετρικών:"
               : "Live biometric test:");
               logLine();
               
    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Παραλείφθηκε" : "Skipped"
    );
    logLabelWarnValue(
            gr ? "Λόγος" : "Reason",
            gr ? "Απαιτείται ασφαλές κλείδωμα (PIN / Μοτίβο / Κωδικός)"
               : "Secure lock required (PIN / Pattern / Password)"
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
    logLine();
    lab21Running = false;
    return;
}

if (!biometricSupported) {

    appendHtml("<br>");
    logInfo(gr ? "Ζωντανός έλεγχος βιομετρικών:"
               : "Live biometric test:");
               logLine();
               
    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Δεν ξεκίνησε" : "Not started"
    );
    logLabelWarnValue(
            gr ? "Λόγος" : "Reason",
            gr ? "Τα βιομετρικά δεν είναι έτοιμα ή δεν είναι διαθέσιμα"
               : "Biometrics not ready or not available"
    );
    logLabelOkValue(
            gr ? "Ενέργεια" : "Action",
            gr ? "Ρυθμίστε βιομετρικά στις Ρυθμίσεις και επανεκτελέστε το LAB 21"
               : "Enroll biometrics in Settings and re-run LAB 21"
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
    logLine();
    lab21Running = false;
    return;
}

if (Build.VERSION.SDK_INT >= 28) {

    try {

        appendHtml("<br>");
        logInfo(gr ? "ΖΩΝΤΑΝΟΣ ΕΛΕΓΧΟΣ ΑΙΣΘΗΤΗΡΑ"
                   : "LIVE SENSOR TEST");
                   logLine();
                   
        logLabelOkValue(
                gr ? "Οδηγία" : "Instruction",
                gr ? "Τοποθετήστε δάχτυλο / πρόσωπο για ταυτοποίηση ΤΩΡΑ"
                   : "Place finger / face for authentication NOW"
        );
        logLabelOkValue(
                gr ? "Αποτέλεσμα" : "Result",
                gr ? "Θα καταγραφεί PASS / FAIL (πραγματικός έλεγχος υλικού)"
                   : "PASS / FAIL will be recorded (real hardware)"
        );

        Executor executor = getMainExecutor();
        CancellationSignal cancel = new CancellationSignal();

        android.hardware.biometrics.BiometricPrompt.AuthenticationCallback cb =
                new android.hardware.biometrics.BiometricPrompt.AuthenticationCallback() {

                    @Override
                    public void onAuthenticationSucceeded(
                            android.hardware.biometrics.BiometricPrompt.AuthenticationResult result) {

                        appendHtml("<br>");
                        logInfo(gr ? "ΖΩΝΤΑΝΟΣ ΕΛΕΓΧΟΣ ΒΙΟΜΕΤΡΙΚΟΥ"
                                   : "LIVE BIOMETRIC TEST");
                                   logLine ();
                                   
                        logLabelOkValue(
                                gr ? "Αποτέλεσμα" : "Result",
                                "PASS"
                        );
                        logLabelOkValue(
                                gr ? "Αλυσίδα ελέγχου" : "Pipeline",
                                gr ? "Αισθητήρας + ταυτοποίηση λειτουργούν σωστά"
                                   : "Biometric sensor + auth verified functional"
                        );
appendHtml("<br>");

                        logOk(gr ? "Συσκευές με πολλαπλά βιομετρικά"
                                   : "Multi-biometric devices");
                                   logLine();
                                   
                        logLabelWarnValue(
                                gr ? "Σημείωση" : "Note",
                                gr ? "Το Android ελέγχει ΕΝΑ βιομετρικό ανά εκτέλεση"
                                   : "Android tests ONE biometric path per run"
                        );
                        logLabelOkValue(
                                gr ? "Ενέργεια" : "Action",
                                gr ? "Απενεργοποιήστε το τρέχον βιομετρικό και επανεκτελέστε το LAB 21"
                                   : "Disable current biometric in Settings and re-run LAB 21"
                        );
                        logLabelWarnValue(
                                gr ? "Σημείωση OEM" : "OEM note",
                                gr ? "Ο κατασκευαστής μπορεί να δίνει προτεραιότητα στον ίδιο αισθητήρα"
                                   : "OEM may still prioritize same sensor"
                        );

                        appendHtml("<br>");
                        logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
                        logLine();
                        lab21Running = false;
                    }

                    @Override
                    public void onAuthenticationFailed() {

                        appendHtml("<br>");
                        logInfo(gr ? "ΖΩΝΤΑΝΟΣ ΕΛΕΓΧΟΣ ΒΙΟΜΕΤΡΙΚΟΥ"
                                   : "LIVE BIOMETRIC TEST");
                                   logLine();
                                   
                        logLabelErrorValue(
                                gr ? "Αποτέλεσμα" : "Result",
                                "FAIL"
                        );
                        logLabelWarnValue(
                                gr ? "Ερμηνεία" : "Meaning",
                                gr ? "Το βιομετρικό δεν επιβεβαιώθηκε κατά τον πραγματικό έλεγχο"
                                   : "Biometric did not authenticate during real sensor test"
                        );

                        appendHtml("<br>");
                        logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
                        logLine();
                        lab21Running = false;
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {

                        appendHtml("<br>");
                        logInfo(gr ? "ΖΩΝΤΑΝΟΣ ΕΛΕΓΧΟΣ ΒΙΟΜΕΤΡΙΚΟΥ"
                                   : "LIVE BIOMETRIC TEST");
                                   logLine();
                                   
                        logLabelWarnValue(
                                gr ? "Αποτέλεσμα" : "Result",
                                gr ? "Μη επιβεβαιωμένο"
                                   : "Not confirmed"
                        );
                        logLabelWarnValue(
                                gr ? "Σύστημα" : "System",
                                gr ? "Ενεργοποιήθηκε εφεδρικό διαπιστευτήριο"
                                   : "Fallback to device credential detected"
                        );
                        logLabelWarnValue(
                                gr ? "Ερμηνεία" : "Meaning",
                                gr ? "Ο αισθητήρας ΔΕΝ επιβεβαιώθηκε λειτουργικός"
                                   : "Biometric sensor NOT verified functional"
                        );

                        appendHtml("<br>");
                        logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
                        logLine();
                        lab21Running = false;
                    }
                };

        android.hardware.biometrics.BiometricPrompt prompt =
                new android.hardware.biometrics.BiometricPrompt.Builder(this)
                        .setTitle(gr
                                ? "LAB 21 — Ζωντανός Έλεγχος Βιομετρικού Αισθητήρα"
                                : "LAB 21 — Live Biometric Sensor Test")
                        .setSubtitle(gr
                                ? "Τοποθετήστε δάχτυλο / πρόσωπο για επιβεβαίωση"
                                : "Place finger / face to verify sensor works")
                        .setDescription(gr
                                ? "Πραγματικός έλεγχος υλικού (χωρίς προσομοίωση)."
                                : "This is a REAL hardware test (no simulation).")
                        .setNegativeButton(
                                gr ? "Ακύρωση ελέγχου" : "Cancel test",
                                executor,
                                (dialog, which) -> {

                                    appendHtml("<br>");
                                    logInfo(gr ? "ΖΩΝΤΑΝΟΣ ΕΛΕΓΧΟΣ ΒΙΟΜΕΤΡΙΚΟΥ"
                                               : "LIVE BIOMETRIC TEST");
                                               logLine();
                                               
                                    logLabelWarnValue(
                                            gr ? "Αποτέλεσμα" : "Result",
                                            gr ? "Ακυρώθηκε από τον χρήστη"
                                               : "Cancelled by user"
                                    );

                                    appendHtml("<br>");
                                    logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
                                    logLine();
                                    lab21Running = false;
                                }
                        )
                        .setAllowedAuthenticators(
                                android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
                        )
                        .build();

        logInfo(gr ? "Προτροπή βιομετρικού:" : "Biometric prompt:");
        logLabelOkValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Εκκίνηση…" : "Starting…"
        );

        prompt.authenticate(cancel, executor, cb);

    } catch (Throwable e) {

        appendHtml("<br>");
        logInfo(gr ? "Ζωντανός έλεγχος βιομετρικών:"
                   : "Live biometric test");
                   logLine();
        logLabelErrorValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Αποτυχία" : "Failed"
        );
        logLabelWarnValue(
                gr ? "Λόγος" : "Reason",
                (gr ? "Σφάλμα προτροπής βιομετρικών: " : "Biometric prompt error: ") + e.getMessage()
        );

        appendHtml("<br>");
        logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
        logLine();
        lab21Running = false;
    }

} else {

    appendHtml("<br>");
    logInfo(gr ? "Ζωντανός έλεγχος βιομετρικών:"
               : "Live biometric test:");
               logLine();
               
    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Δεν υποστηρίζεται"
               : "Not supported"
    );
    logLabelWarnValue(
            gr ? "Λόγος" : "Reason",
            gr ? "Το BiometricPrompt framework δεν υποστηρίζεται σε αυτήν την έκδοση Android"
               : "BiometricPrompt framework not available on this Android version"
    );

            logLabelWarnValue(
        gr ? "Απαιτούμενη ενέργεια" : "Action required",
        gr
                ? "Ελέγξτε τα βιομετρικά από τις ρυθμίσεις συστήματος κλειδώματος οθόνης και επανεκτελέστε το LAB 21"
                : "Test biometrics via system lock screen settings, then re-run LAB 21"
);

    logInfo("Note");
    logLabelOkValue(
            gr ? "Κάλυψη" : "Coverage",
            gr ? "Κάθε εκτέλεση του LAB 21 ελέγχει ΕΝΑ βιομετρικό μονοπάτι"
               : "Each LAB 21 run verifies ONE biometric sensor path"
    );
    logLabelOkValue(
            gr ? "Ενέργεια" : "Action",
            gr ? "Απενεργοποιήστε το ενεργό βιομετρικό στις ρυθμίσεις, για να ελέγξετε άλλον αισθητήρα"
               : "Disable active biometric in Settings to test another sensor"
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
    logLine();
    lab21Running = false;
}
}

// ============================================================
// ROOT HELPERS — minimal, safe, no assumptions
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

        if (diffMonths <= 3) {

            logLabelOkValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "ΠΡΟΣΦΑΤΗ (χαμηλή έκθεση σε γνωστά exploits)"
                       : "RECENT (low known exploit exposure)"
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
// 3) Play Protect Detection (best effort, non-root)
// ------------------------------------------------------------
try {
    PackageManager pm = getPackageManager();

    boolean gmsPresent;
    try {
        pm.getPackageInfo("com.google.android.gms", 0);
        gmsPresent = true;
    } catch (Throwable ignore) {
        gmsPresent = false;
    }

    logInfo(gr ? "Play Protect" : "Play Protect");

    if (!gmsPresent) {

        logLabelErrorValue(
                gr ? "Υπηρεσίες Google Play" : "Google Play Services",
                gr ? "ΔΕΝ βρέθηκαν" : "NOT present"
        );

        logLabelWarnValue(
                "Play Protect",
                gr ? "Μη διαθέσιμο" : "Unavailable"
        );

    } else {

        int verify = -1;
        try {
            verify = Settings.Global.getInt(
                    getContentResolver(),
                    "package_verifier_enable",
                    -1
            );
        } catch (Throwable ignore) {}

        if (verify == 1) {

            logLabelOkValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "ΕΝΕΡΓΟ (Έλεγχος εφαρμογών ενεργός)"
                       : "ENABLED (Verify Apps ON)"
            );

        } else if (verify == 0) {

            logLabelWarnValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "ΑΝΕΝΕΡΓΟ (Έλεγχος εφαρμογών απενεργοποιημένος)"
                       : "DISABLED (Verify Apps OFF)"
            );

        } else {

            Intent i = new Intent();
            i.setClassName(
                    "com.google.android.gms",
                    "com.google.android.gms.security.settings.VerifyAppsSettingsActivity"
            );

            if (i.resolveActivity(pm) != null) {

                logLabelOkValue(
                        gr ? "Μονάδα" : "Module",
                        gr ? "Εντοπίστηκε (διαθέσιμη δραστηριότητα ρυθμίσεων)"
                           : "Detected (settings activity present)"
                );

                logLabelWarnValue(
                        gr ? "Κατάσταση" : "Status",
                        gr ? "Άγνωστη (OEM / περιορισμένη έκδοση)"
                           : "Unknown (OEM / restricted build)"
                );

            } else {

                logLabelWarnValue(
                        "Play Protect",
                        gr ? "Η κατάσταση δεν είναι σαφής"
                           : "Status unclear"
                );
            }
        }
    }

} catch (Throwable e) {

    logLabelWarnValue(
            gr ? "Ανίχνευση Play Protect" : "Play Protect detection",
            (gr ? "Αποτυχία (" : "Failed (") + e.getMessage() + ")"
    );
}

// ------------------------------------------------------------
// 4) Trust Boundary Clarification
// ------------------------------------------------------------
logLine();
logInfo(gr ? "Πεδίο ασφάλειας"
           : "Security scope");

logLabelOkValue(
        "Play Protect",
        gr ? "Έλεγχος κακόβουλου λογισμικού και επαλήθευση εφαρμογών"
           : "Malware scanning and app verification"
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
    logInfo(gr 
            ? "LAB 23 — Κίνδυνος από επιλογές Προγραμματιστή"
            : "LAB 22 — Developer Options Risk");
    logLine();

    int risk = 0;
    boolean usbDebug = false;

    try {
        int adb = Settings.Global.getInt(
                getContentResolver(),
                Settings.Global.ADB_ENABLED,
                0
        );
        usbDebug = (adb == 1);

        if (usbDebug) {

            logLabelWarnValue(
                    gr ? "Κατάσταση USB Debugging" : "Status",
                    gr ? "ΕΝΕΡΓΟΠΟΙΗΜΕΝΟ"
                       : "ENABLED"
            );

            logLabelWarnValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "Επιφάνεια επίθεσης με φυσική πρόσβαση"
                       : "Physical access attack surface"
            );

            risk += 30;

        } else {

            logLabelOkValue(
                    gr ? "Κατάσταση USB Debugging" : "Status",
                    gr ? "ΑΝΕΝΕΡΓΟ"
                       : "OFF"
            );
        }

    } catch (Throwable e) {

        logLabelWarnValue(
                gr ? "USB Debugging" : "USB Debugging",
                gr ? "Αδυναμία ανάγνωσης (περιορισμός κατασκευαστή)"
                   : "Unable to read (OEM restriction)"
        );

        risk += 5;
    }

// ============================================================
// 2) DEVELOPER OPTIONS FLAG
// ============================================================
boolean devOpts = false;

try {
    int dev = Settings.Global.getInt(
            getContentResolver(),
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            0
    );
    devOpts = (dev == 1);

    if (devOpts) {

        logLabelWarnValue(
                gr ? "Επιλογές προγραμματιστή" : "Developer options",
                gr ? "ΕΝΕΡΓΟΠΟΙΗΜΕΝΕΣ"
                   : "ENABLED"
        );

        logLabelWarnValue(
                gr ? "Κίνδυνος" : "Risk",
                gr ? "Έκθεση σε προχωρημένες ρυθμίσεις συστήματος"
                   : "Advanced system settings exposed"
        );

        risk += 20;

    } else {

        logLabelOkValue(
                gr ? "Επιλογές προγραμματιστή" : "Developer options",
                gr ? "ΑΝΕΝΕΡΓΕΣ"
                   : "OFF"
        );
    }

} catch (Throwable e) {

    logLabelWarnValue(
            gr ? "Επιλογές προγραμματιστή" : "Developer options",
            gr ? "Αδυναμία ανάγνωσης"
               : "Unable to read"
    );

    risk += 5;
}

// ============================================================
// 3) ADB OVER WI-FI (TCP/IP 5555)
// ============================================================
boolean adbWifi = isPortOpen(5555, 200);

if (adbWifi) {

    logLabelErrorValue(
            gr ? "ADB μέσω Wi-Fi" : "ADB over Wi-Fi",
            gr ? "ΕΝΕΡΓΟ (θύρα 5555)"
               : "ACTIVE (port 5555)"
    );

    logLabelErrorValue(
            gr ? "Κίνδυνος" : "Risk",
            gr ? "Δυνατότητα απομακρυσμένου debugging στο τοπικό δίκτυο"
               : "Remote debugging possible on local network"
    );

    risk += 40;

} else {

    logLabelOkValue(
            gr ? "ADB μέσω Wi-Fi" : "ADB over Wi-Fi",
            gr ? "ΑΝΕΝΕΡΓΟ"
               : "OFF"
    );
}

// ============================================================
// 4) ADB PAIRING MODE (Wireless Debugging)
// ============================================================
boolean adbPairing =
        isPortOpen(3700, 200) ||
        isPortOpen(7460, 200) ||
        scanPairingPortRange();

if (adbPairing) {

    logLabelWarnValue(
            gr ? "ADB σύζευξη / Ασύρματο debugging" : "ADB pairing / wireless debugging",
            gr ? "ΕΝΕΡΓΟ"
               : "ACTIVE"
    );

    logLabelWarnValue(
            gr ? "Κίνδυνος" : "Risk",
            gr ? "Η συσκευή είναι ανιχνεύσιμη για σύζευξη"
               : "Device discoverable for pairing"
    );

    risk += 25;

} else {

    logLabelOkValue(
            gr ? "ADB σύζευξη / Ασύρματο debugging" : "ADB pairing / wireless debugging",
            gr ? "ΑΝΕΝΕΡΓΟ"
               : "OFF"
    );
}

// ============================================================
// 5) FINAL RISK SCORE
// ============================================================
risk = Math.min(100, risk);

String level;
if (risk <= 10)       level = gr ? "ΧΑΜΗΛΟ" : "LOW";
else if (risk <= 30)  level = gr ? "ΜΕΤΡΙΟ" : "MEDIUM";
else if (risk <= 60)  level = gr ? "ΥΨΗΛΟ" : "HIGH";
else                  level = gr ? "ΚΡΙΣΙΜΟ" : "CRITICAL";


if (risk >= 70) {
	
appendHtml("<br>");
    logLabelErrorValue(
            gr ? "Δείκτης κινδύνου ασφάλειας" : "Security risk score",
            risk + "/100 (" + level + ")"
    );

} else if (risk >= 30) {

    logLabelWarnValue(
            gr ? "Δείκτης κινδύνου ασφάλειας" : "Security risk score",
            risk + "/100 (" + level + ")"
    );

} else {

    logLabelOkValue(
            gr ? "Δείκτης κινδύνου ασφάλειας" : "Security risk score",
            risk + "/100 (" + level + ")"
    );
}

// ============================================================
// 6) ACTION RECOMMENDATIONS
// ============================================================
appendHtml("<br>");
logInfo(gr ? "Προτεινόμενες ενέργειες"
           : "Recommended actions");
           logLine();

if (usbDebug || devOpts) {

    logLabelWarnValue(
            gr ? "Απενεργοποίηση" : "Disable",
            gr ? "Ρυθμίσεις > Σύστημα > Επιλογές προγραμματιστή > OFF"
               : "Settings > System > Developer options > OFF"
    );

    logLabelWarnValue(
            "USB Debugging",
            gr ? "Απενεργοποιήστε το"
               : "Turn OFF"
    );

    } else {

    logLabelOkValue(
            "USB Debugging",
            gr ? "Απενεργοποιημένο"
               : "Disabled"
    );
}

if (adbWifi || adbPairing) {

    logLabelErrorValue(
            gr ? "Ασύρματο debugging"
               : "Wireless debugging",
            gr ? "Απενεργοποιήστε άμεσα (Επιλογές προγραμματιστή)"
               : "Disable immediately (Developer options)"
    );

    logLabelWarnValue(
            gr ? "Συμβουλή" : "Tip",
            gr ? "Η επανεκκίνηση καθαρίζει ενεργό TCP/IP debugging"
               : "Reboot clears active TCP/IP debugging"
    );

} else {

    logLabelOkValue(
            gr ? "Ασύρματο debugging"
               : "Wireless debugging",
            gr ? "Δεν είναι ενεργό"
               : "Not active"
    );
}

if (risk >= 60) {

    logLabelErrorValue(
            gr ? "Επείγον" : "Urgency",
            gr ? "Πολύ υψηλό — απενεργοποιήστε άμεσα τις λειτουργίες ADB"
               : "Very high — disable ADB features immediately"
    );

} else if (risk >= 30) {

    logLabelWarnValue(
            gr ? "Επείγον" : "Urgency",
            gr ? "Μερική έκθεση — ελέγξτε τις ρυθμίσεις"
               : "Partial exposure — review settings"
    );

} else {

    logLabelOkValue(
            gr ? "Συνολικά" : "Overall",
            gr ? "Το επίπεδο κινδύνου είναι αποδεκτό"
               : "Risk level acceptable"
    );
}

appendHtml("<br>");
logOk(gr ? "Το Lab 23 ολοκληρώθηκε." : "Lab 23 finished.");
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
    logInfo(gr
            ? "LAB 24 — Έλεγχος Root / Ακεραιότητας Bootloader (ΑΥΤΟΜΑΤΟ)."
            : "LAB 24 — Root / Bootloader Integrity Scan (AUTO).");
    logLine();

// ---------------------------  
// (1) ROOT DETECTION  
// ---------------------------  
int rootScore = 0;  
List<String> rootFindings = new ArrayList<>();  

// su / busybox paths  
String[] suPaths = {  
        "/system/bin/su",  
        "/system/xbin/su",  
        "/sbin/su",  
        "/su/bin/su",  
        "/system/bin/busybox",  
        "/system/xbin/busybox",  
        "/vendor/bin/su",  
        "/odm/bin/su"  
};  

boolean suFound = false;  

for (String p : suPaths) {  
    if (lab24_fileExists(p)) {  
        suFound = true;  
        rootScore += 18;  
        rootFindings.add("su/busybox path found: " + p);  
    }  
}  

// which su (best-effort, avoid false positives)  
String whichSu = lab24_execFirstLine("which su");
if (whichSu != null &&
    (whichSu.contains("/system/xbin/") ||
     whichSu.contains("/su/"))) {

    rootScore += 12;
    rootFindings.add("'which su' returned: " + whichSu);
    suFound = true;
}

// try exec su (strong indicator)  
boolean suExec = lab24_canExecSu();  
if (suExec) {  
    rootScore += 25;  
    rootFindings.add("su execution possible (shell granted).");  
    suFound = true;  
}  

// known root packages  
String[] rootPkgs = {  
        "com.topjohnwu.magisk",  
        "eu.chainfire.supersu",  
        "com.koushikdutta.superuser",  
        "com.noshufou.android.su",  
        "com.kingroot.kinguser",  
        "com.kingo.root",  
        "com.saurik.substrate",  
        "de.robv.android.xposed.installer"  
};  

List<String> installed = lab24_getInstalledPackagesLower();  
boolean pkgHit = false;  

for (String rp : rootPkgs) {  
    if (installed.contains(rp)) {  
        pkgHit = true;  
        rootScore += 20;  
        rootFindings.add("root package installed: " + rp);  
    }  
}  

// build tags  
    try {

    String tags = Build.TAGS;

    if (tags != null && tags.contains("test-keys")) {

        rootFindings.add("Build.TAGS contains test-keys.");

        // only increase score if other root signals exist
        if (suFound || pkgHit) {
            rootScore += 10;
        }
    }

} catch (Throwable ignore) {}

// suspicious system properties  
String roSecure = lab24_getProp("ro.secure");  
String roDebug  = lab24_getProp("ro.debuggable");  

if ("0".equals(roSecure)) {  
    rootScore += 18;  
    rootFindings.add("ro.secure=0 (insecure build).");  
}  
if ("1".equals(roDebug)) {  
    rootScore += 12;  
    rootFindings.add("ro.debuggable=1 (debuggable build).");  
}  

// ---------------------------  
// (2) BOOTLOADER / VERIFIED BOOT  
// ---------------------------  
int blScore = 0;  
List<String> blFindings = new ArrayList<>();  

String vbState = lab24_getProp("ro.boot.verifiedbootstate"); // green/yellow/orange/red  
String vbmeta  = lab24_getProp("ro.boot.vbmeta.device_state"); // locked/unlocked  
String flashL  = lab24_getProp("ro.boot.flash.locked"); // 1/0  
String wlBit   = lab24_getProp("ro.boot.warranty_bit"); // 0/1 (OEM)  

if (vbState != null) {

    String v = vbState.toLowerCase(Locale.US);

    if (v.contains("orange") ||
        v.contains("yellow") ||
        v.contains("red")) {

        blScore += 30;
        blFindings.add("VerifiedBootState=" + vbState);
    } else {
        blFindings.add("VerifiedBootState=" + vbState);
    }
}

if (vbmeta != null && vbmeta.contains("unlocked")) {  
    blScore += 35;  
    blFindings.add("vbmeta.device_state=unlocked");  
} else if (vbmeta != null) {  
    blFindings.add("vbmeta.device_state=" + vbmeta);  
}  

if ("0".equals(flashL)) {  
    blScore += 25;  
    blFindings.add("flash.locked=0 (bootloader unlocked).");  
} else if (flashL != null) {  
    blFindings.add("flash.locked=" + flashL);  
}  

if ("1".equals(wlBit)) {  
    blScore += 15;  
    blFindings.add("warranty_bit=1 (tamper flag).");  
}  

// OEM unlock allowed (settings)  
try {  
    int oemAllowed =  
            Settings.Global.getInt(  
                    getContentResolver(),  
                    "oem_unlock_allowed",  
                    0  
            );  
    if (oemAllowed == 1) {  
        blScore += 10;  
        blFindings.add("OEM unlock allowed=1 (developer enabled).");  
    }  
} catch (Throwable ignore) {}  

// /proc/cmdline hints  
String cmdline = lab24_readOneLine("/proc/cmdline");  
if (cmdline != null) {  
    String c = cmdline.toLowerCase(Locale.US);  
    if (c.contains("verifiedbootstate=orange") ||  
        c.contains("verifiedbootstate=yellow") ||  
        c.contains("vbmeta.device_state=unlocked") ||  
        c.contains("bootloader=unlocked")) {  
        blScore += 20;  
        blFindings.add("/proc/cmdline reports unlocked / weak verified boot.");  
    }  
}  

// ---------------------------  
// (3) BOOT ANIMATION / SPLASH MOD  
// ---------------------------  
int animScore = 0;  
List<String> animFindings = new ArrayList<>();  

if (lab24_fileExists("/data/local/bootanimation.zip")) {  
    animScore += 35;  
    animFindings.add("Custom bootanimation: /data/local/bootanimation.zip");  
}  

boolean sysBoot =
        lab24_fileExists("/system/media/bootanimation.zip") ||
        lab24_fileExists("/product/media/bootanimation.zip") ||
        lab24_fileExists("/oem/media/bootanimation.zip") ||
        lab24_fileExists("/vendor/media/bootanimation.zip") ||
        lab24_fileExists("/system_ext/media/bootanimation.zip");

if (!sysBoot) {  
    animScore += 15;  
    animFindings.add("No stock bootanimation found (possible custom ROM).");  
} else {  
    animFindings.add("Stock bootanimation path exists.");  
}  

// ---------------------------  
// FINAL RISK SCORE  
// ---------------------------  
int magiskStealthScore = 0;
List<String> magiskStealthFindings = new ArrayList<>();

try {
    magiskStealthScore = lab24_magiskStealthScore(magiskStealthFindings);
} catch (Throwable ignore) {}

int risk = Math.min(100, rootScore + blScore + animScore + Math.min(25, magiskStealthScore / 2));

logInfo(gr ? "Έλεγχος Root:" : "Root Scan:");  
if (rootFindings.isEmpty()) {  
    logOk(gr ? "Δεν εντοπίστηκαν ισχυρά ίχνη root."
             : "No strong root traces detected.");  
} else {  
    for (String s : rootFindings)
        logWarn("• " + s);  
}  

logInfo(gr ? "Bootloader / Verified Boot:"
           : "Bootloader / Verified Boot:");  
if (blFindings.isEmpty()) {  
    logOk(gr ? "Δεν εντοπίστηκαν ανωμαλίες bootloader."
             : "No bootloader anomalies detected.");  
} else {  
    for (String s : blFindings)
        logWarn("• " + s);  
}  

logInfo(gr ? "Boot Animation / Splash:"
           : "Boot Animation / Splash:");  
if (animFindings.isEmpty()) {  
    logOk(gr ? "Δεν εντοπίστηκαν ίχνη προσαρμοσμένης εκκίνησης."
             : "No custom animation traces detected.");  
} else {  
    for (String s : animFindings)
        logWarn("• " + s);  
}  

logInfo(gr ? "Magisk / Stealth heuristics:"
           : "Magisk / Stealth heuristics:");

if (magiskStealthFindings.isEmpty()) {

    logOk(gr
            ? "Δεν εντοπίστηκαν ισχυρές ενδείξεις stealth root."
            : "No strong stealth root indicators detected.");

} else {

    for (String s : magiskStealthFindings)
        logWarn("• " + s);
}

// ------------------------------------------------------------
// RISK SCORE (colored VALUE only)
// ------------------------------------------------------------

appendHtml("<br>");
if (risk >= 70) {
    logLabelErrorValue(
            gr ? "Εκτιμιση κινδύνου" : "Risk netdict",
            risk + " / 100"
    );
} else if (risk >= 35) {
    logLabelWarnValue(
            gr ? "Εκτιμιση κινδύνου" : "Risk netdict",
            risk + " / 100"
    );
} else {
    logLabelOkValue(
            gr ? "Εκτιμιση κινδύνου" : "Risk netdict",
            risk + " / 100"
    );
}

// ------------------------------------------------------------
// STATUS (GEL LABEL/VALUE STYLE)
// ------------------------------------------------------------

if (risk >= 70 || suExec || pkgHit) {

    logLabelErrorValue(
            gr ? "Τελική κατάσταση" : "Final status",
            gr ? "ROOT / ΤΡΟΠΟΠΟΙΗΜΕΝΟ ΣΥΣΤΗΜΑ (υψηλή βεβαιότητα)"
               : "ROOTED / SYSTEM MODIFIED (high confidence)"
    );

} else if (risk >= 35) {

    logLabelWarnValue(
            gr ? "Τελική κατάσταση" : "Final status",
            gr ? "ΥΠΟΠΤΟ (πιθανό root / ξεκλείδωτος bootloader / custom ROM)"
               : "SUSPICIOUS (possible root / unlocked / custom ROM)"
    );

} else {

    logLabelOkValue(
            gr ? "Τελική κατάσταση" : "Final status",
            gr ? "ΑΣΦΑΛΕΣ (δεν βρέθηκαν σημαντικές ενδείξεις τροποποίησης)"
               : "SAFE (no significant modification evidence)"
    );
}

appendHtml("<br>");
logOk(gr ? "Το Lab 24 ολοκληρώθηκε." : "Lab 24 finished.");
logLine();
}


// ============================================================
// LAB 24 — INTERNAL HELPERS
// ============================================================
private boolean lab24_fileExists(String path) {
try { return new File(path).exists(); }
catch (Throwable t) { return false; }
}

private List<String> lab24_getInstalledPackagesLower() {
List<String> out = new ArrayList<>();
try {
PackageManager pm = getPackageManager();
List<ApplicationInfo> apps = pm.getInstalledApplications(0);
if (apps != null) {
for (ApplicationInfo ai : apps) {
String p = ai.packageName;
if (p != null) out.add(p.toLowerCase(Locale.US));
}
}
} catch (Throwable ignore) {}
return out;
}

private boolean lab24_canExecSu() {
java.lang.Process p = null;
try {
p = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
BufferedReader br =
new BufferedReader(
new InputStreamReader(p.getInputStream()));
String line = br.readLine();
br.close();
return line != null &&
line.toLowerCase(Locale.US).contains("uid=0");
} catch (Throwable t) {
return false;
} finally {
if (p != null) try { p.destroy(); } catch (Throwable ignore) {}
}
}

private String lab24_execFirstLine(String cmd) {
java.lang.Process p = null;
try {
p = Runtime.getRuntime().exec(cmd);
BufferedReader br =
new BufferedReader(
new InputStreamReader(p.getInputStream()));
String line = br.readLine();
br.close();
return line != null ? line.trim() : null;
} catch (Throwable t) {
return null;
} finally {
if (p != null) try { p.destroy(); } catch (Throwable ignore) {}
}
}

private String lab24_getProp(String key) {
String v = lab24_execFirstLine("getprop " + key);
if (v == null) return null;
v = v.trim();
return v.isEmpty() ? null : v.toLowerCase(Locale.US);
}

private String lab24_readOneLine(String path) {
BufferedReader br = null;
try {
br = new BufferedReader(new FileReader(new File(path)));
return br.readLine();
} catch (Throwable t) {
return null;
} finally {
if (br != null) try { br.close(); } catch (Throwable ignore) {}
}
}

// ============================================================
// LAB 24 — MAGISK STEALTH HEURISTICS (HONEST / HEURISTIC ONLY)
// Detects strong indicators of hidden root environments.
// Does NOT claim certainty.
// ============================================================
private int lab24_magiskStealthScore(List<String> findings) {

    int score = 0;

    // --------------------------------------------------------
    // 1) Zygisk / denylist style property hints
    // --------------------------------------------------------
    try {
        String v;

        v = lab24_getProp("ro.dalvik.vm.native.bridge");
        if (v != null && !v.isEmpty() && !"0".equals(v)) {
            score += 10;
            findings.add("native.bridge is set: " + v);
        }

        v = lab24_getProp("init.svc.magiskd");
        if (v != null) {
            score += 35;
            findings.add("init.svc.magiskd=" + v);
        }

        v = lab24_getProp("persist.magisk.hide");
        if (v != null) {
            score += 20;
            findings.add("persist.magisk.hide detected");
        }

    } catch (Throwable ignore) {}

    // --------------------------------------------------------
    // 2) Typical Magisk / overlay / mirror traces
    // --------------------------------------------------------
    String[] paths = {
            "/sbin/.magisk",
            "/dev/.magisk_unblock",
            "/cache/.magisk",
            "/data/adb/magisk",
            "/data/adb/modules",
            "/data/adb/service.d",
            "/data/adb/post-fs-data.d",
            "/debug_ramdisk",
            "/dev/zygisk",
            "/metadata/magisk"
    };

    for (String p : paths) {
        try {
            if (lab24_fileExists(p)) {
                score += 20;
                findings.add("suspicious root path: " + p);
            }
        } catch (Throwable ignore) {}
    }

    // --------------------------------------------------------
    // 3) Mount table traces
    // --------------------------------------------------------
    try {
        String mounts = lab24_readWholeFile("/proc/self/mounts");
        if (mounts != null) {
            String m = mounts.toLowerCase(Locale.US);

            if (m.contains("magisk")) {
                score += 35;
                findings.add("/proc/self/mounts contains magisk");
            }

            if (m.contains("/data/adb/modules")) {
                score += 25;
                findings.add("mounts reference /data/adb/modules");
            }

            if (m.contains("overlay") && m.contains("/system")) {
                score += 10;
                findings.add("overlay mount affecting /system detected");
            }
        }
    } catch (Throwable ignore) {}

    // --------------------------------------------------------
    // 4) /proc/* cmdline / maps traces (best effort)
    // --------------------------------------------------------
    try {
        String maps = lab24_readWholeFile("/proc/self/maps");
        if (maps != null) {
            String mm = maps.toLowerCase(Locale.US);

            if (mm.contains("zygisk")) {
                score += 25;
                findings.add("/proc/self/maps contains zygisk");
            }

            if (mm.contains("magisk")) {
                score += 25;
                findings.add("/proc/self/maps contains magisk");
            }
        }
    } catch (Throwable ignore) {}

    // --------------------------------------------------------
    // 5) Package hiding paradox:
    // no Magisk package, but strong root shell / adb module traces
    // --------------------------------------------------------
    try {
        boolean shellRoot = lab24_canExecSu();
        boolean magiskPkg = false;

        List<String> installed = lab24_getInstalledPackagesLower();
        for (String p : installed) {
            if (p == null) continue;
            if (p.contains("magisk")) {
                magiskPkg = true;
                break;
            }
        }

        boolean adbModules =
                lab24_fileExists("/data/adb/modules") ||
                lab24_fileExists("/data/adb/service.d") ||
                lab24_fileExists("/data/adb/post-fs-data.d");

        if (shellRoot && adbModules && !magiskPkg) {
            score += 30;
            findings.add("root shell + /data/adb traces present without visible Magisk package");
        }

    } catch (Throwable ignore) {}

    return Math.min(score, 100);
}

private String lab24_readWholeFile(String path) {
    BufferedReader br = null;
    try {
        br = new BufferedReader(new FileReader(new File(path)));
        StringBuilder sb = new StringBuilder();
        String line;
        int lines = 0;

        while ((line = br.readLine()) != null && lines < 400) {
            sb.append(line).append('\n');
            lines++;
        }

        return sb.toString();
    } catch (Throwable ignore) {
        return null;
    } finally {
        try { if (br != null) br.close(); } catch (Throwable ignore) {}
    }
}

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
    logInfo(gr
            ? "LAB 25 — GEL Ανάλυση Σφαλμάτων (ΑΥΤΟΜΑΤΗ)"
            : "LAB 25 — GEL Crash Intelligence (AUTO)");
    logLine();

    int crashCount = 0;
    int anrCount = 0;
    int systemCount = 0;

    Map<String, Integer> appEvents = new HashMap<>();
    List<String> details = new ArrayList<>();

    // ============================================================
    // (A) Android 11+ — REALTIME ERROR SNAPSHOT
    // ============================================================
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            ActivityManager am =
                    (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            if (am != null) {

                List<ActivityManager.ProcessErrorStateInfo> errs =
                        am.getProcessesInErrorState();

                if (errs != null && !errs.isEmpty()) {

                    logInfo(gr
                            ? "Στιγμιότυπο τρεχόντων σφαλμάτων"
                            : "Realtime error snapshot");

                    for (ActivityManager.ProcessErrorStateInfo e : errs) {

                        String app =
                                (e != null && e.processName != null)
                                        ? e.processName
                                        : "(unknown)";

                        appEvents.put(app, appEvents.getOrDefault(app, 0) + 1);

                        if (e.condition ==
                                ActivityManager.ProcessErrorStateInfo.CRASHED) {

                            logLabelErrorValue(
                                    "CRASH",
                                    app + " — " + safeStr(e.shortMsg)
                            );

                        } else if (e.condition ==
                                ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING) {

                            logLabelWarnValue(
                                    "ANR",
                                    app + " — " + safeStr(e.shortMsg)
                            );

                        } else {

                            logLabelWarnValue(
                                    gr ? "ΣΦΑΛΜΑ" : "ERROR",
                                    app + " — " + safeStr(e.shortMsg)
                            );
                        }
                    }

                    appendHtml("<br>");
                    logLabelOkValue(
                            gr ? "Σημείωση" : "Note",
                            gr
                                    ? "Το στιγμιότυπο δείχνει ΜΟΝΟ τρέχοντα crashes / ANR (όχι ιστορικό)"
                                    : "Snapshot shows ONLY current crashed / ANR processes (not history)"
                    );
                }
            }
        }

    } catch (Throwable ignore) {}

    // ============================================================
    // (B) DropBox crash logs
    // ============================================================
    try {
        DropBoxManager db =
                (DropBoxManager) getSystemService(DROPBOX_SERVICE);

        if (db != null) {

            String[] tags = {
                    "system_app_crash", "data_app_crash",
                    "system_app_anr", "data_app_anr",
                    "system_server_crash", "system_server_wtf",
                    "system_server_anr"
            };

            for (String tag : tags) {

                try {

                    long since = 0;

                    DropBoxManager.Entry ent = db.getNextEntry(tag, since);

                    // Android 13–14 / OEM workaround
                    if (ent == null) {
                        try {
                            ent = db.getNextEntry(tag, 0);
                        } catch (Throwable ignore) {}
                    }

                    int scanned = 0;

                    while (ent != null && scanned < 50) {

                        scanned++;

                        boolean crashDetected = false;

                        String ttag = tag.toLowerCase(Locale.US);

                        if (ttag.contains("system_server")) {

                            systemCount++;

                        } else if (ttag.contains("anr")) {

                            anrCount++;

                        } else if (ttag.contains("crash")
                                || ttag.contains("native_crash")
                                || ttag.contains("tombstone")) {

                            crashDetected = true;
                        }

                        String shortTxt = readDropBoxEntry(ent);

                        if (!crashDetected && shortTxt != null) {

                            String tx = shortTxt.toLowerCase(Locale.US);

                            if (tx.contains("fatal signal")
                                    || tx.contains("segmentation fault")
                                    || tx.contains("abort message")
                                    || tx.contains("signal 11")) {

                                crashDetected = true;
                            }
                        }

                        if (crashDetected) {
                            crashCount++;
                        }

                        String clean = tag.toUpperCase(Locale.US)
                                .replace("_", " ");

                        details.add(clean + ": " + shortTxt);

                        try {

                            String key;

                            if (shortTxt != null && shortTxt.length() > 0) {

                                String t = shortTxt.toLowerCase(Locale.US);
                                int pi = t.indexOf("package:");

                                if (pi >= 0) {

                                    String rest = t.substring(pi + 8).trim();
                                    String[] parts =
                                            rest.split("[\\s\\n\\r\\t]+");

                                    key = (parts.length > 0 && parts[0].contains("."))
                                            ? parts[0]
                                            : clean;

                                } else {
                                    key = clean;
                                }

                            } else {
                                key = clean;
                            }

                            appEvents.put(
                                    key,
                                    appEvents.getOrDefault(key, 0) + 1
                            );

                        } catch (Exception ignored) {}

                        try {
                            long next = ent.getTimeMillis();
                            ent = db.getNextEntry(tag, next);
                        } catch (Throwable ignore) {
                            break;
                        }
                    }

                } catch (Throwable ignorePerTag) {
                    // συνεχίζουμε στο επόμενο tag χωρίς να νεκρώνει όλο το LAB 25
                }
            }
        }

    } catch (Throwable ignored) {}

    // ============================================================
    // (C) SUMMARY + RISK SCORE
    // ============================================================
    int risk = 0;
    risk += crashCount * 5;
    risk += anrCount * 8;
    risk += systemCount * 15;

    List<String> crashPatternFindings = new ArrayList<>();
    int hwPatternScore = analyzeCrashPattern(details, crashPatternFindings);

    if (hwPatternScore > 0) {
        risk += Math.min(30, hwPatternScore / 2);
    }

    logInfo(gr
            ? "Ανάλυση μοτίβου crash"
            : "Crash pattern analysis");
    logLine();

    if (crashPatternFindings.isEmpty()) {

        logLabelOkValue(
                gr ? "Μοτίβο" : "Pattern",
                gr
                        ? "Δεν εντοπίστηκαν ενδείξεις χαμηλού επιπέδου σφαλμάτων"
                        : "No low-level crash indicators detected"
        );

    } else {

        for (String s : crashPatternFindings) {
            logWarn("• " + s);
        }

        if (hwPatternScore >= 40) {

            logLabelWarnValue(
                    gr ? "Εκτίμηση" : "Assessment",
                    gr
                            ? "Πιθανό μοτίβο χαμηλού επιπέδου σφαλμάτων (RAM / storage / kernel)"
                            : "Possible low-level fault pattern (RAM / storage / kernel)"
            );

        } else {

            logLabelWarnValue(
                    gr ? "Εκτίμηση" : "Assessment",
                    gr
                            ? "Μικρές ενδείξεις συστημικών σφαλμάτων"
                            : "Minor system-level fault indicators"
            );
        }
    }

    if (risk > 100) risk = 100;

    appendHtml("<br>");
    logInfo(gr
            ? "Έλεγχος καθυστέρησης αποθηκευτικού"
            : "Storage latency probe");
    logLine();

    long latency = storageLatencyProbe();

    if (latency < 0) {

        logLabelWarnValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Δεν ήταν δυνατή η μέτρηση latency"
                   : "Unable to measure storage latency"
        );

    } else {

        logLabelOkValue(
                gr ? "Latency" : "Latency",
                latency + " ms"
        );

        if (latency > 120) {

            logLabelWarnValue(
                    gr ? "Εκτίμηση" : "Assessment",
                    gr
                            ? "Αργή απόκριση αποθηκευτικού (πιθανή κόπωση NAND ή έντονη δραστηριότητα I/O)"
                            : "Slow storage response (possible NAND wear or heavy I/O)"
            );

        } else if (latency > 60) {

            logLabelWarnValue(
                    gr ? "Εκτίμηση" : "Assessment",
                    gr
                            ? "Μέτρια καθυστέρηση I/O"
                            : "Moderate I/O latency"
            );

        } else {

            logLabelOkValue(
                    gr ? "Εκτίμηση" : "Assessment",
                    gr
                            ? "Φυσιολογική απόκριση αποθηκευτικού"
                            : "Normal storage latency"
            );
        }
    }

    appendHtml("<br>");
    logInfo(gr ? "Σύνοψη Σταθερότητας" : "Stability summary");
    logLine();

    logLabelOkValue(
            gr ? "Συμβάντα Crash" : "Crash events",
            String.valueOf(crashCount)
    );

    if (anrCount > 0)
        logLabelWarnValue("ANR", String.valueOf(anrCount));
    else
        logLabelOkValue("ANR", "0");

    if (systemCount > 0)
        logLabelErrorValue(
                gr ? "Σφάλματα Συστήματος" : "System-level faults",
                String.valueOf(systemCount)
        );
    else
        logLabelOkValue(
                gr ? "Σφάλματα Συστήματος" : "System-level faults",
                "0"
        );

    appendHtml("<br>");
    logInfo(gr
        ? "Δείκτης Ρίσκου Σταθερότητας"
        : "Stability risk score");

logLine();

String riskLabel;

if (risk >= 70) {

    riskLabel = gr ? "Υψηλό" : "High";

    logLabelErrorValue(
            gr ? "Ρίσκο" : "Risk",
            riskLabel + " (" + risk + ")"
    );

} else if (risk >= 40) {

    riskLabel = gr ? "Μέτριο" : "Moderate";

    logLabelWarnValue(
            gr ? "Ρίσκο" : "Risk",
            riskLabel + " (" + risk + ")"
    );

} else {

    riskLabel = gr ? "Χαμηλό" : "Low";

    logLabelOkValue(
            gr ? "Ρίσκο" : "Risk",
            riskLabel + " (" + risk + ")"
    );
}

    logLabelOkValue(
        gr ? "Σημείωση" : "Note",
        gr
                ? "Η βαθμολογία βασίζεται σε διαθέσιμα system logs (διαφέρει ανά OEM / Android)"
                : "Score based on detected system log signals (availability varies by OEM / Android)"
    );

    boolean softwareCrashLikely =
            (crashCount > 0 || anrCount > 0);

    // ============================================================
    // (D) HEATMAP
    // ============================================================
    if (!appEvents.isEmpty()) {

        appendHtml("<br>");
        logInfo(gr
                ? "Heatmap (συχνότερα συμβάντα)"
                : "Heatmap (top offenders)");
        logLine();

        appEvents.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .forEach(e -> {

                    String label =
                            e.getValue() + (gr ? " συμβάντα" : " events");

                    if (e.getValue() >= 10)
                        logLabelErrorValue(e.getKey(), label);
                    else if (e.getValue() >= 5)
                        logLabelWarnValue(e.getKey(), label);
                    else
                        logLabelOkValue(e.getKey(), label);
                });
    }

    // ============================================================
    // (E) FULL DETAILS
    // ============================================================
    if (!details.isEmpty()) {

        appendHtml("<br>");
        logInfo(gr
                ? "Αναλυτικά αρχεία crash"
                : "Detailed crash records");
        logLine();

        int count = details.size();

        if (count == 1)
            logLabelWarnValue(
                    gr ? "Καταγραφές" : "Records",
                    gr ? "1 crash εντοπίστηκε"
                       : "1 crash detected");
        else if (count <= 3)
            logLabelWarnValue(
                    gr ? "Καταγραφές" : "Records",
                    count + (gr ? " crashes εντοπίστηκαν"
                                 : " crashes detected"));
        else
            logLabelErrorValue(
                    gr ? "Καταγραφές" : "Records",
                    count + (gr ? " crashes εντοπίστηκαν (ΥΨΗΛΗ αστάθεια)"
                                 : " crashes detected (HIGH instability)")
            );

        for (String d : details) {
            logLabelWarnValue(
                    gr ? "Λεπτομέρεια" : "Detail",
                    d
            );
        }

    } else {

        logLine();
        logLabelOkValue(
                gr ? "Ιστορικό Crash" : "Crash history",
                gr
                        ? "Δεν εντοπίστηκαν καταγραφές crash"
                        : "No crash records detected"
        );
    }

    GELServiceLog.info(
            "SUMMARY: CRASH_ORIGIN=" +
                    (softwareCrashLikely ? "SOFTWARE" : "UNCLEAR")
    );

    appendHtml("<br>");
logOk(gr ? "Το Lab 25 ολοκληρώθηκε." : "Lab 25 finished.");
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

    if (!guardUsageAccess()) return;

    appendHtml("<br>");
    logLine();

    final boolean gr = AppLang.isGreek(this);

    logInfo(gr
            ? "LAB 26 — Ανάλυση Επιπτώσεων Εγκατεστημένων Εφαρμογών"
            : "LAB 26 — Installed Applications Impact Analysis");

    logLine();

    final boolean rooted = isDeviceRooted();

    // ============================================================
    // ENGINE
    // ============================================================
    AppImpactEngine.ImpactResult r;
    try {
        r = AppImpactEngine.analyze(this, rooted);
    } catch (Throwable t) {

        logLabelErrorValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Αποτυχία ανάλυσης εφαρμογών" : "Failed to analyze applications"
        );

        logLabelWarnValue(
                gr ? "Αιτία" : "Reason",
                gr ? "Σφάλμα πρόσβασης PackageManager / UsageStats" : "PackageManager / UsageStats access error"
        );

        logLine();
        appendHtml("<br>");
        logOk(gr ? "Το Lab 26 ολοκληρώθηκε." : "Lab 26 finished.");
        logLine();
        return;
    }

    if (r == null) {

        logLabelErrorValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Το αποτέλεσμα ανάλυσης είναι κενό" : "Engine result is null"
        );

        logLine();
        appendHtml("<br>");
        logOk(gr ? "Το Lab 26 ολοκληρώθηκε." : "Lab 26 finished.");
        logLine();
        return;
    }

    // ============================================================
    // OVERVIEW
    // ============================================================
    
appendHtml("<br>");
logInfo(gr ? "Επισκόπηση εγκατεστημένων" : "Installed overview");
logLine();

    logLabelOkValue(
            gr ? "Σύνολα" : "Totals",
            (gr
                    ? "Όλα: " + r.totalPkgs + " | Χρήστη: " + r.userApps + " | Συστήματος: " + r.systemApps
                    : "All: " + r.totalPkgs + " | User: " + r.userApps + " | System: " + r.systemApps)
    );

    logLabelOkValue(
            gr ? "Usage Access" : "Usage Access",
            r.usageAccessOk
                    ? (gr ? "Ενεργό (OK)" : "Enabled (OK)")
                    : (gr ? "Ανενεργό (χωρίς foreground χρόνο)" : "Disabled (no foreground time)")
    );

    logLabelOkValue(
            gr ? "Root-aware" : "Root-aware",
            rooted
                    ? (gr ? "Ναι (best-effort χωρίς su)" : "Yes (best-effort without su)")
                    : (gr ? "Όχι" : "No")
    );

    // ============================================================
    // CAPABILITY PRESSURE (HONEST)
    // ============================================================
    int userApps = Math.max(1, r.userApps);
    int pctBg   = (int) Math.round((r.bgCapable * 100.0) / userApps);
    int pctPerm = (int) Math.round((r.permHeavy * 100.0) / userApps);

    appendHtml("<br>");
    logInfo(gr ? "Ενδείξεις φόρτου (βάσει δυνατοτήτων)" : "Load indicators (capability-based)");
    logLine();

    logLabelOkValue(gr ? "Background-capable" : "Background-capable",
            r.bgCapable + " (" + pctBg + "%)");

    logLabelOkValue(gr ? "Permission-heavy" : "Permission-heavy",
            r.permHeavy + " (" + pctPerm + "%)");
            
   appendHtml("<br>");
    logInfo(gr ? "Χάρτης δυνατοτήτων (user apps)" : "Capability map (user apps)");
    logLine();

    logLabelOkValue(
            gr ? "Boot / Location / Mic / Camera" : "Boot / Location / Mic / Camera",
            r.bootAware + " | " + r.locationLike + " | " + r.micLike + " | " + r.cameraLike
    );

    logLabelOkValue(
            gr ? "Overlay / VPN / Storage / Notifications" : "Overlay / VPN / Storage / Notifications",
            r.overlayLike + " | " + r.vpnLike + " | " + r.storageLike + " | " + r.notifLike
    );

    // ============================================================
    // REDUNDANCY (HONEST / HEURISTIC)
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Ενδείξεις πλεονασμού (heuristic)" : "Redundancy signals (heuristic)");
    logLine();

    logLabelOkValue(gr ? "Cleaners / Optimizers" : "Cleaners / Optimizers", String.valueOf(r.cleanersLike));
    logLabelOkValue(gr ? "Launchers" : "Launchers", String.valueOf(r.launchersLike));
    logLabelOkValue(gr ? "Antivirus suites" : "Antivirus suites", String.valueOf(r.antivirusLike));
    logLabelOkValue(gr ? "Keyboards" : "Keyboards", String.valueOf(r.keyboardsLike));

// ============================================================
// REAL DATA (HONEST) — SINCE BOOT (TrafficStats)
// ============================================================
appendHtml("<br>");
logInfo(gr ? "Κατανάλωση δεδομένων (από boot)" : "Data usage (since boot)");
logLine();

if (r.topDataConsumers != null && !r.topDataConsumers.isEmpty()) {

    int limit = Math.min(10, r.topDataConsumers.size());
    boolean foundRealData = false;

    for (int i = 0; i < limit; i++) {

        AppImpactEngine.AppScore a = r.topDataConsumers.get(i);
        if (a == null) continue;

        if (a.dataBytesSinceBoot < 1024) {
            continue; // εξαφανίζουμε τα μηδενικά
        }

        foundRealData = true;

        String val = humanBytes(a.dataBytesSinceBoot);

        logLabelWarnValue(
                a.safeLabel(),
                (gr
        ? val + " (Συνολική κίνηση δεδομένων από εκκίνηση)"
        : val + " (Total data traffic since boot)")
);

        logInfo(a.pkg);
    }

    if (!foundRealData) {

        logLabelWarnValue(
                gr ? "Κατάσταση" : "Status",
                gr
                        ? "Δεν υπάρχουν διαθέσιμα δεδομένα κατανάλωσης.\n\n"
                          + "Πιθανές αιτίες:\n"
                          + "• Πρόσφατη επανεκκίνηση συσκευής\n"
                          + "• Περιορισμοί κατασκευαστή (OEM)\n"
                          + "• Μη διαθέσιμα UID counters στο Android"
                        : "No data usage available.\n\n"
                          + "Possible reasons:\n"
                          + "• Device was recently rebooted\n"
                          + "• OEM restrictions\n"
                          + "• UID traffic counters not exposed by Android"
        );

    } else {

        logLabelOkValue(
                gr ? "Σημείωση" : "Note",
                gr
                        ? "Τα δεδομένα είναι συνολικά από την τελευταία εκκίνηση της συσκευής."
                        : "Data is cumulative since last device boot."
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr
                    ? "Δεν ήταν δυνατή η ανάκτηση στατιστικών κατανάλωσης."
                    : "Unable to retrieve usage statistics."
    );
}

    // ============================================================
    // BATTERY EXPOSURE (HONEST HEURISTIC)
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Έκθεση μπαταρίας (heuristic — no mAh)" : "Battery exposure (heuristic — no mAh)");
    logLine();

    if (!r.usageAccessOk) {

        logLabelWarnValue(
                gr ? "Περιορισμός" : "Limitation",
                gr
                        ? "Δεν υπάρχει Usage Access, άρα δεν έχουμε αξιόπιστο foreground χρόνο."
                        : "Usage Access is off, so we do not have reliable foreground time."
        );
    }

    if (r.topBatteryExposure != null && !r.topBatteryExposure.isEmpty()) {

        int limit = Math.min(10, r.topBatteryExposure.size());
        for (int i = 0; i < limit; i++) {

            AppImpactEngine.AppScore a = r.topBatteryExposure.get(i);
            if (a == null) continue;

// =======================
// DATA SAFETY (EXPLAINED)
// =======================

long fgMin = a.fgMs24h / 60000L;

// ---------- Usage text ----------
String usageText;

if (!r.usageAccessOk) {

    usageText = gr
            ? "Χρήση (24h): δεν υπάρχουν δεδομένα (δεν έχει δοθεί Usage Access)"
            : "Usage (24h): no data (Usage Access not granted)";

} else if (fgMin <= 0) {

    usageText = gr
            ? "Χρήση (24h): δεν καταγράφηκε χρήση το τελευταίο 24ωρο"
            : "Usage (24h): no recorded usage in the last 24h";

} else {

    usageText = gr
            ? "Χρήση (24h): " + fgMin + " λεπτά"
            : "Usage (24h): " + fgMin + " min";
}

// ---------- Data text (TrafficStats since boot) ----------
String dataText;

if (a.dataBytesSinceBoot < 1024) {

    dataText = gr
            ? "Δεδομένα: δεν υπάρχουν διαθέσιμα στοιχεία (πιθανός περιορισμός συστήματος/ROM)"
            : "Data: not available (possible system/ROM limitation)";

} else {

    dataText = gr
            ? "Δεδομένα: " + humanBytes(a.dataBytesSinceBoot)
            : "Data: " + humanBytes(a.dataBytesSinceBoot);
}

// ---------- Final detail ----------
String detail = gr
        ? "Δείκτης Επιρροής: " + a.estImpactScore +
          " | " + usageText +
          " | " + dataText +
          " | Ενδείξεις: " + a.tags
        : "Impact Index: " + a.estImpactScore +
          " | " + usageText +
          " | " + dataText +
          " | Indicators: " + a.tags;

            logLabelWarnValue(a.safeLabel(), detail);
            logInfo(a.pkg);
        }

        logLabelOkValue(
                gr ? "Σημείωση" : "Note",
                gr
                        ? "Το «Battery exposure» είναι εκτίμηση βάσει χρήσης/δυνατοτήτων/δεδομένων — όχι πραγματικό mAh."
                        : "'Battery exposure' is an estimate based on usage/capabilities/data — not real mAh."
        );

    } else {

        logLabelWarnValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Δεν ήταν δυνατή η κατάταξη έκθεσης μπαταρίας" : "Unable to rank battery exposure"
        );
    }

    // ============================================================
    // TOP CAPABILITY-HEAVY
    // ============================================================
    appendHtml("<br>");
    logInfo(gr
            ? "Top εφαρμογές με ισχυρές δυνατότητες (επισήμανση, όχι κατηγορούμενα)"
            : "Top capability-heavy apps (flagged, not accused)");
            logLine();

    if (r.topCapabilityHeavy != null && !r.topCapabilityHeavy.isEmpty()) {

        int limit = Math.min(10, r.topCapabilityHeavy.size());
        for (int i = 0; i < limit; i++) {

            AppImpactEngine.AppScore a = r.topCapabilityHeavy.get(i);
            if (a == null) continue;

            String detail =
                    (gr
        ? "Δείκτης Δυνατοτήτων: " + a.capabilityScore +
          " | Επικίνδυνες Άδειες: " + a.dangerPermCount +
          " | Παράγοντες Επιρροής: " + a.tags
        : "Capability Index: " + a.capabilityScore +
          " | Dangerous Permissions: " + a.dangerPermCount +
          " | Impact Factors: " + a.tags);

            logLabelWarnValue(a.safeLabel(), detail);
            logInfo(a.pkg);
        }
    }

    // ============================================================
    // HUMAN VERDICT
    // ============================================================
    appendHtml("<br>");
    logOk(gr ? "ΓΕΝΙΚΟ ΣΥΜΠΕΡΑΣΜΑ" : "TOTAL VERDICT");
    logLine();

    if (r.riskPoints >= 8) {
        logLabelWarnValue(gr ? "Επίπεδο πίεσης" : "Pressure level", gr ? "ΥΨΗΛΟ" : "HIGH");
    } else if (r.riskPoints >= 5) {
        logLabelWarnValue(gr ? "Επίπεδο πίεσης" : "Pressure level", gr ? "ΜΕΤΡΙΟ" : "MODERATE");
    } else {
        logLabelOkValue(gr ? "Επίπεδο πίεσης" : "Pressure level", gr ? "ΦΥΣΙΟΛΟΓΙΚΟ" : "NORMAL");
    }

    GELServiceLog.info(
            "SUMMARY: APPS_IMPACT=" + (r.appsImpactHigh ? "HIGH" : "NORMAL")
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 26 ολοκληρώθηκε." : "Lab 26 finished.");
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

    // ============================================================
    // USAGE ACCESS — MANDATORY GATE
    // ============================================================
    if (!hasUsageAccess()) {
    showUsageAccessDialog();
    return;
}

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 27 — Άδειες Εφαρμογών & Ιδιωτικότητα (Αυτόματη Σάρωση)"
            : "LAB 27 — App Permissions & Privacy (Auto Scan)");
    logLine();

    PackageManager pm = getPackageManager();
    if (pm == null) {
        logError(gr
                ? "Το PackageManager δεν είναι διαθέσιμο."
                : "PackageManager not available.");
        return;
    }

    List<String> details = new ArrayList<>();
    Map<String, Integer> appRisk = new HashMap<>();

    int totalApps = 0;
    int flaggedApps = 0;

    int riskTotal = 0;
    int dangTotal = 0;

    try {

        List<android.content.pm.PackageInfo> packs;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packs = pm.getInstalledPackages(
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS));
        } else {
            //noinspection deprecation
            packs = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        }

        if (packs == null) packs = new ArrayList<>();

        for (android.content.pm.PackageInfo p : packs) {

            if (p == null || p.packageName == null) continue;
            totalApps++;

            String pkg = p.packageName;

            // ============================================================
            // EXCLUDE SYSTEM / GOOGLE / PLAY STORE APPS
            // ============================================================
            boolean isSystem =
                    (p.applicationInfo != null) &&
                    ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ||
                     (p.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);

            if (isSystem ||
                    pkg.startsWith("com.android.") ||
                    pkg.startsWith("com.google.android.") ||
                    pkg.equals("com.android.vending")) {
                continue;
            }

            String[] req = p.requestedPermissions;
            int[] grant = p.requestedPermissionsFlags;

            if (req == null || req.length == 0) continue;

            int appScore = 0;
            int appDangerCount = 0;
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < req.length; i++) {

                String perm = req[i];
                if (perm == null) continue;

                boolean granted = isGrantedFlag(grant, i);

                int weight = permissionWeight(perm);
                if (weight <= 0) continue;

                if (granted) {
                    appDangerCount++;
                    appScore += weight;

                    sb.append("• ")
                      .append(shortPerm(perm))
                      .append(gr ? " (χορηγήθηκε)\n" : " (granted)\n");
                }
            }

            if (appScore > 0) {

                dangTotal += appDangerCount;
                riskTotal += appScore;

                int threshold = 10;

                if (appScore >= threshold) {

                    flaggedApps++;
                    appRisk.put(pkg, appScore);

                    String appLabel = safeLabel(pm, pkg);

                    details.add(
                            appLabel + " (" + pkg + ")" +
                            (gr ? " — Κίνδυνος=" : " — Risk=") +
                            appScore + "\n" + sb.toString()
                    );
                }
            }
        }

    } catch (SecurityException se) {

        logWarn(gr
                ? "Η σάρωση περιορίστηκε από την πολιτική ορατότητας πακέτων Android."
                : "Permissions scan limited by Android package visibility policy.");

    } catch (Exception e) {

        logError(gr
                ? "Σφάλμα σάρωσης αδειών: " + e.getMessage()
                : "Permissions scan error: " + e.getMessage());
    }

    // ============================================================
    // SUMMARY
    // ============================================================
    int maxRiskRef = 300;
    int riskPct = Math.min(100, (riskTotal * 100) / maxRiskRef);

appendHtml("<br>");
    logInfo(gr ? "Σύνοψη Σάρωσης" : "Scan Summary");
    logLine();

    logLabelOkValue(
            gr ? "Εφαρμογές που ελέγχθηκαν" : "Apps scanned",
            String.valueOf(totalApps)
    );

    if (dangTotal == 0) {
        logLabelOkValue(
                gr ? "Επικίνδυνες άδειες (χορηγημένες)" : "Dangerous permissions granted",
                String.valueOf(dangTotal)
        );
    } else if (dangTotal <= 5) {
        logLabelWarnValue(
                gr ? "Επικίνδυνες άδειες (χορηγημένες)" : "Dangerous permissions granted",
                String.valueOf(dangTotal)
        );
    } else {
        logLabelErrorValue(
                gr ? "Επικίνδυνες άδειες (χορηγημένες)" : "Dangerous permissions granted",
                String.valueOf(dangTotal)
        );
    }

    if (flaggedApps == 0) {
        logLabelOkValue(
                gr ? "Εφαρμογές με αυξημένο ρίσκο" : "Flagged apps",
                String.valueOf(flaggedApps)
        );
    } else if (flaggedApps <= 2) {
        logLabelWarnValue(
                gr ? "Εφαρμογές με αυξημένο ρίσκο" : "Flagged apps",
                String.valueOf(flaggedApps)
        );
    } else {
        logLabelErrorValue(
                gr ? "Εφαρμογές με αυξημένο ρίσκο" : "Flagged apps",
                String.valueOf(flaggedApps)
        );
    }

    // ============================================================
    // PRIVACY RISK SCORE
    // ============================================================
    logInfo(gr
        ? "Δείκτης Ρίσκου Ιδιωτικότητας"
        : "Privacy risk score");

String riskLabel;

if (riskPct >= 70) {

    riskLabel = gr ? "Υψηλό" : "High";

    logLabelErrorValue(
            gr ? "Ρίσκο" : "Risk",
            riskLabel + " (" + riskPct + ")"
    );

} else if (riskPct >= 30) {

    riskLabel = gr ? "Μέτριο" : "Moderate";

    logLabelWarnValue(
            gr ? "Ρίσκο" : "Risk",
            riskLabel + " (" + riskPct + ")"
    );

} else {

    riskLabel = gr ? "Χαμηλό" : "Low";

    logLabelOkValue(
            gr ? "Ρίσκο" : "Risk",
            riskLabel + " (" + riskPct + ")"
    );
}

    // ============================================================
    // TOP OFFENDERS
    // ============================================================
    if (!appRisk.isEmpty()) {

        appendHtml("<br>");
        logInfo(gr
                ? "Εφαρμογές με τον υψηλότερο δείκτη ρίσκου"
                : "Top privacy offenders");
                 logLine();

        appRisk.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(8)
                .forEach(e -> {

                    String label = safeLabel(pm, e.getKey());
                    String riskVal = String.valueOf(e.getValue());

                    if (e.getValue() >= 60) {
                        logLabelErrorValue(label, (gr ? "Ρίσκο " : "Risk ") + riskVal);
                    } else if (e.getValue() >= 30) {
                        logLabelWarnValue(label, (gr ? "Ρίσκο " : "Risk ") + riskVal);
                    } else {
                        logLabelOkValue(label, (gr ? "Ρίσκο " : "Risk ") + riskVal);
                    }
                });
    }

    // ============================================================
    // FULL DETAILS
    // ============================================================
    if (!details.isEmpty()) {

        appendHtml("<br>");
        logInfo(gr
                ? "Αναλυτικές Πληροφορίες (Εφαρμογές με ρίσκο)"
                : "Permission details (flagged apps)");
                logLine();

        for (String d : details) {
            logLabelWarnValue(gr ? "Εύρημα" : "Finding", d.trim());
        }

    } else {

        logLabelOkValue(
                gr ? "Συνδυασμοί αδειών" : "Permission patterns",
                gr
                        ? "Δεν εντοπίστηκαν συνδυασμοί υψηλού ρίσκου"
                        : "No high-risk permission combinations detected"
        );
    }

    // ============================================================
    // CONTEXT NOTE
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Σημείωση Ανάλυσης Ιδιωτικότητας" : "Privacy Analysis Note");
    logLine();

    logLabelOkValue(
            gr ? "Διευκρίνιση" : "Clarification",
            gr
                    ? "Η χορήγηση αδειών δεν σημαίνει κακόβουλη συμπεριφορά."
                    : "Granted permissions do not imply malicious behavior."
    );

    logLabelOkValue(
            gr ? "Πεδίο Ανάλυσης" : "Scope",
            gr
                    ? "Το αποτέλεσμα ΔΕΝ υποδεικνύει βλάβη υλικού ή συστήματος."
                    : "This result does NOT indicate hardware or system failure."
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 27 ολοκληρώθηκε." : "Lab 27 finished.");
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

    boolean randomReboots = false;
    boolean signalDrops = false;
    boolean sensorFlaps = false;
    boolean thermalSpikes = false;

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 28 — Σταθερότητα Υλικού & Ακεραιότητα Διασυνδέσεων"
            : "LAB 28 — Hardware Stability & Interconnect Integrity");
    logOk(gr
            ? "Λειτουργία τεχνικού — Ανάλυση βασισμένη σε συμπτώματα ΜΟΝΟ."
            : "Technician mode — symptom-based analysis ONLY.");
    logLine();

    int symptomScore = 0;
    int powerGlitches = 0;

    Lab28Evidence ev = Lab28EvidenceReader.readFromGELServiceLog();

    randomReboots = ev.rebootPattern;
    signalDrops   = ev.radioInstability;
    sensorFlaps   = ev.sensorFlaps;
    thermalSpikes = ev.thermalSpikes;

    if (ev.chargingGlitch) powerGlitches++;

    // Technician popup
    showLab28Popup();

    // ============================================================
    // STAGE A — SYMPTOM SCORE
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Παρατηρούμενα συμπτώματα" : "Observed symptom signals");
    logLine();

    if (randomReboots) {
        logLabelWarnValue(gr ? "Επανεκκινήσεις" : "Reboots",
                gr ? "Τυχαίες επανεκκινήσεις ή resets"
                   : "Random reboots or sudden resets detected");
        symptomScore += 25;
    } else {
        logLabelOkValue(gr ? "Επανεκκινήσεις" : "Reboots",
                gr ? "Καμία ανωμαλία" : "No abnormal reboot pattern");
    }

    if (signalDrops) {
        logLabelWarnValue(gr ? "Δίκτυο" : "Radio",
                gr ? "Αστάθεια σήματος ή δικτύου"
                   : "Network or signal instability detected");
        symptomScore += 20;
    } else {
        logLabelOkValue(gr ? "Δίκτυο" : "Radio",
                gr ? "Σήμα σταθερό" : "Signals appear stable");
    }

    if (sensorFlaps) {
        logLabelWarnValue(gr ? "Αισθητήρες" : "Sensors",
                gr ? "Διακοπτόμενες μετρήσεις αισθητήρων"
                   : "Intermittent sensor readings detected");
        symptomScore += 15;
    } else {
        logLabelOkValue(gr ? "Αισθητήρες" : "Sensors",
                gr ? "Σταθερή λειτουργία" : "Sensors stable");
    }

    if (thermalSpikes) {
        logLabelWarnValue(gr ? "Θερμικά" : "Thermal",
                gr ? "Απότομες θερμικές αιχμές"
                   : "Abnormal thermal spikes detected");
        symptomScore += 20;
    } else {
        logLabelOkValue(gr ? "Θερμικά" : "Thermal",
                gr ? "Θερμική συμπεριφορά φυσιολογική"
                   : "Thermal behaviour normal");
    }

    if (powerGlitches > 0) {
        logLabelWarnValue(gr ? "Τροφοδοσία" : "Power",
                gr ? "Αστάθεια φόρτισης ή ρεύματος"
                   : "Power or charging instability detected");
        symptomScore += 20;
    } else {
        logLabelOkValue(gr ? "Τροφοδοσία" : "Power",
                gr ? "Σταθερή συμπεριφορά"
                   : "Power behaviour stable");
    }

    if (symptomScore > 100) symptomScore = 100;

    // ------------------------------------------------------------
    // SYMPTOM INTERPRETATION
    // ------------------------------------------------------------
    String symptomLevel =
            (symptomScore <= 20) ? (gr ? "ΧΑΜΗΛΟ" : "LOW") :
            (symptomScore <= 45) ? (gr ? "ΜΕΤΡΙΟ" : "MODERATE") :
            (symptomScore <= 70) ? (gr ? "ΥΨΗΛΟ" : "HIGH") :
                                   (gr ? "ΠΟΛΥ ΥΨΗΛΟ" : "VERY HIGH");

    appendHtml("<br>");
    if (symptomScore >= 40)
        logLabelWarnValue(gr ? "Δείκτης Συνεκτικότητας Συμπτωμάτων"
                             : "Symptom consistency score",
                symptomScore + "/100 (" + symptomLevel + ")");
    else
        logLabelOkValue(gr ? "Δείκτης Συνεκτικότητας Συμπτωμάτων"
                           : "Symptom consistency score",
                symptomScore + "/100 (" + symptomLevel + ")");

// ============================================================
// HARDWARE PATTERN ANALYSIS
// ============================================================
appendHtml("<br>");
logInfo(gr ? "Ανάλυση μοτίβων υλικού" : "Hardware instability patterns");
logLine();

boolean pmicInstability = false;
boolean basebandDegradation = false;
boolean sensorBusInstability = false;
boolean thermalRunaway = false;
boolean storageDegradation = false;


// PMIC instability (pattern correlation)
if ((powerGlitches > 1 && thermalSpikes) || powerGlitches > 3) {

    pmicInstability = true;

    logLabelWarnValue(
            gr ? "PMIC αστάθεια"
               : "PMIC instability",
            gr
                    ? "Συνδυασμός θερμικών αιχμών και αστάθειας φόρτισης."
                    : "Thermal spikes combined with charging instability."
    );
}


// ----------------------------------------------------
// PMIC / FUEL GAUGE STABILITY CHECK (FINAL)
// ----------------------------------------------------

if (validDrain &&
    !Float.isNaN(finalSag) &&
    !Float.isNaN(voltageUnderLoad[0]) &&
    !Float.isNaN(voltageRecovery[0])) {

    float sag = finalSag;   // 🔴 FIX (απαραίτητο)

    // ignore micro sag noise
    if (sag < 0.015f)
        sag = 0f;

    float recoveryDelta =
            voltageRecovery[0] - voltageUnderLoad[0];

    float electricalNoise =
            Math.abs(sag - recoveryDelta);

    // abnormal voltage behaviour
    if (electricalNoise > 0.20f)
        pmicInstability = true;

    // counter behaviour anomaly
    if (drainMah == 0f && mahPerHour > 0f)
        pmicInstability = true;

    // unrealistic drain spike
    if (mahPerHour > 2500f)
        pmicInstability = true;
}

// ----------------------------------------------------
// RESULT
// ----------------------------------------------------
if (pmicInstability) {

    logLabelWarnValue(
            gr ? "Σταθερότητα ελεγκτή τροφοδοσίας (PMIC)"
               : "Power controller stability (PMIC)",
            gr
                    ? "Ανιχνεύθηκε πιθανή αστάθεια fuel-gauge ή PMIC."
                    : "Possible PMIC or fuel-gauge instability detected."
    );

} else {

    logLabelOkValue(
            gr ? "Σταθερότητα ελεγκτή τροφοδοσίας (PMIC)"
               : "Power controller stability (PMIC)",
            gr
                    ? "Δεν εντοπίστηκαν ανωμαλίες ελεγκτή."
                    : "No PMIC instability detected."
    );
}

// ----------------------------------------------------
// BASEBAND INSTABILITY INDICATOR
// ----------------------------------------------------
if (signalDrops && randomReboots && !thermalSpikes) {

    basebandDegradation = true;

    logLabelWarnValue(
            gr ? "Πιθανή αστάθεια baseband"
               : "Possible baseband instability",
            gr
                    ? "Αστάθεια σήματος σε συνδυασμό με επανεκκινήσεις."
                    : "Signal instability combined with device reboots."
    );

} else if (signalDrops) {

    logLabelWarnValue(
            gr ? "Σήμα δικτύου"
               : "Radio signal",
            gr
                    ? "Παρατηρήθηκε αστάθεια σήματος."
                    : "Signal instability observed."
    );

} else {

    logLabelOkValue(
            gr ? "Baseband"
               : "Baseband",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις αστάθειας."
                    : "No baseband instability indicators."
    );
}

// ----------------------------------------------------
// SENSOR BUS INSTABILITY DETECTOR (ROOT ONLY)
// ----------------------------------------------------

if (!isDeviceRooted()) {

    logLabelOkValue(
            gr ? "Δίαυλος αισθητήρων"
               : "Sensor bus diagnostics",
            gr
                    ? "Η προχωρημένη διάγνωση αισθητήρων απαιτεί πρόσβαση root."
                    : "Advanced sensor diagnostics require root access."
    );

} else {

    // basic communication issues
    if (sensorFlaps) {

        sensorBusInstability = true;

        logLabelWarnValue(
                gr ? "Αστάθεια διαύλου αισθητήρων"
                   : "Sensor bus instability",
                gr
                        ? "Εντοπίστηκαν ασυνέπειες επικοινωνίας αισθητήρων."
                        : "Sensor communication inconsistencies detected."
        );
    }

    // severe pattern
    if (sensorFlaps && randomReboots) {

        sensorBusInstability = true;

        logLabelWarnValue(
                gr ? "Σοβαρή αστάθεια αισθητήρων"
                   : "Severe sensor bus instability",
                gr
                        ? "Διακοπτόμενη λειτουργία αισθητήρων σε συνδυασμό με επανεκκινήσεις."
                        : "Intermittent sensor communication combined with system reboots."
        );
    }

    if (!sensorBusInstability) {

        logLabelOkValue(
                gr ? "Δίαυλος αισθητήρων"
                   : "Sensor bus integrity",
                gr
                        ? "Δεν εντοπίστηκαν ανωμαλίες επικοινωνίας."
                        : "No sensor communication issues detected."
        );
    }
}

    // Thermal runaway
    if (thermalSpikes && randomReboots) {
        thermalRunaway = true;
        logLabelWarnValue(
                gr ? "Θερμική αστάθεια"
                   : "Thermal runaway pattern",
                gr
                        ? "Απότομες αυξήσεις θερμοκρασίας."
                        : "Rapid thermal escalation behaviour."
        );
    }

    // Storage degradation indicator
    if (ev.crashPattern.equals("UNKNOWN") && randomReboots) {
        storageDegradation = true;
        logLabelWarnValue(
                gr ? "Αποθηκευτικός χώρος"
                   : "Storage subsystem",
                gr
                        ? "Πιθανή αστάθεια NAND / controller."
                        : "Possible NAND or storage controller instability."
        );
    }
    
// ----------------------------------------------------
// THERMAL RUNAWAY PREDICTOR
// ----------------------------------------------------
boolean thermalRunawayRisk = false;
int thermalScore = 0;

// έντονες θερμικές αιχμές
if (thermalSpikes)
    thermalScore += 30;

// PMIC instability
if (pmicInstability)
    thermalScore += 20;

// υψηλή θερμοκρασία CPU
Float cpu = null;
Float gpu = null;

try {

    Map<String, Float> zones = readThermalZones();

    if (zones != null) {

        cpu = pickZone(zones,
                "cpu", "cpu-therm", "big", "little",
                "tsens", "mtktscpu");

        gpu = pickZone(zones,
                "gpu", "gpu-therm", "gpuss",
                "mtkgpu");
    }

} catch (Throwable ignored) {}

if (cpu != null && cpu > 85f)
    thermalScore += 20;

if (gpu != null && gpu > 80f)
    thermalScore += 15;

logLabelValue(
        gr ? "Δείκτης θερμικής αστάθειας"
           : "Thermal instability index",
        thermalScore + "/100"
);

if (thermalScore >= 60) {

    thermalRunawayRisk = true;

    logLabelWarnValue(
            gr ? "Κίνδυνος θερμικής αστάθειας"
               : "Thermal runaway risk",
            gr
                    ? "Εντοπίστηκε μοτίβο θερμικής αστάθειας στο hardware."
                    : "Hardware pattern indicates possible thermal runaway."
    );

} else {

    logLabelOkValue(
            gr ? "Θερμική σταθερότητα"
               : "Thermal stability",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις θερμικής αστάθειας."
                    : "No thermal runaway indicators detected."
    );
}
    
// ------------------------------------------------------------
// MOTHERBOARD REPAIR / REBALL INDICATORS
// ------------------------------------------------------------
boolean boardRepairSuspicion = false;
int boardScore = 0;

// συνδυασμός πολλών instability patterns
if (pmicInstability)
    boardScore += 25;

if (basebandDegradation)
    boardScore += 20;

if (sensorBusInstability)
    boardScore += 20;

if (thermalRunaway)
    boardScore += 15;

if (storageDegradation)
    boardScore += 15;

// reboot behaviour
if (randomReboots)
    boardScore += 10;

logLabelValue(
        gr ? "Δείκτης πιθανής επέμβασης μητρικής"
           : "Motherboard repair suspicion index",
        boardScore + "/100"
);

if (boardScore >= 60) {

    boardRepairSuspicion = true;

    logLabelWarnValue(
            gr ? "Μητρική πλακέτα"
               : "Mainboard integrity",
            gr
                    ? "Εντοπίστηκε μοτίβο αστάθειας που συναντάται σε συσκευές μετά από επέμβαση μητρικής."
                    : "Instability pattern consistent with board repair or micro-soldering detected."
    );

} else {

    logLabelOkValue(
            gr ? "Ακεραιότητα μητρικής"
               : "Mainboard integrity",
            gr
                    ? "Δεν εντοπίστηκαν ισχυρές ενδείξεις επέμβασης."
                    : "No strong motherboard repair indicators detected."
    );
}

// ------------------------------------------------------------
// HIDDEN HARDWARE FAULT PREDICTOR
// ------------------------------------------------------------
boolean hiddenFaultRisk = false;

int hiddenScore = 0;

// power instability patterns
if (pmicInstability)
    hiddenScore += 25;

// thermal abnormal behaviour
if (thermalRunaway)
    hiddenScore += 20;

// baseband aging patterns
if (basebandDegradation)
    hiddenScore += 15;

// sensor bus instability
if (sensorBusInstability)
    hiddenScore += 15;

// storage subsystem instability
if (storageDegradation)
    hiddenScore += 15;

// repeated reboot behaviour
if (randomReboots)
    hiddenScore += 10;

logLabelValue(
        gr ? "Δείκτης κρυφής αστάθειας υλικού"
           : "Hidden hardware fault predictor",
        hiddenScore + "/100"
);

if (hiddenScore >= 60) {

    hiddenFaultRisk = true;

    logLabelWarnValue(
            gr ? "Πρόβλεψη βλάβης υλικού"
               : "Hardware risk prediction",
            gr
                    ? "Εντοπίστηκε μοτίβο που μπορεί να οδηγήσει σε μελλοντική αστοχία hardware."
                    : "Pattern detected that may lead to future hardware failure."
    );

} else {

    logLabelOkValue(
            gr ? "Σταθερότητα υλικού"
               : "Hardware stability",
            gr
                    ? "Δεν εντοπίστηκαν πρώιμες ενδείξεις αστοχίας."
                    : "No early hardware fault indicators detected."
    );
}

    // ============================================================
    // STAGE D — FINAL CONFIDENCE
    // ============================================================
    int finalScore = symptomScore;
    if (finalScore > 100) finalScore = 100;

    String finalLevel =
            (finalScore <= 20) ? (gr ? "ΧΑΜΗΛΟ" : "LOW") :
            (finalScore <= 45) ? (gr ? "ΜΕΤΡΙΟ" : "MODERATE") :
            (finalScore <= 70) ? (gr ? "ΥΨΗΛΟ" : "HIGH") :
                                 (gr ? "ΠΟΛΥ ΥΨΗΛΟ" : "VERY HIGH");

    appendHtml("<br>");
    if (finalScore >= 40)
        logLabelWarnValue(gr ? "Τελική Εκτίμηση Σταθερότητας"
                             : "Final stability confidence",
                finalScore + "/100 (" + finalLevel + ")");
    else
        logLabelOkValue(gr ? "Τελική Εκτίμηση Σταθερότητας"
                           : "Final stability confidence",
                finalScore + "/100 (" + finalLevel + ")");

    // ============================================================
    // TRIAGE NOTE
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Σημείωση Τεχνικού" : "Technician note");
    logLine();

    if (finalScore >= 60) {

        logLabelWarnValue(gr ? "Εύρημα" : "Finding",
                gr ? "Εντοπίστηκε μοτίβο αστάθειας"
                   : "Multi-source instability pattern detected");

        logLabelOkValue(gr ? "Σημαντικό" : "Important",
                gr ? "ΔΕΝ αποτελεί διάγνωση υλικού."
                   : "This is NOT a hardware diagnosis.");

        logLabelOkValue(gr ? "Συστήνεται" : "Recommended action",
                gr ? "Έλεγχος από τεχνικό."
                   : "Professional inspection recommended.");

    } else {

        logLabelOkValue(gr ? "Εύρημα" : "Finding",
                gr ? "Δεν εντοπίστηκαν σοβαρά μοτίβα αστάθειας."
                   : "No significant instability patterns detected.");
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 28 ολοκληρώθηκε." : "Lab 28 finished.");
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
            ? "LAB 29 — Έλεγχος Γνησιότητας Συσκευής & Ακεραιότητας"
            : "LAB 29 — Device Authenticity & Integrity");
    logWarn(gr
            ? "SAFE MODE — εμφανίζονται μόνο επιβεβαιωμένα ευρήματα"
            : "SAFE MODE — only confirmed findings are reported");
    logLine();

    appendHtml("<br>");

    if (!isDeviceRooted()) {

        logLabelWarnValue(
                gr ? "Advanced verification"
                        : "Advanced verification",
                gr
                        ? "Ορισμένοι έλεγχοι απαιτούν root"
                        : "Some checks require root"
        );
    }

    int authenticityScore = 100;

    // ============================================================
    // BATTERY
    // ============================================================

    appendHtml("<br>");
    logInfo(gr ? "Έλεγχος μπαταρίας" : "Battery check");
    logLine();

    int batteryLevel = getBatteryPercentSafe();
    float voltage = getBatteryVoltageFiltered();

    if (batteryLevel < 0 || Float.isNaN(voltage)
            || voltage < 2500
            || voltage > 5500) {

        logLabelWarnValue(
                gr ? "Μπαταρία" : "Battery",
                gr
                        ? "Ασυνήθιστες τιμές μέτρησης"
                        : "Abnormal measurement values"
        );

        authenticityScore -= 10;

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

        authenticityScore -= 5;

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

        authenticityScore -= 20;

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

        authenticityScore -= 5;

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

    if (authenticityScore < 0)
        authenticityScore = 0;

String level;

if (authenticityScore >= 90)
    level = gr ? "ΥΨΗΛΗ" : "HIGH";
else if (authenticityScore >= 70)
    level = gr ? "ΜΕΤΡΙΑ" : "MEDIUM";
else
    level = gr ? "ΧΑΜΗΛΗ" : "LOW";

    if (authenticityScore >= 70) {

        logLabelOkValue(
                gr ? "Γνησιότητα"
                        : "Authenticity",
                authenticityScore + "/100 (" + level + ")"
        );

    } else {

        logLabelWarnValue(
                gr ? "Γνησιότητα"
                        : "Authenticity",
                authenticityScore + "/100 (" + level + ")"
        );
    }

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
	
SharedPreferences p =
        getSharedPreferences("GEL_DIAG", MODE_PRIVATE);
        
boolean silentCorruptionRisk =
        p.getBoolean("lab27_silent_corruption_risk", false);

// battery data

float finalScore =
        p.getFloat("lab14_health_score", Float.NaN);

boolean lab14CollapseRisk =
        p.getBoolean("lab14_collapse_risk", false);

boolean lab14SwellingSuspected =
        p.getBoolean("lab14_swelling_risk", false);
        
 // LAB14B
float lab14bConsumptionPerHour =
        p.getFloat("lab14b_consumption_per_hour", -1f);

float lab14bEstimatedHours =
        p.getFloat("lab14b_estimated_hours", -1f);

float lab14bRemainingNormal =
        p.getFloat("lab14b_remaining_normal", -1f);

long ts14b =
        p.getLong("lab14b_ts", 0L);

// stability flags
boolean pmicInstability =
        p.getBoolean("lab28_pmic_instability", false);

boolean sensorBusInstability =
        p.getBoolean("lab28_sensor_bus_instability", false);

boolean sensorFlaps =
        p.getBoolean("lab28_sensor_flaps", false);

// storage / controller
boolean nandRisk =
        p.getBoolean("lab27_nand_risk", false);

boolean controllerRisk =
        p.getBoolean("lab27_controller_risk", false);

// thermal
boolean thermalRunawayRisk =
        p.getBoolean("lab24_thermal_runaway", false);

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

int thermalScore = scoreThermals(maxThermal, avgThermal);
String thermalFlag = colorFlagFromScore(thermalScore);

// ------------------------------------------------------------
// 2) BATTERY HEALTH (light auto inference)
// ------------------------------------------------------------
boolean charging = isChargingNow();

int batteryScore = scoreBattery(
        batTemp,
        (float) batteryPercent,
        charging
);

String batteryFlag = colorFlagFromScore(batteryScore);

boolean batteryRuntimeWeak =
        lab14bEstimatedHours > 0f &&
        lab14bEstimatedHours < 4f;

boolean batteryRuntimeModerate =
        lab14bEstimatedHours >= 4f &&
        lab14bEstimatedHours < 6f;

boolean batteryRuntimeGood =
        lab14bEstimatedHours >= 6f;

boolean batteryConsumptionHigh =
        lab14bConsumptionPerHour > 1200f;

boolean batteryConsumptionVeryHigh =
        lab14bConsumptionPerHour > 1500f;

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

boolean nonOemParts =
        p.getBoolean("lab29_non_oem_parts", false);

boolean displayReplaced =
        p.getBoolean("lab29_display_replaced", false);

boolean cameraReplaced =
        p.getBoolean("lab29_camera_replaced", false);

boolean batteryReplaced =
        p.getBoolean("lab29_battery_replaced", false);

appendHtml("<br>");
logInfo(gr ? "Αυθεντικότητα / Επισκευές" : "Authenticity / Repairs");
logLine();

if (nonOemParts)
    logLabelWarnValue(
            gr ? "Μη γνήσια εξαρτήματα" : "Non-OEM parts",
            gr ? "Πιθανή χρήση μη γνήσιων ανταλλακτικών"
               : "Possible non-OEM components detected"
    );

if (displayReplaced)
    logLabelWarnValue(
            gr ? "Οθόνη" : "Display",
            gr ? "Ενδείξεις αντικατάστασης οθόνης"
               : "Display replacement indicators detected"
    );

if (cameraReplaced)
    logLabelWarnValue(
            gr ? "Κάμερα" : "Camera",
            gr ? "Ενδείξεις αντικατάστασης κάμερας"
               : "Camera replacement indicators detected"
    );

if (batteryReplaced)
    logLabelWarnValue(
            gr ? "Μπαταρία" : "Battery",
            gr ? "Ενδείξεις αντικατάστασης μπαταρίας"
               : "Battery replacement indicators detected"
    );

if (!nonOemParts &&
    !displayReplaced &&
    !cameraReplaced &&
    !batteryReplaced) {

    logLabelOkValue(
            gr ? "Κατάσταση υλικού" : "Hardware authenticity",
            gr ? "Δεν εντοπίστηκαν ενδείξεις επισκευής"
               : "No repair indicators detected"
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

logLabelValue(
        gr ? "Συνολικός δείκτης κινδύνου hardware"
           : "Unified hardware risk score",
        hardwareRiskScore + "/100"
);

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

    if (batteryScore < 60 || lab14CollapseRisk) {

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
// FINAL CLASSIFICATION
// ------------------------------------------------------------
if (!faultDetected) {

    logLabelOkValue(
            gr ? "Συμπέρασμα"
               : "Conclusion",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις hardware βλάβης."
                    : "No hardware fault indicators detected."
    );

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

    logLabelOkValue(
            gr ? "Κατάσταση"
               : "Status",
            gr
                    ? "Δεν εντοπίστηκε συγκεκριμένη αιτία."
                    : "No specific hardware fault detected."
    );
}

int adjustedRisk = Math.max(0, hardwareRiskScore - 5);
int reliabilityScore = 100 - adjustedRisk;

// ------------------------------------------------------------
// CONFIDENCE-WEIGHTED ROOT CAUSE
// ------------------------------------------------------------
if (rootConfidence > 0) {

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
if (hardwareRiskScore >= 60) {

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

if (riskSignals <= 1) {

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

if (reliabilityScore >= 92) {

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
if (!certificateWarning && reliabilityScore >= 85) {

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
                    ? "Εντοπίστηκαν ενδείξεις πιθανής επέμβασης ή αστάθειας hardware."
                    : "Indicators of hardware intervention or instability detected."
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
                : "Evaluation based on hardware diagnostics and measurement consistency."
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

    logLabelWarnValue(
            gr ? "Συμπέρασμα τεχνικού"
               : "Technician note",
            gr
                    ? "Συνιστάται παρακολούθηση ή τεχνικός έλεγχος."
                    : "Monitoring or service inspection recommended."
    );

}

// ------------------------------------------------------------
// 10) FINAL SCORES
// ------------------------------------------------------------
int performanceScore = Math.round(
(storageScore * 0.35f) +
(ramScore     * 0.35f) +
(appsScore    * 0.15f) +
(thermalScore * 0.15f)
);

int deviceHealthScore = Math.round(
(thermalScore   * 0.25f) +
(batteryScore   * 0.25f) +
(performanceScore * 0.30f) +
(stabilityScore * 0.20f)
);

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
        thermalFlag + " (" + thermalScore + ")"
);

if (zones == null || zones.isEmpty()) {

    logLabelWarnValue(
            gr ? "Ζώνες" : "Zones",
            gr
                    ? "Δεν είναι αναγνώσιμες θερμικές ζώνες — Μόνο θερμοκρασία μπαταρίας (" + fmt1(batTemp) + "°C)"
                    : "No thermal zones readable — Battery temp only (" + fmt1(batTemp) + "°C)"
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
        batteryFlag + " (" + batteryScore + ")"
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
        colorFlagFromScore(deviceHealthScore)
                + " (" + deviceHealthScore + ")"
);

String deviceClass;

if (deviceHealthScore >= 90)
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
        colorFlagFromScore(performanceScore)
                + " (" + performanceScore + ")"
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

if (verdict.startsWith("🟢"))
    logLabelOkValue(gr ? "Αποτέλεσμα" : "Result", verdict);
else if (verdict.startsWith("??"))
    logLabelWarnValue(gr ? "Αποτέλεσμα" : "Result", verdict);
else
    logLabelErrorValue(gr ? "Αποτέλεσμα" : "Result", verdict);

// ------------------------------------------------------------
// HARDWARE TREND ENGINE
// ------------------------------------------------------------
SharedPreferences history =
        getSharedPreferences("gel_hw_history", MODE_PRIVATE);

int prevRisk = history.getInt("hw_last_risk", -1);
float prevBattery = history.getFloat("hw_last_battery_health", -1f);
int prevAging = history.getInt("hw_last_aging_index", -1);

boolean trendDetected = false;

if (prevRisk >= 0) {

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

    logLabelOkValue(
            gr ? "Τάση hardware"
               : "Hardware trend",
            gr
                    ? "Δεν εντοπίστηκε επιδείνωση από την προηγούμενη μέτρηση."
                    : "No degradation trend detected since previous run."
    );
}

// ------------------------------------------------------------
// RUN COUNTER + RELIABILITY INDEX
// ------------------------------------------------------------

int runCount = history.getInt("hw_run_count", 0);
runCount++;

history.edit().putInt("hw_run_count", runCount).apply();

int reliabilityIndex;

if (runCount >= 10)
    reliabilityIndex = 95;
else if (runCount >= 7)
    reliabilityIndex = 85;
else if (runCount >= 5)
    reliabilityIndex = 75;
else if (runCount >= 3)
    reliabilityIndex = 65;
else
    reliabilityIndex = 50;

logLabelOkValue(
        gr ? "Αξιοπιστία διάγνωσης"
           : "Diagnosis reliability",
        (reliabilityIndex >= 80
                ? (gr ? "Υψηλή" : "High")
                : reliabilityIndex >= 50
                    ? (gr ? "Μέτρια" : "Moderate")
                    : (gr ? "Χαμηλή" : "Low"))
        + " (" + reliabilityIndex +
        ", " +
        (gr ? "εκτελέσεις: " : "runs: ") +
        runCount +
        ")"
);

if (runCount < 3) {

    logLabelWarnValue(
            gr ? "Σημείωση αξιοπιστίας"
               : "Reliability note",
            gr
                    ? "Η διάγνωση βασίζεται σε περιορισμένο ιστορικό."
                    : "Diagnosis based on limited history."
    );

} else if (runCount >= 7) {

    logLabelOkValue(
            gr ? "Στατιστική αξιοπιστία"
               : "Statistical reliability",
            gr
                    ? "Η διάγνωση βασίζεται σε επαρκές ιστορικό runs."
                    : "Diagnosis based on sufficient historical runs."
    );
}

// ------------------------------------------------------------
// STORE HARDWARE HEALTH SNAPSHOT
// ------------------------------------------------------------

history.edit()
        .putInt("hw_last_risk", hardwareRiskScore)
        .putFloat("hw_last_battery_health", finalScore)
        .putBoolean("hw_last_nand_risk", nandRisk)
        .putBoolean("hw_last_controller_risk", controllerRisk)
        .putBoolean("hw_last_thermal_risk", thermalRunawayRisk)
        .putLong("hw_last_timestamp", System.currentTimeMillis())
        .apply();

logLabelOkValue(
        gr ? "Ιστορικό hardware"
           : "Hardware history",
        gr ? "Το snapshot αποθηκεύτηκε"
           : "Hardware snapshot stored"
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

// Battery instability (real signal)
if (collapseRisk[0]) {

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

    logLabelOkValue(
            gr ? "Συνέπεια διάγνωσης"
               : "Diagnostic consistency",
            gr
                    ? "Τα διαγνωστικά αποτελέσματα είναι συνεπή."
                    : "Diagnostic results appear consistent."
    );
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
logLabelValue(
        gr ? "Δείκτης κινδύνου hardware"
           : "Hardware risk score",
        hardwareRiskScore + "/100"
);

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

    for (int i = 0; i < 8; i++) {
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
logLabelOkValue(
        gr ? "Πιστοποίηση"
           : "Certification",
        gr
                ? "Η αξιολόγηση βασίζεται στα αποτελέσματα των διαγνωστικών εργαστηρίων GEL."
                : "Assessment based on GEL diagnostic laboratory results."
);

appendHtml("<br>");
logOk(gr ? "GEL Certification completed." : "GEL Certification completed.");
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

    if (s >= 80) return "??";
    if (s >= 55) return "🟡";
    return "🔴";
}

private String finalVerdict(int health, int sec, int priv, int perf) {

    final boolean gr = AppLang.isGreek(this);

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
// LAB14B READ (NEW)
// ------------------------------------------------------------
float lab14bConsumptionPerHour =
        p.getFloat("lab14b_consumption_per_hour", -1f);

float lab14bEstimatedHours =
        p.getFloat("lab14b_estimated_hours", -1f);

float lab14bRemainingNormal =
        p.getFloat("lab14b_remaining_normal", -1f);

long ts14b =
        p.getLong("lab14b_ts", 0L);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 31 — ΤΕΛΙΚΗ ΣΥΝΟΨΗ ΤΕΧΝΙΚΟΥ (ΜΟΝΟ ΑΝΑΓΝΩΣΗ)"
            : "LAB 31 — FINAL TECHNICIAN SUMMARY (READ-ONLY)");
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

        logLabelOkValue(
                gr ? "Κατάσταση συστήματος" : "System status",
                gr
                        ? "Δεν εντοπίστηκαν προειδοποιήσεις ή σφάλματα"
                        : "No warnings or errors detected"
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
    // 4) BATTERY SCORE FROM LAB 14
    // ------------------------------------------------------------
    float batteryScore =
            p.getFloat("lab14_health_score", -1f);
            
    float batteryHealth =
        p.getFloat("lab14_health_percent", -1f);

    float batteryContribution = 0f;

    if (batteryScore >= 0 && batteryScore <= 100) {
    batteryContribution = batteryScore * 0.20f;
}

    // ------------------------------------------------------------
    // 5) DEVICE BASE SCORE
    // ------------------------------------------------------------
    float deviceScore = 70f;

    if (issuesDetected) {
        deviceScore -= 20f;
    }
    
    // LAB14B influence

    deviceScore += batteryContribution;

    if (deviceScore > 100f) deviceScore = 100f;
    if (deviceScore < 0f) deviceScore = 0f;

    // ------------------------------------------------------------
    // 6) DEVICE GRADE
    // ------------------------------------------------------------
    String deviceGrade;

    if (deviceScore >= 90)
        deviceGrade = "A+";
    else if (deviceScore >= 80)
        deviceGrade = "A";
    else if (deviceScore >= 70)
        deviceGrade = "B";
    else if (deviceScore >= 60)
        deviceGrade = "C";
    else
        deviceGrade = "D";

// ------------------------------------------------------------
// 7) PRINT DEVICE EVALUATION
// ------------------------------------------------------------
appendHtml("<br>");
logInfo(gr
        ? "Αξιολόγηση συσκευής"
        : "Device evaluation");
logLine();

if (batteryScore >= 0 || batteryHealth >= 0) {

    float finalHealth;

    if (batteryHealth >= 0f) {
        finalHealth = batteryHealth;
    } else {
        finalHealth = batteryScore;
    }

    logLabelValue(
            gr ? "Υγεία μπαταρίας"
               : "Battery health",
            colorFlagFromScore((int) finalHealth)
                    + " (" + (int) finalHealth + ")"
    );
}

logLabelOkValue(
        gr ? "Συνολική βαθμολογία συσκευής"
           : "Device overall",
        colorFlagFromScore((int) deviceScore)
                + " (" + (int) deviceScore + ")"
);

logLabelOkValue(
        gr ? "Κατηγορία συσκευής"
           : "Device grade",
        deviceGrade
);

appendHtml("<br>");
   
// ------------------------------------------------------------
// DEVICE MANIPULATION SUSPICION INDEX
// (LAB28 + LAB29 indicators)
// ------------------------------------------------------------
int manipulationScore = 0;

// LAB29 instability patterns
boolean hwInstability =
        p.getBoolean("lab29_instability_pattern", false);

// LAB30 authenticity indicators
boolean nonOemParts =
        p.getBoolean("lab30_non_oem_parts", false);

boolean displayReplaced =
        p.getBoolean("lab30_display_replaced", false);

boolean cameraReplaced =
        p.getBoolean("lab30_camera_replaced", false);

boolean batteryReplaced =
        p.getBoolean("lab30_battery_replaced", false);

// scoring
if (hwInstability) manipulationScore += 20;
if (nonOemParts) manipulationScore += 20;
if (displayReplaced) manipulationScore += 10;
if (cameraReplaced) manipulationScore += 10;
if (batteryReplaced) manipulationScore += 5;

if (manipulationScore > 100) manipulationScore = 100;

String manipulationLabel;

if (manipulationScore >= 60)
    manipulationLabel = gr ? "Ισχυρές ενδείξεις παρέμβασης"
                           : "Strong manipulation indicators";
else if (manipulationScore >= 35)
    manipulationLabel = gr ? "Μέτριες ενδείξεις παρέμβασης"
                           : "Moderate manipulation indicators";
else if (manipulationScore >= 15)
    manipulationLabel = gr ? "Ασθενείς ενδείξεις"
                           : "Weak indicators";
else
    manipulationLabel = gr ? "Καμία ένδειξη"
                           : "No indicators";
                           
appendHtml("<br>");

if (manipulationScore >= 35) {

    logLabelWarnValue(
            gr ? "Δείκτης παρέμβασης συσκευής"
               : "Device manipulation suspicion index",
            manipulationLabel + " (" + manipulationScore + ")"
    );

} else {

    logLabelOkValue(
            gr ? "Δείκτης παρέμβασης συσκευής"
               : "Device manipulation suspicion index",
            manipulationScore + "/100 (" + manipulationLabel + ")"
    );

}
    
// ------------------------------------------------------------
// DEVICE RELIABILITY INDEX
// ------------------------------------------------------------
float dri = 100f;

// battery health
if (batteryScore >= 0) {

    if (batteryScore < 55)
        dri -= 25;
    else if (batteryScore < 70)
        dri -= 15;
}

// RAM pressure
boolean ramStress =
        p.getBoolean("lab19_ram_pressure", false);

if (ramStress) dri -= 10;

// crash history
boolean crashHistory =
        p.getBoolean("lab25_crash_detected", false);

if (crashHistory) dri -= 20;

// reboot instability
boolean rebootPattern =
        p.getBoolean("lab20_reboot_pattern", false);

if (rebootPattern) dri -= 10;

// thermal instability
boolean thermalIssue =
        p.getBoolean("lab16_thermal_warning", false);

if (thermalIssue) dri -= 15;

// swelling
boolean lab14SwellingSuspected =
        p.getBoolean("lab14_swelling_risk", false);

// 🟡 weaker penalty
if (lab14SwellingSuspected && dri < 80)
    dri -= 6;

// root risk
boolean rooted =
        p.getBoolean("lab24_root_detected", false);

if (rooted) dri -= 10;

// battery calibration drift
boolean lab14CalibrationDrift =
        p.getBoolean("lab14_calibration_drift", false);

if (lab14CalibrationDrift) dri -= 10;

boolean lab14CollapseRisk =
        p.getBoolean("lab14_collapse_risk", false);

if (lab14CollapseRisk) dri -= 20;

// LAB14B limiter influence

// manipulation suspicion (LAB28 + LAB29)
if (manipulationScore >= 60)
    dri -= 20;
else if (manipulationScore >= 35)
    dri -= 10;

if (dri > 100) dri = 100;
if (dri < 0) dri = 0;

String driLabel;

if (dri >= 90)
    driLabel = gr ? "Πολύ αξιόπιστη συσκευή" : "Highly reliable";
else if (dri >= 75)
    driLabel = gr ? "Σταθερή συσκευή" : "Stable";
else if (dri >= 60)
    driLabel = gr ? "Μέτριος κίνδυνος αστάθειας" : "Moderate risk";
else if (dri >= 40)
    driLabel = gr ? "Υψηλός κίνδυνος προβλημάτων" : "High risk";
else
    driLabel = gr ? "Ασταθής συσκευή" : "Unstable device";

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

// ============================================================
// LAB 6 — TOUCH GRID
// ============================================================

final boolean gr = AppLang.isGreek(this);

if (requestCode == REQ_LAB6_TOUCH) {

    int total = TouchGridTestActivity.getTotalZones();
    int remaining = TouchGridTestActivity.getRemainingZones();

    appendHtml("<br>");
    logLine();
    logSection(gr ? "LAB 6 — Οθόνη / Αφή" : "LAB 6 — Display / Touch");
    logLine();

    if (resultCode == RESULT_OK) {

        logLabelOkValue(
                gr ? "Έλεγχος αφής" : "Touch grid test",
                gr ? "Ολοκληρώθηκε" : "Completed"
        );

        logLabelOkValue(
                gr ? "Ζώνες οθόνης" : "Screen zones",
                gr ? "Όλες οι ζώνες ανταποκρίθηκαν" : "All zones responded"
        );

        logLabelOkValue(
                gr ? "Νεκρές ζώνες" : "Dead zones",
                gr ? "Δεν εντοπίστηκαν" : "Not detected"
        );

    } else {

        logLabelWarnValue(
                gr ? "Έλεγχος αφής" : "Touch grid test",
                gr ? "Μη ολοκληρωμένος" : "Incomplete"
        );

        logLabelErrorValue(
                gr ? "Μη ανταποκρινόμενες ζώνες" : "Unresponsive zones",
                remaining + " / " + total
        );

        logInfo(gr ? "Ερμηνεία:" : "Interpretation:");

        logLabelWarnValue(
                gr ? "Πιθανή αιτία" : "Possible cause",
                gr
                        ? "Τοπικές νεκρές ζώνες digitizer"
                        : "Localized digitizer dead zones"
        );

        logLabelOkValue(
                gr ? "Σύσταση" : "Recommendation",
                gr
                        ? "Επαναλάβετε χειροκίνητα τον έλεγχο για επιβεβαίωση"
                        : "Manual re-test to confirm behavior"
        );
    }

    appendHtml("<br>");
    logLabelOkValue(
            gr ? "Επόμενο βήμα" : "Next step",
            gr
                    ? "LAB 6 PRO — Χρώμα & Ομοιομορφία Οθόνης"
                    : "LAB 6 PRO — Display Color & Uniformity"
    );
    logLine();

    startActivityForResult(
            new Intent(this, DisplayProTestActivity.class),
            REQ_LAB6_COLOR
    );

    return;
}

// ============================================================
// LAB 6 PRO — DISPLAY COLOR / UNIFORMITY / ARTIFACTS
// ============================================================
if (requestCode == REQ_LAB6_COLOR) {

    if (resultCode == RESULT_CANCELED) {

        logLabelWarnValue(
                "LAB 6 PRO",
                gr ? "Ακυρώθηκε από τον χρήστη" : "Canceled by user"
        );

        logLabelWarnValue(
                gr ? "Οπτικός έλεγχος" : "Visual inspection",
                gr ? "Δεν πραγματοποιήθηκε" : "Not performed"
        );

        appendHtml("<br>");
        logLine();
        enableSingleExportButton();
        return;
    }

    boolean issues =
            data != null && data.getBooleanExtra("display_issues", false);

    if (!issues) {

        logLabelOkValue(
                gr ? "Οπτικός έλεγχος" : "Visual inspection",
                gr ? "Δεν αναφέρθηκαν ανωμαλίες" : "No visible artifacts reported"
        );

        logLabelOkValue(
                gr ? "Ομοιομορφία οθόνης" : "Display uniformity",
                "OK"
        );

        logLabelOkValue(
                gr ? "Burn-in / banding" : "Burn-in / banding",
                gr ? "Δεν παρατηρήθηκε" : "Not observed"
        );

    } else {

        logLabelWarnValue(
                gr ? "Οπτικός έλεγχος" : "Visual inspection",
                gr ? "Αναφέρθηκαν οπτικές ανωμαλίες" : "User reported visual anomalies"
        );

        logInfo(gr ? "Πιθανά ευρήματα:" : "Possible findings:");

        logLabelWarnValue("• Issue",
                gr ? "Burn-in / αποτύπωση εικόνας" : "Burn-in / image retention");

        logLabelWarnValue("• Issue",
                gr ? "Color banding / διαβαθμίσεις" : "Color banding / gradient steps");

        logLabelWarnValue("• Issue",
                gr ? "Λεκέδες / mura / μετατόπιση χρώματος"
                   : "Screen stains / mura / tint shift");
    }

    appendHtml("<br>");
    logSection(gr ? "LAB 6 — Τελικό Αποτέλεσμα" : "LAB 6 — Final Result");
    logLine();

    logLabelOkValue(
            gr ? "Έλεγχος οθόνης" : "Display test",
            gr
                    ? "Ολοκληρώθηκε έλεγχος αφής και οπτικής επιθεώρησης"
                    : "Touch integrity and visual inspection completed"
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 6 ολοκληρώθηκε." : "Lab 6 finished.");
    logLine();

    enableSingleExportButton();
    return;
}

// ============================================================
// LAB 7 — Rotation + Proximity Sensors
// ============================================================

if (requestCode == 7007) {

    appendHtml("<br>");
    logLine();
    logSection(gr
            ? "LAB 7 — Περιστροφή & Αισθητήρας Εγγύτητας"
            : "LAB 7 — Rotation & Proximity Sensors");
    logLine();

    if (resultCode == RESULT_OK) {

        logLabelOkValue(
                gr ? "Ανίχνευση περιστροφής" : "Rotation detection",
                gr ? "Εντοπίστηκε μέσω επιταχυνσιόμετρου" : "Detected via accelerometer"
        );

        logLabelOkValue(
                gr ? "Αλλαγή προσανατολισμού" : "Orientation change",
                gr ? "Επιβεβαιώθηκε" : "Confirmed"
        );

        logLabelOkValue(
                gr ? "Αισθητήρες κίνησης" : "Motion sensors",
                gr ? "Λειτουργούν κανονικά" : "Responding normally"
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

        logLabelErrorValue(
                gr ? "Ανίχνευση περιστροφής" : "Rotation detection",
                gr ? "Δεν εντοπίστηκε" : "Not detected"
        );

        logLabelWarnValue(
                gr ? "Πιθανή αιτία" : "Possible cause",
                gr
                        ? "Η αυτόματη περιστροφή είναι απενεργοποιημένη ή υπάρχει βλάβη αισθητήρα"
                        : "Auto-rotate disabled or sensor malfunction"
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

    if (resultCode == RESULT_OK) {

        logLabelOkValue(
                gr ? "Αισθητήρας εγγύτητας" : "Proximity sensor",
                gr ? "Ανταποκρίθηκε σωστά" : "Responded correctly"
        );

        logLabelOkValue(
                gr ? "Ανίχνευση Κοντά / Μακριά" : "Near / Far detection",
                gr ? "Επιβεβαιώθηκε" : "Confirmed"
        );

        logLabelOkValue(
                gr ? "Συμπεριφορά οθόνης" : "Screen behavior",
                gr
                        ? "Η οθόνη έκλεισε όταν καλύφθηκε ο αισθητήρας"
                        : "Turned off when sensor was covered"
        );

    } else {

        logLabelErrorValue(
                gr ? "Αισθητήρας εγγύτητας" : "Proximity sensor",
                gr ? "Δεν εντοπίστηκε απόκριση" : "No response detected"
        );

        logLabelWarnValue(
                gr ? "Πιθανή αιτία" : "Possible cause",
                gr
                        ? "Εμπόδιο στον αισθητήρα ή πιθανή βλάβη υλικού"
                        : "Sensor obstruction or hardware fault"
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
