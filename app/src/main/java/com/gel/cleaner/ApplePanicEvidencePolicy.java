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


    /**
     * Field-level evidence from ONE structurally eligible panic record.
     * These fields are historical log metadata, never live iPhone measurements.
     * In particular, a date is comparable only with an explicit UTC offset.
     */
    public static final class RecordMetadata {
        public final String timestamp;
        public final long timestampEpochMillis;
        public final String osVersion;
        public final String process;
        public final String modelIdentifier;
        public final String signatureKey;

        RecordMetadata(String timestamp, long epoch, String osVersion,
                       String process, String modelIdentifier, String signatureKey) {
            this.timestamp = timestamp;
            this.timestampEpochMillis = epoch;
            this.osVersion = osVersion;
            this.process = process;
            this.modelIdentifier = modelIdentifier;
            this.signatureKey = signatureKey;
        }
    }

    public static RecordMetadata describeOne(String block) {
        if (!hasPanicEvidence(block)) {
            return new RecordMetadata("", -1L, "", "", "", "");
        }
        String stamp = firstMatch(block,
                "(?i)[\"']?timestamp[\"']?\\s*:\\s*[\"']([^\"'\\r\\n]{10,45})[\"']",
                "(?im)^\\s*Date/Time\\s*:\\s*([^\\r\\n]{10,45})");
        long epoch = parseExplicitTimestamp(stamp);
        String os = firstMatch(block,
                "(?i)[\"']?os_version[\"']?\\s*:\\s*[\"']([^\"'\\r\\n]{3,100})[\"']",
                "(?im)^\\s*OS version\\s*:\\s*([^\\r\\n]{3,100})");
        String process = firstMatch(block,
                "(?im)^\\s*Panicked task\\s*:\\s*([^\\r\\n]{1,100})",
                "(?i)[\"']?process[\"']?\\s*:\\s*[\"']([^\"'\\r\\n]{1,100})[\"']");
        String model = firstMatch(block,
                "(?i)\\b(iPhone\\d{1,2},\\d{1,2})\\b");
        // Only the actual panicString or panic(cpu...) line is a signature.
        // Never include unrelated JSON fields or concatenate other events.
        String panic = firstMatch(block,
                "(?i)[\"']?panicString[\"']?\\s*:\\s*[\"']([^\"'\\r\\n]{8,500})[\"']",
                "(?im)^\\s*(panic\\s*\\(cpu[^\\r\\n]{8,500})");
        String signature = normalizePanicSignature(panic);
        return new RecordMetadata(stamp, epoch, os, process, model, signature);
    }

    private static String normalizePanicSignature(String panic) {
        if (panic == null || panic.trim().isEmpty()) return "";
        // Omit CPU index, caller address and stack addresses that vary by run.
        String value = panic.replaceFirst("(?i)^.*?\\):\\s*", "");
        if (value.equals(panic)) value = panic;
        value = value.replace("\\n", " ").replace("\\r", " ");
        value = value.replaceAll("(?i)0x[0-9a-f]{7,}", "[address]");
        value = value.replaceAll("\\s+", " ").trim().toLowerCase(Locale.ROOT);
        return value.length() > 160 ? value.substring(0, 160) : value;
    }

    private static String firstMatch(String text, String... regexes) {
        if (text == null) return "";
        for (String pattern : regexes) {
            java.util.regex.Matcher m = Pattern.compile(pattern).matcher(text);
            if (m.find()) return m.group(1).trim();
        }
        return "";
    }

    private static long parseExplicitTimestamp(String value) {
        if (value == null || value.trim().isEmpty()) return -1L;
        // Do not compare timezone-less timestamps or fabricate chronology.
        String[] formats = {"yyyy-MM-dd HH:mm:ss.SS Z",
                            "yyyy-MM-dd HH:mm:ss.SSS Z",
                            "yyyy-MM-dd HH:mm:ss Z"};
        for (String format : formats) {
            try {
                java.text.SimpleDateFormat parser =
                        new java.text.SimpleDateFormat(format, Locale.US);
                parser.setLenient(false);
                java.text.ParsePosition pos = new java.text.ParsePosition(0);
                java.util.Date d = parser.parse(value.trim(), pos);
                if (d != null && pos.getIndex() == value.trim().length()) {
                    return d.getTime();
                }
            } catch (RuntimeException ignored) { }
        }
        return -1L;
    }

    public static String limitation(boolean gr) {
        return gr
                ? "Μόνο εισαγόμενα αρχεία: δεν μετρήθηκε η συσκευή, δεν επαληθεύτηκε η αιτία ή η κατάσταση του hardware."
                : "Imported files only: device condition and hardware cause have not been independently verified.";
    }
}
