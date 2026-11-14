package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Records grants; never infers unnecessary, harmful or abused permissions. */
public final class Lab27GrantedPermissionsContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String lab() throws Exception {
        String s = source();
        int a = s.indexOf("private void lab27PermissionsPrivacy() {");
        int b = s.indexOf("// INTERNAL helpers for Lab 27", a);
        assertTrue(a >= 0 && b > a);
        return s.substring(a, b);
    }

    @Test public void titlePromisesAnInventoryNotAnAppRiskDiagnosis() throws Exception {
        String s = source();
        assertTrue(s.contains("27. Χορηγημένες άδειες εφαρμογών"));
        String l = lab();
        assertTrue(l.contains("LAB 27 — Χορηγημένες άδειες εφαρμογών"));
        assertFalse(l.contains("Αυτόματη Σάρωση"));
    }

    @Test public void usesRealGrantedFlagAndRuntimePermissionsOnly() throws Exception {
        String l = lab();
        assertTrue(l.contains("PackageManager.GET_PERMISSIONS"));
        assertTrue(l.contains("requestedPermissionsFlags"));
        assertTrue(l.contains("android.content.pm.PermissionInfo.PROTECTION_DANGEROUS"));
        assertTrue(l.contains("isGrantedFlag(flags, i)"));
        assertTrue(l.contains("if (flags == null || i >= flags.length)"));
        assertFalse(l.contains("permissionWeight("));
        assertFalse(l.contains("Επικίνδυνες"));
    }

    @Test public void verifiedCountsGreenIndividualGrantNamesNeutral() throws Exception {
        String l = lab();
        assertTrue(l.contains("logLabelOkValue(gr ? \"Ορατές εφαρμογές\""));
        assertTrue(l.contains("logLabelOkValue(gr ? \"Εφαρμογές χρήστη που εξετάστηκαν\""));
        assertTrue(l.contains("logLabelOkValue(gr ? \"Χορηγημένες ευαίσθητες άδειες\""));
        assertTrue(l.contains("Math.min(8, details.size())"));
        assertTrue(l.contains("logLabelValue(gr ? \"Εφαρμογή\""));
        assertTrue(l.contains("lab27PermissionLabel(permission, gr)"));
        assertFalse(l.contains("Βάρος αδειών"));
        assertFalse(l.contains("Δείκτης κινδύνου"));
        assertFalse(l.contains("1827"));
    }

    @Test public void avoidsCertifyingPermissionNeedOrAbuse() throws Exception {
        String l = lab();
        assertTrue(l.contains("Η αναγκαιότητά τους δεν ελέγχθηκε."));
        assertFalse(l.contains("περιττές άδειες"));
        assertFalse(l.contains("κακόβουλη συμπεριφορά"));
        assertTrue(l.contains("logOk(gr ? \"Το Lab 27 ολοκληρώθηκε"));
        assertTrue(l.contains("SUMMARY: GRANTED_RUNTIME_PERMISSIONS="));
    }
}
