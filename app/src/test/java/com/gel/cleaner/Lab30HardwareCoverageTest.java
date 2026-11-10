package com.gel.cleaner;
import org.junit.Test;
import static org.junit.Assert.*;
public class Lab30HardwareCoverageTest {
    @Test public void noEvidenceDoesNotMakeAPlus() {
        assertFalse(Lab30HardwareCoverage.canGrade(false, false, false, false));
    }
    @Test public void oneGoodReadingIsNotWholeDeviceCertification() {
        assertFalse(Lab30HardwareCoverage.canGrade(true, false, false, false));
    }
    @Test public void eachMissingDomainBlocksGrades() {
        assertFalse(Lab30HardwareCoverage.canGrade(false, true, true, true));
        assertFalse(Lab30HardwareCoverage.canGrade(true, false, true, true));
        assertFalse(Lab30HardwareCoverage.canGrade(true, true, false, true));
        assertFalse(Lab30HardwareCoverage.canGrade(true, true, true, false));
    }
    @Test public void fullFourDomainEvidenceIsNecessaryForRating() {
        assertTrue(Lab30HardwareCoverage.canGrade(true, true, true, true));
    }
}
