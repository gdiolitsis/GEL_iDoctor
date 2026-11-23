package com.gel.cleaner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

public final class Lab9EssentialHardwareContractTest {
    private static String file(String relative) throws Exception {
        Path p = Paths.get("app/src/main/java/com/gel/cleaner", relative);
        if (!Files.isRegularFile(p)) p = Paths.get("src/main/java/com/gel/cleaner", relative);
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    @Test public void optionalChecksStayInsideLab9AndNeverChangeCertification() throws Exception {
        String manual = file("ManualTestsActivity.java");
        assertTrue(manual.contains("showLab9LiveChecksPrompt"));
        assertTrue(manual.contains("REQ_LAB9_ESSENTIALS = 9001"));
        assertTrue(manual.contains("EssentialHardwareChecksActivity.class"));
        assertTrue(manual.contains("if (requestCode == REQ_LAB9_ESSENTIALS)"));
        assertTrue(manual.contains("if (inventoryFinished) runOnUiThread(this::showLab9LiveChecksPrompt)"));
    }

    @Test public void gpsNfcAndKeysAreObservedNotGuessed() throws Exception {
        String app = file("EssentialHardwareChecksActivity.java");
        assertTrue(app.contains("LocationManager.GPS_PROVIDER"));
        assertTrue(app.contains("newGpsFix(gpsStartNs"));
        assertTrue(app.contains("location.isMock()"));
        assertTrue(app.contains("nfcAdapter.enableReaderMode("));
        assertTrue(app.contains("NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK"));
        assertTrue(app.contains("KeyEvent.KEYCODE_VOLUME_UP"));
        assertTrue(app.contains("KeyEvent.KEYCODE_VOLUME_DOWN"));
        assertFalse(app.contains("getLatitude("));
        assertFalse(app.contains("getLongitude("));
        assertFalse(app.contains("getId("));
        assertTrue(app.contains("USER_CONFIRMED"));
        assertTrue(app.contains("NOT_VERIFIED"));
    }
}
