package com.gel.cleaner;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.*;

/** Exact chapter sources and audited Apple step summaries, never auto repairs. */
public final class AppleIPhoneServiceStepsTest {
    private static Path asset(String name) {
        Path p = Paths.get("app/src/main/assets/" + name);
        return Files.isRegularFile(p) ? p : Paths.get("src/main/assets/" + name);
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
    private static List<AppleIPhoneServiceSteps.Step> audited() throws Exception {
        try (InputStream in = Files.newInputStream(asset(AppleIPhoneServiceSteps.ASSET))) {
            return AppleIPhoneServiceSteps.parse(in, chapters());
        }
    }
    private static ByteArrayInputStream raw(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }

    @Test public void auditedSummariesIncludeRealSourceAndDistinctStages() throws Exception {
        List<AppleIPhoneServiceSteps.Step> steps = audited();
        assertEquals(56, steps.size());
        Set<String> chapters = new HashSet<>();
        for (AppleIPhoneServiceSteps.Step s : steps) {
            chapters.add(s.chapterId);
            assertTrue(s.sourceUrl.startsWith("https://support.apple.com/"));
            assertEquals("2026-11-21", s.reviewedOn);
            assertFalse(s.greek.trim().isEmpty());
            assertFalse(s.english.trim().isEmpty());
            assertFalse("Step outlines are not replace-part recommendations", 
                    s.english.contains("Apple-certified panic repair"));
        }
        assertEquals(9, chapters.size());
        assertEquals(6, AppleIPhoneServiceSteps.forChapter(steps,
                "DIRECTORY_15_PRO_MAX_0689").size());
        assertEquals(6, AppleIPhoneServiceSteps.forChapter(steps,
                "DIRECTORY_15_PRO_MAX_0691").size());
        assertEquals(6, AppleIPhoneServiceSteps.forChapter(steps,
                "MANUAL_14_PRO_0144").size());
    }

    @Test public void exact14ProFindsOnlyItsOwnReviewedGuides() throws Exception {
        List<AppleIPhoneManualChapters.Chapter> reviewed =
                AppleIPhoneServiceSteps.reviewedForExactModel(
                        audited(), chapters(), official(), "iPhone15,2");
        assertEquals(4, reviewed.size());
        for (AppleIPhoneManualChapters.Chapter ch : reviewed)
            assertEquals("MANUAL_14_PRO", ch.manualId);
        assertTrue(AppleIPhoneServiceSteps.reviewedForExactModel(
                audited(), chapters(), official(), "Unknown").isEmpty());
        assertTrue(AppleIPhoneServiceSteps.reviewedForExactModel(
                audited(), chapters(), official(), "iPhone99,9").isEmpty());
        // Directory-only iPhone 15 Pro Max must not be guessed from its neighbor's ID.
        assertTrue(AppleIPhoneServiceSteps.reviewedForExactModel(
                audited(), chapters(), official(), "iPhone16,2").isEmpty());
    }

    @Test public void infraredLaserAndRepairAssistantRestrictionsSurviveTranslation() throws Exception {
        List<AppleIPhoneServiceSteps.Step> camera =
                AppleIPhoneServiceSteps.forChapter(audited(), "DIRECTORY_15_PRO_MAX_0691");
        assertEquals(6, camera.size());
        String english = "";
        for (AppleIPhoneServiceSteps.Step s : camera) english += " " + s.english;
        assertTrue(english.contains("infrared laser"));
        assertTrue(english.contains("requires Repair Assistant"));
        List<AppleIPhoneServiceSteps.Step> diagnostics =
                AppleIPhoneServiceSteps.forChapter(audited(), "DIRECTORY_15_PRO_MAX_0668");
        String diagText = "";
        for (AppleIPhoneServiceSteps.Step s : diagnostics) diagText += " " + s.english;
        assertTrue(diagText.contains("iOS 18+"));
        assertTrue(diagText.contains("iOS 17+"));
        assertTrue(diagText.contains("never into GEL iDoctor"));
    }

    @Test public void allDirectSourcesMustBeModelCorrect() throws Exception {
        List<AppleIPhoneManualChapters.Chapter> all = chapters();
        int direct = 0;
        for (AppleIPhoneManualChapters.Chapter chapter : all) {
            if (chapter.linkScope == AppleIPhoneManualChapters.LinkScope.DIRECT_CHAPTER) direct++;
        }
        assertEquals(93, direct);
        assertEquals("https://support.apple.com/en-us/100381",
                all.stream().filter(x -> x.manualId.equals("MANUAL_14_PRO")
                        && x.titleEnglish.equals("Battery")).findFirst().get().sourceUrl);
        assertEquals("https://support.apple.com/en-us/104929",
                all.stream().filter(x -> x.manualId.equals("DIRECTORY_15_PRO_MAX")
                        && x.titleEnglish.equals("Back Glass")).findFirst().get().sourceUrl);
        assertEquals("https://support.apple.com/en-us/123044",
                all.stream().filter(x -> x.manualId.equals("DIRECTORY_17")
                        && x.titleEnglish.equals("Battery")).findFirst().get().sourceUrl);
    }

    @Test(expected=java.io.IOException.class)
    public void wrongSourceArticleForRealChapterMustFail() throws Exception {
        AppleIPhoneServiceSteps.parse(raw(
                "FAKE\tMANUAL_14_PRO_0144\t1\tSCOPE\tΠεδίο\tScope\t"
                + "https://support.apple.com/en-us/104929\t2026-11-21\n"), chapters());
    }
    @Test(expected=java.io.IOException.class)
    public void cannotAttachStepsToUnlinkedManualContents() throws Exception {
        AppleIPhoneServiceSteps.parse(raw(
                "FAKE\tDIRECTORY_DUO_0950\t1\tSCOPE\tΠεδίο\tScope\t"
                + "https://support.apple.com/en-gb/101538\t2026-11-21\n"), chapters());
    }
    @Test(expected=java.io.IOException.class)
    public void cannotSkipNumberedStages() throws Exception {
        AppleIPhoneServiceSteps.parse(raw(
                "FAKE\tMANUAL_14_PRO_0144\t3\tSCOPE\tΠεδίο\tScope\t"
                + "https://support.apple.com/en-us/100375\t2026-11-21\n"), chapters());
    }
    @Test(expected=java.io.IOException.class)
    public void incompleteStepSequenceIsRejected() throws Exception {
        AppleIPhoneServiceSteps.parse(raw(
                "FAKE\tMANUAL_14_PRO_0144\t1\tSCOPE\tΠεδίο\tScope\t"
                + "https://support.apple.com/en-us/100375\t2026-11-21\n"), chapters());
    }
}
