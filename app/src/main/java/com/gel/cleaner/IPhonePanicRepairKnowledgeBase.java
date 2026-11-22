package com.gel.cleaner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Provenance-preserving, OFFLINE catalog of published iPhone panic repair outcomes.
 *
 * Not an Apple repair manual, hardware fault classifier, probability model or
 * crowdsourced success-rate estimator. No user panic logs or personal data are
 * recorded. Knowledge-base entries require a matching model AND a matching code.
 */
public final class IPhonePanicRepairKnowledgeBase {
    public static final String ASSET = "iphone_panic_repair_knowledge_v1.tsv";

    private IPhonePanicRepairKnowledgeBase() {}

    public enum Outcome {
        SELF_REPORTED_SUCCESS,
        SELF_REPORTED_FAILURE,
        PARTIAL_OR_RECURRING,
        UNRESOLVED
    }

    public static final class RepairCase {
        public final String id;
        /** Multiple attempts within one incident share the same incidentId. */
        public final String incidentId;
        public final String modelIdentifiers;
        public final String panicCode;
        public final String actionKey;
        public final Outcome outcome;
        public final String reportedDate;
        public final String sourceUrl;
        public final String actionGreek;
        public final String actionEnglish;
        public final String noteGreek;
        public final String noteEnglish;

        private RepairCase(String[] columns) {
            id = columns[0];
            incidentId = columns[1];
            modelIdentifiers = columns[2];
            panicCode = canonicalCode(columns[3]);
            actionKey = columns[4];
            outcome = Outcome.valueOf(columns[5]);
            reportedDate = columns[6];
            sourceUrl = columns[7];
            actionGreek = columns[8];
            actionEnglish = columns[9];
            noteGreek = columns[10];
            noteEnglish = columns[11];
        }

        boolean matches(String model, String code) {
            if (model == null || model.trim().isEmpty()
                    || code == null || code.trim().isEmpty()) return false;
            if (!panicCode.equals(canonicalCode(code))) return false;
            for (String identifier : modelIdentifiers.split(";")) {
                if (identifier.trim().equalsIgnoreCase(model.trim())) return true;
            }
            return false;
        }

        public String localizedAction(boolean greek) {
            return greek ? actionGreek : actionEnglish;
        }

        public String localizedNote(boolean greek) {
            return greek ? noteGreek : noteEnglish;
        }

        public String localizedOutcome(boolean greek) {
            switch (outcome) {
                case SELF_REPORTED_SUCCESS:
                    return greek ? "Αναφέρθηκε επιτυχία" : "Success reported";
                case SELF_REPORTED_FAILURE:
                    return greek ? "Αναφέρθηκε αποτυχία" : "Failure reported";
                case PARTIAL_OR_RECURRING:
                    return greek ? "Μερική βελτίωση / επανεμφάνιση panic"
                            : "Partial improvement / panic recurred";
                default:
                    return greek ? "Άγνωστη τελική έκβαση" : "Outcome unresolved";
            }
        }
    }

