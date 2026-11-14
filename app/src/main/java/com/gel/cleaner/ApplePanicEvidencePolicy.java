package com.gel.cleaner;

import java.util.Locale;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Conservative, hardware-agnostic interpretation of imported iOS panic text.
 * Signature keywords are leads, not a validated diagnosis or repair directive.
 */
public final class ApplePanicEvidencePolicy {
    private ApplePanicEvidencePolicy() {}

    private static final Pattern BUG_TYPE_210 = Pattern.compile(
            "(?i)[\\\"']?bug_type[\\\"']?\\s*[:=]\\s*[\\\"']?210\\b");

    public static boolean hasPanicEvidence(String text) {
        if (text == null || text.trim().length() < 32) return false;
        String low = text.toLowerCase(Locale.US);
        return low.contains("panic(cpu")
                || low.contains("panicstring")
                || BUG_TYPE_210.matcher(text).find();
    }

    /**
     * A multi-file archive is not one panic incident. Every keyword required by
     * a signature must occur within the SAME structurally eligible panic block.
     * Distinct imported files must never contribute complementary tokens.
     */
    public static boolean tokensMatchOnePanic(String importedText, List<String> tokens) {
        if (importedText == null || tokens == null || tokens.isEmpty()) return false;
        String[] blocks = importedText.split("(?m)^===== ZIP FILE:");
        for (String block : blocks) {
            if (!hasPanicEvidence(block)) continue;
            String low = block.toLowerCase(Locale.ROOT);
            boolean matched = true;
            boolean any = false;
            for (String token : tokens) {
                if (token == null || token.trim().isEmpty()) continue;
                any = true;
                if (!low.contains(token.toLowerCase(Locale.ROOT))) {
                    matched = false;
                    break;
                }
            }
            if (any && matched) return true;
        }
        return false;
    }

    /**
     * Only explicit failure wording inside an eligible panic entry is a
     * baseband diagnostic hint. "Baseband version" alone is metadata.
     */
    public static boolean hasExplicitBasebandFaultHint(String importedText) {
        if (importedText == null) return false;
        String[] blocks = importedText.split("(?m)^===== ZIP FILE:");
        for (String block : blocks) {
            if (!hasPanicEvidence(block)) continue;
            String low = block.toLowerCase(Locale.ROOT);
            if (low.contains("baseband panic")
                    || low.contains("baseband crash")
                    || low.contains("baseband watchdog")
                    || low.contains("baseband failure")
                    || low.contains("baseband timeout")
                    || low.contains("modem panic")
                    || low.contains("modem crash")
                    || low.contains("modem failure")
                    || low.contains("modem timeout")
                    || low.contains("commcenter crash")) return true;
        }
        return false;
    }

    public static String evidenceClass(boolean hasPanic) {
        return hasPanic ? "IMPORTED_PANIC_LOG_OBSERVED" : "UNVERIFIED_OR_UNSUPPORTED";
    }

    public static String patternConfidence() {
        // No sensitivity, specificity, base rate, OEM calibration, or model-level validation.
        return "UNVERIFIED";
    }

    public static String possibleCause(String cause) {
        if (cause == null || cause.trim().isEmpty()) return "Unknown cause (pattern only)";
        return "Pattern hypothesis, not confirmed: " + cause.trim();
    }

    public static String recommendation(boolean gr) {
        return gr
                ? "Συσχετίστε την ακριβή υπογραφή, τις ημερομηνίες, την επανάληψη και τα πραγματικά συμπτώματα με τεχνικό. Το panic log μόνο του δεν τεκμηριώνει επισκευή ή αντικατάσταση εξαρτήματος."
                : "Correlate the exact signature, dates, recurrence and real symptoms with a technician. A panic log alone does not justify component repair or replacement.";
    }

    public static String limitation(boolean gr) {
        return gr
                ? "Μόνο εισαγόμενα αρχεία: δεν μετρήθηκε η συσκευή, δεν επαληθεύτηκε η αιτία ή η κατάσταση του hardware."
                : "Imported files only: device condition and hardware cause have not been independently verified.";
    }
}
