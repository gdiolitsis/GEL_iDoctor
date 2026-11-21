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
        assertTrue("Official Apple catalog must retain 54 entries", entries.size() >= 54);
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

    @Test public void genericSymptomGuideCanBeReadWithoutMakingUpAnIphoneModel() throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> entries = docs();
        AppleOfficialServiceGuidance.Guidance restart =
                AppleOfficialServiceGuidance.genericForTopic(entries, "UNEXPECTED_RESTART");
        assertNotNull(restart);
        assertEquals("*", restart.models);
        assertTrue(restart.sourceUrl.startsWith("https://support.apple.com/"));
        assertNull("Model manuals must never be returned from the generic workflow",
                AppleOfficialServiceGuidance.genericForTopic(entries, "MODEL_MANUAL"));
        assertNull("Diagnostics require checked model eligibility",
                AppleOfficialServiceGuidance.genericForTopic(entries, "APPLE_DIAGNOSTICS"));
    }

    @Test public void officialSourceExpansionCoversKeyIphoneRepairSymptoms() throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> entries = docs();
        for (String topic : new String[]{"MICROPHONE", "SPEAKER", "CAMERA", "FACE_ID",
                "DISPLAY_TOUCH", "THERMAL", "CELLULAR_SERVICE", "CELLULAR_CALLS",
                "SOFTWARE_RECOVERY", "POWER_NO_BOOT", "TOUCH_ID", "LIQUID_CONNECTOR"}) {
            assertFalse("Missing official Apple source for " + topic,
                    AppleOfficialServiceGuidance.genericForTopicAll(entries, topic).isEmpty());
        }
        assertTrue("Wired charging should have a symptom hub AND basic user guidance",
                AppleOfficialServiceGuidance.genericForTopicAll(entries,
                        "WIRED_CHARGING").size() >= 2);
        assertEquals("iPhone 12 Repair Manual",
                AppleOfficialServiceGuidance.modelManual(entries, "iPhone13,2").title(false));
        assertEquals("iPhone 13 Repair Manual",
                AppleOfficialServiceGuidance.modelManual(entries, "iPhone14,5").title(false));
        assertEquals("iPhone SE 3rd generation Repair Manual",
                AppleOfficialServiceGuidance.modelManual(entries, "iPhone14,6").title(false));
    }

    @Test public void allThirtyCurrentlyListedIphoneRepairManualsAreIndexedWithoutInventedIdentifiers() throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> entries = docs();
        int exact = 0, directory = 0;
        java.util.Set<String> names = new java.util.HashSet<>();
        for (AppleOfficialServiceGuidance.Guidance item : entries) {
            if ("MODEL_MANUAL".equals(item.category)
                    || "MODEL_MANUAL_DIRECTORY".equals(item.category)) {
                assertTrue("Duplicated iPhone Repair Manual", names.add(item.titleEnglish));
                assertTrue("Every manual must link directly to Apple",
                        item.sourceUrl.startsWith("https://support.apple.com/"));
                if ("MODEL_MANUAL".equals(item.category)) {
                    exact++;
                    assertFalse("*".equals(item.models));
                    assertFalse("-".equals(item.models));
                } else {
                    directory++;
                    assertEquals("No unverified model-code mapping for directory manuals",
                            "-", item.models);
                }
            }
        }
        assertEquals("30 official iPhone repair manuals in Apple model directory", 30, names.size());
        assertEquals(12, exact);
        assertEquals(18, directory);
        for (String title : new String[]{"iPhone 12 mini Repair Manual",
                "iPhone 13 Pro Repair Manual", "iPhone 15 Pro Max Repair Manual",
                "iPhone 16 Pro Max Repair Manual", "iPhone 17e Repair Manual",
                "iPhone 18 Pro Max Repair Manual", "iPhone Duo Repair Manual"}) {
            assertTrue("Missing model manual: " + title, names.contains(title));
        }
        assertNull("Directory-only iPhone Duo manual cannot be assigned to a fabricated model",
                AppleOfficialServiceGuidance.modelManual(entries, "iPhone99,9"));
    }

    @Test public void panicClueTopicSelectionIsConditionalNotManufacturerPartDiagnosis() throws Exception {
        assertTrue(AppleOfficialServiceGuidance.conditionalTopicsForClue("MIC2")
                .contains("MICROPHONE"));
        assertTrue(AppleOfficialServiceGuidance.conditionalTopicsForClue("THERMAL_SHUTDOWN")
                .contains("THERMAL"));
        assertTrue(AppleOfficialServiceGuidance.conditionalTopicsForClue("BASEBAND_FAULT")
                .contains("CELLULAR_SERVICE"));
        assertTrue(AppleOfficialServiceGuidance.conditionalTopicsForClue("DISPLAY_CONTROLLER")
                .contains("DISPLAY_TOUCH"));
        assertTrue(AppleOfficialServiceGuidance.conditionalTopicsForClue("STORAGE_CONTROLLER")
                .contains("SOFTWARE_RECOVERY"));
        assertTrue("Numeric SMC codes are not an Apple-official part diagnosis",
                AppleOfficialServiceGuidance.conditionalTopicsForClue("SMC 0x80000").isEmpty());
        assertTrue("No generic replacement recommendations without a clue",
                AppleOfficialServiceGuidance.conditionalTopicsForClue("UNKNOWN").isEmpty());
    }

    @Test public void repairsDiagnosticsModesMustNotBeConfusedWithOlderRestrictions() throws Exception {
        List<AppleOfficialServiceGuidance.Guidance> rows = docs();
        AppleOfficialServiceGuidance.Guidance modern =
                AppleOfficialServiceGuidance.forTopic(rows, "iPhone13,2", "APPLE_DIAGNOSTICS_REPAIR");
        assertNotNull("iPhone 12 may use Apple diagnostics with supported iOS", modern);
        assertTrue(modern.advice(false).contains("iOS 18+"));
        assertTrue(modern.advice(false).contains("iOS 17+"));
        assertNotNull(AppleOfficialServiceGuidance.forTopic(rows,
                "iPhone15,2", "APPLE_DIAGNOSTICS_REPAIR"));
        assertNull(AppleOfficialServiceGuidance.forTopic(rows,
                "iPhone12,1", "APPLE_DIAGNOSTICS_REPAIR"));
        assertNull(AppleOfficialServiceGuidance.forTopic(rows,
                "Unknown", "APPLE_DIAGNOSTICS_REPAIR"));
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
