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
            if (line.contains("lab 31 —") || line.contains("lab 31 -")) {
                insideSummary = true;
                continue;
            }
            if (insideSummary) {
                if (isOtherLabHeader(line)) {
                    insideSummary = false;
                } else {
                    if (line.contains("lab 31 complete")
                            || line.contains("lab 31 ολοκληρώθηκε")) {
                        insideSummary = false;
                    }
                    continue;
                }
            }
            out.append(row).append('\n');
        }
        return out.toString();
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
            return n >= 1 && n <= 31 && n != 31;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
