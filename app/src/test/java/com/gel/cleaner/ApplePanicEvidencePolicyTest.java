package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class ApplePanicEvidencePolicyTest {
    @Test public void acceptsKernelPanic() {
        assertTrue(ApplePanicEvidencePolicy.hasPanicEvidence("timestamp 123; panic(cpu 0 caller 0xfffffff) recovered after reboot"));
    }
    @Test public void acceptsJsonPanicString() {
        assertTrue(ApplePanicEvidencePolicy.hasPanicEvidence("metadata panicString: panic(cpu 0 caller 0xffff) log information"));
    }
    @Test public void acceptsBugType210Unquoted() {
        assertTrue(ApplePanicEvidencePolicy.hasPanicEvidence("metadata: bug_type: 210 and log timestamp 2025-01-01 some content"));
    }
    @Test public void rejectsOtherBugTypes() {
        assertFalse(ApplePanicEvidencePolicy.hasPanicEvidence("metadata: bug_type: 109 and incident 123456789 so much content."));
    }
    @Test public void rejectsThermalOnlyText() {
        assertFalse(ApplePanicEvidencePolicy.hasPanicEvidence("thermal thermal thermal sensor is not panic and not an iOS panic file"));
    }
    @Test public void rejectsJetsamOnlyText() {
        assertFalse(ApplePanicEvidencePolicy.hasPanicEvidence("jetsam memorystatus highwater no panic payload exists here"));
    }
    @Test public void rejectsEmptyOrTiny() {
        assertFalse(ApplePanicEvidencePolicy.hasPanicEvidence(null));
        assertFalse(ApplePanicEvidencePolicy.hasPanicEvidence(""));
        assertFalse(ApplePanicEvidencePolicy.hasPanicEvidence("panicString"));
    }
    @Test public void rejectsFalse210Prefix() {
        assertFalse(ApplePanicEvidencePolicy.hasPanicEvidence("some metadata bug_type: 2109 extra content without actual panic"));
    }
    @Test public void noFabricatedHighConfidence() {
        assertEquals("UNVERIFIED", ApplePanicEvidencePolicy.patternConfidence());
    }
    @Test public void causeIsExplicitlyHypothetical() {
        assertTrue(ApplePanicEvidencePolicy.possibleCause("Memory fault").startsWith("Pattern hypothesis"));
    }
    @Test public void emptyCauseIsUnknown() {
        assertTrue(ApplePanicEvidencePolicy.possibleCause(null).contains("Unknown"));
    }
    @Test public void greekAdviceNeverDemandsRepair() {
        String advice=ApplePanicEvidencePolicy.recommendation(true);
        assertTrue(advice.contains("τεχνικό"));
        assertTrue(advice.contains("δεν τεκμηριώνει"));
    }
    @Test public void englishAdviceNeverDemandsRepair() {
        String advice=ApplePanicEvidencePolicy.recommendation(false);
        assertTrue(advice.contains("does not justify"));
    }
    @Test public void limitationExplicitlyMentionsNoDeviceValidation() {
        assertTrue(ApplePanicEvidencePolicy.limitation(false).contains("not been independently verified"));
    }
    @Test public void signatureCannotBorrowKeywordsFromDifferentImportedPanicFiles() {
        String multi = "\n===== ZIP FILE: panic-a.ips =====\n"
                + "panic(cpu 0): baseband crash without the other required token\n"
                + "===== ZIP FILE: panic-b.ips =====\n"
                + "panic(cpu 1): modem interface error; companion phrase absent\n";
        assertFalse(ApplePanicEvidencePolicy.tokensMatchOnePanic(
                multi, java.util.Arrays.asList("baseband", "modem")));
    }
    @Test public void signatureCanMatchWithinSameVerifiedPanicEntry() {
        String multi = "\n===== ZIP FILE: panic-a.ips =====\n"
                + "panic(cpu 0): baseband modem controller timeout reported on restart\n"
                + "===== ZIP FILE: unrelated.txt =====\n"
                + "This is generic thermal logging with no panic structure.\n";
        assertTrue(ApplePanicEvidencePolicy.tokensMatchOnePanic(
                multi, java.util.Arrays.asList("baseband", "modem")));
    }
    @Test public void standalonePanicStillMatchesAndUnsupportedTextNeverDoes() {
        java.util.List<String> tokens = java.util.Arrays.asList("watchdog", "timeout");
        assertTrue(ApplePanicEvidencePolicy.tokensMatchOnePanic(
                "panic(cpu 0): watchdog timeout observed during restart; trace id 12345", tokens));
        assertFalse(ApplePanicEvidencePolicy.tokensMatchOnePanic(
                "not a panic report: watchdog timeout is ordinary app telemetry", tokens));
        assertFalse(ApplePanicEvidencePolicy.tokensMatchOnePanic(
                "panic(cpu 0): watchdog timeout observed during restart; trace id 12345",
                java.util.Collections.emptyList()));
    }
    @Test public void demoBasebandVersionIsMetadataNotFailure() {
        String example = "panic(cpu 2): Kernel panic: watchdog timeout\n"
                + "Boot args: -v\nBaseband version: 3.20.01\n";
        assertFalse(ApplePanicEvidencePolicy.hasExplicitBasebandFaultHint(example));
    }
    @Test public void explicitBasebandPanicIsStillRecognizedAsHint() {
        assertTrue(ApplePanicEvidencePolicy.hasExplicitBasebandFaultHint(
                "panic(cpu 0): baseband watchdog failure during actual modem operation"));
    }
    @Test public void independentDemoEventsCannotFormOneCombinedCause() {
        String multi = "===== ZIP FILE: PanicLog1 =====\n"
                + "panic(cpu 2): Kernel panic: watchdog timeout\n"
                + "Baseband version: 3.20.01\n"
                + "===== ZIP FILE: PanicLog2 =====\n"
                + "panicString: panic(cpu 0): thermal shutdown\n";
        assertFalse(ApplePanicEvidencePolicy.hasExplicitBasebandFaultHint(multi));
        assertFalse(ApplePanicEvidencePolicy.tokensMatchOnePanic(
                multi, java.util.Arrays.asList("watchdog", "thermal")));
        assertTrue(ApplePanicEvidencePolicy.tokensMatchOnePanic(
                multi, java.util.Arrays.asList("panic(cpu", "watchdog timeout")));
        assertTrue(ApplePanicEvidencePolicy.tokensMatchOnePanic(
                multi, java.util.Arrays.asList("panicString", "thermal shutdown")));
    }
    @Test public void scopedResultLabels() {
        assertEquals("IMPORTED_PANIC_LOG_OBSERVED",ApplePanicEvidencePolicy.evidenceClass(true));
        assertEquals("UNVERIFIED_OR_UNSUPPORTED",ApplePanicEvidencePolicy.evidenceClass(false));
    }
}
