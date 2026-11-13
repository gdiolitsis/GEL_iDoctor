package com.gel.cleaner;

import java.util.Locale;

/**
 * Log-mention triage must not learn symptoms from its own previous output.
 * This preserves other Labs' raw records; it does not validate their claims.
 */
public final class Lab28LogEvidencePolicy {
    private Lab28LogEvidencePolicy() {}

    public static String withoutLab28Reports(String original) {
        if (original == null || original.isEmpty()) return "";
        StringBuilder out = new StringBuilder(original.length());
        boolean insideLab28 = false;
        for (String row : original.split("\\r?\\n", -1)) {
            String normal = row.toLowerCase(Locale.US);
            if (normal.contains("lab 28 —") || normal.contains("lab 28 - hardware stability")
                    || normal.contains("lab 28 - σταθερότητα")) {
                insideLab28 = true;
                continue;
            }
            if (insideLab28) {
                if (isOtherLabHeader(normal)) {
                    // Previous Lab 28 may have been interrupted before its footer.
                    // Never discard a later Lab's evidence merely because of that.
                    insideLab28 = false;
                } else {
                    if (normal.contains("lab 28: log triage complete")
                            || normal.contains("lab 28: ολοκληρώθηκε η διαλογή")) {
                        insideLab28 = false;
                    }
                    continue;
                }
            }
            out.append(row).append('\n');
        }
        return out.toString();
    }

    private static boolean isOtherLabHeader(String raw) {
        String line = raw.trim();
        if (line.startsWith("ℹ")) line = line.substring(1).trim();
        if (!line.startsWith("lab ")) return false;
        int dash = line.indexOf('—', 4);
        if (dash < 0) dash = line.indexOf('-', 4);
        if (dash < 0) return false;
        try {
            int lab = Integer.parseInt(line.substring(4, dash).trim());
            return lab >= 1 && lab <= 31 && lab != 28;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
