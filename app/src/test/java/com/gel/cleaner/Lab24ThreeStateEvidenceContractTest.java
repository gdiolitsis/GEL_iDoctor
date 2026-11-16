package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/** UI contract for independent Android bootloader, Verified Boot and Root evidence. */
public final class Lab24ThreeStateEvidenceContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) {
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        }
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String section(String source, String from, String to) {
        int start = source.indexOf(from);
        int end = source.indexOf(to, start + from.length());
        assertTrue("Cannot find " + from, start >= 0 && end > start);
        return source.substring(start, end);
    }

    @Test public void buttonAndReportNamesMatchAndAllowTwoLineButton() throws Exception {
        String src = source();
        assertTrue(src.contains("24. Έλεγχος Root, Bootloader & Verified Boot"));
        assertTrue(src.contains("LAB 24 — Έλεγχος Root, Bootloader & Verified Boot"));
        assertTrue(src.contains("24. Root, Bootloader & Verified Boot Check"));
        assertTrue(src.contains("LAB 24 — Root, Bootloader & Verified Boot Check"));
        assertFalse(src.contains("24. Ένδειξη Root / Ξεκλείδωτου Bootloader"));
        assertTrue(src.contains("b.setSingleLine(false)"));
        assertTrue(src.contains("b.setMaxLines(2)"));
    }

    @Test public void allThreeIndependentStatesAndUnknownsAreShown() throws Exception {
        String lab = section(source(),
                "private void lab24RootSuspicion()", "// LAB 24 — INTERNAL HELPERS");
        assertTrue(lab.contains("1. Κατάσταση Bootloader"));
        assertTrue(lab.contains("2. Verified Boot — επαλήθευση εκκίνησης"));
        assertTrue(lab.contains("3. Ένδειξη Root / πλήρης πρόσβαση"));
        assertTrue(lab.contains("4. Συμπέρασμα και σύσταση"));
        assertTrue(lab.contains("Κλειδωμένος (συμφωνούν οι ενδείξεις Android)"));
        assertTrue(lab.contains("Ξεκλείδωτος (αναφορά Android)"));
        assertTrue(lab.contains("Μη επιβεβαιωμένος — ανεπαρκή στοιχεία κλειδώματος"));
        assertTrue(lab.contains("bootSignalsConflict"));
        assertTrue(lab.contains("reportedLocked && reportedUnlocked"));
        assertTrue(lab.contains("(vbOrange && reportedLocked)"));
    }

    @Test public void verifiedBootColorsFollowActualDocumentedStates() throws Exception {
        String lab = section(source(),
                "private void lab24RootSuspicion()", "// LAB 24 — INTERNAL HELPERS");
        assertTrue(lab.contains("vbGreen"));
        assertTrue(lab.contains("vbYellow"));
        assertTrue(lab.contains("vbOrange"));
        assertTrue(lab.contains("bootFailed"));
        assertTrue(lab.contains("GREEN — επιτυχής επαλήθευση σύμφωνα με το Android"));
        assertTrue(lab.contains("YELLOW — επαλήθευση με εναλλακτικό κλειδί, όχι αποτυχία"));
        assertTrue(lab.contains("ORANGE — ακεραιότητα εκκίνησης μη εγγυημένη"));
        assertTrue(lab.contains("RED — αναφέρεται αποτυχία επαλήθευσης εκκίνησης"));
        assertTrue(lab.contains("Μη διαθέσιμη ή άγνωστη κατάσταση Verified Boot"));
        assertTrue(lab.contains("logLabelErrorValue("));
        assertTrue(lab.contains("logLabelWarnValue("));
        assertTrue(lab.contains("logLabelOkValue("));
        assertTrue(lab.contains("logLabelValue("));
    }

    @Test public void rootChecksAndReadOnlyBootDetectorsStayIntact() throws Exception {
        String lab = section(source(),
                "private void lab24RootSuspicion()", "// LAB 24 — INTERNAL HELPERS");
        assertTrue(lab.contains("lab24_canExecSu()"));
        assertTrue(lab.contains("lab24_getInstalledPackagesLower()"));
        assertTrue(lab.contains("lab24_magiskStealthScore(magiskStealthFindings)"));
        assertTrue(lab.contains("ro.boot.verifiedbootstate"));
        assertTrue(lab.contains("ro.boot.vbmeta.device_state"));
        assertTrue(lab.contains("ro.boot.flash.locked"));
        assertTrue(lab.contains("boolean meaningfulRootHint = suFound || pkgHit"));
        assertTrue(lab.contains("Η απουσία ενδείξεων δεν αποκλείει κρυμμένο Root"));
        assertTrue(lab.contains("Η επιλογή OEM unlocking δεν αποδεικνύει"));
        assertTrue(lab.contains("Δεν αποτελεί γενική πιστοποίηση ασφάλειας"));
        assertFalse(lab.contains("suExec = false;"));
        assertFalse(lab.contains("Runtime.getRuntime().exec(\"fastboot"));
    }
}
