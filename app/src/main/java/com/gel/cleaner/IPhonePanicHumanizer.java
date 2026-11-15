package com.gel.cleaner;

import java.util.Locale;

/**
 * Human-facing vocabulary for Apple panic-log observations.
 * This class translates names only; it does not classify, score or diagnose.
 * Original technical names remain visible alongside their explanation.
 */
public final class IPhonePanicHumanizer {
    private IPhonePanicHumanizer() {}

    public static String event(String raw, boolean greek) {
        String term = text(raw);
        if (!greek) {
            if ("Multiple separate panic entries".equals(term))
                return "Several separate events (Multiple separate panic entries)";
            return term;
        }
        switch (term) {
            case "Watchdog / Hang":
                return "Μη απόκριση λειτουργίας συστήματος (Watchdog / Hang)";
            case "Userspace Watchdog Timeout":
                return "Μια λειτουργία δεν αποκρίθηκε έγκαιρα (Userspace Watchdog Timeout)";
            case "Thermal Shutdown / Throttle":
                return "Αναφορά θερμικής διακοπής ή περιορισμού απόδοσης (Thermal Shutdown / Throttle)";
            case "Kernel Panic":
                return "Σοβαρή διακοπή λειτουργίας του πυρήνα (Kernel Panic)";
            case "Jetsam / Memory Pressure":
                return "Περιορισμός πόρων μνήμης (Jetsam / Memory Pressure)";
            case "Baseband Watchdog":
                return "Μη απόκριση λειτουργίας κινητής τηλεφωνίας (Baseband Watchdog)";
            case "iOS Crash Report":
                return "Καταγραφή απροσδόκητου τερματισμού εφαρμογής ή υπηρεσίας (iOS Crash Report)";
            case "Multiple separate panic entries":
                return "Περισσότερα από ένα ανεξάρτητα συμβάντα (Multiple separate panic entries)";
            case "Unknown":
            case "Unknown / Generic":
                return "Δεν προσδιορίστηκε τύπος συμβάντος";
            default:
                return term;
        }
    }

    public static String domain(String raw, boolean greek) {
        String term = text(raw);
        if (!greek) {
            if ("Mixed evidence — per-entry evaluation required".equals(term))
                return "Different log indications; evaluate each event separately";
            return term;
        }
        switch (term) {
            case "Kernel / OS Core": return "Πυρήνας λειτουργικού συστήματος (Kernel / OS Core)";
            case "Thermal / Cooling": return "Θερμική διαχείριση (Thermal / Cooling)";
            case "Thermal": return "Θερμική διαχείριση (Thermal)";
            case "Memory / OS Pressure": return "Διαχείριση μνήμης (Memory / OS Pressure)";
            case "Memory": return "Διαχείριση μνήμης (Memory)";
            case "GPU / Graphics":
            case "GPU": return "Γραφικά / επεξεργαστής γραφικών (GPU)";
            case "Baseband / Cellular":
            case "Baseband": return "Λειτουργίες κινητής τηλεφωνίας (Baseband)";
            case "Storage / NAND / FS":
            case "Storage": return "Αποθηκευτικός χώρος (Storage / NAND)";
            case "Power / PMIC":
            case "Power": return "Διαχείριση τροφοδοσίας (Power / PMIC)";
            case "Sensors / I/O":
            case "Sensors": return "Αισθητήρες και περιφερειακά (Sensors / I/O)";
            case "I2C / Peripheral Bus": return "Επικοινωνία με περιφερειακά (I2C)";
            case "Mixed evidence — per-entry evaluation required":
                return "Διαφορετικές ενδείξεις: κάθε αρχείο εξετάζεται χωριστά";
            case "Unknown":
                return "Δεν προσδιορίστηκε κατηγορία υποσυστήματος";
            default:
                return term;
        }
    }

