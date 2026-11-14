package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab10WifiLogValuePolicyTest {
    @Test public void validSsid_isDisplayableGreen() {
        assertTrue(Lab10WifiLogValuePolicy.hasSsid("ForestRock"));
    }
    @Test public void unknownAndEmptySsid_remainNeutral() {
        assertFalse(Lab10WifiLogValuePolicy.hasSsid(null));
        assertFalse(Lab10WifiLogValuePolicy.hasSsid(""));
        assertFalse(Lab10WifiLogValuePolicy.hasSsid(" Unknown "));
        assertFalse(Lab10WifiLogValuePolicy.hasSsid("<unknown ssid>"));
    }
    @Test public void validBssid_isDisplayableGreen() {
        assertTrue(Lab10WifiLogValuePolicy.hasBssid("62:2c:c6:41:d3:02"));
    }
    @Test public void placeholderBssids_remainNeutral() {
        assertFalse(Lab10WifiLogValuePolicy.hasBssid("02:00:00:00:00:00"));
        assertFalse(Lab10WifiLogValuePolicy.hasBssid("00:00:00:00:00:00"));
        assertFalse(Lab10WifiLogValuePolicy.hasBssid("ff:ff:ff:ff:ff:ff"));
        assertFalse(Lab10WifiLogValuePolicy.hasBssid(null));
        assertFalse(Lab10WifiLogValuePolicy.hasBssid("unknown"));
    }
    @Test public void configuredDhcpAddress_isDisplayableGreen() {
        // Android DhcpInfo stores each IPv4 address in little-endian order.
        assertTrue(Lab10WifiLogValuePolicy.hasDhcpAddress(0xCB01A8C0)); // 192.168.1.203
        assertTrue(Lab10WifiLogValuePolicy.hasDhcpAddress(0x0101A8C0)); // 192.168.1.1
    }
    @Test public void zeroAndBroadcastDhcpAddresses_remainNeutral() {
        assertFalse(Lab10WifiLogValuePolicy.hasDhcpAddress(0));  // 0.0.0.0
        assertFalse(Lab10WifiLogValuePolicy.hasDhcpAddress(-1)); // 255.255.255.255
    }
}
