package com.gel.cleaner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Human-selected iPhone model -> Apple-authored repair manual and indexed
 * troubleshooting articles. No model inference from unrelated panic logs and
 * no invented Apple diagnosis or model-specific deep link.
 */
public final class AppleTroubleshootingLibrary {
    private AppleTroubleshootingLibrary() {}

    public static final class TopicLink {
        public final String topic;
        public final String greek;
        public final String english;
        public final String url;
        public final boolean modelIndexed;

        TopicLink(String topic, String greek, String english, String url, boolean modelIndexed) {
            this.topic = topic;
            this.greek = greek;
            this.english = english;
            this.url = url;
            this.modelIndexed = modelIndexed;
        }

        public String label(boolean greekLanguage) {
            String title = greekLanguage ? greek : english;
            if (modelIndexed) return title;
            return title + (greekLanguage ? " — γενικός οδηγός Apple"
                                          : " — general Apple guide");
        }
    }

    public static boolean isOfficialUrl(String url) {
        if (url == null || url.contains("\\") || url.matches("(?s).*\\s+.*"))
            return false;
        try {
            java.net.URI uri = new java.net.URI(url);
            return "https".equalsIgnoreCase(uri.getScheme())
                    && "support.apple.com".equalsIgnoreCase(uri.getHost())
                    && uri.getUserInfo() == null
                    && uri.getPort() == -1;
        } catch (java.net.URISyntaxException exception) {
            return false;
        }
    }

    /**
     * The user's explicit commercial-model selection is matched to the exact
     * English title of an indexed repair manual. A directory-only manual may
     * be opened for reading, but is never treated as a panic-log model match.
     */
    public static AppleOfficialServiceGuidance.Guidance manualForSelection(
            List<AppleOfficialServiceGuidance.Guidance> guides,
            String family, String selectedModel) {
        if (guides == null || !"iphone".equalsIgnoreCase(family)
                || selectedModel == null || selectedModel.trim().isEmpty()) return null;

        String selected = selectedModel.trim().replaceAll("\\s+", " ");
        String suffix = " Repair Manual";
        for (AppleOfficialServiceGuidance.Guidance guide : guides) {
            if (!"MODEL_MANUAL".equals(guide.category)
                    && !"MODEL_MANUAL_DIRECTORY".equals(guide.category)) continue;
            String title = guide.titleEnglish;
            if (title == null || !title.endsWith(suffix)) continue;
            String modelInTitle = title.substring(0, title.length() - suffix.length());
            if (modelInTitle.equalsIgnoreCase(selected) && isOfficialUrl(guide.sourceUrl))
                return guide;
        }
        return null;
    }

    /**
     * Model-indexed direct chapter references come first. Missing model
     * chapters are not copied from a neighboring model. The two commonly
     * requested symptoms absent from some model manuals use explicitly marked
     * generic Apple support articles from the verified source catalogue.
     */
    public static List<TopicLink> topics(
            AppleOfficialServiceGuidance.Guidance selectedManual,
            List<AppleIPhoneManualChapters.Chapter> chapters,
            List<AppleOfficialServiceGuidance.Guidance> guides) {
        if (selectedManual == null) return Collections.emptyList();

        List<TopicLink> output = new ArrayList<>();
        if (chapters != null) for (AppleIPhoneManualChapters.Chapter chapter : chapters) {
            if (!selectedManual.id.equals(chapter.manualId)
                    || chapter.phase != AppleIPhoneManualChapters.Phase.TROUBLESHOOT
                    || chapter.linkScope != AppleIPhoneManualChapters.LinkScope.DIRECT_CHAPTER
                    || !isOfficialUrl(chapter.sourceUrl)) continue;

            String gr = chapter.titleGreek, en = chapter.titleEnglish;
            if ("RESTART".equals(chapter.topic)) {
                // Watchdog is a GEL classification; Apple documents the symptom.
                gr = "Πάγωμα / απρόσμενη επανεκκίνηση (Watchdog)";
                en = "Freeze / unexpected restart (Watchdog)";
            }
            output.add(new TopicLink(chapter.topic, gr, en, chapter.sourceUrl, true));
        }

        // These links are not represented as model-specific chapters.
        addGenericTopic(output, guides, "THERMAL",
                "Υπερθέρμανση / θερμοκρασία", "Overheating / temperature");
        addGenericTopic(output, guides, "WIFI",
                "Αποσυνδέσεις Wi-Fi / ίντερνετ", "Wi-Fi / internet disconnections");
        return Collections.unmodifiableList(output);
    }

    private static void addGenericTopic(List<TopicLink> result,
            List<AppleOfficialServiceGuidance.Guidance> guides, String category,
            String greek, String english) {
        if (guides == null) return;
        for (AppleOfficialServiceGuidance.Guidance guide : guides) {
            if (category.equals(guide.category) && "*".equals(guide.models)
                    && isOfficialUrl(guide.sourceUrl)) {
                result.add(new TopicLink(category, greek, english, guide.sourceUrl, false));
                return;
            }
        }
    }
}
