package com.gel.cleaner;

import java.util.Locale;

/**
 * Customer-facing interpretation of a bounded battery stress observation.
 * A voltage drop is a measured electrical response, NOT capacity loss or an
 * independent state-of-health determination.
 */
public final class Lab14CustomerGuidance {
    private Lab14CustomerGuidance() {}

    public enum Evidence { UNAVAILABLE, LOAD_VOLTAGE_RESPONSE_OBSERVED }

    public static Evidence observation(float beforeV, float underLoadV) {
        if (!Float.isFinite(beforeV) || !Float.isFinite(underLoadV)
                || beforeV < 2.5f || beforeV > 5.0f
                || underLoadV < 2.5f || underLoadV > 5.0f
                || beforeV - underLoadV < 0.005f
                || beforeV - underLoadV >= 1.0f) {
            return Evidence.UNAVAILABLE;
        }
        return Evidence.LOAD_VOLTAGE_RESPONSE_OBSERVED;
    }

    public static String voltageReading(float sagV, boolean gr) {
        if (!Float.isFinite(sagV) || sagV < 0.0f || sagV >= 1.0f) {
            return gr ? "Μη αξιόπιστη μέτρηση" : "No reliable observation";
        }
        return String.format(Locale.US, gr
                ? "%.3f V (παρατηρήθηκε υπό φορτίο)"
                : "%.3f V (observed under load)", sagV);
    }

    public static String evidenceLabel(boolean gr, boolean limited, boolean measuredSag) {
        if (limited || !measuredSag) {
            return gr ? "ΑΝΕΠΑΡΚΗ ΔΕΔΟΜΕΝΑ" : "INSUFFICIENT EVIDENCE";
        }
        return gr ? "ΠΑΡΑΤΗΡΟΥΜΕΝΗ ΜΕΤΑΒΟΛΗ ΤΑΣΗΣ"
                  : "OBSERVED VOLTAGE RESPONSE";
    }

    public static String recommendation(boolean gr, boolean limited, boolean measuredSag) {
        if (limited || !measuredSag) {
            return gr
                    ? "Τα διαθέσιμα στοιχεία δεν επαρκούν για αξιολόγηση φθοράς. Συνιστάται επανάληψη υπό συγκρίσιμες συνθήκες και τεχνικός έλεγχος εφόσον υπάρχουν συμπτώματα."
                    : "Available evidence is insufficient to assess wear. Repeat under comparable conditions and seek technician inspection if symptoms occur.";
        }
        return gr
                ? "Καταγράφηκε μεταβολή τάσης υπό φορτίο. Συνιστάται επανάληψη της μέτρησης υπό συγκρίσιμες συνθήκες και τεχνικός έλεγχος. Η απώλεια πλήρους χωρητικότητας δεν επιβεβαιώθηκε από αυτή τη δοκιμή."
                : "A voltage change under load was recorded. Repeat under comparable conditions and seek technician inspection. Loss of full-charge capacity was not established by this test.";
    }

    public static String electricalWarning(boolean gr, boolean measuredSag) {
        if (!measuredSag) return gr ? "Μη διαθέσιμη παρατήρηση" : "Observation unavailable";
        return gr
                ? "Η μεταβολή τάσης απαιτεί σύγκριση με επαναλήψεις, θερμοκρασία, φόρτιση και πραγματικό ρεύμα."
                : "Interpret voltage response with repeated matched tests, temperature, charge state and actual current.";
    }

    public static String unverifiedCellCondition(boolean gr) {
        return gr
                ? "Δεν αξιολογείται από αυτή τη δοκιμή· απαιτείται τεχνικός ή φυσικός έλεγχος."
                : "Not assessed by this test; technician or physical verification required.";
    }

    /** No non-calibrated voltage sag test can establish battery SoH or replacement need. */
    public static boolean permitsReplacementRecommendation() {
        return false;
    }
}
