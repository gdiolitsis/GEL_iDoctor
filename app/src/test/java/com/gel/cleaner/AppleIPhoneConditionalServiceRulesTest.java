package com.gel.cleaner;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import static org.junit.Assert.*;

/**
 * Manufacturer's per-model, post-test actions may NEVER be inferred from
 * a GEL panic signature. Conditions are strict and require real Apple tests.
 */
public final class AppleIPhoneConditionalServiceRulesTest {
    private static Path asset(String name) {
        Path full = Paths.get("app/src/main/assets/" + name);
        return Files.isRegularFile(full) ? full : Paths.get("src/main/assets/" + name);
    }
    private static List<AppleOfficialServiceGuidance.Guidance> guidance() throws Exception {
        try (InputStream in = Files.newInputStream(asset(AppleOfficialServiceGuidance.ASSET))) {
            return AppleOfficialServiceGuidance.parse(in);
        }
    }
    private static List<AppleIPhoneManualChapters.Chapter> chapters() throws Exception {
        try (InputStream in = Files.newInputStream(asset(AppleIPhoneManualChapters.ASSET))) {
            return AppleIPhoneManualChapters.parse(in, guidance());
        }
    }
    private static List<AppleIPhoneConditionalServiceRules.Rule> rules() throws Exception {
        try (InputStream in = Files.newInputStream(asset(AppleIPhoneConditionalServiceRules.ASSET))) {
            return AppleIPhoneConditionalServiceRules.parse(in, guidance(), chapters());
        }
    }
    private static ByteArrayInputStream data(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }
    @Test public void everyConditionalAppleRuleReferencesExactlyVerifiedModelAndSource() throws Exception {
        List<AppleIPhoneConditionalServiceRules.Rule> all = rules();
        assertEquals(101, all.size());
        Set<String> models = new HashSet<>();
        for (AppleIPhoneConditionalServiceRules.Rule r : all) {
            models.add(r.hardwareId);
            assertEquals("2026-11-22", r.reviewedOn);
            assertTrue(r.sourceUrl.startsWith("https://support.apple.com/en-us/"));
            assertNotNull(AppleOfficialServiceGuidance.modelManual(guidance(), r.hardwareId));
            assertFalse(r.greek.isEmpty());
            assertFalse(r.english.isEmpty());
        }
        assertEquals(12, models.size());
    }

    private static List<AppleIPhoneConditionalServiceRules.Rule> observed(
            String model, AppleIPhoneConditionalServiceRules.Test test, boolean actual) throws Exception {
        return AppleIPhoneConditionalServiceRules.forVerifiedObservedResult(
                rules(), model, test, AppleIPhoneConditionalServiceRules.Result.FAILED, actual);
    }

    @Test public void realBarometerFailureHasDifferentOfficialServicePerModel() throws Exception {
        List<AppleIPhoneConditionalServiceRules.Rule> thirteen =
                observed("iPhone14,3", AppleIPhoneConditionalServiceRules.Test.MRI_BAROMETER, true);
        assertEquals(1, thirteen.size());
        assertEquals(AppleIPhoneConditionalServiceRules.ServicePart.WHOLE_UNIT,
                thirteen.get(0).part);
        List<AppleIPhoneConditionalServiceRules.Rule> fifteen =
                observed("iPhone15,4", AppleIPhoneConditionalServiceRules.Test.MRI_BAROMETER, true);
        assertEquals(1, fifteen.size());
        assertEquals(AppleIPhoneConditionalServiceRules.ServicePart.MAIN_MICROPHONE,
                fifteen.get(0).part);
        assertEquals("https://support.apple.com/en-us/101966", fifteen.get(0).sourceUrl);
    }

    @Test public void soundDiagnosticNeverBorrowsNearbyModelsReplacement() throws Exception {
        List<AppleIPhoneConditionalServiceRules.Rule> old =
                observed("iPhone14,3", AppleIPhoneConditionalServiceRules.Test.AST2_BOTTOM_MIC_LEFT, true);
        List<AppleIPhoneConditionalServiceRules.Rule> newer =
                observed("iPhone15,4", AppleIPhoneConditionalServiceRules.Test.AST2_BOTTOM_MIC_LEFT, true);
        assertEquals(1, old.size());
        assertEquals(1, newer.size());
        assertEquals(AppleIPhoneConditionalServiceRules.ServicePart.WHOLE_UNIT, old.get(0).part);
        assertEquals(AppleIPhoneConditionalServiceRules.ServicePart.MAIN_MICROPHONE, newer.get(0).part);
        List<AppleIPhoneConditionalServiceRules.Rule> rearMic =
                observed("iPhone15,4", AppleIPhoneConditionalServiceRules.Test.AST2_REAR_MIC, true);
        assertEquals(1, rearMic.size());
        assertEquals(AppleIPhoneConditionalServiceRules.ServicePart.BACK_GLASS, rearMic.get(0).part);
    }

