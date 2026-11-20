package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

/** Regression: conditional lay-language battery guidance must not overstate stress evidence. */
public final class Lab14HumanVerdictTest {
    private static Lab14HumanVerdict.Decision goodWeak() {
        return Lab14HumanVerdict.evaluate(true, "Weak", true, false,
                false, false, false, false, 3.0f, 40.0f);
    }

    @Test public void weakWithNormalThermalAndNoOtherFlagsCanContinueConditionally() {
        Lab14HumanVerdict.Decision d = goodWeak();
        assertEquals(Lab14HumanVerdict.Tone.POSITIVE, d.tone);
        assertTrue(d.message.contains("αυξημένη πτώση τάσης"));
        assertTrue(d.message.contains("Μπορείς να συνεχίσεις"));
        assertTrue(d.message.contains("εφόσον"));
        assertTrue(d.message.contains("Δεν προκύπτει προς το παρόν ανάγκη αντικατάστασης"));
        assertTrue(d.message.contains("ενδείξεις φυσιολογικής φθοράς"));
    }

    @Test public void severeSagNeverGetsGreenReassurance() {
        assertEquals(Lab14HumanVerdict.Tone.CAUTION,
                Lab14HumanVerdict.evaluate(true, "Critical", true, false,
                        false, false, false, false, 3f, 40f).tone);
    }

    @Test public void independentConcernFlagsTakePriorityOverWeakClass() {
        for (int i = 0; i < 4; i++) {
            Lab14HumanVerdict.Decision d = Lab14HumanVerdict.evaluate(true, "Weak",
                    true, false, i == 0, i == 1, i == 2, i == 3, 3f, 40f);
            assertEquals(Lab14HumanVerdict.Tone.CAUTION, d.tone);
            assertFalse(d.message.contains("Μπορείς να συνεχίσεις"));
        }
    }

    @Test public void elevatedTemperaturePreventsReassurance() {
        assertEquals(Lab14HumanVerdict.Tone.CAUTION,
                Lab14HumanVerdict.evaluate(true, "Weak", true, false,
                        false, false, false, false, 7f, 42f).tone);
        assertEquals(Lab14HumanVerdict.Tone.CAUTION,
                Lab14HumanVerdict.evaluate(true, "Normal", true, false,
                        false, false, false, false, 3f, 46f).tone);
    }

    @Test public void largeRiseAtNormalAbsoluteTemperatureIsNotCalledOverheating() {
        Lab14HumanVerdict.Decision d = Lab14HumanVerdict.evaluate(
                true, "Normal", true, false, false, false, false, false, 9f, 35f);
        assertEquals(Lab14HumanVerdict.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("μεγάλη άνοδος"));
        assertTrue(d.message.contains("όχι όμως υψηλή απόλυτη θερμοκρασία"));
        assertFalse(d.message.contains("τεχνική εκτίμηση πριν"));
    }

    @Test public void highPeakTemperatureIsSeparateFromRise() {
        Lab14HumanVerdict.Decision d = Lab14HumanVerdict.evaluate(
                true, "Normal", true, false, false, false, false, false, 2f, 46f);
        assertEquals(Lab14HumanVerdict.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("μέγιστη θερμοκρασία"));
        assertTrue(d.message.contains("αυξημένη"));
        assertFalse(d.message.contains("μεγάλη άνοδος"));
    }

    @Test public void trulyHighAbsoluteTemperatureRetainsStrongPrecaution() {
        Lab14HumanVerdict.Decision d = Lab14HumanVerdict.evaluate(
                true, "Normal", true, false, false, false, false, false, 3f, 52f);
        assertEquals(Lab14HumanVerdict.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("Άφησε τη συσκευή να κρυώσει"));
        assertTrue(d.message.contains("διέκοψε τη χρήση"));
    }

    @Test public void severeVoltageSagAloneRequestsRetestBeforeConditionalReferral() {
        Lab14HumanVerdict.Decision d = Lab14HumanVerdict.evaluate(
                true, "Critical", true, false, false, false, false, false, 9f, 35f);
        assertEquals(Lab14HumanVerdict.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("Επανάλαβε σε συγκρίσιμες συνθήκες"));
        assertTrue(d.message.contains("αν το εύρημα επιμένει ή εμφανίζονται συμπτώματα"));
        assertFalse(d.message.contains("τεχνική εκτίμηση πριν"));
    }

    @Test public void englishReportDoesNotConflateRiseWithHighPeak() {
        Lab14HumanVerdict.Decision d = Lab14HumanVerdict.evaluate(
                false, "Normal", true, false, false, false, false, false, 9f, 35f);
        assertEquals(Lab14HumanVerdict.Tone.CAUTION, d.tone);
        assertTrue(d.message.contains("large temperature rise"));
        assertTrue(d.message.contains("absolute battery temperature was not high"));
    }

    @Test public void unknownThermalOrIncompleteEvidenceIsNeutral() {
        assertEquals(Lab14HumanVerdict.Tone.NEUTRAL,
                Lab14HumanVerdict.evaluate(true, "Weak", true, false,
                        false, false, false, false, Float.NaN, Float.NaN).tone);
        assertEquals(Lab14HumanVerdict.Tone.NEUTRAL,
                Lab14HumanVerdict.evaluate(true, "Weak", false, false,
                        false, false, false, false, 3f, 39f).tone);
        assertEquals(Lab14HumanVerdict.Tone.NEUTRAL,
                Lab14HumanVerdict.evaluate(true, "Unknown", true, false,
                        false, false, false, false, 3f, 39f).tone);
        assertEquals(Lab14HumanVerdict.Tone.NEUTRAL,
                Lab14HumanVerdict.evaluate(true, "Normal", true, true,
                        false, false, false, false, 3f, 39f).tone);
    }

    @Test public void normalAndEnglishVerdictsRemainConditional() {
        Lab14HumanVerdict.Decision normal = Lab14HumanVerdict.evaluate(true, "Normal",
                true, false, false, false, false, false, 2f, 38f);
        assertEquals(Lab14HumanVerdict.Tone.POSITIVE, normal.tone);
        assertTrue(normal.message.contains("εφόσον"));
        Lab14HumanVerdict.Decision en = Lab14HumanVerdict.evaluate(false, "Weak",
                true, false, false, false, false, false, 3f, 40f);
        assertEquals(Lab14HumanVerdict.Tone.POSITIVE, en.tone);
        assertTrue(en.message.contains("You can continue normal use provided"));
        assertFalse(en.message.contains("safe without conditions"));
    }
}
