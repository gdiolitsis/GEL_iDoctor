package com.gel.cleaner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Width-measured, lossless line wrapping for the Android Canvas TXT PDF.
 *
 * Never truncate Apple support URLs or panic evidence lines: a link split into
 * visual lines is still fully readable in the printed report. Fragments retain
 * their offsets into the original line so original value colors can be applied
 * after wrapping without guessing status from the text.
 *
 * HTML PDF uses hyperlinks and its own web layout rather than this helper.
 */
public final class GELPdfMeasuredLineWrap {
    private GELPdfMeasuredLineWrap() {}

    public interface TextWidth {
        float measure(String text);
    }

    public static final class Fragment {
        public final String text;
        public final int start;
        public final int end;

        private Fragment(String text, int start, int end) {
            this.text = text;
            this.start = start;
            this.end = end;
        }
    }

    public static List<Fragment> split(String line, float maxWidth, TextWidth width) {
        if (line == null || line.isEmpty()) return Collections.emptyList();
        if (width == null || maxWidth <= 0f)
            throw new IllegalArgumentException("A positive PDF width is required");
        List<Fragment> result = new ArrayList<>();
        int start = 0;
        while (start < line.length()) {
            int remaining = line.length() - start;
            int lo = 0, hi = remaining;
            // Find the longest measured prefix that actually fits Canvas
            // (character-count heuristics clip long manufacturer source URLs).
            while (lo < hi) {
                int mid = lo + (hi - lo + 1) / 2;
                int cut = avoidHalfSurrogate(line, start + mid);
                if (cut <= start) {
                    lo = mid;
                    continue;
                }
                float measured = width.measure(line.substring(start, cut));
                if (measured <= maxWidth) lo = mid;
                else hi = mid - 1;
            }
            int end = avoidHalfSurrogate(line, start + lo);
            if (end <= start) {
                end = start + Character.charCount(line.codePointAt(start));
            }
            // Prefer a nearby word boundary without skipping whitespace;
            // URLs without spaces are split at a measured glyph boundary.
            if (end < line.length() && end - start > 12) {
                int boundary = -1;
                for (int i = end - 1; i > start + (end - start) / 2; i--) {
                    if (Character.isWhitespace(line.charAt(i))) {
                        boundary = i + 1;
                        break;
                    }
                }
                if (boundary > start && boundary <= end) end = boundary;
            }
            result.add(new Fragment(line.substring(start, end), start, end));
            start = end;
        }
        return Collections.unmodifiableList(result);
    }

    private static int avoidHalfSurrogate(String line, int cut) {
        if (cut > 0 && cut < line.length()
                && Character.isHighSurrogate(line.charAt(cut - 1))
                && Character.isLowSurrogate(line.charAt(cut))) {
            return cut - 1;
        }
        return cut;
    }
}
