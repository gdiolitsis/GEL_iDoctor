package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Lab 17 only: the 14A/15/16 prerequisite window is 12h, not 2h. */
public final class Lab17TwelveHourPrerequisiteContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String lab17() throws Exception {
        String s = source();
        int a = s.indexOf("private void lab17RunAuto() {");
        int b = s.indexOf("private void lab17_showPopup(", a);
        assertTrue("Lab17 method not found", a >= 0 && b > a);
        return s.substring(a, b);
    }

    @Test public void onlyRequiredPrerequisiteEvidenceUsesTwelveHours() throws Exception {
        String s = lab17();
        assertTrue(s.contains("final long WINDOW_MS = 12L * 60L * 60L * 1000L;"));
        assertTrue(s.contains("has14 && ts14 <= now && (now - ts14) <= WINDOW_MS;"));
        assertTrue(s.contains("has15 && ts15 <= now && (now - ts15) <= WINDOW_MS;"));
        assertTrue(s.contains("has16 && ts16 <= now && (now - ts16) <= WINDOW_MS;"));
        assertTrue(s.contains("if (!(fresh14 && fresh15 && fresh16))"));
        assertFalse(s.contains("final long WINDOW_MS = 2L * 60L * 60L * 1000L;"));
    }

    @Test public void popupTellsTheSameTwelveHourRuleInBothLanguages() throws Exception {
        String s = lab17();
        assertTrue(s.contains("αποτελέσματα τελευταίων 12 ωρών"));
        assertTrue(s.contains("results required within last 12 hours"));
        assertTrue(s.contains("LAB 14A + LAB 15 + LAB 16 μέσα στις τελευταίες 12 ώρες"));
        assertTrue(s.contains("LAB 14A + LAB 15 + LAB 16 within the last 12 hours"));
        assertFalse(s.contains("τελευταίων 2 ωρών"));
        assertFalse(s.contains("results required within last 2 hours"));
    }

    @Test public void unrelatedAuxiliaryFreshnessIsNotSilentlyChanged() throws Exception {
        String s = lab17();
        assertTrue(s.contains("final long OPTIONAL_EVIDENCE_WINDOW_MS = 2L * 60L * 60L * 1000L;"));
        assertTrue(s.contains("has14b && ts14b <= now && (now - ts14b) <= OPTIONAL_EVIDENCE_WINDOW_MS;"));
        assertTrue(s.contains("(hvLastTs - hvFirstTs) <= OPTIONAL_EVIDENCE_WINDOW_MS;"));
    }

    @Test public void twelveHoursIncludesExactBoundaryButExcludesOlderResults() {
        long now = 1_900_000_000_000L;
        long window = 12L * 60L * 60L * 1000L;
        assertTrue(now - (now - window) <= window);
        assertFalse(now - (now - window - 1L) <= window);
        assertFalse(now - (now + 1L) >= 0L);
    }
}
