package com.gel.cleaner;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;

/** Buyer-facing Lab 25 must not invent errors, recurrence, or a clean bill of health. */
public final class CriticalSystemCrashEvidenceTest {
    private static final Set<String> ROLES = new HashSet<>(Arrays.asList(
            "HOME=com.miui.home",
            "MESSAGING=com.google.android.apps.messaging",
            "DIALER=com.google.android.dialer"));

    @Test public void onlyImportantProcessesCountEvenWhenInstalledAsUserApps() {
        assertEquals("HOME", CriticalSystemCrashEvidence.criticalRole("com.miui.home", ROLES));
        assertEquals("MESSAGING", CriticalSystemCrashEvidence.criticalRole(
                "com.google.android.apps.messaging", ROLES));
        assertEquals("DIALER", CriticalSystemCrashEvidence.criticalRole(
                "com.google.android.dialer:service", ROLES));
        assertEquals("SYSTEM_UI", CriticalSystemCrashEvidence.criticalRole("com.android.systemui", ROLES));
        assertEquals("ANDROID_CORE", CriticalSystemCrashEvidence.criticalRole("system_server", ROLES));
        assertNull(CriticalSystemCrashEvidence.criticalRole("com.openai.chatgpt", ROLES));
        assertNull(CriticalSystemCrashEvidence.criticalRole("com.other.game", ROLES));
    }

    @Test public void dropboxTagAloneNeverTurnsOrdinaryAppCrashIntoSystemFault() {
        assertNull(CriticalSystemCrashEvidence.dropBoxEvent(
                "data_app_crash", 1000L, "Process: com.openai.chatgpt", ROLES));
        assertNull(CriticalSystemCrashEvidence.dropBoxEvent(
                "system_app_crash", 1000L, "generic error with no process attribution", ROLES));
        assertNull(CriticalSystemCrashEvidence.dropBoxEvent(
                "system_server_wtf", 1000L, "Process: system_server", ROLES));
        assertEquals("HOME", CriticalSystemCrashEvidence.dropBoxEvent(
                "data_app_crash", 1000L, "Process: com.miui.home", ROLES).role);
        assertEquals("ANDROID_CORE", CriticalSystemCrashEvidence.dropBoxEvent(
                "system_server_crash", 1000L, "", ROLES).role);
    }

    @Test public void selectedReportNeedsDirectFatalOrAnrEvidence() throws Exception {
        String sample = "11-16 10:04:05.000 E AndroidRuntime: FATAL EXCEPTION: main\n"
                + "11-16 10:04:05.000 E AndroidRuntime: Process: com.miui.home, PID: 100\n"
                + "11-16 10:04:08.000 E ActivityManager: ANR in com.google.android.apps.messaging\n"
                + "11-16 10:04:09.000 E AndroidRuntime: FATAL EXCEPTION: main\n"
                + "11-16 10:04:09.000 E AndroidRuntime: Process: com.openai.chatgpt, PID: 101\n"
                + "11-16 10:04:10.000 I System: Activity resumed com.android.systemui\n";
        CriticalSystemCrashEvidence.Summary s = report(sample);
        assertEquals(2, s.events.size());
        assertEquals("HOME", s.events.get(0).role);
        assertEquals("CRASH", s.events.get(0).kind);
        assertEquals("MESSAGING", s.events.get(1).role);
        assertEquals("ANR", s.events.get(1).kind);
        assertFalse(s.hasVerifiedRecurrence());
    }

    @Test public void repeatedTimestampedCrashesAreDistinguishedFromOneRareCrash() throws Exception {
        String repeated = "11-16 10:00:00.000 E AndroidRuntime: FATAL EXCEPTION: main\n"
                + "Process: com.android.systemui, PID: 10\n"
                + "11-16 10:01:00.000 E AndroidRuntime: FATAL EXCEPTION: main\n"
                + "Process: com.android.systemui, PID: 11\n";
        CriticalSystemCrashEvidence.Summary s = report(repeated);
        assertEquals(2, s.events.size());
        assertTrue(s.hasVerifiedRecurrence());
        String single = "FATAL EXCEPTION: main\nProcess: com.android.systemui\n";
        assertEquals(1, report(single).events.size());
        assertFalse(report(single).hasVerifiedRecurrence());
    }

    @Test public void backgroundRestartOrMentionAloneIsNotACrash() throws Exception {
        assertEquals(0, report("Process: com.android.systemui\n"
                + "11-16 10:00:00.000 I am_proc_died: [com.miui.home]\n"
                + "User said System UI crashed yesterday\n"
                + "kill background com.google.android.apps.messaging\n").events.size());
    }

    @Test public void userSelectedZipCanContainBugreportWithSystemCrash() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes)) {
            zip.putNextEntry(new ZipEntry("version.txt"));
            zip.write("Android 13".getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
            zip.putNextEntry(new ZipEntry("bugreport-device-2026-11-16.txt"));
            zip.write(("11-16 12:03:05.000 E AndroidRuntime: FATAL EXCEPTION: main\n"
                    + "Process: com.android.systemui, PID: 36\n").getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
        CriticalSystemCrashEvidence.Summary s = CriticalSystemCrashEvidence.parseUserReport(
                new ByteArrayInputStream(bytes.toByteArray()), ROLES);
        assertEquals(1, s.events.size());
        assertEquals("SYSTEM_UI", s.events.get(0).role);
        assertEquals("selected Android bugreport ZIP", s.source);
    }

    @Test public void invalidEmptyAndNoRelevantEntriesAreNotReportedAsHealthy() throws Exception {
        try {
            report("");
            fail("Empty document must fail");
        } catch (java.io.IOException expected) { /* no false healthy verdict */ }
        assertEquals(0, report("ordinary logcat without errors\n").events.size());
    }

    private static CriticalSystemCrashEvidence.Summary report(String text) throws Exception {
        return CriticalSystemCrashEvidence.parseUserReport(
                new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), ROLES);
    }
}
