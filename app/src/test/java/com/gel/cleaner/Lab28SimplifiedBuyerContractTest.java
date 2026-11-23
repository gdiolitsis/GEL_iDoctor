package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/** Lab28 must not retest functionality already certified in Lab27. */
public final class Lab28SimplifiedBuyerContractTest {
    static String activity() throws Exception {
        Path file = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(file)) {
            file = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        }
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    static String section(String all, String start, String end) {
        int a = all.indexOf(start), b = all.indexOf(end, a + start.length());
        assertTrue("Missing buyer section", a >= 0 && b > a);
        return all.substring(a, b);
    }

    @Test public void noRepeatedBatteryCameraSensorDisplayOrSystemInventory() throws Exception {
        String source = activity();
        String lab = section(source, "private void runLab29DeviceAuthenticity()",
                "private void showLab29ExternalGuidanceDialog(boolean gr)");
        assertTrue(lab.contains("if (!lab28RequireCertification()) return;"));
        assertTrue(lab.contains("lab28ShowCertificationResult(gr);"));
        assertTrue(lab.contains("IMEI — έλεγχος παρόχων"));
        assertTrue(lab.contains("showLab29ExternalGuidanceDialog(gr);"));
        assertFalse(lab.contains("getBatteryPercentSafe("));
        assertFalse(lab.contains("getBatteryVoltageFiltered("));
        assertFalse(lab.contains("getRefreshRate("));
        assertFalse(lab.contains("FEATURE_CAMERA_ANY"));
        assertFalse(lab.contains("SensorManager"));
        assertFalse(lab.contains("Build.FINGERPRINT"));
        assertFalse(lab.contains("Lab29FactorySpecAudit.capture("));
        assertFalse(lab.contains("lab25_obs_level"));
        assertFalse(lab.contains("lab14_collapse_risk"));
        assertFalse(lab.contains("SAFE MODE"));
    }

    @Test public void certificateGradeCopiedFromLab27AndExplainedWithoutRegrading() throws Exception {
        String source = activity();
        String summary = section(source, "private void lab28ShowCertificationResult(boolean gr)",
                "private void lab29DeviceAuthenticity()");
        assertTrue(summary.contains("cert.getString(\"lab27_cert_grade\", \"\")"));
        assertTrue(summary.contains("Lab28BuyerSummary.explain(grade, confirmedFault, gr)"));
        assertTrue(summary.contains("Lab27GradeDrivers.decode("));
        assertTrue(summary.contains("driver.observation(gr)"));
        assertTrue(summary.contains("driver.recommendedCheck(gr)"));
        assertTrue(summary.contains("driver.isConfirmedFailure()"));
        assertTrue(summary.contains("Βαθμός πιστοποίησης"));
        assertFalse(summary.contains("scoreThermals"));
        assertFalse(summary.contains("scoreBattery"));
        assertFalse(summary.contains("Lab27FunctionGradePolicy.apply"));
    }

    @Test public void gateKeepsFreshExactCertificateButNeedsNoInternetForSummary() throws Exception {
        String source = activity();
        String gate = section(source, "private boolean lab28HasFreshCertificationEvidence()",
                "private void lab29DeviceAuthenticity()");
        assertTrue(gate.contains("Lab27CertificationGate.SCHEMA"));
        assertTrue(gate.contains("Lab27CertificationGate.fresh("));
        assertTrue(gate.contains("Lab28BuyerSummary.validGrade(certifiedGrade)"));
        assertTrue(gate.contains("Lab27GradeDrivers.validEvidence("));
        assertTrue(gate.contains("lab27_cert_grade_drivers"));
        assertTrue(gate.contains("Lab29TechnicalAssessment.isCurrent("));
        String entry = section(source, "private void lab29DeviceAuthenticity()",
                "private void runLab29DeviceAuthenticity()");
        assertTrue(entry.contains("if (!lab28RequireCertification()) return;"));
        assertTrue(entry.contains("runLab29DeviceAuthenticity();"));
        assertFalse(entry.contains("Lab29ConnectivityGate.runOrPrompt("));
    }

    @Test public void imeiIsExternalAndMarketplaceRemainsOptional() throws Exception {
        String source = activity();
        String instructions = section(source, "private void showLab29ExternalGuidanceDialog(boolean gr)",
                "private void showLab28MarketResearchDialog(boolean gr)");
        assertTrue(instructions.contains("*#06#"));
        assertTrue(instructions.contains("https://www.checkmend.com/eu/consumer/report"));
        assertTrue(instructions.contains("ΑΓΓΕΛΙΕΣ & ΦΩΤΟΓΡΑΦΙΕΣ eBay"));
        assertFalse(instructions.contains("getDeviceId("));
        assertFalse(instructions.contains("getImei("));
        String market = section(source, "private void showLab28MarketResearchDialog(boolean gr)",
                "private void lab28OpenEbayMarketSearch(");
        assertFalse(market.contains("lab27_cert_grade"));
        assertFalse(market.contains("lab27_cert_camera_failure"));
        assertTrue(market.contains("Lab28MarketResearch.identify("));
    }
}
