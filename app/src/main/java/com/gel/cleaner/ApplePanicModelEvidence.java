package com.gel.cleaner;

import com.gel.cleaner.iphone.iPhoneSpecs;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model identifiers explicitly present in eligible imported panic records.
 *
 * These are file-level assertions, never a live inspection or authentication
 * of the handset. Different files must not be collapsed into a single model.
 */
public final class ApplePanicModelEvidence {
    private ApplePanicModelEvidence() {}

    // Require a model-identifying field; do not mistake unrelated mentions
    // of iPhone identifiers in diagnostic descriptions for source-device data.
    private static final Pattern MODEL_FIELD = Pattern.compile(
            "(?i)\\b(?:hardware\\s+model|deviceModel|modelIdentifier|product_type|product)\\b"
            + "[\\\"']?\\s*[:=]\\s*[\\\"']?\\s*(iPhone\\d{1,2},\\d{1,2})\\b");

    public static final class Result {
        public final int eligibleRecords;
        public final int recordsWithModel;
        public final boolean conflictingFields;
        public final Map<String, Integer> identifiers;

        private Result(int eligibleRecords, int recordsWithModel,
                boolean conflictingFields, Map<String, Integer> identifiers) {
            this.eligibleRecords = eligibleRecords;
            this.recordsWithModel = recordsWithModel;
            this.conflictingFields = conflictingFields;
            this.identifiers = Collections.unmodifiableMap(new LinkedHashMap<>(identifiers));
        }

        public boolean hasConsistentModel() {
            return !conflictingFields && eligibleRecords > 0
                    && recordsWithModel == eligibleRecords
                    && identifiers.size() == 1;
        }
    }

    public static Result analyze(String importedPanicLogs) {
        int eligible = 0;
        int identified = 0;
        boolean conflicting = false;
        Map<String, Integer> counts = new LinkedHashMap<>();
        if (importedPanicLogs == null || importedPanicLogs.trim().isEmpty()) {
            return new Result(0, 0, false, counts);
        }
        for (String block : importedPanicLogs.split("(?m)^===== ZIP FILE:")) {
            if (!ApplePanicEvidencePolicy.hasPanicEvidence(block)) continue;
            eligible++;

            Matcher matcher = MODEL_FIELD.matcher(block);
            Set<String> perRecord = new LinkedHashSet<>();
            while (matcher.find()) {
                String candidate = matcher.group(1);
                // Retain the canonical identifier spelling when a trusted
                // catalogue match exists; otherwise expose only raw code.
                perRecord.add(candidate.substring(0, 6).equalsIgnoreCase("iphone")
                        ? "iPhone" + candidate.substring(6) : candidate);
            }
            if (!perRecord.isEmpty()) identified++;
            if (perRecord.size() > 1) conflicting = true;
            for (String identifier : perRecord) {
                counts.put(identifier, counts.getOrDefault(identifier, 0) + 1);
            }
        }
        return new Result(eligible, identified, conflicting, counts);
    }

    public static String displayName(String identifier) {
        if (identifier == null) return null;
        String marketing = iPhoneSpecs.marketingNameForIdentifier(identifier);
        return marketing == null ? identifier : marketing + " (" + identifier + ")";
    }
}
