package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Only public Android evidence; never certify physical charger hardware. */
public final class Lab15ChargerEvidenceContractTest {
    private static String section() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String src = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int begin = src.indexOf("private void lab15ChargingSystemSmart()");
        int end = src.indexOf("private void lab16ThermalSnapshot()", begin);
        assertTrue(begin >= 0 && end > begin);
        return src.substring(begin, end);
    }

    @Test public void readsPublicBatterySideEvidenceOnly() throws Exception {
        String s = section();
        assertTrue(s.contains("BatteryManager.BATTERY_PROPERTY_CURRENT_NOW"));
        assertTrue(s.contains("getLongProperty("));
        assertTrue(s.contains("Intent.ACTION_BATTERY_CHANGED"));
        assertTrue(s.contains("BatteryManager.EXTRA_PLUGGED"));
        assertTrue(s.contains("BatteryManager.BATTERY_PLUGGED_AC"));
        assertTrue(s.contains("BatteryManager.BATTERY_PLUGGED_USB"));
        assertTrue(s.contains("BatteryManager.BATTERY_PLUGGED_WIRELESS"));
        assertFalse(s.contains("BatteryManager.EXTRA_MAX_CHARGING_CURRENT"));
        assertFalse(s.contains("BatteryManager.EXTRA_MAX_CHARGING_VOLTAGE"));
        assertFalse(s.contains("max_charging_current"));
        assertFalse(s.contains("max_charging_voltage"));
    }

    @Test public void producesNoFalseCurrentOrChargerDiagnosis() throws Exception {
        String s = section();
        assertTrue(s.contains("currentUa != Long.MIN_VALUE"));
        assertTrue(s.contains("currentUa != 0L"));
        assertTrue(s.contains("netCurrentSamples[0] >= 10"));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Πηγή φόρτισης\""));
        assertTrue(s.contains("Μέσο στιγμιαίο ρεύμα μπαταρίας (δείγματα)"));
        assertTrue(s.contains("logLabelWarnValue("));
        assertTrue(s.contains("δεν πιστοποιεί φορτιστή ή καλώδιο"));
        assertTrue(s.contains("όχι την πραγματική ισχύ εξόδου φορτιστή"));
        assertTrue(s.contains("επαναλάβετε με άλλο συμβατό σετ"));
        assertFalse(s.contains("Ο φορτιστής είναι ελαττωματικός"));
        assertFalse(s.contains("Charger defective"));
    }

    @Test public void existingLab15MeasurementsRemainIntact() throws Exception {
        String s = section();
        assertTrue(s.contains("LAB15_TOTAL_SECONDS"));
        assertTrue(s.contains("readBatterySnapshot()"));
        assertTrue(s.contains("long deltaMah = endInfo.chargeNowMah - startMah[0];"));
        assertTrue(s.contains("double mahPerMin = (minutes > 0) ? (deltaMah / minutes) : -1;"));
        assertTrue(s.contains("if (mahPerMin >= 20.0)"));
        assertTrue(s.contains("} else if (mahPerMin >= 10.0)"));
        assertTrue(s.contains("} else if (mahPerMin >= 5.0)"));
        assertTrue(s.contains("lab15_strengthWeak ? \"WEAK\" : \"NORMAL/STRONG\""));
    }
}
