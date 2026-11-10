package com.gel.cleaner;
import org.junit.Test;
import static org.junit.Assert.*;

public class Lab4AudioPreflightTest {
    @Test public void internalAvailableUnmutedRouteMayStart() {
        assertTrue(Lab4AudioPreflight.canStart(true, true, false, false));
    }
    @Test public void activeVoiceCallCannotBePreempted() {
        assertFalse(Lab4AudioPreflight.canStart(false, true, false, false));
    }
    @Test public void externalBluetoothWiredUsbMustRemainUnchanged() {
        assertFalse(Lab4AudioPreflight.canStart(true, true, true, false));
    }
    @Test public void mutedMicrophoneRequiresExplicitUserActionNotUnmute() {
        assertFalse(Lab4AudioPreflight.canStart(true, true, false, true));
    }
    @Test public void unknownRouteDoesNotBecomeAnInternalMicrophonePass() {
        assertFalse(Lab4AudioPreflight.canStart(true, false, false, false));
    }
}
