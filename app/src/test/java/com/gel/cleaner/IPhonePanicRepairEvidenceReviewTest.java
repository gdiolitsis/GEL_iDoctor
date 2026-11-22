package com.gel.cleaner;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

/**
 * No self-reported forum outcome is an Apple result or a repair prescription.
 * The same device and repeated imported panics cannot become independent
 * successful repairs, and failures must remain visible.
 */
public final class IPhonePanicRepairEvidenceReviewTest {
    private static List<IPhonePanicRepairKnowledgeBase.RepairCase> catalog() throws Exception {
        Path p = Paths.get("app/src/main/assets/iphone_panic_repair_knowledge_v1.tsv");
        if (!Files.isRegularFile(p)) p =
                Paths.get("src/main/assets/iphone_panic_repair_knowledge_v1.tsv");
        try (InputStream in = Files.newInputStream(p)) {
            return IPhonePanicRepairKnowledgeBase.parse(in);
        }
    }

    private static List<IPhonePanicRepairKnowledgeBase.RepairCase> sample(String... entries)
            throws Exception {
        StringBuilder csv = new StringBuilder();
        for (String row : entries) {
            // id ; incident ; exact-model ; code ; action ; result
            String[] c = row.split(";", -1);
            if (c.length != 6) throw new IllegalArgumentException(row);
            csv.append(c[0]).append("\t").append(c[1]).append("\t")
                    .append(c[2]).append("\t").append(c[3]).append("\t")
                    .append(c[4]).append("\t").append(c[5]).append("\t")
                    .append("unknown\thttps://www.ifixit.com/Answers/View/730301/")
                    .append("\tενέργεια\taction\tπερίπτωση\tcase\n");
        }
        return IPhonePanicRepairKnowledgeBase.parse(new ByteArrayInputStream(
                csv.toString().getBytes(StandardCharsets.UTF_8)));
    }

    private static IPhonePanicRepairEvidenceReview.ActionHistory action(
            IPhonePanicRepairEvidenceReview.Assessment assessed, String key) {
        for (IPhonePanicRepairEvidenceReview.ActionHistory row : assessed.actions) {
            if (row.actionKey.equals(key)) return row;
        }
        throw new AssertionError("Missing action " + key);
    }

    @Test public void unknownCodeAndWrongModelHaveNoPublishedFix() throws Exception {
        List<IPhonePanicRepairKnowledgeBase.RepairCase> all = catalog();
        IPhonePanicRepairEvidenceReview.Assessment no =
                IPhonePanicRepairEvidenceReview.assessExact(all, "iPhone99,9", "MIC2");
        assertEquals(IPhonePanicRepairEvidenceReview.Status.NO_MATCH, no.status);
        assertEquals(0, no.distinctIncidents);
        assertEquals(0, no.distinctAttempts);
        assertTrue(no.actions.isEmpty());
        assertTrue(no.display(true).contains("Καμία"));
        assertEquals(IPhonePanicRepairEvidenceReview.Status.NO_MATCH,
                IPhonePanicRepairEvidenceReview.assessExact(
                        all, "iPhone14,3", "WATCHDOG_NO_MISSING_SENSOR").status);
    }

    @Test public void multipleSwapsOnSamePhoneNeverManufactureIndependentSuccess()
            throws Exception {
        IPhonePanicRepairEvidenceReview.Assessment xs =
                IPhonePanicRepairEvidenceReview.assessExact(
                        catalog(), "iPhone11,6", "PRS0");
        assertEquals(IPhonePanicRepairEvidenceReview.Status.MIXED_OR_RECURRING_OUTCOMES,
                xs.status);
        assertEquals(2, xs.distinctIncidents);
        assertEquals(3, xs.distinctAttempts);
        assertEquals(1, xs.incidentsWithAnyReportedSuccess);
        assertEquals(2, xs.incidentsWithAnyReportedFailure);
        IPhonePanicRepairEvidenceReview.ActionHistory port =
                action(xs, "REPLACE_CHARGE_PORT_FLEX");
        assertEquals(2, port.incidents);
        assertEquals(1, port.mixedIncidents);
        assertEquals(1, port.failureOnlyIncidents);
        assertEquals(0, port.successOnlyIncidents);
        assertTrue(xs.display(false).contains("no single reliable fix"));
    }

    @Test public void severalMic2RepairActionsDoNotBecomeOneOfficialPartVerdict()
            throws Exception {
        IPhonePanicRepairEvidenceReview.Assessment xs =
                IPhonePanicRepairEvidenceReview.assessExact(
                        catalog(), "iPhone12,1", "MIC2");
        assertEquals(IPhonePanicRepairEvidenceReview.Status.MIXED_OR_RECURRING_OUTCOMES,
                xs.status);
        assertEquals(5, xs.distinctIncidents);
        assertEquals(7, xs.distinctAttempts);
        assertTrue(xs.actions.size() >= 3);
        IPhonePanicRepairEvidenceReview.ActionHistory port =
                action(xs, "REPLACE_CHARGE_PORT_FLEX");
        assertEquals(2, port.incidents);
        assertEquals(1, port.mixedIncidents);
        assertEquals(1, port.failureOnlyIncidents);
        assertEquals(0, port.successOnlyIncidents);
        assertTrue(xs.independentEvidence(true).contains("διαφορετικά"));
    }

