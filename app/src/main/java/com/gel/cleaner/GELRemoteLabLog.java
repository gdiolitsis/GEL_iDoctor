package com.gel.cleaner;

import java.util.List;
import java.util.regex.Pattern;

/** Recognizes lab entries in the diagnostic stream shared by existing clients. */
final class GELRemoteLabLog {
    private static final Pattern LAB_ENTRY = Pattern.compile(
            "(?iu)(?:[ℹi✔⚠✖]\\x{FE0F}?\\s+)?"
                    + "(?:LAB\\s*(?:[12][0-9]|[1-9])(?:\\s*[A-CΑΒΓ]|\\s+(?:GAMMA|ΓΑΜΜΑ))?"
                    + "(?:\\s+(?:BASE|PRO))?"
                    + "(?:\\s*[-–—:]\\s*\\S.*|\\s+(?:started|ξεκίνησε)\\.?)"
                    + "|GEL Device Certification(?:\\s*[-–—:]\\s*\\S.*)?)");

    private GELRemoteLabLog() {}

    static boolean containsLabEntry(Object lines) {
        if (!(lines instanceof List<?>)) return false;
        for (Object line : (List<?>) lines) {
            if (line instanceof String && LAB_ENTRY.matcher(((String) line).trim()).matches()) {
                return true;
            }
        }
        return false;
    }
}
