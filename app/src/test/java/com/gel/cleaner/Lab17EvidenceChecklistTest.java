package com.gel.cleaner;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class Lab17EvidenceChecklistTest {
    @Test public void completeCoreEvidenceDoesNotRequestRedundantTests() {
        assertEquals("", Lab17EvidenceChecklist.missingCoreLabs(true, true, true));
    }

    @Test public void onlyMissingThermalIsRequested() {
        assertEquals("16", Lab17EvidenceChecklist.missingCoreLabs(true, true, false));
    }

    @Test public void onlyMissingChargeIsRequested() {
        assertEquals("15", Lab17EvidenceChecklist.missingCoreLabs(true, false, true));
    }

    @Test public void onlyMissingBatteryStressIsRequested() {
        assertEquals("14A", Lab17EvidenceChecklist.missingCoreLabs(false, true, true));
    }

    @Test public void allMissingDoNotBecomeAValidScore() {
        assertEquals("14A, 15, 16", Lab17EvidenceChecklist.missingCoreLabs(false, false, false));
    }

    @Test public void twoMissingProduceExactTargetedGuidance() {
        assertEquals("14A, 16", Lab17EvidenceChecklist.missingCoreLabs(false, true, false));
        assertEquals("15, 16", Lab17EvidenceChecklist.missingCoreLabs(true, false, false));
        assertEquals("14A, 15", Lab17EvidenceChecklist.missingCoreLabs(false, false, true));
    }
}
