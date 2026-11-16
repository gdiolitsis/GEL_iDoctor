package com.gel.cleaner;

import java.util.Locale;

/** A final summary must not cite its own earlier summary as new findings. */
public final class Lab31ReportEvidencePolicy {
    private Lab31ReportEvidencePolicy() {}

    public static String withoutPriorSummaries(String original) {
        if (original == null || original.isEmpty()) return "";
        StringBuilder out = new StringBuilder(original.length());
        boolean insideSummary = false;
        for (String row : original.split("\\r?\\n", -1)) {
            String line = row.toLowerCase(Locale.US).trim();
            // New report is Lab 29. Prior builds may contain Lab 30 / Lab 31.
            // Identify final report by its distinctive title, NOT number alone:
            // Lab 29 was formerly also the device-certification lab.
            if (isPriorReportHeader(line)) {
                insideSummary = true;
                continue;
            }
            if (insideSummary) {
                if (isOtherLabHeader(line)) {
                    insideSummary = false;
                } else {
                    if (line.contains("lab 31 complete")
                            || line.contains("lab 31 ολοκληρώθηκε")
                            || line.contains("lab 30 complete")
                            || line.contains("lab 30 ολοκληρώθηκε")
                            || line.contains("lab 29 complete")
                            || line.contains("lab 29 ολοκληρώθηκε")) {
                        insideSummary = false;
                    }
                    continue;
                }
            }
            out.append(row).append('\n');
        }
        return out.toString();
    }

    private static boolean isPriorReportHeader(String line) {
        boolean legacy31 = line.contains("lab 31 —") || line.contains("lab 31 -");
        boolean final29or30 = line.contains("lab 29 —") || line.contains("lab 29 -")
                || line.contains("lab 30 —") || line.contains("lab 30 -");
        return legacy31 || (final29or30 && (
                line.contains("τελικη συνοψη υπαρχοντων τεκμηριων")
                || line.contains("τελική σύνοψη υπαρχόντων τεκμηρίων")
                || line.contains("final existing-evidence summary")
                || line.contains("τελικη τεχνικη αναφορα")
                || line.contains("τελική τεχνική αναφορά")
                || line.contains("final technical report")
        ));
    }

    private static boolean isOtherLabHeader(String raw) {
        String line = raw;
        if (line.startsWith("ℹ")) line = line.substring(1).trim();
        if (!line.startsWith("lab ")) return false;
        int dash = line.indexOf('—', 4);
        if (dash < 0) dash = line.indexOf('-', 4);
        if (dash < 0) return false;
        try {
            int n = Integer.parseInt(line.substring(4, dash).trim());
            return n >= 1 && n <= 31 && !isPriorReportHeader(line);
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
