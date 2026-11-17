package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Lab14A: 40% recovery ratio alone is not evidence of voltage collapse. */
public final class Lab14VoltageDropEvidenceTest {
    @Test public void screenshotVoltageReadingsDoNotFlagCollapseWithoutCorroboration() {
        // 0.043 / 0.116 = 37.1%; hypothetical 2A proxy 0.058 ohm.
        assertFalse(Lab14VoltageDropEvidence.hasCorroboratedConcern(
                0.116f, 0.043f, 0.058f));
    }

    @Test public void screenshotRunWithAcceptableThermalRiseGetsConditionalPositiveVerdict() {
        boolean voltageConcern = Lab14VoltageDropEvidence.hasCorroboratedConcern(
                0.116f, 0.043f, 0.058f);
        Lab14HumanVerdict.Decision d = Lab14HumanVerdict.evaluate(true, "Weak",
                true, false, voltageConcern, false, false, false, 3.0f, 40.0f);
        assertEquals(Lab14HumanVerdict.Tone.POSITIVE, d.tone);
        assertTrue(d.message.contains("ενδείξεις φυσιολογικής φθοράς"));
        assertTrue(d.message.contains("Μπορείς να συνεχίσεις"));
        assertTrue(d.message.contains("εφόσον"));
    }

    @Test public void weakAbsoluteRecoveryPlusHighSagRemainsWarning() {
        assertTrue(Lab14VoltageDropEvidence.hasCorroboratedConcern(
                0.116f, 0.020f, 0.058f));
    }

    @Test public void highDynamicResistanceAndSagRemainsWarning() {
        assertTrue(Lab14VoltageDropEvidence.hasCorroboratedConcern(
                0.130f, 0.043f, 0.110f));
    }

    @Test public void lowRecoveryRatioAloneDoesNotOverrideExistingRule() {
        // Recovery ratio is 37%, but absolute recovery is adequate (43mV).
        assertFalse(Lab14VoltageDropEvidence.hasCorroboratedConcern(
                0.116f, 0.043f, 0.058f));
        // Without a second independent indicator the ratio must not be elevated.
        assertFalse(Lab14VoltageDropEvidence.hasCorroboratedConcern(
                0.040f, 0.013f, 0.058f));
    }

    @Test public void badElectricalInputsCannotBePromotedIntoRisk() {
        assertFalse(Lab14VoltageDropEvidence.hasCorroboratedConcern(
                0.116f, 0.043f, Float.NaN));
        assertFalse(Lab14VoltageDropEvidence.hasCorroboratedConcern(
                Float.NaN, 0.043f, 0.058f));
    }

    @Test public void manualLabUsesEvidenceGateNotTheSingleRatioOverride() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        String s = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
        assertTrue(s.contains("Lab14VoltageDropEvidence.hasCorroboratedConcern("));
        assertFalse(s.contains("recoveryRatio < 0.4f"));
        assertTrue(s.contains("lab14_collapse_risk"));
        assertTrue(s.contains("lab14LogFinalScore("));
    }
}
