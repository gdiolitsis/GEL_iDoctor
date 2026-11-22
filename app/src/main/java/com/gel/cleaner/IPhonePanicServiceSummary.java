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
                found = count + " " + (watchdog == 1
                        ? "Ένα αρχείο αναφέρει μη απόκριση του συστήματος (Watchdog)."
                        : watchdog + " αρχεία αναφέρουν μη απόκριση του συστήματος (Watchdog).");
            } else if (thermal > 0) {
                found = count + " " + (thermal == 1
                        ? "Ένα αρχείο αναφέρει θερμική διακοπή ή περιορισμό απόδοσης."
                        : thermal + " αρχεία αναφέρουν θερμική διακοπή ή περιορισμό απόδοσης.");
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

        // Report the actual recurrence observation only. The imported-log
        // scope and per-file provenance already appear in the earlier labs.
        final String meaning;
        if (greek) {
            meaning = totalFiles == 1
                    ? "Μία καταγραφή· για έλεγχο επανάληψης χρειάζονται περισσότερα αρχεία."
                    : repeatedKnownDomain
                    ? "Η ίδια κατηγορία ενδείξεων επαναλαμβάνεται· συσχετίστε την με τα συμπτώματα."
                    : hasKnownDomain
                    ? "Δεν εντοπίστηκε επανάληψη γνωστής κατηγορίας στα αρχεία."
                    : "Δεν ήταν δυνατή η ομαδοποίηση των ενδείξεων.";
        } else {
            meaning = totalFiles == 1
                    ? "One record; more files are needed to assess recurrence."
                    : repeatedKnownDomain
                    ? "The same clue category recurs; compare it with actual symptoms."
                    : hasKnownDomain
                    ? "No known clue category repeated in these records."
                    : "The clues could not be grouped into known categories.";
        }

        final String repair = greek
                ? "Δεν προκύπτει τεκμηριωμένη ανάγκη αντικατάστασης εξαρτήματος από αυτά τα logs. "
                  + "Αν το iPhone λειτουργεί κανονικά, τα logs από μόνα τους δεν απαιτούν επισκευή."
                : "These logs do not establish a need for part replacement. "
                  + "If the iPhone works normally, the logs alone do not justify a repair.";

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
