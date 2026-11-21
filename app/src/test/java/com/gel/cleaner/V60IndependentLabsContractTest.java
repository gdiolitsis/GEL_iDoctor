package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/** Protects the owner-approved v60 baseline against accidental Shizuku reintroduction. */
public final class V60IndependentLabsContractTest {
    private static String read(String name) throws Exception {
        Path p = Paths.get(name);
        if (!Files.isRegularFile(p) && name.startsWith("app/")) p = Paths.get(name.substring(4));
        if (!Files.isRegularFile(p)) p = Paths.get("../../" + name);
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void noShizukuDependencyPrivilegeOrConsentUi() throws Exception {
        String gradle = read("app/build.gradle");
        String manifest = read("app/src/main/AndroidManifest.xml");
        String screen = read("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        assertFalse(gradle.contains("dev.rikka.shizuku"));
        assertFalse(manifest.contains("rikka.shizuku.ShizukuProvider"));
        assertFalse(manifest.contains("moe.shizuku.manager.permission"));
        assertFalse(manifest.contains("ShizukuOfficialHelpActivity"));
        assertFalse(screen.contains("GelShizukuUi"));
        assertFalse(screen.contains("GelShizukuEvidence"));
        assertFalse(screen.contains("gelShizukuSupplement"));
        assertFalse(screen.contains("Shizuku.requestPermission"));
    }

    @Test public void preserveUsefulIndependentLabsAndAndroidCrashEvidence() throws Exception {
        String screen = read("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String manifest = read("app/src/main/AndroidManifest.xml");
        String parser = read("app/src/main/java/com/gel/cleaner/CriticalSystemCrashEvidence.java");
        assertTrue(screen.contains("29. Τελική Τεχνική Αναφορά"));
        assertTrue(screen.contains("REQ_LAB6_PINCH"));
        assertTrue(screen.contains("PinchZoomTestActivity.EXTRA_DUAL_TOUCH"));
        assertTrue(screen.contains("new Intent(this, DisplayProTestActivity.class)"));
        assertTrue(manifest.contains(".PinchZoomTestActivity"));
        assertTrue(manifest.contains(".Lab25SharedReportActivity"));
        assertTrue(parser.contains("selected Android bugreport ZIP"));
        assertTrue(parser.contains("selected text report"));
        // The old ZIP/TXT picker below Lab 25 must stay removed.
        assertFalse(screen.contains("lab25ChooseBugReport()"));
        assertFalse(screen.contains("requestCode == 25025"));
        assertFalse(screen.contains("Intent.ACTION_OPEN_DOCUMENT"));
        // A user-initiated Android Sharesheet import is still supported.
        assertTrue(screen.contains("Lab25SharedReportActivity.ACTION_IMPORT_LAB25"));
        assertTrue(screen.contains("lab25ReadBugReport(sharedReport)"));
        // Lab14A includes its warm-up in the actual measured session duration.
        assertTrue(screen.contains("lab14ATrace.evaluate(lab14ATraceTotalMs)"));
        assertTrue(screen.contains("bestRate * lab14ATraceTotalMs"));
    }
}
