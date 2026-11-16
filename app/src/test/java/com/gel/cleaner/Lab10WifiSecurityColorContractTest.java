package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Presentation only: observed Wi-Fi security mode is distinct from unverified security strength. */
public final class Lab10WifiSecurityColorContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void wifiPskSecurityTypeIsColoredButWpaDetailIsNeutral() throws Exception {
        String source = source();
        int marker = source.indexOf("int wifiSecurityType = WifiInfo.SECURITY_TYPE_UNKNOWN;");
        int start = source.indexOf("case WifiInfo.SECURITY_TYPE_PSK:", marker);
        int end = source.indexOf("case WifiInfo.SECURITY_TYPE_OWE:", start);
        assertTrue(marker >= 0 && start > marker && end > start);
        String branch = source.substring(start, end);
        assertTrue(branch.contains("logLabelOkValue(gr ? \"Τύπος ασφάλειας Wi-Fi\""));
        assertTrue(branch.contains("\"PSK\""));
        assertTrue(branch.contains("logLabelValue(gr ? \"Διευκρίνιση WPA\""));
        assertFalse(branch.contains("logLabelOkValue(gr ? \"Διευκρίνιση WPA\""));
    }

    @Test public void wifiProtectionShowsObservedSecurityWithoutCertifyingStrength() throws Exception {
        String source = source();
        int marker = source.indexOf("logInfo(gr ? \"Συμπέρασμα Lab 10\"");
        int start = source.indexOf("case WifiInfo.SECURITY_TYPE_PSK:", marker);
        int end = source.indexOf("default:", start);
        assertTrue(marker >= 0 && start > marker && end > start);
        String branch = source.substring(start, end);
        assertTrue(branch.contains("case WifiInfo.SECURITY_TYPE_EAP:"));
        assertTrue(branch.contains("logLabelOkValue("));
        assertTrue(branch.contains("gr ? \"Προστασία Wi-Fi\""));
        assertTrue(branch.contains("logLabelValue(gr ? \"Όριο συμπεράσματος\""));
        assertTrue(branch.contains("Η ακριβής ισχύς της προστασίας δεν τεκμηριώθηκε"));
        assertFalse(branch.contains("logLabelOkValue(gr ? \"Όριο συμπεράσματος\""));
    }

    @Test public void weakAndUnknownNetworksAreNotMarkedSafe() throws Exception {
        String source = source();
        assertTrue(source.contains("case WifiInfo.SECURITY_TYPE_OPEN:\n                    logLabelWarnValue("));
        assertTrue(source.contains("case WifiInfo.SECURITY_TYPE_WEP:\n                    logLabelWarnValue("));
        assertTrue(source.contains("Δεν υπάρχουν επαρκή στοιχεία για συμπέρασμα ασφάλειας"));
        assertTrue(source.contains("Δεν επιβεβαιώθηκε προστασία ασύρματης σύνδεσης")
                || source.contains("δεν επιβεβαιώθηκε προστασία ασύρματης σύνδεσης"));
    }
}
