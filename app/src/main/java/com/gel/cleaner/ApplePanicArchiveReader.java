package com.gel.cleaner;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** Bounded, read-only importer for user-selected Apple panic archives.
 * No device hardware inference, no persistence and no file extraction to disk. */
final class ApplePanicArchiveReader {
    static final class Archive {
        final String text;
        final int eligibleEntries;
        Archive(String text, int eligibleEntries) {
            this.text = text;
            this.eligibleEntries = eligibleEntries;
        }
    }

    private ApplePanicArchiveReader() {}

    static Archive read(InputStream input, int maxEntries, int perEntryBytes,
                        int combinedCharacters) throws IOException {
        if (input == null || maxEntries < 1 || perEntryBytes < 1 || combinedCharacters < 1)
            throw new IllegalArgumentException("Invalid panic ZIP input or size caps");

        StringBuilder all = new StringBuilder();
        int count = 0;
        int scanned = 0;

        try (ZipInputStream zip = new ZipInputStream(new BufferedInputStream(input))) {
            ZipEntry entry;
            byte[] buffer = new byte[4096];
            while ((entry = zip.getNextEntry()) != null) {
                if (++scanned > maxEntries) throw new IOException("ZIP entry limit reached");
                if (entry.isDirectory()) { zip.closeEntry(); continue; }

                String name = entry.getName();
                String lower = name.toLowerCase(Locale.ROOT);
                boolean candidate = lower.contains("panic") || lower.endsWith(".ips")
                        || lower.endsWith(".log") || lower.endsWith(".txt");
                // Even skipped entries are scanned with limits. ZIP-bombs and huge
                // renamed payloads must not bypass an entry-size cap.
                ByteArrayOutputStream raw = candidate ? new ByteArrayOutputStream() : null;
                int bytes = 0;
                int n;
                while ((n = zip.read(buffer)) != -1) {
                    if (n > perEntryBytes - bytes) throw new IOException("ZIP entry exceeds size cap");
                    bytes += n;
                    if (candidate) raw.write(buffer, 0, n);
                }
                zip.closeEntry();
                if (!candidate) continue;

                byte[] data = raw.toByteArray();
                String decoded = new String(data, StandardCharsets.UTF_8);
                if (decoded.indexOf('\uFFFD') >= 0)
                    decoded = new String(data, StandardCharsets.ISO_8859_1);
                if (!ApplePanicEvidencePolicy.hasPanicEvidence(decoded)) continue;

                // Names are evidence identifiers, never paths; strip markup and
                // controls to prevent fake delimiters or HTML in the UI report.
                String safeName = name.replaceAll("[\\r\\n\\t<>]", "_");
                if (safeName.length() > 160) safeName = safeName.substring(0, 160);
                String record = "\n===== ZIP FILE: " + safeName + " =====\n" + decoded + "\n";
                if (record.length() > combinedCharacters - all.length())
                    throw new IOException("Combined panic ZIP text exceeds size cap");
                all.append(record);
                count++;
            }
        }
        if (count == 0) throw new IOException("No supported panic log entries in ZIP");
        return new Archive(all.toString(), count);
    }
}
