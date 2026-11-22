package com.gel.cleaner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML-safe, offline linkification for source URLs in exported service reports.
 *
 * The iPhone Labs write plain text to GELServiceLog, not HTML. Only absolute
 * HTTPS URLs on already-curated Apple/iFixit/NoPowerFix source hosts can become
 * anchors during HTML PDF rendering. No URL is fetched or sent by the app.
 * Unrecognized URLs remain escaped text.
 */
public final class GELReportSourceLinks {
    private GELReportSourceLinks() {}

    private static final Pattern SOURCE_URL = Pattern.compile(
            "https://(?:support\\.apple\\.com|www\\.ifixit\\.com|"
            + "forum\\.nopowerfix\\.com)/[A-Za-z0-9._~/%?=&+#;:@!$'()*,-]+");

    public static String escapeAndLinkify(String input) {
        if (input == null || input.isEmpty()) return "";
        Matcher links = SOURCE_URL.matcher(input);
        StringBuilder html = new StringBuilder(input.length() + 32);
        int previous = 0;
        while (links.find()) {
            int end = links.end();
            // Keep adjacent sentence punctuation outside the hyperlink.
            while (end > links.start() && ".,;:!?)".indexOf(input.charAt(end - 1)) >= 0) {
                end--;
            }
            if (end == links.start()) continue;
            html.append(escape(input.substring(previous, links.start())));
            String url = input.substring(links.start(), end);
            html.append("<a class='source-link' href='")
                    .append(escape(url))
                    .append("'>")
                    .append(escape(url))
                    .append("</a>");
            previous = end;
        }
        html.append(escape(input.substring(previous)));
        return html.toString();
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
