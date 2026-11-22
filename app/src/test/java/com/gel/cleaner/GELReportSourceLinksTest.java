package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/** In HTML PDF, curated Apple/forum source links survive with safe markup. */
public final class GELReportSourceLinksTest {
    @Test public void appleOfficialSourceAndQueryRemainLinked() {
        String html = GELReportSourceLinks.escapeAndLinkify(
                "Apple: https://support.apple.com/en-us/101969?source=lab4&lang=en");
        assertTrue(html.contains("<a class='source-link' href='https://support.apple.com/en-us/101969?source=lab4&amp;lang=en'>"));
        assertTrue(html.contains(">https://support.apple.com/en-us/101969?source=lab4&amp;lang=en</a>"));
        assertFalse(html.contains("&lang=en'"));
    }

    @Test public void originalIndependentRepairReportsAreClickable() {
        String html = GELReportSourceLinks.escapeAndLinkify(
                "iFixit https://www.ifixit.com/Answers/View/795864/ NoPowerFix https://forum.nopowerfix.com/t/iphone-14-pro-max-pink-screen-and-restarts-front-flex-fixed/28");
        assertEquals(2, occurrences(html, "class='source-link'"));
        assertTrue(html.contains("https://www.ifixit.com/Answers/View/795864/"));
        assertTrue(html.contains("https://forum.nopowerfix.com/t/iphone-14-pro-max-pink-screen-and-restarts-front-flex-fixed/28"));
    }

    @Test public void unknownOrSpoofedHostsNeverBecomeHyperlinks() {
        String html = GELReportSourceLinks.escapeAndLinkify(
                "https://www.ifixit.com.evil.example/steal "
                + "https://support.apple.com@evil.example/login "
                + "https://example.org/fake "
                + "http://support.apple.com/en-us/101969");
        assertEquals(0, occurrences(html, "<a "));
    }

    @Test public void hostileLogMarkupIsEscapedNotInterpreted() {
        String html = GELReportSourceLinks.escapeAndLinkify(
                "<script>alert('x')</script> \"quote\" & "
                + "https://support.apple.com/en-us/101966");
        assertFalse(html.contains("<script>"));
        assertTrue(html.contains("&lt;script&gt;alert(&#39;x&#39;)&lt;/script&gt;"));
        assertTrue(html.contains("&quot;quote&quot; &amp; "));
        assertEquals(1, occurrences(html, "class='source-link'"));
    }

    @Test public void trailingPunctuationIsNotIncludedInClickTarget() {
        String html = GELReportSourceLinks.escapeAndLinkify(
                "(https://support.apple.com/en-us/101967), see Apple.");
        assertTrue(html.contains("href='https://support.apple.com/en-us/101967'"));
        assertTrue(html.contains("</a>), see Apple."));
    }

    @Test public void nonUrlDeviceAndClueTextIsNeverRewritten() {
        assertEquals("iPhone15,2 SMC 0x40000",
                GELReportSourceLinks.escapeAndLinkify("iPhone15,2 SMC 0x40000"));
        assertEquals("", GELReportSourceLinks.escapeAndLinkify(null));
        assertEquals("", GELReportSourceLinks.escapeAndLinkify(""));
    }

    private static int occurrences(String input, String needle) {
        int total = 0, at = 0;
        while ((at = input.indexOf(needle, at)) >= 0) {
            total++;
            at += needle.length();
        }
        return total;
    }
}
