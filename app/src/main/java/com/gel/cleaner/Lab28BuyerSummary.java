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
                ? "Το Lab 27 κατέγραψε επιβεβαιωμένο λειτουργικό περιορισμό. "
                  + "Δες το συγκεκριμένο εύρημα και επανέλεγξέ το πριν αποφασίσεις."
                : "Lab 27 documented a confirmed operational limitation. "
                  + "Review and recheck the specific finding before deciding.";

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
                        ? "Οι απαιτούμενοι έλεγχοι ολοκληρώθηκαν με καλή συνολική εικόνα, "
                          + "αλλά καταγράφηκε σημαντικό εύρημα που χρειάζεται επανέλεγχο. "
                          + "Το εύρημα δεν αποδεικνύει από μόνο του βλάβη εξαρτήματος."
                        : "Required checks were completed with generally good results, "
                          + "but a significant finding needs recheck. The observation "
                          + "alone does not prove a defective component.";
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
