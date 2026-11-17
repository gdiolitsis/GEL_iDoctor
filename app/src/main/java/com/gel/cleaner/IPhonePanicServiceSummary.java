package com.gel.cleaner;

/**
 * Plain-language interpretation of imported iPhone panic records.
 * This is an explanation of historical file contents, NOT an assessment
 * of the physical device, root cause, repair necessity or current health.
 */
public final class IPhonePanicServiceSummary {
    private IPhonePanicServiceSummary() {}

    public static final class Result {
        public final String whatWeFound;
        public final String whatItMeans;
        public final String repairAdvice;
        public final String whenToSeekHelp;
        public final boolean repeatedClueNeedsReview;

        Result(String found, String meaning, String repair, String help, boolean repeated) {
            whatWeFound = found;
            whatItMeans = meaning;
            repairAdvice = repair;
            whenToSeekHelp = help;
            repeatedClueNeedsReview = repeated;
        }
    }

    public static Result interpret(boolean greek, int totalFiles,
                                   int watchdogFiles, int thermalFiles,
                                   boolean repeatedKnownDomain, boolean hasKnownDomain) {
        if (totalFiles <= 0) throw new IllegalArgumentException("No eligible panic files");

        // One imported record is counted once, even when it contains many keywords.
        int watchdog = Math.max(0, Math.min(totalFiles, watchdogFiles));
        int thermal = Math.max(0, Math.min(totalFiles - watchdog, thermalFiles));

        final String found;
        if (greek) {
            String count = totalFiles == 1
                    ? "Εξετάσαμε ένα εισαγόμενο panic log."
                    : "Εξετάσαμε " + totalFiles + " εισαγόμενα panic logs.";
            if (watchdog > 0 && thermal > 0) {
                found = count + " Τα " + (totalFiles == 2 ? "δύο" : "αναγνωρισμένα")
                        + " συμβάντα περιλαμβάνουν " + watchdog + " Watchdog (μη απόκριση συστήματος)"
                        + " και " + thermal + " Thermal (θερμική διακοπή ή περιορισμό).";
            } else if (watchdog > 0) {
                found = count + " " + watchdog + " αρχείο/α αναφέρουν Watchdog"
                        + " (μη απόκριση του συστήματος).";
            } else if (thermal > 0) {
                found = count + " " + thermal + " αρχείο/α αναφέρουν θερμική διακοπή"
                        + " ή περιορισμό απόδοσης.";
            } else {
                found = count + " Δεν αναγνωρίστηκε σε αυτά συγκεκριμένος τύπος"
                        + " Watchdog ή θερμικής διακοπής.";
            }
        } else {
            String count = totalFiles == 1
                    ? "We examined one imported panic log."
                    : "We examined " + totalFiles + " imported panic logs.";
            if (watchdog > 0 && thermal > 0) {
                found = count + " The recorded events include " + watchdog
                        + " Watchdog (unresponsive system) and " + thermal
                        + " Thermal (thermal shutdown or limitation).";
            } else if (watchdog > 0) {
                found = count + " " + watchdog + " record(s) refer to a Watchdog"
                        + " (unresponsive system).";
            } else if (thermal > 0) {
                found = count + " " + thermal + " record(s) refer to thermal shutdown"
                        + " or performance limitation.";
            } else {
                found = count + " No specific Watchdog or thermal incident type"
                        + " was identified in these records.";
            }
        }

        final String meaning;
        if (greek) {
            String repetition = totalFiles == 1
                    ? "Με ένα μόνο αρχείο δεν μπορεί να ελεγχθεί αν το φαινόμενο επαναλήφθηκε."
                    : repeatedKnownDomain
                    ? "Εμφανίζεται ξανά η ίδια κατηγορία τεχνικών ενδείξεων στα αρχεία και αξίζει περαιτέρω έλεγχο. Αυτό δεν αποδεικνύει επαναλαμβανόμενη βλάβη στην ίδια συσκευή."
                    : hasKnownDomain
                    ? "Δεν εντοπίστηκε επανάληψη της ίδιας γνωστής κατηγορίας στα αρχεία που εξετάστηκαν."
                    : "Οι κατηγορίες των ενδείξεων δεν προσδιορίστηκαν επαρκώς ώστε να κριθεί αν επαναλαμβάνονται.";
            meaning = "Πρόκειται για ιστορικά συμβάντα που αναφέρουν τα αρχεία, όχι για ζωντανό έλεγχο του iPhone. "
                    + repetition;
        } else {
            String repetition = totalFiles == 1
                    ? "A single record cannot establish whether an event recurred."
                    : repeatedKnownDomain
                    ? "The same technical clue category appears in multiple records and merits follow-up; this does not prove a recurring fault on the same phone."
                    : hasKnownDomain
                    ? "No known clue category repeated among the records reviewed."
                    : "The clue categories are not sufficiently identified to assess recurrence.";
            meaning = "These are historical events described by the files, not a live iPhone diagnostic. "
                    + repetition;
        }

        final String repair = greek
                ? "Τα συγκεκριμένα αρχεία δεν τεκμηριώνουν χαλασμένο εξάρτημα ή ανάγκη άμεσης αντικατάστασής του. "
                  + "Αν το iPhone λειτουργεί κανονικά, δεν προκύπτει λόγος επισκευής μόνο από αυτά τα logs. "
                  + "Η σημερινή λειτουργική κατάσταση της συσκευής δεν έχει μετρηθεί."
                : "These files do not establish a defective part or an immediate need to replace one. "
                  + "If the iPhone currently works normally, these logs alone do not justify a repair. "
                  + "Its present operating condition was not measured.";

        String help = greek
                ? "Αν συνεχίζονται επανεκκινήσεις, κολλήματα ή υπερθέρμανση, συσχέτισε τις ημερομηνίες των logs "
                  + "με τα πραγματικά συμπτώματα και ζήτησε τεχνικό έλεγχο."
                : "If restarts, freezes or overheating continue, compare the log dates with actual symptoms "
                  + "and request a technical inspection.";
        if (thermal > 0) {
            help += greek
                    ? " Αν η συσκευή είναι υπερβολικά ζεστή στην αφή, διακόπτεις χρήση και φόρτιση ώσπου να κρυώσει."
                    : " If the device is uncomfortably hot to touch, stop using and charging it until it cools.";
        }
        return new Result(found, meaning, repair, help, repeatedKnownDomain);
    }
}
