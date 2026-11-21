package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** During-tone acoustic evidence is the only Lab 2 diagnostic input. */
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

    @Test public void fourToneObservationsAreIndependentAndReportedCorrectly() throws Exception {
        String code = lab2();
        assertTrue(code.contains("measured == tones.length"));
        assertTrue(code.contains("confirmed == tones.length"));
        assertTrue(code.contains("if (allFourTonesIndicated)"));
        assertTrue(code.contains("4/4 ακουστικές ενδείξεις κατά την αναπαραγωγή"));
        assertTrue(code.contains("not verification of exact frequencies"));
        assertTrue(code.contains("toneRms > noiseRms * 1.5"));
        assertFalse(code.contains("οι τόνοι δεν επιβεβαιώθηκαν"));
        assertFalse(code.contains("LOW confidence — tones not verified"));
    }

    @Test public void noPostPlaybackMicrophoneReadingCanInventToneProofOrFailure()
            throws Exception {
        String code = lab2();
        assertTrue(code.contains("Ακουστικές ενδείξεις κατά την αναπαραγωγή: "));
        assertTrue(code.contains("logLabelOkValue(gr ? \"Έξοδος ηχείου\""));
        assertTrue(code.contains("toneRms > 20.0"));
        assertFalse(code.contains("MicDiagnosticEngine.run(this)"));
        assertFalse(code.contains("if (rms <= 20)"));
        assertFalse(code.contains("Additional microphone reading"));
        assertFalse(code.contains("Πρόσθετη μέτρηση μικροφώνου"));
        assertFalse(code.contains("Ποιότητα πρόσθετης μέτρησης"));
        assertFalse(code.contains("Mic RMS"));
        assertFalse(code.contains("Mic Peak"));
        assertFalse(code.contains("RMS above 20 after playback, LOW confidence — tones not verified"));
    }
}
