package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public final class Lab27FunctionGradePolicyTest {
    @Test public void confirmedCameraFailureCapsAPlusAtD() {
        assertEquals(49, Lab27FunctionGradePolicy.apply(100, true));
        assertEquals("D", Lab30ScoreEvidence.gradeForMeasuredSnapshot(
                Lab27FunctionGradePolicy.apply(98, true)));
        assertEquals(35, Lab27FunctionGradePolicy.apply(35, true));
    }

    @Test public void observationsAloneCannotCapTheFunctionalGrade() {
        // The caller must pass only confirmed functional failures. Informational
        // warnings in Labs 5, 8 and 14A are not fault evidence.
        assertEquals(100, Lab27FunctionGradePolicy.apply(100, false));
        assertEquals("A+", Lab30ScoreEvidence.gradeForMeasuredSnapshot(
                Lab27FunctionGradePolicy.apply(100, false)));
        assertEquals(92, Lab27FunctionGradePolicy.apply(92, false));
        assertEquals(84, Lab27FunctionGradePolicy.apply(84, false));
    }

    @Test public void missingMeasurementsNeverProduceAPlus() {
        assertEquals(-1, Lab27FunctionGradePolicy.apply(-1, true));
        assertEquals(-1, Lab27FunctionGradePolicy.apply(101, false));
    }
}
