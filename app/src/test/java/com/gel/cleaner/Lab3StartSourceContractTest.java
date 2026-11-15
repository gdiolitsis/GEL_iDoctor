package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/**
 * Static regression checks for the Lab 3 start/exit contract.
 * A passing source check is not a replacement for a real-device audio test.
 */
public final class Lab3StartSourceContractTest {
    private static String readProjectFile(String modulePath) throws Exception {
        for (Path path : new Path[]{Paths.get(modulePath), Paths.get("app").resolve(modulePath)}) {
            if (Files.isRegularFile(path))
                return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        }
        throw new AssertionError("Missing source: " + modulePath);
    }

    private static String lab3() throws Exception {
        String source = readProjectFile("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        int start = source.indexOf("private void lab3EarpieceManual()");
        int end = source.indexOf("LAB 4 — Microphone Recording Check", start);
        assertTrue("Lab 3 source region is present", start >= 0 && end > start);
        return source.substring(start, end);
    }

    @Test public void startsExactlyOnceAndKeepsBackupToneLoop() throws Exception {
        String lab = lab3();
        assertEquals(1, lab.split("start.setOnClickListener", -1).length - 1);
        assertTrue(lab.contains("for (int i = 1; i <= 3; i++)"));
        assertTrue(lab.contains("playEarpieceBeep();"));
        assertTrue(lab.contains("routeToCallEarpiece();"));
    }

    @Test public void failedPlaybackRetainsDialogAndDoesNotClaimCompletedTest() throws Exception {
        String lab = lab3();
        int start = lab.indexOf("start.setOnClickListener");
        int success = lab.indexOf("} else if (playbackCompleted[0]) {", start);
        int dismiss = lab.indexOf("d.dismiss();", start);
        int userConfirmation = lab.indexOf("askUserEarpieceConfirmation();", start);
        assertTrue(success > start);
        assertTrue(dismiss > success);
        assertTrue(userConfirmation > dismiss);
        assertTrue(lab.contains("} else if (d.isShowing()) {"));
        assertTrue(lab.contains("start.setEnabled(true);"));
        assertTrue(lab.contains("lab3Running.set(false);"));
        assertTrue(lab.contains("lab3Cancelled.get()"));
    }

    @Test public void bothExitAndAndroidBackRemainAvailable() throws Exception {
        String lab = lab3();
        assertTrue(lab.contains("exitBtn.setOnClickListener"));
        assertTrue(lab.contains("KeyEvent.KEYCODE_BACK"));
        assertTrue(lab.contains("restoreLab3Audio();"));
    }

    @Test public void manualDiagnosticsStaysPortraitWhenRaisedToEar() throws Exception {
        String manifest = readProjectFile("src/main/AndroidManifest.xml");
        int start = manifest.indexOf("android:name=\".ManualTestsActivity\"");
        assertTrue(start >= 0);
        int end = manifest.indexOf("/>", start);
        assertTrue(end > start);
        assertTrue(manifest.substring(start, end).contains("android:screenOrientation=\"portrait\""));
    }
}
