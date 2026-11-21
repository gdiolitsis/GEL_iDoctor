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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Distinct from the complete URL chapter index and from physical repair steps:
 * independently written, source-reviewed technical triage notes for selected
 * sections of genuine Apple Support articles. One Apple article can contain
 * several separate topics and apply to multiple iPhone models.
 *
 * The chapter ID is always resolved from the exact-model Apple catalog before
 * showing a note. No inference from panic code to failed component or Apple
 * repair recommendation. An unreviewed article receives no invented notes.
 */
public final class AppleIPhoneTechnicalKnowledge {
    public static final String ASSET = "iphone_apple_technical_findings_v1.tsv";

    public enum Kind {
        SYMPTOM, FIRST_CHECK, DIAGNOSTIC, ELIGIBILITY, SAFETY, ESCALATION, VERIFY
    }

    public enum Risk { INFO, NON_INVASIVE, DATA_LOSS, TECHNICIAN_ONLY }

    public static final class Finding {
        public final String id;
        public final String sourceUrl;
        public final AppleIPhoneManualChapters.Phase phase;
        public final String topic;
        public final Kind kind;
        public final Risk risk;
        public final String greek;
        public final String english;
        public final String reviewedOn;

        private Finding(String[] c) {
            id = c[0];
            sourceUrl = c[1];
            phase = AppleIPhoneManualChapters.Phase.valueOf(c[2]);
            topic = c[3];
            kind = Kind.valueOf(c[4]);
            risk = Risk.valueOf(c[5]);
            greek = c[6];
            english = c[7];
            reviewedOn = c[8];
        }

        public String description(boolean greekLanguage) {
            return greekLanguage ? greek : english;
        }
    }

    private AppleIPhoneTechnicalKnowledge() {}

    private static String scopeKey(String url, AppleIPhoneManualChapters.Phase phase,
            String topic) {
        return url + "\u001e" + phase.name() + "\u001e" + topic;
    }

    /**
     * Only reviewed statements bound to a real, official, direct Apple article,
     * with an actual chapter whose topic and phase match. A shared Apple
     * troubleshooting page never silently extends statements to other topics.
     */
    public static List<Finding> parse(InputStream source,
            List<AppleIPhoneManualChapters.Chapter> chapters) throws IOException {
        if (source == null || chapters == null)
            throw new IOException("Missing Apple fact source or model chapter index");
        Set<String> knownScopes = new HashSet<>();
        for (AppleIPhoneManualChapters.Chapter chapter : chapters) {
            if (chapter.linkScope == AppleIPhoneManualChapters.LinkScope.DIRECT_CHAPTER) {
                knownScopes.add(scopeKey(chapter.sourceUrl, chapter.phase, chapter.topic));
            }
        }

        List<Finding> entries = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                source, StandardCharsets.UTF_8))) {
            int number = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                number++;
                if (line.trim().isEmpty() || line.startsWith("#")
                        || line.startsWith("fact_id\t")) continue;
                String[] columns = line.split("\t", -1);
                if (columns.length != 9)
                    throw new IOException("Apple fact field count at line " + number);
                for (String part : columns) {
                    if (part.trim().isEmpty())
                        throw new IOException("Incomplete Apple fact at line " + number);
                }
                final Finding fact;
                try {
                    fact = new Finding(columns);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Invalid Apple fact enum at line " + number, e);
                }
                if (!ids.add(fact.id))
                    throw new IOException("Duplicate Apple fact ID: " + fact.id);
                if (!fact.sourceUrl.matches(
                        "https://support\\.apple\\.com/[a-z]{2}(?:-[a-z]{2})?/[0-9]{5,7}"))
                    throw new IOException("Not an official reviewed Apple article");
                if (!knownScopes.contains(scopeKey(fact.sourceUrl, fact.phase, fact.topic)))
                    throw new IOException("Apple fact has no exact matching source/topic");
                if (!fact.reviewedOn.matches("\\d{4}-\\d{2}-\\d{2}"))
                    throw new IOException("Fact needs a review date");
                entries.add(fact);
            }
        }
        return Collections.unmodifiableList(entries);
    }

    public static List<Finding> forChapter(List<Finding> entries,
            AppleIPhoneManualChapters.Chapter selected) {
        if (entries == null || selected == null
                || selected.linkScope != AppleIPhoneManualChapters.LinkScope.DIRECT_CHAPTER)
            return Collections.emptyList();
        List<Finding> output = new ArrayList<>();
        for (Finding fact : entries) {
            if (fact.sourceUrl.equals(selected.sourceUrl)
                    && fact.phase == selected.phase
                    && fact.topic.equals(selected.topic)) output.add(fact);
        }
        return Collections.unmodifiableList(output);
    }

    /**
     * Only for verified exact hardware models; directory-only Apple manuals
     * require explicit user selection for browsing reference information.
     */
    public static List<Finding> forExactModel(List<Finding> entries,
            List<AppleIPhoneManualChapters.Chapter> chapters,
            List<AppleOfficialServiceGuidance.Guidance> guides, String identifier,
            AppleIPhoneManualChapters.Phase phase, String topic) {
        if (entries == null || topic == null || phase == null)
            return Collections.emptyList();
        List<AppleIPhoneManualChapters.Chapter> exact =
                AppleIPhoneManualChapters.forExactModel(chapters, guides, identifier);
        List<Finding> output = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (AppleIPhoneManualChapters.Chapter chapter : exact) {
            if (chapter.phase != phase || !chapter.topic.equals(topic)) continue;
            for (Finding f : forChapter(entries, chapter)) {
                if (seen.add(f.id)) output.add(f);
            }
        }
        return Collections.unmodifiableList(output);
    }

    public static int chapterCoverage(List<Finding> entries,
            List<AppleIPhoneManualChapters.Chapter> chapters) {
        if (entries == null || chapters == null) return 0;
        int covered = 0;
        for (AppleIPhoneManualChapters.Chapter chapter : chapters)
            if (!forChapter(entries, chapter).isEmpty()) covered++;
        return covered;
    }
}
