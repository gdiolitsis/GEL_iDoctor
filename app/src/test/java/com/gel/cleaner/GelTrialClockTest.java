package com.gel.cleaner;
import org.junit.Test;
import static org.junit.Assert.*;
public class GelTrialClockTest {
    @Test public void oneHourServerGrantOnlyCachesOneMinuteAtATime() {
        GelTrialClock lease = new GelTrialClock(1000,5000,3605000);
        assertTrue(lease.active(1000)); assertTrue(lease.active(60999));
        assertFalse(lease.active(61000)); assertFalse(lease.active(0));
    }
    @Test public void finalSecondsAndExpiredResponsesCannotExtendTrial() {
        GelTrialClock lease = new GelTrialClock(1000,5000,7000);
        assertTrue(lease.active(2999)); assertFalse(lease.active(3000));
        assertFalse(new GelTrialClock(1000,5000,5000).active(1000));
        assertFalse(new GelTrialClock(1000,0,7000).active(1000));
    }
}
