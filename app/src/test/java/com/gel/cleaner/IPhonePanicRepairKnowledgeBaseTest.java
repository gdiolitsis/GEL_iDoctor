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
import java.util.Set;
import java.util.HashSet;

import static org.junit.Assert.*;

/** Published repair outcomes are sourced, model-specific and explicitly unverified. */
public final class IPhonePanicRepairKnowledgeBaseTest {
    private static List<IPhonePanicRepairKnowledgeBase.RepairCase> catalog() throws Exception {
        Path asset = Paths.get("app/src/main/assets/iphone_panic_repair_knowledge_v1.tsv");
        if (!Files.isRegularFile(asset)) {
            asset = Paths.get("src/main/assets/iphone_panic_repair_knowledge_v1.tsv");
        }
        try (InputStream in = Files.newInputStream(asset)) {
            return IPhonePanicRepairKnowledgeBase.parse(in);
        }
    }

    @Test public void initialCatalogHasBothActualReportedSuccessesAndFailures() throws Exception {
        List<IPhonePanicRepairKnowledgeBase.RepairCase> records = catalog();
        assertTrue("seed must grow by case-level verified curation", records.size() >= 24);
        int success = 0, failed = 0, partial = 0, unresolved = 0;
        Set<String> unique = new HashSet<>();
        for (IPhonePanicRepairKnowledgeBase.RepairCase item : records) {
            assertTrue(unique.add(item.id));
            assertTrue(item.sourceUrl.startsWith("https://"));
            assertFalse(item.localizedAction(true).trim().isEmpty());
            assertFalse(item.localizedAction(false).trim().isEmpty());
            assertFalse(item.localizedNote(true).trim().isEmpty());
            assertFalse(item.localizedNote(false).trim().isEmpty());
            assertFalse(item.reportedDate.trim().isEmpty());
            assertFalse("no invented population repair probability",
                    item.noteEnglish.contains("guaranteed"));
            if (item.outcome == IPhonePanicRepairKnowledgeBase.Outcome.SELF_REPORTED_SUCCESS)
                success++;
            if (item.outcome == IPhonePanicRepairKnowledgeBase.Outcome.SELF_REPORTED_FAILURE)
                failed++;
            if (item.outcome == IPhonePanicRepairKnowledgeBase.Outcome.PARTIAL_OR_RECURRING)
                partial++;
            if (item.outcome == IPhonePanicRepairKnowledgeBase.Outcome.UNRESOLVED)
                unresolved++;
        }
        assertTrue(success >= 5);
        assertTrue(failed >= 8);
        assertTrue(partial >= 1);
        assertTrue(unresolved >= 1);
    }

    @Test public void sameMic2CanHaveDifferentReportedSuccessfulFixes() throws Exception {
        List<IPhonePanicRepairKnowledgeBase.RepairCase> matches =
                IPhonePanicRepairKnowledgeBase.lookup(catalog(), "iPhone12,1", "mic2");
        Set<String> actionKeys = new HashSet<>();
        for (IPhonePanicRepairKnowledgeBase.RepairCase item : matches) {
            if (item.outcome == IPhonePanicRepairKnowledgeBase.Outcome.SELF_REPORTED_SUCCESS)
                actionKeys.add(item.actionKey);
        }
        assertTrue(actionKeys.contains("REPLACE_POWER_FLEX"));
        assertTrue(actionKeys.contains("RESEAT_POWER_FLEX"));
        assertTrue(actionKeys.contains("REPLACE_CHARGE_PORT_FLEX"));
    }

    @Test public void failedRepairAndLaterSuccessFromSameDeviceAreSeparateAttempts() throws Exception {
        List<IPhonePanicRepairKnowledgeBase.RepairCase> matches =
                IPhonePanicRepairKnowledgeBase.lookup(catalog(), "iPhone11,6", "prs0");
        boolean failed = false, succeeded = false;
        for (IPhonePanicRepairKnowledgeBase.RepairCase item : matches) {
            if ("thread-795864-owner".equals(item.incidentId)) {
                failed |= item.outcome == IPhonePanicRepairKnowledgeBase.Outcome.SELF_REPORTED_FAILURE;
                succeeded |= item.outcome == IPhonePanicRepairKnowledgeBase.Outcome.SELF_REPORTED_SUCCESS;
            }
        }
        assertTrue("failed first part must not be hidden", failed);
        assertTrue("later reported success must remain separate", succeeded);
    }