    @Test public void failureOnlyReportsRemainFailuresEvenIfSamePhoneHasSeveralAttempts()
            throws Exception {
        IPhonePanicRepairEvidenceReview.Assessment xs =
                IPhonePanicRepairEvidenceReview.assessExact(
                        catalog(), "iPhone12,5", "MIC2");
        assertEquals(IPhonePanicRepairEvidenceReview.Status.ONLY_REPORTED_FAILURES,
                xs.status);
        assertEquals(1, xs.distinctIncidents);
        assertEquals(2, xs.distinctAttempts);
        assertEquals(0, xs.incidentsWithAnyReportedSuccess);
        assertEquals(1, xs.incidentsWithAnyReportedFailure);
        assertTrue(xs.display(true).contains("Δεν έχει τεκμηριωθεί λύση"));
    }

    @Test public void singleReportedSuccessIsNotCalledIndependentProof() throws Exception {
        IPhonePanicRepairEvidenceReview.Assessment xs =
                IPhonePanicRepairEvidenceReview.assessExact(
                        catalog(), "iPhone15,2", "SMC 262144");
        assertEquals(IPhonePanicRepairEvidenceReview.Status.ONE_SELF_REPORTED_SUCCESS,
                xs.status);
        assertEquals(1, xs.distinctIncidents);
        assertEquals(1, xs.distinctAttempts);
        assertTrue(xs.display(false).contains("not been independently verified"));
    }

    @Test public void recurrenceIsNeverReportedAsSuccessfulFix() throws Exception {
        IPhonePanicRepairEvidenceReview.Assessment xs =
                IPhonePanicRepairEvidenceReview.assessExact(
                        catalog(), "iPhone16,1", "SMC 161");
        assertEquals(IPhonePanicRepairEvidenceReview.Status.UNRESOLVED_OR_PARTIAL_ONLY,
                xs.status);
        assertEquals(0, xs.incidentsWithAnyReportedSuccess);
        assertTrue(xs.display(true).contains("μερικές"));
    }

    @Test public void repeatedImportedLogsCannotMultiplyOneSourcedAttempt() throws Exception {
        List<IPhonePanicRepairKnowledgeBase.RepairCase> matches =
                IPhonePanicRepairKnowledgeBase.lookup(catalog(), "iPhone11,6", "PRS0");
        List<IPhonePanicRepairKnowledgeBase.RepairCase> doubled = new ArrayList<>(matches);
        doubled.addAll(matches);
        IPhonePanicRepairEvidenceReview.Assessment xs =
                IPhonePanicRepairEvidenceReview.assess(doubled);
        assertEquals(matches.size(), xs.distinctAttempts);
        assertEquals(2, xs.distinctIncidents);
    }

    @Test public void twoCleanIndependentReportsRemainSelfReportedNotCertified()
            throws Exception {
        IPhonePanicRepairEvidenceReview.Assessment xs =
                IPhonePanicRepairEvidenceReview.assessExact(
                        sample("a;phone1;iPhone15,2;MIC2;ACTION;SELF_REPORTED_SUCCESS",
                                "b;phone2;iPhone15,2;MIC2;ACTION;SELF_REPORTED_SUCCESS"),
                        "iPhone15,2", "MIC2");
        assertEquals(IPhonePanicRepairEvidenceReview.Status.MULTIPLE_SELF_REPORTED_SUCCESSES,
                xs.status);
        assertEquals(2, xs.distinctIncidents);
        assertEquals(2, action(xs, "ACTION").successOnlyIncidents);
        assertTrue(xs.display(false).contains("not certification"));
    }

    @Test public void successAndFailureInOneDeviceIsMixedNotTwoConfirmations()
            throws Exception {
        IPhonePanicRepairEvidenceReview.Assessment xs =
                IPhonePanicRepairEvidenceReview.assessExact(
                        sample("a;phone1;iPhone15,2;MIC2;ACTION;SELF_REPORTED_FAILURE",
                                "b;phone1;iPhone15,2;MIC2;ACTION;SELF_REPORTED_SUCCESS"),
                        "iPhone15,2", "MIC2");
        assertEquals(IPhonePanicRepairEvidenceReview.Status.MIXED_OR_RECURRING_OUTCOMES,
                xs.status);
        assertEquals(1, xs.distinctIncidents);
        assertEquals(1, action(xs, "ACTION").mixedIncidents);
        assertEquals(0, action(xs, "ACTION").successOnlyIncidents);
    }

    @Test public void unresolvedPlusFailedCasesNeverBecomeMultipleSuccesses()
            throws Exception {
        IPhonePanicRepairEvidenceReview.Assessment xs =
                IPhonePanicRepairEvidenceReview.assessExact(
                        sample("a;phone1;iPhone15,2;MIC2;ACTION;SELF_REPORTED_FAILURE",
                                "b;phone2;iPhone15,2;MIC2;ACTION;UNRESOLVED"),
                        "iPhone15,2", "MIC2");
        assertEquals(IPhonePanicRepairEvidenceReview.Status.MIXED_OR_RECURRING_OUTCOMES,
                xs.status);
        assertEquals(0, xs.incidentsWithAnyReportedSuccess);
    }
}
