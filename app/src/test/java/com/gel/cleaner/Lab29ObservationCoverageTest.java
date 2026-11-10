package com.gel.cleaner;
import org.junit.Test;
import static org.junit.Assert.*;
public class Lab29ObservationCoverageTest {
    @Test public void unversionedOrLegacyRecordsAreNotCurrent() {
        assertFalse(Lab29ObservationCoverage.isFresh(0, 1000, 2000));
        assertFalse(Lab29ObservationCoverage.isFresh(1, 1000, 2000));
    }
    @Test public void freshObservationCanBeDescribedAsSuchOnly() {
        assertTrue(Lab29ObservationCoverage.isFresh(2, 1000, 2000));
    }
    @Test public void absentFutureAndStaleAreNotAccepted() {
        assertFalse(Lab29ObservationCoverage.isFresh(2, 0, 2000));
        assertFalse(Lab29ObservationCoverage.isFresh(2, 3000, 2000));
        assertFalse(Lab29ObservationCoverage.isFresh(2, 1000, 7201001));
    }
}
