package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/** Lab27 prerequisite notice must use GEL dialog colors, without altering the gate. */
public final class Lab27PrerequisiteDialogStyleContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void explanationIsNeonAndDialogTitleRemainsUntouched() throws Exception {
        String s = source();
        int start = s.indexOf("private void lab27ShowPrerequisiteDialog(boolean gr, String explanation)");
        int end = s.indexOf("private boolean lab27OemReady(", start);
        assertTrue(start >= 0 && end > start);
        String dlg = s.substring(start, end);
        assertTrue(dlg.contains("content.setText(explanation);"));
        assertTrue(dlg.contains("content.setTextColor(0xFF39FF14);"));
        assertTrue(dlg.contains("LAB 27 — Προϋποθέσεις Πιστοποίησης"));
        assertTrue(dlg.contains("LAB 27 — Certification prerequisites"));
        assertTrue(dlg.contains(".setTitle(gr ?"));
        assertFalse(dlg.contains("content.setTextColor(Color.WHITE)"));
    }

    @Test public void exitIsCanonicalFullWidthRedWhiteYellowGELButton() throws Exception {
        String s = source();
        int start = s.indexOf("private void lab27ShowPrerequisiteDialog(boolean gr, String explanation)");
        int end = s.indexOf("private boolean lab27OemReady(", start);
        assertTrue(start >= 0 && end > start);
        String dlg = s.substring(start, end);
        assertTrue(dlg.contains("lab29DialogButton(gr ? \"ΕΞΟΔΟΣ\" : \"EXIT\", true)"));
        assertTrue(dlg.contains("root.addView(exit, exitParams);"));
        assertTrue(dlg.contains("exit.setOnClickListener(v -> dialog.dismiss());"));
        assertFalse(dlg.contains(".setPositiveButton("));
        int helper = s.indexOf("private Button lab29DialogButton(String text, boolean isExit)");
        int helperEnd = s.indexOf("// LAB 29 — Auto Final Diagnosis", helper);
        assertTrue(helper >= 0 && helperEnd > helper);
        String style = s.substring(helper, helperEnd);
        assertTrue(style.contains("new com.gel.cleaner.GelButton(this)"));
        assertTrue(style.contains("isExit ? Color.WHITE : Color.BLACK"));
        assertTrue(style.contains("isExit ? 0xFFD50000 : 0xFF00CC24"));
        assertTrue(style.contains("buttonBg.setStroke(dp(2), 0xFFFFD700)"));
    }

    @Test public void certificationGateStillRequiresCompletedApplicableLabs() throws Exception {
        String s = source();
        int start = s.indexOf("private void lab30CombineFindings()");
        int end = s.indexOf("private void lab31FinalSummary()", start);
        assertTrue(start >= 0 && end > start);
        String gate = s.substring(start, end);
        assertTrue(gate.contains("lab27PendingBasicLabs(before, applicable)"));
        assertTrue(gate.contains("if (!missing.isEmpty())"));
        assertTrue(gate.contains("lab27ShowPrerequisiteDialog(grGate,"));
        assertTrue(gate.contains("lab27PromptPhysicalConfirmation(grGate, factory);"));
    }
}
