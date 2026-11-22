package com.gel.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GEL Device Certification: completion is not equivalent to passing.
 * A current, warning-free recorded run is required for every applicable
 * basic lab. Human-verified physical connections are a separate gate.
 * The certificate concerns tested functionality, NOT parts authenticity.
 * OEM specification comparison stays a required technical cross-check.
 * OEM matches do not establish that replacement parts are genuine.
 */
final class Lab27CertificationGate {
    static final int SCHEMA = 2; // Invalidate old certificates lacking physical-absence evidence.
    static final long MAX_AGE_MS = 7L * 24L * 60L * 60L * 1000L;

    static final List<String> REQUIRED = Collections.unmodifiableList(Arrays.asList(
            "1", "2", "3", "4", "5", "6", "7", "8",
            "10", "11", "12", "13", "14A", "15", "16", "18", "19", "25"));

    private static final Pattern HEADER = Pattern.compile(
            "^\\s*LAB\\s+(14A|[1-9]|1[0-9]|2[0-9])\\s*[—–-]",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern COMPLETION = Pattern.compile(
            "^\\s*(?:Το\\s+)?LAB\\s+(14A|[1-9]|1[0-9]|2[0-9])"
            + "(?:\\s+BASE)?\\s+(ολοκληρώθηκε|finished|completed)\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private Lab27CertificationGate() { }

    static String labFromHeader(String message) {
        if (message == null) return null;
        Matcher m = HEADER.matcher(message);
        if (!m.find()) return null;
        String key = m.group(1).toUpperCase(Locale.ROOT);
        if ("14".equals(key)) key = "14A"; // The stress test uses "LAB 14" in its heading.
        return REQUIRED.contains(key) ? key : null;
    }

    static String completedLab(String message) {
        if (message == null) return null;
        String lower = message.toLowerCase(Locale.ROOT);
        if (lower.contains("δεν ολοκληρώ") || lower.contains("μερικ")
                || lower.contains("partially") || lower.contains("incomplete")
                || lower.contains("not completed")) return null;
        Matcher m = COMPLETION.matcher(message);
        if (!m.find()) return null;
        String key = m.group(1).toUpperCase(Locale.ROOT);
        return REQUIRED.contains(key) ? key : null;
    }

    static boolean fresh(long recordedWallMs, long nowWallMs) {
        return recordedWallMs > 0 && nowWallMs >= recordedWallMs
                && nowWallMs - recordedWallMs <= MAX_AGE_MS;
    }

    /** Only the applicable labs can be "not applicable"; skipped labs never count as passed. */
    static List<String> pending(List<String> applicable, java.util.Map<String, Long> times,
                                java.util.Map<String, Boolean> passed, long nowWallMs) {
        List<String> missing = new ArrayList<>();
        for (String id : applicable) {
            Long time = times.get(id);
            if (time == null || !fresh(time, nowWallMs)
                    || !Boolean.TRUE.equals(passed.get(id))) missing.add(id);
        }
        return missing;
    }

    /** All three evidence pillars remain required: tests, physical checks, and OEM. */
    static boolean eligible(int successful, int applicable, boolean physicalConfirmed,
                            boolean oemProfileKnown, int oemMatches, int oemDeviations,
                            boolean criticalHistory, String grade) {
        return applicable > 0 && successful == applicable && physicalConfirmed
                && oemProfileKnown && oemMatches > 0 && oemDeviations == 0
                && !criticalHistory && grade != null && !grade.isEmpty();
    }

    /** Physical absence must be explicitly acknowledged, not presumed. */
    static boolean physicalChecksComplete(boolean chargingPortTested,
            boolean wiredHeadsetTested, boolean wiredHeadsetAbsent,
            boolean simTrayTested, boolean simTrayAbsent) {
        return chargingPortTested
                && (wiredHeadsetTested != wiredHeadsetAbsent)
                && (simTrayTested != simTrayAbsent);
    }

    static Set<String> applicable(boolean telephony, boolean bluetooth, boolean cameras,
                                  boolean microphone) {
        Set<String> available = new LinkedHashSet<>(REQUIRED);
        if (!telephony) { available.remove("11"); available.remove("12"); }
        if (!bluetooth) available.remove("13");
        if (!cameras) available.remove("8");
        if (!microphone) available.remove("4");
        return available;
    }
}
