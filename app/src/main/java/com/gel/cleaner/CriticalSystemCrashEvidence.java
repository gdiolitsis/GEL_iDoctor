package com.gel.cleaner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Evidence from explicitly supplied Android bugreports or readable DropBox entries.
 * No root, hidden APIs, network requests, or third-party application crash harvesting.
 *
 * A process being killed/restarted or a UI reload is NOT by itself a crash.
 * Events require a concrete CRASH/ANR signature and a known critical process.
 */
public final class CriticalSystemCrashEvidence {
    private static final int MAX_REPORT_CHARS = 32 * 1024 * 1024;
    private static final int MAX_REPORT_LINES = 450_000;
    private static final int MAX_ZIP_ENTRIES = 300;
    private static final int MAX_EVENTS = 128;
    private static final Pattern PROCESS =
            Pattern.compile("(?i)\\bProcess:\\s*([A-Za-z0-9_.:]+)");
    private static final Pattern ANR =
            Pattern.compile("(?i)\\bANR in\\s+([A-Za-z0-9_.:]+)");
    // Text structures emitted by AOSP tombstones and watchdog diagnostic records.
    // The process identity and an explicit signal must occur in close proximity.
    private static final Pattern NATIVE_PROCESS =
            Pattern.compile("(?i)\\b(?:pid:\\s*\\d+.*?>>>\\s*|Cmdline:\\s*)([A-Za-z0-9_./:-]+)");
    private static final Pattern NATIVE_SIGNAL =
            Pattern.compile("(?i)\\bsignal\\s+(?:6|7|8|11)\\s*\\(SIG(?:ABRT|BUS|FPE|SEGV)\\)");
    private static final Pattern KERNEL_PANIC =
            Pattern.compile("(?i)\\bKernel panic\\s*-\\s*not syncing");
    private static final Pattern WATCHDOG_KILL =
            Pattern.compile("(?i)\\bWATCHDOG KILLING SYSTEM PROCESS\\b");
    private static final Pattern DATE =
            Pattern.compile("(?:\\d{4}-\\d\\d-\\d\\d[ T]\\d\\d:\\d\\d:\\d\\d"
                    + "|\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d(?:\\.\\d+)?)");

    private CriticalSystemCrashEvidence() { }

    public static final class Event {
        public final String role;
        public final String process;
        public final String kind;
        public final String when;
        public final String source;

        private Event(String role, String process, String kind, String when, String source) {
            this.role = role;
            this.process = process;
            this.kind = kind;
            this.when = when;
            this.source = source;
        }

        private String identity() {
            return role + "|" + process + "|" + kind + "|" + when;
        }
    }

    public static final class Summary {
        public final List<Event> events;
        /** True only when an actual document or eligible set of records was read. */
        public final boolean read;
        public final String source;

        private Summary(List<Event> events, boolean read, String source) {
            this.events = Collections.unmodifiableList(new ArrayList<>(events));
            this.read = read;
            this.source = source;
        }

        /**
         * Count distinct *time-separated* critical incidents, not individual log lines.
         * One actual crash often appears in more than one Android log buffer.
         * Missing dates, same-time duplicate entries and different manifestations
         * of the same process failure do not inflate the incident count.
         */
        public int independentIncidentCount() {
            return independentIncidents().size();
        }

        public boolean hasVerifiedRecurrence() {
            List<Event> incidents = independentIncidents();
            for (int i = 0; i < incidents.size(); i++) {
                for (int j = i + 1; j < incidents.size(); j++) {
                    if (incidents.get(i).process.equalsIgnoreCase(incidents.get(j).process))
                        return true;
                }
            }
            return false;
        }