    /**
     * Throws on duplicate case IDs, unsupported outcome or malformed source.
     * The TSV is bundled and curated, never downloaded or modified at runtime.
     */
    public static List<RepairCase> parse(InputStream source) throws IOException {
        if (source == null) throw new IOException("Missing offline repair catalog");
        List<RepairCase> cases = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(source, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty() || line.startsWith("#")
                        || line.startsWith("case_id\t")) continue;
                String[] c = line.split("\t", -1);
                if (c.length != 12) {
                    throw new IOException("Invalid repair case columns on line " + lineNumber);
                }
                if (c[0].trim().isEmpty() || c[1].trim().isEmpty()
                        || c[2].trim().isEmpty() || c[3].trim().isEmpty()
                        || c[8].trim().isEmpty() || c[9].trim().isEmpty()
                        || c[10].trim().isEmpty() || c[11].trim().isEmpty()
                        || !isAcceptedPublishedRepairSource(c[7])
                        || !c[4].matches("[A-Z][A-Z0-9_]{2,80}")
                        || !c[6].matches("(?:unknown|[0-9]{4}-[0-9]{2}-[0-9]{2})")
                        || canonicalCode(c[3]).isEmpty()) {
                    throw new IOException("Incomplete repair evidence on line " + lineNumber);
                }
                if (!ids.add(c[0])) {
                    throw new IOException("Duplicate repair case id on line " + lineNumber);
                }
                for (String model : c[2].split(";", -1)) {
                    if (!model.trim().matches("(?i)iPhone[0-9]{1,2},[0-9]{1,2}"))
                        throw new IOException("Invalid exact iPhone model ID at line " + lineNumber);
                }
                try {
                    cases.add(new RepairCase(c));
                } catch (RuntimeException ex) {
                    throw new IOException("Invalid outcome/code on line " + lineNumber, ex);
                }
            }
        }
        return Collections.unmodifiableList(cases);
    }

    /**
     * The public case catalog is manually curated. Accept only exact approved
     * original repair-case domains and HTTPS; never accept spoofed subdomains
     * such as ifixit.com.evil.example or an arbitrary pasted source URL.
     * A new provider requires a deliberate source review and code change.
     */
    private static boolean isAcceptedPublishedRepairSource(String value) {
        try {
            URI uri = new URI(value);
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null
                    || uri.getUserInfo() != null || uri.getFragment() != null)
                return false;
            String host = uri.getHost().toLowerCase(Locale.ROOT);
            boolean ifixit = ("www.ifixit.com".equals(host) || "ifixit.com".equals(host))
                    && uri.getPath() != null
                    && uri.getPath().matches("/Answers/View/[0-9]+/?");
            boolean nopowerfix = "forum.nopowerfix.com".equals(host)
                    && uri.getPath() != null && uri.getPath().startsWith("/t/");
            return ifixit || nopowerfix;
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    /** Do not fall back to codes from other models, even from the same generation. */
    public static List<RepairCase> lookup(List<RepairCase> all, String model, String code) {
        if (all == null || model == null || code == null) return Collections.emptyList();
        List<RepairCase> matches = new ArrayList<>();
        for (RepairCase item : all) if (item.matches(model, code)) matches.add(item);
        return Collections.unmodifiableList(matches);
    }

    /**
     * Summarizes DISTINCT sourced attempts, not a repair success rate.
     * Repeated imported panic logs cannot multiply the same case.
     */
    public static final class MatchSummary {
        public final int attempts;
        public final int caseThreads;
        public final int reportedSuccesses;
        public final int reportedFailures;
        public final int partialOrRecurring;
        public final int unresolved;

        MatchSummary(int attempts, int caseThreads, int successes, int failures,
                     int partial, int unresolved) {
            this.attempts = attempts;
            this.caseThreads = caseThreads;
            this.reportedSuccesses = successes;
            this.reportedFailures = failures;
            this.partialOrRecurring = partial;
            this.unresolved = unresolved;
        }

        public String display(boolean greek) {
            if (greek) {
                return attempts + " δημοσιευμένες προσπάθειες σε " + caseThreads
                        + " περιστατικά: " + reportedSuccesses + " αναφερόμενες επιτυχίες, "
                        + reportedFailures + " αποτυχίες, " + partialOrRecurring
                        + " μερικές αποκαταστάσεις και " + unresolved
                        + " χωρίς τελικό αποτέλεσμα.";
            }
            return attempts + " published repair attempts in " + caseThreads
                    + " case threads: " + reportedSuccesses + " reported successes, "
                    + reportedFailures + " failures, " + partialOrRecurring
                    + " partial recoveries and " + unresolved + " unresolved.";
        }
    }

    public static MatchSummary summarize(Iterable<RepairCase> matches) {
        if (matches == null) return new MatchSummary(0, 0, 0, 0, 0, 0);
        Set<String> attemptIds = new HashSet<>();
        Set<String> incidentIds = new HashSet<>();
        int success = 0, failed = 0, partial = 0, unresolved = 0;
        for (RepairCase item : matches) {
            if (item == null || !attemptIds.add(item.id)) continue;
            incidentIds.add(item.incidentId);
            switch (item.outcome) {
                case SELF_REPORTED_SUCCESS: success++; break;
                case SELF_REPORTED_FAILURE: failed++; break;
                case PARTIAL_OR_RECURRING: partial++; break;
                default: unresolved++; break;
            }
        }
        return new MatchSummary(attemptIds.size(), incidentIds.size(),
                success, failed, partial, unresolved);
    }

    /** SMC entries use hex irrespective of decimal/hex input formatting. */
    public static String canonicalCode(String raw) {
        if (raw == null) return "";
        String code = raw.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
        if (code.startsWith("SMC ")) {
            String number = code.substring(4).trim();
            try {
                long value = number.startsWith("0X")
                        ? Long.parseLong(number.substring(2), 16)
                        : Long.parseLong(number, 10);
                if (value >= 0 && value <= 0xFFFFFFFFL) {
                    return "SMC 0x" + Long.toHexString(value).toUpperCase(Locale.ROOT);
                }
            } catch (NumberFormatException ignored) {
                // Leave uninterpreted: unknown codes should never match at random.
            }
        }
        return code;
    }
}
