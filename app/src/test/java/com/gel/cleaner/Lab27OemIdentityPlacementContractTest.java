package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/**
 * User-approved separation: Lab 27 compares reported OEM model specifications;
 * Lab 28 is the used-device buyer check (IMEI, transfer and ownership).
 */
public final class Lab27OemIdentityPlacementContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String section(String source, String from, String to) {
        int start = source.indexOf(from);
        int end = source.indexOf(to, start + from.length());
        assertTrue("Missing method boundary: " + from, start >= 0 && end > start);
        return source.substring(start, end);
    }

    @Test public void factorySpecificationReportBelongsToLab27() throws Exception {
        String s = source();
        String certification = section(s,
                "private void lab30CombineFindings()", "private void lab31FinalSummary()");
        assertTrue(certification.contains("lab27DisplayFactorySpecs(gr);"));
        assertTrue(certification.contains("private void lab27DisplayFactorySpecs(boolean gr)"));
        assertTrue(certification.contains("Lab29FactorySpecAudit.capture(this)"));
        assertTrue(certification.contains("factory.matches"));
        assertTrue(certification.contains("factory.deviations"));
        assertTrue(certification.contains("factory.unknowns"));
        assertTrue(certification.contains("factory.officialSource"));
        assertTrue(certification.contains("Εργοστασιακές προδιαγραφές — τεχνική ταυτοποίηση μοντέλου"));
        assertTrue(certification.contains("Συμφωνία χαρακτηριστικών δεν αποδεικνύει γνήσια εξαρτήματα"));
        assertTrue(certification.contains("if (!lab29RequireLab25Evidence()) return;"));
        assertTrue(certification.contains("lab29SaveTechnicalAssessmentFromLab25(p);"));
        assertTrue(certification.contains("gradeForMeasuredSnapshot(deviceHealthScore)"));
    }

    @Test public void factoryCaptureReusesCommonEngineForPreviouslyMissingMeasurements() throws Exception {
        Path file = Paths.get("app/src/main/java/com/gel/cleaner/Lab29FactorySpecAudit.java");
        if (!Files.isRegularFile(file)) file = Paths.get(
                "src/main/java/com/gel/cleaner/Lab29FactorySpecAudit.java");
        String audit = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        assertTrue(audit.contains("engine.readCpuSnapshot()"));
        assertTrue(audit.contains("s.socModel"));
        assertTrue(audit.contains("engine.readGpuSnapshot()"));
        assertTrue(audit.contains("s.gpuName"));
        assertTrue(audit.contains("engine.readCameraSnapshot()"));
        assertTrue(audit.contains("unit.maxJpegPixels"));
        assertTrue(audit.contains("engine.readConnectivitySnapshot()"));
        assertTrue(audit.contains("s.wifi24GHzSupported"));
        assertTrue(audit.contains("s.wifi5GHzSupported"));
        assertTrue(audit.contains("engine.readBatterySnapshot()"));
        assertTrue(audit.contains("m.batteryChargeNowMah = battery.chargeNowMah"));
        assertTrue(audit.contains("m.batteryChargeFullMah = battery.chargeFullMah"));
        assertTrue(audit.contains("m.batteryLevelPercent"));
        assertTrue(audit.contains("s.nfcSupported"));
    }

    @Test public void unknownOemRowsStillDisplayActualAvailableHardwareEvidence() throws Exception {
        String certification = section(source(),
                "private void lab30CombineFindings()", "private void lab31FinalSummary()");
        assertTrue(certification.contains("String partialObservation ="));
        assertTrue(certification.contains("specRow.observed.trim().isEmpty()"));
        assertTrue(certification.contains("Δεν τεκμηριώθηκε πλήρης συμφωνία"));
        assertTrue(certification.contains("logLabelValue(rowName,"));
    }

    @Test public void usedDeviceCheckKeepsBuyerControlsWithoutRepeatingOemTable() throws Exception {
        String s = source();
        String buyer = section(s,
                "private void runLab29DeviceAuthenticity()",
                "private void showLab29ExternalGuidanceDialog(boolean gr)");
        assertFalse(buyer.contains("Lab29FactorySpecAudit.capture(this)"));
        assertFalse(buyer.contains("factory.rows"));
        assertTrue(buyer.contains("if (!lab28RequireCertification()) return;"));
        assertTrue(buyer.contains("lab28ShowCertificationResult(gr);"));
        assertTrue(buyer.contains("showLab29ExternalGuidanceDialog(gr);"));
        assertTrue(buyer.contains("Η σύγκριση εργοστασιακών προδιαγραφών περιλαμβάνεται στο Lab 27."));
        assertTrue(buyer.contains("Το Lab 28 ολοκληρώθηκε"));
    }
}
