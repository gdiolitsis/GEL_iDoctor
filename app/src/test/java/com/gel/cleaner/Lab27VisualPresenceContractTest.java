package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/** v70 QA: OEM visual presence is not a duplicate functional laboratory test. */
public final class Lab27VisualPresenceContractTest {
    private static String activity() throws Exception {
        Path file = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(file))
            file = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    private static String section(String text, String from, String to) {
        int start = text.indexOf(from);
        int end = text.indexOf(to, start + from.length());
        assertTrue("Source section missing: " + from, start >= 0 && end > start);
        return text.substring(start, end);
    }

    @Test public void popupIsGeneratedOnlyFromDocumentedOemPorts() throws Exception {
        String popup = section(activity(), "private void lab27PromptPhysicalConfirmation(",
                "private void lab30CombineFindings()");
        assertTrue(popup.contains("Lab29FactorySpecAudit.physicalProfile(factory)"));
        assertTrue(popup.contains("final Lab27CertificationGate.Presence[] oemPorts"));
        assertTrue(popup.contains("for (Lab29FactorySpecAudit.Row row : factory.rows)"));
        assertTrue(popup.contains("portNames[i] = gr ? row.greek : row.english"));
        assertTrue(popup.contains("if (!Lab27CertificationGate.shouldAsk(oemPorts[portIndex])) continue;"));
        assertTrue(popup.contains("profile.usb, profile.headset, profile.simTray, profile.fm"));
        assertTrue(popup.contains("\"FM radio\""));
        assertTrue(popup.contains("Εφαρμογή ραδιοφώνου "));
        assertTrue(popup.contains("Lab27CertificationGate.fmAppQuestionComplete("));
        assertTrue(popup.contains("lab27UserFmAppPresent = lab27Checked(checks[6])"));
        assertTrue(popup.contains("lab27UserFmAppAbsent = lab27Checked(checks[7])"));
        assertTrue(popup.contains("CheckBox[] checks = new CheckBox[oemPorts.length * 2]"));
        assertTrue(popup.contains("check.setText(portNames[portIndex] + \": \""));
        assertTrue(popup.contains("checks[index ^ 1].setChecked(false)"));
        assertTrue(popup.contains("lab27Checked(checks[0])"));
        assertTrue(popup.contains("lab27UserUsbAbsent = lab27Checked(checks[1])"));
        assertTrue(popup.contains("lab27UserPreflightAuthorized = true;"));
        assertFalse(popup.contains("final String[] physical = gr ? new String[]"));
        assertFalse(popup.contains("esimQuestion"));
        assertFalse(popup.contains("lab27UserEsim"));
        assertTrue(popup.contains("Η φόρτιση εξετάζεται στο Lab 15"));
        assertTrue(popup.contains("SIM/eSIM στο Lab 12"));
    }

    @Test public void factoryReportDistinguishesAutomaticAndHumanEvidence() throws Exception {
        String report = section(activity(), "private void lab27DisplayFactorySpecs(boolean gr)",
                "private StorageSnapshot readStorageSnapshot()");
        assertTrue(report.contains("\"USB connector\".equals(specRow.english)"));
        assertTrue(report.contains("\"3.5 mm jack\".equals(specRow.english)"));
        assertTrue(report.contains("\"SIM tray\".equals(specRow.english)"));
        assertTrue(report.contains("Σύμφωνα με τη δήλωση χρήστη "));
        assertTrue(report.contains("declaredPresent ? \"υπάρχει\" : \"δεν υπάρχει\""));
        assertTrue(report.contains("According to the user declaration, "));
        assertTrue(report.contains("verifiedByUser++"));
        assertTrue(report.contains("mismatchedByUser++"));
        assertTrue(report.contains("unknownsResolvedByUser++"));
        assertTrue(report.contains("factory.unknowns - unknownsResolvedByUser"));
        assertTrue(report.contains("Χαρακτηριστικά χωρίς διαθέσιμη σύγκριση"));
        assertTrue(report.contains("logLabelOkValue(rowName, value)"));
        assertTrue(report.contains("logLabelErrorValue(rowName, value)"));
        assertTrue(report.contains("continue; // Do not show obsolete"));
        assertTrue(report.contains("\"FM radio\".equals(specRow.english)"));
        assertTrue(report.contains("Σύμφωνα με τη δήλωση χρήστη "));
        assertTrue(report.contains("υπάρχει εφαρμογή ραδιοφώνου FM."));
        assertTrue(report.contains("δεν υπάρχει εφαρμογή ραδιοφώνου FM."));
        assertTrue(report.contains("Η απουσία εφαρμογής δεν αποδεικνύει απουσία δέκτη FM."));
        assertTrue(report.contains("App presence is not a measured FM reception verdict."));
    }

    @Test public void oldCertificatesCannotBypassOemSelectedVisualAnswers() {
        assertEquals(11, Lab27CertificationGate.SCHEMA);
        Lab27CertificationGate.Presence yes = Lab27CertificationGate.Presence.PRESENT;
        Lab27CertificationGate.Presence missing = Lab27CertificationGate.Presence.UNKNOWN;
        // This model's OEM does not describe any headphone jack.
        assertTrue(Lab27CertificationGate.physicalChecksComplete(
                yes, missing, yes, true, false, false, false, true, false));
        assertTrue(Lab27CertificationGate.physicalMatchesOem(
                yes, missing, yes, true, false, false, false, true, false));
        assertFalse(Lab27CertificationGate.physicalChecksComplete(
                yes, missing, yes, true, false, false, true, true, false));
    }

    @Test public void anyOemMismatchStillReachesTheReportButNeverPositiveCertification() throws Exception {
        String src = activity();
        String popup = section(src, "private void lab27PromptPhysicalConfirmation(",
                "private void lab30CombineFindings()");
        String report = section(src, "private void lab30CombineFindings()",
                "private boolean lab27ThermalIsElevated(");
        assertFalse(popup.contains("if (!basic || !oemMatch"));
        assertTrue(popup.contains("lab30CombineFindings();"));
        assertTrue(report.contains("lab27UserOemSource.equals(currentPorts.sourceUrl)"));
        assertTrue(report.contains("Lab27CertificationGate.physicalChecksComplete("));
        assertTrue(report.contains("boolean physicalVerified = Lab27CertificationGate.physicalMatchesOem("));
        assertTrue(report.contains("physicalVerified, certFactory.supportedModel"));
        assertTrue(report.contains("if (!eligibleCert)"));
    }
}
