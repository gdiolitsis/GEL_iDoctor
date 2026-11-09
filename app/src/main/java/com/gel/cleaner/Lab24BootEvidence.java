package com.gel.cleaner;

import java.util.Locale;

/**
 * OS-reported Verified Boot properties, NOT a cryptographically attested
 * verdict. Properties may be hidden, missing, altered or OEM-specific.
 */
final class Lab24BootEvidence {
    enum Result {
        UNKNOWN,
        OS_REPORTS_LOCKED_VERIFIED,
        OS_REPORTS_NONSTANDARD_TRUST,
        OS_REPORTS_UNLOCKED_OR_UNVERIFIED,
        OS_PROPERTIES_INCONSISTENT
    }

    private Lab24BootEvidence() {}

    static Result classify(String boot, String vbmetaDevice, String flashLocked) {
        String b = normalize(boot), d = normalize(vbmetaDevice), f = normalize(flashLocked);

        boolean green = "green".equals(b);
        boolean yellow = "yellow".equals(b);
        boolean orange = "orange".equals(b);
        boolean red = "red".equals(b);
        boolean locked = "locked".equals(d) || "1".equals(f);
        boolean unlocked = "unlocked".equals(d) || "0".equals(f);

        // A conflict among the two lock indicators needs review, not a pass.
        if (("locked".equals(d) && "0".equals(f))
                || ("unlocked".equals(d) && "1".equals(f))) {
            return Result.OS_PROPERTIES_INCONSISTENT;
        }
        if (green && unlocked) return Result.OS_PROPERTIES_INCONSISTENT;
        if (orange && locked) return Result.OS_PROPERTIES_INCONSISTENT;
        if (red || orange || unlocked) {
            return Result.OS_REPORTS_UNLOCKED_OR_UNVERIFIED;
        }
        if (yellow) return Result.OS_REPORTS_NONSTANDARD_TRUST;
        if (green && locked) return Result.OS_REPORTS_LOCKED_VERIFIED;
        return Result.UNKNOWN;
    }

    private static String normalize(String text) {
        return text == null ? "" : text.trim().toLowerCase(Locale.US);
    }
}
