package com.gel.cleaner;

import org.junit.Test;
import static org.junit.Assert.*;

public class Lab27PermissionEvidenceTest {
    private static final String CAMERA = "android.permission.CAMERA";
    @Test public void packageFailureIsUnavailable() {
        assertEquals(Lab27PermissionEvidence.Coverage.UNAVAILABLE,
                Lab27PermissionEvidence.coverage(false, 4));
    }
    @Test public void emptySuccessfulScanStillLimited() {
        assertEquals(Lab27PermissionEvidence.Coverage.VISIBLE_PACKAGES_ONLY,
                Lab27PermissionEvidence.coverage(true, 0));
    }
    @Test public void negativeCountIsInvalid() {
        assertEquals(Lab27PermissionEvidence.Coverage.UNAVAILABLE,
                Lab27PermissionEvidence.coverage(true, -1));
    }
    @Test public void missingFlagsUnknownNotDenied() {
        assertEquals(Lab27PermissionEvidence.GrantState.UNKNOWN,
                Lab27PermissionEvidence.granted(new String[]{CAMERA}, null, 0));
    }
    @Test public void shortFlagsUnknownNotDenied() {
        assertEquals(Lab27PermissionEvidence.GrantState.UNKNOWN,
                Lab27PermissionEvidence.granted(new String[]{CAMERA}, new int[0], 0));
    }
    @Test public void outOfRangeUnknown() {
        assertEquals(Lab27PermissionEvidence.GrantState.UNKNOWN,
                Lab27PermissionEvidence.granted(new String[]{CAMERA}, new int[]{2}, 1));
    }
    @Test public void grantedFlagObserved() {
        assertEquals(Lab27PermissionEvidence.GrantState.GRANTED,
                Lab27PermissionEvidence.granted(new String[]{CAMERA}, new int[]{2}, 0));
    }
    @Test public void ungrantedFlagOnlyPermissionSnapshot() {
        assertEquals(Lab27PermissionEvidence.GrantState.NOT_GRANTED,
                Lab27PermissionEvidence.granted(new String[]{CAMERA}, new int[]{0}, 0));
    }
    @Test public void locationGroupIncludesBackground() {
        assertEquals("LOCATION", Lab27PermissionEvidence.category("android.permission.ACCESS_BACKGROUND_LOCATION"));
        assertEquals("LOCATION", Lab27PermissionEvidence.category("android.permission.ACCESS_FINE_LOCATION"));
    }
    @Test public void microphoneAndCameraHaveDifferentGroups() {
        assertEquals("MICROPHONE", Lab27PermissionEvidence.category("android.permission.RECORD_AUDIO"));
        assertEquals("CAMERA", Lab27PermissionEvidence.category(CAMERA));
    }
    @Test public void smsGroupHandlesReceive() {
        assertEquals("SMS", Lab27PermissionEvidence.category("android.permission.RECEIVE_SMS"));
    }
    @Test public void contactsGroupHandlesRead() {
        assertEquals("CONTACTS", Lab27PermissionEvidence.category("android.permission.READ_CONTACTS"));
    }
    @Test public void callGroupHandlesLog() {
        assertEquals("CALLS", Lab27PermissionEvidence.category("android.permission.READ_CALL_LOG"));
    }
    @Test public void notificationsHaveSeparateGroup() {
        assertEquals("NOTIFICATIONS", Lab27PermissionEvidence.category("android.permission.POST_NOTIFICATIONS"));
    }
    @Test public void specialAccessMustNotBeInferredFromRuntimeFlags() {
        assertNull(Lab27PermissionEvidence.category("android.permission.SYSTEM_ALERT_WINDOW"));
    }
    @Test public void unknownPermissionIsNotAThreatScore() {
        assertNull(Lab27PermissionEvidence.category("example.vendor.permission.CUSTOM"));
    }
    @Test public void nullPermissionIsUnknown() {
        assertNull(Lab27PermissionEvidence.category(null));
    }
    @Test public void observedDoesNotMeanPrivacyClean() {
        assertEquals("OBSERVED_LIMITED", Lab27PermissionEvidence.summaryToken(true));
        assertEquals("INCONCLUSIVE", Lab27PermissionEvidence.summaryToken(false));
    }
}
