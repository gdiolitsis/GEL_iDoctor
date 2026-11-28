package com.gel.cleaner;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/** Protect both subscription-gated entry points and the read-only service boundary. */
public final class GELPaidServiceConnectionContractTest {
    private static String source(String file) throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/", file);
        if (!Files.isRegularFile(p)) {
            p = Paths.get("src/main/java/com/gel/cleaner/", file);
        }
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void technicianEntryUsesProGrant() throws Exception {
        String menu = source("DiagnosisMenuActivity.java");
        assertTrue(menu.contains("final boolean showRemoteAssistUi = true;"));
        assertTrue(menu.contains("Σύνδεση με Πελάτη — Διαγνωστικά"));
        assertTrue(menu.contains("Σύνδεση με Τεχνικό"));
        assertTrue(menu.contains("Δωρεάν αποδοχή πρόσκλησης τεχνικού"));
        assertTrue(menu.contains("GELServiceSubscriptionGate.requireTechnicianAccess(this"));
        assertFalse(menu.contains("GELServiceSubscriptionGate.requirePaidSubscription(this)"));
    }

    @Test public void paidGiftAndTesterCanCreateTechnicianSessions() throws Exception {
        String gate = source("GELServiceSubscriptionGate.java");
        assertTrue(gate.contains("GelProEntitlement.isPaidProActive(context)"));
        assertTrue(gate.contains("GelAccountPro.active(context)"));
        assertTrue(gate.contains("GelProEntitlement.isTesterProActive(context)"));
        assertFalse(gate.contains("GELCustomerServiceAccess.isActive("));
        assertFalse(gate.contains("GelTrialAccess.active("));
        assertTrue(gate.contains("GelAccountPro.refresh(activity"));
        assertTrue(gate.contains("ΑΠΑΙΤΕΙΤΑΙ ΕΝΕΡΓΗ ΠΡΟΣΒΑΣΗ PRO"));
        assertTrue(gate.contains("GelProAccountActivity.class"));
        assertFalse(gate.contains("GelProEntitlement.isActive("));
    }

    @Test public void freeCustomerMayAcceptAnEligibleTechniciansInvitation() throws Exception {
        String customer = source("ConnectToTechnicianActivity.java");
        String technician = source("RepairDeviceActivity.java");
        assertFalse(customer.contains("GELServiceSubscriptionGate.requirePaidSubscription(this)"));
        assertFalse(customer.contains("GELServiceSubscriptionGate.showRequired(this)"));
        assertTrue(customer.contains("beginClaimServiceSession(sessionId, serviceCode, qrExpiresAt)"));
        assertTrue(technician.contains("GELServiceSubscriptionGate.requireTechnicianAccess(this"));
        assertTrue(technician.contains("GELServiceSubscriptionGate.hasTechnicianAccess(this)"));
        assertTrue(customer.contains("GELCustomerServiceAccess.grant("));
        assertTrue(customer.contains("GELRemoteDiagnosticsSync.start(this);"));
    }

    @Test public void remoteScreenControlNotLaunchedByTechnicianScreen() throws Exception {
        String technician = source("RepairDeviceActivity.java");
        String viewer = source("GELReadOnlyDiagnosticsActivity.java");
        assertFalse(technician.contains("startActivity(\n                intent\n        );\n    }\n\n    private void showRemoteControlReady()"));
        assertTrue(technician.contains("GELReadOnlyDiagnosticsActivity.class"));
        assertTrue(viewer.contains("READ ONLY"));
        assertFalse(viewer.contains("GELRemoteAssistActivity"));
    }
}
