package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Regression for the iPhone 14 Pro iFixit repair case, May 2026:
 * https://www.ifixit.com/Answers/View/953710/
 * Only the short published panicString lines below are reproduced.
 * This is NOT a full IPS file or a newly invented customer log.
 */
public final class IPhoneSmcSensorArrayRegressionTest {
    private static String publicExcerpt() {
        return "\"product\" : \"iPhone15,2\",\n"
                + "\"panicString\" : \"panic(cpu 3 caller 0xfffffff031e92c88): "
                + "SMC PANIC - ASSERT: target\\/d73\\/target.cpp:320: 0, "
                + "SMC BSC failure, TAOP ----\\n"
                + "spreadsheet ver(*10) 150\\n"
                + "S.sensor array 0 - 5 is 0, 262144, 0, 0, 0\\n"
                + "F.sensor array 0 - 1 is 0\\n";
    }

    @Test public void actualPublishedZeroToFiveFormatDecodesChargingPortClue() {
        ApplePanicHardwareHints.Assessment result =
                ApplePanicHardwareHints.examineOne(publicExcerpt());
        assertEquals("iphone15,2", result.modelIdentifier);
        assertEquals(1, result.findings.size());
        ApplePanicHardwareHints.Finding finding = result.findings.get(0);
        assertEquals("SMC 0x40000", finding.code);
        assertTrue(finding.modelScoped);
        assertTrue(finding.area(true).contains("θύρα φόρτισης"));
        assertTrue(finding.area(true).contains("Όχι επιβεβαιωμένη βλάβη"));
        assertTrue(finding.area(false).contains("charging port"));
        assertTrue(finding.action(true).contains("γνωστό λειτουργικό"));
        assertNotEquals("SMC_SENSOR_ARRAY_UNPARSED", finding.code);
    }

    @Test public void existingZeroToFourLayoutStillParses() {
        // Controlled test fragment: replace only the array-width field.
        String changedRange = publicExcerpt()
                .replace("S.sensor array 0 - 5 is", "S.sensor array 0 - 4 is");
        ApplePanicHardwareHints.Assessment result =
                ApplePanicHardwareHints.examineOne(changedRange);
        assertEquals("SMC 0x40000", result.findings.get(0).code);
    }

    @Test public void widerZeroToSevenLayoutDoesNotDiscardObservedCode() {
        // Controlled internal test variant; not represented as real-source evidence.
        String widerRange = publicExcerpt()
                .replace("S.sensor array 0 - 5 is", "S.sensor array 0 - 7 is");
        ApplePanicHardwareHints.Assessment result =
                ApplePanicHardwareHints.examineOne(widerRange);
        assertEquals("SMC 0x40000", result.findings.get(0).code);
    }

    @Test public void identicalCodeOnDifferentModelMustNotBeCalledChargingPort() {
        String differentModel = publicExcerpt().replace("iPhone15,2", "iPhone12,1");
        ApplePanicHardwareHints.Assessment result =
                ApplePanicHardwareHints.examineOne(differentModel);
        assertEquals("SMC 0x40000", result.findings.get(0).code);
        assertFalse(result.findings.get(0).modelScoped);
        assertFalse(result.findings.get(0).area(true).contains("θύρα φόρτισης"));
    }

    @Test public void unparsedOrAllZeroSensorArrayDoesNotCreatePartAttribution() {
        String allZero = publicExcerpt().replace("0, 262144, 0, 0, 0", "0, 0, 0, 0, 0");
        ApplePanicHardwareHints.Assessment result =
                ApplePanicHardwareHints.examineOne(allZero);
        assertEquals("SMC_SENSOR_ARRAY_UNPARSED", result.findings.get(0).code);
        assertFalse(result.findings.get(0).modelScoped);
    }
}
