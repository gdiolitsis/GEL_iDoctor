package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/** Regression guards for the final v53 GEL Service Report corrections. */
public final class FinalV53ServiceReportConsistencyContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void batteryRuntimeReportsSignedCoolingAndNoInventedSpeedTest() throws Exception {
        String s = source();
        assertTrue(s.contains("tempRise = endTemp[0] - startTemp[0];"));
        assertTrue(s.contains("String tempStr = String.format(Locale.US, \"%+.1f°C\", tempRise);"));
        assertTrue(s.contains("logLabelOkValue(gr ? \"Μεταβολή θερμοκρασίας\""));
        assertFalse(s.contains("SpeedSim (όχι speed test)"));
    }

    @Test public void storedStressRateUsesSameSourceAsDisplayedRate() throws Exception {
        String s = source();
        assertTrue(s.contains("drainRate =\n            (res.drainMah * 3600000.0) / res.durationMs;"));
        assertTrue(s.contains("(res.validDrain && res.durationMs > 0 && res.drainMah > 0)"));
        assertTrue(s.contains("? (res.drainMah * 3600000.0) / res.durationMs"));
        assertTrue(s.contains("putFloat(\"lab14_stress_drain_mah_per_hour\""));
        assertTrue(s.contains("getFloat(\"lab14_stress_drain_mah_per_hour\""));
    }

    @Test public void chargerAndSystemSummaryExposeProvenanceWithoutFalseFault() throws Exception {
        String s = source();
        assertTrue(s.contains("Μέσο στιγμιαίο ρεύμα μπαταρίας (δείγματα)"));
        assertTrue(s.contains("Η καθαρή αύξηση mAh προκύπτει χωριστά"));
        assertTrue(s.contains("logLabelInfoValue(gr ? \"Απόκριση Lab 14A\""));
        assertTrue(s.contains("logLabelInfoValue(gr ? \"Πτώση τάσης Lab 14A\""));
        assertTrue(s.contains("Η καλή λειτουργία αξιολογείται χωριστά"));
    }

    @Test public void technicianSummaryIsExplicitlyScopedToCurrentSession() throws Exception {
        String s = source();
        assertTrue(s.contains("Lab31ReportEvidencePolicy.withoutPriorSummaries("));
        assertTrue(s.contains("Lab29TechnicianAlerts.collect(sourceLog)"));
        assertTrue(s.contains("Παλαιότερα ευρήματα άλλων συνεδριών δεν αξιολογούνται εδώ."));
    }
}
