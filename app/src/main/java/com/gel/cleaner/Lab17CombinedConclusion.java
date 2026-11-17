package com.gel.cleaner;

/**
 * Evidence-only interpretation of Lab 14A + 15 + 16.
 * Weak short-load voltage response may indicate ordinary battery ageing,
 * but it is not by itself evidence of a device fault or a measured SoH.
 * Every conclusion is limited to the tested battery/charging/thermal areas.
 */
public final class Lab17CombinedConclusion {
    private Lab17CombinedConclusion() {}

    public enum Tone { POSITIVE, CAUTION, NEUTRAL }

    public static final class Decision {
        public final Tone tone;
        public final String message;
        private Decision(Tone tone, String message) {
            this.tone = tone;
            this.message = message;
        }
    }

    public static Decision evaluate(boolean gr, String stressClass,
                                    boolean independentStressConcern,
                                    boolean chargingConcern,
                                    boolean thermalConcern,
                                    boolean completeCoverage) {
        final boolean critical = "Critical".equals(stressClass);
        final boolean weak = "Weak".equals(stressClass);
        final boolean known = weak || critical || "Normal".equals(stressClass)
                || "Good".equals(stressClass) || "Excellent".equals(stressClass);
        final boolean batteryConcern = critical || independentStressConcern;

        // A specific warning takes precedence over broad reassurance.
        if (chargingConcern && thermalConcern && batteryConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Υπάρχουν ανεξάρτητες ενδείξεις προς επανέλεγχο στην απόκριση της μπαταρίας, στη φόρτιση και στη θερμική συμπεριφορά. Δεν αποδεικνύεται συγκεκριμένη βλάβη, αλλά χρειάζεται πρόσθετος έλεγχος."
                    : "Battery stress response, charging and thermal behaviour all have findings that warrant follow-up. A specific fault is not established, but further checks are needed.");
        }
        if (chargingConcern && thermalConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Η φόρτιση και η θερμική συμπεριφορά παρουσιάζουν ευρήματα που χρειάζονται επανέλεγχο. Δεν τεκμηριώνεται συγκεκριμένη αιτία βλάβης."
                    : "Charging and thermal behaviour have findings that warrant follow-up. No specific fault cause has been established.");
        }
        if (chargingConcern && batteryConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Καταγράφηκαν ενδείξεις προς επανέλεγχο στη φόρτιση και στην απόκριση της μπαταρίας υπό φορτίο."
                    : "Charging and battery response under load both have findings that warrant follow-up.");
        }
        if (thermalConcern && batteryConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Καταγράφηκαν ενδείξεις προς επανέλεγχο στη θερμική συμπεριφορά και στην απόκριση της μπαταρίας υπό φορτίο."
                    : "Thermal behaviour and battery response under load both have findings that warrant follow-up.");
        }
        if (chargingConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Η φόρτιση παρουσιάζει ενδείξεις που χρειάζονται επανέλεγχο. Η καταγραφή δεν αποδεικνύει βλάβη της συσκευής."
                    : "Charging has findings that warrant follow-up. The readings do not prove a device fault.");
        }
        if (thermalConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Η θερμική συμπεριφορά παρουσιάζει ενδείξεις που χρειάζονται επανέλεγχο. Δεν αποδίδουμε το εύρημα αυτόματα στη μπαταρία."
                    : "Thermal behaviour has findings requiring verification. The cause is not automatically attributed to the battery.");
        }
        if (batteryConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Η φόρτιση και οι διαθέσιμες θερμικές ενδείξεις δεν έδειξαν πρόσθετο πρόβλημα, αλλά η απόκριση της μπαταρίας στην καταπόνηση χρειάζεται επανέλεγχο."
                    : "Charging and available thermal readings did not show an additional issue, but the battery's stress response warrants follow-up.");
        }

        // Incomplete sensor/charge coverage must not be rewritten as proven good health.
        if (!completeCoverage || !known) {
            return new Decision(Tone.NEUTRAL, gr
                    ? (weak
                        ? "Η μπαταρία μπορεί να εμφανίζει φυσιολογική φθορά υπό φορτίο. Δεν καταγράφηκε πρόσθετη προειδοποίηση στη φόρτιση ή στη θερμοκρασία, αλλά τα διαθέσιμα στοιχεία δεν αρκούν για συνολικό θετικό συμπέρασμα."
                        : "Δεν καταγράφηκε συγκεκριμένη προειδοποίηση στα διαθέσιμα αποτελέσματα. Η διαγνωστική κάλυψη όμως είναι μερική, οπότε δεν τεκμηριώνεται συνολική αξιολόγηση.")
                    : (weak
                        ? "The battery may show ordinary ageing under load. No additional charging or thermal warning was recorded, but the available observations are insufficient for an overall positive conclusion."
                        : "No specific warning was recorded in the available results. Diagnostic coverage is incomplete, so an overall conclusion is not established."));
        }

        if (weak) {
            return new Decision(Tone.POSITIVE, gr
                    ? "Η μπαταρία παρουσιάζει ενδείξεις που μπορεί να σχετίζονται με φυσιολογική φθορά, αλλά δεν καταγράφηκε δυσλειτουργία στη φόρτιση ή σοβαρή θερμική ένδειξη. Στη συγκεκριμένη αξιολόγηση, η απόκριση της μπαταρίας δεν φάνηκε να δημιουργεί πρόβλημα στη λειτουργία των ελεγχόμενων υποσυστημάτων. Ο συνδυασμός των αποτελεσμάτων δείχνει ικανοποιητική λειτουργία στους τομείς που ελέγχθηκαν. Δεν προκύπτει από αυτές τις δοκιμές ανάγκη αντικατάστασης της μπαταρίας."
                    : "The battery shows signs that may reflect ordinary ageing, but no charging malfunction or serious thermal finding was recorded. The battery response did not appear to impair the systems evaluated in this trial. Together, the results indicate satisfactory operation in the areas tested. These tests do not establish a need to replace the battery.");
        }

        return new Decision(Tone.POSITIVE, gr
                ? "Η μπαταρία ανταποκρίθηκε ικανοποιητικά στο συγκεκριμένο φορτίο και δεν καταγράφηκαν προβλήματα φόρτισης ή σοβαρές θερμικές ενδείξεις. Ο συνδυασμός των αποτελεσμάτων είναι θετικός για τους τομείς που ελέγχθηκαν."
                : "The battery responded satisfactorily under the tested load, with no recorded charging issues or serious thermal findings. Combined results are positive for the areas tested.");
    }
}
