package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Prioritize during-tone acoustic evidence over an optional later mic sample. */
public final class Lab2AcousticEvidencePriorityContractTest {
    private static String lab2() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String all = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int a = all.indexOf("private void lab2SpeakerSweep()");
        int b = all.indexOf("LAB 3 — Earpiece Audio Path Check", a);
        assertTrue("Lab 2 boundaries missing", a >= 0 && b > a);
        return all.substring(a, b);
    }

    @Test public void fourLiveIndicationsHavePriorityOverLowLaterConfidence() throws Exception {
        String code = lab2();
        assertTrue(code.contains("measured == tones.length"));
        assertTrue(code.contains("confirmed == tones.length"));
        assertTrue(code.contains("if (allFourTonesIndicated)"));
        assertTrue(code.contains("4/4 ακουστικές ενδείξεις κατά την αναπαραγωγή"));
        assertTrue(code.contains("LOW applies only to the later microphone reading"));
        assertTrue(code.contains("not verification of the exact frequencies"));
        assertFalse(code.contains("οι τόνοι δεν επιβεβαιώθηκαν"));
        assertFalse(code.contains("LOW confidence — tones not verified"));
    }

    @Test public void laterMicMayNotInventFrequencyProofOrSpeakerFailure() throws Exception {
        String code = lab2();
        assertTrue(code.contains("if (r == null)"));
        assertTrue(code.contains("if (rms <= 20)"));
        assertTrue(code.contains("does not override playback evidence"));
        assertTrue(code.contains("cannot identify individual tones"));
        assertTrue(code.contains("Ακουστικές ενδείξεις κατά την αναπαραγωγή: "));
        assertFalse(code.contains("Signal above RMS threshold 20 was not confirmed"));
        assertFalse(code.contains("RMS above 20 after playback, LOW confidence — tones not verified"));
        assertTrue(code.contains("logLabelValue(gr ? \"Πρόσθετη μέτρηση μικροφώνου\""));
        assertTrue(code.contains("logLabelOkValue(gr ? \"Έξοδος ηχείου\""));
    }
}
