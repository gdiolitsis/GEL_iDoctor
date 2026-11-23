package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/** Lab27 cross-lab certificate must grade serious operational evidence only. */
public final class Lab27ThermalChargingCertificationContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String section(String source, String start, String end) {
        int a = source.indexOf(start), b = source.indexOf(end, a + start.length());
        assertTrue(start, a >= 0 && b > a);
        return source.substring(a, b);
    }

    @Test public void completedLab15PersistsChargeHeatAndConnectionNotChargePercentage() throws Exception {
        String lab = section(source(),
                "private void lab15ChargingSystemSmart()",
                "private void lab16ThermalSnapshot()");
        assertTrue(lab.contains(".putBoolean(\"lab15_overtemp_observed\", lab15OverTempDuringCharge)"));
        assertTrue(lab.contains(".putBoolean(\"lab15_flap_observed\", lab15FlapUnstable)"));
        assertTrue(lab.contains("if (unplugMs < 2000) {"));
        assertTrue(lab.contains("if (unplugMs >= 2000 && unplugMs < 10000) {"));
        assertTrue(lab.contains("lab15FlapUnstable = true;"));
        assertTrue(lab.contains("if (unplugMs >= 10000) {"));
        assertTrue(lab.contains(".putInt(\"lab15_cert_evidence_schema\", Lab27GradeDrivers.EVIDENCE_SCHEMA)"));
        assertTrue(lab.contains(".putLong(\"lab15_cert_evidence_elapsed_ms\", SystemClock.elapsedRealtime())"));
        assertTrue(lab.contains(".remove(\"lab15_cert_evidence_schema\")"));
        assertTrue(lab.contains("\"lab15_diagnostic_outcome_v1\""));
    }

    @Test public void lab16PersistsOsThermalStatusAndEachPhysicalSector() throws Exception {
        String lab = section(source(), "private void lab16ThermalSnapshot()", "private void lab17RunAuto()");
        assertTrue(lab.contains("power.getCurrentThermalStatus()"));
        assertTrue(lab.contains(".putInt(\"lab16_os_thermal_status\", osThermalStatus)"));
        assertTrue(lab.contains(".putFloat(\"lab16_temp_battery\""));
        assertTrue(lab.contains(".putFloat(\"lab16_temp_cpu\""));
        assertTrue(lab.contains(".putFloat(\"lab16_temp_charger\""));
        assertTrue(lab.contains(".putInt(\"lab16_cert_evidence_schema\", Lab27GradeDrivers.EVIDENCE_SCHEMA)"));
        assertTrue(lab.contains(".putLong(\"lab16_cert_evidence_elapsed_ms\", SystemClock.elapsedRealtime())"));
    }

    @Test public void gradeUsesFreshStructuredEvidenceButNotTransientMetrics() throws Exception {
        String src = source();
        String check = section(src, "private boolean lab27HasFreshOperationalEvidence(",
                "private boolean lab27ThermalIsElevated(");
        assertTrue(check.contains("Lab27GradeDrivers.freshEvidence("));
        assertTrue(check.contains("lab27HasFreshOperationalEvidence(before)"));
        assertTrue(check.contains("lab27HasFreshOperationalEvidence(p)"));
        assertTrue(check.contains("Lab27GradeDrivers.fromOperationalSnapshot("));
        assertTrue(check.contains("Lab27FunctionGradePolicy.apply(lab27GradeDrivers)"));
        assertTrue(check.contains("p.getInt(\"lab16_os_thermal_status\", -1)"));
        assertTrue(check.contains("p.getBoolean(\"lab15_overtemp_observed\", false)"));
        assertTrue(check.contains("p.getBoolean(\"lab15_flap_observed\", false)"));
        assertFalse(check.contains("Lab30ScoreEvidence.deviceHealthScore("));
        assertFalse(check.contains("Lab30ScoreEvidence.functionalPerformanceScore("));
        assertTrue(check.contains(".putString(\"lab27_cert_grade_drivers\", lab27GradeDriverCodes)"));
        assertTrue(check.contains(".putString(\"lab27_cert_charge_advisory\", lab27ChargeAdvisory)"));
        assertTrue(check.contains("Η στάθμη") || check.contains("στάθμη φόρτισης"));
    }

    @Test public void buyerGetsExactRecheckReasonsAndImeiCheckOnly() throws Exception {
        String src = source();
        String buyer = section(src, "private void lab28ShowCertificationResult(boolean gr)",
                "private void showLab29ExternalGuidanceDialog(boolean gr)");
        assertTrue(buyer.contains("Lab27GradeDrivers.decode("));
        assertTrue(buyer.contains("driver.observation(gr)"));
        assertTrue(buyer.contains("driver.recommendedCheck(gr)"));
        assertTrue(buyer.contains("lab27_cert_charge_advisory"));
        assertTrue(buyer.contains("IMEI — έλεγχος παρόχων"));
        assertFalse(buyer.contains("getBatteryPercentSafe("));
        assertFalse(buyer.contains("readThermalZones("));
        assertFalse(buyer.contains("getBatteryVoltageFiltered("));
    }
}
