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
                    ? "Τα διαθέσιμα στοιχεία δεν επαρκούν για συμπέρασμα από αυτή τη δοκιμή. Επανάλαβέ τη σε συγκρίσιμες συνθήκες. Δεν τεκμηριώνεται από αυτό ανάγκη επισκευής ή αντικατάστασης."
                    : "This trial did not provide enough evidence for a conclusion. Repeat under comparable conditions; it does not establish a need for repair or replacement.");
        }

        // A large TEMPERATURE RISE is not the same as a HIGH ABSOLUTE
        // battery temperature. Both deserve a separate user-facing verdict.
        // 45°C is a conservative caution threshold, not proof of damage.
        boolean highAbsoluteTemp = Float.isFinite(batteryPeakC) && batteryPeakC >= 45f;
        boolean veryHighAbsoluteTemp = Float.isFinite(batteryPeakC) && batteryPeakC >= 50f;
        boolean largeTempRise = Float.isFinite(thermalRiseC) && thermalRiseC >= 6.5f;

        if (veryHighAbsoluteTemp) {
            return new Decision(Tone.CAUTION, greek
                    ? "Η μέγιστη θερμοκρασία της μπαταρίας ήταν υψηλή κατά τη δοκιμή. Άφησε τη συσκευή να κρυώσει πριν από νέα καταπόνηση. Αν παραμένει ασυνήθιστα θερμή ή επαναλαμβάνεται το εύρημα, ζήτησε τεχνικό έλεγχο. Σε διόγκωση, ασυνήθιστη οσμή ή έντονη υπερθέρμανση, διέκοψε τη χρήση."
                    : "Battery temperature reached a high absolute value during this test. Let the device cool before further stress. Seek assessment if it remains unusually hot or the finding recurs. Stop use if there is swelling, unusual smell or severe overheating.");
        }

        if (highAbsoluteTemp) {
            return new Decision(Tone.CAUTION, greek
                    ? "Η μέγιστη θερμοκρασία της μπαταρίας ήταν αυξημένη. Η μέτρηση δεν αποδεικνύει φθορά, αλλά συνιστάται ψύξη και επανάληψη σε συγκρίσιμες συνθήκες. Ζήτησε τεχνικό έλεγχο αν επανεμφανιστεί ή υπάρχουν συμπτώματα."
                    : "Peak battery temperature was elevated. This does not establish wear; allow cooling and retest under comparable conditions. Seek technical assessment if it recurs or symptoms occur.");
        }

        if ("Critical".equals(stressClass) || voltageDropFlag || swellingProxyFlag ||
                gaugeDiscrepancyFlag || electricalBehaviourFlag) {
            return new Decision(Tone.CAUTION, greek
                    ? "Καταγράφηκαν ενδείξεις αυξημένης πτώσης τάσης ή ηλεκτρικής ασυνέπειας υπό φορτίο. Μία δοκιμή δεν αποδεικνύει βλάβη ή ανάγκη αντικατάστασης. Επανάλαβε σε συγκρίσιμες συνθήκες και ζήτησε τεχνική εκτίμηση αν το εύρημα επιμένει ή εμφανίζονται συμπτώματα. Αν υπάρχει πραγματική διόγκωση, ασυνήθιστη οσμή ή έντονη υπερθέρμανση, σταμάτησε τη χρήση."
                    : "Increased voltage sag or electrical inconsistency was recorded under load. One test does not establish damage or a replacement need. Repeat under comparable conditions; seek assessment if the finding persists or symptoms occur. Stop use if there is actual swelling, unusual smell or severe overheating.");
        }

        if (largeTempRise) {
            return new Decision(Tone.CAUTION, greek
                    ? "Καταγράφηκε μεγάλη άνοδος θερμοκρασίας κατά την καταπόνηση, όχι όμως υψηλή απόλυτη θερμοκρασία μπαταρίας. Η άνοδος από μόνη της δεν αποδεικνύει υπερθέρμανση ή βλάβη. Επανέλαβε σε συγκρίσιμες συνθήκες και έλεγξε αν επαναλαμβάνεται."
                    : "A large temperature rise was recorded under stress, but the absolute battery temperature was not high. A rise alone does not establish overheating or damage. Repeat under comparable conditions and check whether it recurs.");
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
