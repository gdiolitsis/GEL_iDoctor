package com.gel.cleaner;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * The Shizuku quick-start guide must not force Google Play, claim to automate
 * ADB installation/permissions or strand the user outside the GEL Activity.
 */
public final class GelShizukuSetupGuideContractTest {
    private static String read(String relativePath) throws Exception {
        Path path = Paths.get(relativePath);
        if (!Files.isRegularFile(path))
            path = Paths.get(relativePath.replaceFirst("^app/", ""));
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    @Test public void officialHelpRemainsInAppAndNeverRunsSilentInstaller() throws Exception {
        String screen = read("app/src/main/java/com/gel/cleaner/ShizukuOfficialHelpActivity.java");
        assertTrue(screen.contains("isDownloadMode ? GITHUB_RELEASES : HOME + \"/guide/setup/\""));
        assertTrue(screen.contains("https://shizuku.rikka.app"));
        assertTrue(screen.contains("extends GELAutoActivityHook"));
        assertTrue(screen.contains("webView.getSettings().setJavaScriptEnabled(false)"));
        assertTrue(screen.contains("webView.getSettings().setAllowFileAccess(false)"));
        assertTrue(screen.contains("webView.getSettings().setAllowContentAccess(false)"));
        assertTrue(screen.contains("shizuku.rikka.app\".equalsIgnoreCase(uri.getHost())"));
        assertTrue(screen.contains("if (!\"https\".equalsIgnoreCase(uri.getScheme())) return true"));
        assertTrue(screen.contains("startActivity(new Intent(Intent.ACTION_VIEW, uri)"));
        assertTrue(screen.contains("https://github.com/RikkaApps/Shizuku/releases/latest"));
        assertTrue(screen.contains("isTrustedApkLink(uri)"));
        assertTrue(screen.contains("showDownloadConfirmation(uri)"));
        assertTrue(screen.contains("bg.setColor(Color.BLACK)"));
        assertTrue(screen.contains("bg.setStroke(dp(2), 0xFFFFD700)"));
        assertTrue(screen.contains("message.setTextColor(Color.WHITE)"));
        assertTrue(screen.contains("cancel.setBackgroundColor(0xFF444444)"));
        assertTrue(screen.contains("open.setTextColor(Color.BLACK)"));
        assertTrue(screen.contains("open.setBackgroundColor(0xFF39FF14)"));
        assertFalse(screen.contains("new AlertDialog.Builder(this)"));
        assertFalse(screen.contains("apt.izzysoft.de"));
        assertFalse(screen.contains("coolapk.com"));
        assertFalse(screen.contains("PackageInstaller.Session"));
        assertFalse(screen.contains("REQUEST_INSTALL_PACKAGES"));
    }

    @Test public void humanFirstGuideOffersAllManualStepsAndCanContinueNormally() throws Exception {
        String ui = read("app/src/main/java/com/gel/cleaner/GelShizukuUi.java");
        assertTrue(ui.contains("showSetupGuide()"));
        assertTrue(ui.contains("github.com/RikkaApps/Shizuku/releases/latest"));
        assertTrue(ui.contains("shizuku.rikka.app/guide/setup/"));
        assertTrue(ui.contains("Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS"));
        assertTrue(ui.contains("getLaunchIntentForPackage("));
        assertTrue(ui.contains("moe.shizuku.privileged.api"));
        assertTrue(ui.contains("ΕΛΕΓΧΟΣ ΣΥΝΔΕΣΗΣ"));
        assertTrue(ui.contains("session.requestFromUser()"));
        assertTrue(ui.contains("ΣΥΝΕΧΕΙΑ ΧΩΡΙΣ SHIZUKU"));
        assertTrue(ui.contains("session.stop()"));
        assertTrue(ui.contains("Android 10 ή παλιότερο"));
    }

    @Test public void connectingStillRequiresRealShizukuPermissionAndAdbIdentity() throws Exception {
        String session = read("app/src/main/java/com/gel/cleaner/GelShizukuDiagnosticSession.java");
        assertTrue(session.contains("Shizuku.getUid() != 2000"));
        assertTrue(session.contains("Shizuku.checkSelfPermission()"));
        assertTrue(session.contains("Shizuku.requestPermission(REQUEST_CODE)"));
        assertTrue(session.contains("public boolean isReady()"));
        assertTrue(session.contains("public void restoreApprovedSession()"));
        assertTrue(session.contains("hasRememberedConsent()"));
        assertTrue(session.contains("endSession(true)"));
    }

    @Test public void officialWebHelpIsNotExported() throws Exception {
        String manifest = read("app/src/main/AndroidManifest.xml");
        assertTrue(manifest.contains("android:name=\".ShizukuOfficialHelpActivity\""));
        assertTrue(manifest.contains("android:exported=\"false\""));
        assertTrue(manifest.contains("android:usesCleartextTraffic=\"false\""));
    }

    @Test public void noChangeToNumberedLabsIsNeeded() throws Exception {
        String ui = read("app/src/main/java/com/gel/cleaner/GelShizukuUi.java");
        assertTrue(ui.contains("A single optional consent control BEFORE the numbered Labs"));
        assertTrue(ui.contains("όλα τα εργαστήρια συνεχίζουν κανονικά"));
    }
}
