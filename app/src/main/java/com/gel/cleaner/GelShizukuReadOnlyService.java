package com.gel.cleaner;

import android.os.Process;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Non-daemon Shizuku UserService. Executes ONLY hardcoded, read-only probes as
 * ADB shell UID 2000; intentionally refuses UID 0/root for minimal privilege.
 *
 * The caller can choose a fixed operation ID but NEVER supply a shell command,
 * filesystem path, process name, URL or arbitrary executable argument.
 * Full bugreports and raw crash buffers never leave this process.
 */
public final class GelShizukuReadOnlyService extends IGelShizukuReadOnly.Stub {
    private static final int SHELL_UID = 2000;
    private static final int MAX_OUTPUT = 384 * 1024;

    public GelShizukuReadOnlyService() { }

    @Override public String probe(int operationId) {
        if (Process.myUid() != SHELL_UID) return "UNAVAILABLE|NOT_ADB_SHELL";
        try {
            switch (operationId) {
                case GelShizukuEvidence.CAPACITY:
                    return batteryCapacity();
                case GelShizukuEvidence.CRASH:
                    return crashEvents();
                case GelShizukuEvidence.THERMAL:
                    return thermalStatus();
                case GelShizukuEvidence.MEMORY:
                    return memorySummary();
                case GelShizukuEvidence.BOOT:
                    return bootMetadata();
                case GelShizukuEvidence.UFS:
                    return ufsWear();
                case GelShizukuEvidence.LOW_MEMORY:
                    return lowMemoryEvents();
                case GelShizukuEvidence.APP_OPS:
                    return privacyAppOps();
                default:
                    return "UNAVAILABLE|UNKNOWN_OPERATION";
            }
        } catch (IOException | SecurityException error) {
            // Avoid returning private paths, stack traces, command output or log lines.
            return "UNAVAILABLE|ACCESS_RESTRICTED";
        } catch (RuntimeException error) {
            return "UNAVAILABLE|DIAGNOSTIC_UNAVAILABLE";
        }
    }

    @Override public void destroy() {
        System.exit(0); // Shizuku UserService only, never the Android app.
    }

    private static String batteryCapacity() throws IOException {
        // Standard Linux power_supply charge_full* values are microampere-hours.
        // Fixed, known paths only; do not guess non-standard OEM gauge units.
        String[] full = {
                "/sys/class/power_supply/battery/charge_full",
                "/sys/class/power_supply/bms/charge_full",
                "/sys/class/power_supply/fg/charge_full"
        };
        String[] design = {
                "/sys/class/power_supply/battery/charge_full_design",
                "/sys/class/power_supply/bms/charge_full_design",
                "/sys/class/power_supply/fg/charge_full_design"
        };
        long f = 0, d = 0;
        String fp = "", dp = "";
        for (String path : full) {
            f = readMicroAmpHours(path);
            if (f > 0) { fp = shortSource(path); break; }
        }
        for (String path : design) {
            d = readMicroAmpHours(path);
            if (d > 0) { dp = shortSource(path); break; }
        }
        if (f <= 0 && d <= 0) return "UNAVAILABLE|CAPACITY_FIELDS_INACCESSIBLE";
        return "CAPACITY|" + f + "|" + d + "|" + fp + "|" + dp;
    }

    private static long readMicroAmpHours(String path) {
        try {
            String raw = firstLine(path, 48);
            if (!raw.matches("\\d{6,9}")) return 0; // no guessed unit
            long microAh = Long.parseLong(raw);
            long milliAh = microAh / 1000L;
            return milliAh >= 100L && milliAh <= 30000L ? milliAh : 0;
        } catch (IOException | RuntimeException ignored) {
            return 0;
        }
    }

    private static String shortSource(String path) {
        if (path.contains("/bms/")) return "BMS";
        if (path.contains("/fg/")) return "FG";
        return "BATTERY";
    }

