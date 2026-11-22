package com.gel.cleaner;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/** Lab 12: display Android-visible physical SIM/eSIM per logical slot; real call is opt-in. */
public final class Lab12SimEsimSubscriptionContractTest {
    private static String source() throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        if (!Files.isRegularFile(p))
            p = Paths.get("src/main/java/com/gel/cleaner/ManualTestsActivity.java");
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String section(String s, String start, String end) {
        int a = s.indexOf(start);
        int b = s.indexOf(end, a + start.length());
        assertTrue("Lab 12 section missing: " + start, a >= 0 && b > a);
        return s.substring(a, b);
    }

    @Test public void lab12ReadsActiveSubscriptionTypeBeforeOtherPhoneStateChecks() throws Exception {
        String s = source();
        String method = section(s, "private void lab12CallFunctionInterpretation() {",
                "private void lab12ShowDialog(");
        assertTrue(method.contains("lab12ReportActiveSubscriptionTypes(gr);"));
        assertTrue(method.indexOf("lab12ReportActiveSubscriptionTypes(gr);")
                < method.indexOf("if (s.airplaneOn)"));
        assertTrue(method.contains("runOnUiThread(this::lab12ShowCallConsent);"));
    }

    @Test public void embeddedAndNonEmbeddedTypesComeFromActiveSubscriptionsNotSlotNumber() throws Exception {
        String s = source();
        String reader = section(s, "private void lab12ReportActiveSubscriptionTypes(boolean gr) {",
                "// ============================================================\n// LAB 12 — Call Function Interpretation");
        assertTrue(reader.contains("manager.getActiveSubscriptionInfoList()"));
        assertTrue(reader.contains("subscription.getSimSlotIndex()"));
        assertTrue(reader.contains("subscription.isEmbedded()"));
        assertTrue(reader.contains("eSIM — ενεργή συνδρομή"));
        assertTrue(reader.contains("Φυσική SIM — ενεργή μη ενσωματωμένη συνδρομή"));
        assertTrue(reader.contains("λογική αντιστοίχιση Android"));
        assertFalse(reader.contains("getAvailableSubscriptionInfoList"));
        assertFalse(reader.contains("getIccId("));
        assertFalse(reader.contains("getEid("));
        assertFalse(reader.contains("getPhoneNumber("));
    }

    @Test public void unavailableApiOrPermissionNeverBecomesUnfoundedPhysicalSimFinding() throws Exception {
        String s = source();
        String reader = section(s, "private void lab12ReportActiveSubscriptionTypes(boolean gr) {",
                "// ============================================================\n// LAB 12 — Call Function Interpretation");
        assertTrue(reader.contains("Build.VERSION.SDK_INT < Build.VERSION_CODES.P"));
        assertTrue(reader.contains("Manifest.permission.READ_PHONE_STATE"));
        assertTrue(reader.contains("δεν δόθηκε άδεια"));
        assertTrue(reader.contains("Δεν εμφανίζονται στοιχεία ενεργής συνδρομής"));
        assertTrue(reader.contains("catch (SecurityException denied)"));
        assertTrue(reader.contains("Άγνωστος"));
    }

    @Test public void callStillRequiresExplicitOptInAndUserReportedAudio() throws Exception {
        String s = source();
        String prompt = section(s, "private void lab12ShowCallConsent() {",
                "private void lab12OpenDialer() {");
        assertTrue(prompt.contains("Προαιρετική πραγματική κλήση"));
        assertTrue(prompt.contains("this::lab12OpenDialer"));
        assertTrue(prompt.contains("ΠΑΡΑΛΕΙΨΗ"));
        String dial = section(s, "private void lab12OpenDialer() {",
                "private void lab12AfterDialerReturn() {");
        assertTrue(dial.contains("Intent.ACTION_DIAL"));
        assertFalse(dial.contains("Intent.ACTION_CALL"));
        String reply = section(s, "private void lab12AfterDialerReturn() {",
                "private void lab12ReportGuided(");
        assertTrue(reply.contains("Πραγματοποίησες και ολοκλήρωσες πραγματική κλήση;"));
        assertTrue(reply.contains("Ο συνομιλητής επιβεβαίωσε ότι σε άκουγε;"));
    }
}
