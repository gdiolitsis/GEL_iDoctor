package com.gel.cleaner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The final technician summary reports ONLY actual warning/error evidence
 * already emitted by diagnostic labs. It does not add independent diagnoses,
 * infer hardware failures or recast missing measurements as faults.
 *
 * Input must be the historical log AFTER Lab31ReportEvidencePolicy has
 * removed previous final-summary blocks, preventing recursive alerts.
 */
public final class Lab29TechnicianAlerts {
    private Lab29TechnicianAlerts() {}

    public enum Severity { WARNING, ERROR }

    public static final class Finding {
        public final Severity severity;
        public final String sourceLab;
        public final String message;

        private Finding(Severity severity, String sourceLab, String message) {
            this.severity = severity;
            this.sourceLab = sourceLab;
            this.message = message;
        }
    }

    private static final Pattern LAB_TITLE = Pattern.compile(
            "^(?:ℹ\\s*)?LAB\\s+([1-9]|[12][0-9]|3[01])\\s*[—-]",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    public static List<Finding> collect(String priorLabLog) {
        if (priorLabLog == null || priorLabLog.trim().isEmpty())
            return Collections.emptyList();

        List<Finding> findings = new ArrayList<>();
        String sourceLab = "";
        for (String row : priorLabLog.split("\\r?\\n")) {
            if (row == null) continue;
            String text = row.trim();

            Matcher title = LAB_TITLE.matcher(text);
            if (title.find()) {
                sourceLab = "Lab " + title.group(1);
                continue;
            }

            Severity severity;
            if (text.startsWith("✖")) {
                severity = Severity.ERROR;
            } else if (text.startsWith("⚠")) {
                severity = Severity.WARNING;
            } else {
                continue; // Green, white, raw metrics and explanatory text are NOT alerts.
            }

            String message = text.substring(1).replaceFirst("^[\\s\\uFE0F]+", "").trim();
            if (message.isEmpty()) continue;
            findings.add(new Finding(severity, sourceLab, message));
        }
        return Collections.unmodifiableList(findings);
    }

    public static int count(List<Finding> findings, Severity severity) {
        if (findings == null) return 0;
        int count = 0;
        for (Finding finding : findings) {
            if (finding != null && finding.severity == severity) count++;
        }
        return count;
    }
}
