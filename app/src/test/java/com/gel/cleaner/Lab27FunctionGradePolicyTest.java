package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/** Only documented functional failure evidence changes the certificate. */
public final class Lab27FunctionGradePolicyTest {
    @Test public void confirmedCameraFailureReceivesD() {
        assertEquals(49, Lab27FunctionGradePolicy.apply(true));
        assertEquals("D", Lab30ScoreEvidence.gradeForMeasuredSnapshot(
                Lab27FunctionGradePolicy.apply(true)));
    }

    @Test public void noConfirmedFunctionalFailureReceivesAPlus() {
        assertEquals(100, Lab27FunctionGradePolicy.apply(false));
        assertEquals("A+", Lab30ScoreEvidence.gradeForMeasuredSnapshot(
                Lab27FunctionGradePolicy.apply(false)));
    }

    @Test public void scorerCannotAcceptTemporaryBatteryRamStorageOrUptimeInputs() {
        // The policy deliberately has only one boolean parameter.
        assertEquals(100, Lab27FunctionGradePolicy.apply(false));
        assertEquals(49, Lab27FunctionGradePolicy.apply(true));
    }
}
