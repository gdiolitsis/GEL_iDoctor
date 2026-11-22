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
 * Source-extracted headings of tools/prerequisite PART NAMES only.
 *
 * This is automated factual METADATA and is deliberately NOT an independently
 * source-reviewed step-by-step repair. No panic code is interpreted; no part is
 * deemed faulty because a tool/prerequisite appears in Apple's public manual.
 *
 * Always open the full official illustrated instructions before attempting
 * physical service, verify model/variant and have the required expertise.
 */
public final class AppleIPhoneProcedureRequirements {
    public static final String ASSET = "iphone_apple_procedure_requirements_v1.tsv";

    public enum AssistantModality {
        REQUIRED_TEXT, RECOMMENDED_TEXT, MENTIONED_ONLY, NOT_MENTIONED
    }

    public static final class Record {
        public final String sourceUrl;
        public final int indexedChapters, modelManuals;
        public final String officialTitle, prerequisiteNames, toolNames;
        public final int prerequisiteCount, toolCount;
        public final boolean mentionsBatterySafety, mentionsBrokenGlassSafety;
        public final boolean hasWarningLabels;
        public final AssistantModality assistant;
        public final String checkedOn;

        private Record(String[] c) {
            sourceUrl = c[0];
            indexedChapters = Integer.parseInt(c[1]);
            modelManuals = Integer.parseInt(c[2]);
            officialTitle = c[4];
            prerequisiteNames = c[5];
            toolNames = c[6];
            prerequisiteCount = Integer.parseInt(c[7]);
            toolCount = Integer.parseInt(c[8]);
            mentionsBatterySafety = Boolean.parseBoolean(c[9]);
            mentionsBrokenGlassSafety = Boolean.parseBoolean(c[10]);
            hasWarningLabels = Boolean.parseBoolean(c[11]);
            assistant = AssistantModality.valueOf(c[12]);
            checkedOn = c[14];
        }

        public boolean hasNamedPrerequisites() { return prerequisiteCount > 0; }
        public boolean hasNamedTools() { return toolCount > 0; }

        /** A missing automatic extraction must never mean no required tools. */
        public String assistantLabel(boolean greek) {
            switch (assistant) {
                case REQUIRED_TEXT:
                    return greek ? "Απαιτείται, σύμφωνα με την πηγή" :
                            "Required, as stated in the source";
                case RECOMMENDED_TEXT:
                    return greek ? "Συνιστάται, σύμφωνα με την πηγή" :
                            "Recommended, as stated in the source";
                case MENTIONED_ONLY:
                    return greek ? "Αναφέρεται — ελέγξτε την επίσημη διαδικασία" :
                            "Mentioned — read the official procedure";
                default:
                    return greek ? "Δεν καταγράφηκε αναφορά — δεν σημαίνει μη αναγκαίο" :
                            "No mention extracted — necessity not determined";
            }
        }
    }

    private AppleIPhoneProcedureRequirements() {}

    private static boolean officialNumericArticle(String url) {
        return url != null && url.matches(
                "https://support\\.apple\\.com/[a-z]{2}(?:-[a-z]{2})?/[0-9]{5,7}");
    }

    public static List<Record> parse(InputStream stream,
            List<AppleIPhoneManualChapters.Chapter> catalog) throws IOException {
        if (stream == null || catalog == null)
            throw new IOException("Missing official Apple procedures or chapter index");
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Set<String>> manuals = new HashMap<>();
        for (AppleIPhoneManualChapters.Chapter chapter : catalog) {
            if (chapter.phase != AppleIPhoneManualChapters.Phase.PROCEDURE) continue;
            if (chapter.linkScope != AppleIPhoneManualChapters.LinkScope.DIRECT_CHAPTER)
                throw new IOException("Procedure lacks direct official source");
            Integer soFar = counts.get(chapter.sourceUrl);
            counts.put(chapter.sourceUrl, soFar == null ? 1 : soFar + 1);
            Set<String> used = manuals.get(chapter.sourceUrl);
            if (used == null) {
                used = new HashSet<>();
                manuals.put(chapter.sourceUrl, used);
            }
            used.add(chapter.manualId);
        }

        List<Record> records = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            int lineNo = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.trim().isEmpty() || line.startsWith("#")
                        || line.startsWith("source_url\t")) continue;
                String[] c = line.split("\t", -1);
                if (c.length != 15)
                    throw new IOException("Bad Apple requirement fields at " + lineNo);
                if (!officialNumericArticle(c[0]) || !seen.add(c[0]))
                    throw new IOException("Non-Apple or repeated source at " + lineNo);
                if (!"SOURCE_METADATA_EXTRACTED".equals(c[3])
                        || !"AUTOMATED_LABELS_ONLY_NOT_FULL_SERVICE_REVIEW".equals(c[13]))
                    throw new IOException("Source not structurally checked or misclassified");
                if (c[4].trim().isEmpty() || !c[14].matches("\\d{4}-\\d{2}-\\d{2}"))
                    throw new IOException("Missing Apple title or source-check date");
                for (int k = 9; k <= 11; k++) {
                    if (!"true".equals(c[k]) && !"false".equals(c[k]))
                        throw new IOException("Unclear Apple source flag at " + lineNo);
                }
                final Record r;
                try { r = new Record(c); }
                catch (RuntimeException ex) {
                    throw new IOException("Malformed Apple source metadata " + lineNo, ex);
                }
                if (r.prerequisiteCount < 0 || r.toolCount < 0
                        || r.prerequisiteCount != countLabels(r.prerequisiteNames)
                        || r.toolCount != countLabels(r.toolNames))
                    throw new IOException("Mismatch between Apple list counts and source labels");
                Integer sourceOccurrences = counts.get(r.sourceUrl);
                if (sourceOccurrences == null || r.indexedChapters != sourceOccurrences
                        || r.modelManuals != manuals.get(r.sourceUrl).size())
                    throw new IOException("Procedure source does not match model manual catalog");
                records.add(r);
            }
        }
        if (seen.size() != counts.size())
            throw new IOException("Some official Apple procedure article references are missing");
        return Collections.unmodifiableList(records);
    }

    private static int countLabels(String labels) {
        if (labels == null || labels.trim().isEmpty()) return 0;
        return labels.split("\\s+\\|\\s+", -1).length;
    }

    /** A record is readable only for the exact selected, source-listed chapter. */
    public static Record forChapter(List<Record> all,
            AppleIPhoneManualChapters.Chapter selected) {
        if (all == null || selected == null
                || selected.phase != AppleIPhoneManualChapters.Phase.PROCEDURE
                || selected.linkScope != AppleIPhoneManualChapters.LinkScope.DIRECT_CHAPTER)
            return null;
        for (Record r : all) {
            if (r.sourceUrl.equals(selected.sourceUrl)) return r;
        }
        return null;
    }
}
