package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/** The used-device buyer sees an explanation, not reconstructed hardware tests. */
public final class Lab28BuyerSummaryTest {
    @Test public void interpretsBWithoutDeclaringFaultOrDowngradingIt() {
        String greek = Lab28BuyerSummary.explain("B", false, true);
        assertTrue(greek.contains("καλή"));
        assertTrue(greek.contains("δεν αποδεικνύει βλάβη"));
        String english = Lab28BuyerSummary.explain("B", false, false);
        assertTrue(english.contains("good"));
        assertTrue(english.contains("does not establish a malfunction"));
    }

    @Test public void respondsToConfirmedFaultWithoutInventingOne() {
        String limited = Lab28BuyerSummary.explain("D", true, true);
        assertTrue(limited.contains("επιβεβαιωμένο λειτουργικό περιορισμό"));
        assertTrue(limited.contains("Lab 27"));
        String lowScore = Lab28BuyerSummary.explain("D", false, true);
        assertFalse(lowScore.contains("επιβεβαιωμένο λειτουργικό περιορισμό"));
        assertTrue(lowScore.contains("χαμηλή"));
    }

    @Test public void handlesEverySupportedGradeAndRejectsMissingCertificate() {
        for (String grade : new String[]{"A+", "A", "B", "C", "D"}) {
            assertTrue(Lab28BuyerSummary.validGrade(grade));
            assertFalse(Lab28BuyerSummary.explain(grade, false, true).isEmpty());
            assertFalse(Lab28BuyerSummary.explain(grade, false, false).isEmpty());
        }
        assertFalse(Lab28BuyerSummary.validGrade(""));
        assertFalse(Lab28BuyerSummary.validGrade(null));
        assertFalse(Lab28BuyerSummary.validGrade("B+"));
        assertTrue(Lab28BuyerSummary.explain(null, false, true).contains("Lab 27"));
    }
}
