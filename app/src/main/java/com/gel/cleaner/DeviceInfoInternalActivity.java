// GDiolitsis Engine Lab (GEL) — Author & Developer
// DeviceInfoInternalActivity.java — GEL INTERNAL PRO v10.0
// Full Engine-Based Edition + Soft Expand v3.0 + Neon Values + Root Fallback + Root-Extended Internals + Stealth Masking
// NOTE: Δουλεύω ΠΑΝΩ στο τελευταίο αρχείο σου — χωρίς αλλαγές σε UI / XML.

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Locale;

public class DeviceInfoInternalActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private static final String NEON_GREEN = "#39FF14";

    private boolean isRooted = false;

    private iDoctorEngine engine;

    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager foldUI;

    private TextView[] allContents;
    private TextView[] allIcons;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        engine = iDoctorEngine.get(this);
        isRooted = engine.isDeviceRooted();

        setContentView(R.layout.activity_device_info_internal);

        UIHelpers.applyPressEffectRecursive(getWindow().getDecorView());

        foldUI = new GELFoldableUIManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        TextView title = findViewById(R.id.txtTitleDevice);
        if (title != null) {
            title.setText(getString(R.string.phone_info_internal));
        }

        // CONTENT
        TextView txtSystemContent       = findViewById(R.id.txtSystemContent);
        TextView txtAndroidContent      = findViewById(R.id.txtAndroidContent);
        TextView txtCpuContent          = findViewById(R.id.txtCpuContent);
        TextView txtGpuContent          = findViewById(R.id.txtGpuContent);
        TextView txtThermalContent      = findViewById(R.id.txtThermalContent);
        TextView txtVulkanContent       = findViewById(R.id.txtVulkanContent);
        TextView txtRamContent          = findViewById(R.id.txtRamContent);
        TextView txtStorageContent      = findViewById(R.id.txtStorageContent);
        TextView txtConnectivityContent = findViewById(R.id.txtConnectivityContent);

        // ICONS
        TextView iconSystem       = findViewById(R.id.iconSystemToggle);
        TextView iconAndroid      = findViewById(R.id.iconAndroidToggle);
        TextView iconCpu          = findViewById(R.id.iconCpuToggle);
        TextView iconGpu          = findViewById(R.id.iconGpuToggle);
        TextView iconThermal      = findViewById(R.id.iconThermalToggle);
        TextView iconVulkan       = findViewById(R.id.iconVulkanToggle);
        TextView iconRam          = findViewById(R.id.iconRamToggle);
        TextView iconStorage      = findViewById(R.id.iconStorageToggle);
        TextView iconConnectivity = findViewById(R.id.iconConnectivityToggle);

        allContents = new TextView[]{
                txtSystemContent, txtAndroidContent, txtCpuContent, txtGpuContent,
                txtThermalContent, txtVulkanContent, txtRamContent,
                txtStorageContent, txtConnectivityContent
        };

        allIcons = new TextView[]{
                iconSystem, iconAndroid, iconCpu, iconGpu, iconThermal,
                iconVulkan, iconRam, iconStorage, iconConnectivity
        };

        // CONTENT BUILD
        if (txtSystemContent != null) {
            setNeonSectionText(txtSystemContent, buildSystemInfo());
        }
        if (txtAndroidContent != null) {
            setNeonSectionText(txtAndroidContent, buildAndroidInfo());
        }
        if (txtCpuContent != null) {
            setNeonSectionText(txtCpuContent, buildCpuInfo());
        }
        if (txtGpuContent != null) {
            setNeonSectionText(txtGpuContent, buildGpuInfo());
        }
        if (txtThermalContent != null) {
            setNeonSectionText(txtThermalContent, buildThermalInternalReport());
        }
        if (txtVulkanContent != null) {
            setNeonSectionText(txtVulkanContent, buildVulkanInfo());
        }
        if (txtRamContent != null) {
            setNeonSectionText(txtRamContent, buildRamInfo());
        }
        if (txtStorageContent != null) {
            setNeonSectionText(txtStorageContent, buildStorageInfo());
        }
        if (txtConnectivityContent != null) {
            setNeonSectionText(txtConnectivityContent, buildConnectivityInfo());
        }

        // EXPANDERS
        setupSection(findViewById(R.id.headerSystem), txtSystemContent, iconSystem);
        setupSection(findViewById(R.id.headerAndroid), txtAndroidContent, iconAndroid);
        setupSection(findViewById(R.id.headerCpu), txtCpuContent, iconCpu);
        setupSection(findViewById(R.id.headerGpu), txtGpuContent, iconGpu);
        setupSection(findViewById(R.id.headerThermal), txtThermalContent, iconThermal);
        setupSection(findViewById(R.id.headerVulkan), txtVulkanContent, iconVulkan);
        setupSection(findViewById(R.id.headerRam), txtRamContent, iconRam);
        setupSection(findViewById(R.id.headerStorage), txtStorageContent, iconStorage);
        setupSection(findViewById(R.id.headerConnectivity), txtConnectivityContent, iconConnectivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (foldDetector != null) foldDetector.start();
    }

    @Override
    protected void onPause() {
        if (foldDetector != null) foldDetector.stop();
        super.onPause();
    }

    @Override
    public void onPostureChanged(@NonNull Posture posture) {}

    @Override
    public void onScreenChanged(boolean isInner) {
        if (foldUI != null) foldUI.applyUI(isInner);
    }

    // ============================================================
    // EXPANDER LOGIC WITH ANIMATION
    // ============================================================

    private void setupSection(View header, final TextView content, final TextView icon) {
        if (header == null || content == null || icon == null) return;
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView targetContent, TextView targetIcon) {

        for (int i = 0; i < allContents.length; i++) {
            TextView c = allContents[i];
            TextView ic = allIcons[i];

            if (c == null || ic == null) continue;

            if (c != targetContent && c.getVisibility() == View.VISIBLE) {
                animateCollapse(c);
                ic.setText("＋");
            }
        }

        if (targetContent.getVisibility() == View.VISIBLE) {
            animateCollapse(targetContent);
            targetIcon.setText("＋");
        } else {
            animateExpand(targetContent);
            targetIcon.setText("−");
        }
    }

    private void animateExpand(final View v) {
        v.post(() -> {
            v.measure(
                    View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );

            final int target = v.getMeasuredHeight();

            v.getLayoutParams().height = 0;
            v.setVisibility(View.VISIBLE);
            v.setAlpha(0f);

            v.animate()
                    .alpha(1f)
                    .setDuration(160)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        v.getLayoutParams().height = target;
                        v.requestLayout();
                    })
                    .start();
        });
    }

    private void animateCollapse(final View v) {
        if (v.getVisibility() != View.VISIBLE) return;

        final int initial = v.getHeight();
        v.setAlpha(1f);

        v.animate()
                .alpha(0f)
                .setDuration(120)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    v.setVisibility(View.GONE);
                    v.getLayoutParams().height = initial;
                    v.setAlpha(1f);
                    v.requestLayout();
                })
                .start();
    }

    // ============================================================
    // NEON VALUE COLOR ENGINE
    // ============================================================

    private void setNeonSectionText(TextView tv, String text) {
        if (tv == null) return;
        if (text == null) text = "";
        tv.setText(applyNeonToValues(text));
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
                                    new ForegroundColorSpan(Color.parseColor(NEON_GREEN)),
                                    valueStart,
                                    valueEnd,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                        }
                        previousLabelOnly = false;
                    }
                } else if (previousLabelOnly) {
                    int valueStart = offset;
                    int valueEnd = offset + len;

                    ssb.setSpan(
                            new ForegroundColorSpan(Color.parseColor(NEON_GREEN)),
                            valueStart,
                            valueEnd,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );

                    previousLabelOnly = false;
                } else {
                    previousLabelOnly = false;
                }
            } else {
                previousLabelOnly = false;
            }

            offset += len + 1;
        }

        return ssb;
    }

    // ============================================================
    // CONNECTIVITY INFO — FULL ENGINE-BASED
    // ============================================================

    private String buildConnectivityInfo() {

        iDoctorEngine.ConnectivitySnapshot c =
                engine.readConnectivitySnapshot();

        iDoctorEngine.TelephonySnapshot t =
                engine.readTelephonySnapshot();

        StringBuilder sb = new StringBuilder();
        final String FMT = "%-16s : %s\n";

        sb.append(String.format(
                Locale.US,
                FMT,
                "Airplane Mode",
                t.airplaneOn ? "ON" : "OFF"
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "SIM State",
                describeSimState(t.simState)
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Mobile Service",
                t.inService ? "IN SERVICE" : "OUT OF SERVICE"
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Mobile Data",
                describeDataState(t.dataState)
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Phone Type",
                safeStr(t.phoneType)
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Data Network",
                safeStr(t.dataNetwork)
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Carrier",
                safeStr(t.carrier)
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Country ISO",
                safeStr(t.countryIso)
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Operator Code",
                safeStr(t.operatorCode)
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Roaming",
                t.roaming ? "Yes" : "No"
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Signal Level",
                t.signalLevel >= 0 ? String.valueOf(t.signalLevel) : "N/A"
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "5G NR",
                t.nr5gActive ? "Active" : "No"
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Active SIMs",
                t.activeSimCount > 0 ? String.valueOf(t.activeSimCount) : "N/A"
        ));

        if (t.sims != null && !t.sims.isEmpty()) {
            sb.append("\nSIM Entries:\n");
            for (iDoctorEngine.SimEntry sim : t.sims) {
                if (sim == null) continue;
                sb.append("  slot")
                  .append(sim.slot)
                  .append(" : ")
                  .append(safeStr(sim.carrier))
                  .append("\n");
            }
        }

        sb.append("\n=== Wireless ===\n\n");

        sb.append(String.format(
                Locale.US,
                FMT,
                "Wi-Fi Supported",
                c.wifiSupported ? "Yes" : "No"
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Wi-Fi Enabled",
                c.wifiEnabled ? "Yes" : "No"
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "SSID",
                safeStr(c.ssid)
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Wi-Fi Band",
                safeStr(c.wifiBand)
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Wi-Fi Standard",
                safeStr(c.wifiStandard)
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Link Speed",
                c.linkSpeedMbps > 0 ? c.linkSpeedMbps + " Mbps" : "N/A"
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "RSSI",
                c.rssiDbm != -1 ? c.rssiDbm + " dBm" : "N/A"
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Frequency",
                c.frequencyMhz > 0 ? c.frequencyMhz + " MHz" : "N/A"
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Wi-Fi MAC",
                safeStr(c.wifiMac)
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Bluetooth",
                c.bluetoothSupported ? "Supported" : "Not supported"
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "Bluetooth State",
                safeStr(c.bluetoothState)
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "BLE",
                c.bleSupported ? "Supported" : "Not supported"
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "NFC",
                c.nfcSupported ? "Supported" : "Not supported"
        ));

        sb.append(String.format(
                Locale.US,
                FMT,
                "NFC Enabled",
                c.nfcEnabled ? "Yes" : "No"
        ));

        return sb.toString();
    }

    // ============================================================
    // System Info — ENGINE-BASED
    // ============================================================

    private String buildSystemInfo() {

        iDoctorEngine.SystemSnapshot s =
                engine.readSystemSnapshot();

        StringBuilder sb = new StringBuilder();
        final String FMT = "%-13s : %s\n";

        sb.append(String.format(Locale.US, FMT, "Manufacturer", safeStr(s.manufacturer)));
        sb.append(String.format(Locale.US, FMT, "Brand",        safeStr(s.brand)));
        sb.append(String.format(Locale.US, FMT, "Model",        safeStr(s.model)));
        sb.append(String.format(Locale.US, FMT, "Device",       safeStr(s.device)));
        sb.append(String.format(Locale.US, FMT, "Product",      safeStr(s.product)));
        sb.append(String.format(Locale.US, FMT, "Hardware",     safeStr(s.hardware)));
        sb.append(String.format(Locale.US, FMT, "Board",        safeStr(s.board)));
        sb.append(String.format(Locale.US, FMT, "Bootloader",   safeStr(s.bootloader)));

        sb.append("\n=== System Fingerprint ===\n\n");
        sb.append(safeStr(s.fingerprint)).append("\n\n");

        sb.append(String.format(Locale.US, FMT, "Android ID",  safeStr(s.androidId)));
        sb.append(String.format(Locale.US, FMT, "Device Type", safeStr(s.deviceType)));
        sb.append(String.format(Locale.US, FMT, "Region",      safeStr(s.region)));
        sb.append(String.format(Locale.US, FMT, "Vendor Name", safeStr(s.vendorName)));

        if (!isBlank(s.vbState)) {
            sb.append(String.format(Locale.US, FMT, "VB State", safeStr(s.vbState)));
        }
        if (!isBlank(s.vbDevice)) {
            sb.append(String.format(Locale.US, FMT, "VB Device", safeStr(s.vbDevice)));
        }
        if (!isBlank(s.flashLock)) {
            sb.append(String.format(Locale.US, FMT, "Flash Lock", safeStr(s.flashLock)));
        }

        return sb.toString();
    }

    // ============================================================
    // Android Build Info — ENGINE-BASED
    // ============================================================

    private String buildAndroidInfo() {

        iDoctorEngine.AndroidSnapshot a =
                engine.readAndroidSnapshot();

        StringBuilder sb = new StringBuilder();
        final String FMT = "%-15s : %s\n";

        sb.append(String.format(
                Locale.US,
                FMT,
                "Android",
                safeStr(a.release) + " (SDK " + a.sdk + ")"
        ));

        sb.append(String.format(Locale.US, FMT, "Security Patch", safeStr(a.securityPatch)));
        sb.append(String.format(Locale.US, FMT, "Build ID",       safeStr(a.buildId)));
        sb.append(String.format(Locale.US, FMT, "Build Type",     safeStr(a.buildType)));
        sb.append(String.format(Locale.US, FMT, "Build Tags",     safeStr(a.buildTags)));
        sb.append(String.format(Locale.US, FMT, "Incremental",    safeStr(a.incremental)));

        sb.append("\n=== Baseband ===\n\n");
        sb.append(String.format(Locale.US, FMT, "Release", safeStr(a.baseband)));

        sb.append("\n=== Vendor Release ===\n\n");
        sb.append(String.format(Locale.US, FMT, "Vendor", safeStr(a.vendorRelease)));

        return sb.toString();
    }

    // ============================================================
    // CPU Info — ENGINE-BASED
    // ============================================================

    private String buildCpuInfo() {

        iDoctorEngine.CpuSnapshot cpu =
                engine.readCpuSnapshot();

        iDoctorEngine.ThermalSnapshot ts =
                engine.readThermalSnapshot();

        StringBuilder sb = new StringBuilder();
        final String FMT = "%-12s : %s\n";

        sb.append(String.format(Locale.US, FMT, "ABI", safeStr(cpu.abi)));
        sb.append(String.format(Locale.US, FMT, "CPU Cores", String.valueOf(cpu.cores)));

        if (!isBlank(cpu.modelName) && !"N/A".equals(cpu.modelName)) {
            sb.append(String.format(Locale.US, FMT, "model name", cpu.modelName));
        }

        if (!isBlank(cpu.hardware) && !"N/A".equals(cpu.hardware)) {
            sb.append(String.format(Locale.US, FMT, "hardware", cpu.hardware));
        }

        if (!isBlank(cpu.governor) && !"N/A".equals(cpu.governor)) {
            sb.append(String.format(Locale.US, FMT, "Governor", cpu.governor));
        }

        if (cpu.currentFreqKHz > 0 || cpu.minFreqKHz > 0 || cpu.maxFreqKHz > 0) {

            StringBuilder freq = new StringBuilder();

            if (cpu.currentFreqKHz > 0) {
                freq.append("cur=").append(cpu.currentFreqKHz / 1000).append(" ");
            }
            if (cpu.minFreqKHz > 0) {
                freq.append("min=").append(cpu.minFreqKHz / 1000).append(" ");
            }
            if (cpu.maxFreqKHz > 0) {
                freq.append("max=").append(cpu.maxFreqKHz / 1000);
            }

            sb.append(String.format(
                    Locale.US,
                    FMT,
                    "Freq (MHz)",
                    freq.toString().trim()
            ));
        }

        if (cpu.coreFreqs != null && cpu.coreFreqs.size() >= 2) {
            sb.append(String.format(
                    Locale.US,
                    FMT,
                    "Cluster",
                    "big.LITTLE / multi-cluster detected"
            ));
        }

        if (ts != null && ts.cpu != null && ts.cpu.valid) {
            sb.append(String.format(
                    Locale.US,
                    FMT,
                    "CPU Temp",
                    String.format(Locale.US, "%.1f°C", ts.cpu.tempC)
            ));
        } else {
            sb.append(String.format(Locale.US, FMT, "CPU Temp", "N/A"));
        }

        if (isRooted) {

            sb.append("\n[Root CPU tables]\n");

            boolean added = false;

            if (cpu.coreFreqs != null) {
                for (iDoctorEngine.CoreFreq cf : cpu.coreFreqs) {
                    if (cf == null) continue;

                    StringBuilder row = new StringBuilder();

                    if (cf.currentFreqKHz > 0) {
                        row.append("cur=").append(cf.currentFreqKHz / 1000).append("MHz ");
                    }
                    if (cf.minFreqKHz > 0) {
                        row.append("min=").append(cf.minFreqKHz / 1000).append("MHz ");
                    }
                    if (cf.maxFreqKHz > 0) {
                        row.append("max=").append(cf.maxFreqKHz / 1000).append("MHz");
                    }

                    if (row.length() > 0) {
                        sb.append(String.format(
                                Locale.US,
                                FMT,
                                "cpu" + cf.coreIndex,
                                row.toString().trim()
                        ));
                        added = true;
                    }
                }
            }

            if (!added) {
                sb.append("Root CPU details not exposed by current kernel.\n");
            }
        }

        return sb.toString();
    }

    // ============================================================
    // GPU Info — ENGINE-BASED
    // ============================================================

    private String buildGpuInfo() {

        iDoctorEngine.GpuSnapshot gpu =
                engine.readGpuSnapshot();

        iDoctorEngine.ThermalSnapshot ts =
                engine.readThermalSnapshot();

        StringBuilder sb = new StringBuilder();
        final String FMT = "%-12s : %s\n";

        if (!isBlank(gpu.openGlEsVersion) && !"N/A".equals(gpu.openGlEsVersion)) {
            sb.append(String.format(Locale.US, FMT, "OpenGL ES", gpu.openGlEsVersion));
        }

        if (!isBlank(gpu.eglHardware) && !"N/A".equals(gpu.eglHardware)) {
            sb.append(String.format(Locale.US, FMT, "EGL HW", gpu.eglHardware));
        }

        if (!isBlank(gpu.driver) && !"N/A".equals(gpu.driver)) {
            sb.append(String.format(Locale.US, FMT, "GPU Driver", gpu.driver));
        }

        if (!isBlank(gpu.gpuName) && !"N/A".equals(gpu.gpuName)) {
            sb.append(String.format(Locale.US, FMT, "GPU Name", gpu.gpuName));
        }

        if (ts != null && ts.gpu != null && ts.gpu.valid) {
            sb.append(String.format(
                    Locale.US,
                    FMT,
                    "GPU Temp",
                    String.format(Locale.US, "%.1f°C", ts.gpu.tempC)
            ));
        } else {
            sb.append(String.format(Locale.US, FMT, "GPU Temp", "N/A"));
        }

        if (isRooted) {

            sb.append("\n[Root GPU tables]\n");

            boolean addedRootGpu = false;

            if (gpu.currentFreqHz > 0 || gpu.minFreqHz > 0 || gpu.maxFreqHz > 0) {

                StringBuilder row = new StringBuilder();

                if (gpu.currentFreqHz > 0) {
                    row.append("cur=").append(gpu.currentFreqHz / 1000000).append("MHz ");
                }
                if (gpu.minFreqHz > 0) {
                    row.append("min=").append(gpu.minFreqHz / 1000000).append("MHz ");
                }
                if (gpu.maxFreqHz > 0) {
                    row.append("max=").append(gpu.maxFreqHz / 1000000).append("MHz");
                }

                sb.append(String.format(
                        Locale.US,
                        FMT,
                        "Freq",
                        row.toString().trim()
                ));

                addedRootGpu = true;
            }

            String busy = readSysString("/sys/class/kgsl/kgsl-3d0/gpu_busy_percentage");
            if (busy != null && !busy.isEmpty()) {
                sb.append(String.format(Locale.US, FMT, "Busy GPU", busy.trim() + " %"));
                addedRootGpu = true;
            }

            String avail = readSysString("/sys/class/kgsl/kgsl-3d0/devfreq/available_frequencies");
            if (avail != null && !avail.isEmpty()) {
                sb.append(String.format(Locale.US, FMT, "Avail Freq", avail.trim()));
                addedRootGpu = true;
            }

            if (!addedRootGpu) {
                sb.append("Root GPU metrics not exposed by current driver.\n");
            }
        }

        return sb.toString();
    }

    // ============================================================
    // THERMAL SENSORS — INTERNAL
    // ============================================================

    private String buildThermalInternalReport() {

        iDoctorEngine.ThermalSnapshot ts =
                engine.readThermalSnapshot();

        StringBuilder sb = new StringBuilder();

        sb.append("THERMAL SENSORS (INTERNAL)\n");
        sb.append("──────────────────────────\n");

        final String FMT = "%-18s : %5.1f°C  (%s)\n";

        if (ts == null) {
            sb.append("Thermal sensors not available on this device.\n");
            return sb.toString();
        }

        if (ts.cpu != null && ts.cpu.valid) {
            sb.append(String.format(Locale.US, FMT, "CPU", ts.cpu.tempC, thermalState(ts.cpu.tempC)));
        } else {
            sb.append(String.format(Locale.US, "%-18s : %s\n", "CPU", "N/A"));
        }

        if (ts.gpu != null && ts.gpu.valid) {
            sb.append(String.format(Locale.US, FMT, "GPU", ts.gpu.tempC, thermalState(ts.gpu.tempC)));
        } else {
            sb.append(String.format(Locale.US, "%-18s : %s\n", "GPU", "N/A"));
        }

        if (ts.battery != null && ts.battery.valid) {
            sb.append(String.format(Locale.US, FMT, "Battery", ts.battery.tempC, thermalState(ts.battery.tempC)));
        } else {
            sb.append(String.format(Locale.US, "%-18s : %s\n", "Battery", "N/A"));
        }

        if (ts.skin != null && ts.skin.valid) {
            sb.append(String.format(Locale.US, FMT, "Skin", ts.skin.tempC, thermalState(ts.skin.tempC)));
        }

        if (ts.pmic != null && ts.pmic.valid) {
            sb.append(String.format(Locale.US, FMT, "PMIC", ts.pmic.tempC, thermalState(ts.pmic.tempC)));
        }

        if (ts.charger != null && ts.charger.valid) {
            sb.append(String.format(Locale.US, FMT, "Charger", ts.charger.tempC, thermalState(ts.charger.tempC)));
        }

        if (ts.modemMain != null && ts.modemMain.valid) {
            sb.append(String.format(Locale.US, FMT, "Modem Main", ts.modemMain.tempC, thermalState(ts.modemMain.tempC)));
        }

        if (ts.modemAux != null && ts.modemAux.valid) {
            sb.append(String.format(Locale.US, FMT, "Modem Aux", ts.modemAux.tempC, thermalState(ts.modemAux.tempC)));
        }

        sb.append("\n");
        sb.append("Thermal Zones        : ").append(ts.thermalZoneCount).append("\n");
        sb.append("Cooling Devices      : ").append(ts.hardwareCoolingDeviceCount).append("\n");

        if (ts.coolingDevices != null && !ts.coolingDevices.isEmpty()) {
            sb.append("\nHardware Cooling:\n");
            for (String cd : ts.coolingDevices) {
                sb.append("  ").append(cd).append("\n");
            }
        } else {
            sb.append("\nHardware Cooling:\n");
            sb.append("  No hardware cooling devices found (passive cooling only)\n");
        }

        if (isRooted) {

            File thermalDir = new File("/sys/class/thermal");
            File[] zones = thermalDir.listFiles((dir, name) -> name.startsWith("thermal_zone"));

            if (zones != null && zones.length > 0) {

                sb.append("\nAdvanced Thermal (Root)\n");
                sb.append("──────────────────────\n");

                for (File zone : zones) {
                    try {
                        String type = readSysFile(zone, "type");
                        String tempRaw = readSysFile(zone, "temp");

                        if (type == null || tempRaw == null) continue;

                        float tempC = Float.parseFloat(tempRaw.trim()) / 1000f;

                        sb.append("\n")
                          .append(zone.getName())
                          .append(" [")
                          .append(type.trim())
                          .append("]\n");

                        sb.append("  Current Temp : ")
                          .append(String.format(Locale.US, "%.1f°C", tempC))
                          .append("\n");

                        for (int i = 0; i < 10; i++) {
                            String tp = readSysFile(zone, "trip_point_" + i + "_temp");
                            String tpType = readSysFile(zone, "trip_point_" + i + "_type");

                            if (tp == null || tpType == null) break;

                            float tpC = Float.parseFloat(tp.trim()) / 1000f;

                            sb.append("  Trip ")
                              .append(i)
                              .append(" (")
                              .append(tpType.trim())
                              .append(") : ")
                              .append(String.format(Locale.US, "%.1f°C", tpC))
                              .append("\n");
                        }
                    } catch (Throwable ignore) {}
                }
            }

        } else {
            sb.append("\nAdvanced Info: requires root access\n");
        }

        return sb.toString();
    }

    // ============================================================
    // Vulkan Info
    // ============================================================

    private String buildVulkanInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            boolean hasLevel = getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL
            );

            boolean hasVersion = getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_VULKAN_HARDWARE_VERSION
            );

            sb.append("Feature Level : ").append(hasLevel ? "Yes" : "No").append("\n");
            sb.append("Feature Vers  : ").append(hasVersion ? "Yes" : "No").append("\n");
        } catch (Throwable ignore) {}

        String hw = getProp("ro.hardware.vulkan");
        if (hw != null && !hw.isEmpty()) {
            sb.append("Vulkan HW     : ").append(hw).append("\n");
        }

        String enable = getProp("ro.vulkan.enable");
        if (enable != null && !enable.isEmpty()) {
            sb.append("Vulkan Enable : ").append(enable).append("\n");
        }

        String layers = getProp("debug.vulkan.layers");
        if (layers != null && !layers.isEmpty()) {
            sb.append("Debug Layers  : ").append(layers).append("\n");
        }

        if (sb.length() == 0) {
            sb.append("No Vulkan information available.\n");
        }

        return sb.toString();
    }

    // ============================================================
    // RAM Info — ENGINE-BASED
    // ============================================================

    private String buildRamInfo() {

        iDoctorEngine.MemorySnapshot m =
                engine.readMemorySnapshot();

        StringBuilder sb = new StringBuilder();

        sb.append(padRight("Total RAM", 14)).append(": ")
          .append(m.totalRamMb >= 0 ? m.totalRamMb + " MB" : "N/A")
          .append("\n");

        sb.append(padRight("Used RAM", 14)).append(": ")
          .append(m.usedRamMb >= 0 ? m.usedRamMb + " MB" : "N/A")
          .append("\n");

        sb.append(padRight("Free RAM", 14)).append(": ")
          .append(m.freeRamMb >= 0 ? m.freeRamMb + " MB" : "N/A")
          .append("\n");

        sb.append(padRight("Low Memory", 14)).append(": ")
          .append(m.lowMemory ? "Yes" : "No")
          .append("\n");

        sb.append(padRight("Threshold", 14)).append(": ")
          .append(m.thresholdMb >= 0 ? m.thresholdMb + " MB" : "N/A")
          .append("\n");

        sb.append("\n/proc/meminfo (core):\n\n");

        appendKbAsMb(sb, "MemTotal", m.memTotalKb);
        appendKbAsMb(sb, "MemFree", m.memFreeKb);
        appendKbAsMb(sb, "Cached", m.cachedKb);
        appendKbAsMb(sb, "Active", m.activeKb);
        appendKbAsMb(sb, "Inactive", m.inactiveKb);
        appendKbAsMb(sb, "ZRAM SwapTotal", m.swapTotalKb);
        appendKbAsMb(sb, "ZRAM SwapFree", m.swapFreeKb);

        sb.append(padRight("Buffers", 14)).append(": ")
          .append(m.buffersKb >= 0 ? m.buffersKb + " kB" : "N/A")
          .append("\n");

        boolean zramActive = m.swapTotalKb > 0;

        sb.append(padRight("ZRAM Status", 14)).append(": ")
          .append(zramActive ? "Active" : "Not active")
          .append("\n");

        if (!isRooted) {
            sb.append(padRight("ZRAM Details", 14))
              .append(": Requires Root access\n");
        }

        return sb.toString();
    }

    // ============================================================
    // STORAGE Info — ENGINE-BASED
    // ============================================================

    private String buildStorageInfo() {

        iDoctorEngine.StorageSnapshot s =
                engine.readStorageSnapshot();

        StringBuilder sb = new StringBuilder();

        appendStorageBlockEngine(sb, s.internal);
        appendStorageBlockEngine(sb, s.externalPrimary);

        sb.append("\n=== Core Mounts ===\n\n");
        if (!isBlank(s.mounts) && !"N/A".equals(s.mounts)) {
            appendInterestingMounts(sb, s.mounts);
        } else {
            sb.append("  Not exposed by this device.\n");
        }

        sb.append("\n=== Partitions ===\n\n");
        if (!isBlank(s.partitions) && !"N/A".equals(s.partitions)) {
            sb.append(s.partitions.trim()).append("\n");
        } else {
            sb.append("  Not exposed by this device.\n");
        }

        if (sb.length() == 0) {
            sb.append("Unable to read storage information.\n");
        }

        return sb.toString();
    }

    // ============================================================
    // SoC Temperature — CPU average (non-root, safe)
    // ============================================================

    private Double getSocTempCpuAverage() {
        try {
            File dir = new File("/sys/class/thermal");
            if (!dir.exists() || !dir.isDirectory()) return null;

            File[] zones = dir.listFiles();
            if (zones == null) return null;

            double sum = 0;
            int count = 0;

            for (File z : zones) {
                if (!z.getName().startsWith("thermal_zone")) continue;

                String type = readSysString(z.getAbsolutePath() + "/type");
                if (type == null) continue;

                String low = type.toLowerCase(Locale.US);
                if (!low.contains("cpu")) continue;

                long t = readSysLong(z.getAbsolutePath() + "/temp");
                if (t <= 0) continue;

                double c = (t > 1000) ? t / 1000.0 : t / 10.0;
                sum += c;
                count++;
            }

            if (count == 0) return null;
            return sum / count;

        } catch (Throwable ignore) {
            return null;
        }
    }

    // ============================================================
    // THERMAL HELPERS — INTERNAL
    // ============================================================

    private String readSysFile(File base, String name) {
        if (base == null) return null;
        File f = new File(base, name);
        if (!f.exists()) return null;

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            return line != null ? line.trim() : null;
        } catch (Throwable ignore) {
            return null;
        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception ignored) {}
        }
    }

    private String thermalState(float tempC) {
        if (tempC < 30f) return "COOL";
        if (tempC < 45f) return "NORMAL";
        if (tempC < 60f) return "WARM";
        if (tempC < 75f) return "HOT";
        return "CRITICAL";
    }

    private String mapThermalType(String type) {

        if (type == null) return "";

        String t = type.toLowerCase(Locale.US);

        if (t.contains("battery_therm") || t.contains("batt_therm")) return "Battery Shell";
        if (t.contains("battery")) return "Battery";
        if (t.matches(".*cpu[-_]?0.*")) return "CPU Cluster 0";
        if (t.matches(".*cpu[-_]?1.*")) return "CPU Cluster 1";
        if (t.contains("cpu")) return "CPU Core";
        if (t.contains("gpu")) return "GPU";
        if (t.contains("soc")) return "SoC";
        if (t.contains("skin")) return "Device Skin";
        if (t.contains("backlight")) return "Backlight";
        if (t.contains("ddr")) return "DDR Memory";
        if (t.contains("mem")) return "Memory";

        return type;
    }

    // ============================================================
    // GENERIC HELPERS
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
            } catch (Exception ignored) {}
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
            } catch (Exception ignored) {}
        }
    }

    private long readSysLong(String path) {
        String s = readSysString(path);
        if (s == null || s.isEmpty()) return -1;
        try {
            return Long.parseLong(s);
        } catch (Throwable ignore) {
            return -1;
        }
    }

    private String getProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null ? line.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private long parseKb(String raw) {
        try {
            return Long.parseLong(raw.replaceAll("[^0-9]", ""));
        } catch (Throwable t) {
            return 0;
        }
    }

    private long parseKbSafe(String s) {
        try {
            return parseKb(s);
        } catch (Throwable e) {
            return 0;
        }
    }

    private String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() >= n) return s;
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < n) sb.append(' ');
        return sb.toString();
    }

    private String safeStr(String s) {
        return (s == null || s.trim().isEmpty()) ? "N/A" : s.trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String describeSimState(int simState) {
        switch (simState) {
            case TelephonyManager.SIM_STATE_READY: return "READY";
            case TelephonyManager.SIM_STATE_ABSENT: return "ABSENT";
            case TelephonyManager.SIM_STATE_PIN_REQUIRED: return "PIN REQUIRED";
            case TelephonyManager.SIM_STATE_PUK_REQUIRED: return "PUK REQUIRED";
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED: return "NETWORK LOCKED";
            default: return "UNKNOWN";
        }
    }

    private String describeDataState(int dataState) {
        switch (dataState) {
            case TelephonyManager.DATA_CONNECTED: return "CONNECTED";
            case TelephonyManager.DATA_CONNECTING: return "CONNECTING";
            case TelephonyManager.DATA_DISCONNECTED: return "DISCONNECTED";
            default: return "UNKNOWN";
        }
    }

    private void appendKbAsMb(StringBuilder sb, String label, long kb) {
        sb.append(padRight(label, 14)).append(": ")
          .append(kb >= 0 ? (kb / 1024) + " MB" : "N/A")
          .append("\n");
    }

    private void appendStorageBlockEngine(StringBuilder sb, iDoctorEngine.StorageBlock block) {
        if (block == null) return;
        if (isBlank(block.label) || "N/A".equals(block.label)) return;
        if (block.totalGb < 0 && block.usedGb < 0 && block.freeGb < 0) return;

        sb.append(block.label).append(":\n");
        sb.append("  ").append(padRight("Path", 10))
          .append(": ").append(safeStr(block.path)).append("\n");
        sb.append("  ").append(padRight("Total", 10))
          .append(": ").append(block.totalGb >= 0 ? block.totalGb + " GB" : "N/A").append("\n");
        sb.append("  ").append(padRight("Used", 10))
          .append(": ").append(block.usedGb >= 0 ? block.usedGb + " GB" : "N/A").append("\n");
        sb.append("  ").append(padRight("Free", 10))
          .append(": ").append(block.freeGb >= 0 ? block.freeGb + " GB" : "N/A").append("\n\n");
    }

    private void appendInterestingMounts(StringBuilder sb, String mounts) {
        String[] lines = mounts.split("\n");
        String[] interesting = {
                "/", "/system", "/vendor", "/product",
                "/data", "/cache", "/metadata"
        };

        for (String line : lines) {
            String[] parts = line.split("\\s+");
            if (parts.length < 3) continue;

            String mountPoint = parts[1];
            boolean hit = false;

            for (String it : interesting) {
                if (mountPoint.equals(it)) {
                    hit = true;
                    break;
                }
            }

            if (hit) {
                sb.append("  ")
                  .append(padRight(mountPoint, 10))
                  .append(": ")
                  .append(parts[2])
                  .append(" (")
                  .append(parts[0])
                  .append(")\n");
            }
        }
    }
}
