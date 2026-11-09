package com.gel.cleaner;

import java.util.Locale;
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
