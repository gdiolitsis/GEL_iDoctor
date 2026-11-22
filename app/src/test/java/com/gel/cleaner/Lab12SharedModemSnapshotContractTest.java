package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/** Lab 12 reuses actual Telephony / Modem evidence without implying a real call. */
public final class Lab12SharedModemSnapshotContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void samePeripheralsModemCollectorIsUsedBeforeVoiceConclusions() throws Exception {
        String src = source();
        int method = src.indexOf("private void lab12ReportSharedModemInventory(boolean gr) {");
        int end = src.indexOf("private void lab12ReportActiveSubscriptionTypes(boolean gr)", method);
        assertTrue(method >= 0 && end > method);
        String block = src.substring(method, end);
        assertTrue(block.contains("DeviceInfoPeripheralsActivity.collectRemoteSection(this, \"MODEM\")"));
        assertTrue(block.contains("logLabelOkValue(label, value)"));
        assertTrue(block.contains("logLabelValue(label, value)"));
        assertTrue(block.contains("logLabelWarnValue(label, value)"));
        assertTrue(block.contains("\"Advanced\".equals(label)"));
        assertTrue(block.contains("unknown") && block.contains("SIM slot interpretation"));
        assertFalse(block.contains("getIccId("));
        assertFalse(block.contains("getEid("));
        assertFalse(block.contains("getPhoneNumber("));

        int report = src.indexOf("private void lab12CallFunctionInterpretation() {");
        int bodyEnd = src.indexOf("private void lab12ShowDialog(", report);
        assertTrue(report >= 0 && bodyEnd > report);
        String callBody = src.substring(report, bodyEnd);
        assertTrue(callBody.indexOf("lab12ReportSharedModemInventory(gr);")
                < callBody.indexOf("lab12ReportActiveSubscriptionTypes(gr);"));
        assertTrue(callBody.indexOf("lab12ReportActiveSubscriptionTypes(gr);")
                < callBody.indexOf("if (s.airplaneOn)"));
        assertTrue(callBody.contains("runOnUiThread(this::lab12ShowCallConsent);"));
    }
}
