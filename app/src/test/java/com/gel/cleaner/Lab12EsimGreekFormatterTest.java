package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/** Lab 12 Greek UI must not change the underlying Android modem evidence. */
public final class Lab12EsimGreekFormatterTest {
    @Test public void allNewEsimLabelsAreGreek() {
        assertEquals("Δυνατότητα eSIM (κατά Android)",
                Lab12EsimGreekFormatter.label("eSIM hardware capability"));
        assertEquals("Διαθεσιμότητα υπηρεσίας eSIM",
                Lab12EsimGreekFormatter.label("eSIM service available"));
        assertEquals("Ενεργές συνδρομές eSIM",
                Lab12EsimGreekFormatter.label("Active eSIM profiles"));
        assertEquals("Τύποι ενεργών συνδρομών",
                Lab12EsimGreekFormatter.label("Active subscription types"));
        assertEquals("Phone Type", Lab12EsimGreekFormatter.label("Phone Type"));
    }

    @Test public void negativeOrUnknownStatesNeverBecomeFalseHardwareAbsence() {
        assertEquals("Δεν δηλώνεται από το Android (δεν αποδεικνύει απουσία υλικού eSIM)",
                Lab12EsimGreekFormatter.value(
                        "Not declared by Android (not proof of physical absence)"));
        assertEquals("Απενεργοποιημένη / μη διαθέσιμη (δεν αποδεικνύει απουσία υλικού eSIM)",
                Lab12EsimGreekFormatter.value(
                        "Disabled / unavailable (not proof of hardware absence)"));
        assertEquals("0 στις ενεργές συνδρομές που εμφανίζει το Android",
                Lab12EsimGreekFormatter.value("0 in Android-visible active subscriptions"));
        assertEquals("Άγνωστο — δεν επιτρέπεται πρόσβαση στις συνδρομές",
                Lab12EsimGreekFormatter.value("Unknown — subscription access denied"));
        assertEquals("Άγνωστο — δεν παραχωρήθηκε άδεια κατάστασης τηλεφώνου",
                Lab12EsimGreekFormatter.value("Unknown — phone-state permission not granted"));
        assertEquals("Μη διαθέσιμο σε Android πριν από την έκδοση 9",
                Lab12EsimGreekFormatter.value("Not available from Android API < 28"));
    }

    @Test public void positiveEvidenceAndCountsRemainFaithful() {
        assertEquals("Υποστηρίζεται (το Android δηλώνει δυνατότητα eUICC)",
                Lab12EsimGreekFormatter.value("Supported (Android eUICC feature declared)"));
        assertEquals("Ενεργή (δεν συνεπάγεται ενεργή συνδρομή eSIM)",
                Lab12EsimGreekFormatter.value(
                        "Enabled (does not imply an active eSIM plan)"));
        assertEquals("2 ενεργές συνδρομές eSIM (κατά Android)",
                Lab12EsimGreekFormatter.value(
                        "2 active eSIM subscription(s) reported"));
        assertEquals("0 eSIM / 1 μη ενσωματωμένες SIM (χωρίς έλεγχο φυσικής θήκης)",
                Lab12EsimGreekFormatter.value(
                        "0 eSIM / 1 non-eSIM (not physical-tray verification)"));
        assertEquals("1 eSIM / 2 μη ενσωματωμένες SIM (χωρίς έλεγχο φυσικής θήκης)",
                Lab12EsimGreekFormatter.value(
                        "1 eSIM / 2 non-eSIM (not physical-tray verification)"));
        assertEquals("future modem status",
                Lab12EsimGreekFormatter.value("future modem status"));
    }

    @Test public void lab12OnlyRendersGreekAfterEnglishEvidenceClassification() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String source = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int a = source.indexOf("private void lab12ReportSharedModemInventory(boolean gr)");
        int b = source.indexOf("private void lab12ReportActiveSubscriptionTypes(boolean gr)", a);
        assertTrue(a >= 0 && b > a);
        String block = source.substring(a,b);
        assertTrue(block.contains("ModemEsimPresentationPolicy.isPositiveEvidence(label, value)"));
        assertTrue(block.contains("Lab12EsimGreekFormatter.label(label)"));
        assertTrue(block.contains("Lab12EsimGreekFormatter.value(value)"));
        assertTrue(block.contains("logLabelValue(displayLabel, displayValue)"));
        assertTrue(block.contains("logLabelOkValue(displayLabel, displayValue)"));
        assertTrue(block.contains("if (\"SIM slot interpretation\".equals(label)) continue;"));
        assertFalse(block.contains("logLabelValue(gr ? \"Ερμηνεία\""));
    }
}
