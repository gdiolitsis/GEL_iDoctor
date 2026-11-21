package com.gel.cleaner;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

/** Bilingual, source-reviewed Apple diagnostic notes must never cross model/topic. */
public final class AppleIPhoneTechnicalKnowledgeTest {
    private static Path asset(String file) {
        Path p = Paths.get("app/src/main/assets/" + file);
        return Files.isRegularFile(p) ? p : Paths.get("src/main/assets/" + file);
    }
    private static List<AppleOfficialServiceGuidance.Guidance> official() throws Exception {
        try (InputStream in = Files.newInputStream(asset(AppleOfficialServiceGuidance.ASSET))) {
            return AppleOfficialServiceGuidance.parse(in);
        }
    }
    private static List<AppleIPhoneManualChapters.Chapter> chapters() throws Exception {
        try (InputStream in = Files.newInputStream(asset(AppleIPhoneManualChapters.ASSET))) {
            return AppleIPhoneManualChapters.parse(in, official());
        }
    }
    private static List<AppleIPhoneTechnicalKnowledge.Finding> notes() throws Exception {
        try (InputStream in = Files.newInputStream(asset(AppleIPhoneTechnicalKnowledge.ASSET))) {
            return AppleIPhoneTechnicalKnowledge.parse(in, chapters());
        }
    }
    private static InputStream literal(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }

    @Test public void allReviewedNotesHaveRealMatchingAppleArticleAndRiskAndDate() throws Exception {
        List<AppleIPhoneTechnicalKnowledge.Finding> all = notes();
        assertEquals(108, all.size());
        assertEquals(517, AppleIPhoneTechnicalKnowledge.chapterCoverage(all, chapters()));
        Set<String> urls = new HashSet<>();
        for (AppleIPhoneTechnicalKnowledge.Finding fact : all) {
            assertTrue(fact.sourceUrl.startsWith("https://support.apple.com/en-us/"));
            assertTrue("Explicit source review date", fact.reviewedOn.equals("2026-11-21")
                    || fact.reviewedOn.equals("2026-11-22"));
            assertFalse(fact.greek.trim().isEmpty());
            assertFalse(fact.english.trim().isEmpty());
            urls.add(fact.sourceUrl);
        }
        assertEquals("Nine source-reviewed Apple articles, not all chapter texts", 9, urls.size());
        assertEquals("Chapter catalog coverage is separate from full procedure review", 1074,
                chapters().size());
    }

    @Test public void iphone13ProMaxGetsSafeRestartChecklistNotPartsRecommendation() throws Exception {
        List<AppleIPhoneTechnicalKnowledge.Finding> facts =
                AppleIPhoneTechnicalKnowledge.forExactModel(
                        notes(), chapters(), official(), "iPhone14,3",
                        AppleIPhoneManualChapters.Phase.TROUBLESHOOT, "RESTART");
        assertEquals(6, facts.size());
        assertTrue(facts.stream().allMatch(x ->
                x.sourceUrl.equals("https://support.apple.com/en-us/101969")));
        assertTrue(facts.stream().anyMatch(x -> x.kind
                == AppleIPhoneTechnicalKnowledge.Kind.DIAGNOSTIC));
        assertTrue(facts.stream().anyMatch(x -> x.risk
                == AppleIPhoneTechnicalKnowledge.Risk.DATA_LOSS));
        assertFalse(facts.stream().anyMatch(x -> x.english.contains("Replace the logic board")));
        assertTrue(AppleIPhoneTechnicalKnowledge.forExactModel(
                notes(), chapters(), official(), "iPhone14,3",
                AppleIPhoneManualChapters.Phase.TROUBLESHOOT, "WIRELESS_CHARGING").size() > 0);
    }

    @Test public void unverifiedAndDirectoryOnlyModelCannotBorrowNearestGuidance() throws Exception {
        assertTrue(AppleIPhoneTechnicalKnowledge.forExactModel(
                notes(), chapters(), official(), "iPhone99,9",
                AppleIPhoneManualChapters.Phase.TROUBLESHOOT, "RESTART").isEmpty());
        assertTrue(AppleIPhoneTechnicalKnowledge.forExactModel(
                notes(), chapters(), official(), "Unknown",
                AppleIPhoneManualChapters.Phase.TROUBLESHOOT, "BATTERY_POWER").isEmpty());
        assertTrue(AppleIPhoneTechnicalKnowledge.forExactModel(
                notes(), chapters(), official(), "iPhone16,2",
                AppleIPhoneManualChapters.Phase.PROCEDURE, "CAMERA").isEmpty());
        assertEquals(4, AppleIPhoneTechnicalKnowledge.forExactModel(
                notes(), chapters(), official(), "iPhone15,4",
                AppleIPhoneManualChapters.Phase.CHECK, "GENERAL").size());
    }

