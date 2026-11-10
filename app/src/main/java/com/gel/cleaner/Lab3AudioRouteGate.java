package com.gel.cleaner;

/** Fail-closed safety gate before temporarily changing earpiece audio mode. */
public final class Lab3AudioRouteGate {
    private Lab3AudioRouteGate() {}

    public static boolean canStart(boolean normalAudioMode,
                                   boolean outputInventoryKnown,
                                   boolean externalOutputPresent) {
        return normalAudioMode && outputInventoryKnown && !externalOutputPresent;
    }
}
