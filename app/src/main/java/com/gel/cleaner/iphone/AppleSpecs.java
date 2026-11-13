// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleSpecs.java — MASTER RESOLVER (FINAL / LOCKED)
// Series-First • Reflection-Safe • Future-Proof
// ============================================================

package com.gel.cleaner.iphone;

/**
 * AppleSpecs
 * ------------------------------------------------------------
 * 🔒 SINGLE ENTRY POINT for Apple device resolution
 *
 * Philosophy:
 * • UI selects a STRING (device declaration)
 * • This class resolves it to ONE AppleDeviceSpec
 * • Series-first logic (iPhone 15 Series, iPad Pro Series, etc.)
 * • Never crashes — always returns a valid spec
 *
 * This file should NOT be modified again.
 * New devices go ONLY into:
 *  - iPhoneSpecs.java
 *  - iPadSpecs.java
 */
public final class AppleSpecs {

    // =========================================================
    // PUBLIC RESOLVER
    // =========================================================
    public static AppleDeviceSpec get(String model) {

        if (model == null || model.trim().isEmpty())
            return AppleDeviceSpec.unknown();

        // Normalize input
        String m = normalize(model);

        // -----------------------------------------------------
        // iPHONE FAMILY
        // -----------------------------------------------------
        if (m.startsWith("iphone")) {
            AppleDeviceSpec d = iPhoneSpecs.get(m);
            return d != null ? d : AppleDeviceSpec.unknown();
        }

        // -----------------------------------------------------
        // iPAD FAMILY
        // -----------------------------------------------------
        if (m.startsWith("ipad")) {
            AppleDeviceSpec d = iPadSpecs.get(m);
            return d != null ? d : AppleDeviceSpec.unknown();
        }

        // -----------------------------------------------------
        // UNKNOWN / FUTURE DEVICE
        // -----------------------------------------------------
        return AppleDeviceSpec.unknown();
    }

    // =========================================================
    // NORMALIZATION (CRITICAL)
    // =========================================================
    /**
     * Makes device declaration resilient to:
     * • Case differences
     * • Extra spaces
     * • "Series", "Pro", "Max" wording
     *
     * Example:
     *  "iPhone 15 Pro Max" → "iphone 15 pro max"
     */
    private static String normalize(String s) {
        return s
                .trim()
                .toLowerCase(java.util.Locale.ROOT)
                .replace("series", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    // =========================================================
    // HARD LOCK
    // =========================================================
    private AppleSpecs() {
        // no instances
    }
}
