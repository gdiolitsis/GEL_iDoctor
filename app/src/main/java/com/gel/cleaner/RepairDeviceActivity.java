// GDiolitsis Engine Lab (GEL) — Author & Developer
// RepairDeviceActivity.java
// iDoctor / GEL Professional Technician Service Session
// STEP 1 — Service Session + 2-hour Pairing Code + Pairing UI

package com.gel.cleaner;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import com.gel.cleaner.base.*;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.functions.FirebaseFunctions;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RepairDeviceActivity extends GELAutoActivityHook {

    // ============================================================
    // GEL PRO ENTITLEMENT
    // ============================================================
    private static final String GEL_PRO_PREFS =
            "GEL_PRO_ENTITLEMENT";

    private static final String GEL_PRO_ACTIVE_KEY =
            "active";

    // ============================================================
    // TEMPORARY TECHNICIAN SERVICE TEST UNLOCK
    // true only for real-world two-device testing.
    // MUST be false before production release.
    // ============================================================
    private static final boolean TEMP_TECHNICIAN_TEST_UNLOCK = true;

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

    private static final String FUNCTIONS_REGION =
            "europe-west1";

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

    private Button btnCreateSession;
    private Button btnCopyCode;
    private Button btnNewSession;
    private Button btnCancelSession;

    private boolean gr;

    private FirebaseAuth firebaseAuth;
    private FirebaseFunctions firebaseFunctions;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration sessionListener;

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFunctions = FirebaseFunctions.getInstance(FUNCTIONS_REGION);
        firebaseFirestore = FirebaseFirestore.getInstance();

        buildScreen();

        restoreExistingSession();

        UIHelpers.applyPressEffectRecursive(
                getWindow().getDecorView()
        );
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
                                ? "Δημιουργία Smart Service Session"
                                : "Create Smart Service Session"
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
        // QR AREA
        // ========================================================
        TextView qrSection =
                sectionLabel(
                        gr
                                ? "ΕΓΚΑΤΑΣΤΑΣΗ & ΣΥΝΔΕΣΗ ΜΕ QR"
                                : "INSTALL & CONNECT WITH QR"
                );

        root.addView(
                qrSection
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
                dp(18),
                dp(34),
                dp(18),
                dp(34)
        );

        GradientDrawable qrBg =
                new GradientDrawable();

        qrBg.setColor(
                0xFF080808
        );

        qrBg.setCornerRadius(
                dp(12)
        );

        qrBg.setStroke(
                dp(2),
                0xFFFFD700
        );

        txtQrPlaceholder.setBackground(
                qrBg
        );

        LinearLayout.LayoutParams qrLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        qrLp.setMargins(
                0,
                dp(6),
                0,
                dp(10)
        );

        txtQrPlaceholder.setLayoutParams(
                qrLp
        );

        root.addView(
                txtQrPlaceholder
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
                        ? "1. Σαρώστε το QR από τη συσκευή του πελάτη.\n\n"
                        + "2. Αν το GEL iDoctor δεν είναι εγκατεστημένο, θα ανοίξει η σελίδα εγκατάστασης.\n\n"
                        + "3. Μετά την εγκατάσταση πατήστε Άνοιγμα. Αν το iDoctor υπάρχει ήδη, ανοίγει απευθείας.\n\n"
                        + "4. Το Smart Service Session μεταφέρεται αυτόματα στη συσκευή του πελάτη.\n\n"
                        + "5. Ο 6ψήφιος Service Code παραμένει διαθέσιμος μόνο ως εφεδρεία."
                        :
                        "1. Scan the QR code from the customer's device.\n\n"
                        + "2. If GEL iDoctor is not installed, the installation page will open.\n\n"
                        + "3. After installation tap Open. If iDoctor is already installed, it opens directly.\n\n"
                        + "4. The Smart Service Session is transferred automatically to the customer's device.\n\n"
                        + "5. The 6-digit Service Code remains available only as a fallback."
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
                        + "Ο κωδικός σύνδεσης χρησιμοποιείται μόνο για το συγκεκριμένο Service Session."
                        :
                        "The GEL PRO entitlement remains with the technician. "
                        + "The customer's device does not receive permanent subscription access. "
                        + "The pairing code is used only for the specific Service Session."
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
    // CREATE SERVICE SESSION — REAL FIREBASE BACKEND
    // ============================================================
    private void createServiceSession() {

        if (!isGelProActive()) {
            showGelProRequiredDialog();
            return;
        }

        stopSessionListener();

        txtStatus.setText(
                gr
                        ? "● FIREBASE\nΔημιουργία ασφαλούς Service Session..."
                        : "● FIREBASE\nCreating secure Service Session..."
        );
        txtStatus.setTextColor(0xFFFFD700);

        btnCreateSession.setEnabled(false);
        btnNewSession.setEnabled(false);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

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

                                btnCreateSession.setEnabled(true);
                                btnNewSession.setEnabled(true);

                                txtStatus.setText(
                                        gr
                                                ? "● ΑΠΟΤΥΧΙΑ FIREBASE AUTHENTICATION"
                                                : "● FIREBASE AUTHENTICATION FAILED"
                                );
                                txtStatus.setTextColor(0xFFFF5555);

                                Toast.makeText(
                                        this,
                                        gr
                                                ? "Δεν ήταν δυνατή η ταυτοποίηση της συσκευής τεχνικού."
                                                : "Could not authenticate the technician device.",
                                        Toast.LENGTH_LONG
                                ).show();
                                return;
                            }

                            callCreateServiceSession();
                        }
                );
    }

    private void callCreateServiceSession() {

        firebaseFunctions
                .getHttpsCallable("createServiceSession")
                .call()
                .addOnCompleteListener(
                        this,
                        task -> {
                            btnCreateSession.setEnabled(true);
                            btnNewSession.setEnabled(true);

                            if (!task.isSuccessful() ||
                                    task.getResult() == null ||
                                    !(task.getResult().getData() instanceof java.util.Map)) {

                                String message =
                                        task.getException() != null
                                                ? task.getException().getMessage()
                                                : null;

                                txtStatus.setText(
                                        gr
                                                ? "● ΑΠΟΤΥΧΙΑ ΔΗΜΙΟΥΡΓΙΑΣ SERVICE SESSION"
                                                : "● SERVICE SESSION CREATION FAILED"
                                );
                                txtStatus.setTextColor(0xFFFF5555);

                                Toast.makeText(
                                        this,
                                        message != null
                                                ? message
                                                : (
                                                gr
                                                        ? "Το Firebase Service Session δεν δημιουργήθηκε."
                                                        : "The Firebase Service Session could not be created."
                                        ),
                                        Toast.LENGTH_LONG
                                ).show();
                                return;
                            }

                            java.util.Map<?, ?> result =
                                    (java.util.Map<?, ?>) task.getResult().getData();

                            Object sessionRaw = result.get("sessionId");
                            Object codeRaw = result.get("serviceCode");
                            Object expiryRaw = result.get("expiresAt");

                            String sessionId =
                                    sessionRaw != null
                                            ? String.valueOf(sessionRaw).trim()
                                            : "";

                            String serviceCode =
                                    codeRaw != null
                                            ? String.valueOf(codeRaw).trim()
                                            : "";

                            long pairingExpires =
                                    expiryRaw instanceof Number
                                            ? ((Number) expiryRaw).longValue()
                                            : 0L;

                            if (sessionId.isEmpty() ||
                                    serviceCode.length() != 6 ||
                                    pairingExpires <= 0L) {

                                txtStatus.setText(
                                        gr
                                                ? "● ΜΗ ΕΓΚΥΡΗ ΑΠΑΝΤΗΣΗ FIREBASE"
                                                : "● INVALID FIREBASE RESPONSE"
                                );
                                txtStatus.setTextColor(0xFFFF5555);
                                return;
                            }

                            long now = System.currentTimeMillis();

                            getSharedPreferences(SESSION_PREFS, MODE_PRIVATE)
                                    .edit()
                                    .putString(KEY_SESSION_ID, sessionId)
                                    .putString(KEY_SERVICE_CODE, serviceCode)
                                    .putLong(KEY_CREATED_AT, now)
                                    .putLong(KEY_PAIRING_EXPIRES_AT, pairingExpires)
                                    .putBoolean(KEY_FIREBASE_BACKED, true)
                                    .putBoolean(KEY_SESSION_CONNECTED, false)
                                    .apply();

                            showSession(
                                    sessionId,
                                    serviceCode,
                                    pairingExpires
                            );

                            startSessionListener(sessionId);
                            GELRemoteTargetManager.syncAvailability(this);
                        }
                );
    }

    // ============================================================
    // RESTORE SESSION
    // ============================================================
    private void restoreExistingSession() {

        SharedPreferences prefs =
                getSharedPreferences(SESSION_PREFS, MODE_PRIVATE);

        String sessionId =
                prefs.getString(KEY_SESSION_ID, null);

        String serviceCode =
                prefs.getString(KEY_SERVICE_CODE, null);

        long pairingExpires =
                prefs.getLong(KEY_PAIRING_EXPIRES_AT, 0L);

        boolean firebaseBacked =
                prefs.getBoolean(KEY_FIREBASE_BACKED, false);

        boolean connected =
                prefs.getBoolean(KEY_SESSION_CONNECTED, false);

        if (sessionId == null ||
                serviceCode == null ||
                pairingExpires <= 0L ||
                !firebaseBacked) {

            // Removes legacy local-only sessions created by older test builds.
            clearStoredSession();
            showNoSessionState();
            GELRemoteTargetManager.syncAvailability(this);
            return;
        }

        // The two-hour expiry only limits PAIRING. Once CONNECTED, the
        // Service Session remains valid until it is explicitly ended.
        if (!connected &&
                System.currentTimeMillis() >= pairingExpires) {

            clearStoredSession();
            showNoSessionState();
            GELRemoteTargetManager.syncAvailability(this);

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

        startSessionListener(sessionId);
    }

    private void startSessionListener(String sessionId) {

        stopSessionListener();

        if (sessionId == null || sessionId.trim().isEmpty()) {
            return;
        }

        sessionListener =
                firebaseFirestore
                        .collection("service_sessions")
                        .document(sessionId)
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {
                                        return;
                                    }

                                    if (snapshot == null || !snapshot.exists()) {
                                        clearStoredSession();
                                        showNoSessionState();
                                        GELRemoteTargetManager.syncAvailability(this);
                                        return;
                                    }

                                    String status = snapshot.getString("status");

                                    if ("CONNECTED".equals(status)) {

                                        getSharedPreferences(SESSION_PREFS, MODE_PRIVATE)
                                                .edit()
                                                .putBoolean(KEY_FIREBASE_BACKED, true)
                                                .putBoolean(KEY_SESSION_CONNECTED, true)
                                                .apply();

                                        txtStatus.setText(
                                                gr
                                                        ? "● ΣΥΝΔΕΘΗΚΕ ΣΥΣΚΕΥΗ ΠΕΛΑΤΗ\nΤο Remote Device Mode είναι διαθέσιμο."
                                                        : "● CUSTOMER DEVICE CONNECTED\nRemote Device Mode is available."
                                        );
                                        txtStatus.setTextColor(0xFF39FF14);

                                        txtExpiry.setText(
                                                gr
                                                        ? "Η συσκευή πελάτη συνδέθηκε επιτυχώς στο Service Session."
                                                        : "Customer device successfully connected to the Service Session."
                                        );

                                        GELRemoteTargetManager.syncAvailability(this);
                                        return;
                                    }

                                    if ("WAITING".equals(status)) {

                                        getSharedPreferences(SESSION_PREFS, MODE_PRIVATE)
                                                .edit()
                                                .putBoolean(KEY_FIREBASE_BACKED, true)
                                                .putBoolean(KEY_SESSION_CONNECTED, false)
                                                .apply();

                                        GELRemoteTargetManager.syncAvailability(this);
                                        return;
                                    }

                                    // Any terminal/unavailable state disables remote mode locally.
                                    getSharedPreferences(SESSION_PREFS, MODE_PRIVATE)
                                            .edit()
                                            .putBoolean(KEY_SESSION_CONNECTED, false)
                                            .apply();

                                    GELRemoteTargetManager.syncAvailability(this);
                                }
                        );
    }

    private void stopSessionListener() {
        if (sessionListener != null) {
            sessionListener.remove();
            sessionListener = null;
        }
    }

    // ============================================================
    // DISPLAY ACTIVE SESSION
    // ============================================================
    private void showSession(
            String sessionId,
            String serviceCode,
            long pairingExpires
    ) {

        txtStatus.setText(
                gr
                        ? "● ΕΝΕΡΓΟ SERVICE SESSION\nΑναμονή για σύνδεση συσκευής..."
                        : "● ACTIVE SERVICE SESSION\nWaiting for customer device..."
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

        txtQrPlaceholder.setText(
                gr
                        ? "QR CODE\n\n"
                        + "Το QR pairing θα ενεργοποιηθεί στο επόμενο βήμα.\n\n"
                        + "Προς το παρόν η σύνδεση προετοιμάζεται με τον 6ψήφιο Service Code."
                        :
                        "QR CODE\n\n"
                        + "QR pairing will be activated in the next step.\n\n"
                        + "For now, pairing is prepared using the 6-digit Service Code."
        );

        btnCreateSession.setVisibility(
                View.GONE
        );

        btnCopyCode.setVisibility(
                View.VISIBLE
        );

        btnNewSession.setVisibility(
                View.VISIBLE
        );

        btnCancelSession.setVisibility(
                View.VISIBLE
        );
    }

    // ============================================================
    // NO SESSION STATE
    // ============================================================
    private void showNoSessionState() {

        boolean pro =
                isGelProActive();

        txtStatus.setText(
                pro
                        ? (
                        gr
                                ? "Δεν υπάρχει ενεργό Service Session."
                                : "No active Service Session."
                )
                        : (
                        gr
                                ? "🔒 GEL PRO απαιτείται για Technician Service Sessions."
                                : "🔒 GEL PRO is required for Technician Service Sessions."
                )
        );

        txtStatus.setTextColor(
                pro
                        ? 0xFFCCCCCC
                        : 0xFFFFD700
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

        showGelConfirmDialog(
                gr ? "Ακύρωση Service Session" : "Cancel Service Session",
                gr
                        ? "Θέλετε να ακυρώσετε το ενεργό Service Session;"
                        : "Do you want to cancel the active Service Session?",
                gr ? "Όχι" : "No",
                gr ? "Ακύρωση Session" : "Cancel Session",
                () -> {
                    stopSessionListener();
                    clearStoredSession();
                    GELRemoteTargetManager.syncAvailability(this);
                    showNoSessionState();

                    Toast.makeText(
                            this,
                            gr
                                    ? "Το Service Session έκλεισε σε αυτή τη συσκευή."
                                    : "Service Session closed on this device.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );
    }

    private void showGelConfirmDialog(
            String titleText,
            String messageText,
            String negativeText,
            String positiveText,
            Runnable onPositive
    ) {
        final Dialog dialog = new Dialog(
                this,
                android.R.style.Theme_Material_NoActionBar_TranslucentDecor
        );
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(20), dp(18), dp(20), dp(16));

        GradientDrawable boxBg = new GradientDrawable();
        boxBg.setColor(0xFF0B0B0B);
        boxBg.setCornerRadius(dp(14));
        boxBg.setStroke(dp(2), 0xFFFFD700);
        box.setBackground(boxBg);

        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextColor(0xFFFFD700);
        title.setTextSize(19f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(2), 0, dp(14));
        box.addView(title);

        View divider = new View(this);
        GradientDrawable dividerBg = new GradientDrawable();
        dividerBg.setColor(0xFFFFD700);
        divider.setBackground(dividerBg);
        LinearLayout.LayoutParams dividerLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        dividerLp.setMargins(0, 0, 0, dp(16));
        divider.setLayoutParams(dividerLp);
        box.addView(divider);

        TextView message = new TextView(this);
        message.setText(messageText);
        message.setTextColor(0xFFE6E6E6);
        message.setTextSize(15f);
        message.setGravity(Gravity.CENTER);
        message.setLineSpacing(0f, 1.20f);
        message.setPadding(dp(4), 0, dp(4), dp(18));
        box.addView(message);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);

        Button negative = buildGelDialogButton(negativeText);
        Button positive = buildGelDialogButton(positiveText);

        LinearLayout.LayoutParams leftLp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        leftLp.setMargins(0, 0, dp(6), 0);
        negative.setLayoutParams(leftLp);

        LinearLayout.LayoutParams rightLp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        rightLp.setMargins(dp(6), 0, 0, 0);
        positive.setLayoutParams(rightLp);

        negative.setOnClickListener(v -> dialog.dismiss());
        positive.setOnClickListener(v -> {
            dialog.dismiss();
            if (onPositive != null) onPositive.run();
        });

        row.addView(negative);
        row.addView(positive);
        box.addView(row);

        dialog.setContentView(box);
        dialog.show();
        applyGelDialogWindow(dialog);
    }

    private Button buildGelDialogButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextColor(0xFFFFD700);
        button.setTextSize(15f);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(10), dp(11), dp(10), dp(11));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF0B0B0B);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(2), 0xFFFFD700);
        button.setBackground(bg);
        return button;
    }

    private void applyGelDialogWindow(Dialog dialog) {
        Window window = dialog.getWindow();
        if (window == null) return;

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0.72f;
        window.setAttributes(params);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92f);
        window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    protected void onDestroy() {
        stopSessionListener();
        super.onDestroy();
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

        // TEST BUILD:
        // both physical devices may act as technician.
        if (TEMP_TECHNICIAN_TEST_UNLOCK) {
            return true;
        }

        // Production entitlement path remains intact.
        try {
            return getSharedPreferences(
                    GEL_PRO_PREFS,
                    MODE_PRIVATE
            ).getBoolean(
                    GEL_PRO_ACTIVE_KEY,
                    false
            );
        } catch (Throwable ignore) {
            return false;
        }
    }

    private void showGelProRequiredDialog() {
        final Dialog dialog = new Dialog(
                this,
                android.R.style.Theme_Material_NoActionBar_TranslucentDecor
        );
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(20), dp(18), dp(20), dp(16));

        GradientDrawable boxBg = new GradientDrawable();
        boxBg.setColor(0xFF0B0B0B);
        boxBg.setCornerRadius(dp(14));
        boxBg.setStroke(dp(2), 0xFFFFD700);
        box.setBackground(boxBg);

        TextView title = new TextView(this);
        title.setText("🔒 GEL PRO — Technician Service");
        title.setTextColor(0xFFFFD700);
        title.setTextSize(19f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(2), 0, dp(14));
        box.addView(title);

        View divider = new View(this);
        GradientDrawable dividerBg = new GradientDrawable();
        dividerBg.setColor(0xFFFFD700);
        divider.setBackground(dividerBg);
        LinearLayout.LayoutParams dividerLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        dividerLp.setMargins(0, 0, 0, dp(16));
        divider.setLayoutParams(dividerLp);
        box.addView(divider);

        TextView message = new TextView(this);
        message.setText(
                gr
                        ? "Η λειτουργία «Απομακρυσμένη Διάγνωση & Υποστήριξη Συσκευής» είναι διαθέσιμη μόνο σε επαγγελματίες τεχνικούς με ενεργή συνδρομή GEL PRO.\n\n"
                          + "Η συνδρομή ενεργοποιεί τα Smart Technician Service Sessions για σύνδεση και διάγνωση συσκευών πελατών."
                        : "Remote Device Diagnostics & Support is available only to professional technicians with an active GEL PRO subscription.\n\n"
                          + "The subscription enables Smart Technician Service Sessions for connecting and diagnosing customer devices."
        );
        message.setTextColor(0xFFE6E6E6);
        message.setTextSize(15f);
        message.setGravity(Gravity.CENTER);
        message.setLineSpacing(0f, 1.20f);
        message.setPadding(dp(4), 0, dp(4), dp(18));
        box.addView(message);

        Button ok = buildGelDialogButton("OK");
        ok.setOnClickListener(v -> dialog.dismiss());
        box.addView(ok);

        dialog.setContentView(box);
        dialog.show();
        applyGelDialogWindow(dialog);
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
