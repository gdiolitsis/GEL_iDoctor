package com.gel.cleaner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Offline index of chapter titles from real Apple iPhone Repair Manual contents.
 *
 * Each chapter points at its Apple manual landing page (MANUAL_CONTENTS) unless
 * an Apple-authored direct chapter link is explicitly source-verified.
 * A listed chapter NEVER means Apple diagnosed or recommended replacing a part
 * based on a GEL-extracted panic signature. No logs or identifiers are sent.
 */
public final class AppleIPhoneManualChapters {
    public static final String ASSET = "iphone_apple_manual_chapters_v1.tsv";

    public enum Phase { SAFETY, CHECK, DIAGNOSTICS, TROUBLESHOOT, REFERENCE, PROCEDURE }
    public enum LinkScope { MANUAL_CONTENTS, DIRECT_CHAPTER }

    private AppleIPhoneManualChapters() {}

    public static final class Chapter {
        public final String id;
        public final String manualId;
        public final Phase phase;
        public final String topic;
        public final String titleGreek;
        public final String titleEnglish;
        public final String sourceUrl;
        public final LinkScope linkScope;

        private Chapter(String[] c) {
            id = c[0];
            manualId = c[1];
            phase = Phase.valueOf(c[2]);
            topic = c[3];
            titleGreek = c[4];
            titleEnglish = c[5];
            sourceUrl = c[6];
            linkScope = LinkScope.valueOf(c[7]);
        }

        public String title(boolean greek) { return greek ? titleGreek : titleEnglish; }
    }

    public static List<Chapter> parse(InputStream source,
            List<AppleOfficialServiceGuidance.Guidance> manualSources) throws IOException {
        if (source == null) throw new IOException("Missing Apple manual chapter catalog");
        if (manualSources == null) throw new IOException("Missing verified Apple manual index");
        Map<String, String> officialManuals = new HashMap<>();
        for (AppleOfficialServiceGuidance.Guidance doc : manualSources) {
            if ("MODEL_MANUAL".equals(doc.category)
                    || "MODEL_MANUAL_DIRECTORY".equals(doc.category)) {
                officialManuals.put(doc.id, doc.sourceUrl);
            }
        }
        List<Chapter> rows = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(source, StandardCharsets.UTF_8))) {
            int number = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                number++;
                if (line.trim().isEmpty() || line.startsWith("#")
                        || line.startsWith("entry_id\t")) continue;
                String[] c = line.split("\t", -1);
                if (c.length != 8) throw new IOException("Invalid chapter field count at " + number);
                for (String value : c) {
                    if (value.trim().isEmpty())
                        throw new IOException("Incomplete chapter entry at " + number);
                }
                if (!ids.add(c[0]))
                    throw new IOException("Duplicate chapter ID " + c[0]);
                String sourceUrl = officialManuals.get(c[1]);
                if (sourceUrl == null)
                    throw new IOException("Unknown Apple manual ID " + c[1]);
                if (!c[6].startsWith("https://support.apple.com/"))
                    throw new IOException("Non-Apple chapter reference at " + number);
                final Chapter item;
                try {
                    item = new Chapter(c);
                } catch (IllegalArgumentException ex) {
                    throw new IOException("Invalid chapter phase or link scope at " + number, ex);
                }
                if (item.linkScope == LinkScope.MANUAL_CONTENTS
                        && !item.sourceUrl.equals(sourceUrl)) {
                    throw new IOException("Chapter index must use its own exact model manual URL");
                }
                if (item.phase == Phase.PROCEDURE && item.topic.equals("UNKNOWN"))
                    throw new IOException("Unclassified procedure: " + item.id);
                rows.add(item);
            }
        }
        return Collections.unmodifiableList(rows);
    }

    /** Explicit catalog browsing is by manual ID and never infers hardware matches. */
    public static List<Chapter> forManualId(List<Chapter> all, String id) {
        if (all == null || id == null) return Collections.emptyList();
        List<Chapter> out = new ArrayList<>();
        for (Chapter chapter : all) if (chapter.manualId.equals(id)) out.add(chapter);
        return Collections.unmodifiableList(out);
    }

    /**
     * Only verified hardware-id/manual associations are automatically exposed.
     * Directory-only models NEVER match a guessed hardware code.
     */
    public static List<Chapter> forExactModel(List<Chapter> all,
            List<AppleOfficialServiceGuidance.Guidance> guides, String model) {
        AppleOfficialServiceGuidance.Guidance manual =
                AppleOfficialServiceGuidance.modelManual(guides, model);
        if (manual == null) return Collections.emptyList();
        // Apple sometimes includes shared, cross-model troubleshooting links
        // in a specific manual's TOC. The raw 1,074 source links must stay
        // intact for catalog browsing, but exact-device diagnosis must never
        // suggest a biometric feature absent from the verified device.
        // Every currently auto-matchable MODEL_MANUAL is either the SE3
        // (Home button + Touch ID) or a Face ID iPhone 12-16 series model.
        List<Chapter> relevant = new ArrayList<>();
        boolean touchIdPhone = "MANUAL_SE3".equals(manual.id);
        for (Chapter chapter : forManualId(all, manual.id)) {
            if (touchIdPhone && "FACE_ID".equals(chapter.topic)) continue;
            if (!touchIdPhone && "TOUCH_ID".equals(chapter.topic)) continue;
            relevant.add(chapter);
        }
        return Collections.unmodifiableList(relevant);
    }

    public static List<Chapter> phase(List<Chapter> source, Phase phase) {
        if (source == null || phase == null) return Collections.emptyList();
        List<Chapter> out = new ArrayList<>();
        for (Chapter chapter : source) if (chapter.phase == phase) out.add(chapter);
        return Collections.unmodifiableList(out);
    }

    /** Match a symptom category to official troubleshooting headings, not parts. */
    public static List<Chapter> troubleshootingForTopics(List<Chapter> chapters,
            Set<String> observedClueTopics) {
        if (chapters == null) return Collections.emptyList();
        Set<String> topics = new LinkedHashSet<>();
        topics.add("RESTART"); // Conditional help for imported panic/restart records
        if (observedClueTopics != null) {
            for (String raw : observedClueTopics) {
                if (raw == null) continue;
                switch (raw.toUpperCase(Locale.ROOT)) {
                    case "MICROPHONE": case "SPEAKER": topics.add("SOUND"); break;
                    case "DISPLAY_TOUCH": topics.add("DISPLAY"); topics.add("TOUCH"); break;
                    case "FACE_ID": topics.add("FACE_ID"); break;
                    case "CAMERA": topics.add("CAMERA"); break;
                    case "WIRED_CHARGING": topics.add("WIRED_CHARGING");
                        topics.add("BATTERY_POWER"); break;
                    case "BATTERY_POWER": topics.add("BATTERY");
                        topics.add("BATTERY_POWER"); break;
                    case "CELLULAR_SERVICE": topics.add("CELLULAR"); break;
                    default: break; // Do not manufacture manufacturer code interpretations
                }
            }
        }
        List<Chapter> out = new ArrayList<>();
        for (Chapter c : chapters) {
            if (c.phase == Phase.TROUBLESHOOT && topics.contains(c.topic)) out.add(c);
        }
        return Collections.unmodifiableList(out);
    }
}
