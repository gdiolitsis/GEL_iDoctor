package com.gel.cleaner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Optional live functionality checks appended to Lab 9's existing sensor inventory.
 * Not a new numbered Lab; does not change certification or Lab 27/28 grading.
 * GPS coordinates, NFC tag IDs and user identifiers are never persisted or returned.
 * A missing observation is NOT a hardware-failure verdict.
 */
public final class EssentialHardwareChecksActivity extends GELAutoActivityHook
        implements SensorEventListener {

    public static final String EXTRA_GPS = "lab9_live_gps";
    public static final String EXTRA_GPS_ACCURACY = "lab9_live_gps_accuracy";
    public static final String EXTRA_NFC = "lab9_live_nfc";
    public static final String EXTRA_VOLUME_UP = "lab9_live_volume_up";
    public static final String EXTRA_VOLUME_DOWN = "lab9_live_volume_down";
    public static final String EXTRA_POWER = "lab9_live_power_user";
    public static final String EXTRA_GYRO = "lab9_live_gyro";
    public static final String EXTRA_MAGNET = "lab9_live_magnet";
    public static final String EXTRA_LIGHT = "lab9_live_light";

    public static final String VERIFIED = "VERIFIED";
    public static final String USER_CONFIRMED = "USER_CONFIRMED";
    public static final String USER_FAILED = "USER_FAILED";
    public static final String NOT_VERIFIED = "NOT_VERIFIED";
    public static final String NOT_TESTED = "NOT_TESTED";
    public static final String UNSUPPORTED = "UNSUPPORTED";
    public static final String DISABLED = "DISABLED";
    public static final String PERMISSION_DENIED = "PERMISSION_DENIED";

    private static final int GPS_PERMISSION = 1909;
    private static final long SENSOR_TIME_MS = 20000;
    private static final long GPS_TIME_MS = 35000;
    private static final long NFC_TIME_MS = 35000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Map<String, String> values = new LinkedHashMap<>();
    private final Map<String, TextView> outputs = new LinkedHashMap<>();
    private boolean greek;
    private boolean sensorsRunning, gpsRunning, nfcRunning, volumeRunning;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private LocationListener gpsListener;
    private NfcAdapter nfcAdapter;
    private long gpsStartNs;
    private float gpsAccuracy = Float.NaN;
    private final Map<Integer, float[]> sensorFirst = new LinkedHashMap<>();
    private boolean volumeUp, volumeDown;
    private Runnable sensorTimeout, gpsTimeout, nfcTimeout;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        greek = AppLang.isGreek(this);
        setTitle(greek ? "Lab 9 — Ζωντανοί έλεγχοι λειτουργίας" : "Lab 9 — Live functional checks");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        String[] fields = {EXTRA_GPS, EXTRA_NFC, EXTRA_VOLUME_UP, EXTRA_VOLUME_DOWN,
                EXTRA_POWER, EXTRA_GYRO, EXTRA_MAGNET, EXTRA_LIGHT};
        for (String f : fields) values.put(f, NOT_TESTED);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(0xFF101010);
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        int m = uiDp(16);
        body.setPadding(m, uiDp(22), m, uiDp(24));
        scroll.addView(body);

        label(body, greek ? "LAB 9 — Πραγματικοί έλεγχοι" : "LAB 9 — Live checks",
                21, Color.WHITE, true);
        label(body, greek
                ? "Προαιρετικές δοκιμές. Πράσινο μόνο για πραγματική επιβεβαίωση. "
                  + "Απουσία αποτελέσματος δεν σημαίνει βλάβη."
                : "Optional checks. Green means actual evidence. "
                  + "No result is not proof of hardware failure.",
                14, Color.WHITE, false);

        addGroup(body, greek ? "Έλεγχος γυροσκοπίου, πυξίδας και αισθητήρα φωτός"
                        : "Gyroscope, compass and light sensor check");
        label(body, greek
                ? "Πάτησε «ΕΝΑΡΞΗ ΕΛΕΓΧΟΥ ΑΙΣΘΗΤΗΡΩΝ» και ακολούθησε τα βήματα:\n\n"
                  + "1. Περίστρεψε το κινητό προς διαφορετικές κατευθύνσεις για να ελέγξω "
                  + "το γυροσκόπιο και την πυξίδα.\n\n"
                  + "2. Στρέψε το κινητό προς ένα πιο φωτεινό ή πιο σκιερό σημείο "
                  + "για να μετρήσω τον αισθητήρα φωτός.\n\n"
                  + "Δεν χρειάζεται να αλλάξεις τη φωτεινότητα της οθόνης."
                : "Tap TEST LIVE SENSORS, then follow these steps:\n\n"
                  + "1. Rotate the phone in different directions so I can test "
                  + "the gyroscope and compass.\n\n"
                  + "2. Point the phone towards a brighter or shadier area "
                  + "to measure the ambient light sensor.\n\n"
                  + "You do not need to change the screen brightness.",
                14, Color.WHITE, false);
        addState(body, EXTRA_GYRO, greek ? "Γυροσκόπιο" : "Gyroscope");
        addState(body, EXTRA_MAGNET, greek ? "Μαγνητόμετρο / Πυξίδα" : "Magnetometer / Compass");
        addState(body, EXTRA_LIGHT, greek ? "Αισθητήρας φωτός" : "Ambient light");
        action(body, greek ? "ΕΝΑΡΞΗ ΕΛΕΓΧΟΥ ΑΙΣΘΗΤΗΡΩΝ" : "TEST LIVE SENSORS",
                () -> startSensors(), false);

        addGroup(body, greek ? "GPS — δοκιμή σε ανοιχτό χώρο" : "GPS — test outdoors");
        addState(body, EXTRA_GPS, "GPS");
        action(body, greek ? "ΕΝΑΡΞΗ GPS" : "TEST GPS", () -> startGps(), false);

        addGroup(body, greek ? "NFC — απαιτείται εξωτερική ετικέτα NFC"
                        : "NFC — requires an external NFC tag");
        addState(body, EXTRA_NFC, "NFC");
        action(body, greek ? "ΕΝΑΡΞΗ NFC" : "SCAN NFC TAG", () -> startNfc(), false);

        addGroup(body, greek ? "Φυσικά πλήκτρα" : "Physical buttons");
        label(body, greek
                ? "Πάτησε «ΕΛΕΓΧΟΣ ΠΛΗΚΤΡΩΝ ΕΝΤΑΣΗΣ». "
                  + "Κατόπιν πάτησε διαδοχικά τα φυσικά πλήκτρα + και − της συσκευής."
                : "Tap VOLUME BUTTON TEST first. "
                  + "Then press the phone's physical + and − volume buttons one after the other.",
                14, 0xFFFFD700, false);
        addState(body, EXTRA_VOLUME_UP, "Volume +");
        addState(body, EXTRA_VOLUME_DOWN, "Volume −");
        action(body, greek ? "ΕΛΕΓΧΟΣ ΠΛΗΚΤΡΩΝ ΕΝΤΑΣΗΣ"
                        : "VOLUME BUTTON TEST",
                () -> startVolume(), false);
        addState(body, EXTRA_POWER, greek ? "Power (επιβεβαίωση χρήστη)"
                : "Power (user confirmation)");
        action(body, greek ? "ΧΕΙΡΟΚΙΝΗΤΟΣ ΕΛΕΓΧΟΣ POWER" : "MANUAL POWER CHECK",
                () -> confirmPower(), false);
        label(body, greek
                ? "Το πλήκτρο Home ή η χειρονομία πλοήγησης ελέγχεται από το Android "
                  + "και δεν μπορεί να επαληθευτεί από συνηθισμένη εφαρμογή."
                : "Android handles Home/navigation gestures itself; a normal app "
                  + "cannot verify their hardware key events.",
                13, Color.WHITE, false);

        action(body, greek ? "ΤΕΛΟΣ — ΕΠΙΣΤΡΟΦΗ ΣΤΑ LOGS"
                        : "FINISH — RETURN TO LOGS",
                () -> finishWithResults(), true);
        setContentView(scroll);
    }

    private void startSensors() {
        stopSensors();
        sensorFirst.clear();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager == null) {
            for (String k : new String[]{EXTRA_GYRO, EXTRA_MAGNET, EXTRA_LIGHT})
                set(k, NOT_VERIFIED);
            return;
        }
        int[] types = {Sensor.TYPE_GYROSCOPE, Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_LIGHT};
        String[] keys = {EXTRA_GYRO, EXTRA_MAGNET, EXTRA_LIGHT};
        sensorsRunning = true;
        for (int i = 0; i < types.length; i++) {
            Sensor sensor = sensorManager.getDefaultSensor(types[i]);
            if (sensor == null) {
                set(keys[i], UNSUPPORTED);
            } else {
                boolean registered = sensorManager.registerListener(
                        this, sensor, SensorManager.SENSOR_DELAY_UI);
                set(keys[i], registered ? NOT_VERIFIED : NOT_VERIFIED);
            }
        }
        sensorTimeout = () -> stopSensors();
        handler.postDelayed(sensorTimeout, SENSOR_TIME_MS);
    }

    @Override public void onSensorChanged(SensorEvent e) {
        if (!sensorsRunning || e == null || e.sensor == null || e.values == null) return;
        int type = e.sensor.getType();
        if (type == Sensor.TYPE_GYROSCOPE) {
            if (EssentialHardwareEvidence.gyroMoved(e.values)) set(EXTRA_GYRO, VERIFIED);
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD && e.values.length >= 3) {
            float[] first = sensorFirst.get(type);
            if (first == null) sensorFirst.put(type, e.values.clone());
            else if (EssentialHardwareEvidence.magneticFieldChanged(first, e.values))
                set(EXTRA_MAGNET, VERIFIED);
        } else if (type == Sensor.TYPE_LIGHT && e.values.length >= 1) {
            float[] first = sensorFirst.get(type);
            if (first == null) sensorFirst.put(type, e.values.clone());
            else if (EssentialHardwareEvidence.ambientLightChanged(first[0], e.values[0]))
                set(EXTRA_LIGHT, VERIFIED);
        }
        if (doneOrMissing(EXTRA_GYRO) && doneOrMissing(EXTRA_MAGNET)
                && doneOrMissing(EXTRA_LIGHT)) stopSensors();
    }

    private boolean doneOrMissing(String key) {
        String v = values.get(key);
        return VERIFIED.equals(v) || UNSUPPORTED.equals(v);
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    private void stopSensors() {
        sensorsRunning = false;
        if (sensorTimeout != null) handler.removeCallbacks(sensorTimeout);
        if (sensorManager != null) sensorManager.unregisterListener(this);
    }

    private void startGps() {
        stopGps();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Android 12+ ignores fine-only requests on some releases.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, GPS_PERMISSION);
            return;
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager == null) { set(EXTRA_GPS, NOT_VERIFIED); return; }
        try {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                set(EXTRA_GPS, DISABLED);
                return;
            }
            gpsStartNs = SystemClock.elapsedRealtimeNanos();
            gpsRunning = true;
            set(EXTRA_GPS, NOT_VERIFIED);
            gpsListener = new LocationListener() {
                @Override public void onLocationChanged(Location location) {
                    if (!gpsRunning || location == null) return;
                    boolean mock = Build.VERSION.SDK_INT >= 31
                            ? location.isMock() : location.isFromMockProvider();
                    if (!LocationManager.GPS_PROVIDER.equals(location.getProvider())
                            || !location.hasAccuracy()
                            || !EssentialHardwareEvidence.newGpsFix(gpsStartNs,
                                location.getElapsedRealtimeNanos(), mock, location.getAccuracy()))
                        return;
                    gpsAccuracy = location.getAccuracy();
                    set(EXTRA_GPS, VERIFIED);
                    stopGps();
                }
            };
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000L, 0f, gpsListener);
            gpsTimeout = () -> stopGps();
            handler.postDelayed(gpsTimeout, GPS_TIME_MS);
        } catch (SecurityException denied) {
            stopGps();
            set(EXTRA_GPS, PERMISSION_DENIED);
        } catch (RuntimeException unavailable) {
            stopGps();
            set(EXTRA_GPS, NOT_VERIFIED);
        }
    }

    @Override public void onRequestPermissionsResult(int request, String[] permissions,
                                                        int[] grantResults) {
        super.onRequestPermissionsResult(request, permissions, grantResults);
        if (request == GPS_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startGps();
            else set(EXTRA_GPS, PERMISSION_DENIED);
        }
    }

    private void stopGps() {
        gpsRunning = false;
        if (gpsTimeout != null) handler.removeCallbacks(gpsTimeout);
        if (locationManager != null && gpsListener != null) {
            try { locationManager.removeUpdates(gpsListener); } catch (Exception ignored) { }
        }
        gpsListener = null;
    }

    private void startNfc() {
        stopNfc();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) { set(EXTRA_NFC, UNSUPPORTED); return; }
        if (!nfcAdapter.isEnabled()) { set(EXTRA_NFC, DISABLED); return; }
        nfcRunning = true;
        set(EXTRA_NFC, NOT_VERIFIED);
        try {
            int flags = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B
                    | NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V
                    | NfcAdapter.FLAG_READER_NFC_BARCODE
                    | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
            nfcAdapter.enableReaderMode(this, tag -> runOnUiThread(() -> {
                if (!nfcRunning || tag == null) return;
                // Proves tag discovery only; no UID, NDEF, payment data or tag data read.
                set(EXTRA_NFC, VERIFIED);
                stopNfc();
            }), flags, null);
            nfcTimeout = () -> stopNfc();
            handler.postDelayed(nfcTimeout, NFC_TIME_MS);
        } catch (RuntimeException unavailable) {
            stopNfc();
            set(EXTRA_NFC, NOT_VERIFIED);
        }
    }

    private void stopNfc() {
        nfcRunning = false;
        if (nfcTimeout != null) handler.removeCallbacks(nfcTimeout);
        if (nfcAdapter != null) {
            try { nfcAdapter.disableReaderMode(this); }
            catch (RuntimeException ignored) { }
        }
    }

    private void startVolume() {
        volumeRunning = true;
        volumeUp = volumeDown = false;
        set(EXTRA_VOLUME_UP, NOT_VERIFIED);
        set(EXTRA_VOLUME_DOWN, NOT_VERIFIED);
    }

    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        if (volumeRunning && event != null) {
            int code = event.getKeyCode();
            if (code == KeyEvent.KEYCODE_VOLUME_UP || code == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                    if (code == KeyEvent.KEYCODE_VOLUME_UP) {
                        volumeUp = true; set(EXTRA_VOLUME_UP, VERIFIED);
                    } else {
                        volumeDown = true; set(EXTRA_VOLUME_DOWN, VERIFIED);
                    }
                    if (volumeUp && volumeDown) volumeRunning = false;
                }
                return true; // Do not change the user's volume during the test.
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void confirmPower() {
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                .setTitle(greek ? "Επιβεβαίωση πλήκτρου Power" : "Power button confirmation")
                .setMessage(greek
                        ? "Κλείδωσε και ξεκλείδωσε τη συσκευή χρησιμοποιώντας το φυσικό "
                          + "πλήκτρο Power. Επέστρεψε εδώ. Πέτυχε η δοκιμή; "
                          + "Αυτό αποτελεί δήλωση χρήστη, όχι αυτόματη μέτρηση."
                        : "Lock and unlock the phone using the physical Power key, "
                          + "then return here. Did it work? This is a user report, "
                          + "not an automatic hardware measurement.")
                .setPositiveButton(greek ? "ΝΑΙ, ΛΕΙΤΟΥΡΓΕΙ" : "YES, WORKS",
                        (dialog, which) -> set(EXTRA_POWER, USER_CONFIRMED))
                .setNegativeButton(greek ? "ΟΧΙ, ΔΕΝ ΛΕΙΤΟΥΡΓΕΙ" : "NO, IT FAILED",
                        (dialog, which) -> set(EXTRA_POWER, USER_FAILED))
                .setNeutralButton(greek ? "ΠΑΡΑΛΕΙΨΗ" : "SKIP",
                        (dialog, which) -> set(EXTRA_POWER, NOT_TESTED))
                .show();
    }

    @Override protected void onPause() {
        stopSensors();
        stopGps();
        stopNfc();
        volumeRunning = false;
        super.onPause();
    }

    @Override protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override public void onBackPressed() { finishWithResults(); }

    private void finishWithResults() {
        stopSensors(); stopGps(); stopNfc(); volumeRunning = false;
        Intent result = new Intent();
        for (Map.Entry<String,String> item : values.entrySet())
            result.putExtra(item.getKey(), item.getValue());
        if (Float.isFinite(gpsAccuracy) && VERIFIED.equals(values.get(EXTRA_GPS)))
            result.putExtra(EXTRA_GPS_ACCURACY, gpsAccuracy);
        setResult(RESULT_OK, result);
        finish();
    }

    private void set(String key, String status) {
        values.put(key, status);
        TextView text = outputs.get(key);
        if (text == null) return;
        String word;
        switch (status) {
            case VERIFIED: word = greek ? "ΕΠΑΛΗΘΕΥΤΗΚΕ" : "VERIFIED"; break;
            case USER_CONFIRMED: word = greek ? "ΕΠΙΒΕΒΑΙΩΣΗ ΧΡΗΣΤΗ" : "USER CONFIRMED"; break;
            case USER_FAILED: word = greek ? "ΔΗΛΩΘΗΚΕ ΔΥΣΛΕΙΤΟΥΡΓΙΑ" : "USER REPORTED FAILURE"; break;
            case UNSUPPORTED: word = greek ? "ΔΕΝ ΥΠΟΣΤΗΡΙΖΕΤΑΙ" : "NOT SUPPORTED"; break;
            case DISABLED: word = greek ? "ΑΠΕΝΕΡΓΟΠΟΙΗΜΕΝΟ" : "DISABLED"; break;
            case PERMISSION_DENIED: word = greek ? "ΔΕΝ ΔΟΘΗΚΕ ΑΔΕΙΑ" : "PERMISSION NOT GRANTED"; break;
            case NOT_VERIFIED: word = greek ? "ΔΕΝ ΕΠΑΛΗΘΕΥΤΗΚΕ" : "NOT VERIFIED"; break;
            default: word = greek ? "ΔΕΝ ΕΛΕΓΧΘΗΚΕ" : "NOT TESTED";
        }
        if (EXTRA_GPS.equals(key) && VERIFIED.equals(status) && Float.isFinite(gpsAccuracy))
            word += String.format(Locale.US, " (±%.0f m)", gpsAccuracy);
        text.setText(word);
        text.setTextColor(USER_FAILED.equals(status) ? 0xFFFF4444
                : (VERIFIED.equals(status) || USER_CONFIRMED.equals(status))
                        ? 0xFF39FF14 : Color.WHITE);
    }

    private void addGroup(LinearLayout parent, String title) {
        label(parent, title, 17, 0xFFFFD700, true);
    }

    private void addState(LinearLayout parent, String key, String name) {
        label(parent, name, 15, Color.WHITE, true);
        TextView text = label(parent, "", 14, Color.WHITE, false);
        outputs.put(key, text);
        set(key, values.get(key));
    }

    private TextView label(LinearLayout parent, String value, int sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextColor(color);
        t.setTextSize(sp);
        t.setPadding(0, uiDp(7), 0, uiDp(7));
        if (bold) t.setTypeface(null, Typeface.BOLD);
        parent.addView(t);
        return t;
    }

    private void action(LinearLayout parent, String text, Runnable action, boolean exit) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setTypeface(null, Typeface.BOLD);
        b.setTextColor(exit ? Color.WHITE : Color.BLACK);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(exit ? 0xFFE60000 : 0xFF39FF14);
        bg.setCornerRadius(uiDp(11));
        bg.setStroke(uiDp(2), 0xFFFFD700);
        b.setBackground(bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.topMargin = uiDp(12);
        p.bottomMargin = uiDp(14);
        parent.addView(b, p);
        b.setOnClickListener(v -> action.run());
    }

    private int uiDp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
