package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/** Human-first iPhone evidence summary: no fake hardware-health certification. */
public final class IPhonePanicServiceSummaryTest {
    private IPhonePanicServiceSummary.Result result(boolean greek, int total,
            int watchdog, int thermal, boolean repeated, boolean known) {
        return IPhonePanicServiceSummary.interpret(
                greek, total, watchdog, thermal, repeated, known);
    }

    @Test public void twoDifferentDemoEventsGetClearHumanSummary() {
        IPhonePanicServiceSummary.Result r = result(true,2,1,1,false,true);
        assertTrue(r.whatWeFound.contains("δύο"));
        assertTrue(r.whatWeFound.contains("Watchdog"));
        assertTrue(r.whatWeFound.contains("Thermal"));
        assertTrue(r.whatItMeans.contains("Δεν εντοπίστηκε επανάληψη"));
        assertTrue(r.repairAdvice.contains("δεν προκύπτει λόγος επισκευής μόνο από αυτά τα logs"));
        assertTrue(r.repairAdvice.contains("δεν έχει μετρηθεί"));
        assertTrue(r.whenToSeekHelp.contains("επανεκκινήσεις"));
        assertTrue(r.whenToSeekHelp.contains("διακόπτεις χρήση και φόρτιση"));
        assertFalse(r.repeatedClueNeedsReview);
    }

    @Test public void repeatedDomainIsReviewedNotDiagnosedAsDamaged() {
        IPhonePanicServiceSummary.Result r = result(true,2,2,0,true,true);
        assertTrue(r.whatItMeans.contains("ίδια κατηγορία"));
        assertTrue(r.whatItMeans.contains("δεν αποδεικνύει επαναλαμβανόμενη βλάβη"));
        assertTrue(r.repeatedClueNeedsReview);
        assertTrue(r.repairAdvice.contains("δεν τεκμηριώνουν χαλασμένο εξάρτημα"));
        assertFalse(r.repairAdvice.contains("υγιές iPhone"));
    }

    @Test public void singleFileCannotSupportRecurrenceConclusion() {
        IPhonePanicServiceSummary.Result r = result(true,1,1,0,false,true);
        assertTrue(r.whatWeFound.contains("ένα εισαγόμενο"));
        assertTrue(r.whatItMeans.contains("Με ένα μόνο αρχείο"));
        assertFalse(r.whatItMeans.contains("Δεν εντοπίστηκε επανάληψη"));
        assertFalse(r.repeatedClueNeedsReview);
    }

    @Test public void unidentifiedDomainsDoNotCreateFalseReassurance() {
        IPhonePanicServiceSummary.Result r = result(true,2,0,0,false,false);
        assertTrue(r.whatWeFound.contains("Δεν αναγνωρίστηκε"));
        assertTrue(r.whatItMeans.contains("δεν προσδιορίστηκαν επαρκώς"));
        assertFalse(r.whatItMeans.contains("Δεν εντοπίστηκε επανάληψη"));
    }

    @Test public void englishOutputPreservesUnprovenCurrentHealth() {
        IPhonePanicServiceSummary.Result r = result(false,2,1,1,false,true);
        assertTrue(r.whatWeFound.contains("Watchdog"));
        assertTrue(r.whatWeFound.contains("Thermal"));
        assertTrue(r.whatItMeans.contains("historical events"));
        assertTrue(r.repairAdvice.contains("present operating condition was not measured"));
        assertTrue(r.repairAdvice.contains("logs alone do not justify a repair"));
        assertTrue(r.whenToSeekHelp.contains("uncomfortably hot"));
    }

    @Test public void ordinaryLogsDoNotGetThermalContactAdviceWithoutThermalEvidence() {
        IPhonePanicServiceSummary.Result r = result(true,2,2,0,false,true);
        assertFalse(r.whenToSeekHelp.contains("διακόπτεις χρήση και φόρτιση"));
        assertTrue(r.whenToSeekHelp.contains("τεχνικό έλεγχο"));
    }

    @Test public void inputsDoNotInflateKnownIncidentsBeyondFiles() {
        IPhonePanicServiceSummary.Result r = result(true,2,99,99,false,true);
        assertTrue(r.whatWeFound.contains("2 αρχεία αναφέρουν μη απόκριση"));
        assertFalse(r.whatWeFound.contains("99"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void emptyInputCannotBeReportedAsReviewed() {
        result(true,0,0,0,false,false);
    }
}
