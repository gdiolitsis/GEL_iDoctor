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
                assertTrue(link.greek.contains("Αργή φόρτιση"));
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
        java.util.Set<String> categories = new java.util.HashSet<>();
        for (AppleTroubleshootingLibrary.TopicLink link : links) {
            // Multiple distinct model chapters may share a heading; the
            // generic fallback must never duplicate a model-specific topic.
            if (!link.modelIndexed) {
                for (AppleTroubleshootingLibrary.TopicLink modelTopic : links) {
                    assertFalse("Generic guide duplicates model chapter",
                            modelTopic.modelIndexed && modelTopic.topic.equals(link.topic));
                }
            }
            categories.add(link.topic);
        }
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
        assertTrue("A new main screen must not inherit a prior declaration",
                main.contains("appleModelDeclaredOnCurrentScreen = false"));
        assertTrue("A model becomes current only after explicit picker selection",
                main.contains("appleModelDeclaredOnCurrentScreen = true"));
        assertTrue("Saved model cannot bypass fresh-declaration requirement",
                main.contains("if (!appleModelDeclaredOnCurrentScreen || !\"iphone\".equalsIgnoreCase(family)"));
        assertTrue("Open model picker when declaration is blank",
                main.contains("showAppleModelPicker(\"iphone\", true)"));
        assertTrue("Ask explicitly which model is being searched",
                main.contains("Για ποιο μοντέλο iPhone κάνεις την αναζήτηση;"));
        assertTrue("Continue to manual selector after a new model is chosen",
                main.contains("if (continueToBibliography) showAppleTroubleshootingLibrary()"));
        assertTrue("Cancelled model picker never starts Apple bibliography",
                main.contains("addAppleReferenceAction(root, AppLang.isGreek(this) ? \"ΕΞΟΔΟΣ\" : \"EXIT\","));
        assertTrue("The previous stored model is never silently reused",
                main.contains("Never take an old SharedPreferences value as consent"));
        String modelPicker = main.substring(
                main.indexOf("private void showAppleModelPicker(String type, boolean continueToBibliography)"),
                main.indexOf("private String normalizeAppleModel("));
        assertFalse("Do not use OEM Toast during GEL model selection",
                modelPicker.contains("Toast.makeText("));
        assertTrue("Model picker is scrollable and keeps its red Exit visible",
                modelPicker.contains("Math.min(dp(530)"));
        assertTrue("The declaration label reflects the new selection",
                modelPicker.contains("btn.setText(\"🍎 \" + type.toUpperCase(Locale.US)"));

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
        assertTrue("Embedded page exit is red", viewer.contains("bg.setColor(0xFFB71C1C)"));
        assertTrue("Embedded page exit uses white text", viewer.contains("back.setTextColor(Color.WHITE)"));
        assertTrue("Return bar must use independent TextView, not OEM Button",
                viewer.contains("TextView back = new TextView(this)"));
        assertTrue("Explicit Greek return label must be visible",
                viewer.contains("← ΕΠΙΣΤΡΟΦΗ ΣΤΗΝ ΑΡΧΙΚΗ APPLE"));
        assertTrue("Explicit English return label must be available",
                viewer.contains("← RETURN TO APPLE HOME"));
        assertTrue("Return label centered on red bar",
                viewer.contains("back.setGravity(Gravity.CENTER)"));
        assertTrue("Respect user-selected font size in SP",
                viewer.contains("back.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16f)"));
        assertTrue("Allow multiple lines for narrow displays and large text",
                viewer.contains("back.setSingleLine(false)"));
        assertTrue("Do not horizontally scroll long return labels",
                viewer.contains("back.setHorizontallyScrolling(false)"));
        assertTrue("Never cut text with an ellipsis",
                viewer.contains("back.setEllipsize(null)"));
        assertTrue("Adaptive return-bar height rather than a fixed size",
                viewer.contains("ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT"));
        assertTrue("Minimum accessible touch target remains available",
                viewer.contains("back.setMinHeight(dp(54))"));
        assertFalse("Do not shrink user font choices with auto sizing",
                viewer.contains("back.setAutoSizeTextType"));
        assertTrue("Return label has content description",
                viewer.contains("back.setContentDescription(returnLabel)"));
        assertTrue("Button retains one-tap navigation to Apple home",
                viewer.contains("back.setOnClickListener(v -> returnToAppleHome())"));
        assertFalse("No OEM-styled Button for the return bar",
                viewer.contains("Button back = new Button(this)"));
        assertTrue("Viewer errors stay inside GEL", viewer.contains("status.setVisibility(View.VISIBLE)"));
        assertTrue("Invalid link dialog remains GEL styled", viewer.contains("showInvalidSourceDialog()"));
        assertFalse("Viewer must not use OEM Toast popups", viewer.contains("Toast.makeText("));
        assertTrue(manifest.contains("android:name=\".AppleSupportViewerActivity\""));
        assertTrue(labs.contains("openAppleSourceInApp(url)"));
        assertFalse(labs.contains("315 επίσημες πηγές διαδικασιών"));
    }
}