        /** Repeated failures with meaningful temporal separation. This is a
         * buyer warning based on observable instability, NOT a hardware diagnosis. */
        public boolean hasStrongInstabilityIndications() {
            List<Event> incidents = independentIncidents();
            if (incidents.size() < 3 || !hasVerifiedRecurrence()) return false;
            long earliest = Long.MAX_VALUE, latest = Long.MIN_VALUE;
            for (Event e : incidents) {
                long time = eventSecondOfYear(e.when);
                if (time < 0L) continue;
                earliest = Math.min(earliest, time);
                latest = Math.max(latest, time);
            }
            return latest - earliest >= 5L * 60L;
        }

        public boolean hasSeriousSystemIncident() {
            for (Event e : events) {
                if ("KERNEL_PANIC".equals(e.kind) || "WATCHDOG".equals(e.kind))
                    return true;
            }
            return false;
        }

        private List<Event> independentIncidents() {
            List<Event> independent = new ArrayList<>();
            for (Event candidate : events) {
                long time = eventSecondOfYear(candidate.when);
                if (time < 0L) continue;
                boolean sameIncident = false;
                for (Event previous : independent) {
                    long prevTime = eventSecondOfYear(previous.when);
                    if (candidate.process.equalsIgnoreCase(previous.process)
                            && Math.abs(time - prevTime) < 30L) {
                        sameIncident = true;
                        break;
                    }
                }
                if (!sameIncident) independent.add(candidate);
            }
            return independent;
        }
    }

    public static String criticalRole(String rawProcess, Set<String> roleHolders) {
        if (rawProcess == null) return null;
        String process = rawProcess.trim().toLowerCase(Locale.US);
        if (process.equals("kernel")) return "ANDROID_KERNEL";
        if (process.equals("surfaceflinger") || process.equals("/system/bin/surfaceflinger"))
            return "DISPLAY_COMPOSITOR";
        if (process.equals("system_server")) return "ANDROID_CORE";
        if (process.equals("com.android.systemui") || process.startsWith("com.android.systemui:"))
            return "SYSTEM_UI";
        if (process.equals("com.android.phone") || process.startsWith("com.android.phone:")
                || process.equals("com.android.providers.telephony"))
            return "TELEPHONY_SERVICE";
        if (process.equals("com.android.settings") || process.startsWith("com.android.settings:"))
            return "SETTINGS";
        if (roleHolders != null) {
            for (String entry : roleHolders) {
                if (entry == null) continue;
                int split = entry.indexOf('=');
                if (split <= 0) continue;
                String role = entry.substring(0, split);
                String pkg = entry.substring(split + 1).toLowerCase(Locale.US);
                if (!pkg.isEmpty() && (process.equals(pkg) || process.startsWith(pkg + ":")))
                    return role;
            }
        }
        return null;
    }

    public static Event currentError(String process, boolean anr,
                                     Set<String> roleHolders) {
        String role = criticalRole(process, roleHolders);
        return role == null ? null
                : new Event(role, process, anr ? "ANR" : "CRASH", "", "current");
    }

    public static Event dropBoxEvent(String tag, long timestamp, String excerpt,
                                     Set<String> roleHolders) {
        if (tag == null || timestamp <= 0L) return null;
        String type;
        if (tag.endsWith("_native_crash")) type = "NATIVE_CRASH";
        else if (tag.endsWith("_crash")) type = "CRASH";
        else if (tag.endsWith("_anr")) type = "ANR";
        else if (tag.equals("system_server_watchdog")) type = "WATCHDOG";
        else if (tag.equals("SYSTEM_TOMBSTONE")) type = "NATIVE_CRASH";
        else if (tag.equals("SYSTEM_LAST_KMSG")) type = "KERNEL_PANIC";
        else return null; // A generic WTF/log message is NOT proof of a crash
        String process;
        String header = excerpt == null ? "" : excerpt;
        if (tag.equals("SYSTEM_LAST_KMSG")) {
            if (!KERNEL_PANIC.matcher(header).find()) return null;
            process = "kernel";
        } else if (tag.equals("SYSTEM_TOMBSTONE")) {
            if (!NATIVE_SIGNAL.matcher(header).find()) return null;
            Matcher nativeProcess = NATIVE_PROCESS.matcher(header);
            if (!nativeProcess.find()) return null;
            process = nativeProcess.group(1);
        } else if (tag.startsWith("system_server_")) {
            process = "system_server";
        } else if (tag.startsWith("system_app_") || tag.startsWith("data_app_")) {
            Matcher m = PROCESS.matcher(header);
            if (m.find()) process = m.group(1);
            else {
                m = ANR.matcher(header);
                if (!m.find()) {
                    m = NATIVE_PROCESS.matcher(header);
                    if (!m.find() || !NATIVE_SIGNAL.matcher(header).find()) return null;
                }
                process = m.group(1);
            }
        } else return null;
        String role = criticalRole(process, roleHolders);
        return role == null ? null
                : new Event(role, process, type, Long.toString(timestamp), "dropbox");
    }

