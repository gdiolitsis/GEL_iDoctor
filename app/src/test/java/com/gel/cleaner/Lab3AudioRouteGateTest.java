package com.gel.cleaner;
import org.junit.Test;
import static org.junit.Assert.*;
public class Lab3AudioRouteGateTest {
    @Test public void normalInternalAudioMayStartGuidedTest() {
        assertTrue(Lab3AudioRouteGate.canStart(true, true, false));
    }
    @Test public void activeCallOrOtherCommunicationModeIsUnavailable() {
        assertFalse(Lab3AudioRouteGate.canStart(false, true, false));
    }
    @Test public void bluetoothUsbAndWiredCandidatesCannotBeIgnored() {
        assertFalse(Lab3AudioRouteGate.canStart(true, true, true));
    }
    @Test public void unknownOutputInventoryNeverForcesRoute() {
        assertFalse(Lab3AudioRouteGate.canStart(true, false, false));
        assertFalse(Lab3AudioRouteGate.canStart(false, false, true));
    }
}
