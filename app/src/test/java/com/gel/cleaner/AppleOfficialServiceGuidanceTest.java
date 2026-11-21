package com.gel.cleaner;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.Assert.*;

/** Independent official Apple support guidance, not panic code repair certification. */
public final class AppleOfficialServiceGuidanceTest {
    private static List<AppleOfficialServiceGuidance.Guidance> docs() throws Exception {
        Path file = Paths.get("app/src/main/assets/iphone_apple_official_guidance_v1.tsv");
        if (!Files.isRegularFile(file)) file = Paths.get("src/main/assets/iphone_apple_official_guidance_v1.tsv");
        try (InputStream stream = Files.newInputStream(file)) {
            return AppleOfficialServiceGuidance.parse(stream);
        }
    }

    @Test public void officialDocsAreSourcedAndNoApplePanicMappingIsClaimed() throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> entries = docs();
        assertTrue(entries.size() >= 17);
        for (AppleOfficialServiceGuidance.Guidance e : entries) {
            assertTrue(e.sourceUrl.startsWith("https://support.apple.com/"));
            assertFalse(e.advice(true).isEmpty());
            assertFalse(e.advice(false).isEmpty());
            assertFalse(e.category.contains("PANIC_CODE_REPAIR"));
            assertFalse(e.category.contains("CERTIFIED_FIX"));
        }
    }

    @Test public void exactManualSelectionMustNeverBorrowAnotherIphoneManual() throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> entries = docs();
        assertEquals("iPhone 14 Pro Repair Manual",
                AppleOfficialServiceGuidance.modelManual(entries, "iPhone15,2").title(false));
        assertEquals("iPhone 15 Pro Repair Manual",
                AppleOfficialServiceGuidance.modelManual(entries, "iPhone16,1").title(false));
        assertEquals("iPhone 12 Pro Max Repair Manual",
                AppleOfficialServiceGuidance.modelManual(entries, "iPhone13,4").title(false));
        assertNull("Do not claim an indexed Apple repair manual for older models not curated",
                AppleOfficialServiceGuidance.modelManual(entries, "iPhone12,1"));
        assertNull(AppleOfficialServiceGuidance.modelManual(entries, "iPhone19,9"));
        assertNull("Never auto-select the iPhone 18 Pro directory manual via made-up ID",
                AppleOfficialServiceGuidance.modelManual(entries, "-"));
        assertNull(AppleOfficialServiceGuidance.forTopic(entries, "-", "MODEL_MANUAL_DIRECTORY"));
        assertNull(AppleOfficialServiceGuidance.modelManual(entries, "Unknown"));
    }

    @Test public void appleDiagnosticsOnlyOfferedForSupportedKnownModels() throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> entries = docs();
        assertNull(AppleOfficialServiceGuidance.forTopic(entries, "iPhone12,1", "APPLE_DIAGNOSTICS"));
        assertNull(AppleOfficialServiceGuidance.forTopic(entries, "iPhone13,4", "APPLE_DIAGNOSTICS"));
        assertNotNull(AppleOfficialServiceGuidance.forTopic(entries, "iPhone15,2", "APPLE_DIAGNOSTICS"));
        assertNotNull(AppleOfficialServiceGuidance.forTopic(entries, "iPhone16,1", "APPLE_DIAGNOSTICS"));
        assertNull(AppleOfficialServiceGuidance.forTopic(entries, "Unknown", "UNEXPECTED_RESTART"));
    }

    @Test public void repairAssistantIsModelAndPartConditionalNotPanicProof() throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> entries = docs();
        AppleOfficialServiceGuidance.Guidance olderSupported =
                AppleOfficialServiceGuidance.forTopic(entries, "iPhone13,4", "POST_REPAIR");
        assertNotNull("iPhone 12 eligible battery/display/camera work may use Repair Assistant",
                olderSupported);
        assertTrue(olderSupported.advice(true).contains("εξαρτάται από μοντέλο και εξάρτημα"));
        assertNotNull(AppleOfficialServiceGuidance.forTopic(entries, "iPhone15,2", "POST_REPAIR"));
        assertNotNull(AppleOfficialServiceGuidance.forTopic(entries, "iPhone17,1", "POST_REPAIR"));
        assertNull(AppleOfficialServiceGuidance.forTopic(entries, "iPhone12,1", "POST_REPAIR"));
        assertNull(AppleOfficialServiceGuidance.forTopic(entries, "Unknown", "POST_REPAIR"));
        assertEquals("https://support.apple.com/en-us/120579", olderSupported.sourceUrl);
        assertEquals("2026-11-05", olderSupported.publishedDate);
    }

    @Test public void appleRestartIsNotAnEndorsedMic2ToFlexRecommendation() throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> entries = docs();
        AppleOfficialServiceGuidance.Guidance r =
                AppleOfficialServiceGuidance.forTopic(entries, "iPhone12,1", "UNEXPECTED_RESTART");
        assertNotNull(r);
        assertTrue(r.advice(true).contains("ΔΕΝ συνδέει panic code"));
        assertFalse(AppleOfficialServiceGuidance.topicMightBeUsefulForAction("REPLACE_POWER_FLEX",
                "WIRED_CHARGING"));
        assertTrue(AppleOfficialServiceGuidance.topicMightBeUsefulForAction("REPLACE_CHARGE_PORT_FLEX",
                "WIRED_CHARGING"));
        assertFalse(AppleOfficialServiceGuidance.topicMightBeUsefulForAction("REPLACE_BATTERY",
                "WIRED_CHARGING"));
    }

    @Test(expected=IOException.class)
    public void rejectNonAppleSourcesPassingThemselvesOffAsOfficial() throws Exception {
        String line = "FAKE\tiPhone15,2\tMODEL_MANUAL\tΟδηγίες\tManual"
                + "\tΟδηγία\tSteps\thttps://example.com/man\t2026-11-21\tAPPLE_MODEL_MANUAL\n";
        AppleOfficialServiceGuidance.parse(new ByteArrayInputStream(
                line.getBytes(StandardCharsets.UTF_8)));
    }

    @Test(expected=IOException.class)
    public void rejectWildcardOfficialModelManual() throws Exception {
        String line = "FAKE\t*\tMODEL_MANUAL\tΟδηγίες\tManual"
                + "\tΟδηγία\tSteps\thttps://support.apple.com/en-us/100308\t2026-11-21\tAPPLE_MODEL_MANUAL\n";
        AppleOfficialServiceGuidance.parse(new ByteArrayInputStream(
                line.getBytes(StandardCharsets.UTF_8)));
    }
}
