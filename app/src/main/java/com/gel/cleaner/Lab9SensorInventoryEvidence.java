package com.gel.cleaner;

import java.util.Locale;

/**
 * Interprets Android's sensor INVENTORY labels, not sensor functionality.
 * Manufacturers can report the large-area feature in getVendor() while
 * getName() says only "Touch Sensor" (e.g. Xiaomi Large Area Detect).
 */
public final class Lab9SensorInventoryEvidence {
    private Lab9SensorInventoryEvidence() {}

    public static boolean isLargeAreaTouch(String name, String vendor) {
        String n = name == null ? "" : name.toLowerCase(Locale.US);
        String v = vendor == null ? "" : vendor.toLowerCase(Locale.US);
        boolean touchMarker = n.contains("touch") || v.contains("touch");
        boolean largeAreaMarker =
                (n.contains("large") && (n.contains("area") || n.contains("touch")))
                || (v.contains("large") && (v.contains("area") || v.contains("touch")));
        return touchMarker && largeAreaMarker;
    }
}
