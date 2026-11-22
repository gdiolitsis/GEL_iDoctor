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
import static org.junit.Assert.*;

public final class AppleIPhoneProcedureRequirementsTest {
    private static Path asset(String file) {
        Path p = Paths.get("app/src/main/assets/" + file);
        return Files.isRegularFile(p) ? p : Paths.get("src/main/assets/" + file);
    }
    private static List<AppleIPhoneManualChapters.Chapter> chapters() throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> guides;
        try (InputStream in = Files.newInputStream(asset(AppleOfficialServiceGuidance.ASSET))) {
            guides = AppleOfficialServiceGuidance.parse(in);
        }
        try (InputStream in = Files.newInputStream(asset(AppleIPhoneManualChapters.ASSET))) {
            return AppleIPhoneManualChapters.parse(in, guides);
        }
    }
    private static List<AppleIPhoneProcedureRequirements.Record> records() throws Exception {
        try (InputStream in = Files.newInputStream(asset(AppleIPhoneProcedureRequirements.ASSET))) {
            return AppleIPhoneProcedureRequirements.parse(in, chapters());
        }
    }
    private static AppleIPhoneProcedureRequirements.Record forUrl(String url) throws Exception {
        for (AppleIPhoneProcedureRequirements.Record record : records())
            if (record.sourceUrl.equals(url)) return record;
        return null;
    }

    @Test public void all315ProcedureSourcesAreVerifiedWithoutClaimingFullSteps() throws Exception {
        List<AppleIPhoneManualChapters.Chapter> all = chapters();
        List<AppleIPhoneProcedureRequirements.Record> metadata = records();
        assertEquals(315, metadata.size());
        int procedureCount = 0;
        HashSet<String> allSources = new HashSet<>();
        for (AppleIPhoneManualChapters.Chapter ch : all) {
            if (ch.phase != AppleIPhoneManualChapters.Phase.PROCEDURE) continue;
            procedureCount++;
            allSources.add(ch.sourceUrl);
            assertNotNull(AppleIPhoneProcedureRequirements.forChapter(metadata, ch));
        }
        assertEquals(407, procedureCount);
        assertEquals(allSources.size(), metadata.size());
    }

    @Test public void exactBatteryModelPrerequisitesAndCalibrationWordingMustDiffer() throws Exception {
        AppleIPhoneProcedureRequirements.Record pro14 =
                forUrl("https://support.apple.com/en-us/100381");
        AppleIPhoneProcedureRequirements.Record iphone15 =
                forUrl("https://support.apple.com/en-us/104907");
        AppleIPhoneProcedureRequirements.Record trueDepth =
                forUrl("https://support.apple.com/en-us/104926");
        AppleIPhoneProcedureRequirements.Record backGlass =
                forUrl("https://support.apple.com/en-us/104929");
        assertNotNull(pro14);
        assertNotNull(iphone15);
        assertNotNull(trueDepth);
        assertNotNull(backGlass);
        assertEquals(3, pro14.prerequisiteCount);
        assertTrue(pro14.prerequisiteNames.contains("Bottom speaker"));
        assertTrue(pro14.prerequisiteNames.contains("Taptic Engine"));
        assertFalse(pro14.prerequisiteNames.contains("Back glass"));
        assertEquals(2, iphone15.prerequisiteCount);
        assertTrue(iphone15.prerequisiteNames.contains("Back glass"));
        assertTrue(iphone15.prerequisiteNames.contains("Camera (Optional)"));
        assertEquals(2, trueDepth.prerequisiteCount);
        assertTrue(trueDepth.prerequisiteNames.contains("Top speaker"));
        assertEquals(AppleIPhoneProcedureRequirements.AssistantModality.RECOMMENDED_TEXT,
                pro14.assistant);
        assertEquals(AppleIPhoneProcedureRequirements.AssistantModality.RECOMMENDED_TEXT,
                iphone15.assistant);
        assertEquals(AppleIPhoneProcedureRequirements.AssistantModality.REQUIRED_TEXT,
                trueDepth.assistant);
        assertEquals(AppleIPhoneProcedureRequirements.AssistantModality.RECOMMENDED_TEXT,
                backGlass.assistant);
        // No extracted named prerequisites never means no prerequisites exist.
        assertEquals(0, backGlass.prerequisiteCount);
        assertTrue(backGlass.toolCount > 10);
    }

    @Test(expected = java.io.IOException.class)
    public void cannotSmuggleUnverifiedThirdPartyOrIncompleteSourceInventory() throws Exception {
        String fake = "https://not-apple.example.com/en-us/104907\t1\t1\t"
                + "SOURCE_METADATA_EXTRACTED\tA Fake Repair\t\t\t0\t0\tfalse\tfalse\tfalse\t"
                + "NOT_MENTIONED\tAUTOMATED_LABELS_ONLY_NOT_FULL_SERVICE_REVIEW\t2026-11-22\n";
        AppleIPhoneProcedureRequirements.parse(
                new ByteArrayInputStream(fake.getBytes(StandardCharsets.UTF_8)), chapters());
    }
}
