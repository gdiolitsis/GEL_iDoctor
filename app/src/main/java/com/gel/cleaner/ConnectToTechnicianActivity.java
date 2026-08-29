// GDiolitsis Engine Lab (GEL) — Author & Developer
// ConnectToTechnicianActivity.java
// iDoctor Customer Device Pairing
// FIREBASE TEST — QR Scan + Manual Service Code + REAL claimServiceSession
// Customer device performs Anonymous Auth and claims the technician session.

package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;
import java.util.Map;

public class ConnectToTechnicianActivity extends GELAutoActivityHook {

    private boolean gr;

    private TextView txtStatus;
    private TextView txtSessionId;
    private TextView txtServiceCode;
    private TextView txtExpiry;

    private EditText inputCode;

    // ============================================================
    // FIREBASE — REAL CUSTOMER PAIRING
    // ============================================================
    private static final String FUNCTIONS_REGION =
            "europe-west1";

    private static final String CUSTOMER_SESSION_PREFS =
            "GEL_CUSTOMER_SERVICE_SESSION";

    private static final String KEY_SESSION_ID =
            "session_id";

    private static final String KEY_SERVICE_CODE =
            "service_code";

    private static final String KEY_EXPIRES_AT =
            "expires_at";

    private static final String KEY_CONNECTED =
            "connected";

    private FirebaseAuth firebaseAuth;
    private FirebaseFunctions firebaseFunctions;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration sessionListener;

    private boolean pairingInProgress = false;

    private final ActivityResultLauncher<ScanOptions> qrLauncher =
            registerForActivityResult(
                    new ScanContract(),
                    result -> {

                        if (result == null ||
                                result.getContents() == null) {

                            Toast.makeText(
                                    ConnectToTechnicianActivity.this,
                                    gr
                                            ? "Η σάρωση ακυρώθηκε."
                                            : "Scan cancelled.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        handleQrPayload(
                                result.getContents()
                        );
                    }
            );

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(
                LocaleHelper.apply(base)
        );
    }

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

        firebaseFirestore =
                FirebaseFirestore.getInstance();

        buildScreen();

        restoreStoredCustomerSession();

        UIHelpers.applyPressEffectRecursive(
                getWindow().getDecorView()
        );

        boolean handledSmartLink =
                handleIncomingSmartServiceIntent(
                        getIntent()
                );

        if (!handledSmartLink) {
            GELInstallReferrerManager.checkOnce(
                    this,
                    payload -> runOnUiThread(
                            () -> handleQrPayload(payload)
                    )
            );
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingSmartServiceIntent(intent);
    }

    // ============================================================
    // RESTORE CUSTOMER SESSION AFTER APP RESTART / UPDATE
    // ============================================================
    private void restoreStoredCustomerSession() {

        SharedPreferences prefs =
                getSharedPreferences(
                        CUSTOMER_SESSION_PREFS,
                        MODE_PRIVATE
                );

        boolean connected =
                prefs.getBoolean(
                        KEY_CONNECTED,
                        false
                );

        String sessionId =
                prefs.getString(
                        KEY_SESSION_ID,
                        null
                );

        if (!connected ||
                sessionId == null ||
                sessionId.trim().isEmpty()) {
            return;
        }

        String serviceCode =
                prefs.getString(
                        KEY_SERVICE_CODE,
                        "------"
                );

        long expiresAt =
                prefs.getLong(
                        KEY_EXPIRES_AT,
                        0L
                );

        showConnectedState(
                sessionId.trim(),
                serviceCode != null
                        ? serviceCode
                        : "------",
                expiresAt
        );

        GELRemoteCommandService.ensureRunning(
                this
        );

        attachConnectedSessionListener(
                sessionId.trim()
        );
    }

