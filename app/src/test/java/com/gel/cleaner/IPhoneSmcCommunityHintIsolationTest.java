package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/** Community sensor-code associations are clues, never official Apple verdicts. */
public final class IPhoneSmcCommunityHintIsolationTest {
    private static ApplePanicHardwareHints.Finding smc(String model, String code) {
        String text = "{\"bug_type\":\"210\",\"model\":\"" + model
                + "\",\"panicString\":\"panic(cpu 0 caller 0x123): SMC PANIC - ASSERT;"
                + " S.sensor array 0 - 4 is 0x0, " + code + ", 0x0, 0x0, 0x0\"}";
        ApplePanicHardwareHints.Assessment result = ApplePanicHardwareHints.examineOne(text);
        assertEquals(model.toLowerCase(java.util.Locale.ROOT), result.modelIdentifier);
        assertEquals(1, result.findings.size());
        return result.findings.get(0);
    }

    @Test public void vanillaIphone15UsesOnlyItsOwnPublishedSmcCodeAssociations() {
        assertTrue(smc("iPhone15,4", "0xA1").area(false).contains("battery"));
        assertTrue(smc("iPhone15,4", "0x80000").area(false).contains("charge port"));
        assertTrue(smc("iPhone15,4", "0x100000").area(false).contains("front sensor"));
        assertTrue(smc("iPhone15,4", "0x200000").area(false).contains("wireless"));
    }

    @Test public void unknownOrDifferentModelCannotBorrowIphone15PartMapping() {
        assertTrue(smc("iPhone15,2", "0x80000").area(false).contains("front sensor"));
        assertFalse(smc("iPhone15,2", "0x80000").area(false).contains("charge port assembly"));
        assertTrue(smc("iPhone99,9", "0x80000").area(false)
                .contains("no sufficiently reliable model-specific part mapping"));
    }

    @Test public void noMappingMayDeclareRepairConfirmed() {
        ApplePanicHardwareHints.Finding entry = smc("iPhone15,4", "0xA1");
        assertTrue(entry.area(false).contains("Not a confirmed fault"));
        assertFalse(entry.action(false).toLowerCase(java.util.Locale.ROOT)
                .contains("replace the battery"));
    }
}
