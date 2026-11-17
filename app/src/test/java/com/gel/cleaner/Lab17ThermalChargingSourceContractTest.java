package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Exact Android-source contract: Lab16 snapshot feeds Lab17. */
public final class Lab17ThermalChargingSourceContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if(!Files.isRegularFile(p)) p=Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p),StandardCharsets.UTF_8);
    }
    private static String section(String s,String a,String b) {
        int begin=s.indexOf(a),end=s.indexOf(b,begin+a.length());
        assertTrue(begin>=0 && end>begin);
        return s.substring(begin,end);
    }
    @Test public void sixMeasuredThermalValuesArePersistedByLab16() throws Exception {
        String lab16=section(source(),"private void lab16ThermalSnapshot()","private void lab17RunAuto()");
        for(String key:new String[] {
                "lab16_temp_battery","lab16_temp_cpu","lab16_temp_gpu",
                "lab16_temp_charger","lab16_temp_pmic","lab16_temp_surface"
        }) {
            assertTrue("Missing "+key,lab16.contains(".putFloat(\""+key+"\""));
        }
        assertTrue(lab16.contains(".putBoolean(\"lab16_detailed_sectors_v1\", true)"));
        assertTrue(lab16.contains("Lab16ThermalCorrelation.capture("));
        assertTrue(lab16.contains("Lab16ThermalCorrelation.evaluate("));
        assertTrue(lab16.contains("currentThermalProfile.elevatedPairs"));
        assertTrue(lab16.contains("logLabelWarnValue(pair.names(gr), pair.details(gr))"));
    }

    @Test public void lab17ReadsAllAvailableSensorsAndComparesEveryElevatedPair() throws Exception {
        String lab17=section(source(),"private void lab17RunAuto()","private void lab17_showPopup(");
        for(String key:new String[] {
                "lab16_temp_battery","lab16_temp_cpu","lab16_temp_gpu",
                "lab16_temp_charger","lab16_temp_pmic","lab16_temp_surface"
        }) assertTrue("Missing "+key, lab17.contains("p.getFloat(\""+key+"\""));
        assertTrue(lab17.contains("lab16DetailedAvailable"));
        assertTrue(lab17.contains("lab16ThermalProfile.elevatedPairs"));
        assertTrue(lab17.contains("for (Lab16ThermalCorrelation.Pair pair : lab16ThermalProfile.elevatedPairs)"));
        assertTrue(lab17.contains("conclusionText += \" \" + lab16ThermalProfile.combinedMessage(gr)"));
        assertTrue(lab17.contains("Lab16ThermalCorrelation.index(lab16PeakSource)"));
    }

    @Test public void speedChargeAdviceIsConditionalAndOnlyForSlowCharging() throws Exception {
        String s=source();
        String lab15=section(s,"private void lab15ChargingSystemSmart()","private void lab16ThermalSnapshot()");
        String lab17=section(s,"private void lab17RunAuto()","private void lab17_showPopup(");
        assertTrue(lab15.contains("} else if (lab15_strengthWeak) {"));
        assertTrue(lab15.contains("Αν η συσκευή υποστηρίζει ταχεία φόρτιση"));
        assertTrue(lab15.contains("κατάλληλο καλώδιο"));
        assertTrue(lab17.contains("else if (chargeRateWarn)"));
        assertTrue(lab17.contains("Αν το κινητό υποστηρίζει ταχεία φόρτιση"));
        assertTrue(lab17.contains("lab15ObservedOvertemp"));
        assertTrue(lab17.contains("lab15ObservedDisconnect"));
        int coolBeforeCharge = lab17.indexOf("if (lab15ObservedOvertemp || thermalConcern || lab14StressThermalWarning)");
        int fastCharging = lab17.indexOf("else if (chargeRateWarn)", coolBeforeCharge);
        assertTrue("Cool-down warning must outrank fast-charger suggestion",
                coolBeforeCharge >= 0 && fastCharging > coolBeforeCharge);
        assertTrue(lab17.contains("chargeEvaluation.chargingConcern"));
        assertTrue(lab17.contains("!chargeEvaluation.unknownChargerWithSlowRate"));
        assertTrue(lab17.contains("chargeEvaluation.description"));
        assertFalse(lab17.contains("Ο φορτιστής σου είναι χαλασμένος"));
    }

    @Test public void intelligentTitleAndEvidenceRequirementStayIntact() throws Exception {
        String s=source();
        assertTrue(s.contains("17. Ευφυής Ανάλυση Υγείας Συστήματος"));
        assertTrue(s.contains("LAB 17 — Ευφυής Ανάλυση Υγείας Συστήματος"));
        assertTrue(s.contains("&& lab16HasEvidence && lab16DetailedAvailable"));
        assertTrue(s.contains("lab16ThermalProfile.hasChipReading()"));
        assertTrue(s.contains("lab17_evidence_only_ts"));
    }
}
