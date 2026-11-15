package com.gel.cleaner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Narrow, evidence-backed extraction of iPhone panic clues. Never diagnoses parts.
 *
 * Public references and limitations:
 *  iFixit: https://www.ifixit.com/Wiki/iPhone_Kernel_Panics
 *  iFixit: https://www.ifixit.com/Wiki/iPhone_SMC_Panic_Assertion_Failed
 *  Song et al. (2025): https://dfrws.org/wp-content/uploads/2025/10/A-study-on-the-recovery-of-damaged-iPhone-h_2025_Forensic-Science-Internatio.pdf
 *
 * All mappings are tentative and model-scoped. A symptom recorded in an imported
 * panic is NOT proof that a physical part is defective or that a repair is needed.
 */
public final class ApplePanicHardwareHints {
    private ApplePanicHardwareHints() {}

    private static final Pattern MODEL = Pattern.compile("(?i)\\biphone\\d{1,2},\\d{1,2}\\b");
    private static final Pattern MISSING = Pattern.compile(
            "(?i)missing\\s+sensor(?:\\(s\\)|s)?\\s*[:=]\\s*([^\\r\\n\"}]{1,140})");
    private static final Pattern KNOWN_SENSOR = Pattern.compile(
            "(?i)\\b(mic1|mic2|prs0|tg0b|tg0v)\\b");
    private static final Pattern SMC_ARRAY = Pattern.compile(
            "(?i)\\bS\\.sensor\\s+array\\s+0\\s*-\\s*4\\s+is\\s*"
            + "(0x[0-9a-f]+|\\d+)\\s*,\\s*(0x[0-9a-f]+|\\d+)");
    private static final Pattern FALLBACK_ARRAY = Pattern.compile(
            "(?i)\\bsensor\\s+array\\s+0\\s*-\\s*4\\s+is\\s*"
            + "(0x[0-9a-f]+|\\d+)\\s*,\\s*(0x[0-9a-f]+|\\d+)");

    public static final class Finding {
        public final String code;
        public final String areaGreek;
        public final String areaEnglish;
        public final String checkGreek;
        public final String checkEnglish;
        public final boolean modelScoped;

        Finding(String code, String areaGreek, String areaEnglish,
                String checkGreek, String checkEnglish, boolean modelScoped) {
            this.code = code;
            this.areaGreek = areaGreek;
            this.areaEnglish = areaEnglish;
            this.checkGreek = checkGreek;
            this.checkEnglish = checkEnglish;
            this.modelScoped = modelScoped;
        }
        public String area(boolean greek) { return greek ? areaGreek : areaEnglish; }
        public String action(boolean greek) { return greek ? checkGreek : checkEnglish; }
    }

    public static final class Assessment {
        public final String modelIdentifier;
        public final List<Finding> findings;
        private Assessment(String modelIdentifier, List<Finding> findings) {
            this.modelIdentifier = modelIdentifier;
            this.findings = Collections.unmodifiableList(new ArrayList<>(findings));
        }
    }

