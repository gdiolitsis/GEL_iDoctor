package com.gel.cleaner;
import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;
/** PR144's checkpoint adapted for final v57 numbering, not obsolete interim order. */
public final class Lab29FromLab25CheckpointContractTest {
    static String source() throws Exception {
        Path p=Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p=Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p),StandardCharsets.UTF_8);
    }
    @Test public void certificationConsumesLab25AndPersistsOnlyBoundedAssessment() throws Exception {
        String s=source(),cert=s.substring(s.indexOf("private void lab30CombineFindings()"),s.indexOf("private void lab31FinalSummary()"));
        assertTrue(cert.contains("if (!lab29RequireLab25Evidence()) return;"));
        // Lab 25 remains a mandatory evidence gate but buyer-focused history is not duplicated in Lab 27.
        assertFalse(cert.contains("lab29DisplayLab25Findings(p, gr);"));
        assertTrue(cert.contains("lab29SaveTechnicalAssessmentFromLab25(p);"));
        String writer=s.substring(s.indexOf("private void lab29SaveTechnicalAssessmentFromLab25("),s.indexOf("private void lab30CombineFindings()"));
        assertTrue(writer.contains("lab29_technical_source25_wall_ms"));
        assertTrue(writer.contains("lab29_technical_history_coverage"));
        assertTrue(writer.contains("Lab29TechnicalAssessment.summarize("));
        assertFalse(writer.contains("putString(\"raw_bugreport\""));
    }
    @Test public void oldLab27GoneAndFinalOrderCorrect() throws Exception {
        String s=source();
        assertFalse(s.contains("private void lab28HardwareStability()"));
        assertTrue(s.contains("this::lab30CombineFindings"));
        assertTrue(s.contains("this::lab29DeviceAuthenticity"));
        assertTrue(s.contains("this::lab31FinalSummary"));
        int cert=s.indexOf("PRO  27. GEL Device Certification");
        int buyer=s.indexOf("PRO  28. Προέλεγχος Μεταχειρισμένης");
        int finalReport=s.indexOf("29. Τελική Τεχνική Αναφορά");
        assertTrue(cert>0&&buyer>cert&&finalReport>buyer);
    }
}
