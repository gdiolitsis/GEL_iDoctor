package com.gel.cleaner;

/**
 * Stage-aware checks for a MODE_STATIC AudioTrack. In MODE_STATIC a freshly
 * created track may legitimately be STATE_NO_STATIC_DATA (2) until write().
 * A state probe is not evidence of sound emerging from a physical earpiece.
 */
public final class Lab3StaticAudioTrackPolicy {
    public static final int STATE_UNINITIALIZED = 0;
    public static final int STATE_INITIALIZED = 1;
    public static final int STATE_NO_STATIC_DATA = 2;
    private Lab3StaticAudioTrackPolicy() {}

    public static boolean canAcceptStaticWrite(int beforeWriteState) {
        return beforeWriteState == STATE_INITIALIZED
                || beforeWriteState == STATE_NO_STATIC_DATA;
    }

    public static boolean canRequestPlayback(int afterWriteState,
                                             int writtenSamples,
                                             int requestedSamples) {
        return afterWriteState == STATE_INITIALIZED
                && requestedSamples > 0
                && writtenSamples == requestedSamples;
    }

    public static String failureExplanation(String code, boolean greek) {
        if ("NOT_CREATED".equals(code)) {
            return greek ? "Το Android δεν αρχικοποίησε τη διαδρομή αναπαραγωγής."
                         : "Android could not initialize the playback track.";
        }
        if ("WRITE_INCOMPLETE".equals(code)) {
            return greek ? "Ο τόνος δεν φορτώθηκε πλήρως στο AudioTrack."
                         : "Tone samples were not fully written to AudioTrack.";
        }
        if ("NOT_READY_AFTER_WRITE".equals(code)) {
            return greek ? "Το AudioTrack δεν ήταν έτοιμο μετά τη φόρτωση του τόνου."
                         : "AudioTrack was not ready after loading the tone.";
        }
        if ("PLAY_EXCEPTION".equals(code)) {
            return greek ? "Το Android απέρριψε το αίτημα εκκίνησης αναπαραγωγής."
                         : "Android rejected the playback start request.";
        }
        if ("SESSION_CANCELLED".equals(code)) {
            return greek ? "Η δοκιμή διακόπηκε λόγω αλλαγής κατάστασης της εφαρμογής."
                         : "The app lifecycle interrupted the audio challenge.";
        }
        return greek ? "Δεν επιβεβαιώθηκε η εκτέλεση της ηχητικής δοκιμής."
                     : "The audio challenge could not be completed.";
    }
}
