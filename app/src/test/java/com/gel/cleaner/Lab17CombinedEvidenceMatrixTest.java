package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Full decision matrix: ageing is not independently a fault, but any actual
 * warning, poor measurement quality or insufficient coverage must be honoured.
 */
public final class Lab17CombinedEvidenceMatrixTest {
    private Lab17CombinedConclusion.Decision decide(String cls,
            boolean repeatability, boolean voltage, boolean gauge, boolean proxy,
            boolean electrical, boolean hot14a, boolean thermalRead,
            boolean charging, boolean hot16, boolean coverage) {
        return Lab17CombinedConclusion.evaluateDetailed(true, cls, repeatability,
                voltage, gauge, proxy, electrical, hot14a, thermalRead,
                charging, hot16, coverage);
    }

    @Test public void normalAgeingWithNoObservedSystemImpactIsGreen() {
        Lab17CombinedConclusion.Decision d = decide("Weak",
                false,false,false,false,false,false,true,false,false,true);
        assertEquals(Lab17CombinedConclusion.Tone.POSITIVE, d.tone);
        assertTrue(d.message.contains("δεν προέκυψε ότι η φθορά της μπαταρίας επηρεάζει αρνητικά"));
        assertTrue(d.message.contains("στους τομείς που ελέγχθηκαν"));
        assertFalse(d.message.contains("όλο το σύστημα"));
    }

    @Test public void normalOrGoodExcellentEvidenceIsGreenAndNamesSystemOperation() {
        for (String cls : new String[] {"Normal", "Good", "Excellent"}) {
            Lab17CombinedConclusion.Decision d = decide(cls,
                    false,false,false,false,false,false,true,false,false,true);
            assertEquals(cls, Lab17CombinedConclusion.Tone.POSITIVE, d.tone);
            assertTrue(cls, d.message.contains("Συμπέρασμα για το σύστημα:"));
            assertTrue(cls, d.message.contains("λειτουργία του συστήματος"));
            assertTrue(cls, d.message.contains("τροφοδοσίας, φόρτισης και θερμικής συμπεριφοράς"));
            assertTrue(cls, d.message.contains("περιορίζεται στους ελέγχους"));
            assertFalse(cls, d.message.contains("όλα τα υποσυστήματα"));
            assertFalse(cls, d.message.contains("υγεία ολόκληρης της συσκευής"));
        }
    }

    @Test public void normalSystemVerdictDoesNotMaskPartialCoverageOrWarnings() {
        Lab17CombinedConclusion.Decision partial=decide("Normal",
                false,false,false,false,false,false,true,false,false,false);
        assertEquals(Lab17CombinedConclusion.Tone.NEUTRAL, partial.tone);
        assertFalse(partial.message.contains("Συμπέρασμα για το σύστημα:"));

        Lab17CombinedConclusion.Decision hot=decide("Normal",
                false,false,false,false,false,false,true,false,true,true);
        assertEquals(Lab17CombinedConclusion.Tone.CAUTION,hot.tone);
        assertFalse(hot.message.contains("ικανοποιητική."));

        Lab17CombinedConclusion.Decision charging=decide("Normal",
                false,false,false,false,false,false,true,true,false,true);
        assertEquals(Lab17CombinedConclusion.Tone.CAUTION,charging.tone);
        assertFalse(charging.message.contains("Συμπέρασμα για το σύστημα:"));
    }

    @Test public void explicitSystemVerdictAlsoExistsInEnglish() {
        Lab17CombinedConclusion.Decision d=Lab17CombinedConclusion.evaluateDetailed(
                false,"Normal",false,false,false,false,false,false,true,false,false,true);
        assertEquals(Lab17CombinedConclusion.Tone.POSITIVE,d.tone);
        assertTrue(d.message.contains("System verdict:"));
        assertTrue(d.message.contains("System operation"));
        assertTrue(d.message.contains("tested power, charging and thermal areas"));
        assertTrue(d.message.contains("limited to the tests performed"));
    }

    @Test public void everyIndependentBatterySignalOverridesWeakReassurance() {
        for (int bit=0; bit<4; bit++) {
            Lab17CombinedConclusion.Decision d = decide("Weak", false,
                    bit == 0, bit == 1, bit == 2, bit == 3, false, true,
                    false, false, true);
            assertEquals("signal " + bit, Lab17CombinedConclusion.Tone.CAUTION, d.tone);
            assertFalse(d.message.contains("ικανοποιητική λειτουργία"));
        }
    }

