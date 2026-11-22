package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public final class ModemEsimPresentationPolicyTest {
    @Test public void unavailableEsimAndLiteralNoAreNotPositive() {
        assertFalse(ModemEsimPresentationPolicy.isPositiveEvidence("eSIM hardware capability", "NO"));
        assertFalse(ModemEsimPresentationPolicy.isPositiveEvidence("eSIM hardware capability", "No"));
        assertFalse(ModemEsimPresentationPolicy.isPositiveEvidence("eSIM hardware capability", "Not declared by Android (not proof of physical absence)"));
        assertFalse(ModemEsimPresentationPolicy.isPositiveEvidence("eSIM service available", "Disabled / unavailable (not proof of hardware absence)"));
        assertFalse(ModemEsimPresentationPolicy.isPositiveEvidence("eSIM service available", "Unavailable / not reported"));
        assertFalse(ModemEsimPresentationPolicy.isPositiveEvidence("Active eSIM profiles", "0 in Android-visible active subscriptions"));
        assertFalse(ModemEsimPresentationPolicy.isPositiveEvidence("Active subscription types", "0 eSIM / 1 non-eSIM (not physical-tray verification)"));
        assertFalse(ModemEsimPresentationPolicy.isPositiveEvidence("SIM slot interpretation", "Android logical slot; not physical tray verification"));
        assertFalse(ModemEsimPresentationPolicy.isPositiveEvidence("eSIM hardware capability", "Unknown / unavailable from Android"));
    }

    @Test public void explicitAndroidEsimEvidenceCanBePositive() {
        assertTrue(ModemEsimPresentationPolicy.isPositiveEvidence("eSIM hardware capability", "Supported (Android eUICC feature declared)"));
        assertTrue(ModemEsimPresentationPolicy.isPositiveEvidence("eSIM service available", "Enabled (does not imply an active eSIM plan)"));
        assertTrue(ModemEsimPresentationPolicy.isPositiveEvidence("Active eSIM profiles", "1 active eSIM subscription(s) reported"));
        assertTrue(ModemEsimPresentationPolicy.isPositiveEvidence("Active subscription types", "1 eSIM / 0 non-eSIM (not physical-tray verification)"));
        assertFalse(ModemEsimPresentationPolicy.isPositiveEvidence("Active subscription types", "0 eSIM / 2 non-eSIM (not physical-tray verification)"));
        assertFalse(ModemEsimPresentationPolicy.isPositiveEvidence("Other modem field", "Yes"));
    }
}
