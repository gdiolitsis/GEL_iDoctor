package com.gel.cleaner;

import org.junit.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;

/** Android Sharesheet import remains voluntary, local and inside Lab25. */
public final class Lab25SharedReportRouteContractTest {
    private static String source(String path) throws Exception {
        Path file = Paths.get(path);
        if (!Files.isRegularFile(file) && path.startsWith("app/"))
            file = Paths.get(path.substring(4));
        if (!Files.isRegularFile(file)) file = Paths.get("../../" + path);
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    @Test public void shareTargetIsExportedButManualLabsAreNot() throws Exception {
        String manifest = source("app/src/main/AndroidManifest.xml");
        assertTrue(manifest.contains("android:name=\".Lab25SharedReportActivity\""));
        assertTrue(manifest.contains("android.intent.action.SEND"));
        assertTrue(manifest.contains("android:mimeType=\"application/zip\""));
        assertTrue(manifest.contains("android:mimeType=\"text/plain\""));
        assertTrue(manifest.contains("android:name=\".ManualTestsActivity\"\n            android:exported=\"false\""));
    }

    @Test public void localImportRequiresConsentAndContentUriGrant() throws Exception {
        String s = source("app/src/main/java/com/gel/cleaner/Lab25SharedReportActivity.java");
        assertTrue(s.contains("Intent.ACTION_SEND.equals(intent.getAction())"));
        assertTrue(s.contains("\"content\".equalsIgnoreCase(uri.getScheme())"));
        assertTrue(s.contains("Intent.FLAG_GRANT_READ_URI_PERMISSION"));
        assertTrue(s.contains("dialog.setOnCancelListener"));
        assertTrue(s.contains("\"ANALYZE\""));
        assertTrue(s.contains("\"CANCEL\""));
        assertFalse(s.contains("android.permission.DUMP"));
        assertFalse(s.contains("Runtime.getRuntime().exec("));
    }

    @Test public void newFlowUsesExistingLab25AndNoExtraButton() throws Exception {
        String s = source("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        assertTrue(s.contains("Lab25SharedReportActivity.ACTION_IMPORT_LAB25.equals"));
        assertTrue(s.contains("lab25CrashHistory();"));
        assertTrue(s.contains("lab25ReadBugReport(sharedReport);"));
        assertTrue(s.contains("CriticalSystemCrashEvidence.parseUserReport(input, roles)"));
        assertFalse(s.contains("this::lab25ChooseBugReport"));
        assertFalse(s.contains("↳ Προαιρετικά: Ανάλυση αναφοράς Android"));
    }
}
