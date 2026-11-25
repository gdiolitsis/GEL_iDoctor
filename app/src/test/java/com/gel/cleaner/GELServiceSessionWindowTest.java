package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class GELServiceSessionWindowTest {
    @Test public void serverGrantIsCappedAtEightHours() {
        long now = 1000000000000L;
        assertTrue(GELServiceSessionWindow.validGrant(now, now + 28800000L));
        assertFalse(GELServiceSessionWindow.validGrant(now, now + 28800001L));
        assertFalse(GELServiceSessionWindow.validGrant(now, now));
        assertFalse(GELServiceSessionWindow.validGrant(0, 5000L));
    }

    @Test public void endIsExclusiveAndRebootInvalidatesMonotonicLease() {
        long begin = 1000L, end = begin + GELServiceSessionWindow.MAX_MS;
        long wallEnd = 1000000000000L + GELServiceSessionWindow.MAX_MS;
        assertEquals(GELServiceSessionWindow.MAX_MS,
                GELServiceSessionWindow.remaining(begin, end, begin, wallEnd, 1000000000000L));
        assertEquals(1L,
                GELServiceSessionWindow.remaining(begin, end, end - 1, wallEnd, wallEnd - 1));
        assertEquals(0L,
                GELServiceSessionWindow.remaining(begin, end, end, wallEnd, wallEnd));
        assertEquals(0L,
                GELServiceSessionWindow.remaining(begin, end, begin - 1, wallEnd, wallEnd - 10000L));
        assertEquals(0L,
                GELServiceSessionWindow.remaining(begin, end, begin + 1, wallEnd, wallEnd));
    }
}
