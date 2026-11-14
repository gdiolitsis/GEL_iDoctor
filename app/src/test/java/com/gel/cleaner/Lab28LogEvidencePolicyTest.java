package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class Lab28LogEvidencePolicyTest {
    @Test public void priorLab28ClaimsMustNotBeRescanned() {
        String history = "ℹ LAB 20 — Uptime\n"
                + "ℹ Device uptime recorded\n"
                + "ℹ LAB 28 — Hardware Stability & Interconnect Integrity\n"
                + "⚠ Random reboots or sudden resets detected\n"
                + "⚠ Abnormal thermal spikes detected\n"
                + "ℹ Lab 28: log triage complete; device hardware was not certified.\n"
                + "ℹ LAB 29 — Hardware observations\n";
        String trimmed = Lab28LogEvidencePolicy.withoutLab28Reports(history);
        assertTrue(trimmed.contains("LAB 20"));
        assertTrue(trimmed.contains("LAB 29"));
        assertFalse(trimmed.contains("Random reboots"));
        assertFalse(trimmed.contains("thermal spikes"));
    }

    @Test public void openCurrentLab28AndGreekReportAreExcluded() {
        String history = "ℹ Sensor reading observed\n"
                + "ℹ LAB 28 — Σταθερότητα Υλικού & Ακεραιότητα Διασυνδέσεων\n"
                + "⚠ Τυχαίες επανεκκινήσεις\n"
                + "ℹ Lab 28: ολοκληρώθηκε η διαλογή αναφορών, όχι έλεγχος υγείας hardware.\n"
                + "ℹ LAB 28 — Hardware Stability & Interconnect Integrity\n"
                + "⚠ Random reboots\n";
        String trimmed = Lab28LogEvidencePolicy.withoutLab28Reports(history);
        assertTrue(trimmed.contains("Sensor reading"));
        assertFalse(trimmed.contains("Τυχαίες επανεκκινήσεις"));
        assertFalse(trimmed.contains("Random reboots"));
    }

    @Test public void interruptedPriorReportMustNotHideFollowingLabs() {
        String history = "ℹ LAB 28 — Hardware Stability & Interconnect Integrity\n"
                + "⚠ Random reboots or sudden resets detected\n"
                + "ℹ LAB 30 — Automatic Evidence Summary\n"
                + "ℹ Independent thermal evidence\n";
        String trimmed = Lab28LogEvidencePolicy.withoutLab28Reports(history);
        assertFalse(trimmed.contains("Random reboots"));
        assertTrue(trimmed.contains("LAB 30"));
        assertTrue(trimmed.contains("Independent thermal evidence"));
    }

    @Test public void preservesOtherLabsAndEmptyInputs() {
        assertEquals("", Lab28LogEvidencePolicy.withoutLab28Reports(null));
        assertEquals("", Lab28LogEvidencePolicy.withoutLab28Reports(""));
        assertTrue(Lab28LogEvidencePolicy.withoutLab28Reports(
                "⚠ Signal drop observed\n").contains("Signal drop observed"));
    }
}
