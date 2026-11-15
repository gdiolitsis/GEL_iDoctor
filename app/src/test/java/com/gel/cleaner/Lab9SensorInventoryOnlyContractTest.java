package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/** Lab 9 is a sensor enumeration, not a sensor-performance test. */
public final class Lab9SensorInventoryOnlyContractTest {
    private static String lab9() throws Exception {
        Path path = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(path))
            path = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String s = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        int a = s.indexOf("private void lab9SensorsCheck() {");
        int b = s.indexOf("Helper — Sensor Presence", a);
        assertTrue("Lab9 source not found", a >= 0 && b > a);
        return s.substring(a, b);
    }

    @Test public void finalSummaryListsObservedSensorNamesOnly() throws Exception {
        String s = lab9();
        assertTrue(s.contains("Βρέθηκε καταχώριση με ονομασία SAR/RF"));
        assertTrue(s.contains("Βρέθηκε καταχώριση με ονομασία Pickup"));
        assertTrue(s.contains("Βρέθηκε καταχώριση με ονομασία Large Touch"));
        assertTrue(s.contains("Βρέθηκε καταχώριση με ονομασία Game Rotation"));
        assertTrue(s.contains("Καταγράφηκαν οι αισθητήρες που δηλώνει το Android."));
    }

    @Test public void functioningIsNotClaimedOrDiscussedInInventorySummary() throws Exception {
        String s = lab9();
        assertFalse(s.contains("χρήση μη ελεγμένη"));
        assertFalse(s.contains("lift-to-wake μη ελεγμένο"));
        assertFalse(s.contains("απόκριση μη ελεγμένη"));
        assertFalse(s.contains("όχι απόδειξη βλάβης"));
        assertFalse(s.contains("use not tested"));
        assertFalse(s.contains("response not tested"));
        assertFalse(s.contains("no fault established"));
    }

    @Test public void presentOptionalSensorsAreGreenAndMissingOnesRemainNeutral() throws Exception {
        String s = lab9();
        for (String label : new String[]{"Αισθητήρες SAR", "Pickup sensor",
                "Large area touch", "Game rotation vector"}) {
            assertTrue("Sensor category missing: " + label, s.contains(label));
        }
        assertTrue(s.contains("if (hasSAR)\n            logLabelOkValue("));
        assertTrue(s.contains("if (hasPickup)\n            logLabelOkValue("));
        assertTrue(s.contains("if (hasLargeTouch)\n            logLabelOkValue("));
        assertTrue(s.contains("if (hasGameRotation)\n            logLabelOkValue("));
        assertTrue(s.contains("Δεν βρέθηκε σχετική καταχώριση"));
        assertTrue(s.contains("Το Lab 9 ολοκληρώθηκε (απογραφή αισθητήρων)."));
    }
}
