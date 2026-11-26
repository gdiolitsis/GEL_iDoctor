package com.gel.cleaner;

/**
 * Visual cleanup for service report previews and PDF exports only.
 *
 * Never mutate GELServiceLog: other laboratories use its original diagnostic
 * text, and PdfRow colors are indexed against that unchanged source.
 */
public final class GELServiceReportLinePolicy {
    private GELServiceReportLinePolicy() {}

    private static boolean isDivider(String text) {
        if (text.length() < 6) return false;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != '-') return false;
        }
        return true;
    }

    /** True when the original line should be visible in a report. */
    public static boolean[] visibleMask(String[] lines) {
        boolean[] visible = new boolean[lines.length];
        boolean contentSeen = false;
        boolean previousDivider = false;
        boolean previousBlank = false;

        for (int i = 0; i < lines.length; i++) {
            String text = lines[i] == null ? "" : lines[i].trim();
            if (text.isEmpty()) {
                // No blank area above the first result, and no stack of blanks.
                visible[i] = contentSeen && !previousBlank;
                previousBlank = visible[i];
            } else if (isDivider(text)) {
                // Drop orphan leading dividers and collapse adjacent dividers,
                // even if a blank line lies between them.
                visible[i] = contentSeen && !previousDivider;
                previousDivider = true;
                previousBlank = false;
            } else {
                visible[i] = true;
                contentSeen = true;
                previousDivider = false;
                previousBlank = false;
            }
        }
        return visible;
    }

    public static String cleanPreview(String source) {
        if (source == null || source.isEmpty()) return "";
        String[] lines = source.split("\\n", -1);
        boolean[] mask = visibleMask(lines);
        StringBuilder out = new StringBuilder(source.length());
        for (int i = 0; i < lines.length; i++) {
            if (mask[i]) out.append(lines[i]).append('\n');
        }
        return out.toString().trim().replaceAll("\\n{3,}", "\n\n");
    }
}
