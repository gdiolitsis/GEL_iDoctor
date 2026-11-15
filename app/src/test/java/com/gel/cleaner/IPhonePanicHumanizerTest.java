package com.gel.cleaner;

import org.junit.Test;

import static org.junit.Assert.*;

public class IPhonePanicHumanizerTest {
    @Test public void watchdogHasHumanExplanationWithoutFaultDiagnosis() {
        String text = IPhonePanicHumanizer.event("Watchdog / Hang", true);
        assertTrue(text.contains("Μη απόκριση"));
        assertTrue(text.contains("Watchdog / Hang"));
        assertTrue(IPhonePanicHumanizer.meaning("Watchdog / Hang", true)
                .contains("Δεν αποδεικνύεται"));
    }

    @Test public void thermalEventDoesNotClaimDeviceTemperatureWasMeasured() {
        String text = IPhonePanicHumanizer.event("Thermal Shutdown / Throttle", true);
        assertTrue(text.contains("θερμικής"));
        assertTrue(text.contains("Thermal Shutdown / Throttle"));
        assertTrue(IPhonePanicHumanizer.meaning("Thermal Shutdown / Throttle", true)
                .contains("Δεν έχουμε μετρήσει"));
    }

    @Test public void mixedLogsAreDescribedAsSeparateEvents() {
        assertTrue(IPhonePanicHumanizer.event("Multiple separate panic entries", true)
                .contains("ανεξάρτητα συμβάντα"));
        assertTrue(IPhonePanicHumanizer.domain(
                "Mixed evidence — per-entry evaluation required", true)
                .contains("κάθε αρχείο εξετάζεται χωριστά"));
    }

    @Test public void technicalIdentifiersRemainVisibleForService() {
        assertTrue(IPhonePanicHumanizer.domain("Kernel / OS Core", true)
                .contains("Kernel / OS Core"));
        assertTrue(IPhonePanicHumanizer.domain("Thermal", true)
                .contains("Thermal"));
        assertEquals("Storage / NAND",
                IPhonePanicHumanizer.domain("Storage / NAND", false));
    }

    @Test public void ruleCertaintyAndWeightCannotImplyConfirmedDamage() {
        assertEquals("Η αιτία δεν έχει επιβεβαιωθεί",
                IPhonePanicHumanizer.certainty("UNVERIFIED", true));
        assertTrue(IPhonePanicHumanizer.ruleWeight("MEDIUM", true)
                .contains("όχι βαρύτητα βλάβης"));
    }

    @Test public void unknownTermsAreNotInvented() {
        assertEquals("Unexpected OEM label",
                IPhonePanicHumanizer.event("Unexpected OEM label", true));
        assertEquals("Δεν προσδιορίστηκε τύπος συμβάντος",
                IPhonePanicHumanizer.event("Unknown", true));
    }

    @Test public void humanizedRuleHypothesesCannotDiagnoseHardware() {
        assertTrue(IPhonePanicHumanizer.possibleCause(
                "Watchdog timeout reported in a panic entry; component cause unknown", true)
                .contains("Δεν προκύπτει ποιο εξάρτημα"));
        assertTrue(IPhonePanicHumanizer.possibleCause("thermal shutdown recorded", true)
                .contains("Δεν επιβεβαιώνεται"));
    }

    @Test public void englishOutputRemainsAvailable() {
        assertTrue(IPhonePanicHumanizer.meaning("Watchdog / Hang", false)
                .contains("does not establish"));
        assertTrue(IPhonePanicHumanizer.meaning("Thermal Shutdown / Throttle", false)
                .contains("have not been measured"));
    }
}
