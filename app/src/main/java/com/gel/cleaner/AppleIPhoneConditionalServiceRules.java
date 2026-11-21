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
 * A separate ledger of strictly conditional Apple model/service paths.
 *
 * This is intentionally NOT read by Lab 4 as evidence from imported panic logs.
 * It describes the manufacturer action ONLY if a specific Apple diagnostic
 * really failed, or an observed problem persisted after Apple's required checks.
 * It may be browsed as conditional reference material, never an automatic verdict.
 */
public final class AppleIPhoneConditionalServiceRules {
    public static final String ASSET = "iphone_apple_conditional_service_rules_v1.tsv";

    public enum Test {
        MRI_AMBIENT_LIGHT, MRI_COMPASS, MRI_BAROMETER,
        AST2_RECEIVER, AST2_SPEAKER, AST2_BOTTOM_MIC_LEFT,
        AST2_BOTTOM_MIC_RIGHT, AST2_FRONT_MIC, AST2_REAR_MIC,
        BUTTONS_AFTER_CHECKS, TEMPERATURE_ALERT_AFTER_CHECKS
    }

    public enum Result { FAILED, PERSISTENT_AFTER_CHECKS }

    public enum ServicePart {
        DISPLAY, DISPLAY_ASSEMBLY, MAIN_MICROPHONE, WHOLE_UNIT,
        TOP_SPEAKER, BOTTOM_SPEAKER, ENCLOSURE, BACK_GLASS
    }

    public static final class Rule {
        public final String id;
        public final String manualId;
        public final String hardwareId;
        public final String topic;
        public final Test test;
        public final Result requiredResult;
        public final ServicePart part;
        public final String sourceUrl;
        public final String greek;
        public final String english;
        public final String reviewedOn;

        private Rule(String[] c) {
            id = c[0];
            manualId = c[1];
            hardwareId = c[2];
            topic = c[3];
            test = Test.valueOf(c[4]);
            requiredResult = Result.valueOf(c[5]);
            part = ServicePart.valueOf(c[6]);
            sourceUrl = c[7];
            greek = c[8];
            english = c[9];
            reviewedOn = c[10];
        }

        public String explanation(boolean greekLanguage) {
            return greekLanguage ? greek : english;
        }
    }

    private AppleIPhoneConditionalServiceRules() {}

    private static String key(String manualId, String hardwareId, String topic,
            Test test, Result result) {
        return manualId + "\u001e" + hardwareId + "\u001e" + topic + "\u001e"
                + test.name() + "\u001e" + result.name();
    }

    public static List<Rule> parse(InputStream source,
            List<AppleOfficialServiceGuidance.Guidance> guides,
            List<AppleIPhoneManualChapters.Chapter> chapters) throws IOException {
        if (source == null || guides == null || chapters == null)
            throw new IOException("Missing Apple conditional service rule inputs");

        Map<String, AppleOfficialServiceGuidance.Guidance> verified = new HashMap<>();
        for (AppleOfficialServiceGuidance.Guidance doc : guides) {
            if ("MODEL_MANUAL".equals(doc.category))
                verified.put(doc.id, doc);
        }
        Set<String> chapterScopes = new HashSet<>();
        for (AppleIPhoneManualChapters.Chapter chapter : chapters) {
            if (chapter.phase == AppleIPhoneManualChapters.Phase.TROUBLESHOOT
                    && chapter.linkScope == AppleIPhoneManualChapters.LinkScope.DIRECT_CHAPTER)
                chapterScopes.add(chapter.manualId + "\u001e" + chapter.topic
                        + "\u001e" + chapter.sourceUrl);
        }

        List<Rule> items = new ArrayList<>();
        Set<String> ids = new HashSet<>(), uniqueOutcomes = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                source, StandardCharsets.UTF_8))) {
            String line;
            int number = 0;
            while ((line = reader.readLine()) != null) {
                number++;
                if (line.trim().isEmpty() || line.startsWith("#")
                        || line.startsWith("rule_id\t")) continue;
                String[] fields = line.split("\t", -1);
                if (fields.length != 11)
                    throw new IOException("Invalid Apple conditional rule column count");
                for (String field : fields)
                    if (field.trim().isEmpty())
                        throw new IOException("Incomplete Apple conditional rule at " + number);
                Rule rule;
                try {
                    rule = new Rule(fields);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Unknown Apple test/part/result at " + number, e);
                }
                if (!ids.add(rule.id))
                    throw new IOException("Duplicate Apple rule id: " + rule.id);
                AppleOfficialServiceGuidance.Guidance model = verified.get(rule.manualId);
                if (model == null || !model.appliesTo(rule.hardwareId))
                    throw new IOException("Model is not an exact manufacturer hardware match");
                if (!rule.sourceUrl.matches(
                        "https://support\\.apple\\.com/[a-z]{2}(?:-[a-z]{2})?/[0-9]{5,7}"))
                    throw new IOException("Conditional rule source is not Apple");
                if (!chapterScopes.contains(rule.manualId + "\u001e" + rule.topic
                        + "\u001e" + rule.sourceUrl))
                    throw new IOException("Wrong article or symptom topic for model");
                if (!rule.reviewedOn.matches("\\d{4}-\\d{2}-\\d{2}"))
                    throw new IOException("Missing source review date");
                if ((rule.test == Test.BUTTONS_AFTER_CHECKS
                        || rule.test == Test.TEMPERATURE_ALERT_AFTER_CHECKS)
                        != (rule.requiredResult == Result.PERSISTENT_AFTER_CHECKS))
                    throw new IOException("Incorrect test-versus-persistent symptom condition");
                if (!uniqueOutcomes.add(key(rule.manualId, rule.hardwareId, rule.topic,
                        rule.test, rule.requiredResult)))
                    throw new IOException("Contradictory manufacturer result for same model/test");
                items.add(rule);
            }
        }
        return Collections.unmodifiableList(items);
    }

    /**
     * The caller must establish an independently documented, actual Apple test
     * result; a GEL panic log NEVER counts as having run the Apple test.
     * For persistent conditions, "actuallyRun" means the actual checklist was
     * completed and persistence observed, not that a hardware test was run.
     */
    public static List<Rule> forVerifiedObservedResult(List<Rule> rules,
            String exactHardwareIdentifier, Test test, Result observedResult,
            boolean externallyVerifiedResult) {
        if (!externallyVerifiedResult || rules == null || test == null
                || observedResult == null || exactHardwareIdentifier == null)
            return Collections.emptyList();
        List<Rule> matching = new ArrayList<>();
        for (Rule row : rules) {
            if (row.hardwareId.equals(exactHardwareIdentifier)
                    && row.test == test && row.requiredResult == observedResult)
                matching.add(row);
        }
        return Collections.unmodifiableList(matching);
    }
}
