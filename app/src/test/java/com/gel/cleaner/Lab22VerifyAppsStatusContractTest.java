package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Prevent "Play Protect on" from being inferred by settings-app presence. */
public final class Lab22VerifyAppsStatusContractTest {
    private static String read(String primary, String fallback) throws Exception {
        Path p = Paths.get(primary);
        if (!Files.isRegularFile(p)) p = Paths.get(fallback);
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
    private static String lab22() throws Exception {
        String s = read("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java",
                        "src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        int a = s.indexOf("private void lab22SecurityPatchAndPlayProtect()");
        int b = s.indexOf("// LAB 23 — Developer Options", a);
        assertTrue(a >= 0 && b > a);
        return s.substring(a, b);
    }

    @Test public void usesRealVerifyAppsServiceInsteadOfSettingsPresence() throws Exception {
        String section = lab22();
        String gradle = read("app/build.gradle", "build.gradle");
        assertTrue(gradle.contains("play-services-safetynet:18.1.0"));
        assertTrue(section.contains("com.google.android.gms.safetynet.SafetyNet.getClient(this)"));
        assertTrue(section.contains(".isVerifyAppsEnabled()"));
        assertTrue(section.contains("task.getResult().isVerifyAppsEnabled()"));
        assertFalse(section.contains("VerifyAppsSettingsActivity"));
        assertFalse(section.contains("PACKAGE_VERIFIER_ENABLE"));
        assertFalse(section.contains("settingsAvailable"));
    }

    @Test public void verifiedOnOffUnknownAreSeparateAndColorCoded() throws Exception {
        String section = lab22();
        assertTrue(section.contains("logLabelOkValue("));
        assertTrue(section.contains("Ενεργή (επιβεβαιώθηκε από υπηρεσίες Google)"));
        assertTrue(section.contains("logLabelWarnValue("));
        assertTrue(section.contains("Ανενεργή — ενεργοποιήστε τη σάρωση"));
        assertTrue(section.contains("logLabelValue("));
        assertTrue(section.contains("Δεν ήταν δυνατή η επιβεβαίωση της κατάστασης"));
        assertTrue(section.contains("Η μη διαθέσιμη απάντηση δεν σημαίνει"));
        assertTrue(section.contains("Δεν πραγματοποιήθηκε σάρωση"));
    }

    @Test public void asynchronousProbeDoesNotHangOrLeakOldRuns() throws Exception {
        String section = lab22();
        assertTrue(section.contains("new java.util.concurrent.atomic.AtomicBoolean(false)"));
        assertTrue(section.contains("compareAndSet(false, true)"));
        assertTrue(section.contains("ui.postDelayed(verifyAppsUnavailable, 8000L)"));
        assertTrue(section.contains("requestId != lab22BulletinRunId"));
        assertTrue(section.contains("isFinishing() || isDestroyed()"));
        assertTrue(section.contains("AndroidSecurityBulletinChecker.fetchLatestPublishedPatchLevel()"));
    }
}
