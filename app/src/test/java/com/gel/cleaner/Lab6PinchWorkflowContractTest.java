package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Regression: Lab 6 retains its 96-zone test and color PRO stage. */
public final class Lab6PinchWorkflowContractTest {
    private static String source(String name) throws Exception {
        Path path = Paths.get("app/src/main/java/com/gel/cleaner/" + name);
        if (!Files.isRegularFile(path))
            path = Paths.get("src/main/java/com/gel/cleaner/" + name);
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    @Test public void newStageNeverReplacesGridOrColorPro() throws Exception {
        String activity = source("ManualTestsActivity.java");
        assertTrue(activity.contains("REQ_LAB6_TOUCH = 6006"));
        assertTrue(activity.contains("REQ_LAB6_COLOR = 6007"));
        assertTrue(activity.contains("REQ_LAB6_PINCH = 6008"));
        assertTrue(activity.contains("total == 96 && remaining >= 0"));
        assertTrue(activity.contains("new Intent(this, TouchGridTestActivity.class)"));
        assertTrue(activity.contains("new Intent(this, PinchZoomTestActivity.class)"));
        assertTrue(activity.contains("new Intent(this, DisplayProTestActivity.class)"));
        assertTrue(activity.contains("if (requestCode == REQ_LAB6_PINCH)"));
        assertTrue(activity.contains("if (requestCode == REQ_LAB6_COLOR)"));
    }

    @Test public void completionRequiresBothActualDirections() throws Exception {
        String activity = source("ManualTestsActivity.java");
        assertTrue(activity.contains("dual && zoomIn && zoomOut"));
        assertTrue(activity.contains("resultCode == RESULT_OK"));
        assertTrue(activity.contains("resultCode == RESULT_FIRST_USER"));
        assertTrue(activity.contains("if (resultCode == RESULT_CANCELED)"));
        assertTrue(activity.contains("no overall PASS"));
        assertTrue(activity.contains("Two-finger separation increase verified"));
        assertTrue(activity.contains("Two-finger separation decrease verified"));
    }

    @Test public void sourceUsesTwoDistinctLivePointerIds() throws Exception {
        String screen = source("PinchZoomTestActivity.java");
        assertTrue(screen.contains("event.getPointerCount() == 2"));
        assertTrue(screen.contains("event.getPointerId(0)"));
        assertTrue(screen.contains("event.getPointerId(1)"));
        assertTrue(screen.contains("event.findPointerIndex(firstId)"));
        assertTrue(screen.contains("event.findPointerIndex(secondId)"));
        assertTrue(screen.contains("evidence.observeTwoFingerSpan(now)"));
        assertTrue(screen.contains("RESULT_FIRST_USER"));
        assertTrue(screen.contains("RESULT_CANCELED"));
        assertFalse(screen.contains("Shizuku.requestPermission"));
    }

    @Test public void portraitActivityIsNotExternallyAccessible() throws Exception {
        Path manifest = Paths.get("app/src/main/AndroidManifest.xml");
        if (!Files.isRegularFile(manifest))
            manifest = Paths.get("src/main/AndroidManifest.xml");
        String xml = new String(Files.readAllBytes(manifest), StandardCharsets.UTF_8);
        assertTrue(xml.contains("android:name=\".PinchZoomTestActivity\""));
        assertTrue(xml.contains("android:screenOrientation=\"portrait\""));
    }
}