    /** An interpretation of an imported log, never a live-device diagnosis. */
    public static String meaning(String raw, boolean greek) {
        String term = text(raw);
        if ("Watchdog / Hang".equals(term) || "Userspace Watchdog Timeout".equals(term)
                || "Baseband Watchdog".equals(term)) {
            return greek
                    ? "Το αρχείο αναφέρει ότι μια λειτουργία δεν αποκρίθηκε στον προβλεπόμενο χρόνο. Δεν αποδεικνύεται ότι υπάρχει χαλασμένο εξάρτημα."
                    : "The file reports a function that did not respond in time. This does not establish a defective component.";
        }
        if ("Thermal Shutdown / Throttle".equals(term)) {
            return greek
                    ? "Το αρχείο αναφέρει θερμικό συμβάν ή περιορισμό λειτουργίας. Δεν έχουμε μετρήσει τη θερμοκρασία της συσκευής ούτε επιβεβαιώσει βλάβη."
                    : "The file reports a thermal event or throttling. Device temperature and hardware condition have not been measured.";
        }
        if ("Multiple separate panic entries".equals(term)) {
            return greek
                    ? "Υπάρχουν διαφορετικά αρχεία. Κάθε συμβάν αξιολογείται μόνο του, ώστε να μη συνδυαστούν άσχετες ενδείξεις."
                    : "There are distinct records. Each is reviewed separately so unrelated clues are not combined.";
        }
        if ("Kernel Panic".equals(term)) {
            return greek
                    ? "Έχει καταγραφεί σοβαρή διακοπή του λειτουργικού. Το αρχείο δεν αποδεικνύει την αιτία ή την κατάσταση ενός εξαρτήματος."
                    : "A serious operating-system interruption is recorded. The cause and physical component condition remain unverified.";
        }
        return greek
                ? "Πρόκειται για ένδειξη μέσα σε αρχείο καταγραφής, όχι αποτέλεσμα φυσικού ελέγχου της συσκευής."
                : "This is a log observation, not a physical device test.";
    }

    public static String certainty(String raw, boolean greek) {
        if (!"UNVERIFIED".equalsIgnoreCase(text(raw))) return text(raw);
        return greek ? "Η αιτία δεν έχει επιβεβαιωθεί" : "The cause has not been verified";
    }

    public static String ruleWeight(String raw, boolean greek) {
        String term = text(raw);
        if (!greek) return term;
        if ("HIGH".equalsIgnoreCase(term)) return "Υψηλό βάρος κανόνα (όχι βαρύτητα βλάβης)";
        if ("MEDIUM".equalsIgnoreCase(term)) return "Μεσαίο βάρος κανόνα (όχι βαρύτητα βλάβης)";
        if ("LOW".equalsIgnoreCase(term)) return "Χαμηλό βάρος κανόνα (όχι βαρύτητα βλάβης)";
        return term;
    }

    public static String possibleCause(String raw, boolean greek) {
        if (!greek) return ApplePanicEvidencePolicy.possibleCause(raw);
        String term = text(raw);
        if ("Unknown".equals(term))
            return "Το αρχείο δεν επιτρέπει να προσδιορίσουμε την αιτία.";
        String lower = term.toLowerCase(Locale.ROOT);
        if (lower.contains("watchdog") && lower.contains("timeout"))
            return "Το αρχείο αναφέρει ότι μια λειτουργία δεν αποκρίθηκε έγκαιρα. "
                    + "Δεν προκύπτει ποιο εξάρτημα, αν κάποιο, ευθύνεται.";
        if (lower.contains("thermal"))
            return "Το αρχείο περιέχει θερμική ένδειξη. Δεν επιβεβαιώνεται "
                    + "υπερθέρμανση ή βλάβη της συσκευής χωρίς πρόσθετο έλεγχο.";
        return "Ο κανόνας δίνει μη επιβεβαιωμένη υπόθεση. Τεχνική περιγραφή: " + term;
    }

    public static String evidence(String raw, boolean greek) {
        String term = text(raw);
        if (greek && "Multiple independent panic entries".equals(term))
            return "Περισσότερα από ένα ανεξάρτητα αρχεία panic";
        return term;
    }

    private static String text(String raw) {
        return raw == null || raw.trim().isEmpty() ? "Unknown" : raw.trim();
    }
}
