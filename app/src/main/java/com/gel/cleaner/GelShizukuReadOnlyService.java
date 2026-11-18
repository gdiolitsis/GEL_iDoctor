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
    private static final int MAX_OUTPUT = 96 * 1024;

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

    private static String fixedCommand(String... immutableArgs) throws IOException {
        if (immutableArgs == null || immutableArgs.length < 1) return null;
        ProcessBuilder builder = new ProcessBuilder(immutableArgs);
        builder.redirectErrorStream(true);
        java.lang.Process process = builder.start();
        try {
            // A blocked or excessive diagnostic query is unavailable, not clean.
            if (!process.waitFor(3000, TimeUnit.MILLISECONDS)) {
                process.destroyForcibly();
                return null;
            }
            if (process.exitValue() != 0) return null;
            try (InputStream input = process.getInputStream();
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                byte[] chunk = new byte[2048];
                int n;
                while ((n = input.read(chunk)) >= 0) {
                    if (buffer.size() + n > MAX_OUTPUT) return null;
                    if (n > 0) buffer.write(chunk, 0, n);
                }
                return buffer.toString(StandardCharsets.UTF_8.name());
            }
        } catch (InterruptedException interrupted) {
            process.destroyForcibly();
            Thread.currentThread().interrupt();
            return null;
        } finally {
            process.destroy();
        }
    }
}
