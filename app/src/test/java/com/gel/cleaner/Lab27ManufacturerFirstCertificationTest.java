package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Manufacturer first, user attestation second, and ONLY THEN Lab27 score/certificate. */
public final class Lab27ManufacturerFirstCertificationTest {

    private static String lab27Source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String src = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int begin = src.indexOf("private void lab30CombineFindings() {");
        int end = src.indexOf("private boolean lab27ThermalIsElevated(", begin);
        assertTrue("Lab 27 method", begin >= 0 && end > begin);
        return src.substring(begin, end);
    }

    @Test public void noGradeCanBeCalculatedBeforeUserOemPreflight() throws Exception {
        String s = lab27Source();
        int gate = s.indexOf("if (!lab27UserPreflightAuthorized) {");
        int missing = s.indexOf("lab27PendingBasicLabs(before, applicable)");
        int oem = s.indexOf("Lab29FactorySpecAudit.capture(this)");
        int prompt = s.indexOf("lab27PromptPhysicalConfirmation(grGate, factory)");
        int grade = s.indexOf("Device grade");
        assertTrue(gate >= 0 && missing > gate && oem > missing && prompt > oem);
        assertTrue("Preflight must block before grade", grade > prompt);
        assertTrue(s.contains("lab27UserPreflightAuthorized = false;"));
        assertTrue(s.contains("lab27UserOemSource.equals(currentPorts.sourceUrl)"));
        assertTrue(s.contains("Lab27CertificationGate.physicalMatchesOem("));
        assertTrue(s.contains("if (!eligibleCert)"));
        assertTrue(s.contains("lab27_cert_schema"));
    }

    @Test public void exactOemProfileRequiresPhysicalTrayAndHeadphoneJackOnRedmi() {
        Lab29FactorySpecAudit.Measurements m = new Lab29FactorySpecAudit.Measurements();
        m.manufacturer = "Xiaomi";
        m.model = "23076RN8DY";
        Lab29FactorySpecAudit.Report report = Lab29FactorySpecAudit.evaluate(m);
        Lab29FactorySpecAudit.PhysicalProfile p =
                Lab29FactorySpecAudit.physicalProfile(report);
        assertTrue(p.complete());
        assertTrue(p.sourceUrl.startsWith("https://www.mi.com/"));
        assertEquals(Lab27CertificationGate.Presence.PRESENT, p.usb);
        assertEquals(Lab27CertificationGate.Presence.PRESENT, p.headset);
        assertEquals(Lab27CertificationGate.Presence.PRESENT, p.simTray);
        assertEquals(Lab27CertificationGate.Presence.UNKNOWN, p.esim);
        // Xiaomi's sourced Redmi 12 5G OEM sheet explicitly lists FM radio.
        assertEquals(Lab27CertificationGate.Presence.PRESENT, p.fm);
        assertTrue(Lab27CertificationGate.shouldAsk(p.fm));
        assertTrue(Lab27CertificationGate.physicalMatchesOem(p.usb, p.headset,
                p.simTray, true, false, true, false, true, false));
        assertFalse(Lab27CertificationGate.physicalMatchesOem(p.usb, p.headset,
                p.simTray, true, false, true, false, false, true));
        assertFalse(Lab27CertificationGate.physicalMatchesOem(p.usb, p.headset,
                p.simTray, true, false, false, true, true, false));
        assertFalse(Lab27CertificationGate.physicalMatchesOem(p.usb, p.headset,
                p.simTray, false, true, true, false, true, false));
    }

    @Test public void noOemModelMeansNoPhysicalCertification() {
        Lab29FactorySpecAudit.Measurements m = new Lab29FactorySpecAudit.Measurements();
        m.model = "unknown";
        Lab29FactorySpecAudit.PhysicalProfile p =
                Lab29FactorySpecAudit.physicalProfile(Lab29FactorySpecAudit.evaluate(m));
        assertFalse(p.complete());
        assertFalse(Lab27CertificationGate.physicalMatchesOem(p.usb, p.headset,
                p.simTray, true, false, true, false, true, false));
    }

    @Test public void popupIsManufacturerDirectedAndNeverAsksUnknownAttributes() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String src = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int a = src.indexOf("private void lab27PromptPhysicalConfirmation(");
        int b = src.indexOf("private void lab30CombineFindings()", a);
        assertTrue(a >= 0 && b > a);
        String popup = src.substring(a, b);
        assertTrue(popup.contains("Lab29FactorySpecAudit.physicalProfile(factory)"));
        assertTrue(popup.contains("if (!Lab27CertificationGate.shouldAsk(oemPorts[portIndex])) continue;"));
        assertTrue(popup.contains("for (Lab29FactorySpecAudit.Row row : factory.rows)"));
        assertTrue(popup.contains("portNames[i] = gr ? row.greek : row.english"));
        assertFalse(popup.contains("final String[] physical = gr ? new String[]"));
        assertFalse(popup.contains("esimQuestion"));
        assertFalse(popup.contains("lab27UserEsim"));
        assertFalse(popup.contains("FEATURE_TELEPHONY"));
        assertFalse(popup.contains("hasSystemFeature"));
    }

    @Test public void incompleteOemProfileStillKeepsExplicitKnownPorts() {
        Lab29FactorySpecAudit.PhysicalProfile partial =
                new Lab29FactorySpecAudit.PhysicalProfile("Example", "https://oem.example/spec",
                        Lab27CertificationGate.Presence.PRESENT,
                        Lab27CertificationGate.Presence.UNKNOWN,
                        Lab27CertificationGate.Presence.UNKNOWN,
                        Lab27CertificationGate.Presence.UNKNOWN, false);
        assertTrue(partial.complete());
        assertFalse(Lab27CertificationGate.shouldAsk(partial.headset));
        assertTrue(Lab27CertificationGate.shouldAsk(partial.usb));
        Lab29FactorySpecAudit.PhysicalProfile none =
                new Lab29FactorySpecAudit.PhysicalProfile("Example", "https://oem.example/spec",
                        Lab27CertificationGate.Presence.UNKNOWN,
                        Lab27CertificationGate.Presence.UNKNOWN,
                        Lab27CertificationGate.Presence.UNKNOWN,
                        Lab27CertificationGate.Presence.UNKNOWN, false);
        assertFalse(none.complete());
    }

}
