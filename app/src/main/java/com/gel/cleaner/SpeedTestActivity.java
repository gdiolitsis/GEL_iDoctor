// ============================================================
// SpeedTestActivity
// GEL Native Internet Speed Test
// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================

package com.gel.cleaner;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpeedTestActivity extends AppCompatActivity {

    private static final String TEST_HOST = "https://speed.cloudflare.com";
    private static final int CONNECT_TIMEOUT_MS = 12_000;
    private static final int READ_TIMEOUT_MS = 25_000;

    private static final int PING_ROUNDS = 5;
    private static final int DOWNLOAD_BYTES = 10_000_000;
    private static final int UPLOAD_BYTES = 5_000_000;

    private static final int COLOR_BG = 0xFF101010;
    private static final int COLOR_GOLD = 0xFFFFD700;
    private static final int COLOR_NEON = 0xFF00FF7F;
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_DIM = 0xFFBDBDBD;

    private final Handler ui = new Handler(Looper.getMainLooper());
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(false);

    private TextView statusText;
    private TextView pingValue;
    private TextView downloadValue;
    private TextView uploadValue;
    private TextView verdictText;
    private ProgressBar progress;
    private Button startButton;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildUi();
    }

    private void buildUi() {

        final boolean gr = AppLang.isGreek(this);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(COLOR_BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(18), dp(24), dp(18), dp(24));
        root.setBackgroundColor(COLOR_BG);

        TextView title = new TextView(this);
        title.setText(gr ? "🌐 Έλεγχος Ταχύτητας Internet" : "🌐 Internet Speed Test");
        title.setTextColor(COLOR_NEON);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(8));
        root.addView(title, matchWrap());

        TextView subtitle = new TextView(this);
        subtitle.setText(gr
                ? "Native μέτρηση Ping, Download και Upload"
                : "Native Ping, Download and Upload measurement");
        subtitle.setTextColor(COLOR_DIM);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 0, 0, dp(22));
        root.addView(subtitle, matchWrap());

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(16), dp(18), dp(16), dp(18));
        panel.setBackground(makePanelBackground());

        root.addView(panel, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        pingValue = addMetric(panel, gr ? "PING" : "PING", "-- ms");
        downloadValue = addMetric(panel, "DOWNLOAD", "-- Mbps");
        uploadValue = addMetric(panel, "UPLOAD", "-- Mbps");

        progress = new ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyleHorizontal
        );
        progress.setMax(100);
        progress.setProgress(0);
        progress.setIndeterminate(false);

        LinearLayout.LayoutParams progressLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(8)
        );
        progressLp.setMargins(0, dp(18), 0, dp(10));
        panel.addView(progress, progressLp);

        statusText = new TextView(this);
        statusText.setText(gr ? "Έτοιμο για μέτρηση." : "Ready to test.");
        statusText.setTextColor(COLOR_WHITE);
        statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, dp(8), 0, dp(8));
        panel.addView(statusText, matchWrap());

        verdictText = new TextView(this);
        verdictText.setText("");
        verdictText.setTextColor(COLOR_NEON);
        verdictText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        verdictText.setTypeface(Typeface.DEFAULT_BOLD);
        verdictText.setGravity(Gravity.CENTER);
        verdictText.setPadding(dp(8), dp(20), dp(8), dp(18));
        root.addView(verdictText, matchWrap());

        startButton = new Button(this);
        startButton.setText(gr ? "ΕΝΑΡΞΗ SPEED TEST" : "START SPEED TEST");
        startButton.setAllCaps(false);
        startButton.setTextColor(COLOR_NEON);
        startButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        startButton.setTypeface(Typeface.DEFAULT_BOLD);
        startButton.setBackgroundResource(R.drawable.gel_btn_neon_outline);
        startButton.setOnClickListener(v -> startSpeedTest());

        LinearLayout.LayoutParams startLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
        );
        startLp.setMargins(0, dp(8), 0, dp(12));
        root.addView(startButton, startLp);

        Button closeButton = new Button(this);
        closeButton.setText(gr ? "ΕΠΙΣΤΡΟΦΗ" : "BACK");
        closeButton.setAllCaps(false);
        closeButton.setTextColor(COLOR_WHITE);
        closeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        closeButton.setTypeface(Typeface.DEFAULT_BOLD);
        closeButton.setBackgroundResource(R.drawable.gel_btn_outline_selector);
        closeButton.setOnClickListener(v -> finish());

        root.addView(closeButton, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(54)
        ));

        scroll.addView(root);
        setContentView(scroll);

        UIHelpers.applyPressEffectRecursive(root);
    }

    private TextView addMetric(
            LinearLayout parent,
            String label,
            String initialValue
    ) {

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(11), 0, dp(11));

        TextView name = new TextView(this);
        name.setText(label);
        name.setTextColor(COLOR_WHITE);
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        name.setTypeface(Typeface.DEFAULT_BOLD);

        TextView value = new TextView(this);
        value.setText(initialValue);
        value.setTextColor(COLOR_NEON);
        value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        value.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        value.setGravity(Gravity.END);

        row.addView(name, new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        ));

        row.addView(value, new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        ));

        parent.addView(row, matchWrap());
        return value;
    }

    private void startSpeedTest() {

        if (!running.compareAndSet(false, true)) {
            return;
        }

        final boolean gr = AppLang.isGreek(this);

        if (!hasValidatedInternet()) {
            running.set(false);
            Toast.makeText(
                    this,
                    gr ? "Δεν υπάρχει ενεργή σύνδεση Internet."
                       : "No active Internet connection.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        cancelled.set(false);
        startButton.setEnabled(false);
        startButton.setAlpha(0.55f);

        pingValue.setText("-- ms");
        downloadValue.setText("-- Mbps");
        uploadValue.setText("-- Mbps");
        verdictText.setText("");
        progress.setProgress(0);
        statusText.setText(gr ? "Προετοιμασία μέτρησης..." : "Preparing test...");

        worker.execute(() -> {

            double pingMs = -1d;
            double downloadMbps = -1d;
            double uploadMbps = -1d;

            try {

                updateStatus(gr ? "Μέτρηση Ping..." : "Measuring ping...", 10);
                pingMs = measurePing();

                if (cancelled.get()) return;

                final double finalPing = pingMs;
                ui.post(() -> pingValue.setText(
                        finalPing >= 0d
                                ? String.format(Locale.US, "%.0f ms", finalPing)
                                : "N/A"
                ));

                updateStatus(gr ? "Μέτρηση Download..." : "Measuring download...", 35);
                downloadMbps = measureDownload();

                if (cancelled.get()) return;

                final double finalDownload = downloadMbps;
                ui.post(() -> downloadValue.setText(
                        finalDownload >= 0d
                                ? String.format(Locale.US, "%.1f Mbps", finalDownload)
                                : "N/A"
                ));

                updateStatus(gr ? "Μέτρηση Upload..." : "Measuring upload...", 70);
                uploadMbps = measureUpload();

                if (cancelled.get()) return;

                final double finalUpload = uploadMbps;
                ui.post(() -> uploadValue.setText(
                        finalUpload >= 0d
                                ? String.format(Locale.US, "%.1f Mbps", finalUpload)
                                : "N/A"
                ));

                final String verdict = buildVerdict(
                        pingMs,
                        downloadMbps,
                        uploadMbps,
                        gr
                );

                ui.post(() -> {
                    progress.setProgress(100);
                    statusText.setText(gr
                            ? "Η μέτρηση ολοκληρώθηκε."
                            : "Test completed.");
                    verdictText.setText(verdict);
                });

            } catch (Throwable t) {

                ui.post(() -> {
                    progress.setProgress(0);
                    statusText.setText(gr
                            ? "Η μέτρηση απέτυχε. Δοκίμασε ξανά."
                            : "Test failed. Please try again.");
                    verdictText.setText(gr
                            ? "Δεν ήταν δυνατή η αξιόπιστη μέτρηση."
                            : "A reliable measurement could not be completed.");
                });

            } finally {

                running.set(false);

                ui.post(() -> {
                    startButton.setEnabled(true);
                    startButton.setAlpha(1f);
                });
            }
        });
    }

    private double measurePing() throws Exception {

        long totalMs = 0L;
        int valid = 0;

        for (int i = 0; i < PING_ROUNDS; i++) {

            if (cancelled.get()) return -1d;

            HttpURLConnection connection = null;

            try {

                URL url = new URL(TEST_HOST + "/__down?bytes=1&r=" + System.nanoTime());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
                connection.setReadTimeout(READ_TIMEOUT_MS);
                connection.setUseCaches(false);
                connection.setRequestProperty("Cache-Control", "no-cache");

                long start = SystemClock.elapsedRealtime();

                int code = connection.getResponseCode();

                try (BufferedInputStream input =
                             new BufferedInputStream(connection.getInputStream())) {
                    input.read();
                }

                long elapsed = SystemClock.elapsedRealtime() - start;

                if (code >= 200 && code < 400) {
                    totalMs += elapsed;
                    valid++;
                }

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        return valid > 0
                ? (double) totalMs / (double) valid
                : -1d;
    }

    private double measureDownload() throws Exception {

        HttpURLConnection connection = null;

        try {

            URL url = new URL(
                    TEST_HOST
                            + "/__down?bytes="
                            + DOWNLOAD_BYTES
                            + "&r="
                            + System.nanoTime()
            );

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.setRequestProperty("Cache-Control", "no-cache");

            int code = connection.getResponseCode();

            if (code < 200 || code >= 400) {
                throw new IllegalStateException("Download HTTP " + code);
            }

            byte[] buffer = new byte[64 * 1024];
            long bytesRead = 0L;
            long startNs = System.nanoTime();

            try (BufferedInputStream input =
                         new BufferedInputStream(connection.getInputStream(), buffer.length)) {

                int read;

                while ((read = input.read(buffer)) != -1) {

                    if (cancelled.get()) {
                        return -1d;
                    }

                    bytesRead += read;
                }
            }

            long elapsedNs = System.nanoTime() - startNs;

            if (bytesRead <= 0L || elapsedNs <= 0L) {
                return -1d;
            }

            double seconds = elapsedNs / 1_000_000_000d;
            return (bytesRead * 8d) / seconds / 1_000_000d;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private double measureUpload() throws Exception {

        HttpURLConnection connection = null;

        try {

            URL url = new URL(TEST_HOST + "/__up?r=" + System.nanoTime());

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(UPLOAD_BYTES);
            connection.setRequestProperty(
                    "Content-Type",
                    "application/octet-stream"
            );
            connection.setRequestProperty("Cache-Control", "no-cache");

            byte[] buffer = new byte[64 * 1024];
            int remaining = UPLOAD_BYTES;
            long startNs = System.nanoTime();

            try (OutputStream output =
                         new BufferedOutputStream(connection.getOutputStream(), buffer.length)) {

                while (remaining > 0) {

                    if (cancelled.get()) {
                        return -1d;
                    }

                    int writeNow = Math.min(buffer.length, remaining);
                    output.write(buffer, 0, writeNow);
                    remaining -= writeNow;
                }

                output.flush();
            }

            int code = connection.getResponseCode();
            long elapsedNs = System.nanoTime() - startNs;

            if (code < 200 || code >= 400) {
                throw new IllegalStateException("Upload HTTP " + code);
            }

            if (elapsedNs <= 0L) {
                return -1d;
            }

            double seconds = elapsedNs / 1_000_000_000d;
            return (UPLOAD_BYTES * 8d) / seconds / 1_000_000d;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String buildVerdict(
            double ping,
            double download,
            double upload,
            boolean gr
    ) {

        if (download < 0d || upload < 0d) {
            return gr
                    ? "⚠ Μερική μέτρηση — επανάλαβε το test."
                    : "⚠ Partial result — repeat the test.";
        }

        if (download >= 100d && upload >= 20d && ping >= 0d && ping <= 35d) {
            return gr
                    ? "🏆 Εξαιρετική σύνδεση"
                    : "🏆 Excellent connection";
        }

        if (download >= 50d && upload >= 10d && (ping < 0d || ping <= 60d)) {
            return gr
                    ? "✅ Πολύ καλή σύνδεση"
                    : "✅ Very good connection";
        }

        if (download >= 20d && upload >= 5d && (ping < 0d || ping <= 100d)) {
            return gr
                    ? "✓ Καλή σύνδεση"
                    : "✓ Good connection";
        }

        if (download >= 8d && upload >= 2d) {
            return gr
                    ? "⚠ Μέτρια σύνδεση"
                    : "⚠ Moderate connection";
        }

        return gr
                ? "❗ Αργή ή ασταθής σύνδεση"
                : "❗ Slow or unstable connection";
    }

    private boolean hasValidatedInternet() {

        try {

            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

            if (cm == null) return false;

            Network network = cm.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities caps =
                    cm.getNetworkCapabilities(network);

            return caps != null
                    && caps.hasCapability(
                            NetworkCapabilities.NET_CAPABILITY_INTERNET
                    );

        } catch (Throwable ignore) {
            return false;
        }
    }

    private void updateStatus(String text, int progressValue) {
        ui.post(() -> {
            statusText.setText(text);
            progress.setProgress(progressValue);
        });
    }

    private GradientDrawable makePanelBackground() {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF050505);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(3), COLOR_GOLD);
        return bg;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    private int dp(float value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        );
    }

    @Override
    protected void onDestroy() {
        cancelled.set(true);
        worker.shutdownNow();
        super.onDestroy();
    }
}
