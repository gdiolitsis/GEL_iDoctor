package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/**
 * Regression for the QA v61 device report: the second Lab entry repeated the
 * Shizuku opt-in dialog and did not re-establish the authorized session.
 */
public final class GelShizukuReentryContractTest {
    private static String source(String file) throws Exception {
        Path path = Paths.get("app/src/main/java/com/gel/cleaner/" + file);
        if (!Files.isRegularFile(path))
            path = Paths.get("src/main/java/com/gel/cleaner/" + file);
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    @Test public void priorApprovalRestoresSessionInsteadOfOfferingPopupAgain() throws Exception {
        String ui = source("GelShizukuUi.java");
        assertTrue(ui.contains("if (session.hasRememberedConsent())"));
        assertTrue(ui.contains("session.restoreApprovedSession()"));
        assertTrue(ui.contains("else showConsent()"));
        assertTrue(ui.contains("Shizuku ενεργό — πάτησε για τερματισμό"));
        assertTrue(ui.contains("Shizuku ανενεργό — πάτησε για επανασύνδεση"));
        assertTrue(ui.contains("Shizuku — απαιτείται νέα έγκριση"));
    }

    @Test public void appRemembersOnlyExplicitUserChoice() throws Exception {
        String s = source("GelShizukuDiagnosticSession.java");
        assertTrue(s.contains("consent.edit().putBoolean(APPROVED_KEY, true).apply()"));
        assertTrue(s.contains("public void requestFromUser()"));
        assertTrue(s.contains("public boolean hasRememberedConsent()"));
        assertTrue(s.contains("if (closed || !hasRememberedConsent()) return;"));
        assertTrue(s.contains("Shizuku.addBinderReceivedListenerSticky(binderReceived)"));
        assertTrue(s.contains("Shizuku.removeBinderReceivedListener(binderReceived)"));
    }

    @Test public void restoredPathChecksIndependentShizukuPermissionAndUid() throws Exception {
        String s = source("GelShizukuDiagnosticSession.java");
        String restore = s.substring(s.indexOf("public void restoreApprovedSession()"),
                s.indexOf("private void bindIfPermitted()"));
        assertTrue(restore.contains("Shizuku.getUid() != 2000"));
        assertTrue(restore.contains("Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED"));
        assertTrue(restore.contains("bindIfPermitted()"));
        assertFalse(restore.contains("Shizuku.requestPermission("));
        assertTrue(s.contains("if (!isReady()) {"));
        assertTrue(s.contains("report(\"READY\")"));
    }

    @Test public void closingScreenIsNotRevocationButExplicitStopIs() throws Exception {
        String s = source("GelShizukuDiagnosticSession.java");
        assertTrue(s.contains("public void stop() {\n        endSession(true);"));
        assertTrue(s.contains("endSession(false); // preserve explicit approval for next screen visit"));
        assertTrue(s.contains("if (forgetLocalConsent) {"));
        assertTrue(s.contains("consent.edit().putBoolean(APPROVED_KEY, false).apply()"));
        assertTrue(s.contains("Shizuku.unbindUserService(args, connection, true)"));
        assertTrue(s.contains("worker.shutdownNow()"));
    }

    @Test public void revocationDoesNotFakeActiveStatusOrAutoAskPermission() throws Exception {
        String s = source("GelShizukuDiagnosticSession.java");
        assertTrue(s.contains("report(\"PERMISSION_UNAVAILABLE\")"));
        assertTrue(s.contains("report(\"SHIZUKU_NOT_RUNNING\")"));
        assertTrue(s.contains("if (closed || !userRequested || !hasRememberedConsent()) return"));
        assertTrue(s.contains("if (!closed && userRequested) callback.onEvidence(finalValue)"));
    }
}
