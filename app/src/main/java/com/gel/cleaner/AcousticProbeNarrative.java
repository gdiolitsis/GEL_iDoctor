package com.gel.cleaner;

/** Localized, evidence-scoped conclusions for the existing 1/2 audio probe.
 * A microphone observation is not user-confirmed hearing or speaker health. */
public final class AcousticProbeNarrative {
    private AcousticProbeNarrative() {}

    public static String lab1Conclusion(boolean attempted, boolean observed, boolean greek) {
        if (!attempted) return greek
                ? "ΜΗ ΔΙΑΘΕΣΙΜΟ — δεν εκτελέστηκε ακουστική δειγματοληψία."
                : "UNAVAILABLE — the acoustic probe was not performed.";
        if (!observed) return greek
                ? "ΜΗ ΕΠΙΒΕΒΑΙΩΜΕΝΟ — δεν ανιχνεύθηκε αξιόπιστη συσχέτιση μικροφώνου και τόνου. Δεν τεκμηριώνεται βλάβη."
                : "INCONCLUSIVE — no reliable microphone/tone correlation. No defect established.";
        return greek
                ? "ΘΕΤΙΚΗ ΑΚΟΥΣΤΙΚΗ ΕΝΔΕΙΞΗ — το μικρόφωνο κατέγραψε τον παραγόμενο τόνο κατά την αναπαραγωγή (1/1). Δεν πιστοποιείται η ακουστότητα από τον χρήστη ή η συνολική υγεία του ηχείου."
                : "POSITIVE ACOUSTIC EVIDENCE — microphone captured the generated tone during playback (1/1). User audibility and overall speaker health are not certified.";
    }

    public static String lab2Conclusion(boolean attempted, int heard, int sampled, boolean greek) {
        if (!attempted || sampled <= 0) return greek
                ? "ΜΗ ΔΙΑΘΕΣΙΜΟ — δεν ολοκληρώθηκε έγκυρη ακουστική δειγματοληψία."
                : "UNAVAILABLE — no valid acoustic capture completed.";
        if (sampled < 4) return greek
                ? "ΜΗ ΟΛΟΚΛΗΡΩΜΕΝΗ ΔΕΙΓΜΑΤΟΛΗΨΙΑ — λήφθηκαν " + sampled + "/4 τόνοι. Δεν εξάγεται τελικό συμπέρασμα."
                : "INCOMPLETE CAPTURE — " + sampled + "/4 tones sampled; no final conclusion.";
        int count = Math.max(0, Math.min(heard, sampled));
        if (count == 4) return greek
                ? "ΘΕΤΙΚΗ ΑΚΟΥΣΤΙΚΗ ΕΝΔΕΙΞΗ — ανιχνεύθηκαν 4/4 συγχρονισμένοι τόνοι από το μικρόφωνο. Δεν πιστοποιείται όλο το ακουστικό φάσμα ή η υγεία του ηχείου."
                : "POSITIVE ACOUSTIC EVIDENCE — microphone detected 4/4 synchronized tones. Full frequency response and speaker health are not certified.";
        if (count > 0) return greek
                ? "ΜΕΡΙΚΗ ΑΚΟΥΣΤΙΚΗ ΕΝΔΕΙΞΗ — ανιχνεύθηκαν " + count + "/4 τόνοι. Υπήρξε απόκριση σε ορισμένες συχνότητες, όχι πλήρης επιβεβαίωση. Δεν αποδεικνύεται βλάβη."
                : "PARTIAL ACOUSTIC EVIDENCE — " + count + "/4 tones detected. Some frequency responses observed, not full confirmation; no defect established.";
        return greek
                ? "ΜΗ ΕΠΙΒΕΒΑΙΩΜΕΝΟ — 0/4 συγχρονισμένοι τόνοι ανιχνεύθηκαν· απαιτείται επανάληψη, όχι διάγνωση βλάβης."
                : "INCONCLUSIVE — 0/4 synchronized tones detected; retest, not a hardware diagnosis.";
    }

