package com.gel.cleaner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Source-aware interpretation of one Lab16 thermal snapshot.
 *
 * These deliberately coarse screening thresholds are NOT manufacturer thermal
 * limits, nor proof of fault. CPU/GPU chip readings are not interchangeable
 * with battery, device-surface, PMIC or in-device charger/USB zone readings.
 * Android's own thermal-limitation state is independent evidence.
 */
public final class Lab16ThermalCorrelation {
    private Lab16ThermalCorrelation() {}

    public static final String[] LABELS = {
            "Battery", "CPU", "GPU", "Charger", "PMIC", "Device surface"
    };
    public static final int BATTERY = 0, CPU = 1, GPU = 2,
            CHARGER = 3, PMIC = 4, SURFACE = 5;
    public static final int N = LABELS.length;
    public static final int OS_MODERATE = 2, OS_SEVERE = 3;

    // Indicative watch and high-reading bands for the *type of sensor*.
    private static final float[] WATCH_C = {42f, 75f, 75f, 50f, 65f, 42f};
    private static final float[] HIGH_C  = {48f, 90f, 90f, 65f, 85f, 48f};

    public enum Level { UNKNOWN, NORMAL, ELEVATED, HIGH }

    public static final class Pair {
        public final int first;
        public final int second;
        private Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }
        public String names(boolean gr) {
            return label(first, gr) + " + " + label(second, gr);
        }
        public String details(boolean gr) {
            if (matches(CPU, CHARGER)) {
                return gr
                        ? "Ταυτόχρονη αυξημένη ένδειξη στην CPU και στην περιοχή φόρτισης. Δεν αποδεικνύεται αιτιότητα ή βλάβη φορτιστή."
                        : "Elevated CPU and in-device charging-zone readings coincide. Neither causation nor a faulty charger is established.";
            }
            if (matches(BATTERY, CHARGER)) {
                return gr
                        ? "Συνυπάρχει θέρμανση μπαταρίας και περιοχής φόρτισης· επανέλεγχος σε ηρεμία ή κατά ελεγχόμενη φόρτιση."
                        : "Battery and in-device charging-zone heat coincide; repeat at rest or during controlled charging.";
            }
            if (matches(CPU, GPU)) {
                return gr
                        ? "Παράλληλη θέρμανση CPU και GPU· συμβατή με φορτίο επεξεργασίας, χωρίς απόδειξη της αιτίας."
                        : "CPU and GPU are both elevated; this can accompany processing load but does not establish its cause.";
            }
            if (matches(PMIC, CHARGER)) {
                return gr
                        ? "Παράλληλη θέρμανση διαχείρισης ισχύος και περιοχής φόρτισης· δεν αποδεικνύεται βλάβη."
                        : "Power-management and charging-zone readings are both elevated; no fault is proven.";
            }
            if (matches(BATTERY, SURFACE)) {
                return gr
                        ? "Αυξημένες ενδείξεις μπαταρίας και επιφάνειας· ο χρήστης μπορεί να αισθάνεται τη συσκευή θερμότερη."
                        : "Battery and surface readings are elevated; the device may feel warm.";
            }
            if (matches(BATTERY, CPU)) {
                return gr
                        ? "Η μπαταρία και η CPU είναι θερμότερες ταυτόχρονα. Δεν γνωρίζουμε αν συνδέονται αιτιωδώς· σύγκρινε μετρήσεις σε ηρεμία."
                        : "Battery and CPU are simultaneously warm. Causation is unknown; compare against an idle reading.";
            }
            if (matches(BATTERY, GPU)) {
                return gr
                        ? "Η μπαταρία και η GPU παρουσιάζουν αυξημένες ενδείξεις. Χρειάζεται σύγκριση με χαμηλό φόρτο πριν αποδοθεί αιτία."
                        : "Battery and GPU temperatures are both elevated. Compare under low load before attributing a cause.";
            }
            if (matches(BATTERY, PMIC)) {
                return gr
                        ? "Συνυπάρχουν υψηλότερες ενδείξεις στη μπαταρία και στο κύκλωμα διαχείρισης ισχύος· δεν αποδεικνύεται ηλεκτρική βλάβη."
                        : "Battery and power-management temperatures are elevated together; an electrical fault is not proven.";
            }
            if (matches(CPU, PMIC)) {
                return gr
                        ? "Η CPU και η περιοχή διαχείρισης ισχύος θερμαίνονται ταυτόχρονα· έλεγξε ξανά σε κατάσταση ηρεμίας, χωρίς αυτόματη διάγνωση βλάβης."
                        : "CPU and power-management readings are both elevated; repeat at idle without presuming a hardware defect.";
            }
            if (matches(CPU, SURFACE)) {
                return gr
                        ? "Η αυξημένη ένδειξη CPU συνυπάρχει με θερμότερη επιφάνεια. Δεν αποδεικνύεται ότι η CPU προκάλεσε τη θέρμανση."
                        : "CPU and device-surface readings are elevated together; the CPU is not established as the cause.";
            }
            if (matches(GPU, CHARGER)) {
                return gr
                        ? "Η GPU και η εσωτερική περιοχή φόρτισης παρουσιάζουν αυξημένες ενδείξεις. Απαιτείται έλεγχος υπό χαμηλό φορτίο."
                        : "GPU and internal charging-zone temperatures are elevated together; compare under light load.";
            }
            if (matches(GPU, PMIC)) {
                return gr
                        ? "Η GPU και η διαχείριση ισχύος παρουσιάζουν ταυτόχρονα αυξημένες ενδείξεις· δεν πιστοποιείται αιτιώδης σχέση."
                        : "GPU and power-management readings are elevated simultaneously; causation is not established.";
            }
            if (matches(GPU, SURFACE)) {
                return gr
                        ? "Η αυξημένη ένδειξη GPU συνυπάρχει με θερμότερη επιφάνεια· η προέλευση της θερμότητας παραμένει άγνωστη."
                        : "GPU and surface readings are elevated together; the source of the surface heat is unknown.";
            }
            if (matches(CHARGER, SURFACE)) {
                return gr
                        ? "Θέρμανση στην εσωτερική περιοχή φόρτισης και στην επιφάνεια· έλεγξε και κατά φόρτιση και χωρίς φορτιστή."
                        : "The internal charging zone and device surface are warm; compare while charging and unplugged.";
            }
            if (matches(PMIC, SURFACE)) {
                return gr
                        ? "Η περιοχή διαχείρισης ισχύος και η επιφάνεια παρουσιάζουν αυξημένες ενδείξεις· χρειάζεται επανάληψη για εντοπισμό μοτίβου."
                        : "Power-management and surface temperatures are elevated together; repeat to establish a pattern.";
            }
            return gr
                    ? "Δύο θερμικοί τομείς έχουν αυξημένες ενδείξεις στο ίδιο στιγμιότυπο· η αιτία δεν επιβεβαιώνεται."
                    : "Two thermal areas are elevated in the same snapshot; the cause is unverified.";
        }
        private boolean matches(int a, int b) {
            return (first == a && second == b) || (first == b && second == a);
        }
    }

    public static final class Summary {
        public final float[] temperatures;
        public final Level[] levels;
        public final List<Pair> elevatedPairs;
        public final int measuredCount;
        public final int elevatedCount;
        public final int androidThermalState;
        public final boolean hiddenWarning;

        private Summary(float[] t, Level[] l, List<Pair> pairs, int measured,
                        int elevated, int state, boolean hidden) {
            temperatures = t;
            levels = l;
            elevatedPairs = Collections.unmodifiableList(pairs);
            measuredCount = measured;
            elevatedCount = elevated;
            androidThermalState = state;
            hiddenWarning = hidden;
        }

        public boolean hasWarning() {
            return elevatedCount > 0 || androidThermalState >= OS_MODERATE || hiddenWarning;
        }
        public boolean hasSevereAndroidLimit() {
            return androidThermalState >= OS_SEVERE;
        }
        public boolean hasChipReading() {
            return levels[CPU] != Level.UNKNOWN || levels[GPU] != Level.UNKNOWN;
        }

        public String combinedMessage(boolean gr) {
            StringBuilder sb = new StringBuilder();
            if (elevatedCount == 0) {
                if (measuredCount == 0) {
                    sb.append(gr ? "Δεν υπάρχουν διαθέσιμες αριθμητικές θερμικές ενδείξεις."
                            : "No numeric sensor temperatures were available.");
                } else {
                    sb.append(gr
                            ? "Οι διαθέσιμοι θερμικοί τομείς δεν έδειξαν ταυτόχρονη αυξημένη θερμοκρασία."
                            : "The available thermal areas did not show simultaneous elevated temperatures.");
                }
            } else {
                sb.append(gr ? "Αυξημένες ενδείξεις: " : "Elevated readings: ");
                boolean comma = false;
                for (int i = 0; i < N; i++) {
                    if (levels[i] == Level.ELEVATED || levels[i] == Level.HIGH) {
                        if (comma) sb.append(", ");
                        sb.append(label(i, gr)).append(" ")
                          .append(String.format(Locale.US, "%.1f°C", temperatures[i]));
                        comma = true;
                    }
                }
                sb.append(".");
            }
            if (hasSevereAndroidLimit()) {
                sb.append(gr
                        ? " Το Android αναφέρει σοβαρή θερμική προστασία: υπάρχει καταγεγραμμένη επίπτωση στη λειτουργία."
                        : " Android reports severe thermal protection: an operational thermal limitation is documented.");
            } else if (androidThermalState >= OS_MODERATE) {
                sb.append(gr
                        ? " Το Android αναφέρει ενεργό θερμικό περιορισμό."
                        : " Android reports active thermal limiting.");
            }
            if (hiddenWarning) {
                sb.append(gr
                        ? " Επιπλέον, εντοπίστηκε αυξημένη ένδειξη σε άλλη προσβάσιμη θερμική ζώνη."
                        : " Another accessible thermal zone also showed an elevated reading.");
            }
            if (elevatedPairs.size() > 0) {
                sb.append(gr
                        ? " Οι ταυτόχρονες ενδείξεις δεν αποδεικνύουν ποιο υποσύστημα προκάλεσε τη θέρμανση."
                        : " Simultaneous readings do not establish which subsystem caused the heating.");
            }
            return sb.toString();
        }
    }

    public static float[] emptyTemperatures() {
        float[] values = new float[N];
        for (int i=0; i<N; i++) values[i] = Float.NaN;
        return values;
    }

    public static int index(String label) {
        if (label == null) return -1;
        for (int i=0; i<N; i++) if (LABELS[i].equals(label)) return i;
        return -1;
    }

    public static String label(int i, boolean greek) {
        if (!greek) return LABELS[i];
        switch (i) {
            case BATTERY: return "Μπαταρία";
            case CPU: return "CPU";
            case GPU: return "GPU";
            case CHARGER: return "Περιοχή φόρτισης";
            case PMIC: return "PMIC";
            case SURFACE: return "Επιφάνεια";
            default: throw new IllegalArgumentException("sensor index: " + i);
        }
    }

    public static boolean plausible(float temp) {
        return Float.isFinite(temp) && temp > 0f && temp <= 120f;
    }

    public static void capture(float[] values, String name, float temp) {
        int i = index(name);
        if (i < 0 || !plausible(temp)) return;
        if (!plausible(values[i]) || values[i] < temp) values[i] = temp;
    }

    public static Level level(int index, float temp) {
        if (index < 0 || index >= N || !plausible(temp)) return Level.UNKNOWN;
        if (temp >= HIGH_C[index]) return Level.HIGH;
        if (temp >= WATCH_C[index]) return Level.ELEVATED;
        return Level.NORMAL;
    }

    public static Summary evaluate(float[] values, int androidThermalState,
                                   boolean hiddenWarning) {
        if (values == null || values.length < N)
            throw new IllegalArgumentException("six thermal sectors required");
        float[] ts = new float[N];
        Level[] levels = new Level[N];
        int seen = 0, elevated = 0;
        for (int i=0; i<N; i++) {
            ts[i] = values[i];
            levels[i] = level(i, ts[i]);
            if (levels[i] != Level.UNKNOWN) seen++;
            if (levels[i] == Level.ELEVATED || levels[i] == Level.HIGH) elevated++;
        }
        List<Pair> pairs = new ArrayList<>();
        for (int i=0; i<N; i++) {
            if (levels[i] == Level.NORMAL || levels[i] == Level.UNKNOWN) continue;
            for (int j=i+1; j<N; j++) {
                if (levels[j] != Level.NORMAL && levels[j] != Level.UNKNOWN) {
                    pairs.add(new Pair(i, j));
                }
            }
        }
        return new Summary(ts, levels, pairs, seen, elevated,
                androidThermalState, hiddenWarning);
    }
}
