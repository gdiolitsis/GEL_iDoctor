package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GelDiagnosticSeverityTest {
    @Test public void neutralFormattingIsBackwardCompatible() {
        assertEquals("Temperature: 42.0 C",
                GelDiagnosticSeverity.plainLine(GelDiagnosticSeverity.Level.NEUTRAL,
                        "Temperature", "42.0 C"));
    }

    @Test public void cautionAndErrorHaveExplicitReportMarkers() {
        String warning = GelDiagnosticSeverity.plainLine(
                GelDiagnosticSeverity.Level.WARNING, "Thermal state", "Moderate");
        String error = GelDiagnosticSeverity.plainLine(
                GelDiagnosticSeverity.Level.ERROR, "Battery test", "Measured failure");
        assertTrue(warning.startsWith("⚠ "));
        assertTrue(error.startsWith("✖ "));
        assertFalse(warning.startsWith("✖ "));
        assertFalse(error.startsWith("⚠ "));
        assertEquals("⚠ Thermal state: Moderate", warning);
        assertEquals("✖ Battery test: Measured failure", error);
    }
}