    @Test public void sameManufacturerArticleMustNotLeakNotesAcrossTopics() throws Exception {
        List<AppleIPhoneManualChapters.Chapter> all = chapters();
        AppleIPhoneManualChapters.Chapter display = null, battery = null;
        for (AppleIPhoneManualChapters.Chapter row : all) {
            if (row.manualId.equals("MANUAL_14_PRO")
                    && row.phase == AppleIPhoneManualChapters.Phase.TROUBLESHOOT
                    && row.topic.equals("DISPLAY")) display = row;
            if (row.manualId.equals("MANUAL_14_PRO")
                    && row.phase == AppleIPhoneManualChapters.Phase.TROUBLESHOOT
                    && row.topic.equals("RESTART")) battery = row;
        }
        assertNotNull(display);
        assertNotNull(battery);
        List<AppleIPhoneTechnicalKnowledge.Finding> displayFacts =
                AppleIPhoneTechnicalKnowledge.forChapter(notes(), display);
        List<AppleIPhoneTechnicalKnowledge.Finding> restartFacts =
                AppleIPhoneTechnicalKnowledge.forChapter(notes(), battery);
        assertEquals(8, displayFacts.size());
        assertEquals(6, restartFacts.size());
        assertTrue(displayFacts.stream().allMatch(f -> f.topic.equals("DISPLAY")
                && f.sourceUrl.equals("https://support.apple.com/en-us/101966")));
        assertTrue(restartFacts.stream().allMatch(f -> f.topic.equals("RESTART")
                && f.sourceUrl.equals("https://support.apple.com/en-us/101969")));
    }

    @Test public void threeNewOfficialArticlesAreBoundToRealObservedTopics() throws Exception {
        List<AppleIPhoneTechnicalKnowledge.Finding> all = notes();
        List<AppleOfficialServiceGuidance.Guidance> docs = official();
        List<AppleIPhoneManualChapters.Chapter> chapters = chapters();
        String model = "iPhone14,3"; // iPhone 13 Pro Max has a verified exact ID.
        for (String topic : new String[] {"DISPLAY", "TOUCH", "SENSORS"}) {
            List<AppleIPhoneTechnicalKnowledge.Finding> found =
                    AppleIPhoneTechnicalKnowledge.forExactModel(all, chapters, docs,
                            model, AppleIPhoneManualChapters.Phase.TROUBLESHOOT, topic);
            assertFalse("Apple display/touch/sensors source applies to " + topic, found.isEmpty());
            assertTrue(found.stream().anyMatch(f ->
                    f.sourceUrl.equals("https://support.apple.com/en-us/101966")));
        }
        List<AppleIPhoneTechnicalKnowledge.Finding> audio =
                AppleIPhoneTechnicalKnowledge.forExactModel(all, chapters, docs,
                        model, AppleIPhoneManualChapters.Phase.TROUBLESHOOT, "SOUND");
        assertEquals(9, audio.size());
        assertTrue(audio.stream().anyMatch(f -> f.english.contains("Audio diagnostic")));
        assertTrue(audio.stream().anyMatch(f ->
                f.risk == AppleIPhoneTechnicalKnowledge.Risk.TECHNICIAN_ONLY));
        List<AppleIPhoneTechnicalKnowledge.Finding> physicalSim =
                AppleIPhoneTechnicalKnowledge.forExactModel(all, chapters, docs,
                        model, AppleIPhoneManualChapters.Phase.TROUBLESHOOT, "SIM");
        assertEquals(5, physicalSim.size());
        assertTrue(physicalSim.stream().anyMatch(f ->
                f.english.contains("only to iPhones with a physical SIM tray")));
        List<AppleIPhoneTechnicalKnowledge.Finding> alerts =
                AppleIPhoneTechnicalKnowledge.forExactModel(all, chapters, docs,
                        model, AppleIPhoneManualChapters.Phase.TROUBLESHOOT, "ALERTS");
        assertEquals(5, alerts.size());
        assertTrue(alerts.stream().anyMatch(f -> f.english.contains("0–35")));
    }

