package com.gel.cleaner;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Reads only Google's public Android Security Bulletins index.
 * This compares published AOSP patch levels; it does NOT check device-specific OTA availability.
 */
final class AndroidSecurityBulletinChecker {
    static final String OFFICIAL_INDEX =
            "https://source.android.com/docs/security/bulletin/asb-overview";

    private static final int MAX_RESPONSE_BYTES = 750_000;
    private static final Pattern ROW = Pattern.compile("(?is)<tr\\b[^>]*>(.*?)</tr\\s*>");
    private static final Pattern BULLETIN_LINK = Pattern.compile(
            "(?i)(?:/docs/security/bulletin/)(?:20\\d{2}/)?(20\\d{2}-(?:0[1-9]|1[0-2])-01)(?!\\d)");
    private static final Pattern PATCH_LEVEL = Pattern.compile(
            "\\b20\\d{2}-(?:0[1-9]|1[0-2])-(?:01|05)\\b");

    private AndroidSecurityBulletinChecker() {}

    /** null means the official source was not successfully verified (offline, changed page, etc.). */
    static LocalDate fetchLatestPublishedPatchLevel() {
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) new URL(OFFICIAL_INDEX).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("Accept", "text/html");
            connection.setRequestProperty("User-Agent", "GEL-iDoctor/Android-Security-Bulletin-Check");
            if (connection.getResponseCode() != 200
                    || !"source.android.com".equalsIgnoreCase(connection.getURL().getHost())) {
                return null;
            }
            try (InputStream in = connection.getInputStream();
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                byte[] bytes = new byte[8192];
                int read;
                while ((read = in.read(bytes)) != -1) {
                    if (buffer.size() + read > MAX_RESPONSE_BYTES) return null;
                    buffer.write(bytes, 0, read);
                }
                return parseLatestPublishedPatchLevel(
                        buffer.toString(StandardCharsets.UTF_8.name()),
                        LocalDate.now(java.time.ZoneOffset.UTC));
            }
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    /**
     * Restrict findings to patch dates inside actual Android Security Bulletin table rows,
     * matching the linked bulletin's year/month. Ignore dates in menus, unrelated articles,
     * and future entries; never guess a current patch date from the device clock.
     */
    static LocalDate parseLatestPublishedPatchLevel(String html, LocalDate today) {
        if (html == null || today == null) return null;
        Matcher rows = ROW.matcher(html);
        LocalDate latest = null;
        while (rows.find()) {
            String row = rows.group(1);
            Matcher link = BULLETIN_LINK.matcher(row);
            if (!link.find()) continue;
            String bulletinMonth = link.group(1).substring(0, 7);
            Matcher dates = PATCH_LEVEL.matcher(row);
            while (dates.find()) {
                String candidate = dates.group();
                if (!candidate.startsWith(bulletinMonth)) continue;
                try {
                    LocalDate parsed = LocalDate.parse(candidate);
                    if (!parsed.isAfter(today) && (latest == null || parsed.isAfter(latest))) {
                        latest = parsed;
                    }
                } catch (java.time.format.DateTimeParseException ignored) {
                    // Ignore malformed or invalid dates from HTML.
                }
            }
        }
        return latest;
    }
}
