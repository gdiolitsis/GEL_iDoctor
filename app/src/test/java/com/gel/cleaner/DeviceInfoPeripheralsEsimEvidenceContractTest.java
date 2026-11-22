package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** eSIM: do not confuse an Android logical SIM slot with a physical tray. */
public final class DeviceInfoPeripheralsEsimEvidenceContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/DeviceInfoPeripheralsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/DeviceInfoPeripheralsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String helper() throws Exception {
        String s = source();
        int begin = s.indexOf("private static void appendEsimModemStatus(");
        int end = s.indexOf("private String buildWifiAdvancedInfo()", begin);
        assertTrue("eSIM status helper missing", begin >= 0 && end > begin);
        return s.substring(begin, end);
    }

    @Test public void bothLocalAndRemoteModemPanelsHaveEsimEvidence() throws Exception {
        String s = source();
        assertEquals(2, s.split(java.util.regex.Pattern.quote("appendEsimModemStatus(this, data);"), -1).length - 1);
        assertEquals(2, s.split(java.util.regex.Pattern.quote("private String buildModemInfo()"), -1).length - 1);
        assertTrue(s.contains("import android.telephony.euicc.EuiccManager;"));
    }

    @Test public void separateCapabilityAvailabilityAndActiveProfiles() throws Exception {
        String s = helper();
        assertTrue(s.contains("PackageManager.FEATURE_TELEPHONY_EUICC"));
        assertTrue(s.contains("euicc.isEnabled()"));
        assertTrue(s.contains("getActiveSubscriptionInfoList()"));
        assertTrue(s.contains("subscription.isEmbedded()"));
        assertTrue(s.contains("eSIM hardware capability"));
        assertTrue(s.contains("eSIM service available"));
        assertTrue(s.contains("Active eSIM profiles"));
        assertTrue(s.contains("Active subscription types"));
        assertTrue(s.contains("Android logical slot; not physical tray verification"));
    }

    @Test public void inaccessibleOrUnsupportedEsimMustNeverBeReportedAsAbsent() throws Exception {
        String s = helper();
        assertTrue(s.contains("Build.VERSION.SDK_INT < Build.VERSION_CODES.P"));
        assertTrue(s.contains("Manifest.permission.READ_PHONE_STATE"));
        assertTrue(s.contains("Unknown — phone-state permission not granted"));
        assertTrue(s.contains("Unknown — subscription access denied"));
        assertTrue(s.contains("Not declared by Android (not proof of physical absence)"));
        assertTrue(s.contains("0 in Android-visible active subscriptions"));
        assertFalse(s.contains("getAvailableSubscriptionInfoList"));
        assertFalse(s.contains("getEid("));
        assertFalse(s.contains("getIccId("));
        assertFalse(s.contains("getPhoneNumber("));
        assertFalse(s.contains("getImei("));
    }
}
