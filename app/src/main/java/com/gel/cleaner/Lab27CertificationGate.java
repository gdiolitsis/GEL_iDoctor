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
 * A completed run counts as executed even with adverse findings; those
 * confirmed functional failures may downgrade certification; informational\n * observations do not automatically lower its score.
 * Human-verified physical connections are a separate gate.
 * The certificate concerns tested functionality, NOT parts authenticity.
 * OEM specification comparison stays a required technical cross-check.
 * OEM matches do not establish that replacement parts are genuine.
 */
final class Lab27CertificationGate {
    static final int SCHEMA = 9; // Persist exact grading factors and invalidate certificates without their provenance.
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
            if (time == null || !fresh(time, nowWallMs)) missing.add(id);
        }
        return missing;
    }

    /** Negative completed-lab results are findings, not missing runs. */
    static List<String> withFindings(List<String> applicable, java.util.Map<String, Long> times,
                                     java.util.Map<String, Boolean> passed, long nowWallMs) {
        List<String> findings = new ArrayList<>();
        for (String id : applicable) {
            Long time = times.get(id);
            if (time != null && fresh(time, nowWallMs)
                    && Boolean.FALSE.equals(passed.get(id))) findings.add(id);
        }
        return findings;
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
    static boolean physicalChecksComplete(boolean usbVisuallyPresent, boolean usbVisuallyAbsent,
            boolean jackVisuallyPresent, boolean jackVisuallyAbsent,
            boolean simTrayVisuallyPresent, boolean simTrayVisuallyAbsent) {
        return (usbVisuallyPresent != usbVisuallyAbsent)
                && (jackVisuallyPresent != jackVisuallyAbsent)
                && (simTrayVisuallyPresent != simTrayVisuallyAbsent);
    }

    /** Manufacturer-sourced physical structure, independent of Android SIM/eSIM flags. */
    enum Presence { PRESENT, ABSENT, UNKNOWN }

    /** Only features expressly documented by this exact-model OEM get a question. */
    static boolean shouldAsk(Presence expected) {
        return expected == Presence.PRESENT || expected == Presence.ABSENT;
    }

    /** FM app visibility is NOT a test of an FM receiver or reception quality. */
    static boolean fmAppQuestionComplete(Presence expected,
            boolean appPresent, boolean appAbsent) {
        return shouldAsk(expected)
                ? (appPresent != appAbsent)
                : (!appPresent && !appAbsent);
    }

    /** Unknown OEM information means NOT ASKED, never presumed absent. */
    private static boolean answeredIfSpecified(Presence expected,
            boolean declaredPresent, boolean declaredAbsent) {
        return shouldAsk(expected)
                ? (declaredPresent != declaredAbsent)
                : (!declaredPresent && !declaredAbsent);
    }

    static boolean physicalChecksComplete(Presence usb, Presence headset, Presence sim,
            boolean usbPresent, boolean usbAbsent, boolean jackPresent, boolean jackAbsent,
            boolean simPresent, boolean simAbsent) {
        return (shouldAsk(usb) || shouldAsk(headset) || shouldAsk(sim))
                && answeredIfSpecified(usb, usbPresent, usbAbsent)
                && answeredIfSpecified(headset, jackPresent, jackAbsent)
                && answeredIfSpecified(sim, simPresent, simAbsent);
    }

    /** OEM presence and absence are equally supported; unknown is skipped. */
    private static boolean matchesOem(Presence expected,
            boolean confirmedPresent, boolean confirmedAbsent) {
        if (expected == Presence.PRESENT)
            return confirmedPresent && !confirmedAbsent;
        if (expected == Presence.ABSENT)
            return confirmedAbsent && !confirmedPresent;
        return !confirmedPresent && !confirmedAbsent;
    }

    static boolean physicalMatchesOem(Presence expectedUsb, Presence expectedHeadset,
            Presence expectedSim, boolean usbVisuallyPresent, boolean usbVisuallyAbsent,
            boolean jackVisuallyPresent, boolean jackVisuallyAbsent,
            boolean simTrayVisuallyPresent, boolean simTrayVisuallyAbsent) {
        return (shouldAsk(expectedUsb) || shouldAsk(expectedHeadset) || shouldAsk(expectedSim))
                && physicalChecksComplete(expectedUsb, expectedHeadset, expectedSim,
                    usbVisuallyPresent, usbVisuallyAbsent, jackVisuallyPresent,
                    jackVisuallyAbsent, simTrayVisuallyPresent, simTrayVisuallyAbsent)
                && matchesOem(expectedUsb, usbVisuallyPresent, usbVisuallyAbsent)
                && matchesOem(expectedHeadset, jackVisuallyPresent, jackVisuallyAbsent)
                && matchesOem(expectedSim, simTrayVisuallyPresent, simTrayVisuallyAbsent);
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