    public static Assessment examineOne(String panicBlock) {
        if (!ApplePanicEvidencePolicy.hasPanicEvidence(panicBlock))
            return new Assessment("Unknown", Collections.emptyList());

        String model = "Unknown";
        Matcher modelMatch = MODEL.matcher(panicBlock);
        if (modelMatch.find()) model = modelMatch.group().toLowerCase(Locale.ROOT);

        String log = diagnosticText(panicBlock);
        String low = log.toLowerCase(Locale.ROOT);
        List<Finding> out = new ArrayList<>();

        // A named missing sensor must be explicitly qualified as "missing",
        // not merely present in metadata or in a raw hardware inventory.
        Matcher missing = MISSING.matcher(log);
        LinkedHashSet<String> sensors = new LinkedHashSet<>();
        while (missing.find()) {
            Matcher codes = KNOWN_SENSOR.matcher(missing.group(1));
            while (codes.find()) sensors.add(codes.group(1).toLowerCase(Locale.ROOT));
        }
        for (String sensor : sensors) out.add(sensorFinding(sensor, model));
        if (!out.isEmpty()) return new Assessment(model, out);

        if ((low.contains("smc panic") || low.contains("smc bsc failure"))
                && low.contains("sensor array")) {
            Matcher array = SMC_ARRAY.matcher(log);
            if (!array.find()) array = FALLBACK_ARRAY.matcher(log);
            if (array.find(0)) {
                long primary = toCode(array.group(1));
                long secondary = toCode(array.group(2));
                // The S array normally contains a single meaningful non-zero
                // value, often the second element; never sum unrelated fields.
                long code = secondary != 0L ? secondary : primary;
                if (code > 0 && code <= 0xFFFFFFFFL) {
                    out.add(smcFinding(code, model));
                    return new Assessment(model, out);
                }
            }
            out.add(general("SMC_SENSOR_ARRAY_UNPARSED",
                    "Υπάρχει ένδειξη SMC, αλλά δεν απομονώθηκε αξιόπιστος κωδικός αισθητήρα.",
                    "SMC is reported, but the sensor code could not be extracted reliably.",
                    "Εξέτασε το πλήρες panicString και τον ακριβή κωδικό μοντέλου.",
                    "Review the complete panicString and exact device identifier."));
            return new Assessment(model, out);
        }

        if (low.contains("aop panic") || low.contains("aop nmi power")) {
            out.add(general("AOP_PANIC",
                    "Συμβάν συνεπεξεργαστή AOP (αισθητήρες/χαμηλή κατανάλωση), όχι διάγνωση εξαρτήματος.",
                    "Always-on processor event (sensors / low power), not a part diagnosis.",
                    "Έλεγξε τις υπόλοιπες γραμμές του panic και πραγματικά συμπτώματα.",
                    "Inspect the rest of the panic and actual symptoms."));
        }
        if (Pattern.compile("(?i)\\bi2c[0-9]?\\b|\\bi²c\\b").matcher(log).find()
                && (low.contains("failure") || low.contains("timeout")
                    || low.contains("error") || low.contains("bus"))) {
            out.add(general("I2C_COMMUNICATION",
                    "Καταγράφεται πρόβλημα επικοινωνίας I2C, χωρίς ταυτοποίηση φυσικού εξαρτήματος.",
                    "I2C communication fault is logged; physical part is not identified.",
                    "Χρειάζεται αναγνώριση διαύλου/συσκευής και έλεγχος συνδέσεων από τεχνικό.",
                    "Locate the bus/device and verify connections with a technician."));
        }
        if (low.contains("ans2 recoverable panic") || low.contains("ans2 panic")
                || low.contains("nvme controller panic") || low.contains("apfs_vfsop")) {
            out.add(general("STORAGE_CONTROLLER",
                    "Ένδειξη ελεγκτή αποθήκευσης ή συστήματος αρχείων, όχι μετρημένη υγεία NAND.",
                    "Storage controller/filesystem clue, not measured NAND health.",
                    "Προτεραιότητα στην προστασία δεδομένων και επαλήθευση με πρόσθετα στοιχεία.",
                    "Prioritize data preservation and corroborate with further evidence."));
        }
        if (low.contains("sep rom") && low.contains("panic")) {
            out.add(general("SEP_ROM",
                    "Καταγράφεται συμβάν ασφαλούς επεξεργαστή (SEP ROM), χωρίς επιβεβαίωση αιτίας.",
                    "Secure Enclave ROM event is logged; cause is not confirmed.",
                    "Αποφυγή άσκοπων επεμβάσεων· αξιολόγηση από εξειδικευμένο τεχνικό.",
                    "Avoid speculative repair; seek qualified board-level assessment."));
        }
        if (low.contains("dcp serror") || low.contains("iomfb_video_async")) {
            out.add(general("DISPLAY_CONTROLLER",
                    "Ένδειξη ελεγκτή οθόνης/γραφικών, όχι απόδειξη βλάβης οθόνης.",
                    "Display / graphics controller hint, not proof of a failed screen.",
                    "Συσχέτισε με απώλειες εικόνας και έλεγξε λογισμικό και συνδέσεις.",
                    "Correlate with display symptoms; check software and connections."));
        }
        if (ApplePanicEvidencePolicy.hasExplicitBasebandFaultHint(panicBlock)) {
            out.add(general("BASEBAND_FAULT",
                    "Υπάρχει ρητή ένδειξη υποσυστήματος κινητής τηλεφωνίας.",
                    "An explicit cellular / baseband fault is mentioned.",
                    "Συσχέτισε με πραγματικά προβλήματα κλήσεων ή δικτύου.",
                    "Correlate with observed cellular or calling issues."));
        }
        if (low.contains("watchdog") && !low.contains("missing sensor")
                && !low.contains("smc bsc failure")) {
            out.add(general("WATCHDOG_NO_MISSING_SENSOR",
                    "Διακοπή ελέγχου απόκρισης (Watchdog), χωρίς συγκεκριμένο ελλείποντα αισθητήρα.",
                    "Watchdog check-in failure without a specific missing sensor.",
                    "Πρώτα συσχέτιση με iOS, εφαρμογές και επαναλήψεις· όχι αυτόματη αντικατάσταση υλικού.",
                    "Correlate with iOS, processes and recurrence before considering hardware."));
        }
        if (low.contains("thermal shutdown") && !low.contains("missing sensor")) {
            out.add(general("THERMAL_SHUTDOWN",
                    "Το αρχείο αναφέρει θερμική διακοπή· δεν έγινε μέτρηση θερμοκρασίας.",
                    "Thermal shutdown is recorded; no temperature was measured.",
                    "Επιβεβαίωση με συνθήκες χρήσης και πραγματικές θερμικές μετρήσεις.",
                    "Check real-world conditions and independent temperature evidence."));
        }
        return new Assessment(model, out);
    }

