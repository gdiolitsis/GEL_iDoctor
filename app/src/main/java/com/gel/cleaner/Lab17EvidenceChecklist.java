package com.gel.cleaner;

/** Phase-A Lab 17 prerequisite guidance, without inventing capacity or SoH. */
public final class Lab17EvidenceChecklist {
    public static final class Missing {
        public final int count;
        public final String names;
        private Missing(int count, String names) {
            this.count = count;
            this.names = names;
        }
    }
    private Lab17EvidenceChecklist() {}

    public static Missing missing(boolean lab14a, boolean lab14b,
                                  boolean lab15, boolean lab16) {
        StringBuilder names = new StringBuilder();
        int count = 0;
        if (!lab14a) { names.append("Lab 14A"); count++; }
        if (!lab14b) { if (names.length() > 0) names.append(", ");
            names.append("Lab 14B"); count++; }
        if (!lab15) { if (names.length() > 0) names.append(", ");
            names.append("Lab 15"); count++; }
        if (!lab16) { if (names.length() > 0) names.append(", ");
            names.append("Lab 16"); count++; }
        return new Missing(count, names.toString());
    }
}