    private boolean handleIncomingSmartServiceIntent(Intent intent) {

        if (intent == null || intent.getData() == null) {
            return false;
        }

        String action = intent.getAction();

        if (action != null &&
                !Intent.ACTION_VIEW.equals(action)) {
            return false;
        }

        String payload = intent.getData().toString();

        if (GELSmartServiceLink.parse(payload) == null) {
            return false;
        }

        handleQrPayload(payload);
        return true;
    }

    // ============================================================
    // SCREEN
    // ============================================================
    private void buildScreen() {

        ScrollView scroll =
                new ScrollView(this);

        scroll.setFillViewport(
                true
        );

        scroll.setBackgroundColor(
                0xFF101010
        );

        LinearLayout root =
                new LinearLayout(this);

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
        TextView title =
                new TextView(this);

        title.setText(
                gr
                        ? "Σύνδεση με Τεχνικό"
                        : "Connect to Technician"
        );

        title.setTextColor(
                Color.WHITE
        );

        title.setTextSize(
                23f
        );

        title.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        title.setGravity(
                Gravity.CENTER
        );

        title.setPadding(
                0,
                0,
                0,
                dp(6)
        );

        root.addView(
                title
        );

        TextView subtitle =
                new TextView(this);

        subtitle.setText(
                gr
                        ? "Συνδέστε προσωρινά αυτή τη συσκευή με ενεργό iDoctor Service Session."
                        : "Temporarily connect this device to an active iDoctor Service Session."
        );

        subtitle.setTextColor(
                0xFFCCCCCC
        );

        subtitle.setTextSize(
                14f
        );

        subtitle.setGravity(
                Gravity.CENTER
        );

        subtitle.setPadding(
                0,
                0,
                0,
                dp(18)
        );

        root.addView(
                subtitle
        );

        // ========================================================
        // TEST STATUS
        // ========================================================
        TextView test =
                new TextView(this);

        test.setText(
                "FIREBASE TEST — REAL PAIRING"
        );

        test.setTextColor(
                0xFF39FF14
        );

        test.setTextSize(
                13f
        );

        test.setTypeface(
                Typeface.DEFAULT_BOLD
        );

        test.setGravity(
                Gravity.CENTER
        );

        test.setPadding(
                0,
                0,
                0,
                dp(12)
        );

        root.addView(
                test
        );

        // ========================================================
        // SCAN QR
        // ========================================================
        root.addView(
                sectionLabel(
                        "QR PAIRING"
                )
        );

        TextView qrInfo =
                createInfoText();

        qrInfo.setGravity(
                Gravity.CENTER
        );

        qrInfo.setText(
                gr
                        ? "Σαρώστε το QR που εμφανίζεται στη συσκευή του τεχνικού."
                        : "Scan the QR displayed on the technician's device."
        );

        root.addView(
                wrapInCard(qrInfo)
        );

        Button scan =
                makeActionButton(
                        gr
                                ? "Σάρωση QR Τεχνικού"
                                : "Scan Technician QR"
                );

        scan.setOnClickListener(
                v -> startQrScanner()
        );

        root.addView(
                scan
        );

        // ========================================================
        // MANUAL CODE
        // ========================================================
        root.addView(
                sectionLabel(
                        gr
                                ? "ΧΕΙΡΟΚΙΝΗΤΟΣ ΚΩΔΙΚΟΣ"
                                : "MANUAL SERVICE CODE"
                )
        );

        LinearLayout codeCard =
                createCard();

        TextView codeHelp =
                createInfoText();

        codeHelp.setGravity(
                Gravity.CENTER
        );

        codeHelp.setText(
                gr
                        ? "Εναλλακτικά, εισαγάγετε τον 6ψήφιο Service Code."
                        : "Alternatively, enter the 6-digit Service Code."
        );

        codeHelp.setPadding(
                0,
                0,
                0,
                dp(12)
        );

        codeCard.addView(
                codeHelp
        );

        inputCode =
                new EditText(this);

        inputCode.setHint(
                "000000"
        );

        inputCode.setTextColor(
                Color.WHITE
        );

        inputCode.setHintTextColor(
                0xFF666666
        );

        inputCode.setTextSize(
                26f
        );

        inputCode.setGravity(
                Gravity.CENTER
        );

        inputCode.setSingleLine(
                true
        );

        inputCode.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        inputCode.setFilters(
                new InputFilter[]{
                        new InputFilter.LengthFilter(6)
                }
        );

        GradientDrawable inputBg =
                new GradientDrawable();

        inputBg.setColor(
                0xFF080808
        );

        inputBg.setCornerRadius(
                dp(10)
        );

        inputBg.setStroke(
                dp(2),
                0xFFFFD700
        );

        inputCode.setBackground(
                inputBg
        );

        inputCode.setPadding(
                dp(12),
                dp(12),
                dp(12),
                dp(12)
        );

        codeCard.addView(
                inputCode
        );

        root.addView(
                codeCard
        );

        Button useCode =
                makeActionButton(
                        gr
                                ? "Σύνδεση με Service Code"
                                : "Connect with Service Code"
                );

        useCode.setOnClickListener(
                v -> validateManualCode()
        );

        root.addView(
                useCode
        );

        // ========================================================
        // PAIRING RESULT
        // ========================================================
        root.addView(
                sectionLabel(
                        gr
                                ? "ΚΑΤΑΣΤΑΣΗ ΣΥΝΔΕΣΗΣ"
                                : "PAIRING STATUS"
                )
        );

        LinearLayout resultCard =
                createCard();

        txtStatus =
                createInfoText();

        txtStatus.setGravity(
                Gravity.CENTER
        );

        txtStatus.setText(
                gr
                        ? "Δεν έχει επιλεγεί Service Session."
                        : "No Service Session selected."
        );

        resultCard.addView(
                txtStatus
        );

        TextView sessionLabel =
                smallLabel(
                        "Service Session ID"
                );

        sessionLabel.setPadding(
                0,
                dp(14),
                0,
                dp(4)
        );

        resultCard.addView(
                sessionLabel
        );

        txtSessionId =
                createValueText();

        txtSessionId.setText(
                "—"
        );

        resultCard.addView(
                txtSessionId
        );

        TextView codeLabel =
                smallLabel(
                        gr
                                ? "Service Code"
                                : "Service Code"
                );

        codeLabel.setPadding(
                0,
                dp(12),
                0,
                dp(4)
        );

        resultCard.addView(
                codeLabel
        );

        txtServiceCode =
                createValueText();

        txtServiceCode.setTextSize(
                24f
        );

        txtServiceCode.setTextColor(
                0xFF39FF14
        );

        txtServiceCode.setText(
                "------"
        );

        resultCard.addView(
                txtServiceCode
        );

        txtExpiry =
                createInfoText();

        txtExpiry.setGravity(
                Gravity.CENTER
        );

        txtExpiry.setPadding(
                0,
                dp(10),
                0,
                0
        );

        resultCard.addView(
                txtExpiry
        );

        root.addView(
                resultCard
        );

        // ========================================================
        // SECURITY NOTE
        // ========================================================
        root.addView(
                sectionLabel(
                        gr
                                ? "ΑΣΦΑΛΕΙΑ"
                                : "SECURITY"
                )
        );

        TextView security =
                createInfoText();

        security.setText(
                gr
                        ? "Η σύνδεση γίνεται πλέον μέσω Firebase backend. "
                        + "Το iDoctor δημιουργεί προσωρινή ανώνυμη ταυτότητα στη συσκευή πελάτη και "
                        + "η Cloud Function ελέγχει τον Service Code, τη λήξη και τη διαθεσιμότητα του Session."
                        :
                        "Pairing now uses the Firebase backend. "
                        + "iDoctor creates a temporary anonymous identity for the customer device and "
                        + "the Cloud Function validates the Service Code, expiry and Session availability."
        );

        root.addView(
                wrapInCard(security)
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
    }

    // ============================================================
    // QR SCANNER
    // ============================================================
    private void startQrScanner() {

        ScanOptions options =
                new ScanOptions();

        options.setPrompt(
                gr
                        ? "Σαρώστε το QR του τεχνικού"
                        : "Scan technician QR"
        );

        options.setBeepEnabled(
                false
        );

        options.setOrientationLocked(
                false
        );

        options.setBarcodeImageEnabled(
                false
        );

        qrLauncher.launch(
                options
        );
    }

    // ============================================================
    // QR / SMART SERVICE LINK VALIDATION
    // Accepted formats:
    // 1) https://gel-idoctor.web.app/connect?...
    // 2) gel://technician/pair?...   (legacy / browser fallback)
    // ============================================================
    private void handleQrPayload(String payload) {

        if (payload == null ||
                payload.trim().isEmpty()) {

            showInvalidQr(
                    gr
                            ? "Το QR είναι κενό."
                            : "The QR is empty."
            );

            return;
        }

        GELSmartServiceLink.ParsedLink parsed =
                GELSmartServiceLink.parse(payload);

        if (parsed == null) {

            showInvalidQr(
                    gr
                            ? "Το QR δεν είναι έγκυρο iDoctor Service Session."
                            : "This QR is not a valid iDoctor Service Session."
            );

            return;
        }

        String session = parsed.sessionId;
        String code = parsed.serviceCode;
        long expires = parsed.expiresAt;

        if (System.currentTimeMillis() >= expires) {

            txtStatus.setText(
                    gr
                            ? "● Ο ΚΩΔΙΚΟΣ ΣΥΝΔΕΣΗΣ ΕΧΕΙ ΛΗΞΕΙ"
                            : "● PAIRING CODE EXPIRED"
            );

            txtStatus.setTextColor(
                    0xFFFF5555
            );

            txtSessionId.setText(
                    session
            );

            txtServiceCode.setText(
                    code
            );

            txtExpiry.setText(
                    gr
                            ? "Ζητήστε από τον τεχνικό να δημιουργήσει νέο κωδικό."
                            : "Ask the technician to generate a new code."
            );

            return;
        }

        showValidQr(
                session,
                code,
                expires
        );

        claimServiceSession(
                session,
                code,
                expires
        );
    }

    private void showValidQr(
            String session,
            String code,
            long expires
    ) {

        txtStatus.setText(
                gr
                        ? "● ΕΓΚΥΡΟ TECHNICIAN SESSION\nΈτοιμο για server pairing."
                        : "● VALID TECHNICIAN SESSION\nReady for server pairing."
        );

        txtStatus.setTextColor(
                0xFF39FF14
        );

        txtSessionId.setText(
                session
        );

        txtServiceCode.setText(
                code
        );

        long remainingMs =
                Math.max(
                        0L,
                        expires - System.currentTimeMillis()
                );

        long remainingMinutes =
                remainingMs / 60000L;

        txtExpiry.setText(
                gr
                        ? "Ο κωδικός παραμένει έγκυρος για περίπου "
                        + remainingMinutes
                        + " λεπτά."
                        :
                        "Pairing code remains valid for approximately "
                        + remainingMinutes
                        + " minutes."
        );
    }

    private void showInvalidQr(String reason) {

        txtStatus.setText(
                gr
                        ? "● ΜΗ ΕΓΚΥΡΟ QR\n" + reason
                        : "● INVALID QR\n" + reason
        );

        txtStatus.setTextColor(
                0xFFFF5555
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
    }

    // ============================================================
    // MANUAL SERVICE CODE
    // ============================================================
    private void validateManualCode() {

        String code =
                inputCode
                        .getText()
                        .toString()
                        .trim();

        if (!isSixDigitCode(code)) {

            inputCode.setError(
                    gr
                            ? "Απαιτούνται 6 ψηφία."
                            : "6 digits required."
            );

            return;
        }

        claimServiceSession(
                null,
                code,
                0L
        );
    }

    // ============================================================
    // REAL FIREBASE CLAIM
    // ============================================================
    private void claimServiceSession(
            @Nullable String sessionId,
            String serviceCode,
            long qrExpiresAt
    ) {

        if (pairingInProgress) {

            return;
        }

        if (!isSixDigitCode(serviceCode)) {

            showPairingError(
                    gr
                            ? "Μη έγκυρος Service Code."
                            : "Invalid Service Code."
            );

            return;
        }

        if (sessionId != null &&
                qrExpiresAt > 0L &&
                System.currentTimeMillis() >= qrExpiresAt) {

            showPairingError(
                    gr
                            ? "Ο QR κωδικός έχει λήξει."
                            : "The QR pairing code has expired."
            );

            return;
        }

        pairingInProgress =
                true;

        txtStatus.setText(
                gr
                        ? "● FIREBASE\nΈλεγχος ταυτότητας συσκευής πελάτη..."
                        : "● FIREBASE\nAuthenticating customer device..."
        );

        txtStatus.setTextColor(
                0xFFFFD700
        );

        txtSessionId.setText(
                sessionId != null
                        ? sessionId
                        : (
                        gr
                                ? "Αναζήτηση μέσω Service Code..."
                                : "Looking up Service Code..."
                )
        );

        txtServiceCode.setText(
                serviceCode
        );

        txtExpiry.setText(
                gr
                        ? "Γίνεται ασφαλής σύνδεση με το Firebase backend..."
                        : "Securely connecting to the Firebase backend..."
        );

        FirebaseUser currentUser =
                firebaseAuth.getCurrentUser();

        if (currentUser != null) {

            callClaimServiceSession(
                    sessionId,
                    serviceCode
            );

            return;
        }

        firebaseAuth
                .signInAnonymously()
                .addOnCompleteListener(
                        this,
                        task -> {

                            if (!task.isSuccessful() ||
                                    firebaseAuth.getCurrentUser() == null) {

                                pairingInProgress =
                                        false;

                                showPairingError(
                                        gr
                                                ? "Αποτυχία Firebase Authentication."
                                                : "Firebase Authentication failed."
                                );

                                return;
                            }

                            callClaimServiceSession(
                                    sessionId,
                                    serviceCode
                            );
                        }
                );
    }

    private void callClaimServiceSession(
            @Nullable String sessionId,
            String serviceCode
    ) {

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "code",
                serviceCode
        );

        if (sessionId != null &&
                !sessionId.trim().isEmpty()) {

            data.put(
                    "sessionId",
                    sessionId
            );
        }

        txtStatus.setText(
                gr
                        ? "● FIREBASE\nΑναζήτηση και κατοχύρωση Service Session..."
                        : "● FIREBASE\nLooking up and claiming Service Session..."
        );

        txtStatus.setTextColor(
                0xFFFFD700
        );

        firebaseFunctions
                .getHttpsCallable(
                        "claimServiceSession"
                )
                .call(
                        data
                )
                .addOnCompleteListener(
                        this,
                        task -> {

                            pairingInProgress =
                                    false;

                            if (!task.isSuccessful() ||
                                    task.getResult() == null) {

                                String message =
                                        task.getException() != null
                                                ? task.getException().getMessage()
                                                : null;

                                if (message != null &&
                                        message.contains(
                                                "Technician and customer must use different app identities"
                                        )) {

                                    message =
                                            gr
                                                    ? "Για πραγματική δοκιμή χρειάζεται δεύτερη συσκευή. "
                                                    + "Το ίδιο Firebase identity δεν μπορεί να είναι ταυτόχρονα τεχνικός και πελάτης."
                                                    :
                                                    "A second device is required for a real test. "
                                                    + "The same Firebase identity cannot be both technician and customer.";
                                }

                                showPairingError(
                                        message != null
                                                ? message
                                                : (
                                                gr
                                                        ? "Η σύνδεση με τον τεχνικό απέτυχε."
                                                        : "Could not connect to the technician."
                                        )
                                );

                                return;
                            }

                            Object raw =
                                    task.getResult()
                                            .getData();

                            if (!(raw instanceof Map)) {

                                showPairingError(
                                        gr
                                                ? "Μη έγκυρη απάντηση από τον server."
                                                : "Invalid response from server."
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

                            Object statusRaw =
                                    result.get(
                                            "status"
                                    );

                            if (!(sessionRaw instanceof String) ||
                                    !(codeRaw instanceof String) ||
                                    !(expiryRaw instanceof Number) ||
                                    !(statusRaw instanceof String)) {

                                showPairingError(
                                        gr
                                                ? "Ελλιπής απάντηση από το Firebase backend."
                                                : "Incomplete response from Firebase backend."
                                );

                                return;
                            }

                            String connectedSessionId =
                                    (String) sessionRaw;

                            String connectedServiceCode =
                                    (String) codeRaw;

                            long expiresAt =
                                    ((Number) expiryRaw)
                                            .longValue();

                            String status =
                                    (String) statusRaw;

                            if (!"CONNECTED".equals(status)) {

                                showPairingError(
                                        gr
                                                ? "Το Session δεν επέστρεψε κατάσταση CONNECTED."
                                                : "Session did not return CONNECTED status."
                                );

                                return;
                            }

                            saveConnectedCustomerSession(
                                    connectedSessionId,
                                    connectedServiceCode,
                                    expiresAt
                            );

                            showConnectedState(
                                    connectedSessionId,
                                    connectedServiceCode,
                                    expiresAt
                            );

                            // Start the visible customer-side remote command
                            // transport immediately after successful pairing.
                            GELRemoteCommandService.ensureRunning(
                                    this
                            );

                            attachConnectedSessionListener(
                                    connectedSessionId
                            );
                        }
                );
    }

    private void saveConnectedCustomerSession(
            String sessionId,
            String serviceCode,
            long expiresAt
    ) {

        getSharedPreferences(
                CUSTOMER_SESSION_PREFS,
                MODE_PRIVATE
        )
                .edit()
                .putString(
                        KEY_SESSION_ID,
                        sessionId
                )
                .putString(
                        KEY_SERVICE_CODE,
                        serviceCode
                )
                .putLong(
                        KEY_EXPIRES_AT,
                        expiresAt
                )
                .putBoolean(
                        KEY_CONNECTED,
                        true
                )
                .apply();
    }

    private void showConnectedState(
            String sessionId,
            String serviceCode,
            long expiresAt
    ) {

        txtStatus.setText(
                gr
                        ? "● ΣΥΝΔΕΘΗΚΕ ΜΕ ΤΕΧΝΙΚΟ\nΤο Firebase Service Session είναι ενεργό."
                        : "● CONNECTED TO TECHNICIAN\nFirebase Service Session is active."
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

        long remainingMs =
                Math.max(
                        0L,
                        expiresAt - System.currentTimeMillis()
                );

        long remainingMinutes =
                remainingMs / 60000L;

        txtExpiry.setText(
                gr
                        ? "Η συσκευή συνδέθηκε επιτυχώς. "
                        + "Ο pairing code παραμένει έγκυρος για περίπου "
                        + remainingMinutes
                        + " λεπτά, αλλά το ενεργό Service Session δεν διακόπτεται από τη λήξη του code."
                        :
                        "Device connected successfully. "
                        + "The pairing code remains valid for approximately "
                        + remainingMinutes
                        + " minutes, but the active Service Session is not terminated by code expiry."
        );

        Toast.makeText(
                this,
                gr
                        ? "Σύνδεση με τεχνικό επιτυχής."
                        : "Connected to technician successfully.",
                Toast.LENGTH_LONG
        ).show();
    }

    // ============================================================
    // CUSTOMER — ACTIVE SESSION LIFECYCLE LISTENER
    // ============================================================
    private void attachConnectedSessionListener(
            String sessionId
    ) {

        removeSessionListener();

        if (firebaseFirestore == null ||
                sessionId == null ||
                sessionId.trim().isEmpty()) {
            return;
        }

        sessionListener =
                firebaseFirestore
                        .collection(
                                "service_sessions"
                        )
                        .document(
                                sessionId.trim()
                        )
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {
                                        return;
                                    }

                                    if (snapshot == null ||
                                            !snapshot.exists()) {

                                        handleCustomerSessionEnded(
                                                "ENDED"
                                        );
                                        return;
                                    }

                                    String status =
                                            snapshot.getString(
                                                    "status"
                                            );

                                    if ("CONNECTED".equals(status)) {
                                        return;
                                    }

                                    handleCustomerSessionEnded(
                                            status != null
                                                    ? status
                                                    : "ENDED"
                                    );
                                }
                        );
    }

    private void handleCustomerSessionEnded(
            String status
    ) {

        removeSessionListener();

        getSharedPreferences(
                CUSTOMER_SESSION_PREFS,
                MODE_PRIVATE
        )
                .edit()
                .clear()
                .apply();

        GELRemoteCommandService.stopRemoteService(
                this
        );

        try {
            GELRemoteDiagnosticsSync.stop(
                    getApplicationContext()
            );
        } catch (Throwable ignore) {}

        if (txtStatus != null) {
            txtStatus.setText(
                    gr
                            ? (
                            "CANCELLED".equals(status)
                                    ? "● ΤΟ SERVICE SESSION ΑΚΥΡΩΘΗΚΕ ΑΠΟ ΤΟΝ ΤΕΧΝΙΚΟ"
                                    : "● ΤΟ SERVICE SESSION ΤΕΡΜΑΤΙΣΤΗΚΕ"
                    )
                            : (
                            "CANCELLED".equals(status)
                                    ? "● SERVICE SESSION CANCELLED BY TECHNICIAN"
                                    : "● SERVICE SESSION ENDED"
                    )
            );
            txtStatus.setTextColor(
                    0xFFFFD700
            );
        }

        if (txtSessionId != null) {
            txtSessionId.setText(
                    "—"
            );
        }

        if (txtServiceCode != null) {
            txtServiceCode.setText(
                    "------"
            );
        }

        if (txtExpiry != null) {
            txtExpiry.setText(
                    gr
                            ? "Η απομακρυσμένη σύνδεση έκλεισε με ασφάλεια."
                            : "The remote connection was closed safely."
            );
        }

        Toast.makeText(
                this,
                gr
                        ? "Το Service Session τερματίστηκε."
                        : "Service Session ended.",
                Toast.LENGTH_LONG
        ).show();
    }

    private void removeSessionListener() {

        if (sessionListener != null) {
            sessionListener.remove();
            sessionListener =
                    null;
        }
    }

    @Override
    protected void onDestroy() {

        removeSessionListener();

        super.onDestroy();
    }

    private void showPairingError(String message) {

        txtStatus.setText(
                gr
                        ? "● ΑΠΟΤΥΧΙΑ ΣΥΝΔΕΣΗΣ\n" + message
                        : "● PAIRING FAILED\n" + message
        );

        txtStatus.setTextColor(
                0xFFFF5555
        );

        txtExpiry.setText(
                gr
                        ? "Ελέγξτε τον Service Code ή ζητήστε νέο Session από τον τεχνικό."
                        : "Check the Service Code or ask the technician for a new Session."
        );

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    private boolean isSixDigitCode(String code) {

        return code != null &&
                code.matches("\\d{6}");
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
                Gravity.CENTER
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
                Gravity.CENTER
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
                Gravity.CENTER
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
                1.20f
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
