package com.gel.cleaner;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

/** TXT PDF must retain the full evidence URL, never clip at paper margins. */
public final class GELPdfMeasuredLineWrapTest {
    private static final GELPdfMeasuredLineWrap.TextWidth MONOSPACE =
            text -> text.length();

    @Test public void longAppleManualLinkIsLosslesslyWrapped() {
        String line = "ℹ Apple — Source: https://support.apple.com/en-us/101967"
                + "?a=one&long_query=two&another_value=three and text after.";
        List<GELPdfMeasuredLineWrap.Fragment> fragments =
                GELPdfMeasuredLineWrap.split(line, 26, MONOSPACE);
        assertTrue(fragments.size() >= 3);
        StringBuilder joined = new StringBuilder();
        int expectedStart = 0;
        for (GELPdfMeasuredLineWrap.Fragment f : fragments) {
            assertEquals(expectedStart, f.start);
            assertEquals(line.substring(f.start, f.end), f.text);
            assertTrue(f.text.length() <= 26);
            assertTrue(f.end > f.start);
            expectedStart = f.end;
            joined.append(f.text);
        }
        assertEquals(line.length(), expectedStart);
        assertEquals(line, joined.toString());
    }

    @Test public void longUnbrokenHttpsUrlIsNeverOmitted() {
        String value = "https://support.apple.com/en-us/"
                + "104926abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz0123456789";
        List<GELPdfMeasuredLineWrap.Fragment> fragments =
                GELPdfMeasuredLineWrap.split(value, 12, MONOSPACE);
        StringBuilder joined = new StringBuilder();
        for (GELPdfMeasuredLineWrap.Fragment piece : fragments) {
            assertTrue(piece.text.length() <= 12);
            joined.append(piece.text);
        }
        assertEquals(value, joined.toString());
    }

    @Test public void wrappedSegmentsKeepOriginalValueOffsetsForPrintColors() {
        String line = "ℹ Επίσημη πηγή: https://support.apple.com/en-us/101966";
        int valueStart = line.indexOf("https://");
        assertTrue(valueStart > 0);
        int coloredChars = 0;
        for (GELPdfMeasuredLineWrap.Fragment fragment :
                GELPdfMeasuredLineWrap.split(line, 18, MONOSPACE)) {
            if (fragment.end <= valueStart) continue;
            int splitIndex = Math.max(0, valueStart - fragment.start);
            String suffix = fragment.text.substring(splitIndex);
            assertFalse(suffix.isEmpty());
            coloredChars += suffix.length();
        }
        assertEquals(line.length() - valueStart, coloredChars);
    }

    @Test public void supplementaryUnicodeIsNotSplitWithinSurrogatePair() {
        String line = "ℹ 🙂 📱 Apple source URL";
        List<GELPdfMeasuredLineWrap.Fragment> items =
                GELPdfMeasuredLineWrap.split(line, 4, MONOSPACE);
        StringBuilder joined = new StringBuilder();
        for (GELPdfMeasuredLineWrap.Fragment item : items) {
            assertFalse(item.text.isEmpty());
            assertFalse(Character.isHighSurrogate(
                    item.text.charAt(item.text.length() - 1)));
            assertFalse(Character.isLowSurrogate(item.text.charAt(0)));
            joined.append(item.text);
        }
        assertEquals(line, joined.toString());
    }

    @Test public void blanksAndInvalidWidthsAreHandledExplicitly() {
        assertTrue(GELPdfMeasuredLineWrap.split("", 20, MONOSPACE).isEmpty());
        assertTrue(GELPdfMeasuredLineWrap.split(null, 20, MONOSPACE).isEmpty());
        try {
            GELPdfMeasuredLineWrap.split("source", 0, MONOSPACE);
            fail("Zero paper width must fail");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("positive"));
        }
    }

    @Test public void veryNarrowPaperWidthStillMakesProgress() {
        String line = "📱source";
        List<GELPdfMeasuredLineWrap.Fragment> list =
                GELPdfMeasuredLineWrap.split(line, 1, MONOSPACE);
        StringBuilder all = new StringBuilder();
        for (GELPdfMeasuredLineWrap.Fragment fragment : list)
            all.append(fragment.text);
        assertEquals(line, all.toString());
    }
}
