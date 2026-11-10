package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class WifiLinkEvidenceTest {
    @Test public void oneMegabitPhyStaysOneNotFive() {
        WifiLinkEvidence a = WifiLinkEvidence.fromAndroid(1, -68);
        assertTrue(a.hasPhyRate());
        assertEquals(1, a.phyRateMbps);
        assertTrue(a.hasRssi());
        assertFalse(a.hasMeasuredInternetThroughput());
    }

    @Test public void twoMegabitPhyStaysTwoNotFive() {
        WifiLinkEvidence a = WifiLinkEvidence.fromAndroid(2, -80);
        assertEquals(2, a.phyRateMbps);
        assertFalse(a.hasMeasuredInternetThroughput());
    }

    @Test public void unavailablePhyNeverFallsBackToSeventyTwo() {
        WifiLinkEvidence zero = WifiLinkEvidence.fromAndroid(0, 127);
        WifiLinkEvidence missing = WifiLinkEvidence.fromAndroid(-1, -127);
        assertFalse(zero.hasPhyRate());
        assertFalse(missing.hasPhyRate());
        assertEquals(-1, zero.phyRateMbps);
        assertEquals(-1, missing.phyRateMbps);
        assertFalse(zero.hasRssi());
        assertFalse(missing.hasRssi());
    }

    @Test public void evenStrongLinkDoesNotEstablishInternetSpeed() {
        WifiLinkEvidence a = WifiLinkEvidence.fromAndroid(1200, -45);
        assertTrue(a.hasPhyRate());
        assertEquals(1200, a.phyRateMbps);
        assertFalse(a.hasMeasuredInternetThroughput());
    }

    @Test public void invalidRssiIsUnavailableRatherThanExcellentOrBad() {
        assertFalse(WifiLinkEvidence.fromAndroid(54, 127).hasRssi());
        assertFalse(WifiLinkEvidence.fromAndroid(54, 0).hasRssi());
        assertFalse(WifiLinkEvidence.fromAndroid(54, -150).hasRssi());
    }
}
