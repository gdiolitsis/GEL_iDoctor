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
                if (normal.contains("lab 28: log triage complete")
                        || normal.contains("lab 28: ολοκληρώθηκε η διαλογή")) {
                    insideLab28 = false;
                }
                continue;
            }
            out.append(row).append('\n');
        }
        return out.toString();
    }
}
