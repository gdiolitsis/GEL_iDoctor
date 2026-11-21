package com.gel.cleaner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Offline index of actual Apple support documentation.
 *
 * These are APPLE SYMPTOM-BASED procedures and exact-model repair manuals,
 * NOT Apple mappings of panic codes to failed parts, and NOT repair outcomes.
 * No panic-log content is uploaded or transmitted to Apple.
 */
public final class AppleOfficialServiceGuidance {
    public static final String ASSET = "iphone_apple_official_guidance_v1.tsv";

    private AppleOfficialServiceGuidance() {}

    public static final class Guidance {
        public final String id;
        public final String models;
        public final String category;
        public final String titleGreek, titleEnglish;
        public final String guidanceGreek, guidanceEnglish;
        public final String sourceUrl;
        public final String publishedDate;
        public final String sourceKind;

        Guidance(String[] c) {
            id = c[0];
            models = c[1];
            category = c[2];
            titleGreek = c[3];
            titleEnglish = c[4];
            guidanceGreek = c[5];
            guidanceEnglish = c[6];
            sourceUrl = c[7];
            publishedDate = c[8];
            sourceKind = c[9];
        }

        public String title(boolean gr) { return gr ? titleGreek : titleEnglish; }
        public String advice(boolean gr) { return gr ? guidanceGreek : guidanceEnglish; }

        boolean applies(String model) {
            if (model == null || model.trim().isEmpty()
                    || "unknown".equalsIgnoreCase(model.trim())) return false;
            if ("-".equals(models)) return false; // directory-only: identifier not verified
            if ("*".equals(models)) return true; // generic Apple symptom guides
            for (String identifier : models.split(";")) {
                if (identifier.trim().equalsIgnoreCase(model.trim())) return true;
            }
            return false;
        }
    }

    public static List<Guidance> parse(InputStream source) throws IOException {
        if (source == null) throw new IOException("Missing bundled Apple support index");
        List<Guidance> rows = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(source, StandardCharsets.UTF_8))) {
            String line;
            int number = 0;
            while ((line = reader.readLine()) != null) {
                number++;
                if (line.trim().isEmpty() || line.startsWith("#")
                        || line.startsWith("id\tmodels\t")) continue;
                String[] c = line.split("\t", -1);
                if (c.length != 10) throw new IOException("Invalid Apple source fields at " + number);
                for (int i : new int[]{0,1,2,3,4,5,6,7,9}) {
                    if (c[i].trim().isEmpty())
                        throw new IOException("Incomplete Apple source entry at " + number);
                }
                if (!c[7].startsWith("https://support.apple.com/"))
                    throw new IOException("Not an official Apple support URL at " + number);
                if (!ids.add(c[0])) throw new IOException("Duplicate Apple source id " + c[0]);
                if (c[2].equals("MODEL_MANUAL") && "*".equals(c[1])) {
                    throw new IOException("Model manual must use exact hardware identifiers");
                }
                rows.add(new Guidance(c));
            }
        }
        return Collections.unmodifiableList(rows);
    }

    /** Exact-model manual only. Never attach a manual from a neighboring model. */
    public static Guidance modelManual(List<Guidance> all, String model) {
        if (all == null) return null;
        for (Guidance row : all) {
            if ("MODEL_MANUAL".equals(row.category) && row.applies(model)) return row;
        }
        return null;
    }

    /**
     * May return a GENERIC symptom guide for known model IDs, or a
     * scope-restricted diagnostics / post-repair guide. Never a panic-code mapping.
     */
    public static Guidance forTopic(List<Guidance> all, String model, String category) {
        if (all == null || category == null) return null;
        for (Guidance row : all) {
            if (row.category.equals(category.toUpperCase(Locale.ROOT))
                    && row.applies(model)) return row;
        }
        return null;
    }

    /**
     * Wildcard-only official symptom guidance. May be displayed even when an
     * imported panic log has no verified model identifier, but NEVER treated
     * as a code-to-part or exact-model repair recommendation.
     */
    public static Guidance genericForTopic(List<Guidance> all, String category) {
        if (all == null || category == null) return null;
        for (Guidance row : all) {
            if ("*".equals(row.models)
                    && row.category.equals(category.toUpperCase(Locale.ROOT))) return row;
        }
        return null;
    }

    /** All generic official pages for one documented symptom topic, in catalog order. */
    public static List<Guidance> genericForTopicAll(List<Guidance> all, String category) {
        if (all == null || category == null) return Collections.emptyList();
        List<Guidance> matching = new ArrayList<>();
        for (Guidance row : all) {
            if ("*".equals(row.models)
                    && category.equalsIgnoreCase(row.category)) matching.add(row);
        }
        return Collections.unmodifiableList(matching);
    }

    /**
     * Conditionally relevant Apple documentation based on literal GEL clue
     * categories. This is NOT an Apple interpretation of an SMC/panic code,
     * and does NOT assert that the end-user actually experienced the symptom.
     */
    public static List<String> conditionalTopicsForClue(String code) {
        if (code == null) return Collections.emptyList();
        String k = code.trim().toUpperCase(Locale.ROOT);
        List<String> topics = new ArrayList<>();
        if (k.equals("MIC1") || k.equals("MIC2") || k.startsWith("AUDIO_CODEC_")) {
            topics.add("MICROPHONE");
            if (k.startsWith("AUDIO_CODEC_")) topics.add("SPEAKER");
        } else if (k.equals("THERMAL_SHUTDOWN")) {
            topics.add("THERMAL");
        } else if (k.equals("BASEBAND_FAULT")) {
            topics.add("CELLULAR_SERVICE");
            topics.add("CELLULAR_CALLS");
        } else if (k.equals("DISPLAY_CONTROLLER")) {
            topics.add("DISPLAY_TOUCH");
        } else if (k.equals("STORAGE_CONTROLLER")) {
            topics.add("SOFTWARE_RECOVERY");
        }
        return Collections.unmodifiableList(topics);
    }

    /**
     * A suggested topic is editorial triage, not an assertion the user has
     * that symptom or Apple's interpretation of the imported panic code.
     */
    public static boolean topicMightBeUsefulForAction(String actionKey, String topic) {
        if (actionKey == null || topic == null) return false;
        switch (topic) {
            case "WIRED_CHARGING":
                return actionKey.equals("REPLACE_CHARGE_PORT_FLEX")
                        || actionKey.equals("RESEAT_CHARGE_PORT_FLEX");
            case "WIRELESS_CHARGING":
                return actionKey.equals("REPLACE_WIRELESS_CHARGING_FLEX");
            case "BATTERY_POWER":
                return actionKey.equals("REPLACE_BATTERY");
            default:
                return false;
        }
    }
}