    private static String crashEvents() throws IOException {
        // Observe only the crash buffer, bounded to latest 200 lines. No raw
        // logcat leaves the service, even if a crash log contains personal data.
        String raw = fixedCommand("/system/bin/logcat", "-d", "-b", "crash", "-t", "200");
        if (raw == null) return "UNAVAILABLE|CRASH_BUFFER_RESTRICTED";
        CriticalSystemCrashEvidence.Summary summary =
                CriticalSystemCrashEvidence.parseUserReport(
                        new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8)),
                        Collections.emptySet());
        StringBuilder result = new StringBuilder("CRASH|READ");
        int recorded = 0;
        for (CriticalSystemCrashEvidence.Event e : summary.events) {
            if (recorded >= 8) break;
            // Produced by the existing *critical-process* attribution parser.
            if (e.process == null || !e.process.matches("[A-Za-z0-9_.:]{1,110}"))
                continue;
            if (!e.kind.matches("CRASH|ANR|NATIVE_CRASH|WATCHDOG|KERNEL_PANIC"))
                continue;
            result.append('\n').append(e.process).append('|')
                    .append(e.kind).append('|')
                    .append(e.when.replace('|', '_').replace('\n', ' '));
            recorded++;
        }
        return result.toString();
    }

    private static String thermalStatus() throws IOException {
        String raw = fixedCommand("/system/bin/dumpsys", "thermalservice");
        if (raw == null) return "UNAVAILABLE|THERMAL_SERVICE_RESTRICTED";
        Matcher m = Pattern.compile("(?im)^\\s*(?:mStatus|Thermal Status|Current Thermal Status)\\s*[:=]\\s*([0-6])\\b")
                .matcher(raw);
        return m.find() ? "THERMAL_STATUS|" + m.group(1)
                : "UNAVAILABLE|THERMAL_STATUS_NOT_EXPOSED";
    }

    private static String memorySummary() throws IOException {
        String raw = fixedCommand("/system/bin/dumpsys", "meminfo");
        if (raw == null) return "UNAVAILABLE|MEMINFO_RESTRICTED";
        Matcher total = Pattern.compile("(?im)^\\s*Total RAM:\\s*([\\d,]+)K\\b").matcher(raw);
        Matcher free = Pattern.compile("(?im)^\\s*Free RAM:\\s*([\\d,]+)K\\b").matcher(raw);
        if (!total.find() || !free.find()) return "UNAVAILABLE|MEMINFO_SUMMARY_NOT_EXPOSED";
        return "MEMORY|" + numberOnly(total.group(1)) + "|" + numberOnly(free.group(1));
    }

    private static String bootMetadata() throws IOException {
        // Boot reason is metadata, NOT proof of a previous crash.
        String raw = fixedCommand("/system/bin/getprop", "ro.boot.bootreason");
        if (raw == null) return "UNAVAILABLE|BOOT_REASON_RESTRICTED";
        String reason = raw.trim().replaceAll("[^a-zA-Z0-9_,.:-]", "");
        if (reason.length() > 80) reason = reason.substring(0, 80);
        return reason.isEmpty() ? "UNAVAILABLE|BOOT_REASON_UNKNOWN" : "BOOT|" + reason;
    }

    /**
     * Read only the well-defined UFS health descriptor nodes exposed by the
     * kernel. The service discovers platform UFS devices locally: callers can
     * supply neither paths nor device names. No health descriptor => unknown.
     */
    private static String ufsWear() {
        java.util.ArrayList<File> candidates = new java.util.ArrayList<>();
        candidates.add(new File("/dev/sys/block/bootdevice/health_descriptor"));
        File root = new File("/sys/bus/platform/devices");
        try {
            File[] devices = root.listFiles();
            if (devices != null && devices.length <= 2048) {
                for (File device : devices) {
                    String name = device.getName().toLowerCase(java.util.Locale.US);
                    if (name.contains("ufs") && name.matches("[a-z0-9._:-]{1,96}"))
                        candidates.add(new File(device, "health_descriptor"));
                }
            }
        } catch (SecurityException ignored) { }
        for (File health : candidates) {
            int a = readUfsByte(new File(health, "life_time_estimation_a"));
            int b = readUfsByte(new File(health, "life_time_estimation_b"));
            int e = readUfsByte(new File(health, "eol_info"));
            if (a > 0 || b > 0 || e > 0) return "UFS|" + a + "|" + b + "|" + e;
        }
        // AOSP also exposes eMMC EXT_CSD wear under fixed mmc card nodes.
        return emmcWear();
    }

    /** Two eMMC lifetime bytes and the pre-EOL byte from kernel sysfs.
     * Read only known boot-card candidates, not raw block devices. */
    private static String emmcWear() {
        String[] cards = {
                "/sys/bus/mmc/devices/mmc0:0001",
                "/sys/bus/mmc/devices/mmc1:0001"
        };
        for (String card : cards) {
            try {
                String type = firstLine(card + "/type", 16);
                if (!"MMC".equalsIgnoreCase(type)) continue;
                String[] pair = firstLine(card + "/life_time", 48).split("\\s+");
                if (pair.length != 2) continue;
                int a = parseHexByte(pair[0]);
                int b = parseHexByte(pair[1]);
                int e = readUfsByte(new File(card, "pre_eol_info"));
                if ((a > 0 || b > 0) && a <= 11 && b <= 11 && e >= 1 && e <= 3)
                    return "EMMC|" + a + "|" + b + "|" + e;
            } catch (IOException | RuntimeException ignored) { }
        }
        return "UNAVAILABLE|STORAGE_WEAR_NOT_EXPOSED_TO_SHELL";
    }

    private static int parseHexByte(String value) {
        if (value == null || !value.matches("(?i)(?:0x)?[0-9a-f]{1,2}")) return 0;
        try {
            int parsed = Integer.parseInt(value.replaceFirst("(?i)^0x", ""), 16);
            return parsed > 0 && parsed <= 255 ? parsed : 0;
        } catch (RuntimeException invalid) { return 0; }
    }

    private static int readUfsByte(File f) {
        try {
            return parseHexByte(firstLine(f.getPath(), 16));
        } catch (IOException | RuntimeException ignored) {
            return 0;
        }
    }

    /** Only explicit LMK event tags. Generic app restarts are NOT memory kills. */
    private static String lowMemoryEvents() throws IOException {
        String raw = fixedCommand("/system/bin/logcat", "-d", "-b", "events", "-t", "350");
        if (raw == null || raw.isEmpty()) return "UNAVAILABLE|MEMORY_EVENTS_INACCESSIBLE";
        int count = 0;
        for (String line : raw.split("\\n")) {
            if (line.matches("(?i)^.{0,170}\\b(?:lmk_kill|am_kill_for_memory)\\b.{0,500}$"))
                count++;
        }
        // A bounded rolling events buffer, never the complete device history.
        return "LMK|" + Math.min(350, count);
    }

    /**
     * Optional bounded AppOps sample. Only rows with an explicit Access time
     * leave the shell process; no raw dump, rejected ops, location values or
     * attribution/proxy data are returned. Absence is never proof of no access.
     */
    private static String privacyAppOps() throws IOException {
        String raw = fixedCommand("/system/bin/dumpsys", "appops");
        if (raw == null) return "UNAVAILABLE|APPOPS_RESTRICTED_OR_TOO_LARGE";
        Pattern pkgPattern = Pattern.compile("^\\s*Package ([A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+):\\s*$");
        Pattern opPattern = Pattern.compile("^\\s*(CAMERA|RECORD_AUDIO|FINE_LOCATION|COARSE_LOCATION)\\s*(?:\\([^)]*\\))?\\s*:\\s*$");
        Pattern accessPattern = Pattern.compile("(?i)\\bAccess:\\s*\\[[^\\]]{1,32}\\]\\s*(\\d{4}-\\d\\d-\\d\\d[ T]\\d\\d:\\d\\d:\\d\\d)");
        java.util.LinkedHashMap<String,String> records = new java.util.LinkedHashMap<>();
        String pkg = null, op = null;
        for (String line : raw.split("\\n")) {
            Matcher pm = pkgPattern.matcher(line);
            if (pm.matches()) { pkg = pm.group(1); op = null; continue; }
            if (line.startsWith("Uid ") || line.startsWith("  Uid ")) { pkg = null; op = null; }
            Matcher om = opPattern.matcher(line);
            if (om.matches()) { op = pkg == null ? null : om.group(1); continue; }
            if (pkg == null || op == null) continue;
            Matcher am = accessPattern.matcher(line);
            if (am.find()) {
                String key = pkg + "|" + op;
                String time = am.group(1).replace('T', ' ');
                String prior = records.get(key);
                if (prior == null || time.compareTo(prior) > 0) records.put(key, time);
            }
        }
        if (records.isEmpty()) return "UNAVAILABLE|APPOPS_ACCESS_TIMES_NOT_EXPOSED";
        StringBuilder result = new StringBuilder("APPOPS|SAMPLED");
        int emitted = 0;
        for (java.util.Map.Entry<String,String> item : records.entrySet()) {
            if (emitted++ >= 20) break;
            result.append('\n').append(item.getKey()).append('|').append(item.getValue());
        }
        return result.toString();
    }

    private static String numberOnly(String raw) {
        return raw.replace(",", "");
    }

    private static String firstLine(String path, int max) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File(path)), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            if (line == null || line.length() > max) throw new IOException("Invalid data");
            return line.trim();
        }
    }

    /**
     * Drain stdout while the command executes. Waiting before reading could
     * deadlock when dumpsys/logcat fills the OS pipe. Always bounded by time
     * and bytes; no arbitrary shell, redirection or caller-supplied arguments.
     */
    private static String fixedCommand(String... immutableArgs) throws IOException {
        if (immutableArgs == null || immutableArgs.length < 1) return null;
        ProcessBuilder builder = new ProcessBuilder(immutableArgs);
        builder.redirectErrorStream(true);
        java.lang.Process process = builder.start();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        java.util.concurrent.atomic.AtomicBoolean overflow =
                new java.util.concurrent.atomic.AtomicBoolean(false);
        java.util.concurrent.atomic.AtomicBoolean readFailed =
                new java.util.concurrent.atomic.AtomicBoolean(false);
        Thread drainer = new Thread(() -> {
            try (InputStream input = process.getInputStream()) {
                byte[] chunk = new byte[2048];
                int n;
                while ((n = input.read(chunk)) != -1) {
                    synchronized (buffer) {
                        if (buffer.size() + n > MAX_OUTPUT) overflow.set(true);
                        else if (!overflow.get()) buffer.write(chunk, 0, n);
                    }
                }
            } catch (IOException error) {
                readFailed.set(true);
            }
        }, "gel-shell-readonly-drain");
        drainer.setDaemon(true);
        drainer.start();
        try {
            if (!process.waitFor(4000, TimeUnit.MILLISECONDS)) return null;
            drainer.join(750);
            if (drainer.isAlive() || process.exitValue() != 0
                    || overflow.get() || readFailed.get()) return null;
            synchronized (buffer) {
                return buffer.toString(StandardCharsets.UTF_8.name());
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            process.destroyForcibly();
            if (drainer.isAlive()) drainer.interrupt();
        }
    }

}
