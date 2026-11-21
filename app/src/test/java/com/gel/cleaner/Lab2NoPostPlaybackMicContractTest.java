package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Owner QA: only simultaneous readings support Lab 2, no redundant mic test after tones. */
public final class Lab2NoPostPlaybackMicContractTest {
    @Test public void fourToneRmsRemainsButUnattributableExtraMicCaptureIsGone()
            throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String all = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int start = all.indexOf("private void lab2SpeakerSweep()");
        int finish = all.indexOf("LAB 3 — Earpiece Audio Path Check", start);
        assertTrue(start >= 0 && finish > start);
        String lab2 = all.substring(start, finish);

        // Preserve all four real readings taken while playback is active.
        assertTrue(lab2.contains("ToneGenerator.TONE_DTMF_1"));
        assertTrue(lab2.contains("ToneGenerator.TONE_DTMF_3"));
        assertTrue(lab2.contains("ToneGenerator.TONE_DTMF_6"));
        assertTrue(lab2.contains("ToneGenerator.TONE_DTMF_9"));
        assertTrue(lab2.contains("toneRms > 20.0"));
        assertTrue(lab2.contains("toneRms > noiseRms * 1.5"));
        assertTrue(lab2.contains("confirmed + \"/4\""));
        assertTrue(lab2.contains("Έξοδος ηχείου"));
        assertTrue(lab2.contains("όχι επαλήθευση των ακριβών συχνοτήτων"));

        // After playback, a separate microphone sample cannot identify a tone.
        assertFalse(lab2.contains("MicDiagnosticEngine.run(this)"));
        assertFalse(lab2.contains("Πρόσθετη μέτρηση μικροφώνου"));
        assertFalse(lab2.contains("Ποιότητα πρόσθετης μέτρησης"));
        assertFalse(lab2.contains("Additional microphone reading"));
        assertFalse(lab2.contains("Mic RMS"));
        assertFalse(lab2.contains("Mic Peak"));

        // Lab 1's independent microphone evidence must not be removed.
        assertTrue(all.substring(0, start).contains("MicDiagnosticEngine.run(this)"));
    }
}
