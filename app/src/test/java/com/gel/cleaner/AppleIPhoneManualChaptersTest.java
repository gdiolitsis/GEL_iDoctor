package com.gel.cleaner;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import static org.junit.Assert.*;

/** Prevent invalid Apple model guidance or panic-code-to-replacement claims. */
public final class AppleIPhoneManualChaptersTest {
    private static Path asset(String file) {
        Path full = Paths.get("app/src/main/assets/" + file);
        return Files.isRegularFile(full) ? full : Paths.get("src/main/assets/" + file);
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

    private static boolean includes(List<AppleIPhoneManualChapters.Chapter> list,
            AppleIPhoneManualChapters.Phase phase, String name) {
        for (AppleIPhoneManualChapters.Chapter row : list) {
            if (row.phase == phase && row.titleEnglish.equals(name)) return true;
        }
        return false;
    }

    @Test public void everyOfficialIphoneRepairManualHasSourceLinkedRealChapters() throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> docs = official();
        List<AppleIPhoneManualChapters.Chapter> items = chapters();
        assertTrue("Preserve original 983 headings and newly verified subchapters", items.size() >= 983);
        int proc = 0;
        for (AppleIPhoneManualChapters.Chapter row : items) {
            assertTrue(row.sourceUrl.startsWith("https://support.apple.com/"));
            assertTrue("No full copyrighted manual text is embedded", row.titleEnglish.length() < 96);
            assertFalse(row.titleGreek.trim().isEmpty());
            assertFalse("Manufacturer repair recommendation may not be inferred from panic code",
                    row.titleEnglish.contains("GEL PANIC"));
            if (row.phase == AppleIPhoneManualChapters.Phase.PROCEDURE) proc++;
        }
        assertEquals(407, proc);
        int count = 0;
        for (AppleOfficialServiceGuidance.Guidance manual : docs) {
            if (!"MODEL_MANUAL".equals(manual.category)
                    && !"MODEL_MANUAL_DIRECTORY".equals(manual.category)) continue;
            count++;
            List<AppleIPhoneManualChapters.Chapter> one =
                    AppleIPhoneManualChapters.forManualId(items, manual.id);
            assertFalse("Missing chapter inventory for " + manual.id, one.isEmpty());
            assertTrue(includes(one, AppleIPhoneManualChapters.Phase.SAFETY, "Battery Safety"));
            assertTrue(includes(one, AppleIPhoneManualChapters.Phase.SAFETY, "Broken Glass Safety"));
            assertTrue(includes(one, AppleIPhoneManualChapters.Phase.CHECK, "Quick Checks"));
            assertTrue(includes(one, AppleIPhoneManualChapters.Phase.PROCEDURE, "First Steps"));
            assertTrue(one.stream().anyMatch(x ->
                    x.phase == AppleIPhoneManualChapters.Phase.TROUBLESHOOT));
            assertTrue(one.stream().anyMatch(x ->
                    x.phase == AppleIPhoneManualChapters.Phase.PROCEDURE));
        }
        assertEquals("All thirty manufacturer model manuals", 30, count);
    }

