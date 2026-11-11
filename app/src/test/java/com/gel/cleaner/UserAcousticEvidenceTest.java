package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class UserAcousticEvidenceTest {
    @Test public void positiveUserReportIsIndependentFromMissingAutomaticDetection() {
        String text = UserAcousticEvidence.message(
                2, 4, 2, UserAcousticEvidence.Answer.ALL, true);
        assertTrue(UserAcousticEvidence.isPositive(UserAcousticEvidence.Answer.ALL));
        assertTrue(text.contains("ΕΠΙΒΕΒΑΙΩΣΗ ΧΡΗΣΤΗ"));
        assertTrue(text.contains("2/4"));
        assertTrue(text.contains("και οι 4"));
        assertTrue(text.contains("Δεν αποτελεί πιστοποίηση"));
    }
    @Test public void partialUserReportIsPositiveButNotFullCertification() {
        String text = UserAcousticEvidence.message(
                2, 4, 0, UserAcousticEvidence.Answer.SOME, true);
        assertTrue(UserAcousticEvidence.isPositive(UserAcousticEvidence.Answer.SOME));
        assertTrue(text.contains("ΜΕΡΙΚΗ"));
        assertTrue(text.contains("0/4"));
        assertTrue(text.contains("Δεν τεκμηριώνεται βλάβη ή πλήρης επιτυχία"));
    }
    @Test public void negativeUserReportIsNotGreen() {
        assertFalse(UserAcousticEvidence.isPositive(UserAcousticEvidence.Answer.NONE));
        assertFalse(UserAcousticEvidence.message(
                1, 1, 0, UserAcousticEvidence.Answer.NONE, true)
                .contains("ΕΠΙΒΕΒΑΙΩΣΗ ΧΡΗΣΤΗ"));
    }
    @Test public void positiveLabOneUserReportDoesNotInventAutoSuccess() {
        String text = UserAcousticEvidence.message(
                1, 1, 0, UserAcousticEvidence.Answer.ALL, false);
        assertTrue(text.contains("USER_REPORTED"));
        assertTrue(text.contains("0/1"));
        assertTrue(text.contains("not certified"));
    }
}
