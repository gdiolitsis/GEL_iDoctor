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
}
