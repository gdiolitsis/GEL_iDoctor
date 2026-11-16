package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Historical Lab27 grant contract, migrated to the existing Lab26 inventory. */
public final class Lab27GrantedPermissionsContractTest {
    private static String source(String name) throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/" + name);
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/" + name);
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void lab27PermissionsIsRemovedAndFindingsLiveInLab26() throws Exception {
        String s = source("ManualTestsActivity.java");
        assertFalse(s.contains("private void lab27PermissionsPrivacy()"));
        assertFalse(s.contains("this::lab27PermissionsPrivacy"));
        assertFalse(s.contains("27. Χορηγημένες άδειες εφαρμογών"));
        int a = s.indexOf("private void lab26AppsFootprint()");
        int b = s.indexOf("// ROOT HELPER — BEST EFFORT DIRECTORY SIZE", a);
        assertTrue(a >= 0 && b > a);
        String lab26 = s.substring(a, b);
        assertTrue(lab26.contains("5. Χορηγημένες ευαίσθητες άδειες"));
        assertTrue(lab26.contains("r.userAppsWithGrantedSensitivePermissions"));
        assertTrue(lab26.contains("r.permissionGrantFlagsUnavailableApps"));
        assertTrue(lab26.contains("Η χορήγηση αδειών δεν αποδεικνύει κατάχρηση"));
        assertFalse(lab26.contains("Δείκτης κινδύνου"));
        assertFalse(lab26.contains("permissionWeight("));
    }

    @Test public void actualGrantedFlagCheckedAndOnlyForVisibleUserApps() throws Exception {
        String s = source("AppImpactEngine.java");
        assertTrue(s.contains("requestedPermissionsFlags"));
        assertTrue(s.contains("PackageInfo.REQUESTED_PERMISSION_GRANTED"));
        assertTrue(s.contains("isSensitivePermissionForDisclosure(p)"));
        assertTrue(s.contains("!isSystem && reqGrantFlags != null"));
        assertTrue(s.contains("userAppsWithGrantedSensitivePermissions.add(s)"));
    }

    @Test public void retainedImprovedLabsAndTwentyNineLabMenu() throws Exception {
        String s = source("ManualTestsActivity.java");
        assertFalse(s.contains("27. Καταγραφές λειτουργίας και μετρήσεις"));
        assertFalse(s.contains("private void lab28HardwareStability()"));
        assertTrue(s.contains("27. Προέλεγχος Μεταχειρισμένης"));
        assertTrue(s.contains("28. GEL Device Certification"));
        assertTrue(s.contains("29. Τελική Τεχνική Αναφορά"));
        assertFalse(s.contains("30. Τελική Τεχνική Αναφορά"));
        assertTrue(s.contains("Lab29FactorySpecAudit.capture(this)"));
        assertTrue(s.contains("showLab29ExternalGuidanceDialog(gr)"));
        assertTrue(s.contains("Lab31ReportEvidencePolicy.withoutPriorSummaries"));
    }
    @Test public void proLockDialogUsesRenumberedLabelsInBothLanguages() throws Exception {
        String s = source("ManualTestsActivity.java");
        int a = s.indexOf("private void showGelProLockedDialog(String featureName)");
        int b = s.indexOf("// LAB 3 — STATE (CLASS LEVEL)", a);
        assertTrue(a >= 0 && b > a);
        String popup = s.substring(a, b);
        assertTrue(popup.contains("Το LAB 27 καταγράφει τα διαθέσιμα στοιχεία Android"));
        assertTrue(popup.contains("LAB 27 records Android-exposed device data"));
        assertTrue(popup.contains("Το LAB 28 συνδυάζει τα διαγνωστικά δεδομένα"));
        assertTrue(popup.contains("LAB 28 combines diagnostic data across the device"));
        assertFalse(popup.contains("Το LAB 28 καταγράφει τα διαθέσιμα στοιχεία Android"));
        assertFalse(popup.contains("LAB 29 combines diagnostic data across the device"));
    }

}
