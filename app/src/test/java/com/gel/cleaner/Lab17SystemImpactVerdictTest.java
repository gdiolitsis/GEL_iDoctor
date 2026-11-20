package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Battery stress is not equivalent to system malfunction. */
public final class Lab17SystemImpactVerdictTest {
    private static Lab17SystemImpactVerdict.Decision decision(
            String stress, boolean chargingConcern, boolean thermalConcern,
            boolean androidModerate, boolean androidSevere, boolean highPeak,
            boolean largeRise, boolean full) {
        return Lab17SystemImpactVerdict.evaluate(
                true, stress, false, chargingConcern, true,
                thermalConcern, true, androidModerate, androidSevere,
                highPeak, largeRise, full);
    }

    @Test public void ownerScreenshotHighSagButNormalChargingAndCpuGpuChargerHasNoProvenSystemImpact() {
        Lab17SystemImpactVerdict.Decision d =
                decision("Critical", false, false, false, false, false, true, true);
        assertEquals(Lab17SystemImpactVerdict.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("Η μπαταρία παρουσίασε αυξημένη πτώση τάσης"));
        assertTrue(d.message.contains("το 15 κατέγραψε σταθερή φόρτιση"));
        assertTrue(d.message.contains("το 16 φυσιολογικές ενδείξεις"));
        assertTrue(d.message.contains("Δεν τεκμηριώθηκε ότι η πτώση τάσης επηρεάζει"));
        assertTrue(d.message.contains("μεγάλη άνοδος θερμοκρασίας"));
        assertFalse(d.message.contains("βλάβη όλου του συστήματος."));
    }

    @Test public void possibleOrdinaryBatteryAgeingWithoutObservedSystemImpact() {
        Lab17SystemImpactVerdict.Decision d =
                decision("Weak", false, false, false, false, false, false, true);
        assertEquals(Lab17SystemImpactVerdict.Tone.POSITIVE, d.tone);
        assertTrue(d.message.contains("φυσιολογική φθορά"));
        assertTrue(d.message.contains("χωρίς να καταγράφηκε επίπτωση στη λειτουργία του συστήματος"));
        assertTrue(d.message.contains("οι διαθέσιμες θερμοκρασίες φυσιολογικές"));
        assertFalse(d.message.contains("100% υγιές"));
    }

    @Test public void healthyMeasuredSectorsYieldExplicitPositiveSystemVerdict() {
        Lab17SystemImpactVerdict.Decision d =
                decision("Normal", false, false, false, false, false, false, true);
        assertEquals(Lab17SystemImpactVerdict.Tone.POSITIVE, d.tone);
        assertTrue(d.message.contains("ικανοποιητική λειτουργία του συστήματος"));
        assertTrue(d.message.contains("τροφοδοσίας, φόρτισης και θερμικής συμπεριφοράς"));
        assertTrue(d.message.contains("Δεν πρόκειται για πιστοποίηση"));
    }

    @Test public void severeAndroidThermalStatusProvesOperationalLimitation() {
        Lab17SystemImpactVerdict.Decision d =
                decision("Normal", false, false, true, true, false, false, true);
        assertEquals(Lab17SystemImpactVerdict.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("σοβαρό θερμικό περιορισμό της λειτουργίας του συστήματος"));
        assertTrue(d.message.contains("τεκμηριωμένη λειτουργική επίπτωση"));
    }

    @Test public void moderateAndroidThermalStatusIsNotDeclaredNormal() {
        Lab17SystemImpactVerdict.Decision d =
                decision("Normal", false, false, true, false, false, false, true);
        assertEquals(Lab17SystemImpactVerdict.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("ενεργό θερμικό περιορισμό"));
    }

    @Test public void cpuAndChargerThermalWarningIsReportedWithoutInventingCause() {
        Lab17SystemImpactVerdict.Decision d =
                decision("Weak", false, true, false, false, false, false, true);
        assertEquals(Lab17SystemImpactVerdict.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("διαθέσιμους θερμικούς τομείς"));
        assertTrue(d.message.contains("Δεν επιβεβαιώθηκε"));
        assertFalse(d.message.contains("βλάβη φορτιστή"));
    }

    @Test public void chargerInstabilityAndThermalWarningAreCombined() {
        Lab17SystemImpactVerdict.Decision d =
                decision("Normal", true, true, false, false, false, false, true);
        assertEquals(Lab17SystemImpactVerdict.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("λειτουργία φόρτισης"));
        assertTrue(d.message.contains("θερμικούς"));
        assertTrue(d.message.contains("χωρίς να τεκμηριώνει αιτιώδη σχέση"));
    }

    @Test public void chargingConcernAloneIsSystemChargingAreaNotWholeDeviceFailure() {
        Lab17SystemImpactVerdict.Decision d =
                decision("Normal", true, false, false, false, false, false, true);
        assertEquals(Lab17SystemImpactVerdict.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("λειτουργία φόρτισης"));
        assertTrue(d.message.contains("υπόλοιπων υποσυστημάτων"));
    }

    @Test public void highAbsoluteStressBatteryTemperatureStillPromptsFollowUp() {
        Lab17SystemImpactVerdict.Decision d =
                decision("Normal", false, false, false, false, true, false, true);
        assertEquals(Lab17SystemImpactVerdict.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("αυξημένη απόλυτη θερμοκρασία"));
        assertTrue(d.message.contains("δεν αποδεικνύεται μόνιμη επίπτωση"));
    }

    @Test public void incompleteThermalOrChargingCoverageRemainsNeutral() {
        Lab17SystemImpactVerdict.Decision d = Lab17SystemImpactVerdict.evaluate(
                true, "Normal", false, false, true, false, false,
                false, false, false, false, false);
        assertEquals(Lab17SystemImpactVerdict.Tone.NEUTRAL, d.tone);
        assertTrue(d.message.contains("μερική"));
        assertFalse(d.message.contains("ικανοποιητική λειτουργία"));
    }

    @Test public void englishSystemVerdictIsPresentAndScoped() {
        Lab17SystemImpactVerdict.Decision d = Lab17SystemImpactVerdict.evaluate(
                false, "Critical", false, false, true, false, true,
                false, false, false, true, true);
        assertEquals(Lab17SystemImpactVerdict.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("system operation"));
        assertTrue(d.message.contains("areas examined"));
        assertTrue(d.message.contains("large rise"));
    }

    @Test public void lab17UsesRealStored14And16EvidenceAndDisplaysItsSystemVerdict() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String source = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        assertTrue(source.contains("Lab17SystemImpactVerdict.evaluate("));
        assertTrue(source.contains("Συμπέρασμα για τη λειτουργία του συστήματος"));
        assertTrue(source.contains("lab16ThermalProfile.hasChipReading()"));
        assertTrue(source.contains("lab16OsThermalStatus >= PowerManager.THERMAL_STATUS_SEVERE"));
        assertTrue(source.contains("lab14_stress_thermal_rise_c"));
        assertTrue(source.contains("lab14_stress_peak_battery_c"));
        assertTrue(source.contains("lab14ATrace.evaluate(lab14ATraceTotalMs)"));
        assertTrue(source.contains("durationTrace.evaluate(300000L)"));
    }
}
