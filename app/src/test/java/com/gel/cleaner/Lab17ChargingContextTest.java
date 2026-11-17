package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/** Cross-lab evidence matrix: accessory type is self-report, not diagnostic proof. */
public final class Lab17ChargingContextTest {
    private Lab17ChargingContext.Decision assess(String charger, String rate,
                                                  boolean heat, boolean disconnected,
                                                  boolean systemLimited) {
        return Lab17ChargingContext.evaluate(true, charger, rate, null,
                heat, disconnected, systemLimited);
    }

    @Test public void lowRateWithRegularChargerIsContextNotPhoneFault() {
        for (String rate : new String[] {"MODERATE", "LOW"}) {
            Lab17ChargingContext.Decision d=assess("NO", rate, false,false,false);
            assertTrue(d.slowNetRate);
            assertFalse(d.independentConcern);
            assertFalse(d.slowRateNeedsReview);
            assertFalse(d.chargingConcern);
            assertFalse(d.unknownChargerWithSlowRate);
            assertTrue(d.description.contains("απλό φορτιστή"));
            assertTrue(d.description.contains("Δεν αποδεικνύει βλάβη"));
            assertFalse(d.description.contains("βλάβη του φορτιστή αποδείχθηκε"));
        }
    }

    @Test public void fastChargerClaimRequiresCheckNotFaultAttribution() {
        Lab17ChargingContext.Decision d=assess("YES","LOW",false,false,false);
        assertTrue(d.slowNetRate);
        assertFalse(d.independentConcern);
        assertTrue(d.slowRateNeedsReview);
        assertTrue(d.chargingConcern);
        assertFalse(d.unknownChargerWithSlowRate);
        assertTrue(d.description.contains("δήλωσε ταχυφορτιστή"));
        assertTrue(d.description.contains("δεν επιβεβαιώθηκε"));
        assertTrue(d.description.contains("δεν αποδεικνύει βλάβη"));
    }

    @Test public void unknownChargerSlowRateRestrictsPositiveCoverage() {
        for (String charger : new String[] {"UNKNOWN",null,"","FAST?","MAYBE"}) {
            Lab17ChargingContext.Decision d=assess(charger,"LOW",false,false,false);
            assertTrue(d.slowNetRate);
            assertFalse(d.chargingConcern);
            assertTrue(d.unknownChargerWithSlowRate);
            assertTrue(d.description.contains("δεν είναι γνωστός"));
        }
    }

    @Test public void actualHeatDisconnectionsOrSystemLimitOverrideEveryCharger() {
        int cases=0;
        for (String charger : new String[] {"YES","NO","UNKNOWN"}) {
            for (String rate : new String[] {"HIGH","TYPICAL","MODERATE","LOW"}) {
                for(int flags=1;flags<8;flags++) {
                    Lab17ChargingContext.Decision d=assess(charger,rate,
                            (flags & 1)!=0,(flags & 2)!=0,(flags & 4)!=0);
                    assertTrue("case "+cases,d.independentConcern);
                    assertTrue("case "+cases,d.chargingConcern);
                    cases++;
                }
            }
        }
        assertEquals(84,cases);
    }

    @Test public void normalChargeIsNotConvertedToSlowBySelfReport() {
        for (String charger : new String[] {"YES","NO","UNKNOWN"}) {
            for (String rate : new String[] {"HIGH","TYPICAL"}) {
                Lab17ChargingContext.Decision d=assess(charger,rate,false,false,false);
                assertFalse(d.slowNetRate);
                assertFalse(d.chargingConcern);
                assertFalse(d.unknownChargerWithSlowRate);
                assertEquals("",d.description);
            }
        }
    }

    @Test public void oldWeakClassificationKeepsUnknownMeasurementProvenance() {
        Lab17ChargingContext.Decision d=Lab17ChargingContext.evaluate(true,"UNKNOWN",
                null,"WEAK",false,false,false);
        assertTrue(d.slowNetRate);
        assertTrue(d.unknownChargerWithSlowRate);
        assertFalse(d.chargingConcern);
        Lab17ChargingContext.Decision absent=Lab17ChargingContext.evaluate(true,
                "YES",null,null,false,false,false);
        assertFalse(absent.slowNetRate);
        assertFalse(absent.chargingConcern);
    }

