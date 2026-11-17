package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Regression: intelligent Lab 17 synthesis must not equate Weak/sag with device harm. */
public final class Lab17CombinedConclusionTest {
    private static Lab17CombinedConclusion.Decision greek(
            String cls, boolean electrical, boolean charging, boolean thermal, boolean full) {
        return Lab17CombinedConclusion.evaluate(true, cls, electrical, charging, thermal, full);
    }

    @Test public void ownerScreenshotsWeakBatteryButNormalChargingAndThermalGetsGreen() {
        Lab17CombinedConclusion.Decision d = greek("Weak", false, false, false, true);
        assertEquals(Lab17CombinedConclusion.Tone.POSITIVE, d.tone);
        assertTrue(d.message.contains("φυσιολογική φθορά"));
        assertTrue(d.message.contains("δεν καταγράφηκε δυσλειτουργία στη φόρτιση"));
        assertTrue(d.message.contains("ικανοποιητική λειτουργία στους τομείς που ελέγχθηκαν"));
        assertFalse(d.message.contains("όλο το σύστημα είναι υγιές"));
    }

    @Test public void normalCombinedEvidenceGetsGroundedGreen() {
        Lab17CombinedConclusion.Decision d = greek("Normal", false, false, false, true);
        assertEquals(Lab17CombinedConclusion.Tone.POSITIVE, d.tone);
        assertTrue(d.message.contains("Συμπέρασμα για το σύστημα:"));
        assertTrue(d.message.contains("στους τομείς τροφοδοσίας, φόρτισης και θερμικής συμπεριφοράς"));
        assertTrue(d.message.contains("Η εκτίμηση περιορίζεται στους ελέγχους που πραγματοποιήθηκαν."));
    }

    @Test public void genuineBatteryConcernOverridesWeakReassurance() {
        Lab17CombinedConclusion.Decision d = greek("Weak", true, false, false, true);
        assertEquals(Lab17CombinedConclusion.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("μπαταρίας στην καταπόνηση χρειάζεται επανέλεγχο"));
        assertFalse(d.message.contains("ικανοποιητική λειτουργία"));
    }

    @Test public void criticalBatteryClassCannotBeCalledNormalAgeing() {
        Lab17CombinedConclusion.Decision d = greek("Critical", false, false, false, true);
        assertEquals(Lab17CombinedConclusion.Tone.CAUTION, d.tone);
        assertFalse(d.message.contains("φυσιολογική φθορά"));
    }

    @Test public void thermalProblemIsNeverMisattributedToBattery() {
        Lab17CombinedConclusion.Decision d = greek("Weak", false, false, true, true);
        assertEquals(Lab17CombinedConclusion.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("θερμική συμπεριφορά"));
        assertTrue(d.message.contains("Δεν αποδίδουμε το εύρημα"));
    }

    @Test public void chargingWarningTakesPriority() {
        Lab17CombinedConclusion.Decision d = greek("Weak", false, true, false, true);
        assertEquals(Lab17CombinedConclusion.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("Η φόρτιση"));
    }

    @Test public void incompleteCoverageRemainsNeutralEvenWithWeakBattery() {
        Lab17CombinedConclusion.Decision d = greek("Weak", false, false, false, false);
        assertEquals(Lab17CombinedConclusion.Tone.NEUTRAL, d.tone);
        assertTrue(d.message.contains("δεν αρκούν"));
    }

    @Test public void allThreeDomainsConcernTakesPriority() {
        Lab17CombinedConclusion.Decision d = greek("Critical", true, true, true, true);
        assertEquals(Lab17CombinedConclusion.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("μπαταρίας"));
        assertTrue(d.message.contains("φόρτιση"));
        assertTrue(d.message.contains("θερμική"));
    }

    @Test public void englishVersionDescribesLimitedEvidence() {
        Lab17CombinedConclusion.Decision d = Lab17CombinedConclusion.evaluate(
                false, "Weak", false, false, false, true);
        assertEquals(Lab17CombinedConclusion.Tone.POSITIVE, d.tone);
        assertTrue(d.message.contains("areas tested"));
    }

    @Test public void originalIntelligentSystemHealthTitleStaysUnchanged() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String s = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        assertTrue(s.contains("17. Ευφυής Ανάλυση Υγείας Συστήματος"));
        assertTrue(s.contains("17. Intelligent System Health Analysis"));
        assertTrue(s.contains("LAB 17 — Ευφυής Ανάλυση Υγείας Συστήματος"));
        assertTrue(s.contains("LAB 17 — Intelligent System Health Analysis"));
        assertTrue(s.contains("Lab17CombinedConclusion.evaluateDetailed("));
        assertTrue(s.contains("lab14StressClass"));
        assertTrue(s.contains("lab14CollapseObserved"));
        assertFalse(s.contains("Υπάρχει ένδειξη καταπόνησης ή θερμοκρασίας προς επαλήθευση"));
    }
}
