package com.gel.cleaner;

/**
 * Lab 15 alone interprets the user's charger declaration. It persists a
 * neutral outcome code so Lab 17 needs only the diagnostic result, not the
 * declaration, charger-specific advice, or unverified charging protocol.
 * Genuine heat, disconnection and Android limiting always take precedence.
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

    /**
     * Persist only Lab 15's diagnostic interpretation. The user-reported
     * charger type remains Lab 15 context, not a Lab 17 input or disclosure.
     */
    public static String resultCode(Decision assessed, boolean comparableRate) {
        if (assessed == null) return "RATE_UNAVAILABLE";
        if (assessed.independentConcern) return "OBSERVED_WARNING";
        if (!comparableRate) return "RATE_UNAVAILABLE";
        if (assessed.slowRateNeedsReview) return "SLOW_REVIEW";
        if (assessed.unknownChargerWithSlowRate) return "SLOW_CONTEXT_UNKNOWN";
        if (assessed.slowNetRate) return "SLOW_NO_FAULT_ESTABLISHED";
        return "NO_ISSUE_OBSERVED";
    }

    /**
     * Read the saved LAB 15 *outcome*, not the original questionnaire.
     * Historic v59 records without the new code remain neutral for an
     * isolated slow rate. Never convert unknown context into "fault".
     */
    public static Decision fromLab15Result(
            String savedCode, String rateBand, String legacyStrength,
            boolean chargingOvertemp, boolean disconnects, boolean limited) {
        boolean slow = "MODERATE".equals(rateBand)
                || "LOW".equals(rateBand)
                || (rateBand == null && "WEAK".equals(legacyStrength));
        boolean independent = chargingOvertemp || disconnects || limited
                || "OBSERVED_WARNING".equals(savedCode);
        boolean review = slow && "SLOW_REVIEW".equals(savedCode);
        boolean contextualSlow = "SLOW_NO_FAULT_ESTABLISHED".equals(savedCode);
        // Fail closed on a missing/invalid code, not as a device failure.
        boolean incompleteSlowContext = slow && !review && !contextualSlow;
        return new Decision(slow, independent, review, incompleteSlowContext, "");
    }
}
