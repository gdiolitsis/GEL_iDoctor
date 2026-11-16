package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Checkpoint B1 must not silently perform the C/D lab-order migration. */
public final class Lab29FromLab25CheckpointContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
    @Test public void certificationRequiresFreshLab25WithoutChangingProEntitlement() throws Exception {
        String s = source();
        int start = s.indexOf("private void lab30CombineFindings()");
        int stop = s.indexOf("private void lab31FinalSummary()", start);
        assertTrue(start >= 0 && stop > start);
        String m = s.substring(start, stop);
        int pro = m.indexOf("requireGelPro(");
        int prerequisite = m.indexOf("if (!lab29RequireLab25Evidence()) return;");
        int read = m.indexOf("lab29DisplayLab25Findings(p, gr)");
        int stored = m.indexOf("lab29SaveTechnicalAssessmentFromLab25(p)");
        assertTrue(pro >= 0 && prerequisite > pro);
        assertTrue(read > prerequisite);
        assertTrue(stored > read);
    }
    @Test public void persistSummaryWithSameBootEvidenceNotRawCrashLogs() throws Exception {
        String s = source();
        String m = s.substring(s.indexOf("private void lab29SaveTechnicalAssessmentFromLab25("),
                s.indexOf("private void lab30CombineFindings()"));
        assertTrue(m.contains("lab29_technical_source25_wall_ms"));
        assertTrue(m.contains("lab29_technical_source25_elapsed_ms"));
        assertTrue(m.contains("lab29_technical_history_coverage"));
        assertTrue(m.contains("Lab29TechnicalAssessment.summarize("));
        assertTrue(m.contains("Lab25BuyerEvidence.canUse("));
        assertFalse(m.contains("putString(\"raw_bugreport\""));
    }
    @Test public void keepOldLab27AndExistingBuyerFlowUntouchedInCheckpointB1() throws Exception {
        String s = source();
        assertTrue(s.contains("private void lab28HardwareStability()"));
        assertTrue(s.contains("this::lab29DeviceAuthenticity"));
        assertTrue(s.contains("this::lab30CombineFindings"));
        assertTrue(s.contains("this::lab31FinalSummary"));
        assertTrue(s.contains("27. Καταγραφές λειτουργίας και μετρήσεις"));
        assertTrue(s.contains("30. Τελική Τεχνική Αναφορά"));
    }
}
