package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.Assert.*;

/** Technician-only final Lab 29: source attribution, original severity, no padding. */
public final class Lab29TechnicianAlertsTest {
    @Test public void onlyTypedYellowAndRedLabObservationsAreIncluded() {
        String log = "ℹ LAB 14 — Μπαταρία\n"
                + "Μπαταρία: 81%\n"
                + "✔ Το Lab 14 ολοκληρώθηκε\n"
                + "⚠ Θερμοκρασία μπαταρίας: 44.5°C\n"
                + "ℹ LAB 25 — Ιστορικό Android\n"
                + "✖ Σφάλμα συστήματος: επιβεβαιωμένη αποτυχία\n"
                + "⚠ Ιστορικό: επαναλαμβανόμενα συμβάντα\n"
                + "ℹ LAB 27 — Διαγνωστική σύνοψη\n"
                + "Γνησιότητα συσκευής: ΜΗ ΕΠΑΛΗΘΕΥΜΕΝΗ\n"
                + "Υγεία μπαταρίας (SoH): ΔΕΝ ΜΕΤΡΗΘΗΚΕ\n";
        List<Lab29TechnicianAlerts.Finding> findings = Lab29TechnicianAlerts.collect(log);
        assertEquals(3, findings.size());
        assertEquals("Lab 14", findings.get(0).sourceLab);
        assertEquals("Θερμοκρασία μπαταρίας: 44.5°C", findings.get(0).message);
        assertEquals(Lab29TechnicianAlerts.Severity.WARNING, findings.get(0).severity);
        assertEquals("Lab 25", findings.get(1).sourceLab);
        assertEquals(Lab29TechnicianAlerts.Severity.ERROR, findings.get(1).severity);
        assertTrue(findings.get(1).message.contains("επιβεβαιωμένη αποτυχία"));
        assertEquals(1, Lab29TechnicianAlerts.count(
                findings, Lab29TechnicianAlerts.Severity.ERROR));
        assertEquals(2, Lab29TechnicianAlerts.count(
                findings, Lab29TechnicianAlerts.Severity.WARNING));
    }

    @Test public void priorReportsAreExcludedEvenWhenRepeatedOrInterrupted() {
        String log = "ℹ LAB 16 — Thermal\n"
                + "⚠ CPU: Elevated\n"
                + "ℹ LAB 29 — TECHNICIAN WARNING AND FAILURE SUMMARY\n"
                + "✖ Lab 16: Previously repeated error\n"
                + "⚠ Lab 16: Previously repeated warning\n"
                + "✔ Το Lab 29 ολοκληρώθηκε — σύνοψη τεχνικών ευρημάτων.\n"
                + "ℹ LAB 17 — Battery\n"
                + "✖ Battery: recorded hardware error\n"
                + "ℹ LAB 29 — ΤΕΧΝΙΚΗ ΣΥΝΟΨΗ ΠΡΟΕΙΔΟΠΟΙΗΣΕΩΝ ΚΑΙ ΑΣΤΟΧΙΩΝ\n"
                + "⚠ Lab 17: old report\n";
        String clean = Lab31ReportEvidencePolicy.withoutPriorSummaries(log);
        List<Lab29TechnicianAlerts.Finding> findings = Lab29TechnicianAlerts.collect(clean);
        assertEquals(2, findings.size());
        assertEquals("Lab 16", findings.get(0).sourceLab);
        assertEquals("Lab 17", findings.get(1).sourceLab);
        assertEquals(Lab29TechnicianAlerts.Severity.ERROR, findings.get(1).severity);
        assertFalse(clean.contains("Previously repeated"));
        assertFalse(clean.contains("old report"));
    }

    @Test public void missingOrNeutralObservationsNeverBecomeWarnings() {
        assertTrue(Lab29TechnicianAlerts.collect(null).isEmpty());
        assertTrue(Lab29TechnicianAlerts.collect("").isEmpty());
        String log = "ℹ LAB 27 — Device model\n"
                + "Camera: Not verified\n"
                + "✖\n"
                + "⚠   \n"
                + "✔ 22 matches\n"
                + "Battery SoH: NOT MEASURED\n";
        assertTrue(Lab29TechnicianAlerts.collect(log).isEmpty());
    }

    @Test public void finalMethodContainsNoDuplicatedBatteryIdentityOrImeiExplanations() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String source = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int first = source.indexOf("private void lab31FinalSummary()");
        int next = source.indexOf("private void enableSingleExportButton()", first);
        assertTrue(first >= 0 && next > first);
        String report = source.substring(first, next);
        assertTrue(report.contains("Lab29TechnicianAlerts.collect(sourceLog)"));
        assertTrue(report.contains("Lab31ReportEvidencePolicy.withoutPriorSummaries("));
        assertTrue(report.contains("logLabelErrorValue(label, finding.message)"));
        assertTrue(report.contains("logLabelWarnValue(label, finding.message)"));
        assertTrue(report.contains("enableSingleExportButton()"));
        assertFalse(report.contains("lab14bEstimatedHours"));
        assertFalse(report.contains("lab29_obs_manufacturer"));
        assertFalse(report.contains("Γνησιότητα συσκευής / ανταλλακτικών"));
        assertFalse(report.contains("Ανεξάρτητος έλεγχος IMEI"));
        assertFalse(report.contains("Εκτιμώμενη αυτονομία"));
        assertFalse(report.contains("Χρησιμοποιήστε το κουμπί για εξαγωγή"));
    }
}
