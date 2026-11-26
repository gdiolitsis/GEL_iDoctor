package com.gel.cleaner;

import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.*;

/** Regression tests for non-destructive PDF / preview separator cleanup. */
public final class GELServiceReportLinePolicyTest {

    @Test public void orphanedHeadingDividersAreRemoved() {
        String log = "--------------------------------------------\n"
                + "--------------------------------------------\n\n"
                + "ANDROID MANUAL TESTS – HARDWARE DIAGNOSTICS\n"
                + "--------------------------------------------\n"
                + "ℹ GEL Manual Tests – ready.\n";
        assertEquals("ANDROID MANUAL TESTS – HARDWARE DIAGNOSTICS\n"
                + "--------------------------------------------\n"
                + "ℹ GEL Manual Tests – ready.",
                GELServiceReportLinePolicy.cleanPreview(log));
    }

    @Test public void duplicatedLabBoundaryIsCollapsedWithoutRemovingEvidence() {
        String[] lines = {
                "ℹ LAB 3 – Earpiece Audio Path Check",
                "Result: User confirmed audio playback",
                "✔ Lab 3 finished.",
                "--------------------------------------------",
                "",
                "--------------------------------------------",
                "ℹ LAB 4 – Microphone Hardware Check",
                "Bottom RMS: 2445",
                "Status: Acoustic input was observed on both capture paths"
        };
        boolean[] mask = GELServiceReportLinePolicy.visibleMask(lines);
        assertArrayEquals(new boolean[] {
                true, true, true, true, true, false, true, true, true
        }, mask);
    }

    @Test public void nonDividerTextAndNegativeValuesArePreserved() {
        String log = "Result: -4.0°C\n"
                + "Details: -------------- broken part\n"
                + "--------------------------------------------\n"
                + "Next finding";
        assertTrue(GELServiceReportLinePolicy.cleanPreview(log).contains("Result: -4.0°C"));
        assertTrue(GELServiceReportLinePolicy.cleanPreview(log)
                .contains("Details: -------------- broken part"));
        assertEquals(4, GELServiceReportLinePolicy.cleanPreview(log).split("\\n").length);
    }

    @Test public void openingWhitespaceDoesNotRenderAsEmptyReportRows() {
        String[] lines = { "", "", "------", "", "Lab 27: A+", "", "", "Finding: OK" };
        assertArrayEquals(new boolean[] {
                false, false, false, false, true, true, false, true
        }, GELServiceReportLinePolicy.visibleMask(lines));
    }
}
