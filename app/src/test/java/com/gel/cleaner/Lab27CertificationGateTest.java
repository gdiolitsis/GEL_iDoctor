package com.gel.cleaner;

import org.junit.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.*;

/** Rules must fail closed for omitted, stale, warned or unconfirmed evidence. */
public final class Lab27CertificationGateTest {
    @Test public void exactlyEighteenRequiredBasicLabs() {
        assertEquals(18, Lab27CertificationGate.REQUIRED.size());
        assertTrue(Lab27CertificationGate.REQUIRED.contains("9"));
        assertTrue(Lab27CertificationGate.REQUIRED.contains("14A"));
        assertTrue(Lab27CertificationGate.REQUIRED.contains("25"));
        assertFalse(Lab27CertificationGate.REQUIRED.contains("14B"));
        assertFalse(Lab27CertificationGate.REQUIRED.contains("19"));
        assertFalse(Lab27CertificationGate.REQUIRED.contains("28"));
    }

    @Test public void compactPrerequisiteNoticePreservesExactPendingLabs() {
        assertEquals("1–14Α, 15, 16, 18, 25",
                Lab27CertificationGate.compactPending(Lab27CertificationGate.REQUIRED, true));
        assertEquals("1–14A, 15, 16, 18, 25",
                Lab27CertificationGate.compactPending(Lab27CertificationGate.REQUIRED, false));
        assertEquals("2, 14Α, 25",
                Lab27CertificationGate.compactPending(Arrays.asList("2", "14A", "25"), true));
        assertEquals("",
                Lab27CertificationGate.compactPending(Arrays.<String>asList(), true));
    }

    @Test public void onlyActualPositiveCompletionMessagesAreCounted() {
        assertEquals("3", Lab27CertificationGate.completedLab("Το Lab 3 ολοκληρώθηκε."));
        assertEquals("9", Lab27CertificationGate.completedLab("Το Lab 9 ολοκληρώθηκε."));
        assertEquals("4", Lab27CertificationGate.completedLab("Το Lab 4 BASE ολοκληρώθηκε."));
        assertEquals("14A", Lab27CertificationGate.completedLab(
                "Το Lab 14A ολοκληρώθηκε (έγκυρη καταγραφή δοκιμής καταπόνησης)."));
        assertEquals("25", Lab27CertificationGate.completedLab(
                "Lab 25 completed — only evidenced critical findings were presented."));
        assertNull(Lab27CertificationGate.completedLab("Lab 7 partially completed"));
        assertNull(Lab27CertificationGate.completedLab("Το Lab 7 ολοκληρώθηκε μερικώς"));
        assertNull(Lab27CertificationGate.completedLab("Το Lab 8 δεν ολοκληρώθηκε"));
        assertNull(Lab27CertificationGate.completedLab("Lab 12 passive inventory finished (not a real-call test)."));
        assertNull(Lab27CertificationGate.completedLab("Το Lab 27 ολοκληρώθηκε."));
        assertEquals("14A", Lab27CertificationGate.labFromHeader("LAB 14 — Δοκιμή Καταπόνησης"));
        assertNull(Lab27CertificationGate.labFromHeader("LAB 14B — Αυτονομία"));
    }

    @Test public void staleWarnedAndMissingLabsBlockCertificate() {
        long now = 1000000000L;
        Map<String, Long> timestamps = new HashMap<>();
        Map<String, Boolean> passed = new HashMap<>();
        List<String> applicable = Lab27CertificationGate.REQUIRED;
        for (String id : applicable) {
            timestamps.put(id, now - 1000L);
            passed.put(id, true);
        }
        assertTrue(Lab27CertificationGate.pending(applicable, timestamps, passed, now).isEmpty());
        passed.put("3", false);
        assertTrue(Lab27CertificationGate.pending(applicable, timestamps, passed, now).isEmpty());
        assertEquals(Arrays.asList("3"),
                Lab27CertificationGate.withFindings(applicable, timestamps, passed, now));
        passed.put("3", true);
        assertTrue(Lab27CertificationGate.withFindings(applicable, timestamps, passed, now).isEmpty());
        timestamps.put("15", now - Lab27CertificationGate.MAX_AGE_MS - 1L);
        assertEquals(Arrays.asList("15"),
                Lab27CertificationGate.pending(applicable, timestamps, passed, now));
        timestamps.remove("15");
        assertEquals(Arrays.asList("15"),
                Lab27CertificationGate.pending(applicable, timestamps, passed, now));
    }

    @Test public void functionalCertificationStillRequiresFactorySpecificationComparison() {
        // OEM specifications are an indispensable cross-check, NEVER parts authenticity.
        assertFalse(Lab27CertificationGate.eligible(19, 19, false, true, 27, 0, false, "A+"));
        assertFalse(Lab27CertificationGate.eligible(18, 19, true, true, 27, 0, false, "A+"));
        assertFalse(Lab27CertificationGate.eligible(19, 19, true, true, 27, 0, true, "A+"));
        assertFalse(Lab27CertificationGate.eligible(19, 19, true, false, 0, 0, false, "A+"));
        assertFalse(Lab27CertificationGate.eligible(19, 19, true, true, 0, 0, false, "A+"));
        assertFalse(Lab27CertificationGate.eligible(19, 19, true, true, 27, 1, false, "A+"));
        assertTrue(Lab27CertificationGate.eligible(19, 19, true, true, 27, 0, false, "A+"));
        assertEquals(12, Lab27CertificationGate.SCHEMA);
    }

