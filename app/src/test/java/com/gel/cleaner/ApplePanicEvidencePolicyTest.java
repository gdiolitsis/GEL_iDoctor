package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class ApplePanicEvidencePolicyTest {
    @Test public void acceptsKernelPanic() {
        assertTrue(ApplePanicEvidencePolicy.hasPanicEvidence("timestamp 123; panic(cpu 0 caller 0xfffffff) recovered after reboot"));
    }
    @Test public void acceptsJsonPanicString() {
        assertTrue(ApplePanicEvidencePolicy.hasPanicEvidence("{\\\"panicString\\\":\\\"panic(cpu 0 caller 0xffff) ...\\\"}"));
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
    @Test public void scopedResultLabels() {
        assertEquals("IMPORTED_PANIC_LOG_OBSERVED",ApplePanicEvidencePolicy.evidenceClass(true));
        assertEquals("UNVERIFIED_OR_UNSUPPORTED",ApplePanicEvidencePolicy.evidenceClass(false));
    }
}
