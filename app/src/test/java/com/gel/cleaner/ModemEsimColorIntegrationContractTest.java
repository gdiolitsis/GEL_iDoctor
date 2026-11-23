package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

public final class ModemEsimColorIntegrationContractTest {
    static String read(String name) throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/" + name);
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/" + name);
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void peripheralsColorsOnlyRealPositiveEsimValuesGreen() throws Exception {
        String s = read("DeviceInfoPeripheralsActivity.java");
        int a = s.indexOf("private void applyNeonValues(TextView tv, String text)");
        int b = s.indexOf("// HELPERS — alignment", a);
        assertTrue(a >= 0 && b > a);
        s = s.substring(a, b);
        assertTrue(s.contains("ModemEsimPresentationPolicy.isEsimField(field)"));
        assertTrue(s.contains("ModemEsimPresentationPolicy.isPositiveEvidence(field, value)"));
        assertTrue(s.contains("? neon : Color.WHITE"));
        assertTrue(s.contains(": neon;"));
        assertTrue(s.contains("new ForegroundColorSpan(rowColor)"));
    }

    @Test public void lab12SharesSameEsimPolicy() throws Exception {
        String s = read("ManualTestsActivity.java");
        int a = s.indexOf("private void lab12ReportSharedModemInventory(boolean gr)");
        int b = s.indexOf("private void lab12ReportActiveSubscriptionTypes(boolean gr)", a);
        assertTrue(a >= 0 && b > a);
        s = s.substring(a, b);
        assertTrue(s.contains("ModemEsimPresentationPolicy.isEsimField(label)"));
        assertTrue(s.contains("!ModemEsimPresentationPolicy.isPositiveEvidence(label, value)"));
        assertTrue(s.contains("unknown || neutralContext || nonPositiveEsim"));
        assertTrue(s.contains("Lab12EsimGreekFormatter.label(label)"));
        assertTrue(s.contains("Lab12EsimGreekFormatter.value(value)"));
        assertTrue(s.contains("logLabelWarnValue(displayLabel, displayValue)"));
        assertTrue(s.contains("logLabelValue(displayLabel, displayValue)"));
        assertTrue(s.contains("logLabelOkValue(displayLabel, displayValue)"));
    }
}
