package com.gel.cleaner;

import android.media.AudioDeviceInfo;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Lab 1 audio-route regression: Bluetooth/wired/USB alternatives must never
 * be silently treated as proof that the built-in loudspeaker emitted a tone.
 */
public class AcousticOutputRoutePolicyTest {
    @Test public void builtInOutputsAreNotExternal() {
        assertFalse(AcousticOutputRoutePolicy.couldBeExternalOutput(
                AudioDeviceInfo.TYPE_BUILTIN_SPEAKER));
        assertFalse(AcousticOutputRoutePolicy.couldBeExternalOutput(
                AudioDeviceInfo.TYPE_BUILTIN_EARPIECE));
        assertFalse(AcousticOutputRoutePolicy.couldBeExternalOutput(
                AudioDeviceInfo.TYPE_BUILTIN_MIC));
    }

    @Test public void wiredAndUsbOutputsBlockSourceAttribution() {
        assertTrue(AcousticOutputRoutePolicy.couldBeExternalOutput(
                AudioDeviceInfo.TYPE_WIRED_HEADPHONES));
        assertTrue(AcousticOutputRoutePolicy.couldBeExternalOutput(
                AudioDeviceInfo.TYPE_WIRED_HEADSET));
        assertTrue(AcousticOutputRoutePolicy.couldBeExternalOutput(
                AudioDeviceInfo.TYPE_USB_HEADSET));
    }

    @Test public void bluetoothHearingAidAndHdmiBlockSourceAttribution() {
        assertTrue(AcousticOutputRoutePolicy.couldBeExternalOutput(
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP));
        assertTrue(AcousticOutputRoutePolicy.couldBeExternalOutput(
                AudioDeviceInfo.TYPE_BLUETOOTH_SCO));
        assertTrue(AcousticOutputRoutePolicy.couldBeExternalOutput(
                AudioDeviceInfo.TYPE_BLE_HEADSET));
        assertTrue(AcousticOutputRoutePolicy.couldBeExternalOutput(
                AudioDeviceInfo.TYPE_HEARING_AID));
        assertTrue(AcousticOutputRoutePolicy.couldBeExternalOutput(
                AudioDeviceInfo.TYPE_HDMI));
    }
}
