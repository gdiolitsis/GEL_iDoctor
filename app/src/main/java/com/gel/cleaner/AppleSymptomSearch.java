package com.gel.cleaner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Offline search over the selected iPhone's indexed Apple manual chapters.
 *
 * Search vocabulary is supplied by an editable TSV, not by a series of
 * hardcoded user questions. All results are sourced from official URLs
 * already validated by AppleIPhoneManualChapters / AppleOfficialServiceGuidance.
 * It is a conservative evidence index, not AI diagnosis or unlimited web search.
 */
public final class AppleSymptomSearch {
    public static final String ASSET = "iphone_apple_symptom_search_terms_v1.tsv";
    private static final int MAX_RESULTS = 6;
    private static final Set<String> STOP = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "δεν","δε","μου","σου","του","την","την","τον","στη","στο","στα",
            "στην","στον","της","και","που","οταν","πολυ","καθε","πριν",
            "με","για","απο","ενα","εχει","κανει","ειναι","κινητο",
            "τηλεφωνο","iphone","apple","προβλημα","προβληματα","βλαβη",
            "στοιχεια","τι","το","τα","οι","η","ο","να","σε","αν","ως",
            "not","the","my","it","is","a","an","of","on","in","for","to",
            "with","and","does","do","has","have","phone","iphone","problem",
            "issue","issues","doesnt","wont","sometimes","often"
    )));

    private AppleSymptomSearch() {}

    public static final class Terms {
        public final String topic;
        public final List<String> greek;
        public final List<String> english;

        private Terms(String topic, String[] gr, String[] en) {
            this.topic = topic;
            this.greek = Collections.unmodifiableList(Arrays.asList(gr));
            this.english = Collections.unmodifiableList(Arrays.asList(en));
        }
    }

    public static final class Hit {
        public final String topic, titleGreek, titleEnglish, url;
        public final boolean modelIndexed;
        public final boolean directChapter;
        public final int score;

        private Hit(String topic, String gr, String en, String url,
                boolean modelIndexed, boolean directChapter, int score) {
            this.topic = topic;
            this.titleGreek = gr;
            this.titleEnglish = en;
            this.url = url;
            this.modelIndexed = modelIndexed;
            this.directChapter = directChapter;
            this.score = score;
        }

        public String title(boolean greek) {
            return greek ? titleGreek : titleEnglish;
        }
    }

    public static List<Terms> readTerms(InputStream source) throws IOException {
        if (source == null) throw new IOException("Missing Apple symptom vocabulary");
        List<Terms> entries = new ArrayList<>();
        Set<String> topics = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(source, StandardCharsets.UTF_8))) {
            String line;
            int n = 0;
            while ((line = reader.readLine()) != null) {
                n++;
                if (line.trim().isEmpty() || line.startsWith("#")
                        || line.startsWith("topic\t")) continue;
                String[] fields = line.split("\t", -1);
                if (fields.length != 3 || fields[0].trim().isEmpty()
                        || fields[1].trim().isEmpty() || fields[2].trim().isEmpty())
                    throw new IOException("Invalid symptom-search term row " + n);
                if (!topics.add(fields[0]))
                    throw new IOException("Duplicate symptom topic " + fields[0]);
                entries.add(new Terms(fields[0],
                        fields[1].split(";"), fields[2].split(";")));
            }
        }
        return Collections.unmodifiableList(entries);
    }

    private static final class Entry {
        String topic, greek, english, url;
        boolean modelIndexed, directChapter;
        Entry(String t, String gr, String en, String u, boolean exact, boolean direct) {
            topic=t; greek=gr; english=en; url=u; modelIndexed=exact; directChapter=direct;
        }
    }

    /**
     * Only a chapter of the declared model, or an explicitly generic Apple
     * support procedure. Other models' manuals are never candidate results.
     */
    public static List<Hit> search(String input,
            AppleOfficialServiceGuidance.Guidance manual,
            List<AppleIPhoneManualChapters.Chapter> chapters,
            List<AppleOfficialServiceGuidance.Guidance> guidance,
            List<Terms> terms) {
        if (manual == null || input == null || input.trim().isEmpty())
            return Collections.emptyList();
        List<String> words = significantWords(input);
        if (words.isEmpty()) return Collections.emptyList();

        Map<String,Terms> byTopic = new HashMap<>();
        if (terms != null) for (Terms item : terms) byTopic.put(item.topic, item);
        List<Entry> candidates = new ArrayList<>();

        boolean isTouchIdModel = "MANUAL_SE3".equals(manual.id);
        if (chapters != null) for (AppleIPhoneManualChapters.Chapter chapter : chapters) {
            if (!manual.id.equals(chapter.manualId)
                    || chapter.phase != AppleIPhoneManualChapters.Phase.TROUBLESHOOT
                    || !AppleTroubleshootingLibrary.isOfficialUrl(chapter.sourceUrl))
                continue;
            if (isTouchIdModel && "FACE_ID".equals(chapter.topic)) continue;
            if (!isTouchIdModel && "TOUCH_ID".equals(chapter.topic)) continue;
            candidates.add(new Entry(chapter.topic, chapter.titleGreek,
                    chapter.titleEnglish, chapter.sourceUrl, true,
                    chapter.linkScope == AppleIPhoneManualChapters.LinkScope.DIRECT_CHAPTER));
        }

        // Generic Apple pages are considered only when their specific symptom
        // matches the user's free-text description; they are labeled generic.
        if (guidance != null) for (AppleOfficialServiceGuidance.Guidance guide : guidance) {
            if (!"*".equals(guide.models)
                    || !"APPLE_SYMPTOM_WORKFLOW".equals(guide.sourceKind)
                    || !AppleTroubleshootingLibrary.isOfficialUrl(guide.sourceUrl)) continue;
            if (isTouchIdModel && "FACE_ID".equals(guide.category)) continue;
            if (!isTouchIdModel && "TOUCH_ID".equals(guide.category)) continue;
            candidates.add(new Entry(guide.category, guide.titleGreek,
                    guide.titleEnglish, guide.sourceUrl, false, true));
        }

        List<Hit> scored = new ArrayList<>();
        for (Entry item : candidates) {
            Terms alias = byTopic.get(item.topic);
            int points = score(words, input, item, alias);
            if (points >= 5) {
                scored.add(new Hit(item.topic, item.greek, item.english,
                        item.url, item.modelIndexed, item.directChapter, points));
            }
        }
        scored.sort(Comparator.comparingInt((Hit h) -> h.score).reversed()
                .thenComparing((Hit h) -> !h.modelIndexed)
                .thenComparing(h -> h.titleEnglish));

        // Several official chapters legitimately share a single Apple page.
        // Do not show the same URL as four different results.
        LinkedHashMap<String,Hit> unique = new LinkedHashMap<>();
        for (Hit h : scored) {
            Hit previous = unique.get(h.url);
            if (previous == null
                    || (!previous.modelIndexed && h.modelIndexed
                        && previous.score <= h.score + 4)) unique.put(h.url, h);
        }
        List<Hit> ordered = new ArrayList<>(unique.values());
        ordered.sort(Comparator.comparingInt((Hit h) -> h.score).reversed()
                .thenComparing((Hit h) -> !h.modelIndexed)
                .thenComparing(h -> h.titleEnglish));

        if (ordered.size() > MAX_RESULTS)
            ordered = new ArrayList<>(ordered.subList(0, MAX_RESULTS));
        return Collections.unmodifiableList(ordered);
    }

    private static int score(List<String> words, String input, Entry entry, Terms terms) {
        Set<String> title = new LinkedHashSet<>(significantWords(entry.greek + " " + entry.english));
        Set<String> alias = new LinkedHashSet<>();
        if (terms != null) {
            for (String term : terms.greek) alias.addAll(significantWords(term));
            for (String term : terms.english) alias.addAll(significantWords(term));
        }
        int count = 0;
        int score = 0;
        for (String word : words) {
            if (title.contains(word)) { score += 7; count++; }
            else if (alias.contains(word)) { score += 6; count++; }
        }
        if (count == 0) return 0;
        if (terms != null) {
            String phrase = normalize(input);
            for (String aliasPhrase : terms.greek) {
                String normalized = normalize(aliasPhrase);
                if (normalized.length() >= 6 && phrase.contains(normalized)) {
                    score += 4;
                    break;
                }
            }
            for (String aliasPhrase : terms.english) {
                String normalized = normalize(aliasPhrase);
                if (normalized.length() >= 6 && phrase.contains(normalized)) {
                    score += 4;
                    break;
                }
            }
        }
        if (entry.modelIndexed) score += 2;
        if (count > 1) score += Math.min(6, (count - 1) * 2);
        return score;
    }

    private static List<String> significantWords(String sentence) {
        List<String> words = new ArrayList<>();
        for (String raw : normalize(sentence).split(" ")) {
            if (raw.isEmpty() || raw.length() < 3 || STOP.contains(raw)) continue;
            // Short Greek/English morphological variations (φορτίζει/φόρτιση,
            // κολλάει/κόλλησε) map to a stable lexical prefix. No diagnosis.
            String word = raw.length() >= 6 ? raw.substring(0, 4) : raw;
            if (!words.contains(word)) words.add(word);
        }
        return words;
    }

    private static String normalize(String input) {
        String a = Normalizer.normalize(input.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").replace('ς', 'σ');
        return a.replaceAll("[^\\p{L}\\p{N}]+", " ").trim().replaceAll("\\s+", " ");
    }
}
