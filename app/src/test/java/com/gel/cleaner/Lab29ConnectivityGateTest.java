package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab29ConnectivityGateTest {
    @Test public void validatedInternetAllowsLabToStartWithoutWarning() {
        assertTrue(Lab29ConnectivityGate.isUsableNetwork(true, true, true));
    }

    @Test public void noActiveNetworkRequiresAdvisory() {
        assertFalse(Lab29ConnectivityGate.isUsableNetwork(false, true, true));
    }

    @Test public void captivePortalOrUnvalidatedInternetRequiresAdvisory() {
        assertFalse(Lab29ConnectivityGate.isUsableNetwork(true, true, false));
    }

    @Test public void localOnlyNetworkRequiresAdvisory() {
        assertFalse(Lab29ConnectivityGate.isUsableNetwork(true, false, true));
    }
}
