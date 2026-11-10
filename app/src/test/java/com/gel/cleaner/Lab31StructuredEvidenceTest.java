package com.gel.cleaner;
import org.junit.Test;
import static org.junit.Assert.*;

public class Lab31StructuredEvidenceTest {
    @Test public void noWarningsProseIsNeverAHealthyResult() {
        Lab31StructuredEvidence.Result r =
                Lab31StructuredEvidence.summarize("No warnings or errors detected");
        assertEquals(0, r.records);
        assertEquals(0, r.observed);
    }

    @Test public void recordStatesAreCountedWithoutFabricatedPass() {
        String log = "[10:12] SUMMARY: PART_AUTHENTICITY=UNKNOWN; REPAIR_HISTORY=NOT_AVAILABLE\n"
                + "SUMMARY: APPS_IMPACT=INCONCLUSIVE\n"
                + "SUMMARY: LAB28_SEQUENTIAL_OS_READINGS=OBSERVED; SOURCE=BATTERY_PUBLIC_API\n";
        Lab31StructuredEvidence.Result r = Lab31StructuredEvidence.summarize(log);
        assertEquals(4, r.records);
        assertEquals(1, r.observed);
        assertEquals(3, r.missing);
    }

    @Test public void unknownWordsDoNotBecomeCertification() {
        Lab31StructuredEvidence.Result r =
                Lab31StructuredEvidence.summarize("SUMMARY: ROOT_STATE=UNKNOWN; "
                        + "VERIFIED_BOOT=OS_REPORTED_ONLY; ROOT_PATHS_OBSERVED=0");
        assertEquals(1, r.records);
        assertEquals(0, r.observed);
        assertEquals(1, r.missing);
    }

    @Test public void emptyOrMalformedLogsAreNotGoodHealth() {
        assertEquals(0, Lab31StructuredEvidence.summarize(null).records);
        assertEquals(0, Lab31StructuredEvidence.summarize("").records);
        assertEquals(0,
                Lab31StructuredEvidence.summarize("SUMMARY: abc=OBSERVED; X=UNKNOWN").records);
    }

    @Test public void unrecognizedValuesCannotBeTreatedAsPositiveSignals() {
        Lab31StructuredEvidence.Result r = Lab31StructuredEvidence.summarize(
                "SUMMARY: ROOT_STATE=NOT_A_CERTIFICATE; "
                        + "LAB28_SEQUENTIAL_OS_READINGS=UNVERIFIED");
        assertEquals(1, r.records);
        assertEquals(1, r.missing);
    }
}