    /**
     * Only already-recognized, process-attributed critical events from a
     * Shizuku ADB crash-buffer probe. A restricted buffer is NOT a complete
     * Android crash history. No raw system logs cross the Binder boundary.
     */
    public static Summary fromShizukuCrashBuffer(String wire) {
        GelShizukuEvidence.CrashScan scan = GelShizukuEvidence.parseCrash(wire);
        if (!scan.read) return null;
        List<Event> observed = new ArrayList<>();
        for (GelShizukuEvidence.CrashEvent event : scan.events) {
            String role = criticalRole(event.process, Collections.emptySet());
            if (role == null) continue;
            observed.add(new Event(role, event.process, event.type, event.when,
                    "Shizuku ADB crash buffer"));
        }
        return new Summary(unique(observed), true,
                "Shizuku ADB crash buffer — sampled history only");
    }

    public static Summary fromEvents(List<Event> events, String source) {
        return new Summary(unique(events), true, source);
    }

    /**
     * Parses a user-selected .txt or .zip bugreport locally.
     * ZIP is recognized by magic bytes, not by an untrusted filename/MIME type.
     * Large or malformed archives fail closed rather than yielding a healthy zero.
     */
    public static Summary parseUserReport(InputStream raw, Set<String> roleHolders)
            throws IOException {
        if (raw == null) throw new IOException("No selected report");
        PushbackInputStream input = new PushbackInputStream(raw, 4);
        byte[] magic = new byte[4];
        int n = input.read(magic);
        if (n > 0) input.unread(magic, 0, n);
        if (n >= 4 && magic[0] == 'P' && magic[1] == 'K' && magic[2] == 3 && magic[3] == 4) {
            ZipInputStream zip = new ZipInputStream(input, StandardCharsets.UTF_8);
            int count = 0;
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (++count > MAX_ZIP_ENTRIES) throw new IOException("Too many ZIP entries");
                String name = entry.getName().toLowerCase(Locale.US);
                // No extraction to disk. Read exactly the primary Android report document.
                if (!entry.isDirectory() && name.matches("(?:.*/)?bugreport-[^/]*\\.txt")) {
                    return parseText(zip, roleHolders, "selected Android bugreport ZIP");
                }
                zip.closeEntry();
            }
            throw new IOException("No bugreport TXT in selected ZIP");
        }
        return parseText(input, roleHolders, "selected text report");
    }

    private static Summary parseText(InputStream stream, Set<String> roleHolders,
                                     String source) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8), 8192);
        List<Event> events = new ArrayList<>();
        String line;
        String pendingCrashWhen = "";
        int remainingProcessLines = 0;
        String nativeCandidate = "";
        String nativeWhen = "";
        int remainingNativeLines = 0;
        int totalChars = 0;
        int totalLines = 0;
        while ((line = reader.readLine()) != null) {
            totalChars += line.length() + 1;
            if (++totalLines > MAX_REPORT_LINES || totalChars > MAX_REPORT_CHARS)
                throw new IOException("Report exceeds local safe parsing limit");
            if (line.contains("FATAL EXCEPTION:")) {
                remainingProcessLines = 8;
                pendingCrashWhen = timeFrom(line);
            } else if (remainingProcessLines > 0) {
                Matcher process = PROCESS.matcher(line);
                if (process.find()) {
                    add(events, process.group(1), "CRASH",
                            pendingCrashWhen.isEmpty() ? timeFrom(line) : pendingCrashWhen,
                            source, roleHolders);
                    remainingProcessLines = 0;
                } else remainingProcessLines--;
            }
            Matcher anr = ANR.matcher(line);
            if (anr.find()) {
                add(events, anr.group(1), "ANR", timeFrom(line), source, roleHolders);
            }

            // Separate observed native crashes from normal process restarts.
            Matcher tombstoneProcess = NATIVE_PROCESS.matcher(line);
            if (tombstoneProcess.find()) {
                nativeCandidate = tombstoneProcess.group(1);
                nativeWhen = timeFrom(line);
                remainingNativeLines = 12;
            } else if (remainingNativeLines > 0) remainingNativeLines--;

            if (remainingNativeLines > 0 && NATIVE_SIGNAL.matcher(line).find()) {
                add(events, nativeCandidate, "NATIVE_CRASH",
                        nativeWhen.isEmpty() ? timeFrom(line) : nativeWhen,
                        source, roleHolders);
                remainingNativeLines = 0;
            }
            // Highly specific system-server watchdog escalation. "watchdog"
            // alone, or one application appearing frozen, is not sufficient.
            if (WATCHDOG_KILL.matcher(line).find()) {
                add(events, "system_server", "WATCHDOG", timeFrom(line), source, roleHolders);
            }
            if (KERNEL_PANIC.matcher(line).find()) {
                add(events, "kernel", "KERNEL_PANIC", timeFrom(line), source, roleHolders);
            }
        }
        if (totalLines == 0) throw new IOException("Empty report");
        return new Summary(unique(events), true, source);
    }

    private static void add(List<Event> events, String process, String kind,
                            String when, String source, Set<String> holders) {
        if (events.size() >= MAX_EVENTS) return;
        String role = criticalRole(process, holders);
        if (role != null) events.add(new Event(role, process, kind, when, source));
    }

    /**
     * Normalized month/day/clock time for conservative record de-duplication.
     * Diagnostic reports may omit the year or supply DropBox epoch millis.
     * We do not infer an absolute event year from yearless Android logcat.
     * Events on the same month/day/clock from different years may be merged
     * conservatively rather than manufacturing repeat crashes.
     */
    private static long eventSecondOfYear(String when) {
        if (when == null || when.trim().isEmpty()) return -1L;
        try {
            String value = when.trim();
            if (value.matches("\\d{12,17}")) {
                java.text.SimpleDateFormat format =
                        new java.text.SimpleDateFormat("MM-dd HH:mm:ss", Locale.US);
                value = format.format(new java.util.Date(Long.parseLong(value)));
            } else if (value.matches("\\d{4}-\\d\\d-\\d\\d[ T].*")) {
                value = value.substring(5).replace('T', ' ');
            }
            if (value.length() < 14) return -1L;
            java.text.SimpleDateFormat formatter =
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            formatter.setLenient(false);
            java.util.Date date = formatter.parse("2000-" + value.substring(0, 14));
            if (date == null) return -1L;
            return date.getTime() / 1000L;
        } catch (RuntimeException | java.text.ParseException ignored) {
            return -1L;
        }
    }

    private static String timeFrom(String line) {
        Matcher m = DATE.matcher(line);
        return m.find() ? m.group(0) : "";
    }

    private static List<Event> unique(List<Event> input) {
        List<Event> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        if (input == null) return result;
        for (Event event : input) {
            if (event != null && seen.add(event.identity())) result.add(event);
            if (result.size() >= MAX_EVENTS) break;
        }
        return result;
    }
}
