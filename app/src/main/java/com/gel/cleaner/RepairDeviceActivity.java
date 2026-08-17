// GDiolitsis Engine Lab (GEL) — Author & Developer
// RepairDeviceActivity.java
// iDoctor / GEL Professional Technician Service Session
// STEP 1 — Service Session + 2-hour Pairing Code + Pairing UI

package com.gel.cleaner;

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
import androidx.appcompat.app.AlertDialog;

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
                "Repair a Device"
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
        // QR AREA
        // ========================================================
        TextView qrSection =
                sectionLabel(
                        "QR PAIRING"
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
    // CREATE SERVICE SESSION
    // ============================================================
    private void createServiceSession() {

        // Technician feature — GEL PRO required.
        if (!isGelProActive()) {

            showGelProRequiredDialog();

            return;
        }

        long now =
                System.currentTimeMillis();

        long pairingExpires =
                now + PAIRING_CODE_DURATION_MS;

        String serviceCode =
                generateServiceCode();

        String sessionId =
                generateSessionId();

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
                .apply();

        showSession(
                sessionId,
                serviceCode,
                pairingExpires
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

        if (sessionId == null ||
                serviceCode == null ||
                pairingExpires <= 0L) {

            showNoSessionState();

            return;
        }

        if (System.currentTimeMillis() >= pairingExpires) {

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

        try {

            return getSharedPreferences(
                    GEL_PRO_PREFS,
                    MODE_PRIVATE
            )
                    .getBoolean(
                            GEL_PRO_ACTIVE_KEY,
                            false
                    );

        } catch (Throwable ignore) {

            return false;
        }
    }

    private void showGelProRequiredDialog() {

        new AlertDialog.Builder(this)
                .setTitle(
                        "GEL PRO — Technician Service"
                )
                .setMessage(
                        gr
                                ? "Η λειτουργία Repair a Device προορίζεται για επαγγελματίες τεχνικούς και απαιτεί ενεργή συνδρομή GEL PRO."
                                :
                                "Repair a Device is a professional technician feature and requires an active GEL PRO subscription."
                )
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
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
