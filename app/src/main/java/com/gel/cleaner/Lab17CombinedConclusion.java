package com.gel.cleaner;

/**
 * Evidence-only interpretation of Lab 14A + 15 + 16.
 * Weak short-load voltage response may indicate ordinary battery ageing,
 * but it is not by itself evidence of a device fault or a measured SoH.
 * Every conclusion is limited to the tested battery/charging/thermal areas.
 */
public final class Lab17CombinedConclusion {
    private Lab17CombinedConclusion() {}

    public enum Tone { POSITIVE, CAUTION, NEUTRAL }

    public static final class Decision {
        public final Tone tone;
        public final String message;
        private Decision(Tone tone, String message) {
            this.tone = tone;
            this.message = message;
        }
    }

    /**
     * Full evidence-based reasoning. Repeatability is measurement quality,
     * not an independent electrical fault. Voltage, gauge and stress proxies
     * must remain distinguishable rather than being presented as proof of harm.
     */
    public static Decision evaluateDetailed(
            boolean gr, String stressClass,
            boolean unstableBetweenRuns,
            boolean corroboratedVoltageFlag,
            boolean gaugeDiscrepancyFlag,
            boolean experimentalStressProxyFlag,
            boolean electricalBehaviourFlag,
            boolean stressThermalConcern,
            boolean stressThermalEvidenceAvailable,
            boolean chargingConcern,
            boolean thermalConcern,
            boolean completeCoverage) {

        boolean directBatteryConcern = "Critical".equals(stressClass)
                || corroboratedVoltageFlag
                || gaugeDiscrepancyFlag
                || experimentalStressProxyFlag
                || electricalBehaviourFlag;
        boolean anyThermalConcern = thermalConcern || stressThermalConcern;
        boolean weak = "Weak".equals(stressClass);

        // Independent abnormalities always win over the word "Weak" or an
        // unremarkable result from another lab. Mention the affected domains.
        if (directBatteryConcern || chargingConcern || anyThermalConcern) {
            StringBuilder msg = new StringBuilder();
            if (gr) {
                if (directBatteryConcern) {
                    if ("Critical".equals(stressClass)) {
                        msg.append("Η μπαταρία παρουσίασε αυξημένη πτώση τάσης στο συγκεκριμένο φορτίο και χρειάζεται επανέλεγχο· δεν αποδεικνύεται από αυτό πρόβλημα του συστήματος. ");
                    } else if (corroboratedVoltageFlag) {
                        msg.append("Η απόκριση της μπαταρίας στην καταπόνηση χρειάζεται επανέλεγχο: καταγράφηκε συνδυασμένη ηλεκτρική ένδειξη. ");
                    }
                    if (gaugeDiscrepancyFlag) {
                        msg.append("Η ένδειξη στάθμης φόρτισης παρουσίασε πιθανή ασυμφωνία. ");
                    }
                    if (experimentalStressProxyFlag) {
                        msg.append("Υπάρχει πρόσθετος έμμεσος δείκτης προς επιβεβαίωση, όχι απόδειξη φυσικής βλάβης. ");
                    }
                    if (electricalBehaviourFlag) {
                        msg.append("Το 14Α κατέγραψε πρόσθετη ηλεκτρική συμπεριφορά προς επανέλεγχο. ");
                    }
                } else if (weak) {
                    msg.append("Η μπαταρία μπορεί να παρουσιάζει φυσιολογική φθορά υπό φορτίο. ");
                }
                if (chargingConcern) msg.append("Η φόρτιση παρουσιάζει εύρημα που χρειάζεται επανέλεγχο. ");
                if (stressThermalConcern) {
                    msg.append("Κατά το 14Α καταγράφηκε θερμική μεταβολή υπό φορτίο, η οποία δεν αποδεικνύει από μόνη της υπερθέρμανση ή δυσλειτουργία του συστήματος. ");
                }
                if (thermalConcern) msg.append("Το 16 κατέγραψε θερμικό εύρημα που χρειάζεται επανέλεγχο. ");
                if (!anyThermalConcern && !chargingConcern && directBatteryConcern) {
                    msg.append("Δεν καταγράφηκε πρόσθετη προειδοποίηση στη φόρτιση ή στη θερμική συμπεριφορά. ");
                }
                if (!directBatteryConcern && !chargingConcern && anyThermalConcern) {
                    msg.append("Δεν αποδίδουμε το θερμικό εύρημα αυτόματα στη φθορά της μπαταρίας. ");
                }
                if (unstableBetweenRuns) {
                    msg.append("Υπάρχει επίσης μεταβλητότητα μεταξύ εκτελέσεων, επομένως χρειάζεται συγκρίσιμη επανάληψη. ");
                }
                msg.append("Από τις διαθέσιμες δοκιμές δεν αποδεικνύεται συγκεκριμένη βλάβη ούτε η αιτία της.");
            } else {
                if (directBatteryConcern) {
                    if ("Critical".equals(stressClass)) {
                        msg.append("The battery showed elevated voltage sag under the tested load and warrants follow-up; this alone does not establish system malfunction. ");
                    } else if (corroboratedVoltageFlag) {
                        msg.append("The battery's stress response requires follow-up after corroborated electrical findings. ");
                    }
                    if (gaugeDiscrepancyFlag) msg.append("The battery gauge showed a possible discrepancy. ");
                    if (experimentalStressProxyFlag) {
                        msg.append("An additional indirect indicator needs verification; it is not proof of physical damage. ");
                    }
                    if (electricalBehaviourFlag) {
                        msg.append("Lab 14A recorded additional electrical behaviour requiring follow-up. ");
                    }
                } else if (weak) {
                    msg.append("The battery may show ordinary ageing under load. ");
                }
                if (chargingConcern) msg.append("Charging has a finding that needs follow-up. ");
                if (stressThermalConcern) {
                    msg.append("Lab 14A recorded a temperature change under load, which alone does not establish overheating or system malfunction. ");
                }
                if (thermalConcern) msg.append("Lab 16 recorded a thermal finding needing follow-up. ");
                if (!anyThermalConcern && !chargingConcern && directBatteryConcern) {
                    msg.append("No additional charging or thermal warning was recorded. ");
                }
                if (!directBatteryConcern && !chargingConcern && anyThermalConcern) {
                    msg.append("A thermal finding cannot automatically be attributed to battery ageing. ");
                }
                if (unstableBetweenRuns) {
                    msg.append("Variation between trials also calls for a repeat under comparable conditions. ");
                }
                msg.append("These readings establish neither a specific fault nor its cause.");
            }
            return new Decision(Tone.CAUTION, msg.toString());
        }

        if (!stressThermalEvidenceAvailable) {
            return new Decision(Tone.NEUTRAL, gr
                    ? "Το 14Α δεν διέθετε επαρκή μέτρηση θερμικής μεταβολής υπό φορτίο. Δεν καταγράφηκε συγκεκριμένη δυσλειτουργία φόρτισης ή θερμοκρασίας, αλλά δεν δικαιολογείται συνολικό πράσινο συμπέρασμα χωρίς επαλήθευση αυτής της μέτρησης."
                    : "Lab 14A lacked a reliable under-load temperature-change reading. No specific charging or thermal malfunction was recorded, but an overall positive conclusion requires this reading to be verified.");
        }

        if (unstableBetweenRuns) {
            return new Decision(Tone.NEUTRAL, gr
                    ? "Η μπαταρία μπορεί να παρουσιάζει φθορά, όμως οι διαφορές μεταξύ εκτελέσεων δεν επιτρέπουν ασφαλή συνδυαστική αξιολόγηση. Δεν καταγράφηκε συγκεκριμένη δυσλειτουργία φόρτισης ή θερμοκρασίας. Επανάλαβε το 14Α σε συγκρίσιμες συνθήκες."
                    : "The battery may show ageing, but variation between trials prevents a reliable combined assessment. No specific charging or thermal malfunction was recorded. Repeat Lab 14A under comparable conditions.");
        }

        // Use the existing limited-coverage and positive wording for a stable
        // three-lab reading; no global system-health or SoH guarantee.
        return evaluate(gr, stressClass, false, false, false, completeCoverage);
    }

