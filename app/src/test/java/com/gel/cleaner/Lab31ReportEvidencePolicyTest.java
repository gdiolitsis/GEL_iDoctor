package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Lab31ReportEvidencePolicyTest {
    @Test public void repeatedSummaryDoesNotCountItsOwnAlerts() {
        String log = "⚠ Original diagnostic warning\n"
                + "ℹ LAB 31 — FINAL EXISTING-EVIDENCE SUMMARY\n"
                + "⚠ Entries for review: alert entries exist\n"
                + "⚠ Log entry: Original diagnostic warning\n"
                + "ℹ Lab 31 complete: available observations and limitations recorded.\n";
        String actual = Lab31ReportEvidencePolicy.withoutPriorSummaries(log);
        assertTrue(actual.contains("Original diagnostic warning"));
        assertFalse(actual.contains("Entries for review"));
        assertFalse(actual.contains("Log entry:"));
        assertFalse(actual.contains("LAB 31"));
    }

    @Test public void interruptedSummaryDoesNotHideNewerLabs() {
        String log = "ℹ LAB 31 — ΤΕΛΙΚΗ ΣΥΝΟΨΗ ΥΠΑΡΧΟΝΤΩΝ ΤΕΚΜΗΡΙΩΝ\n"
                + "⚠ Καταγραφή: παλιά προειδοποίηση\n"
                + "ℹ LAB 15 — Φόρτιση\n"
                + "✖ Real charger disconnection observed\n";
        String actual = Lab31ReportEvidencePolicy.withoutPriorSummaries(log);
        assertFalse(actual.contains("παλιά προειδοποίηση"));
        assertTrue(actual.contains("LAB 15"));
        assertTrue(actual.contains("Real charger disconnection observed"));
    }

    @Test public void emptyAndOrdinaryLogsStayConsistent() {
        assertEquals("", Lab31ReportEvidencePolicy.withoutPriorSummaries(null));
        assertEquals("", Lab31ReportEvidencePolicy.withoutPriorSummaries(""));
        assertTrue(Lab31ReportEvidencePolicy.withoutPriorSummaries(
                "✖ Measured fault\n").contains("Measured fault"));
    }
}
