package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/** Lab 8.1 front/rear reporting: colour conveys an estimate, never measured video quality. */
public final class Lab81CameraEstimateColorContractTest {
    private static String source() throws Exception {
        Path path = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(path))
            path = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static String renderer(String src) {
        int start = src.indexOf("private void lab8_1DumpOneCameraCapabilities(");
        int end = src.indexOf("// LAB 8.1 — Helpers (NO NESTED METHODS)", start);
        assertTrue(start >= 0 && end > start);
        return src.substring(start, end);
    }

    @Test public void rearAndFrontCamerasUseTheSameEstimateRenderingCode() throws Exception {
        String src = source();
        assertTrue(src.contains("for (Lab8Cam cam : lab8CamsFor81) {\n        lab8_1DumpOneCameraCapabilities(lab8CmFor81, cam);"));
        String render = renderer(src);
        assertTrue(render.contains("CameraHumanSummary h = buildHumanSummary(cc)"));
        assertTrue(render.contains("h.reportedMaxFps >= 30"));
        assertTrue(render.contains("h.reportedMaxFps > 0"));
    }

    @Test public void estimatedSmoothnessHasGreenYellowNeutralBranches() throws Exception {
        String render = renderer(source());
        assertTrue(render.contains("logLabelOkValue(gr ? \"Ομαλότητα βίντεο (εκτίμηση)\""));
        assertTrue(render.contains("logLabelWarnValue(gr ? \"Ομαλότητα βίντεο (εκτίμηση)\""));
        assertTrue(render.contains("logLabelValue(gr ? \"Ομαλότητα βίντεο (εκτίμηση)\""));
    }

    @Test public void redundantRealLifeEstimateIsOmittedInBothCameraReports() throws Exception {
        String render = renderer(source());
        assertFalse(render.contains("Χρήση στην πράξη (εκτίμηση)"));
        assertFalse(render.contains("Real-life use (estimate)"));
        assertTrue(render.contains("logLabelOkValue(gr ? \"Συμπέρασμα δυνατοτήτων\""));
    }

    @Test public void unreportedFpsNeverBecomesPositiveMeasurement() throws Exception {
        String src = source();
        assertTrue(src.contains("h.reportedMaxFps = maxFps;"));
        assertTrue(src.contains("Δεν αναφέρθηκαν FPS — η ομαλότητα δεν μπορεί να εκτιμηθεί"));
        assertTrue(src.contains("Δεν υπάρχουν αρκετά στοιχεία FPS για εκτίμηση χρήσης."));
        assertFalse(renderer(src).contains("δεν μετρήθηκε αντικειμενική ποιότητα εικόνας."));
        assertFalse(renderer(src).contains("objective image quality was not measured."));
        assertTrue(renderer(src).contains("logLabelValue(gr ? \"Ομαλότητα βίντεο (εκτίμηση)\""));
    }
}
