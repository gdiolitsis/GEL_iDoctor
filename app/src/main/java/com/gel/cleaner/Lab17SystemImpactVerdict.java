package com.gel.cleaner;

/**
 * System-operation synthesis from recent Lab 14A, 15 and 16 evidence.
 *
 * A battery voltage drop alone cannot establish a device-wide malfunction.
 * Equally, normal accessible temperatures do not certify the whole device.
 * The output reports only observed impact on the tested power, charging and
 * thermal areas, separating a stress warning from Android-confirmed limiting.
 */
public final class Lab17SystemImpactVerdict {
    private Lab17SystemImpactVerdict() {}

    public enum Tone { POSITIVE, CAUTION, NEUTRAL }

    public static final class Decision {
        public final Tone tone;
        public final String message;
        private Decision(Tone tone, String message) {
            this.tone = tone;
            this.message = message;
        }
    }

    public static Decision evaluate(
            boolean gr,
            String batteryStressClass,
            boolean independentElectricalConcern,
            boolean chargingConcern,
            boolean chargingEvidenceAvailable,
            boolean thermalConcern,
            boolean thermalEvidenceAvailable,
            boolean androidThermalLimiting,
            boolean androidSevereThermalLimiting,
            boolean highAbsoluteStressBatteryTemperature,
            boolean largeStressTemperatureRise,
            boolean completeCoverage) {

        // Only the OS can establish that an active thermal limitation actually
        // affects system operation; individual sensor readings cannot.
        if (androidSevereThermalLimiting) {
            return new Decision(Tone.CAUTION, gr
                    ? "Το Android ανέφερε σοβαρό θερμικό περιορισμό της λειτουργίας του συστήματος. Πρόκειται για τεκμηριωμένη λειτουργική επίπτωση, όχι όμως για απόδειξη της αιτίας ή μόνιμης βλάβης. Ελέγξτε ξανά μετά την ψύξη."
                    : "Android reported severe thermal limiting of system operation. This is documented functional impact, but not proof of the cause or permanent damage. Recheck after cooling.");
        }
        if (androidThermalLimiting) {
            return new Decision(Tone.CAUTION, gr
                    ? "Το Android ανέφερε ενεργό θερμικό περιορισμό. Η απόδοση του συστήματος μπορεί να επηρεάζεται προσωρινά· η αιτία και τυχόν μόνιμη βλάβη δεν τεκμηριώνονται από αυτό."
                    : "Android reported active thermal limiting. System performance may be temporarily affected; this does not establish the cause or permanent damage.");
        }
        if (chargingConcern && thermalConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Το σύστημα παρουσιάζει ευρήματα στη λειτουργία φόρτισης και στους διαθέσιμους θερμικούς τομείς. Ο συνδυασμός χρειάζεται επανέλεγχο, χωρίς να τεκμηριώνει αιτιώδη σχέση ή συνολική βλάβη συσκευής."
                    : "The system shows findings in charging operation and the available thermal areas. These require follow-up, without establishing causation or a device-wide fault.");
        }
        if (chargingConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Το σύστημα παρουσίασε εύρημα στη λειτουργία φόρτισης, που χρειάζεται επανέλεγχο. Δεν τεκμηριώνεται από αυτό πρόβλημα στη λειτουργία των υπόλοιπων υποσυστημάτων."
                    : "The system showed a charging-operation finding needing follow-up. This does not establish a malfunction of other subsystems.");
        }
        if (thermalConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Στους διαθέσιμους θερμικούς τομείς καταγράφηκε αυξημένη ένδειξη. Δεν επιβεβαιώθηκε από το Android σοβαρός θερμικός περιορισμός· δεν αποδεικνύεται γενικευμένη δυσλειτουργία του συστήματος. Επανέλεγχος στις ίδιες συνθήκες."
                    : "An elevated reading was recorded in the available thermal areas. Android did not confirm severe thermal limiting; a system-wide malfunction is not established. Recheck under comparable conditions.");
        }
        if (highAbsoluteStressBatteryTemperature) {
            return new Decision(Tone.CAUTION, gr
                    ? "Στο 14Α η μπαταρία έφτασε σε αυξημένη απόλυτη θερμοκρασία κατά την καταπόνηση, παρότι το μεταγενέστερο θερμικό στιγμιότυπο δεν ανέφερε πρόβλημα. Χρειάζεται επανέλεγχος· δεν αποδεικνύεται μόνιμη επίπτωση στο σύστημα."
                    : "Lab 14A recorded elevated absolute battery temperature under stress, even though the later thermal snapshot showed no warning. Follow-up is warranted; permanent system impact is not established.");
        }

        if (!completeCoverage || !chargingEvidenceAvailable || !thermalEvidenceAvailable) {
            return new Decision(Tone.NEUTRAL, gr
                    ? "Στα διαθέσιμα αποτελέσματα δεν καταγράφηκε συγκεκριμένη δυσλειτουργία φόρτισης ή θερμικός περιορισμός, αλλά η κάλυψη των ελεγχόμενων τομέων είναι μερική. Δεν μπορούμε να συμπεράνουμε συνολική υγεία ή επίπτωση στη λειτουργία του συστήματος."
                    : "No specific charging malfunction or thermal limiting was recorded in the available results, but coverage of the tested areas is incomplete. Overall system health or functional impact cannot be determined.");
        }

        boolean pronouncedSag = "Critical".equals(batteryStressClass)
                || independentElectricalConcern;
        String thermalRiseNote = largeStressTemperatureRise
                ? (gr
                    ? " Στο 14Α καταγράφηκε επιπλέον μεγάλη άνοδος θερμοκρασίας κατά το φορτίο, όχι όμως από μόνη της υπερθέρμανση."
                    : " Lab 14A also recorded a large rise under stress, which alone does not establish overheating.")
                : "";

        if (pronouncedSag) {
            return new Decision(Tone.CAUTION, gr
                    ? "Η μπαταρία παρουσίασε αυξημένη πτώση τάσης υπό φορτίο, όμως το 15 κατέγραψε σταθερή φόρτιση και το 16 φυσιολογικές ενδείξεις στους διαθέσιμους θερμικούς τομείς. Δεν τεκμηριώθηκε ότι η πτώση τάσης επηρεάζει τη λειτουργία του συστήματος στους τομείς που ελέγχθηκαν."
                            + thermalRiseNote
                            + " Συνιστάται συγκρίσιμος επανέλεγχος της μπαταρίας, όχι διάγνωση βλάβης όλου του συστήματος."
                    : "The battery showed increased voltage sag under load, but Lab 15 recorded stable charging and Lab 16 showed normal readings in the available thermal areas. The tests did not establish an effect of this voltage sag on system operation in the areas examined."
                            + thermalRiseNote
                            + " Repeat the battery test under comparable conditions; a device-wide fault is not established.");
        }

        if ("Weak".equals(batteryStressClass)) {
            return new Decision(Tone.POSITIVE, gr
                    ? "Η μπαταρία μπορεί να παρουσιάζει ενδείξεις συμβατές με φυσιολογική φθορά, χωρίς να καταγράφηκε επίπτωση στη λειτουργία του συστήματος στους ελεγχόμενους τομείς. Η φόρτιση ήταν σταθερή και οι διαθέσιμες θερμοκρασίες φυσιολογικές."
                            + thermalRiseNote
                            + " Αυτό δεν πιστοποιεί τη συνολική υγεία της συσκευής ή το SoH."
                    : "The battery may show signs consistent with ordinary ageing, without observed impact on system operation in the tested areas. Charging was stable and the available temperatures were normal."
                            + thermalRiseNote
                            + " This does not certify overall device health or battery SoH.");
        }

        boolean knownNormal = "Normal".equals(batteryStressClass)
                || "Good".equals(batteryStressClass)
                || "Excellent".equals(batteryStressClass);
        if (knownNormal) {
            return new Decision(Tone.POSITIVE, gr
                    ? "Ο συνδυασμός των αποτελεσμάτων δείχνει ικανοποιητική λειτουργία του συστήματος στους τομείς τροφοδοσίας, φόρτισης και θερμικής συμπεριφοράς που ελέγχθηκαν. Δεν καταγράφηκε συγκεκριμένη δυσλειτουργία ή θερμικός περιορισμός."
                            + thermalRiseNote
                            + " Δεν πρόκειται για πιστοποίηση της συνολικής υγείας της συσκευής."
                    : "Combined results indicate satisfactory system operation in the tested power, charging and thermal areas. No specific malfunction or thermal limiting was recorded."
                            + thermalRiseNote
                            + " This is not a certification of overall device health.");
        }
        return new Decision(Tone.NEUTRAL, gr
                ? "Η λειτουργία φόρτισης και οι διαθέσιμες θερμοκρασίες δεν έδειξαν συγκεκριμένο πρόβλημα, αλλά η απόκριση μπαταρίας στο 14Α δεν ταξινομήθηκε. Δεν τεκμηριώνεται συνολικό συμπέρασμα για το σύστημα."
                : "Charging and the available temperatures showed no specific issue, but the Lab 14A battery response was not classified. An overall system verdict is not established.");
    }
}
