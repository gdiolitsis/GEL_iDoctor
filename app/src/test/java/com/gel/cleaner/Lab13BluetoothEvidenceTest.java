package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab13BluetoothEvidenceTest {
    @Test public void skipNeverBecomesRadioHealthPass() {
        assertEquals(Lab13BluetoothEvidence.Result.NOT_TESTED,
                Lab13BluetoothEvidence.classify(true, false, true, false,
                        0, 60000, 0, 0, 0));
    }
    @Test public void noAccessoryMeansNoFunctionalTest() {
        assertEquals(Lab13BluetoothEvidence.Result.NO_ACCESSORY_OBSERVED,
                Lab13BluetoothEvidence.classify(false, false, true, false,
                        45000, 60000, 0, 0, 0));
    }
    @Test public void waitForAccessoryCannotRunForeverWithoutAnOutcome() {
        assertEquals(Lab13BluetoothEvidence.Result.NO_ACCESSORY_OBSERVED,
                Lab13BluetoothEvidence.classify(false, false, true, false,
                        90000, 60000, 0, 0, 0));
    }
    @Test public void partialMonitorCannotDeclareStability() {
        assertEquals(Lab13BluetoothEvidence.Result.INCOMPLETE,
                Lab13BluetoothEvidence.classify(false, true, true, true,
                        15000, 60000, 0, 0, 0));
    }
    @Test public void adapterOffDuringWindowCannotProduceStableLink() {
        assertEquals(Lab13BluetoothEvidence.Result.ADAPTER_UNAVAILABLE,
                Lab13BluetoothEvidence.classify(false, true, false, true,
                        60000, 60000, 0, 0, 0));
    }
    @Test public void fullWindowOnlyMeansAggregateLinkWasObserved() {
        assertEquals(Lab13BluetoothEvidence.Result.AGGREGATE_LINK_OBSERVED,
                Lab13BluetoothEvidence.classify(false, true, true, true,
                        60000, 60000, 0, 0, 0));
    }
    @Test public void disconnectionIsAnObservedEventNotHardwareFailure() {
        assertEquals(Lab13BluetoothEvidence.Result.AGGREGATE_LINK_CHANGES_OBSERVED,
                Lab13BluetoothEvidence.classify(false, true, true, true,
                        60000, 60000, 2, 1, 0));
    }
    @Test public void ambiguousConnectedAndDisconnectedSameTickIsNotStable() {
        assertEquals(Lab13BluetoothEvidence.Result.AGGREGATE_LINK_CHANGES_OBSERVED,
                Lab13BluetoothEvidence.classify(false, true, true, true,
                        60000, 60000, 0, 0, 1));
    }
    @Test public void negativeCountersOrZeroWindowAreInvalid() {
        assertEquals(Lab13BluetoothEvidence.Result.INCOMPLETE,
                Lab13BluetoothEvidence.classify(false, true, true, true,
                        60000, 60000, -1, 0, 0));
        assertEquals(Lab13BluetoothEvidence.Result.INCOMPLETE,
                Lab13BluetoothEvidence.classify(false, true, true, true,
                        60000, 0, 0, 0, 0));
    }
    @Test public void secondsAreBasedOnElapsedTimeNotHandlerTickCount() {
        assertEquals(0,Lab13BluetoothEvidence.boundedSeconds(999,60));
        assertEquals(39,Lab13BluetoothEvidence.boundedSeconds(39999,60));
        assertEquals(60,Lab13BluetoothEvidence.boundedSeconds(65000,60));
    }
}
