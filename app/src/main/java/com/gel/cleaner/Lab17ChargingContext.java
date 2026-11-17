package com.gel.cleaner;

/**
 * Context-aware interpretation of Lab 15 evidence for Lab 17.
 * The user's charger answer is not a measured charging protocol, input power,
 * proof of compatibility, or an OEM expected charge rate. Actual thermal,
 * connection and system-limiting findings always take precedence.
 */
public final class Lab17ChargingContext {
    private Lab17ChargingContext() {}

    public static final class Decision {
        public final boolean slowNetRate;
        public final boolean independentConcern;
        public final boolean slowRateNeedsReview;
        public final boolean chargingConcern;
        public final boolean unknownChargerWithSlowRate;
        public final String description;

        private Decision(boolean slow, boolean independent, boolean needsReview,
                         boolean unknownWithSlow, String description) {
            this.slowNetRate = slow;
            this.independentConcern = independent;
            this.slowRateNeedsReview = needsReview;
            this.chargingConcern = independent || needsReview;
            this.unknownChargerWithSlowRate = unknownWithSlow;
            this.description = description;
        }
    }

    public static Decision evaluate(boolean greek, String userChargerType,
                                    String netRateBand, String legacyStrengthLabel,
                                    boolean chargingOvertemp, boolean connectionInterruptions,
                                    boolean systemLimited) {
        final boolean slow = "MODERATE".equals(netRateBand)
                || "LOW".equals(netRateBand)
                || (netRateBand == null && "WEAK".equals(legacyStrengthLabel));
        final boolean independent = chargingOvertemp || connectionInterruptions || systemLimited;
        final boolean reportedFast = "YES".equals(userChargerType);
        final boolean reportedRegular = "NO".equals(userChargerType);

        // Slow net battery gain alone cannot isolate the source of slow charging.
        // A fast-charger *claim* supports a follow-up, not a device-fault verdict.
        final boolean needsReview = slow && reportedFast;
        final boolean unknownWithSlow = slow && !reportedFast && !reportedRegular;
        String explanation = "";
        if (slow && reportedFast) {
            explanation = greek
                    ? "Ο χρήστης δήλωσε ταχυφορτιστή και καταγράφηκε χαμηλός καθαρός ρυθμός φόρτισης. Η ενεργοποίηση ταχυφόρτισης δεν επιβεβαιώθηκε. Ελέγξτε συμβατότητα, καλώδιο, στάθμη μπαταρίας και θερμοκρασία· το εύρημα δεν αποδεικνύει βλάβη του κινητού."
                    : "A fast charger was reported with a low net charging rate. Active fast-charge negotiation was not verified. Check compatibility, cable, battery level and temperature; this does not prove a phone defect.";
        } else if (slow && reportedRegular) {
            explanation = greek
                    ? "Με δηλωμένο απλό φορτιστή, ο χαμηλός καθαρός ρυθμός μπορεί να σχετίζεται με την παροχή του φορτιστή ή τις συνθήκες της δοκιμής. Δεν αποδεικνύει βλάβη της συσκευής ούτε επιβεβαιώνει ότι η ταχύτητα φόρτισης είναι η μέγιστη δυνατή."
                    : "With a reported regular charger, low net charge gain may reflect its power delivery or test conditions. This does not prove a phone defect or establish the highest possible charging speed.";
        } else if (slow) {
            explanation = greek
                    ? "Ο τύπος φορτιστή δεν είναι γνωστός. Ο χαμηλός καθαρός ρυθμός δεν τεκμηριώνει βλάβη, αλλά δεν επιτρέπει ασφαλές συμπέρασμα για την αναμενόμενη ταχύτητα φόρτισης."
                    : "Charger type is unknown. Low net charge gain does not establish a defect, and the expected charging speed cannot be reliably assessed.";
        }
        return new Decision(slow, independent, needsReview, unknownWithSlow, explanation);
    }
}