    private static String diagnosticText(String full) {
        // Prefer the actual panicString section over unrelated first-line metadata.
        Matcher marker = Pattern.compile("(?i)[\"']?panicString[\"']?\\s*[:=]\\s*").matcher(full);
        if (marker.find()) return full.substring(marker.end());
        Matcher p = Pattern.compile("(?i)panic\\s*\\(cpu").matcher(full);
        if (p.find()) return full.substring(p.start());
        return full;
    }

    private static long toCode(String s) {
        try {
            String v = s.toLowerCase(Locale.ROOT).trim();
            return v.startsWith("0x") ? Long.parseLong(v.substring(2), 16)
                                       : Long.parseLong(v, 10);
        } catch (NumberFormatException ex) {
            return -1L;
        }
    }

    private static Finding sensorFinding(String code, String model) {
        String genericGr = "Λογικό κανάλι αισθητήρα " + code
                + " δεν αποκρίθηκε. Η φυσική θέση εξαρτάται από το μοντέλο.";
        String genericEn = "Logical sensor " + code
                + " did not respond. Its physical path is model-specific.";
        String areaGr = genericGr, areaEn = genericEn;
        boolean mapped = false;

        // Only the iPhone 11 mapping is supported here by controlled testing
        // (Song et al. 2025) and the published repair knowledge base.
        if ("iphone12,1".equals(model)) {
            if ("mic2".equals(code)) {
                areaGr = "iPhone 11: πίσω μικρόφωνο, καλωδιοταινία κουμπιού ενεργοποίησης "
                        + "ή οι αντίστοιχες συνδέσεις.";
                areaEn = "iPhone 11: rear microphone, power-button flex or associated connectors.";
                mapped = true;
            } else if ("mic1".equals(code) || "prs0".equals(code)) {
                areaGr = "iPhone 11: υποσύστημα θύρας φόρτισης και συνδετήρας του (ένδειξη, όχι απόδειξη).";
                areaEn = "iPhone 11: charge-port assembly or its connector (clue, not proof).";
                mapped = true;
            }
        }
        return new Finding(code.toUpperCase(Locale.ROOT),
                areaGr, areaEn,
                "Έλεγξε πρώτα τη σύνδεση και, αν χρειάζεται, δοκίμασε συμβατό γνωστό λειτουργικό εξάρτημα. "
                        + "Δεν αποδεικνύεται βλάβη μόνο από τον κωδικό.",
                "Inspect the connector first, then test a compatible known-good part if appropriate. "
                        + "The code does not prove a part failure.",
                mapped);
    }

