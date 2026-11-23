package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/** Lab 27: display-only observations must never become functional failures. */
public final class Lab27CertificationNoiseRegressionTest {
    private static String report() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) {
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        }
        String all = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        int first = all.indexOf("private void lab30CombineFindings() {");
        int last = all.indexOf("private boolean lab27ThermalIsElevated(", first);
        assertTrue(first >= 0 && last > first);
        return all.substring(first, last);
    }

    @Test public void staleHiddenSectionsDoNotEmitStandaloneLogSeparators() throws Exception {
        String lab = report();
        int heading = lab.indexOf("Lab27 - Certification καλής λειτουργίας");
        int breakdown = lab.indexOf("Αυτόματη Ανάλυση");
        assertTrue(heading >= 0 && breakdown > heading);
        String introduction = lab.substring(heading, breakdown);
        assertEquals(1, count(introduction, "logLine();"));
        assertFalse(introduction.contains("Uncalibrated diagnosis header not displayed"));

        int summary = lab.indexOf("ΣΥΝΟΨΗ ΜΕΤΡΗΣΕΩΝ ΣΥΣΚΕΥΗΣ");
        int grade = lab.indexOf("Βαθμολογία καλής λειτουργίας", summary);
        assertTrue(summary >= 0 && grade > summary);
        assertEquals(1, count(lab.substring(summary, grade), "logLine();"));
    }

    @Test public void securityAndPrivacyCannotDowngradeFunctionalCertification() throws Exception {
        String lab = report();
        assertFalse(lab.contains("readSecuritySnapshot()"));
        assertFalse(lab.contains("readPrivacySnapshot()"));
        assertFalse(lab.contains("logInfo(gr ? \"Ασφάλεια\" : \"Security\")"));
        assertFalse(lab.contains("logInfo(gr ? \"Ιδιωτικότητα\" : \"Privacy\")"));
        assertFalse(lab.contains("Lab30ScoreEvidence.deviceHealthScore("));
        assertTrue(lab.contains("Lab27FunctionGradePolicy.apply(lab27GradeDrivers)"));
        assertTrue(lab.contains("Lab27GradeDrivers.fromOperationalSnapshot(confirmedCameraFunctionFailure,"));
        assertFalse(lab.contains("confirmedCameraFunctionFailure, lab27EvidenceFindings.size()"));
        assertTrue(lab.contains("if (!lab27GradeDrivers.isEmpty())"));
        assertTrue(lab.contains(".putInt(\"lab27_cert_functional_findings\", confirmedFunctionalFailures)"));
        assertTrue(lab.contains("logLabelInfoValue(gr ? \"Εργαστήρια με παρατηρήσεις\""));
    }

    private static int count(String value, String snippet) {
        int found = 0;
        for (int i = value.indexOf(snippet); i >= 0; i = value.indexOf(snippet, i + snippet.length())) {
            found++;
        }
        return found;
    }
}
