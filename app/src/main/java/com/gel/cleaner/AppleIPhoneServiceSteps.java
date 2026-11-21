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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Curated SHORT stage summaries of individually read Apple repair chapters.
 * A chapter may have a real direct link but NO reviewed step sequence yet.
 * Never fill in missing steps from model similarity, code guesses, forum data,
 * or inferred procedure names. The Apple source itself is the full manual.
 */
public final class AppleIPhoneServiceSteps {
    public static final String ASSET = "iphone_apple_service_steps_v1.tsv";
    public enum Stage { SCOPE, SAFETY, PREPARE, TEST, REMOVAL, REASSEMBLY, VERIFY }

    public static final class Step {
        public final String id;
        public final String chapterId;
        public final int order;
        public final Stage stage;
        public final String greek;
        public final String english;
        public final String sourceUrl;
        public final String reviewedOn;

        private Step(String[] columns) {
            id = columns[0];
            chapterId = columns[1];
            order = Integer.parseInt(columns[2]);
            stage = Stage.valueOf(columns[3]);
            greek = columns[4];
            english = columns[5];
            sourceUrl = columns[6];
            reviewedOn = columns[7];
        }
        public String text(boolean isGreek) { return isGreek ? greek : english; }
    }

    private AppleIPhoneServiceSteps() {}

    public static List<Step> parse(InputStream source,
            List<AppleIPhoneManualChapters.Chapter> chapters) throws IOException {
        if (source == null || chapters == null)
            throw new IOException("Missing Apple service step source or chapter index");

        Map<String, AppleIPhoneManualChapters.Chapter> chapterById = new HashMap<>();
        for (AppleIPhoneManualChapters.Chapter chapter : chapters)
            chapterById.put(chapter.id, chapter);

        Set<String> seenIds = new HashSet<>();
        Map<String, List<Step>> groups = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                source, StandardCharsets.UTF_8))) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.trim().isEmpty() || line.startsWith("#")
                        || line.startsWith("step_id\t")) continue;
                String[] col = line.split("\t", -1);
                if (col.length != 8)
                    throw new IOException("Invalid Apple stage fields at line " + lineNo);
                for (String cell : col) {
                    if (cell.trim().isEmpty())
                        throw new IOException("Incomplete Apple stage at line " + lineNo);
                }
                if (!seenIds.add(col[0]))
                    throw new IOException("Duplicate Apple stage ID " + col[0]);
                final Step step;
                try { step = new Step(col); }
                catch (RuntimeException e) {
                    throw new IOException("Invalid Apple stage type/order " + lineNo, e);
                }
                AppleIPhoneManualChapters.Chapter chapter = chapterById.get(step.chapterId);
                if (chapter == null)
                    throw new IOException("Unknown Apple chapter " + step.chapterId);
                if (chapter.linkScope != AppleIPhoneManualChapters.LinkScope.DIRECT_CHAPTER)
                    throw new IOException("No direct chapter citation for Apple step");
                if (!step.sourceUrl.equals(chapter.sourceUrl)
                        || !step.sourceUrl.startsWith("https://support.apple.com/"))
                    throw new IOException("Wrong Apple chapter source for step " + step.id);
                if (chapter.phase != AppleIPhoneManualChapters.Phase.PROCEDURE
                        && chapter.phase != AppleIPhoneManualChapters.Phase.DIAGNOSTICS
                        && chapter.phase != AppleIPhoneManualChapters.Phase.TROUBLESHOOT)
                    throw new IOException("Step attached to non-procedure Apple contents");
                if (!step.reviewedOn.matches("\\d{4}-\\d{2}-\\d{2}"))
                    throw new IOException("Review date required for each step");
                List<Step> seq = groups.get(step.chapterId);
                if (seq == null) {
                    seq = new ArrayList<>();
                    groups.put(step.chapterId, seq);
                }
                if (step.order != seq.size() + 1)
                    throw new IOException("Non-contiguous or unordered Apple steps");
                seq.add(step);
            }
        }
        List<Step> output = new ArrayList<>();
        for (Map.Entry<String, List<Step>> entry : groups.entrySet()) {
            List<Step> seq = entry.getValue();
            AppleIPhoneManualChapters.Chapter chapter = chapterById.get(entry.getKey());
            if (seq.size() < 4 || seq.get(0).stage != Stage.SCOPE
                    || seq.get(seq.size() - 1).stage != Stage.VERIFY)
                throw new IOException("Incomplete official procedure stage outline");
            if (chapter.phase == AppleIPhoneManualChapters.Phase.PROCEDURE) {
                boolean safety = false, prep = false, removal = false, reassembly = false;
                for (Step step : seq) {
                    safety |= step.stage == Stage.SAFETY;
                    prep |= step.stage == Stage.PREPARE;
                    removal |= step.stage == Stage.REMOVAL;
                    reassembly |= step.stage == Stage.REASSEMBLY;
                }
                if (!(safety && prep && removal && reassembly))
                    throw new IOException("Physical chapter is missing essential stages");
            }
            output.addAll(seq);
        }
        return Collections.unmodifiableList(output);
    }

    public static List<Step> forChapter(List<Step> all, String chapterId) {
        if (all == null || chapterId == null) return Collections.emptyList();
        List<Step> out = new ArrayList<>();
        for (Step step : all) if (step.chapterId.equals(chapterId)) out.add(step);
        return Collections.unmodifiableList(out);
    }

    /** No guessing: only chapters in one verified hardware model's manual. */
    public static List<AppleIPhoneManualChapters.Chapter> reviewedForExactModel(
            List<Step> steps, List<AppleIPhoneManualChapters.Chapter> allChapters,
            List<AppleOfficialServiceGuidance.Guidance> official, String deviceIdentifier) {
        List<AppleIPhoneManualChapters.Chapter> chapters =
                AppleIPhoneManualChapters.forExactModel(
                        allChapters, official, deviceIdentifier);
        if (chapters.isEmpty() || steps == null) return Collections.emptyList();
        Set<String> withSteps = new HashSet<>();
        for (Step step : steps) withSteps.add(step.chapterId);
        List<AppleIPhoneManualChapters.Chapter> out = new ArrayList<>();
        for (AppleIPhoneManualChapters.Chapter chapter : chapters) {
            if (withSteps.contains(chapter.id)) out.add(chapter);
        }
        return Collections.unmodifiableList(out);
    }
}
