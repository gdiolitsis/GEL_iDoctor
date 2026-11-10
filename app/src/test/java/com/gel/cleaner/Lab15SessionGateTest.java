package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab15SessionGateTest {
    @Test public void acceptsOnlyCurrentRunningUnfinishedSession() {
        assertTrue(Lab15SessionGate.mayContinue(3, 3, true, false, false));
        assertFalse(Lab15SessionGate.mayContinue(2, 3, true, false, false));
        assertFalse(Lab15SessionGate.mayContinue(3, 4, true, false, false));
    }

    @Test public void cancellationFailureCompletionAndBackgroundCannotPass() {
        assertFalse(Lab15SessionGate.mayContinue(3, 3, false, false, false));
        assertFalse(Lab15SessionGate.mayContinue(3, 3, true, true, false));
        assertFalse(Lab15SessionGate.mayContinue(3, 3, true, false, true));
    }
}