    public static String reason(String raw, boolean greek) {
        if (!greek) return raw == null ? "Reason unavailable" : raw;
        if (raw == null || raw.isEmpty()) return "Δεν υπάρχει διαθέσιμη αιτιολόγηση.";
        switch (raw) {
            case "Time-aligned generated frequencies observed at microphone":
                return "Οι συχνότητες των τόνων ανιχνεύθηκαν από το μικρόφωνο την ίδια στιγμή που αναπαράγονταν.";
            case "No repeatable synchronized tone above microphone baseline":
                return "Δεν ανιχνεύθηκε επαναλήψιμη απόκριση τόνου πάνω από τον θόρυβο βάσης του μικροφώνου.";
            case "Microphone clipped; repeat at lower level":
                return "Το δείγμα του μικροφώνου παρουσίασε υπερφόρτωση (clipping). Επανάλαβε σε χαμηλότερη ένταση.";
            case "Media stream muted; no acoustic challenge":
                return "Η ένταση πολυμέσων είναι μηδενική· η δοκιμή δεν πραγματοποιήθηκε.";
            case "Microphone capture format unsupported":
                return "Η απαιτούμενη μορφή ηχογράφησης δεν υποστηρίζεται από τη συσκευή.";
            case "Microphone AudioRecord not initialized":
                return "Η καταγραφή μικροφώνου δεν αρχικοποιήθηκε.";
            case "Microphone was not recording":
                return "Το μικρόφωνο δεν ξεκίνησε την καταγραφή.";
            case "Ambient microphone capture incomplete":
                return "Η καταγραφή του περιβάλλοντος θορύβου δεν ολοκληρώθηκε.";
            case "Stimulus microphone capture incomplete":
                return "Η καταγραφή κατά την αναπαραγωγή δεν ολοκληρώθηκε.";
            case "Tone playback request rejected":
                return "Το Android δεν δέχτηκε την εντολή αναπαραγωγής τόνου.";
            case "AudioManager unavailable":
                return "Η διαχείριση ήχου Android δεν είναι διαθέσιμη.";
            case "Context unavailable":
                return "Η εφαρμογή δεν είχε διαθέσιμο ενεργό περιβάλλον δοκιμής.";
            case "Audio output devices unavailable; built-in speaker route unverified":
                return "Δεν ήταν διαθέσιμα στοιχεία εξόδου ήχου· η διαδρομή προς το ηχείο δεν επαληθεύτηκε.";
        }
        if (raw.startsWith("External audio output available"))
            return "Εντοπίστηκε διαθέσιμη εξωτερική έξοδος ήχου. Αποσύνδεσέ την και επανάλαβε.";
        if (raw.startsWith("Cannot inspect available audio outputs"))
            return "Το Android δεν επέτρεψε τον έλεγχο διαθέσιμων εξόδων ήχου.";
        if (raw.startsWith("Audio route, permission or capture unavailable"))
            return "Η διαδρομή ήχου, η άδεια μικροφώνου ή η καταγραφή δεν ήταν διαθέσιμη.";
        // Other low-level DSP errors stay unavailable; do not translate them
        // to a manufactured hardware fault or silently claim success.
        return "Δεν ολοκληρώθηκε η ακουστική ανάλυση με επαληθεύσιμη αιτιολόγηση.";
    }

    public static String route(String raw, boolean greek) {
        if (!greek || raw == null) return raw == null ? "Unknown" : raw;
        String prefix = "Android audio input device type ";
        if (raw.startsWith(prefix))
            return "Είσοδος μικροφώνου Android — τύπος συσκευής "
                    + raw.substring(prefix.length());
        if ("Unknown".equalsIgnoreCase(raw) || "Unknown microphone route".equals(raw))
            return "Άγνωστη διαδρομή μικροφώνου";
        return raw;
    }
}
