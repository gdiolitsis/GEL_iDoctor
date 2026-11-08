package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab11NetworkEvidenceTest {
    @Test public void wifiValidatedDoesNotProveCellular() {
        Lab11NetworkEvidence.DefaultRoute r = Lab11NetworkEvidence.classifyDefault(
                true, true, true, true, false, true, false);
        assertEquals(Lab11NetworkEvidence.DefaultRoute.WIFI_VALIDATED, r);
        assertFalse(Lab11NetworkEvidence.provesCurrentDefaultCellularValidation(r));
    }

    @Test public void validatedCellularIsScopedDefaultRouteOnly() {
        Lab11NetworkEvidence.DefaultRoute r = Lab11NetworkEvidence.classifyDefault(
                true, true, true, true, true, false, false);
        assertEquals(Lab11NetworkEvidence.DefaultRoute.CELLULAR_VALIDATED, r);
        assertTrue(Lab11NetworkEvidence.provesCurrentDefaultCellularValidation(r));
        assertFalse(Lab11NetworkEvidence.isHardwareFailureProven(r, true, true, false));
    }

    @Test public void internetCapabilityIsNotValidatedConnectivity() {
        assertEquals(Lab11NetworkEvidence.DefaultRoute.CELLULAR_NOT_VALIDATED,
                Lab11NetworkEvidence.classifyDefault(
                        true, true, true, false, true, false, false));
    }

    @Test public void validationWithoutAdvertisedInternetCannotProveReachability() {
        assertEquals(Lab11NetworkEvidence.DefaultRoute.CELLULAR_NOT_VALIDATED,
                Lab11NetworkEvidence.classifyDefault(
                        true, true, false, true, true, false, false));
    }

    @Test public void vpnOverCellularCannotBeAttributedToPhysicalRadio() {
        Lab11NetworkEvidence.DefaultRoute r = Lab11NetworkEvidence.classifyDefault(
                true, true, true, true, true, false, true);
        assertEquals(Lab11NetworkEvidence.DefaultRoute.VPN_VALIDATED_UNATTRIBUTED, r);
        assertFalse(Lab11NetworkEvidence.provesCurrentDefaultCellularValidation(r));
    }

    @Test public void mixedTransportDoesNotGetFalseCellularCertification() {
        Lab11NetworkEvidence.DefaultRoute r = Lab11NetworkEvidence.classifyDefault(
                true, true, true, true, true, true, false);
        assertEquals(Lab11NetworkEvidence.DefaultRoute.OTHER_VALIDATED, r);
        assertFalse(Lab11NetworkEvidence.provesCurrentDefaultCellularValidation(r));
    }

    @Test public void missingNetworkAndMissingCapabilitiesRemainUnknown() {
        assertEquals(Lab11NetworkEvidence.DefaultRoute.NO_DEFAULT_NETWORK,
                Lab11NetworkEvidence.classifyDefault(
                        false, false, false, false, false, false, false));
        assertEquals(Lab11NetworkEvidence.DefaultRoute.CAPABILITIES_UNAVAILABLE,
                Lab11NetworkEvidence.classifyDefault(
                        true, false, false, false, false, false, false));
    }

    @Test public void failedServiceReadNeverDefaultsToOutOfService() {
        assertFalse(Lab11NetworkEvidence.isServiceObservationKnown(true, false));
        assertFalse(Lab11NetworkEvidence.isServiceObservationKnown(false, false));
        assertTrue(Lab11NetworkEvidence.isServiceObservationKnown(true, true));
    }

    @Test public void simNotReadyDoesNotEstablishDefectiveModem() {
        Lab11NetworkEvidence.DefaultRoute r = Lab11NetworkEvidence.classifyDefault(
                true, true, true, false, true, false, false);
        assertFalse(Lab11NetworkEvidence.isHardwareFailureProven(r, false, false, false));
    }

    @Test public void noDefaultOrUnvalidatedRouteCannotProveRadioFailure() {
        assertFalse(Lab11NetworkEvidence.isHardwareFailureProven(
                Lab11NetworkEvidence.DefaultRoute.NO_DEFAULT_NETWORK,
                true, true, false));
        assertFalse(Lab11NetworkEvidence.isHardwareFailureProven(
                Lab11NetworkEvidence.DefaultRoute.CELLULAR_NOT_VALIDATED,
                true, true, false));
    }
}