    @Test public void batteryThermalWarningOverridesNormalLab16() {
        Lab17CombinedConclusion.Decision d = decide("Weak",
                false,false,false,false,false,true,true,false,false,true);
        assertEquals(Lab17CombinedConclusion.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("Κατά το 14Α"));
    }

    @Test public void thermalSnapshotWarningDoesNotProveBadBattery() {
        Lab17CombinedConclusion.Decision d = decide("Weak",
                false,false,false,false,false,false,true,false,true,true);
        assertEquals(Lab17CombinedConclusion.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("Το 16 κατέγραψε"));
        assertTrue(d.message.contains("Δεν αποδίδουμε"));
    }

    @Test public void chargingWarningIsNotHiddenByNormalThermal() {
        Lab17CombinedConclusion.Decision d = decide("Weak",
                false,false,false,false,false,false,true,true,false,true);
        assertEquals(Lab17CombinedConclusion.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("φόρτιση"));
    }

    @Test public void multipleIndependentWarningsAreAllExplained() {
        Lab17CombinedConclusion.Decision d = decide("Critical",
                false,true,true,true,true,true,true,true,true,true);
        assertEquals(Lab17CombinedConclusion.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("κρίσιμη"));
        assertTrue(d.message.contains("ηλεκτρική"));
        assertTrue(d.message.contains("στάθμης"));
        assertTrue(d.message.contains("14Α"));
        assertTrue(d.message.contains("16"));
        assertTrue(d.message.contains("φόρτιση"));
    }

    @Test public void variableStressRunsAreNeutralNotInventedDamage() {
        Lab17CombinedConclusion.Decision d = decide("Weak",
                true,false,false,false,false,false,true,false,false,true);
        assertEquals(Lab17CombinedConclusion.Tone.NEUTRAL, d.tone);
        assertTrue(d.message.contains("διαφορές μεταξύ εκτελέσεων"));
        assertFalse(d.message.contains("ικανοποιητική λειτουργία"));
    }

    @Test public void missingStressThermalMeasurementCannotGetGreen() {
        Lab17CombinedConclusion.Decision d = decide("Weak",
                false,false,false,false,false,false,false,false,false,true);
        assertEquals(Lab17CombinedConclusion.Tone.NEUTRAL, d.tone);
        assertTrue(d.message.contains("14Α"));
        assertTrue(d.message.contains("θερμικής μεταβολής"));
    }

    @Test public void missingCoverageCannotGetGreen() {
        Lab17CombinedConclusion.Decision d = decide("Weak",
                false,false,false,false,false,false,true,false,false,false);
        assertEquals(Lab17CombinedConclusion.Tone.NEUTRAL, d.tone);
        assertTrue(d.message.contains("δεν αρκούν"));
    }

    @Test public void unknownStressClassCannotGetGreen() {
        Lab17CombinedConclusion.Decision d = decide(null,
                false,false,false,false,false,false,true,false,false,true);
        assertEquals(Lab17CombinedConclusion.Tone.NEUTRAL, d.tone);
    }

    @Test public void warningsStillTakePriorityWhenCoverageIsPartial() {
        Lab17CombinedConclusion.Decision d = decide("Weak",
                false,false,false,false,true,false,false,false,false,false);
        assertEquals(Lab17CombinedConclusion.Tone.CAUTION, d.tone);
    }

    @Test public void englishNuanceAndWarning() {
        Lab17CombinedConclusion.Decision green = Lab17CombinedConclusion.evaluateDetailed(
                false,"Weak",false,false,false,false,false,false,true,false,false,true);
        assertEquals(Lab17CombinedConclusion.Tone.POSITIVE, green.tone);
        assertTrue(green.message.contains("areas tested"));
        Lab17CombinedConclusion.Decision caution = Lab17CombinedConclusion.evaluateDetailed(
                false,"Weak",false,false,false,false,true,false,true,false,false,true);
        assertEquals(Lab17CombinedConclusion.Tone.CAUTION, caution.tone);
        assertTrue(caution.message.contains("electrical"));
    }

    @Test public void everyBatteryAndThermalWarningCombinationAvoidsGreen() {
        for(int bits=1;bits<128;bits++) {
            // Bits: measured voltage, gauge, proxy, electrical, hot14, charging, hot16
            Lab17CombinedConclusion.Decision d=decide("Weak",false,
                    (bits & 1) !=0,(bits & 2)!=0,(bits & 4)!=0,
                    (bits & 8)!=0,(bits & 16)!=0,true,
                    (bits & 32)!=0,(bits & 64)!=0,true);
            assertEquals("warning combination "+bits, Lab17CombinedConclusion.Tone.CAUTION,d.tone);
        }
    }
}
