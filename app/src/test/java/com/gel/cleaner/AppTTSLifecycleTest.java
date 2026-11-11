package com.gel.cleaner;

import android.content.Context;
import android.os.Looper;
import android.speech.tts.TextToSpeech;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

/** Exercise actual delayed TTS requests through stop, mute and dialog closure. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
@LooperMode(LooperMode.Mode.PAUSED)
public class AppTTSLifecycleTest {
    private Context context;
    private TextToSpeech engine;
    private AtomicBoolean ready;

    @Before public void prepareColdEngine() throws Exception {
        AppTTS.shutdown();
        context = RuntimeEnvironment.getApplication();
        AppTTS.init(context);
        shadowOf(Looper.getMainLooper()).idle();
        Field engineField = AppTTS.class.getDeclaredField("tts");
        engineField.setAccessible(true);
        engine = (TextToSpeech) engineField.get(null);
        Field readyField = AppTTS.class.getDeclaredField("ready");
        readyField.setAccessible(true);
        ready = (AtomicBoolean) readyField.get(null);
        AppTTS.setMuted(context, false);
        ready.set(false);
    }

    @After public void cleanup() { AppTTS.shutdown(); }

    private String spoken() { return shadowOf(engine).getLastSpokenText(); }
    private void warmEngine() {
        ready.set(true);
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(400));
    }

    @Test public void visibleIntroSpeaksAfterEngineBecomesReady() {
        AppTTS.speak(context, "Camera instructions", () -> true);
        warmEngine();
        assertEquals("Camera instructions", spoken());
    }

    @Test public void exitOrStartStopsPendingInitializationSpeech() {
        String before = spoken();
        AppTTS.speak(context, "Closed intro", () -> true);
        AppTTS.stop();
        warmEngine();
        assertEquals(before, spoken());
    }

    @Test public void closedDialogCannotSpeakEvenWithoutExplicitStop() {
        String before = spoken();
        AtomicBoolean visible = new AtomicBoolean(true);
        AppTTS.speak(context, "Old intro", visible::get);
        visible.set(false);
        warmEngine();
        assertEquals(before, spoken());
    }

    @Test public void muteThenUnmuteDoesNotResurrectOldInstructions() {
        String before = spoken();
        AppTTS.speak(context, "Muted intro", () -> true);
        AppTTS.setMuted(context, true);
        AppTTS.setMuted(context, false);
        warmEngine();
        assertEquals(before, spoken());
    }

    @Test public void latestRequestWinsWhileEngineIsStarting() {
        AppTTS.speak(context, "Old lab", () -> true);
        AppTTS.speak(context, "Current lab", () -> true);
        warmEngine();
        assertEquals("Current lab", spoken());
    }

    @Test public void unavailableEngineDoesNotRetryForever() {
        String before = spoken();
        AppTTS.speak(context, "Expired intro", () -> true);
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(11));
        warmEngine();
        assertEquals(before, spoken());
    }

    @Test public void shutdownInvalidatesPendingRequests() {
        String before = spoken();
        AppTTS.speak(context, "Destroyed activity", () -> true);
        AppTTS.shutdown();
        warmEngine();
        assertEquals(before, spoken());
    }
}
