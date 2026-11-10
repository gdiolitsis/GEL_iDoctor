package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab17EvidenceChecklistTest {
    @Test public void completeCoverageHasNoMissingTests() {
        Lab17EvidenceChecklist.Missing m =
                Lab17EvidenceChecklist.missing(true, true, true, true);
        assertEquals(0, m.count);
        assertEquals("", m.names);
    }

    @Test public void legacyOnlyLab16NeededIsRestored() {
        Lab17EvidenceChecklist.Missing m =
                Lab17EvidenceChecklist.missing(true, true, true, false);
        assertEquals(1, m.count);
        assertEquals("Lab 16", m.names);
    }

    @Test public void missingChargingAndBatteryStressAreBothVisible() {
        Lab17EvidenceChecklist.Missing m =
                Lab17EvidenceChecklist.missing(false, true, false, true);
        assertEquals(2, m.count);
        assertEquals("Lab 14A, Lab 15", m.names);
    }

    @Test public void noEvidenceNamesAllFourDomains() {
        Lab17EvidenceChecklist.Missing m =
                Lab17EvidenceChecklist.missing(false, false, false, false);
        assertEquals(4, m.count);
        assertEquals("Lab 14A, Lab 14B, Lab 15, Lab 16", m.names);
    }
}
