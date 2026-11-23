package com.gel.cleaner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lab 12 presentation only. Collector values and evidence/severity rules remain
 * in their original English form, so localization cannot change diagnostics.
 */
final class Lab12EsimGreekFormatter {
    private Lab12EsimGreekFormatter() {}

    private static final Pattern ACTIVE_ESIM = Pattern.compile(
            "^(\\d+) active eSIM subscription\\(s\\) reported$");
    private static final Pattern SUBSCRIPTION_COUNTS = Pattern.compile(
            "^(\\d+) eSIM / (\\d+) non-eSIM \\(not physical-tray verification\\)$");

    static String label(String raw) {
        if (raw == null) return "";
        switch (raw) {
            case "eSIM hardware capability": return "Δυνατότητα eSIM (κατά Android)";
            case "eSIM service available": return "Διαθεσιμότητα υπηρεσίας eSIM";
            case "Active eSIM profiles": return "Ενεργές συνδρομές eSIM";
            case "Active subscription types": return "Τύποι ενεργών συνδρομών";
            default: return raw;
        }
    }

    static String value(String original) {
        if (original == null) return "";
        switch (original) {
            case "Unknown / unavailable from Android":
                return "Άγνωστο / μη διαθέσιμο από το Android";
            case "Not available from Android API < 28":
                return "Μη διαθέσιμο σε Android πριν από την έκδοση 9";
            case "Supported (Android eUICC feature declared)":
                return "Υποστηρίζεται (το Android δηλώνει δυνατότητα eUICC)";
            case "Not declared by Android (not proof of physical absence)":
                return "Δεν δηλώνεται από το Android (δεν αποδεικνύει απουσία υλικού eSIM)";
            case "Unavailable / not reported":
                return "Μη διαθέσιμη / δεν αναφέρεται";
            case "Enabled (does not imply an active eSIM plan)":
                return "Ενεργή (δεν συνεπάγεται ενεργή συνδρομή eSIM)";
            case "Disabled / unavailable (not proof of hardware absence)":
                return "Απενεργοποιημένη / μη διαθέσιμη (δεν αποδεικνύει απουσία υλικού eSIM)";
            case "Unknown — phone-state permission not granted":
                return "Άγνωστο — δεν παραχωρήθηκε άδεια κατάστασης τηλεφώνου";
            case "Unknown — subscription access denied":
                return "Άγνωστο — δεν επιτρέπεται πρόσβαση στις συνδρομές";
            case "0 in Android-visible active subscriptions":
                return "0 στις ενεργές συνδρομές που εμφανίζει το Android";
            default:
                break;
        }
        Matcher active = ACTIVE_ESIM.matcher(original);
        if (active.matches())
            return active.group(1) + " ενεργές συνδρομές eSIM (κατά Android)";
        Matcher counts = SUBSCRIPTION_COUNTS.matcher(original);
        if (counts.matches())
            return counts.group(1) + " eSIM / " + counts.group(2)
                    + " μη ενσωματωμένες SIM (χωρίς έλεγχο φυσικής θήκης)";
        // Forward compatibility: preserve unknown Android values rather than
        // inventing a translation or changing their evidentiary meaning.
        return original;
    }
}
