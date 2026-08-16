// GDiolitsis Engine Lab (GEL) — Author & Developer
// ConnectToTechnicianActivity.java
// iDoctor Customer Device Pairing
// STEP 1 — QR Scan + Manual Service Code Validation
// NOTE: This step validates pairing data locally.
// Real technician/customer connection will be added through backend next.

package com.gel.cleaner;

import android.content.Context;
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

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class ConnectToTechnicianActivity extends GELAutoActivityHook {

    private boolean gr;

    private TextView txtStatus;
    private TextView txtSessionId;
    private TextView txtServiceCode;
    private TextView txtExpiry;

    private EditText inputCode;

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

        buildScreen();

        UIHelpers.applyPressEffectRecursive(
                getWindow().getDecorView()
        );
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
                "PAIRING TEST — LOCAL VALIDATION"
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
                        ? "Σε αυτό το δοκιμαστικό βήμα το iDoctor ελέγχει το QR και τον Service Code τοπικά. "
                        + "Δεν έχει γίνει ακόμη πραγματική σύνδεση με τη συσκευή του τεχνικού. "
                        + "Η σύνδεση μέσω server θα προστεθεί στο επόμενο στάδιο."
                        :
                        "In this test step, iDoctor validates the QR and Service Code locally. "
                        + "No real connection to the technician device has been made yet. "
                        + "Server pairing will be added in the next stage."
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
    // QR PAYLOAD VALIDATION
    // Expected:
    // gel://technician/pair?v=1&session=...&code=......&expires=...
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

        try {

            Uri uri =
                    Uri.parse(
                            payload.trim()
                    );

            String scheme =
                    uri.getScheme();

            String host =
                    uri.getHost();

            String path =
                    uri.getPath();

            if (!"gel".equalsIgnoreCase(scheme) ||
                    !"technician".equalsIgnoreCase(host) ||
                    !"/pair".equals(path)) {

                showInvalidQr(
                        gr
                                ? "Το QR δεν είναι iDoctor Technician Session."
                                : "This QR is not an iDoctor Technician Session."
                );

                return;
            }

            String version =
                    uri.getQueryParameter(
                            "v"
                    );

            String session =
                    uri.getQueryParameter(
                            "session"
                    );

            String code =
                    uri.getQueryParameter(
                            "code"
                    );

            String expiresRaw =
                    uri.getQueryParameter(
                            "expires"
                    );

            if (!"1".equals(version) ||
                    session == null ||
                    !session.startsWith("GEL-") ||
                    !isSixDigitCode(code) ||
                    expiresRaw == null) {

                showInvalidQr(
                        gr
                                ? "Το QR δεν περιέχει έγκυρα στοιχεία Service Session."
                                : "The QR does not contain valid Service Session data."
                );

                return;
            }

            long expires =
                    Long.parseLong(
                            expiresRaw
                    );

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

        } catch (Throwable t) {

            showInvalidQr(
                    gr
                            ? "Μη έγκυρο QR."
                            : "Invalid QR."
            );
        }
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

        txtStatus.setText(
                gr
                        ? "● ΕΓΚΥΡΗ ΜΟΡΦΗ SERVICE CODE\nΑπαιτείται server lookup για να βρεθεί το Session."
                        :
                        "● VALID SERVICE CODE FORMAT\nServer lookup is required to find the Session."
        );

        txtStatus.setTextColor(
                0xFFFFD700
        );

        txtSessionId.setText(
                gr
                        ? "Αναμονή server lookup"
                        : "Waiting for server lookup"
        );

        txtServiceCode.setText(
                code
        );

        txtExpiry.setText(
                gr
                        ? "Στο επόμενο στάδιο ο κωδικός θα αναζητείται στο ενεργό session του τεχνικού."
                        :
                        "In the next stage this code will be looked up against the technician's active session."
        );
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