    @Test public void noNeighborModelManualOrDirectoryEntryBecomesAnExactMatch() throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> docs = official();
        List<AppleIPhoneManualChapters.Chapter> items = chapters();
        List<AppleIPhoneManualChapters.Chapter> iphone14pro =
                AppleIPhoneManualChapters.forExactModel(items, docs, "iPhone15,2");
        assertFalse(iphone14pro.isEmpty());
        assertTrue(iphone14pro.stream().allMatch(x -> x.manualId.equals("MANUAL_14_PRO")));
        assertTrue(includes(iphone14pro, AppleIPhoneManualChapters.Phase.PROCEDURE, "Battery"));
        assertFalse(includes(iphone14pro, AppleIPhoneManualChapters.Phase.PROCEDURE, "USB-C Connector"));
        assertTrue(AppleIPhoneManualChapters.forExactModel(items, docs, "iPhone99,9").isEmpty());
        assertTrue(AppleIPhoneManualChapters.forExactModel(items, docs, "Unknown").isEmpty());
        assertTrue(AppleIPhoneManualChapters.forExactModel(items, docs, "-").isEmpty());
        assertTrue(AppleIPhoneManualChapters.forManualId(items, "DIRECTORY_DUO").size() > 30);
        assertTrue("Unknown model must never get Duo-specific procedures",
                AppleIPhoneManualChapters.forExactModel(items, docs, "iPhone99,9").isEmpty());
    }

    @Test public void specificDeviceVariationsAreNotCopiedToOtherIphones() throws Exception {
        List<AppleIPhoneManualChapters.Chapter> items = chapters();
        List<AppleIPhoneManualChapters.Chapter> duo =
                AppleIPhoneManualChapters.forManualId(items, "DIRECTORY_DUO");
        assertTrue(includes(duo, AppleIPhoneManualChapters.Phase.PROCEDURE, "Battery 1"));
        assertTrue(includes(duo, AppleIPhoneManualChapters.Phase.PROCEDURE, "Battery 2"));
        assertTrue(includes(duo, AppleIPhoneManualChapters.Phase.TROUBLESHOOT, "Hinge Issues"));
        assertTrue(includes(duo, AppleIPhoneManualChapters.Phase.PROCEDURE, "Inner Display"));
        assertTrue(includes(duo, AppleIPhoneManualChapters.Phase.PROCEDURE, "Outer Display"));

        List<AppleIPhoneManualChapters.Chapter> apple18 =
                AppleIPhoneManualChapters.forManualId(items, "DIRECTORY_18_PRO");
        assertTrue(includes(apple18, AppleIPhoneManualChapters.Phase.PROCEDURE,
                "Main Microphone (pSIM)"));
        assertTrue(includes(apple18, AppleIPhoneManualChapters.Phase.PROCEDURE,
                "Main Microphone (eSIM)"));
        assertFalse(includes(apple18, AppleIPhoneManualChapters.Phase.PROCEDURE, "Battery 2"));
        List<AppleIPhoneManualChapters.Chapter> apple12 =
                AppleIPhoneManualChapters.forManualId(items, "MANUAL_12");
        assertFalse(includes(apple12, AppleIPhoneManualChapters.Phase.PROCEDURE,
                "USB-C Connector"));
        assertFalse(includes(apple12, AppleIPhoneManualChapters.Phase.PROCEDURE,
                "Main Microphone"));
    }

    @Test public void symptomLookupReturnsTroubleshootingNotReplacementProcedures() throws Exception {
        List<AppleIPhoneManualChapters.Chapter> items =
                AppleIPhoneManualChapters.forManualId(chapters(), "MANUAL_14_PRO");
        List<AppleIPhoneManualChapters.Chapter> conditional =
                AppleIPhoneManualChapters.troubleshootingForTopics(items,
                        new HashSet<>(Arrays.asList("MICROPHONE", "DISPLAY_TOUCH")));
        assertTrue(includes(conditional, AppleIPhoneManualChapters.Phase.TROUBLESHOOT,
                "Speaker, ringer sound, or microphone issues"));
        assertTrue(includes(conditional, AppleIPhoneManualChapters.Phase.TROUBLESHOOT,
                "Display and image issues"));
        assertTrue(includes(conditional, AppleIPhoneManualChapters.Phase.TROUBLESHOOT,
                "Unexpected freeze, restart, or shutdown issues"));
        assertTrue(conditional.stream().allMatch(x ->
                x.phase == AppleIPhoneManualChapters.Phase.TROUBLESHOOT));
        assertFalse(includes(conditional, AppleIPhoneManualChapters.Phase.PROCEDURE, "Battery"));
    }

    private static InputStream literal(String row) {
        return new ByteArrayInputStream(row.getBytes(StandardCharsets.UTF_8));
    }

    @Test(expected=java.io.IOException.class)
    public void unknownManualIdentifierIsRejected() throws Exception {
        AppleIPhoneManualChapters.parse(literal(
                "FAKE\tNO_MANUAL\tPROCEDURE\tBATTERY\tΜπαταρία\tBattery\t"
                + "https://support.apple.com/en-us/100370\tMANUAL_CONTENTS\n"), official());
    }

    @Test(expected=java.io.IOException.class)
    public void sourceCannotBeReassignedToDifferentIphoneManual() throws Exception {
        AppleIPhoneManualChapters.parse(literal(
                "WRONG\tMANUAL_14_PRO\tPROCEDURE\tBATTERY\tΜπαταρία\tBattery\t"
                + "https://support.apple.com/en-us/100308\tMANUAL_CONTENTS\n"), official());
    }

    @Test(expected=java.io.IOException.class)
    public void rejectUnofficialProcedureLink() throws Exception {
        AppleIPhoneManualChapters.parse(literal(
                "BAD\tMANUAL_14_PRO\tPROCEDURE\tBATTERY\tΜπαταρία\tBattery\t"
                + "https://example.org/fix\tDIRECT_CHAPTER\n"), official());
    }
}