    @Test public void crossModelSmcCodeNeverMapsToWrongHardware() throws Exception {
        List<IPhonePanicRepairKnowledgeBase.RepairCase> all = catalog();
        assertEquals("SMC 0x40000",
                IPhonePanicRepairKnowledgeBase.canonicalCode("SMC 262144"));
        assertFalse(IPhonePanicRepairKnowledgeBase.lookup(all, "iPhone15,2",
                "SMC 262144").isEmpty());
        assertTrue(IPhonePanicRepairKnowledgeBase.lookup(all, "iPhone15,4",
                "SMC 262144").isEmpty());
        assertTrue(IPhonePanicRepairKnowledgeBase.lookup(all, "iPhone16,1",
                "SMC 0x400000").isEmpty());
        assertTrue(IPhonePanicRepairKnowledgeBase.lookup(all, "iPhone19,9",
                "MIC2").isEmpty());
        assertTrue("generic I2C must never prompt a specific display swap",
                IPhonePanicRepairKnowledgeBase.lookup(all, "iPhone11,6",
                        "I2C_COMMUNICATION").isEmpty());
        assertFalse("only explicit I2C3 clock-line signature has the historical report",
                IPhonePanicRepairKnowledgeBase.lookup(all, "iPhone11,6",
                        "I2C3_SCL_STUCK_LOW").isEmpty());
    }

    @Test public void noFalseSensorOrRepairClaimWithUnknownInputs() throws Exception {
        List<IPhonePanicRepairKnowledgeBase.RepairCase> all = catalog();
        assertTrue(IPhonePanicRepairKnowledgeBase.lookup(all, null, "MIC2").isEmpty());
        assertTrue(IPhonePanicRepairKnowledgeBase.lookup(all, "iPhone12,1", null).isEmpty());
        assertTrue(IPhonePanicRepairKnowledgeBase.lookup(all, "iPhone12,1",
                "AOP_PANIC").isEmpty());
        assertEquals("SMC 0x300000",
                IPhonePanicRepairKnowledgeBase.canonicalCode("SMC 3145728"));
    }

    @Test(expected=IOException.class)
    public void duplicateCaseIdsAreRejected() throws Exception {
        String head = "case_id\tincident_id\tmodels\tpanic_code\taction_key\toutcome\t"
                + "reported_date\tsource_url\taction_gr\taction_en\tobservation_gr\tobservation_en\n";
        String row = "dup\tinc\tiPhone12,1\tMIC2\tREPLACE_POWER_FLEX\tSELF_REPORTED_SUCCESS"
                + "\tunknown\thttps://www.ifixit.com/Answers/View/730301/"
                + "\tενέργεια\taction\tαποτέλεσμα\tresult\n";
        IPhonePanicRepairKnowledgeBase.parse(new ByteArrayInputStream(
                (head + row + row).getBytes(StandardCharsets.UTF_8)));
    }

    @Test(expected=IOException.class)
    public void reportsWithoutSourceAreRejected() throws Exception {
        String row = "id\tinc\tiPhone12,1\tMIC2\tACTION\tSELF_REPORTED_SUCCESS"
                + "\tunknown\t\tενέργεια\taction\tαποτέλεσμα\tresult\n";
        IPhonePanicRepairKnowledgeBase.parse(new ByteArrayInputStream(
                row.getBytes(StandardCharsets.UTF_8)));
    }

    @Test public void publishedBenchAudioCodecPanicDoesNotRequireCodecReplacement() throws Exception {
        List<IPhonePanicRepairKnowledgeBase.RepairCase> xs =
                IPhonePanicRepairKnowledgeBase.lookup(catalog(), "iPhone12,5",
                        "AUDIO_CODEC_CS42L77");
        assertEquals(1, xs.size());
        assertEquals("REPLACE_FRONT_SENSOR_FLEX", xs.get(0).actionKey);
        assertTrue(xs.get(0).noteEnglish.contains("without replacing"));
        assertTrue(IPhonePanicRepairKnowledgeBase.lookup(catalog(), "iPhone12,3",
                "AUDIO_CODEC_CS42L77").isEmpty());
    }

    @Test public void intermittentPanicAfterBatteryChangeIsNotACertifiedSuccess() throws Exception {
        List<IPhonePanicRepairKnowledgeBase.RepairCase> xs =
                IPhonePanicRepairKnowledgeBase.lookup(catalog(), "iPhone16,1", "SMC 161");
        assertEquals(1, xs.size());
        assertEquals(IPhonePanicRepairKnowledgeBase.Outcome.PARTIAL_OR_RECURRING,
                xs.get(0).outcome);
        assertTrue(xs.get(0).localizedOutcome(true).contains("επανεμφάνιση"));
    }

    @Test public void sameModelRepeatedFailuresRemainSeparateAndNotIndependentDevices() throws Exception {
        List<IPhonePanicRepairKnowledgeBase.RepairCase> xs =
                IPhonePanicRepairKnowledgeBase.lookup(catalog(), "iPhone12,5", "MIC2");
        int failed = 0;
        for (IPhonePanicRepairKnowledgeBase.RepairCase item : xs) {
            if (item.incidentId.equals("thread-770966-owner")) {
                assertEquals(IPhonePanicRepairKnowledgeBase.Outcome.SELF_REPORTED_FAILURE,
                        item.outcome);
                failed++;
            }
        }
        assertEquals(2, failed);
    }
}
