package com.gel.cleaner;

/** Positive user hearing reports are evidence of audibility, not hardware certification. */
public final class UserAcousticEvidence {
    public enum Answer { ALL, SOME, NONE }
    private UserAcousticEvidence() {}

    public static boolean isPositive(Answer answer) {
        return answer == Answer.ALL || answer == Answer.SOME;
    }

    public static String message(int lab, int requestedTones, int detectedTones,
                                 Answer answer, boolean greek) {
        if (lab != 1 && lab != 2) throw new IllegalArgumentException("Only Labs 1 and 2");
        int total = lab == 1 ? 1 : 4;
        int detected = Math.max(0, Math.min(detectedTones, total));
        String auto = detected + "/" + total;
        if (greek) {
            if (answer == Answer.ALL)
                return "ΕΠΙΒΕΒΑΙΩΣΗ ΧΡΗΣΤΗ — ακούστηκαν καθαρά "
                        + (lab == 1 ? "ο τόνος" : "και οι 4 τόνοι")
                        + ". Αυτόματη συσχέτιση: " + auto
                        + ". Δεν αποτελεί πιστοποίηση υγείας ηχείου.";
            if (answer == Answer.SOME && lab == 2)
                return "ΜΕΡΙΚΗ ΕΠΙΒΕΒΑΙΩΣΗ ΧΡΗΣΤΗ — ακούστηκαν ορισμένοι από τους 4 τόνους. "
                        + "Αυτόματη συσχέτιση: " + auto
                        + ". Δεν τεκμηριώνεται βλάβη ή πλήρης επιτυχία.";
            return "Ο χρήστης δεν επιβεβαίωσε ότι άκουσε "
                    + (lab == 1 ? "τον τόνο" : "κάποιον από τους 4 τόνους")
                    + ". Αυτόματη συσχέτιση: " + auto + ".";
        }
        if (answer == Answer.ALL)
            return "USER_REPORTED — " + (lab == 1 ? "tone heard clearly" : "all 4 tones heard clearly")
                    + ". Automatic correlation: " + auto + ". Speaker health not certified.";
        if (answer == Answer.SOME && lab == 2)
            return "USER_REPORTED — some of 4 tones heard. Automatic correlation: " + auto
                    + ". No full success or hardware failure established.";
        return "USER_REPORTED — " + (lab == 1 ? "tone not heard" : "no tones heard")
                + ". Automatic correlation: " + auto + ".";
    }
}
