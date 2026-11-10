package com.gel.cleaner;

/** Fail closed before a guided internal microphone/earpiece test.
 * Android audio-source profiles do not identify a particular physical mic.
 */
public final class Lab4AudioPreflight {
    private Lab4AudioPreflight() {}

    public static boolean canStart(boolean normalAudioMode,
                                   boolean outputInventoryKnown,
                                   boolean externalOutputPossible,
                                   boolean microphoneMuted) {
        return normalAudioMode && outputInventoryKnown
                && !externalOutputPossible && !microphoneMuted;
    }
}
