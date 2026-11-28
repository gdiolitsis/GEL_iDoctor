package com.gel.cleaner;

import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class GELRemoteLabLogTest {
    @Test public void skipsMenuStartupLinesAndMalformedBatches() {
        assertFalse(GELRemoteLabLog.containsLabEntry(Arrays.asList(
                "ANDROID MANUAL TESTS — HARDWARE DIAGNOSTICS",
                "--------------------------------------------",
                "ℹ GEL Manual Tests — ready. Open a category above and run the required labs.",
                "ℹ Έναρξη χειροκίνητων διαγνωστικών ελέγχων συσκευής.",
                "ℹ Run Lab 27 — GEL Device Certification first.",
                "ℹ LAB 99 — Unknown",
                null, 12)));
        assertFalse(GELRemoteLabLog.containsLabEntry("LAB 1 — Speaker Tone Test"));
        assertFalse(GELRemoteLabLog.containsLabEntry(null));
    }

    @Test public void recognizesLiveLabEntriesFromGreekAndEnglishClients() {
        for (String heading : Arrays.asList(
                "ℹ LAB 1 - Speaker Tone Test",
                "ℹ️ LAB 2 — Έλεγχος Συχνοτήτων Ηχείου",
                "i LAB 5 — Vibration Motor Test",
                "ℹ LAB 4 PRO — Ποιότητα συνομιλίας ακουστικού",
                "LAB 14A — Battery Health and Stress Diagnosis",
                "ℹ LAB 14 Γ — Διάρκεια μπαταρίας",
                "ℹ LAB 14B started",
                "ℹ LAB 14 GAMMA ξεκίνησε",
                "ℹ GEL Device Certification",
                "Lab27 – Certification")) {
            assertTrue(heading, GELRemoteLabLog.containsLabEntry(Collections.singletonList(heading)));
        }
    }

    @Test public void usesServerLogsAndMarksTheViewerOpenedBeforeNavigating() throws Exception {
        Path path = Paths.get("app/src/main/java/com/gel/cleaner/RepairDeviceActivity.java");
        if (!Files.isRegularFile(path)) path = Paths.get("src/main/java/com/gel/cleaner/RepairDeviceActivity.java");
        String technician = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        assertTrue(technician.contains("!technicianScreenResumed || !hasWindowFocus()"));
        assertTrue(technician.contains("!id.equals(prefs.getString(KEY_RESULTS_OPENED_FOR, null))"));
        assertTrue(technician.contains("snapshot.getMetadata().isFromCache()"));
        assertTrue(technician.contains("v -> openReadOnlyResults()"));
        int mark = technician.indexOf("prefs.edit().putString(KEY_RESULTS_OPENED_FOR, id).apply()");
        int launch = technician.indexOf("startActivity(new Intent(this, GELReadOnlyDiagnosticsActivity.class)");
        assertTrue(mark >= 0 && launch > mark);
    }
}