    @Test public void panicLogCannotActivateManufacturerPartsWithoutActualAppleTest() throws Exception {
        for (AppleIPhoneConditionalServiceRules.Test test :
                AppleIPhoneConditionalServiceRules.Test.values()) {
            assertTrue(AppleIPhoneConditionalServiceRules.forVerifiedObservedResult(
                    rules(), "iPhone14,3", test,
                    AppleIPhoneConditionalServiceRules.Result.FAILED, false).isEmpty());
        }
        assertTrue(observed("Unknown",
                AppleIPhoneConditionalServiceRules.Test.MRI_BAROMETER, true).isEmpty());
        assertTrue(observed("iPhone99,9",
                AppleIPhoneConditionalServiceRules.Test.AST2_SPEAKER, true).isEmpty());
        assertTrue(observed("iPhone16,2",
                AppleIPhoneConditionalServiceRules.Test.AST2_REAR_MIC, true).isEmpty());
    }

    @Test public void persistedAlertsRequireDocumentedChecklistNotMRIResult() throws Exception {
        List<AppleIPhoneConditionalServiceRules.Rule> wrong =
                AppleIPhoneConditionalServiceRules.forVerifiedObservedResult(rules(),
                "iPhone15,4", AppleIPhoneConditionalServiceRules.Test.TEMPERATURE_ALERT_AFTER_CHECKS,
                AppleIPhoneConditionalServiceRules.Result.FAILED, true);
        assertTrue(wrong.isEmpty());
        List<AppleIPhoneConditionalServiceRules.Rule> valid =
                AppleIPhoneConditionalServiceRules.forVerifiedObservedResult(rules(),
                "iPhone15,4", AppleIPhoneConditionalServiceRules.Test.TEMPERATURE_ALERT_AFTER_CHECKS,
                AppleIPhoneConditionalServiceRules.Result.PERSISTENT_AFTER_CHECKS, true);
        assertEquals(1, valid.size());
        assertEquals(AppleIPhoneConditionalServiceRules.ServicePart.ENCLOSURE, valid.get(0).part);
        assertTrue(AppleIPhoneConditionalServiceRules.forVerifiedObservedResult(rules(),
                "iPhone15,4", AppleIPhoneConditionalServiceRules.Test.BUTTONS_AFTER_CHECKS,
                AppleIPhoneConditionalServiceRules.Result.PERSISTENT_AFTER_CHECKS, false).isEmpty());
    }

    @Test(expected=java.io.IOException.class)
    public void rejectRuleFromUnverifiedModelHardwareId() throws Exception {
        AppleIPhoneConditionalServiceRules.parse(data(
                "FAKE\tDIRECTORY_17\tiPhone18,1\tSOUND\tAST2_RECEIVER\tFAILED\tTOP_SPEAKER\t"
                + "https://support.apple.com/en-us/101968\tΕξήγηση\tExplanation\t2026-11-22\n"),
                guidance(), chapters());
    }

    @Test(expected=java.io.IOException.class)
    public void rejectWrongAppleArticleEvenForKnownModel() throws Exception {
        AppleIPhoneConditionalServiceRules.parse(data(
                "FAKE\tMANUAL_13_PRO_MAX\tiPhone14,3\tSOUND\tAST2_RECEIVER\tFAILED\tTOP_SPEAKER\t"
                + "https://support.apple.com/en-us/101966\tΕξήγηση\tExplanation\t2026-11-22\n"),
                guidance(), chapters());
    }

    @Test(expected=java.io.IOException.class)
    public void rejectPersistentSymptomLabeledAsDiagnosticFailure() throws Exception {
        AppleIPhoneConditionalServiceRules.parse(data(
                "FAKE\tMANUAL_15\tiPhone15,4\tBUTTONS\tBUTTONS_AFTER_CHECKS\tFAILED\tENCLOSURE\t"
                + "https://support.apple.com/en-us/101970\tΕξήγηση\tExplanation\t2026-11-22\n"),
                guidance(), chapters());
    }

    @Test(expected=java.io.IOException.class)
    public void rejectContradictoryResultForSameModelAndTest() throws Exception {
        String row = "A\tMANUAL_15\tiPhone15,4\tSOUND\tAST2_RECEIVER\tFAILED\tTOP_SPEAKER\t"
                + "https://support.apple.com/en-us/101968\tΕξήγηση\tExplanation\t2026-11-22\n";
        AppleIPhoneConditionalServiceRules.parse(data(row + row.replace("A\t", "B\t")),
                guidance(), chapters());
    }
}
