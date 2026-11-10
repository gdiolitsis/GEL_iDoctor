package com.gel.cleaner;

/** Recommends only missing legacy Lab 17 core evidence domains; no health score. */
public final class Lab17EvidenceChecklist {
    private Lab17EvidenceChecklist() {}

    public static String missingCoreLabs(boolean lab14Valid, boolean lab15Valid,
                                         boolean lab16Valid) {
        StringBuilder missing = new StringBuilder();
        if (!lab14Valid) missing.append("14A");
        if (!lab15Valid) {
            if (missing.length() > 0) missing.append(", ");
            missing.append("15");
        }
        if (!lab16Valid) {
            if (missing.length() > 0) missing.append(", ");
            missing.append("16");
        }
        return missing.toString();
    }
}
