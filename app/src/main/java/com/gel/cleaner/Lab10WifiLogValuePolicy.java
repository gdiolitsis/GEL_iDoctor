package com.gel.cleaner;

/**
 * Lab 10 presentation policy. Green means Android returned a usable value.
 * It never certifies Internet throughput, network security or router health.
 */
final class Lab10WifiLogValuePolicy {
    private Lab10WifiLogValuePolicy() {}

    static boolean hasSsid(String ssid) {
        if (ssid == null) return false;
        String value = ssid.trim();
        return !value.isEmpty()
                && !value.equalsIgnoreCase("Unknown")
                && !value.equalsIgnoreCase("unknown ssid")
                && !value.equalsIgnoreCase("<unknown ssid>");
    }

    static boolean hasBssid(String bssid) {
        if (bssid == null) return false;
        String value = bssid.trim();
        return value.matches("(?i)[0-9a-f]{2}(:[0-9a-f]{2}){5}")
                && !value.equalsIgnoreCase("02:00:00:00:00:00")
                && !value.equalsIgnoreCase("00:00:00:00:00:00")
                && !value.equalsIgnoreCase("ff:ff:ff:ff:ff:ff");
    }

    /** Unset/placeholder DHCP addresses must not look like confirmed values. */
    static boolean hasDhcpAddress(int value) {
        return value != 0 && value != -1;
    }
}