    @Test public void physicalPortsRequireExactlyOneVisualAnswerPerPair() {
        assertTrue(Lab27CertificationGate.physicalChecksComplete(
                true, false, true, false, true, false));
        assertTrue(Lab27CertificationGate.physicalChecksComplete(
                false, true, false, true, false, true));
        assertFalse(Lab27CertificationGate.physicalChecksComplete(
                false, false, true, false, true, false)); // USB unanswered
        assertFalse(Lab27CertificationGate.physicalChecksComplete(
                true, true, true, false, true, false)); // USB contradictory
        assertFalse(Lab27CertificationGate.physicalChecksComplete(
                true, false, false, false, true, false)); // jack unanswered
        assertFalse(Lab27CertificationGate.physicalChecksComplete(
                true, false, true, true, true, false)); // jack contradictory
        assertFalse(Lab27CertificationGate.physicalChecksComplete(
                true, false, true, false, false, false)); // SIM unanswered
        assertFalse(Lab27CertificationGate.physicalChecksComplete(
                true, false, true, false, true, true)); // SIM contradictory
    }

    @Test public void completeVisualDeclarationDoesNotImplyOemAgreement() {
        assertTrue(Lab27CertificationGate.physicalChecksComplete(
                false, true, true, false, true, false));
        assertFalse(Lab27CertificationGate.physicalMatchesOem(
                Lab27CertificationGate.Presence.PRESENT,
                Lab27CertificationGate.Presence.PRESENT,
                Lab27CertificationGate.Presence.PRESENT,
                false, true, true, false, true, false));
        assertTrue(Lab27CertificationGate.physicalMatchesOem(
                Lab27CertificationGate.Presence.PRESENT,
                Lab27CertificationGate.Presence.ABSENT,
                Lab27CertificationGate.Presence.PRESENT,
                true, false, false, true, true, false));
    }

    @Test public void onlyExplicitOemPortsRequireVisualAnswers() {
        Lab27CertificationGate.Presence yes = Lab27CertificationGate.Presence.PRESENT;
        Lab27CertificationGate.Presence no = Lab27CertificationGate.Presence.ABSENT;
        Lab27CertificationGate.Presence unknown = Lab27CertificationGate.Presence.UNKNOWN;
        assertTrue(Lab27CertificationGate.shouldAsk(yes));
        assertTrue(Lab27CertificationGate.shouldAsk(no));
        assertFalse(Lab27CertificationGate.shouldAsk(unknown));
        // OEM specifies USB-C and a physical SIM tray but mentions no 3.5mm jack.
        // User must answer only USB and SIM. Headphone answer stays unanswered.
        assertTrue(Lab27CertificationGate.physicalChecksComplete(
                yes, unknown, yes, true, false, false, false, true, false));
        assertTrue(Lab27CertificationGate.physicalMatchesOem(
                yes, unknown, yes, true, false, false, false, true, false));
        assertFalse(Lab27CertificationGate.physicalChecksComplete(
                yes, unknown, yes, true, false, true, false, true, false));
        assertFalse(Lab27CertificationGate.physicalChecksComplete(
                yes, unknown, yes, false, false, false, false, true, false));
        // A sourced OEM "not present" is different from no OEM information.
        assertTrue(Lab27CertificationGate.physicalChecksComplete(
                yes, no, yes, true, false, false, true, true, false));
        assertTrue(Lab27CertificationGate.physicalMatchesOem(
                yes, no, yes, true, false, false, true, true, false));
        assertFalse(Lab27CertificationGate.physicalMatchesOem(
                yes, no, yes, true, false, true, false, true, false));
        // No documented port: cannot silently certify with no questions.
        assertFalse(Lab27CertificationGate.physicalChecksComplete(
                unknown, unknown, unknown, false, false, false, false, false, false));
        assertFalse(Lab27CertificationGate.physicalMatchesOem(
                unknown, unknown, unknown, false, false, false, false, false, false));
    }

    @Test public void fmAppEvidenceIsRequiredOnlyWhenManufacturerListsFm() {
        Lab27CertificationGate.Presence yes = Lab27CertificationGate.Presence.PRESENT;
        Lab27CertificationGate.Presence no = Lab27CertificationGate.Presence.ABSENT;
        Lab27CertificationGate.Presence unknown = Lab27CertificationGate.Presence.UNKNOWN;
        assertTrue(Lab27CertificationGate.fmAppQuestionComplete(yes, true, false));
        assertTrue(Lab27CertificationGate.fmAppQuestionComplete(yes, false, true));
        assertFalse(Lab27CertificationGate.fmAppQuestionComplete(yes, false, false));
        assertFalse(Lab27CertificationGate.fmAppQuestionComplete(yes, true, true));
        assertTrue(Lab27CertificationGate.fmAppQuestionComplete(no, false, true));
        assertTrue(Lab27CertificationGate.fmAppQuestionComplete(unknown, false, false));
        assertFalse(Lab27CertificationGate.fmAppQuestionComplete(unknown, true, false));
        // The presence/absence of an APP is not a proven FM tuner verdict.
    }

    @Test public void unsupportedHardwareIsMarkedNotApplicableRatherThanPassed() {
        Set<String> full = Lab27CertificationGate.applicable(true, true, true, true);
        assertEquals(18, full.size());
        Set<String> tablet = Lab27CertificationGate.applicable(false, false, true, true);
        assertFalse(tablet.contains("11"));
        assertFalse(tablet.contains("12"));
        assertFalse(tablet.contains("13"));
        assertEquals(16, tablet.size());
    }
}