    private static Finding smcFinding(long code, String model) {
        String hex = "0x" + Long.toHexString(code).toUpperCase(Locale.ROOT);
        String gr = null, en = null;
        String id = model.toLowerCase(Locale.ROOT);

        boolean thirteen = any(id, "iphone14,2", "iphone14,3", "iphone14,4", "iphone14,5");
        boolean fourteen = any(id, "iphone14,7", "iphone14,8");
        boolean fourteenPro = any(id, "iphone15,2", "iphone15,3");
        boolean fifteenPro = any(id, "iphone16,1", "iphone16,2");
        boolean sixteenPro = any(id, "iphone17,1", "iphone17,2");

        if (thirteen) {
            if (code == 0x800L) { gr="θύρα φόρτισης"; en="charging port"; }
            else if (code == 0x10000L) { gr="συγκρότημα εμπρόσθιων αισθητήρων"; en="front sensor assembly"; }
            else if (code == 0x40000L) { gr="σύνδεση/δεδομένα μπαταρίας"; en="battery communication"; }
        } else if (fourteen) {
            if (code == 0x100000L) { gr="θύρα φόρτισης"; en="charging port"; }
            else if (code == 0x200000L) { gr="εμπρόσθιοι αισθητήρες"; en="front sensors"; }
            else if (code == 0x400000L) { gr="πηνίο ασύρματης φόρτισης"; en="wireless charging coil"; }
            else if (code == 0x300000L) { gr="θύρα φόρτισης και εμπρόσθιοι αισθητήρες"; en="charging port and front sensors"; }
            else if (code == 0x600000L) { gr="εμπρόσθιοι αισθητήρες και ασύρματη φόρτιση"; en="front sensors and wireless charging"; }
            else if (code == 0x700000L) { gr="θύρα φόρτισης, εμπρόσθιοι αισθητήρες και ασύρματη φόρτιση"; en="charging port, front sensors and wireless charging"; }
            // 0x500000 has conflicting published explanations; never silently assign parts.
        } else if (fourteenPro) {
            if (code == 0x41L) { gr="επικοινωνία μπαταρίας"; en="battery communication"; }
            else if (code == 0x40000L) { gr="θύρα φόρτισης"; en="charging port"; }
            else if (code == 0x80000L) { gr="εμπρόσθιοι αισθητήρες"; en="front sensors"; }
            else if (code == 0x100000L) { gr="καλωδιοταινία κουμπιού ενεργοποίησης"; en="power-button flex"; }
            else if (code == 0xC0000L) { gr="θύρα φόρτισης και εμπρόσθιοι αισθητήρες"; en="charging port and front sensors"; }
        } else if (fifteenPro) {
            if (code == 0x400000L) { gr="ασύρματη φόρτιση"; en="wireless charging"; }
            // A controlled experiment and published tables disagree for other 15 Pro
            // combinations, so they are deliberately not mapped to one part.
        } else if (sixteenPro) {
            if (code == 3145728L) { gr="θύρα φόρτισης"; en="charging port"; }
        }

        if (gr == null) return new Finding("SMC " + hex,
                "Κωδικός SMC " + hex + " για " + model
                        + " — δεν υπάρχει ασφαλής, μοναδική αντιστοίχιση εξαρτήματος.",
                "SMC code " + hex + " on " + model
                        + " — no sufficiently reliable model-specific part mapping.",
                "Διασταύρωσε τον ακριβή κωδικό μοντέλου και τον πλήρη πίνακα SMC. "
                        + "Μην αγοράσεις ανταλλακτικό μόνο από τον αριθμό.",
                "Check exact model and complete SMC code reference. Do not order parts from this code alone.",
                false);

        return new Finding("SMC " + hex,
                "Στο " + model + " ο κωδικός " + hex + " συνδέεται στις δημοσιευμένες "
                        + "αναφορές με: " + gr + ". Όχι επιβεβαιωμένη βλάβη.",
                "On " + model + ", published evidence associates " + hex
                        + " with: " + en + ". Not a confirmed fault.",
                "Επιθεώρησε συνδέσεις και δοκίμασε συμβατό γνωστό λειτουργικό ανταλλακτικό "
                        + "πριν συναγάγεις βλάβη μητρικής.",
                "Inspect connections and test a compatible known-good part before inferring a board fault.",
                true);
    }

    private static boolean any(String model, String... choices) {
        for (String choice : choices) if (choice.equals(model)) return true;
        return false;
    }

    private static Finding general(String code, String areaGr, String areaEn,
            String checkGr, String checkEn) {
        return new Finding(code, areaGr, areaEn, checkGr, checkEn, false);
    }
}
