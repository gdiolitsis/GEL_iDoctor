// GDiolitsis Engine Lab (GEL) — Author & Developer
// RepairDeviceActivity.java
// iDoctor / GEL Professional Technician Service Session
// FIREBASE TEST — REAL Cloud Function session + Firestore live status + QR

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.ClipData;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.Query;
import com.google.firebase.functions.FirebaseFunctions;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;

public class RepairDeviceActivity extends GELAutoActivityHook {

    // ============================================================
    // GEL PRO ENTITLEMENT
    // ============================================================
    private static final String GEL_PRO_PREFS =
            "GEL_PRO_ENTITLEMENT";

    private static final String GEL_PRO_ACTIVE_KEY =
            "active";

    // ============================================================
    // TEMPORARY TEST UNLOCK
    // ============================================================
    // true  = Repair a Device works without GEL PRO during testing
    // false = normal production GEL PRO entitlement check
    // IMPORTANT: set to false before release.
    private static final boolean TEMP_TEST_UNLOCK = true;

    // ============================================================
    // SERVICE SESSION STORAGE
    // ============================================================
    private static final String SESSION_PREFS =
            "GEL_REPAIR_SESSION";

    private static final String KEY_SESSION_ID =
            "session_id";

    private static final String KEY_SERVICE_CODE =
            "service_code";

    private static final String KEY_CREATED_AT =
            "created_at";

    private static final String KEY_PAIRING_EXPIRES_AT =
            "pairing_expires_at";

    private static final String KEY_FIREBASE_BACKED =
            "firebase_backed";

    private static final String KEY_SESSION_CONNECTED =
            "session_connected";

    // ============================================================
    // PAIRING WINDOW
    // ============================================================
    // The QR / Service Code remains valid for 2 hours while waiting
    // for the customer's device to connect.
    //
    // IMPORTANT:
    // Once real device pairing/backend is added, the ACTIVE SERVICE
    // SESSION will be independent from this pairing-code timeout and
    // will remain active until the technician completes/cancels it.
    // ============================================================
    private static final long PAIRING_CODE_DURATION_MS =
            2L * 60L * 60L * 1000L; // 2 hours

    // ============================================================
    // UI
    // ============================================================
    private TextView txtStatus;
    private TextView txtSessionId;
    private TextView txtServiceCode;
    private TextView txtExpiry;
    private TextView txtQrPlaceholder;
    private ImageView imgQrCode;

    // ============================================================
    // LIVE REMOTE DIAGNOSTICS — TECHNICIAN VIEW
    // ============================================================
    private TextView txtRemoteDiagnosticsStatus;
    private TextView txtRemoteDiagnostics;

    private final StringBuilder remoteDiagnosticsBuffer =
            new StringBuilder();

    private int remoteDiagnosticLineCount = 0;

    private Button btnCreateSession;
    private Button btnCopyCode;
    private Button btnNewSession;
    private Button btnCancelSession;

    // ============================================================
    // REMOTE DEVICE MODE — TECHNICIAN
    // ============================================================
    private TextView txtRemoteControlStatus;
    private Button btnEnterRemoteMode;

    private boolean gr;

    // ============================================================
    // FIREBASE — REAL BACKEND
    // ============================================================
    private static final String FUNCTIONS_REGION =
            "europe-west1";

    private static final String SESSIONS_COLLECTION =
            "service_sessions";

    private FirebaseAuth firebaseAuth;
    private FirebaseFunctions firebaseFunctions;
    private FirebaseFirestore firestore;
    private ListenerRegistration sessionListener;
    private ListenerRegistration diagnosticsListener;

    private String diagnosticsSessionId = null;

    private final SecureRandom secureRandom =
            new SecureRandom();