    public static Decision evaluate(boolean gr, String stressClass,
                                    boolean independentStressConcern,
                                    boolean chargingConcern,
                                    boolean thermalConcern,
                                    boolean completeCoverage) {
        final boolean critical = "Critical".equals(stressClass);
        final boolean weak = "Weak".equals(stressClass);
        final boolean known = weak || critical || "Normal".equals(stressClass)
                || "Good".equals(stressClass) || "Excellent".equals(stressClass);
        final boolean batteryConcern = critical || independentStressConcern;

        // A specific warning takes precedence over broad reassurance.
        if (chargingConcern && thermalConcern && batteryConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Υπάρχουν ανεξάρτητες ενδείξεις προς επανέλεγχο στην απόκριση της μπαταρίας, στη φόρτιση και στη θερμική συμπεριφορά. Δεν αποδεικνύεται συγκεκριμένη βλάβη, αλλά χρειάζεται πρόσθετος έλεγχος."
                    : "Battery stress response, charging and thermal behaviour all have findings that warrant follow-up. A specific fault is not established, but further checks are needed.");
        }
        if (chargingConcern && thermalConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Η φόρτιση και η θερμική συμπεριφορά παρουσιάζουν ευρήματα που χρειάζονται επανέλεγχο. Δεν τεκμηριώνεται συγκεκριμένη αιτία βλάβης."
                    : "Charging and thermal behaviour have findings that warrant follow-up. No specific fault cause has been established.");
        }
        if (chargingConcern && batteryConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Καταγράφηκαν ενδείξεις προς επανέλεγχο στη φόρτιση και στην απόκριση της μπαταρίας υπό φορτίο."
                    : "Charging and battery response under load both have findings that warrant follow-up.");
        }
        if (thermalConcern && batteryConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Καταγράφηκαν ενδείξεις προς επανέλεγχο στη θερμική συμπεριφορά και στην απόκριση της μπαταρίας υπό φορτίο."
                    : "Thermal behaviour and battery response under load both have findings that warrant follow-up.");
        }
        if (chargingConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Η φόρτιση παρουσιάζει ενδείξεις που χρειάζονται επανέλεγχο. Η καταγραφή δεν αποδεικνύει βλάβη της συσκευής."
                    : "Charging has findings that warrant follow-up. The readings do not prove a device fault.");
        }
        if (thermalConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Η θερμική συμπεριφορά παρουσιάζει ενδείξεις που χρειάζονται επανέλεγχο. Δεν αποδίδουμε το εύρημα αυτόματα στη μπαταρία."
                    : "Thermal behaviour has findings requiring verification. The cause is not automatically attributed to the battery.");
        }
        if (batteryConcern) {
            return new Decision(Tone.CAUTION, gr
                    ? "Η φόρτιση και οι διαθέσιμες θερμικές ενδείξεις δεν έδειξαν πρόσθετο πρόβλημα, αλλά η απόκριση της μπαταρίας στην καταπόνηση χρειάζεται επανέλεγχο."
                    : "Charging and available thermal readings did not show an additional issue, but the battery's stress response warrants follow-up.");
        }

        // Incomplete sensor/charge coverage must not be rewritten as proven good health.
        if (!completeCoverage || !known) {
            return new Decision(Tone.NEUTRAL, gr
                    ? (weak
                        ? "Η μπαταρία μπορεί να εμφανίζει φυσιολογική φθορά υπό φορτίο. Δεν καταγράφηκε πρόσθετη προειδοποίηση στη φόρτιση ή στη θερμοκρασία, αλλά τα διαθέσιμα στοιχεία δεν αρκούν για συνολικό θετικό συμπέρασμα."
                        : "Δεν καταγράφηκε συγκεκριμένη προειδοποίηση στα διαθέσιμα αποτελέσματα. Η διαγνωστική κάλυψη όμως είναι μερική, οπότε δεν τεκμηριώνεται συνολική αξιολόγηση.")
                    : (weak
                        ? "The battery may show ordinary ageing under load. No additional charging or thermal warning was recorded, but the available observations are insufficient for an overall positive conclusion."
                        : "No specific warning was recorded in the available results. Diagnostic coverage is incomplete, so an overall conclusion is not established."));
        }

        if (weak) {
            return new Decision(Tone.POSITIVE, gr
                    ? "Η μπαταρία παρουσιάζει ενδείξεις που μπορεί να σχετίζονται με φυσιολογική φθορά, αλλά δεν καταγράφηκε δυσλειτουργία στη φόρτιση ή σοβαρή θερμική ένδειξη. Στη συγκεκριμένη αξιολόγηση, δεν προέκυψε ότι η φθορά της μπαταρίας επηρεάζει αρνητικά τη λειτουργία του συστήματος στους τομείς που ελέγχθηκαν. Ο συνδυασμός των αποτελεσμάτων δείχνει ικανοποιητική λειτουργία στους τομείς που ελέγχθηκαν. Δεν προκύπτει από αυτές τις δοκιμές ανάγκη αντικατάστασης της μπαταρίας."
                    : "The battery shows signs that may reflect ordinary ageing, but no charging malfunction or serious thermal finding was recorded. This assessment found no evidence that battery ageing is adversely affecting system operation in the areas tested. Together, the results indicate satisfactory operation in the areas tested. These tests do not establish a need to replace the battery.");
        }

        // Unlike the ageing-specific path above, the normal/healthy battery
        // path previously omitted the actual SYSTEM verdict promised by Lab 17.
        // Describe only the battery/charging/thermal functions directly tested.
        return new Decision(Tone.POSITIVE, gr
                ? "Η μπαταρία ανταποκρίθηκε ικανοποιητικά στο συγκεκριμένο φορτίο, χωρίς τεκμηριωμένη δυσλειτουργία φόρτισης ή θερμική προειδοποίηση στις διαθέσιμες μετρήσεις. Συμπέρασμα για το σύστημα: Η λειτουργία του συστήματος στους τομείς τροφοδοσίας, φόρτισης και θερμικής συμπεριφοράς είναι ικανοποιητική. Δεν καταγράφηκε ένδειξη ότι οι συγκεκριμένοι τομείς επηρεάζουν αρνητικά τη λειτουργία του συστήματος. Η εκτίμηση περιορίζεται στους ελέγχους που πραγματοποιήθηκαν."
                : "The battery responded satisfactorily under the tested load, without a documented charging malfunction or thermal warning in the available readings. System verdict: System operation is satisfactory in the tested power, charging and thermal areas. No indication was recorded that these areas adversely affect system operation. This assessment is limited to the tests performed.");
    }
}
