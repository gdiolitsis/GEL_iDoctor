package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public final class Lab27FunctionGradePolicyTest {
    @Test public void cameraFailureCapsAPlusAtD() {
        assertEquals(49, Lab27FunctionGradePolicy.apply(100, true, 1));
        assertEquals("D", Lab30ScoreEvidence.gradeForMeasuredSnapshot(
                Lab27FunctionGradePolicy.apply(98, true, 1)));
        assertEquals(35, Lab27FunctionGradePolicy.apply(35, true, 1));
    }
    @Test public void otherAdverseFindingsPreventUnqualifiedAPlus() {
        assertEquals(84, Lab27FunctionGradePolicy.apply(100, false, 1));
        assertEquals("B", Lab30ScoreEvidence.gradeForMeasuredSnapshot(
                Lab27FunctionGradePolicy.apply(100, false, 1)));
        assertEquals(100, Lab27FunctionGradePolicy.apply(100, false, 0));
    }
    @Test public void incompleteMeasurementsRemainUnscored() {
        assertEquals(-1, Lab27FunctionGradePolicy.apply(-1, true, 1));
        assertEquals(-1, Lab27FunctionGradePolicy.apply(101, true, 1));
    }
}
