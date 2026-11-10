package com.gel.cleaner;

/**
 * Only recognizes explicit structured SUMMARY status values from diagnostics.
 * Arbitrary prose (including "no warnings") is never interpreted as a PASS.
 * This is a coverage inventory, not a device-health score.
 */
public final class Lab31StructuredEvidence {
    public static final class Result {
        public final int records;
        public final int observed;
        public final int missing;

        private Result(int records, int observed, int missing) {
            this.records = records;
            this.observed = observed;
            this.missing = missing;
        }
    }

    private Lab31StructuredEvidence() {}

    public static Result summarize(String log) {
        if (log == null || log.trim().isEmpty()) return new Result(0, 0, 0);
        int records = 0, observed = 0, missing = 0;
        for (String line : log.split("\\r?\\n")) {
            int start = line.indexOf("SUMMARY:");
            if (start < 0) continue;
            String payload = line.substring(start + 8);
            for (String pair : payload.split(";")) {
                String part = pair.trim();
                int sep = part.indexOf('=');
                if (sep <= 0 || sep == part.length() - 1) continue;
                String field = part.substring(0, sep).trim();
                String value = part.substring(sep + 1).trim();
                if (!field.matches("[A-Z][A-Z0-9_]{2,}")) continue;
                if ("OBSERVED".equals(value)) {
                    records++;
                    observed++;
                } else if ("UNKNOWN".equals(value)
                        || "INCONCLUSIVE".equals(value)
                        || "UNAVAILABLE".equals(value)
                        || "NOT_AVAILABLE".equals(value)
                        || "NOT_TESTED".equals(value)
                        || "UNVERIFIED".equals(value)) {
                    records++;
                    missing++;
                }
            }
        }
        return new Result(records, observed, missing);
    }
}
