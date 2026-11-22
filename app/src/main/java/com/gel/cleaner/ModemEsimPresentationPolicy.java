package com.gel.cleaner;

/** Neutral eSIM states are not successful eSIM feature measurements. */
final class ModemEsimPresentationPolicy {
    private ModemEsimPresentationPolicy() {}

    static boolean isEsimField(String label) {
        if (label == null) return false;
        switch (label.trim()) {
            case "eSIM hardware capability":
            case "eSIM service available":
            case "Active eSIM profiles":
            case "Active subscription types":
            case "SIM slot interpretation": return true;
            default: return false;
        }
    }

    static boolean isPositiveEvidence(String label, String value) {
        if (!isEsimField(label) || value == null) return false;
        String v = value.trim();
        switch (label.trim()) {
            case "eSIM hardware capability":
                return v.startsWith("Supported (Android eUICC feature declared)");
            case "eSIM service available":
                return v.startsWith("Enabled (does not imply an active eSIM plan)");
            case "Active eSIM profiles":
                return positiveCount(v, " active eSIM subscription(s) reported");
            case "Active subscription types":
                return positiveCount(v, " eSIM / ");
            default:
                // Android logical slot is never physical SIM-tray evidence.
                return false;
        }
    }

    private static boolean positiveCount(String value, String suffix) {
        int space = value.indexOf(' ');
        if (space <= 0 || !value.substring(space).startsWith(suffix)) return false;
        try {
            return Integer.parseInt(value.substring(0, space)) > 0;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
