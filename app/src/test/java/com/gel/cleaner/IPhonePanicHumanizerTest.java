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
        assertTrue(IPhonePanicHumanizer.possibleCause("Thermal shutdown reported in a panic entry; sensor fault unconfirmed", true)
                .contains("Δεν επιβεβαιώνεται"));
    }

    @Test public void allNineRuleFamiliesHaveGreekInterpretations() {
        String[] domains = {
                "Thermal / Sensors", "Power / PMIC", "System / Watchdog",
                "Storage / NAND", "Baseband / Modem", "Memory / RAM",
                "Kernel / OS", "Thermal / Shutdown", "Kernel / OS Core"
        };
        String[] causes = {
                "Thermal sensor not responding (I2C line)",
                "Power management instability",
                "System freeze or watchdog deadlock",
                "Storage read/write failure",
                "Baseband processor crash or communication failure",
                "Unrecoverable memory fault detected",
                "Kernel assertion failure",
                "Watchdog timeout reported in a panic entry; component cause unknown",
                "Thermal shutdown reported in a panic entry; sensor fault unconfirmed"
        };
        for (String domain : domains) {
            String translated = IPhonePanicHumanizer.domain(domain, true);
            assertTrue(translated, translated.contains("("));
        }
        for (String cause : causes) {
            String translated = IPhonePanicHumanizer.possibleCause(cause, true);
            assertFalse(translated, translated.contains("Τεχνική περιγραφή:"));
            assertTrue(translated, translated.contains("Δεν ") || translated.contains("δεν "));
        }
    }

    @Test public void englishOutputRemainsAvailable() {
        assertTrue(IPhonePanicHumanizer.meaning("Watchdog / Hang", false)
                .contains("does not establish"));
        assertTrue(IPhonePanicHumanizer.meaning("Thermal Shutdown / Throttle", false)
                .contains("have not been measured"));
    }
}
