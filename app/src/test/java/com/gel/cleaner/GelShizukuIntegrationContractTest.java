package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** A real Shizuku UserService, optional by explicit consent and only read-only. */
public final class GelShizukuIntegrationContractTest {
    private static String read(String file) throws Exception {
        Path p = Paths.get(file);
        if (!Files.isRegularFile(p) && file.startsWith("app/"))
            p = Paths.get(file.substring(4));
        if (!Files.isRegularFile(p)) p = Paths.get("../../" + file);
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void officialShizukuV13UserServiceIsWired() throws Exception {
        String gradle = read("app/build.gradle");
        String manifest = read("app/src/main/AndroidManifest.xml");
        assertTrue(gradle.contains("dev.rikka.shizuku:api:13.1.5"));
        assertTrue(gradle.contains("dev.rikka.shizuku:provider:13.1.5"));
        assertTrue(gradle.contains("aidl = true"));
        assertTrue(manifest.contains("rikka.shizuku.ShizukuProvider"));
        assertTrue(manifest.contains("android.permission.INTERACT_ACROSS_USERS_FULL"));
        assertTrue(manifest.contains("moe.shizuku.manager.permission.API_V23"));
        assertFalse(manifest.contains("android.permission.DUMP"));
    }

    @Test public void manualUserGrantAndNoRootAreRequired() throws Exception {
        String session = read("app/src/main/java/com/gel/cleaner/GelShizukuDiagnosticSession.java");
        String ui = read("app/src/main/java/com/gel/cleaner/GelShizukuUi.java");
        assertTrue(session.contains("Shizuku.requestPermission(REQUEST_CODE)"));
        assertTrue(session.contains("Shizuku.checkSelfPermission()"));
        assertTrue(session.contains("Shizuku.getUid() != 2000"));
        assertTrue(session.contains("operation > GelShizukuEvidence.APP_OPS"));
        assertTrue(session.contains("Shizuku.bindUserService(args, connection)"));
        assertTrue(session.contains(".daemon(false)"));
        assertTrue(session.contains("Shizuku.unbindUserService(args, connection, true)"));
        assertTrue(ui.contains("session.requestFromUser()"));
        assertTrue(ui.contains("public void offerOnEntry()"));
        assertTrue(ui.contains("if (entryOfferShown) return;"));
        assertTrue(ui.contains("showConsent();"));
        assertTrue(ui.contains("NORMAL LABS"));
        assertTrue(ui.contains("Shizuku"));
        assertFalse(ui.contains("su -c"));
    }

    @Test public void callerCannotSendCommandsOrPathsToPrivilegedService() throws Exception {
        String service = read("app/src/main/java/com/gel/cleaner/GelShizukuReadOnlyService.java");
        String aidl = read("app/src/main/aidl/com/gel/cleaner/IGelShizukuReadOnly.aidl");
        assertTrue(aidl.contains("String probe(int operationId) = 1;"));
        assertFalse(aidl.contains("String command"));
        assertTrue(service.contains("Process.myUid() != SHELL_UID"));
        assertTrue(service.contains("case GelShizukuEvidence.CAPACITY:"));
        assertTrue(service.contains("case GelShizukuEvidence.CRASH:"));
        assertTrue(service.contains("case GelShizukuEvidence.THERMAL:"));
        assertTrue(service.contains("case GelShizukuEvidence.MEMORY:"));
        assertTrue(service.contains("case GelShizukuEvidence.BOOT:"));
        assertTrue(service.contains("case GelShizukuEvidence.UFS:"));
        assertTrue(service.contains("case GelShizukuEvidence.LOW_MEMORY:"));
        assertTrue(service.contains("case GelShizukuEvidence.APP_OPS:"));
        assertTrue(service.contains("gel-shell-readonly-drain"));
        assertTrue(service.contains("health_descriptor"));
        assertTrue(service.contains("APPOPS|SAMPLED"));
        assertTrue(service.contains("LMK|"));
        assertFalse(service.contains("Runtime.getRuntime().exec"));
        assertFalse(service.contains("sh\", \"-c"));
        assertTrue(service.contains("MAX_OUTPUT"));
    }

    @Test public void original29LabsRemainAvailable() throws Exception {
        String activity = read("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        assertTrue(activity.contains("29. Τελική Τεχνική Αναφορά"));
        assertTrue(activity.contains("new GelShizukuUi.Widget("));
        assertTrue(activity.contains("if (savedInstanceState == null && !sharedBugreportEntry)"));
        assertTrue(activity.contains("gelShizukuUi.offerOnEntry();"));
        assertTrue(activity.contains("labsScroll.post(() -> {"));
        assertTrue(activity.contains("gelShizukuUi.close()"));
        assertTrue(activity.contains("gelShizukuUi.getCapacity()"));
        assertTrue(activity.contains("SHIZUKU_GAUGE_FULL"));
        assertTrue(activity.contains("gelShizukuSupplement(GelShizukuEvidence.THERMAL)"));
        assertTrue(activity.contains("gelShizukuSupplement(GelShizukuEvidence.MEMORY)"));
        assertTrue(activity.contains("gelShizukuSupplement(GelShizukuEvidence.BOOT)"));
        assertTrue(activity.contains("gelShizukuSupplement(GelShizukuEvidence.UFS)"));
        assertTrue(activity.contains("gelShizukuSupplement(GelShizukuEvidence.LOW_MEMORY)"));
        assertTrue(activity.contains("gelShizukuSupplement(GelShizukuEvidence.APP_OPS)"));
        assertTrue(activity.contains("lab14HasReportedCapacity = start.chargeFullMah > 0;"));
        assertFalse(activity.contains("lab14HasReportedCapacity = start.chargeFullMah > 0 || start.chargeDesignMah > 0;"));
        assertTrue(activity.contains("CriticalSystemCrashEvidence.fromShizukuCrashBuffer(wire)"));
        assertFalse(activity.contains("this::lab25ChooseBugReport"));
    }
}