    @Test public void appleDiagnosticNotesDoNotCrossUnrelatedTopicsOnSharedArticles() throws Exception {
        List<AppleIPhoneTechnicalKnowledge.Finding> all = notes();
        List<AppleIPhoneManualChapters.Chapter> chapters = chapters();
        AppleIPhoneManualChapters.Chapter button = null, sim = null, camera = null;
        for (AppleIPhoneManualChapters.Chapter ch : chapters) {
            if (!"MANUAL_14_PRO".equals(ch.manualId)) continue;
            if (ch.topic.equals("BUTTONS") && ch.phase
                    == AppleIPhoneManualChapters.Phase.TROUBLESHOOT) button = ch;
            if (ch.topic.equals("SIM") && ch.phase
                    == AppleIPhoneManualChapters.Phase.TROUBLESHOOT) sim = ch;
            if (ch.topic.equals("CAMERA") && ch.phase
                    == AppleIPhoneManualChapters.Phase.TROUBLESHOOT) camera = ch;
        }
        assertNotNull(button);
        assertNotNull(sim);
        assertNotNull(camera);
        assertEquals(button.sourceUrl, sim.sourceUrl);
        assertEquals(6, AppleIPhoneTechnicalKnowledge.forChapter(all, button).size());
        assertEquals(5, AppleIPhoneTechnicalKnowledge.forChapter(all, sim).size());
        assertEquals("Four source-linked camera checks have now been reviewed",
                4, AppleIPhoneTechnicalKnowledge.forChapter(all, camera).size());
    }

    @Test public void cameraAndFaceIdUseOnlyOfficialCameraArticleAndExactSymptoms() throws Exception {
        List<AppleIPhoneTechnicalKnowledge.Finding> camera =
                AppleIPhoneTechnicalKnowledge.forExactModel(notes(), chapters(), official(),
                        "iPhone14,3", AppleIPhoneManualChapters.Phase.TROUBLESHOOT, "CAMERA");
        List<AppleIPhoneTechnicalKnowledge.Finding> face =
                AppleIPhoneTechnicalKnowledge.forExactModel(notes(), chapters(), official(),
                        "iPhone14,3", AppleIPhoneManualChapters.Phase.TROUBLESHOOT, "FACE_ID");
        assertEquals(4, camera.size());
        assertEquals(4, face.size());
        assertTrue(camera.stream().allMatch(x ->
                x.sourceUrl.equals("https://support.apple.com/en-us/101967")));
        assertTrue(face.stream().allMatch(x ->
                x.sourceUrl.equals("https://support.apple.com/en-us/101967")));
        assertTrue(face.stream().anyMatch(x ->
                x.risk == AppleIPhoneTechnicalKnowledge.Risk.TECHNICIAN_ONLY));
        assertTrue(AppleIPhoneTechnicalKnowledge.forExactModel(notes(), chapters(), official(),
                "Unknown", AppleIPhoneManualChapters.Phase.TROUBLESHOOT, "FACE_ID").isEmpty());
    }

    @Test(expected=java.io.IOException.class)
    public void refuseFakeTopicEvenOnRealAppleURL() throws Exception {
        AppleIPhoneTechnicalKnowledge.parse(literal(
                "BAD1\thttps://support.apple.com/en-us/101969\tTROUBLESHOOT\tCAMERA\t"
                + "DIAGNOSTIC\tINFO\tΈλεγχος\tCheck\t2026-11-21\n"), chapters());
    }
    @Test(expected=java.io.IOException.class)
    public void refuseModelSpecificClaimOnWrongAppleArticle() throws Exception {
        AppleIPhoneTechnicalKnowledge.parse(literal(
                "BAD2\thttps://support.apple.com/en-us/104926\tPROCEDURE\tBATTERY\t"
                + "SAFETY\tTECHNICIAN_ONLY\tΈλεγχος\tCheck\t2026-11-21\n"), chapters());
    }
    @Test(expected=java.io.IOException.class)
    public void rejectUnofficialAppleSource() throws Exception {
        AppleIPhoneTechnicalKnowledge.parse(literal(
                "BAD3\thttps://example.com/101969\tTROUBLESHOOT\tRESTART\t"
                + "DIAGNOSTIC\tINFO\tΈλεγχος\tCheck\t2026-11-21\n"), chapters());
    }
    @Test(expected=java.io.IOException.class)
    public void rejectUnreviewedDuplicateEntry() throws Exception {
        String item = "DUP\thttps://support.apple.com/en-us/101964\tCHECK\tGENERAL\t"
                + "FIRST_CHECK\tINFO\tΈλεγχος\tCheck\t2026-11-21\n";
        AppleIPhoneTechnicalKnowledge.parse(literal(item + item), chapters());
    }
}
