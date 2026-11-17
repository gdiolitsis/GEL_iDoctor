package com.gel.cleaner;

/**
 * User-facing interpretation of the Lab 14A stress-test evidence.
 * No battery SoH, remaining lifetime or unconditional safety is inferred.
 * Pure Java so all decision paths can be tested without Android hardware.
 */
public final class Lab14HumanVerdict {
    private Lab14HumanVerdict() {}

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
            boolean greek,
            String stressClass,
            boolean fullValidRun,
            boolean systemLimited,
            boolean voltageDropFlag,
            boolean swellingProxyFlag,
            boolean gaugeDiscrepancyFlag,
            boolean electricalBehaviourFlag,
            float thermalRiseC,
            float batteryPeakC
    ) {
        boolean knownClass = "Weak".equals(stressClass) ||
                "Critical".equals(stressClass) ||
                "Normal".equals(stressClass) ||
                "Good".equals(stressClass) ||
                "Excellent".equals(stressClass);

        if (!fullValidRun || systemLimited || !knownClass) {
            return new Decision(Tone.NEUTRAL, greek
                    ? "Τα διαθέσιμα στοιχεία δεν επαρκούν για να κρίνουμε αν η μπαταρία μπορεί να συνεχίσει να χρησιμοποιείται χωρίς πρόσθετο έλεγχο. Επανάλαβε τη δοκιμή σε συγκρίσιμες συνθήκες."
                    : "The available readings do not establish whether the battery can continue in use without further assessment. Repeat the test under comparable conditions.");
        }

        boolean elevatedThermal = (Float.isFinite(thermalRiseC) && thermalRiseC >= 6.5f) ||
                (Float.isFinite(batteryPeakC) && batteryPeakC >= 45f);

        if ("Critical".equals(stressClass) || voltageDropFlag || swellingProxyFlag ||
                gaugeDiscrepancyFlag || electricalBehaviourFlag || elevatedThermal) {
            return new Decision(Tone.CAUTION, greek
                    ? "Η μπαταρία σου εμφάνισε ενδείξεις που χρειάζονται επανέλεγχο. Δεν αποδεικνύεται ανάγκη αντικατάστασης, αλλά συνιστάται τεχνική εκτίμηση πριν βασιστείς σε αυτή τη δοκιμή για τη συνέχιση χρήσης. Αν εμφανιστεί υπερθέρμανση, διόγκωση, ασυνήθιστη οσμή ή ξαφνικό σβήσιμο, σταμάτησε τη χρήση και ζήτησε έλεγχο."
                    : "Your battery showed findings that require verification. Replacement is not established, but technical assessment is recommended before relying on this test to continue use. Stop using the device and seek inspection if overheating, swelling, unusual smell or unexpected shutdowns occur.");
        }

        if (!Float.isFinite(thermalRiseC) || thermalRiseC < -2f || thermalRiseC > 20f) {
            return new Decision(Tone.NEUTRAL, greek
                    ? "Η ηλεκτρική απόκριση καταγράφηκε, αλλά δεν υπάρχει επαρκής θερμική μέτρηση για συνολικό συμπέρασμα σχετικά με τη συνέχιση χρήσης. Επανάλαβε τη δοκιμή."
                    : "The electrical response was recorded, but temperature evidence is insufficient for an overall continued-use recommendation. Repeat the test.");
        }

        if ("Weak".equals(stressClass)) {
            return new Decision(Tone.POSITIVE, greek
                    ? "Η μπαταρία σου παρουσιάζει ενδείξεις φυσιολογικής φθοράς, με αυξημένη πτώση τάσης υπό φορτίο. Στη συγκεκριμένη δοκιμή η θερμοκρασία και οι υπόλοιπες ηλεκτρικές ενδείξεις παρέμειναν σε αποδεκτά επίπεδα. Μπορείς να συνεχίσεις να τη χρησιμοποιείς κανονικά, εφόσον δεν παρουσιάζει υπερθέρμανση, διόγκωση, ασυνήθιστη οσμή ή απρόσμενες διακοπές λειτουργίας. Δεν προκύπτει προς το παρόν ανάγκη αντικατάστασης."
                    : "Your battery showed elevated voltage sag under load, but this trial recorded no additional concerning electrical or thermal findings. You can continue normal use provided there is no overheating, swelling, unusual smell or unexpected shutdown. These findings do not establish an immediate need for replacement.");
        }

        return new Decision(Tone.POSITIVE, greek
                ? "Η μπαταρία σου ανταποκρίθηκε φυσιολογικά στις μετρήσεις της συγκεκριμένης δοκιμής. Μπορείς να συνεχίσεις την κανονική χρήση, εφόσον δεν εμφανίζει υπερθέρμανση, διόγκωση, ασυνήθιστη οσμή ή ξαφνικά σβησίματα. Η δοκιμή δεν πιστοποιεί τη συνολική ασφάλεια ή το ποσοστό φθοράς της."
                : "Your battery responded normally to the readings taken in this trial. You can continue normal use provided there is no overheating, swelling, unusual smell or unexpected shutdown. This test does not certify overall safety or the battery's state of health.");
    }
}