    @Test public void englishInterpretationPreservesNoFaultConclusion() {
        Lab17ChargingContext.Decision d=Lab17ChargingContext.evaluate(false,"NO",
                "LOW",null,false,false,false);
        assertTrue(d.description.contains("does not prove a phone defect"));
        assertTrue(d.description.contains("regular charger"));
    }
    @Test public void regularChargerContextIsResolvedByLab15ButNeverRepeatedByLab17() {
        Lab17ChargingContext.Decision lab15 = assess("NO","LOW",false,false,false);
        String saved = Lab17ChargingContext.resultCode(lab15,true);
        assertEquals("SLOW_NO_FAULT_ESTABLISHED",saved);
        Lab17ChargingContext.Decision lab17 = Lab17ChargingContext.fromLab15Result(
                saved,"LOW",null,false,false,false);
        assertTrue(lab17.slowNetRate);
        assertFalse(lab17.chargingConcern);
        assertFalse(lab17.unknownChargerWithSlowRate);
        assertEquals("",lab17.description);
    }

    @Test public void fastChargeReviewReachesLab17WithoutOriginalUserAnswer() {
        String saved = Lab17ChargingContext.resultCode(
                assess("YES","LOW",false,false,false),true);
        assertEquals("SLOW_REVIEW",saved);
        Lab17ChargingContext.Decision result = Lab17ChargingContext.fromLab15Result(
                saved,"LOW",null,false,false,false);
        assertTrue(result.slowRateNeedsReview);
        assertTrue(result.chargingConcern);
        assertEquals("",result.description);
    }

    @Test public void unknownAndLegacySlowRateNeverBecomeFalseGreenOrDeviceFault() {
        String saved = Lab17ChargingContext.resultCode(
                assess("UNKNOWN","LOW",false,false,false),true);
        assertEquals("SLOW_CONTEXT_UNKNOWN",saved);
        for(String outcome:new String[]{saved,null,"OLD_UNSUPPORTED","RATE_UNAVAILABLE"}) {
            Lab17ChargingContext.Decision result=Lab17ChargingContext.fromLab15Result(
                    outcome,"LOW",null,false,false,false);
            assertTrue(result.unknownChargerWithSlowRate);
            assertFalse(result.chargingConcern);
            assertFalse(result.independentConcern);
            assertEquals("",result.description);
        }
    }

    @Test public void actualWarningSurvivesAnyStoredClassificationOrMissingBoolean() {
        String[] codes={"SLOW_NO_FAULT_ESTABLISHED","NO_ISSUE_OBSERVED",
                "SLOW_REVIEW","SLOW_CONTEXT_UNKNOWN","RATE_UNAVAILABLE",
                "OBSERVED_WARNING",null};
        int checked=0;
        for(String code:codes) {
            for(int bits=1;bits<8;bits++) {
                Lab17ChargingContext.Decision result=Lab17ChargingContext.fromLab15Result(
                        code,"LOW",null,(bits&1)!=0,(bits&2)!=0,(bits&4)!=0);
                assertTrue(result.independentConcern);
                assertTrue(result.chargingConcern);
                checked++;
            }
        }
        assertEquals(49,checked);
        // Fail-safe if an independently confirmed prior warning was stored.
        Lab17ChargingContext.Decision stored=Lab17ChargingContext.fromLab15Result(
                "OBSERVED_WARNING","HIGH",null,false,false,false);
        assertTrue(stored.chargingConcern);
    }

    @Test public void noRateIsUnknownNotAutoSuccess() {
        Lab17ChargingContext.Decision lab15=assess("NO","UNKNOWN",false,false,false);
        assertEquals("RATE_UNAVAILABLE",Lab17ChargingContext.resultCode(lab15,false));
        Lab17ChargingContext.Decision lab17=Lab17ChargingContext.fromLab15Result(
                "RATE_UNAVAILABLE","UNKNOWN",null,false,false,false);
        assertFalse(lab17.chargingConcern);
        assertFalse(lab17.slowNetRate);
    }

}
