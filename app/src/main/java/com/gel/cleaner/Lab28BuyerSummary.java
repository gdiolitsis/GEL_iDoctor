package com.gel.cleaner;

/**
 * Lab 28 is a buyer-facing explanation of the existing Lab 27 certificate.
 * It must not recompute measurements, interpret warnings as faults, or claim
 * that an IMEI check proves legal ownership or an unblocked network status.
 */
final class Lab28BuyerSummary {
    private Lab28BuyerSummary() {}

    static boolean validGrade(String grade) {
        return "A+".equals(grade) || "A".equals(grade) || "B".equals(grade)
                || "C".equals(grade) || "D".equals(grade);
    }

    static String explain(String grade, boolean confirmedFunctionalFault, boolean greek) {
        if (!validGrade(grade)) return greek
                ? "Δεν υπάρχει έγκυρη βαθμολογία. Εκτέλεσε ξανά το Lab 27."
                : "No valid grade is available. Run Lab 27 again.";

        if (confirmedFunctionalFault) return greek
                ? "Η πιστοποίηση εντόπισε επιβεβαιωμένη δυσλειτουργία. "
                  + "Δες το συγκεκριμένο εύρημα στο Lab 27 πριν αποφασίσεις."
                : "Certification identified a confirmed functional failure. "
                  + "Review the specific Lab 27 finding before deciding.";

        switch (grade) {
            case "A+":
                return greek
                        ? "Οι απαιτούμενοι έλεγχοι ολοκληρώθηκαν και η συσκευή "
                          + "παρουσίασε άριστη λειτουργική εικόνα στους ελεγμένους τομείς."
                        : "Required tests were completed and the device showed excellent "
                          + "functional performance in the tested areas.";
            case "A":
                return greek
                        ? "Οι απαιτούμενοι έλεγχοι ολοκληρώθηκαν. Η συσκευή "
                          + "παρουσίασε πολύ καλή λειτουργική εικόνα στους ελεγμένους τομείς."
                        : "Required tests were completed. The device showed very good "
                          + "functional performance in the tested areas.";
            case "B":
                return greek
                        ? "Οι απαιτούμενοι έλεγχοι ολοκληρώθηκαν. Η συνολική λειτουργική "
                          + "εικόνα είναι καλή, αλλά όχι άριστη. Ο βαθμός Β από μόνος "
                          + "του δεν αποδεικνύει βλάβη."
                        : "Required tests were completed. Overall functional performance "
                          + "is good, though not excellent. Grade B alone does not "
                          + "establish a malfunction.";
            case "C":
                return greek
                        ? "Οι απαιτούμενοι έλεγχοι ολοκληρώθηκαν, αλλά η βαθμολογία "
                          + "των μετρήσεων είναι χαμηλότερη. Δες τα αποτελέσματα "
                          + "στο Lab 27 πριν αποφασίσεις."
                        : "Required tests were completed, but measured performance "
                          + "scored lower. Review Lab 27 before deciding.";
            default:
                return greek
                        ? "Οι μετρήσεις έδωσαν χαμηλή λειτουργική βαθμολογία. "
                          + "Δες το αναλυτικό Lab 27 πριν αποφασίσεις."
                        : "Measurements yielded a low functional grade. "
                          + "Review the full Lab 27 report before deciding.";
        }
    }
}
