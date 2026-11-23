package com.gel.cleaner;

import org.junit.Test;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/** Verified Apple source selection must not invent a model, symptom or browser. */
public final class AppleTroubleshootingLibraryTest {
    private static Path locate(String relative) {
        Path local = Paths.get(relative);
        if (Files.isRegularFile(local)) return local;
        return Paths.get("app").resolve(relative);
    }

    private static List<AppleOfficialServiceGuidance.Guidance> guides() throws Exception {
        try (FileInputStream in = new FileInputStream(locate(
                "src/main/assets/iphone_apple_official_guidance_v1.tsv").toFile())) {
            return AppleOfficialServiceGuidance.parse(in);
        }
    }

    private static List<AppleIPhoneManualChapters.Chapter> chapters(
            List<AppleOfficialServiceGuidance.Guidance> guides) throws Exception {
        try (FileInputStream in = new FileInputStream(locate(
                "src/main/assets/iphone_apple_manual_chapters_v1.tsv").toFile())) {
            return AppleIPhoneManualChapters.parse(in, guides);
        }
    }

    private static String source(String file) throws Exception {
        return new String(Files.readAllBytes(locate(file)), StandardCharsets.UTF_8);
    }

    @Test public void exactCommercialSelectionUsesExactAppleManual() throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> all = guides();
        AppleOfficialServiceGuidance.Guidance manual =
                AppleTroubleshootingLibrary.manualForSelection(all, "iphone", "iPhone 17 Pro");
        assertNotNull(manual);
        assertEquals("DIRECTORY_17_PRO", manual.id);
        assertEquals("https://support.apple.com/en-us/124262", manual.sourceUrl);
        assertNull(AppleTroubleshootingLibrary.manualForSelection(
                all, "iphone", "iPhone 99"));
        assertNull(AppleTroubleshootingLibrary.manualForSelection(
                all, "ipad", "iPhone 17 Pro"));
    }

    @Test public void selectedDeviceGetsOnlyIndexedChaptersPlusMarkedGeneralArticles()
            throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> all = guides();
        AppleOfficialServiceGuidance.Guidance manual =
                AppleTroubleshootingLibrary.manualForSelection(all, "iphone", "iPhone 17 Pro");
        List<AppleTroubleshootingLibrary.TopicLink> links =
                AppleTroubleshootingLibrary.topics(manual, chapters(all), all);
        assertFalse(links.isEmpty());
        boolean restart = false, wired = false, thermal = false, wifi = false;
        for (AppleTroubleshootingLibrary.TopicLink link : links) {
            assertTrue(AppleTroubleshootingLibrary.isOfficialUrl(link.url));
            if ("RESTART".equals(link.topic)) {
                assertTrue(link.modelIndexed);
                assertTrue(link.greek.contains("Watchdog"));
                restart = true;
            }
            if ("WIRED_CHARGING".equals(link.topic)) {
                assertTrue(link.modelIndexed);
                wired = true;
            }
            if ("THERMAL".equals(link.topic)) {
                assertFalse(link.modelIndexed);
                assertTrue(link.label(true).contains("γενικός οδηγός Apple"));
                thermal = true;
            }
            if ("WIFI".equals(link.topic)) {
                assertFalse(link.modelIndexed);
                wifi = true;
            }
        }
        assertTrue("Apple restart chapter missing", restart);
        assertTrue("Apple wired charging chapter missing", wired);
        assertTrue("General thermal reference missing", thermal);
        assertTrue("General Wi-Fi reference missing", wifi);
    }

    @Test public void onlyExactHttpsAppleSupportHostIsAllowed() {
        assertTrue(AppleTroubleshootingLibrary.isOfficialUrl(
                "https://support.apple.com/en-us/124262"));
        assertFalse(AppleTroubleshootingLibrary.isOfficialUrl(
                "http://support.apple.com/en-us/124262"));
        assertFalse(AppleTroubleshootingLibrary.isOfficialUrl(
                "https://support.apple.com.evil.example/en-us/124262"));
        assertFalse(AppleTroubleshootingLibrary.isOfficialUrl(
                "https://attacker@support.apple.com/en-us/124262"));
        assertFalse(AppleTroubleshootingLibrary.isOfficialUrl(
                "https://support.apple.com:444/en-us/124262"));
        assertFalse(AppleTroubleshootingLibrary.isOfficialUrl(
                "javascript:alert(1)"));
    }

    @Test public void mainAppleButtonAndViewerHaveOneBackToAppleHome() throws Exception {
        String main = source("src/main/java/com/gel/cleaner/MainActivity.java");
        String layout = source("src/main/res/layout/activity_main.xml");
        String manifest = source("src/main/AndroidManifest.xml");
        String viewer = source("src/main/java/com/gel/cleaner/AppleSupportViewerActivity.java");
        String labs = source("src/main/java/com/gel/cleaner/IPhoneLabsActivity.java");

        int library = layout.indexOf("@+id/btnAppleTroubleshooting");
        int diagnostics = layout.indexOf("@+id/btnDiagnostics");
        assertTrue(library > 0 && diagnostics > library);
        assertTrue(main.contains("bind(R.id.btnAppleTroubleshooting,"));
        assertTrue("Button must be shown before model selection",
                main.contains("show(R.id.btnAppleTroubleshooting);"));
        assertTrue("Missing selection must show explicit guidance",
                main.contains("Επίλεξε πρώτα μοντέλο συσκευής από το αρχικό μενού"));
        assertTrue("Model guard must prevent navigation without selection",
                main.contains("if (!\"iphone\".equalsIgnoreCase(family) || model == null || model.trim().isEmpty())"));

        String appleDialogs = main.substring(
                main.indexOf("private void showAppleTroubleshootingLibrary()"),
                main.indexOf("// BROWSER PICKER - DYNAMIC"));
        assertTrue("GEL black/gold background", appleDialogs.contains("background.setColor(0xFF080808)"));
        assertTrue("GEL gold panel border", appleDialogs.contains("background.setStroke(dp(2), 0xFFFFD700)"));
        assertTrue("GEL green choices", appleDialogs.contains("0xFF00FF9C"));
        assertTrue("GEL exit must be red", appleDialogs.contains("0xFFB71C1C"));
        assertTrue("GEL exit must have white text", appleDialogs.contains("exit ? Color.WHITE"));
        assertTrue("Apple library must have GEL EXIT", appleDialogs.contains("\"ΕΞΟΔΟΣ\""));
        assertTrue("Long symptom list should scroll", appleDialogs.contains("ScrollView scroll"));
        assertFalse("No OEM default positive button", appleDialogs.contains(".setPositiveButton("));
        assertFalse("No OEM default negative button", appleDialogs.contains(".setNegativeButton("));
                assertTrue(main.contains("startActivity(viewer)"));
        assertTrue(main.contains("AppleSupportViewerActivity.EXTRA_SOURCE_URL"));
        assertTrue(viewer.contains("new OnBackPressedCallback(true)"));
        assertTrue(viewer.contains("Intent.FLAG_ACTIVITY_CLEAR_TOP"));
        assertTrue(viewer.contains("returnToAppleHome();"));
        assertTrue(viewer.contains("new WebView(this)"));
        assertTrue(viewer.contains("isOfficialUrl(next)"));
        assertTrue(manifest.contains("android:name=\".AppleSupportViewerActivity\""));
        assertTrue(labs.contains("openAppleSourceInApp(url)"));
        assertFalse(labs.contains("315 επίσημες πηγές διαδικασιών"));
    }
}