    // ============================================================
    // LOCALE
    // ============================================================
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(
                LocaleHelper.apply(base)
        );
    }

    // ============================================================
    // ON CREATE
    // ============================================================
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        gr = AppLang.isGreek(this);

        firebaseAuth =
                FirebaseAuth.getInstance();

        firebaseFunctions =
                FirebaseFunctions.getInstance(
                        FUNCTIONS_REGION
                );

        firestore =
                FirebaseFirestore.getInstance();

        buildScreen();

        restoreExistingSession();

        UIHelpers.applyPressEffectRecursive(
                getWindow().getDecorView()
        );
    }

    @Override
    protected void onStart() {

        super.onStart();

        attachListenerForStoredFirebaseSession();
    }

    @Override
    protected void onStop() {

        removeSessionListener();
        removeDiagnosticsListener();

        super.onStop();
    }

    // ============================================================
    // MAIN SCREEN
    // ============================================================
    private void buildScreen() {

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(0xFF101010);

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                dp(16),
                dp(18),
                dp(16),
                dp(24)
        );

        root.setBackgroundColor(
                0xFF101010
        );

        // ========================================================
        // TITLE
        // ========================================================
        TextView title = new TextView(this);

        title.setText(
                gr
                        ? "Απομακρυσμένη Διάγνωση & Υποστήριξη Συσκευής"
                        : "Remote Device Diagnostics & Support"
        );

        title.setTextColor(
                Color.WHITE
        );

        title.setTextSize(23f);

        title.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        title.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        title.setPadding(
                0,
                0,
                0,
                dp(6)
        );

        root.addView(title);

        // ========================================================
        // SUBTITLE
        // ========================================================
        TextView subtitle = new TextView(this);

        subtitle.setText(
                gr
                        ? "Επαγγελματική σύνδεση και διάγνωση συσκευής πελάτη"
                        : "Professional customer-device connection and diagnostics"
        );

        subtitle.setTextColor(
                0xFFCCCCCC
        );

        subtitle.setTextSize(14f);

        subtitle.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        subtitle.setPadding(
                0,
                0,
                0,
                dp(18)
        );

        root.addView(subtitle);

        // ========================================================
        // TEMPORARY TEST BUILD BANNER
        // ========================================================
        TextView testBanner = new TextView(this);

        testBanner.setText(
                gr
                        ? "FIREBASE TEST — REAL BACKEND"
                        : "FIREBASE TEST — REAL BACKEND"
        );

        testBanner.setTextColor(
                0xFF39FF14
        );

        testBanner.setTextSize(
                14f
        );

        testBanner.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        testBanner.setGravity(
                Gravity.CENTER
        );

        testBanner.setPadding(
                dp(8),
                dp(8),
                dp(8),
                dp(10)
        );

        root.addView(testBanner);

        // ========================================================
        // TECHNICIAN STATUS
        // ========================================================
        TextView technicianTitle =
                sectionLabel(
                        "TECHNICIAN SERVICE"
                );

        root.addView(
                technicianTitle
        );

        txtStatus =
                createInfoText();

        txtStatus.setGravity(
                Gravity.CENTER
        );

        root.addView(
                wrapInCard(txtStatus)
        );

        // ========================================================
        // CREATE SESSION
        // ========================================================
        btnCreateSession =
                makeActionButton(
                        gr
                                ? "Δημιουργία Service Session"
                                : "Create Service Session"
                );

        btnCreateSession.setOnClickListener(
                v -> createServiceSession()
        );

        root.addView(
                btnCreateSession
        );

        // ========================================================
        // SESSION DETAILS
        // ========================================================
        TextView sessionLabel =
                sectionLabel(
                        gr
                                ? "ΣΤΟΙΧΕΙΑ ΣΥΝΔΕΣΗΣ"
                                : "CONNECTION DETAILS"
                );

        root.addView(
                sessionLabel
        );

        LinearLayout sessionCard =
                createCard();

        TextView idLabel =
                smallLabel(
                        "Service Session ID"
                );

        sessionCard.addView(
                idLabel
        );

        txtSessionId =
                createValueText();

        sessionCard.addView(
                txtSessionId
        );

        TextView codeLabel =
                smallLabel(
                        gr
                                ? "Κωδικός Σύνδεσης"
                                : "Service Code"
                );

        codeLabel.setPadding(
                0,
                dp(14),
                0,
                dp(4)
        );

        sessionCard.addView(
                codeLabel
        );

        txtServiceCode =
                new TextView(this);

        txtServiceCode.setTextColor(
                0xFF39FF14
        );

        txtServiceCode.setTextSize(
                30f
        );

        txtServiceCode.setTypeface(
                Typeface.MONOSPACE,
                Typeface.BOLD
        );

        txtServiceCode.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        txtServiceCode.setPadding(
                0,
                dp(4),
                0,
                dp(4)
        );

        sessionCard.addView(
                txtServiceCode
        );

        txtExpiry =
                createInfoText();

        txtExpiry.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        sessionCard.addView(
                txtExpiry
        );

        root.addView(
                sessionCard
        );

        // ========================================================
        // LIVE REMOTE DIAGNOSTICS
        // ========================================================
        root.addView(
                sectionLabel(
                        gr
                                ? "LIVE ΑΠΟΜΑΚΡΥΣΜΕΝΑ ΔΙΑΓΝΩΣΤΙΚΑ"
                                : "LIVE REMOTE DIAGNOSTICS"
                )
        );

        LinearLayout remoteDiagnosticsCard =
                createCard();

        txtRemoteDiagnosticsStatus =
                createInfoText();

        txtRemoteDiagnosticsStatus.setTextColor(
                0xFFFFD700
        );

        txtRemoteDiagnosticsStatus.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        txtRemoteDiagnosticsStatus.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        remoteDiagnosticsCard.addView(
                txtRemoteDiagnosticsStatus
        );

        txtRemoteDiagnostics =
                createInfoText();

        txtRemoteDiagnostics.setTypeface(
                Typeface.MONOSPACE
        );

        txtRemoteDiagnostics.setTextSize(
                13f
        );

        txtRemoteDiagnostics.setTextIsSelectable(
                true
        );

        txtRemoteDiagnostics.setPadding(
                0,
                dp(10),
                0,
                0
        );

        remoteDiagnosticsCard.addView(
                txtRemoteDiagnostics
        );

        root.addView(
                remoteDiagnosticsCard
        );

        showRemoteDiagnosticsNoSession();

        // ========================================================
        // REMOTE DEVICE MODE — FUNCTIONAL MIRROR FOUNDATION
        // ========================================================
        root.addView(
                sectionLabel(
                        gr
                                ? "REMOTE DEVICE MODE"
                                : "REMOTE DEVICE MODE"
                )
        );

        LinearLayout remoteControlCard =
                createCard();

        txtRemoteControlStatus =
                createInfoText();

        txtRemoteControlStatus.setGravity(
                Gravity.CENTER
        );

        txtRemoteControlStatus.setTextColor(
                0xFFFFD700
        );

        txtRemoteControlStatus.setText(
                gr
                        ? "Αναμονή για σύνδεση συσκευής πελάτη."
                        : "Waiting for customer device connection."
        );

        remoteControlCard.addView(
                txtRemoteControlStatus
        );

        btnEnterRemoteMode =
                makeActionButton(
                        gr
                                ? "Άνοιγμα Remote Device Mode"
                                : "Open Remote Device Mode"
                );

        btnEnterRemoteMode.setVisibility(
                View.GONE
        );

        btnEnterRemoteMode.setOnClickListener(
                v -> openRemoteDeviceMode()
        );

        remoteControlCard.addView(
                btnEnterRemoteMode
        );

        root.addView(
                remoteControlCard
        );

        // ========================================================
        // QR AREA
        // ========================================================
        TextView qrSection =
                sectionLabel(
                        gr
                                ? "SMART SERVICE QR"
                                : "SMART SERVICE QR"
                );

        root.addView(
                qrSection
        );

        LinearLayout qrCard =
                createCard();

        imgQrCode =
                new ImageView(this);

        imgQrCode.setAdjustViewBounds(
                true
        );

        imgQrCode.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

        imgQrCode.setBackgroundColor(
                Color.WHITE
        );

        imgQrCode.setPadding(
                dp(10),
                dp(10),
                dp(10),
                dp(10)
        );

        LinearLayout.LayoutParams qrImageLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(300)
                );

        qrImageLp.setMargins(
                dp(10),
                dp(10),
                dp(10),
                dp(8)
        );

        imgQrCode.setLayoutParams(
                qrImageLp
        );

        imgQrCode.setVisibility(
                View.GONE
        );

        qrCard.addView(
                imgQrCode
        );

        txtQrPlaceholder =
                new TextView(this);

        txtQrPlaceholder.setTextColor(
                0xFFCCCCCC
        );

        txtQrPlaceholder.setTextSize(
                15f
        );

        txtQrPlaceholder.setGravity(
                Gravity.CENTER
        );

        txtQrPlaceholder.setPadding(
                dp(14),
                dp(14),
                dp(14),
                dp(16)
        );

        qrCard.addView(
                txtQrPlaceholder
        );

        root.addView(
                qrCard
        );

        // ========================================================
        // COPY CODE
        // ========================================================
        btnCopyCode =
                makeActionButton(
                        gr
                                ? "Αντιγραφή Service Code"
                                : "Copy Service Code"
                );

        btnCopyCode.setOnClickListener(
                v -> copyServiceCode()
        );

        root.addView(
                btnCopyCode
        );

        // ========================================================
        // NEW PAIRING CODE / SESSION
        // ========================================================
        btnNewSession =
                makeActionButton(
                        gr
                                ? "Δημιουργία Νέου Κωδικού"
                                : "Generate New Code"
                );

        btnNewSession.setOnClickListener(
                v -> createServiceSession()
        );

        root.addView(
                btnNewSession
        );

        // ========================================================
        // CUSTOMER INSTRUCTIONS
        // ========================================================
        root.addView(
                sectionLabel(
                        gr
                                ? "ΟΔΗΓΙΕΣ ΣΥΝΔΕΣΗΣ ΣΥΣΚΕΥΗΣ ΠΕΛΑΤΗ"
                                : "CUSTOMER DEVICE CONNECTION"
                )
        );

        TextView instructions =
                createInfoText();

        instructions.setText(
                gr
                        ? "1. Ανοίξτε το iDoctor στη συσκευή του πελάτη.\n\n"
                        + "2. Επιλέξτε «Connect to Technician».\n\n"
                        + "3. Σαρώστε το QR ή εισαγάγετε τον 6ψήφιο Service Code.\n\n"
                        + "4. Επιτρέψτε τα απαραίτητα δικαιώματα διαγνωστικών ελέγχων.\n\n"
                        + "5. Η συσκευή θα συνδεθεί προσωρινά στο ενεργό Service Session του τεχνικού."
                        :
                        "1. Open iDoctor on the customer's device.\n\n"
                        + "2. Select \"Connect to Technician\".\n\n"
                        + "3. Scan the QR code or enter the 6-digit Service Code.\n\n"
                        + "4. Allow the required diagnostic permissions.\n\n"
                        + "5. The device will temporarily connect to the technician's active Service Session."
        );

        root.addView(
                wrapInCard(instructions)
        );

        // ========================================================
        // PRIVACY INFO
        // ========================================================
        root.addView(
                sectionLabel(
                        gr
                                ? "ΑΣΦΑΛΕΙΑ SESSION"
                                : "SESSION SECURITY"
                )
        );

        TextView privacy =
                createInfoText();

        privacy.setText(
                gr
                        ? "Η συνδρομή GEL PRO παραμένει στον λογαριασμό του τεχνικού. "
                        + "Η συσκευή του πελάτη δεν αποκτά μόνιμη πρόσβαση στη συνδρομή. "
                        + "Ο κωδικός σύνδεσης χρησιμοποιείται μόνο για το συγκεκριμένο Firebase Service Session."
                        :
                        "The GEL PRO entitlement remains with the technician. "
                        + "The customer's device does not receive permanent subscription access. "
                        + "The pairing code is used only for the specific Firebase Service Session."
        );

        root.addView(
                wrapInCard(privacy)
        );

        // ========================================================
        // CANCEL SESSION
        // ========================================================
        btnCancelSession =
                makeActionButton(
                        gr
                                ? "Ακύρωση Service Session"
                                : "Cancel Service Session"
                );

        btnCancelSession.setOnClickListener(
                v -> cancelCurrentSession()
        );

        root.addView(
                btnCancelSession
        );

        // ========================================================
        // BACK
        // ========================================================
        Button back =
                makeActionButton(
                        gr
                                ? "Επιστροφή"
                                : "Back"
                );

        back.setOnClickListener(
                v -> finish()
        );

        root.addView(
                back
        );

        scroll.addView(
                root
        );

        setContentView(
                scroll
        );

        showNoSessionState();
    }

    // ============================================================
    // CREATE SERVICE SESSION
    // ============================================================
    private void createServiceSession() {

        btnCreateSession.setEnabled(
                false
        );

        txtStatus.setText(
                gr
                        ? "● FIREBASE\nΈλεγχος ταυτότητας τεχνικού..."
                        : "● FIREBASE\nAuthenticating technician..."
        );

        txtStatus.setTextColor(
                0xFFFFD700
        );

        FirebaseUser currentUser =
                firebaseAuth.getCurrentUser();

        if (currentUser != null) {

            callCreateServiceSession();

            return;
        }

        firebaseAuth
                .signInAnonymously()
                .addOnCompleteListener(
                        this,
                        task -> {

                            if (!task.isSuccessful() ||
                                    firebaseAuth.getCurrentUser() == null) {

                                btnCreateSession.setEnabled(
                                        true
                                );

                                txtStatus.setText(
                                        gr
                                                ? "● ΑΠΟΤΥΧΙΑ FIREBASE AUTH\nΔεν ήταν δυνατή η σύνδεση με το backend."
                                                : "● FIREBASE AUTH FAILED\nCould not authenticate with the backend."
                                );

                                txtStatus.setTextColor(
                                        0xFFFF5555
                                );

                                Toast.makeText(
                                        this,
                                        gr
                                                ? "Αποτυχία Firebase Authentication."
                                                : "Firebase Authentication failed.",
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            callCreateServiceSession();
                        }
                );
    }

    // ============================================================
    // CALL REAL CLOUD FUNCTION
    // ============================================================
    private void callCreateServiceSession() {

        txtStatus.setText(
                gr
                        ? "● FIREBASE\nΔημιουργία πραγματικού Service Session..."
                        : "● FIREBASE\nCreating real Service Session..."
        );

        txtStatus.setTextColor(
                0xFFFFD700
        );

        firebaseFunctions
                .getHttpsCallable(
                        "createServiceSession"
                )
                .call()
                .addOnCompleteListener(
                        this,
                        task -> {

                            btnCreateSession.setEnabled(
                                    true
                            );

                            if (!task.isSuccessful() ||
                                    task.getResult() == null) {

                                txtStatus.setText(
                                        gr
                                                ? "● ΑΠΟΤΥΧΙΑ BACKEND\nΔεν δημιουργήθηκε Service Session."
                                                : "● BACKEND ERROR\nService Session was not created."
                                );

                                txtStatus.setTextColor(
                                        0xFFFF5555
                                );

                                String message =
                                        task.getException() != null
                                                ? task.getException().getMessage()
                                                : null;

                                Toast.makeText(
                                        this,
                                        message != null
                                                ? message
                                                : (
                                                gr
                                                        ? "Άγνωστο σφάλμα Firebase Functions."
                                                        : "Unknown Firebase Functions error."
                                        ),
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            Object raw =
                                    task.getResult()
                                            .getData();

                            if (!(raw instanceof Map)) {

                                txtStatus.setText(
                                        gr
                                                ? "● ΜΗ ΕΓΚΥΡΗ ΑΠΑΝΤΗΣΗ SERVER"
                                                : "● INVALID SERVER RESPONSE"
                                );

                                txtStatus.setTextColor(
                                        0xFFFF5555
                                );

                                return;
                            }

                            Map<?, ?> result =
                                    (Map<?, ?>) raw;

                            Object sessionRaw =
                                    result.get(
                                            "sessionId"
                                    );

                            Object codeRaw =
                                    result.get(
                                            "serviceCode"
                                    );

                            Object expiryRaw =
                                    result.get(
                                            "expiresAt"
                                    );

                            if (!(sessionRaw instanceof String) ||
                                    !(codeRaw instanceof String) ||
                                    !(expiryRaw instanceof Number)) {

                                txtStatus.setText(
                                        gr
                                                ? "● ΕΛΛΙΠΗΣ ΑΠΑΝΤΗΣΗ SERVER"
                                                : "● INCOMPLETE SERVER RESPONSE"
                                );

                                txtStatus.setTextColor(
                                        0xFFFF5555
                                );

                                return;
                            }

                            String sessionId =
                                    (String) sessionRaw;

                            String serviceCode =
                                    (String) codeRaw;

                            long pairingExpires =
                                    ((Number) expiryRaw)
                                            .longValue();

                            long now =
                                    System.currentTimeMillis();

                            removeSessionListener();
                            removeDiagnosticsListener();
                            showRemoteDiagnosticsWaitingForConnection();

                            SharedPreferences prefs =
                                    getSharedPreferences(
                                            SESSION_PREFS,
                                            MODE_PRIVATE
                                    );

                            prefs.edit()
                                    .putString(
                                            KEY_SESSION_ID,
                                            sessionId
                                    )
                                    .putString(
                                            KEY_SERVICE_CODE,
                                            serviceCode
                                    )
                                    .putLong(
                                            KEY_CREATED_AT,
                                            now
                                    )
                                    .putLong(
                                            KEY_PAIRING_EXPIRES_AT,
                                            pairingExpires
                                    )
                                    .putBoolean(
                                            KEY_FIREBASE_BACKED,
                                            true
                                    )
                                    .putBoolean(
                                            KEY_SESSION_CONNECTED,
                                            false
                                    )
                                    .apply();

                            showSession(
                                    sessionId,
                                    serviceCode,
                                    pairingExpires
                            );

                            attachSessionListener(
                                    sessionId
                            );
                        }
                );
    }

    // ============================================================
    // RESTORE SESSION
    // ============================================================
    private void restoreExistingSession() {

        SharedPreferences prefs =
                getSharedPreferences(
                        SESSION_PREFS,
                        MODE_PRIVATE
                );

        boolean firebaseBacked =
                prefs.getBoolean(
                        KEY_FIREBASE_BACKED,
                        false
                );

        // Remove old local-only test sessions from earlier builds.
        if (!firebaseBacked) {

            clearStoredSession();

            showNoSessionState();

            return;
        }

        String sessionId =
                prefs.getString(
                        KEY_SESSION_ID,
                        null
                );

        String serviceCode =
                prefs.getString(
                        KEY_SERVICE_CODE,
                        null
                );

        long pairingExpires =
                prefs.getLong(
                        KEY_PAIRING_EXPIRES_AT,
                        0L
                );

        boolean connected =
                prefs.getBoolean(
                        KEY_SESSION_CONNECTED,
                        false
                );

        if (sessionId == null ||
                serviceCode == null ||
                pairingExpires <= 0L) {

            showNoSessionState();

            return;
        }

        // Pairing expiry blocks new claims, but it does NOT terminate
        // a Service Session that was already connected.
        if (!connected &&
                System.currentTimeMillis() >= pairingExpires) {

            clearStoredSession();

            showNoSessionState();

            Toast.makeText(
                    this,
                    gr
                            ? "Ο προηγούμενος κωδικός σύνδεσης έληξε. Δημιουργήστε νέο Service Session."
                            : "The previous pairing code expired. Create a new Service Session.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        showSession(
                sessionId,
                serviceCode,
                pairingExpires
        );

        if (connected) {

            showConnectedState();
        }
    }

    // ============================================================
    // FIRESTORE REAL-TIME SESSION LISTENER
    // ============================================================
    private void attachListenerForStoredFirebaseSession() {

        SharedPreferences prefs =
                getSharedPreferences(
                        SESSION_PREFS,
                        MODE_PRIVATE
                );

        if (!prefs.getBoolean(
                KEY_FIREBASE_BACKED,
                false
        )) {

            return;
        }

        String sessionId =
                prefs.getString(
                        KEY_SESSION_ID,
                        null
                );

        if (sessionId == null ||
                sessionId.trim().isEmpty()) {

            return;
        }

        attachSessionListener(
                sessionId
        );
    }

    private void attachSessionListener(String sessionId) {

        if (firestore == null ||
                sessionId == null ||
                sessionId.trim().isEmpty()) {

            return;
        }

        removeSessionListener();

        sessionListener =
                firestore
                        .collection(
                                SESSIONS_COLLECTION
                        )
                        .document(
                                sessionId
                        )
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        removeDiagnosticsListener();
                                        showRemoteDiagnosticsError(
                                                gr
                                                        ? "Η σύνδεση diagnostics διακόπηκε προσωρινά."
                                                        : "Diagnostics connection is temporarily unavailable."
                                        );

                                        txtStatus.setText(
                                                gr
                                                        ? "● FIREBASE OFFLINE / ERROR\nΑναμονή για επανασύνδεση..."
                                                        : "● FIREBASE OFFLINE / ERROR\nWaiting to reconnect..."
                                        );

                                        txtStatus.setTextColor(
                                                0xFFFFD700
                                        );

                                        return;
                                    }

                                    if (snapshot == null ||
                                            !snapshot.exists()) {

                                        removeDiagnosticsListener();
                                        showRemoteDiagnosticsError(
                                                gr
                                                        ? "Το Service Session δεν βρέθηκε."
                                                        : "Service Session was not found."
                                        );

                                        txtStatus.setText(
                                                gr
                                                        ? "● ΤΟ SESSION ΔΕΝ ΒΡΕΘΗΚΕ ΣΤΟ FIREBASE"
                                                        : "● SESSION NOT FOUND IN FIREBASE"
                                        );

                                        txtStatus.setTextColor(
                                                0xFFFF5555
                                        );

                                        return;
                                    }

                                    String status =
                                            snapshot.getString(
                                                    "status"
                                            );

                                    if ("CONNECTED".equals(status)) {

                                        getSharedPreferences(
                                                SESSION_PREFS,
                                                MODE_PRIVATE
                                        )
                                                .edit()
                                                .putBoolean(
                                                        KEY_SESSION_CONNECTED,
                                                        true
                                                )
                                                .apply();

                                        showConnectedState();

                                        attachDiagnosticsListener(
                                                sessionId
                                        );

                                        renderRemoteCommandState(
                                                snapshot.get(
                                                        "remoteCommand"
                                                )
                                        );

                                        return;
                                    }

                                    if ("WAITING".equals(status)) {

                                        removeDiagnosticsListener();
                                        showRemoteDiagnosticsWaitingForConnection();
                                        GELRemoteTargetManager.exitRemoteMode(this);
                                        showRemoteControlWaiting();

                                        getSharedPreferences(
                                                SESSION_PREFS,
                                                MODE_PRIVATE
                                        )
                                                .edit()
                                                .putBoolean(
                                                        KEY_SESSION_CONNECTED,
                                                        false
                                                )
                                                .apply();

                                        txtStatus.setText(
                                                gr
                                                        ? "● ΕΝΕΡΓΟ FIREBASE SERVICE SESSION\nΑναμονή για σύνδεση συσκευής..."
                                                        : "● ACTIVE FIREBASE SERVICE SESSION\nWaiting for customer device..."
                                        );

                                        txtStatus.setTextColor(
                                                0xFF39FF14
                                        );

                                        return;
                                    }

                                    removeDiagnosticsListener();
                                    GELRemoteTargetManager.exitRemoteMode(this);
                                    showRemoteControlWaiting();

                                    showRemoteDiagnosticsError(
                                            gr
                                                    ? "Το Service Session δεν είναι πλέον CONNECTED."
                                                    : "Service Session is no longer CONNECTED."
                                    );

                                    txtStatus.setText(
                                            gr
                                                    ? "● FIREBASE SESSION: " + String.valueOf(status)
                                                    : "● FIREBASE SESSION: " + String.valueOf(status)
                                    );

                                    txtStatus.setTextColor(
                                            0xFFFFD700
                                    );
                                }
                        );
    }

    private void removeSessionListener() {

        if (sessionListener != null) {

            sessionListener.remove();

            sessionListener = null;
        }
    }

    // ============================================================
    // FIRESTORE REAL-TIME DIAGNOSTICS LISTENER
    // ============================================================
    private void attachDiagnosticsListener(String sessionId) {

        if (firestore == null ||
                sessionId == null ||
                sessionId.trim().isEmpty()) {

            return;
        }

        // Parent service_sessions document is updated after each diagnostic
        // batch. Do not tear down/recreate the diagnostics listener every time.
        if (diagnosticsListener != null &&
                sessionId.equals(diagnosticsSessionId)) {

            return;
        }

        removeDiagnosticsListener();

        diagnosticsSessionId =
                sessionId;

        remoteDiagnosticsBuffer.setLength(
                0
        );

        remoteDiagnosticLineCount =
                0;

        showRemoteDiagnosticsConnectedWaiting();

        diagnosticsListener =
                firestore
                        .collection(
                                SESSIONS_COLLECTION
                        )
                        .document(
                                sessionId
                        )
                        .collection(
                                "diagnostics"
                        )
                        .orderBy(
                                "createdAt",
                                Query.Direction.ASCENDING
                        )
                        .limitToLast(
                                200
                        )
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        showRemoteDiagnosticsError(
                                                gr
                                                        ? "Σφάλμα λήψης remote diagnostics. Αναμονή για επανασύνδεση..."
                                                        : "Remote diagnostics listener error. Waiting to reconnect..."
                                        );

                                        return;
                                    }

                                    if (snapshot == null) {
                                        return;
                                    }

                                    for (DocumentChange change :
                                            snapshot.getDocumentChanges()) {

                                        if (change.getType() !=
                                                DocumentChange.Type.ADDED) {

                                            continue;
                                        }

                                        Object rawLines =
                                                change
                                                        .getDocument()
                                                        .get(
                                                                "lines"
                                                        );

                                        if (!(rawLines instanceof List)) {
                                            continue;
                                        }

                                        List<?> lines =
                                                (List<?>) rawLines;

                                        for (Object rawLine : lines) {

                                            if (!(rawLine instanceof String)) {
                                                continue;
                                            }

                                            appendRemoteDiagnosticLine(
                                                    (String) rawLine
                                            );
                                        }
                                    }
                                }
                        );
    }

    private void removeDiagnosticsListener() {

        if (diagnosticsListener != null) {

            diagnosticsListener.remove();

            diagnosticsListener = null;
        }

        diagnosticsSessionId =
                null;
    }

    private void appendRemoteDiagnosticLine(String line) {

        if (line == null) {
            return;
        }

        String clean =
                line.trim();

        if (clean.isEmpty()) {
            return;
        }

        remoteDiagnosticLineCount++;

        if (remoteDiagnosticsBuffer.length() > 0) {
            remoteDiagnosticsBuffer.append(
                    "\n"
            );
        }

        remoteDiagnosticsBuffer.append(
                clean
        );

        // Keep the technician screen responsive during very long sessions.
        // Firestore remains the canonical history; this only caps the local UI.
        final int MAX_UI_CHARS =
                60000;

        final int KEEP_UI_CHARS =
                50000;

        if (remoteDiagnosticsBuffer.length() >
                MAX_UI_CHARS) {

            int cut =
                    remoteDiagnosticsBuffer.length() -
                            KEEP_UI_CHARS;

            int nextLine =
                    remoteDiagnosticsBuffer.indexOf(
                            "\n",
                            cut
                    );

            if (nextLine >= 0) {
                cut =
                        nextLine + 1;
            }

            remoteDiagnosticsBuffer.delete(
                    0,
                    Math.min(
                            cut,
                            remoteDiagnosticsBuffer.length()
                    )
            );
        }

        if (txtRemoteDiagnostics != null) {

            txtRemoteDiagnostics.setText(
                    remoteDiagnosticsBuffer.toString()
            );
        }

        if (txtRemoteDiagnosticsStatus != null) {

            txtRemoteDiagnosticsStatus.setText(
                    gr
                            ? "● LIVE — " + remoteDiagnosticLineCount
                            + " γραμμές diagnostics από τη συσκευή πελάτη"
                            : "● LIVE — " + remoteDiagnosticLineCount
                            + " diagnostic lines from customer device"
            );

            txtRemoteDiagnosticsStatus.setTextColor(
                    0xFF39FF14
            );
        }
    }

    private void showRemoteDiagnosticsNoSession() {

        if (txtRemoteDiagnosticsStatus == null ||
                txtRemoteDiagnostics == null) {

            return;
        }

        remoteDiagnosticsBuffer.setLength(
                0
        );

        remoteDiagnosticLineCount =
                0;

        txtRemoteDiagnosticsStatus.setText(
                gr
                        ? "● ΔΕΝ ΥΠΑΡΧΕΙ SERVICE SESSION"
                        : "● NO SERVICE SESSION"
        );

        txtRemoteDiagnosticsStatus.setTextColor(
                0xFFAAAAAA
        );

        txtRemoteDiagnostics.setText(
                gr
                        ? "Δημιουργήστε Service Session για να ενεργοποιηθεί η απομακρυσμένη ροή diagnostics."
                        : "Create a Service Session to enable the remote diagnostics stream."
        );
    }

    private void showRemoteDiagnosticsWaitingForConnection() {

        if (txtRemoteDiagnosticsStatus == null ||
                txtRemoteDiagnostics == null) {

            return;
        }

        remoteDiagnosticsBuffer.setLength(
                0
        );

        remoteDiagnosticLineCount =
                0;

        txtRemoteDiagnosticsStatus.setText(
                gr
                        ? "● ΑΝΑΜΟΝΗ ΓΙΑ ΣΥΣΚΕΥΗ ΠΕΛΑΤΗ"
                        : "● WAITING FOR CUSTOMER DEVICE"
        );

        txtRemoteDiagnosticsStatus.setTextColor(
                0xFFFFD700
        );

        txtRemoteDiagnostics.setText(
                gr
                        ? "Τα αποτελέσματα των tests θα εμφανίζονται εδώ αυτόματα μετά το CONNECTED."
                        : "Test results will appear here automatically after CONNECTED."
        );
    }

    private void showRemoteDiagnosticsConnectedWaiting() {

        if (txtRemoteDiagnosticsStatus == null ||
                txtRemoteDiagnostics == null) {

            return;
        }

        if (remoteDiagnosticLineCount > 0) {
            return;
        }

        txtRemoteDiagnosticsStatus.setText(
                gr
                        ? "● CONNECTED — ΑΝΑΜΟΝΗ DIAGNOSTICS"
                        : "● CONNECTED — WAITING FOR DIAGNOSTICS"
        );

        txtRemoteDiagnosticsStatus.setTextColor(
                0xFF39FF14
        );

        txtRemoteDiagnostics.setText(
                gr
                        ? "Η συσκευή πελάτη είναι συνδεδεμένη. Εκτελέστε ένα test στη συσκευή πελάτη."
                        : "Customer device is connected. Run a test on the customer device."
        );
    }

    private void showRemoteDiagnosticsError(String message) {

        if (txtRemoteDiagnosticsStatus == null ||
                txtRemoteDiagnostics == null) {

            return;
        }

        txtRemoteDiagnosticsStatus.setText(
                gr
                        ? "● REMOTE DIAGNOSTICS — ΠΡΟΣΩΡΙΝΟ ΣΦΑΛΜΑ"
                        : "● REMOTE DIAGNOSTICS — TEMPORARY ERROR"
        );

        txtRemoteDiagnosticsStatus.setTextColor(
                0xFFFFD700
        );

        if (remoteDiagnosticLineCount == 0) {

            txtRemoteDiagnostics.setText(
                    message != null
                            ? message
                            : ""
            );
        }
    }

    // ============================================================
    // REMOTE DEVICE MODE — TECHNICIAN CONTROL
    // ============================================================
    private void openRemoteDeviceMode() {

        if (!GELRemoteTargetManager.enterRemoteMode(this)) {

            Toast.makeText(
                    this,
                    gr
                            ? "Δεν υπάρχει CONNECTED συσκευή πελάτη."
                            : "No CONNECTED customer device.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Intent intent =
                new Intent(
                        this,
                        MainActivity.class
                );

        intent.putExtra(
                "skip_welcome_once",
                true
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(
                intent
        );
    }

    private void showRemoteControlReady() {

        if (txtRemoteControlStatus == null ||
                btnEnterRemoteMode == null) {

            return;
        }

        txtRemoteControlStatus.setText(
                gr
                        ? "● READY — Η συσκευή πελάτη μπορεί να δεχτεί ασφαλείς remote εντολές."
                        : "● READY — Customer device can receive allowlisted remote commands."
        );

        txtRemoteControlStatus.setTextColor(
                0xFF39FF14
        );

        btnEnterRemoteMode.setVisibility(
                View.VISIBLE
        );
    }

    private void showRemoteControlWaiting() {

        if (txtRemoteControlStatus == null ||
                btnEnterRemoteMode == null) {

            return;
        }

        txtRemoteControlStatus.setText(
                gr
                        ? "Αναμονή για CONNECTED συσκευή πελάτη."
                        : "Waiting for CONNECTED customer device."
        );

        txtRemoteControlStatus.setTextColor(
                0xFFFFD700
        );

        btnEnterRemoteMode.setVisibility(
                View.GONE
        );
    }

    private void renderRemoteCommandState(
            Object raw
    ) {

        if (txtRemoteControlStatus == null ||
                !(raw instanceof Map)) {

            return;
        }

        Map<?, ?> command =
                (Map<?, ?>) raw;

        Object actionRaw =
                command.get(
                        "action"
                );

        Object statusRaw =
                command.get(
                        "status"
                );

        if (actionRaw == null ||
                statusRaw == null) {
            return;
        }

        String action =
                String.valueOf(
                        actionRaw
                );

        String status =
                String.valueOf(
                        statusRaw
                );

        txtRemoteControlStatus.setText(
                "● " + status + " — " + action
        );

        txtRemoteControlStatus.setTextColor(
                "FAILED".equals(status)
                        ? 0xFFFF5555
                        : (
                        "SUCCESS".equals(status)
                                ? 0xFF39FF14
                                : 0xFFFFD700
                )
        );
    }

    private void showConnectedState() {

        showRemoteDiagnosticsConnectedWaiting();
        showRemoteControlReady();

        txtStatus.setText(
                gr
                        ? "● ΣΥΣΚΕΥΗ ΠΕΛΑΤΗ ΣΥΝΔΕΘΗΚΕ\nFirebase real-time pairing ενεργό."
                        : "● CUSTOMER DEVICE CONNECTED\nFirebase real-time pairing is active."
        );

        txtStatus.setTextColor(
                0xFF39FF14
        );

        txtExpiry.setText(
                gr
                        ? "Η σύνδεση είναι ενεργή. Η λήξη του pairing code δεν διακόπτει το Service Session."
                        : "Connection is active. Pairing-code expiry does not terminate the Service Session."
        );

        if (imgQrCode != null) {

            imgQrCode.setImageDrawable(
                    null
            );

            imgQrCode.setVisibility(
                    View.GONE
            );
        }

        txtQrPlaceholder.setText(
                gr
                        ? "✓ Η συσκευή πελάτη συνδέθηκε επιτυχώς μέσω Firebase."
                        : "✓ Customer device connected successfully through Firebase."
        );

        btnCopyCode.setVisibility(
                View.GONE
        );

        // Server-side regenerate/cancel will be added as dedicated
        // callable functions. Hide local-only controls in real mode.
        btnNewSession.setVisibility(
                View.GONE
        );

        btnCancelSession.setVisibility(
                View.GONE
        );
    }

    // ============================================================
    // DISPLAY ACTIVE SESSION
    // ============================================================
    private void showSession(
            String sessionId,
            String serviceCode,
            long pairingExpires
    ) {

        showRemoteDiagnosticsWaitingForConnection();
        showRemoteControlWaiting();

        txtStatus.setText(
                gr
                        ? "● ΕΝΕΡΓΟ FIREBASE SERVICE SESSION\nΑναμονή για σύνδεση συσκευής..."
                        : "● ACTIVE FIREBASE SERVICE SESSION\nWaiting for customer device..."
        );

        txtStatus.setTextColor(
                0xFF39FF14
        );

        txtSessionId.setText(
                sessionId
        );

        txtServiceCode.setText(
                serviceCode
        );

        SimpleDateFormat formatter =
                new SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                );

        String expiryText =
                formatter.format(
                        new Date(pairingExpires)
                );

        txtExpiry.setText(
                gr
                        ? "Ο κωδικός σύνδεσης ισχύει έως " + expiryText
                        : "Pairing code valid until " + expiryText
        );

        String qrPayload =
                buildQrPayload(
                        sessionId,
                        serviceCode,
                        pairingExpires
                );

        Bitmap qrBitmap =
                generateQrBitmap(
                        qrPayload,
                        dp(720)
                );

        if (qrBitmap != null) {

            imgQrCode.setImageBitmap(
                    qrBitmap
            );

            imgQrCode.setVisibility(
                    View.VISIBLE
            );

            txtQrPlaceholder.setText(
                    gr
                            ? "Σαρώστε μία φορά: ανοίγει το iDoctor ή οδηγεί στην εγκατάσταση. Ο Service Code παραμένει διαθέσιμος ως εφεδρεία."
                            : "Scan once: opens iDoctor or takes the customer to installation. The Service Code remains available as a fallback."
            );

        } else {

            imgQrCode.setVisibility(
                    View.GONE
            );

            txtQrPlaceholder.setText(
                    gr
                            ? "Δεν ήταν δυνατή η δημιουργία QR. Χρησιμοποιήστε τον 6ψήφιο Service Code."
                            : "QR generation failed. Use the 6-digit Service Code."
            );
        }

        btnCreateSession.setVisibility(
                View.GONE
        );

        btnCopyCode.setVisibility(
                View.VISIBLE
        );

        // In real Firebase mode these controls stay hidden until
        // dedicated server-side regenerate/cancel functions are deployed.
        btnNewSession.setVisibility(
                View.GONE
        );

        btnCancelSession.setVisibility(
                View.GONE
        );
    }

    // ============================================================
    // NO SESSION STATE
    // ============================================================
    private void showNoSessionState() {

        removeDiagnosticsListener();
        showRemoteDiagnosticsNoSession();
        GELRemoteTargetManager.exitRemoteMode(this);
        showRemoteControlWaiting();

        txtStatus.setText(
                gr
                        ? "● FIREBASE TEST — REAL BACKEND\nΔεν υπάρχει ενεργό Service Session."
                        : "● FIREBASE TEST — REAL BACKEND\nNo active Service Session."
        );

        txtStatus.setTextColor(
                0xFF39FF14
        );

        txtSessionId.setText(
                "—"
        );

        txtServiceCode.setText(
                "------"
        );

        txtExpiry.setText(
                ""
        );

        if (imgQrCode != null) {

            imgQrCode.setImageDrawable(
                    null
            );

            imgQrCode.setVisibility(
                    View.GONE
            );
        }

        txtQrPlaceholder.setText(
                gr
                        ? "Δημιουργήστε Service Session για να εμφανιστεί QR / Service Code."
                        : "Create a Service Session to display QR / Service Code."
        );

        btnCreateSession.setVisibility(
                View.VISIBLE
        );

        btnCopyCode.setVisibility(
                View.GONE
        );

        btnNewSession.setVisibility(
                View.GONE
        );

        btnCancelSession.setVisibility(
                View.GONE
        );
    }

    // ============================================================
    // CANCEL SESSION
    // ============================================================
    private void cancelCurrentSession() {

        new AlertDialog.Builder(this)
                .setTitle(
                        gr
                                ? "Ακύρωση Service Session"
                                : "Cancel Service Session"
                )
                .setMessage(
                        gr
                                ? "Θέλετε να ακυρώσετε το ενεργό Service Session;"
                                : "Do you want to cancel the active Service Session?"
                )
                .setNegativeButton(
                        gr
                                ? "Όχι"
                                : "No",
                        null
                )
                .setPositiveButton(
                        gr
                                ? "Ακύρωση Session"
                                : "Cancel Session",
                        (dialog, which) -> {

                            clearStoredSession();

                            showNoSessionState();

                            Toast.makeText(
                                    this,
                                    gr
                                            ? "Το Service Session ακυρώθηκε."
                                            : "Service Session cancelled.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .show();
    }

    // ============================================================
    // COPY SERVICE CODE
    // ============================================================
    private void copyServiceCode() {

        String code =
                txtServiceCode
                        .getText()
                        .toString()
                        .trim();

        if (code.isEmpty() ||
                "------".equals(code)) {

            return;
        }

        ClipboardManager clipboard =
                (ClipboardManager)
                        getSystemService(
                                CLIPBOARD_SERVICE
                        );

        if (clipboard == null) {

            return;
        }

        ClipData clip =
                ClipData.newPlainText(
                        "GEL Service Code",
                        code
                );

        clipboard.setPrimaryClip(
                clip
        );

        Toast.makeText(
                this,
                gr
                        ? "Ο Service Code αντιγράφηκε."
                        : "Service Code copied.",
                Toast.LENGTH_SHORT
        ).show();
    }

    // ============================================================
    // GENERATE 6-DIGIT SERVICE CODE
    // ============================================================
    private String generateServiceCode() {

        int code =
                100000 +
                secureRandom.nextInt(
                        900000
                );

        return String.valueOf(
                code
        );
    }

    // ============================================================
    // GENERATE SESSION ID
    // ============================================================
    private String generateSessionId() {

        String timestamp =
                new SimpleDateFormat(
                        "yyyyMMdd-HHmmss",
                        Locale.US
                ).format(
                        new Date()
                );

        int suffix =
                100 +
                secureRandom.nextInt(
                        900
                );

        return "GEL-" +
                timestamp +
                "-" +
                suffix;
    }

    // ============================================================
    // SMART SERVICE QR PAYLOAD
    // ============================================================
    private String buildQrPayload(
            String sessionId,
            String serviceCode,
            long pairingExpires
    ) {

        // One HTTPS QR for the technician workflow.
        // Installed iDoctor: opens the customer pairing flow.
        // Not installed: Firebase Hosting sends the customer to Google Play
        // while preserving this same Service Session through Install Referrer.
        return GELSmartServiceLink.buildSmartLink(
                sessionId,
                serviceCode,
                pairingExpires
        );
    }

    // ============================================================
    // LOCAL QR GENERATION — ZXING CORE
    // ============================================================
    private Bitmap generateQrBitmap(
            String payload,
            int requestedSize
    ) {

        if (payload == null ||
                payload.trim().isEmpty()) {

            return null;
        }

        int size =
                Math.max(
                        dp(260),
                        requestedSize
                );

        try {

            Map<EncodeHintType, Object> hints =
                    new HashMap<>();

            hints.put(
                    EncodeHintType.CHARACTER_SET,
                    "UTF-8"
            );

            hints.put(
                    EncodeHintType.ERROR_CORRECTION,
                    ErrorCorrectionLevel.M
            );

            hints.put(
                    EncodeHintType.MARGIN,
                    1
            );

            QRCodeWriter writer =
                    new QRCodeWriter();

            BitMatrix matrix =
                    writer.encode(
                            payload,
                            BarcodeFormat.QR_CODE,
                            size,
                            size,
                            hints
                    );

            Bitmap bitmap =
                    Bitmap.createBitmap(
                            size,
                            size,
                            Bitmap.Config.ARGB_8888
                    );

            for (int y = 0; y < size; y++) {

                for (int x = 0; x < size; x++) {

                    bitmap.setPixel(
                            x,
                            y,
                            matrix.get(x, y)
                                    ? Color.BLACK
                                    : Color.WHITE
                    );
                }
            }

            return bitmap;

        } catch (WriterException |
                 IllegalArgumentException e) {

            return null;
        }
    }

    // ============================================================
    // CLEAR SESSION
    // ============================================================
    private void clearStoredSession() {

        getSharedPreferences(
                SESSION_PREFS,
                MODE_PRIVATE
        )
                .edit()
                .clear()
                .apply();
    }

    // ============================================================
    // GEL PRO
    // ============================================================
    private boolean isGelProActive() {

        // TEST BUILD: intentionally unlocked.
        return true;
    }

    private void showGelProRequiredDialog() {

        // ========================================================
        // GEL DARK-GOLD POPUP
        // Use a plain Dialog so the Android AlertDialog theme
        // cannot force a white panel/background.
        // ========================================================
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(
                Window.FEATURE_NO_TITLE
        );

        LinearLayout box = new LinearLayout(this);

        box.setOrientation(
                LinearLayout.VERTICAL
        );

        box.setPadding(
                dp(20),
                dp(18),
                dp(20),
                dp(16)
        );

        GradientDrawable boxBg =
                new GradientDrawable();

        boxBg.setColor(
                0xFF0B0B0B
        );

        boxBg.setCornerRadius(
                dp(14)
        );

        boxBg.setStroke(
                dp(2),
                0xFFFFD700
        );

        box.setBackground(
                boxBg
        );

        // ========================================================
        // TITLE
        // ========================================================
        TextView title =
                new TextView(this);

        title.setText(
                "🔒 GEL PRO — Technician Service"
        );

        title.setTextColor(
                0xFFFFD700
        );

        title.setTextSize(
                19f
        );

        title.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        title.setGravity(
                Gravity.CENTER
        );

        title.setPadding(
                0,
                dp(2),
                0,
                dp(14)
        );

        box.addView(
                title
        );

        // ========================================================
        // DIVIDER
        // ========================================================
        View divider =
                new View(this);

        GradientDrawable dividerBg =
                new GradientDrawable();

        dividerBg.setColor(
                0xFFFFD700
        );

        divider.setBackground(
                dividerBg
        );

        LinearLayout.LayoutParams dividerLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        dividerLp.setMargins(
                0,
                0,
                0,
                dp(16)
        );

        divider.setLayoutParams(
                dividerLp
        );

        box.addView(
                divider
        );

        // ========================================================
        // MESSAGE
        // ========================================================
        TextView message =
                new TextView(this);

        message.setText(
                gr
                        ? "Η λειτουργία «Repair a Device» είναι διαθέσιμη μόνο σε επαγγελματίες τεχνικούς με ενεργή συνδρομή GEL PRO.\n\n"
                        + "Η συνδρομή ενεργοποιεί τα Technician Service Sessions για τη σύνδεση και διάγνωση συσκευών πελατών."
                        :
                        "“Repair a Device” is available only to professional technicians with an active GEL PRO subscription.\n\n"
                        + "The subscription enables Technician Service Sessions for connecting and diagnosing customer devices."
        );

        message.setTextColor(
                0xFFE6E6E6
        );

        message.setTextSize(
                15f
        );

        message.setGravity(
                Gravity.CENTER
        );

        message.setLineSpacing(
                0f,
                1.20f
        );

        message.setPadding(
                dp(4),
                0,
                dp(4),
                dp(18)
        );

        box.addView(
                message
        );

        // ========================================================
        // OK BUTTON
        // ========================================================
        Button ok =
                new Button(this);

        ok.setText(
                "OK"
        );

        ok.setAllCaps(
                false
        );

        ok.setTextColor(
                0xFFFFD700
        );

        ok.setTextSize(
                16f
        );

        ok.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        ok.setGravity(
                Gravity.CENTER
        );

        ok.setPadding(
                dp(12),
                dp(12),
                dp(12),
                dp(12)
        );

        GradientDrawable okBg =
                new GradientDrawable();

        okBg.setColor(
                0xFF0B0B0B
        );

        okBg.setCornerRadius(
                dp(10)
        );

        okBg.setStroke(
                dp(2),
                0xFFFFD700
        );

        ok.setBackground(
                okBg
        );

        LinearLayout.LayoutParams okLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        okLp.setMargins(
                0,
                dp(2),
                0,
                0
        );

        ok.setLayoutParams(
                okLp
        );

        ok.setOnClickListener(
                v -> dialog.dismiss()
        );

        box.addView(
                ok
        );

        // ========================================================
        // SHOW DIALOG
        // ========================================================
        dialog.setContentView(
                box
        );

        dialog.setCancelable(
                true
        );

        dialog.setCanceledOnTouchOutside(
                true
        );

        Window window =
                dialog.getWindow();

        if (window != null) {

            window.setBackgroundDrawable(
                    new ColorDrawable(
                            Color.TRANSPARENT
                    )
            );

            window.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );

            WindowManager.LayoutParams params =
                    window.getAttributes();

            params.dimAmount =
                    0.72f;

            window.setAttributes(
                    params
            );
        }

        dialog.show();

        // setLayout must be applied AFTER show()
        if (dialog.getWindow() != null) {

            int width =
                    (int) (
                            getResources()
                                    .getDisplayMetrics()
                                    .widthPixels
                                    * 0.92f
                    );

            dialog.getWindow().setLayout(
                    width,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );

            dialog.getWindow().setGravity(
                    Gravity.CENTER
            );
        }
    }

    // ============================================================
    // UI HELPERS
    // ============================================================
    private TextView sectionLabel(String text) {

        TextView tv =
                new TextView(this);

        tv.setText(
                text
        );

        tv.setTextColor(
                0xFFEEEEEE
        );

        tv.setTextSize(
                15f
        );

        tv.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        tv.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        tv.setPadding(
                0,
                dp(16),
                0,
                dp(7)
        );

        return tv;
    }

    private TextView smallLabel(String text) {

        TextView tv =
                new TextView(this);

        tv.setText(
                text
        );

        tv.setTextColor(
                0xFFAAAAAA
        );

        tv.setTextSize(
                12f
        );

        tv.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        tv.setPadding(
                0,
                0,
                0,
                dp(4)
        );

        return tv;
    }

    private TextView createValueText() {

        TextView tv =
                new TextView(this);

        tv.setTextColor(
                Color.WHITE
        );

        tv.setTextSize(
                15f
        );

        tv.setTypeface(
                Typeface.MONOSPACE,
                Typeface.BOLD
        );

        tv.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        return tv;
    }

    private TextView createInfoText() {

        TextView tv =
                new TextView(this);

        tv.setTextColor(
                0xFFCCCCCC
        );

        tv.setTextSize(
                14f
        );

        tv.setLineSpacing(
                0f,
                1.2f
        );

        return tv;
    }

    private LinearLayout createCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(
                0xFF080808
        );

        bg.setCornerRadius(
                dp(12)
        );

        bg.setStroke(
                dp(2),
                0xFFFFD700
        );

        card.setBackground(
                bg
        );

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        lp.setMargins(
                0,
                dp(6),
                0,
                dp(8)
        );

        card.setLayoutParams(
                lp
        );

        return card;
    }

    private View wrapInCard(View child) {

        LinearLayout card =
                createCard();

        card.addView(
                child
        );

        return card;
    }

    private Button makeActionButton(String text) {

        Button button =
                new Button(this);

        button.setText(
                text
        );

        button.setAllCaps(
                false
        );

        button.setTextColor(
                Color.WHITE
        );

        button.setTextSize(
                15f
        );

        button.setBackgroundResource(
                R.drawable.gel_btn_outline_selector
        );

        button.setPadding(
                dp(12),
                dp(14),
                dp(12),
                dp(14)
        );

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        lp.setMargins(
                0,
                dp(6),
                0,
                dp(6)
        );

        button.setLayoutParams(
                lp
        );

        return button;
    }
}
