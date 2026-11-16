package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/** Imported-log identity, not verified live device identity. */
public final class ApplePanicModelEvidenceTest {
    private static String first() {
        return "===== ZIP FILE: PanicLog1 =====\n"
                + "panic(cpu 2): Kernel panic: watchdog timeout\n"
                + "Hardware model: iPhone14,3\n";
    }

    private static String second() {
        return "===== ZIP FILE: PanicLog2 =====\n"
                + "{\"deviceModel\":\"iPhone14,3\", \"panicString\":\"panic(cpu 0): thermal shutdown\"}\n";
    }

    @Test public void twoDemoLogsIdentifyTheSameIPhone13ProMaxWithoutGuessing() {
        ApplePanicModelEvidence.Result result =
                ApplePanicModelEvidence.analyze(first() + second());
        assertEquals(2, result.eligibleRecords);
        assertEquals(2, result.recordsWithModel);
        assertTrue(result.hasConsistentModel());
        assertEquals(Integer.valueOf(2), result.identifiers.get("iPhone14,3"));
        assertEquals("iPhone 13 Pro Max (iPhone14,3)",
                ApplePanicModelEvidence.displayName("iPhone14,3"));
    }

    @Test public void differentPhoneIdentifiersMustNeverBecomeOneCommonModel() {
        String mixed = first()
                + "===== ZIP FILE: another =====\n"
                + "{\"deviceModel\":\"iPhone14,2\",\"panicString\":\"panic(cpu 0): thermal shutdown\"}";
        ApplePanicModelEvidence.Result result = ApplePanicModelEvidence.analyze(mixed);
        assertFalse(result.hasConsistentModel());
        assertEquals(2, result.identifiers.size());
        assertTrue(result.identifiers.containsKey("iPhone14,3"));
        assertTrue(result.identifiers.containsKey("iPhone14,2"));
    }

    @Test public void missingDeviceFieldCannotAuthenticateEntireBundle() {
        String noModel = "===== ZIP FILE: second =====\n"
                + "{\"panicString\":\"panic(cpu 0): thermal shutdown\"}\n";
        ApplePanicModelEvidence.Result result =
                ApplePanicModelEvidence.analyze(first() + noModel);
        assertEquals(2, result.eligibleRecords);
        assertEquals(1, result.recordsWithModel);
        assertFalse(result.hasConsistentModel());
    }

    @Test public void conflictingFieldsInOnePanicMustNotCountAsAConsistentIdentity() {
        String conflicting = "===== ZIP FILE: one =====\n"
                + "panic(cpu 0): watchdog timeout\n"
                + "Hardware model: iPhone14,3\n"
                + "{\"deviceModel\":\"iPhone14,2\"}\n";
        ApplePanicModelEvidence.Result result = ApplePanicModelEvidence.analyze(conflicting);
        assertTrue(result.conflictingFields);
        assertFalse(result.hasConsistentModel());
        assertEquals(2, result.identifiers.size());
    }

    @Test public void incidentalModelMentionOrUnsupportedBlockIsNotTrustedModelMetadata() {
        String one = "===== ZIP FILE: legitimate =====\n"
                + "panic(cpu 0): watchdog timeout and historical iPhone14,3 notes\n";
        String two = "===== ZIP FILE: README =====\n"
                + "Hardware model: iPhone14,3\n";
        ApplePanicModelEvidence.Result result = ApplePanicModelEvidence.analyze(one + two);
        assertEquals(1, result.eligibleRecords);
        assertTrue(result.identifiers.isEmpty());
    }

    @Test public void unknownIdentifierDoesNotBecomeInventedMarketingModel() {
        String unknown = "===== ZIP FILE: unknown =====\n"
                + "panic(cpu 0): watchdog timeout\n"
                + "Hardware model: iPhone99,9\n";
        ApplePanicModelEvidence.Result result = ApplePanicModelEvidence.analyze(unknown);
        assertTrue(result.hasConsistentModel()); // consistent asserted *code*, not catalogue match
        assertEquals("iPhone99,9", ApplePanicModelEvidence.displayName("iPhone99,9"));
    }
}
